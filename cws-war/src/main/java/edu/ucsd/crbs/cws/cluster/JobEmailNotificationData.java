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

package edu.ucsd.crbs.cws.cluster;

/**
 * Simple pojo containing information necessary to send communications
 * to the user about job status
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class JobEmailNotificationData {

    private String _project;
    private String _portalName;
            
    private String _portalURL; 
    private  String _helpEmail;
    private String _bccEmail; 

    /**
     * Gets project name
     * @return 
     */
    public String getProject() {
        return _project;
    }

    /**
     * Sets project name ie SLASH
     * @param _project 
     */
    public void setProject(String _project) {
        this._project = _project;
    }

    /**
     * Gets portal name
     * @return 
     */
    public String getPortalName() {
        return _portalName;
    }

    /**
     * Sets portal name ie SLASH Portal
     * @param _portalName 
     */
    public void setPortalName(String _portalName) {
        this._portalName = _portalName;
    }

    /**
     * Gets url for portal
     * @return 
     */
    public String getPortalURL() {
        return _portalURL;
    }

    /**
     * Sets url for portal
     * @param _portalURL 
     */
    public void setPortalURL(String _portalURL) {
        this._portalURL = _portalURL;
    }

    /**
     * Gets help email address
     * @return 
     */
    public String getHelpEmail() {
        return _helpEmail;
    }

    /**
     * Sets Help Email Address
     * @param _helpEmail 
     */
    public void setHelpEmail(String _helpEmail) {
        this._helpEmail = _helpEmail;
    }

    /**
     * Gets blind carbon copy email address
     * @return empty string if null otherwise whatever was set in {@link #setBccEmail(java.lang.String)}
     */
    public String getBccEmail() {
        if (_bccEmail == null){
            return "";
        }
        return _bccEmail;
    }

    /**
     * Sets blind carbon copy email address
     * @param _bccEmail 
     */
    public void setBccEmail(String _bccEmail) {
        this._bccEmail = _bccEmail;
    }
    
}
