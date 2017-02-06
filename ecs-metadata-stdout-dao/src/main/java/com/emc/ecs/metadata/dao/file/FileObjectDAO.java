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



package com.emc.ecs.metadata.dao.file;


import java.util.Date;

import com.emc.ecs.metadata.dao.ObjectDAO;
import com.emc.object.s3.bean.ListObjectsResult;
import com.emc.object.s3.bean.ListVersionsResult;
import com.emc.object.s3.bean.QueryObjectsResult;
import com.emc.object.s3.bean.S3Object;


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
		//return;
		
		System.out.println("namespace: " + namespace + " bucket: " + bucket);
		
				for( S3Object s3Object : listObjectsResult.getObjects() ) {
					System.out.println("Object key: " + s3Object.getKey() );
		//			System.out.println("  lastModified: " + s3Object.getLastModified().toString());
		//			System.out.println("  rawETag: " + s3Object.getRawETag());
					System.out.println("  size: " + s3Object.getSize());
		//			System.out.println("  storageClass: " + s3Object.getStorageClass().toString());
		//			System.out.println("  owner: " + s3Object.getOwner().toString());		    
				}
		
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

	@Override
	public void initIndexes(Date collectionTime) {
		// TODO Auto-generated method stub
		
	}

}
