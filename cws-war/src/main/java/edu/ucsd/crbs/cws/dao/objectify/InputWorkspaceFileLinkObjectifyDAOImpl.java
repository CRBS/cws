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
import edu.ucsd.crbs.cws.dao.InputWorkspaceFileLinkDAO;
import static edu.ucsd.crbs.cws.dao.objectify.OfyService.ofy;
import edu.ucsd.crbs.cws.workflow.InputWorkspaceFileLink;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class InputWorkspaceFileLinkObjectifyDAOImpl implements InputWorkspaceFileLinkDAO  {

    private static final Logger _log
            = Logger.getLogger(InputWorkspaceFileLinkObjectifyDAOImpl.class.getName());
    
    @Override
    public InputWorkspaceFileLink insert(InputWorkspaceFileLink workspaceFileLink) throws Exception {
        if (workspaceFileLink == null){
            throw new NullPointerException("InputWorkspaceFileLink is null");
        }
        Key<InputWorkspaceFileLink> iKey = ofy().save().entity(workspaceFileLink).now();
        return workspaceFileLink;
    }

    @Override
    public List<InputWorkspaceFileLink> getByJobId(Long jobId, Boolean showDeleted) throws Exception {
        Query<InputWorkspaceFileLink> q = ofy().load().type(InputWorkspaceFileLink.class);
        
        q = q.filter("_jobId ==",jobId);
        
        if (showDeleted == null || showDeleted == false){
            q = q.filter("_deleted",false);
        }
        return q.list();
    }
    
    private Query<InputWorkspaceFileLink> getByWorkspaceFileIdQuery(Long workspaceFileId, 
            Boolean showDeleted) throws Exception {
        Query<InputWorkspaceFileLink> q = ofy().load().type(InputWorkspaceFileLink.class);
        
        q = q.filter("_workspaceFileId ==",workspaceFileId);
        
        if (showDeleted == null || showDeleted == false){
            q = q.filter("_deleted",false);
        }
        return q;
    }

    @Override
    public List<InputWorkspaceFileLink> getByWorkspaceFileId(Long workspaceFileId, Boolean showDeleted) throws Exception {
        Query<InputWorkspaceFileLink> q = getByWorkspaceFileIdQuery(workspaceFileId,
                showDeleted);
        return q.list();
    }

    @Override
    public int getByWorkspaceFileIdCount(Long workspaceFileId, Boolean showDeleted) throws Exception {
        Query<InputWorkspaceFileLink> q = getByWorkspaceFileIdQuery(workspaceFileId,
                showDeleted);
        return q.count();
    }

    @Override
    public InputWorkspaceFileLink getById(Long inputWorkspaceFileLinkId) throws Exception {
        return ofy().load().type(InputWorkspaceFileLink.class).id(inputWorkspaceFileLinkId).now();
    }

    /**
     * In a transaction, resaves {@link InputWorkspaceFileLink} whose {@link InputWorkspaceFileLink#getId() matches 
     * <b>inputWorkspaceFileLinkId</b>
     * @param inputWorkspaceFileLinkId
     * @return {@link InputWorkspaceFileLink} after resave or null if save did not occur
     * @throws Exception If there is an error during the save
     */
    @Override
    public InputWorkspaceFileLink resave(final long inputWorkspaceFileLinkId) throws Exception {
        InputWorkspaceFileLink res = ofy().transact(new Work<InputWorkspaceFileLink>() {
            @Override
            public InputWorkspaceFileLink run() {
                InputWorkspaceFileLink link;
                try {
                    link = getById(inputWorkspaceFileLinkId);
                } catch(Exception ex){
                    return null;
                }
                if (link == null){
                    return null;
                }
                Key<InputWorkspaceFileLink> tLink = ofy().save().entity(link).now();
                return link;
            }
        });
        return res;
    }
     
    /**
     * Gets list of all {@link InputWorkspaceFileLink}s
     * @param showDeleted {@link InputWorkspaceFileLink}s with {@link InputWorkspaceFileLink#isDeleted()} will only
     * be displayed if this parameter is <b>NOT <code>null</code></b> and set to <b><code>true</code></b>
     * @return List of {@link INputWorkspaceFileLink} objects
     * @throws Exception 
     */
    @Override
    public List<InputWorkspaceFileLink> getInputWorkspaceFileLinks(Boolean showDeleted) throws Exception {
        Query<InputWorkspaceFileLink> q = ofy().load().type(InputWorkspaceFileLink.class);
        
        if (showDeleted != null){
            q = q.filter("_deleted",showDeleted);
        }
        else {
            q = q.filter("_deleted",false);
        }
        return q.list();
    }
    
    
    
    

}
