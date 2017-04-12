package com.bbb.thirdparty.omniture.tools;

import java.io.IOException;
import java.util.List;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.thirdparty.omniture.vo.OmnitureGetResponseVO;
import com.bbb.thirdparty.omniture.vo.OmnitureQueueResponseVO;
import com.bbb.thirdparty.omniture.vo.OmnitureReportEmailContentVO;
import com.bbb.thirdparty.omniture.vo.OmnitureReportStatus;
import com.bbb.thirdparty.omniture.vo.QueueRequestVO;

/**
 * Tools interface to declare common methods used in omniture report API call invocation 
 * processing
 * 
 * @author Sapient
 *
 */
public interface OmnitureReportAPITools {
	
	String SMTP_HOST_NAME = "smtp_host_name";
	String MAP_SMTP_HOST = "smtpHostName";
	String EMAIL_CONTENT = "emailContent";
	String SUBJECT = "subject";
	String RECIPIENT_TO = "RecipientTo";
	String RECIPIENT_FROM = "RecipientFrom";
	String ARCHIVAL_RECORDS_FLOW = "fromArchivalOfRecords";
	String GET_FLOW = "fromGetReport";
	String QUEUE_FLOW = "fromQueueReport";
	String PURGE_FLOW = "fromPurgeOmniDataFlow";
	String GENERAL_GET_EXCEPRION_MSSG = "Some Exception occurred while Getting the Report Data";
	String NO_DATE_MSG = "Empty Response in the Report";
	String EXCEPTION_DETAILS = "exceptionDetails";
	String CLS_NAME = "OmnitureReportAPIToolsImpl";
	String INSERT_REPORT_DATA_SQL = "INSERT INTO BBB_OMNITURE_REPORT_DATA (ID, REPORT_ID, REPORT_SUITE,REPORT_TYPE, PERIOD, CONCEPT, BOOST_SCORE, PRODUCT_ID, KEYWORD, RANK,LAST_MODIFIED_DATE) VALUES"
			+ "(OMNITURE_BOOSTING_SEQ.nextVal,?,?,?,?,?,?,?,?,?,?)";
	final String ARCHIVE_REPORT_DATA_SQL = "{call BBB_CORE.OMNITURE_REPORT_DATA_PKG.ARCHIVE_OMNITURE_DATA(?,?,?,?)}";
	final String PURGE_REPORT_DATA_SQL ="{call BBB_CORE.OMNITURE_REPORT_DATA_PKG.PURGE_OMNITURE_DATA(?, ?, ?, ?)}";
	
	String ARCHIVE_EXISTING_DATA = "insert into OMNITURE_ARCHIVED_DATA (id,report_id,report_type,report_suite,period,concept,boost_score,product_id,keyword,rank) "+
			" select OMNITURE_ARCHIVE_SEQ.nextVal,report_id,report_type,report_suite,period,concept,boost_score,product_id,keyword,rank " +
			 " from BBB_OMNITURE_REPORT_DATA where concept = ? and report_id != ? and report_type = ? and keyword = ?";
	String DELETE_EXISTING_DATA = "delete from BBB_OMNITURE_REPORT_DATA where concept = ? and report_id != ? and report_type = ? and keyword = ?";
	
	String INSERT_L2L3BRANDS_DATA_SQL = "INSERT INTO BBB_L2L3Brand_OMNITURE_DATA  (ID, REPORT_ID, REPORT_SUITE,REPORT_TYPE, PERIOD, CONCEPT, PRODUCT_LIST, IDENTIFIER,LAST_MODIFIED_DATE)"
			+ " VALUES (OMNITURE_BOOSTING_SEQ.nextVal,?,?,?,?,?,?,?,?)";
	
	String UPDATE_L2L3BRANDS_DATA_SQL = "update BBB_L2L3Brand_OMNITURE_DATA set product_list =?,last_modified_date=?,report_id = ?"
							+ "where identifier =? and report_type=? and concept=?";
	
	String MERGE_L2L3BRANDS_DATA_SQL = "merge into BBB_L2L3Brand_OMNITURE_DATA D using (select ? as identifier ,? as report_type , ? as concept from dual) S "+ 
			" ON (D.identifier = S.identifier and D.report_type = S.report_type and D.concept = S.concept) when matched then update set D.product_list = ?, " + 
			" D.last_modified_date=?,D.report_id = ? when not matched then insert (D.ID, D.REPORT_ID, D.REPORT_SUITE,D.REPORT_TYPE, D.PERIOD, D.CONCEPT, D.PRODUCT_LIST, " + 
			"D.IDENTIFIER, D.LAST_MODIFIED_DATE) VALUES (OMNITURE_BOOSTING_SEQ.nextVal,?,?,?,?,?,?,?,?) ";
	
	
	String INSERT_POPULAR_KEYWORD_DATA_SQL = "insert into BBB_OMNITURE_REPORT_DATA (ID, REPORT_ID, REPORT_SUITE, REPORT_TYPE, PERIOD, CONCEPT, PRODUCT_ID, " + 
			"KEYWORD, LAST_MODIFIED_DATE) VALUES (OMNITURE_BOOSTING_SEQ.nextVal,?,?,?,?,?,?,?,?) ";
	
	String ARCHIVE_POPULAR_SEARCH_DATA = "insert into OMNITURE_ARCHIVED_DATA (id,report_id,report_type,report_suite,period,concept,boost_score,product_id,keyword,rank)"+
			" select OMNITURE_ARCHIVE_SEQ.nextVal,report_id,report_type,report_suite,period,concept,boost_score,product_id,keyword,rank " +
			" from BBB_OMNITURE_REPORT_DATA where concept = ? and report_id not in "+ 
			" (select report_id from omniture_report_status where batch_id IN (select batch_id from "+ 
			" omniture_report_status where report_id = ?)) and report_type = ? and report_id <> ?";
	
	String DELETE_POPULAR_SEARCH_DATA = "delete from bbb_omniture_report_data where concept=? and report_id not in "+ 
			" (select report_id from omniture_report_status where batch_id IN (select batch_id from " + 
			" omniture_report_status where report_id = ? )) and report_type=?  and report_id <> ?"; 	
	
	public static final String OMNITURE_PERFORMANCE = "Omniture_call_";
	String REPORT_CANCEL_MSG = "Canceling the report id's in batch because first report id has failed";
	String RETRY_FAIL_MSG = "Marking the status as Fail because unable to cancel the report after 3 re-attempts of cancel.";
	
	/**
	 * This method is used to Queue and Get the report id for individual concept from Omniture
	 * @param concept
	 * @param reportType
	 *//*
	void queueOrGetReport(String concept, String reportType);*/
	
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
 
	RepositoryItem[] executeRQLQuery(String rqlQuery, Object[] params, String viewName, MutableRepository repository);
	
	/** This method is used to invoke Queue method and insert report status for individual concept from Omniture
	 * Also sends email in case of queue success/failure
	 * @param concept
	 * @param reportType
	 * @param reportIndex
	 * @param reportEmailContentList
	 */
	boolean doQueueReport(QueueRequestVO requestVO, OmnitureReportEmailContentVO reportEmailContentVO);
	
	/** 
	 * This method is used to retrieve stack trace from exception
	 * @param exception
	 * @return
	 * @throws IOException
	 */
	String getExceptionTrace(Exception exception);
	
	/**This method is used to invoke Get method and does the report processing for individual concept from Omniture
	 * Also sends email in case of queue success/failure
	 * @param concept
	 * @param statusItem
	 * @param firstReport
	 * @param highestRankMap
	 * @param reportEmailContent
	 *  @param archiveFailedStatusEmailVOList
	 */
	String getReportDetailsForAlreadyQueued(String concept, MutableRepositoryItem statusItem, OmnitureReportEmailContentVO reportEmailContent,List<OmnitureReportEmailContentVO>  archiveFailedStatusEmailVOList);
	
	/**
	 * This method is used to Send Email on Success or Failure of Report Processing
	 * @param concept
	 * @param flow
	 * @param reportEmailContentList
	 */
    void sendEmail(String concept, String flow, List<OmnitureReportEmailContentVO> reportEmailContentList);
    
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
	boolean insertReportStatus(final OmnitureQueueResponseVO queueResponseVO,QueueRequestVO requestVO);
	
	/** This method insert the report data into database using Prepared Statement
	 * @param getResponseVO
	 * @param reportID
	 * @param concept
	 * @param highestRankMap
	 * @param reportType
	 * @return status
	 * @throws BBBSystemException
	 */
	int insertReportData(final OmnitureGetResponseVO getResponseVO, String reportID, String concept,  String reportType);
	
	/**
     * This method is used to remove stale omniture report data
     * from archive table.
     * @param concept
     * @throws BBBSystemException
     */
    void purgeOmnitureDataFromArchiveTable(String concept);

    /**
	 * This method is used to get the Omniture Report Queue status. If queue is
	 * empty then it returns BLANK JSONArray otherwise JSONArray of report IDs
	 * with details.
	 * 
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws IOException
	 */
    public List<OmnitureReportStatus> getQueueStatusReport() throws BBBSystemException, BBBBusinessException, IOException;

}
