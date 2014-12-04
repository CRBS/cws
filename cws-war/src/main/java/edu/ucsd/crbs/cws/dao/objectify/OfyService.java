/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2014 The Regents of the University of California All Rights Reserved
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

import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Objectify;
import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.log.Event;
import edu.ucsd.crbs.cws.workflow.Job;
import edu.ucsd.crbs.cws.workflow.InputWorkspaceFileLink;
import edu.ucsd.crbs.cws.workflow.Workflow;
import edu.ucsd.crbs.cws.workflow.WorkspaceFile;


/**
 * Contains static methods to retrieve Objectify object that lets caller interact
 * with Google App Engine NoSQL data store.  Code was taken from Best Practices 
 * section in <a href="http://code.google.com/p/objectify-appengine/wiki">Objectify Wiki</a>
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class OfyService {
    
    /**
     * Put list of classes that need to be persisted by Objectify in here
     */
    static {
        factory().register(Workflow.class);
        factory().register(Job.class);
        factory().register(User.class);
        factory().register(Event.class);
        factory().register(WorkspaceFile.class);
        factory().register(InputWorkspaceFileLink.class);
    }

    /**
     * Gets Objectify object which is the main object used to interact with Google
     * App Engine NoSQL Datastore.  This is the method to call for loading, querying,
     * and saving objects to the data store.
     * @return Objectify object
     */
    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }


    /**
     * Retrieves the ObjectifyFactory object from the Service
     * @return 
     */
    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }


}
