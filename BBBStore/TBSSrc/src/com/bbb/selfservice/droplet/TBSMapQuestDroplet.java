/**
 * 
 */
package com.bbb.selfservice.droplet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.manager.SearchStoreManager;
import com.bbb.tbs.selfservice.manager.TBSMapQuestManager;
import com.bbb.utils.BBBUtility;

public class TBSMapQuestDroplet extends BBBDynamoServlet{
	
	public static final String PARAM_RESULT="result";
	public static final String PARAM_MILES="miles";
	public static final String PARAM_CHANGED_RADIUS="changedRadius";
	public static final String PARAM_RADIUS="radius";
	public static final String OUTPUT="output";
	public static final String ERROR="error";
	public static final String PARAM_SITEID="siteId";
	public static final String PARAM_ZIPCODE="Zipcode";
	public static final String PARAM_CITYSTATE="cityState";
	
	String mDefaultRadius;
	TBSMapQuestManager mMapQuestManager;
	private SearchStoreManager mSearchStoreManager;
	
	/**
	 * This droplet takes in the params Zipcode and radius entered and fetches latitude and longitude.
	 * Which in turn this latitude and longitude are used to fetch the near by stores
	 */
	@Override
	public void service(DynamoHttpServletRequest req,
			DynamoHttpServletResponse res) throws ServletException, IOException {
	vlogDebug("Inside TBSMapQuestDroplet :: service() method :: start");
	String origin = null;
	String storeType = null;
	String siteId = req.getParameter(PARAM_SITEID);
	
	if(!StringUtils.isBlank(req.getParameter(PARAM_ZIPCODE))){
		
		origin = req.getParameter(PARAM_ZIPCODE);
		vlogDebug("The Origin Parameter is passed as Zipcode :: Origin", origin);
		
	}else if(!StringUtils.isBlank(req.getParameter(PARAM_CITYSTATE))){
		
		origin = req.getParameter(PARAM_CITYSTATE);
		vlogDebug("The Origin Parameter is passed as cityState :: Origin", origin);
	}
	Object radius = req.getObjectParameter(PARAM_RADIUS);
	
	if(radius == null){
		
		radius = getDefaultRadius();
		vlogDebug("The radius Parameter is passed as default Value :: radius", radius);
	}
	
    try {
		storeType = getSearchStoreManager().getStoreType(siteId);
	}  catch (BBBSystemException bbbSystemException) {
		logError(LogMessageFormatter.formatMessage(req, "err_store_search_tech_error", BBBCoreErrorConstants.ACCOUNT_ERROR_1194 ), bbbSystemException);
		req.setParameter("errorMessage", bbbSystemException.getErrorCode() + bbbSystemException.getMessage());
		req.serviceLocalParameter(ERROR, req, res);
		return;
	} catch (BBBBusinessException bbbBusinessException) {
		logError(LogMessageFormatter.formatMessage(req, "err_store_search_tech_error", BBBCoreErrorConstants.ACCOUNT_ERROR_1195 ), bbbBusinessException);
		req.setParameter("errorMessage", bbbBusinessException.getErrorCode() + bbbBusinessException.getMessage());
		req.serviceLocalParameter(ERROR, req, res);
		return;
	}
	
	if(origin!=null && radius!=null){
	
			Map<String,Object> latLongMap = new HashMap<String,Object>();
			try {
				
				latLongMap = getMapQuestManager().constructSearchString(storeType, radius, origin);
				vlogDebug("The latitude and longitude map is :: ", latLongMap);
				
			} catch (BBBBusinessException e) {
				vlogError("Exception in calling Mapquest in SERVICE", e);
				req.serviceLocalParameter(ERROR, req, res);
			} catch (BBBSystemException e) {
				logError("\n TBSMapQuestDroplet.SERVICE Request sent to MapQuest web service call is ");
				BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_MAPQUEST_1001, "Exception in calling Mapquest in SERVICE");
				req.serviceLocalParameter(ERROR, req, res);
				
			}	

			if(!latLongMap.isEmpty() && latLongMap.size()>0){
				
				req.setParameter(PARAM_RESULT, latLongMap);
				req.setParameter(PARAM_MILES, radius);
				req.setParameter(PARAM_CHANGED_RADIUS, radius);
				req.serviceLocalParameter(OUTPUT, req, res);
			
			}else {
		
				req.setParameter(PARAM_RESULT, latLongMap);
				req.setParameter(PARAM_MILES, "");
				req.setParameter(PARAM_CHANGED_RADIUS, "");
				req.serviceLocalParameter(ERROR, req, res);
				
			}
		
		}
	vlogDebug("Inside TBSMapQuestDroplet :: service() method :: End");
	}

	
	/**
	 * @return the searchStoreManager
	 */
	public SearchStoreManager getSearchStoreManager() {
		return this.mSearchStoreManager;
	}

	/**
	 * @param pSearchStoreManager
	 *            the searchStoreManager to set
	 */
	public void setSearchStoreManager(SearchStoreManager pSearchStoreManager) {
		this.mSearchStoreManager = pSearchStoreManager;
	}

	/**
	 * @return the mapQuestManager
	 */
	public TBSMapQuestManager getMapQuestManager() {
		return mMapQuestManager;
	}
	/**
	 * @param pMapQuestManager the mapQuestManager to set
	 */
	public void setMapQuestManager(TBSMapQuestManager pMapQuestManager) {
		mMapQuestManager = pMapQuestManager;
	}
	

	/**
	 * @return the defaultRadius
	 */
	public String getDefaultRadius() {
		return mDefaultRadius;
	}
	/**
	 * @param pDefaultRadius the defaultRadius to set
	 */
	public void setDefaultRadius(String pDefaultRadius) {
		mDefaultRadius = pDefaultRadius;
	}
}
