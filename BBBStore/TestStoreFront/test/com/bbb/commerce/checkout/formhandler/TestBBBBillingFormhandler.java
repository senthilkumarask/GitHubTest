package com.bbb.commerce.checkout.formhandler;

import java.util.List;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.common.BBBAddress;
import com.bbb.commerce.common.BBBAddressImpl;
import com.bbb.commerce.common.BBBPopulateStatesDroplet;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBBillingFormhandler extends BaseTestCase{

    @SuppressWarnings("rawtypes")
    public void testHandleSaveBillingAddress() throws Exception {
        final DynamoHttpServletRequest pRequest = this.getRequest();
        final DynamoHttpServletResponse pResponse = this.getResponse();
        final BBBBillingAddressFormHandler billingAddrForm = (BBBBillingAddressFormHandler) this.getObject("billingAddrForm");
        ServletUtil.setCurrentRequest(pRequest);
        // set order to formhandler from request
        // set profile to formhandler from request
        // set address container to formhandler from request
        final String siteId = "BedBathUS";
        SiteContextManager siteContextManager = (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext
					.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
       // billingAddrForm.getPromotionLookupManager().getCouponManager().setSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
        final BBBOrder order = (BBBOrder)billingAddrForm.getOrder();
        order.setSiteId(siteId);
        final BBBAddress mBillingAddress = new BBBAddressImpl();
        mBillingAddress.setFirstName((String)this.getObject("firstName"));
        mBillingAddress.setMiddleName((String)this.getObject("middleName"));
        mBillingAddress.setLastName((String)this.getObject("lastName"));
        mBillingAddress.setAddress1((String)this.getObject("address1"));
        mBillingAddress.setAddress2((String)this.getObject("address2"));
        mBillingAddress.setCity((String)this.getObject("city"));
        mBillingAddress.setEmail((String)this.getObject("email"));
        mBillingAddress.setMobileNumber((String)this.getObject("phoneNumber"));
        mBillingAddress.setPostalCode((String) this.getObject("zipCode"));
        mBillingAddress.setState((String) this.getObject("state"));
        // Error scenario Edit Start
        billingAddrForm.setUserSelectedOption("EDIT");
        billingAddrForm.handleSaveBillingAddress(pRequest, pResponse);
        assertTrue("should give error email not matching", billingAddrForm.getFormError());
        billingAddrForm.resetFormExceptions();
        // Error scenario Edit End

        billingAddrForm.setUserSelectedOption("NEW");
        billingAddrForm.setBillingAddress(mBillingAddress);
        billingAddrForm.handleSaveBillingAddress(pRequest, pResponse);
        assertTrue("should give error email not matching", billingAddrForm.getFormError());
        billingAddrForm.resetFormExceptions();
        billingAddrForm.setConfirmedEmail((String)this.getObject("email"));
        billingAddrForm.handleSaveBillingAddress(pRequest, pResponse);
        // get order and profile from request
        // get the billing address from order and compare

        billingAddrForm.setUserSelectedOption("XYZ");
        mBillingAddress.setEmail((String)this.getObject("email"));
        mBillingAddress.setMobileNumber((String)this.getObject("phoneNumber"));
        billingAddrForm.getBillingAddressContainer().addAddressToContainer("XYZ", order.getBillingAddress());
        billingAddrForm.handleSaveBillingAddress(pRequest, pResponse);

        /*BBBOrder order = (BBBOrder) getRequest().resolveName("/com/bbb/ecommerce/order/orderImpl");*/


        this.addObjectToAssert("firstName", order.getBillingAddress().getFirstName());
        this.addObjectToAssert("middleName", order.getBillingAddress().getMiddleName());
        this.addObjectToAssert("lastName", order.getBillingAddress().getLastName());
        this.addObjectToAssert("address1", order.getBillingAddress().getAddress1());

        assertEquals(order.getBillingAddress().getFirstName(), mBillingAddress.getFirstName());
        assertEquals(order.getBillingAddress().getLastName(), mBillingAddress.getLastName());
        assertEquals(order.getBillingAddress().getEmail(), mBillingAddress.getEmail());
        assertEquals(order.getBillingAddress().getAddress1(), mBillingAddress.getAddress1());
        assertEquals(order.getBillingAddress().getAddress2(), mBillingAddress.getAddress2());
        assertEquals(order.getBillingAddress().getCity(), mBillingAddress.getCity());

        final BBBPopulateStatesDroplet dropletObj = (BBBPopulateStatesDroplet) this.getObject("stateDroplet");
        pRequest.setParameter("siteId", "BedBathUS");
        dropletObj.service(pRequest, pResponse);
        final List resultStates = (List) pRequest.getObjectParameter("states");

        assertNotNull("states not returned by BBBPopulateStatesDroplet", resultStates);
        assertTrue("no states returned by BBBPopulateStatesDroplet", resultStates.size() > 0);


    }

    /*public void testSaveAddressToProfileNegative() throws Exception {
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		BBBBillingAddressFormHandler billingAddrForm = (BBBBillingAddressFormHandler) getObject("bbbFormHandler");

		// set order to formhandler from request
		// set profile to formhandler from request
		// set address container to formhandler from request

		BBBAddress mBillingAddress = new BBBAddressImpl();
		mBillingAddress.setFirstName((String)getObject("firstName"));
		mBillingAddress.setMiddleName((String)getObject("middleName"));
		mBillingAddress.setLastName((String)getObject("lastName"));
		mBillingAddress.setAddress1((String)getObject("address1"));
		mBillingAddress.setAddress2((String)getObject("address2"));
		mBillingAddress.setCity((String)getObject("city"));
		mBillingAddress.setEmail((String)getObject("email"));
		mBillingAddress.setPhoneNumber((String)getObject("phoneNumber"));

		billingAddrForm.setBillingAddress(mBillingAddress);

		billingAddrForm.handleSaveBillingAddress(pRequest, pResponse);

		// get order and profile from request
		// get the billing address from order and compare

		BBBOrder order = (BBBOrder) getRequest().resolveName("/com/bbb/ecommerce/order/orderImpl");
		Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");

		// TODO check for class cast exception
		Profile profile = (Profile)billingAddrForm.getProfile();
		BBBAddressAPI bbbAddressApi = new BBBAddressAPIImpl();
		BBBAddress defaultProfileBillingAddress = bbbAddressApi.getDefaultBillingAddress(profile, "1");

		assertNotSame(defaultProfileBillingAddress.getFirstName(), mBillingAddress.getFirstName());
		assertNotSame(defaultProfileBillingAddress.getLastName(), mBillingAddress.getLastName());
		assertNotSame(defaultProfileBillingAddress.getEmail(), mBillingAddress.getEmail());
		assertNotSame(defaultProfileBillingAddress.getAddress1(), mBillingAddress.getAddress1());
		assertNotSame(defaultProfileBillingAddress.getAddress2(), mBillingAddress.getAddress2());
		assertNotSame(defaultProfileBillingAddress.getCity(), mBillingAddress.getCity());
	}

	public void testSaveAddressToProfilePositive() throws Exception {
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		BBBBillingAddressFormHandler billingAddrForm = (BBBBillingAddressFormHandler) getObject("bbbFormHandler");

		// set order to formhandler from request
		// set profile to formhandler from request
		// set address container to formhandler from request

		BBBAddress mBillingAddress = new BBBAddressImpl();
		mBillingAddress.setFirstName((String)getObject("firstName"));
		mBillingAddress.setMiddleName((String)getObject("middleName"));
		mBillingAddress.setLastName((String)getObject("lastName"));
		mBillingAddress.setAddress1((String)getObject("address1"));
		mBillingAddress.setAddress2((String)getObject("address2"));
		mBillingAddress.setCity((String)getObject("city"));
		mBillingAddress.setEmail((String)getObject("email"));
		mBillingAddress.setPhoneNumber((String)getObject("phoneNumber"));

		billingAddrForm.setSaveToAccount(true);

		billingAddrForm.handleSaveBillingAddress(pRequest, pResponse);

		// get order and profile from request
		// get the billing address from order and compare

		// TODO check for class cast exception
		Profile profile = (Profile) billingAddrForm.getProfile();
		BBBAddressAPI bbbAddressApi = new BBBAddressAPIImpl();

		BBBAddress defaultProfileBillingAddress = bbbAddressApi.getDefaultBillingAddress(profile, "1");

		assertEquals(defaultProfileBillingAddress.getFirstName(), mBillingAddress.getFirstName());
		assertEquals(defaultProfileBillingAddress.getLastName(), mBillingAddress.getLastName());
		assertEquals(defaultProfileBillingAddress.getEmail(), mBillingAddress.getEmail());
		assertEquals(defaultProfileBillingAddress.getAddress1(), mBillingAddress.getAddress1());
		assertEquals(defaultProfileBillingAddress.getAddress2(), mBillingAddress.getAddress2());
		assertEquals(defaultProfileBillingAddress.getCity(), mBillingAddress.getCity());
	}*/
}
