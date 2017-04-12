package com.bbb.paypal;

/**
 * @author smalho
 *
 */
public class BBBPayPalCredentials {

	private String userName;
	private String password;
	private String signature;
	private String version;
	private String soapApiURL;
	private Integer addressOverRide;
	private String paymentAction;
	private Integer expTime;
	private String subject;
	
	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the expTime
	 */
	public Integer getExpTime() {
		return expTime;
	}

	/**
	 * @param expTime the expTime to set
	 */
	public void setExpTime(Integer expTime) {
		this.expTime = expTime;
	}

	/**
	 * @return the paymentAction
	 */
	public String getPaymentAction() {
		return paymentAction;
	}

	/**
	 * @param paymentAction the paymentAction to set
	 */
	public void setPaymentAction(String paymentAction) {
		this.paymentAction = paymentAction;
	}

	
	/**
	 * @return the addressOverRide
	 */
	public Integer getAddressOverRide() {
		return addressOverRide;
	}

	/**
	 * @param addressOverRide the addressOverRide to set
	 */
	public void setAddressOverRide(Integer addressOverRide) {
		this.addressOverRide = addressOverRide;
	}

	/**
	 * @return the showShipping
	 */
	
	/**
	 * @return
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}


	/**
	 * @return the soapApiURL
	 */
	public String getSoapApiURL() {
		return soapApiURL;
	}

	/**
	 * @param soapApiURL
	 *            the soapApiURL to set
	 */
	public void setSoapApiURL(String soapApiURL) {
		this.soapApiURL = soapApiURL;
	}

	/**
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return
	 */
	public String getSignature() {
		return signature;
	}

	
	/**
	 * This method is used for logging purpose
	 * @return String
	 */
	public String getDetailVO() {
		StringBuilder builder = new StringBuilder();
		builder.append("BBBPayPalCredentials [version=");
		builder.append(version);
		builder.append(", soapApiURL=");
		builder.append(soapApiURL);
		builder.append(", addressOverRide=");
		builder.append(addressOverRide);
		builder.append(", paymentAction=");
		builder.append(paymentAction);
		builder.append(", userName=");
		builder.append(userName);
		builder.append(", password=");
		builder.append(password);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * @param signature
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}

}
