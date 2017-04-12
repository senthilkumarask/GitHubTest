	package com.bbb.cms.droplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.result.CategoryParentVO;
import com.bbb.search.bean.result.FacetParentVO;
import com.bbb.search.bean.result.PromoVO;
import com.bbb.search.bean.result.SearchResults;
import com.bbb.search.endeca.EndecaSearch;
import com.bbb.search.integration.SearchManager;
import com.bbb.utils.BBBConfigRepoUtils;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class CmsNavFlyoutDroplet extends BBBDynamoServlet{

	private SearchManager pSearchManager;

	private BBBCatalogTools catalogTools;

	private String mCategoryItemNumberKey;
	int MAX_ELEMENT=7;

	/**
	 * @return the categoryItemNumberKey
	 */
	public String getCategoryItemNumberKey() {
		return mCategoryItemNumberKey;
	}
	/**
	 * @param categoryItemNumberKey the categoryItemNumberKey to set
	 */
	public void setCategoryItemNumberKey(String pCategoryItemNumberKey) {
		this.mCategoryItemNumberKey = pCategoryItemNumberKey;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}
	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	public SearchManager getSearchManager() {
		return pSearchManager;
	}
	public void setSearchManager(SearchManager mSearchManager) {
		this.pSearchManager = mSearchManager;
	}

	/**
	 * Service method used to get all categories.
	 * @param DynamoHttpServletRequest request
	 * @param DynamoHttpServletResponse response
	 *
	 */
	public void service(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response)
			throws javax.servlet.ServletException, java.io.IOException {

		
			logDebug("Starting method CmsNavFlyoutDroplet");

		
		//Performance Monitoring Code.
		final String methodName = "service";

		BBBPerformanceMonitor.start(BBBPerformanceConstants.CMS_Nav_Flyout, methodName);

		SearchQuery pSearchQuery = new SearchQuery();
		String siteId = request.getParameter(BBBCoreConstants.SITE_ID);
		if (siteId == null) {
			siteId = SiteContextManager.getCurrentSiteId();
		}
		String catalogId = request.getParameter("CatalogId");
		String root = request.getParameter("rootCategory");
		String catalogRefId = request.getParameter("catalogRefId");
		HashMap<String, String> pCatalogRef = new HashMap<String, String>();
		pCatalogRef.put("catalogId", catalogId);
		pCatalogRef.put("root", root);
		pSearchQuery.setSiteId(siteId);
		pSearchQuery.setCatalogRef(pCatalogRef);
		String brandFacetName =  getStringValue();
		try {

			// First level category fetch
			pSearchQuery.setHeaderSearch(false);
			logDebug("START: Call from Droplet to fetch Category Tree");
			Map<String,CategoryParentVO> categoryTree = this.getSearchManager().getCategoryTree(pSearchQuery);

			if (categoryTree != null) {
			 categoryTree = this.getSearchManager().filterL2L3ExclusionItemsFromCategoryTree(catalogRefId, categoryTree);
			}

			Object[] categoryTreeArray = (categoryTree != null) ? (categoryTree.values().toArray()) : null;
				ArrayList<Object> categoryArray=new ArrayList<Object>();
				int catIndex=0;
				while (catIndex < BBBCmsConstants.MAX_CATEGORY_COUNT)
				{
					ArrayList<Object> catList = new ArrayList<Object>();
						if (catIndex >= categoryTreeArray.length || catIndex > (BBBCmsConstants.MAX_CATEGORY_COUNT)){
							break;
						}
						catList.add(categoryTreeArray[catIndex]);
						catIndex++;
					if(catList.size()>=0){
					categoryArray.add(catList);
					}
				}
				// Chnges made for "SSW - Nav Cleanup"

			logDebug("END: Call from Droplet to fetch Category Tree");
			
			//REPLACE BELOW LOGIC
			SearchResults browseSearchVO = (SearchResults)request.getObjectParameter("browseSearchVO");
			
			
			SearchResults brandCategory = null;
			String brandQuery = null;

				brandCategory = createResponseForCategory(browseSearchVO, catalogRefId);
				
				for(FacetParentVO brand : brandCategory.getFacets())
				{
					if(brand.getName().equalsIgnoreCase(brandFacetName))
					{
						brandQuery = brand.getQuery();
						break;
					}
				}

				
				logDebug("brandCategory : "+brandCategory);
					

			 	request.setParameter("categoryTreeMap", categoryArray);
				request.setParameter("brandCategory", brandCategory);
				request.setParameter("brandQuery", brandQuery);
				request.serviceParameter("output", request, response);


			} catch (BBBBusinessException e) {
				
					logError("Exception Generated in Underlying Search Procedure : "+e.getMessage());
				

			}
			catch (BBBSystemException e) {
				
					logError("Exception Generated in Underlying Search Procedure : "+e.getMessage());
					
			} catch (Exception e) {
				
					logError("Exception Generated in Underlying Search Procedure : ", e);
					
			} 
					
		
		  logDebug("Existing method CmsNavFlyoutDroplet");				
			

		 BBBPerformanceMonitor.end(BBBPerformanceConstants.CMS_Nav_Flyout, methodName);

	}
	
	private SearchResults createResponseForCategory(SearchResults pOrigBrowseSearchVO, String pCatalogRefId){
		//add category promos
		if(pOrigBrowseSearchVO.getPromoMap() != null){
			if(StringUtils.isNotEmpty(pCatalogRefId)){
				List<PromoVO> promos = pOrigBrowseSearchVO.getPromoMap().get(pCatalogRefId);
				if(promos != null && !promos.isEmpty()){
					pOrigBrowseSearchVO.getPromoMap().put("TOP", promos);
				}else{
					clearPromoMapEntry(pOrigBrowseSearchVO);
				}
			}else{
				clearPromoMapEntry(pOrigBrowseSearchVO);
			}
		}
		return pOrigBrowseSearchVO;
	}
	
	protected void clearPromoMapEntry(SearchResults pOrigBrowseSearchVO){
		if(pOrigBrowseSearchVO.getPromoMap() != null){
			pOrigBrowseSearchVO.getPromoMap().put("TOP", null);
		}
	}
	protected String getStringValue() {
		return BBBConfigRepoUtils.getStringValue(EndecaSearch.DIM_DISPLAY_CONFIGTYPE, EndecaSearch.BRAND_KEY);
		}
}
