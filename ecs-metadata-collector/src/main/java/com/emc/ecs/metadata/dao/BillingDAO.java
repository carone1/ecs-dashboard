package com.emc.ecs.metadata.dao;

import java.util.Date;

import com.emc.ecs.management.entity.NamespaceBillingInfo;
import com.emc.ecs.management.entity.ObjectBuckets;

public interface BillingDAO {

	/**
	 * 
	 */
	public void insert(NamespaceBillingInfo billingData, Date collectionTime);
	public void insert(ObjectBuckets bucketResponse, Date collectionTime);
}
