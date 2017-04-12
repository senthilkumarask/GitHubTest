package com.bbb.valuelink;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.datawire.vxn3.SimpleTransaction;
import net.datawire.vxn3.VXN;
import net.datawire.vxn3.VXNException;
import atg.core.util.StringUtils;

import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.payment.giftcard.GiftCardBeanInfo;
import com.bbb.valuelink.constants.BBBValueLinkConstants;

/**
 * This class make call to ValueLink and return response pay load.
 */
public class ValueLinkClientImpl extends BBBGenericService implements
		ValueLinkClient {

	private static final String INVOKE = "invoke";
	private static final String VALUELINKCLIENTIMPL = "ValueLinkClientImpl";
	private static final String DOTIMEREVERSALCALL = "doTimeReversalCall";
	private VXN vxn = null;

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
		

		String uniqueTransID = null;
		final String methodName = INVOKE;
		String response = null;
		Boolean errorFlag = Boolean.FALSE;
		try {
			this.preInvoke(mVLInputs);

			String vxnDID = null;
			String vxnMID = null;
			String vxnTID = null;
			String svcID = null;
			String appID = null;
			String primaryURL = null;
			String secondryURL = null;
			String requestPayload = null;
			String callType = null;

			if ((mVLInputs != null) && (mVLInputs.size() > 0)) {
				vxnDID = mVLInputs.get(BBBValueLinkConstants.VXN_DID);
				vxnMID = mVLInputs.get(BBBValueLinkConstants.VXN_MID);
				vxnTID = mVLInputs.get(BBBValueLinkConstants.VXN_TID);
				svcID = mVLInputs.get(BBBValueLinkConstants.SVCID);
				appID = mVLInputs.get(BBBValueLinkConstants.APPID);
				primaryURL = mVLInputs
						.get(BBBValueLinkConstants.PRIMARY_URL);
				secondryURL = mVLInputs
						.get(BBBValueLinkConstants.SECONDARY_URL);
				requestPayload = mVLInputs
						.get(BBBValueLinkConstants.REQUEST_PAYLOAD);
				uniqueTransID = mVLInputs
						.get(BBBValueLinkConstants.UNIQUE_TRANS_ID);
				callType = mVLInputs
						.get(BBBValueLinkConstants.CALL_TYPE);

			}
			BBBPerformanceMonitor.start(VALUELINKCLIENTIMPL, methodName + "-" + uniqueTransID);
			// This array is used to store response payload
			char respPayload[] = null;

			final List<String> sdList = new ArrayList<String>(2);
			sdList.add(primaryURL); // Configured value, provided by BBB
			sdList.add(secondryURL);

			char payload[] = null;
			if (requestPayload != null) {
				payload = requestPayload.toCharArray();
			}

			try {

				// Get the instance of the VXN object. This will do a service
				// discovery.
				if (this.vxn == null) {
					this.vxn = VXN.getInstance(sdList, vxnDID, vxnMID, vxnTID,
							svcID, appID);
				}

				/**
				 * Create SimpleTransaction object. We will use OrderId or
				 * PaymentId as clientRef It should be unique
				 * [Timestamp+OrderId+PaymentGroupId] Client Ref(uniqueTransID):
				 * Client Ref should be 14 digits set up as follows
				 * "tttttttVnnnnnn": "ttttttt" = 7 digit transaction ID that is
				 * unique to each transaction. Pad with trailing zeros or
				 * truncate as required. "V" = the letter "V" for Version. The
				 * version should be updated as the version of the application
				 * changes. "nnnnnn" = the version number, 6 digits long, no
				 * periods or spaces, pad with trailing zeros or truncate as
				 * required to meet 6
				 **/
				final SimpleTransaction tr = this.vxn.newSimpleTransaction(uniqueTransID);

				// Set payload for the transaction object.
				tr.setPayload(payload);

				
				this.logDebug("Request Payload:" + new String(payload));
				
				// Execute the transaction object.
				tr.executeXmlRequest();
				
				this.logDebug("Transaction Response Status Code:"
							+ tr.getResponseStatusCode());
				

				// check the status and if it is "OK" then get the payload
				if ((null != tr.getResponseStatusCode())
						&& BBBValueLinkConstants.OK.equals(tr
								.getResponseStatusCode())) {
					// Get response payload.
					respPayload = tr.getPayload();
					// Expected response Payload for balance API
					// "SV DOT format";
					
					this.logDebug("Response Payload Reference:" + Arrays.toString(respPayload));
					
					/*
					 * For testing purpose
					 * if(!callType.equals(BBBCheckoutConstants
					 * .TIMEOUT_REVERSAL)) throw new RuntimeException();
					 */
				}

			} catch (final VXNException vxnException) {
				errorFlag = Boolean.TRUE;
				BBBPerformanceMonitor.cancel(VALUELINKCLIENTIMPL, methodName + "-" + uniqueTransID);
				throw new BBBSystemException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1000,"vxn Exception", vxnException);
			} catch (final RuntimeException runTimeExc) {
				errorFlag = Boolean.TRUE;
				BBBPerformanceMonitor.cancel(VALUELINKCLIENTIMPL, methodName + "-" + uniqueTransID);
				throw new BBBSystemException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1001,"runTime Exception", runTimeExc);
			} finally {
				if (errorFlag) {
					response = this.doTimeReversalCall(mVLInputs, callType);
				}
			}

			
			this.logDebug("Exiting method ValueLinkClient.invoke");
			
			if (response == null) {
				response = new String(respPayload);
			}

		} finally {
			if (!errorFlag) {
				BBBPerformanceMonitor.end(VALUELINKCLIENTIMPL, methodName + "-" + uniqueTransID);
			}

		}
		return response;
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
	private String doTimeReversalCall(final Map<String, String> mVLInputs,
			final String pCallType) throws BBBBusinessException {

		
		this.logDebug("Starting method ValueLinkClient.doTimeReversalCall");
		

		final String methodName = DOTIMEREVERSALCALL;
		String responsePayLoad = null;
		boolean isCanceled = false;

		try {
			BBBPerformanceMonitor.start(VALUELINKCLIENTIMPL, methodName);
			this.preInvoke(mVLInputs);

			if (!(pCallType.equals(BBBValueLinkConstants.REDEEM) || pCallType
					.equals(BBBValueLinkConstants.REDEEM_ROLLBACK))) {
				
				this.logDebug("Not a Redeem or Redeem Rollback call so not doing TimeReversal. Existing from method ValueLinkClient.doTimeReversalCall");
				
				return null;
			}

			final String timeReversalCallCode = mVLInputs
					.get(BBBValueLinkConstants.TRANS_REQ_CODE_VAL_ERROR);

			final String originalTransCodeSeparator = mVLInputs
					.get(BBBValueLinkConstants.ORIGINAL_TRANS_CODE_SEPARATOR);

			final String timeReversalPayload = this.getValueLinkGiftCardUtil()
					.getTimeReversalPayload(
							mVLInputs
									.get(BBBValueLinkConstants.REQUEST_PAYLOAD),
							originalTransCodeSeparator, timeReversalCallCode);

			mVLInputs.put(BBBCheckoutConstants.CALL_TYPE,
					BBBCheckoutConstants.TIMEOUT_REVERSAL);
			mVLInputs.put(BBBValueLinkConstants.REQUEST_PAYLOAD,
					timeReversalPayload);
			
			this.logDebug("!!!!!!!!       doing TimeReversalCall...........");
			
			boolean retry = false;
			int trCount = 1;
			do {
				boolean trSuccess = true;
				try {
					
					this.logDebug("...........Call Number: " + trCount);
					
					responsePayLoad = this.invoke(mVLInputs);
					if ((responsePayLoad == null)
							|| !this.validateTimeReversalResponse(responsePayLoad)) {
						trSuccess = false;
					}
				} catch (final BBBSystemException bse) {
					trSuccess = false;
				}
				if (!trSuccess
						&& (trCount <= Integer.valueOf(this.getTimeReversalRetry()))) {
					trCount++;
					retry = true;
				}
				if ((trCount > Integer.valueOf(this.getTimeReversalRetry()))
						|| trSuccess) {
					retry = false;
				}
				if (retry) {
					this.logDebug("!!!!!!!!       doing TimeReversalCall...........");
				}
			} while (retry);

			if (trCount >= Integer.valueOf(this.getTimeReversalRetry())) {
				if (this.isLoggingWarning()) {
					this.logWarning("Time Reversal Call is not successfull after max retry.");
				}
			}

			if (responsePayLoad != null) {
				responsePayLoad = responsePayLoad
						+ BBBCheckoutConstants.TIMEOUT_REVERSAL;
			}
		} catch (final BBBBusinessException bbe) {
			isCanceled = true;
			BBBPerformanceMonitor.cancel(VALUELINKCLIENTIMPL, methodName);
			throw bbe;
		} finally {
			if (!isCanceled) {
				BBBPerformanceMonitor.end(VALUELINKCLIENTIMPL, methodName);
			}

		}
		
		this.logDebug("Exiting method ValueLinkClient.doTimeReversalCall");
		

		return responsePayLoad;
	}

	/**
	 * This method validates time reversal call response, This method checks if
	 * response status code is either 00, 34 or 33 then success else failure.
	 *
	 * @param responsePayLoad
	 * @return
	 */
	private boolean validateTimeReversalResponse(final String responsePayLoad) {
		
		this.logDebug("Starting method ValueLinkClient.validTimeReversalResponse");
		

		boolean valid = false;

		try {
			if (this.getTimeRevSuccessCodes() == null) {
				throw new BBBSystemException(BBBCoreErrorConstants.INTEGARTION_VALUELINK_ERROR_1002,
						"Success codes for Time reversal call has not been assigned in ValueLinkClient.properties");
			}
			final GiftCardBeanInfo respBean = this.responseProcessor
					.processResponse(responsePayLoad);
			if (respBean != null) {
				final String respCode = respBean.getAuthRespCode();
				if ((respCode != null)
						&& (this.getTimeRevSuccessCodes().contains(respCode))) {
					valid = true;
				}
			}
		} catch (final BBBSystemException e) {
			this.logError("Error in parsing TimeReversal response", e);
		} catch (final BBBBusinessException e) {
			this.logError("Error in parsing TimeReversal response", e);
		}

		
		this.logDebug("Exiting method ValueLinkClient.validTimeReversalResponse");
		
		/*
		  TODO:For testing purpose, Will be removed after FirstData certification testing
		  valid = false;
		 */
		return valid;
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

	public ValueLinkGiftCardUtil getValueLinkGiftCardUtil() {
		return this.mValueLinkGiftCardUtil;
	}
	public void setValueLinkGiftCardUtil(
			final ValueLinkGiftCardUtil pValueLinkGiftCardUtil) {
		this.mValueLinkGiftCardUtil = pValueLinkGiftCardUtil;
	}
	public void setResponseProcessor(final ValueLinkGiftCardProcessor responseProcessor) {
		this.responseProcessor = responseProcessor;
	}
	public ValueLinkGiftCardProcessor getResponseProcessor() {
		return this.responseProcessor;
	}
	public List<String> getTimeRevSuccessCodes() {
		return this.mTimeRevSuccessCodes;
	}
	public void setTimeRevSuccessCodes(final List<String> mTimeRevSuccessCodes) {
		this.mTimeRevSuccessCodes = mTimeRevSuccessCodes;
	}
	public String getTimeReversalRetry() {
		return this.mTimeReversalRetry;
	}
	public void setTimeReversalRetry(final String pTimeReversalRetry) {
		this.mTimeReversalRetry = pTimeReversalRetry;
	}


	List<String> mTimeRevSuccessCodes;
	private ValueLinkGiftCardProcessor responseProcessor;
	private String  mTimeReversalRetry;
	private ValueLinkGiftCardUtil mValueLinkGiftCardUtil;



}