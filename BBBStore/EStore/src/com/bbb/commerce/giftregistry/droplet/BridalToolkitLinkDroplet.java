package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.BridalRegistryVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;

/**
 * The class is the extension of BBBPresentationDroplet which is again extension
 * of the ATG DynamoServlet. The class is responsible rendering the wedding and commitment ceremony registry to the user.
 * 
 * @author ssha53
 * 
 */
public class BridalToolkitLinkDroplet extends
		BBBPresentationDroplet {

	/** The Gift registry manager. */
	private GiftRegistryManager mGiftRegistryManager;

	private static final String SITE_ID = "siteId";

	/**
	 * Fetch Wedding and Commitment ceremony Registry Types for the dropdown to select a registry type and
	 * create a registry.
	 * 
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServletException
	 *             the servlet exception
	 */
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		BBBPerformanceMonitor.start("BridalToolkitGiftRegistryListDroplet", "service");
		logDebug(" BridalToolkitGiftRegistryListDroplet service(DynamoHttpServletRequest," +
				" DynamoHttpServletResponse) - start");
		Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
		List<BridalRegistryVO> bridalRegistryVOList = null;
		String siteId = pRequest.getParameter(SITE_ID);
		try {
			if (!profile.isTransient()) {
				bridalRegistryVOList = getGiftRegistryManager().getBridalRegistries(profile, siteId);
				pRequest.setParameter("bridalRegistryVOList", bridalRegistryVOList);
				pRequest.setParameter("size", bridalRegistryVOList.size());
			}
			logDebug("set BridalToolkitLinkDroplet  output to the display page "
					+ bridalRegistryVOList);
			pRequest.serviceLocalParameter("output", pRequest, pResponse);

		} catch (BBBSystemException e) {
			pRequest.setParameter(OUTPUT_ERROR_MSG, e.getMessage());
			logError(LogMessageFormatter.formatMessage(pRequest, "Business Exception from service of BridalToolkitLinkDroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1073),e);
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		} catch (BBBBusinessException e) {
			pRequest.setParameter(OUTPUT_ERROR_MSG, e.getMessage());
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
			logError(LogMessageFormatter.formatMessage(pRequest, "System Exception from service of BridalToolkitLinkDroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1074),e);
		} catch (RepositoryException e) {
			pRequest.setParameter(OUTPUT_ERROR_MSG, e.getMessage());
			logError(LogMessageFormatter.formatMessage(pRequest, "RepositoryException Exception from service of BridalToolkitLinkDroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1001),e);
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		}

		logDebug(" BridalToolkitLinkDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - ends");
		BBBPerformanceMonitor.end("BridalToolkitGiftRegistryListDroplet", "service");
	
	}

	/**
	 * @return the giftRegistryManager
	 */
	public GiftRegistryManager getGiftRegistryManager() {
		return mGiftRegistryManager;
	}

	/**
	 * @param giftRegistryManager the giftRegistryManager to set
	 */
	public void setGiftRegistryManager(GiftRegistryManager giftRegistryManager) {
		mGiftRegistryManager = giftRegistryManager;
	}

}
