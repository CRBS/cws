/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ucsd.crbs.cws.auth;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public interface Authenticator {
    
    /**
     * Authenticates web request
     * @param request Web request to authenticate
     * @return Authenticated User object
     */
    public User authenticate(HttpServletRequest request,final String userLogin,
            final String userToken,final String loginToRunAs) throws Exception;
    
    public User authenticate(HttpServletRequest request) throws Exception;
}
