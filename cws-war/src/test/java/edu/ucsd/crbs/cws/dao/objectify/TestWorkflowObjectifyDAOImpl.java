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

import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import static edu.ucsd.crbs.cws.dao.objectify.OfyService.ofy;

import edu.ucsd.crbs.cws.dao.JobDAO;
import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.report.DeleteReportImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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
public class TestWorkflowObjectifyDAOImpl {

      private final LocalServiceTestHelper _helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(),
        new LocalBlobstoreServiceTestConfig());

    
    public TestWorkflowObjectifyDAOImpl() {
    }

    @BeforeClass
    public static void setUpClass() {
        Logger.getLogger(WorkflowObjectifyDAOImpl.class.getName()).setLevel(Level.OFF);
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
    public void testDeleteWhereWorkflowHasJobsRunWithIt() throws Exception{
        JobDAO jobDAO = mock(JobDAO.class);
        when(jobDAO.getJobsWithWorkflowIdCount(1L)).thenReturn(1);
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(jobDAO);
        
        DeleteReportImpl dwr = workflowDAO.delete(1L, null);
        assertFalse(dwr.isSuccessful());
        assertTrue(dwr.getId() == 1L);
        assertTrue(dwr.getReason().equals("Cannot delete 1 job(s) have been run under workflow"));
        verify(jobDAO).getJobsWithWorkflowIdCount(1L);
    }

    @Test
    public void testDeleteLogicalWhereUpdateThrowsException() throws Exception {
        JobDAO jobDAO = mock(JobDAO.class);
        when(jobDAO.getJobsWithWorkflowIdCount(1L)).thenReturn(0);
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(jobDAO);
        
        try {
            DeleteReportImpl dwr = workflowDAO.delete(1L, null);
            fail("Expected exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().equals("There was a problem updating the workflow"));
        }
    }
    
    private void deleteLogicalCheck(Boolean deleteParam) throws Exception {
        JobDAO jobDAO = mock(JobDAO.class);
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(jobDAO);
        
        Workflow w = new Workflow();
        w.setName("workflow");
        w.setOwner("bob");
        w = workflowDAO.insert(w);
        assertTrue(w.isDeleted() == false);
        
        when(jobDAO.getJobsWithWorkflowIdCount(w.getId())).thenReturn(0);
        
        DeleteReportImpl dwr = workflowDAO.delete(w.getId(), deleteParam);
        assertTrue(dwr != null);
        assertTrue(dwr.getId() == w.getId());
        assertTrue(dwr.isSuccessful());
        assertTrue(dwr.getReason() == null);
        
        w = workflowDAO.getWorkflowById(w.getId().toString(), null);
        assertTrue(w.isDeleted());
    }
    
    @Test
    public void testDeleteLogicalWithNullForDeleteParam() throws Exception {
        deleteLogicalCheck(null);
    }
    
    @Test
    public void testDeleteLogicalWithFalseForDeleteParam() throws Exception {
        deleteLogicalCheck(Boolean.FALSE);
    }
    
    @Test
    public void testDeleteWhereWorkflowIsNotFound() throws Exception {
        JobDAO jobDAO = mock(JobDAO.class);
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(jobDAO);
        when(jobDAO.getJobsWithWorkflowIdCount(1L)).thenReturn(0);
        
        Workflow w = workflowDAO.getWorkflowById(Long.toString(1), null);
        assertTrue(w == null);
        
        DeleteReportImpl dwr = workflowDAO.delete(1L,Boolean.TRUE);
        assertTrue(dwr != null);
        assertTrue(dwr.getId() == 1L);
        assertTrue(dwr.isSuccessful() == false);
        assertTrue(dwr.getReason().equals("No workflow found"));
        
    }
    
    @Test
    public void testDeleteWhereBlobKeyIsNull() throws Exception {
        JobDAO jobDAO = mock(JobDAO.class);
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(jobDAO);
        
        Workflow w = new Workflow();
        w.setName("workflow");
        w.setOwner("bob");
        w = workflowDAO.insert(w);
        assertTrue(w.isDeleted() == false);
        
        when(jobDAO.getJobsWithWorkflowIdCount(w.getId())).thenReturn(0);
        
        DeleteReportImpl dwr = workflowDAO.delete(w.getId(),Boolean.TRUE);
        assertTrue(dwr != null);
        assertTrue(dwr.isSuccessful());
        assertTrue(dwr.getReason() == null);
        assertTrue(dwr.getId() == w.getId());
        
        w = workflowDAO.getWorkflowById(w.getId().toString(), null);
        assertTrue(w == null);
    }
    

    @Test
    public void testDeleteWhereBlobKeyExistsButNoEntryNotFoundInBlobStore() throws Exception {
        JobDAO jobDAO = mock(JobDAO.class);
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(jobDAO);
        
        Workflow w = new Workflow();
        w.setName("workflow");
        w.setOwner("bob");
        w.setBlobKey("asdfasdf");
        w = workflowDAO.insert(w);
        assertTrue(w.isDeleted() == false);
        
        when(jobDAO.getJobsWithWorkflowIdCount(w.getId())).thenReturn(0);
        
        DeleteReportImpl dwr = workflowDAO.delete(w.getId(),Boolean.TRUE);
        assertTrue(dwr != null);
        assertTrue(dwr.isSuccessful());
        assertTrue(dwr.getReason() == null);
        assertTrue(dwr.getId() == w.getId());

        
        w = workflowDAO.getWorkflowById(w.getId().toString(), null);
        assertTrue(w == null);
    }

    
    
    @Test
    public void testUpdateDeleted() throws Exception {
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(null);
        
        try {
            assertTrue(workflowDAO.updateDeleted(1L, true) == null);
            fail("Expected exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().equals("There was a problem updating the workflow"));
        }
        
        Workflow w = new Workflow();
        w.setName("workflow");
        w.setOwner("bob");
        
        w = workflowDAO.insert(w);
        assertTrue(w.isDeleted() == false);
        
        w = workflowDAO.updateDeleted(w.getId(), true);
        assertTrue(w.isDeleted() == true);
        
        w = workflowDAO.updateDeleted(w.getId(), false);
        assertTrue(w.isDeleted() == false);
    }
    
}