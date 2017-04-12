package com.bbb.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.pipeline.InsertableServletImpl;
import atg.userprofiling.Profile;

import com.bbb.account.BBBDesEncryptionTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSavedItemsSessionBean;
import com.bbb.utils.BBBUtility;
import com.bbb.wishlist.GiftListVO;
import com.bbb.wishlist.manager.BBBGiftlistManager;

public class BBBLoadSavedItemsPipeline extends InsertableServletImpl {
	
	/*
	 * ===================================================================
	 * -------------------- MEMBER VARIABLES -----------------------------
	 * ===================================================================
	 */
	private String mSavedItemsSessionBean;
	private String mUserProfile;
	private String mSave4LaterCookieName;
	private int mSave4LaterCookieAge;
	private String mSave4LaterCookiePath;
	private BBBGiftlistManager giftlistManager;
	private BBBDesEncryptionTools mBBBDesEncryptionTools;
	private boolean enabled;

	
	/*
	 * ===================================================================
	 * ----------------------- CONSTANTS --------------------------------
	 * ===================================================================
	 */
	
	private static final String SITE_ID = "siteId";
	private static final String SAVED_ITEMS_LIST = "savedItemsList";
	
	
	/*
	 * ===================================================================
	 * ----------------------GETTERS and SETTERS ------------------------
	 * ===================================================================
	 */
	
	/**
	 * @return the giftlistManager
	 */
	public BBBGiftlistManager getGiftlistManager() {
		return giftlistManager;
	}

	/**
	 * @param giftlistManager the giftlistManager to set
	 */
	public void setGiftlistManager(BBBGiftlistManager giftlistManager) {
		this.giftlistManager = giftlistManager;
	}
	
	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * @return the mUserProfile
	 */
	public String getUserProfile() {
		return mUserProfile;
	}

	/**
	 * @param mUserProfile the mUserProfile to set
	 */
	public void setUserProfile(String mUserProfile) {
		this.mUserProfile = mUserProfile;
	}

	/**
	 * @return the mBBBDesEncryptionTools
	 */
	public BBBDesEncryptionTools getBBBDesEncryptionTools() {
		return mBBBDesEncryptionTools;
	}

	/**
	 * @param mBBBDesEncryptionTools the mBBBDesEncryptionTools to set
	 */
	public void setBBBDesEncryptionTools(
			BBBDesEncryptionTools pBBBDesEncryptionTools) {
		this.mBBBDesEncryptionTools = pBBBDesEncryptionTools;
	}

	public String getSave4LaterCookieName() {
		return mSave4LaterCookieName;
	}

	public void setSave4LaterCookieName(String save4LaterCookieName) {
		mSave4LaterCookieName = save4LaterCookieName;
	}

	public int getSave4LaterCookieAge() {
		return mSave4LaterCookieAge;
	}

	public void setSave4LaterCookieAge(int save4LaterCookieAge) {
		mSave4LaterCookieAge = save4LaterCookieAge;
	}

	public String getSave4LaterCookiePath() {
		return mSave4LaterCookiePath;
	}

	public void setSave4LaterCookiePath(String save4LaterCookiePath) {
		mSave4LaterCookiePath = save4LaterCookiePath;
	}
	
	/**
	 * @return the saveditemssessionBean
	 */
	public String getSavedItemsSessionBean() {
		return mSavedItemsSessionBean;
	}
	
	/**
	 * @param pSessionBean
	 *            the sessionBean to set
	 */
	public void setSavedItemsSessionBean(String pSavedItemsSessionBean) {
		this.mSavedItemsSessionBean = pSavedItemsSessionBean;
	}
	
	/*
	 * ===================================================================
	 * --------------------------- METHODS -----------------------------
	 * ===================================================================
	 */
	
	

	/**
	 * This method loads the saved items for anonymous user
	 * when session is being established first time, if there exist any wishListCookie in browser request ,
	 * then restore the previous saved items bucket.
	 * When user is transient it converts the current stored items bucket to JSON and 
	 * store it into cookie so that saved items can be later restored for anonymous user
	 * 
	 * @param pRequest
	 *            DynamoHttpServletRequest
	 * @param pResponse
	 *            DynamoHttpServletResponse
	 * @throws ServletException
	 *             if there was an error while executing the code
	 * @throws IOException
	 *             if there was an error with servlet io
	 * @return void
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws IOException, ServletException
	{
		String methodName = "BBBLoadOrderPipeline.service";
		BBBPerformanceMonitor.start(BBBPerformanceConstants.LOAD_SAVED_ITEM_FROM_COOKIE, methodName);
		boolean isSaveForLaterCookie = false;
		boolean isGiftListVO = false;
		String sendSFLCookie = pRequest.getHeader(BBBCoreConstants.SEND_SFL_COOKIE);
		boolean skipCookie = false;
		
		if (BBBCoreConstants.TRUE.equalsIgnoreCase(sendSFLCookie)) {
			// If any of the above header is set true then skip the cookie creation part in this servlet
			// Latest cookie after item is added/removed into / from SFL will be created from OrderPropertyCustomizer along with CartCookie
			skipCookie = true;
		}
		if (isEnabled()){
			if (isLoggingDebug()) {
				logDebug("BBBLoadSavedItemsPipeline :: service :: STARTS");
			}
			final Profile profile = (Profile) pRequest.resolveName(getUserProfile());
			BBBSavedItemsSessionBean savedItemsListBean = (BBBSavedItemsSessionBean) pRequest.resolveName(getSavedItemsSessionBean());
			if(savedItemsListBean.getGiftListVO() == null)
			{
				isGiftListVO = false;
			}
			else
			{
				isGiftListVO = true;
			}
			 
			final Cookie[] allCookies = pRequest.getCookies();
			if (allCookies != null) {
				for (int i = 0; i < allCookies.length; i++) {
					if (allCookies[i].getName().equals(getSave4LaterCookieName()))
					{
						pRequest.getSession().setAttribute(BBBCoreConstants.SAVE4LATER_COOKIE, "true");
						isSaveForLaterCookie = true;
						break;
					} else {
						pRequest.getSession().setAttribute(BBBCoreConstants.SAVE4LATER_COOKIE, "false");
						isSaveForLaterCookie = false;
					}
				}
			}
			
			//For Non Transient User 
			if (profile.isTransient()) {
				if (!isSaveForLaterCookie) {
					// Create cookie for first time 
					Cookie emptyCookie = new Cookie(getSave4LaterCookieName(),"");
					emptyCookie.setMaxAge(getSave4LaterCookieAge());
					emptyCookie.setPath(getSave4LaterCookiePath());
					BBBUtility.addCookie(pResponse, emptyCookie, true);
					pRequest.getSession().setAttribute(BBBCoreConstants.SAVE4LATER_COOKIE, "true");
					isSaveForLaterCookie=true;
				}
				//Second Time user cookie Exists but no Session Data && savedItemsListBean.getGiftListVO().isEmpty()
				else if(isSaveForLaterCookie && savedItemsListBean !=null && !isGiftListVO)
				{
					if (isLoggingDebug()) {
						logDebug("First request session is eastablished and cookie is present");
					}

					final Cookie[] cookies = pRequest.getCookies();
					if (cookies != null) {
						for (int i = 0; i < cookies.length; i++) {
							if (cookies[i].getName().equals(
									getSave4LaterCookieName())) {
								if(!cookies[i].getValue().isEmpty()){
								if (isLoggingDebug()) {
									logDebug("cookie found for saved items ");
								}
								JSONObject jsonObject = null;
								try {
									jsonObject = (JSONObject) JSONSerializer.toJSON(cookies[i].getValue());
								} catch (JSONException e) {
									if (isLoggingError()) {
										logError(LogMessageFormatter.formatMessage(pRequest,"JSESSION ID: "+ pRequest.getSession().getId()+ ", Exception while creating the JSON from cookie, cookie value: "+ cookies[i].getValue(),BBBCoreErrorConstants.CART_ERROR_1024));
									}

								}

								// remove cookie
								final Cookie oldCookie = new Cookie(getSave4LaterCookieName(), "");
								oldCookie.setMaxAge(0);
								oldCookie.setPath("/");
								pResponse.addCookie(oldCookie);

								if (jsonObject != null) {
									DynaBean JSONResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);
									List<String> dynaBeanProperties = (ArrayList<String>) getGiftlistManager().getPropertyNames(JSONResultbean);

									String siteId = getSiteIdFromJsonObject(JSONResultbean, dynaBeanProperties);
									
									if (StringUtils.isNotBlank(siteId)&& StringUtils.equals(siteId,SiteContextManager.getCurrentSiteId())) {
										List<GiftListVO> savedItemsList = new ArrayList();
										try {

											savedItemsList = getGiftlistManager().parseSavedItemListJson(JSONResultbean,siteId);
											savedItemsListBean.setGiftListVO(savedItemsList);
											} catch (NumberFormatException e) {
											if (isLoggingError()) {
												logError(LogMessageFormatter.formatMessage(pRequest,"Exception decrypting OrderId from JSON String"),e);
											}

										}
										if (savedItemsList != null) {
											if (isLoggingDebug()) {
												logDebug("Savedforlater list found in cookies : "+ savedItemsList);
											}

										}
									}

								}
								}
									

							}
						}
					}

					//
				
				}
				//Second Time user cookie Exists Session Data 
				else if (pRequest.getSession() != null && isSaveForLaterCookie && savedItemsListBean != null && isGiftListVO && !skipCookie) {

					// remove cookie
					Cookie oldCookie = new Cookie(getSave4LaterCookieName(), "");
					oldCookie.setMaxAge(0);
					oldCookie.setPath("/");
								
					JSONObject jsonRootObject = new JSONObject();
					jsonRootObject.element(SITE_ID, SiteContextManager.getCurrentSiteId());
					List jsonCIObjectList;

					jsonCIObjectList = createJSONObject(savedItemsListBean.getGiftListVO(), SiteContextManager.getCurrentSiteId());

					if (isLoggingDebug()) {
						logDebug("Json object created successfully");
					}
					jsonRootObject.element(SAVED_ITEMS_LIST, jsonCIObjectList);
					
					oldCookie = new Cookie(getSave4LaterCookieName(), jsonRootObject.toString());
					oldCookie.setMaxAge(getSave4LaterCookieAge());
					oldCookie.setPath("/");
					BBBUtility.addCookie(pResponse, oldCookie, true);
					
				}
				BBBPerformanceMonitor.end(BBBPerformanceConstants.LOAD_SAVED_ITEM_FROM_COOKIE, methodName);
				passRequest(pRequest, pResponse);
			} else {
				BBBPerformanceMonitor.end(BBBPerformanceConstants.LOAD_SAVED_ITEM_FROM_COOKIE, methodName);
				passRequest(pRequest, pResponse);
			}
		} else {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.LOAD_SAVED_ITEM_FROM_COOKIE, methodName);
			passRequest(pRequest, pResponse);
		}
		 
		
		
	}

	/**
	 * Get Site id from json object
	 * @param JSONResultbean
	 * @param dynaBeanProperties
	 */
	private String getSiteIdFromJsonObject(DynaBean JSONResultbean, List<String> dynaBeanProperties) {
		String siteId = null;
		if (dynaBeanProperties.contains(SITE_ID) &&
				JSONResultbean.get(SITE_ID) != null) {
			siteId = JSONResultbean.get(SITE_ID).toString();
		}
		return siteId;
	}
	
	/**
	 * Create JSOn object from the order.
	 * 
	 * @param list
	 * @return the JSON object
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List createJSONObject(List<GiftListVO> list, String siteId) {

		List jsonCIList = new ArrayList();
		JSONObject jsonCIObject;
		for (Iterator<GiftListVO> iterator = list.iterator(); iterator.hasNext();) {
			
			GiftListVO gitem = iterator.next();
			if (gitem instanceof GiftListVO) {
				//s is skuId, p is prodId, q is qty, r is registryId, st is storeId, b is bts, refNum personalized reference number 
				jsonCIObject = new JSONObject();
				jsonCIObject.put("p", gitem.getProdID());
				jsonCIObject.put("s", gitem.getSkuID());
				jsonCIObject.put("q", gitem.getQuantity());
				jsonCIObject.put("pp", gitem.getPrevPrice());
				jsonCIObject.put("r", gitem.getRegistryID());
				jsonCIObject.put("w", gitem.getWishListItemId());
				jsonCIObject.put("oos", gitem.isMsgShownOOS());
				jsonCIObject.put("sm", gitem.getLtlShipMethod());
				//reference number
				jsonCIObject.put("refNum", gitem.getReferenceNumber());
				jsonCIList.add(jsonCIObject);
			}

			

		}
		if(isLoggingDebug()){
			logDebug("Json object created successfully");
		}
		return jsonCIList;
	}

}
