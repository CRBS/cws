package edu.ucsd.crbs.cws.cluster;

import edu.ucsd.crbs.cws.io.ResourceToExecutableScriptWriterImpl;
import edu.ucsd.crbs.cws.io.StringReplacer;
import edu.ucsd.crbs.cws.rest.Constants;
import edu.ucsd.crbs.cws.workflow.Parameter;
import edu.ucsd.crbs.cws.workflow.Task;
import java.io.File;
import java.util.LinkedHashMap;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class TaskCmdScriptCreatorImpl implements TaskCmdScriptCreator,StringReplacer {

    
    public static final String HYPHEN = "-";
    public static final String SPACE = " ";
    public static final String TASK_CMD_SH_TEMPLATE = "/taskcmd.sh.template";
    public static final String TASK_CMD_SH = "taskCmd.sh";
    
    public static final String WORKFLOW_SUFFIX = ".kar";

    public static final String KEPLER_SH_TOKEN = "@@KEPLER_SH@@";
    public static final String TASK_ARGS_TOKEN = "@@TASK_ARGS@@";    
    private final String _workflowsDir;
    private final String _keplerScript;
    private String _taskArgs;
    
    private LinkedHashMap<String, String> m_EscapeMap;

    /**
     * Replaces occurrences of @@TASK_ARGS@@ @@KEPLER_SH@@ with correct values
     * @param line
     * @return 
     */
    @Override
    public String replace(String line) {
        return line.replace(KEPLER_SH_TOKEN, _keplerScript).replace(TASK_ARGS_TOKEN, _taskArgs);
    }

    
    public TaskCmdScriptCreatorImpl(final String workflowsDir,final String keplerScript){
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
    
    @Override
    public String create(final String taskDirectory, Task t) throws Exception {


        _taskArgs = generateTaskArguments(taskDirectory,t);
        
        ResourceToExecutableScriptWriterImpl resToFile = new ResourceToExecutableScriptWriterImpl();
        
        String taskCmd = taskDirectory+File.separator+Constants.OUTPUTS_DIR_NAME+
                File.separator+TASK_CMD_SH;
        
        resToFile.writeResourceToScript(TASK_CMD_SH_TEMPLATE,
                taskCmd,this);
        
        return taskCmd;
    }

    private String generateTaskArguments(final String taskDirectory,Task t) throws Exception{
        StringBuilder sb = new StringBuilder();
        
        //set the initial flags to run a workflow and redirect display actors
        // to a directory
        sb.append(" -runwf -redirectgui ").append(taskDirectory);
        sb.append(File.separator).append(Constants.OUTPUTS_DIR_NAME);
        
        //append all command line arguments
        for (Parameter param : t.getParameters()){
            
            //need to deal with hidden parameters too!!!
            
            sb.append(SPACE).append(HYPHEN).append(param.getName()).append(SPACE);
            sb.append("\"").append(escapeString(param.getValue(),t)).append("\"");
        }
        
        //add path to workflow
        sb.append(SPACE).append(_workflowsDir).append(File.separator);
        sb.append(t.getWorkflow().getId()).append(File.separator);
        sb.append(t.getWorkflow().getId()).append(WORKFLOW_SUFFIX);
        
        return sb.toString();
    }
    
    
     /**
     * Replaces special characters with html codes otherwise the parameters
     * dont get passed properly
     *
     * @param val String to escape if ${ID} is the value it is replaced with id of WorkflowTask
     *              if ${OUTPUTDIR} is the value it is replaced with redirectDisplay path
     *              if ${TASKNAME} is the value it is replaced with JobName set by user or unset if not set by user
     * @param t Task needed to pull out replacement values
     */
    private String escapeString(final String val, Task t) {
        if (val == null) {
            return null;
        }
        
        String tmpVal = val;

        if (val.equals("${TASKNAME}")){
            if (t.getName() == null || t.getName().matches("^\\s*$")){
               tmpVal = "unset";
            }
            else {
               tmpVal = t.getName();
            }            
        }

        for (String k : m_EscapeMap.keySet()) {
            if (val.equals("${ID}")) {
                tmpVal = Long.toString(t.getId());
            } else if (val.equals("${OUTPUTDIR}")) {
                tmpVal = "unset"; //need to set to outputs directory of task
            } else {
                tmpVal = tmpVal.replaceAll(k, m_EscapeMap.get(k));
            }
        }
        return tmpVal;
    }
    
}
