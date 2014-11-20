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

package edu.ucsd.crbs.cws.rest;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class Constants {
    
    
    public static final String HTTPS = "https://";
    
    public static final String HTTP = "http://";
    
    public static final String SLASH = "/";
    
    public static final String JOB_ID_PATH_PARAM = "jobid";
    
    public static final String JOB_ID_REST_PATH = SLASH+"{"+JOB_ID_PATH_PARAM+"}";
    
    public static final String WORKFLOW_SUFFIX = ".kar";
    
    
    public static final String WORKSPACEFILE_ID_PATH_PARAM = "workspacefileid";
    
    public static final String WORKSPACEFILE_ID_REST_PATH = SLASH+"{"+
            WORKSPACEFILE_ID_PATH_PARAM+"}";
    
    
    public static final String USER_ID_PATH_PARAM = "userid";
    
    public static final String USER_ID_REST_PATH = SLASH+"{"+USER_ID_PATH_PARAM+"}";
    
    
    public static final String WORKFLOW_ID_PATH_PARAM = "wfid";
    public static final String WORKFLOW_ID_REST_PATH = SLASH+"{"+WORKFLOW_ID_PATH_PARAM+"}";
    
    /**
     * REST URL path for Users
     */
    public static final String USERS_PATH = "users";
    
    /**
     * REST URL path for Jobs
     */
    public static final String JOBS_PATH = "jobs";
    
    /**
     * REST URL path for Workflows
     */
    public static final String WORKFLOWS_PATH = "workflows";
    
    /**
     * REST URL path for WorkspaceFiles
     */
    public static final String WORKSPACEFILES_PATH = "workspacefiles";
    
    public static final String WORKSPACEFILES_AS_LIST_REST_PATH = SLASH+"aslist";
    
    /**
     * REST path added at end of address like so:  IE https://blah.com/rest/
     */
    public static final String REST_PATH = "rest";
    
    /**
     * User login query parameter name.  This should be passed to all
     * rest calls
     */
    public static final String USER_LOGIN_PARAM = "userlogin";
    
    /**
     * User token query parameter name.  This should be passed to all
     * rest calls
     */
    public static final String USER_TOKEN_PARAM = "usertoken";
    
    
    /**
     * Alternate user login parameter name.  Currently some rest calls
     * require this if invoker is a super user wishing to run a job on behalf
     * of another user
     */
    public static final String USER_LOGIN_TO_RUN_AS_PARAM = "runasuser";
    
    /**
     * workflow id query parameter name which is passed to {@link WorkflowFile}
     * servlet
     */
    public static final String WFID_PARAM = "wfid";
    
    /**
     * workspacefile id query parameter name which is passed to {@link WorkspaceFileServlet}
     */
    public static final String WSFID_PARAM = "id";
    
    public static final String STATUS_QUERY_PARAM = "status";
    public static final String OWNER_QUERY_PARAM = "owner";
    public static final String NOPARAMS_QUERY_PARAM = "noparams";
    public static final String NOWORKFLOWPARAMS_QUERY_PARAM = "noworkflowparams";
    public static final String NOTSUBMITTED_TO_SCHED_QUERY_PARAM = "notsubmittedtoscheduler";
    
    public static final String ESTCPU_QUERY_PARAM = "estcpu";
    public static final String ESTRUNTIME_QUERY_PARAM = "estruntime";
    public static final String ESTDISK_QUERY_PARAM = "estdisk";
    public static final String SUBMITDATE_QUERY_PARAM = "submitdate";
    public static final String STARTDATE_QUERY_PARAM = "startdate";
    public static final String FINISHDATE_QUERY_PARAM = "finishdate";
    public static final String SUBMITTED_TO_SCHED_QUERY_PARAM = "submittedtosched";
    public static final String SCHEDULER_JOB_ID_QUERY_PARAM = "schedulerjobid";
    public static final String DOWNLOADURL_QUERY_PARAM = "downloadurl";

    public static final String DELETED_QUERY_PARAM = "deleted";
    
    public static final String ERROR_QUERY_PARAM = "error";
     
    public static final String DETAILED_ERROR_QUERY_PARAM = "detailederror";
    
    
    public static final String TYPE_QUERY_PARAM = "type";
    public static final String SIZE_QUERY_PARAM = "size";
    
    
    public static final String SYNCED_QUERY_PARAM = "synced";
    
    public static final String PATH_QUERY_PARAM = "path";
    
    public static final String ADD_UPLOAD_URL_PARAM = "adduploadurl";
    
    public static final String SOURCE_JOB_ID_QUERY_PARAM = "sourcejobid";
    
    /**
     * Tells REST service whether this workspace file is from a failed
     * job.  true for yes false for no
     */
    public static final String WS_FAILED_QUERY_PARAM = "isfailed";
    
    /**
     * Tells REST services to load and resave given object.  This is needed
     * in cases where object has new updated fields or indexes have changed.
     */
    public static final String RESAVE_QUERY_PARAM = "resave";
    
    /**
     * outputs Directory where Job should write output
     */
    public static final String OUTPUTS_DIR_NAME = "outputs";
    
    
     /**
     * Special Canvas parameter that will be set to the user running the {@link Job}
     */
    public static final String CWS_USER = "CWS_user";
    
    /**
     * Special Canvas parameter that will be set to the name of the {@link Job}
     */
    public static final String CWS_JOBNAME = "CWS_jobname";
    
    
    /**
     * Special Canvas parameter that will be set to the id of the {@link Job}
     */
    public static final String CWS_JOBID = "CWS_jobid";
    
    /**
     * Special Canvas parameter that will be set to the output directory for the running job
     */
    public static final String CWS_OUTPUTDIR = "CWS_outputdir";
    
    /**
     * Special Canvas parameter that will be set to the email address of the 
     * user running the job
     */
    public static final String CWS_NOTIFYEMAIL = "CWS_notifyemail";

    public static final String DEFAULT_LINE_DELIMITER = "==";
    
    public static final String WORKFLOW_FAILED_FILE = "WORKFLOW.FAILED.txt";
    
    public static final String SIMPLE_ERROR_MESSAGE_KEY = "simple.error.message";
    public static final String DETAILED_ERROR_MESSAGE_KEY = "detailed.error.message";

}
