/*
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  DisplaySingleShippingDroplet.java
 *
 *  DESCRIPTION: This class controls the display of shipping address page content
 *  based on the items in the cart, empty cart or can be shipped on one address or would
 *  need a multiple addresses.
 *
 *  HISTORY:
 *  Oct 14, 2011  Initial version
*/

package com.bbb.commerce.order.purchase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;


import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.common.BBBMultishipVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 * @author nagarg
 * The service method checks for items in cart and returns parameter base on which
 * the single, multi or cart oparams are returned.
 *
 */
public class DisplaySingleShippingDroplet extends BBBDynamoServlet {
    
  
	
	private static final String IS_MULTI_SHIPPING = "isMultiShipping";
	private static final String SHOW_SINGLE_SHIPPING = "showSingleShipping";
    private static final String CART = "cart";
    private static final String IS_SINGLE = "isSingle";
    private static final String MULTI = "multi";
    private static final String SINGLE = "single";
    private static final String SHIP_METHOD_DET = "shippingMethodDetail";
    private BBBCheckoutManager mManager;
    

	
    /* (non-Javadoc)
     * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
     */
    @Override
    
    public void service(DynamoHttpServletRequest req,
    		DynamoHttpServletResponse res) throws ServletException, IOException {
    	Order order = (Order) req.getObjectParameter(BBBCoreConstants.ORDER);
    	String payPalError=req.getParameter("paypalError");
    	String isFormException=req.getParameter("isFormException");
    	String fromMobile = req.getParameter("fromMobile");
    	int commerceItemCount = order.getCommerceItemCount();
    	boolean result;
    	boolean singlePageCheckoutKey = false;
    	List<String> spcBCCFlagList;
    	try {
    		spcBCCFlagList = this.getManager().getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "SinglePageCheckoutOn");
    		if (!BBBUtility.isListEmpty(spcBCCFlagList)) {
    			singlePageCheckoutKey = Boolean.parseBoolean(spcBCCFlagList.get(0));
    		}
    		if(commerceItemCount <= 0) {//cart is empty
    			req.setParameter(IS_MULTI_SHIPPING, false);
    			req.setParameter(SHOW_SINGLE_SHIPPING, false);
    			req.serviceLocalParameter(CART, req, res);
    			return;
    		}

    		if((((BBBOrderImpl) order).isPayPalOrder() && (!StringUtils.isEmpty(payPalError)) && payPalError.equalsIgnoreCase(BBBCoreConstants.TRUE))
    				|| (((BBBOrderImpl) order).isPayPalOrder() && (!StringUtils.isEmpty(isFormException)) && isFormException.equalsIgnoreCase(BBBCoreConstants.TRUE))){
    			this.logDebug("Redirecting to single shipping in case of paypal order");
    			result = true;
    		}
    		else{
    			if(singlePageCheckoutKey && !BBBUtility.isEmpty(fromMobile) && Boolean.valueOf(fromMobile)){
    				// Changes for single page checkout redirection in case of mobile
    				result = getManager().displaySingleShipping(order, true);
    			} else{
    				result = getManager().displaySingleShipping(order, false);
    			}
    		}
    		req.setParameter(IS_SINGLE, result);

    		boolean ordercContainsLTLItem=getManager().orderContainsLTLItem(order);
    		if(ordercContainsLTLItem){
    			req.setParameter(IS_MULTI_SHIPPING, true);
    			req.setParameter(SHOW_SINGLE_SHIPPING, false);
    			req.serviceLocalParameter(MULTI, req, res);
    		} 
    		else {
    			if(result) {//order qualifies for single address shippment
    				req.setParameter(IS_MULTI_SHIPPING, false);
    				req.setParameter(SHOW_SINGLE_SHIPPING, true);
    				req.serviceLocalParameter(SINGLE, req, res);
    				if (order.getShippingGroups().get(0) instanceof BBBHardGoodShippingGroup) {
    					req.setParameter(
    							SHIP_METHOD_DET,
    							getManager()
    							.getShippingGroupManager()
    							.getShippingMethodForID(
    									((BBBHardGoodShippingGroup) (order
    											.getShippingGroups().get(0)))
    											.getShippingMethod()));
    				}
    			} else {//order does not qualify for single address shippment
    				req.setParameter(IS_MULTI_SHIPPING, true);
    				req.setParameter(SHOW_SINGLE_SHIPPING, false);
    				req.serviceLocalParameter(MULTI, req, res);
    			}    
    		}
    	} catch (BBBSystemException e) {
    		if(isLoggingError()) {
    			logError(LogMessageFormatter.formatMessage(null, "SystemException"),
    					e);
    		}
    	} catch (BBBBusinessException e) {
    		if(isLoggingError()) {
    			logError(LogMessageFormatter.formatMessage(null,
    					"BusinessException"), e);
    		}
    	}
    }        
    
    /**
     * LTL
     * This method is used to multishipping determination logic as a rest service
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public BBBMultishipVO isMultiShippingOrder() throws ServletException, IOException {
    	BBBMultishipVO multishipVO = new BBBMultishipVO();
    	DynamoHttpServletRequest req = ServletUtil.getCurrentRequest();
    	DynamoHttpServletResponse res = ServletUtil.getCurrentResponse();
        final OrderHolder cart = (OrderHolder) req.resolveName(BBBCoreConstants.SHOPPING_CART_PATH);
        req.setParameter(BBBCoreConstants.ORDER, cart.getCurrent());
        req.setParameter("fromMobile", true);
    	this.service(req, res);
    	Boolean isMultiShipping  = (Boolean) req.getObjectParameter(IS_MULTI_SHIPPING);
    	Boolean showSingleShipLink  = (Boolean) req.getObjectParameter(SHOW_SINGLE_SHIPPING);
    	multishipVO.setDefaultMultiship(isMultiShipping);
    	multishipVO.setShowSingleShipLink(showSingleShipLink);
    	return multishipVO;	
    }
    
    
    /**
     * @return mManager
     */
    public final BBBCheckoutManager getManager() {
        return mManager;
    }
    /**
     * @param manager
     */
    public final void setManager(BBBCheckoutManager manager) {
        this.mManager = manager;
    }

}
