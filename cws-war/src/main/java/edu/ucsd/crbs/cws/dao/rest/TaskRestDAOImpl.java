package edu.ucsd.crbs.cws.dao.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.objectify.util.jackson.ObjectifyJacksonModule;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import edu.ucsd.crbs.cws.dao.TaskDAO;
import edu.ucsd.crbs.cws.rest.Constants;
import edu.ucsd.crbs.cws.workflow.Task;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;


/**
 * Provides ways to obtain and persist Task objects via REST API calls
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class TaskRestDAOImpl implements TaskDAO {


    private String _restURL;

    public TaskRestDAOImpl() {

    }

    /**
     * Sets the base REST URL
     *
     * @param url
     */
    public void setRestURL(final String url) {
        _restURL = url;
    }

    @Override
    public Task getTaskById(String taskId) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Task> getTasks(String owner, String status, Boolean notSubmittedToScheduler, boolean noParams, boolean noWorkflowParams) throws Exception {
        ClientConfig cc = new DefaultClientConfig();
        cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        Client client = Client.create(cc);
        client.setFollowRedirects(true);
        WebResource resource = client.resource(_restURL).path(Constants.REST_PATH).path(Constants.TASKS_PATH);
        MultivaluedMap queryParams = new MultivaluedMapImpl();

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
            queryParams.add(Constants.NOPARAMS_QUERY_PARAM, true);
        }

        if (noWorkflowParams == true) {
            queryParams.add(Constants.NOWORKFLOWPARAMS_QUERY_PARAM, true);
        }

        String json = resource.queryParams(queryParams).accept(MediaType.APPLICATION_JSON).get(String.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new ObjectifyJacksonModule());
        return mapper.readValue(json, new TypeReference<List<Task>>(){});
    }

    @Override
    public Task insert(Task t) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Task update(long taskId, String status, long estCpu, long estRunTime, long estDisk, long submitDate, long startDate, long finishDate, boolean submittedToScheduler, String downloadURL) throws Exception {
        
        
        return null;
        
    }

}
