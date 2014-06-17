package edu.ucsd.crbs.cws.workflow;

import edu.ucsd.crbs.cws.io.KeplerMomlFromKar;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestWorkflowFromXmlFactory {
    
    @Rule
    public TemporaryFolder Folder = new TemporaryFolder();

    public TestWorkflowFromXmlFactory() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    
    @Test
    public void testWhereWorkflowStreamIsNull() throws Exception {
        WorkflowFromXmlFactory xmlFactory = new WorkflowFromXmlFactory();
        xmlFactory.setWorkflowXml(null);
        assertTrue(xmlFactory.getWorkflow() == null);
    }
    
    @Test
    public void testWhereWorkflowStreamPointsToEmptyFile() throws Exception {
        WorkflowFromXmlFactory xmlFactory = new WorkflowFromXmlFactory();
        xmlFactory.setWorkflowXml(new FileInputStream(Folder.newFile("tempy.xml")));
        assertTrue(xmlFactory.getWorkflow() == null);
    }
    
    @Test
    public void testExampleKarInTestResourcesDirectory() throws Exception {
        WorkflowFromXmlFactory xmlFactory = new WorkflowFromXmlFactory();
        
        URL resourceUrl = getClass().getResource("/example.kar");
        Path resourcePath = Paths.get(resourceUrl.toURI());
        xmlFactory.setWorkflowXml(KeplerMomlFromKar.getInputStreamOfWorkflowMoml(resourcePath.toFile()));
        
        Workflow w = xmlFactory.getWorkflow();
        
        assertTrue(w.getName().equals("Example Workflow"));
        assertTrue(w.getDescription().startsWith("This is an example workflow that can be used as a template\n\nThe Blue"));
        assertTrue(w.getDescription().contains("This text is the description for the workflow."));
        assertTrue(w.getReleaseNotes().contains("-- Some new feature"));
        assertTrue(w.getParameters().size() == 6);

        //put parameters into a hash to make it easier to test
        HashMap<String,WorkflowParameter> paramHash = new HashMap<>();
        for (WorkflowParameter wp : w.getParameters()){
            paramHash.put(wp.getName(), wp);
        }
        
        WorkflowParameter wp;
       
        //example file parameter
        wp = paramHash.get("examplefileparam");
        assertTrue(wp != null);
        assertTrue(wp != null);
        assertTrue(wp.getDisplayName().equals("Example File Param"));
        assertTrue(wp.getDelimiterValue() == null);
        assertTrue(wp.getHelp().equals("Query sequence(s) in fasta format to be used for a BLAST seach."));
        assertTrue(wp.getIsAdvanced() == false);
        assertTrue(wp.getIsRequired() == true);
        assertTrue(wp.getType().equals(WorkflowParameter.Type.FILE));
        assertTrue(wp.getValidationHelp().startsWith("Must be set to an uncompressed"));
        assertTrue(wp.getValidationRegex() == null);
        // @TODO NUCLEOTIDEFASTA is no longer a valid validation type so we should switch it in the kar
        assertTrue(wp.getValidationType().equals("NUCLEOTIDEFASTA"));
        assertTrue(wp.getValue().equals("/somepath"));
        assertTrue(wp.getColumns() == 0L);
        assertTrue(wp.getMaxFileSize() == 20000000L);
        assertTrue(wp.getMaxLength() == 0L);
        assertTrue(wp.getMaxValue() == 0L);
        assertTrue(wp.getMinValue() == 0L);
        assertTrue(wp.getRows() == 0L);
        assertTrue(wp.getValueMap() == null);
        
        //example string parameter
        wp = paramHash.get("examplestringparam");
        assertTrue(wp != null);
        assertTrue(wp.getDisplayName().equals("Example String Param"));
        assertTrue(wp.getDelimiterValue() == null);
        assertTrue(wp.getHelp().startsWith("With this option set to true you can cut and paste"));
        assertTrue(wp.getIsAdvanced() == false);
        assertTrue(wp.getIsRequired() == false);
        assertTrue(wp.getType().equals(WorkflowParameter.Type.TEXT));
        assertTrue(wp.getValidationHelp() == null);
        assertTrue(wp.getValidationRegex().equals("^false|true$"));
        assertTrue(wp.getValidationType().equals("string"));
        assertTrue(wp.getValue().equals("false"));
        assertTrue(wp.getColumns() == 0L);
        assertTrue(wp.getMaxFileSize() == 0L);
        assertTrue(wp.getMaxLength() == 0L);
        assertTrue(wp.getMaxValue() == 0L);
        assertTrue(wp.getMinValue() == 0L);
        assertTrue(wp.getRows() == 0L);
        assertTrue(wp.getValueMap() == null);
        
        
        //example number parameter
        wp = paramHash.get("examplenumberparam");
        assertTrue(wp != null);
        assertTrue(wp.getDisplayName().equals("Example Number Param"));
        assertTrue(wp.getDelimiterValue() == null);
        assertTrue(wp.getHelp().equals("Sets the upper limit on number of database  sequences to show alignments. Maximum allowed value is 50,000."));
        assertTrue(wp.getIsAdvanced() == false);
        assertTrue(wp.getIsRequired() == false);
        assertTrue(wp.getType().equals(WorkflowParameter.Type.TEXT));
        assertTrue(wp.getValidationHelp().equals("Must be set to a whole number between 1 and 50000"));
        assertTrue(wp.getValidationRegex() == null);
        assertTrue(wp.getValidationType().equals("digits"));
        assertTrue(wp.getValue().equals("25"));
        assertTrue(wp.getColumns() == 0L);
        assertTrue(wp.getMaxFileSize() == 0L);
        assertTrue(wp.getMaxLength() == 0L);
        assertTrue(wp.getMaxValue() == 50000L);
        assertTrue(wp.getMinValue() == 1L);
        assertTrue(wp.getRows() == 0L);
        assertTrue(wp.getValueMap() == null);

        wp = paramHash.get("CWS_outputdir");
        assertTrue(wp != null);
        assertTrue(wp.getDisplayName().equals("CWS_outputdir"));
        assertTrue(wp.getDelimiterValue() == null);
        assertTrue(wp.getHelp() == null);
        assertTrue(wp.getIsAdvanced() == false);
        assertTrue(wp.getIsRequired() == false);
        assertTrue(wp.getType().equals(WorkflowParameter.Type.HIDDEN));
        assertTrue(wp.getValidationHelp() == null);
        assertTrue(wp.getValidationRegex() == null);
        assertTrue(wp.getValidationType() == null);
        assertTrue(wp.getValue().equals("/tmp"));
        assertTrue(wp.getColumns() == 0L);
        assertTrue(wp.getMaxFileSize() == 0L);
        assertTrue(wp.getMaxLength() == 0L);
        assertTrue(wp.getMaxValue() == 0L);
        assertTrue(wp.getMinValue() == 0L);
        assertTrue(wp.getRows() == 0L);
        assertTrue(wp.getValueMap() == null);
        
        wp = paramHash.get("CWS_user");
        assertTrue(wp != null);
        assertTrue(wp.getDisplayName().equals("CWS_user"));
        assertTrue(wp.getDelimiterValue() == null);
        assertTrue(wp.getHelp() == null);
        assertTrue(wp.getIsAdvanced() == false);
        assertTrue(wp.getIsRequired() == false);
        assertTrue(wp.getType().equals(WorkflowParameter.Type.HIDDEN));
        assertTrue(wp.getValidationHelp() == null);
        assertTrue(wp.getValidationRegex() == null);
        assertTrue(wp.getValidationType() == null);
        assertTrue(wp.getValue().equals("user"));
        assertTrue(wp.getColumns() == 0L);
        assertTrue(wp.getMaxFileSize() == 0L);
        assertTrue(wp.getMaxLength() == 0L);
        assertTrue(wp.getMaxValue() == 0L);
        assertTrue(wp.getMinValue() == 0L);
        assertTrue(wp.getRows() == 0L);
        assertTrue(wp.getValueMap() == null);
        
        wp = paramHash.get("CWS_taskname");
        assertTrue(wp != null);
        assertTrue(wp.getDisplayName().equals("CWS_taskname"));
        assertTrue(wp.getDelimiterValue() == null);
        assertTrue(wp.getHelp() == null);
        assertTrue(wp.getIsAdvanced() == false);
        assertTrue(wp.getIsRequired() == false);
        assertTrue(wp.getType().equals(WorkflowParameter.Type.HIDDEN));
        assertTrue(wp.getValidationHelp() == null);
        assertTrue(wp.getValidationRegex() == null);
        assertTrue(wp.getValidationType() == null);
        assertTrue(wp.getValue().equals("taskname"));
        assertTrue(wp.getColumns() == 0L);
        assertTrue(wp.getMaxFileSize() == 0L);
        assertTrue(wp.getMaxLength() == 0L);
        assertTrue(wp.getMaxValue() == 0L);
        assertTrue(wp.getMinValue() == 0L);
        assertTrue(wp.getRows() == 0L);
        assertTrue(wp.getValueMap() == null);
        
        
    }

}