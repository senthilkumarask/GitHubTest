package com.bbb.selfservice.common;

import java.io.Serializable;
//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Jaswinder Sidhu
//
//Created on: 03-November-2011
//--------------------------------------------------------------------------------
import java.util.List;

import com.bbb.commerce.catalog.vo.StoreSpecialityVO;

public class StoreDetails  implements Serializable,Comparable<StoreDetails>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mStoreId;
	private String mStoreName;
	private String mStoreDescription;
	private String mAddress;
	private String mCity;
	private String mState;
	private String mCountry;
	private String mPostalCode;
	private String mSatStoreTimings;
	private String mSunStoreTimings;
	private String mWeekdaysStoreTimings;
	private String mOtherTimings1;
	private String mOtherTimings2;
	private String mStorePhone;
	private String mImageCode;
	private String mDistance;
	private String mDistanceUnit;	
	private String mRecordNumber;
	private boolean babyCanadaFlag;
	private String mLng;
	private String mLat;
	private String mSpecialtyShopsCd;
	private List<StoreSpecialityVO> mStoreSpecialityVO;
	private boolean appointmentAvailable;
	private boolean appointmentEligible;
	private int preSelectedServiceRef;
	/**
	 * @return the preSelectedServiceref
	 */
	public int getPreSelectedServiceRef() {
		return preSelectedServiceRef;
	}

	/**
	 * @param preSelectedServiceref the preSelectedServiceref to set
	 */
	public void setPreSelectedServiceRef(int preSelectedServiceRef) {
		this.preSelectedServiceRef = preSelectedServiceRef;
	}

	/**
	 * @return the appointmentAvailable
	 */
	public boolean isAppointmentAvailable() {
		return appointmentAvailable;
	}

	/**
	 * @param appointmentAvailable the appointmentAvailable to set
	 */
	public void setAppointmentAvailable(boolean appointmentAvailable) {
		this.appointmentAvailable = appointmentAvailable;
	}

	/**
	 * @return the appointmentEligible
	 */
	public boolean isAppointmentEligible() {
		return appointmentEligible;
	}

	/**
	 * @param appointmentEligible the appointmentEligible to set
	 */
	public void setAppointmentEligible(boolean appointmentEligible) {
		this.appointmentEligible = appointmentEligible;
	}
	
	/**
	/**
	 * @return the storeSpecialityVO
	 */
	public List<StoreSpecialityVO> getStoreSpecialityVO() {
		return mStoreSpecialityVO;
	}

	/**
	 * @param storeSpecialityVO the storeSpecialityVO to set
	 */
	public void setStoreSpecialityVO(List<StoreSpecialityVO> storeSpecialityVO) {
		this.mStoreSpecialityVO = storeSpecialityVO;
	}



	private String mStoreType;
	private boolean mContactFlag;

	/**
	 * @param pStoreId
	 * @param pStoreName
	 * @param pStoreDescription
	 * @param pAddress	 
	 * @param pCity
	 * @param pState
	 * @param pCountry
	 * @param pPostalCode
	 * @param pSatStoreTimings
	 * @param pSunStoreTimings
	 * @param pWeekdaysStoreTimings
	 * @param pOtherTimings1
	 * @param pOtherTimings2
	 * @param pStorePhone
	 * @param pImageCode
	 * @param pDistance
	 * @param pDistanceUnit	
	 * @param pRecordNumber
	 * @param pLng
	 * @param pLat
	 * @param pSpecialtyShopsCd
	 * @param pStoreType 
	 */
	public StoreDetails(String pStoreId, String pStoreName, // NOPMD by jsidhu on 11/6/11 7:06 PM
			String pStoreDescription, String pAddress, 
			String pCity, String pState, String pCountry, String pPostalCode,
			String pSatStoreTimings, String pSunStoreTimings,
			String pWeekdaysStoreTimings, String pOtherTimings1,
			String pOtherTimings2, String pStorePhone,
			String pImageCode, String pDistance, String pDistanceUnit,
			String pRecordNumber, String pLng, String pLat,
			String pSpecialtyShopsCd, String pStoreType) {
		super();
		mStoreId = pStoreId;
		mStoreName = pStoreName;
		mStoreDescription = pStoreDescription;
		mAddress = pAddress;
		mCity = pCity;
		mState = pState;
		mCountry = pCountry;
		mPostalCode = pPostalCode;
		mSatStoreTimings = pSatStoreTimings;
		mSunStoreTimings = pSunStoreTimings;
		mWeekdaysStoreTimings = pWeekdaysStoreTimings;
		mOtherTimings1 = pOtherTimings1;
		mOtherTimings2 = pOtherTimings2;
		mStorePhone = pStorePhone;
		mImageCode = pImageCode;
		mDistance = pDistance;
		mDistanceUnit = pDistanceUnit;		
		mRecordNumber = pRecordNumber;
		mLng = pLng;
		mLat = pLat;
		mSpecialtyShopsCd = pSpecialtyShopsCd;
		mStoreType = pStoreType;
	}
	
	public StoreDetails(String pStoreId, String pStoreName, // NOPMD by jsidhu on 11/6/11 7:06 PM
			String pStoreDescription, String pAddress, 
			String pCity, String pState, String pCountry, String pPostalCode,
			String pSatStoreTimings, String pSunStoreTimings,
			String pWeekdaysStoreTimings, String pOtherTimings1,
			String pOtherTimings2, String pStorePhone,
			String pImageCode, String pDistance, String pDistanceUnit,
			String pRecordNumber, String pLng, String pLat,
			String pSpecialtyShopsCd, String pStoreType, boolean contactFlag) {
		super();
		mStoreId = pStoreId;
		mStoreName = pStoreName;
		mStoreDescription = pStoreDescription;
		mAddress = pAddress;
		mCity = pCity;
		mState = pState;
		mCountry = pCountry;
		mPostalCode = pPostalCode;
		mSatStoreTimings = pSatStoreTimings;
		mSunStoreTimings = pSunStoreTimings;
		mWeekdaysStoreTimings = pWeekdaysStoreTimings;
		mOtherTimings1 = pOtherTimings1;
		mOtherTimings2 = pOtherTimings2;
		mStorePhone = pStorePhone;
		mImageCode = pImageCode;
		mDistance = pDistance;
		mDistanceUnit = pDistanceUnit;		
		mRecordNumber = pRecordNumber;
		mLng = pLng;
		mLat = pLat;
		mSpecialtyShopsCd = pSpecialtyShopsCd;
		mStoreType = pStoreType;		
		mContactFlag = contactFlag;
	}

	
	
	public boolean getContactFlag() {
		return mContactFlag;
	}



	public void setContactFlag(boolean pContactFlag) {
		mContactFlag = pContactFlag;
	}



	/**
	 * @return the storeName
	 */
	public String getStoreName() {
		return mStoreName;
	}

	/**
	 * @param pStoreName
	 *            the storeName to set
	 */
	public void setStoreName(String pStoreName) {
		mStoreName = pStoreName;
	}

	/**
	 * @return the storeDescription
	 */
	public String getStoreDescription() {
		return mStoreDescription;
	}

	/**
	 * @param pStoreDescription
	 *            the storeDescription to set
	 */
	public void setStoreDescription(String pStoreDescription) {
		mStoreDescription = pStoreDescription;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return mAddress;
	}

	/**
	 * @param pAddress
	 *            the address to set
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
	 * @param pCity
	 *            the city to set
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
	 * @param pState
	 *            the state to set
	 */
	public void setState(String pState) {
		mState = pState;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return mCountry;
	}

	/**
	 * @param pCountry
	 *            the country to set
	 */
	public void setCountry(String pCountry) {
		mCountry = pCountry;
	}

	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return mPostalCode;
	}

	/**
	 * @param pPostalCode
	 *            the postalCode to set
	 */
	public void setPostalCode(String pPostalCode) {
		mPostalCode = pPostalCode;
	}

	/**
	 * @return the satStoreTimings
	 */
	public String getSatStoreTimings() {
		return mSatStoreTimings;
	}

	/**
	 * @param pSatStoreTimings
	 *            the satStoreTimings to set
	 */
	public void setSatStoreTimings(String pSatStoreTimings) {
		mSatStoreTimings = pSatStoreTimings;
	}

	/**
	 * @return the sunStoreTimings
	 */
	public String getSunStoreTimings() {
		return mSunStoreTimings;
	}

	/**
	 * @param pSunStoreTimings
	 *            the sunStoreTimings to set
	 */
	public void setSunStoreTimings(String pSunStoreTimings) {
		mSunStoreTimings = pSunStoreTimings;
	}

	/**
	 * @return the weekdaysStoreTimings
	 */
	public String getWeekdaysStoreTimings() {
		return mWeekdaysStoreTimings;
	}

	/**
	 * @param pWeekdaysStoreTimings
	 *            the weekdaysStoreTimings to set
	 */
	public void setWeekdaysStoreTimings(String pWeekdaysStoreTimings) {
		mWeekdaysStoreTimings = pWeekdaysStoreTimings;
	}

	/**
	 * @return the storeId
	 */
	public String getStoreId() {
		return mStoreId;
	}

	/**
	 * @param pStoreId
	 *            the storeId to set
	 */
	public void setStoreId(String pStoreId) {
		mStoreId = pStoreId;
	}

	/**
	 * @return the storePhone
	 */
	public String getStorePhone() {
		return mStorePhone;
	}

	/**
	 * @param pStorePhone
	 *            the storePhone to set
	 */
	public void setStorePhone(String pStorePhone) {
		mStorePhone = pStorePhone;
	}

	/**
	 * @return the imageCode
	 */
	public String getImageCode() {
		return mImageCode;
	}

	/**
	 * @param pImageCode
	 *            the imageCode to set
	 */
	public void setImageCode(String pImageCode) {
		mImageCode = pImageCode;
	}

	/**
	 * @return the distance
	 */
	public String getDistance() {
		return mDistance;
	}

	/**
	 * @param pDistance
	 *            the distance to set
	 */
	public void setDistance(String pDistance) {
		mDistance = pDistance;
	}

	/**
	 * @return the distanceUnit
	 */
	public String getDistanceUnit() {
		return mDistanceUnit;
	}

	/**
	 * @param pDistanceUnit
	 *            the distanceUnit to set
	 */
	public void setDistanceUnit(String pDistanceUnit) {
		mDistanceUnit = pDistanceUnit;
	}


	/**
	 * @return the recordNumber
	 */
	public String getRecordNumber() {
		return mRecordNumber;
	}

	/**
	 * @param pRecordNumber
	 *            the recordNumber to set
	 */
	public void setRecordNumber(String pRecordNumber) {
		mRecordNumber = pRecordNumber;
	}

	/**
	 * @return the lng
	 */
	public String getLng() {
		return mLng;
	}

	/**
	 * @param pLng
	 *            the lng to set
	 */
	public void setLng(String pLng) {
		mLng = pLng;
	}

	/**
	 * @return the lat
	 */
	public String getLat() {
		return mLat;
	}

	/**
	 * @param pLat
	 *            the lat to set
	 */
	public void setLat(String pLat) {
		mLat = pLat;
	}

	/**
	 * @return the specialtyShopsCd
	 */
	public String getSpecialtyShopsCd() {
		return mSpecialtyShopsCd;
	}

	/**
	 * @param pSpecialtyShopsCd
	 *            the specialtyShopsCd to set
	 */
	public void setSpecialtyShopsCd(String pSpecialtyShopsCd) {
		mSpecialtyShopsCd = pSpecialtyShopsCd;
	}

	/**
	 * @return the storeType
	 */
	public String getStoreType() {
		return mStoreType;
	}

	/**
	 * @param pStoreType
	 *            the storeType to set
	 */
	public void setStoreType(String pStoreType) {
		mStoreType = pStoreType;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder strBuild = new StringBuilder();
		strBuild.append(getStoreId()).append("-").append(getStoreName()).append("-").append(getAddress()).append("-").append(getCity()).append("-").append(getState()).append("-").append(getPostalCode());
		return strBuild.toString();
	}

	public String getOtherTimings1() {
		return mOtherTimings1;
	}

	public void setOtherTimings1(String pOtherTimings1) {
		this.mOtherTimings1 = pOtherTimings1;
	}

	public String getOtherTimings2() {
		return mOtherTimings2;
	}

	public void setOtherTimings2(String pOtherTimings2) {
		this.mOtherTimings2 = pOtherTimings2;
	}

	public boolean isBabyCanadaFlag() {
		return babyCanadaFlag;
	}

	public void setBabyCanadaFlag(boolean babyCanadaFlag) {
		this.babyCanadaFlag = babyCanadaFlag;
	}
	
	@Override
	public int compareTo(StoreDetails storeDetail) {
		//ascending order
		return this.mDistance.compareTo(storeDetail.getDistance());
		
		//descending order
		//return sortableDistance - paramDistance;
		
	}
}
