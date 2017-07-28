/**
 * 
 */
package com.emc.ecs.metadata.dao.elasticsearch;

import java.io.IOException;
import java.util.Date;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.ecs.management.entity.NamespaceQuota;

/**
 * @author nlengc
 *
 */
public class ElasticNamespaceQuotaDAO extends ElasticNamespaceDAO {

	private static Logger LOGGER = LoggerFactory.getLogger(ElasticNamespaceQuotaDAO.class);
	public final static String NAMESPACE_QUOTA_INDEX_NAME = "ecs-namespace-quota";
	public final static String QUOTA_NAMESPACE_INDEX_TYPE = "namespace-quota";
	private static String namespacequotaIndexDayName;

	/**
	 * @param config
	 */
	public ElasticNamespaceQuotaDAO(ElasticDAOConfig config) {
		super(config);
	}

	@Override
	public void initIndexes(Date collectionTime) {
		// init indexes
		initNamespaceQuotaIndex(collectionTime);
	}

	/**
	 * 
	 * @param collectionTime
	 */
	private void initNamespaceQuotaIndex(Date collectionTime) {

		String collectionDayString = DATA_DATE_FORMAT.format(collectionTime);
		namespacequotaIndexDayName = NAMESPACE_QUOTA_INDEX_NAME + "-" + collectionDayString;

		if (elasticClient.admin().indices().exists(new IndicesExistsRequest(namespacequotaIndexDayName)).actionGet()
				.isExists()) {

			// Index already exists need to truncate it and recreate it
			DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(namespacequotaIndexDayName);
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

		elasticClient.admin().indices().create(new CreateIndexRequest(namespacequotaIndexDayName)).actionGet();

		try {
			PutMappingResponse putMappingResponse = elasticClient.admin().indices()
					.preparePutMapping(namespacequotaIndexDayName).setType(QUOTA_NAMESPACE_INDEX_TYPE)
					.setSource(XContentFactory.jsonBuilder().prettyPrint().startObject()
							.startObject(QUOTA_NAMESPACE_INDEX_TYPE).startObject("properties")
							.startObject(NamespaceQuota.BLOCK_SIZE).field("type", "long").endObject()
							.startObject(NamespaceQuota.NOTIFICATION_SIZE).field("type", "long").endObject()
							.startObject(NamespaceQuota.NAMESPACE).field("type", "string")
							.field("index", NOT_ANALYZED_INDEX).endObject()
							.startObject(NamespaceQuota.NAMESPACE + ANALYZED_TAG).field("type", "string")
							.field("index", ANALYZED_INDEX).endObject().startObject(COLLECTION_TIME)
							.field("type", "date").field("format", "strict_date_optional_time||epoch_millis")
							.endObject().endObject()

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
				LOGGER.info("Index Created: " + namespacequotaIndexDayName);
			} else {
				LOGGER.error("Index {" + namespacequotaIndexDayName + "} did not exist. "
						+ "While attempting to create the index in ElasticSearch "
						+ "Templates we were unable to get an acknowledgement.", namespacequotaIndexDayName);
				LOGGER.error("Error Message: {}", putMappingResponse.toString());
				throw new RuntimeException("Unable to create index " + namespacequotaIndexDayName);
			}

		} catch (IOException e) {
			throw new RuntimeException("Unable to create index " + namespacequotaIndexDayName + " " + e.getMessage());
		}
	}
	
	@Override
	public void insert(NamespaceQuota namespaceQuota, Date collectionTime) {
		// Generate JSON for namespace quota
		XContentBuilder namespaceBuilder = toJsonFormat(namespaceQuota, collectionTime);
		elasticClient.prepareIndex( namespacequotaIndexDayName, 
				QUOTA_NAMESPACE_INDEX_TYPE).setSource(namespaceBuilder).get();
	}

	private static XContentBuilder toJsonFormat(NamespaceQuota namespaceQuota, Date collectionTime,
			XContentBuilder builder) {

		try {
			if (builder == null) {
				builder = XContentFactory.jsonBuilder();
			}

			// namespace portion
			builder = builder.startObject().field(NamespaceQuota.BLOCK_SIZE, namespaceQuota.getBlockSize())
					.field(NamespaceQuota.NOTIFICATION_SIZE, namespaceQuota.getNotificationSize())
					.field(NamespaceQuota.NAMESPACE, namespaceQuota.getNamespace())
					.field(NamespaceQuota.NAMESPACE + ANALYZED_TAG, namespaceQuota.getNamespace());
			builder.field(COLLECTION_TIME, collectionTime).endObject();

		} catch (IOException e) {
			throw new RuntimeException(e.getLocalizedMessage());
		}

		return builder;
	}

	public static XContentBuilder toJsonFormat(NamespaceQuota namespaceQuota, Date collectionTime) {
		return toJsonFormat(namespaceQuota, collectionTime, null);
	}
	
	@Override
	public Long purgeOldData(NamespaceDataType type, Date thresholdDate) {
		switch(type) {
		case namespace_quota:
			// Purge old namespace quota objects
			ElasticIndexCleaner.truncateOldIndexes(elasticClient, thresholdDate, NAMESPACE_QUOTA_INDEX_NAME,
					QUOTA_NAMESPACE_INDEX_TYPE);
			return 0L;
		default:
			return 0L;
		}
	}

}
