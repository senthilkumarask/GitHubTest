package com.bbb.deployment;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJBException;
import javax.ejb.FinderException;

import atg.core.util.StringUtils;
import atg.deployment.DistributedDeploymentException;
import atg.deployment.common.event.DeploymentEvent;
import atg.deployment.common.event.DeploymentEventListener;
import atg.epub.project.Project;
import atg.epub.project.ProjectConstants;
import atg.epub.project.ProjectHome;
import atg.nucleus.GenericService;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.importprocess.tools.BBBPIMFeedTools;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBStringUtils;

/**
 * 
 * @author njai13
 * 
 */
public class BBBDeploymentListener extends GenericService implements DeploymentEventListener {
  // the time interval after which deployment stops polling for ENDECA_END in
  // the polling table
  private long timeOut;
  // interval after which we need to poll the polling table for ENDECA_END
  // status
  private long interval;
  private Map<String, String> dsToPollStateMap = new HashMap<String, String>();
  private static final String ENDECA_FAIL = "EndecaFail";
  private static final String ENDECA_END = "EndecaEnd";
  private BBBDeploymentManager deploymentManager;
  private static final String IDLE = "IDLE";
  private static final String ENDECA_FAILED_DUE_TO_TIMEOUT = "ENDECA_FAILED_DUE_TO_TIMEOUT";
  private static final String ENDECA_FAILED_DUE_TO_CRONJOB = "ENDECA_FAILED_DUE_TO_CRONJOB";
  private static final String BBB_REGULAR_PRODUCTION_IMPORT = "BBBRegularProductionImport";
  private static final String BBB_PRICING_PRODUCTION_IMPORT = "BBBPricingProductionImport";
  private boolean isTimeOutForEndeca;
  private String mTargetServerToExcludeForEndeca;
  private BBBPIMFeedTools pimFeedTool;
  
  /**
   * @return the targetServerToExcludeForEndeca
   */
  public String getTargetServerToExcludeForEndeca() {
    return mTargetServerToExcludeForEndeca;
  }

  /**
   * @param pTargetServerToExcludeForEndeca
   *          the targetServerToExcludeForEndeca to set
   */
  public void setTargetServerToExcludeForEndeca(String pTargetServerToExcludeForEndeca) {
    this.mTargetServerToExcludeForEndeca = pTargetServerToExcludeForEndeca;
  }

  /**
   * @return the deploymentManager
   */
  public BBBDeploymentManager getDeploymentManager() {
    return deploymentManager;
  }

  /**
   * @param deploymentManager
   *          the deploymentManager to set
   */
  public void setDeploymentManager(BBBDeploymentManager deploymentManager) {
    this.deploymentManager = deploymentManager;
  }

  /**
   * @return the timeOut
   */
  public long getTimeOut() {
    return timeOut;
  }

  /**
   * @param timeOut
   *          the timeOut to set
   */
  public void setTimeOut(long timeOut) {
    this.timeOut = timeOut;
  }

  /**
   * @return the isTimeOutForEndeca
   */
  public boolean isTimeOutForEndeca() {
    return isTimeOutForEndeca;
  }

  /**
   * @param isTimeOutForEndeca
   *          the isTimeOutForEndeca to set
   */
  public void setTimeOutForEndeca(boolean isTimeOutForEndeca) {
    this.isTimeOutForEndeca = isTimeOutForEndeca;
  }

  /**
   * @return the interval
   */
  public long getInterval() {
    return interval;
  }

  /**
   * @param interval
   *          the interval to set
   */
  public void setInterval(long interval) {
    this.interval = interval;
  }

  /**
   * @return the dsToPollStateMap
   */
  public Map<String, String> getDsToPollStateMap() {
    return dsToPollStateMap;
  }

  /**
   * @param dsToPollStateMap
   *          the dsToPollStateMap to set
   */
  public void setDsToPollStateMap(Map<String, String> dsToPollStateMap) {
    this.dsToPollStateMap = dsToPollStateMap;
  }

  public BBBPIMFeedTools getPimFeedTool() {
    return pimFeedTool;
  }

  public void setPimFeedTool(BBBPIMFeedTools pimFeedTool) {
    this.pimFeedTool = pimFeedTool;
  }

  
  @SuppressWarnings("unchecked")
  @Override
  public void deploymentEvent(DeploymentEvent event) {
    Object eventDeploymentType = null;
    String deploymentType = null;
    final int newState = event.getNewState();
    final String newStateAsString = DeploymentEvent.stateToString(newState);
    final String deploymentId = event.getDeploymentID();
    
    if(event.getAffectedRepositories()==null || event.getAffectedRepositories().isEmpty() || !isRepositoryEligibleForEndecaIndex(event.getAffectedRepositories())){
    	if(isLoggingDebug()){
    		logDebug("Endeca indexing not eligible for AffectedRepositories");
    	}
    	return;
    }
    if(event.getAffectedItemTypes() != null) {
      deploymentType = (String) event.getAffectedItemTypes().get("endecaFeedType");
      if(StringUtils.isEmpty(deploymentType)) {
      
        deploymentType = findDeploymentType();
      }
      logDebug(" deploymentType =" + deploymentType);
      if(!StringUtils.isEmpty(deploymentType)) {
     
        event.getAffectedItemTypes().put("endecaFeedType", deploymentType);
        logDebug("******event.getAffectedItemTypes() =" + event.getAffectedItemTypes());
      }
    }

   
    if (isLoggingDebug()) {
      logDebug("New State as String :: " + newStateAsString + " state as int " + newState + " is the state "
          + "uptable?? " + event.isInterruptable());
      logDebug(" Deployment id " + deploymentId);
      logDebug("deploymentType=" + deploymentType);
      logDebug("event..getErrorMsgResourceKey()=" + event.getErrorMsgResourceKey());
    }
    
    String targetServer = event.getTarget();
   
    if (newState == DeploymentEvent.ACTIVE_APPLY_COMMITTED) {
      
      if (!deploymentManager.isEndecaSuccessFlag()  && this.isDeploymentEndecaEligible(targetServer, event)) {
    	  try {
    	    	ProjectHome home = ProjectConstants.getPersistentHomes().getProjectHome();
    	    	for (int i = 0; i < event.getDeploymentProjectIDs().length; i++) {
    			
    				Project projectDetail = home.findById(event.getDeploymentProjectIDs()[i]);
    				if (projectDetail != null && (projectDetail.getDisplayName().contains(BBB_PRICING_PRODUCTION_IMPORT))) {
    					deploymentType = updatePricingPollingTable(deploymentId + "_Start", true, deploymentId);
    				} else {
    					deploymentType = updatePollingTable(deploymentId + "_Start", true, deploymentId);
    				}
    	    	}
    	  }
    	   catch (EJBException e) {
    		 logError("catalog_2000: " + BBBStringUtils.stack2string(e));
		   } catch (FinderException e) {
			logError("catalog_2001: " + BBBStringUtils.stack2string(e));
		   }
    	  
	      if (isLoggingDebug()) {
	        logDebug("****endecaFeedType=" + deploymentType);
	        logDebug("event.getAffectedItemTypes()=" + event.getAffectedItemTypes());
	      }
      }
      if (isLoggingDebug()) {
        logDebug("state is ACTIVE_APPLY_COMMITTED " + newStateAsString + " calling Add endeca ");
      }
      if (!this.addEndeca(deploymentId, targetServer, event)) {
        if (this.isTimeOutForEndeca) {
          if (isLoggingDebug()) {
            logDebug("ENDECA FAILED due to time out !!! calling interrupt");
          }
          event.interrupt("BBBDeploymentListner interrupted the deployment", " Endeca Failed due to time out ");
          try {
  	    	ProjectHome home = ProjectConstants.getPersistentHomes().getProjectHome();
  	    	for (int i = 0; i < event.getDeploymentProjectIDs().length; i++) {
  			
  				Project projectDetail = home.findById(event.getDeploymentProjectIDs()[i]);
  				if (projectDetail != null && (projectDetail.getDisplayName().contains(BBB_PRICING_PRODUCTION_IMPORT))) {
  					updatePricingPollingTable(ENDECA_FAILED_DUE_TO_TIMEOUT, false, deploymentId);
  				} else {
  					updatePollingTable(ENDECA_FAILED_DUE_TO_TIMEOUT, false, deploymentId);
  				}
  	    	}
          }
	  	   catch (EJBException e) {
	  		  logError("catalog_2000: " + BBBStringUtils.stack2string(e));
	  	   } catch (FinderException e) {
	  		  logError("catalog_2001: " + BBBStringUtils.stack2string(e));
		   }
          
          isTimeOutForEndeca = false;
        } else {
          if (isLoggingDebug()) {
            logDebug("ENDECA FAILED due to cron job fail !!! calling interrupt");
          }
          Map<String, String> DCStatusMap = getStatusPerDC(deploymentId);
          StringBuffer interruptMessage = new StringBuffer();
          if (DCStatusMap != null && !DCStatusMap.isEmpty()) {
            Set<String> keySet = DCStatusMap.keySet();
            interruptMessage.append("Status at data center ");

            for (String key : keySet) {
              interruptMessage.append(key).append(" = ").append(DCStatusMap.get(key)).append(" ");
            }
          }
          event.interrupt("BBBDeploymentListner interrupted the deployment", " Endeca Cron Job Failed " + interruptMessage.toString());
          // updatePollingTable(ENDECA_FAILED_DUE_TO_CRONJOB, false,
          // deploymentId);
        }
        try {

          Thread.sleep(700);

          getDeploymentManager().cancel(deploymentId, true);
        } catch (InterruptedException interruptedException) {

          logError("catalog_1074: " + BBBStringUtils.stack2string(interruptedException));

        } catch (DistributedDeploymentException distexcp) {

          logError("catalog_1075: " + BBBStringUtils.stack2string(distexcp));
        }
      }

    }
   
    if (newState == DeploymentEvent.DEPLOYMENT_DELETED) {
      if (isLoggingDebug()) {
        logDebug("Event is DEPLOYMENT_DELETED removing data from EndecaDeploymentData map");
      }

      if (this.isDeploymentEndecaEligible(targetServer, event)) {
    	  try {
    	    	ProjectHome home = ProjectConstants.getPersistentHomes().getProjectHome();
    	    	for (int i = 0; i < event.getDeploymentProjectIDs().length; i++) {
    			
    				Project projectDetail = home.findById(event.getDeploymentProjectIDs()[i]);
    				if (projectDetail != null && (projectDetail.getDisplayName().contains(BBB_PRICING_PRODUCTION_IMPORT))) {
    					 updatePricingPollingTable(IDLE, false, deploymentId);
    				} else {
    					 updatePollingTable(IDLE, false, deploymentId);
    				}
    	    	}
            }
  	  	   catch (EJBException e) {
  	  		  logError("catalog_2000: " + BBBStringUtils.stack2string(e));
  	  	   } catch (FinderException e) {
  	  		  logError("catalog_2001: " + BBBStringUtils.stack2string(e));
  		   }
       
        deploymentManager.setEndecaSuccessFlag(false);
        deploymentType = null;
      }
      if (isLoggingDebug()) {
        logDebug("Event is DEPLOYMENT_DELETED and deployment is Eligible for Endeca indexing So updating polling table with IDLE status ");
      }
      if (!deploymentManager.getmEndicaDeploymentData().isEmpty()) {
        deploymentManager.getmEndicaDeploymentData().remove(deploymentId);
      }

    }
    if (this.isDeploymentEndecaEligible(targetServer, event) && (newStateAsString.contains("ERROR") || newState == DeploymentEvent.DONE_STOP)) {
      if (isLoggingDebug()) {
        logDebug("Event is " + newStateAsString
            + "and deployment is Eligible for Endeca indexing So updating polling table with IDLE status ");
      }
      try {
	    	ProjectHome home = ProjectConstants.getPersistentHomes().getProjectHome();
	    	for (int i = 0; i < event.getDeploymentProjectIDs().length; i++) {
			
				Project projectDetail = home.findById(event.getDeploymentProjectIDs()[i]);
				if (projectDetail != null && (projectDetail.getDisplayName().contains(BBB_PRICING_PRODUCTION_IMPORT))) {
					 updatePricingPollingTable(IDLE, false, deploymentId);
				} else {
					 updatePollingTable(IDLE, false, deploymentId);
				}
	    	}
      }
  	   catch (EJBException e) {
  		  logError("catalog_2000: " + BBBStringUtils.stack2string(e));
  	   } catch (FinderException e) {
  		  logError("catalog_2001: " + BBBStringUtils.stack2string(e));
	   }
     
    }

  }

  private boolean addEndeca(String deploymentId, String targetServer, DeploymentEvent event) {
    final boolean isEndecaEligible = this.isDeploymentEndecaEligible(targetServer, event);
    final boolean isSwitachableDS = deploymentManager.isSwitachableDS(deploymentId);
    final boolean isEndecaSuccessFlag = deploymentManager.isEndecaSuccessFlag();
    if (isLoggingDebug()) {
      logDebug("addEndeca Method is deployment eligible for Endeca? " + isEndecaEligible);
      logDebug(" is the datasource switchable " + isSwitachableDS);
      logDebug(isSwitachableDS ? " Has Endeca already run on first switch  " + isEndecaSuccessFlag : "");
    }

    boolean isSuccess = true;
    if (isEndecaEligible) {
      // if target is production then endeca indexing needs to happen only on
      // one switch
      if (isSwitachableDS) {
        if (!isEndecaSuccessFlag) {
          if (isLoggingDebug()) {
            logDebug(" Endeca has still to run on one on the switch Datasource on production ");
          }
          isSuccess = this.doEndecaTask(deploymentId);
          if (isLoggingDebug()) {
            logDebug("Did Endeca run successfully on switchable target ? " + isSuccess
                + " set endeca sucess flag as true");
          }
          deploymentManager.setEndecaSuccessFlag(true);
        } else if (isLoggingDebug()) {
          logDebug("Endeca has already run on 1 switch not running again ");
        }
      } else {

        if (isLoggingDebug()) {
          logDebug("target is non switchable ");
        }
        isSuccess = this.doEndecaTask(deploymentId);
        if (isLoggingDebug()) {
          logDebug("Did Endeca run successfully on non switchable target? " + isSuccess);
        }
        ;

      }
    } else if (isLoggingDebug()) {
      logDebug(targetServer
          + " is not eligible for Endeca Indexing or repositories updated in deployment are not Endeca Indexing specific ");
    }

    return isSuccess;
  }

  private boolean isDeploymentEndecaEligible(String targetServer, DeploymentEvent event) {

    if (deploymentManager.isEndecaEligible()
        && isIndexingRequiredForPreview(event.getDeploymentProjectIDs(), targetServer)) {
      return true;
    } else {
      return false;
    }

  }

  /**
   * This method do Endeca Task,it will update pooling table status with active
   * data source.
   * 
   * @param pDeploymentId
   * @return
   */
  private boolean doEndecaTask(String pDeploymentId) {

    if (isLoggingDebug()) {
      logDebug("Entring BBBDeploymentListner.doEndecaTask");
    }

    long startTime = System.currentTimeMillis();
    boolean taskDone = false;
    boolean taskNotDone = true;
    boolean isTimeOut = false;
    String dataSource = deploymentManager.getDeployIdToDSMap().get(pDeploymentId);
    String status = this.getDsToPollStateMap().get(dataSource);
    if (isLoggingDebug()) {
      logDebug("Status to update in polling table " + status + " for datasource " + dataSource);
    }

    updatePollingTable(status, true, pDeploymentId);

    do {
      isTimeOut = (System.currentTimeMillis() - startTime) >= this.timeOut;
      if (checkStatus(ENDECA_END, pDeploymentId, true)) {
        if (isLoggingDebug()) {
          logDebug("status in the polling table is " + ENDECA_END + " Endeca Indexing complete");
        }
        taskNotDone = false;
        taskDone = true;
      } else if (checkStatus(ENDECA_FAIL, pDeploymentId, false) || isTimeOut) {

        if (isLoggingDebug()) {
          logDebug(isTimeOut ? "system waited " + (System.currentTimeMillis() - startTime)
              + " milliseconds  more than timeout time  " + timeOut : "status in the polling table is ENDECA_FAIL");

        }

        taskNotDone = false;
        taskDone = false;

      } else {
        if (isLoggingDebug()) {
          logDebug("status in the polling table has not changed there is still time before we timeout.Calling sleep on thread for interval "
              + getInterval());
        }
        try {
          Thread.sleep(getInterval());
        } catch (InterruptedException e) {
          if (isLoggingError()) {
            logError(LogMessageFormatter.formatMessage(null,
                "BBBDeploymentListener.doEndecaTask() | InterruptedException ", "catalog_1076"), e);
          }
          if (isTimeOut) {
            this.isTimeOutForEndeca = true;
          }
          return false;
        }
      }

    } while (taskNotDone);

    if (isLoggingDebug()) {
      logDebug("Ending BBBDeploymentListener.doEndecaTask");
    }
    if (isTimeOut) {
      this.isTimeOutForEndeca = true;
    }
    return taskDone;
  }

  /**
   * This method checks status of pooling table.
   * 
   * @param status
   * @return
   */
  public boolean checkStatus(String status, String pDeploymentId, boolean checkStatusInBothDC) {
    if (isLoggingDebug()) {
      logDebug("Entring BBBDeploymentManager.checkStatus status to check " + status);
    }

    boolean flag = false;
    try {
      final String origDeploymentId = deploymentManager.getOriginalDeployment(pDeploymentId);
      final String dataSource = deploymentManager.getDeployIdToDSMap().get(origDeploymentId);
      flag = getPimFeedTool().getPoolingStatus(status, dataSource, checkStatusInBothDC);
    } catch (BBBSystemException e) {
      if (isLoggingError()) {
        logError(LogMessageFormatter.formatMessage(null, "BBBDeploymentManager.checkStatus() | ConnectionError ",
            "catalog_1077"), e);
      }

    } catch (BBBBusinessException e) {
      if (isLoggingError()) {
        logError(LogMessageFormatter.formatMessage(null, "BBBDeploymentManager.checkStatus() | SQLException ",
            "catalog_1078"), e);
      }

    }

    if (isLoggingDebug()) {
      logDebug("Ending BBBDeploymentManager.checkStatus");
    }

    return flag;
  }

  /**
   * This method checks status of pooling table.
   * 
   * @param status
   * @return
   */
  public Map<String, String> getStatusPerDC(String pDeploymentId) {
    if (isLoggingDebug()) {
      logDebug("Entring BBBDeploymentManager.getStatusPerDC  ");
    }

    Map<String, String> DCStatusMap = null;
    try {
      final String origDeploymentId = deploymentManager.getOriginalDeployment(pDeploymentId);
      final String dataSource = deploymentManager.getDeployIdToDSMap().get(origDeploymentId);
      DCStatusMap = getPimFeedTool().getStatusPerDC(dataSource);
    } catch (BBBSystemException e) {
      if (isLoggingError()) {
        logError(LogMessageFormatter.formatMessage(null, "BBBDeploymentManager.checkStatus() | ConnectionError ",
            "catalog_1077"), e);
      }

    } catch (BBBBusinessException e) {
      if (isLoggingError()) {
        logError(LogMessageFormatter.formatMessage(null, "BBBDeploymentManager.checkStatus() | SQLException ",
            "catalog_1078"), e);
      }

    }

    if (isLoggingDebug()) {
      logDebug("Ending BBBDeploymentManager.checkStatus");
    }

    return DCStatusMap;
  }

  /**
   * This method update pooling table status.
   * 
   * @param status
   * @param pPimStatus
   * @param pDeploymentId
   */
  public String updatePollingTable(String status, boolean pPimStatus, String pDeploymentId) {

    if (isLoggingDebug()) {
      logDebug("Entring BBBDeploymentManager.updatePollingTable with status: " + status + "Deployment ID : "
          + pDeploymentId);
    }

    try {
      final String origDeploymentId = deploymentManager.getOriginalDeployment(pDeploymentId);
      final String dataSource = deploymentManager.getDeployIdToDSMap().get(origDeploymentId);
      if (isLoggingDebug()) {
        logDebug(" dataSource: " + dataSource);
      }
     return getPimFeedTool().updatePollingStatus(status, pPimStatus, pDeploymentId, dataSource);
    } catch (BBBSystemException e) {
      if (isLoggingError()) {
        
       logError(BBBStringUtils.stack2string(e));
      }

    } catch (BBBBusinessException e) {

      if (isLoggingError()) {
        
        logError(BBBStringUtils.stack2string(e));
      }
    }

    if (isLoggingDebug()) {
      logDebug("Ending BBBDeploymentManager.updatePollingTable");
    }
   return null;
  }

  /**
   * This method update pooling table status.
   * 
   * @param status
   * @param pPimStatus
   * @param pDeploymentId
   */
  public String updatePricingPollingTable(String status, boolean pPimStatus, String pDeploymentId) {

    if (isLoggingDebug()) {
      logDebug("Entring BBBDeploymentManager.updatePollingTable with status: " + status + "Deployment ID : " + pDeploymentId);
    }

    try {
      final String origDeploymentId = deploymentManager.getOriginalDeployment(pDeploymentId);
      final String dataSource = deploymentManager.getDeployIdToDSMap().get(origDeploymentId);
      if (isLoggingDebug()) {
        logDebug(" dataSource: " + dataSource);
      }
     return getPimFeedTool().updatePricingPollingStatus(status, pPimStatus, pDeploymentId, dataSource);
    } catch (BBBSystemException e) {
      if (isLoggingError()) {
        
       logError(BBBStringUtils.stack2string(e));
      }

    } catch (BBBBusinessException e) {

      if (isLoggingError()) {
        
        logError(BBBStringUtils.stack2string(e));
      }
    }

    if (isLoggingDebug()) {
      logDebug("Ending BBBDeploymentManager.updatePollingTable");
    }
   return null;
  }
  
  /**
   * This method update pooling table status.
   * 
   * @param status
   * @param pPimStatus
   * @param pDeploymentId
   */
  public String findDeploymentType() {

    if (isLoggingDebug()) {
      logDebug("Entring BBBDeploymentManager.findDeploymentType");
    }

    try {
     
     return getPimFeedTool().findEndecaDeploymentType();
    } catch (BBBSystemException e) {
      if (isLoggingError()) {
        
        logError(BBBStringUtils.stack2string(e));
       }

    } catch (BBBBusinessException e) {

      if (isLoggingError()) {
        
        logError(BBBStringUtils.stack2string(e));
       }
    }

    if (isLoggingDebug()) {
      logDebug("Ending BBBDeploymentManager.updatePollingTable");
    }
   return null;
  }
  
  public boolean isIndexingRequiredForPreview(String[] pProjectIDs, String target) {
    if (isLoggingDebug()) {
      logDebug("Target is : " + target);
    }

    if (target.equalsIgnoreCase("staging")) {

      ProjectHome home = ProjectConstants.getPersistentHomes().getProjectHome();
      try {
        for (int i = 0; i < pProjectIDs.length; i++) {
          Project projectDetail;

          projectDetail = home.findById(pProjectIDs[i]);

          if (projectDetail != null && (projectDetail.getDisplayName().contains(BBB_REGULAR_PRODUCTION_IMPORT))) {
            if (isLoggingDebug()) {
              logDebug("Production Project Display Name::::::" + projectDetail.getDisplayName());
            }

            return false;
          }

        }
      } catch (EJBException e) {
        if (isLoggingError()) {
          logError("catalog_1080: " + BBBStringUtils.stack2string(e));
        }
      } catch (FinderException e) {
        if (isLoggingError()) {
          logError("catalog_1081: " + BBBStringUtils.stack2string(e));
        }
      }

    }

    if (isLoggingDebug()) {
      logDebug("Endeca endexing is required as target is not staging");
    }
    return true;
  }
  /**
   * The method checks if the repository name is one of the repositories for
   * which endeca indexing needs to be done
   * 
   * @param repositoryName
   * @return
   */
  public boolean isRepositoryEligibleForEndecaIndex(Set<String> pRepositoryNames) {
	  boolean isRepoEligible = false;
	  if(isLoggingDebug()){
		  logDebug("Method: isRepositoryEligibleForEndecaIndex(): BBBDeploymentListener --> Method To validate "+pRepositoryNames+" Repository is eligible for indexing");
	  }
	  for(String repositoryName: pRepositoryNames){
		  isRepoEligible= deploymentManager.isRepositoryEligibleForEndecaIndex(repositoryName);
		  if (isRepoEligible) {
			  if(isLoggingDebug()){
				  logDebug("Endeca Endexing Required for Repoistory::: "+repositoryName);
			  }
	          break;
	        }
	  }
	  return isRepoEligible;
   
  }
}