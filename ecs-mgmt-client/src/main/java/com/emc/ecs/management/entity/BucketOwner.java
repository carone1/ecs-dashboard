/**
 * 
 */
package com.emc.ecs.management.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author nlengc
 *
 */
@XmlRootElement(name = "bucket_owner")
public class BucketOwner {
	
	public final static String VDC_ID = "vdcId";
	public final static String BUCKET_KEY = "bucketKey";
	
	private String vdcId;
	private String bucketKey;
	
	public BucketOwner(String vdcId, String bucketKey) {
		super();
		this.vdcId = vdcId;
		this.bucketKey = bucketKey;
	}
	
	@XmlElement(name = VDC_ID)
	public String getVdcId() {
		return vdcId;
	}
	public void setVdcId(String vdcId) {
		this.vdcId = vdcId;
	}
	
	@XmlElement(name = BUCKET_KEY)
	public String getBucketKey() {
		return bucketKey;
	}
	public void setBucketKey(String bucketKey) {
		this.bucketKey = bucketKey;
	}

}
