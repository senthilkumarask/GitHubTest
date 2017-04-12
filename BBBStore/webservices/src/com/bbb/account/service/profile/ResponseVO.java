package com.bbb.account.service.profile;

import java.io.Serializable;

public class ResponseVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ProfileBasicVO mProfileBasicVO;
	private boolean mAuthorzied;
	private String mMessage;
	private int mResponseType;
	/**
	 * @return the profileBasicVO
	 */
	public ProfileBasicVO getProfileBasicVO() {
		return mProfileBasicVO;
	}
	/**
	 * @param pProfileBasicVO the profileBasicVO to set
	 */
	public void setProfileBasicVO(ProfileBasicVO pProfileBasicVO) {
		mProfileBasicVO = pProfileBasicVO;
	}
	/**
	 * @return the authorzied
	 */
	public boolean isAuthorzied() {
		return mAuthorzied;
	}
	/**
	 * @param pAuthorzied the authorzied to set
	 */
	public void setAuthorzied(boolean pAuthorzied) {
		mAuthorzied = pAuthorzied;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return mMessage;
	}
	/**
	 * @param pMessage the message to set
	 */
	public void setMessage(String pMessage) {
		mMessage = pMessage;
	}
	/**
	 * @return the responseType
	 */
	public int getResponseType() {
		return mResponseType;
	}
	/**
	 * @param pResponseType the responseType to set
	 */
	public void setResponseType(int pResponseType) {
		mResponseType = pResponseType;
	}
	
}
