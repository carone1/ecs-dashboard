/*

The MIT License (MIT)

Copyright (c) 2016 EMC Corporation

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/



package com.emc.ecs.metadata.dao;

import java.util.Date;

import com.emc.ecs.management.entity.NamespaceBillingInfo;
import com.emc.ecs.management.entity.ObjectBuckets;



public interface BillingDAO {

	public enum ManagementDataType {
		billing_namespace,
		billing_bucket,
		object_bucket
	};
	
	/**
	 * Init indexes
	 * @param collectionTime - collection time
	 */
	public void initIndexes(Date collectionTime);
	
	/**
	 * Inserts billing namespace info into datastore 
	 * @param billingData - billing data
	 * @param collectionTime - collection time
	 */
	public void insert(NamespaceBillingInfo billingData, Date collectionTime);
	
	
	
	/**
	 * Inserts bucket info into datastore 
	 * @param bucketResponse - bucket response
	 * @param collectionTime - collection time
	 */
	public void insert(ObjectBuckets bucketResponse, Date collectionTime);
	
	/**
	 * Purge date collected before a certain date
	 * @param type - management data type
	 * @param collectionTime - collection time
	 * @return Long
	 */
	public Long purgeOldData( ManagementDataType type, Date collectionTime);
}
