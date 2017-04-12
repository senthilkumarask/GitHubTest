package com.bbb.commerce.order;

import java.util.List;

import atg.commerce.CommerceException;
import atg.commerce.order.PaymentGroup;

import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.order.bean.BBBOrderPriceInfo;
import com.bbb.utils.BBBUtility;

public class TBSPaymentGroupManager extends BBBPaymentGroupManager {
	
	/**
	 * This method is used to create a new Pay on registry payment and set the payment method details.
	 * @param pOrder - the order
	 * @throws CommerceException - if any CommerceException
	 * 
	 */
	public PayAtRegister createPayAtRegistryPayment(BBBOrder pOrder) throws CommerceException {

		vlogDebug("TBSPaymentGroupManager :: createPayAtRegistryPayment() method :: Start ");

		//Creating pay at registry payment group
		PayAtRegister payatregister = null;
		
		List<PaymentGroup> paymentGroups = pOrder.getPaymentGroups();
		
		for (PaymentGroup paymentGroup : paymentGroups) {
			if(paymentGroup instanceof PayAtRegister){
				payatregister = (PayAtRegister) paymentGroup;
			}
		}

        double orderTotal = BBBUtility.round(((BBBOrderPriceInfo) pOrder.getPriceInfo()).getTotal());
        
        if(payatregister == null){
	        PaymentGroup paymentGroup = this.createPaymentGroup(TBSConstants.PAY_AT_REGISTER);
	        if (paymentGroup instanceof PayAtRegister) {
	            payatregister = (PayAtRegister) paymentGroup;
	        }
        }

        payatregister.setAmount(orderTotal);
        vlogDebug("TBSPaymentGroupManager :: createPayAtRegistryPayment() method :: Start ");
        return payatregister;
	}

}
