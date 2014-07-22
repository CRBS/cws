package edu.ucsd.crbs.cws.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import edu.ucsd.crbs.cws.rest.Constants;
import java.util.Date;


/**
 * Represents Authentication for a given request
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@Entity
public class User {

    @Id private Long _id;    
    @Index private String _login;
    @Index private String _ipAddress;
    @Index private String _token;
    private Date _createDate;
    @Index private int _permissions;
    @Ignore private String _loginToRunJobAs;
    @Index private boolean _deleted;
    
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
    
    public String getIpAddress(){
        return _ipAddress;
    }
    
    public void setIpAddress(final String ipAddress){
        _ipAddress = ipAddress;
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
