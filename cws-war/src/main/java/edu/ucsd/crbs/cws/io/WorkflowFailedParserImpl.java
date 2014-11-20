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

package edu.ucsd.crbs.cws.io;

import edu.ucsd.crbs.cws.rest.Constants;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Parses {@link Constants#WORKFLOW_FAILED_FILE} for error and detailed
 * error messages.
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class WorkflowFailedParserImpl implements WorkflowFailedParser {

    private static final Logger _log
            = Logger.getLogger(WorkflowFailedParserImpl.class.getName());
    
    private String _error;
    private String _detailedError;
    private boolean _exists = false;
    
    /**
     * Takes <b>path</b> and attempts to parse the file which should be in <b>key=value</b>
     * format.  Keys that are parsed:<p/>
     * {@value Constants#SIMPLE_ERROR_MESSAGE_KEY}<br/>
     * {@value Constants#DETAILED_ERROR_MESSAGE_KEY}<p/>
     * 
     * @param path Path to directory containing {@link Constants#WORKFLOW_FAILED_FILE} file or path to a file
     * @throws Exception if there is an error reading the file or if <b>path</b> passed in is null
     */
    @Override
    public void setPath(String path) throws Exception {
        
        _exists = false;
        _error = null;
        _detailedError = null;
        
        if (path == null){
            throw new Exception("Path is null");
        }
        
        File checkPath = new File(path);
        File fileCheck = checkPath;
        if (checkPath.exists()){
            if (checkPath.isDirectory()){
                fileCheck = new File(checkPath.getAbsolutePath()+File.separator+
                        Constants.WORKFLOW_FAILED_FILE);
                if (fileCheck.exists() == false){
                    _log.log(Level.INFO, "{0} not found", 
                            fileCheck.getAbsolutePath());
                    return;
                }
                if (fileCheck.isFile() == false){
                    _log.log(Level.INFO, "{0} is not a file", 
                            fileCheck.getAbsolutePath());
                    return;
                }
            }
            else if (!checkPath.isFile()){
                _log.log(Level.INFO,
                        "{0} is not a file and not a directory that contains {1}",
                        new Object[]{checkPath.getAbsolutePath(),
                        Constants.WORKFLOW_FAILED_FILE});
                return;
            }
        }
        else {
            _log.log(Level.INFO, "{0} path does not exist", checkPath.getAbsolutePath());
            return;
        }
        Properties props = new Properties();
        props.load(new FileReader(fileCheck));
        _error = props.getProperty(Constants.SIMPLE_ERROR_MESSAGE_KEY);
        _detailedError = props.getProperty(Constants.DETAILED_ERROR_MESSAGE_KEY);
        _exists = true;
        
    }

    /**
     * Value of {@value Constants#SIMPLE_ERROR_MESSAGE_KEY} from {@link Constants#WORKFLOW_FAILED_FILE} file
     * @return 
     */
    @Override
    public String getError() {
        return _error;
    }


    /**
     * Value of {@value Constants#DETAILED_ERROR_MESSAGE_KEY} from {@link Constants#WORKFLOW_FAILED_FILE} file
     * @return 
     */
    @Override
    public String getDetailedError() {
        return _detailedError;
    }

    /**
     * @return true if is found via previous {@link #setPath(java.lang.String) }
     */
    @Override
    public boolean exists() {
        return _exists;
    }

}
