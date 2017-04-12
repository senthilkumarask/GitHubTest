package com.bbb.selfservice.common;
import java.io.Serializable;

import net.sf.ezmorph.bean.MorphDynaBean;

/**
 * @author akhaju
 * 
 */
public class Maneuvers implements Serializable {


	private Integer mIndex;
	private Integer mDirection;
	private String mNarrative;
	private String mIconUrl;
	private String mDistance;
	private Integer mTime;
	private Integer mAttributes;
	private String mTransportMode;
	private String mDirectionName;
	private String mMapUrl;
	private MorphDynaBean mStartPoint;
	private String mFormattedTime;
	/**
	 * @return the index
	 */
	public Integer getIndex() {
		return mIndex;
	}
	/**
	 * @param pIndex the index to set
	 */
	public void setIndex(Integer pIndex) {
		mIndex = pIndex;
	}
	/**
	 * @return the direction
	 */
	public Integer getDirection() {
		return mDirection;
	}
	/**
	 * @param pDirection the direction to set
	 */
	public void setDirection(Integer pDirection) {
		mDirection = pDirection;
	}
	/**
	 * @return the narrative
	 */
	public String getNarrative() {
		return mNarrative;
	}
	/**
	 * @param pNarrative the narrative to set
	 */
	public void setNarrative(String pNarrative) {
		mNarrative = pNarrative;
	}
	
	/**
	 * @return the iconUrl
	 */
	public String getIconUrl() {
		return mIconUrl;
	}
	/**
	 * @param pIconUrl the iconUrl to set
	 */
	public void setIconUrl(String pIconUrl) {
		mIconUrl = pIconUrl;
	}
	/**
	 * @return the distance
	 */
	public String getDistance() {
		return mDistance;
	}
	/**
	 * @param pDistance the distance to set
	 */
	public void setDistance(String pDistance) {
		mDistance = pDistance;
	}
	/**
	 * @return the time
	 */
	public Integer getTime() {
		return mTime;
	}
	/**
	 * @param pTime the time to set
	 */
	public void setTime(Integer pTime) {
		mTime = pTime;
	}
	/**
	 * @return the attributes
	 */
	public Integer getAttributes() {
		return mAttributes;
	}
	/**
	 * @param pAttributes the attributes to set
	 */
	public void setAttributes(Integer pAttributes) {
		mAttributes = pAttributes;
	}
	/**
	 * @return the transportMode
	 */
	public String getTransportMode() {
		return mTransportMode;
	}
	/**
	 * @param pTransportMode the transportMode to set
	 */
	public void setTransportMode(String pTransportMode) {
		mTransportMode = pTransportMode;
	}
	/**
	 * @return the directionName
	 */
	public String getDirectionName() {
		return mDirectionName;
	}
	/**
	 * @param pDirectionName the directionName to set
	 */
	public void setDirectionName(String pDirectionName) {
		mDirectionName = pDirectionName;
	}
	/**
	 * @return the mapUrl
	 */
	public String getMapUrl() {
		return mMapUrl;
	}
	/**
	 * @param pMapUrl the mapUrl to set
	 */
	public void setMapUrl(String pMapUrl) {
		mMapUrl = pMapUrl;
	}
	/**
	 * @return the startPoint
	 */
	public MorphDynaBean getStartPoint() {
		return mStartPoint;
	}
	/**
	 * @param pStartPoint the startPoint to set
	 */
	public void setStartPoint(MorphDynaBean pStartPoint) {
		mStartPoint = pStartPoint;
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
	
	
	
}
