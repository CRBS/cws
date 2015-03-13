/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2015 The Regents of the University of California All Rights Reserved
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

import edu.ucsd.crbs.cws.cluster.submission.JobCmdScriptCreator;
import edu.ucsd.crbs.cws.cluster.submission.JobCmdScriptSubmitter;
import edu.ucsd.crbs.cws.cluster.submission.JobDirectoryCreator;
import edu.ucsd.crbs.cws.dao.JobDAO;
import edu.ucsd.crbs.cws.dao.WorkspaceFileDAO;
import edu.ucsd.crbs.cws.io.WorkflowFailedWriter;
import edu.ucsd.crbs.cws.rest.Constants;
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;



/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestJobSubmissionManager {

    public TestJobSubmissionManager() {
    }

    @BeforeClass
    public static void setUpClass() {
       Logger.getLogger(JobSubmissionManager.class.getName()).setLevel(Level.OFF);
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test 
    public void testSubmitJobsWithNoJobsToSubmit() throws Exception {
        
        
        WorkspaceFileDAO workspaceFileDAO = mock(WorkspaceFileDAO.class);
        JobDAO jobDAO = mock(JobDAO.class);
        when(jobDAO.getJobs(null, null, true, false, false, null)).thenReturn(null);
        
        WorkspaceFilePathSetter workspaceFilePathSetter = mock(WorkspaceFilePathSetter.class);
        JobSubmissionManager js = new JobSubmissionManager(jobDAO,workspaceFileDAO,workspaceFilePathSetter,
        null,
        null,
        null,
        null,
        null);
        
        js.submitJobs();
        verify(jobDAO).getJobs(null, null, true, false, false, null);
    }
    
    @Test
    public void testSubmitJobsWithOneJobWhoseWorkspaceFilesAreNotDone() throws Exception {
        WorkspaceFileDAO workspaceFileDAO = mock(WorkspaceFileDAO.class);
        JobDAO jobDAO = mock(JobDAO.class);
        ArrayList<Job> jobs = new ArrayList<Job>();
        Job myJob = new Job();
        myJob.setId(1L);
        myJob.setHasJobBeenSubmittedToScheduler(false);
        myJob.setStatus(Job.IN_QUEUE_STATUS);
        myJob.setName("notdonejob");
        
        jobs.add(myJob);
        
        when(jobDAO.getJobs(null, null, true, false, false, null)).thenReturn(jobs);
        when(jobDAO.update(myJob.getId(), Job.WORKSPACE_SYNC_STATUS, null,null,null, null,null,null,false,null,null, null,null)).thenReturn(myJob);
        
        WorkspaceFilePathSetter workspaceFilePathSetter = mock(WorkspaceFilePathSetter.class);
        WorkspaceFilePathSetterStatus pathSetterStatus = new WorkspaceFilePathSetterStatus();
        
        pathSetterStatus.setReason("some reason");
        pathSetterStatus.setSuccessful(false);
        pathSetterStatus.setSuggestedJobStatus(Job.WORKSPACE_SYNC_STATUS);
        
        when(workspaceFilePathSetter.setPaths(myJob)).thenReturn(pathSetterStatus);
        JobSubmissionManager js = new JobSubmissionManager(jobDAO,
                workspaceFileDAO,
                workspaceFilePathSetter,
                null,
                null,
                null,
                null,
                null);
        
        js.submitJobs();
        verify(jobDAO).getJobs(null, null, true, false, false, null);
        verify(jobDAO).update(myJob.getId(), Job.WORKSPACE_SYNC_STATUS, null,null,null, null,null,null,false,null,null, null,null);
        verify(workspaceFilePathSetter).setPaths(myJob);

        
        
    }
    
    @Test
    public void testSubmitJobsWithOneJobWhoseWorkspaceFilesHaveFailed() throws Exception {
        WorkspaceFileDAO workspaceFileDAO = mock(WorkspaceFileDAO.class);
        
        ArrayList<WorkspaceFile> workspaceFileList = new ArrayList<WorkspaceFile>();
        
        WorkspaceFile wsf = new WorkspaceFile();
        wsf.setId(2L);
        workspaceFileList.add(wsf);

        when(workspaceFileDAO.getWorkspaceFilesBySourceJobId(1L)).thenReturn(workspaceFileList);
        when(workspaceFileDAO.updatePathSizeAndFailStatus(wsf.getId(), null, null, Boolean.TRUE)).thenReturn(wsf);
        
        JobDAO jobDAO = mock(JobDAO.class);
        ArrayList<Job> jobs = new ArrayList<Job>();
        Job myJob = new Job();
        myJob.setId(1L);
        myJob.setName("failedworkspacefilejob");
        myJob.setHasJobBeenSubmittedToScheduler(false);
        myJob.setStatus(Job.IN_QUEUE_STATUS);
        
        jobs.add(myJob);
        
        when(jobDAO.getJobs(null, null, true, false, false, null)).thenReturn(jobs);
        when(jobDAO.update(myJob.getId(), Job.ERROR_STATUS, null,null,null, null,null,null,true,null,null,JobSubmissionManager.WORKSPACE_ERROR_MSG,"some reason")).thenReturn(myJob);
        
        WorkspaceFilePathSetter workspaceFilePathSetter = mock(WorkspaceFilePathSetter.class);
        WorkspaceFilePathSetterStatus pathSetterStatus = new WorkspaceFilePathSetterStatus();
        pathSetterStatus.setReason("some reason");
        pathSetterStatus.setSuccessful(false);
        pathSetterStatus.setSuggestedJobStatus(Job.ERROR_STATUS);
        
        when(workspaceFilePathSetter.setPaths(myJob)).thenReturn(pathSetterStatus);
        JobSubmissionManager js = new JobSubmissionManager(jobDAO,workspaceFileDAO,workspaceFilePathSetter,
        null,
        null,
        null,
        null,null);
        
        js.submitJobs();
        verify(jobDAO).getJobs(null, null, true, false, false, null);
        verify(jobDAO).update(myJob.getId(), Job.ERROR_STATUS, null,null,null, null,null,null,true,null,null, JobSubmissionManager.WORKSPACE_ERROR_MSG,"some reason");
        verify(workspaceFilePathSetter).setPaths(myJob);
        verify(workspaceFileDAO).getWorkspaceFilesBySourceJobId(1L);
        verify(workspaceFileDAO).updatePathSizeAndFailStatus(wsf.getId(), null, null, Boolean.TRUE);
    }

    @Test
    public void testSubmitJobsSingleSuccessfulJob() throws Exception {
        
        WorkspaceFileDAO workspaceFileDAO = mock(WorkspaceFileDAO.class);
        JobDAO jobDAO = mock(JobDAO.class);
        ArrayList<Job> jobs = new ArrayList<Job>();
        Job myJob = new Job();
        myJob.setId(1L);
        myJob.setHasJobBeenSubmittedToScheduler(false);
        myJob.setStatus(Job.IN_QUEUE_STATUS);
        myJob.setName("successfuljob");
        myJob.setSubmitDate(new Date());
        myJob.setSchedulerJobId("10");
        Workflow w = new Workflow();
        Job.REFS_ENABLED = false;
        myJob.setWorkflow(w);
        jobs.add(myJob);
        
         WorkspaceFile wsf = new WorkspaceFile();
        wsf.setId(2L);
        ArrayList<WorkspaceFile> workspaceFileList = new ArrayList<WorkspaceFile>();
        workspaceFileList.add(wsf);

        when(workspaceFileDAO.getWorkspaceFilesBySourceJobId(1L)).thenReturn(workspaceFileList);
        
        when(jobDAO.getJobs(null, null, true, false, false,
                null)).thenReturn(jobs);
        
        when(jobDAO.update(myJob.getId(), Job.PENDING_STATUS, null,null,null,
                myJob.getSubmitDate().getTime(),null,null,true,
                myJob.getSchedulerJobId(),null, null,null)).thenReturn(myJob);
        
        WorkspaceFilePathSetter workspaceFilePathSetter = mock(WorkspaceFilePathSetter.class);
        WorkspaceFilePathSetterStatus pathSetterStatus = new WorkspaceFilePathSetterStatus();
        
        pathSetterStatus.setSuccessful(true);
        pathSetterStatus.setSuggestedJobStatus(Job.IN_QUEUE_STATUS);
        
        JobDirectoryCreator directoryCreator = mock(JobDirectoryCreator.class);
        JobCmdScriptCreator cmdScriptCreator = mock(JobCmdScriptCreator.class);
        JobCmdScriptSubmitter cmdScriptSubmitter = mock(JobCmdScriptSubmitter.class);
        SyncWorkflowFileToFileSystem workflowSync = mock(SyncWorkflowFileToFileSystem.class);
        
        
        when(directoryCreator.create(myJob)).thenReturn("hi");
        when(cmdScriptCreator.create("hi", myJob, 2L)).thenReturn("cmd");
        when(cmdScriptSubmitter.submit("cmd", myJob)).thenReturn("submitted");
        
        WorkflowFailedWriter wfwi = mock(WorkflowFailedWriter.class);
        
        
        
        
        when(workspaceFilePathSetter.setPaths(myJob)).thenReturn(pathSetterStatus);
        JobSubmissionManager js = new JobSubmissionManager(jobDAO,
                workspaceFileDAO,
                workspaceFilePathSetter,
                directoryCreator,
                cmdScriptCreator,
                cmdScriptSubmitter,
                workflowSync,wfwi);
        
        js.submitJobs();
        verify(jobDAO).getJobs(null, null, true, false, false, null);
        verify(jobDAO).update(myJob.getId(), Job.PENDING_STATUS, null,null,null,
                myJob.getSubmitDate().getTime(),null,null,true,
                myJob.getSchedulerJobId(),null, null,null);
        verify(workspaceFilePathSetter).setPaths(myJob);
        verify(workflowSync).sync(myJob.getWorkflow());
        verify(directoryCreator).create(myJob);
        verify(cmdScriptCreator).create("hi", myJob, 2L);
        verify(cmdScriptSubmitter).submit("cmd", myJob);
        verify(wfwi).setPath("hi"+File.separator+Constants.OUTPUTS_DIR_NAME);
        verify(wfwi).write(Constants.JOB_DID_NOT_START_SIMPLE_ERROR, 
                Constants.JOB_DID_NOT_START_DETAILED_ERROR);
    }
    
}