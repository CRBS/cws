/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ucsd.ccdb.segmentation.chm.servlet;

import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import edu.ucsd.ccdb.segmentation.chm.rest.*;

/**
 * ModelDownloadServlet is the Servlet Class to allow the external users to download 
 * the CHM model files.
 * 
 * @author Willy W. Wong
 * @version 1.0.1
 * 
 */
public class ModelDownloadServlet extends HttpServlet {

    ServletConfig config = null;

    DBUtil dbutil = new DBUtil();
    
    /**
     * This is the HTTP doGet method for downloading the CHM model files.
     * 
     * 
     * @param request - is supposed to contain the parameter, "id", which is the id for the model in the database
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    protected void doGet( HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

         String sid = request.getParameter("id");
         long id = Long.parseLong(sid);
         CHM_Model cmodel = null;
         try
         {
             cmodel = dbutil.getChmModel(id);
         }
         catch(Exception e)
         {
             e.printStackTrace();
         }

         if(cmodel == null)
             throw new IOException("Cannot find CHM model with the ID, "+id);
         
         String fileName = cmodel.getMODEL_NAME();
         String fileType = "";
         // Find this file id in database to get file name, and file type

         // You must tell the browser the file type you are going to send
         // for example application/pdf, text/plain, text/html, image/jpg
         response.setContentType(fileType);

         // Make sure to show the download dialog
         response.setHeader("Content-disposition","attachment; filename="+fileName+"-"+cmodel.getId()+".zip");

         // Assume file name is retrieved from database
         // For example D:\\file\\test.pdf
         File folder = new File(this.getChmModelFolder());
         if(!folder.exists())
            throw new IOException("CHM folder, "+folder.getAbsolutePath()+" does not exist!");
         
         File my_file = new File(folder,cmodel.getMODEL_NAME()+".zip");
         if(!my_file.exists())
            throw new IOException("CHM model file, "+my_file.getAbsolutePath()+" does not exist!");
         
         // This should send the file to browser
         OutputStream out = response.getOutputStream();
         FileInputStream in = new FileInputStream(my_file);
         byte[] buffer = new byte[4096];
         int length;
         while ((length = in.read(buffer)) > 0){
            out.write(buffer, 0, length);
         }
         in.close();
         out.flush();
    }
    
    /**
     * This is the helper class for getting the default CHM model directory.
     * @return 
     */
    private String getChmModelFolder()
    {
       return  this.config.getServletContext().getInitParameter("chm_model_folder");
        
    }
    
    /**
     * This is the method for the servlet to initialize.
     * 
     * @param config
     * @throws ServletException 
     */
     public void init(ServletConfig config) throws ServletException
    {
       this.config = config;
        
        
    }
}