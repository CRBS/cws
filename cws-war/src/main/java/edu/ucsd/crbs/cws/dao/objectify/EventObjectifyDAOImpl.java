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

import edu.ucsd.crbs.cws.dao.EventDAO;
import static edu.ucsd.crbs.cws.dao.objectify.OfyService.ofy;
import edu.ucsd.crbs.cws.log.Event;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements EventDAO interface which proves a means to load and save
 * {@link Event} objects to Google NoSQL data store via Objectify
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class EventObjectifyDAOImpl implements EventDAO {

    private static final Logger _log
            = Logger.getLogger(EventObjectifyDAOImpl.class.getName());
    /**
     * Inserts an Event object to the data store
     * @param event Event to store
     * @return Event objet with getId() set with value from data store
     * @throws Exception If there was a problem saving the event
     * @throws NullPointerException if event passed in is null
     */
    @Override
    public Event insert(Event event) throws Exception {
        
        if (event == null){
            throw new NullPointerException("Event is null");
        }
        
        if (event.getDate() == null){
            event.setDate(new Date());
        }
        ofy().save().entity(event).now();
        return event;
    }

    @Override
    public Event neverComplainInsert(Event event) {
        Event res = null;
        try {
            res = insert(event);
        }
        catch(Exception ex){
            _log.log(Level.WARNING, "Unable to save Event", ex);
        }
        return res;
    }

    
}
