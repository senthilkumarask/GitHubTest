package com.bbb.commerce.cart.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.commerce.promotion.PromotionConstants;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
/**
 * This servlet parses all commerceitems and fills the registryMap in order.
 * @author nagarg
 *
 */
public class CartRegistryInfoDroplet extends BBBDynamoServlet {
    
	private GiftRegistryManager mRegistryManager;
	
	public static final ParameterName ERROR_OPARAM = ParameterName.getParameterName("error");
    
    public GiftRegistryManager getRegistryManager() {
        return this.mRegistryManager;
    }    
    
    public void setRegistryManager(GiftRegistryManager registryManager) {
        this.mRegistryManager = registryManager;
    }
    
    /**
     * This droplet will build and add registry map to order
     */
    public void service(DynamoHttpServletRequest req,
            DynamoHttpServletResponse res) throws ServletException, IOException {
        
        BBBOrderImpl order = (BBBOrderImpl) req.getObjectParameter(BBBCoreConstants.ORDER);
        if(order != null){
        	this.getRegistryManager().populateRegistryMapInOrder(req, order);
        }else {
			logDebug("invalid input parameters");
			//input params are null log error and return
			String msg = PromotionConstants.getStringResource("invalidTypeParameter");
		    logError(msg);
		    req.setParameter("errorMsg", msg);
		    req.serviceLocalParameter(ERROR_OPARAM, req, res);
		    return;		
		}
    }
}
