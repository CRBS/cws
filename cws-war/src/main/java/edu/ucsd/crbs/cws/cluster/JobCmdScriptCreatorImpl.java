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

package edu.ucsd.crbs.cws.cluster;

import edu.ucsd.crbs.cws.io.ResourceToExecutableScriptWriterImpl;
import edu.ucsd.crbs.cws.io.StringReplacer;
import edu.ucsd.crbs.cws.rest.Constants;
import edu.ucsd.crbs.cws.workflow.Parameter;
import edu.ucsd.crbs.cws.workflow.Job;
import java.io.File;
import java.util.LinkedHashMap;

/**
 * Creates a {@link JOB_CMD_SH} script that can run the Workflow Job
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class JobCmdScriptCreatorImpl implements JobCmdScriptCreator, StringReplacer {

    public static final String HYPHEN = "-";
    public static final String SPACE = " ";

    /**
     * Name of script that will run the Workflow Job
     */
    public static final String JOB_CMD_SH = "jobCmd.sh";
    
    /**
     * Path to {@link JOB_CMD_SH} template script
     */
    public static final String JOB_CMD_SH_TEMPLATE = "/jobcmd.sh.template";

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
     * Token in {@link JOB_CMD_SH} that will be replaced with path to Kepler script
     */
    public static final String KEPLER_SH_TOKEN = "@@KEPLER_SH@@";
    
    /**
     * Token in {@link JOB_CMD_SH} that will be replaced with Kepler command
     * line arguments needed to run the workflow
     */
    public static final String JOB_ARGS_TOKEN = "@@JOB_ARGS@@";
    
    /**
     * Token in {@link JOB_CMD_SH} that will be replaced with path to java
     * binary
     */
    
    public static final String JAVA_TOKEN = "@@JAVA@@";
    
    /**
     * Token in {@link JOB_CMD_SH} that will be replaced with the name of the
     * job
     */
    public static final String JOB_NAME_TOKEN = "@@JOB_NAME@@";
    
    /**
     * Token in {@link JOB_CMD_SH} that will be replaced with login of the user
     * running the job
     */
    public static final String USER_TOKEN = "@@USER@@";
    
    /**
     * Token in {@link JOB_CMD_SH} that will be replaced with id of Job
     */
    public static final String JOB_ID_TOKEN = "@@JOB_ID@@";
    
    /**
     * Token in {@link JOB_CMD_SH} that will be replaced with email address associated
     * with user
     */
    public static final String NOTIFY_EMAIL_TOKEN = "@@NOTIFY_EMAIL@@";
    
    /**
     * Token in {@link JOB_CMD_SH} that will be replaced with blind carbon copy email
     * address
     */
    public static final String BCC_EMAIL_TOKEN = "@@BCC_EMAIL@@";
    
    public static final String PORTAL_URL_TOKEN = "@@PORTAL_URL@@";
    
    public static final String PORTAL_NAME_TOKEN = "@@PORTAL_NAME@@";
    
    public static final String PROJECT_TOKEN = "@@PROJECT@@";
    
    public static final String HELP_EMAIL_TOKEN = "@@HELP_EMAIL@@";

    
    public static final String REGISTER_OUTPUT_TO_WORKSPACE_TOKEN = "@@REGISTER_OUTPUT_TO_WORKSPACE@@";
    
    public static final String UPDATE_WORKSPACE_PATH_TOKEN = "@@UPDATE_WORKSPACE_PATH@@";

    public static final String REGISTER_WSF_OUTPUT = "registerworkspacefile.out";

    public static final String UPDATE_WSF_OUTPUT = "updateworkspacefile.out";
    
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
    private String _jobArgs;
    
    /**
     * The working directory for the Workflow Task
     */
    private String _workingDir;
    
    private String _registerOutputWorkspaceFile;
    
    private String _updateOutputWorkspaceFilePath;

    private String _registerUpdateJar;
    
    private String _jobId;
    
    private String _user;
    
    private String _jobName;
    
    private String _userEmail;
    
    private JobEmailNotificationData _emailNotifyData;
    
    
    
    
    /**
     * Contains a mapping of ASCII characters to HTML escape codes.  This
     * is needed to replace certain characters with special characters when
     * passing arguments to Kepler script
     */
    private final LinkedHashMap<String, String> m_EscapeMap;

    /**
     * Replaces occurrences of @@JOB_ARGS@@ @@KEPLER_SH@@ with correct values
     *
     * @param line
     * @return
     */
    @Override
    public String replace(String line) {
        return line.replace(KEPLER_SH_TOKEN, _keplerScript).
                replace(JOB_ARGS_TOKEN, _jobArgs).
                replace(JAVA_TOKEN,"java").
                replace(REGISTER_OUTPUT_TO_WORKSPACE_TOKEN,_registerOutputWorkspaceFile).
                replace(UPDATE_WORKSPACE_PATH_TOKEN,_updateOutputWorkspaceFilePath).
                replace(JOB_NAME_TOKEN,_jobName).
                replace(USER_TOKEN,_user).
                replace(NOTIFY_EMAIL_TOKEN,_userEmail).
                replace(PROJECT_TOKEN,_emailNotifyData.getProject()).
                replace(PORTAL_NAME_TOKEN,_emailNotifyData.getPortalName()).
                replace(PORTAL_URL_TOKEN,_emailNotifyData.getPortalURL()).
                replace(HELP_EMAIL_TOKEN,_emailNotifyData.getHelpEmail()).
                replace(BCC_EMAIL_TOKEN,_emailNotifyData.getBccEmail()).
                replace(JOB_ID_TOKEN,_jobId);
                
    }

    public JobCmdScriptCreatorImpl(final String workflowsDir, final String keplerScript,
            final String registerUpdateJar,
            JobEmailNotificationData emailNotifyData) {
       
        _emailNotifyData = emailNotifyData;
        
        _workflowsDir = workflowsDir;
        _keplerScript = keplerScript;
        _registerUpdateJar = registerUpdateJar;
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
     * Creates {@link JOB_CMD_SH} script in jobDirectory.  This script can later
     * be submitted to SGE to run the Workflow Job
     * @param jobDirectory Should be set to base directory of Workflow JOb and 
     * there should exist a {@link Constants.OUTPUS_DIR_NAME} within this directory
     * @param j Job to run
     * @return Full path to {@link JOB_CMD_SH} script that can be used to run the JOb
     * @throws Exception If there is an error, duh
     */
    @Override
    public String create(final String jobDirectory, Job j) throws Exception {
        
        _workingDir = jobDirectory + File.separator + Constants.OUTPUTS_DIR_NAME;

        _jobArgs = generateJobArguments(j);

        
        if (j.getOwner() != null){
            _user = j.getOwner().replace("\""," ");
        }
        else {
            _user = "Unknown";
        }
        
        if (j.getId() != null){
            _jobId = j.getId().toString();
        }
        else {
            _jobId = "Unknown";
        }
        
        if (j.getName() != null){
            _jobName = j.getName().replace("\""," ");
        }
        else {
            _jobName = "Unknown";
        }
        
        setUserEmail(j);
        
        _registerOutputWorkspaceFile = " -jar "+_registerUpdateJar+
                " --registerfile \""+_workingDir+"\" --jobid "+j.getId()+
                " --name \""+j.getName()+" [Job Output]\""+
                " --owner \""+j.getOwner()+"\""+
                " --type \""+j.getWorkflow().getName()+"\""+
                " --description \"Output of Workflow Job ("+j.getId()+
                ") [Workflow Ver "+j.getWorkflow().getVersion()+" ]\" >> "+
                jobDirectory+File.separator+REGISTER_WSF_OUTPUT+" 2>&1";
        
        _updateOutputWorkspaceFilePath = " -jar "+_registerUpdateJar+
                " --updatepath \"`cat "+jobDirectory+File.separator+REGISTER_WSF_OUTPUT+" | sed \"s/^Workspace.*: //\"`\""+
                " --path \""+_workingDir+"\""+
                " --size `du "+_workingDir+" -bs | sed \"s/\\W*\\/.*//\"` >> "+
                jobDirectory+File.separator+UPDATE_WSF_OUTPUT+" 2>&1";
        
        ResourceToExecutableScriptWriterImpl resToFile = new ResourceToExecutableScriptWriterImpl();

        String jobCmd = _workingDir + File.separator + JOB_CMD_SH;

        resToFile.writeResourceToScript(JOB_CMD_SH_TEMPLATE,
                jobCmd, this);

        return jobCmd;
    }

    private void setUserEmail(Job j) throws Exception {
        
        _userEmail = "";
        if (j.getParameters() == null){
            return;
        }
        for (Parameter param : j.getParameters()){
            if (param.getName().equals(Constants.CWS_NOTIFYEMAIL)){
                if (param.getValue() != null && !param.getValue().trim().equals("")){
                    this._userEmail = param.getValue();
                }
            }
        }
    }
    
    /**
     * Iterates through Parameters of Job and generates a String of flags and values
     * @param jobDirectory Base Directory path of Task
     * @param j Job to generate arguments for
     * @return String containing flags and values that can be passed to Kepler script
     * @throws Exception 
     */
    private String generateJobArguments(Job j) throws Exception {

        StringBuilder sb = new StringBuilder();
        //set the initial flags to run a workflow and redirect display actors
        // to a directory
        sb.append(KEPLER_RUN_CMDLINE_ARGS).append(_workingDir);

        String value;
        //append all command line arguments
        for (Parameter param : j.getParameters()) {

            //need to deal with special parameters!!!
            if (param.getName().equals(Constants.CWS_OUTPUTDIR)) {
                value = _workingDir;
            } else if (param.getName().equals(Constants.CWS_JOBNAME)) {
                value = j.getName();
            } else if (param.getName().equals(Constants.CWS_USER)) {
                value = j.getOwner();
            } else if (param.getName().equals(Constants.CWS_JOBID)){
                value = j.getId().toString();
            } else {
                value = param.getValue();
            }

            sb.append(SPACE).append(HYPHEN).append(param.getName()).append(SPACE);
            sb.append("\"").append(escapeString(value)).append("\"");
        }

        //add path to workflow
        sb.append(SPACE).append(_workflowsDir).append(File.separator);
        sb.append(j.getWorkflow().getId()).append(File.separator);
        sb.append(j.getWorkflow().getId()).append(WORKFLOW_SUFFIX);

        return sb.toString();
    }

    /**
     * Replaces special characters with html codes otherwise the parameters dont
     * get passed properly
     * @param val source String
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
