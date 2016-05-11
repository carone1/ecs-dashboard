package com.emc.ecs.metadata.bo;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.emc.ecs.management.entity.ObjectUserDetails;
import com.emc.ecs.metadata.dao.ObjectDAO;
import com.emc.object.Protocol;
import com.emc.object.s3.S3Config;
import com.emc.object.s3.jersey.S3JerseyClient;
import com.emc.rest.smart.ecs.Vdc;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

public class ObjectBO {

	private final static int MAX_THREADS = 50;
	
	private AtomicLong objectCount = new AtomicLong(0L);
	
	//================================
	// Private members
	//================================
	BillingBO 	       billingBO;
	List<String>       ecsObjectHosts;
	ObjectDAO 	 	   objectDAO;
	ThreadPoolExecutor executorThreadPoolExecutor;
	
	
	//================================
	// Constructor
	//================================
	public ObjectBO(BillingBO billingBO, List<String> ecsObjectHosts, ObjectDAO objectDAO ) {
				
		this.billingBO = billingBO;
		this.ecsObjectHosts = ecsObjectHosts;
		this.objectDAO = objectDAO;
		this.executorThreadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_THREADS);
		
	}
	
	//================================
	// Public methods
	//================================
	public void collectObjectData(Date collectionTime) {

		// collect S3 user Id and credentials
		List<ObjectUserDetails> objectUserDetailsList = billingBO.getObjectUserSecretKeys();

		Map<String, S3JerseyClient> s3ObjectClientMap = null;
		Long objectCollectionStart = System.currentTimeMillis();
		
		try {
			// create all required S3 jersey clients for very S3 users
			s3ObjectClientMap = createS3ObjectClients(objectUserDetailsList, this.ecsObjectHosts);

			objectCollectionStart = System.currentTimeMillis();

			// collect objects for all users
			for( ObjectUserDetails objectUserDetails : objectUserDetailsList ) {

				if( objectUserDetails.getObjectUser().getUserId() == null ||
						objectUserDetails.getSecretKeys().getSecretKey1() == null) {
					// some user don't have a secret key configured
					// in that case we just skip over that user
					continue;
				}

				String userId = objectUserDetails.getObjectUser().getUserId().toString();

				S3JerseyClient s3JerseyClient = s3ObjectClientMap.get(userId);
				String namespace = objectUserDetails.getObjectUser().getNamespace().toString();

				if(s3JerseyClient != null && namespace != null) {
				
					NamespaceObjectCollection namespaceObjectCollection = 
							new NamespaceObjectCollection( s3JerseyClient, namespace, this.objectDAO, 
														   collectionTime, objectCount);
					
					// submit namespace collection to thread pool
					executorThreadPoolExecutor.execute(namespaceObjectCollection);
				}
			}

			// take everything down once all thread have completed their work
			executorThreadPoolExecutor.shutdown();
			
			// wait for all threads to terminate
			boolean termination = false; 
			do {
				try {
					termination = executorThreadPoolExecutor.awaitTermination(2, TimeUnit.MINUTES);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while(!termination);
			
			Long objectCollectionFinish = System.currentTimeMillis();
			Double deltaTime = Double.valueOf((objectCollectionFinish - objectCollectionStart)) / 1000 ;
			System.out.println("Collected " + objectCount.get() + " objects");
			System.out.println("Total collection time: " + deltaTime + " seconds");
			
		} finally {
			// ensure to clean up S3 jersey clients
			if(s3ObjectClientMap != null ) {
				for( S3JerseyClient s3JerseyClient : s3ObjectClientMap.values() ) {
					s3JerseyClient.destroy();
				}
			}
		}
	}
	
	
	private Map<String, S3JerseyClient> createS3ObjectClients( List<ObjectUserDetails> objectUserDetailsList, 
															   List<String> ecsObjectHosts      ) {
		
		Map<String, S3JerseyClient> s3JerseyClientList = new HashMap<String, S3JerseyClient>();
		
		// collect objects for all users
		for(  ObjectUserDetails objectUserDetails : objectUserDetailsList ) {

			if( objectUserDetails.getObjectUser().getUserId() == null || 
				objectUserDetails.getSecretKeys().getSecretKey1() == null ) {
				// some user don't have a secret key configured
				// in that case we just skip over that user
				continue;
			}

			// Create object client user
			Vdc vdc = new Vdc((String [])this.ecsObjectHosts.toArray());	
			S3Config s3config = new S3Config(Protocol.HTTPS, vdc);			

			// in all cases, you need to provide your credentials
			s3config.withIdentity(objectUserDetails.getObjectUser().getUserId().toString())
				.withSecretKey(objectUserDetails.getSecretKeys().getSecretKey1());

			s3config.setSmartClient(true);
			URLConnectionClientHandler urlHandler = new URLConnectionClientHandler();
			S3JerseyClient s3JerseyClient = new S3JerseyClient(s3config, urlHandler);
			
			s3JerseyClientList.put(objectUserDetails.getObjectUser().getUserId().toString(), s3JerseyClient);
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // wait for poll to complete
		
		return s3JerseyClientList;
	}
	

	public void shutdown() {
		billingBO.shutdown();
	}
	
}
