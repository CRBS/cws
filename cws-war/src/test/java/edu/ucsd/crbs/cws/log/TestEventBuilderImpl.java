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

package edu.ucsd.crbs.cws.log;

import edu.ucsd.crbs.cws.auth.Permission;
import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
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
public class TestEventBuilderImpl {

    public TestEventBuilderImpl() {
    }

    @BeforeClass
    public static void setUpClass() {
        Logger.getLogger(EventBuilderImpl.class.getName()).setLevel(Level.OFF);
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
    public void testCreateEventNullUserAndNullRequest(){
        EventBuilderImpl builder = new EventBuilderImpl();
        
        Event e = builder.createEvent(null, null);
        assertTrue(e != null);
    }
    
    @Test
    public void testCreateEventNullUserAndEmptyRequest(){
        EventBuilderImpl builder = new EventBuilderImpl();
        
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        
        Event e = builder.createEvent(mockRequest, null);
        verify(mockRequest,times(6)).getHeader(anyString());
        assertTrue(e != null);
    }
    
    @Test
    public void testCreateEventWithFilledRequestAndUser(){
        EventBuilderImpl builder = new EventBuilderImpl();

        User u = new User();
        u.setIpAddress("ip");
        u.setId(new Long(1));
        
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader(EventBuilderImpl.USER_AGENT_HEADER)).thenReturn("agent");
        when(mockRequest.getHeader(EventBuilderImpl.HOST_HEADER)).thenReturn("host");
        when(mockRequest.getHeader(EventBuilderImpl.CITY_HEADER)).thenReturn("city");
        when(mockRequest.getHeader(EventBuilderImpl.REGION_HEADER)).thenReturn("region");
        when(mockRequest.getHeader(EventBuilderImpl.COUNTRY_HEADER)).thenReturn("country");
        when(mockRequest.getHeader(EventBuilderImpl.CITY_LAT_LONG_HEADER)).thenReturn("citylatlong");
        
        Event e = builder.createEvent(mockRequest, u);
        assertTrue(e != null);
        assertTrue(e.getUserAgent().equals("agent"));
        assertTrue(e.getCity().equals("city"));
        assertTrue(e.getCityLatLong().equals("citylatlong"));
        assertTrue(e.getCountry().equals("country"));
        assertTrue(e.getHost().equals("host"));
        assertTrue(e.getIpAddress().equals("ip"));
        assertTrue(e.getRegion().equals("region"));
        assertTrue(e.getUserId() == 1L);
    }

    @Test
    public void testSetAsCreateTaskEvent(){
        EventBuilderImpl builder = new EventBuilderImpl();
        assertTrue(builder.setAsCreateJobEvent(null, null) == null);
        
        Event e = new Event();
        assertTrue(builder.setAsCreateJobEvent(e, null) == null);
        
        Job t = new Job();
        assertTrue(builder.setAsCreateJobEvent(null, t) == null);
        
        Event resEvent = builder.setAsCreateJobEvent(e, t);
        assertTrue(resEvent != null);
        assertTrue(resEvent.getEventType().equals(Event.CREATE_JOB_EVENT_TYPE));
        assertTrue(resEvent.getDate() == null);
        assertTrue(resEvent.getJobId() == null);
        
        t.setId(new Long(2));
        Date curDate = new Date();
        t.setCreateDate(curDate);
        resEvent = builder.setAsCreateJobEvent(e, t);
        assertTrue(resEvent != null);
        assertTrue(resEvent.getEventType().equals(Event.CREATE_JOB_EVENT_TYPE));
        assertTrue(resEvent.getDate().compareTo(curDate) == 0);
        assertTrue(resEvent.getJobId() == 2L);
    }
    
    @Test
    public void testSetAsCreateWorkflowEvent(){
        EventBuilderImpl builder = new EventBuilderImpl();
        assertTrue(builder.setAsCreateWorkflowEvent(null, null) == null);
        
        Event e = new Event();
        assertTrue(builder.setAsCreateWorkflowEvent(e, null) == null);
        
        Workflow w = new Workflow();
        assertTrue(builder.setAsCreateWorkflowEvent(null, w) == null);
        
        Event resEvent = builder.setAsCreateWorkflowEvent(e, w);
        assertTrue(resEvent != null);
        assertTrue(resEvent.getEventType().equals(Event.CREATE_WORKFLOW_EVENT_TYPE));
        assertTrue(resEvent.getDate() == null);
        assertTrue(resEvent.getWorkflowId() == null);
        
        w.setId(new Long(2));
        Date curDate = new Date();
        w.setCreateDate(curDate);
        resEvent = builder.setAsCreateWorkflowEvent(e, w);
        assertTrue(resEvent != null);
        assertTrue(resEvent.getEventType().equals(Event.CREATE_WORKFLOW_EVENT_TYPE));
        assertTrue(resEvent.getDate().compareTo(curDate) == 0);
        assertTrue(resEvent.getWorkflowId() == 2L);
    }
    
    @Test
    public void testSetAsCreateWorkspaceFileEvent(){
        EventBuilderImpl builder = new EventBuilderImpl();
        assertTrue(builder.setAsCreateWorkspaceFileEvent(null, null) == null);
        
        Event e = new Event();
        assertTrue(builder.setAsCreateWorkspaceFileEvent(e, null)  == null);
        WorkspaceFile wsf = new WorkspaceFile();
        Event resEvent = builder.setAsCreateWorkspaceFileEvent(e, wsf);
        assertTrue(resEvent != null);
        assertTrue(resEvent.getEventType().equals(Event.CREATE_WORKSPACEFILE_EVENT_TYPE));
        assertTrue(resEvent.getDate() == null);
        assertTrue(resEvent.getMessage() == null);
        assertTrue(resEvent.getWorkspaceFileId() == null);
        
        Date curDate = new Date();

        wsf.setCreateDate(curDate);
        wsf.setId(new Long(1));
        resEvent = builder.setAsCreateWorkspaceFileEvent(e, wsf);
        assertTrue(resEvent != null);
        assertTrue(resEvent.getEventType().equals(Event.CREATE_WORKSPACEFILE_EVENT_TYPE));
        assertTrue(resEvent.getDate().equals(curDate));
        assertTrue(resEvent.getMessage() == null);
        assertTrue(resEvent.getWorkspaceFileId() == 1);
        
    }
    
    
    @Test
    public void testSetAsFailedCreateTaskEvent(){
        EventBuilderImpl builder = new EventBuilderImpl();
        assertTrue(builder.setAsFailedCreateJobEvent(null, null) == null);
        Event e = new Event();
        assertTrue(builder.setAsFailedCreateJobEvent(e, null) == null);
        
        Job t = new Job();
        Event resEvent = builder.setAsFailedCreateJobEvent(e, t);
        assertTrue(resEvent != null);
        assertTrue(resEvent.getEventType().equals(Event.FAILED_CREATE_JOB_EVENT_TYPE));
        assertTrue(resEvent.getDate() != null);
        assertTrue(resEvent.getMessage().equals(""));
        
        t.setError("someerror");
        resEvent = builder.setAsFailedCreateJobEvent(e, t);
        assertTrue(resEvent != null);
        assertTrue(resEvent.getEventType().equals(Event.FAILED_CREATE_JOB_EVENT_TYPE));
        assertTrue(resEvent.getDate() != null);
        assertTrue(resEvent.getMessage().startsWith("JobError: someerror"));
        
    }
    
    @Test
    public void testSetAsCreateUserEvent(){
        EventBuilderImpl builder = new EventBuilderImpl();
        assertTrue(builder.setAsCreateUserEvent(null, null) == null);
        Event e = new Event();
        assertTrue(builder.setAsCreateUserEvent(e, null) == null);
        User u = new User();
        u.setId(new Long(1));
        Event resEvent = builder.setAsCreateUserEvent(e, u);
        assertTrue(resEvent != null);
        assertTrue(resEvent.getCreatedUserId() == 1);
        assertTrue(resEvent.getDate() == null);
        assertTrue(resEvent.getMessage().equals("perms(0)"));
        Date curDate = new Date();
        u.setCreateDate(curDate);
        u.setPermissions(Permission.CREATE_JOB);
        resEvent = builder.setAsCreateUserEvent(e, u);
        assertTrue(resEvent != null);
        assertTrue(resEvent.getCreatedUserId() == 1);
        assertTrue(resEvent.getDate().equals(curDate));
        assertTrue(resEvent.getMessage().equals("perms(16)"));
    }
    
    @Test
    public void testSetAsLogicalDeleteWorkflowEvent(){
        EventBuilderImpl builder = new EventBuilderImpl();
        assertTrue(builder.setAsLogicalDeleteWorkflowEvent(null, null) == null);
        
        Event e = new Event();
        assertTrue(builder.setAsLogicalDeleteWorkflowEvent(e, null) == null);
        
        Workflow w = new Workflow();
        assertTrue(builder.setAsLogicalDeleteWorkflowEvent(null, w) == null);
        
        Event resEvent = builder.setAsLogicalDeleteWorkflowEvent(e, w);
        assertTrue(resEvent != null);
        assertTrue(resEvent.getEventType().equals(Event.LOGICAL_DELETE_WORKFLOW_EVENT_TYPE));
        assertTrue(resEvent.getDate() != null);
        assertTrue(resEvent.getWorkflowId() == null);
        
        w.setId(new Long(2));
        resEvent = builder.setAsLogicalDeleteWorkflowEvent(e, w);
        assertTrue(resEvent != null);
        assertTrue(resEvent.getEventType().equals(Event.LOGICAL_DELETE_WORKFLOW_EVENT_TYPE));
        assertTrue(resEvent.getDate() != null);
        assertTrue(resEvent.getWorkflowId() == 2L);
    }

    
    @Test
    public void testSetAsDeleteWorkflowEvent(){
        EventBuilderImpl builder = new EventBuilderImpl();
        assertTrue(builder.setAsDeleteWorkflowEvent(null, null) == null);
        
        Event e = new Event();
        assertTrue(builder.setAsDeleteWorkflowEvent(e, null) == null);
        
        Workflow w = new Workflow();
        assertTrue(builder.setAsDeleteWorkflowEvent(null, w) == null);
        
        Event resEvent = builder.setAsDeleteWorkflowEvent(e, w);
        assertTrue(resEvent != null);
        assertTrue(resEvent.getEventType().equals(Event.DELETE_WORKFLOW_EVENT_TYPE));
        assertTrue(resEvent.getDate() != null);
        assertTrue(resEvent.getWorkflowId() == null);
        assertTrue(resEvent.getMessage().equals("Name=null,Version=0,CreateDate=null"));
        
        w.setId(new Long(2));
        w.setVersion(2);
        Date curDate = new Date();
        w.setCreateDate(curDate);
        w.setName("bob");
        resEvent = builder.setAsDeleteWorkflowEvent(e, w);
        assertTrue(resEvent != null);
        assertTrue(resEvent.getEventType().equals(Event.DELETE_WORKFLOW_EVENT_TYPE));
        assertTrue(resEvent.getDate() != null);
        assertTrue(resEvent.getWorkflowId() == 2L);
        assertTrue(resEvent.getMessage().equals("Name=bob,Version=2,CreateDate="+curDate.toString()));
        
        
    }

    
    @Test
    public void testSetAsLogicalDeleteWorkspaceFileEvent(){
        EventBuilderImpl builder = new EventBuilderImpl();
        assertTrue(builder.setAsLogicalDeleteWorkspaceFileEvent(null, null) == null);
        
        Event e = new Event();
        assertTrue(builder.setAsLogicalDeleteWorkspaceFileEvent(e, null) == null);

        WorkspaceFile wsf = new WorkspaceFile();
        assertTrue(builder.setAsLogicalDeleteWorkspaceFileEvent(null, wsf) == null);
        
        Event resEvent = builder.setAsLogicalDeleteWorkspaceFileEvent(e, wsf);
        assertTrue(resEvent != null);
        assertTrue(resEvent.getEventType().equals(Event.LOGICAL_DELETE_WORKSPACEFILE_EVENT_TYPE));
        assertTrue(resEvent.getDate() != null);
        assertTrue(resEvent.getWorkspaceFileId() == null);
        
        wsf.setId(2L);
        resEvent = builder.setAsLogicalDeleteWorkspaceFileEvent(e, wsf);
        assertTrue(resEvent != null);
        assertTrue(resEvent.getEventType().equals(Event.LOGICAL_DELETE_WORKSPACEFILE_EVENT_TYPE));
        assertTrue(resEvent.getDate() != null);
        assertTrue(resEvent.getWorkspaceFileId() == 2L);
    }
    
    @Test
    public void testSetAsDeleteWorkspaceFileEvent(){
        EventBuilderImpl builder = new EventBuilderImpl();
        assertTrue(builder.setAsDeleteWorkspaceFileEvent(null, null) == null);
        
        Event e = new Event();
        assertTrue(builder.setAsDeleteWorkspaceFileEvent(e, null) == null);

        WorkspaceFile wsf = new WorkspaceFile();
        assertTrue(builder.setAsDeleteWorkspaceFileEvent(null, wsf) == null);
        
        Event resEvent = builder.setAsDeleteWorkspaceFileEvent(e, wsf);
        assertTrue(resEvent != null);
        assertTrue(resEvent.getEventType().equals(Event.DELETE_WORKSPACEFILE_EVENT_TYPE));
        assertTrue(resEvent.getDate() != null);
        assertTrue(resEvent.getWorkspaceFileId() == null);
        assertTrue(resEvent.getMessage().equals("Name=null,CreateDate=null,Path=null"));
        
        wsf.setId(2L);
        Date curDate = new Date();
        wsf.setCreateDate(curDate);
        wsf.setName("bob");
        wsf.setPath("thepath");
        
        resEvent = builder.setAsDeleteWorkspaceFileEvent(e, wsf);
        assertTrue(resEvent != null);
        assertTrue(resEvent.getEventType().equals(Event.DELETE_WORKSPACEFILE_EVENT_TYPE));
        assertTrue(resEvent.getDate() != null);
        assertTrue(resEvent.getWorkspaceFileId() == 2L);
        assertTrue(resEvent.getMessage().equals("Name=bob,CreateDate="+
                curDate.toString()+",Path=thepath"));
    }

    
    @Test
    public void testSetAsLogicalDeleteJobEvent(){
        EventBuilderImpl builder = new EventBuilderImpl();
        assertTrue(builder.setAsLogicalDeleteJobEvent(null, null) == null);
        
        Event e = new Event();
        assertTrue(builder.setAsLogicalDeleteJobEvent(e, null) == null);

        Job job = new Job();
        assertTrue(builder.setAsLogicalDeleteJobEvent(null, job) == null);
        
        Event resEvent = builder.setAsLogicalDeleteJobEvent(e, job);
        assertTrue(resEvent != null);
        assertTrue(resEvent.getEventType().equals(Event.LOGICAL_DELETE_JOB_EVENT_TYPE));
        assertTrue(resEvent.getDate() != null);
        assertTrue(resEvent.getWorkspaceFileId() == null);
        
        job.setId(2L);
        resEvent = builder.setAsLogicalDeleteJobEvent(e, job);
        assertTrue(resEvent != null);
        assertTrue(resEvent.getEventType().equals(Event.LOGICAL_DELETE_JOB_EVENT_TYPE));
        assertTrue(resEvent.getDate() != null);
        assertTrue(resEvent.getJobId() == 2L);
    }
    
    @Test
    public void testSetAsDeleteJobEvent(){
        EventBuilderImpl builder = new EventBuilderImpl();
        assertTrue(builder.setAsDeleteJobEvent(null, null) == null);
        
        Event e = new Event();
        assertTrue(builder.setAsDeleteJobEvent(e, null) == null);

        Job job = new Job();
        assertTrue(builder.setAsDeleteJobEvent(null, job) == null);
        
        Event resEvent = builder.setAsDeleteJobEvent(e, job);
        assertTrue(resEvent != null);
        assertTrue(resEvent.getEventType().equals(Event.DELETE_JOB_EVENT_TYPE));
        assertTrue(resEvent.getDate() != null);
        assertTrue(resEvent.getJobId() == null);
        assertTrue(resEvent.getMessage().equals("Name=null,CreateDate=null"));
        
        job.setId(2L);
        Date curDate = new Date();
        job.setCreateDate(curDate);
        job.setName("bob");
        
        resEvent = builder.setAsDeleteJobEvent(e, job);
        assertTrue(resEvent != null);
        assertTrue(resEvent.getEventType().equals(Event.DELETE_JOB_EVENT_TYPE));
        assertTrue(resEvent.getDate() != null);
        assertTrue(resEvent.getJobId()== 2L);
        assertTrue(resEvent.getMessage().equals("Name=bob,CreateDate="+
                curDate.toString()));
    }
}
