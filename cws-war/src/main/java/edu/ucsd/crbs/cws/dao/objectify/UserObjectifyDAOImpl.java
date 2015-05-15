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

package edu.ucsd.crbs.cws.dao.objectify;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;
import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.dao.UserDAO;
import static edu.ucsd.crbs.cws.dao.objectify.OfyService.ofy;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Provides methods to store and retreive {@link User} objects from 
 * GAO datastore
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class UserObjectifyDAOImpl implements UserDAO {

    private static final Logger _log = Logger.getLogger(UserObjectifyDAOImpl.class.getName());
    
    /**
     * Inserts a new {@link User} passed in via <b>u</b> parameter into the
     * data store.  During successful insert {@link User#getId()} will be set
     * and if {@link User#getCreateDate()} is null in <b>u</b> then 
     * {@link User#getCreateDate()} will be set with current time.
     * @param u {@link User} to add to data store
     * @return {@link User}  
     * @throws Exception if <b>u</b> is null or there is an error storing
     * the <b>u</b> into the datastore
     */
    @Override
    public User insert(User u) throws Exception {
        if (u == null){
            throw new NullPointerException("User is null");
        }
        
        if (u.getCreateDate() == null){
            u.setCreateDate(new Date());
        }
        Key<User> uKey = ofy().save().entity(u).now();
        return u;
    }

    /**
     * Gets {@link User} matching <b>login</b> 
     * <b>token</b> parameters.  If multiple 
     * {@link User} objects are found the first one found is returned.
     * @param login value to match {@link User#getLogin()} 
     * @param token value to match {@link User#getToken()}
     * @return <code>null</code> if either parameter <b>login</b>,<b>token</b> 
     * is <b>null</b> or if no {@link User} matches otherwise {@link User}
     * object
     * @throws Exception If there is an error querying the data store
     */
    @Override
    public User getUserByLoginAndToken(final String login,
            final String token) throws Exception {
        
        if (login == null || token == null){
            _log.warning("Login and or Token are null");
            return null;
        }
        
        Query<User> q = ofy().load().type(User.class);
        
        q = q.filter("_login ==",login);
        q = q.filter("_token ==",token);
        
        //hide deleted users
        q = q.filter("_deleted",false);
        
        List<User> user = q.limit(1).list();
        if (user != null && user.isEmpty() == false){
            return user.get(0);
        }
        return null;
    }

    @Override
    public User getUserById(String userId) throws Exception {
        long userIdAsLong;
        try {
            userIdAsLong = Long.parseLong(userId);
        }catch(NumberFormatException nfe){
            throw new Exception(nfe);
        }
        return ofy().load().type(User.class).id(userIdAsLong).now();
    }

    
    
}
