package com.bbb.commerce.shipping.droplet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import com.bbb.commerce.shipping.manager.HolidayMessagingManager;
import com.bbb.commerce.shipping.vo.HolidayMessagingVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

import atg.core.util.StringUtils;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class HolidayMessagingDroplet extends BBBDynamoServlet{
	
	public static final String SHIP_TIME = "shipTime";
	
	HolidayMessagingManager mHolidayMessagingManager;
	
	private Map mShipTimeMap;
	
	/**
	 * To query for the correct messaging based on item availability and selected shipping.  
	 * The droplet will use the current time on the server to find the
	 *  appropriate record and will return the appropriate string.
	 */
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		logDebug("HolidayMessagingDroplet :: service method start");
		
		String shipTime = pRequest.getParameter(SHIP_TIME);
		String availabilityOn=null;
		HolidayMessagingVO lHolidayMessagingVO = null;
		
		if(!StringUtils.isBlank(shipTime)){
			logDebug("the shipTime value is" +shipTime);
			availabilityOn = (String) getShipTimeMap().get(shipTime);
			try {
				String siteId = BBBUtility.getCurrentSiteId(pRequest);
				if (siteId!=null && siteId.endsWith(BBBCoreConstants.SITE_BAB_CA)) {
					logDebug("Getting holiday messaging for Canada site");
					lHolidayMessagingVO = getHolidayMessagingManager().getCanadaMessagingItems(availabilityOn);
				} else {
					logDebug("Getting holiday messaging for US or Baby site");
					lHolidayMessagingVO = getHolidayMessagingManager().getMessagingItems(availabilityOn);
				}
			} catch (RepositoryException e) {
				logError("Exception while querying the Holiday Messaging Repository" +e.getMessage());
				logDebug(e);
				pRequest.setParameter("errorMsg", "Exception while querying the Holiday Messaging Repository");
				pRequest.serviceLocalParameter(TBSConstants.ERROR, pRequest, pResponse);
			} catch (BBBSystemException e) {
				logError("Exception while fetching the Holiday Messaging from config keys" +e.getMessage());
				logDebug(e);
				pRequest.setParameter("errorMsg", "Exception while fetching the Holiday Messaging from config keys");
				pRequest.serviceLocalParameter(TBSConstants.ERROR, pRequest, pResponse);
			} catch (BBBBusinessException e) {
				logError("Exception while fetching the Holiday Messaging  from config keys" +e.getMessage());
				logDebug(e);
				pRequest.setParameter("errorMsg", "Exception while fetching the Holiday Messaging from config keys");
				pRequest.serviceLocalParameter(TBSConstants.ERROR, pRequest, pResponse);
			}
		} else{
			logDebug("the shipTime value is empty" +shipTime);
			pRequest.setParameter("errorMsg", "Empty Ship Time");
			pRequest.serviceLocalParameter(TBSConstants.EMPTY, pRequest,pResponse);
		}
		pRequest.setParameter("HolidayMessagingVO", lHolidayMessagingVO);
		pRequest.serviceLocalParameter(TBSConstants.OUTPUT, pRequest, pResponse);
		logDebug("HolidayMessagingDroplet :: service method end");
	}
	
	/**
	 * @return the holidayMessagingManager
	 */
	public HolidayMessagingManager getHolidayMessagingManager() {
		return mHolidayMessagingManager;
	}

	/**
	 * @param pHolidayMessagingManager the holidayMessagingManager to set
	 */
	public void setHolidayMessagingManager(
			HolidayMessagingManager pHolidayMessagingManager) {
		mHolidayMessagingManager = pHolidayMessagingManager;
	}

	/**
	 * @return the shipTimeMap
	 */
	public Map getShipTimeMap() {
		return mShipTimeMap;
	}

	/**
	 * @param pShipTimeMap the shipTimeMap to set
	 */
	public void setShipTimeMap(Map pShipTimeMap) {
		mShipTimeMap = pShipTimeMap;
	}
}
