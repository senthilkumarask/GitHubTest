package com.bbb.commerce.checklist.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.checklist.tools.CheckListTools;
import com.bbb.commerce.checklist.vo.CategoryVO;
import com.bbb.commerce.checklist.vo.CheckListProgressVO;
import com.bbb.commerce.checklist.vo.CheckListVO;
import com.bbb.commerce.checklist.vo.MyItemVO;
import com.bbb.commerce.checklist.vo.NonRegistryGuideVO;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools;
import com.bbb.commerce.giftregistry.vo.RegistryItemVO;
import com.bbb.commerce.giftregistry.vo.RegistryItemsListVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBLocalCacheContainer;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

/**
 * 
 * This class provides the low level functionality for checklist
 * creation/manipulation.
 * 
 * @author ssi191
 * 
 */
public class CheckListManager extends BBBGenericService {
	private CheckListTools checkListTools;
	private BBBLocalCacheContainer cacheContainer;
	private MutableRepository checkListRepository;
	private GiftRegistryTools giftRegistryTools;
	private BBBCatalogTools catalogTools;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private static final String CLASS_NAME = "CheckListManager";
	private static final String GET_REGISTRY_CHECKLIST = "getRegistryCheckList";
	private static final String GET_STATIC_REGISTRY_CHECKLIST = "getStaticRegistryCheckList";
	private static final String FETCH_RLP_FLAG_FROM_CACHE = "fetchRlpFlagFromCache";
	private static final String SHOW_C1_CAT_ON_RLP = "showC1CategoryOnRlp";
	private static final String FETCH_RLP_FLAG = "fetchRlpFlagFromRepository";
	/**
	 * The method gets the static or dynamic checklist response based on the config key and isDisabled flag for the checklist based on registry type.
	 * @param registryId
	 * @param registryType
	 * @return CheckListVO
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException
	 */
	public CheckListVO getRegistryCheckList(final String registryId,String registryType,final String pageType) throws BBBSystemException, BBBBusinessException{
		BBBPerformanceMonitor.start(
				CLASS_NAME , GET_REGISTRY_CHECKLIST);
		this.logDebug("CheckListManager.getRegistryCheckList() method start");
		this.logDebug("CheckListManager.Input parameters are registryId: " + registryId + " ,registryType: " + registryType + ", page type = " + pageType);
		CheckListVO checkListVO  = new CheckListVO();	
		String regTypeCode = null;
		String siteId = SiteContextManager.getCurrentSiteId(); 		
		boolean fromGuidePage = BBBCoreConstants.GUIDE.equalsIgnoreCase(pageType);
		if(registryType.length()<=3 && !fromGuidePage){
			registryType = this.getCatalogTools().getRegistryTypeName(registryType,siteId);
		}
			if(!this.showCheckListButton(registryType) ){								
				if(!(registryType.length()<=3)){
				 regTypeCode = this.getGiftRegistryTools()
						.getRegistryTypeCode(registryType,siteId);
				} else if (fromGuidePage) {
					regTypeCode = registryType;
				}
				checkListVO.setCheckListDisabled(false);
				if(null != this.getCacheContainer() && null != this.getCacheContainer().get(regTypeCode)){
						this.logDebug("CheckListManager: fetching data from cache");
						if(pageType != null && pageType.equalsIgnoreCase(BBBCoreConstants.DYNAMIC) && this.getCheckListTools().showStaticCheckList(registryType).equalsIgnoreCase(BBBCoreConstants.ON)){
							this.logDebug("CheckListManager: fetching dynamic data from cache");
							checkListVO = this.getCheckListTools().populateDynamicAttributesInCachedVO(registryId,(CheckListVO)this.getCacheContainer().get(regTypeCode));
						}
						else{
							this.logDebug("CheckListManager: fetching static data from cache");
							checkListVO = (CheckListVO)this.getCacheContainer().get(regTypeCode);
							checkListVO.setRegistryId(registryId);
							return this.populateImageUrlInC1(checkListVO);	
						}
				}	
				else{
					RepositoryItem[] checkListItems = this.getCheckListTools().fetchCheckListRepositoryItem(registryType);	
					this.logDebug("CheckListManager: fetching data from repository");
					if(pageType != null && pageType.equalsIgnoreCase(BBBCoreConstants.DYNAMIC) && this.getCheckListTools().showStaticCheckList(registryType).equalsIgnoreCase(BBBCoreConstants.ON)){
						this.logDebug("CheckListManager: fetching dynamic data from repository");
						checkListVO = this.getCheckListTools().populateCheckListVO(registryId,checkListItems);
						this.getCacheContainer().put(regTypeCode, this.getCheckListTools().populateStaticCheckListVO(regTypeCode, false));
					}
					else{
						this.logDebug("CheckListManager: fetching static data from repository");
						checkListVO = this.getCheckListTools().populateStaticCheckListVO(registryId,checkListItems);
						return this.populateImageUrlInC1(checkListVO);	
					}
				}
			}			
			else{
				checkListVO.setCheckListDisabled(true);
			}
		checkListVO.setRegistryId(registryId);
		this.logDebug("CheckListManager.getRegistryCheckList() method end");
		BBBPerformanceMonitor.end(
				CLASS_NAME , GET_REGISTRY_CHECKLIST);
		return checkListVO;
	}	
	
	/**
	 * The method gets the static checklist response for the checklist based on registry type.
	 * @param registryType
	 * @return CheckListVO
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException
	 */
	public CheckListVO getStaticRegistryCheckList(String registryType) throws BBBSystemException, BBBBusinessException{
		BBBPerformanceMonitor.start(
				CLASS_NAME , GET_STATIC_REGISTRY_CHECKLIST);
		this.logDebug("CheckListManager.getStaticRegistryCheckList() method start");
		this.logDebug("CheckListManager.Input parameters are :registryType: " + registryType);
		CheckListVO checkListVO  = new CheckListVO();	
		String regTypeCode = null;
		String regType= null;
		String siteId = SiteContextManager.getCurrentSiteId(); 		
		if(registryType.length()<=3){
			regType = this.getCatalogTools().getRegistryTypeName(registryType,siteId);
			if(!StringUtils.isEmpty(regType)){
				registryType = regType;
			}
		}
			if(!this.showCheckListButton(registryType) ){								
				if(!(registryType.length()<=3)){
				 regTypeCode = this.getGiftRegistryTools()
						.getRegistryTypeCode(registryType,siteId);
				} 
				checkListVO.setCheckListDisabled(false);
				if(null == regTypeCode){
					regTypeCode = registryType;
				}
				if(null != this.getCacheContainer() &&  null != this.getCacheContainer().get(regTypeCode) ){
						this.logDebug("CheckListManager: fetching data from cache");
						checkListVO = (CheckListVO)this.getCacheContainer().get(regTypeCode);
				}
				else{	
					this.logDebug("CheckListManager: fetching data from repository");
					this.getCacheContainer().put(regTypeCode, this.getCheckListTools().populateStaticCheckListVO(regTypeCode, false));
					return (CheckListVO)this.getCacheContainer().get(regTypeCode);
				}		
			}					
			else{
				checkListVO.setCheckListDisabled(true);
			}
		this.logDebug("CheckListManager.getStaticRegistryCheckList() method end");
		BBBPerformanceMonitor.end(
				CLASS_NAME , GET_STATIC_REGISTRY_CHECKLIST);
		return checkListVO;
	}	
	
	/**
	 * The method gets Creates and return  Non registry Guide VO based on the guide type
	 * @param guideTypeCode
	 * @param pageType
	 * @return CheckListVO
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException
	 */
	public NonRegistryGuideVO getNonRegistryGuide(final String guideTypeCode, final String pageType) throws BBBSystemException, BBBBusinessException{
		BBBPerformanceMonitor.start(CLASS_NAME , GET_REGISTRY_CHECKLIST);
		this.logDebug("CheckListManager.getNonRegistryGuide method start. Input parameters , guideTypeCode: " + guideTypeCode + ", page type : " + pageType);

		NonRegistryGuideVO nonRegistryGuideVO  = new NonRegistryGuideVO();	
		boolean isGuideDisabled = true;
		boolean isCheckListGlobalFlagEnabled = this.getCheckListTools().showCheckList();
		//changes to fetch guide data from cache
		if(null != this.getCacheContainer() && null != this.getCacheContainer().get(guideTypeCode)){
			this.logDebug("CheckListManager: fetching data from cache");
			CheckListVO	checkVO = (CheckListVO)this.getCacheContainer().get(guideTypeCode);
			nonRegistryGuideVO = populateGuideVOFromCoherence(guideTypeCode, checkVO);
			isGuideDisabled = nonRegistryGuideVO.isGuideDisabled();
			if(isGuideDisabled)
			{
				nonRegistryGuideVO.setGuideDisabled(true);
			}
		}
		else{
			RepositoryItem[] checkListItems = this.getCheckListTools().fetchCheckListRepositoryItem(guideTypeCode);	
			if(!BBBUtility.isArrayEmpty(checkListItems)){
				isGuideDisabled = (boolean) checkListItems[0].getPropertyValue(BBBCoreConstants.IS_DISABLED);
			}
			if(isCheckListGlobalFlagEnabled && !isGuideDisabled) {								
				nonRegistryGuideVO = populateGuideVO(guideTypeCode, checkListItems);
			} else{
				nonRegistryGuideVO.setGuideDisabled(true);
			}
		}	

		this.logDebug("CheckListManager.getRegistryCheckList() method end");
		BBBPerformanceMonitor.end(CLASS_NAME , GET_REGISTRY_CHECKLIST);
		return nonRegistryGuideVO;
	}
	
	/**
	 * The method gets sets the value into and NonRegistryGuideVO
	 * @param guideTypeCode
	 * @param pageType
	 * @return CheckListVO
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException
	 */

	public NonRegistryGuideVO populateGuideVO(final String guideTypeCode, RepositoryItem[] checkListItems) {
		
		this.logDebug("CheckListManager.populateGuideVO() method starts for Guide type = " + guideTypeCode);
		NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO();
		nonRegistryGuideVO.setGuideTypeCode(guideTypeCode);
		nonRegistryGuideVO.setGuideDisplayName((String) ((RepositoryItem) checkListItems[0]).getPropertyValue(BBBCoreConstants.DISPLAY_NAME));
		this.logDebug("CheckListManager.populateGuideVO() method ends");
		return nonRegistryGuideVO;
	}
	
	/**
	 * The method gets sets the value into and NonRegistryGuideVO from cache
	 * @param guideTypeCode
	 * @param pageType
	 * @return CheckListVO
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException
	 */

	public NonRegistryGuideVO populateGuideVOFromCoherence(final String guideTypeCode, CheckListVO checkListItems) {
		
		this.logDebug("CheckListManager.populateGuideVOFromCoherence() method starts for Guide type = " + guideTypeCode);
		NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO();
		nonRegistryGuideVO.setGuideTypeCode(guideTypeCode);
		nonRegistryGuideVO.setGuideDisplayName( checkListItems.getDisplayName());
		this.logDebug("CheckListManager.populateGuideVOFromCoherence() method ends");
		return nonRegistryGuideVO;
	}
	
	/**
	 * Gets the my item vo.
	 *
	 * @param registryId the registry id
	 * @param registryType the registry type
	 * @return the my item vo
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public MyItemVO getMyItemVO(final String registryId,
			final String registryType) throws BBBSystemException,
			BBBBusinessException {
		
		CheckListVO checkListVO = getRegistryCheckList(registryId, registryType, BBBCoreConstants.STATIC);
		
		return this.getCheckListTools().populateMyItemVO(checkListVO);
				
	}
	
	/**
	 * The method populates the C1 image url in static checklist vo.
	 * @param checkListVO
	 */
	public CheckListVO populateImageUrlInC1(CheckListVO checkListVO){
		this.logDebug("CheckListManager.populateImageUrlInC1() method start");
		String siteId = SiteContextManager.getCurrentSiteId();
	
		for(CategoryVO c1CatVO : checkListVO.getCategoryListVO()){
			if(null != siteId && !StringUtils.isEmpty(siteId)){
				if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
					c1CatVO.setImageURL(c1CatVO.getCaImageURL());
					
					}
				else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)){
					c1CatVO.setImageURL(c1CatVO.getImageURL());
				}
				else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)){
					c1CatVO.setImageURL(c1CatVO.getBabyImageURL());
				}
			}
		}
		this.logDebug("CheckListManager.populateImageUrlInC1() method end");
		return checkListVO;
	}
	
	/**
	 * The method fetches the show on RLP flag from cache.
	 * @param registryId
	 * @param regTypeCode
	 * @param c1CategoryId
	 * @return String
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException
	 */
	public String fetchRlpFlagFromCache(String regTypeCode,String c1CategoryId,String registryId) throws BBBBusinessException, BBBSystemException{
		BBBPerformanceMonitor.start(
				"CheckListManager", "fetchRlpFlagFromCache()");
		this.logDebug("Inside CheckListManager: fetchRlpFlagFromCache()");
		this.logDebug("Input parameters are: regTypeCode: " + regTypeCode + " ,c1CategoryId: " + c1CategoryId + " ,registryId: " + registryId);
		RepositoryItem skuItem = null;
		List<String> totalSkuCatList = new ArrayList<String>();
		CheckListVO checkListVO = (CheckListVO)this.getCacheContainer().get(regTypeCode);
		if(!BBBUtility.isListEmpty(checkListVO.getCategoryListVO()))
		{
			for(CategoryVO c1CatVO : checkListVO.getCategoryListVO()){
				if(c1CatVO.getCategoryId().equalsIgnoreCase(c1CategoryId)){
					if(c1CatVO.isAlwaysShowOnRlp()){
						this.logDebug("Always Show on RLP flag is true");
						BBBPerformanceMonitor.end(
								CLASS_NAME, FETCH_RLP_FLAG_FROM_CACHE);
						return BBBCoreConstants.SHOW;
					}
					else if(!c1CatVO.isAlwaysShowOnRlp() && !c1CatVO.isShowOnRlp()){
						this.logDebug("Show on RLP flag is false and also always show on RLP flag is false");
						BBBPerformanceMonitor.end(
								"CheckListManager", "fetchRlpFlagFromCache()");
						return BBBCoreConstants.OTHER;
					}
					else{
						BBBPerformanceMonitor.end(
								CLASS_NAME, FETCH_RLP_FLAG_FROM_CACHE);
						return fetchC1ShowFlagForCategoryFromCache(registryId,skuItem,totalSkuCatList,c1CatVO);
					}					
				}	

			}
		}
		return BBBCoreConstants.HIDE;
	}
	
	/**
	 * The method fetches the show on RLP flag from repository if data is not present in cache.
	 * @param registryId
	 * @param c1CategoryId
	 * @return String
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException
	 */
	public String fetchRlpFlagFromRepository(String c1CategoryId,String registryId) throws BBBSystemException, BBBBusinessException{
		BBBPerformanceMonitor.start(
				CLASS_NAME, FETCH_RLP_FLAG);
		this.logDebug("Inside CheckListManager: fetchRlpFlagFromCache()");
		this.logDebug("Input parameters are: " + " c1CategoryId: " + c1CategoryId + " ,registryId: " + registryId);
		RepositoryItem skuItem = null;
		MutableRepositoryItem c1CheckListItem = null;
		List<String> totalSkuCatList = new ArrayList<String>();
		try {
			if (!BBBUtility.isEmpty(c1CategoryId)) {
				c1CheckListItem = (MutableRepositoryItem) this.getCheckListRepository().getItem(c1CategoryId,BBBCoreConstants.CHECKLIST_CATEGORY_ITEM_DESCRIPTOR);	
			}
			
		} catch (RepositoryException e) {
			BBBPerformanceMonitor.cancel(
					"CheckListManager", "showC1CategoryOnRlp");
			this.logError(
					BBBCoreErrorConstants.CHECKLIST_ERROR_10133
							+ " RepositoryException from populateStaticCheckListData of CheckListTools",
					e);
			throw new BBBSystemException(
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					e);
		
		}
		if(null != c1CheckListItem && null != c1CheckListItem.getPropertyValue(BBBCoreConstants.SHOW_ON_RLP) && null != c1CheckListItem.getPropertyValue(BBBCoreConstants.ALWAYS_SHOW_ON_RLP)){
			if((boolean)c1CheckListItem.getPropertyValue(BBBCoreConstants.ALWAYS_SHOW_ON_RLP)){
				this.logDebug("Always Show on RLP flag is true");
				BBBPerformanceMonitor.end(
						"CheckListManager", "fetchRlpFlagFromRepository()");
				return BBBCoreConstants.SHOW;
			}
			else if(!(boolean)c1CheckListItem.getPropertyValue(BBBCoreConstants.ALWAYS_SHOW_ON_RLP) && !(boolean)c1CheckListItem.getPropertyValue(BBBCoreConstants.SHOW_ON_RLP)){
				this.logDebug("Show on RLP flag is false and also always show on RLP flag is also false");
				BBBPerformanceMonitor.end(
						"CheckListManager", "fetchRlpFlagFromRepository()");
				return BBBCoreConstants.OTHER;
			}
			else{
				BBBPerformanceMonitor.end(
						CLASS_NAME, FETCH_RLP_FLAG);
				return fetchC1ShowFlagForCategoryFromRepository(registryId,skuItem,c1CheckListItem,totalSkuCatList);
			}
		}
		return BBBCoreConstants.HIDE;		
	}
	
	/**
	 * The method contains the logic to show or hide c1 category on RLP for the case when alwaysShowonRlp is false and showOnRlp is true. 
	 * This method is used in the case when data is present in cache.
	 * @param registryId
	 * @param skuItem
	 * @param c1CatVO
	 * @param totalSkuCatList
	 * @return String
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException
	 */
	public String fetchC1ShowFlagForCategoryFromCache(String registryId,RepositoryItem skuItem,List<String> totalSkuCatList,CategoryVO c1CatVO) throws BBBBusinessException, BBBSystemException{
		this.logDebug("Inside CheckListManager: fetchC1ShowFlagForCategoryFromCache()");
		RegistryItemsListVO registryItems = null;
		
		if(ServletUtil.getCurrentRequest() != null){
			logDebug("Fetching items from session bean");
			BBBSessionBean sessionBean = (BBBSessionBean) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.SESSION_BEAN);
			if(sessionBean.getValues().get(BBBGiftRegistryConstants.REGISTRY_ITEMS)!=null)
			registryItems = (RegistryItemsListVO) sessionBean.getValues().get(BBBGiftRegistryConstants.REGISTRY_ITEMS);
		}else{
			 registryItems = this.getGiftRegistryTools().fetchRegistryItemsFromEcomAdmin(registryId,BBBCoreConstants.RETURN_FALSE,true,BBBGiftRegistryConstants.DEFAULT_ALL_VIEW_FILTER);
		}
		if(null != registryItems && !BBBUtility.isListEmpty(registryItems.getRegistryItemList())){
		this.logDebug("Items are present in registry: " + registryId);
		for(RegistryItemVO regItemVO:registryItems.getRegistryItemList()){
			if(!BBBUtility.isEmpty(String.valueOf(regItemVO.getSku()))){
				skuItem = this.getCheckListTools().getSkuComputedAttributeItem(String.valueOf(regItemVO.getSku()));
			}
			List<String> catListForSku = this.getCheckListTools().getCategoryListForSku(skuItem);
			if(catListForSku != null){
				totalSkuCatList.addAll(catListForSku);
			}
		}
		List<CategoryVO> c2CategoryList = new ArrayList<CategoryVO>();
			if (c1CatVO != null && !BBBUtility.isListEmpty(c1CatVO.getChildCategoryVO())) {
				c2CategoryList = c1CatVO.getChildCategoryVO();
				ListIterator<CategoryVO> c2CategoryListIter = c2CategoryList.listIterator();
				CategoryVO c2CatvO = null;
				while (c2CategoryListIter.hasNext()) {
					List<CategoryVO> c3CategoryList = new ArrayList<CategoryVO>();
					c2CatvO = c2CategoryListIter.next();
					c3CategoryList = c2CatvO.getChildCategoryVO();
					if (null != c3CategoryList && 0 != c3CategoryList.size()) {
						// C2 has C3's
						ListIterator<CategoryVO> c3CategoryListIter = c3CategoryList.listIterator();
						while (c3CategoryListIter.hasNext()) {
							if (totalSkuCatList.contains(c3CategoryListIter.next().getCategoryId())) {
								this.logDebug("c3 item category belongs to registry item");
								return BBBCoreConstants.SHOW;
							}
						}
					} else {
						if (totalSkuCatList.contains(c2CatvO.getCategoryId())) {
							this.logDebug("c2 item category belongs to registry item");
							return BBBCoreConstants.SHOW;
						}
					}
				}
			}
	}
	else{
		this.logDebug("Items are not present in registry");
		return BBBCoreConstants.HIDE;
	}
	return BBBCoreConstants.HIDE;							
}
	/**
	 * The method contains the logic to show or hide c1 category on RLP for the case when alwaysShowonRlp is false and showOnRlp is true. 
	 * This method is used in the case when data is present in repository.
	 * @param registryId
	 * @param skuItem
	 * @param c1CheckListItem
	 * @param totalSkuCatList
	 * @return String
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException
	 */
	public String fetchC1ShowFlagForCategoryFromRepository(String registryId,RepositoryItem skuItem,RepositoryItem c1CheckListItem,List<String> totalSkuCatList) throws BBBBusinessException, BBBSystemException{
		this.logDebug("Inside CheckListManager: fetchC1ShowFlagForCategoryFromRepository()");
		RegistryItemsListVO registryItems = null;
		if(ServletUtil.getCurrentRequest() != null){
			logDebug("Fetching items from session bean");
			BBBSessionBean sessionBean = (BBBSessionBean) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.SESSION_BEAN);
			if(sessionBean.getValues().get(BBBGiftRegistryConstants.REGISTRY_ITEMS)!=null)
			registryItems = (RegistryItemsListVO) sessionBean.getValues().get(BBBGiftRegistryConstants.REGISTRY_ITEMS);
		}else{
			 registryItems = this.getGiftRegistryTools().fetchRegistryItemsFromEcomAdmin(registryId,BBBCoreConstants.RETURN_FALSE,true,BBBGiftRegistryConstants.DEFAULT_ALL_VIEW_FILTER);
		}
		if(null != registryItems && !BBBUtility.isListEmpty(registryItems.getRegistryItemList())){
		this.logDebug("Items are present in registry: " + registryId);
		for(RegistryItemVO regItemVO:registryItems.getRegistryItemList()){
			if(!BBBUtility.isEmpty(String.valueOf(regItemVO.getSku()))){
				skuItem = this.getCheckListTools().getSkuComputedAttributeItem(String.valueOf(regItemVO.getSku()));
			}
			List<String> catListForSku = this.getCheckListTools().getCategoryListForSku(skuItem);
			if(catListForSku!=null){
				totalSkuCatList.addAll(catListForSku);	
			}
			
		}
		@SuppressWarnings("unchecked")
		List<RepositoryItem> c2CheckListItems = (List<RepositoryItem>)c1CheckListItem.getPropertyValue(BBBCoreConstants.CHILD_CHECKLIST_CATEGORY_ID);
		if(!BBBUtility.isListEmpty(c2CheckListItems)){
			for(RepositoryItem c2CheckListItem : c2CheckListItems){
				MutableRepositoryItem checkListItem2;
				try {
					checkListItem2 = (MutableRepositoryItem) this.getCheckListRepository().getItem((String) c2CheckListItem.getRepositoryId(),BBBCoreConstants.CHECKLIST_CATEGORY_ITEM_DESCRIPTOR);
				} catch (RepositoryException e) {
					BBBPerformanceMonitor.cancel(
							"CheckListManager", "showC1CategoryOnRlp");
					this.logError(
							BBBCoreErrorConstants.CHECKLIST_ERROR_10133
									+ " RepositoryException from populateStaticCheckListData of CheckListTools",
							e);
					throw new BBBSystemException(
							BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
							BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
							e);
				
				}
				@SuppressWarnings("unchecked")
				List<RepositoryItem> c3CheckListItems = (List<RepositoryItem>)checkListItem2.getPropertyValue(BBBCoreConstants.CHILD_CHECKLIST_CATEGORY_ID);
				if(!BBBUtility.isListEmpty(c3CheckListItems)){
					for(RepositoryItem c3CheckListItem : c3CheckListItems){	
						if(totalSkuCatList.contains((String) c3CheckListItem.getRepositoryId())){
							this.logDebug("c3 item category belongs to registry item");
							return BBBCoreConstants.SHOW;
						}
					}
				}
				else{
					if(totalSkuCatList.contains((String) c2CheckListItem.getRepositoryId())){
						this.logDebug("c2 item category belongs to registry item");
						return BBBCoreConstants.SHOW;
					}
				}
			}						
		}	
	}
	else{
		this.logDebug("Items are not present in registry");
		return BBBCoreConstants.HIDE;
	}
	return BBBCoreConstants.HIDE;
}
	/**
	 * The method contains the logic to show or hide c1 category on RLP.
	 * @param registryId
	 * @param registryType
	 * @param c1CategoryId
	 * @return String
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException
	 */
	public String showC1CategoryOnRlp(String c1CategoryId,String registryId,String registryType) throws BBBSystemException, BBBBusinessException{		
		this.logDebug("CheckListManager.showC1CategoryOnRlp() method start");
		this.logDebug("Input parameters are: c1CategoryId: " + c1CategoryId + " ,registryId: " + registryId + " ,registryType: " + registryType);
			String siteId = SiteContextManager.getCurrentSiteId();
			String regTypeCode = null;
			if(registryType.length() <= 3){
				regTypeCode = registryType;
			}
			else{
			 regTypeCode = this.getGiftRegistryTools()
					.getRegistryTypeCode(registryType,siteId);
			}
			if(null != this.getCacheContainer() && null != this.getCacheContainer().get(regTypeCode)){
				return this.fetchRlpFlagFromCache(regTypeCode,c1CategoryId,registryId);				
		}
		else{
			return fetchRlpFlagFromRepository(c1CategoryId,registryId);
		}			
	}
	

	 /* this method is used to get the value whether checklist is
	 * disabled or disabled or not for registry.
	 * @param registryType
	 * @return isCheckListDisabled
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public boolean showCheckListButton(final String registryType) throws BBBSystemException, BBBBusinessException
	{
		this.logDebug("CheckListManager.isCheckListDisabled() method start");
		this.logDebug("CheckListManager.Input parameters are registryType: " + registryType);
		boolean isCheckListDisabled = true;
		String regTypeCode = null;
		String siteId = SiteContextManager.getCurrentSiteId(); 	
		String siteFlag = null;
		boolean isCheckListGlobalFlagEnabled = this.getCheckListTools().showCheckList();
		String siteIdValue=getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,SiteContextManager.getCurrentSiteId()).get(0);
		if(isCheckListGlobalFlagEnabled){
			if(!(registryType.length()<=3)){
				 regTypeCode = this.getGiftRegistryTools()
						.getRegistryTypeCode(registryType,siteId);
				} else {
					regTypeCode = registryType;
				}
			if(null != this.getCacheContainer() && null != this.getCacheContainer().get(regTypeCode)){
				CheckListVO	checkListVO = (CheckListVO)this.getCacheContainer().get(regTypeCode);
				//changes for ILD-293 | to set ischecklistDisabled flag on the basis of site
				if (null != checkListVO.getSiteFlag() && !checkListVO.getSiteFlag().contains(siteIdValue)) {
					isCheckListDisabled =true;
				} else {
					isCheckListDisabled = checkListVO.isCheckListDisabled();
				}
				
			}
			else{
				RepositoryItem[] checkListItems = this.getCheckListTools().fetchCheckListRepositoryItem(registryType);
				
				if(null != checkListItems && checkListItems.length >0 ){
					isCheckListDisabled = (boolean) checkListItems[0]
						.getPropertyValue(BBBCoreConstants.IS_DISABLED);
					//changes for ILD-293 | to set ischecklistDisabled flag on the basis of site
					siteFlag = (String)checkListItems[0]
							.getPropertyValue(BBBCoreConstants.SITE_FLAG);
				}
				if (null != siteFlag && !siteFlag.contains(siteIdValue)) {
					isCheckListDisabled =true;
				}
			}
		}
		this.logDebug("CheckListManager.isCheckListDisabled() method end");
		return isCheckListDisabled;
	}

	/**
	 * The method gets the updated checklist progress based on checking or unchecking a category manually.
	 * @param categoryIDForC1
	 * @param categoryIDForC2
	 * @param categoryIDForC3
	 * @param isChecklistSelected
	 * @param totalCheckedCategories
	 * @param registryType
	 * @param registryId
	 * @return CheckListProgressVO
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException
	 */
	public CheckListProgressVO getUpdatedProgressOnManualCheck(final boolean isChecklistSelected,final String categoryIDForC1 ,final String categoryIDForC2,final String categoryIDForC3,final String registryId,final String registryType,int totalC3QuantityAdded,final int totalC3QuanitySuggested,double averagePercentage,int noOfC1, int c3AddedQuantity){
		BBBPerformanceMonitor.start(
				"CheckListManager", "getUpdatedProgressOnManualCheck");
		this.logDebug("CheckListManager.getUpdatedProgressOnManualCheck() method start");
		this.logDebug("CheckListManager.getUpdatedProgressOnManualCheck() method Input params:isChecklistSelected: " + isChecklistSelected + " ,categoryIDForC1: " + categoryIDForC1 + " ,categoryIDForC2: " + categoryIDForC2 + " ,categoryIDForC3: " + categoryIDForC3 + " ,registryId: " + registryId + " ,registryType: " + registryType);
		boolean repositoryUpdateFlag = false;
		CheckListProgressVO checkListProgressVO = new CheckListProgressVO();
		try{
		if(isChecklistSelected){			
			if(!BBBUtility.isEmpty(categoryIDForC3)){				
				repositoryUpdateFlag = this.getCheckListTools().addCategoryToUserProfile(categoryIDForC3,registryId,registryType);
				if(repositoryUpdateFlag){	
					return this.getCheckListProgressVO(true,categoryIDForC3,registryType,checkListProgressVO,categoryIDForC1,categoryIDForC2,categoryIDForC3,totalC3QuantityAdded,totalC3QuanitySuggested,averagePercentage,noOfC1,c3AddedQuantity);					
				}
			}
			else if(!BBBUtility.isEmpty(categoryIDForC2)){
				repositoryUpdateFlag = this.getCheckListTools().addCategoryToUserProfile(categoryIDForC2,registryId,registryType);
				if(repositoryUpdateFlag){
					return this.getCheckListProgressVO(true,categoryIDForC2,registryType,checkListProgressVO,categoryIDForC1,categoryIDForC2,categoryIDForC3,totalC3QuantityAdded,totalC3QuanitySuggested,averagePercentage,noOfC1,c3AddedQuantity);					
				}
			}
		}
		else{
			if(!BBBUtility.isEmpty(categoryIDForC3)){
				repositoryUpdateFlag = this.getCheckListTools().removeCategoryFromUserProfile(categoryIDForC3,registryId,registryType);
				if(repositoryUpdateFlag){
					return this.getCheckListProgressVO(false,categoryIDForC3,registryType,checkListProgressVO,categoryIDForC1,categoryIDForC2,categoryIDForC3,totalC3QuantityAdded,totalC3QuanitySuggested,averagePercentage,noOfC1,c3AddedQuantity);					
				}
			}
			else if(!BBBUtility.isEmpty(categoryIDForC2)){
				repositoryUpdateFlag = this.getCheckListTools().removeCategoryFromUserProfile(categoryIDForC2,registryId,registryType);
				if(repositoryUpdateFlag){
					return this.getCheckListProgressVO(false,categoryIDForC2,registryType,checkListProgressVO,categoryIDForC1,categoryIDForC2,categoryIDForC3,totalC3QuantityAdded,totalC3QuanitySuggested,averagePercentage,noOfC1,c3AddedQuantity);					
				}
			}
		}
	}
		catch(BBBBusinessException ex){
			checkListProgressVO = new CheckListProgressVO();
			checkListProgressVO.setError(this.getLblTxtTemplateManager().getErrMsg("ERR_MANUAL_CHECK_CHECKLIST_MSG", "EN", null));
			
		}
		catch(BBBSystemException e){
			checkListProgressVO = new CheckListProgressVO();
			checkListProgressVO.setError(this.getLblTxtTemplateManager().getErrMsg("ERR_MANUAL_CHECK_CHECKLIST_MSG", "EN", null));
		}
		this.logDebug("CheckListManager.getUpdatedProgressOnManualCheck() method ends");
		BBBPerformanceMonitor.end(
				"CheckListManager", "getUpdatedProgressOnManualCheck");
		return checkListProgressVO;
	}
	
	/**
	 * The method fetches the added of suggested quantity of leaf category visted from checklist
	 * @param skuId
	 * @param currentAddedQuantity
	 * @param previousAddedQuantity
	 * @param addedQuantityShown
	 * @return String
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException
	 */
	public String getUpdatedAddedQuantity(String currentAddedQuantity,String previousAddedQuantity,String addedQuantityShown,String skuId) throws BBBSystemException, BBBBusinessException{
		RepositoryItem skuItem = null;
		BBBPerformanceMonitor.start(
				"CheckListManager", "getUpdatedAddedQuantity");
		boolean flag = false;
		String suggestedQuantity = null;
		this.logDebug("CheckListManager.getUpdatedAddedQuantity() method start:currentAddedQuantity: " + currentAddedQuantity + " previousAddedQuantity: " + previousAddedQuantity + " addedQuantityShown: " + addedQuantityShown + " skuId: " + skuId);
		if(addedQuantityShown.contains(BBBCoreConstants.OF)){
			flag = true;
			suggestedQuantity = addedQuantityShown.substring(addedQuantityShown.indexOf(BBBCoreConstants.OF) + 2).trim();
			addedQuantityShown =  addedQuantityShown.substring(0,addedQuantityShown.indexOf(BBBCoreConstants.OF)).trim();
			
		}
		skuItem = this.getCheckListTools().getSkuComputedAttributeItem(skuId);
		int packageCount = this.getCheckListTools().getPackageCountForSku(skuItem);
		BBBPerformanceMonitor.end(
				"CheckListManager", "getUpdatedAddedQuantity");
		int finalAddedQuantity = (Integer.parseInt(addedQuantityShown) + (Integer.parseInt(currentAddedQuantity) - Integer.parseInt(previousAddedQuantity))*packageCount);	
		if(flag == true){
			return String.valueOf(finalAddedQuantity) + BBBCoreConstants.SPACE + BBBCoreConstants.OF + BBBCoreConstants.SPACE + suggestedQuantity;
		}
		else{
			return String.valueOf(finalAddedQuantity);		
			}
		}
	
	/**
	 * The method gets the updated checklist progress based on checking or unchecking a category manually.
	 * @param boolean check
	 * @param categoryID
	 * @param registryType
	 * @param categoryIDForC1
	 * @param categoryIDForC2
	 * @param categoryIDForC3
	 * @param isChecklistSelected
	 * @param totalCheckedCategories
	 * @param registryType
	 * @param registryId
	 * @return CheckListProgressVO
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException
	 */
	public CheckListProgressVO getCheckListProgressVO(final boolean check,
			final String categoryID, final String registryType,
			final CheckListProgressVO checkListProgressVO,
			final String categoryIDForC1, final String categoryIDForC2,
			final String categoryIDForC3,
			int totalC3QuantityAdded, final int totalC3QuanitySuggested,
			double averagePercentage,int noOfC1,int c3AddedQuantity) throws BBBSystemException,
			BBBBusinessException {
		
		this.logDebug("CheckListManager.getCheckListProgressVO() method start");
		this.logDebug("CheckListManager.Input parameters are categoryIDForC2: " + categoryIDForC2 + " categoryID: " + categoryID + " registryType: " + registryType + " categoryIDForC1: " + categoryIDForC1 + " categoryIDForC3: " + categoryIDForC3 + " totalC3QuantityAdded: " + totalC3QuantityAdded + " totalC3QuanitySuggested: " + totalC3QuanitySuggested + " averagePercentage: " + averagePercentage + " noOfC1: " + noOfC1 + " c3AddedQuantity: " + c3AddedQuantity);
		MutableRepositoryItem checkListItem = this.getCheckListTools()
				.getSuggestedQuanityForCategoryItem(categoryID);
		int addedQuanity = 0;
		if (null != checkListItem) {
			if(null != checkListItem.getPropertyValue(BBBCoreConstants.SUGGESTED_QUANTITY)){
			addedQuanity = (int) checkListItem
					.getPropertyValue(BBBCoreConstants.SUGGESTED_QUANTITY);
			}
			else{
				addedQuanity = 0;
			}
		}
		double initialC1Percentage = this.getCheckListTools()
				.updateCheckListProgress(totalC3QuantityAdded,
						totalC3QuanitySuggested);
		if (check) {
			totalC3QuantityAdded = totalC3QuantityAdded + addedQuanity - c3AddedQuantity;
		} else {
			totalC3QuantityAdded = totalC3QuantityAdded - addedQuanity + c3AddedQuantity;
		}

		return this.getCheckListTools()
				.setCheckListProgressVO(registryType, checkListProgressVO,
						categoryIDForC1, categoryIDForC2, categoryIDForC3,
						 totalC3QuantityAdded,
						totalC3QuanitySuggested, averagePercentage,
						initialC1Percentage,noOfC1);
	}
	
	
	/**
	 * @return the checkListTools
	 */
	public CheckListTools getCheckListTools() {
		return checkListTools;
	}

	/**
	 * @param checkListTools the checkListTools to set
	 */
	public void setCheckListTools(CheckListTools checkListTools) {
		this.checkListTools = checkListTools;
	}

	public MutableRepository getCheckListRepository() {
		return checkListRepository;
	}

	public void setCheckListRepository(MutableRepository checkListRepository) {
		this.checkListRepository = checkListRepository;
	}

	public GiftRegistryTools getGiftRegistryTools() {
		return giftRegistryTools;
	}

	public void setGiftRegistryTools(GiftRegistryTools giftRegistryTools) {
		this.giftRegistryTools = giftRegistryTools;
	}

	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	public BBBLocalCacheContainer getCacheContainer() {
		return cacheContainer;
	}

	public void setCacheContainer(BBBLocalCacheContainer cacheContainer) {
		this.cacheContainer = cacheContainer;
	}
	
	/**
	 * The method is used to get the guide type based on guide id.
	 * @param guideId
	 * @return guidetype
	 */
	
	public String fetchGuideTypeFromId(String guideId) {
		return getCheckListTools().fetchGuideTypeFromId(guideId);
	}
	
	/**
	 * The method is used to set the guide cookie with all the details.
	 * @param guideType
	 * @param pRequest the request
	 * @param isHideGuideFlow 
	 * @return Cookie
	 */
	public Cookie createorUpdateRegistryGuideCookie(String guideType, DynamoHttpServletRequest pRequest, boolean isHideGuideFlow ){
		
		if (isLoggingDebug()) {
			logDebug("CheckListManager.createorUpdateRegistryGuideCookie method started for Guide Id = " + guideType);
		}
		StringBuffer cookieValue = null;
		Cookie originalCookie = null;
		final Cookie[] cookies = pRequest.getCookies();
//		String originalCookie = pRequest.getCookieParameter("registryGuides");
		String guideCookieName = null;
		//List<String> guideCookieValue = null;;	
			guideCookieName = BBBCoreConstants.INTERACTIVE_GUIDE_COOKIE_NAME;	
//		final String cookieName="registryGuides";
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				// Configure Cookie name
				if (cookie.getName().equals(guideCookieName)) {
					originalCookie = cookie;
					break;
				}
			}
		}
		
		if (isHideGuideFlow) {
			String origCookieValue = originalCookie.getValue();
			if (origCookieValue.contains(BBBCoreConstants.COLON)) {
				origCookieValue = origCookieValue.replaceAll(guideType + BBBCoreConstants.COLON, BBBCoreConstants.BLANK);
			} else {
				origCookieValue = origCookieValue.replaceAll(guideType, BBBCoreConstants.BLANK);
			} 
			cookieValue = new StringBuffer(origCookieValue);
		} else {
			if(originalCookie == null || BBBUtility.isEmpty(originalCookie.getValue())){
				cookieValue=new StringBuffer("");
				cookieValue.append(guideType);
			} else {
				if (originalCookie.getValue().contains(guideType)) {
					if (originalCookie.getValue().startsWith(guideType)) {
						cookieValue = new StringBuffer(originalCookie.getValue());
					} else {
						String origCookieValue = originalCookie.getValue();
						origCookieValue = origCookieValue.replace(BBBCoreConstants.COLON + guideType, BBBCoreConstants.BLANK);
						cookieValue = new StringBuffer(guideType).append(BBBCoreConstants.COLON).append(origCookieValue);
					}
				} else {
					cookieValue = new StringBuffer(guideType).append(BBBCoreConstants.COLON).append(originalCookie.getValue());
				}
			}
		}
		
		if (isLoggingDebug() && cookieValue != null) {
			logDebug(" Value to be set in the Registry Checklist cookie is " + cookieValue.toString());
		}
		
		final Cookie cookie = new Cookie(guideCookieName, cookieValue.toString());

		String path= pRequest.getParameter("path");		 
		String domain= pRequest.getParameter("domain");
		if(BBBUtility.isEmpty(domain)){
			if(pRequest.getServerName()!=null) {
				domain=pRequest.getServerName();
			} else {
				domain="";
			}
		}
		if(BBBUtility.isEmpty(path)){
			path="/";
		}
		
		List<String> guideCookieAgeValue=null;
		int cookieMaxAge = 0;

		try {
			guideCookieAgeValue = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.GUIDE_COOKIE_MAX_AGE);
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(null, "BBBSystemException from service of CheckListManager : cookieMaxAge"), e);
		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(null, "BBBBusinessException from service of CheckListManager : cookieMaxAge"), e);
		}

		if(!BBBUtility.isListEmpty(guideCookieAgeValue)){
			cookieMaxAge=Integer.valueOf(guideCookieAgeValue.get(0));
		}
		final StringBuffer debug=new StringBuffer(50);
		debug.append(" Cookie set with followinmg parameters cookie Name ").append(guideCookieName).append(" cookie Value ").append(cookieValue)
		.append(" max age of cookie ").append(cookieMaxAge).append(" domain " ).append(domain).append(" path ").append(path);
		logDebug(debug.toString());

		logDebug(" cookieMaxAge .. " + cookieMaxAge);
		cookie.setMaxAge(cookieMaxAge);
		cookie.setDomain(domain);
		cookie.setPath(path);
		return cookie;

	}


	@SuppressWarnings("unchecked")
	public void resetGuideContext(BBBSessionBean sessionBean) {
//		sessionBean.setChecklistVO(null);
//		sessionBean.getValues().put(BBBCoreConstants.SELECTED_GUIDE_TYPE, null);
		sessionBean.setActivateGuideInRegistryRibbon(false);
	}
	
	/**
	 * The method is used to remove duplicate guide vo.
	 * @param guideType
	 * @param pRequest the request
	 * @return Cookie
	 */

	public List<NonRegistryGuideVO> removeDuplicateNonRegVO(List<NonRegistryGuideVO> nonRegistryGuideVOs) {
		logDebug("CheckListManager.removeDuplicateNonRegVO method started. for nonRegistryGuideVOs = " + nonRegistryGuideVOs);
		List<NonRegistryGuideVO> filteredNonRegVOs = new ArrayList<NonRegistryGuideVO>();
 		Set<String> uniqueGuideTypes = new HashSet<String>();
 		if (!BBBUtility.isListEmpty(nonRegistryGuideVOs)) {
	 		for( NonRegistryGuideVO guideVO : nonRegistryGuideVOs ) {
	 			if(uniqueGuideTypes.add(guideVO.getGuideTypeCode())) {
	 				filteredNonRegVOs.add(guideVO);
	 			}
	 		}
 		}
 		logDebug("CheckListManager.removeDuplicateNonRegVO method ends. Returned nonRegistryGuideVOs = " + filteredNonRegVOs);
 		return filteredNonRegVOs;
		
	}
	
	/**
	 * The method is used to get the  duplicate guide vo.
	 * @param guideType
	 * @param pRequest the request
	 * @return Cookie
	 */
	public List<String> getStaticCheckLists() throws BBBSystemException, BBBBusinessException{
		logDebug("CheckListManager.getStaticCheckLists method started.");
		Map<String,String> registryMap = new HashMap<String,String>();
		List<String> staticCheckList = new ArrayList<String>();
		List<String> regTypeList = new ArrayList<String>();
		String registryTypeConfigKeys = null;
		registryMap.put(BBBCoreConstants.WEDDING_REG_CODE,BBBCoreConstants.WEDDING_REG_NAME);
		registryMap.put(BBBCoreConstants.COMMITMENT_REG_CODE,BBBCoreConstants.COMMITMENT_REG_NAME);
		registryMap.put(BBBCoreConstants.ANNIVERSARY_REG_CODE,BBBCoreConstants.ANNIVERSARY_REG_NAME);
		registryMap.put(BBBCoreConstants.HOUSEWARMING_REG_CODE,BBBCoreConstants.HOUSEWARMING_REG_NAME);
		registryMap.put(BBBCoreConstants.COLLEGE_REG_CODE,BBBCoreConstants.COLLEGE_REG_NAME);
		registryMap.put(BBBCoreConstants.BIRTHDAY_REG_CODE,BBBCoreConstants.BIRTHDAY_REG_NAME);
		registryMap.put(BBBCoreConstants.RETIREMENT_REG_CODE,BBBCoreConstants.RETIREMENT_REG_NAME);
		registryMap.put(BBBCoreConstants.BABY_REG_CODE,BBBCoreConstants.BABY_REG_NAME);
		registryMap.put(BBBCoreConstants.OTHER_REG_CODE,BBBCoreConstants.OTHER_REG_NAME);
		registryMap.put(BBBCoreConstants.BABYBIRTHDAY_REG_CODE,BBBCoreConstants.BIRTHDAY_REG_NAME);
		registryMap.put(BBBCoreConstants.BABYOTHER_REG_CODE,BBBCoreConstants.OTHER_REG_NAME);
		registryTypeConfigKeys = this.getCatalogTools().getConfigKeyValue(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.CHECKLIST_REGISTRY_TYPES, BBBCoreConstants.BLANK);
		if (BBBUtility.isNotEmpty(registryTypeConfigKeys)) {
			regTypeList = Arrays.asList(registryTypeConfigKeys.split(BBBCoreConstants.COMMA));
		}
		for(String regCode:regTypeList){
		if(!StringUtils.isEmpty(registryMap.get(regCode))){
			List<String> showCheckList=null;
			String isCheckListEnabled = null;
			showCheckList = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS,registryMap.get(regCode) + BBBCoreConstants.CHECKLIST_PROGRESS);			
				if(!BBBUtility.isListEmpty(showCheckList)){
					isCheckListEnabled=showCheckList.get(0).toString();
				}
				if(!StringUtils.isEmpty(isCheckListEnabled) && (isCheckListEnabled.equalsIgnoreCase(BBBCoreConstants.OFF) || isCheckListEnabled.equalsIgnoreCase(BBBCoreConstants.NOT_APPLICABLE))){
					logDebug("Config key is off or notApplicable for regCode:" + regCode);
					staticCheckList.add(regCode);
					if(regCode.equalsIgnoreCase(BBBCoreConstants.COMMITMENT_REG_CODE)){
						staticCheckList.add(BBBCoreConstants.COMMITMENT_CEREMONY_REG_TYPE);
					}
					else if(regCode.equalsIgnoreCase(BBBCoreConstants.COLLEGE_REG_CODE)){
						staticCheckList.add(BBBCoreConstants.COLLEGE_REG_TYPE);
					}
					else{
						staticCheckList.add(registryMap.get(regCode));
					}
				}
		}
		else{
			logDebug("Adding to static codes: " + regCode);
			staticCheckList.add(regCode);
		}
	}
	return staticCheckList;
	}
	
	/**
	 * The method is used to retrieve the First leaf category
	 * url for any guide type.
	 * @param guideType
	 * @return Map
	 */

	public HashMap<String, String> getFirstLeafCategoryURL(String guideType) throws BBBSystemException, BBBBusinessException {
		
		logDebug("CheckListManager.getFirstLeafCategoryURL method started.  Guide Type = " + guideType);
		//String firstLeafCatURL = null;
		HashMap<String, String> firstLeafCatURL = new HashMap<String, String>();
		CheckListVO checkListVO = (CheckListVO)this.getCacheContainer().get(guideType);
		if (checkListVO == null) {
			RepositoryItem[] guideItems = getCheckListTools().fetchCheckListRepositoryItem(guideType);
			if (!BBBUtility.isArrayEmpty(guideItems)) {
				firstLeafCatURL = getCheckListTools().getFirstLeafCategoryURL(guideItems[0]);
			}
			
		} else {
			firstLeafCatURL = getCheckListTools().getFirstLeafCategoryURL(checkListVO);
		}
		logDebug("CheckListManager.getFirstLeafCategoryURL method ends. Returned url = " + firstLeafCatURL);
		return firstLeafCatURL;
	}
	
	public List<NonRegistryGuideVO> getNonRegStaticVo(String subTypeCodes) throws BBBSystemException, BBBBusinessException{
		List<NonRegistryGuideVO> nonRegVOList = new ArrayList<NonRegistryGuideVO>();
		String[] subTypeCodesArray = subTypeCodes.split(BBBCoreConstants.COLON);
		NonRegistryGuideVO	nonRegistryGuideVO = null;
		if(!BBBUtility.isEmpty(subTypeCodesArray)){
		for(String codeValue: subTypeCodesArray){
			nonRegistryGuideVO = getNonRegistryGuide(codeValue, BBBCoreConstants.GUIDE);
			// changes for ILD-227 | if guide is disabled, do not add guide vo into session.
			if(null != nonRegistryGuideVO && !nonRegistryGuideVO.isGuideDisabled()){
				nonRegVOList.add(nonRegistryGuideVO);
			}
		}
		}
		return nonRegVOList;
	}

	/** Returns the list of eph category based on the event code
	 * @param registryEventCode
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	@SuppressWarnings("unchecked")
	public LinkedHashMap<String,String> getEPHCategoryBasedOnRegistryType(String registryEventCode) throws BBBSystemException, BBBBusinessException{
		logDebug("Entering getEPHCategoryBasedOnRegistryType with  registryEventCode :" +registryEventCode);
		LinkedHashMap<String,String> ephCategoriesMap=null;
		if(BBBUtility.isEmpty(registryEventCode)){
			return ephCategoriesMap;
		}
		RepositoryItem[] checkListItems = this.getCheckListTools().fetchCheckListRepositoryItem(registryEventCode);
		if(checkListItems!=null){
			ephCategoriesMap=new LinkedHashMap<String,String>();
			List<RepositoryItem> c1EphCategory = (List<RepositoryItem>)checkListItems[0].getPropertyValue(BBBCoreConstants.CHECK_LIST_CATEGORIES);
			if(c1EphCategory !=null){
				for(RepositoryItem ephCategoryVar : c1EphCategory){
					// If the category has flags isShowOnRLP  true and isAlwaysShowOnRLP as true than include in category list else add OTHER in category list.
					if(ephCategoryVar !=null && BBBCoreConstants.SHOW.equalsIgnoreCase(getCheckListTools().includeCategoryOnRLP(ephCategoryVar))){
						if(ephCategoryVar.getPropertyValue(BBBCoreConstants.DISPLAY_NAME) !=null){
						ephCategoriesMap.put(ephCategoryVar.getRepositoryId(),(String) ephCategoryVar.getPropertyValue(BBBCoreConstants.DISPLAY_NAME));
					}else if(ephCategoriesMap.get(BBBCoreConstants.OTHER) !=null && BBBCoreConstants.OTHER.equalsIgnoreCase(getCheckListTools().includeCategoryOnRLP(ephCategoryVar))){
						ephCategoriesMap.put(BBBCoreConstants.OTHER,BBBCoreConstants.OTHER);
					}
				}
				}
			}
		}
		return ephCategoriesMap;
		
	}
	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}


}
