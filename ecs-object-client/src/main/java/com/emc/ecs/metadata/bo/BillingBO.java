package com.emc.ecs.metadata.bo;


import java.util.ArrayList;
import java.util.List;
import com.emc.ecs.management.client.ManagementClient;
import com.emc.ecs.management.client.ManagementClientConfig;
import com.emc.ecs.management.entity.ListNamespaceRequest;
import com.emc.ecs.management.entity.ListNamespacesResult;
import com.emc.ecs.management.entity.Namespace;
import com.emc.ecs.management.entity.NamespaceBillingInfoResponse;
import com.emc.ecs.management.entity.NamespaceRequest;
import com.emc.ecs.metadata.dao.BillingDAO;


public class BillingBO {

	//================================
	// Private members
	//================================
	private ManagementClient client;
	private BillingDAO       billingDAO;
	
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
	 * Collects Billing metadata for all namespace defined on a cluster
	 */
	public void collectBillingData() {
		
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
		
		// At this point we should have all the namespaces supported by the ECS system
		
		for( Namespace namespace : namespaceList ) {
			
			//===============================================
			// Initial billing request for current namespace
			//===============================================
			
			NamespaceRequest namespaceRequest = new NamespaceRequest();
			namespaceRequest.setName(namespace.getName());
			NamespaceBillingInfoResponse namespaceBillingResponse = client.getNamespaceBillingInfo(namespaceRequest);
			
			if(namespaceBillingResponse == null) {
				continue;
			}
			
			// Push collected info into datastore
			if( this.billingDAO != null ) {
				// insert something
				billingDAO.insert(namespaceBillingResponse);
			}
			
			// collect n subsequent pages
			while(namespaceRequest.getNextMarker() != null) {
				namespaceBillingResponse = client.getNamespaceBillingInfo(namespaceRequest);
				
				if( namespaceBillingResponse != null ) {
					namespaceRequest.setNextMarker(namespaceBillingResponse.getNextMarker());
					
					// Push collected info into datastore
					if( this.billingDAO != null ) {
						// insert something
						billingDAO.insert(namespaceBillingResponse);
					}
				} else {
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
	
}
