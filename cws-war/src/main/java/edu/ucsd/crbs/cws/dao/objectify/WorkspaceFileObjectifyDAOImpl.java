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

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.cmd.Query;
import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.dao.WorkspaceFileDAO;
import static edu.ucsd.crbs.cws.dao.objectify.OfyService.ofy;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class WorkspaceFileObjectifyDAOImpl implements WorkspaceFileDAO {

    
    private static final Logger _log
            = Logger.getLogger(WorkspaceFileObjectifyDAOImpl.class.getName());

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
    public List<WorkspaceFile> getWorkspaceFiles(final String owner,
            final Boolean synced) throws Exception {

        Query<WorkspaceFile> q = ofy().load().type(WorkspaceFile.class);

        if (owner != null) {
            q = q.filter("_owner in ", Arrays.asList(owner.split(",")));
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

    @Override
    public WorkspaceFile updateBlobKey(final long workspaceFileId,
            final String key) throws Exception {
        WorkspaceFile resWsp;
        resWsp = ofy().transact(new Work<WorkspaceFile>() {
            @Override
            public WorkspaceFile run() {
                WorkspaceFile wsp = ofy().load().type(WorkspaceFile.class).id(workspaceFileId).now();
                if (wsp == null) {
                    return null;
                }

                if (wsp.getBlobKey() == null && key == null) {
                    return wsp;
                }

                if (wsp.getBlobKey() != null && key != null
                        && wsp.getBlobKey().equals(key)) {
                    return wsp;
                }

                wsp.setBlobKey(key);
                Key<WorkspaceFile> wspKey = ofy().save().entity(wsp).now();
                return wsp;
            }
        });
        if (resWsp == null) {
            throw new Exception("There was a problem updating the WorkspaceFile");
        }
        return resWsp;
    }

    @Override
    public WorkspaceFile updatePath(final long workspaceFileId, final String path) throws Exception {
        WorkspaceFile resWsp;
        resWsp = ofy().transact(new Work<WorkspaceFile>() {
            @Override
            public WorkspaceFile run() {
                WorkspaceFile wsp = ofy().load().type(WorkspaceFile.class).id(workspaceFileId).now();
                if (wsp == null) {
                    return null;
                }

                if (wsp.getPath() == null && path == null) {
                    return wsp;
                }

                if (wsp.getPath() != null) {
                    if (path != null && wsp.getPath().equals(path)) {
                        return wsp;
                    }
                }

                wsp.setPath(path);
                Key<WorkspaceFile> wspKey = ofy().save().entity(wsp).now();
                return wsp;
            }
        });
        if (resWsp == null) {
            throw new Exception("There was a problem updating the WorkspaceFile");
        }
        return resWsp;
    }

    @Override
    public WorkspaceFile update(WorkspaceFile wsp) throws Exception {
        if (wsp == null) {
            throw new IllegalArgumentException("WorkspaceFile cannot be null");
        }
        if (wsp.getId() == null) {
            throw new Exception("Id must be set");
        }

        ofy().save().entity(wsp).now();
        return wsp;
    }

}
