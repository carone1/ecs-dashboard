package com.emc.ecs.management.entity;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;
import org.junit.Assert;


public class NodeTest {

    
    
    @Test
    public void testNodeXml() throws Exception {
    	
     	String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
     							"<node>" +
     							"<nodename>nodename</nodename>" +
     							"<ip>ip</ip>" +
     							"<nodeid>nodeid</nodeid>" +
     							"<rackId>rackid</rackId>" +
     							"<version>version</version>" +
     							"</node>";    	
 
    	Node node = new Node();
    	node.setNodeName("nodename");
    	node.setIp("ip");
    	node.setNodeid("nodeid");
    	node.setRackId("rackid");
    	node.setVersion("version");
    	
    	JAXBContext jaxbContext = JAXBContext.newInstance( Node.class );    	
    	Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
    	
    	jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, false );    	
    	OutputStream byteOut = new ByteArrayOutputStream();    	
    	jaxbMarshaller.marshal( node, byteOut );
    	String bytesOutStr = byteOut.toString();
    	
    	System.out.println(bytesOutStr);
    	
    	Assert.assertEquals( "xml is not matching", expectedOutput, bytesOutStr);

    }
	
	
	
}
