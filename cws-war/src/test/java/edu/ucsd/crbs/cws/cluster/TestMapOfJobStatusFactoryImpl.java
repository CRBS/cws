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
import edu.ucsd.crbs.cws.workflow.Job;
import java.io.File;
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

    public static String TRUE_BINARY = File.separator+"bin"+File.separator+"true";
    public static String FALSE_BINARY = File.separator+"bin"+File.separator+"false";
    
    public TestMapOfJobStatusFactoryImpl() {
        TRUE_BINARY = getBinary(TRUE_BINARY);
        FALSE_BINARY = getBinary(FALSE_BINARY);
    }
    
    public static String getBinary(final String basePath){
        File baseCheck = new File(basePath);
        if (!baseCheck.exists()){
            return File.separator+"usr"+basePath;
        }
        return basePath;
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
        MapOfJobStatusFactoryImpl mapFac = new MapOfJobStatusFactoryImpl(FALSE_BINARY);
        Map<String,String> resMap = mapFac.getJobStatusMap(null);
        assertTrue(resMap.isEmpty() == true);
        
        resMap = mapFac.getJobStatusMap(new ArrayList<Job>());
        assertTrue(resMap.isEmpty() == true);
    }
    
    
    @Test
    public void testgetJobStatusMapWithNTaskThatHasNullJobId() throws Exception {
        MapOfJobStatusFactoryImpl mapFac = new MapOfJobStatusFactoryImpl(FALSE_BINARY);
        ArrayList<Job> jobList = new ArrayList<>();
        jobList.add(new Job());

        Map<String, String> resMap = mapFac.getJobStatusMap(jobList);
        assertTrue(resMap.size() == 0);
    }
    
    //test where panfishstat fails (non zero exit code)
    @Test
    public void testgetJobStatusMapWherePanfishstatfails() throws Exception {
        MapOfJobStatusFactoryImpl mapFac = new MapOfJobStatusFactoryImpl(FALSE_BINARY);
        
        Job myTask = new Job();
        myTask.setSchedulerJobId("1");
        ArrayList<Job> jobList = new ArrayList<>();
        jobList.add(myTask);
        try {
            Map<String,String> resMap = mapFac.getJobStatusMap(jobList);
            fail("Expected exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("Non zero exit code"));
        }
    }
    
    //test where panfishstat has no output
    @Test
    public void testgetJobStatusMapWherePanfishstatHasNoOutput() throws Exception {
        MapOfJobStatusFactoryImpl mapFac = new MapOfJobStatusFactoryImpl(TRUE_BINARY);
        
        Job myTask = new Job();
        myTask.setSchedulerJobId("1");
        ArrayList<Job> jobList = new ArrayList<>();
        jobList.add(myTask);
        Map<String,String> resMap = mapFac.getJobStatusMap(jobList);
        assertTrue(resMap.isEmpty() == true);
        
    }
    //test where we have valid output for jobs
    @Test
    public void testgetJobStatusMapWithOneTask() throws Exception {
        MapOfJobStatusFactoryImpl mapFac = new MapOfJobStatusFactoryImpl("cmd");
        
        Job myTask = new Job();
        myTask.setSchedulerJobId("1");
        ArrayList<Job> jobList = new ArrayList<>();
        jobList.add(myTask);

        RunCommandLineProcess mockCmdRunner = mock(RunCommandLineProcess.class);
        
        when(mockCmdRunner.runCommandLineProcess("cmd",
                MapOfJobStatusFactoryImpl.STATUSOFJOBID,"1")).thenReturn("1="+
                        MapOfJobStatusFactoryImpl.RUNNING+"\n");
        
        mapFac._runCommandLineProcess = mockCmdRunner;
        
        Map<String,String> resMap = mapFac.getJobStatusMap(jobList);
        assertTrue(resMap.isEmpty() == false);
        assertTrue(resMap.keySet().size() == 1);
        assertTrue(resMap.containsKey("1") == true);
        assertTrue(resMap.get("1").equals(Job.RUNNING_STATUS));
    }
    
    //test where we have valid output for jobs
    @Test
    public void testgetJobStatusMapWithTwoTasks() throws Exception {
        MapOfJobStatusFactoryImpl mapFac = new MapOfJobStatusFactoryImpl("cmd");
        
        Job myTask = new Job();
        myTask.setSchedulerJobId("1");
        ArrayList<Job> jobList = new ArrayList<>();
        jobList.add(myTask);
        
        myTask = new Job();
        myTask.setSchedulerJobId("2");
        jobList.add(myTask);

        RunCommandLineProcess mockCmdRunner = mock(RunCommandLineProcess.class);
        
        when(mockCmdRunner.runCommandLineProcess("cmd",
                MapOfJobStatusFactoryImpl.STATUSOFJOBID,"1,2")).thenReturn("1="+
                        MapOfJobStatusFactoryImpl.RUNNING+"\n"+"2="+
                        MapOfJobStatusFactoryImpl.DONE+"\n");
        
        mapFac._runCommandLineProcess = mockCmdRunner;
        
        Map<String,String> resMap = mapFac.getJobStatusMap(jobList);
        assertTrue(resMap.isEmpty() == false);
        assertTrue(resMap.keySet().size() == 2);
        assertTrue(resMap.containsKey("1") == true);
        assertTrue(resMap.get("1").equals(Job.RUNNING_STATUS));
        assertTrue(resMap.containsKey("2") == true);
        assertTrue(resMap.get("2").equals(Job.COMPLETED_STATUS));
    }
    
    //test where we have valid output for jobs
    @Test
    public void testgetJobStatusMapWithThreeTasks() throws Exception {
        MapOfJobStatusFactoryImpl mapFac = new MapOfJobStatusFactoryImpl("cmd");
        
        Job myTask = new Job();
        myTask.setSchedulerJobId("1");
        ArrayList<Job> jobList = new ArrayList<>();
        jobList.add(myTask);
        
        myTask = new Job();
        myTask.setSchedulerJobId("2");
        jobList.add(myTask);

        myTask = new Job();
        myTask.setSchedulerJobId("3");
        jobList.add(myTask);

        RunCommandLineProcess mockCmdRunner = mock(RunCommandLineProcess.class);
        
        when(mockCmdRunner.runCommandLineProcess("cmd",
                MapOfJobStatusFactoryImpl.STATUSOFJOBID,"1,2,3")).thenReturn("1="+
                        MapOfJobStatusFactoryImpl.RUNNING+"\n"+"3=unknown\n2="+
                        MapOfJobStatusFactoryImpl.DONE+"\n");
        
        mapFac._runCommandLineProcess = mockCmdRunner;
        
        Map<String,String> resMap = mapFac.getJobStatusMap(jobList);
        assertTrue(resMap.isEmpty() == false);
        assertTrue(resMap.keySet().size() == 3);
        assertTrue(resMap.containsKey("1") == true);
        assertTrue(resMap.get("1").equals(Job.RUNNING_STATUS));
        assertTrue(resMap.containsKey("2") == true);
        assertTrue(resMap.get("2").equals(Job.COMPLETED_STATUS));
        assertTrue(resMap.containsKey("3") == true);
        assertTrue(resMap.get("3").equals(Job.IN_QUEUE_STATUS));

    }

}