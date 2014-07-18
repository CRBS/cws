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

package edu.ucsd.crbs.cws.workflow;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import java.util.Date;

/**
 * Represents a file
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@Entity
public class WorkspaceFile {
    
    @Id private Long _id;
    @Index private String _name;
    @Index private String _type;
    @Index private String _owner;
    private String _description;
    private Date _createDate;
    private Long _size;
    private String _md5;
    @Index private boolean _Deleted = false;
    @Index private boolean _Dir;
    @Index private String _path;
    @Index private Long _sourceTaskId;
    @Index private String _blobKey;
    @Ignore private String _uploadURL;
    
    public Long getId() {
        return _id;
    }

    public void setId(Long _id) {
        this._id = _id;
    }

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public String getType() {
        return _type;
    }

    public void setType(String _type) {
        this._type = _type;
    }

    public String getOwner() {
        return _owner;
    }

    public void setOwner(String _owner) {
        this._owner = _owner;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String _description) {
        this._description = _description;
    }

    public Date getCreateDate() {
        return _createDate;
    }

    public void setCreateDate(Date _createDate) {
        this._createDate = _createDate;
    }

    public Long getSize() {
        return _size;
    }

    public void setSize(Long _size) {
        this._size = _size;
    }

    public String getMd5() {
        return _md5;
    }

    public void setMd5(String _md5) {
        this._md5 = _md5;
    }

    public boolean getDeleted() {
        return _Deleted;
    }

    public void setDeleted(boolean deleted) {
        this._Deleted = deleted;
    }

    public boolean getDir() {
        return _Dir;
    }

    public void setDir(boolean isDir) {
        _Dir = isDir;
    }

    public String getPath() {
        return _path;
    }

    public void setPath(String _path) {
        this._path = _path;
    }

    public Long getSourceTaskId() {
        return _sourceTaskId;
    }

    public void setSourceTaskId(Long _sourceTaskId) {
        this._sourceTaskId = _sourceTaskId;
    }

    public String getBlobKey() {
        return _blobKey;
    }

    public void setBlobKey(String _blobKey) {
        this._blobKey = _blobKey;
    }

    public String getUploadURL() {
        return _uploadURL;
    }

    public void setUploadURL(String _uploadURL) {
        this._uploadURL = _uploadURL;
    }
}
