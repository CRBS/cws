package edu.ucsd.crbs.cws.dao.objectify;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.cmd.Query;
import edu.ucsd.crbs.cws.dao.TaskDAO;
import static edu.ucsd.crbs.cws.dao.objectify.OfyService.ofy;
import edu.ucsd.crbs.cws.workflow.Task;
import edu.ucsd.crbs.cws.workflow.Workflow;
import java.util.Date;
import java.util.List;

/**
 * Implements TaskDAO interface which provides means to load and
 * save Task objects to Google NoSQL data store via Objectify.
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class TaskObjectifyDAOImpl implements TaskDAO {

    @Override
    public Task getTaskById(final String taskId) throws Exception {
        long taskIdAsLong;
        try {
            taskIdAsLong = Long.parseLong(taskId);
        }
        catch(NumberFormatException nfe){
            throw new Exception(nfe);
        }
        return ofy().load().type(Task.class).id(taskIdAsLong).now();
    }

    @Override
    public List<Task> getTasks(String owner, String status, Boolean notSubmittedToScheduler, boolean noParams, boolean noWorkflowParams) throws Exception {
        Query<Task> q = ofy().load().type(Task.class);
        
        if (status != null){
            q = q.filter("_status",status);
        }
        if (owner != null){
            q = q.filter("_owner",owner);
        }
        if (notSubmittedToScheduler == true){
            q = q.filter("_hasJobBeenSubmittedToScheduler",false);
        }
        
        if (noParams == false && noWorkflowParams == false){
            return q.list();
        }
        
        List<Task> tasks = q.list();
        for (Task t : tasks){
            if (noParams == true){
                t.setParameters(null);
            }
            if (noWorkflowParams == true){
                Workflow w = t.getWorkflow();
                if (w != null){
                    w.setParameters(null);
                    w.setParentWorkflow(null);
                }
            }
        }
        return tasks;
    }

    /**
     * Creates a new Task in the data store
     * @param task Task to insert
     * @return Task object with id updated
     * @throws Exception 
     */
    @Override
    public Task insert(Task task) throws Exception {
        if (task == null){
            throw new NullPointerException("Task is null");
        }
           if (task.getCreateDate() == null) {
            task.setCreateDate(new Date());
        }

        if (task.getWorkflow() == null) {
            throw new Exception("Task Workflow cannot be null");
        }

        if (task.getWorkflow().getId() == null || task.getWorkflow().getId().longValue() <= 0) {
            throw new Exception("Task Workflow id is either null or 0 or less which is not valid");
        }

        //try to load the workflow and only if we get a workflow do we try to save
        //the task otherwise it is an error
        Workflow wf = ofy().load().type(Workflow.class).id(task.getWorkflow().getId()).now();
        if (wf == null) {
            throw new Exception("Unable to load Workflow for Task");
        }
        
        /**
         * @TODO Need to verify the Task Parameters match the Workflow parameters
         * and that valid values are set for each of those parameters
         */

        Key<Task> tKey = ofy().save().entity(task).now();
        
        return task;
    }

    @Override
    public Task update(final long taskId, final String status, final Long estCpu, 
            final Long estRunTime, final Long estDisk, final Long submitDate, 
            final Long startDate, final Long finishDate, 
            final Boolean submittedToScheduler,final String downloadURL,
            final String jobId) throws Exception {

        Task resTask;
        resTask = ofy().transact(new Work<Task>() {
            @Override
            public Task run() {

                Task task = ofy().load().type(Task.class).id(taskId).now();

                if (task == null) {
                    return null;
                }
                boolean taskNeedsToBeSaved = false;
                if (status != null && status.isEmpty() == false) {
                    task.setStatus(status);
                    taskNeedsToBeSaved = true;
                }
                if (estCpu != null && task.getEstimatedCpuInSeconds() != estCpu) {
                    task.setEstimatedCpuInSeconds(estCpu);
                    taskNeedsToBeSaved = true;
                }
                if (estRunTime != null && task.getEstimatedRunTime() != estRunTime) {
                    task.setEstimatedRunTime(estRunTime);
                    taskNeedsToBeSaved = true;
                }
                if (estDisk != null && task.getEstimatedDiskInBytes() != estDisk) {
                    task.setEstimatedDiskInBytes(estDisk);
                    taskNeedsToBeSaved = true;
                }

                if (submitDate != null) {
                    task.setSubmitDate(new Date(submitDate));
                    taskNeedsToBeSaved = true;
                }
                if (startDate != null) {
                    task.setStartDate(new Date(startDate));
                    taskNeedsToBeSaved = true;
                }
                if (finishDate != null) {
                    task.setFinishDate(new Date(finishDate));
                    taskNeedsToBeSaved = true;
                }

                if (submittedToScheduler != null && submittedToScheduler != task.getHasJobBeenSubmittedToScheduler()) {
                    task.setHasJobBeenSubmittedToScheduler(submittedToScheduler);
                    taskNeedsToBeSaved = true;
                }
                if (downloadURL != null) {
                    if (task.getDownloadURL() == null || !task.getDownloadURL().equals(downloadURL)) {
                        task.setDownloadURL(downloadURL);
                        taskNeedsToBeSaved = true;
                    }
                }
                if (jobId != null){
                    if (task.getJobId() == null || !task.getJobId().equals(jobId)){
                        task.setJobId(jobId);
                        taskNeedsToBeSaved = true;
                    }
                }

                if (taskNeedsToBeSaved == true) {
                    Key<Task> tKey = ofy().save().entity(task).now();
                }
                return task;
            }
        });
        if (resTask == null){
            throw new Exception("There was a problem updating the Task");
        }
        return resTask;
    }

}
