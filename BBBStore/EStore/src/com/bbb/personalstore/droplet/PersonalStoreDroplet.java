package com.bbb.personalstore.droplet;

import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.commerce.giftregistry.droplet.BBBPresentationDroplet;
import com.bbb.constants.BBBAccountConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.personalstore.manager.PersonalStoreManager;
import com.bbb.personalstore.vo.PersonalStoreResponseVO;
import com.bbb.utils.BBBUtility;

/**
 * This droplet retrives personal store details as per given site id and
 * profile.
 * 
 * @author rjain40
 * 
 */
public class PersonalStoreDroplet extends BBBPresentationDroplet {

	private PersonalStoreManager mPsManager;
	private PersonalStoreStrategyLookupDroplet strategyLookupDroplet;

	public PersonalStoreStrategyLookupDroplet getStrategyLookupDroplet() {
		return strategyLookupDroplet;
	}

	public void setStrategyLookupDroplet(
			PersonalStoreStrategyLookupDroplet strategyLookupDroplet) {
		this.strategyLookupDroplet = strategyLookupDroplet;
	}

	/**
	 * This method returns <code>PersonalStoreManager</code> contains name of
	 * the manager component to use and get the personal store and strategy
	 * details
	 * 
	 * @return the mPsManager in <code>PersonalStoreManager</code> format
	 */
	public PersonalStoreManager getPsManager() {
		return mPsManager;
	}

	/**
	 * This method sets the PersonalStoreManager to be used from component
	 * properties file and get the personal store and strategy details
	 * 
	 * @param mPsManager
	 *            the personal store manager to set
	 */
	public void setPsManager(final PersonalStoreManager mPsManager) {
		this.mPsManager = mPsManager;
	}

	/**
	 * Droplet to retrieve the strategy details as per siteId and profile.
	 * 
	 * @param pRequest
	 *            - DynamoHttpServletRequest
	 * @param pResponse
	 *            - DynamoHttpServletResponse
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest,
	 * atg.servlet.DynamoHttpServletResponse)
	 */
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws javax.servlet.ServletException, java.io.IOException {
		BBBPerformanceMonitor.start(PersonalStoreDroplet.class.getName() + " : " + "service");
		logDebug("Enter.PersonalStoreDroplet.service(pRequest,pResponse)");
		if (null == getPsManager()) {
			pRequest.serviceParameter(OPARAM_EMPTY, pRequest, pResponse);

		} else {
			PersonalStoreResponseVO psDetails = null;
			try {
				final String siteId = (String) pRequest.getLocalParameter(BBBCoreConstants.SITE_ID);
				final Profile profile = (Profile) pRequest.getObjectParameter(BBBCoreConstants.PS_USER_PROFILE);

				// Calling the manager to get the strategy details in response
				// VO
				logDebug("PersonalStoreDroplet.service : Calling the manager to get the strategy details in responseVO");
				psDetails = getPsManager().getPersonalStoreDetails(siteId, profile);
			} catch (RepositoryException e) {
				logError(e.getSourceException());
				pRequest.setParameter(BBBCoreConstants.Is_ERROR, true);
				pRequest.serviceParameter(BBBAccountConstants.OPARAM_ERROR, pRequest, pResponse);
			}

			if (null == psDetails || BBBUtility.isListEmpty(psDetails.getStrategyDetails())) {
				pRequest.serviceParameter(OPARAM_EMPTY, pRequest, pResponse);
			} else {
				logDebug("PersonalStoreDroplet.service : Set the PersonalStoreResponseVO : " + psDetails);
				pRequest.setParameter(BBBCoreConstants.PS_DETAILS, psDetails);
				pRequest.setParameter("strategyNameMap", getStrategyLookupDroplet().getStrategyNameMap());
				pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
			}

		}
		logDebug("Exit.PersonalStoreDroplet.service(pRequest,pResponse)");
		BBBPerformanceMonitor.end(PersonalStoreDroplet.class.getName() + " : " + "service");
	}
}