/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  GetAccountInfoMarshaller.java
 *
 *  DESCRIPTION: GetAccountInfoMarshaller  marshall's the get account information web-service response. 	
 *  HISTORY:
 *  13/12/11 Initial version
 *
 */

package com.bbb.account;

import java.util.IllegalFormatException;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.account.validatecoupon.ActivateCouponRequestVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bedbathandbeyond.www.activatebigblue.ActivateBigBlueDocument;
import com.bedbathandbeyond.www.activatebigblue.ActivateBigBlueDocument.ActivateBigBlue;

public class ActivateCouponMarshaller extends RequestMarshaller {

	
	private static final long serialVersionUID = 1L;

	public XmlObject buildRequest(ServiceRequestIF pReqVO) throws BBBBusinessException, BBBSystemException {
			logDebug("ActivateCouponMarshaller | buildRequest | starts");
		
		BBBPerformanceMonitor.start("ActivateCouponMarshaller-buildRequest");
		
		ActivateBigBlueDocument activateBigBlueDocument = null;
		try {
			activateBigBlueDocument = ActivateBigBlueDocument.Factory.newInstance();
			activateBigBlueDocument.setActivateBigBlue(getDozerMappedResponse(pReqVO));
		} finally {
			BBBPerformanceMonitor.end("ActivateCouponMarshaller-buildRequest");
		}
			logDebug("ActivateCouponMarshaller | buildRequest | ends");
		return activateBigBlueDocument;

	}

	/**
	 * 
	 * @param reqVO
	 * @return
	 * @throws BBBSystemException
	 */
	private ActivateBigBlue getDozerMappedResponse(ServiceRequestIF pReqVO)
			throws BBBSystemException {
			logDebug("ActivateCouponMarshaller getDozerMappedResponse starts");
		BBBPerformanceMonitor
				.start("ActivateCouponMarshaller-getDozerMappedResponse");

		ActivateBigBlue activateBigBlue = ActivateBigBlue.Factory.newInstance();
		
		//DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try {
			//mapper.map(pReqVO, activateBigBlue);
			activateBigBlue.setSOfferCd(((ActivateCouponRequestVO)pReqVO).getOfferCd());
			activateBigBlue.setEmailAddr(((ActivateCouponRequestVO)pReqVO).getEmailAddr());
			activateBigBlue.setMobilePhone(((ActivateCouponRequestVO)pReqVO).getMobilePhone());
			if(((ActivateCouponRequestVO)pReqVO).getPromoEmailFlag().equalsIgnoreCase("false"))
			activateBigBlue.setSEmailOptIn("N");
			else{
				activateBigBlue.setSEmailOptIn("Y");	
			}
			if(((ActivateCouponRequestVO)pReqVO).getPromoMobileFlag().equalsIgnoreCase("false"))
			activateBigBlue.setSMobileOptIn("N");
			else{
				activateBigBlue.setSMobileOptIn("Y");	
			}
			activateBigBlue.setSiteFlag(((ActivateCouponRequestVO)pReqVO).getmSiteFlag());
			activateBigBlue.setUserToken(((ActivateCouponRequestVO)pReqVO).getUserToken());
		} catch (MappingException me) {
			logError(me);
			BBBPerformanceMonitor
			.end("ActivateCouponMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(me.getMessage(), me);
		} catch (IllegalFormatException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("ActivateCouponMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			// usually this is a result of un-expected Enum value in
			// response
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("ActivateCouponMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(e.getMessage(), e);
		} catch (SecurityException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("ActivateCouponMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(e.getMessage(), e);
		} catch (ClassCastException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("ActivateCouponMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(e.getMessage(), e);
		} finally {
			BBBPerformanceMonitor
					.end("ActivateCouponMarshaller-getDozerMappedResponse");
		}
			logDebug("ActivateCouponMarshaller getDozerMappedResponse ends");
		return activateBigBlue;

	}
}
