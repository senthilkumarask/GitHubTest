package com.bbb.search.vendor.vo;

import java.util.List;
import java.util.Map;

import com.bbb.search.bean.result.Asset;
import com.bbb.search.bean.result.AutoSuggestVO;
import com.bbb.search.bean.result.BBBProductList;
import com.bbb.search.bean.result.CategoryParentVO;
import com.bbb.search.bean.result.CurrentDescriptorVO;
import com.bbb.search.bean.result.FacetParentVO;
import com.bbb.search.bean.result.PaginationVO;
import com.bbb.search.bean.result.PromoVO;
import com.bbb.search.bean.result.SearchResults;

public class VendorResponseVO{

	private BBBProductList bbbProducts;
	private PaginationVO pagingLinks;
	private CategoryParentVO categoryHeader;
	private List<FacetParentVO> facets;
	private List<CurrentDescriptorVO> descriptors;
	private int filterCount;
	private Map<String, Asset> assetMap;
	private String redirUrl;
	private Map<String,List<PromoVO>> promoMap;
	private List<AutoSuggestVO> autoSuggest;
	private Map<String, SearchResults> partialSearchResults;
	private String partialKeywordURL;
	
	/**
	 * @return the bbbProducts
	 */
	public BBBProductList getBbbProducts() {
		return bbbProducts;
	}
	/**
	 * @param bbbProducts the bbbProducts to set
	 */
	public void setBbbProducts(BBBProductList bbbProducts) {
		this.bbbProducts = bbbProducts;
	}
	/**
	 * @return the pagingLinks
	 */
	public PaginationVO getPagingLinks() {
		return pagingLinks;
	}
	/**
	 * @param pagingLinks the pagingLinks to set
	 */
	public void setPagingLinks(PaginationVO pagingLinks) {
		this.pagingLinks = pagingLinks;
	}
	/**
	 * @return the categoryHeader
	 */
	public CategoryParentVO getCategoryHeader() {
		return categoryHeader;
	}
	/**
	 * @param categoryHeader the categoryHeader to set
	 */
	public void setCategoryHeader(CategoryParentVO categoryHeader) {
		this.categoryHeader = categoryHeader;
	}
	/**
	 * @return the facets
	 */
	public List<FacetParentVO> getFacets() {
		return facets;
	}
	/**
	 * @param facets the facets to set
	 */
	public void setFacets(List<FacetParentVO> facets) {
		this.facets = facets;
	}
	/**
	 * @return the descriptors
	 */
	public List<CurrentDescriptorVO> getDescriptors() {
		return descriptors;
	}
	/**
	 * @param descriptors the descriptors to set
	 */
	public void setDescriptors(List<CurrentDescriptorVO> descriptors) {
		this.descriptors = descriptors;
	}
	/**
	 * @return the filterCount
	 */
	public int getFilterCount() {
		return filterCount;
	}
	/**
	 * @param filterCount the filterCount to set
	 */
	public void setFilterCount(int filterCount) {
		this.filterCount = filterCount;
	}
	/**
	 * @return the assetMap
	 */
	public Map<String, Asset> getAssetMap() {
		return assetMap;
	}
	/**
	 * @param assetMap the assetMap to set
	 */
	public void setAssetMap(Map<String, Asset> assetMap) {
		this.assetMap = assetMap;
	}
	/**
	 * @return the redirUrl
	 */
	public String getRedirUrl() {
		return redirUrl;
	}
	/**
	 * @param redirUrl the redirUrl to set
	 */
	public void setRedirUrl(String redirUrl) {
		this.redirUrl = redirUrl;
	}
	/**
	 * @return the promoMap
	 */
	public Map<String, List<PromoVO>> getPromoMap() {
		return promoMap;
	}
	/**
	 * @param promoMap the promoMap to set
	 */
	public void setPromoMap(Map<String, List<PromoVO>> promoMap) {
		this.promoMap = promoMap;
	}
	/**
	 * @return the autoSuggest
	 */
	public List<AutoSuggestVO> getAutoSuggest() {
		return autoSuggest;
	}
	/**
	 * @param autoSuggest the autoSuggest to set
	 */
	public void setAutoSuggest(List<AutoSuggestVO> autoSuggest) {
		this.autoSuggest = autoSuggest;
	}
	/**
	 * @return the partialSearchResults
	 */
	public Map<String, SearchResults> getPartialSearchResults() {
		return partialSearchResults;
	}
	/**
	 * @param partialSearchResults the partialSearchResults to set
	 */
	public void setPartialSearchResults(
			Map<String, SearchResults> partialSearchResults) {
		this.partialSearchResults = partialSearchResults;
	}
	/**
	 * @return the partialKeywordURL
	 */
	public String getPartialKeywordURL() {
		return partialKeywordURL;
	}
	/**
	 * @param partialKeywordURL the partialKeywordURL to set
	 */
	public void setPartialKeywordURL(String partialKeywordURL) {
		this.partialKeywordURL = partialKeywordURL;
	}
	
	

}
