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
package edu.ucsd.crbs.cws.io;

import edu.ucsd.crbs.cws.rest.Constants;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestWorkflowFailedWriterImpl {

    @Rule
    public TemporaryFolder Folder = new TemporaryFolder();

    public TestWorkflowFailedWriterImpl() {
    }

    @BeforeClass
    public static void setUpClass() {
        Logger.getLogger(WorkflowFailedWriterImpl.class.getName()).setLevel(Level.OFF);
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
    public void testSetPathNullArgument() throws Exception {
        WorkflowFailedWriterImpl wfwi = new WorkflowFailedWriterImpl();
        try {
            wfwi.setPath(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            assertTrue(iae.getMessage().equals("Path cannot be null"));
        }
    }

    @Test
    public void testSetPathToNonDirectory() throws Exception {
        WorkflowFailedWriterImpl wfwi = new WorkflowFailedWriterImpl();
        try {
            wfwi.setPath("/asdf/doesnotexist");
            fail("Expected Exception");
        } catch (Exception iae) {
            assertTrue(iae.getMessage().equals("Path /asdf/doesnotexist must be a directory"));
        }
    }

    @Test
    public void testSetPathSuccessful() throws Exception {
        File testFolder = Folder.newFolder();
        WorkflowFailedWriterImpl wfwi = new WorkflowFailedWriterImpl();
        try {
            wfwi.setPath(testFolder.getAbsolutePath());
        } catch (Exception ex) {
            fail("Caught unexpected exception: " + ex.getMessage());
        }
    }

    @Test
    public void testWriteWithUnsetPath() throws Exception {
        File testFolder = Folder.newFolder();
        WorkflowFailedWriterImpl wfwi = new WorkflowFailedWriterImpl();
        try {
            wfwi.write("error", "detailederror");
            fail("Expected exception");
        } catch (NullPointerException npe) {
            assertTrue(npe.getMessage().equals("Path not set, must call setPath first"));
        }
    }

    @Test
    public void testWriteWithNullErrorAndNullDetailedError() throws Exception {
        File testFolder = Folder.newFolder();
        WorkflowFailedWriterImpl wfwi = new WorkflowFailedWriterImpl();
        wfwi.setPath(testFolder.getAbsolutePath());

        try {
            wfwi.write(null, null);
            fail("Expected exception");
        } catch (IllegalArgumentException iae) {
            assertTrue(iae.getMessage().equals("error cannot be null"));
        }
    }

    @Test
    public void testWriteWithNullError() throws Exception {
        File testFolder = Folder.newFolder();
        WorkflowFailedWriterImpl wfwi = new WorkflowFailedWriterImpl();
        wfwi.setPath(testFolder.getAbsolutePath());

        try {
            wfwi.write(null, "detailederror");
            fail("Expected exception");
        } catch (IllegalArgumentException iae) {
            assertTrue(iae.getMessage().equals("error cannot be null"));
        }
    }

    @Test
    public void testWriteWithNullDetailedError() throws Exception {
        File testFolder = Folder.newFolder();
        WorkflowFailedWriterImpl wfwi = new WorkflowFailedWriterImpl();
        wfwi.setPath(testFolder.getAbsolutePath());

        try {
            wfwi.write("error", null);
            fail("Expected exception");
        } catch (IllegalArgumentException iae) {
            assertTrue(iae.getMessage().equals("detailedError cannot be null"));
        }
    }

    @Test
    public void testWriteSuccessful() throws Exception {
        File testFolder = Folder.newFolder();
        WorkflowFailedWriterImpl wfwi = new WorkflowFailedWriterImpl();
        wfwi.setPath(testFolder.getAbsolutePath());
        wfwi.write("error", "detailederror");

        Properties props = new Properties();
        props.load(new FileReader(testFolder.getAbsolutePath() + File.separator + Constants.WORKFLOW_FAILED_FILE));
        assertTrue(props.getProperty(Constants.SIMPLE_ERROR_MESSAGE_KEY).equals("error"));
        assertTrue(props.getProperty(Constants.DETAILED_ERROR_MESSAGE_KEY).equals("detailederror"));
    }

    @Test
    public void testWriteMultipleTimesOnSamePath() throws Exception {
        File testFolder = Folder.newFolder();
        WorkflowFailedWriterImpl wfwi = new WorkflowFailedWriterImpl();
        wfwi.setPath(testFolder.getAbsolutePath());
        wfwi.write("error", "detailederror");

        Properties props = new Properties();
        props.load(new FileReader(testFolder.getAbsolutePath() + File.separator + Constants.WORKFLOW_FAILED_FILE));
        assertTrue(props.getProperty(Constants.SIMPLE_ERROR_MESSAGE_KEY).equals("error"));
        assertTrue(props.getProperty(Constants.DETAILED_ERROR_MESSAGE_KEY).equals("detailederror"));

        wfwi.write("hi", "there");
        props.load(new FileReader(testFolder.getAbsolutePath() + File.separator + Constants.WORKFLOW_FAILED_FILE));
        assertTrue(props.getProperty(Constants.SIMPLE_ERROR_MESSAGE_KEY).equals("hi"));
        assertTrue(props.getProperty(Constants.DETAILED_ERROR_MESSAGE_KEY).equals("there"));
    }

    public void testWriteMultipleTimesWithDifferentPaths() throws Exception{
        File testFolder = Folder.newFolder();
        WorkflowFailedWriterImpl wfwi = new WorkflowFailedWriterImpl();
        wfwi.setPath(testFolder.getAbsolutePath());
        wfwi.write("error", "detailederror");

        Properties props = new Properties();
        props.load(new FileReader(testFolder.getAbsolutePath() + File.separator + Constants.WORKFLOW_FAILED_FILE));
        assertTrue(props.getProperty(Constants.SIMPLE_ERROR_MESSAGE_KEY).equals("error"));
        assertTrue(props.getProperty(Constants.DETAILED_ERROR_MESSAGE_KEY).equals("detailederror"));

        testFolder = Folder.newFolder();
        wfwi.setPath(testFolder.getAbsolutePath());
        wfwi.write("hi", "there");
        props.load(new FileReader(testFolder.getAbsolutePath() + File.separator + Constants.WORKFLOW_FAILED_FILE));
        assertTrue(props.getProperty(Constants.SIMPLE_ERROR_MESSAGE_KEY).equals("hi"));
        assertTrue(props.getProperty(Constants.DETAILED_ERROR_MESSAGE_KEY).equals("there"));
    }
}
