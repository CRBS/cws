package edu.ucsd.crbs.cws.rest;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class Constants {
    
    /**
     * REST URL path for Tasks
     */
    public static final String TASKS_PATH = "tasks";
    
    /**
     * REST URL path for Workflows
     */
    public static final String WORKFLOWS_PATH = "workflows";
    
    /**
     * REST path added at end of address like so:  IE https://blah.com/rest/
     */
    public static final String REST_PATH = "rest";
    
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
    
    /**
     * outputs Directory where Tasks should write output
     */
    public static final String OUTPUTS_DIR_NAME = "outputs";

    
}
