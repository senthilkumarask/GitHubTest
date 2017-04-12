/**
 * 
 */
package com.bbb.importprocess.manager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBStringUtils;

/**
 * 
 * 
 */
public class BBBPricingImportManager extends AbstractBBBImportManager {

  private static final String IMPORT_PROCESS_MANAGER = "importprocess_manager";

  // -------------------------------------
  // property: projectName
  // -------------------------------------

  // -----------------------------------------
  // methods
  // ----------------------------------------

  public static Date getDate() {

    java.util.Date date = new java.util.Date();
    long t = date.getTime();
    java.sql.Date sqlDate = new java.sql.Date(t);
    // java.sql.Time sqlTime = new java.sql.Time(t);
    // java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(t);
    return sqlDate;
  }

  public static long getExecutionTime(Date pStartDate) {

    java.util.Date date = new java.util.Date();
    final long t = date.getTime() - pStartDate.getTime();
    final long diffSeconds = t / 1000;
    // java.sql.Time sqlTime = new java.sql.Time(t);
    // java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(t);
    return diffSeconds;
  }

  /**
   * This method is used to import data to version repository feed feed is
   * executed
   * @param pFeedType Current feed type
   * @param pFeedIds List of feedIds which is going to be imported
   * @throws BBBSystemException
   * @throws BBBBusinessException
   * @throws SQLException
   * 
   */

  @Override
protected void importData(final String pFeedType, final List<String> pFeedIds, final Connection pConnection,
      final boolean isProductionImport) throws BBBSystemException {

    // updated the states
    for (final String feedId : pFeedIds) {

      // Update Pricing
      updatePricing(feedId, pConnection);

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
    final Date startTime = getDate();
    List<String> batchSkuIdsList;
    final List<String> skuIdsSuccessList = new ArrayList<String>();
    final TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
    BBBPerformanceMonitor.start(IMPORT_PROCESS_MANAGER, "updatePricing");

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
      for (final List<String> skuIdsSubList : batchLists) {

        batchSkuIdsList = skuIdsSubList;
        if (isLoggingDebug()) {

          logDebug(new StringBuffer().append("Batch skuIdsSubList=").append(skuIdsSubList).append(" Batch Count=")
              .append(batchCount).append(" Feed Id=").append(pFeedId).toString());
        }

        try {

          transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

          for (final String skuId : batchSkuIdsList) {

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
          getPimFeedTools().updatePricingBadRecords(pFeedId, "", "ECP_PRICING", "PRICING not done for batch=" +batchCount, getImportTools().getDate(), batchLists.toString(), pConnection);
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

      final int noOfRecordsSuccess = skuIdsSuccessList.size();
      final int totalNoOfRecords = skuIds.size();
      if (!skuIds.isEmpty() && !skuIdsSuccessList.isEmpty()) {

        if (noOfRecordsSuccess <= totalNoOfRecords) {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess)
                .append("Records processed sucessfully with errors for Total ").append(totalNoOfRecords)
                .append(" Records in time").append(getExecutionTime(startTime)).append(" secs").toString());
          }

          getPimFeedTools().updatePricingFeedLog(pFeedId, "", "ECP_SKU_PRICING_NEW", formatDate(startTime), formatDate(getDate()), totalNoOfRecords,
              "Update Pricing completed with some errors", "", pConnection);
        } else {
          if (isLoggingDebug()) {

            logDebug(new StringBuffer().append(noOfRecordsSuccess).append("Records processed sucessfully for Total ")
                .append(totalNoOfRecords).append(" Records in time").append(getExecutionTime(startTime))
                .append(" secs").toString());
          }

          getPimFeedTools().updatePricingFeedLog(pFeedId, "", "ECP_SKU_PRICING_NEW", formatDate(startTime), formatDate(getDate()), totalNoOfRecords,
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
 * Converts date format and returns date as string
 * @param sqlDate
 * @return date as string in format used
 */
public static String formatDate(java.sql.Date sqlDate){
	  
	  String dateAsString = null;
	  Format formatter = null;
	  formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
	  dateAsString = formatter.format(sqlDate);
	  
	  return dateAsString;
  }
  
}
  
