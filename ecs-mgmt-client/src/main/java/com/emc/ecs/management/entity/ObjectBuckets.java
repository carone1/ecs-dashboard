package com.emc.ecs.management.entity;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



@XmlRootElement(name = "object_buckets")
@XmlType(propOrder = {"objectBucket", "maxBuckets", "nextMarker", "filter", "nextPageLink" })

public class ObjectBuckets {
	
	private List<ObjectBucket> objectBucketList;
	private Integer 			maxBuckets;
	private String 				nextMarker;
	private String 				filter;
	private Long 				nextPageLink;
	
	@XmlElement(name = "object_bucket")
	public List<ObjectBucket> getObjectBucket() {
		return objectBucketList;
	}
	public void setObjectBucket(List<ObjectBucket> objectBucket) {
		this.objectBucketList = objectBucket;
	}
	
	@XmlElement(name = "MaxBuckets")
	public Integer getMaxBuckets() {
		return maxBuckets;
	}
	public void setMaxBuckets(Integer maxBuckets) {
		this.maxBuckets = maxBuckets;
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
	public Long getNextPageLink() {
		return nextPageLink;
	}
	public void setNextPageLink(Long nextPageLink) {
		this.nextPageLink = nextPageLink;
	}	
	



}



