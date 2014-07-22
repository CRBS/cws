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

import edu.ucsd.crbs.cws.workflow.Parameter;
import edu.ucsd.crbs.cws.workflow.Task;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
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
public class TestTaskParametersDuplicateChecker {

    public TestTaskParametersDuplicateChecker() {
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
    public void testNull(){
        TaskParametersDuplicateChecker checker = new TaskParametersDuplicateChecker();
        
        checker.check(null);
        checker.check(new Task());
    }
    
    @Test
    public void testNoDuplicates(){
        TaskParametersDuplicateChecker checker = new TaskParametersDuplicateChecker();
        
        Task t = new Task();
        ArrayList<Parameter> params = new ArrayList<>();
        params.add(new Parameter("n1","v1"));
        params.add(new Parameter("n2","v2"));
        params.add(new Parameter("n3","v3"));
        
        t.setParameters(params);
        assertTrue(t.getParameters().size() == 3);
        assertTrue(t.getParametersWithErrors() == null);
        checker.check(t);
        assertTrue(t.getParameters().size() == 3);
        assertTrue(t.getParametersWithErrors() == null);
        
    }
    
    @Test
    public void testTwoElementsOneDuplicate(){
        TaskParametersDuplicateChecker checker = new TaskParametersDuplicateChecker();
        
        Task t = new Task();
        ArrayList<Parameter> params = new ArrayList<>();
        params.add(new Parameter("n1","v1"));
        params.add(new Parameter("n1","v2"));
        
        t.setParameters(params);
        assertTrue(t.getParameters().size() == 2);
        assertTrue(t.getParametersWithErrors() == null);
        checker.check(t);
        assertTrue(t.getParameters().size() == 1);
        assertTrue(t.getParametersWithErrors().size() == 1);
        assertTrue(t.getParametersWithErrors().get(0).asString().equals("name=n1,value=v2,error=Duplicate name"));
        assertTrue(t.getParameters().get(0).asString().equals("name=n1,value=v1"));
    }

    
    @Test
    public void testThreeElementsOneDuplicate(){
        TaskParametersDuplicateChecker checker = new TaskParametersDuplicateChecker();
        
        Task t = new Task();
        ArrayList<Parameter> params = new ArrayList<>();
        params.add(new Parameter("n1","v1"));
        params.add(new Parameter("n2","v2"));
        params.add(new Parameter("n1","v3"));
        
        t.setParameters(params);
        assertTrue(t.getParameters().size() == 3);
        assertTrue(t.getParametersWithErrors() == null);
        checker.check(t);
        assertTrue(t.getParameters().size() == 2);
        assertTrue(t.getParametersWithErrors().size() == 1);
        assertTrue(t.getParametersWithErrors().get(0).asString().equals("name=n1,value=v3,error=Duplicate name"));
        assertTrue(t.getParameters().get(0).asString().equals("name=n1,value=v1"));
        assertTrue(t.getParameters().get(1).asString().equals("name=n2,value=v2"));
    }
    
    @Test
    public void testFourElementsTwoDifferentDuplicates(){
        TaskParametersDuplicateChecker checker = new TaskParametersDuplicateChecker();
        
        Task t = new Task();
        ArrayList<Parameter> params = new ArrayList<>();
        params.add(new Parameter("n1","v1"));
        params.add(new Parameter("n2","v2"));
        params.add(new Parameter("n1","v3"));
        params.add(new Parameter("n2","v4"));
        
        
        t.setParameters(params);
        assertTrue(t.getParameters().size() == 4);
        assertTrue(t.getParametersWithErrors() == null);
        checker.check(t);
        assertTrue(t.getParameters().size() == 2);
        assertTrue(t.getParametersWithErrors().size() == 2);
        assertTrue(t.getParametersWithErrors().get(0).asString().equals("name=n1,value=v3,error=Duplicate name"));
        assertTrue(t.getParametersWithErrors().get(1).asString().equals("name=n2,value=v4,error=Duplicate name"));
        
        assertTrue(t.getParameters().get(0).asString().equals("name=n1,value=v1"));
        assertTrue(t.getParameters().get(1).asString().equals("name=n2,value=v2"));
    }
    
    
}