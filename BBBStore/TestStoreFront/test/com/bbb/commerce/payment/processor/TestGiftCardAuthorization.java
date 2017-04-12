package com.bbb.commerce.payment.processor;

import java.util.List;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.PaymentGroup;
import atg.commerce.payment.PaymentManager;
import atg.multisite.SiteContextManager;
import atg.payment.PaymentStatus;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.account.BBBProfileFormHandler;
import com.bbb.commerce.order.BBBGiftCard;
import com.bbb.commerce.order.BBBPaymentGroupManager;
import com.bbb.framework.BBBSiteContext;
import com.bbb.payment.giftcard.BBBGiftCardFormHandler;
import com.sapient.common.tests.BaseTestCase;

public class TestGiftCardAuthorization extends BaseTestCase {
	
	private SiteContextManager siteContextManager;

	/**
	 * @return siteContextManager
	 */
	public final SiteContextManager getSiteContextManager() {
		return this.siteContextManager;
	}

	/**
	 * @param siteContextManager
	 */
	public final void setSiteContextManager(final SiteContextManager siteContextManager) {
		this.siteContextManager = siteContextManager;
	}

	public void testGiftCardAuthorization() throws CommerceException {

		DynamoHttpServletRequest request = getRequest();
		DynamoHttpServletResponse response = getResponse();

		BBBGiftCardFormHandler giftCardHandler = (BBBGiftCardFormHandler) getObject("giftCardFormHandler");
		final BBBProfileFormHandler bbbProfileFormhandler = (BBBProfileFormHandler) this.getObject("bbbProfileFormHandler");
		
		giftCardHandler.getShoppingCart().getOrderManager().removeOrder(giftCardHandler.getOrder().getId());
		giftCardHandler.setOrder(giftCardHandler.getShoppingCart().getOrderManager().createOrder(giftCardHandler.getProfile().getRepositoryId()));
		bbbProfileFormhandler.setOrder(giftCardHandler.getOrder());
		OrderHolder holder = (OrderHolder) this.getObject("shoppingCart");
		holder.setCurrent(giftCardHandler.getOrder());
		
		Order order = giftCardHandler.getOrder();
		String siteId = "BedBathUS";
		order.setSiteId(siteId);
		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
		} catch (Exception siteContextException) {
			System.out.println(siteContextException);
		}
		PaymentStatus giftCardPaymentStatus = null;
		try {
		
		List<PaymentGroup> paymentGrp = null;
		
		String pinNumber = (String)getObject("pinNo");
		String cardNumber = (String)getObject("giftCardNo");
		String siteID = (String)getObject("siteId");
		
			giftCardHandler.setGiftCardNumber(cardNumber);
			giftCardHandler.setGiftCardPin(pinNumber);
			giftCardHandler.setSiteID(siteID);
			
			giftCardHandler.handleCreateGiftCard(request, response);
			paymentGrp = ((BBBPaymentGroupManager)giftCardHandler.getPaymentGroupManager()).
			getPaymentGroups(order, "giftCard");
			if(paymentGrp != null && paymentGrp.size() > 0){
				Integer i = 0;
				for (PaymentGroup paymentGroup : paymentGrp) {
					if(paymentGroup instanceof BBBGiftCard){
						paymentGroup.setAmount(20.0);
						paymentGroup.setAmountAuthorized(0.0);
						i++;
					}
				}
				order.setSiteId("BedBathUS");
				assertTrue("All the payment groups are not of type BBBGiftCard ", i.equals(paymentGrp.size()));
				
				PaymentManager payManager = (PaymentManager)getObject("paymentManager");
				System.out.println("Payment group chains "+payManager.getPaymentGroupToChainNameMap());
				System.out.println("Gift Card chain name "+payManager.getPaymentGroupToChainNameMap().get("com.bbb.commerce.order.BBBGiftCard"));
				List<PaymentGroup> failedGroups = payManager.authorize(order, paymentGrp);
				 
					//System.out.println("failed payment group : " + failedGroups);
				
				giftCardPaymentStatus = payManager.getLastAuthorizationStatus(paymentGrp.get(0));
				System.out.println("Authorized gift card amount: "+ giftCardPaymentStatus.getAmount());
				assertTrue("GifCard Authorization faild ",giftCardPaymentStatus.getTransactionSuccess());
			}
		} catch (Exception e) {
			System.out.println("erorrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*public void authorize(Order pOrder, PaymentGroup pPaymentGroup, double pAmount)
	        throws CommerceException
	    {
	        
	        PaymentStatus status;
	        try
	        {
	        	
	        	
	        	
	            String chainName = "giftCardProcessorChain";
	            PaymentManagerPipelineArgs args = new PaymentManagerPipelineArgs();
	            args.setOrder(pOrder);
	            args.setPaymentManager(payManager);
	            args.setPaymentGroup(pPaymentGroup);
	            args.setAmount(pAmount);
	            args.setAction(PaymentManagerAction.AUTHORIZE);
	            payManager.runProcessorChain(chainName, args);
	            status = args.getPaymentStatus();
	        }
	        catch(CommerceException e)
	        {
	        }
	        postProcessAuthorize(pPaymentGroup, status, pAmount);
	    }*/

}
