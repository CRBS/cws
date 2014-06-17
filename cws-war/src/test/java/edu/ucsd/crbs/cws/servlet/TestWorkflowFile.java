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
import edu.ucsd.crbs.cws.auth.Authenticator;
import edu.ucsd.crbs.cws.auth.Permission;
import edu.ucsd.crbs.cws.auth.User;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
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
public class TestWorkflowFile {

    
    private final LocalServiceTestHelper _helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    
    public TestWorkflowFile() {
    }

    @BeforeClass
    public static void setUpClass() {
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

    //doGet no wfid query parameter
    @Test
    public void testNoWorkflowIdQueryParameterSet(){
        try {
            WorkflowFile servlet = new WorkflowFile();
            
            HttpServletRequest mockRequest = mock(HttpServletRequest.class);
            when(mockRequest.getParameter(WorkflowFile.WFID)).thenReturn(null);
            HttpServletResponse mockResponse = mock(HttpServletResponse.class);
            servlet.doGet(mockRequest,mockResponse);
            
            verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST,
                    WorkflowFile.WFID+
                            " query parameter not set.  No workflow id found");
        }
        catch(Exception ex){
            fail("Unexpected Exception: "+ex.getMessage());
        }
        
    }

    //doGet unauthorized user
    @Test
    public void testUnauthorizedUser(){
        try {
            WorkflowFile servlet = new WorkflowFile();
            
            Authenticator mockAuthen = mock(Authenticator.class);
            servlet._authenticator = mockAuthen;
            User invalidUser = new User();
            invalidUser.setPermissions(Permission.NONE);
            HttpServletRequest mockRequest = mock(HttpServletRequest.class);
            when(mockAuthen.authenticate(mockRequest)).thenReturn(invalidUser);
            when(mockRequest.getParameter(WorkflowFile.WFID)).thenReturn("12345");
            HttpServletResponse mockResponse = mock(HttpServletResponse.class);
            servlet.doGet(mockRequest,mockResponse);
            
            verify(mockResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Not authorized");
        }
        catch(Exception ex){
            fail("Unexpected Exception: "+ex.getMessage());
        }
        
    }
    
    //doGet empty parameter from wfid
     @Test
    public void testEmptyWorkflowIdParameter(){
        try {
            WorkflowFile servlet = new WorkflowFile();
            
            Authenticator mockAuthen = mock(Authenticator.class);
            User validUser = new User();
            servlet._authenticator = mockAuthen;
            validUser.setPermissions(Permission.DOWNLOAD_ALL_WORKFLOWS);
            HttpServletRequest mockRequest = mock(HttpServletRequest.class);
            when(mockAuthen.authenticate(mockRequest)).thenReturn(validUser);
            when(mockRequest.getParameter(WorkflowFile.WFID)).thenReturn("");
            HttpServletResponse mockResponse = mock(HttpServletResponse.class);
            servlet.doGet(mockRequest,mockResponse);
            
            verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Workflow Id passed in via "+WorkflowFile.WFID+" query parameter is empty");
        }
        catch(Exception ex){
            fail("Unexpected Exception: "+ex.getMessage());
        }
        
    }
    
    
    //doGet IOException from workflowDownloader
     @Test
    public void testIOExceptinoFromWorkflowDownloader(){
        try {
            WorkflowFile servlet = new WorkflowFile();
            
            Authenticator mockAuthen = mock(Authenticator.class);
            User validUser = new User();
            servlet._authenticator = mockAuthen;
            
            WorkflowDownloader mockDownloader = mock(WorkflowDownloader.class);
            servlet._workflowDownloader = mockDownloader;
            validUser.setPermissions(Permission.DOWNLOAD_ALL_WORKFLOWS);
            HttpServletRequest mockRequest = mock(HttpServletRequest.class);
            when(mockAuthen.authenticate(mockRequest)).thenReturn(validUser);
            when(mockRequest.getParameter(WorkflowFile.WFID)).thenReturn("12345");
            HttpServletResponse mockResponse = mock(HttpServletResponse.class);
            
            doThrow(new IOException("hi")).when(mockDownloader).send("12345", mockResponse);
            
            servlet.doGet(mockRequest,mockResponse);
            
            verify(mockResponse).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error retreiving workflow file: hi");
            
            
        }
        catch(Exception ex){
            fail("Unexpected Exception: "+ex.getMessage());
        }
        
    }
    
    //doGet successful call
    @Test
    public void testSuccessfulCall(){
        try {
            WorkflowFile servlet = new WorkflowFile();
            
            Authenticator mockAuthen = mock(Authenticator.class);
            User validUser = new User();
            servlet._authenticator = mockAuthen;
            
            WorkflowDownloader mockDownloader = mock(WorkflowDownloader.class);
            servlet._workflowDownloader = mockDownloader;
            validUser.setPermissions(Permission.DOWNLOAD_ALL_WORKFLOWS);
            HttpServletRequest mockRequest = mock(HttpServletRequest.class);
            when(mockAuthen.authenticate(mockRequest)).thenReturn(validUser);
            when(mockRequest.getParameter(WorkflowFile.WFID)).thenReturn("12345");
            HttpServletResponse mockResponse = mock(HttpServletResponse.class);
            
            servlet.doGet(mockRequest,mockResponse);
            
            verify(mockResponse,never()).sendError(anyInt(), anyString());
            
            
        }
        catch(Exception ex){
            fail("Unexpected Exception: "+ex.getMessage());
        }
        
    }
    
    
    
    //@TODO doPost some exception
    
    //@TODO doPost success
   
    
}