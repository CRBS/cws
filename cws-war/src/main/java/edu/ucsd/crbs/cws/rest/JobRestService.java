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

package edu.ucsd.crbs.cws.rest;

import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.auth.Authenticator;
import edu.ucsd.crbs.cws.auth.AuthenticatorImpl;
import edu.ucsd.crbs.cws.auth.Permission;
import edu.ucsd.crbs.cws.dao.EventDAO;
import edu.ucsd.crbs.cws.dao.JobDAO;
import edu.ucsd.crbs.cws.dao.objectify.EventObjectifyDAOImpl;
import edu.ucsd.crbs.cws.dao.objectify.JobObjectifyDAOImpl;
import edu.ucsd.crbs.cws.log.Event;
import edu.ucsd.crbs.cws.log.EventBuilder;
import edu.ucsd.crbs.cws.log.EventBuilderImpl;
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.validate.JobValidator;
import edu.ucsd.crbs.cws.workflow.validate.JobValidatorImpl;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * REST Service to manipulate workflow {@link Job} objects.
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@Path(Constants.SLASH + Constants.JOBS_PATH)
public class JobRestService {

    
    private static final Logger _log
            = Logger.getLogger(JobRestService.class.getName());

    static JobDAO _jobDAO = new JobObjectifyDAOImpl();
    
    static EventDAO _eventDAO = new EventObjectifyDAOImpl();

    static Authenticator _authenticator = new AuthenticatorImpl();

    static EventBuilder _eventBuilder = new EventBuilderImpl();
    
    static JobValidator _validator = new JobValidatorImpl();

    /**
     * HTTP GET call that gets a list of all jobs. The list can be filtered
     * with various query parameters (ie parameters that are in the end of the
     * url ?status=Running&owner=bob).
     * <p/>
     * Example GET call for Jobs owned by user <b>foo</b> and in running
     * state<p/>
     *
     * http://.../jobs?status=Running&owner=foo
     *
     * @param status Only Jobs with given status are returned (?status=)
     * @param owner Only Jobs matching this owner are returned (?owner=)
     * @param noParams Job parameters are stripped from Job objects returned
     * (?noparams=)
     * @param noWorkflowParams Workflow Parameters are stripped from Workflow
     * objects within Job objects returned (?noworkflowparams=)
     * @param notSubmitted Only Jobs that have not been submitted to scheduler
     * are returned (?notsubmittedtoscheduler=)
     * @param userLogin
     * @param userToken
     * @param userLoginToRunAs
     * @param request
     *
     * @return List of Job objects in JSON format with media type set to
     * {@link MediaType.APPLICATION_JSON}
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Job> getJobs(@QueryParam(Constants.STATUS_QUERY_PARAM) final String status,
            @QueryParam(Constants.OWNER_QUERY_PARAM) final String owner,
            @QueryParam(Constants.NOPARAMS_QUERY_PARAM) final boolean noParams,
            @QueryParam(Constants.NOWORKFLOWPARAMS_QUERY_PARAM) final boolean noWorkflowParams,
            @QueryParam(Constants.NOTSUBMITTED_TO_SCHED_QUERY_PARAM) final boolean notSubmitted,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request) {

        try {
            User user = _authenticator.authenticate(request, userLogin, userToken,
                    userLoginToRunAs);
             Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());
            
            // user can list everything  
            if (user.isAuthorizedTo(Permission.LIST_ALL_JOBS)) {
                return _jobDAO.getJobs(owner, status, notSubmitted, noParams, noWorkflowParams);
            }
            throw new Exception("Not Authorized");

        } catch (Exception ex) {
            _log.log(Level.SEVERE,"Caught Exception",ex);
            throw new WebApplicationException(ex);
        }
    }

    /**
     * Gets a specific {@link Job} by id
     *
     * @param jobid Path parameter that denotes id of job to retrieve
     * @param userLogin
     * @param userToken
     * @param userLoginToRunAs
     * @param request
     * @return
     */
    @GET
    @Path(Constants.JOB_ID_REST_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    public Job getJob(@PathParam(Constants.JOB_ID_PATH_PARAM) String jobid,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request) {
        try {
            User user = _authenticator.authenticate(request, userLogin, userToken,
                    userLoginToRunAs);
             Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());

            if (user.isAuthorizedTo(Permission.LIST_ALL_JOBS)) {
                return _jobDAO.getJobById(jobid);
            }
            throw new Exception("Not authorized");
        } catch (Exception ex) {
            _log.log(Level.SEVERE,"Caught Exception",ex);
            throw new WebApplicationException(ex);
        }
    }

    /**
     * Need updateTask method which consumes @POST along with parameters to
     * update Should take a {@link Job}, but use a transaction to load and only modify
     * the fields the caller wants changed
     *
     * @param jobId
     * @param status
     * @param estCpu
     * @param estRunTime
     * @param submitDate
     * @param startDate
     * @param estDisk
     * @param finishDate
     * @param submittedToScheduler
     * @param schedulerJobId
     * @param userLogin
     * @param userToken
     * @param resave
     * @param userLoginToRunAs
     * @param request
     * @return
     */
    @POST
    @Path(Constants.JOB_ID_REST_PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Job updateTask(@PathParam(Constants.JOB_ID_PATH_PARAM) final Long jobId,
            @QueryParam(Constants.STATUS_QUERY_PARAM) final String status,
            @QueryParam(Constants.ESTCPU_QUERY_PARAM) final Long estCpu,
            @QueryParam(Constants.ESTRUNTIME_QUERY_PARAM) final Long estRunTime,
            @QueryParam(Constants.ESTDISK_QUERY_PARAM) final Long estDisk,
            @QueryParam(Constants.SUBMITDATE_QUERY_PARAM) final Long submitDate,
            @QueryParam(Constants.STARTDATE_QUERY_PARAM) final Long startDate,
            @QueryParam(Constants.FINISHDATE_QUERY_PARAM) final Long finishDate,
            @QueryParam(Constants.SUBMITTED_TO_SCHED_QUERY_PARAM) final Boolean submittedToScheduler,
            @QueryParam(Constants.SCHEDULER_JOB_ID_QUERY_PARAM) final String schedulerJobId,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @QueryParam(Constants.RESAVE_QUERY_PARAM) final String resave,
            @Context HttpServletRequest request) {

        try {
            User user = _authenticator.authenticate(request, userLogin, userToken,
                    userLoginToRunAs);
            Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());
            if (jobId != null) {
                _log.log(Level.INFO, "Job id is: {0}", jobId.toString());
            } else {
                _log.info("Job id is null.  wtf");
                throw new WebApplicationException();
            }

            if (user.isAuthorizedTo(Permission.UPDATE_ALL_JOBS)) {
                if (resave != null && resave.equalsIgnoreCase("true")){
                    return _jobDAO.resave(jobId);
                }
                return _jobDAO.update(jobId, status, estCpu, estRunTime, estDisk,
                        submitDate, startDate, finishDate, submittedToScheduler,
                        schedulerJobId);
            }

            throw new Exception("Not Authorized");
        } catch (Exception ex) {
            _log.log(Level.SEVERE,"Caught Exception",ex);
            throw new WebApplicationException(ex);
        }
    }

    /**
     * Creates a new {@link Job} by consuming JSON version of {@link Job} object
     *
     * @param j
     * @param userLogin
     * @param userToken
     * @param userLoginToRunAs
     * @param request
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Job createTask(Job j,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request) {
        try {
            User user = _authenticator.authenticate(request, userLogin, userToken,
                    userLoginToRunAs);
            Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());
            
            if (user.isAuthorizedTo(Permission.CREATE_JOB)) {
                j = _validator.validateParameters(j,user);
                // @TODO add failed create job event
                if (j.getError() != null || j.getParametersWithErrors() != null){
                    _log.log(Level.WARNING,"Validation of Job failed: {0}",
                            j.getSummaryOfErrors());
                    _eventDAO.neverComplainInsert(_eventBuilder.setAsFailedCreateJobEvent(event, j));
                    return j;
                }
                
                //do insert, but skip the workflow checks cause validation did it 
                //already
                
                //clear start submit and finish dates also set status in queue
                //and make sure submitted to scheduler is set to false
                // @TODO should this be put in validator?
                j.setHasJobBeenSubmittedToScheduler(false);
                j.setStartDate(null);
                j.setSubmitDate(null);
                j.setDownloadURL(null);
                j.setFinishDate(null);
                j.setStatus(Job.IN_QUEUE_STATUS);
                
                Job job = _jobDAO.insert(j,true);
                
                _eventDAO.neverComplainInsert(_eventBuilder.setAsCreateJobEvent(event, job));
                
                return job;
            }
            throw new Exception("Not Authorized");
        } catch (Exception ex) {
            _log.log(Level.SEVERE,"Caught Exception",ex);
            throw new WebApplicationException(ex);
        }
    }
}