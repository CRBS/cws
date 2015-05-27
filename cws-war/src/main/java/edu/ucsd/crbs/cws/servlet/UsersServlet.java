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
 * that appears in <b>Administration Console</b> of Google App Engine.<p/>
 * 
 * The original plan was to create a set of html pages and to use Jquery to
 * hit the REST services to provide views to list, update, and delete 
 * {@link User} objects.  Unfortunately this did not work because any request
 * coming from the static pages is coming from a domain that is different then
 * where the REST service is hosted.  There is probably a way to fix this 
 * but I went with a servlet to generate the html pages got around this issue.
 * At some point this should be switched to at least JSP pages.
 * <br/>
 * 
 * <h5>How this works</h5>
 * 
 * <b>Configuration</b><p/>
 * The <b>web.xml</b> file was configured so that requests to <b>/users/*</b> are
 * mapped to this servlet.<br/>
 * In addition, security constraints to <b>/rest/users, /users/* and 
 * /adduser.html</b> that limit access to only <b>admin</b>  were added. <br/>
 * In <b>appengine-web.xml file {@value #USERS_HTML} and /adduser.html</b> were 
 * added to <b>Admin Console</b> page list so they would show up in the 
 * administration page on Google App Engine.<p/>
 * 
 * <b>Flow and usage</b>
 * When deployed to Google App Engine, an admin user can go to the administration
 * console 
 * (<a href="http://appengine.google.com" target="_blank">http://appengine.google.com</a>)
 * and under the <b>Custom</b> section on
 * left side of page should be two pages <b>Add User</b> and <b>User List.</b><br/>
 * The <b>Add user</b> link maps to <b>/adduser.html</b> which is a form page
 * that lets one create a new {@link User}.  Upon hitting submit the result is
 * POSTed to the <code>doPost</code> method of this object.  <br/>
 * The <b>User List</b> link maps to this object and causes <code>doGet</code>
 * method to be invoked which generates a list of {@link User}s in a table view
 * with links on each {@link User} letting the caller edit and or delete.  <br/>
 * For more detailed flow see 
 * {@link #doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
 * and {@link #doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
 * below.
 * 
 * 
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class UsersServlet extends HttpServlet {

    private static final Logger _log = Logger.getLogger(UsersServlet.class.getName());

    /**
     * Provides access to {@link User} objects in Data Store
     */
    private UserDAO _userDAO = new UserObjectifyDAOImpl();

    /**
     * Used to test ip addresses against {@link User} for validity
     */
    private UserIpAddressValidator _ipAddressValidator = new UserIpAddressValidatorImpl();

    
    /**
     * The page that maps to this object, {@link UsersServlet}
     */
    public static final String USERS_HTML = "/users/users.html";
    
    /**
     * Query Parameter that should be set to {@link User#getId()} to view
     * and or edit a {@link User}.  This is used
     * by both POST and GET methods below.
     */
    public static final String USER_ID_QUERY_PARAM = "userid";

    /**
     * Query Parameter that should contain ip address to test against a user
     */
    public static final String TEST_IP_QUERY_PARAM = "testip";

    /**
     * Query Parameter that denotes the {@link User} should be updated
     */
    public static final String UPDATE_USER_QUERY_PARAM = "updateuser";

    public static final String IP_ADDRESS_PARAM = "ipaddress";
    
    public static final String PERMISSION_PARAM = "permission";

    public static final String RAW_PERMISSION_PARAM = "rawpermission";
    
    public static final String TOKEN_PARAM = "token";
    
    public static final String LOGIN_PARAM = "login";
    
    public static final String DELETED_PARAM = "deleted";
    
    public static final String ALL_IP_STRING = "all";
    /**
     * CSS for Google App Engine Admin Console
     */
    public static final String GAE_CSS_LINK = "<link rel=\"stylesheet\" "
            + "href=\"https://appengine.google.com/css/compiled.css\"/>";

    /**
     * Strike through style to denote deleted {@link User}s
     */
    public static final String STRIKE_THROUGH_STYLE_ATTRIBUTE = "style=\"text-decoration: line-through;\"";

    /**
     * Strike through style to denote deleted {@link User}s, but with inline-block
     * added to prevent new line
     */
    public static final String STRIKE_THROUGH_STYLE_INLINE_BLOCK_ATTRIBUTE = "style=\""
            + "text-decoration: line-through; display: inline-block;\"";

    /**
     * Jquery include
     */
    public static final String JQUERY_INCLUDE = "<script src=\""
            + "https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\">"
            + "</script>";

    /**
     * Jquery UI include
     */
    public static final String JQUERY_UI_INCLUDE = " <script src=\""
            + "https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js\">"
            + "</script>";

    /**
     * Jquery CSS include
     */
    public static final String JQUERY_UI_CSS_LINK = "<link rel=\"stylesheet\" "
            + "href=\"https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/themes/smoothness/jquery-ui.css\">";

    /**
     * Handles POST requests to <b>test ip address, add a new {@link User}, 
     * list a single {@link User}, or update a
     * {@link User}.</b> Which action is handled depends on parameters 
     * passed to the <b>req</b>uest<br/>
     * if {@value USER_ID_QUERY_PARAM} parameter is <b>NOT</b> set then a new
     * {@link User} is added.<p/>
     * else  <p/>
     * if {@value TEST_IP_QUERY_PARAM} parameter is set then the ip address 
     * set in the parameter is tested.<p/>
     * if {@value UPDATE_USER_QUERY_PARAM} parameter is set then the {@link User}
     * is updated<p/>
     * If only the {@value USER_ID_QUERY_PARAM} is set then a detailed view of
     * that {@link User} is displayed
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doPost(HttpServletRequest req,
            HttpServletResponse resp) throws ServletException, IOException {

        String userId = req.getParameter(USER_ID_QUERY_PARAM);
        if (userId == null) {
            handleAddUser(req, resp);
            return;
        } else {
            String testip = req.getParameter(TEST_IP_QUERY_PARAM);
            if (testip != null) {
                handleTestIp(userId, testip, req, resp);
                return;
            }
            if (req.getParameter(UPDATE_USER_QUERY_PARAM) != null) {
                handleUpdateUser(userId, req, resp);
            }
            handleUserView(userId, req, resp);
            return;
        }
    }

    /**
     * Converts result of ip test to a string.  
     * @param success denotes whether test was successful (<b>true</b>) or not 
     * @param errorMsg Set to error message if there was a problem with the test
     * @return <b>Valid</b> upon success, <b>Invalid</b> or contents of 
     * <b>errorMsg</b> upon failure.
     */
    private String getTestIpResponse(boolean success, final String errorMsg) {

        if (success == true) {
            return "Valid";
        }

        if (success == false && errorMsg == null) {
            return "Invalid";
        }
        return errorMsg;
    }

    /**
     * Tests <b>testIp</b> against {@link User} specified by <b>userId</b> using
     * {@link #_ipAddressValidator} to see if its a valid request for the user.
     * Result is returned in plain text obtained from 
     * {@link #getTestIpResponse(boolean, java.lang.String)}
     * @param userId id of {@link User}
     * @param testIp ip address to test
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException 
     */
    protected void handleTestIp(final String userId,
            final String testIp, HttpServletRequest req,
            HttpServletResponse resp) throws ServletException, IOException {
        try {
            User u = _userDAO.getUserById(userId);
            if (u == null) {
                _log.log(Level.INFO, "No user found for id {0}", userId);
                resp.getWriter().print(getTestIpResponse(false,
                        "No User Found"));
                return;
            }
            if (u.getAllowedIpAddresses() == null
                    || u.getAllowedIpAddresses().isEmpty()) {
                _log.log(Level.INFO, "ip address list is null or empty for "
                        + "user {0} so all ip addresses are valid",
                        userId);
                resp.getWriter().print(getTestIpResponse(true, null));
                return;
            }
            u.setIpAddress(testIp);
            if (_ipAddressValidator.isUserRequestFromValidIpAddress(u)) {
                _log.log(Level.INFO, "Ip {0} is valid for user {1}",
                        new Object[]{testIp, userId});
                resp.getWriter().print(getTestIpResponse(true, null));
                return;
            }
            _log.log(Level.INFO, "Ip {0} is NOT valid for user {1}",
                    new Object[]{testIp, userId});

            resp.getWriter().print(getTestIpResponse(false, null));
        } catch (Exception ex) {
            throw new ServletException(ex);
        }

    }

    /**
     * Updates {@link User} specified by <b>userId</b> with data in
     * <b>req</b> request. 
     * See {@link #generateUserFromRequest(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, boolean, boolean) } for
     * information on how to pass information via the <b>req</b>.  <br/>
     * If there is an error a small html page is sent to <b>resp</b> with error.
     * otherwise no response is written cause it is assumed caller will handle
     * it.
     * 
     * @param userId
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException 
     */
    protected void handleUpdateUser(final String userId,
            HttpServletRequest req,
            HttpServletResponse resp) throws ServletException, IOException {
        try {
            User u = _userDAO.getUserById(userId);
            if (u == null) {
                resp.getWriter().write("<html>\n<head>\n" + GAE_CSS_LINK + "\n"
                        + "</head>\n<body>\nNo User with Id: " + userId
                        + " found\n</body>\n</html>\n");
                return;
            }
            User updatedUser = generateUserFromRequest(req, resp, false, false);
            u.setLogin(updatedUser.getLogin());
            u.setToken(updatedUser.getToken());
            u.setPermissions(updatedUser.getPermissions());
            u.setAllowedIpAddresses(updatedUser.getAllowedIpAddresses());
            u.setDeleted(updatedUser.isDeleted());
            _userDAO.update(u);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }

    }

    /**
     * Generates an html page that lets a user edit an individual {@link User}
     * identified by <b>userId</b>  If there is a problem loading {@link User}
     * then a small html page is returned denoting the error.  
     * @param userId
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException 
     */
    protected void handleUserView(final String userId,
            HttpServletRequest req,
            HttpServletResponse resp) throws ServletException, IOException {
        try {
            User u = _userDAO.getUserById(userId);
            if (u == null) {
                resp.getWriter().write("<html>\n<head>\n" + GAE_CSS_LINK + "\n"
                        + "</head>\n<body>\nNo User with Id: " + userId
                        + " found\n</body>\n</html>\n");
                return;
            }
            PrintWriter pw = resp.getWriter();
            pw.write("<html>\n");
            pw.write("  <head>\n");
            pw.write("     " + JQUERY_INCLUDE + "\n");
            pw.write("      " + GAE_CSS_LINK + "\n");
            pw.write("      " + JQUERY_UI_CSS_LINK + "\n");
            pw.write("     " + JQUERY_UI_INCLUDE + "\n");
            pw.write("     <script>\n"
                    + "      $(function() {\n"
                    + "        $( document ).tooltip();\n"
                    + "      });\n"
                    + "      function enableDisableEdit(enableFlag){\n"
                    + "        $(\"#login\").prop('disabled',enableFlag);\n"
                    + "        $(\"#token\").prop('disabled',enableFlag);\n"
                    + "        $(\"#ipaddress\").prop('disabled',enableFlag);\n"
                    + "        $(\"#rawpermission\").prop('disabled',enableFlag);\n"
                    + "        $(\"#deletecheckbox\").prop('disabled',enableFlag);\n"
                    + "        $(\"#submitbutton\").prop('disabled',enableFlag);\n"
                    + "        //$(\"#testipbutton\").prop('disabled',!enableFlag);\n"
                    + "        $(\"#testipfield\").prop('disabled',!enableFlag);\n"
                    + "      }\n"
                    + "      function editClick(){\n"
                    + "        enableDisableEdit(!$(\"#editchecky\").prop('checked'));\n"
                    + "      }\n"
                    + "      $(document).ready(function() {\n"
                    + "        editClick();\n"
                    + "        $( \"#testipbutton\").click(function(event){\n"
                    + "          event.preventDefault();\n"
                    + "          alert(\"test ip clicked\");\n"
                    + "          $.ajax({"
                    + "            method: \"POST\","
                    + "            url: \".."+USERS_HTML+"?userid="
                    + u.getId() + "&testip=127.0.0.1\","
                    + "          })"
                    + "            .done(function(msg){"
                    + "              alert(\" Done \" + msg);"
                    + "            });\n"
                    + "        });\n"
                    + "      });\n"
                    + "    </script>\n"
                    + "  </head>\n"
                    + "  <body>\n"
                    + "<h1>Edit/View User</h1>\n");

            pw.write("<input type=\"checkbox\" name=\"editchecky\" "
                    + "onclick=\"editClick()\" id=\"editchecky\" "
                    + "title=\"Click to Edit User\">Edit User</input><p/>");

            pw.write("    <form id=\"edituser\" method=\"post\" "
                    + "action=\".."+USERS_HTML+"?userid=" + u.getId() + "\" >\n");

            pw.write("       <div id=\"formcontents\">\n");

            pw.write("      <b>Login:</b> <input id=\"login\" "
                    + "name=\"login\" type=\"text\" value=\"" + u.getLogin() + "\" "
                    + "title=\"Login for user. No funny characters or spaces\""
                    + " disabled /><p/>\n");

            pw.write("      <b>Token:</b> <input id=\"token\" "
                    + "name=\"token\" type=\"text\" value=\"" + u.getToken() + "\" "
                    + " style=\"width: 40%;\" "
                    + "title=\"Token for user\""
                    + "disabled /><p/>\n");
            
            pw.write("      <b>Permission:</b> <input id=\"rawpermission\" "
                    + "name=\"rawpermission\" type=\"text\" value=\"" + 
                    u.getPermissions() + "\" "
                    + " style=\"width: 30%;\" "
                    + "title=\"Raw Permission for User\""
                    + "disabled /><p/>\n");

            pw.write("      <b>Allowed IP Addresses:</b> <input id=\"ipaddress\" "
                    + "style=\"width: 50%;\""
                    + "name=\"ipaddress\" type=\"text\" value=\""
                    + getIpAddresses(u) + "\" "
                    + "title=\"Comma delimited list of ip addresses allowed or "
                    + "all for all addresses. "
                    + "Supports Ipv4, Ipv6 and Ipv4 CIDR\""
                    + " disabled /><p/>\n");

            pw.write("      <input type=\"checkbox\" id=\"deletecheckbox\" "
                    + "disabled name=\"deleted\" ");

            if (u.isDeleted()) {
                pw.write("checked ");
            }

            pw.write(" value=\"deleted\" "
                    + "title=\"Logically deletes or undeletes User\">"
                    + "Deleted</input><p/>\n");

            pw.write("   <div id=\"buttongroup\" "
                    + "style=\"display: inline-block;\">\n");

            pw.write("      <button id=\"submitbutton\" type=\"submit\" "
                    + "title=\"Updates User with Changes\" "
                    + "form=\"edituser\" value=\"update\">Update</button>");

            pw.write("      <button id=\"testipbutton\" type=\"submit\" "
                    + "title=\"Tests Request Ip Address to see if valid for this "
                    + "User. NOT IMPLEMENTED YET\" "
                    + "form=\"edituser\" value=\"testip\" disabled>Test Request "
                    + "IP</button>\n"
                    + "     </div>\n");

            pw.write("      <input type=\"hidden\" name=\"updateuser\" "
                    + "value=\"true\"/>\n");

            pw.write("       </div>\n");

            pw.write("    </form>\n");
            pw.write("  </body>\n");
            pw.write("</html>\n");
        } catch (Exception ex) {
            _log.log(Level.WARNING, "Caught Exception: " + ex.getMessage(), ex);
            throw new ServletException(ex);
        }
    }

    /**
     * Handles case where user needs to be added, by extracting the user
     * from the <b>req</b> 
     * {@link HttpServletRequest#getParameter(java.lang.String)}
     * method.<br/>  (See {@link #generateUserFromRequest(javax.servlet.http.HttpServletRequest, 
     * javax.servlet.http.HttpServletResponse, boolean, boolean)} for information
     * on what is being extracted.)
     * Once the {@link User} is extracted it is inserted into the data store
     * and a page showing the {@link User} is displayed to the user upon success
     * or an error page upon failure.  
     * @param req
     * @param resp
     * @throws ServletException If any exception is caught
     * @throws IOException
     */
    protected void handleAddUser(HttpServletRequest req,
            HttpServletResponse resp) throws ServletException, IOException {
        try {
            User u = generateUserFromRequest(req, resp, true, true);
            u = _userDAO.insert(u);
            PrintWriter pw = resp.getWriter();
            ArrayList<User> uList = new ArrayList<User>();
            uList.add(u);
            pw.write("<html>\n<head>\n" + GAE_CSS_LINK + "\n<style>\n table,th,td "
                    + "{ border: 1px solid black; }\n</style>\n</head>\n<body>\n");
            pw.write("<h3>User Added</h3>\n");
            pw.write(renderUsersInTable(uList));
            pw.write("</body>\n</html>\n");
        } catch (Exception ex) {
            _log.log(Level.WARNING, "Caught Exception: " + ex.getMessage(), ex);
            throw new ServletException(ex);
        }
    }

    /**
     * Parses <b>req</b> to create a new {@link User}  The following
     * parameters are queried via a the {@link HttpServletRequest#getParameter(java.lang.String)}
     * and {@link HttpServletRequest#getParameterMap()} methods.<p/>
     * 
     * <b>Parameters</b><p/>
     *  <ul>
     *   <li><b>{@value #LOGIN_PARAM}</b> login of user, must be set. No
     *  spaces, and no funny characters.</li>
     *   <li><b>{@value #IP_ADDRESS_PARAM}</b> IP address(es) that requests
     * for this {@link User} can originate from.  This can be a comma delimited
     * list of Ipv4, or Ipv6 addresses with optional CIDR notation to restrict
     * the request to those ip addresses, OR <code>null</code>, 
     * (the text <b>null</b> or <b>all</b>), or an
     * empty string to allow requests from all addresses.  This value is parsed
     * and passed {@link User#setAllowedIpAddresses(java.util.List)}.</li>
     *   <li><b>{@value #TOKEN_PARAM}</b> If <b>NOT</b> <code>null</code> 
     * sets the {@link User#getToken()}.  Otherwise a token is generated from
     * {@link java.util.UUID#randomUUID()}</li>
     *   <li><b>{@value #DELETED_PARAM}</b> If set to anything, then {@link User}
     * will created created with {@link User#isDeleted()} set to <code>true</code>
     *   <li><b>{@value #PERMISSION_PARAM}</b> this is a tricky parameter that
     * is extracted from {@link HttpServletRequest#getParameterMap()} cause
     * it is expected to be a list of integers which correspond to {@link Permission}s
     * this method takes all those integers and adds them up to create the
     * final {@link Permission} set in the {@link User}</li>
     *   <li><b>{@value #RAW_PERMISSION_PARAM}</b> if set and <b>updatePermFromMap</b>
     * is <code>false</b> then this value will set the {@link Permission} for
     * the {@link User}</li>
     *  </ul>
     * 
     * 
     * @param req
     * @param resp
     * @param createToken
     * @param updatePermFromMap
     * @return
     * @throws Exception
     * @throws IOException 
     */
    protected User generateUserFromRequest(HttpServletRequest req,
            HttpServletResponse resp, boolean createToken,
            boolean updatePermFromMap) throws Exception, IOException {
        String login = req.getParameter(LOGIN_PARAM);

        if (login == null || login.isEmpty()) {
            _log.log(Level.WARNING, "Login passed in is empty or null");
            resp.getWriter().write("<html>\n<head>\n" + GAE_CSS_LINK + "\n"
                    + "</head>\n<body>\nUnable to add.  Login is empty or "
                    + "null\n</body>\n</html>\n");
            return null;
        }

        boolean updatePerm = false;
        String ipAddress = req.getParameter(IP_ADDRESS_PARAM);

        int perm = 0;
        if (updatePermFromMap == true) {

            Map<String, String[]> paramMap = req.getParameterMap();
            String[] values = paramMap.get(PERMISSION_PARAM);

            if (values != null) {
                for (String v : values) {
                    perm += Integer.parseInt(v);
                }
            }
            updatePerm = true;
        } else {
            String rawPerm = req.getParameter(RAW_PERMISSION_PARAM);
            if (rawPerm != null && rawPerm.trim().length() > 0){
                _log.log(Level.INFO,"Attempting to convert raw permission {0} "
                        + "to int",rawPerm);
                try {
                    perm = Integer.parseInt(rawPerm);
                }
                catch(NumberFormatException nfe){
                    _log.log(Level.WARNING,"Caught exception on permission parse");
                    throw new Exception(nfe);
                }
                updatePerm = true;
            }
        }
        
        User u = new User();
        u.setLogin(login);
        if (createToken == true) {
            u.setToken(java.util.UUID.randomUUID().toString().replaceAll("-", ""));
        } else {
            u.setToken(req.getParameter(TOKEN_PARAM));
        }
        if (updatePerm == true) {
            _log.log(Level.INFO,"Updating perm to {0}",perm);
            u.setPermissions(perm);
        }

        if (ipAddress != null && ipAddress.isEmpty() == false
                && ipAddress.trim().length() > 0) {

            if (ipAddress.trim().equalsIgnoreCase(ALL_IP_STRING)
                    || ipAddress.trim().equalsIgnoreCase("null")) {
                u.setAllowedIpAddresses(null);
            } else {
                u.setAllowedIpAddresses(Arrays.asList(ipAddress.split(",")));
            }
        } else {
            u.setAllowedIpAddresses(null);
        }
        if (req.getParameter(DELETED_PARAM) != null) {
            u.setDeleted(true);
        }
        return u;
    }

    /**
     * Displays a list of {@link User}s in a table format.  If 
     * {@link HttpServletRequest#getParameter(java.lang.String)} has 
     * {@value #USER_ID_QUERY_PARAM} set then only that {@link User} will be
     * displayed.  
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doGet(HttpServletRequest req,
            HttpServletResponse resp) throws ServletException, IOException {

        String userId = req.getParameter(USER_ID_QUERY_PARAM);
        if (userId != null) {
            handleUserView(userId, req, resp);
            return;
        }

        try {

            PrintWriter pw = resp.getWriter();

            pw.write("<html>\n");
            pw.write("<head>\n"
                    + "       " + GAE_CSS_LINK + "\n"
                    + "<style>\n");
            pw.write(" table,th,td { border: 1px solid black; }\n");
            pw.write("</style></head>\n");

            pw.write("<body>\n<h3>List of Users</h3>\n");

            String login = req.getParameter("login");
            if (login != null) {
                pw.write("User added: " + login + "</p>");
            }

            List<User> users = _userDAO.getUsers(null, Boolean.TRUE);

            pw.write(renderUsersInTable(users));

            pw.write("</body>\n</html>\n");

        } catch (Exception ex) {
            _log.log(Level.WARNING, "Caught Exception: " + ex.getMessage(), ex);
            throw new ServletException(ex);
        }
    }

    /**
     * Given a {@link User} function generates a comma delimited list of allowed
     * ip addresses extracted from {@link User#getAllowedIpAddresses()}
     *
     * @param u
     * @return comma delimited list of allowed ip addresses
     */
    private String getIpAddresses(User u) {

        if (u == null || u.getAllowedIpAddresses() == null
                || u.getAllowedIpAddresses().isEmpty()) {
            return ALL_IP_STRING;
        }
        StringBuilder sb = new StringBuilder();
        for (String ip : u.getAllowedIpAddresses()) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(ip);
        }
        return sb.toString();
    }

    /**
     * Given a list of {@link User}s in <b>users</b> this method generates
     * an html table fragment that lists the users.
     * @param users
     * @return
     * @throws Exception 
     */
    private String renderUsersInTable(List<User> users) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(" <table>\n");
        sb.append("  <tr>\n");
        sb.append("   <th>Login</th>\n");
        sb.append("   <th>Token</th>\n");
        sb.append("   <th>Create Date</th>\n");
        sb.append("   <th>Permissions</th>\n");
        sb.append("   <th>Allowed IP Addresses</th>\n");
        sb.append("  </tr>\n");

        if (users != null) {
            for (User u : users) {
                if (u.isDeleted()) {
                    sb.append("   <tr ").append(STRIKE_THROUGH_STYLE_ATTRIBUTE);
                    sb.append(">\n");
                } else {
                    sb.append("  <tr>\n");
                }
                sb.append("    <td><a href=\""+USERS_HTML+"?userid=");
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
