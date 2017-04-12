package com.bbb.account.webservices.vo;

import java.io.Serializable;
import java.util.Map;


public class LinkCoRegistrantResponseVO implements Serializable{

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	private String coRegProfileId;
	private Map<String, String> errorMap;
	private boolean error;
	
	
	/**
	 * @return the coRegProfileId
	 */
	public String getCoRegProfileId() {
		return coRegProfileId;
	}
	/**
	 * @param coRegProfileId the coRegProfileId to set
	 */
	public void setCoRegProfileId(String coRegProfileId) {
		this.coRegProfileId = coRegProfileId;
	}
	
	@Override
	public String toString() {
		return "LinkCoRegistrantResponseVO [coRegProfileId=" + coRegProfileId
				+ ", isError()=" + isError() + ", getErrorCodes()="
				+ "]";
	}
	/**
	 * @return the errorMap
	 */
	public Map<String, String> getErrorMap() {
		return errorMap;
	}
	/**
	 * @param errorMap the errorMap to set
	 */
	public void setErrorMap(Map<String, String> errorMap) {
		this.errorMap = errorMap;
	}
	/**
	 * @return the error
	 */
	public boolean isError() {
		return error;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(boolean error) {
		this.error = error;
	}

}
