package edu.ucsd.crbs.cws.cluster;

import edu.ucsd.crbs.cws.workflow.Task;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class TaskCmdScriptCreatorImpl implements TaskCmdScriptCreator {

    @Override
    public String create(String taskDirectory, Task t) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
        //Extract TEMPLATE_CMD_SCRIPT from resources or external source
        //examine Task and extract parameters and write as arguments to KEPLER_SCRIPT_PATH
        //in this script which should be written to taskDirectory/taskCmd.sh
        //make taskDirectory/taskCmd.sh executable
        //return taskDirectory/taskCmd.sh
    }

}
