package com.bbb.cache.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import atg.deployment.common.event.DeploymentEvent;
import atg.deployment.common.event.DeploymentEventListener;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.CoherenceCacheContainer;
import com.bbb.utils.BBBUtility;



public class BCCDeploymentCacheListener extends BBBGenericService implements
		DeploymentEventListener {
	
	private static final String REPO_CACHE_MAP_TYPE = "RepoCacheMapType";
	private BBBCatalogTools mCatalogTools;
	private CoherenceCacheContainer coherenceCacheContainer;
	
	
	/**
	 * @return the coherenceCacheContainer
	 */
	public CoherenceCacheContainer getCoherenceCacheContainer() {
		return coherenceCacheContainer;
	}

	/**
	 * @param coherenceCacheContainer the coherenceCacheContainer to set
	 */
	public void setCoherenceCacheContainer(CoherenceCacheContainer coherenceCacheContainer) {
		this.coherenceCacheContainer = coherenceCacheContainer;
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
	
	@SuppressWarnings("unchecked")
	@Override
	public void deploymentEvent(DeploymentEvent event) {
		
		final String METHOD_NAME="deploymentEvent()";
		logDebug("-----"+event.getAffectedItemTypes());
		
		logInfo(getClass().toString()+":"+METHOD_NAME+":"+"Starting Clear Cache for Deployment Event");
		final String newState = DeploymentEvent.stateToString(event
				.getNewState());
		final String oldState = DeploymentEvent.stateToString(event
				.getOldState());
		final String deploymentId = event.getDeploymentID();
		logDebug("New State as String :: " + newState + " state as int "
				+ event.getNewState());
		logDebug("Old State as String :: " + oldState + " state as int "
				+ event.getOldState());
		
		if (event.getNewState() == DeploymentEvent.DEPLOYMENT_COMPLETE) {
			logDebug("Deployment is complete");
			logDebug(" Deployment id " + deploymentId);
			logDebug(" Affected Item Types:" + event.getAffectedItemTypes());
			Set<String> itemsAffected = event.getAffectedItemTypes().keySet();
			Map<String, String> validRepoCacheMap = new HashMap<String, String>();
			try {
				validRepoCacheMap = getCatalogTools().getConfigValueByconfigType(REPO_CACHE_MAP_TYPE);
			} catch (BBBSystemException | BBBBusinessException e1) {
				logError("Exception: " + e1 + "System Exception occured while fetching value for Type: REPO_CACHE_MAP_TYPE ");
			} 
			if(itemsAffected != null)
			{
				for(String item : itemsAffected)
				{
					if (item != null && isValidItem(item, validRepoCacheMap)) {
						String cacheToClear = validRepoCacheMap.get(item);						
						if(cacheToClear != null){
						logDebug("Clear Cache : "+ cacheToClear + "for item : " + item);
						getCoherenceCacheContainer().clearCache(cacheToClear);
						}
					}
				}
			}
		}
		logInfo(getClass().toString()+":"+METHOD_NAME+":"+"Completed Clear Cache for Deployment Event");
	}
	
	public boolean isValidItem(String repositoryPath, Map<String, String> validRepoCacheMap) {

		boolean validItem = false;		
		this.logDebug("Affected Repository : " +repositoryPath);
		if(!BBBUtility.isMapNullOrEmpty(validRepoCacheMap)){
			    	 validItem = validRepoCacheMap.containsKey(repositoryPath);
			    	 this.logDebug("The item "+repositoryPath +"is validItem : "+validItem);
		 }  

		return validItem;
	}
	
}