/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2014 The Regents of the University of California All Rights Reserved
 * 
 * Permission to copy, modify and distribute any part of this CRBS Workflow 
 * Service for educational, research and non-profit purposes, without fee, and
 * without a written agreement is hereby granted, provided that the above 
 * copyright notice, this paragraph and the following three paragraphs appear
 * in all copies.
 * 
 * Those desiring to incorporate this CRBS Workflow Service into commercial 
 * products or use for commercial purposes should contact the Technology
 * Transfer Office, University of California, San Diego, 9500 Gilman Drive, 
 * Mail Code 0910, La Jolla, CA 92093-0910, Ph: (858) 534-5815, 
 * FAX: (858) 534-7345, E-MAIL:invent@ucsd.edu.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR 
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING 
 * LOST PROFITS, ARISING OUT OF THE USE OF THIS CRBS Workflow Service, EVEN IF 
 * THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 * 
 * THE CRBS Workflow Service PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE
 * UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, 
 * UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES
 * NO REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR 
 * EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF 
 * THE CRBS Workflow Service WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER
 * RIGHTS. 
 */

package edu.ucsd.crbs.cws.gae;

import edu.ucsd.crbs.cws.auth.User;
import edu.ucsd.crbs.cws.workflow.WorkflowParameter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Uses Google App Engine URL Fetch service to update value map of WorkflowParameter
 * objects that are of type <b>dropdown</b> 
 * 
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class URLFetcherImpl implements WorkflowParameterDataFetcher {

    /**
     * HTTP prefix
     */
    public static final String HTTP_PREFIX = "http://";
    
    /**
     * HTTPS prefix
     */
    public static final String HTTPS_PREFIX = "https://";
    
    /**
     * FILE prefix
     */
    public static final String FILE_PREFIX = "file://";
            
    /**
     * URL time out in milliseconds
     */
    private static final int URL_CONNECT_TIMEOUT = 5000;
    
    /**
     * URL read timeout in milliseconds
     */
    private static final int URL_READ_TIMEOUT = 5000;
    
    /**
     * Token to be replaced with user login
     */
    public static final String USER_LOGIN_KEYWORD = "@@userlogin@@";
    
    /**
     * Token to be replaced with user token 
     */
    public static final String USER_TOKEN_KEYWORD = "@@usertoken@@";
    
    /**
     * Token to be replaced with user
     */
    public static final String USER_KEYWORD = "@@user@@";
    
    
    private static final Logger _log
            = Logger.getLogger(URLFetcherImpl.class.getName());
    
    /**
     * Updates value map in <b>parameter</b> object by fetching URL in value
     * field for <b>parameter</b> of type dropdown
     * <p/>
     * Before fetching the URL any occurrences of the following tokens will
     * be replaced with values described:
     * 
     * {@link #USER_LOGIN_KEYWORD} -- Any instances of this in URL will be replaced with the {@link User#getLogin()}
     * <p/>
     * {@link #USER_TOKEN_KEYWORD} -- Any instances of this in URL will be replaced with the {@link User#getToken()}
     * <p/>
     * {@link #USER_KEYWORD} -- Any instances of this in URL will be replaced with the {@link User#getLoginToRunJobAs()}
     *                          or if that value is null {@link User#getLogin()} will be used instead
     * <p/>
     * 
     * Another thing to note if the path in <b>value</b> of WorkflowParameter starts with
     * http:// or https:// it will be retreived otherwise the content of <b>value</b> will be
     * parsed using the delimiter set in the WorkflowParameter and each name,value will be
     * delineated by a new line character or (amperstand pound 10 semicolon)
     * 
     * @param parameter
     * @throws Exception 
     */
    @Override
    public void fetchAndUpdate(WorkflowParameter parameter,
            User user) throws Exception {
        if (parameter == null){
            _log.info("Workflow Parameter is null");
            return;
        }
        if (parameter.getType() == null){
            throw new Exception("Parameter's type is null");
        }
        
        //if parameter is not a drop down just return
        if (!parameter.getType().equalsIgnoreCase(WorkflowParameter.Type.DROP_DOWN)){
            return;
        }
        
        if (user == null){
            _log.info("User is null");
            return;
        }
        
        if (parameter.getValue() == null){
            throw new Exception("Parameter's value is null");
        }
        
        
        BufferedReader br = null;
        try {
            if (parameter.getValue().startsWith(HTTP_PREFIX) || 
                parameter.getValue().startsWith(HTTPS_PREFIX) ||
                parameter.getValue().startsWith(FILE_PREFIX)){
                _log.log(Level.INFO, 
                        "Found drop parameter with http URL fetching: {0}", 
                        parameter.getValue());
                br = getBufferedReaderForURL(parameter,user);
            }
            else {
                br =  getBufferedReaderForValue(parameter);
            }
            updateValueMap(br,parameter);
        }
        finally {
            if (br != null){
                br.close();
            }
        }
    }
    
    /**
     * Takes <b>br</b> and updates the <b>parameter</b> {@link WorkflowParameter}
     * value map with data extracted from <b>br</b>
     * <p/>
     * For each line of <b>br</b> the code applies the {@link WorkflowParameter#getNameValueDelimiter()}
     * delimiter to split the line into a key/value.  This is put into the valuemap
     * if key doesn't already exist.  If the split results into more then 2 elements
     * only element 0 (key) and 1 (value) are used.  If the split results in less
     * then 2 elements the key and value are set to the line of data.
     * @param br Opened Buffered Reader containing data to push into value map
     * @param parameter WorkflowParameter to update
     * @throws Exception 
     */
    private void updateValueMap(BufferedReader br, 
            WorkflowParameter parameter) throws Exception {
        
        String curLine = br.readLine();
        
        LinkedHashMap<String,String> dropMap = new LinkedHashMap<>();
        while(curLine != null){
            if (parameter.getNameValueDelimiter() != null){
                String [] splitLine = curLine.split(parameter.getNameValueDelimiter());
                if (splitLine.length >= 2){
                    if (!dropMap.containsKey(splitLine[0])){
                        dropMap.put(splitLine[0],splitLine[1]);
                    }
                }
                else {
                    if (!dropMap.containsKey(curLine)){
                        dropMap.put(curLine, curLine);
                    }
                }
            }
            else {
                if (!dropMap.containsKey(curLine)){
                    dropMap.put(curLine, curLine);
                }
            }
            curLine = br.readLine();
        }
        parameter.setValueMap(dropMap);
        
    }
    
    /**
     * Examines url in <b>parameter</b> {@link WorkflowParameter#getValue()} and replaces
     * any occurrences of {@link URLFetcherImpl#USER_KEYWORD},{@link URLFetcherImpl#USER_LOGIN_KEYWORD},
     * {@link URLFetcherImpl#USER_TOKEN_KEYWORD} with values from <b>user</b> object. The
     * code then attempts to open the url returning a bufferedreader to the output
     * stream
     * @param parameter
     * @param user
     * @return open Stream to content of url
     * @throws Exception 
     */
    private BufferedReader getBufferedReaderForURL(WorkflowParameter parameter,User user) throws Exception {
        
        //need to find any occurrences of special variables and replace them with
        //appropriate values
        
        String loginToRunAs = user.getLoginToRunJobAs();
        
        String updatedURL;
        
        if (user.getLogin() != null){
            //special case if loginto run as is null just use login if its not null
            if (loginToRunAs == null){
                loginToRunAs = user.getLogin();
            }
            updatedURL = parameter.getValue().replace(URLFetcherImpl.USER_LOGIN_KEYWORD, user.getLogin());
        }
        else {
            updatedURL = parameter.getValue();
        }
        
        if (user.getToken() != null){
            updatedURL = updatedURL.replace(URLFetcherImpl.USER_TOKEN_KEYWORD, user.getToken());
        }
        if (loginToRunAs != null){
            updatedURL = updatedURL.replace(URLFetcherImpl.USER_KEYWORD, loginToRunAs);
        }
        
        URL u = new URL(updatedURL);
        URLConnection ucon = u.openConnection();
        ucon.setConnectTimeout(URL_CONNECT_TIMEOUT);
        ucon.setReadTimeout(URL_READ_TIMEOUT);
        return new BufferedReader(new InputStreamReader(ucon.getInputStream()));
    }
    
    /**
     * Applies {@link WorkflowParameter#getLineDelimiter()} in <b>parameter</b> to split content of
     * {@link WorkflowParameter#getValue()} into multiple lines.  If no delimiter
     * is found no splitting into multiple lines is done.  THe code then returns
     * a BufferedReader object on the contents of <b>parameter</b> {@link WorkflowParameter#getValue()}
     * @param parameter WorkflowParameter to examine
     * @return BufferedReader open on content of <b>parameter</b> getValue() data
     * @throws Exception 
     */
    private BufferedReader getBufferedReaderForValue(WorkflowParameter parameter) throws Exception {
        
        if (parameter.getLineDelimiter() == null){
            return new BufferedReader(new StringReader(parameter.getValue()));
        }
        String lineDelimReplacedStr = parameter.getValue().replace(parameter.getLineDelimiter(),"\n");
        return new BufferedReader(new StringReader(lineDelimReplacedStr));
    }
}
