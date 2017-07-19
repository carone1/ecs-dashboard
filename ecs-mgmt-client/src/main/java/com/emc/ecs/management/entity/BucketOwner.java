/**
 * 
 */
package com.emc.ecs.management.entity;

/**
 * @author nlengc
 *
 */
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
	
	public String getVdcId() {
		return vdcId;
	}
	public void setVdcId(String vdcId) {
		this.vdcId = vdcId;
	}
	public String getBucketKey() {
		return bucketKey;
	}
	public void setBucketKey(String bucketKey) {
		this.bucketKey = bucketKey;
	}
	
	

}
