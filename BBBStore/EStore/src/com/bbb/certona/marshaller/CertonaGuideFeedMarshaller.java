package com.bbb.certona.marshaller;

import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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

import com.bbb.certona.utils.CertonaGuideFeedTools;
import com.bbb.certona.vo.CertonaGuideVO;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCertonaConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.jaxb.certona.guidefeed.Guide;
import com.bbb.framework.jaxb.certona.guidefeed.GuideFeedType;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 * The class generates guide feed in the form of a xml file
 */
public class CertonaGuideFeedMarshaller extends BBBGenericService implements ICertonaMarshaller {
	private static final String CERTONA_GUIDE_FEED_CHECK_SUM = "CertonaGuideFeed_CheckSum.txt";
	private static final String QUERY_ALL = "ALL";
	private CertonaGuideFeedTools feedTools;
	private static final String RANGE =" RANGE ";
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	private static final String SCHEMA_NAME = "guides.xsd";
	private static final String GUIDE = "guide";
	private boolean guideDetails;
	private MutableRepository certonaRepository;
	private Map<String, String> typeOfFeedMap = new HashMap<String, String>();
	private Map<String, String> sortValueMap = new HashMap<String, String>();
	private boolean repoGuidelastModDate;
	private Timestamp guideLastModDate = null;
	private Map<String, String> modeMap = new HashMap<String, String>();
	private String guideFeedFilePath;
	private String catFeedFileName;
	private int guideBatchSize;
	private int fileWriteBatchSize;
	private String filePattern;
	
	/**
	 * @return the fileWriteBatchSize
	 */
	public int getFileWriteBatchSize() {
		return this.fileWriteBatchSize;
	}

	/**
	 * @param fileWriteBatchSize
	 *          the fileWriteBatchSize to set
	 */
	public void setFileWriteBatchSize(final int fileWriteBatchSize) {
		this.fileWriteBatchSize = fileWriteBatchSize;
	}

	/**
	 * @return the feedTools
	 */
	public CertonaGuideFeedTools getFeedTools() {
		return this.feedTools;
	}

	/**
	 * @param feedTools
	 *          the feedTools to set
	 */
	public void setFeedTools(final CertonaGuideFeedTools feedTools) {
		this.feedTools = feedTools;
	}

	/**
	 * @return the catFeedFileName
	 */
	public String getCatFeedFileName() {
		return this.catFeedFileName;
	}

	/**
	 * @param catFeedFileName
	 *          the catFeedFileName to set
	 */
	public void setCatFeedFileName(final String catFeedFileName) {
		this.catFeedFileName = catFeedFileName;
	}


	/**
	 * @return the typeOfFeedMap
	 */
	public Map<String, String> getTypeOfFeedMap() {
		return this.typeOfFeedMap;
	}

	/**
	 * @param typeOfFeedMap
	 *          the typeOfFeedMap to set
	 */
	public void setTypeOfFeedMap(final Map<String, String> typeOfFeedMap) {
		this.typeOfFeedMap = typeOfFeedMap;
	}

	/**
	 * @return the certonaRepository
	 */
	public MutableRepository getCertonaRepository() {
		return this.certonaRepository;
	}

	/**
	 * @param certonaRepository
	 *          the certonaRepository to set
	 */
	public void setCertonaRepository(final MutableRepository certonaRepository) {
		this.certonaRepository = certonaRepository;
	}

	/**
	 * @return the sortValueMap
	 */
	public Map<String, String> getSortValueMap() {
		return this.sortValueMap;
	}

	/**
	 * @param sortValueMap
	 *          the sortValueMap to set
	 */
	public void setSortValueMap(final Map<String, String> sortValueMap) {
		this.sortValueMap = sortValueMap;
	}



	/**
	 * @return the modeMap
	 */
	public Map<String, String> getModeMap() {
		return this.modeMap;
	}

	/**
	 * @param modeMap
	 *          the modeMap to set
	 */
	public void setModeMap(final Map<String, String> modeMap) {
		this.modeMap = modeMap;
	}

	/**
	 * @return the filePattern
	 */
	public String getFilePattern() {
		return this.filePattern;
	}

	/**
	 * @param filePattern
	 *          the filePattern to set
	 */
	public void setFilePattern(final String filePattern) {
		this.filePattern = filePattern;
	}


	/**
	 * This method calls the respective marshal methods for guide. 
	 */
	@Override
	public void marshall(final boolean isFullDataFeed, final Timestamp schedulerStartDate) {
			this.logDebug("CertonaGuideFeedMarshaller Method : marshal");
			this.logDebug("Feeds to be run Category:");
			this.marshalGuide(isFullDataFeed, schedulerStartDate);

	}

	/**
	 * The marshal creates marshaled xml file for guide feed. the xsd
	 * corresponding to guide feed is guides.xsd
	 *
	 * @param isFullDataFeed
	 */
	public void marshalGuide(final boolean isFullDataFeed, final Timestamp schedulerStartDate) {
		int totalCount=0;
		int count=0;
		int i=0;
		String rqlQuery=null;
		List<CertonaGuideVO> guideVOList=null;
		boolean status = true;
		
		this.logDebug("CertonaGuideFeedMarshaller Method : marshallGuide ");
		this.logDebug("is full feed required " + isFullDataFeed);
		
		try {
			rqlQuery=QUERY_ALL;
			if (this.isRepoGuidelastModDate() && !isFullDataFeed) {
				
					this.logDebug("Get last modified date from repository");
				
				this.guideLastModDate = this.getLastModifiedDate(GUIDE);
				if(this.guideLastModDate!=null){
					final String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz").format(this.guideLastModDate);
					rqlQuery=BBBCertonaConstants.LAST_MODIFIED_DATE_CERTONA_PROPERTY_NAME+" > datetime(\""+date+"\")";
				}
				
					this.logDebug("last modified date for guide: " + this.getGuideLastModDate());
				
			}
			String rqlQueryRange=rqlQuery +" ORDER BY id "+ RANGE+ totalCount + "+" + this.getGuideBatchSize();
			guideVOList = this.getFeedTools().getGuideDetails(isFullDataFeed,
					this.getGuideLastModDate(),rqlQueryRange);
			if((guideVOList==null) || guideVOList.isEmpty()){
			return;
			}
			totalCount=guideVOList.size();
			count=totalCount;
			status = this.populateGuideFeed(isFullDataFeed, guideVOList, i);

			while(count==this.getGuideBatchSize()){
				guideVOList=null;
				rqlQueryRange=rqlQuery +" ORDER BY id "+ RANGE+ totalCount + "+" + this.getGuideBatchSize();
				guideVOList = this.getFeedTools().getGuideDetails(isFullDataFeed,
						this.getGuideLastModDate(),rqlQueryRange);
				if((guideVOList==null) || guideVOList.isEmpty()){
					return;
				}
				i++;
				status = this.populateGuideFeed(isFullDataFeed, guideVOList, i);
				count=guideVOList.size();
				totalCount = totalCount +count;
			}
			final List<String> configPath = this.getFeedTools().getCatalogTools()
					.getContentCatalogConfigration(this.getGuideFeedFilePath());

			if ((configPath != null) && !configPath.isEmpty()) {
					this.logDebug("Guides processed : "+String.valueOf(totalCount));
					BBBUtility.writeFile(configPath.get(0)
						, CERTONA_GUIDE_FEED_CHECK_SUM, "Guides Processed :" +String.valueOf(totalCount));

			}
		} catch (final BBBBusinessException e1) {
			
				this.logError(LogMessageFormatter.formatMessage(null,
						"CertonaGuideFeedMarshaller.marshallguide() | BBBBusinessException "), e1);
				status = false;
		} catch (final BBBSystemException e) {
				this.logError(LogMessageFormatter.formatMessage(null,
						"CertonaGuideFeedMarshaller.marshallguide() | BBBSystemException "), e);
				status = false;
		} finally {
			this.updateCertonaRepository(schedulerStartDate, isFullDataFeed,GUIDE, status);
		}
	}

	public boolean isGuideDetails() {
		return guideDetails;
	}

	/**
	 * @param guideDetails
	 *          the guideDetails to set
	 */
	public void setGuideDetails(boolean guideDetails) {
		this.guideDetails = guideDetails;
	}
	/**
	 * @return the repoGuidelastModDate
	 */
	public boolean isRepoGuidelastModDate() {
		return repoGuidelastModDate;
	}

	/**
	 * @param repoGuidelastModDate
	 *          the repoGuidelastModDate to set
	 */
	public void setRepoGuidelastModDate(boolean repoGuidelastModDate) {
		this.repoGuidelastModDate = repoGuidelastModDate;
	}
	/**
	 * @return the guideFeedFilePath
	 */
	public String getGuideFeedFilePath() {
		return guideFeedFilePath;
	}

	/**
	 * @param guideFeedFilePath
	 *          the guideFeedFilePath to set
	 */
	public void setGuideFeedFilePath(String guideFeedFilePath) {
		this.guideFeedFilePath = guideFeedFilePath;
	}
	/**
	 * @return the guideBatchSize
	 */
	public int getGuideBatchSize() {
		return guideBatchSize;
	}

	/**
	 * @param guideBatchSize
	 *          the guideBatchSize to set
	 */
	public void setGuideBatchSize(int guideBatchSize) {
		this.guideBatchSize = guideBatchSize;
	}
	/**
	 * @return the guideLastModDate
	 */
	public Timestamp getGuideLastModDate() {
		return guideLastModDate;
	}

	/**
	 * @param guideLastModDate
	 *          the guideLastModDate to set
	 */
	public void setGuideLastModDate(Timestamp guideLastModDate) {
		this.guideLastModDate = guideLastModDate;
	}

	/**
	 * The method creates a JAXB marshaled xml file after data is populated in
	 * JAXB classes for guide feed
	 *
	 * @param guideFeed
	 * @param guideObject
	 * @param indexOfFile
	 *          no of the file that is created for the feed
	 * @return true if marshaling is successful else return false
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public boolean getGuideMarshalFile(final com.bbb.framework.jaxb.certona.guidefeed.GuideFeedType guideFeed,
			final com.bbb.framework.jaxb.certona.guidefeed.ObjectFactory guideObject, final int indexOfFile)
					throws BBBBusinessException, BBBSystemException {
		
			this.logDebug("CertonaCatalogFeedMarshaller Method : getCatMarshalledFile ");
		
		boolean status = true;
		try {
			final JAXBElement<GuideFeedType> categoryRoot = guideObject.createGuideFeed(guideFeed);
			final StringWriter stringWriter = new StringWriter();
			stringWriter.append(XML_HEADER);
			final JAXBContext context = JAXBContext.newInstance(GuideFeedType.class);
			final Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, SCHEMA_NAME);
			marshaller.marshal(categoryRoot, stringWriter);
			final String categoryXml = stringWriter.getBuffer().toString();
			final List<String> configPath = this.getFeedTools().getCatalogTools()
					.getContentCatalogConfigration(this.getGuideFeedFilePath());
			final List<String> configFile = this.getFeedTools().getCatalogTools()
					.getContentCatalogConfigration(this.getCatFeedFileName());
			if ((configPath != null) && !configPath.isEmpty() && (configFile != null) && !configFile.isEmpty()) {
				final String fileName = this.getFileName(configFile.get(0), indexOfFile);
				this.logDebug(indexOfFile + "the file created for guide:" + configPath.get(0) + fileName);
				BBBUtility.writeFile(configPath.get(0)
						, fileName, categoryXml);
			}
		} catch (final JAXBException e) {
				this.logError(LogMessageFormatter.formatMessage(null,
						"CertonaGuideFeedMarshaller.getGuideMarshalFile() | BBBBusinessException "), e);
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
	private String getFileName(final String fileSuffix, final int indexOfFile) throws BBBSystemException, BBBBusinessException {
		String fileName = indexOfFile + fileSuffix;

		final List<String> filePattern = this.getFeedTools().getCatalogTools().getContentCatalogConfigration(
				this.getFilePattern());
		if ((filePattern != null) && !filePattern.isEmpty()) {
			final String pattern = filePattern.get(0);
			final Date currentDate = new Date();
			final SimpleDateFormat format = new SimpleDateFormat(pattern);
			final String dateInFormat = format.format(currentDate);
			fileName = dateInFormat + "-" + fileName;

		}
		return fileName;
	}

	/**
	 * The method populates the JAXB classes for category feed with data from
	 * repository
	 *
	 * @param isFullDataFeed
	 * @param guideVOList
	 * @param indexOfFile
	 *          file no for which data is populated
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public boolean populateGuideFeed(final boolean isFullDataFeed, final List<CertonaGuideVO> guideVOList, final int indexOfFile)
			throws BBBBusinessException, BBBSystemException {
		
			this.logDebug("CertonaGuideFeedMarshaller Method : populateGuideFeed");
			this.logDebug("no of Guides to be sent in feed in" + indexOfFile + " th file is:" + guideVOList.size());
		

		final com.bbb.framework.jaxb.certona.guidefeed.ObjectFactory guideObject = new com.bbb.framework.jaxb.certona.guidefeed.ObjectFactory() ;
		final com.bbb.framework.jaxb.certona.guidefeed.GuideFeedType guideRoot = guideObject
				.createGuideFeedType();
		final List<Guide> jaxbCatDetailList =guideRoot.getGuideDetail();
		Guide jaxbGuide=null;
		if (!BBBUtility.isListEmpty(guideVOList)) {
			for (int i = 0; i < guideVOList.size(); i++) {

				jaxbGuide= new Guide();
				final CertonaGuideVO
				guideVO = guideVOList.get(i);
				jaxbGuide.setGuideId(guideVO.getGuideId());;
				this.logDebug(i + "the guide id that has modified since last feed :" + guideVO.getGuideId());
				jaxbGuide.setGuideURL(guideVO.getGuideUrl());
				String productIds=BBBCoreConstants.BLANK;
				for(String prodId:guideVO.getProductIds()){
					if(BBBUtility.isEmpty(productIds))
						productIds=productIds+prodId;
					else
						productIds=productIds+","+prodId;
					
				}
				jaxbGuide.setProducts(productIds);
				jaxbGuide.setSiteId(guideVO.getSiteId());
				jaxbCatDetailList.add(jaxbGuide);
			
			}
		}
			this.logDebug(" all guides properties set.Marshall of guide feed file start");
		
		return this.getGuideMarshalFile(guideRoot, guideObject, indexOfFile);
	}

	/**
	 *
	 * @param viewValue
	 * @return
	 * @throws BBBSystemException
	 */
	public Timestamp getLastModifiedDate(final String viewValue) throws BBBSystemException {
		
			this.logDebug("CertonaCatalogFeedMarshaller Method : getLastModifiedDate");
		
		try {
			final RepositoryView view = this.getCertonaRepository().getView(BBBCertonaConstants.FEED);
			Timestamp modifiedDate = null;
			final QueryBuilder builder = view.getQueryBuilder();
			final Query[] queries = new Query[2];
			final QueryExpression typeOfFeed = builder.createPropertyQueryExpression(BBBCertonaConstants.TYPE_OF_FEED);
			final QueryExpression catalog = builder.createConstantQueryExpression(this.getTypeOfFeedMap().get(viewValue));
			queries[0] = builder.createComparisonQuery(typeOfFeed, catalog, QueryBuilder.EQUALS);
			final QueryExpression status = builder.createPropertyQueryExpression(BBBCertonaConstants.STATUS);
			final QueryExpression statusValue = builder.createConstantQueryExpression(true);
			queries[1] = builder.createComparisonQuery(status, statusValue, QueryBuilder.EQUALS);
			final Query feedTypeQuery = builder.createAndQuery(queries);
			final SortDirectives sortDirectives = new SortDirectives();
			sortDirectives
			.addDirective(new SortDirective(this.getSortValueMap().get(viewValue), SortDirective.DIR_DESCENDING));
			final RepositoryItem[] certonaItems = view.executeQuery(feedTypeQuery, new QueryOptions(0, 1, sortDirectives,
					null));
			if ((certonaItems != null) && (certonaItems.length > 0)) {
				modifiedDate = (Timestamp) certonaItems[0].getPropertyValue(this.getSortValueMap().get(viewValue));
			}
			
				this.logDebug("Last Modified Date from repository: " + modifiedDate);
			
			return modifiedDate;
		} catch (final RepositoryException e) {
			
				this.logError(LogMessageFormatter.formatMessage(null,
						"CertonaGuideFeedMarshaller.getLastModifiedDate() | RepositoryException "), e);
			
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
		}

	}

	/**
	 * The method updates the guide repository with the details of the scheduler
	 * run
	 *
	 * @param schedulerStartDate
	 * @param isFullDataFeed
	 * @param typeOfFeed
	 * @param status
	 */
	public void updateCertonaRepository(final Timestamp schedulerStartDate, final boolean isFullDataFeed, final String typeOfFeed,
			final boolean status) {
		
			this.logDebug("CertonaGuideFeedMarshaller Method : updateCertonaRepository");
			this.logDebug("scheduler Start Date :" + schedulerStartDate + " full feed?: " + isFullDataFeed + " typeOfFeed: "
					+ typeOfFeed + " status:" + status);
	
		final Date schedulerEndDate = new Date();
		try {
			final MutableRepositoryItem certonaItem = this.getCertonaRepository().createItem(BBBCertonaConstants.FEED);

			certonaItem.setPropertyValue(BBBCertonaConstants.LAST_MODIFIED_DATE, schedulerStartDate);
			certonaItem.setPropertyValue(BBBCertonaConstants.SCHEDULER_COMPLETION_DATE, schedulerEndDate);
			certonaItem.setPropertyValue(BBBCertonaConstants.SCHEDULER_START_DATE, schedulerStartDate);
			certonaItem.setPropertyValue(BBBCertonaConstants.STATUS, status);
			certonaItem.setPropertyValue(BBBCertonaConstants.TYPE_OF_FEED, this.getTypeOfFeedMap().get(typeOfFeed));

			certonaItem.setPropertyValue(BBBCertonaConstants.MODE, this.getModeMap().get(String.valueOf(isFullDataFeed)));
			this.getCertonaRepository().addItem(certonaItem);

		} catch (final RepositoryException e) {
			
				this.logError(LogMessageFormatter.formatMessage(null,
						"CertonaGuideFeedMarshaller.updateCertonaRepository() | RepositoryException "), e);
			
		}
	}
}
