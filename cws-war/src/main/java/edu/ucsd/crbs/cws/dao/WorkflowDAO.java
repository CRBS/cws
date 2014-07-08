package edu.ucsd.crbs.cws.dao;

import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.workflow.Task;
import edu.ucsd.crbs.cws.workflow.Workflow;
import java.util.List;

/**
 * Interface that defines methods to retrieve, save, and modify {@link Workflow} objects
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public interface WorkflowDAO {
   
    /**
     * Queries data store to retrieve workflow identified by workflowId
     * @param workflowId
     * @param user
     * @param userName
     * @return Workflow object upon success or null if none is found
     * @throws Exception If the workflowId is invalid, null, or there is a problem in parsing
     */
    public Workflow getWorkflowById(final String workflowId,
            User user) throws Exception;
    
    /**
     * Gets all workflows from data store
     * @param omitWorkflowParams If set to true then WorkflowParameters will be set to null for every Workflow object returned
     * @return List of Workflow objects if Workflows are found otherwise an empty list or null
     * @throws Exception If there was an error retrieving the Workflows
     */
    public List<Workflow> getAllWorkflows(boolean omitWorkflowParams) throws Exception;
    
    /**
     * Adds a new workflow to the data store.  If the Id of the Workflow is set then the
     * parent field of this Workflow object is set to that Id and this Workflow is given
     * a new Id.
     * 
     * @param w Workflow to add to data store
     * @return Workflow object with new id added
     * @throws Exception If there was an error during persistence
     */
    public Workflow insert(Workflow w) throws Exception;
    
    /**
     * Updates BlobKey for Given Workflow
     * @param worklfowId
     * @param key
     * @return
     * @throws Exception 
     */
    public Workflow updateBlobKey(long workflowId,final String key) throws Exception;

    /**
     * Given a <b>task</b> this method looks at the {@link Workflow} object within
     * and uses its id to load the {@link Workflow} from the data store.  
     * @param task
     * @param user
     * @throws Exception if there is no {@link Workflow} object with id
     * @return 
     */
    public Workflow getWorkflowForTask(Task task,User user) throws Exception;
}
