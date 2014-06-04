package edu.ucsd.crbs.cws.rest;

import com.google.appengine.api.blobstore.UploadOptions.Builder;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.auth.Authenticator;
import edu.ucsd.crbs.cws.auth.AuthenticatorImpl;
import edu.ucsd.crbs.cws.auth.Permission;
import edu.ucsd.crbs.cws.dao.WorkflowDAO;
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
import edu.ucsd.crbs.cws.servlet.WorkflowFile;
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
@Path("/" + Constants.WORKFLOWS_PATH)
public class WorkflowRestService {

    private static final Logger log
            = Logger.getLogger(WorkflowRestService.class.getName());

    WorkflowDAO _workflowDAO;

     static Authenticator _authenticator = new AuthenticatorImpl();
    /**
     * Constructor that by default creates Objectify DAO objects
     */
    public WorkflowRestService() {
        log.info("In constructor of Workflows() rest service");

        _workflowDAO = new WorkflowObjectifyDAOImpl();
    }

    /**
     * HTTP GET request on /workflows URI that returns a list of all Workflow
     * objects from WorkflowDAO. If none are found an empty list is returned. If
     * there is an error a 500 response is returned
     *
     * @return List of Workflow objects in JSON format
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Workflow> getWorkflows(@QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
                                       @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
                                       @Context HttpServletRequest request) {
        List<Workflow> workflows = null;
        try {
            TaskRestService.logRequest(request);
            User user = _authenticator.authenticate(request,userLogin,userToken);
            if (user.isAuthorizedTo(Permission.LIST_ALL_WORKFLOWS)){
                return _workflowDAO.getAllWorkflows(true);
            }
            throw new Exception("Not authorized");
        } catch (Exception ex) {
            throw new WebApplicationException(ex);
        }
    }

    /**
     * Gets a specific Workflow by id
     *
     * @param wfid
     * @return
     */
    @GET
    @Path("/{wfid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Workflow getWorkflow(@PathParam("wfid") String wfid,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @Context HttpServletRequest request) {

        Workflow wf = null;
        try {
            TaskRestService.logRequest(request);
            User user = _authenticator.authenticate(request,userLogin,userToken);
            if (user.isAuthorizedTo(Permission.LIST_ALL_WORKFLOWS)){
                return _workflowDAO.getWorkflowById(wfid);
            }
            throw new Exception("Not authorized");
        } catch (Exception ex) {
            throw new WebApplicationException(ex);
        }
    }

    /**
     * Creates a new workflow by consuming JSON version of Workflow object
     *
     * @param w
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Workflow createWorkflow(Workflow w,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @Context HttpServletRequest request) {
        try {
            TaskRestService.logRequest(request);
            User user = _authenticator.authenticate(request,userLogin,userToken);
            if (! user.isAuthorizedTo(Permission.CREATE_WORKFLOW)){
                throw new Exception("Not authorized");
            }
            
            Workflow insertedWorkflow = _workflowDAO.insert(w);

            //build upload URL and add it to workflow
            BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
            insertedWorkflow.setWorkflowFileUploadURL(blobstoreService.createUploadUrl("/workflowfile",
                    Builder.withGoogleStorageBucketName(WorkflowFile.CLOUD_BUCKET)));
            return insertedWorkflow;
            
        } catch (Exception ex) {
            throw new WebApplicationException(ex);
        }
    }
}
