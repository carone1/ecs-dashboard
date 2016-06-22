package com.emc.ecs.management.entity;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name = "node")
@XmlType(propOrder = {"nodeName", "ip", "nodeid", "rackId", "version"})
public class Node {
  

	private String nodeName;
    private String ip;
    private String nodeid;
	private String rackId;
    private String version;

    @XmlElement(name = "nodename")
    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    @XmlElement(name = "ip")
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
       
    @XmlElement(name = "nodeid")
    public String getNodeid() {
  		return nodeid;
  	}

  	public void setNodeid(String nodeid) {
  		this.nodeid = nodeid;
  	}

    @XmlElement(name = "rackId")
  	public String getRackId() {
  		return rackId;
  	}

  	public void setRackId(String rackId) {
  		this.rackId = rackId;
  	}

    @XmlElement(name = "version")
  	public String getVersion() {
  		return version;
  	}

  	public void setVersion(String version) {
  		this.version = version;
  	}

}



