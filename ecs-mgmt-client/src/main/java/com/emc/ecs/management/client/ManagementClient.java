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


package com.emc.ecs.management.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.Response;

import com.emc.ecs.management.entity.BucketOwner;
import com.emc.ecs.management.entity.ListNamespaceRequest;
import com.emc.ecs.management.entity.ListNamespacesResult;
import com.emc.ecs.management.entity.NamespaceBillingInfo;
import com.emc.ecs.management.entity.NamespaceDetail;
import com.emc.ecs.management.entity.NamespaceQuota;
import com.emc.ecs.management.entity.NamespaceRequest;
import com.emc.ecs.management.entity.ObjectBuckets;
import com.emc.ecs.management.entity.ObjectUserSecretKeys;
import com.emc.ecs.management.entity.ObjectUsers;
import com.emc.ecs.management.entity.ObjectUsersRequest;
import com.emc.ecs.management.entity.VdcDetails;
import com.emc.rest.smart.LoadBalancer;
import com.emc.rest.smart.SmartClientFactory;
import com.emc.rest.smart.SmartConfig;
import com.emc.rest.smart.ecs.EcsHostListProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;




public class ManagementClient {

	//================================
	// Final Members
	//================================
	private static final Integer HOST_LIST_PROVIDER_PORT                = 9020;
	
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
	private static final String REST_QUOTA_NAMESPACES_FIRST 			= "/object/namespaces/namespace/";
	private static final String REST_QUOTA_NAMESPACES_SECOND			= "/quota";
	private static final String REST_ALL_VDC							= "/object/vdcs/vdc/list";
	private static final String REST_ALL_BUCKET_KEYS					= "/diagnostic/RT/0/DumpAllKeys/BUCKET_KEY/";
	private static final String REST_ALL_BUCKET_VALUE_PARAMETER			= "showvalue";
	private static final String REST_ALL_BUCKET_STYLE_PARAMETER			= "useStyle";
	private static final String REST_ALL_BUCKET_KEY_PARAMETER			= "bucketId";
	
	
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
			// using a bogus host as the smart client will replace with a verified healthy host
			// from the configured list
			this.uri = new URI("https://" + "somehost.com" + ":" + this.mgmtConfig.getPort());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e.getLocalizedMessage());
		}		
		
		mgmtClient = createMgmtClient( this.mgmtConfig.getHostList()  );
	}
	
	//================================
	// Public Methods
	//================================
	/**
	 * lists namespaces 
	 * @param namespaceRequest - namespace request 
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
	 * Returns Billing Namespace info 
	 * @param namespaceRequest - namespace request
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
		
		System.out.println("getNamespaceBillingInfo rest request: " + restStr.toString());
		
		WebResource getNamespaceBillingResource;
		
		// get billing namespace Billing ressource
		if(namespaceRequest.getIncludeBuckets()) {
			// include bucket data
			getNamespaceBillingResource = mgmtResource.path(restStr.toString())
											.queryParam(REST_BILLING_NAMESAPCES_BUCKET_INCLUDED, "true");
		} else {
			// just namespace data without bucket details
			getNamespaceBillingResource = mgmtResource.path(restStr.toString());
		}
		
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
												
				System.out.println("getNamespaceBillingResource: " + restStr.toString());
				
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
	 * Returns billing bucket specific info
	 * @param namespaceRequest - namespace request
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
	 * @param objectUsersRequest - object users request
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
	 * @param uid - user id
	 * @param namespace - namespace 
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
	 * Retrieve S3 uid and secret keys
	 * @param uid - user id
	 * @param namespace - namespace
	 * @return ObjectUserSecretKeysResponse
	 */
	public ObjectUserSecretKeys getHostsInVDC(String uid, String namespace) {
				
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
	 * Returns namespace specific details
	 * @return NamespaceDetail
	 */
	public NamespaceDetail getNamespaceDetails(String namespaceid) {
		String authToken = getAuthToken();
		WebResource mgmtResource = this.mgmtClient.resource(uri);
		StringBuilder restStr = new StringBuilder();
		restStr.append(REST_LIST_NAMESPACES);
		restStr.append("/namespace/");
		restStr.append(namespaceid);
		// Get Namespace Detail Ressource
		WebResource getNamespaceDetailResource = mgmtResource.path(restStr.toString());
		NamespaceDetail namespaceDetailResponse = getNamespaceDetailResource.header(X_SDS_AUTH_TOKEN, authToken)
				.get(NamespaceDetail.class);
		return namespaceDetailResponse;
	}
	
	/**
	 * Returns namespace specific details
	 * @return NamespaceDetail
	 */
	public NamespaceQuota getNamespaceQuota(NamespaceRequest namespaceRequest) {
		String authToken = getAuthToken();
		WebResource mgmtResource = this.mgmtClient.resource(uri);
		StringBuilder restStr = new StringBuilder();
		restStr.append(REST_QUOTA_NAMESPACES_FIRST)
		.append(namespaceRequest.getName())
		.append(REST_QUOTA_NAMESPACES_SECOND);
		// Get Namespace Detail Ressource
		WebResource getNamespaceDetailResource = mgmtResource.path(restStr.toString());
		NamespaceQuota namespaceQuotaResponse = getNamespaceDetailResource.header(X_SDS_AUTH_TOKEN, authToken)
				.get(NamespaceQuota.class);
		return namespaceQuotaResponse;
	}
	
	/**
	 * Returns VDC details list
	 * @return
	 */
	public VdcDetails getVdcDetails() {
		String authToken = getAuthToken();
		WebResource mgmtResource = this.mgmtClient.resource(uri);
		StringBuilder restStr = new StringBuilder();
		restStr.append(REST_ALL_VDC);
		// Get Namespace Detail Ressource
		WebResource getNamespaceDetailResource = mgmtResource.path(restStr.toString());
		VdcDetails vdcDetailsResponse = getNamespaceDetailResource.header(X_SDS_AUTH_TOKEN, authToken)
				.get(VdcDetails.class);
		return vdcDetailsResponse;
	}
	
	/**
	 * returns bucket owner list
	 * @return
	 */
	public List<BucketOwner> getBucketOwner() {
		String authToken = getAuthToken();
		final WebResource mgmtResource = this.mgmtClient.resource(uri);
		StringBuilder restStr = new StringBuilder();
		restStr.append(REST_ALL_BUCKET_KEYS);
		// Get Namespace Detail Ressource
		WebResource getNamespaceDetailResource = mgmtResource.path(restStr.toString());
		String bucketKeyReponse = getNamespaceDetailResource.header(X_SDS_AUTH_TOKEN, authToken).get(String.class);
		final List<BucketOwner> bucketOwners = new ArrayList<>();
		final Map<String, List<String>> urlBucketKeysmap = getUrlBucketKeyMap(bucketKeyReponse);
		urlBucketKeysmap.forEach((url, bucketKeys) -> {
			bucketKeys.forEach( bucketKey -> {
				WebResource getBucketDetailsResource = mgmtResource.path(url)
						.queryParam(REST_ALL_BUCKET_KEY_PARAMETER, bucketKey)
						.queryParam(REST_ALL_BUCKET_VALUE_PARAMETER, "gpb")
						.queryParam(REST_ALL_BUCKET_STYLE_PARAMETER, "raw");
				bucketOwners.add(getBucketOwnerFromString(bucketKey, getBucketDetailsResource.get(String.class))); 
			} );
		});
		return bucketOwners;
	}
	
	/**
	 * 
	 * @param response
	 * @return
	 */
	private BucketOwner getBucketOwnerFromString(String bucketKey, String response) {
		final String OWNER_SHIP = "ownerzone";
		final String TEXT_VALUE = "textValue:";
		final String DOUBLE_QUOTES = "\"";
		final String COLON = ":";
		final Pattern pattern = Pattern.compile("(?s)" + OWNER_SHIP + "\\s*" 
		+ TEXT_VALUE + "\\s*" + DOUBLE_QUOTES + "(.*?)" + COLON + "(.*?)" + COLON + "(.*?)" + COLON + "(.*?)" + DOUBLE_QUOTES );
		final Matcher matcher = pattern.matcher(response);
		String vdcId = null;
		while (matcher.find()) {
			vdcId = matcher.group(4);
		}
		BucketOwner bucketOwner = new BucketOwner(vdcId, bucketKey);
		return bucketOwner;
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
	 * @throws RuntimeException - run time exception
	 */
	protected void login() {
		
		WebResource mgmtResource = this.mgmtClient.resource(this.uri);
		
		// login
		WebResource loginResource = mgmtResource.path(REST_LOGIN);
		loginResource.addFilter(new HTTPBasicAuthFilter(this.mgmtConfig.getMgmtUsername(), this.mgmtConfig.getMgmtSecretKey()));
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
	 * @param userName - username 
	 * @param secretKey- secret key
	 * @param port - port
	 * @param ipAddresses - ip addresses
	 * @return Client
	 */
	private Client createMgmtClient( List<String> ipAddresses ) {
		
		String[] ips = (String[])ipAddresses.toArray();
	    SmartConfig smartConfig = new SmartConfig(ips);
	    
	    
	    // creates a standard (non-load-balancing) jersey client
	    Client pollClient = SmartClientFactory.createStandardClient(smartConfig);
	    
	    	    
	    LoadBalancer loadBalancer = smartConfig.getLoadBalancer();
	    
	    // create a host list provider based on the endpoint call (will use the standard client we just made)
	    EcsHostListProvider hostListProvider = new EcsHostListProvider( pollClient, loadBalancer,
	    		                                                        "", "");

	    hostListProvider.setProtocol("http");
	    hostListProvider.setPort(HOST_LIST_PROVIDER_PORT);
	    //hostListProvider.withVdcs(new Vdc(ips));

	    smartConfig.setHostListProvider(hostListProvider);
	    
	    // health check disabled as there seems to be an issue with the smart client and ping messages
	    smartConfig.setHealthCheckEnabled(true);
	    smartConfig.setHostUpdateEnabled(false);

	    return SmartClientFactory.createSmartClient(smartConfig);
	}
	
	/**
	 * 
	 * @param response
	 * @return
	 */
	private Map<String, List<String>> getUrlBucketKeyMap(String response) {
		final Map<String, List<String>> urlBucketMap = new HashMap<>();
		final String HTTP_PREFIX = "http://";
		final String EOF = "\n";
		final String SCHEMA_TYPE = "schemaType";
		final Pattern pattern = Pattern.compile("(?s)" + HTTP_PREFIX + "\\s*(.*?)(?=\\s*" + HTTP_PREFIX + "|$)");
		final Matcher matcher = pattern.matcher(response);
		while (matcher.find()) {
			List<String> arrays = Arrays.asList((HTTP_PREFIX + matcher.group(1)).split(EOF));
			String key = "";
			final List<String> values = new ArrayList<>();
			for(String array : arrays) {
				if (array.startsWith(HTTP_PREFIX)) {
					key = array;
				} else if (array.trim().startsWith(SCHEMA_TYPE)) {
					String bucketId = array.trim().split("\\s+")[3];
					values.add(bucketId);
				}
			}
			urlBucketMap.put(key, values);
		}
		return urlBucketMap;
	}

}
