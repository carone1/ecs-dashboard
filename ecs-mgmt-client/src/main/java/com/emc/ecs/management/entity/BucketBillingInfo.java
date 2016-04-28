package com.emc.ecs.management.entity;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



@XmlRootElement(name = "bucket_billing_info")
@XmlType(propOrder = {"name", "namespace", "vpoolId", "totalSize", "totalSizeUnit", "totalObjects", "sampleTime", "tagSet"})
public class BucketBillingInfo {
	
	public static final String NAME_TAG = "name"; 
	public static final String NAMESPACE_TAG = "namespace";
	public static final String VPOOL_ID_TAG = "vpool_id";
	public static final String TOTAL_SIZE_TAG = "total_size";
	public static final String TOTAL_SIZE_UNIT_TAG = "total_size_unit";
	public static final String TOTAL_OBJECTS_TAG = "total_objects";
	public static final String SAMPLE_TIME_TAG = "sample_time"; 	
	public static final String TAG_SET_TAG = "TagSet";
	public static final String TAG_TAG = "Tag";
	
	private String name;
	private String namespace;
	private String vpoolId;
	private Long totalSize;
	private String totalSizeUnit;
	private Long totalObjects;
	private String sampleTime;
	private List<Tag> tagSet;
	
	@XmlElement(name = NAME_TAG)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name = NAMESPACE_TAG)
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	@XmlElement(name = VPOOL_ID_TAG)
	public String getVpoolId() {
		return vpoolId;
	}
	public void setVpoolId(String vpoolId) {
		this.vpoolId = vpoolId;
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
	
	@XmlElement(name = SAMPLE_TIME_TAG)
	public String getSampleTime() {
		return sampleTime;
	}
	public void setSampleTime(String sampleTime) {
		this.sampleTime = sampleTime;
	}
	
	@XmlElementWrapper(name = TAG_SET_TAG)
	@XmlElement(name = TAG_TAG)
	public List<Tag> getTagSet() {
		return tagSet;
	}
	public void setTagSet(List<Tag> tagSet) {
		this.tagSet = tagSet;
	}
	

}



