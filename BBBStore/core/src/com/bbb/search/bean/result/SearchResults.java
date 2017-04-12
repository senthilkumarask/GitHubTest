/**
 * 
 */
package com.bbb.search.bean.result;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.bbb.commerce.checklist.vo.CheckListSeoUrlHierarchy;

/**
 * @author agupt8
 *
 */
public class SearchResults implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private BBBProductList bbbProducts;
	private PaginationVO pagingLinks;
	private CategoryParentVO categoryHeader;
	private List<FacetParentVO> facets; 
	private List<CurrentDescriptorVO> descriptors;
	private String searchQuery;
	private int filterCount;
	private Map<String, Asset> assetMap;
	private String redirUrl;
	private String catalogIdParam;
	// To hold promotional content.
	private Map<String,List<PromoVO>> promoMap;
	
	// To hold Auto Suggestion related Content.
	private List<AutoSuggestVO> autoSuggest;
	
	//92 F partial Search Result List
	private Map<String, SearchResults> partialSearchResults;
	private String partialFlag;
	private String partialKeywordURL;
	private String urlSearchMode;
	private String parentCatName;
	private String currentCatName;
	
	private List<FacetParentVO> emptyFacetsList;
	private String allTabList;
	private boolean deptAvail;
	private boolean emptyBox;
	private boolean emptyFacets;
	private int facetCountForRefTypeParam;
	private String omnitureVariable;
	private boolean endecaControl;
	private boolean isEphApplied;
	private String  ephQueryDetail;
	private String inStoreEphQuery;
	private String actualAlgorithmApplied;

	public String getActualAlgorithmApplied() {
		return actualAlgorithmApplied;
	}

	public void setActualAlgorithmApplied(String actualAlgorithmApplied) {
		this.actualAlgorithmApplied = actualAlgorithmApplied;
	}
	
	private CheckListSeoUrlHierarchy checkListSeoUrlHierarchy;
	
	public String getInStoreEphQuery() {
		return inStoreEphQuery;
	}
	public void setInStoreEphQuery(String inStoreEphQuery) {
		this.inStoreEphQuery = inStoreEphQuery;
	}
	/**
	 * @return the emptyFacetsList
	 */
	public List<FacetParentVO> getEmptyFacetsList() {
		return emptyFacetsList;
	}
	/**
	 * @param emptyFacetsList the emptyFacetsList to set
	 */
	public void setEmptyFacetsList(List<FacetParentVO> emptyFacetsList) {
		this.emptyFacetsList = emptyFacetsList;
	}
	
	public Map<String, SearchResults> getPartialSearchResults() {
		return partialSearchResults;
	}
	public void setPartialSearchResults(
			Map<String, SearchResults> partialSearchResults) {
		this.partialSearchResults = partialSearchResults;
	}
	/**
	 * @return the catalogIdParam
	 */
	public String getCatalogIdParam() {
		return this.catalogIdParam;
	}
	/**
	 * @param catalogIdParam the catalogIdParam to set
	 */
	public void setCatalogIdParam(String catalogIdParam) {
		this.catalogIdParam = catalogIdParam;
	}
	
	/**
	 * @return the bbbProducts
	 */
	public BBBProductList getBbbProducts() {
		return this.bbbProducts;
	}
	/**
	 * @param bbbProducts the bbbProducts to set
	 */
	public void setBbbProducts(final BBBProductList bbbProducts) {
		this.bbbProducts = bbbProducts;
	}
	
	/**
	 * @return the pagingLinks
	 */
	public PaginationVO getPagingLinks() {
		return this.pagingLinks;
	}
	/**
	 * @param pagingLinks the pagingLinks to set
	 */
	public void setPagingLinks(final PaginationVO pagingLinks) {
		this.pagingLinks = pagingLinks;
	}
	
	/**
	 * @return the categoryHeader
	 */
	public CategoryParentVO getCategoryHeader() {
		return this.categoryHeader;
	}
	/**
	 * @param pCategoryHeader the categoryHeader to set
	 */
	public void setCategoryHeader(final CategoryParentVO pCategoryHeader) {
		this.categoryHeader = pCategoryHeader;
	}
	/**
	 * @return the facets
	 */
	public List<FacetParentVO> getFacets() {
		return this.facets;
	}
	/**
	 * @param facets the facets to set
	 */
	public void setFacets(final List<FacetParentVO> facets) {
		this.facets = facets;
	}
	/**
	 * @return the descriptors
	 */
	public List<CurrentDescriptorVO> getDescriptors() {
		return this.descriptors;
	}
	/**
	 * @param descriptors the descriptors to set
	 */
	public void setDescriptors(final List<CurrentDescriptorVO> descriptors) {
		this.descriptors = descriptors;
	}
	
	/**
	 * @return the searchQuery
	 */
	public String getSearchQuery() {
		return this.searchQuery;
	}
	/**
	 * @param pSearchQuery the searchQuery to set
	 */
	public void setSearchQuery(final String pSearchQuery) {
		this.searchQuery = pSearchQuery;
	}
	/**
	 * @return the assetMap
	 */
	public Map<String, Asset> getAssetMap() {
		return this.assetMap;
	}
	/**
	 * @param assetMap the assetMap to set
	 */
	public void setAssetMap(Map<String, Asset> assetMap) {
		this.assetMap = assetMap;
	}
	/**
	 * @return the promoMap
	 */
	public Map<String,List<PromoVO>> getPromoMap() {
		return this.promoMap;
	}
	/**
	 * @param promoMap the promoMap to set
	 */
	public void setPromoMap(Map<String,List<PromoVO>> promoMap) {
		this.promoMap = promoMap;
	}
	
	public List<AutoSuggestVO> getAutoSuggest() {
		return this.autoSuggest;
	}
	public void setAutoSuggest(List<AutoSuggestVO> autoSuggest) {
		this.autoSuggest = autoSuggest;
	}
	/**
	 * @return the redirUrl
	 */
	public String getRedirUrl() {
		return this.redirUrl;
	}
	/**
	 * @param redirUrl the redirUrl to set
	 */
	public void setRedirUrl(final String redirUrl) {
		this.redirUrl = redirUrl;
	}
	/**
	 * @return the partialFlag. For partial tab results to get results in partial search mode.
	 */
	public String getPartialFlag() {
		return partialFlag;
	}
	/**
	 * @param partialFlag the partialFlag to set
	 */
	public void setPartialFlag(String partialFlag) {
		this.partialFlag = partialFlag;
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
	
	/**
	 * @return the urlSearchMode
	 */
	public String getUrlSearchMode() {
		return urlSearchMode;
	}
	/**
	 * @param urlSearchMode the urlSearchMode to set
	 */
	public void setUrlSearchMode(String urlSearchMode) {
		this.urlSearchMode = urlSearchMode;
	}
	/**
	 * @return the parentCatName
	 */
	public String getParentCatName() {
		return parentCatName;
	}
	/**
	 * @param parentCatName the parentCatName to set
	 */
	public void setParentCatName(String parentCatName) {
		this.parentCatName = parentCatName;
	}
	/**
	 * @return the currentCatName
	 */
	public String getCurrentCatName() {
		return currentCatName;
	}
	/**
	 * @param currentCatName the currentCatName to set
	 */
	public void setCurrentCatName(String currentCatName) {
		this.currentCatName = currentCatName;
	}
	/**
	 * @return the deptAvail
	 */
	public boolean isDeptAvail() {
		return deptAvail;
	}
	/**
	 * @param deptAvail the deptAvail to set
	 */
	public void setDeptAvail(boolean deptAvail) {
		this.deptAvail = deptAvail;
	}
	/**
	 * @return the emptyBox
	 */
	public boolean isEmptyBox() {
		return emptyBox;
	}
	/**
	 * @param emptyBox the emptyBox to set
	 */
	public void setEmptyBox(boolean emptyBox) {
		this.emptyBox = emptyBox;
	}
	/**
	 * @return the emptyFacets
	 */
	public boolean isEmptyFacets() {
		return emptyFacets;
	}
	/**
	 * @param emptyFacets the emptyFacets to set
	 */
	public void setEmptyFacets(boolean emptyFacets) {
		this.emptyFacets = emptyFacets;
	}
	/**
	 * @return the facetCountForRefTypeParam
	 */
	public int getFacetCountForRefTypeParam() {
		return facetCountForRefTypeParam;
	}
	/**
	 * @param facetCountForRefTypeParam the facetCountForRefTypeParam to set
	 */
	public void setFacetCountForRefTypeParam(int facetCountForRefTypeParam) {
		this.facetCountForRefTypeParam = facetCountForRefTypeParam;
	}
	/**
	 * @return the allTabList
	 */
	public String getAllTabList() {
		return allTabList;
	}
	/**
	 * @param allTabList the allTabList to set
	 */
	public void setAllTabList(String allTabList) {
		this.allTabList = allTabList;
	}
	public int getFilterCount() {
		return filterCount;
	}
	public void setFilterCount(int filterCount) {
		this.filterCount = filterCount;
	}
	/**
	 * @return the omnitureVariable
	 */
	public String getOmnitureVariable() {
		return omnitureVariable;
	}
	/**
	 * @param omnitureVariable the omnitureVariable to set
	 */
	public void setOmnitureVariable(String omnitureVariable) {
		this.omnitureVariable = omnitureVariable;
	}
	public boolean isEndecaControl() {
		return endecaControl;
	}
	public void setEndecaControl(boolean endecaControl) {
		this.endecaControl = endecaControl;
	}
	/**
	 * @return the isEphApplied
	 */
	public boolean isEphApplied() {
		return isEphApplied;
	}
	/**
	 * @param isEphApplied the isEphApplied to set
	 */
	public void setEphApplied(boolean isEphApplied) {
		this.isEphApplied = isEphApplied;
	}
	/**
	 * @return the ephQueryDetail
	 */
	public String getEphQueryDetail() {
		return ephQueryDetail;
	}
	/**
	 * @param ephQueryDetail the ephQueryDetail to set
	 */
	public void setEphQueryDetail(String ephQueryDetail) {
		this.ephQueryDetail = ephQueryDetail;
	}
	/**
	 * @return the checkListSeoUrlHierarchy
	 */
	public CheckListSeoUrlHierarchy getCheckListSeoUrlHierarchy() {
		return checkListSeoUrlHierarchy;
	}
	
	/**
	 * @param checkListSeoUrlHierarchy the checkListSeoUrlHierarchy to set
	 */
	public void setCheckListSeoUrlHierarchy(CheckListSeoUrlHierarchy checkListSeoUrlHierarchy) {
		this.checkListSeoUrlHierarchy = checkListSeoUrlHierarchy;
	}
	
	public String getChecklistCategoryName(){
		String categoryName = "";
		if(null != this.getCheckListSeoUrlHierarchy()){
			categoryName =  this.getCheckListSeoUrlHierarchy().getCheckListCategoryName();
		}
		return categoryName;
	}
	
}