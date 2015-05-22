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
import edu.ucsd.crbs.cws.auth.UserIpAddressValidator;
import edu.ucsd.crbs.cws.auth.UserIpAddressValidatorImpl;
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
    
    private UserIpAddressValidator _ipAddressValidator = new UserIpAddressValidatorImpl();
    
    public static final String USER_ID_QUERY_PARAM = "userid";

    public static final String TEST_IP_QUERY_PARAM = "testip";
    
    public static final String UPDATE_USER_QUERY_PARAM = "updateuser";
    
    public static final String GAE_CSS_LINK = "<link rel=\"stylesheet\" href=\"https://appengine.google.com/css/compiled.css\"/>";
    
    public static final String STRIKE_THROUGH_STYLE_ATTRIBUTE = "style=\"text-decoration: line-through;\"";
    public static final String STRIKE_THROUGH_STYLE_INLINE_BLOCK_ATTRIBUTE = "style=\"text-decoration: line-through; display: inline-block;\"";
    
    public static final String JQUERY_INCLUDE = "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\"></script>";
    public static final String JQUERY_UI_INCLUDE = " <script src=\"https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js\"></script>";
    public static final String JQUERY_UI_CSS_LINK = "<link rel=\"stylesheet\" href=\"https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/themes/smoothness/jquery-ui.css\">";
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String userId = req.getParameter(USER_ID_QUERY_PARAM);
        if (userId == null){
            handleAddUser(req,resp);
            return;
        }
        else {
            String testip = req.getParameter(TEST_IP_QUERY_PARAM);
            if (testip != null){
                handleTestIp(userId,testip,req,resp);
                return;
            }
            if (req.getParameter(UPDATE_USER_QUERY_PARAM) != null){
                handleUpdateUser(userId,req,resp);
            }
            handleUserView(userId,req,resp);
            return;
        }
    }
    
    private String getTestIpResponse(boolean success,final String errorMsg) {

        if (success == true){
            return "Valid";
        }

        if (success == false && errorMsg == null){
            return "Invalid";
        }
        return errorMsg;
    }
    
    protected void handleTestIp(final String userId,final String testIp,HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
             User u = _userDAO.getUserById(userId);
             if (u == null){
                 _log.log(Level.INFO,"No user found for id {0}",userId);
                 resp.getWriter().print(getTestIpResponse(false,"No User Found"));
                 return;
             }
             if (u.getAllowedIpAddresses() == null || u.getAllowedIpAddresses().isEmpty()){
                 _log.log(Level.INFO,"ip address list is null or empty for user {0} so all ip addresses are valid",
                         userId);
                 resp.getWriter().print(getTestIpResponse(true,null));
                 return;
             }
             u.setIpAddress(testIp);
             if (_ipAddressValidator.isUserRequestFromValidIpAddress(u)){
                 _log.log(Level.INFO,"Ip {0} is valid for user {1}",
                         new Object[]{testIp,userId});
                 resp.getWriter().print(getTestIpResponse(true,null));
                 return;
             }
             _log.log(Level.INFO,"Ip {0} is NOT valid for user {1}",
                         new Object[]{testIp,userId});
                 
             resp.getWriter().print(getTestIpResponse(false,null));
        }
        catch(Exception ex){
            throw new ServletException(ex);
        }
        
    }
    
    protected void handleUpdateUser(final String userId,HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            User u = _userDAO.getUserById(userId);
            if (u == null){
                resp.getWriter().write("<html>\n<head>\n"+GAE_CSS_LINK+"\n</head>\n<body>\nNo User with Id: "+userId+
                        " found\n</body>\n</html>\n");
                return;
            }
            User updatedUser = generateUserFromRequest(req,resp,false,false);
            u.setLogin(updatedUser.getLogin());
            u.setToken(updatedUser.getToken());
            u.setAllowedIpAddresses(updatedUser.getAllowedIpAddresses());
            u.setDeleted(updatedUser.isDeleted());
            u = _userDAO.update(u);
        }
        catch(Exception ex){
            throw new ServletException(ex);
        }
        
    }
    
    protected void handleUserView(final String userId,HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         try {
            User u = _userDAO.getUserById(userId);
            if (u == null){
                resp.getWriter().write("<html>\n<head>\n"+GAE_CSS_LINK+"\n</head>\n<body>\nNo User with Id: "+userId+
                        " found\n</body>\n</html>\n");
                return;
            }
            PrintWriter pw = resp.getWriter();
            pw.write("<html>\n");
            pw.write("  <head>\n");
            pw.write("     "+JQUERY_INCLUDE+"\n");
            pw.write("      "+GAE_CSS_LINK+"\n");
            pw.write("      "+JQUERY_UI_CSS_LINK+"\n");
            pw.write("     "+JQUERY_UI_INCLUDE+"\n");

            pw.write("     <script>\n");
            pw.write("            $(function() {\n" 
                    + "               $( document ).tooltip();\n" 
                    + "           });\n"
                    + "           function enableDisableEdit(enableFlag){\n"
                    + "               $( \"#login\" ).prop('disabled',enableFlag);\n"
                    + "               $( \"#token\" ).prop('disabled',enableFlag);\n"
                    + "               $( \"#ipaddress\" ).prop('disabled',enableFlag);\n"
                    + "               $( \"#deletecheckbox\" ).prop('disabled',enableFlag);\n"
                    + "               $( \"#submitbutton\" ).prop('disabled',enableFlag);\n"
                    + "               //$( \"#testipbutton\" ).prop('disabled',!enableFlag);\n"
                    + "               $( \"#testipfield\" ).prop('disabled',!enableFlag);\n"
                    + "           }\n");
            pw.write("            function editClick(){\n"
                    + "                enableDisableEdit(!$( \"#editchecky\").prop('checked'));\n"
                    + "           }\n");
            pw.write("            $(document).ready(function() {\n"
                    + "                editClick();\n"
                    + "                $( \"#testipbutton\").click(function(event){\n"
                    + "                    event.preventDefault();\n"
                    + "                    alert(\"test ip clicked\");\n"
                    + "                    $.ajax({"
                    + "                       method: \"POST\","
                    + "                       url: \"../users/users.html?userid="+u.getId()+"&testip=127.0.0.1\","
                    + "                    })"
                    + "                      .done(function(msg){"
                    + "                          alert(\" Done \" + msg);"
                    + "                    });\n"
                    + "                });\n"
                    + "           });\n"
                    + "   </script>\n");
            pw.write("  </head>\n");
            
            
            pw.write("  <body>\n");
            pw.write("<h1>Edit/View User</h1>\n");
            pw.write("<input type=\"checkbox\" name=\"editchecky\" onclick=\"editClick()\" id=\"editchecky\" title=\"Click to Edit User\">Edit User</input><p/>");
            pw.write("    <form id=\"edituser\" method=\"post\" action=\"../users/users.html?userid="+u.getId()+"\" >\n");
            pw.write("       <div id=\"formcontents\">\n");
            pw.write("      <b>Login:</b> <input id=\"login\" "
                    + "name=\"login\" type=\"text\" value=\""+u.getLogin()+"\" "
                    + "title=\"Login for user. No funny characters or spaces\""
                    + " disabled /><p/>\n");
            pw.write("      <b>Token:</b> <input id=\"token\" "
                    + "name=\"token\" type=\"text\" value=\""+u.getToken()+"\" "
                    + " style=\"width: 40%;\" "
                    + "title=\"Token for user\""
                    + "disabled /><p/>\n");
            pw.write("      <b>Allowed IP Addresses:</b> <input id=\"ipaddress\" "
                    + "style=\"width: 50%;\""
                    + "name=\"ipaddress\" type=\"text\" value=\""
                    +getIpAddresses(u)+"\" "
                    + "title=\"Comma delimited list of ip addresses allowed or all for all addresses. "
                    + "Supports Ipv4, Ipv6 and Ipv4 CIDR\""
                    + " disabled /><p/>\n");
            pw.write("      <input type=\"checkbox\" id=\"deletecheckbox\" disabled name=\"deleted\" ");
            if (u.isDeleted()){
                pw.write("checked ");   
            }
                    
            pw.write(" value=\"deleted\" title=\"Logically deletes or undeletes User\">Deleted</input><p/>\n");
            pw.write("   <div id=\"buttongroup\" style=\"display: inline-block;\">\n");
            pw.write("      <button id=\"submitbutton\" type=\"submit\" "
                    + "title=\"Updates User with Changes\" "
                    + "form=\"edituser\" value=\"update\">Update</button>");
            
            pw.write("      <button id=\"testipbutton\" type=\"submit\" "
                    + "title=\"Tests Request Ip Address to see if valid for this User. NOT IMPLEMENTED YET\" "
                    + "form=\"edituser\" value=\"testip\" disabled>Test Request IP</button>\n"
                    + "     </div>\n");
            
            pw.write("      <input type=\"hidden\" name=\"updateuser\" value=\"true\"/>\n");
            pw.write("       </div>\n");
            
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
        try {
            User u = generateUserFromRequest(req,resp,true,true);
            u = _userDAO.insert(u);
            PrintWriter pw = resp.getWriter();
            ArrayList<User> uList = new ArrayList<User>();
            uList.add(u);
            pw.write("<html>\n<head>\n"+GAE_CSS_LINK+"\n<style>\n table,th,td { border: 1px solid black; }\n</style>\n</head>\n<body>\n");
            pw.write("<h3>User Added</h3>\n");
            pw.write(renderUsersInTable(uList));
            pw.write("</body>\n</html>\n");
        }
        catch(Exception ex){
            _log.log(Level.WARNING, "Caught Exception: "+ex.getMessage(), ex);
            throw new ServletException(ex);
        } 
    }

    private User generateUserFromRequest(HttpServletRequest req,HttpServletResponse resp,boolean createToken,boolean updatePermFromMap) throws Exception,IOException {
        String login = req.getParameter("login");
        
        if (login == null || login.isEmpty()){
            _log.log(Level.WARNING, "Login passed in is empty or null");
            resp.getWriter().write("<html>\n<head>\n"+GAE_CSS_LINK+"\n</head>\n<body>\nUnable to add.  Login is empty or null\n</body>\n</html>\n");
            return null;
        }
        
        String ipAddress = req.getParameter("ipaddress");
        
        int perm = 0;
        if (updatePermFromMap == true) {

            Map<String, String[]> paramMap = req.getParameterMap();
            String[] values = paramMap.get("permission");

            if (values != null) {
                for (String v : values) {
                    perm += Integer.parseInt(v);
                }
            }
        }
        User u = new User();
        u.setLogin(login);
        if (createToken == true){
            u.setToken(java.util.UUID.randomUUID().toString().replaceAll("-",""));
        }
        else {
            u.setToken(req.getParameter("token"));
        }
        if (updatePermFromMap == true){
            u.setPermissions(perm);
        }
        
        if (ipAddress != null && ipAddress.isEmpty() == false && ipAddress.trim().length() > 0){
            if (ipAddress.trim().equalsIgnoreCase("all") || 
                ipAddress.trim().equalsIgnoreCase("null")){
                u.setAllowedIpAddresses(null);
            }
            else {
                u.setAllowedIpAddresses(Arrays.asList(ipAddress));
            }
        }
        else {
            u.setAllowedIpAddresses(null);
        }
        if (req.getParameter("deleted") != null){
            u.setDeleted(true);
        }
        return u;
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
            pw.write("<head>\n"
                    + "       "+GAE_CSS_LINK+"\n"
                    + "<style>\n");
            pw.write(" table,th,td { border: 1px solid black; }\n");
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
    
    /**
     * Given a {@link User} function generates a comma delimited list of
     * allowed ip addresses extracted from {@link User#getAllowedIpAddresses()}
     * @param u
     * @return comma delimited list of allowed ip addresses
     */
    private String getIpAddresses(User u){
        
        if (u == null || u.getAllowedIpAddresses() == null || 
                u.getAllowedIpAddresses().isEmpty()){
            return "all";
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
            sb.append(STRIKE_THROUGH_STYLE_INLINE_BLOCK_ATTRIBUTE);
            sb.append(">Strike through</div> denotes deleted User(s)\n");
            return sb.toString();
    }
    
}
