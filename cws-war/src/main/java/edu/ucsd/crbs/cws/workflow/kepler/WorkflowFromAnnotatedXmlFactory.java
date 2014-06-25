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
package edu.ucsd.crbs.cws.workflow.kepler;

import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.WorkflowFromXmlFactory;
import edu.ucsd.crbs.cws.workflow.WorkflowParameter;
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
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class WorkflowFromAnnotatedXmlFactory {

    // Query to find all Parameters
    public static final String MOML_CANVAS_PARAMETER = "/entity/property[@class='ptolemy.data.expr.StringParameter' or @class='ptolemy.data.expr.Parameter' or @class='ptolemy.data.expr.FileParameter']";

    // Query to find all RectangleAttributes
    public static final String MOML_RECTANGLE_ATTRIBUTE = "/entity/property[@class='ptolemy.vergil.kernel.attributes.RectangleAttribute']";
    
    // Query to find all TextAttributes
    public static final String MOML_TEXT_ATTRIBUTE = "/entity/property[@class='ptolemy.vergil.kernel.attributes.TextAttribute']";

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


    public static final Logger log = Logger.getLogger(WorkflowFromXmlFactory.class.getName());

   

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
                if (ra.getDisplayName().replace(" ","").equalsIgnoreCase("workflowname") ||
                    ra.getName().replace(" ","").equalsIgnoreCase("workflowname")){
                    workflow.setName(ra.getTextFromTextAttributes());
                }
                else if (ra.getDisplayName().replace(" ","").equalsIgnoreCase("releasenotes") ||
                    ra.getName().replace(" ","").equalsIgnoreCase("releasenotes")){
                    workflow.setReleaseNotes(ra.getTextFromTextAttributes());
                }
                else if (ra.getDisplayName().replace(" ","").equalsIgnoreCase("description") ||
                    ra.getName().replace(" ","").equalsIgnoreCase("description")){
                    workflow.setDescription(ra.getTextFromTextAttributes());
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
        
        
        
        //iterate through rectangles
        // if key word matches Description, Workflow Name, or Release Notes set appropriate Workflow fields
        // by reading contents of associated annotation
        //if rectangle has one or more parameters and annotation attempt to parse annotation
        // for type information.  If set apply data to parameters and add them to workflow
        return workflow;
    }
    
    
    private List<WorkflowParameter> getWorkflowParameters(RectangleAttribute ra) throws Exception {
        
        String text = ra.getTextFromTextAttributes();
        if (text == null){
            return null;
        }

        if (ra.getIntersectingParameterAttributes() == null || ra.getIntersectingParameterAttributes().isEmpty()){
            return null;
        }
        
        Properties props = new Properties();
        props.load(new StringReader(text));
        String type = props.getProperty("type");
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
            
            wp.setHelp(props.getProperty("help"));
            wp.setValidationHelp(props.getProperty("validationhelp"));
            wp.setValidationType(props.getProperty("validationtype"));
            wp.setValidationRegex(props.getProperty("validationregex"));
            wp.setNameValueDelimiter(props.getProperty("namevaluedelimiter"));
            wp.setLineDelimiter(props.getProperty("linedelimiter"));
            wp.setSelected(props.getProperty("selected"));
            
            wp.setColumns(Long.parseLong(props.getProperty("columns","0")));
            wp.setIsAdvanced(Boolean.valueOf(props.getProperty("isadvanced", "false")));
            wp.setIsRequired(Boolean.valueOf(props.getProperty("isrequired", "false")));
            wp.setMaxFileSize(Long.parseLong(props.getProperty("maxfilesize", "0")));
            wp.setMaxLength(Long.parseLong(props.getProperty("maxlength", "0")));
            wp.setMaxValue(Double.parseDouble(props.getProperty("maxvalue","0")));
            wp.setMinValue(Double.parseDouble(props.getProperty("minvalue","0")));
            wp.setRows(Long.parseLong(props.getProperty("rows", "0")));
            
            params.add(wp);
        }
        return params;
    }
    
    private List<TextAttribute> getTextAttributes(Document doc) throws Exception {
        XPathExpression<Element> xpath = XPathFactory.instance().compile(MOML_TEXT_ATTRIBUTE, Filters.element());

        List<Element> elements = xpath.evaluate(doc);
        ArrayList<TextAttribute> textAttributes = new ArrayList<>();
        for (Element e : elements) {
            TextAttribute ta = new TextAttribute();

            ta.setName(e.getAttributeValue(ELEMENT_NAME_KEY));
            List<Element> children = e.getChildren();
            for (Element child : children) {
                if (child.getAttributeValue(ELEMENT_NAME_KEY).equalsIgnoreCase("_location")) {
                    ta.setCoordinatesViaString(child.getAttributeValue(ATTRIBUTE_VALUE_KEY));
                }
                else if (child.getAttributeValue(ELEMENT_NAME_KEY).equalsIgnoreCase("text")){
                    ta.setText(child.getAttributeValue(ATTRIBUTE_VALUE_KEY));
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
            pa.setName(e.getAttributeValue(ELEMENT_NAME_KEY));
            pa.setType(e.getAttributeValue("class"));
            pa.setValue(e.getAttributeValue(ATTRIBUTE_VALUE_KEY));

            List<Element> children = e.getChildren();
            for (Element child : children) {
                if (child.getName().equals("display")) {
                    pa.setDisplayName(child.getAttributeValue("name"));
                } else if (child.getAttributeValue(ELEMENT_NAME_KEY).equalsIgnoreCase("_location")) {
                    pa.setCoordinatesViaString(child.getAttributeValue(ATTRIBUTE_VALUE_KEY));
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

            ra.setName(e.getAttributeValue(ELEMENT_NAME_KEY));
            List<Element> children = e.getChildren();
            for (Element child : children) {
                if (child.getName().equals("display")) {
                    ra.setDisplayName(child.getAttributeValue("name"));
                } else if (child.getAttributeValue(ELEMENT_NAME_KEY).equalsIgnoreCase("width")) {
                    ra.setWidth(Double.parseDouble(child.getAttributeValue(ATTRIBUTE_VALUE_KEY)));
                } else if (child.getAttributeValue(ELEMENT_NAME_KEY).equalsIgnoreCase("height")) {
                    ra.setHeight(Double.parseDouble(child.getAttributeValue(ATTRIBUTE_VALUE_KEY)));
                } else if (child.getAttributeValue(ELEMENT_NAME_KEY).equalsIgnoreCase("_location")) {
                    ra.setCoordinatesViaString(child.getAttributeValue(ATTRIBUTE_VALUE_KEY));
                }
            }
            if (ra.getDisplayName() == null) {
                ra.setDisplayName(ra.getName());
            }
            rectangles.add(ra);
        }
        return rectangles;
    }

    private String getContentOfValueAttributeFromFirstElementInQuery(Document doc, final String query) throws Exception {
        XPathExpression<Element> xpath = XPathFactory.instance().compile(query, Filters.element());
        Element e = xpath.evaluateFirst(doc);
        if (e == null) {
            return null;
        }
        return e.getAttributeValue(ATTRIBUTE_VALUE_KEY);
    }
};
