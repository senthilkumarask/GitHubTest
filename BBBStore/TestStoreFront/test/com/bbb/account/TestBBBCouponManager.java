/**
 * @author akhaju
 * @version Id: //com.bbb.commerce.order/TestBBBOrderManager.java.TestBBBOrderManager $$
 * @updated $DateTime: Nov 18, 2011 12:46:29 PM
 */
package com.bbb.account;

import atg.nucleus.Nucleus;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.selfservice.droplet.ProcessCouponDroplet;
import com.sapient.common.tests.BaseTestCase;

/**
 * @author akhaju
 *
 */
public class TestBBBCouponManager   extends BaseTestCase{
	
	public void testProcessCoupon() throws Exception {		
		
		
		DynamoHttpServletRequest req = getRequest();
		ProcessCouponDroplet droplet = (ProcessCouponDroplet)Nucleus.getGlobalNucleus().resolveName("/com/bbb/selfservice/ProcessCouponDroplet");
		final BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) this.getObject("catalogTools");
		
		DynamoHttpServletResponse res = getResponse();
		String requestType = (String)getObject("requestType");
		String serviceName = (String)getObject("serviceName");	
		String entryCd = (String)getObject("entryCd");
		String siteFlag = (String)getObject("siteFlag");	
		String userToken = catalogTools.getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY,
				BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(
				BBBCoreConstants.ZERO);
		
		String siteId = (String)getObject("siteId");	
		
		String email = (String)getObject("emailAddr");
		req.setParameter("requestType",requestType );
		req.setParameter("serviceName",serviceName );
		req.setParameter("entryCd", entryCd);
		req.setParameter("siteId", siteId);
		req.setParameter("userToken",userToken );
		req.setParameter("emailAddr",email );
		req.setParameter("siteFlag",siteFlag );
		
		
		droplet.service(req, res);
		
		String obj = (String)req.getObjectParameter("cmsContent");		
		if(obj != null){
			String value = (String)req.getObjectParameter("couponStatus");
 			assertTrue(value.equalsIgnoreCase("Not Activated") || value.equalsIgnoreCase("Activated") || value.equalsIgnoreCase("Redeemed") || value.equalsIgnoreCase("Expired") ||value.equalsIgnoreCase("Pre Activated")); 			 			
		} else {
			obj = (String)req.getObjectParameter("cmsError");
			assertTrue(obj.equalsIgnoreCase("EmailError") || obj.equalsIgnoreCase("CouponActivateError") || obj.equalsIgnoreCase("error") || obj.equalsIgnoreCase("CouponUnknownError"));
		}
		req.getSession().setAttribute(BBBCoreConstants.COUPON_STATUS,BBBCoreConstants.NOT_ACTIVATED);
		droplet.service(req, res);
		req.getSession().setAttribute(BBBCoreConstants.COUPON_STATUS,BBBCoreConstants.ACTIVATED);
		droplet.service(req, res);
		droplet.getQuery();
		droplet.getLblTxtTemplate();
		droplet.getPromTools();
		droplet.getProperty();
		droplet.getTools();
		droplet.addErrorToReqObj(401, req);
		droplet.addErrorToReqObj(402, req);
		droplet.addErrorToReqObj(403, req);
		
		
	}
}
