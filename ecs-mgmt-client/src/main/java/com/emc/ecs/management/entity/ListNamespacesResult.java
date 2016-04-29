package com.emc.ecs.management.entity;


import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(name = "namespaces")
public class ListNamespacesResult {
	
	public final static String NAMESPACE        = "namespace";
	public final static String MAX_NAMESPACES   = "MaxNamespaces";
	public final static String NEXT_MARKER      = "NextMarker";
	public final static String FILTER           = "Filter";
	public final static String NEXT_PAGE_FILTER = "NextPageLink";
	
	private List<Namespace> namespaces = new ArrayList<Namespace>();
	private Integer maxNamespaces;	
	private String nextMarker;
	private String filter;
	private String nextPageLink; 	
	
	@XmlElement(name = NAMESPACE)
	public List<Namespace> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(List<Namespace> namespaces) {
		this.namespaces = namespaces;
	}
	
	@XmlElement(name = MAX_NAMESPACES)
	public Integer getMaxNamespaces() {
		return maxNamespaces;
	}

	public void setMaxNamespaces(Integer maxNamespaces) {
		this.maxNamespaces = maxNamespaces;
	}

	@XmlElement(name = NEXT_MARKER)
	public String getNextMarker() {
		return nextMarker;
	}

	public void setNextMarker(String nextMarker) {
		this.nextMarker = nextMarker;
	}
	
	@XmlElement(name = FILTER)
	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	@XmlElement(name = NEXT_PAGE_FILTER)
	public String getNextPageLink() {
		return nextPageLink;
	}

	public void setNextPageLink(String nextPageLink) {
		this.nextPageLink = nextPageLink;
	}
	

}



