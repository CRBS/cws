package edu.ucsd.crbs.cws.dao.objectify;

import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Objectify;
import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.log.Event;
import edu.ucsd.crbs.cws.workflow.Task;
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
        factory().register(Task.class);
        factory().register(User.class);
        factory().register(Event.class);
        factory().register(WorkspaceFile.class);
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
