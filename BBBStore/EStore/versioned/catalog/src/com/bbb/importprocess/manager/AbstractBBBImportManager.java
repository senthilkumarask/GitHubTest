package com.bbb.importprocess.manager;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.transaction.TransactionManager;

import atg.deployment.server.DeploymentServer;
import atg.deployment.server.Target;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.epub.PublishingException;
import atg.epub.project.InvalidAssetException;
import atg.epub.project.Process;
import atg.epub.project.ProcessHome;
import atg.epub.project.Project;
import atg.epub.project.ProjectConstants;
import atg.nucleus.GenericService;
import atg.process.action.ActionConstants;
import atg.process.action.ActionException;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.security.Persona;
import atg.security.ThreadSecurityManager;
import atg.security.User;
import atg.service.lockmanager.LockManagerException;
import atg.userdirectory.UserDirectoryUserAuthority;
import atg.versionmanager.VersionManager;
import atg.versionmanager.WorkingContext;
import atg.versionmanager.Workspace;
import atg.versionmanager.exceptions.VersionException;
import atg.workflow.ActorAccessException;
import atg.workflow.MissingWorkflowDescriptionException;
import atg.workflow.WorkflowConstants;
import atg.workflow.WorkflowException;
import atg.workflow.WorkflowManager;
import atg.workflow.WorkflowView;

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.importprocess.tools.BBBCatalogImportConstant;
import com.bbb.importprocess.tools.BBBCatalogLoadTools;
import com.bbb.importprocess.tools.BBBPIMFeedTools;
import com.bbb.utils.BBBStringUtils;

public abstract class AbstractBBBImportManager extends GenericService {

  // -------------------------------------
  // property: taskOutcomeId
  // -------------------------------------
  private DeploymentServer mDeploymentServer;
  private String mSuccessOutcomeId;
  private String mFailureOutcomeId;
  private String mProjectName = "BBBCatalogImport-";
  private String mActiveActive;
  private String mNonActiveActive;
  private boolean mOneoffEnabled;
  private BBBCatalogLoadTools mBBBCatalogLoadTools;
  private int mAssetsBatchSize =20;

  private boolean mBatchFeed;

  private BBBPIMFeedTools mBBBPIMFeedTools;
  private String feedType;

  /**
 * @return Returns the feedType
 */
public String getFeedType() {
    return feedType;
  }

  /**
 * @param pFeedType
   *          The feedType to set
 */
public void setFeedType(String pFeedType) {
    this.feedType = pFeedType;
  }

  // -------------------------------------
  // property: transactionManager
  // -------------------------------------
  private TransactionManager mTransactionManager = null;

  /**
   * @return Returns the transactionManager.
   */
  public TransactionManager getTransactionManager() {
    return mTransactionManager;
  }

  /**
   * @param pTransactionManager
   *          The transactionManager to set.
   */
  public void setTransactionManager(final TransactionManager pTransactionManager) {
    mTransactionManager = pTransactionManager;
  }

  // -------------------------------------
  // property: versionManager
  // -------------------------------------
  private VersionManager mVersionManager = null;

  /**
   * @return Returns the versionManager.
   */
  public VersionManager getVersionManager() {
    return mVersionManager;
  }

  /**
   * @param pVersionManager
   *          The versionManager to set.
   */
  public void setVersionManager(final VersionManager pVersionManager) {
    mVersionManager = pVersionManager;
  }

  // -------------------------------------
  // property: workflowManager
  // -------------------------------------
  private WorkflowManager mWorkflowManager = null;

  /**
   * @return Returns the workflowManager.
   */
  public WorkflowManager getWorkflowManager() {
    return mWorkflowManager;
  }

  /**
   * @param pWorkflowManager
   *          The workflowManager to set.
   */
  public void setWorkflowManager(final WorkflowManager pWorkflowManager) {
    mWorkflowManager = pWorkflowManager;
  }

  // -------------------------------------
  // property: userAuthority
  // -------------------------------------
  private UserDirectoryUserAuthority mUserAuthority = null;

  /**
   * Returns the UserAuthority
   */
  public UserDirectoryUserAuthority getUserAuthority() {
    return mUserAuthority;
  }

  /**
   * Sets the UserAuthority
   */
  public void setUserAuthority(final UserDirectoryUserAuthority pUserAuthority) {
    mUserAuthority = pUserAuthority;
  }

  // -------------------------------------
  // property: personaPrefix
  // -------------------------------------
  private String mPersonaPrefix = "Profile$login$";

  /**
   * Returns the PersonaPrefix which is supplied for login
   */
  public String getPersonaPrefix() {

    return mPersonaPrefix;
  }

  /**
   * Sets the PersonaPrefix
   */
  public void setPersonaPrefix(final String pPersonaPrefix) {

    mPersonaPrefix = pPersonaPrefix;
  }

  // -------------------------------------
  // property: userName
  // -------------------------------------
  private String mUserName = "publishing";

  /**
   * Returns the UserName which is supplied upon checking and for logging in
   */
  public String getUserName() {

    return mUserName;
  }

  /**
   * Sets the UserName
   */
  public void setUserName(final String pUserName) {
    mUserName = pUserName;
  }

  /**
   * This method returns the sucess outcome id
   * 
   * @return Return the success out come ID
   */
  public String getSuccessOutcomeId() {
    return mSuccessOutcomeId;
  }

  /**
   * This method set the sucess outcome id
   * 
   * @param successOutcomeId
   */
  public void setSuccessOutcomeId(final String pSuccessOutcomeId) {
    mSuccessOutcomeId = pSuccessOutcomeId;
  }

  /**
   * This method returns the Failure outcome id
   * 
   * @return Return the success out come ID
   */
  public String getFailureOutcomeId() {
    return mFailureOutcomeId;
  }

  /**
   * This method set the Failure outcome id
   * 
   * @param mfailureOutcomeId
   */
  public void setFailureOutcomeId(final String pFailureOutcomeId) {
    mFailureOutcomeId = pFailureOutcomeId;
  }

  /**
   * @return Returns the projectName.
   */
  public String getProjectName() {
    return mProjectName;
  }

  /**
   * @param pProjectName
   *          The projectName to set.
   */
  public void setProjectName(final String pProjectName) {
    mProjectName = pProjectName;
  }

  /**
   * @return the activeActive
   */
  public String getActiveActive() {

    return mActiveActive;
  }

  /**
   * @param pActiveActive
   *          the activeActive to set
   */
  public void setActiveActive(String pActiveActive) {

    mActiveActive = pActiveActive;
  }

  /**
   * @return the nonActiveActive
   */
  public String getNonActiveActive() {
    return mNonActiveActive;
  }

  /**
   * @param pNonActiveActive
   *          the nonActiveActive to set
   */
  public void setNonActiveActive(String pNonActiveActive) {
    mNonActiveActive = pNonActiveActive;
  }

  /**
   * 
   * @return
   */
  /*
   * private Date getCurrentTime(Calendar calendar) {
   * 
   * return new Date(calendar.getTimeInMillis()); }
   */

  /**
   * 
   * @return
   */
  public BBBCatalogLoadTools getImportTools() {

    return mBBBCatalogLoadTools;
  }

  /**
   * 
   * @param pBBBCatalogLoadTools
   */
  public void setImportTools(final BBBCatalogLoadTools pBBBCatalogLoadTools) {

    mBBBCatalogLoadTools = pBBBCatalogLoadTools;
  }

  /**
   * @return the mDeploymentServer
   */
  public DeploymentServer getDeploymentServer() {
    return mDeploymentServer;
  }

  /**
   * @param mDeploymentServer
   *          the mDeploymentServer to set
   */
  public void setDeploymentServer(DeploymentServer mDeploymentServer) {
    this.mDeploymentServer = mDeploymentServer;
  }

  /**
   * 
   * @return
   */
  public boolean isBatchFeed() {

    return mBatchFeed;
  }

  /**
   * 
   * @param pIsBatchFeed
   */
  public void setBatchFeed(final boolean pIsBatchFeed) {

    mBatchFeed = pIsBatchFeed;
  }

  /**
   * @return the bBBPIMFeedTools
   */
  public BBBPIMFeedTools getPimFeedTools() {
    return mBBBPIMFeedTools;
  }

  /**
   * @param pBBBPIMFeedTools
   *          the bBBPIMFeedTools to set
   */
  public void setPimFeedTools(BBBPIMFeedTools pBBBPIMFeedTools) {
    mBBBPIMFeedTools = pBBBPIMFeedTools;
  }

  private boolean isImportDataRequired = false;
  
  public boolean isImportDataRequired() {
  return isImportDataRequired;
  }

  public void setImportDataRequired(boolean isImportDataRequired) {
  this.isImportDataRequired = isImportDataRequired;
  }
  
  /**
 * @return the mAssetsBatchSize
 */
  public int getAssetsBatchSize() {
      return mAssetsBatchSize;
  }

  /**
  * 
  * @param pBatchSize the mAssetsBatchSize to set
  */
  public void setAssetsBatchSize(final int pBatchSize) {
    mAssetsBatchSize = pBatchSize;
  }
  
  // -------------------------------------
  /**
   * This is the starting point for the service. In order to start it, the
   * executeImport() method needs to be called by another service. This method
   * begins a transaction and sets the security context on the thread for the
   * user specified in the userName property. Next, it creates a project and
   * then calls importUserData(). Next, it attempts to advance the project's
   * workflow. Finally, it unsets the security context and commits the
   * transaction.
   * 
   * @param feedIdList
   * @param string
   * 
   */
  public void executeImport(final String pFeedType, final List<String> feedIdList, final String pWorkflowName,
      final String pProjectName, final Connection pConnection) throws BBBSystemException {
    boolean rollback = true;

    if (isLoggingDebug()) {
      logDebug("Start Project Creation");
    }
    // commented as part of defect BBBSL-8700
    //final TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
    Process currentProcess = null;
    String taskOutcomeId;
    final String successOutcomeId = getSuccessOutcomeId();
    final String workflowName = pWorkflowName;
 
    
    String projectName = pProjectName;// getProjectName();
    if(feedIdList!=null && feedIdList.size()>0){
      projectName=projectName+feedIdList.toString();
    }
    try {
      if(projectName!=null){
        projectName=  projectName.replace("[", "(");
        projectName=  projectName.replace("]", ")");
      }
      currentProcess = createProject(projectName, workflowName);
      // commented as part of defect BBBSL-8700
     // transactionDemarcation.begin(getTransactionManager());
      
      //new check, do not import in case of BBBRegularProductionScheduler
      if( this.isImportDataRequired()){
        importData(pFeedType, feedIdList, pConnection,false);
      } else{
        addDeployedAssets(currentProcess);
      }
      
      if (isLoggingDebug()) {
        logDebug("Import Data Completed");
      }
      taskOutcomeId = successOutcomeId;

      /*
       * if (getImportException().isEmpty()) {
       * setTaskOutcomeId(getSuccessOutcomeId()); } else {
       * setTaskOutcomeId(getFailureOutcomeId()); }
       */
      if (isLoggingDebug()) {
        logDebug("Calling Advance Work flow with Id" + taskOutcomeId);
      }
      /*
       * Flag to check One-deployment is Enabled or not to avoid 2 staging
       * environments
       */
      if (isOneoffEnabled()) {
        if (pFeedType != null && (pFeedType.equalsIgnoreCase("BBBRegularStaging") || pFeedType.equalsIgnoreCase("BBBEmergency") && pFeedType.equalsIgnoreCase("BBBPricingStaging"))) {
          List<Target> listTargets = getDeploymentServer().getOneOffTargets();
          if (listTargets != null && !listTargets.isEmpty()) {
            Target targetSite = listTargets.get(0);
            String[] listProjectIds = new String[listTargets.size()];
            if (listProjectIds != null && listProjectIds.length > 0) {
              listProjectIds[0] = currentProcess.getProject().getId();
            }
            logDebug("calling On-off Deployement");
            if (listProjectIds != null && listProjectIds.length > 0) {
              targetSite.deployProjects(listProjectIds, false, new GregorianCalendar(), "admin");
            }
          }
        }

        if (pFeedType != null && (pFeedType.equalsIgnoreCase("BBBRegularProduction") || pFeedType.equalsIgnoreCase("BBBEmergency") && pFeedType.equalsIgnoreCase("BBBPricingProduction"))) {
          advanceWorkflow(currentProcess, taskOutcomeId);
        }
      } else {
        advanceWorkflow(currentProcess, taskOutcomeId);
      }
      if (isLoggingDebug()) {
        logDebug("Completed Task");
      }
      // here we can update the feed id's
      /*
       * for (String feedId : feedIdList) {
       * getPimFeedTools().updateFeedStatus(feedId, "CLOSED", pConnection); }
       */
      rollback = false;
    } catch (WorkflowException e) {

      if (isLoggingError()) {

        logError(BBBStringUtils.stack2string(e));
      }
      rollback = true;
      throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_WORK_FLOW_ERROR,"Workflow Exception=", e);
    } catch (ActionException e) {

      if (isLoggingDebug()) {
        logDebug("Throw Advance Workflow Exception", e);
      }
      if (isLoggingError()) {

        logError(BBBStringUtils.stack2string(e));
      }
      rollback = true;
      throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_ACTION_ERROR,"Action Exception=", e);
    } catch (BBBSystemException bse) {

      try {
        advanceWorkflow(currentProcess, getFailureOutcomeId());
      } catch (WorkflowException we) {
        if (isLoggingError()) {
          logError("Unable to delete project due to following reason");
          logError(BBBStringUtils.stack2string(we));
        }
      } catch (ActionException ae) {
        if (isLoggingError()) {
          logError("Unable to delete project due to following reason");
          logError(BBBStringUtils.stack2string(ae));
        }
      }
      if (isLoggingError()) {

        logError(BBBStringUtils.stack2string(bse));
      }
      rollback = true;
      throw bse;
    } catch (Exception e) {

      if (isLoggingError()) {

        logError(BBBStringUtils.stack2string(e));
      }
      rollback = true;
      throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_GENERIC_ERROR,"Genric Exception=", e);
    } finally {

      WorkingContext.popDevelopmentLine();
      releaseUserIdentity();
      // commented as part of defect BBBSL-8700
     /* try {
        transactionDemarcation.end(rollback);
      } catch (TransactionDemarcationException tde) {
        if (isLoggingError()) {
          
          logError("Unable to roll back due to following reason");
          logError(BBBStringUtils.stack2string(tde));
        }
      }*/
   
    }

  }

  /**
   * Create a project with provided Project Name and associated with provided
   * workflow
   * 
   * @return Process which is used by advance workflow
   * @throws VersionException
   * @throws TransactionDemarcationException
   * @throws WorkflowException
   * @throws ActionException
   * @throws Exception
   */
  public Process createProject(final String pProjectName, final String pWorkFlowName) throws BBBSystemException {

    String workspaceName;
    ProcessHome processHome;
    Process currentProcess;
    Workspace workSpace;

    // Create the project for the import.

    try {

      assumeUserIdentity();

      processHome = ProjectConstants.getPersistentHomes().getProcessHome();
      StringBuffer sbf = new StringBuffer();
      final String processName = sbf.append(pProjectName).append("[").append(getFormattedDate()).append("]").toString();

      currentProcess = processHome.createProcessForImport(processName, pWorkFlowName);

      workspaceName = currentProcess.getProject().getWorkspace();

      workSpace = getVersionManager().getWorkspaceByName(workspaceName);

      if (isLoggingDebug()) {
        logDebug("createProject: Import Project Created. Id: " + currentProcess.getProject().getId() + " Name: "
            + currentProcess.getProject().getDisplayName() + "workSpace=" + workSpace);
      }
      WorkingContext.pushDevelopmentLine(workSpace);
      if (isLoggingDebug()) {
        logDebug("Project Creation Completed");
      }

    } catch (VersionException e) {

      throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_1000,e.getMessage(), e);
    } catch (TransactionDemarcationException e) {

      throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_1001,e.getMessage(), e);
    } catch (WorkflowException e) {

      throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_1002,e.getMessage(), e);
    } catch (ActionException e) {

      throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_1003,e.getMessage(), e);
    } catch (EJBException e) {

      throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_1004,e.getMessage(), e);
    } catch (CreateException e) {

      throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_1005,e.getMessage(), e);
    }

    return currentProcess;
  }

  // -------------------------------------
  /**
   * This method advances the workflow to the next state. If using an unaltered
   * copy of the import-late or import-early workflows, then the taskOutcomeId
   * property should not need to be changed (default is '4.1.1'). If you are
   * using a different workflow or an altered version of the import-xxxx
   * workflows, then the taskOutcomeId can be found in the wdl file for the
   * respective workflow.
   * 
   * @param pProcess
   *          the atg.epub.project.Process object (the project)
   * @param pTaskOutcomeId
   */
  public void advanceWorkflow(final Process pProcess, final String pTaskOutcomeId) throws WorkflowException,
      ActionException {
    final RepositoryItem processWorkflow = pProcess.getProject().getWorkflow();
    if (processWorkflow == null) {
      throw new WorkflowException("Process workflow is missing");
    }
    final String workflowProcessName = (String) processWorkflow.getPropertyValue("processName");
    final String subjectId = pProcess.getId();

    try {
      // An alternative would be to use the global workflow view at

      final WorkflowView workflowView = getWorkflowManager().getWorkflowView(ThreadSecurityManager.currentUser());
      if (workflowProcessName != null) {

        workflowView.fireTaskOutcome(workflowProcessName, WorkflowConstants.DEFAULT_WORKFLOW_SEGMENT, subjectId,
            pTaskOutcomeId, ActionConstants.ERROR_RESPONSE_DEFAULT);
      }
    } catch (MissingWorkflowDescriptionException e) {
      if (isLoggingError()) {

        logError("SERVICE: Advance Workflow Failed: MissingWorkflowDescriptionException: ");
      }
      throw e;

    } catch (ActorAccessException e) {
      if (isLoggingError()) {
        logError("SERVICE: Advance Workflow Failed: ActorAccessException: ");
      }
      throw e;
    } catch (ActionException e) {
      if (isLoggingError()) {
        logError("SERVICE: Advance Workflow Failed: ActionException: ");
      }
      throw e;
    } catch (UnsupportedOperationException e) {
      if (isLoggingError()) {
        logError("SERVICE: Advance Workflow Failed: UnsupportedOperationException: ");
      }
      throw e;
    }
  }

  // -------------------------------------
  /**
   * This method sets the security context for the current thread so that the
   * code executes correctly against secure resources.
   * 
   * @return true if the identity was assumed, false otherwise
   */
  public boolean assumeUserIdentity() {
    if (getUserAuthority() == null) {
      return false;
    }
    final User newUser = new User();
    final Persona persona = getUserAuthority().getPersona(getPersonaPrefix() + getUserName());
    if (persona == null) {
      return false;
    }
    // create a temporary User object for the identity
    newUser.addPersona(persona);

    // replace the current User object
    ThreadSecurityManager.setThreadUser(newUser);

    return true;
  }

  // -------------------------------------
  /**
   * This method unsets the security context on the current thread.
   */
  public void releaseUserIdentity() {
    ThreadSecurityManager.setThreadUser(null);
  }

  /**
   * 
   * @return
   */
  protected Calendar getCalendar() {

    return new GregorianCalendar(Locale.getDefault());
  }

  protected String getFormattedDate() {

    Calendar cal = new GregorianCalendar();
    java.util.Date creationDate = cal.getTime();
    SimpleDateFormat date_format = new SimpleDateFormat("MMM dd,yyyy HH:mm",Locale.US);
    return date_format.format(creationDate);

  }

  /**
   * 
   * @param pFeedType
   * @param pFeedIdList
   * @throws BBBSystemException
   * @throws BBBBusinessException
   * @throws SQLException
   */

  protected abstract void importData(final String pFeedType, final List<String> pFeedIdList,
      final Connection pConnection, final boolean isProductionSchedular) throws BBBSystemException, BBBBusinessException, SQLException;

  /**
   * @return the mOneoffEnabled
   */
  public boolean isOneoffEnabled() {
    return mOneoffEnabled;
  }

  /**
   * @param mOneoffEnabled
   *          the mOneoffEnabled to set
   */
  public void setOneoffEnabled(boolean mOneoffEnabled) {
    this.mOneoffEnabled = mOneoffEnabled;
  }
  
  /**
   * @throws VersionException 
   * @throws EJBException 
   * @throws LockManagerException 
   * @throws BBBBusinessException 
   * @throws RepositoryException 
   * @throws BBBSystemException 
    * 
    */
  //TODO - When project is null ?
    protected void addDeployedAssets(Process currentProcess) throws BBBSystemException {
    
    if (isLoggingDebug()) {
      logDebug("ENTER MTHD=[addDeployedAssets]");
    }

    try{
      
      //get current project
      Project currentProject  = currentProcess.getProject();
      
      if(currentProject ==null){
          if (isLoggingError()) {
                 logError("MTHD=[addDeployedAssets] ErrorMsg=[Project can not be null at this state, there is some system exception]");
             }
             throw new BBBSystemException("InvalidStateException, project must not be null at current state");
      }
      if (isLoggingDebug()) {
        logDebug("MTHD=[addDeployedAssets] MSG=[Calling ImportTools.getStagingDeployedProjects() to]");
      }

      //return projects in descending order
      RepositoryItem[] stagingProjects = getImportTools().getStagingDeployedProjects(getFeedType());

      if (isLoggingDebug()) {
        logDebug("MTHD=[addDeployedAssets] MSG=[Return from ImportTools.getStagingDeployedProjects()]");
      }
      
      LinkedHashMap<String,String> assetURIs = new LinkedHashMap<String,String>();
   
      if(stagingProjects !=null){
      logInfo("MTHD=[addDeployedAssets] "
          + "MSG=[count of deployedProjects to be copied into BBBRegularProduction"
          + stagingProjects.length);
      } else{
      logInfo("MTHD=[addDeployedAssets] "
          + "MSG=[There is no deployedProjects to be copied into BBBRegularProduction, Return");
        //there is no data to copy into new BBBRegularProduction project so return
        return;
      }
      
      //check if this can be performance issue to add all assets from all deployed projects to single map
      //projects are in descending deploymentTime order
      for(RepositoryItem stagingProject: stagingProjects){
     
        @SuppressWarnings("unchecked")
        List<String> projectAssetsURI = (List<String>)stagingProject.
          getPropertyValue(BBBCatalogImportConstant.PROPERTY_PROJECT_ASSETS_URI);
            
        if(projectAssetsURI !=null & !projectAssetsURI.isEmpty()){
            logInfo("MTHD=[addDeployedAssets] "
                + "MSG=[reading assetsURI for deployedProjet="
                + stagingProject.getRepositoryId()
                + " projectName="+stagingProject.
                  getPropertyValue(BBBCatalogImportConstant.PROPERTY_PROJECT_NAME)
                + " deploymentTime="+stagingProject.
                  getPropertyValue(BBBCatalogImportConstant.PROPERTY_DEPLOYEMENT_TIME)
                + " assetsURI count="+projectAssetsURI.size());
          
          for(String URI: projectAssetsURI){
            if (URI!=null){
      
              String[] assetURI=URI.split(BBBCatalogImportConstant.HASH_LITERAL);
              //Do not add to map if asset is already added.
              if (!assetURIs.containsKey(assetURI[0])){
                              assetURIs.put(assetURI[0],assetURI[1]);
              }
            }
          }
        } else{
            logInfo("MTHD=[addDeployedAssets] "
                + "MSG=[There is no asset for deployedProjet="
                + stagingProject.getRepositoryId()
                + " projectName="+stagingProject.
                  getPropertyValue(BBBCatalogImportConstant.PROPERTY_PROJECT_NAME)
                + " deploymentTime="+stagingProject.
                  getPropertyValue(BBBCatalogImportConstant.PROPERTY_DEPLOYEMENT_TIME));
        }
      }
      
      //get batches of assets, assets will be added into batches to currnet project
      addAssetsBatchesIntoProject(currentProject,assetURIs);
      
    }catch(RepositoryException repEx){
           if (isLoggingError()) {
               logError(BBBStringUtils.stack2string(repEx));
           }
      throw new BBBSystemException(repEx.getMessage());
    }catch(BBBBusinessException bizEx){
           if (isLoggingError()) {
               logError(BBBStringUtils.stack2string(bizEx));
           }
           throw new BBBSystemException(bizEx.getMessage());
    }catch(BBBSystemException bSysEx){
           if (isLoggingError()) {
               logError(BBBStringUtils.stack2string(bSysEx));
           }
           throw bSysEx;
    } finally{
      if (isLoggingDebug()) {
        logDebug("EXIT MTHD=[addDeployedAssets]");
      }
    }
    
  }

  /**
   * This method returns assets keys in batches
   * It takes all assetKeys as input and returns
   * batches of assetKeys 
   * 
   * @param assetURIsKeys
   * @return
   * @throws BBBSystemException 
   */
  private void addAssetsBatchesIntoProject(Project currentProject,
      LinkedHashMap<String,String> allAssetURIs) throws BBBSystemException {

        if (isLoggingDebug()) {
          logDebug("Entering Method addAssetsBatchesIntoProject");
        }
    
    //order key set
    Set<String> allAssetURIsKeys = allAssetURIs.keySet();
      List<String> allAssetsKeys = new ArrayList<String>(allAssetURIsKeys);
      
    if(allAssetURIsKeys !=null && !allAssetURIsKeys.isEmpty() ){
      
        int startIndex =0;
        int endIndex =0;
        int totalRecords = allAssetURIsKeys.size();

        logInfo("MTHD=[addAssetsBatchesIntoProject] MSG=[ number of asset for deployment = "+totalRecords+" ]");
      
        // if total records count are less than assets batch size then add all as one
        if (getAssetsBatchSize() <= 0 || totalRecords <= getAssetsBatchSize()) {

          if (isLoggingDebug()) {
          logDebug("Exiting Method addAssetsBatchesIntoProject [Return all input assetURIsKeys as one batch]");
          }
          addAssetsURIToProject(allAssetsKeys,currentProject,allAssetURIs);
          return;
        }

        //calculate number of batches
        final double total = Math.ceil((double) totalRecords / getAssetsBatchSize());
        for (int batchCount = 0; batchCount < total; batchCount++) {

          List<String> assetsSubList = null;

          startIndex = getAssetsBatchSize() * batchCount;
          endIndex = getAssetsBatchSize() * batchCount + getAssetsBatchSize();

          if (startIndex >= totalRecords) {
            startIndex = totalRecords - 1;
            endIndex = totalRecords;
          } else if (endIndex >= totalRecords) {

            endIndex = totalRecords;
          }
          if (isLoggingDebug()) {
            logDebug("MTHD=[getAssetsBatchList] MSG=[startIndex=" + startIndex + " " + "endIndex" + endIndex);
          }
          
          assetsSubList = allAssetsKeys.subList(startIndex, endIndex);
          
         // subList.add(batchList);
          addAssetsURIToProject(assetsSubList,currentProject,allAssetURIs);
          // moved code outside addDeployedAssets method to avoid performnce hit//
        }
    }
      logDebug("Exiting Method addAssetsBatchesIntoProject");
   }

  /**
   * 
   * @param assetKeysList
   * @param currentProject
   * @param allAssetURIs
   * @throws BBBSystemException
   */
  private void addAssetsURIToProject(List<String> assetKeysList,Project currentProject,LinkedHashMap<String,String>allAssetURIs) throws BBBSystemException{

    if (isLoggingDebug()) {
      logDebug("Entering Method addAssetsToProject");
    }
      
      if(assetKeysList == null){
        logInfo("In method addAssetsToProject [assetKeysList is null]");
        return;
      }
    
      String[] assetURIArr = new String[assetKeysList.size()];
      
    if (isLoggingDebug()) {
      logDebug("Assets size is " + assetKeysList.size());
    }
      
      int count = 0;
      for (String assetKey : assetKeysList) {
         String assetValue= allAssetURIs.get(assetKey);
         
         String assetURI= assetKey+BBBCatalogImportConstant.HASH_LITERAL+assetValue;
         assetURIArr[count]=assetURI;
         count++;
      }
       
      try {
        //add assets to project
        currentProject.addAsset(assetURIArr);
        
      } catch (InvalidAssetException e) {
         if (isLoggingError()) {
                 logError(BBBStringUtils.stack2string(e));
             }
         throw new BBBSystemException(e.getMessage());
      } catch (RepositoryException e) {
         if (isLoggingError()) {
                 logError(BBBStringUtils.stack2string(e));
             }
         throw new BBBSystemException(e.getMessage());
      } catch (TransactionDemarcationException e) {
        if (isLoggingError()) {
                 logError(BBBStringUtils.stack2string(e));
             }
        throw new BBBSystemException(e.getMessage());
      } catch (LockManagerException e) {
        if (isLoggingError()) {
                 logError(BBBStringUtils.stack2string(e));
             }
        throw new BBBSystemException(e.getMessage());
      } catch (EJBException e) {
        if (isLoggingError()) {
                 logError(BBBStringUtils.stack2string(e));
             }
        throw new BBBSystemException(e.getMessage());
      } catch (VersionException e) {
        if (isLoggingError()) {
                 logError(BBBStringUtils.stack2string(e));
             }
        throw new BBBSystemException(e.getMessage());
      } catch (PublishingException e) {
        if (isLoggingError()) {
                 logError(BBBStringUtils.stack2string(e));
             }
        throw new BBBSystemException(e.getMessage());
      }finally{
        if (isLoggingDebug()) {
          logDebug("Exiting Method addAssetsToProject");
        }
      }
    
    }
}
