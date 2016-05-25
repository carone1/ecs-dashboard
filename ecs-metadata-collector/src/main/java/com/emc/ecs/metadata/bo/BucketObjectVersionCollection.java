package com.emc.ecs.metadata.bo;


import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.ecs.management.entity.ObjectBucket;
import com.emc.object.s3.bean.Bucket;


public class BucketObjectVersionCollection implements Callable<String> {

	
	final static Logger logger = LoggerFactory.getLogger(BucketObjectVersionCollection.class);
	
	private ObjectCollectionConfig collectionConfig;
	private Bucket                 bucket;

	
	//===========================
	// Public methods
	//===========================
	public BucketObjectVersionCollection( ObjectCollectionConfig collectionConfig, 
								          Bucket         bucket                   ) {
		
		this.collectionConfig = collectionConfig;
		this.bucket           = bucket;
	}
	
	
	@Override
	public String call() throws Exception {
		collectObjectsVersionsPerBucket();
		return "ok";
	}
	
	private ObjectBucket getObjectBucket() {
		NamespaceBucketKey bucketKey = new NamespaceBucketKey(collectionConfig.getNamespace(), bucket.getName());
		return collectionConfig.getBucketMap().get(bucketKey);
	}
	
	private void collectObjectsVersionsPerBucket( ) {

		// Collect all objects in that bucket 
		logger.info("Collecting object version for bucket: " + bucket.getName());
		

		ObjectBucket objectBucket = getObjectBucket();
		
		if(objectBucket != null) {
			listObjectsVersions(objectBucket);
		}	
	}

	
	private void listObjectsVersions( ObjectBucket objectBucket ) {
		
		ListObjectsVersionsCollection listObjectsVersionsCollection = 
				new ListObjectsVersionsCollection( collectionConfig, objectBucket );
		
		listObjectsVersionsCollection.collectObjectVersions();
	}
}
