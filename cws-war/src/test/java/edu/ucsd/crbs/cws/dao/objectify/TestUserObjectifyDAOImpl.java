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

package edu.ucsd.crbs.cws.dao.objectify;

import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import edu.ucsd.crbs.cws.auth.User;
import static edu.ucsd.crbs.cws.dao.objectify.OfyService.ofy;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;



/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestUserObjectifyDAOImpl {

    private final LocalServiceTestHelper _helper
            = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(),
                    new LocalBlobstoreServiceTestConfig());
    
    public TestUserObjectifyDAOImpl() {
    }

    @BeforeClass
    public static void setUpClass() {
        Logger.getLogger(UserObjectifyDAOImpl.class.getName()).setLevel(Level.OFF);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        _helper.setUp();
        ofy().clear();
    }
    
    @After
    public void tearDown() {
        _helper.tearDown();
    }

    @Test
    public void testInsertNullUser() throws Exception {
        UserObjectifyDAOImpl userDAO = new UserObjectifyDAOImpl();
        try {
            userDAO.insert(null);
            fail("Expected exception");
        }
        catch(NullPointerException npe){
            assertTrue(npe.getMessage().equals("User is null"));
        }
        
    }

    //test insert create date is already set
    @Test
    public void testInsertCreateDateSet() throws Exception {
        UserObjectifyDAOImpl userDAO = new UserObjectifyDAOImpl();
        User u = new User();
        u.setLogin("bob");
        Date cDate = new Date();
        u.setCreateDate(cDate);
        u = userDAO.insert(u);
        assertTrue(u.getId() != null);
        assertTrue(u.getCreateDate().getTime() == cDate.getTime());
    }
    
    //test insert create date is null
    @Test
    public void testInsertCreateDateNotSet() throws Exception {
        UserObjectifyDAOImpl userDAO = new UserObjectifyDAOImpl();
        User u = new User();
        u.setLogin("bob");
        u = userDAO.insert(u);
        assertTrue(u.getId() != null);
        assertTrue(u.getCreateDate().getTime() > 0);
    }
    
    @Test public void testGetUserByLoginAndToken() throws Exception {
        UserObjectifyDAOImpl userDAO = new UserObjectifyDAOImpl();

        //does not exist
        assertTrue(userDAO.getUserByLoginAndToken("bob","token") == null);

        User u = new User();
        u.setLogin("bob");
        u.setToken("token");
        
        u = userDAO.insert(u);
        //both null
        assertTrue(userDAO.getUserByLoginAndToken(null, null) == null);
        //token null
        assertTrue(userDAO.getUserByLoginAndToken("bob", null) == null);
        //login null
        assertTrue(userDAO.getUserByLoginAndToken(null, "token") == null);
        
        //valid
        assertTrue(userDAO.getUserByLoginAndToken("bob", 
                "token").getId() == u.getId().longValue());
        
        User u2 = new User();
        u2.setDeleted(true);
        u2.setLogin("joe");
        u2.setToken("token");
        u2 = userDAO.insert(u2);
        
        assertTrue(userDAO.getUserByLoginAndToken("joe", "token") == null);
        u2.setDeleted(false);
        u2 = userDAO.insert(u2);
        assertTrue(userDAO.getUserByLoginAndToken("joe", 
                "token").getId() == u2.getId().longValue());
    } 
    
    @Test
    public void testGetUserByIdNullArg() throws Exception {
        UserObjectifyDAOImpl userDAO = new UserObjectifyDAOImpl();
        try {
            userDAO.getUserById(null);
            fail("Expected Exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().contains("NumberFormatException"));
        }
    }
    
    //test getUserById invalid id (not convertable to long)
    @Test
    public void testGetUserByIdInvalidArg() throws Exception {
        UserObjectifyDAOImpl userDAO = new UserObjectifyDAOImpl();
        try {
            userDAO.getUserById("foo");
            fail("Expected Exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().contains("NumberFormatException"));
        }
    }

    
    //test getUserById non existant user
        @Test
    public void testGetUserByIdNonexistantAndValidUser() throws Exception {
        UserObjectifyDAOImpl userDAO = new UserObjectifyDAOImpl();
        assertTrue(userDAO.getUserById("1") == null);
        User u = new User();
        u = userDAO.insert(u);
        assertTrue(userDAO.getUserById(Long.toString(u.getId()+1)) == null);
        assertTrue(userDAO.getUserById(u.getId().toString()).getId() ==
                u.getId().longValue());
    }
    
}