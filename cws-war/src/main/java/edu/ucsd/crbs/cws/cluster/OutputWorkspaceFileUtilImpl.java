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

import edu.ucsd.crbs.cws.dao.WorkspaceFileDAO;
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import java.util.List;

/**
 * Creates, registers, and updates {@link WorkspaceFile} objects that represent
 * output of {@link Job}s
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class OutputWorkspaceFileUtilImpl implements OutputWorkspaceFileUtil {
    
    public static final String OUTPUT = " Output";
    private WorkspaceFileDAO _workspaceFileDAO;
   
    /**
     * Constructor
     * @param workspaceFileDAO Data Access object used to persist changes to {@link WorkspaceFile} objects
     */
    public OutputWorkspaceFileUtilImpl(WorkspaceFileDAO workspaceFileDAO){
        _workspaceFileDAO = workspaceFileDAO;
    }
    
    /**
     * Creates a new {@link WorkspaceFile} and inserts it into the data store using the DAO object
     * passed in via the constructor
     * @param j {@link Job} that we are registering the output {@link WorkspaceFile} for
     * @param outputDirectory Path to output directory for job, can be null in which case it is not set
     * @return
     * @throws Exception 
     */
    @Override
    public WorkspaceFile createAndRegisterJobOutputAsWorkspaceFile(Job j, String outputDirectory) throws Exception {
        if (j == null){
            throw new IllegalArgumentException("Job cannot be null");
        }
        
        WorkspaceFile wsp = new WorkspaceFile();
        wsp.setName(j.getName()+OUTPUT);
        wsp.setDir(true);
        wsp.setDescription("Workflow Job Output");
        wsp.setOwner(j.getOwner());
        wsp.setSourceJobId(j.getId());
        
        if (outputDirectory != null){
            wsp.setPath(outputDirectory);
        }
        
        return _workspaceFileDAO.insert(wsp, false);
    }
    
    /**
     * Updates the path for the {@link WorkspaceFile} associated with the {@link Job} <b>j</b>
     * passed in
     * @param j {@link Job} that is used to search for {@link WorkspaceFile} objects by examining {@link WorkspaceFile#getSourceJobId()} that match
     * @param outputDirectory Value to set in {@link WorkspaceFile#setPath(java.lang.String)}
     * @return
     * @throws Exception 
     */
    @Override
    public WorkspaceFile updateJobOutputWorkspaceFilePath(Job j,final String outputDirectory) throws Exception {
        
        List<WorkspaceFile> wsfList = _workspaceFileDAO.getWorkspaceFilesBySourceJobId(j.getId());
        if (wsfList == null){
            return null;
        }
        
        for (WorkspaceFile wsf : wsfList){
            if (wsf.getDir() == true){
                if (wsf.getPath() == null){
                    return _workspaceFileDAO.updatePathAndSize(wsf.getId(), outputDirectory,null);
                }
            }
        }
        return null;
    }


}
