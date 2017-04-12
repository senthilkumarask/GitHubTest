package com.bbb.selfservice.common;

import java.io.Serializable;
import java.util.List;


/**
 * @author Jaswinder Sidhu
 * 
 */
public class RouteDetails implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean mHasTollRoad;
	private String mTotalDistance;
	private boolean mHasSeasonalClosure;
	private boolean mHasCountryCross;
	private String mFormattedTime;
	private boolean mHasUnpaved;
	private boolean mHasHighway;
	private boolean mHasFerry;
	private String mSessionId;
	private String mStartPointLat;
	private String mStartPointLng;
	private String mEndPointLat;
	private String mEndPointLng;
	private List<RouteLegs> mRouteLegs;
	private String mRouteMap;
	private String mRouteStartPoint;
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
	 * @return the totalDistance
	 */
	public String getTotalDistance() {
		return mTotalDistance;
	}
	/**
	 * @param pTotalDistance the totalDistance to set
	 */
	public void setTotalDistance(String pTotalDistance) {
		mTotalDistance = pTotalDistance;
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
	 * @return the routeLegs
	 */
	public List<RouteLegs> getRouteLegs() {
		return mRouteLegs;
	}
	/**
	 * @param pRouteLegs the routeLegs to set
	 */
	public void setRouteLegs(List<RouteLegs> pRouteLegs) {
		mRouteLegs = pRouteLegs;
	}
	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return mSessionId;
	}
	/**
	 * @param pSessionId the sessionId to set
	 */
	public void setSessionId(String pSessionId) {
		mSessionId = pSessionId;
	}
	/**
	 * @return the startPointLat
	 */
	public String getStartPointLat() {
		return mStartPointLat;
	}
	/**
	 * @param pStartPointLat the startPointLat to set
	 */
	public void setStartPointLat(String pStartPointLat) {
		mStartPointLat = pStartPointLat;
	}
	/**
	 * @return the startPointLng
	 */
	public String getStartPointLng() {
		return mStartPointLng;
	}
	/**
	 * @param pStartPointLng the startPointLng to set
	 */
	public void setStartPointLng(String pStartPointLng) {
		mStartPointLng = pStartPointLng;
	}
	/**
	 * @return the endPointLat
	 */
	public String getEndPointLat() {
		return mEndPointLat;
	}
	/**
	 * @param pEndPointLat the endPointLat to set
	 */
	public void setEndPointLat(String pEndPointLat) {
		mEndPointLat = pEndPointLat;
	}
	/**
	 * @return the endPointLng
	 */
	public String getEndPointLng() {
		return mEndPointLng;
	}
	/**
	 * @param pEndPointLng the endPointLng to set
	 */
	public void setEndPointLng(String pEndPointLng) {
		mEndPointLng = pEndPointLng;
	}
	/**
	 * @return the routeMap
	 */
	public String getRouteMap() {
		return mRouteMap;
	}
	/**
	 * @param pRouteMap the routeMap to set
	 */
	public void setRouteMap(String pRouteMap) {
		mRouteMap = pRouteMap;
	}
	/**
	 * @return the routeStartPoint
	 */
	public String getRouteStartPoint() {
		return mRouteStartPoint;
	}
	/**
	 * @param pRouteStartPoint the routeStartPoint to set
	 */
	public void setRouteStartPoint(String pRouteStartPoint) {
		mRouteStartPoint = pRouteStartPoint;
	}

	

}