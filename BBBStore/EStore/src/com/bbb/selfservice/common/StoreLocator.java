package com.bbb.selfservice.common;

import java.io.Serializable;

public class StoreLocator implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mPostalCode;
	private String mAddress;
	private String mCity;
	private String mState;
	private int mRadius ;
	private int mPagenumber;
	private String mSearchType;
	private String mLongitude;
	private String mLatitude;
	
	/**
	 * @return the longitude
	 */
	public String getLongitude() {
	    return mLongitude;
	}
	/**
	 * @param mLongitude the longitude to set
	 */
	public void setLongitude(String mLongitude) {
	    this.mLongitude = mLongitude;
	}
	/**
	 * @return the latitude
	 */
	public String getLatitude() {
	    return mLatitude;
	}
	/**
	 * @param mLatitude the latitude to set
	 */
	public void setLatitude(String mLatitude) {
	    this.mLatitude = mLatitude;
	}
	
	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return mPostalCode;
	}
	/**
	 * @param pPostalCode the postalCode to set
	 */
	public void setPostalCode(String pPostalCode) {
		mPostalCode = pPostalCode;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return mAddress;
	}
	/**
	 * @param pAddress the address to set
	 */
	public void setAddress(String pAddress) {
		mAddress = pAddress;
	}
	/**
	 * @return the city
	 */
	public String getCity() {
		return mCity;
	}
	/**
	 * @param pCity the city to set
	 */
	public void setCity(String pCity) {
		mCity = pCity;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return mState;
	}
	/**
	 * @param pState the state to set
	 */
	public void setState(String pState) {
		mState = pState;
	}
	/**
	 * @return the radius
	 */
	public int getRadius() {
		return mRadius;
	}
	/**
	 * @param pRadius the radius to set
	 */
	public void setRadius(int pRadius) {
		mRadius = pRadius;
	}
	/**
	 * @return the pagenumber
	 */
	public int getPagenumber() {
		return mPagenumber;
	}
	/**
	 * @param pPagenumber the pagenumber to set
	 */
	public void setPagenumber(int pPagenumber) {
		mPagenumber = pPagenumber;
	}
	/**
	 * @return the searchType
	 */
	public String getSearchType() {
		return mSearchType;
	}
	/**
	 * @param pSearchType the searchType to set
	 */
	public void setSearchType(String pSearchType) {
		mSearchType = pSearchType;
	}
		
	
}
