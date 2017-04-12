package com.bbb.account.vo;

import com.bbb.framework.integration.ServiceRequestBase;

public class UpdateWalletProfileReqVo extends ServiceRequestBase{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userToken;
	private String siteFlag;
	private String serviceName;
	private String walletId;
	private String mobilePhone;
	private String firstName;
	private String lastName;
	private String address;
	private String city;
	private String state;
	private String postalCode;
	private String emailAddr;
	
	
	public String getEmailAddr() {
		return emailAddr;
	}
	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}
	public String getUserToken() {
		return userToken;
	}
	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
	public String getSiteFlag() {
		return siteFlag;
	}
	public void setSiteFlag(String siteFlag) {
		this.siteFlag = siteFlag;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getWalletId() {
		return walletId;
	}
	public void setWalletId(String walletId) {
		this.walletId = walletId;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	


	
	
	
	
}