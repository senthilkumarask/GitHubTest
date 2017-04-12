package com.bbb.kickstarters.manager;
/**
 * This is a manager for kickStarters, which contains all the methods
 * to retrieve the data from kickStarter repository, adds the business logic to it,
 * and sends filtered data to the droplets.
 * 
 * @author dwaghmare
 * 
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import atg.core.util.StringUtils;
import atg.nucleus.Nucleus;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.targeting.DynamicContentTargeter;
import atg.targeting.TargetingException;
import atg.userprofiling.Profile;

import com.bbb.cms.PromoBoxVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.BazaarVoiceProductVO;
import com.bbb.commerce.catalog.vo.ImageVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.giftregistry.bean.AddItemsBean;
import com.bbb.commerce.giftregistry.bean.GiftRegistryViewBean;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBObjectCache;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.kickstarters.KickStarterItemsVO;
import com.bbb.kickstarters.KickStarterVO;
import com.bbb.kickstarters.PicklistVO;
import com.bbb.kickstarters.RegistryTypeVO;
import com.bbb.kickstarters.TopSkuDetailVO;
import com.bbb.kickstarters.TopSkuProductVO;
import com.bbb.kickstarters.TopSkuVO;
import com.bbb.kickstarters.tools.KickStarterTools;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.search.endeca.EndecaSearch;
import com.bbb.utils.BBBConfigRepoUtils;


public class KickStarterManager extends BBBGenericService {
	private KickStarterTools kickStarterTools;
	private BBBCatalogTools catalogTools = null;
	private final String ARRAY_SEPARETOR="}";
	private final String COMMA_SEPARETOR=",";
	private final String FRD_SLASH_SEPARETOR="/";
	private final String SEARCHQUERY = "searchQuery";
	private GiftRegistryManager mGiftRegMgr;
	private final String RESULTSIZE = "resultSize";	
	protected static final String GETTING_CACHE_TIMEOUT_FOR="getting cacheTimeout for ";
	private int keywordCacheTimeout;
	/**
	 * object for Coherence cache
	 */
	private BBBObjectCache mObjectCache;

	/**
	 * @return the mObjectCache
	 */
	public BBBObjectCache getObjectCache() {
		return this.mObjectCache;
	}

	/**
	 * @param mObjectCache the mObjectCache to set
	 */
	public void setObjectCache(final BBBObjectCache pObjectCache) {
		this.mObjectCache = pObjectCache;
	}


  	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}



	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}


	public KickStarterTools getKickStarterTools() {
		return kickStarterTools;
	}


	public void setKickStarterTools(KickStarterTools kickStarterTools) {
		this.kickStarterTools = kickStarterTools;
	}
	
	/** Endeca Results */
	private EndecaSearch mEndecaSearch;
		
	/**
	 * @return mEndecaQueryGenerator - Generate Endeca Search and get Results
	 */
	public EndecaSearch getEndecaSearch() {
		return mEndecaSearch;
	}

	/**
	 * @param  pEndecaSearch - to set Endeca Search
	 */
	public void setEndecaSearch(EndecaSearch pEndecaSearch) {
		this.mEndecaSearch = pEndecaSearch;
	}


	/**
	 * getKickStarters method calls KickStarterTools and gets the repositoryData
	 *
	 */

	  @SuppressWarnings("unchecked")
	  public KickStarterItemsVO getKickStartersByType(String registryType,String siteId, boolean isTransient, String kickStarterType) throws RepositoryException{
		  
		  BBBPerformanceMonitor.start( KickStarterManager.class.getName() + " : " + "getKickStartersByType(registryType,isTransient,kickStarterType)");
		  
		  
		  logDebug("KickStarterManager:getRestKickStrDataLoggedIn: START");
		  
		  KickStarterItemsVO KickStarterItemsVO = new KickStarterItemsVO();
		  List<String> topConsultantIds =new ArrayList<String>();
		  List<String> shopTheLookIds =new ArrayList<String>();
		  DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		  BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		  
		  List<RepositoryItem> kickStarterDataItems =(List) new ArrayList<RepositoryItem>();

		  kickStarterDataItems = this.kickStarterTools.getKickStartersByType(registryType, siteId, isTransient,kickStarterType);
		  
		  
		  for(RepositoryItem kickStarterDataItem:kickStarterDataItems ){
			  if(kickStarterType.equalsIgnoreCase(BBBGiftRegistryConstants.TOP_CONSULTANT)){
				  topConsultantIds.add((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.ID));
			  }else{
				  shopTheLookIds.add((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.ID));
			  }
		  }
		  if(kickStarterType.equalsIgnoreCase(BBBGiftRegistryConstants.TOP_CONSULTANT)){
		  sessionBean.setTopConsultantIds(topConsultantIds);
		  }
		  else{
			  sessionBean.setShopTheLookIds(shopTheLookIds);
		  }
		  KickStarterItemsVO= getPopulatedVO(kickStarterDataItems);
		  
		  
		  logDebug("KickStarterManager:getRestKickStrDataLoggedIn: EXIT");
			
		  
		  BBBPerformanceMonitor.end( KickStarterManager.class.getName() + " : " + "getKickStartersByType(registryType,isTransient,kickStarterType)");
		  
		  return KickStarterItemsVO;
	  }
	  
	  /**
	  * getKickStartersRest method calls KickStarterTools and gets the repositoryData
	  *
	  */

		  @SuppressWarnings("unchecked")
		  public KickStarterItemsVO getKickStartersForRest(String registryType, String siteId, String channel, String pageName) throws RepositoryException{
			  
			  BBBPerformanceMonitor.start( KickStarterManager.class.getName() + " : " + "getKickStartersForRest(registryType,siteId,channel,pageName)");
			  
			 
			  logDebug("KickStarterManager:getRestKickStrDataLoggedIn: START");
			  			  
			  KickStarterItemsVO KickStarterItemsVO = new KickStarterItemsVO();
			  Profile profile =  (Profile) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.ATG_PROFILE);			  
			  List<RepositoryItem> kickStarterDataItems =(List) new ArrayList<RepositoryItem>();
			  kickStarterDataItems = this.kickStarterTools.getKickStartersForRest(registryType, siteId, profile.isTransient());
			  KickStarterItemsVO= getPopulatedVO(kickStarterDataItems);
			  KickStarterItemsVO.setPromoBoxVOList(getPromoBoxVOs(registryType, siteId, channel, pageName));
			  
			
			  logDebug("KickStarterManager:getRestKickStrDataLoggedIn: EXIT");
			  
			  BBBPerformanceMonitor.end( KickStarterManager.class.getName() + " : " + "getKickStartersForRest(registryType,siteId,channel,pageName)");
			  
			  
			  return KickStarterItemsVO;
		  }
	  
	  /**
	  * This method will execute the targeter and will return the promoboxes.
	  * This method will be executed only for the mobile.
	  *
	  */	  
	  public List<PromoBoxVO> getPromoBoxVOs(String registryType, String siteId, String channel, String pageName) {
		  
		  BBBPerformanceMonitor.start( KickStarterManager.class.getName() + " : " + "getPromoBoxVOs(registryType,siteId,channel,pageName)");
		  
		
		  logDebug("KickStarterManager:getPromoBoxVOs: START");
		  registryType = StringUtils.isEmpty(registryType) ? 
		  			getGiftRegistryManager().getDefaultRegistryTypeBySite(siteId) 
		  				: registryType;
		  
			DynamicContentTargeter targetingServices = (DynamicContentTargeter) Nucleus
					.getGlobalNucleus()
					.resolveName(
							"/atg/registry/RepositoryTargeters/RegistryPagesPromoImageTargeter");
			Profile profile =  (Profile) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.ATG_PROFILE);
			boolean isTransient = profile.isTransient();
			ServletUtil.getCurrentRequest().setParameter(
					BBBCmsConstants.REG_PROMO_BOX_TARGETER_PAGE_NAME_PARAMETER,
					pageName);
			ServletUtil.getCurrentRequest().setParameter(
					BBBCmsConstants.REG_PROMO_BOX_TARGETER_PROMO_SPOT_PARAMETER,
					"Top");
			ServletUtil.getCurrentRequest().setParameter(
					BBBCmsConstants.REG_PROMO_BOX_TARGETER_REGISTRY_TYPE_PARAMETER,
					registryType);
			ServletUtil
					.getCurrentRequest()
					.setParameter(
							BBBCmsConstants.REG_PROMO_BOX_TARGETER_CUST_TYPE_PARAMETER,
							isTransient ? BBBCmsConstants.REG_PROMO_BOX_TARGETER_ANONYMOUS_USER_CUST_TYPE
									: BBBCmsConstants.REG_PROMO_BOX_TARGETER_LOGGED_IN_USER_CUST_TYPE);
			ServletUtil.getCurrentRequest().setParameter(
					BBBCmsConstants.REG_PROMO_BOX_TARGETER_SITE_ID_PARAMETER,
					siteId);
			ServletUtil.getCurrentRequest().setParameter(
					BBBCmsConstants.REG_PROMO_BOX_TARGETER_CHANNEL_PARAMETER,
					channel);
		  
		  List<PromoBoxVO> promoBoxList = null;
		  PromoBoxVO promoBoxVO = null;
		  try {
			Object[] promoContainter = targetingServices.target(ServletUtil.getCurrentRequest(), 1);
			if(promoContainter != null 
					&& promoContainter.length >0) {
				RepositoryItem promoContainterItem = (RepositoryItem) promoContainter[0];
				if(promoContainterItem.getPropertyValue(BBBCmsConstants.REST_SITE_STATIC_TEMPLATE_PROMOBOX) != null) {
					List<RepositoryItem> prmoBoxes = (List<RepositoryItem>)promoContainterItem.getPropertyValue(BBBCmsConstants.REST_SITE_STATIC_TEMPLATE_PROMOBOX);
					for(RepositoryItem promoBox: prmoBoxes) {
						String pId = promoBox.getRepositoryId();
						String pImageURL = promoBox
								.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROMOBOX_IMAGE_URL) == null ? ""
								: (String) promoBox
										.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROMOBOX_IMAGE_URL);
						String pImageAltText = promoBox
								.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROMOBOX_IMAGE_ALT_TEXT) == null ? ""
								: (String) promoBox
										.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROMOBOX_IMAGE_ALT_TEXT);
						String pImageMapName = promoBox
								.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROMOBOX_IMAGE_MAP_NAME) == null ? ""
								: (String) promoBox
										.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROMOBOX_IMAGE_MAP_NAME);
						String pImageMapContent = promoBox
								.getPropertyValue(BBBCmsConstants.REST_STATIC_TEMPLATE_PROMOBOX_IMAGE_MAP_CONTENT) == null ? ""
								: (String) promoBox
										.getPropertyValue(BBBCmsConstants.REST_STATIC_TEMPLATE_PROMOBOX_IMAGE_MAP_CONTENT);
						String pPromoBoxContent = promoBox
								.getPropertyValue(BBBCmsConstants.REST_STATIC_TEMPLATE_PROMOBOX_PROMOBOX_CONTENT) == null ? ""
								: (String) promoBox
										.getPropertyValue(BBBCmsConstants.REST_STATIC_TEMPLATE_PROMOBOX_PROMOBOX_CONTENT);
						String pImageLink = promoBox
								.getPropertyValue(BBBCmsConstants.REST_STATIC_TEMPLATE_PROMOBOX_IMAGE_LINK) == null ? ""
								: (String) promoBox
										.getPropertyValue(BBBCmsConstants.REST_STATIC_TEMPLATE_PROMOBOX_IMAGE_LINK);
						promoBoxVO = new PromoBoxVO(pId, pImageURL, pImageAltText, pImageMapName, pImageMapContent,
								pPromoBoxContent, pImageLink, "", "");
						if(promoBoxList==null) {
							promoBoxList = new ArrayList<PromoBoxVO>();
						}
						promoBoxList.add(promoBoxVO);
					}
				}
			}
		} catch (TargetingException e) {
			logError(e.getMessage(),e);
		}
		  
		
		logDebug("KickStarterManager:getPromoBoxVOs: EXIT");
		
		  
		BBBPerformanceMonitor.end( KickStarterManager.class.getName() + " : " + "getPromoBoxVOs(registryType,siteId,channel,pageName)");
		  
 	    return promoBoxList;
	}


	public KickStarterItemsVO getPopulatedVO(List<RepositoryItem> kickStarterDataItems){
		
		 BBBPerformanceMonitor.start( KickStarterManager.class.getName() + " : " + "getPopulatedVO(kickStarterDataItems)");
	
	     logDebug("KickStarterManager:getPopulatedVO: Start");
	
		 
		  List<KickStarterVO> topConsultants =(List) new ArrayList<KickStarterVO>();
		  KickStarterItemsVO KickStarterItemsVO = new KickStarterItemsVO();
		  if(kickStarterDataItems!=null){
			  for(RepositoryItem kickStarterDataItem:kickStarterDataItems){
				  KickStarterVO kickStarterVO=new KickStarterVO();
				  if(kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.ID)!=null){
				  kickStarterVO.setId((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.ID));
				  }
				  if(kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.HEADING1)!=null){
				  kickStarterVO.setHeading1((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.HEADING1));
				  }
				  if(kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.ISACTIVE)!=null){
				  kickStarterVO.setIsActive((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.ISACTIVE));
				  }
				  if(kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.IMAGEURL) !=null){					  
				  kickStarterVO.setImageUrl((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.IMAGEURL));
				  }
				  if(kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.HEADING2)!=null){
				  kickStarterVO.setHeading2((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.HEADING2));
				  }
				  if(kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.DESCRIPTION)!=null){
				  kickStarterVO.setDescription((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.DESCRIPTION));
				  }
				  if(kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.PRIORITY)!=null){
				  kickStarterVO.setPriority((Integer)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.PRIORITY));
				  }
				  if(kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.KICKSTARTER_TYPE)!=null){
				  kickStarterVO.setKickstarterType((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.KICKSTARTER_TYPE));
				  }
				  if(kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.HEROIMAGEURL) !=null){
					kickStarterVO.setHeroImageUrl((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.HEROIMAGEURL));
				  }
				  if(kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.KICKSTARTER_TYPE) !=null){
						kickStarterVO.setKickstarterType((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.KICKSTARTER_TYPE));
				  }
 					 if(kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.IMAGE_ALT_ATTR) !=null){
						kickStarterVO.setImageAltAttr((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.IMAGE_ALT_ATTR));
 					 }
				  if(kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.HEROIMAGE_ALT_ATTR) !=null){
						kickStarterVO.setHeroImageAltAttr((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.HEROIMAGE_ALT_ATTR));
				  }
				  topConsultants.add(kickStarterVO);
				  kickStarterVO=null;
			  }
			  KickStarterItemsVO.setKickStarterItems(topConsultants);
		  }else{
			  logDebug("no result found for kickStarterDataItems");
		  }
		  
		  logDebug("KickStarterManager:getPopulatedVO: EXIT");
		
		
		  BBBPerformanceMonitor.end( KickStarterManager.class.getName() + " : " + "getPopulatedVO(kickStarterDataItems)");
		  
		  return KickStarterItemsVO;
	  }

	  /*
	   * This method gets consultant details as per consultantId.
	   */
	  @SuppressWarnings("unchecked")
	public KickStarterVO getKickStarterDetailsForRest(String siteId,String consultantId, String registryType, String channel, String pageName) throws RepositoryException{
		  
		  BBBPerformanceMonitor.start( KickStarterManager.class.getName() + " : " + "getKickStarterDetailsForRest");
		  
		  KickStarterVO kickStarterVO = getKickStarterDetails (siteId, consultantId, registryType);
		  kickStarterVO.setPromoBoxVOList(getPromoBoxVOs(registryType, siteId, channel, pageName));
		  
		  BBBPerformanceMonitor.end( KickStarterManager.class.getName() + " : " + "getKickStarterDetailsForRest");
		  
		  return kickStarterVO;
	}

	/*
	   * This method gets consultant details as per consultantId.
	   */
	  @SuppressWarnings("unchecked")
	public KickStarterVO getKickStarterDetails(String siteId,String consultantId, String registryType) throws RepositoryException{
		  
		  BBBPerformanceMonitor.start( KickStarterManager.class.getName() + " : " + "getKickStarterDetails(siteId,consultantId,registryType)");
		  	
		  KickStarterVO kickStarterVO = null;
		  RepositoryItem kickStarterDataItem = null;
		  Set<RepositoryItem> pickLists= new HashSet<RepositoryItem>();
		  List<PicklistVO> picklistsVO=new ArrayList<PicklistVO>();
		  String cacheName = null;
		  int cacheTimeout = 0;
		  Object kickStarterKey = consultantId+BBBCoreConstants.UNDERSCORE+siteId+BBBCoreConstants.UNDERSCORE+registryType;
		  cacheName = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY , BBBCoreConstants.SEARCH_RESULT_CACHE_NAME);
			try {
				cacheTimeout = Integer.parseInt(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.HTML_CACHE_CONFIG_KEY, BBBCoreConstants.KICK_STARTERS_CACHE_TIMEOUT));
				cacheTimeout = cacheTimeout*1000;
			} catch (NumberFormatException e) {
				logError("EndecaSearch.performSearch || NumberFormatException occured in " +
						GETTING_CACHE_TIMEOUT_FOR + BBBCoreConstants.KICK_STARTERS_CACHE_TIMEOUT, e);
				cacheTimeout = getKeywordCacheTimeout();
			} catch (NullPointerException e) {
				logError("EndecaSearch.performSearch || NullPointerException occured in " +
						GETTING_CACHE_TIMEOUT_FOR + BBBCoreConstants.KICK_STARTERS_CACHE_TIMEOUT, e);
				cacheTimeout = getKeywordCacheTimeout();
			}
			logDebug("cacheName:" + cacheName);
			logDebug("kickStartersCacheTimeout :: " + cacheTimeout); 
			
			kickStarterVO = (KickStarterVO) getObjectCache().get(kickStarterKey, cacheName);
			if(null == kickStarterVO) {
				kickStarterVO = new KickStarterVO();
				 kickStarterDataItem = this.kickStarterTools.getTopConsultantDetails(consultantId);
				  if(kickStarterDataItem !=null){
					  if(kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.ID)!=null){
						  kickStarterVO.setId((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.ID));	
					  }
						  if(kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.HEADING1)!=null){
						  kickStarterVO.setHeading1((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.HEADING1));
						  }
						  if(kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.ISACTIVE)!=null){
						  kickStarterVO.setIsActive((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.ISACTIVE));
						  }
						  if(kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.IMAGEURL) !=null){
						  kickStarterVO.setImageUrl((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.IMAGEURL));
						  }
						  if(kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.HEADING2)!=null){
						  kickStarterVO.setHeading2((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.HEADING2));
						  }
						  if(kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.DESCRIPTION)!=null){
						  kickStarterVO.setDescription((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.DESCRIPTION));
						  }
						  if(kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.PRIORITY)!=null){
						  kickStarterVO.setPriority((Integer)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.PRIORITY));
						  }
						  if(kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.HEROIMAGEURL) !=null){
								kickStarterVO.setHeroImageUrl((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.HEROIMAGEURL));
						  }
						  if(kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.KICKSTARTER_TYPE) !=null){
								kickStarterVO.setKickstarterType((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.KICKSTARTER_TYPE));
						  }
		 					if(kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.IMAGE_ALT_ATTR) !=null){
								kickStarterVO.setImageAltAttr((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.IMAGE_ALT_ATTR));
		 					}
						  if(kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.HEROIMAGE_ALT_ATTR) !=null){
								kickStarterVO.setHeroImageAltAttr((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.HEROIMAGE_ALT_ATTR));
						  }
						  				  
						  pickLists =(Set<RepositoryItem>) kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.KICKSTARTER_PICKLISTS);
						  
						  picklistsVO = getPickListVO(pickLists, registryType, siteId);			
						  
						  if(picklistsVO !=null && !picklistsVO.isEmpty() ){
							  kickStarterVO.setKickStarterPicklists(picklistsVO);
						  }
						  getObjectCache().put(kickStarterKey, kickStarterVO, cacheName, cacheTimeout);
						  logDebug("kickStarter search is Cached");
						  logDebug("kickStarter consultantId: "+consultantId);
				}else{
					  logDebug(" no result found for kickStarterDataItem"+kickStarterDataItem +" throwing BBBBusinessException");
				  }
				
			} else {
				logDebug("kickStarterVO found in Cache :: " + kickStarterVO);  
			}
			
		 
		  
		  BBBPerformanceMonitor.end( KickStarterManager.class.getName() + " : " + "getKickStarterDetails(siteId,consultantId,registryType)");
		  
		  return kickStarterVO;
	  }
	  
	  
	 /*
	  * Method to populate the VO with pick list details 
	  */
	  @SuppressWarnings("unchecked")
	public  List<PicklistVO> getPickListVO(Set<RepositoryItem> pickLists, String registryType, String siteId){
		  
		  BBBPerformanceMonitor.start( KickStarterManager.class.getName() + " : " + "getPickListVO(pickLists,registryType,siteId)");
		  
		  
		  RepositoryItem pickListSite;
		  String consultantSiteId=null;
		  Set<RepositoryItem> registryTypes;
		  List<RepositoryItem> topSkus=new ArrayList<RepositoryItem>();
		  List<TopSkuVO> topSkuItems=new ArrayList<TopSkuVO>();
		  List<PicklistVO> picklistsVO=new ArrayList<PicklistVO>();
		  
		  
		  for(RepositoryItem pickList:pickLists){
			  PicklistVO picklistVO=new PicklistVO();
			  List<String> consulatantRegistryTypes = new ArrayList<String>();
			  picklistVO.setId((String)pickList.getPropertyValue(BBBGiftRegistryConstants.ID));
			  if(pickList.getPropertyValue(BBBGiftRegistryConstants.TITLE)!=null){
			  picklistVO.setTitle((String)pickList.getPropertyValue(BBBGiftRegistryConstants.TITLE));
			  }
			  if(pickList.getPropertyValue(BBBGiftRegistryConstants.CONSULTANT_TYPE)!=null){
			  picklistVO.setCustomerType((String)pickList.getPropertyValue(BBBGiftRegistryConstants.CONSULTANT_TYPE));
			  }
			  if(pickList.getPropertyValue(BBBGiftRegistryConstants.PICKLIST_DESCRIPTION)!=null){
			  picklistVO.setPickListDescription((String)pickList.getPropertyValue(BBBGiftRegistryConstants.PICKLIST_DESCRIPTION));
			  }
			  pickListSite = (RepositoryItem) pickList.getPropertyValue(BBBGiftRegistryConstants.KICKSTARTER_SITES);
			  if(pickListSite!=null)
			  consultantSiteId =(String) pickListSite.getPropertyValue(BBBGiftRegistryConstants.ID);
			  
			  if(consultantSiteId.equals(BBBCoreConstants.SITE_BAB_US) && siteId.equals("TBS_BedBathUS")){
				  consultantSiteId="TBS_BedBathUS";
				}
			  else if(consultantSiteId.equals(BBBCoreConstants.SITE_BBB) && siteId.equals("TBS_BuyBuyBaby")){
					consultantSiteId="TBS_BuyBuyBaby";
				}
			  else if(consultantSiteId.equals(BBBCoreConstants.SITE_BAB_CA) && siteId.equals("TBS_BedBathCanada")){
					consultantSiteId="TBS_BedBathCanada";
				}
			  
			  if(pickList.getPropertyValue(BBBGiftRegistryConstants.REGISTRYTYPES)!=null && !((Set<RepositoryItem>) pickList.getPropertyValue(BBBGiftRegistryConstants.REGISTRYTYPES)).isEmpty()){
			  registryTypes = (Set<RepositoryItem>) pickList.getPropertyValue(BBBGiftRegistryConstants.REGISTRYTYPES);
			  List<RegistryTypeVO> listRegistryVo = new ArrayList<RegistryTypeVO>();
			  RegistryTypeVO regTypeVo = new RegistryTypeVO();
			  if(!registryTypes.isEmpty()){
					for(RepositoryItem consulatantRegistryType:registryTypes){
						RepositoryItem conRegTypeItem =((RepositoryItem) consulatantRegistryType.getPropertyValue(BBBGiftRegistryConstants.REGISTRYTYPE));						
						if(conRegTypeItem!=null){
						regTypeVo.setId(consulatantRegistryType.getRepositoryId());
						String regName = (String) conRegTypeItem.getPropertyValue(BBBGiftRegistryConstants.REGISTRY_ITEMDESCRIPTOR_SITEREPO);
						String registryTypeimageUrl = (String)consulatantRegistryType.getPropertyValue(BBBGiftRegistryConstants.REGISTRY_IMAGE_URL);
						regTypeVo.setRegistryTypeimageUrl(registryTypeimageUrl);
						if(regName!=null){
						consulatantRegistryTypes.add(regName.toLowerCase());
						regTypeVo.setRegisteryType(regName);						
						                  }						                     
						listRegistryVo.add(regTypeVo);
						}
						}
			  }
			  if(listRegistryVo!=null && !listRegistryVo.isEmpty()){
			  picklistVO.setRegistryTypes(listRegistryVo);
			  }
				if (registryType != null
						&& !consulatantRegistryTypes.isEmpty()
						&& consulatantRegistryTypes.contains(registryType.toLowerCase())
						&& consultantSiteId.equalsIgnoreCase(siteId)) {
					
					topSkus = (List<RepositoryItem>) pickList.getPropertyValue(BBBGiftRegistryConstants.PICKLIST_SKUS);
					topSkuItems = getTopSkuItems(topSkus,registryType,siteId);
					if(topSkuItems !=null && !topSkuItems.isEmpty()){
					picklistVO.setTopSkus(topSkuItems);
					picklistsVO.add(picklistVO);
					}
				} else if (StringUtils.isEmpty(registryType)
						&& consultantSiteId.equalsIgnoreCase(siteId)) {
					
					topSkus = (List<RepositoryItem>) pickList.getPropertyValue(BBBGiftRegistryConstants.PICKLIST_SKUS);
					topSkuItems = getTopSkuItems(topSkus,registryType,siteId);
					if (topSkuItems != null && !topSkuItems.isEmpty()) {
						picklistVO.setTopSkus(topSkuItems);
						picklistsVO.add(picklistVO);
					}
				}else if(!StringUtils.isEmpty(registryType) 
                        && consultantSiteId.equalsIgnoreCase(siteId))
					{
					try {
						List<String> giftRegistryConfig = getCatalogTools().getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG,siteId);
						if(consulatantRegistryTypes.contains(giftRegistryConfig.get(0).toLowerCase())){
							topSkus = (List<RepositoryItem>) pickList.getPropertyValue(BBBGiftRegistryConstants.PICKLIST_SKUS);
							topSkuItems = getTopSkuItems(topSkus,registryType,siteId);
							if(topSkuItems !=null && !topSkuItems.isEmpty()){
							picklistVO.setTopSkus(topSkuItems);
							picklistsVO.add(picklistVO);
							}
						}
					} catch (BBBSystemException ex) {
						
						logDebug("BBB SystemException  in fetching consultant picklist : "+ ex.getMessage());
						
					} catch (BBBBusinessException xe) {
						
						logDebug("BBB BusinessException  in fetching consultant picklist : "+ xe.getMessage());
						
					}
				}
			  }
				
  }
		  BBBPerformanceMonitor.end( KickStarterManager.class.getName() + " : " + "getPickListVO(pickLists,registryType,siteId)");
		  
		  return picklistsVO;
	  }
	  
	/**
	 *  This method is used to get popular items from tools class
	 * 
	 * @param pRegistryType -type of the registry
	 * @param pSiteId - site id
	 * @return List<ProductVO> - list of product vo objects
	 * @throws RepositoryException - exeception while getting items from repository
	 */
	public List<ProductVO> getPopularItemsDetails(String pRegistryType , String pSiteId) throws RepositoryException{	
		
		BBBPerformanceMonitor.start( KickStarterManager.class.getName() + " : " + "getPopularItemsDetails(pRegistryType,pSiteId)");
		
		
		logDebug("Enter.KickStarterManager.getPopularItemsDetails");
		
		RepositoryItem[]  popularItems = getKickStarterTools().getPopularItems(pRegistryType , pSiteId);
		
			if(popularItems!=null){
				logDebug(Arrays.toString(popularItems));
			}else{
				logDebug("No Popular repository Items");
			}
			
				
		String bccSearchQuery = null;
		List<ProductVO> prodList = null;
		if(popularItems!=null && popularItems.length>=1){	
			for (RepositoryItem popularItem : popularItems){
			bccSearchQuery =  (String) popularItem.getPropertyValue(SEARCHQUERY);
			Integer resultIsize =  (Integer) popularItem.getPropertyValue(RESULTSIZE);			
			if(bccSearchQuery!=null && resultIsize!=null ){	
				prodList = getEndecaSearch().getPopularItemsResults(prodList , bccSearchQuery, pSiteId);
				if(prodList != null
					&& prodList.size() > 5) {
					Collections.shuffle(prodList);
					prodList = prodList.size() > resultIsize? prodList.subList(0, resultIsize):prodList;
				}
				
					if(prodList!=null){
						logDebug("Product List is not null " + Arrays.toString(popularItems));	
					}else{
						logDebug("Products are null");
					}
					
				
			}else{
				
					logDebug("BCC search Query is null..");
				
			}
		}
		}
		if (prodList != null && !prodList.isEmpty()) {
			for (ProductVO productVO : prodList) {
				if (null != productVO) {
					if (null != productVO.getBbbProduct()) {
						if (null != productVO.getBbbProduct().getReviews()) {
							productVO.getBvReviews().setTotalReviewCount(Integer.parseInt(productVO.getBbbProduct().getReviews()));
						}
						if (null != productVO.getBbbProduct().getRatingForCSS()) {
							productVO.getBvReviews().setAverageOverallRating((float) productVO.getBbbProduct().getRatingForCSS() / 10);
						}
					}
				}
			}
		}
		
		logDebug("Exit.KickStarterManager.getPopularItemsDetails");
		BBBPerformanceMonitor.end( KickStarterManager.class.getName() + " : " + "getPopularItemsDetails(pRegistryType,pSiteId)");
		
		return prodList;
	  }
	  /*
	   * Methos to populate topskuvo 
	   */
	  List<TopSkuVO> getTopSkuItems(List<RepositoryItem> topSkus, String registryType, String siteId){
		  
		  BBBPerformanceMonitor.start( KickStarterManager.class.getName() + " : " + "getTopSkuItems(topSkus, registryType , siteId)");
		  
		  List<TopSkuVO> topSkuItems=new ArrayList<TopSkuVO>();
		  
		  for(RepositoryItem topSku:topSkus){
				TopSkuVO topSkuVO=new TopSkuVO();
				TopSkuVO catalogSkuvo = null;
				if(topSku.getPropertyValue(BBBGiftRegistryConstants.SKU)!=null){
					topSkuVO.setSkuId((String)topSku.getPropertyValue(BBBGiftRegistryConstants.SKU));
					catalogSkuvo = setTopSkuVO(topSkuVO, topSku, siteId);
				}
				if(topSku.getPropertyValue(BBBGiftRegistryConstants.REC_QTY) !=null){
				topSkuVO.setRecommanded_qty((Integer)topSku.getPropertyValue(BBBGiftRegistryConstants.REC_QTY));
				}
				if(topSku.getPropertyValue(BBBGiftRegistryConstants.COMMENT)!=null){
				topSkuVO.setComment((String)topSku.getPropertyValue(BBBGiftRegistryConstants.COMMENT));
				}
				if(topSku.getPropertyValue(BBBGiftRegistryConstants.ID) !=null){
				topSkuVO.setId((String)topSku.getPropertyValue(BBBGiftRegistryConstants.ID));
				}
				if(catalogSkuvo!=null && catalogSkuvo.getProductVO()!=null && catalogSkuvo.getProductVO().getProductId()!=null){
				topSkuItems.add(topSkuVO);
				}
			}
		  
		  BBBPerformanceMonitor.end( KickStarterManager.class.getName() + " : " + "getTopSkuItems(topSkus, registryType , siteId)");
		  
		  return topSkuItems;
	  }
	  
	  /*   
	   * Method to send specific sku and product information to rest api
	   * 
	   * 
	   */
	  public TopSkuVO setTopSkuVO(TopSkuVO topSkuVO,RepositoryItem topSku, String siteId){
		  
		  BBBPerformanceMonitor.start( KickStarterManager.class.getName() + " : " + "setTopSkuVO(topSkuVO, topSku , siteId)");
		  
		  
		  double salePrice =0.0;
		  double listPrice = 0.0;
		  double inCartPrice=0.0;
		  TopSkuDetailVO topSkuDetailVO=new TopSkuDetailVO();
		  TopSkuProductVO topSkuProductVO=new TopSkuProductVO();
		  String productId= null;
		  ImageVO skuImagesVO = null;
		  try {
				
			  SKUDetailVO skuDetailVO = catalogTools.getSKUDetails(siteId,(String)topSku.getPropertyValue(BBBGiftRegistryConstants.SKU), false);
				productId = catalogTools.getParentProductForSku((String)topSku.getPropertyValue(BBBGiftRegistryConstants.SKU));
			  ProductVO	productVO = catalogTools.getProductDetails(siteId, productId);
			  BazaarVoiceProductVO  bvDetails = productVO.getBvReviews();
				salePrice = catalogTools.getSalePrice(productId, (String)topSku.getPropertyValue(BBBGiftRegistryConstants.SKU));
				listPrice = catalogTools.getListPrice(productId, (String)topSku.getPropertyValue(BBBGiftRegistryConstants.SKU));
				// For gift owner the price of the sku to be used from in cart price list if in cart sku : BBBH-2408
				
				boolean isInCartSKU=skuDetailVO.isInCartFlag();
				
				if(isInCartSKU){
					inCartPrice=catalogTools.getIncartPrice(productId, (String)topSku.getPropertyValue(BBBGiftRegistryConstants.SKU));
				}
				if(skuDetailVO != null){
					if(skuDetailVO.getColor()!=null){
					topSkuDetailVO.setColor(skuDetailVO.getColor());
					}
					if(skuDetailVO.getDescription()!=null){
					topSkuDetailVO.setDescription(skuDetailVO.getDescription());
					}
					if(skuDetailVO.getDisplayName()!=null){
					topSkuDetailVO.setDisplayName(skuDetailVO.getDisplayName());
					}
					if(skuDetailVO.getSize()!=null){
					topSkuDetailVO.setSize(skuDetailVO.getSize());
					}
					if(skuDetailVO.getUpc()!=null){
					topSkuDetailVO.setUpc(skuDetailVO.getUpc());
					}
					topSkuDetailVO.setSkuImages(skuDetailVO.getSkuImages());
					topSkuDetailVO.setCustomizableRequired(skuDetailVO.isCustomizableRequired());					
				    topSkuDetailVO.setLtlItem(skuDetailVO.isLtlItem());
				    //BBBH-3983 | For handling free shipping message with incart eligible skus
				    catalogTools.updateShippingMessageFlag(skuDetailVO,skuDetailVO.isInCartFlag());
					topSkuDetailVO.setDisplayShipMsg(skuDetailVO.getDisplayShipMsg());
					topSkuDetailVO.setPricingLabelCode(skuDetailVO.getPricingLabelCode());
					topSkuDetailVO.setInCartFlag(skuDetailVO.isInCartFlag());
					
				}
		  
				if(productVO != null){
					//commenting out this code for now to reduct amount of
					//data that we send to mobile
					topSkuProductVO.setCollection(productVO.isCollection());
					topSkuProductVO.setName(productVO.getName());
					topSkuProductVO.setProductId(productVO.getProductId());
					if(productVO.getProductImages()!=null){
					topSkuProductVO.setProductImages(productVO.getProductImages());
					}
					topSkuProductVO.setSeoUrl(productVO.getSeoUrl());
				}
				if(bvDetails !=null){
					topSkuProductVO.setBvProductVO(bvDetails);
				}
		  
		  } catch (BBBSystemException | BBBBusinessException  e) {
			  logError(e.getMessage(),e);
			} 
		topSkuVO.setSkuDetailVO(topSkuDetailVO);
		topSkuVO.setProductVO(topSkuProductVO);
		topSkuVO.setSalePrice(String.valueOf(salePrice));
		topSkuVO.setListPrice(String.valueOf(listPrice));
		topSkuVO.setInCartPriceVal(String.valueOf(inCartPrice));
		
		 BBBPerformanceMonitor.end( KickStarterManager.class.getName() + " : " + "setTopSkuVO(topSkuVO, topSku , siteId)");
		 
		 
		return topSkuVO;
	  }


 /** 
  * This method is used to set kick starter items into a session VO 
  * @param pGiftRegistryViewBean - Registry Bean Vo
  * @param pBBBSessionBean - Session Bean Vo
  * @param pSiteId - Site Id
  * @param pBBBCatalogTools - Catalog Tools
  */
public void  setKickStarterItemsIntoSessionVo(String jsonString ,GiftRegistryViewBean pGiftRegistryViewBean , BBBSessionBean pBBBSessionBean ){
	
	BBBPerformanceMonitor.start( KickStarterManager.class.getName() + " : " +
	                            "setKickStarterItemsIntoSessionVo(jsonString, pGiftRegistryViewBean , pBBBSessionBean)");
	
	   
		logDebug("Enter.KickStarterManager.setKickStarterItemsIntoSessionVo");
		logDebug(jsonString);
     	
	        pGiftRegistryViewBean.setAdditem(null);
			StringTokenizer jsonStTokenizer = new StringTokenizer(jsonString,ARRAY_SEPARETOR);
			 int arrayCounter = 0;		
			 boolean breakMethod = true;
			while(jsonStTokenizer.hasMoreTokens()){
			 String stringValue =	jsonStTokenizer.nextToken();
			 if(arrayCounter== BBBCoreConstants.ZERO){
				 StringTokenizer stringSepartedValues = new StringTokenizer(stringValue,FRD_SLASH_SEPARETOR);
				 while(stringSepartedValues.hasMoreTokens()){			 
					logDebug("first Item "+stringValue);
					 String skuVoValue = stringSepartedValues.nextToken();
					 StringTokenizer skuVo = new StringTokenizer(skuVoValue,COMMA_SEPARETOR);
					 int j = BBBCoreConstants.ZERO;
					 String productId = null;
					 String skuId = null;
					 String quantity = null;
					 String price = null;
					 while(skuVo.hasMoreTokens()){					
						 if(j ==BBBCoreConstants.ZERO){
							 productId = skuVo.nextToken();
						 }else if (j==BBBCoreConstants.ONE){
							 skuId = skuVo.nextToken();
						 }else if (j==BBBCoreConstants.TWO){
							 quantity = skuVo.nextToken();	
							 if(quantity.equals("0")){
								 quantity = null;
							 }
						 }	else if (j==BBBCoreConstants.THREE){
							 price = skuVo.nextToken();
						 }				
					  j = j + BBBCoreConstants.ONE;
					 }
					 if(productId!=null && skuId !=null && quantity!=null){
						 breakMethod = false;
						 pGiftRegistryViewBean = addAllItemsToGiftRegistryViewBean(pGiftRegistryViewBean,productId,skuId,quantity,price);
						 
					 }
				 }
				 if(breakMethod){
					 break;
				 }
				 arrayCounter = arrayCounter+1;
				 String omniProductListNew = pGiftRegistryViewBean.getOmniProductList();
				 omniProductListNew = omniProductListNew.substring(0,
						 omniProductListNew.lastIndexOf(","));
				 pGiftRegistryViewBean.setOmniProductList(omniProductListNew);
			 }else{
				 StringTokenizer stringSepartedValues = new StringTokenizer(stringValue,COMMA_SEPARETOR);
				 
				 
				logDebug("not first item "+stringValue);
				 
				 while(stringSepartedValues.hasMoreTokens()){			 
					 String keyName= stringSepartedValues.nextToken();
					 String[] key = keyName.split(BBBGiftRegistryConstants.EQUAL);
					 if(key[0]!=null && key[0].equals(BBBGiftRegistryConstants.REGISTRY_ID)){
						 if(key[1]!=null){
						 pBBBSessionBean.setRegistryId(key[1]);
						 }
					 }else if (key[0]!=null && key[0].equals(BBBGiftRegistryConstants.EVENT_TYPE)){
						 if(key[1]!=null){
							 pBBBSessionBean.setEventType(key[1]);
							 }
					 }else if (key[0]!=null && key[0].equals(BBBGiftRegistryConstants.ID)){
						 if(key[1]!=null){
							 pBBBSessionBean.setKickStarterId(key[1]);
							 }
					 }
				 }
			 }
			 		}
			pGiftRegistryViewBean.setRegistryId(pBBBSessionBean.getRegistryId());
			
			  
			logDebug("Exit.KickStarterManager.setKickStarterItemsIntoSessionVo");
		     
	    BBBPerformanceMonitor.end( KickStarterManager.class.getName() + " : " + 
		     	"setKickStarterItemsIntoSessionVo(jsonString, pGiftRegistryViewBean , pBBBSessionBean)");
			   
	  }
  
	/**
	 * This method is used to set user inputs into a java bean
	 * @param pGiftRegistryViewBean - view registry bean
	 * @param pKickStarterProductIds - holds list of product ids
	 * @param pKickStarterSkuIds - holds list of sku ids
	 * @param pKickStarteQuantities - houlds list of quantities
	 * @param pPrice - price of a sku
	 */
	public GiftRegistryViewBean addAllItemsToGiftRegistryViewBean(
			GiftRegistryViewBean pGiftRegistryViewBean,
			String pKickStarterProductIds, String  pKickStarterSkuIds,
			String  pKickStarteQuantities,String pPrice) {
		String omniProductList = "";
		BBBPerformanceMonitor.start( KickStarterManager.class.getName() + " : " + 
		     	"addAllItemsToGiftRegistryViewBean");
		
		
		logDebug("Start.KickStarterManager.addAllItemsToGiftRegistryViewBean");
		
		  
		if(pKickStarterSkuIds!=null){			
			List<AddItemsBean> additemList = null;
			if(pGiftRegistryViewBean.getAdditem()!=null && !pGiftRegistryViewBean.getAdditem().isEmpty()){
				additemList = pGiftRegistryViewBean.getAdditem();
			}else{
				additemList = new ArrayList<AddItemsBean>();
			}
		
			final AddItemsBean addItemsBean = new AddItemsBean();
			if(pKickStarterProductIds != null){
				addItemsBean.setProductId(pKickStarterProductIds);
			}
				addItemsBean.setSku(pKickStarterSkuIds);	
			if(pKickStarteQuantities != null){
				addItemsBean.setQuantity(pKickStarteQuantities);	
			}		
			if(pPrice!=null){
				addItemsBean.setPrice(pPrice);
			}			
			additemList.add(addItemsBean);			
			 pGiftRegistryViewBean.setAdditem(additemList);
			 String omnitPrdOld = pGiftRegistryViewBean.getOmniProductList();
			 omniProductList = ";" + addItemsBean.getProductId()
				+ ";;;event22=" + addItemsBean.getQuantity()
				+ "|event23=" + pPrice + ";eVar30="+addItemsBean.getSku()+",";
			 if(omnitPrdOld!=null && !omnitPrdOld.isEmpty()){
				 omniProductList +=omnitPrdOld;
			 }
			 pGiftRegistryViewBean.setOmniProductList(omniProductList);
		}
		
		logDebug(pGiftRegistryViewBean.getAdditem().toString());
		
		
		BBBPerformanceMonitor.end( KickStarterManager.class.getName() + " : " + 
		     	"addAllItemsToGiftRegistryViewBean");
		
		 
		logDebug("Exit.KickStarterManager.addAllItemsToGiftRegistryViewBean");
		 
		  
		  return pGiftRegistryViewBean;
	}	

	/**
	 * Gets the gift registry manager.
	 * 
	 * @return mGiftRegMgr
	 */
	public GiftRegistryManager getGiftRegistryManager() {
		return mGiftRegMgr;
	}

	/**
	 * Sets the gift registry manager.
	 * 
	 * @param pGiftRegMgr
	 *            the new gift registry manager
	 */
	public void setGiftRegistryManager(final GiftRegistryManager pGiftRegMgr) {
		mGiftRegMgr = pGiftRegMgr;
	}


	/**
	 * @return the keywordCacheTimeout
	 */
	public int getKeywordCacheTimeout() {
		return keywordCacheTimeout;
	}



	/**
	 * @param keywordCacheTimeout the keywordCacheTimeout to set
	 */
	public void setKeywordCacheTimeout(int keywordCacheTimeout) {
		this.keywordCacheTimeout = keywordCacheTimeout;
	}
	
}