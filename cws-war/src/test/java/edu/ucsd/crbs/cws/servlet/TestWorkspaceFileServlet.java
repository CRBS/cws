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
import edu.ucsd.crbs.cws.rest.Constants;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author churas
 */
public class TestWorkspaceFileServlet {
    
        private final LocalServiceTestHelper _helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    
    public TestWorkspaceFileServlet() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        Logger.getLogger(WorkspaceFileServlet.class.getName()).setLevel(Level.OFF);
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

    @Test
    public void testNoWorkspaceFileId() throws Exception {
        WorkspaceFileServlet servlet = new WorkspaceFileServlet();
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getParameter(Constants.WSFID_PARAM)).thenReturn(null);
           HttpServletResponse mockResponse = mock(HttpServletResponse.class);
            servlet.doGet(mockRequest,mockResponse);
            
            verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST,
                    Constants.WSFID_PARAM+
                            " query parameter not set.  No workspacefile id found");        
    }
    
        //doGet unauthorized user
    @Test
    public void testUnauthorizedUser(){
        try {
            WorkspaceFileServlet servlet = new WorkspaceFileServlet();
            Authenticator mockAuthen = mock(Authenticator.class);
            servlet._authenticator = mockAuthen;
            User invalidUser = new User();
            invalidUser.setPermissions(Permission.NONE);
            HttpServletRequest mockRequest = mock(HttpServletRequest.class);
            when(mockAuthen.authenticate(mockRequest)).thenReturn(invalidUser);
            when(mockRequest.getParameter(Constants.WSFID_PARAM)).thenReturn("12345");
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
    public void testEmptyWorkspaceFileIdParameter(){
        try {
            WorkspaceFileServlet servlet = new WorkspaceFileServlet();
            Authenticator mockAuthen = mock(Authenticator.class);
            User validUser = new User();
            servlet._authenticator = mockAuthen;
            validUser.setPermissions(Permission.DOWNLOAD_ALL_WORKSPACEFILES);
            HttpServletRequest mockRequest = mock(HttpServletRequest.class);
            when(mockAuthen.authenticate(mockRequest)).thenReturn(validUser);
            when(mockRequest.getParameter(Constants.WSFID_PARAM)).thenReturn("");
            HttpServletResponse mockResponse = mock(HttpServletResponse.class);
            servlet.doGet(mockRequest,mockResponse);
            
            verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "WorkspaceFile Id passed in via "+Constants.WSFID_PARAM+" query parameter is empty");
        }
        catch(Exception ex){
            fail("Unexpected Exception: "+ex.getMessage());
        }
        
    }
    
    
    //doGet IOException from workflowDownloader
     @Test
    public void testIOExceptionFromWorkspaceFileDownloader(){
        try {
            WorkspaceFileServlet servlet = new WorkspaceFileServlet();
            
            Authenticator mockAuthen = mock(Authenticator.class);
            User validUser = new User();
            servlet._authenticator = mockAuthen;
            
            Downloader mockDownloader = mock(Downloader.class);
            servlet._downloader = mockDownloader;
            validUser.setPermissions(Permission.DOWNLOAD_ALL_WORKSPACEFILES);
            HttpServletRequest mockRequest = mock(HttpServletRequest.class);
            when(mockAuthen.authenticate(mockRequest)).thenReturn(validUser);
            when(mockRequest.getParameter(Constants.WSFID_PARAM)).thenReturn("12345");
            HttpServletResponse mockResponse = mock(HttpServletResponse.class);
            
            doThrow(new IOException("hi")).when(mockDownloader).send("12345", mockResponse);
            
            servlet.doGet(mockRequest,mockResponse);
            
            verify(mockResponse).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error retreiving workspacefile: hi");
            
        }
        catch(Exception ex){
            fail("Unexpected Exception: "+ex.getMessage());
        }
        
    }
    
    //doGet successful call
    @Test
    public void testSuccessfulCall(){
        try {
            WorkspaceFileServlet servlet = new WorkspaceFileServlet();
            Authenticator mockAuthen = mock(Authenticator.class);
            User validUser = new User();
            servlet._authenticator = mockAuthen;
            
            Downloader mockDownloader = mock(Downloader.class);
            servlet._downloader = mockDownloader;
            validUser.setPermissions(Permission.DOWNLOAD_ALL_WORKSPACEFILES);
            HttpServletRequest mockRequest = mock(HttpServletRequest.class);
            when(mockAuthen.authenticate(mockRequest)).thenReturn(validUser);
            when(mockRequest.getParameter(Constants.WSFID_PARAM)).thenReturn("12345");
            HttpServletResponse mockResponse = mock(HttpServletResponse.class);
            
            servlet.doGet(mockRequest,mockResponse);
            
            verify(mockResponse,never()).sendError(anyInt(), anyString());
        }
        catch(Exception ex){
            fail("Unexpected Exception: "+ex.getMessage());
        }
        
    }
   
}
