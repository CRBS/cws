package edu.ucsd.crbs.cws.log;

import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.workflow.Task;
import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

/**
 * Creates Event objects and sets appropriate fields to denote different
 * types of Events
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class EventBuilderImpl implements EventBuilder {

   

    /**
     * HttpServletRequest Header Parameter showing what tool the requestor used
     * to make the web request
     */
    public static final String USER_AGENT_HEADER = "User-Agent";
    
    /**
     * HttpServletRequest Header Parameter to get host that responded to request
     */
    public static final String HOST_HEADER = "Host";

    /**
     * GAE engine HttpServletRequest Header Parameter name to get city
     * where request originated
     */
    public static final String CITY_HEADER = "X-AppEngine-City";

    
    /**
     * GAE engine HttpServletRequest Header Parameter name to get region (in U.S. its the state)
     * where request originated
     */
    public static final String REGION_HEADER = "X-AppEngine-Region";

    /**
     * GAE engine HttpServletRequest Header Parameter name to get country
     * where request originated
     */
    public static final String COUNTRY_HEADER = "X-AppEngine-Country";

    /**
     * GAE engine HttpServletRequest Header Parameter name to get latitude and longitude of the city
     * where request originated
     */
    public static final String CITY_LAT_LONG_HEADER = "X-AppEngine-CityLatLong";

    /**
     * Logger
     */
    private static final Logger _log
            = Logger.getLogger(EventBuilderImpl.class.getName());

    /**
     * Creates a new Event by parsing parameters out of HttpServletRequest and User
     * objects passed in.  
     * @param request
     * @param user
     * @return New Event with location data and if possible user information set, note Id is NOT set
     */
    @Override
    public Event createEvent(HttpServletRequest request, User user) {
        Event event = new Event();
        
        if (user != null){
            event.setIpAddress(user.getIpAddress());
            event.setUserId(user.getId());
        }
        else {
            _log.warning("User object is null");
        }
        
        if (request != null){
            event.setUserAgent(request.getHeader(USER_AGENT_HEADER));
            event.setHost(request.getHeader(HOST_HEADER));
            event.setCity(request.getHeader(CITY_HEADER));
            event.setRegion(request.getHeader(REGION_HEADER));
            event.setCountry(request.getHeader(COUNTRY_HEADER));
            event.setCityLatLong(request.getHeader(CITY_LAT_LONG_HEADER));
        }
        
        return event;
    }

    /**
     * Logs task creation event
     * @param event
     * @param task 
     * @return  
     */
    @Override
    public Event setAsCreateTaskEvent(Event event, Task task) {
        
        if (anyOfTheseObjectsNull(event,task) == true){
            _log.log(Level.WARNING,"One or more parameters passed in is null.  Unable to log event.");
            return null;
        }
        event.setTaskId(task.getId());
        event.setEventType(Event.CREATE_TASK_EVENT_TYPE);
        event.setDate(task.getCreateDate());
        
        return event;
    }

    @Override
    public Event setAsCreateWorkflowEvent(Event event, Workflow workflow) {
        if (anyOfTheseObjectsNull(event,workflow) == true){
            _log.log(Level.WARNING,"One or more parameters passed in is null.  Unable to log event.");
            return null;
        }

        event.setWorkflowId(workflow.getId());
        event.setEventType(Event.CREATE_WORKFLOW_EVENT_TYPE);
        event.setDate(workflow.getCreateDate());
        
        return event;
        
    }
    
    @Override
    public Event setAsCreateWorkspaceFileEvent(Event event, WorkspaceFile workspaceFile) {
        if (anyOfTheseObjectsNull(event,workspaceFile) == true){
            _log.log(Level.WARNING,"One or more parameters passed in is null.  Unable to log event.");
            return null;
        }
         event.setWorkspaceFileId(workspaceFile.getId());
         event.setEventType(Event.CREATE_WORKSPACEFILE_EVENT_TYPE);
         event.setDate(workspaceFile.getCreateDate());
         return event;
    }
    
    
    
    /**
     * Checks if any of the Objects passed in is null.  If yes true is returned
     * @param objects Objects to check
     * @return true if no objects are passed in or if objects are null or if one or more objects is null
     */
    private boolean anyOfTheseObjectsNull(Object...objects){
        if (objects == null){
            return true;
        }
        if (objects.length < 1){
            return true;
        }
        
        for (Object o : objects){
            if (o == null){
                return true;
            }
        }
        return false;
    }
}
