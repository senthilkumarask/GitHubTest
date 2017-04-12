package com.bbb.certona.droplet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.certona.utils.CertonaResponseUtils;
import com.bbb.certona.vo.CertonaResponseVO;
import com.bbb.commerce.giftregistry.droplet.BBBPresentationDroplet;
import com.bbb.constants.BBBCertonaConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.httpquery.HTTPQueryManager;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;
import com.bbb.utils.ErrorInfoVO;
import com.bbb.utils.SystemErrorInfo;

/**
 * 
 * @author ikhan2
 * 
 */
public class CertonaDroplet extends BBBPresentationDroplet {

	private CertonaResponseUtils mCertonaResponseUtils;
	private HTTPQueryManager mHttpQueryManager;
	private static final String CLS_NAME = "CLS=[CertonaDroplet]/MSG::";

	/**
	 * The service method for fetching the certona response fomr the web
	 * service. - siteId,isRobot,ResponseVO,schemenames
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		this.logDebug(CLS_NAME + "entering service method");

		final String methodName = BBBCoreConstants.CERTONA_DROPLET;
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CERTONA_REC_CALL, methodName);
		
		try {
			String schemeNames = pRequest.getParameter(BBBCertonaConstants.SCHEME);
			String schemeId = pRequest.getParameter(BBBCertonaConstants.SCHEME_ID);
			if (schemeNames == null) {
				schemeNames = "";
			}
			String [] scheme = StringUtils.split(schemeNames, ';');
			CertonaResponseVO certonaResponseVO = getCertonaResponseUtils().getCertonaResponseVO( pRequest, pResponse);
			
			 String certonaReqURL =  pRequest.getParameter(BBBCoreConstants.CERTONA_REQUEST_URL);
                 if (!BBBUtility.isEmpty(certonaReqURL) && certonaReqURL.contains(BBBCertonaConstants.IPADDRESS)) {
			        int startIndex = certonaReqURL.indexOf(BBBCertonaConstants.IPADDRESS);
			        int certonaReqlen = certonaReqURL.length();
			        String subString = certonaReqURL.substring(startIndex, certonaReqlen);
			        String[] toBeReplaced = subString.split(BBBCoreConstants.AMPERSAND);
		            String replacement = BBBCertonaConstants.HIDDEN_IPADDRESS;
		            certonaReqURL = certonaReqURL.replace(toBeReplaced[0], replacement);
                  }

	        if (certonaResponseVO == null) {
				pRequest.setParameter(BBBCoreConstants.CERTONA_REQUEST_URL,certonaReqURL);
				pRequest.setParameter(OUTPUT_ERROR_MSG,pRequest.getParameter(BBBCoreConstants.WEBSERVICE_ERROR));
				pRequest.serviceLocalParameter(OPARAM_EMPTY, pRequest, pResponse);
			} else {
				 this.logDebug("CertonaResponseVO : " + certonaResponseVO);
					//BBBSL-6574 | Setting requestUrl and reponseXml of Certona
				 	certonaResponseVO.setRequestURL(certonaReqURL);
					certonaResponseVO.setResponseXML(pRequest.getParameter(BBBCoreConstants.CERTONA_RESPONSE_XML));
				/* Adding First element from Certona Response for OOS Recommendation - START */
				if(certonaResponseVO.getResonanceMap() != null && certonaResponseVO.getResonanceMap().get(BBBCertonaConstants.PRODDETAIL_OOS) != null
						&& !BBBUtility.isListEmpty(certonaResponseVO.getResonanceMap().get(BBBCertonaConstants.PRODDETAIL_OOS).getProductsVOsList())){
					certonaResponseVO.setOosProductVO(certonaResponseVO.getResonanceMap().get(BBBCertonaConstants.PRODDETAIL_OOS).getProductsVOsList().get(0));
				}
				/* Adding First element from Certona Response for OOS Recommendation - END */
				//BBBSL-6574 | Setting error code for back up behavior of Certona
				if(null!= pRequest.getParameter(BBBCoreConstants.WEBSERVICE_ERROR) && !pRequest.getParameter(BBBCoreConstants.WEBSERVICE_ERROR).isEmpty()){
					pRequest.setParameter(OUTPUT_ERROR_MSG,pRequest.getParameter(BBBCoreConstants.WEBSERVICE_ERROR));
				}
				pRequest.setParameter(BBBCertonaConstants.CERTONA_RESPONSE_VO, certonaResponseVO);
				pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
				
			}
		} catch (final BBBBusinessException bExcep) {
			this.logError(CLS_NAME + " BBBBusinessException =" + bExcep.getMessage());
			pRequest.setParameter(BBBCoreConstants.CERTONA_REQUEST_URL,pRequest.getParameter(BBBCoreConstants.CERTONA_REQUEST_URL));
			pRequest.setParameter(OUTPUT_ERROR_MSG, bExcep.getMessage());
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);

		} catch (final BBBSystemException bExcep) {
			this.logError(CLS_NAME + " BBBSystemException =" + bExcep.getMessage());
			pRequest.setParameter(BBBCoreConstants.CERTONA_REQUEST_URL,pRequest.getParameter(BBBCoreConstants.CERTONA_REQUEST_URL));
			pRequest.setParameter(OUTPUT_ERROR_MSG, bExcep.getMessage());
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		} finally {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CERTONA_REC_CALL, methodName);
		}
		this.logDebug(CLS_NAME + "exitting service method");
	}

	
	/**
	 * This Method Fetches the certona details
	 * 
	 * @param inputParam
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public CertonaResponseVO getCertonaSlotDetails(final Map<String, String> inputParam) {
		this.logDebug(CLS_NAME + "getCertonaSlotDetails():: inputParam =========>"+ inputParam);

		this.logDebug(CLS_NAME + "entering getCertonaResponseVO method");

		final DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		final DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
		if ((null == inputParam) || inputParam.isEmpty()) {
			return this.getErrorResponseVO(BBBCertonaConstants.ERROR_INVALID_INPUT, BBBCertonaConstants.THIRD_PARTY_CERTONA_ERROR_CODE, true,pRequest);
		}
		if (inputParam.get(BBBCertonaConstants.SITEID) != null) {
			pRequest.setParameter(BBBCertonaConstants.SITE_ID, inputParam.get(BBBCertonaConstants.SITEID));
		} else {
			pRequest.setParameter(BBBCertonaConstants.SITE_ID, SiteContextManager.getCurrentSiteId());
		}
		pRequest.setParameter(BBBCertonaConstants.SCHEME, inputParam.get(BBBCertonaConstants.SCHEME));
		pRequest.setParameter(BBBCertonaConstants.PRODUCTID, inputParam.get(BBBCertonaConstants.PRODUCTID));
		pRequest.setParameter(BBBCertonaConstants.GIFTREGID, inputParam.get(BBBCertonaConstants.GIFTREGID));
		pRequest.setParameter(BBBCertonaConstants.USERID, inputParam.get(BBBCertonaConstants.USERID));
		pRequest.setParameter(BBBCertonaConstants.CONTEXT, inputParam.get(BBBCertonaConstants.CONTEXT));
		pRequest.setParameter(BBBCertonaConstants.NUMBER, inputParam.get(BBBCertonaConstants.NUMBER));
		pRequest.setParameter(BBBCertonaConstants.EXITEMID, inputParam.get(BBBCertonaConstants.EXITEMID));
		pRequest.setParameter(BBBCertonaConstants.CATEGORYID, inputParam.get(BBBCertonaConstants.CATEGORYID));
		pRequest.setParameter(BBBCertonaConstants.COLLEGEID, inputParam.get(BBBCertonaConstants.COLLEGEID));
		pRequest.setParameter(BBBCertonaConstants.REGISTRYTYPE, inputParam.get(BBBCertonaConstants.REGISTRYTYPE));
		pRequest.setParameter(BBBCertonaConstants.TRACKINGID, inputParam.get(BBBCertonaConstants.TRACKINGID));
		pRequest.setParameter(BBBCertonaConstants.SESSIONID, inputParam.get(BBBCertonaConstants.SESSIONID));
		pRequest.setParameter(BBBCertonaConstants.SCHEME_ID, inputParam.get(BBBCertonaConstants.SCHEME_ID_MOBILE));
		pRequest.setParameter(BBBCertonaConstants.RECOMM_PRODUCTS, inputParam.get(BBBCertonaConstants.RECOMM_PRODUCTS));
		
		
		if(StringUtils.isNotBlank(inputParam.get(BBBCertonaConstants.GROUP_FOR_RESLINK))){
			pRequest.setParameter(BBBCertonaConstants.GROUP_FOR_RESLINK, inputParam.get(BBBCertonaConstants.GROUP_FOR_RESLINK));
		}
		
		
		// START 501(A) App Id for NO Results found Schema- Missed in merge
		pRequest.setParameter(BBBCertonaConstants.APP_ID, inputParam.get(BBBCertonaConstants.APPID_CERTONA));
		
		if(StringUtils.isNotBlank(inputParam.get(BBBCertonaConstants.OUTPUT))){
			pRequest.setParameter(BBBCertonaConstants.OUTPUT, inputParam.get(BBBCertonaConstants.OUTPUT));
		}
		
		// END 501(A)
		pRequest.setParameter(BBBCertonaConstants.FROM_REST, "true");
		if ((inputParam.get(BBBCertonaConstants.SCHEME) == null) || StringUtils.isEmpty(inputParam.get(BBBCertonaConstants.SCHEME))) {
			this.logError("Recieve d schemeNames is empty");
			return this.getErrorResponseVO(BBBCertonaConstants.ERROR_INVALID_INPUT, BBBCertonaConstants.THIRD_PARTY_CERTONA_ERROR_CODE, true,pRequest);
		}
		if (this.isLoggingDebug()) {
			@SuppressWarnings("unchecked")
			final Enumeration<String> paramsEnum = pRequest.getParameterNames();
			while (paramsEnum.hasMoreElements()) {
				final String paramName = paramsEnum.nextElement();
				final String paramValue = pRequest.getParameter(paramName);
				this.logDebug("Param Name = " + paramName + "param value =" + paramValue);
			}
		}
		CertonaResponseVO certonaResponseVO = null;
		try {
			this.service(pRequest, pResponse);
			final String error = pRequest.getParameter(OUTPUT_ERROR_MSG);
			if (error != null) {
				this.logError("Certona Serivce throws Business Exception" + error);
				return this.getErrorResponseVO(BBBCertonaConstants.THIRD_PARTY_CERTONA_ERROR_MESSAGE, BBBCertonaConstants.THIRD_PARTY_CERTONA_ERROR_CODE, true,pRequest);
			}
			certonaResponseVO = (CertonaResponseVO) pRequest.getObjectParameter(BBBCertonaConstants.CERTONA_RESPONSE_VO);
			final String content = getCertonaResponseUtils().getCertonaTag(SiteContextManager.getCurrentSiteId());

			/* Invoke Certona in case CertaonTag is enable */
			if ((content != null) && content.equalsIgnoreCase("true")) {
				if (certonaResponseVO == null) {
					String errorDescription = null;
					CertonaResponseVO certonaResponseErrorVo =  this.getErrorResponseVO(BBBCertonaConstants.THIRD_PARTY_CERTONA_ERROR_MESSAGE, BBBCertonaConstants.THIRD_PARTY_CERTONA_ERROR_CODE, true,pRequest);
					
					//Getting the error info from error component
					SystemErrorInfo errorInfo = (SystemErrorInfo) pRequest.resolveName(BBBCertonaConstants.SYSTEM_ERROR_COMPONENT);
					 List <ErrorInfoVO> errorList = errorInfo.getErrorList();
					 if(null != errorList && errorList.size()>0){
					
						 ErrorInfoVO infoVo = errorList.get(0);
						 errorDescription =  infoVo.getErrorDescription();
					 }
					
					 certonaResponseErrorVo.setExceptionDetails(errorDescription);
					return certonaResponseErrorVo;
				
					
				}
			}

		} catch (final ServletException e) {
			getErrorResponseVO(BBBCertonaConstants.THIRD_PARTY_CERTONA_ERROR_MESSAGE, BBBCertonaConstants.THIRD_PARTY_CERTONA_ERROR_CODE, true,pRequest);
		} catch (final IOException e) {
			getErrorResponseVO(BBBCertonaConstants.THIRD_PARTY_CERTONA_ERROR_MESSAGE, BBBCertonaConstants.THIRD_PARTY_CERTONA_ERROR_CODE, true,pRequest);
		} catch (final BBBBusinessException e) {
			getErrorResponseVO(BBBCertonaConstants.THIRD_PARTY_CERTONA_ERROR_MESSAGE, BBBCertonaConstants.THIRD_PARTY_CERTONA_ERROR_CODE, true,pRequest);
		} catch (final BBBSystemException e) {
			getErrorResponseVO(BBBCertonaConstants.THIRD_PARTY_CERTONA_ERROR_MESSAGE, BBBCertonaConstants.THIRD_PARTY_CERTONA_ERROR_CODE, true,pRequest);
		}
		return certonaResponseVO;

	}

	/**
	 * This method is used to create a certonaResponseVO instance and set eror
	 * message and code.
	 * @param pRequest 
	 * 
	 * @return
	 */
	private CertonaResponseVO getErrorResponseVO(final String errorMsg, final String errorCode, final boolean errorExist, DynamoHttpServletRequest pRequest) {
		final CertonaResponseVO certonaResponseVO = new CertonaResponseVO();
		certonaResponseVO.setError(errorMsg);
		certonaResponseVO.setErrorExist(errorExist);
		certonaResponseVO.setErrorCode(errorCode);
		certonaResponseVO.setRequestURL(pRequest.getParameter(BBBCoreConstants.CERTONA_REQUEST_URL));
		return certonaResponseVO;
	}

	/**
	 * @return the mCertonaUtils
	 */
	public CertonaResponseUtils getCertonaResponseUtils() {
		return mCertonaResponseUtils;
	}

	/**
	 * @param mCertonaUtils
	 *            the mCertonaUtils to set
	 */
	public void setCertonaResponseUtils(CertonaResponseUtils mCertonaResponseUtils) {
		this.mCertonaResponseUtils = mCertonaResponseUtils;
	}
	
	/**
	 * @return the mHttpQueryManager
	 */
	public HTTPQueryManager getHttpQueryManager() {
		return mHttpQueryManager;
	}

	/**
	 * @param mHttpQueryManager the mHttpQueryManager to set
	 */
	public void setHttpQueryManager(HTTPQueryManager mHttpQueryManager) {
		this.mHttpQueryManager = mHttpQueryManager;
	}

}
