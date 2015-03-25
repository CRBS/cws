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

package edu.ucsd.crbs.cws.cluster.submission;

import edu.ucsd.crbs.cws.cluster.JobBinaries;
import edu.ucsd.crbs.cws.cluster.JobEmailNotificationData;
import edu.ucsd.crbs.cws.rest.Constants;
import edu.ucsd.crbs.cws.util.RunCommandLineProcessImpl;
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.Parameter;
import edu.ucsd.crbs.cws.workflow.Workflow;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;



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
        Job.REFS_ENABLED = false;
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
    
    private void printFile(File textFile) throws Exception {
        List<String> lines = IOUtils.readLines(new FileReader(textFile));
        for (String line :lines){
            System.out.println(line);
        }
    }
    
    /**
     * Helper method to try to find true command
     */
    private File getAndCheckForTrueBinaryFile(){
        File checkForTrue = new File("/bin/true");
        if (checkForTrue.exists() == false){
            checkForTrue = new File("/usr/bin/true");
            assumeTrue(checkForTrue.exists());
        }
        return checkForTrue;
    }
    
    /**
     * Helper method to try to find false command
     */
    private File getAndCheckForFalseBinaryFile(){
        File checkForFalse = new File("/bin/false");
        if (checkForFalse.exists() == false){
            checkForFalse = new File("/usr/bin/false");
            assumeTrue(checkForFalse.exists());
        }
        return checkForFalse;
    }
    
    private File checkWorkflowFailed(final String outputsDir,
            final String simple,final String detailed) throws Exception {
        
        File failedFile = new File(outputsDir+File.separator+
                "WORKFLOW.FAILED.txt");
        
        assertTrue(failedFile.exists());
        
        List<String> lines = IOUtils.readLines(new FileReader(failedFile));
        boolean simpleFound = false;
        boolean detailedFound = false;
        for (String line : lines){
            if (line.startsWith("simple.error.message")){
                assertTrue(line,line.equals("simple.error.message="+simple));
                simpleFound = true;
            }
            if (line.startsWith("detailed.error.message")){
                assertTrue(line,line.equals("detailed.error.message="+detailed));
                detailedFound = true;
            }
        }
        assertTrue(simpleFound);
        assertTrue(detailedFound);
        return failedFile;
    }
    
    
    @Test
    public void testCreateWithNullJobDirectory() throws Exception{
        
        JobBinaries jb = new JobBinaries();
        jb.setKeplerScript("kepler.sh");
        jb.setRegisterUpdateJar("register.jar");
        jb.setRetryCount(1);
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir",
                jb,null);

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
        
        JobBinaries jb = new JobBinaries();
        jb.setKeplerScript("kepler.sh");
        jb.setRegisterUpdateJar("register.jar");
        jb.setRetryCount(1);
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir",
                jb,null);

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
        
        JobBinaries jb = new JobBinaries();
        jb.setKeplerScript("kepler.sh");
        jb.setRegisterUpdateJar("register.jar");
        jb.setRetryCount(1);
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir",
                jb,null);

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
        
        JobBinaries jb = new JobBinaries();
        jb.setKeplerScript("kepler.sh");
        jb.setRegisterUpdateJar("register.jar");
        jb.setRetryCount(1);
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir",
                jb,null);

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
        JobBinaries jb = new JobBinaries();
        jb.setKeplerScript("kepler.sh");
        jb.setRegisterUpdateJar("register.jar");
        jb.setRetryCount(1);
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir",
                jb,emailNotifyData);
        
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
        boolean emailFound = false;
        boolean errorEmailFound = false;
        boolean javaFound = false;
        boolean keplerFound = false;
        for (String line : lines){
            if (line.startsWith("kepler.sh")){
                assertTrue(":"+line+":",line.equals("kepler.sh  -runwf -redirectgui "+outputsDir.getAbsolutePath()+" /workflowsdir/5/5.kar &"));
                keplerFound = true;
            }
            if (line.startsWith("EMAIL_ADDR=")){
                assertTrue(line.equals("EMAIL_ADDR=\"\""));
                emailFound = true;
            }
            if (line.startsWith("ERROR_EMAIL_ADDR")){
                assertTrue(line.equals("ERROR_EMAIL_ADDR=\"\""));
                errorEmailFound = true;
            }
            if (line.startsWith("  java")){
                assertTrue(line,line.equals("  java  -jar register.jar --updatepath \"10\" --path \""+
                        outputsDir.getAbsolutePath()+
                        "\" --size `du "+outputsDir.getAbsolutePath()+
                        " -bs | sed \"s/\\W*\\/.*//\"` $workspaceStatusFlag >> "+
                        tempDirectory.getAbsolutePath()+
                        "/updateworkspacefile.out 2>&1"));
                javaFound = true;
            }
        }
        assertTrue(emailFound);
        assertTrue(errorEmailFound);
        assertTrue(javaFound);
        assertTrue(keplerFound);
    }
    
    @Test
    public void testCreateWithErrorEmailsSet() throws Exception{
        File tempDirectory = Folder.newFolder();
        File outputsDir = new File(tempDirectory+File.separator+Constants.OUTPUTS_DIR_NAME);
        assertTrue(outputsDir.mkdirs());
        
        JobEmailNotificationData emailNotifyData = createJobEmailNotificationData();
        emailNotifyData.setErrorEmail("error@error.com");
        JobBinaries jb = new JobBinaries();
        jb.setKeplerScript("kepler.sh");
        jb.setRegisterUpdateJar("register.jar");
        jb.setRetryCount(1);
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir",
                jb,emailNotifyData);
        
        Job j = new Job();
        Workflow w = new Workflow();
        w.setId(new Long(5));
        j.setWorkflow(w);
        Parameter emailParam = new Parameter();
        emailParam.setName(Constants.CWS_NOTIFYEMAIL);
        emailParam.setValue("bob@bob.com");
        ArrayList<Parameter> params = new ArrayList<>();
        params.add(emailParam);
        j.setParameters(params);
        
        String jobCmd = scriptCreator.create(tempDirectory.getAbsolutePath(), j,new Long(10));
                
        assertTrue(jobCmd != null);
        assertTrue(jobCmd.equals(outputsDir.getAbsolutePath()+File.separator+JobCmdScriptCreatorImpl.JOB_CMD_SH));
        File checkCmdFile = new File(jobCmd);
        assertTrue(checkCmdFile.canExecute());
        
        List<String> lines = IOUtils.readLines(new FileReader(jobCmd));
        assertTrue(lines != null);
        boolean emailFound = false;
        boolean errorEmailFound = false;
        for (String line : lines){
            if (line.startsWith("EMAIL_ADDR=")){
                assertTrue(line.equals("EMAIL_ADDR=\"bob@bob.com\""));
                emailFound = true;
            }
            if (line.startsWith("ERROR_EMAIL_ADDR")){
                assertTrue(line.equals("ERROR_EMAIL_ADDR=\"error@error.com\""));
                errorEmailFound = true;
            }
        }
        assertTrue(emailFound);
        assertTrue(errorEmailFound);
    }
    
    @Test
    public void testCreateAndRunScriptWithFakeKeplerThatFailsAndNoEmailsSet() throws Exception{
        assumeTrue(SystemUtils.IS_OS_UNIX);
        File baseDirectory = Folder.newFolder();
        File tempDirectory = new File(baseDirectory+File.separator+"subdir");
        File outputsDir = new File(tempDirectory+File.separator+Constants.OUTPUTS_DIR_NAME);
        assertTrue(outputsDir.mkdirs());
        
        JobEmailNotificationData emailNotifyData = createJobEmailNotificationData();
        
        JobBinaries jb = new JobBinaries();
        jb.setKeplerScript(getAndCheckForFalseBinaryFile().getAbsolutePath());
        jb.setRegisterUpdateJar("register.jar");
        jb.setJavaCommand("/bin/echo");
        jb.setRetryCount(1);
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir",
                jb,emailNotifyData);
        
        File checkForTrue = getAndCheckForTrueBinaryFile();
        
        scriptCreator.setJavaBinaryPath(checkForTrue.getAbsolutePath());
        
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
    public void testCreateAndRunScriptWithFakeKeplerThatSucceedsWithNoEmailSet() throws Exception{
        assumeTrue(SystemUtils.IS_OS_UNIX);
        File baseDirectory = Folder.newFolder();
        File tempDirectory = new File(baseDirectory+File.separator+"subdir");
        File outputsDir = new File(tempDirectory+File.separator+Constants.OUTPUTS_DIR_NAME);
        assertTrue(outputsDir.mkdirs());
        
        JobEmailNotificationData emailNotifyData = createJobEmailNotificationData();
        
        File stderrFile = new File(outputsDir.getAbsolutePath()+File.separator+"stderr");
        assertTrue(stderrFile.createNewFile());
        
        JobBinaries jb = new JobBinaries();
        jb.setKeplerScript(getAndCheckForTrueBinaryFile().getAbsolutePath());
        jb.setRegisterUpdateJar("register.jar");
        jb.setJavaCommand("/bin/echo");
        jb.setRetryCount(1);
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir",
                jb,emailNotifyData);
        
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
    public void testCreateAndRunScriptWithFakeKeplerThatSucceedsWithEmailSetButAllJobValuesAreEmpty() throws Exception{
        assumeTrue(SystemUtils.IS_OS_UNIX);
        File baseDirectory = Folder.newFolder();
        File tempDirectory = new File(baseDirectory+File.separator+"subdir");
        File outputsDir = new File(tempDirectory+File.separator+Constants.OUTPUTS_DIR_NAME);
        assertTrue(outputsDir.mkdirs());
        
        JobEmailNotificationData emailNotifyData = createJobEmailNotificationData();
        
        File stderrFile = new File(outputsDir.getAbsolutePath()+File.separator+"stderr");
        assertTrue(stderrFile.createNewFile());
        
        JobBinaries jb = new JobBinaries();
        jb.setKeplerScript(getAndCheckForTrueBinaryFile().getAbsolutePath());
        jb.setRegisterUpdateJar("register.jar");
        jb.setJavaCommand("/bin/echo");
        jb.setEchoCommand("/bin/echo");
        jb.setMailCommand("cat >> email.${finishedMessage};/bin/echo ");
        jb.setRetryCount(1);
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir",
                jb,emailNotifyData);
        
        Job j = new Job();
        Workflow w = new Workflow();
        w.setId(new Long(5));
        Parameter emailParam = new Parameter();
        emailParam.setName(Constants.CWS_NOTIFYEMAIL);
        emailParam.setValue("bob@bob.com");
        ArrayList<Parameter> params = new ArrayList<>();
        params.add(emailParam);
        j.setParameters(params);
        j.setWorkflow(w);
        
        String jobCmd = scriptCreator.create(tempDirectory.getAbsolutePath(), j,new Long(2345));
        assertTrue(jobCmd != null);
        
        RunCommandLineProcessImpl rclpi = new RunCommandLineProcessImpl();
        rclpi.setWorkingDirectory(outputsDir.getAbsolutePath());

        String result = rclpi.runCommandLineProcess(jobCmd);
        
        assertTrue(result.contains("Sending start email for Unknown to user bob@bob.com and bcc to bcc"));
        assertTrue(result.contains("-s project Workflow Unknown - Unknown has started running -r help -b bcc bob@bob.com"));
        assertTrue(result.contains("Sending done email for Unknown to user bob@bob.com and bcc to bcc"));
        assertTrue(result.contains("-s project Workflow Unknown - Unknown has finished -r help -b bcc bob@bob.com"));
        
        File emailStartFile = new File(outputsDir.getAbsolutePath()+File.separator+"email.");
        
        assertTrue(emailStartFile.exists());
        
        List<String> lines = IOUtils.readLines(new FileReader(emailStartFile));
        boolean dearFound = false;
        boolean yourFound = false;
        boolean pleaseFound = false;
        boolean contactFound = false;
        for (String line : lines){
            if (line.startsWith("Dear")){
                assertTrue(line.contains("Dear Unknown,"));
                dearFound = true;
            }
            if (line.startsWith("Your Unknown workflow job:")){
                assertTrue(line.contains("Your Unknown workflow job: Unknown (Unknown) is now actively running on project resources."));
                yourFound = true;
            }
            if (line.startsWith("Please login to the ")){
                assertTrue(line.contains("Please login to the portalname (portalurl) to check status."));
                pleaseFound = true;
            }
            if (line.startsWith("Contact project")){
                assertTrue(line.contains("Contact project support at help if you have any questions regarding your job."));
                contactFound = true;
            }
        }
        assertTrue(dearFound);
        assertTrue(yourFound);
        assertTrue(pleaseFound);
        assertTrue(contactFound);
        

        dearFound = false;
        yourFound = false;
        pleaseFound = false;
        contactFound = false;
        
        File emailDoneFile = new File(outputsDir.getAbsolutePath()+File.separator+"email.finished");
        lines = IOUtils.readLines(new FileReader(emailStartFile));
        for (String line : lines){
            if (line.startsWith("Dear")){
                assertTrue(line.contains("Dear Unknown,"));
                dearFound = true;
            }
            
            if (line.startsWith("Your Unknown workflow job:")){
                assertTrue(line.contains("Your Unknown workflow job: Unknown (Unknown) is now actively running on project resources."));
                yourFound = true;
            }
            
            if (line.startsWith("Please login to the ")){
                assertTrue(line.contains("Please login to the portalname (portalurl) to check status."));
                pleaseFound = true;
            }
            if (line.startsWith("Contact project")){
                assertTrue(line.contains("Contact project support at help if you have any questions regarding your job."));
                contactFound = true;
            }
        }
        assertTrue(dearFound);
        assertTrue(yourFound);
        assertTrue(pleaseFound);
        assertTrue(contactFound);

    }
    
    @Test
    public void testCreateAndRunScriptWithFakeKeplerThatSucceedsWithEmailSetAndValidJobAndErrorEmailIsSet() throws Exception{
        assumeTrue(SystemUtils.IS_OS_UNIX);
        File baseDirectory = Folder.newFolder();
        File tempDirectory = new File(baseDirectory+File.separator+"subdir");
        File outputsDir = new File(tempDirectory+File.separator+Constants.OUTPUTS_DIR_NAME);
        assertTrue(outputsDir.mkdirs());
        
        JobEmailNotificationData emailNotifyData = createJobEmailNotificationData();
        emailNotifyData.setErrorEmail("error@error.com");
        File stderrFile = new File(outputsDir.getAbsolutePath()+File.separator+"stderr");
        assertTrue(stderrFile.createNewFile());
        
        JobBinaries jb = new JobBinaries();
        jb.setKeplerScript(getAndCheckForTrueBinaryFile().getAbsolutePath());
        jb.setRegisterUpdateJar("register.jar");
        jb.setJavaCommand("/bin/echo");
        jb.setEchoCommand("/bin/echo");
        jb.setMailCommand("cat >> email.${finishedMessage};/bin/echo ");
        jb.setRetryCount(1);
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir",
                jb,emailNotifyData);
        
        Job j = new Job();
        Workflow w = new Workflow();
        w.setId(new Long(5));
        w.setName("worky");
        Parameter emailParam = new Parameter();
        emailParam.setName(Constants.CWS_NOTIFYEMAIL);
        emailParam.setValue("bob@bob.com");
        ArrayList<Parameter> params = new ArrayList<>();
        params.add(emailParam);
        j.setParameters(params);
        j.setWorkflow(w);
        j.setId(2345L);
        j.setName("myjoby");
        j.setOwner("bob");
        
        String jobCmd = scriptCreator.create(tempDirectory.getAbsolutePath(), j,new Long(2345));
        assertTrue(jobCmd != null);
        
        RunCommandLineProcessImpl rclpi = new RunCommandLineProcessImpl();
        rclpi.setWorkingDirectory(outputsDir.getAbsolutePath());

        String result = rclpi.runCommandLineProcess(jobCmd);
        
        assertTrue(result.contains("Sending start email for myjoby to user bob@bob.com and bcc to bcc"));
        assertTrue(result.contains("-s project Workflow worky - myjoby has started running -r help -b bcc bob@bob.com"));
        assertTrue(result.contains("Sending done email for myjoby to user bob@bob.com and bcc to bcc"));
        assertTrue(result.contains("-s project Workflow worky - myjoby has finished -r help -b bcc bob@bob.com"));
        
        File emailStartFile = new File(outputsDir.getAbsolutePath()+File.separator+"email.");
        
        assertTrue(emailStartFile.exists());

        File emailDoneFile = new File(outputsDir.getAbsolutePath()+File.separator+"email.finished");
        assertTrue(emailDoneFile.exists());
    }
    
    @Test
    public void testCreateAndRunScriptWithFakeFailingKeplerWithOnlyErrorEmailIsSet() throws Exception{
        assumeTrue(SystemUtils.IS_OS_UNIX);
        File baseDirectory = Folder.newFolder();
        File tempDirectory = new File(baseDirectory+File.separator+"subdir");
        File outputsDir = new File(tempDirectory+File.separator+Constants.OUTPUTS_DIR_NAME);
        assertTrue(outputsDir.mkdirs());
        
         FileWriter fw = new FileWriter(outputsDir.getAbsoluteFile()+File.separator+"WORKFLOW.FAILED.txt.tmp");
        fw.write("simple.error.message=simple\n");
        fw.write("detailed.error.message=detailed\n");
        fw.flush();
        fw.close();
        
        JobEmailNotificationData emailNotifyData = createJobEmailNotificationData();
        emailNotifyData.setErrorEmail("error@error.com");
        File stderrFile = new File(outputsDir.getAbsolutePath()+File.separator+"stderr");
        assertTrue(stderrFile.createNewFile());
        
        JobBinaries jb = new JobBinaries();
        jb.setKeplerScript("/bin/mv WORKFLOW.FAILED.txt.tmp WORKFLOW.FAILED.txt;#");
        jb.setRegisterUpdateJar("register.jar");
        jb.setJavaCommand("/bin/echo");
        jb.setEchoCommand("/bin/echo");
        jb.setMailCommand("cat >> email.${finishedMessage};/bin/echo ");
        jb.setRetryCount(1);
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir",
                jb,emailNotifyData);
        
        Job j = new Job();
        Workflow w = new Workflow();
        w.setId(new Long(5));
        w.setName("worky");
        
        j.setWorkflow(w);
        j.setId(2345L);
        j.setName("myjoby");
        j.setOwner("bob");
        
        String jobCmd = scriptCreator.create(tempDirectory.getAbsolutePath(), j,new Long(2345));
        assertTrue(jobCmd != null);
        
        RunCommandLineProcessImpl rclpi = new RunCommandLineProcessImpl();
        rclpi.setWorkingDirectory(outputsDir.getAbsolutePath());

        String result;
        try {
            result = rclpi.runCommandLineProcess(jobCmd);
            fail("Expected exception");
        }
        catch(Exception ex){
             assertTrue(ex.getMessage().startsWith("Non zero exit code (101)"));
        }
       
        
        File emailStartFile = new File(outputsDir.getAbsolutePath()+File.separator+"email.");
        assertTrue(!emailStartFile.exists());

        File emailFailedFile = new File(outputsDir.getAbsolutePath()+File.separator+"email.failed");
        List<String> lines = IOUtils.readLines(new FileReader(emailFailedFile));
        for (String line : lines){
            if (line.startsWith("Dear")){
                assertTrue(line.contains("Dear bob,"));
            }
        }
    }
    
    @Test
    public void testCreateAndRunScriptWithFakeKeplerThatSucceedsWithPreExistingWorkflowFailedFile() throws Exception{
        assumeTrue(SystemUtils.IS_OS_UNIX);
        File baseDirectory = Folder.newFolder();
        File tempDirectory = new File(baseDirectory+File.separator+"subdir");
        File outputsDir = new File(tempDirectory+File.separator+Constants.OUTPUTS_DIR_NAME);
        assertTrue(outputsDir.mkdirs());
        
        JobEmailNotificationData emailNotifyData = createJobEmailNotificationData();
        
        JobBinaries jb = new JobBinaries();
        jb.setKeplerScript(getAndCheckForTrueBinaryFile().getAbsolutePath());
        jb.setRegisterUpdateJar("register.jar");
        jb.setJavaCommand("/bin/echo");
        jb.setRetryCount(1);
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir",
                jb,emailNotifyData);
        
        Job j = new Job();
        Workflow w = new Workflow();
        w.setId(new Long(5));
        j.setWorkflow(w);
        
        FileWriter fw = new FileWriter(outputsDir.getAbsoluteFile()+File.separator+"WORKFLOW.FAILED.txt");
        fw.write("simple.error.message=simple\n");
        fw.write("detailed.error.message=detailed\n");
        fw.flush();
        fw.close();
        
        String jobCmd = scriptCreator.create(tempDirectory.getAbsolutePath(), j,new Long(2345));
                
        assertTrue(jobCmd != null);
        assertTrue(jobCmd.equals(outputsDir.getAbsolutePath()+File.separator+JobCmdScriptCreatorImpl.JOB_CMD_SH));
        File checkCmdFile = new File(jobCmd);
        assertTrue(checkCmdFile.canExecute());
       
        RunCommandLineProcessImpl rclpi = new RunCommandLineProcessImpl();
        rclpi.setWorkingDirectory(tempDirectory.getAbsolutePath());

        String result = rclpi.runCommandLineProcess(jobCmd);
       
        List<String> yos = IOUtils.readLines(new FileReader(jobCmd));
        
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
        
        JobBinaries jb = new JobBinaries();
        jb.setKeplerScript("/bin/echo -e \"simple.error.message=fake fail\\\\n"+
                "detailed.error.message=fake fail detailed"+
                "\\\\n\" > "+outputsDir.getAbsolutePath()+File.separator+
                "WORKFLOW.FAILED.txt");
        jb.setRegisterUpdateJar("register.jar");
        jb.setJavaCommand("/bin/echo");
        jb.setRetryCount(1);

        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir",
                jb,emailNotifyData);
        
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
        
        JobBinaries jb = new JobBinaries();
        jb.setKeplerScript("kill -s USR2 $$;sleep 100");
        jb.setRegisterUpdateJar("register.jar");
        jb.setJavaCommand("/bin/echo");
        jb.setRetryCount(1);
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir",
                jb,emailNotifyData);
        
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
        
        checkWorkflowFailed(outputsDir.getAbsolutePath(),
                "Job killed by scheduler",
                "Job received USR2 signal which is the signal to exit");
    }
    
    @Test
    public void testCreateAndRunScriptWithFakeKeplerThatSimulatesUSR2SignalButAlreadyHasWorkFlowFailedFile() throws Exception{
        assumeTrue(SystemUtils.IS_OS_UNIX);
        File baseDirectory = Folder.newFolder();
        File tempDirectory = new File(baseDirectory+File.separator+"subdir");
        File outputsDir = new File(tempDirectory+File.separator+Constants.OUTPUTS_DIR_NAME);
        assertTrue(outputsDir.mkdirs());
        
        JobEmailNotificationData emailNotifyData = createJobEmailNotificationData();
        
        JobBinaries jb = new JobBinaries();
        jb.setKeplerScript( "mv "+outputsDir.getAbsoluteFile()+File.separator+
                "WORKFLOW.FAILED.txt2 "
                     +outputsDir.getAbsoluteFile()+File.separator
                     +"WORKFLOW.FAILED.txt; kill -s USR2 $$;sleep 100");
        jb.setRegisterUpdateJar("register.jar");
        jb.setJavaCommand("/bin/echo");
        jb.setRetryCount(1);
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir",
                jb,emailNotifyData);
        
        Job j = new Job();
        Workflow w = new Workflow();
        w.setId(new Long(5));
        j.setWorkflow(w);
        
        FileWriter fw = new FileWriter(outputsDir.getAbsoluteFile()+File.separator+"WORKFLOW.FAILED.txt2");
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
        
        
        File failedFile = checkWorkflowFailed(outputsDir.getAbsolutePath(),
                "simple",
                "detailed");
        
        lines = IOUtils.readLines(new FileReader(failedFile));
        boolean jobFound = false;
        for (String line : lines){
            if (line.startsWith(" Job received")){
                assertTrue(line,line.equals(" Job received USR2 signal which in SGE meant it is about to be killed"));
                jobFound = true;
            }
        }
        assertTrue(jobFound);
    }
    
    @Test
    public void testCreateAndRunScriptWithKeplerThatHasExceptionInStdErrFile() throws Exception{
        assumeTrue(SystemUtils.IS_OS_UNIX);
        File baseDirectory = Folder.newFolder();
        File tempDirectory = new File(baseDirectory+File.separator+"subdir");
        File outputsDir = new File(tempDirectory+File.separator+Constants.OUTPUTS_DIR_NAME);
        assertTrue(outputsDir.mkdirs());
        
        JobEmailNotificationData emailNotifyData = createJobEmailNotificationData();
        
        
        JobBinaries jb = new JobBinaries();
        jb.setKeplerScript(getAndCheckForTrueBinaryFile().getAbsolutePath());
        jb.setRegisterUpdateJar("register.jar");
        jb.setJavaCommand("/bin/echo");
        jb.setRetryCount(1);
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir",
                jb,emailNotifyData);
        
        Job j = new Job();
        Workflow w = new Workflow();
        w.setId(new Long(5));
        j.setWorkflow(w);
        
        FileWriter fw = new FileWriter(outputsDir.getAbsoluteFile()+File.separator+"stderr");
        fw.write("Exception in thread \"main\" Java returned: 1\n" +
"	at org.kepler.build.modules.ModulesTask.execute(ModulesTask.java:106)\n" +
"	at org.kepler.build.runner.Kepler.main(Kepler.java:109)\n" +
"Caused by: Java returned: 1\n" +
"	at org.kepler.build.modules.ModulesTask.execute(ModulesTask.java:106)\n" +
"	at org.kepler.build.runner.Kepler.run(Kepler.java:266)\n" +
"	at org.kepler.build.modules.ModulesTask.execute(ModulesTask.java:102)\n" +
"	... 1 more\n" +
"Caused by: Java returned: 1\n" +
"	at org.apache.tools.ant.taskdefs.Java.execute(Java.java:111)\n" +
"	at org.kepler.build.Run.runSuite(Run.java:379)\n" +
"	at org.kepler.build.Run.run(Run.java:240)\n" +
"	at org.kepler.build.modules.ModulesTask.execute(ModulesTask.java:102)\n" +
"	... 3 more");
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
            assertTrue(ex.getMessage(),ex.getMessage().startsWith("Non zero exit code (101)"));
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

        checkWorkflowFailed(outputsDir.getAbsolutePath(),
                "Error running Kepler",
                "Found Exception in thread main Java returned: 1 in the stderr file for Kepler");
    }
    
    @Test
    public void testCreateAndRunScriptWithKeplerThatHasSQLExceptionInStdOutFile() throws Exception{
        assumeTrue(SystemUtils.IS_OS_UNIX);
        File baseDirectory = Folder.newFolder();
        File tempDirectory = new File(baseDirectory+File.separator+"subdir");
        File outputsDir = new File(tempDirectory+File.separator+Constants.OUTPUTS_DIR_NAME);
        assertTrue(outputsDir.mkdirs());
        
        JobEmailNotificationData emailNotifyData = createJobEmailNotificationData();
        
        JobBinaries jb = new JobBinaries();
        jb.setKeplerScript(getAndCheckForTrueBinaryFile().getAbsolutePath());
        jb.setRegisterUpdateJar("register.jar");
        jb.setJavaCommand("/bin/echo");
        jb.setRetryCount(1);
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir",
                jb,emailNotifyData);
        
        Job j = new Job();
        Workflow w = new Workflow();
        w.setId(new Long(5));
        j.setWorkflow(w);
        
        FileWriter fw = new FileWriter(outputsDir.getAbsoluteFile()+File.separator+"stdout");
        fw.write("     [null]\n" +
"     [null]     ... 4 more\n" +
"     [null] Caused by: java.lang.Exception: Failed to call application initializer class \"org.kepler.gui.KeplerInitializer\".  Perhaps the configuration file \"file:/sharktopus/megashark/cws/bin/Kepler-20141020.103034/common/configs/ptolemy/configs/kepler/ConfigRedirectGUIWithCache.xml\" has a problem?\n" +
"     [null] Caused by: java.sql.SQLException: Unable to start HSQL server for jdbc:hsqldb:hsql://localhost:26343/hsqldb;filepath=hsqldb:file:/home/churas/.kepler/cache-2.4/cachedata/hsqldb\n" +
"     [null]     at ptolemy.actor.gui.ConfigurationApplication.readConfiguration(ConfigurationApplication.java:716)      at org.kepler.util.sql.HSQL._getConnection(HSQL.java:683)\n" +
"     [null]\n");
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
            assertTrue(ex.getMessage(),ex.getMessage().startsWith("Non zero exit code (101)"));
        }
        
        String logFile = tempDirectory.getAbsoluteFile()+File.separator+"job...log";
        File checkLogFile = new File(logFile);
        assertTrue(logFile+" and we ran "+jobCmd,checkLogFile.exists());
        List<String> lines = IOUtils.readLines(new FileReader(logFile));
        boolean exitFound = false;
        for (String line : lines){
            if (line.startsWith("exitcode: ")){
                assertTrue(line,line.equals("exitcode: 101"));
                exitFound = true;
            }
        }
        assertTrue(exitFound);
        
        checkWorkflowFailed(outputsDir.getAbsolutePath(),
                "Error running Kepler due to internal database",
                "SQLException was found in stdout file");
    }
    
    
    @Test
    public void testCreateAndRunScriptWhereUpdateFailsWithThreeRetries() throws Exception{
        assumeTrue(SystemUtils.IS_OS_UNIX);
        File baseDirectory = Folder.newFolder();
        File tempDirectory = new File(baseDirectory+File.separator+"subdir");
        File outputsDir = new File(tempDirectory+File.separator+Constants.OUTPUTS_DIR_NAME);
        assertTrue(outputsDir.mkdirs());
        
        JobEmailNotificationData emailNotifyData = createJobEmailNotificationData();
        emailNotifyData.setErrorEmail("error@error.com > emailargs");
        JobBinaries jb = new JobBinaries();
        jb.setKeplerScript(getAndCheckForTrueBinaryFile().getAbsolutePath());
        jb.setRegisterUpdateJar("register.jar");
        jb.setMailCommand("cat > email.${finishedMessage};/bin/echo");
        jb.setJavaCommand(getAndCheckForFalseBinaryFile().getAbsolutePath());
        jb.setRetryCount(3);
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir",
                jb,emailNotifyData);
        
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
            assertTrue(ex.getMessage(),ex.getMessage().startsWith("Non zero exit code (102)"));
        }
        
        String logFile = tempDirectory.getAbsoluteFile()+File.separator+"job...log";
        File checkLogFile = new File(logFile);
        assertTrue(logFile+" and we ran "+jobCmd,checkLogFile.exists());
        List<String> lines = IOUtils.readLines(new FileReader(logFile));
        for (String line : lines){
            if (line.startsWith("exitcode: ")){
                assertTrue(line,line.equals("exitcode: 102"));
            }
        }
        
        lines = IOUtils.readLines(new FileReader(outputsDir.getAbsoluteFile()+File.separator+"WORKFLOW.FAILED.txt"));
        for (String line : lines){
            if (line.startsWith("simple.error.message")){
                assertTrue(line,line.equals("simple.error.message=Unable to update WorkspaceFile"));
            }
            if (line.startsWith("detailed.error.message")){
                assertTrue(line,line.equals("detailed.error.message=Received non zero exit code (1) when trying to update WorkspaceFile"));
            }
        }
        
        File emailFailedFile = new File(outputsDir.getAbsolutePath()+File.separator+"email.failed");
        
        assertTrue(emailFailedFile.exists());
        
        lines = IOUtils.readLines(new FileReader(emailFailedFile));
        boolean yourFound = false;
        boolean unableFound = false;
        for (String line : lines){
            if (line.startsWith("Your Unknown job: ")){
                assertTrue(line.contains(" has failed"));
                yourFound = true;
            }
            if (line.contains("WORKFLOW.FAILED.txt:")){
                assertTrue(line.contains("Unable to update WorkspaceFile"));
                unableFound = true;
            }
        }
        assertTrue(yourFound);
        assertTrue(unableFound);
       
    }
    
    
     @Test
    public void testCreateAndRunScriptWhereUpdateFailsOnceAndThenWorks() throws Exception{
        assumeTrue(SystemUtils.IS_OS_UNIX);
        File baseDirectory = Folder.newFolder();
        File tempDirectory = new File(baseDirectory+File.separator+"subdir");
        File outputsDir = new File(tempDirectory+File.separator+Constants.OUTPUTS_DIR_NAME);
        assertTrue(outputsDir.mkdirs());
        
        JobEmailNotificationData emailNotifyData = createJobEmailNotificationData();
      
        JobBinaries jb = new JobBinaries();
        jb.setKeplerScript(getAndCheckForTrueBinaryFile().getAbsolutePath());
        jb.setRegisterUpdateJar("register.jar");
        jb.setMailCommand("cat > email.${finishedMessage};/bin/echo");
        jb.setJavaCommand("if [ $cntr -eq 0 ] ; then "+
                getAndCheckForFalseBinaryFile().getAbsolutePath()+"; else "+
                getAndCheckForTrueBinaryFile().getAbsolutePath()+"; fi #");
        jb.setRetryCount(3);
        
        JobCmdScriptCreatorImpl scriptCreator = new JobCmdScriptCreatorImpl("/workflowsdir",
                jb,emailNotifyData);
        
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
        
        String result = rclpi.runCommandLineProcess(jobCmd);
        assertTrue(result.contains("Update of workspace path try 1 of 3 failed.  Sleeping 0 seconds and retrying"));
        
        String logFile = tempDirectory.getAbsoluteFile()+File.separator+"job...log";
        File checkLogFile = new File(logFile);
        assertTrue(logFile+" and we ran "+jobCmd,checkLogFile.exists());
        List<String> lines = IOUtils.readLines(new FileReader(logFile));
        boolean exitFound = false;
        for (String line : lines){
            if (line.startsWith("exitcode: ")){
                assertTrue(line,line.equals("exitcode: 0"));
                exitFound = true;
            }
        }
        assertTrue(exitFound);
    }
}