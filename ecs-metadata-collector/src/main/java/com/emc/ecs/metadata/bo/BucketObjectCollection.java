package com.emc.ecs.metadata.bo;


import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionException;

import com.emc.ecs.management.entity.ObjectBucket;
import com.emc.object.s3.bean.Bucket;
import com.emc.object.s3.bean.ListObjectsResult;
import com.emc.object.s3.request.ListObjectsRequest;




public class BucketObjectCollection implements Callable<String> {

	
	private static final Integer maxObjectPerRequest = 10000;
	
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
		System.out.println("Collecting object for bucket: " + bucket.getName() );

		ObjectBucket objectBucket = getObjectBucket();
		
		// check whether bucket has search keys configured
		if(   objectBucket != null && 
			  objectBucket.getSearchMetadata() != null &&
			! objectBucket.getSearchMetadata().isEmpty()  ) {
			
			// Bucket has search keys
			// need to collect those values
			collectObjectSearchKeys(objectBucket);
		}	
		
		// prepare request object
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucket.getName());
		listObjectsRequest.setMaxKeys(maxObjectPerRequest);
		listObjectsRequest.setNamespace(this.collectionConfig.getNamespace());
		
		long startTime = System.currentTimeMillis();
		
		// collect objects
		ListObjectsResult listObjectsResult = this.collectionConfig.getS3JerseyClient().listObjects(listObjectsRequest);
		
		long stopTime = System.currentTimeMillis();
		Double elapsedTime = Double.valueOf(stopTime - startTime) / 1000;
					
		if(listObjectsResult != null) {
			
			Long collected = (long)listObjectsResult.getObjects().size();
			
			this.collectionConfig.getObjectCount().getAndAdd(collected);
			
			System.out.println("Took: " + elapsedTime + " seconds to collect " +
								collected + " objects from namespace: " + 
								this.collectionConfig.getNamespace() + " bucket: " + bucket.getName());
			
			// add collected entries into datastore
			if(this.collectionConfig.getObjectDAO() != null) {					
				this.collectionConfig.getObjectDAO().insert( listObjectsResult, 
														 	 this.collectionConfig.getNamespace(),
														 	 bucket.getName(), 
														 	 this.collectionConfig.getCollectionTime() );
			}
			
			while(listObjectsResult.isTruncated()) {
				
				listObjectsRequest.setMarker(listObjectsResult.getNextMarker());

				startTime = System.currentTimeMillis();
				listObjectsResult = this.collectionConfig.getS3JerseyClient().listObjects(listObjectsRequest);
				stopTime = System.currentTimeMillis();
				
				elapsedTime = Double.valueOf(stopTime - startTime) / 1000;

				collected = (long)listObjectsResult.getObjects().size();
				this.collectionConfig.getObjectCount().getAndAdd(collected);

				System.out.println("Took: " + elapsedTime + " seconds to collect " +
						           collected + " objects from namespace: " + 
						           this.collectionConfig.getNamespace() + " bucket: " + bucket.getName());

				// add collected entries into datastore
				if(this.collectionConfig.getObjectDAO() != null) {					
					this.collectionConfig.getObjectDAO().insert( listObjectsResult, 
															 	 this.collectionConfig.getNamespace(),
															 	 bucket.getName(), 
															 	 this.collectionConfig.getCollectionTime() );
				}
				
			}				
		}		

	}

	private void collectObjectSearchKeys( ObjectBucket objectBucket) {
		
		ObjectsKeysCollection objectsKeysCollection = 
				new ObjectsKeysCollection( collectionConfig, objectBucket );
		
		// submit bucket objects keys collection to thread pool
		try {
			ObjectBO.getFutures().add(ObjectBO.getThreadPool().submit(objectsKeysCollection));
		} catch (RejectedExecutionException e) {
			// Thread pool didn't accept bucket collection
			// running in the current thread
			System.err.println("Thread pool didn't accept bucket object key collection - running in current thread");
			try {
				objectsKeysCollection.call();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}

}
