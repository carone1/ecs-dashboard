package com.emc.ecs.management.entity;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;
import org.junit.Assert;



public class UserSecretKeysTest {

    
    
    @Test
    public void testGetUserKeysXml() throws Exception {
    	
     	String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
				"<user_secret_keys>" +
				"<secret_key_1>secretKey1</secret_key_1>" +
				"<key_timestamp_1>time1</key_timestamp_1>" +
				"<key_expiry_timestamp_1>expiryTime1</key_expiry_timestamp_1>" +
				"<secret_key_2>secretKey2</secret_key_2>" +
				"<key_timestamp_2>expiryTime2</key_timestamp_2>" +
				"<key_expiry_timestamp_2>expiryTime2</key_expiry_timestamp_2>" +
				"<link>link</link>" +
				"</user_secret_keys>";
    	
 
    	
    	ObjectUserSecretKeysResponse secretKeys = new ObjectUserSecretKeysResponse();
    	secretKeys.setKeyExpiryTimestamp1("expiryTime1");
    	secretKeys.setKeyTimestamp1("time1");
    	secretKeys.setKeyExpiryTimestamp2("expiryTime2");
    	secretKeys.setKeyTimestamp2("expiryTime2");
    	secretKeys.setSecretKey1("secretKey1");
    	secretKeys.setSecretKey2("secretKey2");
    	secretKeys.setLink("link");
    	
    	JAXBContext jaxbContext = JAXBContext.newInstance( ObjectUserSecretKeysResponse.class );    	
    	Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
    	
    	jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, false );    	
    	OutputStream byteOut = new ByteArrayOutputStream();    	
    	jaxbMarshaller.marshal( secretKeys, byteOut );
    	String bytesOutStr = byteOut.toString();
    	
    	Assert.assertEquals( "xml is not matching", expectedOutput, bytesOutStr);

    }
	
	
	
}
