package com.bbb.simplifyRegistry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class RegistryInputsByTypeVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String eventType;
	private boolean isPublic;
	private List<RegistryInputVO> registryInputList=new ArrayList<RegistryInputVO>();
	
	
	public boolean isPublic() {
		return isPublic;
	}
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public List<RegistryInputVO> getRegistryInputList() {
		return registryInputList;
	}
	public void setRegistryInputList(List<RegistryInputVO> registryInputList) {
		this.registryInputList = registryInputList;
	}	
}