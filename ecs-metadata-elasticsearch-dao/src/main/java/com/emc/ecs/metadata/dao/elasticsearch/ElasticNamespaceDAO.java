/**
 * 
 */
package com.emc.ecs.metadata.dao.elasticsearch;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;

import com.emc.ecs.management.entity.NamespaceDetail;
import com.emc.ecs.management.entity.NamespaceQuota;
import com.emc.ecs.metadata.dao.NamespaceDAO;
import com.emc.ecs.metadata.utils.Constants;

/**
 * @author nlengc
 *
 */
public abstract class ElasticNamespaceDAO implements NamespaceDAO {

	private final static String CLIENT_SNIFFING_CONFIG = "client.transport.sniff";
	private final static String CLIENT_CLUSTER_NAME_CONFIG = "cluster.name";
	public final static String COLLECTION_TIME = "collection_time";
	public final static String ANALYZED_TAG = "_analyzed";
	public final static String NOT_ANALYZED_INDEX = "not_analyzed";
	public final static String ANALYZED_INDEX = "analyzed";

	protected TransportClient elasticClient;
	protected static final String DATA_DATE_PATTERN = "yyyy-MM-dd";
	protected static final SimpleDateFormat DATA_DATE_FORMAT = new SimpleDateFormat(DATA_DATE_PATTERN);

	public ElasticNamespaceDAO(ElasticDAOConfig config) {
		try {
			Builder builder = Settings.builder();
			// Check for new hosts within the cluster
			builder.put(CLIENT_SNIFFING_CONFIG, true);
			if (config.getXpackUser() != null) {
				builder.put(Constants.XPACK_SECURITY_USER, config.getXpackUser() + ":" + config.getXpackPassword());
				builder.put(Constants.XPACK_SSL_KEY, config.getXpackSslKey());
				builder.put(Constants.XPACK_SSL_CERTIFICATE, config.getXpackSslCertificate());
				builder.put(Constants.XPACK_SSL_CERTIFICATE_AUTH, config.getXpackSslCertificateAuthothorities());
				builder.put(Constants.XPACK_SECURITY_TRANPORT_ENABLED, "true");
			}
			// specify cluster name
			if (config.getClusterName() != null) {
				builder.put(CLIENT_CLUSTER_NAME_CONFIG, config.getClusterName());
			}
			Settings settings = builder.build();
			// create client
			if (config.getXpackUser() != null) {
				elasticClient = new PreBuiltXPackTransportClient(settings);
			} else {
				elasticClient = new PreBuiltTransportClient(settings);
			}
			// add hosts
			for (String elasticHost : config.getHosts()) {
				elasticClient.addTransportAddress(
						new InetSocketTransportAddress(InetAddress.getByName(elasticHost), config.getPort()));
			}
		} catch (UnknownHostException e) {
			throw new RuntimeException("Unable to initialize Elasticsearch client " + e.getLocalizedMessage());
		}
	}

	@Override
	public void initIndexes(Date collectionTime) {
	}

	@Override
	public void insert(NamespaceDetail namespaceDetail, Date collectionTime) {
	}

	@Override
	public Long purgeOldData(NamespaceDataType type, Date thresholdDate) {
		return 0L;
	}

	@Override
	public void insert(NamespaceQuota namespacequota, Date collectionTime) {
	}
}
