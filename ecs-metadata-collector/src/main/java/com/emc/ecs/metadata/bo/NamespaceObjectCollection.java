package com.emc.ecs.metadata.bo;


import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.emc.ecs.metadata.dao.ObjectDAO;
import com.emc.object.s3.bean.Bucket;
import com.emc.object.s3.bean.ListBucketsResult;
import com.emc.object.s3.jersey.S3JerseyClient;
import com.emc.object.s3.request.ListBucketsRequest;




public class NamespaceObjectCollection implements Runnable {

	
	private final static int MAX_THREADS = 10;
	
	private String         namespace;
	private S3JerseyClient s3JerseyClient;
	private ObjectDAO      objectDAO;
	private Date           collectionTime;
	private AtomicLong     objectCount;
	ThreadPoolExecutor 	   executorThreadPoolExecutor;

	
	//===========================
	// Public methods
	//===========================
	public NamespaceObjectCollection( S3JerseyClient s3JerseyClient, 
									  String             namespace, 
									  ObjectDAO          objectDAO, 
									  Date               collectionTime,
									  AtomicLong         objectCount     ) {
		
		this.s3JerseyClient 			= s3JerseyClient;
		this.namespace      			= namespace;
		this.objectDAO      			= objectDAO;
		this.collectionTime 			= collectionTime;
		this.objectCount    		    = objectCount;
		this.executorThreadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_THREADS);;
		
	}
	
	
	@Override
	public void run() {
		collectObjects();
	}
	
	private void collectObjects() {
		
		// First bucket batch
		ListBucketsResult listBucketsResult = s3JerseyClient.listBuckets();			
		collectObjectsPerBucketBatch( listBucketsResult.getBuckets() );			

		// subsequent bucket batch
		while( listBucketsResult.isTruncated() ) {	
			
			ListBucketsRequest listBucketRequest = new ListBucketsRequest();
			listBucketRequest.setMarker(listBucketsResult.getMarker());
			listBucketRequest.setNamespace(namespace);
								
			listBucketsResult = s3JerseyClient.listBuckets(listBucketRequest);
			collectObjectsPerBucketBatch( listBucketsResult.getBuckets() );				
		}
		
		// take everything down once all thread have completed their work
		executorThreadPoolExecutor.shutdown();
		
		// wait for all threads to terminate
		boolean termination = false; 
		do {
			try {
				termination = executorThreadPoolExecutor.awaitTermination(1, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while(!termination);
		
	}
	
	
	private void collectObjectsPerBucketBatch( List<Bucket> bucketList ) {

		for( Bucket bucket : bucketList ) {
			
			BucketObjectCollection bucketObjectCollection = 
					new BucketObjectCollection( s3JerseyClient, namespace, bucket, this.objectDAO, 
												collectionTime, objectCount );
			
			// submit bucket collection to thread pool
			executorThreadPoolExecutor.execute(bucketObjectCollection);
			
			//collectObjectsPerBucket(bucket);
		}
	}

	
//	private void collectObjectsPerBucket( Bucket bucket ) {
//
//		// Collect all objects in that bucket 
//		System.out.println("Collecting object for bucket: " + bucket.getName() );
//
//		ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucket.getName());
//		listObjectsRequest.setMaxKeys(maxObjectPerRequest);
//		listObjectsRequest.setNamespace(namespace);
//
//		long startTime = System.currentTimeMillis();
//		ListObjectsResult listObjectsResult = s3JerseyClient.listObjects(listObjectsRequest);
//		long stopTime = System.currentTimeMillis();
//		Double elapsedTime = Double.valueOf(stopTime - startTime) / 1000;
//
//		if(listObjectsResult != null) {
//			Long collected = (long)listObjectsResult.getObjects().size();
//			this.objectCount.getAndAdd(collected);
//			System.out.println("Took: " + elapsedTime + " seconds to collect " + collected + " objects");
//
//			if(this.objectDAO != null) {					
//				objectDAO.insert( listObjectsResult, 
//									namespace, bucket.getName(),
//									collectionTime );
//			}
//
//			while(listObjectsResult.isTruncated()) {
//				
//				ListObjectsRequest moreListObjectsRequest = new ListObjectsRequest(bucket.getName());
//				moreListObjectsRequest.setMaxKeys(maxObjectPerRequest);
//				moreListObjectsRequest.setNamespace(namespace);
//				moreListObjectsRequest.setMarker(listObjectsResult.getNextMarker());
//
//				startTime = System.currentTimeMillis();
//				listObjectsResult = s3JerseyClient.listObjects(moreListObjectsRequest);
//				stopTime = System.currentTimeMillis();
//				
//				elapsedTime = Double.valueOf(stopTime - startTime) / 1000;
//
//				collected = (long)listObjectsResult.getObjects().size();
//				this.objectCount.getAndAdd(collected);
//
//				System.out.println("Took: " + elapsedTime + " seconds to collect " + 
//						collected + " objects from bucket: " + bucket.getName());
//
//				if(this.objectDAO != null) {
//					objectDAO.insert( listObjectsResult,
//							namespace,
//							bucket.getName(),
//							collectionTime   );
//				}
//			}				
//		}		
//
//	}
}
