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

import edu.ucsd.crbs.cws.auth.Authenticator;
import edu.ucsd.crbs.cws.auth.Permission;
import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.dao.EventDAO;
import edu.ucsd.crbs.cws.dao.WorkflowDAO;
import edu.ucsd.crbs.cws.log.Event;
import edu.ucsd.crbs.cws.log.EventBuilder;
import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.report.DeleteReportImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
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
public class TestWorkflowRestService {

    public TestWorkflowRestService() {
    }

    @BeforeClass
    public static void setUpClass() {
         Logger.getLogger(WorkflowRestService.class.getName()).setLevel(Level.OFF);
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
    public void testDeleteWorkflowButNotAuthorized() throws Exception {
        
        WorkflowRestService wrs = new WorkflowRestService();
        Authenticator auth = mock(Authenticator.class);
        wrs.setAuthenticator(auth);

        User u = new User();
        u.setPermissions(Permission.NONE);

        
        EventBuilder eventBuilder = mock(EventBuilder.class);
        when(eventBuilder.createEvent(null,u)).thenReturn(new Event());
        wrs.setEventBuilder(eventBuilder);
        
        when(auth.authenticate(null)).thenReturn(u);
        
        try {
            wrs.deleteWorkflow(1L, null, null, null, null, null);
            fail("Expected WebApplicationException due to authorization issue");
        }
        catch(WebApplicationException wae){
            assertTrue(wae.getResponse().getStatus() == HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
    
    @Test
    public void testDeleteWorkflowUserAuthorizedToDeleteOwnWorkflowButNoWorkflowFound() throws Exception {
        
        WorkflowRestService wrs = new WorkflowRestService();
        Authenticator auth = mock(Authenticator.class);
        wrs.setAuthenticator(auth);

        User u = new User();
        u.setLogin("bob");
        u.setPermissions(Permission.DELETE_THEIR_WORKFLOWS);
        
        EventBuilder eventBuilder = mock(EventBuilder.class);
        when(eventBuilder.createEvent(null,u)).thenReturn(new Event());
        wrs.setEventBuilder(eventBuilder);
        
        WorkflowDAO workflowDAO = mock(WorkflowDAO.class);
        wrs.setWorkflowDAO(workflowDAO);
        
        when(workflowDAO.getWorkflowById("1", u)).thenReturn(null);
        
        when(auth.authenticate(null)).thenReturn(u);
        
        
        DeleteReportImpl dwr = wrs.deleteWorkflow(1L, null, null, null, null, null);
        assertTrue(dwr != null);
        assertTrue(dwr.getId() == 1L);
        assertTrue(dwr.getReason().equals("Workflow (1) not found"));
        assertFalse(dwr.isSuccessful());
        
    }
    
    @Test
    public void testDeleteWorkflowUserAuthorizedToDeleteOwnWorkflowButWorkflowOwnerIsNull() throws Exception {
        
        WorkflowRestService wrs = new WorkflowRestService();
        Authenticator auth = mock(Authenticator.class);
        wrs.setAuthenticator(auth);

        User u = new User();
        u.setLogin("bob");
        u.setPermissions(Permission.DELETE_THEIR_WORKFLOWS);
        
        EventBuilder eventBuilder = mock(EventBuilder.class);
        when(eventBuilder.createEvent(null,u)).thenReturn(new Event());
        wrs.setEventBuilder(eventBuilder);
        
        WorkflowDAO workflowDAO = mock(WorkflowDAO.class);
        wrs.setWorkflowDAO(workflowDAO);
        Workflow w = new Workflow();
        when(workflowDAO.getWorkflowById("1", u)).thenReturn(w);
        
        when(auth.authenticate(null)).thenReturn(u);
        
        
        DeleteReportImpl dwr = wrs.deleteWorkflow(1L, null, null, null, null, null);
        assertTrue(dwr != null);
        assertTrue(dwr.getId() == 1L);
        assertTrue(dwr.getReason().equals("Workflow (1) does not have owner"));
        assertFalse(dwr.isSuccessful());
    }
    
    @Test
    public void testDeleteWorkflowUserAuthorizedToDeleteOwnWorkflowButWorkflowOwneDoesNotMatch() throws Exception {
        
        WorkflowRestService wrs = new WorkflowRestService();
        Authenticator auth = mock(Authenticator.class);
        wrs.setAuthenticator(auth);

        User u = new User();
        u.setLogin("bob");
        u.setPermissions(Permission.DELETE_THEIR_WORKFLOWS);
        
        EventBuilder eventBuilder = mock(EventBuilder.class);
        when(eventBuilder.createEvent(null,u)).thenReturn(new Event());
        wrs.setEventBuilder(eventBuilder);
        
        WorkflowDAO workflowDAO = mock(WorkflowDAO.class);
        wrs.setWorkflowDAO(workflowDAO);
        Workflow w = new Workflow();
        w.setOwner("joe");
        when(workflowDAO.getWorkflowById("1", u)).thenReturn(w);
        
        when(auth.authenticate(null)).thenReturn(u);
        
        
        DeleteReportImpl dwr = wrs.deleteWorkflow(1L, null, null, null, null, null);
        assertTrue(dwr != null);
        assertTrue(dwr.getId() == 1L);
        assertTrue(dwr.getReason().equals("bob does not have permission to delete Workflow (1)"));
        assertFalse(dwr.isSuccessful());
        
    }
    
    @Test
    public void testDeleteWorkflowAllAuthorizationButNoWorkflowFound() throws Exception {
        
        WorkflowRestService wrs = new WorkflowRestService();
        Authenticator auth = mock(Authenticator.class);
        wrs.setAuthenticator(auth);

        User u = new User();
        u.setLogin("bob");
        u.setPermissions(Permission.DELETE_ALL_WORKFLOWS);
        
        EventBuilder eventBuilder = mock(EventBuilder.class);
        when(eventBuilder.createEvent(null,u)).thenReturn(new Event());
        wrs.setEventBuilder(eventBuilder);
        
        WorkflowDAO workflowDAO = mock(WorkflowDAO.class);
        wrs.setWorkflowDAO(workflowDAO);
        
        when(workflowDAO.getWorkflowById("1", u)).thenReturn(null);
        
        when(auth.authenticate(null)).thenReturn(u);
        
        
        DeleteReportImpl dwr = wrs.deleteWorkflow(1L, null, null, null, null, null);
        assertTrue(dwr != null);
        assertTrue(dwr.getId() == 1L);
        assertTrue(dwr.getReason().equals("Workflow (1) not found"));
        assertFalse(dwr.isSuccessful());
    }
    
    @Test
    public void testDeleteWorkflowSuccessfulLogicalDelete() throws Exception {
        
        WorkflowRestService wrs = new WorkflowRestService();
        Authenticator auth = mock(Authenticator.class);
        wrs.setAuthenticator(auth);

        User u = new User();
        u.setLogin("bob");
        u.setPermissions(Permission.DELETE_ALL_WORKFLOWS);
        
        EventBuilder eventBuilder = mock(EventBuilder.class);
        when(eventBuilder.createEvent(null,u)).thenReturn(new Event());
        wrs.setEventBuilder(eventBuilder);
        
        WorkflowDAO workflowDAO = mock(WorkflowDAO.class);
        wrs.setWorkflowDAO(workflowDAO);
        Workflow w = new Workflow();
        when(workflowDAO.getWorkflowById("1", u)).thenReturn(w);
        DeleteReportImpl deleteResp = new DeleteReportImpl();
        deleteResp.setId(1L);
        deleteResp.setSuccessful(true);
        when(workflowDAO.delete(1L, null)).thenReturn(deleteResp);
        when(auth.authenticate(null)).thenReturn(u);
        
        DeleteReportImpl dwr = wrs.deleteWorkflow(1L, null, null, null, null, null);
        assertTrue(dwr != null);
        assertTrue(dwr.getId() == 1L);
        assertTrue(dwr.getReason() == null);
        assertTrue(dwr.isSuccessful());
        
        verify(workflowDAO).delete(1L, null);
    }
    
    @Test
    public void testDeleteWorkflowSuccessfulLogicalDeleteWithFalseDeleteParam() throws Exception {
        
        WorkflowRestService wrs = new WorkflowRestService();
        Authenticator auth = mock(Authenticator.class);
        wrs.setAuthenticator(auth);

        User u = new User();
        u.setLogin("bob");
        u.setPermissions(Permission.DELETE_ALL_WORKFLOWS);
        
        EventBuilder eventBuilder = mock(EventBuilder.class);
        Event event = new Event();
        when(eventBuilder.createEvent(null,u)).thenReturn(event);
        wrs.setEventBuilder(eventBuilder);
        
        WorkflowDAO workflowDAO = mock(WorkflowDAO.class);
        wrs.setWorkflowDAO(workflowDAO);
        Workflow w = new Workflow();
        when(workflowDAO.getWorkflowById("1", u)).thenReturn(w);
        DeleteReportImpl deleteResp = new DeleteReportImpl();
        deleteResp.setSuccessful(true);
        deleteResp.setId(1L);
        when(workflowDAO.delete(1L, Boolean.FALSE)).thenReturn(deleteResp);
        when(auth.authenticate(null)).thenReturn(u);
        
        EventDAO eventDAO = mock(EventDAO.class);
        wrs.setEventDAO(eventDAO);
        when(eventBuilder.setAsLogicalDeleteWorkflowEvent(event, w)).thenReturn(event);

        
        DeleteReportImpl dwr = wrs.deleteWorkflow(1L, Boolean.FALSE, null, null, null, null);
        assertTrue(dwr != null);
        assertTrue(dwr.getId() == 1L);
        assertTrue(dwr.getReason() == null);
        assertTrue(dwr.isSuccessful());
        
        verify(workflowDAO).delete(1L, Boolean.FALSE);
        verify(eventBuilder).setAsLogicalDeleteWorkflowEvent(event, w);

    }
    
    @Test
    public void testDeleteWorkflowSuccessfulLogicalDeleteWithTrueDeleteParam() throws Exception {
        
        WorkflowRestService wrs = new WorkflowRestService();
        Authenticator auth = mock(Authenticator.class);
        wrs.setAuthenticator(auth);

        User u = new User();
        u.setLogin("bob");
        u.setPermissions(Permission.DELETE_ALL_WORKFLOWS);
        
        EventBuilder eventBuilder = mock(EventBuilder.class);
        Event event = new Event();
        when(eventBuilder.createEvent(null,u)).thenReturn(event);
        wrs.setEventBuilder(eventBuilder);
        
        
        WorkflowDAO workflowDAO = mock(WorkflowDAO.class);
        wrs.setWorkflowDAO(workflowDAO);
        Workflow w = new Workflow();
        when(workflowDAO.getWorkflowById("1", u)).thenReturn(w);
        
        DeleteReportImpl deleteResp = new DeleteReportImpl();
        deleteResp.setSuccessful(true);
        deleteResp.setId(1L);
        when(workflowDAO.delete(1L, Boolean.TRUE)).thenReturn(deleteResp);
        
        when(auth.authenticate(null)).thenReturn(u);
        
        EventDAO eventDAO = mock(EventDAO.class);
        wrs.setEventDAO(eventDAO);
        when(eventBuilder.setAsDeleteWorkflowEvent(event, w)).thenReturn(event);
        
        DeleteReportImpl dwr = wrs.deleteWorkflow(1L, Boolean.TRUE, null, null, null, null);
        assertTrue(dwr != null);
        assertTrue(dwr.getId() == 1L);
        assertTrue(dwr.getReason(),dwr.getReason() == null);
        assertTrue(dwr.isSuccessful());
        
        verify(workflowDAO).delete(1L, Boolean.TRUE);
        verify(eventBuilder).setAsDeleteWorkflowEvent(event, w);
    }

    
}