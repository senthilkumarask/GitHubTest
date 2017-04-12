package com.bbb.importprocess.manager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

public class BBBProdCatalogImportManager extends BBBCatalogImportManager {

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

  /*  for (String feedId : pFeedIds) {

      
      getPimFeedTools().updateFeedStatus(feedId, "PRODUCTION_IN_PROGRESS", pConnection);
     

    }*/
	 super.importData(pFeedType, pFeedIds, pConnection, true);
  }

  
}
