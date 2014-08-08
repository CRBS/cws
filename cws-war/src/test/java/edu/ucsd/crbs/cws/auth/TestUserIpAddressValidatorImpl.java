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
import java.util.Arrays;
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
public class TestUserIpAddressValidatorImpl {

    public TestUserIpAddressValidatorImpl() {
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
    public void testNullUser(){
        UserIpAddressValidator validator = new UserIpAddressValidatorImpl();
        
        try {
            validator.isUserRequestFromValidIpAddress(null);
            fail("Expected exception cause we passed a null user in");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("User is null"));
        }
    }
    
    @Test
    public void testNullIpAddress(){
        UserIpAddressValidator validator = new UserIpAddressValidatorImpl();
        
        try {
            validator.isUserRequestFromValidIpAddress(new User());
            fail("Expected exception cause ip address is null");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().startsWith("No ip address found"));
        }
    }
    
    @Test
    public void testNullValidIpAddressList() throws Exception{
        UserIpAddressValidator validator = new UserIpAddressValidatorImpl();
        User u = new User();
        u.setIpAddress("192.168.0.1");
        assertTrue(validator.isUserRequestFromValidIpAddress(u) == true);
    }

    @Test
    public void testEmptyValidIpAddressList() throws Exception{
        UserIpAddressValidator validator = new UserIpAddressValidatorImpl();
        User u = new User();
        u.setIpAddress("192.168.0.1");
        u.setAllowedIpAddresses(new ArrayList<String>());
        assertTrue(validator.isUserRequestFromValidIpAddress(u) == true);
    }

    @Test
    public void testIpv4AddressMatchesOnlyAddressInList() throws Exception {
        UserIpAddressValidator validator = new UserIpAddressValidatorImpl();
        User u = new User();
        u.setIpAddress("192.168.0.1");
        u.setAllowedIpAddresses(Arrays.asList("192.168.0.1"));
        assertTrue(validator.isUserRequestFromValidIpAddress(u) == true);
    }

    @Test
    public void testIpv4AddressDoesNotMatchOnlyAddressInList() throws Exception {
        UserIpAddressValidator validator = new UserIpAddressValidatorImpl();
        User u = new User();
        u.setIpAddress("192.168.0.1");
        u.setAllowedIpAddresses(Arrays.asList("192.167.0.1"));
        assertTrue(validator.isUserRequestFromValidIpAddress(u) == false);
    }

    @Test
    public void testIpv4AddressMatchesAddressInListMixedWithIpv6() throws Exception {
        UserIpAddressValidator validator = new UserIpAddressValidatorImpl();
        User u = new User();
        u.setIpAddress("192.168.0.1");
        u.setAllowedIpAddresses(Arrays.asList("192.167.0.1","3ffe:1900:4545:3:200:f8ff:fe21:67cf","192.168.0.1","0.0.0.0"));
        assertTrue(validator.isUserRequestFromValidIpAddress(u) == true);
    }

    @Test
    public void testIpv4AddressDoesNotMatchAddressInListMixedWithIpv6() throws Exception {
        UserIpAddressValidator validator = new UserIpAddressValidatorImpl();
        User u = new User();
        u.setIpAddress("172.168.0.1");
        u.setAllowedIpAddresses(Arrays.asList("192.167.0.1","3ffe:1900:4545:3:200:f8ff:fe21:67cf","192.168.0.1","0.0.0.0"));
        assertTrue(validator.isUserRequestFromValidIpAddress(u) == false);
    }

    ///////////////////
    @Test
    public void testIpv6AddressMatchesOnlyAddressInList() throws Exception {
        UserIpAddressValidator validator = new UserIpAddressValidatorImpl();
        User u = new User();
        u.setIpAddress("3ffe:ffff:f545:3:200:f8ff:fe21:67cf");
        u.setAllowedIpAddresses(Arrays.asList("3ffe:ffff:f545:3:200:f8ff:fe21:67cf"));
        assertTrue(validator.isUserRequestFromValidIpAddress(u) == true);
    }

    @Test
    public void testIpv6AddressDoesNotMatchOnlyAddressInList() throws Exception {
        UserIpAddressValidator validator = new UserIpAddressValidatorImpl();
        User u = new User();
        u.setIpAddress("3ffe:1900:4545:3:200:f8ff:fe21:67cf");
        u.setAllowedIpAddresses(Arrays.asList("3ffe:ffff:4545:3:200:f8ff:fe21:67cf"));
        assertTrue(validator.isUserRequestFromValidIpAddress(u) == false);
    }

    @Test
    public void testIpv6AddressMatchesAddressInListMixedWithIpv4() throws Exception {
        UserIpAddressValidator validator = new UserIpAddressValidatorImpl();
        User u = new User();
        u.setIpAddress("3ffe:1900:4545:3:200:f8ff:fe21:67cf");
        u.setAllowedIpAddresses(Arrays.asList("192.167.0.1","3ffe:1900:4545:3:200:f8ff:fe21:67cf","192.168.0.1","0.0.0.0"));
        assertTrue(validator.isUserRequestFromValidIpAddress(u) == true);
    }

    @Test
    public void testIpv6AddressDoesNotMatchAddressInListMixedWithIpv4() throws Exception {
        UserIpAddressValidator validator = new UserIpAddressValidatorImpl();
        User u = new User();
        u.setIpAddress("3ffe:1900:4545:3:200:f8ff:fe21:6fcf");
        u.setAllowedIpAddresses(Arrays.asList("192.167.0.1","3ffe:1900:4545:3:200:f8ff:fe21:67cf","192.168.0.1","0.0.0.0"));
        assertTrue(validator.isUserRequestFromValidIpAddress(u) == false);
    }
    
    @Test
    public void testIpv6AddressAgainstCidrThatMatches() throws Exception {
        UserIpAddressValidator validator = new UserIpAddressValidatorImpl();
        User u = new User();
        u.setIpAddress("3ffe:1900:4545:3:200:f8ff:fe21:6fcf");
        u.setAllowedIpAddresses(Arrays.asList("3ffe:1900:4545:3:200:f8ff:fe21:6fcf/127"));
        assertTrue(validator.isUserRequestFromValidIpAddress(u) == true);
    }

    @Test
    public void testIpv6AddressAgainstCidrThatDoesNotMatch() throws Exception {
        UserIpAddressValidator validator = new UserIpAddressValidatorImpl();
        User u = new User();
        u.setIpAddress("3ffe:1900:4545:3:200:f8ff:fe21:6fcf");
        u.setAllowedIpAddresses(Arrays.asList("3ffe:1900:4545:3:200:f8ff:fe21:6f1f/128"));
        assertTrue(validator.isUserRequestFromValidIpAddress(u) == false);
    }
    
    @Test
    public void testIpv4AddressAgainstCidrThatMatches() throws Exception {
        UserIpAddressValidator validator = new UserIpAddressValidatorImpl();
        User u = new User();
        u.setIpAddress("100.10.10.10");
        u.setAllowedIpAddresses(Arrays.asList("100.10.10.0/24"));
        assertTrue(validator.isUserRequestFromValidIpAddress(u) == true);
    }

    @Test
    public void testIpv4AddressAgainstCidrThatDoesNotMatch() throws Exception {
        UserIpAddressValidator validator = new UserIpAddressValidatorImpl();
        User u = new User();
        u.setIpAddress("100.10.10.10");
        u.setAllowedIpAddresses(Arrays.asList("100.10.0.0/32"));
        assertTrue(validator.isUserRequestFromValidIpAddress(u) == false);
    }
}