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

import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.dao.UserDAO;
import edu.ucsd.crbs.cws.dao.objectify.UserObjectifyDAOImpl;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Administration Servlet that handles addition and listing of {@link User}s
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class UsersServlet extends HttpServlet {

    private UserDAO _userDAO = new UserObjectifyDAOImpl();
    
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        Map<String,String[]> paramMap = req.getParameterMap();
        PrintWriter pw = resp.getWriter();
        pw.write("Add User Post<p/>");
            
        String login = req.getParameter("login");
        if (login != null){
            pw.write("<p/>login = "+login+"<br/>");
        }
        else {
            pw.write("<p/>login = null<br/>");
            
        }
        
        String ipAddress = req.getParameter("ipaddress");
        if (ipAddress != null){
            pw.write("<p/>IP = "+ipAddress+"<br/>");
        }
        else {
            pw.write("<p/>IP = null<br/>");
            
        }
        
        String[] values = paramMap.get("permission");
        StringBuilder sb = new StringBuilder();

        int perm = 0;
        for (String v : values) {
            perm += Integer.parseInt(v);
        }
        User u = new User();
        u.setLogin(login);
        u.setToken(java.util.UUID.randomUUID().toString().replaceAll("-",""));
        u.setPermissions(perm);
        
        if (ipAddress.isEmpty() == false && ipAddress.trim().length() > 0){
            u.setAllowedIpAddresses(Arrays.asList(ipAddress));
        }
        
        try {
            u = _userDAO.insert(u);
        }
        catch(Exception ex){
            throw new ServletException(ex);
        }
        
        pw.write(u.getLogin()+" "+u.getToken()+"<br/>");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<User> users = _userDAO.getUsers(null, Boolean.TRUE);
            PrintWriter pw = resp.getWriter();
            pw.write("List of Users<p/>");
            if (users != null){
                for (User u : users) {
                    pw.write(u.getLogin() + " " + u.getToken() + " " + u.getCreateDate().toString() + "<br/>");
                }
            }
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    
}
