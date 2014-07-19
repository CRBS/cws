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

package edu.ucsd.crbs.cws.dao;

import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import java.util.List;

/**
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public interface WorkspaceFileDAO {


    /**
     * Gets workspace files owned by this user that exist on the file system of the
     * cluster and are not deleted
     * @param owner
     * @return
     * @throws Exception 
     */
    public List<WorkspaceFile> getWorkspaceFiles(final String owner,
            final Boolean synced) throws Exception;
    
    /**
     * Gets {@link WorkspaceFile} by id
     * @param workspaceFileId
     * @param user
     * @return
     * @throws Exception 
     */
    public WorkspaceFile getWorkspaceFileById(final String workspaceFileId,User user) throws Exception;
    
    
    public List<WorkspaceFile> getWorkspaceFilesById(final String workspaceFileIds,User user) throws Exception;
    
    /**
     * Adds a new {@link WorkspaceFile} to the data store.  
     * @param wsp
     * @return WorkspaceFile object with id set to value from datastore
     * @throws Exception 
     */
    public WorkspaceFile insert(WorkspaceFile wsp,boolean generateUploadURL) throws Exception;

    
    /**
     * Updates BlobKey for given {@link WorkspaceFile} object
     * @param workspaceFileId
     * @param key
     * @return WorkspaceFile object with blobkey set
     * @throws Exception 
     */
    public WorkspaceFile updateBlobKey(long workspaceFileId,final String key) throws Exception;
    
    
    /**
     * Updates path for given {@link WorkspaceFile} object
     * @param workspaceFileId
     * @param path
     * @return
     * @throws Exception 
     */
    public WorkspaceFile updatePath(long workspaceFileId,final String path) throws Exception;

    /**
     * Updates data store with <b>wsp</b> {@link WorkspaceFile}  The {@link WorkspaceFile#getId()} must
     * be set for this to work properly
     * @param wsp
     * @return
     * @throws Exception 
     */
    public WorkspaceFile update(WorkspaceFile wsp) throws Exception;
    
    /**
     * Loads and resaves {@link WorkspaceFile} corresponding to <b>workspaceFileId</b>
     * passed in
     * @param workspaceFileId
     * @return
     * @throws Exception 
     */
    public WorkspaceFile resave(long workspaceFileId) throws Exception;
}
