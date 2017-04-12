package com.bbb.commerce.checkout.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;

import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;

@Deprecated public class DisplayBillingDroplet extends BBBDynamoServlet{
	
	@Deprecated public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException, IOException{
		BBBOrder order = (BBBOrder) pRequest.getObjectParameter(BBBCoreConstants.ORDER);
		if(isLoggingDebug()){
			logDebug("commerce item count is " + order.getCommerceItemCount());
		}
		if(null != order && order.getCommerceItemCount() > 0){
			pRequest.serviceLocalParameter(BBBCheckoutConstants.DISPLAY_BILLING, pRequest, pResponse);
		}else{
			pRequest.serviceLocalParameter(BBBCheckoutConstants.DISPLAY_CART, pRequest, pResponse);
		}
	}
}
