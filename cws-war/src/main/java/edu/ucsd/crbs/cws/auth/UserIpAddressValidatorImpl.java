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

package edu.ucsd.crbs.cws.auth;

import com.google.appengine.repackaged.com.google.common.net.InetAddresses;
import com.googlecode.ipv6.IPv6Address;
import com.googlecode.ipv6.IPv6Network;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.util.SubnetUtils;

/**
 * Checks if User request is originating from valid ip address
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class UserIpAddressValidatorImpl implements UserIpAddressValidator {

    private static final Logger _log
            = Logger.getLogger(UserIpAddressValidatorImpl.class.getName());
    
    /**
     * Checks if {@link User} request is originating from valid ip address by
     * comparing the {@link User#getIpAddress()} against the valid ip addresses
     * in {@link User#getAllowedIpAddresses()} list.  This list can contain
     * ipv4 and ipv6 addresses with or without CIDR notation.
     * @param user {@link User} object with {@link User#getIpAddress()} set
     * @return true if {@link User#getAllowedIpAddresses()} is null or empty or
     * or if {@link User#getIpAddress()} is in {@link User#getAllowedIpAddresses()}
     * list.  
     * @throws Exception if <b>user</b> is null or if no ip address is set for <b>user</b>
     * or if there is an error parsing the ip addresses.
     *  
     */
    @Override
    public boolean isUserRequestFromValidIpAddress(User user) throws Exception {

        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        if (user.getIpAddress() == null || user.getIpAddress().trim().isEmpty() == true) {
            throw new Exception("No ip address found");
        }

        //simple case the list is empty or null so return true
        if (user.getAllowedIpAddresses() == null || user.getAllowedIpAddresses().isEmpty()) {
            return true;
        }
        InetAddress requestAddress = InetAddresses.forString(user.getIpAddress());

        boolean requestAddressIsIpv6 = false;
        if (requestAddress instanceof Inet6Address){
            requestAddressIsIpv6 = true;
        }
        
        for (String validIp : user.getAllowedIpAddresses()) {

            if (doesAddressContainCidr(validIp) == true) {
                
                if (isIpv6Address(validIp)){
                    //if valid ip is an ipv6 address then dont bother checking the cidr
                    //cause the request is ipv4
                    if (requestAddressIsIpv6 == false){
                        continue;
                    }
                    if (isIpv6AddressInCidrAddress(requestAddress,validIp)){
                        return true;
                    }
                }else {
                    //if valid ip is an ipv4 address then dont bother checking the cidr
                    //cause the request is ipv6
                    if (requestAddressIsIpv6 == true){
                        continue;
                    }
                    if (isIpv4AddressInCidrAddress(requestAddress,validIp)){
                        return true;
                    }
                }
                continue;
            } 
            
            InetAddress validAddress = InetAddresses.forString(validIp);
            if (validAddress.equals(requestAddress)) {
                return true;
            }

        }

        return false;
    }

    /**
     * Compares <b>requestAddress</b> against ipv4 CIDR in <b>cidrAddress</b>
     * @param requestAddress requestAddress ipv4 address of the request
     * @param cidrAddress ipv4 CIDR address
     * @return true if the <b>requestAddress</b> is within the range of the 
     * <b>cidrAddress</b>, false otherwise
     */
    private boolean isIpv4AddressInCidrAddress(InetAddress requestAddress,final String cidrAddress) {
        try {
            SubnetUtils snUtils = new SubnetUtils(cidrAddress);
            return snUtils.getInfo().isInRange(requestAddress.getHostAddress());
        }
        catch(Exception ex){
            _log.log(Level.WARNING,"Problems parsing cidr address: {0} and comparing to {1} : {2}",
                    new Object[]{cidrAddress,requestAddress.getHostAddress(),ex.getMessage()});
        }
        return false;
    }
    
    /**
     * Compares <b>requestAddress</b> against ipv6 cidr in <b>cidrAddress</b>
     * @param requestAddress ipv6 address of the request
     * @param cidrAddress ipv6 CIDR address
     * @return true if the <b>requestAddress</b> is within the range of the 
     * <b>cidrAddress</b>, false otherwise
     */
    private boolean isIpv6AddressInCidrAddress(InetAddress requestAddress,final String cidrAddress) {
        try {
            IPv6Network network = IPv6Network.fromString(cidrAddress);
            return network.contains(IPv6Address.fromInetAddress(requestAddress));
        }
        catch(Exception ex){
            _log.log(Level.WARNING,"Problems parsing cidr address: {0} and comparing to {1} : {2}",
                    new Object[]{cidrAddress,requestAddress.getHostAddress(),ex.getMessage()});
        }
        return false;
    }
    
    /**
     * Super simple CIDR check.
     * @param address ip address to check
     * @return true if <b>address</b> contains a forward slash otherwise false.
     */
    private boolean doesAddressContainCidr(final String address) {
        return address.contains("/");
    }
    
    /**
     * Super simple ipv6 check.  
     * @param address ip address to check
     * @return true if <b>address</b> contains a colon otherwise false
     */
    private boolean isIpv6Address(final String address){
        return address.contains(":");
    }
}
