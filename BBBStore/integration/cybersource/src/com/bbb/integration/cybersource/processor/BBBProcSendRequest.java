package com.bbb.integration.cybersource.processor;

import java.math.BigInteger;

import atg.core.util.StringUtils;
import atg.integrations.cybersourcesoap.CyberSourcePrintUtils;
import atg.integrations.cybersourcesoap.CyberSourceUtils;
import atg.integrations.cybersourcesoap.MessageConstant;
import atg.integrations.cybersourcesoap.cc.CreditCardProcParams;
import atg.integrations.cybersourcesoap.cc.processor.ProcSendRequest;
import atg.service.pipeline.PipelineResult;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.common.BBBVBVSessionBean;
//import com.bbb.commerce.common.BBBDOSessionBean;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBTagConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.integration.cybersource.creditcard.CyberSourceVO;
import com.bbb.utils.BBBUtility;
import com.cybersource.stub.CCAuthReply;
import com.cybersource.stub.ReplyMessage;
import com.cybersource.stub.RequestMessage;
import com.cybersource.stub.UCAF;

/**
 * 
 * Processor sends request to CyberSource and sets reply to params
 * 
 */
public class BBBProcSendRequest extends ProcSendRequest {
	private static final String CLASS_NAME = BBBProcSendRequest.class.getName();
	private static final String CYBERSOURCE_AVS_FAIL = "cybersourceAVSFail";
	private static final String CYBERSOURCE_INVALID_CVV = "cybersourceInvalidCVV";
	private static final String CART_AND_CHECKOUT_KEYS = "CartAndCheckoutKeys";
	private static final String BBBVBVSessionBean_COMPONENT_PATH = "/com/bbb/commerce/common/BBBVBVSessionBean";
	//private static final String SessionBean_COMPONENT_PATH = "/com/bbb/commerce/common/BBBDOSessionBean";

	private BBBCatalogTools mCatalogTools = null;
	private boolean mTestEnvironment = false;

	/**
	 * Sends request to CyberSource and sets reply to params
	 */
	@Override
	protected int runCreditCardProcess(CreditCardProcParams pParams,
			PipelineResult pResult) throws Exception {
		final String methodName = CLASS_NAME + ".runCreditCardProcess()";
		int result = SUCCESS;
		boolean systemError = false;
		String siteID = null;
		String eci = null;
		//seeing if an order is a dummy order.
		boolean isDummyOrder = false;
		isDummyOrder = ((BBBOrderImpl) pParams.getCreditCardInfo().getOrder()).isDummyOrder();
		//System.out.println("runCreditCardProcess::"+isDummyOrder);
		if (pParams.getCreditCardInfo() != null
				&& pParams.getCreditCardInfo().getOrder() != null) {
			siteID = pParams.getCreditCardInfo().getOrder().getSiteId();
			/* Check if cybersource is turned off */
			String cybersourceTagOn = getCatalogTools().getThirdPartyTagStatus(
					siteID, getCatalogTools(), BBBTagConstants.CYBERSOURCE_TAG);
			if (cybersourceTagOn != null
					&& cybersourceTagOn
							.equalsIgnoreCase(BBBCoreConstants.FALSE)) {
				/* Flag error which will simulate error scenario */
				if (isLoggingDebug()) {
					logDebug("Skipping Credit Card authorization as CyberSource Integration is turned off");
				}
				systemError = true;
			}else if(isDummyOrder){
				/* checking if it's a dummy order in order to skip cybersource auth*/
				if (isLoggingDebug()) {
					logDebug("Skipping Credit Card authorization as it's a dummy order");
				}
				systemError = true;
			}
		}

		if (!systemError) {
			RequestMessage request = pParams.getRequest();
			if (isTestEnvironment()) {
				if (isLoggingWarning()) {
					logWarning("The application is using CyberSource test interface; Defaulting the currency code to USD for test purpose");
				}
				request.getPurchaseTotals().setCurrency("USD");
			}
			BBBPerformanceMonitor.start(
					BBBPerformanceConstants.CYBERSOURCE_AUTH_CALL, methodName);
			try {
				long startTime = System.currentTimeMillis();
				CyberSourceVO cyberSourceVO = null;
				DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
				BBBVBVSessionBean vbvSessionBean = null;
				if(null != pRequest) {
				cyberSourceVO = (CyberSourceVO)pRequest.resolveName("/com/bbb/integration/cybersource/creditcard/CyberSourceVO");
				/*
				 * Start: 258-3 - Verified by Visa - Cybersource Integration
				 */
				vbvSessionBean = (BBBVBVSessionBean) pRequest.resolveName(BBBVBVSessionBean_COMPONENT_PATH);
				
				//For Verified by VISA and Master Card Secure code validation , we set 3 parameters CAVV,XID,ECI in requestMessage
				//Changes for BBBSL-5134 (To add Master card transactaion related ECI and cavv in UCAF object)
				if (pParams.getCreditCardInfo()
						.getCreditCardType() != null && BBBTagConstants.MASTER_CARD_TYPE
						.equalsIgnoreCase(pParams
								.getCreditCardInfo()
								.getCreditCardType()) && null==request.getUcaf()) {
					logDebug("UCAF is null and creating new UCAF object");
					UCAF ucaf = new UCAF();
					request.setUcaf(ucaf);
				}
				if(vbvSessionBean.getbBBVerifiedByVisaVO()!=null){
					if (BBBUtility.isNotEmpty(vbvSessionBean
								.getbBBVerifiedByVisaVO().getCavv())) {
							if (pParams.getCreditCardInfo()
									.getCreditCardType() != null && !BBBTagConstants.MASTER_CARD_TYPE
									.equalsIgnoreCase(pParams
											.getCreditCardInfo()
											.getCreditCardType())) {
								request.getCcAuthService().setCavv(vbvSessionBean.getbBBVerifiedByVisaVO().getCavv());
							}else{
								logDebug("Master card -- Setting UCAF AuthenticationData : " + vbvSessionBean.getbBBVerifiedByVisaVO().getCavv());
								request.getUcaf().setAuthenticationData(vbvSessionBean.getbBBVerifiedByVisaVO().getCavv());
							}
						}
					if(BBBUtility.isNotEmpty(vbvSessionBean.getbBBVerifiedByVisaVO().getXid()))
						request.getCcAuthService().setXid(vbvSessionBean.getbBBVerifiedByVisaVO().getXid());
					//commented this logic and moved to below section of code 
					//will have them null but VISA honor 3D secure validation
					/*	if (pParams.getCreditCardInfo().getCreditCardType() != null
								&& !BBBTagConstants.MASTER_CARD_TYPE
										.equalsIgnoreCase(pParams
												.getCreditCardInfo()
												.getCreditCardType())
								&& BBBUtility.isNotEmpty(vbvSessionBean
										.getbBBVerifiedByVisaVO()
										.getCommerceIndicator())) {
							request.getCcAuthService().setCommerceIndicator(vbvSessionBean.getbBBVerifiedByVisaVO().getCommerceIndicator());
							logDebug("Non Master card -- Setting commerce indicator with value : " + vbvSessionBean.getbBBVerifiedByVisaVO().getCommerceIndicator());
                		}*/
						
						
					//If 3 d secure authentication is false, then set ECI from response of lookup call else set from authenticated call.
					if(BBBUtility.isNotEmpty(vbvSessionBean.getbBBVerifiedByVisaVO().getAuthenticationEciFlag())){
						request.getCcAuthService().setEciRaw(vbvSessionBean.getbBBVerifiedByVisaVO().getAuthenticationEciFlag());
					}else{
						request.getCcAuthService().setEciRaw(vbvSessionBean.getbBBVerifiedByVisaVO().getLookupEciFlag());
					}
					
					/*BBBSL-5134: New changes for updating MASTER and VISA card commerce indicator. We are overriding VISA commerce indicator value from what
					we set in BBBCheckoutTools because cardinal and cybersource confirmed that commerce indicator should be set as shown below
					VISA:
					ECI RAW			COMMERCE INDICATOR
					05				vbv
					06				vbv_attempted
					07				internet
					MASTER:
					ECI RAW			COMMERCE INDICATOR
					00				internet
					01				spa
					02				spa
					*/ 
						eci = request.getCcAuthService().getEciRaw();
						if (BBBUtility.isNotEmpty(eci)) {
							if (pParams.getCreditCardInfo().getCreditCardType() != null) {
								if (BBBTagConstants.VISA_CARD_TYPE
										.equalsIgnoreCase(pParams
												.getCreditCardInfo()
												.getCreditCardType())) {
									if (BBBTagConstants.VISA_CARD_ECI_05.equalsIgnoreCase(eci)) {
										request.getCcAuthService()
												.setCommerceIndicator(BBBTagConstants.VISA_VBV);
										logDebug("VISA card -- Setting commerce indicator with value : " + BBBTagConstants.VISA_VBV);
									} else if (BBBTagConstants.VISA_CARD_ECI_06.equalsIgnoreCase(eci)) {
										request.getCcAuthService()
												.setCommerceIndicator(
														BBBTagConstants.VISA_VBV_ATTEMPTED);
										logDebug("VISA card -- Setting commerce indicator with value : " + BBBTagConstants.VISA_VBV_ATTEMPTED);
									} else if (BBBTagConstants.VISA_CARD_ECI_07.equalsIgnoreCase(eci)) {
										request.getCcAuthService()
												.setCommerceIndicator(
														BBBTagConstants.EMPTY_COMMERCE_INDICATOR);
										logDebug("VISA card -- Setting commerce indicator with value : " + BBBTagConstants.EMPTY_COMMERCE_INDICATOR);
									}
								} else if(BBBTagConstants.MASTER_CARD_TYPE
										.equalsIgnoreCase(pParams
												.getCreditCardInfo()
												.getCreditCardType())) {
									if (BBBTagConstants.MASTER_CARD_ECI_00.equalsIgnoreCase(eci)) {
										request.getCcAuthService()
												.setCommerceIndicator(BBBTagConstants.EMPTY_COMMERCE_INDICATOR);
										logDebug("Master card -- Setting commerce indicator with value : " + BBBTagConstants.EMPTY_COMMERCE_INDICATOR);
									} else if (BBBTagConstants.MASTER_CARD_ECI_01.equalsIgnoreCase(eci) || BBBTagConstants.MASTER_CARD_ECI_02.equalsIgnoreCase(eci)) {
										request.getCcAuthService()
												.setCommerceIndicator(
														BBBTagConstants.MASTER_CARD_COMMERCE_INDICATOR);
										logDebug("Master card -- Setting commerce indicator with value : " + BBBTagConstants.MASTER_CARD_COMMERCE_INDICATOR);
									}
								}
							}

						}
					//changes end for BBBSL-5134 new changes					
					
					//Changes for BBBSL-5134 (To add Master card transactaion related ECI and cavv in UCAF object)
				if (pParams.getCreditCardInfo().getCreditCardType() != null
								&& BBBTagConstants.MASTER_CARD_TYPE
										.equalsIgnoreCase(pParams
												.getCreditCardInfo()
												.getCreditCardType())) {
							if (BBBUtility.isNotEmpty(eci) && eci.length() > 1) {
								logDebug("Master card -- Setting UCAF CollectionIndicator : "
										+ eci.substring(1));
								request.getUcaf().setCollectionIndicator(
										eci.substring(1));
							}
						}

					}
				} else {
					cyberSourceVO = new CyberSourceVO();
				}
				/*
				 * End: 258-3 - Verified by Visa - Cybersource Integration 
				 */
				
				String totalAmount = request.getPurchaseTotals().getGrandTotalAmount();
				String creditCardNum = pParams.getCreditCardInfo().getCreditCardNumber();
				if (isLoggingDebug()) {
					logDebug("Amount for Auth call : " + totalAmount);
				}
				
				// 1$ call if there is already a response saved from previous request
				if (cyberSourceVO.getCreditCardParams() != null	&& creditCardNum.equals(cyberSourceVO.getCreditCardParams().getCreditCardInfo().getCreditCardNumber()) && totalAmount.equals(cyberSourceVO.getAmount())) {
					if (isLoggingDebug()) {
						logDebug("Subsequent Auth Call for 1 dolloar start");
						logDebug("Amount Before 1$ Auth call : " + request.getPurchaseTotals().getGrandTotalAmount());
					}
					
//					String minAuthAmount = BBBConfigRepoUtils.getStringValue(CART_AND_CHECKOUT_KEYS, "MIN_AUTH_AMOUNT");
					String authAmount = "1"; 
//					if (BBBUtility.isNotEmpty(minAuthAmount)) {
//						authAmount = minAuthAmount;
//					}
					request.getPurchaseTotals().setGrandTotalAmount(authAmount);
					if (isLoggingDebug()) {
						logDebug("Amount After Change in GrandTotal " + request.getPurchaseTotals().getGrandTotalAmount());
					}
					
				}else{
					if (cyberSourceVO.getCreditCardParams() != null) {
						cyberSourceVO.setAmount(null);
						cyberSourceVO.setCreditCardParams(null);
					}
				}
				
				if (isLoggingDebug() && null != vbvSessionBean && vbvSessionBean.getbBBVerifiedByVisaVO()!=null) {
					logDebug("****VBV Request Params ***" );
					if(null != request.getCcAuthService().getCavv()){
						logDebug("**CAVV :" + request.getCcAuthService().getCavv());
					}else{
						logDebug("**CAVV is NULL:");
					}
					
					if(null != request.getCcAuthService().getXid()){
						logDebug("**Xid :" + request.getCcAuthService().getXid());
					}else{
						logDebug("**Xid is NULL:");
					}
					
					if(null != request.getCcAuthService().getCommerceIndicator()){
						logDebug("**CommerceIndicator:" + request.getCcAuthService().getCommerceIndicator());
					}else{
						logDebug("**CommerceIndicator is NULL:");
					}
					
					if(null != request.getCcAuthService().getEciRaw()){
						logDebug("**ECRaw : " + request.getCcAuthService().getEciRaw());
					}else{
						logDebug("**ECRaw is NULL:");
					}
					
					
				}
								
				result = super.runCreditCardProcess(pParams, pResult);
				
				// this parameter is set skip the Cybersource timeout error
				pParams.setCyberSourceStatus(null);
				
				if (result == 0) {
					systemError = true;
					result = 1;
				}
				if (isLoggingInfo()) {
					logInfo("Total Time of BBBProcSendRequest.runCreditCardProcess() method:"
							+ (System.currentTimeMillis() - startTime)
							+ " for request: "
							+ CyberSourcePrintUtils.printRequest(request));
				}
				
				//set the original authCode if call was success
				ReplyMessage reply = pParams.getReply();
				if (reply != null && reply.getReasonCode().intValue() == 100 && cyberSourceVO.getCreditCardParams() != null) {
					String originalAuthTime = cyberSourceVO.getCreditCardParams().getReply().getCcAuthReply().getAuthorizedDateTime();
					String originalAuthCode = cyberSourceVO.getCreditCardParams().getReply().getCcAuthReply().getAuthorizationCode();
					String originalAvsCode = cyberSourceVO.getCreditCardParams().getReply().getCcAuthReply().getAvsCode();
					String originalRequestID = cyberSourceVO.getCreditCardParams().getReply().getRequestID();
					String originalAuthAmount = cyberSourceVO.getAmount();
					
					if (isLoggingDebug()) {
						logDebug("originalAuthTime " + originalAuthTime);
						logDebug("originalAuthCode " + originalAuthCode);
						logDebug("originalAvsCode " + originalAvsCode);
						logDebug("originalRequestID " + originalRequestID);
						logDebug("originalAuthAmount " + originalAuthAmount);
					}
					
					pParams.getReply().setCcAuthReply(cyberSourceVO.getCreditCardParams().getReply().getCcAuthReply());
					pParams.getReply().setRequestID(originalRequestID);
					pParams.getRequest().getPurchaseTotals().setGrandTotalAmount(originalAuthAmount);
					
					if (isLoggingDebug()) {
						logDebug("AuthTime : " + pParams.getReply().getCcAuthReply().getAuthorizedDateTime());
						logDebug("AuthCode : " + pParams.getReply().getCcAuthReply().getAuthorizationCode());
						logDebug("AvsCode : " + pParams.getReply().getCcAuthReply().getAvsCode());
						logDebug("RequestID : " + pParams.getReply().getRequestID());
						logDebug("AuthAmount : " + pParams.getRequest().getPurchaseTotals().getGrandTotalAmount());
					}
					
					cyberSourceVO.setAmount(null);
					cyberSourceVO.setCreditCardParams(null);
				}
			} finally {
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.CYBERSOURCE_AUTH_CALL,
						methodName);
			}
		}
		/* Check if there was System error; if so handle it */
		checkError(systemError, pParams);

		return result;
	}

	/**
	 * Handle System exceptions from Credit Card Authorization and mark the
	 * transaction successful
	 * 
	 * @param errorFlag
	 * @param pParams
	 */
	private void checkError(boolean errorFlag, CreditCardProcParams pParams) {
        DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		int reasonCode = 100;
		ReplyMessage reply = pParams.getReply();
		CyberSourceVO cyberSourceVO = null;
		if(null != request) {
			cyberSourceVO = (CyberSourceVO)request.resolveName("/com/bbb/integration/cybersource/creditcard/CyberSourceVO");
		} else {
			cyberSourceVO = new CyberSourceVO();
		}
        
		if (reply == null) {
			reply = new ReplyMessage();
			reply.setReasonCode(BigInteger.valueOf(150L));
			reply.setCcAuthReply(new CCAuthReply());
			pParams.setReply(reply);
		}

		reasonCode = reply.getReasonCode().intValue();
		logInfo("Cybersource Auth reason code is :" + reasonCode);
		logInfo("Cybersource request ID is: " + reply.getRequestID());
		if (reasonCode == 200) {
            request.setParameter(CYBERSOURCE_AVS_FAIL, BBBCoreConstants.TRUE);
			if (isLoggingInfo()) {
				if(BBBUtility.isEmpty(pParams.getReply().getCcAuthReply().getAuthorizationCode())){
					logInfo("Cybersource AVS Fail " + reasonCode + "\n No AuthorizationCode return from Cyber");
				}else{
					logInfo("Cybersource AVS Fail " + reasonCode + "\nAuthorizationCode return from Cyber");
				}
			}
			//call was success hence save it. First call is always full auth so no need  to save subsequent calls
			//If there is no AuthorizationCode return in Full amount call then no need to save Credit card Info and Next call will be Full Amount call
			if( BBBUtility.isNotEmpty(pParams.getReply().getCcAuthReply().getAuthorizationCode()) && cyberSourceVO.getCreditCardParams() == null){
				if (isLoggingDebug()) {
					logDebug("Saving Credit card Info for 1 $ auth Call");
				}
				RequestMessage requestMessage  = pParams.getRequest();
				String totalAmount = requestMessage.getPurchaseTotals().getGrandTotalAmount();
				cyberSourceVO.setAmount(totalAmount);
				cyberSourceVO.setCreditCardParams(pParams);
			}
		}
		if (reasonCode == 230) {
            request.setParameter(CYBERSOURCE_INVALID_CVV, BBBCoreConstants.TRUE);
			if (isLoggingInfo()) {
				if(BBBUtility.isEmpty(pParams.getReply().getCcAuthReply().getAuthorizationCode())){
					logInfo("Cybersource Invalid CVV " + reasonCode + "\n No AuthorizationCode return from Cyber");
				}else{
					logInfo("Cybersource Invalid CVV " + reasonCode + "\nAuthorizationCode return from Cyber");
				}
			}
			//call was success hence save it. First call is always full auth so no need  to save subsequent calls
			//If there is no AuthorizationCode return in Full amount call then no need to save Credit card Info and Next call will be Full Amount call
			if( BBBUtility.isNotEmpty(pParams.getReply().getCcAuthReply().getAuthorizationCode()) && cyberSourceVO.getCreditCardParams() == null){
				if (isLoggingDebug()) {
					logDebug("Saving Credit card Info for 1 $ auth Call");
				}
				RequestMessage requestMessage  = pParams.getRequest();
				String totalAmount = requestMessage.getPurchaseTotals().getGrandTotalAmount();
				cyberSourceVO.setAmount(totalAmount);
				cyberSourceVO.setCreditCardParams(pParams);
			}
		}
		if (errorFlag || reasonCode == 150 || reasonCode == 151
				|| reasonCode == 152) {
			/*
			 * Mark the Credit card status as successful to make the transaction
			 * success
			 */
			reply.setReasonCode(BigInteger.valueOf(100L));
			String msg = "Credit Card Authorization failed for the following reason(s): ";
			if (StringUtils.isBlank(reply.getCcAuthReply().getAuthRecord())) {
				msg = msg
						+ CyberSourceUtils.getString(
								MessageConstant.CYBER_SOURCE_SYSTEM_FAILURE,
								ServletUtil.getCurrentRequest().getLocale());
			} else {
				msg = msg + reply.getCcAuthReply().getAuthRecord();
			}
			BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_CYBERSOURCE_1001, "Cybersource error in authorization");
			reply.getCcAuthReply().setAuthRecord(msg);
		}
	}


	/**
	 * @return the catalogTools
	 */
	public final BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param pCatalogTools
	 *            the catalogTools to set
	 */
	public final void setCatalogTools(BBBCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	/**
	 * Checks if it is "TEST" interface
	 * 
	 * @param pConnection
	 * @return
	 */
	public boolean isTestEnvironment() {
		return mTestEnvironment;
	}

	public void setTestEnvironment(boolean pTestEnvironment) {
		this.mTestEnvironment = pTestEnvironment;
	}

}
