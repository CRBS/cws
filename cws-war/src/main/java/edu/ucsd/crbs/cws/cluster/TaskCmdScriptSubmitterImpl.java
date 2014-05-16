package edu.ucsd.crbs.cws.cluster;

import edu.ucsd.crbs.cws.workflow.Task;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class TaskCmdScriptSubmitterImpl implements TaskCmdScriptSubmitter {

    @Override
    public String submit(String cmdScript, Task t) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //cd to cmdScriptDir/outputs
        //use QSUB to submit cmdScript
        //parse SGE id and return it
    }

}
