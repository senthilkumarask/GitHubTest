package com.bbb.selfservice.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NICAvailabilityRequestVO {
	private String viewName;
	private Map<String, Map<String,List<String>>> availabilityCriteria;
	

	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	public Map<String, Map<String,List<String>>> getAvailabilityCriteria() {
		return availabilityCriteria;
	}
	public void setAvailabilityCriteria(Map<String, Map<String,List<String>>> availabilityCriteria) {
		this.availabilityCriteria = availabilityCriteria;
	}
}
