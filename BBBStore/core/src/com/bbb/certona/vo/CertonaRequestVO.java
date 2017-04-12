package com.bbb.certona.vo;

import java.util.Map;

import com.bbb.framework.httpquery.vo.HTTPServiceRequestIF;

public class CertonaRequestVO implements HTTPServiceRequestIF {

	private Map<String,String> paramsValuesMap;
	private String serviceName; 
	private String siteId;
	private String serviceType;
	
	@Override
	public String getServiceName() {
		return this.serviceName;
	}
	
	public void setServiceName(final String serviceName) {
		this.serviceName = serviceName;
	}

	@Override
	public Map<String, String> getParamsValuesMap() {
		return this.paramsValuesMap;
	}

	
	public void setParamsValuesMap(final Map<String, String> paramValues) {
		this.paramsValuesMap = paramValues;
	}

	@Override
	public String getSiteId() {
		return this.siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	@Override
	public String getServiceType() {
		return this.serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	@Override
	public String toString() {
		return "CertonaRequestVO [paramsValuesMap=" + this.paramsValuesMap
				+ ", serviceName=" + this.serviceName + ", siteId=" + this.siteId
				+ ", serviceType=" + this.serviceType + "]";
	}

}
