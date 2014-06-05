package edu.ucsd.crbs.cws.dao.objectify;

import edu.ucsd.crbs.cws.dao.EventDAO;
import static edu.ucsd.crbs.cws.dao.objectify.OfyService.ofy;
import edu.ucsd.crbs.cws.log.Event;
import java.util.Date;

/**
 * Implements EventDAO interface which proves a means to load and save
 * {@link Event} objects to Google NoSQL data store via Objectify
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class EventObjectifyDAOImpl implements EventDAO {

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

}
