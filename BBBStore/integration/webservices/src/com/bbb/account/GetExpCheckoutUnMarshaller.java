/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: ssh108
 *
 * Created on: 18-Feb-2014
 * --------------------------------------------------------------------------------
 */

package com.bbb.account;

import org.apache.xmlbeans.XmlObject;

import paypalapi.api.ebay.GetExpressCheckoutDetailsResponseDocument;
import paypalapi.api.ebay.GetExpressCheckoutDetailsResponseType;
import atg.core.util.StringUtils;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bbb.framework.webservices.vo.ErrorStatus;
import com.bbb.paypal.BBBAddressPPVO;
import com.bbb.paypal.BBBGetExpressCheckoutDetailsResVO;
import com.bbb.paypal.PayerInfoVO;
import com.bbb.utils.BBBUtility;

import eblbasecomponents.apis.ebay.AddressType;
import eblbasecomponents.apis.ebay.ErrorType;
import eblbasecomponents.apis.ebay.GetExpressCheckoutDetailsResponseDetailsType;
import eblbasecomponents.apis.ebay.PayerInfoType;
import eblbasecomponents.apis.ebay.PaymentDetailsType;

/**
 * This class contain methods used for unmarshalling the webservice response from PayPal.
 * 
 * @author ssh108
 * 
 */
public class GetExpCheckoutUnMarshaller extends ResponseUnMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	/**
	 * This method is used to build a response.
	 */
	/* (non-Javadoc)
	 * @see com.bbb.framework.webservices.ResponseUnMarshaller#processResponse(org.apache.xmlbeans.XmlObject)
	 */
	@Override
	public ServiceResponseIF processResponse(final XmlObject pResponseDocument) throws BBBSystemException {

		
		logDebug("Entry processResponse of SetExpCheckoutUnMarshaller with ServiceRequestIF object:");
		
		BBBPerformanceMonitor.start("SetExpCheckoutUnMarshaller-processResponse");
		
		ErrorStatus errorStatus = new ErrorStatus();
		errorStatus.setErrorExists(false);
		GetExpressCheckoutDetailsResponseDocument detDoc = (GetExpressCheckoutDetailsResponseDocument) pResponseDocument;
		
		GetExpressCheckoutDetailsResponseType response = detDoc.getGetExpressCheckoutDetailsResponse();
		
		GetExpressCheckoutDetailsResponseDetailsType detailRes = response.getGetExpressCheckoutDetailsResponseDetails();
		
		BBBGetExpressCheckoutDetailsResVO bbbGetVO = new BBBGetExpressCheckoutDetailsResVO();
		
		PayerInfoVO payerInfo = new PayerInfoVO();
		
		BBBAddressPPVO addressVO = new BBBAddressPPVO();
		BBBAddressPPVO shippingAddressVO = new BBBAddressPPVO();
		BBBAddressPPVO bbbBillingAddress = new BBBAddressPPVO();
		final String ack = response.getAck().toString();
				
		PayerInfoType payerInfoType = detailRes.getPayerInfo();
		PaymentDetailsType[] paymentDetails = detailRes.getPaymentDetailsArray();
		AddressType shippingAddress = paymentDetails[0].getShipToAddress();
		AddressType addressType = payerInfoType.getAddress();
		//Setting billing address
		
		AddressType billingAddress= detailRes.getBillingAddress();
		if(billingAddress != null && billingAddress.getName() != null && !billingAddress.getName().equalsIgnoreCase("")){
			String[] fullName= billingAddress.getName().split(" ");
			if(fullName != null && fullName.length > 0){
				populateName(fullName, bbbBillingAddress);
			}
			bbbBillingAddress.setAddress1(billingAddress.getStreet1().replaceAll(BBBCoreConstants.ASCIICHARACTER, BBBCoreConstants.BLANK));	
			if(!StringUtils.isEmpty(billingAddress.getStreet2())) {
				bbbBillingAddress.setAddress2(billingAddress.getStreet2().replaceAll(BBBCoreConstants.ASCIICHARACTER, BBBCoreConstants.BLANK));
			}
			bbbBillingAddress.setCity(billingAddress.getCityName().replaceAll(BBBCoreConstants.ASCIICHARACTER, BBBCoreConstants.BLANK));
			bbbBillingAddress.setState(billingAddress.getStateOrProvince());
			bbbBillingAddress.setCountry(billingAddress.getCountry().toString());
			bbbBillingAddress.setCountryName(billingAddress.getCountryName());
			bbbBillingAddress.setPhoneNumber(billingAddress.getPhone());
			bbbBillingAddress.setPostalCode(billingAddress.getPostalCode());
		}
		
		bbbGetVO.setBillingAddress(bbbBillingAddress);
		
		if (ack.toString().equalsIgnoreCase(BBBCoreConstants.SUCCESS)) {
			bbbGetVO.setAck(response.getAck().toString());
			bbbGetVO.setCorrelationID(response.getCorrelationID());
			bbbGetVO.setTimeStamp(response.getTimestamp().toString());
			bbbGetVO.setToken(detailRes.getToken());
			if(!StringUtils.isEmpty(detailRes.getContactPhone())) {
			bbbGetVO.setContactPhone(detailRes.getContactPhone().replaceAll("[-+ ]",""));
			}
			bbbGetVO.setBuyerEmail(detailRes.getBuyerMarketingEmail());
			if(addressType != null && addressType.getName() != null && !addressType.getName().equalsIgnoreCase("")){
				String[] fullName= addressType.getName().split(" ");
				if(fullName != null && fullName.length > 0){
					populateName(fullName, addressVO);
				}
				addressVO.setState(addressType.getStateOrProvince());
				addressVO.setCity(addressType.getCityName());
				addressVO.setPostalCode(addressType.getPostalCode());
				addressVO.setMobileNumber(addressType.getPhone());
				addressVO.setCountry(addressType.getCountryName());
				addressVO.setAddress1(addressType.getStreet1());
				addressVO.setAddress2(addressType.getStreet2());
			}
			
			payerInfo.setAddress(addressVO);
			payerInfo.setPayerBiz(payerInfoType.getPayerBusiness());
			if(payerInfoType.getPayerCountry() != null){
				payerInfo.setPayerCountryCode(payerInfoType.getPayerCountry().toString());
			}
			payerInfo.setPayerEmail(payerInfoType.getPayer());
			payerInfo.setPayerID(payerInfoType.getPayerID());
			payerInfo.setPayerFirstName(payerInfoType.getPayerName().getFirstName());
			payerInfo.setPayerMiddleName(payerInfoType.getPayerName().getMiddleName());
			payerInfo.setPayerLastName(payerInfoType.getPayerName().getLastName());
			payerInfo.setPayerStatus(payerInfoType.getPayerStatus().toString());
			
			bbbGetVO.setPayerInfo(payerInfo);
			bbbGetVO.setCheckoutStatus(detailRes.getCheckoutStatus());
			
			//Populate Shipping Address 
			if(shippingAddress != null && !StringUtils.isEmpty(shippingAddress.getStreet1())){
				String[] fullName = shippingAddress.getName().split(" ");
				if(fullName != null && fullName.length > 0){
					populateName(fullName, shippingAddressVO);
				}
				shippingAddressVO.setAddress1(shippingAddress.getStreet1());
				shippingAddressVO.setAddress2(shippingAddress.getStreet2());
				shippingAddressVO.setCity(shippingAddress.getCityName());
				shippingAddressVO.setState(shippingAddress.getStateOrProvince());
				shippingAddressVO.setCountry(shippingAddress.getCountry().toString());
				shippingAddressVO.setPostalCode(shippingAddress.getPostalCode());
				shippingAddressVO.setMobileNumber(shippingAddress.getPhone());
				if(BBBUtility.isEmpty(shippingAddress.getAddressStatus().toString())){
					logInfo("Response recieved from paypal - Payer-Id: " + payerInfoType.getPayerID() + " Paypal Response: " + bbbGetVO.toString());
				}
				shippingAddressVO.setAddressStatus(shippingAddress.getAddressStatus().toString());
				bbbGetVO.setShippingAddress(shippingAddressVO);
			}
			
		} else {
			bbbGetVO.setWebServiceError(true);
			final ErrorType[] errorType = response.getErrorsArray();
			for (ErrorType type : errorType) {
				errorStatus.setDisplayMessage(type.getShortMessage());
				errorStatus.setErrorId(Integer.parseInt(type.getErrorCode()));
				errorStatus.setErrorExists(true);
				errorStatus.setErrorMessage(type.getLongMessage());
			}
			
		}
		bbbGetVO.setErrorStatus(errorStatus);
		BBBPerformanceMonitor.end("SetExpCheckoutUnMarshaller-processResponse");
		
		logDebug("Exit processResponse of SetExpCheckoutUnMarshaller with XmlObject object:");
		
		return bbbGetVO;

	}
	
	/**
	 * This method populates First and last name.
	 * Split Full name on basis of "space" and assign last word to last Name, rest will go in First Name
	 * If there is only one word, then assign same word to both first name and last name.
	 * 
	 * @param name
	 * @param address
	 */
	private static void populateName(String[] name, BBBAddressPPVO address){
		
		String firstName = BBBCoreConstants.BLANK;
		String lastName = BBBCoreConstants.BLANK;
		int length = name.length;
		if(length > 1){
			for(int i = 0; i < length -1 ; i++){
				firstName = firstName + name[i] + BBBCoreConstants.SPACE;
			}
			lastName = name[length - 1];
		}
		else{
			firstName = name[length - 1];
			lastName = firstName;
		}
		address.setFirstName(firstName.trim().replaceAll(BBBCoreConstants.ASCIICHARACTER, BBBCoreConstants.BLANK));
		address.setLastName(lastName.trim().replaceAll(BBBCoreConstants.ASCIICHARACTER, BBBCoreConstants.BLANK));
	}

}