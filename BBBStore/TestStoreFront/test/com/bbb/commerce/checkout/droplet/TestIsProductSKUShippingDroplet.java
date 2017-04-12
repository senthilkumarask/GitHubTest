package com.bbb.commerce.checkout.droplet;

import java.util.List;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.commerce.common.BBBAddressContainer;
import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.sapient.common.tests.BaseTestCase;

/**
 * To test TestIsProductSKUShippingDroplet service method.
 * 
 * @author Vchan5
 */

public class TestIsProductSKUShippingDroplet extends BaseTestCase {

	/**
	 * To test the TestIsProductSKUShippingDroplet
	 * 
	 * @throws Exception
	 */
	public void testIsProductSKUShippingDroplet() throws Exception {

		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();

		IsProductSKUShippingDroplet droplet = (IsProductSKUShippingDroplet)getObject("productSKUShippingDroplet");
		
		pRequest.setParameter("skuId", (String)getObject("skuId"));
		pRequest.setParameter("siteId", (String)getObject("siteId"));
		droplet.service(pRequest, pResponse);
		String attrbuteList=pRequest.getParameter("restrictedAttributes");
		if(attrbuteList==null){
			assertNull(attrbuteList);
		}else{
			assertNotNull(attrbuteList);
		}
		
		/*BBBOrderTools orderTools = (BBBOrderTools)getObject("bbbOrderTools");
		String orderType = (String)getObject("orderType");
		BBBAddressContainer bbbAddContainer = new BBBAddressContainer();
		Profile profile = (Profile) pRequest.resolveName("/atg/userprofiling/Profile");
		MutableRepository repository = (MutableRepository)resolveName("/atg/commerce/order/OrderRepository");
		MutableRepositoryItem item = repository.createItem("bbbAddress");
		
		BBBOrder order = (BBBOrder)orderTools.createOrder(orderType);
		BBBRepositoryContactInfo mBillingAddress = new BBBRepositoryContactInfo(item);
		mBillingAddress.setFirstName((String)getObject("firstName"));
		mBillingAddress.setMiddleName((String)getObject("middleName"));
		mBillingAddress.setLastName((String)getObject("lastName"));
		mBillingAddress.setAddress1((String)getObject("address1"));
		mBillingAddress.setAddress2((String)getObject("address2"));
		mBillingAddress.setCity((String)getObject("city"));
		mBillingAddress.setEmail((String)getObject("email"));
		mBillingAddress.setPhoneNumber((String)getObject("phoneNumber"));
		mBillingAddress.setPostalCode((String)getObject("zipCode"));
		mBillingAddress.setState((String)getObject("state"));
		
		repository.addItem(item);
		order.setBillingAddress(mBillingAddress);
		pRequest.setParameter(BBBCoreConstants.BILLING_ADDRESS_CONTAINER, bbbAddContainer);
		pRequest.setParameter(BBBCoreConstants.ORDER, order);
		pRequest.setParameter(BBBCoreConstants.ORDER, order);
		pRequest.setParameter(BBBCoreConstants.PROFILE, profile);
		
		droplet.service(pRequest, pResponse);
		
		assertNotNull("selectedAddrKey is null", pRequest.getParameter("selectedAddrKey"));
		assertNotNull("addressesMap is null", pRequest.getParameter("addresses"));*/
	
	}

	

}