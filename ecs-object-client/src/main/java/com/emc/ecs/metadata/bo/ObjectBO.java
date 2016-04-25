package com.emc.ecs.metadata.bo;


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
import com.emc.rest.smart.ecs.Vdc;

public class ObjectBO {

	
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
	public void collectObjectData() {
		
		List<ObjectUserDetails> objectUserDetailsList = billingBO.getObjectUserSecretKeys();
		
		// collect objects for all users
		for( ObjectUserDetails objectUserDetails : objectUserDetailsList ) {
			
			if(objectUserDetails.getSecretKeys().getSecretKey1() == null) {
				// some user don't have a secret key configured
				// in that case we just skip over that user
				continue;
			}
			
			System.out.println("Object User: `" + objectUserDetails.getObjectUser().getUserId().toString() + "`");
			
			
			// Create object client user
			Vdc vdc = new Vdc((String [])this.ecsObjectHosts.toArray());	
			S3Config s3config = new S3Config(Protocol.HTTPS, vdc);			
			
			// in all cases, you need to provide your credentials
			s3config.withIdentity(objectUserDetails.getObjectUser().getUserId().toString())
					.withSecretKey(objectUserDetails.getSecretKeys().getSecretKey1());
			
			S3JerseyClient s3JerseyClient = new S3JerseyClient(s3config);
			try {								
				// First bucket batch
				ListBucketsResult listBucketsResult = s3JerseyClient.listBuckets();			
				collectObjectPerBucketBatch( s3JerseyClient, listBucketsResult.getBuckets() );			

				// subsequent bucket batch
				while( listBucketsResult.isTruncated() ) {				
					ListBucketsRequest listBucketRequest = new ListBucketsRequest();
					listBucketRequest.setMarker(listBucketsResult.getMarker());
					listBucketsResult = s3JerseyClient.listBuckets(listBucketRequest);
					collectObjectPerBucketBatch( s3JerseyClient, listBucketsResult.getBuckets() );				
				}
			} finally {
				s3JerseyClient.destroy();
			}			
		}
		
	}
	
	private void collectObjectPerBucketBatch(S3JerseyClient s3JerseyClient, List<Bucket> bucketList) {
		
		for( Bucket bucket : bucketList ) {
			// Collect all objects in that bucket 
			System.out.println("Collecting object for bucket: " + bucket.getName());
			
			ListObjectsResult listObjectsResult = s3JerseyClient.listObjects(bucket.getName());
			
			if(listObjectsResult != null) {
				if(this.objectDAO != null) {					
					objectDAO.insert(listObjectsResult);
				}
				
				while(listObjectsResult.isTruncated()) {
					listObjectsResult = s3JerseyClient.listMoreObjects(listObjectsResult);
					
					if(this.objectDAO != null) {
						objectDAO.insert(listObjectsResult);
					}
				}
				
			}
		}
	}

	public void shutdown() {
		billingBO.shutdown();		
	}
	
}
