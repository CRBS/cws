package edu.ucsd.crbs.cws.rest;

import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.blobstore.UploadOptions.Builder;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.auth.Authenticator;
import edu.ucsd.crbs.cws.auth.AuthenticatorImpl;
import edu.ucsd.crbs.cws.auth.Permission;
import edu.ucsd.crbs.cws.dao.EventDAO;
import edu.ucsd.crbs.cws.dao.WorkflowDAO;
import edu.ucsd.crbs.cws.dao.objectify.EventObjectifyDAOImpl;
import edu.ucsd.crbs.cws.workflow.Workflow;
import java.util.List;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import edu.ucsd.crbs.cws.dao.objectify.WorkflowObjectifyDAOImpl;
import edu.ucsd.crbs.cws.log.Event;
import edu.ucsd.crbs.cws.log.EventBuilder;
import edu.ucsd.crbs.cws.log.EventBuilderImpl;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

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

    static WorkflowDAO _workflowDAO = new WorkflowObjectifyDAOImpl();

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
    public List<Workflow> getWorkflows(@QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request) {
        List<Workflow> workflows = null;
        try {
            User user = _authenticator.authenticate(request, userLogin,
                    userToken,userLoginToRunAs);
            Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());
            
            if (user.isAuthorizedTo(Permission.LIST_ALL_WORKFLOWS)) {
                return _workflowDAO.getAllWorkflows(true);
            }
            throw new Exception("Not authorized");
        } catch (Exception ex) {
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
    @Path("/{wfid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Workflow getWorkflow(@PathParam("wfid") String wfid,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request) {

        Workflow wf = null;
        try {
            User user = _authenticator.authenticate(request, userLogin, userToken,
                    userLoginToRunAs);
            Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());
            
            if (user.isAuthorizedTo(Permission.LIST_ALL_WORKFLOWS)) {
                return _workflowDAO.getWorkflowById(wfid,user);
            }
            throw new Exception("Not authorized");
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
            User user = _authenticator.authenticate(request, userLogin, userToken,
                    userLoginToRunAs);
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
            saveEvent(_eventBuilder.setAsCreateWorkflowEvent(event, w));
            
            return insertedWorkflow;

        } catch (Exception ex) {
            _log.log(Level.SEVERE,"Caught Exception",ex);
            throw new WebApplicationException(ex);
        }
    }
    
    /**
     * Save Event to Datastore. If it fails just log it cause it is not critical
     * @param event Event to save
     */
    private void saveEvent(Event event){
        try {
           _eventDAO.insert(event);
        }
        catch(Exception ex){
            _log.log(Level.WARNING, "Unable to save Event", ex);
        }
    }
}
