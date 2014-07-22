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

import edu.ucsd.crbs.cws.workflow.Job;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * Submits Workflow Task Command Script to Panfish for processing
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class JobCmdScriptSubmitterImpl implements JobCmdScriptSubmitter {

    private final String _panfishCast;
    private final String _queue;
    
    public JobCmdScriptSubmitterImpl(final String panfishCast,final String queue){
        _panfishCast = panfishCast;
        _queue = queue;
    }
    
    /**
     * Submits {@link Job} cmdScript which runs Job represented by <b>j</b> to Panfish
     * for processing. 
     * @param cmdScript Full path of script to submit to Panfish
     * @param j Job that will be run by cmdScript
     * @return String containing Job Id from Panfish to track job progress
     * @throws Exception 
     */
    @Override
    public String submit(final String cmdScript,
            Job j) throws Exception {

        String outputDir = new File(cmdScript).getParentFile().getAbsolutePath();

        ProcessBuilder pb = new ProcessBuilder(_panfishCast,
                "-q", _queue,
                "-N", getJobName(j),
                "-o", outputDir + File.separator + "stdout",
                "-e", outputDir + File.separator + "stderr",
                cmdScript);

        pb.directory(new File(outputDir));
        pb.redirectErrorStream(true);

        Process p = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

        StringBuilder sb = new StringBuilder();

        String line = reader.readLine();
        boolean firstLine = true;
        String jobId = null;
        while (line != null) {
            if (firstLine == true){
                // @TODO replace this with a grouping call so its just a single replaceAll
                jobId = line.replaceAll("^Your job ","").replaceAll(" .*","");
                firstLine = false;
            }
            sb.append(line).append("\n");
            line = reader.readLine();
        }
        reader.close();
        
        if (p.waitFor() != 0){
            throw new Exception("Non zero exit code received from Panfish: "+sb.toString());
        }
        
        //set the submit date
        j.setSubmitDate(new Date());
        
        j.setSchedulerJobId(jobId);
        
        return sb.toString();
    }

    /**
     * Generates a job name in a format suitable for Sun/Oracle Grid Engine (SGE).<p/>
     * 
     * If {@link Job} owner is null this method will return unset_workflow<br/>
     * Otherwise method will create a string of format: (owner)_workflow-(job id)<br/>
     * Ex:  bob_workflow-234232345<p/>
     * 
     * In addition, the following characters are replaced with X character:
     * -- any whitespace characters<br/>
     * -- non-word characters<br/>
     * -- digits in the first character of job name     * 
     * @param j Job generate job name for
     * @return Job name to pass to Sun/Oracle Grid Engine using -N flag
     */
    private String getJobName(final Job j) {

        if (j.getOwner() == null) {
            return "unset_workflow-" + j.getId();
        }
        String tempOwner = j.getOwner();
        //SGE does not like these characters in job name so lets swap them with X 
        String spaceTossedOwner = tempOwner.replaceAll("\\s", "X");
        String digitTossedOwner = spaceTossedOwner.replaceAll("^[0-9]", "X");
        return digitTossedOwner.replaceAll("\\W|_", "X") + "_workflow-" + j.getId();
    }

}
