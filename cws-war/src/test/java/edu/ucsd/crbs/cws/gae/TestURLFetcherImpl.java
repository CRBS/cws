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
package edu.ucsd.crbs.cws.gae;

import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.workflow.WorkflowParameter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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
public class TestURLFetcherImpl {

    @Rule
    public TemporaryFolder _folder = new TemporaryFolder();

    public TestURLFetcherImpl() {
    }

    @BeforeClass
    public static void setUpClass() {
        Logger.getLogger(URLFetcherImpl.class.getName()).setLevel(Level.OFF);
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
     * Test passing null parameter
     */
    @Test
    public void testFetchAndUpdateWithNullParameter() throws Exception {
        URLFetcherImpl fetcher = new URLFetcherImpl();
        fetcher.fetchAndUpdate(null, null);
    }

    /**
     * Test where parameter type is null
     */
    @Test
    public void testFetchAndUpdateWhereParameterTypeIsNull() throws Exception {
        URLFetcherImpl fetcher = new URLFetcherImpl();
        WorkflowParameter wp = new WorkflowParameter();
        try {
            fetcher.fetchAndUpdate(wp, null);
            fail("Expected exception");
        } catch (Exception ex) {
            assertTrue(ex.getMessage().startsWith("Parameter's type is null"));
        }
    }

    /**
     * Test passing parameter other then drop down
     */
    @Test
    public void testFetchAndUpdateWithNonDropdownParameter() throws Exception {
        URLFetcherImpl fetcher = new URLFetcherImpl();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setType(WorkflowParameter.Type.TEXT);
        fetcher.fetchAndUpdate(wp, null);
    }

    /**
     * test where user is null
     */
    @Test
    public void testFetchAndUpdateWithNullUser() throws Exception {
        URLFetcherImpl fetcher = new URLFetcherImpl();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setType(WorkflowParameter.Type.DROP_DOWN);
        fetcher.fetchAndUpdate(wp, null);
    }

    /**
     * test with http url, but request fails cause its not found
     */
    @Test
    public void testFetchAndUpdateWithInvalidURL() throws Exception {
        URLFetcherImpl fetcher = new URLFetcherImpl();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setType(WorkflowParameter.Type.DROP_DOWN);
        wp.setValue("http://www.crbs.ucsd.edu/doesnotexisteverevervever");
        wp.setNameValueDelimiter("==");
        User user = new User();
        user.setLogin("bob");

        try {
            fetcher.fetchAndUpdate(wp, user);
            fail("Expected exception");
        } catch (FileNotFoundException ex) {
            assertTrue(ex.getMessage().startsWith("http://"));
        }

    }

    /**
     * test with https url, but request fails
     */
    @Test
    public void testFetchAndUpdateWithInvalidHTTPSURL() throws Exception {
        URLFetcherImpl fetcher = new URLFetcherImpl();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setType(WorkflowParameter.Type.DROP_DOWN);
        wp.setValue("https://www.google.com/doesnotexisteverevervever");
        wp.setNameValueDelimiter("==");
        User user = new User();
        user.setLogin("bob");

        try {
            fetcher.fetchAndUpdate(wp, user);
            fail("Expected exception");
        } catch (FileNotFoundException ex) {
            assertTrue(ex.getMessage().startsWith("https://"));
        }

    }

    /**
     * test with file url and request succeeds no results returned
     */
    @Test
    public void testSuccessfulFetchWithNoData() throws Exception {

        File emptyFile = _folder.newFile();
        
        URLFetcherImpl fetcher = new URLFetcherImpl();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setType(WorkflowParameter.Type.DROP_DOWN);
        wp.setValue("file://" + emptyFile.getAbsolutePath());
        wp.setNameValueDelimiter("==");
        User user = new User();
        user.setLogin("bob");

        fetcher.fetchAndUpdate(wp, user);
        assertTrue(wp.getValueMap() != null);
    }

    /**
     * test with file url and request succeeds with no results, but url has userlogin token
     * and user keywords that need to be replaced
     */
    @Test
    public void testSuccessfulFetchWithNoDataAndTokenReplacementInURL() throws Exception {

        File emptyFile = _folder.newFile("bob.btoken.ouser");
        
        URLFetcherImpl fetcher = new URLFetcherImpl();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setType(WorkflowParameter.Type.DROP_DOWN);
        wp.setValue("file://" + emptyFile.getParentFile().getAbsolutePath()+
                File.separator+"@@userlogin@@.@@usertoken@@.@@user@@");
        wp.setNameValueDelimiter("==");
        User user = new User();
        user.setLogin("bob");
        user.setToken("btoken");
        user.setLoginToRunJobAs("ouser");

        fetcher.fetchAndUpdate(wp, user);
        assertTrue(wp.getValueMap() != null);
    }
    
    /**
     * test with url where tokens need replacement except loginasuser is null so
     * code falls back to {@link User#getLogin()}
     * @throws Exception 
     */
    @Test
    public void testSuccessfulFetchWithNoDataAndLoginUserTokenIsNull() throws Exception {

        File emptyFile = _folder.newFile("bob.btoken.bob");
        
        URLFetcherImpl fetcher = new URLFetcherImpl();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setType(WorkflowParameter.Type.DROP_DOWN);
        wp.setValue("file://" + emptyFile.getParentFile().getAbsolutePath()+
                File.separator+"@@userlogin@@.@@usertoken@@.@@user@@");
        wp.setNameValueDelimiter("==");
        User user = new User();
        user.setLogin("bob");
        user.setToken("btoken");

        fetcher.fetchAndUpdate(wp, user);
        assertTrue(wp.getValueMap() != null);
    }
    
    /**
     * test with file url and request succeeds no namevaluedelimiter set
     */
    @Test
    public void testSuccessfulFetchWithDataAndNoDelimiter() throws Exception {

        File dataFile = _folder.newFile();
        FileWriter fw = new FileWriter(dataFile);
        fw.write("line1\nline2\nline3");
        fw.flush();
        fw.close();

        URLFetcherImpl fetcher = new URLFetcherImpl();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setType(WorkflowParameter.Type.DROP_DOWN);
        wp.setValue("file://" + dataFile.getAbsolutePath());
        User user = new User();
        user.setLogin("bob");

        fetcher.fetchAndUpdate(wp, user);
        assertTrue(wp.getValueMap() != null);
        assertTrue(wp.getValueMap().size() == 3);
        assertTrue(wp.getValueMap().get("line1").equals("line1"));
        assertTrue(wp.getValueMap().get("line2").equals("line2"));
        assertTrue(wp.getValueMap().get("line3").equals("line3"));

    }

    /**
     * test with file url and request succeeds and namevaluedelimiter set
     */
    @Test
    public void testSuccessfulFetchWithDataAndDelimiter() throws Exception {

        File dataFile = _folder.newFile();
        FileWriter fw = new FileWriter(dataFile);
        fw.write("line1=1\nline2=2\nline3=3\nline4");
        fw.flush();
        fw.close();

        URLFetcherImpl fetcher = new URLFetcherImpl();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setType(WorkflowParameter.Type.DROP_DOWN);
        wp.setNameValueDelimiter("=");
        wp.setValue("file://" + dataFile.getAbsolutePath());
        User user = new User();
        user.setLogin("bob");

        fetcher.fetchAndUpdate(wp, user);
        assertTrue(wp.getValueMap() != null);
        assertTrue(wp.getValueMap().size() == 4);
        assertTrue(wp.getValueMap().get("line1").equals("1"));
        assertTrue(wp.getValueMap().get("line2").equals("2"));
        assertTrue(wp.getValueMap().get("line3").equals("3"));
        assertTrue(wp.getValueMap().get("line4").equals("line4"));
    }
    
    /**
     * test with file url and duplicate data
     */
    @Test
    public void testSuccessfulFetchWithDuplicateData() throws Exception {

        File dataFile = _folder.newFile();
        FileWriter fw = new FileWriter(dataFile);
        fw.write("line1=1\nline1=1\nline3=3\n");
        fw.flush();
        fw.close();

        URLFetcherImpl fetcher = new URLFetcherImpl();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setType(WorkflowParameter.Type.DROP_DOWN);
        wp.setNameValueDelimiter("=");
        wp.setValue("file://" + dataFile.getAbsolutePath());
        User user = new User();
        user.setLogin("bob");

        fetcher.fetchAndUpdate(wp, user);
        assertTrue(wp.getValueMap() != null);
        assertTrue(wp.getValueMap().size() == 2);
        assertTrue(wp.getValueMap().get("line1").equals("1"));
        assertTrue(wp.getValueMap().get("line3").equals("3"));
    }
    
    
    /**
     * test with data that is empty
     */
    @Test
    public void testSuccessfulFetchWithValueSetToEmptyData() throws Exception {

    
        URLFetcherImpl fetcher = new URLFetcherImpl();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setType(WorkflowParameter.Type.DROP_DOWN);
        wp.setNameValueDelimiter("=");
        wp.setValue("");
        User user = new User();
        user.setLogin("bob");
        fetcher.fetchAndUpdate(wp, user);
        assertTrue(wp.getValueMap() != null);
    }
    
    /**
     * test with data in value and no delimiter or linedelimiter
     */
    @Test
    public void testSuccessfulFetchWithValueSetToDataWithNoDelimiters() throws Exception {

    
        URLFetcherImpl fetcher = new URLFetcherImpl();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setType(WorkflowParameter.Type.DROP_DOWN);
        wp.setValue("somedatagoeshere");
        User user = new User();
        user.setLogin("bob");
        fetcher.fetchAndUpdate(wp, user);
        assertTrue(wp.getValueMap() != null);
        assertTrue(wp.getValueMap().size() == 1);
        assertTrue(wp.getValueMap().get("somedatagoeshere").equals("somedatagoeshere"));
    }
    
    /**
     * test with data in value with namevaluedelimiter set, but no linedelimiter
     */
     @Test
    public void testSuccessfulFetchWithValueSetToDataWithOnlyNameValueDelimiter() throws Exception {

    
        URLFetcherImpl fetcher = new URLFetcherImpl();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setType(WorkflowParameter.Type.DROP_DOWN);
        wp.setNameValueDelimiter("=");
        wp.setValue("a=1");
        User user = new User();
        user.setLogin("bob");
        fetcher.fetchAndUpdate(wp, user);
        assertTrue(wp.getValueMap() != null);
        assertTrue(wp.getValueMap().size() == 1);
        assertTrue(wp.getValueMap().get("a").equals("1"));
    }
    
    /**
     * test with data in value with namevaluedelimiter set, but no linedelimiter
     */
     @Test
    public void testSuccessfulFetchWithValueSetToDataWithOnlyNameValueDelimiterAndDataHasNewLines() throws Exception {

    
        URLFetcherImpl fetcher = new URLFetcherImpl();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setType(WorkflowParameter.Type.DROP_DOWN);
        wp.setNameValueDelimiter("=");
        wp.setValue("a=1\nb=2");
        User user = new User();
        user.setLogin("bob");
        fetcher.fetchAndUpdate(wp, user);
        assertTrue(wp.getValueMap() != null);
        assertTrue(wp.getValueMap().size() == 2);
        assertTrue(wp.getValueMap().get("a").equals("1"));
        assertTrue(wp.getValueMap().get("b").equals("2"));
    }
    /**
     * test with data in value with namevaluedelimiter and linedelimiter set
     */
    @Test
    public void testSuccessfulFetchWithValueSetToDataWithDelimitersSet() throws Exception {

    
        URLFetcherImpl fetcher = new URLFetcherImpl();
        WorkflowParameter wp = new WorkflowParameter();
        wp.setType(WorkflowParameter.Type.DROP_DOWN);
        wp.setNameValueDelimiter("=");
        wp.setLineDelimiter(",");
        wp.setValue("a=1,b=2,c=3");
        User user = new User();
        user.setLogin("bob");
        fetcher.fetchAndUpdate(wp, user);
        assertTrue(wp.getValueMap() != null);
        assertTrue(wp.getValueMap().size() == 3);
        assertTrue(wp.getValueMap().get("a").equals("1"));
        assertTrue(wp.getValueMap().get("b").equals("2"));
        assertTrue(wp.getValueMap().get("c").equals("3"));
    }
}
