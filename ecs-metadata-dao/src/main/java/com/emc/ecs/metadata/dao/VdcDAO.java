/**
 * 
 */
package com.emc.ecs.metadata.dao;

import java.util.Date;
import java.util.List;

import com.emc.ecs.management.entity.BucketOwner;
import com.emc.ecs.management.entity.VdcDetails;

/**
 * @author nlengc
 *
 */
public interface VdcDAO {
	public enum ManagementDataType {
		vdc
	};
	
	/**
	 * Init indexes
	 * @param collectionTime - collection time
	 */
	public void initIndexes(Date collectionTime);
	
	/**
	 * Inserts vdc details into datastore 
	 * @param vdcDetail - vdc data
	 * @param collectionTime - collection time
	 */
	public void insert(VdcDetails vdcDetails, Date collectionTime);

	/**
	 * 
	 * @param bucketOwners
	 * @param collectionTime
	 */
	public void insert(List<BucketOwner> bucketOwners, Date collectionTime);
}
