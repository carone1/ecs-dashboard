package com.emc.ecs.management.entity;

public class ObjectUserDetails {

	private ObjectUser objectUser;
	private ObjectUserSecretKeys secretKeys;
	
	//============================
	// Constructors
	//============================
	public ObjectUserDetails() {
		
	}
	
	public ObjectUserDetails(ObjectUser objectUser, ObjectUserSecretKeys secretKeys) {
		this.objectUser = objectUser;
		this.secretKeys = secretKeys;
	}
	
	//=============================
	// Public methods
	//=============================
	public ObjectUser getObjectUser() {
		return objectUser;
	}
	
	public void setObjectUser(ObjectUser objectUser) {
		this.objectUser = objectUser;
	}
	
	public ObjectUserSecretKeys getSecretKeys() {
		return secretKeys;
	}
	
	public void setSecretKeys(ObjectUserSecretKeys secretKeys) {
		this.secretKeys = secretKeys;
	}
	
}
