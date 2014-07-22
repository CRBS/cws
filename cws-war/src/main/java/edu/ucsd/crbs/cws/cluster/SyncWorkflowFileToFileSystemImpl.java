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

package edu.ucsd.crbs.cws.cluster;

import edu.ucsd.crbs.cws.jerseyclient.FileDownloader;
import edu.ucsd.crbs.cws.jerseyclient.FileDownloaderImpl;
import edu.ucsd.crbs.cws.rest.Constants;
import edu.ucsd.crbs.cws.workflow.Workflow;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 * Checks and persists Workflow file to file system
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class SyncWorkflowFileToFileSystemImpl implements SyncWorkflowFileToFileSystem {

    
      private static final Logger _log
            = Logger.getLogger(SyncWorkflowFileToFileSystemImpl.class.getName());
    
          static FileDownloader _fileDownloader = new FileDownloaderImpl();

      
    private final String _workflowsDir;
    private final String _getURL;
    private final String _userLogin;
    private final String _token;
    
    public SyncWorkflowFileToFileSystemImpl(final String workflowsDir,
            final String url,final String userLogin,final String token){
        _workflowsDir = workflowsDir;
        _getURL = url;
        _userLogin = userLogin;
        _token = token;
        
    }
    
    /**
     * Code checks if File for Workflow is on the filesystem.  If not code downloads
     * workflow and writes to file system.
     * @param w
     * @throws Exception If any error is encountered
     */
    @Override
    public void sync(Workflow w) throws Exception {
        //TODO need to implement with this logic:
        //http://stackoverflow.com/questions/8928037/how-do-i-get-to-store-a-downloaded-file-with-java-and-jersey
        
        //lets handle the easy case
        //check if the workflow file exists if it does we are done
        if (doesWorkflowExistOnFileSystem(w) == true){
            return;
        }
        
        File wFile = _fileDownloader.downloadFile(_getURL+"/workflowfile",
                Constants.WFID_PARAM,w.getId().toString(),
                _userLogin, _token);
        
        if (wFile == null){
            throw new Exception("No file obtained from web request to base url: "+_getURL);
        }
        
        //make the directory for the workflow
        File wfDir = new File(getWorkflowDirectory(w));
        if (wfDir.isDirectory() == false){
            _log.log(Level.INFO, "Creating directories: {0}", wfDir.getAbsolutePath());
            if (wfDir.mkdirs() == false){
                throw new Exception("Unable to create directory: "+wfDir.getAbsolutePath());
            }
        }
        
        FileUtils.moveFile(wFile,getWorkflowFile(w));
        
        if (doesWorkflowExistOnFileSystem(w) == false){
            throw new Exception("Unable to put workflow on file system");
        }
    }
    
    private String getWorkflowDirectory(Workflow w) throws Exception {
        return this._workflowsDir+File.separator+w.getId().toString();
    }
    
    private File getWorkflowFile(Workflow w) throws Exception {
        Long wfId = w.getId();
        if (wfId == null){
            throw new NullPointerException("Workflow id is null");
        }
        
        return new File(getWorkflowDirectory(w)+File.separator+w.getId().toString()+
                Constants.WORKFLOW_SUFFIX);
    }
    
    /**
     * Checks if a workflow file exists on the file system for the Workflow object
     * passed in.  
     * @param w
     * @return true if workflow file exists on file system otherwise false
     * @throws Exception If there is an IO error or if the workflow id is null or if a non file exists at path work workflow file should reside
     */
    private boolean doesWorkflowExistOnFileSystem(Workflow w) throws Exception {
        
        File fileCheck = getWorkflowFile(w);
        
        if (fileCheck.exists() == true){
             // @TODO add checks for file validity ie size and verify its a workflow etc..
             if (fileCheck.isFile() == true){
                return true;
             }
             else {
                 throw new Exception("Non file exists where workflow should: "+fileCheck.getAbsolutePath());
             }
        }
        return false;
    }

}
