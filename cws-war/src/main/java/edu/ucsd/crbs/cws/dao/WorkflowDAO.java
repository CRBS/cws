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
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.report.DeleteReport;
import java.util.List;

/**
 * Interface that defines methods to retrieve, save, and modify {@link Workflow} objects
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public interface WorkflowDAO {
   
    /**
     * Queries data store to retrieve workflow identified by workflowId
     * @param workflowId
     * @param user
     * @param userName
     * @return Workflow object upon success or null if none is found
     * @throws Exception If the workflowId is invalid, null, or there is a problem in parsing
     */
    public Workflow getWorkflowById(final String workflowId,
            User user) throws Exception;
    
    /**
     * Gets all workflows from data store
     * @param omitWorkflowParams If set to true then WorkflowParameters will be set to null for every Workflow object returned
     * @return List of Workflow objects if Workflows are found otherwise an empty list or null
     * @throws Exception If there was an error retrieving the Workflows
     */
    public List<Workflow> getAllWorkflows(boolean omitWorkflowParams,
            final Boolean showDeleted) throws Exception;
    
    /**
     * Adds a new workflow to the data store.  If the Id of the Workflow is set then the
     * parent field of this Workflow object is set to that Id and this Workflow is given
     * a new Id.
     * 
     * @param w Workflow to add to data store
     * @return Workflow object with new id added
     * @throws Exception If there was an error during persistence
     */
    public Workflow insert(Workflow w) throws Exception;
    
    /**
     * Updates BlobKey for Given Workflow
     * @param worklfowId
     * @param key
     * @return
     * @throws Exception 
     */
    public Workflow updateBlobKey(long workflowId,final String key) throws Exception;

    /**
     * Updates Deleted field for given {@link Workflow}
     * @param workflowId
     * @param isDeleted
     * @return {@link Workflow} matching <b>workflowId</b> loaded from datastore upon succes.
     * @throws Exception 
     */
    public Workflow updateDeleted(final long workflowId,final boolean isDeleted) throws Exception;
    
    /**
     * Given a <b>job</b> this method looks at the {@link Workflow} object within
     * and uses its id to load the {@link Workflow} from the data store.  
     * @param job
     * @param user
     * @throws Exception if there is no {@link Workflow} object with id
     * @return 
     */
    public Workflow getWorkflowForJob(Job job,User user) throws Exception;
    
    /**
     * Loads {@link Workflow} with corresponding <b>workflowId</b> and resaves
     * to datastore
     * @param workflowId
     * @return
     * @throws Exception 
     */
    public Workflow resave(long workflowId) throws Exception;
    
    /**
     * Deletes {@link Workflow} either logically or for real depending on 
     * parameter passed in.  In either case {@link Workflow} can only be deleted
     * if no {@link Job}s are associated with the {@link Workflow}
     */
    
    public DeleteReport delete(long workflowId,Boolean permanentlyDelete) throws Exception;
    
}
