package edu.ucsd.crbs.cws.cluster;

import edu.ucsd.crbs.cws.workflow.Task;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * Submits Workflow Task Command Script to Panfish for processing
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class TaskCmdScriptSubmitterImpl implements TaskCmdScriptSubmitter {

    private String _panfishCast;
    private String _queue;
    
    public TaskCmdScriptSubmitterImpl(final String panfishCast,final String queue){
        _panfishCast = panfishCast;
        _queue = queue;
    }
    
    /**
     * Submits {@link Task} cmdScript which runs Task represented by t to Panfish
     * for processing. 
     * @param cmdScript Full path of script to submit to Panfish
     * @param t Task that will be run by cmdScript
     * @return String containing Job Id from Panfish to track job progress
     * @throws Exception 
     */
    @Override
    public String submit(final String cmdScript,
            Task t) throws Exception {

        String outputDir = new File(cmdScript).getParentFile().getAbsolutePath();

        ProcessBuilder pb = new ProcessBuilder(_panfishCast,
                "-q", _queue,
                "-N", getJobName(t),
                "-o", outputDir + File.separator + "stdout",
                "-e", outputDir + File.separator + "stderr",
                cmdScript);

        pb.directory(new File(outputDir));
        pb.redirectErrorStream(true);

        Process p = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

        StringBuilder sb = new StringBuilder();

        String line = reader.readLine();
        boolean firstLine = true;
        String jobId = null;
        while (line != null) {
            if (firstLine == true){
                // @TODO replace this with a grouping call so its just a single replaceAll
                jobId = line.replaceAll("^Your job ","").replaceAll(" .*","");
                firstLine = false;
            }
            sb.append(line).append("\n");
            line = reader.readLine();
        }
        reader.close();
        
        if (p.waitFor() != 0){
            throw new Exception("Non zero exit code received from Panfish: "+sb.toString());
        }
        
        //set the submit date
        t.setSubmitDate(new Date());
        
        t.setJobId(jobId);
        
        return sb.toString();
    }

    /**
     * Generates a job name in a format suitable for Sun/Oracle Grid Engine (SGE).<p/>
     * 
     * If {@link Task} owner is null this method will return unset_workflow<br/>
     * Otherwise method will create a string of format: (owner)_workflow-(task id)<br/>
     * Ex:  bob_workflow-234232345<p/>
     * 
     * In addition, the following characters are replaced with X character:
     * -- any whitespace characters<br/>
     * -- non-word characters<br/>
     * -- digits in the first character of job name     * 
     * @param t Task generate job name for
     * @return Job name to pass to Sun/Oracle Grid Engine using -N flag
     */
    private String getJobName(final Task t) {

        if (t.getOwner() == null) {
            return "unset_workflow-" + t.getId();
        }
        String tempOwner = t.getOwner();
        //SGE does not like these characters in job name so lets swap them with X 
        String spaceTossedOwner = tempOwner.replaceAll("\\s", "X");
        String digitTossedOwner = spaceTossedOwner.replaceAll("^[0-9]", "X");
        return digitTossedOwner.replaceAll("\\W|_", "X") + "_workflow-" + t.getId();
    }

}
