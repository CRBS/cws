/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ucsd.ccdb.segmentation.chm.rest;
import javax.ws.rs.core.Response;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
/**
 * This class is for throwing the CHM Model Found Exception.
 * 
 * @author Willy W. Wong
 */
public class ChmModelNotFoundException  extends WebApplicationException {


      public ChmModelNotFoundException(String message) {
          super(Response.status(Status.NOT_FOUND).
                  entity(message).type("text/plain").build());
      }
  
    
}
