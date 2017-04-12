/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  ProcessCouponInfoUnMarshaller.java
 *
 *  DESCRIPTION: ProcessCouponInfoUnMarshaller is response unmarshaller for Process Coupon service. 	
 *  HISTORY:
 *  11/22/11 Initial version
 *
 */

package com.bbb.account;

import java.util.IllegalFormatException;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.account.validatecoupon.ValidateCouponResponseVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bbb.framework.webservices.vo.ErrorStatus;
import com.bedbathandbeyond.www.ProcessCouponResponseDocument;
import com.bedbathandbeyond.www.ProcessCouponResponseDocument.ProcessCouponResponse;
import com.bedbathandbeyond.www.ProcessCouponReturn;
import com.bedbathandbeyond.www.Status;

public class ProcessCouponInfoUnMarshaller extends ResponseUnMarshaller {

	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a response.
	 * 
	 * @param pResponseDocument
	 * @return couponsResponseVO
	 * @throws BBBSystemException
	 */
	public ServiceResponseIF processResponse(final XmlObject pResponseDocument)
			throws BBBSystemException {		
			logDebug("ProcessCouponInfoUnMarshaller.ServiceResponseIF method starts");
			logDebug("XmlObject pResponseDocument = " + pResponseDocument);

		ValidateCouponResponseVO couponsResponseVO = null;

		try {
			if (pResponseDocument != null) {

				final ProcessCouponResponse processCpnRes = ((ProcessCouponResponseDocument) pResponseDocument)
						.getProcessCouponResponse();

				if (processCpnRes != null) {
					final ProcessCouponReturn processCpnReturn = processCpnRes
							.getProcessCouponResult();

					if (processCpnReturn.getStatus().getErrorExists()) {
						couponsResponseVO = (ValidateCouponResponseVO) getDozerMappedResponse(processCpnReturn);

					} else {
						couponsResponseVO = new ValidateCouponResponseVO();
						couponsResponseVO.setCouponStatus(processCpnReturn.getCouponStatus());
						final Status status = processCpnReturn.getStatus();
						final ErrorStatus errorStatus = getDozerMappedError(status);
						couponsResponseVO.setStatus(errorStatus);
						couponsResponseVO.setWebServiceError(true);
					}
				}
			}
		} finally {
				logDebug("ProcessCouponInfoUnMarshaller.ServiceResponseIF() method ends");
		}
		return couponsResponseVO;
	}

	/**
	 * This method maps the response XMLObject to the ValidateCouponResponseVO
	 * object
	 * 
	 * @param pCouponsReturn
	 * @return validateCouponsResponseVO
	 * @throws BBBSystemException
	 */
	private ServiceResponseIF getDozerMappedResponse(
			final ProcessCouponReturn pCouponsReturn) throws BBBSystemException {

			logDebug("ProcessCouponInfoUnMarshaller.getDozerMappedResponse() method starts");

		final ValidateCouponResponseVO validateCpnResVO = new ValidateCouponResponseVO();

		final DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try {
			mapper.map(pCouponsReturn, validateCpnResVO);
		} catch (MappingException me) {
			logError(
					"ProcessCouponInfoUnMarshaller.getDozerMappedResponse() have MappingException exception",
					me);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1349,me.getMessage(), me);
		} catch (IllegalFormatException e) {
			logError(
					"ProcessCouponInfoUnMarshaller.getDozerMappedResponse() have IllegalFormatException exception",
					e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1349,e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			logError(
					"ProcessCouponInfoUnMarshaller.getDozerMappedResponse() have IllegalArgumentException exception",
					e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1349,e.getMessage(), e);
		} catch (SecurityException e) {
			logError(
					"ProcessCouponInfoUnMarshaller.getDozerMappedResponse() have SecurityException exception",
					e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1349,e.getMessage(), e);
		} catch (ClassCastException e) {
			logError(
					"ProcessCouponInfoUnMarshaller.getDozerMappedResponse() have ClassCastException exception",
					e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1349,e.getMessage(), e);
		} finally {
				logDebug("ProcessCouponInfoUnMarshaller.getDozerMappedResponse() method ends");
		}

		return validateCpnResVO;
	}

	/**
	 * This method maps the response XMLObject to the ErrorStatus object
	 * 
	 * @param pStatus
	 * @return errorStatus
	 * @throws BBBSystemException
	 */
	private ErrorStatus getDozerMappedError(final Status pStatus)
			throws BBBSystemException {		
			logDebug("ProcessCouponInfoUnMarshaller.getDozerMappedError() method starts");
			logDebug("pStatus = " + pStatus);

		final ErrorStatus errorStatus = new ErrorStatus();

		final DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try {
			mapper.map(pStatus, errorStatus);
		} catch (MappingException me) {
			logError("ProcessCouponInfoUnMarshaller.getDozerMappedError() have MappingException exception",me);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1349,me.getMessage(), me);
		} catch (IllegalFormatException e) {
			logError(
					"ProcessCouponInfoUnMarshaller.getDozerMappedError() have ClassCastException exception",
					e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1349,e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			logError(
					"ProcessCouponInfoUnMarshaller.getDozerMappedError() have IllegalArgumentException exception",
					e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1349,e.getMessage(), e);
		} catch (SecurityException e) {
			logError(
					"ProcessCouponInfoUnMarshaller.getDozerMappedError() have SecurityException exception",
					e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1349,e.getMessage(), e);
		} catch (ClassCastException e) {
			logError(
					"ProcessCouponInfoUnMarshaller.getDozerMappedError() have ClassCastException exception",
					e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1349,e.getMessage(), e);
		} finally {
				logDebug("ProcessCouponInfoUnMarshaller.getDozerMappedError() method ends");
		}
		return errorStatus;
	}

}
