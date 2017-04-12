package com.bbb.account.vo.order;

import java.io.Serializable;

/**
 * GiftPackaging.java
 *
 * This file is part of OrderDetails webservice response
 * 
 */


public class GiftPackaging  implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean isGiftPackagingAvailable;

    private String giftMessage;

    private String giftPackagingFlag;

    private String giftPackagingOption;

    private String giftPackagingInfoText;

    private String giftPackagingWarning;

	/**
	 * @return the isGiftPackagingAvailable
	 */
	public boolean isGiftPackagingAvailable() {
		return isGiftPackagingAvailable;
	}

	/**
	 * @param isGiftPackagingAvailable the isGiftPackagingAvailable to set
	 */
	public void setGiftPackagingAvailable(boolean isGiftPackagingAvailable) {
		this.isGiftPackagingAvailable = isGiftPackagingAvailable;
	}

	/**
	 * @return the giftMessage
	 */
	public String getGiftMessage() {
		return giftMessage;
	}

	/**
	 * @param giftMessage the giftMessage to set
	 */
	public void setGiftMessage(String giftMessage) {
		this.giftMessage = giftMessage;
	}

	/**
	 * @return the giftPackagingFlag
	 */
	public String getGiftPackagingFlag() {
		return giftPackagingFlag;
	}

	/**
	 * @param giftPackagingFlag the giftPackagingFlag to set
	 */
	public void setGiftPackagingFlag(String giftPackagingFlag) {
		this.giftPackagingFlag = giftPackagingFlag;
	}

	/**
	 * @return the giftPackagingOption
	 */
	public String getGiftPackagingOption() {
		return giftPackagingOption;
	}

	/**
	 * @param giftPackagingOption the giftPackagingOption to set
	 */
	public void setGiftPackagingOption(String giftPackagingOption) {
		this.giftPackagingOption = giftPackagingOption;
	}

	/**
	 * @return the giftPackagingInfoText
	 */
	public String getGiftPackagingInfoText() {
		return giftPackagingInfoText;
	}

	/**
	 * @param giftPackagingInfoText the giftPackagingInfoText to set
	 */
	public void setGiftPackagingInfoText(String giftPackagingInfoText) {
		this.giftPackagingInfoText = giftPackagingInfoText;
	}

	/**
	 * @return the giftPackagingWarning
	 */
	public String getGiftPackagingWarning() {
		return giftPackagingWarning;
	}

	/**
	 * @param giftPackagingWarning the giftPackagingWarning to set
	 */
	public void setGiftPackagingWarning(String giftPackagingWarning) {
		this.giftPackagingWarning = giftPackagingWarning;
	}


}
