package com.bbb.internationalshipping.vo;

import java.util.List;
import java.util.Map;


/**
 * This class gives you the information
 * about International Order Submit Variables.
 * @version 1.0
 */
public class BBBInternationalOrderSubmitVO {

	/**
	 * This variable is used to point to omnitureProductString.
	 */
	private String omnitureProductString;
	private List<String> rkgItemURLList;
	private Map<String, Object> certonaMap;
	private Map<String, String> cjParamMap;

	/**
	 * @return the omnitureProductString
	 */
	public String getOmnitureProductString() {
		return omnitureProductString;
	}

	/**
	 * @param omnitureProductString the omnitureProductString to set
	 */
	public void setOmnitureProductString(String omnitureProductString) {
		this.omnitureProductString = omnitureProductString;
	}
	public List<String> getRkgItemURLList() {
		return rkgItemURLList;
	}
	public void setRkgItemURLList(List<String> rkgItemURLList) {
		this.rkgItemURLList = rkgItemURLList;
	}
	public Map<String, Object> getCertonaMap() {
		return certonaMap;
	}
	public void setCertonaMap(Map<String, Object> certonaMap) {
		this.certonaMap = certonaMap;
	}
	public Map<String, String> getCjParamMap() {
		return cjParamMap;
	}
	public void setCjParamMap(Map<String, String> cjParamMap) {
		this.cjParamMap = cjParamMap;
	}
			
}
