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

package edu.ucsd.crbs.cws.cluster;

import edu.ucsd.crbs.cws.App;
import edu.ucsd.crbs.cws.dao.JobDAO;
import edu.ucsd.crbs.cws.io.WorkflowFailedParser;
import edu.ucsd.crbs.cws.log.LogUtil;
import edu.ucsd.crbs.cws.rest.Constants;
import edu.ucsd.crbs.cws.workflow.Job;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Updates status of Jobs by querying Panfish
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class JobStatusUpdater {

    private static final Logger _log
            = Logger.getLogger(JobStatusUpdater.class.getName());

    JobDAO _jobDAO;
    MapOfJobStatusFactory _jobStatusFactory;
    private JobPath _jobPath;
    WorkflowFailedParser _workflowFailedParser;

    /**
     * Constructor
     *
     * @param jobDAO Used to get Task objects and update Task objects
     * @param jobStatusFactory class to obtain status of Tasks from
     * @param workflowFailedParser
     * @param jobPath
     */
    public JobStatusUpdater(JobDAO jobDAO,MapOfJobStatusFactory jobStatusFactory,
            WorkflowFailedParser workflowFailedParser,
            JobPath jobPath) {
        _jobDAO = jobDAO;
        _jobStatusFactory = jobStatusFactory;
        _jobPath = jobPath;
        _workflowFailedParser = workflowFailedParser;
    }

    /**
     * Query for all jobs that have not completed and attempt to update their
     * status
     *
     * @throws Exception
     */
    public void updateJobs() throws Exception {

        String error;
        String detailedError;
        _log.log(Level.INFO, "Updating status for uncompleted jobs...");
        List<Job> jobs = _jobDAO.getJobs(null, App.NOT_COMPLETED_STATUSES, false, false, false,null);
        if (jobs != null && jobs.isEmpty() == false) {

            _log.log(Level.INFO, " found {0} jobs to possibly update", jobs.size());
            Map<String, String> jobStatusMap = _jobStatusFactory.getJobStatusMap(jobs);
            
            for (Job j : jobs) {
                if (jobStatusMap.containsKey(j.getSchedulerJobId())) {
                    String returnedStatus = jobStatusMap.get(j.getSchedulerJobId());
                    if (!returnedStatus.equals(j.getStatus())) {
                        _log.log(Level.INFO, 
                                "\tJob: {0} old status: {1} new status: {2}", 
                                new Object[]{LogUtil.generateJobLogMessage(j), 
                                    j.getStatus(), returnedStatus});
                        
                        j.setStatus(returnedStatus);
                        
                        Long startDate = null;
                        Long finishDate = null;
                        error = null;
                        detailedError = null;
                        if (returnedStatus.equals(Job.RUNNING_STATUS)){
                            j.setStartDate(new Date());
                            startDate = j.getStartDate().getTime();
                            
                        }
                        else if (returnedStatus.equals(Job.COMPLETED_STATUS) ||
                                 returnedStatus.equals(Job.ERROR_STATUS)){
                            
                            //check for WORKFLOW.FAILED.txt file and if it exists
                            //set status to failed.
                            _workflowFailedParser.setPath(_jobPath.getJobOutputDirectory(j));
                            if (_workflowFailedParser.exists()){
                                _log.log(Level.INFO,
                                        "{0} found for job {1}"+
                                        " setting status of job to error",
                                        new Object[]{Constants.WORKFLOW_FAILED_FILE,
                                            LogUtil.generateJobLogMessage(j)});
                                error = _workflowFailedParser.getError();
                                detailedError = _workflowFailedParser.getDetailedError();
                                j.setStatus(Job.ERROR_STATUS);
                            }
                                   
                            j.setFinishDate(new Date());
                            finishDate = j.getFinishDate().getTime();
                        }
                        try {
                            _jobDAO.update(j.getId(), j.getStatus(), null, null, null,
                                    null, startDate, finishDate, true, null,null,error,detailedError);
                        }
                        catch(Exception ex){
                           _log.log(Level.SEVERE,
                                   "There was a problem updating job: {0} Skipping...",
                                   LogUtil.generateJobLogMessage(j));
                        }
                    }
                }
            }
        } else {
            _log.log(Level.INFO, "no jobs to update");
        }
    }
}
