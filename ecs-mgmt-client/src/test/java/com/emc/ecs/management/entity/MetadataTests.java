package com.emc.ecs.management.entity;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;

import junit.framework.Assert;


public class MetadataTests {

    
    
    @Test
    public void testMetadataXml() throws Exception {
    	
     	String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
     							"<metadata>" +
     							"<type>type</type>" +
     							"<name>name</name>" +
     							"<datatype>dataType</datatype>" +
     							"</metadata>";    	
 
    	Metadata metadata = new Metadata();
    	metadata.setType("type");    	
    	metadata.setName("name");
    	metadata.setDataType("dataType");
    	
    	JAXBContext jaxbContext = JAXBContext.newInstance( Metadata.class );    	
    	Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
    	
    	jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, false );    	
    	OutputStream byteOut = new ByteArrayOutputStream();    	
    	jaxbMarshaller.marshal( metadata, byteOut );
    	String bytesOutStr = byteOut.toString();
    	
    	System.out.println(bytesOutStr);
    	
    	Assert.assertEquals( "xml is not matching", expectedOutput, bytesOutStr);

    }
	
	
	
}
