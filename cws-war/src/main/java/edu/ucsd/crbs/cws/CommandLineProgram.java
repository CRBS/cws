package edu.ucsd.crbs.cws;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.impl.provider.entity.StringProvider;
import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.WorkflowFromXmlFactory;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.ws.rs.core.MediaType;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

/**
 * Command line program that takes a kepler xml file or kar file and generates
 * JSON representation of the workflow parameters to display to the user or
 * to push to the service defined at the URL specified on the command line
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class CommandLineProgram {

    public static final String XML_SUFFIX = ".xml";

    public static void main(String[] args) {

        if (args.length < 1) {
            System.err.println("Usage: <workflow xml or kar file> (optional <url to post workflow to>)");
            System.err.println("\nThis program reads a kepler moml file 1.x|2.x and generates a json representation");
            System.err.println("of the parameters needed to run the workflow.  If the optional <url to post workflow to>");
            System.err.println("is passed this program will invoke a http POST with the json data to insert the entry");
            System.exit(1);
        }
        try {
            String workflowFile = args[0];
            String postURL = null;
            Long parentWfId = null;

            if (args.length >= 2) {
                postURL = args[1];
            }

            if (args.length == 3) {
                parentWfId = new Long(args[2]);
            }

            WorkflowFromXmlFactory xmlFactory = new WorkflowFromXmlFactory();
            xmlFactory.setWorkflowXml(new BufferedInputStream(getInputStreamOfWorkflowMoml(workflowFile)));

            Workflow w = xmlFactory.getWorkflow();
            if (w != null) {
                ObjectMapper om = new ObjectMapper();
                if (parentWfId != null) {
                    w.setId(parentWfId);
                }
                if (postURL == null) {
                    System.out.println("\n--- JSON Representation of Workflow ---");
                    ObjectWriter ow = om.writerWithDefaultPrettyPrinter();
                    System.out.println(ow.writeValueAsString(w));
                    System.out.flush();
                    System.out.println("---------------------------------------");

                } else {
                    ClientConfig cc = new DefaultClientConfig();
                    cc.getClasses().add(StringProvider.class);
                    Client client = Client.create(cc);
                    client.setFollowRedirects(true);
                    WebResource resource = client.resource(postURL);
                    String workflowAsJson = om.writeValueAsString(w);

                    String response = resource.type(MediaType.APPLICATION_JSON_TYPE)
                            .entity(workflowAsJson)
                            .post(String.class);
                    System.out.println("response: " + response);

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(2);
        }

        System.exit(0);
    }

    /**
     * Returns an input stream to moml file within path passed in. If the path
     * ends with .xml it is assumed to be a moml file and an inputstream is
     * opened on it. Otherwise the file is assumed to be a .kar file and is
     * opened as a zip file and the first entry within that is a file ending
     * with .xml and has a non zero size is assumed to be a moml file and an
     * inputstream is opened from that path. <br/>
     * It is the responsibility of the caller to close the InputStream
     *
     * @param workflowFile
     * @return InputStream object pointing to start of moml file or null if no
     * moml file is found
     * @throws Exception
     */
    public static InputStream getInputStreamOfWorkflowMoml(final String workflowFile) throws Exception {

        if (workflowFile == null) {
            throw new NullPointerException("workflow file is null");
        }

        //if the path ends with .xml assume it is a moml file and return a FileInputStream
        if (workflowFile.endsWith(XML_SUFFIX)) {
            return new FileInputStream(workflowFile);
        }

        //If we are here, we are assuming the file is a kar (compressed zip file)
        //which is actually a jar file so use JarFile to iterate through all
        //entries til we find a non zero size entry that is not a directory and
        //ends with .xml
        JarFile jf = new JarFile(workflowFile);
        JarEntry je = null;
        for (Enumeration<JarEntry> e = jf.entries(); e.hasMoreElements();) {
            je = e.nextElement();

            if (je.isDirectory() == true) {
                continue;
            }
            if (je.getSize() == 0 || !je.getName().endsWith(XML_SUFFIX)) {
                continue;
            }

            // if we arrived here we found an xml file open a stream to that
            // file hehehe
            return jf.getInputStream(je);
        }
        //didn't find anything just return null
        return null;
    }
}
