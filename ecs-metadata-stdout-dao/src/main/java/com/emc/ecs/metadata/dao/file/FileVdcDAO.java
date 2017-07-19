/**
 * 
 */
package com.emc.ecs.metadata.dao.file;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.emc.ecs.management.entity.BucketOwner;
import com.emc.ecs.management.entity.VdcDetails;
import com.emc.ecs.metadata.dao.VdcDAO;

/**
 * @author nlengc
 *
 */
public class FileVdcDAO implements VdcDAO {

	private String destinationPath;

	public FileVdcDAO(String outputPath) {
		this.destinationPath = outputPath;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initIndexes(Date collectionTime) {
		// init indexes - not applicable here
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert(VdcDetails vdcDetails, Date collectionTime) {

		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(VdcDetails.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			OutputStream byteOut = new ByteArrayOutputStream();
			jaxbMarshaller.marshal(vdcDetails, byteOut);
			String bytesOutStr = byteOut.toString();
			System.out.println(bytesOutStr);

			if (this.destinationPath != null) {
				// could write the formatted output to a file too
			}
		} catch (JAXBException e) {
			throw new RuntimeException(e.getLocalizedMessage());
		}

	}

	@Override
	public void insert(List<BucketOwner> bucketOwners, Date collectionTime) {

		JAXBContext jaxbContext;
		for (BucketOwner bucketOwner : bucketOwners) {
			try {
				jaxbContext = JAXBContext.newInstance(BucketOwner.class);
				Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				OutputStream byteOut = new ByteArrayOutputStream();
				jaxbMarshaller.marshal(bucketOwner, byteOut);
				String bytesOutStr = byteOut.toString();
				System.out.println(bytesOutStr);

				if (this.destinationPath != null) {
					// could write the formatted output to a file too
				}
			} catch (JAXBException e) {
				throw new RuntimeException(e.getLocalizedMessage());
			}
		}
		
	}

	@Override
	public Long purgeOldData(VdcDataType type, Date thresholdDate) {
		// TODO Auto-generated method stub
		return null;
	}

}
