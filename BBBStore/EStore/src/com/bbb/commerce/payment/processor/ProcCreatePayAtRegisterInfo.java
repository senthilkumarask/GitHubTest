package com.bbb.commerce.payment.processor;

import atg.commerce.order.Order;
import atg.commerce.payment.PaymentManagerPipelineArgs;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import com.bbb.commerce.order.PayAtRegister;
import com.bbb.commerce.order.PayAtRegisterBeanInfo;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;

public class ProcCreatePayAtRegisterInfo extends BBBGenericService implements PipelineProcessor {
	
	/**
	 * mPayAtRegisterInfoClass property
	 */
	private String mPayAtRegisterInfoClass;

	/**
	 * @return the payAtRegisterInfoClass
	 */
	public String getPayAtRegisterInfoClass() {
		return mPayAtRegisterInfoClass;
	}

	/**
	 * @param pPayAtRegisterInfoClass the payAtRegisterInfoClass to set
	 */
	public void setPayAtRegisterInfoClass(String pPayAtRegisterInfoClass) {
		mPayAtRegisterInfoClass = pPayAtRegisterInfoClass;
	}

	@Override
	public int[] getRetCodes() {
		int retCodes[] = { 1 };
		return retCodes;
	}

	/**
	 * This method runs the processor to set PayAtRegisterInfo in the
	 * PaymentManagerPipelineArgs for further pipeline calls.
	 * 
	 * @param pParam
	 * @param pResult
	 * @return
	 * @throws Exception
	 */
	@Override
	public int runProcess(Object pParam, PipelineResult pResult) throws Exception {
		
		vlogDebug("ProcCreatePayAtRegistryInfo :: runProcess() :: START");
		PaymentManagerPipelineArgs params = (PaymentManagerPipelineArgs) pParam;
		Order order = params.getOrder();
		PayAtRegister porPaymentGroup = (PayAtRegister) params.getPaymentGroup();
		double amount = params.getAmount();
		
		PayAtRegisterBeanInfo porBeanInfo = getPayAtRegisterInfo();
		addDataToPayAtRegistryInfo(order, porPaymentGroup, amount, params, porBeanInfo);
		
		params.setPaymentInfo(porBeanInfo);
		vlogDebug("ProcCreatePayAtRegistryInfo :: runProcess() :: END");
		return TBSConstants.SUCCESS_CODE;
	}
	
	/**
	 * This method instantiate PaypalInfo.
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws Exception
	 */
	protected PayAtRegisterBeanInfo getPayAtRegisterInfo() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (isLoggingDebug()) {
			logDebug((new StringBuilder()).append("Making a new instance of type: ").append(getPayAtRegisterInfoClass()).toString());
		}

		PayAtRegisterBeanInfo porBeanInfo = (PayAtRegisterBeanInfo) Class.forName(getPayAtRegisterInfoClass()).newInstance();
		return porBeanInfo;
	}
	
	
	/**
	 * This method instantiate PayAtRegisterInfo with information required to
	 * further pipeline execution like auth/debit/credit.
	 * 
	 * @param pOrder
	 * @param pPaymentGroup
	 * @param pAmount
	 * @param pParams
	 * @param pPayAtRegisterInfo
	 */
	protected void addDataToPayAtRegistryInfo(Order pOrder, PayAtRegister pPaymentGroup, double pAmount, PaymentManagerPipelineArgs pParams, PayAtRegisterBeanInfo pPayAtRegisterInfo) {

		vlogDebug("ProcCreatePayAtRegistryInfo :: addDataToPayAtRegistryInfo() :: START");
		
		PayAtRegisterBeanInfo payAtRegisterInfo = pPayAtRegisterInfo;
		payAtRegisterInfo.setOrder((BBBOrderImpl) pOrder);
		payAtRegisterInfo.setPaymentGroupId(pPaymentGroup.getId());
		payAtRegisterInfo.setSiteID(pOrder.getSiteId());
		payAtRegisterInfo.setAmount(pAmount);

		vlogDebug("ProcCreatePayAtRegistryInfo :: addDataToPayAtRegistryInfo() :: END");
	}

}
