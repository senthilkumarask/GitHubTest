package com.bbb.commerce.order.scheduler;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

import atg.adapter.gsa.GSARepository;
import atg.repository.Repository;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

/**
 * This class contains code to purge submitted order 
 * @author Sapient
 *
 */
public class BBBPurgeSubmittedOrderScheduler extends SingletonSchedulableService {
	
	/**
	 * Flag to enable/disable the scheduler
	 */
	private boolean mSchedulerEnabled = false;
	private int daysToPurge;
	private int numOfOrdersToPurge;
	/**
	 * Reference to order repository
	 */
	private Repository orderRepository;
	private BBBCatalogTools catalogTools;
	/**
	 * SQL to invoke archive stored procedure
	 */
	final String ARCHIVE_ORDER_SQL = "{call BBB_CORE.ARCHIVE_SUBMITORDER_PKG.ARCHIVE_SUBMITTED_ORDER(?,?)}";
	
	@Override
	public void doScheduledTask(Scheduler arg0, ScheduledJob arg1) {
		try {
			processPurgeOrders();
		} catch (BBBSystemException | BBBBusinessException e) {
			logError("Exception while fetching value for config type Exception "+ e.getMessage());
		}
	}

	public void processPurgeOrders() throws BBBSystemException, BBBBusinessException {

		final long startTime = System.currentTimeMillis();
		vlogInfo("Entry processPurgeOrders");
		int numOfOrdersToPurge=getCatalogTools().getValueForConfigKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "num_of_orders_to_purge", this.getNumOfOrdersToPurge());
		int daysToPurge=getCatalogTools().getValueForConfigKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "days_to_purge_submitted_order", this.getDaysToPurge());
		if(isSchedulerEnabled()) {
			
			vlogDebug("Days to Purge {0} , Number of orders to purge {1}", daysToPurge,numOfOrdersToPurge);
			Connection connection = null;
			CallableStatement callableStatement = null;
			
		    try {
		    	
		    	connection = ((GSARepository) getOrderRepository()).getDataSource().getConnection();
		    	callableStatement = connection.prepareCall(ARCHIVE_ORDER_SQL);
				callableStatement.setInt(1, daysToPurge);
				callableStatement.setInt(2, numOfOrdersToPurge);
				
				callableStatement.executeUpdate();
				
			} catch (SQLException ex) {
				vlogError("SQL Exception ocurred for while invoking stored procedure  {0}", ex);
			} finally {
				
				try {
					
					if (callableStatement != null) {
						callableStatement.close();
					}
					
					if (connection != null && !connection.isClosed()) {
							connection.close();
					}
					
				} catch (SQLException ex) {
					vlogError("SQL Exception ocurred for while closing connection  ", ex);
				} 
					
			}
			
		} else {
			vlogInfo("BBBPurgeSubmittedOrderScheduler is disabled");
		}
		
		
		final long endTime= System.currentTimeMillis();
		vlogInfo("Total time took for the job::"+ (endTime - startTime)+" milliseconds");
		vlogInfo("Exit processPurgeOrders");
		
	}	
	

	/**
	 * Flag to enable or disable the scheduler
	 * @return mSchedulerEnabled
	 */
	public boolean isSchedulerEnabled() {
		return mSchedulerEnabled;
	}

	/**
	 * @param pSchedulerEnabled
	 */
	public void setSchedulerEnabled(boolean pSchedulerEnabled) {
		this.mSchedulerEnabled = pSchedulerEnabled;
	}

	/**
	 * Returns reference to order repository
	 * @return orderRepository
	 */
	public Repository getOrderRepository() {
		return orderRepository;
	}

	/**
	 * @param orderRepository
	 */
	public void setOrderRepository(Repository orderRepository) {
		this.orderRepository = orderRepository;
	}

	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	public int getDaysToPurge() {
		return daysToPurge;
	}

	public void setDaysToPurge(int daysToPurge) {
		this.daysToPurge = daysToPurge;
	}

	public int getNumOfOrdersToPurge() {
		return numOfOrdersToPurge;
	}

	public void setNumOfOrdersToPurge(int numOfOrdersToPurge) {
		this.numOfOrdersToPurge = numOfOrdersToPurge;
	}

}
