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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Rectangle in a Kepler 2.4 workflow
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class RectangleAttribute extends Attribute {

    private List<TextAttribute> _intersectingTextAttributes;
    private List<ParameterAttribute> _intersectingParameterAttributes;
    private double _width;
    private double _height;

    public RectangleAttribute(){
        super();
    }
    
    public double getWidth() {
        return _width;
    }

    public void setWidth(double width) {
        _width = width;
    }

    public double getHeight() {
        return _height;
    }

    public void setHeight(double height) {
        _height = height;
    }
    
    public boolean addTextAttributeIfIntersecting(TextAttribute attrib){
        if (isItOkayToAddAttribute(attrib) == false){
            return false;
        }
        
        if (_intersectingTextAttributes == null){
            _intersectingTextAttributes = new ArrayList<>();
        }
        _intersectingTextAttributes.add(attrib);
        return true;
    }
    
    public List<TextAttribute> getIntersectingTextAttributes() {
        return _intersectingTextAttributes;
    }

    private boolean isItOkayToAddAttribute(Attribute attribute){
           if (attribute == null){
            return false;
        }
        
        if (doesIntersect(attribute) == false){
            return false;
        }
        return true;
    }
    
    public boolean addParameterAttributeIfIntersecting(ParameterAttribute attrib){
        if (isItOkayToAddAttribute(attrib) == false){
            return false;
        }
        
        if (_intersectingParameterAttributes == null){
            _intersectingParameterAttributes = new ArrayList<>();
        }
        _intersectingParameterAttributes.add(attrib);
        return true;
    }
    
    public List<ParameterAttribute> getIntersectingParameterAttributes() {
        return _intersectingParameterAttributes;
    }
    
    /**
     * Checks if the <b>attrib</b> passed in is located within the rectangle of
     * this object.  The code assumes the coordinates are based on the upper left
     * corner and not centered.
     * @param attrib Attribute whose coordinates are extracted and compared to this object
     * @return true if the <b>attrib</b> lies within the bounds of this object's location false otherwise
     */
    public boolean doesIntersect(Attribute attrib){
        if (attrib == null){
            return false;
        }
        
        //fail if coordinate is above or to left of upper left corner of rectangle
        if (attrib.getXCoordinate() < _xCoordinate ||
            attrib.getYCoordinate() < _yCoordinate){
            return false;
        }
        
        // fail if coordinate is below or to right of lower right corner of rectangle
        if (attrib.getXCoordinate() > _xCoordinate + _width ||
            attrib.getYCoordinate() > _yCoordinate + _height){
            return false;
        }
        return true;
    }
    
    public String getTextFromTextAttributes(){
        if (_intersectingTextAttributes == null || 
            _intersectingTextAttributes.isEmpty()){
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        for (TextAttribute ta : _intersectingTextAttributes){
            if (sb.length() > 0){
                sb.append("\n");
            }
            if (ta.getText() != null){
                sb.append(ta.getText());
            }
        }
        return sb.toString();
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public String asString(){
        StringBuilder sb = new StringBuilder();
        sb.append(super.asString());
        sb.append(",width=").append(Double.toString(_width));
        sb.append(",height=").append(Double.toString(_height));
        
        sb.append(",numtextattributes=");
        if (_intersectingTextAttributes == null || 
            _intersectingTextAttributes.isEmpty()){
            sb.append("0");
        }
        else {
            sb.append(_intersectingTextAttributes.size());
        }
        
        sb.append(",numparameterattributes=");
        if (_intersectingParameterAttributes == null || 
            _intersectingParameterAttributes.isEmpty()){
            sb.append("0");
        }
        else {
            sb.append(_intersectingParameterAttributes.size());
        }
        return sb.toString();
    }

    public void moveCoordinatesToUpperLeftCornerFromCenter(){
        _xCoordinate -= (_width/2);
        _yCoordinate -= (_height/2);
    }
    
}
