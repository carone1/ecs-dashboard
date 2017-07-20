/**
 * 
 */
package com.emc.ecs.metadata.dao.elasticsearch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.ecs.management.entity.BucketOwner;
import com.emc.ecs.management.entity.VdcDetails;
import com.emc.ecs.metadata.dao.VdcDAO;

import Vdc.VdcDetail;

/**
 * @author nlengc
 *
 */
public class ElasticVdcDAO implements VdcDAO {

	private final static String CLIENT_SNIFFING_CONFIG = "client.transport.sniff";
	private final static String CLIENT_CLUSTER_NAME_CONFIG = "cluster.name";
	public final static String VDC_INDEX_NAME = "ecs-vdc";
	public final static String BUCKET_OWNER_INDEX_NAME = "ecs-bucket-owner";
	public final static String VDC_INDEX_TYPE = "vdc-details";
	public final static String BUCKET_OWNER_INDEX_TYPE = "bucket-owner";
	public final static String COLLECTION_TIME = "collection_time";
	public final static String ANALYZED_TAG = "_analyzed";
	public final static String NOT_ANALYZED_INDEX = "not_analyzed";
	public final static String ANALYZED_INDEX = "analyzed";

	private static Logger LOGGER = LoggerFactory.getLogger(ElasticVdcDAO.class);
	private static final String DATA_DATE_PATTERN = "yyyy-MM-dd";
	private static final SimpleDateFormat DATA_DATE_FORMAT = new SimpleDateFormat(DATA_DATE_PATTERN);
	private static String vdcIndexDayName;
	private static String bucketownerIndexDayName;
	private TransportClient elasticClient;

	public ElasticVdcDAO(ElasticDAOConfig config) {
		try {
			Builder builder = Settings.builder();
			// Check for new hosts within the cluster
			builder.put(CLIENT_SNIFFING_CONFIG, true);
			// specify cluster name
			if (config.getClusterName() != null) {
				builder.put(CLIENT_CLUSTER_NAME_CONFIG, config.getClusterName());
			}
			Settings settings = builder.build();
			// create client
			elasticClient = new PreBuiltTransportClient(settings);
			// add hosts
			for (String elasticHost : config.getHosts()) {
				elasticClient.addTransportAddress(
						new InetSocketTransportAddress(InetAddress.getByName(elasticHost), config.getPort()));
			}
		} catch (UnknownHostException e) {
			throw new RuntimeException("Unable to initialize Eleasticsearch client " + e.getLocalizedMessage());
		}
	}

	@Override
	public void initIndexes(Date collectionTime) {
		initVdcIndex(collectionTime);
		initBucketOwnerIndex(collectionTime);
	}

	private void initBucketOwnerIndex(Date collectionTime) {
		String collectionDayString = DATA_DATE_FORMAT.format(collectionTime);
		bucketownerIndexDayName = BUCKET_OWNER_INDEX_NAME + "-" + collectionDayString;

		if (elasticClient.admin().indices().exists(new IndicesExistsRequest(bucketownerIndexDayName)).actionGet()
				.isExists()) {
			// Index already exists need to truncate it and recreate it
			DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(bucketownerIndexDayName);
			ActionFuture<DeleteIndexResponse> futureResult = elasticClient.admin().indices().delete(deleteIndexRequest);

			// Wait until deletion is done
			while (!futureResult.isDone()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		elasticClient.admin().indices().create(new CreateIndexRequest(bucketownerIndexDayName)).actionGet();
		
		try {
			PutMappingResponse putMappingResponse = elasticClient.admin().indices()
					.preparePutMapping(bucketownerIndexDayName).setType(BUCKET_OWNER_INDEX_TYPE)
					.setSource(XContentFactory.jsonBuilder().prettyPrint()
							.startObject()
								.startObject(BUCKET_OWNER_INDEX_TYPE)
									.startObject("properties")
										.startObject(BucketOwner.VDC_ID).field("type", "string")
										.field("index", NOT_ANALYZED_INDEX).endObject()
							
										.startObject(BucketOwner.BUCKET_KEY).field("type", "string")
										.field("index", NOT_ANALYZED_INDEX).endObject()
										
										.startObject(COLLECTION_TIME)
										.field("type", "date").field("format", "strict_date_optional_time||epoch_millis")
										.endObject()
									.endObject()

							// =================================
							// Dynamic fields won't be analyzed
							// =================================
							.startArray("dynamic_templates").startObject().startObject("notanalyzed")
							.field("match", "*").field("match_mapping_type", "string").startObject("mapping")
							.field("type", "string").field("index", NOT_ANALYZED_INDEX).endObject().endObject()
							.endObject().endArray()

							.endObject().endObject())
					.execute().actionGet();

			if (putMappingResponse.isAcknowledged()) {
				LOGGER.info("Index Created: " + vdcIndexDayName);
			} else {
				LOGGER.error("Index {" + vdcIndexDayName + "} did not exist. "
						+ "While attempting to create the index in ElasticSearch "
						+ "Templates we were unable to get an acknowledgement.", vdcIndexDayName);
				LOGGER.error("Error Message: {}", putMappingResponse.toString());
				throw new RuntimeException("Unable to create index " + vdcIndexDayName);
			}

		} catch (IOException e) {
			throw new RuntimeException("Unable to create index " + vdcIndexDayName + " " + e.getMessage());
		}
	}

	/**
	 * 
	 * @param collectionTime
	 */
	private void initVdcIndex(Date collectionTime) {
		String collectionDayString = DATA_DATE_FORMAT.format(collectionTime);
		vdcIndexDayName = VDC_INDEX_NAME + "-" + collectionDayString;

		if (elasticClient.admin().indices().exists(new IndicesExistsRequest(vdcIndexDayName)).actionGet()
				.isExists()) {
			// Index already exists need to truncate it and recreate it
			DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(vdcIndexDayName);
			ActionFuture<DeleteIndexResponse> futureResult = elasticClient.admin().indices().delete(deleteIndexRequest);

			// Wait until deletion is done
			while (!futureResult.isDone()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		elasticClient.admin().indices().create(new CreateIndexRequest(vdcIndexDayName)).actionGet();

		try {
			PutMappingResponse putMappingResponse = elasticClient.admin().indices()
					.preparePutMapping(vdcIndexDayName).setType(VDC_INDEX_TYPE)
					.setSource(XContentFactory.jsonBuilder().prettyPrint()
							.startObject()
								.startObject(VDC_INDEX_TYPE)
									.startObject("properties")
										.startObject(VdcDetail.VDC_ID).field("type", "string")
										.field("index", NOT_ANALYZED_INDEX).endObject()
							
										.startObject(VdcDetail.VDC_NAME).field("type", "string")
										.field("index", NOT_ANALYZED_INDEX).endObject()
										
										.startObject(VdcDetail.INTER_VDC_END_POINTS).field("type", "string")
										.field("index", NOT_ANALYZED_INDEX).endObject()
										
										.startObject(VdcDetail.INTER_VDC_CMD_END_POINTS).field("type", "string")
										.field("index", NOT_ANALYZED_INDEX).endObject()
										
										.startObject(VdcDetail.SECRET_KEYS).field("type", "string")
										.field("index", NOT_ANALYZED_INDEX).endObject()
										
										.startObject(VdcDetail.PERMANENTLY_FAILED).field("type", "string")
										.field("index", NOT_ANALYZED_INDEX).endObject()
										
										.startObject(VdcDetail.LOCAL).field("type", "string")
										.field("index", NOT_ANALYZED_INDEX).endObject()
										
										.startObject(VdcDetail.MGMT_END_POINTS).field("type", "string")
										.field("index", NOT_ANALYZED_INDEX).endObject()
										
										.startObject(VdcDetail.NAME).field("type", "string")
										.field("index", NOT_ANALYZED_INDEX).endObject()
										
										.startObject(VdcDetail.ID).field("type", "string")
										.field("index", NOT_ANALYZED_INDEX).endObject()
										
										.startObject(VdcDetail.LINK).field("type", "string")
										.field("index", NOT_ANALYZED_INDEX).endObject()
										
										.startObject(VdcDetail.INACTIVE).field("type", "string")
										.field("index", NOT_ANALYZED_INDEX).endObject()
										
										.startObject(VdcDetail.GLOBAL).field("type", "string")
										.field("index", NOT_ANALYZED_INDEX).endObject()
										
										.startObject(VdcDetail.REMOTE).field("type", "string")
										.field("index", NOT_ANALYZED_INDEX).endObject()
										
										.startObject(VdcDetail.VDC).field("type", "string")
										.field("index", NOT_ANALYZED_INDEX).endObject()
										
										.startObject(VdcDetail.INTERNAL).field("type", "string")
										.field("index", NOT_ANALYZED_INDEX).endObject()
										
										.startObject(VdcDetail.CREATION_TIME).field("type", "date").field("format", "strict_date_optional_time||epoch_millis")
										.field("index", NOT_ANALYZED_INDEX).endObject()
										
										.startObject(VdcDetail.NAME + ANALYZED_TAG).field("type", "string")
										.field("index", ANALYZED_INDEX).endObject()
										
										.startObject(VdcDetail.VDC_NAME + ANALYZED_TAG).field("type", "string")
										.field("index", ANALYZED_INDEX).endObject()
										
										.startObject(COLLECTION_TIME)
										.field("type", "date").field("format", "strict_date_optional_time||epoch_millis")
										.endObject()
									.endObject()

							// =================================
							// Dynamic fields won't be analyzed
							// =================================
							.startArray("dynamic_templates").startObject().startObject("notanalyzed")
							.field("match", "*").field("match_mapping_type", "string").startObject("mapping")
							.field("type", "string").field("index", NOT_ANALYZED_INDEX).endObject().endObject()
							.endObject().endArray()

							.endObject().endObject())
					.execute().actionGet();

			if (putMappingResponse.isAcknowledged()) {
				LOGGER.info("Index Created: " + vdcIndexDayName);
			} else {
				LOGGER.error("Index {" + vdcIndexDayName + "} did not exist. "
						+ "While attempting to create the index in ElasticSearch "
						+ "Templates we were unable to get an acknowledgement.", vdcIndexDayName);
				LOGGER.error("Error Message: {}", putMappingResponse.toString());
				throw new RuntimeException("Unable to create index " + vdcIndexDayName);
			}

		} catch (IOException e) {
			throw new RuntimeException("Unable to create index " + vdcIndexDayName + " " + e.getMessage());
		}
	}

	@Override
	public void insert(VdcDetails vdcDetails, Date collectionTime) {
		
		if (vdcDetails == null || vdcDetails.getVdcDetails() == null || vdcDetails.getVdcDetails().isEmpty()) {
			return;
		}
		
		BulkRequestBuilder requestBuilder = elasticClient.prepareBulk();
		
		// Generate JSON for object buckets info
		for( VdcDetail vdcDetail : vdcDetails.getVdcDetails()  ) {
			XContentBuilder objectBucketBuilder = toJsonFormat(vdcDetail, collectionTime);
			
			IndexRequestBuilder request = elasticClient.prepareIndex()
	                .setIndex(vdcIndexDayName)
	                .setType(VDC_INDEX_TYPE)
	                .setSource(objectBucketBuilder);
			requestBuilder.add(request);
		}
		
		BulkResponse bulkResponse = requestBuilder.execute().actionGet();
	    int items = bulkResponse.getItems().length;
		LOGGER.info( "Took " + bulkResponse.getTookInMillis() + " ms to index [" + items + "] items in ElasticSearch" + "index: " + 
				vdcIndexDayName + " index type: " +  VDC_INDEX_TYPE ); 
	    
		if( bulkResponse.hasFailures() ) {
			LOGGER.error( "Failures occured while items in ElasticSearch " + "index: " + 
					vdcIndexDayName + " index type: " +  VDC_INDEX_TYPE );
		}
	}
	
	public static XContentBuilder toJsonFormat( VdcDetail vdcDetail, Date collectionTime ) {						
		return toJsonFormat(vdcDetail, collectionTime, null);
	}
	
	private static XContentBuilder toJsonFormat(VdcDetail vdcDetail, Date collectionTime, XContentBuilder builder) {
		try {
			if (builder == null) {
				builder = XContentFactory.jsonBuilder();
			}
			// namespace portion
			builder = builder.startObject()
					.field(VdcDetail.VDC_ID, vdcDetail.getId())
					.field(VdcDetail.VDC_NAME, vdcDetail.getVdcName())
					.field(VdcDetail.INTER_VDC_END_POINTS, vdcDetail.getInterVdcEndPoints())
					.field(VdcDetail.INTER_VDC_CMD_END_POINTS, vdcDetail.getInterVdcCmdEndPoints())
					.field(VdcDetail.SECRET_KEYS, vdcDetail.getSecretKeys())
					.field(VdcDetail.PERMANENTLY_FAILED, vdcDetail.getPermanentlyFailed())
					.field(VdcDetail.LOCAL, vdcDetail.getLocal())
					.field(VdcDetail.MGMT_END_POINTS, vdcDetail.getManagementEndPoints())
					.field(VdcDetail.NAME, vdcDetail.getName())
					.field(VdcDetail.ID, vdcDetail.getId())
					.field(VdcDetail.LINK, vdcDetail.getLink())
					.field(VdcDetail.INACTIVE, vdcDetail.getInactive())
					.field(VdcDetail.GLOBAL, vdcDetail.getGlobal())
					.field(VdcDetail.REMOTE, vdcDetail.getRemote())
					.field(VdcDetail.VDC, vdcDetail.getVdc())
					.field(VdcDetail.INTERNAL, vdcDetail.getInternal())
					.field(VdcDetail.CREATION_TIME, vdcDetail.getCreationTime())
					.field(VdcDetail.NAME + ANALYZED_TAG, vdcDetail.getName())
					.field(VdcDetail.VDC_NAME + ANALYZED_TAG, vdcDetail.getVdcName())
					.field(COLLECTION_TIME, collectionTime).endObject();
		} catch (IOException e) {
			throw new RuntimeException(e.getLocalizedMessage());
		}
		return builder;
	}

	@Override
	public void insert(List<BucketOwner> bucketOwners, Date collectionTime) {
		
		if (bucketOwners != null && !bucketOwners.isEmpty()) {
			BulkRequestBuilder requestBuilder = elasticClient.prepareBulk();
			// Generate JSON for object buckets info
			for (BucketOwner bucketOwner : bucketOwners) {
				XContentBuilder objectBucketBuilder = toJsonFormat(bucketOwner, collectionTime);
				IndexRequestBuilder request = elasticClient.prepareIndex().setIndex(bucketownerIndexDayName)
						.setType(BUCKET_OWNER_INDEX_TYPE).setSource(objectBucketBuilder);
				requestBuilder.add(request);
			}

			BulkResponse bulkResponse = requestBuilder.execute().actionGet();
			int items = bulkResponse.getItems().length;
			LOGGER.info("Took " + bulkResponse.getTookInMillis() + " ms to index [" + items + "] items in ElasticSearch"
					+ "index: " + bucketownerIndexDayName + " index type: " + BUCKET_OWNER_INDEX_TYPE);

			if (bulkResponse.hasFailures()) {
				LOGGER.error("Failures occured while items in ElasticSearch " + "index: " + bucketownerIndexDayName
						+ " index type: " + BUCKET_OWNER_INDEX_TYPE);
			}
		}
	}
	
	public static XContentBuilder toJsonFormat( BucketOwner bucketOwner, Date collectionTime ) {						
		return toJsonFormat(bucketOwner, collectionTime, null);
	}
	
	private static XContentBuilder toJsonFormat(BucketOwner bucketOwner, Date collectionTime, XContentBuilder builder) {
		try {
			if (builder == null) {
				builder = XContentFactory.jsonBuilder();
			}
			// namespace portion
			builder = builder.startObject()
					.field(BucketOwner.VDC_ID, bucketOwner.getVdcId())
					.field(BucketOwner.BUCKET_KEY, bucketOwner.getBucketKey())
					.field(COLLECTION_TIME, collectionTime).endObject();
		} catch (IOException e) {
			throw new RuntimeException(e.getLocalizedMessage());
		}
		return builder;
	}

	@Override
	public Long purgeOldData(VdcDataType type, Date thresholdDate) {
		switch (type) {
		case bucket_owner:
			// Purge old Billing Bucket entries
			ElasticIndexCleaner.truncateOldIndexes(elasticClient, thresholdDate, BUCKET_OWNER_INDEX_NAME,
					BUCKET_OWNER_INDEX_TYPE);
			return 0L;
		case vdc:
			// Purge old Billing Namespace entries
			ElasticIndexCleaner.truncateOldIndexes(elasticClient, thresholdDate, VDC_INDEX_NAME, VDC_INDEX_TYPE);
			return 0L;
		default:
			return 0L;
		}
	}

}
