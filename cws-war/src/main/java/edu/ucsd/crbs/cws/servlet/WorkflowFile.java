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
import edu.ucsd.crbs.cws.dao.WorkflowDAO;
import edu.ucsd.crbs.cws.dao.objectify.WorkflowObjectifyDAOImpl;
import edu.ucsd.crbs.cws.gae.BlobStoreServiceUtil;
import static edu.ucsd.crbs.cws.servlet.WorkflowFile.WFID;
import edu.ucsd.crbs.cws.workflow.Workflow;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that lets caller upload and download Workflow Kar files
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class WorkflowFile extends HttpServlet {

    private static final Logger _log = Logger.getLogger(WorkflowFile.class.getName());

    public static final String WFID = "wfid";

    Authenticator _authenticator = new AuthenticatorImpl();

    Downloader _workflowDownloader = new WorkflowDownloaderImpl();

    WorkflowDAO _workflowDAO = new WorkflowObjectifyDAOImpl();
    /**
     * Handles <b>GET</b> requests to <b>/workflowfile</b> servlet which lets
     * users download a specific workflow kar file. This method assumes the
     * query parameter {@link WFID} is set with a valid workflow id. If not a {@link
     * <p/>
     * The method first verifies the request is authorized to retrieve the data
     * and if not a {@link HttpServletResponse.SC_UNAUTHORIZED} is set in the
     * <b>resp</b> object.
     * <p/>
     * The method then checks the workflow id is not empty or null and if it is
     * a
     * {@link HttpServletResponse.SC_BAD_REQUEST} is set in the <b>resp</b> object.
     * <p/>
     * The method then attempts to load the workflow object from the data store
     * to get the path for the file on google cloud store. If none is found a 
     * {@link HttpServletResponse.SC_NOT_FOUND} is set in the <b>resp</b> object.
     * <p/>
     * Finally the blob key used to retrieve the workflow file from Google's
     * cloud bucket is extracted and if its null a {link
     * HttpServletResponse.SC_INTERNAL_SERVER_ERROR} is set in the <b>resp</b>
     * object.
     *
     * @param req Web request
     * @param resp Response Upon success a kar file is sent in the response
     * otherwise one of several error responses
     * @throws IOException If there was an exception generated from invocation
     * of
     * <b>resp.sendError()</b> method
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        if (req.getParameter(WFID) == null) {
            _log.warning(WFID + " query parameter not set.  No workflow id found");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, WFID
                    + " query parameter not set.  No workflow id found");
            return;
        }

        try {
            User user = _authenticator.authenticate(req);
            if (!user.isAuthorizedTo(Permission.DOWNLOAD_ALL_WORKFLOWS)) {
                _log.warning("Not authorized");
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not authorized");
                return;
            }

            String wfid = req.getParameter(WFID);
            if (wfid.trim().isEmpty()) {
                _log.warning("Workflow Id passed in via " + WFID
                        + " query parameter is empty");
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Workflow Id passed in via " + WFID + " query parameter is empty");
                return;
            }

            _log.log(Level.INFO, "Request to download workflow with id: {0}", wfid);
            _workflowDownloader.send(wfid, resp);
            
        } catch (Exception ex) {
            _log.log(Level.SEVERE, "Unable to load workflow", ex);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retreiving workflow file: " + ex.getMessage());
        }
    }

    /**
     * Handles POST request for /workflowfile servlet. It is expected that this
     * method is only invoked as a redirect from the blobstore upload service
     * which will set the list of uploaded files in the
     * {@link BlobstoreService.getUploads} method. This code will take the first
     * entry as the file uploaded. If there are extra files listed they are
     * noted via warning logs, but otherwise ignored. The code takes the blob
     * key for the first uploaded file it encounters and updates the Workflow
     * object in the data store with the blob key. The response will be set to
     * error under these conditions<p/>
     *
     * If there is an error a
     * {@link HttpServletResponse.SC_INTERNAL_SERVER_ERROR} is set on
     * <b>resp</b> object.
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        // @TODO CHECK IF WE NEED TO VERIFY A VALID USER HERE.  REASON I DONT DO
        // IT NOW IS THE BLOBSTORE WON'T BE SET WITH ANYTHING IN ANY OTHER CASE
        // EXCEPT WHEN REDIRECTED FROM GAE. OR WILL IT?
        
        
        // @TODO refactor this to a upload handler interface so its easy to test this
        //       method
        BlobstoreService blobstoreService = BlobStoreServiceUtil.getBlobstoreService();
        
        try {
            Workflow w = BlobStoreServiceUtil.getWorkflowWithBlobKeyFromMapOfBlobKeyLists(blobstoreService.getUploads(req));
            _workflowDAO.updateBlobKey(w.getId(), w.getBlobKey());
        } catch (Exception ex) {
            _log.log(Level.SEVERE, "Caught exception " + ex.getMessage(), ex);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            "Caught Exception: " + ex.getMessage());
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
    }

}
