package edu.ucsd.crbs.cws.rest;

import edu.ucsd.crbs.cws.dao.TaskDAO;
import edu.ucsd.crbs.cws.dao.objectify.TaskObjectifyDAOImpl;
import edu.ucsd.crbs.cws.workflow.Task;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

/**
 * REST Service to manipulate workflow Task objects.  
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@Path("/"+Constants.TASKS_PATH)
public class TaskRestService {
    
    private static final Logger log
            = Logger.getLogger(TaskRestService.class.getName());

    TaskDAO _taskDAO;
    
    public TaskRestService() {
        log.info("In constructor of Tasks() rest service");
        _taskDAO = new TaskObjectifyDAOImpl();
    }

    /**
     * HTTP GET call that gets a list of all tasks.  The list can be filtered with various query
     * parameters (ie parameters that are in the end of the url ?status=Running&owner=bob).
     * <p/>
     * Example GET call for Tasks owned by user <b>foo</b> and in running state<p/>
     * 
     * http://.../tasks?status=Running&owner=foo
     * 
     * @param status  Only Tasks with given status are returned (?status=)
     * @param owner  Only Tasks matching this owner are returned (?owner=)
     * @param noParams  Task parameters are stripped from Task objects returned (?noparams=)
     * @param noWorkflowParams Workflow Parameters are stripped from Workflow objects within Task objects returned (?noworkflowparams=)
     * @param notSubmitted Only Tasks that have not been submitted to scheduler are returned (?notsubmittedtoscheduler=)
     *
     * @return List of Task objects in JSON format with media type set to {@link MediaType.APPLICATION_JSON}
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Task> getTasks(@QueryParam(Constants.STATUS_QUERY_PARAM) final String status,
                               @QueryParam(Constants.OWNER_QUERY_PARAM) final String owner,
                               @QueryParam(Constants.NOPARAMS_QUERY_PARAM) final boolean noParams,
                               @QueryParam(Constants.NOWORKFLOWPARAMS_QUERY_PARAM) final boolean noWorkflowParams,
                               @QueryParam(Constants.NOTSUBMITTED_TO_SCHED_QUERY_PARAM) final boolean notSubmitted) {
        
        try {
            return this._taskDAO.getTasks(owner, status, notSubmitted, noParams, noWorkflowParams);
        }
        catch(Exception ex){
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
    public Task getTask(@PathParam("taskid") String taskid) {
        try {
            return _taskDAO.getTaskById(taskid);
        }
        catch(Exception ex){
            throw new WebApplicationException(ex);
        }
    }

    /**
     * Need updateTask method which consumes @POST along with parameters to
     * update Should take a Task, but use a transaction to load and only modify
     * the fields the caller wants changed
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
     * @return 
     */
    @POST
    @Path("/{taskid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)

    public Task updateTask(@PathParam("taskid") final Long taskId,
            @QueryParam("status") final String status,
            @QueryParam("estcpu") final long estCpu,
            @QueryParam("estruntime") final long estRunTime,
            @QueryParam("estdisk") final long estDisk,
            @QueryParam("submitdate") final long submitDate,
            @QueryParam("startdate") final long startDate,
            @QueryParam("finishdate") final long finishDate,
            @QueryParam("submittedtosched") final boolean submittedToScheduler,
            @QueryParam("downloadurl") final String downloadURL) {

        if (taskId != null) {
            log.log(Level.INFO, "task id is: {0}", taskId.toString());
        } else {
            log.info("task id is null.  wtf");
            throw new WebApplicationException();
        }

        try {
            return _taskDAO.update(taskId, status, estCpu, estRunTime, estDisk, 
                    submitDate, startDate, finishDate, submittedToScheduler, 
                    downloadURL);
        }
        catch(Exception ex){
            throw new WebApplicationException(ex);
        }
    }

    /**
     * Creates a new task by consuming JSON version of Task object
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task createTask(Task t) {
        try {
            return _taskDAO.insert(t);
        }
        catch(Exception ex){
            throw new WebApplicationException(ex);
        }
    }

}
