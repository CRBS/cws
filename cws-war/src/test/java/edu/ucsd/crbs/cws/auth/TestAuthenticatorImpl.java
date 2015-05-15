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

package edu.ucsd.crbs.cws.auth;

import com.google.api.client.util.Base64;
import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import static edu.ucsd.crbs.cws.dao.objectify.OfyService.ofy;
import edu.ucsd.crbs.cws.dao.objectify.UserObjectifyDAOImpl;
import edu.ucsd.crbs.cws.rest.Constants;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
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
public class TestAuthenticatorImpl {

    private final LocalServiceTestHelper _helper
            = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(),
                    new LocalBlobstoreServiceTestConfig());
    
    public TestAuthenticatorImpl() {
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
        ofy().clear();
    }

    @After
    public void tearDown() {
         _helper.tearDown();
    }

    @Test
    public void testAuthenticateNullRequest(){
        try {
            AuthenticatorImpl auth = new AuthenticatorImpl();
            auth.authenticate(null);
            fail("Expected Exception cause request is null");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("Request is null"));
        }
    }
    
    // test Authenticate null getHeader and no such user
    @Test
    public void testAuthenticateWithNullHeaderAndNoQueryParametersAndNullIp() throws Exception {
         
         AuthenticatorImpl auth = new AuthenticatorImpl();
         HttpServletRequest request = mock(HttpServletRequest.class);
         when(request.getParameter(AuthenticatorImpl.AUTHORIZATION_HEADER)).thenReturn(null);
         when(request.getParameter(Constants.USER_LOGIN_PARAM)).thenReturn(null);
         when(request.getParameter(Constants.USER_TOKEN_PARAM)).thenReturn(null);
         when(request.getParameter(Constants.USER_LOGIN_TO_RUN_AS_PARAM)).thenReturn(null);
         
         User u = auth.authenticate(request);
         assertTrue(u != null);
         assertTrue(u.getPermissions() == Permission.NONE);
         assertTrue(u.getIpAddress() == null);
         
         verify(request).getHeader(AuthenticatorImpl.AUTHORIZATION_HEADER);
         verify(request).getParameter(Constants.USER_LOGIN_PARAM);
         verify(request).getParameter(Constants.USER_TOKEN_PARAM);
         verify(request).getParameter(Constants.USER_LOGIN_TO_RUN_AS_PARAM);
    }
    
    @Test
    public void testAuthenticateWithNullHeaderAndNoQueryParametersAndValidIp() throws Exception {
         
         AuthenticatorImpl auth = new AuthenticatorImpl();
         HttpServletRequest request = mock(HttpServletRequest.class);
         when(request.getRemoteAddr()).thenReturn("192.168.1.1");
         when(request.getHeader(AuthenticatorImpl.AUTHORIZATION_HEADER)).thenReturn(null);
         when(request.getParameter(Constants.USER_LOGIN_PARAM)).thenReturn(null);
         when(request.getParameter(Constants.USER_TOKEN_PARAM)).thenReturn(null);
         when(request.getParameter(Constants.USER_LOGIN_TO_RUN_AS_PARAM)).thenReturn(null);
         
         User u = auth.authenticate(request);
         assertTrue(u != null);
         assertTrue(u.getPermissions() == Permission.NONE);
         assertTrue(u.getIpAddress().equals("192.168.1.1"));
         
         verify(request).getHeader(AuthenticatorImpl.AUTHORIZATION_HEADER);
         verify(request).getParameter(Constants.USER_LOGIN_PARAM);
         verify(request).getParameter(Constants.USER_TOKEN_PARAM);
         verify(request).getParameter(Constants.USER_LOGIN_TO_RUN_AS_PARAM);
    }
    
    // test Authenticate invalid decode of authString empty string
    @Test
    public void testAuthenticateInvalidAuthInHeader() throws Exception {
         AuthenticatorImpl auth = new AuthenticatorImpl();
         HttpServletRequest request = mock(HttpServletRequest.class);
         when(request.getRemoteAddr()).thenReturn("192.168.1.1");
         when(request.getHeader(AuthenticatorImpl.AUTHORIZATION_HEADER)).thenReturn("Basic ");
         

         User u = auth.authenticate(request);
         assertTrue(u != null);
         assertTrue(u.getPermissions() == Permission.NONE);
         assertTrue(u.getIpAddress().equals("192.168.1.1"));
         
         verify(request).getHeader(AuthenticatorImpl.AUTHORIZATION_HEADER);
        
    }

    // test Authenticate invalid decode of authString no colon
    @Test
    public void testAuthenticateInvalidAuthNoColon() throws Exception {
         AuthenticatorImpl auth = new AuthenticatorImpl();
         HttpServletRequest request = mock(HttpServletRequest.class);
         when(request.getRemoteAddr()).thenReturn("192.168.1.1");
         when(request.getHeader(AuthenticatorImpl.AUTHORIZATION_HEADER)).thenReturn("Basic "+encodeString("ha"));
         User u = auth.authenticate(request);
         assertTrue(u != null);
         assertTrue(u.getPermissions() == Permission.NONE);
         assertTrue(u.getIpAddress().equals("192.168.1.1"));
         
         verify(request).getHeader(AuthenticatorImpl.AUTHORIZATION_HEADER);
        
    }

    @Test
    public void testAuthenticateValidAuthInHeaderAndUserInDataStore() throws Exception {
        UserObjectifyDAOImpl userDAO = new UserObjectifyDAOImpl();
       
        User dbuser = new User();
        dbuser.setLogin("bob");
        dbuser.setToken("smith");
        dbuser.setPermissions(Permission.LIST_ALL_JOBS);
        dbuser = userDAO.insert(dbuser);
      
         AuthenticatorImpl auth = new AuthenticatorImpl();
         HttpServletRequest request = mock(HttpServletRequest.class);
         when(request.getRemoteAddr()).thenReturn("192.168.1.1");
         when(request.getHeader(AuthenticatorImpl.AUTHORIZATION_HEADER)).thenReturn("Basic "+encodeString("bob:smith"));
         
         User u = auth.authenticate(request);
         assertTrue(u != null);
         assertTrue(u.getLogin().equals("bob"));
         assertTrue(u.getToken().equals("smith"));
         assertTrue(u.getPermissions() == Permission.LIST_ALL_JOBS);
         assertTrue(u.getIpAddress().equals("192.168.1.1"));
         assertTrue(u.getId() == dbuser.getId().longValue());
         
        verify(request).getHeader(AuthenticatorImpl.AUTHORIZATION_HEADER);

    }
    
    @Test
    public void testAuthenticateValidAuthButNoUser() throws Exception {
        UserObjectifyDAOImpl userDAO = new UserObjectifyDAOImpl();
      
         AuthenticatorImpl auth = new AuthenticatorImpl();
         HttpServletRequest request = mock(HttpServletRequest.class);
         when(request.getRemoteAddr()).thenReturn("192.168.1.1");
         when(request.getHeader(AuthenticatorImpl.AUTHORIZATION_HEADER)).thenReturn("Basic "+encodeString("bob:smith"));
         
         User u = auth.authenticate(request);
         assertTrue(u != null);
         assertTrue(u.getPermissions() == Permission.NONE);
         assertTrue(u.getIpAddress().equals("192.168.1.1"));
         
        verify(request).getHeader(AuthenticatorImpl.AUTHORIZATION_HEADER);

    }
    
    //test valid User, but invalid ip 
    @Test
    public void testAuthenticateValidAuthButInvalidIp() throws Exception {
        UserObjectifyDAOImpl userDAO = new UserObjectifyDAOImpl();
       
        User dbuser = new User();
        dbuser.setLogin("bob");
        dbuser.setToken("smith");
        dbuser.setPermissions(Permission.LIST_ALL_JOBS);
        ArrayList<String> allowedIps = new ArrayList<String>();
        allowedIps.add("192.168.1.2");
        dbuser.setAllowedIpAddresses(allowedIps);
        dbuser = userDAO.insert(dbuser);
      
         AuthenticatorImpl auth = new AuthenticatorImpl();
         HttpServletRequest request = mock(HttpServletRequest.class);
         when(request.getRemoteAddr()).thenReturn("192.168.1.1");
         when(request.getHeader(AuthenticatorImpl.AUTHORIZATION_HEADER)).thenReturn("Basic "+encodeString("bob:smith"));
         
         User u = auth.authenticate(request);
         assertTrue(u.getLogin() == null);
         assertTrue(u.getToken() == null);
         assertTrue(u.getPermissions() == Permission.NONE);
         assertTrue(u.getIpAddress().equals("192.168.1.1"));
         
        verify(request).getHeader(AuthenticatorImpl.AUTHORIZATION_HEADER);

    }
    
     @Test
    public void testAuthenticateUserFromLocal127ip() throws Exception {
        UserObjectifyDAOImpl userDAO = new UserObjectifyDAOImpl();
      
         AuthenticatorImpl auth = new AuthenticatorImpl();
         HttpServletRequest request = mock(HttpServletRequest.class);
         when(request.getRemoteAddr()).thenReturn("127.0.0.1");
         when(request.getHeader(AuthenticatorImpl.AUTHORIZATION_HEADER)).thenReturn("Basic "+encodeString("bob:smith"));
         
         User u = auth.authenticate(request);
         assertTrue(u != null);
         assertTrue(u.getLogin().equals("bob"));
         assertTrue(u.getToken().equals("smith"));
         assertTrue(u.getPermissions() == Permission.ALL);
         assertTrue(u.getIpAddress().equals("127.0.0.1"));
         
        verify(request).getHeader(AuthenticatorImpl.AUTHORIZATION_HEADER);

    }
    
    @Test
    public void testAuthenticateUserFromLocalipv6shortip() throws Exception {
      
         AuthenticatorImpl auth = new AuthenticatorImpl();
         HttpServletRequest request = mock(HttpServletRequest.class);
         when(request.getRemoteAddr()).thenReturn("::1");
         when(request.getHeader(AuthenticatorImpl.AUTHORIZATION_HEADER)).thenReturn("Basic "+encodeString("bob:smith"));
         
         User u = auth.authenticate(request);
         assertTrue(u != null);
         assertTrue(u.getLogin().equals("bob"));
         assertTrue(u.getToken().equals("smith"));
         assertTrue(u.getPermissions() == Permission.ALL);
         assertTrue(u.getIpAddress().equals("::1"));
         
        verify(request).getHeader(AuthenticatorImpl.AUTHORIZATION_HEADER);

    }
    
    @Test
    public void testAuthenticateUserFromLocalipv6ip() throws Exception {
      
         AuthenticatorImpl auth = new AuthenticatorImpl();
         HttpServletRequest request = mock(HttpServletRequest.class);
         when(request.getRemoteAddr()).thenReturn("0:0:0:0:0:0:0:1");
         when(request.getHeader(AuthenticatorImpl.AUTHORIZATION_HEADER)).thenReturn("Basic "+encodeString("bob:smith"));
         
         User u = auth.authenticate(request);
         assertTrue(u != null);
         assertTrue(u.getLogin().equals("bob"));
         assertTrue(u.getToken().equals("smith"));
         assertTrue(u.getPermissions() == Permission.ALL);
         assertTrue(u.getIpAddress().equals("0:0:0:0:0:0:0:1"));
         
        verify(request).getHeader(AuthenticatorImpl.AUTHORIZATION_HEADER);

    }
    
    /**
     * Helper method to encode a string in Base64
     * @param theStr
     * @return 
     */
    public static String encodeString(final String theStr) {
        byte[] ha = Base64.encodeBase64(theStr.getBytes());
        return new String(ha, StandardCharsets.UTF_8);
    }
}