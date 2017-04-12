package com.bbb.commerce.catalog.vo;

import java.io.Serializable;
import java.util.Date;

public class SiteChatAttributesVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mChatURL;
	private boolean mOnOffFlag;
	private boolean mChatFlagPDP;
	private boolean mChatOverrideFlagPDP;
	private boolean mDaasFlagPDP;
	private boolean mDaasOverrideFlagPDP;
	private Date mWeekDayOpenTime;
	private Date mWeekDayCloseTime;
	private Date mWeekEndOpenTime;
	private Date mWeekEndCloseTime;
	/**
	 * @return the mChatURL
	 */
	public String getChatURL() {
		return mChatURL;
	}
	/**
	 * @param pChatURL the mChatURL to set
	 */
	public void setChatURL(String pChatURL) {
		this.mChatURL = pChatURL;
	}
	/**
	 * @return the mOnOffFlag
	 */
	public boolean isOnOffFlag() {
		return mOnOffFlag;
	}
	/**
	 * @param pOnOffFlag the mOnOffFlag to set
	 */
	public void setOnOffFlag(boolean pOnOffFlag) {
		this.mOnOffFlag = pOnOffFlag;
	}

	/**
	 * @return the chatFlagPDP
	 */
	public boolean isChatFlagPDP() {
		return mChatFlagPDP;
	}
	/**
	 * @param pChatFlagPDP the chatFlagPDP to set
	 */
	public void setChatFlagPDP(boolean pChatFlagPDP) {
		mChatFlagPDP = pChatFlagPDP;
	}
	/**
	 * @return the chatOverrideFlagPDP
	 */
	public boolean isChatOverrideFlagPDP() {
		return mChatOverrideFlagPDP;
	}
	/**
	 * @param pChatOverrideFlagPDP the chatOverrideFlagPDP to set
	 */
	public void setChatOverrideFlagPDP(boolean pChatOverrideFlagPDP) {
		mChatOverrideFlagPDP = pChatOverrideFlagPDP;
	}
	/**
	 * @return the daasFlagPDP
	 */
	public boolean isDaasFlagPDP() {
		return mDaasFlagPDP;
	}
	/**
	 * @param pDaasFlagPDP the daasFlagPDP to set
	 */
	public void setDaasFlagPDP(boolean pDaasFlagPDP) {
		mDaasFlagPDP = pDaasFlagPDP;
	}
	/**
	 * @return the daasOverrideFlagPDP
	 */
	public boolean isDaasOverrideFlagPDP() {
		return mDaasOverrideFlagPDP;
	}
	/**
	 * @param pDaasOverrideFlagPDP the daasOverrideFlagPDP to set
	 */
	public void setDaasOverrideFlagPDP(boolean pDaasOverrideFlagPDP) {
		mDaasOverrideFlagPDP = pDaasOverrideFlagPDP;
	}
	/**
	 * @return the mWeekDayOpenTime
	 */
	public final Date getWeekDayOpenTime() {
		return new Date(mWeekDayOpenTime.getTime());
	}
	/**
	 * @param pWeekDayOpenTime the mWeekDayOpenTime to set
	 */
	public void setWeekDayOpenTime(final Date pWeekDayOpenTime) {
		this.mWeekDayOpenTime = new Date(pWeekDayOpenTime.getTime());
	}
	/**
	 * @return the mWeekDayCloseTime
	 */
	public final Date getWeekDayCloseTime() {
		return new Date (mWeekDayCloseTime.getTime());
	}
	/**
	 * @param pWeekDayCloseTime the mWeekDayCloseTime to set
	 */
	public void setWeekDayCloseTime(final Date pWeekDayCloseTime) {
		this.mWeekDayCloseTime = new Date(pWeekDayCloseTime.getTime());
	}
	/**
	 * @return the mWeekEndOpenTime
	 */
	public final Date getWeekEndOpenTime() {
		return new Date(mWeekEndOpenTime.getTime());
	}
	/**
	 * @param pWeekEndOpenTime the mWeekEndOpenTime to set
	 */
	public void setWeekEndOpenTime(final Date pWeekEndOpenTime) {
		this.mWeekEndOpenTime = new Date(pWeekEndOpenTime.getTime());
	}
	/**
	 * @return the mWeekEndCloseTime
	 */
	public final Date getWeekEndCloseTime() {
		return new Date(mWeekEndCloseTime.getTime());
	}
	/**
	 * @param pWeekEndCloseTime the mWeekEndCloseTime to set
	 */
	public void setWeekEndCloseTime(final Date pWeekEndCloseTime) {
		this.mWeekEndCloseTime = new Date(pWeekEndCloseTime.getTime());
	}


}
