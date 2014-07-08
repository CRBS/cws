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

package edu.ucsd.crbs.cws.workflow.validate;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.dao.WorkflowDAO;
import edu.ucsd.crbs.cws.workflow.Parameter;
import edu.ucsd.crbs.cws.workflow.ParameterWithError;
import edu.ucsd.crbs.cws.workflow.Task;
import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.WorkflowParameter;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestTaskValidatorImpl {

     private final LocalServiceTestHelper _helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    
    public TestTaskValidatorImpl() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        _helper.setUp();
        Task.REFS_ENABLED = false;
    }

    @After
    public void tearDown() {
        _helper.tearDown();
    }

   @Test
   public void testNullTask(){
       TaskValidatorImpl tvi = new TaskValidatorImpl();
       try {
        tvi.validateParameters(null, null);
        fail("Expected exception");
       }catch(Exception ex){
           assertTrue(ex.getMessage().startsWith("Task cannot be null"));
       }
   }
   
   @Test
   public void testUnableToLoadWorkflow() throws Exception{
       TaskValidatorImpl tvi = new TaskValidatorImpl();
       TaskParametersChecker mockNullChecker = mock(TaskParametersChecker.class);
       TaskParametersChecker mockDupChecker = mock(TaskParametersChecker.class);
       tvi._taskParamNullChecker = mockNullChecker;
       tvi._taskParamDuplicateChecker = mockDupChecker;
       
       WorkflowDAO mockDAO = mock(WorkflowDAO.class);
       Task t = new Task();
       User u = new User();
       when(mockDAO.getWorkflowForTask(t,u)).thenReturn(null);
       tvi._workflowDAO = mockDAO;
       Task res = tvi.validateParameters(t, u);
       assertTrue(res.getError().equals("Unable to load workflow for task"));
      
   }
   
   @Test
   public void testUnableToLoadWorkflowDueToException() throws Exception{
       TaskValidatorImpl tvi = new TaskValidatorImpl();
       TaskParametersChecker mockNullChecker = mock(TaskParametersChecker.class);
       TaskParametersChecker mockDupChecker = mock(TaskParametersChecker.class);
       tvi._taskParamNullChecker = mockNullChecker;
       tvi._taskParamDuplicateChecker = mockDupChecker;
       
       WorkflowDAO mockDAO = mock(WorkflowDAO.class);
       Task t = new Task();
       User u = new User();
       when(mockDAO.getWorkflowForTask(t,u)).thenThrow(new IllegalArgumentException("error"));
       tvi._workflowDAO = mockDAO;
       Task res = tvi.validateParameters(t, u);
       assertTrue(res.getError().equals("error"));
      
   }


   @Test
   public void testNoParametersInTaskCase() throws Exception{
       TaskValidatorImpl tvi = new TaskValidatorImpl();
       TaskParametersChecker mockNullChecker = mock(TaskParametersChecker.class);
       TaskParametersChecker mockDupChecker = mock(TaskParametersChecker.class);
       tvi._taskParamNullChecker = mockNullChecker;
       tvi._taskParamDuplicateChecker = mockDupChecker;
       
       WorkflowDAO mockDAO = mock(WorkflowDAO.class);
       Task t = new Task();
       User u = new User();
       when(mockDAO.getWorkflowForTask(t,u)).thenReturn(new Workflow());
       tvi._workflowDAO = mockDAO;
       Task res = tvi.validateParameters(t, u);
       assertTrue(res.getError() == null);
      
   }

   
   @Test
   public void testOneParameterNoMatchWithWorkflowParameter() throws Exception{
       TaskValidatorImpl tvi = new TaskValidatorImpl();
       TaskParametersChecker mockNullChecker = mock(TaskParametersChecker.class);
       TaskParametersChecker mockDupChecker = mock(TaskParametersChecker.class);
       tvi._taskParamNullChecker = mockNullChecker;
       tvi._taskParamDuplicateChecker = mockDupChecker;
       
       WorkflowDAO mockDAO = mock(WorkflowDAO.class);
       Task t = new Task();
       Parameter param = new Parameter();
       param.setName("blah");
       param.setValue("value");
       ArrayList<Parameter> paramList = new ArrayList<>();
       paramList.add(param);
       t.setParameters(paramList);
       
       User u = new User();
       Workflow w = new Workflow();
       WorkflowParameter wp = new WorkflowParameter();
       wp.setName("foo");
       wp.setType(WorkflowParameter.Type.TEXT);
       ArrayList<WorkflowParameter> wpList = new ArrayList<>();
       wpList.add(wp);
       w.setParameters(wpList);
       when(mockDAO.getWorkflowForTask(t,u)).thenReturn(w);
       
       tvi._workflowDAO = mockDAO;
       Task res = tvi.validateParameters(t, u);
       assertTrue(res.getError() == null);
       assertTrue(res.getParametersWithErrors().size() == 1);
       ParameterWithError reswp = res.getParametersWithErrors().get(0);
       assertTrue(reswp.getError().startsWith("No matching WorkflowParameter"));
       assertTrue(reswp.getName().equals("blah"));
   }
   
   
   @Test
   public void testNoMatchRequiredWorkflowParameter() throws Exception{
       TaskValidatorImpl tvi = new TaskValidatorImpl();
       TaskParametersChecker mockNullChecker = mock(TaskParametersChecker.class);
       TaskParametersChecker mockDupChecker = mock(TaskParametersChecker.class);
       tvi._taskParamNullChecker = mockNullChecker;
       tvi._taskParamDuplicateChecker = mockDupChecker;
       
       WorkflowDAO mockDAO = mock(WorkflowDAO.class);
       Task t = new Task();
       Parameter param = new Parameter();
       param.setName("blah");
       param.setValue("value");
       ArrayList<Parameter> paramList = new ArrayList<>();
       paramList.add(param);
       t.setParameters(paramList);
       
       User u = new User();
       Workflow w = new Workflow();
       WorkflowParameter wp = new WorkflowParameter();
       wp.setIsRequired(true);
       wp.setName("foo");
       wp.setType(WorkflowParameter.Type.TEXT);
       ArrayList<WorkflowParameter> wpList = new ArrayList<>();
       wpList.add(wp);
       
       wp = new WorkflowParameter();
       wp.setIsRequired(false);
       wp.setName("blah");
       wp.setType(WorkflowParameter.Type.TEXT);
       wpList.add(wp);
       
       w.setParameters(wpList);
       when(mockDAO.getWorkflowForTask(t,u)).thenReturn(w);
       
       tvi._workflowDAO = mockDAO;
       Task res = tvi.validateParameters(t, u);
       assertTrue(res.getError() == null);
       assertTrue(res.getParameters().size() == 1);
       assertTrue(res.getParametersWithErrors().size() == 1);
       ParameterWithError reswp = res.getParametersWithErrors().get(0);
       assertTrue(reswp.getError().startsWith("Required parameter"));
       assertTrue(reswp.getName().equals("foo"));
   }

   @Test
   public void testParameterFailsValidator() throws Exception{
       TaskValidatorImpl tvi = new TaskValidatorImpl();
       TaskParametersChecker mockNullChecker = mock(TaskParametersChecker.class);
       TaskParametersChecker mockDupChecker = mock(TaskParametersChecker.class);
       tvi._taskParamNullChecker = mockNullChecker;
       tvi._taskParamDuplicateChecker = mockDupChecker;
       
       WorkflowDAO mockDAO = mock(WorkflowDAO.class);
       Task t = new Task();
       Parameter param = new Parameter();
       param.setName("blah");
       param.setValue("1.5");
       ArrayList<Parameter> paramList = new ArrayList<>();
       paramList.add(param);
       t.setParameters(paramList);
       
       User u = new User();
       Workflow w = new Workflow();
       WorkflowParameter wp;
       ArrayList<WorkflowParameter> wpList = new ArrayList<>();       
       wp = new WorkflowParameter();
       wp.setIsRequired(false);
       wp.setName("blah");
       wp.setType(WorkflowParameter.Type.TEXT);
       wp.setValidationType(WorkflowParameter.ValidationType.DIGITS);
       wpList.add(wp);
       
       w.setParameters(wpList);
       when(mockDAO.getWorkflowForTask(t,u)).thenReturn(w);
       
       ParameterValidator pMock = mock(ParameterValidator.class);
       when(pMock.validate(param)).thenReturn("error");
       tvi._parameterValidator = pMock;
       
       tvi._workflowDAO = mockDAO;
       Task res = tvi.validateParameters(t, u);
       assertTrue(res.getError() == null);
       assertTrue(res.getParameters().isEmpty());
       assertTrue(res.getParametersWithErrors().size() == 1);
       ParameterWithError reswp = res.getParametersWithErrors().get(0);
       assertTrue(reswp.getError().startsWith("error"));
       assertTrue(reswp.getName().equals("blah"));
   }

   
}