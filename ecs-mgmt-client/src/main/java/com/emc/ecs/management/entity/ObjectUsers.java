package com.emc.ecs.management.entity;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



@XmlRootElement(name = "users")
@XmlType(propOrder = {"blobUser", "maxUsers", "nextMarker", "filter", "nextPathLink" })

public class ObjectUsers {
	
	private List<ObjectUser> blobUser;
	private Integer maxUsers;
	private String nextMarker;
	private String filter;
	private Long nextPathLink;
	
	@XmlElement(name = "blobuser")
	public List<ObjectUser> getBlobUser() {
		return blobUser;
	}
	public void setBlobUser(List<ObjectUser> blobUser) {
		this.blobUser = blobUser;
	}
	
	@XmlElement(name = "MaxUsers")
	public Integer getMaxUsers() {
		return maxUsers;
	}
	public void setMaxUsers(Integer maxUsers) {
		this.maxUsers = maxUsers;
	}
	
	@XmlElement(name = "NextMarker")
	public String getNextMarker() {
		return nextMarker;
	}
	public void setNextMarker(String nextMarker) {
		this.nextMarker = nextMarker;
	}
	
	@XmlElement(name = "Filter")
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	@XmlElement(name = "NextPageLink")
	public Long getNextPathLink() {
		return nextPathLink;
	}
	public void setNextPathLink(Long nextPathLink) {
		this.nextPathLink = nextPathLink;
	}	
	
}



