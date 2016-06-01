package com.emc.ecs.metadata.dao.file;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;


import com.emc.ecs.management.entity.NamespaceBillingInfo;
import com.emc.ecs.management.entity.ObjectBuckets;
import com.emc.ecs.metadata.dao.BillingDAO;

public class FileBillingDAO implements BillingDAO {
	
	private String destinationPath;	
	
	public FileBillingDAO(String outputPath) {
		this.destinationPath = outputPath;
	}
	
	
	@Override
	public void insert(NamespaceBillingInfo billingData, Date collectionTime) {
		
		
	 	JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance( NamespaceBillingInfo.class );
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
	public void insert(ObjectBuckets bucketResponse, Date collectionTime) {

	 	JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance( ObjectBuckets.class );
		  	Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	    	
	    	jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );    	
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


	@Override
	public void purgeOldData(ManagementDataType type, Date collectionTime) {
		// TODO Auto-generated method stub
		
	}

}
