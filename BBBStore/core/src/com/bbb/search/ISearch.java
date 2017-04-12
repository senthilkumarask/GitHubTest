/*
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 * 
 * Reproduction or use of this file without explicit written consent is prohibited.
 * 
 * Created by: Archit Goel
 * 
 * Created on: 13-October-2011
 * --------------------------------------------------------------------------------
 */

package com.bbb.search;

import java.util.List;
import java.util.Map;

import com.bbb.commerce.browse.vo.BrandsListingVO;
//import com.bbb.commerce.catalog.vo.BrandVO;
import com.bbb.commerce.catalog.vo.CollegeVO;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.result.CategoryParentVO;
import com.bbb.search.bean.result.FacetQuery;
import com.bbb.search.bean.result.FacetQueryResults;
import com.bbb.search.bean.result.SearchResults;

/**
 * Interface to access the Search API from ATG Components.
 * 
 * @author agoe21
 */
public interface ISearch {

	/**
	 * This method actually performs Search after integration with Search Engine.
	 * @param searchQuery
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	SearchResults performSearch(SearchQuery searchQuery, boolean cacheSkip)  throws BBBBusinessException,BBBSystemException;
	
	/**
	 *  This method actually performs Search after integration with Search Engine. 
	 * @param searchQuery
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	SearchResults performSearch(SearchQuery searchQuery)  throws BBBBusinessException,BBBSystemException;

	/**
	 * This method is to actually perform refining on the facets/ dimensions. 
	 * @param facetQuery
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	FacetQueryResults performFacetSearch(FacetQuery facetQuery)  throws BBBBusinessException,BBBSystemException;
		
	/** This is to get the Category tree for a  given Category with category Id to be passed as part of Search Query object.
	 * @param pCategoryParentVO
	 * @param pSearchQuery
	 * @return Map
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	Map<String,CategoryParentVO> getCategoryTree(final SearchQuery pSearchQuery) throws BBBBusinessException,BBBSystemException;
	
	/**
	 * @param parentName
	 * @param dimName
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public String getCatalogId(String parentName, String dimName) throws BBBBusinessException, BBBSystemException;
	
	/**
	 * @param catalogId
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public BrandsListingVO getBrands(String catalogId, String siteId) throws BBBBusinessException,BBBSystemException;

	/**
	 * @param catalogId
	 * @param pSiteId
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public List<StateVO> getStates(final String catalogId,final String pSiteId)throws BBBBusinessException,BBBSystemException;

	/**
	 * @param catalogId
	 * @param pSiteId
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public List<CollegeVO> getColleges(final String catalogId,final String pSiteId)throws BBBBusinessException,BBBSystemException;
}
