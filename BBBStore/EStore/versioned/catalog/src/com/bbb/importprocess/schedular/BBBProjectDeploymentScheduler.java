package com.bbb.importprocess.schedular;

import java.util.Date;
import java.util.List;

import atg.repository.RepositoryItem;
import atg.service.email.EmailException;
import atg.service.email.SMTPEmailSender;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.importprocess.manager.BBBProjectDeploymentManager;
import com.bbb.importprocess.tools.BBBPIMFeedTools;
import com.bbb.utils.BBBStringUtils;
import java.sql.Connection;

/**
 * 
 * This is a Singleton Scheduler and is responsible for deploying an already created project in author state.
 * 
 * @author Logixal
 * 
 */

public class BBBProjectDeploymentScheduler extends SingletonSchedulableService {

	private BBBProjectDeploymentManager mProjectDeploymentManager;

	/**
	 * Return ProjectDeploymentManager's instance.
	 * @return mProjectDeploymentManager
	 */
	public BBBProjectDeploymentManager getProjectDeploymentManager() {
		return mProjectDeploymentManager;
	}

	/**
	 * Set ProjectDeploymentManager's instance.
	 * @param pProjectDeploymentManager
	 */
	public void setProjectDeploymentManager(BBBProjectDeploymentManager pProjectDeploymentManager) {
		this.mProjectDeploymentManager = pProjectDeploymentManager;
	}

	private boolean mSchedulerEnabled;
	/**
	 * Returns the whether the scheduler is enabled or not
	 * 
	 * @return the isShedulerEnabled
	 */
	public boolean isSchedulerEnabled() {
		return mSchedulerEnabled;
	}

	/**
	 * This variable signifies to enable or disable the scheduler in specific
	 * environment this value is set from the property file
	 * 
	 * @param isShedulerEnabled
	 *            the isShedulerEnabled to set
	 */
	public void setSchedulerEnabled(boolean pSchedulerEnabled) {
		this.mSchedulerEnabled = pSchedulerEnabled;
	}
	
	/**
	 * This is deployment System exception status property 
	 */
	private String mDeploymentSystemExceptionStatus="DEP_SYSTEM_EXCEPTION";
	
	/**
	 * @return mDeploymentSystemExceptionStatus
	 */
	public String getDeploymentSystemExceptionStatus() {
		return mDeploymentSystemExceptionStatus;
	}

	/**
	 * @param pDeploymentSystemExceptionStatus sets mDeploymentSystemExceptionStatus
	 */
	public void setDeploymentSystemExceptionStatus(String pDeploymentSystemExceptionStatus) {
		mDeploymentSystemExceptionStatus = pDeploymentSystemExceptionStatus;
	}

	/**
	 * This is Production Deployment In Progress Status Property 
	 */
	private String mProductionDeploymentInProgressStatus="PROD_DEP_IN_PROGRESS";

	/**
	 * @return mProductionDeploymentInProgressStatus
	 */
	public String getProductionDeploymentInProgressStatus() {
		return mProductionDeploymentInProgressStatus;
	}

	/**
	 * @param pProductionDeploymentInProgressStatus sets pProductionDeploymentInProgressStatus
	 */
	public void setProductionDeploymentInProgressStatus(String pProductionDeploymentInProgressStatus) {
		mProductionDeploymentInProgressStatus = pProductionDeploymentInProgressStatus;
	}

	/**
	 * Perform the scheduled task for this service. Overridden method, perform project deployment once this method is invoked.
	 * 
	 **/
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

	  /**
	   * mMaxRetries - Property defining the maximum retries counts to wait for import to complete 
	   */
	  private int mMaxRetries;

	  /**
	   * @return mMaxRetries
	   */
	  public int getMaxRetries() {
		  return mMaxRetries;
	  }

	  /**
	   * @param pMaxRetries sets mMaxRetries
	   */
	  public void setMaxRetries(int pMaxRetries) {
		  this.mMaxRetries = pMaxRetries;
	  }

	  /**
	   * mWaitTime - Property defining the waiting for import to complete.
	   */
	  private long mWaitTime;

	  /**
	   * @return mWaitTime
	   */
	  public long getWaitTime() {
		  return mWaitTime;
	  }

	  /**
	   * @param pWaitTime sets mWaitTime
	   */
	  public void setWaitTime(long pWaitTime) {
		  mWaitTime = pWaitTime;
	  }

	  /**
	   * mFailureMailSubject - Subject of mail sent on failure to trigger the deployment.  
	   */
	  private String mFailureMailSubject;

	  /**
	   * @return mFailureMailSubject
	   */
	  public String getFailureMailSubject() {
		  return mFailureMailSubject;
	  }

	  /**
	   * @param pFailureMailSubject sets mFailureMailSubject
	   */
	  public void setFailureMailSubject(String pFailureMailSubject) {
		  mFailureMailSubject = pFailureMailSubject;
	  }

	  /**
	   * mFailureMailSubject - Subject of mail sent on failure to trigger the deployment. 
	   */
	  private String mFailureMailBody;

	  /** 
	   * @return mFailureMailBody
	   */
	  public String getFailureMailBody() {
		  return mFailureMailBody;
	  }

	  /**
	   * @param pFailureMailBody sets mFailureMailBody
	   */
	  public void setFailureMailBody(String pFailureMailBody) {
		  mFailureMailBody = pFailureMailBody;
	  }

	  private boolean mProjectDeploymentRunning;

	  /**
	   * @return mProjectDeploymentRunning - boolean value specifying whether the scheduler is running or not
	   */
	  public boolean isProjectDeploymentRunning() {
		  return mProjectDeploymentRunning;
	  }

	  /**
	   * @param pProjectDeploymentRunning sets mProjectDeploymentRunning
	   */
	  public void setProjectDeploymentRunning(boolean pProjectDeploymentRunning) {
		  mProjectDeploymentRunning = pProjectDeploymentRunning;
	  }
	/**
	 * Overridden method which is starting point to trigger deployment at the scheduled time.
	*/
	@Override
	public void doScheduledTask(final Scheduler pScheduler, final ScheduledJob pJob) {

		if (isSchedulerEnabled()) {
			if (isLoggingDebug()) {
				logDebug("BBBProjectDeploymentScheduler : doScheduledTask() : job: " + pJob.getJobName() + " date = " + new Date());
			}
			deployProject();
		} else {
			if (isLoggingDebug()) {
				logDebug("BBBProjectDeploymentScheduler : doScheduledTask() : Scheduler is Disabled");
			}
		}
	}

	/**
	 * Helps to trigger scheduled task manually
	 */
	public void triggerProjectDeploymentManually() {
		if (isLoggingDebug()) {
			logDebug("BBBProjectDeploymentScheduler : Starting triggerProjectDeploymentManually()");
		}
		deployProject();
		if (isLoggingDebug()) {
			logDebug("BBBProjectDeploymentScheduler : Ending triggerProjectDeploymentManually()");
		}
	}

	/**
	 * This method is responsible for sending an email from emailId specified in senderId property and to emailIds specified in receiver Id array property.
	 * Pass the subject and body of email as parameters.
	 * @param pSubject
	 * @param pBody
	 * @throws EmailException 
	 */
	public void sendEmail(String pSubject, String pBody) throws EmailException {
		if (getRecieverMailId() != null && getRecieverMailId().length > 0) {
				getSmtpEmailSender().sendEmailMessage(getSenderMailId(), getRecieverMailId(), pSubject, pBody);
		} else {
			if(isLoggingDebug()) {
				logDebug("Check Reciever EmailId list");
			}
		}
	}

	/**
	 * This method is responsible for performing several checks before actually starting Project Deployment.
	 * Following checks are performed :
	 * 1. Are there any Open Emergency Feeds 2. Are there any feeds in Exception State
	 * 3. Is Prod/Staging Import in Progress 4. Are any EmergencyRegular Feeds in Open/New2 State
	 * If any of the above conditions are satisfied then we don't deploy the project. 
	 * @throws BBBSystemException Indicating issue in project deployment.
	 */
	private void deployProject() {

		if(isProjectDeploymentRunning()) {
			if(isLoggingDebug()) {
				logDebug("deployProject() : Project Deployment scheduler is already running, hence exiting");
			}
			return;
		}
		setProjectDeploymentRunning(true);
		int currentTryCount = 0;
		String projectId = null;
		Connection connection = null;
		BBBProjectDeploymentManager deploymentManager = getProjectDeploymentManager();
		BBBPIMFeedTools feedTools = deploymentManager.getBbbPIMFeedTools();

		BBBPerformanceMonitor.start("BBBProjectDeploymentScheduler", "deployProject");
		try {
			connection = feedTools.openConnection();
			if(isLoggingDebug()) {
				logDebug("BBBProjectDeploymentScheduler : deployProjects() : Going through checklist before initiating Project Deployment");
			}
			List<String> emergencyFeedsList = feedTools.getEmergencyFeeds(connection);
			if (!emergencyFeedsList.isEmpty()) {
				if(isLoggingDebug()) {
					logDebug("deployProject() : There is a emergency feed to import, hence could not perform the project deployment.");
				}
				return;
			}
			if (feedTools.isPIMFeedInException(connection)) {
				if(isLoggingWarning()) {
					logWarning("deployProject() : Some feed are in exception state, hence could not perform the project deployment.");
				}
				return;
			}
			
			boolean isProdInProgress = feedTools.isProdImportInProgress(connection);
			boolean isStgInProgress = feedTools.isStagingImportInProgress(connection);

			while (isProdInProgress || isStgInProgress) {
				if(isLoggingDebug()) {
					logDebug("Either Staging or Production feed import is in progress, hence waiting as expecting to complete soon.");
				}
				if (currentTryCount > getMaxRetries()) {
					if(isLoggingDebug()) {
						logDebug("Either Staging or Production feed import is in progress for long, hence will do the deployment in next try.");
					}
					return;
				}
				Thread.currentThread().sleep(getWaitTime());
				isProdInProgress = feedTools.isProdImportInProgress(connection);
				isStgInProgress = feedTools.isStagingImportInProgress(connection);
				currentTryCount++;
			}

			if(feedTools.isEmergencyRegularOpen(connection)) {
				if(isLoggingDebug()) {
					logDebug("deployProject() : There is a emergency regular open feed to import, hence could not perform the project deployment.");
				}
				return;
			}

			RepositoryItem project = deploymentManager.getProdProjectToDeploy();
			if (project == null) {
				if(isLoggingDebug()) {
					logDebug("deployProject() : No Project To Deploy hence Exiting the deployment procedure");
				}
				return;
			}
			projectId = project.getRepositoryId();
			if(isLoggingDebug()) {
				logDebug("deployProject() : Basic checks for deploying the project are passed, hence we are good for triggerring deployment of project : " 
					+ project.getItemDisplayName());
			}

			//Update the feed status to Production deployment in progress status
			deploymentManager.updateImportedFeedStatus(connection, projectId, getProductionDeploymentInProgressStatus());

			if (isLoggingDebug()) {
				logDebug("Updated status of feeds in project : " + project.getItemDisplayName() 
					+ " to status : " + getProductionDeploymentInProgressStatus() + " before triggering the deployment.");	
			}

			deploymentManager.deploySingleProdProject(project, connection);
			
			if(isLoggingDebug()) {
				logDebug("deployProject() : The project deployment is triggered succesfully.");
			}
		} catch (InterruptedException ie) {
			if(isLoggingError()) {
				logError("Interrupted Exception : ", ie);
			}
		} catch (BBBSystemException bse) {
			if(isLoggingError()) {
				logError("BBB System Exception : ", bse);
			}
			try {
				deploymentManager.updateImportedFeedStatus(connection, projectId, getDeploymentSystemExceptionStatus());
				sendEmail(getFailureMailSubject(), getFailureMailBody() + " " + BBBStringUtils.stack2string(bse));
			} catch (EmailException emailException) {
				if (isLoggingError()) {
					logError(BBBStringUtils.stack2string(emailException));
				}
			} catch (BBBSystemException bsexp) {
				if (isLoggingError()) {
					logError(BBBStringUtils.stack2string(bsexp));
				}
			}
			BBBPerformanceMonitor.cancel("deployProject");
		} finally {
			feedTools.closeConnection(connection);
			logDebug("Closed the connection");
			BBBPerformanceMonitor.end("BBBProjectDeploymentScheduler", "deployProject");
			setProjectDeploymentRunning(false);
		}
	}
}