package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import javax.servlet.ServletException;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.framework.performance.BBBPerformanceMonitor;


/**
 * This droplet Fetch Registry Info from registry Id and display registry
 * summary info.
 *
 * @author skalr2
 *
 */
public class PrefStoreInPilotStoresValidationDroplet extends BBBPresentationDroplet {

	/** The Constant Store Id. */
	private static final String PREFERED_STORE_ID = "prefredStoreId";
	private static final String REGISTRY_TYPE_CD = "registryTypeCode";

	private SiteContextManager siteContextManager = null;
	private GiftRegistryManager giftRegistryManager = null;

	/**
	 * Validates whether passed storeId is PilotStore or not.
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

	@Override
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		
		BBBPerformanceMonitor.start("PrefStoreInPilotStoresValidationDroplet", "GetRegistryInfo");
		String storeId = pRequest.getParameter(PREFERED_STORE_ID);
		String registryType = pRequest.getParameter(REGISTRY_TYPE_CD);
		boolean isUserAllowedToScheduleAppointment = getGiftRegistryManager().canScheuleAppointment(storeId, registryType, getSiteContextManager(). getCurrentSiteId());
		pRequest.setParameter("isUserAllowedToScheduleAStoreAppointment", isUserAllowedToScheduleAppointment);
		pRequest.serviceParameter(OPARAM_OUTPUT, pRequest, pResponse);
		this.logDebug(" PrefStoreInPilotStoresValidationDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - end");
		BBBPerformanceMonitor.end("PrefStoreInPilotStoresValidationDroplet", "PilotStoreValidation");

	}

	public SiteContextManager getSiteContextManager() {
		return siteContextManager;
	}


	public void setSiteContextManager(SiteContextManager siteContextManager) {
		this.siteContextManager = siteContextManager;
	}


	public GiftRegistryManager getGiftRegistryManager() {
		return giftRegistryManager;
	}


	public void setGiftRegistryManager(GiftRegistryManager giftRegistryManager) {
		this.giftRegistryManager = giftRegistryManager;
	}
	
}
