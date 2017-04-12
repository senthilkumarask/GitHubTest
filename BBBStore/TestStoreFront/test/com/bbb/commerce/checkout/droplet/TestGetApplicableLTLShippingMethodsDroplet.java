package  com.bbb.commerce.checkout.droplet;

import java.util.List;

import atg.nucleus.Nucleus;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.constants.BBBCoreConstants;
import com.sapient.common.tests.BaseTestCase;

/**
 * To test GetApplicableShippingMethodsDroplet's service method.
 * 
 * @author NAGA13
 * @version 1.0
 */

public class TestGetApplicableLTLShippingMethodsDroplet extends BaseTestCase {

	//private static final String DROPLET = "bbbGetApplicableLTLShippingMethodsDroplet";

	/**
	 * To test the perSKU flow of shipping method Droplet - multi shipping.
	 * 
	 * @throws Exception
	 */

	public void testServiceGetApplicableLTLShippingMethods() throws Exception {

		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		GetApplicableLTLShippingMethodsDroplet appLTLshippingMethodDroplet = (GetApplicableLTLShippingMethodsDroplet)Nucleus.getGlobalNucleus().resolveName("/com/bbb/commerce/checkout/droplet/GetApplicableLTLShippingMethodsDroplet");
				
		String skuID = (String)getObject(BBBCoreConstants.SKUID);
		String siteId = (String)getObject(BBBCoreConstants.SITE_ID);
		pRequest.setParameter(BBBCoreConstants.SKUID,skuID);
		pRequest.setParameter(BBBCoreConstants.SITE_ID,siteId);
		// Calling droplet service method
		appLTLshippingMethodDroplet.service(pRequest, pResponse);
		List<ShipMethodVO> shipMethodVOList =(List<ShipMethodVO>) pRequest.getObjectParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST);
		assertFalse(shipMethodVOList.isEmpty());
		
		
		
		
	}

}