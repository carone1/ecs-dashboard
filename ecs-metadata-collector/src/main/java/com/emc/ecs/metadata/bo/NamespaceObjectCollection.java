package com.emc.ecs.metadata.bo;


import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionException;

import com.emc.object.s3.bean.Bucket;
import com.emc.object.s3.bean.ListBucketsResult;
import com.emc.object.s3.request.ListBucketsRequest;




public class NamespaceObjectCollection implements Callable<String> {

	
	//private final static int MAX_THREADS = 10;
	
	ObjectCollectionConfig collectionConfig;
	//ThreadPoolExecutor 	   executorThreadPoolExecutor;

	
	//===========================
	// Public methods
	//===========================
	public NamespaceObjectCollection( ObjectCollectionConfig collectionConfig ) {
		
		this.collectionConfig = collectionConfig;
		//this.executorThreadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_THREADS);;
		
	}
	
	
	@Override
	public String call() throws Exception {
		collectObjects();
		return "ok";
	}
	
	private void collectObjects() {
		
		// First bucket batch
		ListBucketsResult listBucketsResult = this.collectionConfig.getS3JerseyClient().listBuckets();			
		collectObjectsPerBucketBatch( listBucketsResult.getBuckets() );			

		// subsequent bucket batch
		while( listBucketsResult.isTruncated() ) {	
			
			ListBucketsRequest listBucketRequest = new ListBucketsRequest();
			listBucketRequest.setMarker(listBucketsResult.getMarker());
			listBucketRequest.setNamespace(this.collectionConfig.getNamespace());
								
			listBucketsResult = this.collectionConfig.getS3JerseyClient().listBuckets(listBucketRequest);
			collectObjectsPerBucketBatch( listBucketsResult.getBuckets() );				
		}
		
//		// take everything down once all thread have completed their work
//		executorThreadPoolExecutor.shutdown();
//		
//		// wait for all threads to terminate
//		boolean termination = false; 
//		do {
//			try {
//				termination = executorThreadPoolExecutor.awaitTermination(1, TimeUnit.MINUTES);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} while(!termination);
		
	}
	
	
	private void collectObjectsPerBucketBatch( List<Bucket> bucketList ) {

		for( Bucket bucket : bucketList ) {
			
			BucketObjectCollection bucketObjectCollection = 
					new BucketObjectCollection( collectionConfig, bucket );
			
			// submit bucket collection to thread pool
			try {
				ObjectBO.getFutures().add(ObjectBO.getThreadPool().submit(bucketObjectCollection));
			} catch (RejectedExecutionException e) {
				// Thread pool didn't accept bucket collection
				// running in the current thread
				System.err.println("Thread pool didn't accept bucket collection - running in current thread");
				try {
					bucketObjectCollection.call();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
				
		}
	}




}
