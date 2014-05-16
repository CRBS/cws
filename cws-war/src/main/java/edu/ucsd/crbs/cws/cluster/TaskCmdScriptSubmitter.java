package edu.ucsd.crbs.cws.cluster;

import edu.ucsd.crbs.cws.workflow.Task;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public interface TaskCmdScriptSubmitter {
    
    public String submit(final String cmdScript,Task t) throws Exception;
}
