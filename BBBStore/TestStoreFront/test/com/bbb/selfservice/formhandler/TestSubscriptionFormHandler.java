package com.bbb.selfservice.formhandler;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.constants.BBBCoreConstants.FREQUENCY;
import com.bbb.constants.BBBCoreConstants.SUBSCRIPTION_TYPE;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestSubscriptionFormHandler extends BaseTestCase{
	
	
	public void testHandleSubscribe() throws Exception {
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		SubscriptionFormHandler subscriptionFormHandler = (SubscriptionFormHandler) getObject("subscriptionFormHandler");
		subscriptionFormHandler.setEmailAddr((String)getObject("emailAddr"));
		subscriptionFormHandler.setType(SUBSCRIPTION_TYPE.TYPE_SUBSCRIBE_EMAIL_DIRECTMAIL);
		subscriptionFormHandler.setConfirmEmailAddr((String)getObject("confirmEmailAddr"));
		subscriptionFormHandler.setSalutation(new String[]{"1","2"});
		subscriptionFormHandler.setFirstName((String)getObject("firstName"));
		subscriptionFormHandler.setLastName((String)getObject("lastName"));
		subscriptionFormHandler.setAddressLine1((String)getObject("addressLine1"));
		subscriptionFormHandler.setAddressLine2((String)getObject("addressLine2"));
		subscriptionFormHandler.setCity((String)getObject("city"));
		subscriptionFormHandler.setZipcode((String)getObject("zipcode"));
		subscriptionFormHandler.setPhoneNumber((String)getObject("phoneNumber"));
		subscriptionFormHandler.setMobileNumber((String)getObject("mobileNumber"));
		subscriptionFormHandler.setEmailOffer((Boolean)getObject("emailOffer"));
		subscriptionFormHandler.setMobileOffer((Boolean)getObject("mobileOffer"));	
		subscriptionFormHandler.setDirectMailOffer((Boolean)getObject("directMailOffer"));
		subscriptionFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BedBathUS"));
						
		assertFalse("should give error in form validation", subscriptionFormHandler.getFormError());
		subscriptionFormHandler.handleSubscribe(pRequest, pResponse);
		assertTrue("should give error in tibco service calling", subscriptionFormHandler.isSuccessMessage());
		
	}

	public void testHandleUnSubscribeEmail() throws Exception {
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		SubscriptionFormHandler subscriptionFormHandler = (SubscriptionFormHandler) getObject("subscriptionFormHandler");
		subscriptionFormHandler.setEmailAddr((String)getObject("emailAddr"));
		subscriptionFormHandler.setType(SUBSCRIPTION_TYPE.TYPE_UNSUBSCRIBE_EMAIL);
		subscriptionFormHandler.setFrequency(FREQUENCY.FREQUENCY_ONCE_A_MONTH);
		subscriptionFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BedBathUS"));
						
		assertFalse("should give error in form validation", subscriptionFormHandler.getFormError());
		subscriptionFormHandler.handleUnSubscribeEmail(pRequest, pResponse);
		assertTrue("should give error in tibco service calling", subscriptionFormHandler.isSuccessMessage());
		
	}
	
	public void testHandleUnSubscribeDirectMail() throws Exception {
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		SubscriptionFormHandler subscriptionFormHandler = (SubscriptionFormHandler) getObject("subscriptionFormHandler");
		subscriptionFormHandler.setEmailAddr((String)getObject("emailAddr"));
		subscriptionFormHandler.setType(SUBSCRIPTION_TYPE.TYPE_SUBSCRIBE_EMAIL_DIRECTMAIL);
		subscriptionFormHandler.setFirstName((String)getObject("firstName"));
		subscriptionFormHandler.setLastName((String)getObject("lastName"));
		subscriptionFormHandler.setAddressLine1((String)getObject("addressLine1"));
		subscriptionFormHandler.setAddressLine2((String)getObject("addressLine2"));
		subscriptionFormHandler.setCity((String)getObject("city"));
		subscriptionFormHandler.setZipcode((String)getObject("zipcode"));
		subscriptionFormHandler.setMobileNumber((String)getObject("mobileNumber"));
		subscriptionFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BedBathUS"));
						
		assertFalse("should give error in form validation", subscriptionFormHandler.getFormError());
		subscriptionFormHandler.handleUnSubscribeDirectMail(pRequest, pResponse);
		assertTrue("should give error in tibco service calling", subscriptionFormHandler.isSuccessMessage());
		
	}
	
	
	public void testHandleRequestInfo() throws Exception {
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		SubscriptionFormHandler subscriptionFormHandler = (SubscriptionFormHandler) getObject("subscriptionFormHandler");
		
		subscriptionFormHandler.setType(SUBSCRIPTION_TYPE.TYPE_SUBSCRIBE_EMAIL_DIRECTMAIL);
		subscriptionFormHandler.setFrequency(FREQUENCY.FREQUENCY_ONCE_A_MONTH);
		subscriptionFormHandler.setEmailAddr((String)getObject("emailAddr"));
		subscriptionFormHandler.setConfirmEmailAddr((String)getObject("confirmEmailAddr"));
		subscriptionFormHandler.setSalutation(new String[]{"1","2"});
		subscriptionFormHandler.setFirstName((String)getObject("firstName"));
		subscriptionFormHandler.setLastName((String)getObject("lastName"));
		subscriptionFormHandler.setAddressLine1((String)getObject("addressLine1"));
		subscriptionFormHandler.setAddressLine2((String)getObject("addressLine2"));
		subscriptionFormHandler.setCity((String)getObject("city"));
		subscriptionFormHandler.setZipcode((String)getObject("zipcode"));
		subscriptionFormHandler.setPhoneNumber((String)getObject("phoneNumber"));
		subscriptionFormHandler.setMobileNumber((String)getObject("mobileNumber"));
		subscriptionFormHandler.setEmailOffer((Boolean)getObject("emailOffer"));
		subscriptionFormHandler.setMobileOffer((Boolean)getObject("mobileOffer"));	
		subscriptionFormHandler.setDirectMailOffer((Boolean)getObject("directMailOffer"));
		subscriptionFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BedBathUS"));
		
		subscriptionFormHandler.handleRequestInfo(pRequest, pResponse);
		assertTrue("should give error in tibco service calling", subscriptionFormHandler.isSuccessMessage());
		
	}

	
}
