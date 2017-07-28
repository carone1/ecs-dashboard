/**
 * 
 */
package com.emc.ecs.metadata.dao.elasticsearch;

import java.io.IOException;
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
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.ecs.management.entity.BucketOwner;

/**
 * @author nlengc
 *
 */
public class ElasticBucketOwnerDAO extends ElasticVdcDetailDAO {

	public final static String BUCKET_OWNER_INDEX_NAME = "ecs-owner-bucket";
	public final static String BUCKET_OWNER_INDEX_TYPE = "bucket-owner";
	public final static String COLLECTION_TIME = "collection_time";

	private static Logger LOGGER = LoggerFactory.getLogger(ElasticBucketOwnerDAO.class);
	private static String bucketownerIndexDayName;
	
	/**
	 * @param config
	 */
	public ElasticBucketOwnerDAO(ElasticDAOConfig config) {
		super(config);
	}

	@Override
	public void initIndexes(Date collectionTime) {
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
				LOGGER.info("Index Created: " + bucketownerIndexDayName);
			} else {
				LOGGER.error("Index {" + bucketownerIndexDayName + "} did not exist. "
						+ "While attempting to create the index in ElasticSearch "
						+ "Templates we were unable to get an acknowledgement.", bucketownerIndexDayName);
				LOGGER.error("Error Message: {}", putMappingResponse.toString());
				throw new RuntimeException("Unable to create index " + bucketownerIndexDayName);
			}

		} catch (IOException e) {
			throw new RuntimeException("Unable to create index " + bucketownerIndexDayName + " " + e.getMessage());
		}
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
		default:
			return 0L;
		}
	}
}
