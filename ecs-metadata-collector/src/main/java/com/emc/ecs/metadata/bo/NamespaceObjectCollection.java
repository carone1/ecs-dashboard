package com.emc.ecs.metadata.bo;

import java.util.List;

import com.emc.ecs.management.entity.ObjectUserDetails;
import com.emc.object.s3.jersey.S3JerseyClient;

public class NamespaceObjectCollection {

	private ObjectUserDetails objectUserDetails;
	private List<String>      ecsObjectHosts;
	private S3JerseyClient    s3JerseyClient;
	
	public NamespaceObjectCollection(ObjectUserDetails objectUserDetails, List<String> ecsObjectHosts) {
		this.objectUserDetails = objectUserDetails;
		this.ecsObjectHosts = ecsObjectHosts;
		
		
		
	}
	
}
