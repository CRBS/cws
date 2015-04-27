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
import edu.ucsd.crbs.cws.dao.WorkspaceFileDAO;
import edu.ucsd.crbs.cws.log.Event;
import edu.ucsd.crbs.cws.log.EventBuilder;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import edu.ucsd.crbs.cws.workflow.report.DeleteReport;
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
import static org.mockito.Mockito.*;



/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestWorkspaceFileRestService {

    public TestWorkspaceFileRestService() {
    }

    @BeforeClass
    public static void setUpClass() {
        Logger.getLogger(WorkspaceFileRestService.class.getName()).setLevel(Level.OFF);
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
    public void testDeleteWorkspaceFileButNotAuthorized() throws Exception {
     
        WorkspaceFileRestService wrs = new WorkspaceFileRestService();
        Authenticator auth = mock(Authenticator.class);
        wrs.setAuthenticator(auth);

        User u = new User();
        u.setPermissions(Permission.NONE);

        
        EventBuilder eventBuilder = mock(EventBuilder.class);
        when(eventBuilder.createEvent(null,u)).thenReturn(new Event());
        wrs.setEventBuilder(eventBuilder);
        
        when(auth.authenticate(null)).thenReturn(u);
        
        try {
            wrs.deleteWorkspaceFile(1L, null, null, null, null, null);
            fail("Expected exception");
        }
        catch(WebApplicationException wae){
            assertTrue(wae.getResponse().getStatus() == HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
    
    @Test
    public void testDeleteWorkspaceFileUserAuthorizedButNoWorkflowFound() throws Exception {
         WorkspaceFileRestService wrs = new WorkspaceFileRestService();
        Authenticator auth = mock(Authenticator.class);
        wrs.setAuthenticator(auth);

        
        User u = new User();
        u.setLogin("bob");
        u.setPermissions(Permission.DELETE_THEIR_WORKSPACEFILES);
        
        EventBuilder eventBuilder = mock(EventBuilder.class);
        when(eventBuilder.createEvent(null,u)).thenReturn(new Event());
        wrs.setEventBuilder(eventBuilder);
        
        WorkspaceFileDAO workspaceFileDAO = mock(WorkspaceFileDAO.class);
        wrs.setWorkspaceFileDAO(workspaceFileDAO);
        
        when(workspaceFileDAO.getWorkspaceFileById("1", u)).thenReturn(null);
        
        when(auth.authenticate(null)).thenReturn(u);
        
        DeleteReport dwr = wrs.deleteWorkspaceFile(1L, null, null, 
                null, null, null);
        assertTrue(dwr != null);
        assertTrue(dwr.getId() == 1L);
        assertTrue(dwr.getReason().equals("WorkspaceFile (1) not found"));
        assertFalse(dwr.isSuccessful());
    }
    
    @Test
    public void testDeleteWorkspaceFileUserAuthorizedToDeleteOwnButOwnerIsNull() throws Exception {
        WorkspaceFileRestService wrs = new WorkspaceFileRestService();
        Authenticator auth = mock(Authenticator.class);
        wrs.setAuthenticator(auth);

        
        User u = new User();
        u.setLogin("bob");
        u.setPermissions(Permission.DELETE_THEIR_WORKSPACEFILES);
        
        EventBuilder eventBuilder = mock(EventBuilder.class);
        when(eventBuilder.createEvent(null,u)).thenReturn(new Event());
        wrs.setEventBuilder(eventBuilder);
        
        WorkspaceFileDAO workspaceFileDAO = mock(WorkspaceFileDAO.class);
        wrs.setWorkspaceFileDAO(workspaceFileDAO);
        
        WorkspaceFile wsf = new WorkspaceFile();
        wsf.setId(1L);
        when(workspaceFileDAO.getWorkspaceFileById("1", u)).thenReturn(wsf);
        
        when(auth.authenticate(null)).thenReturn(u);
        
        DeleteReport dwr = wrs.deleteWorkspaceFile(1L, null, null, 
                null, null, null);
        assertTrue(dwr != null);
        assertTrue(dwr.getId() == 1L);
        assertTrue(dwr.getReason().equals("WorkspaceFile (1) does not have an owner"));
        assertFalse(dwr.isSuccessful());
    }

    @Test
    public void testDeleteWorkspaceFileUserAuthorizedToDeleteOwnButOwnerDoesNotMatch() throws Exception {
        WorkspaceFileRestService wrs = new WorkspaceFileRestService();
        Authenticator auth = mock(Authenticator.class);
        wrs.setAuthenticator(auth);

        
        User u = new User();
        u.setLogin("bob");
        u.setPermissions(Permission.DELETE_THEIR_WORKSPACEFILES);
        
        EventBuilder eventBuilder = mock(EventBuilder.class);
        when(eventBuilder.createEvent(null,u)).thenReturn(new Event());
        wrs.setEventBuilder(eventBuilder);
        
        WorkspaceFileDAO workspaceFileDAO = mock(WorkspaceFileDAO.class);
        wrs.setWorkspaceFileDAO(workspaceFileDAO);
        
        WorkspaceFile wsf = new WorkspaceFile();
        wsf.setId(1L);
        wsf.setOwner("joe");
        when(workspaceFileDAO.getWorkspaceFileById("1", u)).thenReturn(wsf);
        
        when(auth.authenticate(null)).thenReturn(u);
        
        DeleteReport dwr = wrs.deleteWorkspaceFile(1L, null, null, 
                null, null, null);
        assertTrue(dwr != null);
        assertTrue(dwr.getId() == 1L);
        assertTrue(dwr.getReason().equals("bob does not have permission "
                + "to delete WorkspaceFile (1)"));
        assertFalse(dwr.isSuccessful());
    }
    
    @Test
    public void testDeleteWorkspaceFileAllAuthButNoWorkspaceFileFound() throws Exception {
        WorkspaceFileRestService wrs = new WorkspaceFileRestService();
        Authenticator auth = mock(Authenticator.class);
        wrs.setAuthenticator(auth);

        
        User u = new User();
        u.setLogin("bob");
        u.setPermissions(Permission.DELETE_ALL_WORKSPACEFILES);
        
        EventBuilder eventBuilder = mock(EventBuilder.class);
        when(eventBuilder.createEvent(null,u)).thenReturn(new Event());
        wrs.setEventBuilder(eventBuilder);
        
        WorkspaceFileDAO workspaceFileDAO = mock(WorkspaceFileDAO.class);
        wrs.setWorkspaceFileDAO(workspaceFileDAO);
        
        when(workspaceFileDAO.getWorkspaceFileById("1", u)).thenReturn(null);
        
        when(auth.authenticate(null)).thenReturn(u);
        
        DeleteReport dwr = wrs.deleteWorkspaceFile(1L, null, null, 
                null, null, null);
        assertTrue(dwr != null);
        assertTrue(dwr.getId() == 1L);
        assertTrue(dwr.getReason().equals("WorkspaceFile (1) not found"));
        assertFalse(dwr.isSuccessful());
    }
    
    @Test
    public void testDeleteWorkspaceFileSuccessfulLogicalDelete() throws Exception {
        WorkspaceFileRestService wrs = new WorkspaceFileRestService();
        Authenticator auth = mock(Authenticator.class);
        wrs.setAuthenticator(auth);
        
        User u = new User();
        u.setLogin("bob");
        u.setPermissions(Permission.DELETE_ALL_WORKSPACEFILES);
        
        EventBuilder eventBuilder = mock(EventBuilder.class);
        Event event = new Event();
        when(eventBuilder.createEvent(null,u)).thenReturn(event);
        wrs.setEventBuilder(eventBuilder);
        
        
        WorkspaceFileDAO workspaceFileDAO = mock(WorkspaceFileDAO.class);
        wrs.setWorkspaceFileDAO(workspaceFileDAO);
        
        WorkspaceFile wsf = new WorkspaceFile();
        wsf.setId(1L);
        wsf.setOwner("joe");
        when(workspaceFileDAO.getWorkspaceFileById("1", u)).thenReturn(wsf);

        EventDAO eventDAO = mock(EventDAO.class);
        wrs.setEventDAO(eventDAO);
        when(eventBuilder.setAsLogicalDeleteWorkspaceFileEvent(event, wsf)).thenReturn(event);
        
        when(auth.authenticate(null)).thenReturn(u);

        DeleteReportImpl setDwr = new DeleteReportImpl();
        setDwr.setId(1L);
        setDwr.setSuccessful(true);
        when(workspaceFileDAO.delete(1L, null,false)).thenReturn(setDwr);
        
        DeleteReport dwr = wrs.deleteWorkspaceFile(1L, null, null, 
                null, null, null);
        assertTrue(dwr != null);
        assertTrue(dwr.getId() == 1L);
        assertTrue(dwr.getReason() == null);
        assertTrue(dwr.isSuccessful());
        
        verify(workspaceFileDAO).delete(1L, null,false);
        verify(eventBuilder).setAsLogicalDeleteWorkspaceFileEvent(event, wsf);
    }
    
    
    @Test
    public void testDeleteWorkspaceFileSuccessfulLogicalDeleteWithFalseDeleteParam() throws Exception {
        WorkspaceFileRestService wrs = new WorkspaceFileRestService();
        Authenticator auth = mock(Authenticator.class);
        wrs.setAuthenticator(auth);
        
        User u = new User();
        u.setLogin("bob");
        u.setPermissions(Permission.DELETE_ALL_WORKSPACEFILES);
        
        EventBuilder eventBuilder = mock(EventBuilder.class);
        Event event = new Event();
        when(eventBuilder.createEvent(null,u)).thenReturn(event);
        wrs.setEventBuilder(eventBuilder);
        
        
        WorkspaceFileDAO workspaceFileDAO = mock(WorkspaceFileDAO.class);
        wrs.setWorkspaceFileDAO(workspaceFileDAO);
        
        WorkspaceFile wsf = new WorkspaceFile();
        wsf.setId(1L);
        wsf.setOwner("joe");
        when(workspaceFileDAO.getWorkspaceFileById("1", u)).thenReturn(wsf);

        EventDAO eventDAO = mock(EventDAO.class);
        wrs.setEventDAO(eventDAO);
        when(eventBuilder.setAsLogicalDeleteWorkspaceFileEvent(event, wsf)).thenReturn(event);
        
        when(auth.authenticate(null)).thenReturn(u);

        DeleteReportImpl setDwr = new DeleteReportImpl();
        setDwr.setId(1L);
        setDwr.setSuccessful(true);
        when(workspaceFileDAO.delete(1L, Boolean.FALSE,false)).thenReturn(setDwr);
        
        DeleteReport dwr = wrs.deleteWorkspaceFile(1L, Boolean.FALSE, null, 
                null, null, null);
        assertTrue(dwr != null);
        assertTrue(dwr.getId() == 1L);
        assertTrue(dwr.getReason() == null);
        assertTrue(dwr.isSuccessful());
        
        verify(workspaceFileDAO).delete(1L, Boolean.FALSE,false);
        verify(eventBuilder).setAsLogicalDeleteWorkspaceFileEvent(event, wsf);
    }
    
    @Test
    public void testDeleteWorkspaceFileSuccessfulDelete() throws Exception {
        WorkspaceFileRestService wrs = new WorkspaceFileRestService();
        Authenticator auth = mock(Authenticator.class);
        wrs.setAuthenticator(auth);
        
        User u = new User();
        u.setLogin("bob");
        u.setPermissions(Permission.DELETE_ALL_WORKSPACEFILES);
        
        EventBuilder eventBuilder = mock(EventBuilder.class);
        Event event = new Event();
        when(eventBuilder.createEvent(null,u)).thenReturn(event);
        wrs.setEventBuilder(eventBuilder);
        
        
        WorkspaceFileDAO workspaceFileDAO = mock(WorkspaceFileDAO.class);
        wrs.setWorkspaceFileDAO(workspaceFileDAO);
        
        WorkspaceFile wsf = new WorkspaceFile();
        wsf.setId(1L);
        wsf.setOwner("joe");
        when(workspaceFileDAO.getWorkspaceFileById("1", u)).thenReturn(wsf);

        EventDAO eventDAO = mock(EventDAO.class);
        wrs.setEventDAO(eventDAO);
        when(eventBuilder.setAsDeleteWorkspaceFileEvent(event, wsf)).thenReturn(event);
        
        when(auth.authenticate(null)).thenReturn(u);

        DeleteReportImpl setDwr = new DeleteReportImpl();
        setDwr.setId(1L);
        setDwr.setSuccessful(true);
        when(workspaceFileDAO.delete(1L, Boolean.TRUE,false)).thenReturn(setDwr);
        
        DeleteReport dwr = wrs.deleteWorkspaceFile(1L, Boolean.TRUE, null, 
                null, null, null);
        assertTrue(dwr != null);
        assertTrue(dwr.getId() == 1L);
        assertTrue(dwr.getReason() == null);
        assertTrue(dwr.isSuccessful());
        
        verify(workspaceFileDAO).delete(1L, Boolean.TRUE,false);
        verify(eventBuilder).setAsDeleteWorkspaceFileEvent(event, wsf);
    }
    
    
    
}