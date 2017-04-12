/**
 * 
 */
package com.bbb.cache.listener;

import java.util.Iterator;
import java.util.Set;

import atg.deployment.common.event.DeploymentEvent;
import atg.deployment.common.event.DeploymentEventListener;

import com.bbb.cache.BBBSearchAlgorithmDataLoader;
import com.bbb.common.BBBGenericService;
import com.bbb.framework.performance.BBBPerformanceMonitor;

/**
 * @author Sapient
 *
 */
public class BBBSearchAlgorithmEventListener extends BBBGenericService implements DeploymentEventListener {
	private boolean enabled;
	private BBBSearchAlgorithmDataLoader searchAlgorithmDataLoader;
	private String searchBoostRepository;

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
	 * @return the searchAlgorithmDataLoader
	 */
	public BBBSearchAlgorithmDataLoader getSearchAlgorithmDataLoader() {
		return searchAlgorithmDataLoader;
	}

	/**
	 * @param searchAlgorithmDataLoader the searchAlgorithmDataLoader to set
	 */
	public void setSearchAlgorithmDataLoader(BBBSearchAlgorithmDataLoader searchAlgorithmDataLoader) {
		this.searchAlgorithmDataLoader = searchAlgorithmDataLoader;
	}

	/**
	 * @return the searchBoostRepository
	 */
	public String getSearchBoostRepository() {
		return searchBoostRepository;
	}

	/**
	 * @param searchBoostRepository the searchBoostRepository to set
	 */
	public void setSearchBoostRepository(String searchBoostRepository) {
		this.searchBoostRepository = searchBoostRepository;
	}

	/* (non-Javadoc)
	 * @see atg.deployment.common.event.DeploymentEventListener#deploymentEvent(atg.deployment.common.event.DeploymentEvent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void deploymentEvent(DeploymentEvent event) {
		logDebug("BBBSearchAlgorithmEventListener | deploymentEvent starts!");
		BBBPerformanceMonitor.start("BBBSearchAlgorithmEventListener", "deploymentEvent");

		if (isEnabled() && event.getNewState() == DeploymentEvent.DEPLOYMENT_COMPLETE) {
			logDebug("[BBBSearchAlgorithmEventListener] Deployment is complete");
			logDebug("[BBBSearchAlgorithmEventListener] Deployment id " + event.getDeploymentID());
			logDebug("[BBBSearchAlgorithmEventListener] Affected Item Types:" + event.getAffectedItemTypes());

			Set<String> repositories = (Set<String>) event.getAffectedItemTypes().keySet();
			Iterator<String> iterator = repositories.iterator();
			while (iterator.hasNext()) {
				String affectedRepository = iterator.next();
				if (affectedRepository != null && this.getSearchBoostRepository().equalsIgnoreCase(affectedRepository)) {
					this.getSearchAlgorithmDataLoader().loadSearchAlgorithmmDataInLocalMap();
					break;
				}
			}
		}

		BBBPerformanceMonitor.start("BBBSearchAlgorithmEventListener", "deploymentEvent");
		logDebug("BBBSearchAlgorithmEventListener | deploymentEvent ends.");		
	}

}
