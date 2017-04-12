package com.bbb.commerce.catalog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import atg.adapter.gsa.GSAItemDescriptor;
import atg.adapter.gsa.GSARepository;
import atg.repository.RepositoryException;

import com.bbb.commerce.catalog.vo.BazaarVoiceProductVO;
import com.bbb.commerce.catalog.vo.BazaarVoiceVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCmsConstants;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.jaxb.bazaarvoice.FeedType;
import com.bbb.framework.jaxb.bazaarvoice.ProductType;
import com.bbb.framework.jaxb.bazaarvoice.ReviewStatisticsType;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;


/**
 * This class reads xml given by BazaarVoice and parse it.
 * @author ajosh8
 *
 */
public class BazaarVoiceUnMarshaller extends BBBGenericService {

    private static final String AND_ROWNUM = " and ROWNUM <= ";
	private static final String SITE_ID = " SITE_ID = '";
	private Map<String,String> siteFeedConfiguration = new HashMap<String,String>();
    private Map<String, String> feedFilePath = new HashMap<String, String>();
    private Map<String, String> feedFileArchivePath = new HashMap<String, String>();
    private BazaarVoiceManager mBazaarVoiceManager;
    private BBBCatalogTools catalogTools;
	//Path of the directory where files will be archived
	private String archiveFilePathKey;
	//Path of the new file to be uploaded
	private String filePathKey;
	//File name of the new file to be uploaded

	private int batchNumber;
	private String query;
	

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public int getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(int batchNumber) {
		this.batchNumber = batchNumber;
	}

	/**
	 * @return the bazaarVoiceManager
	 */
	public final BazaarVoiceManager getBazaarVoiceManager() {
		return this.mBazaarVoiceManager;
	}

	/**
	 * @param pBazaarVoiceManager the bazaarVoiceManager to set
	 */
	public final void setBazaarVoiceManager(final BazaarVoiceManager pBazaarVoiceManager) {
		this.mBazaarVoiceManager = pBazaarVoiceManager;
	}

	/**
	 * @return the catalogTools
	 */
	public final BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}
	/**
	 * @param catalogTools the catalogTools to set
	 */
	public final void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * @return the archiveFilePathKey
	 */
	public final String getArchiveFilePathKey() {
		return this.archiveFilePathKey;
	}
	/**
	 * @param archiveFilePathKey the archiveFilePathKey to set
	 */
	public final void setArchiveFilePathKey(final String archiveFilePathKey) {
		this.archiveFilePathKey = archiveFilePathKey;
	}
	/**
	 * @return the filePathKey
	 */
	public final String getFilePathKey() {
		return this.filePathKey;
	}
	/**
	 * @param filePathKey the filePathKey to set
	 */
	public final void setFilePathKey(final String filePathKey) {
		this.filePathKey = filePathKey;
	}
	
	public Map<String, String> getFeedFilePath() {
		return this.feedFilePath;
	}
	
	public void setFeedFilePath(Map<String, String> feedFilePath) {
		this.feedFilePath = feedFilePath;
	}

	public Map<String, String> getFeedFileArchivePath() {
		return this.feedFileArchivePath;
	}
	
	public void setFeedFileArchivePath(Map<String, String> feedFileArchivePath) {
		this.feedFileArchivePath = feedFileArchivePath;
	}
	
	public Map<String, String> getSiteFeedConfiguration() {
		return this.siteFeedConfiguration;
	}
	
	public void setSiteFeedConfiguration(Map<String, String> siteFeedConfiguration) {
		this.siteFeedConfiguration = siteFeedConfiguration;
	}
	
	/**
	 * This method parse bazaar voice file and populates its value to VO.
	 * @return sucess / failure
	 * @throws BBBSystemException Exception
	 * @throws BBBBusinessException Exception
	 * @throws JAXBException Exception
	 */
	@SuppressWarnings("unchecked")
    public boolean unmarshal() throws BBBSystemException, BBBBusinessException, JAXBException {
        logDebug("BazaarVoice Unmarshaller Method Name [unmarshal]");

	    final JAXBContext context = JAXBContext.newInstance(FeedType.class);
	    final Unmarshaller unMarshaller = context.createUnmarshaller();
	    Set<String> siteCodes = getSiteFeedConfiguration().keySet();
	    FileInputStream fstream = null;
	    boolean status = false;
	    String currentFeedSiteName = null;
	  
	       for(String siteCode:siteCodes ){
	    	   if(!Boolean.parseBoolean(getSiteFeedConfiguration().get(siteCode))) {
	   				continue;
	   			}
	    	   String filePath = BBBConfigRepoUtils.getStringValue(BBBCmsConstants.CONTENT_CATALOG_KEYS,getFeedFilePath().get(siteCode)); 
	    	   currentFeedSiteName = siteCode;
	            final BazaarVoiceVO bazaarVoiceVO = new BazaarVoiceVO();
	            try {
	                fstream = extractMethodForObtainingFilePath(filePath);
	            } catch (FileNotFoundException e) {
	                logError("Bazaar Voice feed file not found on this path " + filePath, e);
	                continue;
	            }
	            
	            final JAXBElement<FeedType> itemJaxb = extractMethodForUnmarshalling(unMarshaller, fstream);
	            final FeedType item = itemJaxb.getValue();

	            List<BazaarVoiceProductVO> lst;
	            if (null != item) {
	                final List<ProductType> lstProduct = item.getProduct();
	                lst = new ArrayList<BazaarVoiceProductVO>();
	                //Populating data to VO

	                for (int i = 0; i < lstProduct.size(); i++) {
	                    final ProductType product = lstProduct.get(i);
	                    try {
	                        final BazaarVoiceProductVO bazaarVoiceProductVO = new BazaarVoiceProductVO();
	                        ReviewStatisticsType reviewStatistics = null;
	                        if (null != product) {
	                            bazaarVoiceProductVO.setExternalId(product.getExternalId());
	                            bazaarVoiceProductVO.setId(product.getId());
	                            bazaarVoiceProductVO.setName(product.getName());
	                            bazaarVoiceProductVO.setSource(product.getSource());
	                            reviewStatistics = product.getReviewStatistics();
                                logDebug("Product details: Source:" + product.getSource() + " Name:"
	                                        + product.getName() + " Id:" + product.getId());
	                        }

	                        bazaarVoiceProductVO.setSiteId(currentFeedSiteName);


	                        if (null != reviewStatistics) {

	                            float avgOverallRating = 0.0f;
	                            int overallRatingRange = 0;
	                            int ratingsOnlyReviewCount = 0;

	                            bazaarVoiceProductVO.setTotalReviewCount(reviewStatistics.getTotalReviewCount());

	                            if (reviewStatistics.getOverallRatingRange() != null) {
	                                overallRatingRange = reviewStatistics.getOverallRatingRange().intValue();
	                            }

	                            bazaarVoiceProductVO.setOverallRatingRange(overallRatingRange+"");

	                            if (reviewStatistics.getRatingsOnlyReviewCount() != null) {
	                                ratingsOnlyReviewCount = reviewStatistics.getRatingsOnlyReviewCount().intValue();
	                            }
	                            bazaarVoiceProductVO.setRatingsOnlyReviewCount(ratingsOnlyReviewCount+"");

	                            if (reviewStatistics.getAverageOverallRating() != null) {
	                                avgOverallRating = reviewStatistics.getAverageOverallRating().floatValue();
	                            }
	                            bazaarVoiceProductVO.setAverageOverallRating(avgOverallRating);


                                logDebug("reviewStatistics: TotalReviewCount:"
	                                            + reviewStatistics.getTotalReviewCount()
	                                            + " OverallRatingRange:" + overallRatingRange 
	                                            + " RatingsOnlyReviewCount" + ratingsOnlyReviewCount
	                                            + " AverageOverallRating:" + avgOverallRating);

	                        }
	                        lst.add(bazaarVoiceProductVO);

	                    } catch (Exception ex) {

	                        if (null != product) {
	                            logError("Error in BazaarVoiceUnMarsahlling for product :" + product.getId());
	                        }
	                    }

	                }
	                bazaarVoiceVO.setBazaarVoiceProduct(lst);
	            }	

	            if (bazaarVoiceVO.getBazaarVoiceProduct() == null) {
                    logDebug("Bazaar Voice feed file is empty " + filePath);
	                status = false;
	            } else {
	                //calling method to update/insert review properties.
	                status = true;
	                
	                //PS-22964 defect. Clear the table BBB_BAZAAR_VOICE first & then insert the xml records
	                	Statement statement = null;
	                	PreparedStatement preparedStatement = null;
	                	Connection con = null;
	                	int number = 0;
	                	int configuredValue = getBatchNumber();
	                	if (configuredValue <= 0){
                			configuredValue = 50000;
                		}	 
	                	try {
	                		con = ((GSARepository) getBazaarVoiceManager().getBazaarVoiceRepository()).getDataSource().getConnection();
	                		statement = con.createStatement(); 
                			do{
                				//PS-29040 defect fixed. Clearing bazaar voice repository for site specific BV feed.
                				String sql = getQuery()+SITE_ID + siteCode+"'" + AND_ROWNUM + configuredValue;
                				preparedStatement = con.prepareStatement(sql);
                				
                				number = extractDBCall(preparedStatement);
                					                				
                			} while (number >= configuredValue );			                			
	                	} catch (SQLException excep) {
	                		logError("SQL exception while clearing the BazaarVoice repository", excep);
	                		if(con !=null){
	                			try {	                			
									con.rollback();
								} catch (SQLException e) {
									logError("SQL exception in rollbacking connection", e);
								}
	                		}	                		
	                	}finally{
	                		if (statement != null){
	                			try {
									statement.close();
								} catch (SQLException e) {
									logError("BazaarVoiceUnmarshaller:SQL exception while closing the statement" +e);
								}
	                		}
	                		if(preparedStatement!=null){
	                			try {
									preparedStatement.close();
								} catch (SQLException e) {
									logError("SQL exception while closing the statement" +e);
								}
	                		}
                				
                		
	                	}
	                
	                // Invalidating bazaar voice repository. After deleting the records, if the same feed 
	                // was processed, the records could not be added to the BBB_BAZAAR_VOICE table, because the records 
	                // were being picked up from the repository cache.
	                String[] descriptorNames = ((GSARepository) getBazaarVoiceManager().getBazaarVoiceRepository()).getItemDescriptorNames();
	    			try {
	    				for(String descriptor : descriptorNames){				
	    						GSAItemDescriptor desc = (GSAItemDescriptor) ((GSARepository) getBazaarVoiceManager().getBazaarVoiceRepository()).getItemDescriptor(descriptor);
	    						desc.invalidateCaches();			
	    				}
	    			} catch (RepositoryException exc) {
	    				logError("RepositoryException from "+ BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,exc);
	    			}
	                getBazaarVoiceManager().createUpdateProductBV(bazaarVoiceVO);
	                logDebug("BazaarVoice Feed File " + filePath + " processed successfully");
	            }
	            final File currentFeedFile = new File(filePath);
	            boolean isArchive = false;
	            String archiveFilePath = BBBConfigRepoUtils.getStringValue(BBBCmsConstants.CONTENT_CATALOG_KEYS,getFeedFileArchivePath().get(siteCode)); 

	            if (null != archiveFilePath) {
	                isArchive = this.archiveFile(currentFeedFile, archiveFilePath);
	            }
	            
	            logDebug("Archive was successful?:" + isArchive);
	            
	            try {
	    				fstream.close();
	    		} catch (IOException e) {
	    			logError(e);
	    		}
	        }
	    
	    
	    return status;
	}

	/**
	 * @param preparedStatement
	 * @return
	 * @throws SQLException
	 */
	protected int extractDBCall(PreparedStatement preparedStatement) throws SQLException {
		return preparedStatement.executeUpdate();
	}

	/**
	 * @param unMarshaller
	 * @param fstream
	 * @return
	 * @throws JAXBException
	 */
	protected JAXBElement<FeedType> extractMethodForUnmarshalling(final Unmarshaller unMarshaller,FileInputStream fstream) throws JAXBException {
		return (JAXBElement<FeedType>) unMarshaller.unmarshal(fstream);
	}

	/**
	 * @param filePath
	 * @return
	 * @throws FileNotFoundException
	 */
	protected FileInputStream extractMethodForObtainingFilePath(String filePath) throws FileNotFoundException {
		return new FileInputStream(new File(filePath));
	}



	/**
	 * The method is used for sapeunit.
	 * While running sapeunit the scheduler runs and archives the file so when sape unit is run
	 * it finds no file.In order to solve the problem,at the beginning of sapeunit one random old file is 
	 * moved to the upload location
	 * @param dir Directory
	 * @return File
	 * @throws BBBSystemException Exception
	 * @throws BBBBusinessException Exception
	 */
	public final File moveFileToUploadLoc(final File dir) throws BBBSystemException, BBBBusinessException {
		logDebug("BazaarVoice Unmarshaller Method Name [moveFileToUploadLoc]");
		File fileToMove = null;
		if (dir != null) {
			final File[] files = dir.listFiles();
			if (files != null && files.length > 0) {
				for (int i = 0; i < files.length; i++) {

					logDebug(i + "th File Name selected:" + files[i].getName()
						        + "The file is the one to be used to move to upload location?:"
						        + files[i].getName().endsWith("bazaarvoice.xml"));
					if (files[i].getName().endsWith("bazaarvoice.xml")) {
						fileToMove = files[i];
						logDebug("File selected to move in upload location is:" + fileToMove);
						final File destinationFile = this.getFilePathFrmConfigRepo(this.getFilePathKey());
						fileToMove.renameTo(destinationFile);
						break;
					}
				}
			}
		}
		return fileToMove;
	}
	
	/**This method moves the file from source to destination file.
	 * @param sourceFile Source File
	 * @param destinationFile Destination File
	 * @return Success / Failure
	 */
	private static boolean moveFile(final File sourceFile, final File destinationFile) {

		boolean returnValue = false;
		returnValue = sourceFile.renameTo(destinationFile);
		return returnValue;
	}
	/**The method archives the file after upload into the archive location.
	 * The file name of the archive file is renamed to mmddyyhhmmbazaarvoice.xml
	 * format so that it is unique
	 * @param sourceFile Source File
	 * @param moveDir Destination Directory
	 * @return Success / Failure
	 * @throws BBBSystemException Exception
	 * @throws BBBBusinessException Exception
	 */
	
	public final boolean archiveFile(final File sourceFile, final String moveDir) 
	        throws BBBSystemException, BBBBusinessException {

		logDebug("BazaarVoice Unmarshaller Method Name [archiveFile]");
		
		File destinationFile;
		//      Move new uploaded file to existing  file path
		final String pattern = "mmddyyhhmm";
		final Date currentDate = new Date();
		final SimpleDateFormat format = new SimpleDateFormat(pattern);
		final String dateInFormat = format.format(currentDate);
		destinationFile = new File(moveDir + dateInFormat + "bazaarvoice.xml");


		logDebug("MSG=[destination Directory: " + moveDir + "]" + "MSG=[source File: "
			            + sourceFile.getAbsolutePath() + "]" + "MSG=[destination File: "
			            + destinationFile.getAbsolutePath() + "]");

		return moveFile(sourceFile, destinationFile);
	}
	/**The method returns the FileInputStream to be unMarshalled.
	 * 
	 * @return File Stream
	 * @throws BBBSystemException Exception
	 * @throws BBBBusinessException Exception
	 */
	public final FileInputStream getFilePathForFeed() throws BBBSystemException, BBBBusinessException {
		FileInputStream fstream = null;

		logDebug("BazaarVoice Unmarshaller Method Name [getFilePathForFeed]");
		try {

			final File fileToStream = this.getFilePathFrmConfigRepo(this.getFilePathKey());
			
			if (fileToStream != null) {
			logDebug("MSG=[fileToStream path: " + fileToStream.getAbsolutePath() + "]");
			fstream = extractFileRetreivalForGetFileForFeed(fileToStream);
			}
		} catch (FileNotFoundException e) {
			
			logError("File can't be read at BazaarVoiceUnMarshaller.unmarshal()" + e.getMessage(),e);
			
		}
		return fstream;
	}

	/**
	 * @param fileToStream
	 * @return
	 * @throws FileNotFoundException
	 */
	protected FileInputStream extractFileRetreivalForGetFileForFeed(final File fileToStream)throws FileNotFoundException {
		return new FileInputStream(fileToStream);
	}

	/**The method gets the file path from configure repository and returns the file.
	 * @param key Configuration Key
	 * @return File
	 * @throws BBBSystemException Exception
	 * @throws BBBBusinessException Exception
	 */
	public File getFilePathFrmConfigRepo(final String key) throws BBBSystemException, BBBBusinessException {
		File file = null;
		final List<String> filePath = this.catalogTools.getContentCatalogConfigration(key);
		if (!BBBUtility.isListEmpty(filePath)) {
			logDebug("key is :" + key  + " value is:" + filePath.get(0));
			file = new File(filePath.get(0));
		}
		return file;
	}


	/**The method gets the file paths from configure repository and returns the file paths of the feed files for
	 *  processing.
	 * @param key Configuration Key
	 * @return File Path
	 * @throws BBBSystemException Exception
	 * @throws BBBBusinessException Exception
	 */
	public final String getFeedFilePaths(final String key) throws BBBSystemException, BBBBusinessException {
		
		final List<String> filePath = this.catalogTools.getContentCatalogConfigration(key);
		if (!BBBUtility.isListEmpty(filePath)) {
			logDebug("key is :" + key + " value is:" + filePath.get(0));
			return filePath.get(0);
		}
		logDebug("File path is Empty");
		return null;	
	}
}


