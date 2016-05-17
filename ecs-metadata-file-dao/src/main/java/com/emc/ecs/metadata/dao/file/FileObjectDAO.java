package com.emc.ecs.metadata.dao.file;



import java.util.Date;

import com.emc.ecs.metadata.dao.ObjectDAO;
import com.emc.object.s3.bean.ListObjectsResult;
import com.emc.object.s3.bean.QueryObject;
import com.emc.object.s3.bean.QueryObjectsResult;


public class FileObjectDAO implements ObjectDAO {
	

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

	@Override
	public void insert( QueryObjectsResult queryObjectsResult, String namespace,
			String bucketName, Date collectionTime ) {
		
		System.out.println("namespace: " + namespace + " bucket: " + bucketName);
		for( QueryObject queryObject : queryObjectsResult.getObjects() ) {
			System.out.println("Object key: " + queryObject.getObjectName() );
		}
		
		return;
	}

}
