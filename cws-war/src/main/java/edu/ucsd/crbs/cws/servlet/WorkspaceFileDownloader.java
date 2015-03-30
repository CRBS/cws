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

package edu.ucsd.crbs.cws.servlet;

import edu.ucsd.crbs.cws.dao.WorkspaceFileDAO;
import edu.ucsd.crbs.cws.dao.objectify.WorkspaceFileObjectifyDAOImpl;
import edu.ucsd.crbs.cws.gae.BlobStoreServiceUtil;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class WorkspaceFileDownloader implements Downloader {

    private static final Logger _log = Logger.getLogger(WorkspaceFileDownloader.class.getName());
    
        WorkspaceFileDAO _workspaceFileDAO = new WorkspaceFileObjectifyDAOImpl(null,null);

    
    @Override
    public void send(String id, HttpServletResponse response) throws IOException {
        WorkspaceFile w;
        
        try {
            w = _workspaceFileDAO.getWorkspaceFileById(id, null);
        }
        catch(Exception ex){
             response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error getting workspaceFile by id from data store: "+
                            ex.getMessage());
            return;
        }
        
        if (w == null) {
            _log.log(Level.SEVERE, "Workflow returned by data store is null");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, 
                    "No workspaceFile matching id found: " + id);
            return;
        }

        if (w.getBlobKey() == null) {
            _log.log(Level.SEVERE, "blob key is null");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Key to workspaceFile file not found for id: "
                    + id);
            return;
        }

        try {
            BlobStoreServiceUtil.serveBlobKeyForDownload(w.getBlobKey(), 
                    w.getName(), response);
        }
        catch(Exception ex){
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Caught exception: "+ex.getMessage());
        }
    }

}
