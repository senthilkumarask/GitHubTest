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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import com.fedex.ws.openship.v7.AccessorRoleType;
import com.fedex.ws.openship.v7.Address;
import com.fedex.ws.openship.v7.ClientDetail;
import com.fedex.ws.openship.v7.Contact;
import com.fedex.ws.openship.v7.CreateOpenShipmentActionType;
import com.fedex.ws.openship.v7.CreateOpenShipmentRequest;
import com.fedex.ws.openship.v7.CustomerReference;
import com.fedex.ws.openship.v7.CustomerReferenceType;
import com.fedex.ws.openship.v7.DropoffType;
import com.fedex.ws.openship.v7.EMailLabelDetail;
import com.fedex.ws.openship.v7.EMailNotificationDetail;
import com.fedex.ws.openship.v7.EMailNotificationEventType;
import com.fedex.ws.openship.v7.EMailNotificationFormatType;
import com.fedex.ws.openship.v7.EMailNotificationRecipient;
import com.fedex.ws.openship.v7.EMailNotificationRecipientType;
import com.fedex.ws.openship.v7.EMailRecipient;
import com.fedex.ws.openship.v7.FreightShipmentLineItem;
import com.fedex.ws.openship.v7.LabelFormatType;
import com.fedex.ws.openship.v7.LabelSpecification;
import com.fedex.ws.openship.v7.NotificationSeverityType;
import com.fedex.ws.openship.v7.PackageSpecialServicesRequested;
import com.fedex.ws.openship.v7.PackagingType;
import com.fedex.ws.openship.v7.Party;
import com.fedex.ws.openship.v7.Payment;
import com.fedex.ws.openship.v7.PaymentType;
import com.fedex.ws.openship.v7.Payor;
import com.fedex.ws.openship.v7.PendingShipmentDetail;
import com.fedex.ws.openship.v7.PendingShipmentType;
import com.fedex.ws.openship.v7.RequestedPackageLineItem;
import com.fedex.ws.openship.v7.RequestedShipment;
import com.fedex.ws.openship.v7.ReturnEMailDetail;
import com.fedex.ws.openship.v7.ReturnShipmentDetail;
import com.fedex.ws.openship.v7.ReturnType;
import com.fedex.ws.openship.v7.ServiceType;
import com.fedex.ws.openship.v7.ShipmentSpecialServiceType;
import com.fedex.ws.openship.v7.ShipmentSpecialServicesRequested;
import com.fedex.ws.openship.v7.ShippingDocumentImageType;
import com.fedex.ws.openship.v7.TransactionDetail;
import com.fedex.ws.openship.v7.VersionId;
import com.fedex.ws.openship.v7.WebAuthenticationCredential;
import com.fedex.ws.openship.v7.WebAuthenticationDetail;
import com.fedex.ws.openship.v7.Weight;
import com.fedex.ws.openship.v7.WeightUnits;
import com.fedex.ws.openship.v7.FreightShipmentDetail;
	/** fspd
	 *  This class is used to build a request for FedEx Email label Web Service
	 */
	public class CreatePendingShipmentReqBuilder {
		
		private static final ApplicationLogging MLOGGING =
			    ClassLoggingFactory.getFactory().getLoggerForClass(CreatePendingShipmentReqBuilder.class);
		
		/** 
		 *  to hold ship string value
		 */
		public static String SHIP =  "ship";
		
		/** 
		 *  to hold ship simple date format
		 */
		public static String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";
		
		/** 
		 *  to hold KEY
		 */
		public static String KEY = "key";
		
		/** 
		 *  to hold PASSWORD
		 */
		public static String PASSWORD = "password";
		
		/** 
		 *  to hold ACCTNUM
		 */
		public static String ACCTNUM  = "acctNum";
		
		/** 
		 *  to hold METERNUM
		 */
		public static String METERNUM  = "meterNum";
		
		/** 
		 *  to hold String One
		 */
		public static  String STRING_ONE  = "1";
		
		/** 
		 *  to hold US Country Code
		 */
		public static String US  = "US";
		
		/** 
		 *  to hold US Canada Code
		 */
		public static String CA  = "CA";
		
		/** 
		 *  to hold PENDING_SHIPMENT  Code
		 */
		private String PENDING_SHIPMENT = "Pending Shipment";
		/**
		 * This method is used to build a CreateOpenShipmentRequest 
		 * @param pMap - Customer input details.
		 * @param pBBBCatalogTools - used to get Fedex configurable values
		 * @param pSiteContext - used to get site id
		 * @return request - return constructed request for FedEx email label webservice
		 */
		@SuppressWarnings("static-access")
		public  CreateOpenShipmentRequest buildCreatePendingShipmentRequest(Map<String , String> pMap , BBBCatalogTools pBBBCatalogTools ,SiteContext pSiteContext ) {
			CreateOpenShipmentRequest request =  CreateOpenShipmentRequest.Factory.newInstance();
	        request.setClientDetail(createClientDetail(pBBBCatalogTools,pSiteContext));
	        request.setWebAuthenticationDetail(createWebAuthenticationDetail(pBBBCatalogTools,pSiteContext));
	        VersionId versionId =  VersionId.Factory.newInstance();
	        versionId.setServiceId(SHIP);
	        versionId.setMajor(7);
	        versionId.setIntermediate(0);
	        versionId.setMinor(0);
		    request.setVersion(versionId);
		    TransactionDetail transactionDetail =  TransactionDetail.Factory.newInstance();
		    request.setTransactionDetail(transactionDetail);
		    request.setRequestedShipment(buildRequestedShipment(pMap , pBBBCatalogTools , pSiteContext)); 
		    CreateOpenShipmentActionType coSAT =   CreateOpenShipmentActionType.Factory.newInstance();		    
		    CreateOpenShipmentActionType.Enum  coSATN[] = new CreateOpenShipmentActionType.Enum[1];
			coSATN[0]=coSAT.TRANSFER;
			request.setActionsArray(coSATN);		    
		    return request;
		}
		
		/**
		 * This method is used to build a RequestedShipment tag
		 * @param pMap - Holds the details entered by the customer
		 * @param pBBBCatalogTools  - used to fetch configurable values
		 * @param pSiteContext - used to get site id
		 * @return RequestedShipment
		 */
		private  RequestedShipment buildRequestedShipment(Map<String , String> pMap , BBBCatalogTools pBBBCatalogTools ,SiteContext pSiteContext) {
		    RequestedShipment requestedShipment =  RequestedShipment.Factory.newInstance();
		    Calendar calendar = Calendar.getInstance();
		    requestedShipment.setShipTimestamp(calendar); // Ship date and time
		    requestedShipment.setDropoffType(DropoffType.REGULAR_PICKUP); // Dropoff Types are BUSINESS_SERVICE_CENTER, DROP_BOX, REGULAR_PICKUP, REQUEST_COURIER, STATION
		    requestedShipment.setServiceType(ServiceType.FEDEX_GROUND); // Service types are STANDARD_OVERNIGHT, PRIORITY_OVERNIGHT, FEDEX_GROUND ...
		    requestedShipment.setPackagingType(PackagingType.YOUR_PACKAGING); // Packaging type FEDEX_BOK, FEDEX_PAK, FEDEX_TUBE, YOUR_PACKAGING, ...
		    List<String>  bigDecimal = getBigDecimal(pBBBCatalogTools);
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
			    shipperAddress.setCountryCode(US);
		    }else if (site !=null && (site.equals(BBBCoreConstants.SITE_BAB_CA))){
		    	shipperAddress.setCountryCode(CA);
		    }else{
		    	shipperAddress.setCountryCode(US);
		    }
		    shipperParty.setContact(shipperContact);
		    shipperParty.setAddress(shipperAddress);
		    requestedShipment.setShipper(shipperParty);
		    Party recipientParty =  Party.Factory.newInstance(); // Recipient information
		    Contact recipientContact =  Contact.Factory.newInstance();
		    Address recipientAddress =  Address.Factory.newInstance();		    
		    if(site !=null && (site.equals(BBBCoreConstants.SITE_BAB_US))){
		    	recipientParty = AddUSAddress(pBBBCatalogTools,recipientContact,recipientAddress,recipientParty );
		    }
		    if(site !=null && (site.equals(BBBCoreConstants.SITE_BAB_CA))){
		    	recipientParty = AddCAAddress(pBBBCatalogTools, recipientContact, recipientAddress, recipientParty);
		    }
		    if(site !=null && (site.equals(BBBCoreConstants.SITE_BBB))){
		    	recipientParty = AddBabyAddress(pBBBCatalogTools,recipientContact,recipientAddress,recipientParty );
		    }		    
		    recipientAddress.setResidential(Boolean.TRUE);	    
		    requestedShipment.setRecipient(recipientParty);
		    Payment payment =  Payment.Factory.newInstance(); 
		    payment.setPaymentType(PaymentType.SENDER);
		    Payor payor =  Payor.Factory.newInstance();
		    Party responsibleParty =  Party.Factory.newInstance();
		    Map<String , String> hMap = getClientDetail(pBBBCatalogTools,pSiteContext);
		    responsibleParty.setAccountNumber(hMap.get(ACCTNUM));
			payor.setResponsibleParty(responsibleParty);
		    payment.setPayor(payor);
		    requestedShipment.setShippingChargesPayment(payment);		    
		    RequestedPackageLineItem  rPL =  RequestedPackageLineItem.Factory.newInstance();		    
		    RequestedPackageLineItem  rPLI[] =  new RequestedPackageLineItem [1];		    
		    rPLI[0] = rPL;		    
		    PackageSpecialServicesRequested pSSR =  PackageSpecialServicesRequested.Factory.newInstance();		    
		    rPL.setSpecialServicesRequested(pSSR);		    
		    requestedShipment.setRequestedPackageLineItemsArray(rPLI);		    
		    LabelSpecification labelSpecification =  LabelSpecification.Factory.newInstance(); // Label specification	    
		    labelSpecification.setImageType(ShippingDocumentImageType.PDF);// Image types PDF, PNG, DPL, ...	
		    labelSpecification.setLabelFormatType(LabelFormatType.COMMON_2_D); //LABEL_DATA_ONLY, COMMON2D
		    requestedShipment.setPackageCount(new NonNegativeInteger(STRING_ONE));
		    RequestedPackageLineItem rp[] = new RequestedPackageLineItem[1];
		    Weight rpWeight =  Weight.Factory.newInstance(); // Package weight information
		    rpWeight.setValue(new BigDecimal(Integer.parseInt(bigDecimal.get(0))));
		    rpWeight.setUnits(WeightUnits.LB);
		    CustomerReference custRef[] = new CustomerReference[1];
		    custRef[0] =  CustomerReference.Factory.newInstance();
		    custRef[0].setCustomerReferenceType(CustomerReferenceType.P_O_NUMBER);
		    custRef[0].setValue(pMap.get(BBBCoreConstants.SHIPTORMA));
		    rp[0] =  RequestedPackageLineItem.Factory.newInstance();
		    rp[0].setWeight(rpWeight);
		    rp[0].setCustomerReferencesArray(custRef);
		    rp[0].setSequenceNumber(new PositiveInteger(STRING_ONE));
		    rp[0].setItemDescription(getDefaultLabelName(pBBBCatalogTools));
		    requestedShipment.setRequestedPackageLineItemsArray(rp);
		    requestedShipment.setSpecialServicesRequested(createSpecialService(pBBBCatalogTools,pMap,pSiteContext));		   
		//		    requestedShipment.setFreightShipmentDetail(fspd);
		    return requestedShipment;
		}
		
		/**
		 * This method is used to build ShipmentSpecialServicesRequested tag
		 * @param pBBBCatalogTools - to get configurations
		 * @param pMap - to hold customer requested details
		 * @return - ShipmentSpecialServicesRequested
		 */
		@SuppressWarnings("static-access")
		private  ShipmentSpecialServicesRequested createSpecialService(BBBCatalogTools pBBBCatalogTools,Map<String , String> pMap,SiteContext pSiteContext) {
			ShipmentSpecialServicesRequested specialService =  ShipmentSpecialServicesRequested.Factory.newInstance();		
			ShipmentSpecialServiceType ssT = ShipmentSpecialServiceType.Factory.newInstance();
			ShipmentSpecialServiceType ssT1 = ShipmentSpecialServiceType.Factory.newInstance();
			ShipmentSpecialServiceType.Enum  rPLI[] =  new ShipmentSpecialServiceType.Enum [2];			   
			 rPLI[0] = ssT.RETURN_SHIPMENT;
			 rPLI[1] =  ssT1.PENDING_SHIPMENT;
			specialService.setSpecialServiceTypesArray(rPLI);			
			ReturnShipmentDetail returnShipmentDetail =  ReturnShipmentDetail.Factory.newInstance();
			ReturnType.Enum returnType = ReturnType.PENDING;
			returnShipmentDetail.setReturnType(returnType);					
			EMailLabelDetail emailLabelDetail =  EMailLabelDetail.Factory.newInstance();
			ReturnEMailDetail returnEMailDetail =  ReturnEMailDetail.Factory.newInstance();
			returnEMailDetail.setMerchantPhoneNumber(getMerchNumber(pBBBCatalogTools, pSiteContext).get(0));			
			returnShipmentDetail.setReturnEMailDetail(returnEMailDetail);			
			EMailRecipient recipient =  EMailRecipient.Factory.newInstance();
			recipient.setEmailAddress(pMap.get(BBBCoreConstants.EMAIL_ADDRESS));
			recipient.setRole(AccessorRoleType.SHIPMENT_COMPLETOR);
			EMailRecipient ecp[] = new EMailRecipient[1];
			ecp[0] = recipient;			
			emailLabelDetail.setRecipientsArray(ecp);	
			PendingShipmentDetail pendingShipmentDetail =  PendingShipmentDetail.Factory.newInstance();
			pendingShipmentDetail.setType(PendingShipmentType.EMAIL);		
			pendingShipmentDetail.setEmailLabelDetail(emailLabelDetail);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, getNumOfExpirationDays(pBBBCatalogTools));
			Date date3 = cal.getTime();			
		    SimpleDateFormat df = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
		   	try {
		    	String str = df.format(date3);
				pendingShipmentDetail.setExpirationDate(str);
		    } catch (Exception e) {
		    	MLOGGING.logError("Error creating special service "+ e.getMessage());
		    }
			EMailNotificationDetail eMailNotificationDetail =  EMailNotificationDetail.Factory.newInstance();
			EMailNotificationRecipient[] recipients = new EMailNotificationRecipient[1];
			recipients[0] =  EMailNotificationRecipient.Factory.newInstance();
			recipients[0].setEMailNotificationRecipientType(EMailNotificationRecipientType.BROKER);
			recipients[0].setFormat(EMailNotificationFormatType.HTML);
			recipients[0].setEMailNotificationRecipientType(EMailNotificationRecipientType.RECIPIENT);
			recipients[0].setNotificationEventsRequestedArray(addEMailNotificationEventType());
			eMailNotificationDetail.setRecipientsArray(recipients);
			specialService.setEMailNotificationDetail(eMailNotificationDetail);
			specialService.setPendingShipmentDetail(pendingShipmentDetail);
			specialService.setReturnShipmentDetail(returnShipmentDetail);
			return specialService;
		}
		
		/**
		 * Setting default values for required for FedEx
		 * @return Enum of EMailNotificationEventType
		 */
		private  EMailNotificationEventType.Enum[] addEMailNotificationEventType(){			
			EMailNotificationEventType.Enum []  notifications = {
					EMailNotificationEventType.ON_DELIVERY,
					EMailNotificationEventType.ON_EXCEPTION,
					EMailNotificationEventType.ON_SHIPMENT,
					EMailNotificationEventType.ON_TENDER
				};
			return notifications;
		}
		
		/**
		 * To create client details
		 * 
		 * @param pBBBCatalogTools - to get configuration
		 * @return ClientDetail - return account details
		 */
		private  ClientDetail createClientDetail( BBBCatalogTools pBBBCatalogTools , SiteContext pSite) {
	        ClientDetail clientDetail =  ClientDetail.Factory.newInstance();
	        Map<String , String> clientMap = getClientDetail(pBBBCatalogTools,pSite); 
	     
		        clientDetail.setAccountNumber(clientMap.get(ACCTNUM));
		        clientDetail.setMeterNumber(clientMap.get(METERNUM));
		 
	        return clientDetail;
		}
		
		/**
		 * To create web authentication details
		 * 
		 * @param pBBBCatalogTools - to get configuration
		 * @return return web authentication details
		 */
		private  WebAuthenticationDetail createWebAuthenticationDetail(BBBCatalogTools pBBBCatalogTools,SiteContext pSite) {
	        WebAuthenticationCredential wac =  WebAuthenticationCredential.Factory.newInstance();	    
	        Map<String , String> clientMap = getWebAuthenticationDetail(pBBBCatalogTools,pSite);
	        String	key =  clientMap.get(KEY);
	        String  password = clientMap.get(PASSWORD);	    
	        wac.setKey(key);
	        wac.setPassword(password);
	        WebAuthenticationDetail wAD = WebAuthenticationDetail.Factory.newInstance();
	        wAD.setUserCredential(wac);
			return wAD;
		}
		
		/**
		 * Method is used to validate success response
		 * @param notificationSeverityType - Enum value 
		 * @return true - if response is success
		 */
		public  boolean isResponseOk(NotificationSeverityType.Enum notificationSeverityType) {
			
			if (notificationSeverityType == null) {
				return false;
			}
			if (notificationSeverityType.equals(NotificationSeverityType.WARNING) ||
				notificationSeverityType.equals(NotificationSeverityType.NOTE)    ||
				notificationSeverityType.equals(NotificationSeverityType.SUCCESS)) {
				return true;
			}
	 		return false;
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
	 * Used to get Web Authentication Details
	 * @param pBBBCatalogTools - to get catalog tools
	 * @return - return map for web authentication details of bed bath and beyond
	 */
	public Map<String , String> getWebAuthenticationDetail(BBBCatalogTools pBBBCatalogTools, SiteContext pSite){
		Map<String , String> clientDetailsMap = new HashMap<String, String>();
		try {
			   if (pSite !=null && (pSite.equals(BBBCoreConstants.SITE_BAB_CA))){
			List<String>  key = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CA_KEY);
			List<String>  password = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CA_PASSWORD);
			clientDetailsMap.put(KEY, key.get(0));
			clientDetailsMap.put(PASSWORD, password.get(0));
			   }else{

					List<String>  key = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CMN_KEY);
					List<String>  password = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CMN_PASSWORD);
					clientDetailsMap.put(KEY, key.get(0));
					clientDetailsMap.put(PASSWORD, password.get(0));
					   
			   }
		} catch (BBBSystemException e) {
			MLOGGING.logError("Error getting auth details "+ e.getMessage());
		} catch (BBBBusinessException e) {
			MLOGGING.logError("Error getting auth details "+ e.getMessage());
		}
		return clientDetailsMap;
	}

	/**
	 * Used to get account details
	 * This method is used to generate client details
	 * @param pBBBCatalogTools - to get catalog tools
	 * @return return client map
	 */
	public  Map<String , String> getClientDetail(BBBCatalogTools pBBBCatalogTools , SiteContext pSite){
		Map<String , String> clientDetailsMap = new HashMap<String, String>();
		try {
			   if (pSite !=null && (pSite.equals(BBBCoreConstants.SITE_BAB_CA))){
				   List<String>  acctNum = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CA_ACCOUNTNUMBER);
					List<String>  meterNum = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CA_METERNUMBER);					
			             clientDetailsMap.put(ACCTNUM, acctNum.get(0));
			              clientDetailsMap.put(METERNUM, meterNum.get(0));
			    }else{

					List<String>  acctNum = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CMN_ACCOUNTNUMBER);
					List<String>  meterNum = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CMN_METERNUMBER);
					
			             clientDetailsMap.put(ACCTNUM, acctNum.get(0));
			              clientDetailsMap.put(METERNUM, meterNum.get(0));
			    }
		} catch (BBBSystemException e) {
			MLOGGING.logError("Error getting client details "+ e.getMessage());
		} catch (BBBBusinessException e) {
			MLOGGING.logError("Error getting client details "+ e.getMessage());
		}
		return clientDetailsMap;
	}
	
	/**
	 * This method is used to get default weight.
	 * @param pBBBCatalogTools
	 * @return return default weight from configurations
	 */
	public List<String> getBigDecimal(BBBCatalogTools pBBBCatalogTools){
		List<String> bigDecimal = null;
	    try {
	    	bigDecimal = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CMN_VERSIONID_BIGDECIMAL);
		  }catch (BBBSystemException e) {
			  MLOGGING.logError("Error getting big decimal "+ e.getMessage());
		} catch (BBBBusinessException e) {
			MLOGGING.logError("Error getting big decimal "+ e.getMessage());
		}
		return bigDecimal;
		
	}
	
	/**
	 * This method is used to get Merchant Phone Number.
	 * @param pBBBCatalogTools - to get Catalog Tools
	 * @param pSit - used to get site id
	 * @return return default weight from configurations
	 */
	public List<String> getMerchNumber(BBBCatalogTools pBBBCatalogTools, SiteContext pSit){
		List<String>  Phone = null;
		try{
		if(pSit.getSite().getId().equals(BBBCoreConstants.SITE_BAB_CA)){
			 Phone = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CA_PHONENUMBER);
		}else if(pSit.getSite().getId().equals(BBBCoreConstants.SITE_BBB)){
			 Phone = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_BABY_PHONENUMBER);
		}else{
			 Phone = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CMN_PHONENUMBER);
		}
		}catch (Exception e) {
			MLOGGING.logError("Error getting Merch number "+ e.getMessage());
		}
		  return Phone;
	}
	
	/**
	 * This method is used to get default weight.
	 * @param pBBBCatalogTools
	 * @return return default weight from configurations
	 */
	public String getDefaultLabelName(BBBCatalogTools pBBBCatalogTools){
		List<String> defaultName = null;
	    try {
	    	defaultName = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CMN_DEFSULT_LABEL_NAME);
		  }catch (BBBSystemException e) {
			  MLOGGING.logError("Error getting default label "+ e.getMessage());
		} catch (BBBBusinessException e) {
			MLOGGING.logError("Error getting default label "+ e.getMessage());
		}
		return defaultName.get(0);
		
	}
		
	/**
	 * This method is used to get number of expiration days for a label.
	 * @param pBBBCatalogTools - catalog tools object
	 * @return
	 */
	public int getNumOfExpirationDays(BBBCatalogTools pBBBCatalogTools) {
		List<String> expirationDays = null;
		int expDay = 0;
	    try {
	    	expirationDays = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CMN_NUM_EXPIRATION_DAYS);
	    	String expDays = expirationDays.get(0) ;
	    	expDay = Integer.parseInt(expDays);
	    }catch (BBBSystemException e) {
	    	MLOGGING.logError("Error getting number of expiry days "+ e.getMessage());
		} catch (BBBBusinessException e) {
			MLOGGING.logError("Error getting number of expiry days "+ e.getMessage());
		}
		return expDay;
	}

}
