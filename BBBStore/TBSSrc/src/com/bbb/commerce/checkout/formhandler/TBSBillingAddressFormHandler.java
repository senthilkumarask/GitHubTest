package com.bbb.commerce.checkout.formhandler;

import java.io.IOException;

import javax.servlet.ServletException;

import com.bbb.ecommerce.order.BBBOrderImpl;

import atg.repository.RepositoryException;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * This component is the extension of BBBBillingAddressFormHandler for
 * overriding few functionality that differs in TBS user flow from normal
 * billing address flow.
 * 
 * @author Sapient Consulting Pvt. Ltd.
 * 
 */
public class TBSBillingAddressFormHandler extends BBBBillingAddressFormHandler {
	/**
	 * This method is overridden because we don't want to modify promotion for
	 * TBS user flow while changing billing address. We just need to run
	 * re-pricing.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws RunProcessException
	 * @throws RepositoryException
	 * @throws IOException
	 * @throws ServletException
	 */
	@Override
	protected void postSaveBillingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws RunProcessException, RepositoryException, ServletException, IOException {

		long startTimeBeforeReprice = System.currentTimeMillis();
		final BBBOrderImpl order = (BBBOrderImpl) this.getOrder();
        this.logInfo("TBSBillingAddressFormHandler.postSaveBillingAddress: Starts" + " start :: orderId : " + order.getId());
        if(null != order.getCouponListVo() && order.getCouponListVo().size() > 0){
			this.logInfo(" available coupons in order : " + order.getCouponListVo().get(0).getmPromoId());
    	}
        
		rePriceOrder(getOrder());
		
        this.logInfo("TBSBillingAddressFormHandler.postSaveBillingAddress: end" + " start :: orderId : " + order.getId());
        if(null != order.getCouponListVo() && order.getCouponListVo().size() > 0){
    			this.logInfo(" available coupons in order : " + order.getCouponListVo().get(0).getmPromoId());
    		}
		logInfo("Total time taken in Repricing of order : " + (System.currentTimeMillis() - startTimeBeforeReprice));
	}
}
