package edu.ucsd.crbs.cws.cluster;

import static edu.ucsd.crbs.cws.App.NOT_COMPLETED_STATUSES;
import edu.ucsd.crbs.cws.dao.TaskDAO;
import edu.ucsd.crbs.cws.workflow.Task;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Updates status of Tasks by querying Panfish
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class TaskStatusUpdater {

    private static final Logger log
            = Logger.getLogger(TaskStatusUpdater.class.getName());

    private TaskDAO _taskDAO;
    private String _panfishStat;

    /**
     * Constructor
     *
     * @param taskDAO Used to get Task objects and update Task objects
     * @param panfishStat Path to panfishstat binary used to get status of jobs
     */
    public TaskStatusUpdater(TaskDAO taskDAO, final String panfishStat) {
        _taskDAO = taskDAO;
        _panfishStat = panfishStat;
    }

    /**
     * Query for all tasks that have not completed and attempt to update their
     * status
     *
     * @throws Exception
     */
    public void updateTasks() throws Exception {

        log.log(Level.INFO, "Updating status for uncompleted tasks...");
        List<Task> tasks = _taskDAO.getTasks(null, NOT_COMPLETED_STATUSES, false, false, false);
        if (tasks != null && tasks.isEmpty() == false) {

            log.log(Level.INFO, " found {0} tasks to possibly update", tasks.size());
            Map<String, String> jobStatusMap = getMapOfJobStatusFromPanfish(getCommaDelimitedStringOfJobIds(tasks));

            for (Task t : tasks) {
                if (jobStatusMap.containsKey(t.getJobId())) {
                    String returnedStatus = jobStatusMap.get(t.getJobId());
                    if (!returnedStatus.equals(t.getStatus())) {
                        log.log(Level.INFO, "\tTask: (" + t.getId() + ") "
                                + t.getName() + " old status: " + t.getStatus() + " new status: " + returnedStatus);
                        t.setStatus(returnedStatus);
                        
                        Long startDate = null;
                        Long finishDate = null;
                        
                        if (returnedStatus.equals(Task.RUNNING_STATUS)){
                            t.setStartDate(new Date());
                            startDate = t.getStartDate().getTime();
                            
                        }
                        else if (returnedStatus.equals(Task.COMPLETED_STATUS)){
                            t.setFinishDate(new Date());
                            finishDate = t.getFinishDate().getTime();
                        }
                        _taskDAO.update(t.getId(), t.getStatus(), null, null, null,
                                null, startDate, finishDate, true, null, null);
                    }
                }
            }
        } else {
            log.log(Level.INFO, "no tasks to update");
        }
    }

    /**
     * Invokes panfishstat to get job statuses
     *
     * @param csvString Comma delimited string of job ids to query status on
     * @return Map where key is job id and status as the value
     * @throws Exception
     */
    private Map<String, String> getMapOfJobStatusFromPanfish(final String csvString) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(_panfishStat,
                "--statusofjobid", csvString);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

        Map<String, String> jobStatusMap = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        
        String line = reader.readLine();
        while (line != null) {
            sb.append(line+"\n");
            int equalPos = line.indexOf('=');
            if (equalPos > 0 && equalPos < line.length() - 1) {
                String id = line.substring(0, equalPos);
                String status = line.substring(equalPos + 1);
                String convertedStatus = Task.IN_QUEUE_STATUS;

                if (status.equalsIgnoreCase("running")) {
                    convertedStatus = Task.RUNNING_STATUS;
                } else if (status.equalsIgnoreCase("done")) {
                    convertedStatus = Task.COMPLETED_STATUS;
                } else if (status.equalsIgnoreCase("failed")) {
                    convertedStatus = Task.ERROR_STATUS;
                }

                jobStatusMap.put(id, convertedStatus);
            }

            line = reader.readLine();
        }
        reader.close();

        if (p.waitFor() != 0) {
            throw new Exception("Non zero exit code received from Panfish: " + sb.toString());
        }
        log.log(Level.INFO, "Output from panfishstat:\n" + sb.toString());
        return jobStatusMap;
    }

    /**
     * Examines the list of Task objects building a CSV list from the
     * Task.getJobId() strings
     *
     * @param tasks
     * @return CSV delimited list of jobIds
     */
    private String getCommaDelimitedStringOfJobIds(List<Task> tasks) {
        StringBuilder sb = new StringBuilder();
        for (Task t : tasks) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(t.getJobId());
        }
        return sb.toString();
    }

}
