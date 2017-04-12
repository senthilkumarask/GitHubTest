package com.bbb.search.endeca.vo;

public class SupplementVO {
	
	public enum SupplementType {CONTENT, REDIRECT};
	
	private String templateType;
	
	private String templateId;
	
	private String resourcePath;
	
	private String rulePriority;
	
	private String redirectURL;
	
	private SupplementType type;

	/**
	 * @return the templateType
	 */
	public String getTemplateType() {
		return templateType;
	}

	/**
	 * @param templateType the templateType to set
	 */
	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	/**
	 * @return the templateId
	 */
	public String getTemplateId() {
		return templateId;
	}

	/**
	 * @param templateId the templateId to set
	 */
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	/**
	 * @return the resourcePath
	 */
	public String getResourcePath() {
		return resourcePath;
	}

	/**
	 * @param resourcePath the resourcePath to set
	 */
	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	/**
	 * @return the rulePriority
	 */
	public String getRulePriority() {
		return rulePriority;
	}

	/**
	 * @param rulePriority the rulePriority to set
	 */
	public void setRulePriority(String rulePriority) {
		this.rulePriority = rulePriority;
	}

	/**
	 * @return the redirectURL
	 */
	public String getRedirectURL() {
		return redirectURL;
	}

	/**
	 * @param redirectURL the redirectURL to set
	 */
	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}

	/**
	 * @return the type
	 */
	public SupplementType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(SupplementType type) {
		this.type = type;
	}
	
	

}
