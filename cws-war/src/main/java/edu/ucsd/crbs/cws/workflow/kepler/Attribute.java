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

/**
 * Represents a Kepler Attribute
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class Attribute {

    protected String _name;
    protected String _displayName;
    protected double _xCoordinate;
    protected double _yCoordinate;

    public Attribute(){
        
    }
 

    /**
     * Gets internal name
     * @return 
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets internal name
     * @param name 
     */
    public void setName(String name) {
        this._name = name;
    }

    /**
     * Gets display name
     * @return 
     */
    public String getDisplayName() {
        return _displayName;
    }

    /**
     * Sets display name
     */
    public void setDisplayName(String displayName) {
        this._displayName = displayName;
    }

    /**
     * Parses <b>location</b> extracting coordinates that can later be obtained 
     * via calls to ({@link #getXCoordinate() } {@link #getYCoordinate()})
     * <p/>
     * Format that is parsed:<p/>
     * [X, Y]<br/>
     * <br/>
     * If <b>location</b> does not contain data in above format or if its null 
     * no change is made to object.  Also <b>[</b> and <b>]</b> are not 
     * required, but the <b>,</b> is.
     * 
     * @param location String containing location data in format <b>[X,Y]</b>
     */
    public void setCoordinatesViaString(final String location) {
        if (location == null) {
            return;
        }

        String spacelessLoc = location.replace(" ", "").replace("[", "").replace("]", "").replace("{","").replace("}","");
        String splitCoords[] = spacelessLoc.split(",");
        //@TODO need to catch exception and redo this logic better
        if (splitCoords.length == 2) {
            _xCoordinate = Double.parseDouble(splitCoords[0]);
            _yCoordinate = Double.parseDouble(splitCoords[1]);
        }
    }

    public double getXCoordinate() {
        return _xCoordinate;
    }

    public void setXCoordinate(double xCoordinate) {
        this._xCoordinate = xCoordinate;
    }

    public double getYCoordinate() {
        return _yCoordinate;
    }

    public void setYCoordinate(double yCoordinate) {
        this._yCoordinate = yCoordinate;
    }
    
    /**
     * Gets string representation of object in this format:<p/>
     * name=NAME,displayname=DISPLAYNAME,x=XCOORD,y=YCOORD,centered=BOOLEAN
     * @return String in above format, if null fields are encountered <b>null</b> is output
     */
    public String asString(){
        StringBuilder sb = new StringBuilder();
        
        sb.append("name=");
        if (this._name != null){
            sb.append(_name);
        }
        else {
            sb.append("null");
        }
        sb.append(",displayname=");
        if (this._displayName != null){
            sb.append(_displayName);
        }
        else {
            sb.append("null");
        }
        sb.append(",x=").append(Double.toString(this._xCoordinate));
        sb.append(",y=").append(Double.toString(this._yCoordinate));
        return sb.toString();
    }
}
