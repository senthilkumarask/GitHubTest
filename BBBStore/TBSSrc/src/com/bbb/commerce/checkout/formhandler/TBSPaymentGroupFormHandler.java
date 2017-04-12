package com.bbb.commerce.checkout.formhandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.transaction.Transaction;

import atg.commerce.CommerceException;
import atg.commerce.order.PaymentGroup;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.order.BBBCreditCard;
import com.bbb.commerce.order.PayAtRegister;
import com.bbb.commerce.order.Paypal;
import com.bbb.commerce.order.TBSPaymentGroupManager;
import com.bbb.commerce.order.purchase.CheckoutProgressStates;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;

public class TBSPaymentGroupFormHandler extends BBBPaymentGroupFormHandler {
	
    private String mPorSuccessURL;
    private String mPorErrorURL;
	
    /**
	 * @return the porSuccessURL
	 */
	public String getPorSuccessURL() {
		return mPorSuccessURL;
	}

	/**
	 * @return the porErrorURL
	 */
	public String getPorErrorURL() {
		return mPorErrorURL;
	}

	/**
	 * @param pPorSuccessURL the porSuccessURL to set
	 */
	public void setPorSuccessURL(String pPorSuccessURL) {
		mPorSuccessURL = pPorSuccessURL;
	}

	/**
	 * @param pPorErrorURL the porErrorURL to set
	 */
	public void setPorErrorURL(String pPorErrorURL) {
		mPorErrorURL = pPorErrorURL;
	}


	/**
	 * THis handle method is used to add the PayAtRegister payment group to the order.
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public boolean handlePARPayment(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                    throws ServletException, IOException {
		vlogDebug("TBSPaymentGroupFormHandler :: handlePARPayment() method :: START");
    	Transaction tr = null;
    	BBBOrderImpl order = (BBBOrderImpl) getOrder();
    	synchronized (order) {
    		tr = ensureTransaction();
    		PayAtRegister parPaymentGroup = null;
    		boolean isPaymentGroupExists = false;
			try {
				List<PaymentGroup> paymentGroups = order.getPaymentGroups();
				List<String> removalPaymentGrps = new ArrayList<String>();
				for (final PaymentGroup paymentGroup : paymentGroups) {
					
					if (paymentGroup instanceof BBBCreditCard) {
						removalPaymentGrps.add(paymentGroup.getId());
					}
					if (paymentGroup instanceof Paypal) {
						removalPaymentGrps.add(paymentGroup.getId());
                    }
					if (paymentGroup instanceof PayAtRegister) {
						isPaymentGroupExists = true;
						parPaymentGroup = (PayAtRegister) paymentGroup;
					}
				}
				if (!removalPaymentGrps.isEmpty()) {
					for (String payGrpIDs : removalPaymentGrps) {
						getPaymentGroupManager().removePaymentGroupFromOrder(order, payGrpIDs);
					}
				}
				if(!isPaymentGroupExists){
					parPaymentGroup = ((TBSPaymentGroupManager) getPaymentGroupManager()).createPayAtRegistryPayment(order);
					if(parPaymentGroup == null){
						vlogError("Payment group is not available to assign to order ");
						addFormException(new DropletException("There is an issue occurred, Please try after some time."));
					} else {
						getPaymentGroupManager().addPaymentGroupToOrder(order, parPaymentGroup);
					}
				}
				
				
					
				setCurrentPaymentGroup(parPaymentGroup);
				if(parPaymentGroup != null) {
					vlogInfo("parPaymentGroup.getId()" + parPaymentGroup.getId());
					vlogInfo("parPaymentGroup.getAmount()" + parPaymentGroup.getAmount());
					getOrderManager().addRemainingOrderAmountToPaymentGroup(order,parPaymentGroup.getId());
					setPaymentGroupId(parPaymentGroup.getId());
					
				}
				order.updateVersion();
				getOrderManager().updateOrder(order);

                vlogInfo("Order total :: " + order.getPriceInfo().getTotal());
	                
			} catch (CommerceException e) {
				vlogError("CommerceException occurred while adding PayAtRegister payment :: "+e);
				addFormException(new DropletException("CommerceException occurred while adding PayAtRegister payment", TBSConstants.COMMERCE_EXCEPTION));
			}finally {
	            if (tr != null) {
	                commitTransaction(tr);
	            }
	        }
			if (!getFormError()) {
	            getCheckoutProgressStates().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.REVIEW.toString());
	        }
			String redirectURL = pRequest.getContextPath() + this.getCheckoutProgressStates().getFailureURL();
	        vlogDebug("TBSPaymentGroupFormHandler :: handlePORPayment() method :: END");
	        return checkFormRedirect(redirectURL, redirectURL, pRequest, pResponse);
    	}
    }

	/**
	 * This method is overridden to remove the PayAtRegister payment group, in case of creditcard used.
	 */
	@SuppressWarnings("unchecked")
	public boolean handlePayment(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		vlogDebug("TBSPaymentGroupFormHandler :: handlePayment() method :: START");
    	Transaction tr = null;
    	BBBOrder order = (BBBOrder) getOrder();
    	
    	this.logInfo("TBSPaymentGroupFormHandler :: handlePayment() start :: orderID : "+ order.getId());
        if(null != ((BBBOrderImpl) order).getCouponListVo() && ((BBBOrderImpl) order).getCouponListVo().size() > 0){
    		this.logInfo(" available coupons in order : " + ((BBBOrderImpl) order).getCouponListVo().get(0).getmPromoId());
    	}
    	synchronized (order) {
    		tr = ensureTransaction();
			List<PaymentGroup> paymentGroups = order.getPaymentGroups();
			String payAtRegisterPayGrpID = null;
			for (PaymentGroup paymentGroup : paymentGroups) {
				if (paymentGroup instanceof PayAtRegister) {
					payAtRegisterPayGrpID = paymentGroup.getId();
					break;
				}
			}
			vlogDebug("Removing PayAtRegister payment group from order :: "+payAtRegisterPayGrpID);
	        if (!StringUtils.isBlank(payAtRegisterPayGrpID)) {
	            try {
					getPaymentGroupManager().removePaymentGroupFromOrder(order, payAtRegisterPayGrpID);
					getOrderManager().updateOrder(order);
				} catch (CommerceException e) {
					vlogError("CommerceException occurred while adding removing PayAtRegister payment from order");
					addFormException(new DropletException("CommerceException occurred while removing PayAtRegister payment", TBSConstants.COMMERCE_EXCEPTION));
				}finally {
		            if (tr != null) {
		                commitTransaction(tr);
		            }
		        }
	        }
	        this.logInfo("TBSPaymentGroupFormHandler :: handlePayment() end :: orderID : "+ order.getId());
	        if(null != ((BBBOrderImpl) order).getCouponListVo() && ((BBBOrderImpl) order).getCouponListVo().size() > 0){
	    		this.logInfo(" available coupons in order : " + ((BBBOrderImpl) order).getCouponListVo().get(0).getmPromoId());
	    	}
	        return super.handlePayment(pRequest, pResponse);
    	}
	}

}

