package com.bbb.commerce.checkout.droplet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.order.Order;
import atg.commerce.order.PipelineConstants;
import atg.commerce.pricing.PricingConstants;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.logging.LogMessageFormatter;



	public class SPCRepriceOrder extends BBBDynamoServlet{
		
		private BBBOrderManager orderManager;



	public BBBOrderManager getOrderManager() {
			return orderManager;
		}



		public void setOrderManager(BBBOrderManager orderManager) {
			this.orderManager = orderManager;
		}



	public void  service(final DynamoHttpServletRequest request,final DynamoHttpServletResponse response) throws ServletException, IOException
	{
		Order order = (Order) request.getObjectParameter(BBBCoreConstants.ORDER);
		
		final BBBOrderImpl bbbOrder = (BBBOrderImpl) order;
		Profile profile = (Profile) request.resolveName(BBBCoreConstants.ATG_PROFILE);
		
		//tax calculation issue
       // if ((bbbOrder.getCouponMap() != null) && (bbbOrder.getCouponMap().size() > 0)) {
        	Map map = new HashMap();
    		map.put("Order", order);
    		map.put("Profile", profile);
    		map.put(PricingConstants.PRICING_OPERATION_PARAM,  PricingConstants.OP_REPRICE_ORDER_TOTAL);
    		try {
    			getOrderManager().getPipelineManager().runProcess("repriceOrder", map);
    		} catch (RunProcessException e) {
    			logError(LogMessageFormatter.formatMessage(null, "RunProcessException in SPC reprice droplet", BBBCoreErrorConstants.ACCOUNT_ERROR_1256 ), e);
    			
    		}
       // }
    		request.serviceParameter("output", request, response);
    		
	}
	
	
 
	}