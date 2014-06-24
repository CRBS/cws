package edu.ucsd.crbs.cws.rest;

import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.auth.Authenticator;
import edu.ucsd.crbs.cws.auth.AuthenticatorImpl;
import edu.ucsd.crbs.cws.auth.Permission;
import edu.ucsd.crbs.cws.dao.EventDAO;
import edu.ucsd.crbs.cws.dao.TaskDAO;
import edu.ucsd.crbs.cws.dao.objectify.EventObjectifyDAOImpl;
import edu.ucsd.crbs.cws.dao.objectify.TaskObjectifyDAOImpl;
import edu.ucsd.crbs.cws.log.Event;
import edu.ucsd.crbs.cws.log.EventBuilder;
import edu.ucsd.crbs.cws.log.EventBuilderImpl;
import edu.ucsd.crbs.cws.workflow.Task;
import edu.ucsd.crbs.cws.workflow.validate.TaskValidator;
import edu.ucsd.crbs.cws.workflow.validate.TaskValidatorImpl;
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
@Path(Constants.SLASH + Constants.TASKS_PATH)
public class TaskRestService {

    
    private static final Logger _log
            = Logger.getLogger(TaskRestService.class.getName());

    static TaskDAO _taskDAO = new TaskObjectifyDAOImpl();
    
    static EventDAO _eventDAO = new EventObjectifyDAOImpl();

    static Authenticator _authenticator = new AuthenticatorImpl();

    static EventBuilder _eventBuilder = new EventBuilderImpl();
    
    static TaskValidator _validator = new TaskValidatorImpl();

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
     * @param userLogin
     * @param userToken
     * @param userLoginToRunAs
     * @param request
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
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request) {

        try {
            User user = _authenticator.authenticate(request, userLogin, userToken,
                    userLoginToRunAs);
             Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());
            
            // user can list everything  
            if (user.isAuthorizedTo(Permission.LIST_ALL_TASKS)) {
                return _taskDAO.getTasks(owner, status, notSubmitted, noParams, noWorkflowParams);
            }
            throw new Exception("Not Authorized");

        } catch (Exception ex) {
            _log.log(Level.SEVERE,"Caught Exception",ex);
            throw new WebApplicationException(ex);
        }
    }

    /**
     * Gets a specific Task by id
     *
     * @param taskid Path parameter that denotes id of task to retrieve
     * @param userLogin
     * @param userToken
     * @param userLoginToRunAs
     * @param request
     * @return
     */
    @GET
    @Path(Constants.TASK_ID_REST_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    public Task getTask(@PathParam(Constants.TASK_ID_PATH_PARAM) String taskid,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request) {
        try {
            User user = _authenticator.authenticate(request, userLogin, userToken,
                    userLoginToRunAs);
             Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());

            if (user.isAuthorizedTo(Permission.LIST_ALL_TASKS)) {
                return _taskDAO.getTaskById(taskid);
            }
            throw new Exception("Not authorized");
        } catch (Exception ex) {
            _log.log(Level.SEVERE,"Caught Exception",ex);
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
     * @param userLogin
     * @param userToken
     * @param userLoginToRunAs
     * @param request
     * @return
     */
    @POST
    @Path(Constants.TASK_ID_REST_PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task updateTask(@PathParam(Constants.TASK_ID_PATH_PARAM) final Long taskId,
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
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request) {

        try {
            User user = _authenticator.authenticate(request, userLogin, userToken,
                    userLoginToRunAs);
            Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());
            if (taskId != null) {
                _log.log(Level.INFO, "task id is: {0}", taskId.toString());
            } else {
                _log.info("task id is null.  wtf");
                throw new WebApplicationException();
            }

            if (user.isAuthorizedTo(Permission.UPDATE_ALL_TASKS)) {
                return _taskDAO.update(taskId, status, estCpu, estRunTime, estDisk,
                        submitDate, startDate, finishDate, submittedToScheduler,
                        downloadURL, jobId);
            }

            throw new Exception("Not Authorized");
        } catch (Exception ex) {
            _log.log(Level.SEVERE,"Caught Exception",ex);
            throw new WebApplicationException(ex);
        }
    }

    /**
     * Creates a new task by consuming JSON version of Task object
     *
     * @param t
     * @param userLogin
     * @param userToken
     * @param userLoginToRunAs
     * @param request
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task createTask(Task t,
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
                t = _validator.validateParameters(t,user);
                // @TODO add failed create task event
                if (t.getError() != null || t.getParametersWithErrors() != null){
                    _log.log(Level.WARNING,"Validation of Task failed: {0}",
                            t.getSummaryOfErrors());
                    return t;
                }
                
                //do insert, but skip the workflow checks cause validation did it 
                //already
                Task task = _taskDAO.insert(t,true);
                
                saveEvent(_eventBuilder.setAsCreateTaskEvent(event, task));
                
                return task;
            }
            throw new Exception("Not Authorized");
        } catch (Exception ex) {
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
