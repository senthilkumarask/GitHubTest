package com.bbb.feeds.marketing.marshaller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.core.util.StringUtils;
import atg.repository.MutableRepository;
import atg.repository.RepositoryItem;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.feeds.marketing.utils.BBBMarketingFeedTools;
import com.bbb.feeds.marketing.vo.MarketingFeedVO;
import com.bbb.feeds.marshaller.IFeedsMarshaller;
import com.bbb.utils.BBBUtility;

/**
 * The class generates category , product and sku feed in the form of a xml file
 * 
 * @author Prashanth K Bhoomula
 * 
 */
public class BBBMarketingFeedsMarshaller extends BBBGenericService implements IFeedsMarshaller {

  private BBBMarketingFeedTools feedTools;
  private BBBCatalogTools catalogTools;  
  protected Timestamp lastModDate = null;  
  private MutableRepository feedRepository;
  private List<String> feedHeaders = null;
  protected Map<String,String> siteFeedConfiguration = new HashMap<String,String>(); 
  private String typeOfFeed = null;
  private String feedFilePath;
  private Map<String, String> feedFileName = new HashMap<String, String>();
  private String methodName = null;
  protected String siteName = null;
  protected String catalogId = null;
  private String fieldDelimiter;
  private String feedStaticText = null;
  private boolean includeHeaders = false;
  
  /**
   * This method calls the respective marshal methods for category and product.
   */
	@Override
   public void marshall(boolean isFullDataFeed, Timestamp schedulerStartDate) {
	   
    logDebug("BBBMarketingFeedsMarshaller.marshall Method start");
	if (!isFullDataFeed) {
		logDebug("Get last modified date from repository");
		this.lastModDate = getFeedTools().getLastModifiedDate(getTypeOfFeed());
	}
	logDebug("last modified date: " + this.getLastModDate());
	Set<String> siteCodes = siteFeedConfiguration.keySet();
	logDebug("Generating "+ getTypeOfFeed()+" for Site"+ siteCodes);
	Iterator<String> it = siteCodes.iterator();
	List<MarketingFeedVO>  feedVOList = null;
	loadCatalogData(isFullDataFeed, lastModDate);
	while(it.hasNext()) {
		boolean status = true;
		String siteConfigurationCode = it.next();
		logDebug(getTypeOfFeed()+" Configuration for "+siteConfigurationCode+" is set to"+ siteFeedConfiguration.get(siteConfigurationCode));		
		if(!Boolean.parseBoolean(siteFeedConfiguration.get(siteConfigurationCode))) {
			continue;
		}
		try {
			//Gets the site name for given site code
			List<String> config = getCatalogTools().getContentCatalogConfigration(siteConfigurationCode);	
			if (config == null 
					|| config.isEmpty()) {
				logError(siteConfigurationCode +" is not configured, so skipping it");
				continue;
			}
			this.siteName = config.get(0);
			//getFeedTools().pushSiteContext(siteName);
			//Get the catalog by site name
			RepositoryItem catalog = getFeedTools().getCatalogBySite(siteName);
			if(catalog == null) {
				logError("The "+ siteName +" doesn't have any catalogs");
				continue;
			}
			this.catalogId = catalog.getRepositoryId();
			logDebug("Generating "+getTypeOfFeed()+" for Site "+ siteName +" and from catalog "+catalog.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME));
			feedVOList = getMarketingFeedVOList(isFullDataFeed);		
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
			if(status) {
				status = generateFeed(feedVOList, siteConfigurationCode);
			}
		    getFeedTools().updateRepository(getTypeOfFeed(), schedulerStartDate, isFullDataFeed, status);
		    getFeedTools().getSiteContextManager().clearSiteContextStack();
		}
	}
	getFeedTools().clearCachedCatalogItems();
   }

	protected void loadCatalogData(boolean isFullDataFeed, Timestamp lastModDate) {
	
		try {
			
			getFeedTools().getCatalogItemsForFeedGeneration(isFullDataFeed,lastModDate, 
												BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
		} catch (BBBSystemException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logError(e.getMessage(),e);
		}		
	}

	/**
	 * @param isFullDataFeed 
	 * @param isFullDataFeed
	 * @param typeOfFeed 
	 * @param feedVOList
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	
	protected List<MarketingFeedVO> getMarketingFeedVOList(Object isFullDataFeed)
					throws BBBSystemException, BBBBusinessException {
		
		try {
			
			
			Method method = getFeedTools().getClass().getMethod(methodName,
											new Class[] { Boolean.TYPE, Timestamp.class, List.class,
											String.class, String.class});
			if(method != null) {
				logDebug("Invoking BBBMarketingFeedTools ["+methodName+"] method");				
				return (List<MarketingFeedVO>)method.invoke(getFeedTools(), new Object[] {isFullDataFeed, getLastModDate(),
						this.feedHeaders, this.catalogId, this.siteName});
			}
		} catch (SecurityException e) {
			logError("Security Exception Occured while invoking method "+methodName);
			throw new BBBSystemException("Security Exception Occured while invoking method "+methodName);
		} catch (NoSuchMethodException e) {
			logError("There is no method with the given name:- "+methodName);
			throw new BBBSystemException("NoSuchMethodException Occured while looking for method "+methodName);
		} catch (IllegalArgumentException e) {
			logError("There is no method with name "+methodName+" and given arguments");
			throw new BBBSystemException("IllegalArgumentException Occured while looking for method "+methodName);
		} catch (IllegalAccessException e) {
			logError("Method with name "+methodName+" is not accesible");
			throw new BBBSystemException("IllegalAccessException Occured while looking for method "+methodName);
		} catch (InvocationTargetException e) {
			logError("Exception occured in Method with name "+methodName);
			logError("Exception is "+e.getTargetException());
			throw new BBBSystemException("InvocationTargetException Occured while looking for method "+methodName);
		}
		return null;
	}	

   protected boolean generateFeed(List<MarketingFeedVO> feedVOList, String siteConfigurationCode) {
	   
		//StringBuilder  feedContent = new StringBuilder();
		
		
		List<String> siteConfigPath = null;
		List<String> feedConfigPath = null;
		List<String> feedFolder = null;
		String feedPath=null;
		String fileName=null;
		BufferedWriter bufferedWriter = null;
		FileWriter fileWriter = null;
        File file = null;
		String pathSeparator = File.separator;
		
		
		try {
			/**
			 * Feed path will be configured through three configurations
			 * thirdpartyFeedsPath:- This will point to the root folder for all feeds
			 *  siteConfigurationCode:- Folder name will change based on site
			 *  feed
			 */
			feedConfigPath = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE, BBBCoreConstants.SCHEDULED_FEEDS_PATH_CONFIG_KEY);
			if(feedConfigPath == null 
					 || feedConfigPath.isEmpty()) {
				throw new BBBBusinessException(BBBCoreConstants.SCHEDULED_FEEDS_PATH_CONFIG_KEY+" configuration missing for ");
			}
			siteConfigPath = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE, siteConfigurationCode);
			if(siteConfigPath == null 
					 || siteConfigPath.isEmpty()) {
				throw new BBBBusinessException(siteConfigurationCode+" configuration missing in "+BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE +" configurations");
			}			
			feedFolder = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE, this.getFeedFilePath());
			if(feedFolder == null 
					 || feedFolder.isEmpty()) {
				throw new BBBBusinessException(this.getFeedFilePath()+" configuration missing for "+getTypeOfFeed());
			}
			feedPath = feedConfigPath.get(0)+pathSeparator+siteConfigPath.get(0)+pathSeparator+feedFolder.get(0);
			fileName = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());			
			List<String> configFileName = getCatalogTools().getAllValuesForKey(BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE, getFeedFileName().get(siteConfigurationCode));
			if (configFileName == null 
					|| configFileName.isEmpty()) {
				fileName = getTypeOfFeed()+"-"+fileName;
			} else {
				fileName =configFileName.get(0).replaceAll("yyyymmdd", fileName);
			}
			
			/**
			 * Changes made as part of SAP-1542 where ask is to write to file directly using a BufferedWriter instead of StringBuffer
			 * We will be using String Buffer for chunks of data and then clear the buffer after writing the data to file. The size of chunks
			 * will be set as component property.
			 */
			
			//boolean isFileWriteSuccess = false;
	        try{
	        	final File dirStruc = new File(feedPath);
	            if (!dirStruc.exists()) {
	                // Create the directory tree, if does
	                // not exist
	                dirStruc.mkdirs();
	            }

	            if (dirStruc.canWrite()) {

	                file = new File(dirStruc, fileName);

	                if (file.exists()) { // If file exists, delete it
	                    file.delete();
	                }

	                // Construct the BufferedWriter object
	                fileWriter = new FileWriter(file);
	                bufferedWriter = new BufferedWriter(fileWriter);

	                // Start writing to the output stream
	                //bufferedWriter.write(feedContent.toString());
	                //isFileWriteSuccess = true;

	            } else {
	                throw new BBBSystemException(BBBCoreErrorConstants.UTIL_ERROR_1001,
	                        "Write permissions unavailable for directory : " + feedPath);
	            }

	        //return isFileWriteSuccess;
			
	            if(!StringUtils.isEmpty(feedStaticText) && !BBBUtility.isListEmpty(feedVOList)) {
	            	bufferedWriter.write(feedStaticText);
	            	//feedContent.append(feedStaticText);
				}
				if(includeHeaders && !BBBUtility.isListEmpty(feedVOList)) {
					int i = 0;
					for(String header: feedHeaders) {
						
						if(i<feedHeaders.size()-1) {
							bufferedWriter.write(header+getFieldDelimiter());
							//feedContent.append(header+getFieldDelimiter());
						}
						else {
							bufferedWriter.write(header);
							//feedContent.append(header);
						}
						i++;
					}
					bufferedWriter.newLine();
					//feedContent.append("\n");
				}
				String tempStr = null;
				
				if(feedVOList!=null) {
					for(MarketingFeedVO feedVO: feedVOList) {
						tempStr = feedVO.toString(getFeedHeaders(), getFieldDelimiter());
						tempStr = tempStr.substring(0,tempStr.length()-1);
						bufferedWriter.write(tempStr);
						bufferedWriter.newLine();
						//feedContent.append(tempStr+"\n");
					}
				}
				
				getFeedTools().generateExceptionData(feedPath, siteConfigPath.get(0)+"-"+getTypeOfFeed()+"-"+new SimpleDateFormat("yyyyMMdd").format(new java.util.Date())+".log");
				//BBBUtility.writeFile(feedPath, fileName, feedContent.toString());
				getFeedTools().generateTriggerFile(feedPath, "go.txt", "go");
				return true;
			} catch (final FileNotFoundException ex) {
	            throw new BBBSystemException(BBBCoreErrorConstants.UTIL_ERROR_1002, "Error while creating file"
	                    + (feedPath + fileName), ex);
	        } catch (final IOException ex) {
	            throw new BBBSystemException(BBBCoreErrorConstants.UTIL_ERROR_1003, "Error while creating file"
	                    + (feedPath + fileName), ex);
	        } finally {
	            // Close the BufferedWriter and FileWriter.
	            closeResources(feedPath, fileName, bufferedWriter, fileWriter);
	        }
		}
		catch (BBBSystemException e) {
			logError("Error Occured while retriving the feed file configurations"+ e);
		} catch (BBBBusinessException e) {
			logError("Error Occured while retriving the feed file configurations"+ e);
		}
		/*try {
			
			return BBBUtility.writeFile("C:\\prashanth\\work\\feeds\\marketing\\omniture", getFeedFileName().get(siteConfigurationCode), feedContent.toString());	
		} catch (BBBSystemException e) {
			logError("Error Occured while retriving the feed file configurations"+ e);
		}*/
		return false;
	}

/**
 * @param feedPath
 * @param fileName
 * @param bufferedWriter
 * @param fileWriter
 * @throws BBBSystemException
 */
protected void closeResources(String feedPath, String fileName,
		BufferedWriter bufferedWriter, FileWriter fileWriter)
		throws BBBSystemException {
	try {
	    if (bufferedWriter != null) {
	        bufferedWriter.flush();
	        bufferedWriter.close();
	    }
	    if (fileWriter != null) {
	        fileWriter.close();
	    }
		} catch (final IOException ex) {
			throw new BBBSystemException(BBBCoreErrorConstants.UTIL_ERROR_1003, "Error while creating file"
	        + (feedPath + fileName), ex);
		}
}
   	
	protected void setFeedGenerationDate() {
		if(!StringUtils.isEmpty(feedStaticText)
				&& feedStaticText.indexOf("DATE") != -1) {
			feedStaticText = feedStaticText.replaceFirst("DATE",
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
									.format(new java.util.Date()));			
		}
	}
   
	public BBBMarketingFeedTools getFeedTools() {
		return feedTools;
	}
	
	public void setFeedTools(BBBMarketingFeedTools feedTools) {
		this.feedTools = feedTools;
	}
	
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
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
		
	public List<String> getFeedHeaders() {
		return feedHeaders;
	}

	public void setFeedHeaders(List<String> feedHeaders) {
		this.feedHeaders = feedHeaders;
	}

	public Map<String, String> getSiteFeedConfiguration() {
		return siteFeedConfiguration;
	}
	
	public void setSiteFeedConfiguration(Map<String, String> siteFeedConfiguration) {
		this.siteFeedConfiguration = siteFeedConfiguration;
	}
	
	public String getTypeOfFeed() {
		return typeOfFeed;
	}
	
	public void setTypeOfFeed(String typeOfFeed) {
		this.typeOfFeed = typeOfFeed;
	}
	
	public String getFeedFilePath() {
		return feedFilePath;
	}
	
	public void setFeedFilePath(String feedFilePath) {
		this.feedFilePath = feedFilePath;
	}
	
	public Map<String, String> getFeedFileName() {
		return feedFileName;
	}
	
	public void setFeedFileName(Map<String, String> feedFileName) {
		this.feedFileName = feedFileName;
	}

	public String getSiteName() {
		return siteName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getCatalogId() {
		return catalogId;
	}

	public String getFieldDelimiter() {
		return fieldDelimiter;
	}
	public void setFieldDelimiter(String fieldDelimiter) {
		this.fieldDelimiter = fieldDelimiter;
	}

	public String getFeedStaticText() {
		return feedStaticText;
	}

	public void setFeedStaticText(String feedStaticText) {
		this.feedStaticText = feedStaticText;
	}

	public boolean isIncludeHeaders() {
		return includeHeaders;
	}

	public void setIncludeHeaders(boolean includeHeaders) {
		this.includeHeaders = includeHeaders;
	}
}