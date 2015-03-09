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

package edu.ucsd.crbs.cws.auth;

import java.util.ArrayList;
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
public class TestUser {

    public TestUser() {
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
        User user = new User();
        assertTrue(user.getAsQueryParameters() == null);
        assertTrue(user.getIpAddress() == null);
        assertTrue(user.getLogin() == null);
        assertTrue(user.getLoginToRunJobAs() == null);
        assertTrue(user.getToken() == null);
        assertTrue(user.getCreateDate() == null);
        assertTrue(user.getId() == null);
        assertTrue(user.getPermissions() == 0);
        assertTrue(user.getAllowedIpAddresses() == null);
        assertTrue(user.isDeleted() == false);
        Date aDate = new Date();
        user.setCreateDate(aDate);
        user.setId(1L);
        user.setIpAddress("ip");
        user.setLogin("login");
        user.setLoginToRunJobAs("runas");
        user.setPermissions(Permission.CREATE_JOB);
        user.setToken("token");
        user.setDeleted(true);
        user.setAllowedIpAddresses(new ArrayList<String>());
        assertTrue(user.isDeleted() == true);
        assertTrue(user.getAllowedIpAddresses().isEmpty() == true);
        
        assertTrue(user.getIpAddress().equals("ip"));
        assertTrue(user.getLogin().equals("login"));
        assertTrue(user.getLoginToRunJobAs().equals("runas"));
        assertTrue(user.getToken().equals("token"));
        assertTrue(user.getCreateDate().equals(aDate));
        assertTrue(user.getId() == 1L);
        assertTrue(user.getPermissions() == Permission.CREATE_JOB);
        
    }
    
    
    @Test
    public void testGetAsQueryParameters(){
    
        User user = new User();
        assertTrue(user.getAsQueryParameters() == null);
        
        user.setLogin("login");
        assertTrue(user.getAsQueryParameters() == null);
        
        user.setLogin(null);
        user.setToken("token");
        assertTrue(user.getAsQueryParameters() == null);
        
        user.setLogin("login");
        assertTrue(user.getAsQueryParameters().equals("userlogin=login&usertoken=token"));
    }
    
    @Test
    public void testIsAuthorizedTo(){
        User user = new User();
        assertTrue(user.isAuthorizedTo(Permission.ALL) == false);
        assertTrue(user.isAuthorizedTo(Permission.CREATE_JOB) == false);
        assertTrue(user.isAuthorizedTo(Permission.CREATE_WORKFLOW) == false);
        assertTrue(user.isAuthorizedTo(Permission.CREATE_WORKSPACEFILE) == false);
        assertTrue(user.isAuthorizedTo(Permission.DOWNLOAD_ALL_WORKFLOWS) == false);
        assertTrue(user.isAuthorizedTo(Permission.DOWNLOAD_ALL_WORKSPACEFILES) == false);
        assertTrue(user.isAuthorizedTo(Permission.DOWNLOAD_THEIR_WORKFLOWS) == false);
        assertTrue(user.isAuthorizedTo(Permission.LIST_ALL_JOBS) == false);
        assertTrue(user.isAuthorizedTo(Permission.LIST_ALL_WORKFLOWS) == false);
        assertTrue(user.isAuthorizedTo(Permission.LIST_ALL_WORKSPACEFILES) == false);
        assertTrue(user.isAuthorizedTo(Permission.LIST_THEIR_JOBS) == false);
        assertTrue(user.isAuthorizedTo(Permission.LIST_THEIR_WORKSPACEFILES) == false);
        assertTrue(user.isAuthorizedTo(Permission.NONE) == true);
        assertTrue(user.isAuthorizedTo(Permission.RUN_AS_ANOTHER_USER) == false);
        assertTrue(user.isAuthorizedTo(Permission.UPDATE_ALL_JOBS) == false);
        assertTrue(user.isAuthorizedTo(Permission.UPDATE_ALL_WORKFLOWS) == false);
        assertTrue(user.isAuthorizedTo(Permission.UPDATE_ALL_WORKSPACEFILES) == false);
        assertTrue(user.isAuthorizedTo(Permission.UPDATE_THEIR_JOBS) == false);
        
        user.setPermissions(Permission.CREATE_JOB);
        assertTrue(user.isAuthorizedTo(Permission.CREATE_JOB) == true);
        assertTrue(user.isAuthorizedTo(Permission.CREATE_WORKFLOW) == false);
        assertTrue(user.isAuthorizedTo(Permission.CREATE_WORKSPACEFILE) == false);
        
        user.setPermissions(Permission.CREATE_WORKFLOW | user.getPermissions());
        assertTrue(user.isAuthorizedTo(Permission.CREATE_JOB) == true);
        assertTrue(user.isAuthorizedTo(Permission.CREATE_WORKFLOW) == true);
        assertTrue(user.isAuthorizedTo(Permission.CREATE_WORKSPACEFILE) == false);
        
        user.setPermissions(Permission.NONE);
        assertTrue(user.isAuthorizedTo(Permission.CREATE_JOB) == false);
        assertTrue(user.isAuthorizedTo(Permission.CREATE_WORKFLOW) == false);
        assertTrue(user.isAuthorizedTo(Permission.CREATE_WORKSPACEFILE) == false);
        
        user.setPermissions(Permission.ALL);
        assertTrue(user.isAuthorizedTo(Permission.CREATE_JOB) == true);
        assertTrue(user.isAuthorizedTo(Permission.CREATE_WORKFLOW) == true);
        assertTrue(user.isAuthorizedTo(Permission.CREATE_WORKSPACEFILE) == true);
        assertTrue(user.isAuthorizedTo(Permission.DOWNLOAD_ALL_WORKFLOWS) == true);
        assertTrue(user.isAuthorizedTo(Permission.DOWNLOAD_ALL_WORKSPACEFILES) == true);
        assertTrue(user.isAuthorizedTo(Permission.DOWNLOAD_THEIR_WORKFLOWS) == true);
        assertTrue(user.isAuthorizedTo(Permission.LIST_ALL_JOBS) == true);
        assertTrue(user.isAuthorizedTo(Permission.LIST_ALL_WORKFLOWS) == true);
        assertTrue(user.isAuthorizedTo(Permission.LIST_ALL_WORKSPACEFILES) == true);
        assertTrue(user.isAuthorizedTo(Permission.LIST_THEIR_JOBS) == true);
        assertTrue(user.isAuthorizedTo(Permission.LIST_THEIR_WORKSPACEFILES) == true);
        assertTrue(user.isAuthorizedTo(Permission.NONE) == true);
        assertTrue(user.isAuthorizedTo(Permission.RUN_AS_ANOTHER_USER) == true);
        assertTrue(user.isAuthorizedTo(Permission.UPDATE_ALL_JOBS) == true);
        assertTrue(user.isAuthorizedTo(Permission.UPDATE_ALL_WORKFLOWS) == true);
        assertTrue(user.isAuthorizedTo(Permission.UPDATE_ALL_WORKSPACEFILES) == true);
        assertTrue(user.isAuthorizedTo(Permission.UPDATE_THEIR_JOBS) == true);
        
        
      
        
    }
    

}