package com.bbb.personalstore.droplet;

import java.util.Map;

import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.giftregistry.droplet.BBBPresentationDroplet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.personalstore.manager.PersonalStoreManager;
import com.bbb.personalstore.vo.StrategyResponseVO;

/**
 * This droplet retrives Strategy details based on the strategyId
 * 
 * @author ssard1
 * 
 */
public class PersonalStoreStrategyLookupDroplet extends BBBPresentationDroplet {

	private PersonalStoreManager mPsManager;
	private Map strategyNameMap;
	

	public Map getStrategyNameMap() {
		return strategyNameMap;
	}

	public void setStrategyNameMap(Map strategyNameMap) {
		this.strategyNameMap = strategyNameMap;
	}

	/**
	 * This method returns <code>PersonalStoreManager</code> contains name of
	 * the manager component to use and get the strategy details.
	 * 
	 * @return the mPsManager in <code>PersonalStoreManager</code> format
	 */
	public PersonalStoreManager getPsManager() {
		return mPsManager;
	}

	/**
	 * This method sets the PersonalStoreManager to be used from component
	 * properties file and get the strategy details
	 * 
	 * @param mPsManager
	 *            the personal store manager to set
	 */
	public void setPsManager(final PersonalStoreManager mPsManager) {
		this.mPsManager = mPsManager;
	}

	/**
	 * Droplet to retrieve the strategy details based on the StrategyId
	 * 
	 * @param pRequest
	 *            - DynamoHttpServletRequest
	 * @param pResponse
	 *            - DynamoHttpServletResponse
	 */
	/*
	 *  
	 * @see
	 * atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest,
	 * atg.servlet.DynamoHttpServletResponse)
	 */
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws javax.servlet.ServletException, java.io.IOException {
		BBBPerformanceMonitor.start(PersonalStoreStrategyLookupDroplet.class.getName() + " : " + "service");
		logDebug("Enter.PersonalStoreStrategyLookupDroplet.service(pRequest,pResponse)");
		if (null == getPsManager()) {
			pRequest.serviceParameter(OPARAM_EMPTY, pRequest, pResponse);
			} else {
			StrategyResponseVO strategyDetails = null;
			try {
				final String strategyId 		  = (String) pRequest.getLocalParameter(BBBCoreConstants.STRATEGYID);
				final String strategyContextCode  = (String) pRequest.getLocalParameter(BBBCoreConstants.STRATEGY_CODE);
				
				logDebug("PersonalStoreStrategyLookupDroplet.service : Calling the manager to get the strategy details in responseVO");
				strategyDetails = getPsManager().getPersonalStoreStrategyLookup(strategyId, strategyContextCode);
			} catch (RepositoryException e) {
				logError("Some RepositoryException occured while fetching strategy details for startegy Id : "+e);
				pRequest.serviceParameter(OPARAM_EMPTY, pRequest, pResponse);
			} catch (BBBSystemException e) {
				logError("Some BBBSystemException occured while fetching strategy details for startegy Id : "+e);
				pRequest.serviceParameter(OPARAM_EMPTY, pRequest, pResponse);
			}

			if (null == strategyDetails || (strategyDetails.getStrategyDetails()==null)) {
				pRequest.serviceParameter(OPARAM_EMPTY, pRequest, pResponse);
			} else {
				logDebug("PersonalStoreStrategyLookupDroplet.service : Set the StrategyResponseVO : " + strategyDetails);
				pRequest.setParameter(BBBCoreConstants.STRATEGY_NAME_MAP,getStrategyNameMap().get(strategyDetails.getStrategyDetails().getPropertyValue(BBBCoreConstants.STRATEGY_NAME)));
				pRequest.setParameter(BBBCoreConstants.ST_DETAILS, strategyDetails);
				pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
			}
		}
		
		logDebug("Exit.PersonalStoreStrategyLookupDroplet.service(pRequest,pResponse)");
		
		BBBPerformanceMonitor.end(PersonalStoreStrategyLookupDroplet.class.getName() + " : " + "service");
	}
}