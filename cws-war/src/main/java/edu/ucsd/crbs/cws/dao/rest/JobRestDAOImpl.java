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

package edu.ucsd.crbs.cws.dao.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.objectify.util.jackson.ObjectifyJacksonModule;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.core.impl.provider.entity.StringProvider;
import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.dao.JobDAO;
import edu.ucsd.crbs.cws.jerseyclient.MultivaluedMapFactory;
import edu.ucsd.crbs.cws.jerseyclient.MultivaluedMapFactoryImpl;
import edu.ucsd.crbs.cws.rest.Constants;
import edu.ucsd.crbs.cws.workflow.Job;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Provides ways to obtain and persist {@link Job} objects via REST API calls
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class JobRestDAOImpl implements JobDAO {

    private String _restURL;
    private User _user;

    MultivaluedMapFactory _multivaluedMapFactory = new MultivaluedMapFactoryImpl();

    public JobRestDAOImpl() {

    }

    /**
     * Sets the base REST URL
     *
     * @param url
     */
    public void setRestURL(final String url) {
        _restURL = url;
    }

    public void setUser(User user) {
        _user = user;
    }

    @Override
    public Job getJobById(String jobId) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Job getJobByIdAndUser(String jobId, String user) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public List<Job> getJobs(String owner, String status, Boolean notSubmittedToScheduler, boolean noParams, boolean noWorkflowParams) throws Exception {
        ClientConfig cc = new DefaultClientConfig();
        cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        Client client = Client.create(cc);
        client.addFilter(new HTTPBasicAuthFilter(_user.getLogin(),_user.getToken()));
        client.setFollowRedirects(true);
        WebResource resource = client.resource(_restURL).path(Constants.REST_PATH).path(Constants.JOBS_PATH);
        MultivaluedMap queryParams = _multivaluedMapFactory.getMultivaluedMap(_user);

        if (owner != null) {
            queryParams.add(Constants.OWNER_QUERY_PARAM, owner);
        }

        if (status != null) {
            queryParams.add(Constants.STATUS_QUERY_PARAM, status);
        }

        if (notSubmittedToScheduler != null) {
            queryParams.add(Constants.NOTSUBMITTED_TO_SCHED_QUERY_PARAM, notSubmittedToScheduler.toString());
        }

        if (noParams == true) {
            queryParams.add(Constants.NOPARAMS_QUERY_PARAM, Boolean.TRUE.toString());
        }

        if (noWorkflowParams == true) {
            queryParams.add(Constants.NOWORKFLOWPARAMS_QUERY_PARAM, Boolean.TRUE.toString());
        }

        String json = resource.queryParams(queryParams).accept(MediaType.APPLICATION_JSON).get(String.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new ObjectifyJacksonModule());
        return mapper.readValue(json, new TypeReference<List<Job>>() {
        });
    }

    @Override
    public Job insert(Job j, boolean skipWorkflowCheck) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Job update(long jobId, final String status, Long estCpu, Long estRunTime,
            Long estDisk, Long submitDate, Long startDate, Long finishDate,
            Boolean submittedToScheduler,
            final String schedulerJobId) throws Exception {
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(StringProvider.class);
        Client client = Client.create(cc);
        client.addFilter(new HTTPBasicAuthFilter(_user.getLogin(),_user.getToken()));
        client.setFollowRedirects(true);
        WebResource resource = client.resource(_restURL).path(Constants.REST_PATH).
                path(Constants.JOBS_PATH).path(Long.toString(jobId));

        MultivaluedMap queryParams = _multivaluedMapFactory.getMultivaluedMap(_user);

        if (status != null) {
            queryParams.add(Constants.STATUS_QUERY_PARAM, status);
        }
        if (estCpu != null) {
            queryParams.add(Constants.ESTCPU_QUERY_PARAM, estCpu);
        }
        if (estRunTime != null) {
            queryParams.add(Constants.ESTRUNTIME_QUERY_PARAM, estRunTime);
        }
        if (estDisk != null) {
            queryParams.add(Constants.ESTDISK_QUERY_PARAM, estDisk);
        }
        if (submitDate != null) {
            queryParams.add(Constants.SUBMITDATE_QUERY_PARAM, submitDate.toString());
        }
        if (startDate != null) {
            queryParams.add(Constants.STARTDATE_QUERY_PARAM, startDate.toString());
        }
        if (finishDate != null) {
            queryParams.add(Constants.FINISHDATE_QUERY_PARAM, finishDate.toString());
        }
        if (submittedToScheduler != null) {
            queryParams.add(Constants.SUBMITTED_TO_SCHED_QUERY_PARAM, submittedToScheduler.toString());
        }

        if (schedulerJobId != null) {
            queryParams.add(Constants.SCHEDULER_JOB_ID_QUERY_PARAM, schedulerJobId);
        }

        String json = resource.queryParams(queryParams)
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity("{}")
                .post(String.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new ObjectifyJacksonModule());
        return mapper.readValue(json, new TypeReference<Job>() {
        });
    }

    @Override
    public Job resave(long jobId) throws Exception {
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(StringProvider.class);
        Client client = Client.create(cc);
        client.addFilter(new HTTPBasicAuthFilter(_user.getLogin(),_user.getToken()));
        client.setFollowRedirects(true);
        WebResource resource = client.resource(_restURL).path(Constants.REST_PATH).
                path(Constants.JOBS_PATH).path(Long.toString(jobId));

        MultivaluedMap queryParams = _multivaluedMapFactory.getMultivaluedMap(_user);

        queryParams.add(Constants.RESAVE_QUERY_PARAM, "true");
       
        String json = resource.queryParams(queryParams)
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity("{}")
                .post(String.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new ObjectifyJacksonModule());
        return mapper.readValue(json, new TypeReference<Job>() {
        });    }

    
}
