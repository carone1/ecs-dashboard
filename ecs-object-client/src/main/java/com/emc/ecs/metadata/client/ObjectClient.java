package com.emc.ecs.metadata.client;

import com.emc.object.Protocol;
import com.emc.object.s3.S3Config;
import com.emc.object.s3.bean.AccessControlList;
import com.emc.object.s3.bean.BucketInfo;
import com.emc.object.s3.bean.LifecycleConfiguration;
import com.emc.object.s3.bean.ListBucketsResult;
import com.emc.object.s3.bean.ListObjectsResult;
import com.emc.object.s3.jersey.S3JerseyClient;
import com.emc.object.s3.request.ListBucketsRequest;
import com.emc.object.s3.request.ListObjectsRequest;
import com.emc.object.s3.request.ListVersionsRequest;
import com.emc.rest.smart.ecs.Vdc;

public class ObjectClient {

	S3JerseyClient s3JerseyClient;
	
	
	public ObjectClient() {
		
//		Vdc plymouthLab = new Vdc("").withName("PlymouthLab");		
//		S3Config s3config = new S3Config(Protocol.HTTP, plymouthLab);
//		
//		// in all cases, you need to provide your credentials
//		s3config.withIdentity("eric-caron").withSecretKey("n4tGqMYn67Jk3dkJmZ9+j6rEEJL0G6TJDYi/C5fr");
//		
//		s3JerseyClient = new S3JerseyClient(s3config);
//		
//		s3JerseyClient.bucketExists("some name");
//				
//		// Retention period
//		BucketInfo bucketInfo = s3JerseyClient.getBucketInfo("somename");
//		
//		LifecycleConfiguration lifecycleConfig = s3JerseyClient.getBucketLifecycle("somename");
//		
//		//
//		//AccessControlList acl = s3JerseyClient.getBucketAcl("somename");
//		
//		// Return primary VDC
//		//s3JerseyClient.getBucketLocation(bucketName)
//		
//		// Bucket none, enabled, disabled
//		 s3JerseyClient.getBucketVersioning("bucketName");
//		 
//		 s3JerseyClient.getObject("bucketName", "key");
//		 
//		 ListBucketsResult listBucketResult = s3JerseyClient.listBuckets();			
//		 ListBucketsRequest listBucketRequest = new ListBucketsRequest(); 
//		 s3JerseyClient.listBuckets(listBucketRequest);
//		 
//		 ListObjectsRequest listObjectRequest = new ListObjectsRequest("bucketname");
//		 ListObjectsResult listObjectResult = s3JerseyClient.listObjects(listObjectRequest);
//		 // or
//		 ListObjectsResult listObjectResult2 = s3JerseyClient.listObjects("bucketName");
//				 
//		 ListObjectsResult listMoreObjectResult = s3JerseyClient.listMoreObjects(listObjectResult);
//		 
//		 ListVersionsRequest listVersionsRequest = new ListVersionsRequest("bucketName");
//		 s3JerseyClient.listVersions(listVersionsRequest);
	}
	
}
