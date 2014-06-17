package edu.ucsd.crbs.cws.workflow;

import java.util.Map;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

/**
 * Represents an input Parameter for a Workflow.
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */

@JsonPropertyOrder(value={ "name","displayName","type","value"}, alphabetic=true)
public class WorkflowParameter {
    
    /**
     * Contains list of supported WorkflowParameter types.  The current type
     * for a given WorkflowParameter object can be obtained by calling
     * {@link #getType() }
     */
    public static class Type {
        /**
         * Denotes a hidden field which shouldn't be displayed to the user
         */
        public static final String HIDDEN = "hidden";
        
        /**
         * Text field same as in HTML
         */
        public static final String TEXT = "text";
        
        /**
         * Text field same as in HTML
         */
        public static final String TEXT_AREA = "textarea";
        
        /**
         * File field same as in HTML
         */
        public static final String FILE = "file";
        
        /**
         * Check box field same as in HTML
         */
        public static final String CHECK_BOX = "checkbox";
        
        /**
         * Drop down field maps to "select" in HTML
         */
        public static final String DROP_DOWN = "dropdown";
        
        /**
         * Denotes this field should be a list of values preferably displayed
         * in a drop down menu. @TODO NEED TO IMPLEMENT THIS
         */
        //public static final String DROP_DOWN = "dropdown";

        /**
         * Checks if type passed in is valid.  The comparison is case insensitive.
         * @param type Type to check
         * @return true if its a valid, false otherwise
         */
        public static boolean isValidType(final String type){
            if (type == null){
                return false;
            }
            
            return type.equalsIgnoreCase(HIDDEN) ||
                    type.equalsIgnoreCase(TEXT) ||
                    type.equalsIgnoreCase(FILE) ||
                    type.equalsIgnoreCase(CHECK_BOX) ||
                    type.equalsIgnoreCase(TEXT_AREA) ||
                    type.equalsIgnoreCase(DROP_DOWN);
        }
    }

    /**
     * Contains list of valid Basic Validation types
     */
    public static class ValidationType {
        
        /**
         * Allow decimal numbers
         */
        public static final String NUMBER = "number";
        
        /**
         * Allow only digits that are whole numbers ie (1,2,3,34,-1,-10). 
         * A (-) at the start is allowed to denote negative numbers
         */
        public static final String DIGITS = "digits";
        
        /**
         * Allows any ASCII characters
         */
        public static final String STRING = "string";
    }
    
    
    protected String _name;
    private String _displayName;
    protected String _value;
    private Map<String,String> _valueMap;
    private String _type; //formerly display type
    private String _help; //formerly tooltip/popup
    private boolean _isAdvanced;
    private boolean _isRequired;
    private String _delimiterValue;
    private long _rows;
    private long _columns;
    private String _validationType;  //formerly inputType
    private String _validationHelp; //formerly inputTypeHelp
    private double _maxValue;
    private double _minValue;
    private long _maxLength;
    private String _regex;
    private long _maxFileSize;
    
    public WorkflowParameter(){
        
    }
    
    /**
     * Sets the name that should be displayed for this parameter
     * @param displayName 
     */
    public void setDisplayName(final String displayName){
        _displayName = displayName;
    }
    
    /**
     * Gets the name that should be displayed for this parameter
     * @return 
     */
    public String getDisplayName(){
        return _displayName;
    }
    
    /**
     * Sets the internal name for this parameter
     * @param name 
     */
    public void setName(final String name){
        _name = name;
    }
    
    /**
     * Gets the internal name for this parameter
     * @return 
     */
    public String getName(){
        return _name;
    }
        
    /**
     * Sets the value for this parameter which depending on type
     * could be the default value or contain data used to generate
     * a list that is presented to the user
     * @param value 
     */
    public void setValue(final String value){
        _value = value;
    }
    
    /**
     * Gets the value for this parameter which depending on type
     * could be the default value or contain data used to generate
     * a list that is presented to the user.
     * 
     * @return Value of Parameter
     */
    public String getValue(){
        return _value;
    }
    
    /**
     * Sets the type of parameter this is, currently supported types are
     * text,number,file,hidden,and dropdown
     * 
     * @param type 
     */
    public void setType(final String type){
        _type = type;
    }
    
    /**
     * Gets the type of parameter
     * @return 
     */
    public String getType(){
        return _type;
    }
    
    /**
     * Sets help message to display to user for this parameter
     * @param help 
     */
    public void setHelp(final String help){
        _help = help;
    }

    /**
     * Gets help message to display to user for this parameter
     * @return 
     */
    public String getHelp() {
        return _help;
    }
    
    /**
     * Sets whether this is parameter should be considered an advanced
     * option suitable for configuration by power users
     * @param val 
     */
    public void setIsAdvanced(boolean val){
        _isAdvanced = val;
    }
    
    /**
     * Gets whether this parameter is considered an advanced option
     * @return 
     */
    public boolean getIsAdvanced(){
        return _isAdvanced;
    }
    
    /**
     * Sets whether this parameter is required to be set by user
     * @param val 
     */
    public void setIsRequired(boolean val){
        _isRequired = val;
    }
    
    /**
     * Gets whether this parameter is required to be set by user
     * @return true if Parameter is required to be set by user otherwise false
     */
    public boolean getIsRequired(){
        return _isRequired;
    }
    
    /**
     * Sets the column delimiter value to use in the case {@link #getType()}
     * is dropdown
     * @param val 
     */ 
    public void setDelimiterValue(final String val){
        _delimiterValue = val;
    }
    
    /**
     * Gets the delimiter to use in the case {@link #getType() } is dropdown
     * @return 
     */
    public String getDelimiterValue(){
        return _delimiterValue;
    }
    
    /**
     * Sets the type of validation that should be performed on the data.
     * See {@link ValidationType} for list of valid types
     * @param val 
     */
    public void setValidationType(final String val){
        _validationType = val;
    }
    
    public String getValidationType(){
        return _validationType;
    }
    
    /**
     * Sets message to display to user if validation fails on input set
     * by user
     * @param val 
     */
    public void setValidationHelp(final String val){
        _validationHelp = val;
    }

    /**
     * Gets message to display to user if validation fails on input set by user 
     * @return 
     */
    public String getValidationHelp(){
        return _validationHelp;
    }
    
    /**
     * Sets maximum allowed value for Parameter.  Used in Validation for validation
     * types number, digits, and signedinteger.
     * 
     * @param val 
     */
    public void setMaxValue(double val){
        _maxValue = val;
    }
    
    /**
     * Gets maximum allowed value for parameter.  Used in Validation for validation
     * types number, digits, and signedinteger.
     * @return 
     */
    public double getMaxValue(){
        return _maxValue;
    }
    
    /**
     * Sets minimum allowed value for Parameter.  Used in Validation for validation
     * types number, digits, and signedinteger.
     * @param val 
     */
    public void setMinValue(double val){
        _minValue = val;
    }
    
    /**
     * Gets minimum allowed value for parameter.  Used in Validation for validation
     * types number, digits, and signedinteger.
     * @return 
     */
    public double getMinValue(){
        return _minValue;
    }
    
    /**
     * Sets max length in characters allowed for Parameter.  Used in Validation for validation
     * type string
     * @param maxlen 
     */
    public void setMaxLength(long maxlen){
        _maxLength = maxlen;
    }
    
    /**
     * Gets max length in characters allowed for Parameter.  Used in Validation for validation
     * type string
     * @return 
     */
    public long getMaxLength(){
        return _maxLength;
    }
    
    /**
     * Sets number of rows allowed for textarea parameter.  Used to indicate dimension
     * of textarea to display to user.
     * @param rows 
     */
    public void setRows(long rows){
        _rows = rows;
    }
    
    /**
     * Gets number of rows allowed for textarea parameter.  Used to indicate dimension
     * of textarea to display to user.
     * @return 
     */
    public long getRows(){
        return _rows;
    }
    

    /**
     * Sets number of columns allowed for textarea parameter.  Used to indicate dimension
     * of textarea to display to user.
     * @param columns
     */
    public void setColumns(long columns){
        _columns = columns;
    }
    
    /**
     * Gets number of columns allowed for textarea parameter.  Used to indicate dimension
     * of textarea to display to user.
     * @return 
     */
    public long getColumns(){
        return _columns;
    }

    
    /**
     * Sets validation regular expression for Parameter.  Used in Validation for 
     * validation type string, digits
     * @param regex 
     */
    public void setValidationRegex(final String regex){
        _regex = regex;
    }
    
    
    /**
     * Gets validation regular expression for Parameter.  Used in Validation for 
     * validation type string, digits
     * @return 
     */
    public String getValidationRegex(){
        return _regex;
    }
    
    /**
     * Sets maximum allowed file size for Parameter.  Used in Validation and only
     * applies to Parameters of type file.
     * @param fileSize 
     */
    public void setMaxFileSize(long fileSize){
        _maxFileSize = fileSize;
    }
    
    /**
     * Gets maximum allowed file size for Parameter.  Used in Validation and only
     * applies to Parameters of type file.
     * @return 
     */
    public long getMaxFileSize(){
        return _maxFileSize;
    }
    
    /**
     * Sets a map of (display name)=>(values) that will be presented
     * to the user in some sort of table.
     * @param valueMap 
     */
    public void setValueMap(Map<String,String> valueMap){
        _valueMap = valueMap;
    }
    
    /**
     * Gets a map of (display name)=>(values) that will be presented
     * to the user in some sort of table
     * @return 
     */
    public Map<String,String> getValueMap(){
        return _valueMap;
    }
}
    
