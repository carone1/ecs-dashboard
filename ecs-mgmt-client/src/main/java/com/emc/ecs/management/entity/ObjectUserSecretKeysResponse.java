package com.emc.ecs.management.entity;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name = "user_secret_keys")
@XmlType(propOrder = {"secretKey1", "keyTimestamp1", "keyExpiryTimestamp1", "secretKey2", "keyTimestamp2", "keyExpiryTimestamp2", "link"})
public class ObjectUserSecretKeysResponse {
	private String secretKey1;
	private String keyTimestamp1;
	private String keyExpiryTimestamp1;
	private String secretKey2;
    private String keyTimestamp2;
    private String keyExpiryTimestamp2;
    private String link;
    

	//private String 
    @XmlElement(name = "secret_key_1")
	public String getSecretKey1() {
		return secretKey1;
	}
	public void setSecretKey1(String secretKey1) {
		this.secretKey1 = secretKey1;
	}
	
	@XmlElement(name = "key_timestamp_1")
	public String getKeyTimestamp1() {
		return keyTimestamp1;
	}
	public void setKeyTimestamp1(String keyTimestamp1) {
		this.keyTimestamp1 = keyTimestamp1;
	}

	@XmlElement(name = "key_expiry_timestamp_1")
	public String getKeyExpiryTimestamp1() {
		return keyExpiryTimestamp1;
	}
	public void setKeyExpiryTimestamp1(String keyExpiryTimestamp1) {
		this.keyExpiryTimestamp1 = keyExpiryTimestamp1;
	}
	
	 @XmlElement(name = "secret_key_2")
	public String getSecretKey2() {
		return secretKey2;
	}
	public void setSecretKey2(String secretKey2) {
		this.secretKey2 = secretKey2;
	}
	
	@XmlElement(name = "key_timestamp_2")
	public String getKeyTimestamp2() {
		return keyTimestamp2;
	}
	public void setKeyTimestamp2(String keyTimestamp2) {
		this.keyTimestamp2 = keyTimestamp2;
	}
	
	 @XmlElement(name = "key_expiry_timestamp_2")
	public String getKeyExpiryTimestamp2() {
		return keyExpiryTimestamp2;
	}
	 
	public void setKeyExpiryTimestamp2(String keyExpiryTimestamp2) {
		this.keyExpiryTimestamp2 = keyExpiryTimestamp2;
	}
    
	@XmlElement(name = "link")
    public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}

   
}
