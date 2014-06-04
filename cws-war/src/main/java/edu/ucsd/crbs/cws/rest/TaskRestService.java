package edu.ucsd.crbs.cws.rest;

import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.auth.Authenticator;
import edu.ucsd.crbs.cws.auth.AuthenticatorImpl;
import edu.ucsd.crbs.cws.auth.Permission;
import edu.ucsd.crbs.cws.dao.TaskDAO;
import edu.ucsd.crbs.cws.dao.objectify.TaskObjectifyDAOImpl;
import edu.ucsd.crbs.cws.workflow.Task;
import java.util.ArrayList;
import java.util.Enumeration;
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
 * REST Service to manipulate workflow Task objects.
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@Path("/" + Constants.TASKS_PATH)
public class TaskRestService {

    private static final Logger log
            = Logger.getLogger(TaskRestService.class.getName());

    TaskDAO _taskDAO;

    static Authenticator _authenticator = new AuthenticatorImpl();
    
    public TaskRestService() {
        _taskDAO = new TaskObjectifyDAOImpl();
    }

    /**
     * HTTP GET call that gets a list of all tasks. The list can be filtered
     * with various query parameters (ie parameters that are in the end of the
     * url ?status=Running&owner=bob).
     * <p/>
     * Example GET call for Tasks owned by user <b>foo</b> and in running
     * state<p/>
     *
     * http://.../tasks?status=Running&owner=foo
     *
     * @param status Only Tasks with given status are returned (?status=)
     * @param owner Only Tasks matching this owner are returned (?owner=)
     * @param noParams Task parameters are stripped from Task objects returned
     * (?noparams=)
     * @param noWorkflowParams Workflow Parameters are stripped from Workflow
     * objects within Task objects returned (?noworkflowparams=)
     * @param notSubmitted Only Tasks that have not been submitted to scheduler
     * are returned (?notsubmittedtoscheduler=)
     *
     * @return List of Task objects in JSON format with media type set to
     * {@link MediaType.APPLICATION_JSON}
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Task> getTasks(@QueryParam(Constants.STATUS_QUERY_PARAM) final String status,
            @QueryParam(Constants.OWNER_QUERY_PARAM) final String owner,
            @QueryParam(Constants.NOPARAMS_QUERY_PARAM) final boolean noParams,
            @QueryParam(Constants.NOWORKFLOWPARAMS_QUERY_PARAM) final boolean noWorkflowParams,
            @QueryParam(Constants.NOTSUBMITTED_TO_SCHED_QUERY_PARAM) final boolean notSubmitted,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @Context HttpServletRequest request) {

        try {
            User user = _authenticator.authenticate(request,userLogin,userToken);
            logRequest(request);
           
            // user can list everything  
            if (user.isAuthorizedTo(Permission.LIST_ALL_TASKS)){
                return this._taskDAO.getTasks(owner, status, notSubmitted, noParams, noWorkflowParams);
            }
            throw new Exception("Not Authorized");
            
        } catch (Exception ex) {
            throw new WebApplicationException(ex);
        }
    }

    /**
     * Gets a specific Task by id
     *
     * @param taskid Path parameter that denotes id of task to retrieve
     * @return
     */
    @GET
    @Path("/{taskid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Task getTask(@PathParam("taskid") String taskid,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @Context HttpServletRequest request) {
        try {
            User user = _authenticator.authenticate(request,userLogin,userToken);
            logRequest(request);
            
            if (user.isAuthorizedTo(Permission.LIST_ALL_TASKS)){
                return _taskDAO.getTaskById(taskid);
            }
            throw new Exception("Not authorized");
        } catch (Exception ex) {
            throw new WebApplicationException(ex);
        }
    }

    /**
     * Need updateTask method which consumes @POST along with parameters to
     * update Should take a Task, but use a transaction to load and only modify
     * the fields the caller wants changed
     *
     * @param taskId
     * @param status
     * @param estCpu
     * @param estRunTime
     * @param downloadURL
     * @param submitDate
     * @param startDate
     * @param estDisk
     * @param finishDate
     * @param submittedToScheduler
     * @param jobId
     * @return
     */
    @POST
    @Path("/{taskid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)

    public Task updateTask(@PathParam("taskid") final Long taskId,
            @QueryParam(Constants.STATUS_QUERY_PARAM) final String status,
            @QueryParam(Constants.ESTCPU_QUERY_PARAM) final Long estCpu,
            @QueryParam(Constants.ESTRUNTIME_QUERY_PARAM) final Long estRunTime,
            @QueryParam(Constants.ESTDISK_QUERY_PARAM) final Long estDisk,
            @QueryParam(Constants.SUBMITDATE_QUERY_PARAM) final Long submitDate,
            @QueryParam(Constants.STARTDATE_QUERY_PARAM) final Long startDate,
            @QueryParam(Constants.FINISHDATE_QUERY_PARAM) final Long finishDate,
            @QueryParam(Constants.SUBMITTED_TO_SCHED_QUERY_PARAM) final Boolean submittedToScheduler,
            @QueryParam(Constants.DOWNLOADURL_QUERY_PARAM) final String downloadURL,
            @QueryParam(Constants.JOB_ID_QUERY_PARAM) final String jobId,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @Context HttpServletRequest request) {

        logRequest(request);
        if (taskId != null) {
            log.log(Level.INFO, "task id is: {0}", taskId.toString());
        } else {
            log.info("task id is null.  wtf");
            throw new WebApplicationException();
        }
            
        try {
             User user = _authenticator.authenticate(request,userLogin,userToken);
            logRequest(request);
            if (user.isAuthorizedTo(Permission.UPDATE_ALL_TASKS)){
                return _taskDAO.update(taskId, status, estCpu, estRunTime, estDisk,
                        submitDate, startDate, finishDate, submittedToScheduler,    
                        downloadURL, jobId);
            }
            
            throw new Exception("Not Authorized");
        } catch (Exception ex) {
            throw new WebApplicationException(ex);
        }
    }

    /**
     * Creates a new task by consuming JSON version of Task object
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task createTask(Task t,
           @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
           @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
           @Context HttpServletRequest request) {
        try {
             User user = _authenticator.authenticate(request,userLogin,userToken);
            logRequest(request);
            if (user.isAuthorizedTo(Permission.CREATE_TASK)){
                return _taskDAO.insert(t);
            }
            throw new Exception("Not Authorized");
        } catch (Exception ex) {
            throw new WebApplicationException(ex);
        }
    }

    public static void logRequest(HttpServletRequest request) {
        if (request != null) {
            String requestorIp = request.getRemoteAddr();
            if (requestorIp != null) {
                log.log(Level.INFO, "requestor ip: {0}", requestorIp);
            }

            Enumeration e = request.getHeaderNames();
            String header = null;
            if (e != null) {
                for (; e.hasMoreElements();) {
                    header = (String) e.nextElement();
                    String val = request.getHeader(header);
                    if (val != null) {
                        log.log(Level.INFO, "Header: " + header + " == " + val);
                    }
                }
            }
        }
    }

}
