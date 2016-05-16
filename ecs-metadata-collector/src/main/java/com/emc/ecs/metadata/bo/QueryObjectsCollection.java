package com.emc.ecs.metadata.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.ecs.management.entity.Metadata;
import com.emc.ecs.management.entity.ObjectBucket;
import com.emc.object.s3.bean.QueryObjectsResult;
import com.emc.object.s3.request.QueryObjectsRequest;


public class QueryObjectsCollection implements Callable<String> {

	private static final Integer maxObjectPerRequest = 10000;
	private static final String  SIZE_KEY            = "Size";
	private static final String  LAST_MODIFIED_KEY   = "mtime";
	
	//=============================
	// Private members
	//=============================
	private ObjectCollectionConfig collectionConfig;
	private ObjectBucket           objectBucket;
	private final static Logger    logger = LoggerFactory.getLogger(QueryObjectsCollection.class);
	
	
	public QueryObjectsCollection( ObjectCollectionConfig collectionConfig,  
								  ObjectBucket objectBucket                 ) {
		
		
		this.collectionConfig  = collectionConfig; 
		this.objectBucket      = objectBucket; 
	}
	
	
	//=============================
	// Public methods
	//=============================
	@Override
	public String call() throws Exception {
		queryObjects();
		return "ok";
	}

	public boolean queryObjects(){
		
		// create request
		QueryObjectsRequest queryRequest = createQueryObjectRequest();
		
		if(queryRequest == null) {
			// if the createQueryObjectRequest method returned null
			// is because there are non useful MD keys configureed 
			// against bucket.  return false so list objects operation
			// will be trigerred
			return false;
		}
		
		long startTime = System.currentTimeMillis();
		
		// Query Objects
		try {
			QueryObjectsResult queryResult = collectionConfig.getS3JerseyClient().queryObjects(queryRequest);


			long stopTime = System.currentTimeMillis();
			Double elapsedTime = Double.valueOf(stopTime - startTime) / 1000;

			if(queryResult != null) {

				Long collected = (long)queryResult.getObjects().size();

				this.collectionConfig.getObjectCount().getAndAdd(collected);

				logger.info("Took: " + elapsedTime + " seconds to query " +
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
					this.collectionConfig.getObjectCount().getAndAdd(collected);

					logger.info("Took: " + elapsedTime + " seconds to query " +
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
			
			// known issue ECs returns this error when a bucket has MD keys but has not objects
			if(ex.getMessage().contains("Invalid search index value format or operator used")) {
			  // just silently let this go. This error will eventually be fixed by ECS 
			} else if(ex.getMessage().contains("We encountered an internal error. Please try again")) {
				// Here we could try again
				
				//System.err.println( "Error: Namespace: " + collectionConfig.getNamespace() + " bucket: " + objectBucket.getName() +
           		//	    " Query string: `" + queryRequest.getQuery() + "`" + ex.getMessage() );
			}
		}
		
		return true;
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
			
			// Only want to use MD keys (Last Modified Time or Size) which have
			// the better chance of being present on all objects
			if( LAST_MODIFIED_KEY.equals(metadata.getName()) ||  
					SIZE_KEY.equals(metadata.getName()) ) {

				String dataType = metadata.getDataType().trim().toLowerCase();
				if( dataType.equals("string" ) ) {
					if(queryString.length() > 0) {
						queryString.append(" or ");
					}
					queryString.append("(" + metadata.getName() +" <= '') or (" + metadata.getName() + " >= '' )");
				} else if( dataType.equals("decimal") ) {
					if(queryString.length() > 0) {
						queryString.append(" or ");
					}
					queryString.append("(" + metadata.getName() +" <= 1.0) or (" + metadata.getName() + " >= 1.0 )");
				} else if( dataType.equals("integer") ) {
					if(queryString.length() > 0) {
						queryString.append(" or ");
					}
					queryString.append("(" + metadata.getName() +" <= 1) or (" + metadata.getName() + " >= 1 )");
				} else if( dataType.equals("datetime") ) {
					if(queryString.length() > 0) {
						queryString.append(" or ");
					}
					queryString.append("(" + metadata.getName() +" <= '2015-01-01:00:00:00Z' ) or (" + metadata.getName() + " >= '2015-01-01:00:00:00Z' )");
				} else {
					System.err.println("Unhandled data type: " + dataType);
				}
			}
		}
		
		if(queryString.length() == 0 ) {
			// no  MD keys are configured
			// return null to trigger list operation instead
			return null;
		}
		
		queryRequest.withQuery( queryString.toString() );
		queryRequest.withAttributes( attributeList );
		queryRequest.setMaxKeys( maxObjectPerRequest );
		queryRequest.setNamespace( collectionConfig.getNamespace() );
		
		logger.debug("Namespace: " + collectionConfig.getNamespace() + " bucket: " + objectBucket.getName() +
		           			" Query string: `" + queryString.toString() + "`" );
		
		return queryRequest;
	}
}
