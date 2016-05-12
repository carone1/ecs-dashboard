package com.emc.ecs.metadata.bo;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.emc.ecs.management.entity.ObjectBucket;
import com.emc.ecs.metadata.dao.ObjectDAO;
import com.emc.object.s3.jersey.S3JerseyClient;

public class ObjectCollectionConfig {

	//========================
	// Private members
	//========================
	private String                                namespace;
	private S3JerseyClient                        s3JerseyClient;
	private ObjectDAO                             objectDAO;
	private Date                                  collectionTime;
	private AtomicLong                            objectCount;
	private Map<NamespaceBucketKey, ObjectBucket> bucketMap;
	
	


	public ObjectCollectionConfig( S3JerseyClient                        s3JerseyClient, 
								   String                                namespace, 
								   ObjectDAO                             objectDAO, 
								   Map<NamespaceBucketKey, ObjectBucket> bucketMap,
								   Date                                  collectionTime,
								   AtomicLong                            objectCount     ) {

		this.s3JerseyClient = s3JerseyClient;
		this.namespace      = namespace;
		this.bucketMap      = bucketMap;
		this.objectDAO      = objectDAO;
		this.collectionTime = collectionTime;
		this.objectCount    = objectCount;
	}
	
	//=======================
	// Public methods
	//=======================
	
	public String getNamespace() {
		return namespace;
	}


	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}


	public S3JerseyClient getS3JerseyClient() {
		return s3JerseyClient;
	}


	public void setS3JerseyClient(S3JerseyClient s3JerseyClient) {
		this.s3JerseyClient = s3JerseyClient;
	}


	public ObjectDAO getObjectDAO() {
		return objectDAO;
	}


	public void setObjectDAO(ObjectDAO objectDAO) {
		this.objectDAO = objectDAO;
	}

	public Map<NamespaceBucketKey, ObjectBucket> getBucketMap() {
		return bucketMap;
	}

	public void setBucketMap(Map<NamespaceBucketKey, ObjectBucket> bucketMap) {
		this.bucketMap = bucketMap;
	}

	public Date getCollectionTime() {
		return collectionTime;
	}


	public void setCollectionTime(Date collectionTime) {
		this.collectionTime = collectionTime;
	}


	public AtomicLong getObjectCount() {
		return objectCount;
	}


	public void setObjectCount(AtomicLong objectCount) {
		this.objectCount = objectCount;
	}

	
}
