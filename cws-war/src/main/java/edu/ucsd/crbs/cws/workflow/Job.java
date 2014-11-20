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
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.condition.IfFalse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents an invocation of a given {@link Workflow}
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@Entity
@JsonPropertyOrder(value={ "id","name","owner","status","createDate","submitDate","startDate","finishDate"},alphabetic=true)

public class Job  {
    
    public static boolean REFS_ENABLED = true;
    
    /**
     * Your job is in the queue, awaiting completion of your current running or pending job(s). 
     * Jobs in this state may also be held by the system and will automatically resume 
     * (e.g. jobs are held for system maintenance)
     */
    public static final String IN_QUEUE_STATUS = "In Queue";
    
    /**
     * Your job is ready to run, pending availability of resources
     */
    public static final String PENDING_STATUS = "Pending";
    
    /**
     * Your job is ready to run, pending synchronization of workspace files
     */
    public static final String WORKSPACE_SYNC_STATUS = "Workspace Sync";
    
    /**
     * Your job is running
     */
    public static final String RUNNING_STATUS = "Running";
    
    /**
     * Your job has completed
     */
    public static final String COMPLETED_STATUS = "Completed";
    
    /**
     * Your job has failed due to a detected error
     */
    public static final String ERROR_STATUS = "Error";
    
    /**
     * Your job has been paused by the system and will automatically resume 
     * (e.g. jobs are paused for system maintenance)
     */
    public static final String PAUSED_STATUS = "Paused";
    
    /**
     * Empty class that is used with Objectify queries to skip retrieval of the
     * Workflow object associated with this Job
     */
    public static class Everything {}
    
    @Id private Long _id;
    @Load(Job.Everything.class) private Ref<Workflow> _workflow;
    @Ignore private Workflow _rawWorkflow;
    
    private String _name;
    @Index private String _owner;
    @Index private String _status;
    @Index({IfFalse.class}) private boolean _hasJobBeenSubmittedToScheduler;
    private String _schedulerJobId;
    private long _estimatedCpuInSeconds;
    private long _estimatedRunTimeInSeconds;
    private long _estimatedDiskInBytes;
    private Date _createDate;
    private Date _submitDate;
    private Date _startDate;
    private Date _finishDate;
    @Index private boolean _deleted;
    private String _downloadURL;
    private List<Parameter> _parameters;
    private String _error;
    private String _detailedError;
    @Ignore private List<ParameterWithError> _parametersWithErrors;

    /**
     * This will eventually hold a summary of compute and disk consumed by
     * this job
     */
    //private ComputeSummary _computeSummary
    
    public Job(){
         
    }
    
    public void setId(Long id){
        _id = id;
    }
    
    public Long getId(){
        return _id;
    }
        
    public void setWorkflow(Workflow workflow){
        if (workflow == null){
            _workflow = null;
            _rawWorkflow = null;
            return;
        }
        if (REFS_ENABLED){
            _workflow = Ref.create(workflow);
        }
        _rawWorkflow = workflow;
    }
    
    public Workflow getWorkflow(){
        if (REFS_ENABLED == false){
            return _rawWorkflow;
        }
        if (_workflow == null){
            return null;
        }
        return _workflow.get();
    }
    
    public void setName(final String name){
        _name = name;
    }
    
    public String getName(){
        return _name;
    }
    
    public void setOwner(final String owner){
        _owner = owner;
    }
    
    public String getOwner(){
        return _owner;
    }

    public void setStatus(final String status){
        _status = status;
    }
    
    public String getStatus(){
        return _status;
    }
    
    public void setSchedulerJobId(final String jobId){
        _schedulerJobId = jobId;
    }
    
    public String getSchedulerJobId(){
        return _schedulerJobId;
    }
    
    public void setEstimatedCpuInSeconds(long val){
        _estimatedCpuInSeconds = val;
    }
    
    public long getEstimatedCpuInSeconds(){
        return _estimatedCpuInSeconds;
    }
    
    public void setEstimatedRunTime(long val){
        _estimatedRunTimeInSeconds = val;
    }
    
    public long getEstimatedRunTime(){
        return _estimatedRunTimeInSeconds;
    }
    
    public void setEstimatedDiskInBytes(long val){
        _estimatedDiskInBytes = val;
    }
    
    public long getEstimatedDiskInBytes(){
        return _estimatedDiskInBytes;
    }
    
    public void setCreateDate(final Date date){
        _createDate = date;
    }
    
    public Date getCreateDate(){
        return _createDate;
    }

    public void setSubmitDate(final Date date){
        _submitDate = date;
    }
    
    public Date getSubmitDate(){
        return _submitDate;
    }
    
    public void setStartDate(final Date date){
        _startDate = date;
    }
    
    public Date getStartDate(){
        return _startDate;
    }

    public void setFinishDate(final Date date){
        _finishDate = date;
    }
    
    public Date getFinishDate(){
        return _finishDate;
    }
    
    public void setParameters(List<Parameter> params){
        _parameters = params;
    }
    
    public List<Parameter> getParameters(){
        return _parameters;
    }
    
    public void setHasJobBeenSubmittedToScheduler(boolean val){
        _hasJobBeenSubmittedToScheduler = val;
    }
    
    public boolean getHasJobBeenSubmittedToScheduler(){
        return _hasJobBeenSubmittedToScheduler;
    }
    
    public void setDownloadURL(final String downloadURL){
        _downloadURL = downloadURL;
    }
    
    public String getDownloadURL(){
        return _downloadURL;
    }
    
    /**
     * Contains any high level error encountered during creation or validation
     * of Job
     * @return Error message if there was a problem or null 
     */
    public String getError() {
        return _error;
    }

    /**
     * Sets a human readable message describing an error during creation or validation
     * of this Job
     * @param error 
     */
    public void setError(String error) {
        _error = error;
    }
    
    /**
     * Gets {@link ParameterWithError} list which is a list of parameters that generated
     * errors during the validation process
     * @return List of parameters that had errors during validation
     */
    public List<ParameterWithError> getParametersWithErrors() {
        return _parametersWithErrors;
    }

    public void setParametersWithErrors(List<ParameterWithError> parametersWithErrors) {
        _parametersWithErrors = parametersWithErrors;
    }
    
    public boolean isDeleted() {
        return _deleted;
    }

    public void setDeleted(boolean deleted) {
        _deleted = deleted;
    }

    public String getDetailedError() {
        return _detailedError;
    }

    public void setDetailedError(String detailedError) {
        _detailedError = detailedError;
    }
    
    /**
     * Adds a {@link ParameterWithError} to error list.  If list is null a new one is created
     * @param param Parameter to add
     */
    @JsonIgnore
    public void addParameterWithError(ParameterWithError param){
        if (param == null){
            return;
        }
        if (_parametersWithErrors == null){
            _parametersWithErrors = new ArrayList<ParameterWithError>();
        }
        _parametersWithErrors.add(param);
    }
    
    /**
     * Examines {@link #getError()} and {@link #getParametersWithErrors()} to generate
     * a nice summary of the errors that can be logged
     * @return 
     */
    @JsonIgnore
    public String getSummaryOfErrors(){
        StringBuilder sb = new StringBuilder();
        if (_error != null){
            sb.append("JobError: ").append(_error).append("\n");
        }
        if (_parametersWithErrors != null){
            for (ParameterWithError pwe : _parametersWithErrors){
                sb.append("Parameter: ").append(pwe.asString()).append("\n");
            }
        }
        return sb.toString();
    }
    
    
}
