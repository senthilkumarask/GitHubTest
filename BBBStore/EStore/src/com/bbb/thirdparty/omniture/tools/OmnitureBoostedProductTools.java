package com.bbb.thirdparty.omniture.tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.thirdparty.omniture.vo.OmnitureQueueRequestVO;
import com.bbb.thirdparty.omniture.vo.OmnitureReportElementVO;
import com.bbb.thirdparty.omniture.vo.OmnitureReportMetricVO;
import com.bbb.thirdparty.omniture.vo.OmnitureReportSearchVO;
import com.bbb.thirdparty.omniture.vo.QueueRequestVO;
import com.bbb.thirdparty.omniture.vo.ReportDescriptionVO;

/**
 * Implements methods used in omniture report API call for product boosting
 * processing
 * 
 * @author Sapient
 *
 */
public class OmnitureBoostedProductTools extends OmnitureReportAPIToolsImpl {

	/**
	 * This method is used to prepare the Input for Queue Method
	 * 
	 * @param reportIndex
	 * @param concept
	 * @return OmnitureQueueRequestVO
	 * @throws BBBSystemException
	 * @throws BBBBusinessException 
	 */
	
	public OmnitureQueueRequestVO prepareInputForQueueRequest(QueueRequestVO queueRequestVO) throws BBBSystemException, BBBBusinessException {
		
		String concept=queueRequestVO.getConcept();
		int reportIndex=queueRequestVO.getReportIndex();
		
		logDebug( CLS_NAME + " [ prepareInputForQueueRequest ] method started for concept :: " + concept+" reportIndex is "+reportIndex);
		
		OmnitureQueueRequestVO requestVO = new OmnitureQueueRequestVO();
		ReportDescriptionVO reportDescription = new ReportDescriptionVO();
		// Setting The Elements
		List<OmnitureReportElementVO> elementVOs = new ArrayList<OmnitureReportElementVO>();
        
		//Report batch size from BCC
		//int batchSize = getBbbCatalogTools().getValueForConfigKey(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.OMNITURE_BATCH_SIZE, BBBCoreConstants.FIFTY_THOUSANDS);
				
        //Setting First Elements
		OmnitureReportElementVO elementVO = new OmnitureReportElementVO();
		OmnitureReportSearchVO searchVO = new OmnitureReportSearchVO();
		
		elementVO.setId(getElementId1());
		
		//int noOfSearchedTerms = getBbbCatalogTools().getValueForConfigKey(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.OMNITURE_TOTAL_SEARCHED_TERMS, BBBCoreConstants.TEN_THOUSANDS);
			
		String searchType = getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.L2_BOOSTING_REQUEST_TYPE_NOT, BBBCoreConstants.BLANK);
		
		searchVO.setType(searchType);
		
		String searchKeywordString = getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.OMNITURE_BOOSTED_PRODUCTS_KEYWORDS_TO_OMIT, BBBCoreConstants.BLANK);
		String[] keywordArray = searchKeywordString.split(BBBCoreConstants.COMMA);
		searchVO.setKeywords(Arrays.asList(keywordArray));
		
		/*//calculate number of records to request in current report
		int remainingRecordsToRequest = noOfSearchedTerms - reportIndex*batchSize;
		// In the more records to fetch then set top to batch size otherwise remaining record
		if(remainingRecordsToRequest > batchSize) {
			elementVO.setTop(batchSize);
		} else {
			elementVO.setTop(remainingRecordsToRequest);
		}*/
		elementVO.setTop(queueRequestVO.getTop());
		elementVO.setSearch(searchVO);
		//int startingWith = BBBCoreConstants.ONE + reportIndex*batchSize;
		elementVO.setStartingWith(queueRequestVO.getStartingWith());
		
		elementVOs.add(elementVO);
		
        // Setting Second Element
		elementVO = new OmnitureReportElementVO();
		elementVO.setId(getElementId2());
		//int noOfTopSearchedItems = getBbbCatalogTools().getValueForConfigKey(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.OMNITURE_TOP_SEARCHED_TERMS, BBBCoreConstants.FOUR);
		int noOfTopSearchedItems=queueRequestVO.getOmnitureReportConfigVo().getProductCount();
		elementVO.setTop(noOfTopSearchedItems);
		elementVOs.add(elementVO);
		
		reportDescription.setElements(elementVOs);
		
         //Setting Report Suite Information		
		String omnitureReportSuiteId = getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.OMNITURE_REPORT_SUITE_ID, BBBCoreConstants.BLANK);
		
		reportDescription.setReportSuiteID(omnitureReportSuiteId);
		logDebug( CLS_NAME + " [ prepareInputForQueueRequest ] omnitureReportSuiteId Value :: " + omnitureReportSuiteId);
		
		reportDescription.setDateFrom(queueRequestVO.getDateFrom());
		
		logDebug( CLS_NAME + " [ prepareInputForQueueRequest ] To Date Value :: " + queueRequestVO.getDateTo() + "From Date Vale :: " + queueRequestVO.getDateFrom());
		
		reportDescription.setDateTo(queueRequestVO.getDateTo());
		
		// Setting Metric ID
		String omnitureReportMetricId = getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.OMNITURE_REPORT_METRIC_ID, BBBCoreConstants.BLANK);
		logDebug( CLS_NAME + " [ prepareInputForQueueRequest ] omnitureReportMetricId Value :: " + omnitureReportMetricId);
		
		final List<OmnitureReportMetricVO>metrics = new ArrayList<OmnitureReportMetricVO>();
		OmnitureReportMetricVO omnitureReportMetVO = new OmnitureReportMetricVO();
		omnitureReportMetVO.setId(omnitureReportMetricId);
		metrics.add(omnitureReportMetVO);
		
		reportDescription.setMetrics(metrics);
		
		requestVO.setReportDescription(reportDescription);
		logDebug( CLS_NAME + " [ prepareInputForQueueRequest ] method ends :: " + requestVO);
		
		return requestVO;
	}
		
}