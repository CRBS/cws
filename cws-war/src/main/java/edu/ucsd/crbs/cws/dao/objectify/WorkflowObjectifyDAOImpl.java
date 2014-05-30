package edu.ucsd.crbs.cws.dao.objectify;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import edu.ucsd.crbs.cws.dao.WorkflowDAO;
import static edu.ucsd.crbs.cws.dao.objectify.OfyService.ofy;
import edu.ucsd.crbs.cws.workflow.Task;
import edu.ucsd.crbs.cws.workflow.Workflow;
import java.util.Date;
import java.util.List;

/**
 * Provides access methods to load, modify, and save Workflow objects to Google App
 * Engine Data store using the Objectify library
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class WorkflowObjectifyDAOImpl implements WorkflowDAO {

    /**
     * Adds a new workflow to the data store.  If the Id of the Workflow is set then the
     * parent field of this Workflow object is set to that Id and this Workflow is given
     * a new Id.
     * 
     * @param w Workflow to add to data store
     * @return Workflow object with new id added
     * @throws Exception If there was an error during persistence
     */
    @Override
    public Workflow insert(Workflow w) throws Exception {
        if (w == null){
          throw new Exception("Workflow object passed in is null");
          }
     
        //set date if it is null
      if (w.getCreateDate() == null){
          w.setCreateDate(new Date());
      }
      
      //if version is 0 or less set it to 1
      if (w.getVersion() <= 0){
          w.setVersion(1);
      }
      
      // seems a user is uploading the same workflow twice. 
      // lets create a new workflow using the id to load
      // the old workflow and set it as the parent for this
      // new workflow
      if (w.getId() != null && w.getId().longValue() > 0){
          Workflow parentWf = ofy().load().type(Workflow.class).id(w.getId()).now();
          if (parentWf == null){
              throw new Exception("Unable to load parent Workflow with id: "+w.getId().toString());
          }
          w.setId(null);
          w.setParentWorkflow(parentWf);
          int newVersion = w.getVersion()+1;
          w.setVersion(newVersion);
      }
      
      Key<Workflow> wfKey = ofy().save().entity(w).now();
      return w;
    
    }

    /**
     * Obtains all Workflows from data store, optionally removing the WorkflowParameters
     * if omitWorkflowParams is set to true
     * @param omitWorkflowParams If set to true then WorkflowParameters is set to null
     * @return List of Workflow objects or empty list or null
     */
    @Override
    public List<Workflow> getAllWorkflows(boolean omitWorkflowParams) {
          /* @TODO figure out way to make objectify optionally retreive workflow parameters instead of removing them here */
         List<Workflow> workflows = ofy().load().type(Workflow.class).list();
         if (omitWorkflowParams == false){
             return workflows;
         }
         
         for (Workflow w : workflows){
             w.setParameters(null);
         }
         return workflows;
    }

    /**
     * Queries Objectify to get Workflow matching id passed in.  
     * @param workflowId String containing workflow id.  Id must be greater then 0
     * @return Workflow object or null if none is found
     * @throws Exception If workflowId is null or there is an error parsing the numerical id or if there was an error retrieving from Objectify
     */
    @Override
    public Workflow getWorkflowById(final String workflowId) throws Exception {
        long wfId;
        if (workflowId == null){
            throw new IllegalArgumentException("workflow id cannot be null");
        }
        try {
            wfId = Long.parseLong(workflowId);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException("Unable to parse workflow id from: " + workflowId + " : " + nfe.getMessage());
        }
        
        if (wfId <= 0){
            throw new Exception(Long.toString(wfId)+" is not a valid workflow id");
        }
        return ofy().load().type(Workflow.class).id(wfId).now();
    }

    
    @Override
    public Workflow updateBlobKey(final long workflowId,final String key) throws Exception {
        Workflow resWorkflow;
        resWorkflow = ofy().transact(new Work<Workflow>() {
            @Override
            public Workflow run() {
                Workflow w = ofy().load().type(Workflow.class).id(workflowId).now();

                if (w == null) {
                    return null;
                }
                
                if (w.getBlobKey() == null && key == null){
                   return w;
                }
                
                if (w.getBlobKey() != null && key != null && 
                        w.getBlobKey().equals(key)){
                    return w;
                }
                w.setBlobKey(key);
                Key<Workflow> wKey = ofy().save().entity(w).now();
                return w;
            }
        });
        if (resWorkflow == null){
            throw new Exception("There was a problem updating the workflow");
        }
        return resWorkflow;
    }
}
