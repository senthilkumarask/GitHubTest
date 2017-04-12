/**
 * The BBBPurgeOrderScheduler is a ATG scheduler component which purges orders which are more than 2 years old from the system
 * The scheduler is configured to run at a specific time daily and this will remove all old orders from the system
 */

package com.bbb.commerce.order.scheduler;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;


import javax.transaction.TransactionManager;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

import atg.adapter.gsa.GSARepository;
import atg.commerce.CommerceException;
import atg.commerce.order.OrderManager;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

public class BBBPurgeOrderScheduler extends SingletonSchedulableService{

	private OrderManager mOrderManager;
	private int maxItemsPerTransaction;
	private int totalItems;
	private int daysToPurge;

	private TransactionManager mTransactionManager;
	private BBBCatalogTools catalogTools;
	private boolean mSchedulerEnabled = false;
	private String rqlQuery;
    private String itemDescriptorToQuery;
	private static final long MILLISECONDS_IN_DAY = 86400000;
	
	final String VIEW_REFRESH_SQL = "{ call DBMS_SNAPSHOT.REFRESH( 'INCOMPLETE_ORDER_VIEW_MAT','C')}";
	


	/**
	 * Get the transaction manager component for the scheduler
	 * @return
	 */
	public TransactionManager getTransactionManager() {
		return mTransactionManager;
	}

	/**
	 * Sets the transaction manager component for the scheduler
	 * @param pTransactionManager
	 */
	public void setTransactionManager(TransactionManager pTransactionManager) {
		mTransactionManager = pTransactionManager;
	}

	/**
	 * method returns the OrderManager component
	 * @return
	 */
	public OrderManager getOrderManager() {
		return mOrderManager;
	}

	/**
	 * setter method sets the OrderManager from the properties file
	 * @param pOrderManager
	 */
	public void setOrderManager(OrderManager pOrderManager) {
		mOrderManager = pOrderManager;
	}

	/**
	 * @return the rqlQuery
	 */
	public String getRqlQuery() {
		return rqlQuery;
	}

	/**
	 * @param rqlQuery the rqlQuery to set
	 */
	public void setRqlQuery(String rqlQuery) {
		this.rqlQuery = rqlQuery;
	}

	/**
	 * @return the schedulerEnabled
	 */
	public final boolean isSchedulerEnabled() {
		return mSchedulerEnabled;
	}

	/**
	 * @param pSchedulerEnabled the schedulerEnabled to set
	 */
	public final void setSchedulerEnabled(boolean pSchedulerEnabled) {
		mSchedulerEnabled = pSchedulerEnabled;
	}

	@Override
	/**
	 * Overridden method which is invoked on call of the scheduler.
	 * The method calls the processPurgeOrders to start the purging process
	 */
	public void doScheduledTask(Scheduler pScheduler, ScheduledJob pScheduledjob) {
		this.doScheduledTask();
	}	

	public void doScheduledTask() {

		long startTime = System.currentTimeMillis();
		if(isLoggingInfo()){
			logInfo("Entry doScheduledTask");
		}
		
		try {
			processPurgeOrders();
		} catch (BBBBusinessException | BBBSystemException exception) {
			vlogError("Exception while purging orders ", exception);
		}
		
		long endTime= System.currentTimeMillis();
		if(isLoggingInfo()){
			logInfo("Total time took for the job::"+ (endTime - startTime)+" milliseconds");
			logInfo("Exit doScheduledTask");
		}
	}	
	/**
	 * The method loops through the orders which are eligible to be purged and calls the OrderManager component to remove these orders
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public void processPurgeOrders() throws BBBBusinessException, BBBSystemException{
		if(isLoggingDebug()){
			logDebug("processPurgeOrders::");
		}
		if(isSchedulerEnabled()){
			
			refreshOrderView();
			int maxItemsInEachTransaction = getCatalogTools().getValueForConfigKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "max_items_per_transaction", this.getMaxItemsPerTransaction());
			int totalItems = getCatalogTools().getValueForConfigKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "total_items_to_purge", this.getTotalItems());
			int batchSize = totalItems/maxItemsInEachTransaction;
			
			if(totalItems % maxItemsInEachTransaction != 0) {
				batchSize++;
			}
			
			logDebug("Total orders to process "+totalItems+" number of batch is "+batchSize);
			
	            for (int batchIndex=0;batchIndex < batchSize; batchIndex++) {
	            	
	            TransactionDemarcation td = new TransactionDemarcation();
	    		boolean rollback = true;	
	    		
				RepositoryItem[] expiredOrders = findAllPurgeOrders(batchIndex*maxItemsInEachTransaction);
	
				if(expiredOrders!= null && expiredOrders.length > 0){
					
					if(isLoggingInfo()){
						logInfo("Batch Index "+ batchIndex +"Order count : " + expiredOrders.length);
					}
					try {
						td.begin(getTransactionManager());
						long startTime = System.currentTimeMillis();
						for(int i=0;i<expiredOrders.length;i++){	
							if(isLoggingDebug()){
								logDebug("Removing order with id: "+expiredOrders[i].getRepositoryId());
							}					
							getOrderManager().removeOrder(expiredOrders[i].getRepositoryId());					
						}
						long endTime= System.currentTimeMillis();
						if(isLoggingInfo()){
							logInfo("Total time took to purge the orders::"+ (endTime - startTime)+" milliseconds");
						}
						rollback = false;
					}catch (TransactionDemarcationException e) {
						if(isLoggingError()) {
							logError("TransactionDemarcationException from processPurgeOrders", e);
						}
						throw new BBBSystemException("Order Purge scheduler failed ", e);
					} 
					catch (CommerceException e) {
						if(isLoggingError()) {
							logError("CommerceException from processPurgeOrders:", e);	
						}
						throw new BBBBusinessException("Order Purge scheduler failed ", e);
					} finally {
						if (td != null) {
							try {
								td.end(rollback);
							} catch (TransactionDemarcationException tde) {
								logError("Error ending transaction::" + tde);
							}
						}
					}
				}
	            }
		} else {
			if(isLoggingInfo()){
				logInfo("Purge Order Scheduler is DISABLED !!!");
			}
		}
		if(isLoggingDebug()){
			logDebug("Exit processPurgeOrders");
		}
	}

	/**
	 * Method to refresh order view before processing
	 */
	private void refreshOrderView() {
		
		vlogDebug("Enter :: BBBPurgeOrderScheduler.refreshOrderView()");
		long startTime = System.currentTimeMillis();
		Connection connection = null;
		CallableStatement callableStatement = null;
		
		try {
			
			connection = ((GSARepository) getOrderManager().getOrderTools().getOrderRepository()).getDataSource().getConnection();
			callableStatement = connection.prepareCall(VIEW_REFRESH_SQL);
			
			if (callableStatement != null) {
				callableStatement.execute();
			}
			
			if (!connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException exception) {
			vlogError("Exception while refreshing order view", exception);
		}
		finally{
			if(callableStatement!=null)
				try {
					callableStatement.close();
				} catch (SQLException e) {
					vlogError("SQL Exception while closing statement", e);
				}
		}
		long endTime = System.currentTimeMillis();
		vlogInfo("Total time took for view refresh ::"+ (endTime - startTime)+" milliseconds");
		vlogDebug("Exit :: BBBPurgeOrderScheduler.refreshOrderView()");
	}

	/**
	 * The method runs the purge query on the order repository view and returns a list of orders which is more than 2 years old
	 * @param numberOfOrdersProcessed
	 * @return RepositoryItem[]
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	protected RepositoryItem[] findAllPurgeOrders(int numberOfOrdersProcessed){
		int maxItemsInEachTransaction = getCatalogTools().getValueForConfigKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "max_items_per_transaction", this.getMaxItemsPerTransaction());
		int daysToPurge = getCatalogTools().getValueForConfigKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "days_to_purge", this.getDaysToPurge());
		if(isLoggingInfo()) {
			logInfo("findAllPurgeOrders:: ");
			logInfo("range provided::"+maxItemsInEachTransaction);
			logInfo("already processed orders :: "+numberOfOrdersProcessed);
			logInfo("query provided :: "+this.getRqlQuery());
		}
		long startTime = System.currentTimeMillis();
		RepositoryItem[] expiredOrders = null;		
		try {
				Object[] params = new Object[3];
				Timestamp timestamp = new Timestamp(System.currentTimeMillis() - (daysToPurge * MILLISECONDS_IN_DAY));
				params[0] = timestamp;
				params[1] = numberOfOrdersProcessed;
				params[2] = maxItemsInEachTransaction;
				if(isLoggingInfo()){
					logInfo("orders to purge older than::::"+timestamp);
				}
			expiredOrders = purgeOrdersFromRepository(params);
				if(expiredOrders !=null){
					if(isLoggingDebug()){
						logDebug("total orders to be purged::"+expiredOrders.length);
					}
				}else{
					if(isLoggingInfo()){
						logInfo("no item found to be purged::");
					}
				}

		} catch (RepositoryException e) {
			if(isLoggingError()) {
				logError("Repository Exception in findAllPurgeOrders:",e);	
			}
		}	
		long endTime= System.currentTimeMillis();
		if(isLoggingInfo()){
			logInfo("Total time took to find purged orders::"+ (endTime - startTime) +" milliseconds");
			logInfo("Exit findAllPurgeOrders::");
		}
		return expiredOrders;
	}
	public RepositoryItem[] purgeOrdersFromRepository(Object[] params) throws RepositoryException{
		Repository orderRepository = getOrderManager().getOrderTools().getOrderRepository();		
		RepositoryItem[] expiredOrders = null;	
		RepositoryItemDescriptor orderDescriptor = orderRepository.getItemDescriptor(getItemDescriptorToQuery());
		RepositoryView orderView =  orderDescriptor.getRepositoryView();
		RqlStatement statement = null;
		if (orderView != null) {
			statement = RqlStatement.parseRqlStatement(getRqlQuery());
			if(isLoggingDebug()){
				logDebug("query::"+statement.getQuery().toString());
				logDebug("rangeQuery::"+statement.getRange().toString());
			}
			expiredOrders =  statement.executeQuery(orderView, params);
		}else{
			if(isLoggingDebug()){
				logDebug("order view is null::");
			}

		} 
		return expiredOrders;
	}

	/**
	 * @return itemDescriptorToQuery
	 */
	public String getItemDescriptorToQuery() {
		return itemDescriptorToQuery;
	}

	/**
	 * Sets item descriptor to query
	 * @param itemDescriptorToQuery
	 */
	public void setItemDescriptorToQuery(String itemDescriptorToQuery) {
		this.itemDescriptorToQuery = itemDescriptorToQuery;
	}

	
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	public int getMaxItemsPerTransaction() {
		return maxItemsPerTransaction;
	}

	public void setMaxItemsPerTransaction(int maxItemsPerTransaction) {
		this.maxItemsPerTransaction = maxItemsPerTransaction;
	}

	public int getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(int totalItems) {
		this.totalItems = totalItems;
	}
	
	public int getDaysToPurge() {
		return daysToPurge;
	}

	public void setDaysToPurge(int daysToPurge) {
		this.daysToPurge = daysToPurge;
	}
}