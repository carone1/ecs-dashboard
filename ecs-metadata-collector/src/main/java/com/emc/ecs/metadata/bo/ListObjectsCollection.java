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


import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.ecs.management.entity.ObjectBucket;
import com.emc.object.s3.bean.ListObjectsResult;
import com.emc.object.s3.request.ListObjectsRequest;


public class ListObjectsCollection implements Callable<String> {

	private static final Integer maxObjectPerRequest = 10000;
	
	//=============================
	// Private members
	//=============================
	private ObjectCollectionConfig collectionConfig;
	private ObjectBucket           objectBucket;
	private final static Logger    logger = LoggerFactory.getLogger(ListObjectsCollection.class);
	
	
	public ListObjectsCollection( ObjectCollectionConfig collectionConfig,  
								  ObjectBucket objectBucket                 ) {
		
		this.collectionConfig  = collectionConfig; 
		this.objectBucket      = objectBucket; 
	}
	
	
	//=============================
	// Public methods
	//=============================
	@Override
	public String call() throws Exception {
		collectObjectKeys();
		return "ok";
	}

	//=============================
	// Private methods
	//=============================
	public void collectObjectKeys(){

		// prepare request object
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest(objectBucket.getName());
		listObjectsRequest.setMaxKeys(maxObjectPerRequest);
		listObjectsRequest.setNamespace(collectionConfig.getNamespace());

		long startTime = System.currentTimeMillis();

		try{
			// collect objects
			ListObjectsResult listObjectsResult = collectionConfig.getS3JerseyClient().listObjects(listObjectsRequest);

			long stopTime = System.currentTimeMillis();
			Double elapsedTime = Double.valueOf(stopTime - startTime) / 1000;

			if(listObjectsResult != null) {

				Long collected = (long)listObjectsResult.getObjects().size();

				this.collectionConfig.getObjectCount().getAndAdd(collected);

				logger.info( "Took: " + elapsedTime + " seconds to collect " +
						collected + " objects from namespace: " + 
						collectionConfig.getNamespace() + " bucket: " + objectBucket.getName());

				// add collected entries into datastore
				if( collectionConfig.getObjectDAO() != null) {					
					collectionConfig.getObjectDAO().insert( listObjectsResult, 
							collectionConfig.getNamespace(),
							objectBucket.getName(), 
							collectionConfig.getCollectionTime() );
				}

				// process extra pages of objects
				while(listObjectsResult.isTruncated()) {

					listObjectsRequest.setMarker(listObjectsResult.getNextMarker());

					startTime = System.currentTimeMillis();
					listObjectsResult = collectionConfig.getS3JerseyClient().listObjects(listObjectsRequest);
					stopTime = System.currentTimeMillis();

					elapsedTime = Double.valueOf(stopTime - startTime) / 1000;

					collected = (long)listObjectsResult.getObjects().size();
					collectionConfig.getObjectCount().getAndAdd(collected);

					logger.info("Took: " + elapsedTime + " seconds to collect " +
							collected + " objects from namespace: " + 
							collectionConfig.getNamespace() + " bucket: " + objectBucket.getName());

					// add collected entries into datastore
					if( collectionConfig.getObjectDAO() != null) {					
						collectionConfig.getObjectDAO().insert( listObjectsResult, 
								collectionConfig.getNamespace(),
								objectBucket.getName(), 
								collectionConfig.getCollectionTime() );
					}
				}
			}
		} catch (Exception ex) {
			logger.error("Error occur while listing object from namespace: " + collectionConfig.getNamespace() +
					     " Bucket: " + objectBucket.getName() + " " + ex.getLocalizedMessage() );
			throw new RuntimeException(ex.getLocalizedMessage());
		}
	}
}
