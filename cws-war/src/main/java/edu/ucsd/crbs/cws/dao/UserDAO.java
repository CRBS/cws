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

package edu.ucsd.crbs.cws.dao;

import edu.ucsd.crbs.cws.auth.User;
import java.util.List;

/**
 * Defines methods to retrieve and persist User objects to some persistent store
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public interface UserDAO {
    
    
    /**
     * Gets the User object by login and token.  If multiple User objects match
     * a User object is picked in an indeterminate manner
     * @param login
     * @param token
     * @return
     * @throws Exception 
     */
    public User getUserByLoginAndToken(final String login,
            final String token) throws Exception;
    
    /**
     * Adds a new User
     * @param u
     * @return
     * @throws Exception 
     */
    public User insert(User u) throws Exception;
    
    
    /**
     * Gets User by id passed in
     * @param userId
     * @return
     * @throws Exception 
     */
    public User getUserById(final String userId) throws Exception;
    
    /**
     * Gets list of {@link User}s.
     * @param login If <b>not</b> <code>null</code> only {@link User} with
     * {@link User#getLogin()} matching this value 
     * (supports comma separated list) will be returned.
     * @param showDeleted If <b>not</b> <code>null</code> and set to 
     * <code>true</b> then {@link User} objects with {@link User#isDeleted()} set
     * to <code>true</code> will also be returned.
     * @return List of {@link User} objects or empty list or null if none found
     * @throws Exception 
     */
    public List<User> getUsers(final String login,
            final Boolean showDeleted) throws Exception;
    
    
    /**
     * Updates {@link User} with <b>u</b>.  Note: {@link User#getId()} must
     * be set.
     * @param u 
     * @return Updated {@link User}
     * @throws Exception 
     */
    public User update(User u) throws Exception;
    
    /**
     * Resaves {@link User} with {@link User#getId} matching <b>userId</b> 
     * @param userId
     * @return
     * @throws Exception 
     */
    public User resave(long userId) throws Exception;
}
