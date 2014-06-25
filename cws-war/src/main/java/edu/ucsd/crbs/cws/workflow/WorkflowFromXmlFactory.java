package edu.ucsd.crbs.cws.workflow;

import edu.ucsd.crbs.cws.rest.Constants;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;



/**
 * Instances of this class parse a 1.x kepler XML file to generate a Workflow
 * object.
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class WorkflowFromXmlFactory {

    /**
     * MoML Xpath to get basic canvas parameters
     */
    public static final String MOML_XPATH_STRING = "/entity/property[@class='ptolemy.data.expr.StringParameter']";
    public static final String MOML_XPATH_NUMBER = "/entity/property[@class='ptolemy.data.expr.Parameter']";
    public static final String MOML_XPATH_FILE = "/entity/property[@class='ptolemy.data.expr.FileParameter']";

    // Should I go this way to reduce the number of xpath queries?
    //public static final String MOML_CANVAS_PARAMETER = "/entity/property[@class='ptolemy.data.expr.StringParameter' or @class='ptolemy.data.expr.Parameter' or @class='ptolemy.data.expr.FileParameter']";
    
    /**
     * MoML Xpath to get release notes from annotation on canvas
     */
    public static final String MOML_RELEASE_NOTES = "/entity/property[@class='ptolemy.vergil.kernel.attributes.TextAttribute'][lower-case(@name)='releasenotes']/property[@name='text']";

    /**
     * MoML Xpath to get release notes from annotation on canvas
     */
    public static final String MOML_DESCRIPTION = "/entity/property[@class='ptolemy.vergil.kernel.attributes.TextAttribute'][lower-case(@name)='description']/property[@name='text']";

    
    /**
     * MoML Xpath to get release notes from annotation on canvas
     */
    public static final String MOML_WORKFLOWNAME = "/entity/property[@class='ptolemy.vergil.kernel.attributes.TextAttribute'][lower-case(@name)='workflowname']/property[@name='text']";
    
    public static final String KEPLER_PARAMETER_STRING = "String";
    public static final String KEPLER_PARAMETER_FILE = "File";
    public static final String KEPLER_PARAMETER_NUMBER = "Number";

    public static final String KEPLER_CHECKBOX_FALSE = "false";
    public static final String KEPLER_CHECKBOX_TRUE = "true";
    
    public static final String ELEMENT_NAME_KEY = "name";
    public static final String ELEMENT_VALUE_KEY = "value";
    public static final String ELEMENT_PROPERTY_KEY = "property";   
    
    public static final String ATTRIBUTE_NAME_KEY = "name";
    public static final String ATTRIBUTE_VALUE_KEY = "value";
    
   
    
    public static final HashMap<String, String> _keplerParameterTypes;

    public static final Logger log = Logger.getLogger(WorkflowFromXmlFactory.class.getName());

    static {
        _keplerParameterTypes = new HashMap<>();

        _keplerParameterTypes.put(KEPLER_PARAMETER_STRING, MOML_XPATH_STRING);
        _keplerParameterTypes.put(KEPLER_PARAMETER_NUMBER, MOML_XPATH_NUMBER);
        _keplerParameterTypes.put(KEPLER_PARAMETER_FILE, MOML_XPATH_FILE);

    }

    private InputStream _in;

    
    
    /**
     * Sets the workflow to parse when getWorkflow() is called
     *
     * @param in XML to parse
     */
    public void setWorkflowXml(InputStream in) {
        _in = in;
    }

    /**
     *
     * @return @throws Exception
     */
    public Workflow getWorkflow() throws Exception {

        Document doc;
        try {
            doc = getDocument(_in);
        } catch (Exception ex) {
            log.log(Level.SEVERE, "There was a problem parsing the workflow xml {0}", ex.getMessage());
            return null;
        }
        if (doc == null){
            log.warning("getDocument(Workflow xml input stream) returned null");
            return null;
        }
        return getWorkflowFromDocument(doc);
    }

    /**
     * Returns the root element of parsed XML document
     *
     * @param in XML document to parse
     * @return Root element of document
     * @throws Exception If there is a problem parsing the document
     */
    private Document getDocument(InputStream in) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        return builder.build(in);
    }

    /**
     * Using Xpath queries parses MOML to create Workflow object
     * @param doc Document to parse
     * @return Workflow object upon success or null if none found
     * @throws Exception If there is a parsing error or format error
     */
    private Workflow getWorkflowFromDocument(Document doc) throws Exception {
        
        // create the workflow object and set basic information
        Workflow workflow = new Workflow();
        
        addReleaseNotes(workflow,doc);
        addDescription(workflow,doc);
        addWorkflowName(workflow,doc);
        
        
        
        for (String keplerParameterType : _keplerParameterTypes.keySet()) {
            XPathExpression<Element> xpath = XPathFactory.instance().compile(_keplerParameterTypes.get(keplerParameterType),Filters.element());
            
            List<Element> elements = xpath.evaluate(doc);
            if (elements != null && elements.isEmpty() == false){
                addParametersToWorkflow(workflow,elements,keplerParameterType);
            }
        }
        return workflow;
    }
    
    private String getContentOfValueAttributeFromFirstElementInQuery(Document doc,final String query) throws Exception {
        XPathExpression<Element> xpath = XPathFactory.instance().compile(query,Filters.element());
            Element e = xpath.evaluateFirst(doc);
            if (e == null){
               return null;
            }
            return e.getAttributeValue(ATTRIBUTE_VALUE_KEY);
    }
    
    private void addReleaseNotes(Workflow workflow,Document doc) throws Exception {
       workflow.setReleaseNotes(getContentOfValueAttributeFromFirstElementInQuery(doc,MOML_RELEASE_NOTES));
    }
    
    private void addDescription(Workflow workflow,Document doc) throws Exception {
        workflow.setDescription(getContentOfValueAttributeFromFirstElementInQuery(doc,MOML_DESCRIPTION));
    }

    private void addWorkflowName(Workflow workflow,Document doc) throws Exception {
        workflow.setName(getContentOfValueAttributeFromFirstElementInQuery(doc,MOML_WORKFLOWNAME));
    }
    
    private void addParametersToWorkflow(Workflow workflow,List<Element> elements,final String keplerParameterType) throws Exception {

        List<WorkflowParameter> params = workflow.getParameters();

        //If there are no parameters for this workflow create an empty array and set it
        if (params == null){
            params = new ArrayList<>();
            workflow.setParameters(params);
        }
        for (Element e : elements){
            
            String elementNameKey = e.getAttributeValue(ELEMENT_NAME_KEY);
            WorkflowParameter wParam = new WorkflowParameter();
                
            wParam.setName(elementNameKey);
            wParam.setValue(e.getAttributeValue(ELEMENT_VALUE_KEY));
            setDisplayName(e,wParam,elementNameKey);
            setKeplerParameterAttributes(e,wParam);
            
            //if the parameter name matches one of the 4 special parameters set the type
            //to hidden cause it should not be displayed to the user
            if (elementNameKey.equalsIgnoreCase(Constants.CWS_USER) || 
                elementNameKey.equalsIgnoreCase(Constants.CWS_TASKNAME) ||
                elementNameKey.equalsIgnoreCase(Constants.CWS_OUTPUTDIR) ||
                elementNameKey.equalsIgnoreCase(Constants.CWS_TASKID)){
                wParam.setType(WorkflowParameter.Type.HIDDEN);
            }
            
            //only add parameters with a type
            if (wParam.getType() != null){
                params.add(wParam);
            }
        }
    }
    
    private void setDisplayName(Element element,WorkflowParameter wParam,final String defaultValue){
        
        Element dispElem = element.getChild("display");
            if (dispElem != null){
                wParam.setDisplayName(dispElem.getAttributeValue(ELEMENT_NAME_KEY));
                return;
            }
            
            wParam.setDisplayName(defaultValue);
    }
    
    private void setKeplerParameterAttributes(Element element,WorkflowParameter wParam) throws Exception{
         List<Element> subPropElements = element.getChildren(ELEMENT_PROPERTY_KEY);
        for (Element subEl : subPropElements) {
            if (subEl.getAttributeValue(ATTRIBUTE_NAME_KEY).equalsIgnoreCase("help")){
                wParam.setHelp(subEl.getAttributeValue(ATTRIBUTE_VALUE_KEY));
            }
            if (subEl.getAttributeValue(ATTRIBUTE_NAME_KEY).equalsIgnoreCase("type")){
                String theType = subEl.getAttributeValue(ATTRIBUTE_VALUE_KEY);
                if (theType == null){
                    throw new Exception("value of displaytype attribute for Parameter "+wParam.getName()+" is null. ");
                }
                if (WorkflowParameter.Type.isValidType(theType)){
                    wParam.setType(theType.toLowerCase());
                }
                else {
                    throw new Exception(theType+" is not a valid WorkflowParameter.Type");
                }
            }
            if (subEl.getAttributeValue(ATTRIBUTE_NAME_KEY).equalsIgnoreCase("namevaluedelimiter")) {
                wParam.setNameValueDelimiter(subEl.getAttributeValue(ATTRIBUTE_VALUE_KEY));
            }
            if (subEl.getAttributeValue(ATTRIBUTE_NAME_KEY).equalsIgnoreCase("linedelimiter")) {
                wParam.setLineDelimiter(subEl.getAttributeValue(ATTRIBUTE_VALUE_KEY));
            }
            
            if (subEl.getAttributeValue(ATTRIBUTE_NAME_KEY).equalsIgnoreCase("advanced")) {
                String advVal = subEl.getAttributeValue(ATTRIBUTE_VALUE_KEY);
                if (advVal != null) {
                    if (advVal.equals("") || advVal.equalsIgnoreCase("true")) {
                        wParam.setIsAdvanced(true);
                    } else if (advVal.equalsIgnoreCase("false")) {
                        wParam.setIsAdvanced(false);
                    }
                }
            }
            if (subEl.getAttributeValue(ATTRIBUTE_NAME_KEY).equalsIgnoreCase("required")) {
                String requiredString = subEl.getAttributeValue(ATTRIBUTE_VALUE_KEY);
                if (requiredString != null && !requiredString.trim().equals("")) {
                    if (requiredString.equalsIgnoreCase("true") || requiredString.equalsIgnoreCase("false")) {
                        wParam.setIsRequired(Boolean.valueOf(requiredString));
                    }
                }
            }
            
            if (subEl.getAttributeValue(ATTRIBUTE_NAME_KEY).equalsIgnoreCase("rows")) {
                wParam.setRows(new Long(subEl.getAttributeValue(ATTRIBUTE_VALUE_KEY)));
            }

            if (subEl.getAttributeValue(ATTRIBUTE_NAME_KEY).equalsIgnoreCase("columns")) {
                wParam.setColumns(new Long(subEl.getAttributeValue(ATTRIBUTE_VALUE_KEY)));
            }
            if (subEl.getAttributeValue(ATTRIBUTE_NAME_KEY).equalsIgnoreCase("selected")) {
                wParam.setSelected(subEl.getAttributeValue(ATTRIBUTE_VALUE_KEY));
            }
            
            //this checks for the presence of elements for validation properties
            if (subEl.getAttributeValue(ATTRIBUTE_NAME_KEY).equalsIgnoreCase("validationtype")) {
                wParam.setValidationType(subEl.getAttributeValue(ATTRIBUTE_VALUE_KEY));
            }
            if (subEl.getAttributeValue(ATTRIBUTE_NAME_KEY).equalsIgnoreCase("validationhelp")) {
                wParam.setValidationHelp(subEl.getAttributeValue(ATTRIBUTE_VALUE_KEY));
            }
            if (subEl.getAttributeValue(ATTRIBUTE_NAME_KEY).equalsIgnoreCase("minvalue")) {
                    wParam.setMinValue(Double.parseDouble(subEl.getAttributeValue(ATTRIBUTE_VALUE_KEY)));
            }
            if (subEl.getAttributeValue(ATTRIBUTE_NAME_KEY).equalsIgnoreCase("maxvalue")) {
                    wParam.setMaxValue(Double.parseDouble(subEl.getAttributeValue(ATTRIBUTE_VALUE_KEY)));
            }
            if (subEl.getAttributeValue(ATTRIBUTE_NAME_KEY).equalsIgnoreCase("validationregex")) {
                    wParam.setValidationRegex(subEl.getAttributeValue(ATTRIBUTE_VALUE_KEY));
            }
            if (subEl.getAttributeValue(ATTRIBUTE_NAME_KEY).equalsIgnoreCase("maxlength")) {
                    wParam.setMaxLength(Long.parseLong(subEl.getAttributeValue(ATTRIBUTE_VALUE_KEY)));
            }
            if (subEl.getAttributeValue(ATTRIBUTE_NAME_KEY).equalsIgnoreCase("maxfilesize")) {
                    wParam.setMaxFileSize(Long.parseLong(subEl.getAttributeValue(ATTRIBUTE_VALUE_KEY)));
            }
        }
    }

};

