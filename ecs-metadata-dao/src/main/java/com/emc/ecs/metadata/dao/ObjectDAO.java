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
	
	
	public void insert( ListObjectsResult listObjectsResult, String namespace,
						String bucketName, Date collectionTime );
	
	public void insert( QueryObjectsResult queryObjectsResult, String namespace,
						String bucketName, Date collectionTime );

	public void insert(ListVersionsResult listVersionsResult, String namespace,
			String name, Date collectionTime);
	
	public void purgeOldData( ObjectDataType type, Date collectionTime);
	
	
}
