package edu.ucsd.crbs.cws.cluster;

import edu.ucsd.crbs.cws.rest.Constants;
import edu.ucsd.crbs.cws.workflow.Task;
import java.io.File;

/**
 * Creates directories needed to run a Workflow Task
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class TaskDirectoryCreatorImpl implements TaskDirectoryCreator{

    private final String _baseExecDir;
    
    public TaskDirectoryCreatorImpl(final String baseTaskExecutionDirectory){
        _baseExecDir = baseTaskExecutionDirectory;
    }
    
    /**
     * Creates (TASK_ID)/outputs directory for task
     * @param t
     * @return Path to (TASK_ID)/ directory
     * @throws Exception 
     */
    @Override
    public String create(Task t) throws Exception {
        if (t == null){
            throw new NullPointerException("Task cannot be null");
        }
        
        Long id = t.getId();
        if (id == null){
            throw new NullPointerException("Task id cannot be null");
        }
    
        if (_baseExecDir == null){
            throw new NullPointerException("Base Task Execution directory is null");
        }
        
        File dirToCreate = new File(_baseExecDir+File.separator+t.getOwner()+
                File.separator+id.toString()+File.separator+
                Constants.OUTPUTS_DIR_NAME);
        
        if (dirToCreate.mkdirs() == false){
            throw new Exception("Unable to create directory: "+dirToCreate.getAbsolutePath());
        }
        return dirToCreate.getParentFile().getAbsolutePath();
    }

    
}
