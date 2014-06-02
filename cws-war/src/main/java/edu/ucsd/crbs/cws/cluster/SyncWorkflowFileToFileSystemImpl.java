package edu.ucsd.crbs.cws.cluster;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
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
    
    private final String _workflowsDir;
    private final String _getURL;
    
    public SyncWorkflowFileToFileSystemImpl(final String workflowsDir,
            final String url){
        _workflowsDir = workflowsDir;
        _getURL = url;
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
        
        //there isnt a workflow file so lets download one from web service
        ClientConfig cc = new DefaultClientConfig();
        Client client = Client.create(cc);
        client.setFollowRedirects(true);
        WebResource resource = client.resource(_getURL).path("workflowfile").
                queryParam("wfid", w.getId().toString());
        
        ClientResponse cr = resource.get(ClientResponse.class);
        _log.log(Level.INFO,"Status: "+cr.getStatus()+" Reason: "+cr.getStatusInfo().getReasonPhrase());
        
        // @TODO MOVE 200 to constant
        //try one more time
        if (cr.getStatus() != 200){
            _log.log(Level.WARNING,"First request to service for file failed sleeping 2 seconds and trying again");
            Thread.sleep(2000);
            cr = resource.get(ClientResponse.class);
        }
        
        if (cr.getStatus() != 200){
            throw new Exception("Unable to request workflow file");
        }
       
        File wFile = cr.getEntity(File.class);
        
        if (wFile == null){
            throw new Exception("No file obtained from web request to base url: "+_getURL);
        }
        
        //make the directory for the workflow
        File wfDir = new File(getWorkflowDirectory(w));
        if (wfDir.isDirectory() == false){
            _log.log(Level.FINER,"Creating directories: "+wfDir.getAbsolutePath());
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
