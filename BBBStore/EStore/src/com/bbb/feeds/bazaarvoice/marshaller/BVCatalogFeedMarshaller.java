package com.bbb.feeds.bazaarvoice.marshaller;

import java.io.File;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.lang.StringUtils;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.BrandVO;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.common.BBBGenericService;
import com.bbb.config.manager.ConfigTemplateManager;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.feed.jaxb.util.BBBJAXBCharacterEscapeHandler;
import com.bbb.feeds.bazaarvoice.utils.BVCatalogFeedTools;
import com.bbb.feeds.marshaller.IFeedsMarshaller;
import com.bbb.framework.jaxb.bazaarvoice.catalogfeed.AttributeType;
import com.bbb.framework.jaxb.bazaarvoice.catalogfeed.AttributesType;
import com.bbb.framework.jaxb.bazaarvoice.catalogfeed.BrandType;
import com.bbb.framework.jaxb.bazaarvoice.catalogfeed.BrandsType;
import com.bbb.framework.jaxb.bazaarvoice.catalogfeed.CategoriesType;
import com.bbb.framework.jaxb.bazaarvoice.catalogfeed.CategoryType;
import com.bbb.framework.jaxb.bazaarvoice.catalogfeed.EansType;
import com.bbb.framework.jaxb.bazaarvoice.catalogfeed.FeedType;
import com.bbb.framework.jaxb.bazaarvoice.catalogfeed.ObjectFactory;
import com.bbb.framework.jaxb.bazaarvoice.catalogfeed.OptionalLocalizedStringType;
import com.bbb.framework.jaxb.bazaarvoice.catalogfeed.ProductType;
import com.bbb.framework.jaxb.bazaarvoice.catalogfeed.ProductsType;
import com.bbb.framework.jaxb.bazaarvoice.catalogfeed.UpcsType;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;
import com.sun.xml.bind.marshaller.CharacterEscapeHandler;

import atg.multisite.SiteManager;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 * The class generates category , product and sku feed in the form of a xml file
 * 
 * @author Prashanth K Bhoomula
 * 
 */
public class BVCatalogFeedMarshaller extends BBBGenericService implements
		IFeedsMarshaller {

	private static final String DEPARTMENT1 = "Department1";
	private BVCatalogFeedTools feedTools;
	private Timestamp lastModDate = null;
	private MutableRepository feedRepository;
	private Map<String, String> siteFeedConfiguration = new HashMap<String, String>();
	private String typeOfFeed = null;
	private String catalogFeedFilePath;
	private Map<String, String> catalogFeedFileName = new HashMap<String, String>();
	private String siteName = null;
	private ConfigTemplateManager configTemplateManager = null;
	private SiteManager siteManager = null;
	private BBBCatalogTools catalogTools;	
	private String seanURL = "";
	private String siteURL = "";
	private Map<String,String> siteCategoryNameMap;


	public Map<String, String> getSiteCategoryNameMap() {
		return this.siteCategoryNameMap;
	}

	public void setSiteCategoryNameMap(final Map<String, String> siteCategoryNameMap) {
		this.siteCategoryNameMap = siteCategoryNameMap;
	}
	
	/**
	 * This method calls the respective marshal methods for category and
	 * product.
	 */
	@Override
	public void marshall(final boolean isFullDataFeed,final Timestamp schedulerStartDate) {
		logDebug("BazaarVoice CatalogFeedMarshaller Method : marshal");
		if (!isFullDataFeed) {
			logDebug("Get last modified date from repository");
			this.lastModDate = getFeedTools().getLastModifiedDate(
					getTypeOfFeed());
		}
		logDebug("last modified date for category: " + this.getLastModDate());
		final Set<String> siteCodes = this.siteFeedConfiguration.keySet();
		logDebug("Generating Catalog Feed for Site" + siteCodes);
		final Iterator<String> it = siteCodes.iterator();
		loadCatalogData(isFullDataFeed, this.lastModDate);
		while (it.hasNext()) {
			boolean status = true;
			final String siteConfigurationCode = it.next();
			logDebug("BV Catalog Feed Configuration for "
					+ siteConfigurationCode + " is set to"
					+ this.siteFeedConfiguration.get(siteConfigurationCode));
			if (!Boolean.parseBoolean(this.siteFeedConfiguration
					.get(siteConfigurationCode))) {
				continue;
			}
			
			try {
				// Gets the site name for given site code
				final List<String> config = getCatalogTools().getContentCatalogConfigration(siteConfigurationCode);
				if (config != null && !config.isEmpty()) {
					this.siteName = config.get(0);
				}
//				getFeedTools().pushSiteContext(this.siteName);
				logDebug("siteName from Catalog Configuration Content"
						+ this.siteName);
				
				// Get the catalog by site name
				final RepositoryItem catalog = getFeedTools().getCatalogBySite(
						this.siteName);
				if (catalog == null) {
					logError("The " + this.siteName + " doesn't have any catalogs");
					continue;
				}
				catalog.getRepositoryId();
				logDebug("Generating BV Catalog Feed for Site "
						+ this.siteName
						+ " and from catalog "
						+ catalog
								.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME));

				final ObjectFactory feedObjFactory = new ObjectFactory();
				final FeedType catalogFeed = feedObjFactory.createFeedType();
				final GregorianCalendar gcal = new GregorianCalendar();
				try {
					catalogFeed.setExtractDate(DatatypeFactory.newInstance()
							.newXMLGregorianCalendar(gcal));
				} catch (final DatatypeConfigurationException e1) {
					logError("Data type configuration exception in marshall method", e1);
				}
				catalogFeed.setIncremental(!isFullDataFeed);
				catalogFeed.setName(this.siteName);
				this.seanURL = "https:"
						+ getConfigTemplateManager().getThirdPartyURL(
								"scene7_url", BBBCoreConstants.THIRD_PARTY_URL);
				this.siteURL = "https://"
						+ (String) ((getSiteManager().getSite(this.siteName))
								.getPropertyValue("productionURL"));
				this.marshalCategories(isFullDataFeed, feedObjFactory,
						catalogFeed);
				this.marshalProduct(isFullDataFeed, feedObjFactory, catalogFeed);
				this.marshalBrand(isFullDataFeed, feedObjFactory, catalogFeed);
				getMarshalFile(catalogFeed, feedObjFactory,
						siteConfigurationCode);
			} catch (final BBBBusinessException e) {
				status = false;
				logError("BBBBusinessException error occured while marshalling the BV feed", e);
			} catch (final BBBSystemException e) {
				status = false;
				logError("BBBSystemException error occured while marshalling the BV feed", e);
			} catch (final RepositoryException e) {
				status = false;
				logError("RepositoryException error occured while marshalling the BV feed", e);
			} finally {
				getFeedTools().updateRepository(getTypeOfFeed(),
						schedulerStartDate, isFullDataFeed, status);
//				getFeedTools().getSiteContextManager().clearSiteContextStack();
			}
		}
		getFeedTools().clearCachedCatalogItems();
	}
	
	protected void loadCatalogData(final boolean isFullDataFeed,final Timestamp lastModDate) {
		
		try {
			getFeedTools().getCatalogItemsForFeedGeneration(isFullDataFeed, lastModDate, BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
			getFeedTools().getCatalogItemsForFeedGeneration(isFullDataFeed, lastModDate, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
		} catch (final BBBSystemException e) {
			logError("BBBSystemException error occured while loading catalog data for BV feed", e);
		}
	}


	/**
	 * The marshal creates marshaled xml file for category feed. the xsd
	 * corresponding to category feed is category.xsd
	 * 
	 * @param isFullDataFeed
	 * @param catalogFeedRoot 
	 * @param catalogFeed 
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public void marshalCategories(final boolean isFullDataFeed,
			final ObjectFactory catalogFeedRoot,final FeedType catalogFeed) {

		logDebug("BBBMarketingFeedsMarshaller Method : marshallCategory ");
		logDebug("is full feed required " + isFullDataFeed);
		List<CategoryVO> categoryVOList = null;
		try {
			categoryVOList = getFeedTools().getCategoryDetails(isFullDataFeed,
					this.getLastModDate(), this.siteName);
		} catch (final BBBSystemException e) {
			logInfo("BBBSystemException error occured while retriving the Categories"
					+ e);
		} catch (final BBBBusinessException e) {
			logInfo("BBBBusinessException error occured while retriving the Categories"
					+ e);
		}
		populateCategoryElement(catalogFeedRoot, catalogFeed, categoryVOList);
	}

	/**
	 * The method populates the JAXB classes for category feed with data from
	 * repository
	 * @param catalogFeedRoot 
	 * @param catalogFeed 
	 * @param categoryVOList 
	 * 
	 * @param isFullDataFeed
	 * @param categoryVO
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public void populateCategoryElement(final ObjectFactory catalogFeedRoot,
			final FeedType catalogFeed,final List<CategoryVO> categoryVOList) {

		logDebug("BBBMarketingFeedsMarshaller Method : populateCategoryFeed");
		final CategoriesType categorys = catalogFeedRoot.createCategoriesType();
		final List<CategoryType> jaxbCatDetailList = new ArrayList<CategoryType>();

		if (categoryVOList != null) {

			for (int i = 0; i < categoryVOList.size(); i++) {
				final CategoryType jaxbCategory = new CategoryType();
				final CategoryVO categoryVO = categoryVOList.get(i);
				jaxbCategory.setExternalId(categoryVO.getCategoryId());
				logDebug(i
						+ "th category id that has modified since last feed :"
						+ categoryVO.getCategoryId());

				String categoryName = categoryVO.getCategoryName();
				
				if(this.siteCategoryNameMap !=null){
					for (final Map.Entry<String, String> entry : this.siteCategoryNameMap.entrySet()) {
	
						final String categoryId = entry.getKey();
						final String rootCategoryName = entry.getValue();
						if(categoryId.equalsIgnoreCase(categoryVO.getCategoryId())){
							categoryName = rootCategoryName;
							break;
						}
					}
				}
				logDebug("categoryId is  " + categoryVO.getCategoryId());
				jaxbCategory.setName(categoryName);


			
				jaxbCategory
						.setCategoryPageUrl(categoryVO.getCategoryPageUrl());
				final List<String> parentCategories = categoryVO
						.getParentCategories();
				if (parentCategories != null && !(parentCategories.isEmpty())) {
					for (final String parentCatId : parentCategories) {
						jaxbCategory.setParentExternalId(parentCatId);
					}
				}
				jaxbCategory.setCategoryPageUrl(this.siteURL
						+ categoryVO.getCategoryPageUrl());
				if (categoryVO.getCategoryImage() != null) {
					String categoryImageUrl = categoryVO.getCategoryImage();
					if(categoryImageUrl.contains(" ")){
						categoryImageUrl = categoryImageUrl.replaceAll(" ", "%20");
					}
					jaxbCategory.setImageUrl(getConfigTemplateManager().getThirdPartyURL("scene7_url", BBBCoreConstants.THIRD_PARTY_URL)+"/"+categoryImageUrl);
				}
				jaxbCatDetailList.add(jaxbCategory);
			}
		}
		logDebug("Categories list size : " + jaxbCatDetailList.size());
		categorys.getCategory().addAll(jaxbCatDetailList);
		catalogFeed.setCategories(categorys);
	}

	/**
	 * The marshal creates marshaled xml file for product feed.
	 * 
	 * @param isFullDataFeed
	 * @param catalogFeedRoot 
	 * @param catalogFeed 
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public void marshalProduct(final boolean isFullDataFeed,
			final ObjectFactory catalogFeedRoot,final FeedType catalogFeed) {

		logDebug("BBBMarketingFeedsMarshaller Method:marshallProduct for site"
				+ this.siteName);
		Map<String, ProductVO> productVOList = null;
		try {
			productVOList = getFeedTools().getProductDetails(isFullDataFeed,
					this.getLastModDate(), this.siteName);
		} catch (final BBBSystemException e) {
			logError("BBBSystemException error occured while retriving the Products"
					+ e);
		} catch (final BBBBusinessException e) {
			logError("BBBBusinessException error occured while retriving the Products"
					+ e);
		}
		populateProductElement(catalogFeedRoot, catalogFeed, productVOList,
				this.siteName);
	}

	/**
	 * this method populates the JAXB classes for product feed with data from
	 * repository sent through ProductVO. The empty strings are added in case
	 * there is no data in the repository for the corresponding property.This is
	 * so that as per the requirement 1. For Null/Optional Values - when they
	 * are not present feed should include empty tags rather than no tag. JAXB
	 * will not generate nodes when null object is added
	 * @param catalogFeedRoot 
	 * @param catalogFeed 
	 * @param products 
	 * @param siteName 
	 * 
	 * @param isFullDataFeed
	 * @param productFeed
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public void populateProductElement(final ObjectFactory catalogFeedRoot,
			final FeedType catalogFeed,final Map<String, ProductVO> products,final String siteName) {

		logDebug("BBBMarketingFeedsMarshaller populateProductFeed for site"
				+ siteName);
		final ProductsType productsEle = catalogFeedRoot.createProductsType();
		final List<ProductType> jaxbProdDetailList = new ArrayList<ProductType>();
		if (products != null) {
			final Iterator<ProductVO> iterator = products.values().iterator();
			while (iterator.hasNext()) {
				final ProductType jaxbProduct = catalogFeedRoot
						.createProductType();
				final ProductVO productVO = iterator.next();

				if(productVO.isBvRemoved())
					jaxbProduct.setRemoved(productVO.isBvRemoved());
				else
					jaxbProduct.setDisabled(productVO.getDisabled());
				jaxbProduct.setExternalId(productVO.getProductId());
				jaxbProduct.setName(productVO.getName());
				
					try{
						Map<String, CategoryVO> categoryMap = this.getCatalogTools().getParentCategoryForProduct(productVO.getProductId(), siteName);
						if(!categoryMap.isEmpty()){
							if(null != categoryMap.get("0")){
                                String categoryId = categoryMap.get("0").getCategoryId();
							    if(null != categoryId){
								    jaxbProduct.setCategoryExternalId(categoryId);
							     }
						    }
						}
					}
					catch(BBBBusinessException e){
						this.logError("Product is not available in repository for productId : " + productVO.getProductId());
					}
					catch(BBBSystemException e){
						this.logError("Unable to retrieve data from repository for productId : " + productVO.getProductId());
					}

				jaxbProduct.setProductPageUrl(this.siteURL
						+ productVO.getProductPageUrl());
				if (productVO.getImageUrl() != null) {
					String productImageUrl = productVO.getImageUrl();
					if(productImageUrl.contains(" ")){
						productImageUrl = productImageUrl.replaceAll(" ", "%20");
					}
					jaxbProduct.setImageUrl(this.seanURL +"/"+ productImageUrl);
				}
				
				// Added for BBBSL-1886. Added UPC/ EAN data to BV feeds.
				if(!StringUtils.isEmpty(productVO.getUpcCode())) {
					final UpcsType upcsType = catalogFeedRoot.createUpcsType();
					final List<String> upcs = new ArrayList<String>();
					upcs.add(productVO.getUpcCode());
					upcsType.getUPC().addAll(upcs);
					jaxbProduct.setUPCs(upcsType);
				}
				
				if(!StringUtils.isEmpty(productVO.getEan())) {
					final EansType eansType = catalogFeedRoot.createEansType();
					final List<String> eans = new ArrayList<String>();
					eans.add(productVO.getEan());
					eansType.getEAN().addAll(eans);
					jaxbProduct.setEANs(eansType);
				}
				// END BBBSL-1886.
				
				jaxbProduct.setDescription(productVO.getShortDescription());
				jaxbProduct.setBrandExternalId(productVO.getBrandId());
				//Start BBBSL-1887
				final AttributesType jaxAttributes=new AttributesType();
				if(productVO.getDepartment()!=null) {
	                final AttributeType jaxbAttribute = new AttributeType();
	                jaxbAttribute.setId(DEPARTMENT1);
	                final OptionalLocalizedStringType deptvalue= new OptionalLocalizedStringType();
	                deptvalue.setValue(productVO.getDepartment());
	                jaxbAttribute.getValue().add(0, deptvalue);
	                jaxAttributes.getAttribute().add(jaxbAttribute);
				}
				if(productVO.isCollection()){
					final AttributeType jaxbAttribute_expand = new AttributeType();
					final OptionalLocalizedStringType feExpand = new OptionalLocalizedStringType();
                	feExpand.setValue(BBBCatalogConstants.BAZAARVOICE_FAMILY_EXPAND_PREFIX + productVO.getProductId());
                	jaxbAttribute_expand.setId(BBBCatalogConstants.BAZAARVOICE_FAMILY_EXPAND_ID);
                	jaxbAttribute_expand.getValue().add(0, feExpand);
                	jaxAttributes.getAttribute().add(jaxbAttribute_expand);
                	final AttributeType jaxbAttribute_family = new AttributeType();
                	final OptionalLocalizedStringType feFamily = new OptionalLocalizedStringType();
                	feFamily.setValue(productVO.getProductId());
                	jaxbAttribute_family.setId(BBBCatalogConstants.BAZAARVOICE_FAMILY_ID);
                	jaxbAttribute_family.getValue().add(feFamily);
                	jaxAttributes.getAttribute().add(jaxbAttribute_family);
                }
                if(!BBBUtility.isCollectionEmpty(productVO.getCollectionParentProductIds())){
                	for(String parentProductId: productVO.getCollectionParentProductIds()){
                		final AttributeType jaxbAttribute_family = new AttributeType();
	                	final OptionalLocalizedStringType feExpand = new OptionalLocalizedStringType();
	                	feExpand.setValue(parentProductId);
	                	jaxbAttribute_family.setId(BBBCatalogConstants.BAZAARVOICE_FAMILY_ID);
	                	jaxbAttribute_family.getValue().add(feExpand);
	                	jaxAttributes.getAttribute().add(jaxbAttribute_family);
                	}
                }
                if(!jaxAttributes.getAttribute().isEmpty())
                	jaxbProduct.setAttributes(jaxAttributes);
				 //End BBBSL-1887
				jaxbProdDetailList.add(jaxbProduct);
			}
			logDebug("Products list size : " + jaxbProdDetailList.size());
			productsEle.getProduct().addAll(jaxbProdDetailList);
		}
		catalogFeed.setProducts(productsEle);
	}

	/**
	 * The marshal creates marshaled xml file for product feed.
	 * 
	 * @param isFullDataFeed
	 * @param catalogFeedRoot 
	 * @param catalogFeed 
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public void marshalBrand(final boolean isFullDataFeed,
			final ObjectFactory catalogFeedRoot,final FeedType catalogFeed)
			throws BBBSystemException, BBBBusinessException {

		logDebug("BBBMarketingFeedsMarshaller Method:marshalBrand for site:- "
				+ this.siteName);
		List<BrandVO> brandVOList = null;
		try {
			brandVOList = getFeedTools().getBrandDetails(isFullDataFeed,
					this.getLastModDate(), this.siteName);
		} catch (final BBBSystemException e) {
			logError("BBBSystemException error occured while retriving the Brands"
					+ e);
		} catch (final BBBBusinessException e) {
			logError("BBBBusinessException error occured while retriving the Brands"
					+ e);
		}
		populateBrandElement(catalogFeedRoot, catalogFeed, brandVOList);
	}

	/**
	 * @param catalogFeedRoot
	 * @param catalogFeed
	 * @param brandVOList
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public void populateBrandElement(final ObjectFactory catalogFeedRoot,
			final FeedType catalogFeed,final List<BrandVO> brandVOList)
			throws BBBSystemException, BBBBusinessException {

		logDebug("BBBMarketingFeedsMarshaller populateBrandElement for site:- "
				+ this.siteName);
		final BrandsType brandssEle = catalogFeedRoot.createBrandsType();
		if (brandVOList != null) {
			final List<BrandType> jaxbBrandDetailList = new ArrayList<BrandType>();
			final Iterator<BrandVO> iterator = brandVOList.iterator();
			while (iterator.hasNext()) {
				final BrandType jaxbBrand = catalogFeedRoot.createBrandType();
				final BrandVO brandVO = iterator.next();
				// Add Brand Attributes
				logDebug("BBBMarketingFeedsMarshaller.populateBrandElement adding brand to Brands Element: "
						+ brandVO.getBrandName());
				jaxbBrand.setExternalId(brandVO.getBrandId());
				jaxbBrand.setName(brandVO.getBrandName());
				jaxbBrandDetailList.add(jaxbBrand);
			}
			logDebug("Brands list size after adding to Brands Elment: "
					+ jaxbBrandDetailList.size());
			brandssEle.getBrand().addAll(jaxbBrandDetailList);
		}
		catalogFeed.setBrands(brandssEle);
	}

	/**
	 * The method creates a JAXB marshaled xml file after data is populated in
	 * JAXB classes for category feed
	 * @param catalogFeed 
	 * @param feedObjFactory 
	 * @param siteConfigurationCode 
	 * 
	 * @param categoryFeed
	 * @param catergoryObject
	 * @param indexOfFile
	 *            no of the file that is created for the feed
	 * @return true if marshaling is successful else return false
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public boolean getMarshalFile(final FeedType catalogFeed,
			final ObjectFactory feedObjFactory,
			final String siteConfigurationCode) throws BBBBusinessException,
			BBBSystemException {
		logDebug("BBBMarketingFeedsMarshaller Method : getMarshalFile ");
		boolean status = true;
		try {
			final JAXBElement<FeedType> feedRoot = feedObjFactory
					.createFeed(catalogFeed);
			final StringWriter stringWriter = new StringWriter();
			final JAXBContext context = JAXBContext.newInstance(FeedType.class);
			final Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			// Fix for defect BBBSL-2325
			marshaller.setProperty(CharacterEscapeHandler.class.getName(),
	                new BBBJAXBCharacterEscapeHandler());
			marshaller.marshal(feedRoot, stringWriter);
			final String categoryXml = stringWriter.getBuffer().toString();
			
			List<String> feedConfigPath = null;
			List<String> feedFolder = null;
			List<String> siteConfigPath = null;
			final String pathSeparator = File.separator;
			feedConfigPath = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE, BBBCoreConstants.SCHEDULED_FEEDS_PATH_CONFIG_KEY);
			if(feedConfigPath == null 
					 || feedConfigPath.isEmpty()) {
				throw new BBBBusinessException(BBBCoreConstants.SCHEDULED_FEEDS_PATH_CONFIG_KEY,BBBCoreConstants.SCHEDULED_FEEDS_PATH_CONFIG_KEY+" configuration missing for ");
			}
			siteConfigPath = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE, siteConfigurationCode);
			if(siteConfigPath == null 
					 || siteConfigPath.isEmpty()) {
				throw new BBBBusinessException(BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE,siteConfigurationCode+" configuration missing in "+BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE +" configurations");
			}			
			
			feedFolder = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE, this.getCatalogFeedFilePath());
			if (feedFolder == null || feedFolder.isEmpty()) {
				throw new BBBBusinessException(BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE,this.getCatalogFeedFilePath()+" configuration missing for "+getTypeOfFeed());				
			}
			final String feedPath = feedConfigPath.get(0)+pathSeparator+siteConfigPath.get(0)+pathSeparator+feedFolder.get(0);			
			final List<String> configFileName = getCatalogTools().getAllValuesForKey(BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE, this.catalogFeedFileName.get(siteConfigurationCode));
			String fileName = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
			if (configFileName == null 
					|| configFileName.isEmpty()) {
				fileName = getTypeOfFeed()+"-"+fileName;
			}
			else {
				fileName =configFileName.get(0).replaceAll("yyyymmdd", fileName);
			}
			logDebug("file created for BVCatalogFeed:" + feedPath+ fileName);
			getFeedTools().generateExceptionData(feedPath, siteConfigPath.get(0)+"-"+getTypeOfFeed()+"-"+new SimpleDateFormat("yyyyMMdd").format(new java.util.Date())+".log");
			BBBUtility.writeFile(feedPath, fileName, categoryXml);
			getFeedTools().generateTriggerFile(feedPath, "go.txt", "go");
		} catch (final JAXBException e) {
			logError(
					LogMessageFormatter
							.formatMessage(null,
									"BBBMarketingFeedsMarshaller.getCatMarshalledFile() | BBBBusinessException "),
					e);
			status = false;
			throw new BBBBusinessException(BBBCatalogErrorCodes.JAXB_EXCEPTION,BBBCatalogErrorCodes.JAXB_EXCEPTION,
					e);
		}
		return status;
	}

	/**
	 * @return feedTools
	 */
	public BVCatalogFeedTools getFeedTools() {
		return this.feedTools;
	}

	/**
	 * @param feedTools
	 */
	public void setFeedTools(final BVCatalogFeedTools feedTools) {
		this.feedTools = feedTools;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}

	/**
	 * @param catalogTools
	 */
	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * @return lastModDate
	 */
	public Timestamp getLastModDate() {
		return this.lastModDate;
	}

	/**
	 * @param lastModDate
	 */
	public void setLastModDate(final Timestamp lastModDate) {
		this.lastModDate = lastModDate;
	}

	/**
	 * @return siteFeedConfiguration
	 */
	public Map<String, String> getSiteFeedConfiguration() {
		return this.siteFeedConfiguration;
	}

	/**
	 * @param siteFeedConfiguration
	 */
	public void setSiteFeedConfiguration(final Map<String, String> siteFeedConfiguration) {
		this.siteFeedConfiguration = siteFeedConfiguration;
	}

	/**
	 * @return feedRepository
	 */
	public MutableRepository getFeedRepository() {
		return this.feedRepository;
	}

	/**
	 * @param feedRepository
	 */
	public void setFeedRepository(final MutableRepository feedRepository) {
		this.feedRepository = feedRepository;
	}

	/**
	 * @return typeOfFeed
	 */
	public String getTypeOfFeed() {
		return this.typeOfFeed;
	}

	/**
	 * @param typeOfFeed
	 */
	public void setTypeOfFeed(final String typeOfFeed) {
		this.typeOfFeed = typeOfFeed;
	}

	/**
	 * @return catalogFeedFilePath
	 */
	public String getCatalogFeedFilePath() {
		return this.catalogFeedFilePath;
	}

	/**
	 * @param catalogFeedFilePath
	 */
	public void setCatalogFeedFilePath(final String catalogFeedFilePath) {
		this.catalogFeedFilePath = catalogFeedFilePath;
	}

	/**
	 * @return catalogFeedFileName
	 */
	public Map<String, String> getCatalogFeedFileName() {
		return this.catalogFeedFileName;
	}

	/**
	 * @param catalogFeedFileName
	 */
	public void setCatalogFeedFileName(final Map<String, String> catalogFeedFileName) {
		this.catalogFeedFileName = catalogFeedFileName;
	}

	/**
	 * @return configTemplateManager
	 */
	public ConfigTemplateManager getConfigTemplateManager() {
		return this.configTemplateManager;
	}

	/**
	 * @param configTemplateManager
	 */
	public void setConfigTemplateManager(final ConfigTemplateManager configTemplateManager) {
		this.configTemplateManager = configTemplateManager;
	}

	/**
	 * @return siteManager
	 */
	public SiteManager getSiteManager() {
		return this.siteManager;
	}

	/**
	 * @param siteManager
	 */
	public void setSiteManager(final SiteManager siteManager) {
		this.siteManager = siteManager;
	}
}