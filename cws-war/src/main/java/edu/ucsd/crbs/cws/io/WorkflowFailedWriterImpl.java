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

package edu.ucsd.crbs.cws.io;

import edu.ucsd.crbs.cws.rest.Constants;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Writes {@value Constants#WORKFLOW_FAILED_FILE}
 * file via 
 * {@link WorkflowFailedWriterImpl#write(java.lang.String, java.lang.String) }
 * method
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class WorkflowFailedWriterImpl implements WorkflowFailedWriter {

    private static final Logger _log
            = Logger.getLogger(WorkflowFailedWriterImpl.class.getName());
    
    /**
     * Directory under which the {@value Constants#WORKFLOW_FAILED_FILE} will
     * be written
     */
    private File _path;
    
    /**
     * Sets the directory under which {@value Constants#WORKFLOW_FAILED_FILE} file
     * will be written when {@link #write(java.lang.String, java.lang.String) }
     * is invoked. 
     * @param path Directory to write {@value Constants#WORKFLOW_FAILED_FILE} file
     * @throws Exception if Path is not a directory as detected by 
     *         {@link File#isDirectory() }
     * @throws IllegalArgumentException if path is null
     */
    @Override
    public void setPath(final String path) throws Exception,IllegalArgumentException {
        if (path == null){
            throw new IllegalArgumentException("Path cannot be null");
        }
        _path = new File(path);
        if (!_path.isDirectory()){
            _path = null;
            throw new Exception("Path "+path+" must be a directory");
        }
        
    }
    
    /**
     * Writes <b>error</b> and <b>detailedError</b> to
     * {@value Constants#WORKFLOW_FAILED_FILE} file in directory set via
     * {@link #setPath(java.lang.String) } method
     * @param error Error message set as value for key
     *              {@link Constants#SIMPLE_ERROR_MESSAGE_KEY}
     * @param detailedError Detailed Error message set as value for key
     *              {@link Constants#DETAILED_ERROR_MESSAGE_KEY}
     * @throws NullPointerException if path was not set via 
     *      {@link #setPath(java.lang.String) } method
     * @throws IllegalArgumentException if <b>error</b> or <b>detailedError</b>
     *         are {@code null}
     * @throws IOException if there is an error writing the 
     *         {@value Constants#WORKFLOW_FAILED_FILE} file
     */
    @Override
    public void write(final String error,final String detailedError) throws IOException,
            NullPointerException,IllegalArgumentException{
        if (_path == null){
            throw new NullPointerException("Path not set, must call setPath first");
        }
        if (error == null){
            throw new IllegalArgumentException("error cannot be null");
        }
        if (detailedError == null){
            throw new IllegalArgumentException("detailedError cannot be null");
        }
        FileWriter fw = null;
        try {
            Properties props = new Properties();
            props.setProperty(Constants.SIMPLE_ERROR_MESSAGE_KEY, error);
            props.setProperty(Constants.DETAILED_ERROR_MESSAGE_KEY, detailedError);
            fw = new FileWriter(_path.getAbsolutePath()+File.separator+Constants.WORKFLOW_FAILED_FILE);
            props.store(fw, null);
        }
        finally {
            if (fw != null){
                fw.close();
            }
        }
    }

}
