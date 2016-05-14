package com.emc.ecs.metadata.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.emc.ecs.management.entity.Metadata;
import com.emc.ecs.management.entity.ObjectBucket;
import com.emc.object.s3.bean.ListObjectsResult;
import com.emc.object.s3.bean.QueryObjectsResult;
import com.emc.object.s3.request.QueryObjectsRequest;


public class ObjectsKeysCollection implements Callable<String> {

	private static final Integer maxObjectPerRequest = 10000;
	
	//=============================
	// Private members
	//=============================
	private ObjectCollectionConfig collectionConfig;
	private ObjectBucket           objectBucket;
	
	
	public ObjectsKeysCollection( ObjectCollectionConfig collectionConfig,  
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
	private void collectObjectKeys(){
		
		// create request
		QueryObjectsRequest queryRequest = createQueryObjectRequest();
		
		long startTime = System.currentTimeMillis();
		
		// Query Objects
		try {
			QueryObjectsResult queryResult = collectionConfig.getS3JerseyClient().queryObjects(queryRequest);


			long stopTime = System.currentTimeMillis();
			Double elapsedTime = Double.valueOf(stopTime - startTime) / 1000;

			if(queryResult != null) {

				Long collected = (long)queryResult.getObjects().size();

				//this.collectionConfig.getObjectCount().getAndAdd(collected);

				System.out.println("Took: " + elapsedTime + " seconds to query " +
						collected + " objects from namespace: " + 
						collectionConfig.getNamespace() + " bucket: " + queryResult.getBucketName());


				if(this.collectionConfig.getObjectDAO() != null) {					
					//this.collectionConfig.getObjectDAO().insert( queryResult, 
					//										 	 this.collectionConfig.getNamespace(),
					//										 	 listObjectsResult.getBucketName(), 
					//										 	 this.collectionConfig.getCollectionTime() );
				}

				// extra pages to collect
				while(queryResult.isTruncated()) {

					// Move marker to beginning of next batch
					queryRequest.setMarker(queryResult.getNextMarker());		

					startTime = System.currentTimeMillis();

					queryResult = this.collectionConfig.getS3JerseyClient().queryObjects(queryRequest);

					stopTime = System.currentTimeMillis();

					elapsedTime = Double.valueOf(stopTime - startTime) / 1000;

					collected = (long)queryResult.getObjects().size();
					//this.collectionConfig.getObjectCount().getAndAdd(collected);

					System.out.println("Took: " + elapsedTime + " seconds to query " +
							collected + " objects from namespace: " + 
							this.collectionConfig.getNamespace() + " bucket: " + queryResult.getBucketName());

					if(this.collectionConfig.getObjectDAO() != null) {					
						//this.collectionConfig.getObjectDAO().insert( queryResult, 
						//										 	 this.collectionConfig.getNamespace(),
						//										 	 listObjectsResult.getBucketName(), 
						//										 	 this.collectionConfig.getCollectionTime() );
					}
				}				
			}
		} catch (Exception ex) {
			System.err.println( "Error: Namespace: " + collectionConfig.getNamespace() + " bucket: " + objectBucket.getName() +
		           			    " Query string: `" + queryRequest.getQuery() + "`" + ex.getMessage() );
		}
	}
	
	
	private QueryObjectsRequest createQueryObjectRequest() {
		
		// create request
		QueryObjectsRequest queryRequest = new QueryObjectsRequest(objectBucket.getName());
		
		List<String> attributeList = new ArrayList<String>();
		StringBuilder queryString = new StringBuilder();
		// add index keys to the search query
		for( Metadata metadata: objectBucket.getSearchMetadata() ) {
			
			if( metadata.getName() == null ||
				metadata.getName().isEmpty() ) {
				continue;
			}
			
			attributeList.add(metadata.getName());
			
			switch(metadata.getDataType().trim().toLowerCase()) {
				case "string" :
					if(queryString.length() > 0) {
						queryString.append(" or ");
					}
					queryString.append("(" + metadata.getName() +" <= '') or (" + metadata.getName() + " >= '')");
					break;
				case "decimal" :
					if(queryString.length() > 0) {
						queryString.append(" or ");
					}
					queryString.append("(" + metadata.getName() +" <= 1.0) or (" + metadata.getName() + " >= 1.0)");
					break;
				case "integer" :
					if(queryString.length() > 0) {
						queryString.append(" or ");
					}
					queryString.append("(" + metadata.getName() +" <= 1) or (" + metadata.getName() + " >= 1)");
					break;
				case "datetime" :
					if(queryString.length() > 0) {
						queryString.append(" or ");
					}
					queryString.append("(" + metadata.getName() +" <= 2015-01-01:00:00:00Z) or (" + metadata.getName() + " >= 2015-01-01:00:00:00Z)");
					break;
			}	
		}
	
		queryRequest.withQuery( queryString.toString() );
		queryRequest.withAttributes(attributeList);
		queryRequest.setMaxKeys(maxObjectPerRequest);
		queryRequest.setNamespace(collectionConfig.getNamespace());
		
		System.out.println("Namespace: " + collectionConfig.getNamespace() + " bucket: " + objectBucket.getName() +
		           			" Query string: `" + queryString.toString() + "`" );
		
		return queryRequest;
	}
}
