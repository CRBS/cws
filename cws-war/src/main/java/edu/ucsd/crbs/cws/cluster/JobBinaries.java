/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2015 The Regents of the University of California All Rights Reserved
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
 * Contains binaries and jar paths needed to generate Job script
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class JobBinaries {

    private String _keplerScript;
    private String _mailCommand;
    private String _sleepCommand;
    private String _javaCommand;
    private String _echoCommand;
    private String _rmCommand;
    private String _killCommand;
    private String _registerUpdateJar;
    
    private int _postEmailSleepTimeInSeconds;
    private int _workspaceUpdateRetrySleepTimeInSeconds;
    
    private int _retryCount;

    
    public String getRegisterUpdateJar() {
        if (_registerUpdateJar == null){
            return "";
        }
        return _registerUpdateJar;
    }

    public void setRegisterUpdateJar(String _registerUpdateJar) {
        this._registerUpdateJar = _registerUpdateJar;
    }

    
    public int getRetryCount() {
        return _retryCount;
    }

    public void setRetryCount(int _retryCount) {
        this._retryCount = _retryCount;
    }

    public String getKeplerScript() {
        return _keplerScript;
    }

    public void setKeplerScript(String _keplerScript) {
        this._keplerScript = _keplerScript;
    }

    public String getMailCommand() {
        if (_mailCommand == null){
            return "/bin/mail";
        }
        return _mailCommand;
    }

    public void setMailCommand(String _mailCommand) {
        this._mailCommand = _mailCommand;
    }

    public String getSleepCommand() {
        if (_sleepCommand == null){
            return "sleep";
        }
        return _sleepCommand;
    }

    public void setSleepCommand(String _sleepCommand) {
        this._sleepCommand = _sleepCommand;
    }

    public String getJavaCommand() {
        if (_javaCommand == null){
            return "java";
        }
        return _javaCommand;
    }

    public void setJavaCommand(String _javaCommand) {
        this._javaCommand = _javaCommand;
    }

    public String getEchoCommand() {
        if (_echoCommand == null){
            return "echo";
        }
        return _echoCommand;
    }

    public void setEchoCommand(String _echoCommand) {
        this._echoCommand = _echoCommand;
    }

    public String getRmCommand() {
        if (_rmCommand == null){
            return "/bin/rm";
        }
        return _rmCommand;
    }

    public void setRmCommand(String _rmCommand) {
        this._rmCommand = _rmCommand;
    }

    public String getKillCommand() {
        if (_killCommand == null){
            return "kill";
        }
        return _killCommand;
    }

    public void setKillCommand(String _killCommand) {
        this._killCommand = _killCommand;
    }

    public int getPostEmailSleepTimeInSeconds() {
        return _postEmailSleepTimeInSeconds;
    }

    public void setPostEmailSleepTimeInSeconds(int _postEmailSleepTimeInSeconds) {
        this._postEmailSleepTimeInSeconds = _postEmailSleepTimeInSeconds;
    }

    public int getWorkspaceUpdateRetrySleepTimeInSeconds() {
        return _workspaceUpdateRetrySleepTimeInSeconds;
    }

    public void setWorkspaceUpdateRetrySleepTimeInSeconds(int _workspaceUpdateRetrySleepTimeInSeconds) {
        this._workspaceUpdateRetrySleepTimeInSeconds = _workspaceUpdateRetrySleepTimeInSeconds;
    }
    
}
