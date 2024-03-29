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


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import java.util.List;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import java.util.Date;
import java.util.Iterator;

/**
 * Represents a Workflow which is basically a program that does something.
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@Entity
@Cache
@JsonPropertyOrder(value={ "id","name","createDate","version","description","releaseNotes"},alphabetic=true)
public class Workflow {
    
    public static boolean REFS_ENABLED = true;

    
    public static class Everything {}
    @Id private Long _id;
    @Index private String _name;
    @Index private int _version;
    private String _description;
    @Index private Date _createDate;
    private String _releaseNotes;
    @Index private String _owner;
    @Index private String _author;
    @Index private boolean _deleted;
    private List<WorkflowParameter> _parameters;
    private @Load(Workflow.Everything.class) Ref<Workflow> _parent;
    @Ignore private Workflow _rawParent;
    @Ignore private String _workflowFileUploadURL;
    private String _blobKey;
    
    public Workflow(){
        
    }
        
    public Long getId(){
        return _id;
    }
    public void setId(Long id){
       _id = id;
    }
    
    public boolean isDeleted() {
        return _deleted;
    }

    public void setDeleted(boolean deleted) {
        _deleted = deleted;
    }
    
    public void setVersion(int val){
        _version = val;
    }
    
    public int getVersion(){
        return _version;
    }
    
     public void setParentWorkflow(Workflow workflow){
        _rawParent = workflow;

        if (workflow == null){
            _parent = null;
            return;
        }
        if (REFS_ENABLED){
            _parent = Ref.create(workflow);
        }
        
    }
    
    public Workflow getParentWorkflow(){
        if (REFS_ENABLED == false){
            return _rawParent;
        }
        
        if (_parent == null){
            return null;
        }
        return _parent.get();
    }
    
    public void setCreateDate(final Date date){
        _createDate = date;
    }
    
    public Date getCreateDate(){
        return _createDate;
    }
    
    public void setReleaseNotes(final String notes){
        _releaseNotes = notes;
    }
    
    public String getReleaseNotes(){
        return _releaseNotes;
    }
    
    public String getName(){
        return _name;
    }
    
    public void setName(final String name){
        _name = name;
    }
    
    public void setParameters(List<WorkflowParameter> params){
        _parameters = params;
    }
    
    public List<WorkflowParameter> getParameters(){
        return _parameters;
    }
    
    public void setDescription(final String description){
        _description = description;
    }
    
    public String getDescription(){
        return _description;
    }
    
    public void setWorkflowFileUploadURL(final String url){
        _workflowFileUploadURL = url;
    }
    
    public String getWorkflowFileUploadURL(){
        return _workflowFileUploadURL;
    }
    
    @JsonIgnore
    public void setBlobKey(String key){
        _blobKey = key;
    }
    
    @JsonIgnore
    public String getBlobKey(){
        return _blobKey;
    }
    
    /**
     * Iterates through {@link WorkflowParameter} objects and removes/returns the first one
     * whose {@link WorkflowParameter#getName()} matches <b>name</b> passed in
     * @param name Name to compare
     * @return {@link WorkflowParameter} that matches or null if none found
     */
    @JsonIgnore
    public WorkflowParameter removeWorkflowParameterMatchingName(final String name){
        if (name == null){
            return null;
        }
        
        if (_parameters == null || _parameters.isEmpty() == true){
            return null;
        }
        WorkflowParameter param;
        for (Iterator wParamIterator = _parameters.iterator();wParamIterator.hasNext() ;){
            param = (WorkflowParameter)wParamIterator.next();
            if (param.getName().equals(name)){
                wParamIterator.remove();
                return param;
            }
        }
        return null;
    }
    
    public String getOwner() {
        return _owner;
    }

    public void setOwner(String owner) {
        _owner = owner;
    }
    
    public String getAuthor() {
        return _author;
    }

    public void setAuthor(String author) {
        _author = author;
    }

}

