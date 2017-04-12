package com.bbb.importprocess.manager;

import java.sql.Connection;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.EJBException;
import javax.ejb.FinderException;
import atg.epub.project.Process;
import atg.epub.project.ProcessHome;
import atg.epub.project.Project;
import atg.epub.project.ProjectConstants;
import atg.epub.util.RepositoryUtils;
import atg.nucleus.GenericService;
import atg.process.action.ActionConstants;
import atg.process.action.ActionException;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.security.Persona;
import atg.security.ThreadSecurityManager;
import atg.security.User;
import atg.userdirectory.UserDirectoryUserAuthority;
import atg.versionmanager.Asset;
import atg.versionmanager.VersionManager;
import atg.versionmanager.WorkingVersion;
import atg.versionmanager.Workspace;
import atg.versionmanager.exceptions.VersionException;
import atg.versionmanager.impl.WorkingVersionRepositoryImpl;
import atg.workflow.WorkflowConstants;
import atg.workflow.WorkflowException;
import atg.workflow.WorkflowManager;
import atg.workflow.WorkflowView;

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.importprocess.tools.BBBPIMFeedTools;
import com.bbb.utils.BBBStringUtils;

public class BBBProjectDeploymentManager extends GenericService {

	public static final String PROJECT = "project";
	
	private String mSuccessOutcomeId;
	private String mFailureOutcomeId;
	private String mProjectRQL;
	
	/**
	 * 
	 * @return mProjectRQL
	 */
	public String getProjectRQL() {
		return mProjectRQL;
	}

	/**
	 * 
	 * @param pProjectRQL
	 */
	public void setProjectRQL(String pProjectRQL) {
		mProjectRQL = pProjectRQL;
	}

	private VersionManager mVersionManager = null;

	/**
	 * @return Returns the versionManager.
	 */
	public VersionManager getVersionManager() {
		return mVersionManager;
	}

	/**
	 * @param pVersionManager
	 *            The versionManager to set.
	 */
	public void setVersionManager(final VersionManager pVersionManager) {
		mVersionManager = pVersionManager;
	}

	private WorkflowManager mWorkflowManager = null;

	/**
	 * @return Returns the workflowManager.
	 */
	public WorkflowManager getWorkflowManager() {
		return mWorkflowManager;
	}

	/**
	 * @param pWorkflowManager
	 *            The workflowManager to set.
	 */
	public void setWorkflowManager(final WorkflowManager pWorkflowManager) {
		mWorkflowManager = pWorkflowManager;
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

	private Repository mPublishingRepository;

	public Repository getPublishingRepository() {
		return mPublishingRepository;
	}

	public void setPublishingRepository(Repository pPublishingRepository) {
		this.mPublishingRepository = pPublishingRepository;
	}

	private BBBPIMFeedTools bbbPIMFeedTools;

	public BBBPIMFeedTools getBbbPIMFeedTools() {
		return bbbPIMFeedTools;
	}

	public void setBbbPIMFeedTools(BBBPIMFeedTools bbbPIMFeedTools) {
		this.bbbPIMFeedTools = bbbPIMFeedTools;
	}

	/**
	 * mThrowExceptionOnAssetMergeEnabled - Property to throw Version exception on asset merge if enabled.
	 */
	private boolean mThrowExceptionOnAssetMergeEnabled;

	/**
	 * @return mThrowExceptionOnAssetMergeEnabled - Property to throw Version exception on asset merge if enabled.
	 */
	public boolean isThrowExceptionOnAssetMergeEnabled() {
		return mThrowExceptionOnAssetMergeEnabled;
	}

	/**
	 * @param pThrowExceptionOnAssetMergeEnabled sets mThrowExceptionOnAssetMergeEnabled
	 */
	public void setThrowExceptionOnAssetMergeEnabled(boolean pThrowExceptionOnAssetMergeEnabled) {
		mThrowExceptionOnAssetMergeEnabled = pThrowExceptionOnAssetMergeEnabled;
	}

	/**
	 * property: userAuthority
	 */
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

	/**
	 * property: personaPrefix
	 */
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

	/**
	 * property: userName
	 */
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

	/**
	 * This method unsets the security context on the current thread.
	 */
	public void releaseUserIdentity() {
		ThreadSecurityManager.setThreadUser(null);
	}

	/**
	 * @param pProcess
	 *            the atg.epub.project.Process object (the project)
	 * @param pTaskOutcomeId
	 */
	protected void advanceWorkflow(final Process pProcess, final String pTaskOutcomeId) throws WorkflowException,
	ActionException {
	
		if (isLoggingDebug()) {
			logDebug("      Before  pProcess.getProject(),  date=" + formatDate(getTheDate()));
		}
		final RepositoryItem processWorkflow = pProcess.getProject().getWorkflow();
		if (isLoggingDebug()) {
			logDebug("      After  pProcess.getProject(),  date=" + formatDate(getTheDate()));
		}

		if (processWorkflow == null) {
			throw new WorkflowException("Process workflow is missing");
		}
		final String workflowProcessName = (String) processWorkflow.getPropertyValue("processName");
		final String subjectId = pProcess.getId();


		if (isLoggingDebug()) {
			logDebug("      Before  getWorkflowManager().getWorkflowView(),  date="
					+ formatDate(getTheDate()));
		}
		final WorkflowView workflowView = getWorkflowManager().getWorkflowView(ThreadSecurityManager.currentUser());
		if (isLoggingDebug()) {
			logDebug("      After  getWorkflowManager().getWorkflowView(),  date=" + formatDate(getTheDate()));
		}
		if (workflowProcessName != null) {

			if (isLoggingDebug()) {
				logDebug("      Before  workflowView.fireTaskOutcome(),  date=" + formatDate(getTheDate()));
			}
			workflowView.fireTaskOutcome(workflowProcessName, WorkflowConstants.DEFAULT_WORKFLOW_SEGMENT,
					subjectId, pTaskOutcomeId, ActionConstants.ERROR_RESPONSE_DEFAULT);
			if (isLoggingDebug()) {
				logDebug("      After  workflowView.fireTaskOutcome(),  date=" + formatDate(getTheDate()));
			}
		}
	}	

	private Date getTheDate() {

		java.util.Date date = new java.util.Date();
		long t = date.getTime();
		java.sql.Date sqlDate = new java.sql.Date(t);
		// java.sql.Time sqlTime = new java.sql.Time(t);
		// java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(t);
		return sqlDate;
	}

	private String formatDate(Date date) {

		String dateAsString = null;
		Format formatter = null;
		formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
		dateAsString = formatter.format(date);

		return dateAsString;
	}

	/**
	 * It resets the base version for the assets or set the asset as deleted in case of delete operation on assets.
	 * @param assetUri
	 * @param workSpaceName
	 * @throws VersionException
	 * @throws RepositoryException
	 */
	private void mergeCurrentVersion(String assetUri, String workSpaceName) throws VersionException, RepositoryException {

		if(isLoggingDebug()) {
			logDebug("mergeCurrentVersion : Start");
		}

		WorkingVersion currentVersion = RepositoryUtils.getWorkingVersion(assetUri, workSpaceName);

		if (!currentVersion.isDeleted()) {
			RepositoryUtils.getWorkingVersion(assetUri, workSpaceName).resetParentVersion();
			if(isLoggingDebug()) {
				logDebug("After resetting  parent version in the not deleted  part");	
			}
		} else {
			Asset a = currentVersion.getAsset();
			currentVersion.revert();
			Workspace ws = getVersionManager().getWorkspaceByName(workSpaceName);
			WorkingVersion deletedVersion = ws.checkOut(a);
			deletedVersion.setDeleted(true);
			if(isLoggingDebug()) {
				logDebug("After delete   in  the delete  part");	
			}
		}

	}

	/**
	 * Method responsible for resolving assets conflicts if any and triggering the project deployment.
	 * @param pProject
	 * @param connection
	 * @throws BBBSystemException
	 */
	public void deploySingleProdProject(RepositoryItem pProject, Connection connection) throws BBBSystemException {

		if(isLoggingDebug()) {
			logDebug("deploySingleProdProject : Start");
			logDebug("deploySingleProdProject : Time before loading the project " + pProject.getItemDisplayName() + " : " + formatDate(getTheDate()));
		}
		try {
			assumeUserIdentity();
			ProcessHome processHome = ProjectConstants.getPersistentHomes().getProcessHome();
			Process currentProcess = processHome.findProcessByProjectId(pProject.getRepositoryId());
			Project project = currentProcess.getProject();
			if(isLoggingDebug()) {
				logDebug("deploySingleProdProject : Time before resolving assets conflict in the project " + project.getDisplayName() + " : " + formatDate(getTheDate()));	
			}

			//Resolve Conflicts if any
			resolveAssetConflicts(project);

			if(isLoggingDebug()) {
				logDebug("deploySingleProdProject : Time after resolving assets conflict in the project " + project.getDisplayName() + " : " + formatDate(getTheDate()));
				logDebug("deploySingleProdProject : Before advanceWorkflow,  date="+ formatDate(getTheDate()));	
			}
			advanceWorkflow(currentProcess, getSuccessOutcomeId());
			if (isLoggingDebug()) {
				logDebug("deploySingleProdProject : After advanceWorkflow,  date=" + formatDate(getTheDate()));
				logDebug("deploySingleProdProject : END ");
			}
		} catch (RepositoryException e) {
			if(isLoggingError()) {
				logError("Repository Exception : ", e);
			}
			throw new BBBSystemException("Repository Exception : ", e);
		} catch (EJBException e) {
			if(isLoggingError()) {
				logError("EJB Exception : ", e);
			}
			throw new BBBSystemException("EJB Exception : ", e);
		} catch (WorkflowException e) {
			if(isLoggingError()) {
				logError("Workflow Exception : ", e);
			}
			throw new BBBSystemException("Workflow Exception : ", e);
		} catch (FinderException e) {
			if(isLoggingError()) {
				logError("Finder Exception : ", e);
			}
			throw new BBBSystemException("Finder Exception : ", e);
		} catch (VersionException e) {
			if(isLoggingError()) {
				logError("Version Exception : ", e);
			}
			throw new BBBSystemException("Version Exception : ", e);
		} catch (ActionException e) {
			if(isLoggingError()) {
				logError("Action Exception : ", e);
			}
			throw new BBBSystemException("Action Exception : ", e);
		}  finally {
			releaseUserIdentity();
		}
	}

	/**
	 * This method is responsible for changing the status of already imported feeds to Production deployment in progress status right before deploying the project.
	 * @param pConnection
	 * @param pProjectId
	 * @throws BBBSystemException 
	 */
	public void updateImportedFeedStatus(Connection pConnection, String pProjectId, String pStatus) throws BBBSystemException {
		List<String> feedList = getBbbPIMFeedTools().getProdImportedFeedList(pConnection, pProjectId);
		if(feedList != null && !feedList.isEmpty()) {
			if(isLoggingDebug()) {
				logDebug("Feeds part of the project are as follows : " + feedList);
			}
			for(String feedId : feedList) {
					getBbbPIMFeedTools().updateFeedStatus(feedId, pStatus, pConnection);
			}
		} else {
			if(isLoggingDebug()) {
				logDebug("No Feeds are part of the project");
			}
		}
	}

	private void resolveAssetConflicts(Project pProject) throws EJBException, VersionException, RepositoryException {
		BBBPerformanceMonitor.start("project_deployment_manager", "resolve_asset_conflict");
		Format dateTimeformat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss:SSS");
		if (isLoggingDebug()) {
			logDebug("Time before checking project.assetsUpToDate() = " + dateTimeformat.format(new Date()));	
		}

		if(isThrowExceptionOnAssetMergeEnabled()) {
			if (isLoggingDebug()) {
				logDebug("Throw exception On asset merge is enabled. Hence throwing VersionException.");	
			}
			throw new VersionException("resolveAssetConflicts() : This is TEST VersionException to test exception during resolving asset conflicts.");
		}

		if (isLoggingDebug()) {
			logDebug("Time before checking the project assets are up to date() = " + dateTimeformat.format(new Date()));
		}

		if (!pProject.isAssetsUpToDate()) {
			
			if (isLoggingDebug()) {
				logDebug("Time after checking project.assetsUpToDate() = " + dateTimeformat.format(new Date()));
				logDebug("Starting merging procedure");
			}
			Set assets = pProject.getAssets();
			Iterator setIterator = assets.iterator();
			if (isLoggingDebug()) {
				logDebug("After getting all assets of project "+ dateTimeformat.format(new Date()));	
			}

			while (setIterator.hasNext()) {
				if (isLoggingDebug()) {
					logDebug("Before getting working version "+ dateTimeformat.format(new Date()));	
				}

				WorkingVersionRepositoryImpl wkv = (WorkingVersionRepositoryImpl) setIterator.next();
				
				if (isLoggingDebug()) {
					logDebug("Asset URI : " + wkv.getURIAsString());					
				}

				WorkingVersion wv = RepositoryUtils.getWorkingVersion(wkv.getURIAsString(), wkv
				        .getWorkspace().getDisplayName());
				if (isLoggingDebug()) {
					logDebug("Time before checking asset.isUpToDate() = " + dateTimeformat.format(new Date()));	
				}

				if (!wv.isUpToDate()) {
					if (isLoggingDebug()) {
						logDebug("Time after checking asset.isUpToDate() = " + dateTimeformat.format(new Date()));
						logDebug(wkv.getDisplayName() + " is out of date");
						logDebug("Time before call to resolveConflict = " + dateTimeformat.format(new Date()));
					}
					mergeCurrentVersion(wkv.getURIAsString(), wkv.getWorkspace().getDisplayName());
					if (isLoggingDebug()) {
						logDebug("Time after call to resolveConflict = " + dateTimeformat.format(new Date()));	
					}
				}
			}
			if (isLoggingDebug()) {
				logDebug("Time after checking and resolving the project assets are up to date() = " + dateTimeformat.format(new Date()));
			}
		} else {
			if (isLoggingDebug()) {
				logDebug("Assets in the project : " + pProject.getDisplayName() + " are up to date and time after checking project assets up to date = " + dateTimeformat.format(new Date()));
			}
		}

		BBBPerformanceMonitor.end("project_deployment_manager", "resolve_asset_conflict");
	}
	
	/**
	 * @return Repository item of Project to be deployed on Production.
	 * @throws BBBSystemException
	 */
	public RepositoryItem getProdProjectToDeploy() throws BBBSystemException {

		RepositoryItem project = null;
		if(isLoggingDebug()) {
			logDebug("getProdProjectListToDeploy() : Start");
		}
		try {
			RepositoryView view = getPublishingRepository().getView(PROJECT);
			RqlStatement rql = RqlStatement.parseRqlStatement(getProjectRQL());
			Object params[] = new Object[1];
			RepositoryItem[] projectsToDeploy = rql.executeQuery(view, params);
			if(projectsToDeploy != null) {
				if(projectsToDeploy.length > 1) {
					logError("There are more than 1 projects to deploy : Need exactly 1 project inorder to carry out deployment");
					throw new BBBSystemException("multiple_proj_exception", "Multiple project exist whose name starts with '" + projectsToDeploy[0].getItemDisplayName() + "'");
				}
				project = projectsToDeploy[0];
				if(isLoggingDebug()) {
					logDebug("getProdProjectListToDeploy() : Got single Project To Deploy : " + project.getItemDisplayName());
				}
			} else {
				if(isLoggingDebug()) {
					logDebug("getProdProjectListToDeploy() : No Project To Deploy hence Exiting the deployment procedure");
				}
			}
		} catch (RepositoryException re) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(re));
			}
			throw new BBBSystemException(
					BBBCoreErrorConstants.VERS_TOOLS_REPOSITORY_EXC,
					re.getMessage(), re);
		}
		if(isLoggingDebug()) {
			logDebug("getProdProjectListToDeploy() : Exit");
		}
		return project;
	}
}
