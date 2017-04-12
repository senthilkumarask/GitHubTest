package com.bbb.rest.framework;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.Cookie;

import net.sf.json.JSONObject;
import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.purchase.CommitOrderFormHandler;
import atg.multisite.SiteContextManager;
import atg.rest.filtering.RestPropertyCustomizer;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.BBBDesEncryptionTools;
import com.bbb.account.order.manager.OrderDetailsManager;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.checkout.formhandler.BBBPaymentGroupFormHandler;
import com.bbb.commerce.common.BBBOrderVO;
import com.bbb.commerce.order.formhandler.BBBShippingGroupFormhandler;
import com.bbb.commerce.order.purchase.BBBCartFormhandler;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.utils.BBBUtility;
import com.bbb.wishlist.StoreGiftlistFormHandler;

/**
 * This REST property customizer is used to transform the order object into REST BBBOrderVO
 * 
 * @author Gaurav Bisht
 * @version $Change: 698235 $$DateTime: 2012/04/20 06:41:21 $$Author: jsiddaga $
 * 
 * 
 */

public class OrderPropertyCustomizer  extends BBBGenericService  implements RestPropertyCustomizer{

	public static String CLASS_VERSION = "$Id: //hosting-blueprint/MobileCommerce/version/10.1.1/server/MobileCommerce/src/atg/rest/filtering/customizers/RepriceCartPropertyCustomizer.java#1 $$Change: 698235 $";
	private static final String ORDER_ID = "orderId";
	private static final String SITE_ID = "siteId";
	private static final String ITEM_LIST = "itemList";
	private static final int orderCookieAge = 2592000;
	private static final String orderCookiePath = "/";
	private static final String cartCookie = "CartCookie";

	public Object getPropertyValue(String pPropertyName, Object pResource) {
		logDebug("Entering OrderPropertyCustomizer.getPropertyValue");
		Object value = null;
		BBBOrderVO orderVO = null;
		try {
			value = DynamicBeans.getSubPropertyValue(pResource, pPropertyName);
			logDebug("OrderPropertyCustomizer::: value="+value);
		} catch (PropertyNotFoundException e) {
			logError(e.getMessage(),e);
		}
		try {
			/* Start BBBSL-2667 */
			boolean isValidSession=true;
			if(pResource instanceof BBBPaymentGroupFormHandler){
				isValidSession=!((BBBPaymentGroupFormHandler) pResource).isInvalidateSession();
			}
			// BBBH-1486 story changes
			boolean sddRequired = false;
			DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
			DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
			if(pResource instanceof BBBCartFormhandler){
				if(((BBBCartFormhandler) pResource).getSddRequired() != null){
					sddRequired = Boolean.valueOf(((BBBCartFormhandler) pResource).getSddRequired());
				}
				
			} else if(pResource instanceof StoreGiftlistFormHandler){
				if(((StoreGiftlistFormHandler) pResource).getSddRequired() != null){
					sddRequired = Boolean.valueOf(((StoreGiftlistFormHandler) pResource).getSddRequired());
				}
			} else if(pResource instanceof BBBShippingGroupFormhandler){
				if(((BBBShippingGroupFormhandler) pResource).getSddRequired() != null){
					sddRequired = Boolean.valueOf(((BBBShippingGroupFormhandler) pResource).getSddRequired());
				}
			}
			
			if(sddRequired){
				pRequest.setParameter(BBBCoreConstants.ACTION_REQ, BBBCoreConstants.SAME_DAY_DELIVERY_REQ);
			}
			
			/* End BBBSL-2667 */
			if(isValidSession){
				OrderDetailsManager orderManager = (OrderDetailsManager)ServletUtil.getCurrentRequest().resolveName("/com/bbb/account/OrderDetailsManager");
				orderVO = orderManager.getOrderDetailsVO((BBBOrderImpl) value, false, false);
				Profile profile = (Profile) pRequest.resolveName("/atg/userprofiling/Profile");
				if(profile.isTransient()){
					Object value2 = value;
					if(pResource instanceof CommitOrderFormHandler){
						OrderHolder shoppingCart = (OrderHolder) pRequest.resolveName("/atg/commerce/ShoppingCart");
						value2 = (BBBOrderImpl) shoppingCart.getCurrent();
					}
					JSONObject parentJsonObject = createJSONObject((BBBOrderImpl) value2, SiteContextManager.getCurrentSiteId());
					// add cookie
					final Cookie cookie = new Cookie(cartCookie, parentJsonObject.toString());
					cookie.setMaxAge(orderCookieAge);
					cookie.setPath(orderCookiePath);
					BBBUtility.addCookie(pResponse, cookie, false);

					if (pRequest.getAttribute(BBBCoreConstants.SEND_SFL_COOKIE) != null && (Boolean) (pRequest.getAttribute(BBBCoreConstants.SEND_SFL_COOKIE))) {
						BBBUtility.addCookie(pResponse, orderManager.createSFLCookie(pRequest), true);
					}
				}
				else{
					final Cookie emptyCookie = new Cookie(cartCookie, "");
					emptyCookie.setMaxAge(0);
					emptyCookie.setPath(orderCookiePath);
					//pResponse.addCookie(emptyCookie);
					BBBUtility.addCookie(pResponse, emptyCookie, true);
				}
			}	 

		} catch (BBBSystemException e) {
			logError("OrderPropertyCustomizer::: System Exception occoured - ", e);
		} catch (BBBBusinessException e) {
			logError("OrderPropertyCustomizer::: Business Exception occoured - ", e);
		} catch (CommerceException e) {
			logError("OrderPropertyCustomizer::: Commerce Exception occoured - ", e);
		}
		logDebug("Exiting OrderPropertyCustomizer.getPropertyValue");
		return orderVO;
	}


	@Override
	public void setPropertyValue(String arg0, Object arg1, Object arg2) {
		throw new UnsupportedOperationException("Not implemented");
	}

	/**
	 * Create JSOn object from the order.
	 * 
	 * @param order
	 * @return the JSON object
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JSONObject createJSONObject(BBBOrder order, String siteId) {
		JSONObject jsonRootObject = new JSONObject();
		BBBDesEncryptionTools encryptTools = (BBBDesEncryptionTools)ServletUtil.getCurrentRequest().resolveName("/com/bbb/account/BBBDesEncryptionTools");
		if(encryptTools != null){
			jsonRootObject.element(ORDER_ID, encryptTools.encrypt(order.getId()));}
		jsonRootObject.element(SITE_ID, siteId);

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
				jsonCIObject.put("refNum", ((BBBCommerceItem) citem).getReferenceNumber());
				//BPSI-416 For Anonymous cart adding the LTL ship Method start
				if(((BBBCommerceItem)citem).getLtlShipMethod()!=null){
					jsonCIObject.put("sm", ((BBBCommerceItem)citem).getLtlShipMethod());
					if(((BBBCommerceItem)citem).getLtlShipMethod().equals(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD) && ((BBBCommerceItem) citem).getAssemblyItemId()!=null && !((BBBCommerceItem) citem).getAssemblyItemId().isEmpty())
					{
						jsonCIObject.put("sm",BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD );
					}
				}
				else
				{
					jsonCIObject.put("sm", "");
				}
				//BPSI-416 For Anonymous cart adding the LTL ship Method end
				jsonCIList.add(jsonCIObject);
			}
		}

		logDebug("OrderPropertyCustomizer::: Json object created successfully");

		jsonRootObject.element(ITEM_LIST, jsonCIList);
		return jsonRootObject;
	}

}
