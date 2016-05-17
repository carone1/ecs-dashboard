package com.emc.ecs.metadata.bo;


import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.ecs.management.entity.ObjectBucket;
import com.emc.object.s3.bean.Bucket;


public class BucketObjectCollection implements Callable<String> {

	
	final static Logger logger = LoggerFactory.getLogger(BucketObjectCollection.class);
	
	private ObjectCollectionConfig collectionConfig;
	private Bucket                 bucket;

	
	//===========================
	// Public methods
	//===========================
	public BucketObjectCollection( ObjectCollectionConfig collectionConfig, 
								   Bucket         bucket                   ) {
		
		this.collectionConfig = collectionConfig;
		this.bucket           = bucket;
	}
	
	
	@Override
	public String call() throws Exception {
		collectObjectsPerBucket();
		return "ok";
	}
	
	private ObjectBucket getObjectBucket() {
		NamespaceBucketKey bucketKey = new NamespaceBucketKey(collectionConfig.getNamespace(), bucket.getName());
		return collectionConfig.getBucketMap().get(bucketKey);
	}
	
	private void collectObjectsPerBucket( ) {

		// Collect all objects in that bucket 
		logger.info("Collecting object for bucket: " + bucket.getName());
		

		ObjectBucket objectBucket = getObjectBucket();
		
		// check whether bucket has search keys configured
		if(   objectBucket != null && 
			  objectBucket.getSearchMetadata() != null &&
			! objectBucket.getSearchMetadata().isEmpty()  ) {
			
			// Bucket has search keys
			// need to collect those values
			if( !queryObjects(objectBucket) ) {
				// Something went wonky during the query operation
				// revert to list object call
				listObjects(objectBucket);
			}		
		} else {
			listObjects(objectBucket);
		}
	}

	private boolean queryObjects( ObjectBucket objectBucket) {
		
		QueryObjectsCollection queryObjectsCollection = 
				new QueryObjectsCollection( collectionConfig, objectBucket );
			
		return queryObjectsCollection.queryObjects();
	}

	private void listObjects( ObjectBucket objectBucket ) {
		
//		ListObjectsCollection queryObjectsCollection = 
//				new ListObjectsCollection( collectionConfig, objectBucket );
//		
//		queryObjectsCollection.collectObjectKeys();
	}
}
