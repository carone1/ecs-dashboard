/*

The MIT License (MIT)

Copyright (c) 2016 EMC Corporation

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package com.emc.ecs.management.entity;

import java.net.URI;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



@XmlRootElement(name = "object_bucket")
@XmlType(propOrder = {"created", "softQuota", "fsAccessEnabled", "locked", "vpool", "namespace", "owner", 
		              "isStaleAllowed", "isEncryptionEnabled", "defaultRetention", "blockSize", 
		              "notificationSize", "apiType", "tagSet", "retention", "defaultGroupFileReadPermission",
		              "defaultGroupFileWritePermission", "defaultGroupFileExecutePermission",
		              "defaultGroupDirReadPermission", "defaultGroupDirWritePermission",
		              "defaultGroupDirExecutePermission", "defaultGroup", "searchMetadata",
		              "name", "id", "link", "creationTime", "inactive", "global", "remote",
		              "vdc", "internal" })
public class ObjectBucket {
	
	
	public final static String CREATED_TAG             				     = "created";
	public final static String SOFT_QUOTA_TAG          				     = "softquota";
	public final static String FS_ACCESS_ENABLED_TAG   				     = "fs_access_enabled";
	public final static String LOCKED_TAG              				     = "locked";
	public final static String V_POOL_TAG              				     = "vpool";
	public final static String NAMESPACE_TAG           				     = "namespace";
	public final static String OWNER_TAG               				     = "owner";
	public final static String IS_STALE_ALLOWED_TAG    				     = "is_stale_allowed";
	public final static String IS_ENCRYPTION_ENABLED_TAG		         = "is_encryption_enabled";
	public final static String DEFAULT_RETENTION_TAG   			         = "default_retention";
	public final static String BLOCK_SIZE_TAG          			         = "block_size";
	public final static String NOTIFICATION_SIZE_TAG   			         = "notification_size";
	public final static String API_TYPE_TAG            			         = "api_type";
	public final static String TAG_SET_TAG             			         = "TagSet";
	public final static String RETENTION_TAG           			         = "retention";
	public final static String DEFAULT_GROUP_FILE_READ_PERMISSION_TAG    = "default_group_file_read_permission";
	public final static String DEFAULT_GROUP_FILE_WRITE_PERMISSION_TAG   = "default_group_file_write_permission";
	public final static String DEFAULT_GROUP_FILE_EXECUTE_PERMISSION_TAG = "default_group_file_execute_permission";
	public final static String DEFAULT_GROUP_DIR_READ_PERMISSION_TAG     = "default_group_dir_read_permission";
	public final static String DEFAULT_GROUP_DIR_WRITE_PERMISSION_TAG    = "default_group_dir_write_permission";
	public final static String DEFAULT_GROUP_DIR_EXECUTE_PERMISSION_TAG  = "default_group_dir_execute_permission";
	public final static String DEFAULT_GROUP_TAG					     = "default_group";
	public final static String SEARCH_METADATA_TAG                       = "search_metadata";
	public final static String METADATA_TAG                              = "metadata";
	public final static String NAME_TAG								     = "name";
	public final static String ID_TAG                                    = "id";
	public final static String LINK_TAG								     = "link";
	public final static String CREATION_TIME_TAG    				     = "creation_time";
	public final static String INACTIVE_TAG							     = "inactive";
	public final static String GLOBAL_TAG							     = "global";
	public final static String REMOTE_TAG							     = "remote";
	public final static String VDC_TAG 									 = "vdc";
	public final static String INTERNAL_TAG 							 = "internal";	
	
	
	
	private String         created;
	private String         softQuota;
	private Boolean        fsAccessEnabled;
	private Boolean        locked;
	private String         vpool;
	private String         namespace;
	private String         owner;
	private Boolean        isStaleAllowed;
	private String         isEncryptionEnabled;
	private Long           defaultRetention;
	private Long           blockSize;
	private Long           notificationSize;
	private String         apiType;
	private List<Tag>      tagSet;
	private Long           retention;
	private Boolean        defaultGroupFileReadPermission;
	private Boolean        defaultGroupFileWritePermission;
	private Boolean        defaultGroupFileExecutePermission;
	private Boolean        defaultGroupDirReadPermission;
	private Boolean        defaultGroupDirWritePermission;
	private Boolean        defaultGroupDirExecutePermission;
	private String         defaultGroup;
	private List<Metadata> searchMetadata;
	private String         name;
	private URI            id;
	private String         link;
	private Date	   	   creationTime;
	private Boolean		   inactive;
	private Boolean		   global;
	private Boolean        remote;
	private Vdc            vdc;
	private Boolean        internal;
	
	@XmlElement(name = CREATED_TAG)
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	
	@XmlElement(name = SOFT_QUOTA_TAG)
	public String getSoftQuota() {
		return softQuota;
	}
	public void setSoftQuota(String softQuota) {
		this.softQuota = softQuota;
	}
	
	@XmlElement(name = FS_ACCESS_ENABLED_TAG)
	public Boolean getFsAccessEnabled() {
		return fsAccessEnabled;
	}
	public void setFsAccessEnabled(Boolean fsAccessEnabled) {
		this.fsAccessEnabled = fsAccessEnabled;
	}
	
	@XmlElement(name = LOCKED_TAG)
	public Boolean getLocked() {
		return locked;
	}
	public void setLocked(Boolean locked) {
		this.locked = locked;
	}
	
	@XmlElement(name = V_POOL_TAG)
	public String getVpool() {
		return vpool;
	}
	public void setVpool(String vpool) {
		this.vpool = vpool;
	}
	
	@XmlElement(name = NAMESPACE_TAG)
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	@XmlElement(name = OWNER_TAG)
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	@XmlElement(name = IS_STALE_ALLOWED_TAG)
	public Boolean getIsStaleAllowed() {
		return isStaleAllowed;
	}
	public void setIsStaleAllowed(Boolean isStaleAllowed) {
		this.isStaleAllowed = isStaleAllowed;
	}
	
	@XmlElement(name = IS_ENCRYPTION_ENABLED_TAG )
	public String getIsEncryptionEnabled() {
		return isEncryptionEnabled;
	}
	public void setIsEncryptionEnabled(String isEncryptionEnabled) {
		this.isEncryptionEnabled = isEncryptionEnabled;
	}
	
	@XmlElement(name = DEFAULT_RETENTION_TAG )
	public Long getDefaultRetention() {
		return defaultRetention;
	}
	public void setDefaultRetention(Long defaultRetention) {
		this.defaultRetention = defaultRetention;
	}
	
	@XmlElement(name = BLOCK_SIZE_TAG )
	public Long getBlockSize() {
		return blockSize;
	}
	public void setBlockSize(Long blockSize) {
		this.blockSize = blockSize;
	}
	
	@XmlElement(name = NOTIFICATION_SIZE_TAG )
	public Long getNotificationSize() {
		return notificationSize;
	}
	public void setNotificationSize(Long notificationSize) {
		this.notificationSize = notificationSize;
	}
	
	@XmlElement(name = API_TYPE_TAG )
	public String getApiType() {
		return apiType;
	}
	public void setApiType(String apiType) {
		this.apiType = apiType;
	}
	
	@XmlElement(name = TAG_SET_TAG )
	public List<Tag> getTagSet() {
		return tagSet;
	}
	public void setTagSet(List<Tag> tagSet) {
		this.tagSet = tagSet;
	}
	
	@XmlElement(name = RETENTION_TAG )
	public Long getRetention() {
		return retention;
	}
	public void setRetention(Long retention) {
		this.retention = retention;
	}
	
	@XmlElement(name = DEFAULT_GROUP_FILE_READ_PERMISSION_TAG )
	public Boolean getDefaultGroupFileReadPermission() {
		return defaultGroupFileReadPermission;
	}
	public void setDefaultGroupFileReadPermission(Boolean defaultGroupFileReadPermission) {
		this.defaultGroupFileReadPermission = defaultGroupFileReadPermission;
	}
	
	@XmlElement(name = DEFAULT_GROUP_FILE_WRITE_PERMISSION_TAG )
	public Boolean getDefaultGroupFileWritePermission() {
		return defaultGroupFileWritePermission;
	}
	public void setDefaultGroupFileWritePermission(Boolean defaultGroupFileWritePermission) {
		this.defaultGroupFileWritePermission = defaultGroupFileWritePermission;
	}
	
	@XmlElement(name = DEFAULT_GROUP_FILE_EXECUTE_PERMISSION_TAG )
	public Boolean getDefaultGroupFileExecutePermission() {
		return defaultGroupFileExecutePermission;
	}
	public void setDefaultGroupFileExecutePermission(Boolean defaultGroupFileExecutePermission) {
		this.defaultGroupFileExecutePermission = defaultGroupFileExecutePermission;
	}
	
	@XmlElement(name = DEFAULT_GROUP_DIR_READ_PERMISSION_TAG )
	public Boolean getDefaultGroupDirReadPermission() {
		return defaultGroupDirReadPermission;
	}
	public void setDefaultGroupDirReadPermission(Boolean defaultGroupDirReadPermission) {
		this.defaultGroupDirReadPermission = defaultGroupDirReadPermission;
	}
	
	@XmlElement(name = DEFAULT_GROUP_DIR_WRITE_PERMISSION_TAG )
	public Boolean getDefaultGroupDirWritePermission() {
		return defaultGroupDirWritePermission;
	}
	public void setDefaultGroupDirWritePermission(Boolean defaultGroupDirWritePermission) {
		this.defaultGroupDirWritePermission = defaultGroupDirWritePermission;
	}
	
	@XmlElement(name = DEFAULT_GROUP_DIR_EXECUTE_PERMISSION_TAG )
	public Boolean getDefaultGroupDirExecutePermission() {
		return defaultGroupDirExecutePermission;
	}
	public void setDefaultGroupDirExecutePermission(Boolean defaultGroupDirExecutePermission) {
		this.defaultGroupDirExecutePermission = defaultGroupDirExecutePermission;
	}
	
	@XmlElement(name = DEFAULT_GROUP_TAG )
	public String getDefaultGroup() {
		return defaultGroup;
	}
	public void setDefaultGroup(String defaultGroup) {
		this.defaultGroup = defaultGroup;
	}
	
	@XmlElementWrapper(name= SEARCH_METADATA_TAG )
	@XmlElement(name = METADATA_TAG )
	public List<Metadata> getSearchMetadata() {
		return searchMetadata;
	}
	public void setSearchMetadata(List<Metadata> searchMetadata) {
		this.searchMetadata = searchMetadata;
	}
	
	@XmlElement(name = NAME_TAG )
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name = ID_TAG )
	public URI getId() {
		return id;
	}
	public void setId(URI id) {
		this.id = id;
	}
	
	@XmlElement(name = LINK_TAG)
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	
	@XmlElement(name = CREATION_TIME_TAG )
	public Date getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	
	@XmlElement(name = INACTIVE_TAG )
	public Boolean getInactive() {
		return inactive;
	}
	public void setInactive(Boolean inactive) {
		this.inactive = inactive;
	}
	
	@XmlElement(name = GLOBAL_TAG )
	public Boolean getGlobal() {
		return global;
	}
	public void setGlobal(Boolean global) {
		this.global = global;
	}
	
	@XmlElement(name = REMOTE_TAG )
	public Boolean getRemote() {
		return remote;
	}
	public void setRemote(Boolean remote) {
		this.remote = remote;
	}
	
	@XmlElement(name = VDC_TAG )
	public Vdc getVdc() {
		return vdc;
	}
	public void setVdc(Vdc vdc) {
		this.vdc = vdc;
	}
	
	@XmlElement(name = INTERNAL_TAG )
	public Boolean getInternal() {
		return internal;
	}
	public void setInternal(Boolean internal) {
		this.internal = internal;
	}
	
}



