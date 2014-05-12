package edu.ucsd.crbs.cws.workflow;


/**
 * Instances of this class represent a parameter in a task. 
 * Parameters have a name and a value
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class Parameter {
    private String _name;
    private String _value;
    
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
}
