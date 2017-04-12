package com.bbb.deployment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import java.util.Set;

import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.jms.JMSException;


import atg.epub.project.ProjectConstants;
import atg.epub.project.ProjectHome;


import atg.deployment.common.event.DeploymentEvent;
import atg.deployment.common.event.DeploymentEventListener;
import atg.nucleus.GenericService;
import atg.versionmanager.exceptions.VersionException;
import atg.versionmanager.impl.WorkingVersionRepositoryImpl;

import com.bbb.deployment.vo.SubmitDeploymentStatusVO;
import com.bbb.deployment.tibco.DeploymentStatusVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;


public class BBBDeploymentMessageListener extends GenericService implements DeploymentEventListener {
 
  
  private static final String CLASS_NAME = BBBDeploymentMessageListener.class.getName();
  
  

  @SuppressWarnings("unchecked")
  @Override
  public void deploymentEvent(DeploymentEvent event) {
    
	 logDebug(" deploymentEvent starts");
    ProjectHome home = null;
    	
	home = ProjectConstants.getPersistentHomes().getProjectHome();

    final String deploymentId = event.getDeploymentID();
    logDebug(" deploymentId: " + deploymentId);
    
    Set<WorkingVersionRepositoryImpl> assetset = new HashSet<WorkingVersionRepositoryImpl>();

    try {
    	logDebug(" deploymentProjectId: " + event.getDeploymentProjectIDs()[0]);
		assetset = (home.findById(event.getDeploymentProjectIDs()[0])).getAssets();
	} catch (EJBException e1) {
		logDebug("Exception while fetching assets in deployed project " + e1.getMessage());
	} catch (FinderException e1) {
		logDebug("Exception while fetching assets in deployed project " + e1.getMessage());
	}
    
    if (event.getNewState() == DeploymentEvent.DEPLOYMENT_COMPLETE) {
    	logDebug(" deploymentId: " + deploymentId + "State: " + event.getNewState());
    	List<String> itemsAffected = new ArrayList<String>();
		Set<String> set = event.getAffectedItemTypes().keySet();
		for(WorkingVersionRepositoryImpl key: assetset)
		{
			if(key != null)
			{
				logDebug(" assetset key: " + key);
				try {
					if(key.getRepositoryItem() != null)
					itemsAffected.add(key.getRepositoryItem().getRepositoryId());
				} catch (VersionException e) {
					logDebug("Exception while fetching key from assets in deployed project " + e.getMessage());
				}
			}
		}
		try {
			submitDeploymentMesssageToTibco(deploymentId,itemsAffected);
		} catch (BBBSystemException e) {
		//	e.printStackTrace();
			logError(e.getMessage());

		}
    }
    logDebug(" deploymentEvent method ends");

  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean submitDeploymentMesssageToTibco(String deploymentId, List affectedItems) throws BBBSystemException
	{
		boolean result = false;
		DeploymentStatusVO vo = new DeploymentStatusVO();
		if(affectedItems!=null && !affectedItems.isEmpty()){
			vo.setDeploymentId(deploymentId);
			vo.setItems(affectedItems);
			try {
				result = submitDeploymentStatusMessage(vo);
			} catch (BBBSystemException e) {
				String msg = ": Error while submitting deployment Status [" + deploymentId + "] over TIBCO message bus";
				if(isLoggingError()){
                  logError("Error while submitting deployment Status [" + deploymentId + "] over TIBCO message bus", e);
              }
				throw new BBBSystemException("err_deploy_msg_tibco",msg, e);
			} catch (JMSException e) {
				String msg = ": Error while submitting deployment Status [" + deploymentId + "] over TIBCO message bus";
				if(isLoggingError()){
                  logError("Error while submitting deployment Status [" + deploymentId + "] over TIBCO message bus", e);
              }
				throw new BBBSystemException("err_deploy_msg_tibco",msg, e);
			}
		}
		return result;
	}
	private boolean submitDeploymentStatusMessage(DeploymentStatusVO deploymentStatusVO) throws JMSException, BBBSystemException {
		if (isLoggingDebug()) {
			logDebug("START: Submitting Order [" + deploymentStatusVO.getDeploymentId() + "] Message to TIBCO");
		}
		boolean result = false;

		if (deploymentStatusVO != null) {
			SubmitDeploymentStatusVO submitDeploymentStatusVO = new SubmitDeploymentStatusVO();
			submitDeploymentStatusVO.setDeploymentStatusVO(deploymentStatusVO);
			
			if (isLoggingDebug()) {
				logDebug("Submitting Order [" + deploymentStatusVO.getDeploymentId() + "] Message to TIBCO");
			}
			result = submitDeploymentStatus(submitDeploymentStatusVO);
		}

		if (isLoggingDebug()) {
			logDebug("END: Submitting Order [" + deploymentStatusVO.getDeploymentId() + "] Message to TIBCO");
		}
		return result;
	}
	
	private boolean submitDeploymentStatus(SubmitDeploymentStatusVO submitDeploymentStatusVO) throws BBBSystemException {
		final String methodName = CLASS_NAME + ".submitDeploymentStatus()";
		boolean result = false;
		if(submitDeploymentStatusVO != null && submitDeploymentStatusVO.getDeploymentStatusVO() != null) {
			String deploymentId = submitDeploymentStatusVO.getDeploymentStatusVO().getDeploymentId();
			if (isLoggingDebug()) {
				logDebug("START: Submitting deployment Status [" + deploymentId + "] over TIBCO message bus");
			}
	
			BBBPerformanceMonitor.start(BBBPerformanceConstants.MESSAGING_CALL, methodName);
	
			try {
				submitDeploymentStatusVO.setServiceName("submitDeploymentStatusMessage");
				ServiceHandlerUtil.sendTextMessage(submitDeploymentStatusVO);
				result = true;
			} catch (BBBSystemException se) {
				String msg = ": Error while submitting deployment Status [" + deploymentId + "] over TIBCO message bus";
				if(isLoggingError()){
					logError(msg, se);
				}
				throw new BBBSystemException("err_deploy_msg_tibco", msg, se);
			} catch (BBBBusinessException be) {
				String msg = ": Error while submitting deployment Status [" + deploymentId + "] over TIBCO message bus";
				if(isLoggingError()){
					logError(msg, be);
				}
				throw new BBBSystemException("err_deploy_msg_tibco",msg, be);
			} catch (Exception be) {
              String msg = ": Error while submitting deployment Status [" + deploymentId + "] over TIBCO message bus";
              if(isLoggingError()){
                  logError(msg, be);
              }
              throw new BBBSystemException("err_deploy_msg_tibco",msg, be);
          } finally {
				BBBPerformanceMonitor.end(BBBPerformanceConstants.MESSAGING_CALL, methodName);
			}
			
			if (isLoggingDebug()) {
				logDebug("END: Submitting order [" + deploymentId + "] over TIBCO message bus");
			}
		}
		return result;
	}

 
    
}