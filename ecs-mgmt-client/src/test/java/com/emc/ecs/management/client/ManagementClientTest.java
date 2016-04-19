package com.emc.ecs.management.client;

import java.util.Arrays;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.emc.ecs.management.entity.ListNamespacesResult;
import com.emc.ecs.management.entity.Namespace;
import com.emc.ecs.management.entity.NamespaceBillingInfo;
import com.emc.ecs.management.entity.UserSecretKeys;


public class ManagementClientTest {

	
    private static final Logger l4j = Logger.getLogger(ManagementClientTest.class);
    private ManagementClient client;
   

    @Before
    public void initClient() throws Exception {
        client = new ManagementClient(createMgmtConfig());
    }

    @After
    public void shutdownClient() {
        if (client != null) client.shutdown();
    }
    
//    @Test
//    public void testListNamespaces() throws Exception {
//    	        	
//    	ListNamespacesResult namespacesReponse = client.listNamespaces();
//        Assert.assertNotNull(namespacesReponse.getNamespaces());
//        for( Namespace namespace : namespacesReponse.getNamespaces() ) {
//        	System.out.println("namespace: " + namespace.getName());
//        }
//        
//    }	  
//    
//    @Test
//    public void testGetUserKeys() throws Exception {
//    	        	
//    	UserSecretKeys userSecretKeys = client.getUserSecretKeys("eric-caron", "eric-caron");
//        Assert.assertNotNull(userSecretKeys);
//        System.out.println("Get user secret key");
//        System.out.println("SecretKey1: " + userSecretKeys.getSecretKey1());
//        System.out.println("SecretTimestamp: " + userSecretKeys.getKeyTimestamp1());
//        
//    }
	
    @Test
    public void namespaceBillingInfo() throws Exception {
      	ListNamespacesResult namespacesReponse = client.listNamespaces();
        Assert.assertNotNull(namespacesReponse.getNamespaces());
        for( Namespace namespace : namespacesReponse.getNamespaces() ) {
        	    		
        	System.out.println("namespace: " + namespace.getName());
        	
        	NamespaceBillingInfo namespaceBillingInfo = client.getNamespaceBillingInfo(namespace.getName(), null);
        	Assert.assertNotNull(namespaceBillingInfo);
        	System.out.println("Namespace: " + namespaceBillingInfo.getNamespace());
        	System.out.println("Number of objects: " + namespaceBillingInfo.getTotalObjects());
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
