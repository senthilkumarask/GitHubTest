/**
 * 
 */
package com.bbb.commerce.checklist.vo;

import java.io.Serializable;

/**
 * The Class NonRegistryGuideVO which is used to carry basic details of a Non Registry Guide.
 *
 * @author simra2
 */
public class NonRegistryGuideVO implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	private String guideId;
	private String guideTypeCode;
	private String guideDisplayName;
	private boolean guideDisabled;
	private String registryId;
	/**
	 * @return the guideId
	 */
	public String getGuideId() {
		return guideId;
	}
	/**
	 * @param guideId the guideId to set
	 */
	public void setGuideId(String guideId) {
		this.guideId = guideId;
	}
	/**
	 * @return the guideTypeCode
	 */
	public String getGuideTypeCode() {
		return guideTypeCode;
	}
	/**
	 * @param guideTypeCode the guideTypeCode to set
	 */
	public void setGuideTypeCode(String guideTypeCode) {
		this.guideTypeCode = guideTypeCode;
	}
	/**
	 * @return the guideDisplayName
	 */
	public String getGuideDisplayName() {
		return guideDisplayName;
	}
	/**
	 * @param guideDisplayName the guideDisplayName to set
	 */
	public void setGuideDisplayName(String guideDisplayName) {
		this.guideDisplayName = guideDisplayName;
	}
	/**
	 * @return the guideDisabled
	 */
	public boolean isGuideDisabled() {
		return guideDisabled;
	}
	/**
	 * @param guideDisabled the guideDisabled to set
	 */
	public void setGuideDisabled(boolean guideDisabled) {
		this.guideDisabled = guideDisabled;
	}
	/**
	 * @return the registryId
	 */
	public String getRegistryId() {
		return registryId;
	}
	/**
	 * @param registryId the registryId to set
	 */
	public void setRegistryId(String registryId) {
		this.registryId = registryId;
	}
	
	@Override
	public String toString() {
		return "NonRegistryGuideVO [guideId=" + guideId + ", guideTypeCode="
				+ guideTypeCode + ", guideDisplayName=" + guideDisplayName
				+ ", guideDisabled=" + guideDisabled + ", registryId="
				+ registryId + "]";
	}
	
	
	
	
}
