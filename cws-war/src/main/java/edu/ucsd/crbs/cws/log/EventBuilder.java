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
     * Converts Event into what is known as a Create Job Event
     * @param event
     * @param job
     * @return Same Event object passed in unless there was a problem in which case null is returned
     */
    public Event setAsCreateJobEvent(Event event,Job job);
    
    /**
     * Converts Event into what is known as a failed create job event
     * @param event
     * @param job
     * @return 
     */
    public Event setAsFailedCreateJobEvent(Event event,Job job);
    
    /**
     * Converts Event into what is known as a Create Workflow Event
     * @param event
     * @param workflow
     * @return Same Event object passed in unless there was a problem in which case null is returned
     */
    public Event setAsCreateWorkflowEvent(Event event,Workflow workflow);
    
    public Event setAsCreateWorkspaceFileEvent(Event event,WorkspaceFile workspaceFile);
    
    public Event setAsCreateUserEvent(Event event,User user);
}
