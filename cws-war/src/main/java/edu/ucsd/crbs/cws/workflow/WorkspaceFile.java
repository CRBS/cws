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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.annotation.AlsoLoad;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import java.util.Date;

/**
 * Represents a file or directory in the CRBS Workflow Service.<p/>  
 * These are usually generated when {@link Job} objects are created.<br/>
 * The method {@link #getPath() } connects this object with the real filesystem 
 * and is only set once physical the file or directory exists.  
 * The {@link #getDir() } method denotes whether this object points to a file 
 * or directory.  <br/>
 * In addition, these objects can also optionally be stored in GAE cloud storage
 * in which case {@link #getBlobKey() } will be set with a value.<p/>
 * 
 *
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
    @Index @AlsoLoad("_Deleted") private boolean _deleted = false;
    @Index @AlsoLoad("_Dir") private boolean _dir;
    @Index private String _path;
    @Index private Long _sourceJobId;
    @Index private String _blobKey;
    @Index private boolean _failed = false;
    @Ignore private String _uploadURL;
    
    /**
     * Gets identifier for this {@link WorkspaceFile}.  This is usually generated
     * by whatever datastore is used in <b><code>DAO</code></b> objects.
     * @return identifier for <b><code>this</code></b> object
     */
    public Long getId() {
        return _id;
    }

    /**
     * Sets identifier for this {@link WorkspaceFile}
     * @param id identifier to set
     */
    public void setId(Long id) {
        _id = id;
    }

    /**
     * @return Workspace name
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets Workspace name
     * @param name Name to set
     */
    public void setName(String name) {
        this._name = name;
    }

    /**
     * Usually set to name of {@link Workflow} that generated this object
     * @return Type of {@link WorkspaceFile}
     */
    public String getType() {
        return _type;
    }

    /**
     * Usually set to name of {@link Workflow} that generated this object
     * @param type Type of {@link WorkspaceFile}
     */
    public void setType(String type) {
        this._type = type;
    }

    /**
     * Gets the owner of this {@link WorkspaceFile}
     * @return owner of this {@link WorkspaceFile}
     */
    public String getOwner() {
        return _owner;
    }

    /**
     * Sets owner of this {@link WorkspaceFile}
     * @param owner Owner to set
     */
    public void setOwner(String owner) {
        _owner = owner;
    }

    /**
     * @return Description for this {@link WorkspaceFile}
     */
    public String getDescription() {
        return _description;
    }

    /**
     * Sets the description for this {@link WorkspaceFile} replacing any 
     * previously set value
     * @param description Description to set
     */
    public void setDescription(String description) {
        _description = description;
    }

    /**
     * @return Gets date this object was created
     */
    public Date getCreateDate() {
        return _createDate;
    }

    /**
     * Sets date object is created, replacing any existing value
     * @param createDate Date to set as create date
     */
    public void setCreateDate(Date createDate) {
        _createDate = createDate;
    }

    /**
     * @return Gets size in bytes that this {@link WorkspaceFile} consumes and can be null if unknown
     */
    public Long getSize() {
        return _size;
    }

    /**
     * Sets size in bytes this {@link WorkspaceFile} consumes
     * @param size Size in bytes
     */
    public void setSize(Long size) {
        _size = size;
    }

    /**
     * @return md5 checksum of this {@link WorkspaceFile} if set
     */
    public String getMd5() {
        return _md5;
    }

    /**
     * Sets md5 checksum of this {@link WorkspaceFile}
     * @param _md5 
     */
    public void setMd5(String _md5) {
        this._md5 = _md5;
    }

    /**
     * Denotes whether this {@link WorkspaceFile} has been logically deleted
     * @return <b><code>true</code></b> if this {@link WorkspaceFile} has been logically deleted <b><code>false</code></b> otherwise
     */
    public boolean getDeleted() {
        return _deleted;
    }

    /**
     * Sets whether this {@link WorkspaceFile} has been logically deleted
     * @param deleted <b><code>true</code></b> if this {@link WorkspaceFile} has 
     * been logically deleted <b><code>false</code></b> otherwise
     */
    public void setDeleted(boolean deleted) {
        _deleted = deleted;
    }

    /**
     * Denotes whether this {@link WorkspaceFile} represents a directory
     * @return <b><code>true</code></b> if this {@link WorkspaceFile} represents 
     * a directory <b><code>false</code></b> otherwise
     */
    public boolean getDir() {
        return _dir;
    }

    /**
     * Sets whether this {@link WorkspaceFile} represents a directory replacing
     * any previously set value
     * @param isDir <b><code>true</code></b> if this {@link WorkspaceFile} 
     * represents a directory <b><code>false</code></b> otherwise
     */
    public void setDir(boolean isDir) {
        _dir = isDir;
    }

    /**
     * Gets path on physical file system for this {@link WorkspaceFile}
     * @return full path on physical file system or <b><code>null</code></b>
     */
    public String getPath() {
        return _path;
    }

    /**
     * Sets path on physical file system for this {@link WorkspaceFile} replacing
     * any value previously set
     * @param path full path on physical file system or <b><code>null</code></b>
     */
    public void setPath(String path) {
        _path = path;
    }

    /**
     * Gets identifier of {@link Job} that generated this {@link WorkspaceFile}
     * @return {@link Job} id
     */
    public Long getSourceJobId() {
        return _sourceJobId;
    }

    /**
     * Sets identifier of {@link Job} that generated this {@link WorkspaceFile}
     * @param sourceJobId {@link Job} id
     */
    public void setSourceJobId(Long sourceJobId) {
        this._sourceJobId = sourceJobId;
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
    
    /**
     * Denotes whether this {@link WorkspaceFile} was generated from a {@link Job}
     * ,linked via {@link #getSourceJobId() }, that failed to run correctly.  
     * @return <b><code>true</code></b> if the {@link #getSourceJobId() } failed <b><code>false</code></b> otherwise
     */
    public boolean isFailed() {
        return _failed;
    }

    /**
     * Sets whether this {@link WorkspaceFile} was generated from a {@link Job}
     * ,linked via {@link #getSourceJobId() }, that failed to run correctly.  
     * @param failed <b><code>true</code></b> if the {@link #getSourceJobId() } failed <b><code>false</code></b> otherwise
     */
    public void setFailed(boolean failed) {
        _failed = failed;
    }
    
    /**
     * Updates this {@link WorkspaceFile} with any changes found in <b>wsf</b> passed
     * in.  This implementation examines the following fields and compares them
     * if they are <b>NOT <code>null</code></b><br/>
     * <ul>
     * {@link #getBlobKey() }<br/>
     * {@link #getCreateDate() }<br/>
     * {@link #getDescription() }<br/>
     * {@link #getMd5() }<br/>
     * {@link #getName() }<br/>
     * {@link #getOwner() }<br/>
     * {@link #getPath() }<br/>
     * {@link #getSize() }<br/>
     * {@link #getSourceJobId() }<br/>
     * {@link #getType() }
     * </ul>
     * <p/>
     * 
     * If any of the above fields are different then what is in <code>this</code><br/>
     * {@link WorkspaceFile} then this {@link WorkspaceFile} is updated and 
     * this method returns <code>true</code><br/>
     * In addition, if any of these parameters <b>isDeleted, isFailed, or isDir</b> are 
     * <b>NOT <code>null</code></b><br/> and different then the value in this 
     * {@link WorkspaceFile}.  Then they are used to update {@link #getDeleted()},
     * {@link #isFailed() }, and {@link #getDir() } respectively
     * 
     * 
     * @param wsf
     * @param isDeleted <b>NOT null and <code>true</code></b> 
     * @param isFailed
     * @param isDir
     * @return <b><code>true</code></b> if <b><code>this</code></b> object was updated with values from <b>wsf</b> passed in otherwise <b><code>false</code></b>
     */
    @JsonIgnore
    public boolean updateWithChanges(WorkspaceFile wsf,Boolean isDeleted,
            Boolean isFailed,Boolean isDir){
        if (wsf == null){
            return false;
        }
        boolean updated = false;
        
        if (isDeleted != null && isDeleted != getDeleted()){
            setDeleted(isDeleted);
            updated = true;
        }   
        
        if (isFailed != null && isFailed != isFailed()){
            setFailed(isFailed);
            updated = true;
        }
        
        if (isDir != null && isDir != getDir()){
            setDir(isDir);
            updated = true;
        }
        if (wsf.getBlobKey() != null){
            if (getBlobKey() == null ||
                !getBlobKey().equals(wsf.getBlobKey())){
                setBlobKey(wsf.getBlobKey());
                updated = true;
            }
        }
        
        if (wsf.getCreateDate() != null){
            if (getCreateDate() == null || 
                !getCreateDate().equals(wsf.getCreateDate())){
                setCreateDate(wsf.getCreateDate());
                updated = true;
            }
        }
        if (wsf.getDescription() != null){
            if (getDescription() == null ||
                !getDescription().equals(wsf.getDescription())){
                setDescription(wsf.getDescription());
                updated = true;
            }
        }
        if (wsf.getMd5() != null){
            if (getMd5() == null ||
                !getMd5().equals(wsf.getMd5())){
                setMd5(wsf.getMd5());
                updated = true;
            }
        }
        if (wsf.getName() != null){
            if (getName() == null ||
                !getName().equals(wsf.getName())){
                setName(wsf.getName());
                updated = true;
            }
        }
        
        if (wsf.getOwner() != null){
            if (getOwner() == null ||
                !getOwner().equals(wsf.getOwner())){
                setOwner(wsf.getOwner());
                updated = true;
            }
        }
        if (wsf.getPath() != null){
            if (getPath() == null ||
                !getPath().equals(wsf.getPath())){
                setPath(wsf.getPath());
                updated = true;
            }
        }
        if (wsf.getSize() != null){
            if (getSize() == null ||
                wsf.getSize() != getSize()){
                setSize(wsf.getSize());
                updated = true;
            }
        }

        if (wsf.getSourceJobId() != null){
            if (getSourceJobId() == null ||
                getSourceJobId() != wsf.getSourceJobId()){
                setSourceJobId(wsf.getSourceJobId());
                updated = true;
            }
        }
        if (wsf.getType() != null){
            if (getType() == null ||
                !getType().equals(wsf.getType())){
                setType(wsf.getType());
                updated = true;
            }
        }
        return updated;
    }
}
