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
package edu.ucsd.crbs.cws.auth;

import com.google.api.client.util.Base64;
import java.nio.charset.StandardCharsets;
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
public class TestAuthStringDecoderImpl {

    public TestAuthStringDecoderImpl() {
    }

    @BeforeClass
    public static void setUpClass() {
        Logger.getLogger(AuthStringDecoderImpl.class.getName()).setLevel(Level.OFF);
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
    public void testDecodeAuthString() {
        AuthStringDecoderImpl decoder = new AuthStringDecoderImpl();
        assertTrue(decoder.decodeAuthString(null) == null);
        assertTrue(decoder.decodeAuthString("") == null);
        assertTrue(decoder.decodeAuthString(" ") == null);
        assertTrue(decoder.decodeAuthString(null) == null);
        assertTrue(decoder.decodeAuthString("Basic ") == null);
        assertTrue(decoder.decodeAuthString("basic ") == null);

        assertTrue(decoder.decodeAuthString("Basic "
                + encodeString("hellothere")) == null);

        assertTrue(decoder.decodeAuthString("Basic "
                + encodeString("hello:t:here")) == null);

        assertTrue(decoder.decodeAuthString("Basic "
                + encodeString(":foo")) == null);

        assertTrue(decoder.decodeAuthString("Basic "
                + encodeString("foo:")) == null);

        User u = decoder.decodeAuthString("Basic "
                + encodeString("login:pass"));
        assertTrue(u.getLogin().equals("login"));
        assertTrue(u.getToken().equals("pass"));
        
        u = decoder.decodeAuthString("basic "
                + encodeString("login:pass"));
        assertTrue(u.getLogin().equals("login"));
        assertTrue(u.getToken().equals("pass"));

    }

    /**
     * Helper method to encode a string in Base64
     * @param theStr
     * @return 
     */
    private String encodeString(final String theStr) {
        byte[] ha = Base64.encodeBase64(theStr.getBytes());
        return new String(ha, StandardCharsets.UTF_8);
    }
}
