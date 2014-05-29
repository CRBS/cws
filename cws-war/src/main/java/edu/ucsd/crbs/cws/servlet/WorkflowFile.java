package edu.ucsd.crbs.cws.servlet;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
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

    public static final String CLOUD_BUCKET = "/gs/crbsworkflow.appspot.com/workflows/";

    public static final String BUCKET_NAME = "crbsworkflow.appspot.com";

    public static final String WFID="wfid";
    
    /**
     * This is the service from which all requests are initiated. The retry and
     * exponential backoff settings are configured here.
     */
    private final GcsService gcsService
            = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        _log.info("in get");
        
        String remoteIp = req.getRemoteAddr();
        
        
        if (req.getParameter(WFID) != null){
            String fileName = req.getParameter(WFID);
            if (fileName == null || fileName.trim().isEmpty()){
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid file specified to download");
                return;
            }
            BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
            BlobKey blobKey = blobstoreService.createGsBlobKey(
            CLOUD_BUCKET + fileName);
            resp.setContentType("application/x-download");
            resp.setHeader("Content-Disposition", "attachment; filename="+fileName);
            blobstoreService.serve(blobKey, resp);
            return;
        }
        resp.setContentType("text/plain");
        resp.getWriter().println("Hello, this is a testing servlet. \n\n");
        if (remoteIp != null){
            resp.getWriter().println("Your ip is: "+remoteIp);
        }
        
        Enumeration e = req.getHeaderNames();
        String header = null;
        if (e != null) {
            for (; e.hasMoreElements();) {
                header = (String) e.nextElement();
                resp.getWriter().print("Header: " + header + " == ");
                if (req.getHeader(header) != null) {
                    resp.getWriter().println(req.getHeader(header));
                } else {
                    resp.getWriter().println();
                }
            }
        }

    }
    
     @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("Hello, this is a testing servlet in doPOST. \n\n");

        
         BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
         Map<String,List<BlobKey>> blobMap = blobstoreService.getUploads(req);
         
         if (blobMap != null){
            for (String key : blobMap.keySet()){
                List<BlobKey> bkList = blobMap.get(key);
                String res = "null";
                String keyVal = "null";
                if (bkList != null){
                    res = Integer.toString(bkList.size());
                    keyVal = bkList.get(0).getKeyString();
                }
                resp.getWriter().println("Key "+key+" ==> ("+res+") "+keyVal);
                
            }
         }
        
        Enumeration e = req.getHeaderNames();
        String header = null;
        if (e != null) {
            for (; e.hasMoreElements();) {
                header = (String) e.nextElement();
                resp.getWriter().print("Header: " + header + " == ");
                if (req.getHeader(header) != null) {
                    resp.getWriter().println(req.getHeader(header));
                } else {
                    resp.getWriter().println();
                }
            }
        }
    }

}
