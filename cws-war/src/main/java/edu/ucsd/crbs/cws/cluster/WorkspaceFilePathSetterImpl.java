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

import edu.ucsd.crbs.cws.dao.WorkspaceFileDAO;
import edu.ucsd.crbs.cws.workflow.Parameter;
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Iterates through {@link Job#getParameters()} and updates their {@link Parameter#getValue()}
 * with proper filesystem paths by querying the data store for their location on the file system
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class WorkspaceFilePathSetterImpl implements WorkspaceFilePathSetter {

    
    private static final Logger _log
            = Logger.getLogger(WorkspaceFilePathSetterImpl.class.getName());
    
    WorkspaceFileDAO _workspaceFileDAO;
    
    
    public WorkspaceFilePathSetterImpl(WorkspaceFileDAO workspaceFileDAO){
        _workspaceFileDAO = workspaceFileDAO;
    }

    /**
     * Examines all {@link Parameter} objects in {@link Job} <b>t</b> 
     * where {@link Parameter#isIsWorkspaceId()} is set to true.  The method
     * assumes {@link Parameter#getValue()} is a 
     * {@link WorkspaceFile#getId()}.  The method then retrieves these WorkspaceFile
     * objects from the data store and replaces the {@link Parameter#getValue()} with the
     * value of {@link WorkspaceFile#getPath()} if that path is not null. 
     *
     * @param j Job to update
     * @return true if all files {@link Parameter} were updated successfully with valid paths otherwise false
     * @throws NullPointerException if a Parameter value is null or if {@link WorkspaceFileDAO} set via constructor is null
     * @throws NumberFormatException if a Parameter value cannot be converted to a workspace id which is a Long
     */
    @Override
    public boolean setPaths(Job j) throws Exception {
        if (j == null) {
            _log.log(Level.WARNING,"Job passed in is null");
            return false;
        }
        
        if (j.getParameters() == null || j.getParameters().isEmpty()){
            _log.log(Level.INFO, "Job {0} has no parameters",j.getId());
            return true;
        }
        
        StringBuilder sb = new StringBuilder();
        for (Parameter param : j.getParameters()) {
            if (param.isIsWorkspaceId() == false) {
                continue;
            }
            
            if (param.getValue() == null || param.getValue().isEmpty()){
                continue;
            }
            
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(param.getValue());
        }
        
        if (sb.length() == 0){
            _log.log(Level.INFO,"Job {0} has no workspacefile parameters that require paths to be set",
                    j.getId());
            return true;
        }
        
        Map<Long, WorkspaceFile> wsMap = getMapOfWorkspaceFiles(sb.toString());

        for (Parameter param : j.getParameters()) {
            if (param.isIsWorkspaceId() == false) {
                continue;
            }
            Long wspId = new Long(param.getValue());
            if (!wsMap.containsKey(wspId)) {
                _log.log(Level.INFO,"No workspacefile with id {0} found for job {1}",
                        new Object[]{wspId,j.getId()});
                return false;
            }
            WorkspaceFile wsf = wsMap.get(wspId);
            if (wsf.getPath() == null) {
                _log.log(Level.INFO,"Path is null for workspacefile {0}, a parameter for job {1}",
                        new Object[]{wspId,j.getId()});
                return false;
            }
            param.setValue(wsf.getPath());
        }

        return true;
    }

    /**
     * Uses {@link WorkspaceFileDAO} to query data store for {@link WorkspaceFile} objects
     * generating a Map of the results.
     * @param workspaceIds Comma delimited list of {@link WorkspaceFile#getId()} values
     * @return Map with key set to {@link WorkspaceFile#getId()} and value set to {@link WorkspaceFile}
     * @throws Exception 
     */
    private Map<Long, WorkspaceFile> getMapOfWorkspaceFiles(final String workspaceIds) throws Exception {
        
        if (_workspaceFileDAO == null){
            throw new NullPointerException("WorkspaceFileDAO must be set via constructor");
        }
        
        List<WorkspaceFile> wsFiles = _workspaceFileDAO.getWorkspaceFilesById(workspaceIds, null);
        HashMap<Long, WorkspaceFile> wspMap = new HashMap<>();
        for (WorkspaceFile wsf : wsFiles) {
            wspMap.put(wsf.getId(), wsf);
        }
        return wspMap;
    }
}
