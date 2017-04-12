package com.bbb.thirdparty.omniture.manager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.transaction.SystemException;

import org.apache.commons.lang.time.DateUtils;

import atg.core.util.StringUtils;
import atg.multisite.Site;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextImpl;
import atg.multisite.SiteContextManager;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceMap;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.idgen.IdGenerator;
import atg.service.idgen.IdGeneratorException;
import atg.service.scheduler.CalendarSchedule;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.thirdparty.omniture.tools.OmnitureBoostedProductTools;
import com.bbb.thirdparty.omniture.tools.OmnitureReportAPITools;
import com.bbb.thirdparty.omniture.vo.OmnitureReportConfigVo;
import com.bbb.thirdparty.omniture.vo.OmnitureReportEmailContentVO;
import com.bbb.thirdparty.omniture.vo.OmnitureReportStatus;
import com.bbb.thirdparty.omniture.vo.QueueRequestVO;
import com.bbb.thirdparty.omniture.vo.ReportStatusVO;
import com.bbb.utils.BBBUtility;

public class OmnitureReportAPIManager extends GenericService {
	
	
	private Repository omnitureReportRepository;
	
	private ServiceMap omnitureReportToolsMap;
	
	private OmnitureBoostedProductTools defaultTool;
	private List<String> priorityEmailEventList; 
	private IdGenerator idGenerator;
	private LblTxtTemplateManager mLabelManager;
	


	/**
	 * @return the labelManager
	 */
	public final LblTxtTemplateManager getLabelManager() {
		return mLabelManager;
	}

	/**
	 * @param pLabelManager
	 *            the labelManager to set
	 */
	public final void setLabelManager(LblTxtTemplateManager pLabelManager) {
		mLabelManager = pLabelManager;
	}

	private String omnitureBatchReportSeedName;
	
	private Map<String,String> conceptSiteMap;
	
	private SiteContextManager siteContextManager;
	private static final String CLS_NAME = "OmnitureReportAPIManager";

	/**
	 * This method used to cancel those reportId from Omniture Queue which have
	 * cancel Allowed flag=Y in case if threshold exceeded for a report which
	 * have cancelAllowed=N 
	 * 
	 *  
	 * Also get all running state reports from get queue
	 * call and prepare content for email
	 * 
	 * @param queueStatusList
	 *            , This parameter used to store Omniture report status which
	 *            are in waiting or running state
	 * @param reportEmailContentList
	 *            , This parameter will be used to store Report which are being
	 *            cancel from the Omninture queue, later this list will be used
	 *            to generated status email;
	 */
	public void checkQueue(List<OmnitureReportStatus> queueStatusList,
			Map<String, List<OmnitureReportEmailContentVO>> emailContentListMap)
			throws BBBSystemException, BBBBusinessException, RepositoryException,IOException {
		
		logDebug( CLS_NAME + " [ checkQueue ] method Start:: ");
		
		BBBPerformanceMonitor.start(OmnitureReportAPITools.OMNITURE_PERFORMANCE + "checkQueue");
		
		List<OmnitureReportEmailContentVO> emailContentList= new ArrayList<OmnitureReportEmailContentVO>();
		List<OmnitureReportEmailContentVO> inProgressEmailContentList= new ArrayList<OmnitureReportEmailContentVO>();
		emailContentListMap.put(BBBCoreConstants.OMNITURE_CHECK_QUEUE, emailContentList);
		emailContentListMap.put(BBBCoreConstants.INPROGRESS_EMAIL_EVENT, inProgressEmailContentList);
		
		try {
			
			List<OmnitureReportStatus> omniQueueStatusList=getDefaultTool().getQueueStatusReport();
			
			
		    if(omniQueueStatusList == null || omniQueueStatusList.size() ==0)
		    {
		    	logDebug( CLS_NAME + " [ checkQueue ] omniQueueStatusList is empty:: ");
		    	return ;
		    }
		    
		    queueStatusList.addAll(omniQueueStatusList);
		    // to prepare email content for inProgress report types
		    inProgressEmailContent(queueStatusList,inProgressEmailContentList);
		    
			Map<String,OmnitureReportEmailContentVO> cancellableReportMap=getDefaultTool().getCancellableReportIdMap(queueStatusList);
			
			Set<String> cancelledReportIdSet=null;
			
			if(cancellableReportMap != null && cancellableReportMap.size()>0)
			{   
				cancelledReportIdSet = new HashSet<>(cancellableReportMap.size());
				
				for(String reportId :cancellableReportMap.keySet())
				{
					   try{ 
								boolean isReportCancelled=getDefaultTool().cancelQueuedReport(reportId);
								logDebug("OmnitureReportAPIManager checkQueue: Report cancelled from Omniture Queue with reportId:["+reportId+"],isReportCancelled:["+isReportCancelled+"]");
								if(isReportCancelled)
								{
										ReportStatusVO reportStatusVO=new ReportStatusVO();
										reportStatusVO.setReportId(reportId);
										reportStatusVO.setErrorCode(BBBCoreConstants.OMNITURE_REPORT_STATUS_CANCEL);
										reportStatusVO.setErrorDescription(BBBCoreConstants.OMNITURE_THRESHOLD_EXCEEDED);
										reportStatusVO.setReportOperationStatus(BBBCoreConstants.OMNITURE_REPORT_STATUS_CANCEL);
										Boolean isMarkedCancelInDB=getDefaultTool().updateReportStatusByReportId(reportStatusVO);
										if(isMarkedCancelInDB)
										{
											OmnitureReportEmailContentVO omnitureReportEmailContentVO =cancellableReportMap.get(reportId);
										    omnitureReportEmailContentVO.setOpStatus(BBBCoreConstants.OMNITURE_REPORT_STATUS_CANCEL);
											emailContentList.add(omnitureReportEmailContentVO);
										    cancelledReportIdSet.add(reportId);
										    logDebug("OmnitureReportAPIManager checkQueue: Report cancelled In repository with reportId:["+reportId+"]");
										}
								}
					     }catch(BBBSystemException  | IOException exception )
					     {
					    	 logError( CLS_NAME + " [ checkQueue ] Error in Canceling the report :reportId: ["+reportId+"]: error:-["+exception+"]");
					     }
				}
				if(cancelledReportIdSet.size()>0)
				{
					updateCancelFlagInQueueStatusList(queueStatusList,cancelledReportIdSet);
				}
				
			}
	          
			
			
				
			
		} catch (BBBSystemException | RepositoryException | IOException   exception)
		{
			logError("Exception in OmnitureReportAPIManager: "+exception);
			BBBPerformanceMonitor.end(OmnitureReportAPITools.OMNITURE_PERFORMANCE + "checkQueue");
			throw exception;	
		}
		
		BBBPerformanceMonitor.end(OmnitureReportAPITools.OMNITURE_PERFORMANCE + "checkQueue");
		
		logDebug( CLS_NAME + " [ checkQueue ] method End:: ");
	}
	/**
	 * This method will iterate over data got from getQueue call and set it in inProgressEmailContentList to 
	 * prepare content for email
	 * 
	 * @param queueStatusList
	 *            , This parameter used to store Omniture report status which
	 *            are in waiting or running state
	 * @param inProgressEmailContentList
	 *            , This parameter will be used to store Report which are in progress
	 *             from the Omniture queue, later this list will be used
	 *            to generate status email;
	 */
	private void inProgressEmailContent(List<OmnitureReportStatus> queueStatusList,List<OmnitureReportEmailContentVO> inProgressEmailContentList) throws BBBSystemException, BBBBusinessException, RepositoryException,IOException
	{
		BBBPerformanceMonitor.start(OmnitureReportAPITools.OMNITURE_PERFORMANCE + "inProgressEmailContent");
		if(queueStatusList != null && queueStatusList.size()>0){
			
	    	for(OmnitureReportStatus omnitureReportStatus :queueStatusList)
	    	{
				OmnitureReportEmailContentVO omnitureReportEmailContentVO = new OmnitureReportEmailContentVO();
				String reportId = omnitureReportStatus.getReportID();
				String userId = omnitureReportStatus.getUser();
				logDebug("OmnitureReportAPIManager inProgressEmailContent: Reports got from Omniture Queue with reportId:["+reportId+"],UserId:["+userId+"]");
				omnitureReportEmailContentVO.setReportId(reportId);
				omnitureReportEmailContentVO.setUserId(userId);
				omnitureReportEmailContentVO.setReportSuiteId(omnitureReportStatus.getReportSuiteID());
				inProgressEmailContentList.add(omnitureReportEmailContentVO);

			}
		}
		BBBPerformanceMonitor.end(OmnitureReportAPITools.OMNITURE_PERFORMANCE + "inProgressEmailContent"); 	
	}
	/**
	 * Used to invoke GET method on reportId which are ready for processing
	 * @param queueStatusList  
	 * @param emailContentListMap
	 * @throws Exception 
	 */
	
	public void consumeQueuedReport(List<OmnitureReportStatus> queueStatusList,Map<String,List<OmnitureReportEmailContentVO>> emailContentListMap) throws Exception{
		logDebug( CLS_NAME + " [ consumeQueuedReport ] method Start:: ");
		BBBPerformanceMonitor.start(OmnitureReportAPITools.OMNITURE_PERFORMANCE + "consumeQueuedReport");
		String reportId=null;
	try{
		
		//List to save every report's status for status email.
		List<OmnitureReportEmailContentVO> emailContentList= new ArrayList<OmnitureReportEmailContentVO>();
		emailContentListMap.put(BBBCoreConstants.OMNITURE_CONSUMED_QUEUE, emailContentList);
		
		//List to save failed archive detail for status email.
		List<OmnitureReportEmailContentVO> archiveFailedStatusEmailVOList = new ArrayList<OmnitureReportEmailContentVO>();
		emailContentListMap.put(BBBCoreConstants.OMNITURE_ARCHIVE_REPORT, archiveFailedStatusEmailVOList);
		
		RepositoryItem[] reportStatusItems=getDefaultTool().getReportStatusByStatus(BBBCoreConstants.OMNITURE_REPORT_QUEUED);
				if(reportStatusItems != null  && reportStatusItems.length>0)
				{
					Set<String> queuedRepId=getReportIdInStatusQueue(queueStatusList);
					
					for(RepositoryItem repositoryItem :reportStatusItems)
					{
						OmnitureReportEmailContentVO  emailContentVO=null;
						
						try{	
							 	
								emailContentVO= new OmnitureReportEmailContentVO();
								String repStatus = (String) repositoryItem.getPropertyValue(BBBCoreConstants.REPORT_OP_STATUS);
								reportId=(String) repositoryItem.getPropertyValue(BBBCoreConstants.REPORT_ID);
								if(! queuedRepId.contains(reportId) && repStatus.equalsIgnoreCase(BBBCoreConstants.OMNITURE_REPORT_QUEUED))
								{ 
									
									String concept=(String) repositoryItem.getPropertyValue(BBBCoreConstants.CONCEPT);
									Date queuedDate=(Date) repositoryItem.getPropertyValue(BBBCoreConstants.REPORT_QUEUED_TIME);
									String methodType = (String) repositoryItem.getPropertyValue(BBBCoreConstants.METHOD_TYPE);
									String reportType = (String) repositoryItem.getPropertyValue(BBBCoreConstants.REPORT_TYPE);
									 
									logDebug("OmnitureReportAPIManager : Consuming report with: reportId:["+reportId+"],concept:["+concept+"]");
									if(StringUtils.isNotBlank(methodType)){
										OmnitureReportAPITools omnitureReportAPITools= (OmnitureReportAPITools) getOmnitureReportToolsMap().get(methodType);
										
										omnitureReportAPITools.getReportDetailsForAlreadyQueued(concept, (MutableRepositoryItem) repositoryItem,emailContentVO,archiveFailedStatusEmailVOList);
										emailContentVO.setConcept(concept);
										emailContentVO.setQueuedDate(queuedDate);
										emailContentVO.setReportType(reportType);
										emailContentList.add(emailContentVO);
									}else{
										 logDebug("OmnitureReportAPIManager :methodType should not be null for reportId:["+reportId+"],concept:["+concept+"]");
										 continue;
									     }
																
								}else
									{
											
										logDebug("OmnitureReportAPIManager : report skipped as it is in progress/waiting state: reportId:"+reportId);	
									}
						    }catch(Exception exception){
						    	logError("Exception in OmnitureReportAPIManager consumeQueuedReport: ReportId:["+reportId+"],Exception message:["+exception.getMessage()+"],exception :"+exception);
						    	emailContentVO.setExceptionDetails(BBBUtility.getExceptionTrace(exception));
						    }
					}
				}
				else
				{
					logDebug("OmnitureReportAPIManager : No report available with status queued");	
				}
		}catch(Exception exception)
		{
			logError("Exception in OmnitureReportAPIManager consumeQueuedReport exception message:["+exception.getMessage()+"],Exception["+exception+"]");
			BBBPerformanceMonitor.end(OmnitureReportAPITools.OMNITURE_PERFORMANCE + "consumeQueuedReport");
			
		}
		BBBPerformanceMonitor.end(OmnitureReportAPITools.OMNITURE_PERFORMANCE + "consumeQueuedReport");
		logDebug( CLS_NAME + " [ consumeQueuedReport ] method End:: ");
	}
	
	/**
	 * method used to queue schedule report
	 * @param queueStatusList
	 * @param emailContentListMap
	 * @throws RepositoryException 
	 */
	public void queueScheduledReport(List<OmnitureReportStatus> queueStatusList,Map<String, List<OmnitureReportEmailContentVO>> emailContentListMap) throws RepositoryException
	{
		logDebug( CLS_NAME + " [ queueScheduledReport ] method Start:: ");
		BBBPerformanceMonitor.start(OmnitureReportAPITools.OMNITURE_PERFORMANCE + "queueScheduledReport");
	 try{
		 
		List<OmnitureReportEmailContentVO> emailContentList = new ArrayList<OmnitureReportEmailContentVO>();
			
		emailContentListMap.put(BBBCoreConstants.QUEUED_SCHEDULED_REPORT, emailContentList);
			
		List<OmnitureReportConfigVo> reportVoList	=getScheduleReportVoList( queueStatusList); 
		
		
		Integer maxNumOfAvailableThread=new Integer(getDefaultTool().getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.OMNITURE_API_NO_OF_THREAD, BBBCoreConstants.STRING_TEN));
		
		Integer usedThread=getLiveThreadCountInOmnitureQueue(queueStatusList);
		
		logDebug(CLS_NAME + ":queueScheduledReport ,Used thread in Omniture Queue:["+usedThread+"],Max thread defined in config Key:["+maxNumOfAvailableThread+"]");
		
		maxNumOfAvailableThread=maxNumOfAvailableThread-usedThread;
		
		if(reportVoList != null)
		{   
			// sort based on priority.
			Collections.sort(reportVoList);
			
			for(OmnitureReportConfigVo omnitureReportConfigVo:reportVoList){
				try {	
					    Integer numberOfRequiredThread= omnitureReportConfigVo.getNumOfThread();
					    String concept=omnitureReportConfigVo.getConcept();
					    String reportType=omnitureReportConfigVo.getReportType();
					    Integer priorty=omnitureReportConfigVo.getPriority();
					    
					    Integer count=omnitureReportConfigVo.getCount();
						Integer batchCount=omnitureReportConfigVo.getBatchCount();
						
						logDebug("before performQueue:numOfAvailableThread["+maxNumOfAvailableThread+"],numOfRequired Thread:["+numberOfRequiredThread+"],concept:["+concept+"],reportType:["+reportType+"],priorty["+priorty+"]");
					 	
						if(maxNumOfAvailableThread >= numberOfRequiredThread)
					 		{
								int numberOfQueuedReport=performQueue(omnitureReportConfigVo,emailContentList);
								
								if(batchCount != null && batchCount>0 && batchCount<count)
								{
									maxNumOfAvailableThread=maxNumOfAvailableThread-numberOfQueuedReport; 
								}
								else if(numberOfQueuedReport > 0)
								{
									maxNumOfAvailableThread=maxNumOfAvailableThread-numberOfRequiredThread;
								}
								logDebug("after performQueue:numOfAvailableThread["+maxNumOfAvailableThread+"],numberOfQueuedReport:["+numberOfQueuedReport+"],reportType:["+reportType+"],concept:["+concept+"]");
							}else if(maxNumOfAvailableThread <= 0){
										logDebug("Exit performQueue: As there is no Thread available to Queue the Report");
										break;
									}
					}
					catch (Exception exception) 
				     {
				    	 logError("Exception in queuing the request:"+exception);
				    	 OmnitureReportEmailContentVO omnitureReportEmailContentVO= new OmnitureReportEmailContentVO();
						 omnitureReportEmailContentVO.setReportType(omnitureReportConfigVo.getReportType());
						 omnitureReportEmailContentVO.setExceptionDetails(BBBUtility.getExceptionTrace(exception));
						 emailContentList.add(omnitureReportEmailContentVO);
					 }
				
			}
		}
	 }catch (RepositoryException exception){
		 logError("RepositoryException in queueScheduledReport:"+exception);
		 BBBPerformanceMonitor.end(OmnitureReportAPITools.OMNITURE_PERFORMANCE + "queueScheduledReport");
		 throw exception;
	 }
	
	 BBBPerformanceMonitor.end(OmnitureReportAPITools.OMNITURE_PERFORMANCE + "queueScheduledReport");
	 
	 logDebug( CLS_NAME + " [ queueScheduledReport ] method End:: ");
}
	 
	
	private int performQueue(OmnitureReportConfigVo omnitureReportConfigVo,List<OmnitureReportEmailContentVO> emailContentList) throws IdGeneratorException, SiteContextException, SystemException  
	{
		logDebug( CLS_NAME + " [ performQueue ] method start:: ");
		
		BBBPerformanceMonitor.start(OmnitureReportAPITools.OMNITURE_PERFORMANCE + "performQueue");
		
		Integer count=omnitureReportConfigVo.getCount();
		Integer batchCount=omnitureReportConfigVo.getBatchCount();
		String concept=omnitureReportConfigVo.getConcept();
		String reportType=omnitureReportConfigVo.getReportType();
		String methodType=omnitureReportConfigVo.getMethodType();
		Integer startingWith=omnitureReportConfigVo.getStartWith();
		int numberOfQueuedReport=0;
		boolean isReportQueued=false;
		int numOfReports=0;
		if (startingWith == null){
			startingWith=1;
	     }
	
		OmnitureReportEmailContentVO omnitureReportEmailContentVO=null;
		OmnitureReportAPITools omnitureReportAPITools= (OmnitureReportAPITools) getOmnitureReportToolsMap().get(methodType);
		
		if(omnitureReportAPITools == null)
		{
			logError(CLS_NAME +"Tool Not found in service map for methodType:"+methodType);
			return 0;
		}
	    
		
				  
				 
		int noOfPastDays=0;
		int noOfDays=0;
		
		if(omnitureReportConfigVo.getReportTimeRange() !=null)
		{
			noOfPastDays =omnitureReportConfigVo.getReportTimeRange();
		}
		if(omnitureReportConfigVo.getReportTimeRangeAdjustment() !=null && omnitureReportConfigVo.getReportTimeRangeAdjustment()> 0)
		{
			noOfPastDays=noOfPastDays+omnitureReportConfigVo.getReportTimeRangeAdjustment();
			noOfDays=omnitureReportConfigVo.getReportTimeRangeAdjustment();
		}
		
		
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(BBBCoreConstants.OMNITURE_DATE_FORMAT);
	    cal.setTime(new Date());
	    cal.add(Calendar.DATE, - noOfPastDays);
	    String dateFrom = sdf.format(cal.getTime());
		
		cal = Calendar.getInstance();		
		cal.add(Calendar.DATE, - noOfDays);
	    String dateTo = sdf.format(cal.getTime());
				
		
		
		String siteId=getConceptSiteMap().get(concept);
		
		
		Site  site = this.getSiteContextManager().getSite(siteId);
		final SiteContextImpl context = new SiteContextImpl(this.getSiteContextManager(), site);
		this.getSiteContextManager().pushSiteContext(context);
		
		QueueRequestVO requestVO = new QueueRequestVO();
		requestVO.setConcept(concept);
		requestVO.setReportType(reportType);
		requestVO.setMethodType(methodType);
		requestVO.setDateFrom(dateFrom);
		requestVO.setDateTo(dateTo);
		
				if(! (batchCount != null && batchCount>0 && batchCount<count))
					{
						//batching not required.
						requestVO.setBatchRequest(false);
						requestVO.setCount(count);
						requestVO.setStartingWith(startingWith);
						requestVO.setTop(count-startingWith+1);
						requestVO.setOmnitureReportConfigVo(omnitureReportConfigVo);
						omnitureReportEmailContentVO= new OmnitureReportEmailContentVO();
						emailContentList.add(omnitureReportEmailContentVO);
						logDebug("OmnitureReportAPIManager: performQueue:"+requestVO.toString());
						isReportQueued=	omnitureReportAPITools.doQueueReport(requestVO ,omnitureReportEmailContentVO); 
						if(isReportQueued){
							numberOfQueuedReport=numberOfQueuedReport +1;
						}
					}
				else
					{	
					    int recordsToFetch = (count-startingWith);
				        if(recordsToFetch%batchCount == 0) 
				        	{
				        		numOfReports =   (recordsToFetch/batchCount);
				        	}
						 else
						 	{
							 	numOfReports =   (recordsToFetch/batchCount + 1);
						 	}
						  
				         String batchId = getIdGenerator().generateStringId(getOmnitureBatchReportSeedName());
				         boolean isAnyReportQueuedInBatch=false;
			        
			         
						for(int reportSeq=0;reportSeq<numOfReports;reportSeq++)
						{
							//calculate number of records to request in current report
							int remainingRecordsToRequest = recordsToFetch - (reportSeq*batchCount);
							int top=0;
							// In the more records to fetch then set top to batch size otherwise remaining record
							if(remainingRecordsToRequest > batchCount)
							{
								top=batchCount;
							} 
							else
							{
								top=remainingRecordsToRequest + 1;
							}
							//Changes done as startWith will not only be initialized with 1 but with some randome values also defined in config table
							if (reportSeq>0){
								startingWith = startingWith + batchCount;
								}
							
							
							requestVO.setBatchRequest(true);
							requestVO.setCount(count);
							requestVO.setStartingWith(startingWith);
							requestVO.setTop(top);
							requestVO.setReportIndex(reportSeq);
							requestVO.setBatchId(batchId);
							requestVO.setBatchSize(batchCount);
							requestVO.setOmnitureReportConfigVo(omnitureReportConfigVo);
							logDebug("OmnitureReportAPIManager: performQueue:requestVO"+requestVO.toString());
							omnitureReportEmailContentVO= new OmnitureReportEmailContentVO();
							emailContentList.add(omnitureReportEmailContentVO);
							isAnyReportQueuedInBatch=omnitureReportAPITools.doQueueReport(requestVO,omnitureReportEmailContentVO);
							
							if(isAnyReportQueuedInBatch)
							{
								isReportQueued= true;
								logDebug("OmnitureReportAPIManager: performQueue:Report Queued for reportIndex["+reportSeq+"],batchId["+batchId+"]");
								numberOfQueuedReport=numberOfQueuedReport +1;
							} else if(reportSeq==0 && BBBCoreConstants.POPULAR_SEARCHES.equalsIgnoreCase(methodType)){
								break;
							}
							
						}
					}
				
		getSiteContextManager().popSiteContext(context);
		
		BBBPerformanceMonitor.end(OmnitureReportAPITools.OMNITURE_PERFORMANCE + "performQueue");
		
		logDebug( CLS_NAME + " [ performQueue ] method End:: numberOfQueuedReport:["+numberOfQueuedReport+"]");
		
	return numberOfQueuedReport;
	}
	

	/**
	 * Used to get Report which to be queued today
	 * @param queueStatusList (response of getQueueStatus API)
	 * @throws RepositoryException 
	 */
	
   private List<OmnitureReportConfigVo> getScheduleReportVoList(List<OmnitureReportStatus> queueStatusList) throws RepositoryException
   {
	logDebug( CLS_NAME + " [ getScheduleReportVoList ] method start:: ");
	
	BBBPerformanceMonitor.start(OmnitureReportAPITools.OMNITURE_PERFORMANCE + "getScheduleReportVoList");
	
	List<OmnitureReportConfigVo> reportConfigVoList =getDefaultTool().getOmnitureReportConfig();
	
		 	 
	// Set of String (concept+reportType) to avoid queuing of  reportType for a concept as these reportType already in Omniture Queue. 
	Set<String> inProgressReportType=findInProgressReportType(queueStatusList);
	
	List<OmnitureReportConfigVo> qualifiedReportListToQ=null;
	
		if(reportConfigVoList != null && reportConfigVoList.size()>0)
		{
			 qualifiedReportListToQ=getQualifiedVO(reportConfigVoList,inProgressReportType);
			 logDebug("OmnitureReportAPIManager getScheduleReportVoList Ending" );
			 return qualifiedReportListToQ;	 
		}
		else
		{
			logDebug("OmnitureReportAPIManager Empty reportConfigVoList" );	
		}
		
	BBBPerformanceMonitor.end(OmnitureReportAPITools.OMNITURE_PERFORMANCE + "getScheduleReportVoList");	
	
	logDebug( CLS_NAME + " [ getScheduleReportVoList ] method end:: ");
	return qualifiedReportListToQ;	
	}
   
   
   /**
    * Used to filter reportConfigVoList with inProgressReportType(in the response of getQueueStatus) , reportType which already Queued with currentDate and match freq
    *  of OmnitureReportConfigVo with current date
    * @param reportConfigVoList, Listof Omniture report configuration in the repository
    * @param inProgressReportType, In progress reportType in Omniture Queue.
    * @return qualifiedVOList ,return reports which need to be queued today  
    * @throws RepositoryException 
    */

	private List<OmnitureReportConfigVo> getQualifiedVO(List<OmnitureReportConfigVo> reportConfigVoList,Set<String> inProgressReportType) throws RepositoryException 
	{	
		logDebug( CLS_NAME + " [ getQualifiedVO ] (Pull those report which need to be queued) method start:: ");
		
		BBBPerformanceMonitor.start(OmnitureReportAPITools.OMNITURE_PERFORMANCE + "getQualifiedVO");
		
		List<OmnitureReportConfigVo> qualifiedVOList= new ArrayList<OmnitureReportConfigVo>();
		final Date currentDate = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DAY_OF_MONTH);
		
		/*
		 *   do not queue a report if a report type is already have entry in DB with currentDate(except threshold exceeded report with status canceled)
		 *   reportTypeAlreadyQueuedAndConsumed contain String of concept+reportType to filter reportConfigVoList
		 */
		Set<String> reportTypeAlreadyQueuedAndConsumed=getAlreadyScheduledReportType(currentDate);
		
		logDebug("inProgressReportType in QueueStatus Call:["+inProgressReportType+"]");
		logDebug("AlreadyScheduledReportType in reportStatus Table  :["+reportTypeAlreadyQueuedAndConsumed+"] , with date:"+currentDate);
		
		for(OmnitureReportConfigVo omnitureReportConfigVo : reportConfigVoList)
		{
			String scheduleString=omnitureReportConfigVo.getFreq();
			String reportType=omnitureReportConfigVo.getReportType();
			String concept=omnitureReportConfigVo.getConcept();
			Date schDate=getScheduleDate(scheduleString);
			
			String reportSuits=concept+reportType;
			
			// exclude report which are available in reportStatus with current date(reportTypeAlreadyQueuedAndConsumed)
			// exclude report Type which are in the response of getQueueStatus (inProgressReportType)
			// Queue only those report which schedule  today current date.
			
				if( ! reportTypeAlreadyQueuedAndConsumed.contains(reportSuits)  && ! inProgressReportType.contains(reportSuits) && DateUtils.isSameDay(schDate, currentDate))
				{
					qualifiedVOList.add(omnitureReportConfigVo);
					logDebug("getQualifiedVO :QualifiedVO:ReportType:["+omnitureReportConfigVo.getReportType()+"],reportMethod:["+omnitureReportConfigVo.getMethodType()+"],concept:["+concept+"]");
				}
				else
				{
					logDebug("getQualifiedVO: Skip: ReportType:["+omnitureReportConfigVo.getReportType()+"],reportMethod:["+omnitureReportConfigVo.getMethodType()+"],schDate:["+schDate+"],currentDate["+currentDate+"], concept:["+concept+"], Report  ["+concept+omnitureReportConfigVo.getReportType()+"] skipped due to Schedule Date or it is in the [inProgressReportType] or [AlreadyScheduledReportType] list");
				}
		}
		
		BBBPerformanceMonitor.end(OmnitureReportAPITools.OMNITURE_PERFORMANCE + "getQualifiedVO");
		
		logDebug( CLS_NAME + " [ getQualifiedVO ] method end:: ");
		return qualifiedVOList;
	}

	/**
	 * Used to find reportType which are in progress in Omniture queue to avoid
	 * duplicate queuing of same type of report.(if there is any report which
	 * cancelled,will not be consider as in progress report)
	 * 
	 * @param queueStatusList
	 *            , List of OmnitureReportStatus which are in the response of
	 *            getQueue method(QueueStatus API)
	 *            
	 * @return Set of (concept+reportType) of  report which are in waiting or running state in
	 *         Omniture queue(specific to concept) .
	 *         
	 * @throws RepositoryException
	 */

	private Set<String> findInProgressReportType(List<OmnitureReportStatus> queueStatusList) throws RepositoryException
	{   
		logDebug( CLS_NAME + " [ findInProgressReportType ] method start:: ");
		
		BBBPerformanceMonitor.start(OmnitureReportAPITools.OMNITURE_PERFORMANCE + "findInProgressReportType");
		
		Set<String> inProgressReportType= new HashSet<String>();
	try {  
			if(queueStatusList != null && queueStatusList.size()>0)
			{    
				String configuredUserId= getDefaultTool().getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.OMNITURE_API_USER_NAME_IN_RESPONSE, BBBCoreConstants.OMNITURE_API_DEFAULT_USER_NAME_IN_RESPONSE);
			    
				for(OmnitureReportStatus omnitureReportStatus:queueStatusList){
					String reportId=omnitureReportStatus.getReportID();
					String userId=omnitureReportStatus.getUser();
					
					if( ! omnitureReportStatus.isCancel() && configuredUserId.equalsIgnoreCase(userId) )
					{
					     RepositoryItem[] repositoryItem=getDefaultTool().getReportStatusByReportIdOrBatchId(reportId,null);
						if(repositoryItem != null && repositoryItem.length>0)
						{
							String reportType=(String) repositoryItem[0].getPropertyValue(BBBCoreConstants.REPORT_TYPE);
							String concept=(String) repositoryItem[0].getPropertyValue(BBBCoreConstants.CONCEPT);
							inProgressReportType.add(concept+reportType);
							logDebug("OmnitureReportAPIManager:findInProgressReportType ::reportId found in Respository With reportId:["+reportId+"],reportType:["+reportType+"]concept:["+concept+"]");
						}
					}
					else
					{
						logDebug("OmnitureReportAPIManager:findInProgressReportType ::,reportId:["+reportId+"],configuredUserId:["+configuredUserId+"],isCancel:["+omnitureReportStatus.isCancel()+"],userId:["+userId+"]");
					}
				}
			}
			else
			{
				logDebug("OmnitureReportAPIManager : empty queueStatusList");
			}
		} catch (RepositoryException ex) {
			
		logError("Error in OmnitureReportAPIManager findInProgressReportType"+ex);
		
		BBBPerformanceMonitor.end(OmnitureReportAPITools.OMNITURE_PERFORMANCE + "findInProgressReportType");
		
		throw ex;
		}
	
		BBBPerformanceMonitor.end(OmnitureReportAPITools.OMNITURE_PERFORMANCE + "findInProgressReportType");
	
		logDebug( CLS_NAME + " [ findInProgressReportType ] method end:: inProgressReportType in Omniture Queue:["+inProgressReportType+"]");
		
	return inProgressReportType;
	}
	/**
	 * Used to get the report type which already queued for current date for a concept
	 * @param currentDate
	 * @return set of concept+reportId
	 * @throws RepositoryException
	 */
	private Set<String> getAlreadyScheduledReportType(final Date currentDate) throws RepositoryException
	{	
		logDebug( CLS_NAME + " [ getAlreadyScheduledReportType ] method start:: ");
		
		BBBPerformanceMonitor.start(OmnitureReportAPITools.OMNITURE_PERFORMANCE + "getAlreadyScheduledReportType");
		
		Set<String> scheduledReportTypeSet= new HashSet<>(); 
		RepositoryItem[] resportStatusItems=getDefaultTool().getReportStatusByDate(currentDate);
				if(resportStatusItems != null  && resportStatusItems.length>0)
				{
					for(RepositoryItem  repositoryItem :resportStatusItems)
					{ 
						String reportType=(String) repositoryItem.getPropertyValue(BBBCoreConstants.REPORT_TYPE);
						String reportId=(String) repositoryItem.getPropertyValue(BBBCoreConstants.REPORT_ID);
						String concept=(String) repositoryItem.getPropertyValue(BBBCoreConstants.CONCEPT);
						
						String status=(String) repositoryItem.getPropertyValue(BBBCoreConstants.REPORT_OP_STATUS);
						/*String errorCode=(String) repositoryItem.getPropertyValue(BBBCoreConstants.REPORT_ERROR_CODE);
						String errorDesc=(String) repositoryItem.getPropertyValue(BBBCoreConstants.REPORT_ERROR_DESC);
						
						if(BBBCoreConstants.OMNITURE_REPORT_STATUS_CANCEL.equalsIgnoreCase(status) && 
						   BBBCoreConstants.OMNITURE_REPORT_STATUS_CANCEL.equalsIgnoreCase(errorCode) &&
						   BBBCoreConstants.OMNITURE_THRESHOLD_EXCEEDED.equalsIgnoreCase(errorDesc))
						{	
							
							//If a  reportType Canceled during CheckQueue and this reportType planned for today run,then this reportType should be Queued Again.
							logDebug("OmnitureReportAPIManager :getAlreadyScheduledReportType :Skip reportType:["+reportType+"] ,reportId:["+reportId+"], status:["+status+"]");
							continue;
						}*/
						if(reportType != null)
						{
						scheduledReportTypeSet.add(concept+reportType);
						logDebug("OmnitureReportAPIManager :getAlreadyScheduledReportType : reportType:["+reportType+"] ,reportId:["+reportId+"], status:["+status+"],concept+["+concept+"]");
						}
					}
				}
				
		BBBPerformanceMonitor.end(OmnitureReportAPITools.OMNITURE_PERFORMANCE + "getAlreadyScheduledReportType");		
		logDebug( CLS_NAME + " [ getAlreadyScheduledReportType ] method end:: Report Which Already Queued with Current Date:["+scheduledReportTypeSet+"]");
		
	return scheduledReportTypeSet;
	}
	

	
	/**
	 * Used to  convert user defined string to Date object 
	 * @param scheduleString .eg:-CalendarSchedule setting (calendar * . 1 1,last 14 5) which is for CalendarSchedule setting(1st and last Sunday of every month, 2:05pm)
	 * @return java.util.Date
	 */
	
	private Date getScheduleDate(String scheduleString){
        CalendarSchedule calsch = new CalendarSchedule(scheduleString);
        Date schDate= calsch.getNextJobTimeAsDate();
        logDebug( CLS_NAME + " [ getScheduleDate ] method end:: scheduleString:["+scheduleString+"],schDate:["+schDate+"]");
        return schDate;

	}
    /**
     * Utility method to get set of reportId in the queueStatusList;
     * @param queueStatusList
     * @return inProgressReportId, set 
     */

	private Set<String> getReportIdInStatusQueue(List<OmnitureReportStatus> queueStatusList){
		logDebug( CLS_NAME + " [ getReportIdInStatusQueue ] method Start");
		Set<String> inProgressReportId= new HashSet<String>();
		
		if(queueStatusList != null && queueStatusList .size()>0)
		{
			for(OmnitureReportStatus omnitureReportStatus: queueStatusList){
				inProgressReportId.add(omnitureReportStatus.getReportID());
			}
		}
		logDebug( CLS_NAME + " [ getReportIdInStatusQueue ] method end:: inProgressReportId:["+inProgressReportId+"]");
		return inProgressReportId;
	}
	
	/**
	 * Util function to update status of report as cancel in queueStatusList if report successfully canceled from Omniture queue and updated as cancel in the repository;
	 * @param queueStatusList
	 * @param cancelledReportIdSet
	 */
	private void updateCancelFlagInQueueStatusList(	List<OmnitureReportStatus> queueStatusList,	Set<String> cancelledReportIdSet) {
		logDebug( CLS_NAME + " [ updateCancelFlagInQueueStatusList ] method Start");
		for(OmnitureReportStatus omnitureReportStatus:queueStatusList)
		{
			String reportId=omnitureReportStatus.getReportID();
			if(cancelledReportIdSet.contains(reportId))
			{
				omnitureReportStatus.setCancel(true);
			}
		}
		logDebug( CLS_NAME + " [ updateCancelFlagInQueueStatusList ] method end");
	}

	
   /**
    * @return omnitureReportRepository
    */
	public Repository getOmnitureReportRepository() {
		return omnitureReportRepository;
	}
	/**
	 * @param omnitureReportRepository
	 */
	public void setOmnitureReportRepository(Repository omnitureReportRepository) {
		this.omnitureReportRepository = omnitureReportRepository;
	}

	/**
	 * @return omnitureReportToolsMap
	 */
	public ServiceMap getOmnitureReportToolsMap() {
		return omnitureReportToolsMap;
	}

	/**
	 * @param omnitureReportToolsMap
	 */
	public void setOmnitureReportToolsMap(ServiceMap omnitureReportToolsMap) {
		this.omnitureReportToolsMap = omnitureReportToolsMap;
	}


	/**
	 * @return defaultTool
	 */
	public OmnitureBoostedProductTools getDefaultTool() {
		return defaultTool;
	}


	/**
	 * @param defaultTool
	 */
	public void setDefaultTool(OmnitureBoostedProductTools defaultTool) {
		this.defaultTool = defaultTool;
	}


	/**
	 * @return idGenerator
	 */
	public IdGenerator getIdGenerator() {
		return idGenerator;
	}


	/**
	 * @param idGenerator
	 */
	public void setIdGenerator(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}


	/**
	 * @return omnitureBatchReportSeedName
	 */
	public String getOmnitureBatchReportSeedName() {
		return omnitureBatchReportSeedName;
	}


	/**
	 * @param omnitureBatchReportSeedName
	 */
	public void setOmnitureBatchReportSeedName(String omnitureBatchReportSeedName) {
		this.omnitureBatchReportSeedName = omnitureBatchReportSeedName;
	}


	/**
	 * @return conceptSiteMap
	 */
	public Map<String, String> getConceptSiteMap() {
		return conceptSiteMap;
	}


	/**
	 * @param conceptSiteMap
	 */
	public void setConceptSiteMap(Map<String, String> conceptSiteMap) {
		this.conceptSiteMap = conceptSiteMap;
	}


	/**
	 * @return	siteContextManager
	 */
	public SiteContextManager getSiteContextManager() {
		return siteContextManager;
	}


	/**
	 * @param siteContextManager
	 */
	public void setSiteContextManager(SiteContextManager siteContextManager) {
		this.siteContextManager = siteContextManager;
	}

	/**
	 * This method is used to Send Email on Success or Failure of Report Processing
	 * @param emailContentMap
	 */
    public void sendEmail(Map<String, List<OmnitureReportEmailContentVO>> emailContentMap) {
    	long startTime = System.currentTimeMillis();
		logInfo("Start Time to send email startTime::" + (startTime) + " milliseconds");
		 
    	String emailContent = BBBCoreConstants.BLANK;
    	List<String> emailEventContent= getPriorityEmailEventList();
    	boolean isEmailRequire=false;
    	
		String emailSubject = getDefaultTool().getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.OMNITURE_EMAIL_SUBJECT, BBBCoreConstants.BLANK);
		
		if(BBBUtility.isNotBlank(emailSubject) && emailSubject.contains("envTypeName")){
			String envTypeName=BBBUtility.getEnvTypeName();
			logDebug(CLS_NAME + " - envTypeName Is: " + envTypeName);
			if(BBBUtility.isNotBlank(envTypeName)){
				emailSubject=emailSubject.replace("envTypeName", envTypeName);
			}
			
		}
		if(emailContentMap != null && !emailContentMap.isEmpty()) {
			// Iterating over prioritized list of events  
				for(String emailContentKey : emailEventContent){
				String emailEvent=emailContentKey;
				if( (BBBCoreConstants.OMNITURE_PROCESSING_EXCEPTION.equalsIgnoreCase(emailEvent) || BBBCoreConstants.OMNITURE_ARCHIVE_REPORT.equalsIgnoreCase(emailEvent)) 
					&& BBBUtility.isListEmpty(emailContentMap.get(emailEvent)))
				{
					continue;
				}
				
				if(! isEmailRequire && !BBBUtility.isListEmpty(emailContentMap.get(emailEvent)))
				{
					isEmailRequire =true;
					logDebug(CLS_NAME + " For event[ " + emailEvent+"] :Email Content Available");
				}
				
				logDebug(CLS_NAME + " - Calling [createReportEmailContent] for the report category : " + emailEvent);
				
				String emailEventHeader = getLabelManager().getPageLabel(BBBCoreConstants.LBL+ emailEvent, BBBCoreConstants.DEFAULT_LOCALE, null, BBBCoreConstants.SITE_BAB_US);
				
				logDebug(CLS_NAME + " - Calling [createReportEmailContent] for the report: emailEventHeader : " + emailEventHeader);
				
				if(StringUtils.isBlank(emailEventHeader))
				{
					emailEventHeader= emailEvent;
				}
				emailContent = emailContent +  BBBCoreConstants.OMNITURE_REPORT_DETAILS_PARA+ emailEventHeader + BBBCoreConstants.OMNITURE_REPORT_DETAILS_PARA_END;
				
				if( BBBCoreConstants.OMNITURE_PROCESSING_EXCEPTION.equals(emailEvent))
				{
					
					emailContent = createReportExceptionEmailContent(emailContent, emailContentMap.get(emailEvent));
				}
				else
				{
					emailContent = createReportEmailContent(emailContent, emailContentMap.get(emailEvent),emailEvent);
				}}
		
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		String recipientFrom = getDefaultTool().getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.RECIPIENT_FROM, BBBCoreConstants.BLANK);
		String recipientTo = getDefaultTool().getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.RECIPIENT_TO, BBBCoreConstants.BLANK);
		String host = getDefaultTool().getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.SMTP_CONFIG, BBBCoreConstants.SMTP_HOST_NAME, BBBCoreConstants.BLANK);

		paramMap.put(BBBCoreConstants.RECIPIENT_FROM, recipientFrom);
		paramMap.put(BBBCoreConstants.RECIPIENT_TO, recipientTo);
		paramMap.put(BBBCoreConstants.SUBJECT, emailSubject);
		paramMap.put(BBBCoreConstants.EMAIL_CONTENT, emailContent);
		paramMap.put(BBBCoreConstants.MAP_SMTP_HOST, host);
		
		if(isLoggingDebug()){
		logDebug("Email recipientFrom :: " + recipientFrom);
		logDebug("Email recipientTo :: " + recipientTo);
		logDebug("Email host :: " + host);
		}
		logInfo("Email Subject :: " + emailSubject);
		logInfo("Email Content :: " + emailContent);

		if (!BBBUtility.isEmpty(recipientFrom) && !BBBUtility.isEmpty(recipientTo) && isEmailRequire) {
			logDebug("Sending Email...");
			BBBUtility.sendEmail(paramMap);
		}else{
			logDebug("Not sending email as there is nothing to report. Email required flag:"+isEmailRequire);
		}

		long endTime = System.currentTimeMillis();
		logInfo("Total time took to send email endTime::" + (endTime - startTime) + " milliseconds");
		logDebug( CLS_NAME + " [ sendEmail ] method ends. ");		
	}
    
    /**
     * Method to create Email content in html table for multiple reports
     * @param emailContent
     * @param reportEmailContentList
     * @return
     */
    public String createReportEmailContent(String emailContent, List<OmnitureReportEmailContentVO> reportEmailContentList, String emailEvent) {
    	logDebug( CLS_NAME + " [ createReportEmailContent ] method start with email content :: " + reportEmailContentList);
    	String updatedEmailContent = BBBCoreConstants.BLANK;

    	if(reportEmailContentList == null ) {
    		reportEmailContentList= new ArrayList<OmnitureReportEmailContentVO>();
    	}else if(emailContent == null ) {
    		return updatedEmailContent;
    	}

    	StringBuilder tableContent = new StringBuilder();

    	tableContent.append(BBBCoreConstants.HTML_TABLE_START);
    	
	    // Html table header row created depending upon event fired 
	    if (emailEvent.equalsIgnoreCase(BBBCoreConstants.QUEUED_EMAIL_EVENT))
	    		{
	    tableContent.append(BBBCoreConstants.QUEUED_REPORT_TABLE_HEADER);
	    }
	    else if(emailEvent.equalsIgnoreCase(BBBCoreConstants.INPROGRESS_EMAIL_EVENT))
	    		{
	    tableContent.append(BBBCoreConstants.INPROGRESS_REPORT_TABLE_HEADER);
	    }
	    else if(emailEvent.equalsIgnoreCase(BBBCoreConstants.OMNITURE_CONSUMED_QUEUE))
	    {
	    	tableContent.append(BBBCoreConstants.CONSUMED_REPORT_TABLE_HEADER);
	    }
	  
	    else
	    {
	    	tableContent.append(BBBCoreConstants.REPORT_TABLE_HEADER);
	    }

	    //Populate data in report detail table	
    	for (OmnitureReportEmailContentVO omnitureReportEmailContentVO : reportEmailContentList) {
    		tableContent.append(BBBCoreConstants.HTML_TABLE_ROW_START);
    		tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_START);
		    tableContent.append(!BBBUtility.isEmpty(omnitureReportEmailContentVO.getReportId()) ? omnitureReportEmailContentVO.getReportId() : BBBCoreConstants.BLANK);
		    tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_END);
    		if(emailEvent.equalsIgnoreCase(BBBCoreConstants.INPROGRESS_EMAIL_EVENT)){
            	tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_START);
                tableContent.append(omnitureReportEmailContentVO.getUserId());
                tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_END);
                tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_START);
                tableContent.append(omnitureReportEmailContentVO.getReportSuiteId());
                tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_END);
            }
    		else{
		    tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_START);
		    tableContent.append(!BBBUtility.isEmpty(omnitureReportEmailContentVO.getConcept()) ? omnitureReportEmailContentVO.getConcept() : BBBCoreConstants.BLANK);
		    tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_END);
		    
		    tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_START);
		    tableContent.append(omnitureReportEmailContentVO.getQueuedDate() != null ? String.valueOf(omnitureReportEmailContentVO.getQueuedDate()) : BBBCoreConstants.BLANK);
		    tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_END);
		    
		    tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_START);
		    tableContent.append(!BBBUtility.isEmpty(omnitureReportEmailContentVO.getReportType()) ? omnitureReportEmailContentVO.getReportType() : BBBCoreConstants.BLANK);
		    tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_END);
		 
			tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_START);
		    tableContent.append(!BBBUtility.isEmpty(omnitureReportEmailContentVO.getOpStatus()) ? omnitureReportEmailContentVO.getOpStatus() : BBBCoreConstants.BLANK);
		    tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_END);
		    
		    if(!(emailEvent.equalsIgnoreCase(BBBCoreConstants.QUEUED_EMAIL_EVENT))){
		    tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_START);
		    tableContent.append(omnitureReportEmailContentVO.getRecordsUpdated());
		    tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_END);
             }
		    if(emailEvent.equalsIgnoreCase(BBBCoreConstants.OMNITURE_CONSUMED_QUEUE)){
		    	tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_START);
		    	tableContent.append(omnitureReportEmailContentVO.getNoOfSearchTerms());
		    	tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_END);
		     	tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_START);
		    	tableContent.append(omnitureReportEmailContentVO.getRecordsMoved());
		    	tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_END);
		    }
		   
		    tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_START);
		    tableContent.append(!BBBUtility.isEmpty(omnitureReportEmailContentVO.getExceptionDetails()) ? omnitureReportEmailContentVO.getExceptionDetails() : BBBCoreConstants.BLANK);
		    tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_END);
		    
    		}
    		
        	tableContent.append(BBBCoreConstants.HTML_TABLE_ROW_END);
		}
    	tableContent.append(BBBCoreConstants.HTML_TABLE_END);

    	updatedEmailContent = emailContent + tableContent;
    	logDebug( CLS_NAME + " [ createReportEmailContent ] method ends updated email content :: " +updatedEmailContent);
    	return updatedEmailContent;    	
    }
    
    
    /**
     * Method to create Email content in html table for Exceptin detail
     * @param emailContent
     * @param reportEmailContentList
     * @return
     */
    public String createReportExceptionEmailContent(String emailContent, List<OmnitureReportEmailContentVO> reportEmailContentList) {
    	logDebug( CLS_NAME + " [ createReportExceptionEmailContent ] method start with email content :: " + reportEmailContentList);
    	String updatedEmailContent = BBBCoreConstants.BLANK;

    	if(emailContent == null || reportEmailContentList == null ) {
    		return updatedEmailContent;
    	}

    	StringBuilder tableContent = new StringBuilder();

    	tableContent.append(BBBCoreConstants.HTML_TABLE_START);
    	// Html table header row
	    tableContent.append(BBBCoreConstants.REPORT_TABLE_EXCEPTION_HEADER);

	    //Populate data in report detail table	
    	for (OmnitureReportEmailContentVO omnitureReportEmailContentVO : reportEmailContentList) {
    		tableContent.append(BBBCoreConstants.HTML_TABLE_ROW_START);

	    		tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_START);
	    			tableContent.append(!BBBUtility.isEmpty(omnitureReportEmailContentVO.getExceptionDetails()) ? omnitureReportEmailContentVO.getExceptionDetails() : BBBCoreConstants.BLANK);
	            tableContent.append(BBBCoreConstants.HTML_TABLE_DATA_END);
	            
            tableContent.append(BBBCoreConstants.HTML_TABLE_ROW_END);
		}
    	tableContent.append(BBBCoreConstants.HTML_TABLE_END);

    	updatedEmailContent = emailContent + tableContent;
    	logDebug( CLS_NAME + " [ createReportExceptionEmailContent ] method ends updated email content :: " +updatedEmailContent);
    	return updatedEmailContent;    	
    }
    /**
     * Used to find used thread in Omniture Queue(Reports count in running/waiting state.)
     * @param queueStatusList
     * @return 
     */
	private Integer getLiveThreadCountInOmnitureQueue(	List<OmnitureReportStatus> queueStatusList) 
	{
		logDebug( CLS_NAME + " [ getLiveThreadCountInOmnitureQueue ] method start:: ");
		
		BBBPerformanceMonitor.start(OmnitureReportAPITools.OMNITURE_PERFORMANCE + "getLiveThreadCountInOmnitureQueue");
		Integer usedThread=0;
		if(queueStatusList != null)
		{
			for(OmnitureReportStatus omnitureReportStatus :queueStatusList)
			{
				if(! omnitureReportStatus.isCancel())
				{
					usedThread=usedThread+1;
				}
			}
		}
		
		BBBPerformanceMonitor.end(OmnitureReportAPITools.OMNITURE_PERFORMANCE + "getLiveThreadCountInOmnitureQueue");
		
		logDebug( CLS_NAME + " [ getLiveThreadCountInOmnitureQueue ] method end:: ");
		
	return usedThread;	
	}

	

	public List<String> getPriorityEmailEventList() {
		return priorityEmailEventList;
	}

	public void setPriorityEmailEventList(List<String> priorityEmailEventList) {
		this.priorityEmailEventList = priorityEmailEventList;
	}
	
}
