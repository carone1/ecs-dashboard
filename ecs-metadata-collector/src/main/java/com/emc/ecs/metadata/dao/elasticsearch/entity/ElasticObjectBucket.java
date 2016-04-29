package com.emc.ecs.metadata.dao.elasticsearch.entity;

import java.io.IOException;
import java.util.Date;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.emc.ecs.management.entity.BucketBillingInfo;
import com.emc.ecs.management.entity.ObjectBucket;
import com.emc.ecs.management.entity.ObjectBuckets;


public class ElasticObjectBucket {

	public static final String COLLECTION_TIME = "collection_time";
	
	public static XContentBuilder toJsonFormat( ObjectBucket objectBucket, 
											    Date collectionTime,
											    XContentBuilder builder) {
		
		try {
			if(builder == null) {
				builder = XContentFactory.jsonBuilder();
			}
			
			// initial portion
			builder = builder.startObject()	
					.field(ObjectBucket.CREATED_TAG, objectBucket.getCreated())            				     
					.field(ObjectBucket.SOFT_QUOTA_TAG, objectBucket.getSoftQuota())
					.field(ObjectBucket.FS_ACCESS_ENABLED_TAG, objectBucket.getFsAccessEnabled())
					.field(ObjectBucket.LOCKED_TAG, objectBucket.getLocked())
					.field(ObjectBucket.V_POOL_TAG, objectBucket.getVpool())
					.field(ObjectBucket.NAMESPACE_TAG, objectBucket.getNamespace())
					.field(ObjectBucket.OWNER_TAG, objectBucket.getOwner())
					.field(ObjectBucket.IS_STALE_ALLOWED_TAG, objectBucket.getIsStaleAllowed())
					.field(ObjectBucket.IS_ENCRYPTION_ENABLED_TAG, objectBucket.getIsEncryptionEnabled())
					.field(ObjectBucket.DEFAULT_RETENTION_TAG, objectBucket.getDefaultRetention())
					.field(ObjectBucket.BLOCK_SIZE_TAG, objectBucket.getBlockSize())
					.field(ObjectBucket.NOTIFICATION_SIZE_TAG, objectBucket.getNotificationSize())
					.field(ObjectBucket.API_TYPE_TAG, objectBucket.getApiType())
					.field(ObjectBucket.TAG_SET_TAG, objectBucket.getTagSet())
					.field(ObjectBucket.RETENTION_TAG, objectBucket.getRetention())
					.field(ObjectBucket.DEFAULT_GROUP_FILE_READ_PERMISSION_TAG, objectBucket.getDefaultGroupFileReadPermission())
					.field(ObjectBucket.DEFAULT_GROUP_FILE_WRITE_PERMISSION_TAG, objectBucket.getDefaultGroupFileWritePermission())
					.field(ObjectBucket.DEFAULT_GROUP_FILE_EXECUTE_PERMISSION_TAG, objectBucket.getDefaultGroupFileExecutePermission())
					.field(ObjectBucket.DEFAULT_GROUP_DIR_READ_PERMISSION_TAG, objectBucket.getDefaultGroupDirReadPermission())
					.field(ObjectBucket.DEFAULT_GROUP_DIR_WRITE_PERMISSION_TAG, objectBucket.getDefaultGroupDirWritePermission())
					.field(ObjectBucket.DEFAULT_GROUP_DIR_EXECUTE_PERMISSION_TAG, objectBucket.getDefaultGroupDirExecutePermission())
					.field(ObjectBucket.DEFAULT_GROUP_TAG, objectBucket.getDefaultGroup())
					// To decide if search tag should be included
					//.field(ObjectBucket.SEARCH_METADATA_TAG, objectBucket.getSearchMetadata())					
					.field(ObjectBucket.NAME_TAG, objectBucket.getName())
					.field(ObjectBucket.ID_TAG, objectBucket.getId())
					.field(ObjectBucket.LINK_TAG, objectBucket.getLink())			     
					.field(ObjectBucket.CREATION_TIME_TAG, objectBucket.getCreationTime())			     
					.field(ObjectBucket.INACTIVE_TAG, objectBucket.getInactive())
					.field(ObjectBucket.GLOBAL_TAG, objectBucket.getGlobal())
					.field(ObjectBucket.REMOTE_TAG, objectBucket.getRemote())
					.field(ObjectBucket.VDC_TAG, objectBucket.getVdc())
					.field(ObjectBucket.INTERNAL_TAG, objectBucket.getInternal())								
			 .field(COLLECTION_TIME, collectionTime)
	        .endObject();
									
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return builder;
	}
	
	
	public static XContentBuilder toJsonFormat( ObjectBucket objectBucket, Date collectionTime ) {						
		return toJsonFormat(objectBucket, collectionTime, null);
	}
}
