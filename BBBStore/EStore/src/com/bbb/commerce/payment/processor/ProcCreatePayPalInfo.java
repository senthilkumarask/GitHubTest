package com.bbb.commerce.payment.processor;

import atg.commerce.order.Order;
import atg.commerce.payment.PaymentManagerPipelineArgs;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import com.bbb.commerce.order.Paypal;
import com.bbb.commerce.order.PaypalBeanInfo;
import com.bbb.common.BBBGenericService;
import com.bbb.ecommerce.order.BBBOrderImpl;

public class ProcCreatePayPalInfo extends BBBGenericService implements PipelineProcessor {

	private String payPalInfoClass;

	public ProcCreatePayPalInfo() {
		//constructor
	}

	/**
	 * @return the payPalInfoClass
	 */
	public String getPayPalInfoClass() {
		return payPalInfoClass;
	}

	/**
	 * @param payPalInfoClass
	 *            the payPalInfoClass to set
	 */
	public void setPayPalInfoClass(String payPalInfoClass) {
		this.payPalInfoClass = payPalInfoClass;
	}

	/**
	 * This method instantiate GenericGiftCardInfo with information required to
	 * further pipeline execution like auth/debit/credit.
	 * 
	 * @param pOrder
	 * @param pPaymentGroup
	 * @param pAmount
	 * @param pParams
	 * @param pGenericGiftCardInfo
	 */
	protected void addDataToPayPalInfo(Order pOrder, Paypal pPaymentGroup, double pAmount, PaymentManagerPipelineArgs pParams, PaypalBeanInfo pGenericPayPalInfo) {
		logDebug("Starting method ProcCreateGiftCardInfo.addDataToPayPalInfo");
		pGenericPayPalInfo.setOrder((BBBOrderImpl) pOrder);
		pGenericPayPalInfo.setPaymentGroupId(pPaymentGroup.getId());
		pGenericPayPalInfo.setSiteID(pOrder.getSiteId());
		pGenericPayPalInfo.setAmount(pAmount);
		logDebug("Exiting method ProcCreateGiftCardInfo.addDataToPayPalInfo");
		

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
	protected PaypalBeanInfo getPayPalInfo() throws InstantiationException, IllegalAccessException, ClassNotFoundException {

		logDebug((new StringBuilder()).append("Making a new instance of type: ").append(getPayPalInfoClass()).toString());
		PaypalBeanInfo paypalBeanInfo = (PaypalBeanInfo) Class.forName(getPayPalInfoClass()).newInstance();
		return paypalBeanInfo;
	}

	/**
	 * This method runs the processor to set GiftCardInfo in the
	 * PaymentManagerPipelineArgs for further pipeline calls.
	 * 
	 * @param pParam
	 * @param pResult
	 * @return
	 * @throws Exception
	 */
	public int runProcess(Object pParam, PipelineResult pResult) throws Exception {

	
		logDebug("Starting method ProcCreatePayPalInfo.runProcess");
		PaymentManagerPipelineArgs params = (PaymentManagerPipelineArgs) pParam;
		Order order = params.getOrder();
		Paypal payPal = (Paypal) params.getPaymentGroup();
		double amount = params.getAmount();
		PaypalBeanInfo PaypalInfo = getPayPalInfo();
		addDataToPayPalInfo(order, payPal, amount, params, PaypalInfo);
		logDebug((new StringBuilder()).append("Putting GiftCardInfo object into pipeline: ").append(PaypalInfo.toString()).toString());
		params.setPaymentInfo(PaypalInfo);
		logDebug("Exiting method ProcCreateGiftCardInfo.runProcess");
		
		return 1;
	}

	public int[] getRetCodes() {
		int retCodes[] = { 1 };
		return retCodes;
	}

}
