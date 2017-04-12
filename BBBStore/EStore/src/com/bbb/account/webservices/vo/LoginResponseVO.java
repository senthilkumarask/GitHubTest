package com.bbb.account.webservices.vo;

import java.io.Serializable;
import java.util.Map;


public class LoginResponseVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String profileId; 	
	private boolean profileAutoExtended;
	private Map<String, String> errorMap;
	private boolean error;

	/**
	 * @return the profileId
	 */
	public String getProfileId() {
		return profileId;
	}
	/**
	 * @param profileId the profileId to set
	 */
	public void setProfileId(final String profileId) {
		this.profileId = profileId;
	}
	/**
	 * @return the profileAutoExtended
	 */
	public boolean isProfileAutoExtended() {
		return profileAutoExtended;
	}
	/**
	 * @param profileAutoExtended the profileAutoExtended to set
	 */
	public void setProfileAutoExtended(final boolean profileAutoExtended) {
		this.profileAutoExtended = profileAutoExtended;
	}
	@Override
	public String toString() {
		return "LoginResponseVO [profileId=" + profileId
				+ ", profileAutoExtended=" + profileAutoExtended
				+"]";
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
