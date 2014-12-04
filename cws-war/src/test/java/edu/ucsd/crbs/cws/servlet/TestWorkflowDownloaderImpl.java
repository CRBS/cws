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

package edu.ucsd.crbs.cws.servlet;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import edu.ucsd.crbs.cws.dao.WorkflowDAO;
import edu.ucsd.crbs.cws.workflow.Workflow;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
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
public class TestWorkflowDownloaderImpl {

    
     private final LocalServiceTestHelper _helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    
    public TestWorkflowDownloaderImpl() {
    }

    @BeforeClass
    public static void setUpClass() {
        Logger.getLogger(WorkflowDownloaderImpl.class.getName()).setLevel(Level.OFF);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        _helper.setUp();
    }

    @After
    public void tearDown() {
        _helper.tearDown();
    }

    //test workflow id is null
    @Test
    public void testWorkflowIdIsNull(){
        
        try {
            
            HttpServletResponse mockResponse = mock(HttpServletResponse.class);
            
            Downloader downloader = new WorkflowDownloaderImpl();
            
            downloader.send(null, mockResponse);
            verify(mockResponse).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error getting workflow by id from data store: workflow id cannot be null");
        }
        catch(IOException ex){
            fail(ex.getMessage());
            
        }
        
    }
    
    //test response is null
     @Test
    public void testWhereNoWorkflowIsFound(){
        
        try {
            
            HttpServletResponse mockResponse = mock(HttpServletResponse.class);
            
            Downloader downloader = new WorkflowDownloaderImpl();
            downloader.send("12345", mockResponse);
            verify(mockResponse).sendError(HttpServletResponse.SC_NOT_FOUND,
                    "No workflow matching id found: 12345");
        }
        catch(IOException ex){
            fail(ex.getMessage());
            
        }
        
    }
    //test null workflow for id
    @Test
    public void testWhereReturnedWorkflowIsNull(){
        
        try {
            
            HttpServletResponse mockResponse = mock(HttpServletResponse.class);
            WorkflowDAO mockWorkflowDAO = mock(WorkflowDAO.class);
            when(mockWorkflowDAO.getWorkflowById("12345",null)).thenReturn(null);
            
            WorkflowDownloaderImpl downloader = new WorkflowDownloaderImpl();
            downloader._workflowDAO = mockWorkflowDAO;
            downloader.send("12345", mockResponse);
            verify(mockResponse).sendError(HttpServletResponse.SC_NOT_FOUND,
                    "No workflow matching id found: 12345");
        }
        catch(Exception ex){
            fail(ex.getMessage());
            
        }
        
    }
    //test no or null blobkey for workflow
    @Test
    public void testWhereBlobKeyForWorkflowIsNull(){
        
        try {
            
            HttpServletResponse mockResponse = mock(HttpServletResponse.class);
            WorkflowDAO mockWorkflowDAO = mock(WorkflowDAO.class);
            Workflow noBlobWf = new Workflow();
            noBlobWf.setId(12345L);
            when(mockWorkflowDAO.getWorkflowById("12345",null)).thenReturn(noBlobWf);
            WorkflowDownloaderImpl downloader = new WorkflowDownloaderImpl();
            downloader._workflowDAO = mockWorkflowDAO;
            downloader.send("12345", mockResponse);
            verify(mockResponse).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Key to workflow file not found for workflow: 12345");
        }
        catch(Exception ex){
            fail(ex.getMessage());
            
        }
        
    }
    
    //test success full
    @Test
    public void testWhereCallIsSuccessful(){
        
        try {
            
            HttpServletResponse mockResponse = mock(HttpServletResponse.class);
            WorkflowDAO mockWorkflowDAO = mock(WorkflowDAO.class);
            Workflow wf = new Workflow();
            wf.setId(12345L);
            wf.setBlobKey("key");
            when(mockWorkflowDAO.getWorkflowById("12345",null)).thenReturn(wf);
            WorkflowDownloaderImpl downloader = new WorkflowDownloaderImpl();
            downloader._workflowDAO = mockWorkflowDAO;
            downloader.send("12345", mockResponse);
            
            verify(mockResponse).setContentType("application/x-download");
            verify(mockResponse).setHeader("Content-Disposition","attachment; filename=12345.kar");
            
            
        }
        catch(Exception ex){
            fail(ex.getMessage());
            
        }
        
    }

}