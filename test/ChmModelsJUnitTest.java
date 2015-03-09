/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import junit.framework.TestCase;
import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import junit.framework.Assert;
import java.io.*;
import java.util.*;
import java.net.*;
/**
 *
 * @author ncmir
 */
public class ChmModelsJUnitTest extends TestCase {
    
    public ChmModelsJUnitTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    
   
    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}

    public void testCHMModels()
    {
        ClientConfig config = new DefaultClientConfig();
    Client client = Client.create(config);
    URI uri =  UriBuilder.fromUri("http://localhost:8080/CCDBSlashChmService").build();
    WebResource service = client.resource(uri);

      String json = service.path("rest").path("chm_models").accept(MediaType.APPLICATION_JSON).get(String.class);
      //System.out.println(json);
      Assert.assertTrue(json.contains("CHM_Model"));
    }
    
    
    public void testSingleCHMModel()
    {
         ClientConfig config = new DefaultClientConfig();
    Client client = Client.create(config);
    URI uri =  UriBuilder.fromUri("http://localhost:8080/CCDBSlashChmService").build();
    WebResource service = client.resource(uri);

      String json = service.path("rest").path("chm_models/5235515").accept(MediaType.APPLICATION_JSON).get(String.class);
      //System.out.println(json);
      Assert.assertTrue(json.contains("81739"));
        
        
    }
    

    public void testDownloadModelFile()
    {
        long total = 0;
        try
        {
            String urlString = "http://localhost:8080/CCDBSlashChmService/ChmModelDownloadServlet?id=5235515";
            BufferedInputStream in = new BufferedInputStream(new URL(urlString).openStream());
             final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                //System.out.println(count);
                total=total+count;
            }
        
        }
        catch(Exception e)
        {
            
        }
        /*System.out.println("total file size:"+total);
        boolean result = false;
        if(total > 0)
            result = true;*/
        
        Assert.assertEquals(total, 33392946);
        
    }
 
        
        
    
    

 
}
