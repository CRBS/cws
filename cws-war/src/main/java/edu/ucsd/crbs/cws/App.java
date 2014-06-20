package edu.ucsd.crbs.cws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.googlecode.objectify.ObjectifyService;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.impl.provider.entity.StringProvider;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.impl.MultiPartWriter;
import edu.ucsd.crbs.cws.cluster.TaskStatusUpdater;
import edu.ucsd.crbs.cws.cluster.TaskSubmitter;
import edu.ucsd.crbs.cws.dao.rest.TaskRestDAOImpl;
import edu.ucsd.crbs.cws.io.KeplerMomlFromKar;
import edu.ucsd.crbs.cws.rest.Constants;
import edu.ucsd.crbs.cws.workflow.Parameter;
import edu.ucsd.crbs.cws.workflow.Task;
import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.WorkflowFromXmlFactory;
import edu.ucsd.crbs.cws.workflow.WorkflowParameter;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
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
    
    public static final String OWNER_ARG = "owner";

    public static final String SYNC_WITH_CLUSTER_ARG = "syncwithcluster";

    public static final String HELP_ARG = "h";

    public static final String URL_ARG = "url";

    public static final String WF_EXEC_DIR = "execdir";

    public static final String WF_DIR = "wfdir";

    public static final String KEPLER_SCRIPT = "kepler";

    public static final String PARENT_WFID_ARG = "parentwf";

    public static final String EXAMPLE_JSON_ARG = "examplejson";

    public static final String QUEUE = "queue";

    public static final String CAST = "panfishcast";

    public static final String STAT = "panfishstat";

    public static final String LOGIN = "login";

    public static final String TOKEN = "token";

    //public static final String LOAD_TEST = "loadtest";
    public static final String PROGRAM_HELP = "\nCRBS Workflow Service Command Line Tools "
            + "\n\nThis program provides options to run Workflow Tasks on the local cluster as well"
            + " as add new Workflows to the CRBS Workflow Service";

    /**
     * CSV string of statuses for jobs that have not completed or failed
     */
    public static final String NOT_COMPLETED_STATUSES = Task.IN_QUEUE_STATUS + ","
            + Task.PAUSED_STATUS + "," + Task.PENDING_STATUS + "," + Task.RUNNING_STATUS;

    public static void main(String[] args) {
        Task.REFS_ENABLED = false;
        Workflow.REFS_ENABLED = false;
        try {

            OptionParser parser = new OptionParser() {
                {
                    accepts(UPLOAD_WF_ARG, "Add/Update Workflow").withRequiredArg().ofType(File.class).describedAs(".kar");
                    //accepts(LOAD_TEST,"creates lots of workflows and tasks");
                    accepts(SYNC_WITH_CLUSTER_ARG, "Submits & Synchronizes Workflow Tasks on local cluster with CRBS Workflow Webservice").withRequiredArg().ofType(String.class).describedAs("URL");
                    accepts(UPLOAD_FILE_ARG,"Adds Workspace file").withRequiredArg().ofType(File.class);
                    accepts(URL_ARG, "URL to use with --" + UPLOAD_WF_ARG + " flag").withRequiredArg().ofType(String.class).describedAs("URL");
                    accepts(PARENT_WFID_ARG, "Parent Workflow ID").withRequiredArg().ofType(Long.class).describedAs("Workflow ID");
                    accepts(EXAMPLE_JSON_ARG, "Outputs JSON of Task & Workflow objects");
                    accepts(WF_EXEC_DIR, "Workflow Execution Directory").withRequiredArg().ofType(File.class).describedAs("Directory");
                    accepts(WF_DIR, "Workflows Directory").withRequiredArg().ofType(File.class).describedAs("Directory");
                    accepts(KEPLER_SCRIPT, "Kepler").withRequiredArg().ofType(File.class).describedAs("Script");
                    accepts(QUEUE, "SGE Queue").withRequiredArg().ofType(String.class).describedAs("Queue");
                    accepts(CAST, "Panfishcast binary").withRequiredArg().ofType(File.class).describedAs("panfishcast");
                    accepts(STAT, "Panfishstat binary").withRequiredArg().ofType(File.class).describedAs("panfishstat");
                    accepts(LOGIN, "User Login").withRequiredArg().ofType(String.class).describedAs("username");
                    accepts(TOKEN, "User Token").withRequiredArg().ofType(String.class).describedAs("token");
                    accepts(OWNER_ARG,"Owner").withRequiredArg().ofType(String.class);
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

            if (optionSet.has(HELP_ARG)
                    || (!optionSet.has(SYNC_WITH_CLUSTER_ARG) && !optionSet.has(UPLOAD_WF_ARG))
                    && !optionSet.has(EXAMPLE_JSON_ARG) && !optionSet.has(UPLOAD_FILE_ARG)) {
                System.out.println(PROGRAM_HELP + "\n");
                parser.printHelpOn(System.out);
                System.exit(0);
            }

            if (optionSet.has(EXAMPLE_JSON_ARG)) {

                renderExampleWorkflowsAndTasksAsJson();
                System.exit(0);
            }

            if (optionSet.has(SYNC_WITH_CLUSTER_ARG)) {
                // @TODO NEED TO MAKE JOPT DO THIS REQUIRED FLAG CHECKING STUFF
                if (!optionSet.has(WF_EXEC_DIR)) {
                    System.err.println("-" + WF_EXEC_DIR + " is required with -" + SYNC_WITH_CLUSTER_ARG + " flag");
                    System.exit(2);
                }
                if (!optionSet.has(WF_DIR)) {
                    System.err.println("-" + WF_DIR + " is required with -" + SYNC_WITH_CLUSTER_ARG + " flag");
                    System.exit(3);
                }
                if (!optionSet.has(KEPLER_SCRIPT)) {
                    System.err.println("-" + KEPLER_SCRIPT + " is required with -" + SYNC_WITH_CLUSTER_ARG + " flag");
                    System.exit(4);
                }

                if (!optionSet.has(CAST)) {
                    System.err.println("-" + CAST + " is required with -" + SYNC_WITH_CLUSTER_ARG + " flag");
                    System.exit(5);
                }

                if (!optionSet.has(STAT)) {
                    System.err.println("-" + STAT + " is required with -" + SYNC_WITH_CLUSTER_ARG + " flag");
                    System.exit(6);
                }

                if (!optionSet.has(QUEUE)) {
                    System.err.println("-" + QUEUE + " is required with -" + SYNC_WITH_CLUSTER_ARG + " flag");
                    System.exit(7);
                }

                if (!optionSet.has(LOGIN)) {
                    System.err.println("-" + LOGIN + " is required with -" + SYNC_WITH_CLUSTER_ARG + " flag");
                    System.exit(8);
                }
                if (!optionSet.has(TOKEN)) {
                    System.err.println("-" + TOKEN + " is required with -" + SYNC_WITH_CLUSTER_ARG + " flag");
                    System.exit(9);
                }

                File castFile = (File) optionSet.valueOf(CAST);
                String castPath = castFile.getAbsolutePath();

                File statFile = (File) optionSet.valueOf(STAT);
                String statPath = statFile.getAbsolutePath();

                String queue = (String) optionSet.valueOf(QUEUE);

                File wfExecDir = (File) optionSet.valueOf(WF_EXEC_DIR);
                File wfDir = (File) optionSet.valueOf(WF_DIR);
                File keplerScript = (File) optionSet.valueOf(KEPLER_SCRIPT);

                ObjectifyService.ofy();
                String url = (String) optionSet.valueOf(SYNC_WITH_CLUSTER_ARG);
                TaskRestDAOImpl taskDAO = new TaskRestDAOImpl();
                taskDAO.setRestURL(url);
                taskDAO.setLogin((String) optionSet.valueOf(LOGIN));
                taskDAO.setToken((String) optionSet.valueOf(TOKEN));

                System.out.println("Running sync with cluster");

                // Submit tasks to scheduler
                TaskSubmitter submitter = new TaskSubmitter(taskDAO,
                        wfExecDir.getAbsolutePath(),
                        wfDir.getAbsolutePath(),
                        keplerScript.getAbsolutePath(),
                        castPath, queue,
                        (String) optionSet.valueOf(LOGIN),
                        (String) optionSet.valueOf(TOKEN),
                        url);

                submitter.submitTasks();

                // Update task status
                TaskStatusUpdater updater = new TaskStatusUpdater(taskDAO, statPath);
                updater.updateTasks();

                System.exit(0);
            }

            if (optionSet.has(UPLOAD_FILE_ARG)){
                 String postURL = null;
                if (optionSet.has(URL_ARG)) {
                    postURL = (String) optionSet.valueOf(URL_ARG);
                    if (!optionSet.has(LOGIN)) {
                        System.err.println("-" + LOGIN + " is required with -" + UPLOAD_FILE_ARG + " and -"+URL_ARG+" flag");
                        System.exit(10);
                    }
                    if (!optionSet.has(TOKEN)) {
                        System.err.println("-" + TOKEN + " is required with -" + UPLOAD_FILE_ARG + " and -"+URL_ARG+" flag");
                        System.exit(11);
                    }
                }
                
                File file = (File)optionSet.valueOf(UPLOAD_FILE_ARG);
                WorkspaceFile wsp = new WorkspaceFile();
                wsp.setName(file.getName());
                wsp.setSize(file.length());
                wsp.setDir(file.isDirectory());
                if (optionSet.has(OWNER_ARG)){
                    wsp.setOwner((String)optionSet.valueOf(OWNER_ARG));
                }
                
                ObjectMapper om = new ObjectMapper();
                if (postURL == null){
                       System.out.println("\n--- JSON Representation of WorkspaceFile ---");
                        ObjectWriter ow = om.writerWithDefaultPrettyPrinter();
                        System.out.println(ow.writeValueAsString(wsp));
                        System.out.flush();
                        System.out.println("---------------------------------------");
                        System.exit(0);
                }
                
                
                ClientConfig cc = new DefaultClientConfig();
                        cc.getClasses().add(StringProvider.class);
                        cc.getClasses().add(MultiPartWriter.class);
                        Client client = Client.create(cc);
                        client.setFollowRedirects(true);
                        WebResource resource = client.resource(postURL);
                        String workspaceFileAsJson = om.writeValueAsString(wsp);

                        MultivaluedMap queryParams = new MultivaluedMapImpl();

                        //add authentication tokens
                        queryParams.add(Constants.USER_LOGIN_PARAM, (String) optionSet.valueOf(LOGIN));
                        queryParams.add(Constants.USER_TOKEN_PARAM, (String) optionSet.valueOf(TOKEN));

                        String response = resource.queryParams(queryParams).type(MediaType.APPLICATION_JSON_TYPE)
                                .entity(workspaceFileAsJson)
                                .post(String.class);
                        WorkspaceFile workspaceFileRes = om.readValue(response, WorkspaceFile.class);
                        ObjectWriter ow = om.writerWithDefaultPrettyPrinter();
                        //System.out.println(ow.writeValueAsString(workflowRes));

                        if (workspaceFileRes.getUploadURL() == null) {
                            throw new Exception("No upload url found for workflow!!!"
                                    + ow.writeValueAsString(workspaceFileRes));
                        }

                        // TODO FIX THIS
                        // I gave up trying to get the jersey client to post the
                        // file so as a backup I'm just calling curl
                        uploadWorkspaceFile(workspaceFileRes,file);
                
                System.exit(0);
            }
            
            if (optionSet.has(UPLOAD_WF_ARG)) {

                Long parentWfId = null;

                String postURL = null;
                if (optionSet.has(URL_ARG)) {
                    postURL = (String) optionSet.valueOf(URL_ARG);
                    if (!optionSet.has(LOGIN)) {
                        System.err.println("-" + LOGIN + " is required with -" + UPLOAD_WF_ARG + " and -"+URL_ARG+" flag");
                        System.exit(10);
                    }
                    if (!optionSet.has(TOKEN)) {
                        System.err.println("-" + TOKEN + " is required with -" + UPLOAD_WF_ARG + " and -"+URL_ARG+" flag");
                        System.exit(11);
                    }
                }

                if (optionSet.has(PARENT_WFID_ARG)) {
                    parentWfId = (Long) optionSet.valueOf(PARENT_WFID_ARG);
                }

                File workflowFile = (File) optionSet.valueOf(UPLOAD_WF_ARG);
                WorkflowFromXmlFactory xmlFactory = new WorkflowFromXmlFactory();
                xmlFactory.setWorkflowXml(new BufferedInputStream(KeplerMomlFromKar.getInputStreamOfWorkflowMoml(workflowFile)));

                Workflow w = xmlFactory.getWorkflow();
                if (w != null) {
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
                        ClientConfig cc = new DefaultClientConfig();
                        cc.getClasses().add(StringProvider.class);
                        cc.getClasses().add(MultiPartWriter.class);
                        Client client = Client.create(cc);
                        client.setFollowRedirects(true);
                        WebResource resource = client.resource(postURL);
                        String workflowAsJson = om.writeValueAsString(w);

                        MultivaluedMap queryParams = new MultivaluedMapImpl();

                        //add authentication tokens
                        queryParams.add(Constants.USER_LOGIN_PARAM, (String) optionSet.valueOf(LOGIN));
                        queryParams.add(Constants.USER_TOKEN_PARAM, (String) optionSet.valueOf(TOKEN));

                        String response = resource.queryParams(queryParams).type(MediaType.APPLICATION_JSON_TYPE)
                                .entity(workflowAsJson)
                                .post(String.class);
                        Workflow workflowRes = om.readValue(response, Workflow.class);
                        ObjectWriter ow = om.writerWithDefaultPrettyPrinter();
                        //System.out.println(ow.writeValueAsString(workflowRes));

                        if (workflowRes.getWorkflowFileUploadURL() == null) {
                            throw new Exception("No upload url found for workflow!!!"
                                    + ow.writeValueAsString(workflowRes));
                        }

                        // TODO FIX THIS
                        // I gave up trying to get the jersey client to post the
                        // file so as a backup I'm just calling curl
                        uploadWorkflowFile(workflowRes, workflowFile);

                        /* TODO GET THIS WORKING!!!! Keep getting
                         No MessageBodyWriter for body part of type 'java.io.File' and media type 'application/octet-stream'
                         client = Client.create(cc);
                         client.setFollowRedirects(true);
                         resource = client.resource(workflowRes.getWorkflowFileUploadURL());
                         FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
                         formDataMultiPart.field("filename",workflowRes.getId().toString()+".kar");
                        
                         FileDataBodyPart fdp = new FileDataBodyPart("file",workflowFile);
                         formDataMultiPart.bodyPart(fdp);
                        
                         String res = resource.type(MediaType.MULTIPART_FORM_DATA).
                         accept(MediaType.TEXT_HTML).post(String.class,formDataMultiPart);
                        
                         System.out.println(res);
                         */
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

    public static void uploadWorkflowFile(Workflow w, File workflowFile) throws Exception {

        System.out.println("TODO SWITCH THIS TO USE JERSEY CLIENT!!!\nAttempting to run this command: curl -i -X POST --form '"
                + w.getId().toString() + "=@" + workflowFile.getAbsolutePath() + "' "
                + w.getWorkflowFileUploadURL());
        ProcessBuilder pb = new ProcessBuilder("curl",
                "-i", "-X", "POST", "--form",
                w.getId().toString() + "=@" + workflowFile.getAbsolutePath(),
                w.getWorkflowFileUploadURL());

        pb.redirectErrorStream(true);

        Process p = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

        StringBuilder sb = new StringBuilder();

        String line = reader.readLine();
        boolean firstLine = true;
        String jobId = null;
        while (line != null) {
            sb.append(line).append("\n");
            line = reader.readLine();
        }
        reader.close();

        if (p.waitFor() != 0) {
            throw new Exception("Non zero exit code from curl: " + sb.toString());
        }
        System.out.println("\n");
        System.out.println("--------------- OUTPUT FROM CURL ----------------");
        System.out.println(sb.toString());
        System.out.println("--------------- END OF OUTPUT FROM CURL ---------");

    }

    
    public static void uploadWorkspaceFile(WorkspaceFile w, File file) throws Exception {

        System.out.println("TODO SWITCH THIS TO USE JERSEY CLIENT!!!\nAttempting to run this command: curl -i -X POST --form '"
                + w.getId().toString() + "=@" + file.getAbsolutePath() + "' "
                + w.getUploadURL());
        ProcessBuilder pb = new ProcessBuilder("curl",
                "-i", "-X", "POST", "--form",
                w.getId().toString() + "=@" + file.getAbsolutePath(),
                w.getUploadURL());

        pb.redirectErrorStream(true);

        Process p = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

        StringBuilder sb = new StringBuilder();

        String line = reader.readLine();
        boolean firstLine = true;
        String jobId = null;
        while (line != null) {
            sb.append(line).append("\n");
            line = reader.readLine();
        }
        reader.close();

        if (p.waitFor() != 0) {
            throw new Exception("Non zero exit code from curl: " + sb.toString());
        }
        System.out.println("\n");
        System.out.println("--------------- OUTPUT FROM CURL ----------------");
        System.out.println(sb.toString());
        System.out.println("--------------- END OF OUTPUT FROM CURL ---------");
    }
    

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

        System.out.println("Json for Task with 2 parameters and workflow");
        System.out.println("-----------------------");
        System.out.println(ow.writeValueAsString(getTaskWithParametersAndWorkflow()));
        System.out.flush();
        System.out.println("-----------------------\n\n");

        System.out.println("Json for WorkspaceFile");
        System.out.println("-----------------------");
        System.out.println(ow.writeValueAsString(getWorkspaceFile()));
        System.out.flush();
    }

    public static Task getTaskWithParametersAndWorkflow() {
        Task t = new Task();
        t.setCreateDate(new Date());
        t.setDownloadURL("http://foo.com/asdflkj");
        t.setEstimatedCpuInSeconds(5345);
        t.setEstimatedDiskInBytes(234234234L);
        t.setEstimatedRunTime(334343);
        t.setFinishDate(new Date());
        t.setStatus(Task.RUNNING_STATUS);
        t.setJobId("12322");
        t.setName("some task");
        t.setOwner("someuser");
        t.setStartDate(new Date());
        t.setSubmitDate(new Date());

        Parameter p = new Parameter();
        p.setName("param1");
        p.setValue("some value");

        List<Parameter> params = new ArrayList<>();
        params.add(p);
        t.setParameters(params);
        t.setWorkflow(getWorkflowWithNoParameters());
        return t;
    }

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
}
