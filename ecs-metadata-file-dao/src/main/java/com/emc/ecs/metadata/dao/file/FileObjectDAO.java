package com.emc.ecs.metadata.dao.file;



import java.util.Date;

import com.emc.ecs.metadata.dao.ObjectDAO;
import com.emc.object.s3.bean.ListObjectsResult;
import com.emc.object.s3.bean.S3Object;


public class FileObjectDAO implements ObjectDAO {
	

	@Override
	public void insert( ListObjectsResult listObjectsResult, 
			 			String namesapce,
			 			String bucket,
			 			Date collectionTime) {

		System.out.println("namespace: " + namesapce + " bucket: " + bucket);
		
		for( S3Object s3Object : listObjectsResult.getObjects() ) {
			System.out.println("Object key: " + s3Object.getKey() );

			//			System.out.println("  lastModified: " + s3Object.getLastModified());
			//			System.out.println("  eTag: " + s3Object.getETag());
			//			System.out.println("  rawETag: " + s3Object.getRawETag());
			//		    System.out.println("  size: " + s3Object.getSize());
			//		    System.out.println("  storageClass: " + s3Object.getStorageClass().toString());
			//		    System.out.println("  owner: " + s3Object.getOwner().toString());
		}
		
	}

}
