package com.emc.ecs.management.entity;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;
import org.junit.Assert;



public class ObjectUserTests {

    
    
    @Test
    public void testObjectUserXml() throws Exception {
    	
     	String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
     							"<blobuser>" +
     							"<userid>userId</userid>" +
     							"<namespace>namespace</namespace>" +
     							"</blobuser>";
    	
 
    	ObjectUser objectUser = new ObjectUser();
    	objectUser.setNamespace(new URI("namespace"));
    	objectUser.setUserId(new URI("userId"));
     	
    	JAXBContext jaxbContext = JAXBContext.newInstance( ObjectUser.class );    	
    	Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
    	
    	jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, false );    	
    	OutputStream byteOut = new ByteArrayOutputStream();    	
    	jaxbMarshaller.marshal( objectUser, byteOut );
    	String bytesOutStr = byteOut.toString();
    	
    	System.out.println(bytesOutStr);
    	
    	Assert.assertEquals( "xml is not matching", expectedOutput, bytesOutStr);

    }
	
	
	
}
