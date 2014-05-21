package edu.ucsd.crbs.cws.cluster;

import edu.ucsd.crbs.cws.io.ResourceToExecutableScriptWriterImpl;
import edu.ucsd.crbs.cws.io.StringReplacer;
import edu.ucsd.crbs.cws.rest.Constants;
import edu.ucsd.crbs.cws.workflow.Parameter;
import edu.ucsd.crbs.cws.workflow.Task;
import java.io.File;
import java.util.LinkedHashMap;

/**
 * Creates a {@link TASK_CMD_SH} script that can run the Workflow Task
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class TaskCmdScriptCreatorImpl implements TaskCmdScriptCreator, StringReplacer {

    public static final String HYPHEN = "-";
    public static final String SPACE = " ";

    /**
     * Name of script that will run the Workflow Task
     */
    public static final String TASK_CMD_SH = "taskCmd.sh";
    
    /**
     * Path to {@link TASK_CMD_SH} template script
     */
    public static final String TASK_CMD_SH_TEMPLATE = "/taskcmd.sh.template";

    /**
     * Suffix for Workflow KAR files
     */
    public static final String WORKFLOW_SUFFIX = ".kar";

    /**
     * Flags to tell kepler to run in command line mode and to redirect gui display
     * actors to files
     */
    public static final String KEPLER_RUN_CMDLINE_ARGS = " -runwf -redirectgui ";

    /**
     * Token in {@link TASK_CMD_SH} that will be replaced with path to Kepler script
     */
    public static final String KEPLER_SH_TOKEN = "@@KEPLER_SH@@";
    
    /**
     * Token in {@link TASK_CMD_SH} that will be replaced with Kepler command
     * line arguments needed to run the workflow
     */
    public static final String TASK_ARGS_TOKEN = "@@TASK_ARGS@@";

    /**
     * Special parameter set to output directory of running task
     */
    public static final String CWS_OUTPUTDIR = "CWS_outputdir";

    /**
     * Special parameter set to output directory of running task
     */
    public static final String CWS_USER = "CWS_user";

    /**
     * Special parameter set to output directory of running task
     */
    public static final String CWS_TASKNAME = "CWS_taskname";

    /**
     * Base directory under which the Workflow kar files reside
     */
    private final String _workflowsDir;
    
    /**
     * Path to Kepler script
     */
    private final String _keplerScript;
    
    /**
     * Arguments to pass to Kepler script
     */
    private String _taskArgs;
    
    /**
     * The working directory for the Workflow Task
     */
    private String _workingDir;

    /**
     * Contains a mapping of ASCII characters to HTML escape codes.  This
     * is needed to replace certain characters with special characters when
     * passing arguments to Kepler script
     */
    private final LinkedHashMap<String, String> m_EscapeMap;

    /**
     * Replaces occurrences of @@TASK_ARGS@@ @@KEPLER_SH@@ with correct values
     *
     * @param line
     * @return
     */
    @Override
    public String replace(String line) {
        return line.replace(KEPLER_SH_TOKEN, _keplerScript).
                replace(TASK_ARGS_TOKEN, _taskArgs);
    }

    public TaskCmdScriptCreatorImpl(final String workflowsDir, final String keplerScript) {
        _workflowsDir = workflowsDir;
        _keplerScript = keplerScript;

        m_EscapeMap = new LinkedHashMap<>();
        m_EscapeMap.put("&", "&#38;");
        m_EscapeMap.put(" ", "&#32;");
        m_EscapeMap.put("[!]", "&#33;");
        m_EscapeMap.put("\"", "&#34;");
        m_EscapeMap.put("%", "&#37;");

        m_EscapeMap.put("'", "&#39;");
        m_EscapeMap.put("[(]", "&#40;");
        m_EscapeMap.put("[)]", "&#41;");
        m_EscapeMap.put("[*]", "&#42;");
        //m_EscapeMap.put("/","&#47;");

        m_EscapeMap.put("<", "&#60;");
        m_EscapeMap.put("=", "&#61;");
        m_EscapeMap.put(">", "&#62;");
    }

    /**
     * Creates {@link TASK_CMD_SH} script in taskDirectory.  This script can later
     * be submitted to SGE to run the Workflow Task
     * @param taskDirectory Should be set to base directory of Workflow Task and 
     * there should exist a {@link Constants.OUTPUS_DIR_NAME} within this directory
     * @param t Task to run
     * @return Full path to {@link TASK_CMD_SH} script that can be used to run the Task
     * @throws Exception If there is an error, duh
     */
    @Override
    public String create(final String taskDirectory, Task t) throws Exception {

        _workingDir = taskDirectory + File.separator + Constants.OUTPUTS_DIR_NAME;

        _taskArgs = generateTaskArguments(taskDirectory, t);

        ResourceToExecutableScriptWriterImpl resToFile = new ResourceToExecutableScriptWriterImpl();

        String taskCmd = _workingDir + File.separator + TASK_CMD_SH;

        resToFile.writeResourceToScript(TASK_CMD_SH_TEMPLATE,
                taskCmd, this);

        return taskCmd;
    }

    /**
     * Iterates through Parameters of Task and generates a String of flags and values
     * @param taskDirectory Base Directory path of Task
     * @param t Task to generate arguments for
     * @return String containing flags and values that can be passed to Kepler script
     * @throws Exception 
     */
    private String generateTaskArguments(final String taskDirectory, Task t) throws Exception {

        StringBuilder sb = new StringBuilder();
        //set the initial flags to run a workflow and redirect display actors
        // to a directory
        sb.append(KEPLER_RUN_CMDLINE_ARGS).append(_workingDir);

        String value;
        //append all command line arguments
        for (Parameter param : t.getParameters()) {

            //need to deal with special parameters!!!
            if (param.getName().equals(TaskCmdScriptCreatorImpl.CWS_OUTPUTDIR)) {
                value = _workingDir;
            } else if (param.getName().equals(TaskCmdScriptCreatorImpl.CWS_TASKNAME)) {
                value = t.getName();
            } else if (param.getName().equals(TaskCmdScriptCreatorImpl.CWS_USER)) {
                value = t.getOwner();
            } else {
                value = param.getValue();
            }

            sb.append(SPACE).append(HYPHEN).append(param.getName()).append(SPACE);
            sb.append("\"").append(escapeString(value)).append("\"");
        }

        //add path to workflow
        sb.append(SPACE).append(_workflowsDir).append(File.separator);
        sb.append(t.getWorkflow().getId()).append(File.separator);
        sb.append(t.getWorkflow().getId()).append(WORKFLOW_SUFFIX);

        return sb.toString();
    }

    /**
     * Replaces special characters with html codes otherwise the parameters dont
     * get passed properly
     *
     * @param val String to escape if ${ID} is the value it is replaced with id
     * of WorkflowTask if ${OUTPUTDIR} is the value it is replaced with
     * redirectDisplay path if ${TASKNAME} is the value it is replaced with
     * JobName set by user or unset if not set by user
     * @param t Task needed to pull out replacement values
     */
    private String escapeString(final String val) {
        if (val == null) {
            return null;
        }

        String tmpVal = val;

        for (String k : m_EscapeMap.keySet()) {
            tmpVal = tmpVal.replaceAll(k, m_EscapeMap.get(k));
        }
        return tmpVal;
    }
}
