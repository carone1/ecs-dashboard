package com.emc.ecs.management.entity;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "metadata")
@XmlType(propOrder = {"type", "name", "dataType"})
public class Metadata {
  
	public final static String TYPE      = "type";
	public final static String NAME      = "name";
	public final static String DATA_TYPE = "datatype";

	private String type;
    private String name;
    private String dataType;
    

	@XmlElement(name = TYPE)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElement(name = NAME)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @XmlElement(name = DATA_TYPE)
    public String getDataType() {
  		return dataType;
  	}

  	public void setDataType(String dataType) {
  		this.dataType = dataType;
  	} 
   
}
