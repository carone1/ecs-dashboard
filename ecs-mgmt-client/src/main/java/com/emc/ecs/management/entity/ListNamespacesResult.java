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



