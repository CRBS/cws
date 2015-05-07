/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2015 The Regents of the University of California All Rights Reserved
 * 
 * Permission to copy, modify and distribute any part of this CRBS Workflow 
 * Service for educational, research and non-profit purposes, without fee, and
 * without a written agreement is hereby granted, provided that the above 
 * copyright notice, this paragraph and the following three paragraphs appear
 * in all copies.
 * 
 * Those desiring to incorporate this CRBS Workflow Service into commercial 
 * products or use for commercial purposes should contact the Technology
 * Transfer Office, University of California, San Diego, 9500 Gilman Drive, 
 * Mail Code 0910, La Jolla, CA 92093-0910, Ph: (858) 534-5815, 
 * FAX: (858) 534-7345, E-MAIL:invent@ucsd.edu.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR 
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING 
 * LOST PROFITS, ARISING OUT OF THE USE OF THIS CRBS Workflow Service, EVEN IF 
 * THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 * 
 * THE CRBS Workflow Service PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE
 * UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, 
 * UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES
 * NO REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR 
 * EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF 
 * THE CRBS Workflow Service WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER
 * RIGHTS. 
 */

package edu.ucsd.crbs.cws.dao.objectify;

import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import static edu.ucsd.crbs.cws.dao.objectify.OfyService.ofy;
import edu.ucsd.crbs.cws.workflow.InputWorkspaceFileLink;
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;



/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestInputWorkspaceFileLinkObjectifyDAOImpl {

    
    private final LocalServiceTestHelper _helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(),
        new LocalBlobstoreServiceTestConfig());
    
    public TestInputWorkspaceFileLinkObjectifyDAOImpl() {
    }

    @BeforeClass
    public static void setUpClass() {
         Logger.getLogger(LocalDatastoreService.class.getName()).setLevel(Level.OFF);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        _helper.setUp();
        ofy().clear();
    }

    @After
    public void tearDown() {
        _helper.tearDown();
    }

    @Test
    public void testInsert() throws Exception {
        InputWorkspaceFileLinkObjectifyDAOImpl iwsflDAO = new InputWorkspaceFileLinkObjectifyDAOImpl();
        
        InputWorkspaceFileLink iwsfl = new InputWorkspaceFileLink();
        iwsfl = iwsflDAO.insert(iwsfl);
        assertTrue(iwsfl != null);
        Long theId = iwsfl.getId();
        assertTrue(iwsfl.getId() != null);
        
        iwsfl = iwsflDAO.getById(iwsfl.getId());
        assertTrue(iwsfl != null);
        assertTrue(Objects.equals(iwsfl.getId(), theId));
    }
   
    @Test
    public void testGetByJobIdWhereInputWorkspaceFileLinkIsNotLogicallyDeleted() throws Exception {
        InputWorkspaceFileLinkObjectifyDAOImpl iwsflDAO = new InputWorkspaceFileLinkObjectifyDAOImpl();
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(iwsflDAO);
        
        Job j = new Job();
        j.setName("job");
        j = jobDAO.insert(j, true);
        
        InputWorkspaceFileLink iwsfl = new InputWorkspaceFileLink();
        iwsfl.setJob(j);
        
        iwsfl = iwsflDAO.insert(iwsfl);
        assertTrue(iwsfl != null);
        
        List<InputWorkspaceFileLink> iwsfList = iwsflDAO.getByJobId(j.getId()+1,null);
        assertTrue(iwsfList != null);
        assertTrue(iwsfList.isEmpty());
      
                
        iwsfList = iwsflDAO.getByJobId(j.getId(),null);
        
        assertTrue(iwsfList != null);
        assertTrue(iwsfList.size() == 1);
        assertTrue(Objects.equals(iwsfList.get(0).getId(), iwsfl.getId()));
        
        iwsfList = iwsflDAO.getByJobId(j.getId(),Boolean.FALSE);
        
        assertTrue(iwsfList != null);
        assertTrue(iwsfList.size() == 1);
        assertTrue(Objects.equals(iwsfList.get(0).getId(), iwsfl.getId()));
        
        iwsfList = iwsflDAO.getByJobId(j.getId(),Boolean.TRUE);
        
        assertTrue(iwsfList != null);
        assertTrue(iwsfList.size() == 1);
        assertTrue(Objects.equals(iwsfList.get(0).getId(), iwsfl.getId()));
    }
    
    @Test
    public void testGetByJobIdWhereInputWorkspaceFileLinkIsLogicallyDeleted() throws Exception {
        InputWorkspaceFileLinkObjectifyDAOImpl iwsflDAO = new InputWorkspaceFileLinkObjectifyDAOImpl();
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(iwsflDAO);
        
        Job j = new Job();
        j.setName("job");
        j = jobDAO.insert(j, true);
        
        InputWorkspaceFileLink iwsfl = new InputWorkspaceFileLink();
        iwsfl.setJob(j);
        iwsfl.setDeleted(true);
        iwsfl = iwsflDAO.insert(iwsfl);
        assertTrue(iwsfl != null);
                      
        List<InputWorkspaceFileLink> iwsfList = iwsflDAO.getByJobId(j.getId(),null);
        assertTrue(iwsfList != null);
        assertTrue(iwsfList.size() == 0);
        
        iwsfList = iwsflDAO.getByJobId(j.getId(),Boolean.FALSE);
        assertTrue(iwsfList != null);
        assertTrue(iwsfList.size() == 0);
        
        iwsfList = iwsflDAO.getByJobId(j.getId(),Boolean.TRUE);
        assertTrue(iwsfList != null);
        assertTrue(iwsfList.size() == 1);
        assertTrue(Objects.equals(iwsfList.get(0).getId(), iwsfl.getId()));
    }

    @Test
    public void testGetByWorkspaceFileIdWhereInputWorkspaceFileLinkIsNotLogicallyDeleted() throws Exception {
        InputWorkspaceFileLinkObjectifyDAOImpl iwsflDAO = new InputWorkspaceFileLinkObjectifyDAOImpl();
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(iwsflDAO);
        WorkspaceFileObjectifyDAOImpl workspaceFileDAO = new WorkspaceFileObjectifyDAOImpl(jobDAO,iwsflDAO);

        WorkspaceFile wsf = new WorkspaceFile();
        wsf = workspaceFileDAO.insert(wsf, false);
        InputWorkspaceFileLink iwsfl = new InputWorkspaceFileLink();
        iwsfl.setWorkspaceFile(wsf);
        iwsfl = iwsflDAO.insert(iwsfl);
        assertTrue(iwsfl != null);
        
        List<InputWorkspaceFileLink> iwsfList = iwsflDAO.getByWorkspaceFileId(wsf.getId()+1,null);
        assertTrue(iwsfList != null);
        assertTrue(iwsfList.size() == 0);
        
        iwsfList = iwsflDAO.getByWorkspaceFileId(wsf.getId(),null);
        assertTrue(iwsfList != null);
        assertTrue(iwsfList.size() == 1);
        assertTrue(iwsfList.get(0).getId() == iwsfl.getId());
        
        iwsfList = iwsflDAO.getByWorkspaceFileId(wsf.getId(),Boolean.FALSE);
        assertTrue(iwsfList != null);
        assertTrue(iwsfList.size() == 1);
        assertTrue(iwsfList.get(0).getId() == iwsfl.getId());
        
        iwsfList = iwsflDAO.getByWorkspaceFileId(wsf.getId(),Boolean.TRUE);
        assertTrue(iwsfList != null);
        assertTrue(iwsfList.size() == 1);
        assertTrue(iwsfList.get(0).getId() == iwsfl.getId());
        
        
    }
    
    @Test
    public void testGetByWorkspaceFileIdWhereInputWorkspaceFileLinkIsLogicallyDeleted() throws Exception {
        InputWorkspaceFileLinkObjectifyDAOImpl iwsflDAO = new InputWorkspaceFileLinkObjectifyDAOImpl();
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(iwsflDAO);
        WorkspaceFileObjectifyDAOImpl workspaceFileDAO = new WorkspaceFileObjectifyDAOImpl(jobDAO,iwsflDAO);

        WorkspaceFile wsf = new WorkspaceFile();
       
        wsf = workspaceFileDAO.insert(wsf, false);
        InputWorkspaceFileLink iwsfl = new InputWorkspaceFileLink();
        iwsfl.setWorkspaceFile(wsf);
        iwsfl.setDeleted(true);
        iwsfl = iwsflDAO.insert(iwsfl);
        assertTrue(iwsfl != null);
        
        List<InputWorkspaceFileLink> iwsfList = null;
        iwsfList = iwsflDAO.getByWorkspaceFileId(wsf.getId(),null);
        assertTrue(iwsfList != null);
        assertTrue(iwsfList.size() == 0);
        
        iwsfList = iwsflDAO.getByWorkspaceFileId(wsf.getId(),Boolean.FALSE);
        assertTrue(iwsfList != null);
        assertTrue(iwsfList.size() == 0);
        
        iwsfList = iwsflDAO.getByWorkspaceFileId(wsf.getId(),Boolean.TRUE);
        assertTrue(iwsfList != null);
        assertTrue(iwsfList.size() == 1);
        assertTrue(iwsfList.get(0).getId() == iwsfl.getId());
        
        
    }
    
    
    @Test
    public void testGetByWorkspaceFileIdCountWhereInputWorkspaceFileLinkIsNotLogicallyDeleted() throws Exception {
        InputWorkspaceFileLinkObjectifyDAOImpl iwsflDAO = new InputWorkspaceFileLinkObjectifyDAOImpl();
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(iwsflDAO);
        WorkspaceFileObjectifyDAOImpl workspaceFileDAO = new WorkspaceFileObjectifyDAOImpl(jobDAO,iwsflDAO);

        WorkspaceFile wsf = new WorkspaceFile();
        wsf = workspaceFileDAO.insert(wsf, false);
        InputWorkspaceFileLink iwsfl = new InputWorkspaceFileLink();
        iwsfl.setWorkspaceFile(wsf);
        iwsfl = iwsflDAO.insert(iwsfl);
        assertTrue(iwsfl != null);
        
        assertTrue(iwsflDAO.getByWorkspaceFileIdCount(wsf.getId()+1,null) == 0);
        
        assertTrue(iwsflDAO.getByWorkspaceFileIdCount(wsf.getId(),null) == 1);
        
        assertTrue(iwsflDAO.getByWorkspaceFileIdCount(wsf.getId(),Boolean.FALSE) == 1);
        
        assertTrue(iwsflDAO.getByWorkspaceFileIdCount(wsf.getId(),Boolean.TRUE) == 1);
    }
    
    @Test
    public void testGetByWorkspaceFileIdCountWhereInputWorkspaceFileLinkIsLogicallyDeleted() throws Exception {
        InputWorkspaceFileLinkObjectifyDAOImpl iwsflDAO = new InputWorkspaceFileLinkObjectifyDAOImpl();
        JobObjectifyDAOImpl jobDAO = new JobObjectifyDAOImpl(iwsflDAO);
        WorkspaceFileObjectifyDAOImpl workspaceFileDAO = new WorkspaceFileObjectifyDAOImpl(jobDAO,iwsflDAO);

        WorkspaceFile wsf = new WorkspaceFile();
       
        wsf = workspaceFileDAO.insert(wsf, false);
        InputWorkspaceFileLink iwsfl = new InputWorkspaceFileLink();
        iwsfl.setWorkspaceFile(wsf);
        iwsfl.setDeleted(true);
        iwsfl = iwsflDAO.insert(iwsfl);
        assertTrue(iwsfl != null);
        
        assertTrue(iwsflDAO.getByWorkspaceFileIdCount(wsf.getId(),null) == 0);
        assertTrue(iwsflDAO.getByWorkspaceFileIdCount(wsf.getId(),Boolean.FALSE) == 0);
        assertTrue(iwsflDAO.getByWorkspaceFileIdCount(wsf.getId(),Boolean.TRUE) == 1);
    }
    
    
}