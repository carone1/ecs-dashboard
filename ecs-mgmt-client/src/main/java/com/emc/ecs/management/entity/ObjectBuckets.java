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

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



@XmlRootElement(name = "object_buckets")
@XmlType(propOrder = {"objectBucket", "maxBuckets", "nextMarker", "filter", "nextPageLink" })

public class ObjectBuckets {
	
	public final static String OBJECT_BUCKET  = "object_bucket";
	public final static String MAX_BUCKETS    = "MaxBuckets";
	public final static String NEXT_MARKER    = "NextMarker";
	public final static String FILTER         = "Filter";
	public final static String NEXT_PAGE_LINK = "NextPageLink";	
	
	
	private List<ObjectBucket> objectBucketList;
	private Integer 			maxBuckets;
	private String 				nextMarker;
	private String 				filter;
	private Long 				nextPageLink;
	
	@XmlElement(name = OBJECT_BUCKET)
	public List<ObjectBucket> getObjectBucket() {
		return objectBucketList;
	}
	public void setObjectBucket(List<ObjectBucket> objectBucket) {
		this.objectBucketList = objectBucket;
	}
	
	@XmlElement(name = MAX_BUCKETS)
	public Integer getMaxBuckets() {
		return maxBuckets;
	}
	public void setMaxBuckets(Integer maxBuckets) {
		this.maxBuckets = maxBuckets;
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
	
	@XmlElement(name = NEXT_PAGE_LINK)
	public Long getNextPageLink() {
		return nextPageLink;
	}
	public void setNextPageLink(Long nextPageLink) {
		this.nextPageLink = nextPageLink;
	}	
	



}



