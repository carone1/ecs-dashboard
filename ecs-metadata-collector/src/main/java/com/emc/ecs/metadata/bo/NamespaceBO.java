/**
 * 
 */
package com.emc.ecs.metadata.bo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.elasticsearch.transport.ReceiveTimeoutTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.ecs.management.client.ManagementClient;
import com.emc.ecs.management.client.ManagementClientConfig;
import com.emc.ecs.management.entity.ListNamespaceRequest;
import com.emc.ecs.management.entity.ListNamespacesResult;
import com.emc.ecs.management.entity.Namespace;
import com.emc.ecs.management.entity.NamespaceDetail;
import com.emc.ecs.management.entity.NamespaceQuota;
import com.emc.ecs.management.entity.NamespaceRequest;
import com.emc.ecs.metadata.dao.NamespaceDAO;

/**
 * @author nlengc
 *
 */
public class NamespaceBO {

	private final static Logger LOGGER = LoggerFactory.getLogger(NamespaceBO.class);
	private ManagementClient client;
	private NamespaceDAO namespaceDAO;
	private AtomicLong objectCount;

	public NamespaceBO(String mgmtAccessKey, String mgmtSecretKey, List<String> hosts, Integer port,
			NamespaceDAO namespaceDAO, AtomicLong objectCount) {

		// client config
		ManagementClientConfig clientConfig = new ManagementClientConfig(mgmtAccessKey, mgmtSecretKey, port, hosts);

		this.client = new ManagementClient(clientConfig);
		this.namespaceDAO = namespaceDAO;
		this.objectCount = objectCount;
	}

	/**
	 * Gathers all namespaces present on a cluster
	 * 
	 * @return List - List of namespace
	 */
	public List<Namespace> getNamespaces() {

		List<Namespace> namespaceList = new ArrayList<Namespace>();
		ListNamespaceRequest listNamespaceRequest = new ListNamespaceRequest();

		// first batch
		ListNamespacesResult namespacesResult = client.listNamespaces(listNamespaceRequest);
		namespaceList.addAll(namespacesResult.getNamespaces());

		// n subsequent batches
		while (namespacesResult.getNextMarker() != null) {
			listNamespaceRequest.setNextMarker(namespacesResult.getNextMarker());
			namespacesResult = client.listNamespaces(listNamespaceRequest);
			if (namespacesResult.getNamespaces() != null) {
				namespaceList.addAll(namespacesResult.getNamespaces());
			}
		}

		return namespaceList;
	}

	/**
	 * Gathers all namespaces details present on a cluster
	 * 
	 * @return List - List of namespace details
	 */
	public void collectNamespaceDetails(Date collectionTime) {

		// Start collecting namespace data details from ECS systems
		List<Namespace> namespaceList = getNamespaces();
		// At this point we should have all the namespace supported by the ECS
		// system
		try {
			long objCounter = 0;
			for (Namespace namespace : namespaceList) {
				LOGGER.info("Collecting Details for namespace: " + namespace.getName());
				NamespaceDetail namespaceDetail = client.getNamespaceDetails(namespace.getId());

				if (namespaceDetail == null) {
					continue;
				}
				objCounter++;
				// Push collected details into datastore
				if (namespaceDAO != null) {
					// insert something
					namespaceDAO.insert(namespaceDetail, collectionTime);
				}
			}

			// peg global counter
			this.objectCount.getAndAdd(objCounter);
		} catch (ReceiveTimeoutTransportException re) {
			LOGGER.error("Data collection will be aborted due to an error while connecting to ElasticSearch Cluster ",
					re);
			System.exit(1);
		}
	}
	
	/**
	 * Gathers all namespaces quota present on a cluster
	 * 
	 * @return List - List of namespace quota
	 */
	public void collectNamespaceQuota(Date collectionTime) {

		// Start collecting namespace data quota from ECS systems
		List<Namespace> namespaceList = getNamespaces();
		// At this point we should have all the namespace supported by the ECS
		// system
		try {
			long objCounter = 0;

			for (Namespace namespace : namespaceList) {

				NamespaceRequest namespaceRequest = new NamespaceRequest();
				namespaceRequest.setName(namespace.getName());

				LOGGER.info("Collecting Quota Details for namespace: " + namespace.getName());
				NamespaceQuota namespaceQuota = client.getNamespaceQuota(namespaceRequest);

				if (namespaceQuota == null) {
					continue;
				}

				objCounter++;

				// Push collected details into datastore
				if (namespaceDAO != null) {
					// insert something
					namespaceDAO.insert(namespaceQuota, collectionTime);
				}

			}

			// peg global counter
			this.objectCount.getAndAdd(objCounter);
		} catch (ReceiveTimeoutTransportException re) {
			LOGGER.error("Data collection will be aborted due to an error while connecting to ElasticSearch Cluster ",
					re);
			System.exit(1);
		}
	}

	public void shutdown() {
		if(this.client != null) {
			client.shutdown();
		}
	}

}
