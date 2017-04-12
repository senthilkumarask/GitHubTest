package com.bbb.cms.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.nucleus.ServiceMap;
import atg.repository.MutableRepository;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.repository.seo.IndirectUrlTemplate;
import atg.repository.seo.ItemLinkException;
import atg.repository.seo.UrlParameter;
import atg.service.webappregistry.WebApp;
import atg.servlet.ServletUtil;

import com.bbb.cms.PromoBoxVO;
import com.bbb.cms.RecommendationLandingPageTemplateVO;
import com.bbb.cms.vo.CMSResponseVO;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;
//import com.bbb.exception.BBBSystemException;
/**
 * This class provides a generic method for fetching cms content
 * from any CMS repository
 * Assumption: The repository should contain siteID as one of the property
 *  
 * @author ikhan2
 *
 */
public class ContentTemplateManager extends BBBGenericService {
	

	private static final String EMPTY_STRING ="";
	
	// The item Query
	private String mClpRepoItemQuery;
	
	
	// Map that maintains relation between template to indirect URL template 
	private ServiceMap mIndirectTemplateMap;
	
	// The alternate repository URL for alternate site custom landing page
	private Map<String, String> mAltURLRepoMap;

	/** Map that maintains relation between template to ItemDescriptor */
	private Map<String,String> templateItemDescriporMap;
	
	/** Map that maintains relation which templates require channel */
	private Map<String, String> templatesWithChannelMap;
	
	/** String for channelID */
	private String channelIdProperty;
	
	/** String for channelThemeID */
	private String channelThemeIdProperty;
	
	
	private BBBCatalogTools catalogTools;
	
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

   public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

   /** Getting defaultChannelTheme from config Key */
	private String getDefaultChannelThemeId() throws BBBSystemException {
		String defaultChannelThemeId = null;
		try {
			if(null != this.getCatalogTools().getConfigValueByconfigType(BBBCatalogConstants.CONTENT_CATALOG_KEYS))
			{
				defaultChannelThemeId = this.getCatalogTools().getConfigValueByconfigType(BBBCatalogConstants.CONTENT_CATALOG_KEYS).get(BBBCatalogConstants.DEFAULT_CHANNEL_THEME);
			}
		} catch (BBBBusinessException e) {
			if(this.isLoggingError()){
				this.logError("BBBBusinessException occurred while fetching config key value for default Channel theme");
			}
		}
		return defaultChannelThemeId;
	}

	

	public String getChannelThemeIdProperty() {
		return channelThemeIdProperty;
	}

	public void setChannelThemeIdProperty(String channelThemeIdProperty) {
		this.channelThemeIdProperty = channelThemeIdProperty;
	}

	/** ServiceMap that maintains relation between template to template repository component */
	private ServiceMap templateRepositoryMap;
	static final String REGISTRY_URL =  "SiteTemplate";
	public static final String UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION="6001";
	private static final String PROMOBOX_IMAGE_URL = "imageUrl";
	  private static final String PROMOBOX_IMAGE_ALT_TEXT = "imageAltText";
	  private static final String PROMOBOX_IMAGE_MAP_NAME = "imageMapName";
	  private static final String PROMOBOX_IMAGE_MAP_CONTENT = "imageMapContent";
	  private static final String PROMOBOX_CONTENT = "promoBoxContent";
	  private static final String PROMO_IMAGE_LINK = "imageLink";
	  //private static final String PROMOIMAGE_LINK_LABEL = "linkLabel";
	  //private static final String PROMOIMAGE_LINK_URL = "linkUrl";
		private static final String RECOMMENDER_TOP = "upperPromoBoxSlot";
		private static final String RECOMMENDER_BOTTOM = "bottomPromoBoxSlot";
		private static final String RECOMMENDER_MIDDLE_LIST = "middlePromoBoxSlot";
		public static final String RETRIEVED_MORE_DATA_FROM_REPOSITORY="6002";
		public static final String RETRIEVED_NO_DATA_FROM_REPOSITORY="6003";
		public static final String DESKTOPWEB="DesktopWeb";
		public static final String REGISTRY_TYPE= "registryType";
		public static final String REGISTRYURL= "registryURL";
		public static final String WEDDING= "Wedding";
		public static final String IMAGEMAPCONTENT= "imageMapContent";
		public static final String RECOMMENDER_URL = "recommenderURL";
		public static final String TOKEN_ERROR = "tokenError";
		public static final String VISIBILITY = "visibilityFlag";
		public static final String BABY ="Baby";
		public static final String BEDBATHUS ="BedBathUS";
		public static final String BUYBUYBABY ="BuyBuyBaby";
		public static final String BEDBATHCANADA ="BedBathCanada";
		public static final String TOKEN_ERROR_LOGIN = "tokenErrorLogin";
		public static final String  LOGIN_URL = "logInURL";
	  
	/**
	 * This method returns static page data based on siteId and pageName.
	 * 
	 * @param pSiteId
	 * @param pPageName
	 * @return RepositoryItem
	 * @throws RepositoryException
	 */
	public CMSResponseVO getContent(final String templateName,final  String requestJSON)
			throws RepositoryException, BBBSystemException, BBBBusinessException {

		logDebug("starting method StoreCMSTemplateManager.getStoreCMSTemplateDataJSON, Passed parameters: "
					+ ", templateQueries=" + requestJSON);
		RepositoryItem[] resultRepositoryItem = null;
		
		CMSResponseVO responseVO = new CMSResponseVO();
		/** Required input */

		if( templateName ==null){
			throw new BBBBusinessException(
					BBBCoreConstants.TEMPLATE_ARG_MISSING,
						BBBCoreConstants.TEMPLATE_ARG_MISSING_MESSAGE);
		}
		if( getTemplateRepositoryMap()!=null 
				&& getTemplateRepositoryMap().get(templateName) !=null){
			Repository repository = (Repository)getTemplateRepositoryMap().get(templateName);
			
			String itemDescriptor = null;
			if(null != getTemplateItemDescriporMap().get(templateName))
				itemDescriptor = (String)getTemplateItemDescriporMap().get(templateName);
			
			if( itemDescriptor==null){
				throw new BBBBusinessException(
						BBBCoreConstants.TEMPLATE_VIEW_MISSING, 
							BBBCoreConstants.TEMPLATE_VIEW_MISSING_MESSAGE);
			}
			
			RepositoryView view = repository.getView(itemDescriptor);
			
	
			Object[] rqlQueryAndParams =createRQLJSON(requestJSON, view, templateName);
			String rqlQuery = (String)rqlQueryAndParams[0];	
			
			RqlStatement statement = RqlStatement.parseRqlStatement(rqlQuery);
			
			Object params[] = (Object[])rqlQueryAndParams[1];
			
			try {
				resultRepositoryItem = statement.executeQuery(view,params);
				responseVO.setResponseItems(resultRepositoryItem);
			} 
			catch (IllegalArgumentException iLLArgExp) {
				logError(LogMessageFormatter.formatMessage(null, "getStoreCMSTemplateData:", 
							BBBCoreConstants.TEMPLATE_NOT_EXIST ),iLLArgExp);
				
				throw new BBBBusinessException(
						BBBCoreConstants.ERROR_TEMPLATE_ARGS, 
							BBBCoreConstants.ERROR_TEMPLATE_ARGS_MESSAGE);
			}

			logDebug("Existing method StoreCMSTemplateManager.getStoreCMSTemplateDataJSON");
			
			return responseVO;
			
		} else{
			throw new BBBBusinessException(
					BBBCoreConstants.TEMPLATE_NOT_EXIST, 
						BBBCoreConstants.TEMPLATE_NOT_EXIT_MESSAGE);			
		}

	}
	
	/**
	 * Create RQL
	 * @Assumption : storeSection & channelID will be part of requestJSONString
	 * @param requestMap
	 * @return
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	private Object[] createRQLJSON(final String requestJSONString, RepositoryView view,final String templatenName) 
			throws BBBBusinessException, BBBSystemException {
	
		Object[] rqlQueryAndParams = new Object[2];
		
		String query = EMPTY_STRING;
		
		/** Intialize for two storeSection & channel**/
		Object [] params = null;
		
		String siteID = SiteContextManager.getCurrentSiteId();
	
		/**channelID, assume channelD will be part of every template query */
		String channelID ="";
		/**channelThemeId */
		String channelThemeID = EMPTY_STRING;
		
		if(null != ServletUtil.getCurrentRequest()){
			channelID = ServletUtil.getCurrentRequest().getHeader(getChannelIdProperty());
			channelThemeID = ServletUtil.getCurrentRequest().getHeader(getChannelThemeIdProperty());
		}
		
		if(BBBUtility.isEmpty(channelID) && null != ServletUtil.getCurrentRequest().getParameter(BBBCoreConstants.CHANNEL)){
			channelID = ServletUtil.getCurrentRequest().getParameter(BBBCoreConstants.CHANNEL);
		}
		
		if(channelThemeID != null && channelThemeID.isEmpty())
		{
			if ((!BBBUtility.isEmpty(channelID)) && ((channelID.equals(BBBCoreConstants.FF1) || channelID.equals(BBBCoreConstants.FF2))))
			channelThemeID = getDefaultChannelThemeId();
		}
		
		/** Create query using each property in requestJSON*/
		JSONObject jsonObject = (JSONObject)JSONSerializer.toJSON(requestJSONString);
		DynaBean jsonResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);
		
		DynaClass dynaClass = jsonResultbean.getDynaClass();
		DynaProperty properties[] = dynaClass.getDynaProperties();
		
		int index =0;
		int size = 1;
		
		/** if template query requires channel*/
		if( getTemplatesWithChannelMap() !=null &&
				getTemplatesWithChannelMap().get(templatenName) !=null){
			//channel & siteId should be part of query too
			size = size+1;
		}
		
		//Create query using arguments
		if( properties !=null && properties.length !=0){
				
			size += properties.length;
		
			params=new Object[size];
			
			for( DynaProperty property: properties){
				String keyName = property.getName();
				
				//if key is not blank
				if(keyName !=null && !keyName.equalsIgnoreCase(EMPTY_STRING)){
					
					//value is not empty
					if(jsonResultbean.get(keyName) !=null 
							&& !EMPTY_STRING.equalsIgnoreCase(
									jsonResultbean.get(keyName).toString())){
						
						String value = jsonResultbean.get(keyName).toString();
						if(index>0){
							query += " and ";
						}
						query += keyName +"=?"+index;
						params[index] = value;
						
						index++;
					}
				}
			}
		}
		
		// ChannelTheme in query if applicable
		if(!BBBUtility.isEmpty(channelThemeID))
		{
			size+= 1;
		}
		//if there has been no argument added to query 
		if(index ==0){
			params=new Object[size];
		}
		
		//if template requires channel, add it in rql
		if( getTemplatesWithChannelMap() !=null &&
				getTemplatesWithChannelMap().get(templatenName) !=null){

			if(channelID !=null){
				if(index>0){
					query += " and ";
				}
				query += "channel=?"+index;
				params[index] = channelID;
				
				index++;
				
			} else{
				//throw error that channel is mandatory for the query
				throw new BBBBusinessException(
						BBBCoreConstants.CHANNEL_PARAM_MISSING, 
							BBBCoreConstants.CHANNEL_PARAM_MISSING_MESSAGE);	
			}
		}
		
		// ChannelTheme in query if applicable
		if(!BBBUtility.isEmpty(channelThemeID))
		{
			if(index > 0)
			{
				query += " and ";
			}
			query += "channelTheme includes ?"+index;
			params[index] = channelThemeID;
			
			index++;
		}
		
		//Add siteID in query
		if(index>0){
			query += " and ";
		}		
		query += " sites includes ?"+index;
		params[index] = siteID;
		
		rqlQueryAndParams[0] = query;
		rqlQueryAndParams[1] = params;
		
		return rqlQueryAndParams; 
		
	}
	
	/**
	 * Create RQL
	 * @Assumption : storeSection & channelID will be part of requestJSONString
	 * @param requestMap
	 * @return
	 * @throws BBBBusinessException 
	 */
	private RecommendationLandingPageTemplateVO fetchRegistryContent(final String requestJSONString, RepositoryView view,final String templatenName) 
			throws BBBBusinessException,BBBSystemException {
	
		//Object[] rqlQueryAndParams = new Object[2];
		//RepositoryItem[] registryRepositoryItem = null;
		RepositoryItem[] repositoryItem = null;
		String query = EMPTY_STRING;
		Map<String,Object> values = new HashMap<String,Object>();
		final String methodName = "fetchRegistryContent";
		final RecommendationLandingPageTemplateVO recommendationLandingPageTemplateVO = new RecommendationLandingPageTemplateVO();
		/** Intialize for two storeSection & channel**/
		Object [] params = null;
		
		String siteID = SiteContextManager.getCurrentSiteId();
		logDebug("Site is:" +siteID);
		/**channelID, assume channelD will be part of every template query */
		String channelID ="";
		if(null != ServletUtil.getCurrentRequest()){
			channelID = ServletUtil.getCurrentRequest().getHeader(getChannelIdProperty());
		}
		if(null == channelID || StringUtils.isEmpty(channelID)){
		if(BBBUtility.isEmpty(channelID) && null!=ServletUtil.getCurrentRequest() && null != ServletUtil.getCurrentRequest().getParameter(BBBCoreConstants.CHANNEL)){
			channelID = ServletUtil.getCurrentRequest().getParameter(BBBCoreConstants.CHANNEL);
		}
		else
		{
			channelID = DESKTOPWEB;
		}
		}
		logDebug("Channel is:" +channelID);
		/** Create query using each property in requestJSON*/
		JSONObject jsonObject = (JSONObject)JSONSerializer.toJSON(requestJSONString);
		DynaBean jsonResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);
		
		DynaClass dynaClass = jsonResultbean.getDynaClass();
		DynaProperty properties[] = dynaClass.getDynaProperties();
		
		int index =0;
		int size = 3;
		params=new Object[size];
		//if template requires channel, add it in rql
				if( getTemplatesWithChannelMap() !=null &&
						getTemplatesWithChannelMap().get(templatenName) !=null){

					if(channelID !=null){				
						query += "channel=?"+index;
						params[index] = channelID;
						
						index++;
						
					} else{
						//throw error that channel is mandatory for the query
						throw new BBBBusinessException(
								BBBCoreConstants.CHANNEL_PARAM_MISSING, 
									BBBCoreConstants.CHANNEL_PARAM_MISSING_MESSAGE);	
					}
				}				
				//Add siteID in query
				if(index>0){
					query += " and ";
				}		
				query += "site=?"+index;
				params[index] = siteID;
				index++;
		/** if template query requires channel*/
		
		//Create query using arguments
		if( properties !=null && properties.length !=0){				
			for( DynaProperty property: properties){
				String keyName = property.getName();
				
				//if key is not blank
				if(keyName !=null && !keyName.equalsIgnoreCase(EMPTY_STRING)){
					
					//value is not empty
					if(jsonResultbean.get(keyName) !=null 
							&& !EMPTY_STRING.equalsIgnoreCase(
									jsonResultbean.get(keyName).toString())){
						
						String value = jsonResultbean.get(keyName).toString();
						if(keyName.equalsIgnoreCase(REGISTRYURL)){
							 values.put(BBBCoreConstants.REGISTRY_URL, value);
							 continue;
						}
						if(keyName.equalsIgnoreCase(RECOMMENDER_URL)){							 
							values.put("pRecommenderURL", value);
							continue;
						}
						if(keyName.equalsIgnoreCase(TOKEN_ERROR)){
									values.put("pTokenError", value);
									continue;
						}
						if(keyName.equalsIgnoreCase(VISIBILITY)){
									values.put("pVisibility", value);
									continue;
						}
						if(keyName.equalsIgnoreCase(TOKEN_ERROR_LOGIN)){
									values.put("pTokenErrorLogin", value);
									continue;
						}
						if(keyName.equalsIgnoreCase(LOGIN_URL)){
									values.put("pLogInURL", value);
									continue;
						}
						if(keyName.equalsIgnoreCase(REGISTRY_TYPE)){
							recommendationLandingPageTemplateVO.setRegistryType(value);
						if( getTemplateRepositoryMap()!=null 
								&& getTemplateRepositoryMap().get(REGISTRY_URL) !=null){
							Repository repository = (Repository)getTemplateRepositoryMap().get(REGISTRY_URL);
							
							String itemDescriptor = null;
							if(null != getTemplateItemDescriporMap().get(REGISTRY_URL))
								itemDescriptor = (String)getTemplateItemDescriporMap().get(REGISTRY_URL);
							
							if( itemDescriptor==null){
								logError("ItemDescriptor is null....");
								throw new BBBBusinessException(
										BBBCoreConstants.TEMPLATE_VIEW_MISSING, 
											BBBCoreConstants.TEMPLATE_VIEW_MISSING_MESSAGE);
							}
							
							repositoryItem = this.getRegistryData(itemDescriptor,repository,view,query,params,value,index,keyName);
							logDebug("Fetched the RepositoryItem");
							if(null == repositoryItem){								
								logDebug("Repostory item is null,setting to default wedding");
								if(siteID.equalsIgnoreCase(BEDBATHUS) || siteID.equalsIgnoreCase(BEDBATHCANADA)) {
								value=WEDDING;
								}
								if(siteID.equalsIgnoreCase(BUYBUYBABY)){
									value=BABY;
								}
								repositoryItem = this.getRegistryData(itemDescriptor,repository,view,query,params,value,index,keyName);
							}														
					}
						
				}
						
			}
		} 
		
	
				}
		}
		if(repositoryItem != null && repositoryItem.length==1){

			for (RepositoryItem item : repositoryItem) {

			if(item != null){
				//setting the SiteID
				recommendationLandingPageTemplateVO.setSiteId(siteID);
				recommendationLandingPageTemplateVO.setChannel(channelID);

			//Calling LandingTemplateManager's setPromoBox method for getting content of  Upper PromoBox
				logDebug("Placeholder values are" + values.toString());
				if(null!= item.getPropertyValue(RECOMMENDER_TOP)){
				final RepositoryItem promoBoxTop = (RepositoryItem) item.getPropertyValue(RECOMMENDER_TOP);
				String imageMapContent = promoBoxTop.getPropertyValue(IMAGEMAPCONTENT).toString();
				imageMapContent =  replaceDynamicValues(values,
						imageMapContent);
								
				recommendationLandingPageTemplateVO.setPromoBox(this.setPromoBoxVO1(promoBoxTop, imageMapContent));
				}else{
					
					logDebug("Upper promoBox  is Null for" + siteID );
					
				}

				//Calling LandingTemplateManager's setPromoBox method for getting content of Bottom PromoBox
				final List<RepositoryItem> promoBoxMiddle= (List<RepositoryItem>) item.getPropertyValue(RECOMMENDER_MIDDLE_LIST);
				if(null!=promoBoxMiddle && !promoBoxMiddle.isEmpty()){
					recommendationLandingPageTemplateVO.setPromoBoxList(setPromoBox(promoBoxMiddle));

				}else{
					
					logDebug("Promo Tier Middle  is Null for" + siteID );
					
				}

				if(null!= item.getPropertyValue(RECOMMENDER_BOTTOM)){
				final RepositoryItem promoBoxTop = (RepositoryItem) item.getPropertyValue(RECOMMENDER_BOTTOM);				
				String imageMapContent = promoBoxTop.getPropertyValue(IMAGEMAPCONTENT).toString();
				imageMapContent =  replaceDynamicValues(values,
						imageMapContent);
				
				recommendationLandingPageTemplateVO.setPromoBoxBottom(this.setPromoBoxVO1(promoBoxTop, imageMapContent));
			}else{
				
				logDebug("Upper promoBox  is Null for" + siteID );
				
			}

				}
			}
			}else{
				
				logDebug(methodName+ "RecommendationPage Repository Items returned more than 1 item");
				
				throw new BBBSystemException (RETRIEVED_MORE_DATA_FROM_REPOSITORY,RETRIEVED_MORE_DATA_FROM_REPOSITORY);

			}
		return recommendationLandingPageTemplateVO;
	}
	
	/**
	 * This method is used to execute RQL query.
	 *
	 * @param rqlQuery the rql query in <code>String</code> format
	 * @param params   the params
	 * @param viewName the view name
	 * @param repository the repository
	 * @return the repository item[]
	 * @throws BBBSystemException the bBB system exception
	 * @throws BBBBusinessException the bBB business exception
	 */
	public RepositoryItem[] executeRQLQuery(final String rqlQuery, final Object[] params,
			final String viewName, final MutableRepository repository)
			throws BBBSystemException, BBBBusinessException {

		RepositoryItem[] queryResult = null;
		logDebug("ContentTemplateManager.executeRQLQuery() - rqlQuery ::  " + rqlQuery);
		logDebug("ContentTemplateManager.executeRQLQuery() - viewName  ::  " + viewName);

		if (rqlQuery != null) {
			if (repository != null) {
				try {
					final RqlStatement statement = RqlStatement.parseRqlStatement(rqlQuery);
					final RepositoryView view = repository.getView(viewName);
					if (statement != null && view !=null  &&  params !=null) {
						queryResult = statement.executeQuery(view, params);
						
						if(null == queryResult ){
							logDebug("No results returned for query [" + rqlQuery + "]");
						 } else{
							 queryResult = statement.executeQuery(view, params);
						}
					}else {
						logError(BBBCoreErrorConstants.CLP_ERROR_10132 + viewName +
								" Statement or view or Params are null from executeRQLQuery of CustomLandingTemplate");
					}

			} catch (final RepositoryException e) {
						logError(BBBCoreErrorConstants.CLP_ERROR_10133 + " Repository Exception " +
							"[Unable to retrieve data] from executeRQLQuery of CustomLandingTemplate",e);
					throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
							BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
							e);
				}
			} else {
					logError(BBBCoreErrorConstants.CLP_ERROR_10134 + 
						" Repository is null from executeRQLQuery of CustomLandingTemplate");
			}
		} else {
				logError(BBBCoreErrorConstants.CLP_ERROR_10135 + 
					" Query String is null from executeRQLQuery of CustomLandingTemplate");
		}

		logDebug("ContentTemplateManager.executeRQLQuery() - end");

		return queryResult;
	}


	/**
	 * This method is used to fetch template repository item for given
	 * category id and channel.
	 * 
	 * @param categoryId	category id in <code>String</code> format
	 * @param templateName		templateName in <code>String</code> format    
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private RepositoryItem[] fetchItemFromRepository(final String categoryId, final String templateName, final String channel) 
					throws BBBSystemException, BBBBusinessException {
		
		logDebug("ContentTemplateManager.fetchItemFromRepository() - start");
		logDebug("ContentTemplateManager.fetchItemFromRepository() - categoryId  ::  " + categoryId);
		logDebug("ContentTemplateManager.fetchItemFromRepository() - templateName  ::  " + templateName);
		logDebug("ContentTemplateManager.fetchItemFromRepository() - channel  ::  " + channel);
		
		final MutableRepository repository = 
				(MutableRepository)getTemplateRepositoryMap().get(getAltURLRepoMap().get(templateName));
		
		if(null == repository){
			logDebug("ContentTemplateManager.fetchItemFromRepository() - repository is null");
			return null;
		}
		String itemDescriptor = null;
		// Alternate URL item descriptor
		if(null != getAltURLRepoMap().get(templateName) && 
				null != getTemplateItemDescriporMap().get(getAltURLRepoMap().get(templateName))){
			itemDescriptor = (String)getTemplateItemDescriporMap().get(getAltURLRepoMap().get(templateName));
		}
		if(null == itemDescriptor){
			logDebug("ContentTemplateManager.fetchItemFromRepository() - itemDescriptor is null");
			return null;
		}
		
		RepositoryItem[] clpRepositoryItem = null;
		if (!BBBUtility.isEmpty(categoryId) && !BBBUtility.isEmpty(channel)) {
			final Object[] params = new Object[2];
			params[0] = categoryId;
			params[1] = channel;

			clpRepositoryItem = this.executeRQLQuery(this.getClpRepoItemQuery(),
					params, itemDescriptor, repository);
		}
		
		logDebug("ContentTemplateManager.fetchItemFromRepository() - end");
		
		return clpRepositoryItem;
	}

	/**
	 * This method is used to create a SEO URL for using Clp template
	 * 
	 * @param categoryId   categoryId in <code>String</code> format.
	 * @param templateName  templateName in <code>String</code> format.
	 * @return String
	 */
	
	public String createCanonicalURL(final String categoryId, final String templateName) {
		final String channel = BBBUtility.getChannel();
		return createAlternateURL(categoryId, getAltURLRepoMap().get(templateName), channel);
	}
	
	
	/**
	 * This method is used to create a SEO URL for using alternate template
	 * 
	 * @param categoryId   categoryId in <code>String</code> format.
	 * @param templateName  templateName in <code>String</code> format.
	 * @return String
	 */
	public String createAlternateURL(final String categoryId, final String templateName, final String channel) {
		String formattedURL = null;
		final WebApp pDefaultWebApp = null;

		logDebug("ContentTemplateManager.createAlternateURL() - start");
		
		logDebug("ContentTemplateManager.createAlternateURL() - categoryId  ::  " + categoryId);
		logDebug("ContentTemplateManager.createAlternateURL() - templateName  ::  " + templateName);


		try {
			
			// repository item from repository for creating alternate URL
			final RepositoryItem[] repoItem = 
				fetchItemFromRepository(categoryId, templateName, channel);
			
			final String altTemplateName = getAltURLRepoMap().get(templateName);
			logDebug("ContentTemplateManager.createAlternateURL() - altTemplateName  ::  " + altTemplateName);
			
			
			
			
			// if repository item is null return null
			if(repoItem == null || altTemplateName == null){
				logDebug("ContentTemplateManager.createAlternateURL() - repoItem or alternate url is null ");
				return null;
			}
			
			final String clpName = (String)repoItem[0].
					getPropertyValue(BBBCoreConstants.CLP_NAME);
			if (BBBUtility.isEmpty(clpName)){
				logDebug("ContentTemplateManager.createAlternateURL() - clpName is null ");
				return null;
			}
			
			final UrlParameter[] pUrlParams =((IndirectUrlTemplate) getIndirectTemplateMap().
					get(altTemplateName)).cloneUrlParameters();
			pUrlParams[0].setValue(clpName);
			pUrlParams[1].setValue(categoryId);
			formattedURL = ((IndirectUrlTemplate)getIndirectTemplateMap().get(altTemplateName)).
					formatUrl(pUrlParams, pDefaultWebApp);

			logDebug("ContentTemplateManager.createAlternateURL() - formattedURL  ::  " + formattedURL);			
		} catch (ItemLinkException e) {
				logError(
						"Exception occourred while creating SEO URL for the Custom Landing Page : "
								, e);
		} catch (Exception e) {
				logError(
						"Exception occourred while creating SEO URL for the Custom Landing Page : "
								, e);
		}
		
		logDebug("ContentTemplateManager.createAlternateURL() - formattedURL  ::  " + formattedURL);
		logDebug("ContentTemplateManager.createAlternateURL() - end");
		
		return formattedURL;
	}
	
	/**
	 * This method is used to check the Custom landing template for
	 * the respective category id
	 * @param categoryId   categoryId in <code>String</code> format.
	 * @param templateName  templateName in <code>String</code> format.
	 * @return boolean 
	 */
	
	public boolean checkTemplateForId(final String templateName,final String categoryId){
			logDebug("ContentTemplateManager.checkTemplateForId() - start");
			final MutableRepository repository = 
					(MutableRepository)getTemplateRepositoryMap().get(templateName);
			final String itemDescriptor = getTemplateItemDescriporMap().get(templateName);
			final String channel = BBBUtility.getChannel();
			if(null!=itemDescriptor){
				RepositoryItem[] clpRepositoryItem = null;
				if (!BBBUtility.isEmpty(categoryId)) {
					final Object[] params = new Object[2];
					params[0] = categoryId;
					params[1] = channel;
					try {
						clpRepositoryItem = this.executeRQLQuery(this.getClpRepoItemQuery(),
								params, itemDescriptor, repository);
						if(null==clpRepositoryItem){
							return false;
						}
					} catch (BBBSystemException e) {
						logError(
								"Exception occourred while checking for Custom Landing template for the given CatId : "
										, e);
					} catch (BBBBusinessException e) {
						logError(
								"Exception occourred while checking for Custom Landing template for the given CatId : "
										, e);
					}
				}
			}
			logDebug("ContentTemplateManager.checkTemplateForId() - end");
		return true;
	}
	
	/**
	 * This method is used to fetch recommender and registrant empty landing page template data
	 * @param requestJSON   requestJSON in <code>String</code> format.
	 * @param templateName  templateName in <code>String</code> format.	
	 */
	public RecommendationLandingPageTemplateVO getRegistrantContent( String templateName,  String requestJSON)
			throws RepositoryException, BBBSystemException, BBBBusinessException {

		logDebug("starting method StoreCMSTemplateManager.getStoreCMSTemplateDataJSON, Passed parameters: "
					+ ", templateQueries=" + requestJSON);
		//RepositoryItem[] resultRepositoryItem = null;
		RecommendationLandingPageTemplateVO regVO = null;
		
		/** Required input */

		if( templateName ==null){
			throw new BBBBusinessException(
					BBBCoreConstants.TEMPLATE_ARG_MISSING,
						BBBCoreConstants.TEMPLATE_ARG_MISSING_MESSAGE);
		}
		logDebug("Template name "+templateName);
		if( getTemplateRepositoryMap()!=null 
				&& getTemplateRepositoryMap().get(templateName) !=null){
			Repository repository = (Repository)getTemplateRepositoryMap().get(templateName);
			
			String itemDescriptor = null;
			if(null != getTemplateItemDescriporMap().get(templateName))
				itemDescriptor = (String)getTemplateItemDescriporMap().get(templateName);
			
			if( itemDescriptor==null){
				throw new BBBBusinessException(
						BBBCoreConstants.TEMPLATE_VIEW_MISSING, 
							BBBCoreConstants.TEMPLATE_VIEW_MISSING_MESSAGE);
			}
			
			RepositoryView view = repository.getView(itemDescriptor);	
			regVO =fetchRegistryContent(requestJSON, view, templateName);			
			logDebug("Existing method ContentTemplateManager.getRegistrantContent" +regVO.toString());		
			return regVO;
			
		} else{
			throw new BBBBusinessException(
					BBBCoreConstants.TEMPLATE_NOT_EXIST, 
						BBBCoreConstants.TEMPLATE_NOT_EXIT_MESSAGE);			
		}

	}
	
	public RepositoryItem[] getRegistryData(String itemDescriptor,Repository repository,RepositoryView view,String query,Object [] params,String value,int index,String keyName) throws BBBBusinessException, BBBSystemException{
		String registryQuery = "registryTypeName=?0";
		Object [] registryParams = new Object[1];
		registryParams[0]= value;
		RepositoryItem[] registryRepositoryItem = null;
		RepositoryItem[]  repositoryItem = null;
								
		try {
			RqlStatement statement = RqlStatement.parseRqlStatement(registryQuery);	
			RepositoryView registryView = repository.getView(itemDescriptor);
			registryRepositoryItem = statement.executeQuery(registryView,registryParams);
			if(index>0){
				query += " and ";
			}
			query += keyName +"=?"+index;
			logDebug("Query is" +query);
			if(null!=registryRepositoryItem){
			for (RepositoryItem item : registryRepositoryItem){
				value = (String) item.getPropertyValue("id");
				
				params[index] = value;
				
				RqlStatement statement1 = RqlStatement.parseRqlStatement(query);
				RepositoryItem[] checkItem = statement1.executeQuery(view,params);
				if(null!= checkItem){
				repositoryItem = statement1.executeQuery(view,params);
				return repositoryItem;
				}
			}
			}								
		} 						
		catch (IllegalArgumentException iLLArgExp) {
			logError(LogMessageFormatter.formatMessage(null, "getStoreCMSTemplateData:", 
						BBBCoreConstants.TEMPLATE_NOT_EXIST ),iLLArgExp);
			
			throw new BBBBusinessException(
					BBBCoreConstants.ERROR_TEMPLATE_ARGS, 
						BBBCoreConstants.ERROR_TEMPLATE_ARGS_MESSAGE);
		}
		catch (RepositoryException e) {
	
			logError(LogMessageFormatter.formatMessage(null, "RecommenderLandingPageTemplateManager.getRecommendationData() | RepositoryException ","catalog_1056"), e);
			
			throw new BBBSystemException (UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,e);
		}
		return repositoryItem;
	}
	
	private String replaceDynamicValues(Map pPlaceHolderValues,
			String pEmailSection) {
		
		
			logDebug("[START] replaceDynamicValues");
			logDebug("pEmailSection: "+ pEmailSection);
		
		
		if(pEmailSection!= null && !pPlaceHolderValues.isEmpty())
		{
			Set entrySet = pPlaceHolderValues.entrySet();
		    for (Iterator iterator = entrySet.iterator(); iterator.hasNext();) {
		        Map.Entry value = (Map.Entry) iterator.next();
		        
		        if(value.getValue() != null)
		        {
		        	
		        	logDebug("REPLCING...." + (String) value.getKey() +" by " + value.getValue().toString());
		        	
		        	pEmailSection = pEmailSection.replaceAll((String) value.getKey(), Matcher.quoteReplacement(value.getValue().toString()));
		        }
		        
		    }
				
			
		}
		
		
			logDebug("[END] replaceDynamicValues");
			logDebug("pEmailSection: "+ pEmailSection);
		
		
		return pEmailSection;
	}


public PromoBoxVO setPromoBoxVO1(RepositoryItem pPromoBox,String imageMapContent){
	final PromoBoxVO promoBoxVO1=new PromoBoxVO();
	promoBoxVO1.setImageURL((String)pPromoBox.getPropertyValue(PROMOBOX_IMAGE_URL));
	promoBoxVO1.setImageAltText((String)pPromoBox.getPropertyValue(PROMOBOX_IMAGE_ALT_TEXT));
	promoBoxVO1.setImageMapName((String)pPromoBox.getPropertyValue(PROMOBOX_IMAGE_MAP_NAME));
	promoBoxVO1.setImageMapContent(imageMapContent);
	promoBoxVO1.setPromoBoxContent((String)pPromoBox.getPropertyValue(PROMOBOX_CONTENT));
	promoBoxVO1.setImageLink((String)pPromoBox.getPropertyValue(PROMO_IMAGE_LINK));	
	return promoBoxVO1;
}

public  List<PromoBoxVO> setPromoBox(List<RepositoryItem> pPromoBox){


    PromoBoxVO promoBoxVO = null ;
    List<PromoBoxVO> promoBoxList =new ArrayList<PromoBoxVO>();

   
      logDebug("LandingTemplateManager.setPromoBox() Method Entering");
   

    for (RepositoryItem item : pPromoBox) {
      promoBoxVO=new PromoBoxVO();
      promoBoxVO.setId(item.getRepositoryId());
      promoBoxVO.setImageURL((String)item.getPropertyValue(PROMOBOX_IMAGE_URL));
      promoBoxVO.setImageAltText((String)item.getPropertyValue(PROMOBOX_IMAGE_ALT_TEXT));
      promoBoxVO.setImageMapName((String)item.getPropertyValue(PROMOBOX_IMAGE_MAP_NAME));
      promoBoxVO.setImageMapContent((String)item.getPropertyValue(PROMOBOX_IMAGE_MAP_CONTENT));
      promoBoxVO.setImageLink((String)item.getPropertyValue(PROMO_IMAGE_LINK));
      StringBuffer sb = new StringBuffer();
      sb.append(item.getPropertyValue(PROMOBOX_CONTENT)); 
      promoBoxVO.setPromoBoxContent(sb.toString());
      promoBoxList.add(promoBoxVO);
    }

    
      logDebug("LandingTemplateManager.setPromoBox() Method Ending");
   

    return promoBoxList;

  }
	/**
	 * @return the templateRepositoryMap
	 */
	public ServiceMap getTemplateRepositoryMap() {
		return this.templateRepositoryMap;
	}

	/**
	 * @param templateRepositoryMap the templateRepositoryMap to set
	 */
	public void setTemplateRepositoryMap(final ServiceMap templateRepositoryMap) {
		this.templateRepositoryMap = templateRepositoryMap;
	}


	/**
	 * @return the templatesWithChannelMap
	 */
	public Map<String,String> getTemplatesWithChannelMap() {
		return templatesWithChannelMap;
	}

	
	public void setTemplatesWithChannelMap(
			final Map<String,String> templatesWithChannelMap) {
		this.templatesWithChannelMap = templatesWithChannelMap;
	}
	
	/**
	 * @return the channelIdProperty
	 */
	public String getChannelIdProperty() {
		return channelIdProperty;
	}


	/**
	 * @param channelIdProperty the channelIdProperty to set
	 */
	public void setChannelIdProperty(final String channelIdProperty) {
		this.channelIdProperty = channelIdProperty;
	}
	
	/**
	 * @return the templateItemDescriporMap
	 */
	public Map<String,String> getTemplateItemDescriporMap() {
		return templateItemDescriporMap;
	}

	/**
	 * @param templateItemDescriporMap the templateItemDescriporMap to set
	 */
	public void setTemplateItemDescriporMap(
			final Map<String,String> templateItemDescriporMap) {
		this.templateItemDescriporMap = templateItemDescriporMap;
	}

	/**
	 * @return the clpRepoItemQuery
	 */
	public String getClpRepoItemQuery() {
		return mClpRepoItemQuery;
	}

	/**
	 * @param pClpRepoItemQuery the clpRepoItemQuery to set
	 */
	public void setClpRepoItemQuery(final String pClpRepoItemQuery) {
		mClpRepoItemQuery = pClpRepoItemQuery;
	}

	/**
	 * @return the indirectTemplateMap
	 */
	public ServiceMap getIndirectTemplateMap() {
		return mIndirectTemplateMap;
	}

	/**
	 * @param pIndirectTemplateMap the indirectTemplateMap to set
	 */
	public void setIndirectTemplateMap(final ServiceMap pIndirectTemplateMap) {
		mIndirectTemplateMap = pIndirectTemplateMap;
	}

	/**
	 * @return the altURLRepoMap
	 */
	public Map<String, String> getAltURLRepoMap() {
		return mAltURLRepoMap;
	}

	/**
	 * @param pAltURLRepoMap the altURLRepoMap to set
	 */
	public void setAltURLRepoMap(final Map<String, String> pAltURLRepoMap) {
		mAltURLRepoMap = pAltURLRepoMap;
	}

	
}
