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

import edu.ucsd.crbs.cws.cluster.submission.JobDirectoryCreatorImpl;
import edu.ucsd.crbs.cws.cluster.submission.JobDirectoryCreator;
import edu.ucsd.crbs.cws.cluster.submission.JobCmdScriptSubmitterImpl;
import edu.ucsd.crbs.cws.cluster.submission.JobCmdScriptSubmitter;
import edu.ucsd.crbs.cws.cluster.submission.JobCmdScriptCreator;
import edu.ucsd.crbs.cws.cluster.submission.JobCmdScriptCreatorImpl;
import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.dao.JobDAO;
import edu.ucsd.crbs.cws.dao.WorkspaceFileDAO;
import edu.ucsd.crbs.cws.io.WorkflowFailedWriter;
import edu.ucsd.crbs.cws.io.WorkflowFailedWriterImpl;
import edu.ucsd.crbs.cws.rest.Constants;
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Submits {@link Job} objects to local cluster via SGE
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class JobSubmissionManager {

    
    public static final String WORKSPACE_ERROR_MSG = "Job unable to start due to failure of WorkspaceFile used as input";
    private static final Logger _log
            = Logger.getLogger(JobSubmissionManager.class.getName());

    JobDirectoryCreator _directoryCreator;
    JobCmdScriptCreator _cmdScriptCreator;
    JobCmdScriptSubmitter _cmdScriptSubmitter;
    SyncWorkflowFileToFileSystem _workflowSync;
    WorkspaceFilePathSetter _workspacePathSetter;
    private WorkspaceFileDAO _workspaceFileDAO;
    private JobDAO _jobDAO;
    private WorkflowFailedWriter _failedWorkflowWriter;

    /**
     * Constructor
     *
     * @param jobDAO
     * @param workspaceFileDAO
     * @param workspaceFilePathSetter
     * @param jobPath
     * @param workflowsDir Directory where workflows are stored
     * @param keplerScript Full path to Kepler program
     * @param panfishCast
     * @param queue
     * @param user
     * @param url
     * @param registerUpdateJar
     * @param emailNotifyData
     * @deprecated use other constructor
     */
    public JobSubmissionManager(JobDAO jobDAO,
            WorkspaceFileDAO workspaceFileDAO,
            WorkspaceFilePathSetter workspaceFilePathSetter,
            JobPath jobPath,
            final String workflowsDir,
            final String keplerScript,
            final String panfishCast,
            final String queue,
            final User user,
            final String url,
            final String registerUpdateJar,
            JobEmailNotificationData emailNotifyData) {

        _directoryCreator = new JobDirectoryCreatorImpl(jobPath);
        _cmdScriptCreator = new JobCmdScriptCreatorImpl(workflowsDir, 
                keplerScript, registerUpdateJar + " --url " + url + " --login "
                + user.getLogin() + " --token " + user.getToken(),
        emailNotifyData);
        _cmdScriptSubmitter = new JobCmdScriptSubmitterImpl(panfishCast, queue);
        _workflowSync = new SyncWorkflowFileToFileSystemImpl(workflowsDir, url, user.getLogin(), user.getToken());
        _workspacePathSetter = workspaceFilePathSetter;
        _jobDAO = jobDAO;
        _workspaceFileDAO = workspaceFileDAO;
         _failedWorkflowWriter = new WorkflowFailedWriterImpl();
        
    }
    
    public JobSubmissionManager(JobDAO jobDAO,
            WorkspaceFileDAO workspaceFileDAO,
            WorkspaceFilePathSetter workspaceFilePathSetter,
            JobDirectoryCreator directoryCreator,
            JobCmdScriptCreator cmdScriptCreator,
            JobCmdScriptSubmitter cmdScriptSubmitter,
            SyncWorkflowFileToFileSystem workflowSync,
            WorkflowFailedWriter failedWorkflowWriter){
        _directoryCreator = directoryCreator;
        _cmdScriptCreator = cmdScriptCreator;
        _cmdScriptSubmitter = cmdScriptSubmitter;
        _workflowSync = workflowSync;
        _workspacePathSetter = workspaceFilePathSetter;
        _jobDAO = jobDAO;
        _workspaceFileDAO = workspaceFileDAO;
        _failedWorkflowWriter = failedWorkflowWriter;
    }
            

    /**
     * Submits job to local SGE cluster. This method creates the necessary files
     * and directories. This method will then update the clusterJobId value in
     * the Job and set the status to correct state.
     * 
     * @throws Exception If there was a problem creating or submitting the Job
     */
    public void submitJobs() throws Exception {
        // @TODO Need to move the logic inside the for loop to another object
        // cause this is doing way too much stuff
        
        _log.log(Level.INFO, "Looking for new jobs to submit...");

        List<Job> jobs = _jobDAO.getJobs(null, null, true, false, false,null);
        WorkspaceFilePathSetterStatus status = null;
        String error = null;
        String detailedError = null;
        if (jobs != null) {
            _log.log(Level.INFO, "Found {0} job(s) need to be submitted", 
                    jobs.size());
            for (Job j : jobs) {

                try {
                    
                    //check if workspace files are syncd.  If not update status
                    // to workspace sync and move on to the next Job
                    status = _workspacePathSetter.setPaths(j);
                    if (status.isSuccessful() == false) {
                        boolean submittedToScheduler = false;
                        if (!j.getStatus().equals(status.getSuggestedJobStatus())) {
                            _log.log(Level.INFO,"\tUpdating status for job {0} to {1}",
                                     new Object[]{generateJobLogMessage(j),
                                         status.getSuggestedJobStatus()});
                            
                            
                            error = null;
                            detailedError = null;
                            //if new status is error we need to set isFailed to true for the
                            //workspace file associated with this job
                            if (status.getSuggestedJobStatus().equals(Job.ERROR_STATUS)){
                                Long wsfId = this.getJobsWorkspaceId(j);
                                _workspaceFileDAO.updatePathSizeAndFailStatus(wsfId, null, null, Boolean.TRUE);
                                error = WORKSPACE_ERROR_MSG;
                                detailedError = status.getReason();
                                
                                //we also need to fail this job since we can't run without
                                // the workspace file cs-289
                                submittedToScheduler = true;
                            }
                            
                            _jobDAO.update(j.getId(), status.getSuggestedJobStatus(), null, null, null,
                                    null, null, null, submittedToScheduler,
                                    null,null,error,detailedError);
                        }
                        if (submittedToScheduler == true){
                            _log.log(Level.INFO,"\tAbandoning submission of Job {0} : {1}",
                                    new Object[]{generateJobLogMessage(j),
                                        status.getReason()});
                        }
                        else {
                            _log.log(Level.INFO,"\tTemporarily skipping submission of Job {0} : {1}",
                                    new Object[]{generateJobLogMessage(j),
                                        j.getName(),status.getReason()});
                        }
                        continue;
                    }

                    _log.log(Level.INFO, "\tSubmitting Job: {0}",
                            new Object[]{generateJobLogMessage(j)});

                    submitJob(j);

                    _jobDAO.update(j.getId(), Job.PENDING_STATUS, null, null, null,
                            j.getSubmitDate().getTime(), null, null, true,
                            j.getSchedulerJobId(),null,null,null);
                } catch (Exception ex) {
                    _log.log(Level.SEVERE,
                            "\tProblems submitting job: {0} -- {1}.  Skipping...",
                            new Object[]{generateJobLogMessage(j),
                                j.getName(), ex.getMessage()});
                }
            }
        } else {
            _log.log(Level.INFO, "No jobs need to be submitted");
        }
    }
    
    /**
     * Takes a {@link Job} <b>j</b> and generates a String to identify the job.
     * @param j
     * @return ( job id - job name, workflow name) or (Null job) if job is null
     */
    private String generateJobLogMessage(Job j){
        if (j == null){
            return "(Null Job)";
        }
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("(").
           append(j.getId().toString());
        if (j.getName() != null){
            sb.append(" - ").append(j.getName());
        }
        if (j.getWorkflow() != null && j.getWorkflow().getName() != null){
            sb.append(" ").append(j.getWorkflow().getName());
        }
        return sb.append(")").toString();
    }
    
    /**
     * Verifies {@link Workflow} is on filesystem and creates the {@link Job} directory
     * and associated commands necessary to run the {@link Job}.  Once created the
     * {@link Job} is submitted
     * @param j
     * @throws Exception 
     */
    private void submitJob(Job j) throws Exception {
        _workflowSync.sync(j.getWorkflow());
        String jobDir = _directoryCreator.create(j);
        
        _failedWorkflowWriter.setPath(jobDir+File.separator+Constants.OUTPUTS_DIR_NAME);
        _failedWorkflowWriter.write(Constants.JOB_DID_NOT_START_SIMPLE_ERROR,
                Constants.JOB_DID_NOT_START_DETAILED_ERROR);
        
        String cmdScript = _cmdScriptCreator.create(jobDir, j,
                getJobsWorkspaceId(j));
        
        String submitOut = _cmdScriptSubmitter.submit(cmdScript, j);
        _log.log(Level.INFO,"\tOutput from submit command: {0}",submitOut);
        
    }
    
    /**
     * Gets {@link WorkspaceFile#getId()} whose source is <b>j</b> passed in.
     * @param j
     * @return
     * @throws Exception 
     */
    private Long getJobsWorkspaceId(Job j) throws Exception {
        List<WorkspaceFile> wsfList = _workspaceFileDAO.getWorkspaceFilesBySourceJobId(j.getId());
        if (wsfList == null){
            throw new Exception("No WorkspaceFile for job "+
                    generateJobLogMessage(j));
        }
        
        if (wsfList.size() != 1){
            throw new Exception("\tExpected 1 WorkspaceFile for job"+
                    generateJobLogMessage(j)+
                    ", but got: "+wsfList.size());
        }
        return wsfList.get(0).getId();
    }
    
}
