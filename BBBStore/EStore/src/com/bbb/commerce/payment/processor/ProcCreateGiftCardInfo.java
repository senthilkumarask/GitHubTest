package com.bbb.commerce.payment.processor;

import com.bbb.commerce.order.BBBGiftCard;
import com.bbb.payment.giftcard.GenericGiftCardInfo;

import atg.commerce.order.Order;
import atg.commerce.payment.PaymentManagerPipelineArgs;
import com.bbb.common.BBBGenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

public class ProcCreateGiftCardInfo extends BBBGenericService implements
		PipelineProcessor {

	private String mGiftCardInfoClass;

	public ProcCreateGiftCardInfo() {
		//constructor
	}

	/**
	 * @return the mGiftCardInfoClass
	 */
	public String getGiftCardInfoClass() {
		return mGiftCardInfoClass;
	}

	/**
	 * @param pGiftCardInfoClass
	 *            the mGiftCardInfoClass to set
	 */
	public void setGiftCardInfoClass(String pGiftCardInfoClass) {
		mGiftCardInfoClass = pGiftCardInfoClass;
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
	protected void addDataToGiftCardInfo(Order pOrder,
			BBBGiftCard pPaymentGroup, double pAmount,
			PaymentManagerPipelineArgs pParams,
			GenericGiftCardInfo pGenericGiftCardInfo) {
		logDebug("Starting method ProcCreateGiftCardInfo.addDataToGiftCardInfo");
		pGenericGiftCardInfo.setGiftCardNumber(pPaymentGroup.getCardNumber());
		pGenericGiftCardInfo.setPin(pPaymentGroup.getPin());
		pGenericGiftCardInfo.setOrderID(pOrder.getId());
		pGenericGiftCardInfo.setPaymentGroupId(pPaymentGroup.getId());
		//pGenericGiftCardInfo.setSite(SiteContextManager.getCurrentSite());
		pGenericGiftCardInfo.setSiteId(pOrder.getSiteId());
		pGenericGiftCardInfo.setBalance(pPaymentGroup.getBalance());
		pGenericGiftCardInfo.setAmount(pAmount);
		logDebug("Exiting method ProcCreateGiftCardInfo.addDataToGiftCardInfo");

	}

	/**
	 * This method instantiate GiftCardInfo.
	 * 
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws Exception
	 */
	protected GenericGiftCardInfo getGiftCardInfo() throws InstantiationException, IllegalAccessException, ClassNotFoundException {

		logDebug((new StringBuilder())
					.append("Making a new instance of type: ")
					.append(getGiftCardInfoClass()).toString());
	
		GenericGiftCardInfo giftCardInfo = (GenericGiftCardInfo) Class.forName(
				getGiftCardInfoClass()).newInstance();
		return giftCardInfo;
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
	public int runProcess(Object pParam, PipelineResult pResult)
			throws Exception {
		logDebug("Starting method ProcCreateGiftCardInfo.runProcess");	
		PaymentManagerPipelineArgs params = (PaymentManagerPipelineArgs) pParam;
		Order order = params.getOrder();
		BBBGiftCard giftCard = (BBBGiftCard) params.getPaymentGroup();
		double amount = params.getAmount();
		GenericGiftCardInfo giftCardInfo = getGiftCardInfo();
		addDataToGiftCardInfo(order, giftCard, amount, params, giftCardInfo);
		logDebug((new StringBuilder())
					.append("Putting GiftCardInfo object into pipeline: ")
					.append(giftCardInfo.toString()).toString());	
		params.setPaymentInfo(giftCardInfo);
		logDebug("Exiting method ProcCreateGiftCardInfo.runProcess");

		return 1;
	}

	public int[] getRetCodes() {
		int retCodes[] = { 1 };
		return retCodes;
	}

}
