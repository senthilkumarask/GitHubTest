package com.bbb.cms.droplet;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.cms.HomePageTemplateVO;
import com.bbb.cms.manager.HomePageTemplateManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;

/**
 * 
 * @author asha60
 * 
 */
public class HeroImageIntlDroplet extends BBBDynamoServlet {

	private HomePageTemplateManager mHomePageTemplateManager = null;
	// Defining the Constants
	static final String HOME_TEMP_VO = "homePageTemplateVO";

	/**
	 * This method gets the HomePageTemplateVO from HomePageTemplateManager
	 * 
	 * @param request 
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void service(final DynamoHttpServletRequest request,
			final DynamoHttpServletResponse response)
			throws javax.servlet.ServletException, java.io.IOException {

		final String className = "HeroImageIntlDroplet";
		this.logInfo("Entering in service method of " + className);
		BBBPerformanceMonitor.start(className);
		String siteId = null;
		try {
			if (null != request.getLocalParameter(BBBCmsConstants.SITE_ID)) {
				siteId = (String) request.getLocalParameter(BBBCmsConstants.SITE_ID);
			}
			final HomePageTemplateVO homePageTemplateVO = getHomePageTemplateManager().
					getHeroImagesForIntlCustomer(siteId);

			if (null != homePageTemplateVO) {

				logDebug("Received HomePageTemplateVO : "+ homePageTemplateVO.toString());
				request.setParameter(HOME_TEMP_VO, homePageTemplateVO);
				request.serviceParameter(BBBCoreConstants.OUTPUT, request, response);
			} else {
				logDebug("Received HomePageTemplateVO as Null: ");
				request.serviceParameter(BBBCoreConstants.EMPTY, request, response);
			}
		} catch (BBBBusinessException be) {
					logError(LogMessageFormatter.formatMessage(request,
						"HeroImageIntlDroplet|service()|BBBBusinessException","catalog_1042"), be);
					request.serviceParameter(BBBCoreConstants.ERROR_OPARAM, request,response);
		} catch (BBBSystemException bs) {
						logError(LogMessageFormatter.formatMessage(request,
						"HeroImageIntlDroplet|service()|BBBSystemException","catalog_1043"), bs);
			request.serviceParameter(BBBCoreConstants.ERROR_OPARAM, request, response);
		}

		this.logInfo("Exiting  service method of " + className);
		BBBPerformanceMonitor.end(className);

	}

	/**
	 * @return the homePageTemplateManager
	 */
	public HomePageTemplateManager getHomePageTemplateManager() {
		return mHomePageTemplateManager;
	}

	/**
	 * @param pHomePageTemplateManager
	 *            the homePageTemplateManager to set
	 */
	public void setHomePageTemplateManager(
			HomePageTemplateManager pHomePageTemplateManager) {
		mHomePageTemplateManager = pHomePageTemplateManager;
	}

}
