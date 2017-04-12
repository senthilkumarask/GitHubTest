package com.bbb.social.facebook;

import java.util.Map;

import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.bbb.social.facebook.droplet.FBUserSiteAssocDroplet;
import com.bbb.social.facebook.vo.UserVO;
import com.sapient.common.tests.BaseTestCase;


/**
 * @author jsidhu
 *
 */
public class TestFBUserSiteAssocDroplet extends BaseTestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testService() throws Exception{
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		FBUserSiteAssocDroplet fbUserSiteAssoc = (FBUserSiteAssocDroplet) getObject("fbConnectDroplet");
	

		UserVO fbUserProfile = new UserVO();		
		fbUserProfile.setUserName((String)getObject("fbusername"));
		fbUserProfile.setFirstName((String)getObject("fbfname"));
		fbUserProfile.setEmail((String)getObject("fbemail"));
		fbUserProfile.setGender((String)getObject("gender"));
		fbUserProfile.setLastName((String)getObject("fblname"));
		fbUserProfile.setName((String)getObject("fbname"));
		fbUserProfile.setMiddleName((String)getObject("fbmname"));

		/*Set<SchoolVO> schools = new HashSet<SchoolVO>();
		SchoolVO school = new SchoolVO();

		school.setName("School Name");
		school.setSchoolID("School ID");

		schools.add(school);
		fbUserProfile.setSchools(schools);*/
		pRequest.setParameter("profileLookup",FBConstants.PROFILE_LOOKUP_BASED_ON_FBEMAILID );
		pRequest.setParameter("siteId",BBBSiteContext.getBBBSiteContext((String)getObject("siteId")).getSite().getId());
		pRequest.getSession().setAttribute(FBConstants.FB_BASIC_INFO, fbUserProfile);
		ServletUtil.setCurrentRequest(pRequest);
		fbUserSiteAssoc.service(pRequest, pResponse);
		String sisterSite = pRequest.getParameter("sisterSite");
		assertTrue(((String)getObject("sisterSite")).equals(sisterSite));		
		
	}
	
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
