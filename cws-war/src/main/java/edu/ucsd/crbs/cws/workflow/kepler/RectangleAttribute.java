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
        this._width = width;
    }

    public double getHeight() {
        return _height;
    }

    public void setHeight(double height) {
        this._height = height;
    }
    
    public boolean addTextAttributeIfIntersecting(TextAttribute attrib){
        if (attrib == null){
            return false;
        }
        
        if (doesIntersect(attrib) == false){
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

    public boolean addParameterAttributeIfIntersecting(ParameterAttribute attrib){
        if (attrib == null){
            return false;
        }
        
        if (doesIntersect(attrib) == false){
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
    
    public boolean doesIntersect(Attribute attrib){
        if (attrib == null){
            return false;
        }
        
        //fail if coordinate is above or to left of upper left corner of rectangle
        if (attrib.getXCoordinate() < this._xCoordinate ||
            attrib.getYCoordinate() < this._yCoordinate){
            return false;
        }
        
        // fail if coordinate is below or to right of lower right corner of rectangle
        if (attrib.getXCoordinate() > this._xCoordinate + this._width ||
            attrib.getYCoordinate() > this._yCoordinate + this._height){
            return false;
        }
        return true;
    }
    
    public String getTextFromTextAttributes(){
        if (this._intersectingTextAttributes == null || this._intersectingTextAttributes.isEmpty()){
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        for (TextAttribute ta : this._intersectingTextAttributes){
            if (sb.length() > 0){
                sb.append("\n");
            }
            sb.append(ta.getText());
        }
        return sb.toString();
    }
    
    public String asString(){
        StringBuilder sb = new StringBuilder();
        sb.append(super.asString());
        sb.append(",width=").append(Double.toString(_width));
        sb.append(",height=").append(Double.toString(_height));
        /*
        sb.append("\ntextattributes=\n");
        if (this._intersectingTextAttributes == null || this._intersectingTextAttributes.isEmpty()){
            sb.append("null");
        }
        else {
            for (Attribute ta : this._intersectingTextAttributes){
                sb.append("\t").append(ta.asString()).append("\n");
            }
        }
        sb.append("\nparameterattributes=\n");
        if (this._intersectingParameterAttributes == null || this._intersectingParameterAttributes.isEmpty()){
            sb.append("null");
        }
        else {
            for (Attribute pa : this._intersectingParameterAttributes){
                sb.append("\t").append(pa.asString()).append("\n");
            }
        }
        */
        return sb.toString();
    }

}
