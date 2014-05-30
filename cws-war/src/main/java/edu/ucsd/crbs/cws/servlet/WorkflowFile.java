package edu.ucsd.crbs.cws.servlet;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import edu.ucsd.crbs.cws.dao.WorkflowDAO;
import edu.ucsd.crbs.cws.dao.objectify.WorkflowObjectifyDAOImpl;
import edu.ucsd.crbs.cws.workflow.Workflow;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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

    public static final String CLOUD_BUCKET = "crbsworkflow.appspot.com/workflows/";

    public static final String WFID = "wfid";

    WorkflowDAO _workflowDAO;
    /**
     * This is the service from which all requests are initiated. The retry and
     * exponential backoff settings are configured here.
     */

    public WorkflowFile(){
        _workflowDAO = new WorkflowObjectifyDAOImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        _log.info("in get");


        if (req.getParameter(WFID) != null) {
           
            String wfid = req.getParameter(WFID);
            if (wfid == null || wfid.trim().isEmpty()) {
                _log.warning("wfid is null or empty string");
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid file specified to download");
                return;
            }
             _log.log(Level.INFO, "Got a wfid of: {0}", wfid);

            Workflow w = null;
            try {
                w = this._workflowDAO.getWorkflowById(wfid);
            }
            catch(Exception ex){
                _log.log(Level.SEVERE, "unable to load workflow",ex);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR," Error retreiving workflow file: "+ex.getMessage());
            }
            if (w == null){
                _log.log(Level.SEVERE,"Workflow returned by data store is null");
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR," No workflow matching id found: "+w.getId());
            }
            
            if (w.getBlobKey() == null){
                _log.log(Level.SEVERE,"blob key is null");
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR," Key to workflow file not found for workflow: "+w.getId());
            }
            
            BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

            BlobKey blobKey = new BlobKey(w.getBlobKey());
            resp.setContentType("application/x-download");
            resp.setHeader("Content-Disposition", "attachment; filename=" + wfid+".kar");
            _log.log(Level.INFO, "attempting to serve blob with key: {0}", blobKey.getKeyString());
            blobstoreService.serve(blobKey, resp);
            return;
        }
        
        resp.setContentType("text/plain");
        resp.getWriter().println("Hello, this is a testing servlet. \n\n");
        String remoteIp = req.getRemoteAddr();
        if (remoteIp != null) {
            resp.getWriter().println("Your ip is: " + remoteIp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("Hello, this is doPOST call which registers the workflow file just uploaded. \n\n");

        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        Map<String, List<BlobKey>> blobMap = blobstoreService.getUploads(req);

        if (blobMap != null) {
            for (String key : blobMap.keySet()) {
                List<BlobKey> bkList = blobMap.get(key);
                String res = "null";
                String keyVal = "null";
                if (bkList != null) {
                    res = Integer.toString(bkList.size());
                    keyVal = bkList.get(0).getKeyString();
                }
                
                resp.getWriter().println("Key " + key + " ==> (" + res + ") " + keyVal);
                try {
                    this._workflowDAO.updateBlobKey(Long.parseLong(key),keyVal);
                }
                catch(Exception ex){
                    _log.log(Level.SEVERE, "Unable to update workflow with id: "+key, ex);
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR," Unable to update workflow with id: "+key+" : "+
                            ex.getMessage());
                }
            }
        }
    }

}
