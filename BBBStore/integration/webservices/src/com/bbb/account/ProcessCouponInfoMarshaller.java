/*
 *
 * File  : ProcessCouponInfoMarshaller.java 
 * 
 * Project:     BBB
 * 
 * Description: Request Marshaller class for  ProcessCoupon Web Service
 * 
 * HISTORY:
 * Initial Version: 12/07/2011
 *   
 * 
 */
package com.bbb.account;

import java.util.IllegalFormatException;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bedbathandbeyond.www.ProcessCouponDocument;
import com.bedbathandbeyond.www.ProcessCouponDocument.ProcessCoupon;

public class ProcessCouponInfoMarshaller extends RequestMarshaller {

	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a request for web service call of
	 * ProcessCoupon
	 * 
	 * @param pReqVO
	 * @return processCouponDocument
	 * @throws BBBSystemException
	 * @throws BBBSystemException
	 */
	public XmlObject buildRequest(ServiceRequestIF pReqVO)
			throws BBBBusinessException, BBBSystemException {
			logDebug("ProcessCouponInfoMarshaller.buildRequest() method Starts");
			logDebug("pRreqVO = " + pReqVO);
		ProcessCouponDocument processCouponDocument = null;
		if (pReqVO != null) {
			try {
				processCouponDocument = ProcessCouponDocument.Factory
						.newInstance();
				processCouponDocument
						.setProcessCoupon(getDozerMappedRequest(pReqVO));

			} finally {
					logDebug("ProcessCouponInfoMarshaller.buildRequest() method ends");
			}
		}

		return processCouponDocument;
	}

	/**
	 * This method maps a page specific VO to web service VO.
	 * 
	 * @param pReqVO
	 * @return processCoupon
	 * @throws BBBSystemException
	 */
	private ProcessCoupon getDozerMappedRequest(ServiceRequestIF pReqVO)
			throws BBBSystemException {		
			logDebug("ProcessCouponInfoMarshaller.getDozerMappedRequest() method Starts");
			logDebug("pReqVO = " + pReqVO);
		ProcessCoupon processCoupon = ProcessCoupon.Factory.newInstance();
		if (pReqVO != null) {
			DozerBeanMapper mapper = getDozerBean().getDozerMapper();
			try {
				mapper.map(pReqVO, processCoupon);
			} catch (MappingException me) {
				logError(
						"ProcessCouponInfoMarshaller.getDozerMappedRequest() have MappingException exception",
						me);
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1348,me.getMessage(), me);
			} catch (IllegalFormatException e) {
				logError(
						"ProcessCouponInfoMarshaller.getDozerMappedRequest() have IllegalFormatException exception",
						e);
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1348,e.getMessage(), e);
			} catch (IllegalArgumentException e) {
				logError(
						"ProcessCouponInfoMarshaller.getDozerMappedRequest() have IllegalArgumentException exception",
						e);
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1348,e.getMessage(), e);
			} catch (SecurityException e) {
				logError(
						"ProcessCouponInfoMarshaller.getDozerMappedRequest() have SecurityException exception",
						e);
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1348,e.getMessage(), e);
			} catch (ClassCastException e) {
				logError(
						"ProcessCouponInfoMarshaller.getDozerMappedRequest() have ClassCastException exception",
						e);
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1348,e.getMessage(), e);
			} finally {
					logDebug("ProcessCouponInfoMarshaller.getDozerMappedRequest() method ends");
			}
		}
		return processCoupon;
	}

}
