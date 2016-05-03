package com.emc.ecs.metadata.dao.elasticsearch.entity;

import java.io.IOException;
import java.util.Date;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.emc.ecs.management.entity.BucketBillingInfo;


public class ElasticBucketBillingInfo {

	public static final String COLLECTION_TIME = "collection_time";
	
//	public static XContentBuilder toJsonFormat(BucketBillingInfo bucketInfo, 
//												Date collectionTime,
//												XContentBuilder builder) {
//		
//		try {
//			if(builder == null) {
//				builder = XContentFactory.jsonBuilder();
//			}
//			
//			// initial portion
//			builder = builder.startObject()	    
//			 .field(BucketBillingInfo.NAME_TAG, bucketInfo.getName())
//			 .field(BucketBillingInfo.NAMESPACE_TAG, bucketInfo.getNamespace())
//			 .field(BucketBillingInfo.TOTAL_OBJECTS_TAG, bucketInfo.getTotalObjects())			
//			 .field(BucketBillingInfo.TOTAL_SIZE_TAG, bucketInfo.getTotalSize())
//			 .field(BucketBillingInfo.TOTAL_SIZE_UNIT_TAG, bucketInfo.getTotalSizeUnit())
//			 .field(BucketBillingInfo.VPOOL_ID_TAG, bucketInfo.getVpoolId())
//			 .field(COLLECTION_TIME, collectionTime)
//	        .endObject();
//									
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}	
//		
//		return builder;
//	}
//	
//	
//	public static XContentBuilder toJsonFormat( BucketBillingInfo bucketInfo, Date collectionTime ) {						
//		return toJsonFormat(bucketInfo, collectionTime, null);
//	}
}
