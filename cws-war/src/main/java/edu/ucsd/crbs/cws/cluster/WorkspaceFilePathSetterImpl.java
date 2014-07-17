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
import edu.ucsd.crbs.cws.workflow.Task;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class WorkspaceFilePathSetterImpl implements WorkspaceFilePathSetter {

    WorkspaceFileDAO _workspaceFileDAO;
    
    
    public WorkspaceFilePathSetterImpl(WorkspaceFileDAO workspaceFileDAO){
        _workspaceFileDAO = workspaceFileDAO;
    }

    /**
     * For all {@link Parameter} objects in {@link Task} <b>t</b> where is
     * workspace file is set to true. This method gets the path of the
     * {@link WorkspaceFile} that matches id in {@link Parameter#getValue()}.
     *
     * @param t Task to update
     * @return true if all files were successfully updated, otherwise false.
     * @throws Exception
     */
    @Override
    public boolean setPaths(Task t) throws Exception {
        if (t == null) {
            return false;
        }
        
        if (t.getParameters() == null || t.getParameters().isEmpty()){
            return true;
        }
        
        StringBuilder sb = new StringBuilder();
        for (Parameter param : t.getParameters()) {
            if (param.isIsWorkspaceId() == false) {
                continue;
            }
            if (param.getValue() == null){
                throw new NullPointerException("Parameter value is null");
            }
            
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(param.getValue());
        }
        
        if (this._workspaceFileDAO == null){
            throw new NullPointerException("WorkspaceFileDAO must be set via constructor");
        }
        

        Map<Long, WorkspaceFile> wsMap = getMapOfWorkspaceFiles(sb.toString());

        for (Parameter param : t.getParameters()) {
            if (param.isIsWorkspaceId() == false) {
                continue;
            }
            Long wspId = new Long(param.getValue());
            if (!wsMap.containsKey(wspId)) {
                return false;
            }
            WorkspaceFile wsf = wsMap.get(wspId);
            if (wsf.getPath() == null) {
                return false;
            }
            param.setValue(wsf.getPath());
        }

        return true;
    }

    private Map<Long, WorkspaceFile> getMapOfWorkspaceFiles(final String workspaceIds) throws Exception {
        List<WorkspaceFile> wsFiles = _workspaceFileDAO.getWorkspaceFilesById(workspaceIds, null);
        HashMap<Long, WorkspaceFile> wspMap = new HashMap<>();
        for (WorkspaceFile wsf : wsFiles) {
            wspMap.put(wsf.getId(), wsf);
        }
        return wspMap;
    }
}
