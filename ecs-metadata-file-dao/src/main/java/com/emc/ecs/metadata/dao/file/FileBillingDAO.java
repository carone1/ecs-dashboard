/*
 * Copyright (c) 2016, EMC Corporation.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *     + Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     + Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     + The name of EMC Corporation may not be used to endorse or promote
 *       products derived from this software without specific prior written
 *       permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
