/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  InvalidateDropletCacheScheduler.java
 *
 *  DESCRIPTION: Invalidate Droplet Cache
 *
 *  @author rsain4
 *
 *  HISTORY:
 *
 *  25/08/13 Initial version
 *
 */
package com.bbb.common.scheduler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.servlet.pipeline.HeadPipelineServlet;

import com.bbb.cache.listener.BBBInvalidateCacheListener;
import com.bbb.commerce.browse.droplet.CategoryLandingDroplet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBSearchConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBCacheInvalidatorSource;
import com.bbb.framework.cache.CoherenceCacheContainer;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.result.FacetParentVO;
import com.bbb.search.bean.result.FacetRefinementVO;
import com.bbb.search.bean.result.SearchResults;
import com.bbb.search.droplet.SearchDroplet;
import com.bbb.search.integration.SearchManager;
import com.bbb.utils.BBBConfigRepoUtils;

public class InvalidateDropletCacheScheduler extends
		SingletonSchedulableService {

	private boolean enabled;
	private BBBCacheInvalidatorSource cachaInvalidatorMessageSource;
	private BBBInvalidateCacheListener coherenceCacheListener;
	private SearchManager searchManager;
	private Map<String,String> siteMap;
	private CategoryLandingDroplet categoryLandingDroplet;
	private SiteContextManager siteContextManager;
	private SearchDroplet searchDroplet;
	private CoherenceCacheContainer coherenceCacheContainer;
	private String dynamoHandler = "/atg/dynamo/servlet/dafpipeline/DynamoHandler";
	private String tbsDataCenter;
	private String dcPrefix;

	/**
	 * @return the dcPrefix
	 */
	public String getDcPrefix() {
		return dcPrefix;
	}

	/**
	 * @param dcPrefix the dcPrefix to set
	 */
	public void setDcPrefix(String dcPrefix) {
		this.dcPrefix = dcPrefix;
	}
	/**
	 * @return the coherenceCacheContainer
	 */
	public final CoherenceCacheContainer getCoherenceCacheContainer() {
		return this.coherenceCacheContainer;
	}

	/**
	 * @param coherenceCacheContainer the coherenceCacheContainer to set
	 */
	public final void setCoherenceCacheContainer(final
			CoherenceCacheContainer coherenceCacheContainer) {
		this.coherenceCacheContainer = coherenceCacheContainer;
	}

	/**
	 * @return the siteContextManager
	 */
	public final SiteContextManager getSiteContextManager() {
		return this.siteContextManager;
	}

	/**
	 * @param siteContextManager the siteContextManager to set
	 */
	public final void setSiteContextManager(final SiteContextManager siteContextManager) {
		this.siteContextManager = siteContextManager;
	}

	/**
	 * @return the categoryLandingDroplet
	 */
	public final CategoryLandingDroplet getCategoryLandingDroplet() {
		return this.categoryLandingDroplet;
	}

	/**
	 * @param categoryLandingDroplet the categoryLandingDroplet to set
	 */
	public final void setCategoryLandingDroplet(final
			CategoryLandingDroplet categoryLandingDroplet) {
		this.categoryLandingDroplet = categoryLandingDroplet;
	}

	/**
	 * @return the searchDroplet
	 */
	public final SearchDroplet getSearchDroplet() {
		return this.searchDroplet;
	}

	/**
	 * @param searchDroplet the searchDroplet to set
	 */
	public final void setSearchDroplet(final SearchDroplet searchDroplet) {
		this.searchDroplet = searchDroplet;
	}

	/**
	 * @return the isEnabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param isEnabled
	 *            the isEnabled to set
	 */
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the cachaInvalidatorMessageSource
	 */
	public BBBCacheInvalidatorSource getCachaInvalidatorMessageSource() {
		return cachaInvalidatorMessageSource;
	}

	/**
	 * @param cachaInvalidatorMessageSource
	 *            the cachaInvalidatorMessageSource to set
	 */
	public void setCachaInvalidatorMessageSource(
			BBBCacheInvalidatorSource cachaInvalidatorMessageSource) {
		this.cachaInvalidatorMessageSource = cachaInvalidatorMessageSource;
	}

	/**
	 * @return the coherenceCacheListener
	 */
	public BBBInvalidateCacheListener getCoherenceCacheListener() {
		return coherenceCacheListener;
	}

	/**
	 * @param coherenceCacheListener
	 *            the coherenceCacheListener to set
	 */
	public void setCoherenceCacheListener(
			BBBInvalidateCacheListener coherenceCacheListener) {
		this.coherenceCacheListener = coherenceCacheListener;
	}

	/**
	 * @return the searchManager
	 */
	public SearchManager getSearchManager() {
		return searchManager;
	}

	/**
	 * @param searchManager
	 *            the searchManager to set
	 */
	public void setSearchManager(SearchManager searchManager) {
		this.searchManager = searchManager;
	}

	/**
	 * @return the siteMap
	 */
	public Map<String, String> getSiteMap() {
		return siteMap;
	}

	/**
	 * @param siteMap the siteMap to set
	 */
	public void setSiteMap(Map<String, String> siteMap) {
		this.siteMap = siteMap;
	}

	@Override
	public void doScheduledTask(final Scheduler scheduler,
			final ScheduledJob job) {

		this.doScheduledTask();

	}

	public void doScheduledTask() {

		if (isLoggingInfo()) {
			logInfo("Start:Scheduler started to perform task with job name=["
					+ getJobName() + "]");

		}

		doClearDropletCaches();

		if (isLoggingInfo()) {
			logInfo("End:Scheduler started to perform task with job name=["
					+ getJobName() + "]");

		}
	}

	public void doClearDropletCaches() {
		if (isEnabled()) {
			if (isLoggingDebug()) {
				logDebug("InvalidateDropletCacheScheduler Is enabled");
				long startTime = System.currentTimeMillis();
				logDebug("InvalidateDropletCacheScheduler start at  :"
						+ startTime);
			}

			if (isLoggingInfo()) {
				logInfo("Clear the header flyout coherence cache : Start");
			}

			getCoherenceCacheListener().invalidateHeaderFlyoutCache();

			if (isLoggingInfo()) {
				logInfo("Clear the header flyout coherence cache : End");
			}

			if (isLoggingInfo()) {
				logInfo("Call the Endeca for all three concepts and build the Flyout cache : Start");
			}

			HashMap<String, String> pCatalogRef = new HashMap<String, String>();
			SearchQuery pSearchQuery = new SearchQuery();
			for (Map.Entry<String, String> entry : siteMap.entrySet()) {

				String currentSiteId = entry.getKey();
				String rootCategory = entry.getValue();
				pCatalogRef.put("catalogId", "0");
				pCatalogRef.put("root", rootCategory);
				pSearchQuery.setSiteId(currentSiteId);
				pSearchQuery.setCatalogRef(pCatalogRef);
				pSearchQuery.setHeaderSearch(false);

				try {
					if (isLoggingInfo()) {
						logInfo("Calling getCategoryTree for Root Category "
								+ rootCategory + " for SiteId " + currentSiteId);
					}
					getSearchManager().getCategoryTree(pSearchQuery);
				} catch (BBBBusinessException e) {
					logError("BBBBusinessException in getCategoryTree", e);
				} catch (BBBSystemException e) {
					logError("BBBSystemException in getCategoryTree", e);
				}
			}

			if (isLoggingInfo()) {
				logInfo("Call the Endeca for all three concepts and build the Flyout cache : End");
			}

			//build the L1 category cache
			this.doReInstateL1CategoryCache();

			if (getTbsDataCenter().equalsIgnoreCase(getDcPrefix())) {
				logDebug("Inside InvalidateDropletCacheScheduler Fire Droplet Cache Invalidation Message For TBS");
				getCachaInvalidatorMessageSource().fireTBSNavDropletCacheInvMessage();
			} else {
				logDebug("Inside InvalidateDropletCacheScheduler Fire Droplet Cache Invalidation Message For STORE");
				getCachaInvalidatorMessageSource().fireNavDropletCacheInvalidationMessage();
			}

			//clear Mobile CMS Nav Cache
			doClearMobileCMSNavCaches();


			// Invalidate Popular Keywords by Department Cache
			doClearPopularKeywordCache();

			if (isLoggingDebug()) {
				long endTime = System.currentTimeMillis();
				logDebug("InvalidateDropletCacheScheduler end at  :" + endTime);
			}

		} else {
			if (isLoggingDebug()) {
				logDebug("InvalidateDropletCacheScheduler Is disabled");
			}
		}

	}

	/**
	 * Method used to rebuild the L1 category cache.
	 * For each site there is a call for Perform Search from Search Droplet Service with isHeader= true
	 * and then for each L1 category, categoryLandingDroplet is called which calls the getCategoryTree of EndecaSearch to rebuild L1 Category Cache.
	 */
	private void doReInstateL1CategoryCache() {
		if(isLoggingInfo()) {
			logInfo("Start: InvalidateDropletCacheScheduler.doReInstateL1CategoryCache");
		}

		String currentSiteId = null;
		String cacheName = null;
		cacheName = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY , BBBCoreConstants.HEADER_FLYOUT_CACHE_NAME);

		Iterator initalIt = this.getCoherenceCacheContainer().getAllKeys(cacheName);
		int initalItSize = 0;
		if(null != initalIt) {
			while(initalIt.hasNext()) {
				String cachedKeys = (String)initalIt.next();
				if(isLoggingDebug()) {
					logDebug("Inside : InvalidateDropletCacheScheduler.doReInstateL1CategoryCache : Cached Keys : " + cachedKeys);
				}
				initalItSize++;
			}
		}
		if (isLoggingInfo()) {
			logInfo("Inside : InvalidateDropletCacheScheduler.doReInstateL1CategoryCache : Initial Cache Size before building L1 Cache :" + initalItSize);
		}
		for (Map.Entry<String, String> entry : siteMap.entrySet()) {
			currentSiteId = entry.getKey();
			DynamoHttpServletRequest request = (DynamoHttpServletRequest)(((HeadPipelineServlet) Nucleus.getGlobalNucleus().resolveName(dynamoHandler)).getRequest(null));
			DynamoHttpServletResponse response = request.getResponse();
			ServletUtil.setCurrentRequest(request);
			ServletUtil.setCurrentResponse(response);
			request.setParameter(BBBCoreConstants.SITE_ID, currentSiteId);
			request.setParameter(BBBSearchConstants.CATALOGID, 0);
            request.setParameter(BBBSearchConstants.CATALOG_REF_ID, 10000);
            request.setParameter(BBBSearchConstants.IS_HEADER, BBBCoreConstants.YES_CHAR);
            final long startTime = System.currentTimeMillis();
            if (isLoggingInfo()) {
            	logInfo("Start Time to ReInstateL1CategoryCache() for Site : " + currentSiteId + " is : " + startTime);
            }
			try {
				this.getSiteContextManager().pushSiteContext(this.getSiteContextManager().getSiteContext(currentSiteId));
				this.getSearchDroplet().service(request, response);

	            final SearchResults browseSearchVO = (SearchResults)request.getObjectParameter("browseSearchVO");
	            if(null != browseSearchVO) {
				List<FacetParentVO> facetList = browseSearchVO.getFacets();
					for (FacetParentVO facet:facetList){
						if(null != facet && null != facet.getName() && facet.getName().equals(BBBSearchConstants.DEPARTMENT)) {
						List<FacetRefinementVO> facetRefinementVOs = facet.getFacetRefinement();
							for (FacetRefinementVO facetRefinement:facetRefinementVOs) {
								if(null != facetRefinement) {
									if(isLoggingDebug()) {
										logDebug("Build Cache from CategoryLandingDroplet() call for Category : " + facetRefinement.getCatalogId());
									}
									request.setParameter(BBBCoreConstants.ID, facetRefinement.getCatalogId());
									this.getCategoryLandingDroplet().service(request, response);
								}
							}
						}
					}
	            }
			} catch (ServletException e) {
				logError("ServletException in doReInstateL1CategoryCache", e);
			} catch (IOException e) {
				logError("IOException in doReInstateL1CategoryCache", e);
			} catch (SiteContextException e) {
				logError("SiteContextException in doReInstateL1CategoryCache", e);
			} finally {
				final long endTime = System.currentTimeMillis();
				if (isLoggingInfo()) {
					logInfo("Total Time to ReInstateL1CategoryCache() for Site : " + currentSiteId + " is : " +(endTime-startTime)/1000.0);
				}
			}
		}
		Iterator finalIt = this.getCoherenceCacheContainer().getAllKeys(cacheName);
		int finalItSize = 0;
		if(null != finalIt){
			while(finalIt.hasNext()) {
					String cachedKeys = (String) finalIt.next();
					if(isLoggingDebug()) {
						logDebug("Inside : InvalidateDropletCacheScheduler.doReInstateL1CategoryCache : Cached Keys : " +cachedKeys );
				}
				finalItSize++;
			}
		}
		if (isLoggingInfo()) {
			logInfo("Inside : InvalidateDropletCacheScheduler.doReInstateL1CategoryCache : Final Cache Size after building L1 Cache :" + finalItSize);
			logInfo("End: InvalidateDropletCacheScheduler.doReInstateL1CategoryCache");
		}
	}

	/**
	 * Clear mobile CMS nav flyout cache
	 * This method is not populating the mobile CMS nav cache which will be
	 */
	public void doClearMobileCMSNavCaches() {

		if (isLoggingInfo()) {
			logInfo("doClearMobileCMSNavCaches: clear the mobile CMS flyout coherence cache : Start");
		}

		if (isLoggingDebug()) {
			long startTime = System.currentTimeMillis();
			logDebug("InvalidateDropletCacheScheduler start at  :"	+ startTime);
		}

		//Invalidating Mobile CMS Nav Cache
		getCoherenceCacheListener().invalidateMobileCMSNavCache();

		if (isLoggingDebug()) {
			long endTime = System.currentTimeMillis();
			logDebug("InvalidateDropletCacheScheduler end at  :" + endTime);
		}

		if (isLoggingInfo()) {
			logInfo("Clear the mobile CMS flyout coherence cache : End");
		}

	}

	/**
	 * Clear Popular Keywords coherence cache
	 * This method is not populating Popular Keywords coherence cache
	 */
	public void doClearPopularKeywordCache() {

		if (isLoggingInfo()) {
			logInfo("doClearPopularKeywordCache: clear the Popular Keywords coherence cache : Start");
		}

		if (isLoggingDebug()) {
			long startTime = System.currentTimeMillis();
			logDebug("InvalidateDropletCacheScheduler start at  :"	+ startTime);
		}

		//Invalidating Popular Keywords coherence cache
		getCoherenceCacheListener().invalidatePopularKeywordsCache();

		if (isLoggingDebug()) {
			long endTime = System.currentTimeMillis();
			logDebug("InvalidateDropletCacheScheduler end at  :" + endTime);
		}

		if (isLoggingInfo()) {
			logInfo("clear the Popular Keywords coherence cache : End");
		}

	}

	/**
	 * @return the tbsDataCenter
	 */
	public String getTbsDataCenter() {
		return tbsDataCenter;
	}

	/**
	 * @param tbsDataCenter the tbsDataCenter to set
	 */
	public void setTbsDataCenter(String tbsDataCenter) {
		this.tbsDataCenter = tbsDataCenter;
	}

}
