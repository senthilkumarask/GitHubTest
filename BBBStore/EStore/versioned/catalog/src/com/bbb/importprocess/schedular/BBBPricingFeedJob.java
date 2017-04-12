package com.bbb.importprocess.schedular;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import atg.service.email.EmailException;
import atg.service.email.SMTPEmailSender;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.importprocess.manager.AbstractBBBImportManager;
import com.bbb.importprocess.tools.BBBPIMFeedTools;
import com.bbb.utils.BBBStringUtils;

/**
 * 
 * <p>
 * This class will be consumed by 1 components BBBPricing Feed Job. It also has a schedulable component
 * which enables the ATG scheduler service to invoke the job at predefined
 * scheduled time.
 * 
 * @author ugoel
 **/

public class BBBPricingFeedJob extends SingletonSchedulableService {

  private static final String EMERGENCY = "Emergency";
  
  private static final String PRICING_PRODUCTION = "BBBPricingProduction";
  private static final String PRICING_STAGING = "BBBPricingStaging";
  private static final String PRICING_REGULAR = "Pricing";

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
  private String[] mRecieverMailId;

  public SMTPEmailSender getSmtpEmailSender() {
    return mSmtpEmailSender;
  }

  public void setSmtpEmailSender(final SMTPEmailSender pSmtpEmailSender) {
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
  public void setManualFeed(final boolean mManualFeed) {
  	this.mManualFeed = mManualFeed;
  }

  // -------------------------------------
  /**
   * 
   */
  @Override
public Scheduler getScheduler() {
    return mScheduler;
  }

  // -------------------------------------
  @Override
public void setSchedule(final Schedule pSchedule) {
    mSchedule = pSchedule;
  }

  // -------------------------------------
  @Override
public Schedule getSchedule() {
    return mSchedule;
  }

  public AbstractBBBImportManager getImportManager() {

    return mImportManager;
  }

  public void setImportManager(final AbstractBBBImportManager pBBBImportManager) {

    mImportManager = pBBBImportManager;
  }
  
  private boolean verifyPIMFeedStatus() {
    final BBBPIMFeedTools bbbPIMFeedTools = getImportManager().getPimFeedTools();
    boolean pimStatus = false;
    Connection connection = null;
    try {
      connection = bbbPIMFeedTools.openConnection();
      pimStatus = bbbPIMFeedTools.getPIMPricingFeedStatus(connection);
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
    if(pimStatus && isLoggingDebug()){
   		 logDebug("Regular Pricing Feeds are in progress :::::: Exit Without Processing");
    }
    return pimStatus;
  }
  
	  private void doPricingStaging() {
	    if (isLoggingDebug()) {
	      logDebug("Regular Staging Pricing Feed started");
	    }
	    final BBBPIMFeedTools bbbPIMFeedTools = getImportManager().getPimFeedTools();
	    
	    logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: doPricingStaging()" + "::connection object count before calling verifyPIMFeedStatus():: " + bbbPIMFeedTools.getConnectionCount());
	    final boolean inProgress = verifyPIMFeedStatus();
	    logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: doPricingStaging()" + "::connection object count after calling verifyPIMFeedStatus():: " + bbbPIMFeedTools.getConnectionCount());
	    if (inProgress) {
	    	logDebug("Pricing PIM FEED verifyPIMFeedStatus() shows in progress so returning. Thread id: " + Thread.currentThread().getId());
	    	return;
	    }
	    
	    
	    List<String> regularFeedIdList = null;
	    Connection connection = null;
	    try {
	    	
	      logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: doPricingStaging()" + "::connection object count before opening connection: " + bbbPIMFeedTools.getConnectionCount());
	      connection = bbbPIMFeedTools.openConnection();
	      //logDebug("Regular Staging Feed started");
	      logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: doPricingStaging()" + "::connection object count after opening connection: " + bbbPIMFeedTools.getConnectionCount());

	      regularFeedIdList = bbbPIMFeedTools.getPIMPricingLatestFeed(PRICING_REGULAR, connection, PRICING_STAGING);
	      if (!regularFeedIdList.isEmpty()) {
	        for (final String feedId : regularFeedIdList) {
	          if (isLoggingDebug()) {
	            logDebug("Updating the Pricing feed status for the FeedID:::::" + feedId);
	          }
	          bbbPIMFeedTools.generatePricingFeedId(PRICING_STAGING, connection);
	          bbbPIMFeedTools.updatePricingFeedStatus(feedId, "STAGING_IN_PROGRESS", connection);
	        }
	        /* wait time between the change of the feed_id and the start
			of the feed processing. This is just in case the PIM
			process gets the feed ID from latest_feed_details and is
			in the middle of writing when the feed_id is incremented.*/
	          if(isLoggingDebug()){
	        	  logDebug("Current thread going to sleep for 1 min");
	          }
	          Thread.sleep(60000);
	         
	        getImportManager().executeImport(getJobName(), regularFeedIdList, getWorkFlowName(), getProjectName(), connection);
	      }
	    } catch (BBBSystemException e) {
	      try {
	        if (getRecieverMailId() != null && getRecieverMailId().length != 0) {
	          if (isLoggingDebug()) {
	            logDebug("Sending Email to:::" + getRecieverMailId());
	            logDebug("Sending mail from the Server::::" + getServerName());
	          }
	          if (isLoggingError()) {
	            logError("Unable to run the Pricing feed " + BBBStringUtils.stack2string(e));
	          }
	          if (getRecieverMailId() != null && getRecieverMailId().length > 0) {
	        	  getSmtpEmailSender().sendEmailMessage(getSenderMailId(), getRecieverMailId(),
	                "System error while running RegularSTAGING feed(s) ::"+ regularFeedIdList!=null?regularFeedIdList.toString():null+" on server" + getServerName(),
	                "Unable to run the Pricing feed due to the following Exception:::::::" + BBBStringUtils.stack2string(e));
	          } else {
	            if (isLoggingError()) {
	              logError("System error while running Pricing feed :::" + getServerName());
	              logError("Unable to run the Pricing feed due to the following Exception:::::::" + BBBStringUtils.stack2string(e));
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
	      for (final String feedId : regularFeedIdList) {
	        try {
	          bbbPIMFeedTools.updatePricingFeedStatus(feedId, "SYSTEM_EXCEPTION", connection);
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
	      logDebug("Regular Pricing Staging Feed end");
	    }

	  }
	  
	  
	  private void doPricingProduction() {
		    if (isLoggingDebug()) {
		      logDebug("Regular Production Pricing Feed started");
		    }
		    
		    final BBBPIMFeedTools bbbPIMFeedTools = getImportManager().getPimFeedTools();
		    
		    logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: doRegularProduction()" + "::connection object count before calling verifyPIMFeedStatus():: " + bbbPIMFeedTools.getConnectionCount());
		    final boolean inProgress = verifyPIMFeedStatus();
		    logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: doRegularProduction()" + "::connection object count after calling verifyPIMFeedStatus():: " + bbbPIMFeedTools.getConnectionCount());
		    if (inProgress) {
		    	logDebug("Pricing PIM FEED verifyPIMFeedStatus() shows in progress so returning. Thread id: " + Thread.currentThread().getId());
		      return;
		    }
		    
		    List<String> regularFeedIdList = null;
		   
		    Connection connection = null;
		    try {
		    	 logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: doRegularProduction()" + "::connection object count before opening connection: " + bbbPIMFeedTools.getConnectionCount());
		      connection = bbbPIMFeedTools.openConnection();
		      	 logDebug("ThreadId: " + Thread.currentThread().getId() + "::" + "Method: doRegularProduction()" + "::connection object count after opening connection: " + bbbPIMFeedTools.getConnectionCount());

		      	 regularFeedIdList = bbbPIMFeedTools.getPIMPricingLatestFeed(PRICING_REGULAR, connection, PRICING_PRODUCTION);
		      if (!regularFeedIdList.isEmpty()) {
		        for (final String feedId : regularFeedIdList) {
		          if (isLoggingDebug()) {
		            logDebug("Updating the feed status for the FeedID:::::" + feedId);
		          }
		          
		          bbbPIMFeedTools.generatePricingFeedId(PRICING_REGULAR, connection);
		          bbbPIMFeedTools.updatePricingFeedStatus(feedId, "PRODUCTION_IN_PROGRESS", connection);
		          
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
		       
		        getImportManager().executeImport(getJobName(), regularFeedIdList, getWorkFlowName(), getProjectName(), connection);
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
		      for (final String feedId : regularFeedIdList) {
		        try {
		          bbbPIMFeedTools.updatePricingFeedStatus(feedId, "SYSTEM_EXCEPTION", connection);
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
   * schedule type and calls import according to type of Scheduled Job
   * 
   **/
  @Override
  public void doScheduledTask(final Scheduler pScheduler, final ScheduledJob pJob) {
    if (isSchedulerEnabled()) {

      if (isLoggingDebug()) {
        logDebug("job: " + pJob.getJobName() + " date = " + new Date());
      }
//	  if (getJobName().equals(PRICING_STAGING)) {
//		  doPricingStaging();
//	  }
	  if (getJobName().equals(PRICING_PRODUCTION)) {
		  doPricingProduction();
	  }
    } else {
      if (isLoggingDebug()) {
        logDebug("Scheduler is Disabled");
      }
    }
  }

  public void manualPricingProductionExecution() {
  	doPricingProduction();
  }

//  public void manualPricingStagingExecution() {
//	 doPricingStaging();
//  }

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
  public void setWorkFlowName(final String pWorkFlowName) {
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
  public void setProjectName(final String pProjectName) {
    mProjectName = pProjectName;
  }

  public String getSenderMailId() {
    return mSenderMailId;
  }

  public void setSenderMailId(final String pSenderMailId) {
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
   *          the mRecieverMailIdS to set
   */
  public void setRecieverMailId(final String[] mRecieverMailId) {
    this.mRecieverMailId = mRecieverMailId;
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
  public void setServerName(final String serverName) {
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
   * This variable signifies to enable or disable the scheduler. 
   * In specific environment this value is set from the property file
   * 
   * @param isShedulerEnabled
   *          the isShedulerEnabled to set
   */
  public void setSchedulerEnabled(final boolean pSchedulerEnabled) {
    this.mSchedulerEnabled = pSchedulerEnabled;
  }
  
  /**
	 * Helps to trigger schedule task manually
	 */
	public void executeDoScheduledTask() {
		if(isLoggingDebug()){
			logDebug("Entry executeDoScheduledTask");
		}
		doScheduledTask(null,new ScheduledJob("BBBPricingFeedJob", "", "", null, null, false));
		
		if(isLoggingDebug()){
			logDebug("Exit executeDoScheduledTask");
		}
	}

}
