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

package edu.ucsd.crbs.cws.workflow;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;

/**
 * Links an input {@link WorkspaceFile} with a {@link Job}
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@Entity
@Cache
public class InputWorkspaceFileLink {

    public static boolean REFS_ENABLED = true;

    
    @Id private Long _id;
    private String _parameterName;
    @Index private Ref<Job> _job;
    @Ignore private Job _rawJob;
    @Index private Ref<WorkspaceFile> _workspaceFile;
    @Ignore private WorkspaceFile _rawWorkspaceFile;
    @Index private boolean _deleted;
    
    public Long getId() {
        return _id;
    }

    public void setId(Long _id) {
        this._id = _id;
    }

    public Job getJob() {
        if (REFS_ENABLED == false){
            return _rawJob;
        }
        if (_job == null){
            return null;
        }
        return _job.get();
    }

    public void setJob(Job job) {
        _rawJob = job;
        
        if (job == null){
            _job = null;
            return;
        }
        
        if (REFS_ENABLED ){
            _job = Ref.create(job);
        }
    }

    public WorkspaceFile getWorkspaceFile() {
        if (REFS_ENABLED == false){
            return _rawWorkspaceFile;
        }
        if (_workspaceFile == null){
            return null;
        }
        return _workspaceFile.get();
    }

    public void setWorkspaceFile(WorkspaceFile workspaceFile) {
        _rawWorkspaceFile = workspaceFile;
        if (workspaceFile == null){
            _workspaceFile = null;
            return;
        }
        if (REFS_ENABLED){
            _workspaceFile = Ref.create(workspaceFile);
        }
    }


    public boolean isDeleted() {
        return _deleted;
    }

    public void setDeleted(boolean _deleted) {
        this._deleted = _deleted;
    }
    
    public String getParameterName() {
        return _parameterName;
    }

    public void setParameterName(String parameterName) {
        _parameterName = parameterName;
    }

    
}
