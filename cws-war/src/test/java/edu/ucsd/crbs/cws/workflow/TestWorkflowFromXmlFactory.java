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
        
        URL resourceUrl = getClass().getResource("/example.old.kar");
        Path resourcePath = Paths.get(resourceUrl.toURI());
        xmlFactory.setWorkflowXml(KeplerMomlFromKar.getInputStreamOfWorkflowMoml(resourcePath.toFile()));
        
        Workflow w = xmlFactory.getWorkflow();
        
        assertTrue(w.getName().equals("Example Workflow"));
        assertTrue(w.getDescription().startsWith("This is an example workflow that can be used as a template\n\nThe Blue"));
        assertTrue(w.getDescription().contains("This text is the description for the workflow."));
        assertTrue(w.getReleaseNotes().contains("-- Some new feature"));
        assertTrue(w.getParameters().size() == 10);

        //put parameters into a hash to make it easier to test
        HashMap<String,WorkflowParameter> paramHash = new HashMap<>();
        for (WorkflowParameter wp : w.getParameters()){
            paramHash.put(wp.getName(), wp);
        }
        
        WorkflowParameter wp;
       
        
        //example dropdown
        wp = paramHash.get("exampledropdown");
        assertTrue(wp != null);
        assertTrue(wp.getDisplayName().equals("Example Drop Down"));
        assertTrue(wp.getNameValueDelimiter().equals("=="));
        assertTrue(wp.getHelp().startsWith("Example drop down"));
        assertTrue(wp.getIsAdvanced() == false);
        assertTrue(wp.getIsRequired() == false);
        assertTrue(wp.getType().equals(WorkflowParameter.Type.DROP_DOWN));
        assertTrue(wp.getValidationHelp() == null);
        assertTrue(wp.getValidationRegex() == null);
        assertTrue(wp.getValidationType() == null);
        assertTrue(wp.getValue().startsWith("https"));
        assertTrue(wp.getColumns() == 0L);
        assertTrue(wp.getMaxFileSize() == 0L);
        assertTrue(wp.getMaxLength() == 0L);
        assertTrue(wp.getMaxValue() == 0L);
        assertTrue(wp.getMinValue() == 0L);
        assertTrue(wp.getRows() == 0L);
        assertTrue(wp.getValueMap() == null);
        assertTrue(wp.getSelected().equals("name3"));
        
        //example file parameter
        wp = paramHash.get("examplefile");
        assertTrue(wp != null);
        assertTrue(wp.getDisplayName().equals("Example File"));
        assertTrue(wp.getNameValueDelimiter() == null);
        assertTrue(wp.getHelp().startsWith("This parameter takes a file"));
        assertTrue(wp.getIsAdvanced() == false);
        assertTrue(wp.getIsRequired() == true);
        assertTrue(wp.getType().equals(WorkflowParameter.Type.FILE));
        assertTrue(wp.getValidationHelp().startsWith("Must be"));
        assertTrue(wp.getValidationRegex() == null);
        assertTrue(wp.getValidationType() == null);
        assertTrue(wp.getValue().equals("/somepath"));
        assertTrue(wp.getColumns() == 0L);
        assertTrue(wp.getMaxFileSize() == 20000000L);
        assertTrue(wp.getMaxLength() == 0L);
        assertTrue(wp.getMaxValue() == 0L);
        assertTrue(wp.getMinValue() == 0L);
        assertTrue(wp.getRows() == 0L);
        assertTrue(wp.getValueMap() == null);
        assertTrue(wp.getSelected() == null);

        
        //example string parameter
        wp = paramHash.get("exampletext");
        assertTrue(wp != null);
        assertTrue(wp.getDisplayName().equals("Example Text"));
        assertTrue(wp.getNameValueDelimiter() == null);
        assertTrue(wp.getHelp().startsWith("Example"));
        assertTrue(wp.getIsAdvanced() == false);
        assertTrue(wp.getIsRequired() == false);
        assertTrue(wp.getType().equals(WorkflowParameter.Type.TEXT));
        assertTrue(wp.getValidationHelp() == null);
        assertTrue(wp.getValidationRegex().equals("^.*blah.*"));
        assertTrue(wp.getValidationType().equals("string"));
        assertTrue(wp.getValue().equals("blah blah text"));
        assertTrue(wp.getColumns() == 0L);
        assertTrue(wp.getMaxFileSize() == 0L);
        assertTrue(wp.getMaxLength() == 0L);
        assertTrue(wp.getMaxValue() == 0L);
        assertTrue(wp.getMinValue() == 0L);
        assertTrue(wp.getRows() == 0L);
        assertTrue(wp.getValueMap() == null);
        assertTrue(wp.getSelected() == null);
        
        
        //example number parameter
        wp = paramHash.get("examplenumber");
        assertTrue(wp != null);
        assertTrue(wp.getDisplayName().equals("Example Number"));
        assertTrue(wp.getNameValueDelimiter() == null);
        assertTrue(wp.getHelp().startsWith("Example number parameter"));
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
        assertTrue(wp.getSelected() == null);
        
        //example checkbox parameter
        wp = paramHash.get("examplecheckbox");
        assertTrue(wp != null);
        assertTrue(wp.getDisplayName().equals("Example Checkbox"));
        assertTrue(wp.getNameValueDelimiter() == null);
        assertTrue(wp.getHelp().startsWith("Example checkbox"));
        assertTrue(wp.getIsAdvanced() == false);
        assertTrue(wp.getIsRequired() == false);
        assertTrue(wp.getType().equals(WorkflowParameter.Type.CHECK_BOX));
        assertTrue(wp.getValidationHelp() == null);
        assertTrue(wp.getValidationRegex() == null);
        assertTrue(wp.getValidationType() == null);
        assertTrue(wp.getValue().equals("false"));
        assertTrue(wp.getColumns() == 0L);
        assertTrue(wp.getMaxFileSize() == 0L);
        assertTrue(wp.getMaxLength() == 0L);
        assertTrue(wp.getMaxValue() == 0L);
        assertTrue(wp.getMinValue() == 0L);
        assertTrue(wp.getRows() == 0L);
        assertTrue(wp.getValueMap() == null);
        assertTrue(wp.getSelected() == null);
        
       //example textarea parameter
        wp = paramHash.get("exampletextarea");
        assertTrue(wp != null);
        assertTrue(wp.getDisplayName().equals("Example Text Area"));
        assertTrue(wp.getNameValueDelimiter() == null);
        assertTrue(wp.getHelp().startsWith("Example Text area"));
        assertTrue(wp.getIsAdvanced() == false);
        assertTrue(wp.getIsRequired() == false);
        assertTrue(wp.getType().equals(WorkflowParameter.Type.TEXT_AREA));
        assertTrue(wp.getValidationHelp() == null);
        assertTrue(wp.getValidationRegex() == null);
        assertTrue(wp.getValidationType().equals("string"));
        assertTrue(wp.getValue().equals("blah blah text area"));
        assertTrue(wp.getColumns() == 30L);
        assertTrue(wp.getMaxFileSize() == 0L);
        assertTrue(wp.getMaxLength() == 0L);
        assertTrue(wp.getMaxValue() == 0L);
        assertTrue(wp.getMinValue() == 0L);
        assertTrue(wp.getRows() == 2L);
        assertTrue(wp.getValueMap() == null);
        assertTrue(wp.getSelected() == null);
        
        
        wp = paramHash.get("CWS_outputdir");
        assertTrue(wp != null);
        assertTrue(wp.getDisplayName().equals("CWS_outputdir"));
        assertTrue(wp.getNameValueDelimiter() == null);
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
        assertTrue(wp.getSelected() == null);
        
        wp = paramHash.get("CWS_user");
        assertTrue(wp != null);
        assertTrue(wp.getDisplayName().equals("CWS_user"));
        assertTrue(wp.getNameValueDelimiter() == null);
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
        assertTrue(wp.getSelected() == null);
        
        wp = paramHash.get("CWS_taskname");
        assertTrue(wp != null);
        assertTrue(wp.getDisplayName().equals("CWS_taskname"));
        assertTrue(wp.getNameValueDelimiter() == null);
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
        assertTrue(wp.getSelected() == null);
        
        wp = paramHash.get("CWS_taskid");
        assertTrue(wp != null);
        assertTrue(wp.getDisplayName().equals("CWS_taskid"));
        assertTrue(wp.getNameValueDelimiter() == null);
        assertTrue(wp.getHelp() == null);
        assertTrue(wp.getIsAdvanced() == false);
        assertTrue(wp.getIsRequired() == false);
        assertTrue(wp.getType().equals(WorkflowParameter.Type.HIDDEN));
        assertTrue(wp.getValidationHelp() == null);
        assertTrue(wp.getValidationRegex() == null);
        assertTrue(wp.getValidationType() == null);
        assertTrue(wp.getValue().equals("taskid"));
        assertTrue(wp.getColumns() == 0L);
        assertTrue(wp.getMaxFileSize() == 0L);
        assertTrue(wp.getMaxLength() == 0L);
        assertTrue(wp.getMaxValue() == 0L);
        assertTrue(wp.getMinValue() == 0L);
        assertTrue(wp.getRows() == 0L);
        assertTrue(wp.getValueMap() == null);
        assertTrue(wp.getSelected() == null);
        
        
    }

}