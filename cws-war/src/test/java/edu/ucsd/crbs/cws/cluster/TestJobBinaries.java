/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2015 The Regents of the University of California All Rights Reserved
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



/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestJobBinaries {

    public TestJobBinaries() {
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
        JobBinaries jb = new JobBinaries();
        
        assertTrue(jb.getRegisterUpdateJar().equals(""));
        assertTrue(jb.getRetryCount() == 0);
        assertTrue(jb.getKeplerScript() == null);
        assertTrue(jb.getMailCommand().equals("/bin/mail"));
        assertTrue(jb.getSleepCommand().equals("sleep"));
        assertTrue(jb.getJavaCommand().equals("java"));
        assertTrue(jb.getEchoCommand().equals("echo"));
        assertTrue(jb.getRmCommand().equals("/bin/rm"));
        assertTrue(jb.getKillCommand().equals("kill"));
        assertTrue(jb.getPostEmailSleepTimeInSeconds() == 0);
        assertTrue(jb.getWorkspaceUpdateRetrySleepTimeInSeconds() == 0);
        
        jb.setRegisterUpdateJar("theregister.jar");
        jb.setRetryCount(1);
        jb.setKeplerScript("thekepler");
        jb.setMailCommand("themail");
        jb.setSleepCommand("thesleep");
        jb.setJavaCommand("thejava");
        jb.setEchoCommand("theecho");
        jb.setRmCommand("therm");
        jb.setKillCommand("thekill");
        jb.setPostEmailSleepTimeInSeconds(2);
        jb.setWorkspaceUpdateRetrySleepTimeInSeconds(3);

        assertTrue(jb.getRegisterUpdateJar().equals("theregister.jar"));
        assertTrue(jb.getRetryCount() == 1);
        assertTrue(jb.getKeplerScript().equals("thekepler"));
        assertTrue(jb.getMailCommand().equals("themail"));
        assertTrue(jb.getSleepCommand().equals("thesleep"));
        assertTrue(jb.getJavaCommand().equals("thejava"));
        assertTrue(jb.getEchoCommand().equals("theecho"));
        assertTrue(jb.getRmCommand().equals("therm"));
        assertTrue(jb.getKillCommand().equals("thekill"));
        assertTrue(jb.getPostEmailSleepTimeInSeconds() == 2);
        assertTrue(jb.getWorkspaceUpdateRetrySleepTimeInSeconds() == 3);
    }
}