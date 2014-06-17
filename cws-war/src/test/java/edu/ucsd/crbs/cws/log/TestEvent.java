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

package edu.ucsd.crbs.cws.log;

import java.util.Date;
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
public class TestEvent {

    public TestEvent() {
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

    /**
     * Test getters and setters
     */
    @Test
    public void testGettersAndSetters(){
        Event e = new Event();
        assertTrue(e.getCity() == null);
        assertTrue(e.getCityLatLong() == null);
        assertTrue(e.getCountry() == null);
        assertTrue(e.getEventType() == null);
        assertTrue(e.getHost() == null);
        assertTrue(e.getIpAddress() == null);
        assertTrue(e.getMessage() == null);
        assertTrue(e.getRegion() == null);
        assertTrue(e.getUserAgent() == null);
        assertTrue(e.getDate() == null);
        assertTrue(e.getStringOfLocationData().equals(""));
        assertTrue(e.getId() == null);
        assertTrue(e.getTaskId() == null);
        assertTrue(e.getUserId() == null);
        assertTrue(e.getWorkflowId() == null);
        
        e.setCity("city");
        e.setCityLatLong("citylatlong");
        e.setCountry("country");
        e.setEventType("type");
        e.setHost("host");
        e.setIpAddress("ip");
        e.setMessage("message");
        e.setRegion("region");
        e.setUserAgent("agent");
        Date curDate = new Date();
        e.setDate(curDate);
        e.setId(new Long(1));
        e.setTaskId(new Long(2));
        e.setUserId(new Long(3));
        e.setWorkflowId(new Long(4));
        
        assertTrue(e.getCity().equals("city"));
        assertTrue(e.getCityLatLong().equals("citylatlong"));
        assertTrue(e.getCountry().equals("country"));
        assertTrue(e.getEventType().equals("type"));
        assertTrue(e.getHost().equals("host"));
        assertTrue(e.getIpAddress().equals("ip"));
        assertTrue(e.getMessage().equals("message"));
        assertTrue(e.getRegion().equals("region"));
        assertTrue(e.getUserAgent().equals("agent"));
        assertTrue(e.getDate().compareTo(curDate) == 0);
        assertTrue(e.getStringOfLocationData().equals("location: citylatlong -- city , region  --- country"));
        assertTrue(e.getId() == 1);
        assertTrue(e.getTaskId() == 2);
        assertTrue(e.getUserId() == 3);
        assertTrue(e.getWorkflowId() == 4);
        
        
        
    }

}