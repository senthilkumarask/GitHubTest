package com.bbb.deployment.vo;

import com.bbb.deployment.tibco.DeploymentStatusVO;
import com.bbb.framework.integration.ServiceRequestIF;

public class SubmitDeploymentStatusVO implements ServiceRequestIF  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Boolean mCacheEnabled = null;
	
	private String mServiceName = null;
	
	private DeploymentStatusVO deploymentStatusVO= null;

	/**
	 * @return the deploymentStatusVO
	 */
	public DeploymentStatusVO getDeploymentStatusVO() {
		return deploymentStatusVO;
	}

	/**
	 * @param deploymentStatusVO the deploymentStatusVO to set
	 */
	public void setDeploymentStatusVO(DeploymentStatusVO deploymentStatusVO) {
		this.deploymentStatusVO = deploymentStatusVO;
	}

	/**
	 * @return the cacheEnabled
	 */
	public final Boolean isCacheEnabled() {
		return mCacheEnabled;
	}

	/**
	 * @param pCacheEnabled the cacheEnabled to set
	 */
	public final void setCacheEnabled(Boolean pCacheEnabled) {
		mCacheEnabled = pCacheEnabled;
	}

	/**
	 * @return the serviceName
	 */
	public final String getServiceName() {
		return mServiceName;
	}

	/**
	 * @param pServiceName the serviceName to set
	 */
	public final void setServiceName(String pServiceName) {
		mServiceName = pServiceName;
	}

}
