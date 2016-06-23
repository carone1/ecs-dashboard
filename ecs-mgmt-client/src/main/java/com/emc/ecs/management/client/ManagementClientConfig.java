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

package com.emc.ecs.management.client;

import java.util.List;

public class ManagementClientConfig {
	
	public static final int DEFAULT_PORT = 4443; 
	
	//===================================
	// Private members
	//===================================
	private List<String> hostList;
	private int port;
	private String mgmtUsername;
	private String mgmtSecretKey;
	
	//===================================
	// Constructor
	//===================================
	public ManagementClientConfig( String mgmtUsername, 
								   String mgmtSecretKey,
								   int          port, 
								   List<String> hosts      ) {
		
		this.mgmtUsername = mgmtUsername;
		this.mgmtSecretKey = mgmtSecretKey;
		this.port = port;
		this.hostList = hosts;
	}
	
	public ManagementClientConfig( String mgmtUsername, 
								   String mgmtSecretKey,
								   List<String> hosts     ) {
		
		this.mgmtUsername = mgmtUsername;
		this.mgmtSecretKey = mgmtSecretKey;
		this.port = DEFAULT_PORT;
		this.hostList = hosts;
	}

	//===================================
	// Public Methods
	//===================================
	public List<String> getHostList() {
		return hostList;
	}

	public void setHostList(List<String> hostList) {
		this.hostList = hostList;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getMgmtUsername() {
		return mgmtUsername;
	}

	public void setMgmtUsername(String username) {
		this.mgmtUsername = username;
	}

	public String getMgmtSecretKey() {
		return mgmtSecretKey;
	}

	public void setMgmtSecretKey(String secretKey) {
		this.mgmtSecretKey = secretKey;
	}
	

	
}
