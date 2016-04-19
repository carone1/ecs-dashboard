package com.emc.ecs.management.entity;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "metadata")
@XmlType(propOrder = {"type", "name", "dataType"})
public class Metadata {
  

	private String type;
    private String name;
    private String dataType;


	@XmlElement(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @XmlElement(name = "datatype")
    public String getDataType() {
  		return dataType;
  	}

  	public void setDataType(String dataType) {
  		this.dataType = dataType;
  	} 
   
}
