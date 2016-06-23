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


package com.emc.ecs.metadata.dao.file;


import java.util.Date;
import com.emc.ecs.metadata.dao.ObjectDAO;
import com.emc.object.s3.bean.ListObjectsResult;
import com.emc.object.s3.bean.ListVersionsResult;
import com.emc.object.s3.bean.QueryObjectsResult;


/**
 * Class responsible to implement data store operations defined in ObjectDOA
 * @author carone1
 *
 */
public class FileObjectDAO implements ObjectDAO {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert( ListObjectsResult listObjectsResult, 
			 			String namespace,
			 			String bucket,
			 			Date collectionTime) {
		return;
		
		//System.out.println("namespace: " + namespace + " bucket: " + bucket);
		
		//		for( S3Object s3Object : listObjectsResult.getObjects() ) {
		//			System.out.println("Object key: " + s3Object.getKey() );
		//			System.out.println("  lastModified: " + s3Object.getLastModified().toString());
		//			System.out.println("  rawETag: " + s3Object.getRawETag());
		//			System.out.println("  size: " + s3Object.getSize());
		//			System.out.println("  storageClass: " + s3Object.getStorageClass().toString());
		//			System.out.println("  owner: " + s3Object.getOwner().toString());		    
		//		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert( QueryObjectsResult queryObjectsResult, String namespace,
						String bucketName, Date collectionTime ) {
		
		//System.out.println("namespace: " + namespace + " bucket: " + bucketName);
		//for( QueryObject queryObject : queryObjectsResult.getObjects() ) {
		//	System.out.println("Object key: " + queryObject.getObjectName() );
		//}
		
		return;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert( ListVersionsResult listVersionsResult, String namespace,
						String bucketName, Date collectionTime) {
		
		//		System.out.println("namespace: " + namespace + " bucket: " + bucketName);
		//		
		//		for( AbstractVersion abstractVersion : listVersionsResult.getVersions() ) {
		//			Version version = (Version)abstractVersion;
		//			System.out.println("Object key: " + version.getKey() );
		//			System.out.println("  lastModified: " + version.getLastModified().toString());
		//			System.out.println("  rawETag: " + version.getRawETag());
		//			System.out.println("  size: " + version.getSize());
		//			System.out.println("  storageClass: " + version.getStorageClass().toString());
		//			System.out.println("  owner: " + version.getOwner().toString());		    
		//		}
		
		return;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long purgeOldData(ObjectDataType type, Date collectionTime) {
		// doing nothing
		return 0L;
	}

}
