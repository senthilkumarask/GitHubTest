package com.bbb.certona.marshaller;

import java.io.StringWriter;
import java.sql.Timestamp;
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
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;

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

import com.bbb.certona.utils.CertonaCatalogFeedTools;
import com.bbb.certona.vo.CertonaCategoryVO;
import com.bbb.certona.vo.CertonaProductVO;
import com.bbb.certona.vo.CertonaSKUVO;
import com.bbb.certona.vo.CollectionChildProductAttr;
import com.bbb.certona.vo.Prop65TypeVO;
import com.bbb.certona.vo.RebatesDetails;
import com.bbb.certona.vo.SiteSpecificProductAttr;
import com.bbb.certona.vo.SiteSpecificSKUAttr;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.vo.BrandVO;
import com.bbb.commerce.catalog.vo.ImageVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCertonaConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.jaxb.certona.catalogfeed.categoryfeed.CategoryDetail;
import com.bbb.framework.jaxb.certona.catalogfeed.categoryfeed.CategoryType;
import com.bbb.framework.jaxb.certona.catalogfeed.categoryfeed.ChildCategoriesType;
import com.bbb.framework.jaxb.certona.catalogfeed.productfeed.BrandsType;
import com.bbb.framework.jaxb.certona.catalogfeed.productfeed.ChildProductType;
import com.bbb.framework.jaxb.certona.catalogfeed.productfeed.ChildSkuType;
import com.bbb.framework.jaxb.certona.catalogfeed.productfeed.ProductDetail;
import com.bbb.framework.jaxb.certona.catalogfeed.productfeed.ProductType;
import com.bbb.framework.jaxb.certona.catalogfeed.productfeed.SiteSpecificType;
import com.bbb.framework.jaxb.certona.catalogfeed.skufeed.Prop65Type;
import com.bbb.framework.jaxb.certona.catalogfeed.skufeed.SiteSKUSpecificType;
import com.bbb.framework.jaxb.certona.catalogfeed.skufeed.SkuAttributesType;
import com.bbb.framework.jaxb.certona.catalogfeed.skufeed.SkuDetail;
import com.bbb.framework.jaxb.certona.catalogfeed.skufeed.SkuType;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 * The class generates category , product and sku feed in the form of a xml file
 *
 * @author njai13
 *
 */
public class CertonaCatalogFeedMarshaller extends BBBGenericService implements ICertonaMarshaller {
	private static final String CERTONA_CATEGORY_FEED_CHECK_SUM = "CertonaCategoryFeed_CheckSum.txt";
	private static final String CERTONA_PRODUCT_FEED_CHECK_SUM = "CertonaProductFeed_CheckSum.txt";
	private static final String CERTONA_SKU_FEED_CHECK_SUM = "CertonaSkuFeed_CheckSum.txt";
	private static final String QUERY_ALL = "ALL";
	private CertonaCatalogFeedTools feedTools;
	private static final String RANGE =" RANGE ";
	private boolean catDetails;
	private boolean prodDetails;
	private boolean skuDetails;
	private MutableRepository certonaRepository;
	private Map<String, String> typeOfFeedMap = new HashMap<String, String>();
	private Map<String, String> sortValueMap = new HashMap<String, String>();
	private boolean repoCatlastModDate;
	private boolean repoProdlastModDate;
	private boolean repoSkulastModDate;
	private Timestamp catLastModDate = null;
	private Timestamp prodLastModDate = null;
	private Timestamp skuLastModDate = null;
	private Map<String, String> modeMap = new HashMap<String, String>();
	private String catFeedFilePath;
	private String prodFeedFilePath;
	private String catFeedFileName;
	private String prodFeedFileName;
	private String skuFeedFilePath;
	private String skuFeedFileName;
	private int catBatchSize;
	private int skuBatchSize;
	private int prodBatchSize;
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
	public CertonaCatalogFeedTools getFeedTools() {
		return this.feedTools;
	}

	/**
	 * @param feedTools
	 *          the feedTools to set
	 */
	public void setFeedTools(final CertonaCatalogFeedTools feedTools) {
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
	 * @return the prodFeedFileName
	 */
	public String getProdFeedFileName() {
		return this.prodFeedFileName;
	}

	/**
	 * @param prodFeedFileName
	 *          the prodFeedFileName to set
	 */
	public void setProdFeedFileName(final String prodFeedFileName) {
		this.prodFeedFileName = prodFeedFileName;
	}

	/**
	 * @return the catDetails
	 */
	public boolean isCatDetails() {
		return this.catDetails;
	}

	/**
	 * @param catDetails
	 *          the catDetails to set
	 */
	public void setCatDetails(final boolean catDetails) {
		this.catDetails = catDetails;
	}

	/**
	 * @return the prodDetails
	 */
	public boolean isProdDetails() {
		return this.prodDetails;
	}

	/**
	 * @param prodDetails
	 *          the prodDetails to set
	 */
	public void setProdDetails(final boolean prodDetails) {
		this.prodDetails = prodDetails;
	}

	/**
	 * @return the skuDetails
	 */
	public boolean isSkuDetails() {
		return this.skuDetails;
	}

	/**
	 * @param skuDetails
	 *          the skuDetails to set
	 */
	public void setSkuDetails(final boolean skuDetails) {
		this.skuDetails = skuDetails;
	}

	/**
	 * @return the repoCatlastModDate
	 */
	public boolean isRepoCatlastModDate() {
		return this.repoCatlastModDate;
	}

	/**
	 * @param repoCatlastModDate
	 *          the repoCatlastModDate to set
	 */
	public void setRepoCatlastModDate(final boolean repoCatlastModDate) {
		this.repoCatlastModDate = repoCatlastModDate;
	}

	/**
	 * @return the repoProdlastModDate
	 */
	public boolean isRepoProdlastModDate() {
		return this.repoProdlastModDate;
	}

	/**
	 * @param repoProdlastModDate
	 *          the repoProdlastModDate to set
	 */
	public void setRepoProdlastModDate(final boolean repoProdlastModDate) {
		this.repoProdlastModDate = repoProdlastModDate;
	}

	/**
	 * @return the repoSkulastModDate
	 */
	public boolean isRepoSkulastModDate() {
		return this.repoSkulastModDate;
	}

	/**
	 * @param repoSkulastModDate
	 *          the repoSkulastModDate to set
	 */
	public void setRepoSkulastModDate(final boolean repoSkulastModDate) {
		this.repoSkulastModDate = repoSkulastModDate;
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
	 * @return the catLastModDate
	 */
	public Timestamp getCatLastModDate() {
		return this.catLastModDate;
	}

	/**
	 * @param catLastModDate
	 *          the catLastModDate to set
	 */
	public void setCatLastModDate(final Timestamp catLastModDate) {
		this.catLastModDate = catLastModDate;
	}

	/**
	 * @return the prodLastModDate
	 */
	public Timestamp getProdLastModDate() {
		return this.prodLastModDate;
	}

	/**
	 * @param prodLastModDate
	 *          the prodLastModDate to set
	 */
	public void setProdLastModDate(final Timestamp prodLastModDate) {
		this.prodLastModDate = prodLastModDate;
	}

	/**
	 * @return the skuLastModDate
	 */
	public Timestamp getSkuLastModDate() {
		return this.skuLastModDate;
	}

	/**
	 * @param skuLastModDate
	 *          the skuLastModDate to set
	 */
	public void setSkuLastModDate(final Timestamp skuLastModDate) {
		this.skuLastModDate = skuLastModDate;
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
	 * @return the catBatchSize
	 */
	public int getCatBatchSize() {
		return this.catBatchSize;
	}

	/**
	 * @param catBatchSize
	 *          the catBatchSize to set
	 */
	public void setCatBatchSize(final int catBatchSize) {
		this.catBatchSize = catBatchSize;
	}

	/**
	 * @return the prodBatchSize
	 */
	public int getProdBatchSize() {
		return this.prodBatchSize;
	}

	/**
	 * @param prodBatchSize
	 *          the prodBatchSize to set
	 */
	public void setProdBatchSize(final int prodBatchSize) {
		this.prodBatchSize = prodBatchSize;
	}

	/**
	 * @return the catFeedFilePath
	 */
	public String getCatFeedFilePath() {
		return this.catFeedFilePath;
	}

	/**
	 * @param catFeedFilePath
	 *          the catFeedFilePath to set
	 */
	public void setCatFeedFilePath(final String catFeedFilePath) {
		this.catFeedFilePath = catFeedFilePath;
	}

	/**
	 * @return the prodFeedFilePath
	 */
	public String getProdFeedFilePath() {
		return this.prodFeedFilePath;
	}

	/**
	 * @param prodFeedFilePath
	 *          the prodFeedFilePath to set
	 */
	public void setProdFeedFilePath(final String prodFeedFilePath) {
		this.prodFeedFilePath = prodFeedFilePath;
	}

	/**
	 * @return the skuBatchSize
	 */
	public int getSkuBatchSize() {
		return this.skuBatchSize;
	}

	/**
	 * @param skuBatchSize
	 *          the skuBatchSize to set
	 */
	public void setSkuBatchSize(final int skuBatchSize) {
		this.skuBatchSize = skuBatchSize;
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
	 * @return the skuFeedFilePath
	 */
	public String getSkuFeedFilePath() {
		return this.skuFeedFilePath;
	}

	/**
	 * @param skuFeedFilePath
	 *          the skuFeedFilePath to set
	 */
	public void setSkuFeedFilePath(final String skuFeedFilePath) {
		this.skuFeedFilePath = skuFeedFilePath;
	}

	/**
	 * @return the skuFeedFileName
	 */
	public String getSkuFeedFileName() {
		return this.skuFeedFileName;
	}

	/**
	 * @param skuFeedFileName
	 *          the skuFeedFileName to set
	 */
	public void setSkuFeedFileName(final String skuFeedFileName) {
		this.skuFeedFileName = skuFeedFileName;
	}

	/**
	 * This method calls the respective marshal methods for category,product and
	 * sku. If any particular feed needs to be turned off change
	 * isXXXXXXOnlyDetails property to false e.g. if category feed is not required
	 * set isCategoryOnlyDetails to false
	 */
	@Override
	public void marshall(final boolean isFullDataFeed, final Timestamp schedulerStartDate) {
		
			this.logDebug("CertonaCatalogFeedMarshaller Method : marshal");
			this.logDebug("Feeds to be run Category:" + this.isCatDetails() + " Product: " + this.isProdDetails() + " sku :"
					+ this.isSkuDetails());
		

		if (this.isCatDetails()) {

			this.marshalCategory(isFullDataFeed, schedulerStartDate);

		}
		if (this.isProdDetails()) {

			this.marshalProduct(isFullDataFeed, schedulerStartDate);

		}
		if (this.isSkuDetails()) {
			this.marshalSku(isFullDataFeed, schedulerStartDate);

		}

	}

	/**
	 * The marshal creates marshaled xml file for category feed. the xsd
	 * corresponding to category feed is category.xsd
	 *
	 * @param isFullDataFeed
	 */
	public void marshalCategory(final boolean isFullDataFeed, final Timestamp schedulerStartDate) {
		int totalCount=0;
		int count=0;
		int i=0;
		String rqlQuery=null;
		List<CertonaCategoryVO> categoryVOList=null;
		boolean status = true;
		
		this.logDebug("CertonaCatalogFeedMarshaller Method : marshallCategory ");
		this.logDebug("is full feed required " + isFullDataFeed);
		
		try {
			rqlQuery=QUERY_ALL;
			if (this.isRepoCatlastModDate() && !isFullDataFeed) {
				
					this.logDebug("Get last modified date from repository");
				
				this.catLastModDate = this.getLastModifiedDate("category");
				if(this.catLastModDate!=null){
				final String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz").format(this.catLastModDate);
				rqlQuery=BBBCertonaConstants.LAST_MODIFIED_DATE_CERTONA_PROPERTY_NAME+" > datetime(\""+date+"\")";
				}
				
					this.logDebug("last modified date for category: " + this.getCatLastModDate());
				
			}
			String rqlQueryRange=rqlQuery +" ORDER BY id "+ RANGE+ totalCount + "+" + this.getCatBatchSize();
			categoryVOList = this.getFeedTools().getCategoryDetails(isFullDataFeed,
					this.getCatLastModDate(),rqlQueryRange);
			if((categoryVOList==null) || categoryVOList.isEmpty()){
			return;
			}
			totalCount=categoryVOList.size();
			count=totalCount;
			status = this.populateCategoryFeed(isFullDataFeed, categoryVOList, i);

			while(count==this.getCatBatchSize()){
				categoryVOList=null;
				rqlQueryRange=rqlQuery +" ORDER BY id "+ RANGE+ totalCount + "+" + this.getCatBatchSize();
				categoryVOList = this.getFeedTools().getCategoryDetails(isFullDataFeed,
						this.getCatLastModDate(),rqlQueryRange);
				if((categoryVOList==null) || categoryVOList.isEmpty()){
					return;
				}
				i++;
				status = this.populateCategoryFeed(isFullDataFeed, categoryVOList, i);
				count=categoryVOList.size();
				totalCount = totalCount +count;
			}
			final List<String> configPath = this.getFeedTools().getCatalogTools()
					.getContentCatalogConfigration(this.getCatFeedFilePath());

			if ((configPath != null) && !configPath.isEmpty()) {
				
					this.logDebug("Categories processed : "+String.valueOf(totalCount));
				
				BBBUtility.writeFile(configPath.get(0), CERTONA_CATEGORY_FEED_CHECK_SUM, "Categories Processed :" +String.valueOf(totalCount));

			}
		} catch (final BBBBusinessException e1) {
			
				this.logError(LogMessageFormatter.formatMessage(null,
						"CertonaCatalogFeedMarshaller.marshallCategory() | BBBBusinessException "), e1);
			
			status = false;
		} catch (final BBBSystemException e) {
			
				this.logError(LogMessageFormatter.formatMessage(null,
						"CertonaCatalogFeedMarshaller.marshallCategory() | BBBSystemException "), e);
			
			status = false;
		} finally {
			this.updateCertonaRepository(schedulerStartDate, isFullDataFeed, "category", status);
		}
	}

	/**
	 * The method creates a JAXB marshaled xml file after data is populated in
	 * JAXB classes for category feed
	 *
	 * @param categoryFeed
	 * @param catergoryObject
	 * @param indexOfFile
	 *          no of the file that is created for the feed
	 * @return true if marshaling is successful else return false
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public boolean getCatMarshalFile(final com.bbb.framework.jaxb.certona.catalogfeed.categoryfeed.CategoryType categoryFeed,
			final com.bbb.framework.jaxb.certona.catalogfeed.categoryfeed.ObjectFactory catergoryObject, final int indexOfFile)
					throws BBBBusinessException, BBBSystemException {
		
			this.logDebug("CertonaCatalogFeedMarshaller Method : getCatMarshalledFile ");
		
		boolean status = true;
		try {
			final JAXBElement<CategoryType> categoryRoot = catergoryObject.createCategory(categoryFeed);
			final StringWriter stringWriter = new StringWriter();
			final JAXBContext context = JAXBContext.newInstance(CategoryType.class);
			final Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(categoryRoot, stringWriter);
			final String categoryXml = stringWriter.getBuffer().toString();

			final List<String> configPath = this.getFeedTools().getCatalogTools()
					.getContentCatalogConfigration(this.getCatFeedFilePath());
			final List<String> configFile = this.getFeedTools().getCatalogTools()
					.getContentCatalogConfigration(this.getCatFeedFileName());
			if ((configPath != null) && !configPath.isEmpty() && (configFile != null) && !configFile.isEmpty()) {
				final String fileName = this.getFileName(configFile.get(0), indexOfFile);
				
					this.logDebug(indexOfFile + "th file created for category:" + configPath.get(0) + fileName);
				
				BBBUtility.writeFile(configPath.get(0), fileName, categoryXml);

			}
		} catch (final JAXBException e) {
			
				this.logError(LogMessageFormatter.formatMessage(null,
						"CertonaCatalogFeedMarshaller.getCatMarshalledFile() | BBBBusinessException "), e);
			
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
	 * @param categoryVOList
	 * @param indexOfFile
	 *          file no for which data is populated
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public boolean populateCategoryFeed(final boolean isFullDataFeed, final List<CertonaCategoryVO> categoryVOList, final int indexOfFile)
			throws BBBBusinessException, BBBSystemException {
		
			this.logDebug("CertonaCatalogFeedMarshaller Method : populateCategoryFeed");
			this.logDebug("no of categories to be sent in feed in" + indexOfFile + " th file is:" + categoryVOList.size());
		

		final com.bbb.framework.jaxb.certona.catalogfeed.categoryfeed.ObjectFactory catergoryObject = new com.bbb.framework.jaxb.certona.catalogfeed.categoryfeed.ObjectFactory();
		final com.bbb.framework.jaxb.certona.catalogfeed.categoryfeed.CategoryType categoryRoot = catergoryObject
				.createCategoryType();
		final List<CategoryDetail> jaxbCatDetailList = new ArrayList<CategoryDetail>();
		CategoryDetail jaxbCategory=null;
		if ((categoryVOList != null) && !categoryVOList.isEmpty()) {
			for (int i = 0; i < categoryVOList.size(); i++) {

				jaxbCategory= new CategoryDetail();
				final CertonaCategoryVO categoryVO = categoryVOList.get(i);
				jaxbCategory.setCategoryId(categoryVO.getCategoryId());
				this.logDebug(i + "th category id that has modified since last feed :" + categoryVO.getCategoryId());
				
				jaxbCategory.setCategoryKeywords(categoryVO.getCategoryKeywords() == null ? "" : categoryVO
						.getCategoryKeywords().toString());
				jaxbCategory.setCreationDate(categoryVO.getCreationDate() == null ? null : this.getXMLDate(categoryVO.getCreationDate()));
				jaxbCategory.setDescription(categoryVO.getShortDesc() == null ? "" : categoryVO.getShortDesc());
				jaxbCategory.setLongDescription(categoryVO.getLongDesc() == null ? "" : categoryVO.getLongDesc());
				jaxbCategory.setDisplayName(categoryVO.getCategoryName() == null ? "" : categoryVO.getCategoryName());
				jaxbCategory.setIsActive(categoryVO.isActive());
				jaxbCategory.setIsCollegeCategory(categoryVO.getIsCollege() == null ? false : categoryVO.getIsCollege());
				jaxbCategory
				.setNodeType(categoryVO.getCategoryDisplayType() == null ? "" : categoryVO.getCategoryDisplayType());
				jaxbCategory.setShopGuide(categoryVO.getGuideId() == null ? "" : categoryVO.getGuideId());
				jaxbCategory.setSmallImageURL(categoryVO.getCategoryImage() == null ? "" : categoryVO.getCategoryImage());
				jaxbCategory.setThumbNailImageURL(categoryVO.getThumbNailPath() == null ? "" : categoryVO.getThumbNailPath());
				// add sub categories
				final List<String> subCatList = categoryVO.getSubCatIds();
				final ChildCategoriesType childCats = catergoryObject.createChildCategoriesType();
				childCats.getSubCategoryId().addAll(subCatList);
				jaxbCategory.setChildCategoryIds(childCats);
				// add sub products
				final List<String> childProdList = categoryVO.getChildProducts();
				final com.bbb.framework.jaxb.certona.catalogfeed.categoryfeed.ChildProductType childProds = catergoryObject
						.createChildProductType();
				childProds.getChildProductId().addAll(childProdList);
				jaxbCategory.setChildProductIds(childProds);
				final com.bbb.framework.jaxb.certona.catalogfeed.categoryfeed.SiteType siteType = catergoryObject.createSiteType();
				siteType.getSiteId().addAll(categoryVO.getAssocSites());
				jaxbCategory.setAssociatedSites(siteType);
				jaxbCatDetailList.add(jaxbCategory);
			
					this.logInfo("Category written to feed:"+ categoryVO.getCategoryId());
				
			}
		}
		categoryRoot.getCategoryAttributes().addAll(jaxbCatDetailList);
		
			this.logDebug(" all category properties set.Marshall of category feed file start");
		
		return this.getCatMarshalFile(categoryRoot, catergoryObject, indexOfFile);
	}

	/**
	 * The marshal creates marshaled xml file for product feed. the xsd
	 * corresponding to category feed is product.xsd
	 *
	 * @param isFullDataFeed
	 * @throws BBBSystemException
	 */
	public void marshalProduct(final boolean isFullDataFeed, final Timestamp schedulerStartDate) {
		int totalProductCount=0;
		int indexOfFile=0;
		String rqlQuery=null;
		List<RepositoryItem> productRepoItems = null;
		List<RepositoryItem> productRepoItemsSubList = null;
		boolean status = true;
		
		this.logDebug("CertonaCatalogFeedMarshaller Method : marshalProduct ");
		this.logDebug("is full feed required " + isFullDataFeed);
		
		try {
			rqlQuery=QUERY_ALL;
			
			if (this.isRepoProdlastModDate() && !isFullDataFeed) {
				
					this.logDebug("Get last modified date from repository");
				
				this.prodLastModDate = this.getLastModifiedDate("product");
				if(this.prodLastModDate!=null){
				final String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz").format(this.prodLastModDate);
				this.logDebug(" Formatted last modified date with time zone for product feed:" + date);
				rqlQuery=BBBCertonaConstants.LAST_MODIFIED_DATE_CERTONA_PROPERTY_NAME+" > datetime(\""+date+"\")";
				}
				this.logDebug("last modified date for product feed:" + this.getProdLastModDate());
			}
			
			//fetching count of repository items
			totalProductCount = this.getFeedTools().getCountOfItemsForFullFeed(BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR,rqlQuery);
			
			final List<String> configPath = this.getFeedTools().getCatalogTools()
					.getContentCatalogConfigration(this.getProdFeedFilePath());
			
			if(totalProductCount==0){
				if ((configPath != null) && !configPath.isEmpty()) {
					
						this.logDebug("CertonaCatalogFeedMarshaller.marshalProduct() : Products Processed : "+String.valueOf(totalProductCount));
					
					BBBUtility.writeFile(configPath.get(0), CERTONA_PRODUCT_FEED_CHECK_SUM, "Products Processed :" +String.valueOf(totalProductCount));

				}
				return;
			}
			String rqlQueryRange=null;
			int currentCount=0;
			int fileWriteCount = 0;

			while(totalProductCount - currentCount >= this.getProdBatchSize()){
				productRepoItems=null;
				rqlQueryRange=rqlQuery +" ORDER BY id " + RANGE+ currentCount + "+" + this.getProdBatchSize() ;
				this.logDebug("RQL Query to fetch the products list :"+rqlQueryRange);
				long queryStartTime = System.currentTimeMillis();
				productRepoItems = this.getFeedTools().getProductDetails(isFullDataFeed, this.getProdLastModDate(),rqlQueryRange);
				long queryEndTime = System.currentTimeMillis();
				this.logDebug("CertonaCatalogFeedMarshaller.marshalProduct : rql query in loop took time " + (queryEndTime - queryStartTime) + " ms ");
				if((productRepoItems==null) || productRepoItems.isEmpty()){
					if ((configPath != null) && !configPath.isEmpty()) {
							
								this.logDebug("CertonaCatalogFeedMarshaller.marshalProduct() : Products Processed : "+String.valueOf(totalProductCount));
							
							BBBUtility.writeFile(configPath.get(0), CERTONA_PRODUCT_FEED_CHECK_SUM, "Products Processed :" +String.valueOf(totalProductCount));
					}
					return;
				}
				
				int totalCountInBatch = productRepoItems.size();
				this.logDebug("CertonaCatalogFeedMarshaller.marshalProduct() : totalCountInBatch is " + totalCountInBatch);
				
				while(totalCountInBatch-fileWriteCount>=fileWriteBatchSize){
					productRepoItemsSubList = productRepoItems.subList(fileWriteCount,fileWriteCount + fileWriteBatchSize);
					status = this.populateProductFeed(this.getProductVO(productRepoItemsSubList), indexOfFile);		
					indexOfFile++;
					fileWriteCount+=fileWriteBatchSize;
				}
				if (totalCountInBatch-fileWriteCount < fileWriteBatchSize && totalCountInBatch-fileWriteCount > 0 ){
					productRepoItemsSubList = productRepoItems.subList(fileWriteCount,totalCountInBatch);
					status = this.populateProductFeed(this.getProductVO(productRepoItemsSubList), indexOfFile);
					indexOfFile++;
				}
				
				currentCount+=this.getProdBatchSize();
				fileWriteCount = 0;
			}
			if (totalProductCount - currentCount < this.getProdBatchSize() && totalProductCount - currentCount > 0) {
				
				this.logDebug("Remaining products are less than queried batch size");
				
				rqlQueryRange=rqlQuery +" ORDER BY id "+ RANGE+ currentCount + "+" + (totalProductCount - currentCount);
				this.logDebug("rqlQueryRange is " +  rqlQueryRange);
				
				long queryStartTime = System.currentTimeMillis();
				productRepoItems = this.getFeedTools().getProductDetails(isFullDataFeed, this.getProdLastModDate(),rqlQueryRange);
				long queryEndTime = System.currentTimeMillis();
				this.logDebug("CertonaCatalogFeedMarshaller.marshalProduct : rql query in loop took time " + (queryEndTime - queryStartTime) + " ms ");
				
				int totalCountInBatch = productRepoItems.size();
				
				while(totalCountInBatch-fileWriteCount >= fileWriteBatchSize){
					productRepoItemsSubList = productRepoItems.subList(fileWriteCount,fileWriteCount + fileWriteBatchSize);
					status = this.populateProductFeed(this.getProductVO(productRepoItemsSubList), indexOfFile);		
					indexOfFile++;
					fileWriteCount+=fileWriteBatchSize;
				}
				if (totalCountInBatch-fileWriteCount < fileWriteBatchSize && totalCountInBatch-fileWriteCount > 0 ){
					productRepoItemsSubList = productRepoItems.subList(fileWriteCount,totalCountInBatch);
					status = this.populateProductFeed(this.getProductVO(productRepoItemsSubList), indexOfFile);
				}
			}
			if ((configPath != null) && !configPath.isEmpty()) {
			
					this.logDebug("CertonaCatalogFeedMarshaller.marshalProduct() : Products Processed : "+String.valueOf(totalProductCount));
				
				BBBUtility.writeFile(configPath.get(0), CERTONA_PRODUCT_FEED_CHECK_SUM, "Products Processed :" +String.valueOf(totalProductCount));
			}
		} catch (final BBBBusinessException e1) {
			
				this.logError(LogMessageFormatter.formatMessage(null,
						"CertonaCatalogFeedMarshaller.marshalProduct() | BBBBusinessException "), e1);
			
			status = false;
		} catch (final BBBSystemException e) {
			
				this.logError(
						LogMessageFormatter.formatMessage(null, "CertonaCatalogFeedMarshaller.marshalProduct() | BBBSystemException "),
						e);
			
			status = false;
		} finally {
			this.updateCertonaRepository(schedulerStartDate, isFullDataFeed, "product", status);
		}


	}
	/**
	 * The method prepares a list of CertonaproductVo for each product Repository Item
	 * in  the list of items sent
	 * @param subProdRepoList
	 * @return
	 */
	public List<CertonaProductVO> getProductVO(final List<RepositoryItem> subProdRepoList) {
		
		long createProdVOStartTime = System.currentTimeMillis();
		
		final List<CertonaProductVO> list = new ArrayList<CertonaProductVO>();
		if ((subProdRepoList != null) && !subProdRepoList.isEmpty()) {
			for (int i = 0; i < subProdRepoList.size(); i++) {
				try {
					list.add(this.getFeedTools().populateProductVO(subProdRepoList.get(i)));
				} catch (final BBBBusinessException e) {
						this.logError(LogMessageFormatter.formatMessage(null,
								"CertonaCatalogFeedMarshaller.getProductVO() | BBBBusinessException "), e);
				} catch (final BBBSystemException e) {
						this.logError(
								LogMessageFormatter.formatMessage(null, "CertonaCatalogFeedMarshaller.getProductVO() | BBBSystemException "), e);
				} catch (final Exception e) {
					this.logError(
							LogMessageFormatter.formatMessage(null, "CertonaCatalogFeedMarshaller.getProductVO() | Exception "), e);
				}
			}
		}
		
		long createProdVOEndTime = System.currentTimeMillis();
		this.logDebug("CertonaCatalogFeedMarshaller.getProductVO : Creating PRODUCT VO took time " + (createProdVOEndTime - createProdVOStartTime) + " ms ");
		return list;
	}

	/**
	 * The marshall creates marshalled xml file for sku feed. the xsd
	 * corresponding to category feed is sku.xsd
	 *
	 * @param isFullDataFeed
	 */
	public void marshalSku(final boolean isFullDataFeed, final Timestamp schedulerStartDate) {
		int totalSKUCount = 0;
		int indexOfFile=0;
		String rqlQuery=null;
		List<RepositoryItem> skuRepoItems = null;
		List<RepositoryItem> skuRepoItemsSubList = null;
		boolean status = true;
		
			this.logDebug("CertonaCatalogFeedMarshaller Method : marshalSku ");
			this.logDebug("is full feed required " + isFullDataFeed);
		
		try {
			rqlQuery=QUERY_ALL;
			
			if (this.isRepoSkulastModDate() && !isFullDataFeed) {
					this.logDebug("Get last modified date from repository");
				this.skuLastModDate = this.getLastModifiedDate("sku");
				if(this.skuLastModDate!=null){
				final String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz").format(this.skuLastModDate);
				rqlQuery=BBBCertonaConstants.LAST_MODIFIED_DATE_CERTONA_PROPERTY_NAME+" > datetime(\""+date+"\")";
				}
				this.logDebug("last modified date for sku: " + this.getSkuLastModDate());
			}
			//fetching count of repository items
			totalSKUCount = this.getFeedTools().getCountOfItemsForFullFeed(BBBCatalogConstants.SKU_ITEM_DESCRIPTOR,rqlQuery);
			
			final List<String> configPath = this.getFeedTools().getCatalogTools()
					.getContentCatalogConfigration(this.getSkuFeedFilePath());
			
			if(totalSKUCount==0){
				
				if ((configPath != null) && !configPath.isEmpty()) {
					
					this.logDebug("CertonaCatalogFeedMarshaller.marshalSku() : SKU's Processed : "+String.valueOf(totalSKUCount));
					
					BBBUtility.writeFile(configPath.get(0), CERTONA_SKU_FEED_CHECK_SUM, "SKU's Processed :" +String.valueOf(totalSKUCount));
				}
				return;
			}
			String rqlQueryRange=null;
			int currentCount=0;
			int fileWriteCount = 0;
			
				while(totalSKUCount - currentCount >= this.getSkuBatchSize()){
					skuRepoItems=null;
					rqlQueryRange=rqlQuery +" ORDER BY id "+ RANGE+ currentCount + "+" + this.getSkuBatchSize();
					this.logDebug("rqlQueryRange is " +  rqlQueryRange);
					long queryStartTime = System.currentTimeMillis();
					skuRepoItems = this.getFeedTools().getSKUDetailsRepo(isFullDataFeed, this.getSkuLastModDate(),rqlQueryRange);
					long queryEndTime = System.currentTimeMillis();
					this.logDebug("rql query in loop took time " + (queryEndTime - queryStartTime) + " ms ");
					if((skuRepoItems==null) || skuRepoItems.isEmpty()){
						if ((configPath != null) && !configPath.isEmpty()) {
							
							this.logDebug("CertonaCatalogFeedMarshaller.marshalSku() : SKU's Processed : "+String.valueOf(totalSKUCount));
							
							BBBUtility.writeFile(configPath.get(0), CERTONA_SKU_FEED_CHECK_SUM, "SKU's Processed :" +String.valueOf(totalSKUCount));
						}
						return;
					}
					
					int totalCountInBatch = skuRepoItems.size();
					this.logDebug("totalCountInBatch is " + totalCountInBatch);
				
					while(totalCountInBatch-fileWriteCount>=fileWriteBatchSize){
						skuRepoItemsSubList = skuRepoItems.subList(fileWriteCount,fileWriteCount + fileWriteBatchSize);
						status = this.populateSkuFeed(this.getSkuVO(skuRepoItemsSubList), indexOfFile);		
						indexOfFile++;
						fileWriteCount+=fileWriteBatchSize;
					}
					if (totalCountInBatch-fileWriteCount < fileWriteBatchSize && totalCountInBatch-fileWriteCount > 0 ){
						skuRepoItemsSubList = skuRepoItems.subList(fileWriteCount,totalCountInBatch);
						status = this.populateSkuFeed(this.getSkuVO(skuRepoItemsSubList), indexOfFile);
						indexOfFile++;
					}
					
					currentCount+=this.getSkuBatchSize();
					fileWriteCount = 0;
				}
				
				if (totalSKUCount - currentCount < this.getSkuBatchSize() && totalSKUCount - currentCount > 0) {
					
					this.logDebug("Remaining sku's are less than queried batch size");
					
					rqlQueryRange=rqlQuery +" ORDER BY id "+ RANGE+ currentCount + "+" + (totalSKUCount - currentCount);
					this.logDebug("rqlQueryRange is " +  rqlQueryRange);
					
					long queryStartTime = System.currentTimeMillis();
					skuRepoItems = this.getFeedTools().getSKUDetailsRepo(isFullDataFeed, this.getSkuLastModDate(),rqlQueryRange);
					long queryEndTime = System.currentTimeMillis();
					this.logDebug("rql query in loop took time " + (queryEndTime - queryStartTime) + " ms ");
					
					int totalCountInBatch = skuRepoItems.size();
					this.logDebug("totalCountInBatch is " + totalCountInBatch);
					
					while (totalCountInBatch-fileWriteCount >= fileWriteBatchSize){
						skuRepoItemsSubList = skuRepoItems.subList(fileWriteCount,fileWriteCount + fileWriteBatchSize);
						status = this.populateSkuFeed(this.getSkuVO(skuRepoItemsSubList), indexOfFile);		
						indexOfFile++;
						fileWriteCount+=fileWriteBatchSize;
					}
					if (totalCountInBatch-fileWriteCount < fileWriteBatchSize && totalCountInBatch-fileWriteCount > 0){
						skuRepoItemsSubList = skuRepoItems.subList(fileWriteCount,totalCountInBatch);
						status = this.populateSkuFeed(this.getSkuVO(skuRepoItemsSubList), indexOfFile);
					}

				}
				if ((configPath != null) && !configPath.isEmpty()) {
					
						this.logDebug("CertonaCatalogFeedMarshaller.marshalSku() : SKU's Processed : "+String.valueOf(totalSKUCount));
					
					BBBUtility.writeFile(configPath.get(0), CERTONA_SKU_FEED_CHECK_SUM, "SKU's Processed :" +String.valueOf(totalSKUCount));

				}
		} catch (final BBBBusinessException e1) {
			
				this.logError(LogMessageFormatter.formatMessage(null,
						"CertonaCatalogFeedMarshaller.marshalSku() | BBBBusinessException "), e1);
			
			status = false;
		} catch (final BBBSystemException e) {
			
				this.logError(
						LogMessageFormatter.formatMessage(null, "CertonaCatalogFeedMarshaller.marshalSku() | BBBSystemException "),
						e);
			
			status = false;
		} finally {
			this.updateCertonaRepository(schedulerStartDate, isFullDataFeed, "sku", status);
		}

	}

	/**
	 *
	 * @param productObject
	 * @param productFeed
	 * @param indexOfFile
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public boolean getProdMarshalFile(
			final com.bbb.framework.jaxb.certona.catalogfeed.productfeed.ObjectFactory productObject,
			final ProductType productFeed, final int indexOfFile) throws BBBBusinessException, BBBSystemException {
			this.logDebug("CertonaCatalogFeedMarshaller Method : getProdMarshalFile ");
		boolean status = true;
		try {
			final JAXBElement<ProductType> product = productObject.createProduct(productFeed);
			final StringWriter stringWriter = new StringWriter();
			JAXBContext context;
			context = JAXBContext.newInstance(ProductType.class);
			final Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(product, stringWriter);
			final String productXml = stringWriter.getBuffer().toString();
			final List<String> configPath = this.getFeedTools().getCatalogTools()
					.getContentCatalogConfigration(this.getProdFeedFilePath());
			final List<String> configFile = this.getFeedTools().getCatalogTools()
					.getContentCatalogConfigration(this.getProdFeedFileName());
			if ((configPath != null) && !configPath.isEmpty() && (configFile != null) && !configFile.isEmpty()) {
				final String fileName = this.getFileName(configFile.get(0), indexOfFile);
					this.logDebug(indexOfFile + "th file created for product feed:" + configPath.get(0) + fileName);
				long fileWriteStartTime = System.currentTimeMillis();
				BBBUtility.writeFile(configPath.get(0), fileName, productXml);
				long fileWriteEndTime = System.currentTimeMillis();
				this.logDebug("CertonaCatalogFeedMarshaller.getProdMarshalFile() : File Writing took time " + (fileWriteEndTime - fileWriteStartTime) + " ms ");
			}

		} catch (final JAXBException e) {
			status = false;
			
				this.logError(LogMessageFormatter.formatMessage(null,
						"CertonaCatalogFeedMarshaller.getProdMarshalFile() | JAXBException "), e);
			
			throw new BBBBusinessException(BBBCatalogErrorCodes.JAXB_EXCEPTION,BBBCatalogErrorCodes.JAXB_EXCEPTION, e);

		}
		return status;
	}

	/**
	 * this method populates the JAXB classes for product feed with data from
	 * repository sent through CertonaProductVO. The empty strings are added in
	 * case there is no data in the repository for the corresponding property.This
	 * is so that as per the requirement 1. For Null/Optional Values - when they
	 * are not present feed should include empty tags rather than no tag. JAXB
	 * will not generate nodes when null object is added
	 *
	 * @param isFullDataFeed
	 * @param productFeed
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public boolean populateProductFeed( final List<CertonaProductVO> productVOList, final int indexOfFile)
			throws BBBSystemException, BBBBusinessException {
		
			this.logDebug("CertonaCatalogFeedMarshaller populateProductFeed");
			this.logDebug("no of products to be sent in feed in" + indexOfFile + " th file is:" + productVOList.size());
		
		
		long jaxbWriteStartTime = System.currentTimeMillis();
		
		final com.bbb.framework.jaxb.certona.catalogfeed.productfeed.ObjectFactory productObjectFactory = new com.bbb.framework.jaxb.certona.catalogfeed.productfeed.ObjectFactory();
		final ProductType productFeed = productObjectFactory.createProductType();
		final List<ProductDetail> jaxbProductDetail = new ArrayList<ProductDetail>();
		if ((productVOList != null) && !productVOList.isEmpty()) {
			for (int i = 0; i < productVOList.size(); i++) {
				final ProductDetail jaxbProduct = productObjectFactory.createProductDetail();
				final CertonaProductVO productVO = productVOList.get(i);
				jaxbProduct.setProductId(productVO.getProductId());
				this.logDebug(i + " th product id that is modified" + productVO.getProductId());
				
				//jaxbProduct.setAverageOverallRating(productVO.getAverageOverallRating());
				//jaxbProduct.setTotalReviewCount(productVO.getTotalReviewCount());
				jaxbProduct.setProductKeywords(productVO.getProductKeyWrds() == null ? "" : productVO.getProductKeyWrds());
				jaxbProduct.setCreationDate(productVO.getCreationDate()==null ? null : this.getXMLDate(productVO.getCreationDate()));
				jaxbProduct.setProductEnableDate(productVO.getEnableDate() == null ? null : this.getXMLDate(productVO.getEnableDate()));
				jaxbProduct.setSwatchFlag(productVO.isSwatchFlag());
				jaxbProduct.setProductRollupType(productVO.getProductRollupType() == null ? "" : productVO
						.getProductRollupType());
				jaxbProduct.setCollection(productVO.isCollection());
				jaxbProduct.setIsGiftCertProduct(productVO.getIsGiftCert());
				jaxbProduct.setProductType(productVO.getProductType() == null ? "" : productVO.getProductType());
				this.logDebug("updated basic properties of product not site dependent");
				final ImageVO image = productVO.getProductImages();
				jaxbProduct.setLargeImageURL((image == null) || (image.getLargeImage() == null) ? "" : image.getLargeImage());
				jaxbProduct.setMediumImageURL((image == null) || (image.getMediumImage() == null) ? "" : image.getMediumImage());
				jaxbProduct.setSmallImageURL((image == null) || (image.getSmallImage() == null) ? "" : image.getSmallImage());
				jaxbProduct.setSwatchImageURL((image == null) || (image.getSwatchImage() == null) ? "" : image.getSwatchImage());
				jaxbProduct.setThumbNailImageURL((image == null) || (image.getThumbnailImage() == null) ? "" : image
						.getThumbnailImage());
				jaxbProduct.setZoomImageURL((image == null) || (image.getZoomImage() == null) ? "" : image.getZoomImage());
				jaxbProduct.setZoomIndex((image == null) ? 0 : image.getZoomImageIndex());
				this.logDebug("image details updated for product");
				
				final BrandsType brandType = this.getProdBrands(productVO.getProductBrand(), productObjectFactory.createBrandsType());
				jaxbProduct.setBrand(brandType);

				final List<String> childSkuList = productVO.getChildSKUs();
				jaxbProduct.getChildSkuIds().addAll(this.updateChildSku(childSkuList, productObjectFactory));

				final List<CollectionChildProductAttr> collChildPrdtsList = productVO.getChildProductAttr();
				final List<ChildProductType> jaxbChildPrdtList = this.getCollChildProd(collChildPrdtsList, productObjectFactory);
				jaxbProduct.getChildProductsForCollection().addAll(jaxbChildPrdtList);

				final Map<String, SiteSpecificProductAttr> siteProdProperties = productVO.getSitePrdtAttrMap();
				jaxbProduct.getSiteSpecificDetails().addAll(this.getSiteProdAttr(siteProdProperties));
				//LTL Start -  Added LTL flag to certona product feed
				jaxbProduct.setLtlFlag(productVO.isLtlFlag());
				//LTL end
				//Fix for defect BBBSL-3068
				jaxbProduct.setProductUrl(productVO.getProductUrl() == null ? "" : productVO.getProductUrl());
				jaxbProductDetail.add(jaxbProduct);
			}
			productFeed.getProductAttributes().addAll(jaxbProductDetail);
			
			
			long jaxbWriteEndTime = System.currentTimeMillis();
			
			this.logDebug("CertonaCatalogFeedMarshaller.populateProductFeed : JaxB Creation took time " + (jaxbWriteEndTime - jaxbWriteStartTime) + " ms ");
			
			
		}
		return this.getProdMarshalFile(productObjectFactory, productFeed, indexOfFile);

	}

	/**
	 *
	 * @param childSkuList
	 * @param productObjectFactory
	 * @return
	 */
	private List<ChildSkuType> updateChildSku(final List<String> childSkuList,
			final com.bbb.framework.jaxb.certona.catalogfeed.productfeed.ObjectFactory productObjectFactory) {
		final List<ChildSkuType> jaxbChildSku = new ArrayList<ChildSkuType>();
		if ((childSkuList != null) && !childSkuList.isEmpty()) {

			for (int index = 0; index < childSkuList.size(); index++) {
				final ChildSkuType childSku = productObjectFactory.createChildSkuType();
				childSku.setChildskuId(childSkuList.get(index));
				jaxbChildSku.add(childSku);
				
					this.logDebug("adding child sku : " + childSkuList.get(index));
				
			}

		} else {
			
				this.logDebug("No child sku available for the product");
			
			final ChildSkuType childSku = productObjectFactory.createChildSkuType();
			childSku.setChildskuId("");
			jaxbChildSku.add(childSku);

		}
		
			this.logDebug("Child Sku updated for product");
		
		return jaxbChildSku;
	}

	/**
	 *
	 *
	 * @param siteProdProperties
	 * @return
	 * @throws BBBSystemException
	 */
	private List<SiteSpecificType> getSiteProdAttr(final Map<String, SiteSpecificProductAttr> siteProdProperties) throws BBBSystemException {
		final List<SiteSpecificType> jaxbSiteProdList = new ArrayList<SiteSpecificType>();
		SiteSpecificType jaxbSiteValues=null;
		if ((siteProdProperties != null) && !siteProdProperties.isEmpty()) {
			final Set<String> keys = siteProdProperties.keySet();

			for (final String key : keys) {
				
					this.logDebug("updating site data for site :" + key);
				
				final SiteSpecificProductAttr siteValues = siteProdProperties.get(key);

				jaxbSiteValues = new SiteSpecificType();
				/** Added Site Specific Enable Date for R2.1 #53A */
				jaxbSiteValues.setEnableDate(this.getXMLDateNull(siteValues.getEnableDate()));
				jaxbSiteValues.setCollegeId(siteValues.getCollegeId() == null ? "" : siteValues.getCollegeId());
				jaxbSiteValues.setDescription(siteValues.getShortDescription() == null ? "" : siteValues.getShortDescription());
				jaxbSiteValues.setDisplayName(siteValues.getName() == null ? "" : siteValues.getName());
				jaxbSiteValues.setIsActiveProduct(siteValues.isActiveProduct());
				jaxbSiteValues.setLongDescription(siteValues.getLongDescription() == null ? "" : siteValues
						.getLongDescription());
				jaxbSiteValues.setPriceRangeDescrip(siteValues.getPriceRangeDescription() == null ? "" : siteValues
						.getPriceRangeDescription());
				jaxbSiteValues.setSiteId(key);
				jaxbSiteValues.setSkuHighPrice(siteValues.getSkuHighPrice() == null ? "" : siteValues.getSkuHighPrice());
				jaxbSiteValues.setSkuLowPrice(siteValues.getSkuLowPrice() == null ? "" : siteValues.getSkuLowPrice());
				jaxbSiteValues.setTier(siteValues.getTier() == null ? "" : siteValues.getTier());
				jaxbSiteValues.setAverageOverallRating(siteValues.getAverageOverallRating());
				jaxbSiteValues.setTotalReviewCount(siteValues.getTotalReviewCount());
				

				jaxbSiteProdList.add(jaxbSiteValues);

			}
		} else {
			
				this.logDebug("no site specific data available");
			
			jaxbSiteValues = new SiteSpecificType();
			/** Added Site Specific Enable Date for R2.1 #53A */
			jaxbSiteValues.setEnableDate(null);
			jaxbSiteValues.setCollegeId("");
			jaxbSiteValues.setDescription("");
			jaxbSiteValues.setDisplayName("");
			jaxbSiteValues.setIsActiveProduct(false);
			jaxbSiteValues.setLongDescription("");
			jaxbSiteValues.setPriceRangeDescrip("");
			jaxbSiteValues.setSiteId("");
			jaxbSiteValues.setSkuHighPrice("");
			jaxbSiteValues.setSkuLowPrice("");
			jaxbSiteValues.setTier("");
			jaxbSiteProdList.add(jaxbSiteValues);
			jaxbSiteValues.setAverageOverallRating(0);
			jaxbSiteValues.setTotalReviewCount(0);
		}
		
			this.logDebug("updated site specific data");
		
		return jaxbSiteProdList;
	}

	private List<ChildProductType> getCollChildProd(final List<CollectionChildProductAttr> collChildPrdtsList,
			final com.bbb.framework.jaxb.certona.catalogfeed.productfeed.ObjectFactory productObjectFactory) {
		final List<ChildProductType> jaxbChildPrdtList = new ArrayList<ChildProductType>();
		CollectionChildProductAttr collChildPrdts;
		if ((collChildPrdtsList != null) && !collChildPrdtsList.isEmpty()) {
			for (int index = 0; index < collChildPrdtsList.size(); index++) {
				collChildPrdts= collChildPrdtsList.get(index);
				
				this.logDebug("updating child products for collection product:child product id:"
							+ collChildPrdts.getChildProductId());
				
				final ChildProductType jaxbColChildPrdt = productObjectFactory.createChildProductType();
				jaxbColChildPrdt.setChildProductId((collChildPrdts.getChildProductId() == null) ? "" : collChildPrdts
						.getChildProductId());
				jaxbColChildPrdt.setChildRollUpType((collChildPrdts.getChildRollUpType() == null) ? "" : collChildPrdts
						.getChildRollUpType());
				jaxbColChildPrdt.setLikeUnlike(collChildPrdts.getLikeUnlike());
				jaxbChildPrdtList.add(jaxbColChildPrdt);
			}
		} else {
			this.logDebug("updated child products for collection product");			
			final ChildProductType jaxbColChildPrdt = productObjectFactory.createChildProductType();
			jaxbColChildPrdt.setChildProductId("");
			jaxbColChildPrdt.setChildRollUpType("");
			jaxbColChildPrdt.setLikeUnlike(false);
			jaxbChildPrdtList.add(jaxbColChildPrdt);
		}
		
			this.logDebug("updated child products for collection product");
		
		return jaxbChildPrdtList;
	}

	private XMLGregorianCalendar getXMLDate(final Date date) throws BBBSystemException {
		if (date == null) {
			final Date defaultDate = new Date();
			defaultDate.setTime(0);
			return BBBUtility.getXMLCalendar(defaultDate);
		} else {
			return BBBUtility.getXMLCalendar(date);
		}

	}

	private XMLGregorianCalendar getXMLDateNull(final Date date) throws BBBSystemException {
		if (date == null) {
			return null;
		} else {
			return BBBUtility.getXMLCalendar(date);
		}

	}


	private BrandsType getProdBrands(final BrandVO brands, final BrandsType brandType) {

		brandType.setBrandDescrip((brands == null) || (brands.getBrandDesc() == null) ? "" : brands.getBrandDesc());
		brandType.setBrandDisplay(brands == null ? false : brands.isDisplayFlag());
		brandType.setBrandId((brands == null) || (brands.getBrandId() == null) ? "" : brands.getBrandId());
		brandType.setBrandImageURL((brands == null) || (brands.getBrandImage() == null) ? "" : brands.getBrandImage());
		brandType.setBrandName((brands == null) || (brands.getBrandName() == null) ? "" : brands.getBrandName());
		
			this.logDebug("Brands updated for product");
		
		return brandType;
	}



	public List<CertonaSKUVO> getSkuVO(final List<RepositoryItem> subSkuRepoList) {
		
		long createSkuVOStartTime = System.currentTimeMillis();
		final List<CertonaSKUVO> list = new ArrayList<CertonaSKUVO>();
		if ((subSkuRepoList != null) && !subSkuRepoList.isEmpty()) {
			for (int i = 0; i < subSkuRepoList.size(); i++) {
				try {
					list.add(this.getFeedTools().populateSKUVO(subSkuRepoList.get(i)));
				} catch (final BBBBusinessException e) {
					
						this.logError(LogMessageFormatter.formatMessage(null,
								"CertonaCatalogFeedMarshaller.getSkuVO() | BBBBusinessException "), e);
					
				} catch (final BBBSystemException e) {
					
						this.logError(
								LogMessageFormatter.formatMessage(null, "CertonaCatalogFeedMarshaller.getSkuVO() | BBBSystemException "
										), e);
					
				} catch (final RepositoryException e) {
					
						this.logError(LogMessageFormatter.formatMessage(null,
								"CertonaCatalogFeedMarshaller.getSkuVO() | RepositoryException " ), e);
					
				}
			}
		}
		long createSkuVOEndTime = System.currentTimeMillis();
		this.logDebug("Creating SKU VO took time " + (createSkuVOEndTime - createSkuVOStartTime) + " ms ");
		return list;
	}

	/**
	 *
	 * @param isFullDataFeed
	 * @param skuFeed
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private boolean populateSkuFeed(final List<CertonaSKUVO> subSkuVOList, final int indexOfFile) throws BBBBusinessException,
	BBBSystemException {

		
			this.logDebug("CertonaCatalogFeedMarshaller populateSKUFeed");
			this.logDebug("no of skus to be sent in feed in" + indexOfFile + " th file is:" + subSkuVOList.size());
		
		
		long jaxbWriteStartTime = System.currentTimeMillis();
		
		final com.bbb.framework.jaxb.certona.catalogfeed.skufeed.ObjectFactory skuObjectFactory = new com.bbb.framework.jaxb.certona.catalogfeed.skufeed.ObjectFactory();
		final SkuType skuFeed = skuObjectFactory.createSkuType();
		final List<SkuDetail> jaxbSkuDetail = new ArrayList<SkuDetail>();
		if ((subSkuVOList != null) && !subSkuVOList.isEmpty()) {
			for (int i = 0; i < subSkuVOList.size(); i++) {
				final SkuDetail jaxbSku = skuObjectFactory.createSkuDetail();
				final CertonaSKUVO skuVO = subSkuVOList.get(i);
				jaxbSku.setSkuId(skuVO.getSkuId());
				
					this.logDebug(i + " th sku id that is modified" + skuVO.getSkuId());
				
				jaxbSku.setCreationDate(skuVO.getCreationDate() == null ? null : ((XMLGregorianCalendar)this.getXMLDate(skuVO.getCreationDate())));
				jaxbSku.setSkuEnableDate(skuVO.getSkuEnableDate() == null ? null : this.getXMLDate(skuVO.getSkuEnableDate()));
				jaxbSku.setSkuType(skuVO.getSkuType() == null ? "" : skuVO.getSkuType());
				jaxbSku.setIsGiftCertsku(skuVO.isGiftCertsku());
				jaxbSku.setJdaDept(skuVO.getJdaDept() == null ? "" : skuVO.getJdaDept());
				jaxbSku.setJdaSubDept(skuVO.getJdaSubDept() == null ? "" : skuVO.getJdaSubDept());
				jaxbSku.setJdaClass(skuVO.getJdaClass() == null ? "" : skuVO.getJdaClass());
				jaxbSku.setColor(skuVO.getColor() == null ? "" : skuVO.getColor());
				jaxbSku.setColorGroup(skuVO.getColorGroup() == null ? "" : skuVO.getColorGroup());
				jaxbSku.setSize(skuVO.getSize() == null ? "" : skuVO.getSize());
				jaxbSku.setVdcSkuMessage(skuVO.getVdcSkuMessage() == null ? "" : skuVO.getVdcSkuMessage());
				jaxbSku.setVdcSkuType(skuVO.getVdcSkuType() == null ? "" : skuVO.getVdcSkuType());
				jaxbSku.setUpc(skuVO.getUpc() == null ? "" : skuVO.getUpc());
				jaxbSku.setEComFulfillment(skuVO.geteComFulfillment() == null ? "" : skuVO.geteComFulfillment());
				jaxbSku.setVendorId(skuVO.getVendorId() == null ? "" : skuVO.getVendorId());
				jaxbSku.setOnsale(skuVO.isOnsale());
				jaxbSku.setCollegeId(skuVO.getCollegeId() == null ? "" : skuVO.getCollegeId());
				jaxbSku.setEmailOutOfStock(skuVO.isEmailOutOfStock());
				jaxbSku.setGiftWrapEligible(skuVO.isGiftWrapEligible());
				jaxbSku.setBopusExclusion(skuVO.isBopusExclusion());
				jaxbSku.setSkuTaxStatus(skuVO.getSkuTaxStatus() == null ? "" : skuVO.getSkuTaxStatus());
				//LTL Start -  Added LTL flag to certona feed				
				jaxbSku.setLtlFlag(skuVO.isLtlFlag());
				// LTL End
				
					this.logDebug("updated basic properties of sku not site dependent");
				
				final Prop65Type prop65TypeFlags = this.updateProp65TypeFlags(skuVO.getProp65Flags(),
						skuObjectFactory.createProp65Type());
				jaxbSku.setProp65Flags(prop65TypeFlags);

				final Map<String, SiteSpecificSKUAttr> siteSKUProperties = skuVO.getSiteSKUAttrMap();
				jaxbSku.getSiteSpecificDetails().addAll(this.getSiteSkuAttr(siteSKUProperties));

				jaxbSkuDetail.add(jaxbSku);
			}
			skuFeed.getSkuProperties().addAll(jaxbSkuDetail);
		}
		
		long jaxbWriteEndTime = System.currentTimeMillis();
		
		this.logDebug("CertonaCatalogFeedMarshaller.populateSkuFeed : JaxB Creation took time " + (jaxbWriteEndTime - jaxbWriteStartTime) + " ms ");
		
		return this.getSkuMarshalFile(skuObjectFactory, skuFeed, indexOfFile);

	}

	private Prop65Type updateProp65TypeFlags(final Prop65TypeVO prop65TypeFlags, final Prop65Type skuProp65Type) {

		skuProp65Type.setProp65Crystal(prop65TypeFlags == null ? false : prop65TypeFlags.isProp65Crystal());
		skuProp65Type.setProp65Dinnerware(prop65TypeFlags == null ? false : prop65TypeFlags.isProp65Dinnerware());
		skuProp65Type.setProp65Lighting(prop65TypeFlags == null ? false : prop65TypeFlags.isProp65Lighting());
		skuProp65Type.setProp65Other(prop65TypeFlags == null ? false : prop65TypeFlags.isProp65Other());
	
			this.logDebug("Prop65 Flags Updated for Sku");
		
		return skuProp65Type;
	}

	private List<SiteSKUSpecificType> getSiteSkuAttr(final Map<String, SiteSpecificSKUAttr> siteSKUProperties)
			throws BBBSystemException {
		final List<SiteSKUSpecificType> jaxbSiteSKUList = new ArrayList<SiteSKUSpecificType>();
		if ((siteSKUProperties != null) && !siteSKUProperties.isEmpty()) {
			final Set<String> keys = siteSKUProperties.keySet();
			SiteSKUSpecificType jaxbSiteValues=null;
			for (final String key : keys) {
				
					this.logDebug("updating site data for site :" + key);
				
				final SiteSpecificSKUAttr siteValues = siteSKUProperties.get(key);

				jaxbSiteValues = new SiteSKUSpecificType();
				jaxbSiteValues.setDisplayName(siteValues.getDisplayName() == null ? "" : siteValues.getDisplayName());
				jaxbSiteValues.setIsActiveSku(siteValues.isActiveSku());
				jaxbSiteValues.setDescription(siteValues.getDescription() == null ? "" : siteValues.getDescription());
				jaxbSiteValues.setLongDescription(siteValues.getLongDescription() == null ? "" : siteValues
						.getLongDescription());
				jaxbSiteValues.setListPrice(siteValues.getListPrice());
				jaxbSiteValues.setSalePrice(siteValues.getSalePrice());
				jaxbSiteValues.setShippingSurcharge(siteValues.getShippingSurcharge());
				jaxbSiteValues.setSiteId(key);
				final List<com.bbb.certona.vo.SkuAttributesType> skuAttributes = siteValues.getSkuAttributes();
				jaxbSiteValues.getSkuAttributes().addAll(this.updateSkuAttributes(skuAttributes));

				final List<com.bbb.certona.vo.RebatesDetails> siteSKURebates = siteValues.getSkuRebates();
				jaxbSiteValues.getSkuRebates().addAll(this.updateSkuRebates(siteSKURebates));

				final List<com.bbb.certona.vo.ShippingDetails> eligibleShippingDetails = siteValues.getEligibleShipMethods();
				jaxbSiteValues.getEligibleShipMethods().addAll(this.updateShippingDetails(eligibleShippingDetails));

				final List<com.bbb.certona.vo.ShippingDetails> freeShippingDetails = siteValues.getFreeShipMethods();
				jaxbSiteValues.getFreeShipMethods().addAll(this.updateShippingDetails(freeShippingDetails));

				final List<com.bbb.certona.vo.StatesDetails> statesDetails = siteValues.getNonShippableStates();
				jaxbSiteValues.getNonShippableStates().addAll(this.updateStates(statesDetails));

				jaxbSiteSKUList.add(jaxbSiteValues);

			}
		} else {
			
				this.logDebug("no site specific data available");
			
			final SiteSKUSpecificType jaxbSiteValues = new SiteSKUSpecificType();
			jaxbSiteValues.setDisplayName("");
			jaxbSiteValues.setIsActiveSku(true);
			jaxbSiteValues.setDescription("");
			jaxbSiteValues.setLongDescription("");
			jaxbSiteValues.setSalePrice(0.00);
			jaxbSiteValues.setListPrice(0.00);
			jaxbSiteValues.setShippingSurcharge(0.00);

			final List<com.bbb.certona.vo.SkuAttributesType> skuAttributes = null;
			jaxbSiteValues.getSkuAttributes().addAll(this.updateSkuAttributes(skuAttributes));

			final List<com.bbb.certona.vo.RebatesDetails> siteSKURebates = null;
			jaxbSiteValues.getSkuRebates().addAll(this.updateSkuRebates(siteSKURebates));

			final List<com.bbb.certona.vo.ShippingDetails> eligibleShippingDetails = null;
			jaxbSiteValues.getEligibleShipMethods().addAll(this.updateShippingDetails(eligibleShippingDetails));

			final List<com.bbb.certona.vo.ShippingDetails> freeShippingDetails = null;
			jaxbSiteValues.getFreeShipMethods().addAll(this.updateShippingDetails(freeShippingDetails));

			final List<com.bbb.certona.vo.StatesDetails> statesDetails = null;
			jaxbSiteValues.getNonShippableStates().addAll(this.updateStates(statesDetails));

			jaxbSiteSKUList.add(jaxbSiteValues);
		}

		
		this.logDebug("updated sku attributes");
		
		return jaxbSiteSKUList;
	}

	private List<com.bbb.framework.jaxb.certona.catalogfeed.skufeed.StatesDetails> updateStates(
			final List<com.bbb.certona.vo.StatesDetails> statesDetails) {
		final List<com.bbb.framework.jaxb.certona.catalogfeed.skufeed.StatesDetails> jaxbSKUStates = new ArrayList<com.bbb.framework.jaxb.certona.catalogfeed.skufeed.StatesDetails>();
		com.bbb.framework.jaxb.certona.catalogfeed.skufeed.StatesDetails jaxbSKUState=null;
		if ((statesDetails != null) && !statesDetails.isEmpty()) {
			for (int index = 0; index < statesDetails.size(); index++) {
				jaxbSKUState = new com.bbb.framework.jaxb.certona.catalogfeed.skufeed.StatesDetails();
				jaxbSKUState.setBopus(statesDetails.get(index).isBopus());
				jaxbSKUState.setCountryCd(statesDetails.get(index).getCountryCd() == null ? "" : statesDetails.get(index)
						.getCountryCd());
				jaxbSKUState.setStateCode(statesDetails.get(index).getStateCode() == null ? "" : statesDetails.get(index)
						.getStateCode());
				jaxbSKUState.setStateName(statesDetails.get(index).getStateName() == null ? "" : statesDetails.get(index)
						.getStateName());

				jaxbSKUStates.add(jaxbSKUState);
				
					this.logDebug("adding Sku Attribute : " + statesDetails.get(index));
				
			}

		} else {
			
				this.logDebug("No skuRebates available for the sku");
			
			jaxbSKUState = new com.bbb.framework.jaxb.certona.catalogfeed.skufeed.StatesDetails();

			jaxbSKUState.setBopus(false);
			jaxbSKUState.setCountryCd("");
			jaxbSKUState.setStateCode("");
			jaxbSKUState.setStateName("");

			jaxbSKUStates.add(jaxbSKUState);

		}

		return jaxbSKUStates;
	}

	private List<com.bbb.framework.jaxb.certona.catalogfeed.skufeed.ShippingDetails> updateShippingDetails(
			final List<com.bbb.certona.vo.ShippingDetails> shippingDetails) throws BBBSystemException {
		final List<com.bbb.framework.jaxb.certona.catalogfeed.skufeed.ShippingDetails> jaxbShippingDetails = new ArrayList<com.bbb.framework.jaxb.certona.catalogfeed.skufeed.ShippingDetails>();
		com.bbb.framework.jaxb.certona.catalogfeed.skufeed.ShippingDetails jaxbShippingDetail=null;
		if ((shippingDetails != null) && !shippingDetails.isEmpty()) {
			for (int index = 0; index < shippingDetails.size(); index++) {
				jaxbShippingDetail = new com.bbb.framework.jaxb.certona.catalogfeed.skufeed.ShippingDetails();

				jaxbShippingDetail.setShipMethodId(shippingDetails.get(index).getShipMethodId() == null ? "" : shippingDetails
						.get(index).getShipMethodId());
				jaxbShippingDetail.setCutOffTime(this.getXMLDate(shippingDetails.get(index).getCutOffTime()));
				jaxbShippingDetail.setMaxDaysToShip(shippingDetails.get(index).getMaxDaysToShip());
				jaxbShippingDetail.setMinDaysToShip(shippingDetails.get(index).getMinDaysToShip());
				jaxbShippingDetail.setShipMethodDescription(shippingDetails.get(index).getShipMethodDescription() == null ? ""
						: shippingDetails.get(index).getShipMethodDescription());
				jaxbShippingDetail.setShipMethodName(shippingDetails.get(index).getShipMethodName() == null ? ""
						: shippingDetails.get(index).getShipMethodName());

				jaxbShippingDetails.add(jaxbShippingDetail);
				
					this.logDebug("adding shipping details : " + shippingDetails.get(index));
				
			}

		} else {
			
				this.logDebug("No skuRebates available for the sku");
			
			jaxbShippingDetail = new com.bbb.framework.jaxb.certona.catalogfeed.skufeed.ShippingDetails();
			final Date defaultDate = null;
			jaxbShippingDetail.setShipMethodId("");
			jaxbShippingDetail.setCutOffTime(this.getXMLDate(defaultDate));
			jaxbShippingDetail.setMaxDaysToShip(0);
			jaxbShippingDetail.setMinDaysToShip(0);
			jaxbShippingDetail.setShipMethodDescription("");
			jaxbShippingDetail.setShipMethodName("");

			jaxbShippingDetails.add(jaxbShippingDetail);

		}
		return jaxbShippingDetails;
	}

	private List<com.bbb.framework.jaxb.certona.catalogfeed.skufeed.RebatesDetails> updateSkuRebates(
			final List<RebatesDetails> siteSKURebates) throws BBBSystemException {
		final List<com.bbb.framework.jaxb.certona.catalogfeed.skufeed.RebatesDetails> jaxbSKURebates = new ArrayList<com.bbb.framework.jaxb.certona.catalogfeed.skufeed.RebatesDetails>();
		com.bbb.framework.jaxb.certona.catalogfeed.skufeed.RebatesDetails jaxbSKURebate=null;
		if ((siteSKURebates != null) && !siteSKURebates.isEmpty()) {

			for (int index = 0; index < siteSKURebates.size(); index++) {
				jaxbSKURebate = new com.bbb.framework.jaxb.certona.catalogfeed.skufeed.RebatesDetails();

				jaxbSKURebate.setRebateDescrip(siteSKURebates.get(index).getRebateDescrip() == null ? "" : siteSKURebates.get(
						index).getRebateDescrip());
				jaxbSKURebate.setRebateEndDate(this.getXMLDate((siteSKURebates.get(index).getRebateEndDate())));
				jaxbSKURebate.setRebateId(siteSKURebates.get(index).getRebateId() == null ? "" : siteSKURebates.get(index)
						.getRebateId());
				jaxbSKURebate.setRebateStartDate(this.getXMLDate(siteSKURebates.get(index).getRebateStartDate()));
				jaxbSKURebate.setRebateURL(siteSKURebates.get(index).getRebateURL() == null ? "" : siteSKURebates.get(index)
						.getRebateURL());

				jaxbSKURebates.add(jaxbSKURebate);
				
					this.logDebug("adding Sku Rebates : " + siteSKURebates.get(index));
				
			}

		} else {
			
				this.logDebug("No skuRebates available for the sku");
			
			jaxbSKURebate = new com.bbb.framework.jaxb.certona.catalogfeed.skufeed.RebatesDetails();
			final Date defaultDate = null;
			jaxbSKURebate.setRebateDescrip("");
			jaxbSKURebate.setRebateEndDate(this.getXMLDate(defaultDate));
			jaxbSKURebate.setRebateId("");
			jaxbSKURebate.setRebateStartDate(this.getXMLDate(defaultDate));
			jaxbSKURebate.setRebateURL("");

			jaxbSKURebates.add(jaxbSKURebate);
		}
		return jaxbSKURebates;
	}

	private List<SkuAttributesType> updateSkuAttributes(final List<com.bbb.certona.vo.SkuAttributesType> skuAttributes)
			throws BBBSystemException {
		final List<SkuAttributesType> jaxbAttributes = new ArrayList<SkuAttributesType>();
		SkuAttributesType jaxbAttribute=null;
		if ((skuAttributes != null) && !skuAttributes.isEmpty()) {
			for (int index = 0; index < skuAttributes.size(); index++) {
				jaxbAttribute = new SkuAttributesType();
				jaxbAttribute.setAttributeId(skuAttributes.get(index).getAttributeId() == null ? "" : skuAttributes.get(index)
						.getAttributeId());
				jaxbAttribute.setAttrActionURL(skuAttributes.get(index).getAttrActionURL() == null ? "" : skuAttributes.get(
						index).getAttrActionURL());
				jaxbAttribute.setAttributeDisplayName(skuAttributes.get(index).getAttributeDisplayName() == null ? ""
						: skuAttributes.get(index).getAttributeDisplayName());
				jaxbAttribute.setAttributeEndDate(this.getXMLDate(skuAttributes.get(index).getAttributeEndDate()));
				jaxbAttribute.setAttributeStartDate(this.getXMLDate(skuAttributes.get(index).getAttributeStartDate()));
				jaxbAttribute.setAttrImageURL(skuAttributes.get(index).getAttrImageURL() == null ? "" : skuAttributes
						.get(index).getAttrImageURL());
				jaxbAttribute.setPlaceHolder(skuAttributes.get(index).getPlaceHolder() == null ? "" : skuAttributes.get(index)
						.getPlaceHolder());
				jaxbAttribute.setPriority(skuAttributes.get(index).getPriority());

				jaxbAttributes.add(jaxbAttribute);
				
					this.logDebug("adding Sku Attribute : " + skuAttributes.get(index));
				
			}
		} else {
			
				this.logDebug("No skuAttributes available for the sku");
			
			final Date defaultDate = null;
			jaxbAttribute = new SkuAttributesType();
			jaxbAttribute.setAttributeId("");
			jaxbAttribute.setAttrActionURL("");
			jaxbAttribute.setAttributeDisplayName("");
			jaxbAttribute.setAttributeEndDate(this.getXMLDate(defaultDate));
			jaxbAttribute.setAttributeStartDate(this.getXMLDate(defaultDate));
			jaxbAttribute.setAttrImageURL("");
			jaxbAttribute.setPlaceHolder("");
			jaxbAttribute.setPriority(0);

			jaxbAttributes.add(jaxbAttribute);
		}
		
			this.logDebug("Sku Attributes updated for sku");
		
		return jaxbAttributes;
	}

	/**
	 *
	 * @param productObject
	 * @param productFeed
	 * @param indexOfFile
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public boolean getSkuMarshalFile(final com.bbb.framework.jaxb.certona.catalogfeed.skufeed.ObjectFactory skuObject,
			final SkuType skuType, final int indexOfFile) throws BBBBusinessException, BBBSystemException {
		
			this.logDebug("CertonaCatalogFeedMarshaller Method : getSkuMarshalFile ");
		
		boolean status = true;
		try {
			final JAXBElement<SkuType> sku = skuObject.createSku(skuType);
			final StringWriter stringWriter = new StringWriter();
			JAXBContext context;
			context = JAXBContext.newInstance(SkuType.class);
			final Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(sku, stringWriter);
			final String skuXml = stringWriter.getBuffer().toString();
			final List<String> configPath = this.getFeedTools().getCatalogTools()
					.getContentCatalogConfigration(this.getSkuFeedFilePath());
			final List<String> configFile = this.getFeedTools().getCatalogTools()
					.getContentCatalogConfigration(this.getSkuFeedFileName());
			if ((configPath != null) && !configPath.isEmpty() && (configFile != null) && !configFile.isEmpty()) {
				final String fileName = this.getFileName(configFile.get(0), indexOfFile);
				
					this.logDebug(indexOfFile + "th file created for sku feed:" + configPath.get(0) + fileName);
				
				long fileWriteStartTime = System.currentTimeMillis();
				BBBUtility.writeFile(configPath.get(0), fileName, skuXml);
				long fileWriteEndTime = System.currentTimeMillis();
				this.logDebug("CertonaCatalogFeedMarshaller.getSkuMarshalFile() : File Writing took time " + (fileWriteEndTime - fileWriteStartTime) + " ms ");

			}

		} catch (final JAXBException e) {
			status = false;
			
				this.logError(LogMessageFormatter.formatMessage(null,
						"CertonaCatalogFeedMarshaller.getSkuMarshalFile() | JAXBException "), e);
			
			throw new BBBBusinessException(BBBCatalogErrorCodes.JAXB_EXCEPTION,BBBCatalogErrorCodes.JAXB_EXCEPTION, e);

		}
		return status;
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
						"CertonaCatalogFeedMarshaller.getLastModifiedDate() | RepositoryException "), e);
			
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
		}

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
	public void updateCertonaRepository(final Timestamp schedulerStartDate, final boolean isFullDataFeed, final String typeOfFeed,
			final boolean status) {
		
			this.logDebug("CertonaCatalogFeedMarshaller Method : updateCertonaRepository");
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
						"CertonaCatalogFeedMarshaller.updateCertonaRepository() | RepositoryException "), e);
			
		}
	}
}
