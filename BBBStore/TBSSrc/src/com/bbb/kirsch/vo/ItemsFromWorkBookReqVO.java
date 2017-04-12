package com.bbb.kirsch.vo;

import java.util.Map;

import com.bbb.framework.httpquery.vo.HTTPServiceRequestIF;

public class ItemsFromWorkBookReqVO implements HTTPServiceRequestIF{
	
	private String mServiceName;
	private Map<String, String> mParamsValuesMap;
	private String mSiteId;
	private String mServiceType;
	
	@Override
	public String getServiceName() {
		return mServiceName;
	}
	public void setServiceName(String pServiceName) {
		mServiceName = pServiceName;
	}
	
	@Override
	public Map<String, String> getParamsValuesMap() {
		return mParamsValuesMap;
	}
	public void setParamsValuesMap(Map<String, String> pParamsValuesMap) {
		mParamsValuesMap = pParamsValuesMap;
	}
	
	@Override
	public String getSiteId() {
		return mSiteId;
	}
	public void setSiteId(String pSiteId) {
		mSiteId = pSiteId;
	}
	
	@Override
	public String getServiceType() {
		return mServiceType;
	}
	public void setServiceType(String pServiceType) {
		mServiceType = pServiceType;
	}


}
