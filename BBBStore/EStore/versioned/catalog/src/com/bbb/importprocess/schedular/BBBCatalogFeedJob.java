package com.bbb.importprocess.schedular;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bbb.commerce.catalog.BBBConfigTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.importprocess.manager.AbstractBBBImportManager;
import com.bbb.importprocess.tools.BBBPIMFeedTools;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBStringUtils;
import com.bbb.utils.BBBUtility;

import atg.service.email.EmailException;
import atg.service.email.SMTPEmailSender;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

/**
 * 
 * <p>
 * This class will be consumed by 3 components EmergencyFeedJob,
 * ScheduledFeedJob and RegularFeedJob. It also has a schedulable component
 * which enables the ATG scheduler service to invoke the job at predefined
 * scheduler.
 * 
 * 
 **/

public class BBBCatalogFeedJob extends SingletonSchedulableService {

  private static final String EMERGENCY = "Emergency";

  private static final String REGULAR = "Regular";

  private static final String REGULAR_STAGING = "BBBRegularStaging";

  private static final String REGULAR_PRODUCTION = "BBBRegularProduction";

  private static final String EMERGENCY_JOB = "BBBEmergency";
  
  private static final String PIM_IMPORT_PRODUCTION = "BBBProductionPIMImport";
  
  private static final String ERROR_RECEPIENT_MAIL_ID_CONFIG_KEY = "errorRecipientEmailIds";
  
  private static final String ERROR_RECEPIENT_MAIL_ID_CONFIG_TYPE = "ContentCatalogKeys";

  private boolean mProdImportRunning;

  /**
   * @return mProdImportRunning - boolean value specifying whether the scheduler is running or not
   */
  public boolean isProdImportRunning() {
	  return mProdImportRunning;
  }

  /**
   * @param pProdImportRunning sets mProdImportRunning
   */
  public void setProdImportRunning(boolean pProdImportRunning) {
	  mProdImportRunning = pProdImportRunning;
  }
  private boolean mSchedulerEnabled;
  private boolean mManualFeed;
  // ----------------------------------------------
  // MEMBER VARIABLES
  // ----------------------------------------------

  // -------------------------------------
  // Properties

  /** The Scheduler for this */
  private Scheduler mScheduler;

  private String serverName;

  /** The Schedule for this */
  private Schedule mSchedule;

  /** Workflow Name */
  private String mWorkFlowName;
  private String mProjectName;

  // -------------------------------------
  // Member variables

  private String mSchedulableType;
  private AbstractBBBImportManager mImportManager;

  // Email Configurations and its variables
  private SMTPEmailSender mSmtpEmailSender;
  private String mSenderMailId;
  
  private BBBConfigTools bbbConfigTools;

  public BBBConfigTools getBbbConfigTools() {
	return bbbConfigTools;
}

public void setBbbConfigTools(BBBConfigTools bbbConfigTools) {
	this.bbbConfigTools = bbbConfigTools;
}

public SMTPEmailSender getSmtpEmailSender() {
    return mSmtpEmailSender;
  }

  public void setSmtpEmailSender(SMTPEmailSender pSmtpEmailSender) {
    this.mSmtpEmailSender = pSmtpEmailSender;
  }

  // -------------------------------------
  // Properties
  // -------------------------------------
  @Override
  public void setScheduler(final Scheduler pScheduler) {
    mScheduler = pScheduler;
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

  // -------------------------------------
  /**
   * 
   */
  public Scheduler getScheduler() {
    return mScheduler;
  }

  // -------------------------------------
  public void setSchedule(final Schedule pSchedule) {
    mSchedule = pSchedule;
  }

  // -------------------------------------
  public Schedule getSchedule() {
    return mSchedule;
  }

  public AbstractBBBImportManager getImportManager() {

    return mImportManager;
  }

  public void setImportManager(final AbstractBBBImportManager pBBBImportManager) {

    mImportManager = pBBBImportManager;
  }
  
  
  private String mProductionImportInProgressStatus="PROD_IMPORT_IN_PROGRESS";
  
  
/**
 *   
 * @return mProductionImportInProgressStatus
 */
public String getProductionImportInProgressStatus() {
	return mProductionImportInProgressStatus;
}

/**
 * 
 * @param pProductionImportInProgressStatus
 */
public void setProductionImportInProgressStatus(String pProductionImportInProgressStatus) {
	this.mProductionImportInProgressStatus = pProductionImportInProgressStatus;
}


private String mProductionImportCompletedStatus="PROD_IMPORT_COMPLETED";


/**
 * 
 * @return mProductionImportCompletedStatus
 */
public String getProductionImportCompletedStatus() {
	return mProductionImportCompletedStatus;
}

/**
 * 
 * @param pProductionImportCompletedStatus
 */
public void setProductionImportCompletedStatus(String pProductionImportCompletedStatus) {
	this.mProductionImportCompletedStatus = pProductionImportCompletedStatus;
}


private String mProductionImportExceptionStatus="PROD_SYSTEM_EXCEPTION";

/**
 * 
 * @return mProductionImportExceptionStatus
 */
public String getProductionImportExceptionStatus() {
	return mProductionImportExceptionStatus;
}

/**
 * 
 * @param pProductionImportExceptionStatus
 */
public void setProductionImportExceptionStatus(String pProductionImportExceptionStatus) {
	this.mProductionImportExceptionStatus = pProductionImportExceptionStatus;
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

private void doEmergency() {
    if (isLoggingDebug()) {
      logDebug("Emergency Feed started");
    }
    
    final boolean inProgress = verifyPIMFeedStatus();
  
    if (inProgress) {

      return;
    }
    
    List<String> emregencyFeedIdList = null;
    final BBBPIMFeedTools bbbPIMFeedTools = getImportManager().getPimFeedTools();
    Connection connection = null;
    try {

      connection = bbbPIMFeedTools.openConnection();

      emregencyFeedIdList = bbbPIMFeedTools.getPIMLatestFeed(EMERGENCY, connection);

      if (!emregencyFeedIdList.isEmpty()) {
        for (String feedId : emregencyFeedIdList) {
          if (isLoggingDebug()) {
            logDebug("Updating the feed status for the FeedID:::::" + feedId);
          }
          bbbPIMFeedTools.generateFeedId(EMERGENCY, connection);
         
          bbbPIMFeedTools.updateFeedStatus(feedId, "EMERGENCY_IN_PROGRESS", connection);
          // calling method to update the feed Id
         // bbbPIMFeedTools.generateFeedId(EMERGENCY, connection);
        }
        /* wait time between the change of the feed_id and the start
  		of the feed processing. This is just in case the PIM
  		process gets the feed ID from latest_feed_details and is
  		in the middle of writing when the feed_id is incremented.*/
          if(isLoggingDebug()){
        	  logDebug("Current thread going to sleep for 1 min");
          }
          Thread.sleep(60000);
        getImportManager().executeImport(getJobName(), emregencyFeedIdList, getWorkFlowName(), getProjectName(),
            connection);

      }

    } catch (BBBSystemException e) {
      try {
        if (getRecieverMailId() != null && getRecieverMailId().length != 0) {
          if (isLoggingDebug()) {
            logDebug("Sending Email to:::" + getRecieverMailId());
            logDebug("Sending mail from the Server::::" + getServerName());
          }
          if (isLoggingError()) {
            logError("Unable to run the feed " + BBBStringUtils.stack2string(e));
          }
          if (getRecieverMailId() != null && getRecieverMailId().length > 0) {
            getSmtpEmailSender().sendEmailMessage(getSenderMailId(), getRecieverMailId(),
                "System error while running EMERGENCY feed(s) ::"+emregencyFeedIdList!=null ? emregencyFeedIdList.toString():null+" on server" + getServerName(),
                "Unable to run the feed due to the following Exception:::::::" + BBBStringUtils.stack2string(e));
          } else {
            if (isLoggingError()) {
              logError("System error while running feed :::" + getServerName());
              logError("Unable to run the feed due to the following Exception:::::::" + BBBStringUtils.stack2string(e));
            }
          }
        }
      } catch (EmailException e1) {
        if (isLoggingError()) {
          logError("Check the email Configurations : " + e1);
        }

      }
      if (isLoggingError()) {

        logError(BBBStringUtils.stack2string(e));
      }
      for (String feedId : emregencyFeedIdList) {
        try {
          bbbPIMFeedTools.updateFeedStatus(feedId, "SYSTEM_EXCEPTION", connection);
        } catch (BBBSystemException e1) {

          if (isLoggingError()) {
            logError(BBBStringUtils.stack2string(e1));
          }
        }
      }
    } catch (InterruptedException e) {
    	 if (isLoggingError()) {
             logError(BBBStringUtils.stack2string(e));
           }
	} finally {

      bbbPIMFeedTools.closeConnection(connection);
    }

  }

  private boolean verifyPIMFeedStatus() {
    final BBBPIMFeedTools bbbPIMFeedTools = getImportManager().getPimFeedTools();
    boolean pimStatus = false;
    Connection connection = null;
    try {
      connection = bbbPIMFeedTools.openConnection();
      pimStatus = bbbPIMFeedTools.getPIMFeedStatus(connection);
    } catch (BBBSystemException e) {
      if (isLoggingError()) {
        logError(BBBStringUtils.stack2string(e));
      }
    } catch (BBBBusinessException e) {
      if (isLoggingError()) {
        logError(BBBStringUtils.stack2string(e));
      }
    } finally {
      bbbPIMFeedTools.closeConnection(connection);
    }
    if(pimStatus){
    	 if (isLoggingDebug()) {
    		 logDebug("Emergency or Regular Feeds are in progress ::::::Exit Without Processing");
    	 }
    }
    return pimStatus;
  }

  private void doRegularProduction() {
    if (isLoggingDebug()) {
      logDebug("Regular Production Feed started");
    }
    
    final BBBPIMFeedTools bbbPIMFeedTools = getImportManager().getPimFeedTools();
    
    logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: doRegularProduction()" + "::connection object count before calling verifyPIMFeedStatus():: " + bbbPIMFeedTools.getConnectionCount());
    final boolean inProgress = verifyPIMFeedStatus();
    logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: doRegularProduction()" + "::connection object count after calling verifyPIMFeedStatus():: " + bbbPIMFeedTools.getConnectionCount());
    if (inProgress) {
    	logDebug("verifyPIMFeedStatus() shows in progress so returning. Thread id: " + Thread.currentThread().getId());
      return;
    }
    
    List<String> regularFeedIdList = null;
   
    Connection connection = null;
    try {
    	 logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: doRegularProduction()" + "::connection object count before opening connection: " + bbbPIMFeedTools.getConnectionCount());
      connection = bbbPIMFeedTools.openConnection();
      	 logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: doRegularProduction()" + "::connection object count after opening connection: " + bbbPIMFeedTools.getConnectionCount());
      final List<String> emregencyFeedIdList = bbbPIMFeedTools.getPIMLatestFeed(EMERGENCY, connection);

      if (!emregencyFeedIdList.isEmpty()) {

        return;
      }
      regularFeedIdList = bbbPIMFeedTools.getPIMLatestFeed(REGULAR_PRODUCTION, connection);
      if (!regularFeedIdList.isEmpty()) {
        for (String feedId : regularFeedIdList) {
          if (isLoggingDebug()) {
            logDebug("Updating the feed status for the FeedID:::::" + feedId);
          }
          if(isManualFeed()){
              bbbPIMFeedTools.generateFeedId(REGULAR, connection);
             
              }
          bbbPIMFeedTools.updateFeedStatus(feedId, "PRODUCTION_IN_PROGRESS", connection);
          
        }
        if(isManualFeed()){
        	 /* wait time between the change of the feed_id and the start
      		of the feed processing. This is just in case the PIM
      		process gets the feed ID from latest_feed_details and is
      		in the middle of writing when the feed_id is incremented.*/
              if(isLoggingDebug()){
            	  logDebug("Current thread going to sleep for 1 min");
              }
              Thread.sleep(60000);
            }
        // calling method to update the feed Id
       
        getImportManager().executeImport(getJobName(), regularFeedIdList, getWorkFlowName(), getProjectName(),
            connection);
      }

    } catch (BBBSystemException e) {
      try {
        if (getRecieverMailId() != null && getRecieverMailId().length != 0) {
          if (isLoggingDebug()) {
            logDebug("Sending Email to:::" + getRecieverMailId());
            logDebug("Sending mail from the Server::::" + getServerName());
          }
          if (isLoggingError()) {
            logError("Unable to run the feed " + BBBStringUtils.stack2string(e));
          }
          if (getRecieverMailId() != null && getRecieverMailId().length > 0) {
            getSmtpEmailSender().sendEmailMessage(getSenderMailId(), getRecieverMailId(),
                "System error while running RegularPROD feed(s) ::"+ regularFeedIdList !=null?regularFeedIdList.toString():null +" on server :: " + getServerName(),
                "Unable to run the feed due to the following Exception:::::::" + BBBStringUtils.stack2string(e));
          } else {
            if (isLoggingError()) {
              logError("System error while running feed(s) :::"+ regularFeedIdList +" on server "+ getServerName());
              logError("Unable to run the feed due to the following Exception:::::::" + BBBStringUtils.stack2string(e));
            }
          }
        }
      } catch (EmailException e1) {
        if (isLoggingError()) {
          logError("Check the email Configurations : " + e1);
        }

      }
      if (isLoggingError()) {

        logError(BBBStringUtils.stack2string(e));
      }
      for (String feedId : regularFeedIdList) {
        try {
          bbbPIMFeedTools.updateFeedStatus(feedId, "SYSTEM_EXCEPTION", connection);
        } catch (BBBSystemException e1) {

          if (isLoggingError()) {
            logError(BBBStringUtils.stack2string(e1));
          }
        } 
      }
    } catch (InterruptedException e) {
    	 if (isLoggingError()) {
             logError(BBBStringUtils.stack2string(e));
           }
	} finally {

    	 logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: doRegularProduction()" + "::connection object count before closing connection: " + bbbPIMFeedTools.getConnectionCount());
      bbbPIMFeedTools.closeConnection(connection);
      logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: doRegularProduction()" + "::connection object count after closing connection: " + bbbPIMFeedTools.getConnectionCount());
      logDebug("Regular Production Feed end");
    }

  }

  private void doRegularStaging() {
    if (isLoggingDebug()) {
      logDebug("Regular Staging Feed started");
    }
    
    final BBBPIMFeedTools bbbPIMFeedTools = getImportManager().getPimFeedTools();
    
    logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: doRegularStaging()" + "::connection object count before calling verifyPIMFeedStatus():: " + bbbPIMFeedTools.getConnectionCount());
    final boolean inProgress = verifyPIMFeedStatus();
    logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: doRegularStaging()" + "::connection object count after calling verifyPIMFeedStatus():: " + bbbPIMFeedTools.getConnectionCount());
    if (inProgress) {
    	logDebug("verifyPIMFeedStatus() shows in progress so returning. Thread id: " + Thread.currentThread().getId());
    	return;
    }
    
    
    List<String> regularFeedIdList = null;
    Connection connection = null;
    try {
    	
      logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: doRegularStaging()" + "::connection object count before opening connection: " + bbbPIMFeedTools.getConnectionCount());
      connection = bbbPIMFeedTools.openConnection();
      //logDebug("Regular Staging Feed started");
      logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: doRegularStaging()" + "::connection object count after opening connection: " + bbbPIMFeedTools.getConnectionCount());
   
      
      final List<String> emregencyFeedIdList = bbbPIMFeedTools.getPIMLatestFeed(EMERGENCY, connection);

      if (!emregencyFeedIdList.isEmpty()) {
        return;
      }
      regularFeedIdList = bbbPIMFeedTools.getPIMLatestFeed(REGULAR_STAGING, connection);
      if (!regularFeedIdList.isEmpty()) {
        for (String feedId : regularFeedIdList) {
          if (isLoggingDebug()) {
            logDebug("Updating the feed status for the FeedID:::::" + feedId);
          }
          bbbPIMFeedTools.generateFeedId(REGULAR, connection);
		
          bbbPIMFeedTools.updateFeedStatus(feedId, "STAGING_IN_PROGRESS", connection);

          // calling method to update the feed Id
          //bbbPIMFeedTools.generateFeedId(REGULAR, connection);
        }
        /* wait time between the change of the feed_id and the start
		of the feed processing. This is just in case the PIM
		process gets the feed ID from latest_feed_details and is
		in the middle of writing when the feed_id is incremented.*/
          if(isLoggingDebug()){
        	  logDebug("Current thread going to sleep for 1 min");
          }
          Thread.sleep(60000);
         
        getImportManager().executeImport(getJobName(), regularFeedIdList, getWorkFlowName(), getProjectName(),
            connection);
      }
    } catch (BBBSystemException e) {
      try {
        if (getRecieverMailId() != null && getRecieverMailId().length != 0) {
          if (isLoggingDebug()) {
            logDebug("Sending Email to:::" + getRecieverMailId());
            logDebug("Sending mail from the Server::::" + getServerName());
          }
          if (isLoggingError()) {
            logError("Unable to run the feed " + BBBStringUtils.stack2string(e));
          }
          if (getRecieverMailId() != null && getRecieverMailId().length > 0) {
            getSmtpEmailSender().sendEmailMessage(getSenderMailId(), getRecieverMailId(),
                "System error while running RegularSTAGING feed(s) ::"+ regularFeedIdList!=null?regularFeedIdList.toString():null+" on server" + getServerName(),
                "Unable to run the feed due to the following Exception:::::::" + BBBStringUtils.stack2string(e));
          } else {
            if (isLoggingError()) {
              logError("System error while running feed :::" + getServerName());
              logError("Unable to run the feed due to the following Exception:::::::" + BBBStringUtils.stack2string(e));
            }
          }
        }
      } catch (EmailException e1) {
        if (isLoggingError()) {
          logError("Check the email Configurations : " + e1);
        }

      }
      if (isLoggingError()) {

        logError(BBBStringUtils.stack2string(e));
      }
      for (String feedId : regularFeedIdList) {
        try {
          bbbPIMFeedTools.updateFeedStatus(feedId, "SYSTEM_EXCEPTION", connection);
        } catch (BBBSystemException e1) {

          if (isLoggingError()) {
            logError(BBBStringUtils.stack2string(e1));
          }
        }
      }
    } catch (InterruptedException e) {
    	 if (isLoggingError()) {
             logError(BBBStringUtils.stack2string(e));
           }
	} finally {


      logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: doRegularStaging()" + "::connection object count before closing connection: " + bbbPIMFeedTools.getConnectionCount());
      bbbPIMFeedTools.closeConnection(connection);
      logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: doRegularStaging()" + "::connection object count after closing connection: " + bbbPIMFeedTools.getConnectionCount());
      logDebug("Regular Staging Feed end");
    }

  }
  
  
	/**
	 * This method handles the flow control logic for PIM import scheduler.
	 */
	private void doProductionPIMImport() {

		if (getNoOfFeedsToProcessPerInvoke() == 0) {
			if (isLoggingDebug()) {
				logDebug("BBBCatalogFeedJob : doProductionPIMImport() : Property value noOfFeedsToProcessPerInvoke representing " +
					"numbers of feeds to process per invoke is 0, hence existing without importing any feeds to Production.");	
			}
			return;
		}

		if(isProdImportRunning()) {
			if(isLoggingDebug()) {
				logDebug("doProductionPIMImport() : PIM Import scheduler instance is already running, hence exiting");
			}
			return;
		}
		setProdImportRunning(true);
		BBBPerformanceMonitor.start("BBBProductionPIMImportScheduler", "doProductionPIMImport");
		if (isLoggingDebug()) {
			logDebug("BBBCatalogFeedJob : doProductionPIMImport() :  Production Import Feed started");
		}
		String currentFeed = "";
		final BBBPIMFeedTools bbbPIMFeedTools = getImportManager().getPimFeedTools();
		List<String> regularFeedIdList = null;
		Connection prodImportFeedListConnection = null;
		List<String> processedFeed = new ArrayList<String>();

		try {
			if (isLoggingDebug()) {
				logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: doRegularProduction()"
				        + "::connection object count before opening connection: " + bbbPIMFeedTools.getConnectionCount());
					
			}
			prodImportFeedListConnection = bbbPIMFeedTools.openConnection();
			if (isLoggingDebug()) {
				logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: doRegularProduction()"
				        + "::connection object count after opening connection: " + bbbPIMFeedTools.getConnectionCount());	
			}
			regularFeedIdList = bbbPIMFeedTools.getPIMLatestFeedForProdImport(prodImportFeedListConnection);

			bbbPIMFeedTools.closeConnection(prodImportFeedListConnection);

			if (regularFeedIdList == null || regularFeedIdList.isEmpty()) {
				if (isLoggingDebug()) {
					logDebug("BBBCatalogFeedJob : doProductionPIMImport() :  No feeds to process.....So exiting scheduler");
				}
				return;
			}

			
			for (int feedCount = 0; feedCount < regularFeedIdList.size(); feedCount++) {

				if (feedCount >= getNoOfFeedsToProcessPerInvoke()) {
					if (isLoggingDebug()) {
						logDebug("BBBCatalogFeedJob : doProductionPIMImport() : Since feeds processed count : " + feedCount + 
							" exceeds or equals to number of feed to process per invoke: " + getNoOfFeedsToProcessPerInvoke() + ", hence existing.");
					}
					break;
				}

				String feedId = regularFeedIdList.get(feedCount);

				Connection perFeedConnection = null;
				
				try {
					currentFeed = feedId;

					perFeedConnection = bbbPIMFeedTools.openConnection();

					if (isLoggingDebug()) {
						logDebug("doProductionPIMImport() : Opened a new connection for current working feed : " + currentFeed);
					}

					List<String> emergencyFeedsList = bbbPIMFeedTools.getEmergencyFeeds(perFeedConnection);
					if (!emergencyFeedsList.isEmpty()) {
						if(isLoggingDebug()) {
							logDebug("doProductionPIMImport() : There is a emergency feed to import, hence could not perform the import.");
						}
						return;
					}

					if (bbbPIMFeedTools.isPIMFeedInException(perFeedConnection)) {
						if(isLoggingWarning()) {
							logWarning("doProductionPIMImport() : Some feed are in exception state, hence could not perform the import.");
						}
						return;
					}

					if (bbbPIMFeedTools.isProdDeployInProgress(perFeedConnection)) {
						if(isLoggingWarning()) {
							logWarning("doProductionPIMImport() : The project in which previous feeds were imported is getting deployed in BCC, hence could not perform the import.");
						}
						return;
					}

					if (isManualFeed()) {
						if (isLoggingDebug()) {
							logDebug("Updating the feed status for the FeedID:::::" + feedId);
						}
						bbbPIMFeedTools.generateFeedId(REGULAR, perFeedConnection);
					}

					bbbPIMFeedTools.updateFeedStatus(feedId, getProductionImportInProgressStatus(), perFeedConnection);

					if(isLoggingDebug()) {
						logDebug("doProductionPIMImport() : Started importing Feed : " + feedId + 
								"and status is marked as " + getProductionImportInProgressStatus());
					}

					/* wait time between the change of the feed_id and the start
					of the feed processing. This is just in case the PIM
					process gets the feed ID from latest_feed_details and is
					in the middle of writing when the feed_id is incremented.*/

					if(isManualFeed()) {
						if(isLoggingDebug()) {
							logDebug("Current thread going to sleep for 1 min");
						}
						Thread.sleep(60000);
					}


					List<String> feedIds = new ArrayList<String>();
					feedIds.add(feedId);				
					getImportManager()
					.executeImport(getJobName(), feedIds, getWorkFlowName(), getProjectName(), perFeedConnection);

					bbbPIMFeedTools.updateFeedStatus(feedId, getProductionImportCompletedStatus(), perFeedConnection);
					if(isLoggingDebug()) {
						logDebug("doProductionPIMImport() : Feed " + feedId + " is successfully imported " +
								"and status is marked as " + getProductionImportCompletedStatus());
					}
					processedFeed.add(currentFeed);

				} finally {
					if (isLoggingDebug()) {
						logDebug("doProductionPIMImport() : Closing the connection for current working feed : " + currentFeed);
					}
					bbbPIMFeedTools.closeConnection(perFeedConnection);
				}
			}

		} catch (Exception e) {
			Connection updateFeedStatusConnection = null;
			String[] receiverMailIds = getRecieverMailId();
			if (receiverMailIds != null && receiverMailIds.length > 0) {
				if (isLoggingDebug()) {
					logDebug("Sending Email to:::" + receiverMailIds);
					logDebug("Sending mail from the Server::::" + getServerName());
				}
				try {
					getSmtpEmailSender().sendEmailMessage(
					        getSenderMailId(),
					        receiverMailIds,
					        "System error while running PIM Prod Import feed(s) ::" + currentFeed + " on server :: "
					                + getServerName(),
					        "Unable to run the feed due to the following Exception:::::::"
					                + BBBStringUtils.stack2string(e));
				} catch (EmailException emailException) {
					logError("Error occurred while sending email for error encountered during pim import.", emailException);
				}
			}
			try {
				if(isLoggingError()) {
					logError("System error while running feed(s) :::" + currentFeed + " on server "
					        + getServerName());
					logError("Unable to import the feed due to the following Exception:::::::", e);
				}
				updateFeedStatusConnection = bbbPIMFeedTools.openConnection();
				bbbPIMFeedTools.updateFeedStatus(currentFeed, getProductionImportExceptionStatus(), updateFeedStatusConnection);
			} catch (BBBSystemException bse) {
				if (isLoggingError()) {
					logError("Exception occurred while updating feed status to Error: " ,e);
				}
			} finally {
				bbbPIMFeedTools.closeConnection(updateFeedStatusConnection);
			}
			BBBPerformanceMonitor.cancel("doProductionPIMImport");
		} finally {
			Connection getBadRecordsConnection = null;
			try {
				getBadRecordsConnection = bbbPIMFeedTools.openConnection();
				if(!processedFeed.isEmpty()){
					StringBuffer badFeedsEmailBody = bbbPIMFeedTools.getFeedBadRecords(getBadRecordsConnection, processedFeed, PIM_IMPORT_PRODUCTION);
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
				bbbPIMFeedTools.closeConnection(prodImportFeedListConnection);
				logDebug("Pim Import end");
				BBBPerformanceMonitor.end("BBBProductionPIMImportScheduler", "doProductionPIMImport");
				setProdImportRunning(false);
			}
		}
	}
	
  /**
   * 
   * @return
   */
  public String getScheduleType() {

    return mSchedulableType;
  }

  /**
   * @param mSchedulableType
   */
  public void setScheduleType(final String pSchedulableType) {

    this.mSchedulableType = pSchedulableType;
  }

  /**
   * Perform the scheduled task for this service. Overridden method, verify the
   * schedule type and call different import according to type
   * 
   **/
  @Override
  public void doScheduledTask(final Scheduler pScheduler, final ScheduledJob pJob) {
    if (isSchedulerEnabled()) {
     

      if (isLoggingDebug()) {
        logDebug("job: " + pJob.getJobName() + " date = " + new Date());
      }

      if (getJobName().equals(EMERGENCY_JOB)) {
        doEmergency();
      }
      if (getJobName().equals(REGULAR_STAGING)) {
        doRegularStaging();

      }
      if (getJobName().equals(REGULAR_PRODUCTION)) {
        doRegularProduction();
      }
      if (getJobName().equals(PIM_IMPORT_PRODUCTION)) {
    	  doProductionPIMImport();
      }
    } else {
      if (isLoggingDebug()) {
        logDebug("Scheduler is Disabled");
      }
    }
  }
  
	public void manualProductionPIMImportExecution() {
		if (isLoggingDebug()) {
			logDebug("Before  call to doProductionPIMImport()");
		}
		doProductionPIMImport();
		if (isLoggingDebug()) {
			logDebug("After  call to doProductionPIMImport()");
		}
	}

  public void manualRegularProductionExecution() {
 
    doRegularProduction();
  }

  public void manualEmergencyExecution() {


    doEmergency();
  }

  public void manualRegularStagingExecution() {

    doRegularStaging();

  }

  // -------------------------------------

  /**
   * @return the workFlowName
   */
  public String getWorkFlowName() {
    return mWorkFlowName;
  }

  /**
   * @param pWorkFlowName
   *          the workFlowName to set
   */
  public void setWorkFlowName(String pWorkFlowName) {
    mWorkFlowName = pWorkFlowName;
  }

  /**
   * @return the projectName
   */
  public String getProjectName() {
    return mProjectName;
  }

  /**
   * @param pProjectName
   *          the projectName to set
   */
  public void setProjectName(String pProjectName) {
    mProjectName = pProjectName;
  }

  public String getSenderMailId() {
    return mSenderMailId;
  }

  public void setSenderMailId(String pSenderMailId) {
    this.mSenderMailId = pSenderMailId;
  }

  /**
   * @return the mRecieverMailIdS
   */
  public String[] getRecieverMailId() {
	  List<String> val = null;
	  if(this.getBbbConfigTools()!=null){
		  try {
			val = this.getBbbConfigTools().getAllValuesForKey(ERROR_RECEPIENT_MAIL_ID_CONFIG_TYPE, ERROR_RECEPIENT_MAIL_ID_CONFIG_KEY);
		  } catch (BBBSystemException | BBBBusinessException e) {
			logError("Error Occurred while fetching config_key - " + ERROR_RECEPIENT_MAIL_ID_CONFIG_KEY + " for config_type - " + ERROR_RECEPIENT_MAIL_ID_CONFIG_TYPE);
		  }
		  if(!BBBUtility.isListEmpty(val)){
			  String values = val.get(0);
			  if(!BBBUtility.isEmpty(values)){
				  return values.trim().split(BBBCoreConstants.COMMA);
			  }
		  }
		  String configValue = BBBConfigRepoUtils.getStringValue(ERROR_RECEPIENT_MAIL_ID_CONFIG_TYPE, ERROR_RECEPIENT_MAIL_ID_CONFIG_KEY);
		  if(!BBBUtility.isEmpty(configValue)){
			  String[] ids = configValue.trim().split(BBBCoreConstants.COMMA);
			  return ids;
		  }
	  }else
		  logError("Could not retrieve MailId's as configTools is null.");
	  return null;
  }

  /**
   * @return the serverName
   */
  public String getServerName() {
    return serverName;
  }

  /**
   * @param serverName
   *          the serverName to set
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
   *          the isShedulerEnabled to set
   */
  public void setSchedulerEnabled(boolean pSchedulerEnabled) {
    this.mSchedulerEnabled = pSchedulerEnabled;
  }
  
  /**
	 * Helps to trigger schedule task manually
	 */
	public void executeDoScheduledTask() {
		if(isLoggingDebug()){
			logDebug("Entry executeDoScheduledTask");
		}
		doScheduledTask(null,new ScheduledJob("BBBCatalogFeedJob", "", "", null, null, false));
		
		if(isLoggingDebug()){
			logDebug("Exit executeDoScheduledTask");
		}
	}
}
