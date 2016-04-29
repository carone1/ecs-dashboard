package com.emc.ecs.management.entity;

import java.net.URI;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



@XmlRootElement(name = "blobuser")
@XmlType(propOrder = { "userId", "namespace" })
public class ObjectUser {
	
	public final static String USER_ID   = "userid";
	public final static String NAMESPACE = "namespace";
	
	
	private URI userId;
	private URI namespace;
	
	
	@XmlElement(name = USER_ID)
	public URI getUserId() {
		return userId;
	}
	public void setUserId(URI userId) {
		this.userId = userId;
	}
	
	@XmlElement(name = NAMESPACE)
	public URI getNamespace() {
		return namespace;
	}
	public void setNamespace(URI namespace) {
		this.namespace = namespace;
	}
	
}



