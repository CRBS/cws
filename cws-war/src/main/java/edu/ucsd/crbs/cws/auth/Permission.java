package edu.ucsd.crbs.cws.auth;

/**
 * Represents permissions using a single int
 * where each bit represents a different permission.
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class Permission {
    
    /**
     * No permissions
     */
    public static final int NONE = 0;
    
    /**
     * All permissions
     */
    public static final int ALL = 0xFFFFFFFF;
    
    /**
     * Permission to get a list of all user Tasks
     */
    public static final int LIST_ALL_TASKS = 1;
    
    /**
     * Permission to get a list of their own Tasks
     */
    public static final int LIST_THEIR_TASKS = 2;
    
    /**
     * Permission to update any and all Tasks
     */
    public static final int UPDATE_ALL_TASKS = 4;
    
    /**
     * Permission to update their own Tasks
     */
    public static final int UPDATE_THEIR_TASKS = 8;
    
    /**
     * Permission to create a new Task
     */
    public static final int CREATE_TASK = 16;
    
    /**
     * Permission to get a list of all workflows
     */
    public static final int LIST_ALL_WORKFLOWS = 32;
    
    /**
     * Permission to update all workflows
     */
    public static final int UPDATE_ALL_WORKFLOWS = 64;
    
    /**
     * Permission to create a workflow
     */
    public static final int CREATE_WORKFLOW = 128;
    
    /**
     * Permission to download all workflows
     */
    public static final int DOWNLOAD_ALL_WORKFLOWS = 256;
    
    /**
     * Permission to download their workflows
     */
    public static final int DOWNLOAD_THEIR_WORKFLOWS = 512;
    
    /**
     * Permission to run as another user
     */
    public static final int RUN_AS_ANOTHER_USER = 1024;
    
    /**
     * Permission to list all workspace files
     */
    public static final int LIST_ALL_WORKSPACEFILES = 2048;
    
    /**
     * Permission to list their workspace files
     */
    public static final int LIST_THEIR_WORKSPACEFILES = 4096;
    
    /**
     * Permission to create a workspace file
     */
    public static final int CREATE_WORKSPACEFILE = 8192;
    
    /**
     * Permission to update workspace file
     */
    public static final int UPDATE_ALL_WORKSPACEFILES = 16384;
    
    /**
     * Permission to download workspace file
     */
    public static final int DOWNLOAD_ALL_WORKSPACEFILES = 32768;
}