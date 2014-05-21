package edu.ucsd.crbs.cws.cluster;

import edu.ucsd.crbs.cws.workflow.Task;

/**
 * Classes implementing this interface can submit or run a script that runs
 * a Workflow Task
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public interface TaskCmdScriptSubmitter {
    
    /**
     * Run the cmdScript either directly or by submitting to some
     * batch processing system
     * @param cmdScript Full path to program/script to run
     * @param t Task that will be run by cmdScript
     * @return jobid from batch processing system to track job progress
     * @throws Exception 
     */
    public String submit(final String cmdScript,
            Task t) throws Exception;
}
