package com.bbb.commerce.inventory

import java.sql.Timestamp
import java.util.Calendar;
import java.util.List;

import javax.transaction.Transaction
import javax.transaction.TransactionManager

import spock.lang.specification.BBBExtendedSpec
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.dtm.TransactionImpl
import atg.dtm.TransactionManagerImpl
import atg.repository.ItemDescriptorImpl;
import atg.repository.MutableRepository
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository
import atg.repository.RepositoryException
import atg.repository.RepositoryImpl
import atg.repository.RepositoryItem
import atg.repository.RepositoryView;
import atg.repository.rql.ComparisonQuery
import atg.repository.rql.RqlStatement
import atg.service.lockmanager.ClientLockManager
import atg.service.lockmanager.DeadlockException
import atg.service.lockmanager.LockManagerException
import atg.service.lockmanager.LockReleaser

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.inventory.vo.InventoryFeedVO
import com.bbb.commerce.inventory.vo.InventoryVO;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.repository.RepositoryItemMock

class InventoryToolsImplSpecification  extends BBBExtendedSpec{

	def Repository inventoryRepositoryMock = Mock()
	def BBBCatalogToolsImpl catalogToolsMock  = Mock()
	
	def ClientLockManager clientLockManagerMock  = Mock()
	def TransactionManagerImpl transactionManagerMock  = Mock()
	def InventoryToolsImpl testObj
	
	
	
	def NA="NA"
	
	def setup(){
		testObj = Spy()
		testObj.setLoggingDebug(true)
		testObj.setInventoryRepository(inventoryRepositoryMock)
		testObj.getInventoryRepository()
		testObj.setCatalogTools(catalogToolsMock)
		testObj.getCatalogTools()
		testObj.setTransactionManager(transactionManagerMock)
		testObj.getTransactionManager()
		testObj.setClientLockManager(clientLockManagerMock)
		testObj.getClientLockManager()
	
	   
	}
//-------------------------------------    getInventoryFeedUpdates(final String status)   ------------------------------------------------------------
	def "getInventoryFeedUpdates by status"(){
		
		given:
		String feedID="feedID"
		String feedStatus="feedStatus"
		String catalogRefId="catalogRefId"
		String creationDate="creationDate"
		String startDate="startDate"
		String endDate="endDate"
		String displayName="displayName"
		String description="description"
		String stockLevel="stockLevel"
		String siteStockLevel="siteStockLevel"
		String registryStockLevel="registryStockLevel"
		String translations="translations"
		String buyBuyBabySiteCode="BuyBuyBabySiteCode"
		String bedBathCanadaSiteCode="BedBathCanadaSiteCode"
		
		String catalogRefIdVal="catalogRefId_1"
		
		Calendar calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, - 10);
		java.util.Date now = calendar.getTime()
		java.sql.Timestamp creationDateVal = new java.sql.Timestamp(now.getTime())
		
		 
		calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, +1);
		 now = calendar.getTime()
		java.sql.Timestamp startDateVal = new java.sql.Timestamp(now.getTime())
		
		
		
		calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, + 10);
		 now = calendar.getTime()
		java.sql.Timestamp endDateVal = new java.sql.Timestamp(now.getTime())
		
		String displayNameVal="displayName_11"
		String descriptionVal="description_11"
		Long stockLevelVal=100L
		Long siteStockLevelVal=40L
		Long registryStockLevelVal=200L
		
		
		
		String status="CREATE"
		
		String fullOpenInvItemId="fullOpenInvItemId_1"
		String fullOpenStatus="FULL_OPEN"
		String fullOpenInvItemFeedId="fullOpenInvItemFeedId_1"		
		RepositoryItemMock fullOpenInventoryItemMock = Mock(["id":fullOpenInvItemId])
		fullOpenInventoryItemMock.getRepositoryId() >> fullOpenInvItemId
		fullOpenInventoryItemMock.getPropertyValue(feedStatus) >> fullOpenStatus
		fullOpenInventoryItemMock.getPropertyValue(feedID) >> fullOpenInvItemFeedId
		
		RepositoryView inventoryFeedViewMock=Mock()
		inventoryRepositoryMock.getView("inventoryFeed") >> inventoryFeedViewMock
		
		RepositoryView inventoryViewMock=Mock()
		inventoryRepositoryMock.getView("inventory") >> inventoryViewMock
		
		
		String partialOpenInvItemId="partialOpenInvItemId_1"
		String partialOpenStatus="PARTIAL_OPEN" 
		String partialOpenInvItemFeedId="partialOpenInvItemFeedId_1"		
		RepositoryItemMock partialInventoryItemMock = Mock(["id":partialOpenInvItemId])
		partialInventoryItemMock.getRepositoryId() >> partialOpenInvItemId
		partialInventoryItemMock.getPropertyValue(feedStatus) >> partialOpenStatus
		partialInventoryItemMock.getPropertyValue(feedID) >> partialOpenInvItemFeedId
		
		RepositoryItemMock[] inventoryFeedItemsMock=[fullOpenInventoryItemMock,partialInventoryItemMock]
		
		//Item from inventory View
		
		String inventoryItemId="partialOpenInvItemId_1"
		String inventoryStatus="PARTIAL_OPEN"
		String inventoryFeedId="partialOpenInvItemFeedId_1"
		RepositoryItemMock inventoryItemMock = Mock(["id":inventoryItemId])
		inventoryItemMock.getRepositoryId() >> inventoryItemId
		inventoryItemMock.getPropertyValue(feedStatus) >> inventoryStatus
		inventoryItemMock.getPropertyValue(feedID) >> inventoryFeedId
		inventoryItemMock.getPropertyValue(catalogRefId)>> catalogRefIdVal
		inventoryItemMock.getPropertyValue(creationDate)>> creationDateVal
		inventoryItemMock.getPropertyValue(startDate)>> startDateVal
		inventoryItemMock.getPropertyValue(endDate)>> endDateVal
		inventoryItemMock.getPropertyValue(displayName)>> displayNameVal
		inventoryItemMock.getPropertyValue(description)>> descriptionVal
		inventoryItemMock.getPropertyValue(stockLevel)>> stockLevelVal
		inventoryItemMock.getPropertyValue(siteStockLevel)>> siteStockLevelVal
		inventoryItemMock.getPropertyValue(registryStockLevel)>> registryStockLevelVal
		testObj.threadSleep(_) >> {}
		
		// translation
		
		
		String babyTransItemId="babyTransItemId_1"
		Long babyTransSiteStockLevelVal=13L
		Long babyTransRegistryStockLevelVal=17L
		
		RepositoryItemMock babyInventoryTranItemMock = Mock(["id":babyTransItemId])
		babyInventoryTranItemMock.getRepositoryId() >> babyTransItemId
		babyInventoryTranItemMock.getPropertyValue(siteStockLevel)>> babyTransSiteStockLevelVal
		babyInventoryTranItemMock.getPropertyValue(registryStockLevel)>> babyTransRegistryStockLevelVal
		
		
		String caTransItemId="babyTransItemId_1"
		Long caTransSiteStockLevelVal=23L
		Long caTransRegistryStockLevelVal=27L
		
		RepositoryItemMock caInventoryTranItemMock = Mock(["id":caTransItemId])
		caInventoryTranItemMock.getRepositoryId() >> caTransItemId
		caInventoryTranItemMock.getPropertyValue(siteStockLevel)>> caTransSiteStockLevelVal
		caInventoryTranItemMock.getPropertyValue(registryStockLevel)>> caTransRegistryStockLevelVal
		
		Map<String,RepositoryItemMock> translationMap= new HashMap<String,RepositoryItemMock>()
		translationMap.put(buyBuyBabySiteCode, babyInventoryTranItemMock)
		translationMap.put(bedBathCanadaSiteCode, caInventoryTranItemMock)
		
		inventoryItemMock.getPropertyValue("translations") >> translationMap
		RepositoryItemMock[] inventoryItemsMock=[inventoryItemMock]
		
		testObj.executeQuery(inventoryFeedViewMock,_,_) >> inventoryFeedItemsMock
		
		testObj.executeQuery(inventoryViewMock,_,_) >> inventoryItemsMock
		List<String> listOfSiteCodes = new ArrayList<String>();
		listOfSiteCodes.add(buyBuyBabySiteCode)
		1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, buyBuyBabySiteCode) >> listOfSiteCodes
		listOfSiteCodes = new ArrayList<String>();
		listOfSiteCodes.add(bedBathCanadaSiteCode)
		1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, bedBathCanadaSiteCode) >> listOfSiteCodes
		
		when:
		List<InventoryFeedVO> invVoList=	testObj.getInventoryFeedUpdates(status)
		then:
		invVoList != null 
		invVoList.get(0).getID() == fullOpenInvItemId
		invVoList.get(0).getFeedID() == fullOpenInvItemFeedId
		invVoList.get(0).getFeedStatus() == fullOpenStatus
		invVoList.get(1).getID() == partialOpenInvItemId
		invVoList.get(1).getFeedID() == partialOpenInvItemFeedId
		invVoList.get(1).getFeedStatus() == partialOpenStatus
		invVoList.get(1).getCreationDate() ==creationDateVal
		invVoList.get(1).getStartDate() == startDateVal
		invVoList.get(1).getEndDate() ==endDateVal
		invVoList.get(1).getSiteStockLevel() ==  siteStockLevelVal
		invVoList.get(1).getGlobalStockLevel() ==stockLevelVal
		invVoList.get(1).getGiftRegistryStockLevel() == registryStockLevelVal
		invVoList.get(1).getSkuID() == catalogRefIdVal
		invVoList.get(1).getDisplayName() == displayNameVal
		invVoList.get(1).getDescription() == descriptionVal
		invVoList.get(1).getBASiteStockLevel()==babyTransSiteStockLevelVal
		invVoList.get(1).getBAGiftRegistryStockLevel() == babyTransRegistryStockLevelVal
		invVoList.get(1).getCASiteStockLevel()==caTransSiteStockLevelVal
		invVoList.get(1).getCAGiftRegistryStockLevel() == caTransRegistryStockLevelVal
		
	}
	
	def "getInventoryFeedUpdates by status | siteCode not found in config key"(){
		
		given:
		testObj.threadSleep(_) >> {}
		String feedID="feedID"
		String feedStatus="feedStatus"
		String catalogRefId="catalogRefId"
		String creationDate="creationDate"
		String startDate="startDate"
		String endDate="endDate"
		String displayName="displayName"
		String description="description"
		String stockLevel="stockLevel"
		String siteStockLevel="siteStockLevel"
		String registryStockLevel="registryStockLevel"
		String translations="translations"
		String buyBuyBabySiteCode="BuyBuyBabySiteCode"
		String bedBathCanadaSiteCode="BedBathCanadaSiteCode"
		
		String catalogRefIdVal="catalogRefId_1"
		
		RqlStatement rqlStatementMock = Mock()
		Calendar calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, - 10);
		java.util.Date now = calendar.getTime()
		java.sql.Timestamp creationDateVal = new java.sql.Timestamp(now.getTime())
		
		 
		calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, +1);
		 now = calendar.getTime()
		java.sql.Timestamp startDateVal = new java.sql.Timestamp(now.getTime())
		
		
		
		calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, + 10);
		 now = calendar.getTime()
		java.sql.Timestamp endDateVal = new java.sql.Timestamp(now.getTime())
		
		String displayNameVal="displayName_11"
		String descriptionVal="description_11"
		Long stockLevelVal=100L
		Long siteStockLevelVal=40L
		Long registryStockLevelVal=200L
		
		
		
		String status="CREATE"
		
		String fullOpenInvItemId="fullOpenInvItemId_1"
		String fullOpenStatus="FULL_OPEN"
		String fullOpenInvItemFeedId="fullOpenInvItemFeedId_1"
		RepositoryItemMock fullOpenInventoryItemMock = Mock(["id":fullOpenInvItemId])
		fullOpenInventoryItemMock.getRepositoryId() >> fullOpenInvItemId
		fullOpenInventoryItemMock.getPropertyValue(feedStatus) >> fullOpenStatus
		fullOpenInventoryItemMock.getPropertyValue(feedID) >> fullOpenInvItemFeedId
		
		RepositoryView inventoryFeedViewMock=Mock()
		inventoryRepositoryMock.getView("inventoryFeed") >> inventoryFeedViewMock
		
		RepositoryView inventoryViewMock=Mock()
		inventoryRepositoryMock.getView("inventory") >> inventoryViewMock
		
		
		String partialOpenInvItemId="partialOpenInvItemId_1"
		String partialOpenStatus="PARTIAL_OPEN"
		String partialOpenInvItemFeedId="partialOpenInvItemFeedId_1"
		RepositoryItemMock partialInventoryItemMock = Mock(["id":partialOpenInvItemId])
		partialInventoryItemMock.getRepositoryId() >> partialOpenInvItemId
		partialInventoryItemMock.getPropertyValue(feedStatus) >> partialOpenStatus
		partialInventoryItemMock.getPropertyValue(feedID) >> partialOpenInvItemFeedId
		
		RepositoryItemMock[] inventoryFeedItemsMock=[fullOpenInventoryItemMock,partialInventoryItemMock]
		
		//Item from inventory View
		
		String inventoryItemId="partialOpenInvItemId_1"
		String inventoryStatus="PARTIAL_OPEN"
		String inventoryFeedId="partialOpenInvItemFeedId_1"
		RepositoryItemMock inventoryItemMock = Mock(["id":inventoryItemId])
		inventoryItemMock.getRepositoryId() >> inventoryItemId
		inventoryItemMock.getPropertyValue(feedStatus) >> inventoryStatus
		inventoryItemMock.getPropertyValue(feedID) >> inventoryFeedId
		inventoryItemMock.getPropertyValue(catalogRefId)>> catalogRefIdVal
		inventoryItemMock.getPropertyValue(creationDate)>> creationDateVal
		inventoryItemMock.getPropertyValue(startDate)>> startDateVal
		inventoryItemMock.getPropertyValue(endDate)>> endDateVal
		inventoryItemMock.getPropertyValue(displayName)>> displayNameVal
		inventoryItemMock.getPropertyValue(description)>> descriptionVal
		inventoryItemMock.getPropertyValue(stockLevel)>> stockLevelVal
		inventoryItemMock.getPropertyValue(siteStockLevel)>> siteStockLevelVal
		inventoryItemMock.getPropertyValue(registryStockLevel)>> registryStockLevelVal
		
		
		// translation
		
		
		String babyTransItemId="babyTransItemId_1"
		Long babyTransSiteStockLevelVal=13L
		Long babyTransRegistryStockLevelVal=17L
		
		RepositoryItemMock babyInventoryTranItemMock = Mock(["id":babyTransItemId])
		babyInventoryTranItemMock.getRepositoryId() >> babyTransItemId
		babyInventoryTranItemMock.getPropertyValue(siteStockLevel)>> babyTransSiteStockLevelVal
		babyInventoryTranItemMock.getPropertyValue(registryStockLevel)>> babyTransRegistryStockLevelVal
		
		
		String caTransItemId="babyTransItemId_1"
		Long caTransSiteStockLevelVal=23L
		Long caTransRegistryStockLevelVal=27L
		
		RepositoryItemMock caInventoryTranItemMock = Mock(["id":caTransItemId])
		caInventoryTranItemMock.getRepositoryId() >> caTransItemId
		caInventoryTranItemMock.getPropertyValue(siteStockLevel)>> caTransSiteStockLevelVal
		caInventoryTranItemMock.getPropertyValue(registryStockLevel)>> caTransRegistryStockLevelVal
		
		Map<String,RepositoryItemMock> translationMap= new HashMap<String,RepositoryItemMock>()
		translationMap.put(buyBuyBabySiteCode, babyInventoryTranItemMock)
		translationMap.put(bedBathCanadaSiteCode, caInventoryTranItemMock)
		
		inventoryItemMock.getPropertyValue("translations") >> translationMap
		RepositoryItemMock[] inventoryItemsMock=[inventoryItemMock]
		
		testObj.executeQuery(inventoryFeedViewMock,_,_) >> inventoryFeedItemsMock
		
		testObj.executeQuery(inventoryViewMock,_,_) >> inventoryItemsMock
		
		1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, buyBuyBabySiteCode) >> null
		
		1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, bedBathCanadaSiteCode) >> null
		
		when:
		List<InventoryFeedVO> invVoList=	testObj.getInventoryFeedUpdates(status)
		then:
		invVoList != null
		invVoList.get(0).getID() == fullOpenInvItemId
		invVoList.get(0).getFeedID() == fullOpenInvItemFeedId
		invVoList.get(0).getFeedStatus() == fullOpenStatus
		invVoList.get(1).getID() == partialOpenInvItemId
		invVoList.get(1).getFeedID() == partialOpenInvItemFeedId
		invVoList.get(1).getFeedStatus() == partialOpenStatus
		invVoList.get(1).getCreationDate() ==creationDateVal
		invVoList.get(1).getStartDate() == startDateVal
		invVoList.get(1).getEndDate() ==endDateVal
		invVoList.get(1).getSiteStockLevel() ==  siteStockLevelVal
		invVoList.get(1).getGlobalStockLevel() ==stockLevelVal
		invVoList.get(1).getGiftRegistryStockLevel() == registryStockLevelVal
		invVoList.get(1).getSkuID() == catalogRefIdVal
		invVoList.get(1).getDisplayName() == displayNameVal
		invVoList.get(1).getDescription() == descriptionVal
		invVoList.get(1).getBASiteStockLevel()==null
		invVoList.get(1).getBAGiftRegistryStockLevel() == null
		invVoList.get(1).getCASiteStockLevel()==null
		invVoList.get(1).getCAGiftRegistryStockLevel() == null
		
	}
	
	def "getInventoryFeedUpdates by status | No translation found for baby and CA"(){
		
		given:
		testObj.threadSleep(_) >> {}
		String feedID="feedID"
		String feedStatus="feedStatus"
		String catalogRefId="catalogRefId"
		String creationDate="creationDate"
		String startDate="startDate"
		String endDate="endDate"
		String displayName="displayName"
		String description="description"
		String stockLevel="stockLevel"
		String siteStockLevel="siteStockLevel"
		String registryStockLevel="registryStockLevel"
		String translations="translations"
		String buyBuyBabySiteCode="BuyBuyBabySiteCode"
		String bedBathCanadaSiteCode="BedBathCanadaSiteCode"
		
		String catalogRefIdVal="catalogRefId_1"
		
		RqlStatement rqlStatementMock = Mock()
		Calendar calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, - 10);
		java.util.Date now = calendar.getTime()
		java.sql.Timestamp creationDateVal = new java.sql.Timestamp(now.getTime())
		
		 
		calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, +1);
		 now = calendar.getTime()
		java.sql.Timestamp startDateVal = new java.sql.Timestamp(now.getTime())
		
		
		
		calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, + 10);
		 now = calendar.getTime()
		java.sql.Timestamp endDateVal = new java.sql.Timestamp(now.getTime())
		
		String displayNameVal="displayName_11"
		String descriptionVal="description_11"
		Long stockLevelVal=100L
		Long siteStockLevelVal=40L
		Long registryStockLevelVal=200L
		
		
		
		String status="CREATE"
		
		String fullOpenInvItemId="fullOpenInvItemId_1"
		String fullOpenStatus="FULL_OPEN"
		String fullOpenInvItemFeedId="fullOpenInvItemFeedId_1"
		RepositoryItemMock fullOpenInventoryItemMock = Mock(["id":fullOpenInvItemId])
		fullOpenInventoryItemMock.getRepositoryId() >> fullOpenInvItemId
		fullOpenInventoryItemMock.getPropertyValue(feedStatus) >> fullOpenStatus
		fullOpenInventoryItemMock.getPropertyValue(feedID) >> fullOpenInvItemFeedId
		
		RepositoryView inventoryFeedViewMock=Mock()
		inventoryRepositoryMock.getView("inventoryFeed") >> inventoryFeedViewMock
		
		RepositoryView inventoryViewMock=Mock()
		inventoryRepositoryMock.getView("inventory") >> inventoryViewMock
		
		
		String partialOpenInvItemId="partialOpenInvItemId_1"
		String partialOpenStatus="PARTIAL_OPEN"
		String partialOpenInvItemFeedId="partialOpenInvItemFeedId_1"
		RepositoryItemMock partialInventoryItemMock = Mock(["id":partialOpenInvItemId])
		partialInventoryItemMock.getRepositoryId() >> partialOpenInvItemId
		partialInventoryItemMock.getPropertyValue(feedStatus) >> partialOpenStatus
		partialInventoryItemMock.getPropertyValue(feedID) >> partialOpenInvItemFeedId
		
		RepositoryItemMock[] inventoryFeedItemsMock=[fullOpenInventoryItemMock,partialInventoryItemMock]
		
		//Item from inventory View
		
		String inventoryItemId="partialOpenInvItemId_1"
		String inventoryStatus="PARTIAL_OPEN"
		String inventoryFeedId="partialOpenInvItemFeedId_1"
		RepositoryItemMock inventoryItemMock = Mock(["id":inventoryItemId])
		inventoryItemMock.getRepositoryId() >> inventoryItemId
		inventoryItemMock.getPropertyValue(feedStatus) >> inventoryStatus
		inventoryItemMock.getPropertyValue(feedID) >> inventoryFeedId
		inventoryItemMock.getPropertyValue(catalogRefId)>> catalogRefIdVal
		inventoryItemMock.getPropertyValue(creationDate)>> creationDateVal
		inventoryItemMock.getPropertyValue(startDate)>> startDateVal
		inventoryItemMock.getPropertyValue(endDate)>> endDateVal
		inventoryItemMock.getPropertyValue(displayName)>> displayNameVal
		inventoryItemMock.getPropertyValue(description)>> descriptionVal
		inventoryItemMock.getPropertyValue(stockLevel)>> stockLevelVal
		inventoryItemMock.getPropertyValue(siteStockLevel)>> siteStockLevelVal
		inventoryItemMock.getPropertyValue(registryStockLevel)>> registryStockLevelVal
		
		
		// translation
		
		
		String babyTransItemId="babyTransItemId_1"
		Long babyTransSiteStockLevelVal=13L
		Long babyTransRegistryStockLevelVal=17L
		
		RepositoryItemMock babyInventoryTranItemMock = Mock(["id":babyTransItemId])
		babyInventoryTranItemMock.getRepositoryId() >> babyTransItemId
		babyInventoryTranItemMock.getPropertyValue(siteStockLevel)>> babyTransSiteStockLevelVal
		babyInventoryTranItemMock.getPropertyValue(registryStockLevel)>> babyTransRegistryStockLevelVal
		
		
		String caTransItemId="babyTransItemId_1"
		Long caTransSiteStockLevelVal=23L
		Long caTransRegistryStockLevelVal=27L
		
		RepositoryItemMock caInventoryTranItemMock = Mock(["id":caTransItemId])
		caInventoryTranItemMock.getRepositoryId() >> caTransItemId
		caInventoryTranItemMock.getPropertyValue(siteStockLevel)>> caTransSiteStockLevelVal
		caInventoryTranItemMock.getPropertyValue(registryStockLevel)>> caTransRegistryStockLevelVal
		
		Map<String,RepositoryItemMock> translationMap= new HashMap<String,RepositoryItemMock>()
		translationMap.put(buyBuyBabySiteCode, babyInventoryTranItemMock)
		translationMap.put(bedBathCanadaSiteCode, caInventoryTranItemMock)
		
		inventoryItemMock.getPropertyValue("translations") >> null //set translation to null
		RepositoryItemMock[] inventoryItemsMock=[inventoryItemMock]
		
		testObj.executeQuery(inventoryFeedViewMock,_,_) >> inventoryFeedItemsMock
		
		testObj.executeQuery(inventoryViewMock,_,_) >> inventoryItemsMock
		
		
		 
		
		
		
		when:
		List<InventoryFeedVO> invVoList=	testObj.getInventoryFeedUpdates(status)
		then:
		invVoList != null
		invVoList.get(0).getID() == fullOpenInvItemId
		invVoList.get(0).getFeedID() == fullOpenInvItemFeedId
		invVoList.get(0).getFeedStatus() == fullOpenStatus
		invVoList.get(1).getID() == partialOpenInvItemId
		invVoList.get(1).getFeedID() == partialOpenInvItemFeedId
		invVoList.get(1).getFeedStatus() == partialOpenStatus
		invVoList.get(1).getCreationDate() ==creationDateVal
		invVoList.get(1).getStartDate() == startDateVal
		invVoList.get(1).getEndDate() ==endDateVal
		invVoList.get(1).getSiteStockLevel() ==  siteStockLevelVal
		invVoList.get(1).getGlobalStockLevel() ==stockLevelVal
		invVoList.get(1).getGiftRegistryStockLevel() == registryStockLevelVal
		invVoList.get(1).getSkuID() == catalogRefIdVal
		invVoList.get(1).getDisplayName() == displayNameVal
		invVoList.get(1).getDescription() == descriptionVal
		invVoList.get(1).getBASiteStockLevel()==null
		invVoList.get(1).getBAGiftRegistryStockLevel() == null
		invVoList.get(1).getCASiteStockLevel()==null
		invVoList.get(1).getCAGiftRegistryStockLevel() == null
		
	}
	
	def "getInventoryFeedUpdates by status | item not found in inventory View"(){
		
		given:
		testObj.threadSleep(_) >> {}
		RqlStatement rqlStatementMock = Mock()
		String feedID="feedID"
		String feedStatus="feedStatus"
		
		
		
		String status="CREATE"
		
		String fullOpenInvItemId="fullOpenInvItemId_1"
		String fullOpenStatus="FULL_OPEN"
		String fullOpenInvItemFeedId="fullOpenInvItemFeedId_1"
		RepositoryItemMock fullOpenInventoryItemMock = Mock(["id":fullOpenInvItemId])
		fullOpenInventoryItemMock.getRepositoryId() >> fullOpenInvItemId
		fullOpenInventoryItemMock.getPropertyValue(feedStatus) >> fullOpenStatus
		fullOpenInventoryItemMock.getPropertyValue(feedID) >> fullOpenInvItemFeedId
		
		RepositoryView inventoryFeedViewMock=Mock()
		inventoryRepositoryMock.getView("inventoryFeed") >> inventoryFeedViewMock
		
		RepositoryView inventoryViewMock=Mock()
		inventoryRepositoryMock.getView("inventory") >> inventoryViewMock
		
		
		String partialOpenInvItemId="partialOpenInvItemId_1"
		String partialOpenStatus="PARTIAL_OPEN"
		String partialOpenInvItemFeedId="partialOpenInvItemFeedId_1"
		RepositoryItemMock partialInventoryItemMock = Mock(["id":partialOpenInvItemId])
		partialInventoryItemMock.getRepositoryId() >> partialOpenInvItemId
		partialInventoryItemMock.getPropertyValue(feedStatus) >> partialOpenStatus
		partialInventoryItemMock.getPropertyValue(feedID) >> partialOpenInvItemFeedId
		
		RepositoryItemMock[] inventoryFeedItemsMock=[fullOpenInventoryItemMock,partialInventoryItemMock]
		
		
		
		testObj.executeQuery(inventoryFeedViewMock,_,_) >> inventoryFeedItemsMock
		
		testObj.executeQuery(inventoryViewMock,_,_) >> null
		
		
		
		
		when:
		List<InventoryFeedVO> invVoList=	testObj.getInventoryFeedUpdates(status)
		then:
		invVoList != null
		invVoList.get(0).getID() == fullOpenInvItemId
		invVoList.get(0).getFeedID() == fullOpenInvItemFeedId
		invVoList.get(0).getFeedStatus() == fullOpenStatus
		
		
	}
	
	def "getInventoryFeedUpdates by status | RepositoryException in getRepositoryItemToInventoryFeedVO"(){
		
		given:
		testObj.threadSleep(_) >> {}
		RqlStatement rqlStatementMock = Mock()
		String feedID="feedID"
		String feedStatus="feedStatus"
		
		
		
		String status="CREATE"
		
		String fullOpenInvItemId="fullOpenInvItemId_1"
		String fullOpenStatus="FULL_OPEN"
		String fullOpenInvItemFeedId="fullOpenInvItemFeedId_1"
		RepositoryItemMock fullOpenInventoryItemMock = Mock(["id":fullOpenInvItemId])
		fullOpenInventoryItemMock.getRepositoryId() >> fullOpenInvItemId
		fullOpenInventoryItemMock.getPropertyValue(feedStatus) >> fullOpenStatus
		fullOpenInventoryItemMock.getPropertyValue(feedID) >> fullOpenInvItemFeedId
		
		RepositoryView inventoryFeedViewMock=Mock()
		inventoryRepositoryMock.getView("inventoryFeed") >> inventoryFeedViewMock
		
		RepositoryView inventoryViewMock=Mock()
		inventoryRepositoryMock.getView("inventory") >> inventoryViewMock
		
		
		String partialOpenInvItemId="partialOpenInvItemId_1"
		String partialOpenStatus="PARTIAL_OPEN"
		String partialOpenInvItemFeedId="partialOpenInvItemFeedId_1"
		RepositoryItemMock partialInventoryItemMock = Mock(["id":partialOpenInvItemId])
		partialInventoryItemMock.getRepositoryId() >> partialOpenInvItemId
		partialInventoryItemMock.getPropertyValue(feedStatus) >> partialOpenStatus
		partialInventoryItemMock.getPropertyValue(feedID) >> partialOpenInvItemFeedId
		
		RepositoryItemMock[] inventoryFeedItemsMock=[fullOpenInventoryItemMock,partialInventoryItemMock]
		
		
		
		testObj.executeQuery(inventoryFeedViewMock,_,_) >> inventoryFeedItemsMock
		
		testObj.executeQuery(inventoryViewMock,_,_) >> { throw new RepositoryException("RepositoryException occured during the invocation of executeQuery") }
		
		
		
		
		when:
		List<InventoryFeedVO> invVoList=	testObj.getInventoryFeedUpdates(status)
		then:
		invVoList != null
		invVoList.get(0).getID() == fullOpenInvItemId
		invVoList.get(0).getFeedID() == fullOpenInvItemFeedId
		invVoList.get(0).getFeedStatus() == fullOpenStatus
		invVoList.size() == 1
		
		
	}
	def "getInventoryFeedUpdates by status | BBBSystemException in getRepositoryItemToInventoryFeedVO"(){
		
		given:
		testObj.threadSleep(_) >> {}
		RqlStatement rqlStatementMock = Mock()
		String feedID="feedID"
		String feedStatus="feedStatus"
		
		
		
		String status="CREATE"
		
		String fullOpenInvItemId="fullOpenInvItemId_1"
		String fullOpenStatus="FULL_OPEN"
		String fullOpenInvItemFeedId="fullOpenInvItemFeedId_1"
		RepositoryItemMock fullOpenInventoryItemMock = Mock(["id":fullOpenInvItemId])
		fullOpenInventoryItemMock.getRepositoryId() >> fullOpenInvItemId
		fullOpenInventoryItemMock.getPropertyValue(feedStatus) >> fullOpenStatus
		fullOpenInventoryItemMock.getPropertyValue(feedID) >> fullOpenInvItemFeedId
		
		RepositoryView inventoryFeedViewMock=Mock()
		inventoryRepositoryMock.getView("inventoryFeed") >> inventoryFeedViewMock
		
		RepositoryView inventoryViewMock=Mock()
		inventoryRepositoryMock.getView("inventory") >> inventoryViewMock
		
		
		String partialOpenInvItemId="partialOpenInvItemId_1"
		String partialOpenStatus="PARTIAL_OPEN"
		String partialOpenInvItemFeedId="partialOpenInvItemFeedId_1"
		RepositoryItemMock partialInventoryItemMock = Mock(["id":partialOpenInvItemId])
		partialInventoryItemMock.getRepositoryId() >> partialOpenInvItemId
		partialInventoryItemMock.getPropertyValue(feedStatus) >> partialOpenStatus
		partialInventoryItemMock.getPropertyValue(feedID) >> partialOpenInvItemFeedId
		
		RepositoryItemMock[] inventoryFeedItemsMock=[fullOpenInventoryItemMock,partialInventoryItemMock]
		
		
		
		testObj.executeQuery(inventoryFeedViewMock,_,_) >> inventoryFeedItemsMock
		
		testObj.executeQuery(inventoryViewMock,_,_) >> { throw new BBBSystemException("BBBSystemException occured during the invocation of executeQuery") }
		
		
		
		
		when:
		List<InventoryFeedVO> invVoList=	testObj.getInventoryFeedUpdates(status)
		then:
		invVoList != null
		invVoList.get(0).getID() == fullOpenInvItemId
		invVoList.get(0).getFeedID() == fullOpenInvItemFeedId
		invVoList.get(0).getFeedStatus() == fullOpenStatus
		
		
	}
	def "getInventoryFeedUpdates by status | BBBBusinessException in getRepositoryItemToInventoryFeedVO"(){
		
		given:
		testObj.threadSleep(_) >> {}
		RqlStatement rqlStatementMock = Mock()
		String feedID="feedID"
		String feedStatus="feedStatus"
		
		
		
		String status="CREATE"
		
		String fullOpenInvItemId="fullOpenInvItemId_1"
		String fullOpenStatus="FULL_OPEN"
		String fullOpenInvItemFeedId="fullOpenInvItemFeedId_1"
		RepositoryItemMock fullOpenInventoryItemMock = Mock(["id":fullOpenInvItemId])
		fullOpenInventoryItemMock.getRepositoryId() >> fullOpenInvItemId
		fullOpenInventoryItemMock.getPropertyValue(feedStatus) >> fullOpenStatus
		fullOpenInventoryItemMock.getPropertyValue(feedID) >> fullOpenInvItemFeedId
		
		RepositoryView inventoryFeedViewMock=Mock()
		inventoryRepositoryMock.getView("inventoryFeed") >> inventoryFeedViewMock
		
		RepositoryView inventoryViewMock=Mock()
		inventoryRepositoryMock.getView("inventory") >> inventoryViewMock
		
		
		String partialOpenInvItemId="partialOpenInvItemId_1"
		String partialOpenStatus="PARTIAL_OPEN"
		String partialOpenInvItemFeedId="partialOpenInvItemFeedId_1"
		RepositoryItemMock partialInventoryItemMock = Mock(["id":partialOpenInvItemId])
		partialInventoryItemMock.getRepositoryId() >> partialOpenInvItemId
		partialInventoryItemMock.getPropertyValue(feedStatus) >> partialOpenStatus
		partialInventoryItemMock.getPropertyValue(feedID) >> partialOpenInvItemFeedId
		
		RepositoryItemMock[] inventoryFeedItemsMock=[fullOpenInventoryItemMock,partialInventoryItemMock]
		
		
		
		testObj.executeQuery(inventoryFeedViewMock,_,_) >> inventoryFeedItemsMock
		
		testObj.executeQuery(inventoryViewMock,_,_) >> { throw new BBBBusinessException("BBBBusinessException occured during the invocation of executeQuery") }
		
		
		
		
		when:
		List<InventoryFeedVO> invVoList=	testObj.getInventoryFeedUpdates(status)
		then:
		invVoList != null
		invVoList.get(0).getID() == fullOpenInvItemId
		invVoList.get(0).getFeedID() == fullOpenInvItemFeedId
		invVoList.get(0).getFeedStatus() == fullOpenStatus
		
		
	}
	
	def "getInventoryFeedUpdates by status | RepositoryException during looking item in inventoryFeed View"(){
		
		given:
		testObj.threadSleep(_) >> {}
		RqlStatement rqlStatementMock = Mock()
		String feedID="feedID"
		String feedStatus="feedStatus"
		String status="CREATE"
		String fullOpenInvItemId="fullOpenInvItemId_1"
		String fullOpenStatus="FULL_OPEN"
		String fullOpenInvItemFeedId="fullOpenInvItemFeedId_1"
		RepositoryItemMock fullOpenInventoryItemMock = Mock(["id":fullOpenInvItemId])
		fullOpenInventoryItemMock.getRepositoryId() >> fullOpenInvItemId
		fullOpenInventoryItemMock.getPropertyValue(feedStatus) >> fullOpenStatus
		fullOpenInventoryItemMock.getPropertyValue(feedID) >> fullOpenInvItemFeedId
		
		RepositoryView inventoryFeedViewMock=Mock()
		inventoryRepositoryMock.getView("inventoryFeed") >> inventoryFeedViewMock
		
		testObj.executeQuery(inventoryFeedViewMock,_,_) >> { throw new RepositoryException("RepositoryException occured during the invocation of executeQuery") }
		
		when:
		List<InventoryFeedVO> invVoList=	testObj.getInventoryFeedUpdates(status)
		then:
		BBBSystemException exception = thrown()
	}
//----------------------------------------------------		invalidateInventoryCache() --------------------------
		
		def "validate invalidateInventoryCache "(){
			given:
			ItemDescriptorImpl inventoryDescriptor = Mock()
			inventoryRepositoryMock.getItemDescriptor(	BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >> inventoryDescriptor
			
			ItemDescriptorImpl inventoryDescriptorTranslation = Mock()
			inventoryRepositoryMock.getItemDescriptor(BBBCatalogConstants.ITEM_TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> inventoryDescriptorTranslation
			
		   expect:
			testObj.invalidateInventoryCache()
			 
		}
		
		def "validate invalidateInventoryCache | handle RepositoryException"(){
			given:
			ItemDescriptorImpl inventoryDescriptor = Mock()
			inventoryRepositoryMock.getItemDescriptor(	BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >> inventoryDescriptor
			
			ItemDescriptorImpl inventoryDescriptorTranslation = Mock()
			inventoryRepositoryMock.getItemDescriptor(BBBCatalogConstants.ITEM_TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> inventoryDescriptorTranslation
			
			1* inventoryDescriptor.invalidateCaches(true) >> { throw new RepositoryException("RepositoryException occured during the invocation of executeQuery") }
		    when:
			testObj.invalidateInventoryCache()
			then:
			BBBSystemException exception = thrown()
			 
		}
		
//----------------------------------------removeItemFromCache(final InventoryFeedVO pInventoryFeed)  --------------------------------------------------------------
		def "validate removeItemFromCache"(){
			given:
			String inventoryItemId= "inventoryItemId_1"
			String inventoryDisplayName= "inventoryDisplayName"
			
			InventoryFeedVO inventoryFeedVO = new InventoryFeedVO()
			inventoryFeedVO.setInventoryID(inventoryItemId)
			inventoryFeedVO.setDisplayName(inventoryDisplayName)
			
			ItemDescriptorImpl inventoryDescriptor = Spy()
			inventoryDescriptor.setItemDescriptorName("inventoryDescriptor") 
			
			RepositoryItemMock inventoryItem=Mock(["id":inventoryItemId])
			
			inventoryItem.getRepositoryId() >> inventoryItemId
			inventoryDescriptor.getRepositoryImpl().getItem(inventoryItemId) >> inventoryItem
			RepositoryImpl repositoryImplMock=Mock()
			inventoryDescriptor.setRepository(repositoryImplMock)
			
			
			String inventoryTranslationItemId= "inventoryTransItemId_1"
			ItemDescriptorImpl inventoryTranslationDescriptor = Mock()
			RepositoryItemMock inventoryTranslationItem=Mock(["id":inventoryTranslationItemId])
			inventoryTranslationItem.getRepositoryId()>> inventoryTranslationItemId
			inventoryTranslationDescriptor.getRepositoryImpl().getItem(inventoryTranslationItemId) >> inventoryTranslationItem
			
			Map<String,RepositoryItemMock> translationMap= new HashMap<String,RepositoryItemMock>()
			translationMap.put(inventoryTranslationItemId, inventoryTranslationItem)
			
			
			
			1* inventoryRepositoryMock.getItemDescriptor(BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >> inventoryDescriptor			
			1* inventoryDescriptor.removeItemFromCache(inventoryItemId, true);	
			1* inventoryDescriptor.getRepositoryImpl().getItem(inventoryItemId) >> inventoryItem
			2* inventoryItem.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> translationMap
			1* inventoryRepositoryMock.getItemDescriptor(BBBCatalogConstants.ITEM_TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> inventoryTranslationDescriptor
			1* inventoryTranslationDescriptor.removeItemFromCache(inventoryTranslationItemId, true)
			1* testObj.logDebug("Removed inventory [" + inventoryItemId + " | " + inventoryDisplayName + "] from cache")
			
			expect:
			testObj.removeItemFromCache(inventoryFeedVO)
		}
		
		def "validate removeItemFromCache | translation is null"(){
			given:
			String inventoryItemId= "inventoryItemId_1"
			String inventoryDisplayName= "inventoryDisplayName"
			
			InventoryFeedVO inventoryFeedVO = new InventoryFeedVO()
			inventoryFeedVO.setInventoryID(inventoryItemId)
			inventoryFeedVO.setDisplayName(inventoryDisplayName)
			
			ItemDescriptorImpl inventoryDescriptor = Spy()
			inventoryDescriptor.setItemDescriptorName("inventoryDescriptor")
			
			RepositoryItemMock inventoryItem=Mock(["id":inventoryItemId])
			
			inventoryItem.getRepositoryId() >> inventoryItemId
			inventoryDescriptor.getRepositoryImpl().getItem(inventoryItemId) >> inventoryItem
			RepositoryImpl repositoryImplMock=Mock()
			inventoryDescriptor.setRepository(repositoryImplMock)
			
			
			String inventoryTranslationItemId= "inventoryTransItemId_1"
			ItemDescriptorImpl inventoryTranslationDescriptor = Mock()
			RepositoryItemMock inventoryTranslationItem=Mock(["id":inventoryTranslationItemId])
			inventoryTranslationItem.getRepositoryId()>> inventoryTranslationItemId
			inventoryTranslationDescriptor.getRepositoryImpl().getItem(inventoryTranslationItemId) >> inventoryTranslationItem
			
			Map<String,RepositoryItemMock> translationMap= new HashMap<String,RepositoryItemMock>()
			translationMap.put(inventoryTranslationItemId, inventoryTranslationItem)
			
			
			
			1* inventoryRepositoryMock.getItemDescriptor(BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >> inventoryDescriptor
			1* inventoryDescriptor.removeItemFromCache(inventoryItemId, true);
			1* inventoryDescriptor.getRepositoryImpl().getItem(inventoryItemId) >> inventoryItem
			1* inventoryItem.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> null
			1* testObj.logDebug("Removed inventory [" + inventoryItemId + " | " + inventoryDisplayName + "] from cache")
			
			expect:
			testObj.removeItemFromCache(inventoryFeedVO)
		}
		
	def "validate removeItemFromCache | translationMap  is Empty"(){
		given:
		String inventoryItemId= "inventoryItemId_1"
		String inventoryDisplayName= "inventoryDisplayName"
		
		InventoryFeedVO inventoryFeedVO = new InventoryFeedVO()
		inventoryFeedVO.setInventoryID(inventoryItemId)
		inventoryFeedVO.setDisplayName(inventoryDisplayName)
		
		ItemDescriptorImpl inventoryDescriptor = Spy()
		inventoryDescriptor.setItemDescriptorName("inventoryDescriptor")
		
		RepositoryItemMock inventoryItem=Mock(["id":inventoryItemId])
		
		inventoryItem.getRepositoryId() >> inventoryItemId
		inventoryDescriptor.getRepositoryImpl().getItem(inventoryItemId) >> inventoryItem
		RepositoryImpl repositoryImplMock=Mock()
		inventoryDescriptor.setRepository(repositoryImplMock)
		
		
		String inventoryTranslationItemId= "inventoryTransItemId_1"
		ItemDescriptorImpl inventoryTranslationDescriptor = Mock()
		 
		
		Map<String,RepositoryItemMock> translationMap= new HashMap<String,RepositoryItemMock>()
		 
		
		
		
		1* inventoryRepositoryMock.getItemDescriptor(BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >> inventoryDescriptor
		1* inventoryDescriptor.removeItemFromCache(inventoryItemId, true);
		1* inventoryDescriptor.getRepositoryImpl().getItem(inventoryItemId) >> inventoryItem
		2* inventoryItem.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> translationMap
		0* inventoryRepositoryMock.getItemDescriptor(BBBCatalogConstants.ITEM_TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> inventoryTranslationDescriptor
		0* inventoryTranslationDescriptor.removeItemFromCache(inventoryTranslationItemId, true)
		1* testObj.logDebug("Removed inventory [" + inventoryItemId + " | " + inventoryDisplayName + "] from cache")
		
		expect:
		testObj.removeItemFromCache(inventoryFeedVO)
	}

def "validate removeItemFromCache | translationItemKey  is null"(){
	given:
	String inventoryItemId= "inventoryItemId_1"
	String inventoryDisplayName= "inventoryDisplayName"
	
	InventoryFeedVO inventoryFeedVO = new InventoryFeedVO()
	inventoryFeedVO.setInventoryID(inventoryItemId)
	inventoryFeedVO.setDisplayName(inventoryDisplayName)
	
	ItemDescriptorImpl inventoryDescriptor = Spy()
	inventoryDescriptor.setItemDescriptorName("inventoryDescriptor")
	
	RepositoryItemMock inventoryItem=Mock(["id":inventoryItemId])
	
	inventoryItem.getRepositoryId() >> inventoryItemId
	inventoryDescriptor.getRepositoryImpl().getItem(inventoryItemId) >> inventoryItem
	RepositoryImpl repositoryImplMock=Mock()
	inventoryDescriptor.setRepository(repositoryImplMock)
	
	
	String inventoryTranslationItemId= "inventoryTransItemId_1"
	ItemDescriptorImpl inventoryTranslationDescriptor = Mock()
	RepositoryItemMock inventoryTranslationItem=Mock(["id":inventoryTranslationItemId])
	inventoryTranslationItem.getRepositoryId()>> inventoryTranslationItemId
	inventoryTranslationDescriptor.getRepositoryImpl().getItem(inventoryTranslationItemId) >> inventoryTranslationItem
	
	Map<String,RepositoryItemMock> translationMap= new HashMap<String,RepositoryItemMock>()
	translationMap.put(null, inventoryTranslationItem)
	
	
	
	1* inventoryRepositoryMock.getItemDescriptor(BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >> inventoryDescriptor
	1* inventoryDescriptor.removeItemFromCache(inventoryItemId, true);
	1* inventoryDescriptor.getRepositoryImpl().getItem(inventoryItemId) >> inventoryItem
	2* inventoryItem.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> translationMap
	1* inventoryRepositoryMock.getItemDescriptor(BBBCatalogConstants.ITEM_TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> inventoryTranslationDescriptor
	1* testObj.logDebug("Removed inventory [" + inventoryItemId + " | " + inventoryDisplayName + "] from cache")
	
	expect:
	testObj.removeItemFromCache(inventoryFeedVO)
}
	
def "validate removeItemFromCache | handle RepositoryException"(){
	given:
	String inventoryItemId= "inventoryItemId_1"
	
	InventoryFeedVO inventoryFeedVO = new InventoryFeedVO()
	inventoryFeedVO.setInventoryID(inventoryItemId)
	
	ItemDescriptorImpl inventoryDescriptor = Mock()
	
	1* inventoryRepositoryMock.getItemDescriptor(BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >> { throw new RepositoryException("RepositoryException") }
	when:
	
	testObj.removeItemFromCache(inventoryFeedVO)
	
	then:
    BBBSystemException exception = thrown()
}

//----------------------------------------- updateInventoryFeed(final List<InventoryFeedVO> pInventoryFeed)  ---------------------------------------------------

def "validate updateInventoryFeed "(){
	given:
	String feedId="feedId_1"
	String feedStatus="feedId_Status_Open"
	InventoryFeedVO pInventoryFeed = new InventoryFeedVO()
	pInventoryFeed.setID(feedId)
	pInventoryFeed.setFeedStatus(feedStatus)
	List<InventoryFeedVO> inventoryFeedList=  new ArrayList<>()
	inventoryFeedList.add(pInventoryFeed)
	MutableRepository mutableRepositoryMocked=Mock()
	testObj.setInventoryRepository(mutableRepositoryMocked)
	MutableRepositoryItem mutableInventoryItemMock = Mock(["id":feedId])
	
	1* mutableRepositoryMocked.getItemForUpdate(pInventoryFeed.getID(), "inventoryFeed") >>mutableInventoryItemMock
	1* mutableInventoryItemMock.setPropertyValue("feedStatus", feedStatus);
	1* mutableInventoryItemMock.setPropertyValue("lastUpdated", _);
	1* mutableRepositoryMocked.updateItem(mutableInventoryItemMock)
	expect:
	testObj.updateInventoryFeed(inventoryFeedList)
	 
}

def "validate updateInventoryFeed | handle RepositoryException "(){
	given:
	String feedId="feedId_1"
	String feedStatus="feedId_Status_Open"
	InventoryFeedVO pInventoryFeed = new InventoryFeedVO()
	pInventoryFeed.setID(feedId)
	pInventoryFeed.setFeedStatus(feedStatus)
	List<InventoryFeedVO> inventoryFeedList=  new ArrayList<>()
	inventoryFeedList.add(pInventoryFeed)
	MutableRepository mutableRepositoryMocked=Mock()
	testObj.setInventoryRepository(mutableRepositoryMocked)
	MutableRepositoryItem mutableInventoryItemMock = Mock(["id":feedId])
	
	1* mutableRepositoryMocked.getItemForUpdate(pInventoryFeed.getID(), "inventoryFeed") >> { throw new RepositoryException("RepositoryException") }
	0* mutableInventoryItemMock.setPropertyValue("feedStatus", feedStatus);
	0* mutableInventoryItemMock.setPropertyValue("lastUpdated", _);
	0* mutableRepositoryMocked.updateItem(mutableInventoryItemMock)
	when:
	testObj.updateInventoryFeed(inventoryFeedList)
	then:
	BBBSystemException exception = thrown()
}
//------------------------------- getSKUInventory(final InventoryVO[] pInventory) --------------------------------------------
def "validate getSKUInventory "(){
	given:
	String skuId="inv_sku_id1"
	Long orderQuantity=22
	String siteId="BedBathUS"
	InventoryVO[] pInventory = new InventoryVO[1]
	InventoryVO inventoryVO = new InventoryVO()
	inventoryVO.setSkuID(skuId)
	inventoryVO.setSiteID(siteId)
	inventoryVO.setOrderedQuantity(orderQuantity)
	pInventory[0]= inventoryVO
	
	List<String> listOfSiteCode = new ArrayList<String>();
	listOfSiteCode.add("BedBathUS");
	RepositoryItem[] inventoryItems = new RepositoryItem[1]
	
	String inventoryItemId="invItemId_1"
	String globalStockLevel="100"
	String siteStockLevel="200"
	String regStockLevel="300"
	
	RepositoryItemMock inventoryItemMock=Mock(["id":inventoryItemId])
	1* inventoryItemMock.getRepositoryId() >> inventoryItemId
	1* inventoryItemMock.getPropertyValue(BBBCatalogConstants.STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> globalStockLevel
	1* inventoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> siteStockLevel
	1* inventoryItemMock.getPropertyValue(BBBCatalogConstants.REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> regStockLevel
	inventoryItems[0] = inventoryItemMock
	
	RqlStatement mockedStatement= Mock()
	RepositoryView inventoryRepositoryViewMock = Mock()
	 
	1* inventoryRepositoryMock.getView(BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >> inventoryRepositoryViewMock
	testObj.executeQuery(inventoryRepositoryViewMock, _,_) >> inventoryItems
	1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,"BedBathUSSiteCode") >> listOfSiteCode
	
	when:
	List<InventoryVO> inventoryVOs=testObj.getSKUInventory(pInventory)
	then:
	inventoryVOs.get(0).getInventoryID() == inventoryItemId
	inventoryVOs.get(0).getGlobalStockLevel().toString() == globalStockLevel
	inventoryVOs.get(0).getSiteStockLevel().toString() == siteStockLevel
	inventoryVOs.get(0).getGiftRegistryStockLevel().toString() == regStockLevel
	inventoryVOs.get(0).getSiteID() == siteId
	inventoryVOs.get(0).getSkuID() == skuId
	inventoryVOs.get(0).getOrderedQuantity() == orderQuantity
}

def "validate getSKUInventory | SiteId not equal to BedBathUSSiteCode "(){
	given:
	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathCA"
	InventoryVO[] pInventory = new InventoryVO[1]
	InventoryVO inventoryVO = new InventoryVO()
	inventoryVO.setSkuID(skuId)
	inventoryVO.setSiteID(siteId)
	inventoryVO.setOrderedQuantity(orderQuantity)
	pInventory[0]= inventoryVO
	
	List<String> listOfSiteCode = new ArrayList<String>();
	listOfSiteCode.add("BedBathUS");
	RepositoryItem[] inventoryItems = new RepositoryItem[1]
	
	String transitionsInventoryItemId="transitionsInvItemId_1"
	
	Long transitionsSiteStockLevel=201
	Long transitionsRegStockLevel=301
	RepositoryItemMock transitionsInventoryItemMock=Mock(["id":transitionsInventoryItemId])
	1* transitionsInventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> transitionsSiteStockLevel
	1* transitionsInventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> transitionsRegStockLevel
	Map<String,RepositoryItem> trans=new HashMap<String,RepositoryItem>()
	trans.put(siteId, transitionsInventoryItemMock)
	
	String inventoryItemId="invItemId_1"
	String globalStockLevel="100"
 
	RepositoryItemMock inventoryItemMock=Mock(["id":inventoryItemId])
	1* inventoryItemMock.getRepositoryId() >> inventoryItemId
	1* inventoryItemMock.getPropertyValue(BBBCatalogConstants.STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> globalStockLevel
	
	
	1* inventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> trans
	
	
	inventoryItems[0] = inventoryItemMock
	
	RqlStatement mockedStatement= Mock()
	RepositoryView inventoryRepositoryViewMock = Mock()
	 
	1* inventoryRepositoryMock.getView(BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >> inventoryRepositoryViewMock
	
	testObj.executeQuery(inventoryRepositoryViewMock, _,_) >> inventoryItems
	1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,"BedBathUSSiteCode") >> listOfSiteCode
	
	when:
	List<InventoryVO> inventoryVOs=testObj.getSKUInventory(pInventory)
	then:
	inventoryVOs.get(0).getInventoryID() == inventoryItemId
	inventoryVOs.get(0).getGlobalStockLevel().toString() == globalStockLevel
	inventoryVOs.get(0).getSiteStockLevel() == transitionsSiteStockLevel
	inventoryVOs.get(0).getGiftRegistryStockLevel() == transitionsRegStockLevel
	inventoryVOs.get(0).getSiteID() == siteId
	inventoryVOs.get(0).getSkuID() == skuId
	inventoryVOs.get(0).getOrderedQuantity() == orderQuantity
}

def "validate getSKUInventory | SiteId not equal to BedBathUSSiteCode and trans is Empty "(){
	given:
	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathCA"
	InventoryVO[] pInventory = new InventoryVO[1]
	InventoryVO inventoryVO = new InventoryVO()
	inventoryVO.setSkuID(skuId)
	inventoryVO.setSiteID(siteId)
	inventoryVO.setOrderedQuantity(orderQuantity)
	pInventory[0]= inventoryVO
	
	List<String> listOfSiteCode = new ArrayList<String>();
	listOfSiteCode.add("BedBathUS");
	RepositoryItem[] inventoryItems = new RepositoryItem[1]
	
	 
	Map<String,RepositoryItem> trans=new HashMap<String,RepositoryItem>()
	 
	
	String inventoryItemId="invItemId_1"
	String globalStockLevel="100"
 
	RepositoryItemMock inventoryItemMock=Mock(["id":inventoryItemId])
	1* inventoryItemMock.getRepositoryId() >> inventoryItemId
	1* inventoryItemMock.getPropertyValue(BBBCatalogConstants.STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> globalStockLevel
	
	
	1* inventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> trans
	
	
	inventoryItems[0] = inventoryItemMock
	
	RqlStatement mockedStatement= Mock()
	RepositoryView inventoryRepositoryViewMock = Mock()
	 
	1* inventoryRepositoryMock.getView(BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >> inventoryRepositoryViewMock
	
	testObj.executeQuery(inventoryRepositoryViewMock, _,_) >> inventoryItems
	1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,"BedBathUSSiteCode") >> listOfSiteCode
	
	when:
	List<InventoryVO> inventoryVOs=testObj.getSKUInventory(pInventory)
	then:
	inventoryVOs.get(0).getInventoryID() == inventoryItemId
	inventoryVOs.get(0).getGlobalStockLevel().toString() == globalStockLevel
	inventoryVOs.get(0).getSiteStockLevel() == 0
	inventoryVOs.get(0).getGiftRegistryStockLevel() == 0
	inventoryVOs.get(0).getSiteID() == siteId
	inventoryVOs.get(0).getSkuID() == skuId
	inventoryVOs.get(0).getOrderedQuantity() == orderQuantity
}

def "validate getSKUInventory | SiteId not equal to BedBathUSSiteCode and trans is null "(){
	given:
	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathCA"
	InventoryVO[] pInventory = new InventoryVO[1]
	InventoryVO inventoryVO = new InventoryVO()
	inventoryVO.setSkuID(skuId)
	inventoryVO.setSiteID(siteId)
	inventoryVO.setOrderedQuantity(orderQuantity)
	pInventory[0]= inventoryVO
	
	List<String> listOfSiteCode = new ArrayList<String>();
	listOfSiteCode.add("BedBathUS");
	RepositoryItem[] inventoryItems = new RepositoryItem[1]
	
	
	Map<String,RepositoryItem> trans=null
	 
	
	String inventoryItemId="invItemId_1"
	String globalStockLevel="100"
 
	RepositoryItemMock inventoryItemMock=Mock(["id":inventoryItemId])
	1* inventoryItemMock.getRepositoryId() >> inventoryItemId
	1* inventoryItemMock.getPropertyValue(BBBCatalogConstants.STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> globalStockLevel
	
	
	1* inventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> trans
	
	
	inventoryItems[0] = inventoryItemMock
	
	RqlStatement mockedStatement= Mock()
	RepositoryView inventoryRepositoryViewMock = Mock()
	 
	1* inventoryRepositoryMock.getView(BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >> inventoryRepositoryViewMock
	
	testObj.executeQuery(inventoryRepositoryViewMock, _,_) >> inventoryItems
	1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,"BedBathUSSiteCode") >> listOfSiteCode
	
	when:
	List<InventoryVO> inventoryVOs=testObj.getSKUInventory(pInventory)
	then:
	inventoryVOs.get(0).getInventoryID() == inventoryItemId
	inventoryVOs.get(0).getGlobalStockLevel().toString() == globalStockLevel
	inventoryVOs.get(0).getSiteStockLevel() == 0
	inventoryVOs.get(0).getGiftRegistryStockLevel() == 0
	inventoryVOs.get(0).getSiteID() == siteId
	inventoryVOs.get(0).getSkuID() == skuId
	inventoryVOs.get(0).getOrderedQuantity() == orderQuantity
}
def "validate getSKUInventory | handle RepositoryException "(){
	given:
	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathCA"
	InventoryVO[] pInventory = new InventoryVO[1]
	InventoryVO inventoryVO = new InventoryVO()
	inventoryVO.setSkuID(skuId)
	inventoryVO.setSiteID(siteId)
	inventoryVO.setOrderedQuantity(orderQuantity)
	pInventory[0]= inventoryVO
	
1* inventoryRepositoryMock.getView(BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR)    >> { throw new RepositoryException("RepositoryException") }
	
	
	when:
	List<InventoryVO> inventoryVOs=testObj.getSKUInventory(pInventory)
	then:
	BBBSystemException exception = thrown()
}

def "validate getSKUInventory | handle BBBBusinessException "(){
	given:
	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathCA"
	InventoryVO[] pInventory = new InventoryVO[1]
	InventoryVO inventoryVO = new InventoryVO()
	inventoryVO.setSkuID(skuId)
	inventoryVO.setSiteID(siteId)
	inventoryVO.setOrderedQuantity(orderQuantity)
	pInventory[0]= inventoryVO
	
	List<String> listOfSiteCode = new ArrayList<String>();
	listOfSiteCode.add("BedBathUS");
	RepositoryItem[] inventoryItems = new RepositoryItem[1]
	
	String transitionsInventoryItemId="transitionsInvItemId_1"
	
	Long transitionsSiteStockLevel=201
	Long transitionsRegStockLevel=301
	RepositoryItemMock transitionsInventoryItemMock=Mock(["id":transitionsInventoryItemId])
	0* transitionsInventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> transitionsSiteStockLevel
	0* transitionsInventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> transitionsRegStockLevel
	Map<String,RepositoryItem> trans=new HashMap<String,RepositoryItem>()
	trans.put(siteId, transitionsInventoryItemMock)
	
	String inventoryItemId="invItemId_1"
	String globalStockLevel="100"
 
	RepositoryItemMock inventoryItemMock=Mock(["id":inventoryItemId])
	0* inventoryItemMock.getRepositoryId() >> inventoryItemId
	1* inventoryItemMock.getPropertyValue(BBBCatalogConstants.STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> globalStockLevel
	
	
	0* inventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> trans
	
	
	inventoryItems[0] = inventoryItemMock
	
	RqlStatement mockedStatement= Mock()
	RepositoryView inventoryRepositoryViewMock = Mock()
	 
	1* inventoryRepositoryMock.getView(BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >> inventoryRepositoryViewMock
	
	testObj.executeQuery(inventoryRepositoryViewMock, _,_) >> inventoryItems
	1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,"BedBathUSSiteCode")    >> { throw new BBBBusinessException("BBBBusinessException") }
	1* testObj.logError("Some Business Exception", _);
	when:
	List<InventoryVO> inventoryVOs=testObj.getSKUInventory(pInventory)
	then:
	inventoryVOs.size() == 0
}

//----------------------        updateSKUInventory(final List<InventoryVO> pInventory ----------------------------------------------------
def "validate updateSKUInventory | Site BedBathCA"(){
	given:
	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathCA"
	String inventoryID="inventoryID_1"
	String inventoryRepositoryId="repositoryId_1"
	String inventoryTransaltionID="inventoryTransaltionID_1"
	
	Long siteStockLevel= 11
	Long giftRegistryStockLevel=22
	Long globalStockLevel=56
	List<InventoryVO>  inventoryVoList = new ArrayList<InventoryVO>()
	InventoryVO inventoryVO = new InventoryVO()
	inventoryVO.setSiteID(siteId)
	inventoryVO.setInventoryID(inventoryID)
	inventoryVO.setSiteStockLevel(siteStockLevel)
	inventoryVO.setGiftRegistryStockLevel(giftRegistryStockLevel)
	inventoryVO.setSkuID(skuId)
	inventoryVO.setGlobalStockLevel(globalStockLevel) 
	 
	
	inventoryVoList.add(inventoryVO)
	
	List<String> listOfSiteCode = new ArrayList<String>();
	listOfSiteCode.add("BedBathUS");
	
	MutableRepository mutableRepositoryMocked=Mock()
	testObj.setInventoryRepository(mutableRepositoryMocked)
	
	MutableRepositoryItem mutableInventoryTranslationItemMock = Mock(["id":inventoryTransaltionID])
	Map<String,MutableRepositoryItem> translations=new HashMap<String,MutableRepositoryItem>()
	translations.put(siteId, mutableInventoryTranslationItemMock)
	LockReleaser lockReleaserMock=Mock()
	 
	
	MutableRepositoryItem mutableInventoryItemMock = Mock(["id":inventoryRepositoryId])
	2* mutableInventoryItemMock.getRepositoryId() >> inventoryRepositoryId
	1* mutableRepositoryMocked.getItemForUpdate(inventoryVO.getInventoryID(), BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >>mutableInventoryItemMock
	1* mutableInventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> translations
	1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathUSSiteCode") >> listOfSiteCode
	
	1* mutableRepositoryMocked.updateItem(mutableInventoryItemMock)
	1* testObj.logDebug("Site ID:--->" + inventoryVO.getSiteID())
	1* testObj.logDebug("Global Stock Level(AFS)=" + globalStockLevel + "|| Site Specific Stock Level(ALT_AFS)="
		+ siteStockLevel + "|| Gift Registry Stock Level(IGR)=" + giftRegistryStockLevel);
	testObj.setTransactionManager(transactionManagerMock)
	def Transaction trans = Mock()
	transactionManagerMock.getTransaction() >> trans
	expect:
	testObj.updateSKUInventory(inventoryVoList)
	 
}

def "validate updateSKUInventory | Site BedBathUS"(){
	given:
	testObj.setTransactionManager(transactionManagerMock)
	def Transaction trans = Mock()
	transactionManagerMock.getTransaction() >> trans

	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathUS"
	String inventoryID="inventoryID_1"
	String inventoryRepositoryId="repositoryId_1"
	String inventoryTransaltionID="inventoryTransaltionID_1"
	
	Long siteStockLevel= 11
	Long giftRegistryStockLevel=22
	Long globalStockLevel=56
	List<InventoryVO>  inventoryVoList = new ArrayList<InventoryVO>()
	InventoryVO inventoryVO = new InventoryVO()
	inventoryVO.setSiteID(siteId)
	inventoryVO.setInventoryID(inventoryID)
	inventoryVO.setSiteStockLevel(siteStockLevel)
	inventoryVO.setGiftRegistryStockLevel(giftRegistryStockLevel)
	inventoryVO.setSkuID(skuId)
	inventoryVO.setGlobalStockLevel(globalStockLevel)
	 
	
	inventoryVoList.add(inventoryVO)
	
	List<String> listOfSiteCode = new ArrayList<String>();
	listOfSiteCode.add("BedBathUS");
	
	MutableRepository mutableRepositoryMocked=Mock()
	testObj.setInventoryRepository(mutableRepositoryMocked)
	
	MutableRepositoryItem mutableInventoryTranslationItemMock = Mock(["id":inventoryTransaltionID])
	Map<String,MutableRepositoryItem> translations=new HashMap<String,MutableRepositoryItem>()
	translations.put(siteId, mutableInventoryTranslationItemMock)
	LockReleaser lockReleaserMock=Mock()
	 
	
	MutableRepositoryItem mutableInventoryItemMock = Mock(["id":inventoryRepositoryId])
	1* mutableInventoryItemMock.getRepositoryId() >> inventoryRepositoryId
	1* mutableRepositoryMocked.getItemForUpdate(inventoryVO.getInventoryID(), BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >>mutableInventoryItemMock
	0* mutableInventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> translations
	1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathUSSiteCode") >> listOfSiteCode
	
	1* mutableRepositoryMocked.updateItem(mutableInventoryItemMock)
	1* testObj.logDebug("Site ID:--->" + inventoryVO.getSiteID())
	1* testObj.logDebug("Global Stock Level(AFS)=" + globalStockLevel + "|| Site Specific Stock Level(ALT_AFS)="		+ siteStockLevel + "|| Gift Registry Stock Level(IGR)=" + giftRegistryStockLevel);
	
	expect:
	testObj.updateSKUInventory(inventoryVoList)
	 
}

def "validate updateSKUInventory | handle TransactionDemarcationException"(){
	given:
	testObj.setTransactionManager(null)

	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathUS"
	String inventoryID="inventoryID_1"
	String inventoryRepositoryId="repositoryId_1"
	String inventoryTransaltionID="inventoryTransaltionID_1"
	
	Long siteStockLevel= 11
	Long giftRegistryStockLevel=22
	Long globalStockLevel=56
	List<InventoryVO>  inventoryVoList = new ArrayList<InventoryVO>()
	InventoryVO inventoryVO = new InventoryVO()
	inventoryVO.setSiteID(siteId)
	inventoryVO.setInventoryID(inventoryID)
	inventoryVO.setSiteStockLevel(siteStockLevel)
	inventoryVO.setGiftRegistryStockLevel(giftRegistryStockLevel)
	inventoryVO.setSkuID(skuId)
	inventoryVO.setGlobalStockLevel(globalStockLevel)
	 
	
	inventoryVoList.add(inventoryVO)
	
	List<String> listOfSiteCode = new ArrayList<String>();
	listOfSiteCode.add("BedBathUS");
	
	MutableRepository mutableRepositoryMocked=Mock()
	testObj.setInventoryRepository(mutableRepositoryMocked)
	
	MutableRepositoryItem mutableInventoryTranslationItemMock = Mock(["id":inventoryTransaltionID])
	Map<String,MutableRepositoryItem> translations=new HashMap<String,MutableRepositoryItem>()
	translations.put(siteId, mutableInventoryTranslationItemMock)
	TransactionDemarcation transactionDemarcationMock=Mock()
	LockReleaser lockReleaserMock=Mock()
	
	
	MutableRepositoryItem mutableInventoryItemMock = Mock(["id":inventoryRepositoryId])
	1* mutableInventoryItemMock.getRepositoryId() >> inventoryRepositoryId
	1* mutableRepositoryMocked.getItemForUpdate(inventoryVO.getInventoryID(), BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >>mutableInventoryItemMock
	0* mutableInventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> translations
	1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathUSSiteCode") >> listOfSiteCode
	
	0* mutableRepositoryMocked.updateItem(mutableInventoryItemMock)
	1* testObj.logDebug("Site ID:--->" + inventoryVO.getSiteID())
	1* testObj.logDebug("Global Stock Level(AFS)=" + globalStockLevel + "|| Site Specific Stock Level(ALT_AFS)="+ siteStockLevel + "|| Gift Registry Stock Level(IGR)=" + giftRegistryStockLevel);
	1* testObj.logError("TransactionDemarcationException occured in the beginning while updating online inventory " + inventoryVO.getSkuID(), _);
	expect:
	testObj.updateSKUInventory(inventoryVoList)
}

def "validate updateSKUInventory | handle DeadlockException"(){
	given:
	testObj.setTransactionManager(transactionManagerMock)
	def Transaction trans = Mock()
	transactionManagerMock.getTransaction() >> trans

	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathUS"
	String inventoryID="inventoryID_1"
	String inventoryRepositoryId="repositoryId_1"
	String inventoryTransaltionID="inventoryTransaltionID_1"
	
	Long siteStockLevel= 11
	Long giftRegistryStockLevel=22
	Long globalStockLevel=56
	List<InventoryVO>  inventoryVoList = new ArrayList<InventoryVO>()
	InventoryVO inventoryVO = new InventoryVO()
	inventoryVO.setSiteID(siteId)
	inventoryVO.setInventoryID(inventoryID)
	inventoryVO.setSiteStockLevel(siteStockLevel)
	inventoryVO.setGiftRegistryStockLevel(giftRegistryStockLevel)
	inventoryVO.setSkuID(skuId)
	inventoryVO.setGlobalStockLevel(globalStockLevel)
	 
	
	inventoryVoList.add(inventoryVO)
	
	List<String> listOfSiteCode = new ArrayList<String>();
	listOfSiteCode.add("BedBathUS");
	
	MutableRepository mutableRepositoryMocked=Mock()
	testObj.setInventoryRepository(mutableRepositoryMocked)
	
	MutableRepositoryItem mutableInventoryTranslationItemMock = Mock(["id":inventoryTransaltionID])
	Map<String,MutableRepositoryItem> translations=new HashMap<String,MutableRepositoryItem>()
	translations.put(siteId, mutableInventoryTranslationItemMock)
	TransactionDemarcation transactionDemarcationMock=Mock()
	LockReleaser lockReleaserMock=Mock()
	
	testObj.setTransactionManager(transactionManagerMock)
	 
	
	MutableRepositoryItem mutableInventoryItemMock = Mock(["id":inventoryRepositoryId])
	1* mutableInventoryItemMock.getRepositoryId() >> inventoryRepositoryId
	1* mutableRepositoryMocked.getItemForUpdate(inventoryVO.getInventoryID(), BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >>mutableInventoryItemMock
	0* mutableInventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> translations
	1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathUSSiteCode") >> listOfSiteCode
	
	
	1* testObj.getClientLockManager().acquireWriteLock(inventoryRepositoryId+siteId) >>  { throw new DeadlockException("DeadlockException") }
	0* mutableRepositoryMocked.updateItem(mutableInventoryItemMock)
	1* testObj.logDebug("Site ID:--->" + inventoryVO.getSiteID())
	1* testObj.logDebug("Global Stock Level(AFS)=" + globalStockLevel + "|| Site Specific Stock Level(ALT_AFS)="+ siteStockLevel + "|| Gift Registry Stock Level(IGR)=" + giftRegistryStockLevel);
	1* testObj.logError("Deadlock Exception add Mando inventory update failed for sku " + inventoryVO.getSkuID(), _);
	expect:
	testObj.updateSKUInventory(inventoryVoList)
}


def "validate updateSKUInventory | handle LockManagerException"(){
	given:
	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathUS"
	String inventoryID="inventoryID_1"
	String inventoryRepositoryId="repositoryId_1"
	String inventoryTransaltionID="inventoryTransaltionID_1"
	
	Long siteStockLevel= 11
	Long giftRegistryStockLevel=22
	Long globalStockLevel=56
	List<InventoryVO>  inventoryVoList = new ArrayList<InventoryVO>()
	InventoryVO inventoryVO = new InventoryVO()
	inventoryVO.setSiteID(siteId)
	inventoryVO.setInventoryID(inventoryID)
	inventoryVO.setSiteStockLevel(siteStockLevel)
	inventoryVO.setGiftRegistryStockLevel(giftRegistryStockLevel)
	inventoryVO.setSkuID(skuId)
	inventoryVO.setGlobalStockLevel(globalStockLevel)
	 
	
	inventoryVoList.add(inventoryVO)
	
	List<String> listOfSiteCode = new ArrayList<String>();
	listOfSiteCode.add("BedBathUS");
	
	MutableRepository mutableRepositoryMocked=Mock()
	testObj.setInventoryRepository(mutableRepositoryMocked)
	
	MutableRepositoryItem mutableInventoryTranslationItemMock = Mock(["id":inventoryTransaltionID])
	Map<String,MutableRepositoryItem> translations=new HashMap<String,MutableRepositoryItem>()
	translations.put(siteId, mutableInventoryTranslationItemMock)
	TransactionDemarcation transactionDemarcationMock=Mock()
	LockReleaser lockReleaserMock=Mock()
	
	testObj.setTransactionManager(transactionManagerMock)
	 
	
	MutableRepositoryItem mutableInventoryItemMock = Mock(["id":inventoryRepositoryId])
	1* mutableInventoryItemMock.getRepositoryId() >> inventoryRepositoryId
	1* mutableRepositoryMocked.getItemForUpdate(inventoryVO.getInventoryID(), BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >>mutableInventoryItemMock
	0* mutableInventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> translations
	1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathUSSiteCode") >> listOfSiteCode
	
	
	1* testObj.getClientLockManager().acquireWriteLock(inventoryRepositoryId+siteId)
	
	0* mutableRepositoryMocked.updateItem(mutableInventoryItemMock)
	1* testObj.logDebug("Site ID:--->" + inventoryVO.getSiteID())
	1* testObj.logDebug("Global Stock Level(AFS)=" + globalStockLevel + "|| Site Specific Stock Level(ALT_AFS)="+ siteStockLevel + "|| Gift Registry Stock Level(IGR)=" + giftRegistryStockLevel);
	1* testObj.logError("Lockmanager Exception and Mando inventory update failed for sku " + inventoryVO.getSkuID(), _);
	expect:
	testObj.updateSKUInventory(inventoryVoList)
}

def "validate updateSKUInventory | handle Exception "(){
	given:
	testObj.setTransactionManager(transactionManagerMock)
	def Transaction trans = Mock()
	transactionManagerMock.getTransaction() >> trans
	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathUS"
	String inventoryID="inventoryID_1"
	String inventoryRepositoryId="repositoryId_1"
	String inventoryTransaltionID="inventoryTransaltionID_1"
	
	Long siteStockLevel= 11
	Long giftRegistryStockLevel=22
	Long globalStockLevel=56
	List<InventoryVO>  inventoryVoList = new ArrayList<InventoryVO>()
	InventoryVO inventoryVO = new InventoryVO()
	inventoryVO.setSiteID(siteId)
	inventoryVO.setInventoryID(inventoryID)
	inventoryVO.setSiteStockLevel(siteStockLevel)
	inventoryVO.setGiftRegistryStockLevel(giftRegistryStockLevel)
	inventoryVO.setSkuID(skuId)
	inventoryVO.setGlobalStockLevel(globalStockLevel)
	 
	
	inventoryVoList.add(inventoryVO)
	
	List<String> listOfSiteCode = new ArrayList<String>();
	listOfSiteCode.add("BedBathUS");
	
	MutableRepository mutableRepositoryMocked=Mock()
	testObj.setInventoryRepository(mutableRepositoryMocked)
	
	MutableRepositoryItem mutableInventoryTranslationItemMock = Mock(["id":inventoryTransaltionID])
	Map<String,MutableRepositoryItem> translations=new HashMap<String,MutableRepositoryItem>()
	translations.put(siteId, mutableInventoryTranslationItemMock)
	TransactionDemarcation transactionDemarcationMock=Mock()
	LockReleaser lockReleaserMock=Mock()
	
	testObj.setTransactionManager(transactionManagerMock)
	 
	
	MutableRepositoryItem mutableInventoryItemMock = Mock(["id":inventoryRepositoryId])
	1* mutableInventoryItemMock.getRepositoryId() >> inventoryRepositoryId
	1* mutableRepositoryMocked.getItemForUpdate(inventoryVO.getInventoryID(), BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >>mutableInventoryItemMock
	0* mutableInventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> translations
	1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathUSSiteCode") >> listOfSiteCode
	
	
	1* testObj.getClientLockManager().acquireWriteLock(inventoryRepositoryId+siteId)
	
	1* mutableRepositoryMocked.updateItem(mutableInventoryItemMock) >> { throw new RepositoryException("RepositoryException") }
	1* testObj.logDebug("Site ID:--->" + inventoryVO.getSiteID())
	1* testObj.logDebug("Global Stock Level(AFS)=" + globalStockLevel + "|| Site Specific Stock Level(ALT_AFS)="+ siteStockLevel + "|| Gift Registry Stock Level(IGR)=" + giftRegistryStockLevel);
	1* testObj.logError("System Exception and Mando inventory update failed for sku " + inventoryVO.getSkuID(), _);
	expect:
	testObj.updateSKUInventory(inventoryVoList)
}

def "validate updateSKUInventory | handle TransactionDemarcationException  during ending transaction "(){
	given:
	testObj.setTransactionManager(transactionManagerMock)
	def Transaction trans = Mock()
	transactionManagerMock.getTransaction() >> trans >> trans >>trans >> trans >>{ throw new TransactionDemarcationException("TransactionDemarcationException") } 
	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathUS"
	String inventoryID="inventoryID_1"
	String inventoryRepositoryId="repositoryId_1"
	String inventoryTransaltionID="inventoryTransaltionID_1"
	
	Long siteStockLevel= 11
	Long giftRegistryStockLevel=22
	Long globalStockLevel=56
	List<InventoryVO>  inventoryVoList = new ArrayList<InventoryVO>()
	InventoryVO inventoryVO = new InventoryVO()
	inventoryVO.setSiteID(siteId)
	inventoryVO.setInventoryID(inventoryID)
	inventoryVO.setSiteStockLevel(siteStockLevel)
	inventoryVO.setGiftRegistryStockLevel(giftRegistryStockLevel)
	inventoryVO.setSkuID(skuId)
	inventoryVO.setGlobalStockLevel(globalStockLevel)
	 
	
	inventoryVoList.add(inventoryVO)
	
	List<String> listOfSiteCode = new ArrayList<String>();
	listOfSiteCode.add("BedBathUS");
	
	MutableRepository mutableRepositoryMocked=Mock()
	testObj.setInventoryRepository(mutableRepositoryMocked)
	
	MutableRepositoryItem mutableInventoryTranslationItemMock = Mock(["id":inventoryTransaltionID])
	Map<String,MutableRepositoryItem> translations=new HashMap<String,MutableRepositoryItem>()
	translations.put(siteId, mutableInventoryTranslationItemMock)
	TransactionDemarcation transactionDemarcationMock=Mock()
	LockReleaser lockReleaserMock=Mock()
	
	testObj.setTransactionManager(transactionManagerMock)
	 
	
	MutableRepositoryItem mutableInventoryItemMock = Mock(["id":inventoryRepositoryId])
	1* mutableInventoryItemMock.getRepositoryId() >> inventoryRepositoryId
	1* mutableRepositoryMocked.getItemForUpdate(inventoryVO.getInventoryID(), BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >>mutableInventoryItemMock
	0* mutableInventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> translations
	1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathUSSiteCode") >> listOfSiteCode
	
	
	1* testObj.getClientLockManager().acquireWriteLock(inventoryRepositoryId+siteId)
	
	1* mutableRepositoryMocked.updateItem(mutableInventoryItemMock) 
	1* testObj.logDebug("Site ID:--->" + inventoryVO.getSiteID())
	1* testObj.logDebug("Global Stock Level(AFS)=" + globalStockLevel + "|| Site Specific Stock Level(ALT_AFS)="+ siteStockLevel + "|| Gift Registry Stock Level(IGR)=" + giftRegistryStockLevel);
	
	1* testObj.logError("TransactionDemarcationException occured at the end while updating Mando inventory one of the sku" , _);
	expect:
	testObj.updateSKUInventory(inventoryVoList)
}


def "validate updateSKUInventory | handle RepositoryException "(){
	given:
	
	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathUS"
	String inventoryID="inventoryID_1"
	String inventoryRepositoryId="repositoryId_1"
	String inventoryTransaltionID="inventoryTransaltionID_1"
	
	Long siteStockLevel= 11
	Long giftRegistryStockLevel=22
	Long globalStockLevel=56
	List<InventoryVO>  inventoryVoList = new ArrayList<InventoryVO>()
	InventoryVO inventoryVO = new InventoryVO()
	inventoryVO.setSiteID(siteId)
	inventoryVO.setInventoryID(inventoryID)
	inventoryVO.setSiteStockLevel(siteStockLevel)
	inventoryVO.setGiftRegistryStockLevel(giftRegistryStockLevel)
	inventoryVO.setSkuID(skuId)
	inventoryVO.setGlobalStockLevel(globalStockLevel)
	 
	
	inventoryVoList.add(inventoryVO)
	
	List<String> listOfSiteCode = new ArrayList<String>();
	listOfSiteCode.add("BedBathUS");
	
	MutableRepository mutableRepositoryMocked=Mock()
	testObj.setInventoryRepository(mutableRepositoryMocked)
	
	MutableRepositoryItem mutableInventoryTranslationItemMock = Mock(["id":inventoryTransaltionID])
	Map<String,MutableRepositoryItem> translations=new HashMap<String,MutableRepositoryItem>()
	translations.put(siteId, mutableInventoryTranslationItemMock)
	TransactionDemarcation transactionDemarcationMock=Mock()
	LockReleaser lockReleaserMock=Mock()
	
	testObj.setTransactionManager(transactionManagerMock)
	 
	
	MutableRepositoryItem mutableInventoryItemMock = Mock(["id":inventoryRepositoryId])

	1* mutableRepositoryMocked.getItemForUpdate(inventoryVO.getInventoryID(), BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >>  { throw new RepositoryException("RepositoryException") }
	1* testObj.logError("Error while updating inventory [" + inventoryVO.getSkuID() + "]", _);
	expect:
	testObj.updateSKUInventory(inventoryVoList)
}

def "validate updateSKUInventory | handle BBBBusinessException "(){
	given:
	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathUS"
	String inventoryID="inventoryID_1"
	String inventoryRepositoryId="repositoryId_1"
	String inventoryTransaltionID="inventoryTransaltionID_1"
	
	Long siteStockLevel= 11
	Long giftRegistryStockLevel=22
	Long globalStockLevel=56
	List<InventoryVO>  inventoryVoList = new ArrayList<InventoryVO>()
	InventoryVO inventoryVO = new InventoryVO()
	inventoryVO.setSiteID(siteId)
	inventoryVO.setInventoryID(inventoryID)
	inventoryVO.setSiteStockLevel(siteStockLevel)
	inventoryVO.setGiftRegistryStockLevel(giftRegistryStockLevel)
	inventoryVO.setSkuID(skuId)
	inventoryVO.setGlobalStockLevel(globalStockLevel)
	 
	
	inventoryVoList.add(inventoryVO)
	
	List<String> listOfSiteCode = new ArrayList<String>();
	listOfSiteCode.add("BedBathUS");
	
	MutableRepository mutableRepositoryMocked=Mock()
	testObj.setInventoryRepository(mutableRepositoryMocked)
	
	MutableRepositoryItem mutableInventoryTranslationItemMock = Mock(["id":inventoryTransaltionID])
	Map<String,MutableRepositoryItem> translations=new HashMap<String,MutableRepositoryItem>()
	translations.put(siteId, mutableInventoryTranslationItemMock)
	TransactionDemarcation transactionDemarcationMock=Mock()
	LockReleaser lockReleaserMock=Mock()
	
	testObj.setTransactionManager(transactionManagerMock)
	 
	
	MutableRepositoryItem mutableInventoryItemMock = Mock(["id":inventoryRepositoryId])

	 
	 
	1* mutableRepositoryMocked.getItemForUpdate(inventoryVO.getInventoryID(), BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >>mutableInventoryItemMock
	
	1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathUSSiteCode") >>   { throw new BBBBusinessException("BBBBusinessException") }
	1* testObj.logError("Some Business Exception", _);
	expect:
	testObj.updateSKUInventory(inventoryVoList)
}

def "validate updateSKUInventory | inventoryVoList is empty "(){
	given:
	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathUS"
	String inventoryID="inventoryID_1"
	String inventoryRepositoryId="repositoryId_1"
	String inventoryTransaltionID="inventoryTransaltionID_1"
	
	Long siteStockLevel= 11
	Long giftRegistryStockLevel=22
	Long globalStockLevel=56
	List<InventoryVO>  inventoryVoList =new ArrayList<InventoryVO>()
	MutableRepository mutableRepositoryMocked=Mock()
	testObj.setInventoryRepository(mutableRepositoryMocked)
	expect:
	testObj.updateSKUInventory(inventoryVoList)
}



//----------------------        updateSKUInventoryForAllSites(final List<InventoryVO> pInventory ----------------------------------------------------
def "validate updateSKUInventoryForAllSites | Site BedBathCA"(){
	given:
	testObj.setTransactionManager(transactionManagerMock)
	def Transaction trans = Mock()
	transactionManagerMock.getTransaction() >> trans
	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathCA"
	String inventoryID="inventoryID_1"
	String inventoryRepositoryId="repositoryId_1"
	String inventoryTransaltionID="inventoryTransaltionID_1"
	
	Long siteStockLevel= 11
	Long giftRegistryStockLevel=22
	Long globalStockLevel=56
	List<InventoryVO>  inventoryVoList = new ArrayList<InventoryVO>()
	InventoryVO inventoryVO = new InventoryVO()
	inventoryVO.setSiteID(siteId)
	inventoryVO.setInventoryID(inventoryID)
	inventoryVO.setSiteStockLevel(siteStockLevel)
	inventoryVO.setGiftRegistryStockLevel(giftRegistryStockLevel)
	inventoryVO.setSkuID(skuId)
	inventoryVO.setGlobalStockLevel(globalStockLevel)
	 
	
	inventoryVoList.add(inventoryVO)
	
	List<String> listOfSiteCode = new ArrayList<String>();
	listOfSiteCode.add("BedBathUS");
	
	MutableRepository mutableRepositoryMocked=Mock()
	testObj.setInventoryRepository(mutableRepositoryMocked)
	
	MutableRepositoryItem mutableInventoryTranslationItemMock = Mock(["id":inventoryTransaltionID])
	Map<String,MutableRepositoryItem> translations=new HashMap<String,MutableRepositoryItem>()
	translations.put(siteId, mutableInventoryTranslationItemMock)
	LockReleaser lockReleaserMock=Mock()
	 
	
	MutableRepositoryItem mutableInventoryItemMock = Mock(["id":inventoryRepositoryId])
	mutableInventoryItemMock.getRepositoryId() >> inventoryRepositoryId
	1* mutableRepositoryMocked.getItemForUpdate(inventoryVO.getInventoryID(), BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >>mutableInventoryItemMock
	0* mutableInventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> translations
	1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathUSSiteCode") >> listOfSiteCode
	1* mutableRepositoryMocked.updateItem(mutableInventoryItemMock)
	1* testObj.logDebug("Site ID:--->" + inventoryVO.getSiteID())
	1* testObj.logDebug("Global Stock Level(AFS)=" + globalStockLevel + "|| Site Specific Stock Level(ALT_AFS)="
		+ siteStockLevel + "|| Gift Registry Stock Level(IGR)=" + giftRegistryStockLevel);
	expect:
	testObj.updateSKUInventoryForAllSites(inventoryVoList)
	 
}

def "validate updateSKUInventoryForAllSites | Site BedBathUS"(){
	given:
	testObj.setTransactionManager(transactionManagerMock)
	def Transaction trans = Mock()
	transactionManagerMock.getTransaction() >> trans
	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathUS"
	String inventoryID="inventoryID_1"
	String inventoryRepositoryId="repositoryId_1"
	String inventoryTransaltionID="inventoryTransaltionID_1"
	
	Long siteStockLevel= 11
	Long giftRegistryStockLevel=22
	Long globalStockLevel=56
	List<InventoryVO>  inventoryVoList = new ArrayList<InventoryVO>()
	InventoryVO inventoryVO = new InventoryVO()
	inventoryVO.setSiteID(siteId)
	inventoryVO.setInventoryID(inventoryID)
	inventoryVO.setSiteStockLevel(siteStockLevel)
	inventoryVO.setGiftRegistryStockLevel(giftRegistryStockLevel)
	inventoryVO.setSkuID(skuId)
	inventoryVO.setGlobalStockLevel(globalStockLevel)
	 
	
	inventoryVoList.add(inventoryVO)
	
	List<String> listOfSiteCode = new ArrayList<String>();
	listOfSiteCode.add("BedBathUS");
	
	MutableRepository mutableRepositoryMocked=Mock()
	testObj.setInventoryRepository(mutableRepositoryMocked)
	
	MutableRepositoryItem mutableInventoryTranslationItemMock = Mock(["id":inventoryTransaltionID])
	Map<String,MutableRepositoryItem> translations=new HashMap<String,MutableRepositoryItem>()
	translations.put(siteId, mutableInventoryTranslationItemMock)
	LockReleaser lockReleaserMock=Mock()
	 
	
	MutableRepositoryItem mutableInventoryItemMock = Mock(["id":inventoryRepositoryId])
	mutableInventoryItemMock.getRepositoryId() >> inventoryRepositoryId
	1* mutableRepositoryMocked.getItemForUpdate(inventoryVO.getInventoryID(), BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >>mutableInventoryItemMock
	0* mutableInventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> translations
	1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathUSSiteCode") >> listOfSiteCode
	
	1* mutableRepositoryMocked.updateItem(mutableInventoryItemMock)
	1* testObj.logDebug("Site ID:--->" + inventoryVO.getSiteID())
	1* testObj.logDebug("Global Stock Level(AFS)=" + globalStockLevel + "|| Site Specific Stock Level(ALT_AFS)="		+ siteStockLevel + "|| Gift Registry Stock Level(IGR)=" + giftRegistryStockLevel);
	
	expect:
	testObj.updateSKUInventoryForAllSites(inventoryVoList)
	 
}

def "validate updateSKUInventoryForAllSites | handle TransactionDemarcationException"(){
	given:
	testObj.setTransactionManager(transactionManagerMock)
	def Transaction trans = Mock()
	transactionManagerMock.getTransaction() >> { throw new TransactionDemarcationException("TransactionDemarcationException") }
	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathUS"
	String inventoryID="inventoryID_1"
	String inventoryRepositoryId="repositoryId_1"
	String inventoryTransaltionID="inventoryTransaltionID_1"
	
	Long siteStockLevel= 11
	Long giftRegistryStockLevel=22
	Long globalStockLevel=56
	List<InventoryVO>  inventoryVoList = new ArrayList<InventoryVO>()
	InventoryVO inventoryVO = new InventoryVO()
	inventoryVO.setSiteID(siteId)
	inventoryVO.setInventoryID(inventoryID)
	inventoryVO.setSiteStockLevel(siteStockLevel)
	inventoryVO.setGiftRegistryStockLevel(giftRegistryStockLevel)
	inventoryVO.setSkuID(skuId)
	inventoryVO.setGlobalStockLevel(globalStockLevel)
	 
	
	inventoryVoList.add(inventoryVO)
	
	List<String> listOfSiteCode = new ArrayList<String>();
	listOfSiteCode.add("BedBathUS");
	
	MutableRepository mutableRepositoryMocked=Mock()
	testObj.setInventoryRepository(mutableRepositoryMocked)
	
	MutableRepositoryItem mutableInventoryTranslationItemMock = Mock(["id":inventoryTransaltionID])
	Map<String,MutableRepositoryItem> translations=new HashMap<String,MutableRepositoryItem>()
	translations.put(siteId, mutableInventoryTranslationItemMock)
	TransactionDemarcation transactionDemarcationMock=Mock()
	LockReleaser lockReleaserMock=Mock()
	
	testObj.setTransactionManager(transactionManagerMock)
	 
	
	MutableRepositoryItem mutableInventoryItemMock = Mock(["id":inventoryRepositoryId])
	0* mutableInventoryItemMock.getRepositoryId() >> inventoryRepositoryId
	0* mutableRepositoryMocked.getItemForUpdate(inventoryVO.getInventoryID(), BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >>mutableInventoryItemMock
	0* mutableInventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> translations
	1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathUSSiteCode") >> listOfSiteCode
	0* mutableRepositoryMocked.updateItem(mutableInventoryItemMock)
	1* testObj.logError("TransactionDemarcationException occured in the beginning while updating online inventory " + inventoryVO.getSkuID(), _);
	expect:
	testObj.updateSKUInventoryForAllSites(inventoryVoList)
}

def "validate updateSKUInventoryForAllSites | handle DeadlockException"(){
	given:
	testObj.setTransactionManager(transactionManagerMock)
	def Transaction trans = Mock()
	transactionManagerMock.getTransaction() >> trans
	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathUS"
	String inventoryID="inventoryID_1"
	String inventoryRepositoryId="repositoryId_1"
	String inventoryTransaltionID="inventoryTransaltionID_1"
	
	Long siteStockLevel= 11
	Long giftRegistryStockLevel=22
	Long globalStockLevel=56
	List<InventoryVO>  inventoryVoList = new ArrayList<InventoryVO>()
	InventoryVO inventoryVO = new InventoryVO()
	inventoryVO.setSiteID(siteId)
	inventoryVO.setInventoryID(inventoryID)
	inventoryVO.setSiteStockLevel(siteStockLevel)
	inventoryVO.setGiftRegistryStockLevel(giftRegistryStockLevel)
	inventoryVO.setSkuID(skuId)
	inventoryVO.setGlobalStockLevel(globalStockLevel)
	 
	
	inventoryVoList.add(inventoryVO)
	
	List<String> listOfSiteCode = new ArrayList<String>();
	listOfSiteCode.add("BedBathUS");
	
	MutableRepository mutableRepositoryMocked=Mock()
	testObj.setInventoryRepository(mutableRepositoryMocked)
	
	MutableRepositoryItem mutableInventoryTranslationItemMock = Mock(["id":inventoryTransaltionID])
	Map<String,MutableRepositoryItem> translations=new HashMap<String,MutableRepositoryItem>()
	translations.put(siteId, mutableInventoryTranslationItemMock)
	TransactionDemarcation transactionDemarcationMock=Mock()
	LockReleaser lockReleaserMock=Mock()
	
	testObj.setTransactionManager(transactionManagerMock)
	 
	
	MutableRepositoryItem mutableInventoryItemMock = Mock(["id":inventoryRepositoryId])
	0* mutableInventoryItemMock.getRepositoryId() >> inventoryRepositoryId
	0* mutableRepositoryMocked.getItemForUpdate(inventoryVO.getInventoryID(), BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >>mutableInventoryItemMock
	0* mutableInventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> translations
	1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathUSSiteCode") >> listOfSiteCode
	
	
	1* testObj.getClientLockManager().acquireWriteLock(inventoryID+siteId) >>  { throw new DeadlockException("DeadlockException") }
	0* mutableRepositoryMocked.updateItem(mutableInventoryItemMock)
	0* testObj.logDebug("Site ID:--->" + inventoryVO.getSiteID())
	0* testObj.logDebug("Global Stock Level(AFS)=" + globalStockLevel + "|| Site Specific Stock Level(ALT_AFS)="+ siteStockLevel + "|| Gift Registry Stock Level(IGR)=" + giftRegistryStockLevel);
	1* testObj.logError("Deadlock Exception add Mando inventory update failed for sku " + inventoryVO.getSkuID(), _);
	expect:
	testObj.updateSKUInventoryForAllSites(inventoryVoList)
}


def "validate updateSKUInventoryForAllSites | handle LockManagerException"(){
	given:
	testObj.setTransactionManager(transactionManagerMock)
	def Transaction trans = Mock()
	5*transactionManagerMock.getTransaction() >> trans >> trans >> null
	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathUS"
	String inventoryID="inventoryID_1"
	String inventoryRepositoryId="repositoryId_1"
	String inventoryTransaltionID="inventoryTransaltionID_1"
	
	Long siteStockLevel= 11
	Long giftRegistryStockLevel=22
	Long globalStockLevel=56
	List<InventoryVO>  inventoryVoList = new ArrayList<InventoryVO>()
	InventoryVO inventoryVO = new InventoryVO()
	inventoryVO.setSiteID(siteId)
	inventoryVO.setInventoryID(inventoryID)
	inventoryVO.setSiteStockLevel(siteStockLevel)
	inventoryVO.setGiftRegistryStockLevel(giftRegistryStockLevel)
	inventoryVO.setSkuID(skuId)
	inventoryVO.setGlobalStockLevel(globalStockLevel)
	 
	
	inventoryVoList.add(inventoryVO)
	
	List<String> listOfSiteCode = new ArrayList<String>();
	listOfSiteCode.add("BedBathUS");
	
	MutableRepository mutableRepositoryMocked=Mock()
	testObj.setInventoryRepository(mutableRepositoryMocked)
	
	MutableRepositoryItem mutableInventoryTranslationItemMock = Mock(["id":inventoryTransaltionID])
	Map<String,MutableRepositoryItem> translations=new HashMap<String,MutableRepositoryItem>()
	translations.put(siteId, mutableInventoryTranslationItemMock)
	TransactionDemarcation transactionDemarcationMock=Mock()
	LockReleaser lockReleaserMock=Mock()
	
	testObj.setTransactionManager(transactionManagerMock)
	 
	
	MutableRepositoryItem mutableInventoryItemMock = Mock(["id":inventoryRepositoryId])
	0* mutableInventoryItemMock.getRepositoryId() >> inventoryRepositoryId
	0* mutableRepositoryMocked.getItemForUpdate(inventoryVO.getInventoryID(), BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >>mutableInventoryItemMock
	0* mutableInventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> translations
	1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathUSSiteCode") >> listOfSiteCode
	
	
	1* testObj.getClientLockManager().acquireWriteLock(inventoryID+siteId)
	0* mutableRepositoryMocked.updateItem(mutableInventoryItemMock)
	0* testObj.logDebug("Site ID:--->" + inventoryVO.getSiteID())
	0* testObj.logDebug("Global Stock Level(AFS)=" + globalStockLevel + "|| Site Specific Stock Level(ALT_AFS)="+ siteStockLevel + "|| Gift Registry Stock Level(IGR)=" + giftRegistryStockLevel);
	1* testObj.logError("Lockmanager Exception and Mando inventory update failed for sku " + inventoryVO.getSkuID(), _);
	expect:
	testObj.updateSKUInventoryForAllSites(inventoryVoList)
}

def "validate updateSKUInventoryForAllSites | handle Exception "(){
	given:
	testObj.setTransactionManager(transactionManagerMock)
	def Transaction trans = Mock()
	transactionManagerMock.getTransaction() >> trans
	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathUS"
	String inventoryID="inventoryID_1"
	String inventoryRepositoryId="repositoryId_1"
	String inventoryTransaltionID="inventoryTransaltionID_1"
	
	Long siteStockLevel= 11
	Long giftRegistryStockLevel=22
	Long globalStockLevel=56
	List<InventoryVO>  inventoryVoList = new ArrayList<InventoryVO>()
	InventoryVO inventoryVO = new InventoryVO()
	inventoryVO.setSiteID(siteId)
	inventoryVO.setInventoryID(inventoryID)
	inventoryVO.setSiteStockLevel(siteStockLevel)
	inventoryVO.setGiftRegistryStockLevel(giftRegistryStockLevel)
	inventoryVO.setSkuID(skuId)
	inventoryVO.setGlobalStockLevel(globalStockLevel)
	 
	
	inventoryVoList.add(inventoryVO)
	
	List<String> listOfSiteCode = new ArrayList<String>();
	listOfSiteCode.add("BedBathUS");
	
	MutableRepository mutableRepositoryMocked=Mock()
	testObj.setInventoryRepository(mutableRepositoryMocked)
	
	MutableRepositoryItem mutableInventoryTranslationItemMock = Mock(["id":inventoryTransaltionID])
	Map<String,MutableRepositoryItem> translations=new HashMap<String,MutableRepositoryItem>()
	translations.put(siteId, mutableInventoryTranslationItemMock)
	TransactionDemarcation transactionDemarcationMock=Mock()
	LockReleaser lockReleaserMock=Mock()
	
	testObj.setTransactionManager(transactionManagerMock)
	 
	
	MutableRepositoryItem mutableInventoryItemMock = Mock(["id":inventoryRepositoryId])
	 mutableInventoryItemMock.getRepositoryId() >> inventoryRepositoryId
	1* mutableRepositoryMocked.getItemForUpdate(inventoryVO.getInventoryID(), BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >>mutableInventoryItemMock
	0* mutableInventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> translations
	1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathUSSiteCode") >> listOfSiteCode
	
	
	1* clientLockManagerMock.acquireWriteLock(inventoryID+siteId)
	
	mutableRepositoryMocked.updateItem(mutableInventoryItemMock) >> { throw new RepositoryException("RepositoryException") }
	0* testObj.logDebug("Site ID:--->" + inventoryVO.getSiteID())
	0* testObj.logDebug("Global Stock Level(AFS)=" + globalStockLevel + "|| Site Specific Stock Level(ALT_AFS)="+ siteStockLevel + "|| Gift Registry Stock Level(IGR)=" + giftRegistryStockLevel);
	0* testObj.logError("System Exception and Mando inventory update failed for sku " + inventoryVO.getSkuID(), _);
	expect:
	testObj.updateSKUInventoryForAllSites(inventoryVoList)
}

def "validate updateSKUInventoryForAllSites | handle TransactionDemarcationException  during ending transaction "(){
	given:
	testObj.setTransactionManager(transactionManagerMock)
	def Transaction trans = Mock()
	5*transactionManagerMock.getTransaction() >> trans >> trans >> trans >> trans >>  { throw new TransactionDemarcationException("TransactionDemarcationException") }
	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathUS"
	String inventoryID="inventoryID_1"
	String inventoryRepositoryId="repositoryId_1"
	String inventoryTransaltionID="inventoryTransaltionID_1"
	
	Long siteStockLevel= 11
	Long giftRegistryStockLevel=22
	Long globalStockLevel=56
	List<InventoryVO>  inventoryVoList = new ArrayList<InventoryVO>()
	InventoryVO inventoryVO = new InventoryVO()
	inventoryVO.setSiteID(siteId)
	inventoryVO.setInventoryID(inventoryID)
	inventoryVO.setSiteStockLevel(siteStockLevel)
	inventoryVO.setGiftRegistryStockLevel(giftRegistryStockLevel)
	inventoryVO.setSkuID(skuId)
	inventoryVO.setGlobalStockLevel(globalStockLevel)
	 
	
	inventoryVoList.add(inventoryVO)
	
	List<String> listOfSiteCode = new ArrayList<String>();
	listOfSiteCode.add("BedBathUS");
	
	MutableRepository mutableRepositoryMocked=Mock()
	testObj.setInventoryRepository(mutableRepositoryMocked)
	
	MutableRepositoryItem mutableInventoryTranslationItemMock = Mock(["id":inventoryTransaltionID])
	Map<String,MutableRepositoryItem> translations=new HashMap<String,MutableRepositoryItem>()
	translations.put(siteId, mutableInventoryTranslationItemMock)
	TransactionDemarcation transactionDemarcationMock=Mock()
	LockReleaser lockReleaserMock=Mock()
	
	testObj.setTransactionManager(transactionManagerMock)
	 
	
	MutableRepositoryItem mutableInventoryItemMock = Mock(["id":inventoryRepositoryId])
	mutableInventoryItemMock.getRepositoryId() >> inventoryRepositoryId
	1* mutableRepositoryMocked.getItemForUpdate(inventoryVO.getInventoryID(), BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >>mutableInventoryItemMock
	0* mutableInventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME) >> translations
	1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathUSSiteCode") >> listOfSiteCode
	
	
	0* testObj.getClientLockManager().acquireWriteLock(inventoryRepositoryId+siteId)
	
	1* mutableRepositoryMocked.updateItem(mutableInventoryItemMock)
	1* testObj.logDebug("Site ID:--->" + inventoryVO.getSiteID())
	1* testObj.logDebug("Global Stock Level(AFS)=" + globalStockLevel + "|| Site Specific Stock Level(ALT_AFS)="+ siteStockLevel + "|| Gift Registry Stock Level(IGR)=" + giftRegistryStockLevel);
	1* testObj.logError("TransactionDemarcationException occured at the end while updating Mando inventory one of the sku" , _);
	expect:
	testObj.updateSKUInventoryForAllSites(inventoryVoList)
}


def "validate updateSKUInventoryForAllSites | handle RepositoryException "(){
	given:
	testObj.setTransactionManager(transactionManagerMock)
	def Transaction trans = Mock()
	transactionManagerMock.getTransaction() >> trans
	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathUS"
	String inventoryID="inventoryID_1"
	String inventoryRepositoryId="repositoryId_1"
	String inventoryTransaltionID="inventoryTransaltionID_1"
	
	Long siteStockLevel= 11
	Long giftRegistryStockLevel=22
	Long globalStockLevel=56
	List<InventoryVO>  inventoryVoList = new ArrayList<InventoryVO>()
	InventoryVO inventoryVO = new InventoryVO()
	inventoryVO.setSiteID(siteId)
	inventoryVO.setInventoryID(inventoryID)
	inventoryVO.setSiteStockLevel(siteStockLevel)
	inventoryVO.setGiftRegistryStockLevel(giftRegistryStockLevel)
	inventoryVO.setSkuID(skuId)
	inventoryVO.setGlobalStockLevel(globalStockLevel)
	 
	
	inventoryVoList.add(inventoryVO)
	
	List<String> listOfSiteCode = new ArrayList<String>();
	listOfSiteCode.add("BedBathUS");
	
	MutableRepository mutableRepositoryMocked=Mock()
	testObj.setInventoryRepository(mutableRepositoryMocked)
	
	MutableRepositoryItem mutableInventoryTranslationItemMock = Mock(["id":inventoryTransaltionID])
	Map<String,MutableRepositoryItem> translations=new HashMap<String,MutableRepositoryItem>()
	translations.put(siteId, mutableInventoryTranslationItemMock)
	TransactionDemarcation transactionDemarcationMock=Mock()
	LockReleaser lockReleaserMock=Mock()
	
	testObj.setTransactionManager(transactionManagerMock)
	 
	
	MutableRepositoryItem mutableInventoryItemMock = Mock(["id":inventoryRepositoryId])
	1* testObj.getClientLockManager().acquireWriteLock(inventoryID+siteId)
	1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathUSSiteCode") >> listOfSiteCode
	mutableRepositoryMocked.getItemForUpdate(inventoryVO.getInventoryID(), BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >>  { throw new RepositoryException("RepositoryException") }
	1* testObj.logError("Error while updating inventory [" + inventoryVO.getSkuID() + "]", _);
	expect:
	testObj.updateSKUInventoryForAllSites(inventoryVoList)
}

def "validate updateSKUInventoryForAllSites | handle BBBBusinessException "(){
	given:
	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathUS"
	String inventoryID="inventoryID_1"
	String inventoryRepositoryId="repositoryId_1"
	String inventoryTransaltionID="inventoryTransaltionID_1"
	
	Long siteStockLevel= 11
	Long giftRegistryStockLevel=22
	Long globalStockLevel=56
	List<InventoryVO>  inventoryVoList = new ArrayList<InventoryVO>()
	InventoryVO inventoryVO = new InventoryVO()
	inventoryVO.setSiteID(siteId)
	inventoryVO.setInventoryID(inventoryID)
	inventoryVO.setSiteStockLevel(siteStockLevel)
	inventoryVO.setGiftRegistryStockLevel(giftRegistryStockLevel)
	inventoryVO.setSkuID(skuId)
	inventoryVO.setGlobalStockLevel(globalStockLevel)
	 
	
	inventoryVoList.add(inventoryVO)
	
	List<String> listOfSiteCode = new ArrayList<String>();
	listOfSiteCode.add("BedBathUS");
	
	MutableRepository mutableRepositoryMocked=Mock()
	testObj.setInventoryRepository(mutableRepositoryMocked)
	
	MutableRepositoryItem mutableInventoryTranslationItemMock = Mock(["id":inventoryTransaltionID])
	Map<String,MutableRepositoryItem> translations=new HashMap<String,MutableRepositoryItem>()
	translations.put(siteId, mutableInventoryTranslationItemMock)
	TransactionDemarcation transactionDemarcationMock=Mock()
	LockReleaser lockReleaserMock=Mock()
	
	testObj.setTransactionManager(transactionManagerMock)
	 
	
	MutableRepositoryItem mutableInventoryItemMock = Mock(["id":inventoryRepositoryId])

	 
	 
	0* mutableRepositoryMocked.getItemForUpdate(inventoryVO.getInventoryID(), BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR) >>mutableInventoryItemMock
	
	1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathUSSiteCode") >>   { throw new BBBBusinessException("BBBBusinessException") }
	1* testObj.logError("Some Business Exception", _);
	expect:
	testObj.updateSKUInventoryForAllSites(inventoryVoList)
}

def "validate updateSKUInventoryForAllSites | inventoryVoList is empty "(){
	given:
	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathUS"
	String inventoryID="inventoryID_1"
	String inventoryRepositoryId="repositoryId_1"
	String inventoryTransaltionID="inventoryTransaltionID_1"
	
	Long siteStockLevel= 11
	Long giftRegistryStockLevel=22
	Long globalStockLevel=56
	List<InventoryVO>  inventoryVoList =new ArrayList<InventoryVO>()
	MutableRepository mutableRepositoryMocked=Mock()
	testObj.setInventoryRepository(mutableRepositoryMocked)
	expect:
	testObj.updateSKUInventoryForAllSites(inventoryVoList)
}
def "validate updateSKUInventoryForAllSites | inventoryVoList is null "(){
	given:
	String skuId="inv_sku_id1"
	Long orderQuantity=24
	String siteId="BedBathUS"
	String inventoryID="inventoryID_1"
	String inventoryRepositoryId="repositoryId_1"
	String inventoryTransaltionID="inventoryTransaltionID_1"
	
	Long siteStockLevel= 11
	Long giftRegistryStockLevel=22
	Long globalStockLevel=56
	List<InventoryVO>  inventoryVoList =null
	MutableRepository mutableRepositoryMocked=Mock()
	testObj.setInventoryRepository(mutableRepositoryMocked)
	expect:
	testObj.updateSKUInventoryForAllSites(inventoryVoList)
}
}
