package com.bbb.framework.webservices.test.vo;

import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlObject;

import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.integration.exception.ServiceExceptionConstants.ServiceResponseError;
import com.bbb.framework.integration.exception.ServiceResponseException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bbb.integration.interfaces.validateaddress.v1.validateaddress_xsd.ResponseAddressListType;
import com.bbb.integration.interfaces.validateaddress.v1.validateaddress_xsd.ResponseAddressType;
import com.bbb.integration.interfaces.validateaddress.v1.validateaddress_xsd.ValidateAddressResponseDocument;
import com.bbb.integration.interfaces.validateaddress.v1.validateaddress_xsd.ValidateAddressResponseType;



/**
 * I am just an example of unmarshaller. Please ignore me if you know how to
 * write unmarshaller
 * 
 * @author manohar
 * @version 1.0
 */
public class AddressManagementServiceUnMarshaller extends ResponseUnMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbb.integration.api.unmarshaller.ResponseUnMarshaller#processResponse
	 * (org.apache.xmlbeans.XmlObject)
	 */
	public ServiceResponseIF processResponse(final XmlObject responseDocument) throws BBBSystemException {

		BBBPerformanceMonitor
				.start("AddressManagementServiceUnMarshaller-processResponse");

		ValidateAddressResVO validateAddressResVO = new ValidateAddressResVO();
		if (responseDocument != null) {
			try {
				final ValidateAddressResponseType validateAddressResponseType = ((ValidateAddressResponseDocument) responseDocument)
						.getValidateAddressResponse();
				if (validateAddressResponseType != null) {
					List<AddressVO> addressVOList = getAddressVOs(validateAddressResponseType);
					validateAddressResVO.setAddressVOList(addressVOList);
				}
			} catch (Exception e) {
				throw new BBBSystemException(e.getMessage(), e.getCause());
			}
		}

		BBBPerformanceMonitor
				.end("AddressManagementServiceUnMarshaller-processResponse");
		return validateAddressResVO;
	}

	/**
	 * Gets the address v os.
	 * 
	 * @param validateAddressResponseType
	 *            the validate address response type
	 * 
	 * @return the address v os
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private static List<AddressVO> getAddressVOs(
			final ValidateAddressResponseType validateAddressResponseType)
			throws Exception {

		BBBPerformanceMonitor
				.start("AddressManagementServiceUnMarshaller-getAddressVOs");

		List<AddressVO> addressVOList = null;
		try {
			final ResponseAddressListType responseAddressListType = validateAddressResponseType
					.getValidatedAddressList();
			if (responseAddressListType != null) {
				final ResponseAddressType[] responseAddressTypes = responseAddressListType
						.getValidatedAddressInfoArray();
				if (responseAddressTypes != null) {
					int len = responseAddressTypes.length;
					addressVOList = new ArrayList<AddressVO>(len);
					for (int i = 0; i < len; i++) {
						AddressVO thisAddrVO = new AddressVO();
						thisAddrVO.setAddressLine1(responseAddressTypes[i]
								.getAddressLine1());
						thisAddrVO.setAddressLine2(responseAddressTypes[i]
								.getAddressLine2());
						thisAddrVO.setAddressType(responseAddressTypes[i]
								.getAddressType());
						thisAddrVO.setApartmentNumber(responseAddressTypes[i]
								.getApartmentNumber());
						thisAddrVO.setCity(responseAddressTypes[i].getCity());
						thisAddrVO.setState(responseAddressTypes[i].getState());
						thisAddrVO.setZipCode(responseAddressTypes[i]
								.getZipCode());
						thisAddrVO.setCountry(responseAddressTypes[i]
								.getCountry());
						thisAddrVO
								.setMultipleMatchesFoundInd(responseAddressTypes[i]
										.getMultipleMatchesFoundInd());
						/*
						 * thisAddrVO.setMatchScore(responseAddressTypes[i]
						 * .getAdditionalAddressInfo().getMatchScore());
						 * thisAddrVO.setConfidence(responseAddressTypes[i].
						 * getAdditionalAddressInfo().getConfidence());
						 * thisAddrVO
						 * .setAddressNotFoundInCode1Ind(responseAddressTypes
						 * [i]. getAddressNotFoundInCode1Ind());
						 * thisAddrVO.setUsUrbanName
						 * (responseAddressTypes[i].getUsUrbanName());
						 */

						addressVOList.add(thisAddrVO);
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}

		BBBPerformanceMonitor
				.end("AddressManagementServiceUnMarshaller-getAddressVOs");

		return addressVOList;
	}

}
