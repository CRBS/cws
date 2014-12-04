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
        assertTrue(wsp.getOwner() == null);
        assertTrue(wsp.getDeleted() == false);
        assertTrue(wsp.getDir() == false);
        assertTrue(wsp.getPath() == null);
        assertTrue(wsp.getSourceJobId() == null);
        assertTrue(wsp.getBlobKey() == null);
        assertTrue(wsp.getUploadURL() == null);
        assertTrue(wsp.isFailed() == false);
        
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
        wsp.setFailed(true);
        
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
        assertTrue(wsp.isFailed() == true);
        
    }
    
    @Test
    public void testUpdateWithChangesWithNullValues(){
        WorkspaceFile wsf = new WorkspaceFile();
        assertFalse(wsf.updateWithChanges(null,null,null,null));
        assertFalse(wsf.updateWithChanges(null, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE));
    }
    
    @Test
    public void testUpdateWithChangesAgainstSelf(){
        WorkspaceFile wsf = new WorkspaceFile();
        wsf.setBlobKey("key");
        wsf.setCreateDate(new Date());
        wsf.setDeleted(true);
        wsf.setDescription("description");
        wsf.setDir(true);
        wsf.setFailed(true);
        wsf.setMd5("md5");
        wsf.setName("name");
        wsf.setOwner("owner");
        wsf.setPath("path");
        wsf.setSize(new Long(1));
        wsf.setSourceJobId(new Long(2));
        wsf.setType("type");
        
        assertFalse(wsf.updateWithChanges(wsf, null,null,null));
        assertFalse(wsf.updateWithChanges(wsf, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE));
        
        assertTrue(wsf.updateWithChanges(wsf, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE));
        assertFalse(wsf.updateWithChanges(wsf, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE));
        assertFalse(wsf.updateWithChanges(wsf, Boolean.FALSE, null, null));
        assertTrue(wsf.updateWithChanges(wsf, Boolean.TRUE, null, null));
        assertTrue(wsf.updateWithChanges(wsf, Boolean.FALSE, null, null));
        assertFalse(wsf.updateWithChanges(wsf, Boolean.FALSE, null, null));
        
        assertFalse(wsf.updateWithChanges(wsf, null,null,null));
        
        assertTrue(wsf.updateWithChanges(wsf, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE));
        assertFalse(wsf.updateWithChanges(wsf, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE));
        assertFalse(wsf.updateWithChanges(wsf,null, Boolean.FALSE, null));
        assertTrue(wsf.updateWithChanges(wsf,null, Boolean.TRUE, null));
        assertTrue(wsf.updateWithChanges(wsf,null, Boolean.FALSE, null));
        assertFalse(wsf.updateWithChanges(wsf,null, Boolean.FALSE, null));
        
        assertFalse(wsf.updateWithChanges(wsf, null,null,null));
        
        assertTrue(wsf.updateWithChanges(wsf, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE));
        assertFalse(wsf.updateWithChanges(wsf, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE));
        
        assertFalse(wsf.updateWithChanges(wsf,null,null, Boolean.FALSE));
        assertTrue(wsf.updateWithChanges(wsf,null,null, Boolean.TRUE));
        assertTrue(wsf.updateWithChanges(wsf,null,null, Boolean.FALSE));
        assertFalse(wsf.updateWithChanges(wsf,null,null, Boolean.FALSE));
        
        assertFalse(wsf.updateWithChanges(wsf, null,null,null));
    }
    
    @Test
    public void testUpdateWithChangesToBlobKey(){
        WorkspaceFile baseWsf = new WorkspaceFile();
        WorkspaceFile newWsf = new WorkspaceFile();
        
        //both null
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        
        //base is null new has value
        newWsf.setBlobKey("key");
        assertTrue(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getBlobKey().equals("key"));
        
        //new is null
        newWsf.setBlobKey(null);
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getBlobKey().equals("key"));
        
        //new is same
        newWsf.setBlobKey(baseWsf.getBlobKey());
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getBlobKey().equals("key"));
        
        //new is different
        newWsf.setBlobKey("key2");
        assertTrue(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getBlobKey().equals("key2"));
    }
    
    @Test
    public void testUpdateWithChangesToCreateDate(){
        WorkspaceFile baseWsf = new WorkspaceFile();
        WorkspaceFile newWsf = new WorkspaceFile();
        
        //both null
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        
        Date newDate = new Date();
        //base is null new has value
        newWsf.setCreateDate(newDate);
        assertTrue(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getCreateDate().equals(newDate));
        
        //new is null
        newWsf.setCreateDate(null);
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getCreateDate().equals(newDate));
        
        //new is same
        newWsf.setCreateDate(newDate);
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getCreateDate().equals(newDate));
        
        //new is different
        Date anotherDate = new Date(newDate.getTime()+100L);
        newWsf.setCreateDate(anotherDate);
        assertTrue(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getCreateDate().equals(anotherDate));
    }
        
    
    @Test
    public void testUpdateWithChangesToDescription(){
        WorkspaceFile baseWsf = new WorkspaceFile();
        WorkspaceFile newWsf = new WorkspaceFile();
        
        //both null
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        
        //base is null new has value
        newWsf.setDescription("description");
        assertTrue(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getDescription().equals("description"));
        
        //new is null
        newWsf.setDescription(null);
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getDescription().equals("description"));
        
        //new is same
        newWsf.setDescription(baseWsf.getDescription());
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getDescription().equals("description"));
        
        //new is different
        newWsf.setDescription("description2");
        assertTrue(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getDescription().equals("description2"));
    }
    
    @Test
    public void testUpdateWithChangesToMd5(){
        WorkspaceFile baseWsf = new WorkspaceFile();
        WorkspaceFile newWsf = new WorkspaceFile();
        
        //both null
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        
        //base is null new has value
        newWsf.setMd5("description");
        assertTrue(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getMd5().equals("description"));
        
        //new is null
        newWsf.setMd5(null);
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getMd5().equals("description"));
        
        //new is same
        newWsf.setMd5(baseWsf.getMd5());
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getMd5().equals("description"));
        
        //new is different
        newWsf.setMd5("description2");
        assertTrue(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getMd5().equals("description2"));
    }
    
    @Test
    public void testUpdateWithChangesToName(){
        WorkspaceFile baseWsf = new WorkspaceFile();
        WorkspaceFile newWsf = new WorkspaceFile();
        
        //both null
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        
        //base is null new has value
        newWsf.setName("description");
        assertTrue(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getName().equals("description"));
        
        //new is null
        newWsf.setName(null);
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getName().equals("description"));
        
        //new is same
        newWsf.setName(baseWsf.getName());
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getName().equals("description"));
        
        //new is different
        newWsf.setName("description2");
        assertTrue(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getName().equals("description2"));
    }
    
    @Test
    public void testUpdateWithChangesToOwner(){
        WorkspaceFile baseWsf = new WorkspaceFile();
        WorkspaceFile newWsf = new WorkspaceFile();
        
        //both null
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        
        //base is null new has value
        newWsf.setOwner("description");
        assertTrue(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getOwner().equals("description"));
        
        //new is null
        newWsf.setOwner(null);
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getOwner().equals("description"));
        
        //new is same
        newWsf.setOwner(baseWsf.getOwner());
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getOwner().equals("description"));
        
        //new is different
        newWsf.setOwner("description2");
        assertTrue(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getOwner().equals("description2"));
    }
    
    @Test
    public void testUpdateWithChangesToPath(){
        WorkspaceFile baseWsf = new WorkspaceFile();
        WorkspaceFile newWsf = new WorkspaceFile();
        
        //both null
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        
        //base is null new has value
        newWsf.setPath("description");
        assertTrue(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getPath().equals("description"));
        
        //new is null
        newWsf.setPath(null);
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getPath().equals("description"));
        
        //new is same
        newWsf.setPath(baseWsf.getPath());
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getPath().equals("description"));
        
        //new is different
        newWsf.setPath("description2");
        assertTrue(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getPath().equals("description2"));
    }
    
    @Test
    public void testUpdateWithChangesToSize(){
        WorkspaceFile baseWsf = new WorkspaceFile();
        WorkspaceFile newWsf = new WorkspaceFile();
        
        //both null
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        
        //base is null new has value
        newWsf.setSize(1L);
        assertTrue(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getSize() == 1L);
        
        //new is null
        newWsf.setSize(null);
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getSize() == 1L);
        
        //new is same
        newWsf.setSize(baseWsf.getSize());
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getSize() == 1L);
        
        //new is different
        newWsf.setSize(2L);
        assertTrue(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getSize() == 2L);
    }
    
    @Test
    public void testUpdateWithChangesToSourceJobId(){
        WorkspaceFile baseWsf = new WorkspaceFile();
        WorkspaceFile newWsf = new WorkspaceFile();
        
        //both null
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        
        //base is null new has value
        newWsf.setSourceJobId(1L);
        assertTrue(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getSourceJobId() == 1L);
        
        //new is null
        newWsf.setSourceJobId(null);
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getSourceJobId() == 1L);
        
        //new is same
        newWsf.setSourceJobId(baseWsf.getSourceJobId());
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getSourceJobId() == 1L);
        
        //new is different
        newWsf.setSourceJobId(2L);
        assertTrue(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getSourceJobId() == 2L);
    }
    
    
    @Test
    public void testUpdateWithChangesToType(){
        WorkspaceFile baseWsf = new WorkspaceFile();
        WorkspaceFile newWsf = new WorkspaceFile();
        
        //both null
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        
        //base is null new has value
        newWsf.setType("description");
        assertTrue(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getType().equals("description"));
        
        //new is null
        newWsf.setType(null);
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getType().equals("description"));
        
        //new is same
        newWsf.setType(baseWsf.getType());
        assertFalse(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getType().equals("description"));
        
        //new is different
        newWsf.setType("description2");
        assertTrue(baseWsf.updateWithChanges(newWsf, null,null,null));
        assertTrue(baseWsf.getType().equals("description2"));
    }
    
    
}