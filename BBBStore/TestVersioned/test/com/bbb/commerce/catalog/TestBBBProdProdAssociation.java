package com.bbb.commerce.catalog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.epub.project.Process;
import atg.process.action.ActionException;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.versionmanager.WorkingContext;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.importprocess.manager.BBBCatalogImportManager;
import com.bbb.importprocess.tools.BBBCatalogLoadTools;
import com.bbb.utils.BBBStringUtils;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBProdProdAssociation extends BaseTestCase {

  public void closeConnection(Connection pConnection) {

    if (pConnection != null) {

      try {

        pConnection.close();

      } catch (SQLException e) {
      }
    }

  }

 
  private String getId(final String pId1, final String pId2) {
	    return pId1 + "_" + pId2;
	  }


  
 
  
  /**
   * Test method to Test Product - Product Association
   * 
   * @throws Exception
   */
  public void testProdProdAssociation() throws Exception {

    final String feedId = (String) getObject("feedId");
    final String productId = (String) getObject("productId");
    final String childId = (String) getObject("childId");
    final String relatedType = (String) getObject("relatedType");
    final boolean testResult = associateProductProduct(feedId, productId, childId,relatedType);
    addObjectToAssert("containsAssociation", testResult);
  }
 

  /**
   * Sub Method of testMediaRelnSku. Method used to a project and call
   * CatalogLoadTools().createUpdateMedias method to create tabs or update tab and associate Product -
   * Media relationship. After calling method its deleted the project and
   * return to the calling method.
   * 
   * @param feedId
   * @param pProductId
   * @param pChildMediaId
   * @return
   * @throws BBBSystemException
   * @throws SQLException
   */
  @SuppressWarnings("unchecked")
  
  private boolean associateProductProduct(final String pFeedId, final String pProductId, final String pChildId, final String pRelatedType)
  throws BBBSystemException, SQLException {
    BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    BBBCatalogLoadTools bbbCatalogLoadTools = manager.getImportTools();
    getLogs();
    boolean rollback = true;
    Connection connection = getConnection();

    final String testProductRepostioryId = pProductId;
    final String testChildRepostioryId = pChildId;
   
    MutableRepositoryItem ProductItem = null;
    MutableRepositoryItem childProductItem = null;
   

    final List<String> errorList = new ArrayList<String>();
    List<RepositoryItem> childProductList = null;
   
    boolean testResult = false;

    final String taskOutcomeId = "4.2.1";

    if (manager.isLoggingDebug()) {

      manager.logDebug("execute test case for Product creation pProductId=" + pProductId + " testProductRepostioryId="
          + testProductRepostioryId + " testChildProductRepostioryId=" + testChildRepostioryId);
      manager.logDebug("Start Project Creation");
    }
    final TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
    Process currentProcess = null;

    final String workflowName = getWorkflowName();
    final String projectName = manager.getProjectName();

    try {

      transactionDemarcation.begin(manager.getTransactionManager());
      currentProcess = manager.createProject(projectName, workflowName);
      if (manager.isLoggingDebug()) {

        manager.logDebug("Project Creation Completed");
        manager.logDebug("projectName=" + projectName + " workflowName=" + workflowName);
        manager.logDebug("Start Product Product Association");
        manager.logDebug("feedId=" + pFeedId + " ProductId=" + pProductId + " pChildId=" + pChildId);
      }

      manager.getImportTools().associateProductWithSku(pFeedId, pProductId, errorList, connection, false);

    
     if(pRelatedType.contains("PR")){
      // Get Test Records from Version Repository
      ProductItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(testProductRepostioryId, "product");
      childProductItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(
    		  testChildRepostioryId, "sku");
      if (ProductItem != null) {

        childProductList = (List<RepositoryItem>) ProductItem.getPropertyValue("childSKUs");
     
 }
      }
     else if (pRelatedType.contains("SW")) {
    	  // Get Test Records from Version Repository
          ProductItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(testProductRepostioryId, "product");
          childProductItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(
        		  testChildRepostioryId, "product");
          if (ProductItem != null) {

        	  childProductList = (List<RepositoryItem>) ProductItem.getPropertyValue("fixedRelatedProducts");
           }
      }
     
     else if (pRelatedType.contains("AC") || pRelatedType.contains("CO")) {
    	  
    	  final String childProdId = getId(testProductRepostioryId, testChildRepostioryId);
    	  // Get Test Records from Version Repository
          ProductItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(testProductRepostioryId, "product");
          childProductItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(
        		  childProdId, "bbbPrdReln");
          if (ProductItem != null) {

            childProductList = (List<RepositoryItem>) ProductItem.getPropertyValue("productChildProducts");
           
          }
      }
      if (ProductItem != null) {

         
          if (childProductList != null && childProductList != null) {

            testResult = childProductList.contains(childProductItem);
            if (manager.isLoggingDebug()) {

              manager.logDebug("childProductList size=" + childProductList.size());
              manager.logDebug("childProductList=" + childProductList);
            }
          }

          if (manager.isLoggingDebug()) {

            manager.logDebug("Calling Advance workflow to delete project");
          }
        }
      if (manager.isLoggingDebug()) {

          manager.logDebug("Product Product Association Completed");
        }
      manager.advanceWorkflow(currentProcess, taskOutcomeId);

      if (manager.isLoggingDebug()) {

        manager.logDebug("Completed Task");
      }
      rollback = false;
    } catch (TransactionDemarcationException e) {

      if (manager.isLoggingError()) {

        manager.logError(BBBStringUtils.stack2string(e));
      }
      rollback = true;
      throw new BBBSystemException(e.getMessage(), e);
    } catch (ActionException e) {
      manager.logError("SERVICE: Advance Workflow Failed: ActionException: " + e.getMessage());
      rollback = false;
    } catch (Exception e) {

      if (manager.isLoggingError()) {

        manager.logError(BBBStringUtils.stack2string(e));
      }
      rollback = true;
      throw new BBBSystemException(e.getMessage(), e);
    } finally {

      WorkingContext.popDevelopmentLine();
      manager.releaseUserIdentity();
      try {

        transactionDemarcation.end(rollback);
      } catch (TransactionDemarcationException tde) {
        if (manager.isLoggingError()) {

          manager.logError(BBBStringUtils.stack2string(tde));
        }
      }
      connection.close();
    }

    return testResult;
  }
     
  
  private void getLogs() {
	  BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
	  BBBCatalogLoadTools bbbCatalogLoadTools = manager.getImportTools();
      bbbCatalogLoadTools.setLoggingDebug(true);
  }
  
  private String getWorkflowName() {
	  
	return "/Common/commonWorkflow.wdl";
}
  
 

private Connection getConnection() {
    Connection con = null;
    try {

      Class.forName("oracle.jdbc.driver.OracleDriver");
      con = DriverManager.getConnection("jdbc:oracle:thin:@10.210.4.246:1521:bbbload", "bbb_pim_stg_4_20", "bbb_pim_stg_4_20");

    } catch (Exception e) {
      e.printStackTrace();
    }
    return con;
  }

}
