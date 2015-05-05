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
package edu.ucsd.crbs.cws.dao.objectify;

import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import edu.ucsd.crbs.cws.dao.WorkspaceFileDAO;
import static edu.ucsd.crbs.cws.dao.objectify.OfyService.ofy;
import edu.ucsd.crbs.cws.workflow.InputWorkspaceFileLink;
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.Parameter;
import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.WorkflowParameter;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import edu.ucsd.crbs.cws.workflow.report.DeleteReport;
import edu.ucsd.crbs.cws.workflow.report.DeleteReportImpl;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestJobObjectifyDAOImpl {

    private final LocalServiceTestHelper _helper
            = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(),
                    new LocalBlobstoreServiceTestConfig());

    public TestJobObjectifyDAOImpl() {
    }

    @BeforeClass
    public static void setUpClass() {
        Logger.getLogger(JobObjectifyDAOImpl.class.getName()).setLevel(Level.OFF);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        _helper.setUp();
        ofy().clear();
    }

    @After
    public void tearDown() {
        _helper.tearDown();
    }

    @Test
    public void testDeleteWhereJobNotFound() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);

        DeleteReport dwr = jobDAO.delete(1L, null);
        assertTrue(dwr != null);
        assertTrue(dwr.isSuccessful() == false);
        assertTrue(dwr.getReason().equals("Job not found"));
    }
    
    //test delete where multiple workspace files found as output for job
    @Test
    public void testDeleteWhereMultipleWorkspaceFilesFoundAsOutputForJob() throws Exception {
        InputWorkspaceFileLinkObjectifyDAOImpl inputDAO = new InputWorkspaceFileLinkObjectifyDAOImpl();
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(inputDAO);
        WorkspaceFileObjectifyDAOImpl workspaceDAO = new WorkspaceFileObjectifyDAOImpl(jobDAO, inputDAO);
        jobDAO.setWorkspaceFileDAO(workspaceDAO);
        Job j = new Job();
        j = jobDAO.insert(j, true);
        WorkspaceFile wsf = new WorkspaceFile();
        wsf.setSourceJobId(j.getId());
        wsf = workspaceDAO.insert(wsf, false);
        
        WorkspaceFile wsf2 = new WorkspaceFile();
        wsf2.setSourceJobId(j.getId());
        wsf2 = workspaceDAO.insert(wsf2, false);
        DeleteReport dr = jobDAO.delete(j.getId(),null);
        assertTrue(dr != null);
        assertTrue(dr.isSuccessful() == false);
        assertTrue(dr.getReason().equals("Found 2 WorkspaceFiles as output for Job, but expected 1"));
        
    }
    
    
    //test delete where we are unable to delete workspacefile
    @Test
    public void testDeleteWhereUnableToDeleteWorkspaceFile() throws Exception {
        InputWorkspaceFileLinkObjectifyDAOImpl inputDAO = new InputWorkspaceFileLinkObjectifyDAOImpl();
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(inputDAO);
        WorkspaceFileDAO workspaceDAO = mock(WorkspaceFileDAO.class);
        jobDAO.setWorkspaceFileDAO(workspaceDAO);
        
        Job j = new Job();
        j = jobDAO.insert(j, true);
        ArrayList<WorkspaceFile> wsfList = new ArrayList<WorkspaceFile>();
        WorkspaceFile wsf = new WorkspaceFile();
        wsf.setId(1L);
        wsf.setSourceJobId(j.getId());
        wsfList.add(wsf);
        when(workspaceDAO.getWorkspaceFilesBySourceJobId(j.getId())).thenReturn(wsfList);
    
        DeleteReportImpl wsfReport = new DeleteReportImpl();
        wsfReport.setSuccessful(false);
        wsfReport.setReason("reason");
        wsfReport.setId(wsf.getId());
        when(workspaceDAO.delete(wsf.getId(),null, true)).thenReturn(wsfReport);
        
        DeleteReport dr = jobDAO.delete(j.getId(), null);
        assertTrue(dr != null);
        assertTrue(dr.isSuccessful() == false);
        assertTrue(dr.getReason(),
                dr.getReason().equals("Unable to delete WorkspaceFile ("+wsf.getId()+") : reason"));
        
    }
    
    //test logical delete
     @Test
    public void testDeleteLogical() throws Exception {
        InputWorkspaceFileLinkObjectifyDAOImpl inputDAO = new InputWorkspaceFileLinkObjectifyDAOImpl();
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(inputDAO);
        WorkspaceFileObjectifyDAOImpl workspaceDAO = new WorkspaceFileObjectifyDAOImpl(jobDAO, inputDAO);
        jobDAO.setWorkspaceFileDAO(workspaceDAO);
        Job j = new Job();
        j = jobDAO.insert(j, true);
        WorkspaceFile wsf = new WorkspaceFile();
        wsf.setSourceJobId(j.getId());
        wsf = workspaceDAO.insert(wsf, false);
        
        DeleteReport dr = jobDAO.delete(j.getId(),null);
        assertTrue(dr != null);
        assertTrue(dr.isSuccessful() == true);
        assertTrue(dr.getReason() == null);
        j = jobDAO.getJobById(j.getId().toString());
        assertTrue(j.isDeleted() == true);
        wsf = workspaceDAO.getWorkspaceFileById(wsf.getId().toString(), null);
        assertTrue(wsf.getDeleted() == true);
    }

         @Test
    public void testDeleteLogicalWithFalseFlag() throws Exception {
        InputWorkspaceFileLinkObjectifyDAOImpl inputDAO = new InputWorkspaceFileLinkObjectifyDAOImpl();
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(inputDAO);
        WorkspaceFileObjectifyDAOImpl workspaceDAO = new WorkspaceFileObjectifyDAOImpl(jobDAO, inputDAO);
        jobDAO.setWorkspaceFileDAO(workspaceDAO);
        Job j = new Job();
        j = jobDAO.insert(j, true);
        WorkspaceFile wsf = new WorkspaceFile();
        wsf.setSourceJobId(j.getId());
        wsf = workspaceDAO.insert(wsf, false);
        
        DeleteReport dr = jobDAO.delete(j.getId(),Boolean.FALSE);
        assertTrue(dr != null);
        assertTrue(dr.isSuccessful() == true);
        assertTrue(dr.getReason() == null);
        j = jobDAO.getJobById(j.getId().toString());
        assertTrue(j.isDeleted() == true);
        wsf = workspaceDAO.getWorkspaceFileById(wsf.getId().toString(), null);
        assertTrue(wsf.getDeleted() == true);
    }

    
    //test logical delete no workspace file
 @Test
    public void testDeleteLogicalNoWorkspaceFile() throws Exception {
        InputWorkspaceFileLinkObjectifyDAOImpl inputDAO = new InputWorkspaceFileLinkObjectifyDAOImpl();
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(inputDAO);
        WorkspaceFileObjectifyDAOImpl workspaceDAO = new WorkspaceFileObjectifyDAOImpl(jobDAO, inputDAO);
        jobDAO.setWorkspaceFileDAO(workspaceDAO);
        Job j = new Job();
        j = jobDAO.insert(j, true);
        
        DeleteReport dr = jobDAO.delete(j.getId(),null);
        assertTrue(dr != null);
        assertTrue(dr.isSuccessful() == true);
        assertTrue(dr.getReason() == null);
         j = jobDAO.getJobById(j.getId().toString());
        assertTrue(j.isDeleted() == true);
        
    }
    
    //test permanent delete
    @Test
    public void testDeletePermanent() throws Exception {
        InputWorkspaceFileLinkObjectifyDAOImpl inputDAO = new InputWorkspaceFileLinkObjectifyDAOImpl();
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(inputDAO);
        WorkspaceFileObjectifyDAOImpl workspaceDAO = new WorkspaceFileObjectifyDAOImpl(jobDAO, inputDAO);
        jobDAO.setWorkspaceFileDAO(workspaceDAO);
        Job j = new Job();
        j = jobDAO.insert(j, true);
        WorkspaceFile wsf = new WorkspaceFile();
        wsf.setSourceJobId(j.getId());
        wsf = workspaceDAO.insert(wsf, false);
        
        DeleteReport dr = jobDAO.delete(j.getId(),Boolean.TRUE);
        assertTrue(dr != null);
        assertTrue(dr.isSuccessful() == true);
        assertTrue(dr.getReason() == null);
        j = jobDAO.getJobById(j.getId().toString());
        assertTrue(j == null);
        wsf = workspaceDAO.getWorkspaceFileById(wsf.getId().toString(), null);
        assertTrue(wsf == null);
    }
    
    //test permanent delete no workspace file
@Test
    public void testDeletePermanentNoWorkspaceFile() throws Exception {
        InputWorkspaceFileLinkObjectifyDAOImpl inputDAO = new InputWorkspaceFileLinkObjectifyDAOImpl();
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(inputDAO);
        WorkspaceFileObjectifyDAOImpl workspaceDAO = new WorkspaceFileObjectifyDAOImpl(jobDAO, inputDAO);
        jobDAO.setWorkspaceFileDAO(workspaceDAO);
        Job j = new Job();
        j = jobDAO.insert(j, true);
        
        DeleteReport dr = jobDAO.delete(j.getId(),Boolean.TRUE);
        assertTrue(dr != null);
        assertTrue(dr.isSuccessful() == true);
        assertTrue(dr.getReason() == null);
         j = jobDAO.getJobById(j.getId().toString());
        assertTrue(j == null);
        
    }
 
    
    @Test
    public void testResaveNonExistingJob() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);

        try {
            jobDAO.resave(1L);
            fail("Expected exception");
        } catch (Exception ex) {
            assertTrue(ex.getMessage().equals("There was an error resaving job with id: 1"));
        }
    }

    @Test
    public void testResaveOnValidJob() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);

        Job j = new Job();
        j.setName("bob");
        Job resJob = jobDAO.insert(j, true);
        Job resavedJob = jobDAO.resave(resJob.getId());
        assertTrue(resavedJob.getName().equals("bob"));
        assertTrue(resJob.getId() == resavedJob.getId().longValue());
    }

    @Test
    public void testGetJobByIdWithNullId() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        try {
            jobDAO.getJobById(null);
            fail("Expected Exception");
        } catch (NullPointerException ex) {
            assertTrue(ex.getMessage(),
                    ex.getMessage().equals("jobId cannot be null"));
        }
    }

    @Test
    public void testGetJobByIdWithNonnumericId() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        try {
            jobDAO.getJobById("foo");
            fail("Expected Exception");
        } catch (Exception ex) {
            assertTrue(ex.getMessage(),
                    ex.getMessage().equals("jobId must be numeric, error "
                            + "received when parsing : For input string: "
                            + "\"foo\""));
        }
    }

    @Test
    public void testGetJobByIdAndUserWithNullValues() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);

        try {
            jobDAO.getJobByIdAndUser(null, null);
            fail("expected exception");
        } catch (Exception ex) {
            assertTrue(ex.getMessage(),
                    ex.getMessage().equals("User cannot be null"));
        }
        try {
            jobDAO.getJobByIdAndUser(null, "bob");
            fail("Expected Exception");
        } catch (Exception ex) {
            assertTrue(ex.getMessage(),
                    ex.getMessage().equals("jobId cannot be null"));
        }
    }

    @Test
    public void testGetJobByIdAndUserNoJobFound() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        assertNull(jobDAO.getJobByIdAndUser("1", "bob"));
    }

    @Test
    public void testGetJobByIdAndUserButUserDoesNotMatchAsItsNullOrDifferent() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        Job resJob = jobDAO.insert(j, true);
        assertNull(jobDAO.getJobByIdAndUser(resJob.getId().toString(), "bob"));

        resJob.setOwner("phil");
        jobDAO.update(resJob);
        assertNull(jobDAO.getJobByIdAndUser(resJob.getId().toString(), "bob"));
    }

    @Test
    public void testGetJobByIdAndUserWithMatch() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setOwner("bob");
        Job resJob = jobDAO.insert(j, true);
        Job gotIt = jobDAO.getJobByIdAndUser(resJob.getId().toString(), "bob");
        assertTrue(gotIt != null);
        assertTrue(gotIt.getId() == resJob.getId().longValue());
        assertTrue(gotIt.getOwner().equals("bob"));
    }

    /**
     * test getJobs
     */
    // test all null no jobs found
    @Test
    public void testGetJobsWithNoJobsFound() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        List<Job> jobs = jobDAO.getJobs(null, null, null, true, true, null);
        assertTrue(jobs.isEmpty());
        assertTrue(jobDAO.getJobsCount(null, null, null, null) == 0);

    }

    // test all null 1 job, then multiple jobs
    @Test
    public void testGetJobsNullValsAndOneJobReturned() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(jobDAO);

        Workflow w = new Workflow();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setName("param");
        wp.setType("string");
        ArrayList<WorkflowParameter> wparams = new ArrayList<WorkflowParameter>();
        wparams.add(wp);
        w.setParameters(wparams);
        w = workflowDAO.insert(w);
        Job j = new Job();
        j.setWorkflow(w);
        j.setName("hi");
        ArrayList<Parameter> params = new ArrayList<Parameter>();
        Parameter p = new Parameter();
        p.setName("param");
        p.setValue("value");
        p.setIsWorkspaceId(false);
        params.add(p);
        j.setParameters(params);
        jobDAO.insert(j, true);
        List<Job> jobs = jobDAO.getJobs(null, null, null, false, false, null);
        assertTrue(jobs.size() == 1);
        assertTrue(jobs.get(0).getName().equals("hi"));
        assertTrue(jobs.get(0).getParameters() != null);
        assertTrue(jobs.get(0).getWorkflow().getParameters() != null);
        assertTrue(jobDAO.getJobsCount(null, null, null, null) == 1);
    }

    // test with both noworkflowparams and noparams true

    @Test
    public void testGetJobsNullValsAndNoParamsNoWorkFlowTrueAndOneJobReturned() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(jobDAO);

        Workflow w = new Workflow();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setName("param");
        wp.setType("string");
        ArrayList<WorkflowParameter> wparams = new ArrayList<WorkflowParameter>();
        wparams.add(wp);
        w.setParameters(wparams);
        w = workflowDAO.insert(w);
        Job j = new Job();
        j.setWorkflow(w);
        j.setName("hi");
        ArrayList<Parameter> params = new ArrayList<Parameter>();
        Parameter p = new Parameter();
        p.setName("param");
        p.setValue("value");
        p.setIsWorkspaceId(false);
        params.add(p);
        j.setParameters(params);
        jobDAO.insert(j, true);
        List<Job> jobs = jobDAO.getJobs(null, null, null, true, true, null);
        assertTrue(jobs.size() == 1);
        assertTrue(jobs.get(0).getName().equals("hi"));
        assertTrue(jobs.get(0).getParameters() == null);
        assertTrue(jobs.get(0).getWorkflow().getParameters() == null);
        assertTrue(jobDAO.getJobsCount(null, null, null, null) == 1);
    }

    // test with noworkflowparams true
    @Test
    public void testGetJobsNullValsAndNoWorkFlowTrueAndOneJobReturned() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(jobDAO);

        Workflow w = new Workflow();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setName("param");
        wp.setType("string");
        ArrayList<WorkflowParameter> wparams = new ArrayList<WorkflowParameter>();
        wparams.add(wp);
        w.setParameters(wparams);
        w = workflowDAO.insert(w);
        Job j = new Job();
        j.setWorkflow(w);
        j.setName("hi");
        ArrayList<Parameter> params = new ArrayList<Parameter>();
        Parameter p = new Parameter();
        p.setName("param");
        p.setValue("value");
        p.setIsWorkspaceId(false);
        params.add(p);
        j.setParameters(params);
        jobDAO.insert(j, true);
        List<Job> jobs = jobDAO.getJobs(null, null, null, false, true, null);
        assertTrue(jobs.size() == 1);
        assertTrue(jobs.get(0).getName().equals("hi"));
        assertTrue(jobs.get(0).getParameters() != null);
        assertTrue(jobs.get(0).getWorkflow().getParameters() == null);
        assertTrue(jobDAO.getJobsCount(null, null, null, null) == 1);
    }

    // test with noparams true
    @Test
    public void testGetJobsNullValsAndNoParamsTrueAndOneJobReturned() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(jobDAO);

        Workflow w = new Workflow();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setName("param");
        wp.setType("string");
        ArrayList<WorkflowParameter> wparams = new ArrayList<WorkflowParameter>();
        wparams.add(wp);
        w.setParameters(wparams);
        w = workflowDAO.insert(w);
        Job j = new Job();
        j.setWorkflow(w);
        j.setName("hi");
        ArrayList<Parameter> params = new ArrayList<Parameter>();
        Parameter p = new Parameter();
        p.setName("param");
        p.setValue("value");
        p.setIsWorkspaceId(false);
        params.add(p);
        j.setParameters(params);
        jobDAO.insert(j, true);
        List<Job> jobs = jobDAO.getJobs(null, null, null, true, false, null);
        assertTrue(jobs.size() == 1);
        assertTrue(jobs.get(0).getName().equals("hi"));
        assertTrue(jobs.get(0).getParameters() == null);
        assertTrue(jobs.get(0).getWorkflow().getParameters() != null);
        assertTrue(jobDAO.getJobsCount(null, null, null, null) == 1);
    }

    @Test
    public void testGetJobsMultipleNoConstraints() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(jobDAO);

        Workflow w = new Workflow();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setName("param");
        wp.setType("string");
        ArrayList<WorkflowParameter> wparams = new ArrayList<WorkflowParameter>();
        wparams.add(wp);
        w.setParameters(wparams);
        w = workflowDAO.insert(w);
        Job j = new Job();
        j.setWorkflow(w);
        j.setName("hi");
        ArrayList<Parameter> params = new ArrayList<Parameter>();
        Parameter p = new Parameter();
        p.setName("param");
        p.setValue("value");
        p.setIsWorkspaceId(false);
        params.add(p);
        j.setParameters(params);
        jobDAO.insert(j, true);
        Job j2 = new Job();
        j2.setName("job2");
        jobDAO.insert(j2, true);
        List<Job> jobs = jobDAO.getJobs(null, null, null, true, true, null);
        assertTrue(jobs.size() == 2);
        assertTrue(jobDAO.getJobsCount(null, null, null, null) == 2);
    }

    @Test
    public void testGetJobsMultipleWithVariousConstraints() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j1 = new Job();
        j1.setName("j1");
        j1.setOwner("bob");
        j1.setHasJobBeenSubmittedToScheduler(false);
        j1.setStatus(Job.IN_QUEUE_STATUS);
        jobDAO.insert(j1, true);

        Job j2 = new Job();
        j2.setName("j2");
        j2.setOwner("joe");
        j2.setHasJobBeenSubmittedToScheduler(true);
        j2.setStatus(Job.RUNNING_STATUS);
        jobDAO.insert(j2, true);

        Job j3 = new Job();
        j3.setName("j3");
        j3.setOwner("joe");
        j3.setHasJobBeenSubmittedToScheduler(true);
        j3.setStatus(Job.ERROR_STATUS);
        j3.setDeleted(true);
        jobDAO.insert(j3, true);

        //make sure deleted not shown
        List<Job> jobs = jobDAO.getJobs(null, null, null, true, true, null);
        assertTrue(jobs.size() == 2);
        assertTrue(jobDAO.getJobsCount(null, null, null, null) == 2);

        //constrain not submitted to sched set to true
        jobs = jobDAO.getJobs(null, null, Boolean.TRUE, true, true, null);
        assertTrue(jobs.size() == 1);
        assertTrue(jobDAO.getJobsCount(null, null, Boolean.TRUE, null) == 1);

        //constrain not submitted to sched set to false
        jobs = jobDAO.getJobs(null, null, Boolean.FALSE, true, true, null);
        assertTrue(jobs.size() == 2);
        assertTrue(jobDAO.getJobsCount(null, null, Boolean.FALSE, null) == 2);

        //make sure all shown
        jobs = jobDAO.getJobs(null, null, null, true, true, Boolean.TRUE);
        assertTrue(jobs.size() == 3);
        assertTrue(jobDAO.getJobsCount(null, null, null, Boolean.TRUE) == 3);

        //try owner constraint which will only kick out 1 cause 1 is deleted
        jobs = jobDAO.getJobs("joe", null, null, true, true, null);
        assertTrue(jobs.size() == 1);
        assertTrue(jobDAO.getJobsCount("joe", null, null, null) == 1);
        //dido as previous
        jobs = jobDAO.getJobs("joe", null, null, true, true, Boolean.FALSE);
        assertTrue(jobs.size() == 1);
        assertTrue(jobDAO.getJobsCount("joe", null, null, Boolean.FALSE) == 1);

        //try non existant owner
        jobs = jobDAO.getJobs("joexxx", null, null, true, true, null);
        assertTrue(jobs.size() == 0);
        assertTrue(jobDAO.getJobsCount("joexxx", null, null, null) == 0);

        jobs = jobDAO.getJobs(null, Job.IN_QUEUE_STATUS, null, true, true, null);
        assertTrue(jobs.size() == 1);
        assertTrue(jobDAO.getJobsCount(null, Job.IN_QUEUE_STATUS, null, null) == 1);

        jobs = jobDAO.getJobs(null, Job.IN_QUEUE_STATUS + ","
                + Job.RUNNING_STATUS, null, true, true, null);
        assertTrue(jobs.size() == 2);
        assertTrue(jobDAO.getJobsCount(null, Job.IN_QUEUE_STATUS + ","
                + Job.RUNNING_STATUS, null, null) == 2);

        //try deleted and joe constraint
        jobs = jobDAO.getJobs("joe", null, null, true, true, Boolean.TRUE);
        assertTrue(jobs.size() == 2);
        assertTrue(jobDAO.getJobsCount("joe", null, null, Boolean.TRUE) == 2);

    }

    // test insert null job
    @Test
    public void testInsertWithNullJob() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        try {
            jobDAO.insert(null, true);
            fail("expected exception");
        } catch (NullPointerException npe) {
            assertTrue(npe.getMessage().equals("Job is null"));
        }

    }

    //test insert skip workflow false and workflow is null
    @Test
    public void testInsertWithNullWorkflow() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();

        try {
            jobDAO.insert(j, false);
            fail("expected exception");
        } catch (NullPointerException npe) {
            assertTrue(npe.getMessage().equals("Job Workflow cannot be null"));
        }
    }

    //test insert skip workflow false and job workflow id is null
    @Test
    public void testInsertWithIdOfWorkflowNull() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        Workflow w = new Workflow();
        j.setWorkflow(w);
        try {
            jobDAO.insert(j, false);
            fail("expected exception");
        } catch (Exception ex) {
            assertTrue(ex.getMessage().equals("Job Workflow id is either null or 0 or less which is not valid"));
        }
    }

    //test insert skip workflow false and job workflow id is 0 or less then 0
    @Test
    public void testInsertWithIdOfWorkflowSetToZero() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        Workflow w = new Workflow();
        w.setId(0L);
        j.setWorkflow(w);
        try {
            jobDAO.insert(j, false);
            fail("expected exception");
        } catch (Exception ex) {
            assertTrue(ex.getMessage().equals("Job Workflow id is either null or 0 or less which is not valid"));
        }
    }

    //test insert skip workflow false where workflow for job does not exist
    @Test
    public void testInsertWhereWorkflowDoesNotExist() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        Workflow w = new Workflow();
        w.setId(25L);
        j.setWorkflow(w);
        try {
            jobDAO.insert(j, false);
            fail("expected exception");
        } catch (Exception ex) {
            assertTrue(ex.getMessage().equals("Unable to load Workflow (25) for Job"));
        }
    }

    //test valid insert & create date null with skip workflow true

    @Test
    public void testInsertWithCreateDateNull() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        Job resJob = jobDAO.insert(j, true);
        assertTrue(resJob != null);
        assertTrue(resJob.getId() != null);
        assertTrue(resJob.getCreateDate() != null);
    }

    //test valid insert & create date not null with skip workflow true
    @Test
    public void testInsertWithCreateDateNotNull() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        Date theDate = new Date();
        j.setCreateDate(theDate);
        Job resJob = jobDAO.insert(j, true);
        assertTrue(resJob != null);
        assertTrue(resJob.getId() != null);
        assertTrue(resJob.getCreateDate() == theDate);
    }

    //test insert job with skip workflow false and no parameters
    @Test
    public void testInsertWithSkipWorkflowFalseAndNoParams() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(jobDAO);
        Workflow w = new Workflow();
        w = workflowDAO.insert(w);
        Job j = new Job();
        j.setWorkflow(w);
        Job resJob = jobDAO.insert(j, false);
        assertTrue(resJob != null);
        assertTrue(resJob.getId() != null);
        assertTrue(resJob.getWorkflow().getId() == w.getId().longValue());
    }

    //test insert job with parameters and skip workflow true, no params are file
    @Test
    public void testInsertWhereNoParamsAreFileParams() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        ArrayList<Parameter> params = new ArrayList<Parameter>();
        Parameter p = new Parameter();
        p.setName("foo");
        p.setValue("val");
        params.add(p);

        p = new Parameter();
        p.setName("foo2");
        p.setValue("val2");
        params.add(p);

        p = new Parameter();
        p.setName("foo3");
        p.setValue("val3");
        params.add(p);
        j.setParameters(params);
        Job resJob = jobDAO.insert(j, true);
        assertTrue(resJob != null);
        assertTrue(resJob.getId() != null);
    }

    //test insert job with parameters and skip workflow true, 1 param is a file
    @Test
    public void testInsertWhereOneParamIsFile() throws Exception {
        InputWorkspaceFileLinkObjectifyDAOImpl inputDAO = new InputWorkspaceFileLinkObjectifyDAOImpl();
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(inputDAO);
        WorkspaceFileObjectifyDAOImpl workspaceDAO = new WorkspaceFileObjectifyDAOImpl(jobDAO, inputDAO);

        WorkspaceFile wsf = new WorkspaceFile();
        wsf.setName("bob");
        workspaceDAO.insert(wsf, false);

        Job j = new Job();
        ArrayList<Parameter> params = new ArrayList<Parameter>();
        Parameter p = new Parameter();
        p.setName("foo");
        p.setValue("val");
        params.add(p);

        p = new Parameter();
        p.setName("foo2");
        p.setValue(wsf.getId().toString());
        p.setIsWorkspaceId(true);
        params.add(p);

        p = new Parameter();
        p.setName("foo3");
        p.setValue("val3");
        params.add(p);
        j.setParameters(params);
        Job resJob = jobDAO.insert(j, true);
        assertTrue(resJob != null);
        assertTrue(resJob.getId() != null);

        List<InputWorkspaceFileLink> linkList = inputDAO.getInputWorkspaceFileLinks(null);
        assertTrue(linkList.size() == 1);
        assertTrue(linkList.get(0).getWorkspaceFile().getId() == wsf.getId().longValue());
        assertTrue(linkList.get(0).getParameterName().equals("foo2"));

    }

    @Test
    public void testInsertWhereTwoParamsAreFiles() throws Exception {
        InputWorkspaceFileLinkObjectifyDAOImpl inputDAO = new InputWorkspaceFileLinkObjectifyDAOImpl();
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(inputDAO);
        WorkspaceFileObjectifyDAOImpl workspaceDAO = new WorkspaceFileObjectifyDAOImpl(jobDAO, inputDAO);

        WorkspaceFile wsf = new WorkspaceFile();
        wsf.setName("bob");
        workspaceDAO.insert(wsf, false);

        WorkspaceFile wsf2 = new WorkspaceFile();
        wsf2.setName("bob2");
        workspaceDAO.insert(wsf2, false);

        Job j = new Job();
        ArrayList<Parameter> params = new ArrayList<Parameter>();
        Parameter p = new Parameter();
        p.setName("foo");
        p.setValue("val");
        params.add(p);

        p = new Parameter();
        p.setName("foo2");
        p.setValue(wsf.getId().toString());
        p.setIsWorkspaceId(true);
        params.add(p);

        p = new Parameter();
        p.setName("foo3");
        p.setValue(wsf2.getId().toString());
        p.setIsWorkspaceId(true);
        params.add(p);
        j.setParameters(params);
        Job resJob = jobDAO.insert(j, true);
        assertTrue(resJob != null);
        assertTrue(resJob.getId() != null);

        List<InputWorkspaceFileLink> linkList = inputDAO.getInputWorkspaceFileLinks(null);
        assertTrue(linkList.size() == 2);
    }

    //test job not found
    @Test
    public void testUpdateMultiArgJobNotFound() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        assertNull(jobDAO.update(1L,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null));
    }

    //test nothing to update (all null)

    @Test
    public void testUpdateMultiArgJobAllNull() throws Exception {

        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setName("bob");
        j.setError("error");
        j = jobDAO.insert(j, true);
        Job resJob = jobDAO.update(j.getId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        assertTrue(resJob.getId() == j.getId().longValue());
        assertTrue(resJob.getError().equals("error"));
    }

    //test update fields 1 at a time then all fields
    @Test
    public void testUpdateMultiArgJobUpdateStatus() throws Exception {

        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setName("bob");
        j = jobDAO.insert(j, true);
        Job resJob = jobDAO.update(j.getId(),
                "ha",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        assertTrue(resJob.getId() == j.getId().longValue());
        assertTrue(resJob.getStatus().equals("ha"));
    }

    @Test
    public void testUpdateMultiArgJobUpdateEstCpu() throws Exception {

        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setName("bob");
        j = jobDAO.insert(j, true);
        Job resJob = jobDAO.update(j.getId(),
                null,
                1L,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        assertTrue(resJob.getId() == j.getId().longValue());
        assertTrue(resJob.getEstimatedCpuInSeconds() == 1L);
    }

    @Test
    public void testUpdateMultiArgJobUpdateEstRunTime() throws Exception {

        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setName("bob");
        j = jobDAO.insert(j, true);
        Job resJob = jobDAO.update(j.getId(),
                null,
                null,
                1L,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        assertTrue(resJob.getId() == j.getId().longValue());
        assertTrue(resJob.getEstimatedRunTimeInSeconds() == 1L);
    }

    @Test
    public void testUpdateMultiArgJobUpdateEstDisk() throws Exception {

        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setName("bob");
        j = jobDAO.insert(j, true);
        Job resJob = jobDAO.update(j.getId(),
                null,
                null,
                null,
                1L,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        assertTrue(resJob.getId() == j.getId().longValue());
        assertTrue(resJob.getEstimatedDiskInBytes() == 1L);
    }

    @Test
    public void testUpdateMultiArgJobUpdateSubmitDate() throws Exception {

        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setName("bob");
        j = jobDAO.insert(j, true);
        Job resJob = jobDAO.update(j.getId(),
                null,
                null,
                null,
                null,
                1L,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        assertTrue(resJob.getId() == j.getId().longValue());
        assertTrue(Long.toString(resJob.getSubmitDate().getTime()),
                resJob.getSubmitDate().getTime() == 1L);
    }

    @Test
    public void testUpdateMultiArgJobUpdateStartDate() throws Exception {

        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setName("bob");
        j = jobDAO.insert(j, true);
        Job resJob = jobDAO.update(j.getId(),
                null,
                null,
                null,
                null,
                null,
                1L,
                null,
                null,
                null,
                null,
                null,
                null);
        assertTrue(resJob.getId() == j.getId().longValue());
        assertTrue(Long.toString(resJob.getStartDate().getTime()),
                resJob.getStartDate().getTime() == 1L);
    }

    @Test
    public void testUpdateMultiArgJobUpdateFinishDate() throws Exception {

        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setName("bob");
        j = jobDAO.insert(j, true);
        Job resJob = jobDAO.update(j.getId(),
                null,
                null,
                null,
                null,
                null,
                null,
                1L,
                null,
                null,
                null,
                null,
                null);
        assertTrue(resJob.getId() == j.getId().longValue());
        assertTrue(Long.toString(resJob.getFinishDate().getTime()),
                resJob.getFinishDate().getTime() == 1L);
    }

    @Test
    public void testUpdateMultiArgJobUpdateSubmittedToScheduler() throws Exception {

        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setName("bob");
        j = jobDAO.insert(j, true);
        Job resJob = jobDAO.update(j.getId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Boolean.TRUE,
                null,
                null,
                null,
                null);
        assertTrue(resJob.getId() == j.getId().longValue());
        assertTrue(resJob.getHasJobBeenSubmittedToScheduler() == true);
    }

    @Test
    public void testUpdateMultiArgJobUpdateSchedulerJobId() throws Exception {

        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setName("bob");
        j = jobDAO.insert(j, true);
        Job resJob = jobDAO.update(j.getId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                "ha",
                null,
                null,
                null);
        assertTrue(resJob.getId() == j.getId().longValue());
        assertTrue(resJob.getSchedulerJobId().equals("ha"));
    }

    @Test
    public void testUpdateMultiArgJobUpdateDeleted() throws Exception {

        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setName("bob");
        j = jobDAO.insert(j, true);
        Job resJob = jobDAO.update(j.getId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Boolean.TRUE,
                null,
                null);
        assertTrue(resJob.getId() == j.getId().longValue());
        assertTrue(resJob.isDeleted() == true);
    }

    @Test
    public void testUpdateMultiArgJobUpdateError() throws Exception {

        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setName("bob");
        j = jobDAO.insert(j, true);
        Job resJob = jobDAO.update(j.getId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                "error",
                null);
        assertTrue(resJob.getId() == j.getId().longValue());
        assertTrue(resJob.getError().equals("error"));
    }

    @Test
    public void testUpdateMultiArgJobUpdateDetailedError() throws Exception {

        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setName("bob");
        j = jobDAO.insert(j, true);
        Job resJob = jobDAO.update(j.getId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                "ha");
        assertTrue(resJob.getId() == j.getId().longValue());
        assertTrue(resJob.getDetailedError().equals("ha"));
    }

    @Test
    public void testUpdateMultiArgJobUpdateAll() throws Exception {

        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setName("bob");
        j = jobDAO.insert(j, true);
        Job resJob = jobDAO.update(j.getId(),
                "status",
                1L,
                2L,
                3L,
                4L,
                5L,
                6L,
                Boolean.TRUE,
                "schedulerjobid",
                Boolean.TRUE,
                "error",
                "detailed");
        assertTrue(resJob.getId() == j.getId().longValue());
        assertTrue(resJob.getStatus().equals("status"));
        assertTrue(resJob.getEstimatedCpuInSeconds() == 1L);
        assertTrue(resJob.getEstimatedRunTimeInSeconds() == 2L);
        assertTrue(resJob.getEstimatedDiskInBytes() == 3L);
        assertTrue(resJob.getSubmitDate().getTime() == 4L);
        assertTrue(resJob.getStartDate().getTime() == 5L);
        assertTrue(resJob.getFinishDate().getTime() == 6L);
        assertTrue(resJob.getHasJobBeenSubmittedToScheduler() == true);
        assertTrue(resJob.getSchedulerJobId().equals("schedulerjobid"));
        assertTrue(resJob.isDeleted() == true);
        assertTrue(resJob.getError().equals("error"));
        assertTrue(resJob.getDetailedError().equals("detailed"));
    }

    //test update null job
    @Test
    public void testUpdateNullJob() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        try {
            jobDAO.update(null);
            fail("Expected exception");
        } catch (NullPointerException npe) {
            assertTrue(npe.getMessage().equals("Job cannot be null"));
        }
    }

    //test update job id is null
    @Test
    public void testUpdateJobWithNullId() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        try {
            jobDAO.update(new Job());
            fail("Expected exception");
        } catch (Exception ex) {
            assertTrue(ex.getMessage().equals("Id must be set for Job"));
        }
    }

    //test update set name null after it was set
    @Test
    public void testUpdateSuccessful() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setName("bob");
        j = jobDAO.insert(j, true);
        j.setName(null);
        j = jobDAO.update(j);
        assertTrue(j.getName() == null);

    }
}
