package com.bbb.commerce.order.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.OrderHolder;
import atg.multisite.SiteContextManager;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.api.BBBAddressAPI;
import com.bbb.account.api.BBBAddressVO;
import com.bbb.account.order.manager.OrderDetailsManager;
import com.bbb.commerce.cart.bean.CommerceItemDisplayVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.common.BBBOrderVO;
import com.bbb.commerce.common.ExpressCheckoutVO;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;

public class DisplayExpressCheckoutDroplet extends BBBDynamoServlet {
    
    private BBBCheckoutManager mManager;
    private BBBInventoryManager inventoryManager; 
    private OrderDetailsManager orderDetailsManager;
    private BBBAddressAPI addressApi;
    private BBBCatalogTools catalogTools;
    public BBBInventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public void setInventoryManager(BBBInventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

	public static final String DISABLE_EXPRESS_CHECKOUT = "disableExpressCheckout";
    private static final String ATG_COMMERCE_SHOPPING_CART = "/atg/commerce/ShoppingCart";
	public final static ParameterName TRUE  = ParameterName.getParameterName("true");
    public final static ParameterName FALSE = ParameterName.getParameterName("false");
    
    @Override
    public void service(DynamoHttpServletRequest req,
            DynamoHttpServletResponse res) throws ServletException, IOException {
        Profile profile = (Profile) req.getObjectParameter(BBBCoreConstants.PROFILE);
        BBBOrder order = (BBBOrder) req.getObjectParameter(BBBCoreConstants.ORDER);
        String siteId = SiteContextManager.getCurrentSiteId();
        
        try {
            if(getManager().displayExpressCheckout(profile, order, siteId)) {
            	try {
        			if(getManager().checkRestrictedZip(profile, order, siteId)){
        				req.serviceLocalParameter(TRUE, req, res);
        			}else{
        				req.serviceLocalParameter(FALSE, req, res);
        			}
        		} catch (BBBSystemException e1) {
        			 logError(LogMessageFormatter.formatMessage(req, "Error getting default shipping address information"), e1);
        		} catch (BBBBusinessException b1) {
        			 logError(LogMessageFormatter.formatMessage(req, "Error getting default shipping address information"), b1);
        		}
            }
        } catch (Exception e) {
            logError(LogMessageFormatter.formatMessage(req, "Error getting default shipping/billing/creditcard information"), e);
            return;
        } 
        
    }
    /**
     * Method to check for mobile express checkout condition
     * @return ExpressCheckoutVO
     * @throws BBBSystemException
     * @throws BBBBusinessException
     * @throws PropertyNotFoundException
     * @throws CommerceException 
     */
    public ExpressCheckoutVO getExpressCheckoutCondition() throws BBBSystemException, BBBBusinessException, PropertyNotFoundException, CommerceException
    {
    	logDebug("DisplayExpressCheckoutDroplet.getExpressCheckoutCondition() method starts");
		
		boolean isExpressCheckoutEnabled=false;
		boolean checkRestrictedZip=false;
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		
		final Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
        // Order inSessionOrder = ((BBBOrder) getOrder());
        final OrderHolder cart = (OrderHolder) pRequest.resolveName(ATG_COMMERCE_SHOPPING_CART);
        final BBBOrderImpl inSessionOrder = (BBBOrderImpl) cart.getCurrent();
        String siteId = SiteContextManager.getCurrentSiteId();
        
        pRequest.setParameter(BBBCoreConstants.PROFILE, profile);
		pRequest.setParameter(BBBCoreConstants.ORDER, inSessionOrder);
		
		if(getManager().checkRestrictedZip(profile, inSessionOrder, siteId)){
			checkRestrictedZip =  true;
		}else{
			checkRestrictedZip =  false;
		}
		logDebug("Check for RestrictedZip returned: ["+checkRestrictedZip+"]");
		//Check for State level restriction if zip level restriction is off
		if(!checkRestrictedZip)
		{
			checkRestrictedZip=checkRestrictedState(inSessionOrder,profile,siteId);
			logDebug("Check for Restricted State returned: ["+checkRestrictedZip+"]");
		}
		
		if(getManager().displayExpressCheckout(profile, inSessionOrder, siteId))
		{
			if(checkInventory(pRequest,inSessionOrder))
			{
				isExpressCheckoutEnabled = true;
			}
			logDebug("Check for Express Checkout returned: ["+isExpressCheckoutEnabled+"]");
		}
		ExpressCheckoutVO checkoutVO = new ExpressCheckoutVO();
		checkoutVO.setExpressCheckoutEnabled(isExpressCheckoutEnabled);
		checkoutVO.setCheckRestrictedZip(checkRestrictedZip);
		
		return checkoutVO;
	}
    
    /** @param pRequest DynamoHttpServletRequest  */
   	private boolean checkInventory(final DynamoHttpServletRequest pRequest,
   			BBBOrder order) {
   		logDebug("DisplayExpressCheckoutDroplet.checkRestrictedState() Checking for inventory Size for SKUs in Cart");
   		@SuppressWarnings("unchecked")
   		final List<CommerceItem> commerceItems = order.getCommerceItems();
   		int inventoryResult = 0;
   		BBBCommerceItem bbbItem = null;

   		for (final CommerceItem item : commerceItems) {
   			if (item instanceof BBBCommerceItem) {
   				bbbItem = (BBBCommerceItem) item;
   				// Skip BOPUS items. Check inventory for VDC items
   				if ((bbbItem.getStoreId() == null)) {
   					try {
   						inventoryResult = this.getInventoryManager()
   								.getProductAvailability(order.getSiteId(),
   										bbbItem.getCatalogRefId(), BBBInventoryManager.RETRIEVE_CART, bbbItem.getQuantity());
   						// If inventory is not available
   						if (inventoryResult != BBBInventoryManager.AVAILABLE && inventoryResult != BBBInventoryManager.LIMITED_STOCK) {
   							return false;
   						}
   					} catch (BBBBusinessException e) {
						logError("Exception while checking Inventory for Displaying Mobile Express Checkout"+e);
					} catch (BBBSystemException e) {
						logError("Exception while checking Inventory for Displaying Mobile Express Checkout"+e);
					}
   				}
   			}
   		}
   		return true;
   	}
   	
   	private boolean checkRestrictedState(BBBOrderImpl order,Profile profile,String siteId) throws BBBSystemException, BBBBusinessException, CommerceException
   	{

		logDebug("DisplayExpressCheckoutDroplet.checkRestrictedState() Checking for restricted States");
		
		//Fetching list of restricted states for SKUs in order
		StringBuilder restrictedState=new StringBuilder();
		BBBOrderVO bbbOrderVO = this.getOrderDetailsManager().getOrderDetailsVO(order, false);
		for(CommerceItemDisplayVO itemVO:bbbOrderVO.getCommerceItemVOList())
		{
			if(StringUtils.isBlank(restrictedState.toString()))
			{
				restrictedState.append(itemVO.getCommaSepNonShipableStates());
			}else
			{
				restrictedState.append(BBBCoreConstants.COMMA);
				restrictedState.append(itemVO.getCommaSepNonShipableStates());
			}
			
		}
		
		//Fetching Default address of profile
		final BBBAddressVO defaultAddress = this.getProfileAddressTool().getDefaultShippingAddress(profile, siteId);
		
   		
   		//Storing list of states based on comma separation
   		List<String> states = new ArrayList<String>();
   		if(StringUtils.contains(restrictedState.toString(), ','))
   		{
   			for(String state: StringUtils.split(restrictedState.toString(), ','))
   			{
   				states.add(state);
   			}
   		}
   		else
   		{
   			states.add(restrictedState.toString());
   		}
   		
   		//Get State Codes for all the states
		List<StateVO> stateList=getCatalogTools().getStateList();
		
   		for(String state:states)
   		{
   			for(StateVO stateVO:stateList)
 {
				if (StringUtils.equalsIgnoreCase(stateVO.getStateName(), state)
						&& StringUtils.equalsIgnoreCase(stateVO.getStateCode(),
								defaultAddress.getState())) {
					return true;
				}
			}
   		}
   		return false;
   	}
   	
   	
    public final BBBCheckoutManager getManager() {
        return mManager;
    }
    public final void setManager(BBBCheckoutManager manager) {
        this.mManager = manager;
    }
    
    public final OrderDetailsManager getOrderDetailsManager() {
		return this.orderDetailsManager;
	}

	/** @param orderDetailsManager the orderDetailsManager to set */
	public final void setOrderDetailsManager(final OrderDetailsManager orderDetailsManager) {
		this.orderDetailsManager = orderDetailsManager;
	}
	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}

	/**
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	
	/** @return */
    public final BBBAddressAPI getProfileAddressTool() {
        return this.addressApi;
    }

    /** @param mAddressTool */
    public final void setProfileAddressTool(final BBBAddressAPI mAddressTool) {
        this.addressApi = mAddressTool;
    }

}
