package com.emc.ecs.management.entity;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



@XmlRootElement(name = "bucket_billing_info")
@XmlType(propOrder = {"name", "namespace", "vpoolId", "totalSize", "totalSizeUnit", "totalObjects", "sampleTime", "tagSet"})
public class BucketBillingInfo {
	
	private String name;
	private String namespace;
	private String vpoolId;
	private Long totalSize;
	private String totalSizeUnit;
	private Long totalObjects;
	private String sampleTime;
	private List<Tag> tagSet;
	
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name = "namespace")
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	@XmlElement(name = "vpool_id")
	public String getVpoolId() {
		return vpoolId;
	}
	public void setVpoolId(String vpoolId) {
		this.vpoolId = vpoolId;
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
	
	@XmlElement(name = "sample_time")
	public String getSampleTime() {
		return sampleTime;
	}
	public void setSampleTime(String sampleTime) {
		this.sampleTime = sampleTime;
	}
	
	@XmlElementWrapper(name = "TagSet")
	@XmlElement(name = "Tag")
	public List<Tag> getTagSet() {
		return tagSet;
	}
	public void setTagSet(List<Tag> tagSet) {
		this.tagSet = tagSet;
	}
	

}



