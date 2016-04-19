package com.emc.ecs.management.entity;


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



@XmlRootElement(name = "namespace_billing_info")
@XmlType(propOrder = {"bucketBillingInfo", "nextMarker", "totalSize", "totalSizeUnit", "totalObjects", "namespace", "sampleTime"})
public class NamespaceBillingInfo {
	
	private List<BucketBillingInfo> bucketBillingInfo = new ArrayList<BucketBillingInfo>();
	private String                  nextMarker;
	private Long                    totalSize;
	private String                  totalSizeUnit;
	private Long 					totalObjects;
	private String 					namespace;
	private String					sampleTime;
	
	 @XmlElement(name = "bucket_billing_info")
	public List<BucketBillingInfo> getBucketBillingInfo() {
		return bucketBillingInfo;
	}
	public void setBucketBillingInfo(List<BucketBillingInfo> bucketBillingInfo) {
		this.bucketBillingInfo = bucketBillingInfo;
	}

	@XmlElement(name = "next_marker")
	public String getNextMarker() {
		return nextMarker;
	}
	public void setNextMarker(String nextMarker) {
		this.nextMarker = nextMarker;
	}

	@XmlElement(name = "total_size")
	public Long getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(Long totalSize) {
		this.totalSize = totalSize;
	}

	@XmlElement(name = "total_size_unit")
	public String getTotalSizeUnit() {
		return totalSizeUnit;
	}
	public void setTotalSizeUnit(String totalSizeUnit) {
		this.totalSizeUnit = totalSizeUnit;
	}
	
	@XmlElement(name = "total_objects")
	public Long getTotalObjects() {
		return totalObjects;
	}
	public void setTotalObjects(Long totalObjects) {
		this.totalObjects = totalObjects;
	}
	
	@XmlElement(name = "namespace")
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	@XmlElement(name = "sample_time")
	public String getSampleTime() {
		return sampleTime;
	}
	public void setSampleTime(String sampleTime) {
		this.sampleTime = sampleTime;
	}
		

}



