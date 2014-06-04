package edu.ucsd.crbs.cws.dao.objectify;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;
import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.dao.UserDAO;
import static edu.ucsd.crbs.cws.dao.objectify.OfyService.ofy;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class UserObjectifyDAOImpl implements UserDAO {

    @Override
    public User insert(User u) throws Exception {
        if (u == null){
            throw new NullPointerException("User is null");
        }
        
        if (u.getCreateDate() == null){
            u.setCreateDate(new Date());
        }
        Key<User> uKey = ofy().save().entity(u).now();
        return u;
    }

    
    @Override
    public User getUserByLoginAndToken(String login, String token) throws Exception {
        if (login == null && token == null){
            return null;
        }
        Query<User> q = ofy().load().type(User.class);
        
        if (login != null){
            q = q.filter("_login ==",login);
        }
        if (token != null){
            q = q.filter("_token ==",token);
        }
        List<User> user = q.limit(1).list();
        if (user != null && user.isEmpty() == false){
            return user.get(0);
        }
        return null;
    }

}
