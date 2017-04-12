/**
 * 
 */
package com.bbb.importprocess.manager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.RepositoryException;

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBStringUtils;
import com.bbb.importprocess.vo.StofuImagesVO;

/**
 * 
 * 
 */
public class BBBCatalogImportManager extends AbstractBBBImportManager {

  private static final String IMPORT_PROCESS_MANAGER = "importprocess_manager";

  // -------------------------------------
  // property: projectName
  // -------------------------------------

  // -----------------------------------------
  // methods
  // ----------------------------------------

  protected Date getDate() {

    java.util.Date date = new java.util.Date();
    long t = date.getTime();
    java.sql.Date sqlDate = new java.sql.Date(t);
    // java.sql.Time sqlTime = new java.sql.Time(t);
    // java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(t);
    return sqlDate;
  }

  protected long getExecutionTime(Date pStartDate) {

    java.util.Date date = new java.util.Date();
    long t = date.getTime() - pStartDate.getTime();
    long diffSeconds = t / 1000;
    // java.sql.Time sqlTime = new java.sql.Time(t);
    // java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(t);
    return diffSeconds;
  }

  /**
   * This method is used to import data to version repository feed feed is
   * executed
   * 
   * @param pFeedType
   *          Current feed type
   * @param pFeedIds
   *          List of feedIds which is going to be imported
   * @throws BBBSystemException
   * @throws BBBBusinessException
   * @throws SQLException
   * 
   */

  protected void importData(final String pFeedType, final List<String> pFeedIds, final Connection pConnection,
      final boolean isProductionImport) throws BBBSystemException {

	  Timestamp startTime = null;
	  int isDirectToStaging = getImportTools().isDirectToStaging() ? 0 : 1;
    // updated the states
    for (String feedId : pFeedIds) {

      // Get all the active sites from catalog load tools

      final List<String> siteList = getImportTools().getActiveSites();

      if (isLoggingDebug()) {
        logDebug("Active Site list " + siteList);
      }
      // Updating Dept Properties
      updateDept(feedId, pConnection);

      // Updating SubDept Properties
      updateSubDept(feedId, pConnection);

      // Updating Brand Properties
      updateBrand(feedId, pConnection);

      // Updating Class Properties
      updateClass(feedId, pConnection);

      // Updating SkuThreshold Properties
      updateSkuThreshold(feedId, pConnection);

      // Update Product or Sku properties
      clearProdChildSkuUpdateRequiredList();
      startTime = new Timestamp(getDate().getTime());
      updateItem(feedId, pConnection);
      int numRowsItemsFrequent = getPimFeedTools().getNumRowsForECPTable("ECP_ITEMS_FREQUENT", feedId, pConnection);
      int numRowsItemsRare = getPimFeedTools().getNumRowsForECPTable("ECP_ITEMS_RARE", feedId, pConnection);
      int totalItemRows = numRowsItemsFrequent + numRowsItemsRare;
      getPimFeedTools().updateFeedTimeDetails(feedId, "ECP_ITEMS", totalItemRows, startTime, new Timestamp(getDate().getTime()), isDirectToStaging, pConnection);

      // update product keywords - Added for R2.2 Start
      updateProductKeywords(feedId, pConnection);
      // update product keywords - Added for R2.2 End
      
      // Associate Product with Sku
      associateProductWithSku(feedId, pConnection, isProductionImport);

// Start Commented for R 2.2 T02 --> Create New Pricing Feed      
      // Update Pricing
      // updatePricing(feedId, pConnection);
// End Commented for R 2.2 T02 --> Create New Pricing Feed
      
      
      // Updating Rebate Info Properties
      updateRebateInfo(feedId, pConnection);

      // Associate Sku Attributes with Sku
      associateRebateRelnWithSku(feedId, pConnection, isProductionImport);

      // Update Attributes Properties
      updateAttributes(feedId, pConnection);

      // Associate Sku Attributes with Sku
      associateSkuAttributesRelnWithSku(feedId, pConnection, isProductionImport);

      // Updating Product Tabs Properties
      updateProductTabs(feedId, pConnection, isProductionImport);

      if (siteList == null || siteList.isEmpty()) {

        throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_1006,"Site is not configured in the component");
      }
      // Update Category
      updateCategory(feedId, pConnection, isProductionImport);

      // Associate Category With SubCategories
      associateCategoryWithSubCategory(feedId, pConnection, isProductionImport);

      clearCategoryChildProdUpdateRequiredList();
      startTime = new Timestamp(getDate().getTime());
      for (String siteId : siteList) {

        if (isLoggingDebug()) {
          logDebug("Current Site Id= " + siteId);
        }

        // AssociateCategory with Root Category
        associateCategoriesWithRootCategory(feedId, siteId, pConnection, isProductionImport);

        // Associate Product With Category
        associateProductsWithCategory(feedId, siteId, pConnection, isProductionImport);
      }

      int numRowsNodeSku = getPimFeedTools().getNumRowsForECPTable("ECP_NODE_SKU", feedId, pConnection);
      getPimFeedTools().updateFeedTimeDetails(feedId, "ECP_NODE_SKU", numRowsNodeSku, startTime, new Timestamp(getDate().getTime()), isDirectToStaging, pConnection);

      // Updating and associate Eko sku with sku
      associateEcoSkuWithSku(feedId, pConnection);
        //updating Zip Code Regions
      updateRegionZIPCodes(feedId, pConnection);
      
     // Updating and associate sku with zip code 
      associateSkuWithRegion(feedId, pConnection);
      
      
      // Updating Media Properties
      updateMedia(feedId, pConnection);

      // Associate Media with Product
      associateMediaRelnWithProduct(feedId, pConnection, isProductionImport);
      //updating stofu images 
      updateStofuImages(feedId, pConnection);
    }
    /* updateState(pFeedType, currentState, pFeedIds) */
  }


/**
   * This method update category details for provided pFeedId and pSiteId
   * 
   * @param pFeedId
   * @param pSiteId
   * @throws BBBSystemException
   * @throws BBBBusinessException
   */
  protected void updateCategory(final String pFeedId, final Connection pConnection, final boolean isProductionImport) throws BBBSystemException {
	  
	  if (isLoggingDebug()) {
	     logDebug("updateCategory start");
	  }
    // Create & update all categories
    boolean rollback = true;
    boolean isMonitorCanceled = false;
    // final Calendar calendar = getCalendar();
    final Date startTime = getDate();
    List<String> batchCategoryIdsList;
    int batchCount = 1;
    final List<String> categoryIdsSuccessList = new ArrayList<String>();
    final TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
    BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "updateCategory");

    // get data from PIM categories
    final List<String> categoryIds = getPimFeedTools().getPIMAllCategories(pFeedId, pConnection);
    
    if (isLoggingDebug()) {
       logDebug("   categoryIds = "  + categoryIds);
    }
    String categoryId = null;

    if (categoryIds != null && !categoryIds.isEmpty()) {
      if (isLoggingDebug()) {
        logDebug("No of CategoryIds=" + categoryIds.size());
      }
      // update log into Feed tables
      getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_NODES", formatDate(startTime), formatDate(startTime), 0,
          "Category Creation started for feedId=" + pFeedId, "", pConnection);
      final List<List<String>> batchLists = getImportTools().getBatchList(categoryIds);
      for (List<String> categorySubList : batchLists) {

        batchCategoryIdsList = categorySubList;
        if (isLoggingDebug()) {

          logDebug(new StringBuffer().append("categorySubList =").append(categorySubList).append(" Batch Count=")
              .append(batchCount).append(" Feed Id=").append(pFeedId).toString());
        }

        try {

          transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

          for (String id : batchCategoryIdsList) {

            categoryId = id;
            if (isLoggingDebug()) {
               logDebug("   call createUpdateCategory with  pFeedId=" + pFeedId +" categoryId=" + categoryId);
            }
            getImportTools().createUpdateCategory(pFeedId, categoryId, pConnection, isProductionImport);
            categoryIdsSuccessList.add(categoryId);
            if (isLoggingDebug()) {
               logDebug("   added to success list = " + categoryId);
            }
          }
          batchCount++;
          rollback = false;
        } catch (TransactionDemarcationException e) {

          rollback = true;
          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          BBBPerformanceMonitor.cancel("updateCategory");
          isMonitorCanceled = true;
        } catch (BBBBusinessException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          rollback = false;
          getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_NODES", "Category not created for batch=" +batchCount, getImportTools().getDate(), batchLists.toString(), pConnection);
          BBBPerformanceMonitor.cancel("updateCategory");
          isMonitorCanceled = true;
        } catch (IllegalArgumentException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          BBBPerformanceMonitor.cancel("updateCategory");
          isMonitorCanceled = true;
          throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_ILLEGAL_ARGUMENT_EXC,"IllegalArgumentException", e);
        } finally {
          if (!isMonitorCanceled) {
            BBBPerformanceMonitor.end(IMPORT_PROCESS_MANAGER, "updateCategory");
          }
          try {
            transactionDemarcation.end(rollback);
          } catch (TransactionDemarcationException e) {

            if (isLoggingError()) {

              logError(BBBStringUtils.stack2string(e));
            }
          }
        }
      }

      int noOfRecordsSuccess = categoryIdsSuccessList.size();
      int totalNoOfRecords = categoryIds.size();
      if (!categoryIds.isEmpty() && !categoryIdsSuccessList.isEmpty()) {

        if (getPimFeedTools().hasFeedBadRecords(pFeedId, pConnection)) {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess)
                .append("Records processed sucessfully with error for Total ").append(totalNoOfRecords)
                .append(" Records in time").append(getExecutionTime(startTime)).append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_NODES", formatDate(startTime), formatDate(getDate()), noOfRecordsSuccess,
              "Created Category completed with some errors", "", pConnection);
        } else {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess).append("Records processed sucessfully for Total ")
                .append(totalNoOfRecords).append(" Records in time").append(getExecutionTime(startTime))
                .append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_NODES", formatDate(startTime), formatDate(getDate()), noOfRecordsSuccess,
              "Created Category Successfully", "", pConnection);
        }

      }

    } else {

      if (isLoggingDebug()) {

        logDebug("No data found for Update Category feedId= " + pFeedId);
      }
    }
    if (isLoggingDebug()) {

      logDebug("End of updateCategory ");
    }
        
  }

  /**
   * 
   * @param pFeedId
   * @param pSiteId
   * @throws BBBSystemException
   * @throws BBBBusinessException
   */
  protected void associateCategoryWithSubCategory(final String pFeedId, final Connection pConnection, final boolean isProductionImport)
      throws BBBSystemException {
    // Create & update all categories
    boolean rollback = true;
    boolean isMonitorCanceled = false;
    // final Calendar calendar = getCalendar();
    // final Date startTime = getCurrentTime(calendar);
    final Date startTime = getDate();
    final List<String> parentCategoryIdsSuccessList = new ArrayList<String>();
    final List<String> errorList = new ArrayList<String>();
    final TransactionDemarcation transaction = new TransactionDemarcation();
    int batchCount = 1;
    if (isLoggingDebug()) {
      logDebug("Start associateCategoryWithSubCategory - Category- Subcategory Association");
    }
    BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "associateCategoryWithSubCategory");
    // Get Unique Parent Category data from PIM Table ECP_NODE_PARENT
    final List<String> parentCategoryIds = getPimFeedTools().getUniqueCategories(pFeedId, pConnection);

    if (parentCategoryIds != null && !parentCategoryIds.isEmpty()) {

      if (isLoggingDebug()) {
        logDebug("No of parentCategoryIds=" + parentCategoryIds.size());
      }
      // update log into Feed tables
      getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_NODE_PARENT", formatDate(startTime), formatDate(startTime), 0,
          "Category SubCategory Association started for feedId=" + pFeedId, "", pConnection);
      // Split data into batches
      final List<List<String>> batchLists = getImportTools().getBatchList(parentCategoryIds);

      for (List<String> categorySubList : batchLists) {

        if (isLoggingDebug()) {

          logDebug(new StringBuffer().append("Associate Category Batch =").append(categorySubList)
              .append(" Batch Count=").append(batchCount).append(" Feed Id=").append(pFeedId).toString());
        }
        try {

          transaction.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

          for (String categoryId : categorySubList) {

            getImportTools().associateCategoryWithSubCategory(pFeedId, categoryId, errorList, pConnection, isProductionImport);
            parentCategoryIdsSuccessList.add(categoryId);
          }
          batchCount++;
          rollback = false;
        } catch (TransactionDemarcationException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          rollback = true;
          BBBPerformanceMonitor.cancel("associateCategoryWithSubCategory");
          isMonitorCanceled = true;
        } catch (BBBBusinessException e) {
          getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_NODE_PARENT", "Category SubCategory not created for batch=" +batchCount, getImportTools().getDate(), batchLists.toString(), pConnection);
          rollback = false;
          BBBPerformanceMonitor.cancel("associateCategoryWithSubCategory");
          isMonitorCanceled = true;
        } finally {
          if (!isMonitorCanceled) {
            BBBPerformanceMonitor.end(IMPORT_PROCESS_MANAGER, "associateCategoryWithSubCategory");
          }
          try {
            transaction.end(rollback);
          } catch (TransactionDemarcationException e) {

            if (isLoggingError()) {

              logError(BBBStringUtils.stack2string(e));
            }
          }
        }
      }

      int noOfRecordsSuccess = parentCategoryIdsSuccessList.size();
      int totalNoOfRecords = parentCategoryIds.size();
      if (!parentCategoryIds.isEmpty() && !parentCategoryIdsSuccessList.isEmpty()) {

        if (getPimFeedTools().hasFeedBadRecords(pFeedId, pConnection)) {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess)
                .append("Records processed sucessfully with error for Total ").append(totalNoOfRecords)
                .append(" Records in time").append(getExecutionTime(startTime)).append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_NODE_PARENT", formatDate(startTime), formatDate(getDate()), noOfRecordsSuccess,
              "associateCategoryWithSubCategory completed with some errors", "", pConnection);
        } else {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess).append("Records processed sucessfully for Total ")
                .append(totalNoOfRecords).append(" Records in time").append(getExecutionTime(startTime))
                .append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_NODE_PARENT", formatDate(startTime), formatDate(getDate()), noOfRecordsSuccess,
              "associateCategoryWithSubCategory completed", "", pConnection);
        }

      }

    } else {

      if (isLoggingDebug()) {

        logDebug("No data found for Category - SubCategory Association for feedId= " + pFeedId);
      }
    }
    if (isLoggingDebug()) {

      logDebug("End of Category Subcategory Association ");
    }

  }

  /**
   * Method get Frequent Item Ids List and Rare Item Ids List and join both list
   * as unique ids using set and return unique ids list
   * 
   * @param pFrequentItemIdsList
   * @param pRareItemIdsList
   * @return
   */
  private List<String> getItemIdsList(final List<String> pFrequentItemIdsList, final List<String> pRareItemIdsList) {

    Set<String> set = new HashSet<String>();
    set.addAll(pFrequentItemIdsList);
    set.addAll(pRareItemIdsList);

    return new ArrayList<String>(set);
  }

  /**
   * This method associates products with categories for provided pFeedId and
   * pSiteId
   * 
   * @param pFeedId
   * @param pSiteId
   * @throws BBBSystemException
   * @throws BBBBusinessException
   */
  protected void associateProductsWithCategory(final String pFeedId, final String pSiteId, final Connection pConnection, final boolean isProductionImport)
      throws BBBSystemException {
	  
	  if (isLoggingDebug()) {
	     logDebug("associateProductsWithCategory() Start,  date=" + formatDate(getDate()) + ", feedId = " + pFeedId +  ", siteId = " + pSiteId);
	  }
    // Create & update all categories
    boolean rollback = true;
    boolean isMonitorCanceled = false;
    // final Calendar calendar = getCalendar();
    // final Date startTime = getCurrentTime(calendar);
    final Date startTime = getDate();
    final List<String> categoryIdsSuccessList = new ArrayList<String>();
    final List<String> errorList = new ArrayList<String>();
    List<String> batchCategoryIdsList;

    final TransactionDemarcation transaction = new TransactionDemarcation();

    // update log into Feed tables
    // getPimFeedTools().updateFeedLog(pFeedId, "ECP_NODE_PARENT", startTime,"",
    // "Category Creation started", "");

    if (isLoggingDebug()) {
      logDebug("Start associateProductsWithCategory - Category-Product Association");
    }
    BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "associateProductsWithCategory");
    // get data from PIM categories
    
    final List<String> categoryIds = getPimFeedTools().getUniqueCategoriesForProducts(pFeedId, pSiteId, pConnection);

    String categoryId = null;
    int batchCount = 1;
    if (isLoggingDebug()) {
      logDebug("associateProductsWithCategory() categoryIds" + categoryIds);
      logDebug("associateProductsWithCategory() for  site="+ pSiteId + ", number of categories=" + categoryIds.size());
    }

    if (categoryIds != null && !categoryIds.isEmpty()) {

      final List<List<String>> batchLists = getImportTools().getBatchList(categoryIds);

      for (List<String> categorySubList : batchLists) {

        batchCategoryIdsList = categorySubList;
        if (isLoggingDebug()) {

          logDebug(new StringBuffer().append("associateProductsWithCategory() categorySubList=").append(categorySubList).append(" Batch Count=")
              .append(batchCount).append(" Feed Id=").append(pFeedId).toString());
        }
        try {

          transaction.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);
          for (String id : batchCategoryIdsList) {

            categoryId = id;
            logDebug("associateProductsWithCategory()  Site Id:" +pSiteId +" - id: "+id +" - pFeedId:" +pFeedId +" - errorList:"+errorList +" - isProduction Import:"+isProductionImport);
            try {
	            getImportTools().associateProductsWithCategory(pSiteId, id, pFeedId, errorList, pConnection, isProductionImport);
	            if (isLoggingDebug()) {
	               logDebug("associateProductsWithCategory() Successfully associated");
	            }
	            categoryIdsSuccessList.add(categoryId);
            } catch (BBBBusinessException e) {

	            if (isLoggingError()) {

	              logError(BBBStringUtils.stack2string(e));
	            }
	            getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_NODE_SKU", "Category Subcategory not created for batch=" +batchCount, getImportTools().getDate(), "Exception occurred for category " + id + " in batch " + batchCategoryIdsList, pConnection);
	            //BBBPerformanceMonitor.cancel("associateProductsWithCategory");
	          }
          }
          batchCount++;
          rollback = false;
        } catch (TransactionDemarcationException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          logDebug("Some TransactionDemarcationException");
          rollback = true;
          BBBPerformanceMonitor.cancel("associateProductsWithCategory");
          isMonitorCanceled = true;
        } /*catch (BBBBusinessException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          logDebug("Some Business Exception");
          getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_NODE_SKU", "Category Subcategory not created for batch=" +batchCount, getImportTools().getDate(), batchLists.toString(), pConnection);
          rollback = false;
          BBBPerformanceMonitor.cancel("associateProductsWithCategory");
          isMonitorCanceled = true;
        }*/ finally {
          if (!isMonitorCanceled) {
            BBBPerformanceMonitor.end(IMPORT_PROCESS_MANAGER, "associateProductsWithCategory");
          }
          try {
            transaction.end(rollback);
          } catch (TransactionDemarcationException e) {

            if (isLoggingError()) {

              logError(BBBStringUtils.stack2string(e));
            }
          }
        }
      }

      int noOfRecordsSuccess = categoryIdsSuccessList.size();
      int totalNoOfRecords = categoryIds.size();
      if (isLoggingDebug()) {
         logDebug("CategoryIds: "+categoryIds +" - CategoryIds Success List:"+categoryIdsSuccessList);
         logDebug("noOfRecordsSuccess: "+noOfRecordsSuccess +" - totalNoOfRecords:"+totalNoOfRecords);
      }
      if (!categoryIds.isEmpty() && !categoryIdsSuccessList.isEmpty()) {

        if (getPimFeedTools().hasFeedBadRecords(pFeedId, pConnection)) {

          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess)
                .append("Records processed sucessfully with error for Total ").append(totalNoOfRecords)
                .append(" Records in time").append(getExecutionTime(startTime)).append(" secs").toString());
          }

          if (isLoggingDebug()) {
             logDebug("   updating  log with  success with error  message");
          }
          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_NODE_SKU", formatDate(startTime), formatDate(getDate()), noOfRecordsSuccess,
              "associateProductsWithCategory completed with some errors", "", pConnection);
        } else {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess).append("Records processed sucessfully for Total ")
                .append(totalNoOfRecords).append(" Records in time").append(getExecutionTime(startTime))
                .append(" secs").toString());
          }

          if (isLoggingDebug()) {
             logDebug("   updating  log with  success with only success message");
          }
          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_NODE_SKU", formatDate(startTime), formatDate(getDate()), noOfRecordsSuccess,
              "associateProductsWithCategory completed", "", pConnection);
        }

      }

    } else {

      if (isLoggingDebug()) {
    	  logDebug("No data found for associateProductsWithCategory - Category-Product Association feedId= " + pFeedId);
      }
    }
    if (isLoggingDebug()) {
      logDebug("End of associateProductsWithCategory - Category-Product Association");
      logDebug("associateProductsWithCategory() End,  date=" + formatDate(getDate()) + ", feedId = " + pFeedId +  ", siteId = " + pSiteId);
    }
    
    for(int i = 0; i < getImportTools().getCategoryChildProdUpdateRequiredList().size(); i++) {
         getImportTools().updateLastModifiedDate(getImportTools().getCategoryChildProdUpdateRequiredList().get(i));
    }
     clearCategoryChildProdUpdateRequiredList();
  }

  /**
   * This method associates products with categories for provided pFeedId and
   * pSiteId
   * 
   * @param pFeedId
   * @param pSiteId
   * @throws BBBSystemException
   * @throws BBBBusinessException
   * @throws SQLException
   */
  private void associateProductWithSku(final String pFeedId, final Connection pConnection, final boolean isProductionImport) throws BBBSystemException {

    // Create & update all categories
    boolean rollback = true;
    boolean isMonitorCanceled = false;
    // final Calendar calendar = getCalendar();
    // final Date startTime = getCurrentTime(calendar);
    final Date startTime = getDate();
    final List<String> productIdsSuccessList = new ArrayList<String>();

    List<String> batchCategoryIdsList;

    final TransactionDemarcation transaction = new TransactionDemarcation();
    BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "associateProductWithSku");
    // update log into Feed tables
    // getPimFeedTools().updateFeedLog(pFeedId, "ECP_NODE_PARENT", startTime,"",
    // "Category Creation started", "");

    if (isLoggingDebug()) {
      logDebug("Start associateProductWithSku - Product-Sku Association");
    }

    // Get PIM Unique Product from Table
    final List<String> productIdList = getPimFeedTools().getUniqueProdSkuRelIds(pFeedId, pConnection);
    String productId = null;
    int batchCount = 1;
    if (productIdList != null && !productIdList.isEmpty()) {

      final List<List<String>> batchLists = getImportTools().getBatchList(productIdList);
      final List<String> pBadProductMediaRelationShip = new ArrayList<String>();
      for (List<String> idList : batchLists) {

        batchCategoryIdsList = idList;
        if (isLoggingDebug()) {

          logDebug(new StringBuffer().append("productIdSubList=").append(idList).append(" Batch Count=")
              .append(batchCount).append(" Feed Id=").append(pFeedId).toString());
        }
        try {

          transaction.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

          for (String id : batchCategoryIdsList) {

            productId = id;
            getImportTools().associateProductWithSku(pFeedId, id, pBadProductMediaRelationShip, pConnection, isProductionImport);
            productIdsSuccessList.add(productId);
          }
          batchCount++;
          rollback = false;
        } catch (TransactionDemarcationException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_RELATED_ITEMS", "Product -Product -Sku Relationship not done for batch=" +batchCount, getImportTools().getDate(), batchLists.toString(), pConnection);
          rollback = true;
          BBBPerformanceMonitor.cancel("associateProductWithSku");
          isMonitorCanceled = true;
        } catch (BBBBusinessException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          rollback = false;
          BBBPerformanceMonitor.cancel("associateProductWithSku");
          isMonitorCanceled = true;
        } finally {
          if (!isMonitorCanceled) {
            BBBPerformanceMonitor.end(IMPORT_PROCESS_MANAGER, "associateProductWithSku");
          }
          try {
            transaction.end(rollback);
          } catch (TransactionDemarcationException e) {

            if (isLoggingError()) {

              logError(BBBStringUtils.stack2string(e));
            }
          }
        }
      }

      int noOfRecordsSuccess = productIdsSuccessList.size();
      int totalNoOfRecords = productIdList.size();
      if (!productIdList.isEmpty() && !productIdsSuccessList.isEmpty()) {

        if (getPimFeedTools().hasFeedBadRecords(pFeedId, pConnection)) {
          if (isLoggingDebug()) {

            logDebug(+noOfRecordsSuccess + "Records processed sucessfully with error for Total" + totalNoOfRecords
                + "Records");
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_RELATED_ITEMS", formatDate(startTime), formatDate(getDate()), noOfRecordsSuccess,
              "associateProductWithSku completed with some errors", "", pConnection);
        } else {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess).append("Records processed sucessfully for Total ")
                .append(totalNoOfRecords).append(" Records in time").append(getExecutionTime(startTime))
                .append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_RELATED_ITEMS", formatDate(startTime), formatDate(getDate()), noOfRecordsSuccess,
              "associateProductWithSku completed", "", pConnection);
        }

      }

    } else {

      if (isLoggingDebug()) {

        logDebug("No data found for associateProductWithSku - Product-Sku Association feedId= " + pFeedId);
      }
    }
    if (isLoggingDebug()) {

      logDebug("End of associateProductWithSku - Product-Sku Association");
    }

  }

  /**
   * Associate Root Category with SubCategories
   * 
   * @param pFeedId
   * @param pSiteId
   * @throws BBBSystemException
   * @throws BBBBusinessException
   */
  protected void associateCategoriesWithRootCategory(final String pFeedId, final String pSiteId,
      final Connection pConnection, final boolean isProductionImport) throws BBBSystemException {
    // Associate Root Categories
    boolean rollback = true;
    boolean isMonitorCanceled = false;
    // final Calendar calendar = getCalendar();
    // final Date startTime = getCurrentTime(calendar);
    final Date startTime = getDate();
    final List<String> categoryIdsSuccessList = new ArrayList<String>();
    final List<String> categoryIdsErrorList = new ArrayList<String>();
    String categoryId = null;
    List<String> catId = new ArrayList<String>();

    final TransactionDemarcation transaction = new TransactionDemarcation();
    BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "associateCategoriesWithRootCategory");
    // update log into Feed tables
    // getPimFeedTools().updateFeedLog(pFeedId, "ECP_NODE_PARENT", startTime,"",
    // "Category Creation started", "");

    if (isLoggingDebug()) {
      logDebug("Start  associateCategoriesWithRootCategory Root Category - Subcategory Association");
    }

    try {

      transaction.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

      categoryId = (String) getImportTools().getSiteIdRootCategoryIdMap().get(pSiteId);

      catId = getImportTools().getConfigTools().getAllValuesForKey("ContentCatalogKeys", categoryId);
      if (isLoggingDebug()) {
        logDebug("categoryId=" + categoryId);
      }

      String CategoryId = catId.get(0);
      if (!StringUtils.isEmpty(CategoryId)) {

        getImportTools().associateCategoriesWithRootCategory(pSiteId, pFeedId, CategoryId, categoryIdsErrorList,
            pConnection, isProductionImport);
      } else {
        if (isLoggingDebug()) {
          logDebug("No Category configured for current site. Please configure Root category in BBBCatalogLoadTools.siteRootCategoryIdMap property");
        }
      }
      rollback = false;
    } catch (TransactionDemarcationException e) {

      if (isLoggingError()) {

        logError(BBBStringUtils.stack2string(e));
      }
      rollback = true;
      BBBPerformanceMonitor.cancel("associateCategoriesWithRootCategory");
      isMonitorCanceled = true;
    } catch (BBBBusinessException e) {

      if (isLoggingError()) {

        logError(BBBStringUtils.stack2string(e));
      }
      rollback = true;
      BBBPerformanceMonitor.cancel("associateCategoriesWithRootCategory");
      isMonitorCanceled = true;
    } finally {
      if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(IMPORT_PROCESS_MANAGER, "associateCategoriesWithRootCategory");
      }
      try {
        transaction.end(rollback);
      } catch (TransactionDemarcationException e) {

        if (isLoggingError()) {

          logError(BBBStringUtils.stack2string(e));
        }
      }
    }

    if (!categoryIdsErrorList.isEmpty()) {

      getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_NODE", formatDate(startTime), formatDate(getDate()), categoryIdsSuccessList.size(),
          "associateRootCategoryWithSubCategory completed with some errors", "", pConnection);
    } else {

      getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_NODE", formatDate(startTime), formatDate(getDate()), categoryIdsSuccessList.size(),
          "associateRootCategoryWithSubCategory completed", "", pConnection);

    }

  }

  /**
   * This method update only the product keywords details
   * from PIM ECP_PRODUCTS_KEYWORDS table to catalog schema.
   * R2.2 story 92-A.
   * 
   * @param pFeedId
   * @param pConnection
   * @throws BBBSystemException
   * 
   */
  private void updateProductKeywords(String feedId, Connection pConnection) throws BBBSystemException {
	  
	  List<String> batchItemIdsList;
	  int batchCount = 1;
	  boolean rollback = true;
	  boolean isMonitorCanceled = false;
	  final Date startTime = getDate();
	  final List<String> itemIdsSuccessList = new ArrayList<String>();
	  final TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
	  BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "updateProductKeywords");

	  if(isLoggingDebug()){
		  logDebug("Start BBBCatalogImportManager.updateProductKeywords() method during PIM import process");
	  }

	  //fetch item ids from PIM keywords table.
	  final List<String> keywordsProdList = getPimFeedTools().getKeywordsProducts(feedId, pConnection);

	  if(keywordsProdList != null && !keywordsProdList.isEmpty()){
		  final List<List<String>> batchLists = getImportTools().getBatchList(keywordsProdList);
		  for (List<String> itemSubList : batchLists) {

			  batchItemIdsList = itemSubList;
			  if (isLoggingDebug()) {
				  logDebug(new StringBuffer().append("itemSubList=").append(itemSubList).append(" Batch Count=")
						  .append(batchCount).append(" Feed Id=").append(feedId).toString());
			  }

			  try {
				  transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

				  for(String productId : batchItemIdsList){
					  getImportTools().updateProdKeywordItem(feedId, productId, pConnection);
					  itemIdsSuccessList.add(productId);
				  }
				  batchCount++;
				  rollback = false;
			  } catch (TransactionDemarcationException e) {
				  logError(BBBStringUtils.stack2string(e));

				  rollback = true;
				  BBBPerformanceMonitor.cancel("updateProductKeywords");
				  isMonitorCanceled = true;
			  } finally {
				  if (!isMonitorCanceled) {
					  BBBPerformanceMonitor.end(IMPORT_PROCESS_MANAGER, "updateProductKeywords");
				  }
				  try {
					  transactionDemarcation.end(rollback);
				  } catch (TransactionDemarcationException e) {
					  if (isLoggingError()) {
						  logError(BBBStringUtils.stack2string(e));
					  }
					  if (isLoggingDebug()) {
						  logDebug(BBBStringUtils.stack2string(e));
					  }
				  }
			  }
		  }
		  int noOfRecordsSuccess = itemIdsSuccessList.size();
		  int totalNoOfRecords = keywordsProdList.size();
		  if (!keywordsProdList.isEmpty() && !itemIdsSuccessList.isEmpty()) {

			  if (getPimFeedTools().hasFeedBadRecords(feedId, pConnection)) {
				  if (isLoggingDebug()) {
					  logDebug(new StringBuffer().append(noOfRecordsSuccess)
							  .append("Records processed sucessfully with error for Total ").append(totalNoOfRecords)
							  .append(" Records in time").append(getExecutionTime(startTime)).append(" secs").toString());
				  }

				  getPimFeedTools().updateFeedLog(feedId, "", "ECP_PRODUCTS_KEYWORDS", formatDate(startTime), formatDate(getDate()), noOfRecordsSuccess,
						  "item updation completed with some errors", "", pConnection);
			  } else {
				  if (isLoggingDebug()) {

					  logDebug(new StringBuffer().append(noOfRecordsSuccess).append("Records processed sucessfully for Total ")
							  .append(totalNoOfRecords).append(" Records in time").append(getExecutionTime(startTime))
							  .append(" secs").toString());
				  }

				  getPimFeedTools().updateFeedLog(feedId, "", "ECP_PRODUCTS_KEYWORDS", formatDate(startTime), formatDate(getDate()), noOfRecordsSuccess,
						  "item updation completed", "", pConnection);
			  }
		  }

	  } else{
		  if(isLoggingDebug()){
			  logDebug("No items found in PIM keywords table for the feedId = " + feedId);
		  }
	  }
	  if (isLoggingDebug()) {
		  logDebug("End of BBBCatalogImportManager.updateProductKeywords() method during PIM import process");
	  }
  }
  
  /**
   * This method update item details for provided pFeedId
   * 
   * @param pFeedId
   * @param pSiteId
   * @throws BBBSystemException
   * @throws BBBBusinessException
   * @throws SQLException
   */
  protected void updateItem(final String pFeedId, final Connection pConnection) throws BBBSystemException {
	  
	  if (isLoggingDebug()) {
	     logDebug("updateItem() Start,  date=" + formatDate(getDate()) + ", feedId = " + pFeedId );
	  }
	  
    // Create & update all categories
    boolean rollback = true;
    boolean isMonitorCanceled = false;
    // final Calendar calendar = getCalendar();
    // final Date startTime = getCurrentTime(calendar);
    final Date startTime = getDate();
    List<String> batchItemIdsList;
    final List<String> itemIdsSuccessList = new ArrayList<String>();
    int batchCount = 1;
    final TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
    BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "updateItem");
    // update log into Feed tables
    // getBBBPIMFeedTools().updateFeedLog(feedId, "ECP_NODES", startTime,"",
    // "item Creation started", "");

    if (isLoggingDebug()) {
      logDebug("Start updateItem - Create/Update Product/Sku");
    }

    // get data from PIM categories
    final List<String> frequentItemIdsList = getPimFeedTools().getAllFrequentItems(pFeedId, pConnection);
    final List<String> rareItemIdsList = getPimFeedTools().getAllRareItems(pFeedId, pConnection);
    final List<String> itemIdsList = getItemIdsList(frequentItemIdsList, rareItemIdsList);

    if (itemIdsList != null && !itemIdsList.isEmpty()) {
      if (isLoggingDebug()) {
        logDebug("No of itemIds=" + itemIdsList.size());
      }
      final List<List<String>> batchLists = getImportTools().getBatchList(itemIdsList);
      for (List<String> itemSubList : batchLists) {

        batchItemIdsList = itemSubList;
        if (isLoggingDebug()) {

          logDebug(new StringBuffer().append("itemSubList=").append(itemSubList).append(" Batch Count=")
              .append(batchCount).append(" Feed Id=").append(pFeedId).toString());
        }

        try {

          transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

          for (String itemId : batchItemIdsList) {

            final boolean isFrequent = frequentItemIdsList.contains(itemId);
            final boolean isRare = rareItemIdsList.contains(itemId);
            getImportTools().updateItem(pFeedId, itemId, isFrequent, isRare, pConnection);
            itemIdsSuccessList.add(itemId);
          }
          batchCount++;
          rollback = false;
        } catch (TransactionDemarcationException e) {

          rollback = true;
          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          BBBPerformanceMonitor.cancel("updateItem");
          isMonitorCanceled = true;
        } catch (BBBBusinessException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));

          }
          // update Bad Records for current batch
          getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_ITEMS_FREQUENT", "Item not created for batch=" +batchCount, getImportTools().getDate(), batchItemIdsList.toString(), pConnection);
           rollback = false;
          BBBPerformanceMonitor.cancel("updateItem");
          isMonitorCanceled = true;
        } finally {
          if (!isMonitorCanceled) {
            BBBPerformanceMonitor.end(IMPORT_PROCESS_MANAGER, "updateItem");
          }
          try {
            transactionDemarcation.end(rollback);
          } catch (TransactionDemarcationException e) {

            if (isLoggingError()) {

              logError(BBBStringUtils.stack2string(e));

            }
            if (isLoggingDebug()) {

              logDebug(BBBStringUtils.stack2string(e));
            }
          }
        }
      }

      int noOfRecordsSuccess = itemIdsSuccessList.size();
      int totalNoOfRecords = itemIdsList.size();
      if (!itemIdsList.isEmpty() && !itemIdsSuccessList.isEmpty()) {

        if (getPimFeedTools().hasFeedBadRecords(pFeedId, pConnection)) {

          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess)
                .append("Records processed sucessfully with error for Total ").append(totalNoOfRecords)
                .append(" Records in time").append(getExecutionTime(startTime)).append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_ITEMS", formatDate(startTime), formatDate(getDate()), noOfRecordsSuccess,
              "item Creation completed with some errors", "", pConnection);
        } else {

          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess).append("Records processed sucessfully for Total ")
                .append(totalNoOfRecords).append(" Records in time").append(getExecutionTime(startTime))
                .append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_ITEMS", formatDate(startTime), formatDate(getDate()), noOfRecordsSuccess,
              "item Creation completed", "", pConnection);

        }
      }

    } else {

      if (isLoggingDebug()) {

        logDebug("No data found for updateItem - Create/Update Product/Sku feedId= " + pFeedId);
      }
    }
    if (isLoggingDebug()) {
      logDebug("End of updateItem - Create/Update Product/Sku");
      logDebug("updateItem() End,  date=" + formatDate(getDate()) + ", feedId = " + pFeedId );
    }
    
    
    for(int i = 0; i < getImportTools().getProdChildSkuUpdateRequiredList().size(); i++) {
         getImportTools().updateLastModifiedDateForChildSkus(getImportTools().getProdChildSkuUpdateRequiredList().get(i));
     }
     clearProdChildSkuUpdateRequiredList();
  }

  /**
   * This method update color picture details for provided pFeedId
   * 
   * @param pFeedId
   * @param pSiteId
   * @throws BBBSystemException
   * @throws BBBBusinessException
   */
  protected void updateProductTabs(final String pFeedId, final Connection pConnection, final boolean isProductionImport) throws BBBSystemException {
	  
   if (isLoggingDebug()) {
	  logDebug("updateProductTabs() Start,  date=" + formatDate(getDate()) + ", feedId = " + pFeedId);
    }
	  
    // Create & update all categories
    boolean rollback = true;
    boolean isMonitorCanceled = false;
    // final Calendar calendar = getCalendar();
    // final Date startTime = getCurrentTime(calendar);
    final Date startTime = getDate();
    List<String> batchProductIdsList;
    final List<String> productIdsSuccessList = new ArrayList<String>();
    final List<String> productIdsErrorList = new ArrayList<String>();
    int batchCount = 1;
    final TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
    BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "updateProductTabs");

    // update log into Feed tables
    // getBBBPIMFeedTools().updateFeedLog(feedId, "ECP_PRODUCT_TABS",
    // startTime,"", "Color Picture Item started", "");

    if (isLoggingDebug()) {
      logDebug("Start updateProductTabs - Product Tab creation/updation");
    }

    // get data from PIM categories
    final List<String> productIds = getPimFeedTools().getUniqueProductTabs(pFeedId, pConnection);

    if (productIds != null && !productIds.isEmpty()) {
      if (isLoggingDebug()) {

        logDebug("No of products for updateProductTabs=" + productIds.size());
      }
      final List<List<String>> batchLists = getImportTools().getBatchList(productIds);
      for (List<String> productSubList : batchLists) {

        batchProductIdsList = productSubList;
        if (isLoggingDebug()) {

          logDebug(new StringBuffer().append("productSubList=").append(productSubList).append(" Batch Count=")
              .append(batchCount).append(" Feed Id=").append(pFeedId).toString());
        }

        try {

          transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

          for (String productId : batchProductIdsList) {

        	  getImportTools().createUpdateProductTabs(pFeedId, productId, productIdsErrorList, pConnection, isProductionImport);
            productIdsSuccessList.add(productId);
          }
          batchCount++;
          rollback = false;
        } catch (TransactionDemarcationException e) {

          rollback = true;
          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          BBBPerformanceMonitor.cancel("updateProductTabs");
          isMonitorCanceled = true;
        } catch (BBBBusinessException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_PRODUCT_TABS", "PRODUCT TAB not done for batch=" +batchCount, getImportTools().getDate(), batchProductIdsList.toString(), pConnection);
          productIdsErrorList.add(batchProductIdsList.toString());
          rollback = false;
          BBBPerformanceMonitor.cancel("updateProductTabs");
          isMonitorCanceled = true;
        } catch (IllegalArgumentException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          productIdsErrorList.add(batchProductIdsList.toString());
          throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_ILLEGAL_ARGUMENT_EXC,"IllegalArgumentException", e);
        } finally {
          if (!isMonitorCanceled) {
            BBBPerformanceMonitor.end(IMPORT_PROCESS_MANAGER, "updateProductTabs");
          }
          try {
            transactionDemarcation.end(rollback);
          } catch (TransactionDemarcationException e) {

            if (isLoggingError()) {

              logError(BBBStringUtils.stack2string(e));
            }
          }
        }
      }

      int noOfRecordsSuccess = productIdsSuccessList.size();
      int totalNoOfRecords = productIds.size();
      if (!productIds.isEmpty() && !productIdsSuccessList.isEmpty()) {

        if (getPimFeedTools().hasFeedBadRecords(pFeedId, pConnection)) {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess)
                .append("Records processed sucessfully with error for Total ").append(totalNoOfRecords)
                .append(" Records in time").append(getExecutionTime(startTime)).append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_PRODUCT_TABS", formatDate(startTime), formatDate(getDate()), totalNoOfRecords,
              "product tab Creation completed with some errors", "", pConnection);
        } else {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess).append("Records processed sucessfully for Total ")
                .append(totalNoOfRecords).append(" Records in time").append(getExecutionTime(startTime))
                .append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_PRODUCT_TABS", formatDate(startTime), formatDate(getDate()), totalNoOfRecords,
              "product tab Creation completed", "", pConnection);
        }

      }

    } else {

      if (isLoggingDebug()) {

        logDebug("No data found for UpdateProductTabs - Product Tab creation/updation feedId= " + pFeedId);
      }
    }
    if (isLoggingDebug()) {

      logDebug("End of UpdateProductTabs - Product Tab creation/updation");
      logDebug("updateProductTabs() End,  date=" + formatDate(getDate()) + ", feedId = " + pFeedId);
    }
    
  }

  /**
   * This method update Media details for provided pFeedId
   * 
   * @param pFeedId
   * @param pSiteId
   * @throws BBBSystemException
   * @throws BBBBusinessException
   */
  protected void updateMedia(final String pFeedId, final Connection pConnection) throws BBBSystemException {
    // Create & update all categories
    boolean rollback = true;
    boolean isMonitorCanceled = false;
    // final Calendar calendar = getCalendar();
    // final Date startTime = getCurrentTime(calendar);
    final Date startTime = getDate();
    List<String> batchMediaIdsList;
    final List<String> mediaIdsSuccessList = new ArrayList<String>();
    int batchCount = 1;
    final TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
    BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "updateMedia");
    // update log into Feed tables
    // getBBBPIMFeedTools().updateFeedLog(feedId, "ECP_ITEM_COLOR_PICTURE",
    // startTime,"", "Color Picture Item started", "");

    if (isLoggingDebug()) {
      logDebug("Start updateMedia - Media creation/updation");
    }

    // get data from PIM ECP_MEDIA
    final List<String> mediaIds = getPimFeedTools().getUniqueMediaIds(pFeedId, pConnection);

    if (mediaIds != null && !mediaIds.isEmpty()) {

      if (isLoggingDebug()) {

        logDebug("No of mediaIds=" + mediaIds.size());
      }
      final List<List<String>> batchLists = getImportTools().getBatchList(mediaIds);
      for (List<String> mediaSubList : batchLists) {

        batchMediaIdsList = mediaSubList;
        if (isLoggingDebug()) {

          logDebug(new StringBuffer().append("Batch mediaSubList=").append(mediaSubList).append(" Batch Count=")
              .append(batchCount).append(" Feed Id=").append(pFeedId).toString());
        }

        try {

          transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

          for (String mediaId : batchMediaIdsList) {

            getImportTools().updateOtherMediaProperties(pFeedId, mediaId, pConnection);
            mediaIdsSuccessList.add(mediaId);
          }
          batchCount++;
          rollback = false;
        } catch (TransactionDemarcationException e) {

          rollback = true;
          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          BBBPerformanceMonitor.cancel("updateMedia");
          isMonitorCanceled = true;
        } catch (BBBBusinessException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }

          rollback = false;
          BBBPerformanceMonitor.cancel("updateMedia");
          isMonitorCanceled = true;
        } catch (IllegalArgumentException e) {

          BBBPerformanceMonitor.cancel("updateMedia");
          isMonitorCanceled = true;
          throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_ILLEGAL_ARGUMENT_EXC,"IllegalArgumentException", e);

        } finally {
          if (!isMonitorCanceled) {
            BBBPerformanceMonitor.end(IMPORT_PROCESS_MANAGER, "updateMedia");
          }
          try {
            transactionDemarcation.end(rollback);
          } catch (TransactionDemarcationException e) {

            if (isLoggingError()) {

              logError(BBBStringUtils.stack2string(e));
            }
          }
        }
      }

      int noOfRecordsSuccess = mediaIdsSuccessList.size();
      int totalNoOfRecords = mediaIds.size();
      if (!mediaIds.isEmpty() && !mediaIdsSuccessList.isEmpty()) {

        if (getPimFeedTools().hasFeedBadRecords(pFeedId, pConnection)) {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess)
                .append("Records processed sucessfully with error for Total ").append(totalNoOfRecords)
                .append(" Records in time").append(getExecutionTime(startTime)).append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_MEDIA", formatDate(startTime), formatDate(getDate()), totalNoOfRecords,
              "Media Creation completed with some errors", "", pConnection);
        } else {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess).append("Records processed sucessfully for Total ")
                .append(totalNoOfRecords).append(" Records in time").append(getExecutionTime(startTime))
                .append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_MEDIA", formatDate(startTime), formatDate(getDate()), totalNoOfRecords,
              "Media Creation completed succesfully", "", pConnection);
        }

      }

    } else {

      if (isLoggingDebug()) {

        logDebug("No data found for updateMedia - Media creation/updation feedId= " + pFeedId);
      }
    }
    if (isLoggingDebug()) {

      logDebug("End of updateMedia - Media creation/updation");
    }
  }

  /**
   * Associate Product with Media Item for provided FeedId
   * 
   * @param pFeedId
   * @param pSiteId
   * @throws BBBSystemException
   * @throws BBBBusinessException
   */
  protected void associateMediaRelnWithProduct(final String pFeedId, final Connection pConnection, final boolean isProductionImport)
      throws BBBSystemException {
    // Create & update all categories
    boolean rollback = true;
    boolean isMonitorCanceled = false;
    // final Calendar calendar = getCalendar();
    // final Date startTime = getCurrentTime(calendar);
    final Date startTime = getDate();
    final List<String> productIdsSuccessList = new ArrayList<String>();
    final List<String> errorList = new ArrayList<String>();
    List<String> batchCategoryIdsList;
    int batchCount = 1;

    final TransactionDemarcation transaction = new TransactionDemarcation();
    BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "associateMediaRelnWithProduct");
    // update log into Feed tables
    // getPimFeedTools().updateFeedLog(pFeedId, "ECP_NODE_PARENT", startTime,"",
    // "Category Creation started", "");

    if (isLoggingDebug()) {
      logDebug("Start associateMediaRelnWithProduct - Product Media Association");
    }

    // get data from PIM categories
    final List<String> productIds = getPimFeedTools().getUniqueMediaItemRelIds(pFeedId, pConnection);

    if (productIds != null && !productIds.isEmpty()) {

      if (isLoggingDebug()) {

        logDebug("No of productIds for Media Association=" + productIds.size());
      }
      final List<List<String>> batchLists = getImportTools().getBatchList(productIds);
      for (List<String> idList : batchLists) {

        batchCategoryIdsList = idList;
        if (isLoggingDebug()) {

          logDebug(new StringBuffer().append("Batch productIdSubList for Media Association=").append(idList)
              .append(" Batch Count=").append(batchCount).append(" Feed Id=").append(pFeedId).toString());

        }
        try {

          transaction.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

          for (String productId : batchCategoryIdsList) {

            getImportTools().associateMediaRelnWithProduct(pFeedId, productId, errorList, pConnection, isProductionImport);
            productIdsSuccessList.add(productId);
          }
          batchCount++;
          rollback = false;
        } catch (TransactionDemarcationException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          rollback = true;
          BBBPerformanceMonitor.cancel("associateMediaRelnWithProduct");
          isMonitorCanceled = true;
        } catch (BBBBusinessException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          rollback = false;
          BBBPerformanceMonitor.cancel("associateMediaRelnWithProduct");
          isMonitorCanceled = true;
        } finally {

          if (!isMonitorCanceled) {
            BBBPerformanceMonitor.end(IMPORT_PROCESS_MANAGER, "associateMediaRelnWithProduct");
          }
          try {

            transaction.end(rollback);
          } catch (TransactionDemarcationException e) {

            if (isLoggingError()) {

              logError(BBBStringUtils.stack2string(e));
            }
          }
        }
      }
      int noOfRecordsSuccess = productIdsSuccessList.size();
      if (!productIds.isEmpty()) {

        if (!errorList.isEmpty()) {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append("Records processed sucessfully with errors in time")
                .append(getExecutionTime(startTime)).append(" secs").toString());
          }
          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_ITEM_MEDIA", formatDate(startTime), formatDate(getDate()), noOfRecordsSuccess,
              "associateMediaRelnWithProduct completed with some errors", "", pConnection);
        } else {

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_ITEM_MEDIA", formatDate(startTime), formatDate(getDate()), noOfRecordsSuccess,
              "associateMediaRelnWithProduct completed", "", pConnection);
        }

      }

    } else {

      if (isLoggingDebug()) {

        logDebug("No data found for associateMediaRelnWithProduct - Product Media Association feedId= " + pFeedId);
      }
    }
    if (isLoggingDebug()) {

      logDebug("End of associateMediaRelnWithProduct - Product Media Association");
    }

  }

  /**
   * This method update Media details for provided pFeedId
   * 
   * @param pFeedId
   * @param pSiteId
   * @throws BBBSystemException
   * @throws BBBBusinessException
   */
  protected void updateRebateInfo(final String pFeedId, final Connection pConnection) throws BBBSystemException {
    // Create & update all categories
    boolean rollback = true;
    boolean isMonitorCanceled = false;
    // final Calendar calendar = getCalendar();
    // final Date startTime = getCurrentTime(calendar);
    final Date startTime = getDate();
    List<String> batchReabteIdsList;
    final List<String> rebateInfoIdsSuccessList = new ArrayList<String>();
    final TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
    BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "updateRebateInfo");
    // update log into Feed tables
    // getBBBPIMFeedTools().updateFeedLog(feedId, "ECP_ITEM_COLOR_PICTURE",
    // startTime,"", "Color Picture Item started", "");

    if (isLoggingDebug()) {
      logDebug("Start updateRebateInfo - Rebate creation/updation");
    }

    // get data from PIM ECP_MEDIA
    final List<String> rebateIds = getPimFeedTools().getUniqueRebateIds(pFeedId, pConnection);

    if (rebateIds != null && !rebateIds.isEmpty()) {

      if (isLoggingDebug()) {

        logDebug("No of mediaIds=" + rebateIds.size());
      }
      final List<List<String>> batchLists = getImportTools().getBatchList(rebateIds);
      int batchCount = 1;
      for (List<String> rebateSubList : batchLists) {

        batchReabteIdsList = rebateSubList;
        if (isLoggingDebug()) {

          logDebug(new StringBuffer().append("Batch rebateSubList=").append(rebateSubList).append(" Batch Count=")
              .append(batchCount).append(" Feed Id=").append(pFeedId).toString());
        }

        try {

          transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

          for (String rebateId : batchReabteIdsList) {

            getImportTools().updateRebateInfo(pFeedId, rebateId, pConnection);
            rebateInfoIdsSuccessList.add(rebateId);
          }
          batchCount++;
          rollback = false;
        } catch (TransactionDemarcationException e) {

          rollback = true;
          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          BBBPerformanceMonitor.cancel("updateRebateInfo");
          isMonitorCanceled = true;
        } catch (BBBBusinessException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }

          rollback = false;
          BBBPerformanceMonitor.cancel("updateRebateInfo");
          isMonitorCanceled = true;
        } catch (IllegalArgumentException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          BBBPerformanceMonitor.cancel("updateRebateInfo");
          isMonitorCanceled = true;
          throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_ILLEGAL_ARGUMENT_EXC,"IllegalArgumentException", e);
        } finally {
          if (!isMonitorCanceled) {
            BBBPerformanceMonitor.end(IMPORT_PROCESS_MANAGER, "updateRebateInfo");
          }
          try {
            transactionDemarcation.end(rollback);
          } catch (TransactionDemarcationException e) {

            if (isLoggingError()) {

              logError(BBBStringUtils.stack2string(e));
            }
          }
        }
      }

      int noOfRecordsSuccess = rebateInfoIdsSuccessList.size();
      int totalNoOfRecords = rebateIds.size();
      if (!rebateIds.isEmpty() && !rebateInfoIdsSuccessList.isEmpty()) {

        if (getPimFeedTools().hasFeedBadRecords(pFeedId, pConnection)) {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess)
                .append("Records processed sucessfully with errors for Total ").append(totalNoOfRecords)
                .append(" Records in time").append(getExecutionTime(startTime)).append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_REBATES", formatDate(startTime), formatDate(getDate()), totalNoOfRecords,
              "Rebate Creation completed with some errors", "", pConnection);
        } else {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess).append("Records processed sucessfully for Total ")
                .append(totalNoOfRecords).append(" Records in time").append(getExecutionTime(startTime))
                .append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_REBATES", formatDate(startTime), formatDate(getDate()), totalNoOfRecords,
              "Rebate Creation completed succesfully", "", pConnection);
        }

      }

    } else {

      if (isLoggingDebug()) {

        logDebug("No data found for updateRebateInfo - Rebate creation/updation feedId= " + pFeedId);
      }
    }
    if (isLoggingDebug()) {

      logDebug("End of updateRebateInfo - Rebate creation/updation");
    }
  }
 protected void updateStofuImages(final String pFeedId, final Connection pConnection) throws BBBSystemException {
	    
	    boolean rollback = true;
	    boolean isMonitorCanceled = false;
	    final Date startTime = getDate();
	    List<String> batchImageIdsList;
	    final List<String> imagesInfoIdsSuccessList = new ArrayList<String>();
	    final TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
	    BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "updateStofuImages");
	    // update log into Feed tables
	    if (isLoggingDebug()) {
	      logDebug("Start updateStofuImages - images creation/updation");
	    }
	    // get data from PIM ECP_STOFU_IMAGES
	    
	    final List<String> skuIds = getPimFeedTools().getUniqueStofuImagesId(pFeedId, pConnection);

	    if (skuIds != null && !skuIds.isEmpty()) {

	      if (isLoggingDebug()) {
 
	        logDebug("No of skuIds=" + skuIds.size());
	      }
	      final List<List<String>> batchLists = getImportTools().getBatchList(skuIds);
	      int batchCount = 1;
	      for (List<String> imagesSubList : batchLists) {

	    	  batchImageIdsList = imagesSubList;
	        if (isLoggingDebug()) {

	          logDebug(new StringBuffer().append("Batch imagesSubList=").append(imagesSubList).append(" Batch Count=")
	              .append(batchCount).append(" Feed Id=").append(pFeedId).toString());
	        }

	        try {

	          transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

	          for (String skuId : batchImageIdsList) {

	            getImportTools().updateStofuImages(pFeedId, skuId, pConnection);
	            imagesInfoIdsSuccessList.add(skuId);
	          }
	          batchCount++;
	          rollback = false;
	        } catch (TransactionDemarcationException e) {

	          rollback = true;
	          if (isLoggingError()) {

	            logError(BBBStringUtils.stack2string(e));
	          }
	          BBBPerformanceMonitor.cancel("updateStofuImagesInfo");
	          isMonitorCanceled = true;
	        } catch (BBBBusinessException e) {

	          if (isLoggingError()) {

	            logError(BBBStringUtils.stack2string(e));
	          }

	          rollback = false;
	          BBBPerformanceMonitor.cancel("updateStofuImagesInfo");
	          isMonitorCanceled = true;
	        } catch (IllegalArgumentException e) {

	          if (isLoggingError()) {

	            logError(BBBStringUtils.stack2string(e));
	          }
	          BBBPerformanceMonitor.cancel("updateStofuImagesInfo");
	          isMonitorCanceled = true;
	          throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_ILLEGAL_ARGUMENT_EXC,"IllegalArgumentException", e);
	        } finally {
	          if (!isMonitorCanceled) {
	            BBBPerformanceMonitor.end(IMPORT_PROCESS_MANAGER, "updateStofuImagesInfo");
	          }
	          try {
	            transactionDemarcation.end(rollback);
	          } catch (TransactionDemarcationException e) {

	            if (isLoggingError()) {

	              logError(BBBStringUtils.stack2string(e));
	            }
	          }
	        }
	      }

	      int noOfRecordsSuccess = imagesInfoIdsSuccessList.size();
	      int totalNoOfRecords = skuIds.size();
	      if (!skuIds.isEmpty() && !imagesInfoIdsSuccessList.isEmpty()) {

	        if (getPimFeedTools().hasFeedBadRecords(pFeedId, pConnection)) {
	        	 if (isLoggingDebug()) {

	 	            logDebug(new StringBuffer().append(noOfRecordsSuccess)
	 	                .append("Records processed sucessfully with errors for Total ").append(totalNoOfRecords)
	 	                .append(" Records in time").append(getExecutionTime(startTime)).append(" secs").toString());
	 	          }


	          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_STOFU_IMAGES", formatDate(startTime), formatDate(getDate()), totalNoOfRecords,
	              "IMages  Creation completedwith some errors", "", pConnection);
	        } else {
	          if (isLoggingDebug()) {

	        	  logDebug(new StringBuffer().append(noOfRecordsSuccess).append("Records processed sucessfully for Total ")
	  	                .append(totalNoOfRecords).append(" Records in time").append(getExecutionTime(startTime))
	  	                .append(" secs").toString());
	          }

	          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_STOFU_IMAGES", formatDate(startTime), formatDate(getDate()), totalNoOfRecords,
	              "Stofu Images  Creation  completed succesfully", "", pConnection);
	        }

	       }

	    } else {

	      if (isLoggingDebug()) {

	        logDebug("No data found for updateStofuImages - stofuImages creation/updation feedId= " + pFeedId);
	      }
	    }
	    if (isLoggingDebug()) {

	      logDebug("End of updateStofuImages - stofuImages creation/updation");
	    }
	  }
  /**
   * Associate Sku with Rebate Item for provided FeedId
   * 
   * @param pFeedId
   * @param pSiteId
   * @throws BBBSystemException
   * @throws BBBBusinessException
   */
  protected void associateRebateRelnWithSku(final String pFeedId, final Connection pConnection, final boolean isProductionImport)
      throws BBBSystemException {
    // Associate Rebate Item with Sku
    boolean rollback = true;
    boolean isMonitorCanceled = false;
    // final Calendar calendar = getCalendar();
    // final Date startTime = getCurrentTime(calendar);
    final Date startTime = getDate();
    final List<String> skuIdsSuccessList = new ArrayList<String>();
    List<String> batchSkuIdsList;

    final TransactionDemarcation transaction = new TransactionDemarcation();
    BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "associateRebateRelnWithSku");
    // update log into Feed tables
    // getPimFeedTools().updateFeedLog(pFeedId, "ECP_NODE_PARENT", startTime,"",
    // "Category Creation started", "");

    if (isLoggingDebug()) {
      logDebug("Start associateRebateRelnWithSku - Sku - Rebate Association");
    }

    // get data from PIM categories
    final List<String> skuIds = getPimFeedTools().getUniqueSkuRebatesRelIds(pFeedId, pConnection);

    if (skuIds != null && !skuIds.isEmpty()) {

      if (isLoggingDebug()) {

        logDebug("No of skuIds for RebateInfo Association=" + skuIds.size());
      }
      final List<List<String>> batchLists = getImportTools().getBatchList(skuIds);
      int batchCount = 1;
      for (List<String> idList : batchLists) {

        batchSkuIdsList = idList;
        if (isLoggingDebug()) {

          logDebug(new StringBuffer().append("Batch productIdSubList for RebateInfo Association=").append(idList)
              .append(" Batch Count=").append(batchCount).append(" Feed Id=").append(pFeedId).toString());
        }
        try {

          transaction.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

          for (String skuId : batchSkuIdsList) {

            getImportTools().associateRebateRelnWithSku(pFeedId, skuId, pConnection, isProductionImport);
            skuIdsSuccessList.add(skuId);
          }
          batchCount++;
          rollback = false;
        } catch (TransactionDemarcationException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          rollback = true;
          BBBPerformanceMonitor.cancel("associateRebateRelnWithSku");
          isMonitorCanceled = true;
        } catch (BBBBusinessException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          rollback = false;
          BBBPerformanceMonitor.cancel("associateRebateRelnWithSku");
          isMonitorCanceled = true;
        } finally {
          if (!isMonitorCanceled) {
            BBBPerformanceMonitor.end(IMPORT_PROCESS_MANAGER, "associateRebateRelnWithSku");
          }
          try {
            transaction.end(rollback);
          } catch (TransactionDemarcationException e) {

            if (isLoggingError()) {

              logError(BBBStringUtils.stack2string(e));
            }
          }
        }
      }
      int noOfRecordsSuccess = skuIdsSuccessList.size();
      int totalNoOfRecords = skuIds.size();
      if (!skuIds.isEmpty() && !skuIdsSuccessList.isEmpty()) {

        if (getPimFeedTools().hasFeedBadRecords(pFeedId, pConnection)) {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess)
                .append("Records processed sucessfully with errors for Total ").append(totalNoOfRecords)
                .append(" Records in time").append(getExecutionTime(startTime)).append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_SKU_REBATES", formatDate(startTime), formatDate(getDate()), noOfRecordsSuccess,
              "associateRebateRelnWithSku completed with some errors", "", pConnection);
        } else {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess).append("Records processed sucessfully for Total ")
                .append(totalNoOfRecords).append(" Records in time").append(getExecutionTime(startTime))
                .append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_SKU_REBATES", formatDate(startTime), formatDate(getDate()), noOfRecordsSuccess,
              "associateRebateRelnWithSku completed", "", pConnection);
        }

      }

    } else {

      if (isLoggingDebug()) {

        logDebug("No data found for associateRebateRelnWithSku - Sku-Rebate Association feedId= " + pFeedId);
      }
    }
    if (isLoggingDebug()) {

      logDebug("End of associateRebateRelnWithSku - Sku-Rebate Association");
    }

  }

  /**
   * This method update Pricing details for provided pFeedId and sku
   * 
   * @param pFeedId
   * @param pSiteId
   * @throws BBBSystemException
   * @throws BBBBusinessException
   */
  protected void updatePricing(final String pFeedId, final Connection pConnection) throws BBBSystemException {
    // Create & update all categories
    boolean rollback = true;
    boolean isMonitorCanceled = false;
    // final Calendar calendar = getCalendar();
    // final Date startTime = getCurrentTime(calendar);
    final Date startTime = getDate();
    List<String> batchSkuIdsList;
    final List<String> skuIdsSuccessList = new ArrayList<String>();
    final TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
    BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "updatePricing");
    // update log into Feed tables
    // getBBBPIMFeedTools().updateFeedLog(feedId, "ECP_ITEM_COLOR_PICTURE",
    // startTime,"", "Color Picture Item started", "");

    if (isLoggingDebug()) {
      logDebug("Start updatePricing - Pricing creation/updation");
    }

    // get data from PIM ECP_SKU_PRICING_NEW
    final List<String> skuIds = getPimFeedTools().getUniquePricingSkus(pFeedId, pConnection);

    if (skuIds != null && !skuIds.isEmpty()) {

      if (isLoggingDebug()) {

        logDebug("No of skuIds for pricing=" + skuIds.size());
      }
      final List<List<String>> batchLists = getImportTools().getBatchList(skuIds);
      int batchCount = 1;
      for (List<String> skuIdsSubList : batchLists) {

        batchSkuIdsList = skuIdsSubList;
        if (isLoggingDebug()) {

          logDebug(new StringBuffer().append("Batch skuIdsSubList=").append(skuIdsSubList).append(" Batch Count=")
              .append(batchCount).append(" Feed Id=").append(pFeedId).toString());
        }

        try {

          transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

          for (String skuId : batchSkuIdsList) {

            getImportTools().updatePricing(pFeedId, skuId, pConnection);
            skuIdsSuccessList.add(skuId);
          }
          batchCount++;
          rollback = false;
        } catch (TransactionDemarcationException e) {

          rollback = true;
          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          BBBPerformanceMonitor.cancel("updatePricing");
          isMonitorCanceled = true;
        } catch (BBBBusinessException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_PRICING", "PRICING not done for batch=" +batchCount, getImportTools().getDate(), batchLists.toString(), pConnection);
          rollback = false;
          BBBPerformanceMonitor.cancel("updatePricing");
          isMonitorCanceled = true;
        } catch (IllegalArgumentException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          BBBPerformanceMonitor.cancel("updatePricing");
          isMonitorCanceled = true;
          throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_ILLEGAL_ARGUMENT_EXC,"IllegalArgumentException", e);
        } finally {

          if (!isMonitorCanceled) {
            BBBPerformanceMonitor.end(IMPORT_PROCESS_MANAGER, "updatePricing");
          }
          try {
            transactionDemarcation.end(rollback);
          } catch (TransactionDemarcationException e) {

            if (isLoggingError()) {

              logError(BBBStringUtils.stack2string(e));
            }
          }
        }
      }

      int noOfRecordsSuccess = skuIdsSuccessList.size();
      int totalNoOfRecords = skuIds.size();
      if (!skuIds.isEmpty() && !skuIdsSuccessList.isEmpty()) {

        if (getPimFeedTools().hasFeedBadRecords(pFeedId, pConnection)) {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess)
                .append("Records processed sucessfully with errors for Total ").append(totalNoOfRecords)
                .append(" Records in time").append(getExecutionTime(startTime)).append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_SKU_PRICING_NEW", formatDate(startTime), formatDate(getDate()), totalNoOfRecords,
              "Update Pricing completed with some errors", "", pConnection);
        } else {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess).append("Records processed sucessfully for Total ")
                .append(totalNoOfRecords).append(" Records in time").append(getExecutionTime(startTime))
                .append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_SKU_PRICING_NEW", formatDate(startTime), formatDate(getDate()), totalNoOfRecords,
              "Update Pricing completed succesfully", "", pConnection);
        }

      }

    } else {

      if (isLoggingDebug()) {

        logDebug("No data found for Update Pricing  = " + pFeedId);
      }
    }
    if (isLoggingDebug()) {

      logDebug("End of Update Pricing");
    }
  }

  /**
   * This method update Attributes details for provided pFeedId
   * 
   * @param pFeedId
   * @param pSiteId
   * @throws BBBSystemException
   * @throws BBBBusinessException
   */
  protected void updateAttributes(final String pFeedId, final Connection pConnection) throws BBBSystemException {
	  
	  if (isLoggingDebug()) {
	     logDebug("updateAttributes() Start,  date=" + formatDate(getDate()) + ", feedId = " + pFeedId) ;
	  }
    // Create & update all categories
    boolean rollback = true;
    boolean isMonitorCanceled = false;
    // final Calendar calendar = getCalendar();
    // final Date startTime = getCurrentTime(calendar);
    final Date startTime = getDate();
    List<String> batchAttributeIdsList;
    final List<String> attributeIdsSuccessList = new ArrayList<String>();
    final TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
    BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "updateAttributes");
    // update log into Feed tables
    // getBBBPIMFeedTools().updateFeedLog(feedId, "ECP_ITEM_COLOR_PICTURE",
    // startTime,"", "Color Picture Item started", "");

    if (isLoggingDebug()) {
      logDebug("Start Attributes - Attribute creation/updation");
    }

    // get data from PIM ECP_MEDIA
    final List<String> attributeIds = getPimFeedTools().getUniqueAttributesIds(pFeedId, pConnection);

    if (attributeIds != null && !attributeIds.isEmpty()) {

      if (isLoggingDebug()) {

        logDebug("No of attributeIds=" + attributeIds.size());
      }
      final List<List<String>> batchLists = getImportTools().getBatchList(attributeIds);
      int batchCount = 1;
      for (List<String> attributeSubList : batchLists) {

        batchAttributeIdsList = attributeSubList;
        if (isLoggingDebug()) {

          logDebug(new StringBuffer().append("Batch attributeSubList=").append(attributeSubList)
              .append(" Batch Count=").append(batchCount).append(" Feed Id=").append(pFeedId).toString());
        }

        try {

          transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

          for (String attributeId : batchAttributeIdsList) {

        	  getImportTools().updateItemAttributesProperties(pFeedId, attributeId, pConnection);
            attributeIdsSuccessList.add(attributeId);
          }
          batchCount++;
          rollback = false;
        } catch (TransactionDemarcationException e) {

          rollback = true;
          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          BBBPerformanceMonitor.cancel("updateAttributes");
          isMonitorCanceled = true;
        } catch (BBBBusinessException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }

          rollback = false;
          BBBPerformanceMonitor.cancel("updateAttributes");
          isMonitorCanceled = true;
        } catch (IllegalArgumentException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          BBBPerformanceMonitor.cancel("updateAttributes");
          isMonitorCanceled = true;
          throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_ILLEGAL_ARGUMENT_EXC,"IllegalArgumentException", e);
        } finally {
          if (!isMonitorCanceled) {
            BBBPerformanceMonitor.end(IMPORT_PROCESS_MANAGER, "updateAttributes");
          }
          try {
            transactionDemarcation.end(rollback);
          } catch (TransactionDemarcationException e) {

            if (isLoggingError()) {

              logError(BBBStringUtils.stack2string(e));
            }
          }
        }
      }

      int noOfRecordsSuccess = attributeIdsSuccessList.size();
      int totalNoOfRecords = attributeIds.size();
      if (!attributeIds.isEmpty() && !attributeIdsSuccessList.isEmpty()) {

        if (getPimFeedTools().hasFeedBadRecords(pFeedId, pConnection)) {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess)
                .append("Records processed sucessfully with errors for Total ").append(totalNoOfRecords)
                .append(" Records in time").append(getExecutionTime(startTime)).append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_ITEM_ATTRIBUTES", formatDate(startTime), formatDate(getDate()), totalNoOfRecords,
              "Attributes Creation completed with some errors", "", pConnection);
        } else {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess).append("Records processed sucessfully for Total ")
                .append(totalNoOfRecords).append(" Records in time").append(getExecutionTime(startTime))
                .append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_ITEM_ATTRIBUTES", formatDate(startTime), formatDate(getDate()), totalNoOfRecords,
              "Attributes Creation completed succesfully", "", pConnection);
        }

      }

    } else {

      if (isLoggingDebug()) {

        logDebug("No data found for Attributes - Attributes creation/updation feedId= " + pFeedId);
      }
    }
    if (isLoggingDebug()) {

      logDebug("End of Attributes - Attributes creation/updation");
      logDebug("updateAttributes() End,  date=" + formatDate(getDate()) + ", feedId = " + pFeedId) ;
    }
    
    
  }

  /**
   * Associate Sku with Attributes Item for provided FeedId
   * 
   * @param pFeedId
   * @param pSiteId
   * @throws BBBSystemException
   * @throws BBBBusinessException
   */
  protected void associateSkuAttributesRelnWithSku(final String pFeedId, final Connection pConnection, final boolean isProductionImport)
      throws BBBSystemException {
	  if (isLoggingDebug()) {
	     logDebug("associateSkuAttributesRelnWithSku() Start,  date=" + formatDate(getDate()) + ", feedId = " + pFeedId) ;
	  }
    // Associate Rebate Item with Sku
    boolean rollback = true;
    boolean isMonitorCanceled = false;
    // final Calendar calendar = getCalendar();
    // final Date startTime = getCurrentTime(calendar);
    final Date startTime = getDate();
    final List<String> skuIdsSuccessList = new ArrayList<String>();
    final List<String> errorList = new ArrayList<String>();
    List<String> batchSkuIdsList;

    final TransactionDemarcation transaction = new TransactionDemarcation();
    BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "associateSkuAttributesRelnWithSku");
    // update log into Feed tables
    // getPimFeedTools().updateFeedLog(pFeedId, "ECP_NODE_PARENT", startTime,"",
    // "Category Creation started", "");

    if (isLoggingDebug()) {
      logDebug("Start associateSkuAttributesRelnWithSku - Sku - Attributes Association");
    }

    // get data from PIM categories
    final List<String> skuIds = getPimFeedTools().getUniqueSkuAttributesRelIds(pFeedId, pConnection);

    if (skuIds != null && !skuIds.isEmpty()) {

      if (isLoggingDebug()) {

        logDebug("No of skuIds for Attributes Association=" + skuIds.size());
      }
      final List<List<String>> batchLists = getImportTools().getBatchList(skuIds);
      int batchCount = 1;
      for (List<String> idList : batchLists) {

        batchSkuIdsList = idList;
        if (isLoggingDebug()) {

          logDebug(new StringBuffer().append("Batch productIdSubList for Attributes Association=").append(idList)
              .append(" Batch Count=").append(batchCount).append(" Feed Id=").append(pFeedId).toString());
        }
        try {

          transaction.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

          for (String skuId : batchSkuIdsList) {

        	  getImportTools().associateSkuAttributes(pFeedId, skuId, errorList, pConnection, isProductionImport);
            skuIdsSuccessList.add(skuId);
          }
          batchCount++;
          rollback = false;
        } catch (TransactionDemarcationException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          rollback = true;
          BBBPerformanceMonitor.cancel("associateSkuAttributesRelnWithSku");
          isMonitorCanceled = true;
        } catch (BBBBusinessException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          rollback = false;
          BBBPerformanceMonitor.cancel("associateSkuAttributesRelnWithSku");
          isMonitorCanceled = true;
        } finally {
          if (!isMonitorCanceled) {
            BBBPerformanceMonitor.end(IMPORT_PROCESS_MANAGER, "associateSkuAttributesRelnWithSku");
          }
          try {
            transaction.end(rollback);
          } catch (TransactionDemarcationException e) {

            if (isLoggingError()) {

              logError(BBBStringUtils.stack2string(e));
            }
          }
        }
      }

      int noOfRecordsSuccess = skuIdsSuccessList.size();
      int totalNoOfRecords = skuIds.size();
      if (!skuIds.isEmpty()) {

        if (getPimFeedTools().hasFeedBadRecords(pFeedId, pConnection)) {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess)
                .append("Records processed sucessfully with errors for Total ").append(totalNoOfRecords)
                .append(" Records in time").append(getExecutionTime(startTime)).append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_SKU_DISPLAY_ATTRIBUTES", formatDate(startTime), formatDate(getDate()),
              noOfRecordsSuccess, "associateSkuAttributesRelnWithSku completed with some errors", "", pConnection);
        } else {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess).append("Records processed sucessfully for Total ")
                .append(totalNoOfRecords).append(" Records in time").append(getExecutionTime(startTime))
                .append(" secs").toString());
          }
          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_SKU_DISPLAY_ATTRIBUTES", formatDate(startTime), formatDate(getDate()),
              noOfRecordsSuccess, "associateSkuAttributesRelnWithSku completed", "", pConnection);
        }

      }

    } else {

      if (isLoggingDebug()) {

        logDebug("No data found for associateRebateRelnWithSku - Sku-Rebate Association feedId= " + pFeedId);
      }
    }
    if (isLoggingDebug()) {

      logDebug("End of associateRebateRelnWithSku - Sku-Rebate Association");
      logDebug("associateSkuAttributesRelnWithSku() End,  date=" + formatDate(getDate()) + ", feedId = " + pFeedId) ;
    }
    
    

  }

  /**
   * This method updates Dept details
   * 
   * 
   * @throws BBBSystemException
   * @throws BBBBusinessException
   */
  protected void updateDept(final String pFeedId, final Connection pConnection) throws BBBSystemException {
    // Create & update all categories
    boolean rollback = true;
    boolean isMonitorCanceled = false;
    // final Calendar calendar = getCalendar();
    // final Date startTime = getCurrentTime(calendar);
    final Date startTime = getDate();
    List<String> batchDeptIdsList;
    final List<String> deptIdsSuccessList = new ArrayList<String>();
    final List<String> deptIdsErrorList = new ArrayList<String>();
    final List<String> errorList = new ArrayList<String>();
    final TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
    BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "updateDept");

    if (isLoggingDebug()) {
      logDebug("Start updating Dept creation");
    }

    final List<String> jdaDeptIds = getPimFeedTools().getUniqueDeptIds(pFeedId, pConnection);
    String deptId = null;
    int batchCount = 1;
    if (jdaDeptIds != null && !jdaDeptIds.isEmpty()) {

      final List<List<String>> batchLists = getImportTools().getBatchList(jdaDeptIds);
      for (List<String> deptSubList : batchLists) {

        batchDeptIdsList = deptSubList;
        if (isLoggingDebug()) {

          logDebug(new StringBuffer().append("deptSubList=").append(deptSubList).append(" Batch Count=")
              .append(batchCount).append(" Feed Id=").append(pFeedId).toString());
        }

        try {

          transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

          for (String id : batchDeptIdsList) {

            deptId = id;
            getImportTools().updateDept(pFeedId, deptId, errorList, pConnection);
          }
          if (!StringUtils.isEmpty(deptId)) {

            deptIdsSuccessList.add(deptId);
          }
          batchCount++;
          rollback = false;
        } catch (TransactionDemarcationException e) {

          rollback = true;
          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          BBBPerformanceMonitor.cancel("updateDept");
          isMonitorCanceled = true;
        } catch (BBBBusinessException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          deptIdsErrorList.add(batchDeptIdsList.toString());
          rollback = false;
          BBBPerformanceMonitor.cancel("updateDept");
          isMonitorCanceled = true;
        } catch (IllegalArgumentException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          deptIdsErrorList.add(batchDeptIdsList.toString());
          BBBPerformanceMonitor.cancel("updateDept");
          isMonitorCanceled = true;
          throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_ILLEGAL_ARGUMENT_EXC,"IllegalArgumentException", e);
        } finally {
          if (!isMonitorCanceled) {
            BBBPerformanceMonitor.end(IMPORT_PROCESS_MANAGER, "updateDept");
          }
          try {
            transactionDemarcation.end(rollback);
          } catch (TransactionDemarcationException e) {

            if (isLoggingError()) {

              logError(BBBStringUtils.stack2string(e));
            }
          }
        }
      }
      int noOfRecordsSuccess = deptIdsSuccessList.size();
      int totalNoOfRecords = jdaDeptIds.size();
      if (!jdaDeptIds.isEmpty() && !deptIdsSuccessList.isEmpty()) {
        if (getPimFeedTools().hasFeedBadRecords(pFeedId, pConnection)) {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess)
                .append("Records processed sucessfully with some errors for Total ").append(totalNoOfRecords)
                .append(" Records in time").append(getExecutionTime(startTime)).append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "DEPT", formatDate(startTime), formatDate(getDate()), deptIdsSuccessList.size(),
              "updating Dept completed with some errors", "", pConnection);
        } else {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess).append("Records processed sucessfully for Total ")
                .append(totalNoOfRecords).append(" Records in time").append(getExecutionTime(startTime))
                .append(" secs").toString());
          }
          getPimFeedTools().updateFeedLog(pFeedId, "", "DEPT", formatDate(startTime), formatDate(getDate()), deptIdsSuccessList.size(),
              "updating Dept completed", "", pConnection);

        }
      }

    } else {

      if (isLoggingDebug()) {

        logDebug("No data found for Updating Dept ");
      }
    }
    if (isLoggingDebug()) {
      logDebug("End of Updating Dept");
    }

  }

  /**
   * This method updates Sub Dept details
   * 
   * 
   * @throws BBBSystemException
   * @throws BBBBusinessException
   */
  protected void updateSubDept(final String pFeedId, final Connection pConnection) throws BBBSystemException {
    // Create & update all categories
    boolean rollback = true;
    boolean isMonitorCanceled = false;
    // final Calendar calendar = getCalendar();
    // final Date startTime = getCurrentTime(calendar);
    final Date startTime = getDate();
    List<String> batchSubDeptIdsList;
    final List<String> subDeptIdsSuccessList = new ArrayList<String>();
    final List<String> subDeptIdsErrorList = new ArrayList<String>();
    final List<String> errorList = new ArrayList<String>();
    final TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
    BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "updateSubDept");

    if (isLoggingDebug()) {
      logDebug("Start updating SubDept creation");
    }

    final List<String> jdaDeptIds = getPimFeedTools().getUniqueDeptIdsFromSubDept(pFeedId, pConnection);
    String deptId = null;
    int batchCount = 1;
    if (jdaDeptIds != null && !jdaDeptIds.isEmpty()) {

      final List<List<String>> batchLists = getImportTools().getBatchList(jdaDeptIds);
      for (List<String> deptSubList : batchLists) {

        batchSubDeptIdsList = deptSubList;
        if (isLoggingDebug()) {

          logDebug(new StringBuffer().append("deptSubList=").append(deptSubList).append(" Batch Count=")
              .append(batchCount).append(" Feed Id=").append(pFeedId).toString());
        }

        try {

          transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

          for (String id : batchSubDeptIdsList) {

            deptId = id;
            getImportTools().updateSubDeptProperties(pFeedId, deptId, errorList, pConnection);
          }

          if (!StringUtils.isEmpty(deptId)) {

            subDeptIdsSuccessList.add(deptId);
          }
          rollback = false;
        } catch (TransactionDemarcationException e) {

          rollback = true;
          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          BBBPerformanceMonitor.cancel("updateSubDept");
          isMonitorCanceled = true;
        } catch (BBBBusinessException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          subDeptIdsErrorList.add(batchSubDeptIdsList.toString());
          rollback = false;
          BBBPerformanceMonitor.cancel("updateSubDept");
          isMonitorCanceled = true;
        } catch (IllegalArgumentException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          subDeptIdsErrorList.add(batchSubDeptIdsList.toString());
          BBBPerformanceMonitor.cancel("updateSubDept");
          isMonitorCanceled = true;
          throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_ILLEGAL_ARGUMENT_EXC,"IllegalArgumentException", e);
        } finally {

          if (!isMonitorCanceled) {
            BBBPerformanceMonitor.end(IMPORT_PROCESS_MANAGER, "updateSubDept");
          }
          try {
            transactionDemarcation.end(rollback);
          } catch (TransactionDemarcationException e) {

            if (isLoggingError()) {

              logError(BBBStringUtils.stack2string(e));
            }
          }
        }
      }
      int noOfRecordsSuccess = subDeptIdsSuccessList.size();
      int totalNoOfRecords = jdaDeptIds.size();
      if (!jdaDeptIds.isEmpty() && !subDeptIdsSuccessList.isEmpty()) {
        if (getPimFeedTools().hasFeedBadRecords(pFeedId, pConnection)) {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess)
                .append("Records processed sucessfully with errors for Total ").append(totalNoOfRecords)
                .append(" Records in time").append(getExecutionTime(startTime)).append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "SUB_DEPT", formatDate(startTime), formatDate(getDate()), subDeptIdsSuccessList.size(),
              "updating Sub Dept completed with some errors", "", pConnection);
        } else {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess).append("Records processed sucessfully for Total ")
                .append(totalNoOfRecords).append(" Records in time").append(getExecutionTime(startTime))
                .append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "SUB_DEPT", formatDate(startTime), formatDate(getDate()), subDeptIdsSuccessList.size(),
              "updating Sub Dept completed", "", pConnection);

        }
      }

    } else {

      if (isLoggingDebug()) {

        logDebug("No data found for Updating Sub Dept ");
      }
    }
    if (isLoggingDebug()) {
      logDebug("End of Updating Sub Dept");
    }

  }

  protected void updateBrand(final String pFeedId, final Connection pConnection) throws BBBSystemException {
    // Create & update all categories
    boolean rollback = true;
    boolean isMonitorCanceled = false;
    // final Calendar calendar = getCalendar();
    // final Date startTime = getCurrentTime(calendar);
    final Date startTime = getDate();
    List<String> batchBrandIdsList;
    final List<String> brandIdsSuccessList = new ArrayList<String>();
    final List<String> brandIdsErrorList = new ArrayList<String>();
    final List<String> errorList = new ArrayList<String>();
    final TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
    BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "updateBrand");

    if (isLoggingDebug()) {
      logDebug("Start updating Brand creation");
    }

    final List<String> brandIds = getPimFeedTools().getUniqueBrandIds(pFeedId, pConnection);
    String brandId = null;
    int batchCount = 1;
    if (brandIds != null && !brandIds.isEmpty()) {

      final List<List<String>> batchLists = getImportTools().getBatchList(brandIds);
      for (List<String> brandSubList : batchLists) {

        batchBrandIdsList = brandSubList;
        if (isLoggingDebug()) {

          logDebug(new StringBuffer().append("brandSubList=").append(brandSubList).append(" Batch Count=")
              .append(batchCount).append(" Feed Id=").append(pFeedId).toString());
        }

        try {

          transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

          for (String id : batchBrandIdsList) {

            brandId = id;
            getImportTools().updateBrandsProperties(pFeedId, brandId, errorList, pConnection);
          }
          if (!StringUtils.isEmpty(brandId)) {

            brandIdsSuccessList.add(brandId);
          }
          batchCount++;
          rollback = false;
        } catch (TransactionDemarcationException e) {

          rollback = true;
          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          BBBPerformanceMonitor.cancel("updateBrand");
          isMonitorCanceled = true;
        } catch (BBBBusinessException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          brandIdsErrorList.add(batchBrandIdsList.toString());
          rollback = false;
          BBBPerformanceMonitor.cancel("updateBrand");
          isMonitorCanceled = true;
        } catch (IllegalArgumentException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          brandIdsErrorList.add(batchBrandIdsList.toString());
          BBBPerformanceMonitor.cancel("updateBrand");
          isMonitorCanceled = true;
          throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_ILLEGAL_ARGUMENT_EXC,"IllegalArgumentException", e);
        } finally {
          if (!isMonitorCanceled) {
            BBBPerformanceMonitor.end(IMPORT_PROCESS_MANAGER, "updateBrand");
          }
          try {
            transactionDemarcation.end(rollback);
          } catch (TransactionDemarcationException e) {

            if (isLoggingError()) {

              logError(BBBStringUtils.stack2string(e));
            }
          }
        }
      }
      int noOfRecordsSuccess = brandIdsSuccessList.size();
      int totalNoOfRecords = brandIds.size();
      if (!brandIds.isEmpty() && !brandIdsSuccessList.isEmpty()) {
        if (getPimFeedTools().hasFeedBadRecords(pFeedId, pConnection)) {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess)
                .append("Records processed sucessfully with errors for Total ").append(totalNoOfRecords)
                .append(" Records in time").append(getExecutionTime(startTime)).append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_BRANDS", formatDate(startTime), formatDate(getDate()), brandIdsSuccessList.size(),
              "updating brand completed with some errors", "", pConnection);
        } else {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess).append("Records processed sucessfully for Total ")
                .append(totalNoOfRecords).append(" Records in time").append(getExecutionTime(startTime))
                .append(" secs").toString());
          }
          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_BRANDS", formatDate(startTime), formatDate(getDate()), brandIdsSuccessList.size(),
              "updating brand completed", "", pConnection);

        }
      }
    } else {

      if (isLoggingDebug()) {

        logDebug("No data found for Updating Brand ");
      }
    }
    if (isLoggingDebug()) {
      logDebug("End of Updating Brand");
    }

  }

  /**
   * This method updates Dept details
   * 
   * 
   * @throws BBBSystemException
   * @throws BBBBusinessException
   */
  protected void updateClass(final String pFeedId, final Connection pConnection) throws BBBSystemException {
    // Create & update all categories
    boolean rollback = true;
    boolean isMonitorCanceled = false;
    // final Calendar calendar = getCalendar();
    // final Date startTime = getCurrentTime(calendar);
    final Date startTime = getDate();
    List<String> batchClassIdsList;
    final List<String> subClassIdsSuccessList = new ArrayList<String>();
    final List<String> subClassIdsErrorList = new ArrayList<String>();
    final List<String> errorList = new ArrayList<String>();
    final TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
    BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "updateClass");

    if (isLoggingDebug()) {
      logDebug("Start updating Class creation");
    }

    // get data from PIM ECP_MEDIA
    final List<String> ClassIds = getPimFeedTools().getUniqueClassIds(pFeedId, pConnection);
    String classId = null;
    int batchCount = 1;
    if (ClassIds != null && !ClassIds.isEmpty()) {

      final List<List<String>> batchLists = getImportTools().getBatchList(ClassIds);
      for (List<String> classSubList : batchLists) {

        batchClassIdsList = classSubList;
        if (isLoggingDebug()) {

          logDebug(new StringBuffer().append("classSubList=").append(classSubList).append(" Batch Count=")
              .append(batchCount).append(" Feed Id=").append(pFeedId).toString());
        }

        try {

          transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

          for (String id : batchClassIdsList) {

            classId = id;
            getImportTools().updateClassProperties(pFeedId, classId, errorList, pConnection);
          }
          if (!StringUtils.isEmpty(classId)) {

            subClassIdsSuccessList.add(classId);
          }
          batchCount++;
          rollback = false;
        } catch (TransactionDemarcationException e) {

          rollback = true;
          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          BBBPerformanceMonitor.cancel("updateClass");
          isMonitorCanceled = true;
        } catch (BBBBusinessException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          subClassIdsErrorList.add(batchClassIdsList.toString());
          rollback = false;
          BBBPerformanceMonitor.cancel("updateClass");
          isMonitorCanceled = true;
        } catch (IllegalArgumentException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          subClassIdsErrorList.add(batchClassIdsList.toString());
          BBBPerformanceMonitor.cancel("updateClass");
          isMonitorCanceled = true;
          throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_ILLEGAL_ARGUMENT_EXC,"IllegalArgumentException", e);
        } finally {
          if (!isMonitorCanceled) {
            BBBPerformanceMonitor.end(IMPORT_PROCESS_MANAGER, "updateClass");
          }
          try {
            transactionDemarcation.end(rollback);
          } catch (TransactionDemarcationException e) {

            if (isLoggingError()) {

              logError(BBBStringUtils.stack2string(e));
            }
          }
        }
      }
      int noOfRecordsSuccess = subClassIdsSuccessList.size();
      int totalNoOfRecords = ClassIds.size();
      if (!ClassIds.isEmpty() && !subClassIdsSuccessList.isEmpty()) {
        if (getPimFeedTools().hasFeedBadRecords(pFeedId, pConnection)) {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess)
                .append("Records processed sucessfully with errors for Total ").append(totalNoOfRecords)
                .append(" Records in time").append(getExecutionTime(startTime)).append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_CLASS", formatDate(startTime), formatDate(getDate()),
              subClassIdsSuccessList.size(), "updating class completed with some errors", "", pConnection);
        } else {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess).append("Records processed sucessfully for Total ")
                .append(totalNoOfRecords).append(" Records in time").append(getExecutionTime(startTime))
                .append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_CLASS", formatDate(startTime), formatDate(getDate()),
              subClassIdsSuccessList.size(), "updating class completed", "", pConnection);

        }
      }

    } else {

      if (isLoggingDebug()) {

        logDebug("No data found for Updating Class ");
      }
    }
    if (isLoggingDebug()) {
      logDebug("End of Updating Class");
    }

  }

  protected void updateSkuThreshold(final String pFeedId, final Connection pConnection) throws BBBSystemException {
    // Create & update all categories
    boolean rollback = true;
    boolean isMonitorCanceled = false;
    // final Calendar calendar = getCalendar();
    // final Date startTime = getCurrentTime(calendar);
    final Date startTime = getDate();
    List<String> batchThresholdIdsList;
    final List<String> thresholdIdsSuccessList = new ArrayList<String>();
    final List<String> thresholdIdsErrorList = new ArrayList<String>();
    final List<String> errorList = new ArrayList<String>();
    final TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
    BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "updateSkuThreshold");

    if (isLoggingDebug()) {
      logDebug("Start updating Sku Threshold");
    }

    final List<String> jdaDeptIds = getPimFeedTools().getUniqueThreshIds(pFeedId, pConnection);
    String jdaDeptId = null;
    int batchCount = 1;
    if (jdaDeptIds != null && !jdaDeptIds.isEmpty()) {

      final List<List<String>> batchLists = getImportTools().getBatchList(jdaDeptIds);
      for (List<String> idSubList : batchLists) {

        batchThresholdIdsList = idSubList;
        if (isLoggingDebug()) {

          logDebug(new StringBuffer().append("idSubList=").append(idSubList).append(" Batch Count=").append(batchCount)
              .append(" Feed Id=").append(pFeedId).toString());
        }

        try {

          transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

          for (String id : batchThresholdIdsList) {

            jdaDeptId = id;
            getImportTools().updateSkuThreshold(pFeedId, jdaDeptId, errorList, pConnection);
          }
          if (!StringUtils.isEmpty(jdaDeptId)) {

            thresholdIdsSuccessList.add(jdaDeptId);
          }
          batchCount++;
          rollback = false;
        } catch (TransactionDemarcationException e) {

          rollback = true;
          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          BBBPerformanceMonitor.cancel("updateSkuThreshold");
          isMonitorCanceled = true;
        } catch (BBBBusinessException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          thresholdIdsErrorList.add(batchThresholdIdsList.toString());
          rollback = false;
          BBBPerformanceMonitor.cancel("updateSkuThreshold");
          isMonitorCanceled = true;
        } catch (IllegalArgumentException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          thresholdIdsErrorList.add(batchThresholdIdsList.toString());
          BBBPerformanceMonitor.cancel("updateSkuThreshold");
          isMonitorCanceled = true;
          throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_ILLEGAL_ARGUMENT_EXC,"IllegalArgumentException", e);
        } finally {

          if (!isMonitorCanceled) {
            BBBPerformanceMonitor.end(IMPORT_PROCESS_MANAGER, "updateSkuThreshold");
          }
          try {
            transactionDemarcation.end(rollback);
          } catch (TransactionDemarcationException e) {

            if (isLoggingError()) {

              logError(BBBStringUtils.stack2string(e));
            }
          }
        }
      }

      int noOfRecordsSuccess = thresholdIdsSuccessList.size();
      int totalNoOfRecords = jdaDeptIds.size();
      if (!jdaDeptIds.isEmpty() && !thresholdIdsSuccessList.isEmpty()) {
        if (getPimFeedTools().hasFeedBadRecords(pFeedId, pConnection)) {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess)
                .append("Records processed sucessfully with errors for Total ").append(totalNoOfRecords)
                .append(" Records in time").append(getExecutionTime(startTime)).append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_SKU_THRESHOLD", formatDate(startTime), formatDate(getDate()),
              thresholdIdsSuccessList.size(), "update SkuThreshold completed with some errors", "", pConnection);
        } else {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess).append("Records processed sucessfully for Total ")
                .append(totalNoOfRecords).append(" Records in time").append(getExecutionTime(startTime))
                .append(" secs").toString());
          }

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_SKU_THRESHOLD", formatDate(startTime), formatDate(getDate()),
              thresholdIdsSuccessList.size(), "update SkuThreshold completed", "", pConnection);

        }
      }

    } else {

      if (isLoggingDebug()) {

        logDebug("No data found for Updating Sku Threshold for feedId= " + pFeedId);
      }
    }
    if (isLoggingDebug()) {
      logDebug("End of Updating Sku Threshold");
    }

  }

  
  
    
  /**
   * Associate SKU with  Region, Region would have all details like  ZIP CODE,Region Name
   * 
   * @param pFeedId
   * @param pSiteId
   * @throws BBBSystemException
   * @throws BBBBusinessException
   */
  protected void associateSkuWithRegion(final String pFeedId, final Connection pConnection) throws BBBSystemException {
	  boolean rollback = true;
	  boolean isMonitorCanceled = false;
	  final Date startTime = getDate();
	  final List<String> productIdsSuccessList = new ArrayList<String>();
	  final List<String> errorList = new ArrayList<String>();
	  List<String> batchSkuIdsList;
	  int batchCount = 1;
	  final TransactionDemarcation transaction = new TransactionDemarcation();
	  BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "associateSkuWithRegion");
	  if (isLoggingDebug()) {
		  logDebug("Start associateSkuWithRegion - Sku - ZIP Code Association with Feed Id :"+pFeedId);
	  }   
	  final List<String> skuIds = getPimFeedTools().getUniqueSkuZipRelIds(pFeedId, pConnection);    
	  if (skuIds != null && !skuIds.isEmpty()) {
		  if (isLoggingDebug()) {
			  logDebug("No of skus for SKU - ZIP CODE Association=" + skuIds.size());
		  }
		  final List<List<String>> batchLists = getImportTools().getBatchList(skuIds);
		  for (List<String> idList : batchLists) {
			  batchSkuIdsList = idList;
			  if (isLoggingDebug()) {
				  logDebug(new StringBuffer().append("Batch SKUIdSubList for SKU-ZIP Code Association=").append(idList)
						  .append(" Batch Count=").append(batchCount).append(" Feed Id=").append(pFeedId).toString());
			  }
			  try {
				  transaction.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);
				  for (String skuId : batchSkuIdsList) {
					  getImportTools().associateZipCodeWithSku(pFeedId, skuId, errorList, pConnection);
					  productIdsSuccessList.add(skuId);
				  }
				  batchCount++;
				  rollback = false;
			  } catch (TransactionDemarcationException e) {
				  if (isLoggingError()) {
					  logError(BBBStringUtils.stack2string(e));
				  }
				  rollback = true;
				  BBBPerformanceMonitor.cancel("associateSkuWithRegion");
				  isMonitorCanceled = true;
			  } catch (BBBBusinessException e) {
				  if (isLoggingError()) {
					  logError(BBBStringUtils.stack2string(e));
				  }
				  rollback = false;
				  BBBPerformanceMonitor.cancel("associateSkuWithRegion");
				  isMonitorCanceled = true;
			  } finally {
				  if (!isMonitorCanceled) {
					  BBBPerformanceMonitor.end(IMPORT_PROCESS_MANAGER, "associateSkuWithRegion");
				  }
				  try {
					  transaction.end(rollback);
				  } catch (TransactionDemarcationException e) {
					  if (isLoggingError()) {
						  logError(BBBStringUtils.stack2string(e));
					  }
				  }
			  }
		  }
		  int noOfRecordsSuccess = productIdsSuccessList.size();
		  if (!skuIds.isEmpty()) {
			  if (!errorList.isEmpty()) {
				  getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_SKU_REGION_RESTRICTIONS ", formatDate(startTime), formatDate(getDate()), noOfRecordsSuccess,
						  "associateSkuWithRegion completed with some errors", "", pConnection);
			  } else {
				  getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_SKU_REGION_RESTRICTIONS ", formatDate(startTime), formatDate(getDate()), noOfRecordsSuccess,
						  "associateSkuWithRegion completed", "", pConnection);
			  }
		  }
	  } else {
		  if (isLoggingDebug()) {
			  logDebug("No data found for Association of sku id with zip code  for Feed : " + pFeedId);
		  }
	  }
	  if (isLoggingDebug()) {
		  logDebug("End of associateSkuWithRegion");
	  }
  }  
  
  
  
  
  
  
  
  /**
   * Associate SKU with ECO SKU for provided FeedId
   * 
   * @param pFeedId
   * @param pSiteId
   * @throws BBBSystemException
   * @throws BBBBusinessException
   */
  protected void associateEcoSkuWithSku(final String pFeedId, final Connection pConnection) throws BBBSystemException {

    boolean rollback = true;
    boolean isMonitorCanceled = false;
    final Date startTime = getDate();
    final List<String> productIdsSuccessList = new ArrayList<String>();
    final List<String> errorList = new ArrayList<String>();
    List<String> batchSkuIdsList;
    int batchCount = 1;
    final TransactionDemarcation transaction = new TransactionDemarcation();
    BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "associateEcoSkuWithSku");
    if (isLoggingDebug()) {
      logDebug("Start associateEcoSkuWithSku - Sku - ECO Sku Association");
    }

    // get data from PIM Eco Skus
    final List<String> skuIds = getPimFeedTools().getUniqueEcoSkuRelIds(pFeedId, pConnection);

    if (skuIds != null && !skuIds.isEmpty()) {

      if (isLoggingDebug()) {

        logDebug("No of skus for ECO - SKU Association=" + skuIds.size());
      }
      final List<List<String>> batchLists = getImportTools().getBatchList(skuIds);

      for (List<String> idList : batchLists) {

        batchSkuIdsList = idList;
        if (isLoggingDebug()) {

          logDebug(new StringBuffer().append("Batch SKUIdSubList for ECO - SKU Association=").append(idList)
              .append(" Batch Count=").append(batchCount).append(" Feed Id=").append(pFeedId).toString());
        }
        try {

          transaction.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

          for (String skuId : batchSkuIdsList) {
            getImportTools().associateEcoSkuWithSku(pFeedId, skuId, errorList, pConnection);
            productIdsSuccessList.add(skuId);
          }
          batchCount++;
          rollback = false;
        } catch (TransactionDemarcationException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          rollback = true;
          BBBPerformanceMonitor.cancel("associateEcoSkuWithSku");
          isMonitorCanceled = true;
        } catch (BBBBusinessException e) {

          if (isLoggingError()) {

            logError(BBBStringUtils.stack2string(e));
          }
          rollback = false;
          BBBPerformanceMonitor.cancel("associateEcoSkuWithSku");
          isMonitorCanceled = true;
        } finally {
          if (!isMonitorCanceled) {
            BBBPerformanceMonitor.end(IMPORT_PROCESS_MANAGER, "associateEcoSkuWithSku");
          }
          try {
            transaction.end(rollback);
          } catch (TransactionDemarcationException e) {

            if (isLoggingError()) {

              logError(BBBStringUtils.stack2string(e));
            }
          }
        }
      }
      int noOfRecordsSuccess = productIdsSuccessList.size();
      if (!skuIds.isEmpty()) {

        if (!errorList.isEmpty()) {

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_ECO_ITEMS", formatDate(startTime), formatDate(getDate()), noOfRecordsSuccess,
              "associateEcoSkuWithSku completed with some errors", "", pConnection);
        } else {

          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_ECO_ITEMS", formatDate(startTime), formatDate(getDate()), noOfRecordsSuccess,
              "associateEcoSkuWithSku completed", "", pConnection);
        }

      }

    } else {

      if (isLoggingDebug()) {

        logDebug("No data found for associateEcoSkuWithSku - Eco Sku Association feedId= " + pFeedId);
      }
    }
    if (isLoggingDebug()) {

      logDebug("End of associateEcoSkuWithSku - Eco-Sku Association");
    }

  }
  
  
  
 /**
 * Converts date format and returns date as string
 * @param sqlDate
 * @return date as string in format used
 */
protected String formatDate(java.sql.Date sqlDate){
	  
	  String dateAsString = null;
	  Format formatter = null;
	  formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
	  dateAsString = formatter.format(sqlDate);
	  
	  return dateAsString;
  }
 protected void updateRegionZIPCodes(final String pFeedId, final Connection pConnection) throws BBBSystemException {
	    // Create & update all Zip Codes
	    boolean rollback = true;
	    boolean isMonitorCanceled = false;
	    // final Calendar calendar = getCalendar();
	    // final Date startTime = getCurrentTime(calendar);
	    final Date startTime = getDate();
	    List<String> batchZipCodeRegionIdsList;
	    final List<String> regionIdsSuccessList = new ArrayList<String>();
	    final List<String> regionIdsErrorList = new ArrayList<String>();
	    final List<String> errorList = new ArrayList<String>();
	    final TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
	    BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "updateZipCodeRegion");

	    if (isLoggingDebug()) {
	      logDebug("Start updating Zipcode Region creation");
	    }

	    final List<String> regionIds = getPimFeedTools().getUniqueZipCodeRegions(pFeedId, pConnection);
	    String regionId = null;
	    int batchCount = 1;
	    if (regionIds != null && !regionIds.isEmpty()) {

	      final List<List<String>> batchLists = getImportTools().getBatchList(regionIds);
	      for (List<String> regionSubList : batchLists) {

	        batchZipCodeRegionIdsList = regionSubList;
	        if (isLoggingDebug()) {

	          logDebug(new StringBuffer().append("RegionZipCodesSubList=").append(regionSubList).append(" Batch Count=")
	              .append(batchCount).append(" Feed Id=").append(pFeedId).toString());
	        }

	        try {

	          transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

	          for (String id : batchZipCodeRegionIdsList) {

	            regionId = id;
	            if (isLoggingDebug()) {
	      	      logDebug("Before updateZIPCodeRegion");
	      	    }
	            getImportTools().updateZIPCodeRegion(pFeedId, regionId, errorList, pConnection);
	            if (isLoggingDebug()) {
	      	      logDebug("After updateZIPCodeRegion");
	      	    }
	          }
	          if (!StringUtils.isEmpty(regionId)) {

	            regionIdsSuccessList.add(regionId);
	          }
	          batchCount++;
	          rollback = false;
	        } catch (TransactionDemarcationException e) {

	          rollback = true;
	          if (isLoggingError()) {

	            logError(BBBStringUtils.stack2string(e));
	          }
	          BBBPerformanceMonitor.cancel("updateRegionZipCodes");
	          isMonitorCanceled = true;
	        } catch (BBBBusinessException e) {

	          if (isLoggingError()) {

	            logError(BBBStringUtils.stack2string(e));
	          }
	          regionIdsErrorList.add(batchZipCodeRegionIdsList.toString());
	          rollback = false;
	          BBBPerformanceMonitor.cancel("updateZipCodeRegion");
	          isMonitorCanceled = true;
	        } catch (IllegalArgumentException e) {

	          if (isLoggingError()) {

	            logError(BBBStringUtils.stack2string(e));
	          }
	          regionIdsErrorList.add(batchZipCodeRegionIdsList.toString());
	          BBBPerformanceMonitor.cancel("updateBrand");
	          isMonitorCanceled = true;
	          throw new BBBSystemException("IllegalArgumentException", e);
	        } finally {
	          if (!isMonitorCanceled) {
	            BBBPerformanceMonitor.end(IMPORT_PROCESS_MANAGER, "updateZipCodeRegion");
	          }
	          try {
	            transactionDemarcation.end(rollback);
	          } catch (TransactionDemarcationException e) {

	            if (isLoggingError()) {

	              logError(BBBStringUtils.stack2string(e));
	            }
	          }
	        }
	      }
	      int noOfRecordsSuccess = regionIdsSuccessList.size();
	      int totalNoOfRecords = regionIds.size();
	      if (!regionIds.isEmpty() && !regionIdsSuccessList.isEmpty()) {
	        if (getPimFeedTools().hasFeedBadRecords(pFeedId, pConnection)) {
	          if (isLoggingDebug()) {

	            logDebug(new StringBuffer().append(noOfRecordsSuccess)
	                .append("Records processed sucessfully with errors for Total ").append(totalNoOfRecords)
	                .append(" Records in time").append(getExecutionTime(startTime)).append(" secs").toString());
	          }

	          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_REGION_ZIPCODES",formatDate(startTime), formatDate(getDate()), regionIdsSuccessList.size(),
	              "updating brand completed with some errors", "", pConnection);
	        } else {
	          if (isLoggingDebug()) {

	            logDebug(new StringBuffer().append(noOfRecordsSuccess).append("Records processed sucessfully for Total ")
	                .append(totalNoOfRecords).append(" Records in time").append(getExecutionTime(startTime))
	                .append(" secs").toString());
	          }
	          getPimFeedTools().updateFeedLog(pFeedId, "", "ECP_REGION_ZIPCODES", formatDate(startTime), formatDate(getDate()), regionIdsSuccessList.size(),
	              "updating ZIP Code Regions completed", "", pConnection);

	        }
	      }
	    } else {

	      if (isLoggingDebug()) {

	        logDebug("No data found for Updating Zip Code ");
	      }
	    }
	    if (isLoggingDebug()) {
	      logDebug("End of Updating Zip Code");
	    }

	  }

 	public void clearProdChildSkuUpdateRequiredList() {
 		getImportTools().getProdChildSkuUpdateRequiredList().clear();
 	}

 	public void clearCategoryChildProdUpdateRequiredList() {
 		getImportTools().getCategoryChildProdUpdateRequiredList().clear();
 	}
}
  
