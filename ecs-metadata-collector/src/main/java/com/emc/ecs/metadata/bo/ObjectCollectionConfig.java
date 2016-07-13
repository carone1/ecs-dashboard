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


package com.emc.ecs.metadata.bo;

import java.util.Date;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

import com.emc.ecs.management.entity.ObjectBucket;
import com.emc.ecs.metadata.dao.ObjectDAO;
import com.emc.object.s3.jersey.S3JerseyClient;

public class ObjectCollectionConfig {

	//========================
	// Private members
	//========================
	private String                                namespace;
	private S3JerseyClient                        s3JerseyClient;
	private ObjectDAO                             objectDAO;
	private Date                                  collectionTime;
	private AtomicLong                            objectCount;
	private Map<NamespaceBucketKey, ObjectBucket> bucketMap;
	private ThreadPoolExecutor 					  threadPoolExecutor;
	private Queue<Future<?>>     				  futures;
	private String 								  queryCriteria;
	
	




	public ObjectCollectionConfig( S3JerseyClient                        s3JerseyClient, 
								   String                                namespace, 
								   ObjectDAO                             objectDAO, 
								   Map<NamespaceBucketKey, ObjectBucket> bucketMap,
								   Date                                  collectionTime,
								   AtomicLong                            objectCount,
								   ThreadPoolExecutor 					 threadPoolExecutor,
								   Queue<Future<?>>     				 futures,	        
								   String                                queryCriteria ) {

		this.s3JerseyClient     = s3JerseyClient;
		this.namespace          = namespace;
		this.bucketMap          = bucketMap;
		this.objectDAO          = objectDAO;
		this.collectionTime     = collectionTime;
		this.objectCount        = objectCount;
		this.threadPoolExecutor = threadPoolExecutor;
		this.futures            = futures;
		this.queryCriteria      = queryCriteria;
	}
	
	//=======================
	// Public methods
	//=======================
	
	public ThreadPoolExecutor getThreadPoolExecutor() {
		return threadPoolExecutor;
	}

	public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
		this.threadPoolExecutor = threadPoolExecutor;
	}

	public Queue<Future<?>> getFutures() {
		return futures;
	}

	public void setFutures(Queue<Future<?>> futures) {
		this.futures = futures;
	}

	public String getNamespace() {
		return namespace;
	}


	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}


	public S3JerseyClient getS3JerseyClient() {
		return s3JerseyClient;
	}


	public void setS3JerseyClient(S3JerseyClient s3JerseyClient) {
		this.s3JerseyClient = s3JerseyClient;
	}


	public ObjectDAO getObjectDAO() {
		return objectDAO;
	}


	public void setObjectDAO(ObjectDAO objectDAO) {
		this.objectDAO = objectDAO;
	}

	public Map<NamespaceBucketKey, ObjectBucket> getBucketMap() {
		return bucketMap;
	}

	public void setBucketMap(Map<NamespaceBucketKey, ObjectBucket> bucketMap) {
		this.bucketMap = bucketMap;
	}

	public Date getCollectionTime() {
		return collectionTime;
	}


	public void setCollectionTime(Date collectionTime) {
		this.collectionTime = collectionTime;
	}


	public AtomicLong getObjectCount() {
		return objectCount;
	}


	public void setObjectCount(AtomicLong objectCount) {
		this.objectCount = objectCount;
	}

	public String getQueryCriteria() {
		return queryCriteria;
	}

	public void setQueryCriteria(String queryCriteria) {
		this.queryCriteria = queryCriteria;
	}
}
