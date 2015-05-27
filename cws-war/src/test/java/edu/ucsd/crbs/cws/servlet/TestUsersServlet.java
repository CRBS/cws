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
package edu.ucsd.crbs.cws.servlet;

import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import edu.ucsd.crbs.cws.auth.User;
import static edu.ucsd.crbs.cws.dao.objectify.OfyService.ofy;
import edu.ucsd.crbs.cws.dao.objectify.UserObjectifyDAOImpl;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
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
public class TestUsersServlet {

    private final LocalServiceTestHelper _helper
            = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(),
                    new LocalBlobstoreServiceTestConfig());

    public TestUsersServlet() {
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
    public void testDoGetNoParamsSetNoUsers() throws Exception {
        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        HttpServletResponse mockRes = mock(HttpServletResponse.class);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(mockRes.getWriter()).thenReturn(pw);

        UsersServlet servlet = new UsersServlet();

        servlet.doGet(mockReq, mockRes);

        pw.flush();
        pw.close();

        String res = sw.toString();
        assertTrue(res, res.contains("List of Users"));

        assertTrue(res, !res.contains("<td>"));

    }

    @Test
    public void testDoGetSeveralUsers() throws Exception {

        UserObjectifyDAOImpl userDAO = new UserObjectifyDAOImpl();

        User u = new User();
        u.setLogin("bob");
        u.setToken("tokeny");
        u = userDAO.insert(u);

        User u2 = new User();
        u2.setLogin("fred");
        u2.setToken("hello");
        u2.setPermissions(42);
        u2 = userDAO.insert(u2);

        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        HttpServletResponse mockRes = mock(HttpServletResponse.class);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(mockRes.getWriter()).thenReturn(pw);

        UsersServlet servlet = new UsersServlet();

        servlet.doGet(mockReq, mockRes);

        pw.flush();
        pw.close();

        String res = sw.toString();
        assertTrue(res, res.contains("List of Users"));
        assertTrue(res, res.contains("42"));
        assertTrue(res, res.contains("bob"));
        assertTrue(res, res.contains("tokeny"));
        assertTrue(res, res.contains("fred"));
        assertTrue(res, res.contains("hello"));

    }
    
    @Test
    public void testDoGetWithUserIdButNoUser() throws Exception {
        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        HttpServletResponse mockRes = mock(HttpServletResponse.class);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(mockRes.getWriter()).thenReturn(pw);
        when(mockReq.getParameter(UsersServlet.USER_ID_QUERY_PARAM)).thenReturn("12345");

        UsersServlet servlet = new UsersServlet();

        servlet.doGet(mockReq, mockRes);

        pw.flush();
        pw.close();

        String res = sw.toString();
        assertTrue(res, res.contains("No User with Id: 12345"));
    }
    
    @Test
    public void testDoGetWithUserId() throws Exception {
        
         UserObjectifyDAOImpl userDAO = new UserObjectifyDAOImpl();

        User u = new User();
        u.setLogin("bob");
        u.setToken("tokeny");
        u = userDAO.insert(u);

        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        HttpServletResponse mockRes = mock(HttpServletResponse.class);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(mockRes.getWriter()).thenReturn(pw);
        when(mockReq.getParameter(UsersServlet.USER_ID_QUERY_PARAM))
                .thenReturn(u.getId().toString());

        UsersServlet servlet = new UsersServlet();

        servlet.doGet(mockReq, mockRes);

        pw.flush();
        pw.close();

        String res = sw.toString();
        assertTrue(res, res.contains("bob"));
        assertTrue(res, res.contains("tokeny"));
        
    }
    
    @Test
    public void testGenerateUserFromRequestNullLogin() throws Exception {
        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        HttpServletResponse mockRes = mock(HttpServletResponse.class);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(mockRes.getWriter()).thenReturn(pw);
       
        UsersServlet servlet = new UsersServlet();
        
        assertTrue(servlet.generateUserFromRequest(mockReq, mockRes, true, true) == null);
        pw.flush();
        pw.close();
        String res = sw.toString();
        assertTrue(res,res.contains("Login is empty or null"));
        
    }
    
    @Test
    public void testGenerateUserFromRequestEmptyLogin() throws Exception {
        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        HttpServletResponse mockRes = mock(HttpServletResponse.class);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(mockRes.getWriter()).thenReturn(pw);
        when(mockReq.getParameter(UsersServlet.LOGIN_PARAM)).thenReturn("");
        UsersServlet servlet = new UsersServlet();
        
        assertTrue(servlet.generateUserFromRequest(mockReq, mockRes, true, true) == null);
        pw.flush();
        pw.close();
        String res = sw.toString();
        assertTrue(res,res.contains("Login is empty or null"));
        
    }
    
    @Test
    public void testGenerateUserFromRequestCreateTokenTrueAndPermMapTrue() throws Exception {
        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        HttpServletResponse mockRes = mock(HttpServletResponse.class);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(mockRes.getWriter()).thenReturn(pw);
        when(mockReq.getParameter(UsersServlet.LOGIN_PARAM)).thenReturn("bob");
        HashMap<String,String[]> pMap = new HashMap<String,String[]>();
        String[] permList = new String[2];
        permList[0] = "2";
        permList[1] = "4";
        pMap.put(UsersServlet.PERMISSION_PARAM, permList);
        when(mockReq.getParameterMap()).thenReturn(pMap);
        UsersServlet servlet = new UsersServlet();
        
        User u = servlet.generateUserFromRequest(mockReq, mockRes, true, true);
        pw.flush();
        pw.close();
        String res = sw.toString();
        
        assertTrue(u.getLogin().equals("bob"));
        assertTrue(u.getToken() != null);
        assertTrue(u.getPermissions() == 6);
        List<String> ipAddress = u.getAllowedIpAddresses();
        assertTrue(ipAddress == null);
        assertTrue(u.isDeleted() == false);
    }
    
    @Test
    public void testGenerateUserFromRequestAllStringSetForIp() throws Exception {
        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        HttpServletResponse mockRes = mock(HttpServletResponse.class);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(mockRes.getWriter()).thenReturn(pw);
        when(mockReq.getParameter(UsersServlet.LOGIN_PARAM)).thenReturn("bob");
        when(mockReq.getParameter(UsersServlet.IP_ADDRESS_PARAM)).thenReturn(UsersServlet.ALL_IP_STRING);
        UsersServlet servlet = new UsersServlet();
        
        User u = servlet.generateUserFromRequest(mockReq, mockRes, false, false);
        pw.flush();
        pw.close();
        String res = sw.toString();
        
        assertTrue(u.getLogin().equals("bob"));
        List<String> ipAddress = u.getAllowedIpAddresses();
        assertTrue(ipAddress == null);
        
    }
    
    @Test
    public void testGenerateUserFromRequestNullStringForIpAddress() throws Exception {
        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        HttpServletResponse mockRes = mock(HttpServletResponse.class);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(mockRes.getWriter()).thenReturn(pw);
        when(mockReq.getParameter(UsersServlet.LOGIN_PARAM)).thenReturn("bob");
        when(mockReq.getParameter(UsersServlet.IP_ADDRESS_PARAM)).thenReturn("null");
        when(mockReq.getParameter(UsersServlet.DELETED_PARAM)).thenReturn("true");

        
        HashMap<String,String[]> pMap = new HashMap<String,String[]>();
       
        UsersServlet servlet = new UsersServlet();
        
        User u = servlet.generateUserFromRequest(mockReq, mockRes, false, false);
        pw.flush();
        pw.close();
        String res = sw.toString();
        
        assertTrue(u.getLogin().equals("bob"));
        assertTrue(u.getToken() == null);
        assertTrue(u.getPermissions() == 0);
        List<String> ipAddress = u.getAllowedIpAddresses();
        assertTrue(ipAddress == null);
        assertTrue(u.isDeleted() == true);
    }
    
    
    
    @Test
    public void testGenerateUserFromRequestMultipleIpAddressesAndRawPermSet() throws Exception {
        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        HttpServletResponse mockRes = mock(HttpServletResponse.class);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(mockRes.getWriter()).thenReturn(pw);
        when(mockReq.getParameter(UsersServlet.LOGIN_PARAM)).thenReturn("bob");
        when(mockReq.getParameter(UsersServlet.IP_ADDRESS_PARAM)).thenReturn("1.1.1.1/1,192.168.1.1");
        when(mockReq.getParameter(UsersServlet.DELETED_PARAM)).thenReturn("true");
        when(mockReq.getParameter(UsersServlet.RAW_PERMISSION_PARAM)).thenReturn("45");

        
        HashMap<String,String[]> pMap = new HashMap<String,String[]>();
       
        UsersServlet servlet = new UsersServlet();
        
        User u = servlet.generateUserFromRequest(mockReq, mockRes, false, false);
        pw.flush();
        pw.close();
        String res = sw.toString();
        List<String> ipAddress = u.getAllowedIpAddresses();
        assertTrue(ipAddress.size() == 2);
        assertTrue(u.getPermissions() == 45);
        assertTrue(ipAddress.get(0).equals("1.1.1.1/1") || ipAddress.get(0).equals("192.168.1.1"));
        assertTrue(ipAddress.get(1).equals("1.1.1.1/1") || ipAddress.get(1).equals("192.168.1.1"));
    }
}
