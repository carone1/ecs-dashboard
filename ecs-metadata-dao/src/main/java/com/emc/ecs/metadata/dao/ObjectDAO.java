package com.emc.ecs.metadata.dao;

import java.util.Date;

import com.emc.object.s3.bean.ListObjectsResult;

public interface ObjectDAO {

	
	public void insert( ListObjectsResult listObjectsResult, String namespace,
						String bucketName, Date collectionTime );
	
	
}
