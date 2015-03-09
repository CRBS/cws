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

import com.google.appengine.api.blobstore.BlobstoreService;
import edu.ucsd.crbs.cws.auth.Authenticator;
import edu.ucsd.crbs.cws.auth.AuthenticatorImpl;
import edu.ucsd.crbs.cws.auth.Permission;
import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.dao.WorkspaceFileDAO;
import edu.ucsd.crbs.cws.dao.objectify.WorkspaceFileObjectifyDAOImpl;
import edu.ucsd.crbs.cws.gae.BlobStoreServiceUtil;
import edu.ucsd.crbs.cws.rest.Constants;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class WorkspaceFileServlet extends HttpServlet  {

    
    private static final Logger _log = Logger.getLogger(WorkspaceFileServlet.class.getName());
    
    Authenticator _authenticator = new AuthenticatorImpl();
    
    WorkspaceFileDAO _workspaceFileDAO = new WorkspaceFileObjectifyDAOImpl();
    
    Downloader _downloader = new WorkspaceFileDownloader();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        if (req.getParameter(Constants.WSFID_PARAM) == null){
            _log.warning(Constants.WSFID_PARAM+ " query parameter not set.  No workspacefile id found");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                           Constants.WSFID_PARAM+ 
                           " query parameter not set.  No workspacefile id found");
            return;
        }
        try {
            User user = _authenticator.authenticate(req);
            if (!user.isAuthorizedTo(Permission.DOWNLOAD_ALL_WORKSPACEFILES)) {
                _log.warning("Not authorized");
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not authorized");
                return;
            }

            String id = req.getParameter(Constants.WSFID_PARAM);
            if (id.trim().isEmpty()) {
                _log.warning("WorkspaceFile Id passed in via " + Constants.WSFID_PARAM
                        + " query parameter is empty");
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "WorkspaceFile Id passed in via " + Constants.WSFID_PARAM + " query parameter is empty");
                return;
            }

            _log.log(Level.INFO, "Request to download workspacefile with id: {0}", id);
            _downloader.send(id, resp);
            
        } catch (Exception ex) {
            _log.log(Level.SEVERE, "Unable to load workspacefile", ex);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retreiving workspacefile: " + ex.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         // @TODO CHECK IF WE NEED TO VERIFY A VALID USER HERE.  REASON I DONT DO
        // IT NOW IS THE BLOBSTORE WON'T BE SET WITH ANYTHING IN ANY OTHER CASE
        // EXCEPT WHEN REDIRECTED FROM GAE. OR WILL IT?
        
        
        // @TODO refactor this to a upload handler interface so its easy to test this
        //       method
        BlobstoreService blobstoreService = BlobStoreServiceUtil.getBlobstoreService();
        
        try {
            WorkspaceFile w = BlobStoreServiceUtil.getWorkspaceFileWithBlobKeyFromMapOfBlobKeyLists(blobstoreService.getUploads(req));
            _workspaceFileDAO.updateBlobKey(w.getId(), w.getBlobKey());
        } catch (Exception ex) {
            _log.log(Level.SEVERE, "Caught exception " + ex.getMessage(), ex);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            "Caught Exception: " + ex.getMessage());
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
    }

    

}
