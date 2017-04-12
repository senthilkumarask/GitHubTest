package com.bbb.importprocess.manager;

import java.sql.Connection;
import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.FinderException;

import atg.epub.project.Process;
import atg.epub.project.ProcessHome;
import atg.epub.project.ProjectConstants;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.versionmanager.WorkingContext;
import atg.versionmanager.Workspace;
import atg.versionmanager.exceptions.VersionException;

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;

public class BBBPIMProdCatalogImportManager extends BBBCatalogImportManager {

	private Repository mPublishingRepository;
	
	private Workspace workspace; 

	public Repository getPublishingRepository() {
		return mPublishingRepository;
	}

	public void setPublishingRepository(Repository pPublishingRepository) {
		this.mPublishingRepository = pPublishingRepository;
	}

	public Workspace getWorkspace() {
		return workspace;
	}

	public void setWorkspace(Workspace workspace) {
		this.workspace = workspace;
	}

	private String mProjectRQL;

	public String getProjectRQL() {
		return mProjectRQL;
	}

	public void setProjectRQL(String pProjectRQL) {
		mProjectRQL = pProjectRQL;
	}

	/**
	 * Overridden method to import the data into a single project to be deployed on Production.
	 * Single project is either created or the existing one is used to import data which is 
	 * finally deployed by Project Deployment scheduler. 
	 * @param
	 * @throws BBBSystemException
	 */
	public void executeImport(final String pFeedType, final List<String> feedIdList, final String pWorkflowName,
	        final String pProjectName, final Connection pConnection) throws BBBSystemException {

		RepositoryView view;
		Process process = null;
		try {
			assumeUserIdentity();
			view = getPublishingRepository().getView("project");
			RqlStatement rql = RqlStatement.parseRqlStatement(getProjectRQL());
			Object params[] = new Object[1];
			RepositoryItem[] currentProject = rql.executeQuery(view, params);

			if (currentProject != null && currentProject.length == 1) {
				boolean isProdProjectDeploying = getPimFeedTools().isProdProjectDeploymentInProgress(pConnection, currentProject[0].getRepositoryId());
				if (isProdProjectDeploying) {
					String projectName = pProjectName + "_" + getDate();
					process = createProject(projectName, pWorkflowName);
				} else {
					ProcessHome processHome = ProjectConstants.getPersistentHomes().getProcessHome();
					process = processHome.findProcessByProjectId(currentProject[0].getRepositoryId());
				}
			} else if (currentProject != null && currentProject.length == 2) {
				ProcessHome processHome = ProjectConstants.getPersistentHomes().getProcessHome();
				process = processHome.findProcessByProjectId(currentProject[0].getRepositoryId());
			} else {
				//As no project found to import feeds, hence created a new project.
				String projectName = pProjectName + "_" + getDate();
				process = createProject(projectName, pWorkflowName);
			}

			String wkspName = process.getProject().getWorkspace();
			Workspace wksp = getVersionManager().getWorkspaceByName(wkspName);
			WorkingContext.pushDevelopmentLine(wksp);
			this.setWorkspace(wksp);
			if (isLoggingDebug()) {
				logDebug("   Before importData,  date=" + formatDate(getDate()));
			}
			importData(pFeedType, feedIdList, pConnection, true);
			if (isLoggingDebug()) {
				logDebug("   After importData,  date=" + formatDate(getDate()));
			}

			//This is for maintaining entry for feeds that was processed as part of this current project
			getPimFeedTools().updateFeedListInPrjToDeploy(process.getProject(), feedIdList.get(0), pConnection);
		} catch (RepositoryException e) {
			if(isLoggingError()) {
				logError("RepositoryException : ", e);
			}
			throw new BBBSystemException(BBBCoreErrorConstants.VERS_TOOLS_REPOSITORY_EXC, "Repository Exception : ", e);
		} catch (EJBException e) {
			if(isLoggingError()) {
				logError("EJBException : ", e);
			}
			throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_ACTION_ERROR, "EJB Exception : ", e);
		} catch (FinderException e) {
			if(isLoggingError()) {
				logError("FinderException : ", e);
			}
			throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_ACTION_ERROR, "Finder Exception : ", e);
		} catch (VersionException e) {
			if(isLoggingError()) {
				logError("VersionException : ", e);
			}
			throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_ACTION_ERROR, "Version Exception : ", e);
		} catch (Exception e) {
			if(isLoggingError()) {
				logError("Exception : ", e);
			}
			throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_GENERIC_ERROR, "General Exception : ", e);
		} finally {
			this.setWorkspace(null);
			releaseUserIdentity();
		}
	}
}