package com.bbb.commerce.giftregistry;

import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;

import com.bbb.commerce.giftregistry.droplet.SelectHeaderDroplet;
import com.sapient.common.tests.BaseTestCase;

public class TestSelectHeaderDroplet extends BaseTestCase {

	/** Test case 1 where view is owner and user is loggedin */
	public void testSelectHeader1() throws Exception {
		
		SelectHeaderDroplet selectHeaderDroplet = (SelectHeaderDroplet)getObject("selectHeaderDroplet");
		
		Profile resultProfile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		
		ProfileTools profileTool = (ProfileTools) getRequest().resolveName("/atg/userprofiling/ProfileTools");
	    resultProfile.setDataSource(profileTool.getItemFromEmail("coregtest@example.com"));
	    getRequest().setParameter("profile", resultProfile);
	    getRequest().setRequestURI("/store/giftregistry/view_registry_owner.jsp");
		String siteId = (String) getObject("siteId");

		getRequest().setParameter("siteId",siteId);
		selectHeaderDroplet.service(getRequest(), getResponse());
		
		String headerStatus = (String)getRequest().getParameter("subheader");
		assertObject("showGenericHeader", headerStatus);
    
	}
	
	/** Test case 1 where view is registry feature page. and user is loggedin  */
	public void testSelectHeader2() throws Exception {
		
		SelectHeaderDroplet selectHeaderDroplet = (SelectHeaderDroplet)getObject("selectHeaderDroplet");
		
		Profile resultProfile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");

		
		ProfileTools profileTool = (ProfileTools) getRequest().resolveName("/atg/userprofiling/ProfileTools");
	    resultProfile.setDataSource(profileTool.getItemFromEmail("coregtest@example.com"));
	    getRequest().setParameter("profile", resultProfile);

	    getRequest().setRequestURI("/store/bbregistry/registry_features.jsp");
		String siteId = (String) getObject("siteId");

		getRequest().setParameter("siteId",siteId);
		selectHeaderDroplet.service(getRequest(), getResponse());
		
		String headerStatus = (String)getRequest().getParameter("subheader");
		assertObject("showGenericHeader", headerStatus);
    
	}
	
	
//	/** Test case 1 where view is product detail page and user is not logged in.*/
//	public void testSelectHeader3() throws Exception {
//		
//		SelectHeaderDroplet selectHeaderDroplet = (SelectHeaderDroplet)getObject("selectHeaderDroplet");
//		
//	    getRequest().setRequestURI("/store/browse/product_details.jsp");
//		String siteId = (String) getObject("siteId");
//
//		getRequest().setParameter("siteId",siteId);
//		selectHeaderDroplet.service(getRequest(), getResponse());
//		
//		String headerStatus = (String)getRequest().getParameter("subheader");
//		assertObject("noHeader", headerStatus);
//    
//	}
//	

}
