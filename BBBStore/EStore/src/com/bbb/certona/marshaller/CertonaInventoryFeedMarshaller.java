package com.bbb.certona.marshaller;

import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.QueryOptions;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.SortDirective;
import atg.repository.SortDirectives;

import com.bbb.certona.utils.CertonaInventoryFeedTools;
import com.bbb.certona.vo.CertonaInventoryVO;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCertonaConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.jaxb.certona.inventoryfeed.InventoryDetail;
import com.bbb.framework.jaxb.certona.inventoryfeed.InventoryFeedType;
import com.bbb.framework.jaxb.certona.inventoryfeed.ObjectFactory;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 * Fetches the unprocessed records from repository and marshal's them
 * to feed.
 * 
 * @author njai13
 */
public class CertonaInventoryFeedMarshaller  extends BBBGenericService implements ICertonaMarshaller  {
	private static final String CERTONA_INVENTORY_FEED_CHECK_SUM = "CertonaInventoryFeed_CheckSum.txt";
	private static final String QUERY_ALL = "ALL";
	private static final String RANGE =" RANGE ";
	private CertonaInventoryFeedTools feedTools;
	private MutableRepository certonaRepository;
	private BBBCatalogTools catalogTools;
	private String filePattern;

	/**
	 * Holds values for full or incremental feed
	 */
	private Map<String,String> modeMap=new HashMap<String,String>();

	/**
	 * Path at which feed file needs to be generated
	 */
	private String filePath;

	/**
	 * @return the filePattern
	 */
	public String getFilePattern() {
		return filePattern;
	}
	/**
	 * @param filePattern the filePattern to set
	 */
	public void setFilePattern(String filePattern) {
		this.filePattern = filePattern;
	}

	/**
	 * Name of the feed file
	 */
	private String fileName;

	/**
	 * Name of the feed file
	 */
	private String fileExtention;

	/**
	 * Represents the number of records each feed file would contain. 
	 * This is used to limit the file size if records are huge.
	 */
	private String batchSize;

	/**
	 * Flag to identify if the job is running by scheduler of forced
	 * manually. This is extension point in case feed is required out of turn.
	 * Default is false
	 */
	protected Boolean isManualRun;

	/**
	 * It will only come into picture when <code>mIsManualRun</code> is true.
	 * Feed will be created with date specified by this attribute.  
	 */
	protected Timestamp userLastModDate = null;


	/**
	 * @return the feedTools
	 */
	public CertonaInventoryFeedTools getFeedTools() {
		return feedTools;
	}
	/**
	 * @param feedTools the feedTools to set
	 */
	public void setFeedTools(CertonaInventoryFeedTools feedTools) {
		this.feedTools = feedTools;
	}
	/**
	 * @return the certonaRepository
	 */
	public MutableRepository getCertonaRepository() {
		return certonaRepository;
	}
	/**
	 * @param certonaRepository the certonaRepository to set
	 */
	public void setCertonaRepository(MutableRepository certonaRepository) {
		this.certonaRepository = certonaRepository;
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
	 * @return the modeMap
	 */
	public Map<String, String> getModeMap() {
		return modeMap;
	}
	/**
	 * @param modeMap the modeMap to set
	 */
	public void setModeMap(Map<String, String> modeMap) {
		this.modeMap = modeMap;
	}
	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}
	/**
	 * @param filePath the filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the fileExtention
	 */
	public String getFileExtention() {
		return fileExtention;
	}
	/**
	 * @param fileExtention the fileExtention to set
	 */
	public void setFileExtention(String fileExtention) {
		this.fileExtention = fileExtention;
	}
	/**
	 * @return the batchSize
	 */
	public String getBatchSize() {
		return batchSize;
	}
	/**
	 * @param batchSize the batchSize to set
	 */
	public void setBatchSize(String batchSize) {
		this.batchSize = batchSize;
	}
	/**
	 * @return the isManualRun
	 */
	public Boolean getIsManualRun() {
		return isManualRun;
	}
	/**
	 * @param isManualRun the isManualRun to set
	 */
	public void setIsManualRun(Boolean isManualRun) {
		this.isManualRun = isManualRun;
	}
	/**
	 * @return the userLastModDate
	 */
	public Timestamp getUserLastModDate() {
		return userLastModDate;
	}
	/**
	 * @param userLastModDate the userLastModDate to set
	 */
	public void setUserLastModDate(Timestamp userLastModDate) {
		this.userLastModDate = userLastModDate;
	}


	public void marshall(final boolean isFullDataFeed, final Timestamp schedulerStartDate){
		int totalCount=0;
		int count=0;
		int i=0;
		String rqlQuery=null;
		List<RepositoryItem> inventoryRepoItems = null;
		boolean status = true;

		logDebug("CertonaInventoryFeedMarshaller Method : marshall ");
		logDebug("is full feed required " + isFullDataFeed+ " scheduler start time "+schedulerStartDate +" is this manual run of scheduler ? "+getIsManualRun());

		try {
			rqlQuery=QUERY_ALL;
			if (this.getIsManualRun() || !isFullDataFeed) {

				logDebug("Get last modified date from repository ");

				this.userLastModDate= this.getLastModifiedDate();
				if(this.userLastModDate!=null){
					String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz").format(this.userLastModDate);
					rqlQuery=BBBCertonaConstants.LAST_MODIFIED_DATE_CERTONA_PROPERTY_NAME+" > datetime(\""+date+"\")";
					}
				logDebug("last modified date for inventory: " + this.userLastModDate);
			}
			int inventoryBatchSize=this.getFeedBatchFrmConfigRepo();
			String rqlQueryRange=rqlQuery+" ORDER BY id " + RANGE+ totalCount + "+" + inventoryBatchSize;

			inventoryRepoItems=this.getFeedTools().getInventoryDetails(isFullDataFeed, userLastModDate,rqlQueryRange);
			   if(inventoryRepoItems==null || inventoryRepoItems.isEmpty()){
				return;
				}
				totalCount=inventoryRepoItems.size();
				count=totalCount;
				inventoryRepoItems=this.getFeedTools().getInventoryDetails(isFullDataFeed, userLastModDate,rqlQueryRange);
				while(count==inventoryBatchSize){
					inventoryRepoItems=null;
					rqlQueryRange=rqlQuery +" ORDER BY id "+ RANGE+ totalCount + "+" +inventoryBatchSize;
					inventoryRepoItems=this.getFeedTools().getInventoryDetails(isFullDataFeed, userLastModDate,rqlQueryRange);
					if(inventoryRepoItems==null || inventoryRepoItems.isEmpty()){
						return;
					}
					i++;
					status = populateInventoryFeed( getInventoryVO(inventoryRepoItems), i);
					count=inventoryRepoItems.size();
					totalCount = totalCount +count;
				}
				if (getFilePathFrmConfigRepo() != null && !getFilePathFrmConfigRepo().isEmpty()) {
				
					logDebug("Inventories Processed : "+String.valueOf(totalCount));
					
					BBBUtility.writeFile(getFilePathFrmConfigRepo(), CERTONA_INVENTORY_FEED_CHECK_SUM, "Inventories Processed :" +String.valueOf(totalCount));
				}
		} catch (BBBBusinessException e1) {
			
				logError(LogMessageFormatter.formatMessage(null,
						"CertonaInventoryFeedMarshaller.marshall() | BBBBusinessException "), e1);
			
			status = false;
		} catch (BBBSystemException e) {
			
				logError(
						LogMessageFormatter.formatMessage(null, "CertonaInventoryFeedMarshaller.marshall() | BBBSystemException "),
						e);
			
			status = false;
		} finally {
			this.updateCertonaRepository(schedulerStartDate, isFullDataFeed,  status);
		}



	}

	/**
	 * The method prepares a list of CertonaInventoryVO for each inventory Repository Item
	 * in  the list of items sent
	 * @param subInvRepoList
	 * @return
	 */
	public List<CertonaInventoryVO> getInventoryVO(List<RepositoryItem> subInvRepoList) {
		List<CertonaInventoryVO> list = new ArrayList<CertonaInventoryVO>();
		if (subInvRepoList != null && !subInvRepoList.isEmpty()) {
			for (int i = 0; i < subInvRepoList.size(); i++) {
				list.add(this.getFeedTools().populateInventoryVO(subInvRepoList.get(i))); 
			}
		}
		return list;
	}

	public boolean populateInventoryFeed( List<CertonaInventoryVO> inventoryVOList, int indexOfFile)
			throws BBBBusinessException, BBBSystemException {
	
			logDebug("CertonaInventoryFeedMarshaller Method : populateInventoryFeed");
			logDebug(inventoryVOList!=null && !inventoryVOList.isEmpty()?
					"no of inventory items to be sent in feed in" + indexOfFile + " th file is:" + inventoryVOList.size():" inventory list is null or empty");
		final ObjectFactory inventoryObject = new ObjectFactory();
		InventoryFeedType inventoryFeedType =	inventoryObject.createInventoryFeedType();
		final List<InventoryDetail> inventoryDetailList = inventoryFeedType.getInventoryAttributes();

		if(inventoryVOList != null && !inventoryVOList.isEmpty()){

			for(CertonaInventoryVO currInventoryVO : inventoryVOList){
				String invSkuId=currInventoryVO.getSkuId();
					logDebug("Adding inventory details for sku id "+invSkuId);
				final InventoryDetail inventoryDetail = inventoryObject.createInventoryDetail();
				inventoryDetail.setSkuId(invSkuId);
				inventoryDetail.setStockBBBCA(currInventoryVO.getStockBBBCA());
				inventoryDetail.setStockBBBUS(currInventoryVO.getStockBBBUS());
				inventoryDetail.setStockBuyBuyBaby(currInventoryVO.getStockBuyBuyBaby());
				inventoryDetail.setRegistryStockBBBCA(currInventoryVO.getRegistryStockBBBCA());
				inventoryDetail.setRegistryStockBBBUS(currInventoryVO.getRegistryStockBBBUS());
				inventoryDetail.setRegistryStockBuyBuyBaby(currInventoryVO.getRegistryStockBuyBuyBaby());
				inventoryDetailList.add(inventoryDetail);
			}

		}

		inventoryFeedType.getInventoryAttributes().addAll(inventoryDetailList);
			logDebug("Exiting populateInventoryFeed of CertonaInventoryFeedMarshaller");
		return this.getMarshalFile(inventoryFeedType, inventoryObject, indexOfFile);
	}

	/**
	 * The method creates a JAXB marshaled xml file after data is populated in
	 * JAXB classes for inventory feed
	 * 
	 * @param inventoryFeed
	 * @param catergoryObject
	 * @param indexOfFile
	 *          no of the file that is created for the feed
	 * @return true if marshaling is successful else return false
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public boolean getMarshalFile(InventoryFeedType inventoryFeedType,
			final ObjectFactory inventoryObject, int indexOfFile)
					throws BBBBusinessException, BBBSystemException {
			logDebug("CertonaInventoryFeedMarshaller Method : getMarshalFile ");
		boolean status = true;
		try {
			final JAXBElement<InventoryFeedType> inventoryRoot = inventoryObject.createInventoryFeed(inventoryFeedType);
			final StringWriter stringWriter = new StringWriter();
			final JAXBContext context = JAXBContext.newInstance(InventoryFeedType.class);
			final Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(inventoryRoot, stringWriter);
			final String inventoryXml = stringWriter.getBuffer().toString();

			final List<String> configPath = this.getCatalogTools()
					.getContentCatalogConfigration(this.getFilePath());
			final List<String> configFile = this.getCatalogTools()
					.getContentCatalogConfigration(this.getFileName());
			if (configPath != null && !configPath.isEmpty() && configFile != null && !configFile.isEmpty()) {
				final String fileName = getFileName(configFile.get(0), indexOfFile);
					logDebug(indexOfFile + "th file created for inventory:" + configPath.get(0) + fileName);
				BBBUtility.writeFile(configPath.get(0), fileName, inventoryXml);

			}
		} catch (JAXBException e) {
			
				logError(LogMessageFormatter.formatMessage(null,
						"CertonaInventoryFeedMarshaller.getMarshalFile() | BBBBusinessException "), e);
			
			status = false;
			throw new BBBBusinessException(BBBCatalogErrorCodes.JAXB_EXCEPTION,BBBCatalogErrorCodes.JAXB_EXCEPTION, e);

		}
		return status;

	}

	/**
	 * Returns name of the file that is created for the feed
	 * 
	 * @param fileSuffix
	 * @param indexOfFile
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	private String getFileName(String fileSuffix, int indexOfFile) throws BBBSystemException, BBBBusinessException {
		String fileName = indexOfFile +"-"+ fileSuffix;

		final List<String> filePattern = this.getCatalogTools().getContentCatalogConfigration(
				this.getFilePattern());
		final List<String> fileExt = this.getCatalogTools().getContentCatalogConfigration(this.getFileExtention());
		if (filePattern != null && !filePattern.isEmpty()) {
			final String pattern = filePattern.get(0);
			final Date currentDate = new Date();
			final SimpleDateFormat format = new SimpleDateFormat(pattern);
			final String dateInFormat = format.format(currentDate);
			fileName = dateInFormat + "-" + fileName+"."+fileExt.get(0);

		}
		return fileName;
	}


	/**
	 * Returns the date when scheduler successfully created feed. If this is 
	 * first time scheduler is running then this date would return null.
	 * 
	 * @return lastModifiedDate 
	 * 					- Last time when scheduler successfully ran and creatd feed
	 *
	 * @throws BBBSystemException 
	 */
	private Timestamp getLastModifiedDate() throws BBBSystemException{

		
		logDebug("Entering getLastModifiedDate of CertonaInventoryFeedMarshaller");
		

		Timestamp lastModifiedDate = null;

		if(getCertonaRepository() == null){
			
				logInfo("Certona Repository not configured correctly");
			
			throw new BBBSystemException(BBBCatalogErrorCodes.REPOSITORY_NOT_CONFIGURED_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.REPOSITORY_NOT_CONFIGURED_REPOSITORY_EXCEPTION);
		}

		try {
			final RepositoryView view = getCertonaRepository().getView(BBBCertonaConstants.FEED);

			final QueryBuilder builder = view.getQueryBuilder();
			final Query[] queries = new Query[2];

			final QueryExpression typeOfFeed = builder.createPropertyQueryExpression(BBBCertonaConstants.TYPE_OF_FEED);
			final QueryExpression inventory = builder.createConstantQueryExpression("inventory");
			queries[0] = builder.createComparisonQuery(typeOfFeed, inventory, QueryBuilder.EQUALS);		

			final QueryExpression status = builder.createPropertyQueryExpression(BBBCertonaConstants.STATUS);
			final QueryExpression statusValue = builder.createConstantQueryExpression(true);
			queries[1] = builder.createComparisonQuery(status, statusValue, QueryBuilder.EQUALS);

			final Query feedTypeQuery = builder.createAndQuery(queries);

			final SortDirectives sortDirectives = new SortDirectives();
			sortDirectives.addDirective(new SortDirective(BBBCertonaConstants.LAST_MODIFIED_DATE, SortDirective.DIR_DESCENDING));

			final RepositoryItem [] certonaItems =view.executeQuery(feedTypeQuery, new QueryOptions(0, 1, sortDirectives,null));

			if(certonaItems != null && certonaItems.length > 0){
				lastModifiedDate =  (Timestamp) certonaItems[0].getPropertyValue(BBBCertonaConstants.LAST_MODIFIED_DATE);
			}
		} catch (RepositoryException re) {
			
				logError(LogMessageFormatter.formatMessage(null, "CertonaInventoryFeedMarshaller.getLastModifiedDate() | RepositoryException "), re);
			
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_FETCH_DATA_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_FETCH_DATA_REPOSITORY_EXCEPTION, re);
		}		

		
			logDebug("Exiting getLastModifiedDate of CertonaInventoryFeedMarshaller. Last modified date is :"+lastModifiedDate);
		

		return lastModifiedDate;
	}


	/**
	 * Returns the file path from configure repository and returns the file
	 * path
	 * 
	 * @param counter
	 * @return feedFilePath - File path
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public String getFilePathFrmConfigRepo() throws BBBSystemException, BBBBusinessException{

		
			logDebug("Entering getFilePathFrmConfigRepo of CertonaInventoryFeedMarshaller");
		

		String feedFilePath=null;
		final List<String> filePath = this.getCatalogTools().getContentCatalogConfigration(this.getFilePath());		

		if(filePath != null && !filePath.isEmpty()){

			feedFilePath = filePath.get(0);			

			
				logDebug("Feed file path is : "+feedFilePath);
			
		}

		
			logDebug("Exiting getFilePathFrmConfigRepo of CertonaInventoryFeedMarshaller");
		
		return feedFilePath;
	}



	public int getFeedBatchFrmConfigRepo() throws BBBSystemException, BBBBusinessException{

		
			logDebug("Entering getFeedBatchFrmConfigRepo of CertonaInventoryFeedMarshaller");
		

		int batchSizeFromCfg = 0;		
		final List<String> batchSizes = this.getCatalogTools().getContentCatalogConfigration(this.getBatchSize());

		if(batchSizes != null && !batchSizes.isEmpty()){
			batchSizeFromCfg = Integer.valueOf(batchSizes.get(0));

			
				logDebug("batch size is : "+batchSizeFromCfg);
			
		}

		
			logDebug("Exiting getFileNameFrmConfigRepo of CertonaInventoryFeedMarshaller");
		

		return batchSizeFromCfg;
	}






	/**
	 * The method updates the certona repository with the details of the scheduler
	 * run
	 * 
	 * @param schedulerStartDate
	 * @param isFullDataFeed
	 * @param typeOfFeed
	 * @param status
	 */
	public void updateCertonaRepository(Timestamp schedulerStartDate, boolean isFullDataFeed, 
			boolean status) {
		
			logDebug("CertonaInventoryFeedMarshaller Method : updateCertonaRepository");
			logDebug("scheduler Start Date :" + schedulerStartDate + " full feed?: " + isFullDataFeed + " typeOfFeed:inventory "
					+ " status:" + status);
		
		final Date schedulerEndDate = new Date();
		try {
			final MutableRepositoryItem certonaItem = this.getCertonaRepository().createItem(BBBCertonaConstants.FEED);

			certonaItem.setPropertyValue(BBBCertonaConstants.LAST_MODIFIED_DATE, schedulerStartDate);
			certonaItem.setPropertyValue(BBBCertonaConstants.SCHEDULER_COMPLETION_DATE, schedulerEndDate);
			certonaItem.setPropertyValue(BBBCertonaConstants.SCHEDULER_START_DATE, schedulerStartDate);
			certonaItem.setPropertyValue(BBBCertonaConstants.STATUS, status);
			certonaItem.setPropertyValue(BBBCertonaConstants.TYPE_OF_FEED, BBBCertonaConstants.INVENTORY);

			certonaItem.setPropertyValue(BBBCertonaConstants.MODE, modeMap.get(isFullDataFeed+""));
			this.getCertonaRepository().addItem(certonaItem);

		} catch (RepositoryException e) {
			
				logError(LogMessageFormatter.formatMessage(null,
						"CertonaInventoryFeedMarshaller.updateCertonaRepository() | RepositoryException "), e);
			
		}
	}
}
