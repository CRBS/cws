package edu.ucsd.crbs.cws.workflow;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.googlecode.objectify.Ref;
import java.util.List;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Load;
import java.util.Date;


/**
 * Represents a Workflow which is basically a program that does something.
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@Entity
@JsonPropertyOrder(value={ "id","name","createDate","version","description","releaseNotes"},alphabetic=true)
public class Workflow {
    
    public static boolean REFS_ENABLED = true;

    
    public static class Everything {}
    @Id private Long _id;
    private String _name;
    private int _version;
    private String _description;
    private Date _createDate;
    private String _releaseNotes;
    private List<WorkflowParameter> _parameters;
    private @Load(Workflow.Everything.class) Ref<Workflow> _parent;
    @Ignore private Workflow _rawParent;
    @Ignore private String _workflowFileUploadURL;
    
    public Workflow(){
        
    }
        
    public Long getId(){
        return _id;
    }
    public void setId(Long id){
       _id = id;
    }
    
    public void setVersion(int val){
        _version = val;
    }
    
    public int getVersion(){
        return _version;
    }
    
     public void setParentWorkflow(Workflow workflow){
        if (workflow == null){
            _parent = null;
            _rawParent = null;
            return;
        }
        if (REFS_ENABLED){
            _parent = Ref.create(workflow);
        }
        _rawParent = workflow;
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
}
