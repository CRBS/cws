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
     * Permission to get a list of all user Jobs
     */
    public static final int LIST_ALL_JOBS = 1;
    
    /**
     * Permission to get a list of their own Jobs
     */
    public static final int LIST_THEIR_JOBS = 2;
    
    /**
     * Permission to update any and all Jobs
     */
    public static final int UPDATE_ALL_JOBS = 4;
    
    /**
     * Permission to update their own Jobs
     */
    public static final int UPDATE_THEIR_JOBS = 8;
    
    /**
     * Permission to create a new Job
     */
    public static final int CREATE_JOB = 16;
    
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
    
    /**
     * Permission to list all users
     */
    public static final int LIST_ALL_USERS = 65536;
    
    /**
     * Permission to create user
     */
    public static final int CREATE_USER = 131072;
}