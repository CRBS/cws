package edu.ucsd.crbs.cws.cluster;

import edu.ucsd.crbs.cws.workflow.Task;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public interface TaskCmdScriptCreator {
    
    public String create(final String taskDirectory,Task t) throws Exception;
}
