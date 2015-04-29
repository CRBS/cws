/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2015 The Regents of the University of California All Rights Reserved
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

package edu.ucsd.crbs.cws.dao.objectify;

import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import static edu.ucsd.crbs.cws.dao.objectify.OfyService.ofy;
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.report.DeleteReport;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;



/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestJobObjectifyDAOImpl {

     private final LocalServiceTestHelper _helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(),
        new LocalBlobstoreServiceTestConfig());
    
    public TestJobObjectifyDAOImpl() {
    }

    @BeforeClass
    public static void setUpClass() {
        Logger.getLogger(JobObjectifyDAOImpl.class.getName()).setLevel(Level.OFF);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        _helper.setUp();
        ofy().clear();
    }

    @After
    public void tearDown() {
        _helper.tearDown();
    }

 
    @Test
    public void testDeleteWhereJobNotFound() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        
        DeleteReport dwr = jobDAO.delete(1L, null);
        assertTrue(dwr != null);
        assertTrue(dwr.isSuccessful() == false);
        assertTrue(dwr.getReason().equals("Job not found"));
    }
    
    @Test
    public void testResaveNonExistingJob() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        
        try {
            jobDAO.resave(1L);
            fail("Expected exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().equals("There was an error resaving job with id: 1"));
        }
    }
    
    @Test
    public void testResaveOnValidJob() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        
        Job j = new Job();
        j.setName("bob");
        Job resJob = jobDAO.insert(j, true);
        Job resavedJob = jobDAO.resave(resJob.getId());
        assertTrue(resavedJob.getName().equals("bob"));
        assertTrue(resJob.getId() == resavedJob.getId().longValue());
    }

    @Test
    public void testGetJobByIdWithNullId() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        try {
            jobDAO.getJobById(null);
            fail("Expected Exception");
        }
        catch(NullPointerException ex){
            assertTrue(ex.getMessage(),
                    ex.getMessage().equals("jobId cannot be null"));
        }
    }
    
    @Test
    public void testGetJobByIdWithNonnumericId() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        try {
            jobDAO.getJobById("foo");
            fail("Expected Exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage(),
                    ex.getMessage().equals("jobId must be numeric, error "
                            + "received when parsing : For input string: "
                            + "\"foo\""));
        }
    }
    
    @Test
    public void testGetJobByIdAndUserWithNullValues() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        
        try {
            jobDAO.getJobByIdAndUser(null, null);
            fail("expected exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage(),
                    ex.getMessage().equals("User cannot be null"));
        }
        try {
            jobDAO.getJobByIdAndUser(null,"bob");
            fail("Expected Exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage(),
                    ex.getMessage().equals("jobId cannot be null"));
        }
    }
    
    
    @Test
    public void testGetJobByIdAndUserNoJobFound() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        assertNull(jobDAO.getJobByIdAndUser("1","bob"));
    }
    
    @Test
    public void testGetJobByIdAndUserButUserDoesNotMatchAsItsNullOrDifferent() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        Job resJob = jobDAO.insert(j, true);
        assertNull(jobDAO.getJobByIdAndUser(resJob.getId().toString(),"bob"));
        
        resJob.setOwner("phil");
        jobDAO.update(resJob);
        assertNull(jobDAO.getJobByIdAndUser(resJob.getId().toString(),"bob"));
    }
    
    @Test
    public void testGetJobByIdAndUserWithMatch() throws Exception {
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(null);
        Job j = new Job();
        j.setOwner("bob");
        Job resJob = jobDAO.insert(j, true);
        Job gotIt = jobDAO.getJobByIdAndUser(resJob.getId().toString(),"bob");
        assertTrue(gotIt != null);
        assertTrue(gotIt.getId() == resJob.getId().longValue());
        assertTrue(gotIt.getOwner().equals("bob"));
    }

}