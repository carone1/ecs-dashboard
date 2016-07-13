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
import javax.xml.bind.annotation.XmlType;



@XmlRootElement(name = "namespace_billing_info")
@XmlType(propOrder = {"bucketBillingInfo", "nextMarker", "totalSize", "totalSizeUnit", "totalObjects", "namespace", "sampleTime"})
public class NamespaceBillingInfo {
	
	public static final String BUCKET_BILLING_INFO_TAG = "bucket_billing_info";
	public static final String NEXT_MARKER_TAG         = "next_marker";
	public static final String TOTAL_SIZE_TAG          = "total_size";
	public static final String TOTAL_SIZE_UNIT_TAG     = "total_size_unit";
	public static final String TOTAL_OBJECTS_TAG       = "total_objects";
	public static final String NAMESPACE_TAG           = "namespace";
	public static final String SAMPLE_TIME_TAG         = "sample_time";
	
	
	private List<BucketBillingInfo> bucketBillingInfo = new ArrayList<BucketBillingInfo>();
	private String                  nextMarker;
	private Long                    totalSize;
	private String                  totalSizeUnit;
	private Long 					totalObjects;
	private String 					namespace;
	private String					sampleTime;
	
	 @XmlElement(name = BUCKET_BILLING_INFO_TAG)
	public List<BucketBillingInfo> getBucketBillingInfo() {
		return bucketBillingInfo;
	}
	public void setBucketBillingInfo(List<BucketBillingInfo> bucketBillingInfo) {
		this.bucketBillingInfo = bucketBillingInfo;
	}

	@XmlElement(name = NEXT_MARKER_TAG)
	public String getNextMarker() {
		return nextMarker;
	}
	public void setNextMarker(String nextMarker) {
		this.nextMarker = nextMarker;
	}

	@XmlElement(name = TOTAL_SIZE_TAG)
	public Long getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(Long totalSize) {
		this.totalSize = totalSize;
	}

	@XmlElement(name = TOTAL_SIZE_UNIT_TAG)
	public String getTotalSizeUnit() {
		return totalSizeUnit;
	}
	public void setTotalSizeUnit(String totalSizeUnit) {
		this.totalSizeUnit = totalSizeUnit;
	}
	
	@XmlElement(name = TOTAL_OBJECTS_TAG)
	public Long getTotalObjects() {
		return totalObjects;
	}
	public void setTotalObjects(Long totalObjects) {
		this.totalObjects = totalObjects;
	}
	
	@XmlElement(name = NAMESPACE_TAG)
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	@XmlElement(name = SAMPLE_TIME_TAG)
	public String getSampleTime() {
		return sampleTime;
	}
	public void setSampleTime(String sampleTime) {
		this.sampleTime = sampleTime;
	}
		

}



