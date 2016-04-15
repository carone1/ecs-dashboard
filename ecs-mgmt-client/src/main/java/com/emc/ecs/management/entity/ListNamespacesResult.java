package com.emc.ecs.management.entity;


import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(name = "namespaces")
public class ListNamespacesResult {
	
	private List<Namespace> namespaces = new ArrayList<Namespace>();
	private Integer maxNamespaces;	
	private String nextMarker;
	private String filter;
	private String nextPageLink; 	
	
	@XmlElement(name = "namespace")
	public List<Namespace> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(List<Namespace> namespaces) {
		this.namespaces = namespaces;
	}
	
	@XmlElement(name = "MaxNamespaces")
	public Integer getMaxNamespaces() {
		return maxNamespaces;
	}

	public void setMaxNamespaces(Integer maxNamespaces) {
		this.maxNamespaces = maxNamespaces;
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
	public String getNextPageLink() {
		return nextPageLink;
	}

	public void setNextPageLink(String nextPageLink) {
		this.nextPageLink = nextPageLink;
	}
	





}



