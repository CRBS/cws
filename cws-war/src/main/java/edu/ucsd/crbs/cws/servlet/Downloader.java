/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ucsd.crbs.cws.servlet;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
 * Defines methods that classes can implement to offer ability to download Workflow via
 * Servlet
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public interface Downloader {
    
    
    /**
     * Sends workflow file via <b>response</b> to client as downloadable file
     * @param id
     * @param response
     */
    public void send(final String id,HttpServletResponse response) throws IOException;
    
}
