package com.emc.ecs.metadata.dao.elasticsearch;



import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.ecs.metadata.dao.ObjectDAO;
import com.emc.object.s3.bean.ListObjectsResult;
import com.emc.object.s3.bean.S3Object;



public class ElasticS3ObjectDAO implements ObjectDAO {

	
	private final static String CLIENT_SNIFFING_CONFIG     = "client.transport.sniff";
	private final static String CLIENT_CLUSTER_NAME_CONFIG = "cluster.name";
	public  final static String S3_OBJECT_INDEX_NAME       = "ecs-s3-object";
	public  final static String S3_OBJECT_INDEX_TYPE       = "object-info";
	public  final static String COLLECTION_TIME			   = "collection_time";
	public  final static String ANALYZED_TAG                 = "_analyzed";
	public  final static String NOT_ANALYZED_INDEX           = "not_analyzed";
	public  final static String ANALYZED_INDEX               = "analyzed";
	
	
	public  final static String LAST_MODIFIED_TAG = "last_modified";
	public  final static String SIZE_TAG          = "size";
	public  final static String KEY_TAG           = "key";
	public  final static String OWNER_ID_TAG      = "owner_id";
	public  final static String OWNER_NAME_TAG    = "owner_name";
	public  final static String NAMESPACE_TAG     = "namespace";
	public  final static String BUCKET_TAG        = "bucket";
	
	
	//=========================
	// Private members
	//=========================
	private TransportClient elasticClient;
	private static Logger LOGGER = LoggerFactory.getLogger(ElasticS3ObjectDAO.class);
	
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
			elasticClient = TransportClient.builder().settings(settings).build();
			
			// add hosts
			for( String elasticHost : config.getHosts()) {
				elasticClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(elasticHost), config.getPort()));				
			}
			
			initS3ObjectIndex();
			
		} catch (UnknownHostException e) {
			throw new RuntimeException(e.getLocalizedMessage());
		}				
	}


	@Override
	public void insert(ListObjectsResult listObjectsResult, String namespace, String bucket, Date collectionTime) {
		
		// Generate JSON for object buckets info
		for( S3Object s3Object : listObjectsResult.getObjects() ) {
			XContentBuilder s3ObjectBuilder = toJsonFormat(s3Object, namespace, bucket, collectionTime);
			elasticClient.prepareIndex(S3_OBJECT_INDEX_NAME, S3_OBJECT_INDEX_TYPE).setSource(s3ObjectBuilder).get();
		}
	}
	
	
	//=======================
	// Private methods
	//=======================

	//===========================
	// Billing namespace methods
	//===========================

	private void initS3ObjectIndex() {

		if (elasticClient
				.admin()
				.indices()
				.exists(new IndicesExistsRequest(S3_OBJECT_INDEX_NAME))
				.actionGet()
				.isExists()) {
			// Index already exists no need to re-create it
			return;
		}

		elasticClient.admin().indices().create(new CreateIndexRequest(S3_OBJECT_INDEX_NAME)).actionGet();	

		try {
			PutMappingResponse putMappingResponse = elasticClient.admin().indices()
					.preparePutMapping(S3_OBJECT_INDEX_NAME)
					.setType(S3_OBJECT_INDEX_TYPE)
					.setSource(XContentFactory.jsonBuilder().prettyPrint()
							.startObject()
							.startObject(S3_OBJECT_INDEX_TYPE)
							.startObject("properties")
							// LAST_MODIFIED_TAG
							.startObject( LAST_MODIFIED_TAG ).field("type", "date")
								.field("format", "strict_date_optional_time||epoch_millis").endObject()
							// SIZE_TAG
							.startObject( SIZE_TAG ).field("type", "string").field("type", "long").endObject()
							// KEY_TAG
							.startObject( KEY_TAG ).field("type", "string")
								.field("index", NOT_ANALYZED_INDEX).endObject()
							// KEY_TAG Analyzed
							.startObject( KEY_TAG + ANALYZED_TAG).field("type", "string")
								.field("index", ANALYZED_INDEX).endObject()
							// NAMESPACE_TAG
							.startObject( NAMESPACE_TAG ).field("type", "string")
								.field("index", NOT_ANALYZED_INDEX).endObject()
							// BUCKET_TAG
							.startObject( BUCKET_TAG ).field("type", "string")
								.field("index", NOT_ANALYZED_INDEX).endObject()
							// OWNER_ID_TAG
							.startObject( OWNER_ID_TAG ).field("type", "string")
								.field("index", NOT_ANALYZED_INDEX).endObject()
							// OWNER_NAME_TAG
							.startObject( OWNER_NAME_TAG ).field("type", "string")
								.field("index", NOT_ANALYZED_INDEX).endObject()
							// COLLECTION_TIME
							.startObject( COLLECTION_TIME ).field("type", "date")
								.field("format", "strict_date_optional_time||epoch_millis").endObject() 
							.endObject()
							.endObject()
							.endObject())
							.execute().actionGet();

			if (putMappingResponse.isAcknowledged()) {
				LOGGER.info("Index Created: {}", S3_OBJECT_INDEX_NAME);
			} else {
				LOGGER.error("Index {} did not exist. " + 
						"While attempting to create the index from stored ElasticSearch " +
						"Templates we were unable to get an acknowledgement.", S3_OBJECT_INDEX_NAME);
				LOGGER.error("Error Message: {}", putMappingResponse.toString());
				throw new RuntimeException("Unable to create index " + S3_OBJECT_INDEX_NAME);
			}			

		} catch (IOException e) {
			throw new RuntimeException( "Unable to create index " + 
					S3_OBJECT_INDEX_NAME +
					" " + e.getMessage()           );  
		}

	}


	private static XContentBuilder toJsonFormat( S3Object s3Object, 
			String namespace, 
			String bucket,
			Date collectionTime,
			XContentBuilder builder) {

		try {
			if(builder == null) {
				builder = XContentFactory.jsonBuilder();
			}

			// add relevant fileds
			builder = builder.startObject()	    
					.field( LAST_MODIFIED_TAG, s3Object.getLastModified() )
					.field( SIZE_TAG, s3Object.getSize() )
					.field( KEY_TAG, s3Object.getKey() )
					.field( KEY_TAG + ANALYZED_TAG, s3Object.getKey() )
					.field( NAMESPACE_TAG, namespace )
					.field( BUCKET_TAG, bucket )
					.field( OWNER_ID_TAG, (s3Object.getOwner() != null && s3Object.getOwner().getId() != null)
																			? s3Object.getOwner().getId() : null )	
					.field( OWNER_NAME_TAG, (s3Object.getOwner() != null && s3Object.getOwner().getDisplayName() != null) 
																			? s3Object.getOwner().getDisplayName() : null )	
					.field( COLLECTION_TIME, collectionTime )
					.endObject();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

		return builder;
	}


	public static XContentBuilder toJsonFormat( S3Object s3Object, String namespace, String bucket, Date collectionTime ) {						
		return toJsonFormat(s3Object, namespace, bucket,collectionTime, null);
	}

}
