/**
 * 
 */
package com.emc.ecs.metadata.dao.elasticsearch;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.emc.ecs.management.entity.BucketOwner;
import com.emc.ecs.management.entity.VdcDetails;
import com.emc.ecs.metadata.dao.VdcDAO;

/**
 * @author nlengc
 *
 */
public abstract class ElasticVdcDAO implements VdcDAO {

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

	protected static final String DATA_DATE_PATTERN = "yyyy-MM-dd";
	protected static final SimpleDateFormat DATA_DATE_FORMAT = new SimpleDateFormat(DATA_DATE_PATTERN);
	protected TransportClient elasticClient;

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
	}

	@Override
	public void insert(VdcDetails vdcDetails, Date collectionTime) {
	}

	@Override
	public void insert(List<BucketOwner> bucketOwners, Date collectionTime) {
	}

	@Override
	public Long purgeOldData(VdcDataType type, Date thresholdDate) {
		return 0L;
	}

}
