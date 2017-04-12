package com.bbb.feeds.bazaarvoice.marshaller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import atg.repository.MutableRepository;

import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.feeds.bazaarvoice.utils.BVCatalogFeedTools;
import com.bbb.feeds.marshaller.IFeedsMarshaller;
import com.bbb.framework.jaxb.bazaarvoice.clientfeed.FeedType;
import com.bbb.framework.jaxb.bazaarvoice.clientfeed.ObjectFactory;
import com.bbb.framework.jaxb.bazaarvoice.clientfeed.ProductType;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 * The class generates category , product and sku feed in the form of a xml file
 *
 * @author Prashanth K Bhoomula
 *
 */
public class BVClientFeedMarshaller extends BBBGenericService implements
		IFeedsMarshaller {

	private BVCatalogFeedTools feedTools;
	private Timestamp lastModDate = null;
	private MutableRepository feedRepository;
	private Map<String, String> siteFeedConfiguration = new HashMap<String, String>();
	private String typeOfFeed = null;
	private String bvClientTargetFeedFilePath;
	private String bvClientSourceFeedFilePath;
	private Map<String, String> clientFeedFileName = new HashMap<String, String>();
	private BBBCatalogTools catalogTools;
	private String siteName = null;


	/**
	 * This method calls the respective marshal methods for category and
	 * product.
	 */
	@Override
	public void marshall(final boolean isFullDataFeed, final Timestamp schedulerStartDate) {
		this.logDebug("BazaarVoice ClientFeedMarshaller Method : marshal");
		final Set<String> siteCodes = this.siteFeedConfiguration.keySet();
		final Iterator<String> it = siteCodes.iterator();
		List<String> newFeedList = null;
		while (it.hasNext()) {
			boolean status = true;
			final String siteConfigurationCode = it.next();
			this.logDebug("BV Catalog Feed Configuration for "
					+ siteConfigurationCode + " is set to"
					+ this.siteFeedConfiguration.get(siteConfigurationCode));
			if (!Boolean.parseBoolean(this.siteFeedConfiguration
					.get(siteConfigurationCode))) {
				continue;
			}
			try {
				final List<String> config = this.getCatalogTools().getContentCatalogConfigration(siteConfigurationCode);
				if ((config != null) && !config.isEmpty()) {
					this.siteName = config.get(0);
				}
				this.getFeedTools().pushSiteContext(this.siteName);
				newFeedList = this.getNewlyUploadedFeeds(siteConfigurationCode);
				this.logDebug("Found "+newFeedList.size()+" Feeds:- " + newFeedList);
			} catch (final BBBSystemException bse) {
				this.logError("BBBSystemException exception occured while retriving the feeds:- "+bse);
				//bse.printStackTrace();
			} catch (final BBBBusinessException bbe) {
				this.logError("BBBBusinessException exception occured while retriving the feeds:- "+bbe);
				//bbe.printStackTrace();
			}
			if((newFeedList != null)
					&& (newFeedList.size()>0)) {
				for(final String feedSourceFile: newFeedList) {
					try {
						status = this.generateMobileBVFeed(feedSourceFile, siteConfigurationCode);
						if(status) {
							this.logInfo(feedSourceFile+" feed conversion sucessful");
						}else {
							this.logInfo(feedSourceFile+" feed conversion failed");
						}
					} catch (final BBBBusinessException e) {
						status = false;
						//e.printStackTrace();
						logError(e.getMessage(),e);
					} catch (final BBBSystemException e) {
						status = false;
						//e.printStackTrace();
						logError(e.getMessage(),e);
					} finally {
						this.getFeedTools().updateRepository(this.getTypeOfFeed(), schedulerStartDate, false, status);
						this.getFeedTools().getSiteContextManager().clearSiteContextStack();
					}
				}
			}
			else {
				this.logError("Didn't found any feeds, updating repository as failed. ");
				this.getFeedTools().updateRepository(this.getTypeOfFeed(), schedulerStartDate, false, false);
			}
		}
	}

	/**
	 * This method will reset the BV Client Feed Product elements to Sku elments.
	 * @param  objectFactory
	 * @return FeedType
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public boolean generateMobileBVFeed(final String feedSourceFile, final String siteConfigurationCode) throws BBBBusinessException, BBBSystemException {

		this.logDebug("BVClientFeedMarshaller.getMobileBVFeedElement method Start");
		final String fileName = new File(feedSourceFile).getName();
		final ObjectFactory objectFactory = new ObjectFactory();
		final FeedType bvClientFeed = this.getBVClientFeedElement(feedSourceFile);
		if(bvClientFeed == null) {
			return false;
		}

		final List<ProductType> products = bvClientFeed.getProduct();
		final List<ProductType> convertedProducts = new ArrayList<ProductType>();

		if((products != null) && (products.size() > 0)) {
			this.logDebug("Products Size in BVClient Feed "+products.size());

			final Iterator<ProductType> productIterator = products.iterator();
			ProductType skuAsProductType = null, productType = null;
			String productId =  null, sku = null;
			final List<String> errProdList = new ArrayList<String>();
			ProductVO productVO = null;
			List<String> skus = null;
			Iterator<String> skuIterator = null;
			String prefix = "";
			while(productIterator.hasNext()) {

				productType = productIterator.next();
				productId = productType.getExternalId();
				if(productId.length()<6) {
					continue;
				}
				prefix = "";
				if(productId.startsWith("BBB")
					|| productId.startsWith("BAB")) {
					prefix = productId.substring(0, 3);
					productId = productId.substring(3,productId.length());
				}
				try {
					productVO = this.getFeedTools().getCatalogTools().getProductDetails(this.siteName, productId, false);
				} catch (final BBBBusinessException be) {
					errProdList.add(productId);
					this.logError("Skipping the product with id "+prefix+productId+" due to following exception "+be);
					continue;
				}
				skus = productVO.getChildSKUs();
				if((skus ==null) || (skus.size()==0)) {
					errProdList.add(productId);
					this.logError(prefix+productId+" :-Product doesn't have any skus, skipping the product");
					continue;
				}
				skuIterator = skus.iterator();
				while(skuIterator.hasNext()) {
					sku = skuIterator.next();
					skuAsProductType = objectFactory.createProductType();
					skuAsProductType.setExternalId(prefix+sku);
					this.copyProductAttributes(skuAsProductType, productType);
					convertedProducts.add(skuAsProductType);
				}
			}
			this.logDebug("Products Size in BVClient Mobile Feed "+convertedProducts.size());
			this.logDebug("Removed Products count due to errors "+ Arrays.toString(errProdList.toArray()));
			products.removeAll(products);
			bvClientFeed.getProduct().addAll(convertedProducts);
		}
		return this.getMarshalFile(bvClientFeed, objectFactory, siteConfigurationCode, fileName);
	}

	private void copyProductAttributes(final ProductType skuAsProductType,
			final ProductType productType) {
		skuAsProductType.setId(skuAsProductType.getExternalId());
		skuAsProductType.setDisabled(productType.isDisabled());
		skuAsProductType.setRemoved(productType.isRemoved());
		skuAsProductType.setName(productType.getName());
		skuAsProductType.setSource(productType.getSource());
		skuAsProductType.setCategoryItems(productType.getCategoryItems());
		skuAsProductType.setReviewStatistics(productType.getReviewStatistics());
		skuAsProductType.setNativeReviewStatistics(productType.getNativeReviewStatistics());
		skuAsProductType.setReviews(productType.getReviews());
	}

	/**
	 * This method will read the BV Client Feed and returns Feed element
	 *
	 * @return FeedType
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */

	public FeedType getBVClientFeedElement(final String feedSourceFile) throws BBBBusinessException, BBBSystemException {

		this.logDebug("BVClientFeedMarshaller Method : getBVClientFeedElement ");
		this.logDebug("Processing BVClientSourceFeed "+feedSourceFile);
		FeedType feedType =  null;
		BufferedReader fileReader = null;
		FileReader reader = null;
		try {
			if (feedSourceFile != null) {
				reader = new FileReader(feedSourceFile);
				fileReader = new BufferedReader(reader);
				final JAXBContext context = JAXBContext.newInstance(FeedType.class);
				final Unmarshaller unMarshaller = context.createUnmarshaller();
				final JAXBElement<FeedType> root = (JAXBElement)unMarshaller.unmarshal(fileReader);
				feedType = root.getValue();
			}
		} catch (final FileNotFoundException e) {
			//Scheduler should continue process other site's feeds in case of FileNotFoundException.
			this.logError(LogMessageFormatter.formatMessage(null,
					"BVClientFeedMarshaller.getBVClientFeed() | BVClientFeed File not found"),e);
			//e.printStackTrace();
		} catch (final JAXBException e) {
			this.logError(LogMessageFormatter.formatMessage(null,
		            "BBBMarketingFeedsMarshaller.getBVClientFeedElement() | JAXBException "), e);
			throw new BBBBusinessException(BBBCatalogErrorCodes.JAXB_EXCEPTION,e);
		}
		finally{
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					this.logError("IO exception while closing file reader", e);
				}
			}
			if(fileReader!=null){
				try {
					fileReader.close();
				} catch (IOException e) {
					this.logError("IO exception while closing file reader", e);
				}
			}
		}
		return feedType;
	}

	public List<String> getNewlyUploadedFeeds(final String siteConfigurationCode) throws BBBSystemException, BBBBusinessException {

		final List<String> latestFeed = new ArrayList<String>();
		List<String> feedConfigPath;
		List<String> feedFolder;
		List<String> siteConfigPath;
		final String pathSeparator = File.separator;
		feedConfigPath = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE, BBBCoreConstants.SCHEDULED_FEEDS_PATH_CONFIG_KEY);
		if((feedConfigPath == null)
				 || feedConfigPath.isEmpty()) {
			throw new BBBBusinessException(BBBCoreConstants.SCHEDULED_FEEDS_PATH_CONFIG_KEY+" configuration missing for ");
		}

		feedFolder = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE, this.getBvClientSourceFeedFilePath());
		if ((feedFolder == null) || feedFolder.isEmpty()) {
			throw new BBBBusinessException(this.getBvClientSourceFeedFilePath()+" configuration missing for "+this.getTypeOfFeed());
		}
		siteConfigPath = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE, siteConfigurationCode);
		if((siteConfigPath == null)
				 || siteConfigPath.isEmpty()) {
			throw new BBBBusinessException(siteConfigurationCode+" configuration missing in "+BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE +" configurations");
		}

		final String feedPath = feedConfigPath.get(0)+pathSeparator+siteConfigPath.get(0)+pathSeparator+feedFolder.get(0);

		final File dir = new File(feedPath);
		final File [] files  = dir.listFiles();

        final Timestamp lastRunTimestamp = this.getFeedTools().getLastModifiedDate(this.getTypeOfFeed());
		if(files !=null) {
			for(final File file: files) {
				if((lastRunTimestamp == null)
						|| lastRunTimestamp.before(new Date(file.lastModified()))) {
					latestFeed.add(file.getAbsolutePath());
				}
			}
		}
		return latestFeed;
	}

	/**
	 * The method creates a JAXB marshaled xml file after data is populated in
	 * JAXB classes for category feed
	 *
	 * @param categoryFeed
	 * @param catergoryObject
	 * @param indexOfFile
	 *            no of the file that is created for the feed
	 * @return true if marshaling is successful else return false
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public boolean getMarshalFile(final FeedType bvClientFeed,
			final ObjectFactory feedObjFactory, final String siteConfigurationCode, final String feedFileName) throws BBBBusinessException,
			BBBSystemException {
		this.logDebug("BVClientFeedMarshaller Method : getMarshalFile ");		
		boolean status = true;
		try {
			final JAXBElement<FeedType> feedRoot = feedObjFactory.createFeed(bvClientFeed);
			final StringWriter stringWriter = new StringWriter();
			final JAXBContext context = JAXBContext.newInstance(FeedType.class);
			final Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			marshaller.marshal(feedRoot, stringWriter);
			final String clientFeedXml = stringWriter.getBuffer().toString();
			List<String> feedConfigPath;
			List<String> feedFolder;
			List<String> siteConfigPath;
			final String pathSeparator = File.separator;
			feedConfigPath = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE, BBBCoreConstants.SCHEDULED_FEEDS_PATH_CONFIG_KEY);
			if((feedConfigPath == null)
					 || feedConfigPath.isEmpty()) {
				throw new BBBBusinessException(BBBCoreConstants.SCHEDULED_FEEDS_PATH_CONFIG_KEY+" configuration missing for ");
			}

			feedFolder = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE, this.getBvClientTargetFeedFilePath());
			if ((feedFolder == null) || feedFolder.isEmpty()) {
				throw new BBBBusinessException(this.getBvClientSourceFeedFilePath()+" configuration missing for "+this.getTypeOfFeed());
			}
			siteConfigPath = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE, siteConfigurationCode);
			if((siteConfigPath == null)
					 || siteConfigPath.isEmpty()) {
				throw new BBBBusinessException(siteConfigurationCode+" configuration missing in "+BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE +" configurations");
			}

			final String feedPath = feedConfigPath.get(0)+pathSeparator+siteConfigPath.get(0)+pathSeparator+feedFolder.get(0);
			this.logDebug("BV Client Feed Target file path:" +feedPath);
			BBBUtility.writeFile(feedPath,feedFileName, clientFeedXml);
		} catch (final JAXBException e) {
			this.logError(LogMessageFormatter.formatMessage(null,
										"BVClientFeedMarshaller.getMarshalFile() | BBBBusinessException "),e);
			status = false;
			throw new BBBBusinessException(BBBCatalogErrorCodes.JAXB_EXCEPTION,e);

		}
		return status;

	}


	public BVCatalogFeedTools getFeedTools() {
		return this.feedTools;
	}

	public void setFeedTools(final BVCatalogFeedTools feedTools) {
		this.feedTools = feedTools;
	}

	public Timestamp getLastModDate() {
		return this.lastModDate;
	}

	public void setLastModDate(final Timestamp lastModDate) {
		this.lastModDate = lastModDate;
	}

	public Map<String, String> getSiteFeedConfiguration() {
		return this.siteFeedConfiguration;
	}

	public void setSiteFeedConfiguration(
			final Map<String, String> siteFeedConfiguration) {
		this.siteFeedConfiguration = siteFeedConfiguration;
	}

	public MutableRepository getFeedRepository() {
		return this.feedRepository;
	}

	public void setFeedRepository(final MutableRepository feedRepository) {
		this.feedRepository = feedRepository;
	}

	public String getTypeOfFeed() {
		return this.typeOfFeed;
	}

	public void setTypeOfFeed(final String typeOfFeed) {
		this.typeOfFeed = typeOfFeed;
	}


	public String getBvClientTargetFeedFilePath() {
		return this.bvClientTargetFeedFilePath;
	}

	public void setBvClientTargetFeedFilePath(final String bvClientTargetFeedFilePath) {
		this.bvClientTargetFeedFilePath = bvClientTargetFeedFilePath;
	}

	public String getBvClientSourceFeedFilePath() {
		return this.bvClientSourceFeedFilePath;
	}

	public void setBvClientSourceFeedFilePath(final String bvClientSourceFeedFilePath) {
		this.bvClientSourceFeedFilePath = bvClientSourceFeedFilePath;
	}

	public Map<String, String> getClientFeedFileName() {
		return this.clientFeedFileName;
	}

	public void setClientFeedFileName(final Map<String, String> clientFeedFileName) {
		this.clientFeedFileName = clientFeedFileName;
	}

	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}

	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	public String getSiteName() {
		return this.siteName;
	}

	public void setSiteName(final String siteName) {
		this.siteName = siteName;
	}

}