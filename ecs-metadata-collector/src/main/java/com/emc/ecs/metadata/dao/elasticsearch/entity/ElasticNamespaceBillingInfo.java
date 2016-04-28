package com.emc.ecs.metadata.dao.elasticsearch.entity;

import java.io.IOException;


import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.emc.ecs.management.entity.BucketBillingInfo;
import com.emc.ecs.management.entity.NamespaceBillingInfo;

public class ElasticNamespaceBillingInfo {

	
	
	public static XContentBuilder toJsonFormat(NamespaceBillingInfo billingInfo, XContentBuilder builder) {
		
		try {
			if(builder == null) {
				builder = XContentFactory.jsonBuilder();
			}
			

			
			// namespace portion
			builder = builder.startObject()
			 .field(NamespaceBillingInfo.TOTAL_SIZE_TAG, billingInfo.getTotalSize())
			 .field(NamespaceBillingInfo.TOTAL_SIZE_UNIT_TAG, billingInfo.getTotalSizeUnit())     
			 .field(NamespaceBillingInfo.TOTAL_OBJECTS_TAG, billingInfo.getTotalObjects())
			 .field(NamespaceBillingInfo.NAMESPACE_TAG, billingInfo.getNamespace())
			 .endObject();
			
			
			// bucket portion
			for( BucketBillingInfo bucketInfo : billingInfo.getBucketBillingInfo()) {
				builder = ElasticBucketBillingInfo.toJsonFormat(bucketInfo, builder);
			}
						
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return builder;
	}
	
	
	public static XContentBuilder toJsonFormat( NamespaceBillingInfo billingInfo ) {						
		return toJsonFormat(billingInfo, null);
	}
}
