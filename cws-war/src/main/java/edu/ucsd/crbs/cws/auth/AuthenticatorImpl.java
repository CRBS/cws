package edu.ucsd.crbs.cws.auth;

import edu.ucsd.crbs.cws.dao.UserDAO;
import edu.ucsd.crbs.cws.dao.objectify.UserObjectifyDAOImpl;
import edu.ucsd.crbs.cws.rest.Constants;
import javax.management.relation.Role;
import javax.servlet.http.HttpServletRequest;

/**
 * Default authentication class.  See {@link authenticate} for 
 * authentication rules
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class AuthenticatorImpl implements Authenticator {

    
    private UserDAO _userDAO = new UserObjectifyDAOImpl();
    
    /**
     * Currently this method considers all requests authenticated with
     * <b>SUPER</b> {@link Role}.
     * @TODO need to fix this to set roles by requestor ip
     * @param request
     * @return <b>SUPER</b> role Authentication
     */
    @Override
    public User authenticate(HttpServletRequest request,final String userLogin,
            final String userToken) throws Exception {

        //call get special users to see if this request is a special one.
        // if it is we bypass the query and directly create the object
        String ipAddress = request.getRemoteAddr();
        User user = getUserIfTheyAreSpecial(ipAddress,userLogin,userToken);
        if (user != null){
            return user;
        }
        
        //query data store for user with username and token and return it
        user = _userDAO.getUserByLoginAndToken(userLogin,userToken);
        if (user != null){
            return user;
        }
        
        //fail cause we couldn't find anybody so make a user with no permission
        User invalidUser = new User();
        invalidUser.setPermissions(Permission.NONE);
        return invalidUser;
    }
    
    @Override
    public User authenticate(HttpServletRequest request) throws Exception {
        if (request == null){
            throw new Exception("Request is null");
        }
        return authenticate(request,request.getParameter(Constants.USER_LOGIN_PARAM),
                request.getParameter(Constants.USER_TOKEN_PARAM));
    }

    
    private User getUserIfTheyAreSpecial(final String ipAddress,
                                         final String userLogin,
                                         final String userToken){
        if (ipAddress == null){
            return null;
        }

  /*        
        if (!ipAddress.equals("137.110.119.94") && //coleslaw
            !ipAddress.equals("137.110.113.91") && //cylume
            !ipAddress.equals("137.110.113.108")){ //megashark
            
           return null;
        }
    */    
        if (userToken == null && userLogin == null){
            return null;
        }
        
        // @TODO REMOVE THIS AT SOME POINT and replace with Google's user services
        // so that only users that logged into google can have this crazy privilege
        // https://developers.google.com/appengine/docs/java/users/
        if ((userLogin.equals("mikechiu") && userToken.equals("67cecab615914b2494830ef116a4580a")) ||
             (userLogin.equals("chris") && userToken.equals("dc5902078cfa40b980229662c2e0c226"))){
            User user = new User();
            user.setLogin(userLogin);
            user.setToken(userToken);
            user.setIpAddress(ipAddress);
            user.setPermissions(Integer.MAX_VALUE);
            return user;
        }
        return null;
    }
}
