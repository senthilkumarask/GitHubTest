/*
 *  Copyright 2014, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  CreatePendingShipmentReqBuilder.java
 *
 *  DESCRIPTION: to build email tag request for Fedex Service
 *
 *  HISTORY:
 *  12/19/2014 Initial version
 *
 */
package com.bbb.fedex;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;

import atg.multisite.SiteContext;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.fedex.ws.ship.v15.Address;
import com.fedex.ws.ship.v15.ClientDetail;
import com.fedex.ws.ship.v15.Contact;
import com.fedex.ws.ship.v15.CustomerReference;
import com.fedex.ws.ship.v15.CustomerReferenceType;
import com.fedex.ws.ship.v15.DropoffType;
import com.fedex.ws.ship.v15.LabelFormatType;
import com.fedex.ws.ship.v15.LabelSpecification;
import com.fedex.ws.ship.v15.NotificationSeverityType;
import com.fedex.ws.ship.v15.NotificationSeverityType.Enum;
import com.fedex.ws.ship.v15.PackagingType;
import com.fedex.ws.ship.v15.Party;
import com.fedex.ws.ship.v15.Payment;
import com.fedex.ws.ship.v15.PaymentType;
import com.fedex.ws.ship.v15.Payor;
import com.fedex.ws.ship.v15.ProcessShipmentRequest;
import com.fedex.ws.ship.v15.RequestedPackageLineItem;
import com.fedex.ws.ship.v15.RequestedShipment;
import com.fedex.ws.ship.v15.ReturnShipmentDetail;
import com.fedex.ws.ship.v15.ReturnType;
import com.fedex.ws.ship.v15.ServiceType;
import com.fedex.ws.ship.v15.ShipmentSpecialServiceType;
import com.fedex.ws.ship.v15.ShipmentSpecialServicesRequested;
import com.fedex.ws.ship.v15.ShippingDocumentImageType;
import com.fedex.ws.ship.v15.TransactionDetail;
import com.fedex.ws.ship.v15.VersionId;
import com.fedex.ws.ship.v15.WebAuthenticationCredential;
import com.fedex.ws.ship.v15.WebAuthenticationDetail;
import com.fedex.ws.ship.v15.Weight;
import com.fedex.ws.ship.v15.WeightUnits;

/** 
 *  This class is used to build a request for FedEx Label Web Service
 */
public class ProcessShipmentReqBuilder {
	
	private static final ApplicationLogging MLOGGING =
		    ClassLoggingFactory.getFactory().getLoggerForClass(ProcessShipmentReqBuilder.class);
	/**
	 * This method is used to build ProcessShipmentRequest Tag
	 * @param pMap - Customer information
	 * @param pBBBCatalogTools - CatalogToolsProcessShipmentRequest
	 * @param pSiteContext - SiteContext
	 * @return - return ProcessShipmentRequest
	 */
	@SuppressWarnings("static-access")
	public  ProcessShipmentRequest buildRequest(Map<String , String> pMap , BBBCatalogTools pBBBCatalogTools,SiteContext pSiteContext)
	{		
		ProcessShipmentRequest request =  ProcessShipmentRequest.Factory.newInstance(); // Build a request object
        request.setClientDetail(createClientDetail(pBBBCatalogTools,pSiteContext));
        request.setWebAuthenticationDetail(createWebAuthenticationDetail(pBBBCatalogTools,pSiteContext));
        TransactionDetail transactionDetail =  TransactionDetail.Factory.newInstance();
        request.setTransactionDetail(transactionDetail);
        VersionId versionId =  VersionId.Factory.newInstance();
        versionId.setServiceId(CreatePendingShipmentReqBuilder.SHIP);
        versionId.setMajor(15);
        versionId.setIntermediate(0);
        versionId.setMinor(0);
        request.setVersion(versionId);
        RequestedShipment requestedShipment =  RequestedShipment.Factory.newInstance();
        requestedShipment.setShipTimestamp(Calendar.getInstance()); // Ship date and time
        requestedShipment.setServiceType(ServiceType.FEDEX_GROUND); // Service types are STANDARD_OVERNIGHT, PRIORITY_OVERNIGHT, FEDEX_GROUND ...
        requestedShipment.setDropoffType(DropoffType.REGULAR_PICKUP);  
        requestedShipment.setPackagingType(PackagingType.YOUR_PACKAGING); // Packaging type FEDEX_BOX, FEDEX_PAK, FEDEX_TUBE, YOUR_PACKAGING, ...
        requestedShipment.setShipper(addShipper(pMap,pSiteContext)); // Sender information
        requestedShipment.setRecipient(addRecipient(pBBBCatalogTools,pSiteContext)); // company information
        requestedShipment.setShippingChargesPayment(addShippingChargesPayment(pBBBCatalogTools,pSiteContext));
        ShipmentSpecialServicesRequested sss = ShipmentSpecialServicesRequested.Factory.newInstance();
        ShipmentSpecialServiceType sst1 = ShipmentSpecialServiceType.Factory.newInstance();
        ShipmentSpecialServiceType.Enum type[] = new ShipmentSpecialServiceType.Enum[1];
        type[0] = sst1.RETURN_SHIPMENT;
    	ReturnShipmentDetail returnShipmentDetail =  ReturnShipmentDetail.Factory.newInstance();
		ReturnType.Enum returnType = ReturnType.PRINT_RETURN_LABEL;
		returnShipmentDetail.setReturnType(returnType);	
		sss.setReturnShipmentDetail(returnShipmentDetail);
        sss.setSpecialServiceTypesArray(type);
        requestedShipment.setSpecialServicesRequested(sss);
	    requestedShipment.setLabelSpecification(addLabelSpecification());     
        requestedShipment.setPackageCount(new NonNegativeInteger(CreatePendingShipmentReqBuilder.STRING_ONE));   // FedEx Support only one Label for each request.      
	    RequestedPackageLineItem requestedPackageLineItem = addRequestedPackageLineItem(pBBBCatalogTools,pMap);
	    requestedShipment.setRequestedPackageLineItemsArray(new RequestedPackageLineItem[]{requestedPackageLineItem});
	    
	    request.setRequestedShipment(requestedShipment);
	    return request;
	}
	

	/**
	 * This method is used to add Shipper details
	 * @param pMap - to hold customer details
	 * @param pSiteContext - to get site id from Site Context
	 * @return - Party Tag
	 */
	private  Party addShipper(Map<String , String> pMap , SiteContext pSiteContext){
	    Party shipperParty =  Party.Factory.newInstance(); // Sender information
	    Contact shipperContact =  Contact.Factory.newInstance();
	    shipperContact.setPersonName(pMap.get(BBBCoreConstants.FIRST_NAME.toLowerCase()) +" "+pMap.get(BBBCoreConstants.LAST_NAME.toLowerCase()));
	    shipperContact.setPhoneNumber(pMap.get(BBBCoreConstants.PHONE));
	    Address shipperAddress =  Address.Factory.newInstance();
	    shipperAddress.setStreetLinesArray(new String[] {pMap.get(BBBCoreConstants.CC_ADDRESS1) , pMap.get(BBBCoreConstants.CC_ADDRESS2)});
	    shipperAddress.setCity(pMap.get(BBBCoreConstants.SHIP_FROM_CITY.toLowerCase()));
	    shipperAddress.setStateOrProvinceCode(pMap.get(BBBCoreConstants.CC_STATE.toLowerCase()));
	    shipperAddress.setPostalCode(pMap.get(BBBCoreConstants.CC_POSTAL_CODE.toLowerCase()));
	    String site = pSiteContext.getSite().getId();
	    if(site !=null && (site.equals(BBBCoreConstants.SITE_BAB_US) || site.equals(BBBCoreConstants.SITE_BBB)) ){
		    shipperAddress.setCountryCode(CreatePendingShipmentReqBuilder.US);
	    }else if (site !=null && (site.equals(BBBCoreConstants.SITE_BAB_CA))){
	    	shipperAddress.setCountryCode(CreatePendingShipmentReqBuilder.CA);
	    }else{
	    	shipperAddress.setCountryCode(CreatePendingShipmentReqBuilder.US);
	    }	    
	    shipperParty.setContact(shipperContact);
	    shipperParty.setAddress(shipperAddress);
	    return shipperParty;
	}
	
	/**
	 * This method is used to add company information 
	 * @param pBBBCatalogTools - to catalog tools object
 	 * @param pSiteContext - to get site context
	 * @return - return Party Tag
	 */
	private  Party addRecipient(BBBCatalogTools pBBBCatalogTools , SiteContext pSiteContext){
	    String site = pSiteContext.getSite().getId();
	    Party recipientParty =  Party.Factory.newInstance(); // Recipient information
	    Contact recipientContact =  Contact.Factory.newInstance();
	    Address recipientAddress =  Address.Factory.newInstance();
	    recipientAddress.setResidential(Boolean.valueOf(false));	
	    if(site !=null && (site.equals(BBBCoreConstants.SITE_BAB_US))){
	    	recipientParty = AddUSAddress(pBBBCatalogTools,recipientContact,recipientAddress,recipientParty );
	    }
	    if(site !=null && (site.equals(BBBCoreConstants.SITE_BAB_CA))){
	    	recipientParty = AddCAAddress(pBBBCatalogTools, recipientContact, recipientAddress, recipientParty);
	    }
	    if(site !=null && (site.equals(BBBCoreConstants.SITE_BBB))){
	    	recipientParty = AddBabyAddress(pBBBCatalogTools,recipientContact,recipientAddress,recipientParty );
	    }		    
	    return recipientParty;
	}

	/**
	 * This method is used to add shipping charge details
	 * @param pBBBCatalogTools - to Catalog Tools
	 * @return - payment object
	 */
	private  Payment addShippingChargesPayment(BBBCatalogTools pBBBCatalogTools,SiteContext pSite){
	    Payment payment =  Payment.Factory.newInstance(); // Payment information
	    payment.setPaymentType(PaymentType.SENDER);
	    Payor payor =  Payor.Factory.newInstance();
	    Party responsibleParty =  Party.Factory.newInstance();
	    CreatePendingShipmentReqBuilder createPendShipObject = new CreatePendingShipmentReqBuilder();
	   Map<String , String>  clientMap = createPendShipObject.getClientDetail(pBBBCatalogTools,pSite);
	    responsibleParty.setAccountNumber(clientMap.get(CreatePendingShipmentReqBuilder.ACCTNUM));
	    Address responsiblePartyAddress =  Address.Factory.newInstance();
	    responsibleParty.setAddress(responsiblePartyAddress);
	    responsibleParty.setContact( Contact.Factory.newInstance());
		payor.setResponsibleParty(responsibleParty);
	    payment.setPayor(payor);
	    return payment;
	}

	/**
	 * This method is used to add Line Item details reuired for service
	 * @param pBBBCatalogTools - to hold tools
	 * @return - RequestedPackageLineItem Object
	 */
	private  RequestedPackageLineItem addRequestedPackageLineItem(BBBCatalogTools pBBBCatalogTools,Map<String , String> pMap){
		RequestedPackageLineItem requestedPackageLineItem =  RequestedPackageLineItem.Factory.newInstance();	
		requestedPackageLineItem.setSequenceNumber(new PositiveInteger(CreatePendingShipmentReqBuilder.STRING_ONE));
		requestedPackageLineItem.setGroupPackageCount(new PositiveInteger(CreatePendingShipmentReqBuilder.STRING_ONE));
		CreatePendingShipmentReqBuilder createPendShipObject = new CreatePendingShipmentReqBuilder();
		List<String> bigDecimal = createPendShipObject.getBigDecimal(pBBBCatalogTools);
		requestedPackageLineItem.setWeight(addPackageWeight(Double.parseDouble(bigDecimal.get(0))));
		CustomerReference custRef[] = new CustomerReference[1];
	    custRef[0] =  CustomerReference.Factory.newInstance();
	    custRef[0].setCustomerReferenceType(CustomerReferenceType.P_O_NUMBER);
	    custRef[0].setValue(pMap.get(BBBCoreConstants.SHIPTORMA));
		requestedPackageLineItem.setCustomerReferencesArray(custRef);
		return requestedPackageLineItem;
	}
	
	/**
	 * To add LabelSpecification Object
	 * @return - LabelSpecification Object
	 */
	private  LabelSpecification addLabelSpecification(){
	    LabelSpecification labelSpecification =  LabelSpecification.Factory.newInstance();    
		labelSpecification.setImageType(ShippingDocumentImageType.PDF);	
	    labelSpecification.setLabelFormatType(LabelFormatType.COMMON_2_D); 
	    return labelSpecification;
	}

	
	/**
	 * To Add account details
	 * @param pBBBCatalogTools
	 * @return ClientDetail - Account details
	 */
	private  ClientDetail createClientDetail(BBBCatalogTools pBBBCatalogTools,SiteContext pSite) {
        ClientDetail clientDetail =  ClientDetail.Factory.newInstance();     
        CreatePendingShipmentReqBuilder createPendShipObject = new CreatePendingShipmentReqBuilder();
        Map<String , String> clientMap = createPendShipObject.getClientDetail(pBBBCatalogTools, pSite); 
        clientDetail.setAccountNumber(clientMap.get(CreatePendingShipmentReqBuilder.ACCTNUM));
        clientDetail.setMeterNumber(clientMap.get(CreatePendingShipmentReqBuilder.METERNUM));
        return clientDetail;
	}
	
	/**
	 * To add Web Authentication details
	 * @param pBBBCatalogTools
	 * @return WebAuthenticationDetail Object
	 */
	private  WebAuthenticationDetail createWebAuthenticationDetail(BBBCatalogTools pBBBCatalogTools,SiteContext pSite) {
        WebAuthenticationCredential wac = WebAuthenticationCredential.Factory.newInstance();
        CreatePendingShipmentReqBuilder createPendShipObject = new CreatePendingShipmentReqBuilder();
        Map<String , String> clientMap = createPendShipObject.getWebAuthenticationDetail(pBBBCatalogTools,pSite);
        String	key =  clientMap.get(CreatePendingShipmentReqBuilder.KEY);
        String  password = clientMap.get(CreatePendingShipmentReqBuilder.PASSWORD);	
        wac.setKey(key);
        wac.setPassword(password);
        WebAuthenticationDetail wAu = WebAuthenticationDetail.Factory.newInstance();
        wAu.setUserCredential(wac);
		return wAu;
	}


	
	/**
	 * This method is used to get US return address for bed bath and beyond.	 * 
	 * @param pBBBCatalogTools - To get configurations
	 * @param recipientContact - to set recipient contact details
	 * @param recipientAddress - to set recipient address
	 * @param recipientParty - to set party tag
	 * @return - return party tag
	 */
	private Party AddUSAddress(BBBCatalogTools pBBBCatalogTools , Contact recipientContact ,  Address recipientAddress ,Party recipientParty){	
		
		try {
			List<String>  CompanyName = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CMN_COMPANYNAME);
			List<String>  Street1 = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CMN_STREETLINE1);
			List<String>  City = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CMN_CITY);
			List<String>  State = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CMN_STATE);
			List<String>  CountryCode = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CMN_COUNTRYCODE);
			List<String>  Phone = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CMN_PHONENUMBER);			
			List<String>  postCode = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CMN_POSTALCODE);			
			recipientContact.setCompanyName(CompanyName.get(0));
		    recipientContact.setPhoneNumber(Phone.get(0));
		    recipientAddress.setStreetLinesArray(new String[] {Street1.get(0) });
		    recipientAddress.setCity(City.get(0));
		    recipientAddress.setStateOrProvinceCode(State.get(0));
		    recipientAddress.setPostalCode(postCode.get(0));
		    recipientAddress.setCountryCode(CountryCode.get(0));		    
		    recipientParty.setContact(recipientContact);
		    recipientParty.setAddress(recipientAddress);
		} catch (BBBSystemException e) {
			MLOGGING.logError("Error adding address "+ e.getMessage());
		} catch (BBBBusinessException e) {
			MLOGGING.logError("Error adding address "+ e.getMessage());
		}
		return recipientParty;
    	
	}
	
	/**
	 * This method is used to get US return address for bed bath and beyond Canada.	 * 
	 * @param pBBBCatalogTools - To get configurations
	 * @param recipientContact - to set recipient contact details
	 * @param recipientAddress - to set recipient address
	 * @param recipientParty - to set party tag
	 * @return - return party tag
	 */
	private Party AddCAAddress(BBBCatalogTools pBBBCatalogTools , Contact recipientContact ,  Address recipientAddress ,Party recipientParty){
		try {
			List<String>  CompanyName = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CA_COMPANYNAME);
			List<String>  Street1 = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CA_STREETLINE1);
			List<String>  City = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CA_CITY);
			List<String>  State = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CA_STATE);
			List<String>  CountryCode = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CA_COUNTRYCODE);
			List<String>  Phone = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CA_PHONENUMBER);
			List<String>  postCode = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CA_POSTALCODE);
			recipientContact.setCompanyName(CompanyName.get(0));
			recipientContact.setPhoneNumber(Phone.get(0));
			recipientAddress.setStreetLinesArray(new String[] {Street1.get(0) });
			recipientAddress.setCity(City.get(0));
			recipientAddress.setStateOrProvinceCode(State.get(0));
			recipientAddress.setPostalCode(postCode.get(0));
			recipientAddress.setCountryCode(CountryCode.get(0));
			recipientParty.setContact(recipientContact);
			recipientParty.setAddress(recipientAddress);	
		} catch (BBBSystemException e) {
			MLOGGING.logError("Error adding address "+ e.getMessage());
		} catch (BBBBusinessException e) {
			MLOGGING.logError("Error adding address "+ e.getMessage());
		}
		return recipientParty;
    	
	}
	
	/**
	 * This method is used to get US return address for Buy Buy Baby 
	 * @param pBBBCatalogTools - To get configurations
	 * @param recipientContact - to set recipient contact details
	 * @param recipientAddress - to set recipient address
	 * @param recipientParty - to set party tag
	 * @return - return party tag
	 */
	private Party AddBabyAddress(BBBCatalogTools pBBBCatalogTools , Contact recipientContact ,  Address recipientAddress ,Party recipientParty){
		try {
			List<String>  CompanyName = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_BABY_COMPANYNAME);
			List<String>  Street1 = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_BABY_STREETLINE1);
			List<String>  City = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_BABY_CITY);
			List<String>  State = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_BABY_STATE);
			List<String>  CountryCode = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_BABY_COUNTRYCODE);
			List<String>  Phone = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_BABY_PHONENUMBER);
			List<String>  postCode = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_BABY_POSTALCODE);
			 recipientContact.setCompanyName(CompanyName.get(0));
			    recipientContact.setPhoneNumber(Phone.get(0));
			    recipientAddress.setStreetLinesArray(new String[] {Street1.get(0) });
			    recipientAddress.setCity(City.get(0));
			    recipientAddress.setStateOrProvinceCode(State.get(0));
			    recipientAddress.setPostalCode(postCode.get(0));
			    recipientAddress.setCountryCode(CountryCode.get(0));
			    recipientParty.setContact(recipientContact);
			    recipientParty.setAddress(recipientAddress);	
		} catch (BBBSystemException e) {
			MLOGGING.logError("Error adding address "+ e.getMessage());
		} catch (BBBBusinessException e) {
			MLOGGING.logError("Error adding address "+ e.getMessage());
		}
		return recipientParty;
    	
	}
	
	
	/**
	 * This method is used to validate the response
	 * @param enum1 - type of Enum
	 * @return - true if there are no errors in the response
	 */
	public  boolean isResponseOk(Enum enum1) {
		if (enum1 == null) {
			return false;
		}
		if (enum1.equals(NotificationSeverityType.WARNING) ||
			enum1.equals(NotificationSeverityType.NOTE)    ||
			enum1.equals(NotificationSeverityType.SUCCESS)) {
			return true;
		}
 		return false;
	}
	
	/**
	 * This method is used to add Package Details
	 * @param packageWeight - weight
	 * @return - Weight Object
	 */
	private  Weight addPackageWeight(Double packageWeight){
		Weight weight =  Weight.Factory.newInstance();
		weight.setUnits(WeightUnits.LB);
		weight.setValue(new BigDecimal(packageWeight));
		return weight;
	}
}
