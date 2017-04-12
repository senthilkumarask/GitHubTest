package com.bbb.importprocess.schedular;

import java.sql.Connection;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import atg.service.email.EmailException;
import atg.service.email.SMTPEmailSender;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

import com.bbb.exception.BBBSystemException;
import com.bbb.importprocess.manager.AbstractBBBImportManager;
import com.bbb.importprocess.tools.BBBPIMFeedTools;
import com.bbb.utils.BBBStringUtils;

/**
 * 
 * This class will be used by the service which does direct import from the PIM
 * schema into Staging (bypassing BCC)
 * 
 * @author logixal
 */

public class BBBDirectCatalogFeedJob extends SingletonSchedulableService {

	
	private static final String REGULAR = "Regular";
	private static final String DIRECT_STAGING = "Direct Staging";
	
	private boolean mSchedulerEnabled;
	private AbstractBBBImportManager mImportManager;
	private boolean mManualFeed;

	
	private boolean mDirectImportRunning;

	/**
	 * @return mDirectImportRunning - boolean value specifying whether the scheduler is running or not
	 */
	public boolean isDirectImportRunning() {
		return mDirectImportRunning;
	}

	/**
	 * @param pDirectImportRunning sets mDirectImportRunning
	 */
	public void setDirectImportRunning(boolean pDirectImportRunning) {
		mDirectImportRunning = pDirectImportRunning;
	}

	/**
	   * @return the mManualFeed
	   */
	  public boolean isManualFeed() {
	  	return mManualFeed;
	  }

	  /**
	   * @param mManualFeed the mManualFeed to set
	   */
	  public void setManualFeed(boolean mManualFeed) {
	  	this.mManualFeed = mManualFeed;
	  }

	
	private String mStagingCompleteFeedStatus;
	
	/**
	 * 
	 * @return mStagingCompleteFeedStatus
	 */
	public String getStagingCompleteFeedStatus() {
		return mStagingCompleteFeedStatus;
	}

	/**
	 * 
	 * @param pStagingCompleteFeedStatus
	 */
	public void setStagingCompleteFeedStatus(String pStagingCompleteFeedStatus) {
		mStagingCompleteFeedStatus = pStagingCompleteFeedStatus;
	}

	private String mStagingExceptionFeedStatus;

	/**
	 * 
	 * @return mStagingExceptionFeedStatus
	 */
	public String getStagingExceptionFeedStatus() {
		return mStagingExceptionFeedStatus;
	}

	/**
	 * 
	 * @param pStagingExceptionFeedStatus
	 */
	public void setStagingExceptionFeedStatus(String pStagingExceptionFeedStatus) {
		mStagingExceptionFeedStatus = pStagingExceptionFeedStatus;
	}
	
	private String mStagingProgressFeedStatus;
	/**
	 * 
	 * @return mStagingProgressFeedStatus
	 */
	public String getStagingProgressFeedStatus() {
		return mStagingProgressFeedStatus;
	}

	/**
	 * 
	 * @param pStagingProgressFeedStatus
	 */
	public void setStagingProgressFeedStatus(String pStagingProgressFeedStatus) {
		mStagingProgressFeedStatus = pStagingProgressFeedStatus;
	}

	// Email Configurations and its variables
	private SMTPEmailSender mSmtpEmailSender;
	private String mSenderMailId;
	private String[] mRecieverMailId;

	/**
	 * 
	 * @return mSmtpEmailSender
	 */
	public SMTPEmailSender getSmtpEmailSender() {
		return mSmtpEmailSender;
	}

	/**
	 * 
	 * @param pSmtpEmailSender
	 */
	public void setSmtpEmailSender(SMTPEmailSender pSmtpEmailSender) {
		this.mSmtpEmailSender = pSmtpEmailSender;
	}

	/**
	 * 
	 * @return mImportManager
	 */
	public AbstractBBBImportManager getImportManager() {

		return mImportManager;
	}

	/**
	 * 
	 * @param pBBBImportManager
	 */
	public void setImportManager(final AbstractBBBImportManager pBBBImportManager) {

		mImportManager = pBBBImportManager;
	}
	
	
	private String mBadRecordsEmailSubject = "Feeds with bad record";

	public String getBadRecordsEmailSubject() {
		return mBadRecordsEmailSubject;
	}

	public void setBadRecordsEmailSubject(String pBadRecordsEmailSubject) {
		mBadRecordsEmailSubject = pBadRecordsEmailSubject;
	}

	/**
	 * mNoOfFeedsToProcessPerInvoke - Number of feeds to process in per invoke
	 */
	private int mNoOfFeedsToProcessPerInvoke;

	/**
	 * @return mNoOfFeedsToProcessPerInvoke - Number of feeds to process in per invoke
	 */
	public int getNoOfFeedsToProcessPerInvoke() {
		return mNoOfFeedsToProcessPerInvoke;
	}

	/**
	 * @param pmNoOfFeedsToProcessPerInvoke sets mNoOfFeedsToProcessPerInvoke
	 */
	public void setNoOfFeedsToProcessPerInvoke(int pNoOfFeedsToProcessPerInvoke) {
		mNoOfFeedsToProcessPerInvoke = pNoOfFeedsToProcessPerInvoke;
	}

	private int mMaxAllowedDeployedToStagingFeedCount;

	/**
	 * @return mMaxAllowedDeployedToStagingFeedCount - Max number of feed allowed in DEPLOYED_TO_STAGING to proceed.
	 */
	public int getMaxAllowedDeployedToStagingFeedCount() {
		return mMaxAllowedDeployedToStagingFeedCount;
	}

	/**
	 * @param pMaxAllowedDeployedToStagingFeedCount sets mMaxAllowedDeployedToStagingFeedCount
	 */
	public void setMaxAllowedDeployedToStagingFeedCount(int pMaxAllowedDeployedToStagingFeedCount) {
		mMaxAllowedDeployedToStagingFeedCount = pMaxAllowedDeployedToStagingFeedCount;
	}

	/**
	 * This method contains flow control logic for Direct Staging scheduler
	 */
	private void doDirectStaging() {

		if (getNoOfFeedsToProcessPerInvoke() == 0) {
			if (isLoggingDebug()) {
				logDebug("BBBDirectCatalogFeedJob : doDirectStaging() : Property value noOfFeedsToProcessPerInvoke representing " +
					"numbers of feeds to process per invoke is 0, hence existing without importing any feeds to Production.");	
			}
			return;
		}

		if(isDirectImportRunning()) {
			if(isLoggingDebug()) {
				logDebug("doDirectStaging() : Direct scheduler instance is already running, hence exiting");
			}
			return;
		}
		setDirectImportRunning(true);
		if (isLoggingDebug()) {
			logDebug("doDirectStaging : Processing Regular Staging Feed : Start");
		}

		final BBBPIMFeedTools bbbPIMFeedTools = getImportManager().getPimFeedTools();

		String currentFeedId = null;
		List<String> regularFeedIdList = null;
		Connection directStgFeedListConnection = null;
		List<String> processedFeed = new ArrayList<String>();
		try {

			int deployToStagingPIMFeedCount = bbbPIMFeedTools.getDeployedToStagingPIMFeedCount();

			if(deployToStagingPIMFeedCount > getMaxAllowedDeployedToStagingFeedCount()) {
				if(isLoggingDebug()) {
					logDebug("doDirectStaging() : Number of feed in deployed to staging status reached the maximum allowed feed count, hence exiting");
				}
				return;
			}

			if(isLoggingDebug()) {
				logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: doDirectStaging()"
				        + "::connection object count before opening connection: " + bbbPIMFeedTools.getConnectionCount());	
			}
			directStgFeedListConnection = bbbPIMFeedTools.openConnection();

			if (isLoggingDebug()) {
				logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: doDirectStaging()"
				        + "::connection object count after opening connection: " + bbbPIMFeedTools.getConnectionCount());
			}

			regularFeedIdList = bbbPIMFeedTools.getFeedsForDirectStagingImport(directStgFeedListConnection);

			bbbPIMFeedTools.closeConnection(directStgFeedListConnection);

			if (isLoggingDebug()) {
				logDebug("Regular Feed List to import : " + regularFeedIdList);

			}

			if (regularFeedIdList.isEmpty()) {
				if (isLoggingDebug()) {
					logDebug("doDirectStaging() : No feeds to process, hence doing nothing");
				}
				return;
			}

			
			for (int feedCount = 0; feedCount < regularFeedIdList.size(); feedCount++) {

				if (feedCount >= getNoOfFeedsToProcessPerInvoke()) {
					if (isLoggingDebug()) {
						logDebug("BBBDirectCatalogFeedJob : doDirectStaging() : Since feeds processed count : " + feedCount + 
							" exceeds or equals to number of feed to process per invoke: " + getNoOfFeedsToProcessPerInvoke() + ", hence existing.");
					}
					break;
				}

				String feedId = regularFeedIdList.get(feedCount);

				Connection perFeedConnection = null;

				try {

					currentFeedId = feedId;

					perFeedConnection = bbbPIMFeedTools.openConnection();

					if (isLoggingDebug()) {
						logDebug("doDirectStaging() : Opened a new connection for the current working feed : " + currentFeedId);
					}

					boolean continueProcessing = bbbPIMFeedTools.getPIMFeedStatusForDirectImport();

					if (isLoggingDebug()) {
						logDebug("doDirectStaging() : Continue processing = " + continueProcessing);
					}

					if (!continueProcessing) {
						if (isLoggingWarning()) {
							logWarning("Either an Emergency feed is open or in progress, or other feeds are in exception, hence returning from the service");
						}
						return;
					}

					bbbPIMFeedTools.generateFeedId(REGULAR, perFeedConnection);

					bbbPIMFeedTools.updateFeedStatus(currentFeedId, getStagingProgressFeedStatus(), perFeedConnection);

					/* wait time between the change of the feed_id and the start
					of the feed processing. This is just in case the PIM
					process gets the feed ID from latest_feed_details and is
					in the middle of writing when the feed_id is incremented.*/

					if(isLoggingDebug()) {
						logDebug("Current thread going to sleep for 1 min");
					}
					Thread.sleep(60000);

					List<String> feedIdsToProcess = new ArrayList<String>();
					feedIdsToProcess.add(currentFeedId);

					if (isLoggingDebug()) {
						logDebug("doDirectStaging() : Staring import of Feed : " + currentFeedId + 
								" and status is marked as : " + getStagingProgressFeedStatus());
						logDebug("Before getImportManager().executeImport(getJobName(),  date=" + formatDate(getDate())
								+ "with feeds = " + feedIdsToProcess);
					}

					getImportManager().executeImport(getJobName(), feedIdsToProcess, null, null, perFeedConnection);

					if (isLoggingDebug()) {
						logDebug("After getImportManager().executeImport(getJobName(),  date=" + formatDate(getDate())
								+ "with feeds = " + feedIdsToProcess);
					}

					bbbPIMFeedTools.updateFeedStatus(currentFeedId, getStagingCompleteFeedStatus(), perFeedConnection);
					if (isLoggingDebug()) {
						logDebug("doDirectStaging() : Import of Feed : " + currentFeedId + 
								" is done successfully and status is marked as : " + getStagingCompleteFeedStatus());
					}
					processedFeed.add(currentFeedId);

				} finally {
					if (isLoggingDebug()) {
						logDebug("doDirectStaging() : Closing the connection for the current working feed : " + currentFeedId);
					}
					bbbPIMFeedTools.closeConnection(perFeedConnection);	
				}
			}

		} catch (BBBSystemException e) {
			if (isLoggingError()) {
				logError("BBB System Exception : " + BBBStringUtils.stack2string(e), e);
			}
			Connection updateFeedStatusConnection = null;
			try {
				updateFeedStatusConnection = bbbPIMFeedTools.openConnection();
				bbbPIMFeedTools.updateFeedStatus(currentFeedId, getStagingExceptionFeedStatus(), updateFeedStatusConnection);
				if (getRecieverMailId() != null && getRecieverMailId().length != 0) {
					if (isLoggingDebug()) {
						logDebug("Sending Email to:::" + getRecieverMailId());
						logDebug("Sending mail from the Server::::" + getServerName());
					}
					if (getRecieverMailId() != null && getRecieverMailId().length > 0) {
						getSmtpEmailSender()
						        .sendEmailMessage(
						                getSenderMailId(),
						                getRecieverMailId(),
						                "System error while running Direct Staging Import feed(s) ::" + regularFeedIdList != null ? regularFeedIdList
						                        .toString() : null + " on server" + getServerName(),
						                "Unable to run the feed due to the following Exception:::::::"
						                        + BBBStringUtils.stack2string(e));
					} else {
						if (isLoggingError()) {
							logError("System error while running feed :::" + getServerName());
							logError("Unable to run the feed due to the following Exception:::::::"
							        + BBBStringUtils.stack2string(e));
						}
					}
				}
			} catch (EmailException e1) {
				if (isLoggingError()) {
					logError("Check the email Configurations : " + e1);
				}

			} catch (BBBSystemException e1) {
				if (isLoggingError()) {
					logError("BBB System Exception while update the feed status : " + BBBStringUtils.stack2string(e1), e1);
				}
			} finally {
				bbbPIMFeedTools.closeConnection(updateFeedStatusConnection);
			}
		} catch (InterruptedException e) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(e));
			}
		} finally {
			Connection getBadRecordsConnection = null;
			try {
				getBadRecordsConnection = bbbPIMFeedTools.openConnection();
				if(!processedFeed.isEmpty()) {
					StringBuffer badFeedsEmailBody = bbbPIMFeedTools.getFeedBadRecords(getBadRecordsConnection, processedFeed, DIRECT_STAGING);
					if(!badFeedsEmailBody.toString().trim().equals("")) {
						if (getRecieverMailId() != null && getRecieverMailId().length > 0) {
							if (isLoggingDebug()) {
								logDebug("Sending Bad Record Email to:::" + getRecieverMailId());
								logDebug("Sending mail from the Server::::" + getServerName());
							}						
							getSmtpEmailSender()
							.sendEmailMessage(getSenderMailId(),getRecieverMailId(),getBadRecordsEmailSubject(),badFeedsEmailBody.toString());
						}
					}
				}
			} catch (BBBSystemException e) {
				if (isLoggingError()) {
					logError(BBBStringUtils.stack2string(e));
				}
			} catch (EmailException e) {
				if (isLoggingError()) {
					logError(BBBStringUtils.stack2string(e));
				}
			} finally {
				bbbPIMFeedTools.closeConnection(getBadRecordsConnection);
				bbbPIMFeedTools.closeConnection(directStgFeedListConnection);
				if (isLoggingDebug()) {
					logDebug("doDirectStaging : Processing Regular Staging Feed : End");
				}
				setDirectImportRunning(false);
			}
		}
	}

	/**
	 * Perform the scheduled task for this service. Overridden method, verify
	 * the schedule type and call different import according to type
	 * 
	 **/
	@Override
	public void doScheduledTask(final Scheduler pScheduler, final ScheduledJob pJob) {

		if (isSchedulerEnabled()) {

			if (isLoggingDebug()) {
				logDebug("job: " + pJob.getJobName() + " date = " + new Date());
				logDebug("Before  call to doParellelDirectStaging()");
			}
			doDirectStaging();
			if (isLoggingDebug()) {
				logDebug("After  call to doParellelDirectStaging()");
			}

		} else {
			if (isLoggingDebug()) {
				logDebug("BBBDirectCatalogFeed Scheduler : Scheduler is Disabled");
			}
		}
	}

	/**
	 * Method used for manually invoking direct staging import
	 */
	public void manualDirectStagingExecution() {

		if (isLoggingDebug()) {
			logDebug("Before  call to doParellelDirectStaging()");
		}
		doDirectStaging();
		if (isLoggingDebug()) {
			logDebug("After  call to doParellelDirectStaging()");

		}
	}

	/**
	 * 
	 * @return mSenderMailId
	 */
	public String getSenderMailId() {
		return mSenderMailId;
	}

	/**
	 * 
	 * @param pSenderMailId
	 */
	public void setSenderMailId(String pSenderMailId) {
		this.mSenderMailId = pSenderMailId;
	}

	/**
	 * @return the mRecieverMailIdS
	 */
	public String[] getRecieverMailId() {
		return mRecieverMailId;
	}

	/**
	 * @param mRecieverMailIdS
	 *            the mRecieverMailIdS to set
	 */
	public void setRecieverMailId(String[] mRecieverMailId) {
		this.mRecieverMailId = mRecieverMailId;
	}

	private String serverName;

	/**
	 * @return the serverName
	 */
	public String getServerName() {

		return serverName;
	}

	/**
	 * @param serverName
	 *            the serverName to set
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	/**
	 * Returns the whether the scheduler is enable or not
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
	 * Helps to trigger schedule task manually
	 */
	public void executeDoScheduledTask() {
		if (isLoggingDebug()) {
			logDebug("Entry executeDoScheduledTask");
		}
		doScheduledTask(null, new ScheduledJob("BBBCatalogFeedJob", "", "", null, null, false));

		if (isLoggingDebug()) {
			logDebug("Exit executeDoScheduledTask");
		}
	}

	private Date getDate() {

		java.util.Date date = new java.util.Date();
		long t = date.getTime();
		java.sql.Date sqlDate = new java.sql.Date(t);
		return sqlDate;
	}

	private String formatDate(Date date) {

		String dateAsString = null;
		Format formatter = null;
		formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
		dateAsString = formatter.format(date);

		return dateAsString;
	}
}
