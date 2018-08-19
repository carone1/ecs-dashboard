/**
 * 
 */
package com.emc.ecs.metadata.dao.elasticsearch;

import java.io.IOException;
import java.net.URI;
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

import com.emc.ecs.management.entity.Attribute;
import com.emc.ecs.management.entity.NamespaceDetail;
import com.emc.ecs.management.entity.NamespaceQuota;
import com.emc.ecs.management.entity.UserMapping;

/**
 * @author nlengc
 *
 */
public class ElasticNamespaceDetailDAO extends ElasticNamespaceDAO {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ElasticNamespaceDetailDAO.class);
	public final static String NAMESPACE_DETAIL_INDEX_NAME = "ecs-namespace-detail";
	public final static String DETAIL_NAMESPACE_INDEX_TYPE = "namespace-details";
	
	private static String namespacedetailIndexDayName;

	/**
	 * @param config
	 */
	public ElasticNamespaceDetailDAO(ElasticDAOConfig config) {
		super(config);
	}
	
	@Override
	public void initIndexes(Date collectionTime) {
		initNamespaceDetailIndex(collectionTime);
	}
	
	private void initNamespaceDetailIndex(Date collectionTime) {

		String collectionDayString = DATA_DATE_FORMAT.format(collectionTime);
		namespacedetailIndexDayName = NAMESPACE_DETAIL_INDEX_NAME + "-" + collectionDayString;

		if (elasticClient.admin().indices().exists(new IndicesExistsRequest(namespacedetailIndexDayName)).actionGet()
				.isExists()) {

			// Index already exists need to truncate it and recreate it
			DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(namespacedetailIndexDayName);
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

		elasticClient.admin().indices().create(new CreateIndexRequest(namespacedetailIndexDayName)).actionGet();

		try {
			PutMappingResponse putMappingResponse = elasticClient.admin().indices()
					.preparePutMapping(namespacedetailIndexDayName).setType(DETAIL_NAMESPACE_INDEX_TYPE)
					.setSource(XContentFactory.jsonBuilder().prettyPrint().startObject()
							.startObject(DETAIL_NAMESPACE_INDEX_TYPE).startObject("properties")
							.startObject(NamespaceDetail.ID).field("type", "string").field("index", NOT_ANALYZED_INDEX).endObject()
							.startObject(NamespaceDetail.LINK).field("type", "string").endObject()
							.startObject(NamespaceDetail.NAME).field("type", "string").field("index", NOT_ANALYZED_INDEX).endObject()
							.startObject(NamespaceDetail.NAME + ANALYZED_TAG).field("type", "string").field("index", ANALYZED_INDEX).endObject()
							.startObject(NamespaceDetail.DEFAULT_DATA_SERVCIES_VPOOL).field("type", "string").field("index", NOT_ANALYZED_INDEX).endObject()
							.startObject(NamespaceDetail.ALLOWED_VPOOLS_LIST).field("type", "string").field("index", NOT_ANALYZED_INDEX).endObject()
							.startObject(NamespaceDetail.DISALLOWED_VPOOLS_LIST).field("type", "string").field("index", NOT_ANALYZED_INDEX).endObject()
							.startObject(NamespaceDetail.NAMESPACE_ADMINS).field("type", "string")
							.field("index", NOT_ANALYZED_INDEX).endObject()
							.startObject(NamespaceDetail.IS_ENCRYPTION_ENABLED).field("type", "boolean").endObject()
							.startObject(NamespaceDetail.DEFAULT_BUCKET_BLOCK_SIZE).field("type", "long").endObject()
							.startObject(NamespaceDetail.USER_MAPPING).field("type", "string").endObject()
							.startObject(NamespaceDetail.IS_STALE_ALLOWED).field("type", "boolean").endObject()
							.startObject(NamespaceDetail.IS_COMPLIANCE_ENABLED).field("type", "boolean").endObject()
							.startObject(NamespaceDetail.EXTERNAL_GROUP_ADMINS).field("type", "string").endObject()
							.startObject(NamespaceDetail.GLOBAL).field("type", "boolean").endObject()
							.startObject(NamespaceDetail.INACTIVE).field("type", "boolean").endObject()
                            .startObject(NamespaceDetail.REMOTE).field("type", "boolean").endObject()
							.startObject(NamespaceDetail.INTERNAL).field("type", "boolean").endObject()
							.startObject(NamespaceDetail.VDC).field("type", "string").endObject()
							.startObject(NamespaceDetail.CREATION_TIME).field("type", "date")
							.field("format", "strict_date_optional_time||date_optional_time||basic_date_time||epoch_millis").endObject()
							.startObject(COLLECTION_TIME).field("type", "date")
							.field("format", "strict_date_optional_time||epoch_millis").endObject().endObject()

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
				LOGGER.info("Index Created: " + namespacedetailIndexDayName);
			} else {
				LOGGER.error("Index {" + namespacedetailIndexDayName + "} did not exist. "
						+ "While attempting to create the index in ElasticSearch "
						+ "Templates we were unable to get an acknowledgement.", namespacedetailIndexDayName);
				LOGGER.error("Error Message: {}", putMappingResponse.toString());
				throw new RuntimeException("Unable to create index " + namespacedetailIndexDayName);
			}

		} catch (IOException e) {
			throw new RuntimeException("Unable to create index " + namespacedetailIndexDayName + " " + e.getMessage());
		}
	}

	@Override
	public void insert(NamespaceDetail namespaceDetail, Date collectionTime) {
		// Generate JSON for namespace quota
		XContentBuilder namespaceBuilder = toJsonFormat(namespaceDetail, collectionTime);
		elasticClient.prepareIndex( namespacedetailIndexDayName, 
				DETAIL_NAMESPACE_INDEX_TYPE).setSource(namespaceBuilder).get();
	}

	private static XContentBuilder toJsonFormat(NamespaceDetail namespaceDetail, Date collectionTime,
			XContentBuilder builder) {

		try {
			if (builder == null) {
				builder = XContentFactory.jsonBuilder();
			}

			// namespace portion
			builder = builder.startObject().field(NamespaceDetail.ID, namespaceDetail.getId())
					.field(NamespaceDetail.LINK, namespaceDetail.getLink())
					.field(NamespaceDetail.NAME, namespaceDetail.getName())
					.field(NamespaceDetail.NAME + ANALYZED_TAG, namespaceDetail.getName())
					.field(NamespaceDetail.DEFAULT_DATA_SERVCIES_VPOOL, namespaceDetail.getDefaultDataServicesVPool())
					.field(NamespaceDetail.NAMESPACE_ADMINS, namespaceDetail.getNamespaceAdmins())
					.field(NamespaceDetail.IS_ENCRYPTION_ENABLED, namespaceDetail.getIsEncryptionEnabled())
					.field(NamespaceDetail.DEFAULT_BUCKET_BLOCK_SIZE, namespaceDetail.getDefaultBucketBlockSize())
					.field(NamespaceDetail.IS_STALE_ALLOWED, namespaceDetail.getIsStaledAllowed())
					.field(NamespaceDetail.IS_COMPLIANCE_ENABLED, namespaceDetail.getIsComplianceEnabled())
					.field(NamespaceDetail.EXTERNAL_GROUP_ADMINS, namespaceDetail.getExternalGroupAdmins())
					.field(NamespaceDetail.GLOBAL, namespaceDetail.getGlobal())
					.field(NamespaceDetail.INACTIVE, namespaceDetail.getInactive())
					.field(NamespaceDetail.REMOTE, namespaceDetail.getRemote())
					.field(NamespaceDetail.INTERNAL, namespaceDetail.getInternal())
					.field(NamespaceDetail.CREATION_TIME, namespaceDetail.getCreationTime());

			// ALLOWED_VPOOLS_LIST
			if (namespaceDetail.getAllowedVPools() == null && !namespaceDetail.getAllowedVPools().isEmpty()) {
				builder.startArray(NamespaceDetail.ALLOWED_VPOOLS_LIST);
				for (URI allowedVPool : namespaceDetail.getAllowedVPools()) {
					builder.startObject().field(NamespaceDetail.ALLOWED_VPOOLS_LIST, allowedVPool.toString())
							.endObject();
				}
				builder.endArray();
			}

			// DISALLOWED_VPOOLS_LIST
			if (namespaceDetail.getDisallowedVPools() == null && !namespaceDetail.getDisallowedVPools().isEmpty()) {
				builder.startArray(NamespaceDetail.DISALLOWED_VPOOLS_LIST);
				for (URI allowedVPool : namespaceDetail.getDisallowedVPools()) {
					builder.startObject().field(NamespaceDetail.DISALLOWED_VPOOLS_LIST, allowedVPool.toString())
							.endObject();
				}
				builder.endArray();
			}

			// URI Mapping
			if (namespaceDetail.getUserMappings() == null && !namespaceDetail.getUserMappings().isEmpty()) {
				builder.startArray(NamespaceDetail.USER_MAPPING);
				for (UserMapping userMapping : namespaceDetail.getUserMappings()) {
					// DOMAIN
					builder.startObject().field(UserMapping.DOMAIN, userMapping.getDomain());
					// Attributes
					if (userMapping.getAttributes() != null && !userMapping.getAttributes().isEmpty()) {
						builder.startArray(UserMapping.ATTRIBUTES);
						for (Attribute attribute : userMapping.getAttributes()) {
							builder.startObject().field(Attribute.KEY, attribute.getKey());
							if (attribute.getValues() != null && !attribute.getValues().isEmpty()) {
								builder.startArray(Attribute.VALUE);
								for (String value : attribute.getValues()) {
									builder.startObject().field(Attribute.VALUE, value).endObject();
								}
								builder.endArray();
							}
							builder.endObject();
						}
						builder.endArray();
					}
					// Groups
					if (userMapping.getGroups() != null && !userMapping.getGroups().isEmpty()) {
						builder.startArray(UserMapping.GROUPS);
						for (String group : userMapping.getGroups()) {
							builder.startObject().field(UserMapping.GROUPS, group).endObject();
						}
						builder.endArray();
					}
					builder.endObject();
				}
				builder.endArray();
			}

			builder.field(NamespaceDetail.VDC,
					(namespaceDetail.getVdc() != null) ? namespaceDetail.getVdc().toString() : null);
			builder.field(COLLECTION_TIME, collectionTime).endObject();

		} catch (IOException e) {
			throw new RuntimeException(e.getLocalizedMessage());
		}

		return builder;
	}

	public static XContentBuilder toJsonFormat(NamespaceDetail namespaceDetail, Date collectionTime) {
		return toJsonFormat(namespaceDetail, collectionTime, null);
	}

	@Override
	public Long purgeOldData(NamespaceDataType type, Date thresholdDate) {
		switch(type) {
		case namespace_detail:
			// Purge old nemaspace dertails Objects
			ElasticIndexCleaner.truncateOldIndexes(elasticClient, thresholdDate, NAMESPACE_DETAIL_INDEX_NAME,
					DETAIL_NAMESPACE_INDEX_TYPE);
			return 0L;
		default:
			return 0L;
		}
	}

	@Override
	public void insert(NamespaceQuota namespacequota, Date collectionTime) {
	}

}
