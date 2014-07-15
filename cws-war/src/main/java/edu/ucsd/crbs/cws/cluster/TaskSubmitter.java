package edu.ucsd.crbs.cws.cluster;

import edu.ucsd.crbs.cws.dao.TaskDAO;
import edu.ucsd.crbs.cws.workflow.Task;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Submits Task objects to local cluster via SGE
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class TaskSubmitter {

    private static final Logger log
            = Logger.getLogger(TaskSubmitter.class.getName());

    TaskDirectoryCreator _directoryCreator;
    TaskCmdScriptCreator _cmdScriptCreator;
    TaskCmdScriptSubmitter _cmdScriptSubmitter;
    SyncWorkflowFileToFileSystem _workflowSync;
    WorkspaceFilePathSetter _workspacePathSetter;
    private TaskDAO _taskDAO;

    /**
     * Constructor
     *
     * @param taskDAO
     * @param workflowExecDir Directory under where workflow Tasks should be run
     * @param workflowsDir Directory where workflows are stored
     * @param keplerScript Full path to Kepler program
     * @param panfishCast
     * @param queue
     * @param login
     * @param token
     * @param url
     */
    public TaskSubmitter(TaskDAO taskDAO,
            final String workflowExecDir,
            final String workflowsDir,
            final String keplerScript,
            final String panfishCast,
            final String queue,
            final String login,
            final String token,
            final String url) {
        _directoryCreator = new TaskDirectoryCreatorImpl(workflowExecDir);
        _cmdScriptCreator = new TaskCmdScriptCreatorImpl(workflowsDir, keplerScript);
        _cmdScriptSubmitter = new TaskCmdScriptSubmitterImpl(panfishCast, queue);
        _workflowSync = new SyncWorkflowFileToFileSystemImpl(workflowsDir, url, login, token);

        _taskDAO = taskDAO;
    }

    /**
     * Submits task to local SGE cluster. This method creates the necessary
     * files and directories. This method will then update the jobId value in
     * the Task and set the status to correct state.
     *
     * @throws Exception If there was a problem creating or submitting the Task
     */
    public void submitTasks() throws Exception {
        log.log(Level.INFO, "Looking for new tasks to submit...");

        List<Task> tasks = _taskDAO.getTasks(null, null, true, false, false);

        if (tasks != null) {
            log.log(Level.INFO, " found {0} tasks need to be submitted", tasks.size());
            for (Task t : tasks) {

                //check if workspace files are syncd.  If not update status
                // to workspace sync and move on to the next Task
                if (_workspacePathSetter.setPaths(t) == false) {
                    if (!t.getStatus().equals(Task.WORKSPACE_SYNC_STATUS)) {
                        _taskDAO.update(t.getId(), Task.WORKSPACE_SYNC_STATUS, null, null, null,
                                null, null, null, false,
                                null);
                        continue;
                    }
                }

                log.log(Level.INFO, "\tSubmitting Task: ({0}) {1}",
                        new Object[]{t.getId(), t.getName()});

                submitTask(t);

                _taskDAO.update(t.getId(), Task.PENDING_STATUS, null, null, null,
                        t.getSubmitDate().getTime(), null, null, true,
                        t.getJobId());
            }
        } else {
            log.log(Level.INFO, " no tasks need to be submitted");
        }
    }

    private void submitTask(Task t) throws Exception {
        _workflowSync.sync(t.getWorkflow());
        String taskDir = _directoryCreator.create(t);
        String cmdScript = _cmdScriptCreator.create(taskDir, t);
        _cmdScriptSubmitter.submit(cmdScript, t);
    }
}
