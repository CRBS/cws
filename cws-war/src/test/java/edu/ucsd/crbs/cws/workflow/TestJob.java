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
public class TestJob {

    public TestJob() {
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
        Job t = new Job();
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
        assertTrue(t.getError() == null);
        assertTrue(t.getParametersWithErrors() == null);
        assertTrue(t.getDetailedError() == null);
        
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
        List<Parameter> params = new ArrayList<>();
        t.setParameters(params);
        t.setHasJobBeenSubmittedToScheduler(true);
        t.setDownloadURL("download");
        t.setError("error");
        t.setParametersWithErrors(new ArrayList<ParameterWithError>());
        t.setDetailedError("detailed");
        
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
        assertTrue(t.getParameters().isEmpty());
        assertTrue(t.getHasJobBeenSubmittedToScheduler() == true);
        assertTrue(t.getDownloadURL().equals("download"));
        assertTrue(t.getError().equals("error"));
        assertTrue(t.getParametersWithErrors().isEmpty() == true);
        assertTrue(t.getDetailedError().equals("detailed"));
                
    }

    @Test
    public void testAddParameterWithError(){
        Job t = new Job();
        
        t.addParameterWithError(null);
        
        assertTrue(t.getParametersWithErrors() == null);
        
        t.addParameterWithError(new ParameterWithError("name","value","error"));
        assertTrue(t.getParametersWithErrors().size() == 1);
        t.addParameterWithError(new ParameterWithError("name","value","error"));
        assertTrue(t.getParametersWithErrors().size() == 2);
        
        assertTrue(t.getParametersWithErrors().get(0).getName().equals("name"));
        assertTrue(t.getParametersWithErrors().get(1).getName().equals("name"));
    }
    
    @Test
    public void testGetSummaryOfErrors(){
        Job t = new Job();
        assertTrue(t.getSummaryOfErrors().equals(""));
        t.addParameterWithError(new ParameterWithError("someparam","3","bad"));
        
        assertTrue(t.getSummaryOfErrors().equals("Parameter: name=someparam,value=3,error=bad\n"));
        t.addParameterWithError(new ParameterWithError("m","v","x"));
        assertTrue(t.getSummaryOfErrors().equals("Parameter: name=someparam,value=3,error=bad\nParameter: name=m,value=v,error=x\n"));
        
        t.setError("some error");
        assertTrue(t.getSummaryOfErrors().equals("JobError: some error\nParameter: name=someparam,value=3,error=bad\nParameter: name=m,value=v,error=x\n"));
    }
    
    @Test
    public void testUpdateWithChangesWithNullValues(){
        Job job = new Job();
        
        assertFalse(job.updateWithChanges(null, null,null, null, null,null));
        assertFalse(job.updateWithChanges(null, Boolean.TRUE,Boolean.TRUE, 1L,1L,1L));
    }
    
    @Test
    public void testUpdateWithChangesAgainstSelf(){
        Job job = new Job();

        assertFalse(job.updateWithChanges(job, null,null, null, null,null));
        
        
        job.setCreateDate(new Date());
        job.setDeleted(true);
        job.setHasJobBeenSubmittedToScheduler(true);
        job.setDetailedError("detailed");
        job.setDownloadURL("url");
        job.setError("error");
        job.setFinishDate(new Date());
        job.setName("name");
        job.setOwner("owner");
        job.setSchedulerJobId("1");
        job.setStartDate(new Date());
        job.setStatus("status");
        job.setSubmitDate(new Date());
        job.setEstimatedCpuInSeconds(1L);
        job.setEstimatedDiskInBytes(1L);
        job.setEstimatedRunTime(1L);

        assertFalse(job.updateWithChanges(job, null,null, null, null,null));

        assertFalse(job.updateWithChanges(job, Boolean.TRUE,null, null, null,null));
        assertTrue(job.updateWithChanges(job, Boolean.FALSE,null, null, null,null));
        assertFalse(job.updateWithChanges(job, Boolean.FALSE,null, null, null,null));
        
        assertFalse(job.updateWithChanges(job,null, Boolean.TRUE, null, null,null));
        assertTrue(job.updateWithChanges(job,null, Boolean.FALSE, null, null,null));
        assertFalse(job.updateWithChanges(job,null, Boolean.FALSE, null, null,null));

        assertFalse(job.updateWithChanges(job,null, null, 1L, null,null));
        assertTrue(job.updateWithChanges(job,null, null, 2L, null,null));
        assertFalse(job.updateWithChanges(job,null, null, 2L, null,null));


        assertFalse(job.updateWithChanges(job,null, null,null, 1L,null));
        assertTrue(job.updateWithChanges(job,null, null,null, 2L,null));
        assertFalse(job.updateWithChanges(job,null, null,null, 2L,null));

        
        assertFalse(job.updateWithChanges(job,null, null,null,null, 1L));
        assertTrue(job.updateWithChanges(job,null, null, null,null,2L));
        assertFalse(job.updateWithChanges(job,null, null,null,null, 2L));
        
        assertFalse(job.updateWithChanges(job, null,null, null, null,null));

    }
    
    @Test
    public void testupdateWithChangesToName(){
        Job baseJob = new Job();
        Job newJob = new Job();
        
          //both null
        assertFalse(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        
        //base is null new has value
        newJob.setName("key");
        assertTrue(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getName().equals("key"));
        
        //new is null
        newJob.setName(null);
        assertFalse(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getName().equals("key"));
        
        //new is same
        newJob.setName(baseJob.getName());
        assertFalse(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getName().equals("key"));
        
        //new is different
        newJob.setName("key2");
        assertTrue(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getName().equals("key2"));
    }
    
    @Test
    public void testupdateWithChangesToStatus(){
        Job baseJob = new Job();
        Job newJob = new Job();
        
          //both null
        assertFalse(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        
        //base is null new has value
        newJob.setStatus("key");
        assertTrue(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getStatus().equals("key"));
        
        //new is null
        newJob.setStatus(null);
        assertFalse(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getStatus().equals("key"));
        
        //new is same
        newJob.setStatus(baseJob.getStatus());
        assertFalse(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getStatus().equals("key"));
        
        //new is different
        newJob.setStatus("key2");
        assertTrue(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getStatus().equals("key2"));
    }
    
    @Test
    public void testupdateWithChangesToOwner(){
        Job baseJob = new Job();
        Job newJob = new Job();
        
          //both null
        assertFalse(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        
        //base is null new has value
        newJob.setOwner("key");
        assertTrue(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getOwner().equals("key"));
        
        //new is null
        newJob.setOwner(null);
        assertFalse(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getOwner().equals("key"));
        
        //new is same
        newJob.setOwner(baseJob.getOwner());
        assertFalse(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getOwner().equals("key"));
        
        //new is different
        newJob.setOwner("key2");
        assertTrue(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getOwner().equals("key2"));
    }
    
    @Test
    public void testupdateWithChangesToSchedulerJobId(){
        Job baseJob = new Job();
        Job newJob = new Job();
        
          //both null
        assertFalse(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        
        //base is null new has value
        newJob.setSchedulerJobId("key");
        assertTrue(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getSchedulerJobId().equals("key"));
        
        //new is null
        newJob.setSchedulerJobId(null);
        assertFalse(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getSchedulerJobId().equals("key"));
        
        //new is same
        newJob.setSchedulerJobId(baseJob.getSchedulerJobId());
        assertFalse(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getSchedulerJobId().equals("key"));
        
        //new is different
        newJob.setSchedulerJobId("key2");
        assertTrue(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getSchedulerJobId().equals("key2"));
    }
    
    @Test
    public void testupdateWithChangesToDownloadURL(){
        Job baseJob = new Job();
        Job newJob = new Job();
        
          //both null
        assertFalse(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        
        //base is null new has value
        newJob.setDownloadURL("key");
        assertTrue(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getDownloadURL().equals("key"));
        
        //new is null
        newJob.setDownloadURL(null);
        assertFalse(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getDownloadURL().equals("key"));
        
        //new is same
        newJob.setDownloadURL(baseJob.getDownloadURL());
        assertFalse(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getDownloadURL().equals("key"));
        
        //new is different
        newJob.setDownloadURL("key2");
        assertTrue(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getDownloadURL().equals("key2"));
    }
    
    @Test
    public void testupdateWithChangesToError(){
        Job baseJob = new Job();
        Job newJob = new Job();
        
          //both null
        assertFalse(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        
        //base is null new has value
        newJob.setError("key");
        assertTrue(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getError().equals("key"));
        
        //new is null
        newJob.setError(null);
        assertFalse(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getError().equals("key"));
        
        //new is same
        newJob.setError(baseJob.getError());
        assertFalse(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getError().equals("key"));
        
        //new is different
        newJob.setError("key2");
        assertTrue(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getError().equals("key2"));
    }
    
    @Test
    public void testupdateWithChangesToDetailedError(){
        Job baseJob = new Job();
        Job newJob = new Job();
        
          //both null
        assertFalse(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        
        //base is null new has value
        newJob.setDetailedError("key");
        assertTrue(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getDetailedError().equals("key"));
        
        //new is null
        newJob.setDetailedError(null);
        assertFalse(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getDetailedError().equals("key"));
        
        //new is same
        newJob.setDetailedError(baseJob.getDetailedError());
        assertFalse(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getDetailedError().equals("key"));
        
        //new is different
        newJob.setDetailedError("key2");
        assertTrue(baseJob.updateWithChanges(newJob,null,null,null,null,null));
        assertTrue(baseJob.getDetailedError().equals("key2"));
    }
    
    @Test
    public void testUpdateWithChangesToCreateDate(){
        Job baseJob = new Job();
        Job newJob = new Job();
        
        //both null
        assertFalse(baseJob.updateWithChanges(newJob, null,null,null,null,null));
        
        Date newDate = new Date();
        //base is null new has value
        newJob.setCreateDate(newDate);
        assertTrue(baseJob.updateWithChanges(newJob, null,null,null,null,null));
        assertTrue(baseJob.getCreateDate().equals(newDate));
        
        //new is null
        newJob.setCreateDate(null);
        assertFalse(baseJob.updateWithChanges(newJob, null,null,null,null,null));
        assertTrue(baseJob.getCreateDate().equals(newDate));
        
        //new is same
        newJob.setCreateDate(newDate);
        assertFalse(baseJob.updateWithChanges(newJob, null,null,null,null,null));
        assertTrue(baseJob.getCreateDate().equals(newDate));
        
        //new is different
        Date anotherDate = new Date(newDate.getTime()+100L);
        newJob.setCreateDate(anotherDate);
        assertTrue(baseJob.updateWithChanges(newJob, null,null,null,null,null));
        assertTrue(baseJob.getCreateDate().equals(anotherDate));
    }
    
    @Test
    public void testUpdateWithChangesToStartDate(){
        Job baseJob = new Job();
        Job newJob = new Job();
        
        //both null
        assertFalse(baseJob.updateWithChanges(newJob, null,null,null,null,null));
        
        Date newDate = new Date();
        //base is null new has value
        newJob.setStartDate(newDate);
        assertTrue(baseJob.updateWithChanges(newJob, null,null,null,null,null));
        assertTrue(baseJob.getStartDate().equals(newDate));
        
        //new is null
        newJob.setStartDate(null);
        assertFalse(baseJob.updateWithChanges(newJob, null,null,null,null,null));
        assertTrue(baseJob.getStartDate().equals(newDate));
        
        //new is same
        newJob.setStartDate(newDate);
        assertFalse(baseJob.updateWithChanges(newJob, null,null,null,null,null));
        assertTrue(baseJob.getStartDate().equals(newDate));
        
        //new is different
        Date anotherDate = new Date(newDate.getTime()+100L);
        newJob.setStartDate(anotherDate);
        assertTrue(baseJob.updateWithChanges(newJob, null,null,null,null,null));
        assertTrue(baseJob.getStartDate().equals(anotherDate));
    }
    
    @Test
    public void testUpdateWithChangesToSubmitDate(){
        Job baseJob = new Job();
        Job newJob = new Job();
        
        //both null
        assertFalse(baseJob.updateWithChanges(newJob, null,null,null,null,null));
        
        Date newDate = new Date();
        //base is null new has value
        newJob.setSubmitDate(newDate);
        assertTrue(baseJob.updateWithChanges(newJob, null,null,null,null,null));
        assertTrue(baseJob.getSubmitDate().equals(newDate));
        
        //new is null
        newJob.setSubmitDate(null);
        assertFalse(baseJob.updateWithChanges(newJob, null,null,null,null,null));
        assertTrue(baseJob.getSubmitDate().equals(newDate));
        
        //new is same
        newJob.setSubmitDate(newDate);
        assertFalse(baseJob.updateWithChanges(newJob, null,null,null,null,null));
        assertTrue(baseJob.getSubmitDate().equals(newDate));
        
        //new is different
        Date anotherDate = new Date(newDate.getTime()+100L);
        newJob.setSubmitDate(anotherDate);
        assertTrue(baseJob.updateWithChanges(newJob, null,null,null,null,null));
        assertTrue(baseJob.getSubmitDate().equals(anotherDate));
    }
    
    
   @Test
    public void testUpdateWithChangesToFinishDate(){
        Job baseJob = new Job();
        Job newJob = new Job();
        
        //both null
        assertFalse(baseJob.updateWithChanges(newJob, null,null,null,null,null));
        
        Date newDate = new Date();
        //base is null new has value
        newJob.setFinishDate(newDate);
        assertTrue(baseJob.updateWithChanges(newJob, null,null,null,null,null));
        assertTrue(baseJob.getFinishDate().equals(newDate));
        
        //new is null
        newJob.setFinishDate(null);
        assertFalse(baseJob.updateWithChanges(newJob, null,null,null,null,null));
        assertTrue(baseJob.getFinishDate().equals(newDate));
        
        //new is same
        newJob.setFinishDate(newDate);
        assertFalse(baseJob.updateWithChanges(newJob, null,null,null,null,null));
        assertTrue(baseJob.getFinishDate().equals(newDate));
        
        //new is different
        Date anotherDate = new Date(newDate.getTime()+100L);
        newJob.setFinishDate(anotherDate);
        assertTrue(baseJob.updateWithChanges(newJob, null,null,null,null,null));
        assertTrue(baseJob.getFinishDate().equals(anotherDate));
    }
    
    
}