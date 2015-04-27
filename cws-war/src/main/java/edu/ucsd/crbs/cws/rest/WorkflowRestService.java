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
import com.google.appengine.api.blobstore.UploadOptions.Builder;
import edu.ucsd.crbs.cws.auth.Authenticator;
import edu.ucsd.crbs.cws.auth.AuthenticatorImpl;
import edu.ucsd.crbs.cws.auth.Permission;
import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.dao.EventDAO;
import edu.ucsd.crbs.cws.dao.JobDAO;
import edu.ucsd.crbs.cws.dao.WorkflowDAO;
import edu.ucsd.crbs.cws.dao.objectify.EventObjectifyDAOImpl;
import edu.ucsd.crbs.cws.dao.objectify.InputWorkspaceFileLinkObjectifyDAOImpl;
import edu.ucsd.crbs.cws.dao.objectify.JobObjectifyDAOImpl;
import edu.ucsd.crbs.cws.dao.objectify.WorkflowObjectifyDAOImpl;
import edu.ucsd.crbs.cws.log.Event;
import edu.ucsd.crbs.cws.log.EventBuilder;
import edu.ucsd.crbs.cws.log.EventBuilderImpl;
import edu.ucsd.crbs.cws.servlet.ServletUtil;
import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.WorkflowParameter;
import edu.ucsd.crbs.cws.workflow.report.DeleteReportImpl;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
 * REST Service that allows caller to create, modify, and retrieve Workflow
 * objects.
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@Path(Constants.SLASH + Constants.WORKFLOWS_PATH)
public class WorkflowRestService {

    private static final Logger _log
            = Logger.getLogger(WorkflowRestService.class.getName());

    static Authenticator _authenticator = new AuthenticatorImpl();

    static EventBuilder _eventBuilder = new EventBuilderImpl();
    
    static EventDAO _eventDAO = new EventObjectifyDAOImpl();

    static WorkflowDAO _workflowDAO = new WorkflowObjectifyDAOImpl((JobDAO)new JobObjectifyDAOImpl(new InputWorkspaceFileLinkObjectifyDAOImpl()));

    /**
     * Sets a new {@link Authenticator}
     * @param auth 
     */
    void setAuthenticator(Authenticator auth){
        _authenticator = auth;
    }
    
    void setEventBuilder(EventBuilder eb){
        _eventBuilder = eb;
    }
    
    void setEventDAO(EventDAO ed){
        _eventDAO = ed;
    }
    
    void setWorkflowDAO(WorkflowDAO wd){
        _workflowDAO = wd;
    }
    
    /**
     * URL path to workflow file servlet
     */
    public static final String WORKFLOW_FILE_SERVLET_PATH = "/workflowfile";
    
    /**
     * HTTP GET request on /workflows URI that returns a list of all Workflow
     * objects from WorkflowDAO. If none are found an empty list is returned. If
     * there is an error a 500 response is returned
     *
     * @param userLogin
     * @param userToken
     * @param userLoginToRunAs
     * @param request
     * @return List of Workflow objects in JSON format
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Workflow> getWorkflows(
            @QueryParam(Constants.SHOW_DELETED_QUERY_PARAM)final Boolean showDeleted,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request) {
        List<Workflow> workflows = null;
        try {
            User user = _authenticator.authenticate(request);
            Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());
            
            if (user.isAuthorizedTo(Permission.LIST_ALL_WORKFLOWS)) {
                return _workflowDAO.getAllWorkflows(true,showDeleted);
            }
            throw new WebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
        }
        catch(WebApplicationException wae){
            _log.log(Level.SEVERE,"Caught WebApplicationException",wae);
            throw wae;
        }
        catch (Exception ex) {
            _log.log(Level.SEVERE,"Caught Exception",ex);
            throw new WebApplicationException(ex);
        }
    }

    /**
     * Gets a specific Workflow by id
     *
     * @param wfid
     * @param userLogin
     * @param userToken
     * @param userLoginToRunAs
     * @param request
     * @return
     */
    @GET
    @Path(Constants.WORKFLOW_ID_REST_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    public Workflow getWorkflow(@PathParam(Constants.WORKFLOW_ID_PATH_PARAM) String wfid,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request) {

        Workflow wf = null;
        try {
            User user = _authenticator.authenticate(request);
            Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());
            
            if (user.isAuthorizedTo(Permission.LIST_ALL_WORKFLOWS)) {
                Workflow w = _workflowDAO.getWorkflowById(wfid,user);
                if (w != null){
                    if (w.getParameters() != null){
                        String workspaceFileURL = null;
                        for (WorkflowParameter param : w.getParameters()){
                            if (param.getType().equals(WorkflowParameter.Type.FILE)){
                                if (workspaceFileURL == null){
                                    workspaceFileURL = ServletUtil.buildRequestURLForWorkspaceFile(request, user);
                                }
                                param.setValue(workspaceFileURL);
                            }
                        }
                    }
                }
                return w;
            }
            throw new WebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
            
        }catch(WebApplicationException wae){
            _log.log(Level.SEVERE,"Caught WebApplicationException",wae);
            throw wae;
        } catch (Exception ex) {
            _log.log(Level.SEVERE,"Caught Exception",ex);
            throw new WebApplicationException(ex);
        }
    }

    /**
     * Creates a new workflow by consuming JSON version of Workflow object
     *
     * @param w
     * @param userLogin
     * @param userToken
     * @param userLoginToRunAs
     * @param request
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Workflow createWorkflow(Workflow w,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request) {
        try {
            User user = _authenticator.authenticate(request);
             Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());
            
            if (!user.isAuthorizedTo(Permission.CREATE_WORKFLOW)) {
                throw new Exception("Not authorized");
            }

            Workflow insertedWorkflow = _workflowDAO.insert(w);

            //build upload URL and add it to workflow
            BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
            
            //@TODO need to cache the AppIdentity Service factory default google bucket 
            insertedWorkflow.setWorkflowFileUploadURL(blobstoreService.createUploadUrl(WORKFLOW_FILE_SERVLET_PATH,
                    Builder.withGoogleStorageBucketName(AppIdentityServiceFactory.getAppIdentityService().getDefaultGcsBucketName()+"/workflows/")));
            
            // save this event to datastore, but if it fails no biggy
            _eventDAO.neverComplainInsert(_eventBuilder.setAsCreateWorkflowEvent(event, w));
            
            return insertedWorkflow;

        } catch (Exception ex) {
            _log.log(Level.SEVERE,"Caught Exception",ex);
            throw new WebApplicationException(ex);
        }
    }
    
    @POST
    @Path(Constants.WORKFLOW_ID_REST_PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Workflow updateWorkflow(@PathParam(Constants.WORKFLOW_ID_PATH_PARAM)final Long workflowId,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @QueryParam(Constants.RESAVE_QUERY_PARAM) final String resave,
            @Context HttpServletRequest request) {
        
        try {
            User user = _authenticator.authenticate(request);
            Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());
            
            if (user.isAuthorizedTo(Permission.UPDATE_ALL_WORKFLOWS)){
                if (resave != null && resave.equalsIgnoreCase(Boolean.TRUE.toString())){
                    return _workflowDAO.resave(workflowId);
                }
                return null;
            }
            throw new WebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
        }catch(WebApplicationException wae){
            _log.log(Level.SEVERE,"Caught WebApplicationException",wae);
            throw wae;
        } catch(Exception ex){
             _log.log(Level.SEVERE,"Caught Exception",ex);
            throw new WebApplicationException(ex);
        }
    }
    
    @DELETE
    @Path(Constants.WORKFLOW_ID_REST_PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public DeleteReportImpl deleteWorkflow(@PathParam(Constants.WORKFLOW_ID_PATH_PARAM)final Long workflowId,
            @QueryParam(Constants.PERMANENTLY_DELETE_PARAM)final Boolean permanentlyDelete,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request) {
        try {
            User user = _authenticator.authenticate(request);
            Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());
            Workflow w = null;
            DeleteReportImpl dwr = new DeleteReportImpl();
            dwr.setId(workflowId);
            dwr.setSuccessful(false);
            dwr.setReason("Unknown");
            
            if (!user.isAuthorizedTo(Permission.DELETE_ALL_WORKFLOWS)){
                if (!user.isAuthorizedTo(Permission.DELETE_THEIR_WORKFLOWS)){
                    throw new WebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
                }
                else {
                    w = _workflowDAO.getWorkflowById(workflowId.toString(),user);
                    if (w == null){
                        dwr.setReason("Workflow ("+workflowId+") not found");
                        return dwr;
                    }
                    if (w.getOwner() == null){
                        dwr.setReason("Workflow ("+workflowId+") does not have owner");
                        return dwr;
                    }
                    if (!user.getLoginToRunJobAs().equals(w.getOwner())){
                        dwr.setReason(user.getLoginToRunJobAs()+
                                " does not have permission to delete Workflow ("
                                +workflowId+")");
                        return dwr;
                    }
                }
            }
            else {
                w = _workflowDAO.getWorkflowById(workflowId.toString(), user);
                if (w == null) {
                    dwr.setReason("Workflow ("+workflowId+") not found");
                    return dwr;
                }
            }
            // delete workflow
            dwr = _workflowDAO.delete(workflowId,permanentlyDelete);
            if (dwr == null){
                dwr = new DeleteReportImpl();
                dwr.setId(workflowId);
                dwr.setSuccessful(false);
                dwr.setReason("Unknown");
            }
            
            if (dwr.isSuccessful()){
                if (permanentlyDelete == null || permanentlyDelete == false){
                     _eventDAO.neverComplainInsert(_eventBuilder.setAsLogicalDeleteWorkflowEvent(event, w));
                }
                else {
                     _eventDAO.neverComplainInsert(_eventBuilder.setAsDeleteWorkflowEvent(event, w));
                }
            }
            return dwr;
        }catch(WebApplicationException wae){
            _log.log(Level.SEVERE,"Caught WebApplicationException",wae);
            throw wae;
        } catch(Exception ex){
             _log.log(Level.SEVERE,"Caught Exception",ex);
            throw new WebApplicationException(ex);
        }
        
        
    }
}
