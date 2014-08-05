package edu.ucsd.crbs.cws.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import edu.ucsd.crbs.cws.rest.Constants;
import java.util.Date;
import java.util.List;


/**
 * Represents Authentication for a given request
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@Entity
public class User {

    @Id private Long _id;    
    @Index private String _login;
    @Ignore private String _ipAddress;
    @Index private String _token;
    private Date _createDate;
    @Index private int _permissions;
    @Ignore private String _loginToRunJobAs;
    @Index private boolean _deleted;
    private List<String> _allowedIpAddresses;
    
    public User(){
        
    }
    
    public User(final String login,
            int permissions){
        _login = login;
        _permissions = permissions;
    }
    
    public Long getId(){
        return _id;
    }
    public void setId(Long id){
       _id = id;
    }
    
    /**
     * Gets ip address of request.  
     * @return 
     */
    public String getIpAddress(){
        return _ipAddress;
    }

    /**
     * Sets ip address of request.
     * @param ipAddress 
     */
    public void setIpAddress(final String ipAddress){
        _ipAddress = ipAddress;
    }

    /**
     * Gets list of ip addresses that requests from this user are allowed
     * to originate from.  The addresses can be ipv4 or ipv6 and be in CIDR
     * notation.
     * @return 
     */
    public List<String> getAllowedIpAddresses() {
        return _allowedIpAddresses;
    }

    /**
     * Sets list of ip addresses that requests from this user are allowed
     * to originate from.  The addresses can be ipv4 or ipv6 and be in CIDR
     * notation.
     * @param allowedIpAddresses 
     */
    public void setAllowedIpAddresses(List<String> allowedIpAddresses) {
        _allowedIpAddresses = allowedIpAddresses;
    }
    
    public String getToken(){
        return _token;
    }
    
    public void setToken(final String token){
        _token = token;
    }
    
    public Date getCreateDate(){
        return _createDate;
    }
    
    public void setCreateDate(final Date date){
        _createDate = date;
    }
    
    public int getPermissions(){
        return _permissions;
    }
    
    public void setPermissions(int perms){
        _permissions = perms;
    }
    
    public String getLogin(){
        return _login;
    }
    
    public void setLogin(final String login){
        _login = login;
    }
    
    public String getLoginToRunJobAs(){
        if (_loginToRunJobAs == null){
            return _login;
        }
        return _loginToRunJobAs;
    }
    
    public void setLoginToRunJobAs(final String login){
        _loginToRunJobAs = login;
    }
    
    public boolean isDeleted() {
        return _deleted;
    }

    public void setDeleted(boolean deleted) {
        _deleted = deleted;
    }

    @JsonIgnore
    public boolean isAuthorizedTo(int requestedPermission){
        return ((_permissions & requestedPermission) == requestedPermission);
    }
    
    public String getAsQueryParameters(){
        if (this._login == null || this._token == null){
            return null;
        }
        StringBuilder url = new StringBuilder();
        url.append(Constants.USER_LOGIN_PARAM).append("=").append(_login);
        url.append("&").append(Constants.USER_TOKEN_PARAM).append("=").append(_token);
        return url.toString();
    }
}
