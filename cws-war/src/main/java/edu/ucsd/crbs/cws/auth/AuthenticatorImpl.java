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

    
    UserDAO _userDAO = new UserObjectifyDAOImpl();
    
    /**
     * Currently this method considers all requests authenticated with
     * <b>SUPER</b> {@link Role}.
     * @param userLogin
     * @param userToken
     * @param loginToRunAs
     * @throws java.lang.Exception
     * @TODO need to fix this to set roles by requestor ip
     * @param request
     * @return <b>SUPER</b> role Authentication
     */
    @Override
    public User authenticate(HttpServletRequest request,final String userLogin,
            final String userToken,final String loginToRunAs) throws Exception {

        //call get special users to see if this request is a special one.
        // if it is we bypass the query and directly create the object
        String ipAddress = request.getRemoteAddr();
        User user = getUserIfTheyAreSpecial(ipAddress,userLogin,userToken,
                loginToRunAs);
        if (user != null){
            return user;
        }
        
        //query data store for user with username and token and return it
        user = _userDAO.getUserByLoginAndToken(userLogin,userToken);
        if (user != null){
            user.setIpAddress(ipAddress);
            if (user.isAuthorizedTo(Permission.RUN_AS_ANOTHER_USER) == true){
                user.setLoginToRunTaskAs(loginToRunAs);
            }
            return user;
        }
        
        //fail cause we couldn't find anybody so make a user with no permission
        User invalidUser = new User();
        invalidUser.setIpAddress(ipAddress);
        invalidUser.setPermissions(Permission.NONE);
        return invalidUser;
    }
    
    @Override
    public User authenticate(HttpServletRequest request) throws Exception {
        if (request == null){
            throw new Exception("Request is null");
        }
        return authenticate(request,request.getParameter(Constants.USER_LOGIN_PARAM),
                request.getParameter(Constants.USER_TOKEN_PARAM),
                request.getParameter(Constants.USER_LOGIN_TO_RUN_AS_PARAM));
    }

    
    private User getUserIfTheyAreSpecial(final String ipAddress,
                                         final String userLogin,
                                         final String userToken,
                                         final String loginToRunAs){
        if (ipAddress == null){
            return null;
        }

        if (userToken == null || userLogin == null){
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
            user.setPermissions(Permission.ALL);
            user.setLoginToRunTaskAs(loginToRunAs);
            return user;
        }
        return null;
    }
}
