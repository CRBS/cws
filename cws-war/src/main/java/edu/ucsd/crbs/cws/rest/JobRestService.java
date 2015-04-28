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

import edu.ucsd.crbs.cws.auth.Authenticator;
import edu.ucsd.crbs.cws.auth.AuthenticatorImpl;
import edu.ucsd.crbs.cws.auth.Permission;
import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.cluster.OutputWorkspaceFileUtil;
import edu.ucsd.crbs.cws.cluster.OutputWorkspaceFileUtilImpl;
import edu.ucsd.crbs.cws.dao.EventDAO;
import edu.ucsd.crbs.cws.dao.InputWorkspaceFileLinkDAO;
import edu.ucsd.crbs.cws.dao.JobDAO;
import edu.ucsd.crbs.cws.dao.WorkspaceFileDAO;
import edu.ucsd.crbs.cws.dao.objectify.EventObjectifyDAOImpl;
import edu.ucsd.crbs.cws.dao.objectify.InputWorkspaceFileLinkObjectifyDAOImpl;
import edu.ucsd.crbs.cws.dao.objectify.JobObjectifyDAOImpl;
import edu.ucsd.crbs.cws.dao.objectify.WorkspaceFileObjectifyDAOImpl;
import edu.ucsd.crbs.cws.log.Event;
import edu.ucsd.crbs.cws.log.EventBuilder;
import edu.ucsd.crbs.cws.log.EventBuilderImpl;
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.validate.JobValidator;
import edu.ucsd.crbs.cws.workflow.validate.JobValidatorImpl;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * REST Service to manipulate {@link Workflow} {@link Job} objects.
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@Path(Constants.SLASH + Constants.JOBS_PATH)
public class JobRestService {

    
    private static final Logger _log
            = Logger.getLogger(JobRestService.class.getName());
    
    
    /** @TODO code smell, need factory and singleton for these guys */
    static InputWorkspaceFileLinkDAO _inputWorkspaceFileLinkDAO;
    static JobDAO _jobDAO;
    static WorkspaceFileDAO _workspaceFileDAO;
    
    static {
        _inputWorkspaceFileLinkDAO = new InputWorkspaceFileLinkObjectifyDAOImpl();
    
        _jobDAO = new JobObjectifyDAOImpl(_inputWorkspaceFileLinkDAO);
        _workspaceFileDAO = new WorkspaceFileObjectifyDAOImpl(_jobDAO,
                _inputWorkspaceFileLinkDAO);
        ((JobObjectifyDAOImpl)_jobDAO).setWorkspaceFileDAO(_workspaceFileDAO);

    }
    
    static EventDAO _eventDAO = new EventObjectifyDAOImpl();

    static Authenticator _authenticator = new AuthenticatorImpl();

    static EventBuilder _eventBuilder = new EventBuilderImpl();
    
    static JobValidator _validator = new JobValidatorImpl();
    
    static OutputWorkspaceFileUtil _workspaceFileUtil = new OutputWorkspaceFileUtilImpl(_workspaceFileDAO);

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
     * Permissions really change output of this call.<br/>
     * This method first authenticates <br/>
     * the user associated with <b>userLogin</b> and <b>userToken</b> and verifies
     * that <b>userLoginToRunAs</b> if set is allowed.<p/>
     * 
     * If the {@link User} has {@link Permission#LIST_ALL_JOBS} then the code
     * will take the value in <b>owner</b> and return the results.<br/>
     * If the {@link User} has {@link Permission#LIST_THEIR_JOBS} then the code
     * will only return jobs owned by that User, with the added check to verify
     * that the value of <b>owner</b> if set matches {@link User#getLoginToRunJobAs()}
     * otherwise an error will result.
     * 
     * 
     * @param status Only Jobs with given status are returned (?status=)
     * @param owner Only Jobs matching this owner are returned (?owner=) This 
     *              parameter should only be used if the {@link User} has 
     *              {@link Permission#LIST_ALL_JOBS} otherwise it should be left
     *              unset or set to match the <b>userLogin</b>
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
            @QueryParam(Constants.SHOW_DELETED_QUERY_PARAM) final Boolean showDeleted,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request) {

        try {
            User user = _authenticator.authenticate(request);
             Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());
            
            // user can list everything so let them do whatever
            if (user.isAuthorizedTo(Permission.LIST_ALL_JOBS)) {
                return _jobDAO.getJobs(owner, status, notSubmitted, noParams, noWorkflowParams,showDeleted);
            }
            
            // user can only list their jobs so return error message if they try to
            // list jobs for another user otherwise use their login
            if (user.isAuthorizedTo(Permission.LIST_THEIR_JOBS)){
                if (owner != null && !owner.equals(user.getLoginToRunJobAs())){
                    throw new Exception("Not authorized to list jobs owned by "+owner);
                }
                return _jobDAO.getJobs(user.getLoginToRunJobAs(), status, notSubmitted, noParams, noWorkflowParams,showDeleted);
            }
            
            throw new WebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
        }catch(WebApplicationException wae){
            _log.log(Level.SEVERE,"Caught WebApplicationException",wae);
            throw wae;
            

        } catch (Exception ex) {
            _log.log(Level.SEVERE,"Caught Exception",ex);
            throw new WebApplicationException(ex);
        }
    }

    /**
     * Gets count of jobs constrained by query parameters.  This is done since it
     * is more efficient to query for count this way.
     * @param status
     * @param owner
     * @param noParams
     * @param noWorkflowParams
     * @param notSubmitted
     * @param showDeleted
     * @param userLogin
     * @param userToken
     * @param userLoginToRunAs
     * @param request
     * @return 
     */
    @GET
    @Path(Constants.COUNT_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    public int getJobsCount(@QueryParam(Constants.STATUS_QUERY_PARAM) final String status,
            @QueryParam(Constants.OWNER_QUERY_PARAM) final String owner,
            @QueryParam(Constants.NOPARAMS_QUERY_PARAM) final boolean noParams,
            @QueryParam(Constants.NOWORKFLOWPARAMS_QUERY_PARAM) final boolean noWorkflowParams,
            @QueryParam(Constants.NOTSUBMITTED_TO_SCHED_QUERY_PARAM) final boolean notSubmitted,
            @QueryParam(Constants.SHOW_DELETED_QUERY_PARAM) final Boolean showDeleted,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request) {
        
        try {
            User user = _authenticator.authenticate(request);
             Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());
            
            // user can list everything so let them do whatever
            if (user.isAuthorizedTo(Permission.LIST_ALL_JOBS)) {
                return _jobDAO.getJobsCount(owner, status, notSubmitted, 
                        noParams, noWorkflowParams,showDeleted);
            }
            
            // user can only list their jobs so return error message if they try to
            // list jobs for another user otherwise use their login
            if (user.isAuthorizedTo(Permission.LIST_THEIR_JOBS)){
                if (owner != null && !owner.equals(user.getLoginToRunJobAs())){
                    throw new Exception("Not authorized to count jobs owned by "+owner);
                }
                return _jobDAO.getJobsCount(user.getLoginToRunJobAs(), status, 
                        notSubmitted, noParams, noWorkflowParams,showDeleted);
            }
            
            throw new WebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
        }catch(WebApplicationException wae){
            _log.log(Level.SEVERE,"Caught WebApplicationException",wae);
            throw wae;
            

        } catch (Exception ex) {
            _log.log(Level.SEVERE,"Caught Exception",ex);
            throw new WebApplicationException(ex);
        }
        
    }
        
    /**
     * Gets a specific {@link Job} by id.  Return value of this method
     * is dependent on permissions set for <b>userLogin</b> and <b>userToken</b>
     * set by caller.  If user has {@link Permission#LIST_ALL_JOBS} then the job
     * matching <b>jobid</b> will be returned.  If the user only has 
     * {@link Permission#LIST_THEIR_JOBS} then the job will only be returned if
     * it is owned by the {@link User} associated with <b>userLogin</b> and <b>userToken</b>
     * otherwise no job will be returned.
     *
     * @param jobid Path parameter that denotes id of job to retrieve
     * @param userLogin
     * @param userToken
     * @param userLoginToRunAs
     * @param request
     * @return Json representation of Job matching id
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
            User user = _authenticator.authenticate(request);
             Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());

            if (user.isAuthorizedTo(Permission.LIST_ALL_JOBS)) {
                return _jobDAO.getJobById(jobid);
            }
            if (user.isAuthorizedTo(Permission.LIST_THEIR_JOBS)){
                return _jobDAO.getJobByIdAndUser(jobid,user.getLoginToRunJobAs());
            }
            throw new WebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
        }catch(WebApplicationException wae){
            _log.log(Level.SEVERE,"Caught WebApplicationException",wae);
            throw wae;
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
     * @param deleted
     * @param error
     * @param detailedError
     * @param userLogin
     * @param userToken
     * @param resave
     * @param userLoginToRunAs
     * @param request
     * @deprecated Use {@link #update(java.lang.Long, 
     * edu.ucsd.crbs.cws.workflow.Job, java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String,
     * javax.servlet.http.HttpServletRequest) }
     * @return
     */
    @POST
    @Path(Constants.JOB_ID_REST_PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Job updateJob(@PathParam(Constants.JOB_ID_PATH_PARAM) final Long jobId,
            @QueryParam(Constants.STATUS_QUERY_PARAM) final String status,
            @QueryParam(Constants.ESTCPU_QUERY_PARAM) final Long estCpu,
            @QueryParam(Constants.ESTRUNTIME_QUERY_PARAM) final Long estRunTime,
            @QueryParam(Constants.ESTDISK_QUERY_PARAM) final Long estDisk,
            @QueryParam(Constants.SUBMITDATE_QUERY_PARAM) final Long submitDate,
            @QueryParam(Constants.STARTDATE_QUERY_PARAM) final Long startDate,
            @QueryParam(Constants.FINISHDATE_QUERY_PARAM) final Long finishDate,
            @QueryParam(Constants.SUBMITTED_TO_SCHED_QUERY_PARAM) final Boolean submittedToScheduler,
            @QueryParam(Constants.SCHEDULER_JOB_ID_QUERY_PARAM) final String schedulerJobId,
            @QueryParam(Constants.DELETED_QUERY_PARAM) final Boolean deleted,
            @QueryParam(Constants.ERROR_QUERY_PARAM) final String error,
            @QueryParam(Constants.DETAILED_ERROR_QUERY_PARAM) final String detailedError,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @QueryParam(Constants.RESAVE_QUERY_PARAM) final String resave,
            @Context HttpServletRequest request) {

        try {
            User user = _authenticator.authenticate(request);
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
                        schedulerJobId,deleted,error,detailedError);
            }
            if (user.isAuthorizedTo(Permission.UPDATE_THEIR_JOBS)){
                Job job = _jobDAO.getJobByIdAndUser(jobId.toString(),
                        user.getLoginToRunJobAs());
                if (job == null){
                    throw new Exception("Error retrieving Job or not authorized");
                }
                if (resave != null && resave.equalsIgnoreCase("true")){
                    return _jobDAO.resave(jobId);
                }
                return _jobDAO.update(jobId, status, estCpu, estRunTime, estDisk,
                        submitDate, startDate, finishDate, submittedToScheduler,
                        schedulerJobId,deleted,error,detailedError);
            }

            throw new WebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
        }catch(WebApplicationException wae){
            _log.log(Level.SEVERE,"Caught WebApplicationException",wae);
            throw wae;
        } catch (Exception ex) {
            _log.log(Level.SEVERE,"Caught Exception",ex);
            throw new WebApplicationException(ex);
        }
    }

    
    @PUT
    @Path(Constants.JOB_ID_REST_PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Job update(@PathParam(Constants.JOB_ID_PATH_PARAM) final Long jobId,
            Job job,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @QueryParam(Constants.RESAVE_QUERY_PARAM) final String resave,
            @Context HttpServletRequest request) {

        try {
            User user = _authenticator.authenticate(request);
            Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());
            if (jobId != null) {
                _log.log(Level.INFO, "Job id is: {0}", jobId.toString());
            } else {
                job.setId(jobId);
                _log.log(Level.INFO,"Job id not set in Json settting to {0}",
                        job.getId());
            }

            if (user.isAuthorizedTo(Permission.UPDATE_ALL_JOBS)) {
                if (resave != null && resave.equalsIgnoreCase("true")){
                    return _jobDAO.resave(jobId);
                }
                return _jobDAO.update(job);
            }
            if (user.isAuthorizedTo(Permission.UPDATE_THEIR_JOBS)){
                Job checkJob = _jobDAO.getJobByIdAndUser(jobId.toString(),
                        user.getLoginToRunJobAs());
                if (checkJob == null){
                    throw new Exception("Error retrieving Job or not authorized");
                }
                if (resave != null && resave.equalsIgnoreCase("true")){
                    return _jobDAO.resave(jobId);
                }
                return _jobDAO.update(job);
            }

            throw new WebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
        }catch(WebApplicationException wae){
            _log.log(Level.SEVERE,"Caught WebApplicationException",wae);
            throw wae;
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
    public Job createJob(Job j,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request) {
        try {
            User user = _authenticator.authenticate(request);
            Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());
            
            if (user.isAuthorizedTo(Permission.CREATE_JOB)) {
                j = _validator.validate(j,user);
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
                
                //register workspace file for output of job.
                _workspaceFileUtil.createAndRegisterJobOutputAsWorkspaceFile(job,null);
                
                return job;
            }
            throw new WebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
        }catch(WebApplicationException wae){
            _log.log(Level.SEVERE,"Caught WebApplicationException",wae);
            throw wae;
        } catch (Exception ex) {
            _log.log(Level.SEVERE,"Caught Exception",ex);
            throw new WebApplicationException(ex);
        }
    }
}
