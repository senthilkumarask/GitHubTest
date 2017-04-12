package com.bbb.cms.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bbb.cms.RegistryTemplateVO;
import com.bbb.commerce.catalog.vo.BrandVO;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.rest.cms.tools.RestTemplateTools;

import atg.core.util.StringUtils;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.exception.BBBBusinessException;

import atg.multisite.SiteContextManager;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

public class RegistryTemplateManager extends BBBGenericService{

	private static final String REG_DESCRIPTOR = "siteRegistryPage";
	private static final String REG_PAGE_NAME = "pageName";
	private static final String REG_BBB_PAGE_NAME = "bbbPageName";
	private static final String REG_PAGE_TITLE = "pageTitle";
	private static final String REG_HEADER_FEATURED_CONTENT = "pageHeaderFeaturedContent";
	private static final String REG_PAGE_HEADER_COPY = "pageHeaderCopy";
	private static final String REG_PROMO_IMAGE_URL = "promoImageURL";
	private static final String REG_PROMO_IMAGE_ALT_TEXT = "promoImageAltText";
	private static final String REG_PAGE_COPY = "pageCopy";
	private static final String REG_PAGE_TYPE = "pageType";
	private static final String REG_IMAGE_BOX = "imageBox";
	private static final String SITE = "siteId";
	private static final String OMNITUREDATA ="OmnitureData";
	private static final String LAND_BRANDS = "brands";
	private static final String LAND_BRAND_IMAGE = "brandImage";
	private static final String LAND_BRAND_NAME= "brandName";
	private static final String LAND_BRAND_DESC = "brandDescrip";
	private static final String HEAD_TAG_CONTENT = "headTagContent";
	private static final String BODY_END_TAG_CONTENT = "bodyEndTagContent";

	private Repository mRegistryTemplate;
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

	public Repository getRegistryTemplate() {
		return mRegistryTemplate;
	}

	public void setRegistryTemplate(Repository pRegistryTemplate) {
		this.mRegistryTemplate = pRegistryTemplate;
	}

	/**
	 * This method would interact with RegistryTemplate repository and based on provided page name and
	 * category id it will get the content. 
	 * @param pPageName
	 * @return RegistryTemplateVO
	 */
	public RegistryTemplateVO getRegistryTemplateData(String pPageName,String pSiteId){

		RepositoryView view = null;
		QueryBuilder queryBuilder;
		QueryExpression queryExpPageName;
		QueryExpression queryPageName;
		RepositoryItem[] items = null;
		final Query queryRegistry;

		RegistryTemplateVO registryTemplateVO = null;
		logDebug("RegistryTemplateManager.getRegistryTemplateData() Method Entering");
		try{
			view = getRegistryTemplate().getView(REG_DESCRIPTOR);
			queryBuilder = view.getQueryBuilder();
			queryExpPageName = queryBuilder.createPropertyQueryExpression(REG_PAGE_NAME);
			queryPageName = queryBuilder.createConstantQueryExpression(pPageName);
			final Query[] queries = new Query[2];
			queries[0] = siteIdQuery(pSiteId, queryBuilder);
			queries[1] = queryBuilder.createComparisonQuery(queryExpPageName, queryPageName, QueryBuilder.EQUALS);
			queryRegistry= queryBuilder.createAndQuery(queries);
			logDebug("Landing Query to retrieve data : "+queryRegistry);
			try{
				items = view.executeQuery(queryRegistry);
			}catch (IllegalArgumentException iLLArgExp) {
				items = null;
			}
			if(items !=null){
				logDebug("page found with pageName  "+pPageName);
				for (RepositoryItem item : items) {
					registryTemplateVO =getRegTemplateVO(item);
					List<BrandVO> brandList = getBrandsList(item);
					registryTemplateVO.setBrands(brandList);
					
					//Get Image Box details
					RepositoryItem promoItem = (RepositoryItem) item.getPropertyValue(REG_IMAGE_BOX);
					registryTemplateVO.setImageBox(getTemplateTools().getPromoBoxVO(promoItem));
					break;
				}
			}
			else{
				logDebug("No Page found for page name "+pPageName+" searching the page name in others category " );
				RqlStatement newStatement = RqlStatement.parseRqlStatement(BBBCmsConstants.REGISTRY_NEW_TEMPLATE_QUERY);
				Object newparams[] = new Object[3];
				newparams[0] = pSiteId;
				newparams[1] = "Others";
				newparams[2] = pPageName;
				RepositoryItem[]  regItem = newStatement.executeQuery(view, newparams);
				if (regItem == null)
				{
					logDebug("There is no page in Others Category with the name" + pPageName);
				}
				else
				{	
					logDebug(" Found page with bbPageName "+pPageName +" and pageName Others");
					RepositoryItem item=regItem[0];
					registryTemplateVO =getRegTemplateVO(item);
					List<BrandVO> brandList = getBrandsList(item);
					registryTemplateVO.setBrands(brandList);
					
					//Get Image Box details
					RepositoryItem promoItem = (RepositoryItem) item.getPropertyValue(REG_IMAGE_BOX);
					registryTemplateVO.setImageBox(getTemplateTools().getPromoBoxVO(promoItem));
				}
			}

		}catch (RepositoryException e) {
			LogMessageFormatter.formatMessage(null, "Repository Exception accured at RegistryTemplateManager.getRegistryTemplateData");
		}catch(Exception e){
			LogMessageFormatter.formatMessage(null, "Exception accured at RegistryTemplateManager.getRegistryTemplateData");
		}
		logDebug("Existing method RegistryTemplateManager.getRegistrygTemplateData()");
		return registryTemplateVO;
	}

	/**
	 * This method will return the BrandsVO based on the brand ID's specified in the template
	 * @param item
	 * @return List<BrandVO>
	 */
	private List<BrandVO> getBrandsList(RepositoryItem item){	
		@SuppressWarnings("unchecked")
		List<RepositoryItem> brands= (List<RepositoryItem>) item.getPropertyValue(LAND_BRANDS);
		List<BrandVO> brandList=new ArrayList<BrandVO>();
		BrandVO brandVO=null;

		for (RepositoryItem itemp : brands) {
			brandVO = new BrandVO();
			brandVO.setBrandId((String)itemp.getRepositoryId());
			brandVO.setBrandImage((String)itemp.getPropertyValue(LAND_BRAND_IMAGE));
			brandVO.setBrandName((String)itemp.getPropertyValue(LAND_BRAND_NAME));
			brandVO.setBrandImageAltText((String)itemp.getPropertyValue(LAND_BRAND_DESC));

			brandList.add(brandVO);
		}
		return brandList;
	}
	
	/**
	 * The method sets value in RegistryTemplateVO with the data from 
	 * repository
	 * @param item
	 * @return
	 */
	private RegistryTemplateVO getRegTemplateVO(RepositoryItem item){
		RegistryTemplateVO registryTemplateVO = new RegistryTemplateVO();
		registryTemplateVO.setPageName((String)item.getPropertyValue(REG_PAGE_NAME));
		registryTemplateVO.setBbbPageName((String)item.getPropertyValue(REG_BBB_PAGE_NAME));
		registryTemplateVO.setPageTitle((String)item.getPropertyValue(REG_PAGE_TITLE));
		registryTemplateVO.setPageHeaderFeaturedContent((String)item.getPropertyValue(REG_HEADER_FEATURED_CONTENT));
		registryTemplateVO.setPageHeaderCopy((String)item.getPropertyValue(REG_PAGE_HEADER_COPY));
		registryTemplateVO.setPromoImageURL((String)item.getPropertyValue(REG_PROMO_IMAGE_URL));
		registryTemplateVO.setPromoImageAltText((String)item.getPropertyValue(REG_PROMO_IMAGE_ALT_TEXT));
		registryTemplateVO.setPageCopy((String)item.getPropertyValue(REG_PAGE_COPY));
		registryTemplateVO.setPageType((String)item.getPropertyValue(REG_PAGE_TYPE));
		registryTemplateVO.setOmnitureData((Map) item.getPropertyValue(OMNITUREDATA));
		registryTemplateVO.setPageWrapper(null);
		registryTemplateVO.setPageVariation(null);
		registryTemplateVO.setHeadTagContent((String)item.getPropertyValue(HEAD_TAG_CONTENT));
		registryTemplateVO.setBodyEndTagContent((String)item.getPropertyValue(BODY_END_TAG_CONTENT));
		return registryTemplateVO;
	}

	/**
	 * This function is used to query for states based on Site Id.
	 */
	private Query siteIdQuery(final String psiteId, final QueryBuilder pQueryBuilder) throws RepositoryException {

		String siteId = psiteId;
		logDebug("RegistryTemplateManager.siteIdQuery() Method Entering");
		if (StringUtils.isBlank(siteId)){
			return null;
		}
		final QueryExpression propertyExpression = pQueryBuilder.createPropertyQueryExpression(SITE);
		final QueryExpression rootItem = pQueryBuilder.createConstantQueryExpression(siteId);
		final Query includesQuery = pQueryBuilder.createIncludesQuery(propertyExpression, rootItem);
		logDebug("RegistryTemplateManager.siteIdQuery() Method Ending");
		return includesQuery;
	}

	/**
	 * This method is used to get content for Registry pages based on pageName
	 * 
	 * @param pageName page name whose related registry content needs to be fetched
	 * @return registry page content related information
	 * @throws BBBBusinessException exception in case any error occurred while fetching registry related content
	 */
	public RegistryTemplateVO getRegistryTemplateContent(String pageName) throws BBBBusinessException{
		logDebug("RegistryTemplateManager.getRegistryTemplateContent: START");
		RegistryTemplateVO registryTemplateVO =new RegistryTemplateVO();

		final String siteId = SiteContextManager.getCurrentSiteId();
		logDebug("RegistryTemplateManager.getRegistryTemplateContent inputa param pageName "+pageName +" site Id from SiteContext "+siteId);
		if(StringUtils.isEmpty(pageName)){
			logError(" input parameter pageName is null or empty, throwing BBBBusinessException");
			throw new BBBBusinessException(BBBCmsConstants.ERROR_NULL_INPUT_PARAM, "Page name can not be null");

		}else{
			registryTemplateVO = this.getRegistryTemplateData(pageName, siteId);

			if(registryTemplateVO==null){
				logError(" no result found for pageName"+pageName +" and siteId "+siteId+" throwing BBBBusinessException");
				throw new BBBBusinessException(BBBCmsConstants.ERROR_VALUE_NOT_FOUND, "no result found for page");

			}

		}
		
		logDebug("RegistryTemplateManager.getRegistryTemplateContent: END");
		return registryTemplateVO;
	}

}
