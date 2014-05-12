package edu.ucsd.crbs.cws.workflow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestTask {

    public TestTask() {
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
    public void testBasicGettersAndSetters(){
        Task t = new Task();
        assertTrue(t.getId() == null);
        assertTrue(t.getWorkflow() == null);
        assertTrue(t.getName() == null);
        assertTrue(t.getOwner() == null);
        assertTrue(t.getStatus() == null);
        assertTrue(t.getEstimatedCpuInSeconds() == 0);
        assertTrue(t.getEstimatedRunTime() == 0);
        assertTrue(t.getEstimatedDiskInBytes() == 0);
        assertTrue(t.getCreateDate() == null);
        assertTrue(t.getSubmitDate() == null);
        assertTrue(t.getStartDate() == null);
        assertTrue(t.getFinishDate() == null);
        assertTrue(t.getParameters() == null);
        assertTrue(t.getHasJobBeenSubmittedToScheduler() == false);
        assertTrue(t.getDownloadURL() == null);
        
        t.setId(new Long(5));
        t.setName("name");
        t.setOwner("owner");
        t.setStatus("status");
        t.setEstimatedCpuInSeconds(1);
        t.setEstimatedRunTime(2);
        t.setEstimatedDiskInBytes(3);
        Date createDate = new Date();
        t.setCreateDate(createDate);
        Date submitDate = new Date();
        t.setSubmitDate(submitDate);
        Date startDate = new Date();
        t.setStartDate(startDate);
        Date finishDate = new Date();
        t.setFinishDate(finishDate);
        List<Parameter> params = new ArrayList<Parameter>();
        t.setParameters(params);
        t.setHasJobBeenSubmittedToScheduler(true);
        t.setDownloadURL("download");
        
        assertTrue(t.getId().longValue() == 5);
        assertTrue(t.getName().equals("name"));
        assertTrue(t.getOwner().equals("owner"));
        assertTrue(t.getStatus().equals("status"));
        assertTrue(t.getEstimatedCpuInSeconds() == 1);
        assertTrue(t.getEstimatedRunTime() == 2);
        assertTrue(t.getEstimatedDiskInBytes() == 3);
        assertTrue(t.getCreateDate().equals(createDate));
        assertTrue(t.getSubmitDate().equals(submitDate));
        assertTrue(t.getStartDate().equals(startDate));
        assertTrue(t.getFinishDate().equals(finishDate));
        assertTrue(t.getParameters().size() == 0);
        assertTrue(t.getHasJobBeenSubmittedToScheduler() == true);
        assertTrue(t.getDownloadURL().equals("download"));
    }

    /** @TODO need to add tests for get/setWorkflow() methods **/
}