/**
 * 
 */
package com.emc.ecs.management.entity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author nlengc
 *
 */
@XmlRootElement(name = "user_mapping")
@XmlType(propOrder = {"domain", "attributes", "groups"})
public class UserMapping {
	public final static String DOMAIN = "domain";
	public final static String ATTRIBUTES = "attributes";
	public final static String GROUPS = "groups";
	private String domain;
	private List<Attribute> attributes = new ArrayList<>();
	private List<String> groups = new ArrayList<>();
	
	@XmlElement(name = DOMAIN)
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	@XmlElement(name = ATTRIBUTES)
	public List<Attribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	@XmlElement(name = GROUPS)
	public List<String> getGroups() {
		return groups;
	}
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
}
