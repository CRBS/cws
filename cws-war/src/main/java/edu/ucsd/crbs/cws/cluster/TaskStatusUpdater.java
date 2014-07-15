/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2014 The Regents of the University of California All Rights Reserved
 * 
 * Permission to copy, modify and distribute any part of this CRBS Workflow 
 * Service for educational, research and non-profit purposes, without fee, and
 * without a written agreement is hereby granted, provided that the above 
 * copyright notice, this paragraph and the following three paragraphs appear
 * in all copies.
 * 
 * Those desiring to incorporate this CRBS Workflow Service into commercial 
 * products or use for commercial purposes should contact the Technology
 * Transfer Office, University of California, San Diego, 9500 Gilman Drive, 
 * Mail Code 0910, La Jolla, CA 92093-0910, Ph: (858) 534-5815, 
 * FAX: (858) 534-7345, E-MAIL:invent@ucsd.edu.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR 
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING 
 * LOST PROFITS, ARISING OUT OF THE USE OF THIS CRBS Workflow Service, EVEN IF 
 * THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 * 
 * THE CRBS Workflow Service PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE
 * UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, 
 * UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES
 * NO REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR 
 * EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF 
 * THE CRBS Workflow Service WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER
 * RIGHTS. 
 */

package edu.ucsd.crbs.cws.cluster;

import edu.ucsd.crbs.cws.App;
import edu.ucsd.crbs.cws.dao.TaskDAO;
import edu.ucsd.crbs.cws.workflow.Task;
import java.util.Date;
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

    private static final Logger _log
            = Logger.getLogger(TaskStatusUpdater.class.getName());

    TaskDAO _taskDAO;
    MapOfTaskStatusFactory _jobStatusFactory;

    /**
     * Constructor
     *
     * @param taskDAO Used to get Task objects and update Task objects
     * @param jobStatusFactory class to obtain status of Tasks from
     */
    public TaskStatusUpdater(TaskDAO taskDAO,MapOfTaskStatusFactory jobStatusFactory) {
        _taskDAO = taskDAO;
        _jobStatusFactory = jobStatusFactory;
    }

    /**
     * Query for all tasks that have not completed and attempt to update their
     * status
     *
     * @throws Exception
     */
    public void updateTasks() throws Exception {

        _log.log(Level.INFO, "Updating status for uncompleted tasks...");
        List<Task> tasks = _taskDAO.getTasks(null, App.NOT_COMPLETED_STATUSES, false, false, false);
        if (tasks != null && tasks.isEmpty() == false) {

            _log.log(Level.INFO, " found {0} tasks to possibly update", tasks.size());
            Map<String, String> jobStatusMap = _jobStatusFactory.getJobStatusMap(tasks);
            
            for (Task t : tasks) {
                if (jobStatusMap.containsKey(t.getJobId())) {
                    String returnedStatus = jobStatusMap.get(t.getJobId());
                    if (!returnedStatus.equals(t.getStatus())) {
                        _log.log(Level.INFO, 
                                "\tTask: ({0}) {1} old status: {2} new status: {3}", 
                                new Object[]{t.getId(), t.getName(), 
                                    t.getStatus(), returnedStatus});
                        
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
                                null, startDate, finishDate, true, null);
                    }
                }
            }
        } else {
            _log.log(Level.INFO, "no tasks to update");
        }
    }
}
