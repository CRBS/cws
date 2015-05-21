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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private static final Logger _log = Logger.getLogger(UsersServlet.class.getName());
    
    private UserDAO _userDAO = new UserObjectifyDAOImpl();
    
    public static final String USER_ID_QUERY_PARAM = "userid";
    
    public static final String GAE_CSS_LINK = "<link rel=\"stylesheet\" href=\"https://appengine.google.com/css/compiled.css\"/>";
    
    public static final String STRIKE_THROUGH_STYLE_ATTRIBUTE = "style=\"text-decoration: line-through;\"";
    
    public static final String JQUERY_INCLUDE = "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\"></script>";
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String userId = req.getParameter(USER_ID_QUERY_PARAM);
        if (userId == null){
            handleAddUser(req,resp);
            return;
        }
        else {
            handleUserView(userId,req,resp);
            return;
        }
    }
    
    protected void handleUserView(final String userId,HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         try {
            User u = _userDAO.getUserById(userId);
            if (u == null){
                resp.getWriter().write("<html><head><style>"+GAE_CSS_LINK+"</style></head><body>No User with Id: "+userId+
                        " found</body></html>\n");
                return;
            }
            PrintWriter pw = resp.getWriter();
            pw.write("<html>\n");
            pw.write("  <head>\n");
            pw.write("     "+JQUERY_INCLUDE+"\n");
            pw.write("     <style>\n");
            pw.write("        "+GAE_CSS_LINK+"\n");
            pw.write("     </style>");
            pw.write("  </head>\n");
            
            
            pw.write("  <body>\n");
            pw.write("<h1>Edit/View User</h1>\n");
            pw.write("<input type=\"checkbox\" name=\"editchecky\" id=\"editchecky\">Edit User</input><p/>");
            pw.write("    <form id=\"edituser\" method=\"post\" action=\"../users/users.html\">\n");
            pw.write("      <b>Login:</b> <input id=\"login\" "
                    + "name=\"login\" type=\"text\" value=\""+u.getLogin()+"\" "
                    + "title=\"Login for user. No funny characters or spaces\""
                    + "/><p/>\n");
            pw.write("      <b>Token:</b> <input id=\"token\" "
                    + "name=\"token\" type=\"text\" value=\""+u.getToken()+"\" "
                    + "title=\"Token for user\""
                    + "/><p/>\n");
            pw.write("      <b>Allowed IP Addresses:</b> <input id=\"ipaddress\" "
                    + "style=\"width: 50%;\""
                    + "name=\"ipaddress\" type=\"text\" value=\""
                    +getIpAddresses(u)+"\" "
                    + "title=\"Comma delimited list of ip addresses allowed or null for all. "
                    + "Supports Ipv4, Ipv6 and Ipv4 CIDR\""
                    + "/><p/>\n");
            pw.write("      <input type=\"checkbox\" name=\"deleted\" ");
            if (u.isDeleted()){
                pw.write("checked ");   
            }
                    
            pw.write(" value=\"deleted\">Deleted</input><p/>\n");
            
            pw.write("      <button id=\"submitbutton\" type=\"submit\" "
                    + "title=\"Updates User with Changes\" "
                    + "form=\"edituser\" value=\"update\">Update</button>");
            
            pw.write("      <button id=\"testip\" type=\"submit\" "
                    + "title=\"Tests Request Ip Address to see if valid for this User\" "
                    + "form=\"edituser\" value=\"testip\">Test Request IP</button>");
            
             pw.write(" <input id=\"testip\" "
                    + "name=\"testipaddress\" type=\"text\" value=\"\" "
                    + "title=\"Ip to test with this user to see if its valid\""
                    + "/><p/>\n");
            
            pw.write("    </form>\n");
            pw.write("  </body>\n");
            pw.write("</html>\n");
            
            
        }
        catch(Exception ex){
            _log.log(Level.WARNING, "Caught Exception: "+ex.getMessage(), ex);
            throw new ServletException(ex);
        }
    }
    
    /**
     * Handles case where user needs to be added.
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException 
     */
    protected void handleAddUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        
        if (login == null || login.isEmpty()){
            _log.log(Level.WARNING, "Login passed in is empty or null");
            resp.getWriter().write("<html><head><style>"+GAE_CSS_LINK+"</style></head><body>Unable to add.  Login is empty or null</body></html>\n");
            return;
        }
        
        String ipAddress = req.getParameter("ipaddress");
        
        Map<String,String[]> paramMap = req.getParameterMap();
        String[] values = paramMap.get("permission");

        int perm = 0;
        if (values != null){
            for (String v : values) {
                perm += Integer.parseInt(v);
            }
        }
        
        User u = new User();
        u.setLogin(login);
        u.setToken(java.util.UUID.randomUUID().toString().replaceAll("-",""));
        u.setPermissions(perm);
        
        if (ipAddress != null && ipAddress.isEmpty() == false && ipAddress.trim().length() > 0){
            u.setAllowedIpAddresses(Arrays.asList(ipAddress));
        }
        
        try {
            u = _userDAO.insert(u);
            PrintWriter pw = resp.getWriter();
            ArrayList<User> uList = new ArrayList<User>();
            uList.add(u);
            pw.write("<html><head><style> table,th,td { border: 1px solid black; }\n"+GAE_CSS_LINK+"\n</style></head><body>\n");
            pw.write("<h3>User Added</h3>\n");
            pw.write(renderUsersInTable(uList));
            pw.write("</body></html>\n");
        }
        catch(Exception ex){
            _log.log(Level.WARNING, "Caught Exception: "+ex.getMessage(), ex);
            throw new ServletException(ex);
        } 
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String userId = req.getParameter(USER_ID_QUERY_PARAM);
        if (userId != null){
            handleUserView(userId,req,resp);
            return;
        }
        
        try {
           
            PrintWriter pw = resp.getWriter();
            
            pw.write("<html>\n");
            pw.write("<head><style>\n");
            pw.write(" table,th,td { border: 1px solid black; }\n");
            pw.write("   "+GAE_CSS_LINK+"\n");
            pw.write("</style></head>\n");
            
            pw.write("<body>\n<h3>List of Users</h3>\n");
            
            String login = req.getParameter("login");
            if (login != null){
                pw.write("User added: "+login+"</p>");
            }

            List<User> users = _userDAO.getUsers(null, Boolean.TRUE);
            
            pw.write(renderUsersInTable(users));
            
            pw.write("</body>\n</html>\n");
            
        } catch (Exception ex) {
            _log.log(Level.WARNING, "Caught Exception: "+ex.getMessage(), ex);
            throw new ServletException(ex);
        }
    }
    
    private String getIpAddresses(User u){
        
        if (u == null || u.getAllowedIpAddresses() == null || 
                u.getAllowedIpAddresses().isEmpty()){
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        for (String ip : u.getAllowedIpAddresses()){
            if (sb.length() > 0){
                sb.append(",");
            }
            sb.append(ip);
        }
        return sb.toString();
    }

    private String renderUsersInTable(List<User> users) throws Exception{
        StringBuilder sb = new StringBuilder();
        sb.append(" <table>\n");
        sb.append("  <tr>\n");
        sb.append("   <th>Login</th>\n");
            sb.append("   <th>Token</th>\n");
            sb.append("   <th>Create Date</th>\n");
            sb.append("   <th>Permissions</th>\n");
            sb.append("   <th>Allowed IP Addresses</th>\n");
            sb.append("  </tr>\n");
            

            if (users != null){
                for (User u : users) {
                   if (u.isDeleted()){
                       sb.append("   <tr ").append(STRIKE_THROUGH_STYLE_ATTRIBUTE);
                       sb.append(">\n");
                   }
                   else {
                    sb.append("  <tr>\n");
                   }
                   sb.append("    <td><a href=\"/users/users.html?userid=");
                   sb.append(u.getId());
                   sb.append("\">");
                   sb.append(u.getLogin()).append("</a>");
                   sb.append("</td>\n");
                   sb.append("    <td>").append(u.getToken()).append("</td>\n");
                   sb.append("    <td>").append(u.getCreateDate()).append("</td>\n");
                   sb.append("    <td>").append(u.getPermissions()).append("</td>\n");
                   sb.append("    <td>").append(getIpAddresses(u)).append("</td>\n");
                   sb.append("  </tr>\n");
                }
            }
            sb.append(" </table>\n");
            sb.append("<div ");
            sb.append(STRIKE_THROUGH_STYLE_ATTRIBUTE);
            sb.append(">Strike through denotes deleted User(s)</div>\n");
            return sb.toString();
    }
    
}
