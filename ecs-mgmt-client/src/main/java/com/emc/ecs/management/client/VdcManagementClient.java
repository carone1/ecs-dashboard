/**
 * 
 */
package com.emc.ecs.management.client;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author nlengc
 *
 */
public class VdcManagementClient extends ManagementClient {

//	/**
//	 * @param mgmtConfig
//	 */
//	public VdcManagementClient(ManagementClientConfig mgmtConfig) {
//		super(mgmtConfig);
//	}
	
	
	//================================
	// Constructor
	//================================
	public VdcManagementClient(ManagementClientConfig mgmtConfig){
		super(mgmtConfig);
		this.mgmtConfig = mgmtConfig;
		try {
			// using a bogus host as the smart client will replace with a verified healthy host
			// from the configured list
			this.uri = new URI("http://" + "somehost.com" + ":" + ((VdcManagementClientConfig)this.mgmtConfig).getAlternativePort());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e.getLocalizedMessage());
		}		
		
		mgmtClient = createMgmtClient( this.mgmtConfig.getHostList()  );
	}

}
