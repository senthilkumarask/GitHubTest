/**
 * 
 */
package com.bbb.cache;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import atg.nucleus.ServiceException;
import atg.repository.MutableRepository;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;

import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.cache.BBBLocalCacheContainer;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.bbb.search.endeca.vo.AlgorithmComponentVO;
import com.bbb.search.endeca.vo.SearchBoostingAlgorithmVO;

/**
 * @author Sapient
 *
 */
public class BBBSearchAlgorithmDataLoader extends BBBGenericService {
	private BBBLocalCacheContainer localCacheContainer;
	private boolean enabled;
	private MutableRepository searchBoostRepository;
	private final String SEARCH_BOOST_ALGORITHM = "SearchBoostAlgorithm";
	private final String UNDERSCORE = "_";
	

	/**
	 * @return the localCacheContainer
	 */
	public BBBLocalCacheContainer getLocalCacheContainer() {
		return localCacheContainer;
	}

	/**
	 * @param localCacheContainer the localCacheContainer to set
	 */
	public void setLocalCacheContainer(BBBLocalCacheContainer localCacheContainer) {
		this.localCacheContainer = localCacheContainer;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the searchBoostRepository
	 */
	public MutableRepository getSearchBoostRepository() {
		return searchBoostRepository;
	}

	/**
	 * @param searchBoostRepository the searchBoostRepository to set
	 */
	public void setSearchBoostRepository(MutableRepository searchBoostRepository) {
		this.searchBoostRepository = searchBoostRepository;
	}

	@Override
	public void doStartService() throws ServiceException {
		logDebug("BBBSearchAlgorithmDataLoader | doStartService()");
		if (this.isEnabled()) {			
			loadSearchAlgorithmmDataInLocalMap();	
		} else {
			logDebug("BBBSearchAlgorithmDataLoader | doStartService | enabled flag is false hence SearchAlgorithmmData caching will not be done.");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void loadSearchAlgorithmmDataInLocalMap() {
		logDebug("BBBSearchAlgorithmDataLoader | Entering loadSearchAlgorithmmDataInLocalMap()");
		BBBPerformanceMonitor.start("BBBSearchAlgorithmDataLoader", "loadSearchAlgorithmmDataInLocalMap");
		
		RepositoryItem[] resultRepositoryItem = null;
		List<AlgorithmComponentVO> algorithmVoList = null;
		AlgorithmComponentVO algorithmComponentVO = null;
		Set<String> siteIds = new HashSet<String>();
		SearchBoostingAlgorithmVO algorithmVO = null;

		try {
			RepositoryItemDescriptor dataDesc = this.getSearchBoostRepository().getItemDescriptor(SEARCH_BOOST_ALGORITHM);
			RepositoryView dataView = dataDesc.getRepositoryView();
			QueryBuilder builder = dataView.getQueryBuilder();

			// build query for all repository items
			Query query = builder.createUnconstrainedQuery();
			logDebug("[loadSearchAlgorithmmDataInLocalMap] - Query to fetch items:" + query);

			// execute the query and get the results
			resultRepositoryItem = dataView.executeQuery(query);

			// If repository Items are not empty, put them into local cache.
			if (resultRepositoryItem != null && resultRepositoryItem.length > 0) {
				for (RepositoryItem item : resultRepositoryItem) {
					final String boostCode = (String) item.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE);
					final int repoPageType = item.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_PAGE_TYPE) != null ? (Integer) item.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_PAGE_TYPE) : 0;
					String cacheKey = boostCode + UNDERSCORE + repoPageType;
					
					Set<RepositoryItem> sites = (Set<RepositoryItem>) item.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_SITES);

					if (sites != null && !sites.isEmpty()) {
						/***
						 * Iterate over all the sites and add site Ids to the
						 * SearchBoostingAlgorithmVO for future use.
						 */
						for (RepositoryItem site : sites) {
							final String siteRepoId = site.getRepositoryId();
							siteIds.add(siteRepoId);
						}
					}
					algorithmVoList = new ArrayList<AlgorithmComponentVO>();
					algorithmVO = new SearchBoostingAlgorithmVO();
					algorithmVO.setEntryId(((String) item.getRepositoryId()));
					algorithmVO.setSiteSpectCode(boostCode);
					algorithmVO.setPageType(repoPageType);
					algorithmVO.setOmnitureEventRequired((Boolean) item.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_OMNITURE_EVT_REQ));
					algorithmVO.setRandomizationRequired((boolean) item.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_RANDOMIZATION_REQ));
					algorithmVO.setSites(siteIds);
					algorithmVO.setLastModifiedDate((Timestamp) item.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_LAST_MOD_DATE));
					List algoid = (List) item.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_ID);
					String algorithmDescription = "";
					for (Object object : algoid) {
						RepositoryItem algoRepoItem = (RepositoryItem) object;
						algorithmComponentVO = new AlgorithmComponentVO();
						algorithmComponentVO.setAlgorithmId((String) algoRepoItem.getRepositoryId());
						algorithmComponentVO.setAlgorithmName((String) algoRepoItem.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_NAME));
						algorithmComponentVO.setPercentage((Integer) algoRepoItem.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_PERCENTAGE));
						algorithmComponentVO.setAlgorithmType((Integer) algoRepoItem.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_TYPE));
						algorithmComponentVO.setAlgorithmDescription((String) algoRepoItem.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_DESCRIPTION));
						algorithmComponentVO.setEndecaProperty((String) algoRepoItem.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_ENDECA_PROPERTY));
						algorithmComponentVO.setLastModifiedDate((Timestamp) algoRepoItem.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_LAST_MOD_DATE));
						if(null!=algorithmComponentVO.getAlgorithmDescription()){
							algorithmDescription += BBBCoreConstants.HASH+algorithmComponentVO.getAlgorithmDescription(); 
						}
						algorithmVO.setOmnitureDescription(algorithmDescription);						
						algorithmVoList.add(algorithmComponentVO);
					}
					algorithmVO.setAlgorithmComponents(algorithmVoList);
					for (String siteId : siteIds) {
						String localCacheKey = cacheKey + UNDERSCORE + siteId;
						logDebug("[loadSearchAlgorithmmDataInLocalMap] Cache Key: " + localCacheKey + " for site ids: " + siteIds + " and cache value[algorithmVO]: " + algorithmVO);
						getLocalCacheContainer().put(localCacheKey, algorithmVO);
					}
				}
			}
		} catch (RepositoryException re) {
			logError("BBBSearchAlgorithmDataLoader | loadSearchAlgorithmmDataInLocalMap | Exception", re);
			BBBPerformanceMonitor.cancel("BBBSearchAlgorithmDataLoader", "loadSearchAlgorithmmDataInLocalMap");
		} finally {
			BBBPerformanceMonitor.end("BBBSearchAlgorithmDataLoader", "loadSearchAlgorithmmDataInLocalMap");
		}
	}
}
