package edu.ucsd.crbs.cws.workflow;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.condition.IfFalse;
import java.util.Date;
import java.util.List;

/**
 * Represents a {@link Workflow} Task which is an invocation of a given Workflow.
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@Entity
public class Task  {
    
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
     * Workflow object associated with this Task
     */
    public static class Everything {}
    
    @Id private Long _id;
    @Load(Task.Everything.class) private Ref<Workflow> _workflow;
    
    private String _name;
    @Index private String _owner;
    @Index private String _status;
    @Index({IfFalse.class}) private boolean _hasJobBeenSubmittedToScheduler;
    private long _estimatedCpuInSeconds;
    private long _estimatedRunTimeInSeconds;
    private long _estimatedDiskInBytes;
    private Date _createDate;
    private Date _submitDate;
    private Date _startDate;
    private Date _finishDate;
    
    private String _downloadURL;
    
    
    private List<Parameter> _parameters;
    
    /**
     * This will eventually hold a log of all events for this task
     */
    //private List<TaskLog> _taskLogs 
    
    /**
     * This will eventually hold a summary of compute and disk consumed by
     * this task
     */
    //private ComputeSummary _computeSummary
    
    public Task(){
         
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
            return;
        }
        _workflow = Ref.create(workflow);
    }
    
    public Workflow getWorkflow(){
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
    
}
