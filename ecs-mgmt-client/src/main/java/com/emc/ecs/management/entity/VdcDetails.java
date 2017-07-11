/**
 * 
 */
package com.emc.ecs.management.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import Vdc.VdcDetail;

/**
 * @author nlengc
 *
 */
@XmlRootElement(name = "vdc_list")
@XmlType(propOrder = {"vdcDetails"})
public class VdcDetails {
	
	public final static String VDC  = "vdc";
	
	private List<VdcDetail> vdcDetails;

	@XmlElement(name = VDC)
	public List<VdcDetail> getVdcDetails() {
		return vdcDetails;
	}

	public void setVdcDetails(List<VdcDetail> vdcDetails) {
		this.vdcDetails = vdcDetails;
	}

}
