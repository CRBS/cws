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

import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.UploadOptions;
import edu.ucsd.crbs.cws.auth.Permission;
import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.dao.EventDAO;
import edu.ucsd.crbs.cws.dao.WorkspaceFileDAO;
import edu.ucsd.crbs.cws.dao.objectify.EventObjectifyDAOImpl;
import edu.ucsd.crbs.cws.dao.objectify.WorkspaceFileObjectifyDAOImpl;
import edu.ucsd.crbs.cws.log.Event;
import edu.ucsd.crbs.cws.log.EventBuilder;
import edu.ucsd.crbs.cws.log.EventBuilderImpl;
import static edu.ucsd.crbs.cws.rest.TaskRestService._authenticator;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import java.util.List;
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
@Path(Constants.SLASH + Constants.WORKSPACEFILES_PATH)
public class WorkspaceFileRestService {

    private static final Logger _log
            = Logger.getLogger(WorkspaceFileRestService.class.getName());

    static WorkspaceFileDAO _workspaceFileDAO = new WorkspaceFileObjectifyDAOImpl();

    static EventBuilder _eventBuilder = new EventBuilderImpl();
    static EventDAO _eventDAO = new EventObjectifyDAOImpl();

    public static final String WORKSPACEFILE_SERVLET_PATH = "/workspacefile";
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<WorkspaceFile> getWorkspaceFiles(@QueryParam(Constants.OWNER_QUERY_PARAM) final String owner,
            @QueryParam(Constants.WSFID_PARAM) final String workspaceFileIdList,
            @QueryParam(Constants.SYNCED_QUERY_PARAM) final Boolean synced,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request) {
        try {
            User user = _authenticator.authenticate(request, userLogin, userToken,
                    userLoginToRunAs);
            Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());

            if (user.isAuthorizedTo(Permission.LIST_ALL_WORKSPACEFILES)) {
                if (workspaceFileIdList == null){
                    _log.log(Level.INFO,"calling getWorkspaceFiles");
                    return _workspaceFileDAO.getWorkspaceFiles(owner,synced);
                }
                _log.log(Level.INFO, "calling getWorkspaceFilesById: {0}", 
                        workspaceFileIdList);

                return _workspaceFileDAO.getWorkspaceFilesById(workspaceFileIdList, user);
            }
            throw new Exception("Not authorized");

        } catch (Exception ex) {
            _log.log(Level.SEVERE, "Caught exception ", ex);
            throw new WebApplicationException(ex);
        }
    }

    @GET
    @Path(Constants.WORKSPACEFILE_ID_REST_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkspaceFile getWorkspaceFile(@PathParam(Constants.WORKSPACEFILE_ID_PATH_PARAM) final String workspaceFileId,
            @QueryParam(Constants.ADD_UPLOAD_URL_PARAM) final Boolean addUploadURL,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request) {
        try {
            User user = _authenticator.authenticate(request, userLogin, userToken,
                    userLoginToRunAs);
            Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());

            if (user.isAuthorizedTo(Permission.LIST_ALL_WORKSPACEFILES)) {
                WorkspaceFile wsf = _workspaceFileDAO.getWorkspaceFileById(workspaceFileId, user);
                if (wsf == null){
                    return wsf;
                }
                if (addUploadURL != null && addUploadURL == true){
                    wsf = setFileUploadURL(wsf);
                }
                return wsf;
            }
            throw new Exception("Not authorized");
        } catch (Exception ex) {
            _log.log(Level.SEVERE, "Caught Exception", ex);
            throw new WebApplicationException(ex);
        }

    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkspaceFile createWorkspaceFile(WorkspaceFile workspaceFile,
            @QueryParam(Constants.ADD_UPLOAD_URL_PARAM) final Boolean addUploadURL,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request) {

         try {
            User user = _authenticator.authenticate(request, userLogin, userToken,
                    userLoginToRunAs);
            Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());
            
            if (user.isAuthorizedTo(Permission.CREATE_TASK)) {
                WorkspaceFile resWorkspaceFile = _workspaceFileDAO.insert(workspaceFile,false);
                if (addUploadURL == null || addUploadURL == true){
                    resWorkspaceFile = setFileUploadURL(resWorkspaceFile);
                }
                saveEvent(_eventBuilder.setAsCreateWorkspaceFileEvent(event, 
                        resWorkspaceFile));
                
                return resWorkspaceFile;
            }
            throw new Exception("Not Authorized");
        } catch (Exception ex) {
            _log.log(Level.SEVERE,"Caught Exception",ex);
            throw new WebApplicationException(ex);
        }
    }
    
    @POST
    @Path(Constants.WORKSPACEFILE_ID_REST_PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkspaceFile updateWorkspaceFile(@PathParam(Constants.WORKSPACEFILE_ID_PATH_PARAM)final Long workspaceFileId,
            @QueryParam(Constants.PATH_QUERY_PARAM) final String path,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request) {
    
         try {
            User user = _authenticator.authenticate(request, userLogin, userToken,
                    userLoginToRunAs);
            Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());
            
            if (user.isAuthorizedTo(Permission.UPDATE_ALL_WORKSPACEFILES)) {
                String adjustedPath = path;
                if (path != null && path.equals("")){
                    adjustedPath = null;
                }
                WorkspaceFile resWorkspaceFile = _workspaceFileDAO.updatePath(workspaceFileId, 
                        adjustedPath);
                return resWorkspaceFile;
            }
            throw new Exception("Not Authorized");
        } catch (Exception ex) {
            _log.log(Level.SEVERE,"Caught Exception",ex);
            throw new WebApplicationException(ex);
        }
    }
    
    
    private WorkspaceFile setFileUploadURL(WorkspaceFile wsf) throws Exception {
        //build upload URL and add it to workflow
            BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
            
            //@TODO need to cache the AppIdentity Service factory default google bucket 
            wsf.setUploadURL(blobstoreService.createUploadUrl(WORKSPACEFILE_SERVLET_PATH,
                    UploadOptions.Builder.withGoogleStorageBucketName(AppIdentityServiceFactory.getAppIdentityService().getDefaultGcsBucketName()+
                            "/workspacefile/"+wsf.getOwner()+"/")));
            return wsf;
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
