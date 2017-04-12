package com.bbb.payment.droplet;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.order.BBBGiftCard;
import com.bbb.commerce.order.BBBPaymentGroupManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.order.bean.BBBOrderPriceInfo;

/**
 * This Droplet performs gift card related front end operations.
 * 
 * @author vagra4
 * 
 */
public class BBBPaymentGroupDroplet extends BBBDynamoServlet {

	private BBBPaymentGroupManager mPaymentGroupManager;

	private String mErrorPageURL;

	/**
	 * Service method returns Gift card payment group details.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
	    BBBPerformanceMonitor.start("BBBPaymentGroupDroplet", "service");
		String serviceType = (String) pRequest
				.getLocalParameter(BBBCheckoutConstants.SERVICE_TYPE);

		
		logDebug("Starting method BBBPaymentGroupDroplet.service, serviceType= "
					+ serviceType);
		

		if (BBBCheckoutConstants.GIFT_DET_SERVICE.equals(serviceType)) {
			getGiftCardDetailService(pRequest, pResponse);
		}

		if (BBBCheckoutConstants.GET_PG_ST_ON_LOAD
				.equals(serviceType)) {
			getPaymentGroupStatusOnLoad(pRequest, pResponse);
		}

		
		logDebug("Exiting method BBBPaymentGroupDroplet.service");
		
		BBBPerformanceMonitor.end("BBBPaymentGroupDroplet", "service");
	}

	/**
	 * This method checks that GiftCard payment group is available or not.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws IOException
	 * @throws ServletException
	 */
	@SuppressWarnings("rawtypes")
	public void getPaymentGroupStatusOnLoad(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		
		logDebug("Starting method BBBPaymentGroupDroplet.getPaymentGroupStatusOnLoad");
		
		

		String methodName = BBBCheckoutConstants.GET_PAY_GRP_STATUS_LOAD;
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.BBB_PAYMENTGROUP_DROPLET, methodName);
		
		Order order = null;
		Object orderObj = pRequest
				.getLocalParameter(BBBCheckoutConstants.ORDER);

		TransactionDemarcation td = new TransactionDemarcation();
		boolean rollback = true;

		
		try{
			
			TransactionManager tm = getPaymentGroupManager()
					.getTransactionManager();
			if (tm != null) {
				td.begin(tm, TransactionDemarcation.REQUIRED);
			}
			
			if (orderObj instanceof Order) {	
				try {
					order = (Order) orderObj;
					List paymentGroups = order.getPaymentGroups();
					boolean giftCardGroup = false;
					for (Iterator iterator = paymentGroups.iterator(); iterator
							.hasNext();) {
						PaymentGroup pg = (PaymentGroup) iterator.next();
						if(pg instanceof BBBGiftCard) {
							giftCardGroup = true;
							break;
						}
					}
					
					if(!giftCardGroup) {
						pRequest.setParameter(BBBCheckoutConstants.IS_GIFTCARDS,
								false);
						pRequest.setParameter(
								BBBCheckoutConstants.IS_OD_AMT_COV,
								false);
						pRequest.setParameter(
								BBBCheckoutConstants.IS_MAX_GC_AD,
								false);
						pRequest.serviceLocalParameter(BBBCheckoutConstants.OUTPUT,
								pRequest, pResponse);
						BBBPerformanceMonitor.end(
								BBBPerformanceConstants.BBB_PAYMENTGROUP_DROPLET, methodName);
		
						return;
		
					}
					
					Boolean[] gcInfo = null;
				
				
					synchronized (order) {
						gcInfo = getPaymentGroupManager()
								.processPaymentGroupStatusOnLoad(order);
						pRequest.setParameter(BBBCheckoutConstants.IS_GIFTCARDS,
								gcInfo[0]);
						pRequest.setParameter(
								BBBCheckoutConstants.IS_OD_AMT_COV,
								gcInfo[1]);
						pRequest.setParameter(
								BBBCheckoutConstants.IS_MAX_GC_AD,
								gcInfo[2]);
						pRequest.serviceLocalParameter(BBBCheckoutConstants.OUTPUT,
								pRequest, pResponse);
	
						rollback = false;
					}
				} catch (CommerceException comException) {
					
						logError(
								"Commerce Excpetion during getPaymentGroupStatusOnLoad method",
								comException);
					
					pResponse.sendRedirect(getErrorPageURL());
					BBBPerformanceMonitor.cancel(
							BBBPerformanceConstants.BBB_PAYMENTGROUP_DROPLET, methodName);
				} 
							
			}
		
			
		}catch (TransactionDemarcationException tranDemException) {
				
					logError(
							"TransactionDemarcation Excpetion during getPaymentGroupStatusOnLoad method",
							tranDemException);
				
				pResponse.sendRedirect(getErrorPageURL());
				BBBPerformanceMonitor.cancel(
						BBBPerformanceConstants.BBB_PAYMENTGROUP_DROPLET, methodName);
		} finally {
				try {
					td.end(rollback);
				} catch (TransactionDemarcationException tranDemException) {
					
						logError(
								"TransactionDemarcation Excpetion during getPaymentGroupStatusOnLoad method",
								tranDemException);
					
					pResponse.sendRedirect(getErrorPageURL());
					BBBPerformanceMonitor.cancel(
							BBBPerformanceConstants.BBB_PAYMENTGROUP_DROPLET, methodName);
				}
				BBBPerformanceMonitor.end(BBBPerformanceConstants.BBB_PAYMENTGROUP_DROPLET,
						methodName);
			}		
		
			logDebug("Exiting method BBBPaymentGroupDroplet.getPaymentGroupStatusOnLoad");
		
	}

	/**
	 * This method returns Gift card payment group details.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	public void getGiftCardDetailService(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		
			logDebug("Starting method BBBPaymentGroupDroplet.getGiftCardDetailService");
		

		String methodName = BBBCheckoutConstants.GET_GIFTCARD_DETAILSERVICE;
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.BBB_PAYMENTGROUP_DROPLET, methodName);
		
		Order order = null;
		Object orderObj = pRequest
				.getLocalParameter(BBBCheckoutConstants.ORDER);
		double orderTotal = 0.0;		
		if (orderObj instanceof Order) {
			order = (Order) orderObj;
			List<PaymentGroup> paymentGrp = getPaymentGroupManager()
					.getPaymentGroups(order, BBBCheckoutConstants.GIFTCARD);

			pRequest.setParameter(
					BBBCheckoutConstants.COVEREDBYGC,
					getPaymentGroupManager().getAmountCoveredByGiftCard(
							paymentGrp, order));
			pRequest.setParameter(BBBCheckoutConstants.GIFTCARDS, paymentGrp);
			
			if(null != order.getPriceInfo()){
				orderTotal = ((BBBOrderPriceInfo) order.getPriceInfo()).getTotal();
			}
			pRequest.setParameter(BBBCheckoutConstants.ORDER_TOTAL, orderTotal);
			pRequest.serviceLocalParameter(BBBCheckoutConstants.OUTPUT,
					pRequest, pResponse);
		}

		BBBPerformanceMonitor.end(BBBPerformanceConstants.BBB_PAYMENTGROUP_DROPLET,
				methodName);
		
		
		logDebug("Exiting method BBBPaymentGroupDroplet.getGiftCardDetailService");
		
	}

	/**
	 * @return the mPaymentGroupManager
	 */
	public BBBPaymentGroupManager getPaymentGroupManager() {
		return mPaymentGroupManager;
	}

	/**
	 * @param pPaymentGroupManager
	 *            the mPaymentGroupManager to set
	 */
	public void setPaymentGroupManager(
			BBBPaymentGroupManager pPaymentGroupManager) {
		mPaymentGroupManager = pPaymentGroupManager;
	}

	/**
	 * @return the mErrorPageURL
	 */
	public String getErrorPageURL() {
		return mErrorPageURL;
	}

	/**
	 * @param pErrorPageURL
	 *            the mErrorPageURL to set
	 */
	public void setErrorPageURL(String pErrorPageURL) {
		mErrorPageURL = pErrorPageURL;
	}

}
