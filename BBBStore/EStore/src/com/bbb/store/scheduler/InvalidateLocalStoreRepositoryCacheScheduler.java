package com.bbb.store.scheduler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.adapter.gsa.GSAItemDescriptor;
import atg.adapter.gsa.GSARepository;
import atg.core.exception.ContainerException;
import atg.nucleus.ServiceException;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;
import atg.servlet.ServletUtil;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;
import com.bbb.cache.BBBLocalStoreCacheLoader;
import com.bbb.cache.BBBRepositoryCacheLoader;

/**
 * 
 * @author vnalini This scheduler invalidates the local store repository cache
 *         if the invalidateCahe flag is set to true, and caches the content
 *         into the coherance scheduled at every 30 mins.
 * 
 */
public class InvalidateLocalStoreRepositoryCacheScheduler extends
		SingletonSchedulableService {

	private boolean mSchedulerEnabled;
	private boolean mInvalidateCache;
	private GSARepository mLocalStoreRepository;
	private GSARepository mReposInvalidateRepository;
	private String rqlQuery;

	private String itmDescStoreLocalInventory;
	private String itmDescInvalidateSchedule;
	private String propInvalidateStatus;
	private String invLocalStoreScheduler;
	private final String SUCCESS_SUBJECT = "Local Store Inventory Scheduler | SUCCESS";
	private final String FAILED_SUBJECT = "Local Store Inventory Scheduler | FAILED";
	private final String RECIPIENT_FROM = "RecipientFrom";
	private final String RECIPIENT_TO = "RecipientTo";
	private final String EMAIL_TEMP_INV_SCHEDULER = "txt_email_temp_inv_scheduler";
	private final String SUCCESS_MSG = "txt_email_temp_inv_success";
	private final String FAILURE_MSG = "txt_email_temp_inv_failure";
	private final String CONTENT = "content";
	private final String EMAIL_CONTENT = "emailContent";
	private final String SUBJECT = "subject";
	private final String SMTP_HOST_NAME = "smtp_host_name";
	private final String INV_LOCAL_STORE_SCHEDULER_CONFIG_VALUES = "invLocalStoreRepoSchedulerConfigValues";
	private LblTxtTemplateManager lblTxtTemplateManager;
	private BBBCatalogTools mCatalogTools;
	private BBBLocalStoreCacheLoader repositoryCacheLoader;
	private BBBRepositoryCacheLoader reposCacheLoader;
	String methodName = "doScheduledTask";

	/**
	 * @return mStoreRepository
	 */
	public GSARepository getLocalStoreRepository() {
		return this.mLocalStoreRepository;
	}

	/**
	 * @param pStoreRepository
	 */
	public void setLocalStoreRepository(GSARepository pLocalStoreRepository) {
		this.mLocalStoreRepository = pLocalStoreRepository;
	}

	/**
	 * @return mStoreRepository
	 */
	public GSARepository getReposInvalidateRepository() {
		return this.mReposInvalidateRepository;
	}

	/**
	 * @param pStoreRepository
	 */
	public void setReposInvalidateRepository(
			GSARepository pReposInvalidateRepository) {
		this.mReposInvalidateRepository = pReposInvalidateRepository;
	}

	/**
	 * Returns whether the scheduler is enable or not
	 * 
	 * @return the isShedulerEnabled
	 */
	public boolean isSchedulerEnabled() {
		return this.mSchedulerEnabled;
	}

	/**
	 * This variable signifies to enable or disable the scheduler in specific
	 * environment this value is set from the property file
	 * 
	 * @param pSchedulerEnabled
	 * 
	 */
	public void setSchedulerEnabled(boolean pSchedulerEnabled) {
		this.mSchedulerEnabled = pSchedulerEnabled;
	}

	public boolean isInvalidateCache() {
		return this.mInvalidateCache;
	}

	public void setInvalidateCache(boolean pInvalidateCache) {
		this.mInvalidateCache = pInvalidateCache;
	}

	public String getRqlQuery() {
		return rqlQuery;
	}

	public void setRqlQuery(String rqlQuery) {
		this.rqlQuery = rqlQuery;
	}

	public String getItmDescStoreLocalInventory() {
		return itmDescStoreLocalInventory;
	}

	public void setItmDescStoreLocalInventory(String itmDescStoreLocalInventory) {
		this.itmDescStoreLocalInventory = itmDescStoreLocalInventory;
	}

	public String getItmDescInvalidateSchedule() {
		return itmDescInvalidateSchedule;
	}

	public void setItmDescInvalidateSchedule(String itmDescInvalidateSchedule) {
		this.itmDescInvalidateSchedule = itmDescInvalidateSchedule;
	}

	public String getPropInvalidateStatus() {
		return propInvalidateStatus;
	}

	public void setPropInvalidateStatus(String propInvalidateStatus) {
		this.propInvalidateStatus = propInvalidateStatus;
	}

	public String getInvLocalStoreScheduler() {
		return invLocalStoreScheduler;
	}

	public void setInvLocalStoreScheduler(String invLocalStoreScheduler) {
		this.invLocalStoreScheduler = invLocalStoreScheduler;
	}

	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.mCatalogTools = catalogTools;
	}

	public BBBLocalStoreCacheLoader getRepositoryCacheLoader() {
		return repositoryCacheLoader;
	}

	public void setRepositoryCacheLoader(
			final BBBLocalStoreCacheLoader repositoryCacheLoader) {
		this.repositoryCacheLoader = repositoryCacheLoader;
	}

	public BBBRepositoryCacheLoader getReposCacheLoader() {
		return reposCacheLoader;
	}

	public void setReposCacheLoader(
			final BBBRepositoryCacheLoader reposCacheLoader) {
		this.reposCacheLoader = reposCacheLoader;
	}
	
	@Override
	public void doStartService() throws ServiceException{
		super.doStartService();
		if (isSchedulerEnabled()) {
			logInfo("in doStart() of InvalidateLocalStoreRepositoryCacheScheduler at : " + new Date());
			getRepositoryCacheLoader().loadCache();
		}
	}

	@Override
	public void doScheduledTask(final Scheduler scheduler,
			final ScheduledJob job) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.INVALIDATE_LOCAL_STORE_CACHE_SCHEDULER+ methodName);
		if (isSchedulerEnabled()) {
			if (isLoggingInfo()) {
				logDebug("Started Scheduler Job [" + getJobId() + ": "
						+ getJobName() + "]" + "at date = " + new Date());
				logDebug("Job Description :" + this.getJobDescription()
						+ " Scheduled at " + this.getSchedule());
			}
			
			Map<String, String> pPlaceHolderMap = new HashMap<String, String>();
			Map<String, String> recipients = findRecipients();
			RepositoryItem[] executeQueryInvSchedule=null;
			RepositoryItemDescriptor invalidateSchedule=null;
			MutableRepositoryItem itemForUpdate =null;

			try {

				 invalidateSchedule = getReposInvalidateRepository()
						.getItemDescriptor(getItmDescInvalidateSchedule());
				RepositoryView invalidateScheduleView = invalidateSchedule
						.getRepositoryView();
				RqlStatement statement = null;
				
				if (invalidateScheduleView != null) {
					Object[] params = new Object[1];
					params[0] = getInvLocalStoreScheduler();

					statement = RqlStatement.parseRqlStatement(getRqlQuery());
				   executeQueryInvSchedule = statement
							.executeQuery(invalidateScheduleView, params);
					// TICBCO TABLES sets it true
					if (null != executeQueryInvSchedule) {
						logDebug("InvalidateLocalStoreRepositoryCacheScheduler |doScheduledTask | executeQueryInvSchedule is not NULL");
					} else {
						logDebug("InvalidateLocalStoreRepositoryCacheScheduler |doScheduledTask | executeQueryInvSchedule IS NULL");
					}

					if (null != executeQueryInvSchedule
							&& null != executeQueryInvSchedule[0]
							&& executeQueryInvSchedule[0].getPropertyValue(
									getPropInvalidateStatus()).equals(BBBCoreConstants.ONE)) {
						logDebug("InvalidateLocalStoreRepositoryCacheScheduler |doScheduledTask | executeQueryInvSchedule value is TRUE");
					} else {
						logDebug("InvalidateLocalStoreRepositoryCacheScheduler |doScheduledTask | executeQueryInvSchedule value is FALSE");
					}

					if (null != executeQueryInvSchedule
							&& null != executeQueryInvSchedule[0]
							&& executeQueryInvSchedule[0].getPropertyValue(
									getPropInvalidateStatus()).equals(BBBCoreConstants.ONE)) {
						logDebug("InvalidateLocalStoreRepositoryCacheScheduler | doSchedule method | INVALIDATE_STATUS is | TRUE");

						Map cachingQueries = new HashMap<>();
						cachingQueries.put(BBBCoreConstants.STORE_LOCAL_INVENTORY, "ALL");

						Map emailFlag = new HashMap<>();
						emailFlag.put(BBBCoreConstants.SEND_EMAIL, BBBCoreConstants.TRUE);
						emailFlag.put(BBBCoreConstants.STORE_EMAIL, BBBCoreConstants.LOCAL_STORE_EMAIL);
						String repositoryId = executeQueryInvSchedule[0]
								.getRepositoryId();
						 itemForUpdate = getReposInvalidateRepository()
								.getItemForUpdate(
										repositoryId,
										invalidateSchedule
												.getItemDescriptorName());
						itemForUpdate.setPropertyValue(
								getPropInvalidateStatus(), BBBCoreConstants.TWO);
						getRepositoryCacheLoader().loadCache();

						// update the status to false and send success email
						if (executeQueryInvSchedule[0] != null) {
							logDebug("InvalidateLocalStoreRepositoryCacheScheduler | doSchedule method | executeQuery RepoItem-executeQueryInvSchedule[0] | "
									+ executeQueryInvSchedule[0]);
							
							itemForUpdate.setPropertyValue(
									getPropInvalidateStatus(), BBBCoreConstants.ZERO);

							pPlaceHolderMap.put(
									CONTENT,
									getLblTxtTemplateManager().getPageTextArea(
											SUCCESS_MSG, pPlaceHolderMap));

							String emailContent = getLblTxtTemplateManager()
									.getPageTextArea(EMAIL_TEMP_INV_SCHEDULER,
											pPlaceHolderMap);
							Map<String, String> map = new HashMap<String, String>();
							map.put(RECIPIENT_FROM,
									recipients.get(RECIPIENT_FROM));
							map.put(RECIPIENT_TO, recipients.get(RECIPIENT_TO));
							map.put(RECIPIENT_FROM,
									recipients.get(RECIPIENT_FROM));
							map.put(EMAIL_CONTENT, emailContent);
							map.put(SUBJECT, SUCCESS_SUBJECT);
							map.put("smtpHostName",
									recipients.get(SMTP_HOST_NAME));

							BBBUtility.sendEmail(map);

							logDebug("InvalidateLocalStoreRepositoryCacheScheduler | doSchedule method | repositoryId | "
									+ repositoryId + " |updated to FALSE");
						}

					}
				}
				BBBPerformanceMonitor.end(BBBPerformanceConstants.INVALIDATE_LOCAL_STORE_CACHE_SCHEDULER+ methodName);
			} catch (Exception exc) {
		
				pPlaceHolderMap.put(
						CONTENT,
						getLblTxtTemplateManager().getPageTextArea(FAILURE_MSG,
								pPlaceHolderMap)
								+ ((ContainerException) exc).getSourceException());

				Map<String, String> map = new HashMap<String, String>();
				map.put(RECIPIENT_FROM, recipients.get(RECIPIENT_FROM));
				map.put(RECIPIENT_TO, recipients.get(RECIPIENT_TO));
				map.put("smtpHostName", recipients.get(SMTP_HOST_NAME));
				String emailContent = getLblTxtTemplateManager()
						.getPageTextArea(EMAIL_TEMP_INV_SCHEDULER,
								pPlaceHolderMap);
				map.put(EMAIL_CONTENT, emailContent);
				map.put(SUBJECT, FAILED_SUBJECT);
				BBBUtility.sendEmail(map);
				logError(
						"Exception from "
								+ getJobName()
								+ BBBCatalogErrorCodes.UNABLE_TO_CACHE_DATA_EXCEPTION,
						exc);
				
				 try {
					 if (null != executeQueryInvSchedule) {
							String repositoryId = executeQueryInvSchedule[0]
									.getRepositoryId();
					itemForUpdate = getReposInvalidateRepository()
							.getItemForUpdate(
									repositoryId,
									invalidateSchedule
											.getItemDescriptorName());
					itemForUpdate.setPropertyValue(
							getPropInvalidateStatus(), BBBCoreConstants.ZERO);
				 }
				} catch (RepositoryException e) {
					logError(
							"itemForUpdate Exception from "
									+ getJobName()
									+ BBBCatalogErrorCodes.UNABLE_TO_CACHE_DATA_EXCEPTION,
							exc);
				}
				 BBBPerformanceMonitor.end(BBBPerformanceConstants.INVALIDATE_LOCAL_STORE_CACHE_SCHEDULER+ methodName);
			
			}
		} else {
			if (isLoggingInfo()) {
				logInfo(getJobName() + " task is disabled");
				BBBPerformanceMonitor.end(BBBPerformanceConstants.INVALIDATE_LOCAL_STORE_CACHE_SCHEDULER+ methodName);
			}
		}
	}

	/**
	 * Scheduled task to run the scheduler.
	 */
	public void doScheduledTask() {
		this.doScheduledTask(null, null);
	}

	public Map<String, String> findRecipients() {
		Map<String, String> pPlaceHolderMap = new HashMap<String, String>();
		String recipientTo = null;
		String recipientFrom = null;
		String host = null;
		try {

			List<String> recipientToList = getCatalogTools()
					.getAllValuesForKey(
							INV_LOCAL_STORE_SCHEDULER_CONFIG_VALUES,
							RECIPIENT_TO);
			if (null != recipientToList && !recipientToList.isEmpty()) {
				recipientTo = getCatalogTools().getAllValuesForKey(
						INV_LOCAL_STORE_SCHEDULER_CONFIG_VALUES, RECIPIENT_TO)
						.get(0);
			}
			List<String> recipientFromList = getCatalogTools()
					.getAllValuesForKey(
							INV_LOCAL_STORE_SCHEDULER_CONFIG_VALUES,
							RECIPIENT_FROM);
			if (null != recipientFromList && !recipientFromList.isEmpty()) {
				recipientFrom = getCatalogTools()
						.getAllValuesForKey(
								INV_LOCAL_STORE_SCHEDULER_CONFIG_VALUES,
								RECIPIENT_FROM).get(0);
			}
			List<String> hostList = getCatalogTools().getAllValuesForKey(
					"SMTPConfig", SMTP_HOST_NAME);
			if (null != hostList && !hostList.isEmpty()) {
				host = hostList.get(0);
			}

		} catch (BBBSystemException e) {
			logError("BBBSystemException from " + getJobName(), e);
		} catch (BBBBusinessException e) {
			logError("BBBBusinessException from " + getJobName(), e);
		}
		pPlaceHolderMap.put(RECIPIENT_TO, recipientTo);
		pPlaceHolderMap.put(RECIPIENT_FROM, recipientFrom);
		pPlaceHolderMap.put(SMTP_HOST_NAME, host);

		return pPlaceHolderMap;

	}

	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(
			LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}
}
