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


import javax.ws.rs.core.Response;
/**
 * This class handles the output of (/chm_models/$id) URL
 * 
 * @author Willy W. Wong
 */
public class ChmModelResource {
    @Context
	UriInfo uriInfo;
	@Context
	Request request;
	String id;
	
        DBService db = new DBService();
	public ChmModelResource(UriInfo uriInfo, Request request, 
			String id) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
        //@Produces({MediaType.TEXT_XML})
	public CHM_Model getModel() {
            
            DBUtil dbutil = new DBUtil();
                
		CHM_Model model = null;
               
                try
                {
                    model = dbutil.getChmModel(Long.parseLong(id));
                    if(model == null)
                        throw new ChmModelNotFoundException("Model, "+id+" cannot be found!");
    
		
                }
                catch(NumberFormatException ne)
                {
                    throw new ChmModelNotFoundException("Invalid model ID, "+id);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    throw new ChmModelNotFoundException("Model, "+id+" cannot be found!");
                }
                    
		return model;
	}
        
     
  // for the browser
 /* @GET
  @Produces(MediaType.APPLICATION_JSON)
  public CHM_Model getContactHTML() {
     DBUtil dbutil = new DBUtil();
                
		CHM_Model model = null;
               
                try
                {
                    model = dbutil.getChmModel(Long.parseLong(id));
		
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                    
		return model;
  }*/
}
