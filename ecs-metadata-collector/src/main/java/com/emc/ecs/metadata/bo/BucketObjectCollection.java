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
		
		// Check if a search criteria was specified
		if(collectionConfig.getQueryCriteria() != null) {
			queryObjects( objectBucket ); 
		} else {
			// normal case where we list object or query them if they
			// have any MD keys configured against them
			
			// check whether bucket has search keys configured
			if(   objectBucket != null && 
					objectBucket.getSearchMetadata() != null &&
					! objectBucket.getSearchMetadata().isEmpty()  ) {

				// Bucket has search MD keys configured
				// need to query those objects for that bucket
				if( !queryObjects(objectBucket) ) {
					// Something went wonky during the query operation
					// revert to list object call
					listObjects(objectBucket);
				}		
			} else {
				listObjects(objectBucket);
			}
		}
	}

	private boolean queryObjects( ObjectBucket objectBucket) {
		
		QueryObjectsCollection queryObjectsCollection = 
				new QueryObjectsCollection( collectionConfig, objectBucket );
			
		return queryObjectsCollection.queryObjects();
	}

	private void listObjects( ObjectBucket objectBucket ) {
		
		ListObjectsCollection queryObjectsCollection = 
				new ListObjectsCollection( collectionConfig, objectBucket );
		
		queryObjectsCollection.collectObjectKeys();
	}
}
