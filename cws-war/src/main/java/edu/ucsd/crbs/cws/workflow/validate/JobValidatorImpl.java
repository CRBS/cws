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

import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.dao.WorkflowDAO;
import edu.ucsd.crbs.cws.dao.objectify.WorkflowObjectifyDAOImpl;
import edu.ucsd.crbs.cws.workflow.Parameter;
import edu.ucsd.crbs.cws.workflow.ParameterWithError;
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.WorkflowParameter;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Validates {@link Job} by examining all the parameters and verifying they
 * meet the constraints set by the {@link WorkflowParameter} objects in the
 * associated {@link Workflow}
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class JobValidatorImpl implements JobValidator {

    private static final Logger _log
            = Logger.getLogger(JobValidatorImpl.class.getName());

    WorkflowDAO _workflowDAO = new WorkflowObjectifyDAOImpl();

    JobParametersChecker _jobParamNullChecker = new JobParametersNullNameChecker();
    JobParametersChecker _jobParamDuplicateChecker = new JobParametersDuplicateChecker();
    ParameterValidator _parameterValidator = new ParameterValidatorImpl();

    /**
     * Performs validation of {@link Job} against the {@link Workflow} the <b>job</b>
     * is supposed to be running. If problems are found they will be set in the
     * <b>error</b> methods in {@link Job} object. Namely,
     * {@link Job#getError()} and {@link Job#getParametersWithErrors()}
     *
     * @param job {@link Job} to validate
     * @param user User running the {@link Job}
     * @return <b>job</b> passed in with errors set as appropriate
     * @throws Exception If the <b>job</b> is null
     */
    @Override
    public Job validateParameters(Job job, User user) throws Exception {
        if (job == null) {
            throw new Exception("Job cannot be null");
        }

        _log.log(Level.INFO, "Checking for null parameters");
        _jobParamNullChecker.check(job);

        _log.log(Level.INFO, "Duplicate parameter check");
        //iterate through all the Parameters of the job and verify there are
        // no duplicates first if there are set error
        _jobParamDuplicateChecker.check(job);

        //load Workflow For Task and if this has problems bail cause we need the
        // Workflow object to do anything else
        try {
            Workflow w = _workflowDAO.getWorkflowForJob(job, user);
            if (w != null) {
                _log.log(Level.INFO, "Found workflow setting in job");
                job.setWorkflow(w);
            } else {
                if (job.getWorkflow() != null && job.getWorkflow().getId() != null) {
                    _log.log(Level.WARNING, "Unable to load workflow for job",
                            job.getWorkflow().getId());
                } else {
                    _log.log(Level.WARNING, "Unable to load workflow for job");
                }
                job.setError("Unable to load workflow for job");
                return job;
            }
        } catch (Exception ex) {
            _log.log(Level.SEVERE, "caught exception", ex);
            job.setError(ex.getMessage());
            return job;
        }

        // iterate again through all parameters and find corresponding workflowparameter
        // if no match set error
        if (linkJobParametersWithWorkflowParameters(job) == 0) {
            _log.log(Level.INFO, "No parameters linked with WorkflowParameters");
            return job;
        }

        // for match verify value is within bounds of workflow parameter if not set error
        // keep track of workflow parameters already covered and if there are
        // any extra note those in the error
        Parameter param;
        Iterator pIterator = job.getParameters().iterator();
        for (; pIterator.hasNext();) {
            param = (Parameter) pIterator.next();
            String res = _parameterValidator.validate(param);
            if (res != null) {
                job.addParameterWithError(new ParameterWithError(param, res));
                pIterator.remove();
            }
        }

        return job;
    }

    /**
     * Iterates through {@link Job#getParameters()} for <b>job</b> and links
     * up {@link Parameter} objects with {@link WorkflowParameter} objects into
     * a map that is returned.<p/>
     * This method will fail if any of the following occurs:
     * <p/>
     * If any <b>job</b> {@link Parameter} objects don't have a matching
     * {@link WorkflowParameter}<br/>
     * If no <b>job</b> {@link Parameter} matches a {@link WorkflowParameter}
     * that is <b>required</b> ie {@link WorkflowParameter#getIsRequired() } ==
     * true<br/>
     *
     * <p/>
     * Note it is possible for {@link WorkflowParameter} objects have no
     * corresponding {@link Parameter} and this is fine as long as those
     * {@link WorkflowParameter} objects are not <b>required</b>
     *
     * @param job Job to examine
     * @return Map with {@link WorkflowParameter} as key and {@link Parameter}
     * as value
     */
    private int linkJobParametersWithWorkflowParameters(Job job) {

        WorkflowParameter wParam;
        int count = 0;

        if (job.getParameters() == null) {
            return 0;
        }

        for (Parameter p : job.getParameters()) {

            wParam = job.getWorkflow().removeWorkflowParameterMatchingName(p.getName());
            if (wParam == null) {
                job.addParameterWithError(new ParameterWithError(p, "No matching WorkflowParameter"));
            } else {
                p.setWorkflowParameter(wParam);
                count++;
            }
        }

        if (job.getWorkflow().getParameters().isEmpty() == true) {
            return count;
        }

        for (WorkflowParameter wParameter : job.getWorkflow().getParameters()) {
            if (wParameter.getIsRequired()) {
                job.addParameterWithError(new ParameterWithError(wParameter.getName(), null, "Required parameter not found"));
            }
        }
        return count;
    }
}
