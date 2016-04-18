/*
 * Copyright (c) 2015, EMC Corporation.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *     + Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     + Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     + The name of EMC Corporation may not be used to endorse or promote
 *       products derived from this software without specific prior written
 *       permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.emc.ecs.management.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;


import com.emc.ecs.management.entity.ListNamespacesResult;
import com.emc.ecs.management.entity.NamespaceBillingInfo;
import com.emc.ecs.management.entity.UserSecretKeys;
import com.emc.rest.smart.LoadBalancer;
import com.emc.rest.smart.SmartClientFactory;
import com.emc.rest.smart.SmartConfig;
import com.emc.rest.smart.ecs.EcsHostListProvider;
import com.emc.rest.smart.ecs.Vdc;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;



public class ManagementClient {

	
	private static final String X_SDS_AUTH_TOKEN     = "X-SDS-AUTH-TOKEN";
	private static final String REST_LOGIN           = "/login";
	private static final String REST_LOGOUT          = "/logout";
	private static final String REST_LIST_NAMESPACES = "/object/namespaces";
	private static final String REST_GET_KEYS_FOR_USERS = "/object/user-secret-keys/";
	private static final String REST_BILLING_NAMESPACES_FIRST = "/object/billing/namespace/";
	private static final String REST_BILLING_NAMESPACES_SECOND = "/info";
	private static final String REST_BILLING_NAMESAPCES_BUCKET_INCLUDED = "?include_bucket_detail=true";
	private static final String REST_MARKER = "?marker=";
	
	//================================
	// Private Members
	//================================
	private ManagementClientConfig  mgmtConfig;
	private String 					mgmtAuthToken;
	private Client					mgmtClient;
	private URI						uri;
	
	//================================
	// Constructor
	//================================
	public ManagementClient(ManagementClientConfig mgmtConfig){
		this.mgmtConfig = mgmtConfig;
		try {
			this.uri = new URI("https://" + this.mgmtConfig.getHostList().get(0) + ":" + this.mgmtConfig.getPort());
		} catch (URISyntaxException e) {
			// TODO add error log
			e.printStackTrace();
		}
		this.mgmtClient = createMgmtClient( this.mgmtConfig.getUsername(), 
										    this.mgmtConfig.getSecretKey(), 
										    this.mgmtConfig.getPort(),
										    this.mgmtConfig.getHostList());
	}
	
	//================================
	// Public Methods
	//================================
	public ListNamespacesResult listNamespaces() {
		
		System.out.println("list namespaces");
		String authToken = getAuthToken();
		
		System.out.println(authToken);
		
		WebResource mgmtResource = this.mgmtClient.resource(uri);

		// list namespaces
		WebResource listNamespacesResource = mgmtResource.path(REST_LIST_NAMESPACES);
		
		
		ListNamespacesResult listNamespacetResponse = listNamespacesResource.header(X_SDS_AUTH_TOKEN, authToken)
				.get(ListNamespacesResult.class);
		
		// release the auth token
		logout();
		
		return listNamespacetResponse;
		
	}
	
	public NamespaceBillingInfo getNamespaceBillingInfo(String namespace, String marker) {
		
		
		String authToken = getAuthToken();
						
		WebResource mgmtResource = this.mgmtClient.resource(uri);

		StringBuilder restStr = new StringBuilder();
		restStr.append(REST_BILLING_NAMESPACES_FIRST)
				.append(namespace)
				.append(REST_BILLING_NAMESPACES_SECOND)
				.append(REST_BILLING_NAMESAPCES_BUCKET_INCLUDED);
		
		if(marker != null) {
			restStr.append(REST_MARKER)
					.append(marker);			
		}
		
		// list namespaces
		WebResource getNamespaceBillingResource = mgmtResource.path(restStr.toString());
		
		
		NamespaceBillingInfo namespaceBillingResponse = getNamespaceBillingResource.header(X_SDS_AUTH_TOKEN, authToken)
				.get(NamespaceBillingInfo.class);
		
		// release the auth token
		logout();
		
		return namespaceBillingResponse;
	}
	
	
	
	/**
	 * Retrieve S3 uid and secret keys
	 * @param uid
	 * @param namespace
	 * @return
	 */
	public UserSecretKeys getUserSecretKeys(String uid, String namespace) {
		
		
		String authToken = getAuthToken();
		
		//System.out.println(authToken);
		
		WebResource mgmtResource = this.mgmtClient.resource(uri);

		String restPath = REST_GET_KEYS_FOR_USERS + uid + "/" + namespace;
		// get keys for user
		WebResource listNamespacesResource = mgmtResource.path(restPath);
		
		
		UserSecretKeys userSecretKeys = listNamespacesResource.header(X_SDS_AUTH_TOKEN, authToken)
				.get(UserSecretKeys.class);
		
		// release the auth token
		logout();
		
		return userSecretKeys;
		
		
	}
	
    public void shutdown() {
    	if( this.mgmtClient != null) {
    		SmartClientFactory.destroy(this.mgmtClient);
    	}
    }
	
	
	//================================
	// Private Methods
	//================================
	private String getAuthToken() {
		if(this.mgmtAuthToken == null){
			login();
		}
		return this.mgmtAuthToken;
	}
	
	
	/**
	 * Login using admin username and secretKey 
	 * returned authentication token is stored internally
	 * @throws URISyntaxException 
	 */
	protected void login() {
		
		WebResource mgmtResource = this.mgmtClient.resource(this.uri);
		
		// login
		WebResource loginResource = mgmtResource.path(REST_LOGIN);
		loginResource.addFilter(new HTTPBasicAuthFilter(this.mgmtConfig.getUsername(), this.mgmtConfig.getSecretKey()));
		ClientResponse loginResponse = loginResource.get(ClientResponse.class);
      
        System.out.println(loginResponse.toString());
        
        // Check for sucsess
        int statusCode = loginResponse.getStatusInfo().getStatusCode();
        if( statusCode != Status.OK.getStatusCode() ) {
        	String errorMessage = "Login to " + this.uri + REST_LOGIN + " failed" + " Server returned: " + statusCode;
        	throw new RuntimeException(errorMessage);
        }
        	        
        String authToken = loginResponse.getHeaders().getFirst(X_SDS_AUTH_TOKEN);
				
		if(authToken != null) {
			this.mgmtAuthToken = authToken;
			System.out.println("authToken: " + authToken);
		} else {
			String errorMessage = "Login to " + this.uri + " ok but Server did not return  " + X_SDS_AUTH_TOKEN + 
					              "in response header";
        	throw new RuntimeException(errorMessage);
		}
	}
	
	/**
	 * Login using admin username and secretKey 
	 * returned authentication token is stored internally
	 * @throws URISyntaxException 
	 */
	protected void logout() {
		
		if(this.mgmtAuthToken != null) {
			WebResource mgmtResource = this.mgmtClient.resource(uri);

			// logout
			WebResource logoutResource = mgmtResource.path(REST_LOGOUT);

			ClientResponse logoutResponse = logoutResource.header(X_SDS_AUTH_TOKEN, this.mgmtAuthToken)
					.get(ClientResponse.class);
														
		    // Check for sucsess
	        int statusCode = logoutResponse.getStatusInfo().getStatusCode();
	        if( statusCode != Status.OK.getStatusCode() ) {
	        	String errorMessage = "Login to " + this.uri + REST_LOGOUT + " failed" + " Server returned: " + statusCode;
	        	throw new RuntimeException(errorMessage);
	        }
			
			this.mgmtAuthToken = null;
		}

	}
	
	
	/**
	 * Factory method to create smart rest ECS client
	 * @param userName
	 * @param secretKey
	 * @param port
	 * @param ipAddresses
	 * @return Client
	 */
	private static Client createMgmtClient(String userName, String secretKey, int port, List<String> ipAddresses ) {
		
		String[] ips = (String[])ipAddresses.toArray();
	    SmartConfig smartConfig = new SmartConfig(ips);
	    
	    
	    LoadBalancer loadBalancer = smartConfig.getLoadBalancer();

	    // creates a standard (non-load-balancing) jersey client
	    Client pollClient = SmartClientFactory.createStandardClient(smartConfig);

	    // create a host list provider based on the endpoint call (will use the standard client we just made)
	    EcsHostListProvider hostListProvider = new EcsHostListProvider(pollClient, loadBalancer,
	            userName, secretKey);

	    hostListProvider.setProtocol("https");
	    hostListProvider.setPort(port);
	    hostListProvider.withVdcs(new Vdc(ips));

	    smartConfig.setHostListProvider(hostListProvider);

	    return SmartClientFactory.createSmartClient(smartConfig);
	}
	
	
//	public static void main(String[] args) throws Exception {
//				
//		
//		String adminUsername = "eric-caron-admin";
//		String adminPassword = "Nord99sud";
//		
//		//String host = "10.1.83.51";
//		String host = "localhost";
//		
//		// create smart client
//		Client mgmtClient = createMgmtClient(adminUsername, adminPassword, 4443, Arrays.asList(host));				
//		
//		// uri
//        WebResource mgmtResource = mgmtClient.resource(new URI("https://" + host + ":4443"));
//        
//        // login
//        WebResource loginResource = mgmtResource.path("/login");
//        loginResource.addFilter(new HTTPBasicAuthFilter(adminUsername, adminPassword));
//        ClientResponse loginResponse = loginResource.get(ClientResponse.class);
//        
//        System.out.println(loginResponse.toString());
//        
//        String authToken = loginResponse.getHeaders().getFirst("X-SDS-AUTH-TOKEN");
//
//        System.out.println("auth-token: `" + authToken + "`");
//        
//        // Other XMl requests
//        //MyObject myObject = mgmtResource.path("/foo/bar").get(MyObject.class);
//                
//        
//       // Object namespacesResponse = mgmtResource.path("/object/namespaces")
//       // 		.header("X-SDS-AUTH-TOKEN", authToken)
//       // 		.get(Object.class);
//        
//
//       // System.out.println(namespacesResponse.toString() );	
//        
//        
//        // logout
//        WebResource logoutResource = mgmtResource.path("/logout");
//        //loginResource.addFilter(new HTTPBasicAuthFilter(adminUsername, adminPassword));
//        ClientResponse logoutResponse = logoutResource.header("X-SDS-AUTH-TOKEN", authToken).get(ClientResponse.class);
//        
//        System.out.println(logoutResponse.toString()); 
//	}	

	

	
}
