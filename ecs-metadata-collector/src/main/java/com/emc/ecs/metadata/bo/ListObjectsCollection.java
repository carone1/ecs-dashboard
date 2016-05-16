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
