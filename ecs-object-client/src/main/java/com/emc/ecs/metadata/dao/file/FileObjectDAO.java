package com.emc.ecs.metadata.dao.file;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;


import com.emc.ecs.metadata.dao.ObjectDAO;
import com.emc.object.s3.bean.ListObjectsResult;
import com.emc.object.s3.bean.S3Object;

public class FileObjectDAO implements ObjectDAO {
	

	@Override
	public void insert(ListObjectsResult listObjectsResult) {

		System.out.println("Would be inserting " + listObjectsResult.getObjects().size() + " objects into datastore");
		
//		for( S3Object s3Object: listObjectsResult.getObjects() ) {
//			
//			JAXBContext jaxbContext;
//			try {
//				jaxbContext = JAXBContext.newInstance( S3Object.class );
//			  	Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//		    	
//		    	jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, false );    	
//		    	OutputStream byteOut = new ByteArrayOutputStream();    	
//		    	jaxbMarshaller.marshal( s3Object, byteOut );
//		       	String bytesOutStr = byteOut.toString();	    	
//		    	System.out.println(bytesOutStr);
//		    			    	
//			} catch (JAXBException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} 
//		}
		
	}

}
