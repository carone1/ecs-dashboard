package com.emc.ecs.metadata.bo;


import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.object.s3.bean.Bucket;
import com.emc.object.s3.bean.ListBucketsResult;
import com.emc.object.s3.request.ListBucketsRequest;




public class NamespaceObjectVersionCollection implements Callable<String> {
	
	//===========================
	// Private members
	//===========================
	private final static Logger                 logger = LoggerFactory.getLogger(NamespaceObjectVersionCollection.class);
	private              ObjectCollectionConfig collectionConfig;

	
	//===========================
	// Public methods
	//===========================
	public NamespaceObjectVersionCollection( ObjectCollectionConfig collectionConfig ) {
		
		this.collectionConfig = collectionConfig;
	}
	
	
	@Override
	public String call() throws Exception {
		collectObjectsVersions();
		return "ok";
	}
	
	private void collectObjectsVersions() {
		
		// First bucket batch
		ListBucketsResult listBucketsResult = this.collectionConfig.getS3JerseyClient().listBuckets();			
		collectObjectsVersionsPerBucketBatch( listBucketsResult.getBuckets() );			

		// subsequent bucket batch
		while( listBucketsResult.isTruncated() ) {	
			
			ListBucketsRequest listBucketRequest = new ListBucketsRequest();
			listBucketRequest.setMarker(listBucketsResult.getMarker());
			listBucketRequest.setNamespace(this.collectionConfig.getNamespace());
								
			listBucketsResult = this.collectionConfig.getS3JerseyClient().listBuckets(listBucketRequest);
			collectObjectsVersionsPerBucketBatch( listBucketsResult.getBuckets() );				
		}
	}
	
	
	private void collectObjectsVersionsPerBucketBatch( List<Bucket> bucketList ) {

		for( Bucket bucket : bucketList ) {
			
			BucketObjectVersionCollection bucketObjectVersionCollection = 
					new BucketObjectVersionCollection( collectionConfig, bucket );
			
			// submit bucket collection to thread pool
			try {
				ObjectBO.getFutures().add(ObjectBO.getThreadPool().submit(bucketObjectVersionCollection));
			} catch (RejectedExecutionException e) {
				// Thread pool didn't accept bucket collection
				// running in the current thread
				logger.error("Thread pool didn't accept bucket collection - running in current thread");
				try {
					bucketObjectVersionCollection.call();
				} catch (Exception e1) {
					logger.error("Error occured during bucket object version collection operation - message: " +
				                 e.getLocalizedMessage());
				}
			}	
		}
	}




}
