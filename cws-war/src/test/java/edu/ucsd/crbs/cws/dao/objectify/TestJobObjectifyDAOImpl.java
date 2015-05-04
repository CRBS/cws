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
import static edu.ucsd.crbs.cws.dao.objectify.OfyService.ofy;
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.Parameter;
import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.WorkflowParameter;
import edu.ucsd.crbs.cws.workflow.report.DeleteReport;
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



/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestJobObjectifyDAOImpl {

     private final LocalServiceTestHelper _helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(),
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
    
    @Test
    public void testResaveNonExistingJob() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        
        try {
            jobDAO.resave(1L);
            fail("Expected exception");
        }
        catch(Exception ex){
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
        }
        catch(NullPointerException ex){
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
        }
        catch(Exception ex){
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
        }
        catch(Exception ex){
            assertTrue(ex.getMessage(),
                    ex.getMessage().equals("User cannot be null"));
        }
        try {
            jobDAO.getJobByIdAndUser(null,"bob");
            fail("Expected Exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage(),
                    ex.getMessage().equals("jobId cannot be null"));
        }
    }
    
    
    @Test
    public void testGetJobByIdAndUserNoJobFound() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        assertNull(jobDAO.getJobByIdAndUser("1","bob"));
    }
    
    @Test
    public void testGetJobByIdAndUserButUserDoesNotMatchAsItsNullOrDifferent() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        Job resJob = jobDAO.insert(j, true);
        assertNull(jobDAO.getJobByIdAndUser(resJob.getId().toString(),"bob"));
        
        resJob.setOwner("phil");
        jobDAO.update(resJob);
        assertNull(jobDAO.getJobByIdAndUser(resJob.getId().toString(),"bob"));
    }
    
    @Test
    public void testGetJobByIdAndUserWithMatch() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setOwner("bob");
        Job resJob = jobDAO.insert(j, true);
        Job gotIt = jobDAO.getJobByIdAndUser(resJob.getId().toString(),"bob");
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
        List<Job> jobs = jobDAO.getJobs(null, null, null, true, true,null);
        assertTrue(jobs.isEmpty());
        assertTrue(jobDAO.getJobsCount(null, null, null,null) == 0);
        
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
        List<Job> jobs = jobDAO.getJobs(null, null, null, false, false,null);
        assertTrue(jobs.size() == 1);
        assertTrue(jobs.get(0).getName().equals("hi"));
        assertTrue(jobs.get(0).getParameters() != null);
        assertTrue(jobs.get(0).getWorkflow().getParameters() != null);
        assertTrue(jobDAO.getJobsCount(null, null, null,null) == 1);
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
        List<Job> jobs = jobDAO.getJobs(null, null, null, true, true,null);
        assertTrue(jobs.size() == 1);
        assertTrue(jobs.get(0).getName().equals("hi"));
        assertTrue(jobs.get(0).getParameters() == null);
        assertTrue(jobs.get(0).getWorkflow().getParameters() == null);
        assertTrue(jobDAO.getJobsCount(null, null, null,null) == 1);
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
        List<Job> jobs = jobDAO.getJobs(null, null, null, false, true,null);
        assertTrue(jobs.size() == 1);
        assertTrue(jobs.get(0).getName().equals("hi"));
        assertTrue(jobs.get(0).getParameters() != null);
        assertTrue(jobs.get(0).getWorkflow().getParameters() == null);
        assertTrue(jobDAO.getJobsCount(null, null, null,null) == 1);
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
        List<Job> jobs = jobDAO.getJobs(null, null, null, true, false,null);
        assertTrue(jobs.size() == 1);
        assertTrue(jobs.get(0).getName().equals("hi"));
        assertTrue(jobs.get(0).getParameters() == null);
        assertTrue(jobs.get(0).getWorkflow().getParameters() != null);
        assertTrue(jobDAO.getJobsCount(null, null, null,null) == 1);
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
        List<Job> jobs = jobDAO.getJobs(null, null, null, true, true,null);
        assertTrue(jobs.size() == 2);
        assertTrue(jobDAO.getJobsCount(null, null, null,null) == 2);
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
        assertTrue(jobDAO.getJobsCount(null, null, null,null) == 2);
        
        //constrain not submitted to sched set to true
        jobs = jobDAO.getJobs(null, null, Boolean.TRUE, true, true, null);
        assertTrue(jobs.size() == 1);
        assertTrue(jobDAO.getJobsCount(null, null, Boolean.TRUE,null) == 1);
        
        //constrain not submitted to sched set to false
        jobs = jobDAO.getJobs(null, null, Boolean.FALSE, true, true, null);
        assertTrue(jobs.size() == 2);
        assertTrue(jobDAO.getJobsCount(null, null, Boolean.FALSE,null) == 2);

        
        //make sure all shown
        jobs = jobDAO.getJobs(null, null, null, true, true, Boolean.TRUE);
        assertTrue(jobs.size() == 3);
        assertTrue(jobDAO.getJobsCount(null, null, null,Boolean.TRUE) == 3);
        
    
        //try owner constraint which will only kick out 1 cause 1 is deleted
        jobs = jobDAO.getJobs("joe", null, null, true, true, null);
        assertTrue(jobs.size() == 1);
        assertTrue(jobDAO.getJobsCount("joe", null, null,null) == 1);
        //dido as previous
        jobs = jobDAO.getJobs("joe", null, null, true, true, Boolean.FALSE);
        assertTrue(jobs.size() == 1);
        assertTrue(jobDAO.getJobsCount("joe", null, null,Boolean.FALSE) == 1);
      
        //try non existant owner
        jobs = jobDAO.getJobs("joexxx", null, null, true, true, null);
        assertTrue(jobs.size() == 0);
        assertTrue(jobDAO.getJobsCount("joexxx", null, null,null) == 0);
        
        jobs = jobDAO.getJobs(null, Job.IN_QUEUE_STATUS, null, true, true, null);
        assertTrue(jobs.size() == 1);
        assertTrue(jobDAO.getJobsCount(null, Job.IN_QUEUE_STATUS, null,null) == 1);
        
        jobs = jobDAO.getJobs(null, Job.IN_QUEUE_STATUS+","
                +Job.RUNNING_STATUS, null, true, true, null);
        assertTrue(jobs.size() == 2);
        assertTrue(jobDAO.getJobsCount(null, Job.IN_QUEUE_STATUS+","
                +Job.RUNNING_STATUS, null,null) == 2);
        
        //try deleted and joe constraint
        jobs = jobDAO.getJobs("joe", null, null, true, true, Boolean.TRUE);
        assertTrue(jobs.size() == 2);
        assertTrue(jobDAO.getJobsCount("joe",null, null,Boolean.TRUE) == 2);
      
        
    }
    
    
    // test insert null job
    @Test
    public void testInsertWithNullJob() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        try {
            jobDAO.insert(null, true);
            fail("expected exception");
        }
        catch(NullPointerException npe){
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
        }
       
        catch(NullPointerException npe){
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
        }
        catch(Exception ex){
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
        }
        catch(Exception ex){
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
        }
        catch(Exception ex){
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
    
    /*test insert job with parameters and skip workflow true, 1 param is a file
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
        p.setName("3");
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
    */

}