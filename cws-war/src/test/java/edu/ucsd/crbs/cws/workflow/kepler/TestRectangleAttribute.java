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
public class TestRectangleAttribute {

    public TestRectangleAttribute() {
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
    public void testGetterAndSetter(){
        RectangleAttribute ra = new RectangleAttribute();
        assertTrue(ra.getTextFromTextAttributes() == null);
        assertTrue(ra.getIntersectingParameterAttributes() == null);
        assertTrue(ra.getIntersectingTextAttributes() == null);
        assertTrue(ra.asString().endsWith(",width=0.0,height=0.0,numtextattributes=0,numparameterattributes=0"));

        assertTrue(ra.getHeight() == 0.0);
        assertTrue(ra.getWidth() == 0.0);
        ra.setHeight(1);
        ra.setWidth(2);
        assertTrue(ra.getWidth() == 2);
        assertTrue(ra.getHeight() == 1);
    }
    
    @Test
    public void testDoesIntersect(){
        RectangleAttribute ra = new RectangleAttribute();
        assertTrue(ra.doesIntersect(null) == false);
        
        /*  attribute above left test
          
          #
            ______
           |      |
           |______|
        */
        ra.setXCoordinate(-100);
        ra.setYCoordinate(20);
        ra.setWidth(100);
        ra.setHeight(100);
        
        Attribute attrib = new Attribute();
        attrib.setXCoordinate(-110);
        attrib.setYCoordinate(10);
        
        assertTrue(ra.doesIntersect(attrib) == false);
        
        
        
        /* attribute above test
        
               #
            ______
           |      |
           |______|
        
        */
        
        attrib = new Attribute();
        attrib.setXCoordinate(-50);
        attrib.setYCoordinate(10);
        
        assertTrue(ra.doesIntersect(attrib) == false);
        
        
       /* attribute below test
        
               
            ______
           |      |
           |______|
              #
        */
        
        attrib = new Attribute();
        attrib.setXCoordinate(-50);
        attrib.setYCoordinate(130);
        
        assertTrue(ra.doesIntersect(attrib) == false);

        
        /* attribute left
        
               
            ______
         # |      |
           |______|
              
        */
        
        attrib = new Attribute();
        attrib.setXCoordinate(-150);
        attrib.setYCoordinate(60);
        
        assertTrue(ra.doesIntersect(attrib) == false);

        /* attribute left
        
               
            ______
           |      | #
           |______|
              
        */
        
        attrib = new Attribute();
        attrib.setXCoordinate(10);
        attrib.setYCoordinate(60);
        
        assertTrue(ra.doesIntersect(attrib) == false);
        
        /* attribute within
        
               
            ______
           |    # | 
           |______|
              
        */
        
        attrib = new Attribute();
        attrib.setXCoordinate(-10);
        attrib.setYCoordinate(60);
        
        assertTrue(ra.doesIntersect(attrib) == true);
        
    }

    
    @Test
    public void testAddTextAttributeIfIntersecting(){
        
        RectangleAttribute ra = new RectangleAttribute();
        
        assertTrue(ra.addTextAttributeIfIntersecting(null) == false);
        assertTrue(ra.getIntersectingTextAttributes() == null);
        
        //non intersecting attribute
        ra.setXCoordinate(-100);
        ra.setYCoordinate(20);
        ra.setWidth(100);
        ra.setHeight(100);
        
        TextAttribute attrib = new TextAttribute();
        attrib.setXCoordinate(-110);
        attrib.setYCoordinate(10);
        assertTrue(ra.addTextAttributeIfIntersecting(attrib) == false);
        assertTrue(ra.getIntersectingTextAttributes() == null);
        
        //valid attribute that creates a new list
        attrib.setXCoordinate(-10);
        attrib.setYCoordinate(50);
        assertTrue(ra.addTextAttributeIfIntersecting(attrib) == true);
        assertTrue(ra.getIntersectingTextAttributes().size() == 1);
        
        //add same attribute again there is no checking so it just adds it
        attrib.setXCoordinate(-10);
        attrib.setYCoordinate(50);
        assertTrue(ra.addTextAttributeIfIntersecting(attrib) == true);
        assertTrue(ra.getIntersectingTextAttributes().size() == 2);
    }
    
    
    @Test
    public void testAddParameterAttributeIfIntersecting(){
        
        RectangleAttribute ra = new RectangleAttribute();
        
        assertTrue(ra.addParameterAttributeIfIntersecting(null) == false);
        assertTrue(ra.getIntersectingParameterAttributes() == null);
        
        //non intersecting attribute
        ra.setXCoordinate(-100);
        ra.setYCoordinate(20);
        ra.setWidth(100);
        ra.setHeight(100);
        
        ParameterAttribute attrib = new ParameterAttribute();
        attrib.setXCoordinate(-110);
        attrib.setYCoordinate(10);
        assertTrue(ra.addParameterAttributeIfIntersecting(attrib) == false);
        assertTrue(ra.getIntersectingParameterAttributes() == null);
        
        //valid attribute that creates a new list
        attrib.setXCoordinate(-10);
        attrib.setYCoordinate(50);
        assertTrue(ra.addParameterAttributeIfIntersecting(attrib) == true);
        assertTrue(ra.getIntersectingParameterAttributes().size() == 1);
        
        //add same attribute again there is no checking so it just adds it
        attrib.setXCoordinate(-10);
        attrib.setYCoordinate(50);
        assertTrue(ra.addParameterAttributeIfIntersecting(attrib) == true);
        assertTrue(ra.getIntersectingParameterAttributes().size() == 2);
    }
    
    @Test
    public void testmoveCoordinatesToUpperLeftCornerFromCenter(){
        
        RectangleAttribute ra = new RectangleAttribute();
        assertTrue(ra.getXCoordinate() == 0);
        assertTrue(ra.getYCoordinate() == 0);
        ra.moveCoordinatesToUpperLeftCornerFromCenter();
        assertTrue(ra.getXCoordinate() == 0);
        assertTrue(ra.getYCoordinate() == 0);
        
        ra.setXCoordinate(100);
        ra.setYCoordinate(200);
        ra.setWidth(40);
        ra.setHeight(80);
        assertTrue(ra.getXCoordinate() == 100);
        assertTrue(ra.getYCoordinate() == 200);
        ra.moveCoordinatesToUpperLeftCornerFromCenter();
        
        assertTrue(Math.abs(ra.getXCoordinate()-80)<0.1);
        assertTrue(Math.abs(ra.getYCoordinate() - 160)<0.1);
        ra.moveCoordinatesToUpperLeftCornerFromCenter();
        assertTrue(ra.getXCoordinate() == 60);
        assertTrue(ra.getYCoordinate() == 120);
        ra.setWidth(0);
        ra.setHeight(0);
        ra.moveCoordinatesToUpperLeftCornerFromCenter();
        assertTrue(ra.getXCoordinate() == 60);
        assertTrue(ra.getYCoordinate() == 120);
        
        ra.setXCoordinate(-100);
        ra.setYCoordinate(-200);
        ra.setWidth(40);
        ra.setHeight(80);
        ra.moveCoordinatesToUpperLeftCornerFromCenter();
        assertTrue(ra.getXCoordinate() == -120);
        assertTrue(ra.getYCoordinate() == -240);
    }
    
    @Test
    public void testgetTextFromTextAttributes(){
        RectangleAttribute ra = new RectangleAttribute();
        ra.setXCoordinate(0);
        ra.setYCoordinate(0);
        ra.setWidth(10000);
        ra.setHeight(10000);
        
        assertTrue(ra.getTextFromTextAttributes() == null);
        
        TextAttribute ta = new TextAttribute();
        ta.setXCoordinate(100);
        ta.setYCoordinate(200);
        ta.setText("type=foo\nhello=1");
        assertTrue(ra.addTextAttributeIfIntersecting(ta) == true);
        assertTrue(ra.getTextFromTextAttributes().equals("type=foo\nhello=1"));
        
        ta = new TextAttribute();
        ta.setXCoordinate(100);
        ta.setYCoordinate(200);
        ta.setText(null);
        assertTrue(ra.addTextAttributeIfIntersecting(ta) == true);
        assertTrue(ra.getTextFromTextAttributes().equals("type=foo\nhello=1\n"));
        
        ta = new TextAttribute();
        ta.setXCoordinate(100);
        ta.setYCoordinate(200);
        ta.setText("");
        assertTrue(ra.addTextAttributeIfIntersecting(ta) == true);
        assertTrue(ra.getTextFromTextAttributes().equals("type=foo\nhello=1\n\n"));
        
        ta = new TextAttribute();
        ta.setXCoordinate(100);
        ta.setYCoordinate(200);
        ta.setText("blah");
        assertTrue(ra.addTextAttributeIfIntersecting(ta) == true);
        assertTrue(ra.getTextFromTextAttributes().equals("type=foo\nhello=1\n\n\nblah"));
     
        assertTrue(ra.asString().endsWith(",width=10000.0,height=10000.0,numtextattributes=4,numparameterattributes=0"));
    }
    
}