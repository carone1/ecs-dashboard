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
import java.util.List;

import javax.ws.rs.core.Response;

import com.emc.ecs.management.entity.ListNamespaceRequest;
import com.emc.ecs.management.entity.ListNamespacesResult;
import com.emc.ecs.management.entity.NamespaceBillingInfo;
import com.emc.ecs.management.entity.NamespaceRequest;
import com.emc.ecs.management.entity.ObjectBuckets;
import com.emc.ecs.management.entity.ObjectUserSecretKeys;
import com.emc.ecs.management.entity.ObjectUsers;
import com.emc.ecs.management.entity.ObjectUsersRequest;
import com.emc.rest.smart.LoadBalancer;
import com.emc.rest.smart.SmartClientFactory;
import com.emc.rest.smart.SmartConfig;
import com.emc.rest.smart.ecs.EcsHostListProvider;
import com.emc.rest.smart.ecs.Vdc;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;




public class ManagementClient {

	private static final Integer HOST_LIST_PROVIDER_PORT                = 4440;
	
	private static final String X_SDS_AUTH_TOKEN     					= "X-SDS-AUTH-TOKEN";
	private static final String REST_LOGIN           					= "/login";
	private static final String REST_LOGOUT          					= "/logout";
	private static final String REST_LIST_NAMESPACES 					= "/object/namespaces";
	private static final String REST_GET_OBJECT_USERS					= "/object/users";
	private static final String REST_GET_KEYS_FOR_USERS 				= "/object/user-secret-keys/";
	private static final String REST_BILLING_NAMESPACES_FIRST 			= "/object/billing/namespace/";
	private static final String REST_BILLING_NAMESPACES_SECOND 			= "/info";
	private static final String REST_BILLING_NAMESAPCES_BUCKET_INCLUDED = "include_bucket_detail";
	private static final String REST_OBJECT_BUCKET 						= "/object/bucket";
	private static final String REST_MARKER_PARAMETER 					= "marker";
	private static final String REST_LIMIT_PARAMETER 					= "limit";
	private static final String REST_NAMESPACE_PARAMETER 				= "namespace";
	
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
										    this.mgmtConfig.getHostList());
	}
	
	//================================
	// Public Methods
	//================================
	/**
	 * 
	 * @return ListNamespacesResult
	 */
	public ListNamespacesResult listNamespaces(ListNamespaceRequest namespaceRequest) {
				
		String authToken = getAuthToken();
						
		WebResource mgmtResource = this.mgmtClient.resource(uri);

		// list namespaces
		WebResource listNamespacesResource = mgmtResource.path(REST_LIST_NAMESPACES);
		
		if( namespaceRequest.getNextMarker() != null ) {
			listNamespacesResource = listNamespacesResource.queryParam(REST_MARKER_PARAMETER, 
																		namespaceRequest.getNextMarker());	
		}
		
		ListNamespacesResult listNamespacetResponse = listNamespacesResource.header(X_SDS_AUTH_TOKEN, authToken)
				.get(ListNamespacesResult.class);
						
		return listNamespacetResponse;
		
	}
	
	/**
	 * 
	 * @param namespaceRequest
	 * @return NamespaceBillingInfoResponse
	 */
	public NamespaceBillingInfo getNamespaceBillingInfo(NamespaceRequest namespaceRequest) {
				
		String authToken = getAuthToken();
		
		NamespaceBillingInfo namespaceBillingResponse = null;
						
		WebResource mgmtResource = this.mgmtClient.resource(uri);

		// Call using ?include_bucket_detail=true parameter
		StringBuilder restStr = new StringBuilder();
		restStr.append(REST_BILLING_NAMESPACES_FIRST)
				.append(namespaceRequest.getName())
				.append(REST_BILLING_NAMESPACES_SECOND);
													
		// get billing namespace Billing ressource
		WebResource getNamespaceBillingResource = mgmtResource.path(restStr.toString())
													.queryParam(REST_BILLING_NAMESAPCES_BUCKET_INCLUDED, "true");
		
		// add marker
		if(namespaceRequest.getNextMarker() != null) {
			getNamespaceBillingResource = getNamespaceBillingResource.queryParam(REST_MARKER_PARAMETER, 
																				namespaceRequest.getNextMarker());			
		}
		
		try {
			
			namespaceBillingResponse = getNamespaceBillingResource.header(X_SDS_AUTH_TOKEN, authToken)
																.get(NamespaceBillingInfo.class);
			
		} catch (UniformInterfaceException ex) {
			// ECS has a bug where an http 400 error is returned if 
			// a namespace doesn't have a bucket but the request has 
			// the include_bucket_detail parameter
			// The workaround is to make the same call but without the parameter
			if( ex.getResponse().getStatusInfo().getStatusCode() == Response.Status.BAD_REQUEST.getStatusCode() ) {				
												
				//System.out.println("getNamespaceBillingResource: " + restStr.toString());
				
				// get billing namespace Billing ressource
				getNamespaceBillingResource = mgmtResource.path(restStr.toString());
															
				
				if(namespaceRequest.getNextMarker() != null) {
					getNamespaceBillingResource = getNamespaceBillingResource.queryParam(REST_MARKER_PARAMETER, 
																						namespaceRequest.getNextMarker());			
				}
				
				namespaceBillingResponse = getNamespaceBillingResource.header(X_SDS_AUTH_TOKEN, authToken)
																		.get(NamespaceBillingInfo.class);
			}
		}								
		
		return namespaceBillingResponse;
	}
	
	/**
	 * 
	 * @param namespaceRequest
	 * @return ObjectBucketsResponse
	 */
	public ObjectBuckets getNamespaceBucketInfo(NamespaceRequest namespaceRequest) {
		
		String authToken = getAuthToken();
						
		WebResource mgmtResource = this.mgmtClient.resource(uri);

		StringBuilder restStr = new StringBuilder();
		restStr.append(REST_OBJECT_BUCKET);												
		
		// get billing namespace Billing ressource
		WebResource getNamespaceBucketInfoResource = 
					mgmtResource.path(restStr.toString())
								.queryParam(REST_NAMESPACE_PARAMETER, namespaceRequest.getName());						
		
		// add marker
		if(namespaceRequest.getNextMarker() != null) {
			getNamespaceBucketInfoResource = getNamespaceBucketInfoResource.queryParam(REST_MARKER_PARAMETER, 
																						namespaceRequest.getNextMarker());			
		}
		
		ObjectBuckets namespaceBucketInfoResponse = 
				getNamespaceBucketInfoResource.header(X_SDS_AUTH_TOKEN, authToken).get(ObjectBuckets.class);
						
		return namespaceBucketInfoResponse;
	}
	
	
	/**
	 * Retrieve Object user's uid 
	 * @param objectUsersRequest 
	 * @return ObjectUsers
	 */
	public ObjectUsers getObjectUsersUid(ObjectUsersRequest objectUsersRequest) {
				
		String authToken = getAuthToken();				
		
		WebResource mgmtResource = this.mgmtClient.resource(uri);

		
		// get keys for user
		WebResource objectUsersUidResource = mgmtResource.path(REST_GET_OBJECT_USERS);
		
		// marker parameter
		if(objectUsersRequest.getMarker() != null) {
			objectUsersUidResource = objectUsersUidResource.queryParam( REST_MARKER_PARAMETER, 
																		objectUsersRequest.getMarker());			
		}
		
		// limit parameter
		if(objectUsersRequest.getLimit() != null) {
			objectUsersUidResource = objectUsersUidResource.queryParam( REST_LIMIT_PARAMETER, 
																		String.valueOf(objectUsersRequest.getLimit()));			
		}
		
		ObjectUsers objectUsers = objectUsersUidResource.header(X_SDS_AUTH_TOKEN, authToken)
				.get(ObjectUsers.class);
						
		return objectUsers;
				
	}
	
	/**
	 * Retrieve S3 uid and secret keys
	 * @param uid 
	 * @param namespace
	 * @return ObjectUserSecretKeysResponse
	 */
	public ObjectUserSecretKeys getObjectUserSecretKeys(String uid, String namespace) {
				
		String authToken = getAuthToken();
						
		WebResource mgmtResource = this.mgmtClient.resource(uri);

		String restPath = REST_GET_KEYS_FOR_USERS + uid + "/" + namespace;
		// get keys for user
		WebResource getUserSecretKeysResource = mgmtResource.path(restPath);
		
		ObjectUserSecretKeys userSecretKeys = null;
		
		try {
			userSecretKeys = getUserSecretKeysResource.header(X_SDS_AUTH_TOKEN, authToken)
																					.get(ObjectUserSecretKeys.class);
		} catch (UniformInterfaceException ex) {
			// ECS returns http 404 error if 
			// a user doesn't have a S3 password configured 			
			// The workaround is just to generate an empty reponse
			if( ex.getResponse().getStatusInfo().getStatusCode() == Response.Status.NOT_FOUND.getStatusCode() ) {
				userSecretKeys = new ObjectUserSecretKeys();
			}
		}
								
		return userSecretKeys;		
	}
	
	/**
	 * Shutdown the management client
	 */
    public void shutdown() {
    	if( this.mgmtClient != null) {
    		logout();
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
                     
        // Check for sucsess
        int statusCode = loginResponse.getStatusInfo().getStatusCode();
        if( statusCode != Status.OK.getStatusCode() ) {
        	String errorMessage = "Login to " + this.uri + REST_LOGIN + " failed" + " Server returned: " + statusCode;
        	throw new RuntimeException(errorMessage);
        }
        	        
        String authToken = loginResponse.getHeaders().getFirst(X_SDS_AUTH_TOKEN);
				
		if(authToken != null) {
			this.mgmtAuthToken = authToken;			
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
	private static Client createMgmtClient(String userName, String secretKey, List<String> ipAddresses ) {
		
		String[] ips = (String[])ipAddresses.toArray();
	    SmartConfig smartConfig = new SmartConfig(ips);
	    
	    
	    LoadBalancer loadBalancer = smartConfig.getLoadBalancer();

	    // creates a standard (non-load-balancing) jersey client
	    Client pollClient = SmartClientFactory.createStandardClient(smartConfig);

	    // create a host list provider based on the endpoint call (will use the standard client we just made)
	    EcsHostListProvider hostListProvider = new EcsHostListProvider(pollClient, loadBalancer,
	            userName, secretKey);

	    hostListProvider.setProtocol("http");
	    hostListProvider.setPort(HOST_LIST_PROVIDER_PORT);
	    hostListProvider.withVdcs(new Vdc(ips));

	    smartConfig.setHostListProvider(hostListProvider);
	    
	    // health check disabled as there seems to be an issue with the smart client and ping messages
	    smartConfig.setHealthCheckEnabled(true);
	    smartConfig.setHostUpdateEnabled(true);

	    return SmartClientFactory.createSmartClient(smartConfig);
	}

	
}
