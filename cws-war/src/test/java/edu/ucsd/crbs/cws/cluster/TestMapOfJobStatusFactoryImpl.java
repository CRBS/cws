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

import edu.ucsd.crbs.cws.util.RunCommandLineProcess;
import edu.ucsd.crbs.cws.workflow.Task;
import java.util.ArrayList;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestMapOfJobStatusFactoryImpl {

    public TestMapOfJobStatusFactoryImpl() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testgetJobStatusMapWithNullEmptyTasksList() throws Exception {
        MapOfJobStatusFactoryImpl mapFac = new MapOfJobStatusFactoryImpl("/bin/false");
        Map<String,String> resMap = mapFac.getJobStatusMap(null);
        assertTrue(resMap.isEmpty() == true);
        
        resMap = mapFac.getJobStatusMap(new ArrayList<Task>());
        assertTrue(resMap.isEmpty() == true);
    }
    
    
    @Test
    public void testgetJobStatusMapWithNTaskThatHasNullJobId() throws Exception {
        MapOfJobStatusFactoryImpl mapFac = new MapOfJobStatusFactoryImpl("/bin/false");
        ArrayList<Task> taskList = new ArrayList<>();
        taskList.add(new Task());
        
        try {
            Map<String,String> resMap = mapFac.getJobStatusMap(taskList);
            fail("Expected exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("Task cannot have a null job id"));
        }
    }
    
    //test where panfishstat fails (non zero exit code)
    @Test
    public void testgetJobStatusMapWherePanfishstatfails() throws Exception {
        MapOfJobStatusFactoryImpl mapFac = new MapOfJobStatusFactoryImpl("/bin/false");
        
        Task myTask = new Task();
        myTask.setJobId("1");
        ArrayList<Task> taskList = new ArrayList<>();
        taskList.add(myTask);
        try {
            Map<String,String> resMap = mapFac.getJobStatusMap(taskList);
            fail("Expected exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("Non zero exit code"));
        }
    }
    
    //test where panfishstat has no output
    @Test
    public void testgetJobStatusMapWherePanfishstatHasNoOutput() throws Exception {
        MapOfJobStatusFactoryImpl mapFac = new MapOfJobStatusFactoryImpl("/bin/true");
        
        Task myTask = new Task();
        myTask.setJobId("1");
        ArrayList<Task> taskList = new ArrayList<>();
        taskList.add(myTask);
        Map<String,String> resMap = mapFac.getJobStatusMap(taskList);
        assertTrue(resMap.isEmpty() == true);
        
    }
    //test where we have valid output for tasks
    @Test
    public void testgetJobStatusMapWithOneTask() throws Exception {
        MapOfJobStatusFactoryImpl mapFac = new MapOfJobStatusFactoryImpl("cmd");
        
        Task myTask = new Task();
        myTask.setJobId("1");
        ArrayList<Task> taskList = new ArrayList<>();
        taskList.add(myTask);

        RunCommandLineProcess mockCmdRunner = mock(RunCommandLineProcess.class);
        
        when(mockCmdRunner.runCommandLineProcess("cmd",
                MapOfJobStatusFactoryImpl.STATUSOFJOBID,"1")).thenReturn("1="+
                        MapOfJobStatusFactoryImpl.RUNNING+"\n");
        
        mapFac._runCommandLineProcess = mockCmdRunner;
        
        Map<String,String> resMap = mapFac.getJobStatusMap(taskList);
        assertTrue(resMap.isEmpty() == false);
        assertTrue(resMap.keySet().size() == 1);
        assertTrue(resMap.containsKey("1") == true);
        assertTrue(resMap.get("1").equals(Task.RUNNING_STATUS));
    }
    
    //test where we have valid output for tasks
    @Test
    public void testgetJobStatusMapWithTwoTasks() throws Exception {
        MapOfJobStatusFactoryImpl mapFac = new MapOfJobStatusFactoryImpl("cmd");
        
        Task myTask = new Task();
        myTask.setJobId("1");
        ArrayList<Task> taskList = new ArrayList<>();
        taskList.add(myTask);
        
        myTask = new Task();
        myTask.setJobId("2");
        taskList.add(myTask);

        RunCommandLineProcess mockCmdRunner = mock(RunCommandLineProcess.class);
        
        when(mockCmdRunner.runCommandLineProcess("cmd",
                MapOfJobStatusFactoryImpl.STATUSOFJOBID,"1,2")).thenReturn("1="+
                        MapOfJobStatusFactoryImpl.RUNNING+"\n"+"2="+
                        MapOfJobStatusFactoryImpl.DONE+"\n");
        
        mapFac._runCommandLineProcess = mockCmdRunner;
        
        Map<String,String> resMap = mapFac.getJobStatusMap(taskList);
        assertTrue(resMap.isEmpty() == false);
        assertTrue(resMap.keySet().size() == 2);
        assertTrue(resMap.containsKey("1") == true);
        assertTrue(resMap.get("1").equals(Task.RUNNING_STATUS));
        assertTrue(resMap.containsKey("2") == true);
        assertTrue(resMap.get("2").equals(Task.COMPLETED_STATUS));
    }
    

}