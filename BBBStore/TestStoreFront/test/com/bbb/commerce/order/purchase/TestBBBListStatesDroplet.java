/**
 * @author jpadhi
 * @version Id: //com.bbb.commerce.order.purchase/TestBBBMultiShippingAddressDroplet.java.TestBBBMultiShippingAddressDroplet $$
 * @updated $DateTime: Jan 7, 2012 6:24:40 PM
 */
package com.bbb.commerce.order.purchase;

import java.util.List;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.constants.BBBCoreConstants;
import com.sapient.common.tests.BaseTestCase;

/**
 * @author jpadhi
 *
 */
public class TestBBBListStatesDroplet extends BaseTestCase {
	
	public void testService() throws Exception {
		DynamoHttpServletRequest pRequest =  getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		BBBListStatesDroplet listStatesDroplet = (BBBListStatesDroplet)getObject("bbbListStatesDroplet");		
		String siteId = (String)getObject("siteId");
		
		pRequest.setParameter(BBBCoreConstants.SITE_ID, siteId);
		
		listStatesDroplet.service(pRequest, pResponse);
		

		
		List<StateVO> stateList = (List<StateVO>)pRequest.getObjectParameter("states");
		
		
		assertNotNull("There should states for the site "+siteId, stateList.size());
			
	}

}
