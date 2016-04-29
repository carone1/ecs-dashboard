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



