package com.emc.ecs.metadata.dao.elasticsearch;



import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

import com.emc.ecs.metadata.dao.ObjectDAO;
import com.emc.ecs.metadata.dao.elasticsearch.entity.ElasticS3Object;
import com.emc.object.s3.bean.ListObjectsResult;
import com.emc.object.s3.bean.S3Object;



public class ElasticS3ObjectDAO implements ObjectDAO {

	
	private final static String CLIENT_SNIFFING_CONFIG     = "client.transport.sniff";
	private final static String CLIENT_CLUSTER_NAME_CONFIG = "cluster.name";
	private final static String S3_OBJECT_INDEX_NAME       = "ecs-s3-object";
	private final static String S3_OBJECT_INDEX_TYPE       = "object-info";
	
	
	//=========================
	// Private members
	//=========================
	private TransportClient client;
	
	
	//=========================
	// Public methods
	//=========================
	public ElasticS3ObjectDAO( ElasticDAOConfig config ) {
		
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
	public void insert(ListObjectsResult listObjectsResult, Date collectionTime) {
		
		// Generate JSON for object buckets info
		for( S3Object s3Object : listObjectsResult.getObjects() ) {
			XContentBuilder s3ObjectBuilder = ElasticS3Object.toJsonFormat(s3Object, collectionTime);
			client.prepareIndex(S3_OBJECT_INDEX_NAME, S3_OBJECT_INDEX_TYPE).setSource(s3ObjectBuilder).get();
		}
	}
	
	
	
}
