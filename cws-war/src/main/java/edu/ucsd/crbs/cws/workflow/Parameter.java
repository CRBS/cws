package edu.ucsd.crbs.cws.workflow;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * Instances of this class represent a parameter in a task. 
 * Parameters have a name and a value
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class Parameter {
    protected String _name;
    protected String _value;

    public Parameter(){
        
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
}
