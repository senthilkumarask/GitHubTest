package com.bbb.cms.vo;

import java.io.Serializable;


/**
 * Response VO common to all CMS templates
 * @author ikhan2
 *
 */
public class CLPResponseVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CMSResponseVO cmsResponseVO;
	
	private String alternateURL;
	
	private boolean errorExist;
	
    private String errorCode;
    
    private String errorMessage;

	/**
	 * @return the cmsResponseVO
	 */
	public CMSResponseVO getCmsResponseVO() {
		return cmsResponseVO;
	}

	/**
	 * @param cmsResponseVO the cmsResponseVO to set
	 */
	public void setCmsResponseVO(CMSResponseVO cmsResponseVO) {
		this.cmsResponseVO = cmsResponseVO;
	}

	/**
	 * @return the alternateURL
	 */
	public String getAlternateURL() {
		return alternateURL;
	}

	/**
	 * @param alternateURL the alternateURL to set
	 */
	public void setAlternateURL(String alternateURL) {
		this.alternateURL = alternateURL;
	}

	/**
	 * @return the errorExist
	 */
	public boolean isErrorExist() {
		return errorExist;
	}

	/**
	 * @param errorExist the errorExist to set
	 */
	public void setErrorExist(boolean errorExist) {
		this.errorExist = errorExist;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	

}
