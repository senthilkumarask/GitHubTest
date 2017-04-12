package com.bbb.salesData.scheduler;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.TransactionManager;

import org.apache.commons.lang.time.DateUtils;

import atg.adapter.gsa.LoadingStrategyContext;
import atg.core.util.StringUtils;
import atg.repository.ItemDescriptorImpl;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.NamedQueryView;
import atg.repository.ParameterSupportView;
import atg.repository.Query;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.service.jdbc.SwitchingDataSource;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBException;
import com.bbb.exception.BBBSystemException;
import com.bbb.order.bean.BBBSkuInfoVO;
import com.bbb.order.bean.SalesDataInfoVO;


/**
 *
 * @author rpan11
 *
 */
public class SalesDataFeedScheduler extends SingletonSchedulableService {
	/**
	 * Transaction Manager instance for scheduler
	 */
	private TransactionManager transactionManager;
	private boolean enabled;
	private boolean schedulerEnabled;
	private boolean lazyLoad = true;

	private MutableRepository orderRepository;
	private MutableRepository bbbFeedRepository;
	private BBBCatalogTools catalogTools;
	private String configKeyForDays;
	private String feedType;
	private Map defaultMap;
	private SwitchingDataSource dataSource;
//	private static final String RANGE =" RANGE ";
	private static final String NUMBER_OF_DAYS = "numberOfDays";

	/**
	 * @return the lazyLoad
	 */
	public boolean isLazyLoad() {
	    return this.lazyLoad;
	}

	/**
	 * @param lazyLoad the lazyLoad to set
	 */
	public void setLazyLoad(final boolean lazyLoad) {
	    this.lazyLoad = lazyLoad;
	}

	/**
	 * @return the defaultMap
	 */
	public Map getDefaultMap() {
		return this.defaultMap;
	}

	/**
	 * @param defaultMap the defaultMap to set
	 */
	public void setDefaultMap(final Map defaultMap) {
		this.defaultMap = defaultMap;
	}

	/**
	 * @return the feedType
	 */
	public String getFeedType() {
		return this.feedType;
	}

	/**
	 * @param feedType the feedType to set
	 */
	public void setFeedType(final String feedType) {
		this.feedType = feedType;
	}
	
	/**
	 * @return the switchable datasource
	 */
	public SwitchingDataSource getDataSource() {
		return this.dataSource;
	}
	/**
	 * @param switchable datasource
	 */
	public void setDataSource(SwitchingDataSource pDataSource) {
		this.dataSource = pDataSource;
	}


	/**
	 * @return the configKeyForDays
	 */
	public String getConfigKeyForDays() {

	    String numberOfDays;
	    try {
		numberOfDays = this.getCatalogTools().getAllValuesForKey("FeedSchedulerKeys", NUMBER_OF_DAYS).get(0);
		if(numberOfDays != null){
		    return numberOfDays;
		}
		return this.configKeyForDays;
	    } catch (final BBBSystemException e) {
		 this.logError("Error getting Config key type FeedSchedulerKeys and key numberOfDays" + e);
	    } catch (final BBBBusinessException e) {
		this.logError("Error getting Config key type FeedSchedulerKeys and key numberOfDays" + e);
	    }

	    return this.configKeyForDays;
	}

	/**
	 * @param configKeyForDays the configKeyForDays to set
	 */
	public void setConfigKeyForDays(final String configKeyForDays) {
		this.configKeyForDays = configKeyForDays;
	}
	/**
	 * @param schedulerEnabled the schedulerEnabled to set
	 */
	public void setSchedulerEnabled(final boolean schedulerEnabled) {
	    this.schedulerEnabled = schedulerEnabled;
	}


	/**
	 * @return the orderRepository
	 */
	public MutableRepository getOrderRepository() {
		return this.orderRepository;
	}

	/**
	 * @param orderRepository the orderRepository to set
	 */
	public void setOrderRepository(final MutableRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	/**
	 * @return the bbbFeedRepository
	 */
	public MutableRepository getBbbFeedRepository() {
		return this.bbbFeedRepository;
	}

	/**
	 * @param bbbFeedRepository the bbbFeedRepository to set
	 */
	public void setBbbFeedRepository(final MutableRepository bbbFeedRepository) {
		this.bbbFeedRepository = bbbFeedRepository;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * @return the transactionManager
	 */
	public TransactionManager getTransactionManager() {
		return this.transactionManager;
	}

	/**
	 * @param transactionManager
	 *            the transactionManager to set
	 */
	public void setTransactionManager(
			final TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	/**
	 * @return the isEnabled
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * @param isEnabled
	 *            the isEnabled to set
	 */
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Returns the whether the scheduler is enable or not
	 *
	 * @return the isShedulerEnabled
	 */
	public boolean isSchedulerEnabled() {
		return this.schedulerEnabled;
	}

	/**
	 * This variable signifies to enable or disable the scheduler in specific
	 * environment this value is set from the property file
	 *
	 * @param isShedulerEnabled
	 *            the isShedulerEnabled to set
	 */

	  @Override
	  public void doScheduledTask(final Scheduler scheduler, final ScheduledJob job) {

	      this.doScheduledTask();
	  }

	  public void doScheduledTask() {

	      if (this.isLoggingDebug()) {
		  this.logDebug("Started Scheduler Job [" + this.getJobId() + ": " + this.getJobName() + "]" + "at date = " + new Date());
		  this.logDebug("Job Description :" + this.getJobDescription() + " Scheduled at " + this.getSchedule());
	      }

	      if (this.isSchedulerEnabled()) {

		  try {

    	    	  	SalesDataInfoVO salesInfoVO = new SalesDataInfoVO();
    	    	  	final Object[] params = {"salesDataFeed"};
    	    	  	final MutableRepositoryItem[] feedKeys = (MutableRepositoryItem[]) this.executeFeedNamedRQLQuery("getFeedValueByFeedType",params,"feedSchedularRepository");
    	    	  	MutableRepositoryItem updatedRow = null;

    	    	  	if (null != feedKeys){
    	    	  	    updatedRow = this.getBbbFeedRepository().createItem("feedSchedularRepository");
    	    	  	    if( feedKeys[0].getPropertyValue("feedSchedulerValue") != null){
    	    	  		final Map<String, String> feedValueForFeedKeys = ((Map<String, String>)feedKeys[0].getPropertyValue("feedSchedulerValue"));

    	    	  		if(feedValueForFeedKeys.containsKey("startDate") && (feedValueForFeedKeys.get("startDate")!= null)){
    				    final String startDateStr = feedValueForFeedKeys.get("startDate");
    				    final SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    				    final Date startDate = df.parse(startDateStr);
    				    salesInfoVO.setStartDate(startDate);

    				}

    	    	  		if(feedValueForFeedKeys.containsKey("endDate") && (feedValueForFeedKeys.get("endDate")!= null)){
    	    	  		    final String endDateStr = feedValueForFeedKeys.get("endDate");
    	    	  		    final DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    	    	  		    final Date endDate = df.parse(endDateStr);
    	    	  		    salesInfoVO.setEndDate(endDate);
    	    	  		}

    	    	  		if(feedValueForFeedKeys.containsKey("batchSize") && (feedValueForFeedKeys.get("batchSize")!= null)){
    	    	  		    final String batchSizeStr =  feedValueForFeedKeys.get("batchSize");
    	    	  		    final int batchSize = Integer.parseInt(batchSizeStr);
    	    	  		    salesInfoVO.setBatchSize(batchSize);
    	    	  		    salesInfoVO.setBatchSize(10);

    	    	  		}else{
    	    	  		    final String batchSizeStr = (String) this.getDefaultMap().get("batchSize");
    	    	  		    final int batchSize = Integer.parseInt(batchSizeStr);
    	    	  		    salesInfoVO.setBatchSize(batchSize);

    	    	  		}

    	    	  		if(feedValueForFeedKeys.containsKey("orderStates") && (feedValueForFeedKeys.get("orderStates")!= null)){
    	    	  		    salesInfoVO.setOrderStates(feedValueForFeedKeys.get("orderStates"));
    	    	  		}else{
    	    	  		    salesInfoVO.setOrderStates((String) this.getDefaultMap().get("orderStates"));
    	    	  		}

    				if(salesInfoVO.getBatchSize() == 0){
    				    salesInfoVO.setBatchSize(Integer.parseInt((String)this.getDefaultMap().get("batchSize")));
    				}

    				salesInfoVO = this.getDate(salesInfoVO);
    				salesInfoVO = this.processSalesData(salesInfoVO);

    				final Map<String,String> newMap = new HashMap<String, String>();
    				final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    				newMap.put("startDate", formatter.format(salesInfoVO.getLastEndDate()));
    				newMap.put("endDate", formatter.format(salesInfoVO.getEndDate()));
    				newMap.put("batchSize", Integer.toString(salesInfoVO.getBatchSize()));
    				updatedRow = this.copyFeedRepositoryItem(feedKeys[0], updatedRow);

    				if(updatedRow != null){
    				    this.getBbbFeedRepository().removeItem(feedKeys[0].getRepositoryId(), "feedSchedularRepository");
    				    updatedRow.setPropertyValue("feedSchedulerValue", newMap);
    				    this.getBbbFeedRepository().addItem(updatedRow);
    				}

    	    	  	    }else{
    	    	  		if(this.isLoggingDebug()) {
    	    	  		    this.logDebug("Feed Repository Sceduler Method [doScheduledTask], the Feed Repository for Sales Data can not be null :" + Arrays.toString(feedKeys));
    	    	  		}
    	    	  	    }
    	    	  	}
    	    	  	/* calling calcualteSalesData stored proc */
    	    	  	executeCalculateSalesDataStoredProc();
    	    	  	
    		} catch (final BBBException e) {
    			
    			this.logError("Sales Feed Scheduler Method [doScheduledTask] : BBBException  "+e);
    			
    		}catch (final RepositoryException e) {    			
    				this.logError("Sales Feed Scheduler Method [doScheduledTask] : RepositoryException  "+e);
    			
    		} catch (final ParseException e) {
    			
    			this.logError("Sales Feed Scheduler Method [doScheduledTask] : ParseException  "+e);
    			
    		}
	      if (this.isLoggingDebug()) {
		        this.logDebug("Sales Data Feed Scheduler Task is Successfully Completed !!");
		      }
	      } else {
	      if (this.isLoggingDebug()) {
	        this.logDebug("Scheduler Task is Disabled");
	      }
	    }
	  }



	/**This method will get the data from Order Repository, iterate over commerceItems and set the product and SKU related details to a Map .
	 * Send that Map to Catalog API Method.
	 * Get the details from Catalog API and get a Map
	 * Set these details to  Newly created repository SalesDataRepository
	 */

	public SalesDataInfoVO processSalesData(final SalesDataInfoVO salesInfoVO) throws BBBBusinessException, BBBSystemException {
	    if(this.isLoggingDebug()){
		this.logDebug("[START] getSalesDataFromOrderReposotory : SalesDataFeedScheduler");
	    }

	    if(salesInfoVO != null){

		 Object[] params = null;
		 String stateParam = salesInfoVO.getOrderStates();
		 if(stateParam == null){
		     stateParam = (String) this.getDefaultMap().get("orderStates");
		 }

		 final String[] parts = stateParam.toString().split(":");
		 /*if(parts != null && parts.length == 2){
		     params = new Object[4];
		     params[0] = parts[0];
		     params[1] = parts[1];
		     String rqlQuery = "(state=?0 or state=?1) and (submittedDate>=?2 and submittedDate<=?3)";
		     salesInfoVO.setRqlQuery(rqlQuery);

		 }else if(parts != null && parts.length == 1){
		     params = new Object[4];
		     params[0] = parts[0];
		     params[1] = null;
		     String rqlQuery = "(state=?0) and (submittedDate>=?2 and submittedDate<=?3)";
		     salesInfoVO.setRqlQuery(rqlQuery);
		 } 	*/

		 if(parts != null){
			 final int noOfStates = parts.length;
			 params = new Object[noOfStates +2];
			 String rqlQuery = "(";
			 for (int i=0; i< parts.length ;i++){
				 params[i] = parts[i];
				 rqlQuery += "state=?" +i;
				 if(i != (parts.length -1)){
					 rqlQuery += " or ";
				 }
			 }
			 this.logInfo("Rql query for order states to fetch sales data: " + rqlQuery);
			 rqlQuery += ") and (submittedDate>=?" + (noOfStates) +" and submittedDate<=?" + (noOfStates+1) +")";
			 this.logInfo("Final Rql query for order states to fetch sales data for a given period : " + rqlQuery);
			 salesInfoVO.setRqlQuery(rqlQuery);
		 }
		 if(params != null){

		     try {

			Map<String,BBBSkuInfoVO> productSalesData = new HashMap<String,BBBSkuInfoVO>();
			if(salesInfoVO.getIsCleanUpData()){

			    if(this.isLazyLoad()) {
				LoadingStrategyContext.pushLoadStrategy("lazy");
			    }
			    final RqlStatement statementDate =  RqlStatement.parseRqlStatement("(productId!=?0 )");
			    final RepositoryView viewSales = this.getOrderRepository().getView("salesData");
			    final Object[] paramDate = new Object[1];
			    paramDate[0] = "0";

			    final RepositoryItem[] salesItems = statementDate.executeQuery(viewSales, paramDate);
			    if((salesItems != null) && (salesItems.length > 0)){
				 if(this.isLoggingDebug()){
				     this.logDebug("Sales Data Clean Up Started");
				 }
				for(final RepositoryItem sales :  salesItems){
				    if ((sales != null) && (sales.getRepositoryId() !=null)){
					this.getOrderRepository().removeItem(sales.getRepositoryId() , "salesData");
				    }else{
					this.getOrderRepository().removeItem("null" , "salesData");
				    }
				}
				if(this.isLoggingDebug()){
				    this.logDebug("Sales Data is flushed");
				}
			    }

			}if(salesInfoVO.getIsIncrement()){
			    productSalesData = this.fetchOrderFromRepository(salesInfoVO.getRqlQuery(), params, true, salesInfoVO, productSalesData);

			}

			if((productSalesData != null) && !productSalesData.isEmpty()){

			    for (final String productSalesDataKey : productSalesData.keySet()) {
				if(productSalesDataKey != null){
				    if(this.isLoggingDebug()) {
					this.logDebug("Sales Feed Scheduler Method [processSalesData] : product matched from Catalog :"+productSalesDataKey);
				    }

				    final String productId = null;
				    String siteId = null;

				    final Object[] mapKeyParts = productSalesDataKey.split(":");
				    final String prodId = (String) mapKeyParts[0];
				    siteId = (String) mapKeyParts[1];

				    final BBBSkuInfoVO pBBBSkuInfoVO = productSalesData.get(productSalesDataKey);
				    Double totalAmount = null;
				    Long quantity = null;
				    int numberOfOrder = 0;


				    final RqlStatement statement = RqlStatement.parseRqlStatement("productId=?0 and siteId=?1");
				    final RepositoryView view = this.getOrderRepository().getView("salesData");
				    final RepositoryItem[] queryResult = statement.executeQuery(view, mapKeyParts);

				    if((queryResult != null) && (queryResult[0] != null)){

					final MutableRepositoryItem existingSalesItem =  this.getOrderRepository().getItemForUpdate( queryResult[0].getRepositoryId(), "salesData");
					if(this.isLoggingDebug()) {
					    this.logDebug("Sales Feed Scheduler Method [processSalesData] : Product id  "+productId+" already exists in the Sales Data");
					}

					if(existingSalesItem.getPropertyValue("unitSales") != null){
					    quantity = Long.parseLong((String)existingSalesItem.getPropertyValue("unitSales"));
					}
					if(existingSalesItem.getPropertyValue("orderSales") != null){
					    numberOfOrder = Integer.parseInt((String)existingSalesItem.getPropertyValue("orderSales"));
					}
					if(existingSalesItem.getPropertyValue("totalSales") != null){
					    totalAmount = Double.parseDouble((String)existingSalesItem.getPropertyValue("totalSales"));
					}

					if((quantity != null) && ((quantity + pBBBSkuInfoVO.getQuantity()) > 0)){

					    existingSalesItem.setPropertyValue("unitSales", String.valueOf(quantity + pBBBSkuInfoVO.getQuantity()));
					    existingSalesItem.setPropertyValue("orderSales", String.valueOf(numberOfOrder + pBBBSkuInfoVO.getFrequency()));
					    existingSalesItem.setPropertyValue("totalSales", String.valueOf(totalAmount + pBBBSkuInfoVO.getAmount()));
					    this.getOrderRepository().updateItem(existingSalesItem);

					    final ItemDescriptorImpl salesDataItemDesc = (ItemDescriptorImpl) this.getOrderRepository().getItemDescriptor("salesData");
					    salesDataItemDesc.removeItemFromCache(existingSalesItem.getRepositoryId());
					    if(this.isLoggingDebug()){
						this.logDebug("Updated Product ID  "+prodId+ " with new sales data");
					    }
					}else{
					    this.getOrderRepository().removeItem(existingSalesItem.getRepositoryId());
					    final ItemDescriptorImpl salesDataItemDesc = (ItemDescriptorImpl) this.getOrderRepository().getItemDescriptor("salesData");
					    salesDataItemDesc.removeItemFromCache(existingSalesItem.getRepositoryId());
					}

				    }else if ((pBBBSkuInfoVO.getQuantity() != null) && (pBBBSkuInfoVO.getQuantity() > 0)) {

					if(this.isLoggingDebug()) {
					    this.logDebug("Sales Feed Scheduler Method [processSalesData] : Product id  "+prodId+" does not exists in the Sales Data");
					}
					final Date modificationDate = new Date();
					final MutableRepositoryItem  newSalesItem = this.getOrderRepository().createItem("salesData");
					newSalesItem.setPropertyValue("productId", prodId);
					newSalesItem.setPropertyValue("siteId", siteId);
					newSalesItem.setPropertyValue("unitSales", String.valueOf(pBBBSkuInfoVO.getQuantity()));
					newSalesItem.setPropertyValue("orderSales",  String.valueOf(pBBBSkuInfoVO.getFrequency()));
					newSalesItem.setPropertyValue("totalSales", String.valueOf(pBBBSkuInfoVO.getAmount()));
					newSalesItem.setPropertyValue("lastModifiedDate", modificationDate);
					this.orderRepository.addItem(newSalesItem);
					if(this.isLoggingDebug()){
					    this.logDebug("Added new Product in sales data with Product ID : "+prodId+ " for new sales data");
					}
				    }
				}

			    }
			}
		     } catch (final RepositoryException e) {
			    
				this.logError("Sales Feed Scheduler Method [processSalesData] : RepositoryException  " + e);
			    

		     }  finally {
				if(this.isLazyLoad()) {
					LoadingStrategyContext.popLoadStrategy();
				}
			}
		 }
	    }

	    return salesInfoVO;

	}


	/**This method will get the product related data from Catalog.
	 * and return the Map containing the product and their SKU's details.
	 * @param productSalesDataMap
	 * @return Map
	 */
	public RepositoryItem[] executeFeedNamedRQLQuery(final String pQueryName,
		final Object[] pParams, final String pViewName)
			throws RepositoryException {
	    if (this.isLoggingDebug()) {
		this.logDebug("[START] executeFeedNamedRQLQuery : SalesDataFeedScheduler");
	    }
	    RepositoryItem[] repositoryItems = null;

	    if (this.getBbbFeedRepository() == null) {
		this.logDebug("Can't execute RQL query. The Feed repository object is null");
		return null;
	    }

	    if (StringUtils.isEmpty(pQueryName) || StringUtils.isEmpty(pViewName)) {
		this.logDebug("Empty query name or view name passed. Returning back");
		return null;
	    }

	    this.logDebug("About to execute RQL query:" + pQueryName
		    + "on itemdescriptor:" + pViewName);

	    final NamedQueryView view = (NamedQueryView) this.getBbbFeedRepository().getView(
		    pViewName);

	    if (view != null) {
		final Query query = view.getNamedQuery(pQueryName);

		if (query != null) {
		    repositoryItems = ((ParameterSupportView) view).executeQuery(
			    query, pParams);
		}
	    }

	    if (this.isLoggingDebug()) {
		this.logDebug("[START] executeFeedNamedRQLQuery : SalesDataFeedScheduler");
	    }

	    return repositoryItems;
	}


        /**
         * method returns the milliseconds value of no of days
         * @return
         */
        public long convDaysToMilliSeconds(final int pDays) {
        	return pDays*BBBCoreConstants.MILLISECONDS_IN_ONE_DAY;
        }



	/**
	 * @param rqlQuery
	 * @param params
	 * @param isIncrement
	 * @param salesInfoVO
	 * @return
	 */
	private Map<String,BBBSkuInfoVO> fetchOrderFromRepository(final String rqlQuery, final Object[] params, final boolean isIncrement, final SalesDataInfoVO salesInfoVO,
										final Map<String,BBBSkuInfoVO> productSalesData) throws RepositoryException {

	    int totalCount = 0;
	    boolean continueProcessing = true;

	    while(continueProcessing){

//		 String rqlQueryRange= RANGE + totalCount + "+" + (salesInfoVO.getBatchSize());
//		 salesInfoVO.setRqlQueryWithRange("("+salesInfoVO.getRqlQuery()+")" + "ORDER BY id ASC" + rqlQueryRange);
		try{

		 salesInfoVO.setRqlQueryWithRange(salesInfoVO.getRqlQuery());
		 if(this.isLazyLoad()) {
			LoadingStrategyContext.pushLoadStrategy("lazy");
		 }
		 final RqlStatement statement = RqlStatement.parseRqlStatement(salesInfoVO.getRqlQueryWithRange());
		 final RepositoryView view = this.getOrderRepository().getView("order");

		 params[params.length -2] = new Timestamp(salesInfoVO.getStartDate().getTime());
		 params[params.length -1] = new Timestamp(salesInfoVO.getEndDate().getTime());

		 final RepositoryItem[] orderItems = statement.executeQuery(view, params);

		 if((orderItems != null) && (orderItems.length > 0)){
			this.logInfo("Total Order found matching the criteria:" + orderItems.length);

			for(final RepositoryItem order : orderItems){
			    if(this.isLoggingDebug()) {
			    	this.logDebug("Working on oder id :" + order + " for isIncrement: " + isIncrement);
			    }
			    List<RepositoryItem> cItems =null;
			    try{
				    cItems = (List<RepositoryItem>) order.getPropertyValue("commerceItems");

				    for (final RepositoryItem commerceItem : cItems) {

					String skuId = null;
					String productId = null;
					Long quantity = null;
					String siteId = null;
					Double price = null;
					String productSalesDataKey = null;
					if(commerceItem.getPropertyValue("catalogRefId") != null){
						skuId = (String) commerceItem.getPropertyValue("catalogRefId");
					    }

					if(commerceItem.getPropertyValue("productId") != null){
						productId = (String) commerceItem.getPropertyValue("productId");
					}
					if(commerceItem.getPropertyValue("quantity") != null){
						quantity = (Long) commerceItem.getPropertyValue("quantity");
					}
					if(commerceItem.getPropertyValue("siteId") != null){
						siteId = (String) commerceItem.getPropertyValue("siteId");
					}
					if(commerceItem.getPropertyValue("priceInfo") != null){
					    final RepositoryItem priceInfoItem = (RepositoryItem) commerceItem.getPropertyValue("priceInfo");
					    if(priceInfoItem.getPropertyValue("listPrice") != null){
						price = (Double)priceInfoItem.getPropertyValue("listPrice");
					    }
					}

					if ((productId != null) && (siteId != null) && (skuId != null)){
					    productSalesDataKey = productId+":"+siteId;
					    if(productSalesData.containsKey(productSalesDataKey)){
						final BBBSkuInfoVO pBBBSkuInfoVO = productSalesData.get(productSalesDataKey);
						if(isIncrement && (pBBBSkuInfoVO != null)){
						    pBBBSkuInfoVO.setQuantity(pBBBSkuInfoVO.getQuantity() + quantity);
						    pBBBSkuInfoVO.setFrequency(pBBBSkuInfoVO.getFrequency() + 1);
						    pBBBSkuInfoVO.setAmount(pBBBSkuInfoVO.getAmount() + (price*quantity));
						}
						productSalesData.put(productSalesDataKey, pBBBSkuInfoVO);
					    }else{
						final BBBSkuInfoVO pBBBSkuInfoVO = new BBBSkuInfoVO();
						if(isIncrement){
						    pBBBSkuInfoVO.setQuantity(quantity);
						    pBBBSkuInfoVO.setFrequency(1);
						    pBBBSkuInfoVO.setAmount(price*quantity);
						}
						productSalesData.put(productSalesDataKey, pBBBSkuInfoVO);
					    }
					}
				    }
			    }catch (final Exception e){
			    	this.logError("Repository Exception occured while fetching Commerce Item:",e);
			    }
			}
		    }

		 if((orderItems != null) && (orderItems.length == salesInfoVO.getBatchSize())){
		     totalCount = totalCount + orderItems.length;
		 }else{
		     continueProcessing = false;
		 }

	    } catch (final RepositoryException e){
		this.logError("Repository Exception occured while fetching Order Items Item:",e);
	    }finally {
		if(this.isLazyLoad()) {
		    LoadingStrategyContext.popLoadStrategy();
		}
	    }
	   }

	    return productSalesData;
	}

	/**This method will get configKeyForDays as parameter and subtract this date with current date .
	 * and return the date as currentDate.
	 *
	 * @return currentDate
	 */

	public SalesDataInfoVO getDate(final SalesDataInfoVO saleInfoVO){
	    if(this.isLoggingDebug()){
		this.logDebug("[START] getDate : SalesDataFeedScheduler");
	    }

	    final Date currentDate = new Date();
	    Date cutOffStartDate = null;
	    Date lastStartDate = null;
	    Date lastEndDate = null;
	    Date startDate = saleInfoVO.getStartDate();
	    Date endDate = saleInfoVO.getEndDate();
	    boolean isIncrement = false;
	    boolean isDecrement = false;

	    final int numberofDays = Integer.parseInt(this.getConfigKeyForDays());
	    cutOffStartDate = DateUtils.addDays(currentDate, -numberofDays);

	    if((startDate == null) && (endDate == null)){
		saleInfoVO.setIsCleanUpData(true);
		endDate = currentDate;
		startDate = cutOffStartDate;
		isIncrement = true;
		lastEndDate = cutOffStartDate;
		if(this.isLoggingDebug()){
		    this.logDebug("Sales Data to be Calculated : Setting Date Values to Calculate New Sales Data");
		}
	    }else if(endDate.equals(currentDate) && (startDate != null) &&  startDate.equals(cutOffStartDate)){
		if(this.isLoggingDebug()){
		    this.logDebug("Updated Sales Data : Exiting Sales Data Scheduler");
		}
	    }else if((startDate != null) && (cutOffStartDate != null)){
		if(this.isLoggingDebug()){
		    this.logDebug("Sales Data To be Cleaned : Setting Date Values to Clean Sales Data and Add New Sales Data only");
		}
		saleInfoVO.setIsCleanUpData(true);
		lastStartDate = startDate;
		lastEndDate = cutOffStartDate;
		endDate = currentDate;
		startDate = cutOffStartDate;
		isDecrement = false;
		isIncrement = true;
	    }


	    if(startDate != null){
		saleInfoVO.setStartDate(startDate);
	    }
		saleInfoVO.setEndDate(endDate);
	    if(lastStartDate != null){
		saleInfoVO.setLastStartDate(lastStartDate);
	    }
	    if(lastEndDate != null){
		saleInfoVO.setLastEndDate(lastEndDate);
	    }

	    saleInfoVO.setIsDecrement(isDecrement);
	    saleInfoVO.setIsIncrement(isIncrement);

	    if(this.isLoggingDebug()){
		this.logDebug("Sales Data VO:/n" + saleInfoVO.toString());
		this.logDebug("[End] getDate : SalesDataFeedScheduler");

	    }

	    return saleInfoVO;
	}

	/**
	 * @param item
	 * @param newItem
	 */
	public MutableRepositoryItem copyFeedRepositoryItem(final RepositoryItem item, final MutableRepositoryItem newItem){

	    if((item != null) && (newItem != null)){
		newItem.setPropertyValue("id", item.getPropertyValue("id"));
		newItem.setPropertyValue("feedTypeDesc", item.getPropertyValue("feedTypeDesc"));
		newItem.setPropertyValue("feedType", item.getPropertyValue("feedType"));
	    }

	    return newItem;
	}

	/**
	 * Creating datasource connection
	 * @return
	 * @throws Exception
	 */
	public  Connection openConnection() throws Exception {

		Connection conn = null;
		try {
			conn = getDataSource().getConnection();
		} catch (SQLException e) {
			throw e;
		}
		return conn;
	}
	
	/**
	 * Execute SalesData stored procedure
	 */
	public void executeCalculateSalesDataStoredProc(){
		
			logInfo("SalesDataFeedScheduler: in method executeCalculateSalesData Start ");
			CallableStatement cstmt = null;
			Connection connection =null;
			try {
				connection = openConnection();
				if(null!=connection){
					cstmt = connection.prepareCall("{call calculate_product_sales()}");
					cstmt.execute();
					
					if(this.isLoggingDebug()){
						this.logDebug("SalesData stored procedure completes");
					}
				}
			}catch (SQLException e){
					logError("SQL Exception while executing stored procedure CalculateSalesData in method executeCalculateSalesData"+e);
			}catch (Exception e){
					logError("Error while executing stored procedure CalculateSalesData in executeCalculateSalesData"+e);
			}finally {
				try {
					if(null!=cstmt){
						cstmt.close();
					}
					if(null!=connection){
						connection.close();
					}
					}catch (SQLException e) {
							logError("SQL Exception while executing stored procedure CalculateSalesData in executeCalculateSalesData"+e);
					} 
			}
			logInfo("SalesDataFeedScheduler: in method executeCalculateSalesData End  ");
		}
	
	
}


//class ValueComparator implements Comparator<String> {
//
//    Map<String, Double> base;
//    public ValueComparator(Map<String, Double> base) {
//        this.base = base;
//    }
//    // Note: this comparator imposes orderings that are inconsistent with equals.
//    public int compare(String a, String b) {
//        if (base.get(a) >= base.get(b)) {
//            return -1;
//        } else {
//            return 1;
//        } // returning 0 would merge keys
//    }
//
//}