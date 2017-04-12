package com.bbb.search.vendor;

import java.util.List;
import java.util.Map;

import com.bbb.commerce.browse.vo.BrandsListingVO;
import com.bbb.commerce.catalog.vo.CollegeVO;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.common.BBBGenericService;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.ISearch;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.result.CategoryParentVO;
import com.bbb.search.bean.result.FacetQuery;
import com.bbb.search.bean.result.FacetQueryResults;
import com.bbb.search.bean.result.SearchResults;
import com.bbb.search.vendor.customizer.VendorResponseCustomizer;
import com.bbb.search.vendor.vo.VendorRequestVO;

public class VendorSearch extends BBBGenericService implements ISearch {
	
	private static final String PERFORM_SEARCH = "performSearch";
	private static final String UNSUPPORTED_METHOD = "Unsupported Method";
	private static final String CLASS_NAME = "Vendor_Search";
	private VendorSearchUtil vendorSearchUtil;
	private VendorResponseCustomizer[] customizers;

	public VendorResponseCustomizer[] getCustomizers() {
		return customizers;
	}

	public void setCustomizers(VendorResponseCustomizer[] customizers) {
		this.customizers = customizers;
	}

	public VendorSearchUtil getVendorSearchUtil() {
		return vendorSearchUtil;
	}

	public void setVendorSearchUtil(VendorSearchUtil vendorSearchUtil) {
		this.vendorSearchUtil = vendorSearchUtil;
	}

	/* 
	 * This method invoke the third party search engine and returns the SearchResults
	 * @see com.bbb.search.ISearch#performSearch(com.bbb.search.bean.query.SearchQuery)
	 */
	@Override
	public SearchResults performSearch(SearchQuery pSearchQuery)
			throws BBBBusinessException, BBBSystemException {
		BBBPerformanceMonitor.start(CLASS_NAME,
				PERFORM_SEARCH);
		logDebug("START : VendorSearch.performSearch, input : pSearchQuery:"+pSearchQuery.toString());
		VendorRequestVO vReqVO = getVendorSearchUtil().prepareVendorReuestVO(pSearchQuery);
		SearchResults bsVO = getVendorSearchUtil().performVendorSearch(vReqVO);
		if(bsVO != null){  //ask for this check
			this.doCustomizeResponse(bsVO, pSearchQuery);
		}else {
			logDebug("END : VendorSearch.performSearch, SearchResults not found");
			BBBPerformanceMonitor.end(CLASS_NAME, PERFORM_SEARCH);
			return bsVO;
		}
		
		logDebug("END : VendorSearch.performSearch, SearchResults"+bsVO.toString());  //Ask this
		BBBPerformanceMonitor.end(CLASS_NAME, PERFORM_SEARCH);
		return bsVO;
	}

	@Override
	public FacetQueryResults performFacetSearch(FacetQuery facetQuery)
			throws BBBBusinessException, BBBSystemException {
		logError("VendorSearch.performFacetSearch method is unsupported!!");
		throw new UnsupportedOperationException(UNSUPPORTED_METHOD);
	}
	
	@Override
	public Map<String, CategoryParentVO> getCategoryTree(
			SearchQuery pSearchQuery) throws BBBBusinessException,
			BBBSystemException {
		logError("VendorSearch.getCategoryTree method is unsupported!!");
		throw new UnsupportedOperationException(UNSUPPORTED_METHOD);
	}

	@Override
	public String getCatalogId(String parentName, String dimName)
			throws BBBBusinessException, BBBSystemException {
		logError("VendorSearch.getCatalogId method is unsupported!!");
		throw new UnsupportedOperationException(UNSUPPORTED_METHOD);
	}

	@Override
	public BrandsListingVO getBrands(String catalogId, String siteId)
			throws BBBBusinessException, BBBSystemException {
		logError("VendorSearch.getBrands method is unsupported!!");
		throw new UnsupportedOperationException(UNSUPPORTED_METHOD);
	}

	@Override
	public List<StateVO> getStates(String catalogId, String pSiteId)
			throws BBBBusinessException, BBBSystemException {
		logError("VendorSearch.getStates method is unsupported!!");
		throw new UnsupportedOperationException(UNSUPPORTED_METHOD);
	}

	@Override
	public List<CollegeVO> getColleges(String catalogId, String pSiteId)
			throws BBBBusinessException, BBBSystemException {
		logError("VendorSearch.getColleges method is unsupported!!");
		throw new UnsupportedOperationException(UNSUPPORTED_METHOD);
	}
	
	/**
	 * This method customizes the Vendor Response by calling the customizers
	 * 
	 * @param bsVO
	 */
	protected void doCustomizeResponse(SearchResults bsVO, SearchQuery pSearchQuery){
		BBBPerformanceMonitor.start(CLASS_NAME,
				"doCustomizeResponse");
		logDebug("[START] VendorSearch.doCustomizeResponse() for Search Keyword :: " + pSearchQuery.getKeyWord());
		
		VendorResponseCustomizer[] customizers = getCustomizers();
        if(customizers != null){
            int len = customizers.length;
            for(int i = 0; i < len; i++){
            	VendorResponseCustomizer customizer = customizers[i];
                logDebug((new StringBuilder()).append("Processing customizer:").append(customizer).toString());
                customizer.customizeResponse(bsVO, pSearchQuery);
                }
            }
        logDebug("[END] VendorSearch.doCustomizeResponse()");
        BBBPerformanceMonitor.end(CLASS_NAME,
				"doCustomizeResponse");
       }

	@Override
	public SearchResults performSearch(SearchQuery searchQuery,
			boolean cacheSkip) throws BBBBusinessException, BBBSystemException {
		return performSearch(searchQuery);
	}
	

}
