/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: ssh108
 *
 * Created on: 5-mar-2014
 * --------------------------------------------------------------------------------
 */

package com.bbb.commerce.payment;

import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PaymentGroupManager;
import atg.commerce.order.purchase.PaymentGroupInitializationException;
import atg.commerce.order.purchase.PaymentGroupInitializer;
import atg.commerce.order.purchase.PaymentGroupMapContainer;
import atg.commerce.order.purchase.PaymentGroupMatcher;
import atg.nucleus.GenericService;
import atg.servlet.DynamoHttpServletRequest;
import atg.userprofiling.Profile;

import com.bbb.commerce.order.Paypal;

/**
 * The <code>PayPalInitializer</code> implements the PaymentGroupInitializer
 * interface. The <code>initializePaymentGroups</code> method is used to create
 * PayPal PaymentGroups for the user based on their existence in the Profile.
 * 
 */
public class PayPalInitializer extends GenericService implements PaymentGroupInitializer, PaymentGroupMatcher {

	/**
	 * 
	 */
	private String paypalType;
	private String paypalPaymentGroupName;
	private PaymentGroupManager paymentGroupManager;

	/**
	 * @return the paymentGroupManager
	 */
	public PaymentGroupManager getPaymentGroupManager() {
		return paymentGroupManager;
	}


	/**
	 * @param paymentGroupManager the paymentGroupManager to set
	 */
	public void setPaymentGroupManager(PaymentGroupManager paymentGroupManager) {
		this.paymentGroupManager = paymentGroupManager;
	}


	// --------------------------------------------------
	// Constructors
	// --------------------------------------------------
	/**
	 * Creates a new <code>PayPalInitializer</code> instance.
	 * 
	 */
	public PayPalInitializer() {
		//default constructor
	}

	
	/**
	 * @return the paypalType
	 */
	public String getPaypalType() {
		return paypalType;
	}

	/**
	 * @param paypalType
	 *            the paypalType to set
	 */
	public void setPaypalType(String paypalType) {
		this.paypalType = paypalType;
	}

	/**
	 * @return the paypalPaymentGroupName
	 */
	public String getPaypalPaymentGroupName() {
		return paypalPaymentGroupName;
	}

	/**
	 * @param paypalPaymentGroupName
	 *            the paypalPaymentGroupName to set
	 */
	public void setPaypalPaymentGroupName(String paypalPaymentGroupName) {
		this.paypalPaymentGroupName = paypalPaymentGroupName;
	}

	// --------------------------------------------------
	// Methods
	// --------------------------------------------------

	/**
	 * <code>initializePaymentGroups</code> executes the
	 * <code>initializeCreditCards</code> method.
	 * 
	 * @param pProfile
	 *            a <code>Profile</code> value
	 * @param pPaymentGroupMapContainer
	 *            a <code>PaymentGroupMapContainer</code> value
	 * @param pRequest
	 *            a <code>DynamoHttpServletRequest</code> value
	 * @exception PaymentGroupInitializationException
	 *                if an error occurs
	 */
	public void initializePaymentGroups(Profile pProfile, PaymentGroupMapContainer pPaymentGroupMapContainer, DynamoHttpServletRequest pRequest) throws PaymentGroupInitializationException {
		initializePayPal(pProfile, pPaymentGroupMapContainer, pRequest);
	}


	/**
	 * This method create PayPal PaymentGroup and adds to the container if container don't have PayPal PaymentGroup
	 * @param pProfile
	 * @param pPaymentGroupMapContainer
	 * @param pRequest
	 * @throws PaymentGroupInitializationException
	 */
	public void initializePayPal(Profile pProfile, PaymentGroupMapContainer pPaymentGroupMapContainer, DynamoHttpServletRequest pRequest) throws PaymentGroupInitializationException {
		
		logDebug("PayPalInitializer.initializePayPal() :: Start");
		
		try {
			// get the user's default credit card
			if (pPaymentGroupMapContainer != null && (pPaymentGroupMapContainer.getPaymentGroupMap().get(getPaypalPaymentGroupName()) == null)) {
				Paypal ppPg = (Paypal) getPaymentGroupManager().createPaymentGroup(getPaypalPaymentGroupName());
				pPaymentGroupMapContainer.addPaymentGroup(getPaypalPaymentGroupName(), ppPg);
			}
		} catch (Exception exc) {
			throw new PaymentGroupInitializationException(exc);
		}
		
		logDebug("PayPalInitializer.initializePayPal() :: End");
		
	}

	@Override
	public String matchPaymentGroup(PaymentGroup paymentgroup, PaymentGroupMapContainer paymentgroupmapcontainer) {
		return null;
	}


	@Override
	public String getNewPaymentGroupName(PaymentGroup paymentgroup) {
		return null;
	}

}
