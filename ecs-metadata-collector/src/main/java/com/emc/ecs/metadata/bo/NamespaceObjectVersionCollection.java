/*
 * Copyright (c) 2016, EMC Corporation.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *     + Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     + Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     + The name of EMC Corporation may not be used to endorse or promote
 *       products derived from this software without specific prior written
 *       permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
				collectionConfig.getFutures().add(collectionConfig.getThreadPoolExecutor().submit(bucketObjectVersionCollection));
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
