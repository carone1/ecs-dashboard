/**
 * 
 */
package com.emc.ecs.management.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author nlengc
 *
 */
@XmlRootElement(name = "namespace_quota_details")
@XmlType(propOrder = { "namespace", "blockSize", "notificationSize" })
public class NamespaceQuota {
	
	public final static String NAMESPACE = "namespace";
	public final static String BLOCK_SIZE = "blockSize";
	public final static String NOTIFICATION_SIZE = "notificationSize";
	
	private String namespace;
	private Long blockSize;
	private Long notificationSize;

	@XmlElement(name = NAMESPACE)
	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	@XmlElement(name = BLOCK_SIZE)
	public Long getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(Long blockSize) {
		this.blockSize = blockSize;
	}

	@XmlElement(name = NOTIFICATION_SIZE)
	public Long getNotificationSize() {
		return notificationSize;
	}

	public void setNotificationSize(Long notificationSize) {
		this.notificationSize = notificationSize;
	}

}
