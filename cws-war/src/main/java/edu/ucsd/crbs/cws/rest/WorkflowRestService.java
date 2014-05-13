package edu.ucsd.crbs.cws.rest;

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
import javax.ws.rs.WebApplicationException;


/**
 * REST Service that allows caller to create, modify, and retrieve Workflow
 * objects.  
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@Path("/"+Constants.WORKFLOWS_PATH)
public class WorkflowRestService {

    
    private static final Logger log =
      Logger.getLogger(WorkflowRestService.class.getName());

    WorkflowDAO _workflowDAO;
    
    /**
     * Constructor that by default creates Objectify DAO objects
     */
    public WorkflowRestService(){
        log.info("In constructor of Workflows() rest service");
        
        _workflowDAO = new WorkflowObjectifyDAOImpl();
    }
    
    /**
     * HTTP GET request on /workflows URI that returns a list of all Workflow 
     * objects from WorkflowDAO.  If none are found an empty list is returned.  
     * If there is an error a 500 response is returned
     * 
     * @return List of Workflow objects in JSON format
     */
     @GET
     @Produces(MediaType.APPLICATION_JSON)
     public List<Workflow> getWorkflows(){
        List<Workflow> workflows = null;
        try {
            workflows = _workflowDAO.getAllWorkflows(true);
        }
        catch(Exception ex){
            throw new WebApplicationException(ex);
        }
        return workflows;
     }
     
  /**
   * Gets a specific Workflow by id
   * @param wfid
   * @return 
   */
  @GET
  @Path("/{wfid}")
  @Produces(MediaType.APPLICATION_JSON)
  public Workflow getWorkflow(@PathParam("wfid") String wfid) {
      
      Workflow wf = null;
      try {
        wf = _workflowDAO.getWorkflowById(wfid);
      }
      catch(Exception ex){
          throw new WebApplicationException(ex);
      }
      return wf;
  }
  
  /**
   * Creates a new workflow by consuming JSON version of Workflow object
   * @param w
   * @return 
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Workflow createWorkflow(Workflow w){
      try {
          return _workflowDAO.insert(w);
      }
      catch (Exception ex){
          throw new WebApplicationException(ex);
      }
  }
}
