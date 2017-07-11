/**
 * 
 */
package com.emc.ecs.metadata.bo;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.ecs.management.client.ManagementClient;
import com.emc.ecs.management.client.ManagementClientConfig;
import com.emc.ecs.management.entity.Namespace;
import com.emc.ecs.management.entity.NamespaceQuota;
import com.emc.ecs.management.entity.NamespaceRequest;
import com.emc.ecs.management.entity.VdcDetails;
import com.emc.ecs.metadata.dao.VdcDAO;

/**
 * @author nlengc
 *
 */
public class VdcBO {
	private final static Logger LOGGER = LoggerFactory.getLogger(NamespaceBO.class);
	private ManagementClient client;
	private VdcDAO vdcDAO;
	private AtomicLong objectCount;

	public VdcBO(String mgmtAccessKey, String mgmtSecretKey, List<String> hosts, Integer port, VdcDAO vdcDAO,
			AtomicLong objectCount) {
		// client config
		ManagementClientConfig clientConfig = new ManagementClientConfig(mgmtAccessKey, mgmtSecretKey, port, hosts);
		this.client = new ManagementClient(clientConfig);
		this.vdcDAO = vdcDAO;
		this.objectCount = objectCount;
	}

	/**
	 * Gathers all namespaces quota present on a cluster
	 * 
	 * @return List - List of namespace quota
	 */
	public void collectVdcDetails(Date collectionTime) {
		long objCounter = 0;
		LOGGER.info("Collecting all VDC on cluster. ");
		VdcDetails vdcDetails = client.getVdcDetails();
		if (vdcDetails == null || vdcDetails.getVdcDetails() == null || vdcDetails.getVdcDetails().isEmpty()) {
			return;
		}
		objCounter = objCounter + vdcDetails.getVdcDetails().size();
		// Push collected details into datastore
		if (vdcDAO != null) {
			vdcDAO.insert(vdcDetails, collectionTime);
		}
		// peg global counter
		this.objectCount.getAndAdd(objCounter);
	}

	public void shutdown() {
		if(this.client != null) {
			client.shutdown();
		}
	}

}
