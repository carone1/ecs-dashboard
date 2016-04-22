package com.emc.ecs.metadata.dao;

import com.emc.ecs.management.entity.NamespaceBillingInfoResponse;
import com.emc.ecs.management.entity.ObjectBucketsResponse;

public interface BillingDAO {

	/**
	 * 
	 */
	public void insert(NamespaceBillingInfoResponse billingData);
	public void insert(ObjectBucketsResponse bucketResponse);
}
