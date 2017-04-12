package com.bbb.valuelink;

import java.util.Map;

import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;

/**
 * @author vagra4
 * 
 */

public class ValueLinkGiftCardValidation extends BBBGenericService {

	/**
	 * This method validates input from CofigeKeys repository.
	 * 
	 * @param pCommonVLKeys
	 * @param pRequestHeader
	 * @param pRequestBody
	 * @param pSiteSpecific
	 * @param pVLiteKeys
	 * @throws BBBBusinessException
	 */
	public void preValueLinkRequestPayload(
			final Map<String, String> pCommonVLKeys,
			final Map<String, String> pRequestHeader,
			final Map<String, String> pRequestBody,
			final Map<String, String> pSiteSpecific, final String pVLBlankValue,
			final String pVLiteKeys) throws BBBBusinessException {

		
		logDebug("Starting method ValueLinkGiftCardValidation.preValueLinkRequestPayload");
		

		/**
		 * common keys
		 */
		commonKeyValidation(pCommonVLKeys, pVLBlankValue);

		/**
		 * Request header
		 */
		reqHeaderValidation(pRequestHeader, pVLBlankValue);

		/**
		 * Request body
		 */

		reqBodyValidation(pRequestBody, pVLBlankValue);

		// site specific
		siteSpecificValidation(pSiteSpecific, pVLBlankValue,
				pVLiteKeys);

		
		logDebug("Exiting method ValueLinkGiftCardValidation.preValueLinkRequestPayload");
		

	}

	/**
	 * @param pSiteSpecific
	 * @param pVLBlankValue
	 * @param pVLSiteKeys
	 * @throws BBBBusinessException
	 */
	private void siteSpecificValidation(final Map<String, String> pSiteSpecific,
			final String pVLBlankValue, final String pVLSiteKeys)
			throws BBBBusinessException {
		
		
		logDebug("Starting method ValueLinkGiftCardValidation.siteSpecificValidation");
		
		
		final String mid = pSiteSpecific.get(BBBCheckoutConstants.MID);
		final String localCurrencyVAL = pSiteSpecific
				.get(BBBCheckoutConstants.LOCAL_CURR_VAL);

		if (mid == null || mid.equals(pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,"MID value is blank(" + mid
					+ ") among " + pVLSiteKeys
					+ " of configurKeys repository");
		}
		if (localCurrencyVAL == null
				|| localCurrencyVAL.equals(pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,"localCurrencyVAL value is blank("
					+ localCurrencyVAL + ") among " + pVLSiteKeys
					+ " of configurKeys repository");
		}
		
		
		logDebug("Exiting method ValueLinkGiftCardValidation.siteSpecificValidation");
		
	}

	/**
	 * @param pRequestBody
	 * @param pVLBlankValue
	 * @throws BBBBusinessException
	 */
	private void reqBodyValidation(final Map<String, String> pRequestBody,
			final String pVLBlankValue) throws BBBBusinessException {
		
		
		logDebug("Starting method ValueLinkGiftCardValidation.reqBodyValidation");
		
		
		final String localTransDateIDN = pRequestBody
				.get(BBBCheckoutConstants.LOC_TRANS_DT_IDN);
		final String midTidIDN = pRequestBody.get(BBBCheckoutConstants.MID_TID_IDN);
		final String sourceCodeIDN = pRequestBody
				.get(BBBCheckoutConstants.SOURCE_CODE_IDN);
		final String sourceCodeVAL = pRequestBody
				.get(BBBCheckoutConstants.SOURCE_CODE_VAL);
		final String giftCardNumberIDN = pRequestBody
				.get(BBBCheckoutConstants.GIFT_CARD_IDN);
		final String embCardNumberIDN = pRequestBody
				.get(BBBCheckoutConstants.EMB_CARD_NUM_IDN);
		final String transAmtIDN = pRequestBody
				.get(BBBCheckoutConstants.TRAN_AMOUNT_IDN);

		

		if (getValueLinkGiftCardUtil().isEmpty(localTransDateIDN, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,"localTransDateIDN value is blank("
					+ localTransDateIDN
					+ LASTREQ);
		}
		if (getValueLinkGiftCardUtil().isEmpty(midTidIDN, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,"midTidIDN value is blank("
					+ midTidIDN
					+ LASTREQ);
		}
		if (getValueLinkGiftCardUtil().isEmpty(sourceCodeIDN, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,"sourceCodeIDN value is blank("
					+ sourceCodeIDN
					+ LASTREQ);
		}
		if (getValueLinkGiftCardUtil().isEmpty(sourceCodeVAL, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,"sourceCodeVAL value is blank("
					+ sourceCodeVAL
					+ LASTREQ);
		}
		if (getValueLinkGiftCardUtil().isEmpty(giftCardNumberIDN, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,"giftCardNumberIDN value is blank("
					+ giftCardNumberIDN
					+ LASTREQ);
		}
		if (getValueLinkGiftCardUtil().isEmpty(embCardNumberIDN, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,
					"embossedCardNumberIDN value is blank("
							+ embCardNumberIDN
							+ LASTREQ);
		}
		if (getValueLinkGiftCardUtil().isEmpty(transAmtIDN, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,
					"transactionAmountIDN value is blank("
							+ transAmtIDN
							+ LASTREQ);
		}

		reqBodyValidationPart(pRequestBody, pVLBlankValue);
		
		
		logDebug("Exiting method ValueLinkGiftCardValidation.reqBodyValidation");
		
	}

	/**
	 * @param pRequestBody
	 * @param pVLBlankValue
	 * @throws BBBBusinessException
	 */
	private void reqBodyValidationPart(final Map<String, String> pRequestBody,
			final String pVLBlankValue) throws BBBBusinessException {
		
		
		logDebug("Starting method ValueLinkGiftCardValidation.reqBodyValidationPart");
		
		
		final String extAccNumberIDN = pRequestBody
				.get(BBBCheckoutConstants.EXT_ACC_IDN);
		final String altMerNoIDN = pRequestBody
				.get(BBBCheckoutConstants.ALT_MERCHANT_NUMBER_IDN);
		final String merchantKeyIdIDN = pRequestBody
				.get(BBBCheckoutConstants.MER_KEY_IDN);
		final String merchantKeyIdVal = pRequestBody
				.get(BBBCheckoutConstants.MER_KEY_VAL);
		final String localCurrencyIDN = pRequestBody
				.get(BBBCheckoutConstants.LOCAL_CURR_IDN);
		final String termTransNoIDN = pRequestBody
				.get(BBBCheckoutConstants.TERM_TRANS_IDN);
		
		if (getValueLinkGiftCardUtil().isEmpty(extAccNumberIDN, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,
					"extAccountNumberIDN value is blank("
							+ extAccNumberIDN
							+ LASTREQ);
		}
		if (getValueLinkGiftCardUtil().isEmpty(altMerNoIDN, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,
					"altMerchantNumberIDN value is blank("
							+ altMerNoIDN
							+ LASTREQ);
		}
		if (getValueLinkGiftCardUtil().isEmpty(merchantKeyIdIDN, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,"merchantKeyIdIDN value is blank("
					+ merchantKeyIdIDN
					+ LASTREQ);
		}
		if (getValueLinkGiftCardUtil().isEmpty(localCurrencyIDN, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,"localCurrencyIDN value is blank("
					+ localCurrencyIDN
					+ LASTREQ);
		}
		if (getValueLinkGiftCardUtil().isEmpty(termTransNoIDN, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,
					"terminalTransNumberIDN value is blank("
							+ termTransNoIDN
							+ LASTREQ);
		}
		if (getValueLinkGiftCardUtil().isEmpty(merchantKeyIdVal, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,"merchantKeyIdVal value is blank("
					+ merchantKeyIdVal
					+ LASTREQ);
		}
		
		
		logDebug("Exiting method ValueLinkGiftCardValidation.reqBodyValidationPart");
		
	}

	/**
	 * @param pRequestHeader
	 * @param pVLBlankValue
	 * @throws BBBBusinessException
	 */
	private void reqHeaderValidation(final Map<String, String> pRequestHeader,
			final String pVLBlankValue) throws BBBBusinessException {
		
		
		logDebug("Starting method ValueLinkGiftCardValidation.reqHeaderValidation");
		
		
		final String msgIDN = pRequestHeader.get(BBBCheckoutConstants.MESSAGE_IDN);

		final String msgVAL = pRequestHeader.get(BBBCheckoutConstants.MESSAGE_VAL);

		final String versionNumberVal = pRequestHeader
				.get(BBBCheckoutConstants.VER_NUMBER_VAL);

		final String formatNumberVal = pRequestHeader
				.get(BBBCheckoutConstants.FORMAT_NUMBER_VAL);

		if (getValueLinkGiftCardUtil().isEmpty(msgIDN, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,
					"msgIDN value is blank("
							+ msgIDN
							+ LAST);
		}
		if (getValueLinkGiftCardUtil().isEmpty(msgVAL, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,
					"msgVAL value is blank("
							+ msgVAL
							+ LAST);
		}
		if (getValueLinkGiftCardUtil().isEmpty(versionNumberVal, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,
					"versionNumberVal value is blank("
							+ versionNumberVal
							+ LAST);
		}
		if (getValueLinkGiftCardUtil().isEmpty(formatNumberVal, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,
					"formatNumberVal value is blank("
							+ formatNumberVal
							+ LAST);
		}
		
		
		logDebug("Exiting method ValueLinkGiftCardValidation.reqHeaderValidation");
		
		
	}

	/**
	 * @param pCommonVLKeys
	 * @param pVLBlankValue
	 * @throws BBBBusinessException
	 */
	private void commonKeyValidation(final Map<String, String> pCommonVLKeys,
			final String pVLBlankValue) throws BBBBusinessException {
		
		
		logDebug("Starting method ValueLinkGiftCardValidation.commonKeyValidation");
		
		
		final String tranReqValBal = pCommonVLKeys
				.get(BBBCheckoutConstants.TRANS_REQ_CD_BAL);
		final String transCoValRedem = pCommonVLKeys
				.get(BBBCheckoutConstants.TRANS_REQ_CD_RED);
		final String transRCValUnred = pCommonVLKeys
				.get(BBBCheckoutConstants.TRANS_REQ_CD_UNRED);
		final String transReqCdValErr = pCommonVLKeys
				.get(BBBCheckoutConstants.TRANS_REQ_CD_VAL_ER);
		final String transReqCdValRWK = pCommonVLKeys
				.get(BBBCheckoutConstants.TRANS_REQ_CD_WORK_KEY);
		

		if (getValueLinkGiftCardUtil().isEmpty(tranReqValBal, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,
					"transReqCodeVal_balance value is blank("
							+ tranReqValBal
							+ LASTCOM);
		}
		if (getValueLinkGiftCardUtil().isEmpty(transCoValRedem, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,
					"transCoValRedem value is blank("
							+ transCoValRedem
							+ LASTCOM);
		}
		if (getValueLinkGiftCardUtil().isEmpty(transRCValUnred, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,
					"transRCValUnred value is blank("
							+ transRCValUnred
							+ LASTCOM);
		}
		if (getValueLinkGiftCardUtil().isEmpty(transReqCdValErr, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,
					"transReqCdValErr value is blank("
							+ transReqCdValErr
							+ LASTCOM);
		}
		if (getValueLinkGiftCardUtil().isEmpty(transReqCdValRWK, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,
					"transReqCodeVal_reversal value is blank("
							+ transReqCdValRWK
							+ LASTCOM);
		}
		
		commonKeyValidationPart(pCommonVLKeys, pVLBlankValue);
		
		
		logDebug("Exiting method ValueLinkGiftCardValidation.commonKeyValidation");
		
	}

	/**
	 * @param pCommonVLKeys
	 * @param pVLBlankValue
	 * @throws BBBBusinessException
	 */
	private void commonKeyValidationPart(
			final Map<String, String> pCommonVLKeys,
			final String pVLBlankValue) throws BBBBusinessException {
		
	
		logDebug("Starting method ValueLinkGiftCardValidation.commonKeyValidationPart");

		
		final String appID = pCommonVLKeys.get(BBBCheckoutConstants.APPID);
		final String svcID = pCommonVLKeys.get(BBBCheckoutConstants.SVCID);
		final String primaryURL = pCommonVLKeys
				.get(BBBCheckoutConstants.PRIMARY_URL);
		final String secondaryURL = pCommonVLKeys
				.get(BBBCheckoutConstants.SECONDARY_URL);
		final String vxnTid = pCommonVLKeys.get(BBBCheckoutConstants.VXN_TID);
		final String vxnDid = pCommonVLKeys.get(BBBCheckoutConstants.VXN_DID);
		final String terminalID = pCommonVLKeys
				.get(BBBCheckoutConstants.TERMINAL_ID);
		
		if (getValueLinkGiftCardUtil().isEmpty(appID, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,"appID value is blank(" + appID
					+ LASTCOM);
		}
		if (getValueLinkGiftCardUtil().isEmpty(svcID, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,"svcID value is blank(" + svcID
					+ LASTCOM);
		}
		if (getValueLinkGiftCardUtil().isEmpty(primaryURL, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,"primaryURL value is blank("
					+ primaryURL
					+ LASTCOM);
		}
		if (getValueLinkGiftCardUtil().isEmpty(secondaryURL, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,"secondaryURL value is blank("
					+ secondaryURL
					+ LASTCOM);
		}
		if (getValueLinkGiftCardUtil().isEmpty(vxnTid, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,"vxnTID value is blank(" + vxnTid
					+ LASTCOM);
		}
		if (getValueLinkGiftCardUtil().isEmpty(vxnDid, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,"vxnDID value is blank(" + vxnDid
					+ LASTCOM);
		}
		if (getValueLinkGiftCardUtil().isEmpty(terminalID, pVLBlankValue)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1004,"terminalID value is blank("
					+ terminalID
					+ LASTCOM);
		}
		
		
		logDebug("Exiting method ValueLinkGiftCardValidation.commonKeyValidationPart");
		
	}

	private static final String LASTCOM = ") among valueLinkKeys of configurKeys repository";
	private static final String LASTREQ = ") among valueLinkRequestBody of configurKeys repository";
	private static final String LAST = ") among valueLinkRequestHeader of configurKeys repository";


	private ValueLinkGiftCardUtil mValueLinkGiftCardUtil;

	/**
	 * @return the mValueLinkGiftCardUtil
	 */
	public ValueLinkGiftCardUtil getValueLinkGiftCardUtil() {
		return mValueLinkGiftCardUtil;
	}

	/**
	 * @param pValueLinkGiftCardUtil the mValueLinkGiftCardUtil to set
	 */
	public void setValueLinkGiftCardUtil(
			ValueLinkGiftCardUtil pValueLinkGiftCardUtil) {
		mValueLinkGiftCardUtil = pValueLinkGiftCardUtil;
	}
	
}
