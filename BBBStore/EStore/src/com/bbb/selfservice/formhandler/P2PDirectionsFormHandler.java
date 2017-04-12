package com.bbb.selfservice.formhandler;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

import atg.droplet.DropletException;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.common.BBBGenericFormHandler;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.common.RouteDetails;
import com.bbb.selfservice.common.RouteLegs;
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.manager.SearchStoreManager;
import com.bbb.utils.BBBUtility;

/**
 * @author Jaswinder Sidhu
 * 
 */
public class P2PDirectionsFormHandler extends BBBGenericFormHandler {

	private SearchStoreManager mSearchStoreManager;
	private String mPostalCode;
	private String mStreet;
	private String mCity;
	private String mState;
	private String mSuccessURL;
	private String mErrorURL;
	private String mRouteStartPoint;
	private String mRouteEndPoint;
	private String mRouteMapOption;
	private String mRouteType;
	private boolean mSeasonalRoad;
	private boolean mHighways;
	private boolean mTollRoad;
	private RouteLegs mP2PRoute;
	private RouteDetails mP2PRouteDetails;
	private String mShowDirectionsWithMaps;
	//private List<StateVO> mStateList;
	private BBBCatalogTools mCatalogTools;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private String mDestPostalCode;
	private String mDestStreet;
	private String mDestCity;
	private String mDestState;
	private List<String> mErrorArray;
	private SiteContext mSiteContext;

	/**
	 * @return the siteContext
	 */
	public SiteContext getSiteContext() {
		return mSiteContext;
	}

	/**
	 * @param pSiteContext the siteContext to set
	 */
	public void setSiteContext(SiteContext pSiteContext) {
		mSiteContext = pSiteContext;
	}

	/**
	 * 
	 */
	public P2PDirectionsFormHandler() {
		super();
		mErrorArray = new ArrayList<String>();
	}

	public void validateDirectionsInput(DynamoHttpServletRequest request,
			DynamoHttpServletResponse response) {
			logDebug("P2PDirectionsFormHandler.validateDirectionsInput() method started");
		
		if ((BBBUtility.isEmpty(getState()) || getState().equals("-1"))
				&& BBBUtility.isEmpty(getPostalCode())) {
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
					"err_store_mandatory_fields", request.getLocale().getLanguage(),
					null, null),"err_store_mandatory_fields"));
		} else if (!BBBUtility.isEmpty(getPostalCode())){
			if ((getPostalCode().length() != BBBCoreConstants.FIVE)  ) {
				addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
						"err_store_postalcode_length", request.getLocale().getLanguage(),
						null, null),"err_store_postalcode_length"));
			}
			else if(StringUtils.isAlpha(getPostalCode()))
			{
				addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
						"err_store_postalcode_pattren", request.getLocale().getLanguage(),
						null, null),"err_store_postalcode_pattren"));
			}

		}

	}

	
	/**
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleP2PDirections(DynamoHttpServletRequest request,
			DynamoHttpServletResponse response) throws ServletException,
			IOException {
		validateDirectionsInput(request, response);
		if (this.getFormError()) {
			return checkFormRedirect(null, getErrorURL(), request, response);
		} else {
			try {
				resetFormExceptions();
				// Check routeEnd point values
				if (!BBBUtility.isEmpty(StringUtils.trim(getDestStreet()))
						&& !BBBUtility.isEmpty(StringUtils.trim(getDestCity()))
						&& !BBBUtility.isEmpty(StringUtils.trim(getDestState()))
						&& !BBBUtility.isEmpty(StringUtils.trim(getDestPostalCode()))) {
					
					generateEndPoint();
					generateStartPoint();
					// Check the input fields for routeStart points
					if (!BBBUtility.isEmpty(getRouteStartPoint())) {

						RouteDetails objP2pRouteDetails = getSearchStoreManager()
								.getStoreDirections(getRouteStartPoint(),
										StringUtils.trim(getRouteEndPoint()), getRouteType(),
										isSeasonalRoad(), isHighways(),
										isTollRoad());
						if (objP2pRouteDetails != null) {
							setP2PRouteDetails(objP2pRouteDetails);
							/*setRouteStartPoint(objP2pRouteDetails
									.getRouteStartPoint());*/

						}
					}
				}

			} 
			catch (BBBBusinessException bException) {
				resetFormExceptions();
				setRouteType(SelfServiceConstants.ROUTETYPEQUICK);
				
				logError(LogMessageFormatter.formatMessage(request, "err_store_invalid_starting_point" , BBBCoreErrorConstants.ACCOUNT_ERROR_1124),bException);
				
				if(null!=getErrorArray() && !getErrorArray().isEmpty()){
					getErrorArray().clear();
				}				
				List<String> list = getErrorArray();
				
				list.add(getLblTxtTemplateManager().getErrMsg(
						"err_store_invalid_starting_point", request.getLocale().getLanguage(),
						null, null));
				setErrorArray(list);
				addFormException(new DropletException(bException.getMessage(),"err_store_invalid_starting_point"));
				checkFormRedirect(null, getErrorURL(), request, response);
			}
			catch (BBBSystemException bException) {
				resetFormExceptions();
				setRouteType(SelfServiceConstants.ROUTETYPEQUICK);
				
				logError(LogMessageFormatter.formatMessage(request, "err_store_invalid_starting_point" , BBBCoreErrorConstants.ACCOUNT_ERROR_1124),bException);
				
				if(null!=getErrorArray() && !getErrorArray().isEmpty()){
					getErrorArray().clear();
				}				
				List<String> list = getErrorArray();
				
				list.add(getLblTxtTemplateManager().getErrMsg(
						"err_store_invalid_starting_point", request.getLocale().getLanguage(),
						null, null));
				setErrorArray(list);
				addFormException(new DropletException(bException.getMessage(),"err_store_invalid_starting_point"));
				checkFormRedirect(null, getErrorURL(), request, response);
			}

			return checkFormRedirect(getSuccessURL(), getErrorURL(), request,
					response);
		}
	}

	/**
	 * 
	 * If postal code is given in input criteria the directions will be based on postal code else city, state will be used.
	 */
	private void generateStartPoint() {
		StringBuilder tempRouteStartPoint = new StringBuilder();
		appendString(tempRouteStartPoint,getStreet());
		appendString(tempRouteStartPoint, getCity());
		if (!getState().equals("-1")) {
			appendString(tempRouteStartPoint, getState());
		}
		appendString(tempRouteStartPoint,getPostalCode());
			//appendString(tempRouteStartPoint, getPostalCode());
		setRouteStartPoint(tempRouteStartPoint.toString());
	}

	/**
	 * 
	 * Generate End point from street, city, state and postal code.
	 */
	private void generateEndPoint() {
		StringBuilder tempRouteEndPoint = new StringBuilder();		
			tempRouteEndPoint.append(StringUtils.trim(getDestStreet()));
			appendString(tempRouteEndPoint, StringUtils.trim(getDestCity()));
			appendString(tempRouteEndPoint, StringUtils.trim(getDestState()));
			appendString(tempRouteEndPoint, StringUtils.trim(getDestPostalCode()));
		setRouteEndPoint(tempRouteEndPoint.toString());
	}


	private void appendString(StringBuilder pRouteString, String pAddition) {
		if (!BBBUtility.isEmpty(pAddition)) {
			pRouteString.append(
					!BBBUtility.isEmpty(pRouteString.toString()) ? "," : "")
					.toString();
			pRouteString.append(pAddition);
		}
	}

	public boolean handleExtraParam(DynamoHttpServletRequest request,
			DynamoHttpServletResponse response) throws ServletException,
			IOException {

		// Check routeStart point & routeEnd point values
		if (!BBBUtility.isEmpty(getRouteStartPoint())
				&& !BBBUtility.isEmpty(StringUtils.trim(getRouteEndPoint()))) {
			if (isLoggingInfo()) {
				logInfo("---Inside P2PDirectionsFormHandler.handleDirectionsExtraParam() method---");
			}
			try {
				RouteDetails objP2pRouteDetails = getSearchStoreManager()
						.getStoreDirections(getRouteStartPoint(),
								StringUtils.trim(getRouteEndPoint()), getRouteType(),
								isSeasonalRoad(), isHighways(), isTollRoad());

				if (objP2pRouteDetails != null) {
					if (isLoggingInfo()) {
						logInfo("Value inside RouteDirections object - Distance to travel--> "
								+ objP2pRouteDetails.getTotalDistance()
								+ "-- Travling Time ---> "
								+ objP2pRouteDetails.getFormattedTime());
					}
					// request.setParameter("routeDirections",
					// objP2pRouteDetails);
					// request.setParameter("routeStartPoint",
					// getRouteStartPoint());
					// request.setParameter("routeEndPoint",
					// getRouteEndPoint());
					// request.setParameter("showDirectionsWithMaps",
					// getShowDirectionsWithMaps());
					setP2PRouteDetails(objP2pRouteDetails);
				}
			} 
			catch (BBBBusinessException bException) {
				resetFormExceptions();
				addFormException(new DropletException(bException.getMessage(),
						bException.getErrorCode()));
				return checkFormRedirect(null, getErrorURL(), request, response);
			}
			catch (BBBSystemException bException) {
				resetFormExceptions();
				addFormException(new DropletException(bException.getMessage(),
						bException.getErrorCode()));
				return checkFormRedirect(null, getErrorURL(), request, response);
			}
		}
		return checkFormRedirect(getSuccessURL(), getErrorURL(), request,
				response);
	}

	public static String formatString(String firstPart, String secondPart) {
		StringBuilder strBuild = new StringBuilder(firstPart);
		strBuild.append(",");
		strBuild.append(secondPart);
		return strBuild.toString();
	}

	/**
	 * @return the stateList
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public List<StateVO> getStateList() throws BBBSystemException, BBBBusinessException {
		return getCatalogTools().getStates(SiteContextManager.getCurrentSiteId(),false, null);
	}

	/**
	 * @param pStateList
	 *            the stateList to set
	 *//*
	public void setStateList(List<StateVO> pStateList) {
		mStateList = getCatalogTools().getStates("BuyBuyBaby");
	}*/

	/**
	 * @return the searchStoreManager
	 */
	public SearchStoreManager getSearchStoreManager() {
		return mSearchStoreManager;
	}

	/**
	 * @param pSearchStoreManager
	 *            the searchStoreManager to set
	 */
	public void setSearchStoreManager(SearchStoreManager pSearchStoreManager) {
		mSearchStoreManager = pSearchStoreManager;
	}

	/**
	 * @return the successURL
	 */
	public String getSuccessURL() {
		return mSuccessURL;
	}

	/**
	 * @param pSuccessURL
	 *            the successURL to set
	 */
	public void setSuccessURL(String pSuccessURL) {
		mSuccessURL = pSuccessURL;
	}

	/**
	 * @return the errorURL
	 */
	public String getErrorURL() {
		return mErrorURL;
	}

	/**
	 * @param pErrorURL
	 *            the errorURL to set
	 */
	public void setErrorURL(String pErrorURL) {
		mErrorURL = pErrorURL;
	}

	/**
	 * @return the routeStartPoint
	 */
	public String getRouteStartPoint() {
		return mRouteStartPoint;
	}

	/**
	 * @param pRouteStartPoint
	 *            the routeStartPoint to set
	 */
	public void setRouteStartPoint(String pRouteStartPoint) {
		mRouteStartPoint = pRouteStartPoint;
	}

	/**
	 * @return the routeEndPoint
	 */
	public String getRouteEndPoint() {
		return mRouteEndPoint;
	}

	/**
	 * @param pRouteEndPoint
	 *            the routeEndPoint to set
	 */
	public void setRouteEndPoint(String pRouteEndPoint) {
		mRouteEndPoint = pRouteEndPoint;
	}

	/**
	 * @return the routeMapOption
	 */
	public String getRouteMapOption() {
		return mRouteMapOption;
	}

	/**
	 * @param pRouteMapOption
	 *            the routeMapOption to set
	 */
	public void setRouteMapOption(String pRouteMapOption) {
		mRouteMapOption = pRouteMapOption;
	}

	/**
	 * @return the routeType
	 */
	public String getRouteType() {
		return mRouteType;
	}

	/**
	 * @param pRouteType
	 *            the routeType to set
	 */
	public void setRouteType(String pRouteType) {
		mRouteType = pRouteType;
	}

	/**
	 * @return the seasonalRoad
	 */
	public boolean isSeasonalRoad() {
		return mSeasonalRoad;
	}

	/**
	 * @param pSeasonalRoad
	 *            the seasonalRoad to set
	 */
	public void setSeasonalRoad(boolean pSeasonalRoad) {
		mSeasonalRoad = pSeasonalRoad;
	}

	/**
	 * @return the highways
	 */
	public boolean isHighways() {
		return mHighways;
	}

	/**
	 * @param pHighways
	 *            the highways to set
	 */
	public void setHighways(boolean pHighways) {
		mHighways = pHighways;
	}

	/**
	 * @return the tollRoad
	 */
	public boolean isTollRoad() {
		return mTollRoad;
	}

	/**
	 * @param pTollRoad
	 *            the tollRoad to set
	 */
	public void setTollRoad(boolean pTollRoad) {
		mTollRoad = pTollRoad;
	}

	/**
	 * @return the p2PRoute
	 */
	public RouteLegs getP2PRoute() {
		return mP2PRoute;
	}

	/**
	 * @param pP2pRoute
	 *            the p2PRoute to set
	 */
	public void setP2PRoute(RouteLegs pP2pRoute) {
		mP2PRoute = pP2pRoute;
	}

	/**
	 * @return the p2PRouteDetails
	 */
	public RouteDetails getP2PRouteDetails() {
		return mP2PRouteDetails;
	}

	/**
	 * @param pP2pRouteDetails
	 *            the p2PRouteDetails to set
	 */
	public void setP2PRouteDetails(RouteDetails pP2pRouteDetails) {
		mP2PRouteDetails = pP2pRouteDetails;
	}

	/**
	 * @return the showDirectionsWithMaps
	 */
	public String getShowDirectionsWithMaps() {
		return mShowDirectionsWithMaps;
	}

	/**
	 * @param pShowDirectionsWithMaps
	 *            the showDirectionsWithMaps to set
	 */
	public void setShowDirectionsWithMaps(String pShowDirectionsWithMaps) {
		mShowDirectionsWithMaps = pShowDirectionsWithMaps;
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
	 * @return the street
	 */
	public String getStreet() {
		return mStreet;
	}

	/**
	 * @param pStreet
	 *            the street to set
	 */
	public void setStreet(String pStreet) {
		mStreet = pStreet;
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
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param pCatalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	/**
	 * @return the lblTxtTemplateManager
	 */
	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	/**
	 * @param pLblTxtTemplateManager the lblTxtTemplateManager to set
	 */
	public void setLblTxtTemplateManager(
			LblTxtTemplateManager pLblTxtTemplateManager) {
		lblTxtTemplateManager = pLblTxtTemplateManager;
	}

	/**
	 * @return the destPostalCode
	 */
	public String getDestPostalCode() {
		return mDestPostalCode;
	}

	/**
	 * @param destPostalCode the destPostalCode to set
	 */
	public void setDestPostalCode(String destPostalCode) {
		mDestPostalCode = destPostalCode;
	}

	/**
	 * @return the destStreet
	 */
	public String getDestStreet() {
		return mDestStreet;
	}

	/**
	 * @param destStreet the destStreet to set
	 */
	public void setDestStreet(String destStreet) {
		mDestStreet = destStreet;
	}

	/**
	 * @return the destCity
	 */
	public String getDestCity() {
		return mDestCity;
	}

	/**
	 * @param destCity the destCity to set
	 */
	public void setDestCity(String destCity) {
		mDestCity = destCity;
	}

	/**
	 * @return the destState
	 */
	public String getDestState() {
		return mDestState;
	}

	/**
	 * @param destState the destState to set
	 */
	public void setDestState(String destState) {
		mDestState = destState;
	}

	/**
	 * @return the errorArray
	 */
	public List<String> getErrorArray() {
		return mErrorArray;
	}

	/**
	 * @param errorArray the errorArray to set
	 */
	public void setErrorArray(List<String> pErrorArray) {
		this.mErrorArray = pErrorArray;
	}


	


}
