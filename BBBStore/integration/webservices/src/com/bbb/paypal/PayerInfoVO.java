package com.bbb.paypal;

import java.io.Serializable;

import com.bbb.commerce.giftregistry.vo.AddressVO;
import com.bbb.framework.integration.ServiceResponseBase;
import com.bbb.framework.webservices.vo.ErrorStatus;

/**
 * @author ssh108
 * 
 */
public class PayerInfoVO implements Serializable {


	private static final long serialVersionUID = 1L;
	private String payerID;
	private String payerEmail;
	private String payerStatus;
	private String payerFirstName;
	private String payerMiddleName;
	private String payerLastName;
	private String payerCountryCode;
	private String payerBiz;
	private BBBAddressPPVO address;
	/**
	 * @return the payerID
	 */
	public String getPayerID() {
		return payerID;
	}
	/**
	 * @param payerID the payerID to set
	 */
	public void setPayerID(String payerID) {
		this.payerID = payerID;
	}
	/**
	 * @return the payerEmail
	 */
	public String getPayerEmail() {
		return payerEmail;
	}
	/**
	 * @param payerEmail the payerEmail to set
	 */
	public void setPayerEmail(String payerEmail) {
		this.payerEmail = payerEmail;
	}
	/**
	 * @return the payerStatus
	 */
	public String getPayerStatus() {
		return payerStatus;
	}
	/**
	 * @param payerStatus the payerStatus to set
	 */
	public void setPayerStatus(String payerStatus) {
		this.payerStatus = payerStatus;
	}
	
	/**
	 * @return the payerFirstName
	 */
	public String getPayerFirstName() {
		return payerFirstName;
	}
	/**
	 * @param payerFirstName the payerFirstName to set
	 */
	public void setPayerFirstName(String payerFirstName) {
		this.payerFirstName = payerFirstName;
	}
	/**
	 * @return the payerMiddleName
	 */
	public String getPayerMiddleName() {
		return payerMiddleName;
	}
	/**
	 * @param payerMiddleName the payerMiddleName to set
	 */
	public void setPayerMiddleName(String payerMiddleName) {
		this.payerMiddleName = payerMiddleName;
	}
	/**
	 * @return the payerLastName
	 */
	public String getPayerLastName() {
		return payerLastName;
	}
	/**
	 * @param payerLastName the payerLastName to set
	 */
	public void setPayerLastName(String payerLastName) {
		this.payerLastName = payerLastName;
	}
	/**
	 * @return the payerCountryCode
	 */
	public String getPayerCountryCode() {
		return payerCountryCode;
	}
	/**
	 * @param payerCountryCode the payerCountryCode to set
	 */
	public void setPayerCountryCode(String payerCountryCode) {
		this.payerCountryCode = payerCountryCode;
	}
	/**
	 * @return the payerBiz
	 */
	public String getPayerBiz() {
		return payerBiz;
	}
	/**
	 * @param payerBiz the payerBiz to set
	 */
	public void setPayerBiz(String payerBiz) {
		this.payerBiz = payerBiz;
	}
	/**
	 * @return the address
	 */
	public BBBAddressPPVO getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(BBBAddressPPVO address) {
		this.address = address;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PayerInfoVO [payerID=" + payerID + ", payerEmail=" + payerEmail + ", payerStatus=" + payerStatus + ", payerFirstName=" + payerFirstName + ", payerMiddleName=" + payerMiddleName + ", payerLastName=" + payerLastName + ", payerCountryCode=" + payerCountryCode + ", payerBiz=" + payerBiz + ", address=" + address.toString() + "]";
	}

}
