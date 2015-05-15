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

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;

/**
 * Extracts user:pass from HTTP Authentication string. See {@link #decodeAuthString(java.lang.String)
 * } for more information.
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class AuthStringDecoderImpl implements AuthStringDecoder {

    private static final Logger _log = Logger.getLogger(AuthStringDecoderImpl.class.getName());

    /**
     * Takes Basic HTTP Authentication string via <b>authString</b>
     * parameter in format:
     * <p/>
     *
     * Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
     * <p/>
     *
     * and extracts login and password token from value to right of <b>Basic</b>
     * above which should be in format of <b>login:pass</b> once decoded via
     * {@link DatatypeConverter#parseBase64Binary(java.lang.String)} method
     *
     * @param auth Basic Http Authentication string in format above
     * @return {@link User} with {@link User#getLogin()} set to <b>login</b> and
     * {@link User#getToken()} set to <b>pass</b> or null if parse failed
     */
    @Override
    public User decodeAuthString(final String authString) {

        if (authString == null) {
            _log.log(Level.INFO, "Auth string is null");
            return null;
        }
        
        String authWithBasicRemoved = authString.replaceFirst("[B|b]asic ", "");
        byte[] decodedBytes = Base64.decodeBase64(authWithBasicRemoved);
        if (decodedBytes == null || decodedBytes.length == 0) {
            _log.log(Level.INFO, "Decoded byte array is null or size 0");
            return null;
        }
        String decodedUserPass = new String(decodedBytes);
        _log.log(Level.INFO, "Decoded string: {0}", decodedUserPass);
        
        if (!decodedUserPass.contains(":")) {
            _log.log(Level.INFO, "Decoded auth information does not contain a colon");

            return null;
        }
        String[] userPass = decodedUserPass.split(":");

        if (userPass.length != 2) {
            _log.log(Level.INFO, "Decoded auth has more then 1 colon");

            return null;
        }
        if (userPass[0].length() == 0 || userPass[1].length() == 0) {
            _log.log(Level.INFO, "Login or token is size 0");

            return null;
        }

        User u = new User();
        u.setLogin(userPass[0]);
        u.setToken(userPass[1]);
        return u;
    }

}
