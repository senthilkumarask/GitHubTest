/**
 *
 */
package com.bbb.search.bean.query;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.bbb.commerce.checklist.vo.CheckListSeoUrlHierarchy;
import com.bbb.eph.vo.EPHResultVO;

/** This VO holds are Search Query related parameters.
 * @author agupt8
 *
 */
public class SearchQuery implements Serializable,Cloneable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String keyWord;
	private String narrowDown;
	private SortCriteria sortCriteria;
	private String pageSize;
	private String pageNum;
	private List<QueryAsset> queryAssets;
	private List<String> queryFacets;
	// Member variable to hold catalog Id's (can be categoryId OR BrandId for which results are required)
	private Map<String,String> catalogRef;

	private Map<String,String> mPriceRangeInfo;

	private String mSiteId;
	private String mHostname;

	// Variable to hold if call is from header and only for departments.
	private boolean mHeaderSearch;

	private boolean mFromBrandPage = false;
	private boolean mFromCollege = false;
	private boolean mRedirected = false;
	private boolean mFromCategoryLanding = false;

	private boolean mFromAllBrandsPage = false;

	private String mSearchMode;
	private String refType;
    private String productsReq;
    private String navRecordStructureExpr;
    
    // EPH lookup variables
    private EPHResultVO ephResultVO;
    private boolean isEPHFound;
    private boolean isAutoSuggested;
    private String autoSuggestedKeyword;
    private long numberOfProduct;
    
    private String ephFilterString;
    private boolean isEphApplied;
    private StringBuilder boostingLogs=new StringBuilder();

    private String ephQueryScheme;
    private String storeId;
    private boolean onlineTab=false;
    private String storeIdFromAjax;
    
    //BBBJ - 1220 Checklist_category_plp variables
    private boolean fromChecklistCategory;
	private String checklistCategoryDimensionId;
	
	private String checklistName;
	private String c1name;
	private String c2name;
	private String c3name;
	private String checklistId;
	private String checklistCategoryId;
	

	private CheckListSeoUrlHierarchy checkListSeoUrlHierarchyVo;
    

	public String getStoreIdFromAjax() {
		return storeIdFromAjax;
	}
	public void setStoreIdFromAjax(String storeIdFromAjax) {
		this.storeIdFromAjax = storeIdFromAjax;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public boolean isOnlineTab() {
		return onlineTab;
	}
	public void setOnlineTab(boolean onlineTab) {
		this.onlineTab = onlineTab;
	}
		public String getRefType() {
		return refType;
	}
	public void setRefType(String refType) {
		this.refType = refType;
	}

	//group param added for Endeaca Query param NTK
	 private String groupId;

		//stofu related change starts
		private String channelId;
		/**
		 * @return the groupId
		 */
		public String getGroupId() {
			return groupId;
		}
		/**
		 * @param groupId the groupId to set
		 */
		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}

		private Map<String,String> gsEndecaQuery;
		private boolean gSTypeAhead = false;
		private List<String> stopWrdRemovedString;
		private String partialFlag;
		private String channelThemeId;
		
		//BBBSL-11385 | Creating phantom flag
	    private boolean checkPhantom;
		/**
		 * @return the partialFlag
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
		public List<String> getStopWrdRemovedString() {
			return stopWrdRemovedString;
		}
		public void setStopWrdRemovedString(List<String> stopWrdRemovedString) {
			this.stopWrdRemovedString = stopWrdRemovedString;
		}

		public Map<String, String> getGsEndecaQuery() {
			return gsEndecaQuery;
		}
		public void setGsEndecaQuery(Map<String, String> gsEndecaQuery) {
			this.gsEndecaQuery = gsEndecaQuery;
		}
		public String getChannelId() {
			return channelId;
		}
		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}

		public boolean isgSTypeAhead() {
			return gSTypeAhead;
		}
		public void setgSTypeAhead(boolean gSTypeAhead) {
			this.gSTypeAhead = gSTypeAhead;
		}

		//stofu related change ends


	/* RM Defect 15480 changes*/
	public boolean isRedirected() {
		return mRedirected;
	}
	/* RM Defect 15480 changes*/
	public void setRedirected(boolean mRedirected) {
		this.mRedirected = mRedirected;
	}

	private String sortString;

	private String queryURL;

	/**
	 * @return the queryURL
	 */
	public String getQueryURL() {
		return queryURL;
	}
	/**
	 * @param queryURL the queryURL to set
	 */
	public void setQueryURL(String queryURL) {
		this.queryURL = queryURL;
	}
	public boolean isFromCollege() {
		return mFromCollege;
	}
	public void setFromCollege(boolean pFromCollege) {
		this.mFromCollege = pFromCollege;
	}

	/**
	 * @return the mFromBrandPage
	 */
	public boolean isFromBrandPage() {
		return mFromBrandPage;
	}
	/**
	 * @param mFromBrandPage the mFromBrandPage to set
	 */
	public void setFromBrandPage(boolean pFromBrandPage) {
		this.mFromBrandPage = pFromBrandPage;
	}

	/**
	 * @return the mFromCategoryLanding
	 */
	public boolean isFromCategoryLanding() {
		return mFromCategoryLanding;
	}
	/**
	 * @param mFromBrandPage the mFromBrandPage to set
	 */
	public void setFromCategoryLanding(boolean pFromCategoryLanding) {
		this.mFromCategoryLanding = pFromCategoryLanding;
	}

	public boolean isHeaderSearch() {
		return mHeaderSearch;
	}
	public void setHeaderSearch(boolean pHeaderSearch) {
		this.mHeaderSearch = pHeaderSearch;
	}
	/**
	 * @return the keyWord
	 */
	public String getKeyWord() {
		return keyWord;
	}
	/**
	 * @param keyWord the keyWord to set
	 */
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
	/**
	 * @return the sortCriteria
	 */
	public SortCriteria getSortCriteria() {
		return this.sortCriteria;
	}
	/**
	 * @param sortCriteria the sortCriteria to set
	 */
	public void setSortCriteria(SortCriteria sortCriteria) {
		this.sortCriteria = sortCriteria;
	}

	/**
	 * @return the pageNum
	 */
	public String getPageNum() {
		return pageNum;
	}
	/**
	 * @return the pageSize
	 */
	public String getPageSize() {
		return pageSize;
	}
	/**
	 * @param pPageSize the pageSize to set
	 */
	public void setPageSize(String pPageSize) {
		pageSize = pPageSize;
	}
	/**
	 * @param pPageNum the pageNum to set
	 */
	public void setPageNum(String pPageNum) {
		pageNum = pPageNum;
	}
	/**
	 * @return the catalogRef
	 */
	public Map<String, String> getCatalogRef() {
		return catalogRef;
	}
	/**
	 * @param pCatalogRef the catalogRef to set
	 */
	public void setCatalogRef(Map<String, String> pCatalogRef) {
		catalogRef = pCatalogRef;
	}
	/**
	 * @return the queryAssets
	 */
	public List<QueryAsset> getQueryAssets() {
		return queryAssets;
	}
	/**
	 * @param pQueryAssets the queryAssets to set
	 */
	public void setQueryAssets(List<QueryAsset> pQueryAssets) {
		queryAssets = pQueryAssets;
	}
	/**
	 * @return the queryFacets
	 */
	public List<String> getQueryFacets() {
		return queryFacets;
	}
	/**
	 * @param pQueryFacets the queryFacets to set
	 */
	public void setQueryFacets(List<String> pQueryFacets) {
		queryFacets = pQueryFacets;
	}
	/**
	 * @return mSiteId
	 */
	public String getSiteId() {
		return mSiteId;
	}

	/**
	 * @param mSiteId
	 */
	public void setSiteId(String mSiteId) {
		this.mSiteId = mSiteId;
	}
	/**
	 * @return the mHostname
	 */
	public String getHostname() {
		return mHostname;
	}
	/**
	 * @param mHostname the mHostname to set
	 */
	public void setHostname(String pHostname) {
		this.mHostname = pHostname;
	}
	public String getChannelThemeId() {
		return channelThemeId;
	}
	public void setChannelThemeId(String channelThemeId) {
		this.channelThemeId = channelThemeId;
	}

	// START -- R2.1 -- Price Range Slider Scope Item #66
	/*public Map<String, String> getPriceRangeInfo() {
		return mPriceRangeInfo;
	}
	public void setPriceRangeInfo(Map<String, String> pPriceRangeInfo) {
		this.mPriceRangeInfo = pPriceRangeInfo;
	}*/
	// END   -- R2.1 -- Price Range Slider Scope Item #66
	/**
	 * @return the sortString
	 */
	public String getSortString() {
		return sortString;
	}
	/**
	 * @param sortString the sortString to set
	 */
	public void setSortString(String sortString) {
		this.sortString = sortString;
	}

	public String getSearchMode() {
		return mSearchMode;
	}
	public void setSearchMode(String mSearchMode) {
		this.mSearchMode = mSearchMode;
	}
	public String getNarrowDown() {
		return narrowDown;
	}
	public void setNarrowDown(String narrowDown) {
		this.narrowDown = narrowDown;
	}
	public String getProductsReq() {
		return productsReq;
	}
	public void setProductsReq(String productsReq) {
		this.productsReq = productsReq;
	}
	/**
	 * @return the mFromAllBrandsPage
	 */
	public boolean isFromAllBrandsPage() {
		return mFromAllBrandsPage;
	}
	/**
	 * @param mFromAllBrandsPage the mFromAllBrandsPage to set
	 */
	public void setFromAllBrandsPage(boolean mFromAllBrandsPage) {
		this.mFromAllBrandsPage = mFromAllBrandsPage;
	}
	/**
	 * @return the navRecordStructureExpr
	 */
	public String getNavRecordStructureExpr() {
		return navRecordStructureExpr;
	}
	/**
	 * @param navRecordStructureExpr the navRecordStructureExpr to set
	 */
	public void setNavRecordStructureExpr(String navRecordStructureExpr) {
		this.navRecordStructureExpr = navRecordStructureExpr;
	}
	/**
	 * @return the ephResultVO
	 */
	public EPHResultVO getEphResultVO() {
		return ephResultVO;
	}
	/**
	 * @param ephResultVO the ephResultVO to set
	 */
	public void setEphResultVO(EPHResultVO ephResultVO) {
		this.ephResultVO = ephResultVO;
	}
		
	/**
	 * @return the numberOfProduct
	 */
	public long getNumberOfProduct() {
		return numberOfProduct;
	}
	/**
	 * @param numberOfProduct the numberOfProduct to set
	 */
	public void setNumberOfProduct(long numberOfProduct) {
		this.numberOfProduct = numberOfProduct;
	}
	
	/**
	 * @return the autoSuggestedKeyword
	 */
	public String getAutoSuggestedKeyword() {
		return autoSuggestedKeyword;
	}
	/**
	 * @param autoSuggestedKeyword the autoSuggestedKeyword to set
	 */
	public void setAutoSuggestedKeyword(String autoSuggestedKeyword) {
		this.autoSuggestedKeyword = autoSuggestedKeyword;
	}
	/**
	 * @return the isAutoSuggested
	 */
	public boolean isAutoSuggested() {
		return isAutoSuggested;
	}
	/**
	 * @param isAutoSuggested the isAutoSuggested to set
	 */
	public void setAutoSuggested(boolean isAutoSuggested) {
		this.isAutoSuggested = isAutoSuggested;
	}
	/**
	 * @return the ephQueryScheme
	 */
	public String getEphQueryScheme() {
		return ephQueryScheme;
	}
	/**
	 * @param ephQueryScheme the ephQueryScheme to set
	 */
	public void setEphQueryScheme(String ephQueryScheme) {
		this.ephQueryScheme = ephQueryScheme;
	}
	/**
	 * @return the ephFilterString
	 */
	public String getEphFilterString() {
		return ephFilterString;
	}
	/**
	 * @param ephFilterString the ephFilterString to set
	 */
	public void setEphFilterString(String ephFilterString) {
		this.ephFilterString = ephFilterString;
	}
	/**
	 * @return the boostingLogs
	 */
	public String getBoostingLogs() {
		return boostingLogs.toString();
	}
	/**
	 * @param boostingLogs the boostingLogs to set
	 */
	public void setBoostingLogs(String boostingLogs) {
		this.boostingLogs = new StringBuilder(boostingLogs);
	}
	
	/**
	 * @param boostingLogs the boostingLogs to set
	 */
	public void appendBoostingLogs(String boostingLogs) {
		this.boostingLogs = this.boostingLogs.append(" | ").append(boostingLogs);
	}
	/**
	 * @return the isEPHFound
	 */
	public boolean isEPHFound() {
		return isEPHFound;
	}
	/**
	 * @param isEPHFound the isEPHFound to set
	 */
	public void setEPHFound(boolean isEPHFound) {
		this.isEPHFound = isEPHFound;
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
	
	 //ILD-70 | Introduced to handle extra calls to endeca in case of SWS
	 public Object clone() throws CloneNotSupportedException{
	        return (SearchQuery)super.clone();
	    }
	/**
	 * @return the checkPhantom
	 */
	public boolean isCheckPhantom() {
		return checkPhantom;
	}
	/**
	 * @param checkPhantom the checkPhantom to set
	 */
	public void setCheckPhantom(boolean checkPhantom) {
		this.checkPhantom = checkPhantom;
	}
	
	/**
	 * @return the checklistCategoryEndecaId
	 */
	public String getChecklistCategoryDimensionId() {
		return checklistCategoryDimensionId;
	}
	/**
	 * @param checklistCategoryDimensionId the checklistCategoryEndecaId to set
	 */
	public void setChecklistCategoryDimensionId(String checklistCategoryDimensionId) {
		this.checklistCategoryDimensionId = checklistCategoryDimensionId;
	}
	/**
	 * @return the fromChecklistCategory
	 */
	public boolean isFromChecklistCategory() {
		return fromChecklistCategory;
	}
	/**
	 * @param fromChecklistCategory the fromChecklistCategory to set
	 */
	public void setFromChecklistCategory(boolean fromChecklistCategory) {
		this.fromChecklistCategory = fromChecklistCategory;
	}
	/**
	 * @return the checklistId
	 */
	public String getChecklistId() {
		return checklistId;
	}
	/**
	 * @param checklistId the checklistId to set
	 */
	public void setChecklistId(String checklistId) {
		this.checklistId = checklistId;
	}
	/**
	 * @return the checklistCategoryId
	 */
	public String getChecklistCategoryId() {
		return checklistCategoryId;
	}
	/**
	 * @param checklistCategoryId the checklistCategoryId to set
	 */
	public void setChecklistCategoryId(String checklistCategoryId) {
		this.checklistCategoryId = checklistCategoryId;
	}
	/**
	 * @return the checkListSeoUrlHierarchyVo
	 */
	public CheckListSeoUrlHierarchy getCheckListSeoUrlHierarchyVo() {
		return checkListSeoUrlHierarchyVo;
	}
	
	/**
	 * @param checkListSeoUrlHierarchyVo the checkListSeoUrlHierarchyVo to set
	 */
	public void setCheckListSeoUrlHierarchyVo(CheckListSeoUrlHierarchy checkListSeoUrlHierarchyVo) {
		this.checkListSeoUrlHierarchyVo = checkListSeoUrlHierarchyVo;
	}
	
	public String getChecklistName() {
		return checklistName;
	}
	
	public void setChecklistName(String checklistName) {
		this.checklistName = checklistName;
	}
	
	public String getC1name() {
		return c1name;
	}
	
	public void setC1name(String c1name) {
		this.c1name = c1name;
	}
	
	public String getC2name() {
		return c2name;
	}
	
	public void setC2name(String c2name) {
		this.c2name = c2name;
	}
	
	public String getC3name() {
		return c3name;
	}
	
	public void setC3name(String c3name) {
		this.c3name = c3name;
	}
	
}
