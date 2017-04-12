/**
 * 
 */
package com.bbb.thirdparty.omniture.formhandler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;

import atg.droplet.DropletException;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericFormHandler;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.thirdparty.omniture.tools.OmnitureBoostedProductTools;
import com.bbb.thirdparty.omniture.vo.OmnitureReportConfigVo;
import com.bbb.thirdparty.omniture.vo.ReportStatusVO;
import com.bbb.utils.BBBUtility;
 
 

/**
 * @author Sapient
 *
 */


public class OmnitureDashboardFormhandler extends BBBGenericFormHandler {
	
private LinkedHashMap<String,String> reportStatusTypesMap;

private LinkedHashMap<String,String> conceptsMap;

private String inputReportIdList;



private String concept;

private String reportStatusType;

private String userName;

private String password;

private Integer numberOfDays;

private List<ReportStatusVO> reportStatusVOList;

private String successFailureUrl;

private OmnitureBoostedProductTools omnitureTool;

private Repository omnitureReportRepository;

private String detailsByReportIdsQuery;

private String detailsByStatusQuery;



private boolean displayResult;

//read from config table so that if any new type defined then new type should be displayed in dropdown
private Map<String , String> allReportTypesMap;

private String reportType;

private String byReportIdStatus;

private BBBCatalogTools catalogTools;

private static String CLS_NAME="OmnitureDashboardFormhandler";

private static String REPORT_TYPES="REPORT_TYPES";

private static String ALL="All";

public boolean handleClear(final DynamoHttpServletRequest pRequest,	final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
	 
	logDebug(CLS_NAME+"[handleClear] Start");
	
	setUserName(BBBCoreConstants.BLANK);
	setPassword(BBBCoreConstants.BLANK);	 
	setConcept(BBBCoreConstants.BLANK);
	setReportStatusType(BBBCoreConstants.BLANK);
	setReportType(BBBCoreConstants.BLANK);
	setInputReportIdList(BBBCoreConstants.BLANK);
	logDebug(CLS_NAME+"[handleClear] End :]");
	
	return this.checkFormRedirect(this.getSuccessFailureUrl(),	this.getSuccessFailureUrl(), pRequest, pResponse);
}	
	
/**
 * handle Get Detail request
 * @param pRequest
 * @param pResponse
 * @return
 * @throws ServletException
 * @throws IOException
 */
public boolean handleGetDetail(final DynamoHttpServletRequest pRequest,	final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
	 
	logDebug(CLS_NAME+"[handleGetDetail] Start|  concept:["+getConcept()+"],ReportStatus:["+getReportStatusType()+"]");
	
	if(! validateInputs(pRequest,pResponse) ){
		 
			return this.checkFormRedirect(this.getSuccessFailureUrl(),	this.getSuccessFailureUrl(), pRequest, pResponse);
		 
	 }
	 
	if(validateAdminUser())
	{	
		String sqlQuery=null;
	  try {
		  
				logDebug(CLS_NAME+"[handleGetDetail] ,validateAdminUser done:["+getInputReportIdList()+"]");
		
			   if(getByReportIdStatus().equalsIgnoreCase(reportStatusType)) {
				 
					logDebug(CLS_NAME+"[handleGetDetail] ,validateAdminUser :inputReportList:["+getInputReportIdList()+"]");
					
					sqlQuery=buildSqlQueryByReportIds(getInputReportIdList());
					
					setReportStatusVOList(getOmnitureTool().getReportStatusBySQLQuery(sqlQuery));
					
					setDisplayResult(true);
				}
				else{ 
					 
				 	logDebug(CLS_NAME+"[handleGetDetail] | ReportStatusType:"+getReportStatusType()+",Concepts:"+getConcept());
				 	 
				 	sqlQuery=buildSqlQuery();
				 	
				 	setReportStatusVOList(getOmnitureTool().getReportStatusBySQLQuery(sqlQuery));
				 	 
				 	setDisplayResult(true);
				}
			
			} catch (BBBSystemException bbbSystemExc) {
				addFormException(new DropletException("System Exception occured"));
				logError(CLS_NAME+"[handleGetDetail] BBBSystemException,sqlQuery:["+sqlQuery+"],BBBSystemException:["+bbbSystemExc+"]",bbbSystemExc);
			}catch (Exception exception) {
				addFormException(new DropletException("Exception occured"));
				logError(CLS_NAME+"[handleGetDetail] BBBSystemException,sqlQuery:["+sqlQuery+"],Exception:["+exception+"]",exception);
			}
		
		}
	
	logDebug(CLS_NAME+"[handleGetDetail] End");
	
	return this.checkFormRedirect(this.getSuccessFailureUrl(),	this.getSuccessFailureUrl(), pRequest, pResponse);
}
/**
 * Validate user input before pull build sql query
 * @param pRequest
 * @param pResponse
 * @return
 * @throws ServletException
 * @throws IOException
 */

private boolean validateInputs(final DynamoHttpServletRequest pRequest,	final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
	logDebug(CLS_NAME+"[validateInputs] Start");
	
	boolean inputValidated=false;
	if(BBBUtility.isBlank(getUserName())){
		addFormException(new DropletException("Please provide admin user Name"));
	}
	if(BBBUtility.isBlank(getPassword())){
		addFormException(new DropletException("Please provide admin password"));
	}
	if(BBBUtility.isBlank(getReportStatusType())){ 
		addFormException(new DropletException("Please select Report Status from Dropdown List"));
	}
	if( ! BBBUtility.isBlank(getReportStatusType()) && ! byReportIdStatus.equalsIgnoreCase(getReportStatusType())){

		if(BBBUtility.isBlank(getConcept())){
			addFormException(new DropletException("Please select Concepts from Dropdown List")); 
		}
		if(BBBUtility.isBlank(getReportType())){
			addFormException(new DropletException("Please select Report Types from Dropdown List")); 
		}
	}else if(getByReportIdStatus().equalsIgnoreCase(reportStatusType) && BBBUtility.isBlank(getInputReportIdList()) )
	{
		addFormException(new DropletException("Please provide reportId to fetch detail"));
	}
	if(getFormExceptions() == null || getFormExceptions().size() == 0)
	{
		inputValidated=true;
	}
	
	logDebug(CLS_NAME+"[validateInputs] End::inputValidated:"+inputValidated);
	
	return inputValidated;
	
	 
}

/**
 * Validate admin user login detail from configure keys
 * @return
 */


public boolean validateAdminUser() {
	this.logDebug("validateAdminUser method start");
	List<String> adminUsers = null;
	List<String> listOfPasswords = null;
	
	boolean userValidated=false;
	try {
		adminUsers = this.getCatalogTools().getAllValuesForKey(	"ContentCatalogKeys", "OmniDashboard_admin_name");
		listOfPasswords = this.getCatalogTools().getAllValuesForKey("ContentCatalogKeys", "OmniDashboard_admin_password");
		
		if( BBBUtility.isListEmpty(adminUsers) || BBBUtility.isListEmpty(listOfPasswords))
		{
			logDebug(CLS_NAME+"[validateAdminUser] | Empty adminUsers/Password");
		}
		else{
			String adminUserName=adminUsers.get(0);
			String adminPassword=listOfPasswords.get(0);
			if(getUserName().equalsIgnoreCase(adminUserName) && getPassword().equals(adminPassword))
			{
				userValidated= true;
			}
		}
		if( ! userValidated) {
			addFormException(new DropletException("Please provide correct login details"));
		}
		}catch (final BBBSystemException |BBBBusinessException bbbException) {
			logError(CLS_NAME+"[validateAdminUser] :BBBSystemException| BBBBusinessException",bbbException);
		}
	this.logDebug("validateAdminUser method end");
	
	return userValidated;
}
/**
 * Used to buld sql query to pull report with list  of reportId
 * @param reportIds
 * @return
 */
private String buildSqlQueryByReportIds(String reportIds) {
	
	logDebug(CLS_NAME+":buildQueryByReportIds reportIds:["+reportIds+"]");
	
	reportIds=BBBUtility.removeWhiteSpace(reportIds);
	setInputReportIdList(reportIds); 
	StringBuffer query=  new StringBuffer("SELECT * FROM OMNITURE_REPORT_STATUS WHERE REPORT_ID IN (");
	 
    StringTokenizer reportIdTokenizer = new StringTokenizer(reportIds,BBBCoreConstants.COMMA);
	   
	 while(reportIdTokenizer.hasMoreElements()){
		 query.append(BBBCoreConstants.SINGLE_QUOTE);
		 query.append(reportIdTokenizer.nextElement().toString());
		 query.append(BBBCoreConstants.SINGLE_QUOTE);
		 
		 if(reportIdTokenizer.countTokens()> 0){
			 query.append(BBBCoreConstants.COMMA);
		 }
		  
	 }
	 query.append(") ORDER BY ID,REPORT_ID,BATCH_ID,BATCH_SEQ");
	 
	 String finalQuery=query.toString();
	 
	 logDebug(CLS_NAME+":buildQueryByReportIds End:finalQuery:["+finalQuery+"]");
	 
	 return finalQuery;
}

/**
 * Method used to build sql query with user inputs
 * @return
 */
private String buildSqlQuery() {

	logDebug(CLS_NAME+":buildSqlQuery");

 
	Calendar cal = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat(BBBCoreConstants.OMNITURE_DATE_FORMAT);
	cal.setTime(new Date());
	cal.add(Calendar.DATE, - getNumberOfDays());
	String dateFrom = sdf.format(cal.getTime());
	 
	StringBuffer query=  new StringBuffer("SELECT * FROM OMNITURE_REPORT_STATUS WHERE REPORT_TYPE IN (");
	query.append(getReportTypeList());
	query.append(") AND CONCEPT IN (");
	query.append(getSelectedList(getConcept()));
	query.append(") AND REPORT_OP_STATUS IN (");
	query.append(getSelectedList(getReportStatusType()));
	query.append(") AND TO_CHAR(QUEUED_DATE,'"+BBBCoreConstants.OMNITURE_DATE_FORMAT+"') >='"+dateFrom+"'");
	query.append(" ORDER BY ID,REPORT_ID,BATCH_ID,BATCH_SEQ");
	String finalQuery=query.toString();
	
	logDebug(CLS_NAME+":buildSqlQuery End:finalQuery:["+finalQuery+"]");
	
	return finalQuery;
}
	/**
	 * This method will used to build List of report type 
	 * If user select ALL then this method will return all reportType as String list separated by comma : [eg: ('OBP1','OBP2',....'Popular SearchTerm')]
	 * else selected ReportType
	 * @return
	 */

	private String getReportTypeList()
	{
		logDebug(CLS_NAME+"[getReportTypeList] | start. | getReportType:"+getReportType());
		StringBuffer reportTypeList= new StringBuffer();
			
		 if(ALL.equalsIgnoreCase(getReportType()))
		 {
			for(Map.Entry<String, String> entry: getAllReportTypesMap().entrySet()){
				
				if(! ALL.equalsIgnoreCase(entry.getKey()))
				{
					reportTypeList.append(BBBCoreConstants.SINGLE_QUOTE);
					reportTypeList.append(entry.getKey());
					reportTypeList.append(BBBCoreConstants.SINGLE_QUOTE);
					reportTypeList.append(BBBCoreConstants.COMMA);
				}
				
			}
			reportTypeList.deleteCharAt(reportTypeList.lastIndexOf(BBBCoreConstants.COMMA));
		 }
		 else
		 {
			 	reportTypeList.append(BBBCoreConstants.SINGLE_QUOTE);
				reportTypeList.append(getReportType());
				reportTypeList.append(BBBCoreConstants.SINGLE_QUOTE);
			 
		 }
		 
		 String finalReportTypeList=reportTypeList.toString();	
	
		 logDebug(CLS_NAME+"[getReportTypeList] | start. | finalReportTypeList:"+finalReportTypeList);
	
	 return finalReportTypeList;
	}

	/**
	 * Method used to build list of user input by user selection by Tokenizing
	 * failed-cancel string will be converted to 'failed','cancel'
	 * @param input 
	 * @return
	 */
	private String getSelectedList(String input)
	{
		logDebug(CLS_NAME+"[getList] | start. | selection:"+input);
		StringBuffer  list= new StringBuffer();
		StringTokenizer inputTokenizer= new StringTokenizer(input,BBBCoreConstants.HYPHEN);
		while(inputTokenizer.hasMoreElements())
		{
			list.append(BBBCoreConstants.SINGLE_QUOTE);
			list.append(inputTokenizer.nextElement());
			list.append(BBBCoreConstants.SINGLE_QUOTE);
			
			 if(inputTokenizer.countTokens()> 0){
				 list.append(BBBCoreConstants.COMMA);
			 }
		}
		 
		String finalList=list.toString();
		logDebug(CLS_NAME+"[getList] | ends. | finalList:"+finalList);
	
	 return finalList;
	}

	
	public void getAllReportType() {
		
		logDebug(CLS_NAME+"[getAllReportType] | Start");
		
		Map<String,String> reportType =(Map<String, String>) ServletUtil.getCurrentRequest().getSession().getAttribute(REPORT_TYPES);
		
		if(reportType != null && reportType.size() > 0)
		{
			setAllReportTypesMap(reportType);
		}
		else{
			 try{
				 	reportType	= new LinkedHashMap<String, String>();
				 	reportType.put("All","All");
				 	
					List<OmnitureReportConfigVo> reportConfigList=getOmnitureTool().getOmnitureReportConfig();
				   if(! BBBUtility.isListEmpty(reportConfigList))
				 	{
						  for( OmnitureReportConfigVo omnitureReportConfigVo: reportConfigList)
						  {
							  
							  reportType.put(omnitureReportConfigVo.getReportType(),omnitureReportConfigVo.getReportType());
							  
						  }
						  
					 	}
				 	
				 	ServletUtil.getCurrentRequest().getSession().setAttribute(REPORT_TYPES,reportType);
				 	
				 	setAllReportTypesMap(reportType);
				 	
				 	logDebug(CLS_NAME+"[getAllReportType] | allReportTypes["+reportType+"]");
				} 
				catch (RepositoryException repositoryException)
				{
					logError(CLS_NAME+"[getAllReportType] :RepositoryException",repositoryException);
				}
			  catch (Exception exception)
				{
					logError(CLS_NAME+"[getAllReportType] :Exception",exception);
				}
		}
	
	logDebug(CLS_NAME+"[getAllReportType] | End.");
}	

/**
 * @return the reportStatusTypesMap
 */
public LinkedHashMap<String, String> getReportStatusTypesMap() {
	return reportStatusTypesMap;
}


/**
 * @param reportStatusTypesMap the reportStatusTypesMap to set
 */
public void setReportStatusTypesMap(
		LinkedHashMap<String, String> reportStatusTypesMap) {
	this.reportStatusTypesMap = reportStatusTypesMap;
}


/**
 * @return the conceptsMap
 */
public LinkedHashMap<String, String> getConceptsMap() {
	return conceptsMap;
}


/**
 * @param conceptsMap the conceptsMap to set
 */
public void setConceptsMap(LinkedHashMap<String, String> conceptsMap) {
	this.conceptsMap = conceptsMap;
}


/**
 * @return the inputReportIdList
 */
public String getInputReportIdList() {
	return inputReportIdList;
}


/**
 * @param inputReportIdList the inputReportIdList to set
 */
public void setInputReportIdList(String inputReportIdList) {
	this.inputReportIdList = inputReportIdList;
}


/**
 * @return the concept
 */
public String getConcept() {
	return concept;
}


/**
 * @param concept the concept to set
 */
public void setConcept(String concept) {
	this.concept = concept;
}


/**
 * @return the reportStatusType
 */
public String getReportStatusType() {
	return reportStatusType;
}


/**
 * @param reportStatusType the reportStatusType to set
 */
public void setReportStatusType(String reportStatusType) {
	this.reportStatusType = reportStatusType;
}


/**
 * @return the userName
 */
public String getUserName() {
	return userName;
}


/**
 * @param userName the userName to set
 */
public void setUserName(String userName) {
	this.userName = userName;
}


/**
 * @return the password
 */
public String getPassword() {
	return password;
}


/**
 * @param password the password to set
 */
public void setPassword(String password) {
	this.password = password;
}


/**
 * @return the reportStatusVOList
 */
public List<ReportStatusVO> getReportStatusVOList() {
	return reportStatusVOList;
}


/**
 * @param reportStatusVOList the reportStatusVOList to set
 */
public void setReportStatusVOList(List<ReportStatusVO> reportStatusVOList) {
	this.reportStatusVOList = reportStatusVOList;
}


/**
 * @return the successFailureUrl
 */
public String getSuccessFailureUrl() {
	return successFailureUrl;
}


/**
 * @param successFailureUrl the successFailureUrl to set
 */
public void setSuccessFailureUrl(String successFailureUrl) {
	this.successFailureUrl = successFailureUrl;
}


/**
 * @return the omnitureTool
 */
public OmnitureBoostedProductTools getOmnitureTool() {
	return omnitureTool;
}


/**
 * @param omnitureTool the omnitureTool to set
 */
public void setOmnitureTool(OmnitureBoostedProductTools omnitureTool) {
	this.omnitureTool = omnitureTool;
}


/**
 * @return the omnitureReportRepository
 */
public Repository getOmnitureReportRepository() {
	return omnitureReportRepository;
}


/**
 * @param omnitureReportRepository the omnitureReportRepository to set
 */
public void setOmnitureReportRepository(Repository omnitureReportRepository) {
	this.omnitureReportRepository = omnitureReportRepository;
}




/**
 * @return the allReportTypesMap
 */
public Map<String, String> getAllReportTypesMap() {
	 getAllReportType();
	return allReportTypesMap;
}


/**
 * @param allReportTypesMap the allReportTypesMap to set
 */
public void setAllReportTypesMap(Map<String, String> allReportTypesMap) {
	this.allReportTypesMap = allReportTypesMap;
}


/**
 * @return the reportType
 */
public String getReportType() {
	return reportType;
}


/**
 * @param reportType the reportType to set
 */
public void setReportType(String reportType) {
	this.reportType = reportType;
}


/**
 * @return the byReportIdStatus
 */
public String getByReportIdStatus() {
	return byReportIdStatus;
}


/**
 * @param byReportIdStatus the byReportIdStatus to set
 */
public void setByReportIdStatus(String byReportIdStatus) {
	this.byReportIdStatus = byReportIdStatus;
}


/**
 * @return the catalogTools
 */
public BBBCatalogTools getCatalogTools() {
	return catalogTools;
}


/**
 * @param catalogTools the catalogTools to set
 */
public void setCatalogTools(BBBCatalogTools catalogTools) {
	this.catalogTools = catalogTools;
}


/**
 * @return the detailsByReportIdsQuery
 */
public String getDetailsByReportIdsQuery() {
	return detailsByReportIdsQuery;
}


/**
 * @param detailsByReportIdsQuery the detailsByReportIdsQuery to set
 */
public void setDetailsByReportIdsQuery(String detailsByReportIdsQuery) {
	this.detailsByReportIdsQuery = detailsByReportIdsQuery;
}


/**
 * @return the detailsByStatusQuery
 */
public String getDetailsByStatusQuery() {
	return detailsByStatusQuery;
}


/**
 * @param detailsByStatusQuery the detailsByStatusQuery to set
 */
public void setDetailsByStatusQuery(String detailsByStatusQuery) {
	this.detailsByStatusQuery = detailsByStatusQuery;
}


/**
 * @return the numberOfDays
 */
public Integer getNumberOfDays() {
	if(numberOfDays == null){
		numberOfDays=1;
	}
	
	return numberOfDays;
}


/**
 * @param numberOfDays the numberOfDays to set
 */
public void setNumberOfDays(Integer numberOfDays) {
	this.numberOfDays = numberOfDays;
}


/**
 * @return the displayResult
 */
public boolean isDisplayResult() {
	return displayResult;
}


/**
 * @param displayResult the displayResult to set
 */
public void setDisplayResult(boolean displayResult) {
	this.displayResult = displayResult;
}



}
