package com.bbb.valuelink;

import java.util.HashMap;
import java.util.Map;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.payment.giftcard.GiftCardBeanInfo;
import com.bbb.valuelink.encryption.EncryptionUtil;
import com.bbb.valuelink.encryption.ValueLinkEncryptor;

/**
 * This class provides implementation methods to the interface
 * GiftCardProcessor.
 *
 * This class primarily deals with redemption of a gift card and balance
 * retrieval from ValueLink API.
 *
 * @author vagra4
 *
 */
public class ValueLinkGiftCardProcessorImpl extends BBBGenericService implements
		ValueLinkGiftCardProcessor {

	/**
	 * Performance constants.
	 */
	private static final String PROCESSRESPONSE = "processResponse";
	private static final String CREATEVLREQDATA = "createValueLinkRequestData";
	private static final String CRVLCOMMONREQDATA = "createValuLinkCommonRequestData";
	private static final String CRVLREQPAYLOAD = "createValueLinkRequestPayload";
	private static final String REQLOADBODYOPTFLD = "createRequestPayloadBodyOptionalFields";
	private static final String CREATEREQSUGGESTEDFLD = "createRequestPayloadBodySuggestedFields";
	private static final String CRREQPAYLOADREQUIREDFLD = "createRequestPayloadBodyRequiredFields";
	private static final String CRREQPAYLOADHEADER = "createRequestPayloadHeader";
	private static final String GETTRANSREQCODE = "getTransactionRequestCode";

	/**
	 * Field separator for value link request and response payload.
	 */
	private static final char FS = (char) 0x1C;
	/**
	 * Instance of ValueLinkEncryptor
	 */
	private ValueLinkEncryptor mValueLinkEncryptor;

	private ValueLinkGiftCardValidation mValueLinkGiftCardValidation;

	private ValueLinkGiftCardUtil mValueLinkGiftCardUtil;
	/**
	 * variable to hold value link blank values from configurKeys repository.
	 */
	private String mValueLinkBlankValue;
	/**
	 * Instance of ValueLinkClient.
	 */
	private ValueLinkClient mValueLinkClient;
	/**
	 * Instance of BBBCatalogTools.
	 */
	private BBBCatalogTools mBBBCatalogTools;

	/**
	 * Flag to check if SUGGESTED fields for request Body is required or not
	 */
	private String mReqBodySUGGESTEDFields;

	/**
	 * Flag to check if OPTIONAL fields for request Body is required or not
	 */
	private String mReqBodyOPTIONALFields;

	@Override
	/**
	 * This method authorizes the gift card and returns the balance amount of
	 * Gift Card.
	 *
	 * 2400 call
	 *
	 * @param pGiftCardNo
	 * @param pPinNo
	 * @return GiftCardBeanInfo
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public GiftCardBeanInfo getBalance(String pGiftCardNo, String pPinNo,
			String pOrderID, String pPaymentGroupId, String pSite)
			throws BBBSystemException, BBBBusinessException {
		if(isLoggingDebug()){
		logDebug("Starting method ValueLinkGiftCardProcessorImpl.getBalance");
		}
		String methodName = BBBCheckoutConstants.GET_BALANCE;
		GiftCardBeanInfo giftCardBeanInfo = null;
		boolean isCanceled = false;
		try {

			BBBPerformanceMonitor.start(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);

			Map<String, String> mVLInputs = createValueLinkRequestData(
					pGiftCardNo, pPinNo, pOrderID, pPaymentGroupId,
					BBBCheckoutConstants.GET_BALANCE, null, pSite);

			giftCardBeanInfo = invoke(pGiftCardNo, pPinNo, mVLInputs,
					BBBCheckoutConstants.GET_BALANCE);
			if(isLoggingDebug()){
			logDebug("ValueLinkGiftCardProcessorImpl.getBalance: Final giftCardBeanInfo giftcardId is: " + giftCardBeanInfo.getGiftCardID() + " and status is : " + giftCardBeanInfo.getStatus());
			}
		} catch (BBBSystemException bse) {
			isCanceled = true;
			BBBPerformanceMonitor.cancel(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);
			logError("system exception occured in getBalance service to Valuelink: " + bse);
			throw bse;
		} catch (BBBBusinessException bbe) {
			isCanceled = true;
			BBBPerformanceMonitor.cancel(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);
			logError("business exception occured in getBalance service to Valuelink: " + bbe);
			throw bbe;
		} finally {
			if (!isCanceled) {
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.VALUE_LINK_PROCESSOR,
						methodName);
			}

		}
		if(isLoggingDebug()){
			logDebug("Exiting method ValueLinkGiftCardProcessorImpl.getBalance");
		}
		return giftCardBeanInfo;
	}

	/**
	 * This method redeems a gift card by making a call to Value link API with
	 * the an available amount of Gift Card returned from getBalance call of
	 * ValueLink API.
	 * <p>
	 * The amount returned from valueLink may be completely or partially used
	 * based on order total. If the balanceAmount <= orderTotal then completely
	 * used and if the balanceAmount > orderTotal then partially
	 * used(orderTotal-balanceAmount).
	 *
	 * 2202 call
	 *
	 * @param pGiftCardNo
	 * @param pPinNo
	 * @return GiftCardBeanInfo
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public GiftCardBeanInfo redeem(String pGiftCardNo, String pPinNo,
			String pRedemptionAmount, String pOrderID, String pPaymentGroupId,
			String pSite) throws BBBSystemException, BBBBusinessException {

		if(isLoggingDebug()){
			logDebug("Starting method ValueLinkGiftCardProcessorImpl.redeem");
		}

		String methodName = BBBCheckoutConstants.REDEEM;
		GiftCardBeanInfo giftCardBeanInfo = null;
		boolean isCanceled = false;
		try {
			BBBPerformanceMonitor.start(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);

			Map<String, String> mVLInputs = createValueLinkRequestData(
					pGiftCardNo, pPinNo, getValueLinkGiftCardUtil()
							.stringToHex(pOrderID), pPaymentGroupId,
					BBBCheckoutConstants.REDEEM, pRedemptionAmount, pSite);

			giftCardBeanInfo = invoke(pGiftCardNo, pPinNo, mVLInputs,
					BBBCheckoutConstants.REDEEM);
			if(isLoggingDebug()){
			logDebug("redeem service to Valuelink is executed and giftCardBeanInfo giftcardId is: " + giftCardBeanInfo.getGiftCardID() + " and status is : " + giftCardBeanInfo.getStatus());
			}
		} catch (BBBSystemException bse) {
			isCanceled = true;
			BBBPerformanceMonitor.cancel(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);
			logError("system exception occured in redeem service to Valuelink: " + bse);
			throw bse;
		} catch (BBBBusinessException bbe) {
			isCanceled = true;
			BBBPerformanceMonitor.cancel(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);
			logError("business exception occured in redeem service to Valuelink: " + bbe);
			throw bbe;
		} finally {
			if (!isCanceled) {
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.VALUE_LINK_PROCESSOR,
						methodName);
			}

		}

		if(isLoggingDebug()){
			logDebug("Exiting method ValueLinkGiftCardProcessorImpl.redeem");
		}

		return giftCardBeanInfo;
	}

	/**
	 * This method roll back the previous redeem call.
	 *
	 * 2800 call.
	 *
	 * @param pGiftCardNo
	 * @param pPinNo
	 * @return GiftCardBeanInfo
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public GiftCardBeanInfo redeemRollback(String pGiftCardNo, String pPinNo,
			String pRedemptionAmount, String pOrderID, String pPaymentGroupId,
			String pSite) throws BBBSystemException, BBBBusinessException {

		if(isLoggingDebug()){
			logDebug("Starting method ValueLinkGiftCardProcessorImpl.redeemRollback");
		}

		String methodName = BBBCheckoutConstants.REDEEM_ROLLBACK;
		GiftCardBeanInfo giftCardBeanInfo = null;
		boolean isCanceled = false;
		try {
			BBBPerformanceMonitor.start(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);

			Map<String, String> mVLInputs = createValueLinkRequestData(
					pGiftCardNo, pPinNo, getValueLinkGiftCardUtil()
							.stringToHex(pOrderID), pPaymentGroupId,
					BBBCheckoutConstants.REDEEM_ROLLBACK, pRedemptionAmount,
					pSite);

			giftCardBeanInfo = invoke(pGiftCardNo, pPinNo, mVLInputs,
					BBBCheckoutConstants.REDEEM_ROLLBACK);
			if(isLoggingDebug()){
				logDebug("redeemRollback service to Valuelink is executed and giftCardBeanInfo giftcardId is: " + giftCardBeanInfo.getGiftCardID() + " and status is : " + giftCardBeanInfo.getStatus());
			}
		} catch (BBBSystemException bse) {
			isCanceled = true;
			BBBPerformanceMonitor.cancel(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);
			logError("system exception occured in redeemRollback service to Valuelink: " + bse);
			throw bse;
		} catch (BBBBusinessException bbe) {
			isCanceled = true;
			BBBPerformanceMonitor.cancel(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);
			logError("business exception occured in redeemRollback service to Valuelink: " + bbe);
			throw bbe;
		} finally {
			if (!isCanceled) {
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.VALUE_LINK_PROCESSOR,
						methodName);
			}

		}

		if(isLoggingDebug()){
			logDebug("Exiting method ValueLinkGiftCardProcessorImpl.redeemRollback");
		}
		return giftCardBeanInfo;
	}

	/**
	 * This method roll back the previous redeem call.
	 *
	 * 0704 call
	 *
	 * @param pGiftCardNo
	 * @param pPinNo
	 * @return GiftCardBeanInfo
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public GiftCardBeanInfo timeoutReversal(String pGiftCardNo, String pPinNo,
			String pRedemptionAmount, String pOrderID, String pPaymentGroupId,
			String pSite) throws BBBSystemException, BBBBusinessException {

		if(isLoggingDebug()){
			logDebug("Starting method ValueLinkGiftCardProcessorImpl.timeoutReversal");
		}

		GiftCardBeanInfo giftCardBeanInfo = null;

		Map<String, String> mVLInputs = createValueLinkRequestData(pGiftCardNo,
				pPinNo, pOrderID, pPaymentGroupId,
				BBBCheckoutConstants.CALL_ON_ERROR, pRedemptionAmount, pSite);

		giftCardBeanInfo = invoke(pGiftCardNo, pPinNo, mVLInputs,
				BBBCheckoutConstants.CALL_ON_ERROR);

		if(isLoggingDebug()){
			logDebug("Exiting method ValueLinkGiftCardProcessorImpl.timeoutReversal");
		}
		return giftCardBeanInfo;
	}

	/**
	 * This method roll back the previous redeem call.
	 *
	 * 2010 call.
	 *
	 * @param pGiftCardNo
	 * @param pPinNo
	 * @return GiftCardBeanInfo
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public GiftCardBeanInfo requestWorkingKey(String pGiftCardNo,
			String pPinNo, String pRedemptionAmount, String pOrderID,
			String pPaymentGroupId, String pSite) throws BBBSystemException,
			BBBBusinessException {

		if(isLoggingDebug()){
			logDebug("Starting method ValueLinkGiftCardProcessorImpl.requestWorkingKey");
		}

		GiftCardBeanInfo giftCardBeanInfo = null;

		Map<String, String> mVLInputs = createValueLinkRequestData(pGiftCardNo,
				pPinNo, pOrderID, pPaymentGroupId,
				BBBCheckoutConstants.REQ_WRKING_KEY, pRedemptionAmount, pSite);

		giftCardBeanInfo = invoke(pGiftCardNo, pPinNo, mVLInputs,
				BBBCheckoutConstants.REQ_WRKING_KEY);

		if(isLoggingDebug()){
			logDebug("Exiting method ValueLinkGiftCardProcessorImpl.requestWorkingKey");
		}

		return giftCardBeanInfo;

	}

	/**
	 * This method invokes the ValueLink API for Gift card related calls.
	 *
	 * @param pGiftCardNo
	 * @param pPinNo
	 * @param pVLInputs
	 * @param pCallType
	 * @param pSessionBean
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private GiftCardBeanInfo invoke(String pGiftCardNo, String pPinNo,
			Map<String, String> pVLInputs, String pCallType)
			throws BBBSystemException, BBBBusinessException {

		if(isLoggingDebug()){
			logDebug("Starting method ValueLinkGiftCardProcessorImpl.invoke");
		}

		String balanceResPayload = null;

		if (pVLInputs != null) {
			pVLInputs.put(BBBCheckoutConstants.CALL_TYPE, pCallType);

		}

		balanceResPayload = getValueLinkClient().invoke(pVLInputs);

		GiftCardBeanInfo giftCardBeanInfo = processResponse(balanceResPayload,
				pGiftCardNo, pPinNo);

		if(isLoggingDebug()){
			logDebug("Exiting method ValueLinkGiftCardProcessorImpl.invoke");
		}

		return giftCardBeanInfo;
	}

	/**
	 * This method process ValueLink response.
	 *
	 * <p>
	 * Value Link returns response payLoad in the following format.
	 * <p>
	 * <b>SV.</b>99910949997"+FS+"<b>40</b>2400"+FS+"<b>12</b>100000"+FS+"<b>13<
	 * /b>11212011"+FS+"<b>70</b>7777007222699458"+FS+"<b>42</b>999109499970651
	 * "+FS+"<b>EA</b>31<br>
	 * <b><i>Above example string is having only mandatory fields for request
	 * payload. There are other fields also in the request payload which are
	 * optional and suggested but not mandatory. </i></b>
	 * <p>
	 * <br>
	 * Each field is starts with 2 digit identifier marked in bold above example
	 * string.
	 * <p>
	 * <li>Following values are site specific</li>
	 * <br>
	 * <ol>
	 * tid(terminalID), mid(merchantID), did(DatawireID), localCurrency(Optional
	 * field)
	 * </ol>
	 * <p>
	 * <li>Following fields are application specific.</li>
	 * <br>
	 * <ol>
	 * svcid(Configured value, provided by BBB), AppID(Configured value,
	 * provided by BBB, ONLY Required for SelfRegistration)
	 * </ol>
	 *
	 * <b><u>All the site and application specific fields are saved in the
	 * ConfigureKeys repository as key value pair under following configKeys
	 * </u></b>
	 * <li>valueLinkKeys
	 * <li>valueLinkRequestHeader
	 * <li>valueLinkRequestBody
	 * <li>valueLinkResponseHeader
	 * <li>valueLinkResponseBody
	 * <li>valueLinkKeys_BuyBuyBaby
	 * <li>valueLinkKeys_BedBathUS
	 * <li>valueLinkKeys_BedBathCanada
	 * <p>
	 * <br>
	 * <br>
	 * <font color="green"><b>This method breaks the response payload returned
	 * in above format based on FS, prefix explained above and stores</b><br>
	 * <b>in the GiftCardBeanInfo.</b> </font> <br>
	 * <br>
	 *
	 * @param balanceResPayload
	 * @param pPinNo
	 * @param pGiftCardNo
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public GiftCardBeanInfo processResponse(final String pBalanceResPayload,
			final String pGiftCardNo, final String pPinNo) throws BBBSystemException,
			BBBBusinessException {

		if(isLoggingDebug()){
			logDebug("Starting method ValueLinkGiftCardProcessorImpl.ProcessResponse");
		}

		GiftCardBeanInfo giftCardBeanInfo = null;
		boolean isCanceled = false;
		String methodName = PROCESSRESPONSE;
		String balanceResPayload = pBalanceResPayload;
		try {
			BBBPerformanceMonitor.start(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);

			if (balanceResPayload != null) {
				String timeReversal = null;
				String[] response = balanceResPayload
						.split(BBBCheckoutConstants.TIMEOUT_REVERSAL);

				if (response != null && response.length == 2) {
					timeReversal = response[1];
					balanceResPayload = response[0];
				}

				Map<String, String> balanaceResHeader = getBBBCatalogTools()
						.getConfigValueByconfigType(
								BBBCheckoutConstants.VAL_LNK_RES_HDR);

				Map<String, String> balanaceResBody = getBBBCatalogTools()
						.getConfigValueByconfigType(
								BBBCheckoutConstants.VAL_LNK_RES_BODY);

				if (balanaceResHeader != null && balanaceResBody != null) {

					if(isLoggingDebug()){
					logDebug("balanaceResponseHeader site/application specific key/values from CofigureKeys repository : "
							+ balanaceResHeader);
					logDebug("Response payload : " + balanceResPayload);
					}
					String[] responseFields = null;
					if (balanceResPayload != null) {
						responseFields = balanceResPayload.split(FS + "");
					}

					if (responseFields != null && responseFields.length > 0) {
						giftCardBeanInfo = new GiftCardBeanInfo();
						for (String field : responseFields) {
							if (field != null) {
								if (field
										.startsWith(balanaceResBody
												.get(BBBCheckoutConstants.RESPONSE_CODE))) {
									/**
									 * Setting error or success flag.
									 */
									String[] resCodeArr = mValueLinkGiftCardUtil
											.split(field);
									if (resCodeArr != null
											&& resCodeArr.length > 1) {
										if (BBBCheckoutConstants.RESP_SUCC_CODE
												.equals(resCodeArr[1])) {
											giftCardBeanInfo
													.setStatus(Boolean.TRUE);
											if(isLoggingDebug()){
												logDebug("$$$$$$$$$ ValueLink response returning SUCCESS $$$$$$$$$");
											}
										} else {
											giftCardBeanInfo
													.setStatus(Boolean.FALSE);
											if(isLoggingDebug()){
												logDebug("!!!!!!!!! ValueLink response returning ERROR !!!!!!!!!");
											}
										}
									}
								}

								if (field
										.startsWith(balanaceResBody
												.get(BBBCheckoutConstants.GIFT_CARD_IDN))) {
									// pGiftCardBeanInfo.setGiftCardId(field);
									giftCardBeanInfo.setGiftCardId(pGiftCardNo);
								}

								if (field
										.startsWith(balanaceResBody
												.get(BBBCheckoutConstants.NEW_BALANCE_IDN))) {
									giftCardBeanInfo
											.setBalance(getValueLinkGiftCardUtil()
													.getDollorCentAmount(
															(mValueLinkGiftCardUtil
																	.split(field)[1])));
								}

								if (field
										.startsWith(balanaceResBody
												.get(BBBCheckoutConstants.PREV_BAL_IDN))) {
									giftCardBeanInfo
											.setPreviousBalance(getValueLinkGiftCardUtil()
													.getDollorCentAmount(
															mValueLinkGiftCardUtil
																	.split(field)[1]));
								}

								// Setting user entered PinNo.
								giftCardBeanInfo.setPin(pPinNo);

								// Response detail for gift card status detail

								if (field
										.startsWith(balanaceResBody
												.get(BBBCheckoutConstants.AUTH_CODE_IDN))) {
									giftCardBeanInfo
											.setAuthCode(mValueLinkGiftCardUtil
													.split(field)[1]);
								}

								if (field
										.startsWith(balanaceResBody
												.get(BBBCheckoutConstants.SYS_TRACE_NUM_IDN))) {
									giftCardBeanInfo
											.setTraceNumber(mValueLinkGiftCardUtil
													.split(field)[1]);
								}

								if (field
										.startsWith(balanaceResBody
												.get(BBBCheckoutConstants.RESPONSE_CODE))) {
									giftCardBeanInfo
											.setAuthRespCode(mValueLinkGiftCardUtil
													.split(field)[1]);
								}

								if (field
										.startsWith(balanaceResBody
												.get(BBBCheckoutConstants.CARD_CLASS_IDN))) {
									giftCardBeanInfo
											.setCardClass(mValueLinkGiftCardUtil
													.split(field)[1]);
								}

							}
						}
					}
				}

				if (timeReversal != null && giftCardBeanInfo != null) {
					if(isLoggingDebug()){
						logDebug("!!!!!!!!!!  0704(time reversal) response returned !!!!!!!!!!!");
					}
					giftCardBeanInfo.setTimeoutReversal(Boolean.TRUE);
				}
				if(isLoggingDebug()){
				logDebug("Exiting method ValueLinkGiftCardProcessorImpl.ProcessResponse, giftCardBeanInfo: "
						+ giftCardBeanInfo);
				}

			}
		} catch (BBBSystemException bse) {
			isCanceled = true;
			BBBPerformanceMonitor.cancel(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);
			logError("system exception occured in ValueLinkGiftCardProcessorImpl.ProcessResponse : " + bse);
			throw bse;
		} catch (BBBBusinessException bbe) {
			isCanceled = true;
			BBBPerformanceMonitor.cancel(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);
			logError("business exception occured in ValueLinkGiftCardProcessorImpl.ProcessResponse : " + bbe);
			throw bbe;
		} finally {
			if (!isCanceled) {
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.VALUE_LINK_PROCESSOR,
						methodName);
			}

		}
		return giftCardBeanInfo;
	}

	public GiftCardBeanInfo processResponse(String balanceResPayload) throws BBBSystemException,
			BBBBusinessException {
		return processResponse(balanceResPayload, null, null);
	}

	/**
	 * This method creates ValuLink getBalance call related request data.
	 * <p>
	 *
	 * @param pPinNo
	 * @param pGiftCardNo
	 * @param pPaymentGroupId
	 * @param pOrderID
	 * @param pSite
	 * @return Map<String, Object>
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	private Map<String, String> createValueLinkRequestData(String pGiftCardNo,
			String pPinNo, String pOrderID, String pPaymentGroupId,
			String pCallType, String pRedeemAmount, String pSite)
			throws BBBSystemException, BBBBusinessException {
		if(isLoggingDebug()){
			logDebug("Starting method ValueLinkGiftCardProcessorImpl.createValueLinkBalanceRequestData");
		}
		boolean isCanceled = false;
		String methodName = CREATEVLREQDATA;
		Map<String, String> mVLInputs = new HashMap<String, String>();
		try {
			BBBPerformanceMonitor.start(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);
			createValuLinkCommonRequestData(mVLInputs, pPaymentGroupId, pSite);

			Map<String, String> reqPayLoad = new HashMap<String, String>();

			String requestPayLoad = createValueLinkRequestPayload(pGiftCardNo,
					pPinNo, getVLSiteSpecificConfigType(pSite), pCallType,
					pRedeemAmount, pOrderID);

			reqPayLoad
					.put(BBBCheckoutConstants.REQUEST_PAYLOAD, requestPayLoad);

			if (reqPayLoad != null) {
				mVLInputs.putAll(reqPayLoad);
			}
			if(isLoggingDebug()){
				logDebug("Exiting method ValueLinkGiftCardProcessorImpl.createValueLinkBalanceRequestData");
			}
		} catch (BBBSystemException bse) {
			isCanceled = true;
			BBBPerformanceMonitor.cancel(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);
			logError("system exception occured in ValueLinkGiftCardProcessorImpl.createValueLinkRequestData : " + bse);
			throw bse;
		} catch (BBBBusinessException bbe) {
			isCanceled = true;
			BBBPerformanceMonitor.cancel(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);
			logError("business exception occured in ValueLinkGiftCardProcessorImpl.createValueLinkRequestData : " + bbe);
			throw bbe;
		} finally {
			if (!isCanceled) {
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.VALUE_LINK_PROCESSOR,
						methodName);
			}

		}
		return mVLInputs;
	}

	/**
	 * This method returns value link site specific configKey name to call
	 * ConfigurKeys Repository to fetch site specific data for Value link.
	 *
	 * @param pSite
	 *
	 * @return String
	 */
	private String getVLSiteSpecificConfigType(String pSite) {

		if(isLoggingDebug()){
			logDebug("Starting method ValueLinkGiftCardProcessorImpl.getVLSiteSpecificConfigType");
		}

		String siteSpecificConfigType = new StringBuffer()
				.append(BBBCheckoutConstants.VALUE_LINK_KEYS)
				.append(BBBCheckoutConstants.UNDERSCORE).append(pSite)
				.toString();
		if(isLoggingDebug()){
		logDebug("Exiting method ValueLinkGiftCardProcessorImpl.getVLSiteSpecificConfigType : "
				+ siteSpecificConfigType);
		}
		return siteSpecificConfigType;

	}

	/**
	 * This method creates common request header related data to make ValueLink
	 * API call like tid, mid, did, svcid, AppID
	 *
	 * @param pVLInputs
	 * @param pPaymentGroupId
	 * @param pSite
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private void createValuLinkCommonRequestData(Map<String, String> pVLInputs,
			String pPaymentGroupId, String pSite) throws BBBSystemException,
			BBBBusinessException {
		if(isLoggingDebug()){
		logDebug("Starting method ValueLinkGiftCardProcessorImpl.createValuLinkCommonRequestData. Input parameters --> "
				+ "pVLInputs:"
				+ pVLInputs
				+ ", pPaymentGroupId"
				+ pPaymentGroupId);
		}
		boolean isCanceled = false;
		String methodName = CRVLCOMMONREQDATA;
		try {
			BBBPerformanceMonitor.start(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);
			if (getBBBCatalogTools().getConfigValueByconfigType(
					BBBCheckoutConstants.VALUE_LINK_KEYS) != null) {
				pVLInputs.putAll(getBBBCatalogTools()
						.getConfigValueByconfigType(
								BBBCheckoutConstants.VALUE_LINK_KEYS));
			}

			if (getBBBCatalogTools().getConfigValueByconfigType(
					getVLSiteSpecificConfigType(pSite)) != null) {
				pVLInputs.putAll(getBBBCatalogTools()
						.getConfigValueByconfigType(
								getVLSiteSpecificConfigType(pSite)));
			}

			pVLInputs
					.put(BBBCheckoutConstants.UNIQUE_TRANS_ID,
							getValueLinkGiftCardUtil()
									.generateUniqueClientRef(
											pVLInputs
													.get(BBBCheckoutConstants.CLIENT_REF_VERSION)));

			if(isLoggingDebug()){
				logDebug("Exiting method ValueLinkGiftCardProcessorImpl.createValuLinkCommonRequestData");
			}
		} catch (BBBSystemException bse) {
			isCanceled = true;
			BBBPerformanceMonitor.cancel(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);
			logError("system exception occured in ValueLinkGiftCardProcessorImpl.createValuLinkCommonRequestData : " + bse);
			throw bse;
		} catch (BBBBusinessException bbe) {
			isCanceled = true;
			BBBPerformanceMonitor.cancel(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);
			logError("business exception occured in ValueLinkGiftCardProcessorImpl.createValuLinkCommonRequestData : " + bbe);
			throw bbe;
		} finally {
			if (!isCanceled) {
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.VALUE_LINK_PROCESSOR,
						methodName);
			}

		}
	}

	/**
	 * This method creates request payload for getBalance call in following
	 * format.
	 * <p>
	 * <b>SV.</b>99910949997"+FS+"<b>40</b>2400"+FS+"<b>12</b>100000"+FS+"<b>13<
	 * /b>11212011"+FS+"<b>70</b>7777007222699458"+FS+"<b>42</b>999109499970651
	 * "+FS+"<b>EA</b>31<br>
	 * <b><i>Above example string is having only mandatory fields for request
	 * payload. There are other fields also in the request payload which are
	 * optional and suggested but not mandatory. </i></b>
	 * <p>
	 * FS is field separator with value ,(comma) <br>
	 * Each field is starts with 2 digit identifier marked in bold above example
	 * string.
	 * <p>
	 * <li>Following values are site specific</li>
	 * <br>
	 * <ol>
	 * tid(terminalID), mid(merchantID), did(DatawireID), localCurrency(Optional
	 * field)
	 * </ol>
	 * <p>
	 * <li>Following fields are application specific.</li>
	 * <br>
	 * <ol>
	 * svcid(Configured value, provided by BBB), AppID(Configured value,
	 * provided by BBB, ONLY Required for SelfRegistration)
	 * </ol>
	 *
	 * <b><u>All the site and application specific fields are saved in the
	 * ConfigureKeys repository as key value pair under following configKeys
	 * </u></b>
	 * <li>valueLinkKeys
	 * <li>valueLinkRequestHeader
	 * <li>valueLinkRequestBody
	 * <li>valueLinkKeys_BuyBuyBaby
	 * <li>valueLinkKeys_BedBathUS
	 * <li>valueLinkKeys_BedBathCanada
	 * <p>
	 *
	 * @param pPinNo
	 * @param pGiftCardNo
	 * @param pValueLinkSiteKeys
	 * @param pOrderID
	 * @return String
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	private String createValueLinkRequestPayload(String pGiftCardNo,
			String pPinNo, String pValueLinkSiteKeys, String pCallType,
			String pRedeemAmount, String pOrderID) throws BBBSystemException,
			BBBBusinessException {

		if(isLoggingDebug()){
			logDebug("Starting method ValueLinkGiftCardProcessorImpl.createValueLinkRequestPayload");
		}

		boolean isCanceled = false;
		String methodName = CRVLREQPAYLOAD;
		String requestPayLoad = null;
		try {
			BBBPerformanceMonitor.start(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);
			/**
			 * Example request payload String
			 * "SV.99910949997"+FS+"402400"+FS+"12100000"
			 * +FS+"1311212011"+FS+"707777007222699458"
			 * +FS+"42999109499970651"+FS+"EA31"
			 */

			Map<String, String> siteSpecific = getBBBCatalogTools()
					.getConfigValueByconfigType(pValueLinkSiteKeys);

			String mid = null;

			StringBuffer requestBuffer = new StringBuffer();

			Map<String, String> commonValueLinkKeys = getBBBCatalogTools()
					.getConfigValueByconfigType(
							BBBCheckoutConstants.VALUE_LINK_KEYS);

			if (siteSpecific != null) {

				mid = siteSpecific.get(BBBCheckoutConstants.MID);

				Map<String, String> requestHeader = getBBBCatalogTools()
						.getConfigValueByconfigType(
								BBBCheckoutConstants.VAL_LNK_REQ_HDR);

				Map<String, String> requestBody = getBBBCatalogTools()
						.getConfigValueByconfigType(
								BBBCheckoutConstants.VAL_LNK_REQ_BODY);

				getValueLinkGiftCardValidation().preValueLinkRequestPayload(
						commonValueLinkKeys, requestHeader, requestBody,
						siteSpecific, getValueLinkBlankValue(),
						pValueLinkSiteKeys);

				if (requestHeader != null && requestBody != null
						&& commonValueLinkKeys != null) {

					// Request header creation
					requestBuffer
							.append(createRequestPayloadHeader(requestHeader,
									pCallType, mid, commonValueLinkKeys));

					// Request Body creation for REQUIRED fields
					requestBuffer
							.append(createRequestPayloadBodyRequiredFields(
									pGiftCardNo, pCallType, pRedeemAmount, mid,
									requestBody, commonValueLinkKeys));

					// Request Body creation for SUGGESTED fields
					requestBuffer
							.append(createRequestPayloadBodySuggestedFields(
									pCallType, siteSpecific, requestBody,
									pOrderID));

					// Request Body creation for Optional fields
					requestBuffer
							.append(createRequestPayloadBodyOptionalFields(
									pCallType, siteSpecific, requestBody,
									pPinNo));
				}
			}

			requestPayLoad = requestBuffer.toString();
			if(isLoggingDebug()){
			logDebug("Final balance request payload : " + requestPayLoad);
			logDebug("Exiting method ValueLinkGiftCardProcessorImpl.createValueLinkRequestPayload");
			}
		} catch (BBBSystemException bse) {
			isCanceled = true;
			BBBPerformanceMonitor.cancel(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);
			logError("system exception occured in ValueLinkGiftCardProcessorImpl.createValueLinkRequestPayload : " + bse);
			throw bse;
		} catch (BBBBusinessException bbe) {
			isCanceled = true;
			BBBPerformanceMonitor.cancel(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);
			logError("business exception occured in ValueLinkGiftCardProcessorImpl.createValueLinkRequestPayload : " + bbe);
			throw bbe;
		} finally {
			if (!isCanceled) {
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.VALUE_LINK_PROCESSOR,
						methodName);
			}

		}
		return requestPayLoad;
	}

	/**
	 * This method creates request payload optional fields.
	 *
	 * @param pCallType
	 * @param siteSpecific
	 * @param requestBuffer
	 * @param requestBody
	 * @param fieldSeparator
	 */
	public StringBuffer createRequestPayloadBodyOptionalFields(
			String pCallType, Map<String, String> siteSpecific,
			Map<String, String> requestBody, String pinNumber) {
		if(isLoggingDebug()){
			logDebug("Starting method ValueLinkGiftCardProcessorImpl.createRequestPayloadBodyOptionalFields");
		}

		StringBuffer requestBuffer = new StringBuffer();
		boolean isCanceled = false;
		String methodName = REQLOADBODYOPTFLD;
		try {
			BBBPerformanceMonitor.start(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);
			if (Boolean.TRUE.toString().equalsIgnoreCase(
					getReqBodyOPTIONALFields())
					&& requestBody != null && siteSpecific != null) {
				if(isLoggingDebug()){
				logDebug("Adding optional fields in request payload : "
						+ requestBuffer);
				}

				if (!getValueLinkGiftCardUtil().isEmpty(
						requestBody.get(BBBCheckoutConstants.USER1_IDN),
						getValueLinkBlankValue())
						&& !getValueLinkGiftCardUtil()
								.isEmpty(
										requestBody
												.get(BBBCheckoutConstants.USER1_VAL),
										getValueLinkBlankValue())) {
					requestBuffer
							.append(FS)
							.append(requestBody
									.get(BBBCheckoutConstants.USER1_IDN))
							.append(requestBody
									.get(BBBCheckoutConstants.USER1_VAL));
				}

				if (!getValueLinkGiftCardUtil().isEmpty(
						requestBody.get(BBBCheckoutConstants.USER2_IDN),
						getValueLinkBlankValue())
						&& !getValueLinkGiftCardUtil()
								.isEmpty(
										requestBody
												.get(BBBCheckoutConstants.USER2_VAL),
										getValueLinkBlankValue())) {
					requestBuffer
							.append(FS)
							.append(requestBody
									.get(BBBCheckoutConstants.USER2_IDN))
							.append(requestBody
									.get(BBBCheckoutConstants.USER2_VAL));
				}

				if (!getValueLinkGiftCardUtil().isEmpty(
						requestBody.get(BBBCheckoutConstants.SEC_CARD_VAL_IDN),
						getValueLinkBlankValue())
						&& !getValueLinkGiftCardUtil()
								.isEmpty(
										requestBody
												.get(BBBCheckoutConstants.SECURITY_CARD_VALUE),
										getValueLinkBlankValue())) {
					requestBuffer
							.append(FS)
							.append(requestBody
									.get(BBBCheckoutConstants.SEC_CARD_VAL_IDN))
							.append(requestBody
									.get(BBBCheckoutConstants.SECURITY_CARD_VALUE));
				}

				if (!getValueLinkGiftCardUtil().isEmpty(
						requestBody.get(BBBCheckoutConstants.EXT_ACC_IDN),
						getValueLinkBlankValue())) {
					requestBuffer
							.append(FS)
							.append(requestBody
									.get(BBBCheckoutConstants.EXT_ACC_IDN))
							.append(getValueLinkEncryptor().encryptPin(
									pinNumber));
					// .append("641C2EE048766B9E9E15B62758A9482D");
				}

				if (!getValueLinkGiftCardUtil()
						.isEmpty(
								requestBody
										.get(BBBCheckoutConstants.ALT_MERCHANT_NUMBER_IDN),
								getValueLinkBlankValue())
						&& !getValueLinkGiftCardUtil()
								.isEmpty(
										siteSpecific
												.get(BBBCheckoutConstants.ALT_MERCHANT_VAL),
										getValueLinkBlankValue())) {
					requestBuffer
							.append(FS)
							.append(requestBody
									.get(BBBCheckoutConstants.ALT_MERCHANT_NUMBER_IDN))
							.append(siteSpecific
									.get(BBBCheckoutConstants.ALT_MERCHANT_VAL));
				}

				if (!getValueLinkGiftCardUtil().isEmpty(
						requestBody.get(BBBCheckoutConstants.CLERKID_IDN),
						getValueLinkBlankValue())
						&& !getValueLinkGiftCardUtil()
								.isEmpty(
										requestBody
												.get(BBBCheckoutConstants.CLERKID_VAL),
										getValueLinkBlankValue())) {
					requestBuffer
							.append(FS)
							.append(requestBody
									.get(BBBCheckoutConstants.CLERKID_IDN))
							.append(requestBody
									.get(BBBCheckoutConstants.CLERKID_VAL));
				}

				if (!getValueLinkGiftCardUtil()
						.isEmpty(
								requestBody
										.get(BBBCheckoutConstants.ACCOUNT_ORIGIN_IDN),
								getValueLinkBlankValue())
						&& !getValueLinkGiftCardUtil()
								.isEmpty(
										requestBody
												.get(BBBCheckoutConstants.ACC_ORIGIN_VAL),
										getValueLinkBlankValue())) {
					requestBuffer
							.append(FS)
							.append(requestBody
									.get(BBBCheckoutConstants.ACCOUNT_ORIGIN_IDN))
							.append(requestBody
									.get(BBBCheckoutConstants.ACC_ORIGIN_VAL));
				}

				if (!getValueLinkGiftCardUtil().isEmpty(
						requestBody.get(BBBCheckoutConstants.FOREIGN_CODE_IDN),
						getValueLinkBlankValue())
						&& !getValueLinkGiftCardUtil()
								.isEmpty(
										requestBody
												.get(BBBCheckoutConstants.FOREIGN_CODE_VAL),
										getValueLinkBlankValue())) {
					requestBuffer
							.append(FS)
							.append(requestBody
									.get(BBBCheckoutConstants.FOREIGN_CODE_IDN))
							.append(requestBody
									.get(BBBCheckoutConstants.FOREIGN_CODE_VAL));
				}

				if (BBBCheckoutConstants.GET_BALANCE.equals(pCallType)) {
					if (!getValueLinkGiftCardUtil()
							.isEmpty(
									requestBody
											.get(BBBCheckoutConstants.LOCAL_CURR_IDN),
									getValueLinkBlankValue())
							&& !getValueLinkGiftCardUtil()
									.isEmpty(
											siteSpecific
													.get(BBBCheckoutConstants.LOCAL_CURR_VAL),
											getValueLinkBlankValue())) {
						requestBuffer
								.append(FS)
								.append(requestBody
										.get(BBBCheckoutConstants.LOCAL_CURR_IDN))
								.append(siteSpecific
										.get(BBBCheckoutConstants.LOCAL_CURR_VAL));
					}
				}

				if (!getValueLinkGiftCardUtil().isEmpty(
						requestBody.get(BBBCheckoutConstants.MER_KEY_IDN),
						getValueLinkBlankValue())
						&& !getValueLinkGiftCardUtil()
								.isEmpty(
										requestBody
												.get(BBBCheckoutConstants.MER_KEY_VAL),
										getValueLinkBlankValue())) {
					requestBuffer
							.append(FS)
							.append(requestBody
									.get(BBBCheckoutConstants.MER_KEY_IDN))
							.append(requestBody
									.get(BBBCheckoutConstants.MER_KEY_VAL));
				}

				if (!getValueLinkGiftCardUtil().isEmpty(
						requestBody.get(BBBCheckoutConstants.ECHO_BACK_IDN),
						getValueLinkBlankValue())
						&& !getValueLinkGiftCardUtil()
								.isEmpty(
										requestBody
												.get(BBBCheckoutConstants.ECHO_BACK_VAL),
										getValueLinkBlankValue())) {
					requestBuffer
							.append(FS)
							.append(requestBody
									.get(BBBCheckoutConstants.ECHO_BACK_IDN))
							.append(requestBody
									.get(BBBCheckoutConstants.ECHO_BACK_VAL));
				}

				if (BBBCheckoutConstants.REDEEM.equals(pCallType)
						|| BBBCheckoutConstants.REDEEM_ROLLBACK
								.equals(pCallType)
						|| BBBCheckoutConstants.CALL_ON_ERROR.equals(pCallType)
						|| BBBCheckoutConstants.REQ_WRKING_KEY
								.equals(pCallType)) {
					if (!getValueLinkGiftCardUtil().isEmpty(
							requestBody.get(BBBCheckoutConstants.SIC_CODE_IDN),
							getValueLinkBlankValue())
							&& !getValueLinkGiftCardUtil()
									.isEmpty(
											requestBody
													.get(BBBCheckoutConstants.SIC_CODE_VAL),
											getValueLinkBlankValue())) {
						requestBuffer
								.append(FS)
								.append(requestBody
										.get(BBBCheckoutConstants.SIC_CODE_IDN))
								.append(requestBody
										.get(BBBCheckoutConstants.SIC_CODE_VAL));
					}
				}

			}
			if(isLoggingDebug()){
			logDebug("Exiting method ValueLinkGiftCardProcessorImpl.createRequestPayloadBodyOptionalFields: "
					+ requestBuffer);
			}
		} finally {
			if (!isCanceled) {
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.VALUE_LINK_PROCESSOR,
						methodName);
			}

		}
		return requestBuffer;
	}

	/**
	 * This method creates request payload suggested fields.
	 *
	 * @param pCallType
	 * @param pSiteSpecific
	 * @param requestBuffer
	 * @param pRequestBody
	 * @param pFieldSeparator
	 * @param pOrderID
	 */
	public StringBuffer createRequestPayloadBodySuggestedFields(
			String pCallType, Map<String, String> pSiteSpecific,
			Map<String, String> pRequestBody, String pOrderID) {
		if(isLoggingDebug()){
			logDebug("Starting method ValueLinkGiftCardProcessorImpl.createRequestPayloadBodySuggestedFields");
		}
		StringBuffer requestBuffer = new StringBuffer();
		boolean isCanceled = false;
		String methodName = CREATEREQSUGGESTEDFLD;
		try {
			BBBPerformanceMonitor.start(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);
			if (Boolean.TRUE.toString().equalsIgnoreCase(
					getReqBodySUGGESTEDFields())
					&& pRequestBody != null && pSiteSpecific != null) {
				// Commenting because orderId is exceeding 17 digit length after
				// converted
				// to hex string.
				/*
				 * if (BBBCheckoutConstants.REDEEM.equals(pCallType) ||
				 * BBBCheckoutConstants.REDEEM_ROLLBACK.equals(pCallType) ||
				 * BBBCheckoutConstants.CALL_ON_ERROR.equals(pCallType) ||
				 * BBBCheckoutConstants.REQ_WRKING_KEY .equals(pCallType)) {
				 * requestBuffer .append(FS) .append(pRequestBody
				 * .get(BBBCheckoutConstants.TERM_TRANS_IDN)) .append(pOrderID);
				 * }
				 */

				if (!getValueLinkGiftCardUtil().isEmpty(
						pRequestBody.get(BBBCheckoutConstants.POST_DATE_IDN),
						getValueLinkBlankValue())
						&& !getValueLinkGiftCardUtil()
								.isEmpty(
										pRequestBody
												.get(BBBCheckoutConstants.POST_DATE_VAL),
										getValueLinkBlankValue())) {
					requestBuffer
							.append(FS)
							.append(pRequestBody
									.get(BBBCheckoutConstants.POST_DATE_IDN))
							.append(pRequestBody
									.get(BBBCheckoutConstants.POST_DATE_VAL));
				}

				if (BBBCheckoutConstants.REDEEM.equals(pCallType)
						|| BBBCheckoutConstants.REDEEM_ROLLBACK
								.equals(pCallType)
						|| BBBCheckoutConstants.CALL_ON_ERROR.equals(pCallType)
						|| BBBCheckoutConstants.REQ_WRKING_KEY
								.equals(pCallType)) {
					if (!getValueLinkGiftCardUtil()
							.isEmpty(
									pRequestBody
											.get(BBBCheckoutConstants.LOCAL_CURR_IDN),
									getValueLinkBlankValue())
							&& !getValueLinkGiftCardUtil()
									.isEmpty(
											pSiteSpecific
													.get(BBBCheckoutConstants.LOCAL_CURR_VAL),
											getValueLinkBlankValue())) {
						requestBuffer
								.append(FS)
								.append(pRequestBody
										.get(BBBCheckoutConstants.LOCAL_CURR_IDN))
								.append(pSiteSpecific
										.get(BBBCheckoutConstants.LOCAL_CURR_VAL));
					}
				}

			}
			if(isLoggingDebug()){
				logDebug("Exiting method ValueLinkGiftCardProcessorImpl.createRequestPayloadBodySuggestedFields"
				+ requestBuffer);
			}
		} finally {
			if (!isCanceled) {
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.VALUE_LINK_PROCESSOR,
						methodName);
			}

		}
		return requestBuffer;
	}

	/**
	 * This method creates request payload required fields.
	 *
	 * @param pGiftCardNo
	 * @param pCallType
	 * @param pRedeemAmount
	 * @param mid
	 * @param tid
	 * @param requestBuffer
	 * @param requestBody
	 * @param fieldSeparator
	 * @param commonValueLinkKeys
	 */
	public StringBuffer createRequestPayloadBodyRequiredFields(
			String pGiftCardNo, String pCallType, String pRedeemAmount,
			String mid, Map<String, String> requestBody,
			Map<String, String> commonValueLinkKeys) {
		if(isLoggingDebug()){
		logDebug("Starting method ValueLinkGiftCardProcessorImpl.createRequestPayloadBodyRequiredFields");
		logDebug("input parameters --> " + ", pGiftCardNo: " + pGiftCardNo
				+ ", pCallType: " + pCallType + ", requestBody: " + requestBody
				+ ", mid: " + mid);
		}
		StringBuffer requestBuffer = new StringBuffer();
		boolean isCanceled = false;
		String methodName = CRREQPAYLOADREQUIREDFLD;
		try {
			BBBPerformanceMonitor.start(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);
			if (requestBody != null) {
				// Request Body creation for REQUIRED fields
				requestBuffer
						.append(FS)
						.append(requestBody
								.get(BBBCheckoutConstants.LOC_TRANS_TM_IDN))
						.append(EncryptionUtil.getDateString("HHmmss"))
						.append(FS)
						.append(requestBody
								.get(BBBCheckoutConstants.LOC_TRANS_DT_IDN))
						.append(EncryptionUtil.getDateString("MMddyyyy"))
						.append(FS)
						.append(requestBody
								.get(BBBCheckoutConstants.MID_TID_IDN))
						.append(mid)
						.append(commonValueLinkKeys
								.get(BBBCheckoutConstants.TERMINAL_ID))
						.append(FS)
						.append(requestBody
								.get(BBBCheckoutConstants.SOURCE_CODE_IDN))
						.append(requestBody
								.get(BBBCheckoutConstants.SOURCE_CODE_VAL));

				if (BBBCheckoutConstants.GET_BALANCE.equals(pCallType)) {
					requestBuffer
							.append(FS)
							.append(requestBody
									.get(BBBCheckoutConstants.GIFT_CARD_IDN))
							.append(pGiftCardNo);
				}

				if (BBBCheckoutConstants.REDEEM.equals(pCallType)
						|| BBBCheckoutConstants.REDEEM_ROLLBACK
								.equals(pCallType)
						|| BBBCheckoutConstants.CALL_ON_ERROR.equals(pCallType)
						|| BBBCheckoutConstants.REQ_WRKING_KEY
								.equals(pCallType)) {
					requestBuffer
							.append(FS)
							.append(requestBody
									.get(BBBCheckoutConstants.EMB_CARD_NUM_IDN))
							.append(pGiftCardNo)
							.append(FS)
							.append(requestBody
									.get(BBBCheckoutConstants.TRAN_AMOUNT_IDN))
							.append(pRedeemAmount);
				}

			}
		} finally {
			if (!isCanceled) {
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.VALUE_LINK_PROCESSOR,
						methodName);
			}

		}
		if(isLoggingDebug()){
		logDebug("Exiting method ValueLinkGiftCardProcessorImpl.createRequestPayloadBodyRequiredFields");
		logDebug("returned request payload header --> " + requestBuffer);
		}
		return requestBuffer;
	}

	/**
	 * This method creates request header.
	 *
	 * @param FS
	 * @param pRequestHeader
	 * @param pCallType
	 * @param pMid
	 * @param pCommonValueLinkKeys
	 * @return StringBuffer
	 */
	private StringBuffer createRequestPayloadHeader(
			Map<String, String> pRequestHeader, String pCallType, String pMid,
			Map<String, String> pCommonValueLinkKeys) {
		if(isLoggingDebug()){
		logDebug("Starting method ValueLinkGiftCardProcessorImpl.createRequestPayloadHeader");
		logDebug("input parameters --> " + ", requestHeader: " + pRequestHeader
				+ ", pCallType: " + pCallType + ", pCommonValueLinkKeys: "
				+ pCommonValueLinkKeys);
		}

		StringBuffer requestBuffer = new StringBuffer();

		boolean isCanceled = false;
		String methodName = CRREQPAYLOADHEADER;
		try {
			BBBPerformanceMonitor.start(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);
			if (pRequestHeader != null) {

				String transReqCodeVal = getTransactionRequestCode(
						pCommonValueLinkKeys, pCallType);

				requestBuffer
						.append(pRequestHeader
								.get(BBBCheckoutConstants.MESSAGE_VAL))
						.append(pMid)
						.append(FS)
						.append(pRequestHeader
								.get(BBBCheckoutConstants.VER_NUMBER_VAL))
						.append(pRequestHeader
								.get(BBBCheckoutConstants.FORMAT_NUMBER_VAL))
						.append(transReqCodeVal);

			}
		} finally {
			if (!isCanceled) {
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.VALUE_LINK_PROCESSOR,
						methodName);
			}

		}
		if(isLoggingDebug()){
		logDebug("Exiting method ValueLinkGiftCardProcessorImpl.createRequestPayloadHeader");
		logDebug("Returned request payload header --> " + requestBuffer);
		}
		return requestBuffer;
	}

	/**
	 * This method returns TransactionRequestCode based on call type.
	 *
	 * @param commonValueLinkKeys
	 * @param pCallType
	 * @returnString
	 */
	private String getTransactionRequestCode(
			Map<String, String> commonValueLinkKeys, String pCallType) {
		if(isLoggingDebug()){
		logDebug("Starting method ValueLinkGiftCardProcessorImpl.getTransactionRequestCode");
		logDebug("input parameters --> pCallType: " + pCallType
				+ ", commonValueLinkKeys: " + commonValueLinkKeys);
		}

		String value = null;
		boolean isCanceled = false;
		String methodName = GETTRANSREQCODE;
		try {
			BBBPerformanceMonitor.start(
					BBBPerformanceConstants.VALUE_LINK_PROCESSOR, methodName);
		if (commonValueLinkKeys != null) {

			if (BBBCheckoutConstants.GET_BALANCE.equals(pCallType)) {
				value = commonValueLinkKeys
						.get(BBBCheckoutConstants.TRANS_REQ_CD_BAL);
			}
			if (BBBCheckoutConstants.REDEEM.equals(pCallType)) {
				value = commonValueLinkKeys
						.get(BBBCheckoutConstants.TRANS_REQ_CD_RED);
			}
			if (BBBCheckoutConstants.REDEEM_ROLLBACK.equals(pCallType)) {
				value = commonValueLinkKeys
						.get(BBBCheckoutConstants.TRANS_REQ_CD_UNRED);
			}
			if (BBBCheckoutConstants.CALL_ON_ERROR.equals(pCallType)) {
				value = commonValueLinkKeys
						.get(BBBCheckoutConstants.TRANS_REQ_CD_VAL_ER);
			}
			if (BBBCheckoutConstants.REQ_WRKING_KEY.equals(pCallType)) {
				value = commonValueLinkKeys
						.get(BBBCheckoutConstants.TRANS_REQ_CD_WORK_KEY);
			}
		}
		}finally {
			if (!isCanceled) {
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.VALUE_LINK_PROCESSOR,
						methodName);
			}

		}
		if(isLoggingDebug()){
		logDebug("Exiting method ValueLinkGiftCardProcessorImpl.getTransactionRequestCode --> returned value: "
				+ value);
		}
		return value;
	}

	/**
	 * @return the mValueLinkClient
	 */
	public ValueLinkClient getValueLinkClient() {
		return mValueLinkClient;
	}

	/**
	 * @param pValueLinkClient
	 *            the mValueLinkClient to set
	 */
	public void setValueLinkClient(ValueLinkClient pValueLinkClient) {
		mValueLinkClient = pValueLinkClient;
	}

	/**
	 * @return the mBBBCatalogTools
	 */
	public BBBCatalogTools getBBBCatalogTools() {
		return mBBBCatalogTools;
	}

	/**
	 * @param pBBBCatalogTools
	 *            the mBBBCatalogTools to set
	 */
	public void setBBBCatalogTools(BBBCatalogTools pBBBCatalogTools) {
		mBBBCatalogTools = pBBBCatalogTools;
	}

	/**
	 * @return the mReqBodySUGGESTEDFields
	 */
	public String getReqBodySUGGESTEDFields() {
		return mReqBodySUGGESTEDFields;
	}

	/**
	 * @param pReqBodySUGGESTEDFields
	 *            the mReqBodySUGGESTEDFields to set
	 */
	public void setReqBodySUGGESTEDFields(String pReqBodySUGGESTEDFields) {

		mReqBodySUGGESTEDFields = pReqBodySUGGESTEDFields;
	}

	/**
	 * @return the mReqBodyOPTIONALFields
	 */
	public String getReqBodyOPTIONALFields() {
		return mReqBodyOPTIONALFields;
	}

	/**
	 * @param pReqBodyOPTIONALFields
	 *            the mReqBodyOPTIONALFields to set
	 */
	public void setReqBodyOPTIONALFields(String pReqBodyOPTIONALFields) {
		mReqBodyOPTIONALFields = pReqBodyOPTIONALFields;
	}

	/**
	 * @return the mValueLinkBlankValue
	 */
	public String getValueLinkBlankValue() {
		return mValueLinkBlankValue;
	}

	/**
	 * @param pValueLinkBlankValue
	 *            the mValueLinkBlankValue to set
	 */
	public void setValueLinkBlankValue(String pValueLinkBlankValue) {
		mValueLinkBlankValue = pValueLinkBlankValue;
	}

	/**
	 * @return the mValueLinkGiftCardUtil
	 */
	public ValueLinkGiftCardUtil getValueLinkGiftCardUtil() {
		return mValueLinkGiftCardUtil;
	}

	/**
	 * @param pValueLinkGiftCardUtil
	 *            the mValueLinkGiftCardUtil to set
	 */
	public void setValueLinkGiftCardUtil(
			ValueLinkGiftCardUtil pValueLinkGiftCardUtil) {
		mValueLinkGiftCardUtil = pValueLinkGiftCardUtil;
	}

	/**
	 * @return the mValueLinkEncryptor
	 */
	public ValueLinkEncryptor getValueLinkEncryptor() {
		return mValueLinkEncryptor;
	}

	/**
	 * @param pValueLinkEncryptor
	 *            the mValueLinkEncryptor to set
	 */
	public void setValueLinkEncryptor(ValueLinkEncryptor pValueLinkEncryptor) {
		mValueLinkEncryptor = pValueLinkEncryptor;
	}

	/**
	 * @return the mValueLinkGiftCardValidation
	 */
	public ValueLinkGiftCardValidation getValueLinkGiftCardValidation() {
		return mValueLinkGiftCardValidation;
	}

	/**
	 * @param pValueLinkGiftCardValidation the mValueLinkGiftCardValidation to set
	 */
	public void setValueLinkGiftCardValidation(
			ValueLinkGiftCardValidation pValueLinkGiftCardValidation) {
		mValueLinkGiftCardValidation = pValueLinkGiftCardValidation;
	}
}
