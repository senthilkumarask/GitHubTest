package com.bbb.commerce.order;

import com.bbb.common.BBBGenericService;
import com.bbb.constants.TBSConstants;

public class PayAtRegisterProcessorImpl extends BBBGenericService implements PayAtRegisterProcessor {

	@Override
	/**
	 * This method is used for authorizing the PayAtRegister payment group.
	 * We don't have any validations against the PayAtRegister, so by default we need to set the TransactionSuccess as true.
	 */
	public PayAtRegisterStatus authorize(PayAtRegisterBeanInfo pPaymentInfo) {
		
		vlogDebug("PayAtRegisterProcessorImpl :: authorize() method :: START" + pPaymentInfo);
		
		PayAtRegisterStatus payatRegisterStatus = new PayAtRegisterStatus();
		payatRegisterStatus.setStatusMessage(TBSConstants.SUCESS);
		payatRegisterStatus.setTransactionSuccess(true);
		
		vlogDebug("PayAtRegisterProcessorImpl :: authorize() method :: END" + pPaymentInfo);
		return payatRegisterStatus;
	}

}
