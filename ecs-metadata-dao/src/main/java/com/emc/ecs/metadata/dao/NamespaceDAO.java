/**
 * 
 */
package com.emc.ecs.metadata.dao;

import java.util.Date;

import com.emc.ecs.management.entity.NamespaceDetail;
import com.emc.ecs.management.entity.NamespaceQuota;

/**
 * @author nlengc
 *
 */
public interface NamespaceDAO {
	
	public enum NamespaceDataType {
		namespace_detail,
		namespace_quota
	};

	/**
	 * Init indexes
	 * 
	 * @param collectionTime
	 *            - collection time
	 */
	public void initIndexes(Date collectionTime);

	/**
	 * Inserts details namespace info into datastore
	 * @param namespaceDetail
	 * @param collectionTime
	 */
	public void insert(NamespaceDetail namespaceDetail, Date collectionTime);
	
	/**
	 * Inserts quota namespace info into datastore
	 * @param namespacequota
	 * @param collectionTime
	 */
	public void insert(NamespaceQuota namespacequota, Date collectionTime);
}
