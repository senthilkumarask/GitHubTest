/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  ProcessCouponDroplet.java
 *
 *  DESCRIPTION: ProcessCouponDroplet extends ATG OOTB DynamoServlet
 *  			 and process the coupon by checking the status. Also
 *               can activate the coupon. If some error comes while 
 *               activation the appropriate info is shown.       	
 *  HISTORY:
 *  12/15/11 Initial version
 *
 */
package com.bbb.selfservice.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.PropertyManager;

import com.bbb.account.BBBProfileTools;
import com.bbb.account.validatecoupon.ValidateCouponRequestVO;
import com.bbb.account.validatecoupon.ValidateCouponResponseVO;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.droplet.BBBPromotionTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.vo.ErrorStatus;
import com.bbb.logging.LogMessageFormatter;

public class ProcessCouponDroplet extends BBBDynamoServlet {

	private BBBProfileTools mTools;
	private PropertyManager mProperty;
	private BBBCatalogToolsImpl mCatalogTools;
	private BBBPromotionTools mPromTools;
	private String mQuery;
	private LblTxtTemplateManager mLblTxtTemplate;

	/**
	 * @return the lblTxtTemplate
	 */
	public LblTxtTemplateManager getLblTxtTemplate() {
		return mLblTxtTemplate;
	}

	/**
	 * @param pLblTxtTemplate
	 *            the lblTxtTemplate to set
	 */
	public void setLblTxtTemplate(LblTxtTemplateManager pLblTxtTemplate) {
		mLblTxtTemplate = pLblTxtTemplate;
	}

	/**
	 * @return the query
	 */
	public String getQuery() {
		return mQuery;
	}

	/**
	 * @param pQuery
	 *            the query to set
	 */
	public void setQuery(String pQuery) {
		mQuery = pQuery;
	}

	/**
	 * @return the promTools
	 */
	public BBBPromotionTools getPromTools() {
		return mPromTools;
	}

	/**
	 * @param pPromTools
	 *            the promTools to set
	 */
	public void setPromTools(BBBPromotionTools pPromTools) {
		mPromTools = pPromTools;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogToolsImpl getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param pCatalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogToolsImpl pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	/**
	 * @return the property
	 */
	public PropertyManager getProperty() {
		return mProperty;
	}

	/**
	 * @param pProperty
	 *            the property to set
	 */
	public void setProperty(PropertyManager pProperty) {
		mProperty = pProperty;
	}

	/**
	 * @return the tools
	 */
	public BBBProfileTools getTools() {
		return mTools;
	}

	/**
	 * @param pTools
	 *            the tools to set
	 */
	public void setTools(BBBProfileTools pTools) {
		mTools = pTools;
	}

	/**
	 * @param pReq
	 * @param pRes
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public void service(DynamoHttpServletRequest pReq,
			DynamoHttpServletResponse pRes) throws ServletException,
			IOException {
		logDebug("ProcessCouponDroplet.service() method Starts");

		String requestType = pReq.getParameter(BBBCoreConstants.REQUEST_TYPE);
		try {
			if (requestType != null
					&& requestType.equalsIgnoreCase(BBBCoreConstants.STATUS)) {
				validateCoupon(pReq, pRes, BBBCoreConstants.STATUS);
			} else if (requestType != null
					&& requestType.equalsIgnoreCase(BBBCoreConstants.ACTIVATE)) {
				validateCoupon(pReq, pRes, BBBCoreConstants.ACTIVATE);
			}
		} catch (BBBBusinessException e) {
			pReq.setParameter(BBBCoreConstants.CMS_ERROR,
					BBBCoreConstants.COUPON_UNKNOWN_ERROR);
			pReq.setParameter(BBBCoreConstants.COUPON_STATUS,
					BBBCoreConstants.ERROR);
			pReq.serviceLocalParameter(BBBCoreConstants.ERROR, pReq,
					pRes);
			logError(LogMessageFormatter.formatMessage(pReq, "BBBBussiness Exception from validateCoupon method of ProcessCouponDroplet", BBBCoreErrorConstants.ACCOUNT_ERROR_1192 ), e);
		} catch (BBBSystemException e) {
			pReq.setParameter(BBBCoreConstants.CMS_ERROR,
					BBBCoreConstants.COUPON_UNKNOWN_ERROR);
			pReq.setParameter(BBBCoreConstants.COUPON_STATUS,
					BBBCoreConstants.ERROR);
			pReq.serviceLocalParameter(BBBCoreConstants.ERROR, pReq,
					pRes);
			logError(LogMessageFormatter.formatMessage(pReq, "BBBSystem Exception from validateCoupon method of ProcessCouponDroplet", BBBCoreErrorConstants.ACCOUNT_ERROR_1193 ), e);
		}

		logDebug("ProcessCouponDroplet.service() method ends");
	}

	/**
	 * This method will validate and process the coupon by calling the web
	 * services depend on the parameter pRequestType. If pRequestType id
	 * 'status' it will fetch only status. If pRequestType id 'Activation' it
	 * call the web service for activation of coupon. If something goes wrong in
	 * calling the web service it will return false
	 * 
	 * @param pReq
	 * @param pRes
	 * @throws ServletException
	 * @throws IOException
	 * @return boolean
	 */
	public boolean validateCoupon(DynamoHttpServletRequest pReq,
			DynamoHttpServletResponse pRes, String pRequestType)
			throws ServletException, IOException, BBBBusinessException,
			BBBSystemException {

		logDebug("ProcessCouponDroplet.validateCoupon() method Starts");
			String currSite = pReq.getParameter(BBBCoreConstants.SITE_ID);
			if (currSite != null
					&& pRes != null
					&& pRequestType != null
					&& (pRequestType.equalsIgnoreCase(BBBCoreConstants.STATUS) || pRequestType
							.equalsIgnoreCase(BBBCoreConstants.ACTIVATE))) {
				ValidateCouponRequestVO reqVO = getCouponRequestVO(pReq,
						pRequestType);
				if (reqVO == null) {
					pReq.setParameter(BBBCoreConstants.CMS_ERROR,
							BBBCoreConstants.ERROR);
					pReq.setParameter(BBBCoreConstants.COUPON_STATUS,
							BBBCoreConstants.ERROR);
					pReq.serviceLocalParameter(BBBCoreConstants.ERROR, pReq, pRes);
	
				} else {
					String methodName = BBBCoreConstants.VALIDATE_COUPON;
					BBBPerformanceMonitor.start(
							BBBPerformanceConstants.WEB_SERVICE_CALL, methodName);
					ValidateCouponResponseVO resVO;
					try {
						resVO = invokeService(reqVO);
					} finally {
						BBBPerformanceMonitor.end(
								BBBPerformanceConstants.WEB_SERVICE_CALL,
								methodName);
					}
					String resStatus = null;
					ErrorStatus errorExist = null;
	                if(resVO != null){
						logDebug("WebService Response recieved Coupon Status ="
								+ resVO.getCouponStatus());
						 resStatus = resVO.getCouponStatus();
						 errorExist = resVO.getStatus();
	                }
					if (errorExist != null && !errorExist.isErrorExists()
							 && resStatus != null) {
						getCouponDetailsFromCatalog(reqVO, resStatus, pReq);
					} else if (errorExist != null && errorExist.isErrorExists()) {
						int errId = errorExist.getErrorId();
						addErrorToReqObj(errId, pReq);
					}
					logDebug("ProcessCouponDroplet.validateCoupon() method ends");
					if (pReq.getParameter(BBBCoreConstants.CMS_CONTENT) == null) {
						pReq.serviceLocalParameter(BBBCoreConstants.ERROR, pReq,
								pRes);
						return false;
					} else {
						pReq.serviceLocalParameter(BBBCoreConstants.OUTPUT, pReq,
								pRes);
						return true;
					}
				}
			}
		return false;
	}

	protected ValidateCouponResponseVO invokeService(ValidateCouponRequestVO reqVO)
			throws BBBBusinessException, BBBSystemException {
		return (ValidateCouponResponseVO) ServiceHandlerUtil
				.invoke(reqVO);
	}

	/**
	 * This method helps validateCoupon to set error in request object
	 * 
	 * @param pErrId
	 * @param pReq
	 */
	public void addErrorToReqObj(int pErrId, DynamoHttpServletRequest pReq) {
		logDebug("ProcessCouponDroplet.addErrorToReqObj() method Starts");
		logDebug("pErrId = " + pErrId + ":pReq" + pReq);
		if (pErrId == BBBCoreConstants.FOUR_HUNDRED_ONE) {
			pReq.setParameter(BBBCoreConstants.CMS_ERROR,
					BBBCoreConstants.EMAIL_ERROR_KEY);
		} else if (pErrId == BBBCoreConstants.FOUR_HUNDRED_TWO) {
			pReq.setParameter(BBBCoreConstants.CMS_ERROR,
					BBBCoreConstants.COUPON_ACTIVATE_ERROR);
			pReq.setParameter(BBBCoreConstants.COUPON_STATUS,
					BBBCoreConstants.ERROR);
		} else {
			pReq.setParameter(BBBCoreConstants.CMS_ERROR,
					BBBCoreConstants.COUPON_UNKNOWN_ERROR);
			pReq.setParameter(BBBCoreConstants.COUPON_STATUS,
					BBBCoreConstants.ERROR);
		}
		logDebug("ProcessCouponDroplet.addErrorToReqObj() method ends");
	}

	/**
	 * This method is helper and the part of validateCoupon() method.
	 * 
	 * @param pReqVO
	 * @param pResStatus
	 * @param pReq
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private void getCouponDetailsFromCatalog(ValidateCouponRequestVO pReqVO,
			String pResStatus, DynamoHttpServletRequest pReq)
			throws BBBSystemException, BBBBusinessException {
		logDebug("ProcessCouponDroplet.getCouponDetailsFromCatalog() method Starts");
		logDebug("pReqVO = " + pReqVO + ":pResStatus = " + pResStatus);
			String code = pReqVO.getEntryCd();
			String currSite = pReq.getParameter(BBBCoreConstants.SITE_ID);
			String requestType = pReq.getParameter(BBBCoreConstants.REQUEST_TYPE);
			String couSesStatus = (String)pReq.getSession().getAttribute(BBBCoreConstants.COUPON_STATUS);
			if (requestType != null && requestType.equalsIgnoreCase(BBBCoreConstants.ACTIVATE) 
				&& couSesStatus !=null && couSesStatus.equalsIgnoreCase(BBBCoreConstants.NOT_ACTIVATED)){
				pReq.getSession().setAttribute(BBBCoreConstants.COUPON_STATUS, BBBCoreConstants.ACTIVATED);
			} else if(requestType != null && requestType.equalsIgnoreCase(BBBCoreConstants.ACTIVATE)
					 && couSesStatus !=null && couSesStatus.equalsIgnoreCase(BBBCoreConstants.ACTIVATED)){
				pResStatus = BBBCoreConstants.ALLREADY_ACTIVE;
			} else if(requestType != null && requestType.equalsIgnoreCase(BBBCoreConstants.ACTIVATE)
					 && pResStatus.equalsIgnoreCase(BBBCoreConstants.ACTIVATED)){
				pResStatus = BBBCoreConstants.ALLREADY_ACTIVE;
			} else if(requestType != null && requestType.equalsIgnoreCase(BBBCoreConstants.STATUS)
						&& pResStatus.equalsIgnoreCase(BBBCoreConstants.ACTIVATED)){
				pResStatus = BBBCoreConstants.PRE_ACTIVATED;
			}
			
			if (pResStatus.equalsIgnoreCase(BBBCoreConstants.NOT_ACTIVATED)) {

				String promId = mCatalogTools.getPromotionId(code);
				String cmsContent = getPromTools().getPromotionCouponKey(promId,
						BBBCoreConstants.ACTIVATION_LABEL_ID, currSite, pReq
								.getLocale().getLanguage(), Boolean.FALSE);
				pReq.setParameter(BBBCoreConstants.CMS_CONTENT, cmsContent);
				pReq.setParameter(BBBCoreConstants.COUPON_STATUS, pResStatus);
				pReq.getSession().setAttribute(BBBCoreConstants.COUPON_STATUS, BBBCoreConstants.NOT_ACTIVATED);
			} else if (pResStatus.equalsIgnoreCase(BBBCoreConstants.ACTIVATED)) {
				String promId = mCatalogTools.getPromotionId(code);
				String cmsContent = getPromTools().getPromotionCouponKey(promId,
						BBBCoreConstants.ACTIVATED_LABEL_ID, currSite, pReq
								.getLocale().getLanguage(), Boolean.FALSE);
				pReq.setParameter(BBBCoreConstants.CMS_CONTENT, cmsContent);
				pReq.setParameter(BBBCoreConstants.COUPON_STATUS, pResStatus);
			} else if (pResStatus.equalsIgnoreCase(BBBCoreConstants.REDEEMED)) {
				pReq.setParameter(BBBCoreConstants.CMS_CONTENT,
						BBBCoreConstants.REDEEMED);
				pReq.setParameter(BBBCoreConstants.COUPON_STATUS, pResStatus);
			} else if (pResStatus.equalsIgnoreCase(BBBCoreConstants.EXPIRED)) {
				String promId = mCatalogTools.getPromotionId(code);
				String cmsContent = getPromTools().getPromotionCouponKey(promId,
						BBBCoreConstants.ACTIVATED_LABEL_ID, currSite, pReq
								.getLocale().getLanguage(), Boolean.TRUE);
				pReq.setParameter(BBBCoreConstants.CMS_CONTENT, cmsContent);
				pReq.setParameter(BBBCoreConstants.COUPON_STATUS, pResStatus);
			} else if (pResStatus
					.equalsIgnoreCase(BBBCoreConstants.PRE_ACTIVATED)) {
				String promId = mCatalogTools.getPromotionId(code);
				String cmsContent = getPromTools().getPromotionCouponKey(promId,
						BBBCoreConstants.ACTIVATED_LABEL_ID, currSite, pReq
								.getLocale().getLanguage(), Boolean.FALSE);
				pReq.setParameter(BBBCoreConstants.CMS_CONTENT, cmsContent);
				pReq.setParameter(BBBCoreConstants.COUPON_STATUS, pResStatus);
			} else if (pResStatus
					.equalsIgnoreCase(BBBCoreConstants.ALLREADY_ACTIVE)) {
				String promId = mCatalogTools.getPromotionId(code);
				String cmsContent = getPromTools().getPromotionCouponKey(promId,
						BBBCoreConstants.ACTIVATED_LABEL_ID, currSite, pReq
								.getLocale().getLanguage(), Boolean.FALSE);
				pReq.setParameter(BBBCoreConstants.CMS_CONTENT, cmsContent);
				pReq.setParameter(BBBCoreConstants.COUPON_STATUS, pResStatus);
			}
		logDebug("ProcessCouponDroplet.getCouponDetailsFromCatalog() method ends");
	}

	/**
	 * This method will give the vo object of ValidateCouponRequestVO. It will
	 * fetch the information from parameter and bean then construct an VO
	 * object.
	 * 
	 * @param pReq
	 * @param pRequestType
	 * @return reqVO
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	private ValidateCouponRequestVO getCouponRequestVO(
			DynamoHttpServletRequest pReq, String pRequestType)
			throws BBBSystemException, BBBBusinessException {
		logDebug("ProcessCouponDroplet.getCouponRequestVO() method Starts");
		logDebug("pRequestType = " + pRequestType);
		String siteId = pReq.getParameter(BBBCoreConstants.SITE_ID);
		String siteFlag = null;
		String userToken = null;

		ValidateCouponRequestVO reqVO = null;
		if (getCatalogTools().getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId) != null
				&& getCatalogTools().getAllValuesForKey(
						BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId)
						.size() > BBBCoreConstants.ZERO) {
			siteFlag = getCatalogTools().getAllValuesForKey(
					BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId).get(
					BBBCoreConstants.ZERO);
		}

		if (getCatalogTools().getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY,
				BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) != null
				&& getCatalogTools().getAllValuesForKey(
						BBBWebServiceConstants.TXT_WSDLKEY,
						BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).size() > BBBCoreConstants.ZERO) {
			userToken = getCatalogTools().getAllValuesForKey(
					BBBWebServiceConstants.TXT_WSDLKEY,
					BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(
					BBBCoreConstants.ZERO);
		}

			String serviceName = pReq
					.getParameter(BBBCoreConstants.SERVICE_NAME);
			String entryCd = pReq.getParameter(BBBCoreConstants.ENTRY_CD);
			String emailAddr = pReq.getParameter(BBBCoreConstants.EMAIL_ADDR);
			String mobilePhone = pReq
					.getParameter(BBBCoreConstants.MOBILE_NUMBER);
			if (siteFlag == null || entryCd == null || userToken == null) {
				logDebug("siteFlag = " + siteFlag + ":entryCd = " + entryCd
						+ ":userToken = " + userToken);
				logDebug("ProcessCouponDroplet.getCouponRequestVO() method ends");
				return null;
			}
			reqVO = new ValidateCouponRequestVO();
			reqVO.setServiceName(serviceName);
			reqVO.setAction(pRequestType);
			reqVO.setEmailAddr(emailAddr);
			reqVO.setMobilePhone(mobilePhone);
			reqVO.setEntryCd(entryCd);
			reqVO.setSiteFlag(siteFlag);
			reqVO.setUserToken(userToken);
		
		logDebug("ProcessCouponDroplet.getCouponRequestVO() method ends");
		return reqVO;
	}

	

}
