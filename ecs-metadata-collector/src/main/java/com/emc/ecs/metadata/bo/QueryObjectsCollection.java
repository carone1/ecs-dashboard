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
	//private static final String  SIZE_KEY            = "Size";
	//private static final String  LAST_MODIFIED_KEY   = "LastModified";
	
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
		
		String queryCriteria = collectionConfig.getQueryCriteria();
		
		// create request
		QueryObjectsRequest queryRequest;
		
		if(  queryCriteria != null &&
			!queryCriteria.isEmpty()	) {
			// there is a criteria defined
			queryRequest = createQueryObjectRequest(queryCriteria);
		} else {
			// no criteria we will just collect all possible objects
			queryRequest = createQueryObjectRequest();
		}
		
		if(queryRequest == null) {
			// if the createQueryObjectRequest method returned null
			// is because there are non useful MD keys configured 
			// against bucket.  return false so list objects operation
			// will be triggered
			return false;
		}
		
		Long bucketObjectCount = 0L;
		long startTime = System.currentTimeMillis();
		
		// Query Objects
		try {
			QueryObjectsResult queryResult = collectionConfig.getS3JerseyClient().queryObjects(queryRequest);


			long stopTime = System.currentTimeMillis();
			
			Double elapsedTime = Double.valueOf(stopTime - startTime) / 1000;

			if(queryResult != null) {

				Long collected = (long)queryResult.getObjects().size();
				
				// increase local counter
				bucketObjectCount += collected;

				// Increase central counter
				this.collectionConfig.getObjectCount().getAndAdd(collected);

				logger.info("Took: " + elapsedTime + " seconds to query " +
						collected + " objects from namespace: " + 
						collectionConfig.getNamespace() + " bucket: " + queryResult.getBucketName());

				if(collectionConfig.getObjectDAO() != null) {					
					collectionConfig.getObjectDAO().insert( queryResult, 
															 	 collectionConfig.getNamespace(),
															 	 queryResult.getBucketName(), 
															 	 collectionConfig.getCollectionTime() );
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
					
					// increase local counter
					bucketObjectCount += collected;
					
					// Increase central counter
					collectionConfig.getObjectCount().getAndAdd(collected);

					logger.info("Took: " + elapsedTime + " seconds to query " +
							     collected + " objects from namespace: " + 
							     this.collectionConfig.getNamespace() + " bucket: " + queryResult.getBucketName());

					if(this.collectionConfig.getObjectDAO() != null) {					
						this.collectionConfig.getObjectDAO().insert( queryResult, 
																 	 collectionConfig.getNamespace(),
																 	 queryResult.getBucketName(), 
																 	 collectionConfig.getCollectionTime() );
					}
				}				
			}
		} catch (Exception ex) {
			
			// known issue ECs returns this error when a bucket has MD keys but has not objects
			if(ex.getMessage().contains("Invalid search index value format or operator used")) {
			  // just silently let this go. This error will eventually be fixed by ECS 
				logger.error( "Error: Namespace: " + collectionConfig.getNamespace() + " bucket: " + objectBucket.getName() +
           			    " Query string: `" + queryRequest.getQuery() + "`" + ex.getMessage() );
			} else if(ex.getMessage().contains("We encountered an internal error. Please try again")) {
				// Here we could try again
				
				logger.error( "Error: Namespace: " + collectionConfig.getNamespace() + " bucket: " + objectBucket.getName() +
           			    " Query string: `" + queryRequest.getQuery() + "`" + ex.getMessage() );
			}
		}
		
		if(bucketObjectCount.compareTo(0L) == 0) {
			// there was no object collected using MD keys configured 
			// against the bucket.  Return false so list object operations
			// will be triggered
			return false;
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
			//if( LAST_MODIFIED_KEY.equals(metadata.getName()) 
			//		SIZE_KEY.equals(metadata.getName()) ) {

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
					queryString.append( "( " + metadata.getName() + " > '1970-01-01T00:00:00Z' )");
				} else {
					logger.error("Unhandled data type: " + dataType);
				}
			}
		//}
		
		if(queryString.length() == 0 ) {
			// no  MD keys are configured
			// return null to trigger list operation instead
			return null;
		}
		
		// append ( at beginning
		queryString.insert(0, "( ");
		// append ) at end
		queryString.append(" )");
		
		
		queryRequest.withQuery( queryString.toString() );
		queryRequest.withAttributes( attributeList );
		queryRequest.setMaxKeys( maxObjectPerRequest );
		queryRequest.setNamespace( collectionConfig.getNamespace() );
		
		logger.info("QueryObject Collection for Namespace: " + collectionConfig.getNamespace() + " Bucket: " + objectBucket.getName() );
		
		logger.debug(" Using query string: `" + queryString.toString() + "`" );
		
		return queryRequest;
	}
	
	
	private QueryObjectsRequest createQueryObjectRequest(String queryString) {
		
		// create request
		QueryObjectsRequest queryRequest = new QueryObjectsRequest(objectBucket.getName());
		
		List<String> attributeList = new ArrayList<String>();
		List<String> attributeListDetails = new ArrayList<String>();
		StringBuilder queryBufferString = new StringBuilder();
		
		// add all attribute to attr list
		for( Metadata metadata: objectBucket.getSearchMetadata() ) {
			
			if( metadata.getName() == null ||
				metadata.getName().isEmpty() ) {
				continue;
			}
			
			attributeList.add(metadata.getName());
			
			String attributeDetails = "Name: " + metadata.getName() + " Type:" + metadata.getDataType().trim().toLowerCase();
			attributeListDetails.add(attributeDetails);
		}
		
		
		// add search queryString if at least
		// one search metadata key is defined
		if( attributeList.isEmpty() ) {
			// no  MD keys are configured
			// return null to prevent any querying
			return null;	
		} else {
			queryBufferString.append(queryString);
		}
		
		// append ( at beginning
		queryBufferString.insert(0, "( ");
		// append ) at end
		queryBufferString.append(" )");
		
		
		queryRequest.withQuery( queryString.toString() );
		queryRequest.withAttributes( attributeList );
		queryRequest.setMaxKeys( maxObjectPerRequest );
		queryRequest.setNamespace( collectionConfig.getNamespace() );
		
		logger.info("QueryObject Collection for Namespace: " + collectionConfig.getNamespace() + " Bucket: " + objectBucket.getName() );
		logger.info("MD Keys details: " + attributeListDetails.toString());
		logger.info(" Using query string: `" + queryString.toString() + "`" );
		
		return queryRequest;
	}
	
}
