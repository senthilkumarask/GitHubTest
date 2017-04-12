package com.bbb.commerce.payment.processor;

import atg.commerce.CommerceException;
import atg.commerce.payment.PaymentManagerPipelineArgs;
import atg.commerce.payment.processor.ProcProcessPaymentGroup;
import atg.payment.PaymentStatus;

import com.bbb.commerce.order.PayAtRegisterBeanInfo;
import com.bbb.commerce.payment.BBBPaymentManager;

public class ProcProcessPayAtRegister extends ProcProcessPaymentGroup {

	@Override
	public PaymentStatus authorizePaymentGroup(PaymentManagerPipelineArgs pParams) throws CommerceException {
		
		vlogDebug("ProcessPayAtRegister :: authorizePaymentGroup() :: START");
		PayAtRegisterBeanInfo porInfo = (PayAtRegisterBeanInfo) pParams.getPaymentInfo();

		vlogDebug("ProcessPayAtRegister :: authorizePaymentGroup() :: END");
		return ((BBBPaymentManager) pParams.getPaymentManager()).getPayAtRegisterProcessor().authorize(porInfo);
	}

	@Override
	public PaymentStatus debitPaymentGroup(PaymentManagerPipelineArgs pPParams)
			throws CommerceException {
		/**
		 * This method is not required for PayAtRegister.
		 */
		return null;
	}

	@Override
	public PaymentStatus creditPaymentGroup(PaymentManagerPipelineArgs pPParams)
			throws CommerceException {
		/**
		 * This method is not required for PayAtRegister.
		 */
		return null;
	}

}
