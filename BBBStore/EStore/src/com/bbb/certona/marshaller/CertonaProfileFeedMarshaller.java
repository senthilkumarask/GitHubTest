/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  CertonaProfileFeedMarshaller.java
 *
 *  DESCRIPTION: Fetches the unprocessed records from repository and marshalls them to feed.
 * 
 *  @author rsain4
 *  
 *  HISTORY:
 *  
 *  05/02/12 Initial version
 *
 */
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
import javax.xml.datatype.XMLGregorianCalendar;

import com.bbb.common.BBBGenericService;
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

import com.bbb.certona.utils.CertonaProfileFeedTools;
import com.bbb.certona.vo.CertonaGiftRegistryVO;
import com.bbb.certona.vo.CertonaProfileSiteVO;
import com.bbb.certona.vo.CertonaProfileVO;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCertonaConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.jaxb.certona.profilefeed.GiftRegistryDetails;
import com.bbb.framework.jaxb.certona.profilefeed.ObjectFactory;
import com.bbb.framework.jaxb.certona.profilefeed.UserDetail;
import com.bbb.framework.jaxb.certona.profilefeed.UserSiteAssocType;
import com.bbb.framework.jaxb.certona.profilefeed.UserType;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

public class CertonaProfileFeedMarshaller extends BBBGenericService implements
		ICertonaMarshaller {

	private static final String CERTONA_PROFILE_FEED_CHECK_SUM = "CertonaProfileFeed_CheckSum.txt";
	private static final long serialVersionUID = 1L;
	private static final String QUERY_ALL = "ALL";
	private static final String RANGE =" RANGE ";
	private MutableRepository mCertonaRepository;
	private CertonaProfileFeedTools feedTools;
	private String mTypeOfFeed;
	private boolean bProfileLastModDateFrmRepo;
	private Timestamp mProfileLastModDate = null;
	private Boolean bFeedStatus = false;
	private Timestamp jobStartDate;
	private String datePattern;
	private BBBCatalogTools mCatalogTools;

	
	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

	/**
	 * @return the datePattern
	 */
	public String getDatePattern() {
		return datePattern;
	}

	/**
	 * @param datePattern the datePattern to set
	 */
	public void setDatePattern(final String datePattern) {
		this.datePattern = datePattern;
	}

	/**
	 * @return the jobStartDate
	 */
	public Timestamp getJobStartDate() {
		return jobStartDate;
	}

	/**
	 * @param jobStartDate the jobStartDate to set
	 */
	public void setJobStartDate(final Timestamp jobStartDate) {
		this.jobStartDate = jobStartDate;
	}

	/**
	 * @return the bFeedStatus
	 */
	public Boolean getFeedStatus() {
		return bFeedStatus;
	}

	/**
	 * @param bFeedStatus the bFeedStatus to set
	 */
	public void setFeedStatus(final Boolean pFeedStatus) {
		this.bFeedStatus = pFeedStatus;
	}

	/**
	 * @return the profileLastModDateFrmRepo
	 */
	public boolean isProfileLastModDateFrmRepo() {
		return bProfileLastModDateFrmRepo;
	}

	/**
	 * @param profileLastModDateFrmRepo the profileLastModDateFrmRepo to set
	 */
	public void setProfileLastModDateFrmRepo(final boolean pProfileLastModDateFrmRepo) {
		this.bProfileLastModDateFrmRepo = pProfileLastModDateFrmRepo;
	}

	/**
	 * @return the profileLastModDate
	 */
	public Timestamp getProfileLastModDate() {
		return mProfileLastModDate;
	}

	/**
	 * @param mProfileLastModDate the mProfileLastModDate to set
	 */
	public void setProfileLastModDate(final Timestamp pProfileLastModDate) {
		this.mProfileLastModDate = pProfileLastModDate;
	}

	/**
	 * Holds values for full or incremental feed
	 */
	private Map<String, String> modeMap = new HashMap<String, String>();

	/**
	 * Path at which feed file needs to be generated
	 */
	private String mFilePath;

	/**
	 * Name of the feed file
	 */
	private String mFileName;

	/**
	 * Extension of the feed file
	 */
	private String mFileExtention;

	/**
	 * Represents the number of records each feed file would contain. This is
	 * used to limit the file size if records are huge.
	 */
	private String mBatchSize;

	/**
	 * @return the feedTools
	 */
	public CertonaProfileFeedTools getFeedTools() {
		return feedTools;
	}

	/**
	 * @param feedTools the feedTools to set
	 */
	public void setFeedTools(final CertonaProfileFeedTools feedTools) {
		this.feedTools = feedTools;
	}

	/**
	 * @return the mCertonaRepository
	 */
	public MutableRepository getCertonaRepository() {
		return mCertonaRepository;
	}

	/**
	 * @param pCertonaRepository the certonaRepository to set
	 */
	public void setCertonaRepository(final MutableRepository pCertonaRepository) {
		this.mCertonaRepository = pCertonaRepository;
	}

	/**
	 * @return the mTypeOfFeed
	 */
	public String getTypeOfFeed() {
		return mTypeOfFeed;
	}

	/**
	 * @param pTypeOfFeed the mTypeOfFeed to set
	 */
	public void setTypeOfFeed(final String pTypeOfFeed) {
		this.mTypeOfFeed = pTypeOfFeed;
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
	public void setModeMap(final Map<String, String> modeMap) {
		this.modeMap = modeMap;
	}

	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return mFilePath;
	}

	/**
	 * @param filePath the filePath to set
	 */
	public void setFilePath(final String pFilePath) {
		this.mFilePath = pFilePath;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return mFileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(final String pFileName) {
		this.mFileName = pFileName;
	}

	/**
	 * @return the fileExtention
	 */
	public String getFileExtention() {
		return mFileExtention;
	}

	/**
	 * @param fileExtention the fileExtention to set
	 */
	public void setFileExtention(final String pFileExtention) {
		this.mFileExtention = pFileExtention;
	}

	/**
	 * @return the batchSize
	 */
	public String getBatchSize() {
		return mBatchSize;
	}

	/**
	 * @param batchSize the batchSize to set
	 */
	public void setBatchSize(final String pBatchSize) {
		this.mBatchSize = pBatchSize;
	}

	/**
	 * Fetches the unprocessed records from repository and marshall them to
	 * feed.
	 * 
	 * @param isFullDataFeed - true if this is for complete feed
	 * @param schedulerStartDate - Specifies the date from which records would be processed
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * @throws RepositoryException
	 * 
	 */
	public void marshall(final boolean isFullDataFeed, final Timestamp schedulerStartDate) {
		int totalCount=0;
		int count=0;
		int i=0;
		String rqlQuery=null;
		List<CertonaProfileVO> profileVOList=null;
		logDebug("Entering marshall of CertonaProfileFeedMarshaller");

		try {
			rqlQuery=QUERY_ALL;
			jobStartDate = schedulerStartDate;
			if (isProfileLastModDateFrmRepo() && !isFullDataFeed) {
				logDebug("Get last modified date from repository");
				this.mProfileLastModDate = this.getLastModifiedDate(getTypeOfFeed());
				if(this.mProfileLastModDate!=null){
					String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz").format(this.mProfileLastModDate);
					rqlQuery=BBBCertonaConstants.LAST_MODIFIED_DATE_CERTONA_PROPERTY_NAME+" > datetime(\""+date+"\")";
					}
				logDebug("last modified date for profile: "+this.getProfileLastModDate());
			}
			String rqlQueryRange=rqlQuery +" ORDER BY ID " + RANGE+ totalCount + "+" + this.getBatchSizeFrmConfig();
			profileVOList = feedTools.getProfileDetails(isFullDataFeed, getProfileLastModDate(),rqlQueryRange);
			if(profileVOList==null || profileVOList.isEmpty()){
				return;
				}
				totalCount=profileVOList.size();
				count=totalCount;
				bFeedStatus = this.populateUserFeed(profileVOList, i);
				

				while(count==this.getBatchSizeFrmConfig()){
					profileVOList=null;
					rqlQueryRange=rqlQuery  +" ORDER BY ID "+ RANGE+ totalCount + "+" + this.getBatchSizeFrmConfig();
					profileVOList = feedTools.getProfileDetails(isFullDataFeed, getProfileLastModDate(),rqlQueryRange);
					if(profileVOList==null || profileVOList.isEmpty()){
						return;
					}
					i++;
					bFeedStatus = this.populateUserFeed(profileVOList, i);
					count=profileVOList.size();
					totalCount = totalCount +count;
				}
							
				if (getFilePathFrmConfig() != null && !getFilePathFrmConfig().isEmpty()) {
				
					logDebug("Profiles processed : "+String.valueOf(totalCount));
					
					BBBUtility.writeFile(getFilePathFrmConfig(), CERTONA_PROFILE_FEED_CHECK_SUM, "Profiles Processed :" +String.valueOf(totalCount));

				}
		} catch (BBBSystemException se) {
			logError(LogMessageFormatter.formatMessage(null,"CertonaProfileFeedMarshaller.marshall() | BBBSystemException"), se);
		} catch (BBBBusinessException be) {
			logError(LogMessageFormatter.formatMessage(null,"CertonaProfileFeedMarshaller.marshall() | BBBBusinessException"), be);
		}finally{
			try {
				// Update Certona repository with schedulerStartDate 
				updateCertonaRepository(getTypeOfFeed(), schedulerStartDate, isFullDataFeed, this.getFeedStatus());
			} catch (BBBSystemException bbbse) {
				logError(LogMessageFormatter.formatMessage(null,"CertonaProfileFeedMarshaller.marshall() | BBBSystemException"), bbbse);
			}
		}
		logDebug("Existing marshall of CertonaProfileFeedMarshaller");
	}

	private boolean populateUserFeed(final List<CertonaProfileVO> profileVOList, final int indexOfFile)
			throws BBBSystemException, BBBBusinessException {
		logDebug("Entering populateUserFeed of CertonaProfileFeedMarshaller");
		final ObjectFactory profileObjectFactory = new ObjectFactory();
		final UserType userFeed = profileObjectFactory.createUserType();
		final List<UserDetail> userDetailList = new ArrayList<UserDetail>();

		if (null != profileVOList && !profileVOList.isEmpty()) {
			for (CertonaProfileVO certonaProfileVO : profileVOList) {
				final UserDetail userDetail = profileObjectFactory.createUserDetail();
				userDetail.setUserId( certonaProfileVO.getUserId() == null ? "" : certonaProfileVO.getUserId());
				userDetail.setUserFirstName(certonaProfileVO.getUserFirstName() == null ? "" : certonaProfileVO.getUserFirstName());
				userDetail.setUserLastName(certonaProfileVO.getUserLastName() == null ? "" : certonaProfileVO.getUserLastName());
				userDetail.setUserType(certonaProfileVO.getUserType() == null ? "" : certonaProfileVO.getUserType());
				userDetail.setLocale(certonaProfileVO.getLocale() == null ? "" : certonaProfileVO.getLocale());
				userDetail.setLastActivity(getXMLDate(certonaProfileVO.getLastActivity()));
				userDetail.setRegistrationDate(getXMLDate(certonaProfileVO.getRegistrationDate()));
				userDetail.setEmailAddress(certonaProfileVO.getEmailAddress() == null ? "" : certonaProfileVO.getEmailAddress());
				userDetail.setMobileNumber(certonaProfileVO.getMobileNumber() == null ? "" : certonaProfileVO.getMobileNumber());
				userDetail.setPhoneNumber(certonaProfileVO.getPhoneNumber() == null ? "" : certonaProfileVO.getPhoneNumber());
				userDetail.setGender(certonaProfileVO.getGender() == null ? "" : certonaProfileVO.getGender());
				userDetail.setDateOfBirth(getXMLDate(certonaProfileVO.getDateOfBirth()));
				
				final GiftRegistryDetails giftRegistryDetails = new GiftRegistryDetails();
				final List<CertonaGiftRegistryVO> certonaGiftRegistryVOList = certonaProfileVO.getGiftRegistryVOList();
				if(certonaGiftRegistryVOList !=null && !certonaGiftRegistryVOList.isEmpty()){
					for (CertonaGiftRegistryVO certonaGiftRegistryVO : certonaGiftRegistryVOList) {
						giftRegistryDetails.setRegistryId(certonaGiftRegistryVO.getRegistryId() == null ? "" : certonaGiftRegistryVO.getRegistryId());
						giftRegistryDetails.setSiteId(certonaGiftRegistryVO.getSiteId() == null ? "" : certonaGiftRegistryVO.getSiteId());
						giftRegistryDetails.setEventType(certonaGiftRegistryVO.getEventType() == null ? "" : certonaGiftRegistryVO.getEventType());
						giftRegistryDetails.setEventDate(getXMLDate(certonaGiftRegistryVO.getEventDate()));
					}
				}
				userDetail.getGiftRegistry().add(giftRegistryDetails);
				userDetail.setIsFacebookIntegrated(certonaProfileVO.isFacebookIntegrated());

				final UserSiteAssocType userSiteAssocType = new UserSiteAssocType();
				final List<CertonaProfileSiteVO> certonaProfileSiteVOList = certonaProfileVO.getProfileSiteVOList();
				if(certonaProfileSiteVOList != null && !certonaProfileSiteVOList.isEmpty()){
					for (CertonaProfileSiteVO certonaProfileSiteVO : certonaProfileSiteVOList) {
						userSiteAssocType.setSiteId(certonaProfileSiteVO.getSiteId() == null ? "" : certonaProfileSiteVO.getSiteId());
						userSiteAssocType.setMemberId(certonaProfileSiteVO.getMemberId() == null ? "" : certonaProfileSiteVO.getMemberId());
						userSiteAssocType.setStoreId(certonaProfileSiteVO.getStoreId() == null ? "" : certonaProfileSiteVO.getStoreId());
					}
				}
				userDetail.getUserSiteAssoc().add(userSiteAssocType);
				userDetailList.add(userDetail);
			}
			userFeed.getUserAttributes().addAll(userDetailList);
		}
		logDebug("Existing populateUser of CertonaProfileFeedMarshaller");
		return this.getMarshalFile(profileObjectFactory, userFeed, indexOfFile);

	}

	private boolean getMarshalFile(final ObjectFactory profileObject, final UserType userFeed, final int indexOfFile) throws BBBSystemException, BBBBusinessException  {
		try {
			final JAXBElement<UserType> jaxbElement = profileObject.createUser(userFeed);
			final StringWriter stringWriter = new StringWriter();
			JAXBContext context;
			context = JAXBContext.newInstance(UserType.class);
			final Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(jaxbElement, stringWriter);
			final String profileXml = stringWriter.getBuffer().toString();
			final String fileName = getFullFileName(indexOfFile);
			BBBUtility.writeFile(getFilePathFrmConfig(), fileName, profileXml);

		} catch (JAXBException jaxbe) {
			logError(LogMessageFormatter.formatMessage(null,"CertonaProfileFeedMarshaller.getMarshalFile() | JAXBException"), jaxbe);
			bFeedStatus = false;
		} catch (BBBSystemException bbbe) {
			logError(LogMessageFormatter.formatMessage(null,"CertonaProfileFeedMarshaller.populateUserFeed() | BBBSystemException"), bbbe);
		}
		return true;
	}

	/**
	 * Returns the date when scheduler successfully created feed. If this is
	 * first time scheduler is running then this date would return null.
	 * 
	 * @return lastModifiedDate Last time when scheduler successfully run and created feed
	 * 
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private Timestamp getLastModifiedDate(final String viewName)
			throws BBBSystemException, BBBBusinessException {
		logDebug("Entering getLastModifiedDate of CertonaProfileFeedMarshaller");

		if (getCertonaRepository() == null) {
			logError("Certona Repository not configured correctly");
			throw new BBBSystemException(BBBCatalogErrorCodes.REPOSITORY_NOT_CONFIGURED_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.REPOSITORY_NOT_CONFIGURED_REPOSITORY_EXCEPTION);
		}
		Timestamp modifiedDate = null;
		try {

			final RepositoryView view = this.getCertonaRepository().getView(BBBCertonaConstants.FEED);
			final QueryBuilder builder = view.getQueryBuilder();
			final Query[] queries = new Query[2];
			final QueryExpression typeOfFeed = builder.createPropertyQueryExpression(BBBCertonaConstants.TYPE_OF_FEED);
			final QueryExpression profile = builder.createConstantQueryExpression(viewName);
			queries[0] = builder.createComparisonQuery(typeOfFeed, profile,	QueryBuilder.EQUALS);
			final QueryExpression status = builder.createPropertyQueryExpression(BBBCertonaConstants.STATUS);
			final QueryExpression statusValue = builder.createConstantQueryExpression(true);
			queries[1] = builder.createComparisonQuery(status, statusValue,	QueryBuilder.EQUALS);
			final Query feedTypeQuery = builder.createAndQuery(queries);
			final SortDirectives sortDirectives = new SortDirectives();
			sortDirectives.addDirective(new SortDirective(BBBCertonaConstants.LAST_MODIFIED_DATE, SortDirective.DIR_DESCENDING));
			final RepositoryItem[] profileItems = view.executeQuery(feedTypeQuery, new QueryOptions(0, 1, sortDirectives, null));
			if (profileItems != null && profileItems.length > 0) {
				modifiedDate = (Timestamp) profileItems[0].getPropertyValue(BBBCertonaConstants.LAST_MODIFIED_DATE);
			}
		} catch (RepositoryException re) {
			logError(LogMessageFormatter.formatMessage(null, "CertonaProfileFeedMarshaller.getLastModifiedDate() | RepositoryException"), re);
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_FETCH_DATA_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_FETCH_DATA_REPOSITORY_EXCEPTION, re);
		}
		logDebug("Existing getLastModifiedDate of CertonaProfileFeedMarshaller");
		return modifiedDate;

	}

	/**
	 * Updates the cerona repository with updated date of last successfull run
	 * 
	 * @param typeOfFeed
	 * @param schedulerStartDate
	 * @param isFullDataFeed
	 * @throws BBBSystemException
	 */

	private void updateCertonaRepository(final String typeOfFeed, final Timestamp schedulerStartDate, final boolean isFullDataFeed,
			final boolean status) throws BBBSystemException {
		logDebug("Entering updateCertonaRepository of CertonaProfileFeedMarshaller");
		final Timestamp schedulerEndDate = new Timestamp(new java.util.Date().getTime());
		final MutableRepository certonaRepository = (MutableRepository) getCertonaRepository();
		try {
			final MutableRepositoryItem certonaItem = this.getCertonaRepository().createItem(BBBCertonaConstants.FEED);
			certonaItem.setPropertyValue(BBBCertonaConstants.LAST_MODIFIED_DATE, schedulerStartDate);
			certonaItem.setPropertyValue(BBBCertonaConstants.SCHEDULER_COMPLETION_DATE, schedulerEndDate);
			certonaItem.setPropertyValue(BBBCertonaConstants.SCHEDULER_START_DATE, schedulerStartDate);
			certonaItem.setPropertyValue(BBBCertonaConstants.STATUS, status);
			certonaItem.setPropertyValue(BBBCertonaConstants.TYPE_OF_FEED, typeOfFeed);
			certonaItem.setPropertyValue(BBBCertonaConstants.MODE, this.getModeMap().get(Boolean.valueOf(isFullDataFeed).toString()));
			certonaRepository.addItem(certonaItem);
		} catch (RepositoryException re) {
			logError(LogMessageFormatter.formatMessage(null, "CertonaProfileFeedMarshaller.updateCertonaRepository() | RepositoryException"), re);
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_UPDATE_REPOSITORY_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_UPDATE_REPOSITORY_REPOSITORY_EXCEPTION,	re);
		}
		logDebug("Existing updateCertonaRepository of CertonaProfileFeedMarshaller");
		logDebug("Certona Repository updated at :" + schedulerEndDate);
	}
	
	/**
	 * Returns the full file name 
	 *  
	 * @param counter
	 * @param schedularStartDate
	 * @return fullFileName
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public String getFullFileName(final int counter) throws BBBSystemException, BBBBusinessException{
		
		logDebug("Entering getFullFileName of CertonaProfileFeedMarshaller");
		String fullFileName = null;
		final SimpleDateFormat format = new SimpleDateFormat(getDatePatternFrmConfig());
		final String dateInString = format.format(new Date(jobStartDate.getTime()));
	
		if(getFileNameFrmConfig() != null && getFilePathFrmConfig() != null && getFileExtnFrmConfig() !=null){
			fullFileName = getFileNameFrmConfig() + "_" + dateInString + "_" + (counter + 1) + getFileExtnFrmConfig();
			logDebug("Full Feed file name with location is : "+ fullFileName);
		}
		
		logDebug("Exiting getFullFileName of CertonaProfileFeedMarshaller");
		return fullFileName;
	}

	/**
	 * Returns the file path from configure repository 
	 * 
	 * @return filePathFromCfg - File path
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public String getFilePathFrmConfig() throws BBBSystemException, BBBBusinessException{
		
		logDebug("Entering getFilePathFrmConfig of CertonaProfileFeedMarshaller");
		
		String filePathFrmCfg = null;
		final List<String> filePath = this.getCatalogTools().getAllValuesForKey(BBBCertonaConstants.FEED_CONFIG_TYPE, this.getFilePath());		
		
		if(filePath != null && !filePath.isEmpty()){
			
			filePathFrmCfg = filePath.get(0);			
			
			logDebug("Feed file path from Config is : " + filePathFrmCfg);
		}
		
		logDebug("Exiting getFilePathFrmConfig of CertonaProfileFeedMarshaller");
		return filePathFrmCfg;
	}
	
	/**
	 * Returns the file name from configure repository
	 *  
	 * @param counter
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private String getFileNameFrmConfig() throws BBBSystemException, BBBBusinessException{
		
		logDebug("Entering getFileNameFrmConfig of CertonaProfileFeedMarshaller");
		
		String fileNameFrmCfg = null;
		final List<String> fileName = this.getCatalogTools().getAllValuesForKey(BBBCertonaConstants.FEED_CONFIG_TYPE, this.getFileName());
		
		if(fileName != null && !fileName.isEmpty()){
			fileNameFrmCfg = fileName.get(0);
			logDebug("Feed file name is : "+ fileNameFrmCfg);
		}
		
		logDebug("Exiting getFileNameFrmConfigRepo of CertonaProfileFeedMarshaller");
		return fileNameFrmCfg;
	}

	/**
	 * Returns the file Extn from configure repository
	 *  
	 * @return fileExtnFrmCfg
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private String getFileExtnFrmConfig() throws BBBSystemException, BBBBusinessException{
		
		logDebug("Entering getFileExtnFrmConfig of CertonaProfileFeedMarshaller");
		
		String fileExtnFrmCfg = null;
		final List<String> fileExt = this.getCatalogTools().getAllValuesForKey(BBBCertonaConstants.FEED_CONFIG_TYPE, this.getFileExtention());
		
		if(fileExt != null && !fileExt.isEmpty()){
			fileExtnFrmCfg = "." + fileExt.get(0);
			logDebug("Feed file name is : "+ fileExtnFrmCfg);
		}
		
		logDebug("Exiting getFileExtnFrmConfig of CertonaProfileFeedMarshaller");
		return fileExtnFrmCfg;
	}

	/**
	 * Returns the file batch Size from configure repository
	 *  
	 * @return batchSizeFrmCfg
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private int getBatchSizeFrmConfig() throws BBBSystemException, BBBBusinessException{
		
		logDebug("Entering getBatchSizeFrmConfig of CertonaProfileFeedMarshaller");
		
		int batchSizeFrmCfg = 1;		
		final List<String> batchSizes = this.getCatalogTools().getAllValuesForKey(BBBCertonaConstants.FEED_CONFIG_TYPE, this.getBatchSize());
		
		if(batchSizes != null && !batchSizes.isEmpty()){
			batchSizeFrmCfg = Integer.valueOf(batchSizes.get(0));
			
			logDebug("batch size is : "+ batchSizeFrmCfg);
		}
		
		logDebug("Exiting getBatchSizeFrmConfig of CertonaProfileFeedMarshaller");
		
		return batchSizeFrmCfg;
	}
	/**
	 * Returns the file batch Size from configure repository
	 *  
	 * @return datePatternFrmCfg
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private String getDatePatternFrmConfig() throws BBBSystemException, BBBBusinessException{
		
		logDebug("Entering getDatePatternFrmConfig of CertonaProfileFeedMarshaller");
		
		String datePatternFrmCfg = null;		
		final List<String> datePattern = this.getCatalogTools().getAllValuesForKey(BBBCertonaConstants.FEED_CONFIG_TYPE, this.getDatePattern());
		
		if(datePattern != null && !datePattern.isEmpty()){
			datePatternFrmCfg = datePattern.get(0);
			
			logDebug("Date Pattern is : "+ datePatternFrmCfg);
		}
		
		logDebug("Exiting getDatePatternFrmConfig of CertonaProfileFeedMarshaller");
		
		return datePatternFrmCfg;
	}
	
	private XMLGregorianCalendar getXMLDate(final Date date) throws BBBSystemException{
		XMLGregorianCalendar xmlGregorianCalendar = null;
		if(date == null){
			final Date defaultDate = new Date();
			defaultDate.setTime(0);	
			xmlGregorianCalendar = BBBUtility.getXMLCalendar(defaultDate);
		}
		else{
			xmlGregorianCalendar = BBBUtility.getXMLCalendar(date);
		}
		return xmlGregorianCalendar;
	}


}
