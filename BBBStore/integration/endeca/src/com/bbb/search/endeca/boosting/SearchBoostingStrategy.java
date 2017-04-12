package com.bbb.search.endeca.boosting;

import java.util.List;

import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.endeca.vo.SearchBoostingAlgorithmVO;

/**
 * @author sapient
 *
 */
public interface SearchBoostingStrategy {

	/**
	 * This method is used to get the boosted products for each boosting
	 * strategy
	 * 
	 * @param pSearchQuery
	 * @param serachBoostingAlgorithm
	 * @return
	 */
	List<String> getBoostedProducts(SearchQuery pSearchQuery, SearchBoostingAlgorithmVO searchBoostingAlgorithm, boolean l2l3BrandBoostingEnabled);
}
