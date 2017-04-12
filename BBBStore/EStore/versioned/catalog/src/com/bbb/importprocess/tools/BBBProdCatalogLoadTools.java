package com.bbb.importprocess.tools;


import java.sql.Connection;
import atg.repository.MutableRepositoryItem;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

public class BBBProdCatalogLoadTools extends BBBCatalogLoadTools {
  
  /**
   * 
   * @param pSiteId
   * @param pFeedId
   * @param pItemId
   * @param isFrequent
   * @throws BBBBusinessException
   */
/*
  public void updateItem(final String pFeedId, final String pItemId, final boolean pIsFrequent,
      final boolean pIsRare, Connection pConnection) throws BBBSystemException, BBBBusinessException {

    if (isLoggingDebug()) {
      logDebug("ProductionCatalogTools Method Name [updateItem]");
      logDebug("pFeedId[" + pFeedId + "]  pItemId[" + pItemId + "] pIsFrequent[" + pIsFrequent + "] pIsRare[" + pIsRare
          + "] ");
    }

    MutableRepositoryItem item = getItem(pItemId, BBBCatalogImportConstant.PRODUCT_DESC);
    
    if (item != null) {
      
      updateProductProperties(item);
    }  else {
      
      item = getItem(pItemId, BBBCatalogImportConstant.SKU_DESC);
      if(item != null) {
        
        updateSkuProperties(item);
      } else {
        String error = pItemId + " Item not found in Versioned for Production Deployment";
        logDebug(error);
        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_ITEMS", pItemId, getDate(), error, pConnection);
      }
        
    }
    if (isLoggingDebug()) {

      logDebug("End of updateItem");
    }
  }
   */


 /* private void updateSkuProperties(MutableRepositoryItem pItem) {
    
    if (isLoggingDebug()) {
      
      logDebug("ProductionCatalogTools Method Name [createUpdateItem]");
    }
    try {
    simplePropertyWrite("LastModifiedDate", getLastModifiedDate(), pItem);
  }
*/
/*  private void updateProductProperties(MutableRepositoryItem pItem) {
    
    simplePropertyWrite("LastModifiedDate", getLastModifiedDate(), pItem);
  }
*/
}
