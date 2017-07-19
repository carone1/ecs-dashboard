/**
 * 
 */
package com.emc.ecs.management.client;

import java.util.List;

/**
 * @author nlengc
 *
 */
public class VdcManagementClientConfig extends ManagementClientConfig {
	
	private int alternativePort;

	/**
	 * @param mgmtUsername
	 * @param mgmtSecretKey
	 * @param port
	 * @param hosts
	 */
	public VdcManagementClientConfig(String mgmtUsername, String mgmtSecretKey, int port, int alternativePort, List<String> hosts) {
		super(mgmtUsername, mgmtSecretKey, port, hosts);
		this.setAlternativePort(alternativePort);
	}

	/**
	 * @param mgmtUsername
	 * @param mgmtSecretKey
	 * @param hosts
	 */
	public VdcManagementClientConfig(String mgmtUsername, String mgmtSecretKey, int alternativePort, List<String> hosts) {
		super(mgmtUsername, mgmtSecretKey, hosts);
		this.setAlternativePort(alternativePort);
	}

	public int getAlternativePort() {
		return alternativePort;
	}

	public void setAlternativePort(int alternativePort) {
		this.alternativePort = alternativePort;
	}

}
