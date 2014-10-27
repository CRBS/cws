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

import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.dao.JobDAO;
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Submits {@link Job} objects to local cluster via SGE
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class JobSubmitter {

    private static final Logger _log
            = Logger.getLogger(JobSubmitter.class.getName());

    JobDirectoryCreator _directoryCreator;
    JobCmdScriptCreator _cmdScriptCreator;
    JobCmdScriptSubmitter _cmdScriptSubmitter;
    SyncWorkflowFileToFileSystem _workflowSync;
    WorkspaceFilePathSetter _workspacePathSetter;
    OutputWorkspaceFileUtil _outputWorkspaceFileCreator;
    private JobDAO _jobDAO;

    /**
     * Constructor
     *
     * @param jobDAO
     * @param workspaceFilePathSetter
     * @param outputWorkspaceFileCreator
     * @param jobPath
     * @param workflowsDir Directory where workflows are stored
     * @param keplerScript Full path to Kepler program
     * @param panfishCast
     * @param queue
     * @param user
     * @param url
     * @param registerUpdateJar
     * @param emailNotifyData
     */
    public JobSubmitter(JobDAO jobDAO,
            WorkspaceFilePathSetter workspaceFilePathSetter,
            OutputWorkspaceFileUtil outputWorkspaceFileCreator,
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
        _outputWorkspaceFileCreator = outputWorkspaceFileCreator;

        _jobDAO = jobDAO;
    }

    /**
     * Submits job to local SGE cluster. This method creates the necessary files
     * and directories. This method will then update the clusterJobId value in
     * the Job and set the status to correct state.
     *
     * @throws Exception If there was a problem creating or submitting the Job
     */
    public void submitJobs() throws Exception {
        _log.log(Level.INFO, "Looking for new jobs to submit...");

        List<Job> jobs = _jobDAO.getJobs(null, null, true, false, false);

        if (jobs != null) {
            _log.log(Level.INFO, "Found {0} job(s) need to be submitted", jobs.size());
            for (Job j : jobs) {

                try {
                    
                    //check if workspace files are syncd.  If not update status
                    // to workspace sync and move on to the next Job
                    if (_workspacePathSetter.setPaths(j) == false) {
                        _log.log(Level.INFO,"Workspace files are NOT in place for job {0} skipping...",
                                 j.getId());

                        if (!j.getStatus().equals(Job.WORKSPACE_SYNC_STATUS)) {
                            _log.log(Level.INFO,"Updating status for job {0} to sync",
                                     j.getId());
    
                            _jobDAO.update(j.getId(), Job.WORKSPACE_SYNC_STATUS, null, null, null,
                                    null, null, null, false,
                                    null);
                        }
                        continue;
                    }

                    _log.log(Level.INFO, "\tSubmitting Job: ({0}) {1}",
                            new Object[]{j.getId(), j.getName()});

                    submitJob(j);

                    _jobDAO.update(j.getId(), Job.PENDING_STATUS, null, null, null,
                            j.getSubmitDate().getTime(), null, null, true,
                            j.getSchedulerJobId());
                } catch (Exception ex) {
                    _log.log(Level.SEVERE,
                            "Problems submitting job: {0} -- {1}.  Skipping...",
                            new Object[]{j.getId(), ex.getMessage()});
                }
            }
        } else {
            _log.log(Level.INFO, "No jobs need to be submitted");
        }
    }

    private void submitJob(Job j) throws Exception {
        _workflowSync.sync(j.getWorkflow());
        String jobDir = _directoryCreator.create(j);
        String cmdScript = _cmdScriptCreator.create(jobDir, j);
        
        String submitOut = _cmdScriptSubmitter.submit(cmdScript, j);
        _log.log(Level.INFO,"Output from submit command: {0}",submitOut);
        
    }
}
