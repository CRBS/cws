package edu.ucsd.crbs.cws.dao;

import edu.ucsd.crbs.cws.workflow.Task;
import java.util.List;

/**
 * Interface that defines methods to retrieve and persist Task objects to
 * a data store
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public interface TaskDAO {
    
    /**
     * Gets Task from Data Store by querying using taskId passed in.  
     * @param taskId
     * @return 
     * @throws Exception 
     */
    public Task getTaskById(final String taskId) throws Exception;
    
    /**
     * Gets tasks using parameters as filters
     * @param owner If non null only Tasks with matching owners will be returned
     * @param status If non null only Tasks with matching status will be returned
     * @param notSubmittedToScheduler If non null only Tasks matching submitted to scheduler flag will be returned
     * @param noParams True means to exclude Parameters in returned Tasks
     * @param noWorkflowParams True means to exclude WorkflowParameters in Workflow objects returned with Tasks
     * @return
     * @throws Exception 
     */
    public List<Task> getTasks(final String owner,final String status, final Boolean notSubmittedToScheduler,
            boolean noParams,boolean noWorkflowParams) throws Exception;

    /**
     * Inserts a new task into data store skipping Workflow Check
     * @param t
     * @return
     * @throws Exception 
     */
    public Task insert(Task t,boolean skipWorkflowCheck) throws Exception;
    
    /**
     * Updates existing task with values passed in.  
     * @param taskId
     * @param status
     * @param estCpu
     * @param estRunTime
     * @param estDisk
     * @param submitDate
     * @param startDate
     * @param finishDate
     * @param submittedToScheduler
     * @param jobId
     * @return
     * @throws Exception 
     */
    public Task update(long taskId,final String status,Long estCpu,Long estRunTime,
            Long estDisk,Long submitDate,Long startDate,Long finishDate,
            Boolean submittedToScheduler,final String jobId) throws Exception;
    
    
    /**
     * Loads and resaves {@link Task} with given <b>taskId</b>
     * @param taskId
     * @return Resaved {@link Task}
     * @throws Exception 
     */
    public Task resave(long taskId) throws Exception;
}
