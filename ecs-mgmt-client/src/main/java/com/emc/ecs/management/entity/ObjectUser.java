package com.emc.ecs.management.entity;

import java.net.URI;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



@XmlRootElement(name = "blobuser")
@XmlType(propOrder = { "userId", "namespace" })
public class ObjectUser {
	
	private URI userId;
	private URI namespace;
	
	
	@XmlElement(name = "userid")
	public URI getUserId() {
		return userId;
	}
	public void setUserId(URI userId) {
		this.userId = userId;
	}
	
	@XmlElement(name = "namespace")
	public URI getNamespace() {
		return namespace;
	}
	public void setNamespace(URI namespace) {
		this.namespace = namespace;
	}
	
}



