package com.emc.ecs.metadata.bo;


import java.util.concurrent.Callable;

import com.emc.object.s3.bean.Bucket;
import com.emc.object.s3.bean.ListObjectsResult;
import com.emc.object.s3.request.ListObjectsRequest;




public class BucketObjectCollection implements Callable<String> {

	
	private static final Integer maxObjectPerRequest = 10000;
	
	private ObjectCollectionConfig collectionConfig;
	private Bucket         bucket;

	
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
	
	private void collectObjectsPerBucket( ) {

		// Collect all objects in that bucket 
		System.out.println("Collecting object for bucket: " + bucket.getName() );

		ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucket.getName());
		listObjectsRequest.setMaxKeys(maxObjectPerRequest);
		listObjectsRequest.setNamespace(this.collectionConfig.getNamespace());
		
		long startTime = System.currentTimeMillis();
		
		// collect objects
		ListObjectsResult listObjectsResult = this.collectionConfig.getS3JerseyClient().listObjects(listObjectsRequest);
		
		//QueryObjectsRequest queryRequest = new QueryObjectsRequest(this.bucket.getName());
		//QueryObjectsResult queryResult = this.collectionConfig.getS3JerseyClient().queryObjects(queryRequest);
		
		long stopTime = System.currentTimeMillis();
		Double elapsedTime = Double.valueOf(stopTime - startTime) / 1000;

		if(listObjectsResult != null) {
			
			Long collected = (long)listObjectsResult.getObjects().size();
			
			this.collectionConfig.getObjectCount().getAndAdd(collected);
			
			System.out.println("Took: " + elapsedTime + " seconds to collect " +
								collected + " objects from namespace: " + 
								this.collectionConfig.getNamespace() + " bucket: " + bucket.getName());

			if(this.collectionConfig.getObjectDAO() != null) {					
				this.collectionConfig.getObjectDAO().insert( listObjectsResult, 
															 this.collectionConfig.getNamespace(),
															 bucket.getName(), 
															 this.collectionConfig.getCollectionTime() );
			}

			while(listObjectsResult.isTruncated()) {
				
				ListObjectsRequest moreListObjectsRequest = new ListObjectsRequest(bucket.getName());
				moreListObjectsRequest.setMaxKeys(maxObjectPerRequest);
				moreListObjectsRequest.setNamespace(this.collectionConfig.getNamespace());
				moreListObjectsRequest.setMarker(listObjectsResult.getNextMarker());

				startTime = System.currentTimeMillis();
				listObjectsResult = this.collectionConfig.getS3JerseyClient().listObjects(moreListObjectsRequest);
				stopTime = System.currentTimeMillis();
				
				elapsedTime = Double.valueOf(stopTime - startTime) / 1000;

				collected = (long)listObjectsResult.getObjects().size();
				this.collectionConfig.getObjectCount().getAndAdd(collected);

				System.out.println("Took: " + elapsedTime + " seconds to collect " +
						           collected + " objects from namespace: " + 
						           this.collectionConfig.getNamespace() + " bucket: " + bucket.getName());

				if(this.collectionConfig.getObjectDAO() != null) {
					this.collectionConfig.getObjectDAO().insert( listObjectsResult, 
																this.collectionConfig.getNamespace(), 
																bucket.getName(), 
																this.collectionConfig.getCollectionTime() );
				}
			}				
		}		

	}



}
