/**
 * @author mamish
 * @version Id: //com.bbb.commerce.order/TestBBBOrderManager.java.TestBBBOrderManager $$
 * @updated $DateTime: Nov 18, 2011 12:46:29 PM
 */
package com.bbb.account.droplet;

import java.util.HashMap;
import java.util.Map;

import atg.nucleus.Nucleus;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.sapient.common.tests.BaseTestCase;

/**
 * @author akhaju
 * 
 */
public class TestChangeAncorTagURLDroplet extends BaseTestCase {

	public void testChangeAncorTagURLDroplet() throws Exception {

		DynamoHttpServletRequest req = getRequest();
		DynamoHttpServletResponse res = getResponse();
		BBBChangeAnchorTagUrlDroplet droplet = (BBBChangeAnchorTagUrlDroplet)Nucleus.getGlobalNucleus().resolveName("/com/bbb/account/droplet/BBBChangeAnchorTagUrlDroplet");
		String changedUrl = (String)getObject("changedUrl");
		String requestParamName = (String)getObject("requestParamName");
		String htmlString = "<a href=\"http://google.com\"></a>";
		req.setParameter("htmlString",htmlString );
		req.setParameter("requestParamName",requestParamName );
		req.setParameter("changedUrl",changedUrl );
		droplet.service(req, res);
		assertNotNull(req.getParameter("changedHtmlString"));
		
		BBBSetCookieDroplet cookiedroplet = (BBBSetCookieDroplet)getObject("cookieDroplet");
		Map map = new HashMap();
		map.put("xc", "ys");
		cookiedroplet.setCookies(map);
		req.setParameter("htmlString",htmlString );
		cookiedroplet.service(req, res);
		assertNotNull(req.getCookies());
	}
	
}
