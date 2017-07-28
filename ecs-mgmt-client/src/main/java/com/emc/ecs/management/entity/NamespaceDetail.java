/**
 * 
 */
package com.emc.ecs.management.entity;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author nlengc
 *
 */
@XmlRootElement(name = "namespace")
@XmlType(propOrder = {"defaultDataServicesVPool", "allowedVPools", "disallowedVPools", "namespaceAdmins", "isEncryptionEnabled", 
		"userMappings", "defaultBucketBlockSize", "externalGroupAdmins", "isStaledAllowed", "isComplianceEnabled",
		"name", "id", "link", "creationTime", "global", "inactive", "remote", "vdc", "internal"})
public class NamespaceDetail {

	public final static String ID = "id";
	public final static String LINK = "link";
	public final static String NAME = "name";
	public final static String DEFAULT_DATA_SERVCIES_VPOOL = "default_data_services_vpool";
	public final static String ALLOWED_VPOOLS_LIST = "allowed_vpools_list";
	public final static String DISALLOWED_VPOOLS_LIST = "disallowed_vpools_list";
	public final static String NAMESPACE_ADMINS = "namespace_admins";
	public final static String IS_ENCRYPTION_ENABLED = "is_encryption_enabled";
	public final static String DEFAULT_BUCKET_BLOCK_SIZE = "default_bucket_block_size";
	public final static String USER_MAPPING = "user_mapping";
	public final static String IS_STALE_ALLOWED = "is_stale_allowed";
	public final static String IS_COMPLIANCE_ENABLED = "is_compliance_enabled";
	public final static String CREATION_TIME = "creation_time";
	public final static String EXTERNAL_GROUP_ADMINS = "external_group_admins";
	public final static String GLOBAL = "global";
	public final static String INACTIVE = "inactive";
	public final static String REMOTE = "remote";
	public final static String INTERNAL = "internal";
	public final static String VDC = "vdc"; 

	private URI id;
	private String link;
	private String name;
	private String namespaceAdmins;
	private URI defaultDataServicesVPool;
	private List<URI> allowedVPools = new ArrayList<>();
	private List<URI> disallowedVPools = new ArrayList<>();
	private Boolean isEncryptionEnabled;
	private Boolean isStaledAllowed;
	private Boolean isComplianceEnabled;
	private Boolean global;
	private Boolean inactive;
	private Boolean remote;
	private Boolean internal;
	private Vdc vdc;
	private List<UserMapping> userMappings = new ArrayList<>();
	private Date creationTime;
	private Long defaultBucketBlockSize;
	private String externalGroupAdmins;

	@XmlElement(name = ID)
	public URI getId() {
		return id;
	}

	public void setId(URI id) {
		this.id = id;
	}

	@XmlElement(name = LINK)
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@XmlElement(name = NAME)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = NAMESPACE_ADMINS)
	public String getNamespaceAdmins() {
		return namespaceAdmins;
	}

	public void setNamespaceAdmins(String namespaceAdmins) {
		this.namespaceAdmins = namespaceAdmins;
	}

	@XmlElement(name = DEFAULT_DATA_SERVCIES_VPOOL)
	public URI getDefaultDataServicesVPool() {
		return defaultDataServicesVPool;
	}

	public void setDefaultDataServicesVPool(URI defaultDataServicesVPool) {
		this.defaultDataServicesVPool = defaultDataServicesVPool;
	}

	@XmlElement(name = ALLOWED_VPOOLS_LIST)
	public List<URI> getAllowedVPools() {
		return allowedVPools;
	}

	public void setAllowedVPools(List<URI> allowedVPools) {
		this.allowedVPools = allowedVPools;
	}

	@XmlElement(name = DISALLOWED_VPOOLS_LIST)
	public List<URI> getDisallowedVPools() {
		return disallowedVPools;
	}

	public void setDisallowedVPools(List<URI> disallowedVPools) {
		this.disallowedVPools = disallowedVPools;
	}

	@XmlElement(name = IS_ENCRYPTION_ENABLED)
	public Boolean getIsEncryptionEnabled() {
		return isEncryptionEnabled;
	}

	public void setIsEncryptionEnabled(Boolean isEncryptionEnabled) {
		this.isEncryptionEnabled = isEncryptionEnabled;
	}

	@XmlElement(name = IS_STALE_ALLOWED)
	public Boolean getIsStaledAllowed() {
		return isStaledAllowed;
	}

	public void setIsStaledAllowed(Boolean isStaledAllowed) {
		this.isStaledAllowed = isStaledAllowed;
	}

	@XmlElement(name = IS_COMPLIANCE_ENABLED)
	public Boolean getIsComplianceEnabled() {
		return isComplianceEnabled;
	}

	public void setIsComplianceEnabled(Boolean isComplianceEnabled) {
		this.isComplianceEnabled = isComplianceEnabled;
	}

	@XmlElement(name = GLOBAL)
	public Boolean getGlobal() {
		return global;
	}

	public void setGlobal(Boolean global) {
		this.global = global;
	}

	@XmlElement(name = INACTIVE)
	public Boolean getInactive() {
		return inactive;
	}

	public void setInactive(Boolean inactive) {
		this.inactive = inactive;
	}

	@XmlElement(name = REMOTE)
	public Boolean getRemote() {
		return remote;
	}

	public void setRemote(Boolean remote) {
		this.remote = remote;
	}

	@XmlElement(name = INTERNAL)
	public Boolean getInternal() {
		return internal;
	}

	public void setInternal(Boolean internal) {
		this.internal = internal;
	}

	@XmlElement(name = VDC)
	public Vdc getVdc() {
		return vdc;
	}

	public void setVdc(Vdc vdc) {
		this.vdc = vdc;
	}

	@XmlElement(name = USER_MAPPING)
	public List<UserMapping> getUserMappings() {
		return userMappings;
	}

	public void setUserMappings(List<UserMapping> userMappings) {
		this.userMappings = userMappings;
	}

	@XmlElement(name = CREATION_TIME)
	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	@XmlElement(name = CREATION_TIME)
	public Long getDefaultBucketBlockSize() {
		return defaultBucketBlockSize;
	}

	public void setDefaultBucketBlockSize(Long defaultBucketBlockSize) {
		this.defaultBucketBlockSize = defaultBucketBlockSize;
	}

	@XmlElement(name = EXTERNAL_GROUP_ADMINS)
	public String getExternalGroupAdmins() {
		return externalGroupAdmins;
	}

	public void setExternalGroupAdmins(String externalGroupAdmins) {
		this.externalGroupAdmins = externalGroupAdmins;
	}
	
}
