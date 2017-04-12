package com.bbb.search.endeca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.servlet.GenericHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.query.SortCriteria;
import com.bbb.search.bean.result.FacetQuery;
import com.bbb.search.bean.result.FacetQueryResults;
import com.bbb.search.bean.result.SearchResults;
import com.sapient.common.tests.BaseTestCase;

//import com.bbb.search.endeca.EndecaSearch;

public class TestEndecaSearch extends BaseTestCase{
	
	public TestEndecaSearch() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void testPerformSearch() throws Exception {/*
    	EndecaSearch search = (EndecaSearch) getObject("endecaSearch");
    	
    	SearchQuery req = new SearchQuery();
    	
    	List<String> pQueryFacets = new ArrayList<String>();
		pQueryFacets.add("BRAND");
		req.setQueryFacets(pQueryFacets);
		GenericHttpServletRequest genericHttpServletRequest = new GenericHttpServletRequest();
		genericHttpServletRequest.setHeader(BBBCoreConstants.CHANNEL,"");
		genericHttpServletRequest.setHeader(BBBCoreConstants.HOST,"localhost:7003");
		ServletUtil.getDynamoRequest(this.getRequest()).setRequest(genericHttpServletRequest);
		ServletUtil.setCurrentRequest(this.getRequest());
        req.setSortCriteria(new SortCriteria());
        Map<String,String> pCatalogRef = new HashMap<String, String>();
        req.setCatalogRef(pCatalogRef);
        req.setHostname((String)getObject("hostname"));
        req.setSiteId((String)getObject("siteId"));
        req.setKeyWord((String)getObject("keyWord"));
		req.setPageNum((String)getObject("pageNum"));
		req.setHeaderSearch((Boolean)getObject("headerSearch"));
		SearchResults results = search.performSearch(req);
		
		assertTrue("Search result is empty", results.getBbbProducts().getBBBProducts().size() > 0);
    */}
	
	public void testPerformFacetSearch() throws Exception {/*
    	EndecaSearch search = (EndecaSearch) getObject("endecaSearch");
    	
    	FacetQuery req = new FacetQuery();
    	List<String> pFacets = new ArrayList<String>();
    	pFacets.add("DEPARTMENT");
    	pFacets.add("BRAND");
		req.setFacets(pFacets);
		GenericHttpServletRequest genericHttpServletRequest = new GenericHttpServletRequest();
		genericHttpServletRequest.setHeader(BBBCoreConstants.CHANNEL,"");
		genericHttpServletRequest.setHeader(BBBCoreConstants.HOST,"localhost:7003");
		ServletUtil.getDynamoRequest(this.getRequest()).setRequest(genericHttpServletRequest);
		ServletUtil.setCurrentRequest(this.getRequest());    
        req.setSiteId((String)getObject("siteId"));
        req.setCatalogId((String)getObject("catalogId"));
        req.setKeyword((String)getObject("keyWord"));		
		req.setPageFilterSize((String)getObject("pageFilterSize"));
		req.setShowPopularTerms((Boolean)getObject("showPopularTerms"));		
		FacetQueryResults results = search.performFacetSearch(req);
		
		assertTrue("Facet Search result is empty", results.getResults().get(0)!= null);
    */}
	
	public void testGetPopularItemsResults() throws Exception {/*
    	EndecaSearch search = (EndecaSearch) getObject("endecaSearch");
    	
    	List<ProductVO> prodList = null;
    	String bccSearchQuery = (String)getObject("bccSearchQuery");
    	
		GenericHttpServletRequest genericHttpServletRequest = new GenericHttpServletRequest();
		genericHttpServletRequest.setHeader(BBBCoreConstants.CHANNEL,"");
		genericHttpServletRequest.setHeader(BBBCoreConstants.HOST,"localhost:7003");
		ServletUtil.getDynamoRequest(this.getRequest()).setRequest(genericHttpServletRequest);
		ServletUtil.setCurrentRequest(this.getRequest());    	
		prodList = search.getPopularItemsResults(prodList , bccSearchQuery , (String)getObject("siteId"));
		
		assertTrue("Popular items result is empty", prodList.size()  ==  0 );
    */}
}
