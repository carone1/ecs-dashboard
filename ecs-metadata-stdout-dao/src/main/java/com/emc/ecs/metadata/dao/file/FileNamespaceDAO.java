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
import com.emc.ecs.management.entity.NamespaceQuota;
import com.emc.ecs.metadata.dao.NamespaceDAO;

/**
 * @author nlengc
 *
 */
public class FileNamespaceDAO implements NamespaceDAO {

	private String destinationPath;

	public FileNamespaceDAO(String outputPath) {
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
	public void insert(NamespaceDetail namespaceDetail, Date collectionTime) {

		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(NamespaceDetail.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			OutputStream byteOut = new ByteArrayOutputStream();
			jaxbMarshaller.marshal(namespaceDetail, byteOut);
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
	public void insert(NamespaceQuota namespaceQuota, Date collectionTime) {

		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(NamespaceQuota.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			OutputStream byteOut = new ByteArrayOutputStream();
			jaxbMarshaller.marshal(namespaceQuota, byteOut);
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
	public Long purgeOldData(NamespaceDataType type, Date thresholdDate) {
		// TODO Auto-generated method stub
		return null;
	}

}
