/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2015 The Regents of the University of California All Rights Reserved
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

package edu.ucsd.crbs.cws.log;

import com.google.appengine.api.capabilities.CapabilitiesService;
import com.google.appengine.api.capabilities.Capability;
import com.google.appengine.api.capabilities.CapabilityState;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.Environment;

/**
 * Contains status of Google App Engine services
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class ServiceStatus {
    
    private CapabilityState _blobStoreStatus;
    private CapabilityState _dataStoreReadStatus;
    private CapabilityState _dataStoreWriteStatus;
    private CapabilityState _memCacheStatus;
    private CapabilityState _urlFetchStatus;
    
    private String _applicationId;
    private String _applicationVersionId;
    

    public ServiceStatus(CapabilitiesService service){
        updateStatus(service);
        Environment env = ApiProxy.getCurrentEnvironment();
        _applicationId = env.getAppId();
        _applicationVersionId = env.getVersionId();
    }
    
    /**
     * Updates internal variables with status of Google App Engine services
     * @param service used to obtain status information
     */
    private void updateStatus(CapabilitiesService service){
        _blobStoreStatus = service.getStatus(Capability.BLOBSTORE);
        _dataStoreReadStatus = service.getStatus(Capability.DATASTORE);
        _dataStoreWriteStatus = service.getStatus(Capability.DATASTORE_WRITE);
        _memCacheStatus = service.getStatus(Capability.MEMCACHE);
        _urlFetchStatus = service.getStatus(Capability.URL_FETCH);
    }
    
    public CapabilityState getBlobStoreStatus() {
        return _blobStoreStatus;
    }

    public void setBlobStoreStatus(CapabilityState _blobStoreStatus) {
        this._blobStoreStatus = _blobStoreStatus;
    }

    public CapabilityState getDataStoreReadStatus() {
        return _dataStoreReadStatus;
    }

    public void setDataStoreReadStatus(CapabilityState _dataStoreReadStatus) {
        this._dataStoreReadStatus = _dataStoreReadStatus;
    }

    public CapabilityState getDataStoreWriteStatus() {
        return _dataStoreWriteStatus;
    }

    public void setDataStoreWriteStatus(CapabilityState _dataStoreWriteStatus) {
        this._dataStoreWriteStatus = _dataStoreWriteStatus;
    }

    public CapabilityState getMemCacheStatus() {
        return _memCacheStatus;
    }

    public void setMemCacheStatus(CapabilityState _memCacheStatus) {
        this._memCacheStatus = _memCacheStatus;
    }

    public CapabilityState getUrlFetchStatus() {
        return _urlFetchStatus;
    }

    public void setUrlFetchStatus(CapabilityState _urlFetchStatus) {
        this._urlFetchStatus = _urlFetchStatus;
    }

    public String getApplicationId() {
        return _applicationId;
    }

    public void setApplicationId(String _applicationId) {
        this._applicationId = _applicationId;
    }

    public String getApplicationVersionId() {
        return _applicationVersionId;
    }

    public void setApplicationVersionId(String _applicationVersionId) {
        this._applicationVersionId = _applicationVersionId;
    }
}
