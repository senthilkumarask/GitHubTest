package com.bbb.rest.generic;

import com.bbb.common.BBBGenericService;

public class BBBGenericSessionComponent extends BBBGenericService {
	
	private String deviceLocationLongitude;
	private String deviceLocationLatitude;
	
	public String getDeviceLocationLongitude() {
		return deviceLocationLongitude;
	}
	public void setDeviceLocationLongitude(String deviceLocationLongitude) {
		this.deviceLocationLongitude = deviceLocationLongitude;
	}
	public String getDeviceLocationLatitude() {
		return deviceLocationLatitude;
	}
	public void setDeviceLocationLatitude(String deviceLocationLatitude) {
		this.deviceLocationLatitude = deviceLocationLatitude;
	}
	
		
}
