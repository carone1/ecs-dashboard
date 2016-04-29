package com.emc.ecs.metadata.dao.elasticsearch;

import java.util.List;

public class ElasticDAOConfig {
	
	//==========================
	// Private members
	//==========================
	List<String> hosts;
	Integer      port;
	String       clusterName;
	
	//==========================
	// Public Methods
	//==========================	
	public List<String> getHosts() {
		return hosts;
	}
	public void setHosts(List<String> hosts) {
		this.hosts = hosts;
	}
	
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	
	public String getClusterName() {
		return clusterName;
	}
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
}
