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

package edu.ucsd.crbs.cws.workflow.kepler;

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
public class TestAttribute {

    public TestAttribute() {
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
    public void testGetterAndSetters(){
        Attribute attrib = new Attribute();
        
        assertTrue(attrib.getDisplayName() == null);
        assertTrue(attrib.getName() == null);
        assertTrue(attrib.getXCoordinate() == 0);
        assertTrue(attrib.getYCoordinate() == 0);
        attrib.setDisplayName("display");
        attrib.setName("name");
        attrib.setXCoordinate(1);
        attrib.setYCoordinate(2);
        
        assertTrue(attrib.getDisplayName().equals("display"));
        assertTrue(attrib.getName().equals("name"));
        assertTrue(attrib.getXCoordinate() == 1);
        assertTrue(attrib.getYCoordinate() == 2);

    }
    
    @Test
    public void testSetCoordinatesViaString() throws Exception{
        Attribute attrib = new Attribute();
        
        //try null string
        attrib.setCoordinatesViaString(null);
        assertTrue(attrib.getXCoordinate() == 0);
        assertTrue(attrib.getYCoordinate() == 0);
        
        //try empty string
        attrib.setCoordinatesViaString(null);
        assertTrue(attrib.getXCoordinate() == 0);
        assertTrue(attrib.getYCoordinate() == 0);
        
        //try valid string
        attrib.setCoordinatesViaString("[25.4, 10.3]");
        assertTrue(attrib.getXCoordinate() == 25.4);
        assertTrue(attrib.getYCoordinate() == 10.3);
        
        //try string with negative numbers
         attrib.setCoordinatesViaString("[-3.4,-898.3]");
        assertTrue(attrib.getXCoordinate() == -3.4);
        assertTrue(attrib.getYCoordinate() == -898.3);
        
        //try with missing [ ]
         attrib.setCoordinatesViaString("-0.4 ,28");
        assertTrue(attrib.getXCoordinate() == -0.4);
        assertTrue(attrib.getYCoordinate() == 28);
    
        try {
            //try with missing ,
            attrib.setCoordinatesViaString("[-4 6]");
            fail("Expected exception cause we didnt set a comma in location string");
        } catch (Exception ex) {
            assertTrue(ex.getMessage().equals("No comma delimiting x and y coordinates found"));
        }
        // try with { } 
        attrib.setCoordinatesViaString("{-19.2, 6.6}");
        assertTrue(attrib.getXCoordinate() == -19.2);
        assertTrue(attrib.getYCoordinate() == 6.6);
     
    }
    
    
    @Test
    public void testAsString(){
        Attribute attrib = new Attribute();
        assertTrue(attrib.asString().equals("name=null,displayname=null,x=0.0,y=0.0"));
        
        attrib.setDisplayName("display");
        attrib.setName("name");
        attrib.setXCoordinate(1);
        attrib.setYCoordinate(2);
        assertTrue(attrib.asString().equals("name=name,displayname=display,x=1.0,y=2.0"));
    }
}