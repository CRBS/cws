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

import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.report.DeleteReport;
import java.util.List;

/**
 * Interface that defines methods to retrieve and persist {@link Job} objects to
 * a data store
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public interface JobDAO {

    /**
     * Gets {@link Job} from Data Store by querying using jobId passed in.
     *
     * @param jobId
     * @return
     * @throws Exception
     */
    public Job getJobById(final String jobId) throws Exception;

    /**
     * Gets {@link Job} from Data Store by querying using <b>jobId</b> and
     * <b>user</b>
     * passed in
     *
     * @param jobId id of job
     * @param user login of user
     * @return
     * @throws Exception
     */
    public Job getJobByIdAndUser(final String jobId, final String user) throws Exception;

    /**
     * Gets jobs using parameters as filters
     *
     * @param owner If non null only Jobs with matching owners will be returned
     * @param status If non null only Jobs with matching status will be returned
     * @param notSubmittedToScheduler If non null only Jobs matching submitted
     * to scheduler flag will be returned
     * @param noParams True means to exclude Parameters in returned Jobs
     * @param noWorkflowParams True means to exclude WorkflowParameters in
     * Workflow objects returned with Jobs
     * @return
     * @throws Exception
     */
    public List<Job> getJobs(final String owner, final String status, final Boolean notSubmittedToScheduler,
            boolean noParams, boolean noWorkflowParams, final Boolean showDeleted) throws Exception;

    /**
     * Gets count of {@link Job}s using parameters as filters
     *
     * @param owner If non null only Jobs with matching owners will be returned
     * @param status If non null only Jobs with matching status will be returned
     * @param notSubmittedToScheduler If non null only Jobs matching submitted
     * to scheduler flag will be returned
     * @return
     * @throws Exception
     */
    public int getJobsCount(final String owner, final String status, 
            final Boolean notSubmittedToScheduler,
            final Boolean showDeleted) throws Exception;
    
    /**
     * Inserts a new {@link Job} into data store skipping {@link Workflow} Check
     *
     * @param j
     * @param skipWorkflowCheck
     * @return
     * @throws Exception
     */
    public Job insert(Job j, boolean skipWorkflowCheck) throws Exception;

    /**
     * Updates existing job with values passed in.
     *
     * @param jobId
     * @param status
     * @param estCpu
     * @param estWallTime
     * @param estDisk
     * @param submitDate
     * @param startDate
     * @param finishDate
     * @param submittedToScheduler
     * @param jobId
     * @return
     * @throws Exception
     */
    public Job update(long jobId, final String status, Long estCpu, Long estWallTime,
            Long estDisk, Long submitDate, Long startDate, Long finishDate,
            Boolean submittedToScheduler, final String schedulerJobId,
            final Boolean deleted, final String error, final String detailedError) throws Exception;

    
    /**
     * Updates existing {@link Job} with values set in <b>job</b> 
     * @param job
     * @return
     * @throws Exception 
     */
    public Job update(final Job job) throws Exception;
    
    /**
     * Loads and resaves {@link Job} with given <b>jobId</b>
     *
     * @param jobId
     * @return Resaved {@link Job}
     * @throws Exception
     */
    public Job resave(long jobId) throws Exception;

    /**
     * Gets {@link Job}s that were run from the <b>workflowId</b> passed in
     *
     * @param workflowId id of {@link Workflow} that {@link Job} was run from
     * @return List of {@link Job}s that were run from <b>workflowId</b>
     * @throws Exception If there was an error querying the data store
     */
    public List<Job> getJobsWithWorkflowId(long workflowId) throws Exception;

    /**
     * Gets number of {@link Job}s that were run from the <b>workflowId</b>
     * passed in
     *
     * @param workflowId id of {@link Workflow} that {@link Job} was run from
     * @return number of jobs run under the <b>workflowId</b>
     * @throws Exception If there was an error querying the data store
     */
    public int getJobsWithWorkflowIdCount(long workflowId) throws Exception;
 
    /**
     * Performs logical or real delete of {@link Job} with <b>id</b> of 
     * <b>jobId</b>
     * @param jobId Id of {@link Job}
     * @param permanentlyDelete if non <b>null</b> and <b>true</b> really
     * delete {@link Job}
     * @return {@link DeleteReport} denoting whether delete succeeded.
     * @throws Exception 
     */
    public DeleteReport delete(long jobId,
            Boolean permanentlyDelete) throws Exception;
    
}
