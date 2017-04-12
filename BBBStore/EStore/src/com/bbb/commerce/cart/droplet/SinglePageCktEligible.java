package com.bbb.commerce.cart.droplet;

import java.io.IOException;
import javax.servlet.ServletException;


import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.profile.session.BBBSessionBean;

/**
 * This servlet will get the upsell items for the given cart
 * @author dwaghmare
 *
 */
public class SinglePageCktEligible extends BBBDynamoServlet {
	private BBBCheckoutManager checkoutManager;
	private static String SPC_ELIGIBLE = "singlePageEligible";
	
    public BBBCheckoutManager getCheckoutManager() {
		return checkoutManager;
	}

	public void setCheckoutManager(BBBCheckoutManager checkoutManager) {
		this.checkoutManager = checkoutManager;
	}

	public void service(DynamoHttpServletRequest req,
            DynamoHttpServletResponse res) throws ServletException, IOException {
    	BBBOrderImpl order = (BBBOrderImpl)req.getObjectParameter(BBBCoreConstants.ORDER); 
    	BBBSessionBean sessionBean = (BBBSessionBean) req.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
    	boolean spcEligible = false;
    	boolean ordercContainsLTLItem=false;
    	boolean result = false;
    	
    	int shippingGrpCnt = order.getShippingGroupCount();
    	// second argument is false - as no need to check for single page functionality for mobile
    	result =getCheckoutManager().displaySingleShipping(order, false);
    	try {	
			ordercContainsLTLItem=getCheckoutManager().orderContainsLTLItem(order);
		} catch (BBBSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BBBBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(shippingGrpCnt ==1 && !ordercContainsLTLItem && result){
			spcEligible =true;			
		}
		
		req.setParameter(SPC_ELIGIBLE, spcEligible);
        req.serviceLocalParameter("output", req, res);
		
    }
}