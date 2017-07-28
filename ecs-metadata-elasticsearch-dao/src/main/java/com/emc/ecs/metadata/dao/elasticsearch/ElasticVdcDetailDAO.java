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
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.ecs.management.entity.VdcDetails;

import Vdc.VdcDetail;

/**
 * @author nlengc
 *
 */
public class ElasticVdcDetailDAO extends ElasticVdcDAO {
	public final static String VDC_INDEX_NAME = "ecs-vdc";
	public final static String VDC_INDEX_TYPE = "vdc-details";

	private static Logger LOGGER = LoggerFactory.getLogger(ElasticVdcDetailDAO.class);
	private static String vdcIndexDayName;
	
	/**
	 * @param config
	 */
	public ElasticVdcDetailDAO(ElasticDAOConfig config) {
		super(config);
	}

	@Override
	public void initIndexes(Date collectionTime) {
		initVdcIndex(collectionTime);
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
	public Long purgeOldData(VdcDataType type, Date thresholdDate) {
		switch (type) {
		case vdc:
			// Purge old Billing Namespace entries
			ElasticIndexCleaner.truncateOldIndexes(elasticClient, thresholdDate, VDC_INDEX_NAME, VDC_INDEX_TYPE);
			return 0L;
		default:
			return 0L;
		}
	}
}
