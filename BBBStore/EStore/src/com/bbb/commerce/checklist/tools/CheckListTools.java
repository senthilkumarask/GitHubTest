package com.bbb.commerce.checklist.tools;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.commons.lang.StringUtils;

import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.checklist.vo.CategoryVO;
import com.bbb.commerce.checklist.vo.CheckListPrevNextCategoriesVO;
import com.bbb.commerce.checklist.vo.CheckListProgressVO;
import com.bbb.commerce.checklist.vo.CheckListVO;
import com.bbb.commerce.checklist.vo.ChecklistSKUMapping;
import com.bbb.commerce.checklist.vo.MyItemCategoryVO;
import com.bbb.commerce.checklist.vo.MyItemVO;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools;
import com.bbb.commerce.giftregistry.vo.RegistryItemVO;
import com.bbb.commerce.giftregistry.vo.RegistryItemsListVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
/**
 * 
 * This class provides the low level functionality for gift registry
 * creation/manipulation.
 * 
 * @author sku134
 * 
 */
public class CheckListTools extends BBBGenericService {
	private BBBCatalogTools mCatalogTools;
	private String queryCheckListForRegistryType;
	private MutableRepository checkListRepository;
	private GiftRegistryTools giftRegistryTools;
	private String queryManuallySelectedC3;
	private MutableRepository profileRepository;
	private String querySkuPackageCount;
	private String queryC1Categories;
	private MutableRepository catalogRepository;
	private TransactionManager transactionManager;
	private boolean stagingServer;
	private String[] entities;
	private String urlNonAcceptableRegexPattern;
	private String porchCheckListQuery;

	/**
	 * The method checks if checklist needs to be shown for a given registry type based on a config key.
	 * If the config key is false, static checklist will be shown. 
	 * @param id
	 * @param itemDescriptor
	 * @return
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException
	 */
	public boolean showCheckList() throws BBBSystemException, BBBBusinessException{
		this.logDebug("CheckListTools.showCheckList() method start");
		List<String> showCheckList=null;
		Boolean isCheckListEnabled=false;		
			showCheckList = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,
					BBBCoreConstants.INTERACTIVE_CHECKLIST_KEY);
			if(!BBBUtility.isListEmpty(showCheckList)){
				isCheckListEnabled=Boolean.valueOf(showCheckList.get(0));
				logDebug(" is CheckList feature on ? "+isCheckListEnabled);
				return isCheckListEnabled;
			}			
		this.logDebug("CheckListTools.showCheckList() method ends");		
		return isCheckListEnabled;
	}
	
	public String checkRegistryTypeFromSubTypeCode(String checkListType) throws BBBSystemException, BBBBusinessException{
	final Object[] params = new Object[1];
	params[0] = checkListType;
	String type = null;
		RepositoryItem[] checkListRegistryRepoItem = null;
	checkListRegistryRepoItem = this.executeRQLQuery(
			this.getQueryCheckListForRegistryType(), params, BBBCoreConstants.CHECK_LIST_TYPE ,
			this.getCheckListRepository());
	if(null != checkListRegistryRepoItem){
		type = (String)checkListRegistryRepoItem[0].getPropertyValue(BBBCoreConstants.TYPE_NAME);
	}
	return type;
	}
	/**
	 * The method checks if config key CehcklistCacheEnabled is true or not.
	 * If the config key is true, data is fetched from cache else data is fetched from repository. 
	 * @param id
	 * @param itemDescriptor
	 * @return
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException
	 */
	public boolean isCheckListCacheEnabled() throws BBBSystemException, BBBBusinessException{
		this.logDebug("CheckListTools.isCheckListCacheEnabled() method start");
		List<String> showCheckList=null;
		Boolean isCheckListEnabled=false;		
			showCheckList = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,
					BBBCoreConstants.CHECKLIST_CACHE_ENABLED);
			if(null!=showCheckList && !showCheckList.isEmpty()){
				isCheckListEnabled=Boolean.valueOf(showCheckList.get(0));
				logDebug(" Data to be fetched from cache "+isCheckListEnabled);
				return isCheckListEnabled;
			}			
		this.logDebug("CheckListTools.isCheckListCacheEnabled() method ends");		
		return isCheckListEnabled;
	}
	
	/**
	 * The method populates the CheckListVO from cache.
	 * @param registryId
	 * @param checkListVO
	 * @return CheckListVO
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException
	 */
	public CheckListVO populateDynamicAttributesInCachedVO(final String registryId,CheckListVO checkListVO) throws BBBBusinessException, BBBSystemException{
		BBBPerformanceMonitor.start(
				"CheckListTools", "populateDynamicAttributesInCachedVO");
		this.logDebug("CheckListTools.populateDynamicAttributesInCachedVO() method start");
		this.logDebug("CheckListTools.populateDynamicAttributesInCachedVO() Input parameters are: registryId: " + registryId + " checkListVO: " +checkListVO.getChecklistId());
		long startTime = System.currentTimeMillis();
		CheckListVO checkVO = new CheckListVO();
		double overallAveragePercentage = 0.0;
		RegistryItemsListVO registryItems = this.getGiftRegistryTools().fetchChecklistRegItemsFromEcomAdmin(registryId);
		
	List<ChecklistSKUMapping> checklistSKUList=new ArrayList<ChecklistSKUMapping>();
		
		for(RegistryItemVO regItem:registryItems.getRegistryItemList()){
			ChecklistSKUMapping checklistSKU=new ChecklistSKUMapping();
			checklistSKU.setSkuId(String.valueOf(regItem.getSku()));
			checklistSKU.setQuantityRequested(regItem.getQtyRequested());
			RepositoryItem skuItem =null;
			try {
				skuItem= this.getCatalogRepository().getItem(checklistSKU.getSkuId(),BBBCoreConstants.SKU);
			} catch (RepositoryException e) {
				logError("RepositoryException while fetching items",e);
			}
			if(skuItem !=null){
			
				if(skuItem.getPropertyValue(BBBCoreConstants.CATEGORIES_LIST) !=null){
					 String	categoryListForSku = (String)skuItem.getPropertyValue(BBBCoreConstants.CATEGORIES_LIST);
					 if(!BBBUtility.isEmpty(categoryListForSku)){
					     checklistSKU.setListCatId(Arrays.asList(categoryListForSku.split(BBBCoreConstants.COMMA))); 
					 }
				}	
				if(skuItem.getPropertyValue(BBBCoreConstants.PACKAGE_COUNT)!=null){
					int packageCount = (int)skuItem.getPropertyValue(BBBCoreConstants.PACKAGE_COUNT);
					if(packageCount == 0){
						packageCount = 1;
					} 
					checklistSKU.setPackageCount(packageCount);
				}
				checklistSKUList.add(checklistSKU);
			}
			
		}

		final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		final Profile bbbProfile = (Profile)request.resolveName("/atg/userprofiling/Profile");							
		 List<String> manuallySelectedCategoryList = manuallySelectedCategories(bbbProfile.getRepositoryId(),registryId,checkListVO.getChecklistId());	
		
		List<CategoryVO> modifiedC1CategoryList =  new ArrayList<CategoryVO>();
		List<CategoryVO> modifiedC2CategoryList =null;
		List<CategoryVO> modifiedC3CategoryList = null;	
		DecimalFormat df = new DecimalFormat(BBBCoreConstants.DECIMAL_PLACES);  
		
		for(CategoryVO c1CatVO : checkListVO.getCategoryListVO()){
			
			if(c1CatVO.isShowCheckList() && !c1CatVO.isDisabled() && !c1CatVO.isDeleted()){
				this.logDebug("CheckListTools.populateDynamicAttributesInCachedVO() method C1 IS_DISABLED is false,IS_DELETED is false and SHOW_ON_CHECKLIST are on: " + c1CatVO.getCategoryId());
				if(!c1CatVO.isConfigureComplete() && isStagingServer() || c1CatVO.isConfigureComplete()){
					this.logDebug("CheckListTools.populateDynamicAttributesInCachedVO() method C1 IS_CONFIGURE_COMPLETE is on: " + c1CatVO.getCategoryId());
			 modifiedC2CategoryList = new ArrayList<CategoryVO>();
			CategoryVO modifiedC1CatVO = new CategoryVO();
			modifiedC1CatVO = populateVO(modifiedC1CatVO,c1CatVO);
			List<CategoryVO> c2CategoryList = new ArrayList<CategoryVO>();
			c2CategoryList = modifiedC1CatVO.getChildCategoryVO();
			int completedC2 = 0;
			ListIterator<CategoryVO> c2CategoryListIter = c2CategoryList.listIterator();
			while(c2CategoryListIter.hasNext()){
				modifiedC3CategoryList = new ArrayList<CategoryVO>();
				CategoryVO c2CatVO = new CategoryVO();
				c2CatVO = populateVO(c2CatVO,c2CategoryListIter.next());
				if(!c2CatVO.isDisabled() && !c2CatVO.isDeleted()){
					this.logDebug("CheckListTools.populateDynamicAttributesInCachedVO() method C2 IS_DISABLED off,IS_DELETED is off: "+ c2CatVO.getCategoryId());
					if(!c2CatVO.isConfigureComplete() && isStagingServer() || c2CatVO.isConfigureComplete()){
						this.logDebug("CheckListTools.populateDynamicAttributesInCachedVO() method C2 IS_CONFIGURE_COMPLETE is on: " + c2CatVO.getCategoryId());
				List<CategoryVO> c3CategoryList = c2CatVO.getChildCategoryVO();
				if(null != c3CategoryList && 0 != c3CategoryList.size()){
					//C2 has C3's
					int size = 0;
					ListIterator<CategoryVO> c3CategoryListIter = c3CategoryList.listIterator();
					while (c3CategoryListIter.hasNext()) {
						CategoryVO c3CatVO = new CategoryVO();
						c3CatVO = populateVO(c3CatVO,c3CategoryListIter.next());
						if(!c3CatVO.isDisabled() && !c3CatVO.isDeleted()){
							this.logDebug("CheckListTools.populateDynamicAttributesInCachedVO() method C3 IS_DISABLED off, IS_DELETED is off: " + c3CatVO.getCategoryId());
							if(!c3CatVO.isConfigureComplete() && isStagingServer() || c3CatVO.isConfigureComplete()){
								this.logDebug("CheckListTools.populateDynamicAttributesInCachedVO() method C3 IS_CONFIGURE_COMPLETE is on: " + c3CatVO.getCategoryId());
							size = size + 1;
						c3CatVO = this.updateCheckListCompletionStatus(checklistSKUList,c3CatVO,manuallySelectedCategoryList);
						modifiedC3CategoryList.add(c3CatVO);
						if(c3CatVO.isC3ManualComplete() || c3CatVO.isC3AutoComplete()){
							c2CatVO.setC2CompleteCount(c2CatVO.getC2CompleteCount() + 1);							
						}
						if((c3CatVO.getAddedQuantity() <= c3CatVO.getSuggestedQuantity() && c3CatVO.isC3ManualComplete()) || c3CatVO.getAddedQuantity() > c3CatVO.getSuggestedQuantity()){
							modifiedC1CatVO.setTotalAddedQuantityForPercentageCalc(modifiedC1CatVO.getTotalAddedQuantityForPercentageCalc() + c3CatVO.getSuggestedQuantity());
						}
						else if(c3CatVO.getAddedQuantity() != 0){
							modifiedC1CatVO.setTotalAddedQuantityForPercentageCalc(modifiedC1CatVO.getTotalAddedQuantityForPercentageCalc() + c3CatVO.getAddedQuantity());
						}
						modifiedC1CatVO.setTotalSuggestedQuantityForPercentageCalc(modifiedC1CatVO.getTotalSuggestedQuantityForPercentageCalc() + c3CatVO.getSuggestedQuantity());					
						}
					}
					}
					c2CatVO.setNumberOfC3(size);
					//Setting C3 category map in C2 CategoryVO.
					c2CatVO.setChildCategoryVO(modifiedC3CategoryList);		
				}
				else{
					//C2 is a leaf node
					c2CatVO = this.updateCheckListCompletionStatus(checklistSKUList,c2CatVO,manuallySelectedCategoryList);
					c2CatVO.setChildCategoryVO(null);
					if(c2CatVO.isC3ManualComplete() || c2CatVO.isC3AutoComplete()){
						c2CatVO.setC2CompleteCount(1);						
						modifiedC1CatVO.setC2CompleteCount(++completedC2);
					}
					if((c2CatVO.getAddedQuantity() <= c2CatVO.getSuggestedQuantity() && c2CatVO.isC3ManualComplete()) || c2CatVO.getAddedQuantity() > c2CatVO.getSuggestedQuantity()){
						modifiedC1CatVO.setTotalAddedQuantityForPercentageCalc(modifiedC1CatVO.getTotalAddedQuantityForPercentageCalc() + c2CatVO.getSuggestedQuantity());
					}
					else if(c2CatVO.getAddedQuantity() != 0){
						modifiedC1CatVO.setTotalAddedQuantityForPercentageCalc(modifiedC1CatVO.getTotalAddedQuantityForPercentageCalc() + c2CatVO.getAddedQuantity());
					}
					c2CatVO.setNumberOfC3(1);											
					modifiedC1CatVO.setTotalSuggestedQuantityForPercentageCalc(modifiedC1CatVO.getTotalSuggestedQuantityForPercentageCalc() + c2CatVO.getSuggestedQuantity());											
				}				
				modifiedC2CategoryList.add(c2CatVO);		
				}
			}
			}
		   
			modifiedC1CatVO.setChildCategoryVO(modifiedC2CategoryList);
			//Calculating C1 category percentage here.
			modifiedC1CatVO.setC1PercentageComplete(Double.valueOf(df.format(this.updateCheckListProgress(modifiedC1CatVO.getTotalAddedQuantityForPercentageCalc(),modifiedC1CatVO.getTotalSuggestedQuantityForPercentageCalc()))));
			//Image url is being set based on percentage. If percentage is 100%, completed image url is set at C1 level, else incomplete image url is set.
			modifiedC1CatVO.setImageURL(fetchImageUrlBasedOnPercentage(modifiedC1CatVO.getC1PercentageComplete(),modifiedC1CatVO,null));			
			//Setting label message based on C1 percentage.			
			
			modifiedC1CatVO.setLabelMessage(this.getLabelMessageForC1Percentage(modifiedC1CatVO.getC1PercentageComplete()));

			//Average percentage of all C1's to be used only for mobile.
			overallAveragePercentage = overallAveragePercentage + modifiedC1CatVO.getC1PercentageComplete();
			//Adding C1 CategoryVO in CategoryVO list.
			modifiedC1CategoryList.add(modifiedC1CatVO);
			//Average percentage of all C1's to be used only for mobile.
		}
		}
	}
		checkVO.setCategoryListVO(modifiedC1CategoryList);
		if(modifiedC1CategoryList.size() != 0){
			checkVO.setAverageC1Percentage(Double.valueOf(df.format((double)overallAveragePercentage/(double)modifiedC1CategoryList.size())));
		}else{
			checkVO.setAverageC1Percentage(0);
		}
		long endTime = System.currentTimeMillis();
		this.logDebug("total time taken in populating dynamic CheckListVO() : " + (endTime-startTime));
		BBBPerformanceMonitor.end(
				"CheckListTools", "populateDynamicAttributesInCachedVO");
		return checkVO;
	}
	
	/**
	 * The method populates the properties in CategoryVO from cache.
	 * @param CategoryVO
	 * @param CategoryVO
	 * @return CategoryVO
	 */
	private CategoryVO populateVO(CategoryVO modifiedC1CatVO,
			CategoryVO c1CatVO) {
		BBBPerformanceMonitor.start(
				"CheckListTools", "populateVO");
		this.logDebug("CheckListTools.populateVO() Input parameter: c1CatVO: " + c1CatVO.getCategoryId());
		String siteId = SiteContextManager.getCurrentSiteId();
		
		if(null != siteId && !StringUtils.isEmpty(siteId)){
		if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
			//modifiedC1CatVO.setCategoryURL(c1CatVO.getCacategoryURL());
			modifiedC1CatVO.setCaOverriddenURL(c1CatVO.isCaOverriddenURL());
			modifiedC1CatVO.setCaImageURL(c1CatVO.getCaImageURL());
			modifiedC1CatVO.setCacategoryURL(c1CatVO.getCacategoryURL());
			}
		else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)){
			//modifiedC1CatVO.setCategoryURL(c1CatVO.getUscategoryURL());
			modifiedC1CatVO.setUsOverriddenURL(c1CatVO.isUsOverriddenURL());
			modifiedC1CatVO.setImageURL(c1CatVO.getImageURL());
			modifiedC1CatVO.setUscategoryURL(c1CatVO.getUscategoryURL());
		}
		else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)){
			//modifiedC1CatVO.setCategoryURL(c1CatVO.getBabycategoryURL());
			modifiedC1CatVO.setBabyOverriddenURL(c1CatVO.isBabyOverriddenURL());
			modifiedC1CatVO.setBabyImageURL(c1CatVO.getBabyImageURL());
			modifiedC1CatVO.setBabycategoryURL(c1CatVO.getBabycategoryURL());
		}
		}
		modifiedC1CatVO.setAddedQuantity(c1CatVO.getAddedQuantity());
		modifiedC1CatVO.setC1PercentageComplete(c1CatVO.getC1PercentageComplete());
		modifiedC1CatVO.setC2CompleteCount(c1CatVO.getC2CompleteCount());		
		modifiedC1CatVO.setC3AutoComplete(c1CatVO.isC3AutoComplete());
		modifiedC1CatVO.setC3ManualComplete(c1CatVO.isC3ManualComplete());
		modifiedC1CatVO.setCategoryId(c1CatVO.getCategoryId());
		modifiedC1CatVO.setCategoryName(c1CatVO.getCategoryName());
		modifiedC1CatVO.setChildCategoryVO(c1CatVO.getChildCategoryVO());
		modifiedC1CatVO.setDisplayName(c1CatVO.getDisplayName());
		modifiedC1CatVO.setLabelMessage(c1CatVO.getLabelMessage());
		modifiedC1CatVO.setNumberOfC3(c1CatVO.getNumberOfC3());
		modifiedC1CatVO.setPrimaryParentCategoryId(c1CatVO.getPrimaryParentCategoryId());
		/*modifiedC1CatVO.setSequenceNumber(c1CatVO.getSequenceNumber());*/
		modifiedC1CatVO.setSuggestedQuantity(c1CatVO.getSuggestedQuantity());
		modifiedC1CatVO.setTotalAddedQuantityForPercentageCalc(c1CatVO.getTotalAddedQuantityForPercentageCalc());
		modifiedC1CatVO.setTotalSuggestedQuantityForPercentageCalc(c1CatVO.getTotalSuggestedQuantityForPercentageCalc());	
		modifiedC1CatVO.setDisabled(c1CatVO.isDisabled());	
		modifiedC1CatVO.setDeleted(c1CatVO.isDeleted());
		modifiedC1CatVO.setConfigureComplete(c1CatVO.isConfigureComplete());
		BBBPerformanceMonitor.end(
				"CheckListTools", "populateVO");
		return modifiedC1CatVO;
	}
	
	/**
	 * The method populates the CheckListVO for the given registry Type and registry id.
	 * @param registryId
	 * @param registryType
	 * @param checkListItems the RepositoryItem[]
	 * @return CheckListVO
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException
	 */
	public CheckListVO populateCheckListVO(final String registryId,final RepositoryItem[] checkListItems) throws BBBSystemException, BBBBusinessException{
		BBBPerformanceMonitor.start(
				"CheckListTools", "populateCheckListVO");
		this.logDebug("CheckListTools.populateStaticCheckListData() method start: registryType: " + registryId);
		long startTime = System.currentTimeMillis();
		CheckListVO checkListVO = new CheckListVO();
		double overallAveragePercentage = 0.0;
		
		
		DecimalFormat df = new DecimalFormat(BBBCoreConstants.DECIMAL_PLACES);   
		RegistryItemsListVO registryItems = this.getGiftRegistryTools().fetchChecklistRegItemsFromEcomAdmin(registryId);
		
		List<ChecklistSKUMapping> checklistSKUList=new ArrayList<ChecklistSKUMapping>();
		
		for(RegistryItemVO regItem:registryItems.getRegistryItemList()){
			ChecklistSKUMapping checklistSKU=new ChecklistSKUMapping();
			checklistSKU.setSkuId(String.valueOf(regItem.getSku()));
			checklistSKU.setQuantityRequested(regItem.getQtyRequested());
			RepositoryItem skuItem =null;
			try {
				skuItem= this.getCatalogRepository().getItem(checklistSKU.getSkuId(),BBBCoreConstants.SKU);
			} catch (RepositoryException e) {
				logError("RepositoryException while fetching items",e);
			}
			if(skuItem !=null){
			
				if(skuItem.getPropertyValue(BBBCoreConstants.CATEGORIES_LIST) !=null){
					 String	categoryListForSku = (String)skuItem.getPropertyValue(BBBCoreConstants.CATEGORIES_LIST);
					 if(!BBBUtility.isEmpty(categoryListForSku)){
					     checklistSKU.setListCatId(Arrays.asList(categoryListForSku.split(BBBCoreConstants.COMMA))); 
					 }
				}	
				if(skuItem.getPropertyValue(BBBCoreConstants.PACKAGE_COUNT)!=null){
					int packageCount = (int)skuItem.getPropertyValue(BBBCoreConstants.PACKAGE_COUNT);
					if(packageCount == 0){
						packageCount = 1;
					} 
					checklistSKU.setPackageCount(packageCount);
				}
				checklistSKUList.add(checklistSKU);
			}
			
		}

		final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		final Profile bbbProfile = (Profile)request.resolveName("/atg/userprofiling/Profile");							
		 List<String> manuallySelectedCategoryList = manuallySelectedCategories(bbbProfile.getRepositoryId(),registryId,checkListVO.getChecklistId());	
		 
		if(null != checkListItems && !(checkListItems.length == 0)){
			if(null != checkListItems[0]){
			checkListVO.setChecklistId((String) checkListItems[0].getRepositoryId());
			checkListVO.setChecklistTypeName((String) checkListItems[0].getPropertyValue(BBBCoreConstants.SUBTYPE_CODE));
			checkListVO.setDisplayName((String) checkListItems[0].getPropertyValue(BBBCoreConstants.DISPLAY_NAME));
			checkListVO.setSiteFlag((String) checkListItems[0].getPropertyValue(BBBCoreConstants.SITE_FLAG));
			List<CategoryVO> c1CategoryList = new ArrayList<CategoryVO>();
			if(null != checkListItems[0].getPropertyValue(BBBCoreConstants.CHECK_LIST_CATEGORIES)){
				@SuppressWarnings("unchecked")
				List<RepositoryItem> checkListCategories = (List<RepositoryItem>)checkListItems[0].getPropertyValue(BBBCoreConstants.CHECK_LIST_CATEGORIES);
				if(!BBBUtility.isListEmpty(checkListCategories)){							
					for(RepositoryItem checkListItem : checkListCategories){
						//Populating C1's
						try {
							MutableRepositoryItem c1CheckListItem = (MutableRepositoryItem) getCheckListRepository().getItem((String) checkListItem.getRepositoryId(),BBBCoreConstants.CHECKLIST_CATEGORY_ITEM_DESCRIPTOR);
							if(null != c1CheckListItem && null != c1CheckListItem.getPropertyValue(BBBCoreConstants.SHOW_ON_CHECKLIST) && (boolean)c1CheckListItem.getPropertyValue(BBBCoreConstants.SHOW_ON_CHECKLIST) && null != c1CheckListItem.getPropertyValue(BBBCoreConstants.IS_DISABLED) && !((boolean)c1CheckListItem.getPropertyValue(BBBCoreConstants.IS_DISABLED)) && null != c1CheckListItem.getPropertyValue(BBBCoreConstants.IS_DELETED) && !((boolean)c1CheckListItem.getPropertyValue(BBBCoreConstants.IS_DELETED))){
								this.logDebug("CheckListTools.populateStaticCheckListData() method C1 IS_DISABLED is false, IS_DELETED is also false and SHOW_ON_CHECKLIST are on: " + (String) checkListItem.getRepositoryId());
								if(null != c1CheckListItem.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE) && (!(boolean)c1CheckListItem.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE) && isStagingServer() || (boolean)c1CheckListItem.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE))){
									this.logDebug("CheckListTools.populateStaticCheckListData() method C1 IS_CONFIGURE_COMPLETE is on: " + (String) checkListItem.getRepositoryId());
									CategoryVO c1CategoryVO = populateCategoryVO(c1CheckListItem,null,null,checkListVO);
									int completedC2=0;
								//Populating C2's
								List<CategoryVO> c2CategoryList = new ArrayList<CategoryVO>();
								@SuppressWarnings("unchecked")
								List<RepositoryItem> c2CheckListItems = (List<RepositoryItem>)c1CheckListItem.getPropertyValue(BBBCoreConstants.CHILD_CHECKLIST_CATEGORY_ID);
								if(!BBBUtility.isListEmpty(c2CheckListItems)){
									for(RepositoryItem c2CheckListItem : c2CheckListItems){
										MutableRepositoryItem checkListItem2 = (MutableRepositoryItem) getCheckListRepository().getItem((String) c2CheckListItem.getRepositoryId(),BBBCoreConstants.CHECKLIST_CATEGORY_ITEM_DESCRIPTOR);
										if(null != checkListItem2.getPropertyValue(BBBCoreConstants.IS_DISABLED) && !(boolean)checkListItem2.getPropertyValue(BBBCoreConstants.IS_DISABLED) && null != checkListItem2.getPropertyValue(BBBCoreConstants.IS_DELETED) && !(boolean)checkListItem2.getPropertyValue(BBBCoreConstants.IS_DELETED)){
											this.logDebug("CheckListTools.populateStaticCheckListData() method C2 IS_DISABLED flag is off,IS_DELETED is also false: " + (String) c2CheckListItem.getRepositoryId());
											if(null != checkListItem2.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE) && (!(boolean)checkListItem2.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE) && isStagingServer() || (boolean)checkListItem2.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE))){
												this.logDebug("CheckListTools.populateStaticCheckListData() method C2 IS_CONFIGURE_COMPLETE is on: "+ (String) c2CheckListItem.getRepositoryId());
												CategoryVO c2CategoryVO = populateCategoryVO(checkListItem2,null,c1CategoryVO.getDisplayName(),checkListVO);
										@SuppressWarnings("unchecked")
										
										List<RepositoryItem> c3CheckListItems = (List<RepositoryItem>)checkListItem2.getPropertyValue(BBBCoreConstants.CHILD_CHECKLIST_CATEGORY_ID);
										if(!BBBUtility.isListEmpty(c3CheckListItems)){
											//Populating C3's
											List<CategoryVO> c3CategoryList = new ArrayList<CategoryVO>();
											int size = 0;
											for(RepositoryItem c3CheckListItem : c3CheckListItems){		
												
												MutableRepositoryItem checkListItem3 = (MutableRepositoryItem) getCheckListRepository().getItem((String) c3CheckListItem.getRepositoryId(),BBBCoreConstants.CHECKLIST_CATEGORY_ITEM_DESCRIPTOR);
												if(null != checkListItem3.getPropertyValue(BBBCoreConstants.IS_DISABLED) && !(boolean)checkListItem3.getPropertyValue(BBBCoreConstants.IS_DISABLED) && null != checkListItem3.getPropertyValue(BBBCoreConstants.IS_DELETED) && !(boolean)checkListItem3.getPropertyValue(BBBCoreConstants.IS_DELETED)){
													this.logDebug("CheckListTools.populateStaticCheckListData() method C3 IS_DISABLED flag is off, IS_DELETED is also false: "+ (String) c3CheckListItem.getRepositoryId());
													if(null != checkListItem3.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE) && (!(boolean)checkListItem3.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE) && isStagingServer() || (boolean)checkListItem3.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE))){
														this.logDebug("CheckListTools.populateStaticCheckListData() method C3 IS_CONFIGURE_COMPLETE is on: "+ (String) c3CheckListItem.getRepositoryId());
														CategoryVO c3CategoryVO = populateCategoryVO(checkListItem3,c2CategoryVO.getDisplayName(),c1CategoryVO.getDisplayName(),checkListVO);
												//Calculation of dynamic attributes like quantity and percentage at C3 level.
												
													size = size + 1;
													c3CategoryVO = this.updateCheckListCompletionStatus(checklistSKUList,c3CategoryVO,manuallySelectedCategoryList);
												c3CategoryList.add(c3CategoryVO);
																							
												if(c3CategoryVO.isC3ManualComplete() || c3CategoryVO.isC3AutoComplete()){
													c2CategoryVO.setC2CompleteCount(c2CategoryVO.getC2CompleteCount() + 1);				
												}
												if((c3CategoryVO.getAddedQuantity() <= c3CategoryVO.getSuggestedQuantity() && c3CategoryVO.isC3ManualComplete()) || c3CategoryVO.getAddedQuantity() > c3CategoryVO.getSuggestedQuantity()){
													c1CategoryVO.setTotalAddedQuantityForPercentageCalc(c1CategoryVO.getTotalAddedQuantityForPercentageCalc() + c3CategoryVO.getSuggestedQuantity());
												}
												else if(c3CategoryVO.getAddedQuantity() != 0){
													c1CategoryVO.setTotalAddedQuantityForPercentageCalc(c1CategoryVO.getTotalAddedQuantityForPercentageCalc() + c3CategoryVO.getAddedQuantity());
												}
												c1CategoryVO.setTotalSuggestedQuantityForPercentageCalc(c1CategoryVO.getTotalSuggestedQuantityForPercentageCalc() + c3CategoryVO.getSuggestedQuantity());
												}
											}
												}	
											c2CategoryVO.setNumberOfC3(size);											
											//Setting C3 category map in C2 CategoryVO.
											c2CategoryVO.setChildCategoryVO(c3CategoryList);											
										}
										else{
											//Calculation of dynamic attributes like quantity based on package count and count of categories completed at C2 category level if C2 is leaf node
											c2CategoryVO = this.updateCheckListCompletionStatus(checklistSKUList,c2CategoryVO,manuallySelectedCategoryList);
											c2CategoryVO.setChildCategoryVO(null);
											if(c2CategoryVO.isC3ManualComplete() || c2CategoryVO.isC3AutoComplete()){
												c2CategoryVO.setC2CompleteCount(1);												
												c1CategoryVO.setC2CompleteCount(++completedC2);
											}
											if((c2CategoryVO.getAddedQuantity() <= c2CategoryVO.getSuggestedQuantity() && c2CategoryVO.isC3ManualComplete()) || c2CategoryVO.getAddedQuantity() > c2CategoryVO.getSuggestedQuantity()){
												c1CategoryVO.setTotalAddedQuantityForPercentageCalc(c1CategoryVO.getTotalAddedQuantityForPercentageCalc() + c2CategoryVO.getSuggestedQuantity());
											}
											else if(c2CategoryVO.getAddedQuantity() != 0){
												c1CategoryVO.setTotalAddedQuantityForPercentageCalc(c1CategoryVO.getTotalAddedQuantityForPercentageCalc() + c2CategoryVO.getAddedQuantity());
											}
											c2CategoryVO.setNumberOfC3(1);											
											c1CategoryVO.setTotalSuggestedQuantityForPercentageCalc(c1CategoryVO.getTotalSuggestedQuantityForPercentageCalc() + c2CategoryVO.getSuggestedQuantity());											
										}
										c2CategoryList.add(c2CategoryVO);																			
									}
									}
									}
								}									
								//Setting the C2 category map in C1 category.
								
								c1CategoryVO.setChildCategoryVO(c2CategoryList);
								//Calculating C1 category percentage here.
								c1CategoryVO.setC1PercentageComplete(Double.valueOf(df.format(this.updateCheckListProgress(c1CategoryVO.getTotalAddedQuantityForPercentageCalc(),c1CategoryVO.getTotalSuggestedQuantityForPercentageCalc()))));
								//Image url is being set based on percentage. If percentage is 100%, completed image url is set at C1 level, else incomplete image url is set.
								c1CategoryVO.setImageURL(fetchImageUrlBasedOnPercentage(c1CategoryVO.getC1PercentageComplete(),c1CategoryVO,null));
								//Setting label message based on C1 percentage.
								
								c1CategoryVO.setLabelMessage(this.getLabelMessageForC1Percentage(c1CategoryVO.getC1PercentageComplete()));
								
								//Average percentage of all C1's to be used only for mobile.
								overallAveragePercentage = (double) overallAveragePercentage + c1CategoryVO.getC1PercentageComplete();
								//Adding C1 CategoryVO in CategoryVO list.
								c1CategoryList.add(c1CategoryVO);
							}
						}
						} catch (RepositoryException e) {
							BBBPerformanceMonitor.cancel(
									"CheckListTools", "populateCheckListVO");
							this.logError(
									BBBCoreErrorConstants.CHECKLIST_ERROR_10133
											+ " RepositoryException from populateStaticCheckListData of CheckListTools",
									e);
							throw new BBBSystemException(
									BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
									BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
									e);
						}
						
					}
					//Collections.sort(c1CategoryList, new CheckListComparator());
					}
			}
			checkListVO.setCategoryListVO(c1CategoryList);
			if(c1CategoryList.size() != 0){
				checkListVO.setAverageC1Percentage(Double.valueOf(df.format((double)overallAveragePercentage/(double)c1CategoryList.size())));
			}else{
				checkListVO.setAverageC1Percentage(0);
			}
		}
			else{
				return null;
			}
		this.logDebug("CheckListTools.populateStaticCheckListData() method end");
		long endTime = System.currentTimeMillis();
		this.logDebug("total time taken in populating CheckListVO() : " + (endTime-startTime));
		BBBPerformanceMonitor.end(
				"CheckListTools", "populateCheckListVO");		
		}
		return checkListVO;
	}
	
	/**
	 * The method returns completed or incomplete image url based on percentage. 
	 * @param percentage
	 * @param c1CategoryVO
	 * @param c1Item
	 * @return String
	 */
	public String fetchImageUrlBasedOnPercentage(double percentage,CategoryVO c1CategoryVO,RepositoryItem c1Item){
		this.logDebug("CheckListTools.fetchImageUrlBasedOnPercentage() method start");	
		this.logDebug("CheckListTools.percentage: " + percentage);	
		String siteId = SiteContextManager.getCurrentSiteId();
		if(percentage >= 0 && percentage < 100){
			if(null != siteId && !StringUtils.isEmpty(siteId)){
				if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
					if(null!= c1CategoryVO){
						return c1CategoryVO.getCaImageURL();
					}
					else{
						return (String)c1Item.getPropertyValue(BBBCoreConstants.CA_IMAGE_URL);
					}
					}
				else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)){
					if(null!= c1CategoryVO){
						return c1CategoryVO.getImageURL();
					}
					else
					{
						return (String)c1Item.getPropertyValue(BBBCoreConstants.CHECKLIST_IMAGE_URL);
					}
				}
				else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)){
					if(null!= c1CategoryVO){
						return c1CategoryVO.getBabyImageURL();
					}
					else{
						return (String)c1Item.getPropertyValue(BBBCoreConstants.BABY_IMAGE_URL);
					}
				}
			}
		}
		else{
			if(null != siteId && !StringUtils.isEmpty(siteId)){
				if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
					if(null!= c1CategoryVO){
						return c1CategoryVO.getCaImageURL().replace(BBBCoreConstants.QUESTION_MARK,BBBCoreConstants.COMPLETE_IMAGE);
					}
					else{
						return ((String)c1Item.getPropertyValue(BBBCoreConstants.CA_IMAGE_URL)).replace(BBBCoreConstants.QUESTION_MARK,BBBCoreConstants.COMPLETE_IMAGE);
					}
					}
				else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)){
					if(null!= c1CategoryVO){
						return c1CategoryVO.getImageURL().replace(BBBCoreConstants.QUESTION_MARK,BBBCoreConstants.COMPLETE_IMAGE);
					}
					else{
						return ((String)c1Item.getPropertyValue(BBBCoreConstants.CHECKLIST_IMAGE_URL)).replace(BBBCoreConstants.QUESTION_MARK,BBBCoreConstants.COMPLETE_IMAGE);
					}
				}
				else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)){
					if(null!= c1CategoryVO){
						return c1CategoryVO.getBabyImageURL().replace(BBBCoreConstants.QUESTION_MARK,BBBCoreConstants.COMPLETE_IMAGE);
					}
					else{
						return ((String)c1Item.getPropertyValue(BBBCoreConstants.BABY_IMAGE_URL)).replace(BBBCoreConstants.QUESTION_MARK,BBBCoreConstants.COMPLETE_IMAGE);
					}
				}
			}
		}
		this.logDebug("CheckListTools.fetchImageUrlBasedOnPercentage() method end");	
		return null;
	}
	
	/**
	 * The method populates static checklist vo based on a config key for that registry. 
	 * @param registryId
	 * @param checkListItems
	 * @return CheckListVO
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException
	 */
	public CheckListVO populateStaticCheckListVO(final String registryId,final RepositoryItem[] checkListItems) throws BBBSystemException, BBBBusinessException{
		BBBPerformanceMonitor.start(
				"CheckListTools", "populateCheckListVO");
		this.logDebug("CheckListTools.populateStaticCheckListData() method start: registryType: " + registryId);
		long startTime = System.currentTimeMillis();
		CheckListVO checkListVO = new CheckListVO();
		String siteId = SiteContextManager.getCurrentSiteId();		
		if(null != checkListItems && !(checkListItems.length == 0)){
			if(null != checkListItems[0]){
			checkListVO.setChecklistId((String) checkListItems[0].getRepositoryId());
			checkListVO.setChecklistTypeName((String) checkListItems[0].getPropertyValue(BBBCoreConstants.SUBTYPE_CODE));
			checkListVO.setDisplayName((String) checkListItems[0].getPropertyValue(BBBCoreConstants.DISPLAY_NAME));
			checkListVO.setSiteFlag((String) checkListItems[0].getPropertyValue(BBBCoreConstants.SITE_FLAG));
			List<CategoryVO> c1CategoryList = new ArrayList<CategoryVO>();
			if(null != checkListItems[0].getPropertyValue(BBBCoreConstants.CHECK_LIST_CATEGORIES)){
				@SuppressWarnings("unchecked")
				List<RepositoryItem> checkListCategories = (List<RepositoryItem>)checkListItems[0].getPropertyValue(BBBCoreConstants.CHECK_LIST_CATEGORIES);
				if(!BBBUtility.isListEmpty(checkListCategories)){							
					for(RepositoryItem checkListItem : checkListCategories){
						//Populating C1's
						try {
							MutableRepositoryItem c1CheckListItem = (MutableRepositoryItem) getCheckListRepository().getItem((String) checkListItem.getRepositoryId(),BBBCoreConstants.CHECKLIST_CATEGORY_ITEM_DESCRIPTOR);
							if(null != c1CheckListItem && null != c1CheckListItem.getPropertyValue(BBBCoreConstants.SHOW_ON_CHECKLIST) && (boolean)c1CheckListItem.getPropertyValue(BBBCoreConstants.SHOW_ON_CHECKLIST) && null != c1CheckListItem.getPropertyValue(BBBCoreConstants.IS_DISABLED) && !((boolean)c1CheckListItem.getPropertyValue(BBBCoreConstants.IS_DISABLED)) && null != c1CheckListItem.getPropertyValue(BBBCoreConstants.IS_DELETED) && !((boolean)c1CheckListItem.getPropertyValue(BBBCoreConstants.IS_DELETED))){						
									if(null != c1CheckListItem.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE) && (!(boolean)c1CheckListItem.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE) && isStagingServer() || (boolean)c1CheckListItem.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE))){
								CategoryVO c1CategoryVO = populateCategoryVO(c1CheckListItem,null,null,checkListVO);
								//Populating C2's
								List<CategoryVO> c2CategoryList = new ArrayList<CategoryVO>();
								@SuppressWarnings("unchecked")
								List<RepositoryItem> c2CheckListItems = (List<RepositoryItem>)c1CheckListItem.getPropertyValue(BBBCoreConstants.CHILD_CHECKLIST_CATEGORY_ID);
								if(!BBBUtility.isListEmpty(c2CheckListItems)){
									for(RepositoryItem c2CheckListItem : c2CheckListItems){
										MutableRepositoryItem checkListItem2 = (MutableRepositoryItem) getCheckListRepository().getItem((String) c2CheckListItem.getRepositoryId(),BBBCoreConstants.CHECKLIST_CATEGORY_ITEM_DESCRIPTOR);
										if(null != checkListItem2.getPropertyValue(BBBCoreConstants.IS_DISABLED) && !(boolean)checkListItem2.getPropertyValue(BBBCoreConstants.IS_DISABLED) && null != checkListItem2.getPropertyValue(BBBCoreConstants.IS_DELETED) && !(boolean)checkListItem2.getPropertyValue(BBBCoreConstants.IS_DELETED)){
											if(null != checkListItem2.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE) && (!(boolean)checkListItem2.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE) && isStagingServer() || (boolean)checkListItem2.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE))){
										CategoryVO c2CategoryVO = populateCategoryVO(checkListItem2,null,c1CategoryVO.getDisplayName(),checkListVO);
										@SuppressWarnings("unchecked")
										List<RepositoryItem> c3CheckListItems = (List<RepositoryItem>)checkListItem2.getPropertyValue(BBBCoreConstants.CHILD_CHECKLIST_CATEGORY_ID);
										if(!BBBUtility.isListEmpty(c3CheckListItems)){
											//Populating C3's
											List<CategoryVO> c3CategoryList = new ArrayList<CategoryVO>();
											for(RepositoryItem c3CheckListItem : c3CheckListItems){												
												MutableRepositoryItem checkListItem3 = (MutableRepositoryItem) getCheckListRepository().getItem((String) c3CheckListItem.getRepositoryId(),BBBCoreConstants.CHECKLIST_CATEGORY_ITEM_DESCRIPTOR);
												if(null != checkListItem3.getPropertyValue(BBBCoreConstants.IS_DISABLED) && !(boolean)checkListItem3.getPropertyValue(BBBCoreConstants.IS_DISABLED) && null != checkListItem3.getPropertyValue(BBBCoreConstants.IS_DELETED) && !(boolean)checkListItem3.getPropertyValue(BBBCoreConstants.IS_DELETED)){
													if(null != checkListItem3.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE) && (!(boolean)checkListItem3.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE) && isStagingServer() || (boolean)checkListItem3.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE))){
												CategoryVO c3CategoryVO = populateCategoryVO(checkListItem3,c2CategoryVO.getDisplayName(),c1CategoryVO.getDisplayName(),checkListVO);
												//Calculation of dynamic attributes like quantity and percentage at C3 level.												
												c3CategoryList.add(c3CategoryVO);																																															
											}
												}
											}
											//Setting C3 category map in C2 CategoryVO.
											c2CategoryVO.setChildCategoryVO(c3CategoryList);											
										}
										else{
											//Calculation of dynamic attributes like quantity based on package count and count of categories completed at C2 category level if C2 is leaf node											
											c2CategoryVO.setChildCategoryVO(null);																					
										}
										c2CategoryList.add(c2CategoryVO);																			
									}
								}
									}
								}									
								//Setting the C2 category map in C1 category.
								c1CategoryVO.setChildCategoryVO(c2CategoryList);
								//Calculating C1 category percentage here.															
									if(null != siteId && !StringUtils.isEmpty(siteId)){
										if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
											c1CategoryVO.setImageURL(c1CategoryVO.getCaImageURL());
											}
										else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)){
											c1CategoryVO.setImageURL(c1CategoryVO.getImageURL());
										}
										else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)){
											c1CategoryVO.setImageURL(c1CategoryVO.getBabyImageURL());
										}
									}							
								//Adding C1 CategoryVO in CategoryVO list.
								c1CategoryList.add(c1CategoryVO);
							}		
						}
						} catch (RepositoryException e) {
							BBBPerformanceMonitor.cancel(
									"CheckListTools", "populateCheckListVO");
							this.logError(
									BBBCoreErrorConstants.CHECKLIST_ERROR_10133
											+ " RepositoryException from populateStaticCheckListData of CheckListTools",
									e);
							throw new BBBSystemException(
									BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
									BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
									e);
						}
						
					}
					//Collections.sort(c1CategoryList, new CheckListComparator());
					}
			}
			checkListVO.setCategoryListVO(c1CategoryList);
		}
			else{
				return null;
			}
		this.logDebug("CheckListTools.populateStaticCheckListData() method end");
		long endTime = System.currentTimeMillis();
		this.logDebug("total time taken in populating CheckListVO() : " + (endTime-startTime));
		BBBPerformanceMonitor.end(
				"CheckListTools", "populateCheckListVO");		
		}
		return checkListVO;
	}
	
	/**
	 * This method is used to set label message for c1 category based on C1 percentage
	 * @param percentage
	 *           
	 * @return labelMessage
	 */
	public String getLabelMessageForC1Percentage(double percentage) throws BBBSystemException, BBBBusinessException{
		this.logDebug("CheckListTools.getLabelMessageForC1Percentage() method start");
		this.logDebug("CheckListTools.getLabelMessageForC1Percentage() Setting label for C1 percentage: " + percentage);
		List<String> percentLabelMsg = null;
		List<String> percentRange = null;
		List<String> zeroCheckListPercentage = null;
		DecimalFormat df = new DecimalFormat(BBBCoreConstants.DECIMAL_PLACES);
		String percentRangeValue = null;
		double percent = Double.valueOf(df.format(percentage));
		percentRange = this.getCatalogTools().getAllValuesForKey(
				BBBCoreConstants.CONTENT_CATALOG_KEYS,
				BBBCoreConstants.CHECKLIST_PERCENTAGE_RANGE);
		if (null != percentRange && !percentRange.isEmpty()) {
			  percentRangeValue = percentRange.get(0);
		}	
		String[] percentRangeArray = percentRangeValue.split(BBBCoreConstants.COMMA);
		if(!BBBUtility.isEmpty(percentRangeArray)){
		for(String percentValue: percentRangeArray){
			String[] rangeValues = null;
			if(percentValue.contains(BBBCoreConstants.HYPHEN)){
				rangeValues = percentValue.split(BBBCoreConstants.HYPHEN);
				if(percent >= Double.parseDouble(rangeValues[0]) && percent < Double.parseDouble(rangeValues[1]) && Double.compare(percent, 0) != 0){				
					percentLabelMsg = this.getCatalogTools().getAllValuesForKey(
							BBBCoreConstants.CONTENT_CATALOG_KEYS,
							percentValue + BBBCoreConstants.UNDERSCORE + BBBCoreConstants.CHECKLIST_PERCENTAGE);
					if (!BBBUtility.isListEmpty(percentLabelMsg)) {
						 return percentLabelMsg.get(0);
					}
					
				}
			}
			else{
			if(Double.compare(Double.parseDouble(percentValue),0) == BBBCoreConstants.ZERO && Double.compare(percent , Double.parseDouble(percentValue)) == BBBCoreConstants.ZERO){
				return "";
			}
			else if(Double.compare(Double.parseDouble(percentValue),100) == BBBCoreConstants.ZERO && Double.compare(percent , Double.parseDouble(percentValue)) == BBBCoreConstants.ZERO){
				zeroCheckListPercentage = this.getCatalogTools().getAllValuesForKey(
						BBBCoreConstants.CONTENT_CATALOG_KEYS,
						BBBCoreConstants.HUNDRED_CHECKLIST_PERCENTAGE);
				if(!BBBUtility.isListEmpty(zeroCheckListPercentage)){
					 return zeroCheckListPercentage.get(0);
				}
			}
			}
		}
	}
		return null;
	}
	
	/**
	 * This method is used to fetch the repository item for a categoryId. 
	 * 
	 * @param registryId
	 *            the registryId 
	 * @param categoryId
	 *            the categoryId  
	 * @param registryType
	 *            the registryType
	 * @return boolean
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	public MutableRepositoryItem getSuggestedQuanityForCategoryItem(final String categoryIDForC3)
			throws BBBSystemException {
		this.logDebug("CheckListTools.getSuggestedQuanityForCategoryItem() method start");
		this.logDebug("CheckListTools.categoryIDForC3: " + categoryIDForC3);
		MutableRepositoryItem checkListItem = null;	
		try {
			checkListItem = (MutableRepositoryItem) this
					.getCheckListRepository()
					.getItem(categoryIDForC3,
							BBBCoreConstants.CHECKLIST_CATEGORY_ITEM_DESCRIPTOR);
		} catch (RepositoryException e) {
			BBBPerformanceMonitor.cancel("CheckListManager",
					"getUpdatedProgressOnManualCheck");
			this.logError(
					BBBCoreErrorConstants.CHECKLIST_ERROR_10133
							+ " RepositoryException from getUpdatedProgressOnManualCheck() of CheckListManager",
					e);
			throw new BBBSystemException(
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					e);
		}
		this.logDebug("CheckListTools.getSuggestedQuanityForCategoryItem() method end");
		return checkListItem;
	}
	
	class CheckListComparator implements Comparator<RepositoryItem> {

		  @Override
		  public int compare(RepositoryItem item1, RepositoryItem item2) {
		    return ((Integer) item1.getPropertyValue(BBBCoreConstants.SEQUENCE_NUMBER)).compareTo((Integer)item2.getPropertyValue(BBBCoreConstants.SEQUENCE_NUMBER));
		  }
		}

	/**
	 * This method is used to calculate c1 category percentage
	 * @param categoryVO
	 *            the CategoryVO
	 * @return the CategoryVO
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public double updateCheckListProgress(final int completedCount,final int totalCount) throws BBBSystemException, BBBBusinessException{
		BBBPerformanceMonitor.start(
				"CheckListTools", "updateCheckListProgress");
		this.logDebug("CheckListTools.calculatePercentage() method start");	
		this.logDebug("CheckListTools.TotalAddedQuantityForPercentageCalc: " + completedCount + " ,TotalSuggestedQuantityForPercentageCalc: " + totalCount);
		List<String> thresholdPercentRangeKey = null;
		String thresholdPercentRange = null;
		double percentage = 0.0;
		if(0 != totalCount){
			this.logDebug("CheckListTools.calculatePercentage() method end");
			percentage = ((double)(completedCount*100)/(double)totalCount);
			this.logDebug("Percentage is: "+percentage);
			thresholdPercentRangeKey = this.getCatalogTools().getAllValuesForKey(
					BBBCoreConstants.CONTENT_CATALOG_KEYS,
					BBBCoreConstants.THRESHOLD_CHECKLIST_PERCENTAGE_RANGE);
			if(!BBBUtility.isListEmpty(thresholdPercentRangeKey)){
				thresholdPercentRange = thresholdPercentRangeKey.get(0);
			}
			if(percentage < Integer.parseInt(thresholdPercentRange) && percentage > 0){
				this.logDebug("Percentage is with in threshold range");
					return Integer.parseInt(thresholdPercentRange);				
			}
			else
			{
				this.logDebug("Percentage is not in threshold range");
				return percentage;
			}
		}
		this.logDebug("CheckListTools.calculatePercentage() method end");
		BBBPerformanceMonitor.end(
				"CheckListTools", "updateCheckListProgress");
		return 0;
	}
	
	/**
	 * This method is used to set the values in CheckListProgressVO 
	 * @param checkListProgressVO
	 *            the CheckListProgressVO
	 * @param C1CheklistId
	 * @param C2CheklistId  
	 * @param C3CheklistId 
	 * @param int
	 * @param int
	 * @param int
	 * @return the CheckListProgressVO
	 */
	public CheckListProgressVO setCheckListProgressVO(final String registryType,final CheckListProgressVO checkListProgressVO,final String categoryIDForC1,final String categoryIDForC2,final String categoryIDForC3,int totalC3CountChecked,final int totalC3CategoriesCount,double averagePercentage, double initialC1Percentage,int noOfC1) throws BBBSystemException, BBBBusinessException{
			this.logDebug("CheckListTools.setCheckListProgressVO() method start");
			this.logDebug("Input parameters: registryType: " + registryType + " categoryIDForC1: " + categoryIDForC1 + " categoryIDForC2: " + categoryIDForC2 + " categoryIDForC3: " + categoryIDForC3 + " totalC3CountChecked: " + totalC3CountChecked + " totalC3CategoriesCount: " + totalC3CategoriesCount + " averagePercentage: " + averagePercentage + " initialC1Percentage: " + initialC1Percentage + " noOfC1: " + noOfC1);
			checkListProgressVO.setCategoryIDForC1(categoryIDForC1);
			checkListProgressVO.setCategoryIDForC2(categoryIDForC2);
			checkListProgressVO.setCategoryIDForC3(categoryIDForC3);	
			checkListProgressVO.setUpdatedAddedQuantity(totalC3CountChecked);
			MutableRepositoryItem c1Item = this.getSuggestedQuanityForCategoryItem(categoryIDForC1);
			double updatedC1Percentage = this.updateCheckListProgress(totalC3CountChecked, totalC3CategoriesCount);
			DecimalFormat df = new DecimalFormat(BBBCoreConstants.DECIMAL_PLACES);  
			averagePercentage = (double)((double)((averagePercentage*noOfC1) - initialC1Percentage + updatedC1Percentage)/(double)noOfC1);
			if(updatedC1Percentage <0){
				updatedC1Percentage = 0;
			}
			if(averagePercentage <0){
				averagePercentage = 0;
			}
			//Image url is being set based on percentage. If percentage is 100%, completed image url is set at C1 level, else incomplete image url is set.
			checkListProgressVO.setC1ImageUrl(fetchImageUrlBasedOnPercentage(updatedC1Percentage, null, c1Item));		
			checkListProgressVO.setCategoryProgress(Double.valueOf(df.format(updatedC1Percentage)));
			checkListProgressVO.setOverAllProgress(Double.valueOf(df.format(averagePercentage)));
			checkListProgressVO.setLabelMessage(this.getLabelMessageForC1Percentage(updatedC1Percentage));
			this.logDebug("CheckListTools.setCheckListProgressVO() method end");
			return checkListProgressVO;
		}
	
	/**
	 * This method is used to fetch checkList item from check list 
	 * repository based on registryType.
	 * 
	 * @param registryType
	 *            the registry Type
	 * @return the repository item[]
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	public RepositoryItem[] fetchCheckListRepositoryItem(String registryType) throws BBBSystemException,
			BBBBusinessException {
		BBBPerformanceMonitor.start(
				"CheckListTools", "fetchCheckListRepositoryItem");
		this.logDebug("CheckListTools.fetchCheckListRepositoryItem() method start");
		RepositoryItem[] checkListRegistryRepoItem = null;
		String regTypeCode = null;
		String regTypeList = null;
		regTypeList = this.getCatalogTools().getConfigKeyValue(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.CHECKLIST_REGISTRY_TYPES, BBBCoreConstants.BLANK);
		if(null != registryType && BBBUtility.isNotEmpty(regTypeList) && regTypeList.contains(registryType)){
			regTypeCode = registryType;
		}
		else if(null != registryType){
		String siteId = SiteContextManager.getCurrentSiteId();
		if(registryType.equalsIgnoreCase(BBBCoreConstants.COMMITMENT)){
			registryType = BBBCoreConstants.COMMITMENT + " " + BBBCoreConstants.CEREMONY;
		}
		 regTypeCode = this.getGiftRegistryTools()
				.getRegistryTypeCode(registryType,siteId);
		}
		if (!BBBUtility.isEmpty(regTypeCode)) {
			final Object[] params = new Object[1];
			params[0] = regTypeCode;

			checkListRegistryRepoItem = this.executeRQLQuery(
					this.getQueryCheckListForRegistryType(), params, BBBCoreConstants.CHECKLIST ,
					this.getCheckListRepository());
		}
		this.logDebug("CheckListTools.fetchCheckListRepositoryItem() method ends");
		BBBPerformanceMonitor.end(
				"CheckListTools", "fetchCheckListRepositoryItem");
		return checkListRegistryRepoItem;
	}

	/**
	 * This method is used to populate CategoryVO from category repository item  
	 * @param checkListItem
	 *            the repository item
	 * @return the CategoryVO
	 */
	public CategoryVO populateCategoryVO(final RepositoryItem checkListItem,String c2name,String c1name,CheckListVO checkListVO){
		BBBPerformanceMonitor.start(
				"CheckListTools", "populateCategoryVO");
		this.logDebug("CheckListTools.populateCategoryVO() method starts for cat id: " + (String)checkListItem.getRepositoryId());
		CategoryVO categoryVO = new CategoryVO();
		String siteId = SiteContextManager.getCurrentSiteId();
	
		if(null != siteId && !StringUtils.isEmpty(siteId)){
			
		if(null != checkListItem.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)){
			categoryVO.setDisplayName((String)checkListItem.getPropertyValue(BBBCoreConstants.DISPLAY_NAME));
		}
		
		categoryVO.setCategoryId((String)checkListItem.getRepositoryId());
		
		if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
			if(null != checkListItem.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL)){
				//categoryVO.setCategoryURL((String)checkListItem.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL));
				String categoryDisplayName = (String)checkListItem.getPropertyValue(BBBCoreConstants.DISPLAY_NAME);
				String checklistURL = (String)checkListItem.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL);
				if (categoryDisplayName.contains(BBBCoreConstants.SLASH) || categoryDisplayName.contains(BBBCoreConstants.SPACE)) {
					checklistURL = checklistURL.replaceAll(categoryDisplayName, categoryDisplayName.replaceAll(BBBCoreConstants.SPACE_HYPHEN, BBBCoreConstants.HYPHEN));
				}
				categoryVO.setCaOverriddenURL(true);
				categoryVO.setCacategoryURL(checklistURL);
			}else{
				// BBBJ-1222: DSK/ MOB: PLP SEO (ATG)
				// If admin user has not populated the category url then
				// category urls will be auto generated.
				String formatUrl=fetchFormattedURL(checkListVO,c1name,c2name,categoryVO.getDisplayName(),categoryVO.getCategoryId());
				categoryVO.setCacategoryURL(formatUrl);
			}			
			if(null != checkListItem.getPropertyValue(BBBCoreConstants.CA_IMAGE_URL)){
				categoryVO.setCaImageURL((String)checkListItem.getPropertyValue(BBBCoreConstants.CA_IMAGE_URL));
			}			
		}
		else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)){
			if(null != checkListItem.getPropertyValue(BBBCoreConstants.CATEGORY_URL)){
				//categoryVO.setCategoryURL((String)checkListItem.getPropertyValue(BBBCoreConstants.CATEGORY_URL));
				String categoryDisplayName = (String)checkListItem.getPropertyValue(BBBCoreConstants.DISPLAY_NAME);
				String checklistURL = (String)checkListItem.getPropertyValue(BBBCoreConstants.CATEGORY_URL);
				if (categoryDisplayName.contains(BBBCoreConstants.SLASH) || categoryDisplayName.contains(BBBCoreConstants.SPACE)) {
					checklistURL = checklistURL.replaceAll(categoryDisplayName, categoryDisplayName.replaceAll(BBBCoreConstants.SPACE_HYPHEN, BBBCoreConstants.HYPHEN));
				}
				categoryVO.setUsOverriddenURL(true);
				categoryVO.setUscategoryURL(checklistURL);
			}else{
				// BBBJ-1222: DSK/ MOB: PLP SEO (ATG)
				// If admin user has not populated the category url then
				// category urls will be auto generated.
				String formatUrl=fetchFormattedURL(checkListVO,c1name,c2name,categoryVO.getDisplayName(),categoryVO.getCategoryId());
				categoryVO.setUscategoryURL(formatUrl);
			}
			if(null != checkListItem.getPropertyValue(BBBCoreConstants.CHECKLIST_IMAGE_URL)){
				categoryVO.setImageURL((String)checkListItem.getPropertyValue(BBBCoreConstants.CHECKLIST_IMAGE_URL));
			}				
		}
		else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)){
			if(null != checkListItem.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL)){
				//categoryVO.setCategoryURL((String)checkListItem.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL));
				String categoryDisplayName = (String)checkListItem.getPropertyValue(BBBCoreConstants.DISPLAY_NAME);
				String checklistURL = (String)checkListItem.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL);
				if (categoryDisplayName.contains(BBBCoreConstants.SLASH) || categoryDisplayName.contains(BBBCoreConstants.SPACE)) {
					checklistURL = checklistURL.replaceAll(categoryDisplayName, categoryDisplayName.replaceAll(BBBCoreConstants.SPACE_HYPHEN, BBBCoreConstants.HYPHEN));
				}
				categoryVO.setBabyOverriddenURL(true);
				categoryVO.setBabycategoryURL(checklistURL);
				} else {
					// BBBJ-1222: DSK/ MOB: PLP SEO (ATG)
					// If admin user has not populated the category url then
					// category urls will be auto generated.
					String formatUrl=fetchFormattedURL(checkListVO,c1name,c2name,categoryVO.getDisplayName(),categoryVO.getCategoryId());
					categoryVO.setBabycategoryURL(formatUrl);
				}
			if(null != checkListItem.getPropertyValue(BBBCoreConstants.BABY_IMAGE_URL)){
				categoryVO.setBabyImageURL((String)checkListItem.getPropertyValue(BBBCoreConstants.BABY_IMAGE_URL));
			}					
		}
		
		if(null != checkListItem.getPropertyValue(BBBCoreConstants.CATEGORY_NAME)){
			categoryVO.setCategoryName((String)checkListItem.getPropertyValue(BBBCoreConstants.CATEGORY_NAME));		
		}
		
		if(null != checkListItem.getPropertyValue(BBBCoreConstants.PRIMARY_PARENT_CATEGORY_ID)){
			categoryVO.setPrimaryParentCategoryId((String)checkListItem.getPropertyValue(BBBCoreConstants.PRIMARY_PARENT_CATEGORY_ID));
		}
		/*if(null != checkListItem.getPropertyValue(BBBCoreConstants.SEQUENCE_NUMBER)){
			categoryVO.setSequenceNumber((int)checkListItem.getPropertyValue(BBBCoreConstants.SEQUENCE_NUMBER));
		}*/
		if(null != checkListItem.getPropertyValue(BBBCoreConstants.SUGGESTED_QUANTITY)){
			categoryVO.setSuggestedQuantity((int)checkListItem.getPropertyValue(BBBCoreConstants.SUGGESTED_QUANTITY));	
		}
		if(null != checkListItem.getPropertyValue(BBBCoreConstants.IS_DISABLED)){
			categoryVO.setDisabled((boolean)checkListItem.getPropertyValue(BBBCoreConstants.IS_DISABLED));	
		}
		if(null != checkListItem.getPropertyValue(BBBCoreConstants.IS_DELETED)){
			categoryVO.setDeleted((boolean)checkListItem.getPropertyValue(BBBCoreConstants.IS_DELETED));	
		}
		if(null != checkListItem.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE)){
			categoryVO.setConfigureComplete((boolean)checkListItem.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE));
		}
		}
		this.logDebug("CheckListTools.populateCategoryVO() method ends");
		BBBPerformanceMonitor.end(
				"CheckListTools", "populateCategoryVO");
		return categoryVO;
	}
	
	
	
	/**
	 * This method is used to populate the values in CheckListPrevNextCategoriesVO from the passed categoryId's 
	 * for current category, previous category and next category  
	 * @param prevNextCategoriesVO the CheckListPrevNextCategoriesVO
	 * @param currentCatId the current category Id
	 * @param prevCatId the previous category Id   
	 * @param nextCatId the next category Id  
	 * @return the CheckListPrevNextCategoriesVO
	 * @throws BBBSystemException 
	 */
	public CheckListPrevNextCategoriesVO setSurroundingCatValues(CheckListPrevNextCategoriesVO prevNextCategoriesVO,final String currentCatId,final String prevCatId,final String nextCatId) throws BBBSystemException{
	try {
		BBBPerformanceMonitor.start(
				"CheckListTools", "setSurroundingCatValues");
		String siteId = SiteContextManager.getCurrentSiteId();
		if(null != currentCatId && !StringUtils.isEmpty(currentCatId)){
		MutableRepositoryItem checkListItem3 = (MutableRepositoryItem) this.getCheckListRepository().getItem(currentCatId,BBBCoreConstants.CHECKLIST_CATEGORY_ITEM_DESCRIPTOR);
		if(null != checkListItem3){						
			if(null != siteId && !StringUtils.isEmpty(siteId)){
			if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
				if(null != checkListItem3.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL)){
					prevNextCategoriesVO.setCurrentCatUrl((String)checkListItem3.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL));
				}															
			}
			else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)){
				if(null != checkListItem3.getPropertyValue(BBBCoreConstants.CATEGORY_URL)){
					prevNextCategoriesVO.setCurrentCatUrl((String)checkListItem3.getPropertyValue(BBBCoreConstants.CATEGORY_URL));
				}														
			}
			else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)){
				if(null != checkListItem3.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL)){
					prevNextCategoriesVO.setCurrentCatUrl((String)checkListItem3.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL));
				}													
			}
			if(null != checkListItem3.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)){
				prevNextCategoriesVO.setCurrentCatName((String)checkListItem3.getPropertyValue(BBBCoreConstants.DISPLAY_NAME));		
			}
			if(null != checkListItem3.getPropertyValue(BBBCoreConstants.SUGGESTED_QUANTITY)){
				prevNextCategoriesVO.setSuggestedQuantity((Integer)checkListItem3.getPropertyValue(BBBCoreConstants.SUGGESTED_QUANTITY));		
			}
			prevNextCategoriesVO.setCurrentCatId(currentCatId);
		}			
	} 
		}
		if(null != prevCatId && !StringUtils.isEmpty(prevCatId)){
		MutableRepositoryItem prevItem = (MutableRepositoryItem) this.getCheckListRepository().getItem(prevCatId,BBBCoreConstants.CHECKLIST_CATEGORY_ITEM_DESCRIPTOR);
		if(null != prevItem){
			
			if(null != siteId && !StringUtils.isEmpty(siteId)){
			if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
				if(null != prevItem.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL)){
					prevNextCategoriesVO.setPrevCatUrl((String)prevItem.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL));
				}															
			}
			else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)){
				if(null != prevItem.getPropertyValue(BBBCoreConstants.CATEGORY_URL)){
					prevNextCategoriesVO.setPrevCatUrl((String)prevItem.getPropertyValue(BBBCoreConstants.CATEGORY_URL));
				}														
			}
			else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)){
				if(null != prevItem.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL)){
					prevNextCategoriesVO.setPrevCatUrl((String)prevItem.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL));
				}													
			}
			if(null != prevItem.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)){
				prevNextCategoriesVO.setPrevCatName((String)prevItem.getPropertyValue(BBBCoreConstants.DISPLAY_NAME));		
			}
			prevNextCategoriesVO.setPrevCatId(prevCatId);
			
		}
			
	} 
		}
		if(null != nextCatId && !StringUtils.isEmpty(nextCatId)){
			MutableRepositoryItem nextItem = (MutableRepositoryItem) this.getCheckListRepository().getItem(nextCatId,BBBCoreConstants.CHECKLIST_CATEGORY_ITEM_DESCRIPTOR);
			if(null != nextItem){
				
				if(null != siteId && !StringUtils.isEmpty(siteId)){
				if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
					if(null != nextItem.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL)){
						prevNextCategoriesVO.setNextCatUrl((String)nextItem.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL));
					}															
				}
				else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)){
					if(null != nextItem.getPropertyValue(BBBCoreConstants.CATEGORY_URL)){
						prevNextCategoriesVO.setNextCatUrl((String)nextItem.getPropertyValue(BBBCoreConstants.CATEGORY_URL));
					}														
				}
				else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)){
					if(null != nextItem.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL)){
						prevNextCategoriesVO.setNextCatUrl((String)nextItem.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL));
					}													
				}
				if(null != nextItem.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)){
					prevNextCategoriesVO.setNextCatName((String)nextItem.getPropertyValue(BBBCoreConstants.DISPLAY_NAME));		
				}
				prevNextCategoriesVO.setNextCatId(nextCatId);
				
				
			}
				
		} 
			}
		BBBPerformanceMonitor.end(
				"CheckListTools", "setSurroundingCatValues");
	return prevNextCategoriesVO;
	}catch (RepositoryException e) {
		BBBPerformanceMonitor.cancel(
				"CheckListTools", "setSurroundingCatValues");
		this.logError(
				BBBCoreErrorConstants.CHECKLIST_ERROR_10133
						+ " RepositoryException from setSurroundingCatValues of CheckListTools",
				e);
		throw new BBBSystemException(
				BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
				BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
				e);
	}
	}
	
	/**
	 * This method is used to fetch the categories which are manually checked
	 * from user repository based on registryId,userId,checkListid.
	 * 
	 * @param registryId
	 *            the registry id
	 * @param userId
	 *            the user id
	 *      @param checkListid
	 *            the check list id      
	 * @return the List<String>
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	public List<String> manuallySelectedCategories(final String userId,final String registryId,final String checkListid) throws BBBSystemException, BBBBusinessException{
		BBBPerformanceMonitor.start(
				"CheckListTools", "manuallySelectedCategories");
		this.logDebug("CheckListTools.manuallySelectedCategories() method starts");
		this.logDebug("CheckListTools.manuallySelectedCategories() Input parameters are registryId: " + registryId + " ,userId: " + userId + " ,checkListid: " + checkListid);
		RepositoryItem[] manualSelectedItem = null;
		List<String> manuallySelectedList = null;
		if (userId != null && registryId != null && checkListid != null && !StringUtils.isEmpty(userId) && !StringUtils.isEmpty(registryId) && !StringUtils.isEmpty(checkListid)) {
			final Object[] params = new Object[3];
			params[0] = userId;
			params[1] = registryId;
			params[2] = checkListid;

			manualSelectedItem = this.executeRQLQuery(
					this.getQueryManuallySelectedC3(), params, BBBCoreConstants.USER_MANUAL_CHECKED_CATEGORIES,
					this.getProfileRepository());
			if(manualSelectedItem != null){
				this.logDebug("CheckListTools.manuallySelectedCategories() manualSelectedItem is not null");
				String manuallySelectedC3s = (String)manualSelectedItem[0].getPropertyValue(BBBCoreConstants.USER_COMPLETED_CATEGORY_LIST);
				if(manuallySelectedC3s != null && !StringUtils.isEmpty(manuallySelectedC3s)){
					this.logDebug("CheckListTools.manuallySelectedCategories() manuallySelectedCategories are present");
					String[] arrayManuallySelectedC3 = manuallySelectedC3s.split(BBBCoreConstants.COMMA);
					return Arrays.asList(arrayManuallySelectedC3);
				}
			}
		}
		this.logDebug("CheckListTools.manuallySelectedCategories() method ends");
		BBBPerformanceMonitor.end(
				"CheckListTools", "manuallySelectedCategories");
		return manuallySelectedList;
	}
	
	/**
	 * This method is used to fetch the manually checked categories repository item
	 * from user repository based on registryId,userId,checkListid.
	 * 
	 * @param registryId
	 *            the registry id
	 * @param userId
	 *            the user id
	 *      @param checkListid
	 *            the check list id      
	 * @return the List<String>
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	public MutableRepositoryItem[] manuallySelectedItem(final String userId,final String registryId,final String checkListid) throws BBBSystemException, BBBBusinessException{
		BBBPerformanceMonitor.start(
				"CheckListTools", "manuallySelectedItem");
		this.logDebug("CheckListTools.manuallySelectedItem() method starts");
		this.logDebug("CheckListTools.manuallySelectedItem() Input parameters are registryId: " + registryId + " ,userId: " + userId + " ,checkListid: " + checkListid);
		MutableRepositoryItem[] manualSelectedItem = null;
		if (userId != null && registryId != null && checkListid != null && !StringUtils.isEmpty(userId) && !StringUtils.isEmpty(registryId) && !StringUtils.isEmpty(checkListid)) {
			final Object[] params = new Object[3];
			params[0] = userId;
			params[1] = registryId;
			params[2] = checkListid;

			manualSelectedItem = (MutableRepositoryItem[]) this.executeRQLQuery(
					this.getQueryManuallySelectedC3(), params, BBBCoreConstants.USER_MANUAL_CHECKED_CATEGORIES,
					this.getProfileRepository());			
		}
		this.logDebug("CheckListTools.manuallySelectedItem() method ends");
		BBBPerformanceMonitor.end(
				"CheckListTools", "manuallySelectedItem");
		return manualSelectedItem;
	}
	
	/**
	 * Fetching item for a sku from catalog repository
	 * 
	 * @param skuId
	 *            the skuId 
	 * @return int
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	public RepositoryItem getSkuComputedAttributeItem(final String skuId) throws BBBSystemException, BBBBusinessException{
		BBBPerformanceMonitor.start(
				"CheckListTools", "getSkuComputedAttributeItem");
		this.logDebug("CheckListTools.getSkuComputedAttributeItem() method starts");
		this.logDebug("CheckListTools.getSkuComputedAttributeItem() Input parameters are skuId: " + skuId);
		if (skuId != null && !StringUtils.isEmpty(skuId)) {
			try {
				return this.getCatalogRepository().getItem(skuId,BBBCoreConstants.SKU);
			} catch (RepositoryException e) {
				logError(LogMessageFormatter.formatMessage(null,
						"RepositoryException from service of CheckListTools.getSkuComputedAttributeItem()"), e);
			}
		}
		this.logDebug("CheckListTools.getSkuComputedAttributeItem() method ends");
		BBBPerformanceMonitor.end(
				"CheckListTools", "getSkuComputedAttributeItem");
		return null;
	}
	
	/**
	 * Checks the SKU lies in the given C2/C3.
	 *
	 * @param skuCategoriesListMap the sku categories list map
	 * @param skuId the sku id
	 * @param categoryId the category id
	 * @return true, if is sku in category
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public boolean isSkuInCategory(Map<String, List<String>> skuCategoriesListMap, String skuId, String categoryId) throws BBBSystemException, BBBBusinessException{
		logDebug("Inside method isSkuInCategory of CheckListTools");
		Boolean result = false;
		/*Checking for a map which contains SKUs and corresponding categories list prepared for a list of registry items.*/
		if(skuCategoriesListMap!=null){
			List<String> categoriesForSku = skuCategoriesListMap.get(skuId);
			if(categoriesForSku!=null && !categoriesForSku.isEmpty()){ 
				result = categoriesForSku.contains(categoryId);
			}
		}
		if(result){
			logDebug("skuId " + skuId + " is tagged to categoryId " + categoryId);
		} else {
			logDebug("skuId " + skuId + " is not tagged to categoryId " + categoryId);
		}
		return result;
	}
	
	/**
	 * Gets the sku categories list map.
	 *
	 * @param regItems the reg items
	 * @return the sku categories list map
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public Map<String, List<String>> getSkuCategoriesListMap(List<RegistryItemVO> regItems) throws BBBSystemException, BBBBusinessException {
		logDebug("Inside method getSkuCategoriesListMap of CheckListTools");
		Map<String, List<String>> skuCatListMap = new HashMap<String, List<String>>();
		String skuId = null;
		RepositoryItem skuItem = null;
		List<String> catList = null;
		for(RegistryItemVO regItem : regItems) {
			if(BBBUtility.isNotEmpty(String.valueOf(regItem.getSku()))) {
				skuId = String.valueOf(regItem.getSku());
				skuItem = getSkuComputedAttributeItem(skuId);
				catList = getCategoryListForSku(skuItem);
				if(catList != null && !catList.isEmpty()) {
					skuCatListMap.put(skuId, catList);
				}
			}
		}
		logDebug("SKU - CategoriesList Map for Registry Items " + regItems + " is : " + skuCatListMap.toString());
		return skuCatListMap;
	}
	
	public void setGiftGiverEPHBuckets(List<RegistryItemVO> regItems,
			List<MyItemCategoryVO> ePHBuckets) throws BBBSystemException, BBBBusinessException {
		
		logDebug("Entering setGiftGiverEPHBuckets method");
		Integer c1registryItemsCount;
		List<MyItemCategoryVO> c2CategoriesList;
		List<MyItemCategoryVO> c3CategoriesList;
		MyItemCategoryVO c2CategoryVO;
		List<RegistryItemVO> allRegItems;
		Map<String, List<String>> skuCategoriesListMap;
		List<RegistryItemVO> c2RegItems;
		List<RegistryItemVO> otherC2RegItems;
		
		skuCategoriesListMap = getSkuCategoriesListMap(regItems);
		
		logDebug("Setting Gift Giver EPH Category Buckets for categories : " + ePHBuckets + " for registry items : " + regItems);
		for(MyItemCategoryVO c1Cat : ePHBuckets) {
			
			/*For setting the count of no. of items inside the C1*/
			c1registryItemsCount = 0;
			
			allRegItems = new ArrayList<RegistryItemVO>();
			
			/*For displaying All items for a C1*/ 
			
			c2CategoriesList = c1Cat.getChildCategoryVO();
			if(c2CategoriesList!=null && !c2CategoriesList.isEmpty()){
				ListIterator<MyItemCategoryVO> c2CategoryListIter = c2CategoriesList.listIterator();
				boolean isSKUFound=false;
				/*Iterating over C2 categories*/
				
				while(c2CategoryListIter.hasNext()){
					c2CategoryVO = c2CategoryListIter.next();
					c2RegItems = new ArrayList<RegistryItemVO>();
					c2CategoryVO.setRegistryItems(c2RegItems);
					c3CategoriesList = c2CategoryVO.getChildCategoryVO();
					if(c3CategoriesList!=null && !c3CategoriesList.isEmpty()) {
						ListIterator<MyItemCategoryVO> c3CategoriesListIter = c3CategoriesList.listIterator();
						/*Iterating over C3 categories*/
						c1registryItemsCount = matchRegistryItemsInC3(
								regItems, c3CategoriesList, allRegItems, c1registryItemsCount,
								c3CategoriesListIter, skuCategoriesListMap,isSKUFound);
							c1registryItemsCount +=matchRegistryItemsInC2(
									regItems, c2RegItems,
									c1registryItemsCount,
									c2CategoryVO.getCategoryId(), skuCategoriesListMap,isSKUFound);
					} else {
						
						/*Setting the registry Count and matching items with the C2*/
						c1registryItemsCount = matchRegistryItems(
								regItems, null, allRegItems, c1registryItemsCount,
								c2CategoryVO.getCategoryId(), skuCategoriesListMap, isSKUFound);
					
					}
				}
			}
			/*Iterating over C3 categories*/
			c1Cat.setRegistryItems(allRegItems);
			c1Cat.setRegistryItemsCount(c1registryItemsCount);
		
		}
		MyItemCategoryVO categoryVO = new MyItemCategoryVO();
		categoryVO.setDisplayName(BBBGiftRegistryConstants.OTHER_CATEGORY);
		categoryVO.setCategoryId(BBBGiftRegistryConstants.OTHER_CATEGORY);
		categoryVO.setRegistryItems(regItems);
		categoryVO.setRegistryItemsCount(regItems.size());
		ePHBuckets.add(categoryVO);
		logDebug("Exiting setGiftGiverEPHBuckets method");
	}
	
	/**
	 * Sets the eph category buckets.
	 *
	 * @param listRegistryItemVO the list registry item vo
	 * @param ePHCategoryBuckets the e ph category buckets
	 * @param c1CategoriesOnRLP the c1 categories on rlp
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public void setEPHCategoryBuckets(List<RegistryItemVO> listRegistryItemVO,
			List<MyItemCategoryVO> c1CategoriesOnRLP) throws BBBSystemException,
			BBBBusinessException {
		
		BBBPerformanceMonitor.start(BBBPerformanceConstants.MY_ITEMS_CHECKLIST);
		logDebug("Entering setEPHCategoryBuckets method");
		List<RegistryItemVO> c2RegItems;
		List<RegistryItemVO> otherC2RegItems;
		List<MyItemCategoryVO> c2CategoriesList;
		List<MyItemCategoryVO> c3CategoriesList;
		MyItemCategoryVO c2CategoryVO;
		MyItemCategoryVO otherC2CategoryVO;
		Integer c2RegistryItemsCount;
		Map<String, List<String>> skuCategoriesListMap;
		
		skuCategoriesListMap = getSkuCategoriesListMap(listRegistryItemVO);
		
		logDebug("Setting EPH Category Buckets for categories : " + c1CategoriesOnRLP + " for registry items : " + listRegistryItemVO);
		int otherC2Categorycount;
		/*Iterating over C1 categories*/ 
		for(MyItemCategoryVO c1CatOnRLP : c1CategoriesOnRLP) {
			
			/*For displaying All items for a C1*/ 
			otherC2RegItems = new ArrayList<RegistryItemVO>();
			otherC2CategoryVO = new MyItemCategoryVO();
			otherC2CategoryVO.setCategoryId(BBBGiftRegistryConstants.OTHER_C2);
			otherC2CategoryVO.setDisplayName(BBBGiftRegistryConstants.OTHER_C2);
			otherC2CategoryVO.setRegistryItems(otherC2RegItems);
			otherC2Categorycount=0;
			c2CategoriesList = c1CatOnRLP.getChildCategoryVO();
			boolean isSKUFound=false;
			if(c2CategoriesList!=null && !c2CategoriesList.isEmpty()){
				ListIterator<MyItemCategoryVO> c2CategoriesListIter = c2CategoriesList.listIterator();
				//Iterating over C2 categories
				while(c2CategoriesListIter.hasNext()){
				//	For setting the count of no. of items inside the C2
					c2RegistryItemsCount = 0;
					
					c2CategoryVO = c2CategoriesListIter.next();
					c2RegItems = new ArrayList<RegistryItemVO>();
					c2CategoryVO.setRegistryItems(c2RegItems);
					c3CategoriesList = c2CategoryVO.getChildCategoryVO();
					if(c3CategoriesList!=null && !c3CategoriesList.isEmpty()) {
						ListIterator<MyItemCategoryVO> c3CategoriesListIter = c3CategoriesList.listIterator();
						
						//Iterating over C3 categories
						c2RegistryItemsCount = matchRegistryItemsInC3(
								listRegistryItemVO, c3CategoriesList,
								null, c2RegistryItemsCount,
								c3CategoriesListIter, skuCategoriesListMap,isSKUFound);
						if(!isSKUFound){
						c2RegistryItemsCount +=matchRegistryItemsInC2(
								listRegistryItemVO, c2RegItems,
								c2RegistryItemsCount,
								c2CategoryVO.getCategoryId(), skuCategoriesListMap,isSKUFound);
						}
					} else {
						
						//Setting the registry Count and matching items with the C2
						c2RegistryItemsCount = matchRegistryItems(
								listRegistryItemVO, c2RegItems,
								null, c2RegistryItemsCount,
								c2CategoryVO.getCategoryId(), skuCategoriesListMap,isSKUFound);
					
					}
					//Iterating over C3 categories
					if(!isSKUFound){
						otherC2Categorycount += matchRegistryItemsInC1(
							listRegistryItemVO, 
							otherC2RegItems, otherC2Categorycount,
							c1CatOnRLP.getCategoryId(), skuCategoriesListMap,isSKUFound);
					}
					c2CategoryVO.setRegistryItemsCount(c2RegistryItemsCount);
				}
				}
			if(c2CategoriesList!=null && !c2CategoriesList.isEmpty()){
			c2CategoriesList.add(otherC2CategoryVO);
			}
		}
		
		// For displaying the items not belonging to any EPH category in the 'other' category
		MyItemCategoryVO categoryVO = new MyItemCategoryVO();
		categoryVO.setDisplayName(BBBGiftRegistryConstants.OTHER_CATEGORY);
		categoryVO.setCategoryId(BBBGiftRegistryConstants.OTHER_CATEGORY);
		categoryVO.setRegistryItems(listRegistryItemVO);
		categoryVO.setRegistryItemsCount(listRegistryItemVO.size());
		c1CategoriesOnRLP.add(categoryVO);
		
		for(MyItemCategoryVO c1CatOnRLP : c1CategoriesOnRLP) {
			int c1count=0;
			c2CategoriesList = c1CatOnRLP.getChildCategoryVO();
			if(c2CategoriesList!=null && !c2CategoriesList.isEmpty()){
				ListIterator<MyItemCategoryVO> c2CategoriesListIter = c2CategoriesList.listIterator();
				
				//Iterating over C2 categories
				while(c2CategoriesListIter.hasNext()){
					c2CategoryVO = c2CategoriesListIter.next();
					c3CategoriesList = c2CategoryVO.getChildCategoryVO();
					//For setting the count of no. of items inside the C2
					if(c3CategoriesList!=null && !c3CategoriesList.isEmpty()) {
						ListIterator<MyItemCategoryVO> c3CategoriesListIter=c3CategoriesList.listIterator();
						while(c3CategoriesListIter.hasNext()) {
							MyItemCategoryVO c3CategoryVO = c3CategoriesListIter.next();
							c3CategoryVO.setRegistryItemsCount(c3CategoryVO.getRegistryItems().size());
							if(c3CategoryVO !=null && c3CategoryVO.getRegistryItems()!=null && c3CategoryVO.getRegistryItems().size() >0){
							c1count+=c3CategoryVO.getRegistryItems().size();
						    }
						}
					} 
					c2CategoryVO.setRegistryItemsCount(c2CategoryVO.getRegistryItems().size());
					if(c2CategoryVO !=null && c2CategoryVO.getRegistryItems()!=null && c2CategoryVO.getRegistryItems().size() >0){
						c1count+=c2CategoryVO.getRegistryItems().size();
					    }
					}
				}
			
			if(BBBGiftRegistryConstants.OTHER_CATEGORY.equalsIgnoreCase(c1CatOnRLP.getCategoryId())){
				c1count=c1CatOnRLP.getRegistryItems().size();
			}
			c1CatOnRLP.setRegistryItemsCount(c1count);
		}	
		
		logDebug("Exiting setEPHCategoryBuckets method");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.MY_ITEMS_CHECKLIST);
	}

	private Integer matchRegistryItemsInC1(
			List<RegistryItemVO> listRegistryItemVO,
			List<RegistryItemVO> otherC2RegItems, Integer c2RegistryItemsCount,
			String categoryId, Map<String, List<String>> skuCategoriesListMap, boolean isSKUFound) throws BBBSystemException, BBBBusinessException {
		
		/*For Matching registry items with the given category*/ 
		
		logDebug("Matching registry items to category Id : " + categoryId);
		String skuId;
		RegistryItemVO regItem;
		ListIterator<RegistryItemVO> it = listRegistryItemVO.listIterator();
		while(it.hasNext()){
			regItem = it.next();
			skuId = String.valueOf(regItem.getSku());
			if(isSkuInCategory(skuCategoriesListMap, skuId, categoryId)){
				if(otherC2RegItems != null) {
					otherC2RegItems.add(regItem);	
				}
				c2RegistryItemsCount++;
				isSKUFound=true;
				/*Removing item from registry item list as an item will only match one of the categories*/
				it.remove();
			}
		}
		return c2RegistryItemsCount;
	}

	/**
	 * Match registry items in c2.
	 *
	 * @param listRegistryItemVO the list registry item vo
	 * @param c2RegItems the c2 reg items
	 * @param c2RegistryItemsCount the c2 registry items count
	 * @param c2CategoryId the c2 category id
	 * @param skuCategoriesListMap the sku categories list map
	 * @param isSKUFound 
	 * @return the integer
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	private Integer matchRegistryItemsInC2(
			List<RegistryItemVO> listRegistryItemVO,
			List<RegistryItemVO> c2RegItems, Integer c2RegistryItemsCount,
			String c2CategoryId,
			Map<String, List<String>> skuCategoriesListMap, boolean isSKUFound) throws BBBSystemException, BBBBusinessException {
		
		/*For Matching registry items with the given category*/ 
		
		logDebug("Matching registry items to category Id : " + c2CategoryId);
		String skuId;
		RegistryItemVO regItem;
		ListIterator<RegistryItemVO> it = listRegistryItemVO.listIterator();
		while(it.hasNext()){
			regItem = it.next();
			skuId = String.valueOf(regItem.getSku());
			if(isSkuInCategory(skuCategoriesListMap, skuId, c2CategoryId)){
				if(c2RegItems != null) {
					c2RegItems.add(regItem);	
				}
				c2RegistryItemsCount++;
				isSKUFound=true;
				/*Removing item from registry item list as an item will only match one of the categories*/
				it.remove();
			}
		}
		return c2RegistryItemsCount;
	}

	/**
	 * Matches the Registry Items SKUs 
	 * with C3 category Id and returns increased count in 
	 * case match if found.
	 *
	 * @param listRegistryItemVO the list registry item vo
	 * @param c3CategoriesList the c3 categories map
	 * @param allC2RegItems the all c2 reg items
	 * @param c2RegistryItemsCount the c2 registry items count
	 * @param c3CategoryLisIter the c3 category ids
	 * @param skuCategoriesListMap the sku categories list map
	 * @param isSKUFound 
	 * @return the integer
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public Integer matchRegistryItemsInC3(
			List<RegistryItemVO> listRegistryItemVO,
			List<MyItemCategoryVO> c3CategoriesList,
			List<RegistryItemVO> allC2RegItems,
			Integer c2RegistryItemsCount, ListIterator<MyItemCategoryVO> c3CategoryListIter, Map<String, List<String>> skuCategoriesListMap, boolean isSKUFound)
			throws BBBSystemException, BBBBusinessException {
		List<RegistryItemVO> c3RegItems;
		MyItemCategoryVO c3CategoryVO;
		while(c3CategoryListIter.hasNext()) {
			c3CategoryVO = c3CategoryListIter.next();
			c3RegItems = new ArrayList<RegistryItemVO>();
			c3CategoryVO.setRegistryItems(c3RegItems);
			
			/*Setting the registry Count and matching items with the C3*/
			c2RegistryItemsCount = matchRegistryItems(
					listRegistryItemVO, c3RegItems,
					allC2RegItems, c2RegistryItemsCount,
					c3CategoryVO.getCategoryId(), skuCategoriesListMap,isSKUFound);
		
		}
		return c2RegistryItemsCount;
	}
	
	
	

	/**
	 * Matches registry items SKUs with the passed category id
	 * and returns the increased count if match is found.
	 *
	 * @param listRegistryItemVO the list registry item vo
	 * @param c2RegItems the c2 reg items
	 * @param allC2RegItems the all c2 reg items
	 * @param registryItemsCount the registry items count
	 * @param isSKUFound 
	 * @param c2CategoryId the c2 category id
	 * @return the integer
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public Integer matchRegistryItems(List<RegistryItemVO> listRegistryItemVO,
			List<RegistryItemVO> regItems,
			List<RegistryItemVO> allRegItems, Integer registryItemsCount,
			String categoryId, Map<String, List<String>> skuCategoriesListMap, boolean isSKUFound) throws BBBSystemException,
			BBBBusinessException {
		
		/*For Matching registry items with the given category*/ 
		
		logDebug("Matching registry items to category Id : " + categoryId);
		String skuId;
		RegistryItemVO regItem;
		ListIterator<RegistryItemVO> it = listRegistryItemVO.listIterator();
		while(it.hasNext()){
			regItem = it.next();
			skuId = String.valueOf(regItem.getSku());
			if(isSkuInCategory(skuCategoriesListMap, skuId, categoryId)){
				if(regItems != null) {
					regItems.add(regItem);	
				}
				/*if(allRegItems != null){
					allRegItems.add(regItem);
				}*/
				registryItemsCount++;
				isSKUFound=true;
				
				/*Removing item from registry item list as an item will only match one of the categories*/
				it.remove();
			}
		}
		return registryItemsCount;
	}
	
	/**
	 * This method is used to fetch the package count for a sku from catalog repository
	 * 
	 * @param skuId
	 *            the skuId 
	 * @return int
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	public int getPackageCountForSku(final RepositoryItem catalogCategoryItem) throws BBBSystemException, BBBBusinessException{
		BBBPerformanceMonitor.start(
				"CheckListTools", "getPackageCountForSku");
		this.logDebug("CheckListTools.getPackageCountForSku() method starts");
		int packageCount = 0;
		if (catalogCategoryItem != null) {			
				packageCount = (int)catalogCategoryItem.getPropertyValue(BBBCoreConstants.PACKAGE_COUNT);
				if(packageCount == 0){
					packageCount = 1;
				} 
		}		
		this.logDebug("CheckListTools.getPackageCountForSku() method ends");
		BBBPerformanceMonitor.end(
				"CheckListTools", "getPackageCountForSku");
		return packageCount;
	}
	
	/**
	 * This method is used to fetch the categories to which a sku can belong
	 * 
	 * @param skuId
	 *            the skuId 
	 * @return int
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	public List<String> getCategoryListForSku(final RepositoryItem catalogCategoryItem) throws BBBSystemException, BBBBusinessException{
		BBBPerformanceMonitor.start(
				"CheckListTools", "getCategoryListForSku");
		this.logDebug("CheckListTools.getCategoryListForSku() method starts");
		String categoryListForSku = null;
		List<String> skuCatList = null;
		if (null != catalogCategoryItem) {		
				this.logDebug("CheckListTools.getCategoryListForSku() catalogCategoryItem is not null");
				categoryListForSku = (String)catalogCategoryItem.getPropertyValue(BBBCoreConstants.CATEGORIES_LIST);
				if(!BBBUtility.isEmpty(categoryListForSku)){
					return Arrays.asList(categoryListForSku.split(BBBCoreConstants.COMMA));
				}
			}		
		this.logDebug("CheckListTools.getCategoryListForSku() method ends");
		BBBPerformanceMonitor.end(
				"CheckListTools", "getCategoryListForSku");
		return skuCatList;
	}
	
	/**
	 * This method is used to populate addedQuantity and autoCehck or manualCheck flags for leaf nodes. 
	 * 
	 * @param registryId
	 *            the registryId 
	 * @param registryItems
	 *            the registry items 
	 * @param categoryVO
	 *            the CategoryVO
	 * @param checkListVO
	 *            the CheckListVO
	 * @return int
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	public CategoryVO updateCheckListCompletionStatus(List<ChecklistSKUMapping> checklistSKUList,final CategoryVO categoryVO,List<String> manuallySelectedCategoryList) throws BBBSystemException, BBBBusinessException{
		BBBPerformanceMonitor.start(
				"CheckListTools", "updateCheckListCompletionStatus");
		this.logDebug("CheckListTools.populateC2C3CheckListCompletionFlags() method starts");
		int regItemQuantity = 0;
		if(null != checklistSKUList && !BBBUtility.isListEmpty(checklistSKUList)){
			for(ChecklistSKUMapping checklistSKU:checklistSKUList){
				if(!BBBUtility.isListEmpty(checklistSKU.getListCatId())){
					this.logDebug("The sku does not belong to Other category.");					
					if(checklistSKU.getListCatId().contains(categoryVO.getCategoryId())){
						 regItemQuantity =regItemQuantity + checklistSKU.getPackageCount() * checklistSKU.getQuantityRequested();
						this.logDebug("Current category is contained in category list of sku.");						
						if((categoryVO.getSuggestedQuantity() - regItemQuantity) <= 0){							
							categoryVO.setC3AutoComplete(true);	
						}
						else{
							if(!BBBUtility.isListEmpty(manuallySelectedCategoryList)){
								this.logDebug("Manually checked categories are present for this registry Id.");	
								if(manuallySelectedCategoryList.contains(categoryVO.getCategoryId())){
									this.logDebug("Current category is contained in manually checked categories.");	
									categoryVO.setC3ManualComplete(true);
								}
							}
						}	
						categoryVO.setAddedQuantity(regItemQuantity);
					}
					
				}
			}
		}
		if(categoryVO.getAddedQuantity() == 0){
			if(!BBBUtility.isListEmpty(manuallySelectedCategoryList)){
				if(manuallySelectedCategoryList.contains(categoryVO.getCategoryId())){
					categoryVO.setC3ManualComplete(true);
				}
			}
		}
		this.logDebug("CheckListTools.populateC2C3CheckListCompletionFlags() method ends");
		BBBPerformanceMonitor.end(
				"CheckListTools", "updateCheckListCompletionStatus");
	return categoryVO;
	}
	
	
	

	/**
	 * This method is used to add manually checked category to the list of categories in user profile. 
	 * 
	 * @param registryId
	 *            the registryId 
	 * @param categoryId
	 *            the categoryId  
	 * @param registryType
	 *            the registryType
	 * @return boolean
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	@SuppressWarnings("finally")
	public boolean addCategoryToUserProfile(final String categoryId,final String registryId,final String registryType) throws BBBSystemException, BBBBusinessException{
		BBBPerformanceMonitor.start(
				"CheckListTools", "addCategoryToUserProfile");
		Transaction transaction = null;
		boolean isException = false;
		this.logDebug("CheckListTools.addCategoryToUserProfile() method starts");
		RepositoryItem[] checkListItems = this.fetchCheckListRepositoryItem(registryType);
		if(null != checkListItems){
			String checkListId = (String)checkListItems[0].getRepositoryId();
			final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
			final Profile bbbProfile = (Profile)request.resolveName("/atg/userprofiling/Profile");
			MutableRepositoryItem[] manualSelectedItem = this.manuallySelectedItem(bbbProfile.getRepositoryId(),registryId,checkListId);
			if(null != manualSelectedItem){
				String userCompletedCatList = (String)manualSelectedItem[0].getPropertyValue(BBBCoreConstants.USER_COMPLETED_CATEGORY_LIST);
				if(!BBBUtility.isEmpty(userCompletedCatList)){
					String[] arrayManuallySelectedC3 = userCompletedCatList.split(BBBCoreConstants.COMMA);
						 this.logDebug("CheckListTools.addCategoryToUserProfile() adding the category to repository: " + categoryId);
						 StringBuilder catStringAppend = new StringBuilder(userCompletedCatList);
						 catStringAppend.append(BBBCoreConstants.COMMA + categoryId);
						 manualSelectedItem[0].setPropertyValue(BBBCoreConstants.USER_COMPLETED_CATEGORY_LIST,catStringAppend.toString());						
			}
				else{
					 manualSelectedItem[0].setPropertyValue(BBBCoreConstants.USER_COMPLETED_CATEGORY_LIST,categoryId);
				}
				 try {
					 transaction = ensureTransaction();	
					 this.getProfileRepository().updateItem(manualSelectedItem[0]);
						this.logDebug("CheckListTools.addCategoryToUserProfile() method ends");
					} catch (RepositoryException e) {
						isException = true;
						BBBPerformanceMonitor.cancel(
								"CheckListTools", "addCategoryToUserProfile");
						logError(LogMessageFormatter.formatMessage(null,
								"RepositoryException from service of CheckListTools.addCategoryToUserProfile()"), e);
				 }
				 finally{
						invokeEndTransaction(transaction, isException);
						return true;
					}
				
			}
			else{
				try {
					transaction = ensureTransaction();	
					MutableRepositoryItem manualItem = (MutableRepositoryItem) this.getProfileRepository().createItem(BBBCoreConstants.USER_MANUAL_CHECKED_CATEGORIES);
					manualItem.setPropertyValue(BBBCoreConstants.USER_COMPLETED_CATEGORY_LIST,categoryId);
					manualItem.setPropertyValue(BBBCoreConstants.ID,bbbProfile.getRepositoryId());
					manualItem.setPropertyValue(BBBCoreConstants.REGISTRY_ID,registryId);
					manualItem.setPropertyValue(BBBCoreConstants.CHECKLIST_ID,checkListId);
					this.getProfileRepository().addItem(manualItem);
					this.logDebug("CheckListTools.addCategoryToUserProfile() method ends");
				} catch (RepositoryException e) {
					isException = true;
					BBBPerformanceMonitor.cancel(
							"CheckListTools", "addCategoryToUserProfile");
					logError(LogMessageFormatter.formatMessage(null,
							"RepositoryException from service of CheckListTools.addCategoryToUserProfile()"), e);
				}
				finally{
					invokeEndTransaction(transaction, isException);
					return true;
				}
			}
		}
		BBBPerformanceMonitor.end(
				"CheckListTools", "addCategoryToUserProfile");
		return false;
		}

	/**
	 * @param transaction
	 * @param isException
	 * @throws BBBSystemException
	 */
	protected void invokeEndTransaction(Transaction transaction, boolean isException) throws BBBSystemException {
		endTransaction(isException, transaction);
		if(isException){
			throw new BBBSystemException(BBBCoreErrorConstants.CHECKLIST_ERROR_10132,"Error occured on maual check of checklist item");
		}
	}
	
	/**
	 * This method is used to remove manually the un-checked category from the list of categories in user profile. 
	 * 
	 * @param registryId
	 *            the registryId 
	 * @param categoryId
	 *            the categoryId  
	 * @param registryType
	 *            the registryType
	 * @return boolean
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	@SuppressWarnings("finally")
	public boolean removeCategoryFromUserProfile(final String categoryId,final String registryId,final String registryType) throws BBBSystemException, BBBBusinessException{
		BBBPerformanceMonitor.start(
				"CheckListTools", "removeCategoryFromUserProfile");
		this.logDebug("CheckListTools.removeCategoryFromUserProfile() method starts");
		RepositoryItem[] checkListItems = this.fetchCheckListRepositoryItem(registryType);
		Transaction transaction = null;
		boolean isException = false;
		boolean modifiedFlag = false;
		if(null != checkListItems){
			String checkListId = (String)checkListItems[0].getRepositoryId();
			final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
			final Profile bbbProfile = (Profile)request.resolveName("/atg/userprofiling/Profile");
			MutableRepositoryItem[] manualSelectedItem = this.manuallySelectedItem(bbbProfile.getRepositoryId(),registryId,checkListId);
			if(null != manualSelectedItem){
				String userCompletedCatList = (String)manualSelectedItem[0].getPropertyValue(BBBCoreConstants.USER_COMPLETED_CATEGORY_LIST);
				if(!BBBUtility.isEmpty(userCompletedCatList)){
					String[] arrayManuallySelectedC3 = userCompletedCatList.split(BBBCoreConstants.COMMA);
					 if(Arrays.asList(arrayManuallySelectedC3).contains(categoryId)){
						 this.logDebug("CheckListTools.removeCategoryFromUserProfile() removing the category to repository: " + categoryId);
						 String updatedCategoriesPostRemoval = null;
						 if(userCompletedCatList.contains(categoryId + BBBCoreConstants.COMMA)){
							  updatedCategoriesPostRemoval = userCompletedCatList.replaceFirst(categoryId + BBBCoreConstants.COMMA, "");
							  modifiedFlag = true;
						 }
						 else if(userCompletedCatList.contains(BBBCoreConstants.COMMA + categoryId)){
							 updatedCategoriesPostRemoval = userCompletedCatList.replaceFirst(BBBCoreConstants.COMMA + categoryId, "");
							 modifiedFlag = true;
						 }
						 else if(userCompletedCatList.contains(categoryId)){
							 updatedCategoriesPostRemoval = userCompletedCatList.replaceFirst(categoryId, "");
							 modifiedFlag = true;
						 }
						 manualSelectedItem[0].setPropertyValue(BBBCoreConstants.USER_COMPLETED_CATEGORY_LIST,updatedCategoriesPostRemoval);
						 try {
							 transaction = ensureTransaction();	
							this.getProfileRepository().updateItem(manualSelectedItem[0]);
							this.logDebug("CheckListTools.removeCategoryFromUserProfile() method ends");							
						} catch (RepositoryException e) {
							isException = true;
							BBBPerformanceMonitor.cancel(
									"CheckListTools", "removeCategoryFromUserProfile");
							logError(LogMessageFormatter.formatMessage(null,
									"RepositoryException from service of CheckListTools.removeCategoryFromUserProfile()"), e);
						}
						 finally{
								invokeEndTransaction(transaction, isException);
								return true;
							}
					 }
				}
			}
			}
		BBBPerformanceMonitor.end(
				"CheckListTools", "removeCategoryFromUserProfile");
		return false;
		}
	
	/**
	   * This method ensures that a transaction exists before returning.
	   * If there is no transaction, a new one is started and returned.  In
	   * this case, you must call commitTransaction when the transaction
	   * completes.
	   * @return a <code>Transaction</code> value
	   */
	  protected Transaction ensureTransaction(){
		  try {
			  TransactionManager transactionManager = getTransactionManager();	      
			  Transaction transaction = transactionManager.getTransaction();
			  if (transaction == null) {
				  transactionManager.begin();
				  transaction = transactionManager.getTransaction();
				  return transaction;
			  }
			  return null;
		  }catch (NotSupportedException e) {
			  logError(e);
		  }catch (SystemException e) {
			  logError(e);
		  }
		  return null;
	  }
	
	  /**
		  * method to check if error occur during the transaction if yes then execute 
		  * transection rollback otherwise commit the transaction
		  * 
		  * @param isError flag that represent error
		  * @param pTransaction transaction object
		 */
		private void endTransaction(boolean isError,Transaction pTransaction){
			  try {
				  if(isError){
					  if(pTransaction!=null){
						  pTransaction.rollback();
					  }
				  }else{
					  if(pTransaction!=null){				
						  pTransaction.commit();					
					  }
				  }
			  }catch (SecurityException e) {
				  logError(e);
			  } catch (IllegalStateException e) {
				  logError(e);
			  } catch (RollbackException e) {
				  logError(e);
			  } catch (HeuristicMixedException e) {
				  logError(e);
			  } catch (HeuristicRollbackException e) {
				  logError(e);
			  } catch (SystemException e) {
				  logError(e);
			  }
		  }
	
	/**
	 * This method is used to execute RQL query.
	 * 
	 * @param rqlQuery
	 *            the rql query
	 * @param params
	 *            the params
	 * @param viewName
	 *            the view name
	 * @param repository
	 *            the repository
	 * @return the repository item[]
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	public RepositoryItem[] executeRQLQuery(final String rqlQuery,
			final Object[] params, final String viewName,
			final MutableRepository repository) throws BBBSystemException,
			BBBBusinessException {

		RqlStatement statement = null;
		RepositoryItem[] queryResult = null;
		this.logDebug("GiftRegistryTools.executeRQLQuery() method start");

		if (rqlQuery != null) {
			if (repository != null) {
				try {
					statement = RqlStatement.parseRqlStatement(rqlQuery);
					final RepositoryView view = repository.getView(viewName);
					if (view == null) {
						this.logError(BBBCoreErrorConstants.CHECKLIST_ERROR_10132
								+ viewName
								+ " view is null from executeRQLQuery of CheckListTools");
					}
					queryResult = statement.executeQuery(view, params);

					if (queryResult == null) {
						this.logDebug("No results returned for query ["
								+ rqlQuery + "]");
					}

				} catch (final RepositoryException e) {
					this.logError(
							BBBCoreErrorConstants.CHECKLIST_ERROR_10133
									+ " Repository Exception [Unable to retrieve data] from executeRQLQuery of CheckListTools",
							e);
					throw new BBBSystemException(
							BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
							BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
							e);
				}
			} else {
				this.logError(BBBCoreErrorConstants.CHECKLIST_ERROR_10134
						+ " Repository is null from executeRQLQuery of CheckListTools");
			}
		} else {
			this.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10135
					+ " Query String is null from executeRQLQuery of CheckListTools");
		}

		this.logDebug("CheckListTools.executeRQLQuery() method ends");
		return queryResult;
	}
	//method to populate static checklist
	private Repository registryCheckListRepository;
	
	public Repository getRegistryCheckListRepository() {
		return registryCheckListRepository;
	}

	public void setRegistryCheckListRepository(
			Repository registryCheckListRepository) {
		this.registryCheckListRepository = registryCheckListRepository;
	}

	/**
	 * This method populates the static CheckListVO for registryType to load the same in cache on server startup.
	 * 
	 * @param registryType
	 *            the registryType
	 * @return CheckListVO
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	public CheckListVO populateStaticCheckListVO(final String registryType, boolean fromUtility) throws BBBSystemException, BBBBusinessException{
		BBBPerformanceMonitor.start(
				"CheckListTools", "populateStaticCheckListVO");
		this.logDebug("CheckListTools.populateStaticCheckListVO() method start");
		this.logDebug("Caching for registryType: " + registryType);
		RepositoryItem[] checkListItems = this.fetchCheckListRepositoryItem(registryType);
		CheckListVO checkListVO = new CheckListVO();
		if(null != checkListItems && checkListItems.length != 0){
			checkListVO.setChecklistId((String) checkListItems[0].getRepositoryId());
			checkListVO.setChecklistTypeName((String) checkListItems[0].getPropertyValue(BBBCoreConstants.SUBTYPE_CODE));
			checkListVO.setDisplayName((String) checkListItems[0].getPropertyValue(BBBCoreConstants.DISPLAY_NAME));
			checkListVO.setCheckListDisabled((boolean) checkListItems[0].getPropertyValue(BBBCoreConstants.IS_DISABLED));
			checkListVO.setSiteFlag((String) checkListItems[0].getPropertyValue(BBBCoreConstants.SITE_FLAG));
			List<CategoryVO> c1CategoryList = new ArrayList<CategoryVO>();
			if(null != checkListItems[0].getPropertyValue(BBBCoreConstants.CHECK_LIST_CATEGORIES)){
				@SuppressWarnings("unchecked")
				List<RepositoryItem> checkListCategories = (List<RepositoryItem>)checkListItems[0].getPropertyValue(BBBCoreConstants.CHECK_LIST_CATEGORIES);
				if(!checkListCategories.isEmpty()){		
					for(RepositoryItem checkListItem : checkListCategories){
						//Populating C1's
						try {
							MutableRepositoryItem c1CheckListItem = (MutableRepositoryItem) getRegistryCheckListRepository().getItem((String) checkListItem.getRepositoryId(),BBBCoreConstants.CHECKLIST_CATEGORY_ITEM_DESCRIPTOR);
							if(null != c1CheckListItem){
								if(fromUtility || (null != c1CheckListItem && null != c1CheckListItem.getPropertyValue(BBBCoreConstants.SHOW_ON_CHECKLIST) && (boolean)c1CheckListItem.getPropertyValue(BBBCoreConstants.SHOW_ON_CHECKLIST) && null != c1CheckListItem.getPropertyValue(BBBCoreConstants.IS_DISABLED) && !((boolean)c1CheckListItem.getPropertyValue(BBBCoreConstants.IS_DISABLED)) && null != c1CheckListItem.getPropertyValue(BBBCoreConstants.IS_DELETED) && !((boolean)c1CheckListItem.getPropertyValue(BBBCoreConstants.IS_DELETED)))){
									if(null != c1CheckListItem.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE) && (!(boolean)c1CheckListItem.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE) && isStagingServer() || (boolean)c1CheckListItem.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE))){
								CategoryVO c1CategoryVO = populateStaticCategoryVO(c1CheckListItem,null,null,checkListVO);
								//Populating C2's
								List<CategoryVO> c2CategoryList = new ArrayList<CategoryVO>();
								@SuppressWarnings("unchecked")
								List<RepositoryItem> c2CheckListItems = (List<RepositoryItem>)c1CheckListItem.getPropertyValue(BBBCoreConstants.CHILD_CHECKLIST_CATEGORY_ID);
								if(!BBBUtility.isListEmpty(c2CheckListItems)){									
									for(RepositoryItem c2CheckListItem : c2CheckListItems){
										MutableRepositoryItem checkListItem2 = (MutableRepositoryItem) getRegistryCheckListRepository().getItem((String) c2CheckListItem.getRepositoryId(),BBBCoreConstants.CHECKLIST_CATEGORY_ITEM_DESCRIPTOR);
										if(fromUtility || (null != checkListItem2.getPropertyValue(BBBCoreConstants.IS_DISABLED) && !(boolean)checkListItem2.getPropertyValue(BBBCoreConstants.IS_DISABLED) && null != checkListItem2.getPropertyValue(BBBCoreConstants.IS_DELETED) && !(boolean)checkListItem2.getPropertyValue(BBBCoreConstants.IS_DELETED))){
											if(null != checkListItem2.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE) && (!(boolean)checkListItem2.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE) && isStagingServer() || (boolean)checkListItem2.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE))){
										CategoryVO c2CategoryVO = populateStaticCategoryVO(checkListItem2,null,c1CategoryVO.getDisplayName(),checkListVO);
										@SuppressWarnings("unchecked")
										List<RepositoryItem> c3CheckListItems = (List<RepositoryItem>)checkListItem2.getPropertyValue(BBBCoreConstants.CHILD_CHECKLIST_CATEGORY_ID);
										if(!BBBUtility.isListEmpty(c3CheckListItems)){
											//Populating C3's
											List<CategoryVO> c3CategoryList = new ArrayList<CategoryVO>();
											for(RepositoryItem c3CheckListItem : c3CheckListItems){												
												MutableRepositoryItem checkListItem3 = (MutableRepositoryItem) getRegistryCheckListRepository().getItem((String) c3CheckListItem.getRepositoryId(),BBBCoreConstants.CHECKLIST_CATEGORY_ITEM_DESCRIPTOR);
												if(fromUtility || (null != checkListItem3.getPropertyValue(BBBCoreConstants.IS_DISABLED) && !(boolean)checkListItem3.getPropertyValue(BBBCoreConstants.IS_DISABLED) && null != checkListItem3.getPropertyValue(BBBCoreConstants.IS_DELETED) && !(boolean)checkListItem3.getPropertyValue(BBBCoreConstants.IS_DELETED))){
													if(null != checkListItem3.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE) && (!(boolean)checkListItem3.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE) && isStagingServer() || (boolean)checkListItem3.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE))){
												CategoryVO c3CategoryVO = populateStaticCategoryVO(checkListItem3,c2CategoryVO.getDisplayName(),c1CategoryVO.getDisplayName(),checkListVO);
												//Calculation of dynamic attributes like quantity and percentage at C3 level.
											
											c3CategoryList.add(c3CategoryVO);
											}
										}
											}	
											c2CategoryVO.setNumberOfC3(c3CheckListItems.size());
											//Setting C3 category map in C2 CategoryVO.
											c2CategoryVO.setChildCategoryVO(c3CategoryList);										
										}
										else{
											c2CategoryVO.setChildCategoryVO(null);
											c2CategoryVO.setNumberOfC3(1);																						
										}
										c2CategoryList.add(c2CategoryVO);																			
									}
								}
									}
								}		
								
								//Setting the C2 category map in C1 category.
								c1CategoryVO.setChildCategoryVO(c2CategoryList);						
								//Adding C1 CategoryVO in CategoryVO list.
								c1CategoryList.add(c1CategoryVO);
							}
						}
							}
						} catch (RepositoryException e) {
							BBBPerformanceMonitor.cancel(
									"CheckListTools", "populateStaticCheckListVO");
							this.logError(
									BBBCoreErrorConstants.CHECKLIST_ERROR_10133
											+ " RepositoryException from populateStaticCheckListVO of CheckListTools",
									e);
							throw new BBBSystemException(
									BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
									BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
									e);
						}
					}
					}
			}
			checkListVO.setCategoryListVO(c1CategoryList);	
			
		}

		BBBPerformanceMonitor.end(
				"CheckListTools", "populateStaticCheckListVO");
		return checkListVO;
		}
	

	/**
	 * The method returns the flag true or false to show static checklist based on config key for registry.
	 * @param registryType the registryType
	 * @return boolean
	 */
	public String showStaticCheckList(String registryType) throws BBBSystemException, BBBBusinessException{
		this.logDebug("CheckListManager.showStaticCheckList() method start");
		List<String> showCheckList=null;
		String isCheckListEnabled=null;	
		if(registryType.contains(BBBCoreConstants.COMMITMENT)){
			showCheckList = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS,
				
					BBBCoreConstants.COMMITMENT + BBBCoreConstants.CHECKLIST_PROGRESS);
		}
		else if(registryType.contains(BBBCoreConstants.COLLEGE_REG) || registryType.contains(BBBCoreConstants.UNIVERSITY_REG)){
			showCheckList = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS,
					BBBCoreConstants.COLLEGE_REG + BBBCoreConstants.CHECKLIST_PROGRESS);
		}
		else{
			showCheckList = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS,
					registryType + BBBCoreConstants.CHECKLIST_PROGRESS);
		}
			if(!BBBUtility.isListEmpty(showCheckList)){
				isCheckListEnabled=showCheckList.get(0).toString();
				logDebug(" is Static CheckList feature on ? "+isCheckListEnabled);
				return isCheckListEnabled;
			}			
		this.logDebug("CheckListManager.showStaticCheckList() method ends");		
		return isCheckListEnabled;
	
	}
	
	/**
	 * The method populates the CategoryVO from a given checkListItem and returns the same.
	 * @param checkListItem the RepositoryItem
	 * @return CategoryVO
	 */
	public CategoryVO populateStaticCategoryVO(final RepositoryItem checkListItem,String c2name,String c1name,CheckListVO checklistVO){
		BBBPerformanceMonitor.start(
				"BBBInteractiveCheckListCacheLoader", "populateCategoryVO");
		this.logDebug("BBBInteractiveCheckListCacheLoader.populateCategoryVO() method starts for cat id: " + (String)checkListItem.getRepositoryId());
		CategoryVO categoryVO = new CategoryVO();
		categoryVO.setCategoryId((String)checkListItem.getRepositoryId());
		
		if(null != checkListItem.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)){
			categoryVO.setDisplayName((String)checkListItem.getPropertyValue(BBBCoreConstants.DISPLAY_NAME));
		}
			if(null != checkListItem.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL)){
				String categoryDisplayName = (String)checkListItem.getPropertyValue(BBBCoreConstants.DISPLAY_NAME);
				String checklistURL = (String)checkListItem.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL);
				if (categoryDisplayName.contains(BBBCoreConstants.SLASH) || categoryDisplayName.contains(BBBCoreConstants.SPACE)) {
					checklistURL = checklistURL.replaceAll(categoryDisplayName, categoryDisplayName.replaceAll(BBBCoreConstants.SPACE_HYPHEN, BBBCoreConstants.HYPHEN));
				}
				categoryVO.setCaOverriddenURL(true);
				categoryVO.setCacategoryURL(checklistURL);
			}else{
				// BBBJ-1222: DSK/ MOB: PLP SEO (ATG)
				// If admin user has not populated the category url then
				// category urls will be auto generated.
				String formatUrl=fetchFormattedURL(checklistVO,c1name,c2name,categoryVO.getDisplayName(),categoryVO.getCategoryId());
				categoryVO.setCacategoryURL(formatUrl);
			}	
			if(null != checkListItem.getPropertyValue(BBBCoreConstants.CA_IMAGE_URL)){
				categoryVO.setCaImageURL((String)checkListItem.getPropertyValue(BBBCoreConstants.CA_IMAGE_URL));
			}			
			if(null != checkListItem.getPropertyValue(BBBCoreConstants.CATEGORY_URL)){
				String categoryDisplayName = (String)checkListItem.getPropertyValue(BBBCoreConstants.DISPLAY_NAME);
				String checklistURL = (String)checkListItem.getPropertyValue(BBBCoreConstants.CATEGORY_URL);
				if (categoryDisplayName.contains(BBBCoreConstants.SLASH) || categoryDisplayName.contains(BBBCoreConstants.SPACE)) {
					checklistURL = checklistURL.replaceAll(categoryDisplayName, categoryDisplayName.replaceAll(BBBCoreConstants.SPACE_HYPHEN, BBBCoreConstants.HYPHEN));
				}
				categoryVO.setUsOverriddenURL(true);
				categoryVO.setUscategoryURL(checklistURL);
			}else{
				// BBBJ-1222: DSK/ MOB: PLP SEO (ATG)
				// If admin user has not populated the category url then
				// category urls will be auto generated.
				String formatUrl=fetchFormattedURL(checklistVO,c1name,c2name,categoryVO.getDisplayName(),categoryVO.getCategoryId());
				categoryVO.setUscategoryURL(formatUrl);
			}
			if(null != checkListItem.getPropertyValue(BBBCoreConstants.CHECKLIST_IMAGE_URL)){
				categoryVO.setImageURL((String)checkListItem.getPropertyValue(BBBCoreConstants.CHECKLIST_IMAGE_URL));
			}			
			if(null != checkListItem.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL)){
				String categoryDisplayName = (String)checkListItem.getPropertyValue(BBBCoreConstants.DISPLAY_NAME);
				String checklistURL = (String)checkListItem.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL);
				if (categoryDisplayName.contains(BBBCoreConstants.SLASH) || categoryDisplayName.contains(BBBCoreConstants.SPACE)) {
					checklistURL = checklistURL.replaceAll(categoryDisplayName, categoryDisplayName.replaceAll(BBBCoreConstants.SPACE_HYPHEN, BBBCoreConstants.HYPHEN));
				}
				categoryVO.setBabyOverriddenURL(true);
				categoryVO.setBabycategoryURL(checklistURL);
			}else{
				// BBBJ-1222: DSK/ MOB: PLP SEO (ATG)
				// If admin user has not populated the category url then
				// category urls will be auto generated.
				String formatUrl=fetchFormattedURL(checklistVO,c1name,c2name,categoryVO.getDisplayName(),categoryVO.getCategoryId());
				categoryVO.setBabycategoryURL(formatUrl);
			}
			if(null != checkListItem.getPropertyValue(BBBCoreConstants.BABY_IMAGE_URL)){
				categoryVO.setBabyImageURL((String)checkListItem.getPropertyValue(BBBCoreConstants.BABY_IMAGE_URL));
			}			
			if(null != checkListItem.getPropertyValue(BBBCoreConstants.SHOW_ON_CHECKLIST)){
				categoryVO.setShowCheckList((boolean)checkListItem.getPropertyValue(BBBCoreConstants.SHOW_ON_CHECKLIST));
			}
			if(null != checkListItem.getPropertyValue(BBBCoreConstants.SHOW_ON_RLP)){
				categoryVO.setShowOnRlp((boolean)checkListItem.getPropertyValue(BBBCoreConstants.SHOW_ON_RLP));
			}
			if(null != checkListItem.getPropertyValue(BBBCoreConstants.ALWAYS_SHOW_ON_RLP)){
				categoryVO.setAlwaysShowOnRlp((boolean)checkListItem.getPropertyValue(BBBCoreConstants.ALWAYS_SHOW_ON_RLP));
			}
			if(null != checkListItem.getPropertyValue(BBBCoreConstants.IS_DELETED)){
				categoryVO.setDeleted((boolean)checkListItem.getPropertyValue(BBBCoreConstants.IS_DELETED));
			}
			if(null != checkListItem.getPropertyValue(BBBCoreConstants.IS_DISABLED)){
				categoryVO.setDisabled((boolean)checkListItem.getPropertyValue(BBBCoreConstants.IS_DISABLED));
			}
			if(null != checkListItem.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE)){
				categoryVO.setConfigureComplete((boolean)checkListItem.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE));
			}
		
		if(null != checkListItem.getPropertyValue(BBBCoreConstants.CATEGORY_NAME)){
			categoryVO.setCategoryName((String)checkListItem.getPropertyValue(BBBCoreConstants.CATEGORY_NAME));		
		}
		
		if(null != checkListItem.getPropertyValue(BBBCoreConstants.PRIMARY_PARENT_CATEGORY_ID)){
			categoryVO.setPrimaryParentCategoryId((String)checkListItem.getPropertyValue(BBBCoreConstants.PRIMARY_PARENT_CATEGORY_ID));
		}
		/*if(null != checkListItem.getPropertyValue(BBBCoreConstants.SEQUENCE_NUMBER)){
			categoryVO.setSequenceNumber((int)checkListItem.getPropertyValue(BBBCoreConstants.SEQUENCE_NUMBER));
		}*/
		if(null != checkListItem.getPropertyValue(BBBCoreConstants.SUGGESTED_QUANTITY)){
			categoryVO.setSuggestedQuantity((int)checkListItem.getPropertyValue(BBBCoreConstants.SUGGESTED_QUANTITY));	
		}
		
		this.logDebug("BBBInteractiveCheckListCacheLoader.populateCategoryVO() method ends");
		BBBPerformanceMonitor.end(
				"BBBInteractiveCheckListCacheLoader", "populateCategoryVO");
		return categoryVO;
	}
	
	/** Populate Checklist VO back in case of manual check.
	 * @param checkListProgressVO
	 * @param checkListVO
	 * @return
	 */
	public CheckListVO populateCheckListVOAfterManualCheck(
			CheckListProgressVO checkListProgressVO, CheckListVO checkListVO,boolean isChecklistEnabled) {
		logDebug("Inside populateCheckListVOAfterManualCheck ");
		if(checkListProgressVO ==null){
			return checkListVO;
		}
		CheckListVO tempChecklistVO= checkListVO;
		 for(CategoryVO cat1VO : tempChecklistVO.getCategoryListVO()){
			 if( null != cat1VO  && null != checkListProgressVO.getCategoryIDForC1() && checkListProgressVO.getCategoryIDForC1().equals(cat1VO.getCategoryId())){
				 cat1VO.setImageURL(checkListProgressVO.getC1ImageUrl());
				 cat1VO.setLabelMessage(checkListProgressVO.getLabelMessage());
				 cat1VO.setC1PercentageComplete(checkListProgressVO.getCategoryProgress());
				 List<CategoryVO> c2CategoryVOList = cat1VO.getChildCategoryVO();
				 ListIterator<CategoryVO> c2CategoryVOListIter=c2CategoryVOList.listIterator();
				 while (c2CategoryVOListIter.hasNext())
	        		{  CategoryVO c2CatVO=(CategoryVO)c2CategoryVOListIter.next();
	        			if(checkListProgressVO.getCategoryIDForC2().equals(c2CatVO.getCategoryId()) && checkListProgressVO.getCategoryIDForC3()==null){
	        				c2CatVO.setC3ManualComplete(isChecklistEnabled);
	        				c2CatVO.setC2CompleteCount(checkListProgressVO.getUpdatedAddedQuantity());
	        				break;
	        			}else if(checkListProgressVO.getCategoryIDForC2().equals(c2CatVO.getCategoryId()) && checkListProgressVO.getCategoryIDForC3()!=null){
	        				List<CategoryVO> c3CategoryVOList = c2CatVO.getChildCategoryVO();
	        				 ListIterator<CategoryVO> c3CategoryVOListIter=c3CategoryVOList.listIterator();
	        				while (c3CategoryVOListIter.hasNext()){
	        					CategoryVO c3CatVO=(CategoryVO)c3CategoryVOListIter.next();
	        					if(checkListProgressVO.getCategoryIDForC3().equals(c3CatVO.getCategoryId())){
	        						c3CatVO.setC3ManualComplete(isChecklistEnabled);
	        						c3CatVO.setC2CompleteCount(checkListProgressVO.getUpdatedAddedQuantity());
	        						break;
	        					}  
	        				}
	        			}
	        		}
			 }
		 }
		 tempChecklistVO.setAverageC1Percentage(checkListProgressVO.getOverAllProgress());
		 return tempChecklistVO;
	}
	
	/**
	 * Populate my item vo.
	 *
	 * @param checkListVO the check list vo
	 * @return the my item vo
	 */
	public MyItemVO populateMyItemVO(final CheckListVO checkListVO) {
		
		MyItemVO myItemVO = new MyItemVO();
		
		List<MyItemCategoryVO> myItemCategoryVOs = new ArrayList<MyItemCategoryVO>();
		if(checkListVO!=null){
			for(CategoryVO categoryVO : checkListVO.getCategoryListVO()) {
				MyItemCategoryVO myItemCategoryVO = new MyItemCategoryVO();
				populateMyItemCategoryVO(myItemCategoryVO, categoryVO);
				setChildMapInMyItemCategoryVO(myItemCategoryVO, categoryVO);
				myItemCategoryVOs.add(myItemCategoryVO);
			}
			
			if(checkListVO.getRegistryId() != null){
				myItemVO.setRegistryId(checkListVO.getRegistryId());
			}
			if(checkListVO.getChecklistTypeName() != null) {
				myItemVO.setMyItemsTypeName(checkListVO.getChecklistTypeName());
			}
		}
		
		myItemVO.setCategoryListVO(myItemCategoryVOs);
		
		return myItemVO;
	}
	
	/**
	 * Populate my item category vo.
	 *
	 * @param myItemCategoryVO the my item category vo
	 * @param categoryVO the category vo
	 */
	public void populateMyItemCategoryVO(MyItemCategoryVO myItemCategoryVO, final CategoryVO categoryVO) {
		
		myItemCategoryVO.setAlwaysShowOnRlp(categoryVO.isAlwaysShowOnRlp());
		myItemCategoryVO.setDisabled(categoryVO.isDisabled());
		/*myItemCategoryVO.setSequenceNumber(categoryVO.getSequenceNumber());*/
		myItemCategoryVO.setShowOnRlp(categoryVO.isShowOnRlp());
		
		if(categoryVO.getCategoryId() != null) {
			myItemCategoryVO.setCategoryId(categoryVO.getCategoryId());
		}
		/*if(categoryVO.getCategoryURL() != null) {
			myItemCategoryVO.setCategoryURL(categoryVO.getCategoryURL());
		}*/
		String siteId = SiteContextManager.getCurrentSiteId();
		
		/*if(null != siteId && !StringUtils.isEmpty(siteId)){
		if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){*/
		myItemCategoryVO.setCaOverriddenURL(categoryVO.isCaOverriddenURL());
			myItemCategoryVO.setCacategoryURL(categoryVO.getCacategoryURL());			
			/*}
		else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)){*/
			myItemCategoryVO.setUsOverriddenURL(categoryVO.isUsOverriddenURL());
			myItemCategoryVO.setUscategoryURL(categoryVO.getUscategoryURL());			
		/*}
		else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)){*/
			myItemCategoryVO.setBabyOverriddenURL(categoryVO.isBabyOverriddenURL());
			myItemCategoryVO.setBabycategoryURL(categoryVO.getBabycategoryURL());		
		/*}
		}*/
		if(categoryVO.getDisplayName() != null) {
			myItemCategoryVO.setDisplayName(categoryVO.getDisplayName());
		}
		if(categoryVO.getPrimaryParentCategoryId() != null) {
			myItemCategoryVO.setPrimaryParentCategoryId(categoryVO.getPrimaryParentCategoryId());
		}
		
	}
	
	/**
	 * Sets the child map in my item category vo.
	 *
	 * @param myItemCategoryVO the my item category vo
	 * @param categoryVO the category vo
	 */
   public void setChildMapInMyItemCategoryVO(MyItemCategoryVO myItemCategoryVO, CategoryVO categoryVO) {
		
		List<MyItemCategoryVO> childMap = new ArrayList<MyItemCategoryVO>();
		
		List<CategoryVO> c2List = categoryVO.getChildCategoryVO();
		
		if(c2List!=null && !c2List.isEmpty()){
			ListIterator<CategoryVO>  c2ListIter = c2List.listIterator();
			
			while(c2ListIter.hasNext()) {
				MyItemCategoryVO myItemC2CategoryVO = new MyItemCategoryVO();
				CategoryVO c2Item=c2ListIter.next();
				populateMyItemCategoryVO(myItemC2CategoryVO, c2Item);
				
				List<CategoryVO> c3List = c2Item.getChildCategoryVO();
				
				if(c3List!=null && !c3List.isEmpty()){
					ListIterator<CategoryVO>  c3ListIter = c3List.listIterator();
					List<MyItemCategoryVO> c3ChildList = new ArrayList<MyItemCategoryVO>();
					
					while(c3ListIter.hasNext()) {
						MyItemCategoryVO myItemC3CategoryVO = new MyItemCategoryVO();
						CategoryVO c3Item=c3ListIter.next();
						populateMyItemCategoryVO(myItemC3CategoryVO,c3Item);
						
						c3ChildList.add(myItemC3CategoryVO);
					}
					
					myItemC2CategoryVO.setChildCategoryVO(c3ChildList);
				}
				childMap.add(myItemC2CategoryVO);
				
			}
			myItemCategoryVO.setChildCategoryVO(childMap);
		}
	}
	
	/**
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}
	/**
	 * @param mCatalogTools the mCatalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools mCatalogTools) {
		this.mCatalogTools = mCatalogTools;
	}

	/**
	 * @return the queryCheckListForRegistryType
	 */
	public String getQueryCheckListForRegistryType() {
		return queryCheckListForRegistryType;
	}

	/**
	 * @param queryCheckListForRegistryType the queryCheckListForRegistryType to set
	 */
	public void setQueryCheckListForRegistryType(
			String queryCheckListForRegistryType) {
		this.queryCheckListForRegistryType = queryCheckListForRegistryType;
	}

	/**
	 * @return the checkListRepository
	 */
	public MutableRepository getCheckListRepository() {
		return checkListRepository;
	}

	/**
	 * @param checkListRepository the checkListRepository to set
	 */
	public void setCheckListRepository(MutableRepository checkListRepository) {
		this.checkListRepository = checkListRepository;
	}

	/**
	 * @return the giftRegistryTools
	 */
	public GiftRegistryTools getGiftRegistryTools() {
		return giftRegistryTools;
	}

	/**
	 * @param giftRegistryTools the giftRegistryTools to set
	 */
	public void setGiftRegistryTools(GiftRegistryTools giftRegistryTools) {
		this.giftRegistryTools = giftRegistryTools;
	}

	/**
	 * @return the queryManuallySelectedC3
	 */
	public String getQueryManuallySelectedC3() {
		return queryManuallySelectedC3;
	}

	/**
	 * @param queryManuallySelectedC3 the queryManuallySelectedC3 to set
	 */
	public void setQueryManuallySelectedC3(String queryManuallySelectedC3) {
		this.queryManuallySelectedC3 = queryManuallySelectedC3;
	}

	/**
	 * @return the profileRepository
	 */
	public MutableRepository getProfileRepository() {
		return profileRepository;
	}

	/**
	 * @param profileRepository the profileRepository to set
	 */
	public void setProfileRepository(MutableRepository profileRepository) {
		this.profileRepository = profileRepository;
	}

	/**
	 * @return the querySkuPackageCount
	 */
	public String getQuerySkuPackageCount() {
		return querySkuPackageCount;
	}

	/**
	 * @param querySkuPackageCount the querySkuPackageCount to set
	 */
	public void setQuerySkuPackageCount(String querySkuPackageCount) {
		this.querySkuPackageCount = querySkuPackageCount;
	}

	/**
	 * @return the catalogRepository
	 */
	public MutableRepository getCatalogRepository() {
		return catalogRepository;
	}

	/**
	 * @param catalogRepository the catalogRepository to set
	 */
	public void setCatalogRepository(MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	/**
	 * @return the queryC1Categories
	 */
	public String getQueryC1Categories() {
		return queryC1Categories;
	}

	/**
	 * @param queryC1Categories the queryC1Categories to set
	 */
	public void setQueryC1Categories(String queryC1Categories) {
		this.queryC1Categories = queryC1Categories;
	}

	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public boolean isStagingServer() {
		return stagingServer;
	}

	public void setStagingServer(boolean stagingServer) {
		this.stagingServer = stagingServer;
	}
	
	/**
	 * Fetches guide type based on the guide id
	 *
	 * @param guideId
	 * @return guideType
	 */

	public String fetchGuideTypeFromId(String guideId) {
		if (isLoggingDebug()) {
			logDebug("CheckListTools.fetchGuideTypeFromId() method started for guideId = " + guideId);
		}
		RepositoryItem checkListItem = null;
		String subTypeCode = null;
		try {
			checkListItem = getCheckListRepository().getItem(guideId, BBBCoreConstants.CHECKLIST);
			if (checkListItem != null) {
				subTypeCode = (String) checkListItem.getPropertyValue(BBBCoreConstants.SUBTYPE_CODE);
			}
		} catch (RepositoryException exception) {
			logError("CheckListTools.fetchGuideTypeFromId() RepositoryException occurredd while fetching subtype. ", exception);
		}
		if (isLoggingDebug()) {
			logDebug("CheckListTools.fetchGuideTypeFromId() method end. subTypeCode = " + subTypeCode);
		}
		return subTypeCode;
		
	}
	
	/**
	 * Method fetches leaf category url of first enabled C1's child category (C2 or C3) 
	 * @param guideItem
	 * @return Map
	 */
	
	@SuppressWarnings("unchecked")
	public HashMap<String, String> getFirstLeafCategoryURL(RepositoryItem guideItem) {
		
		logDebug("CheckListTools.getFirstLeafCategoryURL method started for Item = " + guideItem.getRepositoryId());
		String leafCategoryUrl = null;
		String c1DisplayName = BBBCoreConstants.BLANK;
		String c2DisplayName = BBBCoreConstants.BLANK;
		String leafCatDisplayName = BBBCoreConstants.BLANK;
		String c3RepoId = BBBCoreConstants.BLANK;
		String c2RepoId = BBBCoreConstants.BLANK;
		String checklistURL = BBBCoreConstants.BLANK;
		String isOverriddenUrl = BBBCoreConstants.FALSE;
		HashMap<String, String> leafCategoryUrlMap = new HashMap<String, String>();
		List<RepositoryItem> checkListCategories = (List<RepositoryItem>) guideItem .getPropertyValue(BBBCoreConstants.CHECK_LIST_CATEGORIES);
		CheckListVO checkListVO = new CheckListVO();
		if(null != guideItem ){
			if(null != guideItem.getRepositoryId()){
			checkListVO.setChecklistId((String) guideItem.getRepositoryId());
			}
			if(null != guideItem.getPropertyValue(BBBCoreConstants.SUBTYPE_CODE)){
			checkListVO.setChecklistTypeName((String) guideItem.getPropertyValue(BBBCoreConstants.SUBTYPE_CODE));
			}
			if(null != guideItem.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)){
				checkListVO.setDisplayName((String) guideItem.getPropertyValue(BBBCoreConstants.DISPLAY_NAME));
			}
			if(null != guideItem.getPropertyValue(BBBCoreConstants.SITE_FLAG)){
				checkListVO.setSiteFlag((String) guideItem.getPropertyValue(BBBCoreConstants.SITE_FLAG));
			}
			
		}
		String siteId = SiteContextManager.getCurrentSiteId();
		if (!BBBUtility.isListEmpty(checkListCategories)) {
			for (RepositoryItem c1Category : checkListCategories) {
				if (null != c1Category && null != c1Category.getPropertyValue(BBBCoreConstants.SHOW_ON_CHECKLIST)
						&& (boolean) c1Category.getPropertyValue(BBBCoreConstants.SHOW_ON_CHECKLIST) && null != c1Category.getPropertyValue(BBBCoreConstants.IS_DISABLED)
						&& !((boolean) c1Category.getPropertyValue(BBBCoreConstants.IS_DISABLED))
						&& null != c1Category.getPropertyValue(BBBCoreConstants.IS_DELETED)
						&& !((boolean) c1Category.getPropertyValue(BBBCoreConstants.IS_DELETED))) {
					if(null != c1Category.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)){
						c1DisplayName = (String)c1Category.getPropertyValue(BBBCoreConstants.DISPLAY_NAME);
					}
					List<RepositoryItem> c2CheckListCategories = (List<RepositoryItem>) c1Category.getPropertyValue(BBBCoreConstants.CHILD_CHECKLIST_CATEGORY_ID);
					if (!BBBUtility.isListEmpty(c2CheckListCategories)) {
						for (RepositoryItem c2Category : c2CheckListCategories) {
							if (null != c2Category.getPropertyValue(BBBCoreConstants.IS_DISABLED)
									&& !(boolean) c2Category.getPropertyValue(BBBCoreConstants.IS_DISABLED)
									&& null != c2Category.getPropertyValue(BBBCoreConstants.IS_DELETED)
									&& !(boolean) c2Category.getPropertyValue(BBBCoreConstants.IS_DELETED)) {
								if(null != c2Category.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)){
									c2DisplayName = (String)c2Category.getPropertyValue(BBBCoreConstants.DISPLAY_NAME);
								}
								if(null != c2Category.getRepositoryId()){
									c2RepoId = (String)c2Category.getRepositoryId();
								}
								List<RepositoryItem> c3CheckListCategories = (List<RepositoryItem>) c2Category.getPropertyValue(BBBCoreConstants.CHILD_CHECKLIST_CATEGORY_ID);
								if (!BBBUtility.isListEmpty(c3CheckListCategories)) {
									for (RepositoryItem c3Category : c3CheckListCategories) {
										
										if(null != c3Category.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)){
											leafCatDisplayName = (String)c3Category.getPropertyValue(BBBCoreConstants.DISPLAY_NAME);
										}
										if(null != c3Category.getRepositoryId()){
											c3RepoId = (String)c3Category.getRepositoryId();
										}
										
										if(null != siteId && !StringUtils.isEmpty(siteId)){
										if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
												if (null != c3Category
														.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL)) {
													 checklistURL = (String)c3Category.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL);
										}
										}
										else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)){
											if (null != c3Category
													.getPropertyValue(BBBCoreConstants.CATEGORY_URL)) {
												 checklistURL = (String)c3Category.getPropertyValue(BBBCoreConstants.CATEGORY_URL);
										}
										}
										else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)){
											if (null != c3Category
													.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL)) {
												 checklistURL = (String)c3Category.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL);
										}
										}
											if(null != checklistURL && !BBBCoreConstants.BLANK.equalsIgnoreCase(checklistURL)){
												if (leafCatDisplayName.contains(BBBCoreConstants.SLASH) || leafCatDisplayName.contains(BBBCoreConstants.SPACE)) {
													checklistURL = checklistURL.replaceAll(leafCatDisplayName, leafCatDisplayName.replaceAll(BBBCoreConstants.SPACE_HYPHEN, BBBCoreConstants.HYPHEN));
												}
												leafCategoryUrl = checklistURL;
												isOverriddenUrl = BBBCoreConstants.TRUE;
											}	
											else {
												// BBBJ-1222: DSK/ MOB: PLP SEO (ATG)
												// If admin user has not populated the category url then
												// category urls will be auto generated.
												String formatUrl=fetchFormattedURL(checkListVO,c1DisplayName,c2DisplayName,leafCatDisplayName,c3RepoId);
												leafCategoryUrl = formatUrl;
											}
										}
										break;
									}
								} else {
									leafCatDisplayName = c2DisplayName;
									if(null != siteId && !StringUtils.isEmpty(siteId)){
										if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
												if (null != c2Category
														.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL)) {
													checklistURL = (String)c2Category.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL);
										}
									}
										else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)){
											if (null != c2Category
													.getPropertyValue(BBBCoreConstants.CATEGORY_URL)) {
												 checklistURL = (String)c2Category.getPropertyValue(BBBCoreConstants.CATEGORY_URL);
										}
									}
										else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)){
											if (null != c2Category
													.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL)) {
												 checklistURL = (String)c2Category.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL);
										}
										}
											if(null != checklistURL && !BBBCoreConstants.BLANK.equalsIgnoreCase(checklistURL)){
												if (leafCatDisplayName.contains(BBBCoreConstants.SLASH) || leafCatDisplayName.contains(BBBCoreConstants.SPACE)) {
													checklistURL = checklistURL.replaceAll(leafCatDisplayName, leafCatDisplayName.replaceAll(BBBCoreConstants.SPACE_HYPHEN, BBBCoreConstants.HYPHEN));
												}
												leafCategoryUrl = checklistURL;
												isOverriddenUrl = BBBCoreConstants.TRUE;
											}	
											else {
												// BBBJ-1222: DSK/ MOB: PLP SEO (ATG)
												// If admin user has not populated the category url then
												// category urls will be auto generated.
												String formatUrl=fetchFormattedURL(checkListVO,c1DisplayName,null,leafCatDisplayName,c2RepoId);
												leafCategoryUrl = formatUrl;
											}
									}
									break;
								}
								break;
							}
						}
					}
					break;
				}
			}
		}
			
		logDebug("CheckListTools.getFirstLeafCategoryURL method Ends. Leaf Category URL = " + leafCategoryUrl);
		leafCategoryUrlMap.put(BBBCoreConstants.LEAF_CATEGORY_URL, leafCategoryUrl);
		leafCategoryUrlMap.put(BBBCoreConstants.IS_OVERRIDDEN_URL, isOverriddenUrl);
		return leafCategoryUrlMap;
	}

	
	/**
	 * Method fetches leaf category url of first enabled C1's child category (C2 or C3) for Cached VO
	 * @param checkListVO
	 * @return String
	 */
	
	public HashMap<String, String> getFirstLeafCategoryURL(CheckListVO checkListVO) {
		
		logDebug("CheckListTools.getFirstLeafCategoryURL method started for CheckListVO = " + checkListVO);
		String leafCategoryUrl = null;
		boolean isOverriddenUrl = false ;
		HashMap<String, String> leafCategoryUrlMap = new HashMap<String, String>();
		List<CategoryVO> c1CategoryVOs = checkListVO.getCategoryListVO();
		String siteId = SiteContextManager.getCurrentSiteId();
		if (!BBBUtility.isListEmpty(c1CategoryVOs)) {
			for (CategoryVO c1Category : c1CategoryVOs) {
				if (null != c1Category && c1Category.isShowCheckList() && !c1Category.isDisabled() && !c1Category.isDeleted()) {
					List<CategoryVO> c2CheckListCategories = c1Category.getChildCategoryVO();
					if (!BBBUtility.isListEmpty(c2CheckListCategories)) {
						ListIterator<CategoryVO> c2CheckListCategoriesIter=c2CheckListCategories.listIterator();
							while (c2CheckListCategoriesIter.hasNext()) {
								CategoryVO c2CategoryVO = c2CheckListCategoriesIter.next();
								if (!c2CategoryVO.isDisabled() && !c2CategoryVO.isDeleted()) {

									List<CategoryVO> c3CheckListCategories = c2CategoryVO.getChildCategoryVO();
									if (!BBBUtility.isListEmpty(c3CheckListCategories)) {
										ListIterator<CategoryVO> c3CheckListCategoriesIter=c3CheckListCategories.listIterator();
										
										while (c3CheckListCategoriesIter.hasNext()) {
											CategoryVO c3CategoryVO = c3CheckListCategoriesIter.next();
											
											
											if(null != siteId && !StringUtils.isEmpty(siteId)){
												if (siteId
												.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
													leafCategoryUrl = c3CategoryVO
															.getCacategoryURL();
													isOverriddenUrl = c3CategoryVO.isCaOverriddenURL();
												}																					
											else if (siteId
														.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)) {
												leafCategoryUrl = c3CategoryVO
															.getUscategoryURL();
												isOverriddenUrl = c3CategoryVO.isUsOverriddenURL();
											}
											else if (siteId
													.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)) {
												leafCategoryUrl = c3CategoryVO
															.getBabycategoryURL();
												isOverriddenUrl = c3CategoryVO.isBabyOverriddenURL();
											}
											}
											break;
										}
									} else {
										if(null != siteId && !StringUtils.isEmpty(siteId)){
											if (siteId
													.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
												leafCategoryUrl = c2CategoryVO.getCacategoryURL();
												isOverriddenUrl = c2CategoryVO.isCaOverriddenURL();
											}
										else if (siteId
													.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)) {
												leafCategoryUrl = c2CategoryVO.getUscategoryURL();
												isOverriddenUrl = c2CategoryVO.isUsOverriddenURL();
											}
										else if (siteId
												.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)) {
												leafCategoryUrl = c2CategoryVO.getBabycategoryURL();
												isOverriddenUrl = c2CategoryVO.isBabyOverriddenURL();
										}
										}
										break;
									}
									break;
								}
							}
					}
					break;
				}
			}
		}
		logDebug("CheckListTools.getFirstLeafCategoryURL method Ends. Leaf Category URL = " + leafCategoryUrl);
		leafCategoryUrlMap.put(BBBCoreConstants.LEAF_CATEGORY_URL, leafCategoryUrl);
		if(isOverriddenUrl){
			leafCategoryUrlMap.put(BBBCoreConstants.IS_OVERRIDDEN_URL, BBBCoreConstants.TRUE);	
		} else {
			leafCategoryUrlMap.put(BBBCoreConstants.IS_OVERRIDDEN_URL, BBBCoreConstants.FALSE);
		}
		
		return leafCategoryUrlMap;
	
	
	}


	/**This method would return if the category should be included on RLP or not.
	 * Include category on rlp.
	 *
	 * @param ephCategoryRepoItem the eph category repo item
	 * @return the string
	 */
	public String includeCategoryOnRLP(RepositoryItem ephCategoryRepoItem) {
      logDebug("Inside includeCategoryOnRLP with repository item : "+ephCategoryRepoItem);
      if(ephCategoryRepoItem ==null){
    	  return null;
      }
      
      if(null != ephCategoryRepoItem.getPropertyValue(BBBCoreConstants.ALWAYS_SHOW_ON_RLP)){
			if((boolean)ephCategoryRepoItem.getPropertyValue(BBBCoreConstants.ALWAYS_SHOW_ON_RLP)){
				logDebug("Always Show on RLP flag is true for repo id : "+ephCategoryRepoItem.getRepositoryId());
				return BBBCoreConstants.SHOW;
			}
      }
		return null;
	}
	
	/**
	 * Fetch formatted url.
	 *
	 * @param checklistName the checklist name
	 * @param c1name the c1name
	 * @param c2name the c2name
	 * @param displayName the display name
	 * @param categoryId the category id
	 * @return the string
	 */
	public String fetchFormattedURL(CheckListVO checklistVO, String c1name,
			String c2name, String displayName, String categoryId) {
		logDebug("Entering fetchFormattedURL with  c1 name "+c1name+" c2name "+c2name + "Checklist Id :"+categoryId);
		String formatUrl = BBBCoreConstants.SLASH +BBBCoreConstants.CHECKLIST_URL_CONSTANT;
		if (checklistVO !=null && checklistVO.getDisplayName() != null) {
			formatUrl = formatUrl + BBBCoreConstants.SLASH + formatUrlParam(checklistVO.getDisplayName());
		}
		if (c1name != null) {
			formatUrl = formatUrl + BBBCoreConstants.SLASH + formatUrlParam(c1name);
		}
		if (c2name != null) {
			formatUrl = formatUrl + BBBCoreConstants.SLASH + formatUrlParam(c2name);
		}
		
		if (displayName != null) {
			formatUrl = formatUrl + BBBCoreConstants.SLASH + formatUrlParam(displayName);
		}
		
	    if(categoryId !=null){
		formatUrl = formatUrl + BBBCoreConstants.SLASH + formatUrlParam(categoryId);
	    }
	    
		if (checklistVO !=null && checklistVO.getChecklistId() != null) {
			formatUrl = formatUrl + BBBCoreConstants.SLASH + formatUrlParam(checklistVO.getChecklistId());
		}
	    
	    if(formatUrl !=null){
	    	formatUrl=formatUrl.toLowerCase();
	    }

		logDebug("Exiting fetchFormattedURL url "+formatUrl);
		return formatUrl;
	}
	
	/**
	 * Format url param.
	 *
	 * @param urlParamString the url param
	 * @return the string
	 */
	public String formatUrlParam(String urlParamString){
		logDebug("Entering formatUrlParam with urlParam :"+urlParamString);
		urlParamString = escapeHtmlString(urlParamString);
		urlParamString = formattedDisplayName(urlParamString);
		
		logDebug("Exiting fetchFormattedURL url "+urlParamString);
		return urlParamString;
	}

	/**
	 * Method used to escape special character/space in  checklist URL
	 * 
	 * @param displayName
	 * @return resutlString
	 */
	public String formattedDisplayName(String displayName) {
		logDebug("Entering formattedDisplayName with displayName :"+displayName);
		String resultString = displayName;
		if (!BBBUtility.isEmpty(resultString)) {
			resultString = resultString.replaceAll(getUrlNonAcceptableRegexPattern(),
					BBBCoreConstants.HYPHEN);

			if (resultString.startsWith(BBBCoreConstants.HYPHEN)) {
				resultString = resultString.substring(1);
			}
			if (resultString.endsWith(BBBCoreConstants.HYPHEN)) {
				resultString = resultString.substring(0,
						resultString.length() - 1);
			}
		}
		logDebug("Exiting formattedDisplayName with resultString :"+resultString);
		return resultString;
	}
	
	/**
	 * Method used to escape HTML Strings
	 * 
	 * @param pStr
	 * @param pEscapeAmp
	 * @return
	 */
	public String escapeHtmlString(String url) {
		logDebug("Entering escapeHtmlString with url :"+url);
		if (!BBBUtility.isEmpty(url)) {
			if (getEntities() != null && !(getEntities().length == 0)) {
				for (int i = 0; i < entities.length; i++) {
					if (url.contains(entities[i])) {
						url = url.replaceAll(entities[i],
								BBBCoreConstants.BLANK);
					}
				}
			}
		}
		logDebug("Exiting escapeHtmlString with url :"+url);
		return url;
	}
	
	@SuppressWarnings("unchecked")
	public List<RepositoryItem> childCategoriesForChecklist(
			RepositoryItem checklistItem) {
		logDebug("Entering childCategoriesForChecklist ");
		List<RepositoryItem> checkListCategoriesForSitemap = new ArrayList<RepositoryItem>();
		if (checklistItem != null
				&& checklistItem
						.getPropertyValue(BBBCoreConstants.CHECK_LIST_CATEGORIES) != null) {
			logDebug("checklistItem : " + checklistItem.getRepositoryId());
			List<RepositoryItem> checkListCategories = (List<RepositoryItem>) checklistItem
					.getPropertyValue(BBBCoreConstants.CHECK_LIST_CATEGORIES);
			for (RepositoryItem checklistCategory : checkListCategories) {
				boolean isConfigureComplete = false;
				boolean isDisabled = true;
				boolean isDeleted = true;
				if (checklistCategory
						.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE) != null) {
					isConfigureComplete = (boolean) checklistCategory
							.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE);
				}
				if (checklistCategory
						.getPropertyValue(BBBCoreConstants.IS_DISABLED) != null) {
					isDisabled = (boolean) checklistCategory
							.getPropertyValue(BBBCoreConstants.IS_DISABLED);
				}
				if (checklistCategory
						.getPropertyValue(BBBCoreConstants.IS_DELETED) != null) {
					isDeleted = (boolean) checklistCategory
							.getPropertyValue(BBBCoreConstants.IS_DELETED);
				}
				if ((!isConfigureComplete && isStagingServer() || isConfigureComplete)
						&& !isDisabled && !isDeleted) {
					checkListCategoriesForSitemap.add(checklistCategory);
				}
			}
		}
		logDebug("Existing childCategoriesForChecklist ");
		return checkListCategoriesForSitemap;
	}
	
	@SuppressWarnings("unchecked")
	public List<RepositoryItem> childCategoriesForChecklistCategory(
			RepositoryItem parentCategory) {
		List<RepositoryItem> childCheckListCategories = null;
		logDebug("Entering childCategoriesForChecklistCategory ");
		List<RepositoryItem> childCheckListCategoriesForSitemap = new ArrayList<RepositoryItem>();
		if (parentCategory != null
				&& parentCategory
						.getPropertyValue(BBBCoreConstants.CHILD_CHECKLIST_CATEGORY_ID) != null) {
			logDebug("parentCategory : " + parentCategory.getRepositoryId());
			childCheckListCategories = (List<RepositoryItem>) parentCategory
					.getPropertyValue(BBBCoreConstants.CHILD_CHECKLIST_CATEGORY_ID);
			if (childCheckListCategories != null) {
				for (RepositoryItem checklistCategory : childCheckListCategories) {
					boolean isConfigureComplete = false;
					boolean isDisabled = true;
					boolean isDeleted = true;
					if (checklistCategory
							.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE) != null) {
						isConfigureComplete = (boolean) checklistCategory
								.getPropertyValue(BBBCoreConstants.IS_CONFIGURE_COMPLETE);
					}
					if (checklistCategory
							.getPropertyValue(BBBCoreConstants.IS_DISABLED) != null) {
						isDisabled = (boolean) checklistCategory
								.getPropertyValue(BBBCoreConstants.IS_DISABLED);
					}
					if (checklistCategory
							.getPropertyValue(BBBCoreConstants.IS_DELETED) != null) {
						isDeleted = (boolean) checklistCategory
								.getPropertyValue(BBBCoreConstants.IS_DELETED);
					}
					if ((!isConfigureComplete && isStagingServer() || isConfigureComplete)
							&& !isDisabled && !isDeleted) {
						childCheckListCategoriesForSitemap
								.add(checklistCategory);
					}
				}
			}
		}
		logDebug("Exiting childCategoriesForChecklistCategory ");
		return childCheckListCategoriesForSitemap;
	}
	
	
	
	/**
	 * @param pSubTypeCode
	 * @return
	 */
	public List<RepositoryItem> getServiceFamilyListBySubType(String pSubTypeCode){
		RepositoryItem[] serviceFamilyList=null;
		try {
			RepositoryView view = getRegistryCheckListRepository().getView("checkList");
			RqlStatement statement = RqlStatement.parseRqlStatement(getPorchCheckListQuery());
			Object params[] = new Object[2];
			params[0] = pSubTypeCode;
			serviceFamilyList = statement.executeQuery(view, params);
		} catch (RepositoryException e) {
			if(isLoggingError()){
				logError("Error while fetching service Famillist for subtype :"+pSubTypeCode +"  "+e,e);
			}
		}
	
		if(null!=serviceFamilyList){
			List<RepositoryItem> listcategories = (List<RepositoryItem>) serviceFamilyList[0].getPropertyValue("checkListCategories");
			return listcategories;
		}
		else
		{
			return null;
		}
	 
}
	
	
	public String[] getEntities() {
		return entities;
	}

	public void setEntities(String[] entities) {
		this.entities = entities;
	}

	public String getUrlNonAcceptableRegexPattern() {
		return urlNonAcceptableRegexPattern;
	}

	public void setUrlNonAcceptableRegexPattern(
			String urlNonAcceptableRegexPattern) {
		this.urlNonAcceptableRegexPattern = urlNonAcceptableRegexPattern;
	}

	/**
	 * @return the porchCheckListQuery
	 */
	public String getPorchCheckListQuery() {
		return porchCheckListQuery;
	}

	/**
	 * @param porchCheckListQuery the porchCheckListQuery to set
	 */
	public void setPorchCheckListQuery(String porchCheckListQuery) {
		this.porchCheckListQuery = porchCheckListQuery;
	}

}
