/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ucsd.crbs.cws.cluster;

import edu.ucsd.crbs.cws.workflow.Workflow;

/**
 * Persists Workflow to file system if it is not already there
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public interface SyncWorkflowFileToFileSystem {
    
    public void sync(Workflow w) throws Exception;
}
