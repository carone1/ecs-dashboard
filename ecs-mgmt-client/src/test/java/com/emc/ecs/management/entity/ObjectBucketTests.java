package com.emc.ecs.management.entity;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;

import junit.framework.Assert;


public class ObjectBucketTests {

    
    
    @Test
    public void testObjectBucketXml() throws Exception {
    	
     	String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
     							"<object_bucket>" +
     							"<created>created</created>" +
     							"<softquota>softQuota</softquota>" +
     							"<fs_access_enabled>true</fs_access_enabled>" +
     							"<locked>false</locked>" +
     							"<vpool>vpool</vpool>" +
     							"<namespace>namespace</namespace>" +
     							"<owner>owner</owner>" +
     							"<is_stale_allowed>true</is_stale_allowed>" +
     							"<is_encryption_enabled>enabled</is_encryption_enabled>" +
     							"<default_retention>1</default_retention>" +
     							"<block_size>100</block_size>" +
     							"<notification_size>10</notification_size>" +
     							"<api_type>apiType</api_type>" +
     							"<TagSet>" +
     								"<Key>key</Key>" +
     								"<Value>value</Value>" +
     							"</TagSet>" +
     							"<retention>100</retention>" +
     							"<default_group_file_read_permission>true</default_group_file_read_permission>" +
     							"<default_group_file_write_permission>true</default_group_file_write_permission>" +
     							"<default_group_file_execute_permission>true</default_group_file_execute_permission>" +
     							"<default_group_dir_read_permission>true</default_group_dir_read_permission>" +
     							"<default_group_dir_write_permission>true</default_group_dir_write_permission>" +
     							"<default_group_dir_execute_permission>true</default_group_dir_execute_permission>" +
     							"<default_group>defaultGroup</default_group>" +
     							"<search_metadata>" +
     								"<metadata>" +
     									"<type>type</type>" +
     									"<name>name</name>" +
     									"<datatype>dataType</datatype>" +
     								"</metadata>" +
     							"</search_metadata>" +
     							"<name>name</name>" +
     							"<id>id</id>" +
     							"<link>link</link>" +
     							//"<creation_file></creation_file>" +
     							"<inactive>false</inactive>" +
     							"<global>true</global>" +
     							"<remote>false</remote>" +
     							"<vdc>" +
     								"<id>id</id>" +
     								"<link>link</link>" +
     							"</vdc>" +
     							"<internal>false</internal>" +
     							"</object_bucket>";
     							
    	
 
    	ObjectBucket objectBucket = new ObjectBucket();
    	objectBucket.setApiType("apiType");
    	objectBucket.setBlockSize(100L);
    	objectBucket.setCreated("created");
    	//objectBucket.setCreationTime(new Date());
    	objectBucket.setDefaultGroup("defaultGroup");
    	objectBucket.setDefaultGroupDirExecutePermission(true);
    	objectBucket.setDefaultGroupDirReadPermission(true);
    	objectBucket.setDefaultGroupDirWritePermission(true);
    	objectBucket.setDefaultGroupFileExecutePermission(true);
    	objectBucket.setDefaultGroupFileReadPermission(true);
    	objectBucket.setDefaultGroupFileWritePermission(true);
    	objectBucket.setDefaultRetention(1L);
    	objectBucket.setFsAccessEnabled(true);
    	objectBucket.setGlobal(true);
    	objectBucket.setId(new URI("id"));
    	objectBucket.setInactive(false);
    	objectBucket.setInternal(false);
    	objectBucket.setIsEncryptionEnabled("enabled");
    	objectBucket.setIsStaleAllowed(true);
    	objectBucket.setLink("link");
    	objectBucket.setLocked(false);
    	objectBucket.setName("name");
    	objectBucket.setNamespace("namespace");
    	objectBucket.setNotificationSize(10L);
    	objectBucket.setOwner("owner");
    	objectBucket.setRemote(false);
    	objectBucket.setRetention(100L);
    	
    	Metadata metadata = new Metadata();
    	metadata.setDataType("dataType");
    	metadata.setName("name");
    	metadata.setType("type");
    	List<Metadata> searchMetadata = new ArrayList<Metadata>();
    	searchMetadata.add(metadata);    	
    	objectBucket.setSearchMetadata(searchMetadata);
    	
    	objectBucket.setSoftQuota("softQuota");
    	
    	Tag tag = new Tag();
    	tag.setKey("key");
    	tag.setValue("value");
    	List<Tag> tagSet = new ArrayList<Tag>();
    	tagSet.add(tag);
    	objectBucket.setTagSet(tagSet);
    	
    	Vdc vdc = new Vdc();
    	vdc.setId(new URI("id"));
    	vdc.setLink("link");
    	objectBucket.setVdc(vdc);
    	
    	objectBucket.setVpool("vpool");
    	
    	
    	JAXBContext jaxbContext = JAXBContext.newInstance( ObjectBucket.class );    	
    	Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
    	
    	jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, false );    	
    	OutputStream byteOut = new ByteArrayOutputStream();    	
    	jaxbMarshaller.marshal( objectBucket, byteOut );
    	String bytesOutStr = byteOut.toString();
    	
    	System.out.println(bytesOutStr);
    	
    	Assert.assertEquals( "xml is not matching", expectedOutput, bytesOutStr);

    }
	
	
	
}
