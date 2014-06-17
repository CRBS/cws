package edu.ucsd.crbs.cws.servlet;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import edu.ucsd.crbs.cws.dao.WorkflowDAO;
import edu.ucsd.crbs.cws.dao.objectify.WorkflowObjectifyDAOImpl;
import edu.ucsd.crbs.cws.gae.BlobStoreServiceUtil;
import edu.ucsd.crbs.cws.rest.Constants;
import edu.ucsd.crbs.cws.workflow.Workflow;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.mail.Transport.send;
import javax.servlet.http.HttpServletResponse;

/**
 * Via {@link send} method instances of this class send workflow files
 * to client using Google App Engine BlobstoreService
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class WorkflowDownloaderImpl implements WorkflowDownloader {

    private static final Logger _log = Logger.getLogger(WorkflowDownloaderImpl.class.getName());

    WorkflowDAO _workflowDAO = new WorkflowObjectifyDAOImpl();

    /**
     * Given a <b>workflowId</b> this method sends the associate workflow file to
     * client via the <b>response</b> passed in.  Code sets response to 
     * {@link HttpServletResponse.SC_INTERNAL_SERVER_ERROR} if there was a problem
     * loading the workflow from the data store or if there is no file associated
     * with the workflow.  A {@link HttpServletResponse.SC_NOT_FOUND} is set in
     * response if no workflow with id is found.
     *
     * @param workflowId
     * @param response
     * @throws IOException 
     */ 
    @Override
    public void send(String workflowId, 
                     HttpServletResponse response) throws IOException {
        
        Workflow w;

        try {
            w = _workflowDAO.getWorkflowById(workflowId,null);
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error getting workflow by id from data store: "+
                            ex.getMessage());
            return;
        }

        if (w == null) {
            _log.log(Level.SEVERE, "Workflow returned by data store is null");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, 
                    "No workflow matching id found: " + workflowId);
            return;
        }

        if (w.getBlobKey() == null) {
            _log.log(Level.SEVERE, "blob key is null");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Key to workflow file not found for workflow: "
                    + workflowId);
            return;
        }

        BlobstoreService blobstoreService = BlobStoreServiceUtil.getBlobstoreService();

        BlobKey blobKey = new BlobKey(w.getBlobKey());
        response.setContentType("application/x-download");
        
        response.setHeader("Content-Disposition",
                new StringBuilder().append("attachment; filename=").
                        append(workflowId).
                        append(Constants.WORKFLOW_SUFFIX).toString());
        
        _log.log(Level.INFO, "Attempting to serve blob with key: {0}", 
                blobKey.getKeyString());
        blobstoreService.serve(blobKey, response);
    }

}
