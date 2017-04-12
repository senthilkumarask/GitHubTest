package com.bbb.certona.vo;

import java.io.Serializable;
import java.sql.Timestamp;

import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.repository.RepositoryItem;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

public class ShippingDetails implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final ApplicationLogging MLOGGING =
		    ClassLoggingFactory.getFactory().getLoggerForClass(ShippingDetails.class);
	private String shipMethodId;
	private String shipMethodName;
	private String shipMethodDescription;
	private int minDaysToShip;
	private int maxDaysToShip;
	private int minDaysToShipVDC;
	private int maxDaysToShipVDC;
	private Timestamp cutOffTime;
	private RepositoryItem shippingMethodRepsoitoryItem;
	private String siteId;
	private BBBCatalogTools catalogTools;
	
	/*public ShippingDetails(RepositoryItem shippingMethodRepsoitoryItem) {
		this.shippingMethodRepsoitoryItem=shippingMethodRepsoitoryItem;
	}
*/
	public ShippingDetails(RepositoryItem shippingMethodRepsoitoryItem, BBBCatalogTools catalogTools, String siteId) {
		this.shippingMethodRepsoitoryItem=shippingMethodRepsoitoryItem;
		this.catalogTools = catalogTools;
		this.siteId=siteId;
	}
	/**
	 * @return the shipMethodId
	 */
	public String getShipMethodId() {
		if(this.shippingMethodRepsoitoryItem!=null){
			return shippingMethodRepsoitoryItem.getRepositoryId();
			} else{
				return this.shipMethodId;
			}
	}
	/**
	 * @param pShipMethodId the shipMethodId to set
	 */
	public void setShipMethodId(String pShipMethodId) {
		shipMethodId = pShipMethodId;
	}
	/**
	 * @return the shipMethodName
	 */
	public String getShipMethodName() {
		if(this.shippingMethodRepsoitoryItem!=null && shippingMethodRepsoitoryItem.getPropertyValue("shipMethodName")!=null){
			return (String) shippingMethodRepsoitoryItem.getPropertyValue("shipMethodName");
		}
		else{
		return this.shipMethodName;
		}
	}
	/**
	 * @param pShipMethodName the shipMethodName to set
	 */
	public void setShipMethodName(String pShipMethodName) {
		shipMethodName = pShipMethodName;
	}
	/**
	 * @return the shipMethodDescription
	 */
	public String getShipMethodDescription() {
		if(this.shippingMethodRepsoitoryItem!=null && shippingMethodRepsoitoryItem.getPropertyValue(BBBCatalogConstants.SHIPPING_METHOD_DESCRIPTION_SHIPPING_PROPERTY_NAME)!=null){
			return (String) shippingMethodRepsoitoryItem.getPropertyValue(BBBCatalogConstants.SHIPPING_METHOD_DESCRIPTION_SHIPPING_PROPERTY_NAME);
		}
		else{
		return this.shipMethodDescription;
		}
	}
	/**
	 * @param pShipMethodDescription the shipMethodDescription to set
	 */
	public void setShipMethodDescription(String pShipMethodDescription) {
		shipMethodDescription = pShipMethodDescription;
	}
	/**
	 * @return the minDaysToShip
	 */
	public int getMinDaysToShip() {
		RepositoryItem shippingDurationItem = null;
		try {
			shippingDurationItem = this.catalogTools.getShippingDuration(getShipMethodId(), siteId);
		} catch (BBBBusinessException e) {
			  MLOGGING.logError("Error getting minimum days to ship "+ e.getMessage());
		} catch (BBBSystemException e) {
			 MLOGGING.logError("Error getting minimum days to ship "+ e.getMessage());
		}
		if(shippingDurationItem!=null && shippingDurationItem.getPropertyValue(BBBCatalogConstants.MAX_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME)!=null){
			return (Integer) shippingDurationItem.getPropertyValue(BBBCatalogConstants.MAX_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME);
		}
		else{
		return this.minDaysToShip;
		}
	}
	/**
	 * @param pMinDaysToShip the minDaysToShip to set
	 */
	public void setMinDaysToShip(int pMinDaysToShip) {
		
		minDaysToShip = pMinDaysToShip;
	}
	/**
	 * @return the maxDaysToShip
	 */
	public int getMaxDaysToShip() {
		RepositoryItem shippingDurationItem = null;
		try {
			shippingDurationItem = this.catalogTools.getShippingDuration(getShipMethodId(), siteId);
		} catch (BBBBusinessException e) {
			 MLOGGING.logError("Error getting maximum days to ship "+ e.getMessage());
		} catch (BBBSystemException e) {
			 MLOGGING.logError("Error getting maximum days to ship "+ e.getMessage());
		}
		if(shippingDurationItem!=null && shippingDurationItem.getPropertyValue(BBBCatalogConstants.MIN_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME)!=null){
			return (Integer) shippingDurationItem.getPropertyValue(BBBCatalogConstants.MIN_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME);
		}
		else{
		return this.maxDaysToShip;
		}
	}
	/**
	 * @param pMaxDaysToShip the maxDaysToShip to set
	 */
	public void setMaxDaysToShip(int pMaxDaysToShip) {
		maxDaysToShip = pMaxDaysToShip;
	}
	/**
	 * @return the cutOffTime
	 */
	public Timestamp getCutOffTime() {
		RepositoryItem shippingDurationItem = null;
		try {
			shippingDurationItem = this.catalogTools.getShippingDuration(getShipMethodId(), siteId);
		} catch (BBBBusinessException e) {
			 MLOGGING.logError("Error getting cut off time "+ e.getMessage());
		} catch (BBBSystemException e) {
			 MLOGGING.logError("Error getting cut off time "+ e.getMessage());
		}
		if(shippingDurationItem!=null && shippingDurationItem.getPropertyValue(BBBCatalogConstants.CUT_OFF_TIME_SHIPPING_PROPERTY_NAME)!=null){
			return (Timestamp) shippingDurationItem.getPropertyValue(BBBCatalogConstants.CUT_OFF_TIME_SHIPPING_PROPERTY_NAME);
		}
		else{
		return this.cutOffTime;
		}
	}
	/**
	 * @param pCutOffTime the cutOffTime to set
	 */
	public void setCutOffTime(Timestamp pCutOffTime) {
		cutOffTime = pCutOffTime;
	}
	public int getMinDaysToShipVDC() {

		RepositoryItem shippingDurationItem = null;
		try {
			shippingDurationItem = this.catalogTools.getShippingDuration(getShipMethodId(), siteId);
		} catch (BBBBusinessException e) {
			 MLOGGING.logError("Error getting minimum days to ship VDC "+ e.getMessage());
		} catch (BBBSystemException e) {
			 MLOGGING.logError("Error getting minimum days to ship VDC "+ e.getMessage());
		}
		if(shippingDurationItem!=null && shippingDurationItem.getPropertyValue(BBBCatalogConstants.MIN_DAYS_TO_SHIP_VDC_PROPERTY_NAME)!=null){
			return (Integer) shippingDurationItem.getPropertyValue(BBBCatalogConstants.MIN_DAYS_TO_SHIP_VDC_PROPERTY_NAME);
		}
		else{
		return this.minDaysToShipVDC;
		}
	}
	public void setMinDaysToShipVDC(int minDaysToShipVDC) {
		this.minDaysToShipVDC = minDaysToShipVDC;
	}
	public int getMaxDaysToShipVDC() {
		RepositoryItem shippingDurationItem = null;
		try {
			shippingDurationItem = this.catalogTools.getShippingDuration(getShipMethodId(), siteId);
		} catch (BBBBusinessException e) {
			 MLOGGING.logError("Error getting maximum days to ship VDC "+ e.getMessage());
		} catch (BBBSystemException e) {
			 MLOGGING.logError("Error getting maximum days to ship VDC "+ e.getMessage());
		}
		if(shippingDurationItem!=null && shippingDurationItem.getPropertyValue(BBBCatalogConstants.MAX_DAYS_TO_SHIP_VDC_PROPERTY_NAME)!=null){
			return (Integer) shippingDurationItem.getPropertyValue(BBBCatalogConstants.MAX_DAYS_TO_SHIP_VDC_PROPERTY_NAME);
		}
		else{
		return this.maxDaysToShipVDC;
		}
	}
	public void setMaxDaysToShipVDC(int maxDaysToShipVDC) {
		this.maxDaysToShipVDC = maxDaysToShipVDC;
	}
	

}
