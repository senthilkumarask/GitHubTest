package com.bbb.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import atg.nucleus.Nucleus;
import atg.nucleus.ServiceException;
import atg.nucleus.ServiceMap;
import atg.repository.MutableRepository;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;

import com.bbb.common.BBBGenericService;
import com.bbb.eph.customizer.BaseCustomizer;
import com.bbb.eph.customizer.ColorSearchTermCustomizer;
import com.bbb.framework.cache.BBBLocalCacheContainer;
import com.bbb.framework.performance.BBBPerformanceMonitor;

/**
 * changes starts for story : BBBI-393 --
 * ATG - Refresh and load product/other/color maps from BCC to coherence
 * 
 * The Class BBBSemanticDataCacheLoader.
 * This class will be used to update
 * local cache of semantic data repository.
 *  
 */
public class BBBSemanticDataCacheLoader extends BBBGenericService{
	
	private final String SEMANTIC_DATA_REPO = "semanticData";
	private final String KEYWORD_TYPE = "keywordType";
	private final String KEYWORD_TYPE_UNDERSCORE = "keywordType_";
	private final String KEYWORD_LIST = "keywordList";
			
	private MutableRepository semanticDataRepository;	
	private BBBLocalCacheContainer cacheContainer;	
	private boolean enabled;
	
	private Map<String,String> customizerMap;
	
	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the cacheContainer
	 */
	public BBBLocalCacheContainer getCacheContainer() {
		return cacheContainer;
	}

	/**
	 * @param cacheContainer the cacheContainer to set
	 */
	public void setCacheContainer(BBBLocalCacheContainer cacheContainer) {
		this.cacheContainer = cacheContainer;
	}

	/**
	 * @return the semanticDataRepository
	 */
	public MutableRepository getSemanticDataRepository() {
		return semanticDataRepository;
	}

	/**
	 * @param semanticDataRepository the semanticDataRepository to set
	 */
	public void setSemanticDataRepository(MutableRepository semanticDataRepository) {
		this.semanticDataRepository = semanticDataRepository;
	}

	
	/**
	 * This method is called from listener on BCC project deployment event
	 * and affected repository is semanticDataRepository.
	 * This will populate the data in LocalCache BBBSemanticDataCacheContainer
	 * 
	 */
	public void updateSemanticDataCache(){
		logDebug("BBBSemanticDataCacheLoader | Entering updateSemanticDataCache()");
		BBBPerformanceMonitor.start("BBBSemanticDataCacheLoader", "updateSemanticDataCache");
		
		RepositoryItem[] resultRepositoryItem = null;		
				
	    try{
	        RepositoryItemDescriptor dataDesc = this.getSemanticDataRepository().getItemDescriptor(SEMANTIC_DATA_REPO);
		    RepositoryView dataView = dataDesc.getRepositoryView();
		    QueryBuilder builder = dataView.getQueryBuilder();
	    	
		    // build query for all repository items
		    Query query = builder.createUnconstrainedQuery();
		    logDebug("query to fetch items:"+query);
		    
		    // execute the query and get the results
		    resultRepositoryItem = dataView.executeQuery(query);
		    
		    // If repository Items are not empty, put them into local cache.
			if (null != resultRepositoryItem && resultRepositoryItem.length != 0){
				for(int count=0; count<resultRepositoryItem.length; count++){
					logDebug("Cache key : "+KEYWORD_TYPE_UNDERSCORE+resultRepositoryItem[count].getPropertyValue(KEYWORD_TYPE) + 
							"cache value : "+resultRepositoryItem[count].getPropertyValue(KEYWORD_LIST));
					getCacheContainer().put(KEYWORD_TYPE_UNDERSCORE+resultRepositoryItem[count].getPropertyValue(KEYWORD_TYPE), 
							resultRepositoryItem[count].getPropertyValue(KEYWORD_LIST));
				}			 
			}			
	    }
	    catch (RepositoryException re){
	    	logError("BBBSemanticDataCacheLoader | updateSemanticDataCache | Exception", re);
			BBBPerformanceMonitor.cancel("BBBSemanticDataCacheLoader", "updateSemanticDataCache");
	    }
	    finally{
	    	BBBPerformanceMonitor.end("BBBSemanticDataCacheLoader","updateSemanticDataCache");
	    }	    
	}
	
	
	public void reInitializeRegexPattern(){
		logDebug("BBBSemanticDataCacheLoader | Entering reInitializeRegexPattern()");
		BBBPerformanceMonitor.start("BBBSemanticDataCacheLoader", "reInitializeRegexPattern");
		try{
				for(Map.Entry<String, String> entry : getCustomizerMap().entrySet()){						
					 
						Entry<String,String> customizerEntry=   entry;
						String CustomizerCompPath= customizerEntry.getValue();
					try{
						BaseCustomizer customizer=(BaseCustomizer) Nucleus.getGlobalNucleus().resolveName(CustomizerCompPath);
						logDebug("BBBSemanticDataCacheLoader | reInitializing pattern for:["+customizer.getName()+"]");
						customizer.initializeRegexPattern();
					   }
					  	catch (Exception exception){
					  		logError("BBBSemanticDataCacheLoader | Error  during initializeRegexPattern in : ["+CustomizerCompPath+"]", exception);
					  	}
				}
		}catch (Exception exception){
		    	logError("BBBSemanticDataCacheLoader | reInitializeRegexPattern()", exception);
		 }finally{
			 BBBPerformanceMonitor.start("BBBSemanticDataCacheLoader", "reInitializeRegexPattern");
			 logDebug("BBBSemanticDataCacheLoader | Entering reInitializeRegexPattern()");
		 }
		
		
	}
	
	/*
	 * This method calls updateSemanticDataCache() at server start-up.
	 * 
	 */
	@Override
	public void doStartService() throws ServiceException {
		logDebug("BBBSemanticDataCacheLoader | doStartService()");
		if (this.isEnabled()) {			
			updateSemanticDataCache();	
		} else {
			logDebug("BBBSemanticDataCacheLoader | doStartService | enabled flag is false hence semantic data caching will not be done.");
		}
	}

	/**
	 * @return the customizerMap
	 */
	public Map<String, String> getCustomizerMap() {
		return customizerMap;
	}

	/**
	 * @param customizerMap the customizerMap to set
	 */
	public void setCustomizerMap(Map<String, String> customizerMap) {
		this.customizerMap = customizerMap;
	}

	
}
