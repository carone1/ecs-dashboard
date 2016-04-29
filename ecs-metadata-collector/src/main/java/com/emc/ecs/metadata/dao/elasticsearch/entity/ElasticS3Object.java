package com.emc.ecs.metadata.dao.elasticsearch.entity;

import java.io.IOException;
import java.util.Date;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.emc.object.s3.bean.S3Object;


public class ElasticS3Object {

	public static final String COLLECTION_TIME = "collection_time";
	
	public static XContentBuilder toJsonFormat( S3Object s3Object, 
											    Date collectionTime,
												XContentBuilder builder) {
		
		try {
			if(builder == null) {
				builder = XContentFactory.jsonBuilder();
			}
			
			// add relevant fileds
			builder = builder.startObject()	    
			 .field( "last_modified", s3Object.getLastModified())
			 .field( "size", s3Object.getSize())
			 .field( "key", s3Object.getKey())
			 .field( "owner", s3Object.getOwner().toString())			 
			 .field(COLLECTION_TIME, collectionTime)
	        .endObject();
									
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return builder;
	}
	
	
	public static XContentBuilder toJsonFormat( S3Object s3Object, Date collectionTime ) {						
		return toJsonFormat(s3Object, collectionTime, null);
	}
}
