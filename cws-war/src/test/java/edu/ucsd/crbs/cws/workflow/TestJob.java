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
    
}