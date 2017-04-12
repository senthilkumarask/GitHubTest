package com.bbb.feeds.hartehanks.marshaller;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import atg.core.util.StringUtils;
import atg.multisite.SiteManager;
import atg.repository.MutableRepository;

import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.feeds.hartehanks.utils.HHUserPreferencesFeedTools;
import com.bbb.feeds.marketing.vo.MarketingFeedVO;
import com.bbb.feeds.marshaller.IFeedsMarshaller;
import com.bbb.utils.BBBUtility;

/**
 * The class generates Harte Hank User prferences feed
 * 
 * @author Prashanth K Bhoomula
 * 
 */
public class HarteHanksFeedMarshaller extends BBBGenericService implements
		IFeedsMarshaller {

	private Timestamp lastModDate = null;
	private MutableRepository feedRepository;
	private String typeOfFeed = null;
	private HHUserPreferencesFeedTools feedTools = null;
	private BBBCatalogTools catalogTools = null;	
	private String hhCntrlFeedFileName = null;
	private String hhDataFeedFileName = null;
	private String hhFeedFilePath = null;
	private SiteManager siteManager = null;
	private String fieldDelimiter = null;
	private boolean includeHeaders = false; 
	private List<String> controlFeedHeaders = null;
	private List<String> detailsFeedHeaders = null;	
	private long numOfUsersInFile = 0;
	private long start_index = 0;
	private long profileBatchSize = 0;
	private int file_index = 0;
	private String feedPath = null;

	/**
	 * This method calls will get the User preferences and will generate the feed.
	 */
	public void marshall(boolean isFullDataFeed, Timestamp schedulerStartDate) {

		logDebug("HarteHanksFeedMarshaller Method : marshal start");
		file_index = 0;
		long totalCount=start_index;
		int count=0;
		int i=0;
		String rqlQuery="all ";
		
		boolean status = true;
		if (isFullDataFeed) {
			logDebug("Fetch data for full feed :isFullDataFeed: " + isFullDataFeed + " lastModifiedDate:"+ lastModDate);
		}
		else {
			logDebug("Get last modified date from repository");
			this.lastModDate = getFeedTools().getLastModifiedDate(getTypeOfFeed());
			if (lastModDate != null) {
				logDebug("Fetch data for incremental feed lastModifiedDate:" + lastModDate);
				rqlQuery = "lastModifiedDate>?0 ";
			}
		}
		
		List<MarketingFeedVO>  feedVOList = null;
		String rql = "";
		try {
			while(true) {
				this.feedPath = getFeedFilePath();				
				rql=rqlQuery +"ORDER BY id RANGE "+ totalCount + "+" + this.getProfileBatchSize() ;
				logDebug("RQL Query to fetch the Profile list :"+rql);
				feedVOList = getFeedTools().getUserPreferences(isFullDataFeed, rql, this.lastModDate, getDetailsFeedHeaders());
				if(feedVOList==null || feedVOList.isEmpty()){
					if(totalCount==0) {
						throw new BBBBusinessException(BBBCatalogErrorCodes.NO_DATA_FOR_PRODUCT_FEED);
					}
					logDebug("Didn't found any profiles for rql :"+rqlQuery);
					break;
				}
				logDebug("HHUserPreferencesFeedTools [marshal] prepared the VO List: "+feedVOList.size());			
				status = generateFeed(feedVOList);
				count=feedVOList.size();
				totalCount = totalCount +count;
			}
		} catch (BBBBusinessException e) {
			status = false;
			//e.printStackTrace();
			logError(e.getMessage(),e);
		} catch (BBBSystemException e) {
			status = false;			
			//e.printStackTrace();
			logError(e.getMessage(),e);
		}
		finally {
		    getFeedTools().updateRepository(getTypeOfFeed(), schedulerStartDate, isFullDataFeed, status);
		}
		logDebug("HarteHanksFeedMarshaller Method : marshal end");		
	}

	protected boolean generateControlFeed(String configPath, String cntlFeedFileName, String dataFeedFileName, int recordCount, int checkSum) throws BBBSystemException {
		
		logDebug("HarteHanksFeedMarshaller Method : generateControlFeed start");
		StringBuilder  ctrlFeedContent = new StringBuilder();		
		if(includeHeaders) {
			int i = 0;
			for(String header: controlFeedHeaders) {
				
				if(i<controlFeedHeaders.size()-1) {
					ctrlFeedContent.append(header+getFieldDelimiter());
				}
				else {
					ctrlFeedContent.append(header);
				}
				i++;
			}
			ctrlFeedContent.append("\n");
		}
		ctrlFeedContent.append(dataFeedFileName+getFieldDelimiter());
		ctrlFeedContent.append(new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new java.util.Date())+getFieldDelimiter());
		ctrlFeedContent.append(checkSum+getFieldDelimiter());
		ctrlFeedContent.append(recordCount);
		logDebug("HarteHanksFeedMarshaller.generateControlFeed Method : Writing to control file"+cntlFeedFileName);
		return BBBUtility.writeFile(configPath, cntlFeedFileName, ctrlFeedContent.toString());			
	}
	
	protected boolean generateFeed(List<MarketingFeedVO> feedVOList) {
		   
			logDebug("HarteHanksFeedMarshaller Method : generateFeed start");
			StringBuilder  feedContent = new StringBuilder();
			if(includeHeaders) {
				int i = 0;
				for(String header: detailsFeedHeaders) {
					
					if(i<detailsFeedHeaders.size()-1) {
						feedContent.append(header+getFieldDelimiter());
					}
					else {
						feedContent.append(header);
					}
					i++;
				}
				feedContent.append("\n");
			}
			String tempStr = null;
	        int checkSum = 0, userCount = 0, totalCount = 0;
	        if(feedVOList!=null) {
				try {
					for(MarketingFeedVO feedVO: feedVOList) {
						String concept = feedVO.get("concept");
						if(!StringUtils.isEmpty(concept)) {
							try {
								checkSum += Integer.parseInt(concept);
							}catch(NumberFormatException ne) {
								logError("User doesn't have concept, setting it to 0"+ ne);
							}
						}
						tempStr = feedVO.toString(getDetailsFeedHeaders(), getFieldDelimiter());
						tempStr = tempStr.substring(0,tempStr.length()-1);
						feedContent.append(tempStr+"\n");
						userCount++;
						totalCount++;
						if(userCount==getNumOfUsersInFile()
								|| totalCount == feedVOList.size()) {
						  logDebug("HarteHanksFeedMarshaller.generateFeed : writing to file "+file_index); 
						  writeToFile(feedContent, checkSum, userCount, file_index );
						  checkSum = 0;
						  userCount = 0;
						  feedContent.delete(0, feedContent.length());
						  file_index++;
						}
					}
					return true;
				} catch (BBBSystemException e) {
					logError("Error Occured while retriving the feed file configurations"+ e);
				} catch (BBBBusinessException e) {
					logError("Error Occured while retriving the feed file configurations"+ e);
				}
			}
	        logDebug("HarteHanksFeedMarshaller Method : generateFeed End");
			return false;
	}

	private boolean writeToFile(StringBuilder feedContent, int checkSum, int userCount, int index)
			throws BBBSystemException, BBBBusinessException {
		
		logDebug("HarteHanksFeedMarshaller Method : writeToFile Start");
		String cntrlFeedFileName = "", dataFeedFileName = "";

		List<String> configControlFileName = getCatalogTools().getAllValuesForKey(BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE, getHhCntrlFeedFileName());				
		String date = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
		if (configControlFileName == null 
				|| configControlFileName.isEmpty()) {
			cntrlFeedFileName = getTypeOfFeed()+"-Cntrl-"+date+"-"+index+".txt";
		}
		else {
			cntrlFeedFileName =configControlFileName.get(0).replaceAll("yyyymmdd", date+"-"+index);
		}
		logDebug(cntrlFeedFileName+" contrl Feed file created for "+getTypeOfFeed());
		List<String> configDataFileName = getCatalogTools().getAllValuesForKey(BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE, getHhDataFeedFileName());
		if (configDataFileName == null 
				|| configDataFileName.isEmpty()) {
			dataFeedFileName = getTypeOfFeed()+"-data-"+date+".txt";
		}
		else {
			dataFeedFileName =configDataFileName.get(0).replaceAll("yyyymmdd", date+"-"+index);
		}
		logDebug(dataFeedFileName+" data Feed file created for "+getTypeOfFeed());
		
		generateControlFeed(feedPath, cntrlFeedFileName, dataFeedFileName, userCount, checkSum);
		logDebug("HarteHanksFeedMarshaller writeToFile Method :  Writing to data file"+ dataFeedFileName);
		return BBBUtility.writeFile(feedPath, dataFeedFileName, feedContent.toString());
	}

	/**
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private String getFeedFilePath() throws BBBSystemException,
			BBBBusinessException {
		List<String> feedConfigPath = null;
		List<String> feedFolder = null;
		String pathSeparator = File.separator;
		feedConfigPath = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE, BBBCoreConstants.SCHEDULED_FEEDS_PATH_CONFIG_KEY);
		if(feedConfigPath == null 
				 || feedConfigPath.isEmpty()) {
			throw new BBBBusinessException(BBBCoreConstants.SCHEDULED_FEEDS_PATH_CONFIG_KEY+" configuration missing for ");
		}
		
		feedFolder = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE, this.getHhFeedFilePath());
		if (feedFolder == null || feedFolder.isEmpty()) {
			throw new BBBBusinessException(this.getHhDataFeedFileName()+" configuration missing for "+getTypeOfFeed());				
		}
		String feedPath = feedConfigPath.get(0)+pathSeparator+feedFolder.get(0);
		return feedPath;
	}
	
	public Timestamp getLastModDate() {
		return lastModDate;
	}

	public void setLastModDate(Timestamp lastModDate) {
		this.lastModDate = lastModDate;
	}

	public MutableRepository getFeedRepository() {
		return feedRepository;
	}

	public void setFeedRepository(MutableRepository feedRepository) {
		this.feedRepository = feedRepository;
	}

	public String getTypeOfFeed() {
		return typeOfFeed;
	}

	public void setTypeOfFeed(String typeOfFeed) {
		this.typeOfFeed = typeOfFeed;
	}

	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	public HHUserPreferencesFeedTools getFeedTools() {
		return feedTools;
	}

	public void setFeedTools(HHUserPreferencesFeedTools feedTools) {
		this.feedTools = feedTools;
	}

	public SiteManager getSiteManager() {
		return siteManager;
	}

	public void setSiteManager(SiteManager siteManager) {
		this.siteManager = siteManager;
	}

	public String getHhCntrlFeedFileName() {
		return hhCntrlFeedFileName.replaceAll("yyyymmdd", new SimpleDateFormat(
				"yyyyMMdd").format(new java.util.Date()));
	}

	public void setHhCntrlFeedFileName(String hhCntrlFeedFileName) {
		this.hhCntrlFeedFileName = hhCntrlFeedFileName;
	}

	public String getHhDataFeedFileName() {
		return hhDataFeedFileName.replaceAll("yyyymmdd", new SimpleDateFormat(
				"yyyyMMdd").format(new java.util.Date()));
	}

	public void setHhDataFeedFileName(String hhDataFeedFileName) {
		this.hhDataFeedFileName = hhDataFeedFileName;
	}

	public String getHhFeedFilePath() {
		return hhFeedFilePath;
	}

	public void setHhFeedFilePath(String hhFeedFilePath) {
		this.hhFeedFilePath = hhFeedFilePath;
	}

	public String getFieldDelimiter() {
		return fieldDelimiter;
	}

	public void setFieldDelimiter(String fieldDelimiter) {
		this.fieldDelimiter = fieldDelimiter;
	}

	public boolean isIncludeHeaders() {
		return includeHeaders;
	}

	public void setIncludeHeaders(boolean includeHeaders) {
		this.includeHeaders = includeHeaders;
	}

	public List<String> getControlFeedHeaders() {
		return controlFeedHeaders;
	}

	public void setControlFeedHeaders(List<String> controlFeedHeaders) {
		this.controlFeedHeaders = controlFeedHeaders;
	}

	public List<String> getDetailsFeedHeaders() {
		return detailsFeedHeaders;
	}

	public void setDetailsFeedHeaders(List<String> detailsFeedHeaders) {
		this.detailsFeedHeaders = detailsFeedHeaders;
	}

	public long getNumOfUsersInFile() {
		return numOfUsersInFile;
	}

	public void setNumOfUsersInFile(long numOfUsersInFile) {
		this.numOfUsersInFile = numOfUsersInFile;
	}

	public long getProfileBatchSize() {
		return profileBatchSize;
	}

	public void setProfileBatchSize(long profileBatchSize) {
		this.profileBatchSize = profileBatchSize;
	}

	public long getStart_index() {
		return start_index;
	}

	public void setStart_index(long start_index) {
		this.start_index = start_index;
	}
}