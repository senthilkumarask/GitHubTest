/**
 *
 */
package com.bbb.search.endeca.tools;

import java.util.List;
import java.util.Map;

import com.bbb.framework.cache.BBBLocalCacheContainer;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.endeca.vo.SearchBoostingAlgorithmVO;
import com.endeca.navigation.ENEQueryResults;

/**
 * This class used to get the generic values and pass these values to the
 * respective components.
 * 
 * @author sapient
 * 
 */
public interface EndecaSearchTools {
	/**
	 * This method used to get Local Cache Reference
	 */
	public BBBLocalCacheContainer getLocalCacheContainer();
	/**
	 * This method used to get the search boosting algorithms from the Search
	 * Boost repository for the requested boostCode, site id and page name. This boostCode is provided
	 * by the SiteSpect in request.
	 * 
	 * @param searchAlgorithmParams
	 * @return
	 */
	public SearchBoostingAlgorithmVO getSearchBoostingAlgorithms(Map<String, String> searchAlgorithmParams);
	
	/**
	 * This method is used to calculate the total boosted products based on the
	 * percentage from algorithm.
	 * 
	 * @param searchBoostingAlgorithm
	 * @param boostingStrategy
	 * @return
	 */
	public Map<String, String> populateBoostedProductParams(SearchBoostingAlgorithmVO searchBoostingAlgorithm, String boostingStrategy,SearchQuery pSearchQuery);

	/**
	 * This method used to get the updated endeca query for facet boosting
	 * strategy
	 * 
	 * @param searchQuery
	 * @param dimensionIdFromAlgorithm
	 * @param navigationUrl
	 * @return
	 */
	public String getFacetQuery(SearchQuery searchQuery, String dimensionIdFromAlgorithm, List<String> navigationUrl, boolean l2l3BrandBoostingEnabled);

	/**
	 * This method used to get the updated endeca query for SORT boosting
	 * strategy
	 * 
	 * @param searchQuery
	 * @param navigationUrl 
	 * @param dimensionIdFromAlgorithm
	 * @return
	 */
	public String getSortQuery(SearchQuery searchQuery, String sortField, List<String> navigationUrl, boolean l2l3BrandBoostingEnabled);

	/**
	 * This method used to get Boosted product list for SORT and FACET Boosting strategy
	 * 
	 * @param queryResults
	 * @param algorithmProductList
	 * @return
	 */
	public List<String> getBoostedProductList(ENEQueryResults queryResults, List<String> algorithmProductList,SearchQuery pSearchQuery);

	/**
	 * This method is used to fetch respective boosting percentage of each
	 * boosting strategy
	 * 
	 * @param searchBoostingAlgorithm
	 * @return
	 */
	public Map<String, Double> getBoostingPercentage(SearchBoostingAlgorithmVO searchBoostingAlgorithm,SearchQuery pSearchQuery);

	/**
	 * This method used to get the total number of boosted products
	 * 
	 * @param searchBoostingAlgorithm
	 * @param opbCount
	 * @return
	 */
	public int getTotalNumberOfBoostedProducts(SearchBoostingAlgorithmVO searchBoostingAlgorithm, int opbCount,SearchQuery pSearchQuery);

	/**
	 * This method used to get the page type which will be used to get the
	 * search algorithm for the specific page
	 * 
	 * @param searchQuery
	 * @param l2l3BrandBoostingEnabled
	 * 
	 * @return
	 */
	public int getPageType(SearchQuery searchQuery, boolean l2l3BrandBoostingEnabled);

	/**
	 * This method used to get the boosted product list from cache
	 * 
	 * @param boostQuery
	 * @return boostedProductList
	 */
	public List<String> getBoostedProductListFromCache(String boostQuery);

	/**
	 * This method used to insert boosted product list into the cache
	 * 
	 * @param boostQuery
	 * @param boostedProductList
	 */
	public void insertBoostedProductsIntoCache(String boostQuery, List<String> boostedProductList);

	/**
	 * This method used to get the search boosting algorithms from the Local Map
	 * for the requested boostCode, site id and page name. This boostCode is provided
	 * by the SiteSpect in request.
	 * 
	 * @param searchAlgorithmParams
	 * @return
	 */
	public SearchBoostingAlgorithmVO getSearchBoostingAlgorithmsFromLocalMap(Map<String, String> searchAlgorithmParams);
	
	/**
	 * This method generates the searchQueryURL to be used for fetching the product boosting results
	 * ignoring SWS and InStore functionality
	 * 
	 * @param pSearchQuery
	 * @return String searchQuery
	 */
	public String fetchSearchQueryURL(SearchQuery pSearchQuery);
}