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

package edu.ucsd.crbs.cws.io;

import edu.ucsd.crbs.cws.rest.Constants;
import java.io.File;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;



/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestWorkflowFailedParserImpl {

    @Rule
    public TemporaryFolder Folder = new TemporaryFolder();
    
    public TestWorkflowFailedParserImpl() {
    }

    @BeforeClass
    public static void setUpClass() {
        Logger.getLogger(WorkflowFailedParserImpl.class.getName()).setLevel(Level.OFF);
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
    public void testGetAndExistMethodsBeforeSetPathInvocation() throws Exception {
        WorkflowFailedParserImpl parser = new WorkflowFailedParserImpl();
        assertTrue(parser.exists() == false);
        assertTrue(parser.getDetailedError() == null);
        assertTrue(parser.getError() == null);
    }
    
    @Test
    public void testSetPathNullArg() throws Exception {
        WorkflowFailedParserImpl parser = new WorkflowFailedParserImpl();
        try {
            parser.setPath(null);
            fail("Expected exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("Path is null"));
        }
    }
    
    @Test
    public void testWherePathDoesNotExist() throws Exception {
       
        WorkflowFailedParserImpl parser = new WorkflowFailedParserImpl();
        parser.setPath("/path/does/not/existasdflkjasdkfljasdfklj");
        assertTrue(parser.exists() == false);
        assertTrue(parser.getDetailedError() == null);
        assertTrue(parser.getError() == null);
    }
    
    @Test
    public void testWherePathIsDirButHasNoFailedFile() throws Exception {
        File testFolder = Folder.newFolder();
        WorkflowFailedParserImpl parser = new WorkflowFailedParserImpl();
        parser.setPath(testFolder.getAbsolutePath());
        assertTrue(parser.exists() == false);
        assertTrue(parser.getDetailedError() == null);
        assertTrue(parser.getError() == null);
    }

    
    @Test
    public void testWherePathIsDirButAndHasFailedFile() throws Exception {
        File testFolder = Folder.newFolder();
        
        FileWriter fw = new FileWriter(testFolder.getAbsolutePath()+File.separator+Constants.WORKFLOW_FAILED_FILE);
        fw.write(Constants.SIMPLE_ERROR_MESSAGE_KEY+"=simple error\n");
        fw.write(Constants.DETAILED_ERROR_MESSAGE_KEY+"=detailed error\n");
        fw.flush();
        fw.close();
        
        WorkflowFailedParserImpl parser = new WorkflowFailedParserImpl();
        parser.setPath(testFolder.getAbsolutePath());
        assertTrue(parser.exists() == true);
        assertTrue(parser.getDetailedError().equals("detailed error"));
        assertTrue(parser.getError().equals("simple error"));
    }

    @Test
    public void testWherePathIsFailedFile() throws Exception {
        File testFolder = Folder.newFolder();
        String failedFile = testFolder.getAbsolutePath()+File.separator+Constants.WORKFLOW_FAILED_FILE;
        FileWriter fw = new FileWriter(failedFile);
        fw.write(Constants.SIMPLE_ERROR_MESSAGE_KEY+"= simple error\n");
        fw.write(Constants.DETAILED_ERROR_MESSAGE_KEY+"= detailed error\n");
        fw.flush();
        fw.close();
        
        WorkflowFailedParserImpl parser = new WorkflowFailedParserImpl();
        parser.setPath(failedFile);
        assertTrue(parser.exists() == true);
        assertTrue(parser.getDetailedError().equals("detailed error"));
        assertTrue(parser.getError().equals("simple error"));
    }
    
    @Test
    public void testWherePathIsFailedFileButDoesNotHaveAnyData() throws Exception {
        File testFolder = Folder.newFolder();
        String failedFile = testFolder.getAbsolutePath()+File.separator+Constants.WORKFLOW_FAILED_FILE;
        FileWriter fw = new FileWriter(failedFile);
        fw.write("\n");
        fw.flush();
        fw.close();
        
        WorkflowFailedParserImpl parser = new WorkflowFailedParserImpl();
        parser.setPath(failedFile);
        assertTrue(parser.exists() == true);
        assertTrue(parser.getDetailedError() == null);
        assertTrue(parser.getError() == null);
    }
    
    @Test
    public void testWherePathIsFailedFileAndHasDuplicateSimpleError() throws Exception {
        File testFolder = Folder.newFolder();
        String failedFile = testFolder.getAbsolutePath()+File.separator+Constants.WORKFLOW_FAILED_FILE;
        FileWriter fw = new FileWriter(failedFile);
        fw.write(Constants.SIMPLE_ERROR_MESSAGE_KEY+"= simple error\n");
        fw.write(Constants.SIMPLE_ERROR_MESSAGE_KEY+"=error2\n");
        fw.flush();
        fw.close();
        
        WorkflowFailedParserImpl parser = new WorkflowFailedParserImpl();
        parser.setPath(failedFile);
        assertTrue(parser.exists() == true);
        assertTrue(parser.getDetailedError() == null);
        assertTrue(parser.getError(),parser.getError().equals("error2"));
    }

}