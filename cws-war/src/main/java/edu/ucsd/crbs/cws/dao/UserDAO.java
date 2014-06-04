/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ucsd.crbs.cws.dao;

import edu.ucsd.crbs.cws.auth.User;

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
    public User getUserByLoginAndToken(final String login,final String token) throws Exception;
    
    /**
     * Adds a new User
     * @param u
     * @return
     * @throws Exception 
     */
    public User insert(User u) throws Exception;
    
    
}
