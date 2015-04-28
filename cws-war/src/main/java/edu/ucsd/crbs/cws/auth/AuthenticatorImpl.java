package edu.ucsd.crbs.cws.auth;

import edu.ucsd.crbs.cws.dao.UserDAO;
import edu.ucsd.crbs.cws.dao.objectify.UserObjectifyDAOImpl;
import edu.ucsd.crbs.cws.rest.Constants;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

/**
 * Default authentication class.  See {@link authenticate} for 
 * authentication rules
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class AuthenticatorImpl implements Authenticator {

    
    private static final Logger _log
            = Logger.getLogger(AuthenticatorImpl.class.getName());
    
    public static final String AUTHORIZATION_HEADER = "authorization";
    
    UserDAO _userDAO = new UserObjectifyDAOImpl();
    UserIpAddressValidator _ipAddressValidator;
    
    private User createInvalidUser(HttpServletRequest request){
        User invalidUser = new User();
        invalidUser.setIpAddress(request.getRemoteAddr());
        invalidUser.setPermissions(Permission.NONE);
        return invalidUser;
    }
    
    /**
     * 
     * @param userLogin
     * @param userToken
     * @param loginToRunAs
     * @throws java.lang.Exception
     * @TODO need to fix this to set roles by requestor ip
     * @param request
     * @return <b>SUPER</b> role Authentication
     */
    private User authenticate(HttpServletRequest request,final String userLogin,
            final String userToken,final String loginToRunAs) throws Exception {

        _log.log(Level.INFO,"Authenticating user: {0}",userLogin);
        //call get special users to see if this request is a special one.
        // if it is we bypass the query and directly create the object
        
        User user = getUserIfTheyAreSpecial(request.getRemoteAddr(),
                userLogin,userToken,loginToRunAs);
        if (user != null){
            _log.log(Level.INFO, "Found a special User: {0}", user.getLogin());
            return user;
        }
        
        //query data store for user with username and token and return it
        user = _userDAO.getUserByLoginAndToken(userLogin,userToken);
        if (user != null){
            user.setIpAddress(request.getRemoteAddr());
            
            
            if (loginToRunAs != null){
                if (user.isAuthorizedTo(Permission.RUN_AS_ANOTHER_USER) == true){
                    user.setLoginToRunJobAs(loginToRunAs);
                }
                else {
                    throw new Exception("User does not have permission to run as another user");
                }
            }
            
            if (_ipAddressValidator.isUserRequestFromValidIpAddress(user) == false){
                return createInvalidUser(request);
            }
            
            return user;
        }
        
        //fail cause we couldn't find anybody so make a user with no permission
        return createInvalidUser(request);
    }
        
    /**
     * Authenticates request by first looking for HTTP Basic Authentication 
     * credentials to get a user login and token.  If not set, then 
     * {@link Constants#USER_LOGIN_PARAM} and {@link Constants#USER_TOKEN_PARAM} 
     * query parameters are used.<p/>
     * These values are then compared against the {@link User} database and if a
     * match is found and the request is coming from a valid ip address for that
     * {@link User} then the {@link User} object is returned.
     * @param request
     * @return {@link User} matching authentication upon success or {@link User}
     * with null login and token and permissions set to {@link Permission#NONE}
     * if authentication failed.
     * @throws Exception 
     */
    @Override
    public User authenticate(HttpServletRequest request) throws Exception {
        if (request == null) {
            throw new Exception("Request is null");
        }

        String auth = request.getHeader(AUTHORIZATION_HEADER);
        if (auth == null) {
            _log.log(Level.INFO,"No authentication information in header. "
                    +" Will attempt to look at query parameters");
            return authenticate(request,
                    request.getParameter(Constants.USER_LOGIN_PARAM),
                    request.getParameter(Constants.USER_TOKEN_PARAM),
                    request.getParameter(Constants.USER_LOGIN_TO_RUN_AS_PARAM));
        }
        String[] userPass = this.decodeAuthString(auth);
        if (userPass == null || userPass.length != 2) {
            return createInvalidUser(request);
        }
        return authenticate(request, userPass[0],
                userPass[1],
                request.getParameter(Constants.USER_LOGIN_TO_RUN_AS_PARAM));
    }

    
    private User getUserIfTheyAreSpecial(final String ipAddress,
                                         final String userLogin,
                                         final String userToken,
                                         final String loginToRunAs){
        if (ipAddress == null){
            _log.log(Level.INFO,"Unable to get ip address");
            return null;
        }

        if (userToken == null || userLogin == null){
            _log.log(Level.INFO,"User token or login is null");
            return null;
        }
        
        // @TODO REMOVE THIS AT SOME POINT and replace with Google's user 
        // services
        // so that only users that logged into google can have this crazy 
        // privilege
        // https://developers.google.com/appengine/docs/java/users/
        if ((userLogin.equals("mikechiu") && 
                userToken.equals("67cecab615914b2494830ef116a4580a")) ||
             (userLogin.equals("chris") && 
                userToken.equals("dc5902078cfa40b980229662c2e0c226"))){
            User user = new User();
            user.setLogin(userLogin);
            user.setToken(userToken);
            user.setIpAddress(ipAddress);
            user.setPermissions(Permission.ALL);
            user.setLoginToRunJobAs(loginToRunAs);
            return user;
        }
        return null;
    }

    /**
     * Takes Basic HTTP Authentication string via <b>auth</b> parameter in format:<p/>
     * 
     *  Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ== <p/>
     * 
     * and extracts login and password token from value to right of <b>Basic</b> 
     * above
     * which should be in format of <b>login:pass</b> once decoded via 
     * {@link DatatypeConverter#parseBase64Binary(java.lang.String)}
     * method
     * 
     * @param auth Basic Http Authentication string in format above
     * @return String array of length two with first element being login value 
     *         and second being password upon success or <b>null</b> upon error
     */
    private String[] decodeAuthString(final String auth){
        
        if (auth == null){
            return null;
        }
        String authWithBasicRemoved = auth.replaceFirst("[B|b]asic ", "");
        byte[] decodedBytes = DatatypeConverter.parseBase64Binary(authWithBasicRemoved);
        if (decodedBytes == null || decodedBytes.length == 0){
            return null;
        }
        String decodedUserPass = new String(decodedBytes);
        if (!decodedUserPass.contains(":")){
            return null;
        }
        return decodedUserPass.split(":");
    }
}
