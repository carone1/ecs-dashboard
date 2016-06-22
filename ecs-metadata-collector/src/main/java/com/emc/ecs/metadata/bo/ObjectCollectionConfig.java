/*
 * Copyright (c) 2016, EMC Corporation.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *     + Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     + Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     + The name of EMC Corporation may not be used to endorse or promote
 *       products derived from this software without specific prior written
 *       permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
