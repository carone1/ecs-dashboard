/**
 * 
 */
package com.emc.ecs.metadata.dao;

import java.util.Date;

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
}
