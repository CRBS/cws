package edu.ucsd.crbs.cws.io;

import static edu.ucsd.crbs.cws.App.XML_SUFFIX;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class KeplerMomlFromKar {

    
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
    public static InputStream getInputStreamOfWorkflowMoml(final File workflowFile) throws Exception {

        if (workflowFile == null) {
            throw new NullPointerException("workflow file is null");
        }

        //if the path ends with .xml assume it is a moml file and return a FileInputStream
        if (workflowFile.getAbsolutePath().endsWith(XML_SUFFIX)) {
            return new FileInputStream(workflowFile);
        }

        //If we are here, we are assuming the file is a kar (compressed zip file)
        //which is actually a jar file so use JarFile to iterate through all
        //entries til we find a non zero size entry that is not a directory and
        //ends with .xml
        JarFile jf = new JarFile(workflowFile);
        JarEntry je;
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
