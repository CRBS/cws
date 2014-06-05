package edu.ucsd.crbs.cws.log;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import java.util.Date;

/**
 * Instances of this class represent a notable operation that has occurred 
 * due to a Web request.  <p/>
 * The constants defined in this class are the the common types of Events
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@Entity
public class Event {
    
    /**
     * Creation of a Task
     */
    public static final String CREATE_TASK_EVENT_TYPE = "createtask";
    
    /**
     * Creation of a Workflow
     */
    public static final String CREATE_WORKFLOW_EVENT_TYPE = "createworkflow";
    
    @Id private Long _id;
    @Index private Date _date;
    @Index private String _ipAddress;
    private String _host;
    private String _userAgent;
    private String _country;
    private String _region;
    private String _city;
    private String _cityLatLong;
    private String _eventType;
    private Long _workflowId;
    private Long _taskId;
    private Long _userId;
    private String _message;
    
    /**
     * Gets Date Event was created
     * @return 
     */
    public Date getDate(){
        return _date;
    }
    
    /**
     * Sets Date event was created
     * @param date 
     */
    public void setDate(final Date date){
        _date = date;
    }
    
    /**
     * Gets requestors ip address
     * @return 
     */
    public String getIpAddress() {
        return _ipAddress;
    }

    /**
     * Sets requestors ip address
     * @param ipAddress 
     */
    public void setIpAddress(final String ipAddress) {
        _ipAddress = ipAddress;
    }

    /**
     * Gets the host the request was made on
     * @return 
     */
    public String getHost() {
        return _host;
    }

    /**
     * Sets the host the request was made on
     * @param host 
     */
    public void setHost(final String host) {
        this._host = host;
    }

    /**
     * Gets requestor user agent information
     * @return 
     */
    public String getUserAgent() {
        return _userAgent;
    }

    /**
     * Sets requestor user agent information
     * @param userAgent 
     */
    public void setUserAgent(final String userAgent) {
        this._userAgent = userAgent;
    }

    /**
     * Gets country where request was made from
     * @return 
     */
    public String getCountry() {
        return _country;
    }

    /**
     * Sets country where request was made from
     * @param country 
     */
    public void setCountry(final String country) {
        this._country = country;
    }

    /**
     * Gets region where request was made from
     * @return 
     */
    public String getRegion() {
        return _region;
    }

    /**
     * Sets region where request was made from
     * @param region 
     */
    public void setRegion(String region) {
        this._region = region;
    }
    
    /**
     * Gets city where request was made from
     * @return 
     */
    public String getCity() {
        return _city;
    }

    /**
     * Sets city where request was made from
     * @param city 
     */
    public void setCity(final String city) {
        this._city = city;
    }

    /**
     * Gets latitude and longitude of city where
     * request was made from
     * @return 
     */
    public String getCityLatLong() {
        return _cityLatLong;
    }

    /**
     * Sets latitude and longitude of city where
     * request was made from
     * @param cityLatLong 
     */
    public void setCityLatLong(final String cityLatLong) {
        this._cityLatLong = cityLatLong;
    }

    /**
     * Gets type of event for this request which are denoted by constant strings in this
     * class
     * @return 
     */
    public String getEventType() {
        return _eventType;
    }

    /**
     * Sets type of event for this request.  This should be one of the constant
     * strings defined in this class.
     * @param eventType 
     */
    public void setEventType(final String eventType) {
        this._eventType = eventType;
    }

    /**
     * Gets id of {@link User} object pertaining to this Event
     * @return 
     */
    public Long getUserId() {
        return _userId;
    }

    /**
     * Sets id of User object pertaining to this Event
     * @param userId 
     */
    public void setUserId(Long userId) {
        _userId = userId;
    }

    /**
     * Gets id of {@link Workflow} object pertaining to this Event
     * @return 
     */
    public Long getWorkflowId() {
        return _workflowId;
    }

    /**
     * Sets id of {@link Workflow} object pertaining to this Event
     * @param workflowId 
     */
    public void setWorkflowId(Long workflowId) {
        this._workflowId = workflowId;
    }

    /**
     * Gets id of Task object pertaining to this Event
     * @return 
     */
    public Long getTaskId() {
        return _taskId;
    }

    /**
     * Sets id of Task object pertaining to this Event
     * @param taskId 
     */
    public void setTaskId(Long taskId) {
        this._taskId = taskId;
    }

    /**
     * Gets custom message for this event
     * @return 
     */
    public String getMessage() {
        return _message;
    }

    /**
     * Sets custom message for this event
     * @param message
     */
    public void setMessage(final String message) {
        this._message = message;
    }
    
    /**
     * Sets id for this object
     * @param id 
     */
    public void setId(Long id){
        _id = id;
    }
    
    /**
     * Gets id for this object
     * @return 
     */
    public Long getId(){
        return _id;
    }
    
    /**
     * Creates a human readable string of the location data in this object<p/>
     * Format: <br/>
     * location: (Latitude and Longitude) -- (City), (Region), (Country)<p/>
     * 
     * @return String in format above.  If values are not set in object spaces will be put in their stead
     */
    public String getStringOfLocationData(){
        StringBuilder sb = new StringBuilder();
        
        if (getCityLatLong() != null){
            sb.append("location: ").append(getCityLatLong());
        }
        if (getCity() != null){
            sb.append(" -- ").append(getCity()).append(" ");
        }
        
        if (this.getRegion() != null){
            sb.append(", ").append(getRegion()).append(" ");
        }
        if (this.getCountry() != null){
            sb.append(" --- ").append(getCountry());
        }
        return sb.toString();
    }
}
