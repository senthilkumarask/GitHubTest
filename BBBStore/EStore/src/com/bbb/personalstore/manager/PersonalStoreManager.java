package com.bbb.personalstore.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import atg.commerce.order.CommerceItem;
import atg.core.util.StringUtils;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.personalstore.tools.PersonalStoreTools;
import com.bbb.personalstore.vo.PersonalStoreResponseVO;
import com.bbb.personalstore.vo.StrategyResponseVO;
import com.bbb.utils.BBBUtility;

/**
 * This is a manager for personal Store, which contains methods to retrieve the
 * strategy details from Tools class and filter out the strategies based on
 * customer type.This also includes method 
 * 
 * @author rjain40
 * 
 */

public class PersonalStoreManager extends BBBGenericService {

	private PersonalStoreTools mPSTools = null;
	private BBBCatalogTools mCatalogTools;
	private int mMaxCatInCookie;
	private String mSearchDimConfig;
	private String maxAgeLSItmCookie;
	private String mMaxAge;
	private String mLvCookieNme;
	private int mLvCookieMaxAge;
	private String mLbCookieNme;
	private int mLbCookieMaxAge;
	private int mLvMaxItms;

	/**
	 * @return the mLVMaxItms
	 */
	public int getLvMaxItms() {
		return mLvMaxItms;
	}

	/**
	 * @param mLVMaxItms
	 *            the mLVMaxItms to set
	 */
	public void setLvMaxItms(final int mLVMaxItms) {
		this.mLvMaxItms = mLVMaxItms;
	}

	/**
	 * @return the mLVCookieNme
	 */
	public String getLvCookieNme() {
		return mLvCookieNme;
	}

	/**
	 * @param mLVCookieNme
	 *            the mLVCookieNme to set
	 */
	public void setLvCookieNme(final String mLVCookieNme) {
		this.mLvCookieNme = mLVCookieNme;
	}

	/**
	 * @return the mLVCookieMaxAge
	 */
	public int getLvCookieMaxAge() {
		return mLvCookieMaxAge;
	}

	/**
	 * @param mLVCookieMaxAge
	 *            the mLVCookieMaxAge to set
	 */
	public void setLvCookieMaxAge(final int mLVCookieMaxAge) {
		this.mLvCookieMaxAge = mLVCookieMaxAge;
	}

	/**
	 * @return the mLBCookieNme
	 */
	public String getLbCookieNme() {
		return mLbCookieNme;
	}

	/**
	 * @param mLBCookieNme
	 *            the mLBCookieNme to set
	 */
	public void setLbCookieNme(final String mLBCookieNme) {
		this.mLbCookieNme = mLBCookieNme;
	}

	/**
	 * @return the mLBCookieMaxAge
	 */
	public int getLbCookieMaxAge() {
		return mLbCookieMaxAge;
	}

	/**
	 * @param mLBCookieMaxAge
	 *            the mLBCookieMaxAge to set
	 */
	public void setLbCookieMaxAge(final int mLBCookieMaxAge) {
		this.mLbCookieMaxAge = mLBCookieMaxAge;
	}

	/**
	 * This method returns <code>PersonalStoreTools</code> contains name of the
	 * Tools class to call the repository
	 * 
	 * @return the mPSTools in <code>PersonalStoreTools</code> format
	 */
	public PersonalStoreTools getPersonalStoreTools() {
		return mPSTools;
	}

	/**
	 * This method sets the Personal Store Tools class from component properties
	 * file
	 * 
	 * @param mPSTools
	 *            the PersonalStoreTools to set
	 */
	public void setPersonalStoreTools(final PersonalStoreTools mPSTools) {
		this.mPSTools = mPSTools;
	}

	/**
	 * This method generates the last viewed cookie from the product Last
	 * viewed. The limit of number of products is set in the component. Also
	 * this checks if the product Id is in Last bought Cookie, hen do not append
	 * this product in the LV cookie
	 * 
	 * @param pProductId
	 *            Product Id last viewed
	 * @param pLVCookie
	 *            Last Viewed cookie if this already exists
	 * @param pLVCookieValLst
	 *            Last Viewed cookie value if this already exists
	 * @param pLBCookie
	 *            Last Bought Cookie
	 * @param pRequest
	 *            - DynamoHttpServletRequest
	 * @param pResponse
	 *            - DynamoHttpServletResponse
	 * @return Last Viewed Cookie in <code>Cookie</code> format
	 * @throws None
	 */
	public Cookie getLastViewedCookie(final String pProductId, Cookie pLVCookie, final Cookie pLBCookie,
			final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) {

		BBBPerformanceMonitor.start(PersonalStoreManager.class.getName() + " : " + "getLastViewedCookie");
		logDebug("PersonalStoreManager.getLastViewedCookie() - start with " + "product Id : " + pProductId);
		
		boolean isExists = false;
		// Check if the Last bought Items contains the product ID
		// Add the product only if the product does not exist in Last bought
		// Cookie
		if (null != pLBCookie && !BBBUtility.isEmpty(pLBCookie.getValue()) && pLBCookie.getValue().startsWith(pProductId)) {
			isExists = true;
			logDebug("PersonalStoreManager.getLastViewedCookie() - end with Last Bought Cookie value :: " + pLBCookie.getValue());
		}

		if (!isExists) {
			List<String> lVCookieValLst = null;
			if (null != pLVCookie) {
				lVCookieValLst = getSplitCookieList(pLVCookie);
			}
			String cookieValue = null;
			// if no last viewed product list exists
			// create new list and add product id in it
			if (BBBUtility.isListEmpty(lVCookieValLst)) {
				logDebug("PersonalStoreManager.getLastViewedCookie():: No product already exists. So adding productId " + pProductId + " in new list");
				cookieValue = pProductId;
			} else {
				if (null!=lVCookieValLst && lVCookieValLst.contains(pProductId)) {
					// if product id already exists remove and again add to
					// update its position in the list
					lVCookieValLst.remove(pProductId);
					logDebug("PersonalStoreManager.getLastViewedCookie() :: productId " + pProductId + " already exists in last viewed list");
				}
				lVCookieValLst.add(pProductId);
				logDebug("PersonalStoreManager.getLastViewedCookie() :: adding productId " + pProductId + " in already existing list");
				if (lVCookieValLst.size() >= getLvMaxItms()) {
					final int listSize = lVCookieValLst.size();
					final int indxLVPrdt = listSize - getLvMaxItms();
					final StringBuffer debug = new StringBuffer(50);
					logDebug(debug.append("PersonalStoreManager.getLastViewedCookie() :: no of last viewed has exceeded limit of  ").append(getLvMaxItms()).
							append(" adding sublist of last viewed items from starting index ").append(indxLVPrdt).append(" to last index ").append(listSize - 1).
							append(" value in last viewed items is ").append(lVCookieValLst.subList(indxLVPrdt, listSize)).toString());
					cookieValue = getCookieValueFromLst(lVCookieValLst.subList(indxLVPrdt, listSize));

				} else {
					logDebug("PersonalStoreManager.getLastViewedCookie() :: no of last viewed is within limit adding all items");
					cookieValue = getCookieValueFromLst(lVCookieValLst);
				}
			}

			// Get max cookie age from config key
			List<String> configValues = null;
			try {
				configValues = getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.MAX_AGE_LV_COOKIE);
			} catch (BBBBusinessException bbbbEx) {
				logError(BBBCoreErrorConstants.BROWSE_ERROR_1045 + " PersonalStoreManager.getLVCookie():: " 
						+ "Business Exception occurred while fetching configValue for configKey maxAgeLastViewedCookie", bbbbEx);
			} catch (BBBSystemException bbbsEx) {
				logError(BBBCoreErrorConstants.BROWSE_ERROR_1046 + " PersonalStoreManager.getLVCookie():: " 
						+ "System Exception occurred while fetching configValue for configKey maxAgeLastViewedCookie ", bbbsEx);
			}

			int maxAge = 0;
			if (BBBUtility.isListEmpty(configValues)) {
				maxAge = getLvCookieMaxAge();
			} else {
				maxAge = Integer.parseInt(configValues.get(0));
			}

			pLVCookie = createCookie(pLVCookie, cookieValue, maxAge, getLvCookieNme(), pRequest);
			logDebug("PersonalStoreManager.getLastViewedCookie() - end with Last Viewed cookie value :: " + pLVCookie.getValue());
		}
		
		BBBPerformanceMonitor.end(PersonalStoreManager.class.getName() + " : " + "getLastViewedCookie");
		return pLVCookie;

	}

	/**
	 * The method converts the list into a String of Product Ids delimited by :
	 * to be stored as value in cookie
	 * 
	 * @param lvPrdtList
	 * @return Cookie Value in <code>String</code> format
	 */
	private String getCookieValueFromLst(final List<String> pLvPrdtList) {
		final StringBuffer cookieValue = new StringBuffer("");
		if (pLvPrdtList != null && !pLvPrdtList.isEmpty()) {
			for (final String prdtId : pLvPrdtList) {
				cookieValue.append(prdtId).append(BBBCoreConstants.COLON);
			}
			cookieValue.deleteCharAt(cookieValue.lastIndexOf(BBBCoreConstants.COLON));
		}
		return cookieValue.toString();
	}

	/**
	 * The value in cookie is a String of last viewed product ids delimited by :
	 * The method converts the String into list of product Ids to be added
	 * 
	 * @param lastViewedCookie
	 * @return List of Product Ids in <code>List<String></code> format
	 */
	public List<String> getSplitCookieList(final Cookie pLastViewedCookie) {
		final String lvListFrmCookie = pLastViewedCookie.getValue();
		if (!StringUtils.isEmpty(lvListFrmCookie)) {
			final String[] lvPrdts = lvListFrmCookie.split(BBBCoreConstants.COLON);

			return new ArrayList<String>(Arrays.asList(lvPrdts));
		}
		return null;

	}

	/**
	 * This method generates the cookie from the values specified in params
	 * 
	 * @param pCookie
	 *            Cookie if Already Exists
	 * @param pCookieValue
	 *            Cookie Value to be set
	 * @param pMaxAge
	 *            Maximum age of the cookie
	 * @param pCookieName
	 *            Cookie Name
	 * @param pRequest
	 *            - DynamoHttpServletRequest
	 * @return Created Cookie in <code>Cookie</code> format
	 * @throws None
	 */

	private Cookie createCookie(Cookie pCookie, final String pCookieValue, final int pMaxAge, final String pCookieName, final DynamoHttpServletRequest pRequest) {

		final String domain = pRequest.getServerName();
		final String path = BBBCoreConstants.SLASH;
		logDebug("path to set :" + path);
		logDebug("domain to set:" + domain);
		if (pCookie == null) {
			pCookie = new Cookie(pCookieName, pCookieValue);
		} else {
			pCookie.setValue(pCookieValue);
		}
		pCookie.setMaxAge(pMaxAge);
		pCookie.setDomain(domain);
		pCookie.setPath(path);
		return pCookie;

	}

	/**
	 * This method removes the Last bought Items from the Last Viewed Cookie and
	 * then sets the new value in Last Viewed Cookie
	 * 
	 * @param pProductIds
	 *            Product Its of Commerce Items
	 * @param pLVCookie
	 *            Last Viewed cookie if this already exists
	 * @param pCookieName
	 *            Namne of Last Viewed cookie
	 * @param pLVCookieVal
	 *            Last Viewed Cookie Value
	 * @param pRequest
	 *            - DynamoHttpServletRequest
	 * @param pResponse
	 *            - DynamoHttpServletResponse
	 * @return None
	 * 
	 * @throws None
	 */

	private void removeLBItemsFromLVCookie(final List<String> pProductIds, final Cookie pLVCookie, final String pCookieName,
			final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) {
		
		List<String> lVCookieValLst = null;
		if (null != pLVCookie) {
			lVCookieValLst = getSplitCookieList(pLVCookie);
			if(null != lVCookieValLst){
			boolean isExists = false;
			for (final String productId : pProductIds) {
				if (lVCookieValLst.contains(productId)) {
					// remove the products from LV List
					lVCookieValLst.remove(productId);
					logDebug("Product Id : " + productId + "is found in Last Viewed cookie. So removing it.");
					isExists = true;
				}
			}
			if (isExists) {
				// Create the LV cookie with the new Value
				final String lVCookieValue = getCookieValueFromLst(lVCookieValLst);
				logDebug("Modifying the Last Viewed Cookie with Value : " + lVCookieValue);
				final Cookie lVCookie = createCookie(pLVCookie, lVCookieValue, getLvCookieMaxAge(), pCookieName, pRequest);
				BBBUtility.addCookie(pResponse, lVCookie, true);
			}
			}
		}

	}

	/**
	 * This method generates the last bought cookie from the highest priced
	 * commerceItem of the last order that the user has placed. This also
	 * removes those items from Last Viewed Cookie
	 * 
	 * @param pOrder
	 *            Last Order Placed by current profile
	 * @param pLVCookie
	 *            Last Viewed cookie if this already exists
	 * @param pLBCookie
	 *            Last Bought cookie if this already exists
	 * @param pLVCookieValLst
	 *            Last Viewed Cookie Value
	 * @param pRequest
	 *            - DynamoHttpServletRequest
	 * @param pResponse
	 *            - DynamoHttpServletResponse
	 * @return Last Bought Cookie <code>Cookie</code> format
	 * @throws None
	 */

	public Cookie getLastBoughtCookie(final Object pOrder, final Cookie pLVCookie, Cookie pLBCookie,
			final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) {

		if (pOrder != null) {
			String lBCookieValue = null;
			List<String> lVCookieValLst = null;
			final List<String> prdtsIds = new ArrayList<String>();
			double ciPrice = 0.0;
			double initPrice = 0.0;
			List<String> configValues = null;

			for (final CommerceItem commerceItem : (List<CommerceItem>) ((BBBOrder) pOrder).getCommerceItems()) {

				// Generate the Last bought cookie value
				ciPrice = commerceItem.getPriceInfo().getAmount() / commerceItem.getQuantity();
				if (ciPrice > initPrice) {
					lBCookieValue = commerceItem.getAuxiliaryData().getProductId();
					initPrice = ciPrice;
				}
				prdtsIds.add(commerceItem.getAuxiliaryData().getProductId());
			}

			try {
				configValues = getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.MAX_AGE_LB_COOKIE);
			} catch (BBBBusinessException bbbbEx) {
				logError(BBBCoreErrorConstants.BROWSE_ERROR_1045 + " PersonalStoreManager.getLBCookie():: " + 
						"Business Exception occurred while fetching configValue for configKey maxAgeLastBoughtCookie", bbbbEx);
			} catch (BBBSystemException bbbsEx) {
				logError(BBBCoreErrorConstants.BROWSE_ERROR_1046 + " PersonalStoreManager.getLBCookie():: " + 
						"System Exception occurred while fetching configValue for configKey maxAgeLastBoughtCookie ", bbbsEx);
			}

			int maxAge = 0;
			if (BBBUtility.isListEmpty(configValues)) {
				maxAge = getLbCookieMaxAge();
			} else {
				maxAge = Integer.parseInt(configValues.get(0));
			}

			logDebug("PersonalStoreManager.getLBCookie() :: Generating the Last Bought Cookie with Value : " + lBCookieValue);
			// generate the Last Bought Cookie
			pLBCookie = createCookie(pLBCookie, lBCookieValue, maxAge, getLbCookieNme(), pRequest);

			logDebug("PersonalStoreManager.getLBCookie() :: Generating the Last Viewed Cookie after removing the last bought items");
			
			// remove the bought Items from Last Viewed Cookie
			removeLBItemsFromLVCookie(prdtsIds, pLVCookie, getLvCookieNme(), pRequest, pResponse);
		}
		return pLBCookie;
	}

	/**
	 * This method generates cookie Value from the Last Viewed cookie
	 * 
	 * @param lastViewedCookie
	 *            Last Viewed Cookie
	 * @return Last Viewed Cookie Value in<code>String</code> format
	 * @throws None
	 */
	private String getLVPrdtFrmCookie(final Cookie lastViewedCookie) {
		final String lvListFrmCookie = lastViewedCookie.getValue();
		if (!StringUtils.isEmpty(lvListFrmCookie)) {
			final String[] lvPrdts = lvListFrmCookie.split(BBBCoreConstants.COLON);

			return lvPrdts[lvPrdts.length - 1];
		}
		return null;

	}

	/**
	 * This method generates the cookie Value to set the strategy context map
	 * with key as strategy Repository Id and value as cookie value
	 * 
	 * @param pStrategyRepoItem
	 *            Strategy Repository Item
	 * @return Cookie Value in<code>String</code> format
	 * @throws None
	 */
	public String getStrategyContextMap(final RepositoryItem pStrategyRepoItem) {
		
		logDebug("Enter.PersonalStoreManager.getStrategyContextMap(pStrategyRepoItem)");
		final String strategyName = (String) pStrategyRepoItem.getPropertyValue(BBBCoreConstants.STRATEGY_NAME);
		String cookieValue = "";
		final String cookieNames = getPersonalStoreTools().getPersonalStoreCookieMapping().get(strategyName);
		if (null != cookieNames) {
			//Get the cookies array from Cookie Strategy mapping in PersonalStoreTools
			final String[] cookieNamesArray = cookieNames.split(BBBCoreConstants.COLON);
			final DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
			Cookie cookie = null;
			final Cookie[] cookies = pRequest.getCookies();

			for (final String cookieName : cookieNamesArray) {
				if (cookies != null) {
					for (int i = 0; i < cookies.length; i++) {
						if (cookies[i].getName().equals(cookieName)) {
							cookie = cookies[i];
							break;
						}
					}
				}
				//Get the value from Last Viewed Cookie
				if (null != cookie && cookieName.equals(getLvCookieNme()) && BBBUtility.isEmpty(cookieValue)) {
					logDebug("PersonalStoreManager.getStrategyContextMap:: Get the value of strategy : " + strategyName + "from Last Viewed Cookie");
					final String lVPrdtInCookie = getLVPrdtFrmCookie(cookie);
					if (null != lVPrdtInCookie) {
						cookieValue = lVPrdtInCookie;
					}
				}
				//Get the value from Last Bought Cookie
				else if (null != cookie && cookieName.equals(getLbCookieNme()) && BBBUtility.isEmpty(cookieValue)) {
					logDebug("PersonalStoreManager.getStrategyContextMap :: Get the value of strategy : " + strategyName + "from Last Bought Cookie");
					final String lbPrdtInCookie = cookie.getValue();
					if (null != lbPrdtInCookie) {
						cookieValue = lbPrdtInCookie;
					}
				}
			}

		}
		logDebug("Exit.PersonalStoreManager.getStrategyContextMap(pStrategyRepoItem)");
		return cookieValue;
	}

	/**
	 * This method to retrieve the strategy details from Tools class and filter
	 * out the strategies based on customer type.Create the jsp Map for each
	 * strategy and adds the strategy details and jsp Map in
	 * PersonalStoreResponseVO.
	 * 
	 * @param profile
	 *            Profile Item for current profile
	 * @param pSiteId
	 *            Site Id item
	 * @return Personal Store ResponseVO for the personal Store in
	 *         <code>PersonalStoreResponseVO</code> format
	 * @throws None
	 */
	public PersonalStoreResponseVO getPersonalStoreDetails(final String pSiteId, final Profile profile) throws RepositoryException {

		BBBPerformanceMonitor.start(PersonalStoreManager.class.getName() + " : " + "getPersonalStoreDetails");
		logDebug("Enter.PersonalStoreManager.getPersonalStoreDetails(pSiteId,profile) with parmateres - SiteId :" + pSiteId + " profile :" + profile);

		 PersonalStoreResponseVO pSResponseVO = null;

			// Calls the tool class to get the strategy details for the
			// personal Store
			List<RepositoryItem> strategyRepoItems = null;
			String pSTitle = "";
			String pSTitleTrending = "";
			
			logDebug(PersonalStoreManager.class.getName() + ":getPersonalStoreDetails - Calling Personal Store Repository to get the Personal Store Details for the current site");
			final RepositoryItem[] psDetails = getPersonalStoreTools().getStrategyDetails(pSiteId, profile);
			if (null != psDetails) {
				pSResponseVO = new PersonalStoreResponseVO();
				// Get the list of strategies and title from the first
				// Repository Item
				strategyRepoItems = (List<RepositoryItem>) psDetails[0].getPropertyValue(BBBCoreConstants.STRATEGIES);
				pSTitle = (String) psDetails[0].getPropertyValue(BBBCoreConstants.PERSONAL_STORE_TITLE_DEFAULT);
				pSTitleTrending = (String) psDetails[0].getPropertyValue(BBBCoreConstants.PERSONAL_STORE_TITLE_TRENDING);
			}

			if (!BBBUtility.isListEmpty(strategyRepoItems)) {

				final Map<String, String> psStrategyJSPMap = new HashMap<String, String>();
				final Map<String, String> psCtxtCookieMap = new HashMap<String, String>();
				final List<RepositoryItem> psStrategyDetails = new ArrayList<RepositoryItem>();
				final List<String> psStrategyNameDetails = new ArrayList<String>();
				final StringBuffer layoutJSP = new StringBuffer();
				boolean isRecomStrategyExists = false;
				for (final RepositoryItem strategyRepoItem : strategyRepoItems) {

					final String customerType = (String) strategyRepoItem.getPropertyValue(BBBCoreConstants.CUSTOMER_TYPE);
					logDebug(PersonalStoreManager.class.getName() + ":getPersonalStoreDetails - Customer Type for Strategy: " +  
							strategyRepoItem.getRepositoryId() + " is :" + customerType);
					// Add the strategy in case of "both" or type of
					// profile(transient/loggedIn)
					if (null != customerType && (customerType.equalsIgnoreCase(BBBCmsConstants.BOTH) || (profile.isTransient() 
							&& customerType.equalsIgnoreCase(BBBCoreConstants.ANONYMOUS)) || (!profile.isTransient() && customerType.equalsIgnoreCase(BBBCmsConstants.LOGGEDIN)))) {

						final String strategyType = (String) strategyRepoItem.getPropertyValue(BBBCoreConstants.STRATEGY_TYPE);
						final String strategyName = (String) strategyRepoItem.getPropertyValue(BBBCoreConstants.STRATEGY_NAME);
						if (null != strategyType && (strategyType.equalsIgnoreCase(BBBCoreConstants.RECOMMENDATION) || strategyType.equalsIgnoreCase(BBBCoreConstants.TRENDING) )) {
							
						if (strategyType.equalsIgnoreCase(BBBCoreConstants.RECOMMENDATION)) {
							final String cookieValue = getStrategyContextMap(strategyRepoItem);
							logDebug(PersonalStoreManager.class.getName() + ":getPersonalStoreDetails - Cookie Value for Strategy: " +  
									strategyRepoItem.getRepositoryId() + " is :" + cookieValue);
							if(!StringUtils.isEmpty(cookieValue)){
								isRecomStrategyExists = true;
							}else{
								continue;
							}
						
							psCtxtCookieMap.put(strategyRepoItem.getRepositoryId(), cookieValue);
						}
						// Get the layout for each strategy and set the JSP
						
						// map for
						// strategy in response VO
						final RepositoryItem layoutRepoItem = (RepositoryItem) strategyRepoItem.getPropertyValue(BBBCoreConstants.STRATEGY_LAYOUT);

						if (layoutRepoItem != null) {

							logDebug("Layout for strategy " + strategyRepoItem.getRepositoryId() + " is :" + layoutRepoItem.getRepositoryId());

							String layoutName = (String) layoutRepoItem.getPropertyValue(BBBCoreConstants.LAYOUT_NAME).toString().toLowerCase();
							String layoutTypeData = (String) layoutRepoItem.getPropertyValue(BBBCoreConstants.LAYOUT_TYPE_DATA).toString().toLowerCase();
							if (BBBUtility.isEmpty(layoutName)) {
								logDebug("Layout Name for strategy " + strategyRepoItem.getRepositoryId() + " is empty. Setting the degault value");
								layoutName = BBBCoreConstants.DEF_LAYOUT_NAME;
							}
							if (BBBUtility.isEmpty(layoutTypeData)) {
								logDebug("Layout Type of Data for strategy " + strategyRepoItem.getRepositoryId() + " is empty. Setting the degault value");
								layoutTypeData = BBBCoreConstants.DEF_TYPE_DATA;
							}
							layoutJSP.append(layoutName).append(BBBCoreConstants.JSP_DELIMITER).append(layoutTypeData).append(BBBCoreConstants.JSP_EXTENSION).toString();

							logDebug("Layout JSP for strategy " + strategyRepoItem.getRepositoryId() + " is :" + layoutJSP);

							psStrategyJSPMap.put(strategyRepoItem.getRepositoryId(), layoutJSP.toString());
							psStrategyDetails.add(strategyRepoItem);
							psStrategyNameDetails.add(strategyName);
						}
						
						}
					}
					layoutJSP.delete(0, layoutJSP.length());
				}
				
				//Remove the Strategies to avoid duplicate calls to Certona
				List<RepositoryItem> psFinalStrategyDetails = new ArrayList<RepositoryItem>();
				List<String> psFinalStrategyNameDetails = new ArrayList<String>();
				
				for(final RepositoryItem strategy : psStrategyDetails){
					final String strategyType = (String) strategy.getPropertyValue(BBBCoreConstants.STRATEGY_TYPE);
					final String strategyName = (String) strategy.getPropertyValue(BBBCoreConstants.STRATEGY_NAME);
					
					//Check if strategy already exists in the final list
					if(!psFinalStrategyNameDetails.contains(strategyName)){
						if (null != strategyType && (strategyType.equalsIgnoreCase(BBBCoreConstants.RECOMMENDATION))){
							logDebug("Adding the recommended strategy " + strategyName + " in the final list");
							psFinalStrategyDetails.add(strategy);
							psFinalStrategyNameDetails.add(strategyName);
						}else{
							//get the map to check if the corresponding recommendation strategy exists in the final list
							String recomStrategyName = getPersonalStoreTools().getTrendingContextStrategyMap().get(strategyName);
							if(!psStrategyNameDetails.contains(recomStrategyName)){
								logDebug("Adding the Trending strategy " + strategyName + " in the final list");
								psFinalStrategyDetails.add(strategy);
								psFinalStrategyNameDetails.add(strategyName);
							}else{
								logDebug("Removing the strategy " + strategyName + " from the final list");
							}
						}
					}else{
						logDebug("Duplicate found : Removing the strategy " + strategyName + " from the final list");
					}
				}

				// set the JSP map and strategy details in response VO
				pSResponseVO.setStrategyJSPMap(psStrategyJSPMap);
				pSResponseVO.setStrategyDetails(psFinalStrategyDetails);
				pSResponseVO.setStrategyContextMap(psCtxtCookieMap);
				
				//Set the banner according to the type of user(Recommended/Trending)
				if(isRecomStrategyExists){
					logDebug(PersonalStoreManager.class.getName() + ":getPersonalStoreDetails - Setting personal Store Title :" + pSTitle);
					pSResponseVO.setPersonalStoreTitle(pSTitle);
				}else{
					logDebug(PersonalStoreManager.class.getName() + ":getPersonalStoreDetails - Setting personal Store Title :" + pSTitleTrending);
					pSResponseVO.setPersonalStoreTitle(pSTitleTrending);
				}
			}
		
		logDebug("Exit.PersonalStoreManager.getPersonalStoreDetails(pSiteId,profile) with returning PersonalStoreResponseVO :: " + pSResponseVO);

		BBBPerformanceMonitor.end(PersonalStoreManager.class.getName() + " : " + "getPersonalStoreDetails");

		return pSResponseVO;
	}
	
	/**
	 * This method retrieve the strategy details from Tools class and set the cookie value
	 * based on the customer type and set the Strategy details and Cookie Value in the 
	 * StrategyResponseVO.
	 * 
	 * @param strategyId
	 *            StrategyId of a selected Strategy
	 * @param strategyContextCode
	 *            strategyContextCode of a Strategy
	 * @return strategyVO containing StrategyDetails &
	 *            cookieValue
	 * @throws BBBSystemException 
	 */
	public StrategyResponseVO getPersonalStoreStrategyLookup(final String strategyId, final String strategyContextCode) throws RepositoryException, BBBSystemException {

			BBBPerformanceMonitor.start(PersonalStoreManager.class.getName() + " : " + "getPersonalStoreStrategyLookup");
			logDebug("Enter.PersonalStoreManager.getPersonalStoreStrategyLookup(strategyId,strategyContextCode) with parmateres - strategyId :" + strategyId + " strategyContextCode :" + strategyContextCode);
			 
			String cookieValue  = null ;
			final StrategyResponseVO strategyVO = new StrategyResponseVO();

			logDebug(PersonalStoreManager.class.getName() + ":getPersonalStoreStrategyLookup - Calling Personal Store Repository to get the Strategy Details based on the StrategyId");
			RepositoryItem stDetails = null;
			try {
				stDetails = getPersonalStoreTools().getStrategyDetailsLookUp(strategyId);
			} catch (RepositoryException e) {
				throw new BBBSystemException("Repository Exception in getting strategy details:", e);
			} 			
			if (null != stDetails) {
				String strategyType = (String) stDetails.getPropertyValue(BBBCoreConstants.STRATEGY_TYPE);
				if (null != strategyType && strategyType.equalsIgnoreCase(BBBCoreConstants.RECOMMENDATION)) {
					cookieValue = strategyContextCode;
					logDebug(PersonalStoreManager.class.getName() + ":getPersonalStoreStrategyLookup - Cookie Value for Strategy: " +  
							stDetails.getRepositoryId() + " is :" + cookieValue);
				}
			}
			strategyVO.setStrategyDetails(stDetails);
			strategyVO.setStrategyCookieValue(cookieValue);
			
			logDebug("Exit.PersonalStoreManager.getPersonalStoreStrategyLookup(strategyId,strategyContextCode) with returning StrategyResponseVO :: " + strategyVO);
			
			BBBPerformanceMonitor.end(PersonalStoreManager.class.getName() + " : " + "getPersonalStoreStrategyLookup");

			return strategyVO;
	}

	/**
	 * This method is used to create or update last viewed category cookey, this
	 * cookie contain category Id and category type. up to 5 last viewed unique
	 * category information is stored in cookie.
	 * 
	 * @param request
	 *            in <code>DynamoHttpServletRequest</code> format
	 * @param response
	 *            in <code>DynamoHttpServletResponse</code> format
	 * @param categoryId
	 *            in <code>String</code> format
	 * @param categoryName
	 *            in <code>String</code> format
	 * 
	 */
	public void createCategoryCookie(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response, final String categoryId, final String categoryType) {

		BBBPerformanceMonitor.start(PersonalStoreManager.class.getName() + " : " + "createCategoryCookie");
		logDebug("PersonalStoreManager.createCategoryCookie() - start");

		final String cookie = request.getCookieParameter(BBBCoreConstants.CATEGORY_COOKIE);
		final String domain = request.getServerName();
		logDebug("domain to set:" + domain);
		logDebug("categoryId :" + categoryId);
		logDebug("categoryType :" + categoryType);

		// Get max cookie age from config key
		List<String> configValues = null;
		try {
			configValues = getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.MAX_AGE_CAT_COOKIE);
		} catch (BBBBusinessException bbbbEx) {
			logError(BBBCoreErrorConstants.BROWSE_ERROR_1045 + " PersonalStoreManager.createCategoryCookie():: " + 
					"Business Exception occurred while fetching configValue for configKey maxAgeCategoryCookie", bbbbEx);
		} catch (BBBSystemException bbbsEx) {
			logError(BBBCoreErrorConstants.BROWSE_ERROR_1046 + " PersonalStoreManager.createCategoryCookie():: " + 
					"System Exception occurred while fetching configValue for configKey maxAgeCategoryCookie ", bbbsEx);
		}

		String maxAge = null;
		if (BBBUtility.isListEmpty(configValues)) {
			maxAge = getMaxAge();
		} else {
			maxAge = configValues.get(0);
		}
		
		final StringBuffer cookieValue = new StringBuffer();
		cookieValue.append(categoryId).append(BBBCoreConstants.COLON).append(categoryType);
		// Check if cookie not exist create a new category cookie else update
		// existing cookie value
		if (BBBUtility.isEmpty(cookie)) {
			int newMaxAge = -1;
			if (!StringUtils.isBlank(maxAge)) {
				newMaxAge = Integer.parseInt(maxAge);
			}
			Cookie categCookie = createCookie(null, cookieValue.toString(), newMaxAge, BBBCoreConstants.CATEGORY_COOKIE, request);
			BBBUtility.addCookie(response, categCookie, true);
		} else {
			// If cookie already exist update cookie value
			final Cookie cookieArray[] = request.getCookies();
			int maxCatInCookie = getMaxCatInCookie();
			
			for (final Cookie categoryCookie : cookieArray) {
				if (categoryCookie.getName().equals(BBBCoreConstants.CATEGORY_COOKIE)) {
					final String cookieValueArray[] = categoryCookie.getValue().split(BBBCoreConstants.COMMA);

					for (int count = 0; count < cookieValueArray.length; count++) {
						if (!(cookieValue.toString()).contains(cookieValueArray[count])) {
							cookieValue.append(BBBCoreConstants.COMMA).append(cookieValueArray[count]);
							maxCatInCookie -= 1;
						}
						if (maxCatInCookie == 1) {
							break;
						}
					}
					Cookie categCookie = createCookie(categoryCookie, cookieValue.toString(), Integer.parseInt(maxAge), BBBCoreConstants.CATEGORY_COOKIE, request);
					BBBUtility.addCookie(response, categCookie, true);
				}
			}

		}
		logDebug("PersonalStoreManager.createCategoryCookie() - end");
		BBBPerformanceMonitor.end(PersonalStoreManager.class.getName() + " : " + "createCategoryCookie");
	}

	/**
	 * this method is used to create or update last search cookie.
	 * 
	 * @param request
	 *            in <code>DynamoHttpServletRequest</code> format
	 * @param response
	 *            in <code>DynamoHttpServletResponse</code> format
	 * @param searchTerm
	 *            in <code>String</code> format
	 */
	public void createLastSearchedCookie(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse, final String searchTerm) {

		BBBPerformanceMonitor.start(PersonalStoreManager.class.getName() + " : " + "createLastSearchedCookie");
		logDebug("PersonalStoreManager.createLastSearchedCookie() - start");

		List<String> configValues = null;
		try {
			configValues = getCatalogTools().getAllValuesForKey(getSearchDimConfig(), getMaxAgeLastSearchedItemCookie());
		} catch (BBBBusinessException bbbbEx) {
			logError(BBBCoreErrorConstants.BROWSE_ERROR_1045 + " Business Exception occurred while fetching configValue " + 
					"for configKey maxAgeForCurrentViewCookie from method setCurrentViewUsingCookies in SearchDroplet", bbbbEx);
		} catch (BBBSystemException bbbsEx) {
			logError(BBBCoreErrorConstants.BROWSE_ERROR_1046 + " System Exception occurred while fetching configValue " + 
					"for configKey maxAgeForCurrentViewCookie from method setCurrentViewUsingCookies in SearchDroplet", bbbsEx);
		}
	

		if (!BBBUtility.isEmpty(searchTerm)) {
			final String domain = pRequest.getServerName();
			logDebug("domain to set:" + domain);
			logDebug("SearchTerm to set :" + searchTerm);
			String maxAge = null;
			if (BBBUtility.isListEmpty(configValues)) {
				maxAge = getMaxAge();
			} else {
				maxAge = configValues.get(0);
			}
			
			final Cookie cookieSt = new Cookie(BBBCoreConstants.SEARCHTERM, searchTerm);
			if (!StringUtils.isBlank(maxAge)) {
				cookieSt.setMaxAge(Integer.parseInt(maxAge));
			}
			cookieSt.setDomain(domain);
			cookieSt.setPath(BBBCoreConstants.SLASH);
			BBBUtility.addCookie(pResponse, cookieSt, true);
		}

		logDebug("PersonalStoreManager.createLastSearchedCookie() - end");
		BBBPerformanceMonitor.end(PersonalStoreManager.class.getName() + " : " + "createLastSearchedCookie");
	}

	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.mCatalogTools = catalogTools;
	}

	/**
	 * @return the maxCatInCookie
	 */
	public int getMaxCatInCookie() {
		return mMaxCatInCookie;
	}

	/**
	 * @param pMaxCatInCookie
	 *            the maxCatInCookie to set
	 */
	public void setMaxCatInCookie(final int pMaxCatInCookie) {
		mMaxCatInCookie = pMaxCatInCookie;
	}

	/**
	 * @return the maxAgeLastSearchedItemCookie
	 */
	public String getMaxAgeLastSearchedItemCookie() {
		return maxAgeLSItmCookie;
	}

	/**
	 * @param pMaxAgeLastSearchedItemCookie
	 *            the maxAgeLastSearchedItemCookie to set
	 */
	public void setMaxAgeLastSearchedItemCookie(final String maxAgeLSItmCookie) {
		this.maxAgeLSItmCookie = maxAgeLSItmCookie;
	}

	/**
	 * @return the searchDimConfig
	 */
	public String getSearchDimConfig() {
		return mSearchDimConfig;
	}

	/**
	 * @param pSearchDimConfig
	 *            the searchDimConfig to set
	 */
	public void setSearchDimConfig(final String pSearchDimConfig) {
		mSearchDimConfig = pSearchDimConfig;
	}

	/**
	 * @return the maxAge
	 */
	public String getMaxAge() {
		return mMaxAge;
	}

	/**
	 * @param pMaxAge
	 *            the maxAge to set
	 */
	public void setMaxAge(final String pMaxAge) {
		mMaxAge = pMaxAge;
	}

}