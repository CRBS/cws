/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2014 The Regents of the University of California All Rights Reserved
 * 
 * Permission to copy, modify and distribute any part of this CRBS Workflow 
 * Service for educational, research and non-profit purposes, without fee, and
 * without a written agreement is hereby granted, provided that the above 
 * copyright notice, this paragraph and the following three paragraphs appear
 * in all copies.
 * 
 * Those desiring to incorporate this CRBS Workflow Service into commercial 
 * products or use for commercial purposes should contact the Technology
 * Transfer Office, University of California, San Diego, 9500 Gilman Drive, 
 * Mail Code 0910, La Jolla, CA 92093-0910, Ph: (858) 534-5815, 
 * FAX: (858) 534-7345, E-MAIL:invent@ucsd.edu.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR 
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING 
 * LOST PROFITS, ARISING OUT OF THE USE OF THIS CRBS Workflow Service, EVEN IF 
 * THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 * 
 * THE CRBS Workflow Service PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE
 * UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, 
 * UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES
 * NO REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR 
 * EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF 
 * THE CRBS Workflow Service WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER
 * RIGHTS. 
 */

package edu.ucsd.crbs.cws.log;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.condition.IfNotNull;
import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.workflow.Workflow;
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
     * Creation of a {@link Job}
     */
    public static final String CREATE_JOB_EVENT_TYPE = "createjob";
    
     
    /**
     * Creation of a User
     */
    public static final String CREATE_USER_EVENT_TYPE = "createuser";
    
    /**
     * Failed creation of a {@link Job}
     */
    public static final String FAILED_CREATE_JOB_EVENT_TYPE = "failedcreatejob";
    
    
    /**
     * Creation of a Workflow
     */
    public static final String CREATE_WORKFLOW_EVENT_TYPE = "createworkflow";
    
    public static final String CREATE_WORKSPACEFILE_EVENT_TYPE = "createworkspacefile";
    
    /**
     * Logical deletion of a Workflow
     */
    public static final String LOGICAL_DELETE_WORKFLOW_EVENT_TYPE = "logicaldeleteworkflow";
    
    /**
     * Deletion of a Workflow
     */
    public static final String DELETE_WORKFLOW_EVENT_TYPE = "deleteworkflow";
    
    
    
    @Id private Long _id;
    @Index private Date _date;
    @Index private String _ipAddress;
    private String _host;
    private String _userAgent;
    private String _country;
    private String _region;
    private String _city;
    private String _cityLatLong;
    @Index({IfNotNull.class}) private String _eventType;
    @Index({IfNotNull.class}) private Long _workflowId;
    @Index({IfNotNull.class}) private Long _jobId;
    @Index({IfNotNull.class}) private Long _userId;
    @Index({IfNotNull.class}) private Long _createdUserId;
    @Index({IfNotNull.class}) private Long _workspaceFileId;
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
    public Long getJobId() {
        return _jobId;
    }

    /**
     * Sets id of Job object pertaining to this Event
     * @param jobId 
     */
    public void setJobId(Long jobId) {
        this._jobId = jobId;
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
    
     public Long getWorkspaceFileId() {
        return _workspaceFileId;
    }

    public void setWorkspaceFileId(Long _workspaceFileId) {
        this._workspaceFileId = _workspaceFileId;
    }
    
    
    public Long getCreatedUserId() {
        return _createdUserId;
    }

    public void setCreatedUserId(Long createdUserId) {
        _createdUserId = createdUserId;
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
