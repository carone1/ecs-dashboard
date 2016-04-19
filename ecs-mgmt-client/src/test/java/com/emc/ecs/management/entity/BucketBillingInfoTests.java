package com.emc.ecs.management.entity;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;

import junit.framework.Assert;


public class BucketBillingInfoTests {

    
    
    @Test
    public void testBucketBillingInfoXml() throws Exception {
    	
     	String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +     			
     			"<bucket_billing_info>" +
     				"<name>name</name>" +
     				"<namespace>namespace</namespace>" +
     				"<vpool_id>vpoolId</vpool_id>" +
     				"<total_size>1000</total_size>" +
     				"<total_size_unit>GB</total_size_unit>" +
     				"<total_objects>100</total_objects>" +
     				"<sample_time>sampleTime</sample_time>" +
     				"<TagSet>" +
     					"<Tag>" +
     						"<Key>key</Key>" +
     						"<Value>value</Value>" +
     					"</Tag>" +
     				"</TagSet>" +
     	        "</bucket_billing_info>";
    	
 
    	BucketBillingInfo bucketBillingInfo = new BucketBillingInfo();
    	bucketBillingInfo.setName("name");
    	bucketBillingInfo.setNamespace("namespace");
    	bucketBillingInfo.setSampleTime("sampleTime");
    	Tag tag = new Tag();
    	tag.setKey("key");
    	tag.setValue("value");
    	List<Tag> tagList = new ArrayList<Tag>();
    	tagList.add(tag);
    	bucketBillingInfo.setTagSet(tagList);
    	bucketBillingInfo.setTotalObjects(100L);
    	bucketBillingInfo.setTotalSize(1000L);
    	bucketBillingInfo.setTotalSizeUnit("GB");
    	bucketBillingInfo.setVpoolId("vpoolId");
    	
 
    	
    	JAXBContext jaxbContext = JAXBContext.newInstance( BucketBillingInfo.class );    	
    	Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
    	
    	jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, false );    	
    	OutputStream byteOut = new ByteArrayOutputStream();    	
    	jaxbMarshaller.marshal( bucketBillingInfo, byteOut );
    	String bytesOutStr = byteOut.toString();
    	
    	System.out.println(bytesOutStr);
    	
    	Assert.assertEquals( "xml is not matching", expectedOutput, bytesOutStr);

    }
	
	
	
}
