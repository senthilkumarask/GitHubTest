package com.bbb.commerce.browse.droplet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.cms.LandingTemplateVO;
import com.bbb.cms.manager.LandingTemplateManager;
import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBSearchConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.personalstore.manager.PersonalStoreManager;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.result.CategoryParentVO;
import com.bbb.search.bean.result.CategoryRefinementVO;
import com.bbb.search.integration.SearchManager;
import com.bbb.utils.BBBUtility;

/**
 * Droplet to access all data related to category landing page.
 * For data related to catalog it calls catalogTools nd for CMS related data it calls LandingTemplateManager
 * Copyright 2011, Bath & Beyond, Inc. All Rights Reserved.
 * Reproduction or use of this file without explicit written consent is prohibited.
 * Created by: njai13
 * Created on: November-2011
 * @author njai13
 *
 */
public class CategoryLandingDroplet extends BBBDynamoServlet {
	private static final String SAVEDURL = "SAVEDURL";
	public final static String  PARAMETER_SUB_CATEGORY_LIST="subcategoriesList";
	public final static String  PARAMETER_LANDING_TEMPLATEVO="landingTemplateVO";
	public final static String CATEGORYL1 = "categoryL1";
	public final static String CATEGORYL2 = "categoryL2";
	public final static String CATEGORYL3 = "categoryL3";
	public final static String ROOT_CATEGORY = "RootCategory";
	public final static String CATEGORY_VO = "categoryVO";
	
	private BBBCatalogTools mCatalogTools;
	private SearchManager mSearchManager;
	private LandingTemplateManager mLandingTemplateManager;
	
	private PersonalStoreManager mPersonalStoreMgr;
	
	public SearchManager getSearchManager() {
		return mSearchManager;
	}

	public void setSearchManager(SearchManager pSearchManager) {
		this.mSearchManager = pSearchManager;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

	/**
	 * @return the landingTemplateManager
	 */
	public LandingTemplateManager getLandingTemplateManager() {
		return mLandingTemplateManager;
	}

	/**
	 * @param landingTemplateManager the landingTemplateManager to set
	 */
	public void setLandingTemplateManager(
			LandingTemplateManager landingTemplateManager) {
		this.mLandingTemplateManager = landingTemplateManager;
	}

	/**
	 * 
	 */
	public void service(DynamoHttpServletRequest request,
			DynamoHttpServletResponse response)
					throws ServletException, IOException
					{
	    String methodName = "CategoryLandingDroplet_service";
        BBBPerformanceMonitor.start(methodName );
        String siteId = request.getParameter(BBBCoreConstants.SITE_ID);
		String categoryId =request.getParameter(BBBCoreConstants.ID);
		String fetchSubCategories =request.getParameter("fetchSubCategories");
		final String catFlg;
		boolean catVar = false;
		final String subCatPlp;
		boolean subCatPlpVar = false;
		
		if(request.getParameter(BBBSearchBrowseConstants.CATEGORY_LANDING_FLAG) != null){
			 catFlg =request.getParameter(BBBSearchBrowseConstants.CATEGORY_LANDING_FLAG);
			 if(catFlg.equalsIgnoreCase(BBBCoreConstants.TRUE)){
				 catVar=true;
			 }
		}
		
		if(request.getParameter(BBBSearchBrowseConstants.PRODUCT_LISTING_FLAG) != null){
			 subCatPlp = request.getParameter(BBBSearchBrowseConstants.PRODUCT_LISTING_FLAG);
			 if(subCatPlp.equalsIgnoreCase(BBBCoreConstants.TRUE)){
				 subCatPlpVar = true;
			 }
		}		 
		
		Boolean isFirstLevelCategory;
		String categoryL1 = "";
		String categoryL2 = "";
		String categoryL3 = "";
		
		if(isLoggingDebug()) {
			logDebug(new StringBuffer("request Parameters value[categoryId=").append(categoryId).append("][siteId=").append(siteId).append("]").toString());
		}
		//If data needed is from Catalog

		List<CategoryVO> subcategories;
		try {
			final LandingTemplateVO landingTemplateVO = getCategoryContent(siteId, categoryId);
			List<CategoryVO> subCategories = null;
			if (landingTemplateVO.getCategory() != null) {
				subCategories = landingTemplateVO.getCategory().getSubCategories();
			}
			String boostParameter = "";
			StringBuffer subCategoriesIds = new StringBuffer();
			if(subCategories!=null && !subCategories.isEmpty()){
				for (CategoryVO subCategory : subCategories) {
					subCategoriesIds.append(subCategory.getCategoryId());
					subCategoriesIds.append(BBBCoreConstants.SEMICOLON);
				}
				boostParameter = subCategoriesIds.toString();
			}
			logDebug("Max L2 Category from BCC : " + landingTemplateVO.getL2CategoryCount());
			logDebug("Max L3 Category from BCC : " + landingTemplateVO.getL3CategoryCount());
			logDebug("Boost Paramter from BCC : " + boostParameter);
			
			isFirstLevelCategory = this.getCatalogTools().isFirstLevelCategory(categoryId, siteId);
			// Check if this is forcibly to be taken to PLP instead of actually checking its hierarchy in catalog. (As done on click of All tab in PLP page)
			
			Map<String, CategoryVO> parentCategoryMap = null;
			CategoryVO categoryVO = new CategoryVO();
					
			if(!subCatPlpVar){
				//Checked fetchSubCategories boolean for BAND 1253
				if((isFirstLevelCategory || catVar) && BBBCoreConstants.TRUE.equalsIgnoreCase(fetchSubCategories)){
					
					logDebug("Value for fetchSubCategories is : " + fetchSubCategories);
					subcategories = this.getSubCategories(siteId,categoryId, boostParameter);
					categoryVO = this.getCatalogTools().getCategoryDetail(siteId, categoryId,false);
					this.getCatalogTools().getBccManagedCategory(categoryVO);
					request.setParameter (CATEGORY_VO, categoryVO);
										
					logDebug("[OutParameter="+BBBCoreConstants.OPARAM);
					request.setParameter (PARAMETER_SUB_CATEGORY_LIST, subcategories);
					request.serviceParameter (BBBCoreConstants.OPARAM, request, response);
					
				} else if(isFirstLevelCategory || catVar){
					
					logDebug("Value for fetchSubCategories is : " + fetchSubCategories);
					
					parentCategoryMap = this.getCatalogTools().getParentCategory(categoryId, siteId);
					if (parentCategoryMap != null) {
						for (int count = 0; count < parentCategoryMap.size(); count++) {
							CategoryVO category = parentCategoryMap.get(String.valueOf(count)); 
							if (category != null) {
								if (count == 0) {
									categoryL1 = category.getCategoryName();
								}
								if (count == 1) {
									categoryL2 = category.getCategoryName();
								}
							}
						}
					}
					categoryVO = this.getCatalogTools().getCategoryDetail(siteId, categoryId,false);
					
					//included as part of Release 2.1 implementation
					this.getCatalogTools().getBccManagedCategory(categoryVO);
					
					if (categoryVO != null) {
						categoryL3 = categoryVO.getCategoryName();
						if(categoryL1 != null && categoryL1.equalsIgnoreCase(categoryL3)) {
							categoryL3 = "";
						}
					}
					
					//R2.2 Product Comparison Page - 178 A4.Setting last navigated PLP url to cookie : Start
					if(categoryVO != null){
						setUrlToCookie(request, response, categoryVO);
					}
					//R2.2 Product Comparison Page - 178 A4.Setting last navigated PLP url to cookie : End
					
					logDebug("[OutParameter="+BBBCoreConstants.OPARAM);
					request.setParameter (CATEGORYL1, categoryL1);
					request.setParameter (CATEGORYL2, categoryL2);
					request.setParameter (CATEGORYL3, categoryL3);	
					request.setParameter (CATEGORY_VO, categoryVO);
					//request.setParameter (PARAMETER_SUB_CATEGORY_LIST, subcategories);
					request.setParameter (PARAMETER_LANDING_TEMPLATEVO, landingTemplateVO);
					request.serviceParameter (BBBCoreConstants.OPARAM, request, response);
					
					
				}
				else{
					categoryVO = this.getCatalogTools().getCategoryDetail(siteId, categoryId,false);
					
					//R2.2 Product Comparison Page - 178 A4.Setting last navigated PLP url to cookie : Start
					if(categoryVO != null){
						setUrlToCookie(request, response, categoryVO);
					}
					//R2.2 Product Comparison Page - 178 A4.Setting last navigated PLP url to cookie : End
					
					//included as part of Release 2.1 implementation
					this.getCatalogTools().getBccManagedCategory(categoryVO);									

					request.setParameter (CATEGORY_VO, categoryVO);
					logDebug("[OutParameter="+BBBSearchBrowseConstants.OPARAM_SUBCAT);
					request.serviceParameter (BBBSearchBrowseConstants.OPARAM_SUBCAT, request, response);
					
					// Check for category cookie
					String categoryType = null;
					if(null == categoryVO.getSubCategories() || categoryVO.getSubCategories().size() == 0){
						categoryType = BBBCoreConstants.CATEGORY_L3;
					}else {
						categoryType = BBBCoreConstants.CATEGORY_L2;
					}
					
					// Create category cookie for Personal Store
					if(!BBBUtility.isEmpty(categoryType)){
						getPersonalStoreMgr().createCategoryCookie(request, response, categoryVO.getCategoryId(), categoryType);
					}
					
				}
			}
			else{
				categoryVO = this.getCatalogTools().getCategoryDetail(siteId, categoryId,false);
				
				//R2.2 Product Comparison Page - 178 A4.Setting last navigated PLP url to cookie : Start
				if(categoryVO != null){
					setUrlToCookie(request, response, categoryVO);
				}
				//R2.2 Product Comparison Page - 178 A4.Setting last navigated PLP url to cookie : End
				
				//included as part of Release 2.1 implementation
				this.getCatalogTools().getBccManagedCategory(categoryVO);				

				request.setParameter (CATEGORY_VO, categoryVO);
				logDebug("[OutParameter="+BBBSearchBrowseConstants.OPARAM_SUBCAT);
				request.serviceParameter (BBBSearchBrowseConstants.OPARAM_SUBCAT, request, response);
			}
			
		} catch (BBBSystemException e) {

			logError(LogMessageFormatter.formatMessage(request, "System Exception [Category Landing] from service of CategoryLandingDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1024),e);
			request.setParameter(BBBCoreConstants.ERROR, BBBCoreErrorConstants.ERROR_CAT_LANDING);
			request.serviceLocalParameter(BBBCoreConstants.ERROR, request, response);
			response.setStatus(404);
		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(request, "Business Exception [Category Landing] from service of CategoryLandingDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1025),e);
			request.setParameter(BBBCoreConstants.ERROR, BBBCoreErrorConstants.ERROR_CAT_LANDING);
			request.serviceLocalParameter(BBBCoreConstants.ERROR, request, response);
			response.setStatus(404);
		}
		
		BBBPerformanceMonitor.end(methodName );

					}		
	/**
	 * R2.2 178-A4. Product Comparison Page.
	 * This method is used to add the last viewed PLP url to the cookie.
	 * 
	 * @param request
	 * @param response
	 * @param categoryVO
	 */
	private void setUrlToCookie(DynamoHttpServletRequest request,
			DynamoHttpServletResponse response, CategoryVO categoryVO) {
		
		String lastViewedPlpUrl = categoryVO.getSeoURL();
		if(!BBBUtility.isEmpty(lastViewedPlpUrl)){
			lastViewedPlpUrl = request.getContextPath() + lastViewedPlpUrl;
			if(categoryVO.getIsCollege() != null && categoryVO.getIsCollege()){
				lastViewedPlpUrl += "?fromCollege=true";
			}
			String domain=request.getServerName();
			String path="/";
			logDebug("path to set :" + path);
			logDebug("domain to set:" + domain);

			Cookie cookie = new Cookie(SAVEDURL, lastViewedPlpUrl);
			cookie.setDomain(domain);
			cookie.setPath(path);
			BBBUtility.addCookie(response, cookie, true);
		}		
	}

	/**
	 * Method to get subcategories of the current Category
	 * @param siteId
	 * @param categoryId
	 * @return List <CategoryVO> 
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */

	public List <CategoryVO> getSubCategories(final String siteId,final String categoryId, String boostParam) throws BBBSystemException, BBBBusinessException{
		
		logDebug("Entering getSubCategories method");
		List<CategoryVO> pCategoryVOList = new ArrayList<CategoryVO>();
		CategoryVO pCatVO = null;
		SearchQuery pSearchQuery = new SearchQuery(); 
		// Get the root category of this Site.
		String root = null;
		if(null != this.getCatalogTools().getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, siteId+ROOT_CATEGORY)){
			root = this.getCatalogTools().getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, siteId+ROOT_CATEGORY).get(0);
		}
		HashMap<String, String> pCatalogRef = new HashMap<String, String>();
		pCatalogRef.put(BBBSearchConstants.CATALOG_ID, categoryId);
		pCatalogRef.put(BBBSearchConstants.ROOT_CATALOG_ID, root);
		if(!BBBUtility.isEmpty(boostParam)){
			pCatalogRef.put(BBBSearchConstants.BOOST_PARAM, boostParam);
			pSearchQuery.setFromCategoryLanding(true);
		}
		pSearchQuery.setSiteId(siteId);
		pSearchQuery.setCatalogRef(pCatalogRef);
		//BBBSL-11385 | Setting phantom flag as true
		pSearchQuery.setCheckPhantom(true);
		logDebug("setting checkPhantom flag as true.");

		try {
			pSearchQuery.setHeaderSearch(false);
			logDebug("START: Call to fetch Sub Category List");
			Map<String,CategoryParentVO> categoryTree = this.getSearchManager().getCategoryTree(pSearchQuery);
			//System.out.println("Category id: " +pSearchQuery.getCatalogRef().get("catalogId")+" -- Returned VO : "+categoryTree +" --");
			
			if(null != categoryTree){
				for(String key:categoryTree.keySet()){
					pCatVO = new CategoryVO();
					pCatVO.setCategoryId(key);
					pCatVO.setCategoryName(categoryTree.get(key).getName());
					CategoryVO pSubCatVO = null;
					if(null != categoryTree.get(key).getCategoryRefinement()){
						List<CategoryVO> pSubCatVOList = new ArrayList<CategoryVO>();
						for(CategoryRefinementVO pCatRefVO :categoryTree.get(key).getCategoryRefinement()){
							pSubCatVO = new CategoryVO();
							pSubCatVO.setCategoryId(pCatRefVO.getQuery());
							pSubCatVO.setCategoryName(pCatRefVO.getName());
							pSubCatVOList.add(pSubCatVO);
						}
						pCatVO.setSubCategories(pSubCatVOList);
					}
					pCategoryVOList.add(pCatVO);
				}
				return pCategoryVOList;
			}
			else{
				logDebug("Sub Category List Obtained from Endeca is empty. Hence, Fetching the same from Catalog.");
				return getCatalogTools().getCategoryDetail(siteId,categoryId,false).getSubCategories();
			}
		}
		catch (BBBBusinessException e) {
			logError("BBBBusinessException Generated in Underlying Search Procedure : "+e.getMessage());
			return null;
		}
		catch (BBBSystemException e) {
			logError("BBBSystemException Generated in Underlying Search Procedure : "+e.getMessage());
			return null;
		} catch (Exception e) {
			logError("Some Exception Generated in Underlying Search Procedure : "+e.getMessage());
			return null;
		} finally{
			if(isLoggingDebug()){
				logDebug("Exiting method getSubCategories");
			}
		}
	}

	/**
	 * Method to get LandingTemplateVO of the current Category
	 * @param categoryId
	 * @return
	 */
	public LandingTemplateVO getCategoryContent(final String siteId,final String categoryId){
		return getLandingTemplateManager().getLandingTemplateData(BBBSearchBrowseConstants.CATEGORY_LANDING_TEMPLATE,categoryId,siteId);
	}
	

	/**
	 * @return the personalStoreMgr
	 */
	public PersonalStoreManager getPersonalStoreMgr() {
		return mPersonalStoreMgr;
	}

	/**
	 * @param pPersonalStoreMgr the personalStoreMgr to set
	 */
	public void setPersonalStoreMgr(PersonalStoreManager pPersonalStoreMgr) {
		mPersonalStoreMgr = pPersonalStoreMgr;
	}


}