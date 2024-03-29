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
import edu.ucsd.crbs.cws.workflow.Job;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class TestJobParametersNullNameChecker {

    public TestJobParametersNullNameChecker() {
    }

    @BeforeClass
    public static void setUpClass() {
         Logger.getLogger(JobParametersNullNameChecker.class.getName()).setLevel(Level.OFF);
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

    /**
     * Just make sure nothing blows up in either case below
     */
    @Test
    public void testNullTaskAndParameters(){
        JobParametersNullNameChecker checker = new JobParametersNullNameChecker();
        
        checker.check(null);
        checker.check(new Job());
    }

    @Test
    public void testNoNulls(){
        JobParametersNullNameChecker checker = new JobParametersNullNameChecker();
        Job t = new Job();
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
    public void testSingleNullParameter(){
        JobParametersNullNameChecker checker = new JobParametersNullNameChecker();
        Job t = new Job();
        ArrayList<Parameter> params = new ArrayList<>();
        params.add(new Parameter());
        t.setParameters(params);
        assertTrue(t.getParameters().isEmpty() == false);
        assertTrue(t.getParametersWithErrors() == null);
        checker.check(t);
        assertTrue(t.getParameters().isEmpty() == true);
        assertTrue(t.getParametersWithErrors().get(0).getError().equals("Parameter name is null"));
    }
    
    @Test
    public void testSingleNullInListOfThreeParameters(){
        JobParametersNullNameChecker checker = new JobParametersNullNameChecker();
        Job t = new Job();
        ArrayList<Parameter> params = new ArrayList<>();
        params.add(new Parameter("n1","v1"));
        params.add(new Parameter(null,"v2"));
        params.add(new Parameter("n3","v3"));
        
        t.setParameters(params);
        assertTrue(t.getParameters().isEmpty() == false);
        assertTrue(t.getParametersWithErrors() == null);
        checker.check(t);
        assertTrue(t.getParameters().size() == 2);
        assertTrue(t.getParametersWithErrors().size() == 1);
        assertTrue(t.getParametersWithErrors().get(0).asString().equals("name=null,value=v2,error=Parameter name is null"));
        assertTrue(t.getParameters().get(0).asString().equals("name=n1,value=v1"));
        assertTrue(t.getParameters().get(1).asString().equals("name=n3,value=v3"));
    }
}