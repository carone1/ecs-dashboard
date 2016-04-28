package com.emc.ecs.metadata.dao.elasticsearch;


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

import com.emc.ecs.management.entity.NamespaceBillingInfo;
import com.emc.ecs.management.entity.ObjectBuckets;
import com.emc.ecs.metadata.dao.BillingDAO;
import com.emc.ecs.metadata.dao.elasticsearch.entity.ElasticNamespaceBillingInfo;

public class ElasticBillingDAO implements BillingDAO {

	private final static String CLIENT_SNIFFING_CONFIG = "client.transport.sniff";
	private final static String CLIENT_CLUSTER_NAME_CONFIG = "cluster.name";
	
	private TransportClient client;
	
	
	public ElasticBillingDAO(ElasticBillingDAOConfig config) {
		
		try {
			
			Builder builder = Settings.settingsBuilder();
			
			// Check for new hosts within the cluster
			builder.put(CLIENT_SNIFFING_CONFIG, true);
			
			// specify cluster name
			if( config.getClusterName() != null ) {
				builder.put(CLIENT_CLUSTER_NAME_CONFIG, config.getClusterName());
			}
			
			Settings settings = builder.build();
			
			// create client
			client = TransportClient.builder().settings(settings).build();
			
			// add hosts
			for( String elasticHost : config.getHosts()) {
				client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(elasticHost), config.getPort()));				
			}
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}

	@Override
	public void insert( NamespaceBillingInfo billingData ) {
				
		// Generate JSON data
		XContentBuilder namespaceBuilder = ElasticNamespaceBillingInfo.toJsonFormat(billingData);
		
		try {
			System.out.println(namespaceBuilder.prettyPrint().string());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// push data into datastore
		//client.prepareIndex().setSource("ecs", "namespace-billing").setSource(namespaceBuilder).get();
	}

	@Override
	public void insert( ObjectBuckets bucketResponse ) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
