/**
 * 
 */
package com.bbb.commerce.inventory.vo;

import java.sql.Timestamp;

/**
 * @author alakra
 *
 */
public class InventoryFeedVO extends InventoryVO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String mID;

	private String mFeedID;
	
	private String mFeedStatus;
	
	private Timestamp mFeedCreationDate;
	
	private Timestamp mFeedLastUpdatedDate;
	
	/**
	 * 
	 */
	public InventoryFeedVO() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the iD
	 */
	public String getID() {
		return mID;
	}

	/**
	 * @param pID the iD to set
	 */
	public void setID(String pID) {
		mID = pID;
	}

	/**
	 * @return the feedID
	 */
	public String getFeedID() {
		return mFeedID;
	}

	/**
	 * @param pFeedID the feedID to set
	 */
	public void setFeedID(String pFeedID) {
		mFeedID = pFeedID;
	}

	/**
	 * @return the feedStatus
	 */
	public String getFeedStatus() {
		return mFeedStatus;
	}

	/**
	 * @param pFeedStatus the feedStatus to set
	 */
	public void setFeedStatus(String pFeedStatus) {
		mFeedStatus = pFeedStatus;
	}

	/**
	 * @return the feedCreationDate
	 */
	public Timestamp getFeedCreationDate() {
		return mFeedCreationDate;
	}

	/**
	 * @param pFeedCreationDate the feedCreationDate to set
	 */
	public void setFeedCreationDate(Timestamp pFeedCreationDate) {
		mFeedCreationDate = pFeedCreationDate;
	}

	/**
	 * @return the feedLastUpdatedDate
	 */
	public Timestamp getFeedLastUpdatedDate() {
		return mFeedLastUpdatedDate;
	}

	/**
	 * @param pFeedLastUpdatedDate the feedLastUpdatedDate to set
	 */
	public void setFeedLastUpdatedDate(Timestamp pFeedLastUpdatedDate) {
		mFeedLastUpdatedDate = pFeedLastUpdatedDate;
	}

}
