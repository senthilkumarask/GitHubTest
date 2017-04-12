/**
 * 
 */
package com.bbb.payment.giftcard;

import java.util.Calendar;


import atg.payment.PaymentStatusImpl;

/**
 * @author vagra4
 * 
 */
public class GiftCardStatusImpl extends PaymentStatusImpl implements
		GiftCardStatus {

	private static final long serialVersionUID = -1526014913076206617L;

	/**
	 * Property authCode
	 */
	private String mAuthCode;
	/**
	 * Property traceNumber
	 */
	private String mTraceNumber;
	/**
	 * Property authRespCode
	 */
	private String mAuthRespCode;
	/**
	 * Property cardClass
	 */
	private String mCardClass;
	/**
	 * Property previousBalance
	 */
	private String mPreviousBalance;

	/**
	 * Default constructor to be executed during LoadOrder pipeline.
	 */
	public GiftCardStatusImpl() {
		mAuthCode = null;
		mTraceNumber = null;
		mAuthRespCode = null;
		mCardClass = null;
	}
	
	/**
	 * Constructor to instantiate GiftCardStatus.
	 * 
	 * @param pTransactionId
	 * @param pAmount
	 * @param pTransactionSuccess
	 * @param pErrorMessage
	 * @param pTransactionTimestamp
	 * @param pAuthCode
	 * @param pTraceNumber
	 * @param pAuthRespCode
	 * @param pCardClass
	 * @param pPreviousBalance
	 *//*
	public GiftCardStatusImpl(String pTransactionId, double pAmount,
			boolean pTransactionSuccess, String pErrorMessage,
			Date pTransactionTimestamp, String pAuthCode, String pTraceNumber,
			String pAuthRespCode, String pCardClass, String pPreviousBalance) {

		super(pTransactionId, pAmount, pTransactionSuccess, pErrorMessage,
				pTransactionTimestamp);

		mAuthCode = pAuthCode;
		mTraceNumber = pTraceNumber;
		mAuthRespCode = pAuthRespCode;
		mCardClass = pCardClass;
		mPreviousBalance = pPreviousBalance;

	}*/
	
	/**
	 * Constructor to instantiate GiftCardStatus.
	 * 
	 * @param pTransactionId
	 * @param pTransactionSuccess
	 * @param pGiftCardBeanInfo
	 * @param pGiftcertificateInfo
	 */
	public GiftCardStatusImpl(String pTransactionId, boolean pTransactionSuccess, String pErrorMsg, 
			GiftCardBeanInfo pGiftCardBeanInfo,	GenericGiftCardInfo pGiftcertificateInfo){
		super(pTransactionId, pGiftcertificateInfo.getAmount(), pTransactionSuccess,
				pErrorMsg, Calendar.getInstance().getTime());
		mAuthCode = pGiftCardBeanInfo.getAuthCode();
		mTraceNumber = pGiftCardBeanInfo.getTraceNumber();
		mAuthRespCode = pGiftCardBeanInfo.getAuthRespCode();
		mCardClass = pGiftCardBeanInfo.getCardClass();
		mPreviousBalance = pGiftCardBeanInfo.getPreviousBalance();
	}

	/**
	 * getter for property authCode
	 */
	@Override
	public String getAuthCode() {
		return mAuthCode;
	}

	/**
	 * @param pAuthCode
	 *            the mAuthCode to set
	 */
	public void setAuthCode(String pAuthCode) {
		mAuthCode = pAuthCode;
	}

	/**
	 * getter for property traceNumber
	 */
	@Override
	public String getTraceNumber() {
		return mTraceNumber;
	}

	/**
	 * @param pTraceNumber
	 *            the mTraceNumber to set
	 */
	public void setTraceNumber(String pTraceNumber) {
		mTraceNumber = pTraceNumber;
	}

	/**
	 * getter for property authRespCode
	 */
	@Override
	public String getAuthRespCode() {
		return mAuthRespCode;
	}

	/**
	 * @param pAuthRespCode
	 *            the mAuthRespCode to set
	 */
	public void setAuthRespCode(String pAuthRespCode) {
		mAuthRespCode = pAuthRespCode;
	}

	/**
	 * getter for property cardClass
	 */
	@Override
	public String getCardClass() {
		return mCardClass;
	}

	/**
	 * @param pCardClass
	 *            the mCardClass to set
	 */
	public void setCardClass(String pCardClass) {
		mCardClass = pCardClass;
	}

	/**
	 * @return the mPreviousBalance
	 */
	public String getPreviousBalance() {
		return mPreviousBalance;
	}

	/**
	 * @param pPreviousBalance the mPreviousBalance to set
	 */
	public void setPreviousBalance(String pPreviousBalance) {
		mPreviousBalance = pPreviousBalance;
	}
}
