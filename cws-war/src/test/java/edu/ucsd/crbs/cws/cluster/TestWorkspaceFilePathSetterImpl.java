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

import edu.ucsd.crbs.cws.dao.WorkspaceFileDAO;
import edu.ucsd.crbs.cws.workflow.Parameter;
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestWorkspaceFilePathSetterImpl {

    public TestWorkspaceFilePathSetterImpl() {
    }

    @BeforeClass
    public static void setUpClass() {
        Logger.getLogger(WorkspaceFilePathSetterImpl.class.getName()).setLevel(Level.OFF);
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

    //test null job
    @Test
    public void testSetPathsWithNullJob() throws Exception {
        WorkspaceFilePathSetterImpl setter = new WorkspaceFilePathSetterImpl(null);
        WorkspaceFilePathSetterStatus status = setter.setPaths(null);
        assertTrue(status.getReason().equals("Job passed in is null"));
        assertTrue(status.getSuggestedJobStatus().equals(Job.ERROR_STATUS));
        assertFalse(status.isSuccessful());
    }
    
    //test null job id
    @Test
    public void testSetPathsWithNullJobId() throws Exception {
        WorkspaceFilePathSetterImpl setter = new WorkspaceFilePathSetterImpl(null);
        WorkspaceFilePathSetterStatus status = setter.setPaths(new Job());
        assertTrue(status.getReason().equals("Job id is null"));
        assertTrue(status.getSuggestedJobStatus().equals(Job.ERROR_STATUS));
        assertFalse(status.isSuccessful());
    }
    
    //test where no parameters is null
    @Test
    public void testSetPathsWithNullParameters() throws Exception {
        WorkspaceFilePathSetterImpl setter = new WorkspaceFilePathSetterImpl(null);
        Job t = new Job();
        t.setId(new Long(1));
        WorkspaceFilePathSetterStatus status = setter.setPaths(t);
        assertTrue(status.isSuccessful());
        assertTrue(status.getReason().equals("Job 1 has no parameters"));
        assertTrue(status.getSuggestedJobStatus().equals(Job.IN_QUEUE_STATUS));
    }
    
    //test where no parameters
    @Test
    public void testSetPathsWithNoParameters() throws Exception {
        WorkspaceFilePathSetterImpl setter = new WorkspaceFilePathSetterImpl(null);
        Job t = new Job();
        t.setId(new Long(1));
        t.setParameters(new ArrayList<Parameter>());
        WorkspaceFilePathSetterStatus status = setter.setPaths(t);
        assertTrue(status.isSuccessful());
        assertTrue(status.getReason().equals("Job 1 has no parameters"));
        assertTrue(status.getSuggestedJobStatus().equals(Job.IN_QUEUE_STATUS));
        
    }
    
    //test where workspaceFileDAO is null
    @Test
    public void testSetPathsWhereWorkspaceFileDAOIsNull() throws Exception {
        WorkspaceFilePathSetterImpl setter = new WorkspaceFilePathSetterImpl(null);
                
        Job t = new Job();
        t.setId(new Long(1));
        ArrayList<Parameter> params = new ArrayList<>();
        Parameter p = new Parameter();
        p.setName("foo");
        p.setValue("123");
        p.setIsWorkspaceId(true);
        params.add(p);
        t.setParameters(params);
        
        try {
            setter.setPaths(t);
            fail("Expected exception");
        }
        catch(NullPointerException npe){
            assertTrue(npe.getMessage().startsWith("WorkspaceFileDAO must be set"));
        }
    }
    
    //test where parameter value is null for a workspacefile parameter
    @Test
    public void testSetPathsWhereParameterValueIsNull() throws Exception {
        WorkspaceFilePathSetterImpl setter = new WorkspaceFilePathSetterImpl(null);
                
        Job t = new Job();
        t.setId(new Long(1));
        ArrayList<Parameter> params = new ArrayList<>();
        Parameter p = new Parameter();
        p.setName("foo");
        p.setValue(null);
        p.setIsWorkspaceId(true);
        params.add(p);
        t.setParameters(params);
        WorkspaceFilePathSetterStatus status = setter.setPaths(t);
        assertTrue(status.isSuccessful());
        assertTrue(status.getReason().equals("Job 1 has no WorkspaceFile parameters that require paths to be set"));
        assertTrue(status.getSuggestedJobStatus().equals(Job.IN_QUEUE_STATUS));
    }
    
    //test where getting map of workspace parameters throws exception
    @Test
    public void testSetPathsWhereDAOKicksOutAnException() throws Exception {
        
        WorkspaceFileDAO workspaceDAO = mock(WorkspaceFileDAO.class);
        when(workspaceDAO.getWorkspaceFilesById("123", null)).thenThrow(new Exception("error"));
        
        WorkspaceFilePathSetterImpl setter = new WorkspaceFilePathSetterImpl(workspaceDAO);
        
        Job t = new Job();
        t.setId(new Long(1));
        ArrayList<Parameter> params = new ArrayList<>();
        Parameter p = new Parameter();
        p.setName("foo");
        p.setValue("123");
        p.setIsWorkspaceId(true);
        params.add(p);
        
        t.setParameters(params);
        try {
            setter.setPaths(t);
            fail("Expected exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("error"));
        }
        
        
    }
    
    //test with parameters but none are workspace parameters
    @Test
    public void testSetPathsWhereNoParametersAreWorkspaceParametersNull() throws Exception {
        WorkspaceFileDAO workspaceDAO = mock(WorkspaceFileDAO.class);
        WorkspaceFilePathSetterImpl setter = new WorkspaceFilePathSetterImpl(workspaceDAO);
        when(workspaceDAO.getWorkspaceFilesById("", null)).thenThrow(new Exception("This should not be called"));
        Job t = new Job();
        t.setId(new Long(1));
        ArrayList<Parameter> params = new ArrayList<>();
        Parameter p = new Parameter();
        p.setName("foo");
        p.setValue("123");
        p.setIsWorkspaceId(false);
        params.add(p);
        
        p = new Parameter();
        p.setName("foo2");
        p.setValue("x");
        p.setIsWorkspaceId(false);
        params.add(p);
        p = new Parameter();
        p.setName("foo3");
        p.setValue("y");
        p.setIsWorkspaceId(false);
        params.add(p);
        t.setParameters(params);
        WorkspaceFilePathSetterStatus status = setter.setPaths(t);
        assertTrue(status.isSuccessful());
        
        assertTrue(status.getReason().equals("Job 1 has no WorkspaceFile parameters that require paths to be set"));
        assertTrue(status.getSuggestedJobStatus().equals(Job.IN_QUEUE_STATUS));
    }
    
    //test with workspacefile parameters, but value is not a Long
    @Test
    public void testSetPathsWhereWorkspaceFileParameterIsNotALong() throws Exception {
        WorkspaceFileDAO workspaceDAO = mock(WorkspaceFileDAO.class);
        WorkspaceFilePathSetterImpl setter = new WorkspaceFilePathSetterImpl(workspaceDAO);
                
        Job t = new Job();
        t.setId(new Long(1));
        ArrayList<Parameter> params = new ArrayList<>();
        Parameter p = new Parameter();
        p.setName("foo");
        p.setValue("xsd");
        p.setIsWorkspaceId(true);
        params.add(p);
        
        t.setParameters(params);
        try {
            setter.setPaths(t);
            fail("Expected exception");
        } catch(NumberFormatException nfe){
            assertTrue(nfe.getMessage().contains("xsd"));
        }
        
    }
    
    //test with workspacefile parameters that exist in map
    @Test
    public void testSetPathsWhereWorkspaceFileParameterIsValidAndInMapAndHasPath() throws Exception {
        WorkspaceFileDAO workspaceDAO = mock(WorkspaceFileDAO.class);
        ArrayList<WorkspaceFile> wspFileList = new ArrayList<>();
        WorkspaceFile wsf = new WorkspaceFile();
        wsf.setId(new Long(123));
        wsf.setPath("path");
        wspFileList.add(wsf);
        when(workspaceDAO.getWorkspaceFilesById("123", null)).thenReturn(wspFileList);
        WorkspaceFilePathSetterImpl setter = new WorkspaceFilePathSetterImpl(workspaceDAO);
                
        Job t = new Job();
        t.setId(new Long(1));
        ArrayList<Parameter> params = new ArrayList<>();
        Parameter p = new Parameter();
        p.setName("foo");
        p.setValue("123");
        p.setIsWorkspaceId(true);
        params.add(p);
        
        t.setParameters(params);
        WorkspaceFilePathSetterStatus status = setter.setPaths(t);
        assertTrue(status.isSuccessful());
        
        //verify path was adjusted
        assertTrue(p.getValue().equals("path"));
        assertTrue(status.getReason() == null);
        assertTrue(status.getSuggestedJobStatus().equals(Job.IN_QUEUE_STATUS));
        
    }
    
    //test with workspacefile parameters, but one or more dont exist in map
     @Test
    public void testSetPathsWhereWorkspaceFileHasNullPath() throws Exception {
        WorkspaceFileDAO workspaceDAO = mock(WorkspaceFileDAO.class);
        ArrayList<WorkspaceFile> wspFileList = new ArrayList<>();
        WorkspaceFile wsf = new WorkspaceFile();
        wsf.setId(new Long(123));
        wsf.setPath("path");
        wspFileList.add(wsf);
        wsf = new WorkspaceFile();
        wsf.setId(new Long(456));
        wspFileList.add(wsf);
        when(workspaceDAO.getWorkspaceFilesById("123,456", null)).thenReturn(wspFileList);
        WorkspaceFilePathSetterImpl setter = new WorkspaceFilePathSetterImpl(workspaceDAO);
                
        Job t = new Job();
        t.setId(new Long(1));
        ArrayList<Parameter> params = new ArrayList<>();
        Parameter p = new Parameter();
        p.setName("foo");
        p.setValue("123");
        p.setIsWorkspaceId(true);
        params.add(p);
        p = new Parameter();
        p.setName("foo2");
        p.setValue("456");
        p.setIsWorkspaceId(true);
        params.add(p);
        
        t.setParameters(params);
        WorkspaceFilePathSetterStatus status = setter.setPaths(t);

        assertFalse(status.isSuccessful());
        assertTrue(status.getReason(),status.getReason().equals("Path is null for WorkspaceFile 456, a parameter for job 1"));
        assertTrue(status.getSuggestedJobStatus().equals(Job.WORKSPACE_SYNC_STATUS));
        verify(workspaceDAO).getWorkspaceFilesById("123,456", null);
    }
    
    //test with workspacefile parameters, but one or more dont exist in map
     @Test
    public void testSetPathsWhereWorkspaceFileFailed() throws Exception {
        WorkspaceFileDAO workspaceDAO = mock(WorkspaceFileDAO.class);
        ArrayList<WorkspaceFile> wspFileList = new ArrayList<>();
        WorkspaceFile wsf = new WorkspaceFile();
        wsf.setId(new Long(123));
        wsf.setPath("path");
        wsf.setFailed(Boolean.FALSE);
        wspFileList.add(wsf);
        wsf = new WorkspaceFile();
        wsf.setId(new Long(456));
        wsf.setPath("path2");
        wsf.setFailed(Boolean.TRUE);
        wspFileList.add(wsf);
        when(workspaceDAO.getWorkspaceFilesById("123,456", null)).thenReturn(wspFileList);
        WorkspaceFilePathSetterImpl setter = new WorkspaceFilePathSetterImpl(workspaceDAO);
                
        Job t = new Job();
        t.setId(new Long(1));
        ArrayList<Parameter> params = new ArrayList<>();
        Parameter p = new Parameter();
        p.setName("foo");
        p.setValue("123");
        p.setIsWorkspaceId(true);
        params.add(p);
        p = new Parameter();
        p.setName("foo2");
        p.setValue("456");
        p.setIsWorkspaceId(true);
        params.add(p);
        
        t.setParameters(params);
        WorkspaceFilePathSetterStatus status = setter.setPaths(t);

        assertFalse(status.isSuccessful());
        assertTrue(status.getReason(),status.getReason().equals("WorkspaceFile 456 failed, a parameter for job 1"));
        assertTrue(status.getSuggestedJobStatus().equals(Job.ERROR_STATUS));
        verify(workspaceDAO).getWorkspaceFilesById("123,456", null);
    }
    
    //test with workspacefile parameters, but one or more dont exist in map
     @Test
    public void testSetPathsWhereWorkspaceFileFailedAndPathIsNull() throws Exception {
        WorkspaceFileDAO workspaceDAO = mock(WorkspaceFileDAO.class);
        ArrayList<WorkspaceFile> wspFileList = new ArrayList<>();
        WorkspaceFile wsf = new WorkspaceFile();
        wsf.setId(new Long(123));
        wsf.setPath("path");
        wsf.setFailed(Boolean.FALSE);
        wspFileList.add(wsf);
        wsf = new WorkspaceFile();
        wsf.setId(new Long(456));
        wsf.setPath(null);
        wsf.setFailed(Boolean.TRUE);
        wspFileList.add(wsf);
        when(workspaceDAO.getWorkspaceFilesById("123,456", null)).thenReturn(wspFileList);
        WorkspaceFilePathSetterImpl setter = new WorkspaceFilePathSetterImpl(workspaceDAO);
                
        Job t = new Job();
        t.setId(new Long(1));
        ArrayList<Parameter> params = new ArrayList<>();
        Parameter p = new Parameter();
        p.setName("foo");
        p.setValue("123");
        p.setIsWorkspaceId(true);
        params.add(p);
        p = new Parameter();
        p.setName("foo2");
        p.setValue("456");
        p.setIsWorkspaceId(true);
        params.add(p);
        
        t.setParameters(params);
        WorkspaceFilePathSetterStatus status = setter.setPaths(t);

        assertFalse(status.isSuccessful());
        assertTrue(status.getReason(),status.getReason().equals("Path is null for WorkspaceFile 456, a parameter for job 1 and WorkSpaceFile is set to failed status"));
        assertTrue(status.getSuggestedJobStatus().equals(Job.ERROR_STATUS));
        verify(workspaceDAO).getWorkspaceFilesById("123,456", null);
    }
    
     //test with workspacefile parameters, but one or more dont exist in map
     @Test
    public void testSetPathsWhereWorkspaceFileNotInMap() throws Exception {
        WorkspaceFileDAO workspaceDAO = mock(WorkspaceFileDAO.class);
        ArrayList<WorkspaceFile> wspFileList = new ArrayList<>();
        WorkspaceFile wsf = new WorkspaceFile();
        wsf.setId(new Long(123));
        wsf.setPath("path");
        wspFileList.add(wsf);
       
        when(workspaceDAO.getWorkspaceFilesById("123,456", null)).thenReturn(wspFileList);
        
        WorkspaceFilePathSetterImpl setter = new WorkspaceFilePathSetterImpl(workspaceDAO);
                
        Job t = new Job();
        t.setId(new Long(1));
        ArrayList<Parameter> params = new ArrayList<>();
        Parameter p = new Parameter();
        p.setName("foo");
        p.setValue("123");
        p.setIsWorkspaceId(true);
        params.add(p);
        p = new Parameter();
        p.setName("foo2");
        p.setValue("456");
        p.setIsWorkspaceId(true);
        params.add(p);
        
        t.setParameters(params);
        WorkspaceFilePathSetterStatus status = setter.setPaths(t);

        assertFalse(status.isSuccessful());
        assertTrue(status.getReason(),status.getReason().equals("No WorkspaceFile with id 456 found for job 1"));
        assertTrue(status.getSuggestedJobStatus().equals(Job.ERROR_STATUS));
        verify(workspaceDAO).getWorkspaceFilesById("123,456", null);
    }
}