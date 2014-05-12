/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ucsd.crbs.cws.workflow;

import java.util.ArrayList;
import java.util.Date;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestWorkflow {

    public TestWorkflow() {
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
    public void testBasicGettersAndSetters(){
        Workflow w = new Workflow();
        
        assertTrue(w.getId() == null);
        assertTrue(w.getVersion() == 0);
        assertTrue(w.getCreateDate() == null);
        assertTrue(w.getReleaseNotes() == null);
        assertTrue(w.getName() == null);
        assertTrue(w.getParameters() == null);
        assertTrue(w.getDescription() == null);
        
        w.setId(new Long(2));
        w.setVersion(3);
        Date createDate = new Date();
        w.setCreateDate(createDate);
        w.setReleaseNotes("notes");
        w.setName("name");
        w.setParameters(new ArrayList<WorkflowParameter>());
        w.setDescription("description");
        
        assertTrue(w.getId() == 2);
        assertTrue(w.getVersion() == 3);
        assertTrue(w.getCreateDate().equals(createDate));
        assertTrue(w.getReleaseNotes().equals("notes"));
        assertTrue(w.getName().equals("name"));
        assertTrue(w.getParameters().isEmpty());
        assertTrue(w.getDescription().equals("description"));
    }

    /** @TODO add tests to verify get/setParentWorkflow works properly **/
}