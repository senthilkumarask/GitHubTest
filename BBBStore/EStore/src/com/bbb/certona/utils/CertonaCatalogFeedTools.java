package com.bbb.certona.utils;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
//import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


//import atg.adapter.version.VersionRepository;
import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.core.util.StringUtils;
import atg.repository.MutableRepository;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RemovedItemException;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.bbb.certona.vo.CertonaCategoryVO;
import com.bbb.certona.vo.CertonaProductVO;
import com.bbb.certona.vo.CertonaSKUVO;
import com.bbb.certona.vo.CollectionChildProductAttr;
import com.bbb.certona.vo.Prop65TypeVO;
import com.bbb.certona.vo.RebatesDetails;
import com.bbb.certona.vo.ShippingDetails;
import com.bbb.certona.vo.SiteSpecificProductAttr;
import com.bbb.certona.vo.SiteSpecificSKUAttr;
import com.bbb.certona.vo.SkuAttributesType;
import com.bbb.certona.vo.StatesDetails;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.BazaarVoiceProductVO;
import com.bbb.commerce.catalog.vo.BrandVO;
import com.bbb.commerce.catalog.vo.ImageVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCertonaConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

/**
 * Catalog feed helper class. Fetches data from Catalog repository based on
 * date.For full feed all data is fetched.For incremental feed data is fetched
 * based on last modified date
 * 
 * @author njai13
 * 
 */
public class CertonaCatalogFeedTools extends BBBGenericService {
	private Repository catalogRepository;
	private Repository siteRepository;
	private BBBCatalogTools catalogTools;
	private PriceListManager priceListManager;
	private MutableRepository shippingRepository;
	// default site as in catalog
	private String defaultSiteId;
	private Map<String,String> freeShipAttrShipGrpMap=new HashMap<String,String> ();
	private List<String> siteIds=new ArrayList<String>();
	private List<String> nonDefaultSiteIds=new ArrayList<String>();
	public List<String> getNonDefaultSiteIds() {
		return nonDefaultSiteIds;
	}

	public void setNonDefaultSiteIds(List<String> nonDefaultSiteIds) {
		this.nonDefaultSiteIds = nonDefaultSiteIds;
	}



	/**
	 * @return the shippingRepository
	 */
	public MutableRepository getShippingRepository() {
		return shippingRepository;
	}

	/**
	 * @param shippingRepository the shippingRepository to set
	 */
	public void setShippingRepository(MutableRepository shippingRepository) {
		this.shippingRepository = shippingRepository;
	}

	/**
	 * @return the freeShipAttrShipGrpMap
	 */
	public Map<String, String> getFreeShipAttrShipGrpMap() {
		return freeShipAttrShipGrpMap;
	}

	/**
	 * @param freeShipAttrShipGrpMap the freeShipAttrShipGrpMap to set
	 */
	public void setFreeShipAttrShipGrpMap(Map<String, String> freeShipAttrShipGrpMap) {
		this.freeShipAttrShipGrpMap = freeShipAttrShipGrpMap;
	}

	/**
	 * @return the catalogRepository
	 */
	public Repository getCatalogRepository() {
		return catalogRepository;
	}

	/**
	 * @param catalogRepository
	 *          the catalogRepository to set
	 */
	public void setCatalogRepository(final Repository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools
	 *          the catalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * @return the defaultSiteId
	 */
	public String getDefaultSiteId() {
		return defaultSiteId;
	}

	/**
	 * @param defaultSiteId
	 *          the defaultSiteId to set
	 */
	public void setDefaultSiteId(final String defaultSiteId) {
		this.defaultSiteId = defaultSiteId;
	}

	public PriceListManager getPriceListManager() {
		return priceListManager;
	}

	public void setPriceListManager(PriceListManager priceListManager) {
		this.priceListManager = priceListManager;
	}

	public Repository getSiteRepository() {
		return siteRepository;
	}

	public void setSiteRepository(Repository siteRepository) {
		this.siteRepository = siteRepository;
	}

	/**
	 * @return the siteIds
	 */
	public List<String> getSiteIds() {
		return siteIds;
	}

	/**
	 * @param siteIds the siteIds to set
	 */
	public void setSiteIds(List<String> siteIds) {
		this.siteIds = siteIds;
	}

	/**
	 * This method fetches properties for category to be sent in certona feed if
	 * full feed is required or if last modified date is null ,full data for
	 * category is fetched.For incremental feed data that has been modified since
	 * last feed is only fetched
	 * 
	 * @param isFullDataFeed
	 *          :if full feed is required=true else false
	 * @param lastModifiedDate
	 *          :date since last feed was run
	 * @return List of CertonaCategoryVo
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public List<CertonaCategoryVO> getCategoryDetails(final boolean isFullDataFeed, final Timestamp lastModifiedDate)
			throws BBBSystemException, BBBBusinessException {
		logDebug("CertonaCatalogFeedTools [getCategoryDetails]");
		RepositoryItem[] catItems;
		final List<CertonaCategoryVO> catVOList = new ArrayList<CertonaCategoryVO>();
		if (isFullDataFeed || lastModifiedDate == null) {
			catItems = getRepoForFullFeed(BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
			logDebug("Fetch data for full category feed :isFullDataFeed: " + isFullDataFeed + " lastModifiedDate:"
					+ lastModifiedDate);
		} else {
			catItems = getRepoForIncrementalFeed(lastModifiedDate, BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
			logDebug("Fetch data for incremental category feed lastModifiedDate:" + lastModifiedDate);
		}

		if (catItems != null && catItems.length > 0) {
			for (int i = 0; i < catItems.length; i++) {
				final CertonaCategoryVO categoryVO = this.populateCategoryVO(catItems[i]);
				if (categoryVO != null) {
					catVOList.add(categoryVO);
				}
			}
		} else {
			logDebug("No data available in repository for category feed");
			throw new BBBBusinessException(BBBCatalogErrorCodes.NO_DATA_FOR_CATEGORY_FEED,BBBCatalogErrorCodes.NO_DATA_FOR_CATEGORY_FEED);
		}
		logDebug("Exiting getCategoryDetails of CertonaCatalogFeedTools");
		return catVOList;
	}

	/**
	 * the method sets the properties in CertonaCategoryVo corresponding to a
	 * particular repository item
	 * 
	 * @param categoryRepositoryItem
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private CertonaCategoryVO populateCategoryVO(final RepositoryItem categoryRepoItem) {
		
		CertonaCategoryVO categoryVO = null;
		if (categoryRepoItem != null) {
			logDebug("CertonaCatalogFeedTools [populateCategoryVO] Fetch data forcategory Id:"
					+ categoryRepoItem.getRepositoryId());
			categoryVO = setCatBasicproperties(categoryRepoItem);
			final List<RepositoryItem> subCategories = (List<RepositoryItem>) categoryRepoItem
					.getPropertyValue(BBBCatalogConstants.CHILD_CATEGORIES_PROPERTY_NAME);
			final List<String> subCatList = new ArrayList<String>();
			if (subCategories != null && !subCategories.isEmpty()) {
				for (int i = 0; i < subCategories.size(); i++) {
					subCatList.add(subCategories.get(i).getRepositoryId());
				}
			} else {
				subCatList.add("");
			}
			categoryVO.setSubCatIds(subCatList);
			final Set<String> assocSitesSet = (Set<String>) categoryRepoItem
					.getPropertyValue(BBBCatalogConstants.SITE_IDS_CATEGORY_PROPERTY_NAME);
			final List<String> assocSiteList = new ArrayList<String>();
			if (assocSitesSet != null && !assocSitesSet.isEmpty()) {
				assocSiteList.addAll(assocSitesSet);
			} else {
				assocSiteList.add("");
			}
			categoryVO.setAssocSites(assocSiteList);
			final List<String> childProductsList = new ArrayList<String>();
			final List<RepositoryItem> childProducts = (List<RepositoryItem>) categoryRepoItem
					.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_CATEGORY_PROPERTY_NAME);
			if (childProducts != null && !childProducts.isEmpty()) {
				for (int i = 0; i < childProducts.size(); i++) {
					childProductsList.add(childProducts.get(i).getRepositoryId());
				}
			} else {
				childProductsList.add("");
			}
			categoryVO.setChildProducts(childProductsList);
		}

		return categoryVO;
	}
	/***
	 * The method sets basic properties of category for certona feed
	 * @param categoryRepoItem
	 * @return CertonaCategoryVO
	 */
	private CertonaCategoryVO setCatBasicproperties(RepositoryItem categoryRepoItem){
		final CertonaCategoryVO categoryVO = new CertonaCategoryVO();
		categoryVO.setCategoryId((String) categoryRepoItem.getRepositoryId());
		if (categoryRepoItem.getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME) != null) {
			categoryVO.setIsCollege((Boolean) categoryRepoItem
					.getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME));
		}
		if (categoryRepoItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME) != null) {
			categoryVO.setCategoryName((String) categoryRepoItem
					.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME));
		}
		if (categoryRepoItem.getPropertyValue(BBBCatalogConstants.CREATION_DATE_CATEGORY_PROPERTY_NAME) != null) {
			categoryVO.setCreationDate((Timestamp) categoryRepoItem
					.getPropertyValue(BBBCatalogConstants.CREATION_DATE_CATEGORY_PROPERTY_NAME));
		}
		if (categoryRepoItem.getPropertyValue(BBBCatalogConstants.DESCRIPTION_CATEGORY_PROPERTY_NAME) != null) {
			categoryVO.setShortDesc((String) categoryRepoItem
					.getPropertyValue(BBBCatalogConstants.DESCRIPTION_CATEGORY_PROPERTY_NAME));
		}
		if (categoryRepoItem.getPropertyValue(BBBCatalogConstants.LONG_DESCRIPTION_CATEGORY_PROPERTY_NAME) != null) {
			categoryVO.setLongDesc((String) categoryRepoItem
					.getPropertyValue(BBBCatalogConstants.LONG_DESCRIPTION_CATEGORY_PROPERTY_NAME));
		}
		if (categoryRepoItem.getPropertyValue(BBBCatalogConstants.SHOP_GUIDE_CATEGORY_PROPERTY_NAME) != null) {
			categoryVO.setLongDesc((String) categoryRepoItem
					.getPropertyValue(BBBCatalogConstants.SHOP_GUIDE_CATEGORY_PROPERTY_NAME));
		}
		if (categoryRepoItem.getPropertyValue(BBBCatalogConstants.CAT_KEYWORDS_CATEGORY_PROPERTY_NAME) != null) {
			categoryVO.setLongDesc((String) categoryRepoItem
					.getPropertyValue(BBBCatalogConstants.CAT_KEYWORDS_CATEGORY_PROPERTY_NAME));
		}
		if (categoryRepoItem
				.getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_CATEGORY_PROPERTY_NAME) != null) {
			categoryVO.setCategoryImage((String) categoryRepoItem
					.getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_CATEGORY_PROPERTY_NAME));
		}
		if (categoryRepoItem.getPropertyValue(BBBCatalogConstants.NODE_TYPE_CATEGORY_PROPERTY_NAME) != null) {
			categoryVO.setCategoryDisplayType((String) categoryRepoItem
					.getPropertyValue(BBBCatalogConstants.NODE_TYPE_CATEGORY_PROPERTY_NAME));
		}
		categoryVO.setActive(this.getCatalogTools().isCategoryActive(categoryRepoItem));
		return categoryVO;
	}

	/**
	 * This method fetches properties for Product to be sent in certona feed if
	 * full feed is required or if last modified date is null ,full data for
	 * Product is fetched.For incremental feed data that has been modified since
	 * last feed is only fetched
	 * 
	 * @param isFullDataFeed
	 *          :if full feed is required=true else false
	 * @param lastModifiedDate
	 *          :date since last feed was run
	 * @return List of CertonaProductVO
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public List<RepositoryItem> getProductDetails(final boolean isFullDataFeed, final Timestamp lastModifiedDate)
			throws BBBSystemException, BBBBusinessException {
		logDebug("CertonaCatalogFeedTools [getProductDetails]");
		RepositoryItem[] productItems = null;

		if (isFullDataFeed || lastModifiedDate == null) {
			productItems = getRepoForFullFeed(BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
			logDebug("Fetch data for full product feed :isFullDataFeed: " + isFullDataFeed + " lastModifiedDate:"
					+ lastModifiedDate);
		} else {
			productItems = getRepoForIncrementalFeed(lastModifiedDate, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
			logDebug("Fetch data for incremental Product feed lastModifiedDate:" + lastModifiedDate);

		}

		logDebug("Exiting getProductDetails of CertonaCatalogFeedTools");
		if(productItems!=null){
			return Arrays.asList(productItems);
		}
		else{
			return null;
		}
	}
	/**
	 * The method gets data from repository for sku feed depending on whether full
	 * or incremental feed is required
	 * @param isFullDataFeed
	 * @param lastModifiedDate
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */

	public List<RepositoryItem> getSKUDetailsRepo(final boolean isFullDataFeed, final Timestamp lastModifiedDate)
			throws BBBSystemException, BBBBusinessException {
		RepositoryItem[] skuItems = null;

		if (isFullDataFeed || lastModifiedDate == null) {
			skuItems = getSkuRepoForFullFeed(BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
			logDebug("Fetch data for full sku feed :isFullDataFeed: " + isFullDataFeed + " lastModifiedDate:"
					+ lastModifiedDate);
		} else {
			skuItems = getSkuRepoForIncrementalFeed(lastModifiedDate, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
			logDebug("Fetch data for incremental sku feed lastModifiedDate:" + lastModifiedDate);
		}
		if(skuItems!=null){
			return Arrays.asList(skuItems);
		}
		else{
			return null;
		}
	}
	/**
	 * the method sets the properties in CertonaProductVO corresponding to a
	 * particular repository item
	 * 
	 * @param prodRepoItem
	 * @return CertonaProductVO
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	@SuppressWarnings("unchecked")
	public CertonaProductVO populateProductVO(final RepositoryItem prodRepoItem) throws BBBSystemException,
	BBBBusinessException {
		logDebug("CertonaCatalogFeedTools [populateProductVO] Fetch data for product Id:"
				+ prodRepoItem.getRepositoryId());
		//BazaarVoiceProductVO bvReviews = null;
		final CertonaProductVO productVO = new CertonaProductVO();
		if (prodRepoItem != null) {
			productVO.setProductId(prodRepoItem.getRepositoryId());
			productVO.setProductType((String) prodRepoItem
					.getPropertyValue(BBBCatalogConstants.PRODUCT_TYPE_PRODUCT_PROPERTY_NAME));
			String prdtkeywrds = "";
			if (prodRepoItem.getPropertyValue(BBBCatalogConstants.KEYWRDS_PRODUCT_PROPERTY_NAME) != null) {
				final List<String> prdtKeywrdsList = (List<String>) prodRepoItem
						.getPropertyValue(BBBCatalogConstants.KEYWRDS_PRODUCT_PROPERTY_NAME);
				prdtkeywrds = prdtKeywrdsList.toString();
			}
			productVO.setProductKeyWrds(prdtkeywrds);
			if (prodRepoItem.getPropertyValue(BBBCatalogConstants.CREATION_DATE_PRODUCT_PROPERTY_NAME) != null) {
				productVO.setCreationDate((Timestamp) (prodRepoItem
						.getPropertyValue(BBBCatalogConstants.CREATION_DATE_PRODUCT_PROPERTY_NAME)));
			}
			if (prodRepoItem.getPropertyValue(BBBCatalogConstants.ENABLE_DATE_PRODUCT_PROPERTY_NAME) != null) {
				productVO.setEnableDate((Date) (prodRepoItem
						.getPropertyValue(BBBCatalogConstants.ENABLE_DATE_PRODUCT_PROPERTY_NAME)));
			}
			if (prodRepoItem.getPropertyValue(BBBCatalogConstants.COLLEGE_PRODUCT_PROPERTY_NAME) != null) {
				productVO.setCollegeId((String) prodRepoItem
						.getPropertyValue(BBBCatalogConstants.COLLEGE_PRODUCT_PROPERTY_NAME));
			}
			final RepositoryItem rollUpRepo = (RepositoryItem) prodRepoItem
					.getPropertyValue(BBBCatalogConstants.PRODUCT_ROLL_UP_PRODUCT_PROPERTY_NAME);
			if (rollUpRepo != null
					&& rollUpRepo.getPropertyValue(BBBCatalogConstants.ROLLUP_ATTRIBUTE_ROLLUP_PROPERTY_NAME) != null) {

				productVO.setProductRollupType((String) rollUpRepo
						.getPropertyValue(BBBCatalogConstants.ROLLUP_ATTRIBUTE_ROLLUP_PROPERTY_NAME));
			}

			if (prodRepoItem.getPropertyValue(BBBCatalogConstants.SWATCH_PRODUCT_PROPERTY_NAME) != null) {
				productVO.setSwatchFlag((Boolean) prodRepoItem
						.getPropertyValue(BBBCatalogConstants.SWATCH_PRODUCT_PROPERTY_NAME));
			}
			final List<RepositoryItem> skuRepoItems = (List<RepositoryItem>) prodRepoItem
					.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);
			if (skuRepoItems != null) {
				final List<String> childSkuIdList = new ArrayList<String>();
				for (int i = 0; i < skuRepoItems.size(); i++) {
					childSkuIdList.add(skuRepoItems.get(i).getRepositoryId());
				}
				productVO.setChildSKUs(childSkuIdList);
			}
			if (prodRepoItem.getPropertyValue(BBBCatalogConstants.GIFT_CERTIFICATE_PRODUCT_PROPERTY_NAME) != null) {
				productVO.setGiftCert((Boolean) prodRepoItem
						.getPropertyValue(BBBCatalogConstants.GIFT_CERTIFICATE_PRODUCT_PROPERTY_NAME));
			}
			// if product is a collection populate child product related attributes
			Boolean isCollection = false;
			Boolean isLeadProduct = false;
			if (prodRepoItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) != null
					&& prodRepoItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) != null) {
				isCollection = (Boolean) prodRepoItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME);
				isLeadProduct = (Boolean) prodRepoItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME);
				if (isCollection || isLeadProduct) {
					productVO.setCollection(true);
					productVO.setChildProductAttr(this.getChildProductAttr(prodRepoItem));
				} else {
					productVO.setCollection(false);
				}
			}
			// Brand related properties
			final BrandVO brandVO = getProductBrands(prodRepoItem);
			if (brandVO != null) {
				productVO.setProductBrand(brandVO);
			}
			Map<String, SiteSpecificProductAttr> prodAttrMap=new HashMap<String, SiteSpecificProductAttr>();
			// get site specific data for default site
			final SiteSpecificProductAttr siteSpecProd = this.defaultSiteProdDetail(prodRepoItem);
			final List<String> defaultSiteList = this.catalogTools.getContentCatalogConfigration(this.getDefaultSiteId());
			String defaultSite = "";
			if (siteSpecProd != null && defaultSiteList != null && !defaultSiteList.isEmpty()) {
				defaultSite = defaultSiteList.get(0);
				prodAttrMap.put(defaultSite, siteSpecProd);
			}
			logDebug("prodAttrMap: Before putAll: " + prodAttrMap);
			// get site specific data from translations and add in map
			prodAttrMap.putAll( getSiteProductAttr(prodRepoItem,siteSpecProd));
			logDebug("prodAttrMap: After putAll: " + prodAttrMap);
			if (siteSpecProd != null && defaultSiteList != null && !defaultSiteList.isEmpty()) {
			prodAttrMap.put(defaultSite, siteSpecProd);
			}
			logDebug("prodAttrMap: After if stireSpecProd check: " + prodAttrMap);

			/*
			 Note that there are 4 parameters to check if a product is alive
			 * Start date > current date End date < current date disable false weboffered true
			 * out of this start and end date is common for all site but disable and weboffered flag is 
			 * site dependent.Hence check for start and end date is made in isPrdActiveForStartEndDate
			 * and rest of the check is made here once the map is fully prepared */
			if(prodAttrMap!=null && !prodAttrMap.isEmpty()){
				final Set<String> siteKey=prodAttrMap.keySet();
				for(String key:siteKey){
					final boolean dateIsActive = isPrdActiveForStartEndDate(prodRepoItem);
					final boolean disable=prodAttrMap.get(key).isDisable();
					final boolean webOffered=prodAttrMap.get(key).isWeboffered();
					if(! dateIsActive|| disable || !webOffered){
						prodAttrMap.get(key).setActiveProduct(false) ;
					}
					else{
						prodAttrMap.get(key).setActiveProduct(true) ;
					}
				}
			}

			productVO.setSitePrdtAttrMap(prodAttrMap);
			/**
			 * LTL Start - Added LTL flag to certona feed 
			 */
			if (prodRepoItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) != null) {
				productVO.setLtlFlag((Boolean) prodRepoItem
						.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU));
		}

			/**
			 * LTL End
			 */
			// Fix for defect BBBSL-3068
			if (null != prodRepoItem.getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME) ) {
				productVO.setProductUrl((String)(prodRepoItem
						.getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME)));
			}
		}

		// set image details for product
		final ImageVO productImage = getPrdImages(prodRepoItem);
		productVO.setProductImages(productImage);
		
		//set BV ratings data
		//bvReviews = this.getCatalogTools().getBazaarVoiceDetails(prodRepoItem.getRepositoryId());
		//productVO.setAverageOverallRating(bvReviews.getAverageOverallRating());		
		//productVO.setTotalReviewCount(bvReviews.getTotalReviewCount());
		
		return productVO;
	}

	/**
	 * Method to return list of child product attributes in case a product is a
	 * collection or a lead product
	 * 
	 * @param prodRepoItem
	 * @return List of CollectionChildProductAttr with child attributes set
	 */

	@SuppressWarnings("unchecked")
	private List<CollectionChildProductAttr> getChildProductAttr(final RepositoryItem prodRepoItem) {

		logDebug("CertonaCatalogFeedTools [getChildProductAttr] Product is a collection fetching child products:"
				+ prodRepoItem.getRepositoryId());
		final List<RepositoryItem> childProdRelList = (List<RepositoryItem>) prodRepoItem
				.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME);
		List<CollectionChildProductAttr> subProductVOList = null;
		if (childProdRelList != null && !childProdRelList.isEmpty()) {
			subProductVOList = new ArrayList<CollectionChildProductAttr>();
			RepositoryItem childProdItem =null;
			CollectionChildProductAttr childPrdtAttr  =null;
			RepositoryItem rollUpItem =null;
			for (int i = 0; i < childProdRelList.size(); i++) {
				final RepositoryItem bbbPrdRelnItem = childProdRelList.get(i);
				childPrdtAttr= new CollectionChildProductAttr();
				childProdItem	= (RepositoryItem) bbbPrdRelnItem
						.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_PRODUCT_RELATION_PROPERTY_NAME);
				if (childProdItem != null) {
					childPrdtAttr.setChildProductId(childProdItem.getRepositoryId());
				}
				rollUpItem= (RepositoryItem) bbbPrdRelnItem
						.getPropertyValue(BBBCatalogConstants.COLL_ROLLUP_TYPE_COLL_PRODUCT_PROPERTY_NAME);
				if (rollUpItem != null) {
					childPrdtAttr.setChildRollUpType((String) rollUpItem
							.getPropertyValue(BBBCatalogConstants.ROLLUP_ATTR_COLL_PRODUCT_PROPERTY_NAME));
				}
				Boolean likeUnlike = false;
				if (bbbPrdRelnItem.getPropertyValue(BBBCatalogConstants.LIKE_UNLIKE_COLL_PRODUCT_PROPERTY_NAME) != null) {
					likeUnlike = (Boolean) bbbPrdRelnItem
							.getPropertyValue(BBBCatalogConstants.LIKE_UNLIKE_COLL_PRODUCT_PROPERTY_NAME);
				}
				childPrdtAttr.setLikeUnlike(likeUnlike);
				subProductVOList.add(childPrdtAttr);
			}
		}
		return subProductVOList;
	}

	/**
	 * The method returns VO with site specific attributes of the default site
	 * 
	 * @param prodRepoItem
	 * @return
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public SiteSpecificProductAttr defaultSiteProdDetail(final RepositoryItem prodRepoItem) throws BBBSystemException, BBBBusinessException {
		logDebug("CertonaCatalogFeedTools [defaultSiteProdDetail] site specific data for default site:");
		SiteSpecificProductAttr siteSpecProd = null;
		if (prodRepoItem != null) {
			siteSpecProd = new SiteSpecificProductAttr();
			siteSpecProd.setName((String) prodRepoItem
					.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME));
			siteSpecProd.setShortDescription((String) prodRepoItem
					.getPropertyValue(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME));
			siteSpecProd.setLongDescription((String) prodRepoItem
					.getPropertyValue(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME));
			siteSpecProd.setPriceRangeDescription((String) prodRepoItem
					.getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME));
			siteSpecProd.setActiveProduct(this.catalogTools.isProductActive(prodRepoItem));
			siteSpecProd.setSkuLowPrice((String) prodRepoItem
					.getPropertyValue(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME));
			siteSpecProd.setSkuHighPrice((String) prodRepoItem
					.getPropertyValue(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME));
					siteSpecProd.setWeboffered(((Boolean)prodRepoItem
					.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME)).booleanValue());
			siteSpecProd.setDisable(((Boolean)prodRepoItem
					.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME)).booleanValue());
			/** Added Site Specific Enable Date for R2.1 #53A */
			siteSpecProd.setEnableDate((Date) prodRepoItem
					.getPropertyValue(BBBCatalogConstants.ENABLE_DATE_PRODUCT_PROPERTY_NAME));
			
			String defaultSite = "";
			final List<String> defaultSiteList = this.catalogTools.getContentCatalogConfigration(this.getDefaultSiteId());
			if (defaultSiteList != null && !defaultSiteList.isEmpty()) {
				defaultSite = defaultSiteList.get(0);
			}
			
			//margin for default site scope #153.
			String margin = calculateMargin(prodRepoItem, defaultSite);
			
			String tier = calculateTier(margin);
			if(!(BBBUtility.isEmpty(tier)))
				siteSpecProd.setTier(tier);
			
			/*calling getBazaarVoiceDetails method to get review and ratings for product for non default site ids*/
			BazaarVoiceProductVO bazaarVoiceProductVO = this.getCatalogTools().getBazaarVoiceDetails(prodRepoItem.getRepositoryId(),defaultSite);
			if(null!= bazaarVoiceProductVO){
				siteSpecProd.setTotalReviewCount(bazaarVoiceProductVO.getTotalReviewCount());
				siteSpecProd.setAverageOverallRating(bazaarVoiceProductVO.getAverageOverallRating());
			}
			
		}
		return siteSpecProd;
	}
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	public String calculateMargin(final RepositoryItem prodRepoItem, String siteId) throws BBBSystemException {
		
		boolean isCollection = (Boolean)(prodRepoItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME));

		boolean isLeadProduct = (Boolean)(prodRepoItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME));

		String margin = "";
	    
		  if(this.catalogTools.isProductActive(prodRepoItem, siteId)){
			  double finalMargin = 0;
			  if(isCollection){
				  List<RepositoryItem> productChildProducts  = (List<RepositoryItem>) prodRepoItem.getPropertyValue(BBBCatalogConstants.PRODUCT_CHILD_PRODUCTS);
				 
					  logDebug("childProducts Number******** "+productChildProducts.size());
				  
				  
				  if(productChildProducts !=null && !(productChildProducts.isEmpty())){
				  for(RepositoryItem productChildProduct : productChildProducts){

					  RepositoryItem product = (RepositoryItem) productChildProduct.getPropertyValue(BBBCatalogConstants.PRODUCT_ID);
					  if(product == null){
						  continue;
					  }
					  
						  logDebug("product ID******** "+product.getRepositoryId());
					  
					  double profitOnProduct = 0;
					  List<RepositoryItem> childSKUS = null;
					  childSKUS = (List<RepositoryItem>) product.getPropertyValue(BBBCatalogConstants.CHILD_SKUS);
					  if(childSKUS == null || childSKUS.isEmpty()){
						  continue;
					  }
					  for(RepositoryItem sku : childSKUS){
						 
							  logDebug("sku id******** "+sku.getRepositoryId());
						  
						  Map<String,Double> priceMap = getMarginForSku(sku,siteId);
						  if(priceMap ==null || priceMap.isEmpty()){
							  continue;
						  }
						  Double profit = priceMap.get("profit");
						  profitOnProduct = profitOnProduct + profit;
					  }
					  profitOnProduct = profitOnProduct/childSKUS.size();
					  
					  DecimalFormat forma = new DecimalFormat(BBBCatalogConstants.DECIMAL_FORMAT);
					  String marginOnProduct = forma.format(profitOnProduct);
					  
						  logDebug("final Margin******** ***************************"+marginOnProduct + "for site " +siteId);
					  
					
					  finalMargin = finalMargin + profitOnProduct;
				  }
				}
				  if(productChildProducts !=null && !productChildProducts.isEmpty()){
					  finalMargin = finalMargin/productChildProducts.size();
				  }
			  }
			  else {
				  List<RepositoryItem> childSKUS = null;
				  double profitOnProduct = 0;
				  childSKUS = (List<RepositoryItem>) prodRepoItem.getPropertyValue(BBBCatalogConstants.CHILD_SKUS);
				  for(RepositoryItem sku : childSKUS){
					  
						  logDebug("SKu id ******** "+sku.getRepositoryId());
					  
					  Map<String,Double> priceMap = getMarginForSku(sku,siteId);
					  if(priceMap ==null || priceMap.isEmpty()){
						  continue;
					  }
					  Double profit = priceMap.get("profit");
					  profitOnProduct = profitOnProduct + profit;
				  }
				  if(childSKUS !=null && !childSKUS.isEmpty()){
					  profitOnProduct = profitOnProduct/childSKUS.size();
				  }
				  
					  logDebug("Total profit  **********" + profitOnProduct+ "*** skus ===" +childSKUS.size() +"final profit ==="+profitOnProduct);
				  
				  finalMargin = profitOnProduct;
			  }

			  DecimalFormat format = new DecimalFormat("00.00");
			  if(Double.compare(finalMargin, 0) != BBBCoreConstants.ZERO){
				  margin = format.format(finalMargin);
			  }
			 
				  logDebug("Margin on product till two decimal*******==== "+margin + "for site ==== " +siteId);
			   
		  }
		
	  return margin;
	}
	
	private String calculateTier(String margin) {
		  String tier = "";
		  if(BBBUtility.isEmpty(margin)){
			  return tier;
		  }
		  Double marginPercentage = Double.parseDouble(margin);
		  
		  if(marginPercentage <25){
			  tier = "E";
		  }else if((25<= marginPercentage) && (marginPercentage <36)){
			  tier = "D";
		  }else if((36<= marginPercentage) && (marginPercentage <49)){
			  tier = "C";
		  }else if((49<= marginPercentage) && (marginPercentage <60)){
			  tier = "B";
		  }else if(marginPercentage >=60){
			  tier = "A";
		  }
		  return tier;
	}
	
  public Map<String,Double> getMarginForSku(RepositoryItem sku,String siteId) throws BBBSystemException{
	  String siteSpecificCost = null;
	  String listPriceListId =null;
	  String SalePriceListId =null;
	  Map<String,Double> priceMap = new java.util.HashMap<String, Double>();
	  try{
	  RepositoryItem siteItem = getSiteRepository().getItem(siteId, BBBCatalogConstants.SITE_CONFIGURATION);
	  listPriceListId = ( (RepositoryItem)siteItem.getPropertyValue(BBBCatalogConstants.DEFAULT_PRICE_LIST)).getRepositoryId();
	  SalePriceListId = ( (RepositoryItem)siteItem.getPropertyValue(BBBCatalogConstants.DEFAULT_SALE_PRICE_LIST)).getRepositoryId();

	  if(siteId.equalsIgnoreCase("BedBathUS")){
		  siteSpecificCost = (String) sku.getPropertyValue(BBBCatalogConstants.COST_DEFAULT);
	  }
	  else{
		 @SuppressWarnings("unchecked")
		 Set<RepositoryItem> allTranslation =  (Set<RepositoryItem>) sku.getPropertyValue(BBBCatalogConstants.SKU_TRANSLATIONS);

		  for(RepositoryItem translation : allTranslation ){

			  RepositoryItem siteRepo = (RepositoryItem) translation.getPropertyValue(BBBCatalogConstants.SITE);
			  String attributeName = (String) translation.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_NAME);

			  String siteName = siteRepo.getRepositoryId();

			  if(siteId.equalsIgnoreCase(siteName) && attributeName.equalsIgnoreCase(BBBCatalogConstants.COST_DEFAULT)){
				  siteSpecificCost = (String) translation.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_STRING);
			  }
		  }
	  }
	  if(BBBUtility.isEmpty(siteSpecificCost)){
		  //logDebug("putting default cost  ");
		  if( siteId.equals("BuyBuyBaby")){
			  siteSpecificCost = (String) sku.getPropertyValue(BBBCatalogConstants.COST_DEFAULT);
		  }
		  if(BBBUtility.isEmpty(siteSpecificCost)){
			  
				  logDebug("NO cost found for this sku in default property also ");
			  
			  return priceMap;
		  }
	  }
	  String priceList = null;
	  boolean onSale = (Boolean) sku.getPropertyValue(BBBCatalogConstants.ON_SALE);
	  if(onSale){
		  priceList = SalePriceListId;

	  }else{
		  priceList = listPriceListId;
	  }
	  RepositoryView view = getPriceListManager().getPriceListRepository().getView(BBBCatalogConstants.PRICE2);
	  RqlStatement statement = RqlStatement.parseRqlStatement("skuId = ?0 AND priceList =?1 ");
	  Object params[] = new Object[2];
	  params[0] = new String(sku.getRepositoryId());
	  params[1] = priceList;
	  RepositoryItem[] items = statement.executeQuery (view, params);
	 
		  logDebug("items length **********" + items.length);
	 
	  double sellingPrice =  (Double) items[0].getPropertyValue(BBBCatalogConstants.LIST_PRICE); 

	  logDebug("sellingPrice **********" + sellingPrice);
	  double costPrice = Double.parseDouble(siteSpecificCost);
	  logDebug("costPrice **********" + costPrice);
	  double profit = sellingPrice - costPrice;

		  logDebug("sellingPrice **********" + sellingPrice);
		  logDebug("costPrice **********" + costPrice);
		  logDebug("profit **********" + profit);
	  
	  if(Double.compare(sellingPrice, 0) != BBBCoreConstants.ZERO){
		  profit = (profit/sellingPrice)*100;
	  }

	  priceMap.put("sellingPrice", sellingPrice);
	  priceMap.put("costPrice", costPrice);
	  priceMap.put("profit", profit);
	  return priceMap;
	  }catch (RepositoryException e) {
          logError("Catalog API Method Name [getProductDetails]: RepositoryException ");
          throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,e);
          }
  }


	/**
	 * the method returns the ImageVO for the product repository item. ImageVo is
	 * updated with information about images from the repository
	 * 
	 * @param prodRepoItem
	 * @return
	 */
	private ImageVO getPrdImages(final RepositoryItem prodRepoItem) {
		logDebug("CertonaCatalogFeedTools [getPrdImages]");
		final ImageVO productImage = new ImageVO();

		productImage.setLargeImage((String) prodRepoItem
				.getPropertyValue(BBBCatalogConstants.LARGE_IMAGE_IMAGE_PROPERTY_NAME));
		productImage.setSmallImage((String) prodRepoItem.getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_IMAGE_PROPERTY_NAME));
		productImage.setSwatchImage( (String) prodRepoItem
				.getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME));
		productImage.setMediumImage((String) prodRepoItem
				.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME));
		productImage.setThumbnailImage((String) prodRepoItem
				.getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME));
		if (prodRepoItem.getPropertyValue(BBBCatalogConstants.ZOOM_INDEX_PATH_PRODUCT_PROPERTY_NAME) != null) {
			productImage.setZoomImageIndex((Integer) prodRepoItem
					.getPropertyValue(BBBCatalogConstants.ZOOM_INDEX_PATH_PRODUCT_PROPERTY_NAME));
		}
		productImage.setZoomImage((String) prodRepoItem
				.getPropertyValue(BBBCatalogConstants.ZOOM_IMAGE_IMAGE_PROPERTY_NAME));
		if (prodRepoItem.getPropertyValue(BBBCatalogConstants.ANYWHERE_ZOOM_PRODUCT_PROPERTY_NAME) != null) {
			productImage.setAnywhereZoomAvailable((Boolean) prodRepoItem
					.getPropertyValue(BBBCatalogConstants.ANYWHERE_ZOOM_PRODUCT_PROPERTY_NAME));
		}
		return productImage;
	}

	/**
	 * the method returns the map of site and corresponding site specific product
	 * properties in SiteSpecificProductAttr to determine if the product is active
	 * for the particular site the following logic is determined dateIsActive is
	 * initally set to true.if the current date is not within start and end date
	 * of the product then dateIsActive is set to false and the product will be
	 * inactive for both sites. else weboffered and disabled flag values for the
	 * site is checked.if both the dateIsActive and weboffered and prodDisable
	 * check is passed then product is certified as active for the site
	 * 
	 * @param prodRepoItem
	 * @return
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, SiteSpecificProductAttr> getSiteProductAttr(final RepositoryItem prodRepoItem,final SiteSpecificProductAttr defaultSiteAttr) throws BBBSystemException, BBBBusinessException {
		
		logDebug("CertonaCatalogFeedTools [getSiteProductAttr]");
		

		Map<String, SiteSpecificProductAttr> siteProductVOMap = new HashMap<String, SiteSpecificProductAttr>();
		final Set<RepositoryItem> prdTranslations = (Set<RepositoryItem>) prodRepoItem
				.getPropertyValue(BBBCatalogConstants.PROD_TRANS_SITE_TRANS_PROPERTY_NAME);
		for (RepositoryItem transRepo : prdTranslations) {

			final RepositoryItem siteItem = (RepositoryItem) transRepo
					.getPropertyValue(BBBCatalogConstants.SITE_PRODUCT_PROPERTY_NAME);
			if (siteItem != null) {
				final String siteId = siteItem.getRepositoryId();
				if (siteProductVOMap.containsKey(siteId)) {
					// if the site id key is already present in the map add properties to
					// already set SiteSpecificProductAttr object
					siteProductVOMap=this.addSimpleSiteAttrForProd(siteProductVOMap, transRepo,  siteId,defaultSiteAttr);
				} else {
					siteProductVOMap=this.addPrdSiteAttrVOToMap(siteProductVOMap, transRepo,  siteId,defaultSiteAttr);

				}
			}
		}
			
		for (String siteId : this.getNonDefaultSiteIds()) {
			siteProductVOMap = setValueFromDefaultSiteForNoTrans(siteId,
					siteProductVOMap, defaultSiteAttr);

			// margin for non-default sites scope #153.
			String margin = "";
			if(siteProductVOMap.get(siteId) != null){
			margin = calculateMargin(prodRepoItem, siteId);
			
			String tier = calculateTier(margin);
			if(!(BBBUtility.isEmpty(tier)))
				siteProductVOMap.get(siteId).setTier(tier);
			}
			/*calling getBazaarVoiceDetails method to get review and ratings for product for non default site ids*/
			BazaarVoiceProductVO bazaarVoiceProductVO = this.getCatalogTools().getBazaarVoiceDetails(prodRepoItem.getRepositoryId(),siteId);
			if(null!= bazaarVoiceProductVO){
				siteProductVOMap.get(siteId).setTotalReviewCount(bazaarVoiceProductVO.getTotalReviewCount());
				siteProductVOMap.get(siteId).setAverageOverallRating(bazaarVoiceProductVO.getAverageOverallRating());
			}
		}

		return siteProductVOMap;
	}
	/**
	 * The method returns if a product is active or not based 
	 * on start date and end date check.
	 * Note that there are 4 paramerts to check if a product is alive
	 * Start date > current date
	 * End date < current date
	 * disable false
	 * weboffered true
	 * out of this start and end date is common for all ste but disable and weboffered flag is 
	 * site dependent.Hence check for start and end date is made here
	 * and rest of the check will be made once the map is fully prepared
	 * @param prodRepoItem
	 * @return
	 */

	private boolean isPrdActiveForStartEndDate(RepositoryItem prodRepoItem){
		boolean dateIsActive = true;
		final Date currentDate = new Date();
		// get the start and end date of the product to determine if the product is
		// active or not
		if (prodRepoItem.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME) != null
				&& prodRepoItem.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME) != null) {
			final Date startDate = (Date) prodRepoItem.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME);
			final Date endDate = (Date) prodRepoItem.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME);
			if (((endDate != null && currentDate.after(endDate)) || (startDate != null && currentDate.before(startDate)))) {
				dateIsActive = false;
			}
		}
		return dateIsActive;
	}
	/**
	 * The method adds a site specific property in an object of SiteSpecificProductAttr when 
	 * the siteId key is first time added in the map siteProductVOMap
	 * @param siteProductVOMap
	 * @param transRepo
	 * @param siteId
	 * @return
	 */
	private Map<String, SiteSpecificProductAttr> addPrdSiteAttrVOToMap (Map<String, SiteSpecificProductAttr> siteProductVOMap,RepositoryItem transRepo, 
			String siteId ,final SiteSpecificProductAttr defaultSiteAttr){

		final String attributeName = (String) transRepo.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_NAME_SITE_TRANS_PROPERTY_NAME);
		SiteSpecificProductAttr siteProdAttr = null;
		logDebug("new key added in the map for siteid:" + siteId+"new site specific attribute for whih data is to be fetched from translations" + attributeName);
		final String translationStringValue=(String) transRepo
				.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_STRING_SITE_TRANS_PROPERTY_NAME);
		Boolean translationBooleanValue=null;
		if( transRepo.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_BOOLEAN_SITE_TRANS_PROPERTY_NAME)!=null){
			translationBooleanValue=(Boolean) transRepo
					.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_BOOLEAN_SITE_TRANS_PROPERTY_NAME);
		}
		Date translationalDateValue = null;
		if( transRepo.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_DATE_SITE_TRANS_PROPERTY_NAME)!=null){
			translationalDateValue=(Date) transRepo
					.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_DATE_SITE_TRANS_PROPERTY_NAME);
		}
		
		if(!StringUtils.isEmpty(translationStringValue) || translationBooleanValue!=null || translationalDateValue!=null){
			logDebug("Set value of attributeName "+attributeName+" from translation translationBooleanValue "+ translationBooleanValue+" translationStringValue "+ translationStringValue+" translationDateValue "+translationalDateValue);
			siteProdAttr=setValueFromTranslation(attributeName, siteProductVOMap, transRepo);
		}
		else{
			logDebug("Set value of attributeName "+attributeName+" from default site value ");
			siteProdAttr=setValueFromDefaultSite(attributeName,  siteProductVOMap, defaultSiteAttr);
		}
		siteProductVOMap.put(siteId, siteProdAttr);
		return siteProductVOMap;
	}
	/**
	 * The method sets the value of the property in the attribute with the value in the translation
	 * The method is used when the SiteSpecificProductAttr VO is added for the first time corresponding to the site in the map 
	 * @param attributeName
	 * @param siteProductVOMap
	 * @param transRepo
	 * @return
	 */

	private SiteSpecificProductAttr   setValueFromTranslation(String attributeName,Map<String, SiteSpecificProductAttr> siteProductVOMap
			,RepositoryItem transRepo){
		final SiteSpecificProductAttr siteProdAttr = new SiteSpecificProductAttr();
		if (attributeName.equalsIgnoreCase(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME)) {
			siteProdAttr.setName((String) transRepo
					.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_STRING_SITE_TRANS_PROPERTY_NAME));
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME)) {
			siteProdAttr.setShortDescription((String) transRepo
					.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_STRING_SITE_TRANS_PROPERTY_NAME));
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME)) {
			siteProdAttr.setLongDescription((String) transRepo
					.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_STRING_SITE_TRANS_PROPERTY_NAME));
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME)) {
			siteProdAttr.setPriceRangeDescription((String) transRepo
					.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_STRING_SITE_TRANS_PROPERTY_NAME));
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME)
				&& transRepo.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_BOOLEAN_SITE_TRANS_PROPERTY_NAME) != null) {
			final Boolean webOffered = (Boolean) transRepo
					.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_BOOLEAN_SITE_TRANS_PROPERTY_NAME);
			siteProdAttr.setWeboffered(webOffered);
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME)
				&& transRepo.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_BOOLEAN_SITE_TRANS_PROPERTY_NAME) != null) {
			final Boolean prodDisable = (Boolean) transRepo
					.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_BOOLEAN_SITE_TRANS_PROPERTY_NAME);
			siteProdAttr.setDisable(prodDisable);
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME)) {
			siteProdAttr.setSkuLowPrice((String) transRepo
					.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_STRING_SITE_TRANS_PROPERTY_NAME));
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME)) {
			siteProdAttr.setSkuHighPrice((String) transRepo
					.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_STRING_SITE_TRANS_PROPERTY_NAME));
		/** Added Site Specific Enable Date for R2.1 #53A */
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.ENABLE_DATE_PRODUCT_PROPERTY_NAME)) {
			siteProdAttr.setEnableDate((Date) transRepo
					.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_DATE_SITE_TRANS_PROPERTY_NAME));
		}
		return siteProdAttr;
	}

	/**
	 * The method adds the value of the property depicted by the attribute name with the value present for the default site
	 * The method is used when the SiteSpecificProductAttr VO is added for the first time corresponding to the site in the map 
	 * @param attributeName
	 * @param siteId
	 * @param siteProductVOMap
	 * @param defaultSiteAttr
	 * @return
	 */
	private SiteSpecificProductAttr setValueFromDefaultSite(String attributeName,Map<String, SiteSpecificProductAttr> siteProductVOMap,
			final SiteSpecificProductAttr defaultSiteAttr){
		final SiteSpecificProductAttr siteProdAttr = new SiteSpecificProductAttr();
		if (attributeName.equalsIgnoreCase(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME)) {
			siteProdAttr.setName( defaultSiteAttr.getName());
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME)) {
			siteProdAttr.setShortDescription(defaultSiteAttr.getShortDescription());
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME)) {
			siteProdAttr.setLongDescription(defaultSiteAttr.getLongDescription());
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME)) {
			siteProdAttr.setPriceRangeDescription(defaultSiteAttr.getPriceRangeDescription());
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME)) {
			siteProdAttr.setWeboffered(defaultSiteAttr.isWeboffered());
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME)){
			siteProdAttr.setDisable(defaultSiteAttr.isDisable());
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME)) {
			siteProdAttr.setSkuLowPrice(defaultSiteAttr.getSkuLowPrice());
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME)) {
			siteProdAttr.setSkuHighPrice(defaultSiteAttr.getSkuHighPrice());
			/** Added Site Specific Enable Date for R2.1 #53A */
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.ENABLE_DATE_PRODUCT_PROPERTY_NAME)) {
			siteProdAttr.setEnableDate(null);
		}
		return siteProdAttr;
	}
	/**
	 * The method adds a site specific property in an object of SiteSpecificProductAttr VO when 
	 * the siteId key is already added in the map siteProductVOMap.
	 * The method adds the property in the VO added in the map's value
	 * @param siteProductVOMap
	 * @param transRepo
	 * @param siteId
	 * @return
	 */

	private Map<String, SiteSpecificProductAttr>  addSimpleSiteAttrForProd(Map<String, SiteSpecificProductAttr> siteProductVOMap,RepositoryItem transRepo, 
			String siteId,final SiteSpecificProductAttr defaultSiteAttr){

		final String attributeName = (String) transRepo
				.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_NAME_SITE_TRANS_PROPERTY_NAME);
		final String translationStringValue=(String) transRepo
				.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_STRING_SITE_TRANS_PROPERTY_NAME);
		Boolean translationBooleanValue=null;
		if( transRepo.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_BOOLEAN_SITE_TRANS_PROPERTY_NAME)!=null){
			translationBooleanValue=(Boolean) transRepo
					.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_BOOLEAN_SITE_TRANS_PROPERTY_NAME);
		}
		Date translationalDateValue = null;
		if( transRepo.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_DATE_SITE_TRANS_PROPERTY_NAME)!=null){
			translationalDateValue=(Date) transRepo
					.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_DATE_SITE_TRANS_PROPERTY_NAME);
		}
		
		logDebug("key is already added in the map for siteid:" + siteId+"new site specific attribute for whih data is to be fetched from translations" + attributeName);
		/*if the value in the translation is not null populate the value of the property with the translation value else use the value of the property for the
		 * default site to populate the value of the property */
		if(!StringUtils.isEmpty(translationStringValue)|| translationBooleanValue!=null || translationalDateValue!=null){
			logDebug("Set value of attributeName "+attributeName+" from translation translationBooleanValue "+ translationBooleanValue+" translationStringValue "+ translationStringValue+" translationDateValue "+translationalDateValue);
			siteProductVOMap=setValueFromTranslation(attributeName, siteId,siteProductVOMap, transRepo);
		}
		else{
			logDebug("Set value of attributeName "+attributeName+" from default site value as translationStringValue or translationBooleanValue or translationalDateValue is null or empty "+ translationBooleanValue+ translationStringValue );
			siteProductVOMap=setValueFromDefaultSite(attributeName, siteId, siteProductVOMap, defaultSiteAttr);
		}
		return siteProductVOMap;
	}
	/**
	 * The method sets the value of the property in the attribute with the value in the translation
	 * @param attributeName
	 * @param siteId
	 * @param siteProductVOMap
	 * @param transRepo
	 * @return
	 */
	private Map<String, SiteSpecificProductAttr>  setValueFromTranslation(String attributeName,String siteId,Map<String, SiteSpecificProductAttr> siteProductVOMap
			,RepositoryItem transRepo){
		if (attributeName.equalsIgnoreCase(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME)) {
			siteProductVOMap.get(siteId).setName(
					(String) transRepo
					.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_STRING_SITE_TRANS_PROPERTY_NAME));
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME)) {
			siteProductVOMap.get(siteId).setShortDescription(
					(String) transRepo
					.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_STRING_SITE_TRANS_PROPERTY_NAME));
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME)) {
			siteProductVOMap.get(siteId).setLongDescription(
					(String) transRepo
					.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_STRING_SITE_TRANS_PROPERTY_NAME));
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME)) {
			siteProductVOMap.get(siteId).setPriceRangeDescription(
					(String) transRepo
					.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_STRING_SITE_TRANS_PROPERTY_NAME));
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME)
				&& transRepo.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_BOOLEAN_SITE_TRANS_PROPERTY_NAME) != null) {
			final Boolean webOffered = (Boolean) transRepo
					.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_BOOLEAN_SITE_TRANS_PROPERTY_NAME);
			siteProductVOMap.get(siteId).setWeboffered(webOffered);
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME)
				&& transRepo.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_BOOLEAN_SITE_TRANS_PROPERTY_NAME) != null) {
			final Boolean prodDisable = (Boolean) transRepo
					.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_BOOLEAN_SITE_TRANS_PROPERTY_NAME);
			siteProductVOMap.get(siteId).setDisable(prodDisable);
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME)) {
			siteProductVOMap.get(siteId).setSkuLowPrice(
					(String) transRepo
					.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_STRING_SITE_TRANS_PROPERTY_NAME));
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME)) {
			siteProductVOMap.get(siteId).setSkuHighPrice(
					(String) transRepo
					.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_STRING_SITE_TRANS_PROPERTY_NAME));
		
		/** Added Site Specific Enable Date for R2.1 #53A */ 
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.ENABLE_DATE_PRODUCT_PROPERTY_NAME)) {
			siteProductVOMap.get(siteId).setEnableDate(
					(Date) transRepo
					.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_DATE_SITE_TRANS_PROPERTY_NAME)); 
		}
		return siteProductVOMap;
	}
	/**
	 * The method adds the value of the property depicted by the attribute name with the value present for the default site
	 * @param attributeName
	 * @param siteId
	 * @param siteProductVOMap
	 * @param defaultSiteAttr
	 * @return
	 */
	private Map<String, SiteSpecificProductAttr>  setValueFromDefaultSite(String attributeName,String siteId,Map<String, SiteSpecificProductAttr> siteProductVOMap,
			final SiteSpecificProductAttr defaultSiteAttr){
		if (attributeName.equalsIgnoreCase(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME)) {
			siteProductVOMap.get(siteId).setName(
					(String) defaultSiteAttr.getName());
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME)) {
			siteProductVOMap.get(siteId).setShortDescription(defaultSiteAttr.getShortDescription());
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME)) {
			siteProductVOMap.get(siteId).setLongDescription(defaultSiteAttr.getLongDescription());
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME)) {
			siteProductVOMap.get(siteId).setPriceRangeDescription(defaultSiteAttr.getPriceRangeDescription());
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME)) {
			siteProductVOMap.get(siteId).setWeboffered(defaultSiteAttr.isWeboffered());
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME)){
			siteProductVOMap.get(siteId).setDisable(defaultSiteAttr.isDisable());
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME)) {
			siteProductVOMap.get(siteId).setSkuLowPrice(defaultSiteAttr.getSkuLowPrice());
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME)) {
			siteProductVOMap.get(siteId).setSkuHighPrice(defaultSiteAttr.getSkuHighPrice());
		
		/** Added Site Specific Enable Date for R2.1 #53A */ 
		} else if (attributeName.equalsIgnoreCase(BBBCatalogConstants.ENABLE_DATE_PRODUCT_PROPERTY_NAME)) {
			siteProductVOMap.get(siteId).setEnableDate(null); 
		}
		return siteProductVOMap;
	}
	/**
	 * The method adds the value of the property depicted by the attribute name with the value present for the default site
	 * @param attributeName
	 * @param siteId
	 * @param siteProductVOMap
	 * @param defaultSiteAttr
	 * @return
	 */
	private Map<String, SiteSpecificProductAttr>  setValueFromDefaultSiteForNoTrans(String siteId,Map<String, SiteSpecificProductAttr> siteProductVOMap,
			final SiteSpecificProductAttr defaultSiteAttr){
		if(siteProductVOMap!=null && !siteProductVOMap.isEmpty() && siteProductVOMap.get(siteId)!=null  ){
			if ( StringUtils.isEmpty(siteProductVOMap.get(siteId).getName())) {
				siteProductVOMap.get(siteId).setName(
						(String) defaultSiteAttr.getName());
			}  if ( StringUtils.isEmpty(siteProductVOMap.get(siteId).getShortDescription())) {
				siteProductVOMap.get(siteId).setShortDescription(defaultSiteAttr.getShortDescription());
			}  if (StringUtils.isEmpty(siteProductVOMap.get(siteId).getLongDescription())) {
				siteProductVOMap.get(siteId).setLongDescription(defaultSiteAttr.getLongDescription());
			}  if (StringUtils.isEmpty(siteProductVOMap.get(siteId).getPriceRangeDescription())) {
				siteProductVOMap.get(siteId).setPriceRangeDescription(defaultSiteAttr.getPriceRangeDescription());
			}  if ( StringUtils.isEmpty(siteProductVOMap.get(siteId).getSkuLowPrice())) {
				siteProductVOMap.get(siteId).setSkuLowPrice(defaultSiteAttr.getSkuLowPrice());
			}  if ( StringUtils.isEmpty(siteProductVOMap.get(siteId).getSkuHighPrice())) {
				siteProductVOMap.get(siteId).setSkuHighPrice(defaultSiteAttr.getSkuHighPrice());
			/** Added Site Specific Enable Date for R2.1 #53A */
			}  if ( (siteProductVOMap.get(siteId).getEnableDate()) == null) {
				siteProductVOMap.get(siteId).setEnableDate(null);
			}
		}
		return siteProductVOMap;
	}

	/**
	 * The method gets all data from repository for full feed
	 * 
	 * @param viewName
	 * @return
	 * @throws BBBSystemException
	 */
	public RepositoryItem[] getRepoForFullFeed(final String viewName) throws BBBSystemException {
		
			logDebug("CertonaCatalogFeedTools [getRepoForFullFeed] view Name: " + viewName);
		
		RepositoryItem[] catalogItems = null;

		try {
			final RepositoryView catalogView = this.getCatalogRepository().getView(viewName);
			final QueryBuilder queryBuilder = catalogView.getQueryBuilder();
			final Query getAllItemsQuery = queryBuilder.createUnconstrainedQuery();
			catalogItems = catalogView.executeQuery(getAllItemsQuery);
		} catch (RepositoryException e) {
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
		}
		return catalogItems;
	}

	
	
	/**
	 * The method gets count of repository items for full feed
	 * 
	 * @param viewName
	 * @return
	 * @throws BBBSystemException
	 */
	public int getCountOfItemsForFullFeed(final String viewName,String rqlQuery) throws BBBSystemException {

        RqlStatement statement = null;
        int queryResult = 0;
        Repository repository=getCatalogRepository();
        Object[] params=new Object[0];
        if (rqlQuery != null) {
            if (repository != null) {
                try {
                    statement = RqlStatement.parseRqlStatement(rqlQuery);
                    final RepositoryView view = repository.getView(viewName);
                    if (view == null) {
                        this.logInfo("catalog_1019 : View " + viewName + " is null");
                    }

                    queryResult = statement.executeCountQuery(view, params);
                    if (queryResult == 0) {

                        this.logDebug("No results returned for query [" + rqlQuery + "]");

                    }

                } catch (final RepositoryException e) {
                   
                        this.logError("catalog_1020 : Unable to retrieve data");
                    

                    throw new BBBSystemException(
                                    BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                    BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
                }
            } else {
               
                    this.logInfo("catalog_1021 : Repository has no data");
                
            }
        } else {
            
                this.logInfo("catalog_1022 : Query String is null");
            
        }

        return queryResult;
    	}
	
	
	/**
	 * The method gets data from repository for incremental feed based on last
	 * modified date provided
	 * 
	 * @param lastModifiedDate
	 * @param viewName
	 * @return
	 * @throws BBBSystemException
	 */
	public RepositoryItem[] getRepoForIncrementalFeed(final Timestamp lastModifiedDate, final String viewName)
			throws BBBSystemException {
		
			logDebug("CertonaCatalogFeedTools [getRepoForIncrementalFeed] view Name:" + viewName);
		
		RepositoryItem[] catalogItems = null;

		try {

			final RepositoryView catalogView = this.getCatalogRepository().getView(viewName);
			final QueryBuilder queryBuilder = catalogView.getQueryBuilder();
			final QueryExpression pProperty = queryBuilder
					.createPropertyQueryExpression(BBBCertonaConstants.LAST_MODIFIED_DATE_CERTONA_PROPERTY_NAME);
			final QueryExpression pValue = queryBuilder.createConstantQueryExpression(lastModifiedDate);
			final Query query = queryBuilder.createComparisonQuery(pProperty, pValue, QueryBuilder.GREATER_THAN_OR_EQUALS);
			logDebug("Query to retrieve data : " + query);
			catalogItems = catalogView.executeQuery(query);
		} catch (RepositoryException e) {
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
		}
		return catalogItems;
	}



	/**
	 * The method gets all data from repository for full feed
	 * excluding data for store sku
	 * @param viewName
	 * @return
	 * @throws BBBSystemException
	 */
	public RepositoryItem[] getSkuRepoForFullFeed(final String viewName) throws BBBSystemException {
		logDebug("CertonaCatalogFeedTools [getSkuRepoForFullFeed] view Name:" + viewName);
		RepositoryItem[] catalogItems = null;

		try {
			final RepositoryView catalogView = this.getCatalogRepository().getView(viewName);
			final QueryBuilder queryBuilder = catalogView.getQueryBuilder();
			final Query allSkuExceptStoreSKu  = this.getStoreSkuFalseQuery(queryBuilder);
			catalogItems = catalogView.executeQuery(allSkuExceptStoreSKu);

		} catch (RepositoryException e) {
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
		}
		return catalogItems;
	}

	/**
	 * The method gets data from repository for incremental feed based on last
	 * modified date provided
	 * excluding data for store sku
	 * @param lastModifiedDate
	 * @param viewName
	 * @return
	 * @throws BBBSystemException
	 */
	public RepositoryItem[] getSkuRepoForIncrementalFeed(final Timestamp lastModifiedDate, final String viewName)
			throws BBBSystemException {
		logDebug("CertonaCatalogFeedTools [getSkuRepoForIncrementalFeed] view Name:" + viewName);
		RepositoryItem[] catalogItems = null;

		try {

			final RepositoryView catalogView = this.getCatalogRepository().getView(viewName);
			final QueryBuilder queryBuilder = catalogView.getQueryBuilder();

			final Query allSkuForIncrementalFeed = this.getIncrementalSkuQuery(queryBuilder, lastModifiedDate);

			logDebug("Query to retrieve data : " + allSkuForIncrementalFeed);
			catalogItems = catalogView.executeQuery(allSkuForIncrementalFeed);
		} catch (RepositoryException e) {
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
		}
		return catalogItems;
	}

	/**
	 * The method creates query to get sku from repository for incremental feed
	 * This excludes  store sku
	 * @param queryBuilder
	 * @param lastModifiedDate
	 * @return
	 * @throws BBBSystemException
	 */
	public Query getIncrementalSkuQuery(QueryBuilder queryBuilder, Timestamp lastModifiedDate) throws BBBSystemException{
		Query allSkuForIncrementalFeed=null;
		//query to get all sku that have is_store_sku flag as false or null and whose last modified date is graeter than or equal to lastModifiedDate
		final Query [] allModifiedSkuExcludingStoreSku = new Query[2];
		/*
		 * get all sku that have is_store_sku flag as false or null
		 */
		try{
			allModifiedSkuExcludingStoreSku[0] = this.getStoreSkuFalseQuery(queryBuilder);

			final QueryExpression pProperty = queryBuilder
					.createPropertyQueryExpression(BBBCertonaConstants.LAST_MODIFIED_DATE_CERTONA_PROPERTY_NAME);
			final QueryExpression pValue = queryBuilder.createConstantQueryExpression(lastModifiedDate);
			//query lastModifiedDate >= lastModifiedDate
			allModifiedSkuExcludingStoreSku[1] = queryBuilder.createComparisonQuery(pProperty, pValue, QueryBuilder.GREATER_THAN_OR_EQUALS);
			//FINAL QUERY is_store_sku='0' or is_store_sku is null AND lastModifiedDate >= lastModifiedDate
			allSkuForIncrementalFeed = queryBuilder.createAndQuery(allModifiedSkuExcludingStoreSku);
			logDebug("Query to get all sku where store flag is true and last modified date is after last certona feed "+allSkuForIncrementalFeed);
		} catch (RepositoryException e) {
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
		}
		return allSkuForIncrementalFeed;
	}
	/**
	 * The method builds query through query builder to get 
	 * sku that are not store sku
	 * @param queryBuilder
	 * @return
	 * @throws BBBSystemException
	 */
	public Query getStoreSkuFalseQuery(QueryBuilder queryBuilder) throws BBBSystemException{

		Query allSkuExceptStoreSKu=null;
		try {
			//query to get all sku that have is_store_sku flag as false or null
			final QueryExpression storeSkuFalse = queryBuilder.createPropertyQueryExpression(BBBCatalogConstants.IS_STORE_SKU_SKU_PROPERTY_NAME);
			final QueryExpression catalog = queryBuilder.createConstantQueryExpression("0");
			allSkuExceptStoreSKu = queryBuilder.createComparisonQuery(storeSkuFalse, catalog, QueryBuilder.EQUALS);

			logDebug("Query to get all sku where store flag is true "+allSkuExceptStoreSKu);

		} catch (RepositoryException e) {
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
		}
		return allSkuExceptStoreSKu;
	}


	/**
	 * 
	 * @param skuRepoItem
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * @throws RepositoryException
	 */

	public CertonaSKUVO populateSKUVO(final RepositoryItem skuRepoItem)
			throws BBBBusinessException, BBBSystemException,
			RepositoryException {
		final CertonaSKUVO skuVO = new CertonaSKUVO();
		if (skuRepoItem != null) {

			skuVO.setSkuId(skuRepoItem.getRepositoryId());
			if (null != skuRepoItem.getPropertyValue("creationDate")) {
				skuVO.setCreationDate((Timestamp) skuRepoItem.getPropertyValue("creationDate"));
			}
			if (null != skuRepoItem.getPropertyValue(BBBCatalogConstants.ENABLE_DATE_SKU_PROPERTY_NAME)) {
				skuVO.setSkuEnableDate((Date) skuRepoItem.getPropertyValue(BBBCatalogConstants.ENABLE_DATE_SKU_PROPERTY_NAME));
			}
			if (null != skuRepoItem.getPropertyValue("skuType")) {
				skuVO.setSkuType((String) skuRepoItem.getPropertyValue("skuType"));
			}
			if (null != skuRepoItem.getPropertyValue("giftCert")) {
				skuVO.setGiftCertsku((Boolean) skuRepoItem.getPropertyValue("giftCert"));
			}
			if (null != skuRepoItem.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME)) {
				skuVO
				.setJdaDept(((RepositoryItem) skuRepoItem.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME))
						.getRepositoryId());
			}
			if (null != skuRepoItem.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME)) {
				skuVO.setJdaSubDept(((RepositoryItem) skuRepoItem
						.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME)).getRepositoryId());
			}
			if (null != skuRepoItem.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME)) {
				skuVO.setJdaClass((String) skuRepoItem.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME));
			}

			if (null != skuRepoItem.getPropertyValue(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME)) {
				skuVO.setColor((String) skuRepoItem.getPropertyValue(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME));
			}
			if (null != skuRepoItem.getPropertyValue("colorGroup")) {
				skuVO.setColorGroup((String) skuRepoItem.getPropertyValue("colorGroup"));
			}
			if (null != skuRepoItem.getPropertyValue("size")) {
				skuVO.setSize((String) skuRepoItem.getPropertyValue("size"));
			}
			if (null != skuRepoItem.getPropertyValue(BBBCatalogConstants.VDCSKU_TYPE_SKU_PROPERTY_NAME)) {
				skuVO
				.setVdcSkuMessage((String) skuRepoItem.getPropertyValue(BBBCatalogConstants.VDCSKU_TYPE_SKU_PROPERTY_NAME));
			}
			if (null != skuRepoItem.getPropertyValue("vdcSkuType")) {
				skuVO.setVdcSkuType((String) skuRepoItem.getPropertyValue("vdcSkuType"));
			}
			if (null != skuRepoItem.getPropertyValue("upc")) {
				skuVO.setUpc((String) skuRepoItem.getPropertyValue("upc"));
			}
			if (null != skuRepoItem.getPropertyValue("eComFulfillment")) {
				skuVO.seteComFulfillment((String) skuRepoItem.getPropertyValue("eComFulfillment"));
			}
			if (null != skuRepoItem.getPropertyValue("vendorId")) {
				skuVO.setVendorId((String) skuRepoItem.getPropertyValue("vendorId"));
			}
			if (null != skuRepoItem.getPropertyValue("onSale")) {
				skuVO.setOnsale((Boolean) skuRepoItem.getPropertyValue("onSale"));
			}
			if (null != skuRepoItem.getPropertyValue("college")) {
				skuVO.setCollegeId((String) skuRepoItem.getPropertyValue("college"));
			}
			if (null != skuRepoItem.getPropertyValue("emailOutOfStock")) {
				skuVO.setEmailOutOfStock((Boolean) skuRepoItem.getPropertyValue("emailOutOfStock"));
			}
			if (null != skuRepoItem.getPropertyValue("giftWrapEligible")) {
				skuVO.setGiftWrapEligible((Boolean) skuRepoItem.getPropertyValue("giftWrapEligible"));
			}
			if (null != skuRepoItem.getPropertyValue("bopusExclusion")) {
				skuVO.setBopusExclusion((Boolean) skuRepoItem.getPropertyValue("bopusExclusion"));
			}
			if (null != skuRepoItem.getPropertyValue("taxStatus")) {
				skuVO.setSkuTaxStatus((String) skuRepoItem.getPropertyValue("taxStatus"));
			}
			final Prop65TypeVO prop65TypeVO = getProp65Flags(skuRepoItem);
			if (null != prop65TypeVO) {
				logDebug("Prop65TypeVO flags is not Null");
				skuVO.setProp65Flags(prop65TypeVO);
			}
			//Set LTL Flag
			if (null != skuRepoItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU)) {
				skuVO.setLtlFlag((Boolean) skuRepoItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU));
			}

			final SiteSpecificSKUAttr siteAttrVODefaultSite=this.siteAttrVOForDefaultSite(skuRepoItem);
			Map<String, SiteSpecificSKUAttr> siteSKUAttrMap = getSkuAttribute(skuRepoItem,siteAttrVODefaultSite);
			final List<String> defaultSiteList = this.catalogTools.getContentCatalogConfigration(this.getDefaultSiteId());
			if (siteAttrVODefaultSite != null && defaultSiteList != null && !defaultSiteList.isEmpty()) {
				final String defaultSite = defaultSiteList.get(0);
				siteSKUAttrMap.put(defaultSite, siteAttrVODefaultSite);
			}


			if (null != siteSKUAttrMap) {
				for(String siteId:this.getSiteIds()){
					if(!siteSKUAttrMap.containsKey(siteId)){
						siteSKUAttrMap.put(siteId,  new SiteSpecificSKUAttr());
					}
					siteSKUAttrMap=this.addComplexAttrToSiteAttrMap(siteSKUAttrMap, skuRepoItem, siteId);
				}
				skuVO.setSiteSKUAttrMap(siteSKUAttrMap);
			}
		}
		return skuVO;
	}
	/**
	 * The method sets Prop65 flag values for sku
	 * @param skuRepoItem
	 * @return
	 */
	private Prop65TypeVO getProp65Flags(final RepositoryItem skuRepoItem) {
		final Prop65TypeVO prop65TypeVO = new Prop65TypeVO();
		logDebug("Setting the Prop65Flags");
		if (null != skuRepoItem.getPropertyValue("prop65Crystal")) {
			prop65TypeVO.setProp65Crystal((Boolean) skuRepoItem.getPropertyValue("prop65Crystal"));
		}
		if (null != skuRepoItem.getPropertyValue("prop65Dinnerware")) {
			prop65TypeVO.setProp65Dinnerware((Boolean) skuRepoItem.getPropertyValue("prop65Dinnerware"));
		}
		if (null != skuRepoItem.getPropertyValue("prop65Lighting")) {
			prop65TypeVO.setProp65Lighting((Boolean) skuRepoItem.getPropertyValue("prop65Lighting"));
		}
		if (null != skuRepoItem.getPropertyValue("prop65Other")) {
			prop65TypeVO.setProp65Other((Boolean) skuRepoItem.getPropertyValue("prop65Other"));
		}

		return prop65TypeVO;

	}

	/**
	 * the method returns the map of site and corresponding site specific sku
	 * properties VO
	 * 
	 * @param skuRepoItem
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws PriceListException
	 * @throws RepositoryException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, SiteSpecificSKUAttr> getSkuAttribute(final RepositoryItem skuRepoItem,SiteSpecificSKUAttr siteAttrVODefaultSite)
			throws BBBBusinessException, BBBSystemException, RepositoryException {
		final String skuId=skuRepoItem.getRepositoryId();
		logDebug("getSkuAttribute for sku id "+skuId);
		boolean dateIsActive = true;
		final Date currentDate = new Date();
		if (skuRepoItem.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME) != null
				&& skuRepoItem.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME) != null) {
			final Date startDate = (Date) skuRepoItem.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME);
			final Date endDate = (Date) skuRepoItem.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME);
			if (((endDate != null && currentDate.after(endDate)) || (startDate != null && currentDate.before(startDate)))) {
				dateIsActive = false;
				logDebug("dateIsActive " + dateIsActive);
			}
		}
		Map<String, SiteSpecificSKUAttr> siteSKUVOMap = new HashMap<String, SiteSpecificSKUAttr>();
		final Set<RepositoryItem> skuTranslations = (Set<RepositoryItem>) skuRepoItem.getPropertyValue("skuTranslations");
		for (RepositoryItem transRepo : skuTranslations) {
			final RepositoryItem siteItem = (RepositoryItem) transRepo.getPropertyValue("site");
			if (siteItem != null) {
				final String siteId = siteItem.getRepositoryId();
				if (siteSKUVOMap.containsKey(siteId)) {
					siteSKUVOMap=this.addSimpleSiteAttrForSKu(siteSKUVOMap, transRepo,  siteId);
				} else {
					siteSKUVOMap=this.addSkuSiteAttrVOToMap(siteSKUVOMap, transRepo, dateIsActive, siteId);

				}

			}
		}
		for(String siteId:this.getNonDefaultSiteIds()){
			logDebug("Setting non available translation values from default site value");
			siteSKUVOMap=setSkuValueFromDefaultSiteForNoTrans( siteId, siteSKUVOMap, siteAttrVODefaultSite);
		}
		/*
		 * once we have values of weboffered (site specific value) disable (site specific)
		 * and active date based on start and end date (not site specific)
		 * set the isactive flag also
		 */
		final Set<String> keySet=siteSKUVOMap.keySet();
		for(String siteId:keySet){
			final boolean disable=siteSKUVOMap.get(siteId).isDisabled();
			final boolean webOffered=siteSKUVOMap.get(siteId).isWebOffered();
			if( !dateIsActive ||disable || !webOffered){
				siteSKUVOMap.get(siteId).setActiveSku(false) ;
			}
			else{
				siteSKUVOMap.get(siteId).setActiveSku(true) ;
			}
		}
		return siteSKUVOMap;
	}

	/**
	 * 
	 * Once all simple attributes are set for a sku then complex attributes like eligible methods free ship methods are set 
	 * @param siteSKUVOMap
	 * @param skuRepoItem
	 * @param siteId
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private  Map<String, SiteSpecificSKUAttr>  addComplexAttrToSiteAttrMap(Map<String, SiteSpecificSKUAttr> siteSKUVOMap,RepositoryItem skuRepoItem, String siteId ) throws BBBSystemException, BBBBusinessException{

		try {
			Double listPrice = new Double("0.00");
			Double salePrice = new Double("0.00");
			final String skuId=skuRepoItem.getRepositoryId();

			final RepositoryItem siteItem=this.getSiteRepository().getItem(siteId,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
			final RepositoryItem listPriceItem = (RepositoryItem) siteItem.getPropertyValue("defaultListPriceList");
			if (null != listPriceItem) {
				listPrice = this.getPriceForSku(listPriceItem, skuId);
				
					logDebug("List Price is " + listPrice);
				
				siteSKUVOMap.get(siteId).setListPrice(listPrice);
			}
			else  {
				logDebug("List Price is  NUll" + listPrice);
			}

			final RepositoryItem salePriceItem = (RepositoryItem) siteItem.getPropertyValue("defaultSalePriceList");
			if (null != salePriceItem) {
				salePrice = this.getPriceForSku(salePriceItem, skuId);
				
					logDebug("Sale Price is " + salePrice);
				
				siteSKUVOMap.get(siteId).setSalePrice(salePrice);
			}
			else {
				logDebug("Sale Price is  NUll" + salePrice);
			}

			Map<String,RepositoryItem> skuAttrIdRepoItemMap=new HashMap<String,RepositoryItem>();
			skuAttrIdRepoItemMap=this.getCatalogTools().getSkuAttributeList(skuRepoItem, siteId, skuAttrIdRepoItemMap, BBBCoreConstants.BLANK, false);

			if (null != skuAttrIdRepoItemMap && !skuAttrIdRepoItemMap.isEmpty()) {
				final List<SkuAttributesType> skuAttributes = this.getSkuAttrList(skuAttrIdRepoItemMap, siteId);
				
					logDebug("Setting the SKU Attributes");
				
				siteSKUVOMap.get(siteId).setSkuAttributes(skuAttributes);
				final List<ShippingDetails> freeShipMethods = this.updateFreeShipMethodForSku(skuAttrIdRepoItemMap.keySet(), siteId);
				if (null != freeShipMethods) {
					logDebug("Setting the SKU Free Ship Methods");
					siteSKUVOMap.get(siteId).setFreeShipMethods(freeShipMethods);
				}
				else {
					logDebug("Free Ship Methods NULL for sku Id "+skuId +" site id "+siteId);
				}
			}

			final List<RebatesDetails> skuRebates = this.setSkuRebates(skuRepoItem, siteId);
			if (null != skuRebates) {
				logDebug("Setting the SKU Rebates");
				siteSKUVOMap.get(siteId).setSkuRebates(skuRebates);
			}

			final List<StatesDetails> nonShippableStates = this.setNonShippableStates(skuRepoItem);
			if (null != nonShippableStates) {
				logDebug("Setting the SKU Non Shippable States");
				siteSKUVOMap.get(siteId).setNonShippableStates(nonShippableStates);
			}
			List<ShippingDetails> eligibleShipMethods=null;
			eligibleShipMethods = this.setEligibleShipMethods(skuRepoItem, siteId);
			if (null != eligibleShipMethods) {
				logDebug("Setting the SKU eligible Ship Methods");
				siteSKUVOMap.get(siteId).setEligibleShipMethods(eligibleShipMethods);
			}
		} catch (RepositoryException e) {
			
				logError("Error while retrieving shipping method");
			
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,e);
		}

		return siteSKUVOMap;
	}
	/**
	 * 
	 * @param siteSKUVOMap
	 * @param translationRepo
	 * @param dateIsActive
	 * @param siteId
	 * @return
	 */
	private  Map<String, SiteSpecificSKUAttr>  addSimpleSiteAttrForSKu(Map<String, SiteSpecificSKUAttr> siteSKUVOMap,RepositoryItem translationRepo,String siteId ){

		if(translationRepo!=null && translationRepo.getPropertyValue(BBBCertonaConstants.TRANSLATION_PROPERTY_ATTRIBUTE_NAME)!=null){
			final String attributeName = (String) translationRepo.getPropertyValue(BBBCertonaConstants.TRANSLATION_PROPERTY_ATTRIBUTE_NAME);
			logDebug("Updating the Site Specific Attributes for site " + siteId +" attributeName  " +attributeName);
			if(siteSKUVOMap!=null && !siteSKUVOMap.isEmpty() && siteSKUVOMap.get(siteId)!=null){
				if (attributeName.equalsIgnoreCase("displayName")) {
					siteSKUVOMap.get(siteId).setDisplayName((String) translationRepo.getPropertyValue(BBBCertonaConstants.ATTRIBUTE_VALUE_STRING));
				} else if (attributeName.equalsIgnoreCase("description")) {
					siteSKUVOMap.get(siteId).setDescription((String) translationRepo.getPropertyValue(BBBCertonaConstants.ATTRIBUTE_VALUE_STRING));
				} else if (attributeName.equalsIgnoreCase("longDescription")) {
					siteSKUVOMap.get(siteId).setLongDescription((String) translationRepo.getPropertyValue(BBBCertonaConstants.ATTRIBUTE_VALUE_STRING));
				} else if (attributeName.equalsIgnoreCase("shippingSurcharge")) {
					siteSKUVOMap.get(siteId).setShippingSurcharge((Double) translationRepo.getPropertyValue(BBBCertonaConstants.ATTRIBUTE_VALUE_NUMBER));
				} else if (attributeName.equalsIgnoreCase("webOffered")
						&& translationRepo.getPropertyValue(BBBCertonaConstants.ATTRIBUTE_VALUE_BOOLEAN) != null) {
					final Boolean webOffered = (Boolean) translationRepo.getPropertyValue(BBBCertonaConstants.ATTRIBUTE_VALUE_BOOLEAN);
					siteSKUVOMap.get(siteId).setWebOffered(webOffered);
				} else if (attributeName.equalsIgnoreCase("skuDisable")
						&& translationRepo.getPropertyValue(BBBCertonaConstants.ATTRIBUTE_VALUE_BOOLEAN) != null) {
					final Boolean prodDisable = (Boolean) translationRepo.getPropertyValue(BBBCertonaConstants.ATTRIBUTE_VALUE_BOOLEAN);
					siteSKUVOMap.get(siteId).setDisabled(prodDisable);
				} 				
			}
		}
		return siteSKUVOMap;
	}
	/**
	 * 
	 * @param siteSKUVOMap
	 * @param translationRepo
	 * @param dateIsActive
	 * @param siteId
	 * @return
	 */
	private Map<String, SiteSpecificSKUAttr>  addSkuSiteAttrVOToMap(Map<String, SiteSpecificSKUAttr> siteSKUVOMap,RepositoryItem translationRepo,boolean dateIsActive, String siteId ){
		logDebug("Setting the Site Specific Attributes for site " + siteId);
		final SiteSpecificSKUAttr siteSKUAttr = new SiteSpecificSKUAttr();
		final String attributeName = (String) translationRepo.getPropertyValue(BBBCertonaConstants.TRANSLATION_PROPERTY_ATTRIBUTE_NAME);
		if (attributeName.equalsIgnoreCase("displayName")) {
			siteSKUAttr.setDisplayName((String) translationRepo.getPropertyValue(BBBCertonaConstants.ATTRIBUTE_VALUE_STRING));
		} else if (attributeName.equalsIgnoreCase("description")) {
			siteSKUAttr.setDescription((String) translationRepo.getPropertyValue(BBBCertonaConstants.ATTRIBUTE_VALUE_STRING));
		} else if (attributeName.equalsIgnoreCase("longDescription")) {
			siteSKUAttr.setLongDescription((String) translationRepo.getPropertyValue(BBBCertonaConstants.ATTRIBUTE_VALUE_STRING));
		} else if (attributeName.equalsIgnoreCase("shippingSurcharge")) {
			siteSKUAttr.setShippingSurcharge((Double) translationRepo.getPropertyValue(BBBCertonaConstants.ATTRIBUTE_VALUE_NUMBER));
		} else if (attributeName.equalsIgnoreCase("webOffered")
				&& translationRepo.getPropertyValue(BBBCertonaConstants.ATTRIBUTE_VALUE_BOOLEAN) != null) {
			final Boolean webOffered = (Boolean) translationRepo.getPropertyValue(BBBCertonaConstants.ATTRIBUTE_VALUE_BOOLEAN);
			siteSKUAttr.setWebOffered(webOffered);
		} else if (attributeName.equalsIgnoreCase("skuDisable")
				&& translationRepo.getPropertyValue(BBBCertonaConstants.ATTRIBUTE_VALUE_BOOLEAN) != null) {
			final Boolean prodDisable = (Boolean) translationRepo.getPropertyValue(BBBCertonaConstants.ATTRIBUTE_VALUE_BOOLEAN);
			siteSKUAttr.setDisabled(prodDisable);
		}
		siteSKUVOMap.put(siteId, siteSKUAttr);
		return siteSKUVOMap;
	}

	/**
	 * 
	 * @param siteSKUVOMap
	 * @param translationRepo
	 * @param dateIsActive
	 * @param siteId
	 * @return
	 */
	private Map<String, SiteSpecificSKUAttr>  setSkuValueFromDefaultSiteForNoTrans(String siteId,Map<String, SiteSpecificSKUAttr> siteSKUVOMap,
			final SiteSpecificSKUAttr defaultSiteAttr ){
		if(defaultSiteAttr!=null   ){
			if(siteSKUVOMap!=null && !siteSKUVOMap.isEmpty()){
				if(siteSKUVOMap.get(siteId)!=null){
					if ( StringUtils.isEmpty(siteSKUVOMap.get(siteId).getDisplayName())) {
						siteSKUVOMap.get(siteId).setDisplayName(defaultSiteAttr.getDisplayName());
					}  if ( StringUtils.isEmpty(siteSKUVOMap.get(siteId).getDescription())) {
						siteSKUVOMap.get(siteId).setDescription(defaultSiteAttr.getDescription());
					}  if ( StringUtils.isEmpty(siteSKUVOMap.get(siteId).getLongDescription())) {
						siteSKUVOMap.get(siteId).setLongDescription(defaultSiteAttr.getLongDescription());
					}  
				}
				else{
					SiteSpecificSKUAttr newSiteAttrbVO=new SiteSpecificSKUAttr();
					newSiteAttrbVO.setDisplayName(defaultSiteAttr.getDisplayName());
					newSiteAttrbVO.setDescription(defaultSiteAttr.getDescription());
					newSiteAttrbVO.setLongDescription(defaultSiteAttr.getLongDescription());
					newSiteAttrbVO.setShippingSurcharge(defaultSiteAttr.getShippingSurcharge());
					siteSKUVOMap.put(siteId, newSiteAttrbVO);
				}
			}
			else{
				siteSKUVOMap=new HashMap<String, SiteSpecificSKUAttr>();
				SiteSpecificSKUAttr newSiteAttrbVO=new SiteSpecificSKUAttr();
				newSiteAttrbVO.setDisplayName(defaultSiteAttr.getDisplayName());
				newSiteAttrbVO.setDescription(defaultSiteAttr.getDescription());
				newSiteAttrbVO.setLongDescription(defaultSiteAttr.getLongDescription());
				newSiteAttrbVO.setShippingSurcharge(defaultSiteAttr.getShippingSurcharge());
				siteSKUVOMap.put(siteId, newSiteAttrbVO);
			}
		}
		return siteSKUVOMap;
	}
	/**
	 * 
	 * @param skuItem
	 * @return
	 */

	private  SiteSpecificSKUAttr  siteAttrVOForDefaultSite(RepositoryItem skuItem ){

		final SiteSpecificSKUAttr siteSKUAttr = new SiteSpecificSKUAttr();


		siteSKUAttr.setDisplayName((String) skuItem.getPropertyValue("displayName"));

		siteSKUAttr.setDescription((String) skuItem.getPropertyValue("description"));

		siteSKUAttr.setLongDescription((String) skuItem.getPropertyValue("description"));
		if(skuItem.getPropertyValue("shippingSurcharge")!=null){
			siteSKUAttr.setShippingSurcharge((Double) skuItem.getPropertyValue("shippingSurcharge"));
		}
		siteSKUAttr.setActiveSku(this.getCatalogTools().isSkuActive(skuItem));

		return siteSKUAttr;
	}

	/**
	 * The method gets the free ship methods that are applicable for a sku
	 * From the list of all applicable attributes the method checks 
	 * if there are any attribute id for free ship attrbute (present in freeShipAttrShipGrpMap)
	 * if yes then that shipping method is added in the VO
	 * @param applicableAttrIds
	 * @return list of shipping details in the ShipMethodVO
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	private List<ShippingDetails> updateFreeShipMethodForSku(Set<String> applicableAttrIds, String siteId) throws BBBBusinessException, BBBSystemException
	{
		
			logDebug("updateFreeShipMethodForSku  "+applicableAttrIds );
		
		final List<ShippingDetails> freeShippingDetails = new ArrayList<ShippingDetails>();
		
			logDebug("Catalog API Method Name [updateFreeShipMethodForSku] ");
		

		if(freeShipAttrShipGrpMap!=null && !freeShipAttrShipGrpMap.isEmpty()){
			for(String freeShipAttrid:freeShipAttrShipGrpMap.keySet()){
				
					logDebug("Attribute Id from the list of Applicable  attributes for the sku "+freeShipAttrid);
				
				if(applicableAttrIds.contains(freeShipAttrid)){
					final String freeShipMethod=this.freeShipAttrShipGrpMap.get(freeShipAttrid).toLowerCase();
					
						logDebug("Shipping method id ["+freeShipMethod+" is  free ");
					
					final RepositoryItem shipItem=this.getShippingMethod(freeShipMethod);
					freeShippingDetails.add(new ShippingDetails(shipItem, getCatalogTools(), siteId));
				}
			}
		}

		return freeShippingDetails;
	}
	/**
	 * @param shippingMethod
	 * @return
	 * @throws RepositoryException
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public RepositoryItem getShippingMethod(String shippingMethod) throws BBBBusinessException, BBBSystemException {
		RepositoryItem shipItem = null;
		try {
			shipItem = this.getShippingRepository().getItem(shippingMethod, BBBCatalogConstants.SHIPPING_METHOD_ITEM_DESCRIPTOR);

			if(shipItem == null) {
				throw new BBBBusinessException(BBBCatalogErrorCodes.SHIPPING_METHOD_NOT_AVAILABLE_IN_REPOSITORY,BBBCatalogErrorCodes.SHIPPING_METHOD_NOT_AVAILABLE_IN_REPOSITORY);
			}
		} catch (RepositoryException e) {
			
				logError("Error while retrieving shipping method",e);
			

			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,e);
		}
		return shipItem;
	}

	/**
	 * 
	 * @param skuRepoItem
	 * @param siteId
	 * @return
	 * @throws RepositoryException
	 */
	@SuppressWarnings({ "unchecked" })
	private List<ShippingDetails> setEligibleShipMethods(final RepositoryItem skuRepoItem, final String siteId)
			throws RepositoryException {

		final List<ShippingDetails> shipMethodVOList = new ArrayList<ShippingDetails>();

		final List<String> shipIdList = new ArrayList<String>();
		final List<ShippingDetails> shipMethodVOListForSite = new ArrayList<ShippingDetails>();

		final RepositoryItem siteConfiguration = this.getSiteRepository().getItem(siteId,
				BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
		if (siteConfiguration != null) {
			final Set<RepositoryItem> applicableShipMethodSet = (Set<RepositoryItem>) siteConfiguration
					.getPropertyValue(BBBCatalogConstants.APPLICABLE_SHIPMETHODS_SITE_PROPERTY_NAME);
			
				logDebug("Set of applicable Shipping methods [" + applicableShipMethodSet + "] ");
			
			if (applicableShipMethodSet != null && !applicableShipMethodSet.isEmpty()) {
				for (RepositoryItem applicableShipMethod : applicableShipMethodSet) {
					shipIdList.add(applicableShipMethod.getRepositoryId());
					shipMethodVOListForSite.add(new ShippingDetails(applicableShipMethod, getCatalogTools() , siteId ));
				}
			}
		}
		final Set<RepositoryItem> eligibleShipMethodSet = (Set<RepositoryItem>) skuRepoItem
				.getPropertyValue(BBBCatalogConstants.ELIGIBLE_SHIP_METHODS_PROPERTY_NAME);

		if (eligibleShipMethodSet != null && !eligibleShipMethodSet.isEmpty()) {

			for (RepositoryItem eligibleShipMethods : eligibleShipMethodSet) {
				if (shipIdList.contains(eligibleShipMethods.getRepositoryId())) {
					shipMethodVOList.add(new ShippingDetails(eligibleShipMethods, getCatalogTools(), siteId));
				}
			}
			
				logDebug("Returnign the Ship Methods List");
			
			return shipMethodVOList;
		} else {
			return shipMethodVOListForSite;
		}
	}

	/**
	 * 
	 * @param skuRepoItem
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<StatesDetails> setNonShippableStates(final RepositoryItem skuRepoItem) {
		final List<StatesDetails> nonShippableStates = new ArrayList<StatesDetails>();
		final Set<RepositoryItem> nonShippableStatesRepoItemSet = (Set<RepositoryItem>) skuRepoItem
				.getPropertyValue(BBBCatalogConstants.NON_SHIPPABLE_STATES_SHIPPING_PROPERTY_NAME);
		if (!(nonShippableStatesRepoItemSet == null || nonShippableStatesRepoItemSet.isEmpty())) {
			for (RepositoryItem nonShippableStatesRepositoryItem : nonShippableStatesRepoItemSet) {
				nonShippableStates.add(new StatesDetails(nonShippableStatesRepositoryItem));
			}
		}
		return nonShippableStates;
	}
	/**
	 * 
	 * @param skuRepoItem
	 * @param siteId
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	private List<RebatesDetails> setSkuRebates(final RepositoryItem skuRepoItem, final String siteId) {
		// Set Rebates
		final List<RebatesDetails> eligibleRebates = new ArrayList<RebatesDetails>();

		final Set<RepositoryItem> rebatesRepositoryItem = (Set<RepositoryItem>) skuRepoItem
				.getPropertyValue(BBBCatalogConstants.REBATES_ITEM_DESCRIPTOR);
		if (rebatesRepositoryItem != null && !rebatesRepositoryItem.isEmpty()) {
			for (RepositoryItem rebateRepositoryItem : rebatesRepositoryItem) {
				final Set<RepositoryItem> sitesSet = (Set<RepositoryItem>) rebateRepositoryItem
						.getPropertyValue(BBBCatalogConstants.SITES_REBATE_PROPERTY_NAME);
				for (RepositoryItem siteRepoItem : sitesSet) {

					if (siteRepoItem.getRepositoryId().equalsIgnoreCase(siteId)) {
						
							logDebug("Found Sku Rebates for SiteId " + siteId);
						
						eligibleRebates.add(new RebatesDetails(rebateRepositoryItem));

					}
				}
			}
		}

		return eligibleRebates;
	}
	/**
	 * 
	 * @param skuRepoItemMap
	 * @param siteId
	 * @return
	 */

	private List<SkuAttributesType> getSkuAttrList(final Map<String,RepositoryItem> skuRepoItemMap, final String siteId) {
		final List<SkuAttributesType> skuAttributes = new ArrayList<SkuAttributesType>();
		final Set<String> attrIdKeySet=skuRepoItemMap.keySet();
		SkuAttributesType skuAttribute=null;
		for (String key : attrIdKeySet) {
			skuAttribute= new SkuAttributesType();
			final RepositoryItem attributeItem=skuRepoItemMap.get(key);
			skuAttribute.setAttributeId(attributeItem.getRepositoryId());
			if (null != attributeItem
					.getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME)) {
				skuAttribute.setAttributeDisplayName((String) attributeItem
						.getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME));
			}
			if (null != attributeItem.getPropertyValue(BBBCatalogConstants.START_DATE_ATTRIBUTE_PROPERTY_NAME)) {
				skuAttribute.setAttributeStartDate((Timestamp) attributeItem
						.getPropertyValue(BBBCatalogConstants.START_DATE_ATTRIBUTE_PROPERTY_NAME));
			}
			if (null != attributeItem.getPropertyValue(BBBCatalogConstants.END_DATE_ATTRIBUTE_PROPERTY_NAME)) {
				skuAttribute.setAttributeEndDate((Timestamp) attributeItem
						.getPropertyValue(BBBCatalogConstants.END_DATE_ATTRIBUTE_PROPERTY_NAME));
			}
			if (null != attributeItem.getPropertyValue(BBBCatalogConstants.PLACE_HOLDER_ATTRIBUTE_PROPERTY_NAME)) {
				skuAttribute.setPlaceHolder((String) attributeItem
						.getPropertyValue(BBBCatalogConstants.PLACE_HOLDER_ATTRIBUTE_PROPERTY_NAME));
			}
			if (null != attributeItem.getPropertyValue(BBBCatalogConstants.PRIORITY_ATTRIBUTE_PROPERTY_NAME)) {
				skuAttribute.setPriority((Integer) attributeItem
						.getPropertyValue(BBBCatalogConstants.PRIORITY_ATTRIBUTE_PROPERTY_NAME));
			}
			if (null != attributeItem.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME)) {
				skuAttribute.setAttrImageURL((String) attributeItem
						.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME));
			}
			if (null != attributeItem.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME)) {
				skuAttribute.setAttrActionURL((String) attributeItem
						.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME));
			}
			skuAttributes.add(skuAttribute);
		}
		return skuAttributes;
	}

	/**
	 * 
	 * @param priceList
	 * @param skuId
	 * @return
	 */

	private double getPriceForSku(final RepositoryItem priceList, final String skuId) {
		Double price = new Double("0.00");
		if (null != priceList) {
			try {
				final String productId = getCatalogTools().getFirstActiveParentProductForSKU(skuId);
				if (null != productId) {
					logDebug("Parent Product for SKU " + skuId + " is " + productId);
					final RepositoryItem priceItem = getPriceListManager().getPrice(priceList, productId, skuId);
					if (priceItem != null) {
						logDebug("Found Price for SkuId " + skuId);
						price = (Double) priceItem.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME);
					}
				}
			} catch (PriceListException ple) {
				
					logError("Received PriceListException",ple);
				
			} catch (RemovedItemException removedItemException) {
				
				logError("Attempt to use an item which has been removed - skuId : " + skuId );
				
			} catch (BBBBusinessException bbe) {
				
					logError("Received Business Exception",bbe);
				
			} catch (BBBSystemException bse) {
				logError("Received BBBSystemException Exception",bse);
			}
		}
		return price;
	}
	/**
	 * the method returns the brand details for a product in BrandVO
	 * 
	 * @param prodRepoItem
	 * @return
	 */
	private BrandVO getProductBrands(final RepositoryItem prodRepoItem) {
		
			logDebug("CertonaCatalogFeedTools [getProductBrands]");
		
		BrandVO brandVO = null;
		final RepositoryItem brandRepoItem = (RepositoryItem) prodRepoItem
				.getPropertyValue(BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR);
		if (brandRepoItem != null) {
			brandVO = new BrandVO();
			brandVO.setBrandId(brandRepoItem.getRepositoryId());
			if (brandRepoItem.getPropertyValue(BBBCatalogConstants.BRAND_NAME_BRAND_PROPERTY_NAME) != null) {
				brandVO.setBrandName((String) brandRepoItem
						.getPropertyValue(BBBCatalogConstants.BRAND_NAME_BRAND_PROPERTY_NAME));
			}
			if (brandRepoItem.getPropertyValue(BBBCatalogConstants.BRAND_IMAGE_BRAND_PROPERTY_NAME) != null) {
				brandVO.setBrandImage((String) brandRepoItem
						.getPropertyValue(BBBCatalogConstants.BRAND_IMAGE_BRAND_PROPERTY_NAME));
			}
			if (brandRepoItem.getPropertyValue(BBBCatalogConstants.BRAND_DESCRIPTION_BRAND_PROPERTY_NAME) != null) {
				brandVO.setBrandDesc((String) brandRepoItem
						.getPropertyValue(BBBCatalogConstants.BRAND_DESCRIPTION_BRAND_PROPERTY_NAME));
			}

			if (brandRepoItem.getPropertyValue(BBBCatalogConstants.DISPLAY_BRAND_PROPERTY_NAME) != null) {
				brandVO.setDisplayFlag(((Boolean) brandRepoItem
						.getPropertyValue(BBBCatalogConstants.DISPLAY_BRAND_PROPERTY_NAME)));
			}

		}
		return brandVO;
	}
	public List<CertonaCategoryVO> getCategoryDetails(final boolean isFullDataFeed, final Timestamp lastModifiedDate, final String pRqlQueryRange)throws BBBSystemException, BBBBusinessException {
				logDebug("CertonaCatalogFeedTools [getCategoryDetails]");
				RepositoryItem[] catItems=null;
				final List<CertonaCategoryVO> catVOList = new ArrayList<CertonaCategoryVO>();
					catItems = getRepoForFullOrPartialFeed(BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR,pRqlQueryRange);
					logDebug("Fetch data for full category feed :isFullDataFeed: " + isFullDataFeed + " lastModifiedDate:"
							+ lastModifiedDate);

				if (catItems != null && catItems.length > 0) {
					for (int i = 0; i < catItems.length; i++) {
						final CertonaCategoryVO categoryVO = this.populateCategoryVO(catItems[i]);
						if (categoryVO != null) {
							catVOList.add(categoryVO);
						}
					}
				} else {
					logDebug("No data available in repository for category feed");
					throw new BBBBusinessException(BBBCatalogErrorCodes.NO_DATA_FOR_CATEGORY_FEED);
				}
				logDebug("Exiting getCategoryDetails of CertonaCatalogFeedTools");
				return catVOList;
	}

/*	private RepositoryItem[] getRepoForFullFeed(String viewName,
			String pRqlQueryRange)throws BBBSystemException, BBBBusinessException{
		
			if (isLoggingDebug()) {
				logDebug("CertonaCatalogFeedTools [getRepoForFullFeed] view Name: " + viewName);
			}
			RepositoryItem[] catalogItems = null;

			try {		
				catalogItems = getCatalogTools().executeRQLQuery(pRqlQueryRange.toString(), viewName);
			} catch (RepositoryException e) {
				throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION, e);
			}
			return catalogItems;
		}*/
	/**
	 * Method to get Array of repository items based repository view and
	 * RqlQuery passed as argument, basically this method used for retrieving
	 * catalog data like SKU, Product,Category for certona feed file generation
	 * 
	 * @param viewName
	 * @param pRqlQueryRange
	 * @return RepositoryItem[]
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private RepositoryItem[] getRepoForFullOrPartialFeed(String viewName,
			String pRqlQueryRange)throws BBBSystemException, BBBBusinessException{
			RepositoryItem[] repositoryItems = null;
			
				logDebug("CertonaCatalogFeedTools [getRepoForFullFeed] view Name: " + viewName);
				logDebug("CertonaCatalogFeedTools [getRepoForFullFeed] RQL Query Range To Fetch Data: " + pRqlQueryRange);
			
			try {		
				repositoryItems = getCatalogTools().executeRQLQuery(pRqlQueryRange.toString(), viewName);
			} catch (RepositoryException e) {
				throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
			}
			return repositoryItems;
		}
	/**
	 * This method fetches properties for Product to be sent in certona feed if
	 * full feed is required or if last modified date is null ,full data for
	 * Product is fetched.For incremental feed data that has been modified since
	 * last feed is only fetched
	 * 
	 * @param isFullDataFeed
	 *          :if full feed is required=true else false
	 * @param lastModifiedDate
	 *          :date since last feed was run
	 * @return List of CertonaProductVO
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public List<RepositoryItem> getProductDetails(final boolean isFullDataFeed, final Timestamp lastModifiedDate,final String pRqlQueryRange)
			throws BBBSystemException, BBBBusinessException {
		logDebug("CertonaCatalogFeedTools [getProductDetails]");
		RepositoryItem[] productItems = null;

			productItems = getRepoForFullOrPartialFeed(BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR,pRqlQueryRange);
			logDebug("Fetch data for full product feed :isFullDataFeed: " + isFullDataFeed + " lastModifiedDate:"
					+ lastModifiedDate);
			
		logDebug("Exiting getProductDetails of CertonaCatalogFeedTools");
		if(productItems!=null){
			return Arrays.asList(productItems);
		}
		else{
			return null;
		}
	}
	/**
	 * The method gets data from repository for sku feed depending on whether full
	 * or incremental feed is required
	 * @param isFullDataFeed
	 * @param lastModifiedDate
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */

	public List<RepositoryItem> getSKUDetailsRepo(final boolean isFullDataFeed, final Timestamp lastModifiedDate, final String pRqlQueryRange)
			throws BBBSystemException, BBBBusinessException {
		RepositoryItem[] skuItems = null;
			skuItems = getRepoForFullOrPartialFeed(BBBCatalogConstants.SKU_ITEM_DESCRIPTOR,pRqlQueryRange);			
		if(skuItems!=null){
			return Arrays.asList(skuItems);
		}
		else{
			return null;
		}
	}
	
}
