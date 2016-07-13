/*

The MIT License (MIT)

Copyright (c) 2016 EMC Corporation

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package com.emc.ecs.metadata.bo;


import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.object.s3.bean.Bucket;
import com.emc.object.s3.bean.ListBucketsResult;
import com.emc.object.s3.request.ListBucketsRequest;




public class NamespaceObjectCollection implements Callable<String> {
	
	//===========================
	// Private members
	//===========================
	private final static Logger                 logger = LoggerFactory.getLogger(NamespaceObjectCollection.class);
	private              ObjectCollectionConfig collectionConfig;

	
	//===========================
	// Public methods
	//===========================
	public NamespaceObjectCollection( ObjectCollectionConfig collectionConfig ) {
		
		this.collectionConfig = collectionConfig;
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
	}
	
	
	private void collectObjectsPerBucketBatch( List<Bucket> bucketList ) {

		for( Bucket bucket : bucketList ) {
			
			BucketObjectCollection bucketObjectCollection = 
					new BucketObjectCollection( collectionConfig, bucket );
			
			// submit bucket collection to thread pool
			try {
				collectionConfig.getFutures().add(collectionConfig.getThreadPoolExecutor().submit(bucketObjectCollection));
			} catch (RejectedExecutionException e) {
				// Thread pool didn't accept bucket collection
				// running in the current thread
				logger.error("Thread pool didn't accept bucket collection - running in current thread");
				try {
					bucketObjectCollection.call();
				} catch (Exception e1) {
					logger.error("Error occured during bucket object collection operation - message: " + e.getLocalizedMessage());
				}
			}	
		}
	}




}
