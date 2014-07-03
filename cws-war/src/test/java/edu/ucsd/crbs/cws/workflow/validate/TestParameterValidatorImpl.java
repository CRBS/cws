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
import edu.ucsd.crbs.cws.workflow.WorkflowParameter;
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
public class TestParameterValidatorImpl {

    public TestParameterValidatorImpl() {
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
    public void testNullParameterAndNullWorkflowParameter(){
        ParameterValidator pvi = new ParameterValidatorImpl();
        assertTrue(pvi.validate(null).startsWith("Cannot perform validation, Parameter cannot be null"));
        assertTrue(pvi.validate(new Parameter()) == null);
    }
    
    @Test
    public void testRequiredParameterWithNullValue(){
        ParameterValidator pvi = new ParameterValidatorImpl();
        Parameter p = new Parameter();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setIsRequired(true);
        p.setWorkflowParameter(wp);
        assertTrue(pvi.validate(p).startsWith("Parameter value is null, but is set to required"));
    }

    @Test
    public void testRequiredParameterWithNullValidationType(){
        ParameterValidator pvi = new ParameterValidatorImpl();
        Parameter p = new Parameter();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setIsRequired(false);
        p.setWorkflowParameter(wp);
        assertTrue(pvi.validate(p) == null);
    }

    @Test
    public void testValidationNullValueWithValidationTypeSet(){
        ParameterValidator pvi = new ParameterValidatorImpl();
        Parameter p = new Parameter();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setValidationType(WorkflowParameter.ValidationType.NUMBER);
        wp.setIsRequired(false);
        p.setWorkflowParameter(wp);
        assertTrue(pvi.validate(p).equals("Value cannot be null"));
    }
    
    @Test
    public void testNumberValidation(){
        ParameterValidator pvi = new ParameterValidatorImpl();
        Parameter p = new Parameter();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setValidationType(WorkflowParameter.ValidationType.NUMBER);
        wp.setIsRequired(false);
        p.setWorkflowParameter(wp);
        
        //valid number
        p.setValue("1.4");
        assertTrue(pvi.validate(p) == null);
        
        //valid number
        p.setValue("123123");
        assertTrue(pvi.validate(p) == null);
        
        //valid number
        p.setValue("-1023.4");
        assertTrue(pvi.validate(p) == null);

        //invalid number
        p.setValue("hello");
        assertTrue(pvi.validate(p).startsWith("Not a valid number:"));
        
        //range test 
        wp.setMaxValue(10);
        p.setValue("-1");
        assertTrue(pvi.validate(p).startsWith("Value is less then minimum"));
        
        wp.setMinValue(-1.0);
        assertTrue(pvi.validate(p) == null);

        p.setValue("10.1");
        assertTrue(pvi.validate(p).startsWith("Value is greater then maximum"));
    }
    
    @Test
    public void testDigitValidation(){
        ParameterValidator pvi = new ParameterValidatorImpl();
        Parameter p = new Parameter();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setValidationType(WorkflowParameter.ValidationType.DIGITS);
        wp.setIsRequired(false);
        p.setWorkflowParameter(wp);
        
        //valid number
        p.setValue("1");
        assertTrue(pvi.validate(p) == null);
        
        //valid number
        p.setValue("-123123");
        assertTrue(pvi.validate(p) == null);
        
        //valid number
        p.setValue("0");
        assertTrue(pvi.validate(p) == null);

        //invalid number
        p.setValue("123.4");
        assertTrue(pvi.validate(p).startsWith("Not a valid digit:"));

        //invalid number
        p.setValue("whoa");
        assertTrue(pvi.validate(p).startsWith("Not a valid digit:"));

        
        //range test 
        wp.setMaxValue(10);
        p.setValue("-1");
        assertTrue(pvi.validate(p).startsWith("Value is less then minimum"));
        
        wp.setMinValue(-1.0);
        assertTrue(pvi.validate(p) == null);

        p.setValue("11");
        assertTrue(pvi.validate(p).startsWith("Value is greater then maximum"));
    }
    
    @Test
    public void testStringValidation(){
        ParameterValidator pvi = new ParameterValidatorImpl();
        Parameter p = new Parameter();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setValidationType(WorkflowParameter.ValidationType.STRING);
        wp.setIsRequired(false);
        p.setWorkflowParameter(wp);
        
        //valid cause regex is null
        p.setValue("ha");
        assertTrue(pvi.validate(p) == null);
        
        //valid cause regex matches
        wp.setValidationRegex(".*");
        p.setValue("-123123");
        assertTrue(pvi.validate(p) == null);
        
        //valid cause regex matches
        wp.setValidationRegex("^true|false$");
        p.setValue("true");
        assertTrue(pvi.validate(p) == null);
        
        //set max length still valid
        wp.setValidationRegex("^true|false$");
        wp.setMaxLength(5);
        p.setValue("false");
        assertTrue(pvi.validate(p) == null);
        
        //set max length not valid
        wp.setValidationRegex("^true|false$");
        wp.setMaxLength(4);
        p.setValue("false");
        assertTrue(pvi.validate(p).startsWith("Parameter value length:"));
    }
    
    
}