package com.emc.ecs.management.client;


import java.util.Arrays;
import java.util.Properties;

//import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.emc.ecs.management.entity.ListNamespaceRequest;
import com.emc.ecs.management.entity.ListNamespacesResult;
import com.emc.ecs.management.entity.Namespace;
import com.emc.ecs.management.entity.NamespaceBillingInfo;
import com.emc.ecs.management.entity.NamespaceRequest;
import com.emc.ecs.management.entity.ObjectBucket;
import com.emc.ecs.management.entity.ObjectBuckets;
import com.emc.ecs.management.entity.ObjectUser;
import com.emc.ecs.management.entity.ObjectUserSecretKeys;
import com.emc.ecs.management.entity.ObjectUsers;
import com.emc.ecs.management.entity.ObjectUsersRequest;


public class ManagementClientTest {

	
    //private static final Logger l4j = Logger.getLogger(ManagementClientTest.class);
    private ManagementClient client;
   

    @Before
    public void initClient() throws Exception {
        client = new ManagementClient(createMgmtConfig());
    }

    @After
    public void shutdownClient() {
        if (client != null) client.shutdown();
    }
    
    @Test
    public void testListNamespaces() throws Exception {
    	
    	ListNamespaceRequest namespaceRequest = new ListNamespaceRequest();
    	ListNamespacesResult namespacesReponse = client.listNamespaces(namespaceRequest);
        Assert.assertNotNull(namespacesReponse.getNamespaces());
        for( Namespace namespace : namespacesReponse.getNamespaces() ) {
        	System.out.println("namespace: " + namespace.getName());
        }
        
    }	  
 
    @Test
    public void testGetUserUids() throws Exception {

    	ObjectUsersRequest usersRequest = new ObjectUsersRequest();
    	ObjectUsers objectUsers = client.getObjectUsersUid(usersRequest);
    	Assert.assertNotNull(objectUsers);
    	System.out.println("Get users uid");
    	for(ObjectUser objectUser : objectUsers.getBlobUser()) {
    		System.out.println("Namespace: " + objectUser.getNamespace());
    		System.out.println("UID: " + objectUser.getUserId());
    	}

    } 
    
    @Test
    public void testGetUserKeys() throws Exception {
    	   
    	ObjectUsersRequest usersRequest = new ObjectUsersRequest();
    	ObjectUsers objectUsers = client.getObjectUsersUid(usersRequest);
    	Assert.assertNotNull(objectUsers);

    	for(ObjectUser objectUser : objectUsers.getBlobUser()) {
    		System.out.println("Get user secret key user id: " + objectUser.getUserId());
    		ObjectUserSecretKeys userSecretKeys = 
    				client.getObjectUserSecretKeys(objectUser.getUserId().toString(), objectUser.getNamespace().toString());
    		Assert.assertNotNull(userSecretKeys);    		
    		System.out.println("SecretKey1: " + userSecretKeys.getSecretKey1());
    		System.out.println("Secret1Timestamp: " + userSecretKeys.getKeyTimestamp1());
    	}        
    }
	
    @Test
    public void namespaceBillingInfo() throws Exception {

    	ListNamespaceRequest listNamespaceRequest = new ListNamespaceRequest();
    	ListNamespacesResult namespacesReponse = client.listNamespaces(listNamespaceRequest);
    	Assert.assertNotNull(namespacesReponse.getNamespaces());

    	int totalObjects = 0;
    	int totalSize = 0;

    	for( Namespace namespace : namespacesReponse.getNamespaces() ) {
    		
    			System.out.println("namespace: " + namespace.getName());
    			NamespaceRequest namespaceRequest = new NamespaceRequest();
    			namespaceRequest.setName(namespace.getName());        	        	                        	

    			NamespaceBillingInfo namespaceBillingInfoResponse = 
    										client.getNamespaceBillingInfo(namespaceRequest);
    			Assert.assertNotNull(namespaceBillingInfoResponse);

    			System.out.println("namespace: " + namespaceBillingInfoResponse.getNamespace());
    			System.out.println("total size: " + namespaceBillingInfoResponse.getTotalSize());
    			System.out.println("size unit: " + namespaceBillingInfoResponse.getTotalSizeUnit());
    			System.out.println("total objects: " + namespaceBillingInfoResponse.getTotalObjects());
    			totalObjects += namespaceBillingInfoResponse.getTotalObjects();
    			totalSize += namespaceBillingInfoResponse.getTotalSize();
    		
    	}

    	System.out.println("Total objects: " + totalObjects);
    	System.out.println("Total size: " + totalSize);
    }
   
    @Test
    public void namespaceBucketInfo() throws Exception {
    	
    	ListNamespaceRequest listNamespaceRequest = new ListNamespaceRequest();
      	ListNamespacesResult namespacesReponse = client.listNamespaces(listNamespaceRequest);
        Assert.assertNotNull(namespacesReponse.getNamespaces());
        
        for( Namespace namespace : namespacesReponse.getNamespaces() ) {  
        	NamespaceRequest namespaceRequest = new NamespaceRequest();
        	namespaceRequest.setName(namespace.getName());
        	ObjectBuckets bucketsResponse = client.getNamespaceBucketInfo(namespaceRequest);
        	
        	System.out.println("namespace: " + namespace.getName());
        	if( bucketsResponse != null && bucketsResponse.getObjectBucket() != null) {        		
        		for( ObjectBucket objectBucket : bucketsResponse.getObjectBucket() ) {
        			System.out.println("Block Name: " + objectBucket.getName());
        			System.out.println("Api Type: " + objectBucket.getApiType());
        			System.out.println("Bucket Owner: " + objectBucket.getOwner());        			
        		}
        	}
        }
    }
    
    
	private ManagementClientConfig createMgmtConfig() throws Exception {
		Properties props = TestConfig.getProperties("test", true);

		String accessKey = TestConfig.getPropertyNotEmpty(props, TestProperties.MGMT_ACCESS_KEY);
		String secretKey = TestConfig.getPropertyNotEmpty(props, TestProperties.MGMT_SECRET_KEY);
		String hostKey = TestConfig.getPropertyNotEmpty(props, TestProperties.MGMT_HOSTS);
		String[] hostKeys = hostKey.split(",");
		String portKey = TestConfig.getPropertyNotEmpty(props, TestProperties.MGMT_PORT);

		ManagementClientConfig mgmtConfig = new ManagementClientConfig(accessKey, 
																		secretKey,
																		Integer.valueOf(portKey),
																		Arrays.asList(hostKeys));
		return mgmtConfig;
		
	}
	
	
}
