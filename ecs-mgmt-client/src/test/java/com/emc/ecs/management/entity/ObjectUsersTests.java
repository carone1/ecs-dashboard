package com.emc.ecs.management.entity;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;

import junit.framework.Assert;


public class ObjectUsersTests {

    
    
    @Test
    public void testObjectUserXml() throws Exception {
    	
     	String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
     							"<users>" +
     							"<blobuser>" +
     							"<userid>userId</userid>" +
     							"<namespace>namespace</namespace>" +
     							"</blobuser>" +    	     	        
     							"<MaxUsers>10</MaxUsers>" +
     							"<NextMarker>nextMarker</NextMarker>" +
     							"<Filter>filter</Filter>" +
     							"<NextPageLink>10</NextPageLink>" +
     							"</users>";
     	
 
    	ObjectUser objectUser = new ObjectUser();
    	objectUser.setNamespace(new URI("namespace"));
    	objectUser.setUserId(new URI("userId"));
 
    	List<ObjectUser> userList = new ArrayList<ObjectUser>();
    	userList.add(objectUser);
    	
    	ObjectUsers objectUsers = new ObjectUsers();
    	objectUsers.setBlobUser(userList);
    	objectUsers.setFilter("filter");
    	objectUsers.setMaxUsers(10);
    	objectUsers.setNextMarker("nextMarker");
    	objectUsers.setNextPathLink(10L);
    	
    	JAXBContext jaxbContext = JAXBContext.newInstance( ObjectUsers.class );    	
    	Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
    	
    	jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, false );    	
    	OutputStream byteOut = new ByteArrayOutputStream();    	
    	jaxbMarshaller.marshal( objectUsers, byteOut );
    	String bytesOutStr = byteOut.toString();
    	
    	System.out.println(bytesOutStr);
    	
    	Assert.assertEquals( "xml is not matching", expectedOutput, bytesOutStr);

    }
	
	
	
}
