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

import edu.ucsd.crbs.cws.dao.JobDAO;
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
    
    //test insert null workflow
    @Test
    public void testInsertNullWorkflow() throws Exception {
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(null);
        try {
            workflowDAO.insert(null);
            fail("Expected exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().equals("Workflow object passed in is null"));
        }
    }
    
    @Test
    public void testInsertNullNameWorkflow() throws Exception {
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(null);
        try {
            workflowDAO.insert(new Workflow());
            fail("Expected exception");
        }
        catch(NullPointerException npe){
            assertTrue(npe.getMessage().equals("Workflow name cannot be null"));
        }
    }
    
    //test insert create date set on workflow with no ancestors and version unset
    @Test
    public void testInsertWithCreateDateSet() throws Exception {
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(null);
        Workflow w = new Workflow();
        Date cDate = new Date();
        w.setCreateDate(cDate);
        w.setName("foo");
        assertTrue(w.getVersion() == 0);
        w = workflowDAO.insert(w);
        assertTrue(w.getId() != null);
        assertTrue(w.getCreateDate().getTime() == cDate.getTime());
        assertTrue(w.getVersion() == 1);
        
    }
    
    //test insert create date not set on workflow with no ancestors
    @Test
    public void testInsertWithCreateDateNotSet() throws Exception {
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(null);
        Workflow w = new Workflow();
        w.setName("foo");
        assertTrue(w.getCreateDate() == null);
        w = workflowDAO.insert(w);
        assertTrue(w.getId() != null);
        assertTrue(w.getCreateDate() != null);
    }
    
    //test insert version set negative and no ancestors
    @Test
    public void testInsertWithVersionSetNegative() throws Exception {
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(null);
        Workflow w = new Workflow();
        Workflow gee = new Workflow();
        gee.setName("joey");
        workflowDAO.insert(gee);
        w.setName("foo");
        w.setParentWorkflow(gee);
        w.setVersion(-1);
        assertTrue(w.getCreateDate() == null);
        w = workflowDAO.insert(w);
        assertTrue(w.getId() != null);
        assertTrue(w.getVersion() == 1);
        assertTrue(w.getParentWorkflow() == null);
    }
    
    //test insert and 1 ancestor workflow with max version 1
    @Test
    public void testInsertWithOneAncestor() throws Exception {
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(null);
        Workflow baseWf = new Workflow();
        baseWf.setName("foo");
        baseWf = workflowDAO.insert(baseWf);
        Workflow w = new Workflow();
        w.setName(baseWf.getName());
        w = workflowDAO.insert(w);
        assertTrue(w.getId() != null);
        assertTrue(w.getVersion() == 2);
    }

    
    //test insert and 3 ancestor workflows with max version 3
    @Test
    public void testInsertWithThreeAncestors() throws Exception {
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(null);
        Workflow baseWf = new Workflow();
        baseWf.setName("foo");
        baseWf = workflowDAO.insert(baseWf);
        baseWf = workflowDAO.insert(baseWf);
        baseWf = workflowDAO.insert(baseWf);
        
        Workflow w = new Workflow();
        w.setName(baseWf.getName());
        w = workflowDAO.insert(w);
        assertTrue(w.getId() != null);
        assertTrue(w.getVersion() == 4);
        assertTrue(w.getParentWorkflow().getId() == baseWf.getId().longValue());
    }
    
    //test getAllWorkflows no workflows found
    @Test
    public void testGetAllWorkflowsNoWorkflowsFound() throws Exception {
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(null);
        List<Workflow> wfList = workflowDAO.getAllWorkflows(false, null);
        assertTrue(wfList.isEmpty());
        
    }
    
    //test getAllWorkflows showDeleted with different values
    @Test
    public void testGetAllWorkflowsNoWorkflowsshowDeletedNull() throws Exception {
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(null);
        Workflow w = new Workflow();
        w.setName("bob");
        w.setDeleted(true);
        workflowDAO.insert(w);
        w = new Workflow();
        w.setName("joe");
        w.setDeleted(false);
        workflowDAO.insert(w);
        
        List<Workflow> wfList = workflowDAO.getAllWorkflows(false, null);
        assertTrue(wfList.size() == 1);
        assertTrue(wfList.get(0).getName().equals("joe"));
        
        wfList = workflowDAO.getAllWorkflows(false, Boolean.FALSE);
        assertTrue(wfList.size() == 1);
        assertTrue(wfList.get(0).getName().equals("joe"));
        
        wfList = workflowDAO.getAllWorkflows(false, Boolean.TRUE);
        assertTrue(wfList.size() == 2);
    }
    
    //test getAllWorkflows omitWorkflowParams true
    @Test
    public void testGetAllWorkflowsOmittedParams() throws Exception {
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(null);
        Workflow w = new Workflow();
       
        w.setName("bob");
        ArrayList<WorkflowParameter> wParams = new ArrayList<WorkflowParameter>();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setName("yo");
        w.setParameters(wParams);
        workflowDAO.insert(w);
        
        List<Workflow> wfList = workflowDAO.getAllWorkflows(false, null);
        assertTrue(wfList.size() == 1);
        assertTrue(wfList.get(0).getName().equals("bob"));
        assertTrue(wfList.get(0).getParameters() != null);
        
        wfList = workflowDAO.getAllWorkflows(true, null);
        assertTrue(wfList.size() == 1);
        assertTrue(wfList.get(0).getName().equals("bob"));
        assertTrue(wfList.get(0).getParameters() == null);
    }
    
    
    
   

    @Test
    public void testDeleteWhereWorkflowHasJobsRunWithIt() throws Exception{
        JobDAO jobDAO = mock(JobDAO.class);
        when(jobDAO.getJobsWithWorkflowIdCount(1L)).thenReturn(1);
        WorkflowObjectifyDAOImpl workflowDAO = new WorkflowObjectifyDAOImpl(jobDAO);
        
        DeleteReport dwr = workflowDAO.delete(1L, null);
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
            DeleteReport dwr = workflowDAO.delete(1L, null);
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
        
        DeleteReport dwr = workflowDAO.delete(w.getId(), deleteParam);
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
        
        DeleteReport dwr = workflowDAO.delete(1L,Boolean.TRUE);
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
        
        DeleteReport dwr = workflowDAO.delete(w.getId(),Boolean.TRUE);
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
        
        DeleteReport dwr = workflowDAO.delete(w.getId(),Boolean.TRUE);
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
            assertTrue(workflowDAO.updateDeletedAndVersion(1L, true,
                    null) == null);
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
        assertTrue(w.getVersion() == 1);
        w = workflowDAO.updateDeletedAndVersion(w.getId(), true,null);
        assertTrue(w.isDeleted() == true);
        assertTrue(w.getVersion() == 1);
        
        w = workflowDAO.updateDeletedAndVersion(w.getId(), false,2);
        assertTrue(w.isDeleted() == false);
        assertTrue(w.getVersion() == 2);
        
        w = workflowDAO.updateDeletedAndVersion(w.getId(), null,null);
        assertTrue(w.isDeleted() == false);
        assertTrue(w.getVersion() == 2);
        
    }
    
}