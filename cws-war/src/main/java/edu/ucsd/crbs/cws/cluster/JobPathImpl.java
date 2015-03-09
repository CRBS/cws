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

import edu.ucsd.crbs.cws.rest.Constants;
import edu.ucsd.crbs.cws.workflow.Job;
import java.io.File;

/**
 * Generates path to {@link Job} on filesystem
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class JobPathImpl implements JobPath{

    private String _baseExecDir;
    
    /**
     * Constructor
     * @param baseExecDir Base file system path.  All jobs will be put under this directory
     */
    public JobPathImpl(final String baseExecDir){
        _baseExecDir = baseExecDir;
    }
    
    /**
     * Generates output directory by invoking {@link #getJobBaseDirectory(edu.ucsd.crbs.cws.workflow.Job)}
     * and appending {@link Constants#OUTPUTS_DIR_NAME}
     * @param j
     * @return
     * @throws Exception 
     */
    @Override
    public String getJobOutputDirectory(Job j) throws Exception {
        return getJobBaseDirectory(j)+File.separator+
                Constants.OUTPUTS_DIR_NAME;
    }

    /**
     * Generates base directory by appending the {@link Job#getOwner()} and
     * {@link Job#getId()} to the <b>baseExecDir</b> passed in to the constructor
     * @return 
     * @throws Exception If the <b>j</b> is null, if baseExec Directory is null, if {@link Job#getOwner() } or {@link Job#getId()} is null
     */
    @Override
    public String getJobBaseDirectory(Job j) throws Exception {
        if (_baseExecDir == null){
            throw new NullPointerException("Base directory cannot be null");
        }
        if (j == null){
            throw new NullPointerException("Job cannot be null");
        }
        if (j.getOwner() == null){
            throw new NullPointerException("Job owner cannot be null");
        }
        
        if (j.getId() == null){
            throw new NullPointerException("Job id cannot be null");
        }
        return _baseExecDir+File.separator+j.getOwner()+
                File.separator+j.getId().toString();
    }

}
