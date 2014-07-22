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

package edu.ucsd.crbs.cws.workflow;

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
public class TestWorkspaceFile {

    public TestWorkspaceFile() {
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
    public void testGettersAndSetters(){
        WorkspaceFile wsp = new WorkspaceFile();
        assertTrue(wsp.getId() == null);
        assertTrue(wsp.getName() == null);
        assertTrue(wsp.getType() == null);
        assertTrue(wsp.getOwner() == null);
        assertTrue(wsp.getDescription() == null);
        assertTrue(wsp.getCreateDate() == null);
        assertTrue(wsp.getSize() == null);
        assertTrue(wsp.getMd5() == null);
        assertTrue(wsp.getDeleted() == false);
        assertTrue(wsp.getDir() == false);
        assertTrue(wsp.getPath() == null);
        assertTrue(wsp.getSourceJobId() == null);
        assertTrue(wsp.getBlobKey() == null);
        assertTrue(wsp.getUploadURL() == null);
        
        wsp.setId(new Long(1));
        wsp.setName("name");
        wsp.setType("type");
        wsp.setOwner("owner");
        wsp.setDescription("description");
        Date aDate = new Date();
        wsp.setCreateDate(aDate);
        wsp.setSize(new Long(2));
        wsp.setMd5("md5");
        wsp.setDeleted(true);
        wsp.setDir(true);
        wsp.setPath("path");
        wsp.setSourceJobId(new Long(3));
        wsp.setBlobKey("blobkey");
        wsp.setUploadURL("uploadurl");
        
        assertTrue(wsp.getId() == 1);
        assertTrue(wsp.getName().equals("name"));
        assertTrue(wsp.getType().equals("type"));
        assertTrue(wsp.getOwner().equals("owner"));
        assertTrue(wsp.getDescription().equals("description"));
        assertTrue(wsp.getCreateDate().equals(aDate));
        assertTrue(wsp.getSize() == 2);
        assertTrue(wsp.getMd5().equals("md5"));
        assertTrue(wsp.getDeleted() == true);
        assertTrue(wsp.getDir() == true);
        assertTrue(wsp.getPath().equals("path"));
        assertTrue(wsp.getSourceJobId() == 3);
        assertTrue(wsp.getBlobKey().equals("blobkey"));
        assertTrue(wsp.getUploadURL().equals("uploadurl"));
        
    }

}