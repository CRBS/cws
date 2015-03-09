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


/**
 * Instances of this class represent a parameter in a Job. 
 * Parameters have a name and a value
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class Parameter {
    protected String _name;
    protected String _value;
    protected boolean _isWorkspaceId;
    
    protected WorkflowParameter _workflowParameter;

    public Parameter(){
        
    }
    
    public Parameter(final String name, final String value){
        _name = name;
        _value = value;
    }
    
    /**
     * Sets name
     * @param name Name to set, can be null
     */
    public void setName(final String name){
        _name = name;
    }
    
    /**
     * Gets name
     * @return String containing name, can be null
     */
    public String getName(){
        return _name;
    }
        
    /**
     * Sets value
     * @param value Value to set, can be null
     */
    public void setValue(final String value){
        _value = value;
    }
    
    /**
     * Gets value
     * @return String containing value, can be null
     */
    public String getValue(){
        return _value;
    }
    
    @JsonIgnore
    public String asString(){
        StringBuilder sb = new StringBuilder();
        sb.append("name=");
        if (_name == null){
            sb.append("null,value=");
        }
        else {
            sb.append(_name).append(",value=");
        }
        if (_value == null){
            sb.append("null");
        }
        else {
            sb.append(_value);
        }
        return sb.toString();
    }
    
    public WorkflowParameter getWorkflowParameter() {
        return _workflowParameter;
    }

    public void setWorkflowParameter(WorkflowParameter workflowParameter) {
        _workflowParameter = workflowParameter;
    }
    
    public boolean isIsWorkspaceId() {
        return _isWorkspaceId;
    }

    public void setIsWorkspaceId(boolean isWorkspaceId) {
        _isWorkspaceId = isWorkspaceId;
    }
}
