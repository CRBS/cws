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

import edu.ucsd.crbs.cws.util.RunCommandLineProcess;
import edu.ucsd.crbs.cws.util.RunCommandLineProcessImpl;
import edu.ucsd.crbs.cws.workflow.Job;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates Map of job statuses for a given set of jobs by calling panfishstat
 * command line program
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class MapOfJobStatusFactoryImpl implements MapOfJobStatusFactory {

    static final String NEW_LINE = "\n";
    
    /**
     * Delimiter between job id and status in panfishstat output
     */
    static final String EQUAL_SIGN = "=";
    
    static final String COMMA = ",";

    /**
     * Denotes running jobs in panfishstat output
     */
    static final String RUNNING = "running";
    
    /**
     * Denotes completed jobs in panfishstat output
     */
    static final String DONE = "done";
    
    /**
     * Denotes failed jobs in panfishstat output
     */
    static final String FAILED = "failed";
    
    /**
     * Flag to pass to panfishstat to get status of jobs
     */
    static final String STATUSOFJOBID = "--statusofjobid";
    
    private static final Logger _log
            = Logger.getLogger(MapOfJobStatusFactoryImpl.class.getName());
    
    /**
     * Panfishstat binary path
     */
    private final String _panfishStat;

    RunCommandLineProcess _runCommandLineProcess = new RunCommandLineProcessImpl();
    
    public MapOfJobStatusFactoryImpl(final String panfishStat){
        _panfishStat = panfishStat;
    }
    
    /**
     * Calls panfishstat to get updated status of <b>jobs</b> passed in
     * @param jobs Jobs to check
     * @return Map with key set to job id and value set to status.  Status will 
     * be one of the following {@link Job#IN_QUEUE_STATUS}, 
     * {@link Job#RUNNING_STATUS}, {@link Job#COMPLETED_STATUS}, or 
     * {@link Job#ERROR_STATUS}
     * @throws Exception if there was a problem calling panfishstat or if 
     * panfishstat returns non zero exit code
     */
    @Override
    public Map<String, String> getJobStatusMap(List<Job> jobs) throws Exception {
        
        // @TODO need to handle case where there are 2,000+ jobs to get status for
        // cause the bash command line will fail with too many arguments errors
        String delimStringOfJobIds = getCommaDelimitedStringOfJobIds(jobs);
        Map<String, String> jobStatusMap = new HashMap<>();
        
        if (delimStringOfJobIds == null || delimStringOfJobIds.isEmpty()){
            _log.log(Level.INFO,"No jobs to examine");
            return jobStatusMap;
        }
        
        String result = _runCommandLineProcess.runCommandLineProcess(_panfishStat,
                STATUSOFJOBID,delimStringOfJobIds);
       
        if (result == null){
            return jobStatusMap;
        }
        
        String[] lines = result.split(NEW_LINE);
        for (String line : lines) {
            int equalPos = line.indexOf(EQUAL_SIGN);
            if (equalPos <= 0 || equalPos >= line.length() - 1) {
              continue;   
            }
            
            String id = line.substring(0, equalPos);
            
            String status = line.substring(equalPos + 1);

            //set to default of Task.IN_QUEUE_STATUS
            String convertedStatus = Job.IN_QUEUE_STATUS;
            
            if (status.equalsIgnoreCase(RUNNING)) {
                convertedStatus = Job.RUNNING_STATUS;
            } else if (status.equalsIgnoreCase(DONE)) {
                convertedStatus = Job.COMPLETED_STATUS;
            } else if (status.equalsIgnoreCase(FAILED)) {
                convertedStatus = Job.ERROR_STATUS;
            }
            
            jobStatusMap.put(id, convertedStatus);
        }

        _log.log(Level.INFO, "Output from panfishstat:\n{0}", result);
        return jobStatusMap;
    }

    /**
     * Examines the list of Job objects building a CSV list from the
     * Job.getId strings
     *
     * @param jobs
     * @return CSV delimited list of jobIds
     */
    private String getCommaDelimitedStringOfJobIds(List<Job> jobs) throws Exception {
        StringBuilder sb = new StringBuilder();
        
        if (jobs == null || jobs.isEmpty() == true){
            return null;
        }
        
        for (Job j : jobs) {
            if (sb.length() > 0) {
                sb.append(COMMA);
            }
            if (j.getSchedulerJobId() == null){
                continue;
            }
            sb.append(j.getSchedulerJobId());
            
        }
        return sb.toString();
    }
    
}
