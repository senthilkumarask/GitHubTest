/**
 * 
 */
package com.bbb.commerce.checkout.vbv.vo;

import java.io.Serializable;

/**
 * @author ngup50
 *
 */
public class BBBVerifiedByVisaVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String enrolled;
	private String lookupErrorNo;
	private String lookupErrorDesc;
	private String transactionId;
	private String orderId;
	private String lookupEciFlag;
	private String aCSUrl;
	private String payload;
	private String lookupResponse;
	private String lookupRequest;
	private String cardVerNumber;
	private String transactionType;
	private String authenticateRequest;
	private String authenticateResponse;
	private String pAResStatus;
	private String signatureVerification;
	private String authenticationEciFlag;
	private String xid;
	private String cavv;
	private String authenticationErrorNo;
	private String authenticationErrorDesc;
	private String Centinel_PIType;
	private String message;
	private String commerceIndicator;
	private String token;
	
	/**
	 * @return the enrolled
	 */
	public String getEnrolled() {
		return enrolled;
	}
	/**
	 * @param enrolled the enrolled to set
	 */
	public void setEnrolled(String enrolled) {
		this.enrolled = enrolled;
	}
	/**
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}
	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	/**
	 * @return the orderId
	 */
	public String getOrderId() {
		return orderId;
	}
	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	/**
	 * @return the aCSUrl
	 */
	public String getaCSUrl() {
		return aCSUrl;
	}
	/**
	 * @param aCSUrl the aCSUrl to set
	 */
	public void setaCSUrl(String aCSUrl) {
		this.aCSUrl = aCSUrl;
	}
	/**
	 * @return the payload
	 */
	public String getPayload() {
		return payload;
	}
	/**
	 * @param payload the payload to set
	 */
	public void setPayload(String payload) {
		this.payload = payload;
	}
	/**
	 * @return the lookupResponse
	 */
	public String getLookupResponse() {
		return lookupResponse;
	}
	/**
	 * @param lookupResponse the lookupResponse to set
	 */
	public void setLookupResponse(String lookupResponse) {
		this.lookupResponse = lookupResponse;
	}
	/**
	 * @return the lookupRequest
	 */
	public String getLookupRequest() {
		return lookupRequest;
	}
	/**
	 * @param lookupRequest the lookupRequest to set
	 */
	public void setLookupRequest(String lookupRequest) {
		this.lookupRequest = lookupRequest;
	}
	/**
	 * @return the cardVerNumber
	 */
	public String getCardVerNumber() {
		return cardVerNumber;
	}
	/**
	 * @param cardVerNumber the cardVerNumber to set
	 */
	public void setCardVerNumber(String cardVerNumber) {
		this.cardVerNumber = cardVerNumber;
	}
	/**
	 * @return the transactionType
	 */
	public String getTransactionType() {
		return transactionType;
	}
	/**
	 * @param transactionType the transactionType to set
	 */
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	/**
	 * @return the authenticateRequest
	 */
	public String getAuthenticateRequest() {
		return authenticateRequest;
	}
	/**
	 * @param authenticateRequest the authenticateRequest to set
	 */
	public void setAuthenticateRequest(String authenticateRequest) {
		this.authenticateRequest = authenticateRequest;
	}
	/**
	 * @return the authenticateResponse
	 */
	public String getAuthenticateResponse() {
		return authenticateResponse;
	}
	/**
	 * @param authenticateResponse the authenticateResponse to set
	 */
	public void setAuthenticateResponse(String authenticateResponse) {
		this.authenticateResponse = authenticateResponse;
	}
	/**
	 * @return the lookupErrorNo
	 */
	public String getLookupErrorNo() {
		return lookupErrorNo;
	}
	/**
	 * @param lookupErrorNo the lookupErrorNo to set
	 */
	public void setLookupErrorNo(String lookupErrorNo) {
		this.lookupErrorNo = lookupErrorNo;
	}
	/**
	 * @return the lookupErrorDesc
	 */
	public String getLookupErrorDesc() {
		return lookupErrorDesc;
	}
	/**
	 * @param lookupErrorDesc the lookupErrorDesc to set
	 */
	public void setLookupErrorDesc(String lookupErrorDesc) {
		this.lookupErrorDesc = lookupErrorDesc;
	}
	/**
	 * @return the lookupEciFlag
	 */
	public String getLookupEciFlag() {
		return lookupEciFlag;
	}
	/**
	 * @param lookupEciFlag the lookupEciFlag to set
	 */
	public void setLookupEciFlag(String lookupEciFlag) {
		this.lookupEciFlag = lookupEciFlag;
	}
	/**
	 * @return the pAResStatus
	 */
	public String getpAResStatus() {
		return pAResStatus;
	}
	/**
	 * @param pAResStatus the pAResStatus to set
	 */
	public void setpAResStatus(String pAResStatus) {
		this.pAResStatus = pAResStatus;
	}
	/**
	 * @return the signatureVerification
	 */
	public String getSignatureVerification() {
		return signatureVerification;
	}
	/**
	 * @param signatureVerification the signatureVerification to set
	 */
	public void setSignatureVerification(String signatureVerification) {
		this.signatureVerification = signatureVerification;
	}
	/**
	 * @return the authenticationEciFlag
	 */
	public String getAuthenticationEciFlag() {
		return authenticationEciFlag;
	}
	/**
	 * @param authenticationEciFlag the authenticationEciFlag to set
	 */
	public void setAuthenticationEciFlag(String authenticationEciFlag) {
		this.authenticationEciFlag = authenticationEciFlag;
	}
	/**
	 * @return the xid
	 */
	public String getXid() {
		return xid;
	}
	/**
	 * @param xid the xid to set
	 */
	public void setXid(String xid) {
		this.xid = xid;
	}
	/**
	 * @return the cavv
	 */
	public String getCavv() {
		return cavv;
	}
	/**
	 * @param cavv the cavv to set
	 */
	public void setCavv(String cavv) {
		this.cavv = cavv;
	}
	/**
	 * @return the authenticationErrorNo
	 */
	public String getAuthenticationErrorNo() {
		return authenticationErrorNo;
	}
	/**
	 * @param authenticationErrorNo the authenticationErrorNo to set
	 */
	public void setAuthenticationErrorNo(String authenticationErrorNo) {
		this.authenticationErrorNo = authenticationErrorNo;
	}
	/**
	 * @return the authenticationErrorDesc
	 */
	public String getAuthenticationErrorDesc() {
		return authenticationErrorDesc;
	}
	/**
	 * @param authenticationErrorDesc the authenticationErrorDesc to set
	 */
	public void setAuthenticationErrorDesc(String authenticationErrorDesc) {
		this.authenticationErrorDesc = authenticationErrorDesc;
	}
	/**
	 * @return the centinel_PIType
	 */
	public String getCentinel_PIType() {
		return Centinel_PIType;
	}
	/**
	 * @param centinel_PIType the centinel_PIType to set
	 */
	public void setCentinel_PIType(String centinel_PIType) {
		Centinel_PIType = centinel_PIType;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the commerceIndicator
	 */
	public String getCommerceIndicator() {
		return commerceIndicator;
	}
	/**
	 * @param commerceIndicator the commerceIndicator to set
	 */
	public void setCommerceIndicator(String commerceIndicator) {
		this.commerceIndicator = commerceIndicator;
	}
	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}
	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}
}
