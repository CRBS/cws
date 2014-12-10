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

import edu.ucsd.crbs.cws.workflow.InputWorkspaceFileLink;
import edu.ucsd.crbs.cws.workflow.Job;
import java.util.List;

/**
 * Defines methods to retrieve and store {@link InputWorkspaceFile} objects
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public interface InputWorkspaceFileLinkDAO {

    /**
     * Adds a new {@link InputWorkspaceFileLink} to data store
     * @param workspaceFileLink Object to add to datastore
     * @return {@link InputWorkspaceFileLink} with id set
     * @throws Exception 
     */
    public InputWorkspaceFileLink insert(InputWorkspaceFileLink workspaceFileLink) throws Exception;
    
    /**
     * Resaves {@link InputWorkspaceFileLink} whose {@link InputWorkspaceFileLink#getId() matches 
     * <b>inputWorkspaceFileLinkId</b>
     * @param inputWorkspaceFileLinkId
     * @return {@link InputWorkspaceFileLink} after resave
     * @throws Exception If there is an error during the save
     */
    public InputWorkspaceFileLink resave(final long inputWorkspaceFileLinkId) throws Exception;
    
    /**
     * Gets {@link InputWorkspaceFileLink} objects whose {@link InputWorkspaceFileLink#getJobId()} matches <b>jobId</b>
     * @param jobId Job id to look for
     * @param showDeleted If true then {@link InputWorkspaceFileLink} objects with {@link InputWorkspaceFileLink#isDeleted()} set to true will be returned
     * @return
     * @throws Exception 
     */
    public List<InputWorkspaceFileLink> getByJobId(Long jobId,Boolean showDeleted) throws Exception;
    
    /**
     * Gets {@link InputWorkspaceFileLink} objects whose {@link InputWorkspaceFileLink#getWorkspaceFileId() } matches <b>workspaceFileId</b>
     * @param workspaceFileId
     * @param showDeleted If true then {@link InputWorkspaceFileLink} objects with {@link InputWorkspaceFileLink#isDeleted()} set to true will be returned
     * @return
     * @throws Exception 
     */
    public List<InputWorkspaceFileLink> getByWorkspaceFileId(Long workspaceFileId,Boolean showDeleted) throws Exception;
    
    /**
     * Gets {@link InputWorkspaceFileLink} object whose {@link InputWorkspaceFileLink#getId()} matches <b>inputWorkspaceFileLinkId</b>
     * @param inputWorkspaceFileLinkId
     * @return
     * @throws Exception 
     */
    public InputWorkspaceFileLink getById(Long inputWorkspaceFileLinkId) throws Exception;
    
    /**
     * Gets list of all {@link InputWorkspaceFileLink}s
     * @param showDeleted {@link InputWorkspaceFileLink}s with {@link InputWorkspaceFileLink#isDeleted()} will only
     * be displayed if this parameter is <b>NOT <code>null</code></b> and set to <b><code>true</code></b>
     * @return List of {@link INputWorkspaceFileLink} objects
     * @throws Exception 
     */
    public List<InputWorkspaceFileLink> getInputWorkspaceFileLinks(Boolean showDeleted) throws Exception;
    
    /**
     * Examines <b>job</b> passed in and adds/corrects/removes 
     * {@link InputWorkspaceFileLink} objects as needed.  
     * @param job
     * @return
     * @throws Exception 
     */
 //   public void updateInputWorkspaceFileLinksForJob(Job job) throws Exception;
    
    
}
