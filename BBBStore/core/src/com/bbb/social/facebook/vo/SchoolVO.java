package com.bbb.social.facebook.vo;

import java.io.Serializable;


/**
 * 
 * @author manohar
 * @version 1.0
 */
public class SchoolVO implements Serializable {
	
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
	private String mSchoolID;
	
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
	 * @return the schoolID
	 */
	public final String getSchoolID() {
		return mSchoolID;
	}

	/**
	 * @param pSchoolID the schoolID to set
	 */
	public final void setSchoolID(String pSchoolID) {
		mSchoolID = pSchoolID;
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
