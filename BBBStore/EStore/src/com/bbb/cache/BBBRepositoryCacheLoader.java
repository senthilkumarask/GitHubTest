package com.bbb.cache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import atg.adapter.gsa.GSARepository;
import atg.nucleus.ServiceException;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

public class BBBRepositoryCacheLoader extends BBBGenericService {

	// private static final String CATALOG_CACHE_QUERIES ="CatalogCacheQueries";
	private static final String CLS_NAME = "OmnitureBoostedProductTools";
	private static final String RANGE = " RANGE ";
	private final String SUCCESS_SUBJECT = "Local Store Inventory Scheduler | SUCCESS";
	private final String FAILED_SUBJECT = "Local Store Inventory Scheduler | FAILED";
	private final String RECIPIENT_FROM = "RecipientFrom";
	private final String RECIPIENT_TO = "RecipientTo";
	private final String EMAIL_TEMP_INV_SCHEDULER = "txt_email_temp_inv_scheduler";
	private final String SUCCESS_MSG = "txt_email_temp_inv_success";
	private final String FAILURE_MSG = "txt_email_temp_inv_failure";
	private final String SMTP_HOST_NAME = "smtp_host_name";
	private LblTxtTemplateManager lblTxtTemplateManager;
	private final String CONTENT = "content";
	private final String EMAIL_CONTENT = "emailContent";
	private final String SUBJECT = "subject";
	private final String INV_LOCAL_STORE_SCHEDULER_CONFIG_VALUES = "invLocalStoreRepoSchedulerConfigValues";

	private BBBCatalogTools catalogTools;
	private String skuThresholdsRql;
	private int poolSize;
	private int rangeCount;
	private ExecutorService threadPool;
	private GSARepository localStoreRepository;
	private GSARepository mReposInvalidateRepository;
	private String itmDescInvalidateSchedule;
	private String invLocalStoreScheduler;
	private String rqlQuery;

	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	public String getSkuThresholdsRql() {
		return skuThresholdsRql;
	}

	public void setSkuThresholdsRql(String skuThresholdsRql) {
		this.skuThresholdsRql = skuThresholdsRql;
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public int getRangeCount() {
		return rangeCount;
	}

	public void setRangeCount(int rangeCount) {
		this.rangeCount = rangeCount;
	}

	public ExecutorService getThreadPool() {
		return threadPool;
	}

	public void setThreadPool(ExecutorService threadPool) {
		this.threadPool = threadPool;
	}

	public GSARepository getLocalStoreRepository() {
		return localStoreRepository;
	}

	public void setLocalStoreRepository(GSARepository localStoreRepository) {
		this.localStoreRepository = localStoreRepository;
	}

	@Override
	public void doStartService() throws ServiceException {
		super.doStartService();
		threadPool = Executors.newFixedThreadPool(poolSize);
		// startCatalogCaching();
		// cacheSkuThresholds();
		// loadRepositoryCache(this.getLocalStoreRepository(), null);
	}

	public void loadRepositoryCache(final MutableRepository repository,
			Map<String, String> cachingQueries, Map<String, String> emailFlag) {

		if (isLoggingDebug()) {
			logDebug(CLS_NAME
					+ " [ loadRepositoryCache ] method starts for repository :: "
					+ repository.getRepositoryName() + " CachingQueries is :: "
					+ cachingQueries);
		}

		try {

			if (!BBBUtility.isMapNullOrEmpty(cachingQueries)) {

				for (final String viewName : cachingQueries.keySet()) {
					final String rqlQuery = cachingQueries.get(viewName);
					logDebug("View Name is :: " + viewName);
					Runnable newQueryThread = new Runnable() {
						public void run() {
							logDebug("starting thread=" + Thread.currentThread().getName());
							int totalCount = BBBCoreConstants.ZERO;
							String rqlQueryRange = rqlQuery + RANGE
									+ totalCount + BBBCoreConstants.PLUS
									+ getRangeCount();
							totalCount = this.exeuteQuery(rqlQueryRange);
							//totalCount = 1;
							int count = totalCount;
							while (count == getRangeCount()) {
								rqlQueryRange = rqlQuery + RANGE + totalCount
										+ BBBCoreConstants.PLUS
										+ getRangeCount();
								count = this.exeuteQuery(rqlQueryRange);
								totalCount = totalCount + count;
							}
						}

						private int exeuteQuery(String query) {
							int itemCount = BBBCoreConstants.ZERO;
							try {

								logDebug("RQL to execute is :: " + query);
								logDebug("Free Memory before query is:"
										+ getMemoryUsed());
								Object[] params = new Object[BBBCoreConstants.ONE];
								long startTime = System.currentTimeMillis();
								logDebug("Start time before calling the external coherance load:"
										+ startTime);
								RepositoryItem[] repositoryItems = getCatalogTools()
										.executeRQLQuery(query, params,
												viewName, repository);
								long endTime = System.currentTimeMillis();
								logDebug("End time after calling the external coherance load:"
										+ endTime);
								logDebug("Free Memory after query is:"
										+ getMemoryUsed());
								if (repositoryItems != null
										&& repositoryItems.length >= 0) {
									logDebug("Number of items of type "
											+ viewName + " cached are "
											+ repositoryItems.length);
									itemCount = repositoryItems.length;
									//COMMENTED SIZE OF
									/*
									try {
										logDebug("Size of 1 item is : "
												+ sizeof(repositoryItems));
										System.out
												.println("Size of 1 item is : "
														+ sizeof(repositoryItems));
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}*/
								} else {
									logDebug("No records fetch for items type "
											+ viewName);
								}

								logDebug("RQL executed successfully :: "
										+ query);

							} catch (BBBSystemException exception) {
								
								logDebug("BBBSystemException | Sending FAILURE EMAIL - BBBSystemException");
								Map<String, String> pPlaceHolderMap = new HashMap<String, String>();
								Map<String, String> recipients = findRecipients();

								pPlaceHolderMap.put(
										CONTENT,
										getLblTxtTemplateManager()
												.getPageTextArea(FAILURE_MSG,
														pPlaceHolderMap)
												+ exception);

								Map<String, String> map = new HashMap<String, String>();
								map.put(RECIPIENT_FROM,
										recipients.get(RECIPIENT_FROM));
								map.put(RECIPIENT_TO,
										recipients.get(RECIPIENT_TO));
								map.put("smtpHostName",
										recipients.get(SMTP_HOST_NAME));
								String emailContent = getLblTxtTemplateManager()
										.getPageTextArea(
												EMAIL_TEMP_INV_SCHEDULER,
												pPlaceHolderMap);
								map.put(EMAIL_CONTENT, emailContent);
								map.put(SUBJECT, FAILED_SUBJECT);
								BBBUtility.sendEmail(map);
								logError(
										"RepositoryException from "
												+ "BBBRepositoryCacheLoader"
												+ BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
										exception);
								logError(
										"BBBSystemException occured for query:"
												+ query, exception);

							}
							return itemCount;
						}
					};
					threadPool.execute(newQueryThread);
				}
				if (emailFlag.containsKey("sendEmail")) {
					logDebug("BBBRepositoryCacheLoader | Sending Success EMAIL ");

					Map<String, String> pPlaceHolderMap = new HashMap<String, String>();
					Map<String, String> recipients = findRecipients();

					pPlaceHolderMap.put(CONTENT, getLblTxtTemplateManager()
							.getPageTextArea(SUCCESS_MSG, pPlaceHolderMap));

					String emailContent = getLblTxtTemplateManager()
							.getPageTextArea(EMAIL_TEMP_INV_SCHEDULER,
									pPlaceHolderMap);
					Map<String, String> map = new HashMap<String, String>();
					map.put(RECIPIENT_FROM, recipients.get(RECIPIENT_FROM));
					map.put(RECIPIENT_TO, recipients.get(RECIPIENT_TO));
					map.put(RECIPIENT_FROM, recipients.get(RECIPIENT_FROM));
					map.put(EMAIL_CONTENT, emailContent);
					map.put(SUBJECT, SUCCESS_SUBJECT);
					map.put("smtpHostName", recipients.get(SMTP_HOST_NAME));

					BBBUtility.sendEmail(map);
					// Set the flag to FALSE
					RepositoryItemDescriptor invalidateSchedule = getReposInvalidateRepository()
							.getItemDescriptor(getItmDescInvalidateSchedule());
					RepositoryView invalidateScheduleView = invalidateSchedule
							.getRepositoryView();
					RqlStatement statement = null;
					if (invalidateScheduleView != null) {
						Object[] params = new Object[1];
						params[0] = getInvLocalStoreScheduler();

						statement = RqlStatement
								.parseRqlStatement(getRqlQuery());
						RepositoryItem[] executeQueryInvSchedule = statement
								.executeQuery(invalidateScheduleView, params);
					}

				}
			}
		} catch (RepositoryException re) {
			logDebug("BBBRepositoryCacheLoader | Sending FAILURE EMAIL - Repository Exception");

			Map<String, String> pPlaceHolderMap = new HashMap<String, String>();
			Map<String, String> recipients = findRecipients();

			pPlaceHolderMap.put(CONTENT, getLblTxtTemplateManager()
					.getPageTextArea(FAILURE_MSG, pPlaceHolderMap) + re);

			Map<String, String> map = new HashMap<String, String>();
			map.put(RECIPIENT_FROM, recipients.get(RECIPIENT_FROM));
			map.put(RECIPIENT_TO, recipients.get(RECIPIENT_TO));
			map.put("smtpHostName", recipients.get(SMTP_HOST_NAME));
			String emailContent = getLblTxtTemplateManager().getPageTextArea(
					EMAIL_TEMP_INV_SCHEDULER, pPlaceHolderMap);
			map.put(EMAIL_CONTENT, emailContent);
			map.put(SUBJECT, FAILED_SUBJECT);
			BBBUtility.sendEmail(map);
			logError(
					"RepositoryException from "
							+ "BBBRepositoryCacheLoader"
							+ BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					re);
			logError("RepositoryException occured for query:" + re);
		} finally {

			logDebug("Exiting BBBCatalogStartupCache.startCatalogCaching()");
			threadPool.shutdown();
		}

	}

	private static long getMemoryUsed() {
		long totalMemory = Runtime.getRuntime().totalMemory();
		long freeMemory = Runtime.getRuntime().freeMemory();
		return (totalMemory - freeMemory);
	}

	public static int sizeof(Object obj) throws IOException {

		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				byteOutputStream);

		objectOutputStream.writeObject(obj);
		objectOutputStream.flush();
		objectOutputStream.close();

		return byteOutputStream.toByteArray().length;
	}

	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(
			LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
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
			logError("BBBSystemException from RepositoryCacheLoader", e);
		} catch (BBBBusinessException e) {
			logError("BBBBusinessException from RepositoryCacheLoader", e);
		}
		pPlaceHolderMap.put(RECIPIENT_TO, recipientTo);
		pPlaceHolderMap.put(RECIPIENT_FROM, recipientFrom);
		pPlaceHolderMap.put(SMTP_HOST_NAME, host);

		return pPlaceHolderMap;

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

	public String getItmDescInvalidateSchedule() {
		return itmDescInvalidateSchedule;
	}

	public void setItmDescInvalidateSchedule(String itmDescInvalidateSchedule) {
		this.itmDescInvalidateSchedule = itmDescInvalidateSchedule;
	}

	public String getInvLocalStoreScheduler() {
		return invLocalStoreScheduler;
	}

	public void setInvLocalStoreScheduler(String invLocalStoreScheduler) {
		this.invLocalStoreScheduler = invLocalStoreScheduler;
	}

	public String getRqlQuery() {
		return rqlQuery;
	}

	public void setRqlQuery(String rqlQuery) {
		this.rqlQuery = rqlQuery;
	}
}
