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
import edu.ucsd.crbs.cws.workflow.Task;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates Map of job statuses for a given set of tasks by calling panfishstat
 * command line program
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class MapOfTaskStatusFactoryImpl implements MapOfTaskStatusFactory {

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
            = Logger.getLogger(MapOfTaskStatusFactoryImpl.class.getName());
    
    /**
     * Panfishstat binary path
     */
    private final String _panfishStat;

    RunCommandLineProcess _runCommandLineProcess = new RunCommandLineProcessImpl();
    
    public MapOfTaskStatusFactoryImpl(final String panfishStat){
        _panfishStat = panfishStat;
    }
    
    /**
     * Calls panfishstat to get updated status of <b>tasks</b> passed in
     * @param tasks Tasks to check
     * @return Map with key set to job id and value set to status.  Status will 
     * be one of the following {@link Task#IN_QUEUE_STATUS}, 
     * {@link Task#RUNNING_STATUS}, {@link Task#COMPLETED_STATUS}, or 
     * {@link Task#ERROR_STATUS}
     * @throws Exception if there was a problem calling panfishstat or if 
     * panfishstat returns non zero exit code
     */
    @Override
    public Map<String, String> getJobStatusMap(List<Task> tasks) throws Exception {
        
        // @TODO need to handle case where there are 2,000+ tasks to get status for
        // cause the bash command line will fail with too many arguments errors
        String delimStringOfJobIds = getCommaDelimitedStringOfJobIds(tasks);
        Map<String, String> jobStatusMap = new HashMap<>();
        
        if (delimStringOfJobIds == null){
            _log.log(Level.INFO,"No tasks to examine");
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
            String convertedStatus = Task.IN_QUEUE_STATUS;
            
            if (status.equalsIgnoreCase(RUNNING)) {
                convertedStatus = Task.RUNNING_STATUS;
            } else if (status.equalsIgnoreCase(DONE)) {
                convertedStatus = Task.COMPLETED_STATUS;
            } else if (status.equalsIgnoreCase(FAILED)) {
                convertedStatus = Task.ERROR_STATUS;
            }
            
            jobStatusMap.put(id, convertedStatus);
        }

        _log.log(Level.INFO, "Output from panfishstat:\n{0}", result);
        return jobStatusMap;
    }

    /**
     * Examines the list of Task objects building a CSV list from the
     * Task.getJobId() strings
     *
     * @param tasks
     * @return CSV delimited list of jobIds
     */
    private String getCommaDelimitedStringOfJobIds(List<Task> tasks) throws Exception {
        StringBuilder sb = new StringBuilder();
        
        if (tasks == null || tasks.isEmpty() == true){
            return null;
        }
        
        for (Task t : tasks) {
            if (sb.length() > 0) {
                sb.append(COMMA);
            }
            if (t.getJobId() == null){
                throw new Exception("Task cannot have a null job id");
            }
            sb.append(t.getJobId());
            
        }
        return sb.toString();
    }
    
}
