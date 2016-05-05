package com.emc.ecs.metadata.bo;


import java.util.Date;
import java.util.List;

import com.emc.ecs.management.entity.ObjectUserDetails;
import com.emc.ecs.metadata.dao.ObjectDAO;
import com.emc.object.Protocol;
import com.emc.object.s3.S3Config;
import com.emc.object.s3.bean.Bucket;
import com.emc.object.s3.bean.ListBucketsResult;
import com.emc.object.s3.bean.ListObjectsResult;
import com.emc.object.s3.jersey.S3JerseyClient;
import com.emc.object.s3.request.ListBucketsRequest;
import com.emc.object.s3.request.ListObjectsRequest;
import com.emc.rest.smart.ecs.Vdc;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

public class ObjectBO {

	private static final Integer maxObjectPerRequest = 10000;	
	private Long objectCount = 0L;
	
	//================================
	// Private members
	//================================
	BillingBO 	 billingBO;
	List<String> ecsObjectHosts;
	ObjectDAO 	 objectDAO;
	
	//================================
	// Constructor
	//================================
	public ObjectBO(BillingBO billingBO, List<String> ecsObjectHosts, ObjectDAO objectDAO ) {
				
		this.billingBO = billingBO;
		this.ecsObjectHosts = ecsObjectHosts;
		this.objectDAO = objectDAO;
		
	}
	
	//================================
	// Public methods
	//================================
	public void collectObjectData(Date collectionTime) {
		
		List<ObjectUserDetails> objectUserDetailsList = billingBO.getObjectUserSecretKeys();
		
		Long objectCollectionStart = System.currentTimeMillis();
		
		// collect objects for all users
		for( ObjectUserDetails objectUserDetails : objectUserDetailsList ) {
			
			if(objectUserDetails.getSecretKeys().getSecretKey1() == null) {
				// some user don't have a secret key configured
				// in that case we just skip over that user
				continue;
			}
						
			System.out.println("Object User: `" + objectUserDetails.getObjectUser().getUserId().toString() + "`");
			
			String namespace = objectUserDetails.getObjectUser().getNamespace().toString();
			
			// Create object client user
			Vdc vdc = new Vdc((String [])this.ecsObjectHosts.toArray());	
			S3Config s3config = new S3Config(Protocol.HTTPS, vdc);			
			
			// in all cases, you need to provide your credentials
			s3config.withIdentity(objectUserDetails.getObjectUser().getUserId().toString())
					.withSecretKey(objectUserDetails.getSecretKeys().getSecretKey1());
			
			s3config.setSmartClient(true);
			
			URLConnectionClientHandler urlHandler = new URLConnectionClientHandler();
			
			S3JerseyClient s3JerseyClient = new S3JerseyClient(s3config, urlHandler);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // wait for poll to complete
									
			try {				
				// First bucket batch
				ListBucketsResult listBucketsResult = s3JerseyClient.listBuckets();			
				objectCount += collectObjectPerBucketBatch( s3JerseyClient, 
															listBucketsResult.getBuckets(),
															namespace,
															collectionTime  );			

				// subsequent bucket batch
				while( listBucketsResult.isTruncated() ) {	
					
					ListBucketsRequest listBucketRequest = new ListBucketsRequest();
					listBucketRequest.setMarker(listBucketsResult.getMarker());
					listBucketRequest.setNamespace(namespace);
										
					listBucketsResult = s3JerseyClient.listBuckets(listBucketRequest);
					objectCount += collectObjectPerBucketBatch( s3JerseyClient, 
																listBucketsResult.getBuckets(),
																namespace,
																collectionTime  );				
				}
			} finally {
				s3JerseyClient.destroy();
			}
			
			
		}
		
		Long objectCollectionFinish = System.currentTimeMillis();
		Double deltaTime = Double.valueOf((objectCollectionFinish - objectCollectionStart)) / 1000 ;
		System.out.println("Collected " + objectCount + " objects");
		System.out.println("Total collection time: " + deltaTime + " seconds");
		
	}
	
	private Long collectObjectPerBucketBatch( S3JerseyClient s3JerseyClient, List<Bucket> bucketList, 
											  String namespace, Date collectionTime    ) {
		
		Long objectCount = 0L;
		
		
		for( Bucket bucket : bucketList ) {
			// Collect all objects in that bucket 
			System.out.println("Collecting object for bucket: " + bucket.getName() );
			
			ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucket.getName());
			listObjectsRequest.setMaxKeys(maxObjectPerRequest);
			listObjectsRequest.setNamespace(namespace);
			
			long startTime = System.currentTimeMillis();
			ListObjectsResult listObjectsResult = s3JerseyClient.listObjects(listObjectsRequest);
			long stopTime = System.currentTimeMillis();
		    Double elapsedTime = Double.valueOf(stopTime - startTime) / 1000;
		    
							
			if(listObjectsResult != null) {
				Long collected = (long)listObjectsResult.getObjects().size();
				objectCount += collected;
				System.out.println("Took: " + elapsedTime + " seconds to collect " + collected + " objects");
				
				if(this.objectDAO != null) {					
					objectDAO.insert( listObjectsResult, 
									  namespace, bucket.getName(),
									  collectionTime );
				}
				
				while(listObjectsResult.isTruncated()) {
					ListObjectsRequest moreListObjectsRequest = new ListObjectsRequest(bucket.getName());
					moreListObjectsRequest.setMaxKeys(maxObjectPerRequest);
					moreListObjectsRequest.setNamespace(namespace);
					moreListObjectsRequest.setMarker(listObjectsResult.getNextMarker());
					
					startTime = System.currentTimeMillis();
					listObjectsResult = s3JerseyClient.listObjects(moreListObjectsRequest);
					stopTime = System.currentTimeMillis();
					elapsedTime = Double.valueOf(stopTime - startTime) / 1000;
				    System.out.println(elapsedTime);
					
				    collected = (long)listObjectsResult.getObjects().size();
					objectCount += collected;
					
					System.out.println("Took: " + elapsedTime + " seconds to collect " + collected + " objects");
					
					if(this.objectDAO != null) {
						objectDAO.insert( listObjectsResult,
										  namespace,
										  bucket.getName(),
										  collectionTime   );
					}
				}				
			}			
		}
		
		return objectCount;
	}

	public void shutdown() {
		billingBO.shutdown();		
	}
	
}
