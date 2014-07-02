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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import edu.ucsd.crbs.cws.rest.Constants;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 * Checks to see if a given {@link WorkspaceFile} file/dir exists on the filesystem
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class SyncWorkspaceFileToFileSystemImpl implements SyncWorkspaceFileToFileSystem {

     private static final Logger _log
            = Logger.getLogger(SyncWorkspaceFileToFileSystemImpl.class.getName());
    
     
    private final String _workspaceDir;
    private final String _getURL;
    private final String _userLogin;
    private final String _token;
     
     public  SyncWorkspaceFileToFileSystemImpl(final String workspaceDir,
             final String url, final String userLogin, final String token){
         
        _workspaceDir = workspaceDir;
        _getURL = url;
        _userLogin = userLogin;
        _token = token;
     }
     
    @Override
    public void sync(WorkspaceFile wsf) throws Exception {
        
        if (wsf == null){
            throw new IllegalArgumentException("WorkspaceFile cannot be null");
        }
        
        if (doesWorkspaceFileExistOnFileSystem(wsf) == true){
            return;
        }
        
        //there isnt a workflow file so lets download one from web service
        ClientConfig cc = new DefaultClientConfig();
        Client client = Client.create(cc);
        client.setFollowRedirects(true);
        WebResource resource = client.resource(_getURL).path("workspacefile").
                queryParam(Constants.WSFID_PARAM, wsf.getId().toString()).
                queryParam(Constants.USER_LOGIN_PARAM, _userLogin).
                queryParam(Constants.USER_TOKEN_PARAM,_token);
        
        ClientResponse cr = resource.get(ClientResponse.class);
        _log.log(Level.INFO, "Status: {0} Reason: {1}", new Object[]{cr.getStatus(), 
            cr.getStatusInfo().getReasonPhrase()});
        
        // @TODO MOVE 200 to constant
        //try one more time
        if (cr.getStatus() != 200){
            _log.log(Level.WARNING,"First request to service for file failed sleeping 2 seconds and trying again");
            Thread.sleep(2000);
            cr = resource.get(ClientResponse.class);
        }
        
        if (cr.getStatus() != 200){
            throw new Exception("Unable to request workspace file");
        }
       
        File wFile = cr.getEntity(File.class);
        
        if (wFile == null){
            throw new Exception("No file obtained from web request to base url: "+_getURL);
        }
        
        //make the directory for the workflow
        File wfDir = new File(getWorkspaceDirectory(wsf));
        if (wfDir.isDirectory() == false){
            _log.log(Level.INFO, "Creating directories: {0}", wfDir.getAbsolutePath());
            if (wfDir.mkdirs() == false){
                throw new Exception("Unable to create directory: "+wfDir.getAbsolutePath());
            }
        }
        
        FileUtils.moveFile(wFile,getWorkspaceFile(wsf));
        
        if (doesWorkspaceFileExistOnFileSystem(wsf) == false){
            throw new Exception("Unable to put workspace file on file system");
        }
        
        // need to update WorkspaceFile on GAE with path
        
    }
    
    private boolean doesWorkspaceFileExistOnFileSystem(WorkspaceFile wsf) throws Exception {
        String path = wsf.getPath();
        if (path == null){
            return false;
        }
        
        File fileCheck = new File(path);
        return fileCheck.exists();
    }
    
    private String getWorkspaceDirectory(WorkspaceFile wsf) throws Exception {
        return _workspaceDir+File.separator+wsf.getOwner()+File.separator+
                wsf.getId().toString();
    }
    
    private File getWorkspaceFile(WorkspaceFile wsf) throws Exception {
        Long wfId = wsf.getId();
        if (wfId == null){
            throw new NullPointerException("Workspace file id is null");
        }
        File newWsFile = new File(getWorkspaceDirectory(wsf)+File.separator+wsf.getName());
        wsf.setPath(newWsFile.getAbsolutePath());
        return newWsFile;
    }
}
