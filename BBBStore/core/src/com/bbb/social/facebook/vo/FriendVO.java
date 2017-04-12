package com.bbb.social.facebook.vo;

import java.io.Serializable;

/**
 * 
 * @author manohar
 * @version 1.0
 */
public class FriendVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private String mId;
	
	/**
	 * 
	 */
	private String mUserName;
	
	/**
	 * 
	 */
	private String mName;

	/**
	 * @return the id
	 */
	public String getId() {
		return mId;
	}

	/**
	 * @param pId the id to set
	 */
	public void setId(String pId) {
		mId = pId;
	}

	/**
	 * @return the userName
	 */
	public final String getUserName() {
		return mUserName;
	}

	/**
	 * @param pUserName the userName to set
	 */
	public final void setUserName(String pUserName) {
		mUserName = pUserName;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return mName;
	}

	/**
	 * @param pName the name to set
	 */
	public void setName(String pName) {
		mName = pName;
	}
}
