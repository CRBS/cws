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

import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import java.util.Date;
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
     * @param val
     * @return returns string "null" if <b>val</b> is null otherwise returns <b>val</b>
     */
    private String nullSafeString(final String val){
        if (val == null){
            return "null";
        }
        return val;
    }
    
    private String nullSafeDateString(final Date date){
        if (date == null){
            return "null";
        }
        return date.toString();
    }
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
     * Logs Job creation event
     * @param event
     * @param job 
     * @return  
     */
    @Override
    public Event setAsCreateJobEvent(Event event, Job job) {
        
        if (anyOfTheseObjectsNull(event,job) == true){
            _log.log(Level.WARNING,"One or more parameters passed in is null.  Unable to log event.");
            return null;
        }
        event.setJobId(job.getId());
        event.setEventType(Event.CREATE_JOB_EVENT_TYPE);
        event.setDate(job.getCreateDate());
        
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

    @Override
    public Event setAsFailedCreateJobEvent(Event event, Job job) {
        if (anyOfTheseObjectsNull(event,job) == true){
            _log.log(Level.WARNING,"One or more parameters passed in is null.  Unable to log event.");
            return null;
        }
        event.setEventType(Event.FAILED_CREATE_JOB_EVENT_TYPE);
        event.setDate(new Date());
        String errorSummary = job.getSummaryOfErrors();
        if (errorSummary != null){
            event.setMessage(job.getSummaryOfErrors());
        }
        return event;
    }

    @Override
    public Event setAsCreateUserEvent(Event event, User user) {
        if (anyOfTheseObjectsNull(event,user) == true){
            _log.log(Level.WARNING,"One or more parameters passed in is null.  Unable to log event.");
            return null;
        }
        
        event.setEventType(Event.CREATE_USER_EVENT_TYPE);
        event.setDate(user.getCreateDate());
        event.setCreatedUserId(user.getId());
        event.setMessage("perms("+user.getPermissions()+")");
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

    @Override
    public Event setAsLogicalDeleteWorkflowEvent(Event event, Workflow workflow) {
        if (anyOfTheseObjectsNull(event,workflow) == true){
            _log.log(Level.WARNING,"One or more parameters passed in is null.  Unable to log event.");
            return null;
        }

        event.setWorkflowId(workflow.getId());
        event.setEventType(Event.LOGICAL_DELETE_WORKFLOW_EVENT_TYPE);
        event.setDate(new Date());
        
        return event;
    }

    @Override
    public Event setAsDeleteWorkflowEvent(Event event, Workflow workflow) {
         if (anyOfTheseObjectsNull(event,workflow) == true){
            _log.log(Level.WARNING,"One or more parameters passed in is null.  Unable to log event.");
            return null;
        }

        event.setWorkflowId(workflow.getId());
        event.setEventType(Event.DELETE_WORKFLOW_EVENT_TYPE);
        event.setDate(new Date());
        StringBuilder sb = new StringBuilder();
        
        sb.append("Name=");
        sb.append(nullSafeString(workflow.getName()));
        
        sb.append(",Version=");
        sb.append(Integer.toString(workflow.getVersion()));
        
        sb.append(",CreateDate=");
        sb.append(nullSafeDateString(workflow.getCreateDate()));
        
        event.setMessage(sb.toString());
       
        return event;
    }

    @Override
    public Event setAsLogicalDeleteWorkspaceFileEvent(Event event, WorkspaceFile wsf) {
        if (anyOfTheseObjectsNull(event,wsf) == true){
            _log.log(Level.WARNING,"One or more parameters passed in is null.  Unable to log event.");
            return null;
        }

        event.setWorkspaceFileId(wsf.getId());
        event.setEventType(Event.LOGICAL_DELETE_WORKSPACEFILE_EVENT_TYPE);
        event.setDate(new Date());
        
        return event;
    }

    @Override
    public Event setAsDeleteWorkspaceFileEvent(Event event, WorkspaceFile wsf) {
        if (anyOfTheseObjectsNull(event,wsf) == true){
            _log.log(Level.WARNING,"One or more parameters passed in is null.  Unable to log event.");
            return null;
        }
         
        event.setWorkspaceFileId(wsf.getId());
        event.setEventType(Event.DELETE_WORKSPACEFILE_EVENT_TYPE);
        event.setDate(new Date());
        
        StringBuilder sb = new StringBuilder();
        sb.append("Name=");
        sb.append(nullSafeString(wsf.getName()));
        
        sb.append(",CreateDate=");
        sb.append(nullSafeDateString(wsf.getCreateDate()));
        
        sb.append(",Path=");
        sb.append(nullSafeString(wsf.getPath()));
        
        event.setMessage(sb.toString());
        return event;
    }

    @Override
    public Event setAsDeleteJobEvent(Event event, Job job) {
        if (this.anyOfTheseObjectsNull(event,job) == true){
             _log.log(Level.WARNING,"One or more parameters passed in is null.  Unable to log event.");
            return null;
        }
        event.setJobId(job.getId());
        event.setEventType(Event.DELETE_JOB_EVENT_TYPE);
        event.setDate(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append("Name=");
        sb.append(nullSafeString(job.getName()));
        
        sb.append(",CreateDate=");
        sb.append(nullSafeDateString(job.getCreateDate()));
        event.setMessage(sb.toString());
        return event;
    }

    @Override
    public Event setAsLogicalDeleteJobEvent(Event event, Job job) {
                if (this.anyOfTheseObjectsNull(event,job) == true){
             _log.log(Level.WARNING,"One or more parameters passed in is null.  Unable to log event.");
            return null;
        }
        event.setJobId(job.getId());
        event.setEventType(Event.LOGICAL_DELETE_JOB_EVENT_TYPE);
        event.setDate(new Date());
        return event;
    }
}
