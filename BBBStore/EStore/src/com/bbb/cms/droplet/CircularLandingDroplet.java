package com.bbb.cms.droplet;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.cms.LandingTemplateVO;
import com.bbb.cms.manager.LandingTemplateManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;

/**
 * CircularLandingDroplet retrieves the data from CircularLanding
 * 
 * @author rsaini
 * 
 */
public class CircularLandingDroplet extends BBBDynamoServlet {

	private LandingTemplateManager mLandingTemplateManager = null;

	/**
	 * @return the landingTemplateManager
	 */
	public LandingTemplateManager getLandingTemplateManager() {
		return mLandingTemplateManager;
	}

	/**
	 * @param pLandingTemplateManager
	 *            the landingTemplateManager to set
	 */
	public void setLandingTemplateManager(LandingTemplateManager pLandingTemplateManager) {
		mLandingTemplateManager = pLandingTemplateManager;
	}

	/**
	 * This method gets the value of site id from the page and fetches the
	 * states greater than the current date and site id.
	 */
	public void service(final DynamoHttpServletRequest request,
			final DynamoHttpServletResponse response)
			throws javax.servlet.ServletException, java.io.IOException {

		logDebug("starting method CircularLandingDroplet");

		String pageName = null;
		String categoryId = BBBCmsConstants.NOT_REQUIRED;
		String siteId = null;

		if (request.getLocalParameter(BBBCmsConstants.PAGENAME) != null) {
			pageName = (String) request.getLocalParameter(BBBCmsConstants.PAGENAME);
		}

		logDebug("Input Paramters : " + pageName);

		if (request.getLocalParameter(BBBCmsConstants.SITE_ID) != null) {
			siteId = (String) request.getLocalParameter(BBBCmsConstants.SITE_ID);
		} else {
			siteId = getCurrentSiteId();
		}

		if (StringUtils.isEmpty(pageName)) {
			request.serviceParameter(BBBCmsConstants.EMPTY, request, response);
		} else {
			logDebug("Calling LandingTemplateManager : ");
			LandingTemplateVO landingTemplateVO = getLandingTemplateManager().getLandingTemplateData(pageName, categoryId, siteId);
			if (landingTemplateVO != null) {
				logDebug("Received LandingTemplateVO : " + landingTemplateVO);
				request.setParameter(BBBCmsConstants.LANDING_TEMPLATE_VO, landingTemplateVO);
				request.serviceParameter(BBBCmsConstants.OUTPUT, request, response);
			} else {
				logDebug("Received LandingTemplateVO as Null: ");
				request.serviceParameter(BBBCmsConstants.EMPTY, request, response);
			}
		}

		logDebug("Exiting method CircularLandingDroplet");

	}

	protected String getCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}
}