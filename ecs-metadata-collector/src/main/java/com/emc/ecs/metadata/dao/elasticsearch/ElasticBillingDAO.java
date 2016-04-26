package com.emc.ecs.metadata.dao.elasticsearch;


import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class ElasticBillingDAO {

	
	public ElasticBillingDAO() {
		
		try {
			Settings settings = Settings.settingsBuilder()
			        .put("client.transport.sniff", true).build();
			TransportClient client = TransportClient.builder().settings(settings).build()					
			        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("host1"), 9300))
			        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("host2"), 9300));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
