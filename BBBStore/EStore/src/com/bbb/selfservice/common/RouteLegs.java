package com.bbb.selfservice.common;

import java.io.Serializable;
import java.util.List;

/**
 * @author akhaju
 * 
 */
public class RouteLegs implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean mHasTollRoad;
	private String mIndex;
	private String mLegDistance;
	private boolean mHasSeasonalClosure;
	private boolean mHasCountryCross;
	private String mFormattedTime;
	private boolean mHasUnpaved;
	private boolean mHasHighway;
	private boolean mHasFerry;
	private List<Maneuvers> mManeuvers;
	/**
	 * @return the hasTollRoad
	 */
	public boolean isHasTollRoad() {
		return mHasTollRoad;
	}
	/**
	 * @param pHasTollRoad the hasTollRoad to set
	 */
	public void setHasTollRoad(boolean pHasTollRoad) {
		mHasTollRoad = pHasTollRoad;
	}
	/**
	 * @return the index
	 */
	public String getIndex() {
		return mIndex;
	}
	/**
	 * @param pIndex the index to set
	 */
	public void setIndex(String pIndex) {
		mIndex = pIndex;
	}
	/**
	 * @return the legDistance
	 */
	public String getLegDistance() {
		return mLegDistance;
	}
	/**
	 * @param pLegDistance the legDistance to set
	 */
	public void setLegDistance(String pLegDistance) {
		mLegDistance = pLegDistance;
	}
	/**
	 * @return the hasSeasonalClosure
	 */
	public boolean isHasSeasonalClosure() {
		return mHasSeasonalClosure;
	}
	/**
	 * @param pHasSeasonalClosure the hasSeasonalClosure to set
	 */
	public void setHasSeasonalClosure(boolean pHasSeasonalClosure) {
		mHasSeasonalClosure = pHasSeasonalClosure;
	}
	/**
	 * @return the hasCountryCross
	 */
	public boolean isHasCountryCross() {
		return mHasCountryCross;
	}
	/**
	 * @param pHasCountryCross the hasCountryCross to set
	 */
	public void setHasCountryCross(boolean pHasCountryCross) {
		mHasCountryCross = pHasCountryCross;
	}
	/**
	 * @return the formattedTime
	 */
	public String getFormattedTime() {
		return mFormattedTime;
	}
	/**
	 * @param pFormattedTime the formattedTime to set
	 */
	public void setFormattedTime(String pFormattedTime) {
		mFormattedTime = pFormattedTime;
	}
	/**
	 * @return the hasUnpaved
	 */
	public boolean isHasUnpaved() {
		return mHasUnpaved;
	}
	/**
	 * @param pHasUnpaved the hasUnpaved to set
	 */
	public void setHasUnpaved(boolean pHasUnpaved) {
		mHasUnpaved = pHasUnpaved;
	}
	/**
	 * @return the hasHighway
	 */
	public boolean isHasHighway() {
		return mHasHighway;
	}
	/**
	 * @param pHasHighway the hasHighway to set
	 */
	public void setHasHighway(boolean pHasHighway) {
		mHasHighway = pHasHighway;
	}
	/**
	 * @return the hasFerry
	 */
	public boolean isHasFerry() {
		return mHasFerry;
	}
	/**
	 * @param pHasFerry the hasFerry to set
	 */
	public void setHasFerry(boolean pHasFerry) {
		mHasFerry = pHasFerry;
	}
	/**
	 * @return the maneuvers
	 */
	public List<Maneuvers> getManeuvers() {
		return mManeuvers;
	}
	/**
	 * @param pManeuvers the maneuvers to set
	 */
	public void setManeuvers(List<Maneuvers> pManeuvers) {
		mManeuvers = pManeuvers;
	}

	

}