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
package edu.ucsd.crbs.cws.workflow.validate;

import edu.ucsd.crbs.cws.workflow.Parameter;
import edu.ucsd.crbs.cws.workflow.ParameterWithError;
import edu.ucsd.crbs.cws.workflow.Job;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Checks for duplicate {@link Parameter} objects in {@link Job}
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class JobParametersDuplicateChecker implements JobParametersChecker {

    /**
     * Examines {@link Parameter} objects and flags those objects with duplicate
     * {@link Parameter#getName()} values by putting every occurrence after the
     * 1st encounter into {@link Job#getParametersWithErrors() } list
     *
     * @param job
     */
    @Override
    public void check(Job job) {
        
        if (job == null){
            return;
        }
        
        if (job.getParameters() == null || job.getParameters().isEmpty() == true) {
            return;
        }

        HashSet<String> paramNames = new HashSet<>();
        Parameter p;
        Iterator pIterator = job.getParameters().iterator();
        for (; pIterator.hasNext();) {
            p = (Parameter) pIterator.next();

            //skip null names, should already be filtered out, but just in case
            if (p.getName() == null) {
                continue;
            }

            if (paramNames.contains(p.getName())) {
                job.addParameterWithError(new ParameterWithError(p, "Duplicate name"));
                pIterator.remove();
            } else {
                paramNames.add(p.getName());
            }
        }
    }

}
