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
	
	@XmlElement(name = "created")
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	
	@XmlElement(name = "softquota")
	public String getSoftQuota() {
		return softQuota;
	}
	public void setSoftQuota(String softQuota) {
		this.softQuota = softQuota;
	}
	
	@XmlElement(name = "fs_access_enabled")
	public Boolean getFsAccessEnabled() {
		return fsAccessEnabled;
	}
	public void setFsAccessEnabled(Boolean fsAccessEnabled) {
		this.fsAccessEnabled = fsAccessEnabled;
	}
	
	@XmlElement(name = "locked")
	public Boolean getLocked() {
		return locked;
	}
	public void setLocked(Boolean locked) {
		this.locked = locked;
	}
	
	@XmlElement(name = "vpool")
	public String getVpool() {
		return vpool;
	}
	public void setVpool(String vpool) {
		this.vpool = vpool;
	}
	
	@XmlElement(name = "namespace")
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	@XmlElement(name = "owner")
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	@XmlElement(name = "is_stale_allowed")
	public Boolean getIsStaleAllowed() {
		return isStaleAllowed;
	}
	public void setIsStaleAllowed(Boolean isStaleAllowed) {
		this.isStaleAllowed = isStaleAllowed;
	}
	
	@XmlElement(name = "is_encryption_enabled")
	public String getIsEncryptionEnabled() {
		return isEncryptionEnabled;
	}
	public void setIsEncryptionEnabled(String isEncryptionEnabled) {
		this.isEncryptionEnabled = isEncryptionEnabled;
	}
	
	@XmlElement(name = "default_retention")
	public Long getDefaultRetention() {
		return defaultRetention;
	}
	public void setDefaultRetention(Long defaultRetention) {
		this.defaultRetention = defaultRetention;
	}
	
	@XmlElement(name = "block_size")
	public Long getBlockSize() {
		return blockSize;
	}
	public void setBlockSize(Long blockSize) {
		this.blockSize = blockSize;
	}
	
	@XmlElement(name = "notification_size")
	public Long getNotificationSize() {
		return notificationSize;
	}
	public void setNotificationSize(Long notificationSize) {
		this.notificationSize = notificationSize;
	}
	
	@XmlElement(name = "api_type")
	public String getApiType() {
		return apiType;
	}
	public void setApiType(String apiType) {
		this.apiType = apiType;
	}
	
	@XmlElement(name = "TagSet")
	public List<Tag> getTagSet() {
		return tagSet;
	}
	public void setTagSet(List<Tag> tagSet) {
		this.tagSet = tagSet;
	}
	
	@XmlElement(name = "retention")
	public Long getRetention() {
		return retention;
	}
	public void setRetention(Long retention) {
		this.retention = retention;
	}
	
	@XmlElement(name = "default_group_file_read_permission")
	public Boolean getDefaultGroupFileReadPermission() {
		return defaultGroupFileReadPermission;
	}
	public void setDefaultGroupFileReadPermission(Boolean defaultGroupFileReadPermission) {
		this.defaultGroupFileReadPermission = defaultGroupFileReadPermission;
	}
	
	@XmlElement(name = "default_group_file_write_permission")
	public Boolean getDefaultGroupFileWritePermission() {
		return defaultGroupFileWritePermission;
	}
	public void setDefaultGroupFileWritePermission(Boolean defaultGroupFileWritePermission) {
		this.defaultGroupFileWritePermission = defaultGroupFileWritePermission;
	}
	
	@XmlElement(name = "default_group_file_execute_permission")
	public Boolean getDefaultGroupFileExecutePermission() {
		return defaultGroupFileExecutePermission;
	}
	public void setDefaultGroupFileExecutePermission(Boolean defaultGroupFileExecutePermission) {
		this.defaultGroupFileExecutePermission = defaultGroupFileExecutePermission;
	}
	
	@XmlElement(name = "default_group_dir_read_permission")
	public Boolean getDefaultGroupDirReadPermission() {
		return defaultGroupDirReadPermission;
	}
	public void setDefaultGroupDirReadPermission(Boolean defaultGroupDirReadPermission) {
		this.defaultGroupDirReadPermission = defaultGroupDirReadPermission;
	}
	
	@XmlElement(name = "default_group_dir_write_permission")
	public Boolean getDefaultGroupDirWritePermission() {
		return defaultGroupDirWritePermission;
	}
	public void setDefaultGroupDirWritePermission(Boolean defaultGroupDirWritePermission) {
		this.defaultGroupDirWritePermission = defaultGroupDirWritePermission;
	}
	
	@XmlElement(name = "default_group_dir_execute_permission")
	public Boolean getDefaultGroupDirExecutePermission() {
		return defaultGroupDirExecutePermission;
	}
	public void setDefaultGroupDirExecutePermission(Boolean defaultGroupDirExecutePermission) {
		this.defaultGroupDirExecutePermission = defaultGroupDirExecutePermission;
	}
	
	@XmlElement(name = "default_group")
	public String getDefaultGroup() {
		return defaultGroup;
	}
	public void setDefaultGroup(String defaultGroup) {
		this.defaultGroup = defaultGroup;
	}
	
	@XmlElementWrapper(name="search_metadata")
	@XmlElement(name = "metadata")
	public List<Metadata> getSearchMetadata() {
		return searchMetadata;
	}
	public void setSearchMetadata(List<Metadata> searchMetadata) {
		this.searchMetadata = searchMetadata;
	}
	
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name = "id")
	public URI getId() {
		return id;
	}
	public void setId(URI id) {
		this.id = id;
	}
	
	@XmlElement(name = "link")
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	
	@XmlElement(name = "creation_file")
	public Date getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	
	@XmlElement(name = "inactive")
	public Boolean getInactive() {
		return inactive;
	}
	public void setInactive(Boolean inactive) {
		this.inactive = inactive;
	}
	
	@XmlElement(name = "global")
	public Boolean getGlobal() {
		return global;
	}
	public void setGlobal(Boolean global) {
		this.global = global;
	}
	
	@XmlElement(name = "remote")
	public Boolean getRemote() {
		return remote;
	}
	public void setRemote(Boolean remote) {
		this.remote = remote;
	}
	
	@XmlElement(name = "vdc")
	public Vdc getVdc() {
		return vdc;
	}
	public void setVdc(Vdc vdc) {
		this.vdc = vdc;
	}
	
	@XmlElement(name = "internal")
	public Boolean getInternal() {
		return internal;
	}
	public void setInternal(Boolean internal) {
		this.internal = internal;
	}
	
}



