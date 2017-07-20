/**
 * 
 */
package com.emc.ecs.metadata.dao.elasticsearch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.ecs.management.entity.Attribute;
import com.emc.ecs.management.entity.NamespaceDetail;
import com.emc.ecs.management.entity.NamespaceQuota;
import com.emc.ecs.management.entity.UserMapping;
import com.emc.ecs.metadata.dao.NamespaceDAO;

/**
 * @author nlengc
 *
 */
public class ElasticNamespaceDAO implements NamespaceDAO {

	private final static String CLIENT_SNIFFING_CONFIG = "client.transport.sniff";
	private final static String CLIENT_CLUSTER_NAME_CONFIG = "cluster.name";
	public final static String NAMESPACE_DETAIL_INDEX_NAME = "ecs-namespace-detail";
	public final static String NAMESPACE_QUOTA_INDEX_NAME = "ecs-namespace-quota";
	public final static String DETAIL_NAMESPACE_INDEX_TYPE = "namespace-details";
	public final static String QUOTA_NAMESPACE_INDEX_TYPE = "namespace-quota";
	public final static String COLLECTION_TIME = "collection_time";
	public final static String ANALYZED_TAG = "_analyzed";
	public final static String NOT_ANALYZED_INDEX = "not_analyzed";
	public final static String ANALYZED_INDEX = "analyzed";

	private TransportClient elasticClient;
	private static Logger LOGGER = LoggerFactory.getLogger(ElasticNamespaceDAO.class);
	private static final String DATA_DATE_PATTERN = "yyyy-MM-dd";
	private static final SimpleDateFormat DATA_DATE_FORMAT = new SimpleDateFormat(DATA_DATE_PATTERN);
	private static String namespacedetailIndexDayName;
	private static String namespacequotaIndexDayName;

	public ElasticNamespaceDAO(ElasticDAOConfig config) {
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
		// init indexes
		initNamespaceDetailIndex(collectionTime);
		initNamespaceQuotaIndex(collectionTime);
	}
	
	/**
	 * 
	 * @param collectionTime
	 */
	private void initNamespaceQuotaIndex(Date collectionTime) {

		String collectionDayString = DATA_DATE_FORMAT.format(collectionTime);
		namespacequotaIndexDayName = NAMESPACE_QUOTA_INDEX_NAME + "-" + collectionDayString;

		if (!elasticClient.admin().indices().exists(new IndicesExistsRequest(namespacequotaIndexDayName)).actionGet()
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
							.startObject(NamespaceQuota.NAMESPACE).field("type", "string").field("index", NOT_ANALYZED_INDEX).endObject()
							.startObject(NamespaceQuota.NAMESPACE+ANALYZED_TAG).field("type", "string").field("index", ANALYZED_INDEX).endObject()
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
							.startObject(NamespaceDetail.LINK).field("type", "string").field("index", NOT_ANALYZED_INDEX).endObject()
							.startObject(NamespaceDetail.NAME).field("type", "string").field("index", NOT_ANALYZED_INDEX).endObject()
							.startObject(NamespaceDetail.NAME + ANALYZED_TAG).field("type", "string").field("index", ANALYZED_INDEX).endObject()
							.startObject(NamespaceDetail.DEFAULT_DATA_SERVCIES_VPOOL).field("type", "string").field("index", NOT_ANALYZED_INDEX).endObject()
//							.startObject(NamespaceDetail.ALLOWED_VPOOLS_LIST).field("type", "string").field("index", NOT_ANALYZED_INDEX).endObject()
//							.startObject(NamespaceDetail.DISALLOWED_VPOOLS_LIST).field("type", "string").field("index", NOT_ANALYZED_INDEX).endObject()
							.startObject(NamespaceDetail.NAMESPACE_ADMINS).field("type", "string")
							.field("index", NOT_ANALYZED_INDEX).endObject()
							.startObject(NamespaceDetail.IS_ENCRYPTION_ENABLED).field("type", "boolean")
							.field("index", NOT_ANALYZED_INDEX).endObject()
							.startObject(NamespaceDetail.DEFAULT_BUCKET_BLOCK_SIZE).field("type", "long")
							.field("index", NOT_ANALYZED_INDEX).endObject().startObject(NamespaceDetail.USER_MAPPING)
							.field("type", "string").field("index", NOT_ANALYZED_INDEX).endObject()
							.startObject(NamespaceDetail.IS_STALE_ALLOWED).field("type", "boolean")
							.field("index", NOT_ANALYZED_INDEX).endObject()
							.startObject(NamespaceDetail.IS_COMPLIANCE_ENABLED).field("type", "boolean")
							.field("index", NOT_ANALYZED_INDEX).endObject()
							.startObject(NamespaceDetail.EXTERNAL_GROUP_ADMINS).field("type", "string")
							.field("index", NOT_ANALYZED_INDEX).endObject().startObject(NamespaceDetail.GLOBAL)
							.field("type", "boolean").field("index", NOT_ANALYZED_INDEX).endObject()
							.startObject(NamespaceDetail.INACTIVE).field("type", "boolean")
							.field("index", NOT_ANALYZED_INDEX).endObject().startObject(NamespaceDetail.REMOTE)
							.field("type", "boolean").field("index", NOT_ANALYZED_INDEX).endObject()
							.startObject(NamespaceDetail.INTERNAL).field("type", "boolean")
							.field("index", NOT_ANALYZED_INDEX).endObject().startObject(NamespaceDetail.VDC)
							.field("type", "string").field("index", NOT_ANALYZED_INDEX).endObject()
							.startObject(NamespaceDetail.CREATION_TIME).field("type", "date")
							.field("format", "strict_date_optional_time||epoch_millis").endObject()
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

	@Override
	public void insert(NamespaceQuota namespaceQuota, Date collectionTime) {
		// Generate JSON for namespace quota
		XContentBuilder namespaceBuilder = toJsonFormat(namespaceQuota, collectionTime);
		elasticClient.prepareIndex( namespacequotaIndexDayName, 
				QUOTA_NAMESPACE_INDEX_TYPE).setSource(namespaceBuilder).get();
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
	
	public static XContentBuilder toJsonFormat(NamespaceDetail namespaceDetail, Date collectionTime) {
		return toJsonFormat(namespaceDetail, collectionTime, null);
	}

	public static XContentBuilder toJsonFormat(NamespaceQuota namespaceQuota, Date collectionTime) {
		return toJsonFormat(namespaceQuota, collectionTime, null);
	}

	@Override
	public Long purgeOldData(NamespaceDataType type, Date thresholdDate) {
		switch(type) {
		case namespace_detail:
			// Purge old nemaspace dertails Objects
			ElasticIndexCleaner.truncateOldIndexes(elasticClient, thresholdDate, NAMESPACE_DETAIL_INDEX_NAME,
					DETAIL_NAMESPACE_INDEX_TYPE);
			return 0L;
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
