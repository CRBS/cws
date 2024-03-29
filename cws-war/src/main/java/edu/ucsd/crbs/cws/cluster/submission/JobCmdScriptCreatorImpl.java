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

package edu.ucsd.crbs.cws.cluster.submission;

import edu.ucsd.crbs.cws.cluster.JobBinaries;
import edu.ucsd.crbs.cws.cluster.JobEmailNotificationData;
import edu.ucsd.crbs.cws.io.KeplerHtmlStringEscaper;
import edu.ucsd.crbs.cws.io.ResourceToExecutableScriptWriter;
import edu.ucsd.crbs.cws.io.ResourceToExecutableScriptWriterImpl;
import edu.ucsd.crbs.cws.io.StringEscaper;
import edu.ucsd.crbs.cws.io.StringReplacer;
import edu.ucsd.crbs.cws.rest.Constants;
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.Parameter;
import java.io.File;
import java.util.LinkedHashMap;

/**
 * Creates a {@link JOB_CMD_SH} script that can run the Workflow Job
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class JobCmdScriptCreatorImpl implements JobCmdScriptCreator, StringReplacer {

    public static final String HYPHEN = "-";
    public static final String SPACE = " ";

    public static final String UNKNOWN = "Unknown";
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
    
    public static final String WORKFLOW_NAME_TOKEN = "@@WORKFLOW_NAME@@";

    
    public static final String REGISTER_OUTPUT_TO_WORKSPACE_TOKEN = "@@REGISTER_OUTPUT_TO_WORKSPACE@@";
    
    public static final String UPDATE_WORKSPACE_PATH_TOKEN = "@@UPDATE_WORKSPACE_PATH@@";
    
    public static final String RETRY_COUNT_TOKEN = "@@RETRY_COUNT@@";
    
    public static final String SLEEP_TOKEN = "@@SLEEP@@";
    
    public static final String UPDATE_WORKSPACE_RETRY_SLEEP_TIME_TOKEN = "@@UPDATE_WORKSPACE_RETRY_SLEEP_TIME@@";
    
    public static final String ERROR_EMAIL_TOKEN = "@@ERROR_EMAIL@@";
    
    public static final String ECHO_TOKEN = "@@ECHO@@";
    
    public static final String RM_TOKEN = "@@RM@@";
    
    public static final String KILL_TOKEN = "@@KILL@@";
    
    public static final String MAIL_TOKEN = "@@MAIL@@";
    
    public static final String POST_EMAIL_SLEEP_TOKEN = "@@POST_EMAIL_SLEEP@@";

    public static final String REGISTER_WSF_OUTPUT = "registerworkspacefile.out";

    public static final String UPDATE_WSF_OUTPUT = "updateworkspacefile.out";
    
    /**
     * Base directory under which the Workflow kar files reside
     */
    private final String _workflowsDir;
    
    /**
     * Arguments to pass to Kepler script
     */
    private String _jobArgs;
    
    /**
     * The working directory for the Workflow Task
     */
    private String _workingDir;
    
    private String _updateOutputWorkspaceFilePath;
    
    private String _jobId;
    
    private String _user;
    
    private String _jobName;
    
    private String _userEmail;
    
    private JobEmailNotificationData _emailNotifyData;
    
    private ResourceToExecutableScriptWriter _resToFile;

    private StringEscaper _stringEscaper;

    private String _workflowName;
    
    
    private JobBinaries _jobBinaries;
    
    
    /**
     * Replaces occurrences of @@JOB_ARGS@@ @@KEPLER_SH@@ with correct values
     *
     * @param line
     * @return
     */
    @Override
    public String replace(String line) {
        return line.replace(KEPLER_SH_TOKEN, _jobBinaries.getKeplerScript()).
                replace(JOB_ARGS_TOKEN, _jobArgs).
                replace(JAVA_TOKEN,_jobBinaries.getJavaCommand()).
                replace(UPDATE_WORKSPACE_PATH_TOKEN,_updateOutputWorkspaceFilePath).
                replace(JOB_NAME_TOKEN,_jobName).
                replace(USER_TOKEN,_user).
                replace(NOTIFY_EMAIL_TOKEN,_userEmail).
                replace(PROJECT_TOKEN,_emailNotifyData.getProject()).
                replace(PORTAL_NAME_TOKEN,_emailNotifyData.getPortalName()).
                replace(PORTAL_URL_TOKEN,_emailNotifyData.getPortalURL()).
                replace(HELP_EMAIL_TOKEN,_emailNotifyData.getHelpEmail()).
                replace(BCC_EMAIL_TOKEN,_emailNotifyData.getBccEmail()).
                replace(WORKFLOW_NAME_TOKEN,_workflowName).
                replace(JOB_ID_TOKEN,_jobId).
                replace(RETRY_COUNT_TOKEN,Integer.toString(_jobBinaries.getRetryCount())).
                replace(SLEEP_TOKEN,_jobBinaries.getSleepCommand()).
                replace(UPDATE_WORKSPACE_RETRY_SLEEP_TIME_TOKEN,Integer.toString(_jobBinaries.getWorkspaceUpdateRetrySleepTimeInSeconds())).
                replace(ERROR_EMAIL_TOKEN,_emailNotifyData.getErrorEmail()).
                replace(ECHO_TOKEN,_jobBinaries.getEchoCommand()).
                replace(RM_TOKEN,_jobBinaries.getRmCommand()).
                replace(KILL_TOKEN,_jobBinaries.getKillCommand()).
                replace(MAIL_TOKEN,_jobBinaries.getMailCommand()).
                replace(POST_EMAIL_SLEEP_TOKEN,Integer.toString(_jobBinaries.getPostEmailSleepTimeInSeconds()));
                
    }

    
    public JobCmdScriptCreatorImpl(final String workflowsDir,JobBinaries jobBinaries,
            JobEmailNotificationData emailNotifyData){
        _workflowsDir = workflowsDir;
        _jobBinaries = jobBinaries;
        _emailNotifyData = emailNotifyData;
        _resToFile = new ResourceToExecutableScriptWriterImpl();
        _stringEscaper = new KeplerHtmlStringEscaper();
    }
    
    
    /**
     * 
     * @param workflowsDir
     * @param keplerScript
     * @param registerUpdateJar
     * @param emailNotifyData 
     * @deprecated Use other constructor
     */
    public JobCmdScriptCreatorImpl(final String workflowsDir, final String keplerScript,
            final String registerUpdateJar,
            JobEmailNotificationData emailNotifyData) {
       this(workflowsDir,createJobBinaries(keplerScript,registerUpdateJar),emailNotifyData);
    }
    
    /**
     * Creates {@link JobBinaries} object
     * @param keplerScript
     * @param registerUpdateJar
     * @return 
     */
    private static JobBinaries createJobBinaries(final String keplerScript,
            final String registerUpdateJar){
        JobBinaries jobBinaries = new JobBinaries();
        jobBinaries.setRetryCount(3);
        jobBinaries.setPostEmailSleepTimeInSeconds(10);
        jobBinaries.setWorkspaceUpdateRetrySleepTimeInSeconds(60);
        jobBinaries.setKeplerScript(keplerScript);
        jobBinaries.setRegisterUpdateJar(registerUpdateJar);
        return jobBinaries;
    }
    
    public void setJavaBinaryPath(final String path){
        _jobBinaries.setJavaCommand(path);
    }
    
    public void setResourceToExecutableScriptWriter(ResourceToExecutableScriptWriter scriptWriter){
        _resToFile = scriptWriter;
    }
    
    public void setStringEscaper(StringEscaper stringEscaper){
        _stringEscaper = stringEscaper;
    }

    /**
     * Creates {@link JOB_CMD_SH} script in jobDirectory.  This script can later
     * be submitted to SGE to run the Workflow Job
     * @param jobDirectory Should be set to base directory of Workflow JOb and 
     * there should exist a {@link Constants.OUTPUS_DIR_NAME} within this directory
     * @param j Job to run
     * @param workspaceFileId
     * @return Full path to {@link JOB_CMD_SH} script that can be used to run the Job
     * @throws Exception If there is an error, duh
     */
    @Override
    public String create(final String jobDirectory, Job j,Long workspaceFileId) throws Exception {
        
        if (jobDirectory == null){
            throw new Exception("Job Directory cannot be null");
        }
        
        if (j == null){
            throw new Exception("Job cannot be null");
        }
        
        _workingDir = jobDirectory + File.separator + Constants.OUTPUTS_DIR_NAME;

        if (j.getWorkflow() == null){
            throw new Exception("Workflow cannot be null");
        }
        
        if (j.getWorkflow().getId() == null){
            throw new Exception("Workflow id cannot be null");
        }
        
        _jobArgs = generateJobArguments(j);

        
        if (j.getOwner() != null){
            _user = j.getOwner().replace("\""," ");
        }
        else {
            _user = UNKNOWN;
        }
        
        if (j.getId() != null){
            _jobId = j.getId().toString();
        }
        else {
            _jobId = UNKNOWN;
        }
        
        if (j.getName() != null){
            _jobName = j.getName().replace("\""," ");
        }
        else {
            _jobName = UNKNOWN;
        }
        
        if (j.getWorkflow().getName() != null){
            _workflowName = j.getWorkflow().getName();
        }
        else {
            _workflowName = UNKNOWN;
        }
        
        setUserEmail(j);
        
        _updateOutputWorkspaceFilePath = " -jar "+
                _jobBinaries.getRegisterUpdateJar()+
                " --updatepath \""+workspaceFileId.toString()+"\""+
                " --path \""+_workingDir+"\""+
                " --size `du "+_workingDir+
                " -bs | sed \"s/\\W*\\/.*//\"` $workspaceStatusFlag >> "+
                jobDirectory+File.separator+UPDATE_WSF_OUTPUT+" 2>&1";        

        String jobCmd = _workingDir + File.separator + JOB_CMD_SH;

        _resToFile.writeResourceToScript(JOB_CMD_SH_TEMPLATE,
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
        
        if (j.getParameters() != null) {
            //append all command line arguments
            for (Parameter param : j.getParameters()) {

                //need to deal with special parameters!!!
                if (param.getName().equals(Constants.CWS_OUTPUTDIR)) {
                    value = _workingDir;
                } else if (param.getName().equals(Constants.CWS_JOBNAME)) {
                    value = j.getName();
                } else if (param.getName().equals(Constants.CWS_USER)) {
                    value = j.getOwner();
                } else if (param.getName().equals(Constants.CWS_JOBID)) {
                    value = j.getId().toString();
                } else {
                    value = param.getValue();
                }

                sb.append(SPACE).append(HYPHEN).append(param.getName()).append(SPACE);
                sb.append("\"").append(_stringEscaper.escapeString(value)).append("\"");
            }
        }

        //add path to workflow
        sb.append(SPACE).append(_workflowsDir).append(File.separator);
        sb.append(j.getWorkflow().getId()).append(File.separator);
        sb.append(j.getWorkflow().getId()).append(WORKFLOW_SUFFIX);

        return sb.toString();
    }
}
