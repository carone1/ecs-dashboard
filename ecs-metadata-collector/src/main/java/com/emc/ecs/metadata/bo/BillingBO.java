package com.emc.ecs.metadata.bo;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.ecs.management.client.ManagementClient;
import com.emc.ecs.management.client.ManagementClientConfig;
import com.emc.ecs.management.entity.BucketBillingInfo;
import com.emc.ecs.management.entity.ListNamespaceRequest;
import com.emc.ecs.management.entity.ListNamespacesResult;
import com.emc.ecs.management.entity.Namespace;
import com.emc.ecs.management.entity.NamespaceBillingInfo;
import com.emc.ecs.management.entity.NamespaceRequest;
import com.emc.ecs.management.entity.ObjectBucket;
import com.emc.ecs.management.entity.ObjectBuckets;
import com.emc.ecs.management.entity.ObjectUser;
import com.emc.ecs.management.entity.ObjectUserDetails;
import com.emc.ecs.management.entity.ObjectUserSecretKeys;
import com.emc.ecs.management.entity.ObjectUsers;
import com.emc.ecs.management.entity.ObjectUsersRequest;
import com.emc.ecs.metadata.dao.BillingDAO;


public class BillingBO {

	//================================
	// Private members
	//================================
	private ManagementClient client;
	private BillingDAO       billingDAO;
	private final static Logger         logger = LoggerFactory.getLogger(BillingBO.class);
	
	//================================
	// Constructor
	//================================
	public BillingBO( String accessKey, 
					  String secretKey, 
					  List<String> hosts, 
					  Integer    port, 
					  BillingDAO billingDAO ) {
		
		// client config
		ManagementClientConfig clientConfig = new ManagementClientConfig( accessKey, 
																		  secretKey,
																		  port,
																		  hosts       );

		// create client        																	
		this.client = new ManagementClient(clientConfig);
		
		// DAO
		this.billingDAO = billingDAO;
		
	}
	
	//================================
	// Public methods
	//================================
	/**
	 * Retrieve Object uid and secret keys	 
	 * @return ObjectUserDetails
	 */
	public List<ObjectUserDetails> getObjectUserSecretKeys() {
		
		List<ObjectUserDetails> userDetails = new ArrayList<ObjectUserDetails>();

		// Collect all uids in order to collect secret keys after
		List<ObjectUser> objectUserList = new ArrayList<ObjectUser>();
		
		// first batch
		ObjectUsersRequest objectUsersRequest = new ObjectUsersRequest();
		ObjectUsers objectUsersResult = client.getObjectUsersUid(objectUsersRequest);
		
		if(objectUsersResult != null) {
			if(objectUsersResult.getBlobUser() != null) {
				objectUserList.addAll(objectUsersResult.getBlobUser());
			}
			
			objectUsersRequest.setMarker(objectUsersResult.getNextMarker());
			
			// Subsequent batches
			while(objectUsersResult.getNextMarker() != null) {
				objectUsersResult = client.getObjectUsersUid(objectUsersRequest);
				if(objectUsersResult != null) {
					objectUserList.addAll(objectUsersResult.getBlobUser());
					objectUsersRequest.setMarker(objectUsersResult.getNextMarker());
				} else {
					break;
				}
			}	
		}
		
		// Collect secret keys
		for( ObjectUser objectUser : objectUserList) {
			ObjectUserSecretKeys objectUserSecretKeys = 
					client.getObjectUserSecretKeys( objectUser.getUserId().toString(), 
													objectUser.getNamespace().toString());
			if(objectUserSecretKeys != null) {
				userDetails.add(new ObjectUserDetails(objectUser, objectUserSecretKeys));
			}
			
		}
		return userDetails;
	}
	
	
	
	/**
	 * Collects Billing metadata for all namespace defined on a cluster
	 * @param Date
	 */
	public void collectBillingData( Date collectionTime ) {
										
		
		// Collect the object bucket data first in order to use some of
		// the fields from object bucket
		Map<NamespaceBucketKey, ObjectBucket> objectBuckets = new HashMap<NamespaceBucketKey, ObjectBucket>();
		getObjectBukcetData(objectBuckets);
		
		// Start collecting billing data from ECS systems
		List<Namespace> namespaceList = getNamespaces();
		
		// At this point we should have all namespaces in the ECS system
		
		for( Namespace namespace : namespaceList ) {
			
			//===============================================
			// Initial billing request for current namespace
			//===============================================
			
			NamespaceRequest namespaceRequest = new NamespaceRequest();
			namespaceRequest.setName(namespace.getName());
			NamespaceBillingInfo namespaceBillingResponse = client.getNamespaceBillingInfo(namespaceRequest);
			
			if(namespaceBillingResponse == null) {
				continue;
			}
			
			// add object bucket attributes
			for(BucketBillingInfo bucketBillingInfo : namespaceBillingResponse.getBucketBillingInfo()) {
				
				NamespaceBucketKey namespaceBucketKey = new NamespaceBucketKey( namespace.getName(), 
																				bucketBillingInfo.getName());
				ObjectBucket objectBucket = objectBuckets.get( namespaceBucketKey);
				
				if(objectBucket != null) {
					// set api type
					bucketBillingInfo.setApiType(objectBucket.getApiType());
					// set namespace
					bucketBillingInfo.setNamespace(namespace.getName());
				}
			}
			
			// Push collected info into datastore
			if( this.billingDAO != null ) {
				// insert something
				billingDAO.insert(namespaceBillingResponse, collectionTime);
			}
			
			// collect n subsequent pages
			while(namespaceRequest.getNextMarker() != null) {
				namespaceBillingResponse = client.getNamespaceBillingInfo(namespaceRequest);
				
				if( namespaceBillingResponse != null ) {
					namespaceRequest.setNextMarker(namespaceBillingResponse.getNextMarker());
					
					// add object bucket attributes
					for(BucketBillingInfo bucketBillingInfo : namespaceBillingResponse.getBucketBillingInfo()) {
						ObjectBucket objectBucket = objectBuckets.get(bucketBillingInfo.getName());
						
						if(objectBucket != null) {
							// set api type
							bucketBillingInfo.setApiType(objectBucket.getApiType());
						}
					}
					
					// Push collected info into datastore
					if( this.billingDAO != null ) {
						// insert something
						billingDAO.insert(namespaceBillingResponse, collectionTime);
					}
				} else {
					namespaceRequest.setNextMarker(null);
				}
			}			
		}		
	}

	/**
	 * Collects Bucket metadata for all namespace defined on a cluster
	 * @param Date
	 */
	public void getObjectBukcetData( Map<NamespaceBucketKey, ObjectBucket> objectBucketMap) {
		
		collectObjectBukcetData( objectBucketMap,
								 null,            // no collection time required
								 null             // no DAO required 
								       );
		
	}
	
	/**
	 * Collects Bucket metadata for all namespace defined on a cluster
	 * @param Date
	 */
	public void collectObjectBukcetData( Date collectionTime ) {
		
		collectObjectBukcetData( null,  // no map required
				 				 collectionTime,    
				 				 this.billingDAO
				       							 );						
	}
	
	/**
	 * Collects Bucket metadata for all namespace defined on a cluster
	 * @param Date
	 */
	private  void collectObjectBukcetData( Map<NamespaceBucketKey, ObjectBucket> objectBucketMap,
										   Date collectionTime, BillingDAO billDAO    ) {
										
		
		// Start collecting billing data from ECS systems
		List<Namespace> namespaceList = getNamespaces();
		
		// At this point we should have all the namespace supported by the ECS system
		
		for( Namespace namespace : namespaceList ) {
			
			//===============================================
			// Initial billing request for current namespace
			//===============================================
			
			NamespaceRequest namespaceRequest = new NamespaceRequest();
			namespaceRequest.setName(namespace.getName());
			ObjectBuckets objectBucketsResponse = client.getNamespaceBucketInfo(namespaceRequest);
			
			if(objectBucketsResponse == null) {
				continue;
			}
			
			logger.info("Collect Billing Data for namespace: " + namespace.getName());
			
			// Push collected info into datastore
			if( billDAO != null ) {
				// insert something
				billDAO.insert(objectBucketsResponse, collectionTime);
			}
			
			// Add to return map
			if( objectBucketsResponse.getObjectBucket() != null && 
				objectBucketMap	!= null                            ) {
				
				for ( ObjectBucket objectBucket : objectBucketsResponse.getObjectBucket()) {
					NamespaceBucketKey key = new NamespaceBucketKey(namespace.getName(), objectBucket.getName());
					objectBucketMap.put(key, objectBucket);
				}
				
			}
			
			// collect n subsequent pages
			while(namespaceRequest.getNextMarker() != null) {
				objectBucketsResponse = client.getNamespaceBucketInfo(namespaceRequest);
				
				if( objectBucketsResponse != null ) {
					namespaceRequest.setNextMarker(objectBucketsResponse.getNextMarker());
					
					// Push collected info into datastore
					if( billDAO != null ) {
						// insert something
						billDAO.insert(objectBucketsResponse, collectionTime);
					}
					
					// Add to return map
					if( objectBucketsResponse.getObjectBucket() != null  && 
					    objectBucketMap	!= null                             ) {
						
						for ( ObjectBucket objectBucket : objectBucketsResponse.getObjectBucket()) {
							NamespaceBucketKey key = new NamespaceBucketKey(namespace.getName(), objectBucket.getName());
							objectBucketMap.put(key, objectBucket);
						}
						
					}
					
				} else {
					// stop the loop
					namespaceRequest.setNextMarker(null);
				}
			}			
		}
		
	}
	
	
	public void shutdown() {
		if(this.client != null) {
			client.shutdown();
		}
		
	}		
	
	/**
	 *  Gathers all namespaces present on a cluster
	 * @return List<Namespace>
	 */
	public List<Namespace> getNamespaces() {

		// Start collecting billing data from ECS systems
		List<Namespace> namespaceList = new ArrayList<Namespace>();

		// collect namespace names
		ListNamespaceRequest listNamespaceRequest = new ListNamespaceRequest();

		// first batch
		ListNamespacesResult namespacesResult = client.listNamespaces(listNamespaceRequest);
		namespaceList.addAll(namespacesResult.getNamespaces());

		// n subsequent batches
		while(namespacesResult.getNextMarker() != null) {

			listNamespaceRequest.setNextMarker(namespacesResult.getNextMarker());

			namespacesResult =  client.listNamespaces(listNamespaceRequest);

			if(namespacesResult.getNamespaces() != null) {
				namespaceList.addAll(namespacesResult.getNamespaces());
			}
		}

		return namespaceList;
	}
	
}
