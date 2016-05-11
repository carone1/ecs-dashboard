package com.emc.ecs.metadata.bo;


import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import com.emc.ecs.metadata.dao.ObjectDAO;
import com.emc.object.s3.bean.Bucket;

import com.emc.object.s3.bean.ListObjectsResult;
import com.emc.object.s3.jersey.S3JerseyClient;
import com.emc.object.s3.request.ListObjectsRequest;



public class BucketObjectCollection implements Runnable {

	
	private static final Integer maxObjectPerRequest = 10000;
	
	private String         namespace;
	private S3JerseyClient s3JerseyClient;
	private ObjectDAO      objectDAO;
	private Date           collectionTime;
	private AtomicLong     objectCount;
	private Bucket         bucket;

	
	//===========================
	// Public methods
	//===========================
	public BucketObjectCollection( S3JerseyClient s3JerseyClient, 
								   String 		  namespace, 
								   Bucket         bucket,
								   ObjectDAO      objectDAO, 
								   Date           collectionTime,
								   AtomicLong     objectCount     ) {
		
		this.s3JerseyClient = s3JerseyClient;
		this.namespace      = namespace;
		this.bucket         = bucket;
		this.objectDAO      = objectDAO;
		this.collectionTime = collectionTime;
		this.objectCount    = objectCount;
		
	}
	
	
	@Override
	public void run() {
		collectObjectsPerBucket();
	}
	
	private void collectObjectsPerBucket( ) {

		// Collect all objects in that bucket 
		System.out.println("Collecting object for bucket: " + bucket.getName() );

		ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucket.getName());
		listObjectsRequest.setMaxKeys(maxObjectPerRequest);
		listObjectsRequest.setNamespace(namespace);

		long startTime = System.currentTimeMillis();
		ListObjectsResult listObjectsResult = s3JerseyClient.listObjects(listObjectsRequest);
		long stopTime = System.currentTimeMillis();
		Double elapsedTime = Double.valueOf(stopTime - startTime) / 1000;

		if(listObjectsResult != null) {
			
			Long collected = (long)listObjectsResult.getObjects().size();
			this.objectCount.getAndAdd(collected);
			
			System.out.println("Took: " + elapsedTime + " seconds to collect " +
								collected + " objects from namespace: " + namespace + " bucket: " + bucket.getName());

			if(this.objectDAO != null) {					
				objectDAO.insert( listObjectsResult, namespace, bucket.getName(), collectionTime );
			}

			while(listObjectsResult.isTruncated()) {
				
				ListObjectsRequest moreListObjectsRequest = new ListObjectsRequest(bucket.getName());
				moreListObjectsRequest.setMaxKeys(maxObjectPerRequest);
				moreListObjectsRequest.setNamespace(namespace);
				moreListObjectsRequest.setMarker(listObjectsResult.getNextMarker());

				startTime = System.currentTimeMillis();
				listObjectsResult = s3JerseyClient.listObjects(moreListObjectsRequest);
				stopTime = System.currentTimeMillis();
				
				elapsedTime = Double.valueOf(stopTime - startTime) / 1000;

				collected = (long)listObjectsResult.getObjects().size();
				this.objectCount.getAndAdd(collected);

				System.out.println("Took: " + elapsedTime + " seconds to collect " +
						           collected + " objects from namespace: " + namespace + " bucket: " + bucket.getName());

				if(this.objectDAO != null) {
					objectDAO.insert( listObjectsResult, namespace, bucket.getName(), collectionTime );
				}
			}				
		}		

	}
}
