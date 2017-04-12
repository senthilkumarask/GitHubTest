package com.bbb.commerce.vo;

import java.io.Serializable;

import atg.userprofiling.Profile;
import com.bbb.commerce.common.BBBAddressContainer;
import com.bbb.commerce.order.paypal.BBBPayPalSessionBean;
import com.bbb.commerce.order.purchase.CheckoutProgressStates;



/**
 * This VO Contains all the session scoped components to be passed
 * as arguments to manager and tools class
 * 
 * @author aban13
 *
 */
public class PayPalInputVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BBBPayPalSessionBean paypalSessionBean;
	private CheckoutProgressStates checkoutState;
	private Profile profile;
	private BBBAddressContainer addressCoontainer;
	private BBBAddressContainer multiShipAddContainer;
	private boolean spcSession;
	
	public boolean isSpcSession() {
		return spcSession;
	}
	public void setSpcSession(boolean spcSession) {
		this.spcSession = spcSession;
	}
	/**
	 * @return the multiShipAddContainer
	 */
	public BBBAddressContainer getMultiShipAddContainer() {
		return multiShipAddContainer;
	}
	/**
	 * @param multiShipAddContainer the multiShipAddContainer to set
	 */
	public void setMultiShipAddContainer(BBBAddressContainer multiShipAddContainer) {
		this.multiShipAddContainer = multiShipAddContainer;
	}
	/**
	 * @return the paypalSessionBean
	 */
	public BBBPayPalSessionBean getPaypalSessionBean() {
		return this.paypalSessionBean;
	}
	/**
	 * @return the checkoutState
	 */
	public CheckoutProgressStates getCheckoutState() {
		return this.checkoutState;
	}
	/**
	 * @return the profile
	 */
	public Profile getProfile() {
		return this.profile;
	}
	/**
	 * @return the addressCoontainer
	 */
	public BBBAddressContainer getAddressCoontainer() {
		return this.addressCoontainer;
	}
	/**
	 * @param paypalSessionBean the paypalSessionBean to set
	 */
	public void setPaypalSessionBean(BBBPayPalSessionBean paypalSessionBean) {
		this.paypalSessionBean = paypalSessionBean;
	}
	/**
	 * @param checkoutState the checkoutState to set
	 */
	public void setCheckoutState(CheckoutProgressStates checkoutState) {
		this.checkoutState = checkoutState;
	}
	/**
	 * @param profile the profile to set
	 */
	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	/**
	 * @param addressCoontainer the addressCoontainer to set
	 */
	public void setAddressCoontainer(BBBAddressContainer addressCoontainer) {
		this.addressCoontainer = addressCoontainer;
	}
	
}
