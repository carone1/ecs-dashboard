/*

The MIT License (MIT)

Copyright (c) 2016 EMC Corporation

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/


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

/**
 * Class responsible to implement data store operations defined in BillingDOA
 * @author carone1
 *
 */
public class FileBillingDAO implements BillingDAO {
	
	private String destinationPath;	
	
	public FileBillingDAO(String outputPath) {
		this.destinationPath = outputPath;
	}
	
	/**
	 * {@inheritDoc}
	 */
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
				// could write the formatted output to a file too
			}
		} catch (JAXBException e) {
			throw new RuntimeException(e.getLocalizedMessage());
		}    										

	}
			
	/**
	 * {@inheritDoc}
	 */
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
	    		// could write the formatted output to a file too
			}
		} catch (JAXBException e) {
			throw new RuntimeException(e.getLocalizedMessage());
		}    										

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long purgeOldData(ManagementDataType type, Date collectionTime) {
		// 
		return 0L;
	}

}
