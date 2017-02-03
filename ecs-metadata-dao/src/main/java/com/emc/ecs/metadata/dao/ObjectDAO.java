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

import com.emc.object.s3.bean.ListObjectsResult;
import com.emc.object.s3.bean.ListVersionsResult;
import com.emc.object.s3.bean.QueryObjectsResult;

public interface ObjectDAO {

	public enum ObjectDataType {
		object,
		object_versions
	};
	
	/**
	 * Init indexes
	 * @param collectionTime - collection time
	 */
	public void initIndexes(Date collectionTime);
	
	/**
	 * Inserts list object data into datastore
	 * @param listObjectsResult - list object result
	 * @param namespace - namespace
	 * @param bucketName - bucket name
	 * @param collectionTime - collection time
	 */
	public void insert( ListObjectsResult listObjectsResult, String namespace,
						String bucketName, Date collectionTime );
	
	/**
	 * Inserts query object data into datastore
	 * 
	 * @param queryObjectsResult - query object result
	 * @param namespace - namespace
	 * @param bucketName - bucket name
	 * @param collectionTime - collection time
	 */
	public void insert( QueryObjectsResult queryObjectsResult, String namespace,
						String bucketName, Date collectionTime );

	/**
	 * Inserts object versions data into datastore
	 * @param listVersionsResult - list version result
	 * @param namespace - namespace
	 * @param name - name
	 * @param collectionTime - collection time
	 */
	public void insert( ListVersionsResult listVersionsResult, String namespace,
						String name, Date collectionTime);
	
	
	/**
	 * Purges object data collected before a certain date
	 * 
	 * @param type - object data type
	 * @param collectionTime - collection time
	 * @return Long
	 */
	public Long purgeOldData( ObjectDataType type, Date collectionTime);
	
	
}
