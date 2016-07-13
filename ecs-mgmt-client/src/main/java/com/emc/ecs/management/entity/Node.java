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



