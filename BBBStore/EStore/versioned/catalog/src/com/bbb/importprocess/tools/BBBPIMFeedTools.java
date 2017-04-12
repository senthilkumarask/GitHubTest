package com.bbb.importprocess.tools;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import atg.core.util.StringUtils;
import atg.epub.project.Project;
import atg.nucleus.GenericService;

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.importprocess.vo.AttributeVO;
import com.bbb.importprocess.vo.BrandVO;
import com.bbb.importprocess.vo.CategoryVO;
import com.bbb.importprocess.vo.EcoSkuVO;
import com.bbb.importprocess.vo.ItemMediaVO;
import com.bbb.importprocess.vo.ItemVO;
import com.bbb.importprocess.vo.MediaVO;
import com.bbb.importprocess.vo.MiscVO;
import com.bbb.importprocess.vo.OperationVO;
import com.bbb.importprocess.vo.ProdSkuVO;
import com.bbb.importprocess.vo.ProductTabVO;
import com.bbb.importprocess.vo.ProductVO;
import com.bbb.importprocess.vo.RebateVO;
import com.bbb.importprocess.vo.SkuDisplayAttributesVO;
import com.bbb.importprocess.vo.SkuPricingVO;
import com.bbb.importprocess.vo.SkuVO;
import com.bbb.importprocess.vo.StofuImageParentVO;
import com.bbb.importprocess.vo.StofuImagesVO;
import com.bbb.importprocess.vo.ZipCodeVO;
import com.bbb.utils.BBBStringUtils;
import com.bbb.utils.BBBUtility;

public class BBBPIMFeedTools extends GenericService {


  private static final String CA_WEB_SKU_STATUS_CD = "CA_WEB_SKU_STATUS_CD";
private static final String CA_CHAIN_SKU_STATUS_CD = "CA_CHAIN_SKU_STATUS_CD";
private static final String WEB_SKU_STATUS_CD = "WEB_SKU_STATUS_CD";
private static final String CHAIN_SKU_STATUS_CD = "CHAIN_SKU_STATUS_CD";
private static final String EMERGENCY_IN_PROGRESS = "EMERGENCY_IN_PROGRESS";
  private static final String STAGING_IN_PROGRESS = "STAGING_IN_PROGRESS";
  private static final String PRODUCTION_IN_PROGRESS = "PRODUCTION_IN_PROGRESS";
  private static final String NO_RECORD_FOUND = "No Record Found";
  // *** Performance Monitor Method Constants **** //
  private static final String GET_REBATE_DETAILS = "getRebateDetails";
  private static final String GET_SKU_PRICING_DETAILS = "getSkuPricingDetails";
  private static final String GET_BRAND_DETAILS = "getBrandDetails";
  private static final String GET_ITEM_ATT_DETAIL = "getItemAttributesDetail";
  private static final String GET_PIM_OTHER_MEDIA_DETAIL = "getPIMOtherMediaDetail";
  private static final String GET_PIM_ITEM_MEDIA_DETAIL = "getPIMItemMediaDetail";
  private static final String GET_PIM_ITEM_DETAIL = "getPIMItemDetail";
  private static final String GET_PIM_CATEGORY_DETAIL = "getPIMCategoryDetail";
  private static final String UPDATE_FEED_STATUS_METHOD = "updateFeedStatus";
  private static final String UPDATE_FEED_LIST_FOR_PROJECT = "updateFeedStatusForProject";
  private static final String GET_SKU_ATT_VO_LIST = "getSkuAttributesVOList";
  private static final String GET_ID_VO_LIST = "getIdVOList";
  private static final String GET_ID_LIST_PROD_IMPORT = "getPIMLatestFeedForProdImport";
  private static final String GET_ID_LIST = "getIdList";
  private static final String GET_SKU_THRESH_DETAILS = "getSkuThreshDetails";
  private static final String GET_PIM_CLASS_LIST_VO = "getPIMClassListVO";
  private static final String GET_PIM_SUB_DEPT_DETAIL = "getPIMSubDeptDetail";
  private static final String GET_PIM_DEPT_DETAIL = "getPIMDeptDetail";
  private static final String UPDATE_BAD_RECORDS = "updateBadRecords";
  private static final String GET_PIM_PROD_SKU_DETAIL = "getPIMProdSkuDetail";
  private static final String POPULATE_CATEGORY_KEYWORDS = "populateCategoryKeywords";
  private static final String POPULATE_PRODUCT_KEYWORDS = "populateProductKeywords";
  private static final String GET_RESULT_SET = "getResultSet";
  private static final String GET_PRODUCT_TABS_DETAILS = "getProductTabsDetails";
  // *** Param Constant **** //
  private static final String LIKE_UNLIKE_FLAG = "LIKE_UNLIKE_FLAG";
  private static final String ROLLUP_TYPE_CODE = "ROLLUP_TYPE_CODE";
  private static final String CHILD_SKU_ID = "CHILD_SKU_ID";
  private static final String BOOLEAN_FLAG = "Y";
  private static final String SKU_ID = "SKU_ID";
  private static final String SELECT_UNIQUE_PARENT_CATEGORIES = "Select UNIQUE(NODE_ID) from ECP_NODE_PARENT where FEED_ID = ? ";
  private static final String NODE_ID = "NODE_ID";
  private static final String CHILD_NODE_ID = "CHILD_NODE_ID";
  private static final String OPEN = "OPEN";
  private static final String EMERGENCY = "EMERGENCY";
  private static final String STAGING = "STAGING";
  private static final String DEPLOYED_TO_STAGING = "DEPLOYED_TO_STAGING";
  private static final String IS_PRIMARY = "IS_PRIMARY";
  private static final String SEQUENCE_NUM = "SEQUENCE_NUM";
  private static final String REGULAR = "Regular";
  private static final String EMERGENCY_REGULAR = "Emergency_Regular";
  private static final String FEED_ID = "FEED_ID";
  private static final String REGULAR_STAGING = "BBBRegularStaging";
  private static final String REGULAR_PRODUCTION = "BBBRegularProduction";
  private static final String PICK_FOR_STAGING = "PICK_FOR_STAGING";
  private static final String SITE_FLAG = "SITE_FLAG";
  private static final String PIM_IMPORT_PRODUCTION = "BBBProductionPIMImport";
  // *** Select Query *** //
  private static final String SELECT_EMERGENCY_FEEDS = "select feed_id from ecp_feed_monitoring where feed_type='Emergency' and (feed_status='OPEN' or feed_status='EMERGENCY_IN_PROGRESS')";
  private static final String SELECT_PROD_IMP_IN_PROGRESS_FEEDS = "select feed_id from ecp_feed_monitoring where feed_status='PROD_IMPORT_IN_PROGRESS'";
  private static final String SELECT_UNIQUE_CATEGORIES = "Select NODE_ID from ECP_NODES where FEED_ID = ? ";
  private static final String SELECT_CAT_KEYWORDS_DETAILS = "Select KEYWORD_STRING from ECP_NODES_KEYWORDS where FEED_ID = ? and NODE_ID = ?";
  private static final String SELECT_PROD_KEYWORDS_DETAILS = "Select KEYWORD_STRING from ECP_PRODUCTS_KEYWORDS where FEED_ID = ? and SKU_ID = ?";
  private static final String SELECT_BRANDS_DETAILS = "Select * from ECP_BRANDS where FEED_ID = ? and BRAND_ID = ?";
  private static final String SELECT_POLLING_DETAILS = "Select * from bbb_deployment_polling where polling_status = ? and  ID =? ";
  private static final String GET_POLLING_DETAILS_PER_DC = "Select DATA_CENTER,polling_status from bbb_deployment_polling where   ID =? ";
  private static final String SELECT_DEPLOYMENT_DETAILS = "Select * from ECP_FEED_MONITORING where FEED_STATUS in(?,?,?)";
  private static final String SELECT_ALL_BRAND = "Select UNIQUE(BRAND_ID) from ECP_BRANDS where FEED_ID = ?";
  private static final String SELECT_SKU_PRICING_DETAILS = "Select * from ECP_SKU_PRICING_NEW where FEED_ID = ? and SKU_ID = ?";
  private static final String SELECT_UNIQUE_PRICING_SKU_IDS = "Select UNIQUE(SKU_ID) from ECP_SKU_PRICING_NEW where FEED_ID = ?";
  private static final String SELECT_UNIQUE_REBATE_IDS = "Select UNIQUE(REBATE_ID) from ECP_REBATES where FEED_ID = ?";
  private static final String SELECT_REBATE_DETAILS = "Select * from ECP_REBATES where FEED_ID = ? and REBATE_ID = ?";
  private static final String SELECT_UNIQUE_SKU_REBATES_REL_IDS = "Select UNIQUE(SKU_ID) from ECP_SKU_REBATES where FEED_ID = ? ";
  private static final String SELECT_REBATE_IDS = "Select REBATE_ID,OPERATION_FLAG from ECP_SKU_REBATES where FEED_ID = ? and SKU_ID = ? ";
  private static final String SELECT_ALL_FREQUENT_ITEMS = "Select UNIQUE(SKU_ID) from ECP_ITEMS_FREQUENT where FEED_ID = ? ";
  private static final String SELECT_ALL_RARE_ITEMS = "Select UNIQUE(SKU_ID) from ECP_ITEMS_RARE where FEED_ID = ? ";
  private static final String SELECT_DIRECT_REGULAR_FEEDS = "select FEED_ID from ecp_feed_monitoring where feed_type in ('Regular','Emergency_Regular') and (feed_status='OPEN' or feed_status='STAGING_IN_PROGRESS') Order By FEED_ID";
  	private static final String SELECT_DIRECT_FEED = "SELECT FEED_STATUS FROM ECP_DIRECT_FEED_MONITORING WHERE FEED_ID = ?";
	private static final String INSERT_DIRECT_FEED = "INSERT INTO ECP_DIRECT_FEED_MONITORING (FEED_ID,FEED_STATUS) values (?,'IN_PROGRESS')";
	private static final String SELECT_PRJ_IMPORTED_FEED = "select * from ecp_prj_imported_feeds where project_id=? and feed_id=?";
	private static final String INSERT_PRJ_IMPORTED_FEED = "INSERT INTO ecp_prj_imported_feeds (project_id,feed_id,project_name,imported_time) values (?,?,?,CURRENT_TIMESTAMP)";
	
  	private static final String SELECT_FEED_STATUS = "SELECT COUNT(*) FROM ECP_FEED_MONITORING WHERE FEED_STATUS LIKE ? OR FEED_STATUS LIKE ? OR FEED_STATUS LIKE ?";
  	private static final String SELECT_FEED_STATUS_IN_EXCEPTION = "SELECT COUNT(*) FROM ECP_FEED_MONITORING WHERE FEED_STATUS LIKE '%ERROR%' OR FEED_STATUS LIKE '%SYSTEM%'";
  	private static final String DIRECT_SELECT_INPROGRESS_FEED_STATUS = "SELECT COUNT(*) FROM ECP_FEED_MONITORING WHERE FEED_STATUS = 'PROD_DEP_IN_PROGRESS'";
	private static final String DIRECT_SELECT_ECP_ERROR_FEED_STATUS = "SELECT COUNT(*) FROM ECP_FEED_MONITORING WHERE FEED_STATUS LIKE '%SYSTEM%' OR FEED_STATUS LIKE '%ERROR%'";
	private static final String DIRECT_SELECT_EMERGENCY_FEED_STATUS = "select COUNT(*) from ECP_FEED_MONITORING WHERE FEED_TYPE = 'Emergency' and (feed_status = 'OPEN' OR feed_status LIKE '%IN_PROGRESS%')";
	private static final String SELECT_FEED_ID = "Select FEED_ID from ECP_FEED_MONITORING where FEED_TYPE = ? and FEED_STATUS = ? Order By FEED_ID";
	private static final String SELECT_FEED_ID_PROD_IMPORT = "Select FEED_ID from ECP_FEED_MONITORING where (FEED_TYPE = 'Regular' or FEED_TYPE = 'Emergency_Regular') and (FEED_STATUS = 'DEPLOYED_TO_STAGING' or FEED_STATUS = 'PROD_IMPORT_IN_PROGRESS') Order By FEED_ID";
	private static final String SELECT_CHILD_CATEGORIES = "Select CHILD_NODE_ID,OPERATION_FLAG from ECP_NODE_PARENT where FEED_ID = ? and NODE_ID = ? ORDER BY SEQUENCE_NUM";
	private static final String UPDATE_FEED_STATUS = "Update ECP_FEED_MONITORING set FEED_STATUS = ? where FEED_ID = ?";
	private static final String UPDATE_DIRECT_FEED_STATUS = "Update ECP_DIRECT_FEED_MONITORING set FEED_STATUS = ? where FEED_ID = ?";
	private static final String UPDATE_POOLING_RECORD = "Update bbb_deployment_polling  set polling_status = ?, TYPE_DEPLOYMENT=?,TARGET=? where ID =? ";
	private static final String UPDATE_POOLING = "Update bbb_deployment_polling  set polling_status = 'EndecaEnd' where polling_status in(?,?,?) ";
	private static final String UPDATE_FEED_STATUS_AFTER_DEPLOYMENT = "Update ECP_FEED_MONITORING set FEED_STATUS = ? where FEED_STATUS in (?)";
	private static final String SELECT_CATEGORY_DETAILS = "Select * from ECP_NODES where FEED_ID = ? and NODE_ID = ?";
	private static final String SELECT_MEDIA_DETAILS = "Select * from ECP_MEDIA where FEED_ID = ? and MEDIA_ID = ?";

	private static final String SELECT_BAD_FEED_RECORD_FOR_DIRECT = "select * from ecp_feed_bad_records where feed_id = ? and log_message like 'Direct:%'";
	private static final String SELECT_BAD_FEED_RECORD_FOR_IMPORT = "select * from ecp_feed_bad_records where feed_id = ? and log_message not like 'Direct:%'";
	private static final String SELECT_ITEM_DETAILS = "Select * from ECP_ITEMS_FREQUENT frequent, ECP_ITEMS_RARE rare where frequent.FEED_ID = ? and frequent.SKU_ID = ? and  rare.feed_id = frequent.feed_id and  rare.sku_id = frequent.sku_id ";
	private static final String SELECT_FREQUENT_ITEM_DETAILS = "Select * from ECP_ITEMS_FREQUENT where FEED_ID = ? and SKU_ID = ?";
	private static final String SELECT_RARE_ITEM_DETAILS = "Select * from ECP_ITEMS_RARE where FEED_ID = ? and SKU_ID = ?";
	private static final String SELECT_UNIQUE_PRODUCT_TABS = "Select UNIQUE(SKU_ID) from ECP_PRODUCT_TABS where FEED_ID = ? ";
	private static final String SELECT_SKU_PRODUCT_TABS = "Select TAB_ID,OPERATION_FLAG,SITE_FLAG from ECP_PRODUCT_TABS where FEED_ID = ? and SKU_ID = ? ORDER BY TAB_SEQUENCE";

	private static final String SELECT_ITEM_TAB_DETAILS = "Select * from ECP_PRODUCT_TABS where FEED_ID = ? and SKU_ID = ? and TAB_ID = ?";
	private static final String SELECT_UNIQUE_ATTRIBUTES_IDS = "Select UNIQUE(ITEM_ATTRIBUTE_ID) from ECP_ITEM_ATTRIBUTES where FEED_ID = ? ";
	private static final String SELECT_ITEM_ATTRIBUTES_DETAILS = "Select * from ECP_ITEM_ATTRIBUTES where FEED_ID = ? and ITEM_ATTRIBUTE_ID = ?";
	private static final String SELECT_ITEM_ATTRIBUTES_DETAILS_INC_VALUE = "Select * from ECP_ITEM_ATTRIBUTES where FEED_ID = ? and ITEM_ATTRIBUTE_ID = ? and ITEM_VALUE_ID = ?";

	private static final String SELECT_UNIQUE_SKU_ATTRIBUTES = "Select UNIQUE(SKU_ID) from ECP_SKU_ATTRIBUTES where FEED_ID = ? ";
	private static final String SELECT_SKU_ATTRIBUTES = "Select * from ECP_SKU_ATTRIBUTES where SKU_ID = ? and FEED_ID = ? ";

	private static final String SELECT_ALL_MEDIA = "Select UNIQUE(MEDIA_ID) from ECP_MEDIA where FEED_ID = ? ";
	private static final String SELECT_FEED_STATUS_FOR_FEED_ID = "select feed_status from ecp_feed_monitoring where feed_id=?";


  private static final String SELECT_UNIQUE_MEDIA_REL_IDS = "Select UNIQUE(SKU_ID) from ECP_PROD_MEDIA where FEED_ID = ? ";
  private static final String SELECT_SKU_MEDIA = "Select MEDIA_ID,OPERATION_FLAG from ECP_PROD_MEDIA where FEED_ID = ? and SKU_ID = ? ORDER BY SEQUENCE";
  private static final String SELECT_ITEM_MEDIA_DETAILS = "Select * from ECP_PROD_MEDIA where FEED_ID = ? and SKU_ID = ? and MEDIA_ID = ?";
  private static final String SELECT_CHILD_PRODUCTS = "Select SKU_ID,OPERATION_FLAG,SITE_FLAG,IS_PRIMARY,SEQUENCE_NUM from ECP_NODE_SKU where FEED_ID = ? and NODE_ID = ? ORDER BY SEQUENCE_NUM";
  // private static final String SELECT_UNIQUE_CATEGORIES_PRODUCTS =
  // "Select UNIQUE(NODE_ID) from ECP_NODE_SKU where FEED_ID = ? ";


	private static final String SELECT_UNIQUE_PROD_SKU_REL_IDS = "Select UNIQUE(SKU_ID) from ECP_RELATED_ITEMS where FEED_ID = ? ";
	private static final String SELECT_SKU_PROD_REL = "Select CHILD_SKU_ID,RELATED_TYPE_CD,OPERATION_FLAG,ROLLUP_TYPE_CODE,LIKE_UNLIKE_FLAG,SEQUENCE_NUM from ECP_RELATED_ITEMS where FEED_ID = ? and SKU_ID = ? ORDER BY SEQUENCE_NUM";
	private static final String SELECT_SKU_PROD_DETAILS = "Select * from ECP_RELATED_ITEMS where FEED_ID = ? and SKU_ID = ? and CHILD_SKU_ID = ?";

	private static final String SELECT_ALL_DEPT = "Select UNIQUE(JDA_DEPT_ID) from DEPT where FEED_ID = ?  ";
	private static final String SELECT_DEPT_DETAILS = "Select * from DEPT where JDA_DEPT_ID = ? and FEED_ID = ?  ";

	private static final String SELECT_ALL_DEPT_SUB_DEPT = "Select UNIQUE(JDA_DEPT_ID)  from SUB_DEPT where FEED_ID = ?";
	private static final String SELECT_ALL_SUB_DEPT1 = "Select JDA_DEPT_ID from SUB_DEPT where JDA_SUB_DEPT_ID = ? and FEED_ID = ?  ";

	private static final String SELECT_SUB_DEPT_IDS = "Select * from SUB_DEPT where JDA_DEPT_ID = ? and FEED_ID = ? ";

	private static final String SELECT_ALL_CLASS = "Select UNIQUE(JDA_CLASS)  from CLASS where FEED_ID = ?";

	private static final String SELECT_ALL_CLASS1 = "Select JDA_DEPT_ID, JDA_SUB_DEPT_ID from CLASS where JDA_CLASS = ? and FEED_ID = ? ";
	private static final String SELECT_CLASS_DETAILS = "Select * from CLASS where JDA_CLASS=? and FEED_ID = ? ";

	private static final String SELECT_ALL_SKU_THRESH = "Select UNIQUE(SKU_THRESHOLD_ID) from ECP_SKU_THRESHOLDS where FEED_ID = ?";
	private static final String SELECT_SKU_THRESH_DETAILS = "Select * from ECP_SKU_THRESHOLDS where FEED_ID = ? and SKU_THRESHOLD_ID = ?";
	private static final String IMPORT_PROCESS_PIMTOOLS = "importprocess_pimtools";
	private static final String SELECT_ECO_FEE_SKU_DETAILS = " Select * from ECP_ECO_ITEMS where FEED_ID = ? and SKU_ID = ?";
	private static final String SELECT_ECO_SKU_REL_IDS = "Select UNIQUE(SKU_ID) from ECP_ECO_ITEMS where FEED_ID = ?";
	private static final String SELECT_SKU_ZIP_REL_IDS = "Select UNIQUE(SKU_ID) from ECP_SKU_REGION_RESTRICTIONS where FEED_ID = ?";
	private static final String SELECT_SKU_ZIP = " Select * from ECP_SKU_REGION_RESTRICTIONS where FEED_ID = ? and SKU_ID = ?";
	private static final String SELECT_SKU_ZIP_DETAILS = " Select * from ECP_REGION_ZIPCODES  where FEED_ID = ? and REGION_ID  = ? ";
	private static final String SELECT_ZIP_CODE_REGION_IDS = "Select UNIQUE(REGION_ID) from ECP_REGION_ZIPCODES where FEED_ID = ?";
 
	private static final String SELECT_SKU_IMAGES = "select * from ECP_STOFU_IMAGES where  FEED_ID = ? and SKU_ID = ?";
	private static final String SELECT_ALL_STOFU_IMAGES = "Select UNIQUE(SKU_ID) from ECP_STOFU_IMAGES where FEED_ID = ? ";
	private static final String SELECT_DISTINCT_PRODUCTFLAG = "select distinct(product_flag) from ECP_STOFU_IMAGES where  FEED_ID = ? and SKU_ID = ? ";
	
	//DEPLOYMENT SCHEDULER QUERIES
	private static final String SELECT_STAGING_IMP_IN_PROGRESS_FEEDS = "select feed_id from ecp_feed_monitoring where feed_status='STAGING_IN_PROGRESS'";
	private static final String SELECT_EMERGENCY_REGULAR_NEW2_OPEN_DTS = "select feed_id from ecp_feed_monitoring where feed_type='Emergency_Regular' and (feed_status='NEW2' or feed_status='OPEN' or feed_status='DEPLOYED_TO_STAGING')";
	private static final String SELECT_IMPORTED_FEED_IN_PRD_PROJ = "select feed_id from ecp_prj_imported_feeds where project_id = ?" ;
	private static final String UPDATE_ECP_PRJ_IMPORTED_FEEDS = "update ecp_prj_imported_feeds set deployed_time = CURRENT_TIMESTAMP where project_id = ?";
	private static final String UPDATE_DEPLOYED_FEEDS = "updating ecp_prj_imported_feeds deployed feeds";
	private static final String UPDATE_DEPLOYED_FEEDS_TIMESTAMP = "update timestamp of deployed feeds";
	
	// Queries for PIM Pricing Import
	private static final String SELECT_PRICING_DEPLOYMENT_DETAILS = "Select * from ECP_PRICING_FEED_MONITORING where FEED_STATUS in(?,?,?)";
	private static final String SELECT_PRICING_FEED_STATUS = "SELECT COUNT(*) FROM ECP_PRICING_FEED_MONITORING WHERE FEED_STATUS LIKE ? OR FEED_STATUS LIKE ? OR FEED_STATUS LIKE ?";
	private static final String SELECT_PRICING_FEED_ID = "Select FEED_ID from ECP_PRICING_FEED_MONITORING where FEED_TYPE = ? and FEED_STATUS = ? Order By FEED_ID";
	private static final String UPDATE_PRICING_FEED_STATUS = "Update ECP_PRICING_FEED_MONITORING set FEED_STATUS = ? where FEED_ID = ?";
	private static final String UPDATE_PRICING_FEED_STATUS_AFTER_DEPLOYMENT = "Update ECP_PRICING_FEED_MONITORING set FEED_STATUS = ? where FEED_STATUS in (?)";
	private static final String UPDATE_PRICING_POOLING_RECORD = "Update bbb_deployment_polling  set polling_status = ?, TYPE_DEPLOYMENT=?,TARGET=?, PRICING_FEED=? where ID =? ";

	// Constants for Pricing Import
	private static final String PRICING_PRODUCTION = "BBBPricingProduction";
	private static final String PRICING_STAGING = "BBBPricingStaging";
	private static final String CATALOG_PRICING_FEED = "CATALOG_PRICING_FEED";
	private static final String PRICING_REGULAR = "Pricing";
	
	//Query to fetch the keywords items from PIM schema.
	private static final String SELECT_KEYWORDS_PRODUCTS = "Select UNIQUE(SKU_ID) from ECP_PRODUCTS_KEYWORDS WHERE FEED_ID = ?";
	
	private int mConnectionCount = 0;

	//Query for Bad feed record check
	private static final String SELECT_BAD_FEED_RECORD = "select count(*) from ecp_feed_bad_records where feed_id=?";

	private static final String SELECT_PROD_DEP_IN_PROGRESS_FEEDS = "select feed_id from ecp_feed_monitoring where feed_status='PROD_DEP_IN_PROGRESS'";
	private static final String DIRECT_SELECT_DEPLOYED_TO_STAGING_FEED_STATUS = "select count(*) from ECP_FEED_MONITORING WHERE FEED_STATUS = 'DEPLOYED_TO_STAGING'";
	// ----------------------------------------------

	// MEMBER VARIABLES
	// ----------------------------------------------
	
	
	private String mLogTableStatusPrefix;

	/**
	 * 
	 * @return mLogTableStatusPrefix
	 */
	public String getLogTableStatusPrefix() {
		return this.mLogTableStatusPrefix;
	}

	/**
	 * 
	 * @param mLogTableStatusPrefix
	 */
	public void setLogTableStatusPrefix(String pLogTableStatusPrefix) {
		this.mLogTableStatusPrefix = pLogTableStatusPrefix;
	}

	public  String mProdImportCompletedStatus = "PROD_IMPORT_COMPLETED";
	

	public String getProdImportCompletedStatus() {
		return mProdImportCompletedStatus;
	}

	public void setProdImportCompletedStatus(String pProdImportCompletedStatus) {
		mProdImportCompletedStatus = pProdImportCompletedStatus;
	}
	
	public  String mProdDeployProgressStatus = "PROD_DEP_IN_PROGRESS";


	public String getProdDeployProgressStatus() {
		return mProdDeployProgressStatus;
	}

	public void setProdDeployProgressStatus(String pProdDeployProgressStatus) {
		mProdDeployProgressStatus = pProdDeployProgressStatus;
	}

	private Map<String, String> dsToPollingTableRowMap = new HashMap<String, String>();

	private String mFeedInProgressQuery;

	/**
	 * @return the dsToPollingTableRowMap
	 */
	public Map<String, String> getDsToPollingTableRowMap() {
		return dsToPollingTableRowMap;
	}

	/**
	 * @param dsToPollingTableRowMap
	 *            the dsToPollingTableRowMap to set
	 */
	public void setDsToPollingTableRowMap(
			Map<String, String> dsToPollingTableRowMap) {
		this.dsToPollingTableRowMap = dsToPollingTableRowMap;
	}

	/**
	 * /**
	 * 
	 * @return
	 */
	private boolean isConnectionOpen(final Connection pConnection)
			throws SQLException {

		return (pConnection != null && !pConnection.isClosed());

	}

	/**
	 * 
	 * @param pPreparedStatement
	 */
	private void closePreparedStatement(final PreparedStatement pPreparedStatement) {
		try {
			if (pPreparedStatement != null && !pPreparedStatement.isClosed()) {
				pPreparedStatement.close();
			}
		} catch (SQLException e) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(e));
			}
		}
	}

	/**
	 * 
	 * @param pResultSet
	 */
	private void closeResultSet(final ResultSet pResultSet) {

		try {

			if (pResultSet != null && !pResultSet.isClosed()) {
				pResultSet.close();
			}
		} catch (SQLException e) {
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(e));
			}
			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(e));
			}
		}
	}

	/**
	 * @return the mConnection
	 */
	public Connection getConnection(Connection pConnection)
			throws BBBSystemException {
		try {
			if (!isConnectionOpen(pConnection)) {

				if (isLoggingError()) {

					logError("Connection is already Closed");
				}
				throw new BBBSystemException(
						BBBCoreErrorConstants.VER_TOOLS_CON_CLOSED,
						"Connection Closed");
			}
		} catch (SQLException sqlex) {
			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlex));
			}
			throw new BBBSystemException(
					BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,
					sqlex.getMessage(), sqlex);
		}
		return pConnection;
	}

	/**
	 * 
	 * @return
	 */

	public Connection openConnection() throws BBBSystemException {

		Connection connection = null;
		try {
			if (isLoggingDebug()) {
				logDebug("Open Connection....");
			}

			DataSource dataSource = null;
			InitialContext initalContext = null;
			try {
				initalContext = new InitialContext();
				dataSource = (DataSource) initalContext.lookup("PIM");
			} catch (NamingException e) {
				if (isLoggingError()) {

					logError(BBBStringUtils.stack2string(e));
				}
			}

			if (dataSource != null) {

				connection = dataSource.getConnection();
				mConnectionCount++;
			}

		} catch (SQLException sqlex) {
			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlex));
			}
			throw new BBBSystemException(
					BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,
					sqlex.getMessage(), sqlex);
		}

		return connection;
	}

	/**
	 * 
	 */
	public void closeConnection(Connection pConnection) {
		boolean isClose = false;
		if (pConnection != null) {

			try {
				if (isConnectionOpen(pConnection)) {
					pConnection.close();
					isClose = true;
					if (isLoggingDebug()) {
						logDebug("Connection Closed Sucess....");
					}
				} else {

					if (isLoggingDebug()) {
						logDebug("Connection is already closed or no opened....");
					}
				}
			} catch (SQLException sqlex) {

				if (isLoggingError()) {

					logError(BBBStringUtils.stack2string(sqlex));
				}
			} finally {

				try {

					if (!isClose && isConnectionOpen(pConnection)) {
						pConnection.close();
					}
					mConnectionCount--;
				} catch (SQLException e) {
					if (isLoggingError()) {

						logError(BBBStringUtils.stack2string(e));
					}
				}
			}
		}

	}

	public int getConnectionCount() {
		return mConnectionCount;
	}

	public void setConnectionCount(int connectionCount) {
		mConnectionCount = connectionCount;
	}

	/**
	 * 
	 * @param pFeedType
	 * @param pConnection
	 * @return
	 */
	public void generateFeedId(final String pFeedType, Connection pConnection)
			throws BBBSystemException {

		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;

		PreparedStatement st = null;
		ResultSet rs = null;
		String query = null;
		int count = 0;

		try {

			long id = getNextId(pConnection, pFeedType);
						
			st = pConnection.prepareStatement(" select count(*) from LATEST_FEED_DETAILS where FEED_TYPE= ? ");
			st.setString(1,pFeedType);
			rs = st.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
			if (count == 0) {

				query = "INSERT INTO LATEST_FEED_DETAILS(CURRENT_FEED_ID, FEED_TYPE) VALUES ('"
						+ id + "','" + pFeedType + "')";
			} else {
        query = "UPDATE LATEST_FEED_DETAILS SET CURRENT_FEED_ID  = '" + id + "' WHERE FEED_TYPE='" + pFeedType + "'";
			}

			if (!StringUtils.isEmpty(query)) {
        prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				prepareStatement.executeUpdate();

        prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
			}

		} catch (SQLException e) {
			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(e));
			}

		} finally {

			closeResultSet(rs);
			if(st!=null){try {
				st.close();
			} catch (SQLException e) {
				logError("Error in generating feed id " + e.getMessage());
			}}			
			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
		}

	}

	/**
	 * 
	 * @param pFeedType
	 * @param pConnection
	 * @return
	 */
	public void generatePricingFeedId(final String pFeedType, Connection pConnection)
			throws BBBSystemException {

		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		Statement st = null;
		ResultSet rs = null;

		String query = null;
		int count = 0;

		try {

			long id = getNextPricingId(pConnection, pFeedType);
			String NUM_ROWS = " SELECT COUNT(*) FROM LATEST_FEED_DETAILS where FEED_TYPE='" + PRICING_REGULAR + "'";

			
			 st = pConnection.createStatement();
			 rs = st.executeQuery(NUM_ROWS);
			if (rs.next()) {
				count = rs.getInt(1);
			}
			if (count == 0) {
				query = "INSERT INTO LATEST_FEED_DETAILS(CURRENT_FEED_ID, FEED_TYPE) VALUES ('" + id + "','" + PRICING_REGULAR + "')";
			} else {
				query = "UPDATE LATEST_FEED_DETAILS SET CURRENT_FEED_ID  = '" + id + "' WHERE FEED_TYPE='" + PRICING_REGULAR + "'";
			}

			if (!StringUtils.isEmpty(query)) {
				prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				prepareStatement.executeUpdate();
				prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			}

		} catch (SQLException e) {
			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(e));
			}

		} finally {

			closeResultSet(rs);
			if(st!=null){try {
				st.close();
			} catch (SQLException e) {
				logError("Error in generating price feed id"+ e.getMessage());
			}}
			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
		}

	}
	
	private static long getNextId(Connection cn, String feedType)
			throws SQLException, BBBSystemException {

		Statement st = null;
		ResultSet rs = null;
		String query = null;
		long pricingId = 0;
		
		if (feedType.equalsIgnoreCase("Regular")) {
			query = "SELECT current_feed_id FROM LATEST_FEED_DETAILS where feed_type = 'Regular'";
		} else if (feedType.equalsIgnoreCase("Emergency")) {
			query = "SELECT current_feed_id FROM LATEST_FEED_DETAILS where feed_type = 'Emergency'";
		}

		st = cn.createStatement();
		rs = st.executeQuery(query);
		if (!rs.next()) {
			pricingId = 1;
		} else {
			pricingId = (rs.getLong(1) + 1);// New feed_id is current feed_id + 1
		}
		
		st.close();
		rs.close();
		
		return pricingId;
	}
	
	/**
	 * This generates next Feed ID for Pricing Feed
	 * @param pFeedType
	 * @param pConnection
	 * @return
	 */
	private static long getNextPricingId(Connection cn, String feedType) throws SQLException, BBBSystemException {

		Statement st = null;
		ResultSet rs = null;
		String query = null;
		long pricingId = 0;
		if (feedType.equalsIgnoreCase("Pricing")) {
			query = "SELECT current_feed_id FROM LATEST_FEED_DETAILS where feed_type = 'Pricing'";
		}

	
		
		 st = cn.createStatement();
		 rs = st.executeQuery(query);
		if (!rs.next()) {
			pricingId = 1;
		} else {
			pricingId = rs.getLong(1) + 1; 
		}
		
		st.close();
		rs.close();
		return pricingId;
	}

	/**
	 * 
	 * @param pFeedType
	 * @param pConnection
	 * @return
	 */
  public List<String> getPIMLatestFeed(final String pFeedType, final Connection pConnection) throws BBBSystemException {
		if (pFeedType != null && pFeedType.equalsIgnoreCase(REGULAR_PRODUCTION)) {
      return getIdList(pConnection, SELECT_FEED_ID, REGULAR, DEPLOYED_TO_STAGING, FEED_ID);
    } else if (pFeedType != null && pFeedType.equalsIgnoreCase(REGULAR_STAGING)) {
      return getIdList(pConnection, SELECT_FEED_ID, REGULAR, OPEN, FEED_ID);
		} else {
      return getIdList(pConnection, SELECT_FEED_ID, pFeedType, OPEN, FEED_ID);
		}
	}

	/**
	 * Get the Latest Feed Id for Pricing Feed obtained ny PIM feed  
	 * @param pFeedType
	 * @param pConnection
	 * @return
	 */
	public List<String> getPIMPricingLatestFeed(final String pFeedType, final Connection pConnection , final String pFeedName) throws BBBSystemException {
		if (pFeedType != null && pFeedName != null && pFeedName.equalsIgnoreCase(PRICING_PRODUCTION) && pFeedType.equalsIgnoreCase(PRICING_REGULAR)) {
			return getPricingIdList(pConnection, SELECT_PRICING_FEED_ID, PRICING_REGULAR, OPEN, FEED_ID);
		}
//		else if (pFeedType != null && pFeedName != null && pFeedName.equalsIgnoreCase(PRICING_STAGING) && pFeedType.equalsIgnoreCase(PRICING_REGULAR)) {
//			return getPricingIdList(pConnection, SELECT_PRICING_FEED_ID, PRICING_REGULAR, OPEN, FEED_ID);
//		}
		return new ArrayList<String>();
	}
	
	/**
	 * This method is used to get all categories with provided pFeedId and
	 * pSiteId
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @return
	 * @throws BBBSystemException
	 */
	public List<String> getPIMAllCategories(final String pFeedId,
			final Connection pConnection) throws BBBSystemException {

		// String querySite =
    // "Select NODE_ID from ECP_NODES where FEED_ID = ? and SITE_FLAG LIKE '%" +
		// pSiteId + "%'";
		String querySite = SELECT_UNIQUE_CATEGORIES;

		if (isLoggingDebug()) {

			logDebug(querySite);
		}

    List<String> categoryIdList = getIdList(pConnection, querySite, pFeedId, null, NODE_ID);
		if (isLoggingDebug()) {

			logDebug("feedId=" + pFeedId + " categoryIdList=" + categoryIdList);
		}
		return categoryIdList;

	}

	/**
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @return
	 * @throws BBBSystemException
	 */
  public List<String> getUniqueCategories(final String pFeedId, final Connection pConnection) throws BBBSystemException {

		String querySite = SELECT_UNIQUE_PARENT_CATEGORIES;

		if (isLoggingDebug()) {

			logDebug(querySite);
		}

    List<String> categoryIdList = getIdList(pConnection, querySite, pFeedId, null, NODE_ID);
		if (isLoggingDebug()) {

			logDebug("feedId= " + pFeedId + " categoryIds=" + categoryIdList);
		}
		return categoryIdList;

	}

	/**
	 * 
	 * @param pFeedId
	 * @param pCategoryId
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
  public List<OperationVO> getChildCategories(final String pFeedId, final String pCategoryId,
      final Connection pConnection) throws BBBSystemException, BBBBusinessException {

		String querySite = SELECT_CHILD_CATEGORIES;

		if (isLoggingDebug()) {

			logDebug(querySite);
		}

    List<OperationVO> categoryIdsVOList = getIdVOList(pConnection, querySite, pFeedId, pCategoryId, CHILD_NODE_ID,
        null, "OPERATION_FLAG");

		if (isLoggingDebug()) {

			logDebug("Latest feedId" + pFeedId + " CategoryId=" + pCategoryId);
		}
		return categoryIdsVOList;

	}

	/**
	 * 
	 * @param pFeedId
	 * @param pCategoryId
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
  public List<OperationVO> getSkuAttibutes(final String pFeedId, final String pAttibuteId, final Connection pConnection)
			throws BBBSystemException, BBBBusinessException {

		String querySite = SELECT_ITEM_ATTRIBUTES_DETAILS;

		if (isLoggingDebug()) {

			logDebug(querySite);
		}

    List<OperationVO> categoryIdsVOList = getIdVOList(pConnection, querySite, pFeedId, pAttibuteId, "ITEM_VALUE_ID",
        null, null);

		if (isLoggingDebug()) {

			logDebug("Latest feedId" + pFeedId + " CategoryId=" + pAttibuteId);
		}
		return categoryIdsVOList;

	}

	/**
	 * This method is used to get all products with provided pFeedId and pSiteId
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @return
	 * @throws BBBSystemException
	 */
  public List<String> getAllFrequentItems(final String pFeedId, final Connection pConnection) throws BBBSystemException {

		String querySite = SELECT_ALL_FREQUENT_ITEMS;

		if (isLoggingDebug()) {

			logDebug(querySite);
		}

    List<String> itemIdList = getIdList(pConnection, querySite, pFeedId, null, SKU_ID);
		if (isLoggingDebug()) {

			logDebug("feedId=" + pFeedId + " itemIdList=" + itemIdList);
		}
		return itemIdList;

	}

	/**
	 * This method is used to get all products with provided pFeedId and pSiteId
	 * 
	 * @param pSiteId
	 * @return
	 * @throws BBBSystemException
	 */
  public List<String> getAllRareItems(final String pFeedId, final Connection pConnection) throws BBBSystemException {

		String querySite = SELECT_ALL_RARE_ITEMS;

		if (isLoggingDebug()) {

			logDebug(querySite);
		}

    List<String> itemIdList = getIdList(pConnection, querySite, pFeedId, null, SKU_ID);
		if (isLoggingDebug()) {

			logDebug("feedId=" + pFeedId + " itemIdList=" + itemIdList);
		}
		return itemIdList;

	}

	/**
	 * 
	 * @param pFeedType
	 * @return
	 */
  public boolean isEmergencyFeedInPlace(final Connection pConnection) throws BBBSystemException {

    List<String> feedIds = getIdList(pConnection, SELECT_FEED_ID, EMERGENCY, PICK_FOR_STAGING, FEED_ID);

		if (isLoggingDebug()) {
			logDebug(" feed ids=" + feedIds);
		}
		if (feedIds != null && !feedIds.isEmpty()) {
			return true;
		}
		return false;

	}

	/**
	 * 
	 * @param pQuery
	 * @param pParamArg1
	 * @param pParamArg2
	 * @param pParamArg3
	 * @param pResultSetArg
	 * @return
	 * @throws BBBSystemException
	 */
	private List<String> getPricingIdList(final Connection pConnection,
			final String pQuery, final String pParamArg1,
			final String pParamArg2, final String pResultSetArg)
			throws BBBSystemException {

		ResultSet resultSet = null;
		boolean isMonitorCanceled = false;
		List<String> idsList = new ArrayList<String>();
		if (isLoggingDebug()) {

			logDebug("pQuery=" + pQuery);
			logDebug("pParamArg1=" + pParamArg1);
			logDebug("pParamArg2=" + pParamArg2);
			logDebug("pResultSetArg" + pResultSetArg);
		}

		if (!StringUtils.isEmpty(pQuery)) {
			PreparedStatement prepareStatement = null;
			try {
				BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, GET_ID_LIST);
				prepareStatement = getConnection(pConnection).prepareStatement(pQuery, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				if (!StringUtils.isEmpty(pParamArg1)) {
					prepareStatement.setString(1, pParamArg1);
				}
				if (!StringUtils.isEmpty(pParamArg2)) {
					prepareStatement.setString(2, pParamArg2);
				}
				resultSet = prepareStatement.executeQuery();
				if (resultSet == null) {
					return idsList;
				}
				while (resultSet.next()) {

					idsList.add(resultSet.getString(pResultSetArg));
				}
				if (isLoggingDebug()) {
					logDebug("ResultSet Size=" + idsList.size());
				}
			} catch (SQLException sqlException) {

				if (isLoggingError()) {

					logError(BBBStringUtils.stack2string(sqlException));
				}
				if (isLoggingDebug()) {

					logDebug(BBBStringUtils.stack2string(sqlException));
				}
				BBBPerformanceMonitor.cancel(GET_ID_LIST);
				isMonitorCanceled = true;

			} finally {
				closeResultSet(resultSet);
				closePreparedStatement(prepareStatement);
				if (!isMonitorCanceled) {
					BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS,
							GET_ID_LIST);
				}
			}

		}

		return idsList;
	}
	
	/**
	 * 
	 * @param pQuery
	 * @param pParamArg1
	 * @param pParamArg2
	 * @param pResultSetArg
	 * @return
	 * @throws BBBSystemException
	 */
	private List<String> getIdList(final Connection pConnection,
			final String pQuery, final String pParamArg1,
			final String pParamArg2, final String pResultSetArg)
			throws BBBSystemException {

		ResultSet resultSet = null;
		boolean isMonitorCanceled = false;
		List<String> idsList = new ArrayList<String>();
		if (isLoggingDebug()) {

			logDebug("pQuery=" + pQuery);
			logDebug("pParamArg1=" + pParamArg1);
			logDebug("pParamArg2=" + pParamArg2);
			logDebug("pResultSetArg" + pResultSetArg);
		}

		if (!StringUtils.isEmpty(pQuery)) {
			PreparedStatement prepareStatement = null;
			try {
				BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, GET_ID_LIST);
				prepareStatement = getConnection(pConnection).prepareStatement(pQuery, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				if (!StringUtils.isEmpty(pParamArg1)) {
					prepareStatement.setString(1, pParamArg1);
				}
				if (!StringUtils.isEmpty(pParamArg2)) {
					prepareStatement.setString(2, pParamArg2);
				}
				resultSet = prepareStatement.executeQuery();
				if (resultSet == null) {
					return idsList;
				}
				while (resultSet.next()) {

					idsList.add(resultSet.getString(pResultSetArg));
				}
				if (isLoggingDebug()) {
					logDebug("ResultSet Size=" + idsList.size());
				}
			} catch (SQLException sqlException) {

				if (isLoggingError()) {

					logError(BBBStringUtils.stack2string(sqlException));
				}
				if (isLoggingDebug()) {

					logDebug(BBBStringUtils.stack2string(sqlException));
				}
				BBBPerformanceMonitor.cancel(GET_ID_LIST);
				isMonitorCanceled = true;

			} finally {
				closeResultSet(resultSet);
				closePreparedStatement(prepareStatement);
				if (!isMonitorCanceled) {
					BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS,
							GET_ID_LIST);
				}
			}

		}

		return idsList;
	}

	/**
	 * @param pConnection
	 * @return Latest PIM Feed list for production import
	 * @throws BBBSystemException
	 */
	public List<String> getPIMLatestFeedForProdImport(final Connection pConnection)
			throws BBBSystemException {

		ResultSet resultSet = null;
		boolean isMonitorCanceled = false;
		List<String> idsList = new ArrayList<String>();
		if (isLoggingDebug()) {
			logDebug("Latest PIM Feed for Prod Import Query=" + SELECT_FEED_ID_PROD_IMPORT);
		}

		PreparedStatement prepareStatement = null;
		try {
			BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, GET_ID_LIST_PROD_IMPORT);
			prepareStatement = getConnection(pConnection).prepareStatement(SELECT_FEED_ID_PROD_IMPORT, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			resultSet = prepareStatement.executeQuery();
			if (resultSet == null) {
				return idsList;
			}
			while (resultSet.next()) {
				idsList.add(resultSet.getString(FEED_ID));
			}
			if (isLoggingDebug()) {
				logDebug("ResultSet Size=" + idsList.size());
			}
		} catch (SQLException sqlException) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(sqlException));
			}
			BBBPerformanceMonitor.cancel(GET_ID_LIST_PROD_IMPORT);
			isMonitorCanceled = true;
			throw new BBBSystemException(
					BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,
					sqlException.getMessage(), sqlException);
		} finally {
			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
			if (!isMonitorCanceled) {
				BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, GET_ID_LIST_PROD_IMPORT);
			}
		}
		return idsList;
	}

	/**
	 * 
	 * @param pQuery
	 * @param pParamArg1
	 * @param pParamArg2
	 * @param pResultSetArg1
	 * @return
	 * @throws BBBBusinessException
	 */
	private List<OperationVO> getIdVOList(final Connection pConnection,
			final String pQuery, final String pParamArg1,
			final String pParamArg2, final String pResultSetArg1,
			final String pResultSetArg2, final String pResultSetArg3)
			throws BBBSystemException, BBBBusinessException {
		boolean isMonitorCanceled = false;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		String id = null;
		String operationCode = null;
		String siteIds = null;
		// String relatedType = null;
		OperationVO itemVO = null;

		if (isLoggingDebug()) {
			logDebug("Inside method getIdVOList ");
		}

		List<OperationVO> idsVOList = new ArrayList<OperationVO>();
		if (!StringUtils.isEmpty(pQuery)) {

			try {
        BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, GET_ID_VO_LIST);

        prepareStatement = getConnection(pConnection).prepareStatement(pQuery, ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				if (!StringUtils.isEmpty(pParamArg1)) {
					prepareStatement.setString(1, pParamArg1);
				}
				if (!StringUtils.isEmpty(pParamArg2)) {

					prepareStatement.setString(2, pParamArg2);
				}

				resultSet = prepareStatement.executeQuery();
				while (resultSet.next()) {

					itemVO = new OperationVO();
					if (!StringUtils.isEmpty(pResultSetArg1)) {
						id = resultSet.getString(pResultSetArg1);
					}
					// operationCode = resultSet.getString("OPERATION_FLAG");
					// relatedType= resultSet.getString("RELATED_TYPE_CD");

					if (!StringUtils.isEmpty(pResultSetArg2)) {

						siteIds = resultSet.getString(pResultSetArg2);
					}

					if (!StringUtils.isEmpty(pResultSetArg3)) {

						operationCode = resultSet.getString(pResultSetArg3);
					}

					itemVO.setId(id);
					itemVO.setOperationCode(operationCode);
					itemVO.setSiteIds(siteIds);
					// itemVO.setRelatedType(relatedType);
					idsVOList.add(itemVO);
				}
			} catch (SQLException sqlException) {
				if (isLoggingError()) {

					logError(BBBStringUtils.stack2string(sqlException));
				}
				if (isLoggingDebug()) {

					logDebug(BBBStringUtils.stack2string(sqlException));
				}

				BBBPerformanceMonitor.cancel(GET_ID_VO_LIST);
				isMonitorCanceled = true;
        throw new BBBBusinessException(BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,sqlException.getMessage(), sqlException);

			} finally {

				closeResultSet(resultSet);
				closePreparedStatement(prepareStatement);
				if (!isMonitorCanceled) {
          BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, GET_ID_VO_LIST);
				}
			}

		}

    return idsVOList;
  }

  private List<OperationVO> getIdVOList(final Connection pConnection, final String pQuery, final String pParamArg1,
      final String pParamArg2, final String pResultSetArg1, final String pResultSetArg2, final String pResultSetArg3,
      final String pResultSetArg4) throws BBBSystemException, BBBBusinessException {
    boolean isMonitorCanceled = false;
    PreparedStatement prepareStatement = null;
    ResultSet resultSet = null;
    String id = null;
    String operationCode = null;
    String siteIds = null;
    String sequence = null;
    String primaryNodeId = null;
    // String relatedType = null;
    OperationVO itemVO = null;

    if (isLoggingDebug()) {
      logDebug("Inside method getIdVOList ");
    }

		List<OperationVO> idsVOList = new ArrayList<OperationVO>();
		if (!StringUtils.isEmpty(pQuery)) {

			try {
        BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, GET_ID_VO_LIST);

        prepareStatement = getConnection(pConnection).prepareStatement(pQuery, ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				if (!StringUtils.isEmpty(pParamArg1)) {
					prepareStatement.setString(1, pParamArg1);
				}
				if (!StringUtils.isEmpty(pParamArg2)) {

					prepareStatement.setString(2, pParamArg2);
				}
				resultSet = prepareStatement.executeQuery();
				while (resultSet.next()) {

					itemVO = new OperationVO();
					if (!StringUtils.isEmpty(pResultSetArg1)) {
						id = resultSet.getString(pResultSetArg1);
					}
					// operationCode = resultSet.getString("OPERATION_FLAG");
					// relatedType= resultSet.getString("RELATED_TYPE_CD");

					if (!StringUtils.isEmpty(pResultSetArg2)) {

						siteIds = resultSet.getString(pResultSetArg2);
					}

          if (!StringUtils.isEmpty(pResultSetArg3)) {

            operationCode = resultSet.getString(pResultSetArg3);
          }
          if (!StringUtils.isEmpty(pResultSetArg4) && !StringUtils.isEmpty(resultSet.getString(pResultSetArg4))) {
        	  if(pResultSetArg4.equalsIgnoreCase(IS_PRIMARY) && resultSet.getString(pResultSetArg4).trim().equalsIgnoreCase("Y"))
        	  {
        		  primaryNodeId = pParamArg2;
        	  }
        	  else{
        		  sequence = resultSet.getString(pResultSetArg4);
        	  }
          } 
         String productSeqNumber = resultSet.getString(SEQUENCE_NUM);
          
          itemVO.setProductSeqNumber(productSeqNumber);
          itemVO.setId(id);
          itemVO.setOperationCode(operationCode);
          itemVO.setSiteIds(siteIds);
          itemVO.setSequence(sequence);
          itemVO.setPrimaryNodeId(primaryNodeId);
          // itemVO.setRelatedType(relatedType);
          idsVOList.add(itemVO);
        }
      } catch (SQLException sqlException) {
        if (isLoggingError()) {

					logError(BBBStringUtils.stack2string(sqlException));
				}
				if (isLoggingDebug()) {

					logDebug(BBBStringUtils.stack2string(sqlException));
				}

				BBBPerformanceMonitor.cancel(GET_ID_VO_LIST);
				isMonitorCanceled = true;
        throw new BBBBusinessException(BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,sqlException.getMessage(), sqlException);

			} finally {

				closeResultSet(resultSet);
				closePreparedStatement(prepareStatement);
				if (!isMonitorCanceled)
          BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, GET_ID_VO_LIST);
			}

		}

		return idsVOList;
	}

	/**
	 * 
	 * @param pQuery
	 * @param pParamArg1
	 * @param pParamArg2
	 * @param pResultSetArg1
	 * @return
	 * @throws BBBBusinessException
	 */
	private List<SkuDisplayAttributesVO> getSkuAttributesVOList(
			final Connection pConnection, final String pId, final String pFeedId)
			throws BBBSystemException, BBBBusinessException {

		boolean isMonitorCanceled = false;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;

		SkuDisplayAttributesVO skuDisplayAttributesVO = null;

		if (isLoggingDebug()) {
			logDebug("Inside method getSkuAttributesVOList ");
		}

		List<SkuDisplayAttributesVO> idsVOList = new ArrayList<SkuDisplayAttributesVO>();
		String pQuery = SELECT_SKU_ATTRIBUTES;

		if (!StringUtils.isEmpty(pQuery)) {

			try {
        BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, GET_SKU_ATT_VO_LIST);

        prepareStatement = getConnection(pConnection).prepareStatement(pQuery, ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				prepareStatement.setString(1, pId);
				prepareStatement.setString(2, pFeedId);

				resultSet = prepareStatement.executeQuery();
				while (resultSet.next()) {

					skuDisplayAttributesVO = populateSkuAttribute(resultSet);

					idsVOList.add(skuDisplayAttributesVO);
				}
			} catch (SQLException sqlException) {
				if (isLoggingError()) {
					logError(BBBStringUtils.stack2string(sqlException));
				}
				if (isLoggingDebug()) {
					logDebug(BBBStringUtils.stack2string(sqlException));
				}

				BBBPerformanceMonitor.cancel(GET_ID_VO_LIST);
				isMonitorCanceled = true;
        throw new BBBBusinessException(BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,sqlException.getMessage(), sqlException);

			} finally {

				closeResultSet(resultSet);
				closePreparedStatement(prepareStatement);
				if (!isMonitorCanceled)
          BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, GET_ID_VO_LIST);
			}

		}

		return idsVOList;
	}

	public Boolean getPoolingStatus(String pStatus, String pDataSource, boolean checkStatusInBothDC) throws BBBSystemException, BBBBusinessException {

		if (isLoggingDebug()) {
			logDebug("Inside method getPoolingStatus status " + pStatus + " data Source " + pDataSource);
		}

		String query = SELECT_POLLING_DETAILS;
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		String primaryId = this.dsToPollingTableRowMap.get(pDataSource);
		if (isLoggingDebug()) {
			logDebug("primary Id of the polling table against which status needs to be checked  " + primaryId);
		}
		try {
			connection = openConnection();

			if (connection != null) {
				prepareStatement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				resultSet = getResultSet(connection, prepareStatement, pStatus, primaryId, null);

				if (resultSet != null && resultSet.next()) {
					if (isLoggingDebug()) {
						logDebug("status in polling table is " + pStatus + " for datasource " + pDataSource);
					}
					if (checkStatusInBothDC) {
						if (resultSet.absolute(2)) {
							return true;
						} else {
							return false;
						}
					} else {
						return true;
					}
				} else {
					if (isLoggingDebug()) {
            logDebug("status in polling table is NOT " + pStatus + " for datasource " + pDataSource);
					}
					return false;
				}
			} else {

        throw new BBBSystemException(BBBCoreErrorConstants.VER_TOOLS_CON_PROBLEM,"Connection problem");
			}
		} catch (SQLException sqlException) {

			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}

      throw new BBBBusinessException(BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,sqlException.getMessage(), sqlException);
		} finally {

			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
			closeConnection(connection);
		}

	}

  public Map<String, String> getStatusPerDC(String pDataSource) throws BBBSystemException, BBBBusinessException {
		Map<String, String> DCStatusMap = new HashMap<String, String>();

		if (isLoggingDebug()) {
			logDebug(" data Source " + pDataSource);
		}

		String query = GET_POLLING_DETAILS_PER_DC;
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		String primaryId = this.dsToPollingTableRowMap.get(pDataSource);
		if (isLoggingDebug()) {
      logDebug("primary Id of the polling table against which status needs to be checked  " + primaryId);
		}
		try {
			connection = openConnection();

			if (connection != null) {
        prepareStatement = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        resultSet = getResultSet(connection, prepareStatement, primaryId, null, null);

				if (resultSet != null) {
					while (resultSet.next()) {
            DCStatusMap.put(resultSet.getString("DATA_CENTER"), resultSet.getString("polling_status"));
					}
				}
			} else {
        throw new BBBSystemException(BBBCoreErrorConstants.VER_TOOLS_CON_PROBLEM,"Connection problem");
			}
		} catch (SQLException sqlException) {

			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}

      throw new BBBBusinessException(BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,sqlException.getMessage(), sqlException);
		} finally {

			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
			closeConnection(connection);
		}
		return DCStatusMap;

	}

	/**
	 * 
	 * @param pStatus
	 * @return
	 * @throws BBBBusinessException
	 */
	public String updatePollingStatus(final String pStatus,
			final Boolean pPimStatus, final String pDeploymentId,
			final String pDataSource) throws BBBSystemException,
			BBBBusinessException {

		PreparedStatement prepareStatement = null;
		String query = SELECT_DEPLOYMENT_DETAILS;
		ResultSet resultSet = null;
		Connection connection = null;
		String deploymentType = "PARTIAL";
		if (isLoggingDebug()) {
			logDebug("pStatus=" + pStatus + " pPimStatus=" + pPimStatus
					+ " pDeploymentId=" + pDeploymentId + " pDataSource="
					+ pDataSource);
		}

		try {

			connection = openConnection();
			String primaryId = this.dsToPollingTableRowMap.get(pDataSource);
			if (isLoggingDebug()) {
        logDebug("primary Id of the polling table for which status needs to be updated " + primaryId);
			}
      // If Pim status is true then find the deployment type from the PIM schema
			// where Feed type is in
			// STAGING_IN_PROGRESS,PRODUCTION_IN_PROGRESS,EMERGENCY_IN_PROGRESS
			 if (pPimStatus) {
				if (isLoggingDebug()) {
					logDebug("Getting Status from open feeds");
				}
				prepareStatement = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                resultSet = getResultSet(connection, prepareStatement, STAGING_IN_PROGRESS, PRODUCTION_IN_PROGRESS, EMERGENCY_IN_PROGRESS);

				if (resultSet != null) {
					while (resultSet.next()) {
						deploymentType = resultSet.getString("DEPLOYMENT_TYPE");
	
						if (deploymentType.equalsIgnoreCase("FULL")) {
							break;
						}
					}
				}
			} else{
				if (isLoggingDebug()) {
					logDebug("No PIM Result found for STAGING_IN_PROGRESS, PRODUCTION_IN_PROGRESS, EMERGENCY_IN_PROGRESS");
				}
			}

			if (isLoggingDebug()) {

				logDebug("deploymentType=" + deploymentType);
			}
			prepareStatement = connection.prepareStatement(UPDATE_POOLING_RECORD);
			prepareStatement.setString(1, pStatus);
			prepareStatement.setString(2, deploymentType);
			prepareStatement.setString(3, pDataSource);
			prepareStatement.setString(4, primaryId);

			insertToMetaTable(pStatus, pDeploymentId, pDataSource, deploymentType, connection);
			prepareStatement.executeUpdate();
			return deploymentType;

		} catch (SQLException sqlex) {

			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlex));
			}

		} finally {

			closePreparedStatement(prepareStatement);
			closeConnection(connection);
		}
		return null;
	}
	
	
	/**
	 * 
	 * @param pStatus
	 * @return
	 * @throws BBBBusinessException
	 */
	public String updatePricingPollingStatus(final String pStatus,
			final Boolean pPimStatus, final String pDeploymentId,
			final String pDataSource) throws BBBSystemException,
			BBBBusinessException {

		PreparedStatement prepareStatement = null;
		String query = SELECT_PRICING_DEPLOYMENT_DETAILS;
		ResultSet resultSet = null;
		Connection connection = null;
		String deploymentType = "PARTIAL";
		if (isLoggingDebug()) {
			logDebug("pStatus=" + pStatus + " pPimStatus=" + pPimStatus + " pDeploymentId=" + pDeploymentId + " pDataSource=" + pDataSource);
		}

		try {

			connection = openConnection();
			String primaryId = this.dsToPollingTableRowMap.get(pDataSource);
			if (isLoggingDebug()) {
				logDebug("primary Id of the polling table for which status needs to be updated " + primaryId);
			}
			 if (pPimStatus) {
				if (isLoggingDebug()) {
					logDebug("Getting Status from open feeds");
				}
				prepareStatement = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                resultSet = getResultSet(connection, prepareStatement, STAGING_IN_PROGRESS, PRODUCTION_IN_PROGRESS, EMERGENCY_IN_PROGRESS);

				if (resultSet != null) {
					while (resultSet.next()) {
						deploymentType = resultSet.getString("DEPLOYMENT_TYPE");
	
						if (deploymentType.equalsIgnoreCase("FULL")) {
							break;
						}
					}
				}
			} else{
				if (isLoggingDebug()) {
					logDebug("No PIM Result found for STAGING_IN_PROGRESS, PRODUCTION_IN_PROGRESS, EMERGENCY_IN_PROGRESS");
				}
			}

			if (isLoggingDebug()) {

				logDebug("deploymentType=" + deploymentType);
			}
			prepareStatement = connection.prepareStatement(UPDATE_PRICING_POOLING_RECORD);
			prepareStatement.setString(1, pStatus);
			prepareStatement.setString(2, deploymentType);
			prepareStatement.setString(3, pDataSource);
			prepareStatement.setString(4, primaryId);
			prepareStatement.setString(5, "Y");

			insertToMetaTable(pStatus, pDeploymentId, pDataSource, deploymentType, connection);
			prepareStatement.executeUpdate();
			return deploymentType;

		} catch (SQLException sqlex) {

			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlex));
			}

		} finally {

			closePreparedStatement(prepareStatement);
			closeConnection(connection);
		}
		return null;
	}

  private void insertToMetaTable(final String pStatus, final String pDeploymentId, final String pDataSource,
			final String pDeploymentType, final Connection pConnection) {

		String query = "INSERT INTO BBB_DEPLOYMENT_METADATA (DEPLOYMENT_ID,TARGET,POLLING_STATUS,LAST_MODIFIED_DATE,TYPE_DEPLOYMENT) VALUES (?,?,?,?,?)";
		if (isLoggingDebug()) {

			logDebug("Inside insertToMetaTable Method.....");
      logDebug("pStatus=" + pStatus + "pDeploymentId=" + pDeploymentId + "pDataSource=" + pDataSource
          + "deploymentType=" + pDeploymentType);
			logDebug("query=" + query);
		}
		PreparedStatement prepareStatement = null;
		try {
      prepareStatement = pConnection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

			prepareStatement.setString(1, pDeploymentId);
			prepareStatement.setString(2, pDataSource);
			prepareStatement.setString(3, pStatus);
			prepareStatement.setDate(4, null);
			prepareStatement.setString(5, pDeploymentType);

			prepareStatement.executeUpdate();
		} catch (SQLException e) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(e));
			}
		}
		finally{
			try{
				if(prepareStatement!=null){
					prepareStatement.close();
				}
			}catch(SQLException e){
				logError("SQL exception is thrown while closing preparedStatement", e);
			}
		}

	}

	public void checkPoolingStatus() throws BBBSystemException {

		PreparedStatement prepareStatement = null;
		Connection connection = null;
		try {
			connection = openConnection();

			prepareStatement = connection.prepareStatement(UPDATE_POOLING);
			prepareStatement.setString(1, "COMPLETE_STAGING");
			prepareStatement.setString(2, "COMPLETE_SWITCH_B");
			prepareStatement.setString(3, "COMPLETE_SWITCH_A");
			prepareStatement.executeUpdate();
		} catch (SQLException e) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(e));
			}
		} finally {
			closePreparedStatement(prepareStatement);
			closeConnection(connection);
		}
	}

	/**
	 * 
	 * @param pFeedId
	 * @param pStatus
	 * @return
	 */
  public int updateFeedStatus(final String pFeedId, final String pStatus, final Connection pConnection)
      throws BBBSystemException {

		PreparedStatement prepareStatement = null;
		boolean isMonitorCanceled = false;
		BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, UPDATE_FEED_STATUS_METHOD);

		try {

			prepareStatement = getConnection(pConnection).prepareStatement(UPDATE_FEED_STATUS);
			prepareStatement.setString(1, pStatus);
			prepareStatement.setString(2, pFeedId);

			return prepareStatement.executeUpdate();

		} catch (SQLException sqlex) {

			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlex));
			}
			BBBPerformanceMonitor.cancel(UPDATE_FEED_STATUS_METHOD);
			isMonitorCanceled = true;

		} finally {

			closePreparedStatement(prepareStatement);
			if (!isMonitorCanceled) {
				BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, UPDATE_FEED_STATUS_METHOD);
			}
		}
		return 0;
	}

	/**
	 * This method update the list of feed to be deployed in the production project. 
	 * @param pProjectId
	 * @param pFeedId
	 * @param pConnection
	 * @throws BBBSystemException
	 */
	public void updateFeedListInPrjToDeploy(final Project pProject, final String pFeedId, final Connection pConnection) 
		throws BBBSystemException {

		if(isLoggingDebug()) {
			logDebug("PIM Feed Tools : updateFeedListInPrjToDeploy : Entry");
		}
		String projectId = pProject.getId();
		String projectName = pProject.getDisplayName();
		ResultSet resultSet = null;
		PreparedStatement prepareStatement = null;
		BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, UPDATE_FEED_LIST_FOR_PROJECT);
		try {
			prepareStatement = pConnection.prepareStatement(SELECT_PRJ_IMPORTED_FEED, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			prepareStatement.setString(1, projectId);
			prepareStatement.setInt(2, Integer.parseInt(pFeedId.trim()));
			resultSet = prepareStatement.executeQuery();

			if (resultSet != null && !resultSet.next()) {
				prepareStatement = pConnection.prepareStatement(INSERT_PRJ_IMPORTED_FEED);
				prepareStatement.setString(1, projectId);
				prepareStatement.setInt(2, Integer.parseInt(pFeedId.trim()));
				prepareStatement.setString(3, projectName);
				int updateCount = prepareStatement.executeUpdate();
				if (updateCount >= 1) {
					if(isLoggingDebug()) {
						logDebug("Feed : " + pFeedId + " is processed in project : " + projectName);
					}
				}
			} else {
				if(isLoggingDebug()) {
					logDebug("Feed : " + pFeedId + " is already listed for processed feed in project with Id : " + projectName + ", hence not updating feed list.");
				}
			}
		} catch (SQLException sqlex) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(sqlex));
			}
		} finally {
			closePreparedStatement(prepareStatement);
			
				BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, UPDATE_FEED_LIST_FOR_PROJECT);
		}
		if(isLoggingDebug()) {
			logDebug("PIM Feed Tools : updateFeedListInPrjToDeploy : Exit");
		}
	}

//	public void updateFeedStatus(final String pDeploymentState)
//			throws BBBSystemException {
//		String currentState = null;
//		String currentStateEmergency = null;
//		String nextState = null;
//		if (pDeploymentState.equalsIgnoreCase("Staging")
//				|| pDeploymentState.equalsIgnoreCase("Preview")) {
//			currentState = STAGING_IN_PROGRESS;
//			nextState = DEPLOYED_TO_STAGING;
//		}
//		if (pDeploymentState.equalsIgnoreCase("Production")) {
//			currentState = PRODUCTION_IN_PROGRESS;
//			currentStateEmergency = EMERGENCY_IN_PROGRESS;
//			nextState = "CLOSED";
//		}
//		PreparedStatement prepareStatement = null;
//		boolean isMonitorCanceled = false;
//		BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, UPDATE_FEED_STATUS_METHOD);
//		Connection connection = null;
//		try {
//
//			connection = openConnection();
//			if (connection != null) {
//				prepareStatement = getConnection(connection).prepareStatement(
//						UPDATE_FEED_STATUS_AFTER_DEPLOYMENT);
//				prepareStatement.setString(1, nextState);
//				prepareStatement.setString(2, currentState);
//				prepareStatement.setString(3, currentStateEmergency);
//				prepareStatement.executeUpdate();
//			}
//		} catch (SQLException sqlex) {
//
//			if (isLoggingError()) {
//
//				logError(BBBStringUtils.stack2string(sqlex));
//			}
//			BBBPerformanceMonitor.cancel(UPDATE_FEED_STATUS_METHOD);
//			isMonitorCanceled = true;
//
//		} finally {
//
//			closePreparedStatement(prepareStatement);
//			closeConnection(connection);
//			if (!isMonitorCanceled) {
//				BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, UPDATE_FEED_STATUS_METHOD);
//			}
//		}
//	}

//	public void updateFeedStatus(final String pNextState,
//			final String pPreviousState) throws BBBSystemException {
//		PreparedStatement prepareStatement = null;
//		boolean isMonitorCanceled = false;
//		BBBPerformanceMonitor
//				.start(IMPORT_PROCESS_PIMTOOLS, "updateFeedStatus");
//		Connection connection = null;
//		try {
//
//			connection = openConnection();
//			if (connection != null) {
//				prepareStatement = getConnection(connection).prepareStatement(UPDATE_FEED_STATUS_AFTER_DEPLOYMENT);
//				prepareStatement.setString(1, pNextState);
//				prepareStatement.setString(2, pPreviousState);
//				prepareStatement.executeUpdate();
//			}
//		} catch (SQLException sqlex) {
//
//			if (isLoggingError()) {
//
//				logError(BBBStringUtils.stack2string(sqlex));
//			}
//			BBBPerformanceMonitor.cancel("updateFeedStatus");
//			isMonitorCanceled = true;
//
//		} finally {
//
//			closePreparedStatement(prepareStatement);
//			closeConnection(connection);
//			if (!isMonitorCanceled) {
//				BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, "updateFeedStatus");
//			}
//		}
//	}

//	Start :- Added for Pricing Feed T02
	/**
	 * 
	 * @param pFeedId
	 * @param pStatus
	 * @return
	 */
	public int updatePricingFeedStatus(final String pFeedId, final String pStatus, final Connection pConnection) throws BBBSystemException {

		PreparedStatement prepareStatement = null;
		boolean isMonitorCanceled = false;
		BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, UPDATE_FEED_STATUS_METHOD);

		try {

			prepareStatement = getConnection(pConnection).prepareStatement(UPDATE_PRICING_FEED_STATUS);
			prepareStatement.setString(1, pStatus);
			prepareStatement.setString(2, pFeedId);

			return prepareStatement.executeUpdate();

		} catch (SQLException sqlex) {

			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlex));
			}
			BBBPerformanceMonitor.cancel(UPDATE_FEED_STATUS_METHOD);
			isMonitorCanceled = true;

		} finally {
			closePreparedStatement(prepareStatement);
			if (!isMonitorCanceled) {
				BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, UPDATE_FEED_STATUS_METHOD);
			}
		}
		return 0;
	}


	/**
	 * @param pDeploymentState
	 * @throws BBBSystemException
	 */
//	public void updatePricingFeedStatus(final String pDeploymentState)
//			throws BBBSystemException {
//		String currentState = null;
//		String currentStateEmergency = null;
//		String nextState = null;
//		if (pDeploymentState.equalsIgnoreCase("Staging") || pDeploymentState.equalsIgnoreCase("Preview")) {
//			currentState = STAGING_IN_PROGRESS;
//			nextState = DEPLOYED_TO_STAGING;
//		}
//		if (pDeploymentState.equalsIgnoreCase("Production")) {
//			currentState = PRODUCTION_IN_PROGRESS;
//			currentStateEmergency = EMERGENCY_IN_PROGRESS;
//			nextState = "CLOSED";
//		}
//		PreparedStatement prepareStatement = null;
//		boolean isMonitorCanceled = false;
//		BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, UPDATE_FEED_STATUS_METHOD);
//		Connection connection = null;
//		try {
//
//			connection = openConnection();
//			if (connection != null) {
//				prepareStatement = getConnection(connection).prepareStatement(UPDATE_PRICING_FEED_STATUS_AFTER_DEPLOYMENT);
//				prepareStatement.setString(1, nextState);
//				prepareStatement.setString(2, currentState);
//				prepareStatement.setString(3, currentStateEmergency);
//				prepareStatement.executeUpdate();
//			}
//		} catch (SQLException sqlex) {
//
//			if (isLoggingError()) {
//
//				logError(BBBStringUtils.stack2string(sqlex));
//			}
//			BBBPerformanceMonitor.cancel(UPDATE_FEED_STATUS_METHOD);
//			isMonitorCanceled = true;
//
//		} finally {
//
//			closePreparedStatement(prepareStatement);
//			closeConnection(connection);
//			if (!isMonitorCanceled) {
//				BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, UPDATE_FEED_STATUS_METHOD);
//			}
//		}
//	}

	/**
	 * @param pNextState
	 * @param pPreviousState
	 * @throws BBBSystemException
	 */
//	public void updatePricingFeedStatus(final String pNextState,
//			final String pPreviousState) throws BBBSystemException {
//		PreparedStatement prepareStatement = null;
//		boolean isMonitorCanceled = false;
//		BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, "updateFeedStatus");
//		Connection connection = null;
//		try {
//
//			connection = openConnection();
//			if (connection != null) {
//				prepareStatement = getConnection(connection).prepareStatement(UPDATE_PRICING_FEED_STATUS_AFTER_DEPLOYMENT);
//				prepareStatement.setString(1, pNextState);
//				prepareStatement.setString(2, pPreviousState);
//				prepareStatement.executeUpdate();
//			}
//		} catch (SQLException sqlex) {
//
//			if (isLoggingError()) {
//
//				logError(BBBStringUtils.stack2string(sqlex));
//			}
//			BBBPerformanceMonitor.cancel("updateFeedStatus");
//			isMonitorCanceled = true;
//
//		} finally {
//
//			closePreparedStatement(prepareStatement);
//			closeConnection(connection);
//			if (!isMonitorCanceled) {
//				BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, "updateFeedStatus");
//			}
//		}
//	}

// End :- Added for Pricing Feed T02	
	/**
	 * 
	 * @param pCategoryId
	 * @param pSiteId
	 * @param pFeedId
	 * @param pConnection
	 * @return
	 * @throws BBBBusinessException
	 */
  public CategoryVO getPIMCategoryDetail(final String pCategoryId, final String pFeedId, Connection pConnection)
			throws BBBSystemException, BBBBusinessException {

		boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, GET_PIM_CATEGORY_DETAIL);
		String query = SELECT_CATEGORY_DETAILS;
		PreparedStatement prepareStatement = null;
		CategoryVO categoryVO = null;

		ResultSet resultSet = null;
		try {

      prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
      resultSet = getResultSet(pConnection, prepareStatement, pFeedId, pCategoryId, null);
			categoryVO = new CategoryVO();
			populateCategories(resultSet, categoryVO);
      populateCategoryKeywords(pFeedId, pCategoryId, categoryVO, pConnection);
			return categoryVO;

		} catch (SQLException sqlException) {
			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}
			BBBPerformanceMonitor.cancel(GET_PIM_CATEGORY_DETAIL);
			isMonitorCanceled = true;

      throw new BBBBusinessException(BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,sqlException.getMessage(), sqlException);
		} finally {

			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
			if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, GET_PIM_CATEGORY_DETAIL);

			}
		}
	}

	/**
	 * 
	 * @param pItemId
	 * @param pFeedId
	 * @param pConnection
	 * @return
	 * @throws BBBBusinessException
	 */
  public String getPIMKeywordsDetails(final String pQuery, final String pFeedId, final String pItemId,
      final String pResultSetArg, final Connection pConnection) throws BBBSystemException, BBBBusinessException {

		String keyword = null;
    List<String> keywordList = getIdList(pConnection, pQuery, pFeedId, pItemId, pResultSetArg);
		if (keywordList != null && !keywordList.isEmpty()) {
			keyword = keywordList.get(0);
		}

		return keyword;
	}

	/**
	 * 
	 * @param pFeedId
	 * @param pItemId
	 * @param pConnection
	 * @return
	 * @throws BBBBusinessException
	 */
  public ItemVO getPIMItemDetail(final String pFeedId, final String pItemId, final boolean isFrequent,
      final boolean isRare, Connection pConnection) throws BBBSystemException, BBBBusinessException {
		boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, GET_PIM_ITEM_DETAIL);
		String query = getProductDetailQuery(isFrequent, isRare);
		PreparedStatement prepareStatement = null;
		ItemVO itemVO = null;
		ResultSet resultSet = null;
		if (isLoggingDebug()) {

			logDebug("In getPIMItemDetail()");
			logDebug("pFeedId=" + pFeedId);
			logDebug("isFrequent=" + isFrequent);
			logDebug("isRare=" + isRare);
			logDebug(query);
		}

		try {

      prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
      resultSet = getResultSet(pConnection, prepareStatement, pFeedId, pItemId, null);
      itemVO = populateItem(resultSet, pFeedId, pItemId, isFrequent, isRare, pConnection);

			if (isLoggingDebug()) {
				logDebug("End of getPIMItemDetail()");
			}
		} catch (SQLException sqlException) {
			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}
			BBBPerformanceMonitor.cancel(GET_PIM_ITEM_DETAIL);
			isMonitorCanceled = true;

      throw new BBBBusinessException(BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,sqlException.getMessage(), sqlException);
		} finally {

			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
			if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, GET_PIM_ITEM_DETAIL);
			}
		}
		return itemVO;
	}

	/**
	 * Reomve this Method
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pMediaId
	 * @param pConnection
	 * @return
	 */
  public ItemMediaVO getPIMItemMediaDetail(String pFeedId, final String pSkuId, final String pMediaId,
      Connection pConnection) throws BBBSystemException, BBBBusinessException {
		boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, GET_PIM_ITEM_MEDIA_DETAIL);
		String query = SELECT_ITEM_MEDIA_DETAILS;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {

      prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
      resultSet = getResultSet(pConnection, prepareStatement, pFeedId, pSkuId, pMediaId);
			return populateItemMedia(resultSet);

		} catch (SQLException sqlException) {

			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}
			BBBPerformanceMonitor.cancel(GET_PIM_ITEM_MEDIA_DETAIL);
			isMonitorCanceled = true;
      throw new BBBBusinessException(BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,sqlException.getMessage(), sqlException);
		} finally {

			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
			if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, GET_PIM_ITEM_MEDIA_DETAIL);

			}
		}
	}

	/**
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pAttributeId
	 * @param pConnection
	 * @return
	 */
	/*
	 * public SkuDisplayAttributesVO getPIMSkuAttributeDetail(String pFeedId,
	 * final String pSkuId, final String pAttributeId, final String
	 * pValueId,Connection pConnection) throws BBBSystemException,
	 * BBBBusinessException { boolean isMonitorCanceled = false;
	 * BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS,
	 * GET_PIM_SKU_ATT_DETAIL); String query =
   * SELECT_SKU_DISPLAY_ATTRIBUTES_DETAILS; PreparedStatement prepareStatement =
   * null; ResultSet resultSet = null; try {
	 * 
	 * prepareStatement = getConnection(pConnection).prepareStatement(query,
	 * ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY); resultSet =
	 * getAttributeResultSet(pConnection, prepareStatement, pFeedId, pSkuId,
	 * pAttributeId, pValueId); return populateSkuAttribute(resultSet);
	 * 
	 * } catch (SQLException sqlException) {
	 * 
	 * if (isLoggingError()) {
	 * 
	 * logError(BBBStringUtils.stack2string(sqlException)); } if
	 * (isLoggingDebug()) {
	 * 
	 * logDebug(BBBStringUtils.stack2string(sqlException)); }
	 * 
	 * BBBPerformanceMonitor.cancel(GET_PIM_SKU_ATT_DETAIL); isMonitorCanceled =
	 * true;
	 * 
   * throw new BBBBusinessException(sqlException.getMessage(), sqlException); }
   * finally {
	 * 
	 * closeResultSet(resultSet); closePreparedStatement(prepareStatement); if
	 * (!isMonitorCanceled){ BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS,
	 * GET_PIM_SKU_ATT_DETAIL); }
	 * 
	 * 
	 * } }
	 */
	/**
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pMediaId
	 * @param pConnection
	 * @return
	 */
  public MediaVO getPIMOtherMediaDetail(String pFeedId, String pMediaId, Connection pConnection)
      throws BBBSystemException, BBBBusinessException {

		boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, GET_PIM_OTHER_MEDIA_DETAIL);
		String query = SELECT_MEDIA_DETAILS;
		PreparedStatement prepareStatement = null;

		ResultSet resultSet = null;
		try {

      prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
      resultSet = getResultSet(pConnection, prepareStatement, pFeedId, pMediaId, null);

			return populateMedia(resultSet);

		} catch (SQLException sqlException) {

			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}
			BBBPerformanceMonitor.cancel(GET_PIM_OTHER_MEDIA_DETAIL);
			isMonitorCanceled = true;
      throw new BBBBusinessException(BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,sqlException.getMessage(), sqlException);
		} finally {

			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
			if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, GET_PIM_OTHER_MEDIA_DETAIL);

			}
		}
	}

	/**
	 * This method fetch all the unique AttributIds
	 * 
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pConnection
	 * @return BrandVO
	 */
  public List<String> getUniqueAttributesIds(final String pFeedId, final Connection pConnection)
      throws BBBSystemException {

    return getIdList(pConnection, SELECT_UNIQUE_ATTRIBUTES_IDS, pFeedId, null, "ITEM_ATTRIBUTE_ID");
	}

	/**
	 * Get the attribute item details
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pItemAttributeId
	 * @param pConnection
	 * @return
	 */
  public AttributeVO getItemAttributesDetail(String pFeedId, String pItemAttributeId, Connection pConnection)
			throws BBBSystemException, BBBBusinessException {
		boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, GET_ITEM_ATT_DETAIL);
		String query = SELECT_ITEM_ATTRIBUTES_DETAILS;
		PreparedStatement prepareStatement = null;

		ResultSet resultSet = null;
		try {

      prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
      resultSet = getResultSet(pConnection, prepareStatement, pFeedId, pItemAttributeId, null);

			return populateItemAttributes(resultSet);

		} catch (SQLException sqlException) {

			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}

			BBBPerformanceMonitor.cancel(GET_ITEM_ATT_DETAIL);
			isMonitorCanceled = true;
      throw new BBBBusinessException(BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,sqlException.getMessage(), sqlException);
		} finally {

			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
			if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, GET_ITEM_ATT_DETAIL);
			}
		}
	}

	/**
	 * Get the attribute item details
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pItemAttributeId
	 * @param pConnection
	 * @return
	 */
  public AttributeVO getItemAttributesDetailValue(String pFeedId, String pItemAttributeId, String pItemValueId,
      Connection pConnection) throws BBBSystemException, BBBBusinessException {
		boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, GET_ITEM_ATT_DETAIL);
		String query = SELECT_ITEM_ATTRIBUTES_DETAILS_INC_VALUE;
		PreparedStatement prepareStatement = null;

		ResultSet resultSet = null;
		try {

      prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
      resultSet = getResultSet(pConnection, prepareStatement, pFeedId, pItemAttributeId, pItemValueId);

			return populateItemAttributes(resultSet);

		} catch (SQLException sqlException) {

			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}

			BBBPerformanceMonitor.cancel(GET_ITEM_ATT_DETAIL);
			isMonitorCanceled = true;
      throw new BBBBusinessException(BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,sqlException.getMessage(), sqlException);
		} finally {

			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
			if (!isMonitorCanceled)
        BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, GET_ITEM_ATT_DETAIL);
		}
	}

	/**
   * if frequent is true and rare is true - else if frequent is true and rare is
   * false - Fetch only frequent data else if frequent is false and rare is true
   * - Fetch only Rare data otherwise Fetch both frequent and rare data
	 * 
	 * @param pIsFrequent
	 * @param pIsRare
	 * @return
	 */
	private String getProductDetailQuery(boolean pIsFrequent, boolean pIsRare) {

		if (pIsFrequent && pIsRare) {
			// Fetch both frequent and rare data
			return SELECT_ITEM_DETAILS;
		} else if (pIsFrequent && !pIsRare) {
			// Fetch only Frequent data
			return SELECT_FREQUENT_ITEM_DETAILS;
		} else if (!pIsFrequent && pIsRare) {
			// Fetch only Rare data
			return SELECT_RARE_ITEM_DETAILS;
		}
		return SELECT_ITEM_DETAILS;
	}

	/**
	 * 
	 * @param pFeedId
	 * @param pItemId
	 * @return
	 * @throws BBBBusinessException
	 */
  public ResultSet getPIMRareItemDetail(final String pFeedId, final String pItemId, final Connection pConnection)
			throws BBBSystemException, BBBBusinessException {

		String query = SELECT_RARE_ITEM_DETAILS;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {

      prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
      resultSet = getResultSet(pConnection, prepareStatement, pFeedId, pItemId, null);

			return resultSet;
		} catch (SQLException sqlException) {

			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}

      throw new BBBBusinessException(BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,sqlException.getMessage(), sqlException);
		} finally {

			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
		}

	}

	/**
	 * This method fetch all the brand details from the staging data base and
	 * populate BrandVO for the calling method
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pBrandId
	 * @param pConnection
	 * @return BrandVO
	 * @throws BBBBusinessException
	 */
  public BrandVO getBrandDetails(final String pFeedId, final String pBrandId, final Connection pConnection)
      throws BBBSystemException, BBBBusinessException {
		boolean isMonitorCanceled = false;
		BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, GET_BRAND_DETAILS);
		String query = SELECT_BRANDS_DETAILS;

		if (isLoggingDebug()) {

			logDebug("query=" + query);
		}
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {

      prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
      resultSet = getResultSet(pConnection, prepareStatement, pFeedId, pBrandId, null);
			return populateBrandDetails(resultSet);
		} catch (SQLException sqlException) {

			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}

			BBBPerformanceMonitor.cancel(GET_BRAND_DETAILS);
			isMonitorCanceled = true;
      throw new BBBBusinessException(BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,sqlException.getMessage(), sqlException);
		} finally {

			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
			if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, GET_BRAND_DETAILS);
      }
    }
  }
	/**
	 * This method fetch all the brand details from the staging data base and
	 * populate BrandVO for the calling method
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pRegionId
	 * @param pConnection
	 * @return BrandVO
	 * @throws BBBBusinessException
	 */
  public ZipCodeVO getZipCodeRegionDetails(final String pFeedId, final String pRegionId, final Connection pConnection)
			throws BBBSystemException, BBBBusinessException {
		boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, "GET REGION DETAILS");
		String query = SELECT_SKU_ZIP_DETAILS;

		if (isLoggingDebug()) {

			logDebug("query=" + query);
		}
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {

      prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
      resultSet = getResultSet(pConnection, prepareStatement, pFeedId, pRegionId, null);
			return populateZipCodeRegionDetails(resultSet);
		} catch (SQLException sqlException) {

			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}

			BBBPerformanceMonitor.cancel(GET_BRAND_DETAILS);
			isMonitorCanceled = true;
      throw new BBBBusinessException(sqlException.getMessage(), sqlException);
		} finally {

			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
			if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, GET_BRAND_DETAILS);
			}
		}
	}

	/**
	 * This method fetch all the pricing details from the staging data base and
	 * populate SkuPricingVO for the calling method
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pSkuId
	 * @param pConnection
	 * @return BrandVO
	 * @throws BBBBusinessException
	 */
  public SkuPricingVO getSkuPricingDetails(final String pFeedId, final String pSkuId, final Connection pConnection)
			throws BBBSystemException, BBBBusinessException {
		boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, GET_SKU_PRICING_DETAILS);
		String query = SELECT_SKU_PRICING_DETAILS;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {

      prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
      resultSet = getResultSet(pConnection, prepareStatement, pFeedId, pSkuId, null);
			return populateSkuPricing(resultSet);
		} catch (SQLException sqlException) {

			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}

			BBBPerformanceMonitor.cancel(GET_SKU_PRICING_DETAILS);
			isMonitorCanceled = true;
      throw new BBBBusinessException(BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,sqlException.getMessage(), sqlException);
		} finally {

			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
			if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, GET_SKU_PRICING_DETAILS);

			}
		}
	}

	/**
	 * This method fetch all the unique sku ids from the staging data base and
	 * returns unique sku List
	 * 
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pBrandId
	 * @param pConnection
	 * @return BrandVO
	 */
  public List<String> getUniqueSkuRebatesRelIds(final String pFeedId, final Connection pConnection)
      throws BBBSystemException {

    return getIdList(pConnection, SELECT_UNIQUE_SKU_REBATES_REL_IDS, pFeedId, null, "SKU_ID");
	}

	/**
	 * This method fetch all the unique rebateIds
	 * 
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pConnection
	 * @return BrandVO
	 */
  public List<String> getUniqueRebateIds(final String pFeedId, final Connection pConnection) throws BBBSystemException {

    return getIdList(pConnection, SELECT_UNIQUE_REBATE_IDS, pFeedId, null, "REBATE_ID");
	}
	public List<String> getUniqueStofuImagesId(final String pFeedId,
			final Connection pConnection) throws BBBSystemException {

		return getIdList(pConnection, SELECT_ALL_STOFU_IMAGES, pFeedId, null,
				"SKU_ID");
	}
	/**
	 * This method fetch all the rebate info details from the staging data base
	 * and populate RebateVO for the calling method
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pBrandId
	 * @param pConnection
	 * @return BrandVO
	 * @throws BBBBusinessException
	 */
  public RebateVO getRebateDetails(final String pFeedId, final String pRebateId, final Connection pConnection)
			throws BBBSystemException, BBBBusinessException {
		boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, GET_REBATE_DETAILS);
		String query = SELECT_REBATE_DETAILS;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {

      prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
      resultSet = getResultSet(pConnection, prepareStatement, pFeedId, pRebateId, null);
			return populateRebateDetails(resultSet);
		} catch (SQLException sqlException) {

			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}
			BBBPerformanceMonitor.cancel(GET_REBATE_DETAILS);
			isMonitorCanceled = true;
      throw new BBBBusinessException(BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,sqlException.getMessage(), sqlException);
		} finally {

			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
			if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, GET_REBATE_DETAILS);

			}
		}

	}

	/**
	 * This method fetch all the unique skus details from the staging data base
	 * and returns unique SkuId List
	 * 
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pBrandId
	 * @param pConnection
	 * @return BrandVO
	 */
  public List<String> getUniqueProductTabs(final String pFeedId, final Connection pConnection)
      throws BBBSystemException {

    return getIdList(pConnection, SELECT_UNIQUE_PRODUCT_TABS, pFeedId, null, "SKU_ID");
	}

	/**
	 * This method fetch all the unique products ids from the staging data base
	 * and returns unique product List
	 * 
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pBrandId
	 * @param pConnection
	 * @return BrandVO
	 */
  public List<String> getUniqueMediaItemRelIds(final String pFeedId, final Connection pConnection)
      throws BBBSystemException {

    return getIdList(pConnection, SELECT_UNIQUE_MEDIA_REL_IDS, pFeedId, null, "SKU_ID");
	}

	/**
	 * This method fetch all the Sku Attributes and its operation and returns
	 * SkuAttributesVO
	 * 
	 * @param pFeedId
	 * @param pId
	 * @param pConnection
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
  public List<SkuDisplayAttributesVO> getSkuAttributeList(final String pFeedId, final String pId,
      final Connection pConnection) throws BBBSystemException, BBBBusinessException {

		// QueryVO queryVO = new QueryVO(SELECT_SKU_ATTRIBUTES, new
		// String[]{pFeedId, pId, "ITEM_ATTRIBUTE_ID", null,"OPERATION_FLAG",
		// "ITEM_VALUE_ID"});

		return getSkuAttributesVOList(pConnection, pId, pFeedId);
	}

	/**
	 * This method fetch all the unique sku ids from the staging data base and
	 * returns unique sku List
	 * 
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pBrandId
	 * @param pConnection
	 * @return BrandVO
	 */
  public List<String> getUniqueSkuAttributesRelIds(final String pFeedId, final Connection pConnection)
      throws BBBSystemException {

    return getIdList(pConnection, SELECT_UNIQUE_SKU_ATTRIBUTES, pFeedId, null, "SKU_ID");
	}

	/**
	 * Get Rebate Ids and Operation flag for the provided skuId and feedId
	 * 
	 * @param pFeedId
	 * @param pSkuId
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
  public List<OperationVO> getRebateList(final String pFeedId, final String pSkuId, final Connection pConnection)
			throws BBBSystemException, BBBBusinessException {

		String querySite = SELECT_REBATE_IDS;

		if (isLoggingDebug()) {

			logDebug(querySite);
		}

    List<OperationVO> rebateIdsVOList = getIdVOList(pConnection, querySite, pFeedId, pSkuId, "REBATE_ID", null,
        "OPERATION_FLAG");

		if (isLoggingDebug()) {

			logDebug("Latest feedId" + pFeedId + " SkuId=" + pSkuId);
		}
		return rebateIdsVOList;

	}

	/**
	 * This method fetch all the media id and its operation and returns
	 * OperationVO
	 * 
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pBrandId
	 * @param pConnection
	 * @return BrandVO
	 * @throws BBBBusinessException
	 */
  public List<OperationVO> getSkuMedia(final String pFeedId, final String pId, final Connection pConnection)
			throws BBBSystemException, BBBBusinessException {

    return getIdVOList(pConnection, SELECT_SKU_MEDIA, pFeedId, pId, "MEDIA_ID", null, "OPERATION_FLAG");
	}

	// Stofu Related Method
	public StofuImageParentVO getStofuImages(final String pFeedId,
			final String pSkuId, final Connection pConnection)
			throws BBBSystemException, BBBBusinessException {
		String queryImages = SELECT_SKU_IMAGES;
		String quesyDistinctProductFlag = SELECT_DISTINCT_PRODUCTFLAG;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		StofuImagesVO stofuImagesVO = null;
		List<StofuImagesVO> stofuImagesVOList = new ArrayList<StofuImagesVO>();
		StofuImageParentVO stofuImageParentVO = new StofuImageParentVO();
		try {
			prepareStatement = getConnection(pConnection).prepareStatement(
					quesyDistinctProductFlag, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			resultSet = getResultSet(pConnection, prepareStatement, pFeedId,
					pSkuId, null);

			if (resultSet.next()) {
				boolean productFlag = getBoolean("PRODUCT_FLAG", resultSet);
				stofuImageParentVO.setProductFlag(productFlag);
			}
			if (!resultSet.next()) {
				try {

					prepareStatement = getConnection(pConnection)
							.prepareStatement(queryImages,
									ResultSet.TYPE_FORWARD_ONLY,
									ResultSet.CONCUR_READ_ONLY);
					resultSet = getResultSet(pConnection, prepareStatement,
							pFeedId, pSkuId, null);

					while (resultSet.next()) {
						stofuImagesVO = populateStofuImagesVO(resultSet);
						stofuImagesVOList.add(stofuImagesVO);

					}

					stofuImageParentVO.setStofuImagesVOList(stofuImagesVOList);

				} catch (SQLException sqlException) {

					if (isLoggingError()) {

						logError(BBBStringUtils.stack2string(sqlException));
					}
					if (isLoggingDebug()) {

						logDebug(BBBStringUtils.stack2string(sqlException));
					}

					throw new BBBBusinessException(
							BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,
							sqlException.getMessage(), sqlException);
				}
			}

			else {
				return null;
			}
			return stofuImageParentVO;
		} catch (SQLException sqlException) {

			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}

			throw new BBBBusinessException(
					BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,
					sqlException.getMessage(), sqlException);
		}

		finally {

			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);

		}

	}

	private StofuImagesVO populateStofuImagesVO(final ResultSet pResultSet)
			throws SQLException {

		if (pResultSet == null) {

			return null;
		}
		StofuImagesVO stofuImagesVO = new StofuImagesVO();
		String scene7URL = pResultSet.getString("SCENE7_URL");
		String shotType = pResultSet.getString("SHOT_TYPE");
		String operationFlag = pResultSet.getString("OPERATION_FLAG");
		stofuImagesVO.setScene7URL(scene7URL);
		stofuImagesVO.setShotType(shotType);
		stofuImagesVO.setOperationFlag(operationFlag);
		return stofuImagesVO;
	}

	/**
	 * This method fetch all the unique skus details from the staging data base
	 * and returns OperationVO
	 * 
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pBrandId
	 * @param pConnection
	 * @return BrandVO
	 * @throws BBBBusinessException
	 */
  public List<OperationVO> getProductTabs(final String pFeedId, final String pSKUId, final Connection pConnection)
			throws BBBSystemException, BBBBusinessException {

    return getIdVOList(pConnection, SELECT_SKU_PRODUCT_TABS, pFeedId, pSKUId, "TAB_ID", "SITE_FLAG", "OPERATION_FLAG");
	}

  public ProductTabVO getProductTabsDetails(final String pFeedId, final String pId, final String pTabId,
      final Connection pConnection) throws BBBBusinessException, BBBSystemException {
		boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, GET_PRODUCT_TABS_DETAILS);
		String query = SELECT_ITEM_TAB_DETAILS;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {

      prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
      resultSet = getResultSet(pConnection, prepareStatement, pFeedId, pId, pTabId);
			return populateProductTabsVO(resultSet);
		} catch (SQLException sqlException) {

			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}
			BBBPerformanceMonitor.cancel(GET_PRODUCT_TABS_DETAILS);
			isMonitorCanceled = true;

      throw new BBBBusinessException(BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,sqlException.getMessage(), sqlException);
		} finally {

			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
			if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, GET_PRODUCT_TABS_DETAILS);

			}
		}
	}

	/**
	 * This method fetch all the unique items ids from the staging data base and
	 * returns item ids List
	 * 
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pBrandId
	 * @param pConnection
	 * @return BrandVO
	 */
	/*
   * public List<String> getUniqueColorPictureItems(final String pFeedId, final
   * Connection pConnection) throws BBBSystemException {
	 * 
	 * return getIdList(pConnection, SELECT_ALL_COLOR_PICTURE, pFeedId, null,
	 * SKU_ID); }
	 */

	/**
	 * This method fetch all the unique items ids from the staging data base and
	 * returns media ids List
	 * 
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pBrandId
	 * @param pConnection
	 * @return BrandVO
	 */
  public List<String> getUniqueMediaIds(final String pFeedId, final Connection pConnection) throws BBBSystemException {

    return getIdList(pConnection, SELECT_ALL_MEDIA, pFeedId, null, "MEDIA_ID");
	}

	/**
	 * This method fetch all the unique skuids for pricing association from the
	 * staging table ECP_SKU_PRICING_NEW and returns sku ids List
	 * 
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pConnection
	 * @return BrandVO
	 */
  public List<String> getUniquePricingSkus(final String pFeedId, final Connection pConnection)
      throws BBBSystemException {

    return getIdList(pConnection, SELECT_UNIQUE_PRICING_SKU_IDS, pFeedId, null, SKU_ID);
	}

	/*
   * public ItemColorPictureVO getColorPictureItemDetails(String pFeedId, String
   * pId, Connection pConnection) throws BBBSystemException,
	 * BBBBusinessException {
	 * 
	 * String query = SELECT_COLOR_PICTURE_DETAILS; PreparedStatement
	 * prepareStatement = null; ItemColorPictureVO itemColorPictureVO = null;
	 * ResultSet resultSet = null; try {
	 * 
	 * prepareStatement = getConnection(pConnection).prepareStatement(query,
	 * ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY); resultSet =
	 * getResultSet(pConnection, prepareStatement, pFeedId, pId, null);
	 * 
	 * itemColorPictureVO = populateColorPictureVO(resultSet); } catch
	 * (SQLException sqlException) {
	 * 
	 * if (isLoggingError()) {
	 * 
	 * logError(BBBStringUtils.stack2string(sqlException)); } if
	 * (isLoggingDebug()) {
	 * 
	 * logDebug(BBBStringUtils.stack2string(sqlException)); }
	 * 
   * throw new BBBBusinessException(sqlException.getMessage(), sqlException); }
   * return itemColorPictureVO; }
	 */

	/**
   * This method get PreparedStatement as input and execute query and return the
   * ResultSet. Mostly this method is used to get the details of category items,
   * product items and others
	 * 
	 * @param pConnection
	 * @param pPrepareStatement
	 * @param pQuery
	 * @param pParamArg1
	 * @param pParamArg2
	 * @param pParamArg3
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
  private ResultSet getResultSet(Connection pConnection, PreparedStatement pPrepareStatement, final String pParamArg1,
      final String pParamArg2, final String pParamArg3) throws BBBSystemException, BBBBusinessException {
		boolean isMonitorCanceled = false;

		ResultSet resultSet = null;
		if (isLoggingDebug()) {

			logDebug("In the getResulSet Get Details");
			logDebug("param1 =" + pParamArg1);
			logDebug("param2 =" + pParamArg2);
			logDebug("param3 =" + pParamArg3);
		}
		try {

      BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, GET_RESULT_SET);
			pPrepareStatement.setString(1, pParamArg1);

			if (!StringUtils.isEmpty(pParamArg2)) {

				pPrepareStatement.setString(2, pParamArg2);
			}
			if (!StringUtils.isEmpty(pParamArg3)) {

				pPrepareStatement.setString(3, pParamArg3);
			}
			resultSet = pPrepareStatement.executeQuery();
			if (isLoggingDebug()) {

				logDebug("Query Executed successfully");
			}
		} catch (SQLException sqlException) {

			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}
			BBBPerformanceMonitor.cancel(GET_RESULT_SET);
			isMonitorCanceled = true;
      throw new BBBBusinessException(BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,sqlException.getMessage(), sqlException);
		} finally {
			if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, GET_RESULT_SET);
			}
		}
		return resultSet;
	}

	/**
	 * 
	 * @param pFeedId
	 * @param pTable
	 * @param pStartTime
	 * @param pEndTime
	 * @param pStatus
	 * @param pConnection
	 * @param pString4
	 */

  public void updateFeedLog(final String pFeedId, final String pLog, final String pTable, final String pStartDate,
      final String pEndDate, final int pTotalNoOfRecords, final String pStatus, final String pExceptionLog,
			Connection pConnection) throws BBBSystemException {

		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		
		// Concatenating mLogTableStatusPrefix to Status string to identify which scheduler has inserted the row
		// Either it was done by the direct staging scheduler or the regular staging scheduler
		String status = pStatus;
		if(mLogTableStatusPrefix!=null && !mLogTableStatusPrefix.equals("")){
			status = mLogTableStatusPrefix + status;
		}

		String pQuery = "INSERT INTO ECP_FEED_LOG (FEED_ID, LOG_ID, TABLE_PROCESSED,START_TIME,END_TIME, NO_OF_RECORDS, STATUS, EXCEPTION_LOG) VALUES (?, ?,?,?, ?, ?,?, ?)";

		if (!StringUtils.isEmpty(pQuery)) {

			try {

        prepareStatement = getConnection(pConnection).prepareStatement(pQuery, ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				prepareStatement.setString(1, pFeedId);
				prepareStatement.setString(2, pLog);
				prepareStatement.setString(3, pTable);
				prepareStatement.setString(4, pStartDate);
				prepareStatement.setString(5, pEndDate);
				prepareStatement.setInt(6, pTotalNoOfRecords);
				prepareStatement.setString(7, status);
				prepareStatement.setString(8, pExceptionLog);

				prepareStatement.executeUpdate();

			} catch (SQLException e) {

				if (isLoggingError()) {

					logError(BBBStringUtils.stack2string(e));
				}

			} finally {

				closeResultSet(resultSet);
				closePreparedStatement(prepareStatement);
			}

		}

	}

	
// Start : - Added for Pricing Feed T02 
	/**
	 * 
	 * @param pFeedId
	 * @param pTable
	 * @param pStartTime
	 * @param pEndTime
	 * @param pStatus
	 * @param pConnection
	 * @param pString4
	 */

	public void updatePricingFeedLog(final String pFeedId, final String pLog,
			final String pTable, final String pStartDate,
			final String pEndDate, final int pTotalNoOfRecords,
			final String pStatus, final String pExceptionLog,
			Connection pConnection) throws BBBSystemException {

		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;

		String pQuery = "INSERT INTO ECP_PRICING_FEED_LOG (FEED_ID, LOG_ID, TABLE_PROCESSED,START_TIME,END_TIME, NO_OF_RECORDS, STATUS, EXCEPTION_LOG) VALUES (?, ?,?,?, ?, ?,?, ?)";

		if (!StringUtils.isEmpty(pQuery)) {

			try {

				prepareStatement = getConnection(pConnection).prepareStatement(
						pQuery, ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				prepareStatement.setString(1, pFeedId);
				prepareStatement.setString(2, pLog);
				prepareStatement.setString(3, pTable);
				prepareStatement.setString(4, pStartDate);
				prepareStatement.setString(5, pEndDate);
				prepareStatement.setInt(6, pTotalNoOfRecords);
				prepareStatement.setString(7, pStatus);
				prepareStatement.setString(8, pExceptionLog);

				prepareStatement.executeUpdate();

			} catch (SQLException e) {

				if (isLoggingError()) {

					logError(BBBStringUtils.stack2string(e));
				}

			} finally {

				closeResultSet(resultSet);
				closePreparedStatement(prepareStatement);
			}

		}

	}
//	End : - Added for Pricing Feed T02
	
	private CategoryVO populateCategories(final ResultSet pResultSet,
			final CategoryVO pCategoryVO) throws SQLException {

		if (pResultSet == null || !pResultSet.next()) {

			return null;
		}

		pCategoryVO.setCategoryName(pResultSet.getString("NODE_NAME"));
		pCategoryVO.setImageUrl(pResultSet.getString("NODE_IMAGE"));
		pCategoryVO.setIsCollege(getBoolean("IS_COLLEGE", pResultSet));
		pCategoryVO.setShopGuideId(pResultSet.getString("SHOP_GUIDE_ID"));
		pCategoryVO.setDisable(getBoolean("DISPLAY_FLAG", pResultSet));
		pCategoryVO.setSequenceNum(pResultSet.getInt("SEQUENCE_NUM"));
		pCategoryVO.setNodeType(pResultSet.getString("NODE_TYPE"));
		pCategoryVO.setOperationFlag(pResultSet.getString("OPERATION_FLAG"));
		pCategoryVO.setPhantomCategory(getBoolean("PHANTOM_CATEGORY",
				pResultSet));
		// PROPERTIES added for stofu
		pCategoryVO.setScene7URL(pResultSet.getString("scene7_url"));
		pCategoryVO.setGSimageorientation(pResultSet
				.getInt("GS_Image_Orientation"));

		return pCategoryVO;
	}

  private ItemVO populateItem(final ResultSet pResultSet, final String pFeedId, final String pItemId,
      final boolean pIsFrequent, final boolean pIsRare, final Connection pConnection) throws SQLException,
			BBBSystemException {

		ItemVO itemVO = new ItemVO();
		if (isLoggingDebug()) {

			logDebug("In populateItem");
		}
		if (pResultSet == null || !pResultSet.next()) {

			return null;
		}

    boolean collectionFlag = getBoolean("Collection_Flag", pResultSet);
    boolean productFlag = getBoolean("Product_Flag", pResultSet);
    boolean leadProdFlag = getBoolean("LEAD_PROD_FLAG", pResultSet);

    itemVO.setProductFlag(productFlag);
    itemVO.setCollectionFlag(collectionFlag);
    itemVO.setLeadProduct(leadProdFlag);

		if (productFlag || collectionFlag || leadProdFlag) {
			if (isLoggingDebug()) {

				logDebug("Item is a product");
			}

			if (pIsFrequent) {
				populateProductFrequentVO(pResultSet, itemVO);

			}
			if (pIsRare) {
				populateProductRareVO(pResultSet, itemVO);
				populateImageMedia(pResultSet, itemVO);
			}
			populateProductKeywords(pFeedId, pItemId, itemVO, pConnection);
		} else {
			if (isLoggingDebug()) {

				logDebug("Item is a sku");
			}

			if (pIsFrequent) {
				populateSkuFrequentVO(pResultSet, itemVO);
			}
			if (pIsRare) {

				populateSkuRareVO(pResultSet, itemVO);
				populateImageMedia(pResultSet, itemVO);
			}

		}
		if (isLoggingDebug()) {
			logDebug("End of populateItem");
		}
		return itemVO;
	}

	/**
   * Populate Item Rare properties Image details. Properties are common for both
   * Product and Sku
	 * 
	 * @param pResultSet
	 * @param pItemVO
	 * @throws SQLException
	 */
  private void populateImageMedia(ResultSet pResultSet, ItemVO pItemVO) throws SQLException {

		String imgSmallLoc = pResultSet.getString("IMG_SMALL_LOC");
		String imgMedLoc = pResultSet.getString("IMG_MED_LOC");
		String imgLargeLoc = pResultSet.getString("IMG_LARGE_LOC");
		String imgSwatchLoc = pResultSet.getString("IMG_SWATCH_LOC");
		String imgZoomLoc = pResultSet.getString("IMG_ZOOM_LOC");
		String imgZoomIndex = pResultSet.getString("IMG_ZOOM_INDEX");

		String anyWhereZoom = pResultSet.getString("ANY_WHERE_ZOOM");

		String collection = pResultSet.getString("COLLECTION_THUMBNAIL");

		// newly added properties for Horz IMG Loc and Vert IMG loc

		String imgHorzLoc = pResultSet.getString("IMG_HORZ_LOC");
		String imgVertLoc = pResultSet.getString("IMG_VERT_LOC");

		pItemVO.setImgSmallLoc(imgSmallLoc);
		pItemVO.setImgMedLoc(imgMedLoc);
		pItemVO.setImgLargeLoc(imgLargeLoc);
		pItemVO.setImgSwatchLoc(imgSwatchLoc);
		pItemVO.setImgZoomLoc(imgZoomLoc);
		pItemVO.setImgZoomIndex(imgZoomIndex);
		pItemVO.setAnyWhereZoom(anyWhereZoom);
		pItemVO.setCollectionThumbnail(collection);
		// newly added properties for Horz IMG Loc and Vert IMG loc
		pItemVO.setImgHorzLoc(imgHorzLoc);
		pItemVO.setImgVertLoc(imgVertLoc);
	}

	/**
	 * Populate frequent product details
	 * 
	 * @param pResultSet
	 * @param pItemVO
	 * @throws SQLException
	 */
  private void populateProductFrequentVO(ResultSet pResultSet, ItemVO pItemVO) throws SQLException {
		ProductVO productVO = pItemVO.getProductVO();

		String webOfferedFlag = pResultSet.getString("WEB_OFFERED_FLAG");
		String babWebOfferedFlag = pResultSet.getString("BAB_WEB_OFFERED_FLAG");
		String caWebOfferedFlag = pResultSet.getString("CA_WEB_OFFERED_FLAG");
		String productDisableFlag = pResultSet.getString("DISABLE_FLAG");
		String babDisableFlag = pResultSet.getString("BAB_DISABLE_FLAG");
		String caDisableFlag = pResultSet.getString("CA_DISABLE_FLAG");
		String intlRestricted = pResultSet.getString("INTL_RESTRICTED");
		String designatedProductId = pResultSet.getString("DESIGNATED_PRODUCT_ID");
		String productTitle = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("PRODUCT_TITLE"), false);
		// Calling method to update Stofu Flags
		populateStofuFrequentProductProperties(pResultSet, pItemVO);
		String caProductTitle = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("CA_PRODUCT_TITLE"), false);
		String babProductTitle = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("BAB_PRODUCT_TITLE"), false);
		String shortDescription = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("SHORT_DESCRIPTION"), false);
		String caShortDescription = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("CA_SHORT_DESCRIPTION"), false);
		String babShortDescription = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("BAB_SHORT_DESCRIPTION"), false);
		String webDescrip = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("WEB_DESCRIP"), false);
		String caWebDescrip = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("CA_WEB_DESCRIP"), false);
		String babWebDescrip = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("BAB_WEB_DESCRIP"), false);
		String priceRangeDescrip = pResultSet.getString("PRICE_RANGE_DESCRIP");

		String caPriceRangeDescrip = pResultSet
				.getString("CA_PRICE_RANGE_DESCRIP");
		String skuLowPrice = pResultSet.getString("SKU_LOW_PRICE");
		String caSkuLowPrice = pResultSet.getString("CA_SKU_LOW_PRICE");
		String skuHightPrice = pResultSet.getString("SKU_HIGH_PRICE");
		String caSkuHightPrice = pResultSet.getString("CA_SKU_HIGH_PRICE");
		// Stofu properties
		String gsProductTitle = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("GS_PRODUCT_TITLE"), false);
		String gsCaProductTitle = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("GS_CA_PRODUCT_TITLE"), false);
		String gsBabProductTitle = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("GS_BAB_PRODUCT_TITLE"), false);
		String gsShortDescription = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("GS_SHORT_DESCRIPTION"), false);
		String gsCaShortDescription = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("GS_CA_SHORT_DESCRIPTION"), false);
		String gsBabShortDescription = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("GS_BAB_SHORT_DESCRIPTION"), false);
		String gsWebDescrip = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("GS_WEB_DESCRIP"), false);
		String gsCaWebDescrip = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("GS_CA_WEB_DESCRIP"), false);
		String gsBabWebDescrip = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("GS_BAB_WEB_DESCRIP"), false);
		String gsPriceRangeDescrip = pResultSet
				.getString("GS_PRICE_RANGE_DESCRIP");

		String gsCaPriceRangeDescrip = pResultSet
				.getString("GS_CA_PRICE_RANGE_DESCRIP");
		String gsSkuLowPrice = pResultSet.getString("GS_SKU_LOW_PRICE");
		String gsCaSkuLowPrice = pResultSet.getString("GS_CA_SKU_LOW_PRICE");
		String gsSkuHightPrice = pResultSet.getString("GS_SKU_HIGH_PRICE");
		String gsCaSkuHightPrice = pResultSet.getString("GS_CA_SKU_HIGH_PRICE");

		// end of Stofu properties
		String showImagesCollection = pResultSet
				.getString("SHOW_IMAGES_COLLECTION");

		// Common Properties
		pItemVO.setWebOfferedFlag(webOfferedFlag);
		pItemVO.setBABWebOfferedFlag(babWebOfferedFlag);
		pItemVO.setCAWebOfferedFlag(caWebOfferedFlag);
		pItemVO.setDisableFlag(productDisableFlag);
		pItemVO.setBABDisableFlag(babDisableFlag);
		pItemVO.setCADisableFlag(caDisableFlag);
		pItemVO.setProductTitle(productTitle);
		pItemVO.setBABProductTitle(babProductTitle);
		pItemVO.setCAProductTitle(caProductTitle);
		pItemVO.setShortDescription(shortDescription);
		pItemVO.setBABShortDescription(babShortDescription);
		pItemVO.setCAShortDescription(caShortDescription);
		pItemVO.setWebDescrip(webDescrip);
		pItemVO.setBABWebDescrip(babWebDescrip);
		pItemVO.setCAWebDescrip(caWebDescrip);
		pItemVO.setDesignatedProductId(designatedProductId);
		//STOFU Properties
		pItemVO.setGSProductTitle(gsProductTitle);
		pItemVO.setGSBABProductTitle(gsBabProductTitle);
		pItemVO.setGSCAProductTitle(gsCaProductTitle);
		pItemVO.setGSShortDescription(gsShortDescription);
		pItemVO.setGSBABShortDescription(gsBabShortDescription);
		pItemVO.setGSCAShortDescription(gsCaShortDescription);
		pItemVO.setGSWebDescrip(gsWebDescrip);
		pItemVO.setGSBABWebDescrip(gsBabWebDescrip);
		pItemVO.setGSCAWebDescrip(gsCaWebDescrip);
		productVO.setGSPriceRangeDescrip(gsPriceRangeDescrip);
		productVO.setGSCAPriceRangeDescrip(gsCaPriceRangeDescrip);
		productVO.setGSSkuLowPrice(gsSkuLowPrice);
		productVO.setGSCASKULowPrice(gsCaSkuLowPrice);
		productVO.setGSSkuHightPrice(gsSkuHightPrice);
		productVO.setGSCASkuHightPrice(gsCaSkuHightPrice);
		productVO.setIntlRestricted(intlRestricted);
		//End of STOFU properties
		
		
		
		/*
		 * pItemVO.setGSWebOfferedFlag(gsWebOfferedFlag);
		 * pItemVO.setGSDisableFlag(gsDisableFlag);
		 */
		// Product Specific Properties
    
		productVO.setPriceRangeDescrip(priceRangeDescrip);
		// productVO.setBABPriceRangeDescrip(babPriceRangeDescrip);
		productVO.setCAPriceRangeDescrip(caPriceRangeDescrip);
		productVO.setSkuLowPrice(skuLowPrice);
		productVO.setCASkuLowPrice(caSkuLowPrice);
		productVO.setSkuHightPrice(skuHightPrice);
		productVO.setCASkuHightPrice(caSkuHightPrice);
		productVO.setShowImagesCollection(showImagesCollection);

	}
/**
 * 
 * @param pResultSet
 * @param pItemVO
 */
	
	//populating ItemVO to include stofu flags
	private void populateStofuFrequentProductProperties(ResultSet pResultSet, ItemVO pItemVO) {
	
		boolean gsBBBWebOfferedFlag = getBoolean("GS_BBB_WEB_OFFERED", pResultSet);
		boolean gsBABWebOfferedFlag = getBoolean("GS_BAB_WEB_OFFERED", pResultSet);
		boolean gsCAWebOfferedFlag = getBoolean("GS_CA_WEB_OFFERED", pResultSet);
		boolean gsBBBDisabledFlag = getBoolean("GS_BBB_DISABLED", pResultSet);
		boolean gsBABDisabledFlag = getBoolean("GS_BAB_DISABLED", pResultSet);
		boolean gsCADisabledFlag = getBoolean("GS_CA_DISABLED", pResultSet);
		
        pItemVO.setGSBBBWebOfferedFlag(gsBBBWebOfferedFlag);
	    pItemVO.setGSBABWebOfferedFlag(gsBABWebOfferedFlag);
		pItemVO.setGSCAWebOfferedFlag(gsCAWebOfferedFlag);
		pItemVO.setGSBBBDisabledFlag(gsBBBDisabledFlag);
		pItemVO.setGSBABDisabledFlag(gsBABDisabledFlag);
		pItemVO.setGSCADisabledFlag(gsCADisabledFlag);
				
	}
//end
	/**
	 * Populate rare product details
	 * 
	 * @param pResultSet
	 * @param pItemVO
	 * @throws SQLException
	 */
	private void populateProductRareVO(ResultSet pResultSet, ItemVO pItemVO)
			throws SQLException {

		ProductVO productVO = pItemVO.getProductVO();
    
		// Common
		String webOnlyFlag = pResultSet.getString("WEB_ONLY_FLAG");
		String caWebOnlyFlag = pResultSet.getString("CA_WEB_ONLY_FLAG");
		String babWebOnlyFlag = pResultSet.getString("BAB_WEB_ONLY_FLAG");
		String skuCollegeId = pResultSet.getString("SKU_COLLEGE_ID");
    String emailOutOfStockFlag = pResultSet.getString("EMAIL_OUT_OF_STOCK_FLAG");

		boolean collectionFlag = getBoolean("COLLECTION_FLAG", pResultSet);
		String swatchFlag = pResultSet.getString("SWATCH_FLAG");
		String brandId = pResultSet.getString("BRAND_ID");
		String shopGuideId = pResultSet.getString("SHOP_GUIDE_ID");
		boolean giftCertProduct = getBoolean("GIFT_CERT_FLAG", pResultSet);
		Date enableDate = pResultSet.getDate("ENABLE_DT");
		/** Added Site Specific Enable Date for R2.1 #53 PIM FEED*/
	    Date caEnableDate = pResultSet.getDate("CA_ENABLE_DT");
	    Date babEnableDate = pResultSet.getDate("BAB_ENABLE_DT");
		// String rollUpType = pResultSet.getString("EMAIL_OUT_OF_STOCK_FLAG");
	    
	    String vendorId = pResultSet.getString("VENDOR_ID");
	    
	  //Start ever living pdp properties, productkillflags
		boolean disableForeverPDPFlag = getBoolean("DISABLE_FOREVER_PDP_FLAG", pResultSet);
		boolean babDisableForeverPDPFlag = getBoolean("BAB_DISABLE_FOREVER_PDP_FLAG", pResultSet);
		boolean caDisableForeverPDPFlag = getBoolean("CA_DISABLE_FOREVER_PDP_FLAG", pResultSet);
		boolean gsDisableForeverPDPFlag = getBoolean("GS_DISABLE_FOREVER_PDP_FLAG", pResultSet);
		pItemVO.setDisableForeverPDPFlag(disableForeverPDPFlag);
		pItemVO.setBabDisableForeverPDPFlag(babDisableForeverPDPFlag);
		pItemVO.setCaDisableForeverPDPFlag(caDisableForeverPDPFlag);
		pItemVO.setGsDisableForeverPDPFlag(gsDisableForeverPDPFlag);
		//End ever living pdp properties, productkillflags
		
		
		//Stofu properties
		Date gsEnableDate = pResultSet.getDate("GS_ENABLE_DT");
		Date gsBABEnableDate = pResultSet.getDate("GS_BAB_ENABLE_DT");
		Date gsCAEnableDate = pResultSet.getDate("GS_CA_ENABLE_DT");
		
		pItemVO.setGSEnableDate(gsEnableDate);
		pItemVO.setGSBABEnableDate(gsBABEnableDate);
		pItemVO.setGSCAEnableDate(gsCAEnableDate);
		//End of stofu properties
		
		//LTL-34(PIM feed processing changes for Products)
		String ltlFlag = pResultSet.getString("LTL_FLAG");
		productVO.setLtlFlag(ltlFlag);
		
		// Common Properties
		pItemVO.setWebOnlyFlag(webOnlyFlag);
		pItemVO.setCAWebOnlyFlag(caWebOnlyFlag);
		pItemVO.setBabWebOnlyFlag(babWebOnlyFlag);
		pItemVO.setEmailOutOfStockFlag(emailOutOfStockFlag);
		pItemVO.setSkuCollegeId(skuCollegeId);
		pItemVO.setEnableDate(enableDate);
		/** Setting Site Specific Enable Date from ITEMVO for R2.1 #53 PIM FEED*/
	    pItemVO.setCAEnableDate(caEnableDate);
	    pItemVO.setBABEnableDate(babEnableDate);

		// Product Specific
		productVO.setCollectionFlag(collectionFlag);
		productVO.setSwatchFlag(swatchFlag);

		productVO.setBrandId(brandId);
		productVO.setShopGuideId(shopGuideId);
		productVO.setGiftCertProduct(giftCertProduct);
		productVO.setVendorId(vendorId);
		

	}

  private void populateSkuFrequentVO(ResultSet pResultSet, ItemVO pItemVO) throws SQLException {

		SkuVO skuVO = pItemVO.getSkuVO();
    String productTitle = BBBStringUtils.escapeHtmlString(pResultSet.getString("PRODUCT_TITLE"), false);

    String caProductTitle = BBBStringUtils.escapeHtmlString(pResultSet.getString("CA_PRODUCT_TITLE"), false);
    String babProductTitle = BBBStringUtils.escapeHtmlString(pResultSet.getString("BAB_PRODUCT_TITLE"), false);
    String shortDescription = BBBStringUtils.escapeHtmlString(pResultSet.getString("SHORT_DESCRIPTION"), false);
    String caShortDescription = BBBStringUtils.escapeHtmlString(pResultSet.getString("CA_SHORT_DESCRIPTION"), false);
    String babShortDescription = BBBStringUtils.escapeHtmlString(pResultSet.getString("BAB_SHORT_DESCRIPTION"), false);
    String webDescrip = BBBStringUtils.escapeHtmlString(pResultSet.getString("WEB_DESCRIP"), false);
    String caWebDescrip = BBBStringUtils.escapeHtmlString(pResultSet.getString("CA_WEB_DESCRIP"), false);
    String babWebDescrip = BBBStringUtils.escapeHtmlString(pResultSet.getString("BAB_WEB_DESCRIP"), false);
		//STOFU properties
		String gsProductTitle = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("GS_PRODUCT_TITLE"), false);

		String gsCaProductTitle = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("GS_CA_PRODUCT_TITLE"), false);
		String gsBabProductTitle = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("GS_BAB_PRODUCT_TITLE"), false);
		String gsShortDescription = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("GS_SHORT_DESCRIPTION"), false);
		String gsCaShortDescription = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("GS_CA_SHORT_DESCRIPTION"), false);
		String gsBabShortDescription = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("GS_BAB_SHORT_DESCRIPTION"), false);
		String gsWebDescrip = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("GS_WEB_DESCRIP"), false);
		String gsCaWebDescrip = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("GS_CA_WEB_DESCRIP"), false);
		String gsBabWebDescrip = BBBStringUtils.escapeHtmlString(
				pResultSet.getString("GS_BAB_WEB_DESCRIP"), false);
		
		//End of STOFU properties
		String webOfferedFlag = pResultSet.getString("WEB_OFFERED_FLAG");
		String babWebOfferedFlag = pResultSet.getString("BAB_WEB_OFFERED_FLAG");
		String caWebOfferedFlag = pResultSet.getString("CA_WEB_OFFERED_FLAG");
		String productDisableFlag = pResultSet.getString("DISABLE_FLAG");
		String babDisableFlag = pResultSet.getString("BAB_DISABLE_FLAG");
		String caDisableFlag = pResultSet.getString("CA_DISABLE_FLAG");
		String isBopus = pResultSet.getString("BOPUS_EXCLUSION_FLAG");
		
		populateStofuFrequentSkuProperties(pResultSet, pItemVO);

		String shippingSurcharge = pResultSet.getString("SHIPPING_SURCHARGE");
    String caShippingSurcharge = pResultSet.getString("CA_SHIPPING_SURCHARGE");
    String babShippingSurcharge = pResultSet.getString("BAB_SHIPPING_SURCHARGE");
    
    String skuIntlRestricted = pResultSet.getString("INTL_RESTRICTED");

		// Common Properties
		pItemVO.setWebOfferedFlag(webOfferedFlag);
		pItemVO.setBABWebOfferedFlag(babWebOfferedFlag);
		pItemVO.setCAWebOfferedFlag(caWebOfferedFlag);
		pItemVO.setDisableFlag(productDisableFlag);
		pItemVO.setBABDisableFlag(babDisableFlag);
		pItemVO.setCADisableFlag(caDisableFlag);
		pItemVO.setProductTitle(productTitle);
		pItemVO.setBABProductTitle(babProductTitle);
		pItemVO.setCAProductTitle(caProductTitle);
		pItemVO.setShortDescription(shortDescription);
		pItemVO.setBABShortDescription(babShortDescription);
		pItemVO.setCAShortDescription(caShortDescription);
		pItemVO.setWebDescrip(webDescrip);
		pItemVO.setBABWebDescrip(babWebDescrip);
		pItemVO.setCAWebDescrip(caWebDescrip);
		
		//STOFU properties
		pItemVO.setGSProductTitle(gsProductTitle);
		pItemVO.setGSBABProductTitle(gsBabProductTitle);
		pItemVO.setGSCAProductTitle(gsCaProductTitle);
		pItemVO.setGSShortDescription(gsShortDescription);
		pItemVO.setGSBABShortDescription(gsBabShortDescription);
		pItemVO.setGSCAShortDescription(gsCaShortDescription);
		pItemVO.setGSWebDescrip(gsWebDescrip);
		pItemVO.setGSBABWebDescrip(gsBabWebDescrip);
		pItemVO.setGSCAWebDescrip(gsCaWebDescrip);
		
		//END of STOFU properties
		/*
		 * pItemVO.setGSWebOfferedFlag(gsWebOfferedFlag);
		 * pItemVO.setGSDisableFlag(gsDisableFlag);
		 */
		
		
		skuVO.setBopus(isBopus);
		skuVO.setShippingSurcharge(shippingSurcharge);
		skuVO.setBABShippingSurcharge(babShippingSurcharge);
		skuVO.setCAShippingSurcharge(caShippingSurcharge);
		skuVO.setIntlRestricted(skuIntlRestricted);
		

	}
 //updating ItemVo to include stofu falgs
	private void populateStofuFrequentSkuProperties(ResultSet pResultSet,
			ItemVO pItemVO) {
		
				boolean gsBBBWebOfferedFlag = getBoolean("GS_BBB_WEB_OFFERED", pResultSet);
				boolean gsBABWebOfferedFlag = getBoolean("GS_BAB_WEB_OFFERED", pResultSet);
				boolean gsCAWebOfferedFlag = getBoolean("GS_CA_WEB_OFFERED", pResultSet);
				boolean gsBBBDisabledFlag = getBoolean("GS_BBB_DISABLED", pResultSet);
				boolean gsBABDisabledFlag = getBoolean("GS_BAB_DISABLED", pResultSet);
				boolean gsCADisabledFlag = getBoolean("GS_CA_DISABLED", pResultSet);
				
				
				pItemVO.setGSBBBWebOfferedFlag(gsBBBWebOfferedFlag);
				pItemVO.setGSBABWebOfferedFlag(gsBABWebOfferedFlag);
				pItemVO.setGSCAWebOfferedFlag(gsCAWebOfferedFlag);
				pItemVO.setGSBBBDisabledFlag(gsBBBDisabledFlag);
				pItemVO.setGSBABDisabledFlag(gsBABDisabledFlag);
				pItemVO.setGSCADisabledFlag(gsCADisabledFlag);
			}
	
	//end

  private void populateSkuRareVO(ResultSet pResultSet, ItemVO pItemVO) throws SQLException {

		SkuVO skuVO = pItemVO.getSkuVO();

		// Common
		String webOnlyFlag = pResultSet.getString("WEB_ONLY_FLAG");
		String caWebOnlyFlag = pResultSet.getString("CA_WEB_ONLY_FLAG");
		String babWebOnlyFlag = pResultSet.getString("BAB_WEB_ONLY_FLAG");

		String jdaDeptId = pResultSet.getString("JDA_DEPT_ID");
		String jdaSubDeptid = pResultSet.getString("JDA_SUB_DEPT_ID");
		String jdaClass = pResultSet.getString("JDA_CLASS_ID");

		String giftCertFlag = pResultSet.getString("GIFT_CERT_FLAG");
		String skuCollegeId = pResultSet.getString("SKU_COLLEGE_ID");
		String skuTypeCd = pResultSet.getString("SKU_TYPE_CD");
    String emailOutOfStockFlag = pResultSet.getString("EMAIL_OUT_OF_STOCK_FLAG");

		String color = pResultSet.getString("COLOR");
		String colorgroup = pResultSet.getString("COLOR_GROUP");
    String size = StringUtils.escapeHtmlString(pResultSet.getString("SIZE_DESC"), false);
		if (BBBUtility.isNotEmpty(size)) {
			size = size.replace("&apos;", "&#39;");
		}
		String giftWrapEligible = pResultSet.getString("GIFT_WRAP_ELIGIBLE");
		String vdcSkuType = pResultSet.getString("VDC_SKU_TYPE");
		String vdcSkuMessage = pResultSet.getString("VDC_SKU_MESSAGE");
		String upc = pResultSet.getString("UPC");
    String ecomFulfilmentFlag = pResultSet.getString("CA_ECOM_FULFILMENT_FLAG");
		String overWeightFlag = pResultSet.getString("OVERWEIGHT_FLAG");
		String overSizeFlag = pResultSet.getString("OVERSIZE_FLAG");
		String bbbForceBelowLine = pResultSet.getString("BBB_FORCE_BELOW_LINE");
		String babForceBelowLine = pResultSet.getString("BAB_FORCE_BELOW_LINE");
		String caForceBelowLine = pResultSet.getString("CA_FORCE_BELOW_LINE");

		String prop65Lighting = pResultSet.getString("PROP65_LIGHTING");
		String prop65Crystal = pResultSet.getString("PROP65_CRYSTAL");
		String prop65DinnerWare = pResultSet.getString("PROP65_DINNERWARE");
		String prop65Other = pResultSet.getString("PROP65_OTHER");

		String cost = pResultSet.getString("COST");
		String caCost = pResultSet.getString("CA_COST");
		String taxCd = pResultSet.getString("TAX_CD");
		String vendorId = pResultSet.getString("VENDOR_ID");

		//LTL-33(PIM feed processing changes for SKUs)
		String assemblyTime = pResultSet.getString("ASSEMBLY_TIME");
		String isAssemblyOffered = pResultSet.getString("IS_ASSEMBLY_OFFERED");
		String ltlFlag = pResultSet.getString("LTL_FLAG");
		String orderToShipSla = pResultSet.getString("ORDER_TO_SHIP_SLA");
		String caseWeight = pResultSet.getString("CASE_WEIGHT");

		String eligibleShipMethods = pResultSet
				.getString("ELIGIBLE_SHIP_METHODS");
		String freeShippingMethods = pResultSet
				.getString("FREE_SHIPPING_METHODS");
		String caFreeShippingMethods = pResultSet
				.getString("CA_FREE_SHIPPING_METHODS");
		String babFreeShippingMethods = pResultSet
				.getString("BAB_FREE_SHIPPING_METHODS");
		String nonShippableStates = pResultSet
				.getString("NON_SHIPPABLE_STATES");
		Date enableDate = pResultSet.getDate("ENABLE_DT");
		
		//EXIM or Katori Properties
		String customizationOfferedFlag = pResultSet
						.getString(BBBCatalogImportConstant.CL_CUSTOMIZATION_OFFERED_FLAG);
		String babCustomizationOfferedFlag = pResultSet
						.getString(BBBCatalogImportConstant.CL_BAB_CUSTOMIZATION_OFFERED_FLAG);
		String caCustomizationOfferedFlag = pResultSet
						.getString(BBBCatalogImportConstant.CL_CA_CUSTOMIZATION_OFFERED_FLAG);
		String gsCustomizationOfferedFlag = pResultSet
						.getString(BBBCatalogImportConstant.CL_GS_CUSTOMIZATION_OFFERED_FLAG);
		String personalizationType = pResultSet
						.getString(BBBCatalogImportConstant.CL_PERSONALIZATION_TYPE);
		String eligibleCustomizationCodes = pResultSet
						.getString(BBBCatalogImportConstant.CL_ELIGIBLE_CUSTOMIZATION_CODES);
		String minShippingDays = pResultSet.getString(BBBCatalogImportConstant.CL_MIN_SHIPPING_DAYS);
		String maxShippingDays = pResultSet.getString(BBBCatalogImportConstant.CL_MAX_SHIPPING_DAYS);
		String shippingCutoffOffset = pResultSet
						.getString(BBBCatalogImportConstant.CL_SHIPPING_CUTOFF_OFFSET);	
		
		//Stofu properties
		Date gsEnableDate = pResultSet.getDate("GS_ENABLE_DT");
		Date gsBABEnableDate = pResultSet.getDate("GS_BAB_ENABLE_DT");
		Date gsCAEnableDate = pResultSet.getDate("GS_CA_ENABLE_DT");
		
		pItemVO.setGSEnableDate(gsEnableDate);
		pItemVO.setGSBABEnableDate(gsBABEnableDate);
		pItemVO.setGSCAEnableDate(gsCAEnableDate);
		//End of stofu properties

		// added skuImages here

		// Common Properties
		pItemVO.setWebOnlyFlag(webOnlyFlag);
		pItemVO.setCAWebOnlyFlag(caWebOnlyFlag);
		pItemVO.setBabWebOnlyFlag(babWebOnlyFlag);
		pItemVO.setEmailOutOfStockFlag(emailOutOfStockFlag);
		pItemVO.setSkuCollegeId(skuCollegeId);
		pItemVO.setEnableDate(enableDate);

		skuVO.setColor(color);
		skuVO.setColorGroup(colorgroup);
		skuVO.setCost(cost);
		skuVO.setCaCost(caCost);
		skuVO.setSize(size);

    skuVO.setTaxCd(taxCd);
    skuVO.setGiftCertfFag(giftCertFlag);
    skuVO.setSkuTypeCd(skuTypeCd);
    skuVO.setGiftWrapEligible(giftWrapEligible);
    skuVO.setUpc(upc);
    skuVO.setEligibleShipMethods(eligibleShipMethods);
    skuVO.setFreeShippingMethods(freeShippingMethods);
    skuVO.setCaFreeShippingMethods(caFreeShippingMethods);
    skuVO.setBabFreeShippingMethods(babFreeShippingMethods);
    skuVO.setNonShippableStates(nonShippableStates);
    skuVO.setVDCSkuType(vdcSkuType);
    skuVO.setVDCSkuMessage(vdcSkuMessage);

    skuVO.setJdaDeptId(jdaDeptId);
    skuVO.setJdaSubDeptid(jdaSubDeptid);
    skuVO.setJdaClass(jdaClass);
    skuVO.setCAEcomFulfilmentFlag(ecomFulfilmentFlag);
    skuVO.setOverWeightFlag(overWeightFlag);
    skuVO.setOverSizeFlag(overSizeFlag);
    skuVO.setForceBelowLine(bbbForceBelowLine);
    skuVO.setBabForceBelowLine(babForceBelowLine);
    skuVO.setCaForceBelowLine(caForceBelowLine);
    skuVO.setProp65Crystal(prop65Crystal);
    skuVO.setProp65Lighting(prop65Lighting);
    skuVO.setProp65DinnerWare(prop65DinnerWare);
    skuVO.setProp65Other(prop65Other);
    skuVO.setVendorId(vendorId);
    
    skuVO.setAssemblyTime(assemblyTime);
	skuVO.setAssemblyOffered(isAssemblyOffered);
	skuVO.setLtlFlag(ltlFlag);
	skuVO.setOrderToShipSla(orderToShipSla);
	skuVO.setCaseWeight(caseWeight);
	
    if(isLoggingDebug()){
		logDebug("check for cost " + skuVO.getCaCost()+ skuVO.getCost());
	}
			
    
    
	//Katori Properties
    
    skuVO.setCustomizationOfferedFlag(customizationOfferedFlag);
    skuVO.setBabCustomizationOfferedFlag(babCustomizationOfferedFlag);
    skuVO.setCaCustomizationOfferedFlag(caCustomizationOfferedFlag);
    skuVO.setGsCustomizationOfferedFlag(gsCustomizationOfferedFlag);
    skuVO.setPersonalizationType(personalizationType);
    skuVO.setEligibleCustomizationCodes(eligibleCustomizationCodes);
    skuVO.setMinShippingDays(minShippingDays);
    skuVO.setMaxShippingDays(maxShippingDays);
    skuVO.setShippingCutoffOffset(shippingCutoffOffset);
    
    //  PIM Feed Import BBBP-6233 Open 
    String chainSkuStatusCd, webSkuStatusCd, caChainSkuStatusCd, caWebSkuStatusCd;
    chainSkuStatusCd = pResultSet.getString(CHAIN_SKU_STATUS_CD);
    webSkuStatusCd = pResultSet.getString(WEB_SKU_STATUS_CD);
    caChainSkuStatusCd = pResultSet.getString(CA_CHAIN_SKU_STATUS_CD);
    caWebSkuStatusCd = pResultSet.getString(CA_WEB_SKU_STATUS_CD);
    
    if (StringUtils.isNotBlank(chainSkuStatusCd)) {
    	skuVO.setChainSkuStatusCd(chainSkuStatusCd.charAt(0));
    }
    if (StringUtils.isNotBlank(webSkuStatusCd)) {
    	skuVO.setWebSkuStatusCd(webSkuStatusCd.charAt(0));
    }
    if (StringUtils.isNotBlank(caChainSkuStatusCd)) {
    	skuVO.setCaChainSkuStatusCd(caChainSkuStatusCd.charAt(0));
    }
    if (StringUtils.isNotBlank(caWebSkuStatusCd)) {
    	skuVO.setCaWebSkuStatusCd(caWebSkuStatusCd.charAt(0));
    }
	
  }

	/**
	 * Populate Product Keywords
	 * 
	 * @param pFeedId
	 * @param pItemId
	 * @param itemVO
	 * @param pConnection
	 * @throws BBBSystemException
	 */
  private void populateProductKeywords(final String pFeedId, final String pItemId, final ItemVO itemVO,
			final Connection pConnection) throws BBBSystemException {
		String keywords = null;
		boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, POPULATE_PRODUCT_KEYWORDS);
		try {
      keywords = getPIMKeywordsDetails(SELECT_PROD_KEYWORDS_DETAILS, pFeedId, pItemId, "KEYWORD_STRING", pConnection);
		} catch (BBBBusinessException e) {
			if (isLoggingError()) {

        logError("ProductId=" + pItemId + " has does not have keywords, return null");
				logError(BBBStringUtils.stack2string(e));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(e));
			}
			BBBPerformanceMonitor.cancel(POPULATE_PRODUCT_KEYWORDS);
			isMonitorCanceled = true;

		} finally {
			if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, POPULATE_PRODUCT_KEYWORDS);
			}
		}
		itemVO.getProductVO().setKeywords(keywords);
	}

	/**
	 * Populate Category Keywords
	 * 
	 * @param pFeedId
	 * @param pCategoryId
	 * @param pCategoryVO
	 * @param pConnection
	 * @throws BBBSystemException
	 */
  private void populateCategoryKeywords(final String pFeedId, final String pCategoryId, final CategoryVO pCategoryVO,
			final Connection pConnection) throws BBBSystemException {
		String keywords = null;
		boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, POPULATE_CATEGORY_KEYWORDS);
		try {
      keywords = getPIMKeywordsDetails(SELECT_CAT_KEYWORDS_DETAILS, pFeedId, pCategoryId, "KEYWORD_STRING", pConnection);
		} catch (BBBBusinessException e) {
			if (isLoggingError()) {

        logError("pCategoryId=" + pCategoryId + " has does not have keywords, return null");
				logError(BBBStringUtils.stack2string(e));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(e));
			}
			BBBPerformanceMonitor.cancel(POPULATE_CATEGORY_KEYWORDS);
			isMonitorCanceled = true;
		} finally {
			if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, POPULATE_CATEGORY_KEYWORDS);
			}

		}
		if (isLoggingDebug()) {
			logDebug("populate keyword for pCategoryId=" + pCategoryId);
			logDebug("keywords=" + keywords);
		}
		pCategoryVO.setKeywords(keywords);

	}

  private ProductTabVO populateProductTabsVO(final ResultSet pResultSet) throws SQLException {

		ProductTabVO productTabVO = new ProductTabVO();
		if (pResultSet == null || !pResultSet.next()) {

			return null;
		}

		String tabName = pResultSet.getString("TAB_NAME");
		String tabContent = pResultSet.getString("TAB_CONTENT");
		int tabSequence = pResultSet.getInt("TAB_SEQUENCE");
		String siteId = pResultSet.getString("SITE_FLAG");

		productTabVO.setTabName(tabName);
		productTabVO.setTabContent(tabContent);
		productTabVO.setTabSequence(tabSequence);
		productTabVO.setSiteId(siteId);
		return productTabVO;
	}

  /**
   * This method populate all the brand details from provided ResultSet
   * 
   * @param pResultSet
   * @return
   * @throws SQLException
   */
  private BrandVO populateBrandDetails(final ResultSet pResultSet) throws SQLException {
    BrandVO brandVO = new BrandVO();

    if (pResultSet == null || !pResultSet.next()) {

      return null;
    }

    brandVO.setName(BBBStringUtils.escapeHtmlString(pResultSet.getString("BRAND_NAME"), false));
    brandVO.setDescrip(BBBStringUtils.escapeHtmlString(pResultSet.getString("BRAND_NAME"), false));
    brandVO.setBrandImage(pResultSet.getString("BRAND_LOGO"));
    brandVO.setDisplayFlag(getBoolean("DISPLAY_FLAG", pResultSet));
    brandVO.setSiteId(pResultSet.getString("SITE_FLAG"));

    return brandVO;
  }
/**
   * This method populate all the brand details from provided ResultSet
   * 
   * @param pResultSet
   * @return
   * @throws SQLException
   */
  private ZipCodeVO populateZipCodeRegionDetails(final ResultSet pResultSet) throws SQLException {
	  ZipCodeVO regionVO = new ZipCodeVO();

    if (pResultSet == null || !pResultSet.next()) {

      return null;
    }
    regionVO.setRegionId(pResultSet.getString("REGION_ID"));
    regionVO.setRegionName(pResultSet.getString("REGION_NAME"));
    regionVO.setZipCode(pResultSet.getString("ZIP_CODES"));
    regionVO.setOperationFlag(pResultSet.getString("OPERATION_FLAG"));
    
    return regionVO;
  }

  /**
   * This method populate all the rebate details from provided ResultSet
   * 
   * @param pResultSet
   * @return
   * @throws SQLException
   */
  private RebateVO populateRebateDetails(final ResultSet pResultSet) throws SQLException {
    RebateVO rebateVO = new RebateVO();

    if (pResultSet == null || !pResultSet.next()) {

      return null;
    }

    rebateVO.setDescrip(BBBStringUtils.escapeHtmlString(pResultSet.getString("DESCRIPTION"), false));
    rebateVO.setStartDate(pResultSet.getTimestamp("START_DT"));
    rebateVO.setEndDate(pResultSet.getTimestamp("END_DT"));

    rebateVO.setRebateUrl(pResultSet.getString("REBATE_URL"));
    rebateVO.setSiteId(pResultSet.getString("SITE_FLAG"));

    return rebateVO;
  }

  /**
   * 
   * @param pResultSet
   * @return
   * @throws SQLException
   */
  private MediaVO populateMedia(ResultSet pResultSet) throws SQLException {

    MediaVO mediaVO = new MediaVO();

    if (pResultSet == null || !pResultSet.next()) {

      return null;
    }

    mediaVO.setMediaType(pResultSet.getString("MEDIA_TYPE"));
    mediaVO.setProvider(pResultSet.getString("PROVIDER_ID"));
    mediaVO.setMediaSource(pResultSet.getString("MEDIA_SOURCE"));
    mediaVO.setMediaDescription(BBBStringUtils.escapeHtmlString(pResultSet.getString("MEDIA_DESCRIP"), false));
    mediaVO.setComments(pResultSet.getString("COMMENTS"));
    mediaVO.setMediaTranscript(BBBStringUtils.escapeHtmlString(pResultSet.getString("MEDIA_TRANSCRIPT")));
    mediaVO.setStartDate(pResultSet.getTimestamp("START_DATE"));
    mediaVO.setEndDate(pResultSet.getTimestamp("END_DATE"));
    mediaVO.setWidgetId(pResultSet.getString("WIDGET_ID"));
    mediaVO.setSiteIds(pResultSet.getString("SITE_FLAG"));
    return mediaVO;
  }

  /**
   * Populate AttributeVO data from ECP_ITEMS_ATTRIBUTES
   * 
   * @param pResultSet
   * @return
   * @throws SQLException
   */
  private AttributeVO populateItemAttributes(ResultSet pResultSet) throws SQLException {

		AttributeVO attributeVO = new AttributeVO();

		if (pResultSet == null || !pResultSet.next()) {

			return null;
		}

    attributeVO.setDescription(BBBStringUtils.escapeHtmlString(pResultSet.getString("DESCRIP"), false));
		attributeVO.setStartDT(pResultSet.getTimestamp("START_DT"));
		attributeVO.setEndDT(pResultSet.getTimestamp("END_DT"));
		attributeVO.setActionURL(pResultSet.getString("ACTION_URL"));
		attributeVO.setImageURL(pResultSet.getString("IMAGE_URL"));
		attributeVO.setPlaceHolder(pResultSet.getString("PLACE_HOLDER"));
		attributeVO.setPriority(pResultSet.getString("PRIORITY"));
		attributeVO.setSiteFlag(pResultSet.getString("SITE_FLAG"));
		attributeVO.setIntlFlag(pResultSet.getString("INTL_FLAG"));
		return attributeVO;
	}

	/**
	 * 
	 * @param pResultSet
	 * @return
	 * @throws SQLException
	 */
  private ItemMediaVO populateItemMedia(ResultSet pResultSet) throws SQLException {
		ItemMediaVO itemMediaVO = new ItemMediaVO();

		if (pResultSet == null || !pResultSet.next()) {

			return null;
		}

		itemMediaVO.setMediaId(pResultSet.getString("MEDIA_ID"));
		itemMediaVO.setSequenceNum(pResultSet.getString("SEQUENCE"));
		itemMediaVO.setStartDate(pResultSet.getTimestamp("START_DATE"));
		itemMediaVO.setEndDate(pResultSet.getTimestamp("END_DATE"));
		itemMediaVO.setOperationFlag(pResultSet.getString("OPERATION_FLAG"));
		itemMediaVO.setSiteFlag(pResultSet.getString("SITE_FLAG"));
		itemMediaVO.setWidget(pResultSet.getString("WIDGET_ID"));
		itemMediaVO.setComments(pResultSet.getString("COMMENTS"));
		return itemMediaVO;
	}

	/**
	 * Populate Sku Attributes
	 * 
	 * @param pResultSet
	 * @return
	 * @throws SQLException
	 */
  private SkuDisplayAttributesVO populateSkuAttribute(ResultSet pResultSet) throws SQLException {
		SkuDisplayAttributesVO skuAttributeVO = new SkuDisplayAttributesVO();

		if (pResultSet == null) {

			return null;
		}

    skuAttributeVO.setItemAttributeId(pResultSet.getString("ITEM_ATTRIBUTE_ID"));
		skuAttributeVO.setValueId(pResultSet.getString("ITEM_VALUE_ID"));
		skuAttributeVO.setSkuId(pResultSet.getString("SKU_ID"));
		skuAttributeVO.setStartDT(pResultSet.getTimestamp("START_DT"));
		skuAttributeVO.setEndDT(pResultSet.getTimestamp("END_DT"));
		skuAttributeVO.setOperationFlag(pResultSet.getString("OPERATION_FLAG"));
		skuAttributeVO.setSiteFlag(pResultSet.getString("SITE_FLAG"));
		skuAttributeVO.setMiscInfo(pResultSet.getString("MISC_INFO"));

		return skuAttributeVO;
	}

	/**
	 * Populate Sku Pricing properties
	 * 
	 * @param pResultSet
	 * @return
	 * @throws SQLException
	 */
  private SkuPricingVO populateSkuPricing(ResultSet pResultSet) throws SQLException {

		SkuPricingVO skuPricingVO = new SkuPricingVO();

		if (pResultSet == null || !pResultSet.next()) {

			return null;
		}

		skuPricingVO.setWasPrice(pResultSet.getString("WAS_PRICE"));
		skuPricingVO.setCAWasPrice(pResultSet.getString("CA_WAS_PRICE"));
        skuPricingVO.setJDARetailPrice(pResultSet.getString("JDA_RETAIL_PRICE"));
        skuPricingVO.setCAJDARetailPrice(pResultSet.getString("CA_JDA_RETAIL_PRICE"));
        skuPricingVO.setMXWasPrice(pResultSet.getString("MX_WAS_PRICE"));
        skuPricingVO.setMXJDARetailPrice(pResultSet.getString("MX_JDA_RETAIL_PRICE"));
        skuPricingVO.setInCartPrice(pResultSet.getString("IN_CART_PRICE"));
        skuPricingVO.setInCartPriceCA(pResultSet.getString("IN_CART_PRICE_CA"));
        skuPricingVO.setInCartPriceMX(pResultSet.getString("IN_CART_PRICE_MX"));
       
		return skuPricingVO;
	}

	/**
	 * If ResultSet parameter value is Y return boolean true else return boolean
	 * false
	 * 
	 * @param pString
	 * @param pResultSet
	 * @return
	 */
	private boolean getBoolean(String pString, ResultSet pResultSet) {

		boolean flag = false;
		try {

			String data = pResultSet.getString(pString);
			if (data != null && data.trim().equals(BOOLEAN_FLAG)) {
				flag = true;
			} else {
				flag = false;
			}

		} catch (SQLException sqlException) {

			if (isLoggingError()) {

        logError(pString + " has following issue, for now set boolean as false");
				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}
		}
		return flag;
	}

	
	/**
   * This method fetches all the products for provided pCategoryId, pFeedId and
   * pSiteId
	 * 
	 * @param pFeedId
	 * @param pSiteId
	 * @param pCategoryId
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
  public List<OperationVO> getChildProducts(final String pFeedId, final String pSiteId, final String pCategoryId,
      final Connection pConnection) throws BBBSystemException, BBBBusinessException {

		if (isLoggingDebug()) {

			logDebug("Inside method getChildProducts for pCategoryId=" + pCategoryId);
		}
		String querySite = SELECT_CHILD_PRODUCTS;

		if (isLoggingDebug()) {

			logDebug(querySite);
		}

    List<OperationVO> categoryIdsVOList = getIdVOList(pConnection, querySite, pFeedId, pCategoryId, SKU_ID, SITE_FLAG,
				"OPERATION_FLAG", IS_PRIMARY);
		if (isLoggingDebug()) {

			logDebug("Latest feedId" + pFeedId + " CategoryId=" + pCategoryId);
		}
		return categoryIdsVOList;

	}

	/**
	 * This method fetches the list of categories for given pFeedId and pSiteId
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
  public List<OperationVO> getUniqueRootCategories(final String pFeedId, final String pSiteId,
      final Connection pConnection) throws BBBSystemException, BBBBusinessException {

		// String querySite = SELECT_UNIQUE_CATEGORIES_PRODUCTS;
		String querySite = "Select UNIQUE(NODE_ID),OPERATION_FLAG from ECP_NODES where FEED_ID = ?  and ROOT_NODE_FLAG = 'Y' and SITE_FLAG LIKE '%"
				+ pSiteId + "%'";
		if (isLoggingDebug()) {

			logDebug(querySite);
		}

    List<OperationVO> rootCategoryIdList = getIdVOList(pConnection, querySite, pFeedId, null, NODE_ID, null,
        "OPERATION_FLAG");
		if (isLoggingDebug()) {

      logDebug("feedId= " + pFeedId + " Root Category Ids=" + rootCategoryIdList);
		}
		return rootCategoryIdList;

	}

	/**
	 * This method fetches the list of categories for given pFeedId and pSiteId
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @return
	 * @throws BBBSystemException
	 */
  public List<String> getUniqueCategoriesForProducts(final String pFeedId, final String pSiteId,
      final Connection pConnection) throws BBBSystemException {

		// String querySite = SELECT_UNIQUE_CATEGORIES_PRODUCTS;
    String querySite = "Select UNIQUE(NODE_ID) from ECP_NODE_SKU where FEED_ID = ? and SITE_FLAG LIKE '%" + pSiteId
        + "%'";

		if (isLoggingDebug()) {

			logDebug(querySite);
		}

    List<String> categoryIdList = getIdList(pConnection, querySite, pFeedId, null, NODE_ID);
		if (isLoggingDebug()) {

			logDebug("feedId= " + pFeedId + " categoryIds=" + categoryIdList);
		}
		return categoryIdList;

	}

	/**
	 * Convert String in integer false
	 * 
	 * @param pString
	 * @param pResultSet
	 * @return
	 */
	/*
	 * private int getInt(String pString, ResultSet pResultSet) {
	 * 
	 * int value = 0;
	 * 
	 * try {
	 * 
	 * String data = pResultSet.getString(pString); if
	 * (!StringUtils.isEmpty(data)) { value = Integer.parseInt(data); }
	 * 
	 * } catch (SQLException sqlException) {
	 * 
	 * if (isLoggingError()) {
	 * 
	 * logError(pString + " has following issue, for now set 0 as default");
	 * logError(sqlException.getMessage()); } if (isLoggingDebug()) {
	 * 
	 * logDebug(BBBStringUtils.stack2string(sqlException)); } } catch
	 * (NumberFormatException nex) { if (isLoggingError()) {
	 * 
	 * logError(pString + " has following issue, for now set 0 as default");
	 * logError(nex.getMessage()); } if (isLoggingDebug()) {
	 * 
	 * logDebug(BBBStringUtils.stack2string(nex)); } } return value; }
	 */

	/**
	 * This method fetch all the unique products ids from the staging data base
	 * and returns unique product List
	 * 
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pBrandId
	 * @param pConnection
	 * @return BrandVO
	 */
  public List<String> getUniqueProdSkuRelIds(final String pFeedId, final Connection pConnection)
      throws BBBSystemException {

    return getIdList(pConnection, SELECT_UNIQUE_PROD_SKU_REL_IDS, pFeedId, null, "SKU_ID");
	}

	/**
	 * This method fetches the sku item
	 * 
	 * 
	 * @param pFeedId
	 * @param pId
	 * @param pConnection
	 * @throws BBBBusinessException
	 */
  public List<OperationVO> getSkuItem(final String pFeedId, final String pId, final Connection pConnection)
      throws BBBSystemException, BBBBusinessException {

    return getIdVOList(pConnection, SELECT_SKU_PROD_REL, pFeedId, pId, CHILD_SKU_ID, "RELATED_TYPE_CD",
        "OPERATION_FLAG", "SEQUENCE_NUM");

	}

	/**
	 * This method fetches all the product properties
	 * 
	 * @param pFeedId
	 * @param pSkuId
	 * @param pChildSkuId
	 * @throws BBBBusinessException
	 */
  public ProdSkuVO getPIMProdSkuDetail(String pFeedId, final String pSkuId, final String pChildSkuId,
      Connection pConnection) throws BBBSystemException, BBBBusinessException {
		boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, GET_PIM_PROD_SKU_DETAIL);
		String query = SELECT_SKU_PROD_DETAILS;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {

      prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
      resultSet = getResultSet(pConnection, prepareStatement, pFeedId, pSkuId, pChildSkuId);
			return populateItem(resultSet);

		} catch (SQLException sqlException) {
			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}

			BBBPerformanceMonitor.cancel(GET_PIM_PROD_SKU_DETAIL);
			isMonitorCanceled = true;
      throw new BBBBusinessException(BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,sqlException.getMessage(), sqlException);
		} finally {

			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
			if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, GET_PIM_PROD_SKU_DETAIL);
			}

		}
	}

	/**
	 * This method populates the resultset
	 * 
	 * @param pResultSet
	 * @return
	 * @throws SQLException
	 */
	private ProdSkuVO populateItem(ResultSet pResultSet) throws SQLException {
		ProdSkuVO itemSkuVO = new ProdSkuVO();

		if (pResultSet == null || !pResultSet.next()) {

			return null;
		}

		itemSkuVO.setChildSkuId(pResultSet.getString(CHILD_SKU_ID));
		itemSkuVO.setRollupType(pResultSet.getString(ROLLUP_TYPE_CODE));
		itemSkuVO.setLikeUnlikeFlag(getBoolean(LIKE_UNLIKE_FLAG, pResultSet));
		itemSkuVO.setOperationFlag(pResultSet.getString("OPERATION_FLAG"));

		return itemSkuVO;
	}

	public void updateBadRecords(final String pFeedId,
			final String pBadRecordId, final String pTableName,
			final String pRecordData, final Date pDate,
			final String pLogMessage, Connection pConnection)
			throws BBBSystemException {

		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;

		// Concatenating mLogTableStatusPrefix to logMessage string to identify which scheduler has inserted  the row
		// Either it was done by the direct staging scheduler or the regular staging scheduler
		String logMessage = pLogMessage;
		if(mLogTableStatusPrefix!=null && !mLogTableStatusPrefix.equals("")){
		   logMessage = mLogTableStatusPrefix + logMessage;
		}
		
		boolean isMonitorCanceled = false;
		BBBPerformanceMonitor
				.start(IMPORT_PROCESS_PIMTOOLS, UPDATE_BAD_RECORDS);

		String pQuery = "INSERT INTO ECP_FEED_BAD_RECORDS(FEED_ID, BAD_RECORD_ID, TABLE_NAME, RECORD_DATA,\"DATE\", LOG_MESSAGE) VALUES (?,?,?,?,?,?)";

		if (!StringUtils.isEmpty(pQuery)) {

			try {

				prepareStatement = getConnection(pConnection).prepareStatement(
						pQuery, ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				prepareStatement.setString(1, pFeedId);
				prepareStatement.setString(2, pBadRecordId);
				prepareStatement.setString(3, pTableName);
				prepareStatement.setString(4, pRecordData);
				prepareStatement.setDate(5, pDate);
				prepareStatement.setString(6, logMessage);

				prepareStatement.executeUpdate();

			} catch (SQLException e) {
				if (isLoggingError()) {

					logError(BBBStringUtils.stack2string(e));
				}
				BBBPerformanceMonitor.cancel(UPDATE_BAD_RECORDS);
				isMonitorCanceled = true;

			} finally {

				closeResultSet(resultSet);
				closePreparedStatement(prepareStatement);
				if (!isMonitorCanceled) {
					BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS,
							UPDATE_BAD_RECORDS);
				}
			}

		}

	}

// Start :- Added for Pricing Feed T02
	
	/**
	 * @param pFeedId
	 * @param pBadRecordId
	 * @param pTableName
	 * @param pRecordData
	 * @param pDate
	 * @param pLogMessage
	 * @param pConnection
	 * @throws BBBSystemException
	 */
	public void updatePricingBadRecords(final String pFeedId,
			final String pBadRecordId, final String pTableName,
			final String pRecordData, final Date pDate,
			final String pLogMessage, Connection pConnection)
			throws BBBSystemException {

		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;

		boolean isMonitorCanceled = false;
		BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, UPDATE_BAD_RECORDS);

		String pQuery = "INSERT INTO ECP_PRICING_FEED_BAD_RECORDS (FEED_ID, BAD_RECORD_ID, TABLE_NAME, RECORD_DATA,\"DATE\", LOG_MESSAGE) VALUES (?,?,?,?,?,?)";

		if (!StringUtils.isEmpty(pQuery)) {

			try {
				prepareStatement = getConnection(pConnection).prepareStatement(pQuery, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				prepareStatement.setString(1, pFeedId);
				prepareStatement.setString(2, pBadRecordId);
				prepareStatement.setString(3, pTableName);
				prepareStatement.setString(4, pRecordData);
				prepareStatement.setDate(5, pDate);
				prepareStatement.setString(6, pLogMessage);

				prepareStatement.executeUpdate();

			} catch (SQLException e) {
				if (isLoggingError()) {
					logError(BBBStringUtils.stack2string(e));
				}
				BBBPerformanceMonitor.cancel(UPDATE_BAD_RECORDS);
				isMonitorCanceled = true;

			} finally {
				closeResultSet(resultSet);
				closePreparedStatement(prepareStatement);
				if (!isMonitorCanceled) {
					BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, UPDATE_BAD_RECORDS);
				}
			}

		}

	}
	
// End :- Added for Pricing Feed T02
	
	/**
	 * This method fetch all the unique items ids from the staging data base and
	 * returns media ids List
	 * 
	 * 
	 * @param pConnection
	 */
  public List<String> getUniqueDeptIds(final String pFeedId, final Connection pConnection) throws BBBSystemException {

    return getIdList(pConnection, SELECT_ALL_DEPT, pFeedId, null, "JDA_DEPT_ID");
	}

	/**
	 * 
	 * @param pDeptId
	 * @param pConnection
	 * @return
	 */
  public MiscVO getPIMDeptDetail(String pFeedId, String pSubDeptId, Connection pConnection) throws BBBSystemException,
			BBBBusinessException {
		boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, GET_PIM_DEPT_DETAIL);
		String query = SELECT_DEPT_DETAILS;

		if (isLoggingDebug()) {

			logDebug("query=" + query);
		}
		PreparedStatement prepareStatement = null;

		ResultSet resultSet = null;
		try {

      prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
      resultSet = getResultSet(pConnection, prepareStatement, pSubDeptId, pFeedId, null);

			return populateDept(resultSet);

		} catch (SQLException sqlException) {

			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}
			BBBPerformanceMonitor.cancel(GET_PIM_DEPT_DETAIL);
			isMonitorCanceled = true;

      throw new BBBBusinessException(BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,sqlException.getMessage(), sqlException);
		} finally {

			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
			if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, GET_PIM_DEPT_DETAIL);
			}
		}
	}

	/**
	 * 
	 * @param pResultSet
	 * @return
	 * @throws SQLException
	 */
	private MiscVO populateDept(ResultSet pResultSet) throws SQLException {

		MiscVO deptVO = new MiscVO();

		if (pResultSet == null || !pResultSet.next()) {

			return null;
		}

    deptVO.setDescrip(BBBStringUtils.escapeHtmlString(pResultSet.getString("DESCRIP"), false));

		return deptVO;
	}

	/**
	 * This method fetch all the unique items ids from the staging data base and
	 * returns media ids List
	 * 
	 * 
	 * @param pConnection
	 */
  public List<String> getUniqueDeptIdsFromSubDept(final String pFeedId, final Connection pConnection)
      throws BBBSystemException {

    return getIdList(pConnection, SELECT_ALL_DEPT_SUB_DEPT, pFeedId, null, "JDA_DEPT_ID");
	}

	/**
	 * 
	 * @param pDeptId
	 * @param pConnection
	 * @return
	 */
  public List<MiscVO> getPIMSubDeptDetail(String pFeedId, String pSubDeptId, Connection pConnection)
      throws BBBSystemException, BBBBusinessException {
		boolean isMonitorCanceled = false;
		List<MiscVO> subDeptVOList = new ArrayList<MiscVO>();

		String query = SELECT_SUB_DEPT_IDS;
		if (isLoggingDebug()) {

			logDebug("query=" + query);
		}

		PreparedStatement prepareStatement = null;

		ResultSet resultSet = null;
		try {
      BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, GET_PIM_SUB_DEPT_DETAIL);
      prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
      resultSet = getResultSet(pConnection, prepareStatement, pSubDeptId, pFeedId, null);
			while (resultSet.next()) {

				subDeptVOList.add(populateSubDept(resultSet));
			}
			return subDeptVOList;
		} catch (SQLException sqlException) {

			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}
			BBBPerformanceMonitor.cancel(GET_PIM_SUB_DEPT_DETAIL);
			isMonitorCanceled = true;
      throw new BBBBusinessException(BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,sqlException.getMessage(), sqlException);
		} finally {

			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
			if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, GET_PIM_SUB_DEPT_DETAIL);

			}
		}
	}

	/**
	 * 
	 * @param pResultSet
	 * @return
	 * @throws SQLException
	 */
	private MiscVO populateSubDept(ResultSet pResultSet) throws SQLException {

		MiscVO deptVO = new MiscVO();

		if (pResultSet == null) {

			return null;
		}

		deptVO.setDeptid(pResultSet.getString("JDA_DEPT_ID"));
		deptVO.setSubDeptid(pResultSet.getString("JDA_SUB_DEPT_ID"));
    deptVO.setDescrip(BBBStringUtils.escapeHtmlString(pResultSet.getString("DESCRIP"), false));

		return deptVO;
	}

  public List<OperationVO> getdeptId(final String subDeptId, final Connection pConnection) throws BBBSystemException,
			BBBBusinessException {

    return getIdVOList(pConnection, SELECT_ALL_SUB_DEPT1, subDeptId, null, null, "JDA_DEPT_ID", null);
	}

	/**
	 * This method fetch all the unique items ids from the staging data base and
	 * returns media ids List
	 * 
	 * 
	 * @param pConnection
	 */
  public List<String> getUniqueBrandIds(final String pFeedId, final Connection pConnection) throws BBBSystemException {

    return getIdList(pConnection, SELECT_ALL_BRAND, pFeedId, null, "BRAND_ID");
	}

	/**
	 * This method fetch all the unique items ids from the staging data base and
	 * returns media ids List
	 * 
	 * 
	 * @param pConnection
	 */
  public List<String> getUniqueClassIds(final String pFeedId, final Connection pConnection) throws BBBSystemException {

    return getIdList(pConnection, SELECT_ALL_CLASS, pFeedId, null, "JDA_CLASS");
  }

  public List<OperationVO> getClassId(final String pFeedId, final String classId, final Connection pConnection)
			throws BBBSystemException, BBBBusinessException {

    return getIdVOList(pConnection, SELECT_ALL_CLASS1, classId, pFeedId, "JDA_SUB_DEPT_ID", "JDA_DEPT_ID", null);
	}

	/**
	 * 
	 * @param pDeptId
	 * @param pConnection
	 * @return
	 */
  public List<MiscVO> getPIMClassListVO(String pFeedId, String pClassId, Connection pConnection)
      throws BBBSystemException, BBBBusinessException {
		boolean isMonitorCanceled = false;
		List<MiscVO> classVOList = new ArrayList<MiscVO>();
    BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, GET_PIM_CLASS_LIST_VO);

		String query = SELECT_CLASS_DETAILS;
		PreparedStatement prepareStatement = null;

		ResultSet resultSet = null;
		try {

      prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
      resultSet = getResultSet(pConnection, prepareStatement, pClassId, pFeedId, null);

			while (resultSet.next()) {

				MiscVO classVO = populateClass(resultSet);
				if (classVO == null) {
					continue;
				}
				if (isLoggingDebug()) {
					logDebug("populate Class=" + classVO.toString());
				}
				classVOList.add(classVO);
			}
			return classVOList;
		} catch (SQLException sqlException) {

			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}

			BBBPerformanceMonitor.cancel(GET_PIM_CLASS_LIST_VO);
			isMonitorCanceled = true;
      throw new BBBBusinessException(sqlException.getMessage(), sqlException);
		} finally {

			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
			if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, GET_PIM_CLASS_LIST_VO);
			}
		}
	}

	/**
	 * 
	 * @param pResultSet
	 * @return
	 * @throws SQLException
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	private MiscVO populateClass(ResultSet pResultSet) throws SQLException {

		MiscVO classVO = new MiscVO();

		if (pResultSet == null) {

			return null;
		}

		classVO.setDeptid(pResultSet.getString("JDA_DEPT_ID"));
		classVO.setSubDeptid(pResultSet.getString("JDA_SUB_DEPT_ID"));
    classVO.setDescrip(BBBStringUtils.escapeHtmlString(pResultSet.getString("DESCRIP"), false));

		return classVO;
	}

	/**
	 * This method fetch all the unique items ids from the staging data base and
	 * returns media ids List
	 * 
	 * 
	 * @param pConnection
	 */
  public List<String> getUniqueThreshIds(final String pFeedId, final Connection pConnection) throws BBBSystemException {

    return getIdList(pConnection, SELECT_ALL_SKU_THRESH, pFeedId, null, "SKU_THRESHOLD_ID");
	}

	/**
	 * This method fetch all the brand details from the staging data base and
	 * populate BrandVO for the calling method
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pBrandId
	 * @param pConnection
	 * @return BrandVO
	 * @throws BBBBusinessException
	 */
  public List<MiscVO> getSkuThreshDetails(final String pFeedId, final String pDeptId, final Connection pConnection)
			throws BBBSystemException, BBBBusinessException {
		boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, GET_SKU_THRESH_DETAILS);
		String query = SELECT_SKU_THRESH_DETAILS;
		List<MiscVO> threshHoldList = new ArrayList<MiscVO>();
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {

      prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
      resultSet = getResultSet(pConnection, prepareStatement, pFeedId, pDeptId, null);

			while (resultSet.next()) {
				MiscVO thresholdVO = populateSkuThreshDetails(resultSet);
				if (isLoggingDebug()) {

					logDebug(thresholdVO.toString());
				}

				if (thresholdVO == null) {
					continue;
				}
				threshHoldList.add(thresholdVO);
			}

			return threshHoldList;
		} catch (SQLException sqlException) {

			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}
			BBBPerformanceMonitor.cancel(GET_SKU_THRESH_DETAILS);
			isMonitorCanceled = true;
      throw new BBBBusinessException(BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,sqlException.getMessage(), sqlException);
		} finally {

			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
			if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, GET_SKU_THRESH_DETAILS);
			}
		}

	}

	/**
	 * This method populate all the brand details from provided ResultSet
	 * 
	 * @param pResultSet
	 * @return
	 * @throws SQLException
	 */
  private MiscVO populateSkuThreshDetails(final ResultSet pResultSet) throws SQLException {
		MiscVO threshVO = new MiscVO();

		if (pResultSet == null) {

			return null;
		}

		threshVO.setSkuThresholdid(pResultSet.getString("SKU_THRESHOLD_ID"));
		threshVO.setDeptid(pResultSet.getString("JDA_DEPT_ID"));
		threshVO.setSubDeptid(pResultSet.getString("JDA_SUB_DEPT_ID"));
		threshVO.setClassid(pResultSet.getString("JDA_CLASS"));
		threshVO.setSkuid(pResultSet.getString("SKU_ID"));
		threshVO.setSiteFlag(pResultSet.getString("SITE_FLAG"));
		threshVO.setType(pResultSet.getString("TYPE"));
		threshVO.setThresholdLimited(pResultSet.getLong("THRESHOLD_LIMITED"));
    threshVO.setThresholdAvailable(pResultSet.getLong("THRESHOLD_AVAILABLE"));

		return threshVO;
	}

	/**
	 * 
	 * @param pFeedId
	 * @param pConnection
	 * @return
	 * @throws BBBSystemException
	 */
  public List<String> getUniqueEcoSkuRelIds(String pFeedId, Connection pConnection) throws BBBSystemException {

    return getIdList(pConnection, SELECT_ECO_SKU_REL_IDS, pFeedId, null, "SKU_ID");
	}



	/**
	 * Method to return Unique ZIP code regions
	 * @param pFeedId
	 * @param pConnection
	 * @return
	 * @throws BBBSystemException
	 */
  public List<String> getUniqueZipCodeRegions(String pFeedId, Connection pConnection) throws BBBSystemException {

    return getIdList(pConnection, SELECT_ZIP_CODE_REGION_IDS, pFeedId, null, "REGION_ID");
	}
	/**
	 * 
	 * @param pFeedId
	 * @param pConnection
	 * @return
	 * @throws BBBSystemException
	 */
  public List<String> getUniqueSkuZipRelIds(String pFeedId, Connection pConnection) throws BBBSystemException {

    return getIdList(pConnection, SELECT_SKU_ZIP_REL_IDS, pFeedId, null, "SKU_ID");
	}
  
  
  
  

	/**
	 * This method fetch all the ECO Sku details from the staging data base and
	 * populate ECOSkuVO for the calling method
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pBrandId
	 * @param pConnection
	 * @return BrandVO
	 * @throws BBBBusinessException
	 */
  public List<EcoSkuVO> getEcoSkuDetailList(final String pFeedId, final String pSkuId, final Connection pConnection)
			throws BBBSystemException, BBBBusinessException {

		String query = SELECT_ECO_FEE_SKU_DETAILS;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {

      prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
      resultSet = getResultSet(pConnection, prepareStatement, pFeedId, pSkuId, null);
			return populateEcoFeeSKUDetailList(resultSet);
		} catch (SQLException sqlException) {

			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}

      throw new BBBBusinessException(BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,sqlException.getMessage(), sqlException);
		} finally {

			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
		}

	}

  
	/**
	 * This method fetch all the ECO Sku details from the staging data base and
	 * populate ECOSkuVO for the calling method
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pBrandId
	 * @param pConnection
	 * @return BrandVO
	 * @throws BBBBusinessException
	 */
  public List<ZipCodeVO> getSkuRegionList(final String pFeedId, final String pSkuId, final Connection pConnection)
			throws BBBSystemException, BBBBusinessException {
		String query = SELECT_SKU_ZIP;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		String regionId = null;
		String siteId = null;
		try {
      prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
      resultSet = getResultSet(pConnection, prepareStatement, pFeedId, pSkuId, null);      

			if (resultSet != null) {
    	  return populateRegionDetailList(pSkuId,resultSet,pFeedId,siteId,pConnection);    	     
			} else {
				return null;
			}

		} catch (SQLException sqlException) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {
				logDebug(BBBStringUtils.stack2string(sqlException));
			}
      throw new BBBBusinessException(sqlException.getMessage(), sqlException);
		} finally {
			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
		}

	}

	/**
	 * This method fetch all the ECO Sku details from the staging data base and
	 * populate ECOSkuVO for the calling method
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pBrandId
	 * @param pConnection
	 * @return BrandVO
	 * @throws BBBBusinessException
	 */
  public List<ZipCodeVO> getZipCodeRegion(final String pFeedId, final String pSkuId, final Connection pConnection)
			throws BBBSystemException, BBBBusinessException {
		// String query = SELECT_SKU_ZIP;
		String query = SELECT_SKU_ZIP_DETAILS;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		String regionId = null;
		String siteId = null;
		try {
      prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
      resultSet = getResultSet(pConnection, prepareStatement, pFeedId, pSkuId, null);      
			while (resultSet.next()) {
				if (resultSet.getString("REGION_ID") != null) {
					regionId = resultSet.getString("REGION_ID");

    	/*  if(resultSet.getString("SITE_ID") != null){
    	   siteId = resultSet.getString("SITE_ID");
    	  }   */ 	  
    	  return populateRegionDetailList(pSkuId,regionId,pFeedId,siteId,pConnection);    	     
				} else {
					return null;
				}
			}
		} catch (SQLException sqlException) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {
				logDebug(BBBStringUtils.stack2string(sqlException));
			}
      throw new BBBBusinessException(sqlException.getMessage(), sqlException);
		} finally {
			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
		}
		return null;
	}

	/**
	 * This method populate all the rebate details from provided ResultSet
	 * 
	 * @param pResultSet
	 * @return
	 * @throws SQLException
	 * @throws BBBBusinessException
	 */
  private List<EcoSkuVO> populateEcoFeeSKUDetailList(final ResultSet pResultSet) throws SQLException,
			BBBBusinessException {

		List<EcoSkuVO> ecoSkuVOList = new ArrayList<EcoSkuVO>();
		EcoSkuVO ecoSkuVO = null;
		try {
			if (pResultSet == null) {

				return null;
			}

			while (pResultSet.next()) {

				ecoSkuVO = new EcoSkuVO();
				ecoSkuVO.setSKUId(pResultSet.getString("SKU_ID"));
				ecoSkuVO.setStateId(pResultSet.getString("STATE_ID"));
				ecoSkuVO.setEcoFeeSkuId(pResultSet.getString("FEE_SKU_ID"));
				ecoSkuVO.setEcoFeeSkuType(pResultSet.getString("FEE_SKU_TYPE"));
				ecoSkuVOList.add(ecoSkuVO);
			}

		} catch (SQLException sqlException) {
			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}
      throw new BBBBusinessException(BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,sqlException.getMessage(), sqlException);

		}
		return ecoSkuVOList;
	}

  
  
  

	/**
	 * This method populate all the Sku Region details from provided ResultSet
	 * 
	 * @param pResultSet
	 * @return
	 * @throws SQLException
	 * @throws BBBBusinessException
	 */

  private List<ZipCodeVO> populateRegionDetailList(String pSkuId,ResultSet resultSet2,String pFeedId,String pSiteId,final Connection pConnection) throws SQLException,
			BBBBusinessException, BBBSystemException {
		List<ZipCodeVO> zipCodeVOList = new ArrayList<ZipCodeVO>();
		ZipCodeVO zipCodeVO = null;

		try {

			while (resultSet2.next()) {
				zipCodeVO = new ZipCodeVO();
				zipCodeVO.setRegionId(resultSet2.getString("REGION_ID"));
				zipCodeVO.setSkuId(resultSet2.getString("sku_id"));
				zipCodeVO.setOperationFlag(resultSet2.getString("OPERATION_FLAG"));
				zipCodeVOList.add(zipCodeVO);
			}
		} finally {
			closeResultSet(resultSet2);
		}
		return zipCodeVOList;
	}


	/**
	 * This method populate all the Sku Region details from provided ResultSet
	 * 
	 * @param pResultSet
	 * @return
	 * @throws SQLException
	 * @throws BBBBusinessException
	 */

  private List<ZipCodeVO> populateRegionDetailList(String pSkuId,String pRegionId,String pFeedId,String pSiteId,final Connection pConnection) throws SQLException,
			BBBBusinessException {
		List<ZipCodeVO> zipCodeVOList = new ArrayList<ZipCodeVO>();
		ZipCodeVO zipCodeVO = null;
		String query = SELECT_SKU_ZIP_DETAILS;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			resultSet = getResultSet(pConnection, prepareStatement, pFeedId, pRegionId, null);    
			if (resultSet == null) {
				return null;
			}
			while (resultSet.next()) {
				zipCodeVO = new ZipCodeVO();
				zipCodeVO.setRegionName(resultSet.getString("REGION_NAME"));
				zipCodeVO.setZipCode(resultSet.getString("ZIP_CODES"));
				zipCodeVO.setSiteId(pSiteId);
				zipCodeVO.setRegionId(pRegionId);
				zipCodeVO.setSkuId(pSkuId);
				zipCodeVOList.add(zipCodeVO);
			}
		} catch (BBBSystemException e) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(e));
			}
			if (isLoggingDebug()) {
				logDebug(BBBStringUtils.stack2string(e));
			}
		} finally {
			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
		}
		return zipCodeVOList;
	}


  
  
  
	/**
	 * This method will return boolean true if any of the Pricing feed status is staging
	 * in progress of Emergency In Progress
	 * 
	 * @param pConnection
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
  public boolean getPIMFeedStatus(Connection pConnection) throws BBBSystemException, BBBBusinessException {
		String query = SELECT_FEED_STATUS;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		boolean feedStatus = false;
		try {
			if (getFeedInProgressQuery() != null
					&& !getFeedInProgressQuery().isEmpty()) {
				query = getFeedInProgressQuery();
			}
			prepareStatement = getConnection(pConnection).prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			if (getFeedInProgressQuery() == null
					|| getFeedInProgressQuery().isEmpty()) {
				prepareStatement.setString(1, "%_IN_PROGRESS%");
				prepareStatement.setString(2, "%ERROR%");
				prepareStatement.setString(3, "%SYSTEM%");
			}
			resultSet = prepareStatement.executeQuery();
			if (resultSet != null && resultSet.next()) {
				if (isLoggingDebug()) {
					logDebug("No of records:" + resultSet.getInt(1));
				}
				feedStatus = (resultSet.getInt(1) > 0);
			}
		} catch (SQLException sqlException) {

			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}

			throw new BBBBusinessException(
					BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,
					sqlException.getMessage(), sqlException);
		} finally {

			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
		}
		return feedStatus;
	}
  
  public int getResultCount(PreparedStatement pStmt, Connection pConnection){
	  
	  PreparedStatement prepareStatement = null;
	  ResultSet resultSet = null;
	  int feedCount=0;
				
			try{
			resultSet = pStmt.executeQuery();
			if (resultSet != null && resultSet.next()) {
				if (isLoggingDebug()) {
					logDebug("No of records:" + resultSet.getInt(1));
				}
				feedCount = resultSet.getInt(1) ;
			}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				logError(e.getMessage());

			}  finally {

				closeResultSet(resultSet);
				closePreparedStatement(prepareStatement);
			}
			
	  return feedCount;
  }
  
  /**
   * @return Flag PIM Feed status for direct import.
   * @throws BBBSystemException
   * @throws BBBBusinessException
   */
public boolean getPIMFeedStatusForDirectImport() throws BBBSystemException {
	  Connection conn = null;
	  boolean feedStatus = false;
	  
	  try{
		  conn = openConnection();

		  String emergencyFeedStatusQuery = DIRECT_SELECT_EMERGENCY_FEED_STATUS;
		  PreparedStatement emergencyFeedStatusQueryStmt = conn.prepareStatement(emergencyFeedStatusQuery, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		  int emergencyFeedCount = getResultCount(emergencyFeedStatusQueryStmt, conn);

		  if(emergencyFeedCount==0) {
			  String errorFeedQuery = DIRECT_SELECT_ECP_ERROR_FEED_STATUS;
			  PreparedStatement errorFeedQueryStmt = conn.prepareStatement(errorFeedQuery, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			  int errorFeedCount = getResultCount(errorFeedQueryStmt, conn);

			  if(errorFeedCount==0) {	  
				  String inProgressFeedQuery = DIRECT_SELECT_INPROGRESS_FEED_STATUS;
				  PreparedStatement inProgressFeedQueryStmt = conn.prepareStatement(inProgressFeedQuery, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				  int inProgressFeedCount = getResultCount(inProgressFeedQueryStmt, conn);

				  if(inProgressFeedCount==0){
					  feedStatus = true;
				  }
			  }
		  }
	} catch (SQLException sqlException) {
		if (isLoggingError()) {
			logError(BBBStringUtils.stack2string(sqlException));
		}
		throw new BBBSystemException(
				BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,
				sqlException.getMessage(), sqlException);
	} finally{
		closeConnection(conn);
	}
	return feedStatus;
}
  /**
	 * 
	 * @param pFeedId
	 * @param pStatus
	 * @return
	 */
public int updateDirectFeedStatus(final String pFeedId, final String pStatus, final Connection pConnection)
    throws BBBSystemException {

		PreparedStatement prepareStatement = null;
		boolean isMonitorCanceled = false;
		BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, UPDATE_FEED_STATUS_METHOD);

		try {

			prepareStatement = getConnection(pConnection).prepareStatement(UPDATE_DIRECT_FEED_STATUS);
			prepareStatement.setString(1, pStatus);
			prepareStatement.setString(2, pFeedId);

			return prepareStatement.executeUpdate();

		} catch (SQLException sqlex) {

			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlex));
			}
			BBBPerformanceMonitor.cancel(UPDATE_FEED_STATUS_METHOD);
			isMonitorCanceled = true;

		} finally {

			closePreparedStatement(prepareStatement);
			if (!isMonitorCanceled) {
				BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, UPDATE_FEED_STATUS_METHOD);
			}
		}
		return 0;
	}


public boolean isFeedAlreadyProcessed(String pFeedId, Connection pConnection){
	boolean isfeedProcessed = false;
	PreparedStatement prepareStatement = null;
	ResultSet resultSet = null;
	try {
		prepareStatement = pConnection.prepareStatement(SELECT_DIRECT_FEED);
		prepareStatement.setString(1, pFeedId);
		resultSet = prepareStatement.executeQuery();
		if (resultSet != null && resultSet.next()) {
			if (isLoggingDebug()) {
				logDebug("No of records:" + resultSet.getInt(1));
			}
			String feedStatus = resultSet.getString("feed_status");
			if(feedStatus.equals("CLOSED")){
				isfeedProcessed = true;
			}
		}		
		
	} catch (SQLException sqlex) {
		if (isLoggingError()) {

			logError(BBBStringUtils.stack2string(sqlex));
		}
	}
	finally{
		closePreparedStatement(prepareStatement);
	}
	
	return isfeedProcessed;
}

public void insertDirectFeedDetail(String pFeedId, Connection pConnection ) {
	PreparedStatement prepareStatement = null;
	
	try {
		prepareStatement = pConnection.prepareStatement(INSERT_DIRECT_FEED);
		prepareStatement.setString(1, pFeedId);
		
		prepareStatement.executeUpdate();
		pConnection.commit();
	} catch (SQLException sqlex) {
		if (isLoggingError()) {

			logError(BBBStringUtils.stack2string(sqlex));
		}
	}
	finally{
		closePreparedStatement(prepareStatement);
	}
	
}
	
	/**
	 * This method will return boolean true if any of the Pricing feed status is
	 * In Progress, Error or System state
	 * 
	 * @param pConnection
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public boolean getPIMPricingFeedStatus(Connection pConnection)
			throws BBBSystemException, BBBBusinessException {
		String query = SELECT_PRICING_FEED_STATUS;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		boolean feedStatus = false;
		try {
			if (getFeedInProgressQuery() != null
					&& !getFeedInProgressQuery().isEmpty()) {
				query = getFeedInProgressQuery();
			}
			prepareStatement = getConnection(pConnection).prepareStatement(
					query, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			if (getFeedInProgressQuery() == null
					|| getFeedInProgressQuery().isEmpty()) {
				prepareStatement.setString(1, "%_IN_PROGRESS%");
				prepareStatement.setString(2, "%ERROR%");
				prepareStatement.setString(3, "%SYSTEM%");
			}
			resultSet = prepareStatement.executeQuery();
			if (resultSet != null && resultSet.next()) {
				if (isLoggingDebug()) {
					logDebug("No of records for Pricing:" + resultSet.getInt(1));
				}
				feedStatus = (resultSet.getInt(1) > 0);
			}
		} catch (SQLException sqlException) {

			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlException));
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(sqlException));
			}

			throw new BBBBusinessException(BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION, sqlException.getMessage(), sqlException);
		} finally {

			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
		}
		return feedStatus;
	}
	
  public String findEndecaDeploymentType() throws BBBSystemException, BBBBusinessException {
		PreparedStatement prepareStatement = null;
		String query = SELECT_DEPLOYMENT_DETAILS;
		ResultSet resultSet = null;
		Connection connection = null;
		String deploymentType = "PARTIAL";


		try {

			connection = openConnection();

      
			if (isLoggingDebug()) {
				logDebug("Getting Status from open feeds");
			}
        prepareStatement = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        resultSet = getResultSet(connection, prepareStatement, STAGING_IN_PROGRESS, PRODUCTION_IN_PROGRESS,
					EMERGENCY_IN_PROGRESS);

			if (resultSet != null) {

				while (resultSet.next()) {

					deploymentType = resultSet.getString("DEPLOYMENT_TYPE");
					if (isLoggingDebug()) {
						logDebug("deploymentType=" + deploymentType);
					}

					if (deploymentType.equalsIgnoreCase("FULL")) {
						break;
					}
				}
			}

     

			if (isLoggingDebug()) {

				logDebug("*********deploymentType=" + deploymentType);
			}
			return deploymentType;

		} catch (SQLException sqlex) {

			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlex));
			}

		} finally {

			closePreparedStatement(prepareStatement);
			closeConnection(connection);
		}
		return null;
	}

	/**
	 * @return the feedInProgressQuery
	 */
	public String getFeedInProgressQuery() {
		return mFeedInProgressQuery;
	}

	/**
 * @param feedInProgressQuery the feedInProgressQuery to set
	 */
	public void setFeedInProgressQuery(String feedInProgressQuery) {
		this.mFeedInProgressQuery = feedInProgressQuery;
	}

	
	/**
	 * This method is used to fetch all the product ids from 
	 * ECP_PRODUCTS_KEYWORDS table for the mentioned feed id.
	 * 
	 * @param feedId
	 * @param pConnection
	 * @return
	 * @throws BBBSystemException
	 */
	public List<String> getKeywordsProducts(String feedId,
			Connection pConnection) throws BBBSystemException {
		
		// Query to fetch the item ids for the specified feed id.
		String queryString = SELECT_KEYWORDS_PRODUCTS;
		if(isLoggingDebug()){
			logDebug("Query to fetch the items from PIM : " + queryString);
		}
		
		List<String> itemsList = getIdList(pConnection, queryString, feedId, null, SKU_ID);
		
		if(isLoggingDebug()){
			logDebug("Items returned from PIM keywords table : " + itemsList + " for feedId = " + feedId);
		}
		return itemsList;
	}
	

	/*
	 * This method only populates the item vo with its product keywords from PIM
	 * 
	 */
	public ItemVO getPIMProductKeywordsDetail(String feedId, String productId,
			ItemVO itemVO, Connection pConnection) throws BBBSystemException {
		
		// Populate the item vo with it's product keywords
		populateProductKeywords(feedId, productId, itemVO, pConnection);
		return itemVO;
	}
	
	
	public String getPIMFeedStatus(String pFeedId, Connection pConnection) throws BBBSystemException, BBBBusinessException {
		String feedStatus=null;
		PreparedStatement preparedStatement = null;
		String feedStatusQuery = SELECT_FEED_STATUS_FOR_FEED_ID;
		ResultSet resultSet = null;
		try {
			preparedStatement = pConnection.prepareStatement(feedStatusQuery, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			preparedStatement.setString(1,pFeedId);
	        resultSet = preparedStatement.executeQuery();
	        if(resultSet!=null){
	        	while(resultSet.next()){
	        		feedStatus = resultSet.getString("feed_status");
	        	}
	        }
				
		} catch (SQLException e) {
			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(e));
			}
		}
		finally{
			closePreparedStatement(preparedStatement);
		}
		return feedStatus;
	}
	
	/**
	 * Method returns Feeds list for direct staging import
	 * @param pConnection
	 * @return Feeds list
	 * @throws BBBSystemException 
	 */
	public List<String> getFeedsForDirectStagingImport(Connection pConnection) throws BBBSystemException {
		List<String>feedList = new ArrayList<String>();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = pConnection.prepareStatement(SELECT_DIRECT_REGULAR_FEEDS);
			resultSet = preparedStatement.executeQuery();
	        if(resultSet!=null){
	        	while(resultSet.next()){
	        		feedList.add(resultSet.getString("feed_id"));
	        	}
	        }
		} catch (SQLException sqlex) {
			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(sqlex));
			}
			throw new BBBSystemException(
					BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,
					sqlex.getMessage(), sqlex);
		}
		finally{
			closePreparedStatement(preparedStatement);
		}
		return feedList;
	}
	
	/**
	 * @param pConnection
	 * @return List of Emergency Feeds
	 * @throws BBBSystemException
	 */
	public List<String> getEmergencyFeeds(Connection pConnection) throws BBBSystemException {

		ResultSet resultSet = null;
		List<String> idsList = new ArrayList<String>();
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = getConnection(pConnection).prepareStatement(SELECT_EMERGENCY_FEEDS,
			        ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			resultSet = prepareStatement.executeQuery();
			if (resultSet == null) {
				return idsList;
			}
			while (resultSet.next()) {
				idsList.add(resultSet.getString("feed_id"));
			}
		} catch (SQLException sqlex) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(sqlex));
			}
			throw new BBBSystemException(
					BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,
					sqlex.getMessage(), sqlex);
		} finally{
			closePreparedStatement(prepareStatement);
		}
		return idsList;
	}	
	
	/**
	 * @param pConnection
	 * @return isProdImportInProgress - Flag indicating if production import is in progress.
	 * @throws BBBSystemException
	 */
	public boolean isProdImportInProgress(Connection pConnection) throws BBBSystemException {
		boolean inProgress = true;
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = getConnection(pConnection).prepareStatement(
			        SELECT_PROD_IMP_IN_PROGRESS_FEEDS, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int count = getResultCount(prepareStatement, getConnection(pConnection));
			if (count < 1) {
				inProgress = false;
			}
		} catch (SQLException sqlex) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(sqlex));
			}
			throw new BBBSystemException(
					BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,
					sqlex.getMessage(), sqlex);
		} finally{
			closePreparedStatement(prepareStatement);
		}
		return inProgress;
	}

	/**
	 * @param pConnection
	 * @return isPIMFeedInException - Flag indicating if PIM feeds are in exception state.
	 * @throws BBBSystemException
	 */
	public boolean isPIMFeedInException(Connection pConnection) throws BBBSystemException {
		boolean inException = true;
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = getConnection(pConnection).prepareStatement(
			        SELECT_FEED_STATUS_IN_EXCEPTION, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int count = getResultCount(prepareStatement, getConnection(pConnection));
			if (count < 1) {
				inException = false;
			}
		} catch (SQLException sqlex) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(sqlex));
			}
			throw new BBBSystemException(
					BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,
					sqlex.getMessage(), sqlex);
		} finally {
			closePreparedStatement(prepareStatement);
		}
		return inException;

	}
	
	/**
	 * This method is responsible for determining if any feed's status is set to STAGING_IN_PROGRESS.
	 * @param pConnection
	 * @return
	 * @throws BBBSystemException
	 * @throws SQLException 
	 */
	public boolean isStagingImportInProgress(Connection pConnection) throws BBBSystemException {
		boolean inProgress = true;
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = getConnection(pConnection).prepareStatement(
					SELECT_STAGING_IMP_IN_PROGRESS_FEEDS, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int count = getResultCount(prepareStatement, getConnection(pConnection));
			if (count < 1) {
				inProgress = false;
			}
		} catch (SQLException sqlex) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(sqlex));
			}
			throw new BBBSystemException(
					BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,
					sqlex.getMessage(), sqlex);
		} finally {
			closePreparedStatement(prepareStatement);
		}
		return inProgress;
	}
	
	/**
	 * This method is responsible for determining if there are any EmergencyRegular Feeds in NEW2 or OPEN state.
	 * @param pConnection
	 * @return
	 * @throws SQLException 
	 * @throws BBBSystemException 
	 */
	public boolean isEmergencyRegularOpen(Connection pConnection) throws BBBSystemException {
		boolean emergencyRegularOpen = false;
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = getConnection(pConnection).prepareStatement(
					SELECT_EMERGENCY_REGULAR_NEW2_OPEN_DTS, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int count = getResultCount(prepareStatement, getConnection(pConnection));
			if (count > 0) {
				emergencyRegularOpen = true;
			}

		} catch (SQLException sqlex) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(sqlex));
			}
			throw new BBBSystemException(
					BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,
					sqlex.getMessage(), sqlex);
		} finally {
			closePreparedStatement(prepareStatement);
		} 
		return emergencyRegularOpen;
	}
	
	/**
	 * This method is responsible for getting List of feedIds by querying ecp_prj_imported_feeds.
	 * These feeds are imported in Production Project. 
	 * @param pConnection
	 * @param pProjectId
	 * @return
	 * @throws BBBSystemException 
	 */
	public List<String> getProdImportedFeedList(Connection pConnection,String pProjectId) throws BBBSystemException {
		List<String> feedList = null;
		if(pProjectId!=null && pConnection!=null) {
		final String feedIdColumnLabel = "feed_id";
			feedList = getIdList(pConnection, SELECT_IMPORTED_FEED_IN_PRD_PROJ, pProjectId, null, feedIdColumnLabel);
		}
		return feedList;
	}
	
	/**
	 * @param pConnection
	 * @param pProjectId
	 * @param pProductionDeploymentInProgressStatus
	 * @return Flag isProdProjectDeploying - Indicating the Project with the given Id is getting deployed. 
	 * @throws BBBSystemException
	 */
	public boolean isProdProjectDeploymentInProgress(Connection pConnection, String pProjectId) throws BBBSystemException {

		boolean isProdProjectDeploying = false;
		List<String> feedList = getProdImportedFeedList(pConnection, pProjectId);
		if (feedList != null && !feedList.isEmpty()) {
			try {
				for (String feedId : feedList) {
					String feedStatus = getPIMFeedStatus(feedId, pConnection);
					if (feedStatus != null && feedStatus.equalsIgnoreCase(getProdDeployProgressStatus())) {
						isProdProjectDeploying = true;
						break;
					}
				}
			} catch (BBBBusinessException bbe) {
				if (isLoggingError()) {
					logError(BBBStringUtils.stack2string(bbe));
				}
				throw new BBBSystemException(
						BBBCoreErrorConstants.VERS_MGR_GENERIC_ERROR,
						bbe.getMessage(), bbe);
			}
		}
		return isProdProjectDeploying;
	}

	public void updateProjectFeedDeploymentTime(String pProjectId,Connection pConnection) 
			throws BBBSystemException {

			if(isLoggingDebug()) {
				logDebug("PIM Feed Tools : updateProjectFeedDeploymentTime : Entry");
			}

			PreparedStatement prepareStatement = null;
			BBBPerformanceMonitor.start(UPDATE_DEPLOYED_FEEDS, UPDATE_DEPLOYED_FEEDS_TIMESTAMP);
			try {
				prepareStatement = pConnection.prepareStatement(UPDATE_ECP_PRJ_IMPORTED_FEEDS);
				prepareStatement.setString(1, pProjectId);
				int noOfRowsUpdated = prepareStatement.executeUpdate();

					if (noOfRowsUpdated >= 1) {
						if(isLoggingDebug()) {
							logDebug(noOfRowsUpdated+" feeds are closed and deployed as part of project : " + pProjectId);
						}
					}
				
			} catch (SQLException sqlex) {
				if (isLoggingError()) {
					logError(BBBStringUtils.stack2string(sqlex));
				}
			} finally {
				closePreparedStatement(prepareStatement);
					BBBPerformanceMonitor.end(UPDATE_DEPLOYED_FEEDS, UPDATE_DEPLOYED_FEEDS_TIMESTAMP);
			}
			if(isLoggingDebug()) {
				logDebug("PIM Feed Tools : updateProjectFeedDeploymentTime : Exit");
			}
		}
	
	public StringBuffer getFeedBadRecords(Connection pConnection, List<String> feedIds, String Service) throws BBBSystemException {
		StringBuffer badFeedsRecordsEmail = new StringBuffer();
		String query = null;
		ResultSet resultSet = null;
		PreparedStatement prepareStatement = null;
		if(!Service.equals(PIM_IMPORT_PRODUCTION)){
			query = SELECT_BAD_FEED_RECORD_FOR_DIRECT;
		}else{
			query =SELECT_BAD_FEED_RECORD_FOR_IMPORT;
		}
		try {
			for(String feedId : feedIds) {
				prepareStatement = getConnection(pConnection).prepareStatement(
						query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				prepareStatement.setString(1, feedId);
				resultSet = prepareStatement.executeQuery();
				if (resultSet != null) {
					while(resultSet.next()) {
						badFeedsRecordsEmail.append("For Feed_id : ").append(resultSet.getInt(1)).append("  Record : ").append(resultSet.getString(4)).append("  ").append(resultSet.getString(6));
						badFeedsRecordsEmail.append(System.getProperty("line.separator"));
					} 
					badFeedsRecordsEmail.append(System.getProperty("line.separator"));
				}	
			}
		} catch (SQLException sqlex) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(sqlex));
			}
			throw new BBBSystemException(
					BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,
					sqlex.getMessage(), sqlex);
		}
		finally
		{
			closeResultSet(resultSet);
			closePreparedStatement(prepareStatement);
		}
		return badFeedsRecordsEmail;

	}

	/**
	 * @param pFeedId
	 * @param pConnection
	 * @return Bad record count for given feed.
	 * @throws BBBSystemException
	 */
	public boolean hasFeedBadRecords(final String pFeedId, final Connection pConnection) throws BBBSystemException {
		boolean badRecords = false;
		if(isLoggingDebug()) {
			logDebug("PIM Feed Tools : getFeedBadRecordsCount : Entry");
		}
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			Connection connection = getConnection(pConnection);//NOPMD connection is passed in method argument
			prepareStatement = connection.prepareStatement(SELECT_BAD_FEED_RECORD);
			prepareStatement.setInt(1, Integer.parseInt(pFeedId.trim()));
			resultSet = prepareStatement.executeQuery();
			if (resultSet != null && resultSet.next()) {
				badRecords = true;
			}
		} catch (SQLException sqlex) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(sqlex));
			}
			throw new BBBSystemException(
					BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,
					sqlex.getMessage(), sqlex);
		} finally {
			closePreparedStatement(prepareStatement);
			closeResultSet(resultSet);
		}
		if(isLoggingDebug()) {
			logDebug("Bad records found for feed id " + Integer.parseInt(pFeedId.trim()) + ": " + badRecords);
			logDebug("PIM Feed Tools : getFeedBadRecordsCount : Exit");
		}
		return badRecords;
	}

	/**
	 * Inserting the feed timing details in ECP_FEED_TIME_DETAILS table
	 * @param pFeedId
	 * @param pTableProcessed
	 * @param pNumRows
	 * @param pStartTime
	 * @param pEndTime
	 * @param pIsProdImport
	 * @param pConnection
	 * @throws BBBSystemException
	 */
	public void updateFeedTimeDetails(final String pFeedId, final String pTableProcessed, final int pNumRows,
			final Timestamp pStartTime, final Timestamp pEndTime, final int pIsProdImport, Connection pConnection) throws BBBSystemException {

		PreparedStatement prepareStatement = null;

		final String pQuery = "INSERT INTO ECP_FEED_TIME_DETAILS (FEED_ID, TABLE_PROCESSED, NO_OF_ROWS, START_TIME, END_TIME, IS_PROD_IMPORT) VALUES (?, ?, ?, ?, ?, ?)";

		try {
			Connection connection = getConnection(pConnection);//NOPMD connection is passed in method argument

			prepareStatement = connection.prepareStatement(pQuery);
			prepareStatement.setInt(1, Integer.parseInt(pFeedId));
			prepareStatement.setString(2, pTableProcessed);
			prepareStatement.setInt(3, pNumRows);
			prepareStatement.setTimestamp(4, pStartTime);
			prepareStatement.setTimestamp(5, pEndTime);
			prepareStatement.setInt(6, pIsProdImport);

			prepareStatement.executeUpdate();
		} catch (SQLException sqlex) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(sqlex));
			}
			throw new BBBSystemException(
					BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,
					sqlex.getMessage(), sqlex);
		} finally {
			closePreparedStatement(prepareStatement);
		}
	}

	/**
	 * @param pTableName
	 * @param pFeedId
	 * @param pConnection
	 * @return Number of rows for the given FEED_ID in given table.
	 * @throws BBBSystemException
	 */
	public int getNumRowsForECPTable(final String pTableName, final String pFeedId,
			final Connection pConnection) throws BBBSystemException {
		if(isLoggingDebug()) {
			logDebug("PIM Feed Tools : getNumRowsForECPTable : Entry");
		}

		int count = 0;
		final String SELECT_NUM_ROWS_ECP_TABLE = "select count(*) from " + pTableName + " where feed_id=?";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			Connection connection = getConnection(pConnection);//NOPMD connection is passed in method argument
			
			preparedStatement = connection.prepareStatement(SELECT_NUM_ROWS_ECP_TABLE, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			preparedStatement.setInt(1, Integer.parseInt(pFeedId));
			resultSet = preparedStatement.executeQuery();
			
			if(resultSet != null && resultSet.next()) {
				count = resultSet.getInt(1);
			}
		} catch(SQLException sqlex) {
			if(isLoggingError()) {
				logError(BBBStringUtils.stack2string(sqlex));
			}
			throw new BBBSystemException(
					BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,
					sqlex.getMessage(), sqlex);
		} finally{
			closePreparedStatement(preparedStatement);
			closeResultSet(resultSet);
		}

		if(isLoggingDebug()) {
			logDebug("No of rows in " + pTableName + " is: " + count);
			logDebug("PIM Feed Tools : getNumRowsForECPTable : Exit");
		}
		return count;
	}

	/**
	 * @param pConnection
	 * @return <code>True</code> if feeds are getting deployed and <code>false</code> otherwise.
	 * @throws BBBSystemException
	 */
	public boolean isProdDeployInProgress(Connection pConnection) throws BBBSystemException {
		boolean inProgress = true;
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = getConnection(pConnection).prepareStatement(
			        SELECT_PROD_DEP_IN_PROGRESS_FEEDS, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int count = getResultCount(prepareStatement, getConnection(pConnection));
			if (count < 1) {
				inProgress = false;
			}
		} catch (SQLException sqlex) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(sqlex));
			}
			throw new BBBSystemException(
					BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,
					sqlex.getMessage(), sqlex);
		} finally {
			closePreparedStatement(prepareStatement);
		}
		return inProgress;
	}

	/**
	 * @return feeds in DEPLOYED_TO_STAGING status count
	 * @throws BBBSystemException
	 */
	public int getDeployedToStagingPIMFeedCount() throws BBBSystemException {
		Connection conn = null;
		int deployedToStagingFeedCount = 0;
		try {
			conn = openConnection();
			String deployedToStagingFeedStatusQuery = DIRECT_SELECT_DEPLOYED_TO_STAGING_FEED_STATUS;
			PreparedStatement deployedToStagingFeedStatusQueryStmt = conn.prepareStatement(deployedToStagingFeedStatusQuery, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			deployedToStagingFeedCount = getResultCount(deployedToStagingFeedStatusQueryStmt, conn);
		} catch (SQLException sqlException) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(sqlException));
			}
			throw new BBBSystemException(
					BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,
					sqlException.getMessage(), sqlException);
		} finally {
			closeConnection(conn);
		}
		return deployedToStagingFeedCount;
	}

	/*
	 * public String[] getProcessIds() { // TODO Auto-generated method stub
	 * return null; }
	 * 
	 * public void updateProcessIdForFeed(String pFeedId, String
	 * pProcessId,Connection pConnection) { PreparedStatement prepareStatement =
	 * null; boolean isMonitorCanceled = false;
	 * BBBPerformanceMonitor.start(IMPORT_PROCESS_PIMTOOLS, "updateFeedStatus");
	 * String query="INSERT INTO PENDING_PROJECTS VALUES(?,?,?)"; try {
	 * 
	 * prepareStatement =
	 * getConnection(pConnection).prepareStatement(UPDATE_FEED_STATUS);
	 * prepareStatement.setString(1, pFeedId); prepareStatement.setString(2,
	 * pProcessId); prepareStatement.setString(3, pFeedId);
	 * prepareStatement.executeUpdate(); } catch (SQLException sqlex) {
	 * 
	 * if (isLoggingError()) {
	 * 
	 * logError(BBBStringUtils.stack2string(sqlex)); }
   * BBBPerformanceMonitor.cancel("updateFeedStatus"); isMonitorCanceled = true;
	 * 
	 * } catch (BBBSystemException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } finally {
	 * 
	 * closePreparedStatement(prepareStatement); if (!isMonitorCanceled){
   * BBBPerformanceMonitor.end(IMPORT_PROCESS_PIMTOOLS, "updateFeedStatus"); } }
	 * 
	 * }
	 */

}
