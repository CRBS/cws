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

import edu.ucsd.crbs.cws.cluster.JobPath;
import edu.ucsd.crbs.cws.cluster.JobPathImpl;
import edu.ucsd.crbs.cws.cluster.submission.JobDirectoryCreatorImpl;
import edu.ucsd.crbs.cws.workflow.Job;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;


/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestJobDirectoryCreatorImpl {

    
    @Rule
    public TemporaryFolder _folder = new TemporaryFolder();
    
    public TestJobDirectoryCreatorImpl() {
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
    public void testCreateWithNullJobPath() throws Exception {
        JobDirectoryCreatorImpl jobDirCreator = new JobDirectoryCreatorImpl(null);
        try {
            jobDirCreator.create(new Job());
            fail("Expected NullPointerException");
        }
        catch(NullPointerException npe){
            assertTrue(npe.getMessage(),npe.getMessage().contains("JobPath cannot be null"));
        }
    }
    
    @Test
    public void testCreateWithNullJob() throws Exception {
        JobPathImpl jobPath = new JobPathImpl("/foo");
        JobDirectoryCreatorImpl jobDirCreator = new JobDirectoryCreatorImpl(jobPath);
        try {
            jobDirCreator.create(null);
            fail("Expected NullPointerException");
        }
        catch(NullPointerException npe){
            
            assertTrue(npe.getMessage(),npe.getMessage().contains("Job cannot be null"));
        }
    }

    @Test
    public void testCreateWhereDirectoryAlreadyExists() throws Exception {
        File tempDir = _folder.newFolder();
        File subDir = new File(tempDir+File.separator+"sub");
        
        assertTrue("Trying to make subdirectory for test",subDir.mkdirs());
        
        Job j = new Job();
        
        JobPath jobPath = mock(JobPath.class);
        when(jobPath.getJobOutputDirectory(j)).thenReturn(subDir.getAbsolutePath());
        
        JobDirectoryCreatorImpl jobDirCreator = new JobDirectoryCreatorImpl(jobPath);
        
        assertTrue(jobDirCreator.create(j).equals(tempDir.getAbsolutePath()));
        
    }
    
    @Test
    public void testCreateWhereUnableToMakeDirectory() throws Exception {
        File tempDir = _folder.newFolder();
        File subDir = new File(tempDir+File.separator+"sub");
        
        assertTrue("Trying to make a file for test",subDir.createNewFile());
        
        Job j = new Job();
        
        JobPath jobPath = mock(JobPath.class);
        when(jobPath.getJobOutputDirectory(j)).thenReturn(subDir.getAbsolutePath());
        
        JobDirectoryCreatorImpl jobDirCreator = new JobDirectoryCreatorImpl(jobPath);
        try {
            jobDirCreator.create(j);
        }
        catch(Exception ex){
            assertTrue(ex.getMessage(),ex.getMessage().contains("Unable to create directory"));
        }
    }
    
    @Test
    public void testCreateWhereDirectoryCreationIsSuccessful() throws Exception {
        File tempDir = _folder.newFolder();
        File subDir = new File(tempDir+File.separator+"sub");
        
        Job j = new Job();
        
        JobPath jobPath = mock(JobPath.class);
        when(jobPath.getJobOutputDirectory(j)).thenReturn(subDir.getAbsolutePath());
        
        JobDirectoryCreatorImpl jobDirCreator = new JobDirectoryCreatorImpl(jobPath);
        assertTrue(subDir.isDirectory() == false);
        
        assertTrue(jobDirCreator.create(j).equals(tempDir.getAbsolutePath()));
        assertTrue(subDir.isDirectory() == true);
        
    }
    
}