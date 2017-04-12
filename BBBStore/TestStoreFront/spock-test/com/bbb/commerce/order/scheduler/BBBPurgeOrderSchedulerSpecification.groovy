package com.bbb.commerce.order.scheduler

import java.sql.CallableStatement
import java.sql.Connection
import java.sql.SQLException;

import javax.sql.DataSource
import javax.transaction.TransactionManager

import spock.lang.specification.BBBExtendedSpec
import atg.adapter.gsa.GSARepository;
import atg.commerce.CommerceException
import atg.commerce.order.OrderManager
import atg.commerce.order.OrderTools
import atg.repository.QueryBuilder
import atg.repository.Repository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryItemDescriptor
import atg.repository.RepositoryView
import atg.service.scheduler.ScheduledJob
import atg.service.scheduler.Scheduler

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCoreConstants;
import com.bbb.repository.RepositoryItemMock

class BBBPurgeOrderSchedulerSpecification extends BBBExtendedSpec {
	
	def OrderManager orderManagerMock = Mock()
	def TransactionManager transactionManagerMock = Mock()
	def BBBPurgeOrderScheduler purgeOrderScheduler
	def RepositoryView orderRepositoryViewMock = Mock()
	def Repository repositoryMock = Mock()
	def GSARepository gsaRepositoryMock = Mock()
	def RepositoryItemDescriptor itemDecsMock = Mock()
	def OrderTools orderToolsMock = Mock()
	def QueryBuilder queyrbuilderMock = Mock()
	def DataSource dataSrc = Mock()
	def Connection conn = Mock()
	def CallableStatement cStata = Mock()
	BBBCatalogTools catalogToolsMock = Mock()
	def setup(){
		purgeOrderScheduler = new BBBPurgeOrderScheduler(orderManager:orderManagerMock,transactionManager:transactionManagerMock,catalogTools:catalogToolsMock)
		purgeOrderScheduler.setRqlQuery("submittedDate < ?0 RANGE +?1")
		purgeOrderScheduler.setMaxItemsPerTransaction(500)
		purgeOrderScheduler.setTotalItems(10000)
		purgeOrderScheduler.setItemDescriptorToQuery("inCompleteOrders")
	}
	def "Disabling the removing of orders when scheduler runs"(){
		expect:"Purge Order Scheduler is DISABLED !!!"
		purgeOrderScheduler.doScheduledTask()
	}  
	def "Scheculer runs to remove orders more than two years"(){
		given :
		purgeOrderScheduler= Spy()
		purgeOrderScheduler.setSchedulerEnabled(true)
		purgeOrderScheduler.setLoggingInfo(true)
		purgeOrderScheduler.setLoggingDebug(true)
		purgeOrderScheduler.setCatalogTools(catalogToolsMock)
		List items = new ArrayList<RepositoryItemMock>()
		catalogToolsMock.getValueForConfigKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "max_items_per_transaction", 5)>>5
		catalogToolsMock.getValueForConfigKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "total_items_to_purge", 10)>>10
		RepositoryItemMock itemMock = new RepositoryItemMock()
		itemMock.setRepositoryId("O1234")
		items.add(itemMock)
		RepositoryItem[] repItems = items.toArray()
		purgeOrderScheduler.findAllPurgeOrders(_) >> repItems
		purgeOrderScheduler.getTransactionManager() >> transactionManagerMock
		purgeOrderScheduler.getOrderManager() >> orderManagerMock
		orderManagerMock.getOrderTools() >> orderToolsMock
		orderToolsMock.getOrderRepository() >> gsaRepositoryMock >> repositoryMock
		gsaRepositoryMock.getDataSource()  >> dataSrc
		dataSrc.getConnection()  >> conn
		conn.isClosed() >> false
		1*conn.prepareCall(_) >> cStata
		purgeOrderScheduler.setRqlQuery("submittedDate < ?0 RANGE +?1")
		purgeOrderScheduler.setMaxItemsPerTransaction(5)
		purgeOrderScheduler.setTotalItems(10)
		//repositoryMock.getItemDescriptorToQuery()
		when:
		purgeOrderScheduler.doScheduledTask()
		then:
		items.size() == 1
		1*cStata.close()
		1*cStata.execute()
		1*conn.close()
		
	}
	def "No more orders to remove more than two years"(){
		given :
		purgeOrderScheduler= Spy()
		purgeOrderScheduler.setSchedulerEnabled(true)
		purgeOrderScheduler.setLoggingInfo(true)
		purgeOrderScheduler.setLoggingDebug(true)
		purgeOrderScheduler.setCatalogTools(catalogToolsMock)
		List items = new ArrayList<RepositoryItemMock>()
		catalogToolsMock.getValueForConfigKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "max_items_per_transaction", 5)>>5
		catalogToolsMock.getValueForConfigKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "total_items_to_purge", 2)>>3
		purgeOrderScheduler.findAllPurgeOrders(_) >> null
		purgeOrderScheduler.getTransactionManager() >> transactionManagerMock
		purgeOrderScheduler.getOrderManager() >> orderManagerMock
		orderManagerMock.getOrderTools() >> orderToolsMock
		orderToolsMock.getOrderRepository() >> gsaRepositoryMock >> repositoryMock
		gsaRepositoryMock.getDataSource()  >> dataSrc
		dataSrc.getConnection()  >> conn
		conn.isClosed() >> true
		1*conn.prepareCall(_) >> cStata
		purgeOrderScheduler.setRqlQuery("submittedDate < ?0 RANGE +?1")
		purgeOrderScheduler.setMaxItemsPerTransaction(5)
		purgeOrderScheduler.setTotalItems(2)
		when:
		purgeOrderScheduler.doScheduledTask()
		then:
		items.size() == 0
		1*cStata.close()
		1*cStata.execute()
		0*conn.close()
	}
	def "Scheculer runs  non of the orders are more than two years"(){
		given :
		purgeOrderScheduler= Spy()
		purgeOrderScheduler.setSchedulerEnabled(true)
		purgeOrderScheduler.setLoggingInfo(false)
		purgeOrderScheduler.setLoggingDebug(true)
		purgeOrderScheduler.setCatalogTools(catalogToolsMock)
		List items = new ArrayList<RepositoryItemMock>()
		catalogToolsMock.getValueForConfigKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "max_items_per_transaction", 5)>>5
		catalogToolsMock.getValueForConfigKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "total_items_to_purge", 10)>>10
		RepositoryItem[] repItems = items.toArray()
		purgeOrderScheduler.findAllPurgeOrders(_) >> repItems
		purgeOrderScheduler.getTransactionManager() >> transactionManagerMock
		purgeOrderScheduler.getOrderManager() >> orderManagerMock
		1*purgeOrderScheduler.processPurgeOrders()
		orderManagerMock.getOrderTools() >> orderToolsMock
		orderToolsMock.getOrderRepository() >> gsaRepositoryMock >> repositoryMock
		gsaRepositoryMock.getDataSource()  >> dataSrc
		dataSrc.getConnection()  >> conn
		conn.isClosed() >> false
		1*conn.prepareCall(_) >> null
		purgeOrderScheduler.setRqlQuery("submittedDate < ?0 RANGE +?1")
		purgeOrderScheduler.setMaxItemsPerTransaction(5)
		purgeOrderScheduler.setTotalItems(10)
		when:
		purgeOrderScheduler.doScheduledTask()
		then:
		items.size() == 0
		0*cStata.close()
		0*cStata.execute()
		1*conn.close()
	}
	def "Scheculer runs to remove orders more than two years throws TransactionDemarcationException"(){
		given :
		purgeOrderScheduler= Spy()
		purgeOrderScheduler.setSchedulerEnabled(true)
		purgeOrderScheduler.setLoggingInfo(false)
		purgeOrderScheduler.setLoggingDebug(false)
		purgeOrderScheduler.setLoggingError(true)
		purgeOrderScheduler.setCatalogTools(catalogToolsMock)
		catalogToolsMock.getValueForConfigKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "max_items_per_transaction", 5)>>5
		catalogToolsMock.getValueForConfigKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "total_items_to_purge", 10)>>10
		List items = new ArrayList<RepositoryItemMock>()
		RepositoryItemMock itemMock = new RepositoryItemMock()
		itemMock.setRepositoryId("O1234")
		items.add(itemMock)
		RepositoryItem[] repItems = items.toArray()
		purgeOrderScheduler.findAllPurgeOrders(_) >> repItems
		purgeOrderScheduler.getOrderManager() >> orderManagerMock
		orderManagerMock.getOrderTools() >> orderToolsMock
		orderToolsMock.getOrderRepository() >> gsaRepositoryMock >> repositoryMock
		gsaRepositoryMock.getDataSource()  >> dataSrc
		dataSrc.getConnection()  >> {throw new SQLException("Mock of sql exception")}
		conn.isClosed() >> false
		0*conn.prepareCall(_) >> cStata
		purgeOrderScheduler.setRqlQuery("submittedDate < ?0 RANGE +?1")
		purgeOrderScheduler.setMaxItemsPerTransaction(5)
		purgeOrderScheduler.setTotalItems(10)
		1*purgeOrderScheduler.logError("TransactionDemarcationException from processPurgeOrders", _);
		when:"TransactionDemarcationException from processPurgeOrders"
		purgeOrderScheduler.doScheduledTask()
		then:
		0*cStata.close()
		0*cStata.execute()
		0*conn.close()
		
	}
	def "Scheculer runs to remove orders more than two years throws Commerce Exception"(){
		given :
		purgeOrderScheduler= Spy()
		purgeOrderScheduler.setSchedulerEnabled(true)
		purgeOrderScheduler.setLoggingInfo(false)
		purgeOrderScheduler.setLoggingDebug(false)
		purgeOrderScheduler.setLoggingError(true)
		purgeOrderScheduler.setCatalogTools(catalogToolsMock)
		catalogToolsMock.getValueForConfigKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "max_items_per_transaction", 5)>>5
		catalogToolsMock.getValueForConfigKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "total_items_to_purge", 10)>>10
		purgeOrderScheduler.getTransactionManager() >> transactionManagerMock
		List items = new ArrayList<RepositoryItemMock>()
		RepositoryItemMock itemMock = new RepositoryItemMock()
		itemMock.setRepositoryId("O1234")
		items.add(itemMock)
		RepositoryItem[] repItems = items.toArray()
		purgeOrderScheduler.findAllPurgeOrders(_) >> repItems
		purgeOrderScheduler.getOrderManager() >> orderManagerMock
		orderManagerMock.getOrderTools() >> orderToolsMock
		orderToolsMock.getOrderRepository() >> gsaRepositoryMock >> repositoryMock
		gsaRepositoryMock.getDataSource()  >> dataSrc
		dataSrc.getConnection()  >> conn
		conn.isClosed() >> false
		1*conn.prepareCall(_) >> cStata
		purgeOrderScheduler.setRqlQuery("submittedDate < ?0 RANGE +?1")
		purgeOrderScheduler.setMaxItemsPerTransaction(5)
		purgeOrderScheduler.setTotalItems(10)
		orderManagerMock.removeOrder(_) >> {throw new CommerceException("CommerceException from processPurgeOrders")}
		1*purgeOrderScheduler.logError("CommerceException from processPurgeOrders:", _);
		when:"CommerceException from processPurgeOrders"
		purgeOrderScheduler.doScheduledTask()
		then:
		1*cStata.close()
		1*cStata.execute()
		1*conn.close()
	}
	def "fecthing orders more than two years"(){
		given :
		purgeOrderScheduler= Spy()
		purgeOrderScheduler.setSchedulerEnabled(true)
		purgeOrderScheduler.setLoggingInfo(true)
		purgeOrderScheduler.setLoggingDebug(true)
		purgeOrderScheduler.setCatalogTools(catalogToolsMock)
		catalogToolsMock.getValueForConfigKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "max_items_per_transaction", 5)>>5
		catalogToolsMock.getValueForConfigKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "total_items_to_purge", 10)>>10
		List items = new ArrayList<RepositoryItemMock>()
		RepositoryItemMock itemMock = new RepositoryItemMock()
		itemMock.setRepositoryId("O1234")
		items.add(itemMock)
		RepositoryItem[] repItems = items.toArray()
		purgeOrderScheduler.purgeOrdersFromRepository(_) >> repItems
		when:
		List orders = purgeOrderScheduler.findAllPurgeOrders(2)
		then:
		orders.size() == 1
	}
	def "fecthing orders more than two years throws repository exception"(){
		given :
		purgeOrderScheduler.setSchedulerEnabled(true)
		purgeOrderScheduler.setLoggingInfo(false)
		purgeOrderScheduler.setLoggingDebug(true)
		orderManagerMock.getOrderTools() >> orderToolsMock
		orderToolsMock.getOrderRepository() >> repositoryMock
		repositoryMock.getItemDescriptor("inCompleteOrders") >> {throw new RepositoryException("Repository Exception in findAllPurgeOrders:")}
		itemDecsMock.getRepositoryView() >> orderRepositoryViewMock
		orderRepositoryViewMock.getQueryBuilder() >> queyrbuilderMock
		when:"Repository Exception in findAllPurgeOrders"
		def orders = purgeOrderScheduler.findAllPurgeOrders(2)
		then:
		orders == null
		
	}
	def "fecthing orders more than two years when order view not available"(){
		given :
		purgeOrderScheduler.setSchedulerEnabled(true)
		purgeOrderScheduler.setLoggingInfo(true)
		purgeOrderScheduler.setLoggingDebug(true)
		orderManagerMock.getOrderTools() >> orderToolsMock
		orderToolsMock.getOrderRepository() >> repositoryMock
		repositoryMock.getItemDescriptor("inCompleteOrders") >> itemDecsMock
		itemDecsMock.getRepositoryView() >> null
		orderRepositoryViewMock.getQueryBuilder() >> queyrbuilderMock
		when:"order view is null::"
		def orders = purgeOrderScheduler.findAllPurgeOrders(2)
		then:
		orders == null
	}
	def "fecthing orders more than two years from repository"(){
		given :
		purgeOrderScheduler.setSchedulerEnabled(true)
		purgeOrderScheduler.setLoggingInfo(true)
		purgeOrderScheduler.setLoggingDebug(true)
		orderManagerMock.getOrderTools() >> orderToolsMock
		orderToolsMock.getOrderRepository() >> repositoryMock
		repositoryMock.getItemDescriptor("inCompleteOrders") >> itemDecsMock
		itemDecsMock.getRepositoryView() >> orderRepositoryViewMock
		orderRepositoryViewMock.getQueryBuilder() >> queyrbuilderMock
		when:
		def orders = purgeOrderScheduler.findAllPurgeOrders(2)
		then:
		orders == null
	}
}
