/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2014 The Regents of the University of California All Rights Reserved
 * 
 * Permission to copy, modify and distribute any part of this CRBS Workflow 
 * Service for educational, research and non-profit purposes, without fee, and
 * without a written agreement is hereby granted, provided that the above 
 * copyright notice, this paragraph and the following three paragraphs appear
 * in all copies.
 * 
 * Those desiring to incorporate this CRBS Workflow Service into commercial 
 * products or use for commercial purposes should contact the Technology
 * Transfer Office, University of California, San Diego, 9500 Gilman Drive, 
 * Mail Code 0910, La Jolla, CA 92093-0910, Ph: (858) 534-5815, 
 * FAX: (858) 534-7345, E-MAIL:invent@ucsd.edu.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR 
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING 
 * LOST PROFITS, ARISING OUT OF THE USE OF THIS CRBS Workflow Service, EVEN IF 
 * THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 * 
 * THE CRBS Workflow Service PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE
 * UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, 
 * UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES
 * NO REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR 
 * EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF 
 * THE CRBS Workflow Service WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER
 * RIGHTS. 
 */

package edu.ucsd.crbs.cws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.googlecode.objectify.ObjectifyService;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.impl.provider.entity.StringProvider;
import com.sun.jersey.multipart.impl.MultiPartWriter;
import static edu.ucsd.crbs.cws.App.LOGIN_ARG;
import static edu.ucsd.crbs.cws.App.RUN_AS_ARG;
import static edu.ucsd.crbs.cws.App.TOKEN_ARG;
import edu.ucsd.crbs.cws.auth.Permission;
import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.cluster.MapOfJobStatusFactoryImpl;
import edu.ucsd.crbs.cws.cluster.JobStatusUpdater;
import edu.ucsd.crbs.cws.cluster.JobSubmitter;
import edu.ucsd.crbs.cws.cluster.WorkspaceFilePathSetterImpl;
import edu.ucsd.crbs.cws.dao.rest.JobRestDAOImpl;
import edu.ucsd.crbs.cws.dao.rest.WorkflowRestDAOImpl;
import edu.ucsd.crbs.cws.dao.rest.WorkspaceFileRestDAOImpl;
import edu.ucsd.crbs.cws.io.KeplerMomlFromKar;
import edu.ucsd.crbs.cws.jerseyclient.MultivaluedMapFactory;
import edu.ucsd.crbs.cws.jerseyclient.MultivaluedMapFactoryImpl;
import edu.ucsd.crbs.cws.log.Event;
import edu.ucsd.crbs.cws.rest.Constants;
import edu.ucsd.crbs.cws.util.RunCommandLineProcess;
import edu.ucsd.crbs.cws.util.RunCommandLineProcessImpl;
import edu.ucsd.crbs.cws.workflow.Parameter;
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.WorkflowParameter;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import edu.ucsd.crbs.cws.workflow.kepler.WorkflowFromAnnotatedXmlFactory;
import java.io.BufferedInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Command line program that takes a kepler xml file or kar file and generates
 * JSON representation of the workflow parameters to display to the user or to
 * push to the service defined at the URL specified on the command line
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class App {

    public static final String XML_SUFFIX = ".xml";

    public static final String UPLOAD_WF_ARG = "uploadwf";
    
    public static final String UPLOAD_FILE_ARG = "uploadfile";
    
    public static final String DOWNLOAD_FILE_ARG = "downloadfile";
    
    public static final String REGISTER_FILE_ARG = "registerfile";
    
    public static final String GET_WORKSPACE_FILE_INFO_ARG = "fileinfo";
    
    public static final String UPDATE_PATH_ARG = "updatepath";
    
    public static final String PATH_ARG = "path";
    
    public static final String JOB_ID_ARG = "jobid";
    
    public static final String MD5_ARG = "md5";
    
    public static final String SIZE_ARG = "size";
    
    public static final String OWNER_ARG = "owner";

    public static final String SYNC_WITH_CLUSTER_ARG = "syncwithcluster";
    
    public static final String HELP_ARG = "h";

    public static final String URL_ARG = "url";

    public static final String WF_EXEC_DIR_ARG = "execdir";

    public static final String WF_DIR_ARG = "wfdir";

    public static final String KEPLER_SCRIPT_ARG = "kepler";

    public static final String PARENT_WFID_ARG = "parentwf";

    public static final String EXAMPLE_JSON_ARG = "examplejson";

    public static final String QUEUE_ARG = "queue";

    public static final String CAST_ARG = "panfishcast";

    public static final String STAT_ARG = "panfishstat";

    public static final String LOGIN_ARG = "login";

    public static final String TOKEN_ARG = "token";
    
    public static final String RUN_AS_ARG = "runas";
    
    public static final String RESAVE_WORKSPACEFILE_ARG = "resavefile";

    public static final String RESAVE_JOB_ARG = "resavejob";

    public static final String RESAVE_WORKFLOW_ARG = "resaveworkflow";
    
    //public static final String LOAD_TEST = "loadtest";
    public static final String PROGRAM_HELP = "\nCRBS Workflow Service Command Line Tools "
            + "\n\nThis program provides options to run Workflow Jobs on the local cluster as well"
            + " as add new Workflows to the CRBS Workflow Service";

    /**
     * CSV string of statuses for jobs that have not completed or failed
     */
    public static final String NOT_COMPLETED_STATUSES = Job.IN_QUEUE_STATUS + ","
            + Job.PAUSED_STATUS + "," + Job.PENDING_STATUS + "," + Job.RUNNING_STATUS +","+
            ","+Job.WORKSPACE_SYNC_STATUS;
   

    public static void main(String[] args) {
        Job.REFS_ENABLED = false;
        Workflow.REFS_ENABLED = false;
        try {

            OptionParser parser = new OptionParser() {
                {
                    accepts(UPLOAD_WF_ARG, "Add/Update Workflow").withRequiredArg().ofType(File.class).describedAs(".kar");
                    //accepts(LOAD_TEST,"creates lots of workflows and jobs");
                    accepts(SYNC_WITH_CLUSTER_ARG, "Submits & Synchronizes Workflow Jobs on local cluster with CRBS Workflow Webservice").withRequiredArg().ofType(String.class).describedAs("URL");
                    accepts(UPLOAD_FILE_ARG,"Registers and uploads Workspace file to REST service").withRequiredArg().ofType(File.class);
                    accepts(REGISTER_FILE_ARG,"Registers Workspace file to REST service (DOES NOT UPLOAD FILE TO REST SERVICE)").withRequiredArg().ofType(File.class);
                    accepts(GET_WORKSPACE_FILE_INFO_ARG,"Outputs JSON of specified workspace file(s)").withRequiredArg().ofType(String.class).describedAs("workspace file id");
                    accepts(DOWNLOAD_FILE_ARG,"Downloads Workspace file").withRequiredArg().ofType(String.class).describedAs("workspace file id");
                    accepts(UPDATE_PATH_ARG,"Updates Workspace file path").withRequiredArg().ofType(String.class).describedAs("workspace file id");
                    accepts(PATH_ARG,"Sets WorkspaceFile file path.  Used in coordination with --"+UPDATE_PATH_ARG).withRequiredArg().ofType(String.class).describedAs("file path");
                    accepts(URL_ARG, "URL to use with --" + UPLOAD_WF_ARG + ", --"+UPLOAD_FILE_ARG+", --"+GET_WORKSPACE_FILE_INFO_ARG+" flags").withRequiredArg().ofType(String.class).describedAs("URL");
                    accepts(PARENT_WFID_ARG, "Used to set parent workflow id when invoking --"+UPLOAD_WF_ARG).withRequiredArg().ofType(Long.class).describedAs("Workflow Id");
                    accepts(EXAMPLE_JSON_ARG, "Outputs example JSON of Job, User, Workflow, and WorkspaceFile objects");
                    accepts(WF_EXEC_DIR_ARG, "Workflow Execution Directory").withRequiredArg().ofType(File.class).describedAs("Directory");
                    accepts(WF_DIR_ARG, "Workflows Directory").withRequiredArg().ofType(File.class).describedAs("Directory");
                    accepts(KEPLER_SCRIPT_ARG, "Kepler").withRequiredArg().ofType(File.class).describedAs("Script");
                    accepts(QUEUE_ARG, "SGE Queue").withRequiredArg().ofType(String.class).describedAs("Queue");
                    accepts(CAST_ARG, "Panfishcast binary").withRequiredArg().ofType(File.class).describedAs("panfishcast");
                    accepts(STAT_ARG, "Panfishstat binary").withRequiredArg().ofType(File.class).describedAs("panfishstat");
                    accepts(LOGIN_ARG, "User Login").withRequiredArg().ofType(String.class).describedAs("username");
                    accepts(TOKEN_ARG, "User Token").withRequiredArg().ofType(String.class).describedAs("token");
                    accepts(RUN_AS_ARG, "User to run as (for power accounts that can run as other users)").withRequiredArg().ofType(String.class).describedAs("runas");
                    accepts(OWNER_ARG,"Sets owner when creating Workspace file and Workflow").withRequiredArg().ofType(String.class).describedAs("username");
                    accepts(JOB_ID_ARG,"Sets job id for Workspace file when used with --"+UPLOAD_FILE_ARG+" and --"+REGISTER_FILE_ARG).withRequiredArg().ofType(Long.class).describedAs("Job Id");
                    accepts(MD5_ARG,"Sets md5 for Workspace file when used with --"+UPLOAD_FILE_ARG+" and --"+REGISTER_FILE_ARG).withRequiredArg().ofType(String.class).describedAs("MD5 message digest");
                    accepts(SIZE_ARG,"Sets size in bytes for Workspace file when used with --"+UPLOAD_FILE_ARG+" and --"+REGISTER_FILE_ARG).withRequiredArg().ofType(String.class).describedAs("Size of file/dir in bytes");
                    accepts(RESAVE_WORKSPACEFILE_ARG,"Resaves Workspace file").withRequiredArg().ofType(Long.class).describedAs("WorkspaceFile Id");
                    accepts(RESAVE_JOB_ARG,"Resaves Job").withRequiredArg().ofType(Long.class).describedAs("Job Id");
                    accepts(RESAVE_WORKFLOW_ARG,"Resaves Workflow").withRequiredArg().ofType(Long.class).describedAs("Workflow Id");

                    accepts(HELP_ARG).forHelp();
                    
                }
            };

            OptionSet optionSet = null;
            try {
                optionSet = parser.parse(args);
            } catch (OptionException oe) {
                System.err.println("\nThere was an error parsing arguments: " + oe.getMessage() + "\n\n");
                parser.printHelpOn(System.err);
                System.exit(1);
            }

            if (optionSet.has(HELP_ARG) ||
                    (!optionSet.has(SYNC_WITH_CLUSTER_ARG) && 
                     !optionSet.has(UPLOAD_WF_ARG)) &&
                     !optionSet.has(EXAMPLE_JSON_ARG) && 
                     !optionSet.has(UPLOAD_FILE_ARG) &&
                     !optionSet.has(GET_WORKSPACE_FILE_INFO_ARG) &&
                     !optionSet.has(UPDATE_PATH_ARG) &&
                     !optionSet.has(REGISTER_FILE_ARG) &&
                     !optionSet.has(RESAVE_WORKSPACEFILE_ARG) &&
                     !optionSet.has(RESAVE_JOB_ARG) &&
                     !optionSet.has(RESAVE_WORKFLOW_ARG)) {
                System.out.println(PROGRAM_HELP + "\n");
                parser.printHelpOn(System.out);
                System.exit(0);
            }

            if (optionSet.has(EXAMPLE_JSON_ARG)) {

                renderExampleWorkflowsAndTasksAsJson();
                System.exit(0);
            }

            MultivaluedMapFactory multivaluedMapFactory = new MultivaluedMapFactoryImpl();
            
            if (optionSet.has(REGISTER_FILE_ARG)){
                addNewWorkspaceFile(optionSet,false,REGISTER_FILE_ARG);
                System.exit(0);
            }
            
            if (optionSet.has(RESAVE_WORKSPACEFILE_ARG)){
                failIfOptionSetMissingURLOrLoginOrToken(optionSet,"--"+RESAVE_WORKSPACEFILE_ARG+" flag");
                WorkspaceFileRestDAOImpl workspaceFileDAO = new WorkspaceFileRestDAOImpl();
                User u = getUserFromOptionSet(optionSet);
                workspaceFileDAO.setUser(u);
                workspaceFileDAO.setRestURL((String)optionSet.valueOf(URL_ARG));
                Long workspaceId = (Long)optionSet.valueOf(RESAVE_WORKSPACEFILE_ARG);
                if (workspaceId == -1){
                    System.out.println("Resaving all workspace files");
                    List<WorkspaceFile> wsfList = workspaceFileDAO.getWorkspaceFiles(null, null);
                    if (wsfList != null){
                        System.out.println("Found "+wsfList.size()+
                                " workspace files to resave");
                        for (WorkspaceFile wsf : wsfList){
                            workspaceFileDAO.resave(wsf.getId());
                        }
                    }
                }
                else {
                    workspaceFileDAO.resave(workspaceId);
                }
                System.exit(0);
            }
            
            if (optionSet.has(RESAVE_JOB_ARG)){
                failIfOptionSetMissingURLOrLoginOrToken(optionSet,"--"+RESAVE_JOB_ARG+" flag");
                JobRestDAOImpl jobDAO = new JobRestDAOImpl();
                User u = getUserFromOptionSet(optionSet);
                jobDAO.setUser(u);
                jobDAO.setRestURL((String)optionSet.valueOf(URL_ARG));
                Long jobId = (Long)optionSet.valueOf(RESAVE_JOB_ARG);
                if (jobId == -1){
                    System.out.println("Resaving all jobs");
                    List<Job> jobList = jobDAO.getJobs(null,null,null, true, true);
                    if (jobList != null){
                        System.out.println("Found "+jobList.size()+" jobs to resave");
                        for (Job j : jobList){
                            System.out.println("job id: "+j.getId());
                            jobDAO.resave(j.getId());
                        }
                    }
                }
                else {
                    jobDAO.resave(jobId);
                }
                System.exit(0);
            }
            
            if (optionSet.has(RESAVE_WORKFLOW_ARG)){
                failIfOptionSetMissingURLOrLoginOrToken(optionSet,"--"+RESAVE_WORKFLOW_ARG+" flag");
                WorkflowRestDAOImpl workflowDAO = new WorkflowRestDAOImpl();
                User u = getUserFromOptionSet(optionSet);
                workflowDAO.setUser(u);
                workflowDAO.setRestURL((String)optionSet.valueOf(URL_ARG));
                Long workflowId = (Long)optionSet.valueOf(RESAVE_WORKFLOW_ARG);
                if (workflowId == -1){
                    System.out.println("Resaving all workflows");
                    List<Workflow> workflowList = workflowDAO.getAllWorkflows(true);
                    if (workflowList != null){
                        System.out.println("Found "+workflowList.size()+" workflow(s) to resave");
                        for (Workflow w : workflowList){
                            System.out.println("workflow id: "+w.getId());
                            workflowDAO.resave(w.getId());
                        }
                    }
                }
                else {
                    workflowDAO.resave(workflowId);
                }
                System.exit(0);
            }
            
            if (optionSet.has(UPDATE_PATH_ARG)) {
                failIfOptionSetMissingURLOrLoginOrToken(optionSet,"--"+UPDATE_PATH_ARG+" flag");
                
                User u = getUserFromOptionSet(optionSet);
                String workspaceId = (String)optionSet.valueOf(UPDATE_PATH_ARG);
                String path = null;
                if (optionSet.has(PATH_ARG)){
                    path = (String)optionSet.valueOf(PATH_ARG);
                }
                
                WorkspaceFileRestDAOImpl workspaceFileDAO = new WorkspaceFileRestDAOImpl();
                workspaceFileDAO.setUser(u);
                workspaceFileDAO.setRestURL((String)optionSet.valueOf(URL_ARG));
                workspaceFileDAO.updatePath(Long.parseLong(workspaceId), path);
                System.exit(0);
            }
            
            if (optionSet.has(SYNC_WITH_CLUSTER_ARG)) {
                // @TODO NEED TO MAKE JOPT DO THIS REQUIRED FLAG CHECKING STUFF
                if (!optionSet.has(WF_EXEC_DIR_ARG)) {
                    System.err.println("-" + WF_EXEC_DIR_ARG + " is required with -" + SYNC_WITH_CLUSTER_ARG + " flag");
                    System.exit(2);
                }
                if (!optionSet.has(WF_DIR_ARG)) {
                    System.err.println("-" + WF_DIR_ARG + " is required with -" + SYNC_WITH_CLUSTER_ARG + " flag");
                    System.exit(3);
                }
                if (!optionSet.has(KEPLER_SCRIPT_ARG)) {
                    System.err.println("-" + KEPLER_SCRIPT_ARG + " is required with -" + SYNC_WITH_CLUSTER_ARG + " flag");
                    System.exit(4);
                }

                if (!optionSet.has(CAST_ARG)) {
                    System.err.println("-" + CAST_ARG + " is required with -" + SYNC_WITH_CLUSTER_ARG + " flag");
                    System.exit(5);
                }

                if (!optionSet.has(STAT_ARG)) {
                    System.err.println("-" + STAT_ARG + " is required with -" + SYNC_WITH_CLUSTER_ARG + " flag");
                    System.exit(6);
                }

                if (!optionSet.has(QUEUE_ARG)) {
                    System.err.println("-" + QUEUE_ARG + " is required with -" + SYNC_WITH_CLUSTER_ARG + " flag");
                    System.exit(7);
                }
                
                failIfOptionSetMissingLoginOrToken(optionSet,"--" + SYNC_WITH_CLUSTER_ARG + " flag");

                File castFile = (File) optionSet.valueOf(CAST_ARG);
                String castPath = castFile.getAbsolutePath();

                File statFile = (File) optionSet.valueOf(STAT_ARG);
                String statPath = statFile.getAbsolutePath();

                String queue = (String) optionSet.valueOf(QUEUE_ARG);

                File wfExecDir = (File) optionSet.valueOf(WF_EXEC_DIR_ARG);
                File wfDir = (File) optionSet.valueOf(WF_DIR_ARG);
                File keplerScript = (File) optionSet.valueOf(KEPLER_SCRIPT_ARG);

                User u = getUserFromOptionSet(optionSet);
                
                ObjectifyService.ofy();
                String url = (String) optionSet.valueOf(SYNC_WITH_CLUSTER_ARG);
                JobRestDAOImpl jobDAO = new JobRestDAOImpl();
                jobDAO.setRestURL(url);
                jobDAO.setUser(u);

                System.out.println("Running sync with cluster");

                WorkspaceFileRestDAOImpl workspaceFileDAO = new WorkspaceFileRestDAOImpl();
                workspaceFileDAO.setRestURL(url);
                
                WorkspaceFilePathSetterImpl pathSetter = new WorkspaceFilePathSetterImpl(workspaceFileDAO);
                
                // Submit jobs to scheduler
                JobSubmitter submitter = new JobSubmitter(jobDAO,
                        pathSetter,
                        wfExecDir.getAbsolutePath(),
                        wfDir.getAbsolutePath(),
                        keplerScript.getAbsolutePath(),
                        castPath, queue,u,
                        url);

                submitter.submitJobs();

                // Update job status for all jobs in system
                MapOfJobStatusFactoryImpl jobStatusFactory = new MapOfJobStatusFactoryImpl(statPath);
                JobStatusUpdater updater = new JobStatusUpdater(jobDAO, jobStatusFactory);
                updater.updateJobs();

                System.exit(0);
            }
            
            if (optionSet.has(App.GET_WORKSPACE_FILE_INFO_ARG)){
                failIfOptionSetMissingURLOrLoginOrToken(optionSet,"--"+GET_WORKSPACE_FILE_INFO_ARG+" flag");
                
                WorkspaceFileRestDAOImpl workspaceFileDAO = new WorkspaceFileRestDAOImpl();
                
                workspaceFileDAO.setRestURL((String)optionSet.valueOf(URL_ARG));

                List<WorkspaceFile> wsFiles = workspaceFileDAO.getWorkspaceFilesById((String)optionSet.valueOf(GET_WORKSPACE_FILE_INFO_ARG),null);
                
                if (wsFiles != null){
                    ObjectMapper om = new ObjectMapper();
                    ObjectWriter ow = om.writerWithDefaultPrettyPrinter();
                    System.out.print("[");
                    boolean first = true;
                    for (WorkspaceFile wsf : wsFiles){
                        if (first == false){
                            System.out.println(",");
                        }
                        else {
                            first = false;
                        }
                        System.out.print(ow.writeValueAsString(wsf));
                    }
                    System.out.println("]");
                }
                else {
                    System.err.println("[]");
                }
                System.exit(0);
            }
            
            if (optionSet.has(UPLOAD_FILE_ARG)){
                addNewWorkspaceFile(optionSet,true,UPLOAD_FILE_ARG);
                System.exit(0);
            }

            if (optionSet.has(UPLOAD_WF_ARG)) {

                Long parentWfId = null;

                String postURL = null;
                if (optionSet.has(URL_ARG)) {
                    postURL = (String) optionSet.valueOf(URL_ARG);
                    failIfOptionSetMissingLoginOrToken(optionSet,"--"+UPLOAD_WF_ARG+" and --"+URL_ARG+" flag");
                }

                if (optionSet.has(PARENT_WFID_ARG)) {
                    parentWfId = (Long) optionSet.valueOf(PARENT_WFID_ARG);
                }

                File workflowFile = (File) optionSet.valueOf(UPLOAD_WF_ARG);
                //WorkflowFromXmlFactory xmlFactory = new WorkflowFromXmlFactory();
                WorkflowFromAnnotatedXmlFactory xmlFactory = new WorkflowFromAnnotatedXmlFactory();
                xmlFactory.setWorkflowXml(new BufferedInputStream(KeplerMomlFromKar.getInputStreamOfWorkflowMoml(workflowFile)));

                Workflow w = xmlFactory.getWorkflow();
                if (w != null) {

                    if (optionSet.has(OWNER_ARG)) {
                        w.setOwner((String) optionSet.valueOf(OWNER_ARG));
                    }

                    ObjectMapper om = new ObjectMapper();
                    if (parentWfId != null) {
                        w.setId(parentWfId);
                    }
                    if (postURL == null) {
                        System.out.println("\n--- JSON Representation of Workflow ---");
                        ObjectWriter ow = om.writerWithDefaultPrettyPrinter();
                        System.out.println(ow.writeValueAsString(w));
                        System.out.flush();
                        System.out.println("---------------------------------------");

                    } else {
                        postURL = new StringBuilder().append(postURL).append(Constants.SLASH).
                        append(Constants.REST_PATH).append(Constants.SLASH).
                        append(Constants.WORKFLOWS_PATH).toString();

                        ClientConfig cc = new DefaultClientConfig();
                        cc.getClasses().add(StringProvider.class);
                        cc.getClasses().add(MultiPartWriter.class);
                        Client client = Client.create(cc);
                        client.setFollowRedirects(true);
                        WebResource resource = client.resource(postURL);
                        String workflowAsJson = om.writeValueAsString(w);

                        User u = getUserFromOptionSet(optionSet);
                        MultivaluedMap queryParams = multivaluedMapFactory.getMultivaluedMap(u);

                        String response = resource.queryParams(queryParams).type(MediaType.APPLICATION_JSON_TYPE)
                                .entity(workflowAsJson)
                                .post(String.class);
                        Workflow workflowRes = om.readValue(response, Workflow.class);
                        ObjectWriter ow = om.writerWithDefaultPrettyPrinter();

                        if (workflowRes.getWorkflowFileUploadURL() == null) {
                            throw new Exception("No upload url found for workflow!!!"
                                    + ow.writeValueAsString(workflowRes));
                        }

                        uploadWorkflowFile(workflowRes, workflowFile);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Caught Exception: " + ex.getMessage());

            System.exit(2);
        }

        System.exit(0);
    }
    
    public static void addNewWorkspaceFile(OptionSet optionSet,boolean uploadFile,final String theArg) throws Exception {
        String postURL = null;
        if (optionSet.has(URL_ARG)) {
            postURL = (String) optionSet.valueOf(URL_ARG);
            failIfOptionSetMissingLoginOrToken(optionSet, "--" + theArg + " and --" + URL_ARG + " flag");
        }

        File file = (File) optionSet.valueOf(theArg);
        WorkspaceFile wsp = new WorkspaceFile();
        wsp.setName(file.getName());
        wsp.setSize(file.length());
        wsp.setDir(file.isDirectory());
        if (optionSet.has(OWNER_ARG)) {
            wsp.setOwner((String) optionSet.valueOf(OWNER_ARG));
        }
        if (optionSet.has(PATH_ARG)){
            wsp.setPath((String)optionSet.valueOf(PATH_ARG));
        }
        if (optionSet.has(JOB_ID_ARG)){
            wsp.setSourceJobId((Long)optionSet.valueOf(JOB_ID_ARG));
        }
        if (optionSet.has(MD5_ARG)){
            wsp.setMd5((String)optionSet.valueOf(MD5_ARG));
        }

        ObjectMapper om = new ObjectMapper();
        ObjectWriter ow = om.writerWithDefaultPrettyPrinter();
        if (postURL == null) {
            System.out.println("\n--- JSON Representation of WorkspaceFile ---");
            System.out.println(ow.writeValueAsString(wsp));
            System.out.flush();
            System.out.println("---------------------------------------");
            System.exit(0);
        }
        User u = getUserFromOptionSet(optionSet);
        WorkspaceFileRestDAOImpl workspaceFileDAO = new WorkspaceFileRestDAOImpl();
        workspaceFileDAO.setRestURL(postURL);
        workspaceFileDAO.setUser(u);
        
        WorkspaceFile workspaceFileRes = workspaceFileDAO.insert(wsp,uploadFile);
        
        if (uploadFile == false){
            return;
        }
        
        if (workspaceFileRes.getUploadURL() == null) {
            throw new Exception("No upload url found for workflow!!!"
                    + ow.writeValueAsString(workspaceFileRes));
        }
        uploadWorkspaceFile(workspaceFileRes, file);
    }
    
    public static void failIfOptionSetMissingURLOrLoginOrToken(OptionSet optionSet,final String message){
        failIfOptionSetMissingURL(optionSet,message);
        failIfOptionSetMissingLoginOrToken(optionSet,message);
    }
    
    public static void failIfOptionSetMissingURL(OptionSet optionSet, final String message) {
        if (!optionSet.has(URL_ARG)) {
            System.err.println("--" + URL_ARG + " is required with " + message);
            System.exit(12);
        }
    }
   
    public static void failIfOptionSetMissingLoginOrToken(OptionSet optionSet,final String message){
        if (!optionSet.has(LOGIN_ARG)){
            System.err.println("--"+LOGIN_ARG+" is required with "+message);
            System.exit(10);
        }
        if (!optionSet.has(TOKEN_ARG)){
           System.err.println("--"+TOKEN_ARG+" is required with "+message);
            System.exit(11);
        }
    }
    
    /**
     * Parses <b>optionSet</b> for {@link LOGIN#LOGIN_ARG}, {@link TOKEN#TOKEN_ARG}, and {@link RUN_AS#RUN_AS_ARG} 
     * to generate {@link User} object
     * @param optionSet
     * @return User object with {@link User#getLogin()}, {@link User#getLoginToRunTaskAs()}, and
     * {@link User#getToken()} set to values from <b>optionSet</b>
     */
    public static User getUserFromOptionSet(OptionSet optionSet){
        User u = new User();
        if (optionSet.has(LOGIN_ARG)){
            u.setLogin((String)optionSet.valueOf(LOGIN_ARG));
        }
        
        if (optionSet.has(TOKEN_ARG)){
            u.setToken((String)optionSet.valueOf(TOKEN_ARG));
        }
        
        if (optionSet.has(RUN_AS_ARG)){
            u.setLoginToRunTaskAs((String)optionSet.valueOf(RUN_AS_ARG));
        }
        return u;
    }
    
    /**
     * Using curl this method uploads via POST a {@link Workflow} file to REST
     * service
     * @param w
     * @param workflowFile
     * @throws Exception 
     */
    public static void uploadWorkflowFile(Workflow w, File workflowFile) throws Exception {

        RunCommandLineProcess procRunner = new RunCommandLineProcessImpl();
        System.out.println("TODO SWITCH THIS TO USE JERSEY CLIENT!!!\nAttempting to run this command: curl -i -X POST --form '" + 
                w.getId().toString() + "=@" + workflowFile.getAbsolutePath() + "' "
                + w.getWorkflowFileUploadURL());

        String res = procRunner.runCommandLineProcess("curl",
                "-i", "-X", "POST", "--form",
                w.getId().toString() + "=@" + workflowFile.getAbsolutePath(),
                w.getWorkflowFileUploadURL());
        
        System.out.println("\n");
        System.out.println("--------------- OUTPUT FROM CURL ----------------");
        System.out.println(res);
        System.out.println("--------------- END OF OUTPUT FROM CURL ---------");

    }

    /**
     * Using a curl this method uploads via POST a 
     * {@link WorkspaceFile} file to REST service
     * @param w
     * @param file
     * @throws Exception 
     */
    public static void uploadWorkspaceFile(WorkspaceFile w, File file) throws Exception {
        RunCommandLineProcess procRunner = new RunCommandLineProcessImpl();
        System.out.println("TODO SWITCH THIS TO USE JERSEY CLIENT!!!\nAttempting to run this command: curl -i -X POST --form '"
                + w.getId().toString() + "=@" + file.getAbsolutePath() + "' "
                + w.getUploadURL());
        String res = procRunner.runCommandLineProcess("curl",
                "-i", "-X", "POST", "--form",
                w.getId().toString() + "=@" + file.getAbsolutePath(),
                w.getUploadURL());

        System.out.println("\n");
        System.out.println("--------------- OUTPUT FROM CURL ----------------");
        System.out.println(res);
        System.out.println("--------------- END OF OUTPUT FROM CURL ---------");
    }
    

    /**
     * Prints out examples of {@link Workflow}, {@link WorkspaceFile}, {@link Job}, {@link WorkspaceFile}
     * objects in pretty JSON format
     * @throws Exception 
     */
    public static void renderExampleWorkflowsAndTasksAsJson() throws Exception {

        ObjectMapper om = new ObjectMapper();
        ObjectWriter ow = om.writerWithDefaultPrettyPrinter();

        System.out.println("Json for Empty Workflow");
        System.out.println("-----------------------");
        System.out.println(ow.writeValueAsString(getWorkflowWithNoParameters()));
        System.out.flush();
        System.out.println("-----------------------\n\n");

        System.out.println("Json for Workflow with Parameters");
        System.out.println("-----------------------");

        System.out.println(ow.writeValueAsString(getWorkflowWithParameters()));
        System.out.flush();
        System.out.println("-----------------------\n\n");

        System.out.println("Json for Job with 2 parameters and workflow");
        System.out.println("-----------------------");
        System.out.println(ow.writeValueAsString(getJobWithParametersAndWorkflow()));
        System.out.flush();
        System.out.println("-----------------------\n\n");

        System.out.println("Json for WorkspaceFile");
        System.out.println("-----------------------");
        System.out.println(ow.writeValueAsString(getWorkspaceFile()));
        System.out.flush();
        
        
        System.out.println("Json for User");
        System.out.println("-----------------------");
        System.out.println(ow.writeValueAsString(getUser()));
        System.out.flush();
        
        System.out.println("Json for Event");
        System.out.println("-----------------------");
        System.out.println(ow.writeValueAsString(getEvent()));
        System.out.flush();

    }

    /**
     * Creates example {@link Job} with {@link Parameter} objects and a
     * {@link Workflow}
     * @return 
     */
    public static Job getJobWithParametersAndWorkflow() {
        Job j = new Job();
        j.setCreateDate(new Date());
        j.setDownloadURL("http://foo.com/asdflkj");
        j.setEstimatedCpuInSeconds(5345);
        j.setEstimatedDiskInBytes(234234234L);
        j.setEstimatedRunTime(334343);
        j.setFinishDate(new Date());
        j.setStatus(Job.RUNNING_STATUS);
        j.setSchedulerJobId("12322");
        j.setName("some job");
        j.setOwner("someuser");
        j.setStartDate(new Date());
        j.setSubmitDate(new Date());

        Parameter p = new Parameter();
        p.setName("param1");
        p.setValue("some value");

        List<Parameter> params = new ArrayList<>();
        params.add(p);
        j.setParameters(params);
        j.setWorkflow(getWorkflowWithNoParameters());
        return j;
    }

    /**
     * Creates example {@link Workflow} object with no {@link WorkflowParameter} objects
     * @return 
     */
    public static Workflow getWorkflowWithNoParameters() {
        Workflow w = new Workflow();
        w.setId(new Long(10));
        w.setName("workflowname");
        w.setDescription("Contains description of workflow displayable to the user");
        w.setCreateDate(new Date());
        w.setReleaseNotes("Contains release notes for this release of Workflow");
        w.setVersion(2);
        return w;
    }

    /**
     * Creates example {@link Workflow} object with {@link WorkflowParameter} objects
     * all filled with fake data
     * @return example {@link Workflow}
     */
    public static Workflow getWorkflowWithParameters() {
        Workflow w = getWorkflowWithNoParameters();

        WorkflowParameter wp = new WorkflowParameter();
        wp.setType("text");
        wp.setValue("initial value to put in text field");
        wp.setName("foo");
        wp.setDisplayName("Example text field parameter");
        wp.setHelp("Tooltip information goes here.\n"
                + "This Parameter is a basic html text field.\n"
                + "DisplayName is the label that should be shown\n"
                + "to the user\n"
                + "");
        wp.setMaxLength(50);
        wp.setValidationHelp("Text to display to user if validation fails");
        wp.setValidationType("string");
        wp.setValidationRegex("^cheese|wine$");

        List<WorkflowParameter> params = new ArrayList<>();
        params.add(wp);

        wp = new WorkflowParameter();
        wp.setType("dropdown");
        wp.setValue("displayName1==value1;displayName2==value2;displayName3==value3");
        wp.setName("bar");
        wp.setDisplayName("Bar");
        wp.setMaxValue(100);
        wp.setMinValue(0);
        HashMap<String,String> valueMap = new HashMap<>();
        valueMap.put("displayName1", "value1");
        valueMap.put("displayName2", "value2");
        valueMap.put("displayName3", "value3");
                
        wp.setValueMap(valueMap);
        wp.setNameValueDelimiter("==");
        wp.setLineDelimiter(";");
        wp.setHelp("Tooltip goes here.  This is a dropdown field");
        params.add(wp);
        
        w.setParameters(params);
        return w;
    }
    
    /**
     * Creates example {@link WorkspaceFile} object with some fake data
     * @return Example {@link WorkspaceFile}
     */
    public static WorkspaceFile getWorkspaceFile(){
        WorkspaceFile wsp = new WorkspaceFile();
        wsp.setId(new Long(123123));
        wsp.setType("png");
        wsp.setCreateDate(new Date());
        wsp.setDescription("some information");
        wsp.setDeleted(false);
        wsp.setDir(false);
        wsp.setMd5("cec200b3e7c2c013fecdaaa5cceaf526");
        wsp.setName("foo.png");
        wsp.setOwner("bob");
        wsp.setPath("/home/foo/bob/123/foo.png");
        wsp.setSize(new Long(123123));
        return wsp;
    }
    
    /**
     * Creates an example {@link User} object with some fake data
     * @return Example {@link User}
     */
    public static User getUser(){
        User user = new User();
        user.setCreateDate(new Date());
        user.setId(new Long(1));
        user.setLogin("bob");
        user.setToken("sometokenxxxxxx");
        user.setPermissions(Permission.CREATE_USER);
        user.setIpAddress("127.0.0.1");
        return user;
    }
 
    public static Event getEvent(){
        Event e = new Event();
        e.setCity("San Diego");
        e.setCityLatLong("0,0");
        e.setCountry("USA");
        e.setDate(new Date());
        e.setHost("foo.com");
        e.setIpAddress("127.0.0.1");
        e.setMessage("some message");
        e.setRegion("NA");
        return e;
    }
}
