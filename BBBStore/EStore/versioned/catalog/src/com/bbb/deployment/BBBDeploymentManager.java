package com.bbb.deployment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import atg.adapter.gsa.GSARepository;
import atg.core.util.StringUtils;
import atg.deployment.Deployment;
import atg.deployment.DeploymentData;
import atg.deployment.DeploymentManager;
import atg.deployment.DeploymentOptions;
import atg.deployment.DistributedDeploymentException;
import atg.deployment.agent.Switchable;
import atg.deployment.messaging.DeploymentMessage;
import atg.deployment.repository.RepositoryDeploymentData;
import atg.naming.NameUnresolver;
import atg.nucleus.Nucleus;
import atg.repository.RepositoryException;
import atg.service.jdbc.WatcherDataSource;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.importprocess.tools.BBBPIMFeedTools;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBStringUtils;

/**
 * This class is used to customize ATG OOB Deployment.We want to sink catalog
 * data among both data center and Endeca.
 * 
 * @author ajosh8
 * 
 */

public class BBBDeploymentManager extends DeploymentManager {

  private List<String> endecaEligibleRepoList = new ArrayList<String>();
  private boolean endecaIndexingRequired;
  private boolean endecaSuccessFlag;
  private static final String HYPHEN = "-";
  private Map<String, List<DeploymentData>> mEndicaDeploymentData = new HashMap<String, List<DeploymentData>>();
  private Map<String, String> deployIdToDSMap = new HashMap<String, String>();

  private String dataSource;
  private Map<String, String> dsToPollStateMap = new HashMap<String, String>();
  private BBBPIMFeedTools pimFeedTool;

  public boolean isEndecaSuccessFlag() {
    return endecaSuccessFlag;
  }

  public void setEndecaSuccessFlag(boolean endecaSuccessFlag) {
    this.endecaSuccessFlag = endecaSuccessFlag;
  }

  public BBBPIMFeedTools getPimFeedTool() {
    return pimFeedTool;
  }

  public void setPimFeedTool(BBBPIMFeedTools pimFeedTool) {
    this.pimFeedTool = pimFeedTool;
  }

  /**
   * @return the deployIdToDSMap
   */
  public Map<String, String> getDeployIdToDSMap() {
    return deployIdToDSMap;
  }

  /**
   * @param deployIdToDSMap
   *          the deployIdToDSMap to set
   */
  public void setDeployIdToDSMap(Map<String, String> deployIdToDSMap) {
    this.deployIdToDSMap = deployIdToDSMap;
  }

  /**
   * @return the endecaIndexingRequired
   */
  public boolean isEndecaIndexingRequired() {
    return endecaIndexingRequired;
  }

  /**
   * @param endecaIndexingRequired
   *          the endecaIndexingRequired to set
   */
  public void setEndecaIndexingRequired(boolean endecaIndexingRequired) {
    this.endecaIndexingRequired = endecaIndexingRequired;
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

  /**
   * @return the endecaEligibleRepoList
   */
  public List<String> getEndecaEligibleRepoList() {
    return endecaEligibleRepoList;
  }

  /**
   * @param endecaEligibleRepoList
   *          the endecaEligibleRepoList to set
   */
  public void setEndecaEligibleRepoList(List<String> endecaEligibleRepoList) {
    this.endecaEligibleRepoList = endecaEligibleRepoList;
  }

  /**
   * @return the mEndicaDeploymentData
   */
  public Map<String, List<DeploymentData>> getmEndicaDeploymentData() {
    return mEndicaDeploymentData;
  }

  /**
   * @param mEndicaDeploymentData
   *          the mEndicaDeploymentData to set
   */
  public void setmEndicaDeploymentData(Map<String, List<DeploymentData>> mEndicaDeploymentData) {
    this.mEndicaDeploymentData = mEndicaDeploymentData;
  }

  /**
   * @return the dataSource
   */
  public String getDataSource() {
    return dataSource;
  }

  /**
   * @param dataSource
   *          the dataSource to set
   */
  public void setDataSource(String dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * This method handles processing the DeploymentMessage object which was
   * delivered as part of a JMS message.
   * 
   * @param pMessage
   *          the DeploymentMessage to process
   * @throws JMSException
   *           if any exceptions occur, they are wrapped in a JMSException
   */

  public void handleMessage(DeploymentMessage pMessage) throws RepositoryException, DistributedDeploymentException {
    if (isLoggingDebug()) {
      logDebug("Entring in BBBDeployment.handleMessage");
    }
    final String deploymentId = pMessage.getDeploymentId();

    final String status = pMessage.getStatus();
    if (isLoggingDebug()) {
      logDebug(" status in deployment manager " + status + " Deployment ID " + deploymentId);
    }

    super.handleMessage(pMessage);
    if (status.equals(Deployment.REFERENCE_UPDATE_PHASE)) {

      try {
        if (isEndecaIndexingRequired() && isEndecaEligible()) {
          if (isLoggingDebug()) {
            logDebug("sleeping thread after Reference update phase ");
          }
          Thread.sleep(15000);
          if (isLoggingDebug()) {
            logDebug("thread OUT OF  sleep  after Reference update phase ");
          }
        }
      } catch (InterruptedException e) {
        if (isLoggingError()) {
          logError(BBBStringUtils.stack2string(e));
        }
      }
    }
  }

  /**
   * This method validates whether we need to proceed for Endeca Indexing
   * process or just call super to deploy using OOTB ATG deployment
   * 
   * @return boolean
   */
  public boolean isEndecaEligible() {

    if (isLoggingDebug()) {
      logDebug("Entring BBBDeployment.isEndecaEligible Map has value? " + !mEndicaDeploymentData.isEmpty());
    }
    if (!mEndicaDeploymentData.isEmpty() && endecaIndexingRequired) {
      if (isLoggingDebug()) {
        logDebug("Deployment status contains  data from repositories that need Endeca call ");
      }
      return true;
    } else {
      if (isLoggingDebug()) {
        logDebug("Deployment or deployment status is not eligible for endeca call");
      }
      return false;
    }
  }

  /**
   * This method will take deployment data, fetches the catalog data and put it
   * into map.
   */
  public String deploy(DeploymentData pDataArray[], DeploymentOptions pOptions, String pUserDeploymentId)
      throws DistributedDeploymentException {
    if (isLoggingDebug()) {
      logDebug("Entring  BBBDeploymentManager.deploy with Deployment Data :" + pDataArray + " Deployment Options : "
          + pOptions + " DeploymentID : " + pUserDeploymentId);
    }

    // if endeca has already run on 1 switch of a switchable target then while
    // deploying on second switch don't update status
    // this is because at this time 2 deployments can run simultaneously one on
    // staging and 1 on production
    if (isEndecaIndexingRequired() && !isEndecaSuccessFlag()) {
      final List<DeploymentData> localDataArray = new ArrayList<DeploymentData>();
      int i = 0;
      final String deploymentIdWithoutHypen = this.getOriginalDeployment(pUserDeploymentId);

      do {
        if (i >= pDataArray.length)
          break;

        if (pDataArray[i] instanceof RepositoryDeploymentData) {
          final RepositoryDeploymentData repositoryDeploymentData = (RepositoryDeploymentData) pDataArray[i];
          if (repositoryDeploymentData == null) {
            continue;
          }
          final int count = repositoryDeploymentData.getMarkerCount();

          if (count > 0) {
            if (isLoggingDebug()) {
              logDebug("Marker count for source repository " + repositoryDeploymentData.getSourceRepository()
                  + " is:::" + count + " name of repository is "
                  + repositoryDeploymentData.getSourceRepository().getRepositoryName());
            }
            if (repositoryDeploymentData.getSourceRepository() != null
                && isRepositoryEligibleForEndecaIndex(repositoryDeploymentData.getSourceRepository()
                    .getRepositoryName())) {
              localDataArray.add(pDataArray[i]);
            }
          }
        }

        i++;
      } while (true);

      // if deployment has data for repositories which need Endeca indexing
      if (localDataArray != null && !localDataArray.isEmpty()) {
        // if status is ideal only then add status

        mEndicaDeploymentData.put(deploymentIdWithoutHypen, localDataArray);
        if (isLoggingDebug()) {
          logDebug("Ending BBBDeploymentManager.deploy status in polling table is ideal and deployment has data from repositories that require endeca indexing");
        }
        isSwitachableDS(pUserDeploymentId);

        return super.deploy(pDataArray, pOptions, pUserDeploymentId);

      }
      // if deployment does not have any data that need Endeca indexing then
      // call OOTB deploy
      else {
        if (isLoggingDebug()) {
          logDebug("Ending BBBDeploymentManager.deploy No data that need Endeca indexing ");
        }

        return super.deploy(pDataArray, pOptions, pUserDeploymentId);
      }

    } else {
      if (isLoggingDebug()) {
        logDebug("Endeca Indexing is not required calling super deploy");
      }
      return super.deploy(pDataArray, pOptions, pUserDeploymentId);
    }
  }

 

  /**
   * 
   * @param pDeploymentId
   * @return
   */
  public String getOriginalDeployment(String pDeploymentId) {
    String origDeploymentId = pDeploymentId;
    if (!StringUtils.isEmpty(pDeploymentId)) {
      if (pDeploymentId.indexOf(HYPHEN) > 0) {
        origDeploymentId = pDeploymentId.substring(0, pDeploymentId.indexOf(HYPHEN));
      }
    }
    return origDeploymentId;
  }

 
  /**
   * The method checks if the repository name is one of the repositories for
   * which endeca indexing needs to be done
   * 
   * @param repositoryName
   * @return
   */
  public boolean isRepositoryEligibleForEndecaIndex(String repositoryName) {
    boolean isRepoEligible = false;
    if (getEndecaEligibleRepoList() != null && !getEndecaEligibleRepoList().isEmpty()) {
      for (int i = 0; i < getEndecaEligibleRepoList().size(); i++) {
        if (repositoryName.contains(getEndecaEligibleRepoList().get(i))) {
          isRepoEligible = true;
          break;
        }
      }
      return isRepoEligible;
    } else {
      return isRepoEligible;
    }
  }

  /**
   * This method will check that data source is staging or switching.
   * 
   * @param pDeploymentId
   * @return boolean
   */
  public boolean isSwitachableDS(final String pDeploymentId) {

    if (isLoggingDebug()) {
      logDebug("Entring BBBDeploymentManager.isSwitachableDS deploymentid " + pDeploymentId);
    }

    final boolean endecaIndexingRequired = true;
    final String origDeploymentId = this.getOriginalDeployment(pDeploymentId);
    final List<DeploymentData> dataArray = (ArrayList<DeploymentData>) mEndicaDeploymentData.get(origDeploymentId);

    if (isLoggingDebug()) {
      logDebug("Deploying Catalog data: " + dataArray + " original deploymentId " + origDeploymentId);
    }

    final NameUnresolver nameUnresolver = Nucleus.getGlobalNucleus().getNameUnresolver();
    if (dataArray != null && !dataArray.isEmpty()) {

      for (int i = 0; i < dataArray.size(); i++) {
        final RepositoryDeploymentData repositoryDeploymentData = (RepositoryDeploymentData) dataArray.get(i);

        if (repositoryDeploymentData != null) {
          final GSARepository gsa = (GSARepository) Nucleus.getGlobalNucleus().resolveName(
              nameUnresolver.unresolveName(repositoryDeploymentData.getDestinationRepository()));
          if (gsa != null) {
            final javax.sql.DataSource ds = gsa.getDataSource();
            if (isLoggingDebug()) {
              logDebug("Data Source is : " + ds);
            }

            if ((ds instanceof WatcherDataSource)) {
              this.deployIdToDSMap.put(origDeploymentId, "staging");
              return false;
            } else if ((ds instanceof Switchable)) {
              final Switchable initialLiveDS = (Switchable) ds;
              this.deployIdToDSMap.put(origDeploymentId, initialLiveDS.getLiveDataStoreName());

              return true;
            }
          } else {
            if (isLoggingDebug()) {
              logDebug("Unable to resolve name for repository for deployment id" + origDeploymentId);
            }
          }
        } else {
          if (isLoggingDebug()) {
            logDebug("RepositoryDeploymentData is NULL for deployment id" + origDeploymentId);
          }

        }

      }

    } else {
      if (isLoggingDebug()) {
        logDebug("Catalog data is null for deployment id" + origDeploymentId);
      }
    }

    if (isLoggingDebug()) {
      logDebug("Exit BBBDeploymentManager.isSwitachableDS");
    }
    return endecaIndexingRequired;
  }

}
