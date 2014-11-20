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

import edu.ucsd.crbs.cws.rest.Constants;
import edu.ucsd.crbs.cws.util.RunCommandLineProcessImpl;
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.Workflow;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;



/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestJobCmdScriptCreatorImpl {

    
     
    @Rule
    public TemporaryFolder Folder = new TemporaryFolder();
    
    public TestJobCmdScriptCreatorImpl() {
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

    private JobEmailNotificationData createJobEmailNotificationData() {

        JobEmailNotificationData emailNotifyData = new JobEmailNotificationData();
        emailNotifyData.setBccEmail("bcc");
        emailNotifyData.setHelpEmail("help");
        emailNotifyData.setPortalName("portalname");
        emailNotifyData.setPortalURL("portalurl");
        emailNotifyData.setProject("project");
        return emailNotifyData;
    }
    
    
    @Test
    public void testCreateWithNullJobDirectory() throws Exception{
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir","kepler.sh","register.jar",
        null);

        try {
            scriptCreator.create(null, new Job(),new Long(10));
            fail("Expected exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("Job Directory cannot be null"));
        }
    }

    @Test
    public void testCreateWithNullJob() throws Exception{
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir","kepler.sh","register.jar",
        null);

        try {
            scriptCreator.create("blah", null,new Long(10));
            fail("Expected exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("Job cannot be null"));
        }
    }

    
    @Test
    public void testCreateWithNullWorkflow() throws Exception{
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir","kepler.sh","register.jar",
        null);

        try {
            scriptCreator.create("blah", new Job(),new Long(10));
            fail("Expected exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("Workflow cannot be null"));
        }
    }
    
    @Test
    public void testCreateWithNullWorkflowId() throws Exception{
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir","kepler.sh","register.jar",
        null);

        try {
            Job j = new Job();
            j.setWorkflow(new Workflow());
            scriptCreator.create("blah", j,new Long(10));
            fail("Expected exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("Workflow id cannot be null"));
        }
    }
    
    @Test
    public void testCreateWithJobWithNoArgs() throws Exception{
        File tempDirectory = Folder.newFolder();
        File outputsDir = new File(tempDirectory+File.separator+Constants.OUTPUTS_DIR_NAME);
        assertTrue(outputsDir.mkdirs());
        
        JobEmailNotificationData emailNotifyData = createJobEmailNotificationData();
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir","kepler.sh","register.jar",
        emailNotifyData);
        
        Job j = new Job();
        Workflow w = new Workflow();
        w.setId(new Long(5));
        j.setWorkflow(w);
        
        String jobCmd = scriptCreator.create(tempDirectory.getAbsolutePath(), j,new Long(10));
                
        assertTrue(jobCmd != null);
        assertTrue(jobCmd.equals(outputsDir.getAbsolutePath()+File.separator+JobCmdScriptCreatorImpl.JOB_CMD_SH));
        File checkCmdFile = new File(jobCmd);
        assertTrue(checkCmdFile.canExecute());
        
        List<String> lines = IOUtils.readLines(new FileReader(jobCmd));
        assertTrue(lines != null);
        for (String line : lines){
            if (line.startsWith("kepler.sh")){
                assertTrue(":"+line+":",line.equals("kepler.sh  -runwf -redirectgui "+outputsDir.getAbsolutePath()+" /workflowsdir/5/5.kar &"));
            }
            if (line.startsWith("EMAIL_ADDR=")){
                assertTrue(line.equals("EMAIL_ADDR=\"\""));
            }
            if (line.startsWith("java")){
                assertTrue(line,line.equals("java  -jar register.jar --updatepath \"10\" --path \""+
                        outputsDir.getAbsolutePath()+
                        "\" --size `du "+outputsDir.getAbsolutePath()+
                        " -bs | sed \"s/\\W*\\/.*//\"` $workspaceStatusFlag >> "+
                        tempDirectory.getAbsolutePath()+
                        "/updateworkspacefile.out 2>&1"));
            }
        }
    }
    
    @Test
    public void testCreateAndRunScriptWithFakeKeplerThatFails() throws Exception{
        assumeTrue(SystemUtils.IS_OS_UNIX);
        File baseDirectory = Folder.newFolder();
        File tempDirectory = new File(baseDirectory+File.separator+"subdir");
        File outputsDir = new File(tempDirectory+File.separator+Constants.OUTPUTS_DIR_NAME);
        assertTrue(outputsDir.mkdirs());
        
        JobEmailNotificationData emailNotifyData = createJobEmailNotificationData();
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir","/bin/false","register.jar",
        emailNotifyData);
        scriptCreator.setJavaBinaryPath("/bin/true");
        
        Job j = new Job();
        Workflow w = new Workflow();
        w.setId(new Long(5));
        j.setWorkflow(w);
        
        String jobCmd = scriptCreator.create(tempDirectory.getAbsolutePath(), j,new Long(10));
                
        assertTrue(jobCmd != null);
        assertTrue(jobCmd.equals(outputsDir.getAbsolutePath()+File.separator+JobCmdScriptCreatorImpl.JOB_CMD_SH));
        File checkCmdFile = new File(jobCmd);
        assertTrue(checkCmdFile.canExecute());
       
        RunCommandLineProcessImpl rclpi = new RunCommandLineProcessImpl();
        rclpi.setWorkingDirectory(tempDirectory.getAbsolutePath());

        try {
            String result = rclpi.runCommandLineProcess(jobCmd);
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("Non zero exit code (1)"));
        }
        
        String logFile = baseDirectory.getAbsoluteFile()+File.separator+"job...log";
        File checkLogFile = new File(logFile);
        assertTrue(logFile+" and we ran "+jobCmd,checkLogFile.exists());
        List<String> lines = IOUtils.readLines(new FileReader(logFile));
        for (String line : lines){
            if (line.startsWith("exitcode: ")){
                assertTrue(line,line.equals("exitcode: 1"));
            }
        }
        
       
    }
    
    @Test
    public void testCreateAndRunScriptWithFakeKeplerThatSucceeds() throws Exception{
        assumeTrue(SystemUtils.IS_OS_UNIX);
        File baseDirectory = Folder.newFolder();
        File tempDirectory = new File(baseDirectory+File.separator+"subdir");
        File outputsDir = new File(tempDirectory+File.separator+Constants.OUTPUTS_DIR_NAME);
        assertTrue(outputsDir.mkdirs());
        
        JobEmailNotificationData emailNotifyData = createJobEmailNotificationData();
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir","/bin/true","register.jar",
        emailNotifyData);
        scriptCreator.setJavaBinaryPath("/bin/echo");
        
        Job j = new Job();
        Workflow w = new Workflow();
        w.setId(new Long(5));
        j.setWorkflow(w);
        
        String jobCmd = scriptCreator.create(tempDirectory.getAbsolutePath(), j,new Long(2345));
                
        assertTrue(jobCmd != null);
        assertTrue(jobCmd.equals(outputsDir.getAbsolutePath()+File.separator+JobCmdScriptCreatorImpl.JOB_CMD_SH));
        File checkCmdFile = new File(jobCmd);
        assertTrue(checkCmdFile.canExecute());
       
        RunCommandLineProcessImpl rclpi = new RunCommandLineProcessImpl();
        rclpi.setWorkingDirectory(tempDirectory.getAbsolutePath());

        String result = rclpi.runCommandLineProcess(jobCmd);
       
        
        String logFile = baseDirectory.getAbsoluteFile()+File.separator+"job...log";
        File checkLogFile = new File(logFile);
        assertTrue(logFile+" and we ran "+jobCmd,checkLogFile.exists());
        List<String> lines = IOUtils.readLines(new FileReader(logFile));
        for (String line : lines){
            if (line.startsWith("exitcode: ")){
                assertTrue(line,line.equals("exitcode: 0"));
            }
        }
        
         String updateFile = tempDirectory.getAbsoluteFile()+File.separator+
                "updateworkspacefile.out";
        
        lines = IOUtils.readLines(new FileReader(updateFile));
        for (String line : lines){
            if (line.startsWith("-jar")){
                assertTrue(line,line.startsWith("-jar register.jar --updatepath 2345 --path "+
                        outputsDir.getAbsolutePath()+
                        " --size "));
                assertTrue(line,line.endsWith(" --workspacefilefailed false"));
            }
        }
       
    }
    
    @Test
    public void testCreateAndRunScriptWithFakeKeplerThatGeneratesWorkflowFailedFile() throws Exception{
        assumeTrue(SystemUtils.IS_OS_UNIX);
        File baseDirectory = Folder.newFolder();
        File tempDirectory = new File(baseDirectory+File.separator+"subdir");
        File outputsDir = new File(tempDirectory+File.separator+Constants.OUTPUTS_DIR_NAME);
        assertTrue(outputsDir.mkdirs());
        
        JobEmailNotificationData emailNotifyData = createJobEmailNotificationData();
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir",
                "/bin/echo -e \"simple.error.message=fake fail\\\\n"+
                "detailed.error.message=fake fail detailed"+
                "\\\\n\" > "+outputsDir.getAbsolutePath()+File.separator+"WORKFLOW.FAILED.txt","register.jar",
        emailNotifyData);
        scriptCreator.setJavaBinaryPath("/bin/echo");
        
        Job j = new Job();
        Workflow w = new Workflow();
        w.setId(new Long(5));
        j.setWorkflow(w);
        
        String jobCmd = scriptCreator.create(tempDirectory.getAbsolutePath(), j,new Long(10));
                
        assertTrue(jobCmd != null);
        assertTrue(jobCmd.equals(outputsDir.getAbsolutePath()+File.separator+JobCmdScriptCreatorImpl.JOB_CMD_SH));
        File checkCmdFile = new File(jobCmd);
        assertTrue(checkCmdFile.canExecute());
       
        RunCommandLineProcessImpl rclpi = new RunCommandLineProcessImpl();
        rclpi.setWorkingDirectory(outputsDir.getAbsolutePath());
        String result;
        try {
            result = rclpi.runCommandLineProcess(jobCmd);
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("Non zero exit code (101)"));
        }
        
        String logFile = tempDirectory.getAbsoluteFile()+File.separator+"job...log";
        File checkLogFile = new File(logFile);
        assertTrue(logFile+" and we ran "+jobCmd,checkLogFile.exists());
        List<String> lines = IOUtils.readLines(new FileReader(logFile));
        for (String line : lines){
            if (line.startsWith("exitcode: ")){
                assertTrue(line,line.equals("exitcode: 101"));
            }
        }
        
        String updateFile = tempDirectory.getAbsoluteFile()+File.separator+
                "updateworkspacefile.out";
        
        lines = IOUtils.readLines(new FileReader(updateFile));
        for (String line : lines){
            if (line.startsWith("-jar")){
                assertTrue(line,line.startsWith("-jar register.jar --updatepath 10 --path "+
                        outputsDir.getAbsolutePath()+
                        " --size "));
                assertTrue(line,line.endsWith(" --workspacefilefailed true"));
            }
        }
        
        
       
    }
    
    
    
    @Test
    public void testCreateAndRunScriptWithFakeKeplerThatSimulatesUSR2Signal() throws Exception{
        assumeTrue(SystemUtils.IS_OS_UNIX);
        File baseDirectory = Folder.newFolder();
        File tempDirectory = new File(baseDirectory+File.separator+"subdir");
        File outputsDir = new File(tempDirectory+File.separator+Constants.OUTPUTS_DIR_NAME);
        assertTrue(outputsDir.mkdirs());
        
        JobEmailNotificationData emailNotifyData = createJobEmailNotificationData();
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir",
                "kill -s USR2 $$;sleep 100","register.jar",
        emailNotifyData);
        scriptCreator.setJavaBinaryPath("/bin/echo");
        
        Job j = new Job();
        Workflow w = new Workflow();
        w.setId(new Long(5));
        j.setWorkflow(w);
        
        String jobCmd = scriptCreator.create(tempDirectory.getAbsolutePath(), j,new Long(10));
                
        assertTrue(jobCmd != null);
        assertTrue(jobCmd.equals(outputsDir.getAbsolutePath()+File.separator+JobCmdScriptCreatorImpl.JOB_CMD_SH));
        File checkCmdFile = new File(jobCmd);
        assertTrue(checkCmdFile.canExecute());
       
        RunCommandLineProcessImpl rclpi = new RunCommandLineProcessImpl();
        rclpi.setWorkingDirectory(outputsDir.getAbsolutePath());
        String result;
        try {
            result = rclpi.runCommandLineProcess(jobCmd);
        }
        catch(Exception ex){
            assertTrue(ex.getMessage(),ex.getMessage().startsWith("Non zero exit code (100)"));
        }
        
        String logFile = tempDirectory.getAbsoluteFile()+File.separator+"job...log";
        File checkLogFile = new File(logFile);
        assertTrue(logFile+" and we ran "+jobCmd,checkLogFile.exists());
        List<String> lines = IOUtils.readLines(new FileReader(logFile));
        for (String line : lines){
            if (line.startsWith("exitcode: ")){
                assertTrue(line,line.equals("exitcode: 100"));
            }
        }
        
        lines = IOUtils.readLines(new FileReader(outputsDir.getAbsoluteFile()+File.separator+"WORKFLOW.FAILED.txt"));
        for (String line : lines){
            if (line.startsWith("simple.error.message")){
                assertTrue(line,line.equals("simple.error.message=Job killed by scheduler"));
            }
            if (line.startsWith("detailed.error.message")){
                assertTrue(line,line.equals("detailed.error.message=Job received USR2 signal which is the signal to exit"));
            }
        }
        
       
    }
    
    @Test
    public void testCreateAndRunScriptWithFakeKeplerThatSimulatesUSR2SignalButAlreadyHasWorkFlowFailedFile() throws Exception{
        assumeTrue(SystemUtils.IS_OS_UNIX);
        File baseDirectory = Folder.newFolder();
        File tempDirectory = new File(baseDirectory+File.separator+"subdir");
        File outputsDir = new File(tempDirectory+File.separator+Constants.OUTPUTS_DIR_NAME);
        assertTrue(outputsDir.mkdirs());
        
        JobEmailNotificationData emailNotifyData = createJobEmailNotificationData();
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir",
                "kill -s USR2 $$;sleep 100","register.jar",
        emailNotifyData);
        scriptCreator.setJavaBinaryPath("/bin/echo");
        
        Job j = new Job();
        Workflow w = new Workflow();
        w.setId(new Long(5));
        j.setWorkflow(w);
        
        FileWriter fw = new FileWriter(outputsDir.getAbsoluteFile()+File.separator+"WORKFLOW.FAILED.txt");
        fw.write("simple.error.message=simple\n");
        fw.write("detailed.error.message=detailed\n");
        fw.flush();
        fw.close();
        
        
        String jobCmd = scriptCreator.create(tempDirectory.getAbsolutePath(), j,new Long(10));
                
        assertTrue(jobCmd != null);
        assertTrue(jobCmd.equals(outputsDir.getAbsolutePath()+File.separator+JobCmdScriptCreatorImpl.JOB_CMD_SH));
        File checkCmdFile = new File(jobCmd);
        assertTrue(checkCmdFile.canExecute());
       
        RunCommandLineProcessImpl rclpi = new RunCommandLineProcessImpl();
        rclpi.setWorkingDirectory(outputsDir.getAbsolutePath());
        String result;
        try {
            result = rclpi.runCommandLineProcess(jobCmd);
        }
        catch(Exception ex){
            assertTrue(ex.getMessage(),ex.getMessage().startsWith("Non zero exit code (100)"));
        }
        
        String logFile = tempDirectory.getAbsoluteFile()+File.separator+"job...log";
        File checkLogFile = new File(logFile);
        assertTrue(logFile+" and we ran "+jobCmd,checkLogFile.exists());
        List<String> lines = IOUtils.readLines(new FileReader(logFile));
        for (String line : lines){
            if (line.startsWith("exitcode: ")){
                assertTrue(line,line.equals("exitcode: 100"));
            }
        }
        
        lines = IOUtils.readLines(new FileReader(outputsDir.getAbsoluteFile()+File.separator+"WORKFLOW.FAILED.txt"));
        for (String line : lines){
            if (line.startsWith("simple.error.message")){
                assertTrue(line,line.equals("simple.error.message=simple"));
            }
            if (line.startsWith("detailed.error.message")){
                assertTrue(line,line.equals("detailed.error.message=detailed"));
            }
            if (line.startsWith(" Job received")){
                assertTrue(line,line.equals(" Job received USR2 signal which in SGE meant it is about to be killed"));
            }
        }
        
       
    }
}