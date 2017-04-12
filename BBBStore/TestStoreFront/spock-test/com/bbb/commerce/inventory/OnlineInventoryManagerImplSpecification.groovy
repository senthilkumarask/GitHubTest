package com.bbb.commerce.inventory
/*
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  SearchDropletSpecification.groovy
 *
 *  DESCRIPTION: Test Search droplet
 *
 *  HISTORY:
 *  Aug 18, 2016  Initial version
*/


import atg.repository.rql.ComparisonQuery
import atg.repository.ItemDescriptorImpl;
import atg.repository.MutableRepository
import atg.repository.Repository
import atg.repository.RepositoryException
import atg.repository.RepositoryView
import atg.repository.rql.RqlStatement

import java.sql.Timestamp;
import java.util.Calendar
import java.util.List;
import java.util.Map

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.inventory.vo.InventoryFeedVO;
import com.bbb.commerce.inventory.vo.InventoryVO
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.repository.RepositoryItemMock;

import spock.lang.specification.BBBExtendedSpec

class OnlineInventoryManagerImplSpecification extends BBBExtendedSpec {
	
	def OnlineInventoryManagerImpl testObj;
	def InventoryToolsImpl inventoryToolsMock = Mock()		
	def BBBCatalogTools catalogToolsMock = Mock()	
	def MutableRepository inventoryRepositoryMock = Mock()
	def Repository repositoryMock = Mock()
	def mockedSkuId="Mocked_Sku_001"
	def queryString="catalogRefId=?0"
	def RepositoryView viewMock=Mock()
	def String[] skuIdArray=new String[1]
	def String  NA="NA"
	def BBBInventoryManagerImpl inventoryManager= Mock()
	
	
	def setup(){
		testObj = Spy()
		testObj.setInventoryTools(inventoryToolsMock)
		testObj.setInventoryManager(inventoryManager)
		testObj.getInventoryManager()
		testObj.setCatalogTools(catalogToolsMock)
		testObj.getCatalogTools() >> catalogToolsMock
		testObj.setLoggingDebug(true)
	}
/***************************************************************************************************************************************************************	
---------------------------------------------------  getInventory(String skuId, String siteIdx)-----------------------------------------------------------------
*****************************************************************************************************************************************************************/		
	def "getInventory VO by passing  skuId"(){
		
		given:
			RqlStatement rqlStatementMock = Mock()
			Calendar calendar = Calendar.getInstance()
			calendar.add(Calendar.DATE, - 10);
			java.util.Date now = calendar.getTime()
			java.sql.Timestamp startDate = new java.sql.Timestamp(now.getTime())
			
			
			calendar = Calendar.getInstance()
			calendar.add(Calendar.DATE, + 100);
			 now = calendar.getTime()
			java.sql.Timestamp endDate = new java.sql.Timestamp(now.getTime())
			
			calendar = Calendar.getInstance()
			calendar.add(Calendar.DATE, - 9);
			 now = calendar.getTime()
			java.sql.Timestamp creationDate = new java.sql.Timestamp(now.getTime())
			
			
			calendar = Calendar.getInstance()
			calendar.add(Calendar.DATE, + 1);
			 now = calendar.getTime()
			java.sql.Timestamp availableDate = new java.sql.Timestamp(now.getTime())
			
			
			def String mockedInventoryId="Mocked_InventoryId_001"
			def long mockedSiteStockLevel=10L
			def long mockedRegStockLevel=11L
			def long mockedGlobalStockLevel=112L
			def String displayName ="MockedDisplayName"
			def String description ="MockedDescriptionInventoryPropertyName"
			
			RepositoryItemMock inventoryItemMock = Mock(["id":mockedInventoryId])
			inventoryItemMock.getRepositoryId() >> mockedInventoryId
			
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.CATALOG_REF_ID_INVENTORY_PROPERTY_NAME) >> mockedSkuId
			
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> mockedSiteStockLevel
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> mockedSiteStockLevel
			
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> mockedRegStockLevel
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> mockedRegStockLevel
			
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> mockedGlobalStockLevel
			
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_INVENTORY_PROPERTY_NAME) >> startDate
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_INVENTORY_PROPERTY_NAME) >> endDate
			
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.CREATION_DATE_INVENTORY_PROPERTY_NAME) >> creationDate
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.AVAILABILITY_DATE_INVENTORY_PROPERTY_NAME) >> availableDate
			
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_INVENTORY_PROPERTY_NAME) >> displayName
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_INVENTORY_PROPERTY_NAME) >> description
			
			
			RepositoryItemMock[] inventoryItemsMock = [inventoryItemMock]
			inventoryRepositoryMock.getView("inventory") >> viewMock
			inventoryToolsMock.getInventoryRepository() >> inventoryRepositoryMock
			
			testObj.setQueryString(queryString)
			testObj.getInventoryRqlStatement(queryString) >> rqlStatementMock
			rqlStatementMock.executeQuery(*_) >> inventoryItemsMock
			
		when:
			InventoryVO inventoryVO=testObj.getInventory(mockedSkuId, NA)
			
		then:
			inventoryVO.getInventoryID() == mockedInventoryId && inventoryVO.getSkuID() == mockedSkuId &&
			inventoryVO.getSiteStockLevel() == mockedSiteStockLevel && inventoryVO.getGiftRegistryStockLevel() == mockedRegStockLevel &&
			inventoryVO.getGlobalStockLevel() == mockedGlobalStockLevel  && inventoryVO.getStartDate() == startDate &&
			inventoryVO.getEndDate() == endDate && inventoryVO .getCreationDate() == creationDate &&
			inventoryVO.availabilityDate == availableDate && inventoryVO.getDisplayName() == displayName &&
			inventoryVO.getDescription() == description
		
	}
	
	def "getInventory VO by passing  skuId | handle RepositoryException"(){
		
		given:
			
			inventoryRepositoryMock.getView("inventory") >> viewMock
			
			inventoryToolsMock.getInventoryRepository() >> repositoryMock
			testObj.setQueryString(queryString)
			testObj.executeRQLQuery(*_) >> { throw new RepositoryException("RepositoryException occured during the invocation of executeQuery") }
		  when:
			 InventoryVO inventoryVo = testObj.getInventory(mockedSkuId, NA)
		then:
		inventoryVo == null
		thrown(BBBSystemException)
			
	}
	
	def "getInventory VO by passing blank skuId"(){
		given:
		    mockedSkuId = ""		
		when:
			InventoryVO inventoryVO=testObj.getInventory(mockedSkuId, NA)
		then:
			BBBBusinessException exception = thrown()		
	}
	
	
	def "getInventory VO by passing skuId and SKU does not exist in repository"(){
		
		given:
			RqlStatement rqlStatementMock = Mock()
			inventoryRepositoryMock.getView("inventory") >> viewMock
			inventoryToolsMock.getInventoryRepository() >> inventoryRepositoryMock			 
			testObj.setQueryString(queryString)
			testObj.getInventoryRqlStatement(queryString) >> rqlStatementMock
			rqlStatementMock.executeQuery(*_) >> null // return result null 
		when:
			InventoryVO inventoryVO=testObj.getInventory(mockedSkuId, NA)
		then:
			BBBBusinessException exception = thrown()
	}
	
	def "getInventory VO by passing skuId and Catch reposstoryExc in executeQuery"(){
		
		given:
			RqlStatement rqlStatementMock = Mock()
			inventoryRepositoryMock.getView("inventory") >> viewMock
			inventoryToolsMock.getInventoryRepository() >> inventoryRepositoryMock
			testObj.setQueryString(queryString)
			testObj.getInventoryRqlStatement(queryString) >> { throw new RepositoryException("RepositoryException occured during the invocation of executeQuery") }
			rqlStatementMock.executeQuery(*_) >> null // return result null
		when:
			InventoryVO inventoryVO=testObj.getInventory(mockedSkuId, NA)
		then:
			BBBSystemException exception = thrown()
	}
	
	def "getInventory VO by passing skuId and View is null"(){
		
		given:
			RqlStatement rqlStatementMock = Mock()
			inventoryRepositoryMock.getView("inventory") >> null
			inventoryToolsMock.getInventoryRepository() >> inventoryRepositoryMock
			testObj.setQueryString(queryString)
			testObj.getInventoryRqlStatement(queryString) >> rqlStatementMock
			rqlStatementMock.executeQuery(*_) >> null			
		when:
			InventoryVO inventoryVO=testObj.getInventory(mockedSkuId, NA)
		then:
			BBBBusinessException exception = thrown()
	}
	
	def "getInventory VO by passing skuId and Repository path is not defined"(){
		
		given:
			inventoryToolsMock.getInventoryRepository() >> null
			testObj.setQueryString(queryString)			
		when:
			InventoryVO inventoryVO=testObj.getInventory(mockedSkuId, NA)
		then:
			BBBBusinessException exception = thrown()
	}
	
	
	def "getInventory VO by passing skuId and rql query is null"(){
		
		given:			
			testObj.setQueryString(null)			
		when:
			InventoryVO inventoryVO=testObj.getInventory(mockedSkuId, NA)
		then:
			BBBBusinessException exception = thrown()
	}
/***************************************************************************************************************************************************************	
---------------------------------------------------  getCachedInventory(String skuId, String siteId)-----------------------------------------------------------------
*****************************************************************************************************************************************************************/
	def "getCachedInventory VO by passing  skuId"(){
		
		given:
			RqlStatement rqlStatementMock = Mock()
			Calendar calendar = Calendar.getInstance()
			calendar.add(Calendar.DATE, - 10);
			java.util.Date now = calendar.getTime()
			java.sql.Timestamp startDate = new java.sql.Timestamp(now.getTime())
			
			
			calendar = Calendar.getInstance()
			calendar.add(Calendar.DATE, + 100);
			 now = calendar.getTime()
			java.sql.Timestamp endDate = new java.sql.Timestamp(now.getTime())
			
			calendar = Calendar.getInstance()
			calendar.add(Calendar.DATE, - 9);
			 now = calendar.getTime()
			java.sql.Timestamp creationDate = new java.sql.Timestamp(now.getTime())
			
			
			calendar = Calendar.getInstance()
			calendar.add(Calendar.DATE, + 1);
			 now = calendar.getTime()
			java.sql.Timestamp availableDate = new java.sql.Timestamp(now.getTime())
			
			
			def String mockedInventoryId="Mocked_InventoryId_001"
			
			def long mockedSiteStockLevel=10L
			def long mockedRegStockLevel=11L
			def long mockedGlobalStockLevel=112L
			def String displayName ="MockedDisplayName"
			def String description ="MockedDescriptionInventoryPropertyName"
			
			RepositoryItemMock inventoryItemMock = Mock(["id":mockedInventoryId])
			inventoryItemMock.getRepositoryId() >> mockedInventoryId
			
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.CATALOG_REF_ID_INVENTORY_PROPERTY_NAME) >> mockedSkuId
			
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> mockedSiteStockLevel
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> mockedSiteStockLevel
			
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> mockedRegStockLevel
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> mockedRegStockLevel
			
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> mockedGlobalStockLevel
			
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_INVENTORY_PROPERTY_NAME) >> startDate
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_INVENTORY_PROPERTY_NAME) >> endDate
			
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.CREATION_DATE_INVENTORY_PROPERTY_NAME) >> creationDate
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.AVAILABILITY_DATE_INVENTORY_PROPERTY_NAME) >> availableDate
			
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_INVENTORY_PROPERTY_NAME) >> displayName
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_INVENTORY_PROPERTY_NAME) >> description
			
			
			RepositoryItemMock[] inventoryItemsMock = [inventoryItemMock]
			inventoryRepositoryMock.getView("inventory") >> viewMock
			inventoryToolsMock.getInventoryRepository() >> inventoryRepositoryMock
			
			testObj.setQueryString(queryString)
			testObj.getInventoryRqlStatement(queryString) >> rqlStatementMock
			rqlStatementMock.executeQuery(*_) >> inventoryItemsMock
			
		when:
			InventoryVO inventoryVO=testObj.getCachedInventory(mockedSkuId, NA)
			
		then:
			inventoryVO.getInventoryID() == mockedInventoryId && inventoryVO.getSkuID() == mockedSkuId &&
			inventoryVO.getSiteStockLevel() == mockedSiteStockLevel && inventoryVO.getGiftRegistryStockLevel() == mockedRegStockLevel &&
			inventoryVO.getGlobalStockLevel() == mockedGlobalStockLevel  && inventoryVO.getStartDate() == startDate &&
			inventoryVO.getEndDate() == endDate && inventoryVO .getCreationDate() == creationDate &&
			inventoryVO.availabilityDate == availableDate && inventoryVO.getDisplayName() == displayName &&
			inventoryVO.getDescription() == description
		
	}
	
	def "getCachedInventory VO by passing blank skuId"(){
		given:
			mockedSkuId = ""
		when:
			InventoryVO inventoryVO=testObj.getCachedInventory(mockedSkuId, NA)
		then:
			BBBBusinessException exception = thrown()
	}
	def "getCachedInventory VO | handle RepositoryException"(){
		
		given:
			
			inventoryRepositoryMock.getView("inventory") >> viewMock
			skuIdArray[0]=mockedSkuId
			inventoryToolsMock.getInventoryRepository() >> repositoryMock
			testObj.setQueryString(queryString)
			testObj.executeRQLQuery(*_) >> { throw new RepositoryException("RepositoryException occured during the invocation of executeQuery") }
		  when:
			 Map<String, InventoryVO> inventoryMap = testObj.getCachedInventory(mockedSkuId, NA)
		then:
		inventoryMap == null
		thrown(BBBSystemException)
			
	}
	
	def "getCachedInventory VO by passing skuId and SKU does not exist in repository"(){
		
		given:
			RqlStatement rqlStatementMock = Mock()
			inventoryRepositoryMock.getView("inventory") >> viewMock
			inventoryToolsMock.getInventoryRepository() >> inventoryRepositoryMock
			testObj.setQueryString(queryString)
			testObj.getInventoryRqlStatement(queryString) >> rqlStatementMock
			rqlStatementMock.executeQuery(*_) >> null // return result null
		when:
			InventoryVO inventoryVO=testObj.getCachedInventory(mockedSkuId, NA)
		then:
			BBBBusinessException exception = thrown()
	}
	
	def "getCachedInventory VO by passing skuId and View is null"(){
		
		given:
			RqlStatement rqlStatementMock = Mock()
			inventoryRepositoryMock.getView("inventory") >> null
			inventoryToolsMock.getInventoryRepository() >> inventoryRepositoryMock
			testObj.setQueryString(queryString)
			testObj.getInventoryRqlStatement(queryString) >> rqlStatementMock
			rqlStatementMock.executeQuery(*_) >> null
		when:
			InventoryVO inventoryVO=testObj.getCachedInventory(mockedSkuId, NA)
		then:
			BBBBusinessException exception = thrown()
	}
	
	def "getCachedInventory VO by passing skuId and Repository path is not defined"(){
		
		given:
			inventoryToolsMock.getInventoryRepository() >> null
			testObj.setQueryString(queryString)
		when:
			InventoryVO inventoryVO=testObj.getCachedInventory(mockedSkuId, NA)
		then:
			BBBBusinessException exception = thrown()
	}
	
	
	def "getCachedInventory VO by passing skuId and rql query is null"(){
		
		given:
			testObj.setQueryString(null)
		when:
			InventoryVO inventoryVO=testObj.getCachedInventory(mockedSkuId, NA)
		then:
			BBBBusinessException exception = thrown()
	}
	
	/***************************************************************************************************************************************************************
	 ---------------------------------------------------  getInventory(String[] skuId, String siteId)-----------------------------------------------------------------
	 *****************************************************************************************************************************************************************/
		 def "getInventory Map  by passing  skuId"(){
			 
			 given:
			 
			     
				 RqlStatement rqlStatementMock = Mock()
				 Calendar calendar = Calendar.getInstance()
				 calendar.add(Calendar.DATE, - 10);
				 java.util.Date now = calendar.getTime()
				 java.sql.Timestamp startDate = new java.sql.Timestamp(now.getTime())
				 
				 
				 calendar = Calendar.getInstance()
				 calendar.add(Calendar.DATE, + 100);
				  now = calendar.getTime()
				 java.sql.Timestamp endDate = new java.sql.Timestamp(now.getTime())
				 
				 calendar = Calendar.getInstance()
				 calendar.add(Calendar.DATE, - 9);
				  now = calendar.getTime()
				 java.sql.Timestamp creationDate = new java.sql.Timestamp(now.getTime())
				 
				 
				 calendar = Calendar.getInstance()
				 calendar.add(Calendar.DATE, + 1);
				  now = calendar.getTime()
				 java.sql.Timestamp availableDate = new java.sql.Timestamp(now.getTime())
				 
				 
				 def String mockedInventoryId="Mocked_InventoryId_001"
				
				 def long mockedSiteStockLevel=10L
				 def long mockedRegStockLevel=11L
				 def long mockedGlobalStockLevel=112L
				 def String displayName ="MockedDisplayName"
				 def String description ="MockedDescriptionInventoryPropertyName"
				 
				 skuIdArray[0]=mockedSkuId
				 
				 
				 RepositoryItemMock inventoryItemMock = Mock(["id":mockedInventoryId])
				 inventoryItemMock.getRepositoryId() >> mockedInventoryId
				 
				 inventoryItemMock.getPropertyValue(BBBCatalogConstants.CATALOG_REF_ID_INVENTORY_PROPERTY_NAME) >> mockedSkuId
				 
				 inventoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> mockedSiteStockLevel
				 inventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> mockedSiteStockLevel
				 
				 inventoryItemMock.getPropertyValue(BBBCatalogConstants.REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> mockedRegStockLevel
				 inventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> mockedRegStockLevel
				 
				 inventoryItemMock.getPropertyValue(BBBCatalogConstants.STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> mockedGlobalStockLevel
				 
				 inventoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_INVENTORY_PROPERTY_NAME) >> startDate
				 inventoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_INVENTORY_PROPERTY_NAME) >> endDate
				 
				 inventoryItemMock.getPropertyValue(BBBCatalogConstants.CREATION_DATE_INVENTORY_PROPERTY_NAME) >> creationDate
				 inventoryItemMock.getPropertyValue(BBBCatalogConstants.AVAILABILITY_DATE_INVENTORY_PROPERTY_NAME) >> availableDate
				 
				 inventoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_INVENTORY_PROPERTY_NAME) >> displayName
				 inventoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_INVENTORY_PROPERTY_NAME) >> description
				 
				 
				 RepositoryItemMock[] inventoryItemsMock = [inventoryItemMock]
				 inventoryRepositoryMock.getView("inventory") >> viewMock
				 inventoryToolsMock.getInventoryRepository() >> inventoryRepositoryMock
				 
				 testObj.setQueryString(queryString)
				 testObj.getInventoryRqlStatement(queryString) >> rqlStatementMock
				 rqlStatementMock.executeQuery(*_) >> inventoryItemsMock
				 
			 when:
				 Map skuIdInventoryVOMap=testObj.getInventory(skuIdArray, NA)
				 InventoryVO inventoryVO =skuIdInventoryVOMap.get(mockedSkuId);
				 
			 then:
				 inventoryVO.getInventoryID() == mockedInventoryId && inventoryVO.getSkuID() == mockedSkuId &&
				 inventoryVO.getSiteStockLevel() == mockedSiteStockLevel && inventoryVO.getGiftRegistryStockLevel() == mockedRegStockLevel &&
				 inventoryVO.getGlobalStockLevel() == mockedGlobalStockLevel  && inventoryVO.getStartDate() == startDate &&
				 inventoryVO.getEndDate() == endDate && inventoryVO .getCreationDate() == creationDate &&
				 inventoryVO.availabilityDate == availableDate && inventoryVO.getDisplayName() == displayName &&
				 inventoryVO.getDescription() == description
			 
		 }
		 
		 def "getInventory Map by passing blank array of skuId"(){
			 given:
				skuIdArray=new String[0]
			 when:
				 InventoryVO inventoryVO=testObj.getInventory(skuIdArray, NA)
			 then:
				 BBBBusinessException exception = thrown()
		 }
		 def "getInventory Map  | handle RepositoryException"(){
			 
			 given:
				 
				 inventoryRepositoryMock.getView("inventory") >> viewMock
				 skuIdArray[0]=mockedSkuId
				 inventoryToolsMock.getInventoryRepository() >> repositoryMock
				 testObj.setQueryString(queryString)
				 testObj.executeRQLQuery(*_) >> { throw new RepositoryException("RepositoryException occured during the invocation of executeQuery") }
			   when:
				  Map<String, InventoryVO> inventoryMap = testObj.getInventory(skuIdArray, NA)
			 then:
			 inventoryMap == null
			 thrown(BBBSystemException)
				 
		 }
		 
		 def "getInventory Map by passing skuId and SKU does not exist in repository"(){
			 
			 given:
				 RqlStatement rqlStatementMock = Mock()
				 inventoryRepositoryMock.getView("inventory") >> viewMock
				 inventoryToolsMock.getInventoryRepository() >> inventoryRepositoryMock
				 testObj.setQueryString(queryString)
				 testObj.getInventoryRqlStatement(queryString) >> rqlStatementMock
				 rqlStatementMock.executeQuery(*_) >> null // return result null
			 when:
				 InventoryVO inventoryVO=testObj.getInventory(skuIdArray, NA)
			 then:
				 BBBBusinessException exception = thrown()
		 }
		 
		 def "getInventory Map by passing skuId and View is null"(){
			 
			 given:
				 RqlStatement rqlStatementMock = Mock()
				 inventoryRepositoryMock.getView("inventory") >> null
				 inventoryToolsMock.getInventoryRepository() >> inventoryRepositoryMock
				 testObj.setQueryString(queryString)
				 testObj.getInventoryRqlStatement(queryString) >> rqlStatementMock
				 rqlStatementMock.executeQuery(*_) >> null
			 when:
				 InventoryVO inventoryVO=testObj.getInventory(skuIdArray, NA)
			 then:
				 BBBBusinessException exception = thrown()
		 }
		 
		 def "getInventory Map by passing skuId and Repository path is not defined"(){
			 
			 given:
				 inventoryToolsMock.getInventoryRepository() >> null
				 testObj.setQueryString(queryString)
			 when:
				 InventoryVO inventoryVO=testObj.getInventory(skuIdArray, NA)
			 then:
				 BBBBusinessException exception = thrown()
		 }
		 
		 
		 def "getInventory Map by passing skuId and rql query is null"(){
			 
			 given:
				 testObj.setQueryString(null)
			 when:
				 InventoryVO inventoryVO=testObj.getInventory(skuIdArray, NA)
			 then:
				 BBBBusinessException exception = thrown()
		 }
 /***************************************************************************************************************************************************************
  ---------------------------------------------------  getInventoryFeedUpdates(String status)---------------------------------------------------
  *****************************************************************************************************************************************************************/
	def "getInventoryFeedUpdates by status"(){
		given:
			 String invetoryStatus="CREATED"
			 String mID ="Mocked_Id"		
			 String mFeedID="Mocked_FeedID"		
		     Calendar calendar = Calendar.getInstance()
			 calendar.add(Calendar.DATE, - 10);
			 java.util.Date now = calendar.getTime()
			 java.sql.Timestamp mFeedCreationDate = new java.sql.Timestamp(now.getTime())
			
			 calendar.add(Calendar.DATE, - 12);
			 now = calendar.getTime()
			 java.sql.Timestamp mFeedLastUpdatedDate = new java.sql.Timestamp(now.getTime())
				
			
			
			 List<InventoryFeedVO> invetoryStatusList1 = new ArrayList<InventoryFeedVO>()		
			 InventoryFeedVO inventoryFeedVO1 = new InventoryFeedVO()
			
			 inventoryFeedVO1.setID(mID)
			 inventoryFeedVO1.setFeedID(mFeedID)
			 inventoryFeedVO1.setFeedStatus(invetoryStatus)
			 inventoryFeedVO1.setFeedCreationDate(mFeedCreationDate);
			 inventoryFeedVO1.setFeedLastUpdatedDate(mFeedLastUpdatedDate)
			
			 invetoryStatusList1.add(inventoryFeedVO1)
		
		     inventoryToolsMock.getInventoryFeedUpdates(invetoryStatus) >> invetoryStatusList1
		when:
		List<InventoryFeedVO> invetoryStatusList2= testObj.getInventoryFeedUpdates(invetoryStatus)
		InventoryFeedVO  inventoryFeedVO2 =invetoryStatusList2.get(0)
		then:
		inventoryFeedVO2.getID() == mID && inventoryFeedVO2.getFeedID() == mFeedID &&
		inventoryFeedVO2.getFeedCreationDate() == mFeedCreationDate && inventoryFeedVO2.getFeedLastUpdatedDate() == mFeedLastUpdatedDate
	}
	
/***************************************************************************************************************************************************************
 ---------------------------------------------------invalidateInventoryCache()---------------------------------------------------
 *****************************************************************************************************************************************************************/
	def "invalidateInventoryCache"(){
		given:
			inventoryToolsMock.invalidateInventoryCache()
		when:
		testObj.invalidateInventoryCache()
		then:
			1* inventoryToolsMock.invalidateInventoryCache()
	}
	
/***************************************************************************************************************************************************************
 ---------------------------------------------------  updateInventoryFeed(InventoryFeedVO pInventoryFeed)  ---------------------------------------------------
 *****************************************************************************************************************************************************************/
	   def "do updateInventoryFeed"(){
		   given:
		   def InventoryFeedVO inventoryFeedVO= new InventoryFeedVO()
		   def List<InventoryFeedVO> invFeedVoList= new ArrayList<InventoryFeedVO>();
		   invFeedVoList.add(inventoryFeedVO);
		   1* inventoryToolsMock.updateInventoryFeed(invFeedVoList)
		  expect:
		   testObj.updateInventoryFeed(invFeedVoList)
		   
	   }
	
	
	
/***************************************************************************************************************************************************************
 ---------------------------------------------------  invalidateInventoryCache(InventoryFeedVO pInventoryFeed)---------------------------------------------------
 *****************************************************************************************************************************************************************/
   def "do invalidateInventoryCache"(){
	   given:
	   InventoryFeedVO inventoryFeedVO= new InventoryFeedVO()
	   inventoryFeedVO.setInventoryID("Mocked_inv_ID");
	   inventoryToolsMock.removeItemFromCache(inventoryFeedVO)
	   when:
	   
	   testObj.invalidateInventoryCache(inventoryFeedVO)
	   then:
	   1* inventoryToolsMock.removeItemFromCache(inventoryFeedVO)
   }
	

/***************************************************************************************************************************************************************
---------------------------------------------------   getAltAfs(String skuId, String siteId)  ---------------------------------------------------
*****************************************************************************************************************************************************************/

	  def "getAltAfs"(){
		  given:
		  
		  def String mockedInventoryId="Mocked_InventoryId_001"
		  
		  def long altAfsVal=10L
		  RqlStatement rqlStatementMock = Mock()
		  RepositoryItemMock inventoryItemMock = Mock(["id":mockedInventoryId])
		  inventoryItemMock.getRepositoryId() >> mockedInventoryId
		  inventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> altAfsVal
		  
		  RepositoryItemMock[] inventoryItemsMock = [inventoryItemMock]
		  inventoryRepositoryMock.getView("inventory") >> viewMock
		  inventoryToolsMock.getInventoryRepository() >> inventoryRepositoryMock
		  
		  testObj.setQueryString(queryString)
		  testObj.getInventoryRqlStatement(queryString) >> rqlStatementMock
		  rqlStatementMock.executeQuery(*_) >> inventoryItemsMock
		 when:
		  Long altAfs = testObj.getAltAfs(mockedSkuId,NA)
		  then:
		  altAfs ==altAfsVal
	  }
	  def "getAltAfs | handle RepositoryException"(){
		  
		  given:
			  
			  inventoryRepositoryMock.getView("inventory") >> viewMock
			  skuIdArray[0]=mockedSkuId
			  inventoryToolsMock.getInventoryRepository() >> repositoryMock
			  testObj.setQueryString(queryString)
			  testObj.executeRQLQuery(*_) >> { throw new RepositoryException("RepositoryException occured during the invocation of executeQuery") }
			when:
			   Long altAfs = testObj.getAltAfs(mockedSkuId, NA)
		  then:
		  altAfs == null
		  thrown(BBBSystemException)
			  
	  }
	  
	  def "getAltAfs for null skuId"(){
		  given:
		  	String mockedInventoryId="Mocked_InventoryId_001"
		 when:
		  	Long altAfs = testObj.getAltAfs(null,NA)
		 then:
		  BBBBusinessException exception = thrown()
	  }
  /***************************************************************************************************************************************************************
   ---------------------------------------------------  getMaxStockSku(List<String> skuId, String siteId)  ---------------------------------------------------
   *****************************************************************************************************************************************************************/
  def "getMaxStockSku from list of sku"(){
	  given:
	  String sku1="mouckedSku_1"
	  String sku2="mouckedSku_2"
	  String sku3="mouckedSku_3"
	  
	  String inv1="mouckedInv_1"
	  String inv2="mouckedInv_2"
	  String inv3="mouckedInv_3"
	  
	  List<String> skuList= new ArrayList<String>();
	  skuList.add(sku1)
	  skuList.add(sku2)
	  skuList.add(sku3)
	  
	  long sku1Inv=1
	  long sku2Inv=20
	  long sku3Inv=11
	  
	  
	  RepositoryItemMock inventoryItemMock1 = Mock(["id":inv1])
	  inventoryItemMock1.getRepositoryId() >> inv1	  
	  inventoryItemMock1.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> sku1Inv
	  
	  RepositoryItemMock inventoryItemMock2 = Mock(["id":inv2])
	  inventoryItemMock2.getRepositoryId() >> inv2	  
	  inventoryItemMock2.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> sku2Inv
	  
	  RepositoryItemMock inventoryItemMock3 = Mock(["id":inv3])
	  inventoryItemMock3.getRepositoryId() >> inv3	  
	  inventoryItemMock3.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> sku3Inv
	  
	
	  
	  RepositoryItemMock[] inventoryItemsMock = [inventoryItemMock1,inventoryItemMock2,inventoryItemMock3]
	  inventoryRepositoryMock.getView("inventory") >> viewMock
	  inventoryToolsMock.getInventoryRepository() >> inventoryRepositoryMock
	  RqlStatement rqlStatementMock = Mock()
	  testObj.setQueryString(queryString)
	  testObj.getInventoryRqlStatement(queryString) >> rqlStatementMock
	  rqlStatementMock.executeQuery(*_) >>> inventoryItemsMock
	 when:
	  String  maxInventorySkuId = testObj.getMaxStockSku(skuList,NA)
	  then:
	  sku2 == maxInventorySkuId
  }
  
  def "getMaxStockSku | handle RepositoryException"(){
	  
	  given:
		  String sku1="mouckedSku_1"
		  String sku2="mouckedSku_2"
		  String sku3="mouckedSku_3"
		  
		  String inv1="mouckedInv_1"
		  String inv2="mouckedInv_2"
		  String inv3="mouckedInv_3"
		  
		  List<String> skuList= new ArrayList<String>();
		  skuList.add(sku1)
		  skuList.add(sku2)
		  skuList.add(sku3)
		  inventoryRepositoryMock.getView("inventory") >> viewMock
		  skuIdArray[0]=mockedSkuId
		  inventoryToolsMock.getInventoryRepository() >> repositoryMock
		  testObj.setQueryString(queryString)
		  testObj.executeRQLQuery(*_) >> { throw new RepositoryException("RepositoryException occured during the invocation of executeQuery") }
		when:
		   String maxInventorySkuId = testObj.getMaxStockSku(skuList, NA)
	  then:
	  maxInventorySkuId == null
	  thrown(BBBSystemException)
		  
  }
  
  def "Test getMaxStockSku with empty sku list"(){
	  given:
	  	List<String> skuList= new ArrayList<String>();
	when:
	  	String  maxInventorySkuId = testObj.getMaxStockSku(skuList,NA)
	 then:
	  	BBBBusinessException exception = thrown()
  }
  
  /***************************************************************************************************************************************************************
   ---------------------------------------------------    getAfs(String skuId, String siteId)  ---------------------------------------------------
   *****************************************************************************************************************************************************************/
  def "getAfs"(){
	  given:
	  
	  def String mockedInventoryId="Mocked_InventoryId_001"
	  
	  long afsVal=10L
	  RqlStatement rqlStatementMock = Mock()
	  RepositoryItemMock inventoryItemMock = Mock(["id":mockedInventoryId])
	  inventoryItemMock.getRepositoryId() >> mockedInventoryId
	  inventoryItemMock.getPropertyValue(BBBCatalogConstants.STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> afsVal
	  
	  RepositoryItemMock[] inventoryItemsMock = [inventoryItemMock]
	  inventoryRepositoryMock.getView("inventory") >> viewMock
	  inventoryToolsMock.getInventoryRepository() >> inventoryRepositoryMock
	  
	  testObj.setQueryString(queryString)
	  testObj.getInventoryRqlStatement(queryString) >> rqlStatementMock
	  rqlStatementMock.executeQuery(*_) >> inventoryItemsMock
	 when:
	  Long altAfs = testObj.getAfs(mockedSkuId,NA)
	  then:
	  altAfs ==afsVal
  }
  
  def "getAfs | handle  RepositoryException"(){
	  
	  given:
		  
		  inventoryRepositoryMock.getView("inventory") >> viewMock
		  inventoryToolsMock.getInventoryRepository() >> repositoryMock
		  testObj.setQueryString(queryString)
		  testObj.executeRQLQuery(*_) >> { throw new RepositoryException("RepositoryException occured during the invocation of executeQuery") }
		when:
		   Long altAfs = testObj.getAfs(mockedSkuId, NA)
	  then:
	  altAfs == null
	  thrown(BBBSystemException)
		  
  }
  
  def "Test getAfs with for null skuId"(){
	  given:
		  String mockedInventoryId="Mocked_InventoryId_001"
	 when:
		  Long altAfs = testObj.getAfs(null,NA)
	 then:
	  BBBBusinessException exception = thrown()
  }
  
  /***************************************************************************************************************************************************************
   ---------------------------------------------------    getIgr(String skuId, String siteId)----------------------------------------------------------------------
   *****************************************************************************************************************************************************************/
  def "getIgr"(){
	  given:
	  
	  def String mockedInventoryId="Mocked_InventoryId_001"
	  
	  long igrVal=10L
	  RqlStatement rqlStatementMock = Mock()
	  RepositoryItemMock inventoryItemMock = Mock(["id":mockedInventoryId])
	  inventoryItemMock.getRepositoryId() >> mockedInventoryId
	  inventoryItemMock.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) >> igrVal
	  
	  RepositoryItemMock[] inventoryItemsMock = [inventoryItemMock]
	  inventoryRepositoryMock.getView("inventory") >> viewMock
	  inventoryToolsMock.getInventoryRepository() >> inventoryRepositoryMock
	  
	  testObj.setQueryString(queryString)
	  testObj.getInventoryRqlStatement(queryString) >> rqlStatementMock
	  rqlStatementMock.executeQuery(*_) >> inventoryItemsMock
	 when:
	  Long igrVal1 = testObj.getIgr(mockedSkuId,NA)
	  then:
	  igrVal ==igrVal1
  }
  def "getIgr | handle  RepositoryException"(){
	  
	  given:
		  
		  inventoryRepositoryMock.getView("inventory") >> viewMock
		  inventoryToolsMock.getInventoryRepository() >> repositoryMock
		  testObj.setQueryString(queryString)
		  testObj.executeRQLQuery(*_) >> { throw new RepositoryException("RepositoryException occured during the invocation of executeQuery") }
		when:
		   Long altAfs = testObj.getIgr(mockedSkuId, NA)
	  then:
	  altAfs == null
	  thrown(BBBSystemException)
		  
  }
  def "Test getIgr with for null skuId"(){
	  given:
		  String mockedInventoryId="Mocked_InventoryId_001"
	 when:
		  Long altAfs = testObj.getIgr(null,NA)
	 then:
	  BBBBusinessException exception = thrown()
  }
  
 /***************************************************************************************************************************************************************
 ---------------------------------------------------    getInventoryRqlStatement(String rqlQuery) ---------------------------------------------------------------
 ***************************************************************************************************************************************************************/
  
  def "getInventoryRqlStatement"(){
	  given:
	  	 
	 when:
	  RqlStatement rqlStml = testObj.getInventoryRqlStatement(queryString)
	  ComparisonQuery comparisonQuery=rqlStml.getQuery()
	  then:
	  comparisonQuery.getLeft().toString()+"="+comparisonQuery.getRight().toString() == queryString
  }
 
  /***************************************************************************************************************************************************************
   ---------------------------------------------------    decrementInventoryStock(final InventoryVO[] pInventoryVOs)  --------------------------------------------
   ***************************************************************************************************************************************************************/

  def "decrementInventoryStock | update all stock level inventory | non CA siteId"(){
	  given:
	  InventoryVO[] inventoryVOArray=new InventoryVO[3];
	  
	  List<String> config = new ArrayList<String>()	  
	  config.add("TRUE");
	  
	  List<String> siteCodes = new ArrayList<String>()
	  siteCodes.add("BedBathCA")
	  
	  List<InventoryVO> inventoryVOList= new ArrayList<InventoryVO>()
	  InventoryVO inventoryVO1 = new InventoryVO()
	  inventoryVO1.setInventoryID("mockInvId_1")
	  inventoryVO1.setGlobalStockLevel(10)
	  inventoryVO1.setSiteStockLevel(11)
	  inventoryVO1.setGiftRegistryStockLevel(12)
	  inventoryVO1.setSiteID("BedBathUS")
	  inventoryVO1.setSkuID("mockedSkuId_1")
	  inventoryVO1.setOrderedQuantity(1) //order Quantity is less than [ global stock]
	  inventoryVOList.add(inventoryVO1)
	  
	  
	  InventoryVO inventoryVO2 = new InventoryVO()
	  inventoryVO2.setInventoryID("mockInvId_2")
	  inventoryVO2.setGlobalStockLevel(20)
	  inventoryVO2.setSiteStockLevel(21)
	  inventoryVO2.setGiftRegistryStockLevel(22)
	  inventoryVO2.setSiteID("BedBathUS")
	  inventoryVO2.setSkuID("mockedSkuId_2")
	  inventoryVO2.setOrderedQuantity(25) //order Quantity is less than [ global stock + site stock]
	  inventoryVOList.add(inventoryVO2)
	  
	  
	  InventoryVO inventoryVO3 = new InventoryVO()
	  inventoryVO3.setInventoryID("mockInvId_3")
	  inventoryVO3.setGlobalStockLevel(30)
	  inventoryVO3.setSiteStockLevel(31)
	  inventoryVO3.setGiftRegistryStockLevel(32)
	  inventoryVO3.setSiteID("BedBathUS")
	  inventoryVO3.setSkuID("mockedSkuId_3") 
	  inventoryVO3.setOrderedQuantity(64) //order Quantity is less than [ global stock + site stock + Gift Registry Stock]
	  inventoryVOList.add(inventoryVO3)
	  
	  
	  InventoryVO inventoryVO4 = new InventoryVO()
	  inventoryVO4.setInventoryID("mockInvId_4")
	  inventoryVO4.setGlobalStockLevel(30)
	  inventoryVO4.setSiteStockLevel(31)
	  inventoryVO4.setGiftRegistryStockLevel(32)
	  inventoryVO4.setSiteID("BedBathUs")
	  inventoryVO4.setSkuID("mockedSkuId_4")
	  inventoryVO4.setOrderedQuantity(100) // globalStock + registryStock + siteStock) > 0
	  inventoryVOList.add(inventoryVO4)
	  
	  
	  InventoryVO inventoryVO5 = new InventoryVO()
	  inventoryVO5.setInventoryID("mockInvId_5")
	  inventoryVO5.setGlobalStockLevel(-1)
	  inventoryVO5.setSiteStockLevel(-1)
	  inventoryVO5.setGiftRegistryStockLevel(-1) // globalStock <0 , registryStock <0, siteStock<0
	  inventoryVO5.setSiteID("BedBathUs")
	  inventoryVO5.setSkuID("mockedSkuId_4")
	  inventoryVO5.setOrderedQuantity(10) 
	  inventoryVOList.add(inventoryVO5)
	  
	  List<SKUDetailVO> skuDetailVOList = new ArrayList<SKUDetailVO>()
	  
	  SKUDetailVO skuDetailVO1 = new SKUDetailVO()
	  skuDetailVO1.setVdcSku(false)
	  skuDetailVO1.setEcomFulfillment("E")
	  skuDetailVOList.add(skuDetailVO1)
	  
	  SKUDetailVO skuDetailVO2 = new SKUDetailVO()
	  skuDetailVO2.setVdcSku(false)
	  skuDetailVO2.setEcomFulfillment("E")
	  skuDetailVOList.add(skuDetailVO2)
	  
	  SKUDetailVO skuDetailVO3 = new SKUDetailVO()
	  skuDetailVO3.setVdcSku(false)
	  skuDetailVO3.setEcomFulfillment(null)
	  skuDetailVOList.add(skuDetailVO3)
	  
	  SKUDetailVO skuDetailVO4 = new SKUDetailVO()
	  skuDetailVO4.setVdcSku(false)
	  skuDetailVO4.setEcomFulfillment(null)
	  skuDetailVOList.add(skuDetailVO4)
	  
	  SKUDetailVO skuDetailVO5 = new SKUDetailVO()
	  skuDetailVO5.setVdcSku(false)
	  skuDetailVO5.setEcomFulfillment(null)
	  skuDetailVOList.add(skuDetailVO5)
	  
	 
	  
	  1* catalogToolsMock.getContentCatalogConfigration(BBBCoreConstants.UPDATE_ALL_INVENTORY_CONFIG) >> config
	  1* inventoryToolsMock.getSKUInventory(inventoryVOArray) >> inventoryVOList
	  5* catalogToolsMock.getSKUDetails(*_) >>> skuDetailVOList
	  5* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathCanadaSiteCode") >> siteCodes
	  1* inventoryToolsMock.updateSKUInventoryForAllSites(inventoryVOList)
	  testObj.getCatalogTools() >> catalogToolsMock
	 
	  expect: 
	  	testObj.decrementInventoryStock(inventoryVOArray)
}
  
  def "decrementInventoryStock | update all stock level inventory | CA siteId"(){
	  given:
	  InventoryVO[] inventoryVOArray=new InventoryVO[3];
	  
	  List<String> config = new ArrayList<String>()
	  config.add("TRUE");
	  
	  List<String> siteCodes = new ArrayList<String>()
	  siteCodes.add("BedBathCA")
	  
	  List<InventoryVO> inventoryVOList= new ArrayList<InventoryVO>()
	  InventoryVO inventoryVO1 = new InventoryVO()
	  inventoryVO1.setInventoryID("mockInvId_1")
	  inventoryVO1.setGlobalStockLevel(10)
	  inventoryVO1.setSiteStockLevel(11)
	  inventoryVO1.setGiftRegistryStockLevel(12)
	  inventoryVO1.setSiteID("BedBathCA")
	  inventoryVO1.setSkuID("mockedSkuId_1")
	  inventoryVO1.setOrderedQuantity(1) //order Quantity is less than [siteStock]
	  inventoryVOList.add(inventoryVO1)
	  
	  
	  InventoryVO inventoryVO2 = new InventoryVO()
	  inventoryVO2.setInventoryID("mockInvId_2")
	  inventoryVO2.setGlobalStockLevel(20)
	  inventoryVO2.setSiteStockLevel(21)
	  inventoryVO2.setGiftRegistryStockLevel(22)
	  inventoryVO2.setSiteID("BedBathCA")
	  inventoryVO2.setSkuID("mockedSkuId_2")
	  inventoryVO2.setOrderedQuantity(25) //order Quantity is greater than [siteStock]
	  inventoryVOList.add(inventoryVO2)
	  
	 
	  InventoryVO inventoryVO3 = new InventoryVO()
	  inventoryVO3.setInventoryID("mockInvId_1")
	  inventoryVO3.setGlobalStockLevel(10)
	  inventoryVO3.setSiteStockLevel(11)
	  inventoryVO3.setGiftRegistryStockLevel(12)
	  inventoryVO3.setSiteID("BedBathCA")
	  inventoryVO3.setSkuID("mockedSkuId_1")
	  inventoryVO3.setOrderedQuantity(1) //order Quantity is less than [siteStock]
	  inventoryVOList.add(inventoryVO3)
	  
	  
	  InventoryVO inventoryVO4 = new InventoryVO()
	  inventoryVO4.setInventoryID("mockInvId_2")
	  inventoryVO4.setGlobalStockLevel(20)
	  inventoryVO4.setSiteStockLevel(21)
	  inventoryVO4.setGiftRegistryStockLevel(22)
	  inventoryVO4.setSiteID("BedBathCA")
	  inventoryVO4.setSkuID("mockedSkuId_2")
	  inventoryVO4.setOrderedQuantity(25) //order Quantity is greater than [siteStock]
	  inventoryVOList.add(inventoryVO4)
	  
	 
	  List<SKUDetailVO> skuDetailVOList = new ArrayList<SKUDetailVO>()
	  
	  SKUDetailVO skuDetailVO1 = new SKUDetailVO()
	  skuDetailVO1.setVdcSku(false)
	  skuDetailVO1.setEcomFulfillment(null)
	  skuDetailVOList.add(skuDetailVO1)
	  
	  SKUDetailVO skuDetailVO2 = new SKUDetailVO()
	  skuDetailVO2.setVdcSku(false)
	  skuDetailVO2.setEcomFulfillment(null)
	  skuDetailVOList.add(skuDetailVO2)
	  
	  SKUDetailVO skuDetailVO3 = new SKUDetailVO()
	  skuDetailVO3.setVdcSku(false)
	  skuDetailVO3.setEcomFulfillment("E")
	  skuDetailVOList.add(skuDetailVO3)
	  
	  SKUDetailVO skuDetailVO4 = new SKUDetailVO()
	  skuDetailVO4.setVdcSku(false)
	  skuDetailVO4.setEcomFulfillment("No_E")
	  skuDetailVOList.add(skuDetailVO4)
	  
	  
	  1* catalogToolsMock.getContentCatalogConfigration(BBBCoreConstants.UPDATE_ALL_INVENTORY_CONFIG) >> config
	  1* inventoryToolsMock.getSKUInventory(inventoryVOArray) >> inventoryVOList
	  4* catalogToolsMock.getSKUDetails(*_) >>> skuDetailVOList
	  4* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathCanadaSiteCode") >> siteCodes
	  1* inventoryToolsMock.updateSKUInventoryForAllSites(inventoryVOList)
	  testObj.getCatalogTools() >> catalogToolsMock
	 
	  expect:
		  testObj.decrementInventoryStock(inventoryVOArray)
}
  
  def "decrementInventoryStock | update all stock level inventory | do not update inventory status"(){
	  given:
	  InventoryVO[] inventoryVOArray=new InventoryVO[3];
	  
	  List<String> config = new ArrayList<String>()
	  config.add("FALSE");
	  
	  List<String> siteCodes = new ArrayList<String>()
	  siteCodes.add("BedBathCA")
	  
	  List<InventoryVO> inventoryVOList= new ArrayList<InventoryVO>()
	  InventoryVO inventoryVO1 = new InventoryVO()
	  inventoryVO1.setInventoryID("mockInvId_1")
	  inventoryVO1.setGlobalStockLevel(10)
	  inventoryVO1.setSiteStockLevel(11)
	  inventoryVO1.setGiftRegistryStockLevel(12)
	  inventoryVO1.setSiteID("BedBathCA")
	  inventoryVO1.setSkuID("mockedSkuId_1")
	  inventoryVO1.setOrderedQuantity(1) //order Quantity is less than [siteStock]
	  inventoryVOList.add(inventoryVO1)
	  
	  List<SKUDetailVO> skuDetailVOList = new ArrayList<SKUDetailVO>()
	  
	  SKUDetailVO skuDetailVO1 = new SKUDetailVO()
	  skuDetailVO1.setVdcSku(false)
	  skuDetailVO1.setEcomFulfillment(null)
	  skuDetailVOList.add(skuDetailVO1)
	  
	  
	  
	  
	  1* catalogToolsMock.getContentCatalogConfigration(BBBCoreConstants.UPDATE_ALL_INVENTORY_CONFIG) >> config
	  1* inventoryToolsMock.getSKUInventory(inventoryVOArray) >> inventoryVOList
	  1* catalogToolsMock.getSKUDetails(*_) >>> skuDetailVOList
	  1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathCanadaSiteCode") >> siteCodes
	  0* inventoryToolsMock.updateSKUInventoryForAllSites(inventoryVOList)
	  testObj.getCatalogTools() >> catalogToolsMock
	 
	  expect:
		  testObj.decrementInventoryStock(inventoryVOArray)
}
  

  def "decrementInventoryStock | update all stock level inventory | stock reach to zero"(){
	  given:
	  InventoryVO[] inventoryVOArray=new InventoryVO[3];
	  
	  List<String> config = new ArrayList<String>()
	  config.add("TRUE");
	  
	  List<String> siteCodes = new ArrayList<String>()
	  siteCodes.add("BedBathCA")
	  
	  List<InventoryVO> inventoryVOList= new ArrayList<InventoryVO>()
	  InventoryVO inventoryVO1 = new InventoryVO()
	  inventoryVO1.setInventoryID("mockInvId_1")
	  inventoryVO1.setGlobalStockLevel(10)
	  inventoryVO1.setSiteStockLevel(11)
	  inventoryVO1.setGiftRegistryStockLevel(12)
	  inventoryVO1.setSiteID("BedBathUS")
	  inventoryVO1.setSkuID("mockedSkuId_1")
	  inventoryVO1.setOrderedQuantity(10) //order Quantity = [ global stock]
	  inventoryVOList.add(inventoryVO1)
	  
	  
	  InventoryVO inventoryVO2 = new InventoryVO()
	  inventoryVO2.setInventoryID("mockInvId_2")
	  inventoryVO2.setGlobalStockLevel(20)
	  inventoryVO2.setSiteStockLevel(15)
	  inventoryVO2.setGiftRegistryStockLevel(22)
	  inventoryVO2.setSiteID("BedBathUS")
	  inventoryVO2.setSkuID("mockedSkuId_2")
	  inventoryVO2.setOrderedQuantity(35) //order Quantity =[ global stock + site stock]
	  inventoryVOList.add(inventoryVO2)
	  
	  
	  InventoryVO inventoryVO3 = new InventoryVO()
	  inventoryVO3.setInventoryID("mockInvId_3")
	  inventoryVO3.setGlobalStockLevel(30)
	  inventoryVO3.setSiteStockLevel(4)
	  inventoryVO3.setGiftRegistryStockLevel(6)
	  inventoryVO3.setSiteID("BedBathUS")
	  inventoryVO3.setSkuID("mockedSkuId_3")
	  inventoryVO3.setOrderedQuantity(40) //order Quantity is less than [ global stock + site stock + Gift Registry Stock]
	  inventoryVOList.add(inventoryVO3)
	  
	  
	 
	  
	  List<SKUDetailVO> skuDetailVOList = new ArrayList<SKUDetailVO>()
	  
	  SKUDetailVO skuDetailVO1 = new SKUDetailVO()
	  skuDetailVO1.setVdcSku(false)
	  skuDetailVO1.setEcomFulfillment("E")
	  skuDetailVOList.add(skuDetailVO1)
	  
	  SKUDetailVO skuDetailVO2 = new SKUDetailVO()
	  skuDetailVO2.setVdcSku(false)
	  skuDetailVO2.setEcomFulfillment("E")
	  skuDetailVOList.add(skuDetailVO2)
	  
	  SKUDetailVO skuDetailVO3 = new SKUDetailVO()
	  skuDetailVO3.setVdcSku(false)
	  skuDetailVO3.setEcomFulfillment("No_E")
	  skuDetailVOList.add(skuDetailVO3)
	  
	  
	  
	 
	  
	  1* catalogToolsMock.getContentCatalogConfigration(BBBCoreConstants.UPDATE_ALL_INVENTORY_CONFIG) >> config
	  1* inventoryToolsMock.getSKUInventory(inventoryVOArray) >> inventoryVOList
	  3* catalogToolsMock.getSKUDetails(*_) >>> skuDetailVOList
	  3* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathCanadaSiteCode") >> siteCodes
	  1* inventoryToolsMock.updateSKUInventoryForAllSites(inventoryVOList)
	  testObj.getCatalogTools() >> catalogToolsMock
	 
	  expect:
		  testObj.decrementInventoryStock(inventoryVOArray)
}
  
  def "decrementInventoryStock | update all stock level inventory | BBBSystemException during invocation of getSKUDetails"(){
	  given:
	  InventoryVO[] inventoryVOArray=new InventoryVO[3];
	  
	  List<String> config = new ArrayList<String>()
	  config.add("TRUE");
	  
	  List<String> siteCodes = new ArrayList<String>()
	  siteCodes.add("BedBathCA")
	  
	  List<InventoryVO> inventoryVOList= new ArrayList<InventoryVO>()
	  InventoryVO inventoryVO1 = new InventoryVO()
	  inventoryVO1.setInventoryID("mockInvId_1")
	  inventoryVO1.setGlobalStockLevel(10)
	  inventoryVO1.setSiteStockLevel(11)
	  inventoryVO1.setGiftRegistryStockLevel(12)
	  inventoryVO1.setSiteID("BedBathUS")
	  inventoryVO1.setSkuID("mockedSkuId_1")
	  inventoryVO1.setOrderedQuantity(10) //order Quantity = [ global stock]
	  inventoryVOList.add(inventoryVO1)
	  
	  List<SKUDetailVO> skuDetailVOList = new ArrayList<SKUDetailVO>()
	  
	  SKUDetailVO skuDetailVO1 = new SKUDetailVO()
	  skuDetailVO1.setVdcSku(false)
	  skuDetailVO1.setEcomFulfillment("E")
	  skuDetailVOList.add(skuDetailVO1)
	  
	 
	 
	  1* catalogToolsMock.getContentCatalogConfigration(BBBCoreConstants.UPDATE_ALL_INVENTORY_CONFIG) >> config
	  1* inventoryToolsMock.getSKUInventory(inventoryVOArray) >> inventoryVOList
	  1* catalogToolsMock.getSKUDetails(*_) >> { throw new BBBSystemException("BBBSystemException during invocation of getSKUDetails")}
	  testObj.getCatalogTools() >> catalogToolsMock
	  expect:
		  testObj.decrementInventoryStock(inventoryVOArray)
}
  
  
  
  def "decrementInventoryStock | update all stock level inventory | BBBBusinessException during invocation of getSKUDetails"(){
	  given:
	  InventoryVO[] inventoryVOArray=new InventoryVO[3];
	  
	  List<String> config = new ArrayList<String>()
	  config.add("TRUE");
	  
	  List<String> siteCodes = new ArrayList<String>()
	  siteCodes.add("BedBathCA")
	  
	  List<InventoryVO> inventoryVOList= new ArrayList<InventoryVO>()
	  InventoryVO inventoryVO1 = new InventoryVO()
	  inventoryVO1.setInventoryID("mockInvId_1")
	  inventoryVO1.setGlobalStockLevel(10)
	  inventoryVO1.setSiteStockLevel(11)
	  inventoryVO1.setGiftRegistryStockLevel(12)
	  inventoryVO1.setSiteID("BedBathUS")
	  inventoryVO1.setSkuID("mockedSkuId_1")
	  inventoryVO1.setOrderedQuantity(10) //order Quantity = [ global stock]
	  inventoryVOList.add(inventoryVO1)
	  
	  List<SKUDetailVO> skuDetailVOList = new ArrayList<SKUDetailVO>()
	  
	  SKUDetailVO skuDetailVO1 = new SKUDetailVO()
	  skuDetailVO1.setVdcSku(false)
	  skuDetailVO1.setEcomFulfillment("E")
	  skuDetailVOList.add(skuDetailVO1)
	  
	 
	 
	  1* catalogToolsMock.getContentCatalogConfigration(BBBCoreConstants.UPDATE_ALL_INVENTORY_CONFIG) >> config
	  1* inventoryToolsMock.getSKUInventory(inventoryVOArray) >> inventoryVOList
	  1* catalogToolsMock.getSKUDetails(*_) >> { throw new BBBBusinessException("BBBBusinessException during invocation of getSKUDetails")}
	  testObj.getCatalogTools() >> catalogToolsMock
	  expect:
		  testObj.decrementInventoryStock(inventoryVOArray)
}
  
  
  def "decrementInventoryStock | exception handling for getContentCatalogConfigration"(){
	  given:
	  	  InventoryVO[] inventoryVOArray=new InventoryVO[3];
		  1* catalogToolsMock.getContentCatalogConfigration(BBBCoreConstants.UPDATE_ALL_INVENTORY_CONFIG) >>  {throw new Exception("Exception")}
	  when:
	  	  testObj.getCatalogTools() >> catalogToolsMock
		  testObj.decrementInventoryStock(inventoryVOArray)
	  then:
	  Exception exception = thrown()
}
  
def "decrementInventoryStock | check with null inventoryVos"(){
	  given:
		InventoryVO[] inventoryVOArray=null
		  
	  expect:
		  
		  testObj.decrementInventoryStock(inventoryVOArray)
	
}

/***************************************************************************************************************************************************************
 ---------------------------------------------------    invalidateItemInventoryCache(String pSkuId, String pSiteId)  --------------------------------------------
 ***************************************************************************************************************************************************************/

def "invalidateItemInventoryCache  | remove Item from repository Cache by SKU"(){
	given:
	  		RqlStatement rqlStatementMock = Mock()
			ItemDescriptorImpl cacheInvItemDesc = Mock()
			inventoryRepositoryMock.getItemDescriptor(BBBCatalogConstants.CACHED_INVENTORY_ITEM_DESCRIPTOR)>> cacheInvItemDesc
			
			ItemDescriptorImpl cacheInvTranslationItemDesc = Mock()
			inventoryRepositoryMock.getItemDescriptor(BBBCatalogConstants.ITEM_CACHED_TRANSLATIONS_INVENTORY_PROPERTY_NAME)>> cacheInvTranslationItemDesc
			
			inventoryToolsMock.getInventoryRepository() >> inventoryRepositoryMock
			
			
			// inventory translations
			
			String mockedInventoryTranslationId="Mocked_InventoryTranslationId_001"
			String mockedInventoryTranslationDisplayName= "Mocked_InventoryIdTranslation_001_displayName"
			String mockedInventoryTranslationDescription= "Mocked_InventoryIdTranslation_001_description"
			
			RepositoryItemMock inventoryItemTranslationMock = Mock(["id":mockedInventoryTranslationId])
			inventoryItemTranslationMock.getRepositoryId() >> mockedInventoryTranslationId
			inventoryItemTranslationMock.getPropertyValue(BBBCatalogConstants.CATALOG_REF_ID_INVENTORY_PROPERTY_NAME) >> mockedSkuId
			inventoryItemTranslationMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_INVENTORY_PROPERTY_NAME) >> mockedInventoryTranslationDisplayName
			inventoryItemTranslationMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_INVENTORY_PROPERTY_NAME) >> mockedInventoryTranslationDescription
			
			
			Map<String ,RepositoryItemMock> invTranslation = new HashMap<String ,RepositoryItemMock>();
			invTranslation.put(mockedInventoryTranslationId, inventoryItemTranslationMock)
			
			String mockedInventoryId="Mocked_InventoryId_001"
			String mockedInventoryDisplayName= "Mocked_InventoryId_001_displayName"
			String mockedInventoryDescription= "Mocked_InventoryId_001_description"	
					
			RepositoryItemMock inventoryItemMock = Mock(["id":mockedInventoryId])
			inventoryItemMock.getRepositoryId() >> mockedInventoryId
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.CATALOG_REF_ID_INVENTORY_PROPERTY_NAME) >> mockedSkuId
			
			RepositoryItemMock[] inventoryItemsMock = [inventoryItemMock]
			inventoryRepositoryMock.getView("inventory") >> viewMock
			
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_INVENTORY_PROPERTY_NAME) >> mockedInventoryDisplayName
			inventoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_INVENTORY_PROPERTY_NAME) >> mockedInventoryDescription 
			inventoryItemMock.getPropertyValue("translations") >> invTranslation
			
			testObj.setQueryString(queryString)
			testObj.getInventoryRqlStatement(queryString) >> rqlStatementMock
			1* rqlStatementMock.executeQuery(*_) >> inventoryItemsMock
			
			1* cacheInvItemDesc.removeItemFromCache(inventoryItemsMock[0].getRepositoryId())
			1* cacheInvTranslationItemDesc.removeItemFromCache(inventoryItemTranslationMock.getRepositoryId(),true)
			
		expect:
			testObj.invalidateItemInventoryCache(mockedSkuId, NA)
 }


def "invalidateItemInventoryCache  | remove Item from repository Cache by SKU| SKU does not exist in repository"(){
	given:	
			RqlStatement rqlStatementMock = Mock()
			inventoryToolsMock.getInventoryRepository() >> inventoryRepositoryMock
			
			testObj.setQueryString(queryString)
			testObj.getInventoryRqlStatement(queryString) >> rqlStatementMock
			1* rqlStatementMock.executeQuery(*_) >> null 
	when:
		testObj.invalidateItemInventoryCache(mockedSkuId,NA)
	then:
		BBBBusinessException bbbBusinessException = thrown()
 }
def "invalidateItemInventoryCache | handle  RepositoryException"(){
	
	given:
		
		inventoryRepositoryMock.getView("inventory") >> viewMock
		inventoryToolsMock.getInventoryRepository() >> repositoryMock
		testObj.setQueryString(queryString)
		testObj.executeRQLQuery(*_) >> { throw new RepositoryException("RepositoryException occured during the invocation of executeQuery") }
	  when:
		 testObj.invalidateItemInventoryCache(mockedSkuId, NA)
	then:
	 thrown(BBBSystemException)
		
}

def "invalidateItemInventoryCache  | invalidateItemInventoryCache with null[do not pass skuId]"(){
	given:
	  InventoryVO[] inventoryVOArray=null
	 when:
		testObj.invalidateItemInventoryCache(null,NA)
	then:
		Exception exception = thrown()
}

  
}
