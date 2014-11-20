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

package edu.ucsd.crbs.cws.dao.objectify;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.cmd.Query;
import edu.ucsd.crbs.cws.dao.JobDAO;
import static edu.ucsd.crbs.cws.dao.objectify.OfyService.ofy;
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.Workflow;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Implements TaskDAO interface which provides means to load and save Task
 * objects to Google NoSQL data store via Objectify.
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class JobObjectifyDAOImpl implements JobDAO {

    private static final String COMMA = ",";

    
    /**
     * In a transaction this method loads a {@link Job} with matching <b>jobId</b>
     * and resaves it to data store
     * @param jobId
     * @return
     * @throws Exception 
     */
    @Override
    public Job resave(final long jobId) throws Exception {

        Job resTask = ofy().transact(new Work<Job>() {
            @Override
            public Job run() {
                Job job;
                try {
                    job = getJobById(Long.toString(jobId));
                } catch (Exception ex) {
                    return null;
                }
                if (job == null) {
                    return null;
                }

                Key<Job> tKey = ofy().save().entity(job).now();
                return job;
            }
        });

        return resTask;
    }
                
    @Override
    public Job getJobById(final String jobId) throws Exception {
        long jobIdAsLong;
        try {
            jobIdAsLong = Long.parseLong(jobId);
        } catch (NumberFormatException nfe) {
            throw new Exception(nfe);
        }
        return ofy().load().type(Job.class).id(jobIdAsLong).now();
    }

    /**
     * Gets {@link Job} matching <b>jobId</b> and <b>user</b>
     * @param jobId
     * @param user 
     * @return {@link Job} if the {@link Job#getOwner()} matches <b>user</b> 
     *         and neither value is null otherwise null is returned
     * @throws Exception if <b>jobId</b> is not parseable as a Long or there was 
     *         an issue with the data store
     */
    @Override
    public Job getJobByIdAndUser(String jobId, String user) throws Exception {

        if (user == null){
            return null;
        }
        
        Job job = this.getJobById(jobId);
        if (job == null){
            return null;
        }
        if (job.getOwner() == null){
            return null;
        }
        
        if (job.getOwner().equals(user)){
            return job;
        }
        return null;
    }
    
    

    @Override
    public List<Job> getJobs(String owner, String status, Boolean notSubmittedToScheduler, boolean noParams, boolean noWorkflowParams) throws Exception {
        Query<Job> q = ofy().load().type(Job.class);

        if (status != null) {
            q = q.filter("_status in ", generateListFromCommaSeparatedString(status));
        }
        if (owner != null) {
            q = q.filter("_owner", owner);
        }
        if (notSubmittedToScheduler == true) {
            q = q.filter("_hasJobBeenSubmittedToScheduler", false);
        }

        if (noParams == false && noWorkflowParams == false) {
            return q.list();
        }

        List<Job> job = q.list();
        for (Job j : job) {
            if (noParams == true) {
                j.setParameters(null);
            }
            if (noWorkflowParams == true) {
                Workflow w = j.getWorkflow();
                if (w != null) {
                    w.setParameters(null);
                    w.setParentWorkflow(null);
                }
            }
        }
        return job;
    }

    /**
     * Creates a new Job in the data store
     *
     * @param job Job to insert
     * @param skipWorkflowCheck
     * @return Job object with id updated
     * @throws Exception
     */
    @Override
    public Job insert(Job job, boolean skipWorkflowCheck) throws Exception {
        if (job == null) {
            throw new NullPointerException("Job is null");
        }
        if (job.getCreateDate() == null) {
            job.setCreateDate(new Date());
        }

        if (skipWorkflowCheck == false) {

            if (job.getWorkflow() == null) {
                throw new Exception("Job Workflow cannot be null");
            }

            if (job.getWorkflow().getId() == null || job.getWorkflow().getId() <= 0) {
                throw new Exception("Job Workflow id is either null or 0 or less which is not valid");
            }
            //try to load the workflow and only if we get a workflow do we try to save
            //the job otherwise it is an error
            Workflow wf = ofy().load().type(Workflow.class).id(job.getWorkflow().getId()).now();
            if (wf == null) {
                throw new Exception("Unable to load Workflow for Job");
            }
        }
        /**
         * @TODO Need to verify the Job Parameters match the Workflow
         * parameters and that valid values are set for each of those parameters
         */
        Key<Job> jKey = ofy().save().entity(job).now();

        return job;
    }

    @Override
    public Job update(final long jobId, final String status, final Long estCpu,
            final Long estRunTime, final Long estDisk, final Long submitDate,
            final Long startDate, final Long finishDate,
            final Boolean submittedToScheduler,
            final String schedulerJobId,
            final Boolean deleted,
            final String error,
            final String detailedError) throws Exception {

        Job resJob;
        resJob = ofy().transact(new Work<Job>() {
            @Override
            public Job run() {

                Job job = ofy().load().type(Job.class).id(jobId).now();

                if (job == null) {
                    return null;
                }
                boolean jobNeedsToBeSaved = false;
                if (status != null && status.isEmpty() == false) {
                    job.setStatus(status);
                    jobNeedsToBeSaved = true;
                }
                if (estCpu != null && job.getEstimatedCpuInSeconds() != estCpu) {
                    job.setEstimatedCpuInSeconds(estCpu);
                    jobNeedsToBeSaved = true;
                }
                if (estRunTime != null && job.getEstimatedRunTime() != estRunTime) {
                    job.setEstimatedRunTime(estRunTime);
                    jobNeedsToBeSaved = true;
                }
                if (estDisk != null && job.getEstimatedDiskInBytes() != estDisk) {
                    job.setEstimatedDiskInBytes(estDisk);
                    jobNeedsToBeSaved = true;
                }

                if (submitDate != null) {
                    job.setSubmitDate(new Date(submitDate));
                    jobNeedsToBeSaved = true;
                }
                if (startDate != null) {
                    job.setStartDate(new Date(startDate));
                    jobNeedsToBeSaved = true;
                }
                if (finishDate != null) {
                    job.setFinishDate(new Date(finishDate));
                    jobNeedsToBeSaved = true;
                }
                if (deleted != null){
                    job.setDeleted(deleted.booleanValue());
                    jobNeedsToBeSaved = true;
                }
                if (error != null){
                    job.setError(error);
                    jobNeedsToBeSaved = true;
                }
                if (detailedError != null){
                    job.setDetailedError(detailedError);
                    jobNeedsToBeSaved = true;
                }

                if (submittedToScheduler != null && submittedToScheduler != job.getHasJobBeenSubmittedToScheduler()) {
                    job.setHasJobBeenSubmittedToScheduler(submittedToScheduler);
                    jobNeedsToBeSaved = true;
                }
                
                if (schedulerJobId != null) {
                    if (job.getSchedulerJobId() == null || !job.getSchedulerJobId().equals(schedulerJobId)) {
                        job.setSchedulerJobId(schedulerJobId);
                        jobNeedsToBeSaved = true;
                    }
                }

                if (jobNeedsToBeSaved == true) {
                    Key<Job> jKey = ofy().save().entity(job).now();
                }
                return job;
            }
        });
        if (resJob == null) {
            throw new Exception("There was a problem updating the Task");
        }
        return resJob;
    }

    private List<String> generateListFromCommaSeparatedString(final String val) {
        return Arrays.asList(val.split(COMMA));
    }
}
