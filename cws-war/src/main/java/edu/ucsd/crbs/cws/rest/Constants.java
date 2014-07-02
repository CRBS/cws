package edu.ucsd.crbs.cws.rest;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class Constants {
    
    
    public static final String HTTPS = "https://";
    
    public static final String HTTP = "http://";
    
    public static final String SLASH = "/";
    
    public static final String TASK_ID_PATH_PARAM = "taskid";
    
    public static final String TASK_ID_REST_PATH = SLASH+"{"+TASK_ID_PATH_PARAM+"}";
    
    public static final String WORKFLOW_SUFFIX = ".kar";
    
    
    public static final String WORKSPACEFILE_ID_PATH_PARAM = "workspacefileid";
    
    public static final String WORKSPACEFILE_ID_REST_PATH = SLASH+"{"+
            WORKSPACEFILE_ID_PATH_PARAM+"}";
    
    
    public static final String USER_ID_PATH_PARAM = "userid";
    
    public static final String USER_ID_REST_PATH = SLASH+"{"+USER_ID_PATH_PARAM+"}";
    
    
    /**
     * REST URL path for Users
     */
    public static final String USERS_PATH = "users";
    
    /**
     * REST URL path for Tasks
     */
    public static final String TASKS_PATH = "tasks";
    
    /**
     * REST URL path for Workflows
     */
    public static final String WORKFLOWS_PATH = "workflows";
    
    /**
     * REST URL path for WorkspaceFiles
     */
    public static final String WORKSPACEFILES_PATH = "workspacefiles";
    
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
     * require this if invoker is a super user wishing to run a task on behalf
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
    public static final String JOB_ID_QUERY_PARAM = "jobid";
    public static final String DOWNLOADURL_QUERY_PARAM = "downloadurl";
    
    
    public static final String SYNCED_QUERY_PARAM = "synced";
    
    public static final String PATH_QUERY_PARAM = "path";
    
    public static final String ADD_UPLOAD_URL_PARAM = "adduploadurl";
    /**
     * outputs Directory where Tasks should write output
     */
    public static final String OUTPUTS_DIR_NAME = "outputs";
    
    
     /**
     * Special Canvas parameter that will be set to the user running the job
     */
    public static final String CWS_USER = "CWS_user";
    
    /**
     * Special Canvas parameter that will be set to the name of the task
     */
    public static final String CWS_TASKNAME = "CWS_taskname";
    
    
    /**
     * Special Canvas parameter that will be set to the id of the task
     */
    public static final String CWS_TASKID = "CWS_taskid";
    
    /**
     * Special Canvas parameter that will be set to the output directory for the running job
     */
    public static final String CWS_OUTPUTDIR = "CWS_outputdir";

    
}
