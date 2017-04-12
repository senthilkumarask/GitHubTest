package com.bbb.importprocess.listener;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.jms.JMSException;

import atg.adapter.gsa.GSARepository;
import atg.deployment.common.DeploymentException;
import atg.deployment.common.event.DeploymentEvent;
import atg.deployment.common.event.DeploymentEventListener;
import atg.dtm.TransactionDemarcationException;
import atg.epub.PublishingException;
import atg.epub.project.Project;
import atg.epub.project.ProjectConstants;
import atg.epub.project.ProjectHome;
import atg.nucleus.GenericService;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.service.email.EmailException;
import atg.service.email.SMTPEmailSender;
import atg.versionmanager.AssetVersion;
import atg.versionmanager.exceptions.VersionException;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.importprocess.tools.BBBCatalogImportConstant;
import com.bbb.importprocess.tools.BBBPIMFeedTools;
import com.bbb.utils.BBBStringUtils;


public class PIMDeploymentEventListener extends GenericService implements DeploymentEventListener {

	private static final String MANUAL_REVERT = "MANUAL_REVERT";
	private static final String ERROR_IN_DEPLOYMENT_STAG_PROJECT_STAGING = "ERROR_IN_DEPLOYMENT_STAG_PROJECT_STAGING";
	private static final String ERROR_IN_DEPLOYMENT_EMERG_PROJECT_STAGING = "ERROR_IN_DEPLOYMENT_EMERG_PROJECT_STAGING";
	private static final String ERROR_IN_STAG_SAVE_DEPLOYED_PROJECT_ASSETS = "ERROR_IN_STAG_SAVE_DEPLOYED_PROJECT_ASSETS";
	private static final String ERROR_IN_EMERG_SAVE_DEPLOYED_PROJECT_ASSETS = "ERROR_IN_EMERG_SAVE_DEPLOYED_PROJECT_ASSETS";
	private static final String ERROR_IN_DEPLOYMENT_STAG_PROJECT_ROLLBACK = "ERROR_IN_DEPLOYMENT_STAG_PROJECT_ROLLBACK";
	private static final String ERROR_IN_DEPLOYMENT_EMER_PROJECT_PRODUCTION_ROLLBACK = "ERROR_IN_DEPLOYMENT_EMER_PROJECT_PRODUCTION_ROLLBACK";
	private static final String ERROR_IN_DEPLOYMENT_EMER_PROJECT_PRODUCTION = "ERROR_IN_DEPLOYMENT_EMERG_PROJECT_PRODUCTION";
	private static final String ERROR_IN_DEPLOYMENT_PROD_PROJECT_PRODUCTION = "ERROR_IN_DEPLOYMENT_PROD_PROJECT_PRODUCTION";
	private static final String ERROR_IN_DEPLOYMENT_PROD_PROJECT_STAGING = "ERROR_IN_DEPLOYMENT_PROD_PROJECT_STAGING";
	private static final String ERROR_IN_DEPLOYMENT_PROD_PROJECT_STAGING_ROLLBACK = "ERROR_IN_DEPLOYMENT_PROD_PROJECT_STAGING_ROLLBACK";
	private static final String ERROR_IN_DEPLOYMENT_EMERG_PROJECT_ROLLBACK = "ERROR_IN_DEPLOYMENT_EMERG_PROJECT_ROLLBACK";
	private static final String ERROR_IN_DEPLOYMENT_PROD_PROJECT_PRODUCTION_ROLLBACK = "ERROR_IN_DEPLOYMENT_PROD_PROJECT_PRODUCTION_ROLLBACK";
	private static final String ERROR_IN_PRODUCTION_DELETE_DEPLOYED_PROJECT_ASSETS = "ERROR_IN_PRODUCTION_DELETE_DEPLOYED_PROJECT_ASSETS";
	private static final String PROJECT_ITEM_DESCRIPTOR = "project";
	private static final String DISPLAY_NAME_CONTAINS_AND_CHECKED_IN = "displayName CONTAINS ?0 and checkedIn = ?1 ";
	private static final String ACTIVE_LOCK = "ACTIVE_LOCK";
	private static final String DEPLOYMENT_DELETED = "DEPLOYMENT_DELETED";
	private static final String BBB_EMERGENCY_IMPORT = "BBBEmergencyImport";
	private static final String BBB_REGULAR_STAGING_IMPORT = "BBBRegularStagingImport";
	private static final String CLOSED = "CLOSED";
	private static final String BBB_REGULAR_PRODUCTION_IMPORT = "BBBRegularProductionImport";
	private static final String BBB_PRICING_STAGING_IMPORT = "BBBPricingStagingImport";
	private static final String BBB_PRICING_PRODUCTION_IMPORT = "BBBPricingProductionImport";
	private static final String EMERGENCY_IN_PROGRESS = "EMERGENCY_IN_PROGRESS";
	private static final String PRODUCTION_IN_PROGRESS = "PRODUCTION_IN_PROGRESS";
	private static final String PROD_DEP_IN_PROGRESS = "PROD_DEP_IN_PROGRESS";
	private static final String DEPLOYED_TO_STAGING = "DEPLOYED_TO_STAGING";
	private static final String STAGING_IN_PROGRESS = "STAGING_IN_PROGRESS";
	private String mStagingServer;
	private String mProductionServer;
	private BBBPIMFeedTools bbbPIMFeedTools;
	private MutableRepository mPublishingRepo;
	public static final String UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION = "2003";
	private MutableRepository mSavedProjectRepository;
	private boolean importDataRequired;
	
	public boolean isImportDataRequired() {
		return importDataRequired;
	}

	public void setImportDataRequired(boolean isImportDataRequired) {
		this.importDataRequired = isImportDataRequired;
	}

	public MutableRepository getSavedProjectRepository() {
		return mSavedProjectRepository;
	}

	public void setSavedProjectRepository(MutableRepository mSavedProjectRepository) {
		this.mSavedProjectRepository = mSavedProjectRepository;
	}
	private static final String  module = PIMDeploymentEventListener.class.getName();
	private static final ApplicationLogging logger = ClassLoggingFactory.getFactory().getLoggerForClassName(module);

	public BBBPIMFeedTools getBbbPIMFeedTools() {
		return bbbPIMFeedTools;
	}

	public void setBbbPIMFeedTools(BBBPIMFeedTools bbbPIMFeedTools) {
		this.bbbPIMFeedTools = bbbPIMFeedTools;
	}

	/**
	 * @return the mStagingServer
	 */
	public String getStagingServer() {
		return mStagingServer;
	}

	/**
	 * @param mStagingServer
	 *            the mStagingServer to set
	 */
	public void setStagingServer(String mStagingServer) {
		this.mStagingServer = mStagingServer;
	}

	/**
	 * @return the mProductionServer
	 */
	public String getProductionServer() {
		return mProductionServer;
	}

	/**
	 * @param mProductionServer
	 *            the mProductionServer to set
	 */
	public void setProductionServer(String mProductionServer) {
		this.mProductionServer = mProductionServer;
	}

	/**
	 * @return the mPublishingRepo
	 */
	public MutableRepository getPublishingRepo() {
		return mPublishingRepo;
	}

	/**
	 * @param mPublishingRepo
	 *            the mPublishingRepo to set
	 */
	public void setPublishingRepo(MutableRepository mPublishingRepo) {
		this.mPublishingRepo = mPublishingRepo;
	}
	
	  // Email Configurations and its variables
	  private SMTPEmailSender mSmtpEmailSender;

	  public SMTPEmailSender getSmtpEmailSender() {
	    return mSmtpEmailSender;
	  }

	  public void setSmtpEmailSender(SMTPEmailSender pSmtpEmailSender) {
	    this.mSmtpEmailSender = pSmtpEmailSender;
	  }
	  private String mSenderMailId;
	  
	  public String getSenderMailId() {
		    return mSenderMailId;
		  }

	  public void setSenderMailId(String pSenderMailId) {
		this.mSenderMailId = pSenderMailId;
	  }	
	  
	  private String[] mRecieverMailId;
	  /**
	   * @return the mRecieverMailIdS
	   */
	  public String[] getRecieverMailId() {
		  return mRecieverMailId;
	  }

	  /**
	   * @param mRecieverMailIdS
	   *          the mRecieverMailIdS to set
	   */
	  public void setRecieverMailId(String[] mRecieverMailId) {
		  this.mRecieverMailId = mRecieverMailId;
	  }

	@SuppressWarnings("static-access")
	@Override
	public void deploymentEvent(DeploymentEvent pEvent) {
		try {
			ProjectHome home = ProjectConstants.getPersistentHomes().getProjectHome();
			
			//When importData is disabled for BBBProductionScheduler then persist projects in DeployedProjectRepository
			if(!this.isImportDataRequired()){
				//when deployment is complete save deployed project assets
				if(!pEvent.isDeploymentRevert() && !pEvent.isDeploymentRollback() && pEvent.stateToString(pEvent.getNewState()).endsWith(DEPLOYMENT_DELETED)){
					saveDeployedProjectAssets(pEvent);
				}
			}
			
			for (int i = 0; i < pEvent.getDeploymentProjectIDs().length; i++) {
				Project projectDetail = home.findById(pEvent.getDeploymentProjectIDs()[i]);
				if(pEvent.getTarget().equalsIgnoreCase(getProductionServer()) && pEvent.stateToString(pEvent.getNewState()).endsWith(ACTIVE_LOCK)){
					if (projectDetail != null && (projectDetail.getDisplayName().contains(BBB_PRICING_STAGING_IMPORT))) {
						Object[] params = new Object[2];
						params[0] = BBB_PRICING_STAGING_IMPORT;
						params[1] = true;
						final RepositoryItem[] projectItems = this.executeRQLQuery(DISPLAY_NAME_CONTAINS_AND_CHECKED_IN, params, PROJECT_ITEM_DESCRIPTOR, getPublishingRepo());
						if (projectItems != null) {
							for (int j = 0; j < projectItems.length; j++) {
								final Project projectPricing = home.findById(projectItems[j].getRepositoryId());
								if (isLoggingDebug()) {
									logDebug("Project Checked In Date:::::::::::" + projectPricing.getCheckinDate());
									logDebug("Project Checkedin Status:::::::::::" + projectPricing.isCheckedIn());
								}
								projectPricing.delete("Publishing");
								final GSARepository gsa=(GSARepository)getPublishingRepo();
								if(gsa!=null){
								gsa.invalidateCaches();
								}
							}
						}
					}else {
						Object[] params = new Object[2];
						params[0] = BBB_REGULAR_STAGING_IMPORT;
						params[1] = true;
						final RepositoryItem[] projectItems = this.executeRQLQuery(DISPLAY_NAME_CONTAINS_AND_CHECKED_IN, params, PROJECT_ITEM_DESCRIPTOR, getPublishingRepo());
						if (projectItems != null) {
							for (int j = 0; j < projectItems.length; j++) {
								final Project projectCatalog = home.findById(projectItems[j].getRepositoryId());
								if (isLoggingDebug()) {
									logDebug("Project Checked In Date:::::::::::" + projectCatalog.getCheckinDate());
									logDebug("Project Checkedin Status:::::::::::" + projectCatalog.isCheckedIn());
								}
								projectCatalog.delete("Publishing");
								final GSARepository gsa=(GSARepository)getPublishingRepo();
								if(gsa!=null){
								gsa.invalidateCaches();
								}
							}
						}
					}
				}
			}
				
			if (isLoggingDebug()) {
				logDebug("PIM feed Eventlistener");
				logDebug("New state is " + pEvent.getNewState());
				logDebug("New state string is " + DeploymentEvent.stateToString(pEvent.getNewState()));
				logDebug("Old state string is " + DeploymentEvent.stateToString(pEvent.getOldState()));
				logDebug("Deployment string " + pEvent.toString());
				if (pEvent.getNewState() == 6 || DeploymentEvent.stateToString(pEvent.getNewState()).startsWith("ERROR")) {
					logDebug("new could identify state " + pEvent.getErrorMessage());
				}
				if (pEvent.getOldState() == 6 || DeploymentEvent.stateToString(pEvent.getOldState()).startsWith("ERROR")) {
					logDebug("old could identify state " + pEvent.getErrorMessage());
				}
				logDebug("Exiting PIM Feed listener");
				logDebug("Event State " + pEvent.stateToString(pEvent.getNewState()));
			}
			if (pEvent.isDeploymentRevert()) {
				logInfo("Deployment has been Reverted");
			}
			if (pEvent.isDeploymentRollback()) {
				logInfo("Deployment has been Roll Back");
			}
			/*
			 * The below condition will update the Error status of feed
			 * Monitoring table for Failure deployment
			 */
			if (pEvent.getNewState() == 6 || DeploymentEvent.stateToString(pEvent.getNewState()).startsWith("ERROR")) {
				for (int i = 0; i < pEvent.getDeploymentProjectIDs().length; i++) {
					Project projectDetail = home.findById(pEvent.getDeploymentProjectIDs()[i]);
					if (projectDetail != null && (projectDetail.getDisplayName().contains(BBB_REGULAR_PRODUCTION_IMPORT)
						|| projectDetail.getDisplayName().contains(BBB_REGULAR_STAGING_IMPORT)
						|| projectDetail.getDisplayName().contains(BBB_PRICING_STAGING_IMPORT) 
						|| projectDetail.getDisplayName().contains(BBB_PRICING_PRODUCTION_IMPORT) 
						|| projectDetail.getDisplayName().contains("BBBEmergencyImport"))) {
						if (pEvent.getTarget().equalsIgnoreCase(getStagingServer())) {
								logInfo("Updating Error Message for Target Staging Only Project");
							if (projectDetail.getDisplayName().contains(BBB_REGULAR_PRODUCTION_IMPORT.concat("_"))) {
								updateRegularProdFeedStatus(projectDetail.getId(), ERROR_IN_DEPLOYMENT_PROD_PROJECT_STAGING);
								sendEmail(ERROR_IN_DEPLOYMENT_PROD_PROJECT_STAGING,"Error while deploying prod project on staging server");
							} else if (projectDetail.getDisplayName().contains(BBB_REGULAR_PRODUCTION_IMPORT.concat("-("))) {
								logInfo("Updating Error Message for Target Staging Only Project");
								updateFeedStatus(projectDetail.getDisplayName(), ERROR_IN_DEPLOYMENT_PROD_PROJECT_STAGING);
							} else if (projectDetail.getDisplayName().contains(BBB_PRICING_PRODUCTION_IMPORT)) {
								updatePricingFeedStatus(projectDetail.getDisplayName(), ERROR_IN_DEPLOYMENT_PROD_PROJECT_STAGING);
							} else if (projectDetail.getDisplayName().contains("BBBEmergencyImport")) {
								updateFeedStatus(projectDetail.getDisplayName(), ERROR_IN_DEPLOYMENT_EMERG_PROJECT_STAGING);
							} else {
								updateFeedStatus(projectDetail.getDisplayName(), ERROR_IN_DEPLOYMENT_STAG_PROJECT_STAGING);
							}
						}
						if (pEvent.getTarget().equalsIgnoreCase(getProductionServer())) {
							if (projectDetail.getDisplayName().contains(BBB_REGULAR_PRODUCTION_IMPORT.concat("_"))) {
									logInfo("Updating Error Message for Target Production Only Project");
								updateRegularProdFeedStatus(projectDetail.getId(), ERROR_IN_DEPLOYMENT_PROD_PROJECT_PRODUCTION);
								sendEmail(ERROR_IN_DEPLOYMENT_PROD_PROJECT_PRODUCTION,"Error while deploying prod project on production server");
							} else if (projectDetail.getDisplayName().contains(BBB_REGULAR_PRODUCTION_IMPORT.concat("-("))) {
								logInfo("Updating Error Message for Target Production Only Project");
								updateFeedStatus(projectDetail.getDisplayName(), ERROR_IN_DEPLOYMENT_PROD_PROJECT_PRODUCTION);
							} else if (projectDetail.getDisplayName().contains(BBB_PRICING_PRODUCTION_IMPORT)){
									logInfo("Updating Error Message for Target Production Only Pricing Project");
								updatePricingFeedStatus(projectDetail.getDisplayName(), ERROR_IN_DEPLOYMENT_PROD_PROJECT_PRODUCTION);
							}
							if (projectDetail.getDisplayName().contains(BBB_EMERGENCY_IMPORT)) {
									logInfo("Updating Error Message for Emergency Project");
								updateFeedStatus(projectDetail.getDisplayName(), ERROR_IN_DEPLOYMENT_EMER_PROJECT_PRODUCTION);
							}
						}

					}
				}
			}
			/*
			 * This Condition will check Any Deployment is resumed after
			 * Deployment Failure
			 */
			if (!pEvent.isDeploymentRollback() && DeploymentEvent.stateToString(pEvent.getNewState()).startsWith("ACTIVE_LOCK")
					&& DeploymentEvent.stateToString(pEvent.getOldState()).startsWith("ERROR")) {
				for (int i = 0; i < pEvent.getDeploymentProjectIDs().length; i++) {
					Project projectDetail = home.findById(pEvent.getDeploymentProjectIDs()[i]);
					if (projectDetail != null && (projectDetail.getDisplayName().contains(BBB_REGULAR_PRODUCTION_IMPORT)
						|| projectDetail.getDisplayName().contains(BBB_REGULAR_STAGING_IMPORT)
						|| projectDetail.getDisplayName().contains(BBB_PRICING_STAGING_IMPORT) 
						|| projectDetail.getDisplayName().contains(BBB_PRICING_PRODUCTION_IMPORT) 
						|| projectDetail.getDisplayName().contains("BBBEmergencyImport"))) {
						if (pEvent.getTarget().equalsIgnoreCase(getStagingServer())) {
							if (isLoggingDebug()) {
								logDebug("Updating Error Message for Target Staging Only Project");
							}
							if (projectDetail.getDisplayName().contains(BBB_REGULAR_PRODUCTION_IMPORT.concat("_"))) {
								updateRegularProdFeedStatus(projectDetail.getId(),PROD_DEP_IN_PROGRESS);
								sendEmail(PROD_DEP_IN_PROGRESS,"Error while deploying prod project on staging server and DeploymentRollback is true and deployment event state is active lock");
							} else if (projectDetail.getDisplayName().contains(BBB_REGULAR_PRODUCTION_IMPORT.concat("-("))) {
								updateFeedStatus(projectDetail.getDisplayName(), PRODUCTION_IN_PROGRESS);
							} else if (projectDetail.getDisplayName().contains(BBB_PRICING_PRODUCTION_IMPORT)) {
								updatePricingFeedStatus(projectDetail.getDisplayName(), PRODUCTION_IN_PROGRESS);
							} else if (projectDetail.getDisplayName().contains("BBBEmergencyImport")) {
								updateFeedStatus(projectDetail.getDisplayName(), EMERGENCY_IN_PROGRESS);
							} else {
								updateFeedStatus(projectDetail.getDisplayName(), STAGING_IN_PROGRESS);
							}
						}
						if (pEvent.getTarget().equalsIgnoreCase(getProductionServer())) {
							if (projectDetail.getDisplayName().contains(BBB_REGULAR_PRODUCTION_IMPORT.concat("_"))) {
								if (isLoggingDebug()) {
									logDebug("Updating Error Message for Target Production Only Project");
								}
								updateRegularProdFeedStatus(projectDetail.getId(),PROD_DEP_IN_PROGRESS);
								sendEmail(PROD_DEP_IN_PROGRESS,"Error while deploying prod project on production server and DeploymentRollback is true and deployment event state is active lock");
							} else if (projectDetail.getDisplayName().contains(BBB_REGULAR_PRODUCTION_IMPORT.concat("-("))) {
								updateFeedStatus(projectDetail.getDisplayName(), PRODUCTION_IN_PROGRESS);
							} else if (projectDetail.getDisplayName().contains(BBB_PRICING_PRODUCTION_IMPORT)) {
								if (isLoggingDebug()) {
									logDebug("Updating Error Message for Target Production Only Pricing Project");
								}
								updatePricingFeedStatus(projectDetail.getDisplayName(), PRODUCTION_IN_PROGRESS);
							}
							if (projectDetail.getDisplayName().contains(BBB_EMERGENCY_IMPORT)) {
								if (isLoggingDebug()) {
									logDebug("Updating Error Message for Emergency Project");
								}
								updateFeedStatus(projectDetail.getDisplayName(), EMERGENCY_IN_PROGRESS);
							}
						}

					}
				}
			}
			/*
			 * The below condition will update the Error status of feed
			 * Monitoring table for Rollback Deployment
			 */
			if (pEvent.isDeploymentRollback()) {
				for (int i = 0; i < pEvent.getDeploymentProjectIDs().length; i++) {
					Project projectDetail = home.findById(pEvent.getDeploymentProjectIDs()[i]);
					if (projectDetail != null && (projectDetail.getDisplayName().contains(BBB_REGULAR_PRODUCTION_IMPORT)
						|| projectDetail.getDisplayName().contains(BBB_REGULAR_STAGING_IMPORT)
						|| projectDetail.getDisplayName().contains(BBB_PRICING_STAGING_IMPORT) 
						|| projectDetail.getDisplayName().contains(BBB_PRICING_PRODUCTION_IMPORT) 
						|| projectDetail.getDisplayName().contains("BBBEmergencyImport"))) {
						if (pEvent.getTarget().equalsIgnoreCase(getStagingServer())) {
							if (isLoggingDebug()) {
								logDebug("Updating Error Message for Target Staging Only Project");
							}
							if (projectDetail.getDisplayName().contains(BBB_REGULAR_PRODUCTION_IMPORT.concat("_"))) {
								updateRegularProdFeedStatus(projectDetail.getId(),ERROR_IN_DEPLOYMENT_PROD_PROJECT_STAGING_ROLLBACK);
								sendEmail(ERROR_IN_DEPLOYMENT_PROD_PROJECT_STAGING_ROLLBACK,"Error while deploying prod project on production server and DeploymentRollback is true");
							} else if (projectDetail.getDisplayName().contains(BBB_REGULAR_PRODUCTION_IMPORT.concat("-("))) {
								updateFeedStatus(projectDetail.getDisplayName(), ERROR_IN_DEPLOYMENT_PROD_PROJECT_STAGING_ROLLBACK);
							} else if (projectDetail.getDisplayName().contains(BBB_PRICING_PRODUCTION_IMPORT)) {
								updatePricingFeedStatus(projectDetail.getDisplayName(), ERROR_IN_DEPLOYMENT_PROD_PROJECT_STAGING_ROLLBACK);
							} else if (projectDetail.getDisplayName().contains("BBBEmergencyImport")) {
								updateFeedStatus(projectDetail.getDisplayName(), ERROR_IN_DEPLOYMENT_EMERG_PROJECT_ROLLBACK);
							} else {
								updateFeedStatus(projectDetail.getDisplayName(), ERROR_IN_DEPLOYMENT_STAG_PROJECT_ROLLBACK);
							}
						}
						if (pEvent.getTarget().equalsIgnoreCase(getProductionServer())) {
							if (projectDetail.getDisplayName().contains(BBB_REGULAR_PRODUCTION_IMPORT.concat("_"))) {
								if (isLoggingDebug()) {
									logDebug("Updating Error Message for Target Production Only Project");
								}
								updateRegularProdFeedStatus(projectDetail.getId(),ERROR_IN_DEPLOYMENT_PROD_PROJECT_PRODUCTION_ROLLBACK);
								sendEmail(ERROR_IN_DEPLOYMENT_PROD_PROJECT_PRODUCTION_ROLLBACK,"Error while deploying prod project on production server and DeploymentRollback is true");
							} else if (projectDetail.getDisplayName().contains(BBB_REGULAR_PRODUCTION_IMPORT.concat("-("))) {
								if (isLoggingDebug()) {
									logDebug("Updating Error Message for Target Production Only Project");
								}
								updateFeedStatus(projectDetail.getDisplayName(), ERROR_IN_DEPLOYMENT_PROD_PROJECT_PRODUCTION_ROLLBACK);
							}else if (projectDetail.getDisplayName().contains(BBB_PRICING_PRODUCTION_IMPORT)) {
								if (isLoggingDebug()) {
									logDebug("Updating Error Message for Target Production Only Pricing Project");
								}
								updatePricingFeedStatus(projectDetail.getDisplayName(), ERROR_IN_DEPLOYMENT_PROD_PROJECT_PRODUCTION_ROLLBACK);
							if (projectDetail.getDisplayName().contains(BBB_EMERGENCY_IMPORT)) {
								if (isLoggingDebug()) {
									logDebug("Updating Error Message for Emergency Project");
								}
								updateFeedStatus(projectDetail.getDisplayName(), ERROR_IN_DEPLOYMENT_EMER_PROJECT_PRODUCTION_ROLLBACK);
								}
							}

						}
					}
				}
			}
			if (pEvent.isDeploymentRevert() && pEvent.stateToString(pEvent.getNewState()).endsWith(DEPLOYMENT_DELETED)) {
				for (int i = 0; i < pEvent.getDeploymentProjectIDs().length; i++) {
					Project projectDetail = home.findById(pEvent.getDeploymentProjectIDs()[i]);
					if (projectDetail != null && projectDetail.getDisplayName().contains("BBBEmergencyImport")) {
						if (pEvent.getTarget().equalsIgnoreCase(getStagingServer())) {
							if (isLoggingDebug()) {
								logDebug("Updating Error Message for Target Staging Only Project");
							}
							updateFeedStatus(projectDetail.getDisplayName(), MANUAL_REVERT);
						}
					}
				}
			}
			if (DeploymentEvent.stateToString(pEvent.getNewState()).endsWith("ACTIVE_APPLY") && !pEvent.isDeploymentRevert() && !pEvent.isDeploymentRollback()) {
				logger.logDebug("Depoly State" + DeploymentEvent.stateToString(pEvent.getNewState()));

			}
			/*
			 * The below condition will update the status of feed Monitoring
			 * table for successful deployment
			 */
			if (!pEvent.isDeploymentRevert() && !pEvent.isDeploymentRollback() && pEvent.stateToString(pEvent.getNewState()).endsWith(DEPLOYMENT_DELETED)) {
				if (isLoggingDebug()) {
					logDebug("Updating the status of the PIM Feed for Target:::::::: " + pEvent.getTarget());
				}
				for (int i = 0; i < pEvent.getDeploymentProjectIDs().length; i++) {
					Project projectDetail = home.findById(pEvent.getDeploymentProjectIDs()[i]);
					if (projectDetail != null && (projectDetail.getDisplayName().contains(BBB_REGULAR_PRODUCTION_IMPORT)
						|| projectDetail.getDisplayName().contains(BBB_REGULAR_STAGING_IMPORT)
						|| projectDetail.getDisplayName().contains(BBB_PRICING_STAGING_IMPORT) 
						|| projectDetail.getDisplayName().contains(BBB_PRICING_PRODUCTION_IMPORT) 
						|| projectDetail.getDisplayName().contains(BBB_EMERGENCY_IMPORT))) {
						if (isLoggingDebug()) {
							logDebug("Updating the status of the PIM Feed Project :::::::: " + projectDetail.getDisplayName());
						}
						if (pEvent.getTarget().equalsIgnoreCase(getStagingServer()) && projectDetail.getDisplayName().contains(BBB_REGULAR_STAGING_IMPORT)) {
							updateFeedStatus(projectDetail.getDisplayName(), DEPLOYED_TO_STAGING);
						}else if (pEvent.getTarget().equalsIgnoreCase(getStagingServer()) && projectDetail.getDisplayName().contains(BBB_PRICING_STAGING_IMPORT)) {
							updatePricingFeedStatus(projectDetail.getDisplayName(), DEPLOYED_TO_STAGING);
						}
						if (pEvent.getTarget().equalsIgnoreCase(getProductionServer()) && projectDetail.getDisplayName().contains(BBB_PRICING_PRODUCTION_IMPORT)) {
							updatePricingFeedStatus(projectDetail.getDisplayName(), CLOSED);
						} else if (pEvent.getTarget().equalsIgnoreCase(getProductionServer()) && projectDetail.getDisplayName().contains(BBB_REGULAR_PRODUCTION_IMPORT.concat("_"))) {
							updateRegularProdFeedStatus(projectDetail.getId(), CLOSED);
							//insert deployed time for feeds for a given project in ecp_prj_imported_feeds table
							updateProjectFeedDeployedTime(projectDetail.getId());
						} else if (pEvent.getTarget().equalsIgnoreCase(getProductionServer())) {
							updateFeedStatus(projectDetail.getDisplayName(), CLOSED);
						}

					}
				}
			}
			
			//When importData is disabled for BBBProductionScheduler then after successful production deployment, delete data from DeployedProjectRepository
			if(!this.isImportDataRequired()){
				
				//if event is deployment_deleted and it is not rollback and not revert then delete data from DeployedProjectRepository
				if (!pEvent.isDeploymentRevert() && !pEvent.isDeploymentRollback() && pEvent.stateToString(pEvent.getNewState()).endsWith(DEPLOYMENT_DELETED)) {
					if (isLoggingDebug()) {
						logDebug("MTHD=[deploymentEvent] MSG=[check if project is BBB_REGULAR_PRODUCTION_IMPORT ");
					}
					
					for (int i = 0; i < pEvent.getDeploymentProjectIDs().length; i++) {
						
						Project projectDetail = home.findById(pEvent.getDeploymentProjectIDs()[i]);
						try{
							if (projectDetail != null
									&& projectDetail.getDisplayName().contains(BBB_REGULAR_PRODUCTION_IMPORT)) {
									logInfo("MTHD=[deploymentEvent] MSG=[Project is BBB_REGULAR_PRODUCTION_IMPORT,"
											+ " Remove assets from DeployedProjectRepository PIM Feed Project : " 
											+ projectDetail.getDisplayName());
								
								//remove deployed project assets
								removeDeployedProjectAssets(BBBCatalogImportConstant.PROPERTY_PROJECT_TYPE_REGULAR);
								
							}
							
						}catch(RepositoryException ex){
							updateFeedStatus(projectDetail.getDisplayName(), ERROR_IN_PRODUCTION_DELETE_DEPLOYED_PROJECT_ASSETS);
						}
					}
				}
			}

		} catch (BBBSystemException e) {
			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(e));
			}
		} catch (EJBException e) {
			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(e));
			}
		} catch (FinderException e) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(e));
			}
		} catch (DeploymentException e) {
			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(e));
			}
		} catch (TransactionDemarcationException e) {
			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(e));
			}
		} catch (RemoveException e) {
			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(e));
			}
		} catch (VersionException e) {
			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(e));
			}
		} catch (PublishingException e) {
			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(e));
			}
		} catch (JMSException e) {
			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(e));
			}
		} catch (RepositoryException e) {
			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(e));
			}
		} catch (BBBBusinessException e) {
			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(e));
			}
		} catch (EmailException e) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(e));
			}
		}

	}
	
	/**
	 * This method will save project assets into the DeployedProjectAssets repository
	 * when deployment target is staging, deployment status is DEPLOYMENT_COMPLETE and 
	 * project name contains either BBBRegularStagingImport or BBBEmergencyImport or BBBPricingStagingImport
	 * 
	 * @param pEvent
	 */
	@SuppressWarnings("unchecked")
	private void saveDeployedProjectAssets(DeploymentEvent pEvent){

		try{
			ProjectHome home = ProjectConstants.getPersistentHomes().getProjectHome();
			
			if (isLoggingDebug()) {
				logDebug("ENTER MTHD=[saveDeployedProjectAssets] target: " + pEvent.getTarget());
			}
			
			for (int i = 0; i < pEvent.getDeploymentProjectIDs().length; i++) {
				
				Project projectDetail = home.findById(pEvent.getDeploymentProjectIDs()[i]);
			
				if (isLoggingDebug()) {
					logDebug(" MTHD=[saveDeployedProjectAssets] check if project is either : " + pEvent.getTarget());
				}
				//save deployed assets when project is either BBBRegularStaging or BBBEmergeyImport or BBBPricingStagingImport
				if (projectDetail != null
						&& projectDetail.getDisplayName().contains(BBB_REGULAR_STAGING_IMPORT)) {
					
					String projectId= pEvent.getDeploymentProjectIDs()[i];
					
					if (isLoggingDebug()) {
						logDebug(" MTHD=[saveDeployedProjectAssets] MSG=[Saving deployed project and statusof the PIM Feed Project : " 
									+ projectDetail.getDisplayName()
									+" Project Checked In Date: "+projectDetail.getCheckinDate()
									+" Project Checkedin Status: "+projectDetail.isCheckedIn());
					}
					if (pEvent.getTarget().equalsIgnoreCase(getStagingServer())
							&&  
							projectDetail.getDisplayName().contains(BBB_REGULAR_STAGING_IMPORT)) {
		
						try{
							//create primary table details
							MutableRepositoryItem savedProjectItem = getSavedProjectRepository().createItem(projectId, BBBCatalogImportConstant.ITEM_DESCRIPTOR_PROJECT_INFO);
							savedProjectItem.setPropertyValue(BBBCatalogImportConstant.PROPERTY_PROJECT_NAME, projectDetail.getDisplayName());
							savedProjectItem.setPropertyValue(BBBCatalogImportConstant.PROPERTY_DEPLOYEMENT_TIME, Calendar.getInstance().getTime());
							
							//set feed type or regular
							savedProjectItem.setPropertyValue(BBBCatalogImportConstant.PROPERTY_PROJECT_TYPE, BBBCatalogImportConstant.PROPERTY_PROJECT_TYPE_REGULAR);
							//add project assets
							addProjectAssets(savedProjectItem, (Set<AssetVersion>)projectDetail.getCheckedInAssets());
							
							getSavedProjectRepository().addItem(savedProjectItem);
							
							if (isLoggingDebug()) {
								logDebug(" MTHD=[saveDeployedProjectAssets] assests are saved for project : " + projectId);
							}
						}catch(RepositoryException ex){
							if (isLoggingError()) {
								logError("Error in MTHD=[saveDeployedProjectAssets] "+ BBBStringUtils.stack2string(ex));
							}
							updateFeedStatus(projectDetail.getDisplayName(), ERROR_IN_STAG_SAVE_DEPLOYED_PROJECT_ASSETS);
						}
					}
				}
			}

		} catch (EJBException e) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(e));
			}
		} catch (FinderException e) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(e));
			}
		}
		
		if (isLoggingDebug()) {
			logDebug("Exit MTHD=[saveDeployedProjectAssets]saveDeployedProjectAssets for target:::::::: " + pEvent.getTarget());
		}
	/*	Object[] params = new Object[3];
		params[0] = BBB_REGULAR_STAGING_IMPORT;
		params[1] = BBB_EMERGENCY_IMPORT;
		params[2] = true;
		
		//create order by query -- add sort directives
		RepositoryItem[] projectItems = this
				.executeRQLQuery(DISPLAY_NAME_CONTAINS_EITHER_OF_AND_CHECKED_IN, params, PROJECT_ITEM_DESCRIPTOR, getPublishingRepo());
		
		or
		
		//	for (int i = 0; i < pEvent.getDeploymentProjectIDs().length; i++) {
		Project projectDetail = home.findById(pEvent.getDeploymentProjectIDs()[i]);
		
		
		if (projectItems != null) {
			for (int i = 0; i < projectItems.length; i++) {
				String projectId= projectItems[i].getRepositoryId();
				Project projectDetail = home.findById(projectId);
				if (isLoggingDebug()) {
					logDebug("Project Checked In Date:::::::::::" + projectDetail.getCheckinDate());
					logDebug("Project Checkedin Status:::::::::::" + projectDetail.isCheckedIn());
				}

				
				//create primary table details
				MutableRepositoryItem savedProjectItem = getSavedProjectRepository().createItem(projectId, "saved-project-info");
				savedProjectItem.setPropertyValue("projectName", projectDetail.getDisplayName());
				savedProjectItem.setPropertyValue("deploymentTime", System.currentTimeMillis());

				//add project assets
				addProjectAssets(savedProjectItem, (Set<AssetVersion>)projectDetail.getCheckedInAssets());
				
				getSavedProjectRepository().addItem(savedProjectItem);
				
			}
		}*/
		
	}

	 /**
	  * Persist deployed project assets 
	  * @param savedProjectItem
	  * @param assets
	  */
	private void addProjectAssets(final MutableRepositoryItem savedProjectItem,
			Set<AssetVersion> assets) {

		if(isLoggingDebug()){
			logDebug(" Enter MTHD=[addProjectAssets]");
		}
		
		@SuppressWarnings({ "unchecked"})
		List<String> projectAssets = (List<String>) savedProjectItem
				.getPropertyValue(BBBCatalogImportConstant.PROPERTY_PROJECT_ASSETS_URI);

		if (projectAssets == null) {
			projectAssets = new ArrayList<String>();
			if(isLoggingDebug()){
				logDebug(" MTHD=[addProjectAssets]/MSG=[create new list for projectAssets]");
			}
		}

		for (AssetVersion asset : assets) {
			String assetUri = asset.getURI().getURIString();
			projectAssets.add(assetUri);
		}
		
		if(isLoggingDebug()){
			logDebug(" MTHD=[addProjectAssets]/MSG=[adding projectAssets = " +projectAssets +"]");
		}
		
		savedProjectItem.setPropertyValue(BBBCatalogImportConstant.PROPERTY_PROJECT_ASSETS_URI, projectAssets);

		if(isLoggingDebug()){
			logDebug(" Exit MTHD=[addProjectAssets]");
		}
	}

	
	/**This method removes the deloyed projects
	 * @param feedType
	 * @throws RepositoryException
	 */
	@SuppressWarnings("unchecked")
	private void removeDeployedProjectAssets(String feedType) throws RepositoryException{

		if(isLoggingDebug()){
			logDebug(" Enter MTHD=[removeDeployedProjectAssets]");
		}
		RepositoryItem[] deployedProjectsItems = null;
		
		try{
			Object[] params = new String[1];
			params[0] = feedType;
			
			deployedProjectsItems = executeRQLQuery(BBBCatalogImportConstant.RQL_REMOVE_DEPLOYED_PROJECT_BY_TYPE,
					params, BBBCatalogImportConstant.ITEM_DESCRIPTOR_PROJECT_INFO, getSavedProjectRepository());
			
		}catch(RepositoryException ex){
			if(isLoggingError()){
				logError(" MTHD=[removeDeployedProjectAssets] RepositoryException="+ex.getMessage());
			}
		}catch(BBBBusinessException ex){
			if(isLoggingError()){
				logError(" MTHD=[removeDeployedProjectAssets] BBBBusinessException=" +ex.getMessage());
			}
		}catch(BBBSystemException ex){
			if(isLoggingError()){
				logError(" MTHD=[removeDeployedProjectAssets] BBBSystemException=" +ex.getMessage());
			}
		}
		
		if(deployedProjectsItems !=null){
			logInfo("MTHD=[removeDeployedProjectAssets] "
						+ "MSG=[number of deployedProjet(s) = "+deployedProjectsItems.length+" ]");
			List<String> projectAssetsURI = null;
			int removedAssets = 0;
			
			for(RepositoryItem deployedProject : deployedProjectsItems){
				if(null != deployedProject) {
					if(null != deployedProject.getPropertyValue(BBBCatalogImportConstant.PROPERTY_PROJECT_ASSETS_URI)) {
						projectAssetsURI = (List<String>) deployedProject.getPropertyValue(BBBCatalogImportConstant.PROPERTY_PROJECT_ASSETS_URI);
						removedAssets += projectAssetsURI.size();
					}    
					String id = deployedProject.getRepositoryId();
					getSavedProjectRepository().removeItem(id, BBBCatalogImportConstant.ITEM_DESCRIPTOR_PROJECT_INFO);
				}
			}
			logInfo("MTHD=[removeDeployedProjectAssets] "
					+ "MSG=[number of removed assets for deployedProjet(s) = "+removedAssets+" ]");
		
		}	
		
		
		if(isLoggingDebug()){
			logDebug(" Exit MTHD=[removeDeployedProjectAssets]");
		}
	}

	private RepositoryItem[] executeRQLQuery(String rqlQuery, Object[] params, String viewName, MutableRepository repository) throws RepositoryException,
			BBBSystemException, BBBBusinessException {
		RqlStatement statement = null;
		RepositoryItem[] queryResult = null;
		if (rqlQuery != null) {
			if (repository != null) {
				try {
					statement = RqlStatement.parseRqlStatement(rqlQuery);
					RepositoryView view = repository.getView(viewName);
					if (view == null && isLoggingError()) {
						logError("View " + viewName + " is null");
					}

					queryResult = statement.executeQuery(view, params);
					if (isLoggingDebug() && queryResult == null) {

						logDebug("No results returned for query [" + rqlQuery + "]");

					}

				} catch (RepositoryException e) {
					if (isLoggingError()) {
						logError("Unable to retrieve data");
					}

					throw new BBBSystemException(UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION, e);
				}
			} else {
				if (isLoggingError()) {
					logError("Repository has no data");
				}
			}
		} else {
			if (isLoggingError()) {
				logError("Query String is null");
			}
		}

		return queryResult;
	}
	
	/**
	 * This method is responsible for updating the status of feeds involved in deployment either to CLOSED if deployment is successful or to one of the error states.
	 * @param pProjectId
	 * @param pStatus
	 * @throws BBBSystemException 
	 */
	public void updateRegularProdFeedStatus(String pProjectId,String pStatus) throws BBBSystemException {
		Connection connection = null;
		List<String> feedList = null;
		try {
			connection = getBbbPIMFeedTools().openConnection();
			feedList = getBbbPIMFeedTools().getProdImportedFeedList(connection, pProjectId);
			if(!feedList.isEmpty()) {
			  for(String feedId : feedList) {
				getBbbPIMFeedTools().updateFeedStatus(feedId, pStatus, connection);
			  }
			}
		} catch (BBBSystemException e) {
			throw new BBBSystemException("Error while updating status of feeds ",e);
		} finally {
			logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: updateRegularProdFeedStatus()" + "::connection object count before closing connection: " + bbbPIMFeedTools.getConnectionCount());
			getBbbPIMFeedTools().closeConnection(connection);
			logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: updateRegularProdFeedStatus()" + "::connection object count after closing connection: " + bbbPIMFeedTools.getConnectionCount());
		}
		
	}
	public void updateFeedStatus(String projectName, String pStatus) {
		Connection pConnection = null;
		int startIdx = projectName.indexOf("(");
		int endIdx = projectName.indexOf(")");
		String[] feedIds = projectName.substring(startIdx + 1, endIdx).split(",");
		if (feedIds.length > 0) {
			try {
				 logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: updateFeedStatus()" + "::connection object count before opening connection: " + bbbPIMFeedTools.getConnectionCount());
				pConnection = getBbbPIMFeedTools().openConnection();
				 logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: updateFeedStatus()" + "::connection object count after opening connection: " + bbbPIMFeedTools.getConnectionCount());
				for (String feedId : feedIds) {
					getBbbPIMFeedTools().updateFeedStatus(feedId, pStatus, pConnection);
				}
			} catch (BBBSystemException e) {
				if (isLoggingError()) {

					logError(BBBStringUtils.stack2string(e));
				}
			}finally{
				logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: updateFeedStatus()" + "::connection object count before closing connection: " + bbbPIMFeedTools.getConnectionCount());
				getBbbPIMFeedTools().closeConnection(pConnection);
				 logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: updateFeedStatus()" + "::connection object count after closing connection: " + bbbPIMFeedTools.getConnectionCount());
			}
		}
	}

	
//	Start :- Added for Pricing Feed T02
	/**
	 * @param projectName
	 * @param pStatus
	 */
	public void updatePricingFeedStatus(String projectName, String pStatus) {
		Connection pConnection = null;
		int startIdx = projectName.indexOf("(");
		int endIdx = projectName.indexOf(")");
		String[] feedIds = projectName.substring(startIdx + 1, endIdx).split(",");
		if (feedIds.length > 0) {
			try {
				 logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: updateFeedStatus()" + "::connection object count before opening connection: " + bbbPIMFeedTools.getConnectionCount());
				pConnection = getBbbPIMFeedTools().openConnection();
				 logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: updateFeedStatus()" + "::connection object count after opening connection: " + bbbPIMFeedTools.getConnectionCount());
				for (String feedId : feedIds) {
					getBbbPIMFeedTools().updatePricingFeedStatus(feedId, pStatus, pConnection);
				}
			} catch (BBBSystemException e) {
				if (isLoggingError()) {

					logError(BBBStringUtils.stack2string(e));
				}
			}finally{
				logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: updateFeedStatus()" + "::connection object count before closing connection: " + bbbPIMFeedTools.getConnectionCount());
				getBbbPIMFeedTools().closeConnection(pConnection);
				 logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: updateFeedStatus()" + "::connection object count after closing connection: " + bbbPIMFeedTools.getConnectionCount());
			}
		}
	}
	
	/**
	 * This method is responsible for sending an email from emailId specified in senderId property and to emailIds specified in reciever Id array property.
	 * Pass the subject and body of email as parameters.
	 * @param pSubject
	 * @param pBody
	 * @throws EmailException 
	 */
	public void sendEmail(String pSubject,String pBody) throws EmailException {
		if (getRecieverMailId() != null && getRecieverMailId().length > 0) {
				getSmtpEmailSender().sendEmailMessage(getSenderMailId(), getRecieverMailId(),pSubject,pBody);
		}
	}

	/**
	 * Method to update the deployed time for the feeds of given project. 
	 * @param pProjectId
	 * @throws BBBSystemException 
	 */
	public void updateProjectFeedDeployedTime(String pProjectId) throws BBBSystemException {
		Connection conn = null;
		if(isLoggingDebug()) {
			logDebug("updateProjectFeedDeployedTime : START");
		}
		try {
			conn = getBbbPIMFeedTools().openConnection();
			getBbbPIMFeedTools().updateProjectFeedDeploymentTime(pProjectId, conn);
		} finally {
			logDebug("Closing the connection");
			getBbbPIMFeedTools().closeConnection(conn);
		}
	}
//	End :- Added for Pricing Feed T02
}