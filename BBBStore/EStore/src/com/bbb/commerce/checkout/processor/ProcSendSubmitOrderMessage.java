package com.bbb.commerce.checkout.processor;

import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.PipelineConstants;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import com.bbb.commerce.checkout.tibco.BBBSubmitOrderHandler;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.constants.BBBTagConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;

public class ProcSendSubmitOrderMessage extends ApplicationLoggingImpl implements PipelineProcessor {

	/**
	 * Final Static variable for success.
	 */
	private static final int SUCCESS = 1;
	private static final int FAILURE = 2;

	static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

	/**
	 * Resource bundle
	 */
	private static ResourceBundle sResourceBundle = LayeredResourceBundle.getBundle("atg.commerce.order.OrderResources");

	private String mLoggingIdentifier = "ProcSendSubmitOrderMessage";

	/**
	 * Order Helper instance for the scheduler to work with Orders
	 */
	private BBBSubmitOrderHandler mOrderHelper;

	/**
	 * Overriden method of PipelineProcessor which indicated the return code for
	 * run process method.
	 * 
	 * @return Int: Array of int .
	 */
	public int[] getRetCodes() {
		int[] retn = { SUCCESS, FAILURE };
		return retn;
	}

	/**
	 * Sets property LoggingIdentifier
	 */
	public void setLoggingIdentifier(String pLoggingIdentifier) {
		mLoggingIdentifier = pLoggingIdentifier;
	}

	/**
	 * Returns property LoggingIdentifier
	 */
	public String getLoggingIdentifier() {
		return mLoggingIdentifier;
	}

	/**
	 * @return the orderHelper
	 */
	public final BBBSubmitOrderHandler getOrderHelper() {
		return mOrderHelper;
	}

	/**
	 * @param pOrderHelper
	 *            the orderHelper to set
	 */
	public final void setOrderHelper(BBBSubmitOrderHandler pOrderHelper) {
		mOrderHelper = pOrderHelper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atg.service.pipeline.PipelineProcessor#runProcess(java.lang.Object,
	 * atg.service.pipeline.PipelineResult)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public int runProcess(Object pParam, PipelineResult pResult) throws Exception {
		long startTime = System.currentTimeMillis();
		if (isLoggingInfo()) {
			logInfo("Start time recorded as " + startTime);
			
		}
		HashMap map = (HashMap) pParam;
		HttpServletRequest request = (HttpServletRequest) map.get(PipelineConstants.REQUEST);
		BBBOrder bbbOrder = (BBBOrder) map.get(PipelineConstants.ORDER);

		if (bbbOrder == null) {
			throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter", MY_RESOURCE_NAME, sResourceBundle));
		}

		if (isLoggingDebug()) {
			logDebug("START: Processing Submit Order pipeline event for order [" + bbbOrder.getId() + "]");
		}

		BBBPerformanceMonitor.start(BBBPerformanceConstants.SUBMIT_ORDER_PROCESS, "runProcess");

		try {
			String orderSubmissionTagOn = BBBCoreConstants.FALSE;
			String content = getOrderHelper().getCatalogTools().getThirdPartyTagStatus(bbbOrder.getSiteId(), getOrderHelper().getCatalogTools(), BBBTagConstants.ORDER_SUBMISSION_TAG);
			if (!StringUtils.isBlank(content)) {
				orderSubmissionTagOn = content;
			}
			if (orderSubmissionTagOn.equalsIgnoreCase(BBBCoreConstants.TRUE)) {
				if(!(bbbOrder.getSubStatus().equalsIgnoreCase(BBBCoreConstants.ORDER_SUBSTATUS_DUMMY_RESTORE_INVENTORY)|| bbbOrder.getSubStatus().equalsIgnoreCase(BBBCoreConstants.ORDER_SUBSTATUS_DUMMY_IGNORE_INVENTORY) )){
				getOrderHelper().processSubmitOrder(bbbOrder, request);
				}
				
				
			}
		} catch (Exception e) {
			if (isLoggingDebug()) {
				logDebug(BBBCoreErrorConstants.CHECKOUT_ERROR_1009 + ": Error while sending Inventory decrement from 'Send Submit Order Message' pipeline request", e);
			}
		}
		boolean decInvInNewThread = false;
		try {
		
			List decInvInNewThreadList = getOrderHelper().getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "Decrement_Inventory_In_New_Thread");
			if (!BBBUtility.isListEmpty(decInvInNewThreadList)) {
				decInvInNewThread = Boolean.parseBoolean((String) decInvInNewThreadList.get(0));
			}
		} catch(BBBBusinessException be) {
			logError("BBBBusinessException occurred while fetching config key value :: Decrement_Inventory_In_New_Thread " + be);
		} catch(BBBSystemException se) {
			logError("BBBSystemException occurred while fetching config key value :: Decrement_Inventory_In_New_Thread " + se);
		}
		
		if (decInvInNewThread) {
			decrementInventory(pParam);
		} else {
			
			try {
				String jmsInvDecEnabled = BBBCoreConstants.TRUE;
				List<String> config = getOrderHelper().getCatalogTools().getContentCatalogConfigration("JMSInventoryDec");
				if (config.size() > 0) {
					jmsInvDecEnabled = config.get(0);
				}

				if(!bbbOrder.getSubStatus().equalsIgnoreCase(BBBCoreConstants.ORDER_SUBSTATUS_DUMMY_IGNORE_INVENTORY)){

					if(!isInternationalOrder(bbbOrder.getSalesChannel()))
					{
						if (jmsInvDecEnabled != null && Boolean.parseBoolean(jmsInvDecEnabled)) {
							getOrderHelper().submitInventoryMesssage(bbbOrder);
						} else {
							getOrderHelper().decrementInventoryRepository(bbbOrder);
						}
					}
				}
				
			} 
			catch (Exception e) {
				if (isLoggingDebug()) {
					logDebug(BBBCoreErrorConstants.CHECKOUT_ERROR_1009 + ": Error while sending Inventory decrement from 'Send Submit Order Message' pipeline request", e);
				}
			}
		}
		try {
			if(!(bbbOrder.getSubStatus().equalsIgnoreCase(BBBCoreConstants.ORDER_SUBSTATUS_DUMMY_RESTORE_INVENTORY)|| bbbOrder.getSubStatus().equalsIgnoreCase(BBBCoreConstants.ORDER_SUBSTATUS_DUMMY_IGNORE_INVENTORY) )){
				if(!isInternationalOrder(bbbOrder.getSalesChannel()))
				{
					getOrderHelper().sendMail(bbbOrder, request);
				}
			}
		} catch (Exception e) {
			if (isLoggingError()) {
				logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1009 + ": Error while sending email from 'Send Submit Order Message' pipeline request", e);
			}
		}

		BBBPerformanceMonitor.end(BBBPerformanceConstants.SUBMIT_ORDER_PROCESS, "runProcess");

		if (isLoggingDebug()) {
			logDebug("END: Processing Submit Order pipeline event for order [" + bbbOrder.getId() + "]");
		}
		
		if (isLoggingInfo()) {
			logInfo("Total Time of ProcSendSubmitOrderMessage.runProcess() method:" + (System.currentTimeMillis() - startTime));
			logInfo("Process executing in thread : " + Thread.currentThread().getId());
		}
		return SUCCESS;
	}

	/**this method is used for making the order update inventory call to run in another thread making it asynchronous(BBBJ-881)
	 * @param pParam
	 */
	private void decrementInventory(Object pParam) {
		
		JMSInvDecProcessingThread newJmsProc = new JMSInvDecProcessingThread(pParam);
		Thread newJMSThread = new Thread(newJmsProc);
		newJMSThread.start();
		
	}
	public boolean isInternationalOrder(String saleChannel) {
		boolean isInternationalOrder = false;
		if (BBBInternationalShippingConstants.CHANNEL_DESKTOP_BFREE
				.equalsIgnoreCase(saleChannel)
				|| BBBInternationalShippingConstants.CHANNEL_MOBILE_APP_BFREE
						.equalsIgnoreCase(saleChannel)
				|| BBBInternationalShippingConstants.CHANNEL_MOBILE_BFREE
						.equalsIgnoreCase(saleChannel)) {
			isInternationalOrder = true;
		}
		return isInternationalOrder;
	}
	
	private class JMSInvDecProcessingThread implements Runnable{
		
		private Object tObj;
		JMSInvDecProcessingThread(Object obj){
			this.tObj = obj; 
		}

		@Override
		public void run() {
			long startTime = System.currentTimeMillis();
			logInfo("Start time recorded as " + startTime);
			HashMap map = (HashMap) tObj;
			BBBOrder bbbOrder = (BBBOrder) map.get(PipelineConstants.ORDER);
			logInfo("Name of the current thread : " + Thread.currentThread().getId());
			try {
				String jmsInvDecEnabled = BBBCoreConstants.TRUE;
				List<String> config = getOrderHelper().getCatalogTools().getContentCatalogConfigration("JMSInventoryDec");
				if (config.size() > 0) {
					jmsInvDecEnabled = config.get(0);
				}

				if(!bbbOrder.getSubStatus().equalsIgnoreCase(BBBCoreConstants.ORDER_SUBSTATUS_DUMMY_IGNORE_INVENTORY)){

					if(!isInternationalOrder(bbbOrder.getSalesChannel()))
					{
						if (jmsInvDecEnabled != null && Boolean.parseBoolean(jmsInvDecEnabled)) {
							getOrderHelper().submitInventoryMesssage(bbbOrder);
						} else {
							getOrderHelper().decrementInventoryRepository(bbbOrder);
						}
					}
				}
				logInfo("Total Time of JMSInvDecProcessingThread.run() method:" + (System.currentTimeMillis() - startTime));
			} 
			catch (Exception e) {
				if (isLoggingDebug()) {
					logDebug(BBBCoreErrorConstants.CHECKOUT_ERROR_1009 + ": Error while sending Inventory decrement from 'Send Submit Order Message' pipeline request", e);
				}
			}
		}
	}
	
}