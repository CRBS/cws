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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;
import static org.mockito.AdditionalAnswers.*;

import edu.ucsd.crbs.cws.dao.WorkspaceFileDAO;
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestOutputWorkspaceFileUtilImpl {

    public TestOutputWorkspaceFileUtilImpl() {
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
    public void testcreateAndRegisterJobOutputAsWorkspaceFileWithNullJob() throws Exception {
        OutputWorkspaceFileUtilImpl workspaceUtil = new OutputWorkspaceFileUtilImpl(null);
        
        try {
            workspaceUtil.createAndRegisterJobOutputAsWorkspaceFile(null, "foo");
            fail("Expected Exception");
        }
        catch(IllegalArgumentException ex){
            assertTrue(ex.getMessage().startsWith("Job cannot be null"));
        }
    }

   @Test 
   public void testcreateAndRegisterJobOutputAsWorkspaceFileWithNullJobName() throws Exception {
        OutputWorkspaceFileUtilImpl workspaceUtil = new OutputWorkspaceFileUtilImpl(null);

        try {
            workspaceUtil.createAndRegisterJobOutputAsWorkspaceFile(new Job(),null);
            fail("Expected exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("Job name cannot be null"));
        }
   }
   
   @Test 
   public void testcreateAndRegisterJobOutputAsWorkspaceFileWithNullWorkflow() throws Exception {
        OutputWorkspaceFileUtilImpl workspaceUtil = new OutputWorkspaceFileUtilImpl(null);

        try {
            Job job = new Job();
            job.setName("name");
            workspaceUtil.createAndRegisterJobOutputAsWorkspaceFile(job,null);
            fail("Expected exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("Workflow for job cannot be null"));
        }
   }
    
   @Test 
   public void testcreateAndRegisterJobOutputAsWorkspaceFileWithNullWorkflowName() throws Exception {
        OutputWorkspaceFileUtilImpl workspaceUtil = new OutputWorkspaceFileUtilImpl(null);

        try {
            Job job = new Job();
            job.setName("name");
            Workflow w = new Workflow();
            job.setWorkflow(w);
            workspaceUtil.createAndRegisterJobOutputAsWorkspaceFile(job,null);
            fail("Expected exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("Workflow name for job cannot be null"));
        }
   }

       
   @Test 
   public void testcreateAndRegisterJobOutputAsWorkspaceFileWithNullOwner() throws Exception {
        OutputWorkspaceFileUtilImpl workspaceUtil = new OutputWorkspaceFileUtilImpl(null);

        try {
            Job job = new Job();
            job.setName("name");
            Workflow w = new Workflow();
            w.setName("workflow");
            job.setWorkflow(w);
            workspaceUtil.createAndRegisterJobOutputAsWorkspaceFile(job,null);
            fail("Expected exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("Owner of job cannot be null"));
        }
   }

    
   @Test 
   public void testcreateAndRegisterJobOutputAsWorkspaceFileWithSucessfulInsert() throws Exception {
        WorkspaceFileDAO workspaceDAO = mock(WorkspaceFileDAO.class);
        WorkspaceFile wsp = new WorkspaceFile();
        
        when(workspaceDAO.insert(wsp,false)).thenReturn(wsp);
        when(workspaceDAO.insert(wsp,false)).thenReturn(wsp);
        OutputWorkspaceFileUtilImpl workspaceUtil = new OutputWorkspaceFileUtilImpl(workspaceDAO);

        Job job = new Job();
        job.setName("name");
        job.setOwner("bob");
        job.setId(new Long(1));
        Workflow w = new Workflow();
        w.setVersion(3);
        w.setName("workflow");
        job.setWorkflow(w);
        
        wsp = workspaceUtil.createAndRegisterJobOutputAsWorkspaceFile(job,null);
        assertTrue(wsp.getName().equals("name [Job Output]")); 
        assertTrue(wsp.getOwner().equals("bob"));
        assertTrue(wsp.getType().equals("workflow"));
        assertTrue(wsp.getSourceJobId() == 1);
        assertTrue(wsp.getDescription().equals("Output of Workflow Job (1) [ Workflow Ver 3 ]"));
        assertTrue(wsp.getDir() == true);
        
        wsp = workspaceUtil.createAndRegisterJobOutputAsWorkspaceFile(job,"blah");
        assertTrue(wsp.getName().equals("name [Job Output]")); 
        assertTrue(wsp.getOwner().equals("bob"));
        assertTrue(wsp.getType().equals("workflow"));
        assertTrue(wsp.getSourceJobId() == 1);
        assertTrue(wsp.getDescription().equals("Output of Workflow Job (1) [ Workflow Ver 3 ]"));
        assertTrue(wsp.getPath().equals("blah"));
        assertTrue(wsp.getDir() == true);
        
        
   
   }
}
