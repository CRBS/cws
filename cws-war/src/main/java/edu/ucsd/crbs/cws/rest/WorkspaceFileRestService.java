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

import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.UploadOptions;
import edu.ucsd.crbs.cws.auth.Permission;
import edu.ucsd.crbs.cws.auth.User;
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
import static edu.ucsd.crbs.cws.rest.JobRestService._authenticator;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
 * {@link WorkspaceFile} REST service.  
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@Path(Constants.SLASH + Constants.WORKSPACEFILES_PATH)
public class WorkspaceFileRestService {

    private static final Logger _log
            = Logger.getLogger(WorkspaceFileRestService.class.getName());

    static InputWorkspaceFileLinkDAO _inputWorkspaceFileLinkDAO = new InputWorkspaceFileLinkObjectifyDAOImpl();
    
    static JobDAO _jobDAO = new JobObjectifyDAOImpl(_inputWorkspaceFileLinkDAO);
    
    static WorkspaceFileDAO _workspaceFileDAO = new WorkspaceFileObjectifyDAOImpl(_jobDAO,
            _inputWorkspaceFileLinkDAO);

    static EventBuilder _eventBuilder = new EventBuilderImpl();
    static EventDAO _eventDAO = new EventObjectifyDAOImpl();

    public static final String WORKSPACEFILE_SERVLET_PATH = "/workspacefile";
    
    /**
     * Gets a list of {@link WorkspaceFile} objects
     * @param owner
     * @param workspaceFileIdList
     * @param sourceJobId
     * @param type
     * @param isFailed
     * @param synced
     * @param userLogin
     * @param userToken
     * @param userLoginToRunAs
     * @param request
     * @return 
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<WorkspaceFile> getWorkspaceFiles(@QueryParam(Constants.OWNER_QUERY_PARAM) final String owner,
            @QueryParam(Constants.WSFID_PARAM) final String workspaceFileIdList,
            @QueryParam(Constants.SOURCE_JOB_ID_QUERY_PARAM)final Long sourceJobId,
            @QueryParam(Constants.TYPE_QUERY_PARAM) final String type,
            @QueryParam(Constants.WS_FAILED_QUERY_PARAM)final Boolean isFailed,
            @QueryParam(Constants.SYNCED_QUERY_PARAM) final Boolean synced,
            @QueryParam(Constants.SHOW_DELETED_QUERY_PARAM) final Boolean showDeleted,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request) {
        
        return getWorkspaceFileList(owner,workspaceFileIdList,sourceJobId,type,isFailed,synced,showDeleted,
                userLogin,userToken,userLoginToRunAs,request);
    }
    
    private List<WorkspaceFile> getWorkspaceFileList(final String owner,
            final String workspaceFileIdList,
            final Long sourceJobId,
            final String type,
            final Boolean isFailed,
            final Boolean synced,
            final Boolean showDeleted,
            final String userLogin,
            final String userToken,
            final String userLoginToRunAs,
            HttpServletRequest request) throws WebApplicationException {
        try {
            User user = _authenticator.authenticate(request);
            Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());

            
            if (user.isAuthorizedTo(Permission.LIST_ALL_WORKSPACEFILES)) {
                if (sourceJobId != null){
                    return _workspaceFileDAO.getWorkspaceFilesBySourceJobId(sourceJobId);
                }
                if (workspaceFileIdList == null){
                    _log.log(Level.INFO,"calling getWorkspaceFiles");
                    
                    return _workspaceFileDAO.getWorkspaceFiles(owner,type,isFailed,synced,showDeleted);
                }
                _log.log(Level.INFO, "calling getWorkspaceFilesById: {0}", 
                        workspaceFileIdList);

                return _workspaceFileDAO.getWorkspaceFilesById(workspaceFileIdList, user);
            }
            if (user.isAuthorizedTo(Permission.LIST_THEIR_WORKSPACEFILES)){
                if (owner != null && !owner.equals(user.getLoginToRunJobAs())){
                    throw new Exception(user.getLoginToRunJobAs()+" cannot list workspace files owned by "+owner);
                }
                
                if (workspaceFileIdList == null){
                    _log.log(Level.INFO,"calling getWorkspaceFiles");
                    return _workspaceFileDAO.getWorkspaceFiles(user.getLoginToRunJobAs(),
                            type,isFailed,synced,showDeleted);
                }
                throw new Exception("Workspace files by id is NOT currently supported with only LIST_THEIR_WORKSPACEFILES permission");
            }
           throw new WebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
        }catch(WebApplicationException wae){
            _log.log(Level.SEVERE,"Caught WebApplicationException",wae);
            throw wae;

        } catch (Exception ex) {
            _log.log(Level.SEVERE, "Caught exception ", ex);
            throw new WebApplicationException(ex);
        }
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path(Constants.WORKSPACEFILES_AS_LIST_REST_PATH)
    public String getWorkspaceFilesAsList(@QueryParam(Constants.OWNER_QUERY_PARAM) final String owner,
            @QueryParam(Constants.WSFID_PARAM) final String workspaceFileIdList,
            @QueryParam(Constants.SOURCE_JOB_ID_QUERY_PARAM) final Long sourceJobId,
            @QueryParam(Constants.SYNCED_QUERY_PARAM) final Boolean synced,
            @QueryParam(Constants.TYPE_QUERY_PARAM) final String type,
            @QueryParam(Constants.WS_FAILED_QUERY_PARAM)final Boolean isFailed,
            @QueryParam(Constants.SHOW_DELETED_QUERY_PARAM) final Boolean showDeleted,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request) {
        
        List<WorkspaceFile> workspaceFileList = getWorkspaceFileList(owner,
                workspaceFileIdList,sourceJobId,type,isFailed,synced,showDeleted,
                userLogin,userToken,userLoginToRunAs,request);
        if (workspaceFileList == null || workspaceFileList.isEmpty()){
            return "";
        }
        
        StringBuilder result = new StringBuilder();
        for (WorkspaceFile wsf : workspaceFileList){
            if (wsf.getPath() == null){
                result.append("notsyncd - ");
            }
            if (wsf.getDir()){
                result.append("dir: ");
            }
            result.append(wsf.getName());
            result.append(" ( ");
            result.append(wsf.getType());
            result.append(" ) ");
            result.append(wsf.getSize());
            result.append(" bytes");
            result.append(getTruncatedDescription(wsf));
            result.append(Constants.DEFAULT_LINE_DELIMITER);
            result.append(wsf.getId());
            result.append("\n");
        }
        return result.toString();
    }

    private String getTruncatedDescription(WorkspaceFile wsf){
        if (wsf.getDescription() == null){
            return "";
        }
        int descLen = wsf.getDescription().length();
        StringBuilder sb = new StringBuilder();
        sb.append(" [");
        if (descLen < 50){
            sb.append(wsf.getDescription());
        }
        else {
            sb.append(wsf.getDescription().substring(0, 50));
        }
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Retrieves {@link WorkspaceFile} with corresponding <b>workspaceFileId</b>
     * 
     * @param workspaceFileId
     * @param addUploadURL
     * @param userLogin
     * @param userToken
     * @param userLoginToRunAs
     * @param request
     * @return 
     */
    @GET
    @Path(Constants.WORKSPACEFILE_ID_REST_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkspaceFile getWorkspaceFile(@PathParam(Constants.WORKSPACEFILE_ID_PATH_PARAM) final String workspaceFileId,
            @QueryParam(Constants.ADD_UPLOAD_URL_PARAM) final Boolean addUploadURL,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request) {
        try {
            User user = _authenticator.authenticate(request);
            Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());

            if (user.isAuthorizedTo(Permission.LIST_ALL_WORKSPACEFILES)) {
                WorkspaceFile wsf = _workspaceFileDAO.getWorkspaceFileById(workspaceFileId, user);
                if (wsf == null){
                    return wsf;
                }
                if (addUploadURL != null && addUploadURL == true){
                    wsf = setFileUploadURL(wsf);
                }
                return wsf;
            }
            throw new WebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
        }catch(WebApplicationException wae){
            _log.log(Level.SEVERE,"Caught WebApplicationException",wae);
            throw wae;
        } catch (Exception ex) {
            _log.log(Level.SEVERE, "Caught Exception", ex);
            throw new WebApplicationException(ex);
        }

    }
    
    /**
     * Creates a new {@link WorkspaceFile} in the data store
     * @param workspaceFile
     * @param addUploadURL
     * @param userLogin
     * @param userToken
     * @param userLoginToRunAs
     * @param request
     * @return 
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkspaceFile createWorkspaceFile(WorkspaceFile workspaceFile,
            @QueryParam(Constants.ADD_UPLOAD_URL_PARAM) final Boolean addUploadURL,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @Context HttpServletRequest request) {

         try {
            User user = _authenticator.authenticate(request);
            Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());
            if (user.isAuthorizedTo(Permission.CREATE_WORKSPACEFILE) ||
                user.isAuthorizedTo(Permission.CREATE_ANY_WORKSPACEFILE)) {
                
                //if user is NOT authorized to create any workspace file then set owner
                //to the user
                if (!user.isAuthorizedTo(Permission.CREATE_ANY_WORKSPACEFILE)){
                    workspaceFile.setOwner(user.getLoginToRunJobAs());
                }
                WorkspaceFile resWorkspaceFile = _workspaceFileDAO.insert(workspaceFile,false);
                if (addUploadURL == null || addUploadURL == true){
                    resWorkspaceFile = setFileUploadURL(resWorkspaceFile);
                }
                _eventDAO.neverComplainInsert(_eventBuilder.setAsCreateWorkspaceFileEvent(event, 
                        resWorkspaceFile));
                
                return resWorkspaceFile;
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
     * Updates an existing {@link WorkspaceFile} with id of <b>workspaceFileId</b>
     * 
     * @param workspaceFileId
     * @param path
     * @param size
     * @param userLogin
     * @param userToken
     * @param userLoginToRunAs
     * @param resave
     * @param request
     * @return 
     */
    @POST
    @Path(Constants.WORKSPACEFILE_ID_REST_PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkspaceFile updateWorkspaceFile(@PathParam(Constants.WORKSPACEFILE_ID_PATH_PARAM)final Long workspaceFileId,
            @QueryParam(Constants.PATH_QUERY_PARAM) final String path,
            @QueryParam(Constants.SIZE_QUERY_PARAM) final String size,
            @QueryParam(Constants.WS_FAILED_QUERY_PARAM) final Boolean isFailed,
            @QueryParam(Constants.DELETED_QUERY_PARAM) final Boolean isDeleted,
            @QueryParam(Constants.USER_LOGIN_PARAM) final String userLogin,
            @QueryParam(Constants.USER_TOKEN_PARAM) final String userToken,
            @QueryParam(Constants.USER_LOGIN_TO_RUN_AS_PARAM) final String userLoginToRunAs,
            @QueryParam(Constants.RESAVE_QUERY_PARAM) final String resave,
            @Context HttpServletRequest request) {
    
         try {
            User user = _authenticator.authenticate(request);
            Event event = _eventBuilder.createEvent(request, user);
            _log.info(event.getStringOfLocationData());
            
            if (user.isAuthorizedTo(Permission.UPDATE_ALL_WORKSPACEFILES)) {
                if (resave != null && resave.equalsIgnoreCase("true")){
                    return _workspaceFileDAO.resave(workspaceFileId);
                }
                WorkspaceFile updatedWsp = new WorkspaceFile();
                updatedWsp.setId(workspaceFileId);
                if (path != null && !path.equals("")){
                    updatedWsp.setPath(path);
                }
                if (size != null){
                    updatedWsp.setSize(new Long(size));
                }
                
                WorkspaceFile resWorkspaceFile = _workspaceFileDAO.update(updatedWsp, isDeleted, isFailed,null);
                return resWorkspaceFile;
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
     * Generates and sets an upload url in <b>wsf</b> passed in
     * @param wsf
     * @return
     * @throws Exception 
     */
    private WorkspaceFile setFileUploadURL(WorkspaceFile wsf) throws Exception {
        //build upload URL and add it to workflow
            BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
            
            //@TODO need to cache the AppIdentity Service factory default google bucket 
            wsf.setUploadURL(blobstoreService.createUploadUrl(WORKSPACEFILE_SERVLET_PATH,
                    UploadOptions.Builder.withGoogleStorageBucketName(AppIdentityServiceFactory.getAppIdentityService().getDefaultGcsBucketName()+
                            "/workspacefile/"+wsf.getOwner()+"/")));
            return wsf;
    }
}
