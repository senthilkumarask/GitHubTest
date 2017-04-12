/**
 * 
 */
package com.bbb.integration.cybersource.creditcard;

import atg.integrations.cybersourcesoap.CyberSourceStatus;

import com.cybersource.stub.CCAuthReply;
import com.cybersource.stub.ReplyMessage;

/**
 * @author alakra
 * 
 */
public class CreditCardStatus extends CyberSourceStatus implements BBBCreditCardStatus {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2434890574351790099L;

	/**
	 * 
	 */
	private String mAuthorizationCode = null;

	/**
	 * 
	 */
	private String mAuthResponseRecord = null;

	public CreditCardStatus() {
		super();
	}

	/**
	 * A constructor that takes a ReplyMessage object and totalAmount
	 */
	public CreditCardStatus(ReplyMessage pReply, String pTotalAmount) {
		super(pReply, pTotalAmount);
		
		CCAuthReply ccReply = pReply.getCcAuthReply();
		if(ccReply != null) {
			setAuthorizationCode(ccReply.getAuthorizationCode());
			setAuthResponseRecord(ccReply.getAuthRecord());
		}
	}

	/**
	 * A constructor that takes a ReplyMessage object
	 */
	public CreditCardStatus(ReplyMessage pReply) {
		super(pReply);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbb.integration.cybersource.creditcard.BBBCreditCardStatus#getAuthCode
	 * ()
	 */
	@Override
	public String getAuthorizationCode() {
		return mAuthorizationCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbb.integration.cybersource.creditcard.BBBCreditCardStatus#setAuthCode
	 * (java.lang.String)
	 */
	@Override
	public void setAuthorizationCode(String pAuthorizationCode) {
		this.mAuthorizationCode = pAuthorizationCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbb.integration.cybersource.creditcard.BBBCreditCardStatus#
	 * getAuthResponseRecord()
	 */
	@Override
	public String getAuthResponseRecord() {
		return mAuthResponseRecord;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbb.integration.cybersource.creditcard.BBBCreditCardStatus#
	 * setAuthResponseRecord(java.lang.String)
	 */
	@Override
	public void setAuthResponseRecord(String pAuthResposeRecord) {
		this.mAuthResponseRecord = pAuthResposeRecord;
	}
}