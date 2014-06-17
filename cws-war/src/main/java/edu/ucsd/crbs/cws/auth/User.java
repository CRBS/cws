package edu.ucsd.crbs.cws.auth;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
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
    private int _permissions;
    @Ignore private String _loginToRunTaskAs;
    
    
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
    
    public String getLoginToRunTaskAs(){
        return _loginToRunTaskAs;
    }
    
    public void setLoginToRunTaskAs(final String login){
        _loginToRunTaskAs = login;
    }
    
    public boolean isAuthorizedTo(int requestedPermission){
        return ((_permissions & requestedPermission) == requestedPermission);
    }
}
