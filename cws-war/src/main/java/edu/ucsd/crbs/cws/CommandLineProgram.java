package edu.ucsd.crbs.cws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.googlecode.objectify.ObjectifyService;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.impl.provider.entity.StringProvider;
import edu.ucsd.crbs.cws.cluster.TaskSubmitter;
import edu.ucsd.crbs.cws.dao.rest.TaskRestDAOImpl;
import edu.ucsd.crbs.cws.workflow.Parameter;
import edu.ucsd.crbs.cws.workflow.Task;
import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.WorkflowFromXmlFactory;
import edu.ucsd.crbs.cws.workflow.WorkflowParameter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.ws.rs.core.MediaType;
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
public class CommandLineProgram {

    public static final String XML_SUFFIX = ".xml";

    public static final String UPLOAD_WF_ARG = "uploadwf";

    public static final String SYNC_WITH_CLUSTER_ARG = "syncwithcluster";

    public static final String HELP_ARG = "h";

    public static final String URL_ARG = "url";
    
    public static final String WF_EXEC_DIR = "execdir";

    public static final String WF_DIR = "wfdir";

    public static final String KEPLER_SCRIPT = "kepler";
    
    public static final String PARENT_WFID_ARG = "parentwf";

    public static final String EXAMPLE_JSON_ARG = "examplejson";

    public static final String PROGRAM_HELP = "\nCRBS Workflow Service Command Line Tools "
            + "\n\nThis program provides options to run Workflow Tasks on the local cluster as well"
            + " as add new Workflows to the CRBS Workflow Service";

    public static void main(String[] args) {
        Task.REFS_ENABLED = false;
        Workflow.REFS_ENABLED = false;
        try {

            OptionParser parser = new OptionParser() {
                {
                    accepts(UPLOAD_WF_ARG, "Add/Update Workflow").withRequiredArg().ofType(File.class).describedAs(".xml or .kar");
                    accepts(SYNC_WITH_CLUSTER_ARG, "Submits & Synchronizes Workflow Tasks on local cluster with CRBS Workflow Webservice").withRequiredArg().ofType(String.class).describedAs("URL");
                    accepts(URL_ARG, "URL to use with --" + UPLOAD_WF_ARG + " flag").withRequiredArg().ofType(String.class).describedAs("URL");
                    accepts(PARENT_WFID_ARG, "Parent Workflow ID").withRequiredArg().ofType(Long.class).describedAs("Workflow ID");
                    accepts(EXAMPLE_JSON_ARG, "Outputs JSON of Task & Workflow objects");
                    accepts(WF_EXEC_DIR,"Workflow Execution Directory").withRequiredArg().ofType(File.class).describedAs("Directory");
                    accepts(WF_DIR,"Workflows Directory").withRequiredArg().ofType(File.class).describedAs("Directory");
                    accepts(KEPLER_SCRIPT,"Kepler").withRequiredArg().ofType(File.class).describedAs("Script");
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
                    && !optionSet.has(EXAMPLE_JSON_ARG)) {
                System.out.println(PROGRAM_HELP + "\n");
                parser.printHelpOn(System.out);
                System.exit(0);
            }

            if (optionSet.has(EXAMPLE_JSON_ARG)) {

                renderExampleWorkflowsAndTasksAsJson();
                System.exit(0);
            }

            if (optionSet.has(SYNC_WITH_CLUSTER_ARG)) {
                if (!optionSet.has(WF_EXEC_DIR)){
                    System.err.println("-"+WF_EXEC_DIR+" is required with -"+SYNC_WITH_CLUSTER_ARG+" flag");
                    System.exit(2);
                }
                if (!optionSet.has(WF_DIR)){
                    System.err.println("-"+WF_DIR+" is required with -"+SYNC_WITH_CLUSTER_ARG+" flag");
                    System.exit(3);
                }
                if (!optionSet.has(KEPLER_SCRIPT)){
                    System.err.println("-"+KEPLER_SCRIPT+" is required with -"+SYNC_WITH_CLUSTER_ARG+" flag");
                    System.exit(4);
                }
                
                File wfExecDir = (File)optionSet.valueOf(WF_EXEC_DIR);
                File wfDir = (File)optionSet.valueOf(WF_DIR);
                File keplerScript = (File)optionSet.valueOf(KEPLER_SCRIPT);
                
                ObjectifyService.ofy();
                String url = (String) optionSet.valueOf(SYNC_WITH_CLUSTER_ARG);
                TaskRestDAOImpl taskDAO = new TaskRestDAOImpl();
                taskDAO.setRestURL(url);
                System.out.println("Running sync with cluster");

                TaskSubmitter submitter = new TaskSubmitter(wfExecDir.getAbsolutePath(),
                        wfDir.getAbsolutePath(),
                        keplerScript.getAbsolutePath());

                List<Task> tasks = taskDAO.getTasks(null, null, true, false, false);
                if (tasks != null) {
                    System.out.println("there are " + tasks.size() + " tasks");
                    for (Task t : tasks) {
                        System.out.println("Task: " + t.getId() + " named: " + t.getName() + " status: " + t.getStatus());
                        String res = submitter.submitTask(t);
                        if (res != null){
                            System.out.println(res);
                        }
                        taskDAO.update(t.getId(), "hello", null, null, null, null, null, null, true, url, t.getJobId());
                    }
                } else {
                    System.out.println("tasks is null");
                }

                System.exit(0);
            }

            Long parentWfId = null;

            String postURL = null;
            if (optionSet.has(URL_ARG)) {
                postURL = (String) optionSet.valueOf(URL_ARG);
            }

            if (optionSet.has(PARENT_WFID_ARG)) {
                parentWfId = (Long) optionSet.valueOf(PARENT_WFID_ARG);
            }

            if (optionSet.has(UPLOAD_WF_ARG)) {
                File workflowFile = (File) optionSet.valueOf(UPLOAD_WF_ARG);
                WorkflowFromXmlFactory xmlFactory = new WorkflowFromXmlFactory();
                xmlFactory.setWorkflowXml(new BufferedInputStream(getInputStreamOfWorkflowMoml(workflowFile)));

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
                        Client client = Client.create(cc);
                        client.setFollowRedirects(true);
                        WebResource resource = client.resource(postURL);
                        String workflowAsJson = om.writeValueAsString(w);

                        String response = resource.type(MediaType.APPLICATION_JSON_TYPE)
                                .entity(workflowAsJson)
                                .post(String.class);
                        Object json = om.readValue(response, Object.class);
                        ObjectWriter ow = om.writerWithDefaultPrettyPrinter();
                        System.out.println(ow.writeValueAsString(json));
                        //System.out.println("response: " + response);

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

    /**
     * Returns an input stream to moml file within path passed in. If the path
     * ends with .xml it is assumed to be a moml file and an inputstream is
     * opened on it. Otherwise the file is assumed to be a .kar file and is
     * opened as a zip file and the first entry within that is a file ending
     * with .xml and has a non zero size is assumed to be a moml file and an
     * inputstream is opened from that path. <br/>
     * It is the responsibility of the caller to close the InputStream
     *
     * @param workflowFile
     * @return InputStream object pointing to start of moml file or null if no
     * moml file is found
     * @throws Exception
     */
    public static InputStream getInputStreamOfWorkflowMoml(final File workflowFile) throws Exception {

        if (workflowFile == null) {
            throw new NullPointerException("workflow file is null");
        }

        //if the path ends with .xml assume it is a moml file and return a FileInputStream
        if (workflowFile.getAbsolutePath().endsWith(XML_SUFFIX)) {
            return new FileInputStream(workflowFile);
        }

        //If we are here, we are assuming the file is a kar (compressed zip file)
        //which is actually a jar file so use JarFile to iterate through all
        //entries til we find a non zero size entry that is not a directory and
        //ends with .xml
        JarFile jf = new JarFile(workflowFile);
        JarEntry je;
        for (Enumeration<JarEntry> e = jf.entries(); e.hasMoreElements();) {
            je = e.nextElement();

            if (je.isDirectory() == true) {
                continue;
            }
            if (je.getSize() == 0 || !je.getName().endsWith(XML_SUFFIX)) {
                continue;
            }

            // if we arrived here we found an xml file open a stream to that
            // file hehehe
            return jf.getInputStream(je);
        }
        //didn't find anything just return null
        return null;
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

    }
    
    public static Task getTaskWithParametersAndWorkflow(){
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
        wp.setType("number");
        wp.setValue("7");
        wp.setName("bar");
        wp.setDisplayName("Bar");
        wp.setMaxValue(100);
        wp.setMinValue(0);

        wp.setHelp("Tooltip goes here.  This is just a number field");
        params.add(wp);
        w.setParameters(params);
        return w;
    }
}
