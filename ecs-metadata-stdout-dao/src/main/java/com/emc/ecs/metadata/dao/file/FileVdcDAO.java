/**
 * 
 */
package com.emc.ecs.metadata.dao.file;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.emc.ecs.management.entity.NamespaceDetail;
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
			jaxbContext = JAXBContext.newInstance(NamespaceDetail.class);
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

}
