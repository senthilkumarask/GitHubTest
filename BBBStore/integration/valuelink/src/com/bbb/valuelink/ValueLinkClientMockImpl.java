package com.bbb.valuelink;

import java.util.Map;

import net.datawire.vxn3.VXNException;
import atg.core.util.StringUtils;

import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.valuelink.constants.BBBValueLinkConstants;

/**
 * This class make call to ValueLink and return response pay load.
 */
public class ValueLinkClientMockImpl extends BBBGenericService implements
		ValueLinkClient {

	private ValueLinkGiftCardUtil mValueLinkGiftCardUtil;

	private String mMockAmount;

	/**
	 * This method calls ValuLink API for Gift card.
	 *
	 * @param mVLInputs
	 * @return
	 * @throws BBBSystemException
	 */
	@Override
	public String invoke(final Map<String, String> mVLInputs)
			throws BBBSystemException, BBBBusinessException {

		
		this.logDebug("starting method ValueLinkClient.invoke, passed params::: "
					+ mVLInputs);
		
		Boolean errorFlag = Boolean.FALSE;
		this.preInvoke(mVLInputs);

		/*String vxnDID = null;
		String vxnMID = null;
		String vxnTID = null;
		String svcID = null;
		String appID = null;
		String primaryURL = null;
		String secondryURL = null;*/
		String requestPayload = null;
		//String uniqueTransID = null;
		String callType = null;

		if ((mVLInputs != null) && (mVLInputs.size() > 0)) {
			/*vxnDID = mVLInputs.get(BBBValueLinkConstants.VXN_DID);
			vxnMID = mVLInputs.get(BBBValueLinkConstants.VXN_MID);
			vxnTID = mVLInputs.get(BBBValueLinkConstants.VXN_TID);
			svcID = mVLInputs.get(BBBValueLinkConstants.SVCID);
			appID = mVLInputs.get(BBBValueLinkConstants.APPID);
			primaryURL = mVLInputs
					.get(BBBValueLinkConstants.PRIMARY_URL);
			secondryURL = mVLInputs
					.get(BBBValueLinkConstants.SECONDARY_URL);
					*/
			requestPayload = mVLInputs
					.get(BBBValueLinkConstants.REQUEST_PAYLOAD);
			/*uniqueTransID = mVLInputs
					.get(BBBValueLinkConstants.UNIQUE_TRANS_ID);*/
			callType = mVLInputs.get(BBBValueLinkConstants.CALL_TYPE);

		}
		
		this.logDebug("mVLInputs : " + mVLInputs);
		
		// This array is used to store response payload
		String respPayload = null;

		/*List<String> sdList = new ArrayList<String>(2);
		sdList.add(primaryURL);
		sdList.add(secondryURL);*/

		/*char payload[] = null;
		if (requestPayload != null) {
			payload = requestPayload.toCharArray();
		}*/

		try {

			// Get the instance of the VXN object. This will do a service
			// discovery.
			/*VXN vxn = VXN.getInstance(sdList, vxnDID, vxnMID, vxnTID, svcID,
					appID);*/

			final char FS = (char)0x1C;
			final String array[] = requestPayload.split(FS+"");
			String giftCardNo = null;
			for (final String string : array) {
				if(string.startsWith("70")){
					giftCardNo = string.substring(2, string.length());
				}

			}
			final String success_status = "00";
			final String error_status = "12";
			String status = null;

			final String mid="99032159997";
			final String tid="0000";

			if ("getBalance".equals(callType)) {

				if((giftCardNo != null) && giftCardNo.startsWith("99")){
					status = success_status;
				}else{
					status = error_status;
					throw new VXNException("The gift card or Pin you entered is not valid, please check and try again");
				}

				final String mockGetBalanceResponse = "40"+FS+"900"+FS+"11123456"+FS+"38123456"+FS+"39"+status+FS+"42"+mid+tid+FS+"4412345678901"+FS+"70"+giftCardNo+FS+"75123456"+FS+"76"+this.getMockAmount()+FS
						+"78123456789012"+FS+"8012345"+FS+"81123456"+FS+"82123"+FS+"A012345678"+FS+"B012"+FS+"BC840"+FS+"C0123"+FS+"E9123456"+FS+"F61234";

				respPayload = mockGetBalanceResponse;

			}


			if ((callType.equals(BBBValueLinkConstants.REDEEM) || callType
					.equals(BBBValueLinkConstants.REDEEM_ROLLBACK))){

				//actual Redeem Response Payload:40 0900 11142396 38658313 3900 75250 760   707777060829214424 A001013500 42990321599970000 4400000000000 F62202 B00 780 C0840
											   //40 0900 11141946 38834966 3900 75462 76462 707777060829226907 A001013500 42990321599970000 4400000000000 F62400 B00 780 C0840

				final String mockRedeemResponse = "40"+FS+"0900"+FS+"11142396"+FS+"38658313"+FS+"3900"+FS+"75"+this.getMockAmount()+FS+"760"+FS+"707777060829214424"+FS+"A001013500"+FS+"42990321599970000"+
											FS+"4400000000000"+FS+"F62202"+FS+"B00"+FS+"780"+FS+"C0840";

				respPayload = mockRedeemResponse;

			}


		} catch (final VXNException vxnException) {
			errorFlag = Boolean.TRUE;
		
			this.logError(vxnException.getMessage());
			
			throw new BBBSystemException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1000,vxnException.getMessage(),
					vxnException.getCause());
		} catch (final RuntimeException runTimeExc) {
			errorFlag = Boolean.TRUE;
			this.logError(runTimeExc.getMessage());
			
			throw new BBBSystemException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1001,runTimeExc.getMessage(),
					runTimeExc.getCause());
		} finally {
			if (errorFlag) {
				this.doTimeReversalCall(mVLInputs, callType);
			}
		}

	
		this.logDebug("Exiting method ValueLinkClient.invoke");
		

		return respPayload;
	}

	/**
	 * Time reversal call is to be done if any error occurs during redeem or
	 * redeem roll back calls.
	 *
	 * @param mVLInputs
	 * @param pCallType
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public void doTimeReversalCall(final Map<String, String> mVLInputs,
			final String pCallType) throws BBBSystemException, BBBBusinessException {

		
		this.logDebug("Starting method ValueLinkClient.doTimeReversalCall");
		

		final String transCode = mVLInputs.get(BBBValueLinkConstants.REQUEST_PAYLOAD)
				.substring(17, 21);

		final String timeReversalCallCode = mVLInputs
				.get(BBBValueLinkConstants.TRANS_REQ_CODE_VAL_ERROR);

		final String originalTransCodeSeparator = mVLInputs
				.get(BBBValueLinkConstants.ORIGINAL_TRANS_CODE_SEPARATOR);

		if ((pCallType.equals(BBBValueLinkConstants.REDEEM) || pCallType
				.equals(BBBValueLinkConstants.REDEEM_ROLLBACK))
				&& !transCode.equals(timeReversalCallCode)) {

			
			this.logDebug("!!!!!!!!       doing TimeReversalCall...........");
			

			final String timeReversalPayload = this.getValueLinkGiftCardUtil()
					.getTimeReversalPayload(
							mVLInputs
									.get(BBBValueLinkConstants.REQUEST_PAYLOAD),
							originalTransCodeSeparator, timeReversalCallCode);

			mVLInputs.put(BBBValueLinkConstants.REQUEST_PAYLOAD,
					timeReversalPayload);

			this.invoke(mVLInputs);
		} else {
			
			this.logDebug("Skipping TimeReversalCall because calltype is not Redeem/RedeemRollback.");
			
		}

		
		this.logDebug("Exiting method ValueLinkClient.doTimeReversalCall");
		

	}

	/**
	 * This method validates input parameters.
	 *
	 * @param mVLInputs
	 * @throws BBBBusinessException
	 */
	private void preInvoke(final Map<String, String> mVLInputs)
			throws BBBBusinessException {

		String vxndid = null;
		String vxnmid = null;
		String vxntid = null;
		String svcID = null;
		String appID = null;
		String primaryURL = null;
		String secondryURL = null;
		String balanceAPIPayload = null;
		String uniqueTransID = null;
		String requestPayload = null;
		String callType = null;

		if ((mVLInputs != null) && (mVLInputs.size() > 0)) {
			vxndid = mVLInputs.get(BBBValueLinkConstants.VXN_DID);
			vxnmid = mVLInputs.get(BBBValueLinkConstants.VXN_MID);
			vxntid = mVLInputs.get(BBBValueLinkConstants.VXN_TID);
			svcID = mVLInputs.get(BBBValueLinkConstants.SVCID);
			appID = mVLInputs.get(BBBValueLinkConstants.APPID);
			primaryURL = mVLInputs
					.get(BBBValueLinkConstants.PRIMARY_URL);
			secondryURL = mVLInputs
					.get(BBBValueLinkConstants.SECONDARY_URL);
			balanceAPIPayload = mVLInputs
					.get(BBBValueLinkConstants.REQUEST_PAYLOAD);
			uniqueTransID = mVLInputs
					.get(BBBValueLinkConstants.UNIQUE_TRANS_ID);
			requestPayload = mVLInputs
					.get(BBBValueLinkConstants.REQUEST_PAYLOAD);
			callType = mVLInputs.get(BBBValueLinkConstants.CALL_TYPE);

		}

		if (StringUtils.isEmpty(vxndid)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1003,
					"vxnDID is null in input parameter to  ValueLinkClientImpl.invoke method");
		}
		if (StringUtils.isEmpty(vxnmid)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1003,
					"vxnMID is null in input parameter to  ValueLinkClientImpl.invoke method");
		}
		if (StringUtils.isEmpty(vxntid)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1003,
					"vxnTID is null in input parameter to  ValueLinkClientImpl.invoke method");
		}
		if (StringUtils.isEmpty(svcID)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1003,
					"svcID is null in input parameter to  ValueLinkClientImpl.invoke method");
		}
		if (StringUtils.isEmpty(appID)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1003,
					"appID is null in input parameter to  ValueLinkClientImpl.invoke method");
		}
		if (StringUtils.isEmpty(primaryURL)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1003,
					"primaryURL is null in input parameter to  ValueLinkClientImpl.invoke method");
		}
		if (StringUtils.isEmpty(secondryURL)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1003,
					"secondryURL is null in input parameter to  ValueLinkClientImpl.invoke method");
		}
		if (StringUtils.isEmpty(balanceAPIPayload)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1003,
					"balanceAPIPayload is null in input parameter to  ValueLinkClientImpl.invoke method");
		}
		if (StringUtils.isEmpty(uniqueTransID)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1003,
					"uniqueTransID is null in input parameter to  ValueLinkClientImpl.invoke method");
		}
		if (StringUtils.isEmpty(requestPayload)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1003,
					"requestPayload is null in input parameter to  ValueLinkClientImpl.invoke method");
		}
		if (StringUtils.isEmpty(callType)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1003,
					"callType is null in input parameter to  ValueLinkClientImpl.invoke method");
		}
	}

	/**
	 * @return the mValueLinkGiftCardUtil
	 */
	public ValueLinkGiftCardUtil getValueLinkGiftCardUtil() {
		return this.mValueLinkGiftCardUtil;
	}

	/**
	 * @param pValueLinkGiftCardUtil
	 *            the mValueLinkGiftCardUtil to set
	 */
	public void setValueLinkGiftCardUtil(
			final ValueLinkGiftCardUtil pValueLinkGiftCardUtil) {
		this.mValueLinkGiftCardUtil = pValueLinkGiftCardUtil;
	}

	/**
	 * @return the mMockAmount
	 */
	public String getMockAmount() {
		return this.mMockAmount;
	}

	/**
	 * @param pMockAmount the mMockAmount to set
	 */
	public void setMockAmount(final String pMockAmount) {
		this.mMockAmount = pMockAmount;
	}
}