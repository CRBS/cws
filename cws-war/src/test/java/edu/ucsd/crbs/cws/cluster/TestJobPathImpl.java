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
import edu.ucsd.crbs.cws.workflow.Job;
import java.io.File;
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
public class TestJobPathImpl {

    public TestJobPathImpl() {
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
    public void testGetJobBaseDirectoryBaseDirNotSet() throws Exception {
        JobPathImpl jpi = new JobPathImpl(null);
        try {
            jpi.getJobBaseDirectory(new Job());
            fail("Expected Exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("Base directory cannot be null"));
        }
    }
    
    @Test
    public void testGetJobBaseDirectoryJobNull() throws Exception {
        JobPathImpl jpi = new JobPathImpl("base");
        try {
            jpi.getJobBaseDirectory(null);
            fail("Expected Exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("Job cannot be null"));
        }
    }
    
    @Test
    public void testGetJobBaseDirectoryJobOwnerNull() throws Exception {
        JobPathImpl jpi = new JobPathImpl("base");
        try {
            Job j = new Job();
            jpi.getJobBaseDirectory(j);
            fail("Expected Exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("Job owner cannot be null"));
        }
    }

    @Test
    public void testGetJobBaseDirectoryJobIdNull() throws Exception {
        JobPathImpl jpi = new JobPathImpl("base");
        try {
            Job j = new Job();
            j.setOwner("owner");
            jpi.getJobBaseDirectory(j);
            
            fail("Expected Exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("Job id cannot be null"));
        }
    }

    @Test
    public void testGetJobBaseDirectoryValidJob() throws Exception {
        JobPathImpl jpi = new JobPathImpl("base");
        Job j = new Job();
        j.setOwner("owner");
        j.setId(new Long(10));
        assertTrue(jpi.getJobBaseDirectory(j).equals("base" + File.separator
                + "owner" + File.separator + "10"));
    }

    
    
    
    
    //////////////////////////////////////////////////////////////////////////
    @Test
    public void testGetJobOutputDirectoryBaseDirNotSet() throws Exception {
        JobPathImpl jpi = new JobPathImpl(null);
        try {
            jpi.getJobOutputDirectory(new Job());
            fail("Expected Exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("Base directory cannot be null"));
        }
    }
    
    @Test
    public void testGetJobOutputDirectoryJobNull() throws Exception {
        JobPathImpl jpi = new JobPathImpl("base");
        try {
            jpi.getJobOutputDirectory(null);
            fail("Expected Exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("Job cannot be null"));
        }
    }
    
    @Test
    public void testGetJobOutputDirectoryJobOwnerNull() throws Exception {
        JobPathImpl jpi = new JobPathImpl("base");
        try {
            Job j = new Job();
            jpi.getJobOutputDirectory(j);
            fail("Expected Exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("Job owner cannot be null"));
        }
    }

    @Test
    public void testGetJobOutputDirectoryJobIdNull() throws Exception {
        JobPathImpl jpi = new JobPathImpl("base");
        try {
            Job j = new Job();
            j.setOwner("owner");
            jpi.getJobOutputDirectory(j);
            
            fail("Expected Exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("Job id cannot be null"));
        }
    }

    @Test
    public void testGetJobOutputDirectoryValidJob() throws Exception {
        JobPathImpl jpi = new JobPathImpl("base");
        Job j = new Job();
        j.setOwner("owner");
        j.setId(new Long(10));
        assertTrue(jpi.getJobOutputDirectory(j).equals("base" + File.separator
                + "owner" + File.separator + "10"+File.separator+Constants.OUTPUTS_DIR_NAME));
    }
    
    
    
    
    
    

}