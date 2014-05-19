package edu.ucsd.crbs.cws.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class ResourceToExecutableScriptWriterImpl {
    
    
    public void writeResourceToScript(final String resourcePath,final String destinationScript,StringReplacer replacer) throws Exception {
         if (resourcePath == null){
            throw new IllegalArgumentException("resourcePath method parameter cannot be null");
        }
         
        if (destinationScript == null){
            throw new IllegalArgumentException("destinationScript method parameter cannot be null");
        }
        
        //load script
        List<String> scriptLines = IOUtils.readLines(Class.class.getResourceAsStream(resourcePath));

        BufferedWriter bw = new BufferedWriter(new FileWriter(destinationScript));
                        
        for (String line : scriptLines){
            if (replacer != null){
                bw.write(replacer.replace(line));
            }
            else {
                bw.write(line);
            }
            bw.newLine();
        }
        bw.flush();
        bw.close();
        
        //make script executable
        File script = new File(destinationScript);
        script.setExecutable(true, false);
        
    }
}
