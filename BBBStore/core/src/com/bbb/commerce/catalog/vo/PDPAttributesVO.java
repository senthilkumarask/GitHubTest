package com.bbb.commerce.catalog.vo;

import java.io.Serializable;


/**
 * This is a custom class for information regarding Chat and Display Ask and Answer for PDP.
 *
 * @author agu117
 */

public class PDPAttributesVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * =====================================================
	 * * MEMBER VARIABLES
	 * =====================================================
	 */

	private String categoryId;
	private boolean chatEnabled;
	private boolean askAndAnswerEnabled;

	private boolean siteChatOnOffFlag;
	private boolean pdpChatOnOffFlag;
	private boolean pdpChatOverrideFlag;
	private boolean pdpDaasOnOffFlag;
	private boolean pdpDaasOverrideFlag;
	
	
	//@author psin52 Added for story 117-A5 | Grid/List view configurable -	START
	private String defaultDisplayType;

	/**
	 * @return defaultDisplayType
	 */
	public String getDefaultDisplayType() {
		return defaultDisplayType;
	}
	/**
	 * @param defaultDisplayType
	 */
	public void setDefaultDisplayType(String defaultDisplayType) {
		this.defaultDisplayType = defaultDisplayType;
	}
	
	//@author psin52 Added for story 117-A5 | Grid/List view configurable -	END
	/**
	 * @return the categoryId
	 */
	public String getCategoryId() {
		return categoryId;
	}
	/**
	 * @param pCategoryId the categoryId to set
	 */
	public void setCategoryId(String pCategoryId) {
		categoryId = pCategoryId;
	}
	/**
	 * @return the isChatEnabled
	 */
	public boolean isChatEnabled() {
		return chatEnabled;
	}
	/**
	 * @param pIsChatEnabled the isChatEnabled to set
	 */
	public void setChatEnabled(boolean pIsChatEnabled) {
		chatEnabled = pIsChatEnabled;
	}
	/**
	 * @return the isAskAndAnswerEnabled
	 */
	public boolean isAskAndAnswerEnabled() {
		return askAndAnswerEnabled;
	}
	/**
	 * @param pIsAskAndAnswerEnabled the isAskAndAnswerEnabled to set
	 */
	public void setAskAndAnswerEnabled(boolean pIsAskAndAnswerEnabled) {
		askAndAnswerEnabled = pIsAskAndAnswerEnabled;
	}

	/**
	 * @return the siteChatOnOffFlag
	 */
	public boolean isSiteChatOnOffFlag() {
		return siteChatOnOffFlag;
	}
	/**
	 * @param pSiteChatOnOffFlag the siteChatOnOffFlag to set
	 */
	public void setSiteChatOnOffFlag(boolean pSiteChatOnOffFlag) {
		siteChatOnOffFlag = pSiteChatOnOffFlag;
	}
	/**
	 * @return the pdpChatOnOffFlag
	 */
	public boolean isPdpChatOnOffFlag() {
		return pdpChatOnOffFlag;
	}
	/**
	 * @param pPdpChatOnOffFlag the pdpChatOnOffFlag to set
	 */
	public void setPdpChatOnOffFlag(boolean pPdpChatOnOffFlag) {
		pdpChatOnOffFlag = pPdpChatOnOffFlag;
	}
	/**
	 * @return the pdpChatOverrideFlag
	 */
	public boolean isPdpChatOverrideFlag() {
		return pdpChatOverrideFlag;
	}
	/**
	 * @param pPdpChatOverrideFlag the pdpChatOverrideFlag to set
	 */
	public void setPdpChatOverrideFlag(boolean pPdpChatOverrideFlag) {
		pdpChatOverrideFlag = pPdpChatOverrideFlag;
	}
	/**
	 * @return the pdpDaaasOnOffFlag
	 */
	public boolean isPdpDaasOnOffFlag() {
		return pdpDaasOnOffFlag;
	}
	/**
	 * @param pdpDaaasOnOffFlag the pdpDaaasOnOffFlag to set
	 */
	public void setPdpDaasOnOffFlag(boolean pdpDaasOnOffFlag) {
		this.pdpDaasOnOffFlag = pdpDaasOnOffFlag;
	}
	/**
	 * @return the pdpDaaasOverrideFlag
	 */
	public boolean isPdpDaasOverrideFlag() {
		return pdpDaasOverrideFlag;
	}
	/**
	 * @param pPdpDaasOverrideFlag the pdpDaasOverrideFlag to set
	 */
	public void setPdpDaasOverrideFlag(boolean pPdpDaasOverrideFlag) {
		pdpDaasOverrideFlag = pPdpDaasOverrideFlag;
	}


}
