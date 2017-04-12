package com.bbb.personalstore.tools;

import java.util.HashMap;

import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.userprofiling.Profile;

import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;

/**
 * This is the tools class for personal Store, which contains the methods to
 * retrieve the strategy details from Personal Store repository.
 * 
 * @author rjain40
 * 
 */

public class PersonalStoreTools extends BBBGenericService {

	private Repository mPSRepository;
	private HashMap<String, String> mPSTargetMapping ;
	private HashMap<String, String> mPSCookieMapping ;
	private HashMap<String, String> mPSTrendingConMapping ;
    /** 
     * @return the mPSTrendingConMapping 
     */ 
    public HashMap<String, String> getTrendingContextStrategyMap() { 
            return mPSTrendingConMapping; 
    }  

    /** 
     * @param mPSTrendingConMapping 
     *            the mPSTrendingConMapping to set 
     */ 
    public void setTrendingContextStrategyMap(HashMap<String, String> mPSTrendingConMapping) { 
            this.mPSTrendingConMapping = mPSTrendingConMapping; 
    }



	/**
	 * @return the mPSCookieMapping
	 */
	public HashMap<String, String> getPersonalStoreCookieMapping() {
		return mPSCookieMapping;
	}

	/**
	 * @param mPSCookieMapping
	 *            the mPSCookieMapping to set
	 */
	public void setPersonalStoreCookieMapping(HashMap<String, String> mPSCookieMapping) {
		this.mPSCookieMapping = mPSCookieMapping;
	}

	/**
	 * This method returns <code>HashMap</code> contains strategy Code as key
	 * and Slot component as value
	 * 
	 * @return the mPSSlotMapping in <code>HashMap<String, String></code> format
	 */
	public HashMap<String, String> getPersonalStoreTargetMapping() {
		return mPSTargetMapping;
	}

	/**
	 * This method sets strategy to slot mapping from component properties file
	 * 
	 * @param mPSSlotMapping
	 *            the StrategySlotMap to set
	 */
	public void setPersonalStoreTargetMapping(final HashMap<String, String> mPSTargetMapping) {
		this.mPSTargetMapping = mPSTargetMapping;
	}

	/**
	 * This method returns <code>Repository</code> contains the Personal Store
	 * Repository
	 * 
	 * @return the mPSRepository in <code>Repository</code> format
	 */
	public Repository getPersonalStoreRepository() {
		return mPSRepository;
	}

	/**
	 * This method sets the Personal Store Repository from component properties
	 * file
	 * 
	 * @param mPSRepository
	 *            the PersonalStoreRepository to set
	 */
	public void setPersonalStoreRepository(final Repository mPSRepository) {
		this.mPSRepository = mPSRepository;
	}

	/**
	 * This method is used to get the strategy details from Personal Store
	 * Repository applicable to the corresponding site.
	 * 
	 * @param profile
	 *            Profile Item for current profile
	 * @param pSiteId
	 *            Site Id item
	 * @return List of repository Items(Strategies) for the personal Store in
	 *         <code>List</code> format
	 * @throws None
	 */
	public RepositoryItem[] getStrategyDetails(final String pSiteId, final Profile profile) throws RepositoryException {

		BBBPerformanceMonitor.start(PersonalStoreTools.class.getName() + " :" + " getStrategyDetails");
		logDebug("Enter.PersonalStoreTools.getStrategyDetails(pSiteId,profile) with parmateres - SiteId :" + pSiteId + " profile :" + profile);
		RepositoryItem[] psDetails = null;
		try {
			final RepositoryView view = getPersonalStoreRepository().getView(BBBCoreConstants.PERSONAL_STORE);
			final QueryBuilder queryBuilder = view.getQueryBuilder();

			/* Create query for Site attribute */
			final QueryExpression siteProperty = queryBuilder.createPropertyQueryExpression(BBBCoreConstants.SITE_PROPERTY);
			final QueryExpression siteValue = queryBuilder.createConstantQueryExpression(pSiteId);
			final Query repoQuery = queryBuilder.createComparisonQuery(siteProperty, siteValue, QueryBuilder.EQUALS);

			// Executing the query to get all the personal Stores from Personal
			// Store Repository
			psDetails = view.executeQuery(repoQuery);
		} finally {
			logDebug("Exit.PersonalStoreTools.getStrategyDetails(pSiteId,profile) with returning Repository Items :: " + psDetails);
			BBBPerformanceMonitor.end(PersonalStoreTools.class.getName() + ": " + "getStrategyDetails");
		}
		return psDetails;
	}
	
	/**
	 * This method is used to get the strategy details from Personal Store
	 * Repository applicable to the corresponding site.
	 * 
	 * @param strategyId
	 *            strategyId of the selected Strategy
	 * @return Repository Item(Strategies) for desired Strategy
	 * @throws BBBSystemException 
	 */
	public RepositoryItem getStrategyDetailsLookUp(final String strategyId) throws RepositoryException, BBBSystemException {

		BBBPerformanceMonitor.start(PersonalStoreTools.class.getName() + " :" + " getStrategyDetailsLookUp");
		
		logDebug("Enter.PersonalStoreTools.getStrategyDetailsLookUp(strategyId) with parmateres - strategyId :" + strategyId);
		
		RepositoryItem[] psDetails = null;

		final RepositoryView view = getPersonalStoreRepository().getView(BBBCoreConstants.PS_STRATEGY);
		final QueryBuilder queryBuilder = view.getQueryBuilder();

		/* Create query for Site attribute */
		final QueryExpression startegyProperty = queryBuilder.createPropertyQueryExpression(BBBCoreConstants.STRATEGY_ID);
		final QueryExpression startegyValue = queryBuilder.createConstantQueryExpression(strategyId);
		final Query repoQuery = queryBuilder.createComparisonQuery(startegyProperty, startegyValue, QueryBuilder.EQUALS);

		// Executing the query to get the Strategy details based on the Strategy Id from Store repository
		try {
			psDetails = view.executeQuery(repoQuery);
		}catch (RepositoryException e) {
			throw new BBBSystemException("Repository Exception in getting strategy details:", e);
		} finally {
			
			logDebug("Exit.PersonalStoreTools.getStrategyDetailsLookUp(strategyId) with returning Repository Item :: " + psDetails);
			
			BBBPerformanceMonitor.end(PersonalStoreTools.class.getName() + ": " + "getStrategyDetailsLookUp");
		}
		return psDetails[0];
	}

}