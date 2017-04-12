package com.bbb.commerce.checklist.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.checklist.vo.CheckListSeoUrlHierarchy;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBObjectCache;
import com.bbb.search.endeca.EndecaSearchUtil;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

import atg.adapter.gsa.GSAItem;
import atg.multisite.Site;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextImpl;
import atg.multisite.SiteContextManager;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;

/** 
 * 
 * Tool for @link CheckListCategoryHierarchyCacheScheduler
 *
 */
public class CheckListCategoryHierarchyCacheTool extends BBBGenericService {
	
	
	private Repository registryCheckListRepository;
	private EndecaSearchUtil endecaSearchUtil;
	private String defaultPoolSize;
	private ExecutorService threadPool;
	private BBBCatalogTools bbbCatalogTools;
	private CheckListTools checkListTools;
	private SiteContextManager siteContextManager;
	private BBBObjectCache objectCache;	
	private boolean detailLog;
	
		/**
	 * This method creates check list hierarchy and put in the coherence
	 * cache.will be called from scheduler and server startup
	 * 
	 * @return boolean
	 */
	
	
	public boolean populateCheckListHierarchyAndLoadInCache() {
		boolean isSuccess = false;
		long startTime = System.currentTimeMillis();
		 
		logDebug("CheckListCategoryHierarchyCacheTool populateCheckListHierarchyAndLoadInCache start");
	try {
		 
			List<CheckListSeoUrlHierarchy> seoUrlList = populateCheckListHierarchy();
			clearProductDimCache();
			populateInfoFromEndecaByMultiThreadJob(seoUrlList);
			isSuccess=putHierarchyIntoCoherenceCache(seoUrlList);
		} catch (BBBSystemException systemException) {
			logError("populateCheckListHierarchyAndLoadInCache BBBSystemException", systemException);
		} catch (InterruptedException interruptedException) {
			logError("populateCheckListHierarchyAndLoadInCache interruptedException", interruptedException);
		}catch (Exception exception) {
		logError("populateCheckListHierarchyAndLoadInCache exception", exception);
		}

		logDebug("CheckListCategoryHierarchyCacheTool populateCheckListHierarchyAndLoadInCache End. isSuccess["
				+ isSuccess + "], TimeTook[" + (System.currentTimeMillis() - startTime));
		return isSuccess;
	}

	
	/**
	 * Clear Product dim Id cache so that newly Dimesnions of newly created
	 * category get laoded inProductDimCache
	 */
	
	private void clearProductDimCache() {
				String cacheName = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY , BBBCoreConstants.PRODUCT_DIM_ID_CACHE_NAME);
				getObjectCache().clearCache(cacheName);
	}

/**
 * This method put only those VO which have valid endeca details.
 * @param seoUrlList
 * @return 
 */

private boolean putHierarchyIntoCoherenceCache(List<CheckListSeoUrlHierarchy> seoUrlList) {
		
	logDebug("CheckListCategoryHierarchyCacheTool putHierarchyIntoCoherenceCache start. ");
	long startTime=System.currentTimeMillis();
	String	cacheName = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY , BBBCoreConstants.CHECKLIST_PLP_SEO_URL_CACHE_NAME);
	//long  cacheTimeout = Long.valueOf(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY , BBBCoreConstants.CHECKLIST_PLP_SEO_URL_CACHE_TIMEOUT));
	boolean isAnyVoLoaded=false;
	int totalVo=0;
	int loadedVo=0;
	if( ! BBBUtility.isListEmpty(seoUrlList)){
		
		totalVo = seoUrlList.size();
		
		getObjectCache().clearCache(cacheName);
		
		for(CheckListSeoUrlHierarchy checkListSeoUrlHierarchy: seoUrlList){
			if(checkListSeoUrlHierarchy.isProcessed()){
				getObjectCache().put(checkListSeoUrlHierarchy.getSeoUrl(), checkListSeoUrlHierarchy, cacheName);
				logDebug("loadedingUrl:"+checkListSeoUrlHierarchy.getSeoUrl());
				if(isDetailLog()){
					logDebug("loadedingUrl:"+checkListSeoUrlHierarchy.getSeoUrl()+",["+checkListSeoUrlHierarchy+"]");
				}
				isAnyVoLoaded= true;
				loadedVo= loadedVo+1;
			}
		}
	}
		logDebug("CheckListCategoryHierarchyCacheTool putHierarchyIntoCoherenceCache End : TimeTook["
				+ (System.currentTimeMillis() - startTime) + ",cacheName:" + cacheName + ",totalVoinList:[" + totalVo + "]" + ",loadedVo:[" + loadedVo + ",SkippedVo:[" + (totalVo - loadedVo)
				+ "],isAnyVoLoaded:[" + isAnyVoLoaded + "]");
		
	return isAnyVoLoaded;
 }

/**
    * Method to generated Hierarchy of category of CheckList
    * @return
    * @throws BBBSystemException
    */
	private List<CheckListSeoUrlHierarchy> populateCheckListHierarchy() throws BBBSystemException 
	{
		
		long startTime=System.currentTimeMillis();
		List<CheckListSeoUrlHierarchy> allC1UrlHierarchyList= new ArrayList<CheckListSeoUrlHierarchy>();
		logDebug("CheckListCategoryHierarchyCacheTool populateCheckListHierarchy start");
		
	try {   
			List<String> regSubTypeList=getRegistrySubTypeCodeList();
			
			RepositoryItemDescriptor checkListItemDescriptor= getRegistryCheckListRepository().getItemDescriptor(BBBCoreConstants.CHECKLIST);
			RepositoryView checkListView=checkListItemDescriptor.getRepositoryView();
			QueryBuilder queryBuilder=checkListView.getQueryBuilder();
			Query fetchAllCheckList=queryBuilder.createUnconstrainedQuery();
			RepositoryItem[] checkListItems=checkListView.executeQuery(fetchAllCheckList);
			
			if(checkListItems != null && checkListItems.length > 0){
			logDebug("CheckListCategoryHierarchyCacheTool populateCheckListHierarchy checkListItems size:["+checkListItems.length+"]");
			
				for(RepositoryItem checkListItem : checkListItems){
					
					String checkListDisplayName=(String) checkListItem.getPropertyValue(BBBCoreConstants.DISPLAY_NAME);
					String checkListId=checkListItem.getRepositoryId();
					String subTypeCode=(String) checkListItem.getPropertyValue(BBBCoreConstants.SUBTYPE_CODE); 
					
					boolean isCheckListDisabled=false;
					boolean isRegTypeCheckList= false;
					if(regSubTypeList.contains(subTypeCode)){
						 isRegTypeCheckList= true;
					}
					if (checkListItem.getPropertyValue(BBBCoreConstants.IS_DISABLED) != null&& (Boolean) checkListItem.getPropertyValue(BBBCoreConstants.IS_DISABLED)) {
						isCheckListDisabled=true;
					}
					
					checkListDisplayName=getCheckListTools().formatUrlParam(checkListDisplayName);
					List<GSAItem> checkListCategoryItems= (List<GSAItem>) checkListItem.getPropertyValue(BBBCoreConstants.CHECK_LIST_CATEGORIES);
					allC1UrlHierarchyList.addAll(loadC1AndChildWithHierarchy(checkListCategoryItems,checkListId,checkListDisplayName,isRegTypeCheckList,isCheckListDisabled));
					 
				}
			}
			
		} catch (RepositoryException repositoryException) {
			logError("RepositoryException occured in populateCheckListHierarchy ", repositoryException);
			throw new BBBSystemException("Exception occured in populateCheckListHierarchy ", repositoryException);
		
	  } catch (Exception exception) {
		logError("Exception occured in populateCheckListHierarchy ", exception);
		throw new BBBSystemException("Exception occured in populateCheckListHierarchy ", exception);
	  }
		finally{
		logDebug("CheckListCategoryHierarchyCacheTool populateCheckListHierarchy End: tookTime:["+(System.currentTimeMillis()-startTime)+"],allC1UrlHierarchyList:"+allC1UrlHierarchyList.size());
		}
		
		return allC1UrlHierarchyList;
	}
	
	private List<String> getRegistrySubTypeCodeList() throws RepositoryException{
		
		long startTime=System.currentTimeMillis();
		List<String> subTypeCodeList= new ArrayList<String>();
		logDebug("CheckListCategoryHierarchyCacheTool getRegistrySubTypeCodeList start");
		
		RepositoryItemDescriptor checkListItemDescriptor= getRegistryCheckListRepository().getItemDescriptor(BBBCoreConstants.CHECK_LIST_TYPE);
		RepositoryView checkListView=checkListItemDescriptor.getRepositoryView();
		QueryBuilder queryBuilder=checkListView.getQueryBuilder();
		QueryExpression typeNameExp=queryBuilder.createPropertyQueryExpression(BBBCoreConstants.TYPE_NAME);
		QueryExpression registryValExp=queryBuilder.createConstantQueryExpression("REGISTRY");
		Query fetchRegistrySubType=queryBuilder.createComparisonQuery(typeNameExp, registryValExp, QueryBuilder.EQUALS);
		RepositoryItem[] registrySubTypeItems=checkListView.executeQuery(fetchRegistrySubType);
		if(registrySubTypeItems != null){
			for(RepositoryItem registrySubTypeItem:registrySubTypeItems){
				subTypeCodeList.add((String) registrySubTypeItem.getPropertyValue(BBBCoreConstants.SUBTYPE_CODE));
			}
			 
		}
		logDebug("CheckListCategoryHierarchyCacheTool getRegistrySubTypeCodeList End: tookTime:["+(System.currentTimeMillis()-startTime)+"],subTypeCodeList:"+subTypeCodeList);
		return subTypeCodeList;
		
	}
	
	/**
	 * Load C1 category with child category
	 * @param checkListCategoryItems
	 * @param checkListId
	 * @param checkListDisplayName
	 * @return
	 */
	protected List<CheckListSeoUrlHierarchy> loadC1AndChildWithHierarchy(List<GSAItem> checkListCategoryItems,
			String checkListId, String checkListDisplayName, boolean isRegTypeCheckList,boolean isCheckListDisabled) {

		long startTime=System.currentTimeMillis();
		
		logDebug("CheckListCategoryHierarchyCacheTool loadC1AndChildWithHierarchy start:checkListDisplayName"+checkListDisplayName+",checkListId:"+checkListId);
		
		List<CheckListSeoUrlHierarchy> seoUrlHierarchyList=  new ArrayList<CheckListSeoUrlHierarchy>();
		if(checkListCategoryItems != null && checkListCategoryItems.size() >0)
		{ 
			 
				for(RepositoryItem checkListCategory : checkListCategoryItems){
					
					
					CheckListSeoUrlHierarchy checkListSeoUrlHierarchy= new CheckListSeoUrlHierarchy();
					List<CheckListSeoUrlHierarchy> c2ChildOfC1Category=null;
					String c1CategoryId					=	(String) checkListCategory.getRepositoryId();
					String c1CategoryDisplayName		=	(String) checkListCategory.getPropertyValue(BBBCoreConstants.DISPLAY_NAME);
					
					Boolean showOnCheckList		=	(Boolean) checkListCategory.getPropertyValue(BBBCoreConstants.SHOW_ON_CHECKLIST);					
					Boolean isDisable		=	(Boolean) checkListCategory.getPropertyValue(BBBCoreConstants.IS_DISABLED);					
					Boolean isDeleted		=	(Boolean) checkListCategory.getPropertyValue(BBBCoreConstants.IS_DELETED);
					
					String babyCategoryURL =(String) checkListCategory.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL);
					String caCategoryURL =(String) checkListCategory.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL);
					String tbsCategoryURL=(String) checkListCategory.getPropertyValue(BBBCoreConstants.TBS_CATEGORY_URL);
					String categoryURL=(String) checkListCategory.getPropertyValue(BBBCoreConstants.CATEGORY_URL);			
				
					if(BBBUtility.isNotBlank(c1CategoryDisplayName)){
						c1CategoryDisplayName=c1CategoryDisplayName.trim();
					}
					
					logDebug("CheckListCategoryHierarchyCacheTool loadC1AndChildWithHierarchy:"+checkListId+", c1CategoryId:["+c1CategoryId+"],c1CategoryDisplayName:["+c1CategoryDisplayName+"]");
					
					List<GSAItem> childCategoryListItems=  (List<GSAItem>) checkListCategory.getPropertyValue(BBBCoreConstants.CHILD_CHECKLIST_CATEGORY_ID);
					
					String seoComplianceCategoryName=getCheckListTools().formatUrlParam(c1CategoryDisplayName);
					
					c2ChildOfC1Category = loadC2C3ChildWithSibling(seoUrlHierarchyList, childCategoryListItems, checkListId,
						checkListDisplayName, c1CategoryDisplayName, seoComplianceCategoryName,
						checkListSeoUrlHierarchy, false, isRegTypeCheckList, isCheckListDisabled);

					StringBuilder endecaSeoUrl=new StringBuilder(checkListId+BBBCoreConstants.TILDE);
					endecaSeoUrl.append(c1CategoryDisplayName+BBBCoreConstants.TILDE+c1CategoryId);
					
					StringBuffer buildSeoUrl= new StringBuffer( BBBCoreConstants.CHECKLIST.toLowerCase());
					buildSeoUrl.append(BBBCoreConstants.SLASH);
					buildSeoUrl.append(checkListDisplayName);
					buildSeoUrl.append(BBBCoreConstants.SLASH);
					buildSeoUrl.append(seoComplianceCategoryName);
					buildSeoUrl.append(BBBCoreConstants.SLASH);
					buildSeoUrl.append(c1CategoryId);
					buildSeoUrl.append(BBBCoreConstants.SLASH);
					buildSeoUrl.append(checkListId);
					
					checkListSeoUrlHierarchy.setSeoUrl(buildSeoUrl.toString().toLowerCase());
					
					checkListSeoUrlHierarchy.setEndecaSeoUrl(endecaSeoUrl.toString());
					
					checkListSeoUrlHierarchy.setCheckListDisplayName(checkListDisplayName);
					checkListSeoUrlHierarchy.setCheckListId(checkListId);
					checkListSeoUrlHierarchy.setCheckListCategoryName(c1CategoryDisplayName);
					checkListSeoUrlHierarchy.setChildsSeoUrls(c2ChildOfC1Category);
					checkListSeoUrlHierarchy.setCategoryLevel(1);
					if(showOnCheckList &&  !isDisable  &&  !isDeleted ){
						checkListSeoUrlHierarchy.setCategoryEnabled(true);
					}
					checkListSeoUrlHierarchy.setBabyCategoryURL(babyCategoryURL);
					checkListSeoUrlHierarchy.setCaCategoryURL(caCategoryURL);
					checkListSeoUrlHierarchy.setTbsCategoryURL(tbsCategoryURL);
					checkListSeoUrlHierarchy.setCategoryURL(categoryURL);
					checkListSeoUrlHierarchy.setRegTypeCheckList(isRegTypeCheckList);
					checkListSeoUrlHierarchy.setCheckListDisabled(isCheckListDisabled);
					
					seoUrlHierarchyList.add(checkListSeoUrlHierarchy);
				}
		}
		logDebug("CheckListCategoryHierarchyCacheTool loadC1AndChildWithHierarchy End:  tookTime:["+(System.currentTimeMillis()-startTime)+"],seoUrlHierarchyList:"+seoUrlHierarchyList.size());
		return seoUrlHierarchyList;
	}
	/**
	 * Load c2 and C3 category with child and siblings
	 * @param seoUrlHierarchyList
	 * @param childCategoryListItems
	 * @param checkListId
	 * @param checkListDisplayName
	 * @param parentCategoryDisplayName
	 * @param level
	 * @return
	 */
	protected List<CheckListSeoUrlHierarchy> loadC2C3ChildWithSibling(
			List<CheckListSeoUrlHierarchy> seoUrlHierarchyList, List<GSAItem> childCategoryListItems,
			String checkListId, String checkListDisplayName, String parentCategoryDisplayName,
			String seoComplianceParentCategoryName, CheckListSeoUrlHierarchy parentCheckListSeoUrlHierarchy,boolean isC3,boolean isRegTypeCheckList,boolean isCheckListDisabled) {
		
		List<CheckListSeoUrlHierarchy> c2c3ChildsOfC1C2=null;
		long startTime=System.currentTimeMillis();
		 
		logDebug("CheckListCategoryHierarchyCacheTool loadC2C3ChildWithSibling start:parentCategoryDisplayName["+parentCategoryDisplayName+"],seoComplianceCategoryName:[");
		List<CheckListSeoUrlHierarchy> c3ChildOfC2Category=null;
		if(childCategoryListItems != null && childCategoryListItems.size() >0)
		{ 
			c2c3ChildsOfC1C2= new ArrayList<CheckListSeoUrlHierarchy>();
			
				for(RepositoryItem checkListCategoryItem : childCategoryListItems){
					
					CheckListSeoUrlHierarchy checkListSeoUrlHierarchy= new CheckListSeoUrlHierarchy();	
					
					String c2c3CategoryId=(String) checkListCategoryItem.getRepositoryId();
					String c2c3CategoryDisplayName=(String) checkListCategoryItem.getPropertyValue(BBBCoreConstants.DISPLAY_NAME);
					List<GSAItem> childCategoryItems=  (List<GSAItem>) checkListCategoryItem.getPropertyValue(BBBCoreConstants.CHILD_CHECKLIST_CATEGORY_ID);
					
					
					Boolean showOnCheckList		=	(Boolean) checkListCategoryItem.getPropertyValue(BBBCoreConstants.SHOW_ON_CHECKLIST);					
					Boolean isDisable		=	(Boolean) checkListCategoryItem.getPropertyValue(BBBCoreConstants.IS_DISABLED);					
					Boolean isDeleted		=	(Boolean) checkListCategoryItem.getPropertyValue(BBBCoreConstants.IS_DELETED);
					
					String babyCategoryURL =(String) checkListCategoryItem.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL);
					String caCategoryURL =(String) checkListCategoryItem.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL);
					String tbsCategoryURL=(String) checkListCategoryItem.getPropertyValue(BBBCoreConstants.TBS_CATEGORY_URL);
					String categoryURL=(String) checkListCategoryItem.getPropertyValue(BBBCoreConstants.CATEGORY_URL);	
					
					if(BBBUtility.isNotBlank(c2c3CategoryDisplayName)){
						c2c3CategoryDisplayName=c2c3CategoryDisplayName.trim();
					}
					
					logDebug("CheckListCategoryHierarchyCacheTool loadC2C3ChildWithSibling:checkListId:"+checkListId+", c2c3CategoryId:["+c2c3CategoryId+"],c2c2CategoryDisplayName:["+c2c3CategoryDisplayName+"]");
					
					String categoryHierarchyName=parentCategoryDisplayName+BBBCoreConstants.TILDE+c2c3CategoryDisplayName;
					
					String seoComplianceParentNChildCategoryName= seoComplianceParentCategoryName+ BBBCoreConstants.SLASH + getCheckListTools().formatUrlParam(c2c3CategoryDisplayName);
					
					// skip if child is parent of parent after c3
					if( ! isC3 )
						{
						    
							c3ChildOfC2Category = loadC2C3ChildWithSibling(seoUrlHierarchyList, childCategoryItems, checkListId,
							checkListDisplayName, categoryHierarchyName, seoComplianceParentNChildCategoryName,
							checkListSeoUrlHierarchy, true, isRegTypeCheckList,isCheckListDisabled);
						}
					 
					
					StringBuffer buildSeoUrl= new StringBuffer( BBBCoreConstants.CHECKLIST.toLowerCase());
					buildSeoUrl.append(BBBCoreConstants.SLASH);
					buildSeoUrl.append(checkListDisplayName);
					buildSeoUrl.append(BBBCoreConstants.SLASH);
					buildSeoUrl.append(seoComplianceParentNChildCategoryName);
					buildSeoUrl.append(BBBCoreConstants.SLASH);
					buildSeoUrl.append(c2c3CategoryId);
					buildSeoUrl.append(BBBCoreConstants.SLASH);
					buildSeoUrl.append(checkListId); 
					 
					checkListSeoUrlHierarchy.setSeoUrl(buildSeoUrl.toString().toLowerCase());
					
					StringBuilder endecaSeoUrl=new StringBuilder(checkListId+BBBCoreConstants.TILDE);
					endecaSeoUrl.append(categoryHierarchyName+BBBCoreConstants.TILDE+c2c3CategoryId);
					
					checkListSeoUrlHierarchy.setEndecaSeoUrl(endecaSeoUrl.toString());
					checkListSeoUrlHierarchy.setCheckListDisplayName(checkListDisplayName);
					checkListSeoUrlHierarchy.setCheckListId(checkListId);
					checkListSeoUrlHierarchy.setChildsSeoUrls(c3ChildOfC2Category);
					checkListSeoUrlHierarchy.setCheckListCategoryName(c2c3CategoryDisplayName);
					checkListSeoUrlHierarchy.setParentSeoUrl(parentCheckListSeoUrlHierarchy);
					if(isC3){
						checkListSeoUrlHierarchy.setCategoryLevel(3);
					}else{
						checkListSeoUrlHierarchy.setCategoryLevel(2);
					}
					
					if(showOnCheckList &&  !isDisable  &&  !isDeleted ){
						checkListSeoUrlHierarchy.setCategoryEnabled(true);
					}
					
					checkListSeoUrlHierarchy.setBabyCategoryURL(babyCategoryURL);
					checkListSeoUrlHierarchy.setCaCategoryURL(caCategoryURL);
					checkListSeoUrlHierarchy.setTbsCategoryURL(tbsCategoryURL);
					checkListSeoUrlHierarchy.setCategoryURL(categoryURL);
					checkListSeoUrlHierarchy.setRegTypeCheckList(isRegTypeCheckList);
					checkListSeoUrlHierarchy.setCheckListDisabled(isCheckListDisabled);
					
					c2c3ChildsOfC1C2.add(checkListSeoUrlHierarchy);
					seoUrlHierarchyList.add(checkListSeoUrlHierarchy);
				}
			for(CheckListSeoUrlHierarchy categoryHierarchy :c2c3ChildsOfC1C2)
			{
			   
				categoryHierarchy.setSiblingsSeoUrls(c2c3ChildsOfC1C2);
			}
				
				 
				
		}
		logDebug("CheckListCategoryHierarchyCacheTool loadC2C3ChildWithSibling End: tookTime:["+(System.currentTimeMillis()-startTime)+"]");
		return c2c3ChildsOfC1C2 ;
	}
	
	
	/**
	 * performs multithreading to load information from endeca 
	 * @param seoUrlList
	 * @throws InterruptedException
	 */
	 private void populateInfoFromEndecaByMultiThreadJob(List<CheckListSeoUrlHierarchy> seoUrlList) throws InterruptedException {
		logDebug("CheckListCategoryHierarchyCacheTool populateInfoFromEndecaByMultiThreadJob start: DefaultPoolSize"+getDefaultPoolSize()); 
		long startTime= System.currentTimeMillis();
	
			
		String threadPoolSize= getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.CHECKLIST_THREAD_POOL_SIZE, getDefaultPoolSize());
		 
		logDebug("CheckListCategoryHierarchyCacheTool populateInfoFromEndecaByMultiThreadJob: DefaultPoolSize:"+getDefaultPoolSize()+",threadPoolSize:"+threadPoolSize+",seoUrlList:"+seoUrlList.size()); 
		
		int numberOfThread=Integer.parseInt(threadPoolSize);
		threadPool =Executors.newFixedThreadPool(Integer.parseInt(threadPoolSize));
		  
		int numberOfItem=seoUrlList.size();   
		int minItemsPerThread = numberOfItem / numberOfThread;  
		int maxItemsPerThread = minItemsPerThread + 1;
		int threadsWithMaxItems = numberOfItem - numberOfThread * minItemsPerThread;
		int start = 0;
		  CountDownLatch latchCount = new CountDownLatch(numberOfThread);
		
		    for (int i = 0; i < numberOfThread; i++) {
		        int itemsCount = (i < threadsWithMaxItems ? maxItemsPerThread : minItemsPerThread);
		        int end = start + itemsCount;
		        logDebug("PopulateDimAndProductCount: start::["+start+"],end:["+end+"]");
		        Runnable asyncJob = new PopulateDimAndProductCount(seoUrlList.subList(start, end),latchCount);
		        threadPool.submit(asyncJob);
		        start = end;
		    }
		    logDebug("CheckListCategoryHierarchyCacheTool populateInfoFromEndecaByMultiThreadJob : Thread started.. waiting for finish");
			
		    latchCount.await();
				
		    logDebug("CheckListCategoryHierarchyCacheTool populateInfoFromEndecaByMultiThreadJob End: tookTime:["+(System.currentTimeMillis()-startTime)+"]");
			
		}
      /**
       * This method fetch dimensionId for a URL and product count with default siteId
       * @param checkListSeoUrlHierarchy
       * @throws BBBBusinessException
       * @throws BBBSystemException
       */
	 public void populateEndecaInfoForAllSite(CheckListSeoUrlHierarchy checkListSeoUrlHierarchy){
			
		 logDebug("CheckListCategoryHierarchyCacheTool populateEndecaInfoForAllSite: checkListId:"+checkListSeoUrlHierarchy.getCheckListId()+",categoryd:"+checkListSeoUrlHierarchy.getCheckListCategoryName());
		 long startTime= System.currentTimeMillis();
		 
		 try {  
				
				Site site = getSiteContextManager().getSite(BBBCoreConstants.SITE_BAB_US);
				SiteContextImpl context = new SiteContextImpl(getSiteContextManager(), site);
				getSiteContextManager().pushSiteContext(context);
					
				  
					 String seoUrlDimId = getEndecaSearchUtil().getCatalogId(BBBEndecaConstants.CHECKLIST_CATEGORY,checkListSeoUrlHierarchy.getEndecaSeoUrl());
					 if(BBBUtility.isNotBlank(seoUrlDimId)){
						 checkListSeoUrlHierarchy.setSeoUrlDimensionId(seoUrlDimId);
						 if(BBBUtility.isBlank(checkListSeoUrlHierarchy.getCategoryURL())) {
							 checkListSeoUrlHierarchy.setProductCountUS(getEndecaSearchUtil().getProductCount(seoUrlDimId,BBBCoreConstants.SITE_BAB_US));
							 }
						 if(BBBUtility.isBlank(checkListSeoUrlHierarchy.getBabyCategoryURL())) {
							 checkListSeoUrlHierarchy.setProductCountBaby(getEndecaSearchUtil().getProductCount(seoUrlDimId,BBBCoreConstants.SITE_BBB));
						 }
						 if(BBBUtility.isBlank(checkListSeoUrlHierarchy.getCaCategoryURL())) {
							 checkListSeoUrlHierarchy.setProductCountCa(getEndecaSearchUtil().getProductCount(seoUrlDimId,BBBCoreConstants.SITE_BAB_CA));
						 }
						 checkListSeoUrlHierarchy.setProcessed(true);
					 }else{
						 logDebug("CheckListCategoryHierarchyCacheTool populateEndecaInfoForAllSite dim is null for: ["+checkListSeoUrlHierarchy.getEndecaSeoUrl()+"]");
					 }
					 
				getSiteContextManager().popSiteContext(context);
				
				} catch (SiteContextException siteContextException) {
					logError("SiteContextException occured populateEndecaInfoForAllSite due to: ",siteContextException);
				} catch (Exception exception) {
					logError("Exception occured populateEndecaInfoForAllSite due to: ",exception);
				}
			logDebug("CheckListCategoryHierarchyCacheTool populateEndecaInfoForAllSite End: tookTime:["+(System.currentTimeMillis()-startTime)+"]");
	  	}
	 
	
	
	


class PopulateDimAndProductCount implements Runnable {
		 
		private List<CheckListSeoUrlHierarchy> seoUrlSubList;
		private CountDownLatch latch;
        public PopulateDimAndProductCount(List<CheckListSeoUrlHierarchy> seoUrlList,CountDownLatch latch){
        	this.seoUrlSubList=seoUrlList;
        	this.latch=latch;
        }
        
		@Override
		public void run() {
			if(seoUrlSubList != null)
			{
			   try{
					for(CheckListSeoUrlHierarchy checkListSeoUrlHierarchy: seoUrlSubList)
						{
							populateEndecaInfoForAllSite(checkListSeoUrlHierarchy);
						}
			   }finally{ 
				   latch.countDown();
			   }
			}
		}

			
}



/**
 * @return the registryCheckListRepository
 */
public Repository getRegistryCheckListRepository() {
	return registryCheckListRepository;
}


/**
 * @param registryCheckListRepository the registryCheckListRepository to set
 */
public void setRegistryCheckListRepository(Repository registryCheckListRepository) {
	this.registryCheckListRepository = registryCheckListRepository;
}


/**
 * @return the endecaSearchUtil
 */
public EndecaSearchUtil getEndecaSearchUtil() {
	return endecaSearchUtil;
}


/**
 * @param endecaSearchUtil the endecaSearchUtil to set
 */
public void setEndecaSearchUtil(EndecaSearchUtil endecaSearchUtil) {
	this.endecaSearchUtil = endecaSearchUtil;
}


/**
 * @return the defaultPoolSize
 */
public String getDefaultPoolSize() {
	return defaultPoolSize;
}


/**
 * @param defaultPoolSize the defaultPoolSize to set
 */
public void setDefaultPoolSize(String defaultPoolSize) {
	this.defaultPoolSize = defaultPoolSize;
}


/**
 * @return the threadPool
 */
public ExecutorService getThreadPool() {
	return threadPool;
}


/**
 * @param threadPool the threadPool to set
 */
public void setThreadPool(ExecutorService threadPool) {
	this.threadPool = threadPool;
}


/**
 * @return the bbbCatalogTools
 */
public BBBCatalogTools getBbbCatalogTools() {
	return bbbCatalogTools;
}


/**
 * @param bbbCatalogTools the bbbCatalogTools to set
 */
public void setBbbCatalogTools(BBBCatalogTools bbbCatalogTools) {
	this.bbbCatalogTools = bbbCatalogTools;
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


/**
 * @return the siteContextManager
 */
public SiteContextManager getSiteContextManager() {
	return siteContextManager;
}


/**
 * @param siteContextManager the siteContextManager to set
 */
public void setSiteContextManager(SiteContextManager siteContextManager) {
	this.siteContextManager = siteContextManager;
}


/**
 * @return the objectCache
 */
public BBBObjectCache getObjectCache() {
	return objectCache;
}


/**
 * @param objectCache the objectCache to set
 */
public void setObjectCache(BBBObjectCache objectCache) {
	this.objectCache = objectCache;
}

/**
 * @return the detailLog
 */
public boolean isDetailLog() {
	return detailLog;
}



/**
 * @param detailLog the detailLog to set
 */
public void setDetailLog(boolean detailLog) {
	this.detailLog = detailLog;
}

	
}
