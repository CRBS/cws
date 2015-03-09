/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ucsd.ccdb.segmentation.chm.rest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
/**
 * This class handle the output of the /chm_models URL.
 * 
 * @author Willy W. Wong
 */
@Path("/chm_models")
public class ChmModelsResource {
    
  @Context
  UriInfo uriInfo;
  @Context
  Request request;

  
  DBService db = new DBService();
  // Return the list of todos to the user in the browser
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  //@Produces(MediaType.TEXT_XML)
  public List<CHM_Model> getModelList() {
    DBUtil dbutil = new DBUtil();
        List<CHM_Model> list = new ArrayList<CHM_Model>();
        try
        {
        //list = dbutil.getAllChmModels();
          list = dbutil.getAllChmModelsWithDetails();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return list;
  }
  
  
    @Path("{id}")
            public ChmModelResource getModel(
                            @PathParam("id") String id) {
                    return new ChmModelResource(uriInfo, request, id);
            }
    
    
}
