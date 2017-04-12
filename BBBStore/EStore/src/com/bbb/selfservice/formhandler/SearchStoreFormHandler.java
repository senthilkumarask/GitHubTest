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
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.account.BBBProfileManager;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.AppointmentVO;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.common.BBBGenericFormHandler;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.common.StoreLocator;
import com.bbb.selfservice.manager.ScheduleAppointmentManager;
import com.bbb.selfservice.manager.SearchStoreManager;
import com.bbb.utils.BBBUtility;

/**
 * @author Jaswinder Sidhu
 * 
 */
public class SearchStoreFormHandler extends BBBGenericFormHandler {

	private SearchStoreManager mSearchStoreManager;

	private StoreLocator mStoreLocator;
	private String mRecordsPerPage;
	private String mMapQuestSearchString;
	private String mSearchStoreSuccessURL;
	private String mSearchStoreErrorURL;
	private String mInputSearchString;
	private int mMiles;
	//private List<StateVO> mStateList;
	private BBBCatalogTools mCatalogTools;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private int mCookieAge;
	private BBBProfileManager mManager;
	private Profile mProfile;
	private String mSiteContext;
	private String mInputType;
	private String mSearchBasedOn;
	private String mDefaultAppointment;
	private ScheduleAppointmentManager mScheduleAppointmentManager;
	private TransactionManager mTransactionManager;
	private boolean formInitialised;
	private static final Integer COOKIE_LOGIN_SECURITY_STATUS = Integer.valueOf(2);
	private String fromPage;// Page Name set from JSP
	private Map<String,String> successUrlMap;
	private Map<String,String> errorUrlMap;

	/**
	 * @return the fromPage
	 */
	public String getFromPage() {
		return fromPage;
	}

	/**
	 * @param fromPage the fromPage to set
	 */
	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	/**
	 * @return the successUrlMap
	 */
	public Map<String, String> getSuccessUrlMap() {
		return successUrlMap;
	}

	/**
	 * @param successUrlMap the successUrlMap to set
	 */
	public void setSuccessUrlMap(Map<String, String> successUrlMap) {
		this.successUrlMap = successUrlMap;
	}

	/**
	 * @return the errorUrlMap
	 */
	public Map<String, String> getErrorUrlMap() {
		return errorUrlMap;
	}

	/**
	 * @param errorUrlMap the errorUrlMap to set
	 */
	public void setErrorUrlMap(Map<String, String> errorUrlMap) {
		this.errorUrlMap = errorUrlMap;
	}

	/**
	 * Sets property TransactionManager
	 * 
	 * @param pTransactionManager
	 *            a <code>TransactionManager</code> value
	 */
	public void setTransactionManager(TransactionManager pTransactionManager) {
		mTransactionManager = pTransactionManager;
	}

	/**
	 * Returns property TransactionManager
	 * 
	 * @return a <code>TransactionManager</code> value
	 */
	public TransactionManager getTransactionManager() {
		return mTransactionManager;
	}
	
	/**
	 * @return the mMiles
	 */
	public int getMiles() {
		return mMiles;
	}

	/**
	 * @param mMiles the mMiles to set
	 */
	public void setMiles(int pMiles) {
		this.mMiles = pMiles;
	}

	/**
	/**
	 * @return the siteContext
	 */
	public String getSiteContext() {
		return mSiteContext;
	}

	/**
	 * @param pSiteContext the siteContext to set
	 */
	public void setSiteContext(String pSiteContext) {
		mSiteContext = pSiteContext;
	}

	/**
	 * @return the manager
	 */
	public BBBProfileManager getManager() {
		return mManager;
	}

	/**
	 * @param pManager the manager to set
	 */
	public void setManager(BBBProfileManager pManager) {
		mManager = pManager;
	}

	/**
	 * @return the profile
	 */
	public Profile getProfile() {
		return mProfile;
	}

	/**
	 * @param pProfile the profile to set
	 */
	public void setProfile(Profile pProfile) {
		mProfile = pProfile;
	}

	
	
	// private String mShowDirectionsWithMaps;
	// Member variable for P2PDirections
	/*
	 * private String mRouteStartPoint; private String mRouteEndPoint; private
	 * String mRouteMapOption; private String mRouteType; private RouteLegs
	 * mP2PRoute; private RouteDetails mP2PRouteDetails;
	 */

	public void preSearchStore(DynamoHttpServletRequest request,
			DynamoHttpServletResponse response) {		
			if(isLoggingDebug()){
				logDebug("SearchStoreFormHandler.preSearchStore() method started"); 
			}
		if(!BBBUtility.isEmpty(getStoreLocator().getPostalCode()))
		{
			if(SiteContextManager.getCurrentSiteId().equals("BedBathCanada"))
			{
			if (!BBBUtility.isCanadaZipValid(getStoreLocator().getPostalCode()) ) 
			{
				logError("err_store_postalcode_invalid");
				addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
						"err_store_postalcode_invalid", request.getLocale().getLanguage(),
						null, null),"err_store_postalcode_invalid"));
			}
			
		    }
			if (!BBBUtility.isValidZip(getStoreLocator().getPostalCode()) ) 
			{
				logError("err_store_postalcode_invalid");
				addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
						"err_store_postalcode_invalid", request.getLocale().getLanguage(),
						null, null),"err_store_postalcode_invalid"));
			}
		}
		else if (!BBBUtility.isEmpty(getStoreLocator().getCity())
				&& (!BBBUtility.isEmpty(getStoreLocator().getState()) && !getStoreLocator().getState().equals(BBBCoreConstants.MINUS_ONE))) 
		{
			if (!BBBUtility.isValidCity(getStoreLocator().getCity()) || (!BBBUtility.isAlphaOnly(getStoreLocator().getState()))) 
			{
				logError("City or State invalid");
				addFormException(new DropletException(
						getLblTxtTemplateManager().getErrMsg(
								"err_store_city_field_pattren",
								request.getLocale().getLanguage(), null, null),"err_store_city_field_pattren"));
			}
		}
		else if(!BBBUtility.isEmpty(getStoreLocator().getLatitude()) && !BBBUtility.isEmpty(getStoreLocator().getLongitude())) {
		    if(!BBBUtility.isStringLengthValid(getStoreLocator().getLatitude() , 1 , 50) && !BBBUtility.isStringLengthValid(getStoreLocator().getLongitude(), 1 , 50)){
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_store_latitude_longitude_invalid", request.getLocale().getLanguage(),null, null),"err_store_latitude_longitude_invalid"));
		    }
		}
		else {
			logError("err_store_mandatory_fields");
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
					"err_store_mandatory_fields", request.getLocale().getLanguage(),
					null, null),"err_store_mandatory_fields"));
		}

	}

	/**
	 * Get input data and store the details in cookie files
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleSearchStore(DynamoHttpServletRequest request,
			DynamoHttpServletResponse response) throws ServletException,
			IOException {
		return searchStore(request , response , true);
	}
	
	/**This is alternate method of handleSearchStore
	 * Get input data and store the details in cookie files
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	
	public boolean handleSearchStoreWithNoRedirect(DynamoHttpServletRequest request , DynamoHttpServletResponse response)throws ServletException,
	IOException	{
		
		return searchStore(request , response , false);
	}
	
	public boolean searchStore(DynamoHttpServletRequest request , DynamoHttpServletResponse response , boolean flag) throws ServletException,
	IOException	{
		if(getStoreLocator()!=null)
		{
			if(isLoggingDebug()){
				logDebug("handleSearchStore:: Input values \ngetStoreLocator().getAddress(): "+ getStoreLocator().getAddress() + "\ngetStoreLocator().getCity(): " + getStoreLocator().getCity()+"\ngetStoreLocator().getState(): "+getStoreLocator().getState()+"\ngetStoreLocator().getPostalCode(): " + getStoreLocator().getPostalCode() + "\ngetStoreLocator().getSearchType(): " + getStoreLocator().getSearchType() );
			}
		}
		String siteId = SiteContextManager.getCurrentSiteId();
		if (StringUtils.isNotEmpty(getFromPage())) {
			setSearchStoreSuccessURL(request.getContextPath()
					+ getSuccessUrlMap().get(getFromPage()));
			setSearchStoreErrorURL(request.getContextPath()
					+ getErrorUrlMap().get(getFromPage()));

		}
		preSearchStore(request, response);
		if (this.getFormError()) {
			/*
			 * -Changing the functionality for the defect BBB-60
			 * -To prevent IllegalStateException
			 * 
			 * */
			if(!flag){
				return false;
			}
			return checkFormRedirect(null, getSearchStoreErrorURL(), request,
					response);
		} else {
			StringBuilder strBuild = new StringBuilder();
			// Search Based on Zip/Postal Code-- If user mention Zip/Postal Code
			// then priority search will be based on Zip/postal code
			String siteIds = null;
			try {
				siteIds = getSearchStoreManager().getStoreType(getSiteContext());
			}  catch (BBBSystemException bbbSystemException) {
				logError(LogMessageFormatter.formatMessage(request, "err_store_search_tech_error", BBBCoreErrorConstants.ACCOUNT_ERROR_1214 ), bbbSystemException);
				logError("err_store_search_tech_error " + BBBCoreConstants.MAPQUESTSTORETYPE, bbbSystemException);
				addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
						"err_store_search_tech_error", request.getLocale().getLanguage(),
						null, null),"err_store_search_tech_error"));
				if(!flag){
					return false;
				}
				return checkFormRedirect(getSearchStoreSuccessURL(),
						getSearchStoreErrorURL(), request, response);
			} catch (BBBBusinessException bbbBusinessException) {
				logError(LogMessageFormatter.formatMessage(request, "err_store_search_tech_error", BBBCoreErrorConstants.ACCOUNT_ERROR_1215 ), bbbBusinessException);
				addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
						"err_store_search_tech_error", request.getLocale().getLanguage(),
						null, null),"err_store_search_tech_error"));
				if(!flag){
					return false;
				}
				return checkFormRedirect(getSearchStoreSuccessURL(),
						getSearchStoreErrorURL(), request, response);
			}
			if (!BBBUtility.isEmpty(getStoreLocator().getLatitude()) && !BBBUtility.isEmpty(getStoreLocator().getLongitude()) && getSearchBasedOn() != null && getSearchBasedOn().equals(SelfServiceConstants.SEARCH_BASED_ON_USER_LOCATION)){
			    
			    // Search based on user coordinates
			    getStoreLocator().setSearchType(SelfServiceConstants.COORDINATES_BASED_SEARCH);
			    
			    //strBuild.append(BBBCoreConstants.STORE_TYPE + siteIds);
			    //strBuild.append(BBBCoreConstants.AMPERSAND).append(SelfServiceConstants.MAPQUEST_COLUMN_LIST);
			    //strBuild.append(BBBCoreConstants.RADIUSPARAM).append(getStoreLocator().getRadius());
			    //strBuild.append(BBBCoreConstants.ORIGINPARAM).append(getStoreLocator().getLatitude()+"|"+getStoreLocator().getLongitude());
				
				// To display search string above the result grid on jsp
			    setInputSearchString(getStoreLocator().getLatitude()+"|"+getStoreLocator().getLongitude());
			    setMiles(getStoreLocator().getRadius());
			    
			    request.getSession().setAttribute(SelfServiceConstants.LAT, getStoreLocator().getLatitude());
			    request.getSession().setAttribute(SelfServiceConstants.LNG, getStoreLocator().getLongitude());
			    setInputType(SelfServiceConstants.COORDINATES_BASED_SEARCH);

			    
			}else if (!BBBUtility.isEmpty(getStoreLocator().getPostalCode())) {
				getStoreLocator().setSearchType(
						SelfServiceConstants.ZIPCODE_BASED_STORE_SEARCH);
				
				/*strBuild.append(BBBCoreConstants.STORE_TYPE + siteIds);
				strBuild.append(BBBCoreConstants.AMPERSAND).append(
						SelfServiceConstants.MAPQUEST_COLUMN_LIST);
				strBuild.append(BBBCoreConstants.RADIUSPARAM).append(
						getStoreLocator().getRadius());
				strBuild.append(BBBCoreConstants.ORIGINPARAM).append(getStoreLocator().getPostalCode());
*/
				strBuild.append(SelfServiceConstants.LOCATION_EQUALS);
				strBuild.append(getStoreLocator().getPostalCode());
				
				// To display search string above the result grid on jsp
				setInputSearchString(getStoreLocator().getPostalCode());
				setMiles(getStoreLocator().getRadius());
				setInputType(SelfServiceConstants.ZIPCODE_BASED_STORE_SEARCH);
			} else if (!BBBUtility.isEmpty(getStoreLocator().getCity()) && (!BBBUtility.isEmpty(getStoreLocator().getState()) && 
					!getStoreLocator().getState().equals(BBBCoreConstants.MINUS_ONE) && BBBUtility.isValidState(getStoreLocator().getState()))) 
			{
			    	// Search based on Address details
				getStoreLocator().setSearchType(SelfServiceConstants.ADDRESS_BASED_STORE_SEARCH);
				
				/*strBuild.append(BBBCoreConstants.STORE_TYPE + siteIds);
				strBuild.append(BBBCoreConstants.AMPERSAND).append(
						SelfServiceConstants.MAPQUEST_COLUMN_LIST);
				strBuild.append(BBBCoreConstants.RADIUSPARAM).append(
						getStoreLocator().getRadius());*/
				setMiles(getStoreLocator().getRadius());
				
				if (!BBBUtility.isEmpty(getStoreLocator().getAddress()) && BBBUtility.isValidAddressLine1(getStoreLocator().getAddress())) {
					setInputSearchString(getStoreLocator().getAddress() +  BBBCoreConstants.COMMA_WITH_SPACE
							+ getStoreLocator().getCity() + BBBCoreConstants.COMMA_WITH_SPACE
							+ getStoreLocator().getState());
					//strBuild.append(BBBCoreConstants.ORIGINPARAM).append(getInputSearchString());
					strBuild.append(SelfServiceConstants.LOCATION_EQUALS);
					strBuild.append(getStoreLocator().getAddress()).append(BBBCoreConstants.COMMA);
					strBuild.append(getStoreLocator().getCity()).append(BBBCoreConstants.COMMA);
					strBuild.append(getStoreLocator().getState()).append(BBBCoreConstants.COMMA);
					strBuild.append(getStoreLocator().getPostalCode());
					
				} else {
					
					setInputSearchString(getStoreLocator().getCity() + BBBCoreConstants.COMMA_WITH_SPACE
							+ getStoreLocator().getState());
					//strBuild.append(BBBCoreConstants.ORIGINPARAM).append(getInputSearchString());
					strBuild.append(SelfServiceConstants.LOCATION_EQUALS);
					strBuild.append(getStoreLocator().getCity()).append(BBBCoreConstants.COMMA);
					strBuild.append(getStoreLocator().getState()).append(BBBCoreConstants.COMMA);
					strBuild.append(getStoreLocator().getPostalCode());
				}
				setInputType(SelfServiceConstants.ADDRESS_BASED_STORE_SEARCH);
				
			}
			if(isLoggingDebug()){
				logDebug("strBuild: " + strBuild.toString());
				logDebug("getInputType: " + getInputType());
				logDebug("getInputSearchString: " + getInputSearchString());
			}
			
			if (strBuild.toString() != null) {
				
				//setting search attributes in session
				request.getSession().setAttribute(BBBCoreConstants.STATUS, strBuild.toString());
				setMapQuestSearchString(strBuild.toString());
				
			}
			request.getSession().setAttribute(BBBCoreConstants.TYPE, getInputType());
			request.getSession().setAttribute(SelfServiceConstants.STORESEARCHINPUTSTRINGCOOKIENAME, getInputSearchString());
			request.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, getMiles());
			request.getSession().setAttribute(BBBCoreConstants.STORE_TYPE  , siteIds);
			request.getSession().setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE , BBBCoreConstants.FALSE);
			
			this.setFormInitialised(true);
			/*
			 * -Change the functionality for the defect BBB-60
			 * -To prevent IllegalStateException
			 * 
			 * */
			if(!flag){
				return false;
			}
				return checkFormRedirect(getSearchStoreSuccessURL(),
						getSearchStoreErrorURL(), request, response);
		}
		
	}
	public List<AppointmentVO> getAppointmentList() throws Exception {
		return getScheduleAppointmentManager().fetchEnabledAppointmentTypes(SiteContextManager.getCurrentSiteId());			
	}
	
	public int getPreSelectedServiceRef() throws Exception {
		return getScheduleAppointmentManager().fetchPreSelectedServiceRef(this.getDefaultAppointment());			
	}

	public static String formatString(String firstPart, String secondPart) {
		StringBuilder strBuild = new StringBuilder(firstPart);
		strBuild.append(BBBCoreConstants.COMMA);
		strBuild.append(secondPart);
		return strBuild.toString();
	}

	/**
	 * @return the storeLocator
	 */
	public StoreLocator getStoreLocator() {
		if (mStoreLocator == null) {
			mStoreLocator = new StoreLocator();
			return mStoreLocator;
		} else {
			return mStoreLocator;
		}
	}

	/**
	 * @param pStoreLocator
	 *            the storeLocator to set
	 */
	public void setStoreLocator(StoreLocator pStoreLocator) {
		mStoreLocator = pStoreLocator;
	}

	/**
	 * @return the recordsPerPage
	 */
	public String getRecordsPerPage() {
		return mRecordsPerPage;
	}

	/**
	 * @param pRecordsPerPage
	 *            the recordsPerPage to set
	 */
	public void setRecordsPerPage(String pRecordsPerPage) {
		mRecordsPerPage = pRecordsPerPage;
	}

	/**
	 * @return the mapQuestSearchString
	 */
	public String getMapQuestSearchString() {
		return mMapQuestSearchString;
	}

	/**
	 * @param pMapQuestSearchString
	 *            the mapQuestSearchString to set
	 */
	public void setMapQuestSearchString(String pMapQuestSearchString) {
		mMapQuestSearchString = pMapQuestSearchString;
	}

	/**
	 * @return the searchStoreSuccessURL
	 */
	public String getSearchStoreSuccessURL() {
		return mSearchStoreSuccessURL;
	}

	/**
	 * @param searchStoreSuccessURL
	 *            the searchStoreSuccessURL to set
	 */
	public void setSearchStoreSuccessURL(String searchStoreSuccessURL) {
		mSearchStoreSuccessURL = searchStoreSuccessURL;
	}

	/**
	 * @return the searchStoreErrorURL
	 */
	public String getSearchStoreErrorURL() {
		return mSearchStoreErrorURL;
	}

	/**
	 * @param searchStoreErrorURL
	 *            the searchStoreErrorURL to set
	 */
	public void setSearchStoreErrorURL(String searchStoreErrorURL) {
		mSearchStoreErrorURL = searchStoreErrorURL;
	}

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
	 * @return the inputSearchString
	 */
	public String getInputSearchString() {
		return mInputSearchString;
	}

	/**
	 * @param pInputSearchString
	 *            the inputSearchString to set
	 */
	public void setInputSearchString(String pInputSearchString) {
		mInputSearchString = pInputSearchString;
	}
	
	/**
	 * @return the inputSearchType
	 */
	public String getInputType() {
	    return mInputType;
	}
	
	/**
	 * @param mInputType
	 *            the inputSearchType to set
	 */
	public void setInputType(String mInputType) {
	    this.mInputType = mInputType;
	}
	
	/**
	 * @return the searchBasedOn
	 */
	public String getSearchBasedOn() {
	    return mSearchBasedOn;
	}
	/**
	 * @param mSearchBasedOn
	 *            the searchBasedOn to set
	 */
	public void setSearchBasedOn(String mSearchBasedOn) {
	    this.mSearchBasedOn = mSearchBasedOn;
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
	}
*/
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
	 * @return the cookieAge
	 */
	public int getCookieAge() {
		return mCookieAge;
	}

	/**
	 * @param pCookieAge the cookieAge to set
	 */
	public void setCookieAge(int pCookieAge) {
		mCookieAge = pCookieAge;
	}
	
	/**
	 * This handler method handles the updation of user's profile for the 
	 * current site 
	 * 
	 * @param request
	 * @param response
	 * @return true/false
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleModifyFavStore(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
			if(isLoggingDebug()){
				logDebug("SearchStoreFormHandler.handleModifyFavStore() method started");
			}
		String favStoreId = pRequest.getParameter(BBBCoreConstants.FAVOURITE_STORE_ID);
		final Integer currentSecurityCode = (Integer) this.getProfile().getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS);
		mSearchStoreSuccessURL = pRequest.getRequestURI();
		Transaction pTransaction = null;
		boolean status = false;
		boolean isError = false;
		//commenting the code for defect id PS-51527..As per the defect this functionality should work for recognized user
	//	if (!COOKIE_LOGIN_SECURITY_STATUS.equals(currentSecurityCode)) {
			try {
				pTransaction = this.ensureTransaction();
				status = mManager.updateSiteItem(mProfile,
						SiteContextManager.getCurrentSiteId(), favStoreId,
						BBBCoreConstants.FAVOURITE_STORE_ID);
			} catch (RepositoryException e) {
				isError = true;
				logError(
						LogMessageFormatter.formatMessage(
								null,
								"RepositoryException : SearchStoreFormHandler.handleModifyFavStore while updating the user Profile",
								BBBCoreErrorConstants.ACCOUNT_ERROR_1159), e);
			} catch (NotSupportedException exc) {
				logError(
						LogMessageFormatter.formatMessage(
								null,
								"NotSupportedException in SearchStoreFormHandler.handleModifyFavStore",
								BBBCoreErrorConstants.ACCOUNT_ERROR_1237), exc);
			} catch (SystemException exc) {
				logError(
						LogMessageFormatter.formatMessage(
								null,
								"SystemException in in SearchStoreFormHandler.handleModifyFavStore",
								BBBCoreErrorConstants.ACCOUNT_ERROR_1238), exc);
			} finally {
				endTransaction(isError, pTransaction);
			}
		//}
		if (isLoggingInfo()) {
			logInfo("User Profile favStore update status = " + status
					+ " for site " + SiteContextManager.getCurrentSiteId());
	}
		logDebug("SearchStoreFormHandler.handleModifyFavStore() method end.");
		return checkFormRedirect(mSearchStoreSuccessURL, null, pRequest,
				pResponse);
	}
	
	/**
	 * This method handles is used for FavStoreAPI in user's profile for the 
	 * current site 
	 * 
	 * @param request
	 * @param response
	 * @return true/false
	 * @throws ServletException
	 * @throws IOException
	 */
	
	private String mFavStoreId = null;
	
	

	public String getFavStoreId() {
		return mFavStoreId;
	}

	public void setFavStoreId(String mFavStoreId) {
		this.mFavStoreId = mFavStoreId;
	}
	

	public boolean handleSetFavStoreId(DynamoHttpServletRequest pRequest,
	DynamoHttpServletResponse pResponse) throws ServletException,
	IOException {
			if(isLoggingDebug()){
				logDebug("SearchStoreFormHandler.handleSetFavStoreId() method started");
			}
	
		boolean status = false;	
		boolean isError = false;
		Transaction pTransaction = null;
		if( getFavStoreId() != null && !"".equals( getFavStoreId())){
			try {
				pTransaction = this.ensureTransaction();
				status = mManager.updateSiteItem(mProfile,
						SiteContextManager.getCurrentSiteId(), getFavStoreId(),
						BBBCoreConstants.FAVOURITE_STORE_ID);
			} catch (NotSupportedException exc) {
				logError(
						LogMessageFormatter.formatMessage(
								null,
								"NotSupportedException in SearchStoreFormHandler.handleSetFavStoreId",
								BBBCoreErrorConstants.ACCOUNT_ERROR_1237), exc);
			} catch (SystemException exc) {
				logError(
						LogMessageFormatter.formatMessage(
								null,
								"SystemException in in SearchStoreFormHandler.handleSetFavStoreId",
								BBBCoreErrorConstants.ACCOUNT_ERROR_1238), exc);
			} catch (RepositoryException exc) {
				isError = true;
				logError(
						LogMessageFormatter.formatMessage(
								null,
								"RepositoryException : SearchStoreFormHandler.handleSetFavStoreId while updating the user Profile",
								BBBCoreErrorConstants.ACCOUNT_ERROR_1159), exc);
			} finally {
				endTransaction(isError, pTransaction);
			}
		}else{
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg(SelfServiceConstants.ERROR_FAVORITE_STORE,
							pRequest.getLocale().getLanguage(), null, null),
					SelfServiceConstants.ERROR_FAVORITE_STORE));
			
		}
			
		    
					
		if(isLoggingDebug()){
			logDebug("User Profile favStore update status = " + status
					 + " for site " + SiteContextManager.getCurrentSiteId());
			logDebug("SearchStoreFormHandler.handleSetFavStoreId() method end.");
		}
		
        return status;
	}

	public ScheduleAppointmentManager getScheduleAppointmentManager() {
		return mScheduleAppointmentManager;
	}

	public void setScheduleAppointmentManager(
			ScheduleAppointmentManager pScheduleAppointmentManager) {
		mScheduleAppointmentManager = pScheduleAppointmentManager;
	}

	public String getDefaultAppointment() {
		return mDefaultAppointment;
	}

	public void setDefaultAppointment(String pDefaultAppointment) {
		mDefaultAppointment = pDefaultAppointment;
	}
	
	/**
	 * This method ensures that a transaction exists before returning. If there
	 * is no transaction, a new one is started and returned. In this case, you
	 * must call commitTransaction when the transaction completes.
	 * 
	 * @return a <code>Transaction</code> value
	 * @throws NotSupportedException 
	 * @throws SystemException 
	 */
	protected Transaction ensureTransaction() throws NotSupportedException,
			SystemException {
		try {
			TransactionManager tm = getTransactionManager();
			Transaction t = tm.getTransaction();
			if (t == null) {
				tm.begin();
				t = tm.getTransaction();
				return t;
			}
			return null;
		} catch (NotSupportedException exc) {
			throw new NotSupportedException(
					"NotSupportedException in SearchStoreFormHandler.ensureTransaction");
		} catch (SystemException exc) {
			throw new SystemException(
					"SystemException in in SearchStoreFormHandler.ensureTransaction");
		}
	}
	
	/**
	 * This method ensures that the commit/rollback performed on transaction if
	 * it was returned from database operations.
	 * 
	 * @param isError
	 *            flag that represents error status
	 * @param pTransaction
	 *            transaction object
	 */
	private void endTransaction(final boolean isError,
			final Transaction pTransaction) {
		try {
			if (isError) {
				if (pTransaction != null) {
					pTransaction.rollback();
				}
			} else {
				if (pTransaction != null) {
					pTransaction.commit();
				}
			}
		} catch (SecurityException e) {
			logError(e);
		} catch (IllegalStateException e) {
			logError(e);
		} catch (RollbackException e) {
			logError(e);
		} catch (HeuristicMixedException e) {
			logError(e);
		} catch (HeuristicRollbackException e) {
			logError(e);
		} catch (SystemException e) {
			logError(e);
		}
	}

	/**
	 * @return the formInitialised
	 */
	public boolean isFormInitialised() {
		return formInitialised;
	}

	/**
	 * @param formInitialised the formInitialised to set
	 */
	public void setFormInitialised(boolean formInitialised) {
		this.formInitialised = formInitialised;
	}

}
