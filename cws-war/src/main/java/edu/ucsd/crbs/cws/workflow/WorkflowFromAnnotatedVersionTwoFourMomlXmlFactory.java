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

import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.WorkflowFromVersionOneMomlXmlFactory;
import edu.ucsd.crbs.cws.workflow.WorkflowParameter;
import edu.ucsd.crbs.cws.workflow.kepler.ParameterAttribute;
import edu.ucsd.crbs.cws.workflow.kepler.RectangleAttribute;
import edu.ucsd.crbs.cws.workflow.kepler.TextAttribute;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

/**
 * Creates Workflow object from Kepler 2.4 XML document.  This factory
 * uses the new annotation markup for describing UI mapping for Kepler
 * parameters
 * 
 * 
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class WorkflowFromAnnotatedVersionTwoFourMomlXmlFactory {

    // Query to find all Parameters
    public static final String MOML_CANVAS_PARAMETER = "/entity/property[@class='ptolemy.data.expr.StringParameter' or @class='ptolemy.data.expr.Parameter' or @class='ptolemy.data.expr.FileParameter']";

    // Query to find all RectangleAttributes
    public static final String MOML_RECTANGLE_ATTRIBUTE = "/entity/property[@class='ptolemy.vergil.kernel.attributes.RectangleAttribute']";
    
    // Query to find all TextAttributes
    public static final String MOML_TEXT_ATTRIBUTE = "/entity/property[@class='ptolemy.vergil.kernel.attributes.TextAttribute']";
    
    public static final String NAME_ATTRIBUTE = "name";

    public static final String VALUE_ATTRIBUTE = "value";
    
    public static final String LOCATION_ATTRIBUTE = "_location";
    
    public static final String DISPLAY_ELEMENT = "display";
    
    public static final String CENTERED_ATTRIBUTE = "centered";
    
    public static final String WIDTH = "width";
    
    public static final String HEIGHT = "height";
    
    public static final String CLASS = "class";
    
    public static final String TEXT = "text";
    
    
    public static final String WORKFLOW_NAME="workflowname";
    public static final String RELEASE_NOTES="releasenotes";
    public static final String DESCRIPTION="description";
    public static final String AUTHOR = "author";
    
    public static final String TYPE_KEY = "type";
    public static final String HELP_KEY = "help";
    public static final String VALIDATIONHELP_KEY = "validationhelp";
    public static final String VALIDATIONTYPE_KEY = "validationtype";
    public static final String VALIDATIONREGEX_KEY = "validationregex";
    public static final String NAMEVALUEDELIMITER_KEY = "namevaluedelimiter";
    public static final String LINEDELIMITER_KEY = "linedelimiter";
    public static final String SELECTED_KEY = "selected";
    public static final String COLUMNS_KEY = "columns";
    public static final String ISADVANCED_KEY = "isadvanced";
    public static final String ISREQUIRED_KEY = "isrequired";
    public static final String MAXFILESIZE_KEY = "maxfilesize";
    public static final String MAXLENGTH_KEY = "maxlength";
    public static final String MAXVALUE_KEY = "maxvalue";
    public static final String MINVALUE_KEY = "minvalue";
    public static final String ROWS_KEY = "rows";
    public static final String ALLOWED_WORKSPACEFILE_TYPES = "allowedworkspacefiletypes";
    public static final String ALLOW_FAILED_WORKSPACEFILE = "allowfailedworkspacefile";
    

    public static final Logger log = Logger.getLogger(WorkflowFromVersionOneMomlXmlFactory.class.getName());

    /**
     * Input Stream set via {@link #setWorkflowXml(java.io.InputStream)} 
     */
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
     * Parses Kepler 2.4 xml from stream set in {@link #setWorkflowXml(java.io.InputStream)}
     * to extract a {@link Workflow} object.  
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
        if (doc == null) {
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
     *
     * @param doc Document to parse
     * @return Workflow object upon success or null if none found
     * @throws Exception If there is a parsing error or format error
     */
    private Workflow getWorkflowFromDocument(Document doc) throws Exception {

        // create the workflow object and set basic information
        Workflow workflow = new Workflow();

        // find all RectangleAttributes and get name, display name, and locations
        List<RectangleAttribute> rectangles = getRectangleAttributes(doc);
        if (rectangles == null || rectangles.isEmpty()) {
           return workflow;
        }
        
        // find all parameters and record type, name, display name, value, and location
        List<ParameterAttribute> parameters = getParameterAttributes(doc);
        
        // find all annotations and record name, display name, text, and location
        List<TextAttribute> textAttributes = getTextAttributes(doc);

        //attach Text Attributes to Rectangles
        if (textAttributes != null && textAttributes.isEmpty() == false) {
            for (RectangleAttribute rectangle : rectangles) {
                Iterator textIterator = textAttributes.iterator();
                for (; textIterator.hasNext();) {
                    TextAttribute ta = (TextAttribute) textIterator.next();
                    rectangle.addTextAttributeIfIntersecting(ta);
                }

            }
        }
        
        //attach all parameters to Rectangles
        if (parameters != null && parameters.isEmpty() == false) {
            for (RectangleAttribute rectangle : rectangles) {

                Iterator paramIterator = parameters.iterator();
                for (; paramIterator.hasNext();) {
                    ParameterAttribute pa = (ParameterAttribute) paramIterator.next();
                    rectangle.addParameterAttributeIfIntersecting(pa);
                }
            }
        }
        
        ArrayList<WorkflowParameter> paramList = new ArrayList<>();
        //loop through RectangleAttributes and update workflow object with parameters etc
        
        for (RectangleAttribute ra : rectangles) {
                if (doesRectangleNameMatch(ra,WORKFLOW_NAME)){
                    workflow.setName(ra.getTextFromTextAttributes());
                }
                else if (doesRectangleNameMatch(ra,RELEASE_NOTES)){
                    workflow.setReleaseNotes(ra.getTextFromTextAttributes());
                }
                else if (doesRectangleNameMatch(ra,DESCRIPTION)){
                    workflow.setDescription(ra.getTextFromTextAttributes());
                }
                else if (doesRectangleNameMatch(ra,AUTHOR)){
                    workflow.setAuthor(ra.getTextFromTextAttributes());
                }

                else {
                    List<WorkflowParameter> wpList = getWorkflowParameters(ra);
                    if (wpList != null && wpList.isEmpty() == false){
                        paramList.addAll(wpList);
                    }
                }
        }
        if (paramList.isEmpty() == false){
            workflow.setParameters(paramList);
        }
        
        return workflow;
    }
    
    
    /**
     * Invokes {@link #doSpaceRemovedStringsMatch()} on {@link RectangleAttribute#getDisplayName()} and
     * {@link RectangleAttribute#getName()} returning true if either return true
     * @param ra RectangleAttribute to obtain display name and name from
     * @param name name to compare against the display name and name
     * @return true if either the name or display name in <b>ra</b> match otherwise false
     */
    private boolean doesRectangleNameMatch(RectangleAttribute ra,final String name){
        if (ra == null){
            return false;
        }
        return doSpaceRemovedStringsMatch(ra.getDisplayName(),name) || doSpaceRemovedStringsMatch(ra.getName(),name);
    }
    
    /**
     * Removes any spaces from <b>base</b> and then performs an caseless comparison
     * against <b>match</b> string
     * 
     * @param base Base string that has spaces removed
     * @param match String to compare against base string
     * @return true if space removed <b>base</b> matches <b>match</b> string 
     * using the string equalsIgnoreCase method. false if no match or if either 
     * inputs are null
     */
    private boolean doSpaceRemovedStringsMatch(final String base,final String match){
        if (base == null || match == null){
            return false;
        }
        return base.replace(" ","").equalsIgnoreCase(match);
    }
    
    /**
     * Generates @{link WorkflowParameter} objects by iterating through all the {@link ParameterAttribute} objects
     * in <b>ra</b> object passed in.  Each @{link WorkflowParameter} is annotated with data from
     * the {@link TextAttribute} in the <b>ra</b> extracted from {@link RectangleAttribute#getTextFromTextAttributes()}<br/>
     * It is assumed the data from the preceeding method is in the <b>key=value<b/> format compatible
     * with parsing by {@link java.util.Properties}.  This method looks for the following
     * key words:<p/>
     * {@link #TYPE_KEY}<br/>
     * {@link #HELP_KEY}<br/>
     * {@link #VALIDATIONHELP_KEY}<br/>
     * {@link #VALIDATIONTYPE_KEY}<br/>
     * {@link #VALIDATIONREGEX_KEY}<br/>
     * {@link #NAMEVALUEDELIMITER_KEY}<br/>
     * {@link #LINEDELIMITER_KEY}<br/>
     * {@link #SELECTED_KEY}<br/>
     * {@link #COLUMNS_KEY}<br/>
     * {@link #ISADVANCED_KEY}<br/>
     * {@link #ISREQUIRED_KEY}<br/>
     * {@link #MAXFILESIZE_KEY}<br/>
     * {@link #MAXLENGTH_KEY}<br/>
     * {@link #MAXVALUE_KEY}<br/>
     * {@link #MINVALUE_KEY}<br/>
     * {@link #ROWS_KEY}<br/>
     * 
     * @param ra
     * @return
     * @throws Exception 
     */
    private List<WorkflowParameter> getWorkflowParameters(RectangleAttribute ra) throws Exception {
        
        // if there isn't a text attribute text then these parameters shouldn't
        // be displayed to the user so just return
        String text = ra.getTextFromTextAttributes();
        if (text == null){
            return null;
        }

        //bail if there are no parameters
        if (ra.getIntersectingParameterAttributes() == null ||
                ra.getIntersectingParameterAttributes().isEmpty()){
            return null;
        }
        
        //attempt to extract properties from text attributes
        Properties props = new Properties();
        props.load(new StringReader(text));
        String type = props.getProperty(TYPE_KEY);
        if (type == null){
            return null;
        }
        
        ArrayList<WorkflowParameter> params = new ArrayList<>();
        
        for (ParameterAttribute pa : ra.getIntersectingParameterAttributes()){
            WorkflowParameter wp = new WorkflowParameter();
            
            wp.setName(pa.getName());
            wp.setDisplayName(pa.getDisplayName());
            wp.setType(type);
            wp.setValue(pa.getValue());
            
            wp.setHelp(props.getProperty(HELP_KEY));
            wp.setValidationHelp(props.getProperty(VALIDATIONHELP_KEY));
            wp.setValidationType(props.getProperty(VALIDATIONTYPE_KEY));
            wp.setValidationRegex(props.getProperty(VALIDATIONREGEX_KEY));
            wp.setNameValueDelimiter(props.getProperty(NAMEVALUEDELIMITER_KEY));
            wp.setLineDelimiter(props.getProperty(LINEDELIMITER_KEY));
            wp.setSelected(props.getProperty(SELECTED_KEY));
            
            wp.setColumns(Long.parseLong(props.getProperty(COLUMNS_KEY,"0")));
            wp.setIsAdvanced(Boolean.valueOf(props.getProperty(ISADVANCED_KEY, "false")));
            wp.setIsRequired(Boolean.valueOf(props.getProperty(ISREQUIRED_KEY, "false")));
            wp.setMaxFileSize(Long.parseLong(props.getProperty(MAXFILESIZE_KEY, "0")));
            wp.setMaxLength(Long.parseLong(props.getProperty(MAXLENGTH_KEY, "0")));
            wp.setMaxValue(Double.parseDouble(props.getProperty(MAXVALUE_KEY,"0")));
            wp.setMinValue(Double.parseDouble(props.getProperty(MINVALUE_KEY,"0")));
            wp.setRows(Long.parseLong(props.getProperty(ROWS_KEY, "0")));
            
            wp.setAllowFailedWorkspaceFile(Boolean.valueOf(props.getProperty(ALLOW_FAILED_WORKSPACEFILE,"false")));
            wp.setAllowedWorkspaceFileTypes(props.getProperty(ALLOWED_WORKSPACEFILE_TYPES));
            
            params.add(wp);
        }
        return params;
    }
    
    /**
     * Runs xpath query {@link #MOML_TEXT_ATTRIBUTE} to get TextAttribute 
     * elements from document.  The code then parses out the <b>name</b>, 
     * <b>coordinates<b/>, and <b>text</b> to generate {@link TextAttribute} 
     * objects.<p/>
     * The <b>name</b> is the value from the {@link #NAME_ATTRIBUTE} attribute 
     * in the element returned from the xpath query<br/>
     * The <b>coordinates</b> is the value obtained from the 
     * {@link #VALUE_ATTRIBUTE} in the  child element whose value from the 
     * {@link #NAME_ATTRIBUTE} matches {@link #LOCATION_ATTRIBUTE}<br/>
     * The <b>text</b> is the value from the {@link #VALUE_ATTRIBUTE} attribute 
     * in the child element whose value from the {@link #NAME_ATTRIBUTE} matches 
     * {@link #TEXT}
     * 
     * @param doc
     * @return List of {@link TextAttribute} objects if found
     * @throws Exception 
     */
    private List<TextAttribute> getTextAttributes(Document doc) throws Exception {
        XPathExpression<Element> xpath = XPathFactory.instance().compile(MOML_TEXT_ATTRIBUTE, Filters.element());

        List<Element> elements = xpath.evaluate(doc);
        ArrayList<TextAttribute> textAttributes = new ArrayList<>();
        for (Element e : elements) {
            TextAttribute ta = new TextAttribute();

            ta.setName(e.getAttributeValue(NAME_ATTRIBUTE));
            List<Element> children = e.getChildren();
            for (Element child : children) {
                if (child.getAttributeValue(NAME_ATTRIBUTE).equalsIgnoreCase(LOCATION_ATTRIBUTE)) {
                    ta.setCoordinatesViaString(child.getAttributeValue(VALUE_ATTRIBUTE));
                }
                else if (child.getAttributeValue(NAME_ATTRIBUTE).equalsIgnoreCase(TEXT)){
                    ta.setText(child.getAttributeValue(VALUE_ATTRIBUTE));
                }
            }
            textAttributes.add(ta);
        }
        return textAttributes;
    }

    private List<ParameterAttribute> getParameterAttributes(Document doc) throws Exception {
        XPathExpression<Element> xpath = XPathFactory.instance().compile(MOML_CANVAS_PARAMETER, Filters.element());
        List<Element> elements = xpath.evaluate(doc);
        ArrayList<ParameterAttribute> parameters = new ArrayList<>();
        for (Element e : elements) {
            ParameterAttribute pa = new ParameterAttribute();
            pa.setName(e.getAttributeValue(NAME_ATTRIBUTE));
            pa.setType(e.getAttributeValue(CLASS));
            pa.setValue(e.getAttributeValue(VALUE_ATTRIBUTE));

            List<Element> children = e.getChildren();
            for (Element child : children) {
                if (child.getName().equals(DISPLAY_ELEMENT)) {
                    pa.setDisplayName(child.getAttributeValue(NAME_ATTRIBUTE));
                } else if (child.getAttributeValue(NAME_ATTRIBUTE).equalsIgnoreCase(LOCATION_ATTRIBUTE)) {
                    pa.setCoordinatesViaString(child.getAttributeValue(VALUE_ATTRIBUTE));
                }

            }
            if (pa.getDisplayName() == null) {
                pa.setDisplayName(pa.getName());
            }
            parameters.add(pa);
        }
        return parameters;
    }

    private List<RectangleAttribute> getRectangleAttributes(Document doc) throws Exception {
        XPathExpression<Element> xpath = XPathFactory.instance().compile(MOML_RECTANGLE_ATTRIBUTE, Filters.element());

        List<Element> elements = xpath.evaluate(doc);
        ArrayList<RectangleAttribute> rectangles = new ArrayList<>();
        for (Element e : elements) {
            RectangleAttribute ra = new RectangleAttribute();
            boolean centered = false;
            ra.setName(e.getAttributeValue(NAME_ATTRIBUTE));
            List<Element> children = e.getChildren();
            for (Element child : children) {
                if (child.getName().equals(DISPLAY_ELEMENT)) {
                    ra.setDisplayName(child.getAttributeValue(NAME_ATTRIBUTE));
                } else if (child.getAttributeValue(NAME_ATTRIBUTE).equalsIgnoreCase(WIDTH)) {
                    ra.setWidth(Double.parseDouble(child.getAttributeValue(VALUE_ATTRIBUTE)));
                } else if (child.getAttributeValue(NAME_ATTRIBUTE).equalsIgnoreCase(HEIGHT)) {
                    ra.setHeight(Double.parseDouble(child.getAttributeValue(VALUE_ATTRIBUTE)));
                } else if (child.getAttributeValue(NAME_ATTRIBUTE).equalsIgnoreCase(LOCATION_ATTRIBUTE)) {
                    ra.setCoordinatesViaString(child.getAttributeValue(VALUE_ATTRIBUTE));
                } else if (child.getAttributeValue(NAME_ATTRIBUTE).equalsIgnoreCase(CENTERED_ATTRIBUTE)){
                    centered = Boolean.parseBoolean(child.getAttributeValue(VALUE_ATTRIBUTE));
                }
            }
            if (ra.getDisplayName() == null) {
                ra.setDisplayName(ra.getName());
            }
            
            //if centered we need to adjust x and y coordinate to be upper left corner
            if (centered == true){
                ra.moveCoordinatesToUpperLeftCornerFromCenter();
            }
            
            rectangles.add(ra);
        }
        return rectangles;
    }
};
