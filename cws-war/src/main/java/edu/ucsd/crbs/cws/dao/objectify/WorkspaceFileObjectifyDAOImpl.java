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
package edu.ucsd.crbs.cws.dao.objectify;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.cmd.Query;
import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.dao.InputWorkspaceFileLinkDAO;
import edu.ucsd.crbs.cws.dao.JobDAO;
import edu.ucsd.crbs.cws.dao.WorkspaceFileDAO;
import static edu.ucsd.crbs.cws.dao.objectify.OfyService.ofy;
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import edu.ucsd.crbs.cws.workflow.report.DeleteWorkspaceFileReport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides methods to load and save WorkspaceFile objects via Objectify
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class WorkspaceFileObjectifyDAOImpl implements WorkspaceFileDAO {

    private static final Logger _log
            = Logger.getLogger(WorkspaceFileObjectifyDAOImpl.class.getName());
    
    private JobDAO _jobDAO = null;
    private InputWorkspaceFileLinkDAO _inputWorkspaceFileLinkDAO = null;
    
    /*
    public WorkspaceFileObjectifyDAOImpl(JobDAO jobDAO){
        _jobDAO = jobDAO;
    }
    */
    
    

    @Override
    public WorkspaceFile resave(final long workspaceFileId) throws Exception {
        
        WorkspaceFile resWorkspaceFile = ofy().transact(new Work<WorkspaceFile>() {
            @Override
            public WorkspaceFile run() {
                WorkspaceFile wsf;
                try {
                    wsf = getWorkspaceFileById(Long.toString(workspaceFileId),null);
                } catch (Exception ex) {
                    _log.log(Level.SEVERE,"Caught exception",ex);
                    return null;
                }
                if (wsf == null) {
                    return null;
                }

                Key<WorkspaceFile> tKey = ofy().save().entity(wsf).now();
                return wsf;
            }
        });

        return resWorkspaceFile;
    }
    
    
    
    @Override
    public List<WorkspaceFile> getWorkspaceFiles(final String owner,final String type,
            final Boolean isFailed,final Boolean synced,final Boolean showDeleted) throws Exception {

        Query<WorkspaceFile> q = ofy().load().type(WorkspaceFile.class);

        if (owner != null) {
            q = q.filter("_owner in ", Arrays.asList(owner.split(",")));
        }
        
        if (type != null){
            q = q.filter("_type in ",Arrays.asList(type.split(",")));
        }
        
        if (isFailed != null){
            q = q.filter("_failed ==",isFailed);
        }
        
        if (showDeleted != null){
            q = q.filter("_deleted ==",showDeleted);
        }
        else {
            q = q.filter("_deleted ==",false);
        }

        if (synced != null) {
            if (synced == true) {
                q = q.filter("_path !=", null);
            } else {
                q = q.filter("_path ==", null);
            }
        }
        return q.list();
    }

    @Override
    public List<WorkspaceFile> getWorkspaceFilesById(String workspaceFileIds, User user) throws Exception {
        if (workspaceFileIds == null) {
            _log.log(Level.INFO,"workspaceFileIds was null");
            return null;
        }
        _log.log(Level.INFO, "Querying with id(s): {0}", workspaceFileIds);
        
        ArrayList<Long> idList = new ArrayList<>();
        //need to split ids by comma and then convert them into longs
        for (String id : workspaceFileIds.split(",")){
           idList.add(new Long(id));
        }
        
        Map<Long, WorkspaceFile> res = ofy().load().type(WorkspaceFile.class).ids(idList);
        if (res == null) {
            _log.log(Level.INFO,"query returned null");
            return null;
        }
        _log.log(Level.INFO, "Found {0} workspace files", res.values().size());
        ArrayList<WorkspaceFile> workspaceFiles = new ArrayList<>();
        workspaceFiles.addAll(res.values());
        return workspaceFiles;
    }

    @Override
    public WorkspaceFile getWorkspaceFileById(String workspaceFileId, User user) throws Exception {
        long wspId;
        if (workspaceFileId == null) {
            throw new IllegalArgumentException("workspace file id cannot be null");
        }

        try {
            wspId = Long.parseLong(workspaceFileId);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException("Unable to parse workspacefile id from: " + workspaceFileId + " : " + nfe.getMessage());
        }
        if (wspId <= 0) {
            throw new Exception(workspaceFileId + " is not a valid workspace file id");
        }

        return ofy().load().type(WorkspaceFile.class).id(wspId).now();
    }

    @Override
    public WorkspaceFile insert(WorkspaceFile wsp,boolean generateUploadURL) throws Exception {
        if (wsp == null) {
            throw new Exception("WorkspaceFile passed in is null");
        }

        if (wsp.getCreateDate() == null) {
            wsp.setCreateDate(new Date());
        }
        Key<WorkspaceFile> wspKey = ofy().save().entity(wsp).now();
        return wsp;
    }

    /**
     * 
     * @param workspaceFileId
     * @param key
     * @return {@link WorkspaceFile} from data store with updates
     * @throws Exception 
     * @deprecated This method has been replaced by {@link #update(edu.ucsd.crbs.cws.workflow.WorkspaceFile, java.lang.Boolean, java.lang.Boolean, java.lang.Boolean) }
     */
    @Override
    public WorkspaceFile updateBlobKey(final long workspaceFileId,
            final String key) throws Exception {
        WorkspaceFile tempWsp = new WorkspaceFile();
        tempWsp.setId(workspaceFileId);
        tempWsp.setBlobKey(key);
        return update(tempWsp,null,null,null);
    }

    /**
     * 
     * @param workspaceFileId
     * @param path
     * @param size
     * @param isFailed
     * @return {@link WorkspaceFile} from data store with updates
     * @throws Exception 
     * @deprecated This method has been replaced by {@link #update(edu.ucsd.crbs.cws.workflow.WorkspaceFile, java.lang.Boolean, java.lang.Boolean, java.lang.Boolean) }
     */
    @Override
    public WorkspaceFile updatePathSizeAndFailStatus(final long workspaceFileId, final String path,
            final String size,final Boolean isFailed) throws Exception {
        
        WorkspaceFile tempWsp = new WorkspaceFile();
        tempWsp.setId(workspaceFileId);
        tempWsp.setPath(path);
        if (size != null){
            tempWsp.setSize(new Long(size));
        }
        
        return update(tempWsp,null,isFailed,null);
    }

    @Override
    public WorkspaceFile update(final WorkspaceFile wsp,final Boolean isDeleted, 
            final Boolean isFailed,final Boolean isDir) throws Exception {
        if (wsp == null) {
            throw new IllegalArgumentException("WorkspaceFile cannot be null");
        }
        if (wsp.getId() == null) {
            throw new Exception("Id must be set");
        }
        WorkspaceFile resWsp;
        resWsp = ofy().transact(new Work<WorkspaceFile>() {
            @Override
            public WorkspaceFile run() {
                WorkspaceFile wspFromDataStore = ofy().load().type(WorkspaceFile.class).id(wsp.getId()).now();
                if (wspFromDataStore == null) {
                    return null;
                }                
                
                if (wspFromDataStore.updateWithChanges(wsp, isDeleted, isFailed, isDir)){
                    Key<WorkspaceFile> wspKey = ofy().save().entity(wspFromDataStore).now();
                    return wspFromDataStore;
                }
                return wspFromDataStore;
            }
        });
        if (resWsp == null) {
            throw new Exception("There was a problem updating the WorkspaceFile");
        }
        return resWsp;
    }

    
    @Override
    public List<WorkspaceFile> getWorkspaceFilesBySourceJobId(long sourceJobId) throws Exception {
        Query<WorkspaceFile> q = ofy().load().type(WorkspaceFile.class);
        q = q.filter("_sourceJobId ==", sourceJobId);

        return q.list();
    }
    
    @Override
    public DeleteWorkspaceFileReport delete(long workspaceFileId, Boolean permanentlyDelete,
            boolean ignoreParentJob) throws Exception {
        DeleteWorkspaceFileReport dwr = new DeleteWorkspaceFileReport();
        dwr.setId(workspaceFileId);
        dwr.setSuccessful(false);
        dwr.setReason("Unknown");
        
        //load workspace file
        WorkspaceFile wsf = this.getWorkspaceFileById(Long.toString(workspaceFileId), null);
        if (wsf == null){
            dwr.setReason("WorkspaceFile not found");
            return dwr;
        }
        
        _log.log(Level.INFO,"Checking if its possible to delete WorkspaceFile {0} ",
                workspaceFileId);
        
        if (ignoreParentJob == false){
            //see if WorkspaceFile is output of existing Job
            if (wsf.getSourceJobId() != null){
                Job j = _jobDAO.getJobById(wsf.getSourceJobId().toString());
                if (j != null){
                    dwr.setReason("Cannot delete WorkspaceFile it is output of job ("+
                            j.getId()+" "+j.getName());
                    return dwr;
                }
            }
        }
        
        //see if WorkspaceFile was used as input for any Job
        int numLinkedWorkspaceFiles = _inputWorkspaceFileLinkDAO.getByWorkspaceFileIdCount(wsf.getId(), 
                null);
        if (numLinkedWorkspaceFiles > 0){
            dwr.setReason("Found WorkspaceFile is linked to "+
                    numLinkedWorkspaceFiles+" Job(s)");
            return dwr;
        }
        
        //if permanentlyDelete is not null and true then run real delete
        if (permanentlyDelete != null && permanentlyDelete == true) {
            if (wsf.getBlobKey() != null) {
                _log.log(Level.INFO, "Blob key found {0}  Deleting from blobstore",
                        wsf.getBlobKey());
                BlobKey bk = new BlobKey(wsf.getBlobKey());
                BlobInfo bInfo = new BlobInfoFactory().loadBlobInfo(bk);
                if (bInfo == null) {
                    _log.log(Level.WARNING, "No BlobInfo found");
                } else {
                    _log.log(Level.INFO, "Found file {0}, attempting to delete", bInfo.getFilename());
                    BlobstoreServiceFactory.getBlobstoreService().delete(bk);
                }
            }
            ofy().delete().type(WorkspaceFile.class).id(wsf.getId()).now();
        }
        else {
            update(wsf, true,null,null);
        }
        dwr.setSuccessful(true);
        dwr.setReason(null);
        return dwr;
    }

}
