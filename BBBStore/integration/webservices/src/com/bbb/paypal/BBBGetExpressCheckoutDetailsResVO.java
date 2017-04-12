package com.bbb.paypal;


/**
 * @author ssh108
 * 
 */
public class BBBGetExpressCheckoutDetailsResVO extends PayPalResponseVO {


	private static final long serialVersionUID = 1L;
	private String token;
	private PayerInfoVO payerInfo;
	private String contactPhone;
	private String checkoutStatus;
	private String buyerEmail;
	private BBBAddressPPVO billingAddress;
	private BBBAddressPPVO shippingAddress;
	
	/**
	 * @return
	 */
	public BBBAddressPPVO getShippingAddress() {
		return this.shippingAddress;
	}
	/**
	 * @param shippingAddress
	 */
	public void setShippingAddress(BBBAddressPPVO shippingAddress) {
		this.shippingAddress = shippingAddress;
	}
	/**
	 * @return the token
	 */
	public String getToken() {
		return this.token;
	}
	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}
	/**
	 * @return the payerInfo
	 */
	public PayerInfoVO getPayerInfo() {
		return this.payerInfo;
	}
	/**
	 * @param payerInfo the payerInfo to set
	 */
	public void setPayerInfo(PayerInfoVO payerInfo) {
		this.payerInfo = payerInfo;
	}
	/**
	 * @return the contactPhone
	 */
	public String getContactPhone() {
		return this.contactPhone;
	}
	/**
	 * @param contactPhone the contactPhone to set
	 */
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	/**
	 * @return the checkoutStatus
	 */
	public String getCheckoutStatus() {
		return this.checkoutStatus;
	}
	/**
	 * @param checkoutStatus the checkoutStatus to set
	 */
	public void setCheckoutStatus(String checkoutStatus) {
		this.checkoutStatus = checkoutStatus;
	}
	/**
	 * @return the buyerEmail
	 */
	public String getBuyerEmail() {
		return this.buyerEmail;
	}
	/**
	 * @param buyerEmail the buyerEmail to set
	 */
	public void setBuyerEmail(String buyerEmail) {
		this.buyerEmail = buyerEmail;
	}
	/**
	 * @return the billingAddress
	 */
	public BBBAddressPPVO getBillingAddress() {
		return this.billingAddress;
	}
	/**
	 * @param billingAddress the billingAddress to set
	 */
	public void setBillingAddress(BBBAddressPPVO billingAddress) {
		this.billingAddress = billingAddress;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
 		return "BBBGetExpressCheckoutDetailsResVO [token=" + token + ", payerInfo=" + payerInfo.toString() + ", contactPhone=" + contactPhone + ", checkoutStatus=" + checkoutStatus + ", buyerEmail=" + buyerEmail + ", billingAddress=" + ( billingAddress !=null ? billingAddress.toString():null) + 
				", shippingAddress=" +( shippingAddress !=null ? shippingAddress.toString():null)  +	"]";
	}

}
