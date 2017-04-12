package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;

import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.RegistrySkinnyVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

// TODO: Auto-generated Javadoc
/**
 * 
 * This ServletBean class provides list of user's future registies. based on
 * siteId.
 * 
 * This is used for AnnouncementCard use case.
 * 
 * @author ikhan2
 * 
 */
public class GiftRegistryAnnouncementCardDroplet extends BBBPresentationDroplet {

	/** Constants for string literal profile. */
	private static final String PROFILE = "profile";
	
	private static final String SITE_ID = "siteId";

	/** Constants for string literal registries. */
	private static final String REGISTRIES = "registries";

	/** Constants for string literal count. */
	private static final String COUNT = "count";

	/** Constants for string literal BuyBuyBaby. */
	private static final String BBBABY = "BuyBuyBaby";

	/** Reference varialbe for GiftRegistryManager. */
	private GiftRegistryManager giftRegistryManager;

	/** The lbl txt template manager. */
	private LblTxtTemplateManager lblTxtTemplateManager;

	/**
	 * Getter for giftRegistryManager.
	 * 
	 * @return giftRegistryManager
	 */
	public GiftRegistryManager getGiftRegistryManager() {
		return giftRegistryManager;
	}

	/**
	 * Setter for giftRegistryManager.
	 * 
	 * @param pGiftRegMgr
	 *            the new gift registry manager
	 */
	public void setGiftRegistryManager(final GiftRegistryManager pGiftRegMgr) {
		giftRegistryManager = pGiftRegMgr;
	}

	/**
	 * The method fetch and set the user's future registries based on siteID.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void service(final DynamoHttpServletRequest request,
			final DynamoHttpServletResponse response) throws ServletException,
			IOException {

		logDebug("GiftRegistryAnnouncementCardDroplet.service() method start");
		final String pSiteId = request.getParameter(SITE_ID);
		final Profile pProfile = (Profile) request.getObjectParameter(PROFILE);

		try {

			// do not display the card announcement page to transient user
			if (pProfile == null || pProfile.isTransient()) {

				logDebug("GiftRegistryAnnouncementCardDroplet.service()"
						+ " MSG=[profile is transient] " + " method ends");
				logError(LogMessageFormatter.formatMessage(request, "User profile transient Exception from service of GiftRegistryAnnouncementCardDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1014));
				request.setParameter(OUTPUT_ERROR_MSG, getLblTxtTemplateManager()
						.getErrMsg("err_annce_card_profile_transient",
								request.getLocale().getLanguage(), null, null));
				request.serviceLocalParameter(OPARAM_EMPTY, request,
						response);
				return;
			}

			List<RegistrySkinnyVO> registries = null;
			// giftRegistryManager.getFutureRegistryList( pProfile, pSiteId);

			if (pSiteId.equalsIgnoreCase(BBBABY)) {
				registries = getGiftRegistryManager().fetchUsersBabyRegistries(
						pProfile, pSiteId);
			} else {

				registries = getGiftRegistryManager().fetchUsersWeddingOrBabyRegistries(
						pProfile, pSiteId);
			}

			// do not display the card announcement page to user with 0
			// registries
			if (BBBUtility.isListEmpty(registries)) {
				logDebug("GiftRegistryAnnouncementCardDroplet.service()"
						+ " MSG=[User has no registry]" + " method ends");
				request.serviceLocalParameter(OPARAM_EMPTY, request,
						response);
				
				if (pSiteId.equalsIgnoreCase(BBBABY)) {
					request.setParameter(OUTPUT_ERROR_MSG, getLblTxtTemplateManager()
						.getErrMsg("err_annce_card_no_baby_registry",
								request.getLocale().getLanguage(), null, null));
				} else {
					logError(LogMessageFormatter.formatMessage(request, "err_annce_card_no_baby_wed_registry Exception from service of GiftRegistryAnnouncementCardDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1013));
					request.setParameter(OUTPUT_ERROR_MSG, getLblTxtTemplateManager()
						.getErrMsg("err_annce_card_no_baby_wed_registry",
								request.getLocale().getLanguage(), null, null));
				}
				
				return;
			}
			RegistryEventDateComparator eventDateComparator = new RegistryEventDateComparator();
			eventDateComparator.setSortOrder(2);
			Collections.sort(registries, eventDateComparator);
			logDebug("GiftRegistryAnnouncementCardDroplet.service()"
					+ "MSG=[User has " + registries.size() + " registries]"
					+ " method ends");
			request.setParameter(REGISTRIES, registries);
			request.setParameter(COUNT, Integer.valueOf(registries.size()));
			request.serviceLocalParameter(OPARAM_OUTPUT, request, response);

		} catch (RepositoryException rExcep) {
			logError("RepositoryException", rExcep);
			request.setParameter(OUTPUT_ERROR_MSG, rExcep.getMessage());
			request.serviceLocalParameter(OPARAM_ERROR, request, response);

		} catch (BBBBusinessException bExcep) {
			logError("BBBBusinessException", bExcep);
			request.setParameter(OUTPUT_ERROR_MSG, bExcep.getMessage());
			request.serviceLocalParameter(OPARAM_ERROR, request, response);

		} catch (BBBSystemException sysExcep) {
			logError("BBBSystemException", sysExcep);
			request.setParameter(OUTPUT_ERROR_MSG, sysExcep.getMessage());
			request.serviceLocalParameter(OPARAM_ERROR, request, response);
		}

	}

	/**
	 * Getter for lblTxtTemplateManager
	 * @return lblTxtTemplateManager
	 */
	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	/**
	 * Setter for lblTxtTemplateManager
	 * @param lblTxtTemplateManager
	 */
	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}

}
