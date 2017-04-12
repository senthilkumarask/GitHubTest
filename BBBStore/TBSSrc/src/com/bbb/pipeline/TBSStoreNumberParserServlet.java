
package com.bbb.pipeline;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.pipeline.InsertableServletImpl;

import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.tbs.selfservice.manager.TBSSearchStoreManager;


/**
 * Servlet to pick up the store number and set it in the session
 * 1. If it is a desktop, then pickup the store number from cookie
 * 2. If tablet, then pick it up from the url
 * 
 * If there are no store number set in session, then open the modal to capture it.
 * 
 */
public class TBSStoreNumberParserServlet extends InsertableServletImpl {

	private List<String> mTabletAgents;
	private String mHeaderParameter;
	private boolean mEnabled = false;
	private String mDefaultStoreNumber;
	private String storeNumberFromParam = null;
	private String storeNumberFromCookie = null;

	/**
	 * mSearchStoreManager
	 */
	private TBSSearchStoreManager mSearchStoreManager;

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
	
	/**
	 * @param pRequest
	 * @param pResponse
	 */
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws IOException, ServletException {
		if(isEnabled()) {
			storeNumberFromParam = pRequest.getParameter(TBSConstants.STORE_NUMBER);
			getStoreNumber(storeNumberFromParam,pRequest,pResponse);
		}
		passRequest(pRequest, pResponse);
	}
	
	
	/**
	 * 
	 * @param pStoreNumberFromParam
	 * @param pRequest
	 * @param pResponse
	 * @throws IOException 
	 * @throws ServletException 
	 */
	private void getStoreNumber(String pStoreNumberFromParam, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		try {
			if(!pRequest.getRequestURI().contains("init-session")){
				mDefaultStoreNumber=getSearchStoreManager().getDefaultStoreType(SiteContextManager.getCurrentSiteId());
			}
		}  catch (BBBSystemException bbbSystemException) {
			logError(LogMessageFormatter.formatMessage(pRequest, "err_default_store_tech_error", BBBCoreErrorConstants.ACCOUNT_ERROR_1194 ), bbbSystemException);
			pRequest.setParameter("errorMessage", bbbSystemException.getErrorCode() + bbbSystemException.getMessage());
			pRequest.serviceLocalParameter("error", pRequest, pResponse);
			return;
		} catch (BBBBusinessException bbbBusinessException) {
			logError(LogMessageFormatter.formatMessage(pRequest, "err_default_store_tech_error", BBBCoreErrorConstants.ACCOUNT_ERROR_1195 ), bbbBusinessException);
			pRequest.setParameter("errorMessage", bbbBusinessException.getErrorCode() + bbbBusinessException.getMessage());
			pRequest.serviceLocalParameter("error", pRequest, pResponse);
			return;
		}
		if(!StringUtils.isBlank(storeNumberFromParam)){
			//valid store
			boolean isValidStoreId = getSearchStoreManager().isValidStore(storeNumberFromParam);
			vlogDebug("Is Valid Store returned"+isValidStoreId); 
			if(isValidStoreId){
			// Create or update cookie
			addCookie(pResponse, storeNumberFromParam);
		    pRequest.getSession().setAttribute(TBSConstants.STORE_NUMBER_LOWER, storeNumberFromParam);
			}else{
				addCookie(pResponse, mDefaultStoreNumber);
			    pRequest.getSession().setAttribute(TBSConstants.STORE_NUMBER_LOWER, mDefaultStoreNumber);	
			}
		} else {
			storeNumberFromCookie = getCookie(pRequest);
			vlogDebug ("storeNumberFromCookie=="+storeNumberFromCookie);
			if (!StringUtils.isBlank(storeNumberFromCookie)){
				pRequest.getSession().setAttribute(TBSConstants.STORE_NUMBER_LOWER,storeNumberFromCookie);
			} else if(pRequest.getSession().getAttribute(TBSConstants.STORE_NUMBER_LOWER) == null){
				//if store number is not there in Request, cookie and session then chek the IP range
				String systemIpRange = null;
				RepositoryItem storeItem = null;
				String storeNumfromRepository = null;
				String ipAddress = pRequest.getRemoteAddr();
				vlogDebug("IP Address :: "+ ipAddress);
				
				if(!StringUtils.isBlank(ipAddress)){
					systemIpRange = ipAddress.substring(0, ipAddress.lastIndexOf(".")+1);
					storeItem = getSearchStoreManager().getStoreItem(systemIpRange);
				} 
				if(storeItem != null){
					storeNumfromRepository = storeItem.getRepositoryId();
				}
				if(!StringUtils.isBlank(storeNumfromRepository)){
					vlogDebug ("storeNumber using IP Range =="+storeNumfromRepository);
					addCookie(pResponse, storeNumfromRepository);
					pRequest.getSession().setAttribute(TBSConstants.STORE_NUMBER_LOWER, storeNumfromRepository);
				}
				//if the store number is not there in repository, then get the config store number as 990
				if(StringUtils.isBlank(storeNumfromRepository)){
					vlogDebug ("storeNumber using IP Range not found");
					addCookie(pResponse, mDefaultStoreNumber);
					pRequest.getSession().setAttribute(TBSConstants.STORE_NUMBER_LOWER, mDefaultStoreNumber);
				}
			}
			vlogDebug ("StoreNumber in session=="+pRequest.getSession().getAttribute(TBSConstants.STORE_NUMBER_LOWER));
		}
		
	}

	/**
     * This method is used to return the tablet agents
     * @return mTabletAgents
     */
	public List<String> getTabletAgents() {
		return mTabletAgents;
	}
	/**
     * This method is used to set the tablet agents
     * @param pTabletAgents - List of agents
     */
	public void setTabletAgents(List<String> pTabletAgents) {
		mTabletAgents = pTabletAgents;
	}
	/**
     * This method is used to return the header parameter
     * @return mHeaderParameter
     */
	public String getHeaderParameter() {
		return mHeaderParameter;
	}
	/**
     * This method is used to set the header parameter
     * @param pHeaderParameter
     */
	public void setHeaderParameter(String pHeaderParameter) {
		mHeaderParameter = pHeaderParameter;
	}
	
	/**
	 * - if mHeaderParameter is not null and its value contains a string from list mTabletAgents - return true;
	 * - if all checks fail - return false.
	 * @param pRequest
	 * @return
	 */
	private boolean isTabletDevice(DynamoHttpServletRequest pRequest) {
		if (!StringUtils.isBlank(mHeaderParameter)) {
			String userAgent = pRequest.getHeader(mHeaderParameter);
			if (isLoggingDebug()) logDebug ("userAgent=="+userAgent);
			if (userAgent != null) {
				userAgent = userAgent.toLowerCase(pRequest.getLocale());
				for (String agent : mTabletAgents) {
					if (userAgent.contains(agent)) {
						return true;
					}
				}
			}
		}

		return false;
	}
	
	private String getCookie(HttpServletRequest request) {
		Cookie cookies[] = request.getCookies();
		String cookieValue = null;
		if (cookies != null)
			for (int i = 0; i < cookies.length; i++) {
				String name = cookies[i].getName();
				if (TBSConstants.STORE_NUMBER_COOKIE.equals(name)) {
					cookieValue = cookies[i].getValue();
					break;
				}
			}
		return cookieValue;
	}

	private void addCookie(DynamoHttpServletResponse pResponse, String pStoreNum){
		vlogDebug ("TBSStoreNumberParserServlet :: addCookie() :: START with store Number == "+pStoreNum);
		Cookie storeNumberCookie = new Cookie(TBSConstants.STORE_NUMBER_COOKIE,pStoreNum);
	    // Set expiry date of 30 days for the cookie.
		storeNumberCookie.setMaxAge(60*60*24*30);
		storeNumberCookie.setPath("/"); 
	    // Add the cookie in the response header.
	    pResponse.addCookie(storeNumberCookie);
	    vlogDebug ("TBSStoreNumberParserServlet :: addCookie() :: END");
	}
	
	
	public boolean isEnabled() {
		return mEnabled;
	}

	public void setEnabled(boolean pEnabled) {
		mEnabled = pEnabled;
	}
	
}
