/**
 * 
 */
package com.bbb.commerce.giftregistry.scheduler;

import java.sql.SQLException;
import java.util.Date;

import atg.nucleus.Nucleus;
import atg.repository.RepositoryItem;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.servlet.pipeline.HeadPipelineServlet;

import com.bbb.commerce.giftregistry.manager.GiftRegistryRecommendationManager;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools;
import com.bbb.commerce.giftregistry.tool.RegistryBulkNotificationVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;

/**
 * @author pbahet
 *
 */
public class SendMonthlyRecommEmailScheduler extends
		SingletonSchedulableService {

	private GiftRegistryTools mGiftRegistryTools;
	private GiftRegistryRecommendationManager giftRegistryRecommendationManager;
	private int batchSize;
	private boolean enabled;
	
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param isEnabled
	 *            the isEnabled to set
	 */
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the giftRegistryTools
	 */
	public GiftRegistryTools getGiftRegistryTools() {
		return this.mGiftRegistryTools;
	}

	/**
	 * @param pGiftRegistryTools
	 *            the giftRegistryTools to set
	 */
	public void setGiftRegistryTools(final GiftRegistryTools pGiftRegistryTools) {
		this.mGiftRegistryTools = pGiftRegistryTools;
	}
	
	/** @return the giftRegistryManager */
    public final GiftRegistryRecommendationManager getGiftRegistryRecommendationManager() {
        return this.giftRegistryRecommendationManager;
    }

    /** @param pGiftRegistryManager the giftRegistryManager to set */
    public final void setGiftRegistryRecommendationManager(final GiftRegistryRecommendationManager giftRegistryRecommendationManager) {
        this.giftRegistryRecommendationManager = giftRegistryRecommendationManager;
    }
	
	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	@Override
	public void doScheduledTask(Scheduler paramScheduler,
			ScheduledJob paramScheduledJob) {
		Date schedulerStartDate = new Date();
		logDebug("SendMonthlyRecommEmailScheduler Started at "+schedulerStartDate);
		if(!isEnabled()) {
			logDebug("SendWeeklyRecommEmailScheduler is disabled. Please enable the scheduler "+schedulerStartDate);
			return;
		}
		BBBPerformanceMonitor.start("SendMonthlyRecommEmailScheduler Started at "+schedulerStartDate);
		try {
			getGiftRegistryTools().refreshMattView(BBBGiftRegistryConstants.EMAIL_OPT_MONTHLY);
			getGiftRegistryTools().refreshRepoItemCache(BBBGiftRegistryConstants.RECOMMENDATION_EMAIL_MONTHLY);
			boolean breakLoop=false;
			int counter=0;
			while(!breakLoop) {
				RepositoryItem[] recomEmails = getGiftRegistryTools().fetchRecomEmail(counter*getBatchSize(),getBatchSize(),
						BBBGiftRegistryConstants.RECOMMENDATION_EMAIL_MONTHLY);
				if(null == recomEmails) {
					breakLoop = true;
					break;
				}
				else
				{
					sendRegistrantEmail(recomEmails);
					if(recomEmails.length < getBatchSize()) {
						breakLoop = true;
						break;
					}
				}
				counter++;
			}
			getGiftRegistryTools().logBatchJobStatus(BBBGiftRegistryConstants.EMAIL_OPT_MONTHLY,schedulerStartDate);
		} catch (BBBSystemException e) {
			logError("SendMonthlyRecommEmailScheduler Started at "+schedulerStartDate+" Failed",e);
		} catch (BBBBusinessException e) {
			logError("SendMonthlyRecommEmailScheduler Started at "+schedulerStartDate+" Failed",e);
		} catch (SQLException e) {
			if (e.getMessage() != null
					&& e.getMessage().contains("Registry not found")) {
				logInfo("SendMonthlyRecommEmailScheduler doScheduledTask started :: Registry not found");
				
			} else {
			logError("SendMonthlyRecommEmailScheduler Started at "+schedulerStartDate+" Failed",e);
			}
		}
		BBBPerformanceMonitor.end("SendMonthlyRecommEmailScheduler Ended");
	}
	
	public void sendRegistrantEmail(RepositoryItem[] dailyRecomEmails) throws BBBSystemException {
		logDebug("Entry convertToVOAndSendEmail");
		RegistryBulkNotificationVO registryBulkNotificationVO = new RegistryBulkNotificationVO();
		for(RepositoryItem dailyRecomEmail : dailyRecomEmails) {
			registryBulkNotificationVO.setRegistryId(dailyRecomEmail.getPropertyValue(BBBGiftRegistryConstants.REGISTRY_ID).toString());
			registryBulkNotificationVO.setProfileId(dailyRecomEmail.getPropertyValue(BBBGiftRegistryConstants.USER_ID).toString());
			registryBulkNotificationVO.setRegistFirstName(dailyRecomEmail.getPropertyValue(BBBGiftRegistryConstants.FIRST_NAME).toString());
			registryBulkNotificationVO.setRegistLastName(dailyRecomEmail.getPropertyValue(BBBGiftRegistryConstants.LAST_NAME).toString());
			registryBulkNotificationVO.setRecomCount((Integer)dailyRecomEmail.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDATION_COUNT));
			registryBulkNotificationVO.setRegistrantEmail(dailyRecomEmail.getPropertyValue(BBBGiftRegistryConstants.REGISTRANT_EMAIL).toString());
			registryBulkNotificationVO.setEventType(dailyRecomEmail.getPropertyValue(BBBGiftRegistryConstants.EVENT_TYPE).toString());
			registryBulkNotificationVO.setSiteId(dailyRecomEmail.getPropertyValue(BBBGiftRegistryConstants.SITE_ID).toString());
			DynamoHttpServletRequest request = (DynamoHttpServletRequest)(((HeadPipelineServlet) Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.DYNAMO_HANDLER)).getRequest(null));
			// In schedular there is no request object avaliable that why create a reques
			DynamoHttpServletResponse response = request.getResponse();
			ServletUtil.setCurrentRequest(request);
			ServletUtil.setCurrentResponse(response);
			DynamoHttpServletRequest req = ServletUtil.getCurrentRequest();
			req.setParameter(BBBGiftRegistryConstants.EMAIL_TYPE, BBBGiftRegistryConstants.SCHEDULED_RECOMMENDATION_EMAIL_TYPE);
			req.setParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE, BBBGiftRegistryConstants.SCHEDULED_RECOMMENDATION_EMAIL_FREQ_MONTHLY);
			req.setParameter(BBBCoreConstants.CHANNEL,BBBCoreConstants.DEFAULT_CHANNEL_VALUE);
			// adding Site id for the scheduler
			req.setParameter(BBBCoreConstants.IS_FROM_SCHEDULER, BBBCoreConstants.TRUE);
			req.setParameter(BBBCoreConstants.SITE_ID, registryBulkNotificationVO.getSiteId());
			getGiftRegistryRecommendationManager().sendRegistrantScheduledBulkEmail(registryBulkNotificationVO);
			logDebug("RegistryBulkNotificationVO"+registryBulkNotificationVO.toString());
		}
	}
	
	/**
	 * Helps to trigger schedule task manually
	 */
	public void executeDoScheduledTask() {
		logDebug("Entry executeDoScheduledTask");
		doScheduledTask(null,new ScheduledJob("SendMonthlyRecommEmailScheduler", "", "", null, null, false));
		logDebug("Exit executeDoScheduledTask");
	}
	
	@Override
	public void logDebug(String pMessage) {
		if(isLoggingDebug()){
			super.logDebug(pMessage);
		}
	}
}
