package com.emc.ecs.management.entity;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



@XmlRootElement(name = "nodes")
@XmlType(propOrder = {"nodes" })

public class Nodes {
	
	private List<Node> nodes;
	
	@XmlElement(name = "node")
	public List<Node> getNodes() {
		return nodes;
	}
	public void setNode(List<Node> nodes) {
		this.nodes = nodes;
	}

}



