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
import com.sun.jersey.multipart.impl.MultiPartWriter;
import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.dao.WorkspaceFileDAO;
import edu.ucsd.crbs.cws.jerseyclient.MultivaluedMapFactoryImpl;
import edu.ucsd.crbs.cws.jerseyclient.MultivaluedMapFactory;
import edu.ucsd.crbs.cws.rest.Constants;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import edu.ucsd.crbs.cws.workflow.report.DeleteReport;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class WorkspaceFileRestDAOImpl implements WorkspaceFileDAO {
    
    private String _restURL;
    private User _user;
    
    MultivaluedMapFactory _multivaluedMapFactory = new MultivaluedMapFactoryImpl();
    
    /**
     * Sets the base REST URL
     *
     * @param url
     */
    public void setRestURL(final String url) {
        _restURL = url;
    }
    
    public void setUser(User user){
        _user = user;
    }
 
    @Override
    public List<WorkspaceFile> getWorkspaceFilesById(String workspaceFileIds, User user) throws Exception {
        ClientConfig cc = new DefaultClientConfig();
        cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        Client client = Client.create(cc);
        
        if (user != null){
            client.addFilter(new HTTPBasicAuthFilter(user.getLogin(),user.getToken()));
        }
        else if (_user != null) {
            client.addFilter(new HTTPBasicAuthFilter(_user.getLogin(),_user.getToken()));
        }        
        
        client.setFollowRedirects(true);
        WebResource resource = client.resource(_restURL).path(Constants.REST_PATH).path(Constants.WORKSPACEFILES_PATH);
        MultivaluedMap queryParams = _multivaluedMapFactory.getMultivaluedMap(user);
        if (workspaceFileIds != null){
            queryParams.add(Constants.WSFID_PARAM,workspaceFileIds);
        }

        String json = resource.queryParams(queryParams).accept(MediaType.APPLICATION_JSON).get(String.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new ObjectifyJacksonModule());
        return mapper.readValue(json, new TypeReference<List<WorkspaceFile>>() {
        });
        
    }
    
    
    
    @Override
    public List<WorkspaceFile> getWorkspaceFiles(String owner, final String type,final Boolean isFailed,Boolean synced,
            final Boolean showDeleted) throws Exception {
        ClientConfig cc = new DefaultClientConfig();
        cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        Client client = Client.create(cc);
        client.addFilter(new HTTPBasicAuthFilter(_user.getLogin(),_user.getToken()));
        client.setFollowRedirects(true);
        WebResource resource = client.resource(_restURL).path(Constants.REST_PATH).path(Constants.WORKSPACEFILES_PATH);
        
        MultivaluedMap queryParams = _multivaluedMapFactory.getMultivaluedMap(_user);

        if (owner != null) {
            queryParams.add(Constants.OWNER_QUERY_PARAM, owner);
        }
        if (type != null){
            queryParams.add(Constants.TYPE_QUERY_PARAM,type);
        }
        if (isFailed != null){
            queryParams.add(Constants.WS_FAILED_QUERY_PARAM, isFailed.toString());
        }

        if (synced != null) {
            queryParams.add(Constants.SYNCED_QUERY_PARAM,synced.toString());
        }
        if (showDeleted != null){
            queryParams.add(Constants.SHOW_DELETED_QUERY_PARAM,showDeleted.toString());
        }

        String json = resource.queryParams(queryParams).accept(MediaType.APPLICATION_JSON).get(String.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new ObjectifyJacksonModule());
        return mapper.readValue(json, new TypeReference<List<WorkspaceFile>>() {
        });
    }

    @Override
    public WorkspaceFile getWorkspaceFileById(String workspaceFileId, User user) throws Exception {
        ClientConfig cc = new DefaultClientConfig();
        cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        Client client = Client.create(cc);
        
        if (user != null){
            client.addFilter(new HTTPBasicAuthFilter(user.getLogin(),user.getToken()));
        }
        else if (_user != null) {
            client.addFilter(new HTTPBasicAuthFilter(_user.getLogin(),_user.getToken()));
        }        
        
        client.setFollowRedirects(true);
        WebResource resource = client.resource(_restURL)
                .path(Constants.REST_PATH)
                .path(Constants.WORKSPACEFILES_PATH)
                .path(workspaceFileId);
        String json = resource.accept(MediaType.APPLICATION_JSON).get(String.class);
    
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new ObjectifyJacksonModule());
        return mapper.readValue(json, new TypeReference<WorkspaceFile>() {
        });
    }

    @Override
    public WorkspaceFile insert(WorkspaceFile wsp,boolean generateUploadURL) throws Exception {
        ObjectMapper om = new ObjectMapper();
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(StringProvider.class);
        cc.getClasses().add(MultiPartWriter.class);
        Client client = Client.create(cc);
        client.addFilter(new HTTPBasicAuthFilter(_user.getLogin(),_user.getToken()));
        client.setFollowRedirects(true);
        WebResource resource = client.resource(_restURL).
                path(Constants.REST_PATH).path(Constants.WORKSPACEFILES_PATH);

        String workspaceFileAsJson = om.writeValueAsString(wsp);

        MultivaluedMap queryParams = _multivaluedMapFactory.getMultivaluedMap(_user);

        if (generateUploadURL == false){
            queryParams.add(Constants.ADD_UPLOAD_URL_PARAM, "false");
        }

        String response = resource.queryParams(queryParams).type(MediaType.APPLICATION_JSON_TYPE)
                .entity(workspaceFileAsJson)
                .post(String.class);
        return om.readValue(response, WorkspaceFile.class);
    }

    @Override
    public WorkspaceFile updateBlobKey(long workspaceFileId, String key) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public WorkspaceFile updatePathSizeAndFailStatus(long workspaceFileId, String path,
            final String size,final Boolean isFailed) throws Exception {
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(StringProvider.class);
        cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        Client client = Client.create(cc);
        client.addFilter(new HTTPBasicAuthFilter(_user.getLogin(),_user.getToken()));
        client.setFollowRedirects(true);
        WebResource resource = client.resource(_restURL).
                path(Constants.REST_PATH).path(Constants.WORKSPACEFILES_PATH).
                path(Long.toString(workspaceFileId));
        
        MultivaluedMap queryParams = _multivaluedMapFactory.getMultivaluedMap(_user);
        if (path != null){
            queryParams.add(Constants.PATH_QUERY_PARAM, path);
        }
        
        if (size != null){
            queryParams.add(Constants.SIZE_QUERY_PARAM, size);
        }
        if (isFailed != null){
            queryParams.add(Constants.WS_FAILED_QUERY_PARAM, isFailed.toString());
        }
        
         String json = resource.queryParams(queryParams)
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity("{}")
                .post(String.class);
         
           ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new ObjectifyJacksonModule());
        return mapper.readValue(json, new TypeReference<WorkspaceFile>() {
        });
    }

    @Override
    public WorkspaceFile update(WorkspaceFile wsp,Boolean isDeleted, 
            Boolean isFailed,Boolean isDir) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public WorkspaceFile update(WorkspaceFile wsp) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    

    @Override
    public WorkspaceFile resave(long workspaceFileId) throws Exception {
         ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(StringProvider.class);
        cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        Client client = Client.create(cc);
        client.addFilter(new HTTPBasicAuthFilter(_user.getLogin(),_user.getToken()));
        client.setFollowRedirects(true);
        WebResource resource = client.resource(_restURL).
                path(Constants.REST_PATH).path(Constants.WORKSPACEFILES_PATH).
                path(Long.toString(workspaceFileId));
        
        MultivaluedMap queryParams = _multivaluedMapFactory.getMultivaluedMap(_user);
        queryParams.add(Constants.RESAVE_QUERY_PARAM, "true");
        
         String json = resource.queryParams(queryParams)
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity("{}")
                .post(String.class);
         
           ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new ObjectifyJacksonModule());
        return mapper.readValue(json, new TypeReference<WorkspaceFile>() {
        });
    }

    @Override
    public List<WorkspaceFile> getWorkspaceFilesBySourceJobId(long sourceJobId) throws Exception {
        ClientConfig cc = new DefaultClientConfig();
        cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        Client client = Client.create(cc);
        client.addFilter(new HTTPBasicAuthFilter(_user.getLogin(),_user.getToken()));
        client.setFollowRedirects(true);
        WebResource resource = client.resource(_restURL).path(Constants.REST_PATH).path(Constants.WORKSPACEFILES_PATH);
        
        MultivaluedMap queryParams = _multivaluedMapFactory.getMultivaluedMap(_user);
        queryParams.add(Constants.SOURCE_JOB_ID_QUERY_PARAM, Long.toString(sourceJobId));

        String json = resource.queryParams(queryParams).accept(MediaType.APPLICATION_JSON).get(String.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new ObjectifyJacksonModule());
        return mapper.readValue(json, new TypeReference<List<WorkspaceFile>>() {
        });
    }

    @Override
    public DeleteReport delete(long workspaceFileId, 
            Boolean permanentlyDelete,
            boolean ignoreParentJob) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
