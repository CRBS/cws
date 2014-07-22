package edu.ucsd.crbs.cws.cluster;

import edu.ucsd.crbs.cws.workflow.Task;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public interface TaskDirectoryCreator {
    
    public String create(Task t) throws Exception;
}
