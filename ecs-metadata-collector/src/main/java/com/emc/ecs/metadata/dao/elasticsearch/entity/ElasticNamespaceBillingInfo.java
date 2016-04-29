package com.emc.ecs.metadata.dao.elasticsearch.entity;

import java.io.IOException;
import java.util.Date;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;


import com.emc.ecs.management.entity.NamespaceBillingInfo;

public class ElasticNamespaceBillingInfo {

	public static final String COLLECTION_TIME = "collection_time";
	
	public static XContentBuilder toJsonFormat( NamespaceBillingInfo billingInfo, 
												Date collectionTime, 
												XContentBuilder builder         ) {
		
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
			 .field(COLLECTION_TIME, collectionTime)
			 .endObject();
																									
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return builder;
	}
	
	
	public static XContentBuilder toJsonFormat( NamespaceBillingInfo billingInfo, Date collectionTime ) {						
		return toJsonFormat(billingInfo, collectionTime, null);
	}
}
