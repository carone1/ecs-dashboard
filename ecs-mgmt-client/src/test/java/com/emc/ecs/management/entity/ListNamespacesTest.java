package com.emc.ecs.management.entity;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;
import org.junit.Assert;



public class ListNamespacesTest {

    
    
    @Test
    public void testListNamespacesResultXml() throws Exception {
    	    	
       	String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
       							"<namespaces>" +
       							"<Filter>filter</Filter>" +
       							"<MaxNamespaces>100</MaxNamespaces>" + 
       							"<namespace>" +
       							"<name>name1</name>" +
       								"<id>id1</id>" +
       								"<link>link1</link>" +
       							"</namespace>" +
       							"<namespace>" +
       								"<name>name2</name>" +
       								"<id>id2</id>" +
       								"<link>link2</link>" +
       							"</namespace>" +
       							"<NextMarker>nextMarker</NextMarker>" +
       							"<NextPageLink>nextPageLink</NextPageLink>" +
       							"</namespaces>";

    	
    	ListNamespacesResult namespacesResult = new ListNamespacesResult();
    	namespacesResult.setMaxNamespaces(100);
    	namespacesResult.setNextMarker("nextMarker");
    	namespacesResult.setNextPageLink("nextPageLink");
    	namespacesResult.setFilter("filter");
    	
    	List<Namespace> namespaces = new ArrayList<Namespace>();
    	
    	// namespace1
    	Namespace namespace1 = new Namespace();
    	namespace1.setId("id1");
    	namespace1.setLink("link1");
    	namespace1.setName("name1");
    	namespaces.add(namespace1);
    	
    	// namespace2
    	Namespace namespace2 = new Namespace();
    	namespace2.setId("id2");
    	namespace2.setLink("link2");
    	namespace2.setName("name2");
    	namespaces.add(namespace2);
    	
    	namespacesResult.setNamespaces(namespaces);
    	
    	JAXBContext jaxbContext = JAXBContext.newInstance( ListNamespacesResult.class );    	
    	Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
    	
    	jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, false );    	
    	OutputStream byteOut = new ByteArrayOutputStream();    	
    	jaxbMarshaller.marshal( namespacesResult, byteOut );
    	String bytesOutStr = byteOut.toString();
    	
    	System.out.println(bytesOutStr);
    	
    	Assert.assertEquals( "xml is not matching", expectedOutput, bytesOutStr);

    }
	
	
	
}
