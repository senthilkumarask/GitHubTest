package com.bbb.thirdparty.omniture.tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.thirdparty.omniture.vo.OmnitureGetResponseVO;
import com.bbb.thirdparty.omniture.vo.OmnitureQueueRequestVO;
import com.bbb.thirdparty.omniture.vo.OmnitureReportDataVO;
import com.bbb.thirdparty.omniture.vo.OmnitureReportElementVO;
import com.bbb.thirdparty.omniture.vo.OmnitureReportMetricVO;
import com.bbb.thirdparty.omniture.vo.OmnitureReportSearchVO;
import com.bbb.thirdparty.omniture.vo.QueueRequestVO;
import com.bbb.thirdparty.omniture.vo.ReportDescriptionVO;
import com.bbb.utils.BBBUtility;

import atg.adapter.gsa.GSARepository;

/**
 * Implements methods used in omniture report API call for popular search terms
 * boosting processing
 * @author Sapient
 */

public class OmnitureBoostedPopularSearchTermsTools extends OmnitureReportAPIToolsImpl{
	
	private static final String PREPARE_INPUT_FOR_QUEUE_REQUEST = "prepareInputForQueueRequest";
	private String CLS_NAME = "OmnitureBoostedPopularSearchTermsTools";
	private String insertReportData = "insertReportData";
	private String removeExistingRecords = "removeExistingRecords";
	private String getKeyword = "getKeyword";
	private String keywordPatternToFilter;
	
	/**
	 * @return the keywordPatternToFilter
	 */
	public final String getKeywordPatternToFilter() {
		return keywordPatternToFilter;
	}


	/**
	 * @param keywordPatternToFilter the keywordPatternToFilter to set
	 */
	public final void setKeywordPatternToFilter(String keywordPatternToFilter) {
		this.keywordPatternToFilter = keywordPatternToFilter;
	}


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
		OmnitureReportSearchVO searchVO = new OmnitureReportSearchVO();
		
		// Setting The Elements
		List<OmnitureReportElementVO> elementVOs = new ArrayList<OmnitureReportElementVO>();
        
        //Setting First Element
		OmnitureReportElementVO elementVO = new OmnitureReportElementVO();
		
		elementVO.setId(getElementId1());
		elementVO.setTop(queueRequestVO.getTop());
		logDebug( CLS_NAME + " [ prepareInputForQueueRequest ] Config ID Value :: " + getElementId1() + "Top value is " + queueRequestVO.getTop());
		final String filteringType = getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.POPULAR_TERMS_BOOSTING_REQUEST_TYPE_NOT, BBBCoreConstants.BLANK);
		
		searchVO.setType(filteringType);
		
		
		final String keywordsToOmit = getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.POPULAR_TERMS_BOOSTING_KEYWORDS_TO_OMIT, BBBCoreConstants.BLANK);
		final String[] keywordArray = keywordsToOmit.split(BBBCoreConstants.COMMA);
		searchVO.setKeywords(Arrays.asList(keywordArray));
			
		logDebug( CLS_NAME + " [ prepareInputForQueueRequest ] filtering Type Value :: " + filteringType + "Keyword omiitted value is " + keywordsToOmit);
		elementVO.setSearch(searchVO);
		elementVO.setStartingWith(queueRequestVO.getStartingWith());
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
		final String omnitureReportMetricId = getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.POPULAR_SEARCH_BOOSTING_REPORT_METRIC_ID, BBBCoreConstants.BLANK); 
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
	 * This method will isert the data in the BBB_OMNITURE_REPORT_DATA TABLE.
	 * The logic will be inserted and the data for the older batch will be deleted on the successful execution of the data.
	 * ProductId column will be populated with the count for the popular search terms.
	 */
	@Override
	public int insertReportData(OmnitureGetResponseVO omnitureGetResponseVO, String reportID, String concept,
			String reportType) {
		logDebug(CLS_NAME + "[insertReportData] method starts" );
		
		long startTime = System.currentTimeMillis();
		logDebug(CLS_NAME + "[insertReportData] start time is " + startTime);
		BBBPerformanceMonitor.start(CLS_NAME +BBBCoreConstants.UNDERSCORE+ insertReportData);
		
		logDebug("Report Id " + reportID + " concept is :- " + concept + "reportType :- " + reportType);		
		
		PreparedStatement preparedStatement = null;
		Connection connection = null;
		boolean success = false;
		
		final List<OmnitureReportDataVO> dataList = omnitureGetResponseVO.getReport().getData();
		int record = 0;
		final int batchSize = getBbbCatalogTools().getValueForConfigKey(BBBCoreConstants.OMNITURE_BOOSTING, 
				BBBCoreConstants.INSERT_REPORT_BATCH_SIZE, BBBCoreConstants.DEFAULT_BATCH_SIZE);
		String keyword = null;
		try {
			
			connection = ((GSARepository)getOmnitureReportRepository()).getDataSource().getConnection();	
			connection.setAutoCommit(false);
			preparedStatement = connection.prepareStatement(INSERT_POPULAR_KEYWORD_DATA_SQL);
			for (OmnitureReportDataVO omnitureReportDataVO : dataList) {
				success = false;
                record++;				
				
				
				
				if(null != omnitureReportDataVO.getName()) {
					keyword = getKeyword(omnitureReportDataVO.getName());
					//BBBI - 4920
					if(BBBUtility.isEmpty(keyword)){
						continue;
					}
				}
				;
				logDebug("Keyword To be set :- " + keyword );				
			
				preparedStatement.setString(1, reportID);														// report Id
				preparedStatement.setString(2, omnitureGetResponseVO.getReport().getReportSuite().getId());		// report Suite
				preparedStatement.setString(3, reportType);														// report type
				preparedStatement.setString(4, omnitureGetResponseVO.getReport().getPeriod());					// Period
				preparedStatement.setString(5, concept);														// concept
				preparedStatement.setDouble(6, omnitureReportDataVO.getCounts()[0]);							// product Id
				preparedStatement.setString(7, keyword);														// Keyword 
				preparedStatement.setTimestamp(8, new Timestamp(System.currentTimeMillis()));					// Time stamp
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
				
				if(success && record >0){
					removeExistingRecords(concept, omnitureGetResponseVO,
							reportID, reportType);
				}
				
			} catch (SQLException e) {
				record=0;
				setErrorInOmnitureVO(omnitureGetResponseVO, e);
			}
		}
		
		BBBPerformanceMonitor.end(CLS_NAME +BBBCoreConstants.UNDERSCORE+ insertReportData);
		
		long endTime = System.currentTimeMillis();
		logDebug(CLS_NAME + "[insertReportData] end time is " + (endTime-startTime));
		logDebug(CLS_NAME + "[insertReportData] method ends" );
		
		return record;
	}

	
	
	/**
	 * This method is used the data to archive table and delete the entries from the Omniture report data table.
	 * @param concept
	 * @param record
	 * @param omnitureGetResponseVO
	 * @param reportID
	 * @param reportType
	 */
	private void removeExistingRecords(String concept, OmnitureGetResponseVO omnitureGetResponseVO,
			String reportID, String reportType) {
		
		logDebug(CLS_NAME + "removeExistingRecords starts");
		
		logDebug("Concept: -" + concept + " reportId :- " + reportID + " reportType :- " + reportType );
		BBBPerformanceMonitor.start(CLS_NAME +BBBCoreConstants.UNDERSCORE+ removeExistingRecords);
		PreparedStatement archiveDataStatement = null;
		PreparedStatement deleteDataStatement = null;			
		
		Connection connection = null;
		boolean success = false;
		
		try {
			connection = ((GSARepository) getOmnitureReportRepository()).getDataSource().getConnection();
			
			logDebug("Archiving popular search data in to archive table");
			
			archiveDataStatement = connection.prepareStatement(ARCHIVE_POPULAR_SEARCH_DATA);
			archiveDataStatement.setString(1,concept);
			archiveDataStatement.setString(2,reportID);
			archiveDataStatement.setString(3, reportType);
			archiveDataStatement.setString(4, reportID);
			int recordsMoved=archiveDataStatement.executeUpdate();
			// get count of rows inserted in archive table which needs to be send in email
			omnitureGetResponseVO.setRecordsMoved(recordsMoved);
			logDebug("Archiving finished And Records Moved is"+ recordsMoved);
		
			
			logDebug("Deleting existing data from Omniture report data table");
	
			deleteDataStatement = connection.prepareStatement(DELETE_POPULAR_SEARCH_DATA);		
			deleteDataStatement.setString(1, concept);
			deleteDataStatement.setString(2,reportID);
			deleteDataStatement.setString(3, reportType);
			deleteDataStatement.setString(4, reportID);
			deleteDataStatement.executeQuery();
			
			logDebug("Deleting existing data finished");
			success=true;	
		
		}catch (SQLException e) {
			setErrorInOmnitureVO(omnitureGetResponseVO, e);
			BBBPerformanceMonitor.cancel(CLS_NAME +BBBCoreConstants.UNDERSCORE+ removeExistingRecords);
			
		} finally {
			try {
				if (archiveDataStatement != null) {					
					archiveDataStatement.close();
				}
				if(deleteDataStatement!=null){
					deleteDataStatement.close();
				}
				if(connection!=null){						
					if(success){							
						connection.commit();
					}else{
						connection.rollback();
					}
					if(!connection.isClosed()){
						connection.close();
					}
				}
				
			} catch (SQLException e) {
				logError("SQL Exception ocurred while closing the statement", e);	
				BBBPerformanceMonitor.cancel(CLS_NAME +BBBCoreConstants.UNDERSCORE+ removeExistingRecords);
			}
			
		}
		BBBPerformanceMonitor.end(CLS_NAME +BBBCoreConstants.UNDERSCORE+ removeExistingRecords);
		logDebug(CLS_NAME + "removeExistingRecords ends");
		
	}


	/**
	 * This method returns filtered keyword by replacing special characters with space.
	 * @param name
	 * @return
	 */
	private String getKeyword(String keyword) {
		
		logDebug(CLS_NAME + "getKeyword starts : Keyword To Filer : " +keyword );
		BBBPerformanceMonitor.start(CLS_NAME +BBBCoreConstants.UNDERSCORE+ getKeyword);
		if (keyword.contains(BBBCoreConstants.SLASH)){
			keyword = keyword.split(BBBCoreConstants.SLASH)[0];
		}
		final String tempKeywordPatternConfigValue = getBbbCatalogTools().getConfigKeyValue(
                BBBCoreConstants.OMNITURE_BOOSTING,
                BBBCoreConstants.POPULAR_SEARCH_KEYWORD_FILTER_PATTERN,
                this.getKeywordPatternToFilter());

        keyword = StringEscapeUtils.unescapeHtml(keyword).replaceAll(tempKeywordPatternConfigValue, BBBCoreConstants.SPACE);
        //BBBI - 4920 | Trimming the keyword to remove the trailing spaces
        keyword = keyword.trim();
		logDebug(CLS_NAME + "getKeyword ends : Filtered Keyword : " +keyword );
		BBBPerformanceMonitor.end(CLS_NAME +BBBCoreConstants.UNDERSCORE+ getKeyword);
		return keyword;
		
	}

}

	

