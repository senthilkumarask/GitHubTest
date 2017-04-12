package com.bbb.cache.listener;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import atg.deployment.common.event.DeploymentEvent;
import atg.deployment.common.event.DeploymentEventListener;

import com.bbb.cache.BBBSemanticDataCacheLoader;
import com.bbb.common.BBBGenericService;
import com.bbb.framework.performance.BBBPerformanceMonitor;

/* */
public class BBBSemanticCacheListener extends BBBGenericService implements DeploymentEventListener{
	
	private BBBSemanticDataCacheLoader semanticDataCacheLoader;	
	private boolean enabled;
	private List<String> repositoryList;
	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}
	/**
	 * @return the repositoryList
	 */
	public List<String> getRepositoryList() {
		return repositoryList;
	}
	/**
	 * @param repositoryList the repositoryList to set
	 */
	public void setRepositoryList(List<String> repositoryList) {
		this.repositoryList = repositoryList;
	}
	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	/**
	 * @return the semanticDataCacheLoader
	 */
	public BBBSemanticDataCacheLoader getSemanticDataCacheLoader() {
		return semanticDataCacheLoader;
	}
	/**
	 * @param semanticDataCacheLoader the semanticDataCacheLoader to set
	 */
	public void setSemanticDataCacheLoader(
			BBBSemanticDataCacheLoader semanticDataCacheLoader) {
		this.semanticDataCacheLoader = semanticDataCacheLoader;
	}
	
	public void deploymentEvent(DeploymentEvent event) {
		logDebug("BBBSemanticCacheListener | deploymentEvent starts");
		BBBPerformanceMonitor.start("BBBSemanticCacheListener", "deploymentEvent");
				
		if (isEnabled() && event.getNewState() == DeploymentEvent.DEPLOYMENT_COMPLETE) {
			logDebug("Deployment is complete");
			logDebug(" Deployment id " + event.getDeploymentID());
			logDebug(" Affected Item Types:" + event.getAffectedItemTypes());
			
				Set<String> repositories = (Set<String>)event.getAffectedItemTypes().keySet();       
				Iterator<String> iterator = repositories.iterator();				       
				while(iterator.hasNext()){              
					String affectedRepository = iterator.next();					
					if(affectedRepository != null && this.getRepositoryList().contains(affectedRepository)){
						  this.getSemanticDataCacheLoader().updateSemanticDataCache();	
						  this.getSemanticDataCacheLoader().reInitializeRegexPattern();
				          break;         
				 	}       
				}
		}
		
		BBBPerformanceMonitor.start("BBBSemanticCacheListener", "deploymentEvent");
		logDebug("BBBSemanticCacheListener | deploymentEvent ends");		
	}
	
}
