package com.emc.ecs.management.entity;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;
import org.junit.Assert;

public class TagTest {

    
    
    @Test
    public void testTagXml() throws Exception {
    	
     	String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
     							"<namespace><Key>key</Key><Value>value</Value></namespace>";
    	
 
    	Tag tag = new Tag();
    	tag.setKey("key");
    	tag.setValue("value");
 
    	
    	JAXBContext jaxbContext = JAXBContext.newInstance( Tag.class );    	
    	Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
    	
    	jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, false );    	
    	OutputStream byteOut = new ByteArrayOutputStream();    	
    	jaxbMarshaller.marshal( tag, byteOut );
    	String bytesOutStr = byteOut.toString();
    	
    	System.out.println(bytesOutStr);
    	
    	Assert.assertEquals( "xml is not matching", expectedOutput, bytesOutStr);

    }
	
	
	
}
