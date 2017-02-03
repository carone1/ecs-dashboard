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


import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.ecs.management.entity.ObjectBucket;
import com.emc.ecs.management.entity.ObjectUserDetails;
import com.emc.ecs.metadata.dao.ObjectDAO;
import com.emc.object.Protocol;
import com.emc.object.s3.S3Config;
import com.emc.object.s3.jersey.S3JerseyClient;
import com.emc.rest.smart.ecs.Vdc;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

public class ObjectBO {

	//================================
	// Private members
	//================================
	private BillingBO 	       			billingBO;
	private List<String>       			ecsObjectHosts;
	private ObjectDAO 	 	   			objectDAO;
	private ThreadPoolExecutor 			threadPoolExecutor;
	private Queue<Future<?>>     		futures;
	private AtomicLong           		objectCount;
	
	private final static Logger         logger = LoggerFactory.getLogger(ObjectBO.class);

	

	//================================
	// Constructor
	//================================
	public ObjectBO( BillingBO          billingBO, 
					 List<String>       ecsObjectHosts,
					 ObjectDAO          objectDAO,
					 ThreadPoolExecutor threadPoolExecutor,
					 Queue<Future<?>>   futures,
					 AtomicLong         objectCount ) {
				
		this.billingBO          = billingBO;
		this.ecsObjectHosts     = ecsObjectHosts;
		this.threadPoolExecutor = threadPoolExecutor;
		this.futures            = futures;
		this.objectDAO          = objectDAO;
		this.objectCount        = objectCount;
	}
	
	//================================
	// Public methods
	//================================
	
	public ThreadPoolExecutor getThreadPool() {
		return threadPoolExecutor;
	}
	
	public Collection<Future<?>> getFutures() {
		return futures;
	}
	
	public void collectObjectData(Date collectionTime) {

		collectObjectData(collectionTime, null);
	}
	
	
	public void collectObjectData(Date collectionTime, String queryCriteria) {
		
		// collect S3 user Id and credentials
		List<ObjectUserDetails> objectUserDetailsList = billingBO.getObjectUserSecretKeys();
		
		// Collect bucket details
		Map<NamespaceBucketKey, ObjectBucket> objectBucketMap = new HashMap<>();
		billingBO.getObjectBucketData(objectBucketMap);

		Map<String, S3JerseyClient> s3ObjectClientMap = null;
		
		try {
			// create all required S3 jersey clients for very S3 users
			s3ObjectClientMap = createS3ObjectClients(objectUserDetailsList, this.ecsObjectHosts);
			
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
				
					ObjectCollectionConfig collectionConfig = new ObjectCollectionConfig(s3JerseyClient, 
																						 namespace,
																						 this.objectDAO,
																						 objectBucketMap,
																						 collectionTime,
																						 objectCount,
																						 threadPoolExecutor,
																						 futures, 
																						 queryCriteria );
					
					NamespaceObjectCollection namespaceObjectCollection = 
							new NamespaceObjectCollection( collectionConfig );
				
					try {
						// submit namespace collection to thread pool
						futures.add(threadPoolExecutor.submit(namespaceObjectCollection));
					} catch (RejectedExecutionException e) {
						// Thread pool didn't accept bucket collection
						// running in the current thread
						logger.error("Thread pool didn't accept bucket collection - running in current thread");
						try {
							namespaceObjectCollection.call();
						} catch (Exception e1) {
							logger.error("Error occured during namespace object collection operation - message: " + e.getLocalizedMessage());
						}
					}
				}
			}


			
		} finally {
			// ensure to clean up S3 jersey clients
			if(s3ObjectClientMap != null ) {
				for( S3JerseyClient s3JerseyClient : s3ObjectClientMap.values() ) {
					s3JerseyClient.destroy();
				}
			}
		}
		
	}
	
	public void collectObjectVersionData(Date collectionTime) {

		// collect S3 user Id and credentials
		List<ObjectUserDetails> objectUserDetailsList = billingBO.getObjectUserSecretKeys();
		
		// Collect bucket details
		Map<NamespaceBucketKey, ObjectBucket> objectBucketMap = new HashMap<>();
		billingBO.getObjectBucketData(objectBucketMap);

		Map<String, S3JerseyClient> s3ObjectClientMap = null;
		
		try {
			// create all required S3 jersey clients for very S3 users
			s3ObjectClientMap = createS3ObjectClients(objectUserDetailsList, this.ecsObjectHosts);
			
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
				
					ObjectCollectionConfig collectionConfig = new ObjectCollectionConfig( s3JerseyClient, 
																						  namespace,
																						  objectDAO,
																						  objectBucketMap,
																						  collectionTime,
																						  objectCount,
																						  threadPoolExecutor,
																						  futures, 
																						  null // no criteria required here
																						  );
					
					NamespaceObjectVersionCollection namespaceObjectVersionCollection = 
							new NamespaceObjectVersionCollection( collectionConfig );
				
					try {
						// submit namespace collection to thread pool
						futures.add(threadPoolExecutor.submit(namespaceObjectVersionCollection));
					} catch (RejectedExecutionException e) {
						// Thread pool didn't accept bucket collection
						// running in the current thread
						logger.error("Thread pool didn't accept bucket collection - running in current thread");
						try {
							namespaceObjectVersionCollection.call();
						} catch (Exception e1) {
							logger.error("Error occured during namespace object version collection operation - message: "
						    + e.getLocalizedMessage());
						}
					}
				}
			}
			
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
			S3Config s3config = new S3Config(Protocol.HTTP, vdc);			

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
			logger.error(e.getLocalizedMessage());
		} // wait for poll to complete
		
		return s3JerseyClientList;
	}
	

	public void shutdown() {
		billingBO.shutdown();
	}


	
}
