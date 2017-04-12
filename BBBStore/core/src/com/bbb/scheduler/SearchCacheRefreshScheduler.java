package com.bbb.scheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;

import atg.adapter.gsa.GSARepository;
import atg.nucleus.Nucleus;
import atg.nucleus.RequestScopeManager;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.servlet.pipeline.HeadPipelineServlet;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.BBBConfigTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.ISearch;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.query.SortCriteria;
import com.bbb.utils.BBBUtility;


/**
 * This scheduler checks for the cache_refresh_required flag in BBB_DEPLOYMENT_POLLING
 * table and based on that it queries the Omniture boosted repository for keywords of
 * OBP products and invokes perform Search to replace the keywords in near-keyword-search-cache
 * 
 * @author asi162
 *
 */
public class SearchCacheRefreshScheduler extends SingletonSchedulableService{

	
	private ExecutorService threadPool;
	private GSARepository omnitureBoostedRepository;
	private GSARepository catalogRepository;
	private BBBCatalogTools catalogTools;
	private boolean schedulerEnabled;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private String dataCenter;
	private String deploymentId;
	private String datasourcePim;
	private ArrayList<String> siteId;
	private ISearch endecaSearch;	
	private final String SMTP_HOST_NAME = "smtp_host_name";
	private final String SMTPHOSTNAME ="smtpHostName";
	private final String EMAIL_CONTENT = "emailContent";
	private final String SUBJECT = "subject";
	private String pageNum;
	private String pageSize;
	private Long threadSleepTimeinMl;
	

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}




	private BBBConfigTools bbbConfigTools;
	private String successMessage;
	private String cacheStatus;
	private int totalSize;
	private int poolSize;
	private String cacheRefreshQuery;
	
	public String getCacheRefreshQuery() {
		return cacheRefreshQuery;
	}

	public void setCacheRefreshQuery(String cacheRefreshQuery) {
		this.cacheRefreshQuery = cacheRefreshQuery;
	}

	public String getUpdateDeploymentPollingQuery() {
		return updateDeploymentPollingQuery;
	}

	public void setUpdateDeploymentPollingQuery(String updateDeploymentPollingQuery) {
		this.updateDeploymentPollingQuery = updateDeploymentPollingQuery;
	}




	private String updateDeploymentPollingQuery;
	private  String keywordCountQuery;
	private String brandIdQuery;
	private String brandCountQuery;
	private String categoryCountQuery;
	private String categoryQuery;
    private  String keywordsQuery;
	private  String reportType;
	private final String EMAIL_KEYWORD_SEARCH_SCHEDULER = "txt_email_keyword_refresh_scheduler";
	private final String FAILURE_MSG = "txt_keyword_refresh_failure_email";
	private String Keyword = "keyword";
	private String brandId = "brand_id";
	private String categoryId = "category_Id";
	private String dynamoHandler = "/atg/dynamo/servlet/dafpipeline/DynamoHandler";
	String methodName = "doScheduledTask";
	String methodName1 = "populatePopularKeywordsCache";
	String methodName2 = "populateBrandCache";
	String methodName3 = "populateCategoriesCache";
	String method4 = "buildPopularKeywordQuery";
	String method5 = "buildBrandQuery";
	String method6= "buildCategoryQuery";
	public String getKeywordCountQuery() {
		return keywordCountQuery;
	}

	public void setKeywordCountQuery(String keywordCountQuery) {
		this.keywordCountQuery = keywordCountQuery;
	}

	public String getKeywordsQuery() {
		return keywordsQuery;
	}

	public void setKeywordsQuery(String keywordsQuery) {
		this.keywordsQuery = keywordsQuery;
	}

	
	HashMap<String, String> endecaKeywords = new HashMap<String,String>();

	private Map<String, String> siteIdMap;
	private Map<String, String> fixedParentId;

	
	

	public Map<String, String> getSiteIdMap() {
		return siteIdMap;
	}

	public void setSiteIdMap(Map<String, String> siteIdMap) {
		this.siteIdMap = siteIdMap;
	}

	public String getCacheStatus() {
		return cacheStatus;
	}

	public void setCacheStatus(String cacheStatus) {
		this.cacheStatus = cacheStatus;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public String getPageNum() {
		return pageNum;
	}

	public void setPageNum(String pageNum) {
		this.pageNum = pageNum;
	}


	public ISearch getEndecaSearch() {
		return endecaSearch;
	}

	public void setEndecaSearch(ISearch endecaSearch) {
		this.endecaSearch = endecaSearch;
	}

	
	public ArrayList<String> getSiteId() {
		return siteId;
	}

	public void setSiteId(ArrayList<String> siteId) {
		this.siteId = siteId;
	}

	public GSARepository getOmnitureBoostedRepository() {
		return omnitureBoostedRepository;
	}

	public void setOmnitureBoostedRepository(GSARepository omnitureBoostedRepository) {
		this.omnitureBoostedRepository = omnitureBoostedRepository;
	}

	public boolean isSchedulerEnabled() {
		return schedulerEnabled;
	}

	public void setSchedulerEnabled(boolean schedulerEnabled) {
		this.schedulerEnabled = schedulerEnabled;
	}

	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}


	public String getDataCenter() {
		return dataCenter;
	}

	public void setDataCenter(String dataCenter) {
		this.dataCenter = dataCenter;
	}

	public String getDeploymentId() {
		return deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	public String getDatasourcePim() {
		return datasourcePim;
	}

	public void setDatasourcePim(String datasourcePim) {
		this.datasourcePim = datasourcePim;
	}

	@Override
	public void doScheduledTask(Scheduler arg0, ScheduledJob arg1) {	
		
		if (this.isLoggingDebug()) {
            this.logDebug("SearchCacheRefreshScheduler: - Started");
        }
		
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_CACHE_SCHEDULER+ methodName);
		if(!isSchedulerEnabled()){
			logDebug("Scheduler is disabled");
			return;
		}
		
		Connection connection = null ;
		boolean isErrorInKeywordRefresh = false;
		boolean isErrorInCategoryRefresh = false;
		boolean isErrorInBrandRefresh = false;
		PreparedStatement statement = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String isCacheRefreshRequired = null;

		try {
			//Check for particular deployment ID and dataCenter if cache refresh required
			connection = openConnection();
			statement = connection.prepareStatement(getCacheRefreshQuery());
			statement.setString(1, getDeploymentId());
			statement.setString(2, getDataCenter());
            resultSet = statement.executeQuery();
			while(resultSet.next()){
				isCacheRefreshRequired = resultSet.getString(BBBCoreConstants.CACHE_REFRESH_REQUIRED);					
				break;
			}		
			
			if(isLoggingDebug()){
				logDebug("Value of Cache_refresh_required column is " + isCacheRefreshRequired);
			}
			// if cache refresh required then fetch top keywords depending on total size mentioned and cache them.
			if(isCacheRefreshRequired!=null && isCacheRefreshRequired.equalsIgnoreCase(getCacheStatus())){
				
				int poolSize =getBbbConfigTools().getValueForConfigKey(BBBCoreConstants.SEARCH_CACHE_SCHEDULER_TYPE, BBBCoreConstants.POOL_SIZE, getPoolSize());
				boolean populateKeywordCacheFlag = Boolean.valueOf(getBbbConfigTools().getConfigKeyValue(BBBCoreConstants.SEARCH_CACHE_SCHEDULER_TYPE, BBBCoreConstants.POPULATE_KEYWORD_CACHE_FLAG, BBBCoreConstants.FALSE));
				boolean populateCategoryCacheFlag = Boolean.valueOf(getBbbConfigTools().getConfigKeyValue(BBBCoreConstants.SEARCH_CACHE_SCHEDULER_TYPE, BBBCoreConstants.POPULATE_CATEGORY_CACHE_FLAG, BBBCoreConstants.FALSE));
				boolean populateBrandCacheFlag = Boolean.valueOf(getBbbConfigTools().getConfigKeyValue(BBBCoreConstants.SEARCH_CACHE_SCHEDULER_TYPE, BBBCoreConstants.POPULATE_BRAND_CACHE_FLAG,BBBCoreConstants.FALSE));
				
				if(isLoggingDebug()){
					logDebug("Pool Size is: " + poolSize);
				}
				
				threadPool = Executors.newFixedThreadPool(poolSize);
				
				for(String siteId:getSiteId()){	
					if(isLoggingDebug()){
						logDebug("Value of siteId is " + siteId);
					}
					if(isLoggingDebug()){
						logDebug("Value of populateKeywordCacheFlag is " + populateKeywordCacheFlag);
					}
					// To get the top(n) popular keywords and query endeca
					if(populateKeywordCacheFlag) {
						isErrorInKeywordRefresh = populatePopularKeywordsCache(siteId,threadPool);
						
					}
					if(isLoggingDebug()){
						logDebug("Value of populateCategoryCacheFlag is " + populateCategoryCacheFlag);
					}
					//To get all L2 L3 categories based on site id and query endeca
					if(populateCategoryCacheFlag) {
						isErrorInCategoryRefresh = populteCategoriesCache(siteId,threadPool);
						
						
					}
					if(isLoggingDebug()){
						logDebug("Value of populateBrandCacheFlag is " + populateBrandCacheFlag);
					}
					//To get all brands based on site id and query endeca
					if(populateBrandCacheFlag) {
						isErrorInBrandRefresh = populateBrandCache(siteId,threadPool);
											}
			
				}
				// if error occured while fetching popular keywords, brands and categories set column CACHE_REFRESH_REQUIRED with CacheRefreshFailed otherwise CacheRefreshDone
				if(!isErrorInKeywordRefresh && !isErrorInCategoryRefresh && !isErrorInBrandRefresh){
					updateCacheRefreshColumn(connection,getSuccessMessage()); // update the column value for Failure Message
				}
				
				
				
			}
			
		} catch (SQLException e) {
			logError("Error while creating statement",e);
			sendErrorEmail("Error while creating statement"  + e.getMessage());
		} catch (BBBSystemException e) {
			logError("Error while  running SearchCacheSchedulerKeys",e);
			sendErrorEmail("Error while running SearchCacheSchedulerKeys. " + e);
		} finally{
				try{
					
					if(statement!=null){
						statement.close();
					}
					if(preparedStatement!=null){
						preparedStatement.close();
					}
				}
				catch (SQLException e){
					logError("error in closing statement", e);
				}
			
				closeConnection(connection);
				BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_CACHE_SCHEDULER+ methodName);
		}
		
		if (this.isLoggingDebug()) {
            this.logDebug("SearchCacheRefreshScheduler: - Ended");
        }
		
	}


	/**
	 * THis method send error email 
	 * @param message
	 */
	private void sendErrorEmail(String message) {
		Map<String,String> placeholderMap = new HashMap<String,String>();
		
		placeholderMap.put("content", getLblTxtTemplateManager().getPageTextArea(FAILURE_MSG, placeholderMap) + message);
		
		String emailContent = getLblTxtTemplateManager().getPageTextArea(EMAIL_KEYWORD_SEARCH_SCHEDULER, placeholderMap);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(BBBCoreConstants.RECIPIENT_TO, getBbbConfigTools().getConfigKeyValue(BBBCoreConstants.SEARCH_CACHE_SCHEDULER_TYPE, BBBCoreConstants.RECIPIENT_TO, BBBCoreConstants.BLANK));
		map.put(BBBCoreConstants.RECIPIENT_FROM,getBbbConfigTools().getConfigKeyValue(BBBCoreConstants.SEARCH_CACHE_SCHEDULER_TYPE, BBBCoreConstants.RECIPIENT_FROM, BBBCoreConstants.BLANK));
		map.put(EMAIL_CONTENT, emailContent);
		map.put(SUBJECT, getBbbConfigTools().getConfigKeyValue(BBBCoreConstants.SEARCH_CACHE_SCHEDULER_TYPE, BBBCoreConstants.EMAIL_SUBJECT, BBBCoreConstants.BLANK));			
		map.put(SMTPHOSTNAME,	getBbbConfigTools().getConfigKeyValue(BBBCoreConstants.SMTP_CONFIG, SMTP_HOST_NAME, BBBCoreConstants.BLANK));			
		BBBUtility.sendEmail(map);	
	}

	/**
	 * This method updates the cache_refresh_required column after the scheduler has refreshed the cache.
	 * 
	 * @param connection
	 * @throws SQLException
	 */
	private void updateCacheRefreshColumn(Connection connection,String cacheRefreshStatus) throws SQLException {
		
		PreparedStatement preparedStatement;
				
		preparedStatement = connection.prepareStatement(getUpdateDeploymentPollingQuery());			
		preparedStatement.setString(1, cacheRefreshStatus);
		preparedStatement.setString(2, getDeploymentId());
		preparedStatement.setString(3, getDataCenter());
		preparedStatement.executeUpdate();
					
		if(isLoggingDebug()){
			logDebug("Updated the CACHE_REFRESH_REQUIRED column in BBB_DEPLOYMENT_POLLING");
		}
		preparedStatement.close();
		
	}

	/**
	 * Build query for popular keywords
	 * @param searchQuery
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	private void buildPopularKeywordQuery(SearchQuery searchQuery) throws BBBBusinessException, BBBSystemException {
		if(isLoggingDebug()){
			logDebug("buildPopularKeywordQuery : Started");
		}
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_CACHE_SCHEDULER+ method4);
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
        SortCriteria sortCriteria = new SortCriteria();
        sortCriteria.setSortAscending(false);
        sortCriteria.setSortFieldName(null);
        HashMap<String, String> pCatalogRef = new HashMap<String, String>();        
        pCatalogRef.put(BBBCoreConstants.CATALOG_ID, getEndecaSearch().getCatalogId(BBBCoreConstants.RECORD_TYPE, BBBCoreConstants.PRODUCT));
        pCatalogRef.put(BBBCoreConstants.CATALOG_REF_ID, null);
        searchQuery.setSortCriteria(sortCriteria);
        searchQuery.setCatalogRef(pCatalogRef);
        searchQuery.setPageNum(getPageNum());
        searchQuery.setPageSize((getBbbConfigTools().getConfigKeyValue(BBBCatalogConstants.CONTENT_CATALOG_KEYS, BBBCatalogConstants.PER_PAGE_DEFAULT, getPageSize())));
        searchQuery.setHostname(request.getServerName());
        BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_CACHE_SCHEDULER+ method4);
        if (this.isLoggingDebug()) {
            this.logDebug("buildPopularKeywordQuery : End");
        }
	}
	
	/**
	 * Build query for brands
	 * @param searchQuery
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	private void buildBrandQuery(SearchQuery searchQuery) throws BBBBusinessException, BBBSystemException {
		
		if(isLoggingDebug()){
			logDebug("buildBrandQuery : Started");
		}
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_CACHE_SCHEDULER+ method5);
		
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
        //for brand
		SortCriteria sortCriteria = new SortCriteria();
		sortCriteria.setSortAscending(true);
		sortCriteria.setSortFieldName(BBBCoreConstants.DEFAULT);
		HashMap<String, String> pCatalogRef = new HashMap<String, String>();  
		pCatalogRef.put(BBBCoreConstants.CATALOG_ID, getEndecaSearch().getCatalogId(BBBCoreConstants.RECORD_TYPE, BBBCoreConstants.PRODUCT));
		pCatalogRef.put(BBBCoreConstants.CATALOG_REF_ID, null);
		pCatalogRef.put(BBBCoreConstants.FRM_BRAND_PAGE, "true");
		searchQuery.setFromBrandPage(true);
		searchQuery.setHostname(request.getServerName());
		searchQuery.setHeaderSearch(false);
		searchQuery.setPageNum(getPageNum());
		searchQuery.setPageSize((getBbbConfigTools().getConfigKeyValue(BBBCatalogConstants.CONTENT_CATALOG_KEYS, BBBCatalogConstants.PER_PAGE_DEFAULT, getPageSize())));
		searchQuery.setCatalogRef(pCatalogRef);
		searchQuery.setSortCriteria(sortCriteria);
		BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_CACHE_SCHEDULER+ method5);
		 if (this.isLoggingDebug()) {
	            this.logDebug("buildBrandQuery : End");
	        }
		

	}
	/**
	 * Build query for categories
	 * @param searchQuery
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	private void buildCategoryQuery(SearchQuery searchQuery, String category_id) {
		// TODO Auto-generated method stub
		//category L2 L3 
		if(isLoggingDebug()){
			logDebug("buildCategoryQuery : Started");
		}
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_CACHE_SCHEDULER+ method6);
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		SortCriteria sortCriteria = new SortCriteria();
		sortCriteria.setSortAscending(true);
		sortCriteria.setSortFieldName(BBBCoreConstants.DEFAULT);
		HashMap<String, String> pCatalogRef = new HashMap<String, String>();  
		pCatalogRef.put(BBBCoreConstants.CATALOG_ID, category_id.replace('-', '+'));
		pCatalogRef.put(BBBCoreConstants.CATALOG_REF_ID, category_id);
		searchQuery.setHostname(request.getServerName());
		searchQuery.setHeaderSearch(false);
		searchQuery.setPageNum(getPageNum());
		searchQuery.setPageSize((getBbbConfigTools().getConfigKeyValue(BBBCatalogConstants.CONTENT_CATALOG_KEYS, BBBCatalogConstants.PER_PAGE_DEFAULT, getPageSize())));
		searchQuery.setCatalogRef(pCatalogRef);
		searchQuery.setSortCriteria(sortCriteria);
		BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_CACHE_SCHEDULER+ method6);
		 if (this.isLoggingDebug()) {
	            this.logDebug("buildCategoryQuery : End");
	        }
	}

	/**
	 * 
	 * @return
	 * @throws BBBSystemException
	 */
	private Connection openConnection() throws BBBSystemException {

		Connection connection = null;
		try {
			if (isLoggingDebug()) {
				logDebug("Open Connection....");
			}

			DataSource dataSource = null;
			InitialContext initialContext = null;
			try {
				initialContext = new InitialContext();
				
				if(isLoggingDebug()){
					NamingEnumeration<NameClassPair> list = initialContext.list("");
					while (list.hasMore()) {
						if (isLoggingDebug()) {
						  logDebug(list.next().getName());
						}
					}
				}
				
				dataSource = (DataSource) initialContext.lookup(getDatasourcePim());
			} catch (NamingException e) {
				if (isLoggingError()) {
					logError("Error in getting PIM dataSource ",e);
				}
			}
			if (dataSource != null) {
				connection = dataSource.getConnection();
				
				if (isLoggingDebug()) {
					logDebug("Connection is created");
				}
			}
		} catch (SQLException sqlex) {
			if (isLoggingError()) {

				logError("SQL Exception" ,sqlex);
			}
			throw new BBBSystemException(
					BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,
					sqlex.getMessage(), sqlex);
		}

		return connection;
	}
	
	/**
	 * 
	 * @param pConnection
	 */
	private void closeConnection(Connection pConnection) {
		boolean isClose = false;
		if (pConnection != null) {

			try {
				if (isConnectionOpen(pConnection)) {
					pConnection.close();
					isClose = true;
					if (isLoggingDebug()) {
						logDebug("Connection Closed Sucess....");
					}
				} else {

					if (isLoggingDebug()) {
						logDebug("Connection is already closed or not opened....");
					}
				}
			} catch (SQLException sqlex) {
				if (isLoggingError()) {
					logError(sqlex);
				}
			} finally {

				try {
					if (!isClose && isConnectionOpen(pConnection)) {
						pConnection.close();
					}
				} catch (SQLException e) {
					if (isLoggingError()) {
						logError(e);
					}
				}
			}
		}

	}

	private boolean isConnectionOpen(final Connection pConnection)
			throws SQLException {

		return (pConnection != null && !pConnection.isClosed());

	}

	/**
	 * THis nested class is used for multi threading to get categories from DB 
	 * 
	 * @author asi162
	 *
	 */
	public class CategoryRunnableThread implements Runnable{
		
		private CountDownLatch latch;
		private String category_id;
		public String getCategory_id() {
			return category_id;
		}



		public void setCategory_id(String category_id) {
			this.category_id = category_id;
		}
		private String siteId;
		
				
		public CategoryRunnableThread(String category_id, CountDownLatch latchCount,String siteId) {
			this.category_id = category_id;
			this.latch = latchCount;
			this.siteId = siteId;
		}
		


		public String getSiteId() {
			return siteId;
		}

		public void setSiteId(String siteId) {
			this.siteId = siteId;
		}


		public CountDownLatch getLatch() {
			return latch;
		}
		public void setLatch(CountDownLatch latch) {
			this.latch = latch;
		}
		@Override
		public void run() {
			long startTime = System.currentTimeMillis();
			if (isLoggingDebug()) {
			logDebug(Thread.currentThread().getName()
					+ "Free Memory before processing CategoryRunnableThread:"
					+ getMemoryUsed() + " Start time :" + startTime);
			}
			try {
				Thread.sleep(threadSleepTimeinMl);
				if (isLoggingDebug()) {
					logDebug("Next Thread going to sleep for "+threadSleepTimeinMl + " miliseconds");
				}
			} catch (InterruptedException e1) {
				logError("Error caught while thread sleep: "+e1);
			}
			DynamoHttpServletRequest request = (DynamoHttpServletRequest)(((HeadPipelineServlet) Nucleus.getGlobalNucleus().resolveName(dynamoHandler)).getRequest(null));
			DynamoHttpServletResponse response = request.getResponse();
			ServletUtil.setCurrentRequest(request);
			ServletUtil.setCurrentResponse(response);
			
			try{
				
				SearchQuery searchQuery = new SearchQuery();
				
				buildCategoryQuery(searchQuery,category_id); 	// build the searchQuery to generate ENDECA query from query generator.	
									
				searchQuery.setSiteId(getSiteId());
			
				getEndecaSearch().performSearch(searchQuery, BBBCoreConstants.RETURN_TRUE);			// perform ENDECA Search for keywords
				
		
			}catch (BBBBusinessException | BBBSystemException e) {
				logError("Error while performing endeca search",e);								
			}finally{
				latch.countDown();
				ServletUtil.setCurrentRequest(null);
				ServletUtil.setCurrentResponse(null);
				if (request != null) {
					if(null != request.getSession()) {
						request.getSession().invalidate();	
					}
					RequestScopeManager m = request.getRequestScopeManager();
					if (m == null)
						return;
					m.removeContext(request);
				}
				long endTime = System.currentTimeMillis();
				if (isLoggingDebug()) {
					logDebug(Thread.currentThread().getName() + "Free Memory after processing CategoryRunnableThread:"
						+ getMemoryUsed()
						+ " Total time by this thread:"
						+ (endTime - startTime));
				}
			}			
		}



		
		
	}
	/**
	 * THis nested class is used for multi threading to get keywords from DB 
	 * 
	 * @author asi162
	 *
	 */
	public class KeywordRunnableThread implements Runnable{
		
		private CountDownLatch latch;
		private String keyword;
		private String siteId;
		
				
		public KeywordRunnableThread(String keyword, CountDownLatch latchCount,String siteId) {
			this.keyword = keyword;
			this.latch = latchCount;
			this.siteId = siteId;
		}
		


		public String getSiteId() {
			return siteId;
		}

		public void setSiteId(String siteId) {
			this.siteId = siteId;
		}


		public String getKeyword() {
			return keyword;
		}
		
		public void setKeyword(String keyword) {
			this.keyword = keyword;
		}

		public CountDownLatch getLatch() {
			return latch;
		}
		public void setLatch(CountDownLatch latch) {
			this.latch = latch;
		}
		@Override
		public void run() {
			long startTime = System.currentTimeMillis();
			if (isLoggingDebug()) {
			logDebug(Thread.currentThread().getName()
					+ "Free Memory before processing KeywordRunnableThread:"
					+ getMemoryUsed() + " Start time :" + startTime);
			}
			try {
					Thread.sleep(threadSleepTimeinMl);
					if (isLoggingDebug()) {
						logDebug("Thread going to sleep for "+threadSleepTimeinMl + " miliseconds");
					}
				} catch (InterruptedException e1) {
					logError("Error caught while thread sleep: "+e1);
				}
			DynamoHttpServletRequest request = (DynamoHttpServletRequest)(((HeadPipelineServlet) Nucleus.getGlobalNucleus().resolveName(dynamoHandler)).getRequest(null));
			DynamoHttpServletResponse response = request.getResponse();
			ServletUtil.setCurrentRequest(request);
			ServletUtil.setCurrentResponse(response);
			
			try{
				
				SearchQuery searchQuery = new SearchQuery();
				
				buildPopularKeywordQuery(searchQuery); 	// build the searchQuery to generate ENDECA query from query generator.	
									
				searchQuery.setSiteId(getSiteId());
				searchQuery.setKeyWord(getKeyword());	
				getEndecaSearch().performSearch(searchQuery, BBBCoreConstants.RETURN_TRUE);			// perform ENDECA Search for keywords
				
		
			}catch (BBBBusinessException | BBBSystemException e) {
				logError("Error while performing endeca search",e);								
			}finally{
				latch.countDown();
				ServletUtil.setCurrentRequest(null);
				ServletUtil.setCurrentResponse(null);
				if (request != null) {
					if(null != request.getSession()) {
						request.getSession().invalidate();	
					}
					RequestScopeManager m = request.getRequestScopeManager();
					if (m == null)
						return;
					m.removeContext(request);
				}
				long endTime = System.currentTimeMillis();
				if (isLoggingDebug()) {
				logDebug(Thread.currentThread().getName() + "Free Memory after processing KeywordRunnableThread:"
						+ getMemoryUsed()
						+ " Total time by this thread:"
						+ (endTime - startTime));
				}
			}			
		}
		
	}
	/**
	 * THis nested class is used for multi threading to get Brands from DB 
	 * 
	 * @author asi162
	 *
	 */
	public class BrandRunnableThread implements Runnable{
		
		private CountDownLatch latch;
		private String keyword;
		private String siteId;
		
				
		public BrandRunnableThread(String keyword, CountDownLatch latchCount,String siteId) {
			this.keyword = keyword;
			this.latch = latchCount;
			this.siteId = siteId;
		}
		


		public String getSiteId() {
			return siteId;
		}

		public void setSiteId(String siteId) {
			this.siteId = siteId;
		}


		public String getKeyword() {
			return keyword;
		}
		
		public void setKeyword(String keyword) {
			this.keyword = keyword;
		}

		public CountDownLatch getLatch() {
			return latch;
		}
		public void setLatch(CountDownLatch latch) {
			this.latch = latch;
		}
		@Override
		public void run() {
			long startTime = System.currentTimeMillis();
			if (isLoggingDebug()) {
				logDebug(Thread.currentThread().getName()
					+ "Free Memory before processing BrandRunnableThread:"
					+ getMemoryUsed() + " Start time :" + startTime);
			}
			try {
				Thread.sleep(threadSleepTimeinMl);
				if (isLoggingDebug()) {
					logDebug("Thread going to sleep for "+threadSleepTimeinMl + " miliseconds");
				}
			} catch (InterruptedException e1) {
				logError("Error caught while thread sleep: "+e1);
			}
			DynamoHttpServletRequest request = (DynamoHttpServletRequest)(((HeadPipelineServlet) Nucleus.getGlobalNucleus().resolveName(dynamoHandler)).getRequest(null));
			DynamoHttpServletResponse response = request.getResponse();
			ServletUtil.setCurrentRequest(request);
			ServletUtil.setCurrentResponse(response);
			
			try{
				
				SearchQuery searchQuery = new SearchQuery();
				
				buildBrandQuery(searchQuery); 	// build the searchQuery to generate ENDECA query from query generator.	
									
				searchQuery.setSiteId(getSiteId());
				searchQuery.setKeyWord(getKeyword());	
				getEndecaSearch().performSearch(searchQuery, BBBCoreConstants.RETURN_TRUE);			// perform ENDECA Search for keywords
				
		
			}catch (BBBBusinessException | BBBSystemException e) {
				logError("Error while performing endeca search",e);								
			}finally{
				latch.countDown();
				ServletUtil.setCurrentRequest(null);
				ServletUtil.setCurrentResponse(null);
				if (request != null) {
					if(null != request.getSession()) {
						request.getSession().invalidate();	
					}
					RequestScopeManager m = request.getRequestScopeManager();
					if (m == null)
						return;
					m.removeContext(request);
				}
				long endTime = System.currentTimeMillis();
				if (isLoggingDebug()) {
				logDebug(Thread.currentThread().getName() + "Free Memory after processing BrandRunnableThread:"
						+ getMemoryUsed()
						+ " Total time by this thread:"
						+ (endTime - startTime));
				}
			}			
		}
		
	}
	// Method to get top(n) popular keywords and call run method to query endeca and put it in cache
	private boolean populatePopularKeywordsCache(String siteId, ExecutorService threadPool)
	{
		if (this.isLoggingDebug()) {
            this.logDebug("Inside populatePopularKeywordsCache: - Started");
        }
		int totalKeywordSize = getBbbConfigTools().getValueForConfigKey(BBBCoreConstants.SEARCH_CACHE_SCHEDULER_TYPE, BBBCoreConstants.TOTAL_SIZE, getTotalSize());
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_CACHE_SCHEDULER+ methodName1);
		Connection connection2 = null;
		PreparedStatement preparedStatement = null;	
		PreparedStatement preparedStatement2 = null;	
		ResultSet resultSet2 = null;
		ResultSet resultSet = null;
		boolean isError=false;
		try{
		connection2 = getOmnitureBoostedRepository().getConnection();
		
		//to get the count of popular keywords
		preparedStatement2 = connection2.prepareStatement(getKeywordCountQuery());
		preparedStatement2.setString(1, getReportType());
		preparedStatement2.setString(2, siteId);
		preparedStatement2.setInt(3, totalKeywordSize );
		resultSet2 = preparedStatement2.executeQuery();
		int size=0;
		while(resultSet2.next()){						
			size=resultSet2.getInt(1);
		}
			
		
		// to get the top n keyword name and product_id 		
		preparedStatement = connection2.prepareStatement(getKeywordsQuery());
		preparedStatement.setString(1,getReportType());
		preparedStatement.setString(2, siteId);
		preparedStatement.setInt(3, totalKeywordSize );
		resultSet = preparedStatement.executeQuery();
		
		
		if(isLoggingDebug()){
			logDebug("Size of Result set for " + siteId + " is " + size);
		}
		
		
		CountDownLatch latchCount = new CountDownLatch(size);

		while(resultSet.next()){
			if(isLoggingDebug()){
				logDebug("Fetch details for keyword " + resultSet.getString(Keyword));
			}
			Runnable keywordrunnable = new KeywordRunnableThread(resultSet.getString(Keyword), latchCount, getSiteIdMap().get(siteId));						
			threadPool.execute(keywordrunnable);
		}
		
		latchCount.await();
		}catch (SQLException e) {
			logError("error in creating statement",e);
			sendErrorEmail("error in creating statement"  + e.getMessage());
			isError=true;
		} catch (InterruptedException e) {
			logError("Error while waiting",e);
			sendErrorEmail("Error while fetching config keys for SearchCacheSchedulerKeys. " + e);
			isError=true;
		} finally{
				try{
					if(resultSet2!=null){
						resultSet2.close();
					}
					if(resultSet!=null){
						resultSet.close();
					}
					if(preparedStatement!=null){
						preparedStatement.close();
					}
					if(preparedStatement2!=null){
						preparedStatement2.close();
					}
				}
				catch (SQLException e){
					logError("error in closing statement", e);
					isError=true;
				}
				closeConnection(connection2);
				BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_CACHE_SCHEDULER+ methodName1);
		}
	return isError;
	}
	// Method to get brands id and call run method to query endeca and put the result in cache
	private boolean populateBrandCache(String siteId, ExecutorService threadPool){
		if (this.isLoggingDebug()) {
            this.logDebug("getBrand: - Started");
        }
		
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_CACHE_SCHEDULER+ methodName2);
		Connection connection3 = null;
		PreparedStatement preparedStatement3 = null;
		PreparedStatement preparedStatement4 = null;
		ResultSet resultSet3 = null;
		ResultSet resultSet = null;
		boolean isError=false;
		try{
		connection3 = getCatalogRepository().getConnection();
		preparedStatement3 = connection3.prepareStatement(getBrandCountQuery());
		preparedStatement3.setString(1, getSiteIdMap().get(siteId));
		preparedStatement3.setString(2, BBBCoreConstants.STRING_ONE);
	    resultSet3 = preparedStatement3.executeQuery();
		int size=0;
		while(resultSet3.next()){						
			size=resultSet3.getInt(1);
		}
		if(isLoggingDebug()){
			logDebug("Size of Result set for " + siteId + " is " + size);
		}
		
		preparedStatement4 = connection3.prepareStatement(getBrandIdQuery());
		preparedStatement4.setString(1, getSiteIdMap().get(siteId));
		preparedStatement4.setString(2, BBBCoreConstants.STRING_ONE);
	    resultSet = preparedStatement4.executeQuery();
		CountDownLatch latchCount = new CountDownLatch(size);
       while(resultSet.next()){
		
    	   if(isLoggingDebug()){
				logDebug("Fetch details for Brand " + resultSet.getString(brandId));
			}
			Runnable brandRunnable = new BrandRunnableThread(resultSet.getString(brandId), latchCount, getSiteIdMap().get(siteId));						
			threadPool.execute(brandRunnable);
			
		}

		latchCount.await();	
		}
		catch (SQLException e) {
			logError("error in creating statement",e);
			sendErrorEmail("error in creating statement"  + e.getMessage());
			isError=true;
		} catch (InterruptedException e) {
			logError("Error while waiting",e);
			sendErrorEmail("Error while fetching config keys for SearchCacheSchedulerKeys. " + e);
			isError=true;
		} finally{
				try{
					if(resultSet3!=null){
						resultSet3.close();
					}
					if(resultSet!=null){
						resultSet.close();
					}
					
					if(preparedStatement3!=null){
						preparedStatement3.close();
					}

					if(preparedStatement4!=null){
						preparedStatement4.close();
					}
				}
				catch (SQLException e){
					logError("error in closing statement", e);
					isError=true;
				}
				closeConnection(connection3);
				BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_CACHE_SCHEDULER+ methodName2);
		}
		return isError;
		
	}
	// Method to get L2 L3 categories and call run method to query endeca and put it in cache
	   private boolean populteCategoriesCache(String siteId,
				ExecutorService threadPool) {
			// TODO Auto-generated method stub
		if (this.isLoggingDebug()) {
	        this.logDebug("Inside populteCategoriesCache: - Started");
	    }
		
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_CACHE_SCHEDULER+ methodName3);
		Connection connection4 = null;
		PreparedStatement preparedStatement5 = null;
		PreparedStatement preparedStatement6 = null;
		ResultSet resultSet = null;
		ResultSet resultSet3 = null;
		boolean isError=false;
		try{

		connection4 = getCatalogRepository().getConnection();
		preparedStatement5 = connection4.prepareStatement(getCategoryCountQuery());
		preparedStatement5.setString(1, getSiteIdMap().get(siteId));
		preparedStatement5.setString(2, getFixedParentId().get(siteId));
		preparedStatement5.setString(3, BBBCoreConstants.STRING_ZERO);
	    resultSet3 = preparedStatement5.executeQuery();
	    int size=0;
		while(resultSet3.next()){						
			size=resultSet3.getInt(1);
		}
		if(isLoggingDebug()){
			logDebug("Size of Result set for " + siteId + " is " + size);
		}
		
		preparedStatement6 = connection4.prepareStatement(getCategoryQuery());
		preparedStatement6.setString(1, getSiteIdMap().get(siteId));
		preparedStatement6.setString(2, getFixedParentId().get(siteId));
		preparedStatement6.setString(3, BBBCoreConstants.STRING_ZERO);
	    resultSet = preparedStatement6.executeQuery();
	    CountDownLatch latchCount = new CountDownLatch(size);
	    while(resultSet.next()){
	    	   if(isLoggingDebug()){
					logDebug("Fetch details for category " + resultSet.getString(categoryId));
				}
	    	Runnable categoryRunnable = new CategoryRunnableThread(resultSet.getString(categoryId), latchCount, getSiteIdMap().get(siteId));						
			threadPool.execute(categoryRunnable);
	    	}
	   


		latchCount.await();	
		}
		catch (SQLException e) {
			logError("error in creating statement",e);
			sendErrorEmail("error in creating statement"  + e.getMessage());
			isError=true;
		} catch (InterruptedException e) {
			logError("Error while waiting",e);
			sendErrorEmail("Error while fetching config keys for SearchCacheSchedulerKeys. " + e);
			isError=true;
		}
		finally{
				try{
					
					if(resultSet!=null){
						resultSet.close();
					}
					if(resultSet3!=null){
						resultSet3.close();
					}
					if(preparedStatement5!=null){
						preparedStatement5.close();
					}
					if(preparedStatement6!=null){
						preparedStatement6.close();
					}
				}
				catch (SQLException e){
					logError("error in closing statement", e);
					isError=true;
				}
				closeConnection(connection4);
				BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_CACHE_SCHEDULER+ methodName3);
		}
		return isError;
		}
	public void executeDoScheduledTask() {
		if(isLoggingDebug()){
			logDebug("Entry executeDoScheduledTask");
		}
		doScheduledTask(null,new ScheduledJob("SearchCacheRefreshScheduler", getJobName(), getJobDescription(), null, null, false));

		if(isLoggingDebug()){
			logDebug("Exit executeDoScheduledTask");
		}
	}

	private static long getMemoryUsed() {
		long totalMemory = Runtime.getRuntime().totalMemory();
		long freeMemory = Runtime.getRuntime().freeMemory();
		return (totalMemory - freeMemory);
	}

	
	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public BBBConfigTools getBbbConfigTools() {
		return bbbConfigTools;
	}

	public void setBbbConfigTools(BBBConfigTools bbbConfigTools) {
		this.bbbConfigTools = bbbConfigTools;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public GSARepository getCatalogRepository() {
		return catalogRepository;
	}

	public void setCatalogRepository(GSARepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	public String getBrandIdQuery() {
		return brandIdQuery;
	}

	public void setBrandIdQuery(String brandIdQuery) {
		this.brandIdQuery = brandIdQuery;
	}

	public String getBrandCountQuery() {
		return brandCountQuery;
	}

	public void setBrandCountQuery(String brandCountQuery) {
		this.brandCountQuery = brandCountQuery;
	}

	public String getCategoryQuery() {
		return categoryQuery;
	}

	public void setCategoryQuery(String categoryQuery) {
		this.categoryQuery = categoryQuery;
	}


	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	public Map<String, String> getFixedParentId() {
		return fixedParentId;
	}

	public void setFixedParentId(Map<String, String> fixedParentId) {
		this.fixedParentId = fixedParentId;
	}

	public String getCategoryCountQuery() {
		return categoryCountQuery;
	}

	public void setCategoryCountQuery(String categoryCountQuery) {
		this.categoryCountQuery = categoryCountQuery;
	}

	public Long getThreadSleepTimeinMl() {
		return threadSleepTimeinMl;
	}

	public void setThreadSleepTimeinMl(Long threadSleepTimeinMl) {
		this.threadSleepTimeinMl = threadSleepTimeinMl;
	}

	
}
