package com.emc.ecs.metadata.dao.elasticsearch;



import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

import com.emc.ecs.management.entity.BucketBillingInfo;
import com.emc.ecs.management.entity.NamespaceBillingInfo;
import com.emc.ecs.management.entity.ObjectBucket;
import com.emc.ecs.management.entity.ObjectBuckets;
import com.emc.ecs.metadata.dao.BillingDAO;
import com.emc.ecs.metadata.dao.elasticsearch.entity.ElasticBucketBillingInfo;
import com.emc.ecs.metadata.dao.elasticsearch.entity.ElasticNamespaceBillingInfo;
import com.emc.ecs.metadata.dao.elasticsearch.entity.ElasticObjectBucket;

public class ElasticBillingDAO implements BillingDAO {

	private final static String CLIENT_SNIFFING_CONFIG       = "client.transport.sniff";
	private final static String CLIENT_CLUSTER_NAME_CONFIG   = "cluster.name";
	private final static String BILLING_INDEX_NAME           = "ecs-billing";
	private final static String NAMESPACE_BILLING_INDEX_TYPE = "namespace-info";
	private final static String BUCKET_BILLING_INDEX_TYPE    = "bucket-info";
	private final static String BUCKET_INDEX_NAME            = "ecs-bucket";
	private final static String OBJECT_BUCKET_INDEX_TYPE     = "object-bucket";
	
	private TransportClient client;
	
	
	public ElasticBillingDAO(ElasticDAOConfig config) {
		
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
	public void insert( NamespaceBillingInfo billingData, Date collectionTime ) {
				
		// Generate JSON for namespace billing info
		XContentBuilder namespaceBuilder = ElasticNamespaceBillingInfo.toJsonFormat(billingData, collectionTime);				
		client.prepareIndex(BILLING_INDEX_NAME, NAMESPACE_BILLING_INDEX_TYPE).setSource(namespaceBuilder).get();
		
		
		// Generate JSON for namespace billing info
		for(BucketBillingInfo bucketBillingInfo : billingData.getBucketBillingInfo()) {
			XContentBuilder bucketBuilder = ElasticBucketBillingInfo.toJsonFormat(bucketBillingInfo, collectionTime);			
			client.prepareIndex(BILLING_INDEX_NAME, BUCKET_BILLING_INDEX_TYPE).setSource(bucketBuilder).get();			
		}
	}

	@Override
	public void insert( ObjectBuckets objectBuckets, Date collectionTime ) {
		
		// Generate JSON for object buckets info
		for( ObjectBucket objectBucket : objectBuckets.getObjectBucket() ) {
			XContentBuilder objectBucketBuilder = ElasticObjectBucket.toJsonFormat(objectBucket, collectionTime);
			client.prepareIndex(BUCKET_INDEX_NAME, OBJECT_BUCKET_INDEX_TYPE).setSource(objectBucketBuilder).get();
		}		
	}
	
	
	
}
