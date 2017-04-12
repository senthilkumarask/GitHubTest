package com.bbb.thirdparty.omniture.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.MessageDigest;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import atg.adapter.gsa.GSARepository;
import atg.core.util.Base64;
import atg.core.util.StringUtils;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.QueryOptions;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.SortDirective;
import atg.repository.SortDirectives;
import atg.repository.rql.RqlStatement;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBConfigTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.httpquery.HTTPCallInvoker;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.thirdparty.omniture.vo.ArchiveDataVO;
import com.bbb.thirdparty.omniture.vo.OmnitureGetRequestVO;
import com.bbb.thirdparty.omniture.vo.OmnitureGetResponseVO;
import com.bbb.thirdparty.omniture.vo.OmnitureQueueRequestVO;
import com.bbb.thirdparty.omniture.vo.OmnitureQueueResponseVO;
import com.bbb.thirdparty.omniture.vo.OmnitureReportConfigVo;
import com.bbb.thirdparty.omniture.vo.OmnitureReportDataVO;
import com.bbb.thirdparty.omniture.vo.OmnitureReportEmailContentVO;
import com.bbb.thirdparty.omniture.vo.OmnitureReportStatus;
import com.bbb.thirdparty.omniture.vo.QueueRequestVO;
import com.bbb.thirdparty.omniture.vo.ReportStatusVO;
import com.bbb.utils.BBBUtility;

/**
 * Implements common methods used in omniture report API call invocation 
 * processing
 * 
 * @author Sapient
 *
 */
public abstract class OmnitureReportAPIToolsImpl extends BBBGenericService implements OmnitureReportAPITools {
	
	private MutableRepository omnitureReportRepository;
	private String queuedReportQuery;
	private String currentQueuedReportQuery;
	private String checkLatestReportQuery;
	private HTTPCallInvoker httpCallInvoker;
	private BBBConfigTools bbbCatalogTools;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private String elementId1;
	private String elementId2;
	boolean sendEmailOnPurgeFailure;
	private String omnitureBoostedDataPurgeQuery;
	private String omnitureBoostedDataPurgeSelectQuery;
	private String omnitureEndPoint;
	private String archiveData = "archiveData";
	private String getReportDetailsForAlreadyQueued = "getReportDetailsForAlreadyQueued";
	private String archiveOlderRecords = "archiveOlderRecords";
	
	/**
	 * @return omnitureEndPoint
	 */
	public String getOmnitureEndPoint() {
		return omnitureEndPoint;
	}

	/**
	 * @param omnitureEndPoint the omnitureEndPoint to set
	 */
	public void setOmnitureEndPoint(String omnitureEndPoint) {
		this.omnitureEndPoint = omnitureEndPoint;
	}

	public MutableRepository getOmnitureReportRepository() {
		return omnitureReportRepository;
	}

	public void setOmnitureReportRepository(
			MutableRepository omnitureReportRepository) {
		this.omnitureReportRepository = omnitureReportRepository;
	}
	
	public String getQueuedReportQuery() {
		return queuedReportQuery;
	}

	public void setQueuedReportQuery(String queuedReportQuery) {
		this.queuedReportQuery = queuedReportQuery;
	}

	public String getCurrentQueuedReportQuery() {
		return currentQueuedReportQuery;
	}

	public void setCurrentQueuedReportQuery(String currentQueuedReportQuery) {
		this.currentQueuedReportQuery = currentQueuedReportQuery;
	}

	public String getCheckLatestReportQuery() {
		return checkLatestReportQuery;
	}

	public void setCheckLatestReportQuery(String checkLatestReportQuery) {
		this.checkLatestReportQuery = checkLatestReportQuery;
	}

	public HTTPCallInvoker getHttpCallInvoker() {
		return httpCallInvoker;
	}

	public void setHttpCallInvoker(HTTPCallInvoker httpCallInvoker) {
		this.httpCallInvoker = httpCallInvoker;
	}
	
	public BBBConfigTools getBbbCatalogTools() {
		return bbbCatalogTools;
	}

	public void setBbbCatalogTools(BBBConfigTools bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}

	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}
	

	public String getElementId1() {
		return elementId1;
	}

	public void setElementId1(String elementId1) {
		this.elementId1 = elementId1;
	}

	public String getElementId2() {
		return elementId2;
	}

	public void setElementId2(String elementId2) {
		this.elementId2 = elementId2;
	}
	
	public boolean isSendEmailOnPurgeFailure() {
		return sendEmailOnPurgeFailure;
	}

	public void setSendEmailOnPurgeFailure(boolean sendEmailOnPurgeFailure) {
		this.sendEmailOnPurgeFailure = sendEmailOnPurgeFailure;
	}
	
	/**
     * @return the omnitureBoostedDataPurgeQuery
     */
	public String getOmnitureBoostedDataPurgeQuery() {
		return omnitureBoostedDataPurgeQuery;
	}

	/**
	 * @param omnitureBoostedDataPurgeQuery
	 * 			the omnitureBoostedDataPurgeQuery to set
	 */
	public void setOmnitureBoostedDataPurgeQuery(String omnitureBoostedDataPurgeQuery) {
		this.omnitureBoostedDataPurgeQuery = omnitureBoostedDataPurgeQuery;
	}

	/**
	 * @return omnitureBoostedDataPurgeSelectQuery
	 */
	public String getOmnitureBoostedDataPurgeSelectQuery() {
		return omnitureBoostedDataPurgeSelectQuery;
	}
		
	/**
	 * @param omnitureBoostedDataPurgeSelectQuery
	 */
	public void setOmnitureBoostedDataPurgeSelectQuery(String omnitureBoostedDataPurgeSelectQuery) {
		this.omnitureBoostedDataPurgeSelectQuery = omnitureBoostedDataPurgeSelectQuery;
	}
	
	/** 
	 * This method retrieves the already queued report form the database
	 * 
	 * @param rqlQuery
	 * @param params
	 * @param viewName
	 * @param repository
	 * @return RepositoryItem[]
	 * @throws BBBSystemException,
	 * @throws BBBBusinessException, 
	 */

	public RepositoryItem[] executeRQLQuery(final String rqlQuery, final Object[] params, final String viewName, final MutableRepository repository) {

		RqlStatement statement = null;
		RepositoryItem[] queryResult = null;
		logDebug("OmnitureReportAPIToolsImpl.executeRQLQuery() method start for rqlQuery :: " + rqlQuery + " viewName :: " + viewName);
		
		if (isLoggingDebug()) {
			int index = 0;
			for (Object rqlParam : params) {
				logDebug("params[" + index + "] :: " + rqlParam );
				index++;
			}
		}
		
		if (rqlQuery != null) {
			if (repository != null) {
				try {
					statement = RqlStatement.parseRqlStatement(rqlQuery);
					final RepositoryView view = repository.getView(viewName);
					if (view == null) {
						logError(viewName + " view is null from executeRQLQuery of OmnitureBoostedProductTools");
					}
					queryResult = statement.executeQuery(view, params);

					if (queryResult == null) {
						logDebug("No results returned for query ["	+ rqlQuery + "]");
					}

				} catch (final RepositoryException e) {
					logError(" Repository Exception [Unable to retrieve data] from executeRQLQuery of OmnitureBoostedProductTools", e);
				}
			} else {
				logError(" Repository is null from executeRQLQuery of OmnitureBoostedProductTools");
			}
		} else {
		      logError(" Query String is null from executeRQLQuery of OmnitureBoostedProductTools");
		}

		logDebug("OmnitureReportAPIToolsImpl.executeRQLQuery() method ends");
		return queryResult;
	}
	

	/** This method is used to Queue and Get the report id for individual concept from Omniture
	 * @param concept
	 * @param reportType
	 *//*
	public void queueOrGetReport(String concept,String reportType) {
		
		
		logDebug( CLS_NAME + " [ queueOrGetReport ] method starts for concept :: " + concept +" and report type is "+reportType);
				
		if(BBBUtility.isEmpty(concept) || BBBUtility.isEmpty(reportType)) {
			return;
		}
		//Get the already queued reports
		getReports(concept, reportType);
		
		//queue reports for given concept and type in the case where no report already queued for this type and concept
		queueReports(concept, reportType);
			
		logDebug( CLS_NAME + " [ queueOrGetReport ] method ends for concept :: " + concept+" and report type is "+reportType);
	
	}*/

	/**
	 * @param concept
	 * @param reportType
	 *//*
	protected void queueReports(String concept, String reportType) {
		logDebug( CLS_NAME + " [ queueReports ] method starts for concept :: " + concept +" and report type is "+reportType);
		final Calendar cal = Calendar.getInstance();
		final Date currentDate = DateUtils.truncate(cal.getTime(), Calendar.DAY_OF_MONTH);
		
		final Object[] queueParams = new Object[BBBCoreConstants.FOUR];
		queueParams[0] = BBBCoreConstants.OMNITURE_REPORT_QUEUED;
		queueParams[1] = currentDate;
		queueParams[2] = concept;
		queueParams[3] = reportType;
		
		final RepositoryItem [] curentRepoItemRes = this.executeRQLQuery(this.getCurrentQueuedReportQuery(), queueParams, BBBCoreConstants.REPORT_STATUS, this.getOmnitureReportRepository());
		if (curentRepoItemRes == null || curentRepoItemRes.length == 0) {
			
			logDebug( CLS_NAME + " [ queueReports ] || No Reports found In Queued Status for Concept :: " + concept + " Queued Date :: " + currentDate);
			
            int numOfReportsToQueue = calculateNumberOfReports();
            List<OmnitureReportEmailContentVO> reportEmailContentList = new ArrayList<OmnitureReportEmailContentVO>();
			for (int reportQueueIndex=0; reportQueueIndex < numOfReportsToQueue; reportQueueIndex++) {
				 waitForOmnitureCall(reportType,concept);
			     boolean queueSuccess = doQueueReport(concept,reportType,reportQueueIndex, reportEmailContentList);
			     //In the case any one of the reports from batch is not queued successfully don't queue reports further
			     if(!queueSuccess) {
			    	// As per Queue + Fail Logic COS, - If all attempts fail, then we move on to next report/batch
				    // nth report is abandoned after 3 attempts, with 30 secs wait time and move to next report
			    	 logError("Queue report for the report index# " + reportQueueIndex + " for concept " + concept + " failed even after max retry attempts.");
			    	//break;
			     }
			     
			} 
			
			sendEmail(concept, QUEUE_FLOW, reportEmailContentList);
			
		} else {
			logInfo(CLS_NAME + " [ queueReports ] || Report already Queued for Concept :: " + concept + " Queued Date :: " + currentDate);
		}
		logDebug( CLS_NAME + " [ queueReports ] method ends for concept :: " + concept +" and report type is "+reportType);
	}*/

	/**
	 * Get Already queued reports
	 * 
	 * @param concept
	 * @param reportType
	 * @return
	 */
	/*protected void getReports(String concept, String reportType) {
		
		logDebug( CLS_NAME + " [ getReports ] method starts for concept :: " + concept +" and report type is "+reportType);
		
		final Calendar cal = Calendar.getInstance();
		final Date currentDate = DateUtils.truncate(cal.getTime(), Calendar.DAY_OF_MONTH);
		
	    final Object[] params = new Object[BBBCoreConstants.FOUR];
		params[0] = BBBCoreConstants.OMNITURE_REPORT_QUEUED;
		params[1] = currentDate;
		params[2] = concept;
		params[3] = reportType;
		final RepositoryItem [] omniReportRepoRes = this.executeRQLQuery(this.getQueuedReportQuery(), params, BBBCoreConstants.REPORT_STATUS, this.getOmnitureReportRepository());
		
		if (omniReportRepoRes != null && omniReportRepoRes.length > 0) {
			int numberOfReports = omniReportRepoRes.length;
			logDebug( CLS_NAME + " [ getReports ] || Found  :: " + numberOfReports + " Reports In Queued Status ");
			
			Map <String,Integer> highestRankMap = new HashMap<String, Integer>(); 
			List<OmnitureReportEmailContentVO> reportEmailContentList = new ArrayList<OmnitureReportEmailContentVO>();
			boolean firstReport = true;
			
			for (int reportIndex = 0; reportIndex < numberOfReports; reportIndex++) {
				waitForOmnitureCall(reportType,concept);
				final MutableRepositoryItem omniReportItem = (MutableRepositoryItem) omniReportRepoRes[reportIndex];
				String reportStatus = (String)omniReportItem.getPropertyValue(BBBCoreConstants.REPORT_OP_STATUS);
				final Date reportQueuedDate = (Date) omniReportItem.getPropertyValue(BBBCoreConstants.REPORT_QUEUED_TIME);
				final String reportId = (String)omniReportItem.getPropertyValue(BBBCoreConstants.REPORT_ID);
				
				final boolean isLatestReport = checkForLatestReportToGet(reportQueuedDate, currentDate, concept, reportId,reportType);			
				if (!BBBUtility.isEmpty(reportStatus) && BBBCoreConstants.OMNITURE_REPORT_QUEUED.equalsIgnoreCase(reportStatus) && isLatestReport) {
					
					reportStatus = getReportDetailsForAlreadyQueued(concept, omniReportItem,firstReport,highestRankMap,reportEmailContentList);
					OmnitureReportEmailContentVO omnitureReportEmailContentVO = reportEmailContentList.get(reportIndex);
					//In the case first report in batch is not ready don't process any report in batch
					if(firstReport && BBBCoreConstants.REPORT_NOT_READY.equalsIgnoreCase(reportStatus)) {
						
						logInfo("First report "+reportId+" is not ready in batch, skipping the batch processing for concept :: " + concept +" and report type is "+reportType);
						if(omnitureReportEmailContentVO != null) {
							omnitureReportEmailContentVO.setExceptionDetails("First report "+reportId+" is not ready in batch, skipping the batch processing");
						}
						
						break;
					} else if (BBBCoreConstants.REPORT_NOT_READY.equalsIgnoreCase(reportStatus) || BBBCoreConstants.OMNITURE_REPORT_FAILED.equalsIgnoreCase(reportStatus)) {
						vlogError("Report "+reportId+" is "+reportStatus+" so marking remaining reports in batch as failed");
						if(omnitureReportEmailContentVO != null) {
							omnitureReportEmailContentVO.setExceptionDetails("Report "+reportId+" is "+reportStatus+" so marking remaining reports in batch as failed");
						}
						//If any report in batch is failed or not ready then mark the remaining reports in batch as failed
						markRemainingReportFailed(reportId,omniReportRepoRes, reportIndex, reportEmailContentList);
						break;
					}
				}
				firstReport = false;
			}
			sendEmail(concept, GET_FLOW, reportEmailContentList);	
		} else {
			logInfo(CLS_NAME + " [ getReports ] || No Report Available to Get for Concept :: " + concept + " In Queued Status ");
		}
		logDebug( CLS_NAME + " [ getReports ] method ends for concept :: " + concept +" and report type is "+reportType);
	}*/
	
	/** This method is used to invoke Queue method and insert report status for individual concept from Omniture
	 * Also sends email in case of queue success/failure
	 * @param concept
	 * @param reportType
	 * @param reportIndex
	 * @param reportEmailContentList
	 * @return queueSuccess
	 */
	@SuppressWarnings("finally")
	//QueueRequestVO requestVO, OmnitureReportEmailContentVO reportEmailContentVO
	public boolean doQueueReport(QueueRequestVO requestVO, OmnitureReportEmailContentVO reportEmailContentVO) {
		String concept=requestVO.getConcept();
		String reportType=requestVO.getReportType();
		int reportIndex=requestVO.getReportIndex();
		logDebug( CLS_NAME + " [ doQueueReport ] method starts for concept :: " + concept + " and report type " + reportType +" reportIndex is "+reportIndex);
		
		OmnitureQueueResponseVO omnitureQueueResponseVO = null;
		boolean queueSuccess = false;
		String omnitureReportQueue = getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.OMNITURE_REPORT_QUEUE, BBBCoreConstants.BLANK);
		String exceptionDetails = BBBCoreConstants.BLANK;
		String reportStatus = BBBCoreConstants.OMNITURE_REPORT_FAILED;
		try {
			// Re-factored code to Retry Logic
				omnitureQueueResponseVO = getOmnitureReportQueueResponse(requestVO);
				logDebug(CLS_NAME + " [ doQueueReport ] - omnitureQueueResponseVO received from Omniture API Call for [" + omnitureReportQueue + "] : " + omnitureQueueResponseVO);
				queueSuccess = insertReportStatus(omnitureQueueResponseVO,requestVO);
				if (queueSuccess) {
					logInfo("Report With Report ID :: " + omnitureQueueResponseVO.getReportID() + " Queued successfully for Concept :: " + concept);
					reportStatus = BBBCoreConstants.OMNITURE_REPORT_QUEUED;
				} else {
					logError("Report for Concept :: " + concept + " not queued.");
				}
				
		} catch (BBBSystemException | BBBBusinessException | IOException e) {
			logError("BBBSystemException or BBBBusinessException or IOException  Occurred while Queueing Report ", e);
			exceptionDetails = getExceptionTrace(e);
		}  catch (Exception exception) {
			logError("Exception Occurred while Queueing Report ", exception);
			exceptionDetails = getExceptionTrace(exception);
		} 
		finally {
			 
			reportEmailContentVO.setExceptionDetails(exceptionDetails);
			reportEmailContentVO.setReportType(reportType);
			reportEmailContentVO.setOpStatus(reportStatus);
			reportEmailContentVO.setConcept(concept);
			reportEmailContentVO.setQueuedDate(new Date());
			reportEmailContentVO.setReportId(omnitureQueueResponseVO != null ? omnitureQueueResponseVO.getReportID() : BBBCoreConstants.BLANK);
			logDebug( CLS_NAME + " [ doQueueReport ] method ends for concept :: " + concept + " and report type " + reportType);
			
			return queueSuccess;
		}
		
	}
	
	/** 
	 * This method is used to retrive stack trace from exception
	 * @param exception
	 * @return
	 * @throws IOException
	 */
	public String getExceptionTrace(Exception exception) {
		final Writer result = new StringWriter();
		PrintWriter printWriter = null;
		String errorMessage = BBBCoreConstants.BLANK;
		try {
			printWriter = new PrintWriter(result);
			exception.printStackTrace(printWriter);
			errorMessage = result.toString();
		} catch (Exception ex) {
			logError("Some exception occurred", ex);
		}finally {
			try {
				printWriter.close();
				result.close();
			} catch (IOException ex) {
				logError("IO Exception occurred", ex);
			}
		}
		return errorMessage;
	}
	
	/**
	 * This method is used to prepare the Input for Queue Method
	 * 
	 * @param reportIndex
	 * @param concept
	 * @return OmnitureQueueRequestVO
	 * @throws BBBSystemException
	 * @throws BBBBusinessException 
	 */
	
	public abstract OmnitureQueueRequestVO prepareInputForQueueRequest(QueueRequestVO requestVO) throws BBBSystemException, BBBBusinessException;
	
	/** This method is used to check if the report is latest one.
	 * If report is latest then return true otherwise return false
	 * @param reportQueuedDate
	 * @param currentDate
	 * @param concept
	 * @param reportId 
	 * @param reportType
	 * @return boolean
	 */
	private boolean checkForLatestReportToGet( final Date reportQueuedDate, final Date currentDate, final String concept, final String reportId,final String reportType) {
		
		logDebug( CLS_NAME + " [ checkForLatestReport ] method starts for concept :: " + concept + " and report type is " + reportType + " Report Queued Date :: " + reportQueuedDate + "Current Date :: " + currentDate);
		
		boolean latestReportToGet = true;
		final Object[] params = new Object[BBBCoreConstants.FOUR];
		params[0] = reportQueuedDate;
		params[1] = currentDate;
		params[2] = concept;
		params[3] = reportType;
		
		RepositoryItem [] alreadyQueuedReportsItem = this.executeRQLQuery(this.getCheckLatestReportQuery(), params, BBBCoreConstants.REPORT_STATUS, this.getOmnitureReportRepository());
		if (alreadyQueuedReportsItem != null && alreadyQueuedReportsItem.length > 0) {
			logInfo( CLS_NAME + " [ checkForLatestReportToGet ] || Report  :: " + reportId + " is not latest hence will be skipped ");
			latestReportToGet = false;
		} 
		
		logDebug( CLS_NAME + " [ checkForLatestReport ] method ends for concept :: " + concept + " and report type is " + reportType + " Is Report Latest :: " + latestReportToGet);
		
		return latestReportToGet;
		
	}
	
	/**This method is used to invoke Get method and does the report processing for individual concept from Omniture
	 * Also sends email in case of queue success/failure
	 * @param concept
	 * @param omniReportStatusItem
	 * @param firstReport
	 * @param highestRankMap
	 * @param reportEmailContentList
	 * @return reportStatus
	 */
	public String getReportDetailsForAlreadyQueued(String concept, MutableRepositoryItem omniReportStatusItem, OmnitureReportEmailContentVO reportEmailContent,List<OmnitureReportEmailContentVO>  archiveFailedStatusEmailVOList) {
		
		logDebug( CLS_NAME + " [ getReportDetailsForAlreadyQueued ] method starts for concept :: " + concept);
		
		BBBPerformanceMonitor.start(BBBPerformanceConstants.OMNITURE_REPORT_TOOLS_IMPL + getReportDetailsForAlreadyQueued);
		String reportStatus = BBBCoreConstants.OMNITURE_REPORT_FAILED;
		
		OmnitureGetRequestVO omnitureGetRequestVO = null;
		OmnitureGetResponseVO omnitureGetResponseVO = null;
		int recordsUpdated = BBBCoreConstants.ZERO;
		boolean getSuccess = false;
		String exceptionDetails = BBBCoreConstants.BLANK;
		String reportId = (String) omniReportStatusItem.getPropertyValue(BBBCoreConstants.REPORT_ID);
		
		String reportType = (String) omniReportStatusItem.getPropertyValue(BBBCoreConstants.METHOD_TYPE);
		logDebug( CLS_NAME + " [ getReportDetailsForAlreadyQueued ] method :: Report Id available to Get is :: " + reportId + " for concept :: " + concept);
		
		try {
			
			omnitureGetRequestVO = prepareInputForGetRequest(reportId);

			/***
			 * BBBI-3039 # Invoke Omniture report GET API which now includes the
			 * retry logic. IF FIRST attempt is failed then maximum 3 [BCC
			 * configurable] retry attempts allowed.
			 */
			omnitureGetResponseVO = invokeOmnitureReportGetAPI(omnitureGetRequestVO);
		
			if (omnitureGetResponseVO != null) {
				logDebug("Omniture response "+omnitureGetResponseVO);
				if (BBBUtility.isEmpty(omnitureGetResponseVO.getError()) && BBBUtility.isEmpty(omnitureGetResponseVO.getError_description())) {
					recordsUpdated = insertReportData(omnitureGetResponseVO, omnitureGetRequestVO.getReportID(), concept, reportType);
					
					if(recordsUpdated == BBBCoreConstants.ZERO 
						&& BBBUtility.isEmpty(omnitureGetResponseVO.getError()) && BBBUtility.isEmpty(omnitureGetResponseVO.getError_description())
						&& BBBUtility.isListEmpty(omnitureGetResponseVO.getReport().getData()))
						{
							omnitureGetResponseVO.setError(NO_DATE_MSG);
							omnitureGetResponseVO.setError_description(NO_DATE_MSG);
						}
				}
				getSuccess = updateReportStatus(omnitureGetResponseVO, omnitureGetRequestVO.getReportID(), omniReportStatusItem, concept);
				exceptionDetails = omnitureGetResponseVO.getError_description();
				if (getSuccess && recordsUpdated != 0 && BBBUtility.isEmpty(omnitureGetResponseVO.getError()) && BBBUtility.isEmpty(omnitureGetResponseVO.getError_description())) {
					// In the case first report in batch successful archive  older records.
					//Calling this twice to archive and delete in one run
					
						//In first run,archive  older record for last modified date less than x days 
					   archiveOlderRecords(reportType,concept,BBBCoreConstants.ONE,reportId,archiveFailedStatusEmailVOList);
					   //In second run,archive older  records greater than threshold for particular report type and concept
					   archiveOlderRecords(reportType,concept,BBBCoreConstants.ZERO,reportId,archiveFailedStatusEmailVOList);
					
					
					reportStatus = BBBCoreConstants.OMNITURE_REPORT_SUCCESS;
				}else if(BBBCoreConstants.REPORT_NOT_READY.equalsIgnoreCase(omnitureGetResponseVO.getError())) {
					reportStatus = BBBCoreConstants.REPORT_NOT_READY;
				}
				
			} else {
				omnitureGetResponseVO = new OmnitureGetResponseVO();
				omnitureGetResponseVO.setError(GENERAL_GET_EXCEPRION_MSSG);
				omnitureGetResponseVO.setError_description(GENERAL_GET_EXCEPRION_MSSG);
				exceptionDetails = GENERAL_GET_EXCEPRION_MSSG;
				updateReportStatus(omnitureGetResponseVO, omnitureGetRequestVO.getReportID(), omniReportStatusItem, concept);
			}
		} catch (BBBSystemException | BBBBusinessException | IOException ex ) {
			logError("BBBSystemException Occurred while Getting Report ", ex);
			String exceptionMessage=ex.getMessage();
			if(StringUtils.isBlank(exceptionMessage))
			{
				exceptionMessage=	GENERAL_GET_EXCEPRION_MSSG;
			}
			omnitureGetResponseVO = new OmnitureGetResponseVO();
			omnitureGetResponseVO.setError(exceptionMessage);
			omnitureGetResponseVO.setError_description(exceptionMessage);
			exceptionDetails = getExceptionTrace(ex);
			updateReportStatus(omnitureGetResponseVO, omnitureGetRequestVO.getReportID(), omniReportStatusItem, concept);
		}  catch (Exception ex) {
			logError("Exception Occurred while Getting Report", ex);
			omnitureGetResponseVO = new OmnitureGetResponseVO();
			omnitureGetResponseVO.setError(ex.getStackTrace().toString().length() > 100 ? ex.getStackTrace().toString().substring(0, 100): ex.getStackTrace().toString());
			omnitureGetResponseVO.setError_description(ex.getStackTrace().toString().length() > 500 ? ex.getStackTrace().toString().substring(0, 500): ex.getStackTrace().toString());
			exceptionDetails = getExceptionTrace(ex);
			updateReportStatus(omnitureGetResponseVO, omnitureGetRequestVO.getReportID(), omniReportStatusItem, concept);
		} finally {			
			
					
			try{
					/*
					 * Condition to check if the first report Id for Popular Searches is failed.
					 */
					if(recordsUpdated == 0 && omniReportStatusItem.getPropertyValue(BBBCoreConstants.REPORT_BATCH_SEQ) != null &&  omniReportStatusItem.getPropertyValue(BBBCoreConstants.REPORT_BATCH_SEQ).equals(0) 
							&& reportType.equals(BBBCoreConstants.POPULAR_SEARCHES)){
						
						cancelReports(omniReportStatusItem, reportId,reportEmailContent, concept);				
						
					}else{
						reportEmailContent.setReportId(reportId);
						//Set number of records updated in report data table and send in email
						reportEmailContent.setRecordsUpdated(recordsUpdated);
						//Set number of records moved in archive table and send in email
						if(omnitureGetResponseVO != null){
						reportEmailContent.setRecordsMoved(omnitureGetResponseVO.getRecordsMoved());
						}
						reportEmailContent.setExceptionDetails(exceptionDetails);
						if(omnitureGetResponseVO != null && omnitureGetResponseVO.getReport() != null && omnitureGetResponseVO.getReport().getData() != null){
						reportEmailContent.setNoOfSearchTerms(omnitureGetResponseVO.getReport().getData().size());
						}
						reportEmailContent.setOpStatus(reportStatus);				
					}
			 }catch(Exception exception)
			 {
				 logError(CLS_NAME + " [ getReportDetailsForAlreadyQueued ] method ::exception Occurred in finally block:", exception);
			 }
			BBBPerformanceMonitor.end(BBBPerformanceConstants.OMNITURE_REPORT_TOOLS_IMPL+ getReportDetailsForAlreadyQueued);
		}
		logDebug( CLS_NAME + " [ getReportDetailsForAlreadyQueued ] method :: Report status is :: " + reportStatus);
		return reportStatus;
	}

	/**
	 * This methods cancels all the report id's for the particular batch
	 * except the report id with 0 sequence for this batch.
	 * This method is created for canceling the other report Ids if the first report Id consumption is falied for Poplar SEarches.
	 * @param omniReportStatusItem
	 * @param reportId
	 * @param reportEmailContent
	 * @param concept
	 */
	private void cancelReports(MutableRepositoryItem omniReportStatusItem, String reportId, OmnitureReportEmailContentVO reportEmailContent, String concept) {
		logDebug(CLS_NAME + "[cancelReports] method starts : concept" + concept + " & report Id : " + reportId);
		BBBPerformanceMonitor.start(CLS_NAME + "cancelReports");
		try {
			RepositoryItem[] items = getReportStatusByReportIdOrBatchId(null, (String) omniReportStatusItem.getPropertyValue(BBBCoreConstants.REPORT_BATCH_ID));
			boolean reportCanceled = false;
			StringBuffer reportsToBeCanceled = new StringBuffer();	
			int maxRetryAttempts = 3;
			int attempts = 0;
			for(RepositoryItem item:items){
				String repId = (String)item.getPropertyValue(BBBCoreConstants.REPORT_ID); 
				if(!repId.equalsIgnoreCase(reportId)){
					
					reportsToBeCanceled.append(repId);
					reportsToBeCanceled.append(BBBCoreConstants.COMMA);
					reportCanceled = cancelQueuedReport(repId);
					MutableRepositoryItem mutableRepository = (MutableRepositoryItem) item;					
					
					if(reportCanceled){
						logDebug("Inside CLS_NAME: Method : cancelReports : Reports are succesfully canceled");
						mutableRepository.setPropertyValue(BBBCoreConstants.REPORT_OP_STATUS, BBBCoreConstants.OMNITURE_REPORT_STATUS_CANCEL);
						reportEmailContent.setExceptionDetails(REPORT_CANCEL_MSG);
						reportEmailContent.setOpStatus(BBBCoreConstants.OMNITURE_REPORT_STATUS_CANCEL);			
					}else{
						logDebug("Inside CLS_NAME: Method : cancelReports : Reports are not succesfully canceled : Retry logic in place");
						inner:while(attempts < maxRetryAttempts){
							
							attempts++;
							waitForNextRetryAttempt(reportId, concept);
							
							reportCanceled = cancelQueuedReport(reportId);
							
							if(reportCanceled){
								logDebug("Inside CLS_NAME: Method : cancelReports : Reports are succesfully canceled : Attempts : "+ attempts);
								mutableRepository.setPropertyValue(BBBCoreConstants.REPORT_OP_STATUS, BBBCoreConstants.OMNITURE_REPORT_STATUS_CANCEL);
								reportEmailContent.setExceptionDetails(REPORT_CANCEL_MSG);
								reportEmailContent.setOpStatus(BBBCoreConstants.OMNITURE_REPORT_STATUS_CANCEL);	
								break inner;
							}
						}
					
						if(!reportCanceled){
							logDebug("Inside CLS_NAME: Method : cancelReports : Reports are not canceled");
							mutableRepository.setPropertyValue(BBBCoreConstants.REPORT_OP_STATUS, BBBCoreConstants.OMNITURE_REPORT_FAILED);
							mutableRepository.setPropertyValue(BBBCoreConstants.REPORT_ERROR_DESC, RETRY_FAIL_MSG);
							reportEmailContent.setExceptionDetails(RETRY_FAIL_MSG);
							reportEmailContent.setOpStatus(BBBCoreConstants.OMNITURE_REPORT_FAILED);
						}
					}
				}
			}
			
			reportsToBeCanceled.setLength(reportsToBeCanceled.length()-1);
			
			reportEmailContent.setReportId(reportsToBeCanceled.toString());
			reportEmailContent.setRecordsUpdated(0);			
			
		} catch (RepositoryException e) {
			logError("Repository Exception while fetching reports for batch id" + e);
		} catch (BBBSystemException e) {
			logError("System exception while canceling the queue" + e);
		} catch (BBBBusinessException e) {
			logError("Business exception while canceling the queue" + e);					
		} catch (IOException e) {
			logError("Business exception while canceling the queue" + e);
		}
		
		BBBPerformanceMonitor.end(CLS_NAME + "cancelReports");
		logDebug(CLS_NAME + "[cancelReports] method ends ");
	}

	/**
	 * This method used to get omniture response for the Report.Queue and retry
	 * upto 3 attempts if the expected response is not received.
	 * 
	 * @param reportIndex
	 * @param concept
	 * @param reportEmailContentList
	 * @return
	 * @throws BBBSystemException
	 * @throws IOException
	 * @throws BBBBusinessException
	 */
	private OmnitureQueueResponseVO getOmnitureReportQueueResponse(QueueRequestVO requestVO) throws BBBSystemException, IOException, BBBBusinessException {
		String concept=requestVO.getConcept();
		String reportType=requestVO.getReportType();
		int reportIndex=requestVO.getReportIndex();
		
		logDebug("OmnitureReportAPIToolsImpl.getOmnitureReportQueueResponse starts with report index: " + reportIndex);
		BBBPerformanceMonitor.start(OMNITURE_PERFORMANCE + "getOmnitureReportQueueResponse");
		
		OmnitureQueueResponseVO omnitureQueueResponseVO = null;
		OmnitureQueueRequestVO omnitureQueueRequestVO = prepareInputForQueueRequest(requestVO);
		final int maxFailRetryAttempts = getBbbCatalogTools().getValueForConfigKey(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.OMNITURE_MAX_RETRY_ATTEMPTS, BBBCoreConstants.ZERO);
		String omnitureReportQueue = getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.OMNITURE_REPORT_QUEUE, BBBCoreConstants.BLANK);
		
		
		int failedRetryAttempts = 0;
		String jsonForQueue = null;
		String jsonResponseQueue = null;
		boolean retry = false;
		do {
			try {
				retry = false;
				if(failedRetryAttempts > 0) {
					logDebug(CLS_NAME + " [getOmnitureReportQueueResponse] - During Report.Queue - Retry Attempt # " + failedRetryAttempts + " for report index # " + reportIndex);
					waitForNextRetryAttempt(String.valueOf(reportIndex), concept);
				}
				failedRetryAttempts++;
				jsonForQueue = getHttpCallInvoker().parseJSONRequest(omnitureQueueRequestVO);
				jsonResponseQueue = getHttpCallInvoker().callPostRestService(omnitureEndPoint + BBBCoreConstants.METHOD + omnitureReportQueue, jsonForQueue, getHeader(), false);
				omnitureQueueResponseVO = getHttpCallInvoker().parseJSONResponse(OmnitureQueueResponseVO.class, jsonResponseQueue);
				
				if (omnitureQueueResponseVO == null || (!BBBUtility.isEmpty(omnitureQueueResponseVO.getError()) && !BBBUtility.isEmpty(omnitureQueueResponseVO.getError_description()))) {
					retry = true;
					String errorDetails = omnitureQueueResponseVO != null ? omnitureQueueResponseVO.getError_description() : "[Empty Response]";
					logError("Response received in Omniture API Call to [Report.Queue] : " + omnitureQueueResponseVO + " and Error details are : " + errorDetails);
				}
			} catch (BBBSystemException | BBBBusinessException | IOException e) {
				logError("Exception generated during the Omniture API Call : " + e);
				retry = true;
				if (failedRetryAttempts > maxFailRetryAttempts) {
					BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "getOmnitureReportQueueResponse");
					logError("Report for Concept :: " + concept + " with report index: " + reportIndex + " is not queued even after maximum Retry attempts# " + maxFailRetryAttempts);
					throw e;
				}
			} 
		} while(retry && failedRetryAttempts <= maxFailRetryAttempts);
		
		logDebug("OmnitureReportAPIToolsImpl.getOmnitureReportGetResponse end with omnitureQueueResponseVO: " + omnitureQueueResponseVO);
		BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "getOmnitureReportQueueResponse");
		return omnitureQueueResponseVO;
	}

	/**
	 * This method used to get Omniture response for the Report.GET and retry
	 * up to 3 attempts if the expected response is not received.
	 * 
	 * @param omnitureGetRequestVO
	 * @return
	 * @throws BBBSystemException
	 * @throws IOException
	 * @throws BBBBusinessException
	 */
	private OmnitureGetResponseVO invokeOmnitureReportGetAPI(OmnitureGetRequestVO omnitureGetRequestVO) throws BBBSystemException, IOException, BBBBusinessException {
		BBBPerformanceMonitor.start(OMNITURE_PERFORMANCE + "_invokeOmnitureReportGetAPI");

		final String retryMsg = "# Retry attempts for Report.GET";
		final String reportId = omnitureGetRequestVO.getReportID();
		OmnitureGetResponseVO omnitureGetResponseVO = null;
		final int maxFailRetryAttempts = getBbbCatalogTools().getValueForConfigKey(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.OMNITURE_MAX_RETRY_ATTEMPTS, BBBCoreConstants.ZERO);
		String omnitureReportGet = getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.OMNITURE_REPORT_GET, BBBCoreConstants.BLANK);
		logDebug("OmnitureReportAPIToolsImpl.invokeOmnitureReportGetAPI starts with report ID: " + reportId + " and maxFailRetryAttempts: " + maxFailRetryAttempts);

		int failedRetryAttempts = 0;
		String jsonForGet = null;
		String jsonResponseGet = null;
		boolean retry = false;
		do {
			try {
				retry = false;
				if(failedRetryAttempts > 0) {
					logDebug(CLS_NAME + " [invokeOmnitureReportGetAPI] - During Report.GET - Retry Attempt # " + failedRetryAttempts + " for report ID # " + reportId);
					waitForNextRetryAttempt(reportId, retryMsg);
				}
				failedRetryAttempts++;
				jsonForGet = getHttpCallInvoker().parseJSONRequest(omnitureGetRequestVO);
				jsonResponseGet = getHttpCallInvoker().callPostRestService(omnitureEndPoint + BBBCoreConstants.METHOD + omnitureReportGet, jsonForGet, getHeader(), false);
				omnitureGetResponseVO = getHttpCallInvoker().parseJSONResponse(OmnitureGetResponseVO.class, jsonResponseGet);

				if (omnitureGetResponseVO == null || (!BBBUtility.isEmpty(omnitureGetResponseVO.getError()) && !BBBUtility.isEmpty(omnitureGetResponseVO.getError_description()))) {
					retry = true;
					String errorDetails = omnitureGetResponseVO != null ? omnitureGetResponseVO.getError_description() : "[Empty Response]";
					logError("ERROR: Response received in Omniture GET API Call to [Report.GET] : " + omnitureGetResponseVO + " and Error details are : " + errorDetails);
					if (failedRetryAttempts > maxFailRetryAttempts) {
						logError(CLS_NAME + " [invokeOmnitureReportGetAPI] with report id: " + reportId + " is not GET even after maximum Retry attempts # " + maxFailRetryAttempts);
					}
				}
			} catch (BBBSystemException | BBBBusinessException | IOException e) {
				logError(CLS_NAME + " # ERROR: Exception generated during the Omniture GET API Call : " + e);
				retry = true;
				if (failedRetryAttempts > maxFailRetryAttempts) {
					BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "_invokeOmnitureReportGetAPI");
					logError(CLS_NAME + "# [invokeOmnitureReportGetAPI] with report id: " + reportId + " is not GET even after maximum Retry attempts # " + maxFailRetryAttempts);
					throw e;
				}
			} 
		} while(retry && failedRetryAttempts <= maxFailRetryAttempts);

		logDebug("OmnitureReportAPIToolsImpl.invokeOmnitureReportGetAPI end with omnitureGetResponseVO: " + omnitureGetResponseVO);
		BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "_invokeOmnitureReportGetAPI");
		return omnitureGetResponseVO;
	}

	/** This method updates the report status after the Get operation
	 * 
	 * @param omnitureGetResponseVO
	 * @param reportID
	 * @param omniReportStatusItem
	 * @param concept
	 * @param recordsUpdated
	 * @param firstReport
	 * @return 
	 * @throws BBBSystemException,
	 * @throws BBBBusinessException, 
	 */

	private boolean updateReportStatus(OmnitureGetResponseVO omnitureGetResponseVO, String reportID, MutableRepositoryItem omniReportStatusItem, String concept) {
		
		logDebug("OmnitureReportAPIToolsImpl.updateReportStatus() method starts for Report ID :: " + reportID + " and concept :: " + concept);
		
    	
		BBBPerformanceMonitor.start(OMNITURE_PERFORMANCE + "updateReportStatus");
		final MutableRepository omniReportRepository = this.getOmnitureReportRepository();
		Calendar calender = Calendar.getInstance();
		Date getTime = calender.getTime();
        boolean getSuccess = false;
		try {
			if (!BBBUtility.isEmpty(reportID) && reportID.equalsIgnoreCase((String) omniReportStatusItem.getPropertyValue(BBBCoreConstants.REPORT_ID))) {
				if (BBBUtility.isEmpty(omnitureGetResponseVO.getError()) && BBBUtility.isEmpty(omnitureGetResponseVO.getError_description())) {
					logDebug("Report Operation is Success for :: " + reportID + " and concept :: " + concept);
					
					getSuccess = true;
					omniReportStatusItem.setPropertyValue(BBBCoreConstants.REPORT_ERROR_CODE, null);
					omniReportStatusItem.setPropertyValue(BBBCoreConstants.REPORT_ERROR_DESC, null);
					
				} else {
					omniReportStatusItem.setPropertyValue(BBBCoreConstants.REPORT_ERROR_CODE, omnitureGetResponseVO.getError());
					omniReportStatusItem.setPropertyValue(BBBCoreConstants.REPORT_ERROR_DESC, omnitureGetResponseVO.getError_description());
				}
				
				if (getSuccess) {
					
					omniReportStatusItem.setPropertyValue(BBBCoreConstants.REPORT_OP_STATUS,BBBCoreConstants.OMNITURE_REPORT_SUCCESS);
					
				} else if (BBBCoreConstants.REPORT_NOT_READY.equalsIgnoreCase(omnitureGetResponseVO.getError())) {
					
					omniReportStatusItem.setPropertyValue(BBBCoreConstants.REPORT_OP_STATUS,BBBCoreConstants.OMNITURE_REPORT_QUEUED);
					
				} else  {
					omniReportStatusItem.setPropertyValue(BBBCoreConstants.REPORT_OP_STATUS,BBBCoreConstants.OMNITURE_REPORT_FAILED);
				}
				
				int getAttempts = BBBCoreConstants.ONE;
				
				if(omniReportStatusItem.getPropertyValue(BBBCoreConstants.GET_ATTEMPTS) != null) {
					getAttempts = ((int)omniReportStatusItem.getPropertyValue(BBBCoreConstants.GET_ATTEMPTS))+1;
				}
				
				omniReportStatusItem.setPropertyValue(BBBCoreConstants.GET_ATTEMPTS, getAttempts);
				omniReportStatusItem.setPropertyValue(BBBCoreConstants.REPORT_GET_TIME_1, getTime);
				omniReportStatusItem.setPropertyValue(BBBCoreConstants.REPORT_EX_TIME, omnitureGetResponseVO.getRunSeconds());
				omniReportStatusItem.setPropertyValue(BBBCoreConstants.REPORT_PROC_FINISH_TIME, omnitureGetResponseVO.getRunSeconds() + omnitureGetResponseVO.getWaitSeconds());
				omniReportRepository.updateItem(omniReportStatusItem);
				/*
				 * update last success run date in BBB_OMNITURE_REPORT_CONFIG
				 * table If omniReportStatusItem have batchId in It,then for
				 * this batchId all report should be success state 
				 * 
				 * if omniReportStatusItem don't have batchId in it,then last
				 * success run need to be updated for individual reports
				 */
				if (getSuccess)
				{
				updateLastSuccessRunDate(omniReportStatusItem);
				}
			}
		} catch (RepositoryException e) {
			logError("RepositoryException occurred while updating Report Status", e);
			BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "updateReportStatus");
		}
		
		BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "updateReportStatus");
		
		logDebug("OmnitureReportAPIToolsImpl.updateReportStatus() method End");
		
		
		return getSuccess;
	}

	/**
	 * Used to update last success run date in the BBB_OMNITURE_REPORT_CONFIG
	 * table
	 * 
	 * If a report type have multiple report in batch,then last success run in
	 * report config table should only be updated if all report successfully
	 * processed .
	 * 
	 * If a report type have only one report then last success run should be
	 * update at the time of success full processing of that report.
	 * 
	 * @param propertyValue
	 * @throws RepositoryException
	 */
	
	private void updateLastSuccessRunDate(RepositoryItem reportStatusItem) throws RepositoryException {
		
		logDebug(CLS_NAME+" [updateLastSuccessRunDate] method Start: RepositoryId:"+reportStatusItem.getRepositoryId());
		
		BBBPerformanceMonitor.start(OMNITURE_PERFORMANCE + "updateLastSuccessRunDate");
		String batchId =(String) reportStatusItem.getPropertyValue(BBBCoreConstants.REPORT_BATCH_ID);
		String reportType=(String) reportStatusItem.getPropertyValue(BBBCoreConstants.REPORT_TYPE);
		String concept=(String) reportStatusItem.getPropertyValue(BBBCoreConstants.CONCEPT);
		String methodType=(String) reportStatusItem.getPropertyValue(BBBCoreConstants.METHOD_TYPE);
		String pReportId=(String)   reportStatusItem.getPropertyValue(BBBCoreConstants.REPORT_ID);
		if(batchId != null && StringUtils.isNotBlank(batchId))
		{	
			logDebug("updateLastSuccessRunDate: batch Queued report : pReportId:"+pReportId+",batchId:"+batchId);
			RepositoryItem[] allReportInBatch=	getReportStatusByReportIdOrBatchId(null,batchId);
			if(allReportInBatch != null && allReportInBatch.length> 0)
			{
				boolean allReportInSuccessState=true;
				
				for(RepositoryItem reportStatus :allReportInBatch)
				{
					String status=(String) reportStatus.getPropertyValue(BBBCoreConstants.REPORT_OP_STATUS);
					if( ! BBBCoreConstants.OMNITURE_REPORT_SUCCESS .equalsIgnoreCase(status))
					{
						allReportInSuccessState =false;	
						String reportId=(String) reportStatus.getPropertyValue(BBBCoreConstants.REPORT_ID);
						String Id= reportStatus.getRepositoryId();
						logDebug("updateLastSuccessRunDate: Report  not in success state with reportId["+reportId+"],RepositoryId["+Id+"] , for Report Type ["+reportType+"] ,batchId:["+batchId+"],concept:["+concept+"]");
						break;
					}
				}
				if(allReportInSuccessState)
				{
					updateDateInRepository(reportType, methodType, concept);
				}
			}
			
		}
		else
		{	
			logDebug("updateLastSuccessRunDate: Single Queued report : pReportId:"+pReportId);
			String status=(String) reportStatusItem.getPropertyValue(BBBCoreConstants.REPORT_OP_STATUS);
			if(BBBCoreConstants.OMNITURE_REPORT_SUCCESS .equalsIgnoreCase(status))
			{
				updateDateInRepository(reportType, methodType, concept);
			}
		}
		
		BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "updateLastSuccessRunDate");
		logDebug(CLS_NAME+" [updateLastSuccessRunDate] method end"+batchId);
	}
	
	public void updateDateInRepository(String reportType,String methodType,String concept) throws RepositoryException
	{
		
		logDebug(CLS_NAME+" [updateDateInRepository] method Start: reportType:"+reportType+",methodType:"+methodType+",concept:"+concept);
		
		BBBPerformanceMonitor.start(OMNITURE_PERFORMANCE + "updateDateInRepository");
		
		RepositoryItemDescriptor reportStatusDesc = this.getOmnitureReportRepository().getItemDescriptor(BBBCoreConstants.OMNITURE_REPORT_CONFIG);
		RepositoryView view=reportStatusDesc.getRepositoryView();
		QueryBuilder queryBuilder = view.getQueryBuilder();
		
        QueryExpression reportTypePropQueyEx =     queryBuilder.createPropertyQueryExpression(BBBCoreConstants.REPORT_TYPE);
        QueryExpression reportTypeValueEx =    queryBuilder.createConstantQueryExpression(reportType);
        Query reportTypeEqualQuery =   queryBuilder.createComparisonQuery(reportTypePropQueyEx, reportTypeValueEx, QueryBuilder.EQUALS);
        
        
        QueryExpression methodTypePropQueyEx =     queryBuilder.createPropertyQueryExpression(BBBCoreConstants.METHOD_TYPE);
        QueryExpression methodTypeValueEx =    queryBuilder.createConstantQueryExpression(methodType);
        Query methodTypeEqualQuery =   queryBuilder.createComparisonQuery(methodTypePropQueyEx, methodTypeValueEx, QueryBuilder.EQUALS);
        
        
        
        QueryExpression conceptPropQueyEx =     queryBuilder.createPropertyQueryExpression(BBBCoreConstants.CONCEPT);
        QueryExpression conceptValueEx =    queryBuilder.createConstantQueryExpression(concept);
        Query conceptEqualQuery =   queryBuilder.createComparisonQuery(conceptPropQueyEx, conceptValueEx, QueryBuilder.EQUALS);
        
        
        Query[] reportTypeByConcepAndMethodType = { reportTypeEqualQuery, methodTypeEqualQuery ,conceptEqualQuery};
        Query reportTypeByConcepAndMethodTypeQuery = queryBuilder.createAndQuery(reportTypeByConcepAndMethodType);
		MutableRepositoryItem[] reportConfig = (MutableRepositoryItem[])view.executeQuery(reportTypeByConcepAndMethodTypeQuery);
		if(reportConfig != null && reportConfig.length>0)
		{	
			Date lastSuccessRunDate=new Date();
			reportConfig[0].setPropertyValue(BBBCoreConstants.REPORT_LAST_SUCCESS_RUN_DATE, lastSuccessRunDate);
			getOmnitureReportRepository().updateItem(reportConfig[0]);
		    logDebug(CLS_NAME+" [updateDateInRepository] method :Item updated with date:["+lastSuccessRunDate+"],RepositoryId:"+reportConfig[0].getRepositoryId());
		   
		}
		
		BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "updateDateInRepository");
		
		logDebug(CLS_NAME+" [updateDateInRepository] method Ends");
		
	}
	/**
	 * This method is used to Send Email on Success or Failure of Report Processing
	 * @param concept
	 * @param flow
	 * @param reportEmailContentList
	 */
    public void sendEmail(String concept, String flow, List<OmnitureReportEmailContentVO> reportEmailContentList) {

		long startTime = System.currentTimeMillis();
		logDebug( CLS_NAME + " [ sendEmail ] method starts :: Concept :: " + concept + " Flow :: " + flow);
		
		logInfo("Start Time to send email startTime::" + (startTime) + " milliseconds");
		
		BBBPerformanceMonitor.start(OMNITURE_PERFORMANCE + "sendEmail");
		
		Map<String, String> paramMap = new HashMap<String, String>();
		String recipientFrom = getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.RECIPIENT_FROM, BBBCoreConstants.BLANK);
		String recipientTo = getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.RECIPIENT_TO, BBBCoreConstants.BLANK);
		String emailContent = BBBCoreConstants.BLANK;
		String emailSubject = BBBCoreConstants.BLANK;
		String host = getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.SMTP_CONFIG, SMTP_HOST_NAME, BBBCoreConstants.BLANK);
		Map<String, String> placeHolderMap = new HashMap<String, String>();
		placeHolderMap.put(BBBCoreConstants.CONCEPT, concept);
		
		if (PURGE_FLOW.equalsIgnoreCase(flow)) {
			emailSubject = getLblTxtTemplateManager().getPageTextArea(
					BBBCoreConstants.OMNITURE_PURGE_FAIL_MSSG_TXT, placeHolderMap);
			emailContent = getLblTxtTemplateManager().getPageTextArea(
					BBBCoreConstants.OMNITURE_PURGE_FAIL_BODY_TXT,	placeHolderMap);
		} else if (GET_FLOW.equalsIgnoreCase(flow)) {
				//placeHolderMap.put(EXCEPTION_DETAILS, errorMessage);
			    placeHolderMap.put(BBBCoreConstants.REPORT_TYPE,reportEmailContentList.get(0).getReportType());
				emailSubject = getLblTxtTemplateManager().getPageTextArea(
						BBBCoreConstants.OMNITURE_GET_REPORT_MSSG_TXT,	placeHolderMap);
				emailContent = getLblTxtTemplateManager().getPageTextArea(
						BBBCoreConstants.OMNITURE_GET_REPORT_BODY_TXT,	placeHolderMap);
				emailContent = createReportEmailContent(emailContent,reportEmailContentList);
		} else if (ARCHIVAL_RECORDS_FLOW.equalsIgnoreCase(flow)) {
			placeHolderMap.put(EXCEPTION_DETAILS, reportEmailContentList.get(0).getExceptionDetails());
			placeHolderMap.put(BBBCoreConstants.REPORT_TYPE,reportEmailContentList.get(0).getReportType());
			emailSubject = getLblTxtTemplateManager().getPageTextArea(
					BBBCoreConstants.OMNITURE_ARCHIVAL_FAIL_MSSG, placeHolderMap);
			emailContent = getLblTxtTemplateManager().getPageTextArea(
					BBBCoreConstants.OMNITURE_ARCHIVAL_FAIL_BODY, placeHolderMap);
		} else if (QUEUE_FLOW.equalsIgnoreCase(flow)) {
			    placeHolderMap.put(BBBCoreConstants.REPORT_TYPE,reportEmailContentList.get(0).getReportType());
				emailSubject = getLblTxtTemplateManager().getPageTextArea(
						BBBCoreConstants.OMNITURE_QUEUE_REPORT_MSSG, placeHolderMap);
				emailContent = getLblTxtTemplateManager().getPageTextArea(
						BBBCoreConstants.OMNITURE_QUEUE_REPORT_BODY, placeHolderMap);
				emailContent = createReportEmailContent(emailContent, reportEmailContentList);
		} else {
			placeHolderMap.put(EXCEPTION_DETAILS, reportEmailContentList.get(0).getExceptionDetails());
			emailSubject = getLblTxtTemplateManager().getPageTextArea(
					BBBCoreConstants.OMNITURE_GENERAL_FAIL_MSSG, placeHolderMap);
			emailContent = getLblTxtTemplateManager().getPageTextArea(
					BBBCoreConstants.OMNITURE_GENERAL_FAIL_BODY, placeHolderMap);
		}
		
		paramMap.put(RECIPIENT_FROM, recipientFrom);
		paramMap.put(RECIPIENT_TO, recipientTo);
		paramMap.put(SUBJECT, emailSubject);
		paramMap.put(EMAIL_CONTENT, emailContent);
		paramMap.put(MAP_SMTP_HOST, host);
		logInfo("emailSubject :: " + emailSubject);
		logInfo("emailContent :: " + emailContent);
		
		if (!BBBUtility.isEmpty(recipientFrom) && !BBBUtility.isEmpty(recipientTo)) {
			logDebug("Sending Email");
			BBBUtility.sendEmail(paramMap);
		}
		
		long endTime = System.currentTimeMillis();
		logInfo("Total time took to send email endTime::" + (endTime - startTime) + " milliseconds");
		
		BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "sendEmail");
		
		logDebug( CLS_NAME + " [ sendEmail ] method ends Concept :: " + concept);
		
	}
    
    /**
     * Method to create Email content in html tabular for multiple reports
     * @param emailContent
     * @param reportEmailContentList
     * @return
     */
	public String createReportEmailContent(String emailContent, List<OmnitureReportEmailContentVO> reportEmailContentList) {
    	logDebug( CLS_NAME + " [ createReportEmailContent ] method start email content :: " + reportEmailContentList);
    	String updatedEmailContent = BBBCoreConstants.BLANK;
    	
    	if(emailContent == null || reportEmailContentList == null ) {
    		return updatedEmailContent;
    	}
    	
    	StringBuilder tableContent = new StringBuilder();
    	
    	tableContent.append(BBBCoreConstants.HTML_TABLE_START);
    	// Html table header row
	    tableContent.append(BBBCoreConstants.REPORT_TABLE_HEADER);
	    		    	
	    //Populate data in report detail table	
    	for (OmnitureReportEmailContentVO omnitureReportEmailContentVO : reportEmailContentList) {
    		tableContent.append(BBBCoreConstants.HTML_TABLE_ROW_START);
    		
    		tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_START);
            tableContent.append(!BBBUtility.isEmpty(omnitureReportEmailContentVO.getReportId()) ? omnitureReportEmailContentVO.getReportId() : BBBCoreConstants.BLANK);
            tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_END);
        	
        	tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_START);
            tableContent.append(!BBBUtility.isEmpty(omnitureReportEmailContentVO.getOpStatus()) ? omnitureReportEmailContentVO.getOpStatus() : BBBCoreConstants.BLANK);
            tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_END);
         	
         	tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_START);
            tableContent.append(omnitureReportEmailContentVO.getRecordsUpdated());
            tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_END);
         	
         	tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_START);
            tableContent.append(!BBBUtility.isEmpty(omnitureReportEmailContentVO.getExceptionDetails()) ? omnitureReportEmailContentVO.getExceptionDetails() : BBBCoreConstants.BLANK);
            tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_END);
         	
        	tableContent.append(BBBCoreConstants.HTML_TABLE_ROW_END);
		}
    	tableContent.append(BBBCoreConstants.HTML_TABLE_END);
    	
    	updatedEmailContent = emailContent + tableContent;
    	logDebug( CLS_NAME + " [ createReportEmailContent ] method ends updated email content :: " +updatedEmailContent);
    	return updatedEmailContent;    	
    }
    /**
	 * This method is used to prepare the Input for Get Method
	 * 
	 * @param concept
	 * @return OmnitureGetRequestVO
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private OmnitureGetRequestVO prepareInputForGetRequest(String reportID) {
		
		logDebug( CLS_NAME + " [ prepareInputForGetRequest ] method starts :: Report ID :: " + reportID);
		
		OmnitureGetRequestVO requestVO = new OmnitureGetRequestVO();
		requestVO.setReportID(reportID);
		
		logDebug( CLS_NAME + " [ prepareInputForGetRequest ] method ends :: " + requestVO);
		
		return requestVO;
		
	}
	
	/**
	 * This method is used to insert Report Status after Queue Method
	 * 
	 * @param queueResponseVO
	 * @param concept
	 * @param reportType
	 * @return status
	 * @throws BBBSystemException
	 * @throws BBBBusinessException 
	 */
	public boolean insertReportStatus(final OmnitureQueueResponseVO queueResponseVO,QueueRequestVO requestVO) {
		
		String concept=requestVO.getConcept();
		String reportType=requestVO.getReportType();
		
		logDebug("OmnitureReportAPIToolsImpl.insertReportStatus() method starts for concept :: " + concept + "Queue Response :: " + queueResponseVO);
		
		BBBPerformanceMonitor.start(OMNITURE_PERFORMANCE + "insertReportStatus");
		
		boolean insertSuccess = false;
		MutableRepositoryItem omniReportItem = null;
		final MutableRepository omniReportRepository = this.getOmnitureReportRepository();
		
		Calendar calender = Calendar.getInstance();
		Date currentDate = DateUtils.truncate(calender.getTime(), Calendar.DAY_OF_MONTH);
		
        if (queueResponseVO != null && !BBBUtility.isEmpty(queueResponseVO.getReportID())) {
			
			try {
				omniReportItem = omniReportRepository.createItem(BBBCoreConstants.REPORT_STATUS);
				omniReportItem.setPropertyValue(BBBCoreConstants.REPORT_ID, queueResponseVO.getReportID());
				omniReportItem.setPropertyValue(BBBCoreConstants.METHOD_TYPE, requestVO.getMethodType());
				omniReportItem.setPropertyValue(BBBCoreConstants.CONCEPT, concept);
				omniReportItem.setPropertyValue(BBBCoreConstants.REPORT_QUEUED_TIME, currentDate);
				omniReportItem.setPropertyValue(BBBCoreConstants.REPORT_OP_STATUS, BBBCoreConstants.OMNITURE_REPORT_QUEUED);
				omniReportItem.setPropertyValue(BBBCoreConstants.REPORT_ERROR_CODE, queueResponseVO.getError());
				omniReportItem.setPropertyValue(BBBCoreConstants.REPORT_ERROR_DESC, queueResponseVO.getError_description());
				omniReportItem.setPropertyValue(BBBCoreConstants.REPORT_TYPE,requestVO.getReportType());
				if(requestVO.isBatchRequest()){
					omniReportItem.setPropertyValue(BBBCoreConstants.REPORT_BATCH_ID, requestVO.getBatchId());
					omniReportItem.setPropertyValue(BBBCoreConstants.REPORT_BATCH_SEQ, requestVO.getReportIndex());
					omniReportItem.setPropertyValue(BBBCoreConstants.REPORT_COUNT, requestVO.getCount());
					omniReportItem.setPropertyValue(BBBCoreConstants.REPORT_RANGE_FROM,requestVO.getStartingWith());
					
					// Its value will vary depending on the startWith defined for particular report type
					 Integer rangeTo=requestVO.getTop()+requestVO.getStartingWith()-BBBCoreConstants.ONE;
					if(rangeTo > requestVO.getCount()){
						rangeTo = requestVO.getCount();
					}
						omniReportItem.setPropertyValue(BBBCoreConstants.REPORT_RANGE_TO,rangeTo);
				}
				
				omniReportRepository.addItem(omniReportItem);
				insertSuccess = true;
			} catch (final RepositoryException e) {
				logError("RepositoryException occurred while queuing the report", e);
			}
        }
		
        BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "insertReportStatus");
        
        logDebug("OmnitureReportAPIToolsImpl.insertReportStatus() method ends");
		
		return insertSuccess;
	}
	
	/**
	 * This method is used to generate the authentication Header for Get/Queue Method
	 * @return String
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	 private String getHeader() throws UnsupportedEncodingException, BBBSystemException, BBBBusinessException {
		 
		 String omnitureAPIUserName = getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.OMNITURE_API_USER_NAME, BBBCoreConstants.BLANK);	
		 String omnitureAPISecretKey = getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.OMNITURE_API_SECRET_KEY, BBBCoreConstants.BLANK);
		 
		    byte[] nonceBytes = generateNonce();
	        String nonce = base64Encode(nonceBytes);
	        String createdTimeStamp = generateTimestamp();
	        String passwordDigestEncrypted = getBase64Digest(nonceBytes, createdTimeStamp.getBytes(BBBCoreConstants.UTF_8), omnitureAPISecretKey.getBytes(BBBCoreConstants.UTF_8));
	        StringBuffer header = new StringBuffer("UsernameToken Username=\"");
	        header.append(omnitureAPIUserName);
	        header.append("\", ");
	        header.append("PasswordDigest=\"");
	        header.append(passwordDigestEncrypted.trim());
	        header.append("\", ");
	        header.append("Nonce=\"");
	        header.append(nonce.trim());
	        header.append("\", ");
	        header.append("Created=\"");
	        header.append(createdTimeStamp);
	        header.append("\"");
	        logDebug("OmnitureReportAPIToolsImpl.getHeader() - omniture API header -- " + header.toString());
	        return header.toString();
	    }
	 
	   private static String base64Encode(byte[] bytes) {
	    	return Base64.encodeToString(bytes);
	    }
	   
	   private static byte[] generateNonce() {
	        String generatedNonce = Long.toString(new Date().getTime());
	        return generatedNonce.getBytes();
	    }

	   private static String generateTimestamp() {
	        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	        return dateFormatter.format(new Date());
	    }

	   private static synchronized String getBase64Digest(byte[] nonce, byte[] created, byte[] password) {
	      try {
	        MessageDigest messageDigester = MessageDigest.getInstance("SHA-1");
	        messageDigester.reset();
	        messageDigester.update(nonce);
	        messageDigester.update(created);
	        messageDigester.update(password);
	        return base64Encode(messageDigester.digest());
	      } catch (java.security.NoSuchAlgorithmException e) {
	        throw new RuntimeException(e);
	      }
	    }
	    
	   /** This method insert the report data into database using Prepared Statement
		 * @param omnitureGetResponseVO
		 * @param reportID
		 * @param concept
		 * @param reportType
		 * @param highestRankMap
		 * @return status
		 * @throws BBBSystemException
		 */
	   public int insertReportData(final OmnitureGetResponseVO omnitureGetResponseVO, String reportID, String concept, String reportType) {
			
			logDebug("OmnitureReportAPIToolsImpl.insertReportData() method start for concept :: " + concept + " and Report ID :: " + reportID);
			long startTime = System.currentTimeMillis();
			Connection connection = null;
		
			double boostScore;
			Set<String> keywordSet = new HashSet<String>();
			Set<String> batchKeywordSet = new HashSet<String>();
			int record = BBBCoreConstants.ZERO;
			final int batchSize = getBbbCatalogTools().getValueForConfigKey(BBBCoreConstants.OMNITURE_BOOSTING, 
					BBBCoreConstants.INSERT_REPORT_BATCH_SIZE, 1000);
			
			PreparedStatement preparedStatement = null;
			try {
				List<OmnitureReportDataVO> dataList = omnitureGetResponseVO.getReport().getData();
				
				if (!BBBUtility.isListEmpty(dataList)) {	
					
					int rank = 1;							
									
					connection = ((GSARepository) getOmnitureReportRepository()).getDataSource().getConnection();
					connection.setAutoCommit(false);
					
					preparedStatement = connection.prepareStatement(INSERT_REPORT_DATA_SQL);
					for (OmnitureReportDataVO omnitureReportDataVO : dataList) {
						   
							String keyword= omnitureReportDataVO.getName();
							if(BBBUtility.isNotBlank(keyword))
							{
								keyword=keyword.toLowerCase();
							}
						    batchKeywordSet.add(keyword);
							List<OmnitureReportDataVO> breakdownList = omnitureReportDataVO.getBreakdown();
							if (!BBBUtility.isListEmpty(breakdownList)) {
								
								for (OmnitureReportDataVO omnitureReportDataVO2 : breakdownList) {
									
									logDebug("Omniture response data "+omnitureReportDataVO2);
									
									if (!BBBCoreConstants.OMNITURE_PRODUCT_OTHER.equalsIgnoreCase(omnitureReportDataVO2.getName())
											&& !BBBCoreConstants.OMNITURE_UNSPECIFIED.equalsIgnoreCase(omnitureReportDataVO2.getName())
											&& !BBBCoreConstants.OMNITURE_UNDEFINED.equalsIgnoreCase(omnitureReportDataVO2.getName())) {
										
										boostScore = 0.0;
										record = record + BBBCoreConstants.ONE;
										if (omnitureReportDataVO2.getCounts() != null && omnitureReportDataVO2.getCounts().length > 0) {
											boostScore = omnitureReportDataVO2.getCounts()[BBBCoreConstants.ZERO];
										}								
										
										
										preparedStatement.setString(1, reportID);
										preparedStatement.setString(2, omnitureGetResponseVO.getReport().getReportSuite().getId());
										preparedStatement.setString(3, reportType);
										preparedStatement.setString(4, omnitureGetResponseVO.getReport().getPeriod());
										preparedStatement.setString(5, concept);
										preparedStatement.setDouble(6, boostScore);
										preparedStatement.setString(7, omnitureReportDataVO2.getName());
										preparedStatement.setString(8, keyword);
										preparedStatement.setInt(9, rank);
										preparedStatement.setTimestamp(10, new Timestamp(System.currentTimeMillis()));
										preparedStatement.addBatch();		                				
									}
								}
							}
							
							if(record % batchSize == BBBCoreConstants.ZERO)	{
								logDebug("Record "+record+" Batch Size "+batchSize);
								record = executeBatchCommitOrRollback(omnitureGetResponseVO, reportID, concept,
										connection, keywordSet, batchKeywordSet, record, preparedStatement);
							}
						     
					}
					record = executeBatchCommitOrRollback(omnitureGetResponseVO, reportID, concept,
							connection, keywordSet, batchKeywordSet, record, preparedStatement);					
				
				}
				
			} catch (SQLException e) {
				record = 0;
				setErrorInOmnitureVO(omnitureGetResponseVO, e);
			} finally {				
				try {
					if (preparedStatement != null) {
						preparedStatement.close();
					}

					if (connection != null  && !connection.isClosed()) {
						connection.close();
					}
					
				} catch (SQLException e) {
					record = 0;
					setErrorInOmnitureVO(omnitureGetResponseVO, e);
				}	
				
				if (!keywordSet.isEmpty()) {
					archiveAndRemoveExistingRecords(keywordSet, concept, record, omnitureGetResponseVO,
							reportID, reportType,batchSize);
					logDebug("Records Moved in Archive table"+ omnitureGetResponseVO.getRecordsMoved());
				}
				
			}
			logDebug("OmnitureReportAPIToolsImpl.insertReportData() method ends for Concept :: " + concept);
			long endTime = System.currentTimeMillis();
			logInfo("Total time taken to insert report data into database for concept :: " + concept + " :: " + (endTime-startTime) + " millisecpnds");
			return record;
		}

	protected int executeBatchCommitOrRollback(final OmnitureGetResponseVO omnitureGetResponseVO, String reportID,
			String concept, Connection connection, Set<String> keywordSet, Set<String> batchKeywordSet, int record,
			PreparedStatement preparedStatement) {
		boolean success = false;
		try{
			preparedStatement.executeBatch();
			success = true;
		}
		catch(SQLException e){
			setErrorInOmnitureVO(omnitureGetResponseVO, e);
		}
		finally {
			try {
				if (connection != null) {
					if (success) {
						logInfo("Total No Of Records Entered :: " + record + " For Concept :: " + concept + " For Report ID : " + reportID);
						connection.commit();
						keywordSet.addAll(batchKeywordSet);
						batchKeywordSet.clear();
					} else {
						logError("Some Exception occurred hence Making the operation Rollback :: Report ID :: " + reportID);
						connection.rollback();
					}
				}

			} catch (SQLException e) {
				record = 0;
				logError("SQL Exception ocurred for while committing/closing the preparedStatement/connection ", e);
				omnitureGetResponseVO.setError(e.getMessage());
				omnitureGetResponseVO.setError_description(e.getMessage());
			}									
		}
		return record;
	}


		/**
		 * This method is used to archive and remove the existing records from omniture report data table
		 * @param connection
		 * @param keywordSet
		 * @param concept
		 * @param record
		 * @param omnitureGetResponseVO
		 * @param reportID
		 * @param reportType 
		 * @param batchSize2 
		 */
		protected void archiveAndRemoveExistingRecords(Set<String> keywordSet, String concept,
				int record, OmnitureGetResponseVO omnitureGetResponseVO, String reportID, String reportType, int batchSize) {
			logDebug(CLS_NAME + "archiveAndRemoveExistingRecords methods starts --  ");
			
			PreparedStatement archiveDataStatement = null;
			PreparedStatement deleteDataStatement = null;			
			int count = 0;			
			Connection connection = null;
			boolean success = false;
			try {
				connection = ((GSARepository) getOmnitureReportRepository()).getDataSource().getConnection();
				connection.setAutoCommit(false); 
				archiveDataStatement = connection.prepareStatement(ARCHIVE_EXISTING_DATA);
				deleteDataStatement = connection.prepareStatement(DELETE_EXISTING_DATA);
				for(String keyword: keywordSet){
					success = false;
					count++;
					
					archiveDataStatement.setString(1,concept);
					archiveDataStatement.setString(2,reportID);
					archiveDataStatement.setString(3, reportType);
					archiveDataStatement.setString(4, keyword);
					
					archiveDataStatement.addBatch();
					
					deleteDataStatement.setString(1, concept);
					deleteDataStatement.setString(2,reportID);
					deleteDataStatement.setString(3, reportType);
					deleteDataStatement.setString(4, keyword);
					
					deleteDataStatement.addBatch();		
					
					if(count % batchSize == 0){						
						success = executeInBatches(connection, omnitureGetResponseVO, archiveDataStatement,
								deleteDataStatement, success);
					}
				}
				// Execute query for inserting data into Archive table and get count of rows inserted which needs to be send in email
				if(archiveDataStatement!=null){
					int affectedRows[]=	archiveDataStatement.executeBatch();
					for(int affectedRecords:affectedRows){
						omnitureGetResponseVO.setRecordsMoved(omnitureGetResponseVO.getRecordsMoved() + affectedRecords);
					}
				}
				
				if(deleteDataStatement!=null){
					deleteDataStatement.executeBatch();
				}
				success = true;
			} catch (SQLException e) {
				record = 0;
				setErrorInOmnitureVO(omnitureGetResponseVO, e);
				
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
					}
				} catch (SQLException e) {
					logError("SQL Exception ocurred while closing the statement", e);	
				}
			}
			
			logDebug(CLS_NAME + "archiveAndRemoveExistingRecords methods ends --  ");
				
		}
		
		/**
		 *  Executes the delete and archive statement for one batch 
		 * 
		 */
		private boolean executeInBatches(Connection connection, OmnitureGetResponseVO omnitureGetResponseVO,
				PreparedStatement archiveDataStatement, PreparedStatement deleteDataStatement, boolean success)
				throws SQLException {
			try{
				// Execute query  for inserting data in batches into Archive table and get count of rows inserted which needs to be send in email
				int affectedRows[]=	archiveDataStatement.executeBatch();
				for(int affectedRecords:affectedRows){
					omnitureGetResponseVO.setRecordsMoved(omnitureGetResponseVO.getRecordsMoved() + affectedRecords);
				}
				
				deleteDataStatement.executeBatch();
				success = true;
			}catch(SQLException e){
				setErrorInOmnitureVO(omnitureGetResponseVO, e);
			}finally{							
				if(connection!=null){								
					if(success){
						connection.commit();
					}else{
						connection.rollback();
					}
				}
			}
			return success;
		}

		

		/**
		 * Set Error in Omniture Response VO 
		 */
		protected void setErrorInOmnitureVO(final OmnitureGetResponseVO omnitureGetResponseVO, SQLException e) {
			logError("SQL Exception ocurred for while executing SQL ", e);
			omnitureGetResponseVO.setError(e.getMessage());
			omnitureGetResponseVO.setError_description(e.getMessage());
		}
		
		/** This method archive older data for older for rest of the report id except one passed into the input
		 * @param reportID
		 * @param concept
		 * @param reportType
		 * @return rowsMoved
		 */
		private long archiveOlderRecords(String reportType, String concept,int check_for_records_flag,String reportId,List<OmnitureReportEmailContentVO>  archiveFailedStatusEmailVOList) {
			
			logDebug("OmnitureReportAPIToolsImpl.archiveOlderRecords() method start for concept :: " + concept + " Report Type :: " + reportType + "Records flag value::" +  check_for_records_flag);
			
			BBBPerformanceMonitor.start(BBBPerformanceConstants.OMNITURE_REPORT_TOOLS_IMPL+ archiveOlderRecords);
			ArchiveDataVO archiveDataVO=new ArchiveDataVO();
			archiveDataVO.setStoreProcQuery(ARCHIVE_REPORT_DATA_SQL);
			archiveDataVO.setReportType(reportType);
			archiveDataVO.setChkRecordsFlag(check_for_records_flag);
			archiveDataVO.setConcept(concept);
			archiveDataVO.setArchiveProc(true);
			//method archiveData to call stored procedures ARCHIVE_OMNITURE_DATA and PURGE_OMNITURE_DATA
			logDebug("Calling procedure ARCHIVE_OMNITURE_DATA to archive old data from main table");
			long recordsMoved=archiveData(archiveDataVO,reportId,archiveFailedStatusEmailVOList);
			logDebug("Ending procedure ARCHIVE_OMNITURE_DATA");
			if (recordsMoved == 1){
				logDebug("Calling procedure PURGE_OMNITURE_DATA to delete old data from main table");
				archiveDataVO.setStoreProcQuery(PURGE_REPORT_DATA_SQL);
				archiveDataVO.setArchiveProc(false);
			    recordsMoved=archiveData(archiveDataVO,reportId,archiveFailedStatusEmailVOList);
			    logDebug("Ending procedure PURGE_OMNITURE_DATA");
			}
			BBBPerformanceMonitor.end(BBBPerformanceConstants.OMNITURE_REPORT_TOOLS_IMPL+ archiveOlderRecords);
			return recordsMoved;
		}
		
		/** This method to call the stored procedures for archiving and deleting old records from main table
		 * @param ArchiveDataVO
		 */
		private long archiveData(ArchiveDataVO archiveDataVO,String reportId,List<OmnitureReportEmailContentVO>  archiveFailedStatusEmailVOList)
		{
			long startTime = System.currentTimeMillis();
			
			BBBPerformanceMonitor.start(BBBPerformanceConstants.OMNITURE_REPORT_TOOLS_IMPL+ archiveData);
			logDebug("OmnitureReportAPIToolsImpl.archiveData() method start on" + startTime);
			long recordsMoved=0;
			boolean success = false;
			Connection connection = null;
			
			CallableStatement callableStatement = null;
		    String errorDetails = BBBCoreConstants.BLANK;
		    //Calling Stored Procedure to archive and delete old data from main table
			try {
				logDebug("Calling stored procedure for archiving older records from main table" + archiveDataVO.getStoreProcQuery());
				connection = ((GSARepository) getOmnitureReportRepository()).getDataSource().getConnection();
				
				if(archiveDataVO.isArchiveProc()){
				connection.setAutoCommit(false);
			    }
				else {
					connection.setAutoCommit(true);
				}
				
				callableStatement = connection.prepareCall(archiveDataVO.getStoreProcQuery());
			    callableStatement.setString(1, archiveDataVO.getReportType());
				callableStatement.setString(2, archiveDataVO.getConcept());
				callableStatement.setInt(3, archiveDataVO.getChkRecordsFlag());
				callableStatement.registerOutParameter(4, java.sql.Types.BIGINT);
				callableStatement.executeUpdate();
				recordsMoved = callableStatement.getLong(4);
		        success = true;
			} catch (SQLException ex) {
				logError("SQL Exception ocurred for while invoking stored procedure  ", ex);
				errorDetails = getExceptionTrace(ex);
			} catch (Exception ex) {
				logError("Some Exception ocurred for while invoking stored procedure  ", ex);
				errorDetails = getExceptionTrace(ex);
			} finally {
				try {
					if (callableStatement != null) {
						callableStatement.close();
					}
					if (connection != null) {
						if (success) {
							if(archiveDataVO.isArchiveProc()){
							connection.commit();
							}
							logInfo("Records archived successfully for Concept :: " + archiveDataVO.getConcept());
						} else {
							logError("Some Exception occurred hence Making the operation Rollback");
							connection.rollback();
						}
						if (!connection.isClosed()) {
							connection.close();
						}
					}
					
				} catch (SQLException e) {
					success = false;
					recordsMoved = 0;
					logError("SQL Exception ocurred for while committing/closing the preparedStatement/connection ", e);
				}
				BBBPerformanceMonitor.end(BBBPerformanceConstants.OMNITURE_REPORT_TOOLS_IMPL+ archiveData);
			}
			logDebug("OmnitureReportAPIToolsImpl.archiveOlderRecords() method ends for Concept :: " + archiveDataVO.getConcept());
			long endTime = System.currentTimeMillis();
			logDebug("Total time to archive older data :: " + (endTime-startTime) + "  milliseconds" );
			if (!success) {
				OmnitureReportEmailContentVO reportEmailContent = new OmnitureReportEmailContentVO();
				StringBuffer  opertaion= new StringBuffer("Error in Archiving");
				if(archiveDataVO.getChkRecordsFlag() == BBBCoreConstants.ONE)
				 {
					opertaion.append("(By day threshold)");
				 }
				else{
					opertaion.append("(By record count threshold)");
				}
				if(archiveDataVO.isArchiveProc()){
					opertaion.append(" in archiving data due to:-");
				}
				else
				{
					opertaion.append(" in deleting data due to:-");	
				}
				reportEmailContent.setExceptionDetails(opertaion.toString()+errorDetails);
				reportEmailContent.setReportType(archiveDataVO.getReportType());
				reportEmailContent.setReportId(reportId);
				reportEmailContent.setConcept(archiveDataVO.getConcept());
				archiveFailedStatusEmailVOList.add(reportEmailContent);
				
			}
			BBBPerformanceMonitor.end(BBBPerformanceConstants.OMNITURE_REPORT_TOOLS_IMPL+ archiveData);
			logDebug("Total records archived :: " + recordsMoved);
			return recordsMoved;
		}
		/**
	     * This method is used to get retention days for omniture report data.
	     * @return int
	     * @throws BBBSystemException
	     */
		public int getRetentionDaysFromConfigKey() {
			logDebug("Entry OmnitureReportAPIToolsImpl.getRetentionDaysFromConfigKey");
			final int DEFAULT_RETENTION_DAYS = 7;
			int retention_days = DEFAULT_RETENTION_DAYS; 
			String strRetentionDaysValue = "" ; 
			try {
				if (!BBBUtility.isListEmpty(this.getBbbCatalogTools()
						.getAllValuesForKey(BBBCoreConstants.OMNITURE_BOOSTING,
								BBBCoreConstants.OMNITURE_REPORT_RETENTION_DAYS))) {
					strRetentionDaysValue = this.getBbbCatalogTools()
							.getAllValuesForKey(BBBCoreConstants.OMNITURE_BOOSTING,
									BBBCoreConstants.OMNITURE_REPORT_RETENTION_DAYS).get(0).toString(); 
					retention_days = Integer.parseInt(strRetentionDaysValue);
				}
			} catch (NumberFormatException e) {
				logError(
						"NumberFormatException Exception occurred while converting retention days [" 
							+ strRetentionDaysValue + "] from string to integer", e);
			} catch (BBBBusinessException e) {
				logError(
						"BBBBusinessException Exception occurred while fetching"
						+ " omniture_report_retention_days from config key", e);
			} catch (BBBSystemException e) {
				logError(
						"BBBSystemException Exception occurred while fetching"
						+ " omniture_report_retention_days from config key", e);
			}
			logDebug("Exit OmnitureReportAPIToolsImpl.getRetentionDaysFromConfigKey");
			return retention_days;
		}
		
		/**
	     * This method is used to remove stale omniture report data
	     * from archive table.
	     * @param concept
	     * @throws BBBSystemException
	     */
	    public void purgeOmnitureDataFromArchiveTable(String concept) {
	    	logDebug("Entry OmnitureReportAPIToolsImpl.purgeOmnitureDataFromArchiveTable");
			int retentionDays = getRetentionDaysFromConfigKey();
			ResultSet rs =null;
			boolean success = false;
			int rowsDeleted = 0;
			int batchSize = BBBCoreConstants.TEN_THOUSANDS;
			int rowCount = 0;
			long startTime = System.currentTimeMillis();
			Connection connection = null;
			PreparedStatement preparedStatement = null;
			PreparedStatement preparedStatementForSelect = null;
			try {
				connection = ((GSARepository) this.getOmnitureReportRepository())
						.getDataSource().getConnection();
				
				if (connection != null) {
					connection.setAutoCommit(false);
					preparedStatement = connection.prepareStatement(getOmnitureBoostedDataPurgeQuery());
					preparedStatementForSelect = connection.prepareStatement(getOmnitureBoostedDataPurgeSelectQuery());
					preparedStatementForSelect.setString(1, concept);
					preparedStatementForSelect.setInt(2, retentionDays);
					rs = preparedStatementForSelect.executeQuery();
					while(rs.next())
					{
						rowCount = rs.getInt(1);
					}
					if(rowCount > 0)
					{
						int loopCount = (int) Math.ceil(rowCount/batchSize);
						if(loopCount > 0)
						{
							for (int  iCount = 0 ; iCount < loopCount ; iCount++) {
								{
									success = false;
									preparedStatement.setString(1, concept);
									preparedStatement.setInt(2, retentionDays);
									preparedStatement.setInt(3, batchSize);
									preparedStatement.addBatch();
									try{
										preparedStatement.executeBatch();
										success = true;
									}
									catch(SQLException e){
										logError("SQL Exception ocurred for while executing SQL ", e);
									}
									finally {
										try {
											if (connection != null) {
												if (success) {
													logDebug("Batch Sequence for delete "+iCount +" of concept "+concept);
													connection.commit();
												} else {
													logError("Some Exception occurred hence Making the operation Rollback");
													connection.rollback();
												}
											}

										} catch (SQLException e) {
											logError("SQL Exception ocurred for while committing/closing the preparedStatement/connection ", e);
										}
									}
									//preparedStatement.setInt(2, retentionDays);
								}
							}
						}
						if(preparedStatement != null)
						{
							preparedStatement.setString(1, concept);
							preparedStatement.setInt(2, retentionDays);
							preparedStatement.setInt(3, batchSize);
							preparedStatement.addBatch();
							preparedStatement.executeBatch();
						}
					}
					success = true;
				}
				else
				{
					logError("Connection is not established");
				}
			} catch (SQLException e) {
				logError("SQL Exception occurred while deleting data from database", e);
			} finally {
				try {
					if(rs!=null){
						rs.close();
					}
					if(preparedStatementForSelect!=null){
						preparedStatementForSelect.close();
					}
					if(preparedStatement!=null){
						preparedStatement.close();
					}
					if(connection!=null){
						if (success) {
							connection.commit();
							logDebug("Connection committed, rows deleted: " + rowCount);
						} else {
							logError("Some Exception occurred hence making the operation Rollback");
							connection.rollback();
						}
						if (!connection.isClosed()) {
							connection.close();
						}
					}
				} catch (SQLException e) {
					success = false;
					logError("SQL Exception occurred while deleting data from database, rows deleted: " + rowsDeleted, e);
				}
			}
			long endTime = System.currentTimeMillis();
			logDebug("Total time took for the Data Purge job:" + (endTime - startTime) + " milliseconds"
					+ " for concept " + concept);
			
			if (isSendEmailOnPurgeFailure()) {
				if (!success) {
					sendEmail(concept, PURGE_FLOW, null);
				}
			}
			logDebug("Exit OmnitureReportAPIToolsImpl.purgeOmnitureDataFromArchiveTable");
		}
	    
     /**
     * In the case any report is failed or not ready, mark remaining reports in batch as failed 
     * @param failedReportId
     * @param omnitureReportItems
     * @param updateStartIndex
     * @param reportEmailContentList
     */
    /*private void markRemainingReportFailed(String failedReportId, RepositoryItem [] omnitureReportItems, int updateStartIndex, List<OmnitureReportEmailContentVO> reportEmailContentList) {
    	   logDebug("Enter OmnitureReportAPIToolsImpl.markRemainingReportFailed failed reportId "+failedReportId+" updateStartIndex "+updateStartIndex+ " Report status items "+omnitureReportItems);
    	   
    	   if(omnitureReportItems == null) {
    		   return;
    	   }
    	   int numberOfReports = omnitureReportItems.length;
    	   final MutableRepository omniReportRepository = this.getOmnitureReportRepository();
    	   Calendar calender = Calendar.getInstance();
   		   Date getTime = calender.getTime();
   		   int i = 0;
    	   for(;updateStartIndex < numberOfReports;updateStartIndex++) {
    		   String reportID = null;
	    	   try {   
	    		   MutableRepositoryItem omniReportStatusItem = (MutableRepositoryItem) omnitureReportItems[updateStartIndex];
	    		   reportID = (String)omniReportStatusItem.getPropertyValue(BBBCoreConstants.REPORT_ID);
	    		   
	    		   cancelQueuedReport(reportID);
	    		   
	    		   omniReportStatusItem.setPropertyValue(BBBCoreConstants.REPORT_OP_STATUS,BBBCoreConstants.OMNITURE_REPORT_FAILED);
	    		   
	    		   int getAttempts = BBBCoreConstants.ONE;
				   if(omniReportStatusItem.getPropertyValue(BBBCoreConstants.GET_ATTEMPTS) != null) {
						getAttempts = ((int)omniReportStatusItem.getPropertyValue(BBBCoreConstants.GET_ATTEMPTS))+1;
					}
				   
				   omniReportStatusItem.setPropertyValue(BBBCoreConstants.GET_ATTEMPTS, getAttempts);
				   omniReportStatusItem.setPropertyValue(BBBCoreConstants.REPORT_GET_TIME_1, getTime);
				   omniReportStatusItem.setPropertyValue(BBBCoreConstants.REPORT_ERROR_CODE, BBBCoreConstants.REPORT_ERROR_CODE_BATCH_FAIL);
				   omniReportStatusItem.setPropertyValue(BBBCoreConstants.REPORT_ERROR_DESC, BBBCoreConstants.REPORT_ERROR_DESC_BATCH_FAIL+BBBCoreConstants.SPACE+failedReportId);
	    		   omniReportRepository.updateItem(omniReportStatusItem);
	    		   
	    	   } catch (RepositoryException e) {
				   logError("RepositoryException occurred while updating Report Status", e);
			   } finally {
				   //Email content for first failed report is already created so skip it
				   if (i > 0) {
					   OmnitureReportEmailContentVO reportEmailContent = new OmnitureReportEmailContentVO();
					   reportEmailContent.setReportId(reportID);
					   reportEmailContent.setOpStatus(BBBCoreConstants.OMNITURE_REPORT_FAILED);
					   reportEmailContent.setExceptionDetails(BBBCoreConstants.REPORT_ERROR_DESC_BATCH_FAIL+BBBCoreConstants.SPACE+failedReportId);
					   reportEmailContentList.add(reportEmailContent);
				   }
				   i++;
			   }
    	   }
    	   
    	   logDebug("Exit OmnitureReportAPIToolsImpl.markRemainingReportFailed");
       }*/
    
    /**
     * Cancel the already queued report on omniture side
     * @param reportId
     */
    public boolean cancelQueuedReport(String reportId) throws BBBSystemException , BBBBusinessException , IOException {
    	
    	logDebug(CLS_NAME+" cancelQueuedReport starts and reportId:"+reportId);
    	
		BBBPerformanceMonitor.start(OMNITURE_PERFORMANCE + "getQueueStatusReport");
		
		boolean isCancel=false;
    	if(reportId == null ) {
    	    return false;	
    	}
    	
    	OmnitureGetRequestVO omnitureGetRequestVO = null;
    	String omnitureReportCancel = getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.OMNITURE_REPORT_CANCEL, BBBCoreConstants.BLANK);
				
		try {
						
			omnitureGetRequestVO = prepareInputForGetRequest(reportId);
	        String jsonForCancel = getHttpCallInvoker().parseJSONRequest(omnitureGetRequestVO);
			String jsonResponseCancel = getHttpCallInvoker().callPostRestService(omnitureEndPoint + BBBCoreConstants.METHOD + omnitureReportCancel, jsonForCancel, getHeader(), false);
			//OmnitureGetResponseVO cancelJSONResponse = getHttpCallInvoker().parseJSONResponse(OmnitureGetResponseVO.class, jsonResponseCancel);
			logInfo("Cancel queued report " + reportId + " response json "+jsonResponseCancel);
			//If response is true then request cancelled successfully
			if(BBBCoreConstants.TRUE.equalsIgnoreCase(jsonResponseCancel))
			{
				isCancel=true;
			}
			
		} catch (BBBSystemException | IOException exception) {
			logError("Exception Occurred while canceling Report ", exception);
			BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "getQueueStatusReport");
			throw exception;
		} 
		
		BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "getQueueStatusReport");
		
		logDebug(CLS_NAME+" cancelQueuedReport end: cancel:"+isCancel);
		
		return isCancel;
    }

	/**
	 * This method is used to get the Omniture Report Queue status VO In List. If getQueue is not
	 * empty then it returns list of OmnitureReportStatus .
	 * 
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws IOException
	 */
    public List<OmnitureReportStatus> getQueueStatusReport() throws BBBSystemException, BBBBusinessException, IOException {
		
		
		logDebug("OmnitureReportAPIToolsImpl.getQueueStatusReport method call to Omniture API starts");
		
		BBBPerformanceMonitor.start(OMNITURE_PERFORMANCE + "getQueueStatusReport");
		List<OmnitureReportStatus> queueStatusList = new ArrayList<OmnitureReportStatus>();
		
		final String omnitureReportGetQueue = getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.OMNITURE_REPORT_GET_QUEUE, BBBCoreConstants.BLANK);
		try {
			final String jsonGetQueueStatus = getHttpCallInvoker()
					.callPostRestService(
							omnitureEndPoint + BBBCoreConstants.METHOD
									+ omnitureReportGetQueue, "", getHeader(), false);
			
			queueStatusList = getHttpCallInvoker().parseJSONArrayResponse(
					OmnitureReportStatus.class, jsonGetQueueStatus);
			
			if (queueStatusList != null && !queueStatusList.isEmpty()) {
				logDebug("OmnitureReportAPIToolsImpl.getQueueStatusReport - Number of Reports in Queue : "
						+ queueStatusList.size());
			} else {
				logDebug("OmnitureReportAPIToolsImpl.getQueueStatusReport - No Reports in Queue.");
			}
		} catch (BBBSystemException | BBBBusinessException|IOException exception) {
			
			logError("Exception Occurred while getting queue status Report from Omniture API Call ", exception);
			BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "getQueueStatusReport");
			throw exception;
			
		} 
		
		BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "getQueueStatusReport");
		
		logDebug("OmnitureReportAPIToolsImpl.getQueueStatusReport method call to Omniture API ends");
		
		return queueStatusList;
	}
    
    /**
	 * Method to introduce wait between omniture calls
	 * @param reportType
	 * @param concept
	 */
	public void waitForOmnitureCall(String reportType, String concept) {
		logDebug("Waiting for omniture API call, Report type "+reportType+" and concept "+concept);
		try {
			
			Thread.sleep(getBbbCatalogTools().getValueForConfigKey(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.OMNITURE_CALL_WAIT_TIME_MILLS, BBBCoreConstants.SIXTY_THOUSANDS));
			
		} catch (InterruptedException interruptedException) {
			logError("InterruptedException while wait in omniture call ",interruptedException);
		}
		
		logDebug("Resuming after wating for omniture call");	
	}

	/**
	 * This method used to introduce a wait interval between retry attempts when
	 * queue status is not SUCCESS.
	 * 
	 * @param reportParam
	 * @param concept
	 */
	public void waitForNextRetryAttempt(final String reportParam, final String concept) {
		logDebug("Waiting for next retry attempt on omniture API call, Report type/ID/Index " + reportParam + " and concept " + concept);
		try {
			Thread.sleep(getBbbCatalogTools().getValueForConfigKey(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.OMNITURE_RETRY_WAIT_TIME_MILLIS, BBBCoreConstants.THIRTY_THOUSANDS));
		} catch (InterruptedException interruptedException) {
			logError("InterruptedException while wait for retry attempt on omniture call ", interruptedException);
		}
		logDebug("Resuming after wating for retry attempt on omniture call...");	
	}
	/**
	 * Used to update report Status by reportId
	 * @param reportStatusVO
	 * @return boolean if success
	 * @throws RepositoryException 
	 */
	
	 public boolean updateReportStatusByReportId(ReportStatusVO reportStatusVO) throws RepositoryException{


		 logDebug(CLS_NAME+" updateReportStatusByReportId method starts and reportId:"+reportStatusVO.getReportId());
	    	
		 BBBPerformanceMonitor.start(OMNITURE_PERFORMANCE + "updateReportStatusByReportId");
		 
		 boolean isUpdated=false;	
			
		 if(reportStatusVO != null && StringUtils.isNotBlank(reportStatusVO.getReportId()))
		 {  
			 try {
					    String reportId=reportStatusVO.getReportId();
					    RepositoryItemDescriptor reportStatusDesc = this.getOmnitureReportRepository().getItemDescriptor(BBBCoreConstants.REPORT_STATUS);
						RepositoryView view=reportStatusDesc.getRepositoryView();
						QueryBuilder queryBuilder = view.getQueryBuilder();
			            QueryExpression reportIdPropQueyEx =     queryBuilder.createPropertyQueryExpression(BBBCoreConstants.REPORT_ID);
				        QueryExpression reportIdConstQueryEx =    queryBuilder.createConstantQueryExpression(reportId);
				        Query reportIdEqualQuery =   queryBuilder.createComparisonQuery(reportIdPropQueyEx, reportIdConstQueryEx, QueryBuilder.EQUALS);
				        MutableRepositoryItem[] reportStatus = (MutableRepositoryItem[]) view.executeQuery(reportIdEqualQuery);
					  if(reportStatus != null && reportStatus.length>0)
					  {
						 reportStatus[0].setPropertyValue(BBBCoreConstants.REPORT_ERROR_CODE, reportStatusVO.getErrorCode());
						 reportStatus[0].setPropertyValue(BBBCoreConstants.REPORT_ERROR_DESC, reportStatusVO.getErrorDescription());
						 reportStatus[0].setPropertyValue(BBBCoreConstants.REPORT_OP_STATUS, reportStatusVO.getReportOperationStatus()); 
					     getOmnitureReportRepository().updateItem(reportStatus[0]);
					     logDebug("updateReportStatusByReportId Item updated with ItemId:["+reportStatus[0].getRepositoryId()+"],reportId:["+reportId+"]");
					     isUpdated=true;
					  }
					 else
					 {
						 logDebug("updateReportStatusByReportId ItemNot found with reportId:"+reportId);
						
					 }
			      }
			 	catch (RepositoryException rEx)
			 	{
					logError("Repository Exception in updateReportStatusByReportId:"+rEx);
					BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "updateReportStatusByReportId");
					throw rEx;
				} 
		  }
		 
		 BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "updateReportStatusByReportId");
		 
		 logDebug(CLS_NAME+" updateReportStatusByReportId method ends and reportId:["+reportStatusVO.getReportId()+"],isUpdated:["+isUpdated+"]");
		 
		 return isUpdated; 
	 }
	 
	/**
	 * This method will return reportId as key and EmailStatusVo as value for
	 * those reportId which can be cancelled in case if there is any report in
	 * queueStatusList which exceeds threshold
	 * 
	 * @param queueStatusList
	 *            , return reportId which need to be canceled .
	 * @return
	 */
		public Map<String,OmnitureReportEmailContentVO> getCancellableReportIdMap(List<OmnitureReportStatus> queueStatusList) throws RepositoryException
		{
			
			 logDebug(CLS_NAME+" [getCancellableReportIdMap] method start");
	    	
			 BBBPerformanceMonitor.start(OMNITURE_PERFORMANCE + "getCancellableReportIdMap");
			 
			Map<String,OmnitureReportEmailContentVO> cancelableReportIdMap= new HashMap<String,OmnitureReportEmailContentVO>();
			try{
					  if(queueStatusList != null && queueStatusList.size()>0)
						{	
						  	// A map object to save Configuration of report to avoid Repository call.Where key is combination of Concept+ReportType
							Map<String,OmnitureReportConfigVo> reportConfigVoMap=new HashMap<String,OmnitureReportConfigVo>();
							
							List<OmnitureReportConfigVo> reportConfigList= getOmnitureReportConfig();
							String configuredUserId= getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.OMNITURE_API_USER_NAME_IN_RESPONSE, BBBCoreConstants.OMNITURE_API_DEFAULT_USER_NAME_IN_RESPONSE);
							if(reportConfigList != null && reportConfigList.size()>0)
							{
							  for(OmnitureReportConfigVo omnitureReportConfigVo: reportConfigList)
							   {
								  reportConfigVoMap.put(omnitureReportConfigVo.getConcept()+omnitureReportConfigVo.getReportType(), omnitureReportConfigVo);
							   }
								constructCancelableReportIdEmailVoMap(queueStatusList, reportConfigVoMap, configuredUserId, cancelableReportIdMap);
							}
					  }
				  else
					  {
						 logDebug("There is not any in-progress or waiting report in the Omniture Queue");
					  }
				}
				catch (final RepositoryException   rex)
				{
				logError("There is RepositoryException in getCancellableReportIdMap due to :"+rex);
				BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "getCancellableReportIdMap");	
				throw rex;
				}
			
			  BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "getCancellableReportIdMap");	
			  
			  logDebug(CLS_NAME+" [getCancellableReportIdMap]  method end");
	    	
			 
		  return cancelableReportIdMap;
		}

		/**
		 * Method used to get reportStatus repository item from the repository by reportId
		 * @param reportId
		 * @return RepositoryItem
		 * @throws RepositoryException 
		 */
		public RepositoryItem[] getReportStatusByReportIdOrBatchId(String reportId,String batchId) throws RepositoryException
		{
			 logDebug(CLS_NAME+" [getReportStatusByReportIdOrBatchId] method start :reportId:["+reportId+"],batchId:["+batchId+"]");
		    	
			 BBBPerformanceMonitor.start(OMNITURE_PERFORMANCE + "getReportStatusByReportIdOrBatchId");
			 
				RepositoryItem[] reportStatus=null;
				RepositoryItemDescriptor reportStatusDesc;
				try {
					
					reportStatusDesc = this.getOmnitureReportRepository().getItemDescriptor(BBBCoreConstants.REPORT_STATUS);
					RepositoryView view=reportStatusDesc.getRepositoryView();
					QueryBuilder queryBuilder = view.getQueryBuilder();
					QueryExpression reportIdPropQueyEx =null;
					QueryExpression reportIdConstQueryEx =null;
					
					if(StringUtils.isNotBlank(reportId))
					{	  
						  logDebug(CLS_NAME+" [getReportStatusByReportIdOrBatchId] build query by reportId");
			              reportIdPropQueyEx =     queryBuilder.createPropertyQueryExpression(BBBCoreConstants.REPORT_ID);
				          reportIdConstQueryEx =    queryBuilder.createConstantQueryExpression(reportId);
					}
					else if(StringUtils.isNotBlank(batchId))
					{
						  logDebug(CLS_NAME+" [getReportStatusByReportIdOrBatchId] build query by batchId");
						  reportIdPropQueyEx =     queryBuilder.createPropertyQueryExpression(BBBCoreConstants.REPORT_BATCH_ID);
				          reportIdConstQueryEx =    queryBuilder.createConstantQueryExpression(batchId);
					}
			        Query reportIdEqualQuery =   queryBuilder.createComparisonQuery(reportIdPropQueyEx, reportIdConstQueryEx, QueryBuilder.EQUALS);
		            reportStatus = view.executeQuery(reportIdEqualQuery);
				    } 
					catch (RepositoryException e) 
					{
						logError("RepositoryException in getReportStatusByReportIdOrBatchId:"+e);
						BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "getReportStatusByReportIdOrBatchId");
						throw e;
				    }
				
				BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "getReportStatusByReportIdOrBatchId");
				 
				logDebug(CLS_NAME+" [getReportStatusByReportIdOrBatchId] method ends");
				
			return reportStatus;
		}
		
		/**
		 * Method used to get reportStatus repository item from the repository by status
		 * @param reportId
		 * @return RepositoryItem
		 * @throws RepositoryException 
		 */
		public RepositoryItem[] getReportStatusByStatus(String status) throws RepositoryException
		{		
				logDebug(CLS_NAME+" [getReportStatusByStatus] method start:statusString:"+status);
	    	
				BBBPerformanceMonitor.start(OMNITURE_PERFORMANCE + "getReportStatusByStatus");
			 
				RepositoryItem[] reportStatus=null;
				RepositoryItemDescriptor reportStatusDesc;
				try {
					
					reportStatusDesc = this.getOmnitureReportRepository().getItemDescriptor(BBBCoreConstants.REPORT_STATUS);
					RepositoryView view=reportStatusDesc.getRepositoryView();
					QueryBuilder queryBuilder = view.getQueryBuilder();
		            QueryExpression reportIdPropQueyEx =     queryBuilder.createPropertyQueryExpression(BBBCoreConstants.REPORT_OP_STATUS);
			        QueryExpression reportIdConstQueryEx =    queryBuilder.createConstantQueryExpression(status);
			        Query reportIdEqualQuery =   queryBuilder.createComparisonQuery(reportIdPropQueyEx, reportIdConstQueryEx, QueryBuilder.EQUALS);
			        SortDirectives sortDirectives = new SortDirectives();
			        sortDirectives.addDirective(new SortDirective(BBBCoreConstants.ID, SortDirective.DIR_ASCENDING));
		            reportStatus = view.executeQuery(reportIdEqualQuery, new QueryOptions(0, -1, sortDirectives, null));
				    } 
					catch (RepositoryException e)
					{
						 logError("RepositoryException in getReportStatusByStatus"+e.getMessage());
						 BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "getReportStatusByStatus");
						 throw e;
				     }
				
				BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "getReportStatusByStatus");
				logDebug(CLS_NAME+" [getReportStatusByStatus] method End");
				
			return reportStatus;
		}
		
		
		/**
		 * Method used to get reportStatus repository item from the repository by Date
		 * @param reportId
		 * @return RepositoryItem
		 * @throws RepositoryException 
		 */
		public RepositoryItem[] getReportStatusByDate(Date currentDate ) throws RepositoryException
		{		
				logDebug(CLS_NAME+" [getReportStatusByDate] method start:currentDate:"+currentDate);
	    	
				BBBPerformanceMonitor.start(OMNITURE_PERFORMANCE + "getReportStatusByDate");
			
				RepositoryItem[] reportStatus=null;
				RepositoryItemDescriptor reportStatusDesc;
				try {
					
					reportStatusDesc = this.getOmnitureReportRepository().getItemDescriptor(BBBCoreConstants.REPORT_STATUS);
					RepositoryView view=reportStatusDesc.getRepositoryView();
					QueryBuilder queryBuilder = view.getQueryBuilder();
		            QueryExpression reportIdPropQueyEx =     queryBuilder.createPropertyQueryExpression(BBBCoreConstants.REPORT_QUEUED_TIME);
			        QueryExpression reportIdConstQueryEx =    queryBuilder.createConstantQueryExpression(currentDate);
			        Query reportIdEqualQuery =   queryBuilder.createComparisonQuery(reportIdPropQueyEx, reportIdConstQueryEx, QueryBuilder.EQUALS);
		            reportStatus = view.executeQuery(reportIdEqualQuery);
				    } 
				   catch (RepositoryException e)
				   {
						 logError("RepositoryException in getReportStatusByDate: error msg:"+e.getMessage());
						 BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "getReportStatusByDate");
						 throw e;
				    }
				
				BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "getReportStatusByDate");
				
			   logDebug(CLS_NAME+" [getReportStatusByDate] method End");
			return reportStatus;
		}
		
		/**
		 * Used to get all available report configurations from repository . 
		 * @return List<OmnitureReportConfigVo> , return list of VO of report configuration
		 * @throws RepositoryException 
		 */
		public List<OmnitureReportConfigVo> getOmnitureReportConfig() throws RepositoryException
		{
			logDebug(CLS_NAME+" [getOmnitureReportConfig] method Start:");
	    	
			BBBPerformanceMonitor.start(OMNITURE_PERFORMANCE + "getOmnitureReportConfig");
			
			List<OmnitureReportConfigVo> reportConfigVoList=null;
			try {
				
				RepositoryItemDescriptor reportConfigDesc = this.getOmnitureReportRepository().getItemDescriptor(BBBCoreConstants.OMNITURE_REPORT_CONFIG);
				RepositoryView view=reportConfigDesc.getRepositoryView();
				QueryBuilder queryBuilder=view.getQueryBuilder();
				Query fetchAllItem=queryBuilder.createUnconstrainedQuery();
				RepositoryItem[] allConfigItem=view.executeQuery(fetchAllItem);
				
					if(allConfigItem != null){
						reportConfigVoList= new ArrayList<OmnitureReportConfigVo>(allConfigItem.length);
						for(RepositoryItem configItem :allConfigItem){
							OmnitureReportConfigVo omnitureReportConfigVo= new OmnitureReportConfigVo();
							omnitureReportConfigVo.setReportConfigId((String) configItem.getPropertyValue(BBBCoreConstants.REPORT_CONFIG_ID));
							omnitureReportConfigVo.setMethodType((String) configItem.getPropertyValue(BBBCoreConstants.METHOD_TYPE));
							omnitureReportConfigVo.setReportType((String) configItem.getPropertyValue(BBBCoreConstants.REPORT_TYPE));
							omnitureReportConfigVo.setConcept((String) configItem.getPropertyValue(BBBCoreConstants.CONCEPT));
							omnitureReportConfigVo.setFreq((String) configItem.getPropertyValue(BBBCoreConstants.REPORT_FREQ));
							omnitureReportConfigVo.setCancelAllowed((String) configItem.getPropertyValue(BBBCoreConstants.REPORT_CANCEL_ALLOWED));
							omnitureReportConfigVo.setPriority((Integer) configItem.getPropertyValue(BBBCoreConstants.REPORT_PRIORITY));
							omnitureReportConfigVo.setCount((Integer) configItem.getPropertyValue(BBBCoreConstants.REPORT_COUNT));
							omnitureReportConfigVo.setBatchCount((Integer) configItem.getPropertyValue(BBBCoreConstants.REPORT_BATCH_COUNT));
							omnitureReportConfigVo.setNumOfThread((Integer) configItem.getPropertyValue(BBBCoreConstants.REPORT_NUMBER_OF_THREAD));
							omnitureReportConfigVo.setThresholdDays((Integer) configItem.getPropertyValue(BBBCoreConstants.REPORT_THRESHOLD_DAYS));
							omnitureReportConfigVo.setReportTimeRange((Integer) configItem.getPropertyValue(BBBCoreConstants.REPORT_TIME_RANGE));
							omnitureReportConfigVo.setReportTimeRangeAdjustment((Integer) configItem.getPropertyValue(BBBCoreConstants.REPORT_TIME_RANGE_ADJUST));
							omnitureReportConfigVo.setProductCount((Integer) configItem.getPropertyValue(BBBCoreConstants.REPORT_PRODUCT_COUNT));
							omnitureReportConfigVo.setLastSuccessRunDate((Timestamp) configItem.getPropertyValue(BBBCoreConstants.REPORT_LAST_SUCCESS_RUN_DATE));
							omnitureReportConfigVo.setStartWith((Integer) configItem.getPropertyValue(BBBCoreConstants.START_WITH));

							reportConfigVoList.add(omnitureReportConfigVo);
						}
					}
			    } 
				catch (RepositoryException e)
			    {
				       logError("RepositoryException in getOmnitureReportConfig:"+e.getMessage());
				       BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "getOmnitureReportConfig");
				       throw e;
			    }
			
			BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "getOmnitureReportConfig");
			
			logDebug(CLS_NAME+" [getOmnitureReportConfig] method End");
			
		 return reportConfigVoList;
		}
		
		/**
		 * used to get ReportId with EmailContentVO in Map which can be cancel.
		 * @param queueStatusList
		 * @param reportConfigVoMap
		 * @param configuredUserId
		 * @param cancelableReportIdMap
		 * @throws RepositoryException
		 */
		private boolean constructCancelableReportIdEmailVoMap(List<OmnitureReportStatus> queueStatusList,Map<String,OmnitureReportConfigVo> reportConfigVoMap,String configuredUserId,Map<String,OmnitureReportEmailContentVO> cancelableReportIdMap) throws RepositoryException
		{	
			logDebug(CLS_NAME+" [constructCancelableReportIdEmailVoMap] method Start");
	    	
			BBBPerformanceMonitor.start(OMNITURE_PERFORMANCE + "constructCancelableReportIdEmailVoMap");
			boolean isThresholdExceeded=false;
			for(OmnitureReportStatus omnitureReportStatus :queueStatusList)
			{
				String reportId=omnitureReportStatus.getReportID();
				String userId=omnitureReportStatus.getUser();
				if(StringUtils.isNotBlank(configuredUserId) && configuredUserId.equalsIgnoreCase(userId))
				{
					     RepositoryItem[] reportStatusList=getReportStatusByReportIdOrBatchId(reportId,null);
					     if(reportStatusList != null && reportStatusList.length>0)
					       {
							    RepositoryItem reportStatus=reportStatusList[0];
								String concept=(String) reportStatus.getPropertyValue(BBBCoreConstants.CONCEPT);
								String reportType=(String) reportStatus.getPropertyValue(BBBCoreConstants.REPORT_TYPE);
								Date queuedDate= (Date) reportStatus.getPropertyValue(BBBCoreConstants.REPORT_QUEUED_TIME);
								/*
								 * this key used to pull configuration of report from the Map
								 */
								String conceptAndReportTypeAsKey= concept+reportType;
									if(reportConfigVoMap.containsKey(conceptAndReportTypeAsKey)&& reportConfigVoMap.get(conceptAndReportTypeAsKey) != null)
									{
											String isCancelAllowed=reportConfigVoMap.get(conceptAndReportTypeAsKey).getCancelAllowed();
											if("Y".equalsIgnoreCase(isCancelAllowed))
											{
												OmnitureReportEmailContentVO omnitureReportEmailContentVO = new OmnitureReportEmailContentVO();
												omnitureReportEmailContentVO.setReportId(reportId);
												omnitureReportEmailContentVO.setReportType(reportType);
												omnitureReportEmailContentVO.setQueuedDate(queuedDate);
												omnitureReportEmailContentVO.setConcept(concept);
												omnitureReportEmailContentVO.setExceptionDetails(BBBCoreConstants.OMNITURE_THRESHOLD_EXCEEDED);
												cancelableReportIdMap.put(reportId,omnitureReportEmailContentVO);
												logDebug("getCancelableReportIdEmailVoMap:Threshold Exceeded ,adding :reportId:"+reportId);
												
											 }
											if(! isThresholdExceeded && "N".equalsIgnoreCase(isCancelAllowed))
											{
												 Integer thresholdDays=reportConfigVoMap.get(conceptAndReportTypeAsKey).getThresholdDays();
												 Date lastSuccessRun= reportConfigVoMap.get(conceptAndReportTypeAsKey).getLastSuccessRunDate();
												 	if(lastSuccessRun != null && thresholdDays != null && thresholdDays >0 )
												 	{
													 	int lapseDays = calculateQueuedDays(lastSuccessRun);
														if(thresholdDays < lapseDays)
														{ 
															isThresholdExceeded=true;
															logDebug("isThresholdExceededForReport: Threshold exceeded for Report:["+reportId+"]:reportType:["+reportType+"]:thresholdDays:["+thresholdDays+"]:lapseDays:["+lapseDays+"]");
															
														}
												 	}
												else{
												 		logDebug("isThresholdExceededForReport: blank queuedDate or thresholdDays for ReportId:["+reportId+"]");	
												 	}
											}
									}
						    }
				 }
			 }
			
			// if threshold is not exceeded for any report,do not cancel any report
			if( ! isThresholdExceeded  && cancelableReportIdMap.size() > 0)
			{
				cancelableReportIdMap.clear();
			}
			
			BBBPerformanceMonitor.end(OMNITURE_PERFORMANCE + "constructCancelableReportIdEmailVoMap");
			
			logDebug(CLS_NAME+" [constructCancelableReportIdEmailVoMap] method end: isThresholdExceeded:"+isThresholdExceeded+",calcellableReportId:["+cancelableReportIdMap.keySet()+"]");
			
			return isThresholdExceeded;
		}
		
	    /**
	     * calculate day difference between date.
	     * @param queuedDate
	     * @return int, day difference.
	     */
		private  int calculateQueuedDays(Date queuedDate){
			logDebug(CLS_NAME+" [calculateQueuedDays] method start:queuedDate:"+queuedDate);
			 Date todayDate=new Date();
			 if(todayDate.getTime() > queuedDate.getTime()){
			 LocalDate  localDate1=new LocalDate(todayDate.getTime());
			 LocalDate  localDate2=new LocalDate(queuedDate.getTime());
			 int daysDiff =Days.daysBetween(localDate2, localDate1).getDays();
			 
			 logDebug(CLS_NAME+" [calculateQueuedDays] method End :daysDiff:"+daysDiff);
			 
			 return daysDiff;
			 }
			 else
			 return 0;
		}
		
		/**
		 * Commit the connection if its success while executing the query otherwise rollback
		 * @param reportID 
		 * @param removeExistingRecords 
		 * 
		 */
		protected int commitConnectionOrRollback(OmnitureGetResponseVO omnitureGetResponseVO, Connection connection,
				boolean success, int record, String reportID) {
			try{
				if(success){
					connection.commit();
				}	
				else{
					connection.rollback();
					logError("Some Exception occurred hence Making the operation Rollback for report ID ::" + reportID);
				}	
				
			}catch(SQLException e){
				record = 0;
				logError("SQL Exception ocurred for while committing/closing the preparedStatement/connection ", e);
				omnitureGetResponseVO.setError(e.getMessage());
				omnitureGetResponseVO.setError_description(e.getMessage());
			}
			return record;
		}
/**
 * This method pull reportStatus Item from Omniture_report_status table and returns list of reportItem		
 * @param sqlQuery
 * @return
 * @throws BBBSystemException
 */
public List<ReportStatusVO> getReportStatusBySQLQuery(String sqlQuery) throws BBBSystemException {
			
			logDebug(CLS_NAME+":getReportStatusBySQLQuery sqlQuery:["+sqlQuery+"]");
			
			BBBPerformanceMonitor.start(CLS_NAME + "getReportStatusBySQLQuery");
			List<ReportStatusVO> reportDetailVoList=null;
			Connection connection=null;
			PreparedStatement preparedStatement = null;
			ResultSet rs=null;
			 try {
						  
						  	
				 connection = ((GSARepository)getOmnitureReportRepository()).getDataSource().getConnection();
				 preparedStatement=connection.prepareStatement(sqlQuery);
				 rs= preparedStatement.executeQuery();
				 if(rs != null){
						reportDetailVoList= new ArrayList<ReportStatusVO>();
						while(rs.next()){
						ReportStatusVO reportStatusVO= new ReportStatusVO();
						reportDetailVoList.add(reportStatusVO);
						
						reportStatusVO.setReportId(rs.getString(BBBCoreConstants.REPORT_ID_DDL));
						reportStatusVO.setReportType(rs.getString(BBBCoreConstants.REPORT_TYPE_DDL));
						reportStatusVO.setQueuedDate(rs.getDate(BBBCoreConstants.REPORT_QUEUED_DATE_DDL));
						reportStatusVO.setReportGetTime1(rs.getTimestamp(BBBCoreConstants.REPORT_GET_TIME_FIRST_DDL));
						reportStatusVO.setConcept(rs.getString(BBBCoreConstants.CONCEPT));
						reportStatusVO.setErrorCode(rs.getString(BBBCoreConstants.REPORT_ERROR_CODE_DDL));
						reportStatusVO.setErrorDescription(rs.getString(BBBCoreConstants.REPORT_ERROR_DESCRIPTION_DDL));
						reportStatusVO.setReportOperationStatus(rs.getString(BBBCoreConstants.REPORT_OP_STATUS_DDL));
						reportStatusVO.setBatchId(rs.getString(BBBCoreConstants.REPORT_BATCH_ID_DDL));
						reportStatusVO.setBatchSeq(rs.getInt(BBBCoreConstants.REPORT_BATCH_SEQ_DDL));
						reportStatusVO.setCount(rs.getInt(BBBCoreConstants.REPORT_COUNT_DDL));
						reportStatusVO.setRangeFrom(rs.getInt(BBBCoreConstants.REPORT_RANGE_FROM_DDL));
						reportStatusVO.setRangeTo(rs.getInt(BBBCoreConstants.REPORT_RANGE_TO_DDL));
						reportStatusVO.setAttempts(rs.getInt(BBBCoreConstants.GET_ATTEMPTS_DDL));  
						} 
					}else{
						logDebug(CLS_NAME+"[setResult] | No results found for request");
					}
				} catch (SQLException sqlException) {
					logError("sqlException in getReportStatusBySQLQuery due to : "+sqlException);
					throw new BBBSystemException("sqlException in getReportStatusBySQLQuery due to : "+sqlException) ;
				}
			 	catch (Exception exception) {
					logError("Exception in getReportStatusBySQLQuery due to : "+exception);
					throw new BBBSystemException("Exception in getReportStatusBySQLQuery due to : "+exception) ; 
				}
				finally
				{ 
					 try{
							if(rs != null)
							{
								rs.close();
							}
							if(preparedStatement != null)
							{
								preparedStatement.close();
							}
							if(connection != null)
							{
								connection.close();
							}
						 }
					 	catch (SQLException e) {
					 		logError(CLS_NAME + " Error in getReportStatusBySQLQuery:"+e);
					 	}
					 
				}
			 
		 BBBPerformanceMonitor.end(CLS_NAME + "getReportStatusBySQLQuery");
		 
		 logDebug(CLS_NAME+":getReportStatusBySQLQuery End:reportDetailVoList:"+reportDetailVoList);
		 
		 return reportDetailVoList;
		}
		
}
