package com.bbb.pipeline;

/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBCollegeLinkPipeline.java
 *
 *  DESCRIPTION: A pipeline servlet handled the redirection logic for order promotions. 
 *   
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.lang.StringUtils;

import com.bbb.account.BBBDesEncryptionTools;
import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.commerce.cart.droplet.CartRegistryInfoDroplet;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.exim.bean.EximCustomizedAttributesVO;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.commerce.order.purchase.BBBCartFormhandler;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.utils.BBBUtility;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.purchase.AddCommerceItemInfo;
import atg.multisite.SiteContext;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.pipeline.InsertableServletImpl;
import atg.userprofiling.Profile;
import net.sf.ezmorph.MorphException;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * DESCRIPTION: A pipeline servlet loads.
 * 
 * @author nagarg
 */
public class BBBLoadOrderPipeline extends InsertableServletImpl {

	/*
	 * ===================================================================
	 * -------------------- MEMBER VARIABLES -----------------------------
	 * ===================================================================
	 */
	private String mUserProfile;
	private String mShoppingCart;
	private String mCartModifierFormHandler;
	private BBBDesEncryptionTools mBBBDesEncryptionTools;
	private BBBOrderManager mOrderManager;
	private Boolean mEnabled;
	private String orderCookieName;
	private int orderCookieAge;
	private String orderCookiePath;
	private String siteContextPath;

	/*
	 * ===================================================================
	 * ----------------------- CONSTANTS --------------------------------
	 * ===================================================================
	 */
	
	private static final String ORDER_ID = "orderId";
	private static final String SITE_ID = "siteId";
	private static final String ITEM_LIST = "itemList";
	private static final Integer COOKIE_LOGIN_SECURITY_STATUS = Integer.valueOf(2);
	/*
	 * ===================================================================
	 * ----------------------GETTERS and SETTERS ------------------------
	 * ===================================================================
	 */
	
	private BBBEximManager eximManager;
	private CartRegistryInfoDroplet cartRegistryInfoDroplet;
	
	public CartRegistryInfoDroplet getCartRegistryInfoDroplet() {
		return cartRegistryInfoDroplet;
	}

	public void setCartRegistryInfoDroplet(CartRegistryInfoDroplet cartRegistryInfoDroplet) {
		this.cartRegistryInfoDroplet = cartRegistryInfoDroplet;
	}

	/**
	 * @return the eximPricingManager
	 */
	public BBBEximManager getEximManager() {
		return eximManager;
	}

	/**
	 * @param eximPricingManager the eximPricingManager to set
	 */
	public void setEximManager(BBBEximManager eximManager) {
		this.eximManager = eximManager;
	}
	
	/**
 	* BBBCatalogTools property.
 	*/	
	private BBBCatalogTools mCatalogUtil;
	public BBBCatalogTools getCatalogUtil() {
		return this.mCatalogUtil;
	}	

	public void setCatalogUtil(final BBBCatalogTools catalogUtil) {
		this.mCatalogUtil = catalogUtil;
	}
	/**
	 * Getter for CartModifierFormHandler.
	 * 
	 * @return the getCartModifierFormHandler.
	 */
	public String getCartModifierFormHandler() {
		return mCartModifierFormHandler;
	}

	/**
	 * Getter for site context path.
	 * 
	 * @return site context path
	 */
	public String getSiteContextPath() {
		return siteContextPath;
	}

	/**
	 * Setter for site context path.
	 * 
	 * @param siteContextPath
	 */
	public void setSiteContextPath(String siteContextPath) {
		this.siteContextPath = siteContextPath;
	}

	/**
	 * Setter for CartModifierFormHandler.
	 * 
	 * @param pCartModifierFormHandler
	 */
	public void setCartModifierFormHandler(String pCartModifierFormHandler) {
		this.mCartModifierFormHandler = pCartModifierFormHandler;
	}
	

	/**
	 * Getter for OrderManager.
	 * 
	 * @return the OrderManager
	 */
	public BBBOrderManager getOrderManager() {
		return mOrderManager;
	}

	/**
	 * Setter for OrderManager.
	 * 
	 * @param pOrderManager
	 */
	public void setOrderManager(BBBOrderManager pOrderManager) {
		this.mOrderManager = pOrderManager;
	}

	public String getShoppingCart() {
		return mShoppingCart;
	}

	public void setShoppingCart(String pShoppingCart) {
		this.mShoppingCart = pShoppingCart;
	}

	/**
	 * @return the orderCookiePath
	 */
	public String getOrderCookiePath() {
		return orderCookiePath;
	}

	/**
	 * @param pOrderCookiePath
	 *            the orderCookiePath to set
	 */
	public void setOrderCookiePath(final String pOrderCookiePath) {
		orderCookiePath = pOrderCookiePath;
	}

	/**
	 * @return the orderCookieName
	 */
	public String getOrderCookieName() {
		return orderCookieName;
	}

	/**
	 * @param pOrderCookieName
	 *            the orderCookieName to set
	 */
	public void setOrderCookieName(final String pOrderCookieName) {
		orderCookieName = pOrderCookieName;
	}

	/**
	 * @return the orderCookieAge
	 */
	public int getOrderCookieAge() {
		return orderCookieAge;
	}

	/**
	 * @param pOrderCookieAge
	 *            the orderCookieAge to set
	 */
	public void setOrderCookieAge(final int pOrderCookieAge) {
		orderCookieAge = pOrderCookieAge;
	}

	/**
	 * @return the enabled
	 */
	public Boolean getEnabled() {
		return mEnabled;
	}

	/**
	 * @param pEnabled
	 *            the enabled to set
	 */
	public void setEnabled(final Boolean pEnabled) {
		mEnabled = pEnabled;
	}

	/**
	 * @return the userProfile
	 */
	public String getUserProfile() {
		return mUserProfile;
	}

	/**
	 * @param pRepositoryItem
	 *            the userProfile to set
	 */
	public void setUserProfile(final String userprofile) {
		mUserProfile = userprofile;
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

	/*
	 * ===================================================================
	 * --------------------------- METHODS -----------------------------
	 * ===================================================================
	 */
	
	/**
	 * This method does redirection task according to the business rules i.e
	 * when the request is coming from specified url and passed orderId is valid
	 * and attached promotion is valid user will be redirected to college
	 * landing page.
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
	
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws IOException, ServletException {

		
		String methodName = "BBBLoadOrderPipeline.service";
		BBBPerformanceMonitor.start(BBBPerformanceConstants.LOAD_ORDER_FROM_COOKIE, methodName);
			
		if (getEnabled() && pRequest.getRequestURI().indexOf("qas") == -1) {

			final Profile profile = (Profile) pRequest.resolveName(getUserProfile());
			final Integer securityStatus = (Integer) profile.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS);
			OrderHolder cart = (OrderHolder) pRequest.resolveName(getShoppingCart());
			SiteContext siteContext = (SiteContext) pRequest.resolveName(getSiteContextPath());

			if (null != pRequest.getSession().getAttribute(BBBCoreConstants.COOKIE_USED_ONCE)) {
				
				
				/* Updation of cart cookie is done in REST order customizer, so skipping this flow here*/
				if ("/rest".equals(pRequest.getContextPath())){
					passRequest(pRequest, pResponse);
					return;
				}
				
				if(isLoggingDebug()){
					logDebug("current order present in Shopping cart, add/update cookies, Profile -  " + profile);
				}
				if(profile.isTransient() || COOKIE_LOGIN_SECURITY_STATUS.equals(securityStatus) ){
					BBBOrder order = (BBBOrder) cart.getCurrent();

					// creates json object from the order
					if(null != order && null != siteContext && null != siteContext.getSite()){
						JSONObject parentJsonObject = createJSONObject(order, siteContext);

						// add cookie
						final Cookie cookie = new Cookie(getOrderCookieName(), parentJsonObject.toString());
						cookie.setMaxAge(getOrderCookieAge());
						cookie.setPath(getOrderCookiePath());
						BBBUtility.addCookie(pResponse, cookie, true);
						//pResponse.addCookie(cookie);
						if(isLoggingDebug()){
							logDebug("Cookie added for order-" + cookie);
						}
					}else{
						if(isLoggingDebug()){
							logDebug("Cookie not added for order:" + order + " siteContext:" + siteContext);
						}
					}
				}else{
					final Cookie emptyCookie = new Cookie(getOrderCookieName(), "");
					emptyCookie.setMaxAge(0);
					emptyCookie.setPath(getOrderCookiePath());
					//pResponse.addCookie(emptyCookie);
					BBBUtility.addCookie(pResponse, emptyCookie, true);
				}

			} else {
				if(isLoggingDebug()){
					logDebug("Shopping cart is empty, get cookies ");
				}	
				// set the cookie param in session
				pRequest.getSession().setAttribute(BBBCoreConstants.COOKIE_USED_ONCE, "true");
				
				final Cookie[] cookies = pRequest.getCookies();
				
			if (cookies != null && profile.isTransient()) {
					for (int i = 0; i < cookies.length; i++) {
						if (cookies[i].getName().equals(getOrderCookieName())) {
							if(isLoggingDebug()){
								logDebug("Shopping cart is empty, cookie found for order ");
							}	
							JSONObject jsonObject = null;
							try{
								if(!BBBUtility.isEmpty(cookies[i].getValue())) {
								jsonObject = (JSONObject) JSONSerializer.toJSON(cookies[i].getValue());
								}
							}catch (JSONException e) {
								if (isLoggingError()) {
			    					logError(LogMessageFormatter.formatMessage(pRequest, "JSESSION ID: " + pRequest.getSession().getId()+ ", Exception while creating the JSON from cookie, cookie value: " + cookies[i].getValue(), BBBCoreErrorConstants.CART_ERROR_1024));
			    				}
								
								if(isLoggingDebug()){
									logDebug("remove OrderCookie " );
								}
								
								// remove cookie
								final Cookie cookie = new Cookie(getOrderCookieName(), "");
								cookie.setMaxAge(0);
								cookie.setPath(getOrderCookiePath());
								pResponse.addCookie(cookie);
																
							}
							if(jsonObject != null){
								DynaBean JSONResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);
								List<String> dynaBeanProperties = (ArrayList<String>) getPropertyNames(JSONResultbean);
	
								
								String siteId = getSiteIdFromJsonObject(JSONResultbean, dynaBeanProperties);
								if(StringUtils.isNotBlank(siteId) && StringUtils.equals(siteId, siteContext.getSite().getId())){
									String orderId = null;
									try{
										if(getOrderIdFromJsonObject(JSONResultbean, dynaBeanProperties) != null){
											orderId = getOrderIdFromJsonObject(JSONResultbean, dynaBeanProperties);
										}
									}catch (NumberFormatException e) {
										if (isLoggingError()) {
					    					logError(LogMessageFormatter.formatMessage(pRequest, "Exception decrypting OrderId from JSON String" ), e);
					    				}
										
									}
									if (StringUtils.isNotBlank(orderId)) {
										if(isLoggingDebug()){
											logDebug("OrderId found in cookies order Id: " + orderId);
										}
										
										try {
											BBBOrder newOrder = (BBBOrder) getOrderManager().createOrder(profile.getRepositoryId(),
													getOrderManager().getOrderTools().getDefaultOrderType());
											
											if (isLoggingDebug()) {
												logDebug(LogMessageFormatter.formatMessage(pRequest, "New order created with id:" + newOrder));
						    				}
											
											cart.setCurrent(newOrder);
											BBBCartFormhandler cmHandler = (BBBCartFormhandler) pRequest.resolveName(getCartModifierFormHandler());
											cmHandler.setOrder(newOrder);
											
											AddCommerceItemInfo[] itemInfoArray = getCommerceitemInfoArray(pRequest, JSONResultbean, dynaBeanProperties, cmHandler);
											
											
											if(itemInfoArray != null){
												//Story Id - BPSI-4433 Start - Exim Changes to call multi-ref API
												if(itemInfoArray.length != 0 && ("true".equalsIgnoreCase(getEximManager().getKatoriAvailability()))) {
													Map<String, EximCustomizedAttributesVO> eximRefNumMap = getEximManager().getEximDetailsMapByMultiRefNum(itemInfoArray, newOrder);
													cmHandler.setEximRefNumMap(eximRefNumMap);
												}
												//Story Id - BPSI-4433 End - Changes to call multi-ref API
												cmHandler.setFromPipelineFlag(true);
												cmHandler.addItemsFromCookie(itemInfoArray, pRequest, pResponse);
												cmHandler.setFromPipelineFlag(false);
											}
											
											BBBOrder order = (BBBOrder) cart.getCurrent();
											synchronized (order) {
												getOrderManager().initializePosListInSession(pRequest, order);
												try {
								         			this.getOrderManager().updateOrder(order);
								         		} catch (final CommerceException e) {
								         			this.logError(BBBCoreErrorConstants.CART_ERROR_1021 + ": commerceException", e);
								         		}	
											}
											pRequest.setParameter(BBBCoreConstants.ORDER, order);
											getCartRegistryInfoDroplet().service(pRequest, pResponse);
											if (isLoggingDebug()) {
												logDebug(LogMessageFormatter.formatMessage(pRequest, "Item froms cookies added to shopping cart:"));
						    				}
											
										} catch (CommerceException e) {
											if (isLoggingError()) {
						    					logError(LogMessageFormatter.formatMessage(pRequest, "Exception while creating the order from cookie" ), e);
						    				}
										}
									}
								}
								
	
								break;
							}
						}
					}
				}

			}

		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.LOAD_ORDER_FROM_COOKIE, methodName);

		passRequest(pRequest, pResponse);

	}
	
	
	
	/**
	 * @param itemArray
	 * @param count
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public int populateAddItemInfo(List<DynaBean> itemArray, AddCommerceItemInfo[] itemInfos, String siteId) {
		int count = 0;
		for (DynaBean item : itemArray) {
			//s is skuId, p is prodId, q is qty, r is registryId, st is storeId, b is bts
			itemInfos[count].setCatalogRefId(item.get("s").toString());
			itemInfos[count].setProductId(item.get("p").toString());
			itemInfos[count].setQuantity(Long.parseLong(item.get("q").toString()));
			List<String> itemProperties = (ArrayList<String>) getPropertyNames(item);
			if(itemProperties.contains("sm"))
			{   boolean isShippingMethodExistsForSku = false;
				String shippingMethod = item.get("sm").toString();
				Boolean isAssemblyOffered = false;
				try{
					boolean isSkuLTL = getCatalogUtil().isSkuLtl(siteId, item.get("s").toString());
					if(BBBUtility.isNotEmpty(shippingMethod) && isSkuLTL){
						
						if(shippingMethod.equalsIgnoreCase(BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD)){
							shippingMethod = BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD;
							isAssemblyOffered = true;
						}
						//set in transient property of gift list
						isShippingMethodExistsForSku = getCatalogUtil().isShippingMethodExistsForSku(siteId, item.get("s").toString(), shippingMethod, isAssemblyOffered);
					} else if(BBBUtility.isEmpty(shippingMethod) && isSkuLTL) {
						isShippingMethodExistsForSku = true;
					}
				}catch(BBBSystemException e){
						String msg = "Error while retrieving product details for item [" + item.get("s").toString() + "]";
                    	this.logError(msg, e);
				}
				catch(BBBBusinessException e){
						String msg = "Error while retrieving product details for item [" + item.get("s").toString() + "]";
                    	this.logError(msg, e);
				}
				if (isShippingMethodExistsForSku) {
				if(item.get("sm").toString().equalsIgnoreCase(BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD))
            	{
            		//checks if shipping method code for LTL item is white glove with assembly if true then shipping method name is white glove 
            		//and assembly is true.
                	itemInfos[count].getValue().put(BBBCatalogConstants.LTL_SHIP_METHOD,BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD);
                	itemInfos[count].getValue().put(BBBCatalogConstants.WHITE_GLOVE_ASSEMLBY,"true");
            	
            	}
            	else if(item.get("sm").toString().equalsIgnoreCase(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD))
            	{
            		//In case not while glove with assembly for LTL item then set whatever is coming as input
            		itemInfos[count].getValue().put(BBBCatalogConstants.LTL_SHIP_METHOD,BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD);
            		itemInfos[count].getValue().put(BBBCatalogConstants.WHITE_GLOVE_ASSEMLBY,"false");
                }
            	else
            		itemInfos[count].getValue().put(BBBCatalogConstants.LTL_SHIP_METHOD, item.get("sm").toString());
            	} else {
            		itemInfos[count].getValue().put("prevLtlShipMethod",shippingMethod); 
            		itemInfos[count].getValue().put("shipMethodUnsupported",BBBCatalogConstants.TRUE);
            		itemInfos[count].getValue().put(BBBCatalogConstants.WHITE_GLOVE_ASSEMLBY,isAssemblyOffered.toString()); 
            	}
			}
			if (itemProperties.contains("r")) {
				itemInfos[count].getValue().put("registryId", item.get("r").toString());
			}
			if ((itemProperties.contains("st")) && (null != item.get("st")) && !(item.get("st").toString().equals(""))) {
				itemInfos[count].getValue().put("storeId", item.get("st").toString());
			}
			if (itemProperties.contains("refNum")) {
				itemInfos[count].getValue().put("referenceNumber", item.get("refNum").toString());
			}
			itemInfos[count].getValue().put("bts", item.get("b").toString());
			itemInfos[count].getValue().put("prevPrice", item.get("prc").toString());
			if ((itemProperties.contains("seqNo")) && (null != item.get("seqNo")) && !(item.get("seqNo").toString().equals(""))) {
			itemInfos[count].getValue().put("seqNumber",item.get("seqNo").toString());
			}
			if ((itemProperties.contains(BBBCoreConstants.ORIGINAL_LTL_SHIP_METHOD)) && (null != item.get(BBBCoreConstants.ORIGINAL_LTL_SHIP_METHOD)) && 
					!(item.get(BBBCoreConstants.ORIGINAL_LTL_SHIP_METHOD).toString().equals(""))) {
				itemInfos[count].getValue().put(BBBCoreConstants.REGISTRANT_SHIP_METHOD, item.get(BBBCoreConstants.ORIGINAL_LTL_SHIP_METHOD).toString());
			}
			itemInfos[count].getValue().put(BBBCoreConstants.OOS, item.get(BBBCoreConstants.OOS).toString());
			count++;
		}
		if(isLoggingDebug()){
			logDebug("Add Item info populated succesfully - item count:" + count);
		}
		
		return count;
	}

	/**
	 * Creates the ADDCommerceitemInfo Array to be used while adding items to
	 * the cart.
	 * 
	 * @param JSONResultbean
	 * @param dynaBeanProperties
	 */
	@SuppressWarnings("unchecked")
	private AddCommerceItemInfo[] getCommerceitemInfoArray(final DynamoHttpServletRequest pRequest, DynaBean JSONResultbean, List<String> dynaBeanProperties,BBBCartFormhandler cmHandler) {

		if(isLoggingDebug()){
			logDebug("Start: getCommerceitemInfoArray");
		}
		
		AddCommerceItemInfo[] itemInfoArray = null;
		if (dynaBeanProperties.contains(ITEM_LIST)) {
			List<DynaBean> itemArray = (ArrayList<DynaBean>) JSONResultbean.get(ITEM_LIST);
			for (Iterator<DynaBean> iterator = itemArray.iterator(); iterator.hasNext();) {
				DynaBean item = (DynaBean) iterator.next();
				if(StringUtils.isBlank((String) item.get("p")) || StringUtils.isBlank((String) item.get("s")) || null == item.get("q") || !(((Integer) item.get("q")) > 0)) {
					iterator.remove();
				}	
			}			
			String siteId = (String) JSONResultbean.get(SITE_ID);

			itemInfoArray = new AddCommerceItemInfo[itemArray.size()];

			for (int index = 0; index < itemArray.size(); index++) {
				itemInfoArray[index] = new AddCommerceItemInfo();
			}
			try{
				populateAddItemInfo(itemArray, itemInfoArray, siteId);
			}catch (MorphException e) {
				if(isLoggingError()){
					logError(LogMessageFormatter.formatMessage(pRequest, "Exception while reading JSON object from cookie" ), e);
				}
				return null;
			}catch (JSONException e) {
				if(isLoggingError()){
					logError(LogMessageFormatter.formatMessage(pRequest, "Exception while reading JSON object from cookie" ), e);
				}
				return null;
			}
		}
		
		if(isLoggingDebug()){
			logDebug("End: getCommerceitemInfoArray - AddCommerceItemInfo created");
		}
		
		return itemInfoArray;
	}

	/**
	 * @param JSONResultbean
	 * @param dynaBeanProperties
	 */
	private String getOrderIdFromJsonObject(DynaBean JSONResultbean, List<String> dynaBeanProperties) {
		String orderId = null;
		if (dynaBeanProperties.contains(ORDER_ID) &&
				JSONResultbean.get(ORDER_ID) != null) {
			orderId = JSONResultbean.get(ORDER_ID).toString();
		}
		return orderId;
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
	 * @param order
	 * @return the JSON object
	 */
	@SuppressWarnings("unchecked")
	private JSONObject createJSONObject(BBBOrder order, SiteContext siteContext) {

		JSONObject jsonRootObject = new JSONObject();
		jsonRootObject.element(ORDER_ID, getBBBDesEncryptionTools().encrypt(order.getId()));
		jsonRootObject.element(SITE_ID, siteContext.getSite().getId());
		
		List jsonCIList = new ArrayList();
		JSONObject jsonCIObject;
		for (Iterator<CommerceItem> iterator = order.getCommerceItems().iterator(); iterator.hasNext();) {
			
			CommerceItem citem = iterator.next();
			if (citem instanceof BBBCommerceItem) {
				//s is skuId, p is prodId, q is qty, r is registryId, st is storeId, b is bts
				jsonCIObject = new JSONObject();
				jsonCIObject.put("s", citem.getCatalogRefId());
				jsonCIObject.put("p", ((BBBCommerceItem) citem).getAuxiliaryData().getProductId());
				jsonCIObject.put("q", citem.getQuantity());
				jsonCIObject.put("b", ((BBBCommerceItem) citem).getBts());
				jsonCIObject.put("st", ((BBBCommerceItem) citem).getStoreId());
				jsonCIObject.put("r", ((BBBCommerceItem) citem).getRegistryId());
				jsonCIObject.put("prc", ((BBBCommerceItem) citem).getPrevPrice());
				jsonCIObject.put("oos", ((BBBCommerceItem) citem).isMsgShownOOS());
				jsonCIObject.put("seqNo", ((BBBCommerceItem) citem).getSeqNumber());
				jsonCIObject.put("refNum", ((BBBCommerceItem) citem).getReferenceNumber());
				jsonCIObject.put(BBBCoreConstants.ORIGINAL_LTL_SHIP_METHOD, ((BBBCommerceItem) citem).getRegistrantShipMethod());
				if(((BBBCommerceItem)citem).getLtlShipMethod()!=null){
					
					if(((BBBCommerceItem)citem).getLtlShipMethod().equals(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD) && ((BBBCommerceItem) citem).getAssemblyItemId()!=null && !((BBBCommerceItem) citem).getAssemblyItemId().isEmpty())
					{
							jsonCIObject.put("sm",BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD );
					}
					else
					{
						jsonCIObject.put("sm", ((BBBCommerceItem)citem).getLtlShipMethod());
					}
				}
				else
				{
					jsonCIObject.put("sm", "");
				}
				jsonCIList.add(jsonCIObject);
			}

			

		}
		if(isLoggingDebug()){
			logDebug("Json object created successfully");
		}
		jsonRootObject.element(ITEM_LIST, jsonCIList);
		return jsonRootObject;
	}

	/*
	 * To get the properties names from JSON result string
	 */
	private List<String> getPropertyNames(DynaBean pDynaBean) {

		DynaClass dynaClass = pDynaBean.getDynaClass();
		DynaProperty properties[] = dynaClass.getDynaProperties();
		List<String> propertyNames = new ArrayList<String>();
		for (int i = 0; i < properties.length; i++) {
			String name = properties[i].getName();
			propertyNames.add(name);
		}
		return propertyNames;
	}

	

}
