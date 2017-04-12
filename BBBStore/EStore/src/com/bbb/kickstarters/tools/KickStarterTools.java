package com.bbb.kickstarters.tools;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.bbb.commerce.cart.utils.RepositoryPriorityComparator;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;

/**
 * This is a manager for kickStarters, which contains all the methods
 * to retrieve the data from kickStarter repository.
 * 
 * @author dwaghmare
 * 
 */

public class KickStarterTools extends BBBGenericService {

private Repository kickStarterRepository;

public static final String POPULARITEMS = "popularItems";

 public Repository getKickStarterRepository() {
			return kickStarterRepository;
		}
		
 public void setKickStarterRepository(Repository kickStarterRepository) {
			this.kickStarterRepository = kickStarterRepository;
		}


		/**
		 * getTopConsultants retrieves repository data for logged in as well as anonymous user
		 * 
		 */
	@SuppressWarnings({ "unchecked", "null" })
	public List<RepositoryItem> getKickStartersByType(String registryType,String siteId, boolean isTransient, String kickStarterType)
		throws RepositoryException {
		
		BBBPerformanceMonitor.start( KickStarterTools.class.getName() + " : " + "getKickStartersByType(registryType,siteId,isTransient,kickStarterType)");
		
		String customerStatus;
		List<RepositoryItem> kickStarters =new ArrayList<RepositoryItem>();
		
		logDebug("starting method getKickStarterDataLoggedIn, Passed parameters: "
						+ "pSiteId=" + siteId + ", registryType=" + registryType);
			RepositoryItem[] kickStarterItems =null;
			RepositoryView view = getKickStarterRepository().getView(BBBGiftRegistryConstants.KICKSTARTER);
			RqlStatement statement = RqlStatement
					.parseRqlStatement(BBBGiftRegistryConstants.RQL_QUERY_KICKSTARTERS);
		
			Object params[] = new Object[2];
			params[0] = 1;
			params[1] = kickStarterType;

			try {
				kickStarterItems = statement.executeQuery(view, params);

			} catch (IllegalArgumentException iLLArgExp) {
					
					logError(LogMessageFormatter.formatMessage(null, "getKickStarterDataLoggedIn:","catalog_1065" ),iLLArgExp);
						
		
					//kickStarterItems = null;
			}
		
			if (kickStarterItems != null) {
				kickStarters = getSortedKickStarters(kickStarterItems,registryType, siteId, isTransient);
			}
			
			BBBPerformanceMonitor.end( KickStarterTools.class.getName() + " : " + "getKickStartersByType(registryType,siteId,isTransient,kickStarterType)");
			
			return kickStarters;
		}
	
	/**
	 * This method will be used to get all top items from repository based on registry type.
	 * If registry is null then it will pull all records.
	 * @param pRegistryType - type of registry
	 * @param pSiteId - site id
	 * @exception RepositoryException - repository exception while getting an item
	 * 
	 */
	public RepositoryItem[] getPopularItems(String pRegistryType , String pSiteId)
	throws RepositoryException {
		
		BBBPerformanceMonitor.start( KickStarterTools.class.getName() + " : " + "getPopularItems");
	
	
		logDebug("Enter.KickStarterTools.getTopItems(pRegistryType,pSiteId)");
		RepositoryItem[] popularItems =null;
		List<RepositoryItem> popularRepoItems =new ArrayList<RepositoryItem>();
		RepositoryView view = getKickStarterRepository().getView(POPULARITEMS);	
		try {
			QueryBuilder queryBuilder = view.getQueryBuilder();
			Query repoQuery = queryBuilder.createUnconstrainedQuery();
			popularItems = view.executeQuery(repoQuery);
			if(popularItems!=null ){
              for(RepositoryItem repoItem : popularItems){
            	  RepositoryItem siteRepoItem = (RepositoryItem) repoItem.getPropertyValue(BBBGiftRegistryConstants.KICKSTARTER_SITES);
            	  Set<RepositoryItem>  repoRegistryItems =  (Set<RepositoryItem> )repoItem.getPropertyValue(BBBGiftRegistryConstants.REGISTRYTYPE)  ;
               	  Iterator<RepositoryItem> repoIterator = repoRegistryItems.iterator();
            	if(siteRepoItem!=null && siteRepoItem.getRepositoryId().equals(pSiteId)){            		
               	  boolean addRpoItem = false;
               	  while(repoIterator.hasNext()){
               		  RepositoryItem repoItr =  repoIterator.next();
               		  String regName = (String)repoItr.getPropertyValue(BBBGiftRegistryConstants.REGISTRY_ITEMDESCRIPTOR_SITEREPO);
               		  if(pRegistryType == null
               				  || (regName!=null && regName.contains(pRegistryType))){
                   		addRpoItem = true;                		  
                   	  }
            	}
               	 if(addRpoItem){
                  	  popularRepoItems.add(repoItem);
                   }
            	  }
            	 
              }
         	 if(popularRepoItems!=null && ! popularRepoItems.isEmpty()){         				
         		RepositoryItem[] repoArray = popularRepoItems.toArray(new RepositoryItem[popularRepoItems.size()]);
         		
				logDebug("Exit.KickStarterTools.getTopItems(pRegistryType,pSiteId)");
			
         		 BBBPerformanceMonitor.end( KickStarterTools.class.getName() + " : " + "getPopularItems");
			
         		return repoArray;
         	 }
			}else{
				logDebug("Exit.KickStarterTools.getTopItems(pRegistryType,pSiteId)");
				BBBPerformanceMonitor.end( KickStarterTools.class.getName() + " : " + "getPopularItems");
				
				 return popularItems;
			}
		} catch (IllegalArgumentException iLLArgExp) {
					
				logError("IllegalArgumentException......."+iLLArgExp.getMessage());
				
		}
		
			logDebug("Exit.KickStarterTools.getTopItems(String pRegistryType,pSiteId)");
		
		BBBPerformanceMonitor.end( KickStarterTools.class.getName() + " : " + "getPopularItems");
		 
		return null;
	}
	/**
	 * This method is used to expose Rest API for kick starter details 
	 * @param registryType - type of the registry
	 * @param siteId - site Id
	 * @param isTransient - customer status
	 * @return  List<RepositoryItem>  - list of repository items
	 * @throws RepositoryException - repository exception
	 */
	public List<RepositoryItem> getKickStartersForRest(String registryType,String siteId, boolean isTransient)
	throws RepositoryException {
		
		BBBPerformanceMonitor.start( KickStarterTools.class.getName() + " : " + "getKickStartersForRest(registryType,siteId,isTransient)");
		
		logDebug("Enter.KickStarterTools.getKickStartersForRest(String pRegistryType,pSiteId,isTransient)");
		
	String customerStatus;
	List<RepositoryItem> kickStarters =new ArrayList<RepositoryItem>();
	
		logDebug("starting method getKickStarterDataLoggedIn, Passed parameters: "
					+ "pSiteId=" + siteId + ", registryType=" + registryType);
	
		RepositoryItem[] kickStarterItems =null;
		RepositoryView view = getKickStarterRepository().getView(BBBGiftRegistryConstants.KICKSTARTER);
		RqlStatement statement = RqlStatement
				.parseRqlStatement(BBBGiftRegistryConstants.RQL_QUERY_KICKSTARTERS_REST);
	
		Object params[] = new Object[1];
		params[0] = 1;

		try {
			kickStarterItems = statement.executeQuery(view, params);

		} catch (IllegalArgumentException iLLArgExp) {
		
					logError(LogMessageFormatter.formatMessage(null, "getKickStarterDataLoggedIn:","catalog_1065" ),iLLArgExp);
	
				//kickStarterItems = null;
		}
	
		if (kickStarterItems != null) {
			kickStarters = getSortedKickStarters(kickStarterItems,registryType, siteId, isTransient);
		}
		
		
		logDebug("Exit.KickStarterTools.getKickStartersForRest(String pRegistryType,pSiteId,isTransient)");
		
		BBBPerformanceMonitor.end( KickStarterTools.class.getName() + " : " + "getKickStartersForRest(registryType,siteId,isTransient)");
		
		return kickStarters;
	}
	
	
	public List<RepositoryItem> getSortedKickStarters(RepositoryItem[] kickStarterItems, String registryType,String siteId,boolean isTransient){
		
		BBBPerformanceMonitor.start( KickStarterTools.class.getName() + " : " + "getSortedKickStarters(kickStarterItems,registryType,siteId,isTransient)");
		
	
		logDebug("Enter.KickStarterTools.getSortedKickStarters");
		
		List<RepositoryItem> kickStarters =new ArrayList<RepositoryItem>();
		Set<RepositoryItem> pickLists = new HashSet<RepositoryItem>();
		RepositoryItem pickListSite;
		String consultantSiteId=null;
		Set<RepositoryItem> registryTypes;
		for(RepositoryItem kickStarterItem:kickStarterItems ){
			pickLists =(Set<RepositoryItem>) kickStarterItem.getPropertyValue(BBBGiftRegistryConstants.KICKSTARTER_PICKLISTS);
			for(RepositoryItem pickList:pickLists ){
				List<String> consulatantRegistryTypes = new ArrayList<String>();
				pickListSite = (RepositoryItem) pickList.getPropertyValue(BBBGiftRegistryConstants.KICKSTARTER_SITES);
				consultantSiteId =(String) pickListSite.getPropertyValue(BBBGiftRegistryConstants.ID);
				String customerTypeInfo =(String) pickList.getPropertyValue(BBBGiftRegistryConstants.CONSULTANT_TYPE);
				registryTypes = (Set<RepositoryItem>) pickList.getPropertyValue(BBBGiftRegistryConstants.REGISTRYTYPES);
				if(registryType !=null && !registryType.isEmpty() && !registryTypes.isEmpty()&& !isTransient){
				for(RepositoryItem consulatantRegistryType:registryTypes){
					RepositoryItem conRegTypeRepo =((RepositoryItem) consulatantRegistryType.getPropertyValue(BBBGiftRegistryConstants.REGISTRYTYPE));
					if(conRegTypeRepo!=null){
						String regType = (String)conRegTypeRepo.getPropertyValue(BBBGiftRegistryConstants.REGISTRY_ITEMDESCRIPTOR_SITEREPO);
						if(regType!=null){
							consulatantRegistryTypes.add(regType.toLowerCase());
						}
					}
					
					}
				if((consultantSiteId!=null &&consultantSiteId.equals(siteId)) && (customerTypeInfo !=null &&((customerTypeInfo.equals(BBBGiftRegistryConstants.LOGGEDIN)) || (customerTypeInfo.equals(BBBGiftRegistryConstants.BOTH)))) && (!consulatantRegistryTypes.isEmpty()&& consulatantRegistryTypes.contains(registryType.toLowerCase()))){
					kickStarters.add(kickStarterItem);
				}
				}else if((registryType == null || registryType.isEmpty()) && !isTransient){
					if((consultantSiteId!=null &&consultantSiteId.equals(siteId)) && (customerTypeInfo !=null &&((customerTypeInfo.equals(BBBGiftRegistryConstants.LOGGEDIN)) || (customerTypeInfo.equals(BBBGiftRegistryConstants.BOTH))))){
						kickStarters.add(kickStarterItem);
					}
				}else{
					if((consultantSiteId!=null &&consultantSiteId.equals(siteId)) && (customerTypeInfo !=null &&((customerTypeInfo.equals(BBBGiftRegistryConstants.ANANYMOUS)) || (customerTypeInfo.equals(BBBGiftRegistryConstants.BOTH))))){
						if(registryTypes!=null){
						//for(int i=0; i<registryTypes.size();i++)
							kickStarters.add(kickStarterItem);
						}
					}
				}
				
			}
		}
		if(kickStarters!=null && kickStarters.size() > 1){
		 Collections.sort(kickStarters, new RepositoryPriorityComparator());
		}
		
		logDebug("Exit.KickStarterTools.getSortedKickStarters");
		
		BBBPerformanceMonitor.end( KickStarterTools.class.getName() + " : " + "getSortedKickStarters(kickStarterItems,registryType,siteId,isTransient)");
		return kickStarters;	
	}
	
	/**
	 * This method is used to get a kick starter detail by passing kick starter id
	 * @param consultantId - kick starter repository id
	 * @return  RepositoryItem  - repository item
	 * @throws RepositoryException - repository exception
	 */
	public RepositoryItem getTopConsultantDetails(String consultantId)
	throws RepositoryException {
	
	BBBPerformanceMonitor.start( KickStarterTools.class.getName() + " : " + "getTopConsultantDetails(consultantId)");
		
	RepositoryItem topConsultantDetails = null;
	
		logDebug("starting method getTopConsultantDetails, Passed parameters: "
					+ " consultantId=" + consultantId);
		RepositoryItem[] kickStarterItems =null;
		RepositoryView view = getKickStarterRepository().getView(BBBGiftRegistryConstants.KICKSTARTER);
		RqlStatement statement = RqlStatement
				.parseRqlStatement(BBBGiftRegistryConstants.RQL_QUERY_CONSULTANT_DETAILS);
	
		Object params[] = new Object[1];
		params[0] = consultantId;

		try {
			kickStarterItems = statement.executeQuery(view, params);

		} catch (IllegalArgumentException iLLArgExp) {
	
	
			logError(LogMessageFormatter.formatMessage(null, "getKickStarterDataLoggedIn:","catalog_1065" ),iLLArgExp);
					
	
		}
	
		if (kickStarterItems[0] != null) {
				topConsultantDetails =kickStarterItems[0];
		}
	
		
		logDebug("Existing method kickStarterTools.getTopConsultantDetails");
		
		
		BBBPerformanceMonitor.end( KickStarterTools.class.getName() + " : " + "getTopConsultantDetails(consultantId)");
		
		return topConsultantDetails;
	}
	
	
}