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

package edu.ucsd.crbs.cws.gae;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;

/**
 * Utility class to extract the uploaded workflow id and associated blobkey
 * from blobMap generated from BlobstoreService
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class BlobStoreServiceUtil {
    
    static final Logger _log = Logger.getLogger(BlobStoreServiceUtil.class.getName());
    
    static BlobstoreService _blobStoreService;
    
    
    private static String[] getFirstBlobKeyFromMap(Map<String, List<BlobKey>> blobMap) throws Exception{
        if (blobMap == null) {
            throw new Exception("Unable to get list of uploads from BlobStoreService");
        }

        if (blobMap.keySet() == null || blobMap.keySet().isEmpty() == true) {
            throw new Exception("No uploaded files found");
        }

        if (blobMap.keySet().size() > 1) {
            _log.log(Level.WARNING, "Found {0} keys  Selecting the 1st one",
                    blobMap.keySet().size());
        }

        String id = blobMap.keySet().iterator().next();
        List<BlobKey> bkList = blobMap.get(id);

        if (bkList == null || bkList.isEmpty() == true) {
            throw new Exception("No uploaded files found for id " + id);
        }

        if (bkList.size() > 1) {
            _log.log(Level.WARNING,
                    "Found {0} blob keys for wfid {1}.  Expected only 1, using first one",
                    new Object[]{bkList.size(), id});
        }
        return new String[]{id,bkList.get(0).getKeyString()};
    }
    
    /**
     * Helper method that takes the <b>blobMap</b> generated from {@link BlobstoreService.getUploads()}
     * extracting the workflow id and {@link BlobKey} associated with the workflow
     * file uploaded.  The method puts those into an empty Workflow object and
     * returns it
     * @param blobMap
     * @return Workflow object with only id and blobkey set
     * @throws Exception If there is an error parsing the blob Map or if keys or values are null
     */
    public static Workflow getWorkflowWithBlobKeyFromMapOfBlobKeyLists(Map<String, List<BlobKey>> blobMap) throws Exception {
        
        String[] firstBlobKeyAndVal = getFirstBlobKeyFromMap(blobMap);
        Workflow w = new Workflow();
        w.setBlobKey(firstBlobKeyAndVal[1]);
        w.setId(Long.parseLong(firstBlobKeyAndVal[0]));
        return w;
    }
    
    public static WorkspaceFile getWorkspaceFileWithBlobKeyFromMapOfBlobKeyLists(Map<String, List<BlobKey>> blobMap) throws Exception {
        
        String[] firstBlobKeyAndVal = getFirstBlobKeyFromMap(blobMap);
        WorkspaceFile w = new WorkspaceFile();
        w.setBlobKey(firstBlobKeyAndVal[1]);
        w.setId(Long.parseLong(firstBlobKeyAndVal[0]));
        return w;
    }
    
    public static BlobstoreService getBlobstoreService(){
        if (_blobStoreService != null){
            return _blobStoreService;
        }
        return BlobstoreServiceFactory.getBlobstoreService();
    }
    
    public static void serveBlobKeyForDownload(final String key,final String filename,
            HttpServletResponse response) throws Exception {
        
        if (response == null){
            throw new Exception("HttpServletResponse is null");
        }
        
        if (key == null){
             response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Key is null");
             return;
        }
        
        if (filename == null){
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "filename to serve is null");
             return;
        }
        
        BlobstoreService blobstoreService = BlobStoreServiceUtil.getBlobstoreService();

        BlobKey blobKey = new BlobKey(key);
        response.setContentType("application/x-download");
        
        response.setHeader("Content-Disposition",
                new StringBuilder().append("attachment; filename=").
                        append(filename).toString());
        
        _log.log(Level.INFO, "Attempting to serve blob with key: {0}", 
                blobKey.getKeyString());
        blobstoreService.serve(blobKey, response);
    }

}
