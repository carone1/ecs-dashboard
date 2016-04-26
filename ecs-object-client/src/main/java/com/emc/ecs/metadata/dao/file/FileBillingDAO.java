package com.emc.ecs.metadata.dao.file;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;


import com.emc.ecs.management.entity.NamespaceBillingInfoResponse;
import com.emc.ecs.management.entity.ObjectBucketsResponse;
import com.emc.ecs.metadata.dao.BillingDAO;

public class FileBillingDAO implements BillingDAO {
	
	private String destinationPath;	
	
	public FileBillingDAO(String outputPath) {
		this.destinationPath = outputPath;
	}
	
	
	@Override
	public void insert(NamespaceBillingInfoResponse billingData) {
		
		
	 	JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance( NamespaceBillingInfoResponse.class );
		  	Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	    	
	    	jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );    	
	    	OutputStream byteOut = new ByteArrayOutputStream();    	
	    	jaxbMarshaller.marshal( billingData, byteOut );
	       	String bytesOutStr = byteOut.toString();	    	
	    	System.out.println(bytesOutStr);
	    	
	    	if (this.destinationPath != null) {
				
			}
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    										

	}
			

	@Override
	public void insert(ObjectBucketsResponse bucketResponse) {

	 	JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance( ObjectBucketsResponse.class );
		  	Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	    	
	    	jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, false );    	
	    	OutputStream byteOut = new ByteArrayOutputStream();    	
	    	jaxbMarshaller.marshal( bucketResponse, byteOut );
	       	String bytesOutStr = byteOut.toString();	    	
	    	System.out.println(bytesOutStr);
	    	
	    	if (this.destinationPath != null) {
				
			}
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    										

	}

}
