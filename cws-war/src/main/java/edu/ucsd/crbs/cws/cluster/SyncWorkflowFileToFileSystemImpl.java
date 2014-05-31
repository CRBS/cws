package edu.ucsd.crbs.cws.cluster;

import edu.ucsd.crbs.cws.workflow.Workflow;

/**
 * Checks and persists Workflow file to file system
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class SyncWorkflowFileToFileSystemImpl implements SyncWorkflowFileToFileSystem {

    /**
     * Code checks if File for Workflow is on the filesystem.  If not code downloads
     * workflow and writes to file system.
     * @param w
     * @throws Exception If any error is encountered
     */
    @Override
    public void sync(Workflow w) throws Exception {
        //TODO need to implement with this logic:
        //http://stackoverflow.com/questions/8928037/how-do-i-get-to-store-a-downloaded-file-with-java-and-jersey
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
