package com.emc.ecs.metadata.bo;


import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.ecs.management.entity.ObjectBucket;
import com.emc.object.s3.bean.ListVersionsResult;
import com.emc.object.s3.bean.VersioningConfiguration;
import com.emc.object.s3.request.ListVersionsRequest;


public class ListObjectsVersionsCollection implements Callable<String> {

	private static final Integer maxObjectPerRequest = 10000;
	
	//=============================
	// Private members
	//=============================
	private ObjectCollectionConfig collectionConfig;
	private ObjectBucket           objectBucket;
	private final static Logger    logger = LoggerFactory.getLogger(ListObjectsVersionsCollection.class);
	
	
	public ListObjectsVersionsCollection( ObjectCollectionConfig collectionConfig,  
								  ObjectBucket objectBucket                 ) {
		
		this.collectionConfig  = collectionConfig; 
		this.objectBucket      = objectBucket; 
	}
	
	
	//=============================
	// Public methods
	//=============================
	@Override
	public String call() throws Exception {
		collectObjectVersions();
		return "ok";
	}

	//=============================
	// Public methods
	//=============================
	public void collectObjectVersions(){

		// Verify if versioning is enabled on bucket
		if(!getBucketVersioningConfiguration().equals(VersioningConfiguration.Status.Enabled)) {
			logger.info( "No object version collected from namespace: " + 
					     collectionConfig.getNamespace() + " bucket: " + objectBucket.getName() + 
					     " because versioning is disabled on bucket");
			return;
		}
		
		
		// prepare request object
		ListVersionsRequest listVersionsRequest = new ListVersionsRequest(objectBucket.getName());
		listVersionsRequest.setMaxKeys(maxObjectPerRequest);
		listVersionsRequest.setNamespace(collectionConfig.getNamespace());

		long startTime = System.currentTimeMillis();

		try{
			// collect objects
			ListVersionsResult listVersionsResult = collectionConfig.getS3JerseyClient().listVersions(listVersionsRequest);
			
			

			long stopTime = System.currentTimeMillis();
			Double elapsedTime = Double.valueOf(stopTime - startTime) / 1000;

			if(listVersionsResult != null) {

				Long collected = (long)listVersionsResult.getVersions().size();

				this.collectionConfig.getObjectCount().getAndAdd(collected);

				logger.info( "Took: " + elapsedTime + " seconds to collect " +
						collected + " objects from namespace: " + 
						collectionConfig.getNamespace() + " bucket: " + objectBucket.getName());

				// add collected entries into datastore
				if( collectionConfig.getObjectDAO() != null) {					
					collectionConfig.getObjectDAO().insert( listVersionsResult, 
												collectionConfig.getNamespace(),
												objectBucket.getName(), 
												collectionConfig.getCollectionTime() );
				}

				// process extra pages of objects
				while(listVersionsResult.isTruncated()) {

					listVersionsRequest.setVersionIdMarker(listVersionsResult.getNextVersionIdMarker());

					startTime = System.currentTimeMillis();
					listVersionsResult = collectionConfig.getS3JerseyClient().listVersions(listVersionsRequest);
					stopTime = System.currentTimeMillis();

					elapsedTime = Double.valueOf(stopTime - startTime) / 1000;

					collected = (long)listVersionsResult.getVersions().size();
					collectionConfig.getObjectCount().getAndAdd(collected);

					logger.info("Took: " + elapsedTime + " seconds to collect " +
							collected + " objects versions from namespace: " + 
							collectionConfig.getNamespace() + " bucket: " + objectBucket.getName());

					// add collected entries into datastore
					if( collectionConfig.getObjectDAO() != null) {					
						collectionConfig.getObjectDAO().insert( listVersionsResult, 
								collectionConfig.getNamespace(),
								objectBucket.getName(), 
								collectionConfig.getCollectionTime() );
					}
				}
			}
		} catch (Exception ex) {
			logger.error("Error occur while listing object versions from namespace: " + collectionConfig.getNamespace() +
					     " Bucket: " + objectBucket.getName() + " " + ex.getLocalizedMessage() );
			throw new RuntimeException(ex.getLocalizedMessage());
		}
	}
	
	
	
	//=============================
	// Private methods
	//=============================
	private VersioningConfiguration.Status getBucketVersioningConfiguration() {
		VersioningConfiguration versionConfig = collectionConfig.getS3JerseyClient().getBucketVersioning(objectBucket.getName());
		
		if(versionConfig != null && versionConfig.getStatus() != null) {
			return versionConfig.getStatus();
		} else {
			// assume versioning is disaabled
			return VersioningConfiguration.Status.Suspended;
		}
		
		
	}
	
}
