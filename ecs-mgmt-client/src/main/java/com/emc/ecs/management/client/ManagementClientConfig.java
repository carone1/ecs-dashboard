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
