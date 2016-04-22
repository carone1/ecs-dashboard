package com.emc.ecs.metadata.bo;

public class ObjectBO {

	
	//================================
	// Private members
	//================================
	
	
	//================================
	// Constructor
	//================================
	public ObjectBO() {
		
	}
	
	//================================
	// Public methods
	//================================
	public void collectObjectData() {
		
	}
	
	
	//	private static void listBuckets() {
	//	
	//	// List all buckets 
	//	ListBucketsResult bucketsResults = s3JerseyClient.listBuckets();
	//	
	//	for( Bucket bucket : bucketsResults.getBuckets()) {
	//		System.out.println("Bucket Name: " + bucket.getName());
	//	}
	//}

	//Vdc plymouthLab = new Vdc(ecsHost).withName("PlymouthLab");		
	//S3Config s3config = new S3Config(Protocol.HTTPS, plymouthLab);			
	//
	//// in all cases, you need to provide your credentials
	//s3config.withIdentity("eric-caron").withSecretKey("n4tGqMYn67Jk3dkJmZ9+j6rEEJL0G6TJDYi/C5fr");
	//
	//
	//s3JerseyClient = new S3JerseyClient(s3config);
}
