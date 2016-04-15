package com.emc.ecs.management.entity;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "namespace")
@XmlType(propOrder = {"key", "value"})
public class Tag {
  

	private String key;
    private String value;

    @XmlElement(name = "Key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @XmlElement(name = "Value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
       
    
    @Override
  	public int hashCode() {
  		final int prime = 31;
  		int result = 1;
  		result = prime * result + ((key == null) ? 0 : key.hashCode());
  		return result;
  	}

  	@Override
  	public boolean equals(Object obj) {
  		if (this == obj)
  			return true;
  		if (obj == null)
  			return false;
  		if (getClass() != obj.getClass())
  			return false;
  		Tag other = (Tag) obj;
  		if (key == null) {
  			if (other.key != null)
  				return false;
  		} else if (!key.equals(other.key))
  			return false;
  		return true;
  	}
}
