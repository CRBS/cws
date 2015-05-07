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
package edu.ucsd.crbs.cws.rest;

import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import edu.ucsd.crbs.cws.auth.Authenticator;
import edu.ucsd.crbs.cws.auth.Permission;
import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.dao.objectify.JobObjectifyDAOImpl;
import static edu.ucsd.crbs.cws.dao.objectify.OfyService.ofy;
import edu.ucsd.crbs.cws.dao.objectify.WorkflowObjectifyDAOImpl;
import edu.ucsd.crbs.cws.dao.objectify.WorkspaceFileObjectifyDAOImpl;
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import edu.ucsd.crbs.cws.workflow.report.DeleteReport;
import edu.ucsd.crbs.cws.workflow.validate.JobParametersNullNameChecker;
import edu.ucsd.crbs.cws.workflow.validate.JobValidatorImpl;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author churas
 */
public class TestJobRestService {
    
    private final LocalServiceTestHelper _helper
            = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(),
                    new LocalBlobstoreServiceTestConfig());

    
    public TestJobRestService() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        Logger.getLogger(JobRestService.class.getName()).setLevel(Level.OFF);
        Logger.getLogger(JobValidatorImpl.class.getName()).setLevel(Level.OFF);
        Logger.getLogger(JobParametersNullNameChecker.class.getName()).setLevel(Level.OFF);
        Logger.getLogger(LocalDatastoreService.class.getName()).setLevel(Level.OFF);
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
    public void testInsertNoPermission() throws Exception {
        Authenticator auth = mock(Authenticator.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        
        User u = new User();
        when(auth.authenticate(request)).thenReturn(u);
        JobRestService jrs = new JobRestService();
        jrs.setAuthenticator(auth);
        Job j = new Job();
        try {
            jrs.createJob(j, null, null, null, request);
            fail("Expected exception");
        }
        catch(WebApplicationException wae){
            
        }
    }
    
    @Test
    public void testInsertValidationFail() throws Exception {
        Authenticator auth = mock(Authenticator.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        
        User u = new User();
        u.setLogin("bob");
        u.setPermissions(Permission.CREATE_JOB);
        when(auth.authenticate(request)).thenReturn(u);
        JobRestService jrs = new JobRestService();
        jrs.setAuthenticator(auth);
        Job j = new Job();
        j = jrs.createJob(j, null, null, null, request);
        assertTrue(j != null);
        assertTrue(j.getError() != null);
        assertTrue(j.getError(),j.getError().equals("No Workflow found"));
    }
    
    @Test
    public void testInsertSuccessful() throws Exception {
        Authenticator auth = mock(Authenticator.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(jobDAO);
        Workflow w = new Workflow();
        w.setName("bob");
        w.setOwner("joe");
        
        workflowDAO.insert(w);
        User u = new User();
        u.setLogin("bob");
        u.setPermissions(Permission.CREATE_JOB);
        when(auth.authenticate(request)).thenReturn(u);
        JobRestService jrs = new JobRestService();
        jrs.setAuthenticator(auth);
        Job j = new Job();
        j.setOwner("bob");
        j.setName("myjob");
        j.setHasJobBeenSubmittedToScheduler(true);
        j.setStartDate(new Date());
        j.setSubmitDate(new Date());
        j.setFinishDate(new Date());
        j.setStatus(null);
        j.setWorkflow(w);
        j = jrs.createJob(j, null, null, null, request);
        assertTrue(j.getError() == null);
        assertTrue(j.getStatus().equals(Job.IN_QUEUE_STATUS));
        assertTrue(j.getStartDate() == null);
        assertTrue(j.getSubmitDate() == null);
        assertTrue(j.getFinishDate() == null);
        assertTrue(j.getHasJobBeenSubmittedToScheduler() == false);
        assertTrue(j.getDownloadURL() == null);
        WorkspaceFileObjectifyDAOImpl workspaceDAO = new WorkspaceFileObjectifyDAOImpl(jobDAO,null);
        List<WorkspaceFile> wsf = workspaceDAO.getWorkspaceFilesBySourceJobId(j.getId());
        assertTrue(wsf.size() == 1);
        
        
        
    }
    
    //test user not authorized to delete
    @Test
    public void testDeleteNotAuthorized() throws Exception {
        Authenticator auth = mock(Authenticator.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        
        User u = new User();
        when(auth.authenticate(request)).thenReturn(u);
        JobRestService jrs = new JobRestService();
        jrs.setAuthenticator(auth);
        DeleteReport dr = jrs.delete(1L, null, null, null, null, request);
        assertTrue(dr != null);
        assertTrue(dr.getId() == 1L);
        assertTrue(dr.isSuccessful() == false);
        assertTrue(dr.getReason().equals("Not authorized to delete"));
    }
    
    //test job not found when authorized to delete all jobs
    @Test
    public void testDeleteAuthorizedToDeleteAllJobsButJobNotFound() throws Exception {
        Authenticator auth = mock(Authenticator.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        
        User u = new User();
        u.setPermissions(Permission.DELETE_ALL_JOBS);
        when(auth.authenticate(request)).thenReturn(u);
        JobRestService jrs = new JobRestService();
        jrs.setAuthenticator(auth);
        DeleteReport dr = jrs.delete(1L, null, null, null, null, request);
        assertTrue(dr != null);
        assertTrue(dr.getId() == 1L);
        assertTrue(dr.isSuccessful() == false);
        assertTrue(dr.getReason(),dr.getReason().equals("Job (1) not found"));
    }
    
    //test job not found authorized to ownly delete their jobs
    @Test
    public void testDeleteAuthorizedToDeleteTheirJobsButJobNotFound() throws Exception {
        Authenticator auth = mock(Authenticator.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        
        User u = new User();
        u.setPermissions(Permission.DELETE_THEIR_JOBS);
        when(auth.authenticate(request)).thenReturn(u);
        JobRestService jrs = new JobRestService();
        jrs.setAuthenticator(auth);
        DeleteReport dr = jrs.delete(1L, null, null, null, null, request);
        assertTrue(dr != null);
        assertTrue(dr.getId() == 1L);
        assertTrue(dr.isSuccessful() == false);
        assertTrue(dr.getReason(),dr.getReason().equals("Job (1) not found"));
    }        
    
    //test delete job does not have owner (needed for their perm)
    @Test
    public void testDeleteJobDoesNotHaveOwner() throws Exception {
        Authenticator auth = mock(Authenticator.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        jobDAO.insert(j, true);
        
        
        User u = new User();
        u.setPermissions(Permission.DELETE_THEIR_JOBS);
        when(auth.authenticate(request)).thenReturn(u);
        JobRestService jrs = new JobRestService();
        jrs.setAuthenticator(auth);
        DeleteReport dr = jrs.delete(j.getId(), null, null, null, null, request);
        assertTrue(dr != null);
        assertTrue(dr.getId() == j.getId());
        assertTrue(dr.isSuccessful() == false);
        assertTrue(dr.getReason(),dr.getReason().equals("Job ("+j.getId()+
                ") does not have owner"));
    }      
    
    //test delete job, but owner does not match so not allowed
    @Test
    public void testDeleteJobWhereCallerDoesNotMatchJobOwner() throws Exception {
        Authenticator auth = mock(Authenticator.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setOwner("bob");
        jobDAO.insert(j, true);
        
        
        User u = new User();
        u.setLogin("joe");
        u.setPermissions(Permission.DELETE_THEIR_JOBS);
        when(auth.authenticate(request)).thenReturn(u);
        JobRestService jrs = new JobRestService();
        jrs.setAuthenticator(auth);
        DeleteReport dr = jrs.delete(j.getId(), null, null, null, null, request);
        assertTrue(dr != null);
        assertTrue(dr.getId() == j.getId());
        assertTrue(dr.isSuccessful() == false);
        assertTrue(dr.getReason(),dr.getReason().equals("joe does not have "
                + "permission to delete Job ("+j.getId()+")"));
        
        
    }
            
    //delete logical
     @Test
    public void testDeleteLogicalWhereOwnerAndUserDiffButHaveAllDeletePerm() throws Exception {
        Authenticator auth = mock(Authenticator.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setOwner("bob");
        jobDAO.insert(j, true);
        
        User u = new User();
        u.setLogin("joe");
        u.setPermissions(Permission.DELETE_ALL_JOBS);
        when(auth.authenticate(request)).thenReturn(u);
        JobRestService jrs = new JobRestService();
        jrs.setAuthenticator(auth);
        DeleteReport dr = jrs.delete(j.getId(), null, null, null, null, request);
        assertTrue(dr != null);
        assertTrue(dr.getId() == j.getId());
        assertTrue(dr.isSuccessful() == true);
        assertTrue(dr.getReason(),dr.getReason() == null);
        j = jobDAO.getJobById(j.getId().toString());
        assertTrue(j.isDeleted() == true);
    }

    @Test
    public void testDeleteLogicalWithTheirDeletePerm() throws Exception {
        Authenticator auth = mock(Authenticator.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setOwner("bob");
        jobDAO.insert(j, true);
        
        User u = new User();
        u.setLogin("bob");
        u.setPermissions(Permission.DELETE_THEIR_JOBS);
        when(auth.authenticate(request)).thenReturn(u);
        JobRestService jrs = new JobRestService();
        jrs.setAuthenticator(auth);
        DeleteReport dr = jrs.delete(j.getId(), null, null, null, null, request);
        assertTrue(dr != null);
        assertTrue(dr.getId() == j.getId());
        assertTrue(dr.isSuccessful() == true);
        assertTrue(dr.getReason(),dr.getReason() == null);
        j = jobDAO.getJobById(j.getId().toString());
        assertTrue(j.isDeleted() == true);
    }

    @Test
    public void testDeleteLogicalWithTheirDeletePermAndBooleanFalsePassedToPermDeleteArg() throws Exception {
        Authenticator auth = mock(Authenticator.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setOwner("bob");
        jobDAO.insert(j, true);
        
        User u = new User();
        u.setLogin("bob");
        u.setPermissions(Permission.DELETE_THEIR_JOBS);
        when(auth.authenticate(request)).thenReturn(u);
        JobRestService jrs = new JobRestService();
        jrs.setAuthenticator(auth);
        DeleteReport dr = jrs.delete(j.getId(), Boolean.FALSE, null, null, null, request);
        assertTrue(dr != null);
        assertTrue(dr.getId() == j.getId());
        assertTrue(dr.isSuccessful() == true);
        assertTrue(dr.getReason(),dr.getReason() == null);
        j = jobDAO.getJobById(j.getId().toString());
        assertTrue(j.isDeleted() == true);
    }

    
    
    //delete permanent
    @Test
    public void testDeleteLogicalWithAllDeletePerm() throws Exception {
        Authenticator auth = mock(Authenticator.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setOwner("bob");
        jobDAO.insert(j, true);
        
        User u = new User();
        u.setLogin("bob");
        u.setPermissions(Permission.DELETE_THEIR_JOBS);
        when(auth.authenticate(request)).thenReturn(u);
        JobRestService jrs = new JobRestService();
        jrs.setAuthenticator(auth);
        DeleteReport dr = jrs.delete(j.getId(), Boolean.TRUE, null, null, null, request);
        assertTrue(dr != null);
        assertTrue(dr.getId() == j.getId());
        assertTrue(dr.isSuccessful() == true);
        assertTrue(dr.getReason(),dr.getReason() == null);
        j = jobDAO.getJobById(j.getId().toString());
        assertTrue(j == null);
    }
    
    //test updateJob null job id
    @Test
    public void testUpdateJobNullJobId() throws Exception {
        Authenticator auth = mock(Authenticator.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        User u = new User();
        when(auth.authenticate(request)).thenReturn(u);
        JobRestService jrs = new JobRestService();
        jrs.setAuthenticator(auth);
        try {
            jrs.updateJob(null, null, Long.MIN_VALUE, Long.MIN_VALUE,
                    Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE, 
                    Long.MIN_VALUE, Boolean.TRUE, null, Boolean.TRUE,
                    null, null, null, null, null, null, request);
            fail("Expected exception");
        }
        catch(WebApplicationException wae){
        }
    }
    
    //test updateJob not authorized
    @Test
    public void testUpdateJobNotAuthorized() throws Exception {
        Authenticator auth = mock(Authenticator.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        User u = new User();
        when(auth.authenticate(request)).thenReturn(u);
        JobRestService jrs = new JobRestService();
        jrs.setAuthenticator(auth);
        try {
            jrs.updateJob(1L, null, Long.MIN_VALUE, Long.MIN_VALUE,
                    Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE, 
                    Long.MIN_VALUE, Boolean.TRUE, null, Boolean.TRUE,
                    null, null, null, null, null, null, request);
            fail("Expected exception");
        }
        catch(WebApplicationException wae){
            assertTrue(wae.getResponse().getStatus() == HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
    
    @Test
    public void testUpdateNotAuthorized() throws Exception {
        Authenticator auth = mock(Authenticator.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        User u = new User();
        when(auth.authenticate(request)).thenReturn(u);
        JobRestService jrs = new JobRestService();
        jrs.setAuthenticator(auth);
        try {
            jrs.update(1L,null, null, null, null, null, request);
            fail("Expected exception");
        }
        catch(WebApplicationException wae){
            assertTrue(wae.getResponse().getStatus() == HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    //test updateJob update job their authorized but job not owned by them
    @Test
    public void testUpdateJobTheirAuthorizedJobNotOwnedByThem() throws Exception {
        Authenticator auth = mock(Authenticator.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        User u = new User();
        u.setPermissions(Permission.UPDATE_THEIR_JOBS);
        u.setLogin("bob");
        when(auth.authenticate(request)).thenReturn(u);
        
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setOwner("joe");
        j = jobDAO.insert(j, true);
        JobRestService jrs = new JobRestService();
        jrs.setAuthenticator(auth);
        try {
        j = jrs.updateJob(j.getId(), "0", 1L, 2L,
                    3L, 4L, 5L, 
                    6L, Boolean.TRUE, "8", Boolean.TRUE,
                    "9", "10",null, null,null, null, request);
        }catch(WebApplicationException wae){
            assertTrue(":"+wae.getMessage()+":",
                    wae.getMessage().contains("Error retrieving Job or not authorized"));
        }
    }
    
     @Test
    public void testUpdateTheirAuthorizedJobNotOwnedByThem() throws Exception {
        Authenticator auth = mock(Authenticator.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        User u = new User();
        u.setPermissions(Permission.UPDATE_THEIR_JOBS);
        u.setLogin("bob");
        when(auth.authenticate(request)).thenReturn(u);
        
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setOwner("joe");
        j = jobDAO.insert(j, true);
        JobRestService jrs = new JobRestService();
        jrs.setAuthenticator(auth);
        try {
        j = jrs.update(j.getId(),j,
                null, null,null, null, request);
        }catch(WebApplicationException wae){
            assertTrue(":"+wae.getMessage()+":",
                    wae.getMessage().contains("Error retrieving Job or not authorized"));
        }
    }
    //test updateJob update job all authorized
    @Test
    public void testUpdateJobAllAuthorized() throws Exception {
        Authenticator auth = mock(Authenticator.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        User u = new User();
        u.setPermissions(Permission.UPDATE_ALL_JOBS);
        when(auth.authenticate(request)).thenReturn(u);
        
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j = jobDAO.insert(j, true);
        JobRestService jrs = new JobRestService();
        jrs.setAuthenticator(auth);
        j = jrs.updateJob(j.getId(), "0", 1L, 2L,
                    3L, 4L, 5L, 
                    6L, Boolean.TRUE, "8", Boolean.TRUE,
                    "9", "10",null, null,null, null, request);
        assertTrue(j.getStatus().equals("0"));
        assertTrue(j.getEstimatedCpuInSeconds() == 1L);
        assertTrue(j.getEstimatedWallTimeInSeconds() == 2L);
        assertTrue(j.getEstimatedDiskInBytes() == 3L);
        assertTrue(j.getSubmitDate().getTime() == 4L);
        assertTrue(j.getStartDate().getTime() == 5L);
        assertTrue(j.getFinishDate().getTime() == 6L);
        assertTrue(j.getHasJobBeenSubmittedToScheduler() == true);
        assertTrue(j.getSchedulerJobId().equals("8"));
        assertTrue(j.isDeleted() == true);
        assertTrue(j.getError().equals("9"));
        assertTrue(j.getDetailedError().equals("10"));
    }
    
    @Test
    public void testUpdateAllAuthorized() throws Exception {
        Authenticator auth = mock(Authenticator.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        User u = new User();
        u.setPermissions(Permission.UPDATE_ALL_JOBS);
        when(auth.authenticate(request)).thenReturn(u);
        
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j = jobDAO.insert(j, true);
        JobRestService jrs = new JobRestService();
        jrs.setAuthenticator(auth);
        j.setStatus("0");
        
        j = jrs.update(j.getId(), j,null, null,null, null, request);
        assertTrue(j.getStatus().equals("0"));
        
    }
 
    @Test
    public void testUpdateTheirAuthorizedAndResaveSetToFalse() throws Exception {
        Authenticator auth = mock(Authenticator.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        User u = new User();
        u.setLogin("bob");
        u.setPermissions(Permission.UPDATE_THEIR_JOBS);
        when(auth.authenticate(request)).thenReturn(u);
        
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setOwner("bob");
        j = jobDAO.insert(j, true);
        JobRestService jrs = new JobRestService();
        jrs.setAuthenticator(auth);
        j.setStatus("0");
        
        j = jrs.update(j.getId(), j,null, null,null, Boolean.FALSE, request);
        assertTrue(j.getStatus().equals("0"));
        
    }
 

    //test updateJob update job their authorized
    @Test
    public void testUpdateJobTheirAuthorizedAndResaveSetToFalse() throws Exception {
        Authenticator auth = mock(Authenticator.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        User u = new User();
        u.setLogin("bob");
        u.setPermissions(Permission.UPDATE_THEIR_JOBS);
        when(auth.authenticate(request)).thenReturn(u);
        
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setOwner("bob");
        j = jobDAO.insert(j, true);
        JobRestService jrs = new JobRestService();
        jrs.setAuthenticator(auth);
        j = jrs.updateJob(j.getId(), "0", 1L, 2L,
                    3L, 4L, 5L, 
                    6L, Boolean.TRUE, "8", Boolean.TRUE,
                    "9", "10",null, null,null, Boolean.FALSE, request);
        assertTrue(j.getStatus().equals("0"));
        assertTrue(j.getEstimatedCpuInSeconds() == 1L);
        assertTrue(j.getEstimatedWallTimeInSeconds() == 2L);
        assertTrue(j.getEstimatedDiskInBytes() == 3L);
        assertTrue(j.getSubmitDate().getTime() == 4L);
        assertTrue(j.getStartDate().getTime() == 5L);
        assertTrue(j.getFinishDate().getTime() == 6L);
        assertTrue(j.getHasJobBeenSubmittedToScheduler() == true);
        assertTrue(j.getSchedulerJobId().equals("8"));
        assertTrue(j.isDeleted() == true);
        assertTrue(j.getError().equals("9"));
        assertTrue(j.getDetailedError().equals("10"));
    }

    //test updateJob resave flag true
    @Test
    public void testUpdateJobTheirAuthorizedResaveSetToTrue() throws Exception {
        Authenticator auth = mock(Authenticator.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        User u = new User();
        u.setLogin("bob");
        u.setLoginToRunJobAs("joe");
        u.setPermissions(Permission.UPDATE_THEIR_JOBS);
        when(auth.authenticate(request)).thenReturn(u);
        
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setOwner("joe");
        j = jobDAO.insert(j, true);
        JobRestService jrs = new JobRestService();
        jrs.setAuthenticator(auth);
        j = jrs.updateJob(j.getId(), "0", 1L, 2L,
                    3L, 4L, 5L, 
                    6L, Boolean.TRUE, "8", Boolean.TRUE,
                    "9", "10",null, null,null, Boolean.TRUE, request);
        assertTrue(j.getStatus() == null);
    }
    
    @Test
    public void testUpdateTheirAuthorizedResaveSetToTrue() throws Exception {
        Authenticator auth = mock(Authenticator.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        User u = new User();
        u.setLogin("bob");
        u.setLoginToRunJobAs("joe");
        u.setPermissions(Permission.UPDATE_THEIR_JOBS);
        when(auth.authenticate(request)).thenReturn(u);
        
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setOwner("joe");
        
        j = jobDAO.insert(j, true);
        JobRestService jrs = new JobRestService();
        jrs.setAuthenticator(auth);
        j.setStatus("hello");
        j = jrs.update(j.getId(), j,
                    null, null,null, Boolean.TRUE, request);
        assertTrue(j.getStatus() == null);
    }
}
