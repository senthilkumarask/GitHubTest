package com.bbb.commerce.payment.processor;

import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.processor.ValidatePaymentGroupPipelineArgs;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import com.bbb.commerce.order.PayAtRegister;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.TBSConstants;

/**
 * This class implements OOTB PipelineProcessor and extends BBBGenericService.
 * This is used to validate the PayAtRegister paymentgroup at the time of checkout process.  
 *
 */
public class ValidatePayAtRegister extends BBBGenericService implements PipelineProcessor {

	@Override
	public int[] getRetCodes() {
		int retCodes[] = { 1 };
		return retCodes;
	}

	/**
	 * This method id used to validate the PayAtRegister payment group at the time of checkout.
	 * As we don't have any validations for PayAtRegister, we need to send always 1 as return value.
	 * @param pParam
	 * @param pResult
	 * @return 
	 * @throws Exception
	 * 
	 */
	@Override
	public int runProcess(Object pParam, PipelineResult pResult) throws Exception {
		
		vlogDebug("ValidatePayAtRegister :: runProcess() :: START");
		//As we don't have any validations for PayAtRegister, we need to send always 1 as return value.
        vlogDebug("ValidatePayAtRegister :: runProcess() :: END");  
		return TBSConstants.SUCCESS_CODE;
	}

}
