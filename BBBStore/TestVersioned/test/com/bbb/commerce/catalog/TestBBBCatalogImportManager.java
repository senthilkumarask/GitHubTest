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
import com.bbb.importprocess.tools.BBBPIMFeedTools;
import com.bbb.utils.BBBStringUtils;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBCatalogImportManager extends BaseTestCase {

  public void closeConnection(Connection pConnection) {

    if (pConnection != null) {

      try {

        pConnection.close();

      } catch (SQLException e) {
      }
    }

  }

  private String getStatus(final Connection pConnection, final String pParamArg1) throws SQLException {

    ResultSet resultSet = null;
    String status = null;
    String query = "Select FEED_STATUS from ecp_feed_monitoring where feed_id= ?";
    PreparedStatement prepareStatement = null;
    try {

      prepareStatement = pConnection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      prepareStatement.setString(1, pParamArg1);

      resultSet = prepareStatement.executeQuery();

      if (resultSet.next()) {

        status = resultSet.getString("FEED_STATUS");

      }
    } catch (SQLException sqlException) {

    } finally {

      if (resultSet != null && !resultSet.isClosed()) {
        resultSet.close();
      }
      if (prepareStatement != null && !prepareStatement.isClosed()) {
        prepareStatement.close();
      }

    }

    if (!StringUtils.isEmpty(status)) {
      status = status.trim();
    }

    return status;
  }

  /**
   * 
   * @param pSiteId
   * @param pCategoryId
   * @return
   */
  private String getCategoryId(final String pSiteId, final String pCategoryId,
      BBBCatalogImportManager bbbCatalogImportManager) {

    String bbbSiteId = bbbCatalogImportManager.getImportTools().getPimSiteToBBBSiteMap().get(pSiteId);
    return pCategoryId;
  }

  /*
   * public void testImportData() throws Exception { BBBCatalogImportManager
   * manager = (BBBCatalogImportManager) getObject("manager"); final
   * BBBPIMFeedTools bbbPIMFeedTools = manager.getPimFeedTools(); String status
   * = null;
   * 
   * Connection connection = getConnection(); if (connection != null) {
   * 
   * final List<String> regularFeedIdList =
   * bbbPIMFeedTools.getPIMLatestFeed("Regular", connection); if
   * (!regularFeedIdList.isEmpty()) {
   * 
   * manager.executeImport("Regular", regularFeedIdList, connection);
   * 
   * } status = getStatus(connection, "1");
   * System.out.println("++++++++++++++++++" + status); }
   * bbbPIMFeedTools.updateFeedStatus("1", "OPEN", connection);
   * closeConnection(connection);
   * 
   * addObjectToAssert("status", "CLOSED"); }
   */
  
  /**
   * Test method to Test Sku - RebateInfo Association
   * 
   * @throws Exception
   */
  public void testRebateRelnSku() throws Exception {

    final String feedId = (String) getObject("feedId");
    final String skuIdId = (String) getObject("skuId");
    final String childRebateId = (String) getObject("childRebateId");

    final boolean testResult = associateSkuRebate(feedId, skuIdId, childRebateId);
    addObjectToAssert("containsAssociation", testResult);
  }
  
 
  
  
  /**
   * Test method to Test Product - ProductTab Association
   * 
   * @throws Exception
   */
  public void testProductTabRelnProduct() throws Exception {

    final String feedId = (String) getObject("feedId");
    final String productId = (String) getObject("productId");
    final String childProductTabId = (String) getObject("childProductTabId");

    final boolean testResult = associateProductProductTab(feedId, productId, childProductTabId);
    addObjectToAssert("containsAssociation", testResult);
  }
  
  
  
  /**
   * Test method to validate price update in the price list
   * 
   * @throws Exception
   */
  public void testPricingImportData() throws Exception {

    final String feedId = (String) getObject("feedId");
    final String skuId = (String) getObject("skuId");
    final String priceListId = (String) getObject("priceListId");

    final BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    final double resultPrice = updatePricing(feedId, skuId, priceListId);
    if (manager.isLoggingDebug()) {

      manager.logDebug("Testing asset ");
    }
    addObjectToAssert("testResultPrice", resultPrice);
  }

  
  
  /**
   * Test method to validate category created or deleted successfully
   * 
   * @throws Exception
   */
  public void testCategoryImportData() throws Exception {

    final String feedId = (String) getObject("feedId");
    final String siteId = (String) getObject("siteId");
    final String categoryId = (String) getObject("categoryId");

    final BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    final String testResultCategoryId = createCategory(feedId, siteId, categoryId);
    if (manager.isLoggingDebug()) {

      manager.logDebug("Testing asset ");
    }
    addObjectToAssert("resultRepositoryId", testResultCategoryId);
  }

  
  
  /**
   * Test method to Test category subCategory Association
   * 
   * @throws Exception
   */
  public void testCatRootCatAssociationImportData() throws Exception {

    final String feedId = (String) getObject("feedId");
    final String siteId = (String) getObject("siteId");
    final String childCategoryId = (String) getObject("childCategoryId");

    final boolean testResult = associateCategoryWithRootCategory(feedId, siteId, childCategoryId);
    addObjectToAssert("containsAssociation", testResult);
  }

  
 
  /**
   * Sub Method of testCategoryImportData. Method used to create project and
   * call CatalogLoadTools().createUpdateCategory method to create or delete
   * category. After calling method its deleted the project and return to the
   * calling method.
   * 
   * @param pFeedId
   * @param pSiteId
   * @param categoryId
   * @param pChildCategoryId
   * @return
   * @throws BBBSystemException
   * @throws SQLException
 * @throws BBBBusinessException 
   */
  @SuppressWarnings("unchecked")
  private boolean associateCategoryWithRootCategory(final String pFeedId, final String pSiteId,
      final String pChildCategoryId) throws BBBSystemException, SQLException, BBBBusinessException {
	  List<String> catId = new ArrayList<String>();
    BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    BBBCatalogLoadTools bbbCatalogLoadTools = manager.getImportTools();
    getLogs();
    boolean rollback = true;
    Connection connection = getConnection();

    final String categoryId = bbbCatalogLoadTools.getSiteIdRootCategoryIdMap().get(pSiteId);
    catId =  manager.getImportTools().getConfigTools().getAllValuesForKey("ContentCatalogKeys",categoryId);
    manager.logDebug("categoryId=" + catId);
   
    
    String testCatRepostioryId = catId.get(0);
    
    final String testChildCatRepostioryId = pChildCategoryId;
    MutableRepositoryItem categoryItem = null;
    MutableRepositoryItem childCategoryItem = null;
    String testResultCategoryId = null;
    final List<String> errorList = new ArrayList<String>();
    List<RepositoryItem> childCategoriesList = null;
    boolean testResult = false;

    final String taskOutcomeId = "4.2.1";

    if (manager.isLoggingDebug()) {

      manager.logDebug("execute test case for root category =" + testCatRepostioryId);
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
        manager.logDebug("Start Category SubCategory Association");
        manager.logDebug("feedId=" + pFeedId + " siteId=" + pSiteId + " categoryId=" + testCatRepostioryId
            + "childCategoryId=" + pChildCategoryId);
      }

      manager.getImportTools().associateCategoriesWithRootCategory(pSiteId,pFeedId, testCatRepostioryId,
          errorList, connection, true);

      if (manager.isLoggingDebug()) {

        manager.logDebug("Category SubCategory Association Completed");
      }

      // Get Test Records from Version Repository
      categoryItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(testCatRepostioryId,
          "category");
      childCategoryItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(
          testChildCatRepostioryId, "category");
      if (categoryItem != null) {

        testResultCategoryId = categoryItem.getRepositoryId();
     

      childCategoriesList = (List<RepositoryItem>) categoryItem.getPropertyValue("fixedChildCategories");
      if (childCategoriesList != null && childCategoryItem != null) {

        testResult = childCategoriesList.contains(childCategoryItem);
        if (manager.isLoggingDebug()) {

          manager.logDebug("childCategoriesList size=" + childCategoriesList.size());
        }
      }
      }

      if (manager.isLoggingDebug()) {

        manager.logDebug("testResultCategoryId=" + testResultCategoryId);
        manager.logDebug("Calling Advance workflow to delete project");
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

 
  
  

  /**
   * Test method to Test Product - Media Association
   * 
   * @throws Exception
   */
  public void testMediaRelnWithProduct() throws Exception {

    final String feedId = (String) getObject("feedId");
    final String productId = (String) getObject("productId");
    final String childMediaId = (String) getObject("childMediaId");

    final boolean testResult = associateProductMedia(feedId, productId, childMediaId);
    addObjectToAssert("containsAssociation", testResult);
  }

  
  /**
   * Test method to Test Sku - Attribute Association
   * 
   * @throws Exception
   */
  public void testAttributesRelnWithSku() throws Exception {

    final String feedId = (String) getObject("feedId");
    final String skuIdId = (String) getObject("skuId");
    final String childAttributeId = (String) getObject("attributeId");
    final String valueId = (String) getObject("valueId");

    final boolean testResult = associateSkuAttributes(feedId, skuIdId, childAttributeId,valueId);
    addObjectToAssert("containsAssociation", testResult);
  }
  
 
  /**
   * Sub Method of testCategoryImportData. Method used to create project and
   * call CatalogLoadTools().createUpdateCategory method to create or delete
   * category. After calling method its deleted the project and return to the
   * calling method.
   * 
   * @param feedId
   * @param siteId
   * @param categoryId
   * @return
   * @throws BBBSystemException
   * @throws SQLException
   */
  private String createCategory(final String feedId, final String siteId, final String categoryId)
      throws BBBSystemException, SQLException {
    BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    BBBCatalogLoadTools bbbCatalogLoadTools = manager.getImportTools();
    getLogs();
    boolean rollback = true;
    Connection connection = getConnection();
    final String categoryRepostioryId = getCategoryId(siteId, categoryId, manager);
    String testResultCategoryId = null;

    final String taskOutcomeId = manager.getFailureOutcomeId();
    MutableRepositoryItem categoryItem = null;
    if (manager.isLoggingDebug()) {
      manager.logDebug("execute test case for category creation cagtegoryId=" + categoryId + " categoryRepostioryId="
          + categoryRepostioryId);
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
        manager.logDebug("Start Category Creation");
        manager.logDebug("feedId=" + feedId + " siteId=" + siteId + " categoryId=" + categoryId);
      }
      manager.getImportTools().createUpdateCategory(feedId, categoryId, connection, true);

      if (manager.isLoggingDebug()) {
        manager.logDebug("Import Data Completed");
      }
      categoryItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(categoryRepostioryId,
          "category");
      if (categoryItem != null) {
	          testResultCategoryId = categoryItem.getRepositoryId();
      }
     
      
      if (manager.isLoggingDebug()) {

        manager.logDebug("testResultCategoryId=" + testResultCategoryId);
        manager.logDebug("Calling Advance workflow to delete project");
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

    return testResultCategoryId;
  }



  /**
   * Test method to Test category subCategory Association
   * 
   * @throws Exception
   */
  public void testCatSubCatAssociationImportData() throws Exception {

    final String feedId = (String) getObject("feedId");
    final String siteId = (String) getObject("siteId");
    final String categoryId = (String) getObject("categoryId");
    final String childCategoryId = (String) getObject("childCategoryId");

    final boolean testResult = associateCategorySubCategory(feedId, siteId, categoryId, childCategoryId);
    addObjectToAssert("containsAssociation", testResult);
  }

  /**
   * Sub Method of testCategoryImportData. Method used to create project and
   * call CatalogLoadTools().createUpdateCategory method to create or delete
   * category. After calling method its deleted the project and return to the
   * calling method.
   * 
   * @param feedId
   * @param siteId
   * @param categoryId
   * @param pChildCategoryId
   * @return
   * @throws BBBSystemException
   * @throws SQLException
   */
  @SuppressWarnings("unchecked")
  private boolean associateCategorySubCategory(final String feedId, final String siteId, final String categoryId,
      final String pChildCategoryId) throws BBBSystemException, SQLException {
    BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    BBBCatalogLoadTools bbbCatalogLoadTools = manager.getImportTools();
    getLogs();
    boolean rollback = true;
    Connection connection = getConnection();

    final String testCatRepostioryId = categoryId;
    final String testChildCatRepostioryId = pChildCategoryId;
    MutableRepositoryItem categoryItem = null;
    MutableRepositoryItem childCategoryItem = null;
    String testResultCategoryId = null;
    final List<String> errorList = new ArrayList<String>();
    List<RepositoryItem> childCategoriesList = null;
    boolean testResult = false;

    final String taskOutcomeId = "4.2.1";

    if (manager.isLoggingDebug()) {

      manager.logDebug("execute test case for category creation cagtegoryId=" + categoryId + " categoryRepostioryId="
          + testCatRepostioryId + " testChildCatRepostioryId=" + testChildCatRepostioryId);
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
        manager.logDebug("Start Category SubCategory Association");
        manager.logDebug("feedId=" + feedId + " siteId=" + siteId + " categoryId=" + categoryId);
      }

      manager.getImportTools().associateCategoryWithSubCategory(feedId, categoryId, errorList, connection, false);

      if (manager.isLoggingDebug()) {

        manager.logDebug("Category SubCategory Association Completed");
      }

      // Get Test Records from Version Repository
      categoryItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(testCatRepostioryId,
          "category");
      childCategoryItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(
          testChildCatRepostioryId, "category");
      if (categoryItem != null) {

        testResultCategoryId = categoryItem.getRepositoryId();
      

      childCategoriesList = (List<RepositoryItem>) categoryItem.getPropertyValue("fixedChildCategories");
      if (childCategoriesList != null && childCategoryItem != null) {

        testResult = childCategoriesList.contains(childCategoryItem);
        if (manager.isLoggingDebug()) {

          manager.logDebug("childCategoriesList size=" + childCategoriesList.size());
        }
      }
      }

      if (manager.isLoggingDebug()) {

        manager.logDebug("testResultCategoryId=" + testResultCategoryId);
   manager.logDebug("Calling Advance workflow to delete project");
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
 /**
   * Test method to validate item created or deleted successfully
   * 
   * @throws Exception
   */
  public void testItemImportData() throws Exception {

    final String feedId = (String) getObject("feedId");
    final String siteId = (String) getObject("siteId");
    final String itemId = (String) getObject("itemId");
    final boolean isProduct = (Boolean) getObject("isProduct");
    final boolean isFrequent = (Boolean) getObject("isFrequent");
    final boolean isRare = (Boolean) getObject("isRare");

    final BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    final String testResultItemId = createItem(feedId, siteId, itemId, isProduct, isFrequent, isRare);
    if (manager.isLoggingDebug()) {

      manager.logDebug("Testing asset ");
    }
    addObjectToAssert("resultRepositoryId", testResultItemId);
  }

  /**
   * Sub Method of testItemImportData. Method used to create Item and call
   * CatalogLoadTools().createItem method to create or delete item. After
   * calling method its deleted the project and return to the calling method.
   * 
   * @param pFeedId
   * @param pSiteId
   * @param pItemId
   * @return
   * @throws BBBSystemException
   * @throws SQLException
   */
  private String createItem(final String pFeedId, final String pSiteId, final String pItemId, final boolean pIsProduct,
      final boolean pIsFrequent, final boolean pIsRare) throws BBBSystemException, SQLException {
    BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    BBBCatalogLoadTools bbbCatalogLoadTools = manager.getImportTools();
    getLogs();
    boolean rollback = true;
    Connection connection = getConnection();
    final String itemRepostioryId = pItemId;
    String testResultItemId = null;

    final String taskOutcomeId = "4.2.1";
    MutableRepositoryItem item = null;
    if (manager.isLoggingDebug()) {
      manager.logDebug("execute test case for category creation itemId=" + pItemId + " itemRepostioryId="
          + itemRepostioryId);
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
        manager.logDebug("Start Item Creation");
        manager.logDebug("feedId=" + pFeedId + " siteId=" + pSiteId + " itemId=" + pItemId);
      }
      manager.getImportTools().updateItem(pFeedId, pItemId, pIsFrequent, pIsRare, connection);

      if (manager.isLoggingDebug()) {
        manager.logDebug("Import Data Completed");
      }
      if (pIsProduct) {
        item = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(itemRepostioryId, "product");
      } else {
        item = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(itemRepostioryId, "sku");
      }

      if (item != null) {

        testResultItemId = item.getRepositoryId();
      }
      if (manager.isLoggingDebug()) {

        manager.logDebug("testResultItemId=" + testResultItemId);
        manager.logDebug("Calling Advance workflow to delete project");
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

    return testResultItemId;
  }
 
  /**
   * Sub Method of testPricingImportData. Method used to create project and call
   * CatalogLoadTools().updatePricing method to update pricing. After calling
   * method its deleted the project and return to the calling method.
   * 
   * @param pFeedId
   * @param siteId
   * @param categoryId
   * @return
   * @throws BBBSystemException
   * @throws SQLException
   */
  private double updatePricing(final String pFeedId, final String pSkuId, final String pPriceListId)
      throws BBBSystemException, SQLException {
    BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    BBBCatalogLoadTools bbbCatalogLoadTools = manager.getImportTools();
    getLogs();
    boolean rollback = true;
    Connection connection = getConnection();

    double resultPrice = 0.0;

    final String taskOutcomeId = manager.getFailureOutcomeId();
    RepositoryItem priceItem = null;
    if (manager.isLoggingDebug()) {
      manager.logDebug("execute test case for update pricing for pSkuId=" + pSkuId);
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
        manager.logDebug("Start Update Pricing");
        manager.logDebug("feedId=" + pFeedId + " skuId=" + pSkuId + " pPriceListId=" + pPriceListId);
      }
      manager.getImportTools().updatePricing(pFeedId, pSkuId, connection);
      if (manager.isLoggingDebug()) {
        manager.logDebug("Import Data Completed");
      }
      priceItem = bbbCatalogLoadTools.getPriceListManager().getPrice(pPriceListId, null, pSkuId);
      if (priceItem != null) {

        resultPrice = (Double) priceItem.getPropertyValue("listPrice");
      }
      if (manager.isLoggingDebug()) {

        manager.logDebug("resultPrice=" + resultPrice);
        manager.logDebug("Calling Advance workflow to delete project");
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

    return resultPrice;
  }

  /**
   * Test method to validate Dept Info created or deleted successfully
   * 
   * @throws Exception
   */
  public void testDeptImportData() throws Exception {

    final String feedId = (String) getObject("feedId");
    final String deptId = (String) getObject("id");

    final BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    final String testResultDeptId = updateDept(feedId, deptId);
    if (manager.isLoggingDebug()) {

      manager.logDebug("Testing asset ");
    }
    addObjectToAssert("resultRepositoryId", testResultDeptId);
  }

  
  /**
   * Sub Method of testCategoryImportData. Method used to create project and
   * call CatalogLoadTools().updateDeptProperties method to create or delete Dept
   * Info. After calling method its deleted the project and return to the
   * calling method.
   * 
   * @param feedId
   * @param pDeptId
   * @return
   * @throws BBBSystemException
   * @throws SQLException
   */
  private String updateDept(final String feedId, final String pDeptId) throws BBBSystemException, SQLException {
    BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    BBBCatalogLoadTools bbbCatalogLoadTools = manager.getImportTools();
    getLogs();
    final List<String> errorList = new ArrayList<String>();
    boolean rollback = true;
    Connection connection = getConnection();

    String testResultDeptId = null;

    final String taskOutcomeId = manager.getFailureOutcomeId();
    MutableRepositoryItem deptItem = null;
    if (manager.isLoggingDebug()) {
      manager.logDebug("execute test case for Dept creation DeptId=" + pDeptId);
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
        manager.logDebug("Start Dept Creation");
        manager.logDebug("feedId=" + feedId + " pDeptId=" + pDeptId);
      }
      manager.getImportTools().updateDept(feedId, pDeptId,errorList, connection);

      if (manager.isLoggingDebug()) {
        manager.logDebug("Import Data Completed");
      }
      deptItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(pDeptId, "jdaDept");
      if (deptItem != null) {

        testResultDeptId = deptItem.getRepositoryId();
      }
      if (manager.isLoggingDebug()) {

        manager.logDebug("testResultDeptId=" + testResultDeptId);
        manager.logDebug("Calling Advance workflow to delete project");
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

    return testResultDeptId;
  }


  /**
   * Test method to validate SubDept Info created or deleted successfully
   * 
   * @throws Exception
   */
  public void testSubDeptImportData() throws Exception {

    final String feedId = (String) getObject("feedId");
    final String deptId = (String) getObject("deptId");
    final String subDeptId = (String) getObject("subDeptId");

    final BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    final String testResultSubDeptId = updateSubDept(feedId,deptId, subDeptId);
    if (manager.isLoggingDebug()) {

      manager.logDebug("Testing asset ");
    }
    addObjectToAssert("resultRepositoryId", testResultSubDeptId);
  }

  
  /**
   * Sub Method of testCategoryImportData. Method used to create project and
   * call CatalogLoadTools().updateSubDeptProperties method to create or delete SubDept
   * Info. After calling method its deleted the project and return to the
   * calling method.
   * 
   * @param feedId
   * @param pSubDeptId
   * @return
   * @throws BBBSystemException
   * @throws SQLException
   */
  private String updateSubDept(final String feedId,final String pDeptId, final String pSubDeptId) throws BBBSystemException, SQLException {
    BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    BBBCatalogLoadTools bbbCatalogLoadTools = manager.getImportTools();
    getLogs();
    final List<String> errorList = new ArrayList<String>();
    boolean rollback = true;
    Connection connection = getConnection();

    String testResultSubDeptId = null;
    String subDeptRepostioryId = getSubId(pDeptId,pSubDeptId);
    final String taskOutcomeId = manager.getFailureOutcomeId();
    MutableRepositoryItem subDeptItem = null;
    if (manager.isLoggingDebug()) {
      manager.logDebug("execute test case for SubDept creation pSubDeptId=" + pSubDeptId);
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
        manager.logDebug("Start SubDept Creation");
        manager.logDebug("feedId=" + feedId + " pSubDeptId=" + pSubDeptId);
      }
      manager.getImportTools().updateSubDeptProperties(feedId, pSubDeptId,errorList, connection);

      if (manager.isLoggingDebug()) {
        manager.logDebug("Import Data Completed");
      }
      subDeptItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(subDeptRepostioryId, "jdaSubDept");
      if (subDeptItem != null) {

    	  testResultSubDeptId = subDeptItem.getRepositoryId();
      }
      if (manager.isLoggingDebug()) {

        manager.logDebug("testResultSubDeptId=" + testResultSubDeptId);
        manager.logDebug("Calling Advance workflow to delete project");
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

    return testResultSubDeptId;
  }


  /**
   * Test method to validate SubDept Info created or deleted successfully
   * 
   * @throws Exception
   */
 public void testBrandImportData() throws Exception {

    final String feedId = (String) getObject("feedId");
	 
    final String brandId = (String) getObject("brandId");

    final BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    final String testResultBrandId = updateBrand(feedId, brandId);
    if (manager.isLoggingDebug()) {

      manager.logDebug("Testing asset ");
    }
    addObjectToAssert("resultRepositoryId", testResultBrandId);
  }

  
  /**
   * Sub Method of testCategoryImportData. Method used to create project and
   * call CatalogLoadTools().updateSubDeptProperties method to create or delete SubDept
   * Info. After calling method its deleted the project and return to the
   * calling method.
   * 
   * @param feedId
   * @param pSubDeptId
   * @return
   * @throws BBBSystemException
   * @throws SQLException
   */
  private String updateBrand(final String feedId, final String pBrandId) throws BBBSystemException, SQLException {
    BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    BBBCatalogLoadTools bbbCatalogLoadTools = manager.getImportTools();
    getLogs();
    final List<String> errorList = new ArrayList<String>();
    boolean rollback = true;
    Connection connection = getConnection();

    String testResultBrandId = null;

    final String taskOutcomeId = manager.getFailureOutcomeId();
    MutableRepositoryItem BrandItem = null;
    if (manager.isLoggingDebug()) {
      manager.logDebug("execute test case for Brand creation pBrandId=" + pBrandId);
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
        manager.logDebug("Start Brand Creation");
        manager.logDebug("feedId=" + feedId + " pBrandId=" + pBrandId);
      }
      manager.getImportTools().updateBrandsProperties(feedId, pBrandId,errorList, connection);

      if (manager.isLoggingDebug()) {
        manager.logDebug("Import Data Completed");
      }
      BrandItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(pBrandId, "bbbBrand");
      if (BrandItem != null) {

    	  testResultBrandId = BrandItem.getRepositoryId();
      }
      if (manager.isLoggingDebug()) {

        manager.logDebug("testResultBrandId=" + testResultBrandId);
        manager.logDebug("Calling Advance workflow to delete project");
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

    return testResultBrandId;
  }

 

  /**
   * Test method to validate SubDept Info created or deleted successfully
   * 
   * @throws Exception
   */
 public void testSkuThresholdImportData() throws Exception {

    final String feedId = (String) getObject("feedId");
    final String jdaDeptId = (String) getObject("jdaDeptId");
    final String jdaSubDeptId = (String) getObject("jdaSubDeptId");
    final String skuId = (String) getObject("skuId");
    final String classId = (String) getObject("classId");
    final BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    final String testResultSkuThresholdId = updateSkuThreshold(feedId,jdaDeptId,jdaSubDeptId,skuId,classId);
    if (manager.isLoggingDebug()) {

      manager.logDebug("Testing asset ");
    }
    addObjectToAssert("resultRepositoryId", testResultSkuThresholdId);
  }

  
  /**
   * Sub Method of testCategoryImportData. Method used to create project and
   * call CatalogLoadTools().updateSubDeptProperties method to create or delete SubDept
   * Info. After calling method its deleted the project and return to the
   * calling method.
   * 
   * @param feedId
   * @param pSubDeptId
   * @return
   * @throws BBBSystemException
   * @throws SQLException
   */
  private String updateSkuThreshold(final String feedId,final String pJdaDeptId,final String pJdaSubDeptId,final String pSkuId, final String pClassId) throws BBBSystemException, SQLException {
    BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    BBBCatalogLoadTools bbbCatalogLoadTools = manager.getImportTools();
    getLogs();
    final List<String> errorList = new ArrayList<String>();
    boolean rollback = true;
    Connection connection = getConnection();

    String testResultSkuThresholdId = null;
    final String skuThresholdId= getThresholdId(pJdaDeptId,pJdaSubDeptId,pClassId,pSkuId);
    final String taskOutcomeId = manager.getFailureOutcomeId();
    MutableRepositoryItem skuThresholdItem = null;
    if (manager.isLoggingDebug()) {
      manager.logDebug("execute test case for SkuThreshold creation pSkuThresholdId=" + skuThresholdId);
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
        manager.logDebug("Start SubDept Creation");
        manager.logDebug("feedId=" + feedId + " pSkuThresholdId=" + skuThresholdId);
      }
      manager.getImportTools().updateSkuThreshold(feedId, pJdaDeptId,errorList, connection);

      if (manager.isLoggingDebug()) {
        manager.logDebug("Import Data Completed");
      }
      skuThresholdItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(skuThresholdId, "skuThresholds");
      if (skuThresholdItem != null) {

    	  testResultSkuThresholdId = skuThresholdItem.getRepositoryId();
      }
      if (manager.isLoggingDebug()) {

        manager.logDebug("testResultSkuThresholdId=" + testResultSkuThresholdId);
        manager.logDebug("Calling Advance workflow to delete project");
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

    return testResultSkuThresholdId;
  }

  private String getClassId(final String pId1, final String pId2,final String pId3) {
	    return pId1 + ":" + pId2 + ":" +pId3;
	  }
  
  private String getId(final String pId1, final String pId2,final String pId3) {
	    return pId1 + "_" + pId2 + "_" +pId3;
	  }
  
  
  private String getSubId(final String pId1, final String pId2) {
	    return pId1 + "_" + pId2;
	  }
  
  private String getThresholdId(final String pId1, final String pId2,final String pId3,final String pId4) {
	    return pId1 + "_" + pId2 + "_" +pId3 +"_" +pId4;
	  }
  
  /**
   * Test method to validate Dept Info created or deleted successfully
   * 
   * @throws Exception
   */
  public void testClassImportData() throws Exception {

    final String feedId = (String) getObject("feedId");
    final String classId = (String) getObject("classId");
    final String deptId = (String) getObject("deptId");
    final String subDeptId = (String) getObject("subDeptId");
  

    final BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    final String testResultClassId = updateClass(feedId, classId,deptId,subDeptId);
    if (manager.isLoggingDebug()) {

      manager.logDebug("Testing asset ");
    }
    addObjectToAssert("resultRepositoryId", testResultClassId);
  }

  
  /**
   * Sub Method of testCategoryImportData. Method used to create project and
   * call CatalogLoadTools().updateClassProperties method to create or delete Class
   * Info. After calling method its deleted the project and return to the
   * calling method.
   * 
   * @param feedId
   * @param pDeptId
   * @return
   * @throws BBBSystemException
   * @throws SQLException
   */
  private String updateClass(final String feedId, final String pClassId,final String pDeptId,final String pSubDeptId) throws BBBSystemException, SQLException {
    BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    BBBCatalogLoadTools bbbCatalogLoadTools = manager.getImportTools();
    
    final String classID= getClassId(pDeptId,pSubDeptId,pClassId);
    getLogs();
    final List<String> errorList = new ArrayList<String>();
    boolean rollback = true;
    Connection connection = getConnection();

    String testResultClassId = null;

    final String taskOutcomeId = manager.getFailureOutcomeId();
    MutableRepositoryItem ClassItem = null;
    if (manager.isLoggingDebug()) {
      manager.logDebug("execute test case for Class creation ClassId=" + pClassId);
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
        manager.logDebug("Start Class Creation");
        manager.logDebug("feedId=" + feedId + " pClassId=" + pClassId);
      }
      manager.getImportTools().updateClassProperties(feedId, pClassId,errorList, connection);

      if (manager.isLoggingDebug()) {
        manager.logDebug("Import Data Completed");
      }
      ClassItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(classID, "class");
      if (ClassItem != null) {

        testResultClassId = ClassItem.getRepositoryId();
      }
      if (manager.isLoggingDebug()) {

        manager.logDebug("testResultClassId=" + testResultClassId);
        manager.logDebug("Calling Advance workflow to delete project");
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

    return testResultClassId;
  }

 

  /**
   * Test method to Test category subCategory Association
   * 
   * @throws Exception
   */
  public void testCatProdAssociationImportData() throws Exception {

    final String feedId = (String) getObject("feedId");
    final String siteId = (String) getObject("siteId");
    final String categoryId = (String) getObject("categoryId");
    final String childProductId = (String) getObject("childProductId");

    final boolean testResult = associateCategoryProd(feedId, siteId, categoryId, childProductId);
    addObjectToAssert("containsAssociation", testResult);
  }

  /**
   * Sub Method of testCategoryImportData. Method used to create project and
   * call CatalogLoadTools().createUpdateCategory method to create or delete
   * category. After calling method its deleted the project and return to the
   * calling method.
   * 
   * @param feedId
   * @param siteId
   * @param categoryId
   * @param pChildProdId
   * @return
   * @throws BBBSystemException
   * @throws SQLException
   */
  @SuppressWarnings("unchecked")
  private boolean associateCategoryProd(final String feedId, final String siteId, final String categoryId,
      final String pChildProdId) throws BBBSystemException, SQLException {
    BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    BBBCatalogLoadTools bbbCatalogLoadTools = manager.getImportTools();
    getLogs();
    boolean rollback = true;
    Connection connection = getConnection();

    final String testCatRepostioryId = categoryId;
    final String testChildProdRepostioryId = pChildProdId;
    MutableRepositoryItem categoryItem = null;
    MutableRepositoryItem childProductItem = null;
    String testResultCategoryId = null;
    final List<String> errorList = new ArrayList<String>();
    List<RepositoryItem> childProductList = null;
    boolean testResult = false;

    final String taskOutcomeId = "4.2.1";

    if (manager.isLoggingDebug()) {

      manager.logDebug("execute test case for category creation cagtegoryId=" + categoryId + " categoryRepostioryId="
          + testCatRepostioryId + " testChildProdRepostioryId=" + testChildProdRepostioryId);
      manager.logDebug("Start Category - Project Association");
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
        manager.logDebug("Start Category Project Association");
        manager.logDebug("feedId=" + feedId + " siteId=" + siteId + " categoryId=" + categoryId + " childProdId="
            + pChildProdId);
      }

      manager.getImportTools().associateProductsWithCategory(siteId, categoryId, feedId, errorList, connection, false);

      if (manager.isLoggingDebug()) {

        manager.logDebug("Category SubCategory Association Completed");
      }

      // Get Test Records from Version Repository
      categoryItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(testCatRepostioryId,
          "category");
      childProductItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(
          testChildProdRepostioryId, "product");
      if (categoryItem != null) {

        testResultCategoryId = categoryItem.getRepositoryId();
     

      childProductList = (List<RepositoryItem>) categoryItem.getPropertyValue("fixedChildProducts");
      if (childProductList != null && childProductItem != null) {

        testResult = childProductList.contains(childProductItem);
        if (manager.isLoggingDebug()) {

          manager.logDebug("childCategoriesList size=" + childProductList.size());
        }
      }
      }

      if (manager.isLoggingDebug()) {

        manager.logDebug("testResultCategoryId=" + testResultCategoryId);
        manager.logDebug("Calling Advance workflow to delete project");
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

  /**
   * Test method to validate Rebate Info created or deleted successfully
   * 
   * @throws Exception
   */
  public void testRebateImportData() throws Exception {

    final String feedId = (String) getObject("feedId");
    final String rebateId = (String) getObject("rebateId");

    final BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    final String testResultRebateId = createRebateInfo(feedId, rebateId);
    if (manager.isLoggingDebug()) {

      manager.logDebug("Testing asset ");
    }
    addObjectToAssert("resultRepositoryId", testResultRebateId);
  }


  
  /**
   * Test method to validate Attributes Info created or deleted successfully
   * 
   * @throws Exception
   */
  public void testAttributesImportData() throws Exception {

    final String feedId = (String) getObject("feedId");

    final String attributeId = (String) getObject("attributeId");
    final String valueId = (String) getObject("valueId");

    final BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    final String testResultAttributeId = createAttributes(feedId, attributeId,valueId);
    if (manager.isLoggingDebug()) {

      manager.logDebug("Testing asset ");
    }
    addObjectToAssert("resultRepositoryId", testResultAttributeId);
  }

  /**
   * Test method to validate Attributes Info created or deleted successfully
   * 
   * @throws Exception
   */
  public void testMediaImportData() throws Exception {

    final String feedId = (String) getObject("feedId");

    final String mediaId = (String) getObject("mediaId");

    final BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    final String testResultMediaId = createMedia(feedId, mediaId);
    if (manager.isLoggingDebug()) {

      manager.logDebug("Testing asset ");
    }
    addObjectToAssert("resultRepositoryId", testResultMediaId);
  }
  

 

  /**
   * Sub Method of testCategoryImportData. Method used to create project and
   * call CatalogLoadTools().updateRebateInfo method to create or delete Rebate
   * Info. After calling method its deleted the project and return to the
   * calling method.
   * 
   * @param feedId
   * @param siteId
   * @param pRebateId
   * @return
   * @throws BBBSystemException
   * @throws SQLException
   */
  private String createRebateInfo(final String feedId, final String pRebateId) throws BBBSystemException, SQLException {
    BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    BBBCatalogLoadTools bbbCatalogLoadTools = manager.getImportTools();
    getLogs();
    boolean rollback = true;
    Connection connection = getConnection();

    String testResultRebateId = null;

    final String taskOutcomeId = manager.getFailureOutcomeId();
    MutableRepositoryItem rebateItem = null;
    if (manager.isLoggingDebug()) {
      manager.logDebug("execute test case for Rebate creation RebateId=" + pRebateId);
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
        manager.logDebug("Start Rebate Creation");
        manager.logDebug("feedId=" + feedId + " pRebateId=" + pRebateId);
      }
      manager.getImportTools().updateRebateInfo(feedId, pRebateId, connection);

      if (manager.isLoggingDebug()) {
        manager.logDebug("Import Data Completed");
      }
      rebateItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(pRebateId, "rebateInfo");
      if (rebateItem != null) {

        testResultRebateId = rebateItem.getRepositoryId();
      }
      if (manager.isLoggingDebug()) {

        manager.logDebug("testResultRbateId=" + testResultRebateId);
        manager.logDebug("Calling Advance workflow to delete project");
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

    return testResultRebateId;
  }

  
  /**
   * Sub Method of testCategoryImportData. Method used to create project and
   * call CatalogLoadTools().updateAttribute method to create or delete
   * Attribute. After calling method its deleted the project and return to the
   * calling method.
   * 
   * @param feedId
   * @param siteId
   * @param pAttributeId
   * @return
   * @throws BBBSystemException
   * @throws SQLException
   */
  private String createAttributes(final String feedId, final String pAttributeId,final String pValueId) throws BBBSystemException,
      SQLException {

    BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    BBBCatalogLoadTools bbbCatalogLoadTools = manager.getImportTools();
    getLogs();
    boolean rollback = true;
    Connection connection = getConnection();
    String itemAttributeId = getSubId(pAttributeId,pValueId);
    String testAttributeId = null;

    final String taskOutcomeId = manager.getFailureOutcomeId();
    MutableRepositoryItem attributeItem = null;
    if (manager.isLoggingDebug()) {
      manager.logDebug("execute test case for attribute creation for pAttributeId=" + pAttributeId);
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
        manager.logDebug("Start Attributes Creation");
        manager.logDebug("feedId=" + feedId + " pItemAttributeId=" + pAttributeId);
      }
      manager.getImportTools().updateItemAttributesProperties(feedId, pAttributeId, connection);

      if (manager.isLoggingDebug()) {
        manager.logDebug("Import Data Completed");
      }
      attributeItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(itemAttributeId,
          "attributeInfo");
      if (attributeItem != null) {

        testAttributeId = attributeItem.getRepositoryId();
      }
      if (manager.isLoggingDebug()) {

        manager.logDebug("testResultAttributeId=" + testAttributeId);
        manager.logDebug("Calling Advance workflow to delete project");
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

    return testAttributeId;
  }



  /**
   * Sub Method of testMediaImportData. Method used to create project and call
   * CatalogLoadTools().updateMedia method to create or delete Media. After
   * calling method its deleted the project and return to the calling method.
   * 
   * @param feedId
   * @param siteId
   * @param pMediaId
   * @return
   * @throws BBBSystemException
   * @throws SQLException
   */
  private String createMedia(final String feedId, final String pMediaId) throws BBBSystemException, SQLException {

    BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    BBBCatalogLoadTools bbbCatalogLoadTools = manager.getImportTools();
    getLogs();
    boolean rollback = true;
    Connection connection = getConnection();

    String testMediaId = null;

    final String taskOutcomeId = manager.getFailureOutcomeId();
    MutableRepositoryItem mediaItem = null;
    if (manager.isLoggingDebug()) {
      manager.logDebug("execute test case for media creation for pMediaId=" + pMediaId);
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
        manager.logDebug("Start Media Creation");
        manager.logDebug("feedId=" + feedId + " pMediaId=" + pMediaId);
      }
      manager.getImportTools().updateOtherMediaProperties(feedId, pMediaId, connection);

      if (manager.isLoggingDebug()) {
        manager.logDebug("Import Data Completed");
      }
      mediaItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(pMediaId, "bbbOtherMedia");
      if (mediaItem != null) {

        testMediaId = mediaItem.getRepositoryId();
      }
      if (manager.isLoggingDebug()) {

        manager.logDebug("testResultMediaId=" + testMediaId);
        manager.logDebug("Calling Advance workflow to delete project");
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

    return testMediaId;
  }

  

  /**
   * Sub Method of testRebateRelnSku. Method used to a project and call
   * CatalogLoadTools().associateRebateRelnWithSku method to associate Sku -
   * Rebate relationship. After calling method its deleted the project and
   * return to the calling method.
   * 
   * @param feedId
   * @param pSkuId
   * @param pChildRebateId
   * @return
   * @throws BBBSystemException
   * @throws SQLException
   */
  @SuppressWarnings("unchecked")
  private boolean associateSkuRebate(final String pFeedId, final String pSkuId, final String pChildRebateId)
      throws BBBSystemException, SQLException {
    BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    BBBCatalogLoadTools bbbCatalogLoadTools = manager.getImportTools();
    getLogs();
    boolean rollback = true;
    Connection connection = getConnection();

    final String testSkuRepostioryId = pSkuId;
    final String testChildRebateRepostioryId = pChildRebateId;
    MutableRepositoryItem skuItem = null;
    MutableRepositoryItem childRebateItem = null;

    final List<String> errorList = new ArrayList<String>();
    Set<RepositoryItem> childRebateSet = null;
    boolean testResult = false;

    final String taskOutcomeId = "4.2.1";

    if (manager.isLoggingDebug()) {

      manager.logDebug("execute test case for sku creation pSkuId=" + pSkuId + " testSkuRepostioryId="
          + testSkuRepostioryId + " testChildRebateRepostioryId=" + testChildRebateRepostioryId);
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
        manager.logDebug("Start Sku Rebate Association");
        manager.logDebug("feedId=" + pFeedId + " SkuId=" + pSkuId + " childRebateId=" + pChildRebateId);
      }

     
      manager.getImportTools().associateRebateRelnWithSku(pFeedId, pSkuId, connection, false);

      if (manager.isLoggingDebug()) {

        manager.logDebug("Sku Rebate Association Completed");
      }

      // Get Test Records from Version Repository
      skuItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(testSkuRepostioryId, "sku");
      childRebateItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(
          testChildRebateRepostioryId, "rebateInfo");
      if (skuItem != null) {

        childRebateSet = (Set<RepositoryItem>) skuItem.getPropertyValue("rebates");
        if (childRebateSet != null && childRebateItem != null) {

          testResult = childRebateSet.contains(childRebateItem);
          if (manager.isLoggingDebug()) {

            manager.logDebug("childRebateSet size=" + childRebateSet.size());
            manager.logDebug("childRebateSet=" + childRebateSet);
          }
        }

        if (manager.isLoggingDebug()) {

          manager.logDebug("Calling Advance workflow to delete project");
        }
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



  /**
   * Sub Method of testAttributesRelnWithSku. Method used to a project and call
   * CatalogLoadTools().associateSkuAttributes method to associate Sku -
   * Attribute relationship. After calling method its deleted the project and
   * return to the calling method.
   * 
   * @param feedId
   * @param pSkuId
   * @param pChildAttributeId
   * @return
   * @throws BBBSystemException
   * @throws SQLException
   */
  @SuppressWarnings("unchecked")
  private boolean associateSkuAttributes(final String pFeedId, final String pSkuId, final String pChildAttributeId,final String pValueId)
      throws BBBSystemException, SQLException {
    BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    BBBCatalogLoadTools bbbCatalogLoadTools = manager.getImportTools();
    getLogs();
    boolean rollback = true;
    Connection connection = getConnection();

    final String testSkuRepostioryId = pSkuId;
    final String testChildAttributeRepostioryId = pChildAttributeId;
    MutableRepositoryItem skuItem = null;
    MutableRepositoryItem childAttributeItem = null;

    String testAttributeRepositoryId = getId(pSkuId, pChildAttributeId, pValueId);
    final List<String> errorList = new ArrayList<String>();
    Set<RepositoryItem> childAttributeSet = null;
    boolean testResult = false;

    final String taskOutcomeId = "4.2.1";

    if (manager.isLoggingDebug()) {

      manager.logDebug("execute test case for sku creation pSkuId=" + pSkuId + " testSkuRepostioryId="
          + testSkuRepostioryId + " testChildAttributeRepostioryId=" + testChildAttributeRepostioryId);
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
        manager.logDebug("Start Sku Attribute Association");
        manager.logDebug("feedId=" + pFeedId + " SkuId=" + pSkuId + " childAttributeId=" + pChildAttributeId);
      }

      manager.getImportTools().associateSkuAttributes(pFeedId, pSkuId, errorList, connection, false);

      if (manager.isLoggingDebug()) {

        manager.logDebug("Sku Attribute Association Completed");
      }

      // Get Test Records from Version Repository
      skuItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(testSkuRepostioryId, "sku");
      childAttributeItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(
    		  testAttributeRepositoryId, "bbbskuAttrReln");
      if (skuItem != null) {

        childAttributeSet = (Set<RepositoryItem>) skuItem.getPropertyValue("skuAttributeRelns");
        if (childAttributeSet != null && childAttributeItem != null) {

          testResult = childAttributeSet.contains(childAttributeItem);
          if (manager.isLoggingDebug()) {

            manager.logDebug("childAttributeSet size=" + childAttributeSet.size());
            manager.logDebug("childAttributeSet=" + childAttributeSet);
          }
        }

        if (manager.isLoggingDebug()) {

          manager.logDebug("Calling Advance workflow to delete project");
        }
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



  /**
   * Sub Method of testProductTabRelnSku. Method used to a project and call
   * CatalogLoadTools().createUpdateProductTabs method to create tabs or update tab and associate Product -
   * ProductTab relationship. After calling method its deleted the project and
   * return to the calling method.
   * 
   * @param feedId
   * @param pProductId
   * @param pChildProductTabId
   * @return
   * @throws BBBSystemException
   * @throws SQLException
   */
  @SuppressWarnings("unchecked")
  private boolean associateProductProductTab(final String pFeedId, final String pProductId, final String pChildProductTabId)
      throws BBBSystemException, SQLException {
    BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    BBBCatalogLoadTools bbbCatalogLoadTools = manager.getImportTools();
    getLogs();
    boolean rollback = true;
    Connection connection = getConnection();

    final String testProductRepostioryId = pProductId;
    final String testChildProductTabRepostioryId = pChildProductTabId;
    MutableRepositoryItem ProductItem = null;
    MutableRepositoryItem childProductTabItem = null;

    final List<String> errorList = new ArrayList<String>();
    List<RepositoryItem> childProductTabList = null;
    boolean testResult = false;

    final String taskOutcomeId = "4.2.1";

    if (manager.isLoggingDebug()) {

      manager.logDebug("execute test case for Product creation pProductId=" + pProductId + " testProductRepostioryId="
          + testProductRepostioryId + " testChildProductTabRepostioryId=" + testChildProductTabRepostioryId);
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
        manager.logDebug("Start Product ProductTab Association");
        manager.logDebug("feedId=" + pFeedId + " ProductId=" + pProductId + " childProductTabId=" + pChildProductTabId);
      }

      manager.getImportTools().createUpdateProductTabs(pFeedId, pProductId, errorList, connection, false);

      if (manager.isLoggingDebug()) {

        manager.logDebug("Product ProductTab Association Completed");
      }

      // Get Test Records from Version Repository
      ProductItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(testProductRepostioryId, "product");
      childProductTabItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(
          testChildProductTabRepostioryId, "productTab");
      if (ProductItem != null) {

        childProductTabList = (List<RepositoryItem>) ProductItem.getPropertyValue("productTabs");
        if (childProductTabList != null && childProductTabItem != null) {

          testResult = childProductTabList.contains(childProductTabItem);
          if (manager.isLoggingDebug()) {

            manager.logDebug("childProductTabSet size=" + childProductTabList.size());
            manager.logDebug("childProductTabSet=" + childProductTabList);
          }
        }

        if (manager.isLoggingDebug()) {

          manager.logDebug("Calling Advance workflow to delete project");
        }
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
  private boolean associateProductMedia(final String pFeedId, final String pProductId, final String pChildMediaId)
      throws BBBSystemException, SQLException {
    BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    BBBCatalogLoadTools bbbCatalogLoadTools = manager.getImportTools();
    getLogs();
    boolean rollback = true;
    Connection connection = getConnection();

    final String testProductRepostioryId = pProductId;
    final String testChildMediaRepostioryId = pChildMediaId;
    MutableRepositoryItem ProductItem = null;
    MutableRepositoryItem childMediaItem = null;

    final List<String> errorList = new ArrayList<String>();
    List<RepositoryItem> childMediaList = null;
    boolean testResult = false;

    final String taskOutcomeId = "4.2.1";

    if (manager.isLoggingDebug()) {

      manager.logDebug("execute test case for Product creation pProductId=" + pProductId + " testProductRepostioryId="
          + testProductRepostioryId + " testChildMediaRepostioryId=" + testChildMediaRepostioryId);
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
        manager.logDebug("Start Product Media Association");
        manager.logDebug("feedId=" + pFeedId + " ProductId=" + pProductId + " childMediaId=" + pChildMediaId);
      }

      manager.getImportTools().associateMediaRelnWithProduct(pFeedId, pProductId, errorList, connection, false);

      if (manager.isLoggingDebug()) {

        manager.logDebug("Product Media Association Completed");
      }

      // Get Test Records from Version Repository
      ProductItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(testProductRepostioryId, "product");
      childMediaItem = (MutableRepositoryItem) bbbCatalogLoadTools.getProductCatalog().getItem(
          testChildMediaRepostioryId, "bbbOtherMedia");
      if (ProductItem != null) {

        childMediaList = (List<RepositoryItem>) ProductItem.getPropertyValue("mediaReln");
        if (childMediaList != null && childMediaItem != null) {

          testResult = childMediaList.contains(childMediaItem);
          if (manager.isLoggingDebug()) {

            manager.logDebug("childMediaList size=" + childMediaList.size());
            manager.logDebug("childMediaList=" + childMediaList);
          }
        }

        if (manager.isLoggingDebug()) {

          manager.logDebug("Calling Advance workflow to delete project");
        }
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
 
  
    /**
   * Test method to validate Dept Info created or deleted successfully
   * 
   * @throws Exception
   */
   /* public void testSEOImportData() throws Exception {

    final String feedId = (String) getObject("feedId");
    final String seoTagId = (String) getObject("seoTagId");
    final String displayName = (String) getObject("displayName");
    final String title = (String) getObject("title");
    final String description = (String) getObject("description");
    final String keywords = (String) getObject("keywords");
    final String contentKey = (String) getObject("contentKey");
    final boolean isTranslation = (Boolean) getObject("isTranslation");
    final String babTitle = (String) getObject("babTitle");
    final String caTitle = (String) getObject("caTitle");
   

     final BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    final String testResultSeoTagId = updateSeoTags(feedId,seoTagId,displayName,title,description,keywords,contentKey, isTranslation,babTitle,caTitle);
    if (manager.isLoggingDebug()) {

      manager.logDebug("Testing asset ");
    }
    addObjectToAssert("resultRepositoryId", testResultSeoTagId);
  }
*/
  
  /**
   * Sub Method of testCategoryImportData. Method used to create project and
   * call CatalogLoadTools().updateDeptProperties method to create or delete Dept
   * Info. After calling method its deleted the project and return to the
   * calling method.
   * 
   * @param feedId
   * @param pDeptId
   * @return
   * @throws BBBSystemException
   * @throws SQLException
   */
  private String updateSeoTags(final String pFeedId,final String pSeoTagId, final String pDisplayName, final String pTitle,
	      final String pDescription, final String pKeywords, final String pContentKey, final boolean isTranslation,final String babTitle,final String caTitle) throws BBBSystemException, SQLException {
    BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
    BBBCatalogLoadTools bbbCatalogLoadTools = manager.getImportTools();
    getLogs();
    boolean rollback = true;
    Connection connection = getConnection();

    String testResultSeoTagId = null;

    final String taskOutcomeId = manager.getFailureOutcomeId();
    MutableRepositoryItem seoItem = null;
    if (manager.isLoggingDebug()) {
      manager.logDebug("execute test case for SEO creation pSeoTagId=" + pSeoTagId);
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
        manager.logDebug("Start SEO Creation");
        manager.logDebug("feedId=" + pFeedId + " pSeoTagId=" + pSeoTagId);
      }
      manager.getImportTools().updateSeoTags(pFeedId,pSeoTagId, pDisplayName, pTitle,pDescription, pKeywords,pContentKey, isTranslation,babTitle,caTitle);

      if (manager.isLoggingDebug()) {
        manager.logDebug("Import Data Completed");
      }
      seoItem = (MutableRepositoryItem) bbbCatalogLoadTools.getSeoRepository().getItem(pSeoTagId, "SEOTags");
      if (seoItem != null) {

        testResultSeoTagId = seoItem.getRepositoryId();
      }
      if (manager.isLoggingDebug()) {

        manager.logDebug("testResultSeoTagId=" + testResultSeoTagId);
        manager.logDebug("Calling Advance workflow to delete project");
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

    return testResultSeoTagId;
  }
  
  private void getLogs() {
	  BBBCatalogImportManager manager = (BBBCatalogImportManager) getObject("manager");
	  BBBCatalogLoadTools bbbCatalogLoadTools = manager.getImportTools();
	  BBBPIMFeedTools bbbPIMFeedTools = manager.getPimFeedTools();
      bbbCatalogLoadTools.setLoggingDebug(true);
      bbbPIMFeedTools.setLoggingDebug(true);
  }
  
  private String getWorkflowName() {
	  
   return "/Common/commonWorkflow.wdl";
	//return "/staging/pimWorkflowProd.wdl";
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
