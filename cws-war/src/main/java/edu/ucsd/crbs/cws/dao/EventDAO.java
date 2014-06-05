package edu.ucsd.crbs.cws.dao;

import edu.ucsd.crbs.cws.log.Event;

/**
 * Defines methods to retrieve and persist Events to a persistent store
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public interface EventDAO {
    
    /**
     * Inserts an event to a persistent store
     * @param event Event to store
     * @return Same Event object with id set
     * @throws Exception If there was a problem saving
     */
    public Event insert(Event event) throws Exception;
    
}
