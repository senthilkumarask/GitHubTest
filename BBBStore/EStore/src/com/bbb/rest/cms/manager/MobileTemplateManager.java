package com.bbb.rest.cms.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import atg.multisite.SiteContextManager;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.ServletUtil;

import com.bbb.cms.CategoryLandingContentVO;
import com.bbb.cms.ContentBoxVO;
import com.bbb.cms.HomePageContentVO;
import com.bbb.cms.LinkVO;
import com.bbb.cms.NavigationLinksVO;
import com.bbb.cms.PromoContainerVO;
import com.bbb.cms.StaticTemplateContentVO;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.rest.cms.tools.RestTemplateTools;
import com.bbb.utils.BBBUtility;

public class MobileTemplateManager extends BBBGenericService {
	

	private Repository mStaticTemplateRepository;
	private Repository mHomePageTemplateRepository;
	private Repository mNavLinksTemplateRepository;
	
	private Repository mCategoryTemplateRepository;
	
	private static final String LANDING_DESCRIPTOR = "catLandingWS";
	private static final String BANNER = "CategoryBanner";
	private static final String CAROUSAL = "CategoryCarousel";
	protected static final String CHANNEL_ID = "clientID";
	protected static final String DEFAULT_CHANNEL_VALUE="Web";
	private BBBCatalogTools mCatalogTools;
	private Repository mBbbManagedCatalogRepository;
	private String defaultShippingMethod; 

	/**
	 * @return the defaultShippingMethod
	 */
	public String getDefaultShippingMethod() {
		return defaultShippingMethod;
	}
	
	/**
	 * @param defaultShippingMethod to Set
	 */
	public void setDefaultShippingMethod(String defaultShippingMethod) {
		this.defaultShippingMethod = defaultShippingMethod;
	}

	
	/**
	 * @return the bbbManagedCatalogRepository
	 */
	public Repository getBbbManagedCatalogRepository() {
		return mBbbManagedCatalogRepository;
	}

	/**
	 * @param pBbbManagedCatalogRepository the bbbManagedCatalogRepository to set
	 */
	public void setBbbManagedCatalogRepository(Repository pBbbManagedCatalogRepository) {
		mBbbManagedCatalogRepository = pBbbManagedCatalogRepository;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param pCatalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	public Repository getCategoryTemplateRepository() {
		return mCategoryTemplateRepository;
	}
	
	public void setCategoryTemplateRepository(
			Repository mCategoryTemplateRepository) {
		this.mCategoryTemplateRepository = mCategoryTemplateRepository;
	}

	/**
	 * @return the mHomePageTemplateRepository
	 */
	public Repository getHomePageTemplateRepository() {
		return mHomePageTemplateRepository;
	}

	/**
	 * @param mHomePageTemplateRepository the mHomePageTemplateRepository to set
	 */
	public void setHomePageTemplateRepository(
			Repository mHomePageTemplateRepository) {
		this.mHomePageTemplateRepository = mHomePageTemplateRepository;
	}

	/**
	 * @return the mNavLinksTemplateRepository
	 */
	public Repository getNavLinksTemplateRepository() {
		return mNavLinksTemplateRepository;
	}

	/**
	 * @param mNavLinksTemplateRepository the mNavLinksTemplateRepository to set
	 */
	public void setNavLinksTemplateRepository(
			Repository mNavLinksTemplateRepository) {
		this.mNavLinksTemplateRepository = mNavLinksTemplateRepository;
	}
	/**
	 * @return the mStaticTemplateRepository
	 */
	public Repository getStaticTemplateRepository() {
		return mStaticTemplateRepository;
	}

	/**
	 * @param mStaticTemplateRepository the mStaticTemplateRepository to set
	 */
	public void setStaticTemplateRepository(Repository mStaticTemplateRepository) {
		this.mStaticTemplateRepository = mStaticTemplateRepository;
	}

	private RestTemplateTools templateTools;

	/**
	 * @return the templateTools
	 */
	public RestTemplateTools getTemplateTools() {
		return templateTools;
	}

	/**
	 * @param templateTools the templateTools to set
	 */
	public void setTemplateTools(RestTemplateTools templateTools) {
		this.templateTools = templateTools;
	}

	

	/**
	 * This method returns static page data based on siteId and pageName.
	 * 
	 * @param pSiteId
	 * @param pPageName
	 * @return RepositoryItem
	 * @throws RepositoryException
	 */
	/**
	 * This method returns static page data based on siteId and pageName.
	 * 
	 * @param pSiteId
	 * @param pPageName
	 * @return RepositoryItem
	 * @throws RepositoryException
	 */
	public RepositoryItem getStaticTemplateData(String pPageName, String pTemplateItemDescriptorName, String pSiteId)
			throws RepositoryException {
		
		
		logDebug("starting method RestStaticTemplateManager.getStaticTemplateData, Passed parameters: "
					+ ", pPageName=" + pPageName +", pTemplateItemDescriptorName=" + pTemplateItemDescriptorName);
		
		
		RepositoryItem staticPageDetail = null;
		RepositoryView view = getStaticTemplateRepository().getView(pTemplateItemDescriptorName);

		RqlStatement statement = RqlStatement.parseRqlStatement(BBBCmsConstants.REST_STATIC_TEMPLATE_QUERY);

		Object params[] = new Object[2];
		params[0] = pSiteId;
		params[1] = pPageName;

		RepositoryItem[] siteStaticPageItems = null;

		try {
			siteStaticPageItems = statement.executeQuery(view, params);
		} 
		catch (IllegalArgumentException iLLArgExp) {
			/*Code change for JIRA ticket# BBBSL-11390 Starts  */
			logDebug(LogMessageFormatter.formatMessage(null, "getStaticTemplateData:","catalog_1065" ),iLLArgExp);
			/*Code change for JIRA ticket# BBBSL-11390 Ends  */
			siteStaticPageItems = null;
		}

		if (siteStaticPageItems != null) {
			staticPageDetail = siteStaticPageItems[0];
		} else {
			
			logDebug("MobileTemplateManager.getStaticTemplateData :: null result returns for pageName " + pPageName);
			logDebug("MobileTemplateManager.getStaticTemplateData :: Executing query for OTHERS pageName and bbbPageName " + pPageName);
			
			RqlStatement newStatement = RqlStatement.parseRqlStatement(BBBCmsConstants.STATIC_NEW_TEMPLATE_QUERY);
			Object newparams[] = new Object[3];
			newparams[0] = pSiteId;
			newparams[1] = "Others";
			newparams[2] = pPageName;
			
			
			try {
				siteStaticPageItems = newStatement.executeQuery(view, newparams);
				if (siteStaticPageItems == null)
				{
					
					logDebug("MobileTemplateManager.getStaticTemplateData :: There is no page with the name" + pPageName);
					
				}
				else
				{	
					staticPageDetail = siteStaticPageItems[0];
				}
			} catch (IllegalArgumentException iLLArgExp) {
				
				
				logError(LogMessageFormatter.formatMessage(null, "getStaticTemplateData:","catalog_1066" ),iLLArgExp);
				
				siteStaticPageItems = null;
			}
		}

		
		
		logDebug("Existing method RestStaticTemplateManager.getStaticTemplateData");
		
		return staticPageDetail;
	}
	
	
	/**
	 * This is overloaded method used to get content for Static page based on pageName and also VDC shipping message based on skuId. 
	 * As of now, this service is called from PDP to get Vendor shipping details.
	 * 
	 * @param pageName page name whose static content details needs to be fetched
	 * @param pTemplateType Derive Item descriptor name based on template type
	 * @param vdcSkuId skuId corresponding to which message is derived
	 * @return static content related details for a particular page
	 * @throws BBBBusinessException exception in case any business error occurred
	 * @throws BBBSystemException exception in case any error occurred while fetching static content
	 */
	@SuppressWarnings("unchecked")
	public StaticTemplateContentVO getStaticContent(String pPageName, String pTemplateType, String vdcSkuId) throws BBBBusinessException, BBBSystemException{
		logDebug("RestStaticTemplateManager.getStaticContent : START");
	
		//Get the static content based on page name 
		StaticTemplateContentVO staticTemplateContentVO =  getStaticContent(pPageName, pTemplateType);
		String shipMethod = getDefaultShippingMethod();
	
		//Get the vdc shipping message based on skuId
		if(BBBUtility.isNotEmpty(vdcSkuId)){
			String vdcShipMessage =  getCatalogTools().getVDCShipMessage(vdcSkuId, false, shipMethod, new Date(), true);
			logDebug("RestStaticTemplateManager.getStaticContent - Set the VDC Shipping message "+ vdcShipMessage + "for skuId :" + vdcSkuId);
			staticTemplateContentVO.setVdcShipMessage(vdcShipMessage);
		}
		logDebug("RestStaticTemplateManager.getStaticContent : END");
		return staticTemplateContentVO;
	}
	
	/**
	 * This method is used to get content for Static pages based on pageName
	 * @param pageName page name whose static content details needs to be fetched
	 * @return static content related details for a particular page
	 * @throws BBBBusinessException exception in case any business error occurred
	 * @throws BBBSystemException exception in case any error occurred while fetching static content
	 */
	@SuppressWarnings("unchecked")
	public StaticTemplateContentVO getStaticContent(String pPageName, String pTemplateType) throws BBBBusinessException, BBBSystemException{
		logDebug("RestStaticTemplateManager.getStaticContent : START");
		StaticTemplateContentVO staticContentVO =null;
		try {

			final String pSiteId = SiteContextManager.getCurrentSiteId();
			logDebug("starting method RestStaticTemplateManager.getStaticContent, Passed parameters: "	+ ", pPageName=" + pPageName +", pTemplateType=" + pTemplateType +", pSiteId=" + pSiteId);
			
			if(BBBUtility.isEmpty(pSiteId)){
				logError("pSiteId is null or empty throwing BBBBusinessException");
				throw new BBBBusinessException(BBBCmsConstants.ERROR_INVALID_SITE_ID, "Site Id is null or invalid");
			}
			
			if(BBBUtility.isEmpty(pPageName)){
				logError("input parameter pageName is null or empty throwing BBBBusinessException");
				throw new BBBBusinessException(BBBCmsConstants.ERROR_NULL_INPUT_PARAM_PAGE_NAME, "Page name can not be Empty");
			}
			String pItemDescriptorName = null;
			staticContentVO = new StaticTemplateContentVO();
			// Collection item Type Static Template
			if(BBBCmsConstants.REST_STATIC_COLLECTION_TEMPLATE_TYPE.equals(pTemplateType)){
				pItemDescriptorName = BBBCmsConstants.REST_SITE_STATIC_COLLECTION_TEMPLATE;
			}
			// Modal Type Static Template
			else if(BBBCmsConstants.REST_STATIC_MODAL_TEMPLATE_TYPE.equals(pTemplateType)){
				pItemDescriptorName = BBBCmsConstants.REST_SITE_STATIC_MODAL_TEMPLATE;
			}
			// Content Type Static Template
			else{
				pItemDescriptorName = BBBCmsConstants.REST_SITE_STATIC_CONTENT_TEMPLATE;
			}

			final RepositoryItem staticTemplateData = this.getStaticTemplateData(pPageName, pItemDescriptorName, pSiteId);
			if(staticTemplateData!=null){
				
				getCommonData(staticContentVO, pSiteId, staticTemplateData);
				
				// Collection item Type Static Template
				if(BBBCmsConstants.REST_STATIC_COLLECTION_TEMPLATE_TYPE.equals(pTemplateType)){
					getCollectionData(staticContentVO, pSiteId,staticTemplateData);
					
				}
				
				// Modal Type Static Template
				else if(BBBCmsConstants.REST_STATIC_MODAL_TEMPLATE_TYPE.equals(pTemplateType)){
					getModalData(staticContentVO, staticTemplateData);
				}
				
				// Content Type Static Template
				else{
					getStaticData(staticContentVO, staticTemplateData);
				}
			}else{
				logError(" no result found for pageName "+pPageName +" and pTemplateType "+pTemplateType +" and siteId "+pSiteId+" throwing BBBBusinessException");
			//	throw new BBBBusinessException(BBBCmsConstants.ERROR_VALUE_NOT_FOUND, "No result found for pageName "+pPageName +" and pTemplateType "+pTemplateType +" and siteId "+pSiteId);
	         	staticContentVO.setErrorExist(true);
				staticContentVO.setErrorMsg("No result found for pageName "+pPageName +" and pTemplateType "+pTemplateType +" and siteId "+pSiteId);
				staticContentVO.setErrorCode(BBBCmsConstants.ERROR_VALUE_NOT_FOUND);
				return(staticContentVO);
				
			}
		} catch (RepositoryException e) {
			logError(" Repository Error in fetching data",e);
			throw new BBBSystemException(BBBCmsConstants.ERROR_FETCH_STATIC_CONTENT, "Error occurred while fetching static content");
			
		}
		logDebug("RestStaticTemplateManager.getStaticContent : END");
		return staticContentVO;
	}

	private void getCommonData(final StaticTemplateContentVO staticContentVO,
			final String pSiteId, final RepositoryItem staticTemplateData) {
		staticContentVO.setTemplateId(staticTemplateData.getRepositoryId());
		staticContentVO.setPageName((String) staticTemplateData.getPropertyValue(BBBCmsConstants.REST_STATIC_TEMPLATE_PROPERTY_PAGE_NAME));
		staticContentVO.setSeoUrl((String) staticTemplateData.getPropertyValue(BBBCmsConstants.REST_STATIC_TEMPLATE_PROPERTY_SEO_URL));
		staticContentVO.setSiteId(pSiteId);
	}

	private void getStaticData(final StaticTemplateContentVO staticContentVO,
			final RepositoryItem staticTemplateData) {
		
		staticContentVO.setPageSubHeaderCopy((String) staticTemplateData.getPropertyValue(BBBCmsConstants.REST_STATIC_CONTENT_TEMPLATE_PROPERTY_PAGE_SUBHEADER_COPY));
		staticContentVO.setPageTitle((String) staticTemplateData.getPropertyValue(BBBCmsConstants.REST_STATIC_CONTENT_TEMPLATE_PROPERTY_PAGE_TITLE));
		staticContentVO.setPageCopy((String) staticTemplateData.getPropertyValue(BBBCmsConstants.REST_STATIC_COLLECTION_TEMPLATE_PROPERTY_PAGE_COPY));					
		staticContentVO.setBbbPageName((String) staticTemplateData.getPropertyValue(BBBCmsConstants.REST_STATIC_COLLECTION_TEMPLATE_PROPERTY_BBB_PAGE_NAME));
		staticContentVO.setOmnitureData((Map) staticTemplateData.getPropertyValue(BBBCmsConstants.TYPE_OMNITUREDATA));
		staticContentVO.setHeadTagContent((String) staticTemplateData.getPropertyValue(BBBCmsConstants.REST_STATIC_CONTENT_TEMPLATE_HEAD_TAG_CONTENT));
		staticContentVO.setBodyEndTagContent((String) staticTemplateData.getPropertyValue(BBBCmsConstants.REST_STATIC_CONTENT_TEMPLATE_BODY_ENDTAG_CONTENT));
		staticContentVO.setRegistryRibbonFlag((boolean)staticTemplateData.getPropertyValue(BBBCmsConstants.REST_STATIC_CONTENT_TEMPLATE_REGISTRY_RIBBON_FLAG));
		// Get Carousel Item
		RepositoryItem carousel = (RepositoryItem) staticTemplateData.getPropertyValue(BBBCmsConstants.REST_STATIC_CONTENT_TEMPLATE_PROPERTY_CAROUSEL);
		if(null != carousel){
			staticContentVO.setCarouselVO(getTemplateTools().getCarouselVO(carousel));
		}
		
		// Get Page Content /Text Areas
		List<RepositoryItem> pageContents = (List<RepositoryItem>) staticTemplateData.getPropertyValue(BBBCmsConstants.REST_STATIC_CONTENT_TEMPLATE_PROPERTY_PAGE_CONTENTS);
		if(null != pageContents && !pageContents.isEmpty()){
			List<ContentBoxVO> pList = new ArrayList<ContentBoxVO>();
			for(RepositoryItem pRepItem: pageContents){
				pList.add(getTemplateTools().getContentBoxVO(pRepItem));
			}
			staticContentVO.setPageContent(pList);
		}
	}

	private void getModalData(final StaticTemplateContentVO staticContentVO,
			final RepositoryItem staticTemplateData) {
		staticContentVO.setPageMessage((String) staticTemplateData.getPropertyValue(BBBCmsConstants.REST_STATIC_MODAL_TEMPLATE_PROPERTY_PAGE_MESSAGE));
		staticContentVO.setPageTitle((String) staticTemplateData.getPropertyValue(BBBCmsConstants.REST_STATIC_MODAL_TEMPLATE_PROPERTY_PAGE_TITLE));
		staticContentVO.setOmnitureData((Map) staticTemplateData.getPropertyValue(BBBCmsConstants.MODAL_OMNITUREDATA));
		// Get Promo Item
		RepositoryItem promoItem = (RepositoryItem) staticTemplateData.getPropertyValue(BBBCmsConstants.REST_STATIC_MODAL_TEMPLATE_PROPERTY_PROMOBOX);
		if(null != promoItem){
			staticContentVO.setImageBox(getTemplateTools().getPromoBoxVO(promoItem));
		}
	}

	@SuppressWarnings("unchecked")
	private void getCollectionData(final StaticTemplateContentVO staticContentVO,
			final String pSiteId, final RepositoryItem staticTemplateData)
			throws BBBSystemException, BBBBusinessException {
		
		staticContentVO.setPageHeaderCopy((String) staticTemplateData.getPropertyValue(BBBCmsConstants.REST_STATIC_COLLECTION_TEMPLATE_PROPERTY_PAGE_HEADER_COPY));
		staticContentVO.setPageCopy((String) staticTemplateData.getPropertyValue(BBBCmsConstants.REST_STATIC_COLLECTION_TEMPLATE_PROPERTY_PAGE_COPY));
		staticContentVO.setBbbPageName((String) staticTemplateData.getPropertyValue(BBBCmsConstants.REST_STATIC_COLLECTION_TEMPLATE_PROPERTY_BBB_PAGE_NAME));
		// Get Carousel Item
		RepositoryItem promoItem = (RepositoryItem) staticTemplateData.getPropertyValue(BBBCmsConstants.REST_STATIC_COLLECTION_TEMPLATE_PROPERTY_CAROUSEL);
		if(null != promoItem){
			staticContentVO.setImageBox(getTemplateTools().getPromoBoxVO(promoItem));
		}
		
		// Get Banner Item
		RepositoryItem banner = (RepositoryItem) staticTemplateData.getPropertyValue(BBBCmsConstants.REST_STATIC_COLLECTION_TEMPLATE_PROPERTY_PROMOBOX);
		if(null != banner){
			staticContentVO.setBanner(getTemplateTools().getBannerVO(banner));
		}

		// Get Product Listing
		List<RepositoryItem> productItems = (List<RepositoryItem>) staticTemplateData.getPropertyValue(BBBCmsConstants.REST_STATIC_COLLECTION_TEMPLATE_PROPERTY_PRODUCT_LIST);
		if(null != productItems && !productItems.isEmpty()){
			for(RepositoryItem pRepItem: productItems){
				staticContentVO.getProductList().add(getTemplateTools().getProductCarouselVO(pRepItem,pSiteId));
			}
		}
		
		//Get noOfProductsInProductCarousel
		staticContentVO.setNoOfProductsInProductCarousel((String) staticTemplateData.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_NO_OF_PRODUCTS_IN_CAROUSAL));
	}
	

	/**
	 * @param categoryId : used to get the template associated with passed category
	 * @return CategoryLandingVO : contains the value of the template
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public CategoryLandingContentVO getCategoryPageContent(final String categoryId ) throws BBBBusinessException, BBBSystemException{
		logDebug("LandingTemplateManager.getCategoryPageContent() : START");
		RepositoryView view = null;
		QueryBuilder queryBuilder;
		QueryExpression queryCategoryId;
		QueryExpression queryExpCategoryId;
		RepositoryItem[] items = null;
		final Query queryLanding;
		final CategoryLandingContentVO landingTemplateVO =new CategoryLandingContentVO();
		try { 
			final String pSiteId = SiteContextManager.getCurrentSiteId();
			if(BBBUtility.isEmpty(pSiteId)){
				logError("pSiteId is null or empty throwing BBBBusinessException");
				landingTemplateVO.setErrorExist(true);
				landingTemplateVO.setErrorCode(BBBCatalogErrorCodes.INVALID_INPUT_PROVIDED);
				landingTemplateVO.setErrorMessage(BBBCmsConstants.ERR_INVALID_SITE_ID);
				return landingTemplateVO;
				//throw new BBBBusinessException(BBBCmsConstants.ERROR_INVALID_SITE_ID, "Site Id is null or invalid");
			}
			if(BBBUtility.isEmpty(categoryId)){
				logError("categoryId is null");
				landingTemplateVO.setErrorExist(true);
				landingTemplateVO.setErrorCode(BBBCatalogErrorCodes.INVALID_INPUT_PROVIDED);
				landingTemplateVO.setErrorMessage(BBBCatalogConstants.INVALID_CATEGORY);
				return landingTemplateVO;
				//throw new BBBBusinessException(BBBCmsConstants.ERROR_NULL_INPUT_PARAM_CATEGORY_ID, "required parameter cannot be empty");
			}
			logDebug("LandingTemplateManager.getLandingTemplateData() Method Entering");
			view = getCategoryTemplateRepository().getView(LANDING_DESCRIPTOR);
			queryBuilder = view.getQueryBuilder();
			queryExpCategoryId = queryBuilder.createPropertyQueryExpression("category");
			queryCategoryId = queryBuilder.createConstantQueryExpression(categoryId);

			final Query[] queries = new Query[2];
			queries[0] = siteIdQuery(pSiteId, queryBuilder);
			queries[1] = queryBuilder.createComparisonQuery(queryExpCategoryId, queryCategoryId, QueryBuilder.EQUALS);
			queryLanding= queryBuilder.createAndQuery(queries);

			logDebug("Landing Query to retrieve data : "+queryLanding);

			items = view.executeQuery(queryLanding);
			if(items !=null && items.length > 0){
				RepositoryItem item = items[0];
				landingTemplateVO.setTemplateId((String)item.getPropertyValue("id"));
				landingTemplateVO.setSiteId(pSiteId);
				RepositoryItem bannerItem = (RepositoryItem) item.getPropertyValue(BANNER);
				if(bannerItem != null){
					landingTemplateVO.setBannerVO(getTemplateTools().getBannerVO(bannerItem));
				}
				RepositoryItem carouselItem = (RepositoryItem) item.getPropertyValue(CAROUSAL);
				if(carouselItem != null){
					landingTemplateVO.setCarouselVO(getTemplateTools().getCarouselVO(carouselItem));  
				}
				
				RepositoryItem categoryItem = (RepositoryItem) item.getPropertyValue(BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
				if(categoryItem != null){
					landingTemplateVO.setCategoryName((String)categoryItem.getPropertyValue(BBBCoreConstants.DISPLAY_NAME));
				}
				
			}else{
				logError("No result found for siteId "+pSiteId+" throwing BBBBusinessException");
				landingTemplateVO.setErrorExist(true);
				landingTemplateVO.setErrorCode(BBBCatalogErrorCodes.DATA_NOT_FOUND);
				landingTemplateVO.setErrorMessage(BBBCmsConstants.DATA_NOT_FOUND);
				//throw new BBBBusinessException(BBBCmsConstants.ERROR_VALUE_NOT_FOUND, "Value not found");
			}
		} catch (RepositoryException e) {
			logError(" Repository Error in fetching data",e);
			landingTemplateVO.setErrorExist(true);
			landingTemplateVO.setErrorMessage(BBBCatalogConstants.CATEGORY_UNAVAILABLE);
			landingTemplateVO.setErrorCode(BBBCatalogErrorCodes.NOT_ABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY);
			//throw new BBBSystemException(BBBCmsConstants.ERROR_FETCH_CATEGORYPAGE_CONTENT, "Error occurred while fetching categorypage content");
		}
		logDebug("LandingTemplateManager.getCategoryPageContent() : END");
		return landingTemplateVO;
	}	
	
	private Query siteIdQuery(final String pSiteId, final QueryBuilder pQueryBuilder) throws RepositoryException {

		logDebug("LandingTemplateManager.siteIdQuery() Method Entering");

		final QueryExpression expSiteId = pQueryBuilder.createPropertyQueryExpression("site");
		final QueryExpression siteIdParam = pQueryBuilder.createConstantQueryExpression(pSiteId);
		final Query includesQuery = pQueryBuilder.createComparisonQuery(expSiteId, siteIdParam,QueryBuilder.EQUALS);

		logDebug("LandingTemplateManager.siteIdQuery() Method Ending");

		return includesQuery;
	}
	

	/**
	 * This method returns static page data based on siteId and pageName.
	 * 
	 * @param pSiteId
	 * @param pPageName
	 * @return RepositoryItem
	 * @throws RepositoryException
	 */
	public RepositoryItem getHomePageTemplateData(String pSiteId)
			throws RepositoryException {
		
		
		logDebug("starting method RestHomePageTemplateManager.getHomePageTemplateData, Passed parameters: "
					+ ", pTemplateItemDescriptorName=" + BBBCmsConstants.REST_SITE_HOMEPAGE_TEMPLATE);
		
		
		RepositoryItem homePagePageDetail = null;
		RepositoryView view = getHomePageTemplateRepository().getView(BBBCmsConstants.REST_SITE_HOMEPAGE_TEMPLATE);

		RqlStatement statement = RqlStatement.parseRqlStatement(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_QUERY);
		BBBSessionBean sessionBean = (BBBSessionBean)ServletUtil.getCurrentRequest().resolveName("/com/bbb/profile/session/SessionBean");
		Boolean isInternationalUser =(Boolean) sessionBean.isInternationalShippingContext();
		Object params[] = new Object[2];
		params[0] = pSiteId;
		params[1] = isInternationalUser;
 		

		RepositoryItem[] siteHomePageItems = null;

		try {
			siteHomePageItems = statement.executeQuery(view, params);
		} 
		catch (IllegalArgumentException iLLArgExp) {
			
			logError(LogMessageFormatter.formatMessage(null, "getHomePageTemplateData:","catalog_1065" ),iLLArgExp);
			
			siteHomePageItems = null;
		}

		if (siteHomePageItems != null) {
			homePagePageDetail = siteHomePageItems[0]; 
		} else {
			params[1] = false;
			siteHomePageItems = statement.executeQuery(view, params);
			homePagePageDetail = siteHomePageItems[0];
		}
		
		logDebug("Existing method RestHomePageTemplateManager.getHomePageTemplateData");
		
		return homePagePageDetail;
	}
	
	/**
	 * This method is used to get content for Home page
	 * @return Home Page content related details 
	 * @throws BBBBusinessException exception in case any business error occurred
	 * @throws BBBSystemException exception in case any error occurred while fetching static content
	 */
	@SuppressWarnings("unchecked")
	public HomePageContentVO getHomePageContent() throws BBBBusinessException, BBBSystemException{
		logDebug("RestHomePageContentVO.getHomePageContent : START");
		final HomePageContentVO homePageContentVO = new HomePageContentVO();
		try {

			final String pSiteId = SiteContextManager.getCurrentSiteId();
			logDebug("starting method RestHomePageContentVO.getHomePageContent, Passed parameters: "	+ ", pSiteId=" + pSiteId);
			
			if(BBBUtility.isEmpty(pSiteId)){
				logError("pSiteId is null or empty throwing BBBBusinessException");
				throw new BBBBusinessException(BBBCmsConstants.ERROR_INVALID_SITE_ID, "Site Id is null or invalid");
			}
			else{
				
				final RepositoryItem homePageTemplateData = this.getHomePageTemplateData(pSiteId);
				if(homePageTemplateData!=null){
					
					//sets RestHomePageContentVO details
					homePageContentVO.setTemplateId(homePageTemplateData.getRepositoryId());
					homePageContentVO.setSiteId(pSiteId);
					
					// Get Carousel Item
					RepositoryItem carousel = (RepositoryItem) homePageTemplateData.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROPERTY_CAROUSEL);
					if(null != carousel){
						homePageContentVO.setCarouselVO(getTemplateTools().getCarouselVO(carousel));
					}
					
					// Get Banner Item
					RepositoryItem banner = (RepositoryItem) homePageTemplateData.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROPERTY_BANNER);
					if(null != banner){
						homePageContentVO.setBannerVO(getTemplateTools().getBannerVO(banner));
					}
					
					// Get ProductCarouselVO Item
					RepositoryItem productCarousel = (RepositoryItem) homePageTemplateData.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROPERTY_PRODUCT_CAROUSEL);
					if(null != productCarousel){
						homePageContentVO.setProductCarouselVO(getTemplateTools().getProductCarouselVO(productCarousel,pSiteId));
					}
					
					// Get CategoryContainerVO Item
					RepositoryItem categoryContainer = (RepositoryItem) homePageTemplateData.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROPERTY_CATEGORY_CAROUSEL);
					if(null != categoryContainer){
						homePageContentVO.setCategoryContainerVO(getTemplateTools().getCategoryContainerVO(categoryContainer,pSiteId));
					}
					
					// Get homePromoTierLayOut1 Item
					List<RepositoryItem> homePromoTierLayOut1 = (List<RepositoryItem>) homePageTemplateData.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROPERTY_PROMO_LAYOUT1);
					
					if(null != homePromoTierLayOut1 && !homePromoTierLayOut1.isEmpty()){
						List<PromoContainerVO> pList = new ArrayList<PromoContainerVO>();
						for(RepositoryItem pRepItem: homePromoTierLayOut1){
							pList.add(getTemplateTools().getHomePromoTierLayOut(pRepItem));
						}
						homePageContentVO.setHomePromoTierLayOut1(pList);
					}
					
					// Get homePromoTierLayOut2 Item
					List<RepositoryItem> homePromoTierLayOut2 = (List<RepositoryItem>) homePageTemplateData.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROPERTY_PROMO_LAYOUT2);
					
					if(null != homePromoTierLayOut2 && !homePromoTierLayOut2.isEmpty()){
						List<PromoContainerVO> pList = new ArrayList<PromoContainerVO>();
						for(RepositoryItem pRepItem: homePromoTierLayOut2){
							pList.add(getTemplateTools().getHomePromoTierLayOut(pRepItem));
						}
						homePageContentVO.setHomePromoTierLayOut2(pList);
					}
					
				}else{
					logError(" no result found for siteId "+pSiteId+" throwing BBBBusinessException");
					throw new BBBBusinessException(BBBCmsConstants.ERROR_VALUE_NOT_FOUND, "Value not found");
				}
			}
		} catch (RepositoryException e) {
			logError(" Repository Error in fetching data",e);
			throw new BBBSystemException(BBBCmsConstants.ERROR_FETCH_HOMEPAGE_CONTENT, "Error occurred while fetching homepage content");
			
		}
		logDebug("RestHomePageContentVO.getHomePageContent : END");
		if(isLoggingDebug()){
			logDebug("Exit RestHomePageContentVO method [getHomePageContent ] :: " + homePageContentVO.toString());
		}
		return homePageContentVO;
	}
	
	
	@SuppressWarnings("unchecked")
	public NavigationLinksVO getNavigationLinks() throws BBBBusinessException, BBBSystemException
	{
		logDebug("RestHomePageContentVO.getNavigationLinks : START");
		final NavigationLinksVO navLinksVO = new NavigationLinksVO();
		try {

			final String pSiteId = SiteContextManager.getCurrentSiteId();
			
			if(BBBUtility.isEmpty(pSiteId)){
				logError("pSiteId is null or empty throwing BBBBusinessException");
				throw new BBBBusinessException(BBBCmsConstants.ERROR_EMPTY_SITE_ID, "Site id can not be Empty");
			}
			else{
				String channelId = BBBUtility.getChannel();
				logDebug("starting method RestHomePageContentVO.getNavigationLinks, Passed parameters: "	+ ", pSiteId=" + pSiteId + "pChannelId=" + channelId);
				
				final RepositoryItem navigationLinksData = this.getNavigationLinksData(pSiteId,channelId);
				if(navigationLinksData!=null)
				{
					navLinksVO.setNavLinkId(navigationLinksData.getRepositoryId());
					navLinksVO.setSiteId(pSiteId);
					
					List<RepositoryItem> bannerList = (List<RepositoryItem>) navigationLinksData.getPropertyValue(BBBCmsConstants.REST_NAVIGATION_LINKS_TEMPLATE_PROPERTY_LINKS);
					
					if(null != bannerList && !bannerList.isEmpty()){
						List<LinkVO> pList = new ArrayList<LinkVO>();
						for(RepositoryItem pRepItem: bannerList){
							if(pRepItem != null)
							pList.add(getTemplateTools().getLinkVO(pRepItem));
						}
						navLinksVO.setBannerList(pList);
					}
				}
				else{
					logError(" no result found for siteId "+pSiteId);
				}
					
			}
	}
		catch (RepositoryException e) {
			logError(" Repository Error in fetching data",e);
			throw new BBBSystemException(BBBCmsConstants.ERROR_FETCH_NAVIGATION_LINKS_CONTENT, "Error occurred while fetching navigation links content");
			
		}
		logDebug("Existing method RestHomePageTemplateManager.getNavigationLinks");
		
		return navLinksVO;
	}
	
	private RepositoryItem getNavigationLinksData(String pSiteId, String pChannelId)
			throws RepositoryException {
		
		logDebug("starting method RestHomePageTemplateManager.getNavigationLinksData, Passed parameters: "
					+ ", pTemplateItemDescriptorName=" + BBBCmsConstants.REST_NAVIGATION_LINKS_TEMPLATE);
				
		RepositoryItem navigationLinksDetail = null;
		RepositoryView view = getNavLinksTemplateRepository().getView(BBBCmsConstants.REST_NAVIGATION_LINKS_TEMPLATE);

		RqlStatement statement = RqlStatement.parseRqlStatement(BBBCmsConstants.REST_NAVIGATION_LINKS_TEMPLATE_QUERY);

		Object params[] = new Object[2];
		params[0] = pSiteId;
		params[1] = pChannelId;

		RepositoryItem[] siteNavigationLinks = null;

		try {
			siteNavigationLinks = statement.executeQuery(view, params);
		} 
		catch (IllegalArgumentException iLLArgExp) {
			
			logError(LogMessageFormatter.formatMessage(null, "getNavigationLinksData:","catalog_1065" ),iLLArgExp);
			
			siteNavigationLinks = null;
		}

		if (siteNavigationLinks != null && siteNavigationLinks.length > 0) {
			logDebug("Fetched Navigation Link Data: " + siteNavigationLinks[0]);
			navigationLinksDetail = siteNavigationLinks[0]; 
		}
		logDebug("Existing method RestHomePageTemplateManager.getNavigationLinksData");
		return navigationLinksDetail;
	}


}
