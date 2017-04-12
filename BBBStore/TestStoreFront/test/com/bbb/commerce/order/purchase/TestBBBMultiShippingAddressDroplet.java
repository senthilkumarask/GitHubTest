/**
 * @author jpadhi
 * @version Id: //com.bbb.commerce.order.purchase/TestBBBMultiShippingAddressDroplet.java.TestBBBMultiShippingAddressDroplet $$
 * @updated $DateTime: Jan 7, 2012 6:24:40 PM
 */
package com.bbb.commerce.order.purchase;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import atg.core.util.Address;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.account.BBBProfileTools;
import com.bbb.account.api.BBBAddressAPI;
import com.bbb.account.api.BBBAddressVO;
import com.bbb.commerce.common.BBBAddress;
import com.bbb.commerce.common.BBBAddressContainer;
import com.bbb.commerce.order.BBBShippingGroupManager;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

/**
 * @author jpadhi
 *
 */
public class TestBBBMultiShippingAddressDroplet extends BaseTestCase {
	
	public void testService() throws Exception {
		DynamoHttpServletRequest pRequest =  getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		BBBMultiShippingAddressDroplet addrDroplet = (BBBMultiShippingAddressDroplet)getObject("bbbAddrDroplet");
		BBBAddressContainer addrContainer = (BBBAddressContainer)getObject("bbbAddContainer");
		BBBAddressAPI bbbAddrAPI = (BBBAddressAPI)getObject("bbbAddrAPI");
		BBBOrderTools orderTools = (BBBOrderTools)getObject("bbbOrderTools");
		BBBShippingGroupContainerService bbbSGCS = (BBBShippingGroupContainerService)getObject("bbbSGCS");
		BBBShippingGroupManager bbbSGMgr = (BBBShippingGroupManager)getObject("bbbSGMgr"); 
		Profile profile = (Profile) pRequest.resolveName("/atg/userprofiling/Profile");
		
		String siteId = (String)getObject("siteId");
		getRequest().setParameter("siteId", siteId);
		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		
		String orderType = (String)getObject("orderType");
		
		BBBOrder order = (BBBOrder)orderTools.createOrder(orderType);
				
		pRequest.setParameter("addressContainer", addrContainer);
		pRequest.setParameter(BBBCoreConstants.PROFILE, profile);
		pRequest.setParameter(BBBCoreConstants.ORDER, order);
		pRequest.setParameter(BBBCoreConstants.SITE_ID, siteId);
		
		addrDroplet.service(pRequest, pResponse);
		
		BBBProfileTools bbbProfileTools = (BBBProfileTools)getRequest().resolveName("/atg/userprofiling/ProfileTools");
		Iterator<String> addressNamesIterator = bbbProfileTools.getProfileAddressNames(profile).iterator();
		while(addressNamesIterator.hasNext()){
			bbbProfileTools.removeProfileRepositoryAddress(profile, addressNamesIterator.next(), true);
		}
		
		BBBAddressContainer newAddrContainer = (BBBAddressContainer)pRequest.getObjectParameter("addressContainer");
		newAddrContainer.getAddressMap().clear();
		Map<String, BBBAddress> addrMap = newAddrContainer.getAddressMap();
		
		assertEquals("There should not be any address in Map ", 0, addrMap.size());
		
		//Add new address to Order's Shipping group
		BBBAddressVO tempAddrVO = createNewAddr("addr1  ", "addr2", "fName   ", "lName  ", "45788", "Arizona", "CA");
		Address pAddress = (Address)tempAddrVO;
		String shippingMethod = "1a";
		BBBHardGoodShippingGroup hgSG = bbbSGMgr.createHardGoodShippingGroup(order, pAddress, shippingMethod);
		String name = ""+new Date().getTime();
		bbbSGCS.addShippingGroup(name, hgSG);
		((BBBHardGoodShippingGroup)order.getShippingGroup(hgSG.getId())).setSourceId(name);
		
		pRequest.setParameter("addressContainer", addrContainer);
		pRequest.setParameter(BBBCoreConstants.PROFILE, profile);
		pRequest.setParameter(BBBCoreConstants.ORDER, order);
		pRequest.setParameter(BBBCoreConstants.SITE_ID, siteId);
		
		addrDroplet.service(pRequest, pResponse);
		
		newAddrContainer = (BBBAddressContainer)pRequest.getObjectParameter("addressContainer");
		addrMap = newAddrContainer.getAddressMap();
		
		assertEquals("There should be 1 address in Map ", 1, addrMap.size());
		
		//Adding new Address in Profile
		
		bbbAddrAPI.addNewShippingAddress(profile, tempAddrVO, siteId);
		
		pRequest.setParameter("addressContainer", addrContainer);
		pRequest.setParameter(BBBCoreConstants.PROFILE, profile);
		pRequest.setParameter(BBBCoreConstants.ORDER, order);
		pRequest.setParameter(BBBCoreConstants.SITE_ID, siteId);
		
		addrDroplet.service(pRequest, pResponse);
		
		newAddrContainer = (BBBAddressContainer)pRequest.getObjectParameter("addressContainer");
		addrMap = newAddrContainer.getAddressMap();
		
		assertEquals("There should be 2 address in Map ", 2, addrMap.size());
			
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public BBBAddressVO createNewAddr(String addr1, String addr2, String fName, 
			String lName, String pPostalCode, String pCity, String pState) {
		BBBAddressVO addressVO = new BBBAddressVO();
		addressVO.setAddress1(addr1);
		addressVO.setAddress2(addr2);
		addressVO.setFirstName(fName);
		addressVO.setLastName(lName);


		addressVO.setCountry("US");

		

		addressVO.setPostalCode(pPostalCode);
		addressVO.setCity(pCity);
		addressVO.setState(pState);

		return addressVO;
	}
}
