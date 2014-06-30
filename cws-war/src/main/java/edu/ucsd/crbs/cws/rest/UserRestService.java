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

package edu.ucsd.crbs.cws.rest;

import edu.ucsd.crbs.cws.auth.Authenticator;
import edu.ucsd.crbs.cws.auth.AuthenticatorImpl;
import edu.ucsd.crbs.cws.auth.Permission;
import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.dao.EventDAO;
import edu.ucsd.crbs.cws.dao.UserDAO;
import edu.ucsd.crbs.cws.dao.objectify.EventObjectifyDAOImpl;
import edu.ucsd.crbs.cws.dao.objectify.UserObjectifyDAOImpl;
import edu.ucsd.crbs.cws.log.Event;
import edu.ucsd.crbs.cws.log.EventBuilder;
import edu.ucsd.crbs.cws.log.EventBuilderImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@Path(Constants.SLASH+Constants.USERS_PATH)
public class UserRestService {
    
    private static final Logger _log
            = Logger.getLogger(UserRestService.class.getName());
    
    
    static EventDAO _eventDAO = new EventObjectifyDAOImpl();

    static Authenticator _authenticator = new AuthenticatorImpl();

    static EventBuilder _eventBuilder = new EventBuilderImpl();
    
    static UserDAO _userDAO = new UserObjectifyDAOImpl();
    
    @GET
    @Path(Constants.USER_ID_REST_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser(@PathParam(Constants.USER_ID_PATH_PARAM) String userid,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request){
    
        try {
            User user = _authenticator.authenticate(request,userLogin,userToken,userLoginToRunAs);
            Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());
            if (user.isAuthorizedTo(Permission.LIST_ALL_USERS)){
                return _userDAO.getUserById(userid);
            }
            throw new Exception("Not Authorized");
        } catch(Exception ex){
            _log.log(Level.SEVERE,"Caught Exception",ex);
            throw new WebApplicationException(ex);
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User createUser(User u,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request){
        try {
            User user = _authenticator.authenticate(request,userLogin,userToken,userLoginToRunAs);
            Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());
            if (user.isAuthorizedTo(Permission.CREATE_USER)){
                if (u.getLogin() == null || u.getLogin().trim().equals("")){
                    throw new Exception("Login must be set");
                }
                //if no token is set create one
                if (u.getToken() == null){
                    u.setToken(java.util.UUID.randomUUID().toString());
                }
                
                u = _userDAO.insert(u);
                saveEvent(_eventBuilder.setAsCreateUserEvent(event, u));
                return u;
            }
            throw new Exception("Not Authorized");
        } catch(Exception ex){
            _log.log(Level.SEVERE,"Caught Exception",ex);
            throw new WebApplicationException(ex);
        }
        
    }
    
    
     private void saveEvent(Event event){
        try {
           _eventDAO.insert(event);
        }
        catch(Exception ex){
            _log.log(Level.WARNING, "Unable to save Event", ex);
        }
    }

}
