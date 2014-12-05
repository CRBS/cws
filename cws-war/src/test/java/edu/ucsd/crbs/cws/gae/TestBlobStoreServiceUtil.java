/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2014 The Regents of the University of California All Rights Reserved
 * 
 * Permission to copy, modify and distribute any part of this cws-war for 
 * educational, research and non-profit purposes, without fee, and without a 
 * written agreement is hereby granted, provided that the above copyright notice, 
 * this paragraph and the following three paragraphs appear in all copies.
 * 
 * Those desiring to incorporate this cws-war into commercial products
 * or use for commercial purposes should contact the Technology Transfer Office, 
 * University of California, San Diego, 9500 Gilman Drive, Mail Code 0910, 
 * La Jolla, CA 92093-0910, Ph: (858) 534-5815, FAX: (858) 534-7345, 
 * E-MAIL:invent@ucsd.edu.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR 
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING 
 * LOST PROFITS, ARISING OUT OF THE USE OF THIS cws-war, EVEN IF THE UNIVERSITY 
 * OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE cws-war PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE UNIVERSITY 
 * OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES NO 
 * REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR 
 * EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF 
 * THE cws-war WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS. 
 */

package edu.ucsd.crbs.cws.gae;

import com.google.appengine.api.blobstore.BlobKey;
import edu.ucsd.crbs.cws.gae.BlobStoreServiceUtil;
import edu.ucsd.crbs.cws.workflow.Workflow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class TestBlobStoreServiceUtil {

    public TestBlobStoreServiceUtil() {
    }

    @BeforeClass
    public static void setUpClass() {
        Logger.getLogger("edu.ucsd.crbs.cws.gae.BlobStoreServiceUtil").setLevel(Level.OFF);
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
    
    
    //test null map
    @Test
    public void testNullMap(){
        try {
            BlobStoreServiceUtil.getWorkflowWithBlobKeyFromMapOfBlobKeyLists(null);
            fail("Expected exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().equals("Unable to get list of uploads from BlobStoreService"));
        }
    }
    
    //test empty map
    @Test
    public void testEmptyMap(){
        try {
            BlobStoreServiceUtil.getWorkflowWithBlobKeyFromMapOfBlobKeyLists(new HashMap<String,List<BlobKey>>());
            fail("Expected exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().equals("No uploaded files found"));
        }
    }
    
    //test map with key, but no blobs
    @Test
    public void testMapWithKeyButNoBlobKeys(){
        
        try {
            HashMap<String,List<BlobKey>> hMap = new HashMap<>();
            hMap.put("12345", null);
            BlobStoreServiceUtil.getWorkflowWithBlobKeyFromMapOfBlobKeyLists(hMap);
            fail("Expected exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().equals("No uploaded files found for id 12345"));
        }
    }
    
    //test map with one key and one blob key
    @Test
    public void testMapWithKeyAndOneBlobKey(){
        
        try {
            HashMap<String,List<BlobKey>> hMap = new HashMap<>();
            ArrayList<BlobKey> bkList = new ArrayList<>();
            bkList.add(new BlobKey("mykey"));
            hMap.put("12345", bkList);
            Workflow w = BlobStoreServiceUtil.getWorkflowWithBlobKeyFromMapOfBlobKeyLists(hMap);
            assertTrue(w != null);
            assertTrue(w.getId() == 12345);
            assertTrue(w.getBlobKey().equals("mykey"));
        }
        catch(Exception ex){
            fail("Unexpected Exception: "+ex.getMessage());
        }
    }
    
    //test map with one key and multiple blobs
    @Test
    public void testMapWithKeyAndMultipleBlobKeys(){
        
        try {
            HashMap<String,List<BlobKey>> hMap = new HashMap<>();
            ArrayList<BlobKey> bkList = new ArrayList<>();
            bkList.add(new BlobKey("mykey"));
            bkList.add(new BlobKey("mykey2"));
            bkList.add(new BlobKey("mykey3"));

            hMap.put("12345", bkList);
            Workflow w = BlobStoreServiceUtil.getWorkflowWithBlobKeyFromMapOfBlobKeyLists(hMap);
            assertTrue(w != null);
            assertTrue(w.getId() == 12345);
            assertTrue(w.getBlobKey().equals("mykey"));
        }
        catch(Exception ex){
            fail("Unexpected Exception: "+ex.getMessage());
        }
    }
    
    //test map with multiple keys and multiple blobs
    @Test
    public void testMapWithMulitpleKeys(){
        
        try {
            HashMap<String,List<BlobKey>> hMap = new HashMap<>();
            ArrayList<BlobKey> bkList = new ArrayList<>();
            bkList.add(new BlobKey("mykey"));

            hMap.put("12345", bkList);
            ArrayList<BlobKey> bkListTwo = new ArrayList<>();
            bkListTwo.add(new BlobKey("mykey2"));
            hMap.put("3",bkListTwo);
            Workflow w = BlobStoreServiceUtil.getWorkflowWithBlobKeyFromMapOfBlobKeyLists(hMap);
            assertTrue(w != null);
            assertTrue((w.getBlobKey().equals("mykey") && w.getId() == 12345) ||
                    (w.getBlobKey().equals("mykey2") && w.getId() == 3));
        }
        catch(Exception ex){
            fail("Unexpected Exception: "+ex.getMessage());
        }
    }
}