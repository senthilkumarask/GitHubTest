package com.bbb.thirdparty.omniture.tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.thirdparty.omniture.vo.OmnitureGetResponseVO;
import com.bbb.thirdparty.omniture.vo.OmnitureQueueRequestVO;
import com.bbb.thirdparty.omniture.vo.OmnitureReportDataVO;
import com.bbb.thirdparty.omniture.vo.OmnitureReportElementVO;
import com.bbb.thirdparty.omniture.vo.OmnitureReportMetricVO;
import com.bbb.thirdparty.omniture.vo.QueueRequestVO;
import com.bbb.thirdparty.omniture.vo.ReportDescriptionVO;
import com.bbb.utils.BBBUtility;

import atg.adapter.gsa.GSARepository;


/**
 * Implements methods used in omniture report API call for L2->boosted products, 
 * L3->boosted products and brand->boosted products ,boosting processing
 *
 */

public class OmnitureBoostedL2L3BrandProductTools  extends OmnitureReportAPIToolsImpl {
	
	private static final String PREPARE_INPUT_FOR_QUEUE_REQUEST = "prepareInputForQueueRequest";
	private String CLS_NAME = "OmnitureBoostedL2L3BrandProductTools";
	private String insertReportData = "insertReportData";
	private String getProductList = "getProductList";
	private String updateReportType = "updateReportType";
	/**
	 * This method is used to prepare the Input RequestVO for Queue Method
	 * 
	 * @param reportIndex
	 * @param concept
	 * @return OmnitureQueueRequestVO
	 * @throws BBBSystemException
	 * @throws BBBBusinessException 
	 */
	@Override
	public OmnitureQueueRequestVO prepareInputForQueueRequest(QueueRequestVO queueRequestVO) throws BBBSystemException, BBBBusinessException {
		
		logDebug( CLS_NAME + " [ prepareInputForQueueRequest ] method started for concept :: " + queueRequestVO.getConcept()+" reportIndex is "+queueRequestVO.getReportIndex());
		BBBPerformanceMonitor.start(CLS_NAME, PREPARE_INPUT_FOR_QUEUE_REQUEST);
		OmnitureQueueRequestVO requestVO = new OmnitureQueueRequestVO();
		ReportDescriptionVO reportDescription = new ReportDescriptionVO();
		
		// Setting The Elements
		List<OmnitureReportElementVO> elementVOs = new ArrayList<OmnitureReportElementVO>();
        
        //Setting First Element
		OmnitureReportElementVO elementVO = new OmnitureReportElementVO();
		
		elementVO.setId(getElementId1());
		elementVO.setTop(queueRequestVO.getTop());
		elementVO.setStartingWith(queueRequestVO.getStartingWith());
		elementVOs.add(elementVO);
		
        // Setting Second Element
		elementVO = new OmnitureReportElementVO();
		elementVO.setId(getElementId2());
		final int noOfTopSearchedItems = queueRequestVO.getOmnitureReportConfigVo().getProductCount();
		
		elementVO.setTop(noOfTopSearchedItems);
		elementVOs.add(elementVO);
		
		reportDescription.setElements(elementVOs);
		
         //Setting Report Suite Information		
		final String omnitureReportSuiteId = this.getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.OMNITURE_REPORT_SUITE_ID, BBBCoreConstants.BLANK);
		
		reportDescription.setReportSuiteID(omnitureReportSuiteId);
		logDebug( CLS_NAME + " [ prepareInputForQueueRequest ] omnitureReportSuiteId Value :: " + omnitureReportSuiteId);
		
		//Setting To Date
		reportDescription.setDateFrom(queueRequestVO.getDateFrom());
		reportDescription.setDateTo(queueRequestVO.getDateTo());
		
		logDebug( CLS_NAME + " [ prepareInputForQueueRequest ] To Date Value :: " + queueRequestVO.getDateTo() + "From Date Vale :: " + queueRequestVO.getDateFrom());
		
		
		// Setting Metric ID
		final String omnitureReportMetricId = getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.CATEGORY_BRAND_PROUCT_BOOSTING_REPORT_METRIC_ID, BBBCoreConstants.BLANK); 
		logDebug( CLS_NAME + " [ prepareInputForQueueRequest ] omnitureReportMetricId Value :: " + omnitureReportMetricId);
		
		final List<OmnitureReportMetricVO>metrics = new ArrayList<OmnitureReportMetricVO>();
		OmnitureReportMetricVO omnitureReportMetVO = new OmnitureReportMetricVO();
		omnitureReportMetVO.setId(omnitureReportMetricId);
		metrics.add(omnitureReportMetVO);
		
		reportDescription.setMetrics(metrics);
		
		requestVO.setReportDescription(reportDescription);
		logDebug( CLS_NAME + " [ prepareInputForQueueRequest ] method ends OmnitureQueueRequestVO value :: " + requestVO.toString());
		BBBPerformanceMonitor.end(CLS_NAME, PREPARE_INPUT_FOR_QUEUE_REQUEST);
		 
		return requestVO;
			
	}

	/**
	 * Calculates number of reports in report batch based on batch size and total no of search keywords
	 * @return numOfReports
	 */
	public int calculateNumberOfReports() {
		
		logDebug( CLS_NAME + " [ calculateNumberOfReports ] method starts");
		int numOfReports = 1;
		int noOfSearchedTerms = getBbbCatalogTools().getValueForConfigKey(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.L2_BOOSTING_TOTAL_SEARCHED_TERMS, BBBCoreConstants.TEN_THOUSANDS);
		int batchSize = getBbbCatalogTools().getValueForConfigKey(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.L2_BOOSTING_BATCH_SIZE, BBBCoreConstants.FIFTY_THOUSANDS);
						
		logDebug("Total no of search terms are "+noOfSearchedTerms+" and batch size is "+batchSize);
		
		if(noOfSearchedTerms%batchSize == 0) {
			
			numOfReports = noOfSearchedTerms/batchSize;
			
		} else {
			
			numOfReports = noOfSearchedTerms/batchSize + 1;
			
		}
		
		logDebug( CLS_NAME + " [ calculateNumberOfReports ] method ends :: No of report is "+numOfReports);
		return numOfReports;
		
	}
	
	/**
	 * This method will Insert the L2L3Brand Data in the table BBB_L2L3BRAND_OMNITURE_DATA
	 * Report Type logic will be calculated and the report type either as L2, L3 or Brand will be set.
	 * The data will be either inserted for the new entries and will be updated for the existing entries.
	 * Merge Query for the same is used for the performance improvement 
	 */
	@Override
	public int insertReportData(OmnitureGetResponseVO omnitureGetResponseVO, String reportID, String concept,
			String reportType) {
		
		logDebug(CLS_NAME + "[insertReportData] method starts" );
		BBBPerformanceMonitor.start(CLS_NAME + BBBCoreConstants.UNDERSCORE + insertReportData);
		
		long startTime = System.currentTimeMillis();
		logDebug(CLS_NAME + "Start Time is :- " + startTime);
		
		logDebug("Report Id " + reportID + " concept is :- " + concept + "reportType :- " + reportType);
		
		PreparedStatement preparedStatement = null;
		Connection connection = null;
		boolean success = false;
		
		List<OmnitureReportDataVO> dataList = omnitureGetResponseVO.getReport().getData();
		int record = 0;
		int batchSize = getBbbCatalogTools().getValueForConfigKey(BBBCoreConstants.OMNITURE_BOOSTING, 
				BBBCoreConstants.INSERT_L2L3BRAND_BATCH_SIZE, BBBCoreConstants.DEFAULT_BATCH_SIZE);
		
		String typeOfReport =null;
		String categoryName;
		String[] splitCategoryName;
		String categoryId;
		StringBuffer productList;
	
		try {
			
			connection = ((GSARepository)getOmnitureReportRepository()).getDataSource().getConnection();	
			connection.setAutoCommit(false);
			preparedStatement = connection.prepareStatement(MERGE_L2L3BRANDS_DATA_SQL);
			for (OmnitureReportDataVO omnitureReportDataVO : dataList) {
				success = false;
				
				categoryName = omnitureReportDataVO.getName();
				splitCategoryName = categoryName.split(BBBCoreConstants.COLON);
				if(reportType.equalsIgnoreCase(BBBCoreConstants.L2L3BRAND)){					
					if(BBBCoreConstants.OMNITURE_PRODUCT_OTHER.equalsIgnoreCase(categoryName)
								|| BBBCoreConstants.OMNITURE_UNSPECIFIED.equalsIgnoreCase(categoryName)
								|| BBBCoreConstants.OMNITURE_UNDEFINED.equalsIgnoreCase(categoryName)
								|| categoryName.equalsIgnoreCase(BBBCoreConstants.NON_BROWSE)
								|| categoryName.equalsIgnoreCase(BBBCoreConstants.UNKNOWN_AT_TIME_OF_PURCHASE)
								|| categoryName.contains(BBBCoreConstants.CATEGORY_PAGE)){
						continue;
					}
					typeOfReport = updateReportType(splitCategoryName[0]);
					/***
					 * BBBI-1958 | Report_type = null when consuming L2L3BRAND response from omniture
					 */
					if (typeOfReport == null) {
						continue;
					}
				}				
				record++;
				
				
				/*
				 * The below check to add the 0th Index as Category Id will be removed once we get the category Id added in evar 6 from omniture.
				*/
				if(splitCategoryName.length == 2){
					categoryId =  splitCategoryName[1];
				}else{
					categoryId =  splitCategoryName[0];
				}
				
				List<OmnitureReportDataVO> breakdownList = omnitureReportDataVO.getBreakdown();
				Collections.sort(breakdownList);
				
				productList = getProductList(breakdownList);
							
				logDebug("TypeOfReport :-" + typeOfReport + " categoryId :- "+ categoryId + "productList :- " + productList.toString());
				
				
				preparedStatement.setString(1, categoryId);
				preparedStatement.setString(2, typeOfReport);
				preparedStatement.setString(3, concept);
				preparedStatement.setString(4, productList.toString());
				preparedStatement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
				preparedStatement.setString(6, reportID);
				preparedStatement.setString(7, reportID);
				preparedStatement.setString(8, omnitureGetResponseVO.getReport().getReportSuite().getId());
				preparedStatement.setString(9, typeOfReport);
				preparedStatement.setString(10, omnitureGetResponseVO.getReport().getPeriod());
				preparedStatement.setString(11, concept);
				preparedStatement.setString(12, productList.toString());
				preparedStatement.setString(13, categoryId);
				preparedStatement.setTimestamp(14, new Timestamp(System.currentTimeMillis()));
				preparedStatement.executeQuery();
				success = true;
				
				
				if(record % batchSize == BBBCoreConstants.ZERO)	{
					record = commitConnectionOrRollback(omnitureGetResponseVO, connection, success, record,reportID);
				}					
			}
			
			record = commitConnectionOrRollback(omnitureGetResponseVO, connection, success, record,reportID);
			
		} catch (SQLException sqlException) {
			record=0;
			setErrorInOmnitureVO(omnitureGetResponseVO, sqlException);
			
		}finally{
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}				
				if (!connection.isClosed()) {
					connection.close();
				}
				
			} catch (SQLException e) {
				record=0;
				setErrorInOmnitureVO(omnitureGetResponseVO, e);
			}
		}
		
		long endTime = System.currentTimeMillis();
		logDebug("Total Time taken to execute " + CLS_NAME + " is :- " + (endTime - startTime));
		BBBPerformanceMonitor.end(CLS_NAME + BBBCoreConstants.UNDERSCORE + insertReportData);
		logDebug(CLS_NAME + "[insertReportData] method ends" );
		//Archiving is not done in L2L3Brand so records moved to archive table is set to zero
		omnitureGetResponseVO.setRecordsMoved(BBBCoreConstants.ZERO);
		return record;
	}

	/**
	 * This methods returns the list of product id's separated by comma(,)
	 */
	private StringBuffer getProductList(List<OmnitureReportDataVO> breakdownList) {
		logDebug(CLS_NAME + "Inside Method : getProductList");
		BBBPerformanceMonitor.start(CLS_NAME + BBBCoreConstants.UNDERSCORE + getProductList);
		StringBuffer productList;
		productList = new StringBuffer();

		if (!BBBUtility.isListEmpty(breakdownList)) {

			for (OmnitureReportDataVO omnitureBreakDownList : breakdownList) {
				if (!BBBCoreConstants.OMNITURE_PRODUCT_OTHER.equalsIgnoreCase(omnitureBreakDownList.getName())
						&& !BBBCoreConstants.OMNITURE_UNSPECIFIED.equalsIgnoreCase(omnitureBreakDownList.getName())
						&& !BBBCoreConstants.OMNITURE_UNDEFINED.equalsIgnoreCase(omnitureBreakDownList.getName())) {
					productList.append(omnitureBreakDownList.getName()+ BBBCoreConstants.COMMA);
					
					
				}
			}
		}
		if(productList.length() >0){
			productList.setLength(productList.length()-1);
		}
		logDebug(CLS_NAME + "Exiting Method : updateReportType : productList : "+productList.toString());
		BBBPerformanceMonitor.end(CLS_NAME+ BBBCoreConstants.UNDERSCORE +getProductList);
		return productList;
	}

	
	/**
	 * This methods updates the report type whether its l2,l3 or brand type
	 */
	private String updateReportType(String categoryName) {
		logDebug(CLS_NAME + "Inside Method : updateReportType : categoryName : "+categoryName);
		BBBPerformanceMonitor.start(CLS_NAME+BBBCoreConstants.UNDERSCORE+updateReportType);
		String typeOfReport = null;
		List<String> categoryList = new ArrayList<String>();
		categoryList = Arrays.asList(categoryName.split(BBBCoreConstants.GREATER_THAN_SYMBOL));
		if (categoryList != null) {
			if (categoryList.get(0).equals(BBBCoreConstants.BRAND) && categoryList.size() == 2) {
				typeOfReport = BBBCoreConstants.OBB;
			} else if ((categoryList.size() == 3 && categoryList.get(2).equals(BBBCoreConstants.ALL))
					|| (!categoryList.get(0).equals(BBBCoreConstants.BRAND) && categoryList.size() == 2 )) {
				typeOfReport = BBBCoreConstants.OBL2;
			} else if (categoryList.size() == 3 || (categoryList.size() == 4 && categoryList.get(3).equals(BBBCoreConstants.ALL))) {
				typeOfReport = BBBCoreConstants.OBL3;
			}
		}
		logDebug(CLS_NAME + "Exiting Method : updateReportType : typeOfReport : "+typeOfReport);
		BBBPerformanceMonitor.end(CLS_NAME+BBBCoreConstants.UNDERSCORE+updateReportType);
		return typeOfReport;
	}
	
	
}
