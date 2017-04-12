package com.bbb.cms.droplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.query.SortCriteria;
import com.bbb.search.bean.result.CategoryParentVO;
import com.bbb.search.bean.result.FacetParentVO;
import com.bbb.search.bean.result.SearchResults;
import com.bbb.search.integration.SearchManager;
import com.bbb.utils.BBBConfigRepoUtils;

public class TBSNavFlyoutDroplet extends BBBDynamoServlet{
	
	private SearchManager pSearchManager;

	private BBBCatalogTools catalogTools;
	private final int FIX_NAV_COLUMNS = 4;
	
	private String mCategoryItemNumberKey;
	
	
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
	
	public void service(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response)
			throws javax.servlet.ServletException, java.io.IOException {
		
		if (isLoggingDebug()) {
			logDebug("Starting method CmsNavFlyoutDroplet");
		}
		
		//Performance Monitoring Code.
		final String methodName = "service";     
		
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CMS_Nav_Flyout, methodName);
		
		SearchQuery pSearchQuery = new SearchQuery();  
		String siteId = SiteContextManager.getCurrentSiteId();
		String catalogId = request.getParameter("CatalogId"); 
		String root = request.getParameter("rootCategory");
		String catalogRefId = request.getParameter("catalogRefId");
		HashMap<String, String> pCatalogRef = new HashMap<String, String>();
		
		pCatalogRef.put("catalogId", catalogId);
		pCatalogRef.put("root", root);
		pSearchQuery.setSiteId(siteId);
		pSearchQuery.setCatalogRef(pCatalogRef);

		String brandFacetDisplayName = BBBConfigRepoUtils.getStringValue("DimDisplayConfig", "Brand");
		try {
			
			// First level category fetch
			pSearchQuery.setHeaderSearch(false);
			logDebug("START: Call from Droplet to fetch Category Tree");
			Map<String,CategoryParentVO> categoryTree = this.getSearchManager().getCategoryTree(pSearchQuery);
			Object[] categoryTreeBaseMap =  categoryTree.values().toArray();
			
			int column = FIX_NAV_COLUMNS;
			Object[] categoryArray = new Object[column];
			int lenght = categoryTreeBaseMap.length;
			List<Object> tempList;

			int counter = 0;
			int rows = lenght / column;
			int extVar = lenght % column;
			int varCount = 0;
			boolean flag = true;

			while (column > 0) {
				if (lenght > counter) {
					if (extVar > 0) {
						rows++;
						extVar--;
						flag = false;

					}
					tempList = new ArrayList<Object>();
					for (int i = 0; i < rows; i++) {
						tempList.add(categoryTreeBaseMap[counter]);
						counter++;
					}
					categoryArray[varCount] = tempList;
					varCount++;

					if (flag == false) {
						rows--;
						flag = true;
					}
					
				}
				column--;
			}
			logDebug("END: Call from Droplet to fetch Category Tree");
			SearchQuery searchQuery = new SearchQuery();
			Map<String,String> catalogRef = new HashMap<String, String>();
			final SortCriteria sortCriteria = new SortCriteria();
			SearchResults brandCategory = null;
			String brandQuery = null;
			
			try {
				
				//ATG/Endeca Upgrade fix - for indicating search is for brand facets 
				//and adding Brand dimension id to expose query param
				catalogId += " " + catalogRefId;
				catalogRef.put("searchType","TopNav_Brand_Search");
				catalogRefId += " " + this.getSearchManager().getCatalogId("Brand",null);
				//ATG/Endeca Upgrade fix
				
				catalogRef.put("catalogId", catalogId);
				catalogRef.put("catalogRefId", catalogRefId);
				
				searchQuery.setSiteId(siteId);
				searchQuery.setCatalogRef(catalogRef);
				searchQuery.setSortCriteria(sortCriteria);
				List<String> pQueryFacets = new ArrayList<String>();
				pQueryFacets.add("BRAND");
				searchQuery.setQueryFacets(pQueryFacets);
				
				// Flag to set that Search is for Header only.
				searchQuery.setHeaderSearch(true);
				 
				brandCategory = this.getSearchManager().performSearch(searchQuery);
				for(FacetParentVO brand : brandCategory.getFacets())
				{
					if(brand.getName().equalsIgnoreCase(brandFacetDisplayName))
					{
						brandQuery = brand.getQuery();
						break;
					}
				}
				if ( isLoggingDebug() ) 
					{
						logDebug("brandCategory : "+brandCategory);
					}
				
				} catch (BBBBusinessException e) {
					if ( isLoggingDebug() ) 
						{
							logDebug("Exception Generated in Underlying Search Procedure : "+e.getMessage());
						}
				}
				catch (BBBSystemException e) {
					if ( isLoggingDebug() ) 
						{
							logDebug("Exception Generated in Underlying Search Procedure : "+e.getMessage());
						}
				}
			
			 	request.setParameter("categoryTreeMap", categoryArray);   
				request.setParameter("brandCategory", brandCategory);
				request.setParameter("brandQuery", brandQuery);
				request.serviceParameter("output", request, response);

			
			} catch (BBBBusinessException e) {
				if ( isLoggingDebug() ) 
					{
						logDebug("Exception Generated in Underlying Search Procedure : "+e.getMessage());
					}
			}
			catch (BBBSystemException e) {
				if ( isLoggingDebug() ) 
					{
						logDebug("Exception Generated in Underlying Search Procedure : "+e.getMessage());
					}
			} catch (Exception e) {
				if ( isLoggingDebug() ) 
					{
						logDebug("Exception Generated in Underlying Search Procedure : "+e.getMessage());
					}
			} finally{
				if (isLoggingDebug()) {
					{
						logDebug("Existing method CmsNavFlyoutDroplet");
					}
				}
				
			}
		
		 BBBPerformanceMonitor.end(BBBPerformanceConstants.CMS_Nav_Flyout, methodName);
				
	}
	
	
}
