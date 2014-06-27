package edu.ucsd.crbs.cws.log;

import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.workflow.Task;
import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import javax.servlet.http.HttpServletRequest;

/**
 * Creates, Logs, and persists Event objects
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public interface EventBuilder {
  
    
    /**
     * Logs the request
     * @param request
     * @param user 
     */
    public Event createEvent(HttpServletRequest request,User user);
    
    /**
     * Converts Event into what is known as a Create Task Event
     * @param event
     * @param task
     * @return Same Event object passed in unless there was a problem in which case null is returned
     */
    public Event setAsCreateTaskEvent(Event event,Task task);
    
    /**
     * Converts Event into what is known as a failed create task event
     * @param event
     * @param task
     * @return 
     */
    public Event setAsFailedCreateTaskEvent(Event event,Task task);
    
    /**
     * Converts Event into what is known as a Create Workflow Event
     * @param event
     * @param workflow
     * @return Same Event object passed in unless there was a problem in which case null is returned
     */
    public Event setAsCreateWorkflowEvent(Event event,Workflow workflow);
    
    public Event setAsCreateWorkspaceFileEvent(Event event,WorkspaceFile workspaceFile);
}
