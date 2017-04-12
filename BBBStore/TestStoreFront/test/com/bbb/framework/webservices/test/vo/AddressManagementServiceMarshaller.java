package com.bbb.framework.webservices.test.vo;

import java.math.BigInteger;
import java.util.Calendar;

import org.apache.xmlbeans.XmlObject;

import atg.core.util.StringUtils;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.integration.exception.ServiceExceptionConstants.ServiceRequestError;
import com.bbb.framework.integration.exception.ServiceRequestException;
import com.bbb.framework.integration.vo.RequestErrorVO;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bbb.integration.common.header.wsmessageheader.v2.TrackingMessageHeaderType;
import com.bbb.integration.common.header.wsmessageheader.v2.WsMessageHeaderDocument;
import com.bbb.integration.common.header.wsmessageheader.v2.WsMessageHeaderType;
import com.bbb.integration.interfaces.validateaddress.v1.validateaddress_xsd.AdditionalValidationOptionsType;
import com.bbb.integration.interfaces.validateaddress.v1.validateaddress_xsd.RequestAddressType;
import com.bbb.integration.interfaces.validateaddress.v1.validateaddress_xsd.ValidateAddressDocument;
import com.bbb.integration.interfaces.validateaddress.v1.validateaddress_xsd.ValidateAddressType;




/**
 * I am just an example for marshaller and please ignore me if you know how to
 * write a marshaller
 * 
 * @author manohar
 * @version 1.0
 */

public class AddressManagementServiceMarshaller extends RequestMarshaller {
	
	/* (non-Javadoc)
	 * @see com.bbb.integration.api.marshaller.RequestMarshallerIF#buildHeader(com.bbb.integration.api.marshaller.RequestMarshallerIF.HeaderVersion)
	 */
	public XmlObject buildHeader() throws BBBSystemException, BBBBusinessException{

		BBBPerformanceMonitor
		.start("AddressManagementServiceMarshaller-buildHeader");
	
		WsMessageHeaderDocument wsMessageHeaderDocumentV2 = null;
		
			wsMessageHeaderDocumentV2 = WsMessageHeaderDocument.Factory
					.newInstance();
			try {
				wsMessageHeaderDocumentV2
						.setWsMessageHeader(buildWsMessageHeaderTypeV2());
			} catch (Exception e) {
				throw new BBBSystemException(e.getMessage(), e.getCause());
			}

		

		BBBPerformanceMonitor
		.end("AddressManagementServiceMarshaller-buildHeader");
	
		return wsMessageHeaderDocumentV2;
	}

	/* (non-Javadoc)
	 * @see com.bbb.integration.api.marshaller.RequestMarshallerIF#buildRequest(com.bbb.integration.api.vo.ServiceRequestIF)
	 */
	public XmlObject buildRequest(ServiceRequestIF reqVO) throws BBBBusinessException, BBBSystemException {

		BBBPerformanceMonitor
		.start("AddressManagementServiceMarshaller-buildRequest");
	
		ValidateAddressDocument validateAddressDocument = null;
		try {
			validateAddressDocument = ValidateAddressDocument.Factory
					.newInstance();
			validateAddressDocument
					.setValidateAddress(buildValidateAddressType((ValidateAddressReqVO) reqVO));
		} catch (Exception e) {
			throw new BBBSystemException(e.getMessage(),e.getCause());
		}

		BBBPerformanceMonitor
		.end("AddressManagementServiceMarshaller-buildRequest");
	
		return validateAddressDocument;
	}

	/**
	 * Builds the validate address type.
	 * 
	 * @param validateAddressReqVO the validate address req vo
	 * 
	 * @return the validate address type
	 * 
	 * @throws Exception the exception
	 */
	private  ValidateAddressType buildValidateAddressType(
			ValidateAddressReqVO validateAddressReqVO) throws Exception {

		BBBPerformanceMonitor
		.start("AddressManagementServiceMarshaller-buildValidateAddressType");
	
		ValidateAddressType validateAddressType = null;
		try {
			validateAddressType = ValidateAddressType.Factory.newInstance();
			validateAddressType
					.setAddressInfo(buildRequestAddressType(validateAddressReqVO));
			validateAddressType
					.setAdditionalValidationOptions(buildAdditionalValidationOptionsType(validateAddressReqVO));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		BBBPerformanceMonitor
		.end("AddressManagementServiceMarshaller-buildValidateAddressType");
	
		return validateAddressType;
	}

	/**
	 * Builds the request address type.
	 * 
	 * @param validateAddressReqVO the validate address req vo
	 * 
	 * @return the request address type
	 * 
	 * @throws Exception the exception
	 */
	private  RequestAddressType buildRequestAddressType(
			ValidateAddressReqVO validateAddressReqVO) throws Exception {

		BBBPerformanceMonitor
		.start("AddressManagementServiceMarshaller-buildRequestAddressType");
	
		RequestAddressType requestAddressType = null;
		try {
			requestAddressType = RequestAddressType.Factory.newInstance();
			String addressLine1 = null;
			addressLine1 = validateAddressReqVO.getAddressVO().getAddressLine1();
			
			if(!StringUtils.isEmpty(validateAddressReqVO.getAddressVO().getApartmentNumber())){
				addressLine1 += " "+validateAddressReqVO.getAddressVO().getApartmentNumber();
			}
			if (!StringUtils.isEmpty(addressLine1)) {
				requestAddressType.setAddressLine1(addressLine1);
			}
			if(!StringUtils.isBlank(validateAddressReqVO.getAddressVO().getAddressLine2())){
				requestAddressType.setAddressLine2(validateAddressReqVO.getAddressVO().getAddressLine2());
			}
			
			
			
			requestAddressType.setCity(validateAddressReqVO.getAddressVO()
					.getCity());
			requestAddressType.setState(validateAddressReqVO.getAddressVO()
					.getState());
			requestAddressType.setZipCode(validateAddressReqVO.getAddressVO()
					.getZipCode());
			requestAddressType.setCountry(validateAddressReqVO.getAddressVO()
					.getCountry());
			/*if(!StringUtils.isEmpty(validateAddressReqVO.getAddressVO().getUsUrbanName())){
				requestAddressType.setUsUrbanName(validateAddressReqVO
						.getAddressVO().getUsUrbanName());
			}*/
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		BBBPerformanceMonitor
		.end("AddressManagementServiceMarshaller-buildRequestAddressType");
	
		return requestAddressType;
	}

	/**
	 * Builds the additional validation options type.
	 * 
	 * @param validateAddressReqVO the validate address req vo
	 * 
	 * @return the additional validation options type
	 * 
	 * @throws Exception the exception
	 */
	private  AdditionalValidationOptionsType buildAdditionalValidationOptionsType(
			ValidateAddressReqVO validateAddressReqVO) throws Exception {

		BBBPerformanceMonitor
		.start("AddressManagementServiceMarshaller-buildAdditionalValidationOptionsType");
	
		AdditionalValidationOptionsType additionalValidationOptionsType = null;
		try {
			additionalValidationOptionsType = AdditionalValidationOptionsType.Factory
					.newInstance();
			additionalValidationOptionsType
					.setMultipleMatchInd(validateAddressReqVO
							.isMultipleMatchInd());
			additionalValidationOptionsType
					.setMaxMatchingAddressesToBeReturned(validateAddressReqVO
							.getMaxMatchingAddressToBeReturned());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		BBBPerformanceMonitor
		.end("AddressManagementServiceMarshaller-buildAdditionalValidationOptionsType");
	
		return additionalValidationOptionsType;
	}

	/**
	 * Builds the ws message header type v2.
	 * 
	 * @return the ws message header type
	 * 
	 * @throws Exception the exception
	 */
	private  WsMessageHeaderType buildWsMessageHeaderTypeV2()
			throws Exception {

		BBBPerformanceMonitor
		.start("AddressManagementServiceMarshaller-buildWsMessageHeaderTypeV2");
	
		WsMessageHeaderType wsMessageHeaderTypeV2 = null;
		try {
			wsMessageHeaderTypeV2 = WsMessageHeaderType.Factory.newInstance();
			wsMessageHeaderTypeV2
					.setTrackingMessageHeader(buildTrackingMessageHeaderTypeV2());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		BBBPerformanceMonitor
		.end("AddressManagementServiceMarshaller-buildWsMessageHeaderTypeV2");
	
		return wsMessageHeaderTypeV2;
	}

	/**
	 * Builds the tracking message header type v2.
	 * 
	 * @return the tracking message header type
	 * 
	 * @throws Exception the exception
	 */
	private  TrackingMessageHeaderType buildTrackingMessageHeaderTypeV2()
			throws Exception {

		BBBPerformanceMonitor
		.start("AddressManagementServiceMarshaller-buildTrackingMessageHeaderTypeV2");
	
		TrackingMessageHeaderType trackingMessageHeaderTypeV2 = null;
		try {
			trackingMessageHeaderTypeV2 = TrackingMessageHeaderType.Factory
					.newInstance();
			trackingMessageHeaderTypeV2.setApplicationId("test BBB");
			trackingMessageHeaderTypeV2.setApplicationUserId("Manohar");
			trackingMessageHeaderTypeV2.setMessageId("testing validate address webservice using webservice framework");
			trackingMessageHeaderTypeV2.setTimeToLive(new BigInteger("120"));
			trackingMessageHeaderTypeV2.setMessageDateTimeStamp(Calendar.getInstance());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		BBBPerformanceMonitor
		.end("AddressManagementServiceMarshaller-buildTrackingMessageHeaderTypeV2");
	
		return trackingMessageHeaderTypeV2;
	}

}