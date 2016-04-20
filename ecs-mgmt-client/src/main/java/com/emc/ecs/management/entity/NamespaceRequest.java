package com.emc.ecs.management.entity;



public class NamespaceRequest {
	private String nextMarker;	
    private String name;
               
    public String getNextMarker() {
        return nextMarker;
    }

    public void setNextMarker(String nextMarker) {
        this.nextMarker = nextMarker;
    } 
        
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
   
}
