package com.bbb.commerce.shipping.vo;

import java.io.Serializable;

public class HolidayMessagingVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mStandardLabel;
	private String mExpeditedLabel;
	private String mName;
	
	 public HolidayMessagingVO() {
		    super();
	 }
	  
	
	  public HolidayMessagingVO(String pStandardLabel, String pExpeditedLabel, String pName) 
	  {
		    super();
		    mStandardLabel = pStandardLabel;
		    mExpeditedLabel = pExpeditedLabel;
		    mName = pName;
	  }

	/**
	 * @return the standardLabel
	 */
	public String getStandardLabel() {
		return mStandardLabel;
	}

	/**
	 * @param pStandardLabel the standardLabel to set
	 */
	public void setStandardLabel(String pStandardLabel) {
		mStandardLabel = pStandardLabel;
	}

	/**
	 * @return the expeditedLabel
	 */
	public String getExpeditedLabel() {
		return mExpeditedLabel;
	}

	/**
	 * @param pExpeditedLabel the expeditedLabel to set
	 */
	public void setExpeditedLabel(String pExpeditedLabel) {
		mExpeditedLabel = pExpeditedLabel;
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
