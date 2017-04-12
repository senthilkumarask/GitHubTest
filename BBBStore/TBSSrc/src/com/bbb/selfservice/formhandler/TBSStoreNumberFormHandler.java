package com.bbb.selfservice.formhandler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import atg.droplet.DropletException;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.common.BBBGenericFormHandler;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.tbs.selfservice.manager.TBSSearchStoreManager;
import com.bbb.utils.BBBUtility;

public class TBSStoreNumberFormHandler extends BBBGenericFormHandler {
	
	private String mSuccessURL="";
	private String mErrorURL="";
	private LblTxtTemplateManager mLblTxtTemplateManager;
	private String mStoreNumber;
	/**
	 * Property to hold TBSSearchStoreManager reference.
	 */
	private TBSSearchStoreManager mSearchStoreManager;
	
	private Map storeSiteMap;
	
	private String mStoreUrlParam;
	
	/**
	 * @return the mSuccessURL
	 */
	public String getSuccessURL() {
		return mSuccessURL;
	}
	/**
	 * @param pSuccessURL the mSuccessURL to set
	 */
	public void setSuccessURL(String pSuccessURL) {
		this.mSuccessURL = pSuccessURL;
	}
	/**
	 * @return the mErrorURL
	 */
	public String getErrorURL() {
		return mErrorURL;
	}
	public Map getStoreSiteMap() {
		return storeSiteMap;
	}
	public void setStoreSiteMap(Map storeSiteMap) {
		this.storeSiteMap = storeSiteMap;
	}
	public String getStoreUrlParam() {
		return mStoreUrlParam;
	}
	public void setStoreUrlParam(String pStoreUrlParam) {
		this.mStoreUrlParam = pStoreUrlParam;
	}
	/**
	 * @param pErrorURL the mErrorURL to set
	 */
	public void setErrorURL(String pErrorURL) {
		this.mErrorURL = pErrorURL;
	}
	/**
	 * @return the lblTxtTemplateManager
	 */
	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return mLblTxtTemplateManager;
	}

	/**
	 * @param pLblTxtTemplateManager - the lblTxtTemplateManager to set
	 */
	public void setLblTxtTemplateManager(LblTxtTemplateManager pLblTxtTemplateManager) {
		mLblTxtTemplateManager = pLblTxtTemplateManager;
	}
	/**
	 * @return the mStoreNumber
	 */
	public String getStoreNumber() {
		return mStoreNumber;
	}
	/**
	 * @param pStoreNumber the mStoreNumber to set
	 */
	public void setStoreNumber(String pStoreNumber) {
		this.mStoreNumber = pStoreNumber;
	}
	/**
	 * @return the searchStoreManager
	 */
	public TBSSearchStoreManager getSearchStoreManager() {
		return mSearchStoreManager;
	}
	/**
	 * @param pSearchStoreManager the searchStoreManager to set
	 */
	public void setSearchStoreManager(TBSSearchStoreManager pSearchStoreManager) {
		mSearchStoreManager = pSearchStoreManager;
	}

	private boolean mValidationRequired = false;
	
	/**
	 * @return mValidationRequired
	 */
	public boolean isValidationRequired() {
		return mValidationRequired;
	}
	
	/**
	 * @param pValidationRequired the mValidationRequired to set
	 */
	public void setValidationRequired(boolean pValidationRequired) {
		this.mValidationRequired = pValidationRequired;
	}

	/**
	 * This method is customized to validate the different form fields to match
	 * the business rules
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param errorPlaceHolderMap
	 *            Hash Map to store error message key
	 * 
	 */
	private void validateRequestInfo(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse,
			Map<String, String> errorPlaceHolderMap) {
		String errorMessage;

			logDebug("StoreNumberFormHandler.validateRequestInfo() method started");

		if (BBBUtility.isEmpty(mStoreNumber)) {
			errorPlaceHolderMap.put("fieldName", "Store Number");
			errorMessage = getLblTxtTemplateManager().getErrMsg(
					BBBCoreErrorConstants.ERROR_STORE_NUMBER_EMPTY, pRequest.getLocale().getLanguage(),
					errorPlaceHolderMap, null);
			addFormException(new DropletException(errorMessage,BBBCoreErrorConstants.ERROR_STORE_NUMBER_EMPTY));
		}
		logDebug("StoreNumberFormHandler.validateRequestInfo() method ends");
	}
	/**
	 * This method will update the store number in the session bean
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return boolean
	 */
	public boolean handleUpdate(DynamoHttpServletRequest pRequest,DynamoHttpServletResponse pResponse) throws ServletException,IOException {

		if (isLoggingDebug()) logDebug("StoreNumberFormHandler.handleUpdate() method started");
		Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
		validateRequestInfo(pRequest, pResponse, errorPlaceHolderMap);
		if (isLoggingDebug()) logDebug ("Store number:"+getStoreNumber());
		//Validate if the store number entered exists in the repository
		StoreDetails store = null;
		try {
			store = getSearchStoreManager().findStoreById(getStoreNumber());
		} catch (RepositoryException e) {
			TBSStoreNumberStatusVO statusVO = new TBSStoreNumberStatusVO("error",e.getMessage(),getStoreNumber(),"");
			pRequest.getSession().setAttribute("statusVO",statusVO);
			if (isLoggingError()) logError("RepositoryException when validating store number:" + e);
		} catch (SQLException e) {
			TBSStoreNumberStatusVO statusVO = new TBSStoreNumberStatusVO("error",e.getMessage(),getStoreNumber(),"");
			pRequest.getSession().setAttribute("statusVO",statusVO);
			if (isLoggingError()) logError("SQLException when validating store number:" + e);
		}
		if (store != null){		
			TBSStoreNumberStatusVO statusVO = new TBSStoreNumberStatusVO("success","",getStoreNumber(),"");
			if(null!= store.getStoreType()){
				String redireUrl = null;
				if(!getSiteId().equals(store.getStoreType())){
					if (!isValidationRequired()) {
						if (store.getStoreType().equals(TBSConstants.BED_BATH_CA)) {
							redireUrl = (String) getStoreSiteMap().get(TBSConstants.BED_BATH_CA)+ getStoreUrlParam().trim()+ store.getStoreId();
						} else if (store.getStoreType().equals(TBSConstants.BUY_BUY_BABY)) {
							redireUrl = (String) getStoreSiteMap().get(TBSConstants.BUY_BUY_BABY)+ getStoreUrlParam().trim()+ store.getStoreId();
						} else {
							redireUrl = (String) getStoreSiteMap().get(TBSConstants.BED_BATH_US)+ getStoreUrlParam().trim()+ store.getStoreId();
						}
						statusVO.setRedirectUrl(redireUrl);
					} else {
						if (isLoggingDebug()) logDebug("StoreNumberFormHandler.handleUpdate() call  with validationRequired flag true. Since store number : "+getStoreNumber()+", belong to different site, hence not updating it.");
						statusVO = new TBSStoreNumberStatusVO("differentSite","Store number belongs to different concept",getStoreNumber(),"");
						pRequest.getSession().setAttribute("statusVO", statusVO);
						return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
					}
				}
			}
			
			if (isLoggingDebug()) logDebug("store number added into session:"+getStoreNumber());
			// Create or update cookie
			Cookie storeNumberCookie = new Cookie("store_number",getStoreNumber());
		    // Set expiry date of 30 days for the cookie.
			storeNumberCookie.setMaxAge(60*60*24*30); 
			storeNumberCookie.setPath("/"); 
		    // Add the cookie in the response header.
		    pResponse.addCookie( storeNumberCookie );
			if (isLoggingDebug()) logDebug("Cookie created for store number:"+getStoreNumber());

		 	pRequest.getSession().setAttribute("statusVO",statusVO);
			
		} else {
			TBSStoreNumberStatusVO statusVO = new TBSStoreNumberStatusVO("error","Invalid store number",getStoreNumber(),"");
			pRequest.getSession().setAttribute("statusVO",statusVO);
		}
		if (isLoggingDebug()) logDebug("StoreNumberFormHandler.handleUpdate() method ends");

			return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
		}
	
	
	  public final String getSiteId() {
	        String siteId= SiteContextManager.getCurrentSiteId();
	        if( siteId.startsWith("TBS") ) {
	        	siteId = siteId.substring(4);
			}
	        return siteId;
	    }
	  
	  /**
	   * This method used to validate the store number on placing order.
	   * 
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleValidateStoreNumber(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		if (isLoggingDebug())
			logDebug("StoreNumberFormHandler.handleValidateStoreNumber() method start");
		TBSStoreNumberStatusVO statusVO = null;
		String message = null;
		String status = null;
		StoreDetails store = null;
		HttpSession session = pRequest.getSession();
		String storeNumber = (String) session
				.getAttribute(TBSConstants.STORE_NUMBER_LOWER);
		setStoreNumber(storeNumber);
		if (getStoreNumber().isEmpty()) {
			status = "error";
			message = "No store number found. Please enter your store number";
		} else {
			try {
				store = getSearchStoreManager().findStoreById(getStoreNumber());
			} catch (RepositoryException e) {
				if (isLoggingError())
					logError("RepositoryException when validating store number:"
							+ e);
			} catch (SQLException e) {
				if (isLoggingError())
					logError("SQLException when validating store number:" + e);
			}
			if (store != null && store.getStoreType() != null) {
				if (!getSiteId().equals(store.getStoreType())) {
					status = "differentSite";
					message = "Store number belongs to different concept";
					if (isLoggingDebug())
						logDebug("storeNumber : " + getStoreNumber()
								+ " belong to the different site : "
								+ store.getStoreType());
				} else {
					status = "success";
					message = "valid store number";
					if (isLoggingDebug())
						logDebug("storeNumber : " + getStoreNumber()
								+ " belong to the current site : "
								+ getSiteId());
				}
			} else {
				status = "error";
				message = "Invalid store number";
				if (isLoggingDebug())
					logDebug("Unable to find a store with storeNumber : "
							+ getStoreNumber());
			}
		}
		statusVO = new TBSStoreNumberStatusVO(status, message,
				getStoreNumber(), "");
		pRequest.getSession().setAttribute("statusVO", statusVO);
		if (isLoggingDebug())
			logDebug("StoreNumberFormHandler.handleValidateStoreNumber() method ends");
		return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest,
				pResponse);
	}

}
