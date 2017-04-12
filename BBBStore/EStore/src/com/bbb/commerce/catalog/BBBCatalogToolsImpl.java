package com.bbb.commerce.catalog;

import static com.bbb.constants.BBBCoreConstants.COLLECTION_CHILD_RELN_CACHE_NAME;
import static com.bbb.constants.BBBCoreConstants.DISABLE_COLLECTION_PARENT_CACHE;
import static com.bbb.constants.BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;

import atg.adapter.gsa.GSARepository;
import atg.commerce.claimable.ClaimableTools;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.core.util.StringUtils;
import atg.droplet.BBBCurrencyTagConvertor;
import atg.droplet.TagConversionException;
import atg.droplet.TagConverterManager;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.nucleus.naming.ComponentName;
import atg.repository.MutableRepository;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.QueryOptions;
import atg.repository.RemovedItemException;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryView;
import atg.repository.SortDirective;
import atg.repository.SortDirectives;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.BBBProfileManager;
import com.bbb.account.profile.vo.ProfileEDWInfoVO;
import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.cache.BBBLocalDynamicPriceSKUCache;
import com.bbb.cms.PromoBoxVO;
import com.bbb.cms.tools.CmsTools;
import com.bbb.commerce.browse.manager.ProductManager;
import com.bbb.commerce.browse.vo.RecommendedSkuVO;
import com.bbb.commerce.cart.utils.RepositoryPriorityComparator;
import com.bbb.commerce.catalog.comparison.vo.CompareProductEntryVO;
import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.commerce.catalog.vo.BCCManagedPromoCategoryVO;
import com.bbb.commerce.catalog.vo.BazaarVoiceProductVO;
import com.bbb.commerce.catalog.vo.BrandVO;
import com.bbb.commerce.catalog.vo.CategoryMappingVo;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.commerce.catalog.vo.CollectionProductVO;
import com.bbb.commerce.catalog.vo.CollegeVO;
import com.bbb.commerce.catalog.vo.CountryVO;
import com.bbb.commerce.catalog.vo.CreditCardTypeVO;
import com.bbb.commerce.catalog.vo.EcoFeeSKUVO;
import com.bbb.commerce.catalog.vo.GiftWrapVO;
import com.bbb.commerce.catalog.vo.ImageVO;
import com.bbb.commerce.catalog.vo.LTLAssemblyFeeVO;
import com.bbb.commerce.catalog.vo.LTLDeliveryChargeVO;
import com.bbb.commerce.catalog.vo.MediaVO;
import com.bbb.commerce.catalog.vo.PDPAttributesVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.RebateVO;
import com.bbb.commerce.catalog.vo.RecommendedCategoryVO;
import com.bbb.commerce.catalog.vo.RegionVO;
import com.bbb.commerce.catalog.vo.RegistryCategoryMapVO;
import com.bbb.commerce.catalog.vo.RegistryTypeVO;
import com.bbb.commerce.catalog.vo.RollupTypeVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.catalog.vo.SiteChatAttributesVO;
import com.bbb.commerce.catalog.vo.SiteVO;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.commerce.catalog.vo.StoreSpecialityVO;
import com.bbb.commerce.catalog.vo.StoreVO;
import com.bbb.commerce.catalog.vo.TabVO;
import com.bbb.commerce.catalog.vo.ThresholdVO;
import com.bbb.commerce.catalog.vo.VendorInfoVO;
import com.bbb.commerce.checkout.util.BBBOrderUtilty;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.inventory.OnlineInventoryManager;
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBEximConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.edwData.EDWProfileDataVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBDynamicPriceCacheContainer;
import com.bbb.framework.cache.BBBObjectCache;
import com.bbb.framework.goldengate.DCPrefixIdGenerator;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.repositorywrapper.IRepositoryWrapper;
import com.bbb.repositorywrapper.RepositoryWrapperImpl;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.result.BBBDynamicPriceSkuVO;
import com.bbb.search.bean.result.BBBDynamicPriceVO;
import com.bbb.search.bean.result.BBBProduct;
import com.bbb.search.bean.result.CategoryRefinementVO;
import com.bbb.search.bean.result.FacetRefinementVO;
import com.bbb.search.bean.result.SortOptionVO;
import com.bbb.search.bean.result.SortOptionsVO;
import com.bbb.search.endeca.EndecaSearchUtil;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.bbb.search.endeca.tools.EndecaSearchTools;
import com.bbb.search.endeca.vo.SearchBoostingAlgorithmVO;
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.selfservice.manager.ScheduleAppointmentManager;
import com.bbb.selfservice.manager.SearchStoreManager;
import com.bbb.selfservice.vo.BeddingShipAddrVO;
import com.bbb.selfservice.vo.SchoolVO;
import com.bbb.seo.CategorySeoLinkGenerator;
import com.bbb.tools.BBBEdwRepositoryTools;
import com.bbb.tools.BBBPriceListRepositoryTools;
import com.bbb.tools.BBBSchoolRepositoryTools;
import com.bbb.tools.BBBShippingRepositoryTools;
import com.bbb.tools.BBBSiteRepositoryTools;
import com.bbb.tools.BBBStoreRepositoryTools;
import com.bbb.tools.GlobalRepositoryTools;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;
import com.bbb.utils.DeliveryChargeComparator;



/**
 * The Class BBBCatalogToolsImpl.
 */
public class BBBCatalogToolsImpl extends BBBConfigToolsImpl implements BBBCatalogTools {
	
	private static final String STRING_00 = "00";

	private static final String NUM_REGEX = "\\d+";

	private static final String EMPTYSTRING = "";

	/** The Constant HYPHEN. */
	private static final String HYPHEN = " - ";
    
    /** The Constant SHIPPING_DURATION_IS. */
    private static final String SHIPPING_DURATION_IS = "ShippingDuration is ";
    
    /** The Constant ON_SALE. */
    private static final String ON_SALE = "onSale";
	
	/** The Constant KEY_NOT_FOUND_FOR_PDP_HARMON_TAB_NAME. */
	private static final String KEY_NOT_FOUND_FOR_PDP_HARMON_TAB_NAME = "key not found for PDP Harmon Tab Name";
    
    /** The Constant MM_DD_YYYY. */
    private static final String MM_DD_YYYY = "MM/dd/yyyy";
    
    /** The Constant DD_MM_YYYY. */
    private static final String DD_MM_YYYY = "dd/MM/yyyy";
    
    /** The Constant ID. */
    private static final String ID = "id";
    
    /** The Constant SKU_ATTRIBUTE. */
    private static final String SKU_ATTRIBUTE = "skuAttribute";
    
    /** The Constant ALL. */
    private static final String ALL = "all";
    
    /** The Constant CATALOG_API_METHOD_NAME. */
    private static final String CATALOG_API_METHOD_NAME = "Catalog API Method Name";
    
    /** The Constant BRAND_NAME_RQL. */
    private static final String BRAND_NAME_RQL = "brandName = ?0";
    
    /** The Constant BBB_BUSINESS. */
    private static final String BBB_BUSINESS = "BBBBusiness";
	
	/** The Constant BBB_SYSTEM. */
	private static final String BBB_SYSTEM = "BBBSystem";
	
	/** The Constant EXCEPTION_BAZAAR_VOICE_KEY_NOT_FOUND_FOR_SITE. */
	private static final String EXCEPTION_BAZAAR_VOICE_KEY_NOT_FOUND_FOR_SITE = "Exception - Bazaar voice key not found for site";
	
	/** The Constant ZOOM_MAX. */
	private static final String ZOOM_MAX = "5";
	
	/** The Constant ZOOM_MIN. */
	private static final String ZOOM_MIN = "1";
	
	/** The Constant L2CATEGORY. */
	private static final String L2CATEGORY = "1";
	
	/** The Constant CLAIMABLE_ITEM_DESCRIPTOR_NAME. */
	public static final String CLAIMABLE_ITEM_DESCRIPTOR_NAME ="PromotionClaimable";
	
	/** The site repository. */
	private MutableRepository siteRepository;
	
	/** The int ship sku restriction repository. */
	private MutableRepository intShipSkuRestrictionRepository;
    
    /** The shipping repository. */
    private MutableRepository shippingRepository;
    
    /** The promotion repository. */
    private MutableRepository promotionRepository;
    
    /** The school repository. */
    private MutableRepository schoolRepository;
    
    /** The school ver repository. */
    private MutableRepository schoolVerRepository;
    
    /** The catalog repository. */
    private MutableRepository catalogRepository;
    
    /** The configure repository. */
    private MutableRepository configureRepository;
    
    /** The store repository. */
    private MutableRepository storeRepository;
    
    /** The coupon repository. */
    private MutableRepository couponRepository;
    
    /** The reg cat repo. */
    private MutableRepository regCatRepo;
    
    /** The credit card country repository. */
    private MutableRepository creditCardCountryRepository;
    
    /** The international billing repository. */
    private MutableRepository internationalBillingRepository;
    
    /** The bazaar voice repository. */
    private MutableRepository bazaarVoiceRepository;
    
    /** The vendor repository. */
    private MutableRepository vendorRepository;
    
    /** The page order repository. */
    private Repository pageOrderRepository;
    
    /** The attribute info repository. */
    private Repository attributeInfoRepository;
    
    /** The guides repository. */
    private Repository guidesRepository;
    
    /** The inventory manager. */
    private OnlineInventoryManager inventoryManager;
    
    /** The price list manager. */
    private PriceListManager priceListManager;
    
    /** The idgen. */
    private DCPrefixIdGenerator idgen;
    
    /** The search store manager. */
    private SearchStoreManager searchStoreManager;
	
	/** The m object cache. */
	private BBBObjectCache mObjectCache;
	
	/** The bbb inventory manager. */
	private BBBInventoryManager bbbInventoryManager;
    
    /** The recommended sku repository. */
    private Repository recommendedSkuRepository;

    /** The ship grp free ship attr map. */
    private Map<String, String> shipGrpFreeShipAttrMap = new HashMap<String, String>();
    
    /** The roll up sku property map. */
    private Map<String, String> rollUpSkuPropertyMap = new HashMap<String, String>();
    
    /** The free ship attr ship grp map. */
    private Map<String, String> freeShipAttrShipGrpMap = new HashMap<String, String>();
    
    /** The state no of days than normal map. */
    private Map<String, String> stateNoOfDaysThanNormalMap = new HashMap<String, String>();
    
    /** The exceptional delivery date states list. */
    private List<String> exceptionalDeliveryDateStatesList = new ArrayList<String>();
    
    /** The prop65 attributes list. */
    private List<String> prop65AttributesList = new ArrayList<String>();

    /** The shipping cost rql query. */
    private String shippingCostRqlQuery;
    
    /** The shipping fee rql query. */
    private String shippingFeeRqlQuery;
    
    /** The shipping duration rql query. */
    private String shippingDurationRqlQuery;
    
    /** The shipping method rql query. */
    private String shippingMethodRqlQuery;
    
    /** The shipping fee rql query for null state. */
    private String shippingFeeRqlQueryForNullState;
    
    /** The threshold query. */
    private String thresholdQuery;
    
    /** The promotion query. */
    private String promotionQuery;
    
    /** The registry type query. */
    private String registryTypeQuery;
    
    /** The college category query. */
    private String collegeCategoryQuery;
    
    /** The gift cert product query. */
    private String giftCertProductQuery;
    
    /** The college query. */
    private String collegeQuery;
    
    /** The bopus eligible state query. */
    private String bopusEligibleStateQuery;
    
    /** The bopus in eligible store query. */
    private String bopusInEligibleStoreQuery;
    
    /** The bopus disabled store query. */
    private String bopusDisabledStoreQuery;
    
    /** The bopus disalbed state query. */
    private String bopusDisalbedStateQuery;
    
    /** The profile price list property name. */
    private String profilePriceListPropertyName;
    
    /** The default locale. */
    private String defaultLocale;
    
    /** The canada stores query. */
    private String canadaStoresQuery;
    
    /** The max products per college key. */
    private String maxProductsPerCollegeKey;
    
    /** The config type. */
    private String configType;
    
    /** The college config key. */
    private String collegeConfigKey;
    
    /** The college config key canada. */
    private String collegeConfigKeyCanada;
    
    /** The others cat key. */
    private String othersCatKey;
    
    /** The rebate key. */
    private String rebateKey;
    
    /** The zoom keys. */
    private String zoomKeys;
    
    /** The special purchase attr id. */
    private String specialPurchaseAttrID;
    
    /** The beyond value attr id. */
    private String beyondValueAttrID;
    
    /** The clearence attr id. */
    private String clearenceAttrID;
    
    /** The dorm room collection cat id. */
    private String dormRoomCollectionCatId;
    
    /** The zip code saperater. */
    private String zipCodeSaperater;
    
    /** The brand name query. */
    private String brandNameQuery;
    
    /** The page order query. */
    private String pageOrderQuery;
    
    /** The college category key. */
    private String collegeCategoryKey;
    
    /** The product guide rql query. */
    private String productGuideRqlQuery;
    
    /** The coupon rule sku query. */
    private String couponRuleSkuQuery;
    
    /** The coupon rule vendor dept class query. */
    private String couponRuleVendorDeptClassQuery;
	
	/** The coupon rule vendor dept query. */
	private String couponRuleVendorDeptQuery;
    
    /** The country name. */
    private String countryName;
    
    /** The brand id query. */
    private String brandIdQuery;
    
    /** The us category query. */
    private String usCategoryQuery;
    
    /** The canada category query. */
    private String canadaCategoryQuery;
    
    /** The bazaar voice site specific. */
    private boolean bazaarVoiceSiteSpecific;
	
	/** The preview enabled. */
	private Boolean previewEnabled;
	
	/** The m cms tools. */
	private CmsTools mCmsTools;
	
	/** The claimable tools. */
	private ClaimableTools claimableTools;
	
	/** The dynamic price repository. */
	private Repository dynamicPriceRepository;
	
	/** The price list repository. */
	private MutableRepository priceListRepository;
	
	/** The product cache container. */
	private BBBDynamicPriceCacheContainer productCacheContainer;
	
	/** The sku cache container. */
	private BBBDynamicPriceCacheContainer skuCacheContainer;
	/**	This is the Instance of EndecaSearchTools. */
	private EndecaSearchTools endecaSearchTools;
	/**	This is the Map to hold pageTypeMap. */
	private Map<String, String> pageTypeMap;
	/** The enable dyn repo call plp. */
	private boolean enableDynRepoCallPLP;
	//Jupiter code refactor
	
	/** This is the Instance for BBBSiteRepositoryTools. */
    private BBBSiteRepositoryTools siteRepositoryTools;
    /** This is the Instance for BBBEdwRepositoryTools. */
    private BBBEdwRepositoryTools edwRepositoryTools;
    /** This is the Instance for BBBShippingRepositoryTools. */
    private BBBShippingRepositoryTools shippingRepositoryTools;
    /** This is the Instance for BBBSchoolRepositoryTools. */
    private BBBSchoolRepositoryTools schoolRepositoryTools;
    /** This is the Instance for BBBStoreRepositoryTools. */
    private BBBStoreRepositoryTools storeRepositoryTools;
    /** This is the Instance for BBBPriceListRepositoryTools. */
    private BBBPriceListRepositoryTools priceListRepositoryTools;
    /** This is the Instance for GlobalRepositoryTools. */
    private GlobalRepositoryTools globalRepositoryTools;
         
    private SearchQuery searchQuery;
    
    private String productRelItemQuery;
    private String parentProductQuery;
    private String parentProductNullQuery;
    private ProductManager productManager;
    private String numOfProdRecommToShow;

	public ProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}

	public SearchQuery getSearchQuery() {
		return searchQuery;
	}

	public void setSearchQuery(SearchQuery searchQuery) {
		this.searchQuery = searchQuery;
	}
    
	private EndecaSearchUtil searchUtil;

	public EndecaSearchUtil getSearchUtil() {
		return searchUtil;
	}

	public void setSearchUtil(EndecaSearchUtil searchUtil) {
		this.searchUtil = searchUtil;
	}
	/** Enable local dynamic sku caching*/
	private boolean dynamicSKUCacheEnabledDefault;
	
	/** TBD text for penny price */
	private List<String> tbdPriceString;

	/**
	 * @return the globalRepositoryTools
	 */
	public GlobalRepositoryTools getGlobalRepositoryTools() {
		return globalRepositoryTools;
	}

	/**
	 * @param globalRepositoryTools the globalRepositoryTools to set
	 */
	public void setGlobalRepositoryTools(GlobalRepositoryTools globalRepositoryTools) {
		this.globalRepositoryTools = globalRepositoryTools;
	}

	/**
	 * @return the storeRepositoryTools
	 */
	public BBBStoreRepositoryTools getStoreRepositoryTools() {
		return storeRepositoryTools;
	}

	/**
	 * @param storeRepositoryTools the storeRepositoryTools to set
	 */
	public void setStoreRepositoryTools(BBBStoreRepositoryTools storeRepositoryTools) {
		this.storeRepositoryTools = storeRepositoryTools;
	}

	/**
	 * @return the priceListRepositoryTools
	 */
	public BBBPriceListRepositoryTools getPriceListRepositoryTools() {
		return priceListRepositoryTools;
	}

	/**
	 * @param priceListRepositoryTools the priceListRepositoryTools to set
	 */
	public void setPriceListRepositoryTools(
			BBBPriceListRepositoryTools priceListRepositoryTools) {
		this.priceListRepositoryTools = priceListRepositoryTools;
	}

	/**
	 * @return the shippingRepositoryTools
	 */
	public BBBShippingRepositoryTools getShippingRepositoryTools() {
		return shippingRepositoryTools;
	}

	/**
	 * @param shippingRepositoryTools the shippingRepositoryTools to set
	 */
	public void setShippingRepositoryTools(
			BBBShippingRepositoryTools shippingRepositoryTools) {
		this.shippingRepositoryTools = shippingRepositoryTools;
	}

	/**
	 * @return the schoolRepositoryTools
	 */
	public BBBSchoolRepositoryTools getSchoolRepositoryTools() {
		return schoolRepositoryTools;
	}

	/**
	 * @param schoolRepositoryTools the schoolRepositoryTools to set
	 */
	public void setSchoolRepositoryTools(
			BBBSchoolRepositoryTools schoolRepositoryTools) {
		this.schoolRepositoryTools = schoolRepositoryTools;
	}

	/**
	 * @return the edwRepositoryTools
	 */
	public BBBEdwRepositoryTools getEdwRepositoryTools() {
		return edwRepositoryTools;
	}

	/**
	 * @param edwRepositoryTools the edwRepositoryTools to set
	 */
	public void setEdwRepositoryTools(BBBEdwRepositoryTools edwRepositoryTools) {
		this.edwRepositoryTools = edwRepositoryTools;
	}

	/**
	 * @return the siteRepositoryTools
	 */
	public BBBSiteRepositoryTools getSiteRepositoryTools() {
		return siteRepositoryTools;
	}

	/**
	 * @param siteRepositoryTools the siteRepositoryTools to set
	 */
	public void setSiteRepositoryTools(BBBSiteRepositoryTools siteRepositoryTools) {
		this.siteRepositoryTools = siteRepositoryTools;
	}

	/**
	 * Gets the price list repository.
	 *
	 * @return priceListRepository
	 */
	public MutableRepository getPriceListRepository() {
		return priceListRepository;
	}

	/** The sdd ship method id. */
	private String sddShipMethodId;
	/**	This is the integer to hold lengthOfSwatch. */
	private int lengthOfSwatch;
    
    /** The sdd ship method charge. */
    private String sddShipMethodCharge;
    
	/**
	 * Gets the sdd ship method charge.
	 *
	 * @return the sdd ship method charge
	 */
	public String getSddShipMethodCharge() {
		return sddShipMethodCharge;
	}
	
	/**
	 * Sets the sdd ship method charge.
	 *
	 * @param sddShipMethodCharge the new sdd ship method charge
	 */
	public void setSddShipMethodCharge(String sddShipMethodCharge) {
		this.sddShipMethodCharge = sddShipMethodCharge;
	}
	
	/**
	 * Gets the sdd ship method id.
	 *
	 * @return the sdd ship method id
	 */
	public String getSddShipMethodId() {
		return sddShipMethodId;
	}
	
	/**
	 * Sets the sdd ship method id.
	 *
	 * @param sddShipMethodId the new sdd ship method id
	 */
	public void setSddShipMethodId(String sddShipMethodId) {
		this.sddShipMethodId = sddShipMethodId;
	}
	
	/**
	 * Sets the price list repository.
	 *
	 * @param priceListRepository the new price list repository
	 */
	public void setPriceListRepository(MutableRepository priceListRepository) {
		this.priceListRepository = priceListRepository;
	}

	/**
	 * Gets the claimable tools.
	 *
	 * @return claimableTools
	 */
	public ClaimableTools getClaimableTools() {
		return claimableTools;
	}
	
	/**
	 * Sets the claimable tools.
	 *
	 * @param claimableTools the new claimable tools
	 */
	public void setClaimableTools(ClaimableTools claimableTools) {
		this.claimableTools = claimableTools;
	}

	/** The currency tag converter. */
	private BBBCurrencyTagConvertor currencyTagConverter;
	 
 	/** The bbb site to attribute site map. */
 	private Map<String, String> bbbSiteToAttributeSiteMap;
	
	/** The customization offered site map. */
	private Map<String, String> customizationOfferedSiteMap;
	//LTL 
	/** The Constant Delivery_Surcharge_SKU. */
	private static final String DELIVERY_SURCHARGE_SKU = "DeliverySurcharge";
	
	/** The Constant Assembly_Fee_SKU. */
	private static final String ASSEMBLY_FEE_SKU = "AssemblyFee";
	/** The exim manager. */
	private BBBEximManager eximManager;
	
	/** The log coupon details. */
	private boolean logCouponDetails;
	
	// Regions
	/** The regions rql query. */
	private String regionsRqlQuery;
	
	/** The m schedule appointment manager. */
	private ScheduleAppointmentManager mScheduleAppointmentManager;
	

	/** The Constant EDW_DATA_RQL. */
	private static final String EDW_DATA_RQL = "profileId = ?0";
	

	/** The profile adapter repository. */
	private GSARepository edwProfileDataRepository;
	
	
	/** The edw site spect data repository. */
	private GSARepository edwSiteSpectDataRepository;

	/**
	 * 	private GSARepository edwSiteSpectDataRepository;.
	 *
	 * @return the mScheduleAppointmentManager
	 */
	public ScheduleAppointmentManager getScheduleAppointmentManager() {
		return mScheduleAppointmentManager;
	}

	/**
	 * Sets the schedule appointment manager.
	 *
	 * @param mScheduleAppointmentManager the mScheduleAppointmentManager to set
	 */
	public void setScheduleAppointmentManager(
			ScheduleAppointmentManager mScheduleAppointmentManager) {
		this.mScheduleAppointmentManager = mScheduleAppointmentManager;
	}
	
	
	/** The m user profile repository. */
	private MutableRepository mUserProfileRepository;
	
	/** The sites. */
	private List<String> sites;
    
	/** The dc prefix. */
	private String dcPrefix;
	
	/** The data center map. */
	private Map<String, String> dataCenterMap;
	
	
	/** The edw ttl. */
	private String edwTTL;
	
	/**
	 * Gets the edw ttl.
	 *
	 * @return the edw ttl
	 */
	public String getEdwTTL() {
		return edwTTL;
	}

	/**
	 * Sets the edw ttl.
	 *
	 * @param edwTTL the new edw ttl
	 */
	public void setEdwTTL(String edwTTL) {
		this.edwTTL = edwTTL;
	}

	

	/**
	 * Gets the data center map.
	 *
	 * @return the data center map
	 */
	public Map<String, String> getDataCenterMap() {
		return dataCenterMap;
	}

	/**
	 * Sets the data center map.
	 *
	 * @param dataCenterMap the data center map
	 */
	public void setDataCenterMap(Map<String, String> dataCenterMap) {
		this.dataCenterMap = dataCenterMap;
	}

	/**
	 * Gets the dc prefix.
	 *
	 * @return the dc prefix
	 */
	public String getDcPrefix() {
		return dcPrefix;
	}

	/**
	 * Sets the dc prefix.
	 *
	 * @param dcPrefix the new dc prefix
	 */
	public void setDcPrefix(String dcPrefix) {
		this.dcPrefix = dcPrefix;
	}

	/**
	 * Gets the sites.
	 *
	 * @return the sites
	 */
	public List<String> getSites() {
		return sites;
	}

	/**
	 * Sets the sites.
	 *
	 * @param sites the new sites
	 */
	public void setSites(List<String> sites) {
		this.sites = sites;
	}
	
	/**
	 * Gets the user profile repository.
	 *
	 * @return the userProfileRepository
	 */
	public MutableRepository getUserProfileRepository() {
		return mUserProfileRepository;
	}

	/**
	 * Sets the user profile repository.
	 *
	 * @param pUserProfileRepository the new user profile repository
	 */
	public void setUserProfileRepository(final MutableRepository pUserProfileRepository) {
		this.mUserProfileRepository = pUserProfileRepository;
	}
	
	
	/**
	 * Gets the edw site spect data repository.
	 *
	 * @return the edw site spect data repository
	 */
	public GSARepository getEdwSiteSpectDataRepository() {
		return edwSiteSpectDataRepository;
	}

	/**
	 * Sets the edw site spect data repository.
	 *
	 * @param edwSiteSpectDataRepository the new edw site spect data repository
	 */
	public void setEdwSiteSpectDataRepository(
			GSARepository edwSiteSpectDataRepository) {
		this.edwSiteSpectDataRepository = edwSiteSpectDataRepository;
	}

	/**
	 * Gets the edw profile data repository.
	 *
	 * @return the edw profile data repository
	 */
	public GSARepository getEdwProfileDataRepository() {
		return edwProfileDataRepository;
	}

	/**
	 * Sets the edw profile data repository.
	 *
	 * @param edwProfileDataRepository the new edw profile data repository
	 */
	public void setEdwProfileDataRepository(GSARepository edwProfileDataRepository) {
		this.edwProfileDataRepository = edwProfileDataRepository;
	}

	/**
	 * Checks if is log coupon details.
	 *
	 * @return the logCouponDetails
	 */
	public boolean isLogCouponDetails() {
		return logCouponDetails;
	}

	/**
	 * Sets the log coupon details.
	 *
	 * @param logCouponDetails the logCouponDetails to set
	 */
	public void setLogCouponDetails(boolean logCouponDetails) {
		this.logCouponDetails = logCouponDetails;
	}
	


	/**
	 * Gets the recommended sku repository.
	 *
	 * @return the recommendedSkuRepository
	 */
	public Repository getRecommendedSkuRepository() {
		return recommendedSkuRepository;
	}

	/**
	 * Sets the recommended sku repository.
	 *
	 * @param recommendedSkuRepository            the recommendedSkuRepository to set
	 */
	public void setRecommendedSkuRepository(Repository recommendedSkuRepository) {
		this.recommendedSkuRepository = recommendedSkuRepository;
	}

	/**
	 * Gets the int ship sku restriction repository.
	 *
	 * @return the int ship sku restriction repository
	 */
	public MutableRepository getIntShipSkuRestrictionRepository() {
		return intShipSkuRestrictionRepository;
	}

	/**
	 * Sets the int ship sku restriction repository.
	 *
	 * @param intShipSkuRestrictionRepository the new int ship sku restriction repository
	 */
	public void setIntShipSkuRestrictionRepository(
			MutableRepository intShipSkuRestrictionRepository) {
		this.intShipSkuRestrictionRepository = intShipSkuRestrictionRepository;
	}


	/**
	 * Gets the us category query.
	 *
	 * @return the usCategoryQuery
	 */
	public String getUsCategoryQuery() {
		return usCategoryQuery;
	}

	/**
	 * Sets the us category query.
	 *
	 * @param usCategoryQuery the usCategoryQuery to set
	 */
	public void setUsCategoryQuery(String usCategoryQuery) {
		this.usCategoryQuery = usCategoryQuery;
	}

	/**
	 * Gets the canada category query.
	 *
	 * @return the canadaCategoryQuery
	 */
	public String getCanadaCategoryQuery() {
		return canadaCategoryQuery;
	}

	/**
	 * Sets the canada category query.
	 *
	 * @param canadaCategoryQuery the canadaCategoryQuery to set
	 */
	public void setCanadaCategoryQuery(String canadaCategoryQuery) {
		this.canadaCategoryQuery = canadaCategoryQuery;
	}

    /**
     * Gets the country name.
     *
     * @return the countryName
     */
    public final String getCountryName() {
        return this.countryName;
    }

    /**
     * Sets the country name.
     *
     * @param countryName the countryName to set
     */
    public final void setCountryName(final String countryName) {
        this.countryName = countryName;
    }

    /**
     * Gets the coupon rule sku query.
     *
     * @return the couponRuleSkuQuery
     */
	public String getCouponRuleSkuQuery() {
		return couponRuleSkuQuery;
	}

	/**
	 * Sets the coupon rule sku query.
	 *
	 * @param couponRuleSkuQuery the couponRuleSkuQuery to set
	 */
	public void setCouponRuleSkuQuery(String couponRuleSkuQuery) {
		this.couponRuleSkuQuery = couponRuleSkuQuery;
	}

	/**
	 * Gets the coupon rule vendor dept class query.
	 *
	 * @return the couponRuleVendorDeptClassQuery
	 */
	public String getCouponRuleVendorDeptClassQuery() {
		return couponRuleVendorDeptClassQuery;
	}

	/**
	 * Sets the coupon rule vendor dept class query.
	 *
	 * @param couponRuleVendorDeptClassQuery the couponRuleVendorDeptClassQuery to set
	 */
	public void setCouponRuleVendorDeptClassQuery(
			String couponRuleVendorDeptClassQuery) {
		this.couponRuleVendorDeptClassQuery = couponRuleVendorDeptClassQuery;
	}

	/**
	 * Gets the coupon rule vendor dept query.
	 *
	 * @return the couponRuleVendorDeptQuery
	 */
	public String getCouponRuleVendorDeptQuery() {
		return couponRuleVendorDeptQuery;
	}

	/**
	 * Sets the coupon rule vendor dept query.
	 *
	 * @param couponRuleVendorDeptQuery the couponRuleVendorDeptQuery to set
	 */
	public void setCouponRuleVendorDeptQuery(String couponRuleVendorDeptQuery) {
		this.couponRuleVendorDeptQuery = couponRuleVendorDeptQuery;
	}

    /**
     * Gets the credit card country repository.
     *
     * @return the creditCardCountryRepository
     */
    public final MutableRepository getCreditCardCountryRepository() {
        return this.creditCardCountryRepository;
    }

    /**
     * Sets the credit card country repository.
     *
     * @param creditCardCountryRepository the creditCardCountryRepository to set
     */
    public final void setCreditCardCountryRepository(final MutableRepository creditCardCountryRepository) {
        this.creditCardCountryRepository = creditCardCountryRepository;
    }
    
    /**
     * Gets the international billing repository.
     *
     * @return internationalBillingRepository
     */
    public final MutableRepository getInternationalBillingRepository() {
		return internationalBillingRepository;
	}
    
    /**
     * Sets the international billing repository.
     *
     * @param internationalBillingRepository the new international billing repository
     */
	public final void setInternationalBillingRepository(final
			MutableRepository internationalBillingRepository) {
		this.internationalBillingRepository = internationalBillingRepository;
	}


    /**
     * Gets the product guide rql query.
     *
     * @return the mProductGuideRqlQuery
     */
    public final String getProductGuideRqlQuery() {
        return this.productGuideRqlQuery;
    }

    /**
     * Sets the product guide rql query.
     *
     * @param pProductGuideRqlQuery the mProductGuideRqlQuery to set
     */
    public final void setProductGuideRqlQuery(final String pProductGuideRqlQuery) {
        this.productGuideRqlQuery = pProductGuideRqlQuery;
    }

    /**
     * Gets the guides repository.
     *
     * @return the mGuidesRepository
     */
    public final Repository getGuidesRepository() {
        return this.guidesRepository;
    }

    /**
     * Sets the guides repository.
     *
     * @param pGuidesRepository the mGuidesRepository to set
     */
    public final void setGuidesRepository(final Repository pGuidesRepository) {
        this.guidesRepository = pGuidesRepository;
    }

    /**
     * Gets the page order query.
     *
     * @return Page order query
     */
    public final String getPageOrderQuery() {
        return this.pageOrderQuery;
    }

    /**
     * Sets the page order query.
     *
     * @param pageOrderQuery the new page order query
     */
    public final void setPageOrderQuery(final String pageOrderQuery) {
        this.pageOrderQuery = pageOrderQuery;
    }

    /**
     * Gets the dorm room collection cat id.
     *
     * @return the dormRoomCollectionCatId
     */
    public final String getDormRoomCollectionCatId() {
        return this.dormRoomCollectionCatId;
    }

    /**
     * Sets the dorm room collection cat id.
     *
     * @param dormRoomCollectionCatId the dormRoomCollectionCatId to set
     */
    public final void setDormRoomCollectionCatId(final String dormRoomCollectionCatId) {
        this.dormRoomCollectionCatId = dormRoomCollectionCatId;
    }

    /**
     * Gets the free ship attr ship grp map.
     *
     * @return the freeShipAttrShipGrpMap
     */
    public final Map<String, String> getFreeShipAttrShipGrpMap() {
        return this.freeShipAttrShipGrpMap;
    }

    /**
     * Gets the search store manager.
     *
     * @return the searchStoreManager
     */
    public final SearchStoreManager getSearchStoreManager() {
        return this.searchStoreManager;
    }

    /**
     * Sets the search store manager.
     *
     * @param pSearchStoreManager the searchStoreManager to set
     */
    public final void setSearchStoreManager(final SearchStoreManager pSearchStoreManager) {
        this.searchStoreManager = pSearchStoreManager;
    }

    /**
     * Sets the free ship attr ship grp map.
     *
     * @param freeShipAttrShipGrpMap the freeShipAttrShipGrpMap to set
     */
    public final void setFreeShipAttrShipGrpMap(final Map<String, String> freeShipAttrShipGrpMap) {
        this.freeShipAttrShipGrpMap = freeShipAttrShipGrpMap;
    }

    /**
     * Gets the attribute info repository.
     *
     * @return the attributeInfoRepository
     */
    public final Repository getAttributeInfoRepository() {
        return this.attributeInfoRepository;
    }

    /**
     * Sets the attribute info repository.
     *
     * @param pAttributeInfoRepository the attributeInfoRepository to set
     */
    public final void setAttributeInfoRepository(final Repository pAttributeInfoRepository) {
        this.attributeInfoRepository = pAttributeInfoRepository;
    }

    /**
     * Gets the page order repository.
     *
     * @return Page Order Repository
     */
    public final Repository getPageOrderRepository() {
        return this.pageOrderRepository;
    }

    /**
     * Sets the page order repository.
     *
     * @param pageOrderRepository the new page order repository
     */
    public final void setPageOrderRepository(final Repository pageOrderRepository) {
        this.pageOrderRepository = pageOrderRepository;
    }

    // added as part of InstantPreview story
    /** The environment identifier. */
    private EnvironmentIdentifier environmentIdentifier;

    /**
     * Gets the environment identifier.
     *
     * @return Environment Identifier
     */
    public final EnvironmentIdentifier getEnvironmentIdentifier() {
        return this.environmentIdentifier;
    }

    /**
     * Sets the environment identifier.
     *
     * @param environmentIdentifier the new environment identifier
     */
    public final void setEnvironmentIdentifier(final EnvironmentIdentifier environmentIdentifier) {
        this.environmentIdentifier = environmentIdentifier;
    }

    /**
     * Gets the state no of days than normal map.
     *
     * @return State Number of Days
     */
    public final Map<String, String> getStateNoOfDaysThanNormalMap() {
        return this.stateNoOfDaysThanNormalMap;
    }

    /**
     * Sets the state no of days than normal map.
     *
     * @param stateNoOfDaysThanNormalMap the state no of days than normal map
     */
    public final void setStateNoOfDaysThanNormalMap(final Map<String, String> stateNoOfDaysThanNormalMap) {
        this.stateNoOfDaysThanNormalMap = stateNoOfDaysThanNormalMap;
    }

    /**
     * Gets the exceptional delivery date states list.
     *
     * @return Exception Deliver Date State LIst
     */
    public final List<String> getExceptionalDeliveryDateStatesList() {
        return this.exceptionalDeliveryDateStatesList;
    }

    /**
     * Sets the exceptional delivery date states list.
     *
     * @param exceptionalDeliveryDateStatesList the new exceptional delivery date states list
     */
    public final void setExceptionalDeliveryDateStatesList(final List<String> exceptionalDeliveryDateStatesList) {
        this.exceptionalDeliveryDateStatesList = exceptionalDeliveryDateStatesList;
    }

    /**
     * Gets the prop65 attributes list.
     *
     * @return Prop65 Attribute List
     */
    public final List<String> getProp65AttributesList() {
        return this.prop65AttributesList;
    }

    /**
     * Sets the prop65 attributes list.
     *
     * @param prop65AttributesList the new prop65 attributes list
     */
    public final void setProp65AttributesList(final List<String> prop65AttributesList) {
        this.prop65AttributesList = prop65AttributesList;
    }

    /**
     * Gets the rebate key.
     *
     * @return Rebate Key
     */
    public final String getRebateKey() {
        return this.rebateKey;
    }

    /**
     * Sets the rebate key.
     *
     * @param rebateKey the new rebate key
     */
    public final void setRebateKey(final String rebateKey) {
        this.rebateKey = rebateKey;
    }

    /**
     * Gets the zoom keys.
     *
     * @return Zoom Keys
     */
    public final String getZoomKeys() {
        return this.zoomKeys;
    }

    /**
     * Sets the zoom keys.
     *
     * @param zoomKeys the new zoom keys
     */
    public final void setZoomKeys(final String zoomKeys) {
        this.zoomKeys = zoomKeys;
    }

    /**
     * Gets the reg cat repo.
     *
     * @return the regCatRepo
     */
    public final MutableRepository getRegCatRepo() {
        return this.regCatRepo;
    }

    /**
     * Gets the ship grp free ship attr map.
     *
     * @return the shipGrpFreeShipAttrMap
     */
    public final Map<String, String> getShipGrpFreeShipAttrMap() {
        return this.shipGrpFreeShipAttrMap;
    }

    /**
     * Sets the ship grp free ship attr map.
     *
     * @param shipGrpFreeShipAttrMap the shipGrpFreeShipAttrMap to set
     */
    public final void setShipGrpFreeShipAttrMap(final Map<String, String> shipGrpFreeShipAttrMap) {
        this.shipGrpFreeShipAttrMap = shipGrpFreeShipAttrMap;
    }

    /**
     * Sets the reg cat repo.
     *
     * @param regCatRepo the regCatRepo to set
     */
    public final void setRegCatRepo(final MutableRepository regCatRepo) {
        this.regCatRepo = regCatRepo;
    }

    /**
     * Gets the max products per college key.
     *
     * @return the maxProductsPerCollegeKey
     */
    public final String getMaxProductsPerCollegeKey() {
        return this.maxProductsPerCollegeKey;
    }

    /**
     * Sets the max products per college key.
     *
     * @param maxProductsPerCollegeKey the maxProductsPerCollegeKey to set
     */
    public final void setMaxProductsPerCollegeKey(final String maxProductsPerCollegeKey) {
        this.maxProductsPerCollegeKey = maxProductsPerCollegeKey;
    }
    
    /**
     * Checks if is bazaar voice site specific.
     *
     * @return the bazaarVoiceSiteSpecific
     */
    public final boolean isBazaarVoiceSiteSpecific() {
        return this.bazaarVoiceSiteSpecific;
    }

    /**
     * Sets the bazaar voice site specific.
     *
     * @param bazaarVoiceSiteSpecific the bazaarVoiceSiteSpecific to set
     */
    public final void setBazaarVoiceSiteSpecific(final boolean bazaarVoiceSiteSpecific) {
        this.bazaarVoiceSiteSpecific = bazaarVoiceSiteSpecific;
    }

    /**
     * Gets the canada stores query.
     *
     * @return the canadaStoresQuery
     */
    public final String getCanadaStoresQuery() {
        return this.canadaStoresQuery;
    }

    /**
     * Sets the canada stores query.
     *
     * @param canadaStoresQuery the canadaStoresQuery to set
     */
    public final void setCanadaStoresQuery(final String canadaStoresQuery) {
        this.canadaStoresQuery = canadaStoresQuery;
    }

    /**
     * Gets the store repository.
     *
     * @return the storeRepository
     */
    public MutableRepository getStoreRepository() {
        return this.storeRepository;
    }

    /**
     * Sets the store repository.
     *
     * @param storeRepository the storeRepository to set
     */
    public final void setStoreRepository(final MutableRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    /**
     * Gets the default locale.
     *
     * @return Default Locale
     */
    public final String getDefaultLocale() {
        return this.defaultLocale;
    }

    /**
     * Sets the default locale.
     *
     * @param defaultLocale the new default locale
     */
    public final void setDefaultLocale(final String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    /**
     * Gets the bopus in eligible store query.
     *
     * @return the bopusInEligibleStoreQuery
     */
    public final String getBopusInEligibleStoreQuery() {
        return this.bopusInEligibleStoreQuery;
    }

    /**
     * Sets the bopus in eligible store query.
     *
     * @param bopusInEligibleStoreQuery the bopusInEligibleStoreQuery to set
     */
    public final void setBopusInEligibleStoreQuery(final String bopusInEligibleStoreQuery) {
        this.bopusInEligibleStoreQuery = bopusInEligibleStoreQuery;
    }

    /**
     * Gets the bopus eligible state query.
     *
     * @return the bopusEligibleStateQuery
     */
    public final String getBopusEligibleStateQuery() {
        return this.bopusEligibleStateQuery;
    }

    /**
     * Sets the bopus eligible state query.
     *
     * @param bopusEligibleStateQuery the bopusEligibleStateQuery to set
     */
    public final void setBopusEligibleStateQuery(final String bopusEligibleStateQuery) {
        this.bopusEligibleStateQuery = bopusEligibleStateQuery;
    }

    /**
     * Gets the college query.
     *
     * @return the collegeQuery
     */
    public final String getCollegeQuery() {
        return this.collegeQuery;
    }

    /**
     * Sets the college query.
     *
     * @param collegeQuery the collegeQuery to set
     */
    public final void setCollegeQuery(final String collegeQuery) {
        this.collegeQuery = collegeQuery;
    }

    /**
     * Gets the gift cert product query.
     *
     * @return the giftCertProductQuery
     */
    public final String getGiftCertProductQuery() {
        return this.giftCertProductQuery;
    }

    /**
     * Sets the gift cert product query.
     *
     * @param giftCertProductQuery the giftCertProductQuery to set
     */
    public final void setGiftCertProductQuery(final String giftCertProductQuery) {
        this.giftCertProductQuery = giftCertProductQuery;
    }

    /**
     * Gets the brand name query.
     *
     * @return the BrandNameQuery
     */
    public final String getBrandNameQuery() {
        return this.brandNameQuery;
    }

    /**
     * Sets the brand name query.
     *
     * @param brandNameQuery the brandNameQuery to set
     */
    public final void setBrandNameQuery(final String brandNameQuery) {
        this.brandNameQuery = brandNameQuery;
    }

    /**
     * Gets the college category query.
     *
     * @return the collegeCategoryQuery
     */
    public final String getCollegeCategoryQuery() {
        return this.collegeCategoryQuery;
    }

    /**
     * Sets the college category query.
     *
     * @param collegeCategoryQuery the collegeCategoryQuery to set
     */
    public final void setCollegeCategoryQuery(final String collegeCategoryQuery) {
        this.collegeCategoryQuery = collegeCategoryQuery;
    }

    /**
     * Gets the bazaar voice repository.
     *
     * @return the bazaarVoiceRepository
     */
    public final MutableRepository getBazaarVoiceRepository() {
        return this.bazaarVoiceRepository;
    }

    /**
     * Sets the bazaar voice repository.
     *
     * @param pBazaarVoiceRepository the bazaarVoiceRepository to set
     */
    public final void setBazaarVoiceRepository(final MutableRepository pBazaarVoiceRepository) {
        this.bazaarVoiceRepository = pBazaarVoiceRepository;
    }

    /**
     * Gets the promotion repository.
     *
     * @return the promotionRepository
     */
    public final MutableRepository getPromotionRepository() {
        return this.promotionRepository;
    }

    /**
     * Sets the promotion repository.
     *
     * @param promotionRepository the promotionRepository to set
     */
    public final void setPromotionRepository(final MutableRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    /**
     * Gets the shipping fee rql query for null state.
     *
     * @return the shippingFeeRqlQueryForNullState
     */
    public final String getShippingFeeRqlQueryForNullState() {
        return this.shippingFeeRqlQueryForNullState;
    }

    /**
     * Gets the school repository.
     *
     * @return the schoolRepository
     */
    public final MutableRepository getSchoolRepository() {
        return this.schoolRepository;
    }

    /**
     * Sets the school repository.
     *
     * @param schoolRepository the schoolRepository to set
     */
    public final void setSchoolRepository(final MutableRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    /**
     * Gets the school ver repository.
     *
     * @return the schoolRepository
     */
    public final MutableRepository getSchoolVerRepository() {
        return this.schoolVerRepository;
    }

    /**
     * Sets the school ver repository.
     *
     * @param schoolVerRepository schoolRepository the schoolRepository to set
     */
    public final void setSchoolVerRepository(final MutableRepository schoolVerRepository) {
        this.schoolVerRepository = schoolVerRepository;
    }

    /**
     * Sets the shipping fee rql query for null state.
     *
     * @param shippingFeeRqlQueryForNullState the shippingFeeRqlQueryForNullState to set
     */
    public final void setShippingFeeRqlQueryForNullState(final String shippingFeeRqlQueryForNullState) {
        this.shippingFeeRqlQueryForNullState = shippingFeeRqlQueryForNullState;
    }

    /**
     * Gets the shipping cost rql query.
     *
     * @return the shippingCostRqlQuery
     */
    public final String getShippingCostRqlQuery() {
        return this.shippingCostRqlQuery;
    }

    /**
     * Sets the shipping cost rql query.
     *
     * @param shippingCostRqlQuery the shippingCostRqlQuery to set
     */
    public final void setShippingCostRqlQuery(final String shippingCostRqlQuery) {
        this.shippingCostRqlQuery = shippingCostRqlQuery;
    }

    /**
     * Gets the site repository.
     *
     * @return the siteRepository
     */
    @Override
    public MutableRepository getSiteRepository() {
        return this.siteRepository;
    }

    /**
     * Sets the site repository.
     *
     * @param siteRepository the siteRepository to set
     */
    @Override
    public void setSiteRepository(final MutableRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    /**
     * Gets the shipping repository.
     *
     * @return the shippingRepository
     */
    public final MutableRepository getShippingRepository() {
        return this.shippingRepository;
    }

    /**
     * Sets the shipping repository.
     *
     * @param shippingRepository the shippingRepository to set
     */
    public final void setShippingRepository(final MutableRepository shippingRepository) {
        this.shippingRepository = shippingRepository;
    }

    /**
     * Gets the catalog repository.
     *
     * @return the catalogRepository
     */
    public MutableRepository getCatalogRepository() {
        return this.catalogRepository;
    }

    /**
     * Sets the catalog repository.
     *
     * @param catalogRepository the catalogRepository to set
     */
    public final void setCatalogRepository(final MutableRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBConfigToolsImpl#getIdgen()
     */
    @Override
    public final DCPrefixIdGenerator getIdgen() {
        return this.idgen;
    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBConfigToolsImpl#setIdgen(com.bbb.framework.goldengate.DCPrefixIdGenerator)
     */
    @Override
    public final void setIdgen(final DCPrefixIdGenerator idgen) {
        this.idgen = idgen;
    }

    /**
     * Gets the roll up sku property map.
     *
     * @return the rollUpSkuPropertyMap
     */
    public final Map<String, String> getRollUpSkuPropertyMap() {
        return this.rollUpSkuPropertyMap;
    }

    /**
     * Sets the roll up sku property map.
     *
     * @param rollUpSkuPropertyMap the rollUpSkuPropertyMap to set
     */
    public final void setRollUpSkuPropertyMap(final Map<String, String> rollUpSkuPropertyMap) {
        this.rollUpSkuPropertyMap = rollUpSkuPropertyMap;
    }

    /**
     * Gets the shipping fee rql query.
     *
     * @return the shippingFeeRqlQuery
     */
    public final String getShippingFeeRqlQuery() {
        return this.shippingFeeRqlQuery;
    }

    /**
     * Sets the shipping fee rql query.
     *
     * @param shippingFeeRqlQuery the shippingFeeRqlQuery to set
     */
    public final void setShippingFeeRqlQuery(final String shippingFeeRqlQuery) {
        this.shippingFeeRqlQuery = shippingFeeRqlQuery;
    }

    /**
     * Gets the configure repository.
     *
     * @return the configRepository
     */
    @Override
    public final MutableRepository getConfigureRepository() {

        return this.configureRepository;
    }

    /**
     * Sets the configure repository.
     *
     * @param configRepository the configRepository to set
     */
    @Override
    public final void setConfigureRepository(final MutableRepository configRepository) {

        this.configureRepository = configRepository;
    }

    /**
     * Gets the inventory manager.
     *
     * @return the inventoryManager
     */
    public final OnlineInventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    /**
     * Sets the inventory manager.
     *
     * @param inventoryManager the inventoryManager to set
     */
    public final void setInventoryManager(final OnlineInventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    /**
     * Gets the threshold query.
     *
     * @return the thresholdQuery
     */
    public final String getThresholdQuery() {
        return this.thresholdQuery;
    }

    /**
     * Sets the threshold query.
     *
     * @param thresholdQuery the thresholdQuery to set
     */
    public final void setThresholdQuery(final String thresholdQuery) {
        this.thresholdQuery = thresholdQuery;
    }

    /**
     * Gets the promotion query.
     *
     * @return the promotionQuery
     */
    public final String getPromotionQuery() {
        return this.promotionQuery;
    }

    /**
     * Gets the registry type query.
     *
     * @return Registry Type Query
     */
    public final String getRegistryTypeQuery() {
        return this.registryTypeQuery;
    }

    /**
     * Sets the registry type query.
     *
     * @param registryTypeQuery the new registry type query
     */
    public final void setRegistryTypeQuery(final String registryTypeQuery) {
        this.registryTypeQuery = registryTypeQuery;
    }

    /**
     * Sets the promotion query.
     *
     * @param promotionQuery the promotionQuery to set
     */
    public final void setPromotionQuery(final String promotionQuery) {
        this.promotionQuery = promotionQuery;
    }

    /**
     * Gets the price list manager.
     *
     * @return Price List Manager
     */
    public final PriceListManager getPriceListManager() {
        return this.priceListManager;
    }

    /**
     * Sets the price list manager.
     *
     * @param priceListManager the new price list manager
     */
    public final void setPriceListManager(final PriceListManager priceListManager) {
        this.priceListManager = priceListManager;
    }

    /**
     * Sets the profile price list property name.
     *
     * @param pProfilePriceListPropertyName the new profile price list property name
     */
    public final void setProfilePriceListPropertyName(final String pProfilePriceListPropertyName) {
        this.profilePriceListPropertyName = pProfilePriceListPropertyName;
    }

    /**
     * Gets the others cat key.
     *
     * @return the othersCatKey
     */
    public final String getOthersCatKey() {
        return this.othersCatKey;
    }

    /**
     * Sets the others cat key.
     *
     * @param othersCatKey the othersCatKey to set
     */
    public final void setOthersCatKey(final String othersCatKey) {
        this.othersCatKey = othersCatKey;
    }

    /**
     * Gets the profile price list property name.
     *
     * @return Profile LIst Property Manager
     */
    public final String getProfilePriceListPropertyName() {
        return this.profilePriceListPropertyName;
    }

    /**
     * Gets the config type.
     *
     * @return the configType
     */
    public final String getConfigType() {
        return this.configType;
    }

    /**
     * Sets the config type.
     *
     * @param configType the configType to set
     */
    public final void setConfigType(final String configType) {
        this.configType = configType;
    }

    /**
     * Gets the college config key.
     *
     * @return the collegeConfigKey
     */
    public final String getCollegeConfigKey() {
        return this.collegeConfigKey;
    }

    /**
     * Sets the college config key.
     *
     * @param collegeConfigKey the collegeConfigKey to set
     */
    public final void setCollegeConfigKey(final String collegeConfigKey) {
        this.collegeConfigKey = collegeConfigKey;
    }

    /**
     * Gets the college config key canada.
     *
     * @return College Config Key for Canada
     */
    public final String getCollegeConfigKeyCanada() {
        return this.collegeConfigKeyCanada;
    }

    /**
     * Sets the college config key canada.
     *
     * @param collegeConfigKeyCanada the new college config key canada
     */
    public final void setCollegeConfigKeyCanada(final String collegeConfigKeyCanada) {
        this.collegeConfigKeyCanada = collegeConfigKeyCanada;
    }

    /**
     * Gets the coupon repository.
     *
     * @return the couponRepository
     */
    public final MutableRepository getCouponRepository() {
        return this.couponRepository;
    }

    /**
     * Sets the coupon repository.
     *
     * @param couponRepository the couponRepository to set
     */
    public final void setCouponRepository(final MutableRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    /** The stores query. */
    private String storesQuery;

    /**
     * Gets the stores query.
     *
     * @return Stores Query
     */
    public final String getStoresQuery() {
        return this.storesQuery;
    }

    /**
     * Sets the stores query.
     *
     * @param storesQuery the new stores query
     */
    public final void setStoresQuery(final String storesQuery) {
        this.storesQuery = storesQuery;
    }

    /**
     * Gets the college category key.
     *
     * @return College Category Key
     */
    public final String getCollegeCategoryKey() {
        return this.collegeCategoryKey;
    }

    /**
     * Sets the college category key.
     *
     * @param collegeCategoryKey the new college category key
     */
    /**
	 * @return the endecaSearchTools
	 */
	public EndecaSearchTools getEndecaSearchTools() {
		return endecaSearchTools;
	}
	/**
	 * @param endecaSearchTools the endecaSearchTools to set
	 */
	public void setEndecaSearchTools(EndecaSearchTools endecaSearchTools) {
		this.endecaSearchTools = endecaSearchTools;
	}
	/**
	 * @return the pageTypeMap
	 */
	public Map<String, String> getPageTypeMap() {
		return pageTypeMap;
	}
	/**
	 * @param pageTypeMap the pageTypeMap to set
	 */
	public void setPageTypeMap(Map<String, String> pageTypeMap) {
		this.pageTypeMap = pageTypeMap;
	}
	/** @param collegeCategoryKey */
    public final void setCollegeCategoryKey(final String collegeCategoryKey) {
        this.collegeCategoryKey = collegeCategoryKey;
    }

    /**
     *  Overloaded sku details method for store type sku does not check for weboffered flag if the sku is of type store
     * i.e. bopus flag is true
     *
     * @param siteId the site id
     * @param skuId the sku id
     * @param calculateAboveBelowLine the calculate above below line
     * @param isMinimal the is minimal
     * @param includeStoreItems the include store items
     * @return SKU Details
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public SKUDetailVO getSKUDetails(final String siteId, final String skuId,
                    final boolean calculateAboveBelowLine, final boolean isMinimal, final boolean includeStoreItems)
                    throws BBBSystemException, BBBBusinessException {
        this.logDebug("Catalog API Method Name [getSKUDetails] for store sku siteId[" + siteId + "] pSkuId[" + skuId
                        + "]");
        if ((siteId == null) || StringUtils.isEmpty(siteId)) {
            throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                            BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
        }
        boolean sameDayDeliveryFlag = false;
		String sddEligibleOn = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG);
		if(null != sddEligibleOn){
			sameDayDeliveryFlag = Boolean.valueOf(sddEligibleOn);
		}
        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetailsForStore");
            final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId,
                            BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
            if (skuRepositoryItem == null) {
                this.logTrace("Repository Item is null for sku id " + skuId);
                throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
            }

            boolean isStoreSku = false;
            if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_STORE_SKU_SKU_PROPERTY_NAME) != null) {
                isStoreSku = ((Boolean) skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.IS_STORE_SKU_SKU_PROPERTY_NAME)).booleanValue();
            }

            final boolean isSkuActive = this.isSkuActive(skuRepositoryItem);
            this.logTrace(skuId + " Are details required  for store items ? " + includeStoreItems
                            + " is sku a store sku?? " + isStoreSku);

            if (!isSkuActive || (isStoreSku && includeStoreItems)) {
                this.logTrace(skuId + " sku is a store sku so returning minimal details of Sku   ");
                SKUDetailVO skuDetail = this.getMinimalSku(skuRepositoryItem);
                if (skuDetail.isLtlItem()) {
                	skuDetail.setEligibleShipMethods(this.getLTLEligibleShippingMethods(skuRepositoryItem.getRepositoryId(), siteId, this.getDefaultLocale()));
        		} else {
        			skuDetail.setEligibleShipMethods(this.getShippingMethodsForSku(siteId, skuId, sameDayDeliveryFlag));
        		}
                return skuDetail;
            }

            return this.getSKUDetailVO(skuRepositoryItem, siteId, calculateAboveBelowLine);
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getSKUDetails]: RepositoryException "
                            + BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION);
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetailsForStore");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);

        } finally {
        	this.logDebug("Catalog API Method Name [getSKUDetails] for store sku siteId[" + siteId + "] pSkuId[" + skuId
        			+ "] Exit");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetailsForStore");
        }

    }
		
	/**
	 * It return min SKu details without Checking for Active Flag.
	 *
	 * @param skuRepositoryItem the sku repository item
	 * @return the minimal sku
	 */
    public SKUDetailVO getMinimalSku(final RepositoryItem skuRepositoryItem) {
    	
    	this.logDebug("Catalog API Method Name [getMinimalSku] for skuRepositoryItem [" + skuRepositoryItem.getRepositoryId() + "] Entry");
        final SKUDetailVO skuVo = new SKUDetailVO(skuRepositoryItem);
        final boolean isSkuActive = this.isSkuActive(skuRepositoryItem);
        skuVo.setSkuId(skuRepositoryItem.getRepositoryId());
        if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) != null) {
        	skuVo.setLtlItem(((Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU)).booleanValue());
        }
        
        if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_SKU_PROPERTY_NAME) != null) {
            skuVo.setDisplayName((String) skuRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_SKU_PROPERTY_NAME));
        }
        skuVo.setActiveFlag(isSkuActive);
        if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) != null) {
            final boolean webOffered = ((Boolean) skuRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME)).booleanValue();
            skuVo.setWebOfferedFlag(webOffered);
        }
        if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) != null) {
            final boolean disable = ((Boolean) skuRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME)).booleanValue();
            skuVo.setDisableFlag(disable);
        }
		 skuVo.setAssemblyOffered(false);
        if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_ASSEMBLY_FEE_OFFERED) != null) {
        	skuVo.setAssemblyOffered((Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_ASSEMBLY_FEE_OFFERED));
		}
        skuVo.setStoreSKU(true);
        skuVo.setActiveFlag(false);
        if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME) != null) {
            skuVo.setColor((String) skuRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME));
        }
        if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SIZE_SKU_PROPERTY_NAME) != null) {
            skuVo.setSize((String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SIZE_SKU_PROPERTY_NAME));
        }

        final ImageVO skuImages = new ImageVO();
        if ((skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_IMAGE_PROPERTY_NAME) != null)
                        || (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME) != null)) {
            final String smallImagePath = (String) skuRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_IMAGE_PROPERTY_NAME);
            skuImages.setSmallImage(smallImagePath);
        }
        if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME) != null) {
            final String mediumImagePath = (String) skuRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME);
            skuImages.setMediumImage(mediumImagePath);
        }
        skuVo.setSkuImages(skuImages);
        if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.UPC_SKU_PROPERTY_NAME) != null) {
            skuVo.setUpc((String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.UPC_SKU_PROPERTY_NAME));
        }

        // added for bopus and flagoff condition
        if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.BOPUS_EXCLUSION_SKU_PROPERTY_NAME) != null) {
            skuVo.setBopusExcludedForMinimalSku(((Boolean) skuRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.BOPUS_EXCLUSION_SKU_PROPERTY_NAME)).booleanValue());
        }
		/*	BPSI-3285 DSK | Handle Pricing message for Personalized Item */
        if (skuRepositoryItem.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) != null) {
        	skuVo.setPersonalizationType(((String) skuRepositoryItem.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE)));
        }
        String shipMsgDisplayFlag = BBBCoreConstants.FALSE;
		try {
			shipMsgDisplayFlag = getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG).get(0);
		if(Boolean.parseBoolean(shipMsgDisplayFlag)){
			updateShippingMessageFlag(skuVo, false, 0.0);
		}
		} catch (BBBSystemException | BBBBusinessException e) {
			logError("Error while getting config key ShipMsgDisplayFlag value", e);
		}
        
        this.logDebug("Catalog API Method Name [getMinimalSku] for skuRepositoryItem [" + skuRepositoryItem.getRepositoryId() + "] Exit");
        return skuVo;
    }

    /**
     *  If the SKU does not exist in the system then the method will throw BBBBusinessException with an error code
     * indicating the SKU does not exist If the SKU is not active yet based on Start date and End date method method
     * will throw BBBBusinessException with an error code indicating the SKU is not active If the SKU is not offered
     * online or on the site which is determined based on WEB_OFFERED_FLAG and DISABLE_FLAG the method will set a flag
     * "isSoldOnline" on the SKU as false. The front end needs to handle this flag in Browse and Registry accordingly If
     * the SKU exists in the system, the details are retrieved from the Repository Cache and the VO is prepared and sent
     * to front end If the calculateAboveBelowLine flag is true then the Above or Below Line logic is calculated for
     * each SKU
     *
     * @param siteId the site id
     * @param skuId the sku id
     * @param calculateAboveBelowLine the calculate above below line
     * @param intlUser the intl user
     * @return the SKU details
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public SKUDetailVO getSKUDetails(final String siteId, final String skuId,
                    final boolean calculateAboveBelowLine,String ... intlUser) throws BBBSystemException, BBBBusinessException {
        this.logDebug("Catalog API Method Name [getSKUDetails] siteId[" + siteId + "] pSkuId[" + skuId + "]");
        if ((siteId == null) || StringUtils.isEmpty(siteId)) {
            throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                            BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
        }
        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetails");
            final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId,
                            BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
            if (skuRepositoryItem == null) {
                this.logTrace("Repository Item is null for skuId " + skuId);
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetails");
                throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
            }

            final boolean isActive = this.isSkuActive(skuRepositoryItem,intlUser);
            this.logTrace(skuId + " Sku is disabled no longer available");
            if (isActive) {
                return this.getSKUDetailVO(skuRepositoryItem, siteId, calculateAboveBelowLine);
            }
            
            this.logTrace(skuId + " In Exception Sku is disabled no longer available");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetails");
            throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY,
                            BBBCatalogErrorCodes.SKU_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY);
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getSKUDetails]: RepositoryException for sku Id " + skuId);
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetails");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);

        } finally {
        	this.logDebug("Catalog API Method Name [getSKUDetails] siteId[" + siteId + "] pSkuId[" + skuId + "] Exit");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetails");
        }

    }
    
    /**
     * Adding a new method to for everLiving pdp sku details. Retriving sku details without checking if it's active.
     *
     * @param siteId the site id
     * @param skuId the sku id
     * @param calculateAboveBelowLine the calculate above below line
     * @return the ever living sku details
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    
    	public final SKUDetailVO getEverLivingSKUDetails(final String siteId, final String skuId,
            final boolean calculateAboveBelowLine) throws BBBSystemException, BBBBusinessException {
			this.logDebug("Catalog API Method Name [getEverLivingSKUDetails] siteId[" + siteId + "] pSkuId[" + skuId + "]");
			if ((siteId == null) || StringUtils.isEmpty(siteId)) {
			    throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
			                    BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
			}
			try {
			    BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetails");
			    final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId,
			                    BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
			    if (skuRepositoryItem == null) {
			        this.logTrace("Repository Item is null for skuId " + skuId);
			        BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetails");
			        throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
			                        BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
			    }
			        return this.getEverLivingSKUDetailVO(skuRepositoryItem, siteId, calculateAboveBelowLine);
			        
			} catch (final RepositoryException e) {
			    this.logError("Catalog API Method Name [getEverLivingSKUDetails]: RepositoryException for sku Id " + skuId);
			    BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetails");
			    throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
			                    BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
			
			} finally {
				this.logDebug("Catalog API Method Name [getEverLivingSKUDetails] siteId[" + siteId + "] pSkuId[" + skuId + "] Exit");
			    BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetails");
			}
			
			}																		

    /**
     *  If the SKU does not exist in the system then the method will throw BBBBusinessException with an error code
     * indicating the SKU does not exist If the SKU is not active yet based on Start date and End date method method
     * will throw BBBBusinessException with an error code indicating the SKU is not active If the SKU is not offered
     * online or on the site which is determined based on WEB_OFFERED_FLAG and DISABLE_FLAG the method will set a flag
     * "isSoldOnline" on the SKU as false. The front end needs to handle this flag in Browse and Registry accordingly If
     * the SKU exists in the system, the details are retrieved from the Repository Cache and the VO is prepared and sent
     * to front end
     *
     * @param siteId the site id
     * @param skuId the sku id
     * @return the SKU details
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public SKUDetailVO getSKUDetails(final String siteId, final String skuId)
                    throws BBBSystemException, BBBBusinessException {
    	boolean sameDayDeliveryFlag = false;
        boolean currentZipEligibility = false;
        String regionPromoAttr = BBBCoreConstants.BLANK;
        this.logDebug("Catalog API Method Name [getSKUDetails] siteId[" + siteId + "] pSkuId[" + skuId + "]");
        if ((siteId == null) || StringUtils.isEmpty(siteId)) {
            throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                            BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
        }
        final String isSDDON = getAllValuesForKey(BBBCmsConstants.SDD_KEY , BBBCmsConstants.SDD_SHOW_ATTRI).get(0);

        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetails");
            final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId,
                            BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
            if (skuRepositoryItem == null) {
                this.logTrace("Repository Item is null for skuId " + skuId);
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetails");
                throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
            }

            this.logDebug("Catalog API Method Name [getSKUDetailVO] for site " + siteId);
            final SKUDetailVO sKUDetailVO = new SKUDetailVO(skuRepositoryItem);
            sKUDetailVO.setSkuId(skuId);
            sKUDetailVO.setWebOfferedFlag(true);
            sKUDetailVO.setActiveFlag(true);
            sKUDetailVO.setDisableFlag(false);
            sKUDetailVO.setAssemblyOffered(false);
            
            String isIntlRestricted = BBBCoreConstants.NO_CHAR;
            if (!BBBUtility.isEmpty((String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED))) {
            	isIntlRestricted  = (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED);
			}
            sKUDetailVO.setIntlRestricted(BBBCoreConstants.YES_CHAR.equalsIgnoreCase(isIntlRestricted));
            if (skuRepositoryItem.getPropertyValue(ON_SALE) != null) {
                sKUDetailVO.setOnSale(((Boolean) skuRepositoryItem.getPropertyValue(ON_SALE)).booleanValue());
            }
            String sddEligibleOn = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG);
            
    		if(null != sddEligibleOn){
    			sameDayDeliveryFlag = Boolean.valueOf(sddEligibleOn);
    		}
    		if(sameDayDeliveryFlag) {
    			BBBSessionBean sessionBean = (BBBSessionBean) resolveComponentFromRequest(BBBCoreConstants.SESSION_BEAN);
            	if(null!=sessionBean && null!=sessionBean.getCurrentZipcodeVO()) {
            			currentZipEligibility = sessionBean.getCurrentZipcodeVO().isSddEligibility();
            			regionPromoAttr =sessionBean.getCurrentZipcodeVO().getPromoAttId();
              	}
    		}
            
        	
            Map<String, RepositoryItem> attributeNameRepoItemMap = new HashMap<String, RepositoryItem>();
            attributeNameRepoItemMap = this.getSkuAttributeList(skuRepositoryItem, siteId, attributeNameRepoItemMap, regionPromoAttr, currentZipEligibility);
            if (!BBBUtility.isMapNullOrEmpty(attributeNameRepoItemMap)) {
                try {
                    boolean rebateFlag = false;
                    final List<String> rebateAttributeKeyList = this.getAllValuesForKey(
                                    BBBCmsConstants.CONTENT_CATALOG_KEYS, this.getRebateKey());
                    if ((rebateAttributeKeyList != null) && !rebateAttributeKeyList.isEmpty()) {
                        final String rebateKeys[] = rebateAttributeKeyList.get(0).split(BBBCoreConstants.COMMA);
                        for (final String itemRebateKey : rebateKeys) {
                            if (attributeNameRepoItemMap.containsKey(itemRebateKey)) {
                                rebateFlag = true;
                                break;
                            }
                        }
                        if (rebateFlag) {
                            this.logTrace("Sku has Rebate Attribute setting eligible rebates ");
                            sKUDetailVO.setHasRebate(true);
                            sKUDetailVO.setEligibleRebates(this.updateRebatesForSku(skuRepositoryItem, siteId));
                        } else {
                            this.logTrace("Sku does not have Rebate Attribute Not setting eligible rebates ");
                            sKUDetailVO.setHasRebate(false);
                        }
                    }
                    
                    // hasSDDAttribs
                    boolean sddAttribs = false;
                    final List<String> sddAttributeKeyList = this.getAllValuesForKey(
                    		BBBCmsConstants.SDD_KEY, BBBCmsConstants.SDD_ATTRIBUTE_LIST);
        			if (!BBBUtility.isListEmpty(sddAttributeKeyList)) {
                        final String sddAtrribsKeys[] = sddAttributeKeyList.get(0).split(BBBCoreConstants.COMMA);
                        for (String itemSddKey : sddAtrribsKeys) {
                        	itemSddKey=itemSddKey.trim();
                            if (attributeNameRepoItemMap.containsKey(itemSddKey)) {
                            	sddAttribs = true;
                            	if(isSDDON.equalsIgnoreCase(BBBCoreConstants.FALSE)){
                            		attributeNameRepoItemMap.remove(itemSddKey);
                            	}
                            }
                        }
                        if (sddAttribs) {
                            this.logTrace("Sku has sddAttribs Attribute setting eligible SDD ");
                            sKUDetailVO.setHasSddAttribute(true);
                        } else {
                            this.logTrace("Sku has sddAttribs Attribute setting not eligible SDD ");
                            sKUDetailVO.setHasSddAttribute(false);
                        }
                    }
                } catch (final BBBBusinessException e) {
                    this.logError("Business Exeption caught for rebate config key", e);
                } catch (final BBBSystemException e) {
                    this.logError("System Exeption caught for rebate config key", e);
                }
                sKUDetailVO.setFreeShipMethods(this.updateFreeShipMethodForSku(attributeNameRepoItemMap.keySet()));
                // sku attribute map in ProductVO
                final Map<String, List<AttributeVO>> skuAttributesMap = this
                                .getSkuAttributeMap(attributeNameRepoItemMap);
                if (skuAttributesMap != null) {
                    this.logTrace("Setting attribute map for sku");
                    sKUDetailVO.setSkuAttributes(skuAttributesMap);
                }
            }
            if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) != null) {
            	sKUDetailVO.setLtlItem(((Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU)).booleanValue());
            }
            if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_ASSEMBLY_FEE_OFFERED) != null) {
            	sKUDetailVO.setAssemblyOffered((Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_ASSEMBLY_FEE_OFFERED));
    		}
            List<String> isCustomizable = this.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.IS_CUSTOMIZABLE_ON);
            if(isCustomizable.get(0).equalsIgnoreCase(BBBCoreConstants.TRUE)){
            	sKUDetailVO.setCustomizationOffered(this.isCustomizationOfferedForSKU(skuRepositoryItem,siteId));
            	sKUDetailVO.setCustomizableRequired(this.isCustomizationRequiredForSKU(skuRepositoryItem,siteId));
            }else{
            	sKUDetailVO.setCustomizationOffered(false);
            	sKUDetailVO.setCustomizableRequired(false);
            }
			/** BPSI-3285 DSK | Handle Pricing message for Personalized Item*/
            if (skuRepositoryItem.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) != null) {
            	sKUDetailVO.setPersonalizationType(((String) skuRepositoryItem.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE)));
            }
            if (isLoggingTrace()) {
            	 this.logTrace(sKUDetailVO.toString());
			}
            String shipMsgDisplayFlag = BBBCoreConstants.FALSE;
    		try {
    			shipMsgDisplayFlag = getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG).get(0);
    		} catch (BBBSystemException | BBBBusinessException e) {
    			logError("Error while getting config key ShipMsgDisplayFlag value", e);
    		}
    		if(Boolean.parseBoolean(shipMsgDisplayFlag)){
    			updateShippingMessageFlag(sKUDetailVO, false, 0.0);
    		}
    		
            return sKUDetailVO;
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getSKUDetails]: RepositoryException for sku Id " + skuId);
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetails");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);

        } finally {
        	this.logDebug("Catalog API Method Name [getSKUDetails] siteId[" + siteId + "] pSkuId[" + skuId + "] Exit");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetails");
        }

    }
	
	// Start : R 2.2 Product Image SiteMap Generation 504-b 
	/* (non-Javadoc)
	 * @see com.bbb.commerce.catalog.BBBCatalogTools#getSKUDetails(java.lang.String, boolean, java.lang.String)
	 */
	@Override
	public  SKUDetailVO getSKUDetails(String siteId, boolean checkSKUActiveFlag, String skuId) throws BBBSystemException,
			BBBBusinessException {
		logDebug("Catalog API Method Name [getSKUDetails] for store sku siteId["+siteId+"] pSkuId["+skuId+"]");
		if(siteId==null || StringUtils.isEmpty(siteId)){
			throw new BBBBusinessException (BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
		}
		try {
			BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL+" getSKUDetailsForStore");
			final RepositoryItem skuRepositoryItem = getCatalogRepository().getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
			if(skuRepositoryItem==null){
				logTrace("Repository Item is null for sku id "+skuId);
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL+" getSKUDetailsForStore");
				throw new BBBBusinessException (BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
			}
			return getMinimalSku(skuRepositoryItem);
		} catch (RepositoryException e) {
			logError("Catalog API Method Name [getSKUDetails]: RepositoryException "+ BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION);
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL+" getSKUDetailsForStore");
			throw new BBBSystemException (BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,e);

		}
		finally{
			logDebug("Catalog API Method Name [getSKUDetails] for store sku siteId["+siteId+"] pSkuId["+skuId+"] Exit");
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL+" getSKUDetailsForStore");
		}

	}
	// End : R 2.2 Product Image SiteMap Generation 504-b
	/* (non-Javadoc)
	 * @see com.bbb.commerce.catalog.BBBCatalogTools#getRecommendedSKU(java.lang.String, com.bbb.ecommerce.order.BBBOrderImpl)
	 */
	public RecommendedSkuVO  getRecommendedSKU(String siteId, BBBOrderImpl order) {
		this.logDebug("Catalog API Method Name [getRecommendedSKU] for siteId ["+ siteId +"] order ["+ order.getOnlineOrderNumber() +"] Entry");
		List<RepositoryItem> recommItems = getRecommendedSKUs(siteId, order);
		RecommendedSkuVO recommSkuVO = null;
		if(!BBBUtility.isListEmpty(recommItems)) {
			recommSkuVO = new RecommendedSkuVO();
			try {
				String skuId = ((RepositoryItem)recommItems.get(0).getPropertyValue(BBBCatalogConstants.PROPERTY_NAME_RECOMM_SKU)).getRepositoryId();
				String parentProductId = getParentProductForSku(skuId);
				SKUDetailVO skuDetailVO = getSKUDetails(siteId,  skuId, false);
				skuDetailVO.setParentProdId( parentProductId);
				recommSkuVO.setRecommSKUVO(skuDetailVO);
				recommSkuVO.setSalePrice(getSalePrice(parentProductId, skuId));
				recommSkuVO.setListPrice(getListPrice(parentProductId, skuId));
				recommSkuVO.setComment((String)recommItems.get(0).getPropertyValue(BBBCatalogConstants.PROPERTY_NAME_DESCRIPTION));
				recommSkuVO.setCartSkuId(recommItems.get(0).getRepositoryId());
				return recommSkuVO;
			} catch (BBBSystemException e) {
				logError(e.getMessage(),e);
			} catch (BBBBusinessException e) {
				logError(e.getMessage(),e);
			}
		}
		this.logDebug("Catalog API Method Name [getRecommendedSKU] for siteId ["+ siteId +"] order ["+ order.getOnlineOrderNumber() +"] Exit");
		return null;
	}
	
	/**
	 * Gets the recommended sk us.
	 *
	 * @param siteId the site id
	 * @param order the order
	 * @return the recommended sk us
	 */
	private List<RepositoryItem>  getRecommendedSKUs(String siteId, BBBOrderImpl order) {
		this.logDebug("Catalog API Method Name [getRecommendedSKUs] for siteId ["+ siteId +"] order ["+ order.getOnlineOrderNumber() +"] Entry");
		String skuIdString = EMPTYSTRING;
        BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getRecommendedSKUs");
        List<String> skuList = order.getOrderSkus();
		for(String skuId: skuList) {
			skuIdString = skuIdString+skuId+"\",\"";
		}
		if(StringUtils.isEmpty(skuIdString)) {
			return null;
		}
		skuIdString = "\""+skuIdString.substring(0, skuIdString.length()-2);
		logTrace("Order SkuList "+skuIdString);
		try {
			
	        final RepositoryView repositoryView = this.getRecommendedSkuRepository().getView(
	                BBBCatalogConstants.ITEM_DESCRIPTOR_RECOMMENDED_SKUS);
	        RepositoryItem[] items = null;
	        String rqlQuery = "ID IN {"+skuIdString+"}";
	        RqlStatement statement = getRqlQueryStatement(rqlQuery);
		
			try {
				items = statement.executeQuery(repositoryView, new Object[] {});

			} catch (IllegalArgumentException iLLArgExp) {
				logDebug("Catalog API Method Name [getRecommendedSKUs]: RepositoryException "+ BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION + iLLArgExp);
			}
			return filterRecommendedItems(items, siteId, order);
		} catch (RepositoryException e) {
			logError("Catalog API Method Name [getRecommendedSKUs]: RepositoryException "+ BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION + e);
		}
		finally{
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL+" getRecommendedSKUs");
		}		
		this.logDebug("Catalog API Method Name [getRecommendedSKUs] for siteId ["+ siteId +"] order ["+ order.getOnlineOrderNumber() +"] Exit");
		return null;
	}

	/**
	 * @param rqlQuery
	 * @return
	 * @throws RepositoryException
	 */
	protected RqlStatement getRqlQueryStatement(String rqlQuery)
			throws RepositoryException {
		return RqlStatement.parseRqlStatement(rqlQuery);
	}

    /**
     * Filter recommended items.
     *
     * @param items the items
     * @param siteId the site id
     * @param order the order
     * @return the list
     */
    private List<RepositoryItem> filterRecommendedItems(RepositoryItem[] items, String siteId, BBBOrderImpl order) {
    	this.logDebug("Catalog API Method Name [filterRecommendedItems] for siteId ["+ siteId +"] order ["+ order.getOnlineOrderNumber() +"] Entry");
		List<RepositoryItem> filteredRecommItem = new ArrayList<RepositoryItem>();
    	if(items != null) {
    		
    		for(RepositoryItem item: items) {
    			RepositoryItem recommSku = (RepositoryItem)item.getPropertyValue(BBBCatalogConstants.PROPERTY_NAME_RECOMM_SKU);
    			if(isSkuActive(recommSku)
    				&&!order.getOrderSkus().contains(recommSku.getRepositoryId())
    				&& validateInventoryRule(recommSku, siteId)) {
    				filteredRecommItem.add(item);
    			}
    		}
    		if(filteredRecommItem.size()>1) {
    			Collections.sort(filteredRecommItem, new RepositoryPriorityComparator());
    		}
			logTrace("Recommended Items List: "+filteredRecommItem);
    		return filteredRecommItem;
    	}
    	this.logDebug("Catalog API Method Name [filterRecommendedItems] for siteId ["+ siteId +"] order ["+ order.getOnlineOrderNumber() +"] Exit");
		return null;
	}	
	
	/**
	 * Validate inventory rule.
	 *
	 * @param recommSku the recomm sku
	 * @param siteId the site id
	 * @return true, if successful
	 */
	private boolean validateInventoryRule(RepositoryItem recommSku, String siteId) {
		this.logDebug("Catalog API Method Name [filterRecommendedItems] for siteId ["+ siteId +"] recommSku ["+ recommSku +"] Entry");
		int recommItemStock;
		try {
			recommItemStock = getBbbInventoryManager().getProductAvailability( siteId, recommSku.getRepositoryId(),BBBInventoryManager.PRODUCT_DISPLAY,0);
		} catch (BBBBusinessException e) {
			logError("SystemException while fetching stock status for skuid : " + recommSku.getRepositoryId() + "from getCompareProductDetail() method", e);
			return false;
		} catch (BBBSystemException e) {
			logError("SystemException while fetching stock status for skuid : " + recommSku.getRepositoryId() + "from getCompareProductDetail() method", e);
			return false;
		}
		if(BBBInventoryManager.NOT_AVAILABLE == recommItemStock) {
			logTrace("Recommended Items Inventory validation dropped the sku with ID: "+recommSku.getRepositoryId());
			return false;
		}
		this.logDebug("Catalog API Method Name [filterRecommendedItems] for siteId ["+ siteId +"] recommSku ["+ recommSku +"] Entry");
		return true;
	}

	/**
	 * Get Minimal SKU details.
	 *
	 * @param skuId the sku id
	 * @return the minimal sku
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	@Override
	public  SKUDetailVO getMinimalSku(String skuId) throws BBBSystemException,
			BBBBusinessException {
		logDebug("Catalog API Method Name [getMinimalSku] for store sku  ["+skuId+"]");
		if(StringUtils.isEmpty(skuId)){
			throw new BBBBusinessException (BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
		}
		try {
			BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL+" getSKUDetailsForStore");
			final RepositoryItem skuRepositoryItem = getCatalogRepository().getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
			if(skuRepositoryItem==null){
				logTrace("Repository Item is null for sku id "+skuId);
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL+" getSKUDetailsForStore");
				throw new BBBBusinessException (BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
			}
			return getMinimalSku(skuRepositoryItem);
		} catch (RepositoryException e) {
			logError("Catalog API Method Name [getMinimalSku]: RepositoryException "+ BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION);
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL+" getSKUDetailsForStore");
			throw new BBBSystemException (BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,e);

		}
		finally{
			logDebug("Catalog API Method Name [getMinimalSku] for store sku  ["+skuId+"] Exit");
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL+" getSKUDetailsForStore");
		}

	}
	
	/**
	 *  if ((for the given site packNHoldEnabled) and (date is within start/end date)) then return true, else false.
	 *
	 * @param siteId the site id
	 * @param date the date
	 * @return is Pack & Hold Window
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */

    @Override
    public final boolean isPackNHoldWindow(final String siteId, final Date date)
                    throws BBBSystemException, BBBBusinessException {
    	//TODO - use this code post-Iliad-Live return getSiteRepositoryTools().isPackNHoldWindow(siteId, date);
    	return getSiteRepositoryTools().isPackNHoldWindow(siteId, date);
    }

    /**
     *  The API returns thresholdVO corresponding to given site and sku id Query the threshold item-descriptor on basis
     * of input sku id and site id. if the repository item returned is null i.e. no row exists in database corresponding
     * to given siteid ans skuid get the sku repository item and fetch properties jdsdept,jdssubdept and jdaclass.If sku
     * repository item is null throw BBBBusinessException.Now again query threshold item descriptor with the values of
     * jdsdept,jdssubdept and jdaclass fetched from sku.if the return repository-item is not null fetch thresholdLimited
     * and thresholdAvailable properties and set in ThresholdVO.
     *
     * @param siteId the site id
     * @param skuId the sku id
     * @return ThresholdVO
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */

    @Override
    public  ThresholdVO getSkuThreshold(final String siteId, final String skuId)
                    throws BBBSystemException, BBBBusinessException {
    	String threshHoldFlag = "false";
    	threshHoldFlag = this.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.EOM_THRESHOLD_FLAG).get(0);
        this.logDebug("Catalog API Method Name [getSkuThreshold] siteId [  " + siteId + "] skuId " + skuId);
        if ((skuId != null) && (siteId != null) && !StringUtils.isEmpty(skuId) && !StringUtils.isEmpty(siteId) && threshHoldFlag.equalsIgnoreCase("true")) {
            ThresholdVO thresholdVO = null;
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getSkuThreshold");

                RepositoryItem[] thresholdRepositoryItem;
                Object[] params = new Object[2];
                params[0] = skuId;
                params[1] = siteId;

                thresholdRepositoryItem = this.executeRQLQuery(this.getThresholdQuery(), params,
                                BBBCatalogConstants.SKU_THRESHOLD_ITEM_DESCRIPTOR, this.getCatalogRepository());

                if ((thresholdRepositoryItem == null) || (thresholdRepositoryItem.length <= 0)) {
                    this.logTrace("threshold RepositoryItem value is  null or empty SkuId is not present in theshold Repository "
                                    + skuId);
                    this.logTrace("SkuId " + skuId + " is not present in threshold table");

                    // get sku repositoryitem to fetch jda related properties
                    final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId,
                                    BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
                    if (skuRepositoryItem == null) {
                        this.logTrace("Repository Item is null for SKuId " + skuId);
                        throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
                                        BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
                    }
                    String jdaDept = EMPTYSTRING;
                    String jdaSubDept = EMPTYSTRING;
                    String jdaClass = EMPTYSTRING;
                    final StringBuilder jdaQuery = new StringBuilder(20);

                    if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) != null) {
                        jdaDept = ((RepositoryItem) skuRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME))
                                        .getRepositoryId();
                        jdaQuery.append("jdaDeptId = ?0  and ");
                    } else {

                        jdaQuery.append("jdaDeptId is null and  ");
                    }

                    if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) != null) {
                        jdaSubDept = ((RepositoryItem) skuRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME))
                                        .getRepositoryId();
                        if (jdaSubDept.indexOf('_') > 0) {
                            jdaSubDept = jdaSubDept.substring(jdaSubDept.indexOf('_') + 1);
                        }
                        jdaQuery.append(" jdaSubDeptId = ?1 and ");
                    } else {
                        jdaQuery.append(" jdaSubDeptId is null and ");
                    }

                    if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME) != null) {
                        jdaClass = (String) skuRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME);
                        jdaQuery.append(" jdaClass = ?2 ");
                    } else {
                        jdaQuery.append(" jdaClass is null ");
                    }
                    this.logTrace("jdaQuery [" + jdaQuery + "]");
                    params = new Object[3];
                    params[0] = jdaDept;
                    params[1] = jdaSubDept;
                    params[2] = jdaClass;

                    this.logTrace("query to be executed for sku threshold item descriptor " + jdaQuery);
                    this.logTrace("jdaDept value [" + jdaDept + "] jdaSubDept value [" + jdaSubDept
                                    + "] jdaClass value [" + jdaClass + "]");
                    // again query threshold item descriptor based on jda related params
                    thresholdRepositoryItem = this.executeRQLQuery(jdaQuery.toString(), params,
                                    BBBCatalogConstants.SKU_THRESHOLD_ITEM_DESCRIPTOR, this.getCatalogRepository());

                    if ((thresholdRepositoryItem != null) && (thresholdRepositoryItem.length > 0)) {
                        thresholdVO = this.getThresholdVO(thresholdRepositoryItem[0]);
                    }
                } else {
                    this.logTrace("SkuId " + skuId + " is present in threshold table");
                    thresholdVO = this.getThresholdVO(thresholdRepositoryItem[0]);
                }
            } catch (final RepositoryException e) {

                this.logError("Catalog API Method Name [getSkuThreshold]: RepositoryException for skuId " + skuId);
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSkuThreshold");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);

            } finally {
            	this.logDebug("Catalog API Method Name [getSkuThreshold] siteId [  " + siteId + "] skuId " + skuId + " Exit");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSkuThreshold");
            }
            return thresholdVO;
        }

        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }

    /**
     * Gets the threshold vo.
     *
     * @param thresholdRepositoryItem the threshold repository item
     * @return the threshold vo
     */
    protected ThresholdVO getThresholdVO(final RepositoryItem thresholdRepositoryItem) {
        final ThresholdVO thresholdVO = new ThresholdVO();
        if (thresholdRepositoryItem.getPropertyValue(BBBCatalogConstants.THRESHOLD_AVAILABLE_THRESHOLD_PROPERTY_NAME) != null) {
            this.logTrace("Threshold availablility value "
                            + thresholdRepositoryItem
                                            .getPropertyValue(BBBCatalogConstants.THRESHOLD_AVAILABLE_THRESHOLD_PROPERTY_NAME));
            thresholdVO.setThresholdAvailable(((Long) thresholdRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.THRESHOLD_AVAILABLE_THRESHOLD_PROPERTY_NAME))
                            .longValue());
        }

        if (thresholdRepositoryItem.getPropertyValue(BBBCatalogConstants.THRESHOLD_LIMITED_THRESHOLD_PROPERTY_NAME) != null) {
            this.logTrace("Threshold Limited value "
                            + thresholdRepositoryItem
                                            .getPropertyValue(BBBCatalogConstants.THRESHOLD_LIMITED_THRESHOLD_PROPERTY_NAME));
            thresholdVO.setThresholdLimited(((Long) thresholdRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.THRESHOLD_LIMITED_THRESHOLD_PROPERTY_NAME))
                            .longValue());
        }
        return thresholdVO;
    }

    /**
     *  The method executes the business logic to determine if a sku is below line or not.
     *
     * @param siteId the site id
     * @param skuId the sku id
     * @return true, if is SKU below line
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */

    @Override
    public boolean isSKUBelowLine(final String siteId, final String skuId)
                    throws BBBSystemException, BBBBusinessException {

        this.logDebug("Catalog API Method Name [isSKUBelowLine] siteId [ " + siteId + "] skuId " + skuId);
        RepositoryItem skuRepositoryItem = null;

        boolean isForceBelowLineSKu = false;
        BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " isSKUBelowLine");
        try {
            skuRepositoryItem = this.getCatalogRepository().getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
            if (skuRepositoryItem == null) {
                this.logTrace("Repository Item is null for skuId " + skuId);
                throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
            }
            if ((skuRepositoryItem.getPropertyValue(BBBCatalogConstants.FORCE_BELOW_LINE_SKU_PROPERTY_NAME) != null)
                            && ((Boolean) skuRepositoryItem
                                            .getPropertyValue(BBBCatalogConstants.FORCE_BELOW_LINE_SKU_PROPERTY_NAME))
                                            .booleanValue()) {
                isForceBelowLineSKu = ((Boolean) skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.FORCE_BELOW_LINE_SKU_PROPERTY_NAME))
                                .booleanValue();
               
                    final StringBuilder logDebug = new StringBuilder();
                    logDebug.append("Value of isForceBelowLineSKu for skuId ").append(skuId).append(" is: ")
                                    .append(isForceBelowLineSKu);
                    this.logTrace(logDebug.toString());
                
                return isForceBelowLineSKu;
            }

            this.logTrace("Value of isForceBelowLineSKu is null for skuId " + skuId);
            final long altfs = this.getInventoryManager().getAltAfs(skuId, siteId).longValue();
            final long afs = this.getInventoryManager().getAfs(skuId, siteId).longValue();
            final long igr = this.getInventoryManager().getIgr(skuId, siteId).longValue();
            this.logTrace("Value of altfs [" + altfs + "]afs[ " + afs + "] igr [" + igr + "]");
            String ecomFullfillment = null;
            // if site is bbb.ca
            if ("BedBathCanada".equalsIgnoreCase(siteId)) {
                this.logTrace("site is bedbath Canada so getting value for ecom flag in sku ECOM FLAG VALUE ["
                                + skuRepositoryItem
                                                .getPropertyValue(BBBCatalogConstants.ECOM_FULFILLMENT_SKU_PROPERTY_NAME)
                                + "]");
                if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.ECOM_FULFILLMENT_SKU_PROPERTY_NAME) != null) {
                    ecomFullfillment = (String) skuRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.ECOM_FULFILLMENT_SKU_PROPERTY_NAME);
                }
                if ((ecomFullfillment != null) && "E".equalsIgnoreCase(ecomFullfillment.trim())) {

                    if ((altfs + afs) > 0 || igr > 0) {
                        return false;
                    }
                    return true;
                }

                if (altfs > 0 || igr > 0) {
                    return false;
                }
                return true;
            }

            // if site is other than bbb.ca
            if ((altfs + afs) > 0 || igr > 0) {
                return false;
            }
            return true;
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [isSKUBelowLine]: RepositoryException ");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " isSKUBelowLine");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
        	this.logDebug("Catalog API Method Name [isSKUBelowLine] siteId [ " + siteId + "] skuId " + skuId + " Exit");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " isSKUBelowLine");
        }

    }

 	/**
	  *  The method executes the business logic to determine if Customization is Required For sku or not.
	  *
	  * @param skuRepositoryItem the sku repository item
	  * @param siteId the site id
	  * @return true, if is customization required for sku
	  */
    @Override
    public final boolean isCustomizationRequiredForSKU(final RepositoryItem skuRepositoryItem , final String siteId) {
        boolean isCustomizationRequired = false;
        boolean isCustomizationOffered = this.isCustomizationOfferedForSKU(skuRepositoryItem, siteId);
        String personalizationType = (String) skuRepositoryItem.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE);
        String customizationCodes = (String) skuRepositoryItem.getPropertyValue(BBBCoreConstants.ELIGIBLE_CUSTOMIZATION_CODES);
        if( isCustomizationOffered && !BBBUtility.isEmpty(customizationCodes)
        		&& !BBBUtility.isEmpty(personalizationType) &&
        		( BBBCoreConstants.PERSONALIZATION_CODE_CR.equalsIgnoreCase(personalizationType)
        				|| BBBCoreConstants.PERSONALIZATION_CODE_PB.equalsIgnoreCase(personalizationType) ) ) {
        	isCustomizationRequired = true;
        }
        this.logDebug("Catalog API Method Name [isCustomizationRequiredForSKU] siteId [ " + siteId
        		+ "] skuId " + skuRepositoryItem.getRepositoryId() + "] isCustomizationRequired [ " + isCustomizationRequired);
        return isCustomizationRequired;
    }
    
    /**
   	 *  The method executes the business logic to determine if Customization is Offered For sku on this site or not.
   	 *
   	 * @param skuRepositoryItem the sku repository item
   	 * @param siteId the site id
   	 * @return true, if is customization offered for sku
   	 */
    @Override
   	 public boolean isCustomizationOfferedForSKU(final RepositoryItem skuRepositoryItem , final String siteId) {
   	    
    	return getGlobalRepositoryTools().isCustomizationOfferedForSKU(skuRepositoryItem, siteId);
    	
   	 }
    
    /**
     *  If the SKU does not exist in the system then the method will throw BBBusinessException with an error code
     * indicating that SKU does not exist If SKU is eligible to be shipped to all states the list returned will be null
     * If the SKU attribute indicates that a SKU cannot be shipped to certain states, the State details for them are
     * looked up from the Repository Item Cache and sent in the response.
     *
     * @param pSiteId the site id
     * @param pSkuId the sku id
     * @return the non shippable states for sku
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public List<StateVO> getNonShippableStatesForSku(final String pSiteId, final String pSkuId)
                    throws BBBSystemException, BBBBusinessException {
        
        this.logDebug("Catalog API Method Name [getNonShippableStatesForSku] siteId [  " + pSiteId + "] pSkuId["
                        + pSkuId + "]");
        if ((pSiteId != null) && !StringUtils.isEmpty(pSiteId)) {
        	BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getNonShippableStatesForSku");
            final List<StateVO> stateVOList = new ArrayList<StateVO>();
            try {
                final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(pSkuId,
                                BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
                if (skuRepositoryItem == null) {
                    throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
                                    BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
                }
                final boolean isActive = this.isSkuActive(skuRepositoryItem);
                if (isActive) {
                    @SuppressWarnings ("unchecked")
                    final Set<RepositoryItem> nonShippableStatesRepoItemSet = (Set<RepositoryItem>) skuRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.NON_SHIPPABLE_STATES_SHIPPING_PROPERTY_NAME);

                    if ((nonShippableStatesRepoItemSet == null) || nonShippableStatesRepoItemSet.isEmpty()) {
                        this.logTrace("No non shippable states configured for sku id [" + pSkuId + "]");
                        return null;
                    }
                    StateVO stateVO = null;
                    for (final RepositoryItem nonShippableStatesRepositoryItem : nonShippableStatesRepoItemSet) {
                        stateVO = this.getStateVO(pSiteId, nonShippableStatesRepositoryItem);
                     
                            final StringBuilder logDebugMessage = new StringBuilder();
                            logDebugMessage.append("adding state [").append(stateVO.getStateName())
                                            .append("] as non shippable for skuid [").append(pSkuId).append(']');
                            this.logTrace(logDebugMessage.toString());
                        
                        stateVOList.add(stateVO);
                    }
                    BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getNonShippableStatesForSku");
                    return stateVOList;
                }
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getNonShippableStatesForSku");
                this.logTrace("SKU[" + pSkuId + "] is disabled or inactive");
                throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY,
                                BBBCatalogErrorCodes.SKU_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY);
            } catch (final RepositoryException e) {
                this.logError("Catalog API Method Name [getNonShippableStatesForSku]: RepositoryException ");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getNonShippableStatesForSku");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
            } finally {
            	this.logDebug("Catalog API Method Name [getNonShippableStatesForSku] siteId [  " + pSiteId + "] pSkuId["
                        + pSkuId + "] Exit");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getNonShippableStatesForSku");
            }
        }
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }

    /**
     * Validate prod attribute.
     *
     * @param siteId the site id
     * @param product the product
     * @param attributeId the attribute id
     * @return true, if successful
     */
    private boolean validateProdAttribute(final String siteId, final RepositoryItem product, final String attributeId) {

     
            final StringBuilder debug = new StringBuilder(50);
            if(product != null){
            debug.append("Catalog API Method Name [validateProdAttribute]siteId [").append(siteId)
                            .append("] productId [  ").append(product.getRepositoryId()).append(" ] AttributeId[")
                            .append(attributeId).append(']');
            }
            	
            this.logDebug(debug.toString());


        if (StringUtils.isEmpty(attributeId) || StringUtils.isEmpty(siteId) || (product == null)) {
            return false;
        }
        Map<String, RepositoryItem> attributeMap = new HashMap<String, RepositoryItem>();
        attributeMap = this.getProductAttributeList(product, siteId, attributeMap);
        if ((attributeMap != null) && !attributeMap.isEmpty()) {
            final Set<String> applicableAttrIds = attributeMap.keySet();
            if (applicableAttrIds.contains(attributeId)) {
                this.logTrace("Attribute id [ " + attributeId + " ] is  configured for product Id:: "
                                + product.getRepositoryId() + "and siteId:::" + siteId);
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#isClearanceProduct(java.lang.String, atg.repository.RepositoryItem)
     */
    @Override
    public final boolean isClearanceProduct(final String siteId, final RepositoryItem product) {
        return this.validateProdAttribute(siteId, product, this.getClearenceAttrID());
    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#isBeyondValueProduct(java.lang.String, atg.repository.RepositoryItem)
     */
    @Override
    public final boolean isBeyondValueProduct(final String siteId, final RepositoryItem product) {
        return this.validateProdAttribute(siteId, product, this.getBeyondValueAttrID());
    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#isSpecialPurchaseProduct(java.lang.String, atg.repository.RepositoryItem)
     */
    @Override
    public final boolean isSpecialPurchaseProduct(final String siteId, final RepositoryItem product) {
        return this.validateProdAttribute(siteId, product, this.getSpecialPurchaseAttrID());
    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#isFreeShipping(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean isFreeShipping(final String siteId, final String skuId, final String shippingMethodId)
                    throws BBBBusinessException, BBBSystemException {
        
            final StringBuilder debug = new StringBuilder(50);
            debug.append("Catalog API Method Name [isFreeShipping]siteId [").append(siteId).append("] skuId [  ")
                            .append(skuId).append(" ] ShippingMethodId[").append(shippingMethodId).append(']');
            this.logDebug(debug.toString());


        if (!StringUtils.isEmpty(shippingMethodId) && !StringUtils.isEmpty(siteId) && !StringUtils.isEmpty(skuId)) {
            final String freeShipAttr = this.shipGrpFreeShipAttrMap.get(shippingMethodId.toLowerCase());
            this.logTrace("Free shipping attribute corresponding to shipping method is " + shippingMethodId + " is "
                            + freeShipAttr);
            try {
                if (!StringUtils.isEmpty(freeShipAttr)) {
                    final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId,
                                    BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
                    if (skuRepositoryItem != null) {
                        Map<String, RepositoryItem> attributeMap = new HashMap<String, RepositoryItem>();
                        attributeMap = this.getSkuAttributeList(skuRepositoryItem, siteId, attributeMap, BBBCoreConstants.BLANK, false);
                        if (!BBBUtility.isMapNullOrEmpty(attributeMap)) {
                            final Set<String> applicableAttrIds = attributeMap.keySet();
                            if (applicableAttrIds.contains(freeShipAttr)) {
                                this.logTrace("Shipping method id [ " + shippingMethodId + " ] is  free for sku Id:: "
                                                + skuId + "and siteId:::" + siteId);
                                return true;
                            }
                        }
                    } else {
                        this.logTrace("SkuId is not present in the repository skuId:::" + skuId);
                        throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
                                        BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
                    }
                } else {
                    this.logTrace("No Free ship attr for the shipping id " + shippingMethodId);
                }
            } catch (final RepositoryException e) {
                this.logError("Catalog API Method Name [isFreeShipping]: RepositoryException ");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
            }
        } else {
            this.logTrace("input parameter is null siteId:" + siteId + " shippingMethodId: " + shippingMethodId
                            + "skuId: " + skuId);
            throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                            BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
        }
        this.logTrace("Shipping method id [" + shippingMethodId + " is not  free for sku Id::" + skuId
                        + "and siteId:::" + siteId);
        return false;
    }

    
    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#hasSDDAttribute(java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public final boolean hasSDDAttribute(final String siteId, final RepositoryItem repositoryItem, final String regionPromoAttr, final boolean isZipSDDEligibile)
                    throws BBBBusinessException, BBBSystemException {
        
            final StringBuilder debug = new StringBuilder(50);
            debug.append("Catalog API Method Name [isSDDAttribute]siteId [").append(siteId).append("] skuId [  ")
                            .append(repositoryItem).append(" ] regionPromoAttr[").append(regionPromoAttr).append("] isZipSDDEligibile [").append(isZipSDDEligibile);
            this.logDebug(debug.toString());


		if (!StringUtils.isEmpty(siteId)) {
			if (repositoryItem != null) {
				try {
					Map<String, RepositoryItem> attributeMap = new HashMap<>();
					if (BBBCatalogConstants.SKU_ITEM_DESCRIPTOR
							.equals(repositoryItem.getItemDescriptor().getItemDescriptorName())) {
						attributeMap = this.getSkuAttributeList(repositoryItem, siteId, attributeMap, regionPromoAttr,
								isZipSDDEligibile);
					} else if (BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR
							.equals(repositoryItem.getItemDescriptor().getItemDescriptorName())) {
						attributeMap = this.getProductAttributeList(repositoryItem, siteId, attributeMap);
					}
					if (!BBBUtility.isMapNullOrEmpty(attributeMap)) {
						final List<String> sddAttributeKeyList = this.getAllValuesForKey(BBBCmsConstants.SDD_KEY,
								BBBCmsConstants.SDD_ATTRIBUTE_LIST);
						if ((sddAttributeKeyList != null) && !sddAttributeKeyList.isEmpty()) {
							final String sddAtrribsKeys[] = sddAttributeKeyList.get(0).split(BBBCoreConstants.COMMA);
							for (final String itemSddKey : sddAtrribsKeys) {
								if (attributeMap.containsKey(itemSddKey.trim())) {
									this.logTrace("SDD attribute present for sku:::" + repositoryItem);
									return true;
								}
							}

						}
					}
				} catch (final RepositoryException e) {
					this.logError("Catalog API Method Name [isSDDAttribute]: RepositoryException ");
					throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
							BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
				}
			} else {
				this.logTrace("SkuId is not present in the repository skuId:::" + repositoryItem);
				throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
						BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
			}
		} else {
            this.logTrace("input parameter is null siteId:" + siteId + "skuId: " + repositoryItem);
            throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                            BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
        }
        this.logTrace("SDD attribute NOT present for sku:::" + repositoryItem);
        return false;
    }

    /**
     *  If the ShippingMethodId does not exist in the system the method will throw a BBBBusinessException with an error
     * code indicating that the Shipping Method does not exist If the shipping method exists but not applicable for the
     * given state and site combination, method will throw a BBBBusinessException with an error code indicating that the
     * Shipping Method is not applicable for the site The shipping fee is returned through a combination of RQL query
     * with Iterating the Repository Items returned as part of RQL query. Here both ATG repository query-cache and
     * item-cache will be used.
     *
     * @param siteId the site id
     * @param shippingMethodId the shipping method id
     * @param stateId the state id
     * @param subTotalAmount the sub total amount
     * @param regionIdFromOrder the region id from order
     * @return the shipping fee
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public double getShippingFee(final String siteId, final String shippingMethodId, final String stateId,
                    final double subTotalAmount, String regionIdFromOrder) throws BBBSystemException, BBBBusinessException {
    	
        return getShippingRepositoryTools().getShippingFee(siteId, shippingMethodId, stateId, subTotalAmount, regionIdFromOrder);
    }

    /**
     *  If the SKU id does not exist in the repository then this method will throw a BBBBusinessException with an error
     * code indicating that the SKU does not exist. If the SKU is a gift card the method returns true and if the SKU is
     * not a gift card the method returns false The SKU attributes are read from the SKU Repository Item Cache
     *
     * @param siteId the site id
     * @param skuId the sku id
     * @return true, if is fixed price shipping
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public final boolean isFixedPriceShipping(final String siteId, final String skuId)
                    throws BBBSystemException, BBBBusinessException {
        
            final StringBuilder debug = new StringBuilder(50);
            debug.append("Catalog API Method Name [isFixedPriceShipping] siteId[").append(siteId).append("] skuId[")
                            .append(skuId);
            this.logDebug(debug.toString());

        
        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " isFixedPriceShipping");
            final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId,
                            BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
            if (skuRepositoryItem == null) {
                if (this.isLoggingError()) {
                    this.logError("catalog_1001: Sku[" + siteId + "] is not present in Repository");
                }
                throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
            }
            if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.GIFT_CERT_SKU_PROPERTY_NAME) != null) {
                this.logTrace("gift cert property value for skuId[" + skuId + "] is "
                                + skuRepositoryItem.getPropertyValue(BBBCatalogConstants.GIFT_CERT_SKU_PROPERTY_NAME));
                return ((Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.GIFT_CERT_SKU_PROPERTY_NAME))
                                .booleanValue();
            }
            this.logTrace("SKU[" + skuId + "] is not a gift Card");
            return false;
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [isFixedPriceShipping]: RepositoryException ");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
        	debug.append(" Exit");
        	this.logDebug(debug.toString());
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " isFixedPriceShipping");
        }
    }

    /**
     *  If the SKU Id does not exist in the system, the method will throw a BBBBusiness Exception with an error code
     * indicating that the SKU does not exist If there is no surcharge configured for the SKU, the method returns the
     * value as $0.00 The SKU attributes are read from the Repository Item cache. If the SKU is found in the cache the
     * value is read from cache otherwise ATG retrieves it from database and caches it for future use
     *
     * @param pSiteId the site id
     * @param pSkuId the sku id
     * @param shippingMethodId the shipping method id
     * @return the SKU surcharge
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public double getSKUSurcharge(final String pSiteId, final String pSkuId, final String shippingMethodId)
                    throws BBBSystemException, BBBBusinessException {
       
            final StringBuilder debug = new StringBuilder(50);
            debug.append("Catalog API Method Name [isFixedPriceShipping] siteId[").append(pSiteId)
                            .append(" ] skuId [ ").append(pSkuId).append(" ] ShippingMethodId[")
                            .append(shippingMethodId).append(']');
            this.logDebug(debug.toString());

        double surcharge = 0.00;

        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUSurcharge");
            final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(pSkuId,
                            BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
            if (skuRepositoryItem == null) {
                if (this.isLoggingError()) {
                    this.logError("catalog_1002:Sku[" + pSkuId + "] is not present in Repository");
                }
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUSurcharge");
                throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
            }

            if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SHIPPING_SURCHARGE_SKU_PROPERTY_NAME) != null) {
                surcharge = ((Double) skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.SHIPPING_SURCHARGE_SKU_PROPERTY_NAME))
                                .doubleValue();
                this.logTrace("applicable surcharge[" + surcharge + "]");
            }
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getSKUSurcharge]: RepositoryException ");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUSurcharge");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
        	debug.append(" Exit");
        	this.logDebug(debug.toString());
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUSurcharge");
        }
        return surcharge;
    }

    /**
     *  Get details of college categories sleep,eat etc for college landing page.
     *
     * @return the college categories
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final CategoryVO getCollegeCategories() throws BBBBusinessException, BBBSystemException {

        final String siteId = SiteContextManager.getCurrentSiteId();
        final String rootCollegeId = this.getRootCollegeIdFrmConfig(siteId);
       
            this.logTrace("Catalog API Method Name [getCollegeCategories] siteid " + siteId + " root college Id "
                            + rootCollegeId);

        return this.getCategoryDetail(siteId, rootCollegeId,false);
    }

    /**
     *  The method gets category details in the form of CategoryVO If the category does not exist in the repository throw
     * BBBBusinessException indicating the error code Check if product is disabled throw BBBBusinessException indicating
     * the error code Check if product is a collection or a lead sku If isCollection or isLeadSu is true product is a
     * collection and has child products so return collectionVO if product is a collection it doesnot have child sku or
     * rollup types or attributes if product is a lead sku it has child sku,rolluptypes and attributes.
     *
     * @param pSiteId the site id
     * @param pCategoryId the category id
     * @param isProductListRequired the is product list required
     * @return the category detail
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public CategoryVO getCategoryDetail(final String pSiteId, final String pCategoryId, final boolean isProductListRequired)
                    throws BBBSystemException, BBBBusinessException {
       
            final StringBuilder debug = new StringBuilder(50);
            debug.append("Catalog API Method Name [getCategoryDetail] siteId[").append(pSiteId)
                            .append("] pCategoryId [").append(pCategoryId);
            this.logDebug(debug.toString());


        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getCategoryDetail");

            final RepositoryView repositoryView = this.getCatalogRepository().getView(
                            BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
            final QueryBuilder queryBuilder = repositoryView.getQueryBuilder();
            final QueryExpression expressionProperty = queryBuilder.createPropertyQueryExpression(ID);
            final QueryExpression expressionValue = queryBuilder.createConstantQueryExpression(pCategoryId);

            final Query query = queryBuilder.createComparisonQuery(expressionProperty, expressionValue,
                            QueryBuilder.EQUALS);
            final RepositoryItem[] items = repositoryView.executeQuery(query);
            if ((items == null) || (items.length == 0)) {
                throw new BBBBusinessException(BBBCatalogErrorCodes.CATEGORY_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.CATEGORY_NOT_AVAILABLE_IN_REPOSITORY);
            }

            final RepositoryItem categoryRepositoryItem = items[0];
            final boolean isActive = this.isCategoryActive(categoryRepositoryItem);
            this.logTrace(pCategoryId + " isActive " + isActive);
            if (!isActive) {
                throw new BBBBusinessException(
                                BBBCatalogErrorCodes.CATEGORY_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY,
                                BBBCatalogErrorCodes.CATEGORY_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY);
            }

            @SuppressWarnings ("unchecked")
            final List<RepositoryItem> subCategories = (List<RepositoryItem>) categoryRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.CHILD_CATEGORIES_PROPERTY_NAME);
            if (!BBBUtility.isListEmpty(subCategories)) {
                final List<CategoryVO> subcategoryVOList = new ArrayList<CategoryVO>();
                CategoryVO subcategoryVO = null;
                final StringBuffer logDisabledCategory = new StringBuffer("Categories are disabled Ignoring Category IDs : ");
                for (int i = 0; i < subCategories.size(); i++) {
                    this.logTrace(i + "th subcategory id [" + subCategories.get(i).getRepositoryId() + "]");
                    try {
                        subcategoryVO = this.getCategoryDetail(pSiteId, subCategories.get(i).getRepositoryId(),isProductListRequired);
                        subcategoryVOList.add(subcategoryVO);
                    } catch (final BBBBusinessException e) {
                    	logDisabledCategory.append(subCategories.get(i).getRepositoryId())
                                            .append(" in subcategory of main category ").append(pCategoryId)
                                            .append(" Message is ").append(e.getMessage()).append(" || ");
                            this.logDebug(logDisabledCategory.toString());
                        }
                    }
                return this.getCategoryVO(categoryRepositoryItem, subcategoryVOList, null);
            }

            if(isProductListRequired){
            	final List<String> childProductsList = new ArrayList<String>();
	            @SuppressWarnings ("unchecked")
	            final List<RepositoryItem> childProducts = (List<RepositoryItem>) categoryRepositoryItem
	                            .getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_CATEGORY_PROPERTY_NAME);
	            if (!BBBUtility.isListEmpty(childProducts)) {
	                for (int i = 0; i < childProducts.size(); i++) {
	                    this.logTrace(i + "th child product id [" + childProducts.get(i).getRepositoryId() + "]");
	                    childProductsList.add(childProducts.get(i).getRepositoryId());
	                }
	                return this.getCategoryVO(categoryRepositoryItem, null, childProductsList);
	            }
            }
            return this.getCategoryVO(categoryRepositoryItem, null, null);

        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getCategoryDetail]: RepositoryException ");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCategoryDetail");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
            		BBBCatalogConstants.DATA_UNAVAILABLE_IN_REPOSITORY, e);
        } finally {
        	debug.append(" Exit");
        	this.logDebug(debug.toString());
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCategoryDetail");
        }
    }

    @Override
    public final boolean isPhantomCategory(final String pCategoryId) throws BBBSystemException, BBBBusinessException {
        try {
            final RepositoryItem categoryRepositoryItem = this.getCatalogRepository().getItem(pCategoryId, BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
		    if (categoryRepositoryItem == null || !this.isCategoryActive(categoryRepositoryItem)) {
		        throw new BBBBusinessException(BBBCatalogErrorCodes.CATEGORY_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY, BBBCatalogErrorCodes.CATEGORY_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY);
		    }
		    Boolean isPhantomCategory = false;
		    if (categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY) != null) {
		    	isPhantomCategory =  (Boolean) categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY);
		    }
            this.logDebug("Catalog API Method Name [isPhantomCategory] pCategoryId " + pCategoryId + " isPhantom = " + isPhantomCategory);
            return isPhantomCategory;
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [isPhantomCategory]: RepositoryException ");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, BBBCatalogConstants.DATA_UNAVAILABLE_IN_REPOSITORY, e);
        }
    }
    
    
    /* Method to return clearance product list vo's for given site id and category id (non-Javadoc)* @see
     * com.bbb.commerce.catalog.BBBCatalogTools#getClearanceProducts(java.lang .String, java.lang.String) */
    @Override
    public final List<ProductVO> getClearanceProducts(final String pSiteId, final String pCategoryId)
                    throws BBBSystemException, BBBBusinessException {
      
            final StringBuilder debug = new StringBuilder(50);
            debug.append("Catalog API Method Name [getClearanceProducts] siteId[").append(pSiteId)
                            .append("] pCategoryId [").append(pCategoryId);
            this.logDebug(debug.toString());
      
        final List<ProductVO> pProductDetails = new ArrayList<ProductVO>();
        final List<CategoryVO> lastNodecatVOs = new ArrayList<CategoryVO>();
        int productCount = 0;
        final List<CategoryVO> removeCat = new ArrayList<CategoryVO>();
        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getClearanceProducts");
            final List<String> listOfClearanceKeys = this.getAllValuesForKey(this.getConfigType(), pSiteId
                            + "_noClearanceProducts");
            final int maxProducts = Integer.parseInt(listOfClearanceKeys.get(0).toString());
            int iterationCount = 0;
            final CategoryVO clearanceCatVO = this.getCategoryDetail(pSiteId, pCategoryId,true);
            if (null != clearanceCatVO) {
                
                // Calling the method to set the sub categories into the
                // lastNodecatVOs
                if (!BBBUtility.isListEmpty(clearanceCatVO.getSubCategories())) {
                    this.setlastNodeCategories(clearanceCatVO, lastNodecatVOs);
                    this.logDebug("list of leaf level categorires::::" + lastNodecatVOs.toString());
                    if (!lastNodecatVOs.isEmpty()) {
                        this.logDebug(":::List of Last node categories:::");
                        while (true) {
                            for (final CategoryVO catVO1 : lastNodecatVOs) {
                                if ((catVO1.getChildProducts().size() >= (iterationCount + 1))) {
                                    try {
                                    	//get minimal product details
                                    	 ProductVO productVO = this.getProductDetails(pSiteId, catVO1
                                                .getChildProducts().get(iterationCount), true);
                                        if (productVO != null) {
                                            pProductDetails.add(productVO);
                                        }
                                        productCount++;
                                    } catch (final BBBBusinessException bbbbException) {
                                        if (this.isLoggingError()) {
                                            this.logError("catalog_1003:Error while fetching the product Details"
                                                            + bbbbException);
                                        }
                                    }

                                    if (productCount >= maxProducts) {
                                        break;
                                    }
                                    if (catVO1.getChildProducts().size() <= (iterationCount + 1)) {
                                        removeCat.add(catVO1);
                                    }
                                } else {
                                    removeCat.add(catVO1);
                                }
                            }

                            if (!removeCat.isEmpty()) {
                                lastNodecatVOs.removeAll(removeCat);
                            }
                            if ((productCount >= maxProducts) || lastNodecatVOs.isEmpty()) {
                                break;
                            }
                            iterationCount++;
                        }
                    } else {
                        this.logDebug("lastNodecatVOs is Null");
                    }
                }
                lastNodecatVOs.clear();
            }
        } catch (final BBBBusinessException e) {
         this.logError("catalog_1004:BBB Business Exception:::" + e.getMessage(),e);          
        } catch (final BBBSystemException e) {
          this.logError("catalog_1005:BBB System Exception:::" + e.getMessage(),e);
        } finally {
        	debug.append(" Exit");
        	this.logDebug(debug.toString());
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getClearanceProducts");
        }
        return pProductDetails;
    }

    /* method to fetch list of product that are available for clearance.
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getClearanceProduct() */
    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getClearanceProduct()
     */
    @Override
    public final List<CollectionProductVO> getClearanceProduct() throws BBBSystemException, BBBBusinessException {
        this.logDebug("BBBCatalogToolsImpl.getClearanceProduct : START");
        List<CollectionProductVO> productList = new ArrayList<CollectionProductVO>();
        final String siteId =getCurrentSiteId();
        final String key = siteId + "_" + BBBCmsConstants.CONFIG_KEY;

        final List<String> listOfClearanceCatItems = this.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, key);
        if (!BBBUtility.isListEmpty(listOfClearanceCatItems)) {
            productList = this.getClearanceCollections(siteId, listOfClearanceCatItems.get(0));
        }
        this.logDebug("BBBCatalogToolsImpl.getClearanceProduct : END");
        return productList;
    }

    /**
     *  Used for Rest API Method to return clearance collections list vo's for given site id and category id.
     * (non-Javadoc)* @see com.bbb.commerce.catalog.BBBCatalogTools#getClearanceProducts(java.lang .String,
     * java.lang.String)
     *
     * @param pSiteId the site id
     * @param pCategoryId the category id
     * @return Collection Product List
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    public final List<CollectionProductVO> getClearanceCollections(final String pSiteId, final String pCategoryId)
                    throws BBBSystemException, BBBBusinessException {
     
            final StringBuilder debug = new StringBuilder(50);
            debug.append("Catalog API Method Name [getClearanceProducts] siteId[").append(pSiteId)
                            .append("] pCategoryId [").append(pCategoryId);
            this.logDebug(debug.toString());
        final List<CollectionProductVO> pProductDetails = new ArrayList<CollectionProductVO>();
        final List<CategoryVO> lastNodecatVOs = new ArrayList<CategoryVO>();
        int productCount = 0;
        final List<CategoryVO> removeCat = new ArrayList<CategoryVO>();
        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getClearanceProducts");
            final List<String> listOfClearanceKeys = this.getAllValuesForKey(this.getConfigType(), pSiteId
                            + "_noClearanceProducts");
            final int maxProducts = Integer.parseInt(listOfClearanceKeys.get(0).toString());
            int iterationCount = 0;
            final CategoryVO clearanceCatVO = this.getCategoryDetail(pSiteId, pCategoryId,true);
            if (null != clearanceCatVO) {
                
                // Calling the method to set the sub categories into the
                // lastNodecatVOs
                if (!BBBUtility.isListEmpty(clearanceCatVO.getSubCategories())) {
                    this.setlastNodeCategories(clearanceCatVO, lastNodecatVOs);
                    this.logTrace("list of leaf level categorires::::" + lastNodecatVOs.toString());
                    if (!lastNodecatVOs.isEmpty()) {
                        this.logTrace(":::List of Last node categories:::");
                        while (true) {
                            for (final CategoryVO catVO1 : lastNodecatVOs) {
                                CollectionProductVO collectionProductVO = null;
                                ProductVO productVO = null;
                                if ((catVO1.getChildProducts().size() >= (iterationCount + 1))) {
                                    try {
                                    	//get minimal product details
                                        productVO = this.getProductDetails(pSiteId,
                                                        catVO1.getChildProducts().get(iterationCount), true);
                                        if (productVO != null) {
                                            if (productVO.isCollection()) {
                                                collectionProductVO = (CollectionProductVO) productVO;
                                            } else {
                                                collectionProductVO = new CollectionProductVO(productVO);
                                            }
                                            pProductDetails.add(collectionProductVO);
                                        }
                                        productCount++;
                                    } catch (final BBBBusinessException bbbbException) {
                                        if (this.isLoggingError()) {
                                            this.logError("catalog_1003:Error while fetching the product Details"
                                                            + bbbbException);
                                        }
                                    }

                                    if (productCount >= maxProducts) {
                                        break;
                                    }
                                    if (catVO1.getChildProducts().size() <= (iterationCount + 1)) {
                                        removeCat.add(catVO1);
                                    }
                                } else {
                                    removeCat.add(catVO1);
                                }
                            }

                            if (!removeCat.isEmpty()) {
                                lastNodecatVOs.removeAll(removeCat);
                            }
                            if ((productCount >= maxProducts) || lastNodecatVOs.isEmpty()) {
                                break;
                            }
                            iterationCount++;
                        }
                    } else {
                        this.logTrace("lastNodecatVOs is Null");
                    }
                }
                lastNodecatVOs.clear();
            }
        } catch (final BBBBusinessException e) {
            this.logError("catalog_1004:BBB Business Exception:::" + e.getMessage(),e);
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getClearanceProducts");
            throw new BBBSystemException(BBBCatalogErrorCodes.BUSSINESS_EXCEPTION_COLLEGE_DORM_ROOM,
                            BBBCatalogErrorCodes.BUSSINESS_EXCEPTION_COLLEGE_DORM_ROOM, e);
        } catch (final BBBSystemException e) {
            this.logError("catalog_1005:BBB System Exception:::" + e.getMessage(),e);
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getClearanceProducts");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
        	debug.append(" Exit");
        	this.logDebug(debug.toString());
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getClearanceProducts");
        }
        return pProductDetails;
    }

    /**
     *  Method to set the sub categories lastNodeCatVOs which don't have the sub categories.
     *
     * @param clearanceCatVO the clearance cat vo
     * @param lastNodecatVOs the last nodecat v os
     */
    public final void setlastNodeCategories(final CategoryVO clearanceCatVO, final List<CategoryVO> lastNodecatVOs) {
        if (!BBBUtility.isListEmpty(clearanceCatVO.getSubCategories())) {
            this.logDebug("size of Sub Categories" + clearanceCatVO.getSubCategories().size());
            for (final CategoryVO catVO : clearanceCatVO.getSubCategories()) {
                this.logDebug("CategoryID" + catVO.getCategoryId());
                if ((catVO.getSubCategories() == null) && (catVO.getChildProducts() != null)) {
                    lastNodecatVOs.add(catVO);
                } else {
                    this.setlastNodeCategories(catVO, lastNodecatVOs);
                }
            }
        }

    }

    /**
     *  The method returns a list of ProductVO for products whose gift cert flag is true if no product is available with
     * giftcert true then a BBBBusinessException is thrown if the input parameter is null then also BBBBusinessException
     * exception is thrown.
     *
     * @param siteId the site id
     * @return the gift products
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public final List<ProductVO> getGiftProducts(final String siteId) throws BBBSystemException, BBBBusinessException {
        this.logDebug("Catalog API Method Name [getGiftProducts] siteId[" + siteId + "] ");
        if (BBBUtility.isNotBlank(siteId)) {
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getGiftProducts");
                RepositoryItem[] giftProductItem = null;
                final List<ProductVO> giftCardproductVOList = new ArrayList<ProductVO>();
                giftProductItem = this.executeRQLQuery(this.getGiftCertProductQuery(),
                                BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR, this.getCatalogRepository());
                if ((giftProductItem != null) && (giftProductItem.length > 0)) {
                    for (int i = 0; i < giftProductItem.length; i++) {
                        final RepositoryItem prodRepoItem = giftProductItem[i];
                        this.logTrace(i + "th gift product id [" + prodRepoItem.getRepositoryId() + "]");
                        if (this.isProductActive(prodRepoItem)) {
                            this.logTrace(prodRepoItem.getRepositoryId() + " is active");
                            giftCardproductVOList.add(this.getProductDetails(siteId, prodRepoItem.getRepositoryId()));
                        }
                    }
                    return giftCardproductVOList;
                }
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getGiftProducts");
                throw new BBBBusinessException(BBBCatalogErrorCodes.NO_GIFT_CARD_PRODUCT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.NO_GIFT_CARD_PRODUCT_AVAILABLE_IN_REPOSITORY);
            } catch (final RepositoryException e) {
                this.logError("Catalog API Method Name [getGiftProducts]: RepositoryException ");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getGiftProducts");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
            } finally {
            	this.logDebug("Catalog API Method Name [getGiftProducts] siteId[" + siteId + "] Exit");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getGiftProducts");
            }
        }
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getProductDetails(java.lang.String, java.lang.String)
     */
    @Override
    public ProductVO getProductDetails(final String pSiteId, final String pProductId)
                    throws BBBSystemException, BBBBusinessException {
        return this.getProductDetails(pSiteId, pProductId, false);
    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getProductDetails(java.lang.String, java.lang.String, boolean)
     */
    @Override
    public ProductVO getProductDetails(final String pSiteId, final String pProductId, final boolean minimal)
                    throws BBBSystemException, BBBBusinessException {
        return this.getProductDetails(pSiteId, pProductId, true, minimal);
    }
    
    
    //Ever living pdp product details 
    /**
     *  The method returns ProductVO If product is a collection then the rollup types of the childProducts need to come.
     * from collectionRollupType property of bbbPrdReln item-descriptor where as if product is not a collection then the
     * rollup types need to be populated with prodRollupType property of product To decide if roll up needs to be
     * populated from relation or product a flag populateRollUp is added as a parameter.For collection this flag is
     * false and for a product it is true;
     *
     * @param pSiteId the site id
     * @param pProductId the product id
     * @param populateRollUp the populate roll up
     * @param isMinimalDetails the is minimal details
     * @param isAddException the is add exception
     * @return Product Details
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */

    @Override
    public final ProductVO getEverLivingProductDetails(final String pSiteId, final String pProductId,
            final boolean populateRollUp, final boolean isMinimalDetails, final boolean isAddException)
            throws BBBSystemException, BBBBusinessException {
            final StringBuilder debug = new StringBuilder(50);
            debug.append("Catalog API Method Name [getEverLivingProductDetails] siteId[").append(pSiteId)
                            .append("] pProductId [").append(pProductId);
            this.logDebug(debug.toString());
     
        final long startTime = System.currentTimeMillis();
        if (!StringUtils.isEmpty(pSiteId)) {
            ProductVO productVO;
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_1");
                final RepositoryItem productRepositoryItem = this.getCatalogRepository().getItem(pProductId,
                                BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
                if (productRepositoryItem == null) {
                	 BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_1");
                    throw new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
                                    BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);
                }

                final boolean isEverLiving = this.isEverlivingProduct(pProductId,pSiteId);
                
                this.logTrace(pProductId + " Product is isEverLiving [" + isEverLiving + "]");
                if (!isEverLiving && isAddException) {
                	 BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_1");
                    throw new BBBBusinessException(
                                    BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY,
                                    BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY);
                }

                boolean isCollection = false;
                boolean isLeadProduct = false;
                if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) != null) {
                    isCollection = ((Boolean) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME))
                                    .booleanValue();
                }
                if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) != null) {
                    isLeadProduct = ((Boolean) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME))
                                    .booleanValue();
                }
                this.logTrace("Product is a Collection [" + isCollection + "] product is a lead product ["
                                + isLeadProduct + "]");
                if ((isCollection || isLeadProduct) && !isMinimalDetails) {
                    @SuppressWarnings ("unchecked")
                    final List<RepositoryItem> childProductsRelationList = (List<RepositoryItem>) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME);
                    if ((!BBBUtility.isListEmpty(childProductsRelationList)) || isLeadProduct) {
                        final List<ProductVO> subProductVOList = new ArrayList<ProductVO>();
                        RepositoryItem childProdItem = null;
                        RepositoryItem childProdRollUpTypesItem = null;
                        ProductVO subProductVO = null;
                        String childProductId = null;
                        boolean giftFlag = false;
                       if(childProductsRelationList != null){
                    	   this.logTrace("No of child Products [" + childProductsRelationList.size() + "]");
                        for (int i = 0; i < childProductsRelationList.size(); i++) {

                            childProdItem = (RepositoryItem) childProductsRelationList.get(i).getPropertyValue(
                                            BBBCatalogConstants.CHILD_PRODUCTS_PRODUCT_RELATION_PROPERTY_NAME);
                            if (null != childProdItem.getPropertyValue("giftCertProduct")) {
                                giftFlag = ((Boolean) childProdItem.getPropertyValue("giftCertProduct")).booleanValue();
                            }

                            childProductId = childProdItem.getRepositoryId();
                            this.logTrace("Product Id of " + i + "th child  [" + childProductId + "]");
                            // check if sub product is active only then add to list
                            if (this.isProductActive(childProdItem)) {
                                @SuppressWarnings ("unchecked")
                                final List<RepositoryItem> skuRepositoryItems = (List<RepositoryItem>) childProdItem
                                                .getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);
                                childProdRollUpTypesItem = (RepositoryItem) childProductsRelationList
                                                .get(i)
                                                .getPropertyValue(
                                                                BBBCatalogConstants.COLLECTION_ROLL_UP_PRODUCT_RELATION_PROPERTY_NAME);
                                subProductVO = this.getProductDetails(pSiteId, childProductId, false);
                                if (childProdRollUpTypesItem != null) {
                                    final String childProductrollUpAttribute = (String) childProdRollUpTypesItem
                                                    .getPropertyValue(BBBCatalogConstants.ROLLUP_ATTRIBUTE_ROLLUP_PROPERTY_NAME);
                                    this.logTrace("Product Relation Roll Up attribute String of  " + i + "th child  ["
                                                    + childProdItem.getRepositoryId() + "]");
                                  //don't need to check active status for ever living
									subProductVO
											.setPrdRelationRollup(this
													.getRollUpAttributeForProduct(
															childProductrollUpAttribute,
															skuRepositoryItems,
															false));
                                }
                                subProductVOList.add(subProductVO);
                            }else if(this.isEverlivingProduct(childProductId,pSiteId)) {
                                @SuppressWarnings ("unchecked")
                                final List<RepositoryItem> skuRepositoryItems = (List<RepositoryItem>) childProdItem
                                                .getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);
                                childProdRollUpTypesItem = (RepositoryItem) childProductsRelationList
                                                .get(i)
                                                .getPropertyValue(
                                                                BBBCatalogConstants.COLLECTION_ROLL_UP_PRODUCT_RELATION_PROPERTY_NAME);
                                subProductVO = this.getEverLivingProductDetails(pSiteId, childProductId, true, false, true);
                                if (childProdRollUpTypesItem != null) {
                                    final String childProductrollUpAttribute = (String) childProdRollUpTypesItem
                                                    .getPropertyValue(BBBCatalogConstants.ROLLUP_ATTRIBUTE_ROLLUP_PROPERTY_NAME);
                                    this.logTrace("Product Relation Roll Up attribute String of  " + i + "th child  ["
                                                    + childProdItem.getRepositoryId() + "]");
                                 //don't need to check active status for ever living
									subProductVO
											.setPrdRelationRollup(this
													.getRollUpAttributeForProduct(
															childProductrollUpAttribute,
															skuRepositoryItems,
															false));
                                }
                                subProductVOList.add(subProductVO);
                            } else {
                                this.logTrace("sub product with Product Id   [" + childProductId
                                                + "] is diabled so not including in list of sub products");
                            }
                        }
					   }
                        productVO = this.getCollectionProductVO(productRepositoryItem, subProductVOList, pSiteId);
                        // If all child SKU have giftCertProduct true then set this true
                        productVO.setGiftCertProduct(Boolean.valueOf(giftFlag)); 
                        //setting everLiving flag for collections and accessories
                        productVO.setIsEverLiving(isEverLiving);
                        // Logic for View Product guide link on PDP.
                        final String prodGuideId = this.getProductGuideId(productRepositoryItem, pSiteId);
                        if (!BBBUtility.isEmpty(prodGuideId)) {
                            productVO.setShopGuideId(prodGuideId);
                        }
                        getVendorInfoProductVO(productVO, productRepositoryItem);
                        this.logTrace(productVO.toString());
                        return productVO;
                    }
                    this.logError("catalog_1006: Product is a collection but has no child products");
                    throw new BBBBusinessException(
                                    BBBCatalogErrorCodes.CHILD_PRODUCTS_NOT_PRESENT_FOR_COLLECTION_PRODUCT,
                                    BBBCatalogErrorCodes.CHILD_PRODUCTS_NOT_PRESENT_FOR_COLLECTION_PRODUCT);
                }
                this.logTrace("Product is not a Collection ");
                return this.getEverLivingProductVO(productRepositoryItem, pSiteId, populateRollUp, isMinimalDetails, isEverLiving);
            } catch (final RepositoryException e) {
                this.logError("Catalog API Method Name [getProductDetails]: RepositoryException ");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_1");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
            } finally {
                final long totalTime = System.currentTimeMillis() - startTime;
                this.logDebug("Total time taken for BBBCatalogTools.getProductDetails() is: " + totalTime
                                + " for product id: " + pProductId + " and minimal details is: " + isMinimalDetails);
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_1");
            }
        }
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }
    
    //End ever living product details

    /**
     * This method returns only SEO Meta Details for product.
     *  - Title
     *  - Description
     *  - Keyowrds
     *  
     *
     * @param pSiteId the site id
     * @param pProductId the product id
     * @return the product vo meta details
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    public ProductVO getProductVOMetaDetails(final String pSiteId, final String pProductId)
            throws BBBSystemException, BBBBusinessException {
    	
            final StringBuilder debug = new StringBuilder(50);
            debug.append("Catalog API Method Name [getProductVOMetaDetails] siteId[").append(pSiteId)
                            .append("] pProductId [").append(pProductId);
            this.logDebug(debug.toString());
        final long startTime = System.currentTimeMillis();
        if (StringUtils.isNotEmpty(pSiteId)) {
            ProductVO productVO = new ProductVO();
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getProductVOMetaDetails");
                final RepositoryItem productRepositoryItem = this.getCatalogRepository().getItem(pProductId,
                                BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
                
                if (productRepositoryItem == null) {
                    this.logTrace("Product Not Available in repository for Product ID : " + pProductId);
                    throw new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
                                    BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);
                }

                if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME) != null) {
                    productVO.setName((String) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME));
                }

                if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME) != null) {
                    productVO.setShortDescription((String) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME));
                }
                
                if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.PRODUCT_KEYWORDS) != null) {
                    productVO.setPrdKeywords((String) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.PRODUCT_KEYWORDS));
                }
                getVendorInfoProductVO(productVO, productRepositoryItem);
                
                return productVO;
                
            } catch (final RepositoryException e) {
                this.logError("Catalog API Method Name [getProductVOMetaDetails]: RepositoryException ");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductVOMetaDetails");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
            } finally {
                final long totalTime = System.currentTimeMillis() - startTime;
                this.logDebug("Total time taken for BBBCatalogTools.getProductVOMetaDetails() is: " + totalTime
                                + " for product id: " + pProductId);
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductVOMetaDetails");
            }
        }
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }
    
    /**
     *  The method returns ProductVO If product is a collection then the rollup types of the childProducts need to come.
     * from collectionRollupType property of bbbPrdReln item-descriptor where as if product is not a collection then the
     * rollup types need to be populated with prodRollupType property of product To decide if roll up needs to be
     * populated from relation or product a flag populateRollUp is added as a parameter.For collection this flag is
     * false and for a product it is true;
     *
     * @param pSiteId the site id
     * @param pProductId the product id
     * @param populateRollUp the populate roll up
     * @param isMinimalDetails the is minimal details
     * @param isAddException the is add exception
     * @return Product Details
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */

    @Override
    public final ProductVO getProductDetails(final String pSiteId, final String pProductId,
                    final boolean populateRollUp, final boolean isMinimalDetails, final boolean isAddException)
                    throws BBBSystemException, BBBBusinessException {
            final StringBuilder debug = new StringBuilder(50);
            debug.append("Catalog API Method Name [getProductDetails] siteId[").append(pSiteId)
                            .append("] pProductId [").append(pProductId);
            this.logDebug(debug.toString());
           final long startTime = System.currentTimeMillis();
        if ((pSiteId != null) && !StringUtils.isEmpty(pSiteId)) {
        	String isIntlRestricted = BBBCoreConstants.NO_CHAR;
            ProductVO productVO = new ProductVO();
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_1");
                final RepositoryItem productRepositoryItem = this.getCatalogRepository().getItem(pProductId,
                                BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
                if (productRepositoryItem == null) {
                	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_1");
                    throw new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
                                    BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);
                }

                final boolean isActive = this.isProductActive(productRepositoryItem);
                if (!BBBUtility.isEmpty((String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED))) {
			    	isIntlRestricted = (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED);
				}
                productVO.setIntlRestricted(BBBCoreConstants.YES_CHAR.equalsIgnoreCase(isIntlRestricted));
                this.logTrace(pProductId + " Product is active [" + isActive + "]");
                if (!isActive && isAddException) {
                	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_1");
                	 throw new BBBBusinessException(
                                    BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY,
                                    BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY);
                }

                boolean isCollection = false;
                boolean isLeadProduct = false;
                boolean isLTLProduct=false;
                if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) != null) {
                	isLTLProduct = ((Boolean) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.IS_LTL_SKU))
                                    .booleanValue();
                }
                
                if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_ID_PRODUCT_PROPERTY)!=null){
                	productVO.setVendorId((String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_ID_PRODUCT_PROPERTY));
                }
                
                //LTL check product
                productVO.setLtlProduct(Boolean.valueOf(isLTLProduct));
                if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) != null) {
                    isCollection = ((Boolean) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME))
                                    .booleanValue();
                }
                if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) != null) {
                    isLeadProduct = ((Boolean) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME))
                                    .booleanValue();
                }
                this.logTrace("Product is a Collection [" + isCollection + "] product is a lead product ["
                                + isLeadProduct + "]");
                if ((isCollection || isLeadProduct)) {
                    @SuppressWarnings ("unchecked")
                    final List<RepositoryItem> childProductsRelationList = (List<RepositoryItem>) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME);
                    if ((childProductsRelationList != null && !childProductsRelationList.isEmpty()) || isLeadProduct) {
                        final List<ProductVO> subProductVOList = new ArrayList<ProductVO>();
                        this.logTrace("No of child Products [" + subProductVOList.size() + "]");
                        RepositoryItem childProdItem = null;
                        RepositoryItem childProdRollUpTypesItem = null;
                        ProductVO subProductVO = null;
                        String childProductId = null;
                        boolean giftFlag = false;
                        
                        if(childProductsRelationList != null && !isMinimalDetails){
	                        for (int i = 0; i < childProductsRelationList.size(); i++) {
	
	                            childProdItem = (RepositoryItem) childProductsRelationList.get(i).getPropertyValue(
	                                            BBBCatalogConstants.CHILD_PRODUCTS_PRODUCT_RELATION_PROPERTY_NAME);
	                            if (null != childProdItem.getPropertyValue("giftCertProduct")) {
	                                giftFlag = ((Boolean) childProdItem.getPropertyValue("giftCertProduct")).booleanValue();
	                            }
	
	                            childProductId = childProdItem.getRepositoryId();
	                            this.logTrace("Product Id of " + i + "th child  [" + childProductId + "]");
	                            // check if sub product is active only then add to list
	                            if (this.isProductActive(childProdItem)) {
	                                @SuppressWarnings ("unchecked")
	                                final List<RepositoryItem> skuRepositoryItems = (List<RepositoryItem>) childProdItem
	                                                .getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);
	                                childProdRollUpTypesItem = (RepositoryItem) childProductsRelationList
	                                                .get(i)
	                                                .getPropertyValue(
	                                                                BBBCatalogConstants.COLLECTION_ROLL_UP_PRODUCT_RELATION_PROPERTY_NAME);
	                                subProductVO = this.getProductDetails(pSiteId, childProductId, false);
	                                if (childProdRollUpTypesItem != null) {
	                                    final String childProductrollUpAttribute = (String) childProdRollUpTypesItem
	                                                    .getPropertyValue(BBBCatalogConstants.ROLLUP_ATTRIBUTE_ROLLUP_PROPERTY_NAME);
	                                    this.logTrace("Product Relation Roll Up attribute String of  " + i + "th child  ["
	                                                    + childProdItem.getRepositoryId() + "]");
	                                    
	                                    
	                                    int lengthOfRollUpAttr= 0;
	            						int lengthOfSizeSwatch = (Integer.parseInt(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.LENGTH_OF_SWATCH)));
	            						
	            							 Map<String, List<RollupTypeVO>> rollup=  this.getRollUpAttributeForProduct(childProductrollUpAttribute,skuRepositoryItems,true);
	            							 if(rollup.get("SIZE")!= null ){
	            								 for(int j=0;j<rollup.get("SIZE").size();j++)
	            								 {
	            									String rollUpAttributeValue = StringEscapeUtils.unescapeHtml(rollup.get("SIZE").get(j).getRollupAttribute());
	            									lengthOfRollUpAttr = lengthOfRollUpAttr + rollUpAttributeValue.length()+ lengthOfSwatch;
	            									  
	            								 }
	            								 if(lengthOfRollUpAttr > lengthOfSizeSwatch){
	            		                            	productVO.setDisplaySizeAsSwatch(false);
	            		            				}
	            		                            else{
	            		                            	productVO.setDisplaySizeAsSwatch(true);
	            		                            }
	            	                            }
	                                    
	                                    
	    							// Flag Added to check Product/SKU Active item for SiteMap Changes
	    						
	                                 // Flag Added to check Product/SKU Active
										// item for SiteMap Changes 504 -b
										subProductVO
												.setPrdRelationRollup(rollup);
	                                }
	                                if(subProductVO.getAttributesList() !=null){
										Set<String> keySet=subProductVO.getAttributesList().keySet();
										List<AttributeVO> allAttributesList = new ArrayList<AttributeVO>();
										for(String key:keySet){
											List<AttributeVO> attributesList = subProductVO.getAttributesList().get(key);
												if( attributesList!=null){
													allAttributesList.addAll(attributesList);
												}
										}
										subProductVO.setProductAllAttributesVO(remDuplicateAttributes(allAttributesList));																												
								}
	                                subProductVOList.add(subProductVO);
	                            } else {
	                                this.logTrace("sub product with Product Id   [" + childProductId
	                                                + "] is diabled so not including in list of sub products");
	                            }
	                        }
                        }
                        
                        productVO = this.getCollectionProductVO(productRepositoryItem, subProductVOList, pSiteId);
                        // If all child SKU have giftCertProduct true then set this true
                        productVO.setGiftCertProduct(Boolean.valueOf(giftFlag));
                        // Logic for View Product guide link on PDP.
                        final String prodGuideId = this.getProductGuideId(productRepositoryItem, pSiteId);
                        if (!BBBUtility.isEmpty(prodGuideId)) {
                            productVO.setShopGuideId(prodGuideId);
                        }
                        
                        getVendorInfoProductVO(productVO, productRepositoryItem);
                        
                        this.logTrace(productVO.toString());
 
                        return productVO;
                    }
                    this.logError("catalog_1006: Product is a collection but has no child products");
                    BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_1");
                    throw new BBBBusinessException(
                                    BBBCatalogErrorCodes.CHILD_PRODUCTS_NOT_PRESENT_FOR_COLLECTION_PRODUCT,
                                    BBBCatalogErrorCodes.CHILD_PRODUCTS_NOT_PRESENT_FOR_COLLECTION_PRODUCT);
                }
                this.logTrace("Product is not a Collection ");
                
                // setting isMinimalDetails to false for retrieving child skus 
                return this.getProductVO(productRepositoryItem, pSiteId, populateRollUp, false);
            } catch (final RepositoryException e) {
                this.logError("Catalog API Method Name [getProductDetails]: RepositoryException ");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_1");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
            } finally {
                final long totalTime = System.currentTimeMillis() - startTime;
                this.logDebug("Total time taken for BBBCatalogTools.getProductDetails() is: " + totalTime
                                + " for product id: " + pProductId + " and minimal details is: " + isMinimalDetails);
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_1");
            }
        }
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }
    
    /**
     * Lead Product
     * BPSI 2425
     * This is method for getting product detail for main product. It will be called for collection's main product and accessory's lead product also.
     * This method will populate only Main/Lead product and if main product is either collection or lead accessory then
     * it uses childProduct Ids to generate certonaLink String which is used for third party Certona.
     * 
     * For accessory and collection product, this method does not fetch child product detail that is it does not call getProductDetail() in iteration.
     * Child product details will be be fetched separately when child details are rendered in AJAX flow.
     *
     * @param pSiteId the site id
     * @param pProductId the product id
     * @param populateRollUp the populate roll up
     * @param isMinimalDetails the is minimal details
     * @param isAddException the is add exception
     * @return the main product details
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    public final ProductVO getMainProductDetails(final String pSiteId, final String pProductId,
                    final boolean populateRollUp, final boolean isMinimalDetails, final boolean isAddException)
                    throws BBBSystemException, BBBBusinessException {
        final StringBuilder debug = new StringBuilder(50);
        //DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();

        debug.append("Catalog API Method Name [getLeadProductDetails] siteId[").append(pSiteId)
                        .append("] pProductId [").append(pProductId);
        this.logDebug(debug.toString());
        final long startTime = System.currentTimeMillis();
        if (!StringUtils.isEmpty(pSiteId)) {
        	String isIntlRestricted = BBBCoreConstants.NO_CHAR;
            ProductVO productVO = new ProductVO();
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getLeadProductDetails_1");
                final RepositoryItem productRepositoryItem = this.getCatalogRepository().getItem(pProductId,
                                BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
                if (productRepositoryItem == null) {
                    throw new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
                                    BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);
                }
                final boolean isActive = this.isProductActive(productRepositoryItem);
                if (!BBBUtility.isEmpty((String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED))) {
			    	isIntlRestricted = (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED);
				}
                productVO.setIntlRestricted(BBBCoreConstants.YES_CHAR.equalsIgnoreCase(isIntlRestricted));
                this.logTrace(pProductId + " Product is active [" + isActive + "]");
                if (!isActive && isAddException) {
                	 throw new BBBBusinessException(
                                    BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY,
                                    BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY);
                }
                boolean isCollection = false;
                boolean isLeadProduct = false;
    			boolean isLTLProduct=false;
    			if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) != null) {
    				isLTLProduct = ((Boolean) productRepositoryItem
    						.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU))
    						.booleanValue();
    			}

    			if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_ID_PRODUCT_PROPERTY)!=null){
    				productVO.setVendorId((String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_ID_PRODUCT_PROPERTY));
    			}

    			//LTL check product
    			productVO.setLtlProduct(Boolean.valueOf(isLTLProduct));
                if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) != null) {
                    isCollection = ((Boolean) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME))
                                    .booleanValue();
                }
                if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) != null) {
                    isLeadProduct = ((Boolean) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME))
                                    .booleanValue();
                }
                
                
                this.logTrace("Product is a Collection [" + isCollection + "] product is a lead product ["
                                + isLeadProduct + "]");
                if ((isCollection || isLeadProduct) && !isMinimalDetails) {
                    @SuppressWarnings ("unchecked")
                    final List<RepositoryItem> childProductsRelationList = (List<RepositoryItem>) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME);
                    if ((childProductsRelationList != null && !childProductsRelationList.isEmpty()) ||  isLeadProduct) {
                        final List<ProductVO> subProductVOList = new ArrayList<ProductVO>();
                        this.logTrace("No of child Products [" + subProductVOList.size() + "]");
                        RepositoryItem childProdItem = null;
    					
                        String childProductId = null;
                        boolean giftFlag = false;
                        if(childProductsRelationList != null){
                        for (int i = 0; i < childProductsRelationList.size(); i++) {
                            childProdItem = (RepositoryItem) childProductsRelationList.get(i).getPropertyValue(
                                            BBBCatalogConstants.CHILD_PRODUCTS_PRODUCT_RELATION_PROPERTY_NAME);
                            if (null != childProdItem.getPropertyValue("giftCertProduct")) {
                                giftFlag = ((Boolean) childProdItem.getPropertyValue("giftCertProduct")).booleanValue();
                            }
                            childProductId = childProdItem.getRepositoryId();
                            this.logTrace("Product Id of MainMethod" + i + "th child  [" + childProductId + "]");
                            // check if sub product is active only then add to list
                            if (this.isProductActive(childProdItem)) {
                                @SuppressWarnings ("unchecked")
                                ProductVO subProductVO = new ProductVO();
                                subProductVO.setProductId(childProductId);
                                subProductVOList.add(subProductVO);
                            } else {
                                this.logTrace("sub product with Product Id   [" + childProductId
                                                + "] is diabled so not including in list of sub products");
                            }
                        }
                        }
                        productVO = this.getCollectionProductVO(productRepositoryItem, subProductVOList, pSiteId);
                        // If all child SKU have giftCertProduct true then set this true
                        productVO.setGiftCertProduct(Boolean.valueOf(giftFlag));
                        // Logic for View Product guide link on PDP.
                        final String prodGuideId = this.getProductGuideId(productRepositoryItem, pSiteId);
                        if (!BBBUtility.isEmpty(prodGuideId)) {
                            productVO.setShopGuideId(prodGuideId);
                        }
                        
                        getVendorInfoProductVO(productVO, productRepositoryItem);
                        

                        this.logTrace(productVO.toString());
                        //request.setParameter("nextIndex",0);
                        return productVO;
                    }
                    this.logError("catalog_1006: Product is a collection but has no child products");
                    throw new BBBBusinessException(
                                    BBBCatalogErrorCodes.CHILD_PRODUCTS_NOT_PRESENT_FOR_COLLECTION_PRODUCT,
                                    BBBCatalogErrorCodes.CHILD_PRODUCTS_NOT_PRESENT_FOR_COLLECTION_PRODUCT);
                }
                this.logTrace("Product is not a Collection ");
                
                //setting isMinimalDetails to false for retrieving child skus
                return this.getProductVO(productRepositoryItem, pSiteId, populateRollUp, false);
            } catch (final RepositoryException e) {
                this.logError("Catalog API Method Name [getProductDetails]: RepositoryException ");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
            } finally {
                final long totalTime = System.currentTimeMillis() - startTime;
                this.logDebug("Total time taken for BBBCatalogTools.getProductDetails() is: " + totalTime
                                + " for product id: " + pProductId + " and minimal details is: " + isMinimalDetails);
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_1");
            }
        }
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }

    /**
     * This method set vendor info in product vo.
     *
     * @param productVO the product vo
     * @param productRepositoryItem the product repository item
     * @return the vendor info product vo
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
	private void getVendorInfoProductVO(ProductVO productVO,
			final RepositoryItem productRepositoryItem)
			throws BBBSystemException, BBBBusinessException {
		if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_ID_PRODUCT_PROPERTY)!=null){
			productVO.setVendorId((String)productRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_ID_PRODUCT_PROPERTY));
		}
		if(!BBBUtility.isEmpty(productVO.getVendorId())){
			productVO.setVendorInfoVO(this.getVendorInfo(productVO.getVendorId()));                	
		}
	}
	
	
	
	// Start : R 2.2 Product Image SiteMap Generation 504-b 
	/**
	 * Added for SiteMap generation to get Minimal details.
	 * It uses repository item to give product details.
	 *
	 * @param productRepositoryItem the product repository item
	 * @param pSiteId the site id
	 * @return the product details
	 */
	@SuppressWarnings("boxing")
	@Override
	public ProductVO getProductDetails(RepositoryItem productRepositoryItem, String pSiteId){
		
		logDebug("Get Product Details using Product Id ");
		ProductVO productVO = getProductVO(productRepositoryItem,pSiteId,true);
			return productVO;
	}
	// End : R 2.2 Product Image SiteMap Generation 504-b 
	
    /**
	 *  The method returns ProductVO If product is a collection then the rollup types of the childProducts need to come.
	 * from collectionRollupType property of bbbPrdReln item-descriptor where as if product is not a collection then the
	 * rollup types need to be populated with prodRollupType property of product To decide if roll up needs to be
	 * populated from relation or product a flag populateRollUp is added as a parameter.For collection this flag is
	 * false and for a product it is true;
	 *
	 * @param pSiteId the site id
	 * @param pProductId the product id
	 * @param populateRollUp the populate roll up
	 * @param isMinimalDetails the is minimal details
	 * @return Product Details
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
    public final ProductVO getProductDetails(final String pSiteId, final String pProductId,
                    final boolean populateRollUp, final boolean isMinimalDetails)
                    throws BBBSystemException, BBBBusinessException {
        return this.getProductDetails(pSiteId, pProductId, true, isMinimalDetails, true);
    }

    /**
     *  This method returns the value of skuId for given values of rollup attributes.
     *
     * @param siteId the site id
     * @param productId the product id
     * @param rollUpTypeValueMap the roll up type value map
     * @return the SKU details
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public final String getSKUDetails(final String siteId, final String productId,
                    final Map<String, String> rollUpTypeValueMap) throws BBBSystemException, BBBBusinessException {

            final StringBuilder debug = new StringBuilder(50);
            debug.append("Catalog API Method Name [getProductDetails] siteId[").append(siteId).append("] productId [")
                            .append(productId);
            this.logDebug(debug.toString());
        final List<String> propertyNameList = new ArrayList<String>();
        final List<String> propertyValueList = new ArrayList<String>();
        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_2");
            final RepositoryItem productRepositoryItem = this.getCatalogRepository().getItem(productId,
                            BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
            if (productRepositoryItem == null) {
                throw new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);
            }
            @SuppressWarnings ("unchecked")
            final List<RepositoryItem> skuRepositoryItems = (List<RepositoryItem>) productRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);
            this.logTrace("skuRepositoryItems value [ " + skuRepositoryItems + "]");
            if (skuRepositoryItems != null) {
                // add properties name list in propertyNameList and property value list in propertyValueList
                final Set<Entry<String, String>> propertyNameSet = rollUpTypeValueMap.entrySet();
                for (final Entry<String, String> propertyName : propertyNameSet) {
                    propertyNameList.add(propertyName.getKey().toLowerCase());
                    propertyValueList.add(rollUpTypeValueMap.get(propertyName.getKey()));
                }
                // for each SKU repository item check if all the properties value match as per the properties in
                // rollUpTypeValueMap
                for (int i = 0; i < skuRepositoryItems.size(); i++) {
                    String skuPropertyValue = EMPTYSTRING;
                    this.logTrace("Check the value of rollup properties for the" + i + "th sku with id value [ "
                                    + skuRepositoryItems.get(i).getRepositoryId() + "]");
                    final boolean isActive = this.isSkuActive(skuRepositoryItems.get(i));
                    if (isActive) {
                        for (int index = 0; index < rollUpTypeValueMap.size(); index++) {
                            skuPropertyValue = (String) skuRepositoryItems.get(i).getPropertyValue(
                                            this.getRollUpSkuPropertyMap().get(
                                                            propertyNameList.get(index).toLowerCase()));
                            this.logTrace("value of rollup property [" + propertyNameList.get(index).toLowerCase()
                                            + "] for the sku from repository is [ " + skuPropertyValue + "]");
                            this.logTrace("value of input rollup property ["
                                            + propertyNameList.get(index).toLowerCase() + "]to be compared is  [ "
                                            + propertyValueList.get(index) + "]");
                            // If even one of the properties don't match break from the loop
                            if ((skuPropertyValue == null)
                                            || !skuPropertyValue.equalsIgnoreCase(propertyValueList.get(index))) {
                                break;
                            }

                            else if (index == (rollUpTypeValueMap.size() - 1)) {
                                this.logTrace("Final SkuId that matches is [ "
                                                + skuRepositoryItems.get(i).getRepositoryId() + "]");
                                return skuRepositoryItems.get(i).getRepositoryId();
                            }
                        }
                    }
                }
            }
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getProductDetails_2]: RepositoryException ");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_2");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
        	debug.append(" Exit");
        	this.logDebug(debug.toString());
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_2");
        }
        return null;
    }

    /**
     *  The method returns list of all ProductVO whose parent category has isCollege flag as true.
     *
     * @param siteId the site id
     * @param categoryId the category id
     * @return the college collections
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */

    @Override
    @SuppressWarnings ("unchecked")
    public final List<CollectionProductVO> getCollegeCollections(final String siteId, final String categoryId)
                    throws BBBSystemException, BBBBusinessException {

            final StringBuilder debug = new StringBuilder(50);
            debug.append("Catalog API Method Name [getCollegeCollections] siteId[").append(siteId)
                            .append("] categoryId [").append(categoryId);
            this.logDebug(debug.toString());
    
        if ((siteId != null) && !StringUtils.isEmpty(siteId)) {
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getCollegeCollections");
                final List<CollectionProductVO> collegeCollectionList = new ArrayList<CollectionProductVO>();
                final Object[] params = new Object[1];
                params[0] = categoryId;
                final RepositoryItem[] categoryItem = this.executeRQLQuery(this.getCollegeCategoryQuery(), params,
                                BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR, this.getCatalogRepository());
                if ((categoryItem != null) && (categoryItem.length > 0)) {
                    RepositoryItem productRepositoryItem = null;
                    boolean isActive = false;
                    boolean isCollection = false;
                    boolean isLeadProduct = false;
                    for (final RepositoryItem element : categoryItem) {
                    List<RepositoryItem> childProducts = (List<RepositoryItem>) element
                                        .getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_CATEGORY_PROPERTY_NAME);
                        if ((childProducts != null) && !childProducts.isEmpty()) {
                            for (int index = 0; index < childProducts.size(); index++) {
                                productRepositoryItem = childProducts.get(index);
                                isActive = this.isProductActive(productRepositoryItem);
                                final String productId = productRepositoryItem.getRepositoryId();

                                if (isActive) {
                                    if (productRepositoryItem
                                                    .getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) != null) {
                                        isCollection = ((Boolean) productRepositoryItem
                                                        .getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME))
                                                        .booleanValue();
                                        this.logTrace("value of isCollection flag [ " + isCollection + "]");
                                    }
                                    if (productRepositoryItem
                                                    .getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) != null) {
                                        isLeadProduct = ((Boolean) productRepositoryItem
                                                        .getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME))
                                                        .booleanValue();
                                        this.logTrace("value of isLeadProduct flag [ " + isLeadProduct + "]");
                                    }
                                    if (isCollection || isLeadProduct) {
                                        this.logTrace("add product id [ " + productId
                                                        + "] to college collection list as it is a collection");
                                        try {
                                            collegeCollectionList.add((CollectionProductVO) this.getProductDetails(
                                                            siteId, productId));
                                        } catch (final BBBBusinessException e) {
                                            final StringBuilder logDebug = new StringBuilder();
                                            logDebug.append("Ignore Product ID : ").append(productId)
                                                            .append(" in college Collection. Message : ")
                                                            .append(e.getMessage());
                                            if (this.isLoggingError()) {
                                                this.logError("catalog_1007:" + logDebug.toString(), e);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return collegeCollectionList;
                }
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCollegeCollections");
                throw new BBBBusinessException(BBBCatalogErrorCodes.CATEGORY_IS_NOT_COLLEGE_CATEGORY,
                                BBBCatalogErrorCodes.CATEGORY_IS_NOT_COLLEGE_CATEGORY);

            } finally {
            	debug.append(" Exit");
            	this.logDebug(debug.toString());
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCollegeCollections");
            }
        }

        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getDormRoomCollections(java.lang.String, java.lang.String)
     */
    @Override
    @SuppressWarnings ("unchecked")
    public final List<CollectionProductVO> getDormRoomCollections(final String siteId, final String categoryId)
                    throws BBBSystemException, BBBBusinessException {

            final StringBuilder debug = new StringBuilder(50);
            debug.append("Catalog API Method Name [getDormRoomCollections] siteId[").append(siteId)
                            .append("] categoryId [").append(categoryId);
            this.logDebug(debug.toString());
     
        if ((siteId != null) && !StringUtils.isEmpty(siteId)) {
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getDormRoomCollections");
                final List<CollectionProductVO> collegeCollectionList = new ArrayList<CollectionProductVO>();

                final RepositoryItem categoryItem = this.getCatalogRepository().getItem(categoryId,
                                BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
                if (categoryItem != null) {
                    RepositoryItem productRepositoryItem = null;
                    boolean isActive = false;
                    boolean isCollection = false;
                    boolean isLeadProduct = false;
                    List<RepositoryItem> childProducts = (List<RepositoryItem>) categoryItem
                                    .getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_CATEGORY_PROPERTY_NAME);
                    if ((childProducts != null) && !childProducts.isEmpty()) {
                        for (int index = 0; index < childProducts.size(); index++) {
                            productRepositoryItem = childProducts.get(index);
                            isActive = this.isProductActive(productRepositoryItem);
                            final String productId = productRepositoryItem.getRepositoryId();

                            if (isActive) {
                                if (productRepositoryItem
                                                .getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) != null) {
                                    isCollection = ((Boolean) productRepositoryItem
                                                    .getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME))
                                                    .booleanValue();
                                    this.logTrace("value of isCollection flag [ " + isCollection + "]");
                                }
                                if (productRepositoryItem
                                                .getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) != null) {
                                    isLeadProduct = ((Boolean) productRepositoryItem
                                                    .getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME))
                                                    .booleanValue();
                                    this.logTrace("value of isLeadProduct flag [ " + isLeadProduct + "]");
                                }
                                if (isCollection || isLeadProduct) {
                                    this.logTrace("add product id [ " + productId
                                                    + "] to college dorm room collection list as it is a collection");
                                    try {
                                        collegeCollectionList.add((CollectionProductVO) this.getProductDetails(siteId,
                                                        productId));
                                    } catch (final BBBBusinessException e) {
                                        final StringBuilder logDebug = new StringBuilder();
                                        logDebug.append("Ignore Product ID : ").append(productId)
                                                        .append(" in college Collection. Message : ")
                                                        .append(e.getMessage());
                                        this.logError("catalog_1008:" + logDebug.toString(), e);
                                    }
                                }
                            }
                        }
                    }

                    return collegeCollectionList;
                }
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getDormRoomCollections");
                throw new BBBBusinessException(BBBCatalogErrorCodes.CATEGORY_IS_NOT_COLLEGE_CATEGORY,
                                BBBCatalogErrorCodes.CATEGORY_IS_NOT_COLLEGE_CATEGORY);

            } catch (final RepositoryException e) {
                this.logError("Catalog API Method Name [getCollegeCollections]: RepositoryException ");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getDormRoomCollections");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);

            } finally {
            	debug.append(" Exit");
            	this.logDebug(debug.toString());
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getDormRoomCollections");
            }
        }

        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }

    /* Overridden method to fetch list of product available for Doom Room
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getDormRoomCollection() */
    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getDormRoomCollection()
     */
    @Override
    public final List<CollectionProductVO> getDormRoomCollection() throws BBBSystemException, BBBBusinessException {
        this.logDebug("BBBCatalogToolsImpl.getDormRoomCollection(): START");
        final String siteId = SiteContextManager.getCurrentSiteId();

        final String keyDormCollection = this.getDormRoomCollectionCatId() + siteId;
        final String dormRoomId = this.getAllValuesForKey(this.getConfigType(), keyDormCollection).get(0);
        this.logDebug("Calling BBBCatalogToolsImpl.getDormRoomCollections(siteId, dormRoomId)");
        return this.getDormRoomCollections(siteId, dormRoomId);
    }

    /**
     *  This method identifies whether the skuId passed is of type gift card or not The API reads the giftCert property
     * of the sku and returns the value. If giftCert is null it returns false
     *
     * @param pSiteId the site id
     * @param skuId the sku id
     * @return true, if is gift card item
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public boolean isGiftCardItem(final String pSiteId, final String skuId)
                    throws BBBSystemException, BBBBusinessException {

            final StringBuilder debug = new StringBuilder(50);
            debug.append("Catalog API Method Name [isGiftCardItem] siteId[").append(pSiteId).append("] skuId [")
                            .append(skuId);
            this.logDebug(debug.toString());

        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " isGiftCardItem");
            final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId,
                            BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
            if (skuRepositoryItem == null) {
            	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " isGiftCardItem");
                throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
            }

            if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.GIFT_CERT_SKU_PROPERTY_NAME) != null) {
                final boolean giftCert = ((Boolean) skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.GIFT_CERT_SKU_PROPERTY_NAME)).booleanValue();
                this.logTrace("skuId  gift cert Item" + giftCert);
                return giftCert;
            }

            this.logTrace("skuId is not a gift cert Item");
            return false;
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [isGiftCardItem]: RepositoryException ");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " isGiftCardItem");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
        	debug.append(" Exit");
        	this.logDebug(debug.toString());
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " isGiftCardItem");
        }
    }

    /**
     *  This method identifies whether the skuId passed is eleigible for gift wrap The API reads the giftWrapEligible
     * property of the sku and returns the value. If giftWrapEligible is null it returns false
     *
     * @param pSiteId the site id
     * @param skuId the sku id
     * @return true, if is gift wrap item
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public final boolean isGiftWrapItem(final String pSiteId, final String skuId)
                    throws BBBSystemException, BBBBusinessException {

            final StringBuilder debug = new StringBuilder(30);
            debug.append("Catalog API Method Name [isGiftWrapItem] siteId[").append(pSiteId).append("] skuId [")
                            .append(skuId);
            this.logDebug(debug.toString());

        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " isGiftWrapItem");
            final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId,
                            BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
            if (skuRepositoryItem == null) {
                this.logTrace("skuId Repository item is null ");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " isGiftWrapItem");
                throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
            }

            final boolean isActive = this.isSkuActive(skuRepositoryItem);
            if (!isActive) {
                this.logTrace("skuId is not active. Assuming the sku is not a gift wrap Item");
                return false;
            }

            boolean giftWrapEligible = false;
            if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.GIFT_WRAP_ELIGIBLE_SKU_PROPERTY_NAME) != null) {
                giftWrapEligible = ((Boolean) skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.GIFT_WRAP_ELIGIBLE_SKU_PROPERTY_NAME))
                                .booleanValue();
            }

            this.logTrace("skuId is a gift Wrap Item" + giftWrapEligible);
            return giftWrapEligible;

        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [isGiftWrapItem]: RepositoryException ");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " isGiftWrapItem");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
        	debug.append(" Exit");
        	this.logDebug(debug.toString());
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " isGiftWrapItem");
        }
    }

    /**
     *  The API method reads the registry type applicable for the site and returns the corresponding RegistryTypeVO.
     *
     * @param siteId the site id
     * @return the registry types
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public final List<RegistryTypeVO> getRegistryTypes(final String siteId)
                    throws BBBSystemException, BBBBusinessException {
    	
    	return getSiteRepositoryTools().getRegistryTypes(siteId);
    }

    /**
     *  If the default shipping method is not defined for a site then the method returns BBBBusinessException with an
     * error code indicating that there is no default shipping method configured for the site The Shipping Method
     * details are retrieved from the Repository Item Cache and returned in the method response.
     *
     * @param siteId the site id
     * @return the default shipping method
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public ShipMethodVO getDefaultShippingMethod(final String siteId)
                    throws BBBBusinessException, BBBSystemException {
    	
    	return getSiteRepositoryTools().getDefaultShippingMethod(siteId);
    }

    /**
     *  If the stateid does not exist in the state table then method returns BBBBusinessException with an error code
     * indicating that the state does not exist The state details are read from the Site Repository Item Cache.
     *
     * @param siteId the site id
     * @param stateId the state id
     * @return true, if is nexus state
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */

    @Override
    public final boolean isNexusState(final String siteId, final String stateId)
                    throws BBBBusinessException, BBBSystemException {
    	return getSiteRepositoryTools().isNexusState(siteId, stateId);
    }

    /**
     *  If there are no credit card types configured for a site the method throws a BBBBusinessException with an error
     * code indicating that no credit card types configured If the credit card types are configured, the attribute is
     * read from the Site Repository Item Cache and returned in the method response.
     *
     * @param siteId the site id
     * @return Credit Card Types
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final List<CreditCardTypeVO> getCreditCardTypes(final String siteId)
                    throws BBBBusinessException, BBBSystemException {
    	
    	return getSiteRepositoryTools().getCreditCardTypes(siteId);
    }

    /**
     * Gets the credit card types.
     *
     * @return the creditcardtypes
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    public final List<CreditCardTypeVO> getCreditCardTypes() throws BBBSystemException, BBBBusinessException {
        return this.getCreditCardTypes(SiteContextManager.getCurrentSiteId());
    }

    /**
     *  Get list of states for corresponding domain.
     *
     * @return the state list
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final List<StateVO> getStateList() throws BBBBusinessException, BBBSystemException {

        final String siteId = getCurrentSiteId();
            this.logTrace("Catalog API Method Name [getStateList] siteid " + siteId);

        final List<StateVO> stateVOList = this.getStates(siteId, true,null);
        return stateVOList;
    }

    /**
	 * @return
	 */
	protected String getCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}

    /**
     *  Get list of states for College corresponding domain here we are excluding MilitaryStates.
     *
     * @return the college state list
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
	public final List<StateVO> getCollegeStateList()
			throws BBBBusinessException, BBBSystemException {
		final String siteId = SiteContextManager.getCurrentSiteId();

			this.logTrace("Catalog API Method Name [getCollegeStateList] siteid "
					+ siteId);

		final List<StateVO> stateVOList = this.getStates(siteId, false, null);
		return stateVOList;
	}

    /**
     *  If there are no states configured for a particular site the method throws a BBBBusinessException with an error
     * code indicating that states are not configured The states list returned will include the AFO/FPO states If the
     * states property of the SiteRepository is not null, the list is read from the Site Repository Item Cache.
     *
     * @param siteId the site id
     * @param showMilitaryStates the show military states
     * @param noShowPage the no show page
     * @return the states
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     * @showMilitaryStates 
     */
    @Override
   public List<StateVO> getStates(final String siteId, final boolean showMilitaryStates,  String noShowPage)
                    throws BBBBusinessException, BBBSystemException {

    	return getSiteRepositoryTools().getStates(siteId, showMilitaryStates, noShowPage);
    	 
    }

    /**
     *  The API method returns the details of gift wrap sku corresponding to a site if the site id passed is not present
     * in the repository a BBBBusinessException is thrown indicating the error code.
     *
     * @param siteId the site id
     * @return Gift Wrap VO
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */

    @Override
    public GiftWrapVO getWrapSkuDetails(final String siteId) throws BBBBusinessException, BBBSystemException {
    	//TODO - use this code post-Iliad-Live return getSiteRepositoryTools().getWrapSkuDetails(siteId);
            this.logDebug("Catalog API Method Name [getWrapSku] siteId " + siteId);
        return getSiteRepositoryTools().getWrapSkuDetails(siteId);
    }

    /**
     *  The Rest API method returns the details of gift wrap sku corresponding to a site if the site id passed is not
     * present in the repository a BBBBusinessException is thrown indicating the error code.
     *
     * @return Gift Wrap
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */

    @Override
    public final GiftWrapVO getGiftWrapSKUDetails() throws BBBBusinessException, BBBSystemException {
            this.logDebug("Start Catalog API Method Name [getGiftWrapSKUDetails]");
        GiftWrapVO giftWrapVO = null;

        final String siteId = SiteContextManager.getCurrentSiteId();
        giftWrapVO = this.getWrapSkuDetails(siteId);
            this.logDebug("End Catalog API Method Name [getGiftWrapSKUDetails]");
        return giftWrapVO;

    }

    /**
     *  The API method returns the map of Greetings corresponding to a site if the site id passed is not present in the
     * repository a BBBBusinessException is thrown indicating the error code.
     *
     * @param siteId the site id
     * @return Common Greetings
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final Map<String, String> getCommonGreetings(final String siteId)
                    throws BBBBusinessException, BBBSystemException {
    	
    	return getSiteRepositoryTools().getCommonGreetings(siteId);
    	
    }
    
    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getLTLAssemblyFeeSkuDetails(java.lang.String)
     */
    public LTLAssemblyFeeVO getLTLAssemblyFeeSkuDetails(final String siteId) throws BBBBusinessException, BBBSystemException {
    	//TODO - use this code post-Iliad-Live getSiteRepositoryTools().getLTLAssemblyFeeSkuDetails(siteId);         
    	return getSiteRepositoryTools().getLTLAssemblyFeeSkuDetails(siteId);
    	

    }
   
   
    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getLTLDeliveryChargeSkuDetails(java.lang.String)
     */
    public LTLDeliveryChargeVO getLTLDeliveryChargeSkuDetails(final String siteId) throws BBBBusinessException, BBBSystemException {
    	
    	return getSiteRepositoryTools().getLTLDeliveryChargeSkuDetails(siteId);      
    	
    }

   
    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#isAssemblyFeeOffered(java.lang.String, java.lang.String)
     */
    public boolean isAssemblyFeeOffered (String skuId)
			throws BBBSystemException, BBBBusinessException
 {
			this.logDebug("Catalog API Method Name [isAssemblyFeeOffered] skuId[" + skuId+"]");
		RepositoryItem skuRepositoryItem;
		boolean isAssemblyFeeOffered = false;
		try {
			skuRepositoryItem = this.getCatalogRepository().getItem(skuId,
					BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);

			if (skuRepositoryItem == null) {
				this.logError("Sku not found");
				throw new BBBBusinessException(
						BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
						BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
			}
			if (skuRepositoryItem
					.getPropertyValue(BBBCatalogConstants.IS_ASSEMBLY_FEE_OFFERED) != null) {

				isAssemblyFeeOffered = ((Boolean) skuRepositoryItem
						.getPropertyValue(BBBCatalogConstants.IS_ASSEMBLY_FEE_OFFERED));
			}

		} catch (RepositoryException e) {
			this.logError("Catalog API Method Name [isAssemblyFeeOffered]: RepositoryException ");
			throw new BBBSystemException(
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					e);

		}
		this.logDebug("Exiting Catalog API Method Name [isAssemblyFeeOffered]");
		return isAssemblyFeeOffered;

	}
    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getCaseWeightForSku(java.lang.String, java.lang.String)
     */
    public Double getCaseWeightForSku (String skuId)
			throws BBBSystemException, BBBBusinessException
 {
            this.logDebug("Catalog API Method Name [getCaseWeightForSku] skuId["+skuId+"]");
		RepositoryItem skuRepositoryItem;
		Double caseWeight = null;
		try {
			skuRepositoryItem = this.getCatalogRepository().getItem(skuId,
					BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);

			if (skuRepositoryItem == null) {
				this.logError("Sku not found");
				throw new BBBBusinessException(
						BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
						BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
			}
			if (skuRepositoryItem
					.getPropertyValue(BBBCatalogConstants.SKU_WEIGHT) != null) {

				caseWeight = Double.valueOf((String) skuRepositoryItem
						.getPropertyValue(BBBCatalogConstants.SKU_WEIGHT));
			}

		} catch (RepositoryException e) {
			this.logError("Catalog API Method Name [getCaseWeightForSku]: RepositoryException ");
			throw new BBBSystemException(
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					e);

		}
		this.logDebug("Exiting Catalog API Method Name [getCaseWeightForSku]");
		
		return caseWeight;
	}
    
    /**
     *  If the SKU does not exist in the system then the method will throw a BBBBusinessException with an error code
     * indicating that SKU does not exist If the SKU exist and no shipping methods are configured for the SKU, the
     * method will return all the shipping methods applicable for a site If the SKU contains the shipping methods and
     * the site in which the SKU belongs contains less no of applicable ship methods then the SKU shipping methods are
     * filtered matching against the Site Shipping Methods. Site shipping methods takes precedence over SKU's eligible
     * shipping methods The Shipping Method details are retrieved from the Repository Item Cache and returned as part of
     * the response
     *
     * @param siteId the site id
     * @param skuId the sku id
     * @param sameDayDeliveryFlag the same day delivery flag
     * @return the shipping methods for sku
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final List<ShipMethodVO> getShippingMethodsForSku(final String siteId, final String skuId, boolean sameDayDeliveryFlag)
                    throws BBBBusinessException, BBBSystemException {

    	return getSiteRepositoryTools().getShippingMethodsForSku(siteId, skuId, sameDayDeliveryFlag);
    }


    
    /**
     *  If the ShippingMethodId is invalid the method will throw a BBBBusinessException with an error code indicating
     * that Shipping Method is not valid. The shipping cost for gift card is retrieved using an RQL query and uses the
     * Repository Query Cache
     *
     * @param siteId the site id
     * @param shippingMethodId the shipping method id
     * @return the double
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public double shippingCostForGiftCard(final String siteId, final String shippingMethodId)
                    throws BBBBusinessException, BBBSystemException {
    	
    	return getShippingRepositoryTools().shippingCostForGiftCard(siteId, shippingMethodId);
    }
    
    /**
     * Method to get the promotion from the claimable repository , when we know the coupon ID .
     *
     * @param couponId the coupon id
     * @return RepositoryItem[]
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public  RepositoryItem[] getPromotions(final String couponId) throws BBBSystemException, BBBBusinessException {
        RepositoryItem promotionRepositoryItem[] = null;
        	
            this.logDebug("Catalog API Method Name [getPromotionId] Parameter couponId[" + couponId + "]");
            // Added for My offers Automation... Getting Promotion from claimable repository
            if ((couponId != null) && !StringUtils.isEmpty(couponId)) {
            	try {
            		RepositoryItem coupon =null;
	                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getPromotionId");
	                RepositoryView claimableView;
					claimableView = this.getClaimableTools().getClaimableRepository()
							.getView(CLAIMABLE_ITEM_DESCRIPTOR_NAME);
				
			
					QueryBuilder queryBuilder = claimableView.getQueryBuilder();
					QueryExpression idColumn = queryBuilder
					.createPropertyQueryExpression(this.getClaimableTools().getIdPropertyName());

					QueryExpression idValue = queryBuilder.createConstantQueryExpression(couponId);
					Query claimableQuery = queryBuilder.createPatternMatchQuery(idColumn,
						idValue, 4, true);

					logDebug("Case insensitive coupon lookup query:"
							+ claimableQuery.getQueryRepresentation());

					RepositoryItem[] item= claimableView.executeQuery(claimableQuery);
					if(item != null && item.length > 0){
						coupon = item[0];
					}
				//
                String promotionsName = this.getClaimableTools().getPromotionsPropertyName();
           		vlogDebug("Promotion property name:{0}.", new Object[] { promotionsName });
           		if(coupon!=null){
	           		Set<RepositoryItem> multiplePromotions = (Set<RepositoryItem>) coupon.getPropertyValue(promotionsName);
	           		//
	           		if(multiplePromotions != null){
	           			promotionRepositoryItem = new RepositoryItem[multiplePromotions.size()];
	           			Iterator it = multiplePromotions.iterator();
	           			int count = 0;
						while (it.hasNext()) {
							promotionRepositoryItem[count] = (RepositoryItem) it.next();
							count++;
						}
	           		}
           		}
            }
            catch (RepositoryException e) {
						logError("Error while getting Item form the claimable repository "+e);
			}finally {
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getPromotionId");
            }
        } else {
        	this.logDebug("Catalog API Method Name [getPromotionId] Exit");
            throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                            BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
        }
        return promotionRepositoryItem;
    }
    
    /**
     * For everliving pdp This method gives a list of all applicable rollup values for second roll up when first rollup is selected When a
     * user selects value (firstRollUpValue) corresponding to first rollup,we need all the values of second roll up that
     * are applicable.so if there are 2 roll ups size and color and user selects red color then we need the list of all
     * sizes that are applicable for color red.
     *
     * @param productId the product id
     * @param firstRollUpValue the first roll up value
     * @param firstRollUpType the first roll up type
     * @param secondRollUpType the second roll up type
     * @return the ever living roll up list
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */

    @Override
    public final List<RollupTypeVO> getEverLivingRollUpList(final String productId, final String firstRollUpValue,
                    final String firstRollUpType, final String secondRollUpType)
                    throws BBBSystemException, BBBBusinessException {

            this.logDebug("Catalog API Method Name [getEverLivingRollUpList] Parameter productId[" + productId
                            + "] firstRollUpValue [" + firstRollUpValue + "]" + " firstRollUpType [" + firstRollUpType
                            + "]secondRollUpType [" + secondRollUpType + "]");
        try {

            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getRollUpList");
            final RepositoryItem item = this.getCatalogRepository().getItem(productId,
                            BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
            this.logTrace("product RepositoryItem value  is [" + item + "]");
            final List<RollupTypeVO> secondRollUpAttribute = new ArrayList<RollupTypeVO>();
            if (item == null) {
            	 BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getEverLivingRollUpList");
                throw new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);
            }

            @SuppressWarnings ("unchecked")
            final List<RepositoryItem> skuRepositoryItems = (List<RepositoryItem>) item
                            .getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);
            if (skuRepositoryItems != null) {
                this.logTrace(" sku RepositoryItems List is not null  [" + skuRepositoryItems + "]");
                for (int i = 0; i < skuRepositoryItems.size(); i++) {
                    if (this.isSkuActive(skuRepositoryItems.get(i))) {
                        final String propertyValueFromMap = this.getRollUpSkuPropertyMap().get(
                                        firstRollUpType.toLowerCase());
                        final String propertyValue = (String) skuRepositoryItems.get(i).getPropertyValue(
                                        propertyValueFromMap);
                        this.logTrace(" Value of" + propertyValueFromMap + " for skuid ["
                                        + skuRepositoryItems.get(i).getRepositoryId() + "] is " + propertyValue);
                        if ((propertyValue != null) && propertyValue.equalsIgnoreCase(firstRollUpValue)) {
                            this.logTrace(propertyValue + " for skuid  [" + skuRepositoryItems.get(i).getRepositoryId()
                                            + "] ");
                            final RollupTypeVO rollupTypeVO = new RollupTypeVO();
                            rollupTypeVO.setFirstRollUp(propertyValue);
                            final String secondPropertyValueFromMap = this.getRollUpSkuPropertyMap().get(
                                            secondRollUpType.toLowerCase());
                            final String secondPropertyValue = (String) skuRepositoryItems.get(i).getPropertyValue(
                                            secondPropertyValueFromMap);
                            if (secondPropertyValue != null) {
                                rollupTypeVO.setRollupAttribute(secondPropertyValue);
                            }
                            if (skuRepositoryItems.get(i).getPropertyValue(
                                            BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME) != null) {

                                final String swatchImagePath = (String) skuRepositoryItems.get(i).getPropertyValue(
                                                BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME);
                                rollupTypeVO.setSwatchImagePath(swatchImagePath);
                            }

                            if (skuRepositoryItems.get(i).getPropertyValue(
                                            BBBCatalogConstants.LARGE_IMAGE_PROPERTY_NAME) != null) {

                                final String largeImagePath = (String) skuRepositoryItems.get(i).getPropertyValue(
                                                BBBCatalogConstants.LARGE_IMAGE_PROPERTY_NAME);
                                rollupTypeVO.setLargeImagePath(largeImagePath);
                            }

                            if (skuRepositoryItems.get(i).getPropertyValue(
                                            BBBCatalogConstants.THUMBNAIL_IMAGE_PROPERTY_NAME) != null) {

                                final String thumnailImagePath = (String) skuRepositoryItems.get(i).getPropertyValue(
                                                BBBCatalogConstants.THUMBNAIL_IMAGE_PROPERTY_NAME);
                                rollupTypeVO.setThumbnailImagePath(thumnailImagePath);
                            }

                            if (skuRepositoryItems.get(i).getPropertyValue(
                                            BBBCatalogConstants.SMALL_IMAGE_PROPERTY_NAME) != null) {

                                final String smallImagePath = (String) skuRepositoryItems.get(i).getPropertyValue(
                                                BBBCatalogConstants.SMALL_IMAGE_PROPERTY_NAME);
                                rollupTypeVO.setSmallImagePath(smallImagePath);
                            }

                            secondRollUpAttribute.add(rollupTypeVO);
                        }
                    } else {
                        this.logTrace("skuId  " + skuRepositoryItems.get(i).getRepositoryId()
                                        + " is inactive so excluding from rollup list");
                    }
                }
                // sorting color/finish alphabetically
                if ((null != secondRollUpType)
                                && ("COLOR".equalsIgnoreCase(secondRollUpType)) || "FINISH".equalsIgnoreCase(secondRollUpType)) {
                    Collections.sort(secondRollUpAttribute);
                }
            }
            return secondRollUpAttribute;
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getEverLivingRollUpList]: RepositoryException ");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getEverLivingRollUpList");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
        	this.logDebug("Catalog API Method Name [getEverLivingRollUpList] Parameter productId[" + productId
                    + "] firstRollUpValue [" + firstRollUpValue + "]" + " firstRollUpType [" + firstRollUpType
                    + "]secondRollUpType [" + secondRollUpType + "] Exit");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getEverLivingRollUpList");
        }
    }

    /**
     *  This method gives a list of all applicable rollup values for second roll up when first rollup is selected When a
     * user selects value (firstRollUpValue) corresponding to first rollup,we need all the values of second roll up that
     * are applicable.so if there are 2 roll ups size and color and user selects red color then we need the list of all
     * sizes that are applicable for color red.
     *
     * @param productId the product id
     * @param firstRollUpValue the first roll up value
     * @param firstRollUpType the first roll up type
     * @param secondRollUpType the second roll up type
     * @return the roll up list
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */

    @Override
    public final List<RollupTypeVO> getRollUpList(final String productId, final String firstRollUpValue,
                    final String firstRollUpType, final String secondRollUpType)
                    throws BBBSystemException, BBBBusinessException {
            this.logTrace("Catalog API Method Name [getRollUpList] Parameter productId[" + productId
                            + "] firstRollUpValue [" + firstRollUpValue + "]" + " firstRollUpType [" + firstRollUpType
                            + "]secondRollUpType [" + secondRollUpType + "]");
        try {

            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getRollUpList");
            final RepositoryItem item = this.getCatalogRepository().getItem(productId,
                            BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
            this.logTrace("product RepositoryItem value  is [" + item + "]");
            final List<RollupTypeVO> secondRollUpAttribute = new ArrayList<RollupTypeVO>();
            if (item == null) {
            	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getRollUpList");
                throw new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);
            }

            final boolean isActive = this.isProductActive(item);
            this.logTrace(productId + " Product is active [" + isActive + "]");
            if (!isActive) {
            	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getRollUpList");
                throw new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY,
                                BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY);
            }

            @SuppressWarnings ("unchecked")
            final List<RepositoryItem> skuRepositoryItems = (List<RepositoryItem>) item
                            .getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);
            if (skuRepositoryItems != null) {
                this.logTrace(" sku RepositoryItems List is not null  [" + skuRepositoryItems + "]");
                for (int i = 0; i < skuRepositoryItems.size(); i++) {
                    if (this.isSkuActive(skuRepositoryItems.get(i))) {
                        final String propertyValueFromMap = this.getRollUpSkuPropertyMap().get(
                                        firstRollUpType.toLowerCase());
                        final String propertyValue = (String) skuRepositoryItems.get(i).getPropertyValue(
                                        propertyValueFromMap);
                        this.logTrace(" Value of" + propertyValueFromMap + " for skuid ["
                                        + skuRepositoryItems.get(i).getRepositoryId() + "] is " + propertyValue);
                        if ((propertyValue != null) && propertyValue.equalsIgnoreCase(firstRollUpValue)) {
                            this.logTrace(propertyValue + " for skuid  [" + skuRepositoryItems.get(i).getRepositoryId()
                                            + "] ");
                            final RollupTypeVO rollupTypeVO = new RollupTypeVO();
                            rollupTypeVO.setFirstRollUp(propertyValue);
                            final String secondPropertyValueFromMap = this.getRollUpSkuPropertyMap().get(
                                            secondRollUpType.toLowerCase());
                            final String secondPropertyValue = (String) skuRepositoryItems.get(i).getPropertyValue(
                                            secondPropertyValueFromMap);
                            if (secondPropertyValue != null) {
                                rollupTypeVO.setRollupAttribute(secondPropertyValue);
                            }
                            if (skuRepositoryItems.get(i).getPropertyValue(
                                            BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME) != null) {

                                final String swatchImagePath = (String) skuRepositoryItems.get(i).getPropertyValue(
                                                BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME);
                                rollupTypeVO.setSwatchImagePath(swatchImagePath);
                            }

                            if (skuRepositoryItems.get(i).getPropertyValue(
                                            BBBCatalogConstants.LARGE_IMAGE_PROPERTY_NAME) != null) {

                                final String largeImagePath = (String) skuRepositoryItems.get(i).getPropertyValue(
                                                BBBCatalogConstants.LARGE_IMAGE_PROPERTY_NAME);
                                rollupTypeVO.setLargeImagePath(largeImagePath);
                            }

                            if (skuRepositoryItems.get(i).getPropertyValue(
                                            BBBCatalogConstants.THUMBNAIL_IMAGE_PROPERTY_NAME) != null) {

                                final String thumnailImagePath = (String) skuRepositoryItems.get(i).getPropertyValue(
                                                BBBCatalogConstants.THUMBNAIL_IMAGE_PROPERTY_NAME);
                                rollupTypeVO.setThumbnailImagePath(thumnailImagePath);
                            }

                            if (skuRepositoryItems.get(i).getPropertyValue(
                                            BBBCatalogConstants.SMALL_IMAGE_PROPERTY_NAME) != null) {

                                final String smallImagePath = (String) skuRepositoryItems.get(i).getPropertyValue(
                                                BBBCatalogConstants.SMALL_IMAGE_PROPERTY_NAME);
                                rollupTypeVO.setSmallImagePath(smallImagePath);
                            }

                            secondRollUpAttribute.add(rollupTypeVO);
                        }
                    } else {
                        this.logTrace("skuId  " + skuRepositoryItems.get(i).getRepositoryId()
                                        + " is inactive so excluding from rollup list");
                    }
                }
                // sorting color/finish alphabetically
                if ((null != secondRollUpType)
                                && ("COLOR".equalsIgnoreCase(secondRollUpType)) || "FINISH".equalsIgnoreCase(secondRollUpType)) {
                    Collections.sort(secondRollUpAttribute);
                }
            }
            return secondRollUpAttribute;
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getRollUpList]: RepositoryException ");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getRollUpList");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getRollUpList");
        }
    }

    /**
     *  This method returns a List of ProductVO for a given college id and siteId get the skuId which have the input
     * college id. then for each skuId get the set of parent products for each parent product in the set call the
     * productDetails API and add the productVO returned in the list to be returned
     *
     * @param collegeId the college id
     * @param siteId the site id
     * @return the college product
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */

    @Override
    public  List<ProductVO> getCollegeProduct(final String collegeId, final String siteId)
                    throws BBBBusinessException, BBBSystemException {

            final StringBuilder debug = new StringBuilder(30);
            debug.append("Catalog API Method Name [getCollegeProduct] siteId[").append(siteId).append("] collegeId [")
                            .append(collegeId);
            this.logDebug(debug.toString());

        if ((collegeId != null) && (siteId != null) && !StringUtils.isEmpty(siteId) && !StringUtils.isEmpty(collegeId)) {
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getCollegeProduct");

                final List<ProductVO> collegeProductsList = new ArrayList<ProductVO>();

                String productId = null;

                final Object[] param = new Object[1];
                param[0] = collegeId;

                final List<String> maxProduct = this.getAllValuesForKey(this.getConfigType(),
                                this.getMaxProductsPerCollegeKey());
                final int maxProductsPerCollege = Integer.parseInt(maxProduct.get(0));

                final RepositoryItem[] collegeProductItem = this.executeRQLQuery(this.getCollegeQuery(), param,
                                BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR, this.getCatalogRepository());
                this.logTrace("collegSkuItem value[" + Arrays.toString(collegeProductItem) + "]");
                if ((collegeProductItem != null) && (collegeProductItem.length > 0)) {

                    for (int i = 0; i < collegeProductItem.length; i++) {
                        if (this.isProductActive(collegeProductItem[i])) {
                            if ((collegeProductItem[i] != null) && (i <= maxProductsPerCollege)) {
                                productId = collegeProductItem[i].getRepositoryId();
                                collegeProductsList.add(this.getProductDetails(siteId, productId));
                                this.logTrace(" parent product id [" + productId + "] is active ");
                            } else {
                                break;
                            }
                        } else {
                            this.logTrace("product id [" + productId + "] is not active ");
                        }
                    }
                    this.logTrace("total products for the college id is [" + collegeProductsList.size() + "]");
                    return collegeProductsList;
                }
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCollegeProduct");
                throw new BBBBusinessException(
                                BBBCatalogErrorCodes.NO_PRODUCT_AVAILABLE_IN_REPOSITORY_FOR_GIVEN_COLLEGE_ID,
                                BBBCatalogErrorCodes.NO_PRODUCT_AVAILABLE_IN_REPOSITORY_FOR_GIVEN_COLLEGE_ID);
            } finally {
            	debug.append(" Exit");
            	this.logDebug(debug.toString());
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCollegeProduct");
            }
        }
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }

    /**
     * Execute rql query.
     *
     * @param rqlQuery the rql query
     * @param viewName the view name
     * @param repository the repository
     * @return Repository Items Result
     * @throws RepositoryException the repository exception
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    public RepositoryItem[] executeRQLQuery(final String rqlQuery, final String viewName,
                    final MutableRepository repository)
                    throws RepositoryException, BBBSystemException, BBBBusinessException {
        return this.executeRQLQuery(rqlQuery, new Object[1], viewName, repository);
    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#executeRQLQuery(java.lang.String, java.lang.String)
     */
    @Override
    public final RepositoryItem[] executeRQLQuery(final String rqlQuery, final String viewName)
                    throws RepositoryException, BBBSystemException, BBBBusinessException {
        return this.executeRQLQuery(rqlQuery, new Object[1], viewName, this.getCatalogRepository());
    }

    /**
     *  This method checks if sku is active sku is active if all the following conditions is true if weboffered flag is
     * true and disable is false and current date lies between start and end date of the sku.
     *
     * @param siteId the site id
     * @param skuId the sku id
     * @return true, if is SKU available
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public final boolean isSKUAvailable(final String siteId, final String skuId)
                    throws BBBSystemException, BBBBusinessException {

            final StringBuilder debug = new StringBuilder(30);
            debug.append("Catalog API Method Name [isSKUAvailable] siteId[ ").append(siteId).append("] skuId [")
                            .append(skuId);
            this.logDebug(debug.toString());

        if ((siteId != null) && !StringUtils.isEmpty(siteId)) {
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " isSKUAvailable");
                final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId,
                                BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
                return this.isSkuActive(skuRepositoryItem);

            } catch (final RepositoryException e) {
                this.logError("Catalog API Method Name [isSKUAvailable]: RepositoryException ");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " isSKUAvailable");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
            } finally {
            	debug.append(" Exit");
            	this.logDebug(debug.toString());
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " isSKUAvailable");
            }
        }

        this.logDebug("Input parameter siteId is null");
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }

    /**
     *  This method returns the default country for a given site id If the default country is not present it returns
     * null.
     *
     * @param siteId the site id
     * @return Default Country for Site
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public String getDefaultCountryForSite(final String siteId) throws BBBBusinessException, BBBSystemException {
    	//TODO - use this code post-Iliad-Live return getSiteRepositoryTools().getDefaultCountryForSite(siteId);
    	return getSiteRepositoryTools().getDefaultCountryForSite(siteId);
    }

    /**
     *  To get the category id of the first college category in the catalog for the site.
     *
     * @param siteId the site id
     * @return College ID
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    public final String getCollegeIdForSite(final String siteId) throws BBBBusinessException, BBBSystemException {
        this.logDebug("Catalog API Method Name [getCollegeIdForSite] siteId" + siteId);
        if ((siteId != null) && !StringUtils.isEmpty(siteId)) {
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getCollegeIdForSite");
                final List<String> configRepoList = this.getAllValuesForKey(this.getConfigType(), siteId.trim()
                                + "RootCategory");

                final String commerceRootCatId = configRepoList.get(0);
                this.logTrace("commerceRootCatId value [" + commerceRootCatId + "]");
                final RepositoryItem categoryRepositoryItem = this.getCatalogRepository().getItem(commerceRootCatId,
                                BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
                if (categoryRepositoryItem != null) {
                    @SuppressWarnings ("unchecked")
                    final List<RepositoryItem> subCategories = (List<RepositoryItem>) categoryRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.CHILD_CATEGORIES_PROPERTY_NAME);
                    if ((subCategories != null) && !subCategories.isEmpty()) {
                        for (int i = 0; i < subCategories.size(); i++) {
                            boolean isCollege = false;
                            if (subCategories.get(i)
                                            .getPropertyValue(BBBCatalogConstants.COLLEGE_PRODUCT_PROPERTY_NAME) != null) {
                                isCollege = ((Boolean) subCategories.get(i).getPropertyValue(
                                                BBBCatalogConstants.COLLEGE_PRODUCT_PROPERTY_NAME)).booleanValue();
                            }

                            if (isCollege) {
                                return subCategories.get(i).getRepositoryId();
                            }
                        }
                        BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCollegeIdForSite");
                        throw new BBBBusinessException(BBBCatalogErrorCodes.NO_DEFAULT_COLLEGE_ID_FOR_SITE,
                                        BBBCatalogErrorCodes.NO_DEFAULT_COLLEGE_ID_FOR_SITE);
                    }
                    BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCollegeIdForSite");
                    throw new BBBBusinessException(BBBCatalogErrorCodes.NO_DEFAULT_COLLEGE_ID_FOR_SITE,
                                    BBBCatalogErrorCodes.NO_DEFAULT_COLLEGE_ID_FOR_SITE);
                }
                this.logTrace("COMMERCE root category id " + commerceRootCatId + " not in repository");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCollegeIdForSite");
                throw new BBBBusinessException(BBBCatalogErrorCodes.COMMERCE_ROOT_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.COMMERCE_ROOT_NOT_AVAILABLE_IN_REPOSITORY);
            } catch (final RepositoryException e) {
                this.logError("Catalog API Method Name [getCollegeIdForSite]: RepositoryException ");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCollegeIdForSite");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
            } finally {
            	this.logDebug("Catalog API Method Name [getCollegeIdForSite] siteId" + siteId + " Exit");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCollegeIdForSite");
            }
        }
        this.logDebug("Input parameter siteId is null");
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }

    /**
     *  The method gets the 1st parent product of the sku.
     *
     * @param skuId the sku id
     * @return the parent product for sku
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public String getParentProductForSku(final String skuId) throws BBBBusinessException, BBBSystemException {
        this.logDebug("Catalog API Method Name [getParentProductForSku] skuId " + skuId);
        String parentProductId = EMPTYSTRING;       
        if (BBBUtility.isNotEmpty(skuId)) {
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getParentProductForSku");
                final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId.trim(),
                                BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
                if (skuRepositoryItem == null) {
                    this.logDebug("Repository Item is null for sku id " + skuId);
                    BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getParentProductForSku");
                    throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
                                    BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
                }
                @SuppressWarnings ("unchecked")
                final Set<RepositoryItem> parentProduct = (Set<RepositoryItem>) skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME);
                if ((parentProduct != null) && !parentProduct.isEmpty()) {
                    for (final RepositoryItem productRepositoryItem : parentProduct) {
                        if (this.isProductActive(productRepositoryItem)) {
                            parentProductId = productRepositoryItem.getRepositoryId();
                            this.logTrace("parent Product Id[" + parentProductId + "]");
                            break;
                        }
                    }
                }
                return parentProductId;
            } catch (final RepositoryException e) {
            	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getParentProductForSku");
                this.logError("Catalog API Method Name [getParentProductForSku]: RepositoryException ");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                		BBBCatalogConstants.DATA_UNAVAILABLE_IN_REPOSITORY, e);
            } finally {
            	this.logDebug("Catalog API Method Name [getParentProductForSku] skuId " + skuId + " Exit");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getParentProductForSku");
            }
        }
        this.logDebug("Input parameter siteId is null");
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }
    
    /**
     *  The method gets the 1st parent product item of the sku.
     *
     * @param skuId the sku id
     * @return the parent product item for sku
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public  RepositoryItem getParentProductItemForSku(final String skuId) throws BBBBusinessException, BBBSystemException {
        this.logDebug("Catalog API Method Name [getParentProductItemForSku] skuId " + skuId);
        String parentProductId = EMPTYSTRING;
        RepositoryItem parentProductItem = null;
        if ((skuId != null) && !StringUtils.isEmpty(skuId)) {
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getParentProductForSku");
                final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId.trim(),
                                BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
                if (skuRepositoryItem == null) {
                    this.logDebug("Repository Item is null for sku id " + skuId);
                    BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getParentProductForSku");
                    throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
                                    BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
                }
                @SuppressWarnings ("unchecked")
                final Set<RepositoryItem> parentProduct = (Set<RepositoryItem>) skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME);
                if ((parentProduct != null) && !parentProduct.isEmpty()) {
                    for (RepositoryItem productRepositoryItem : parentProduct) {
                        if (this.isProductActive(productRepositoryItem)) {
                            parentProductId = productRepositoryItem.getRepositoryId();
                            parentProductItem = productRepositoryItem;
                            this.logTrace("parent Product Id[" + parentProductId + "]");
                            break;
                        }
                    }
                }
                return parentProductItem;
            } catch (final RepositoryException e) {
            	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getParentProductForSku");
                this.logError("Catalog API Method Name [getParentProductItemForSku]: RepositoryException ");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                		BBBCatalogConstants.DATA_UNAVAILABLE_IN_REPOSITORY, e);
            } finally {
            	this.logDebug("Catalog API Method Name [getParentProductItemForSku] skuId " + skuId + " Exit");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getParentProductForSku");
            }
        }
        this.logDebug("Input parameter siteId is null");
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }
    
    /**
     *  The method gets the 1st parent product of the sku.Does not check for active parent. The new method is introduced
     * for gift Registry sorting . The boolean activeParentNotReqd should be sent as true
     *
     * @param skuId the sku id
     * @param activeParentNotReqd the active parent not reqd
     * @return the parent product for sku
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final String getParentProductForSku(final String skuId, final boolean activeParentNotReqd)
                    throws BBBBusinessException, BBBSystemException {

            final StringBuilder debug = new StringBuilder(30);
            debug.append("Catalog API Method Name [getParentProductForSku] skuId[").append(skuId)
                            .append("] activeParentNotReqd??").append(activeParentNotReqd);
            this.logDebug(debug.toString());

        String parentProductId = EMPTYSTRING;
        if ((skuId != null) && !StringUtils.isEmpty(skuId)) {
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getParentProductForSku");
                final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId.trim(),
                                BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
                if (skuRepositoryItem == null) {
                    this.logDebug("Repository Item is null");
                    BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getParentProductForSku");
                    throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
                                    BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
                }
                @SuppressWarnings ("unchecked")
                final Set<RepositoryItem> parentProduct = (Set<RepositoryItem>) skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME);
                if ((parentProduct != null) && !parentProduct.isEmpty()) {
                    for (final RepositoryItem productRepositoryItem : parentProduct) {
                        parentProductId = productRepositoryItem.getRepositoryId();
                        this.logTrace("parent Product Id[" + parentProductId + "]");
                        break;
                    }
                }
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getParentProductForSku");
                return parentProductId;
            } catch (final RepositoryException e) {
                this.logError("Catalog API Method Name [getParentProductForSku]: RepositoryException ");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getParentProductForSku");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                		BBBCatalogConstants.DATA_UNAVAILABLE_IN_REPOSITORY, e);
            } finally {
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getParentProductForSku");
            }
        }
        this.logDebug("Input parameter siteId is null");
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }

    /**
     *  The method returns the imageVo corresponding a sku.
     *
     * @param skuId the sku id
     * @return the sku images
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final ImageVO getSkuImages(final String skuId) throws BBBBusinessException, BBBSystemException {
        this.logDebug("Catalog API Method Name [getSkuImages] skuId " + skuId);
        if ((skuId != null) && !StringUtils.isEmpty(skuId)) {
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getSkuImages");
                final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId.trim(),
                                BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
                if (skuRepositoryItem == null) {
                    this.logDebug("Sku Repository Item is null for sku :" + skuId);
                    BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSkuImages");
                    throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
                                    BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
                }
                final ImageVO skuImages = new ImageVO();

                skuImages.setLargeImage(skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.LARGE_IMAGE_IMAGE_PROPERTY_NAME) != null ? (String) skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.LARGE_IMAGE_IMAGE_PROPERTY_NAME) : EMPTYSTRING);
                skuImages.setSmallImage(skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_IMAGE_PROPERTY_NAME) != null ? (String) skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_IMAGE_PROPERTY_NAME) : EMPTYSTRING);

                skuImages.setSwatchImage(skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME) != null ? (String) skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME) : EMPTYSTRING);
                skuImages.setMediumImage(skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME) != null ? (String) skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME) : EMPTYSTRING);
                skuImages.setThumbnailImage(skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME) != null ? (String) skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME) : EMPTYSTRING);
                if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.ZOOM_INDEX_PATH_PRODUCT_PROPERTY_NAME) != null) {
                    skuImages.setZoomImageIndex(((Integer) skuRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.ZOOM_INDEX_PATH_PRODUCT_PROPERTY_NAME))
                                    .intValue());
                }
                skuImages.setZoomImage(skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.ZOOM_IMAGE_IMAGE_PROPERTY_NAME) != null ? (String) skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.ZOOM_IMAGE_IMAGE_PROPERTY_NAME) : EMPTYSTRING);
                if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.ANYWHERE_ZOOM_PRODUCT_PROPERTY_NAME) != null) {
                    skuImages.setAnywhereZoomAvailable(((Boolean) skuRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.ANYWHERE_ZOOM_PRODUCT_PROPERTY_NAME))
                                    .booleanValue());
                }
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSkuImages");
                return skuImages;
            } catch (final RepositoryException e) {
                this.logError("Catalog API Method Name [getSkuImages]: RepositoryException ");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSkuImages");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                		BBBCatalogConstants.DATA_UNAVAILABLE_IN_REPOSITORY, e);
            } finally {
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSkuImages");
            }
        }
        this.logDebug("Input parameter siteId is null");
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }

    /**
     *  The method gets the root college category id from the config repository.
     *
     * @param pSiteId the site id
     * @return the root college id frm config
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public final String getRootCollegeIdFrmConfig(final String pSiteId) throws BBBSystemException, BBBBusinessException {
        this.logDebug("Catalog API Method Name [getRootCollegeIdFrmConfig]configtype " + this.getConfigType());

        List<String> collegeList = null;

        try {
            if (BBBCatalogConstants.BED_BATH_US.equalsIgnoreCase(pSiteId) || TBSConstants.SITE_TBS_BAB_US.equalsIgnoreCase(pSiteId)) {
                collegeList = this.getAllValuesForKey(this.getConfigType(), this.getCollegeConfigKey());

            } else {
                collegeList = this.getAllValuesForKey(this.getConfigType(), this.getCollegeConfigKeyCanada());
            }

        } catch (final BBBBusinessException e) {
            this.logError(" exception while fetching root college id " + e.getMessage(), e);
        }

        if ((collegeList != null) && !collegeList.isEmpty()) {
            return collegeList.get(0);
        }
        this.logDebug("College root id is not defined in configure repository");
        throw new BBBSystemException(BBBCatalogErrorCodes.ROOT_COLLEGE_ID_NOT_FOUND,
                        BBBCatalogErrorCodes.ROOT_COLLEGE_ID_NOT_FOUND);
    }

    /**
     *  The method returns value from config repository for the type content and catalog.
     *
     * @param key the key
     * @return the content catalog configration
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public  List<String> getContentCatalogConfigration(final String key)
                    throws BBBSystemException, BBBBusinessException {

        List<String> configrationValue = new ArrayList<String>();
        if ((key != null) && !StringUtils.isEmpty(key)) {
            configrationValue = this.getAllValuesForKey(this.getConfigType(), key);
        }
        return configrationValue;

    }

    /**
     *  this method checks if the particular sku needs to be excluded from the promotion or not. The skuid and promo code
     * is first queried in the coupon repository. if return is not null then sku is excluded. else get the vendor id and
     * jda dept of the sku and again query the coupon repository.if return is null sku is not excluded else sku is
     * excluded
     *
     * @param skuId the sku id
     * @param promotionCode the promotion code
     * @param isAppliedCoupon the is applied coupon
     * @return true, if is sku excluded
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public boolean isSkuExcluded(final String skuId, final String promotionCode, boolean isAppliedCoupon)
                    throws BBBSystemException, BBBBusinessException {
    	boolean isCouponApplied = isAppliedCoupon;
        this.logDebug("Catalog API Method Name [isSkuExcluded] : Start");
        // R2.1.1 Scope #522 : Coupons requirement + refactored entire Function - Start
        if (!StringUtils.isEmpty(promotionCode) && !StringUtils.isEmpty(skuId)) {
            this.logTrace("promotionCode:" + promotionCode + ",SkuId:" + skuId);

            RepositoryItem[] couponRuleItem;
            String jdaDeptId = null;
            String jdaSubDeptId = null;
            RepositoryItem jdaDeptItem = null;
            RepositoryItem jdaSubDeptItem = null;
            
            final Object[] params = new Object[9];
            try {

                final RepositoryItem skuRepositoryItem = this.catalogRepository.getItem(skuId,
                                BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
                if (skuRepositoryItem == null) {
                    this.logTrace("SKU id not in Repository " + skuId);
                    this.logDebug("Catalog API Method Name [isSkuExcluded] : End");
                    return false;
                }

                final String vendorId = (String) skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME);
                jdaDeptItem = (RepositoryItem) skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME);
                jdaSubDeptItem = (RepositoryItem) skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME);
                final String jdaClass = (String) skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME);

                this.logTrace("SKU : VendorId=" + vendorId + ", jdaClass=" + jdaClass);

                if (jdaDeptItem != null) {
                    jdaDeptId = jdaDeptItem.getRepositoryId();
                    this.logTrace("jdaDeptId=" + jdaDeptId);
                }
                if (jdaSubDeptItem != null) {
                    jdaSubDeptId = jdaSubDeptItem.getRepositoryId();
                    this.logTrace("jdaSubDeptId=" + jdaSubDeptId);
                }
                
                String siteId = null;
        		
        		if(null != ServletUtil.getCurrentRequest()) {
                    siteId = (String) ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID);
        		}

        		if (BBBUtility.isEmpty(siteId)){
                    siteId = getCurrentSiteId();
        		}
                // Fetch Rules for the Coupon Code
                params[0] = promotionCode;
                params[1] = BBBCoreConstants.EXCLUSION_RULE;
                params[2] = siteId;
        
                if(this.isLogCouponDetails() && isCouponApplied){
					this.logInfo("SKU Properties to be checked for inclusion : VendorId=" + vendorId +", jdaDeptId=" + jdaDeptId 
							+ ", jdaSubDeptId=" + jdaSubDeptId + ", jdaClass=" + jdaClass);
					isCouponApplied= false;
        		}
				params[3] = skuId;
				params[4] = jdaDeptId;
				params[5] = jdaSubDeptId;
				params[6] = jdaClass;
				params[7] = vendorId;
				params[8] = BBBCoreConstants.STRING_ZERO;
				
				this.logDebug("Passing parameters for sku Exclusion : promotionCode=" + promotionCode + " ,skuId = " + skuId);
				couponRuleItem = this.executeRQLQuery(this.getCouponRuleSkuQuery(), params,
						BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR, this.getCouponRepository());
				if (couponRuleItem != null && couponRuleItem.length > 0) {
					this.logDebug("Exclusion Rule Applicable for  sku " + skuId);
					return true;
				}
				
				this.logDebug("Passing parameters for sku Exclusion : promotionCode=" + promotionCode + " ,VendorId = " + vendorId + " ,DeptId=" + "0");
				couponRuleItem = this.executeRQLQuery(this.getCouponRuleVendorDeptQuery(), params,
						BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR, this.getCouponRepository());
				
				if (couponRuleItem != null && couponRuleItem.length > 0) {
					this.logDebug("Exclusion Rule Applicable for  sku " + skuId);
					return true;
				}
				
				this.logDebug("Passing parameters for sku Exclusion : promotionCode=" + promotionCode + " ,VendorId = " + vendorId + " or 0" + " ,DeptId = " + jdaDeptId + " ,SubDeptId=" + jdaSubDeptId + " ,jdaClass=" + jdaClass);
				couponRuleItem = this.executeRQLQuery(this.getCouponRuleVendorDeptClassQuery(), params,
						BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR, this.getCouponRepository());
				
				if (couponRuleItem != null && couponRuleItem.length > 0) {
						this.logDebug("Exclusion Rule Applicable for  sku " + skuId);
						return true;
				}
				
            } catch (final RepositoryException rpex) {
                this.logError("Catalog API Method Name [isSkuExcluded]: RepositoryException ");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, rpex);
            }
        }

        this.logDebug("Coupon or Sku were null or No Rule(s) found for the Coupon");
        this.logDebug("Catalog API Method Name [isSkuExcluded] : End");
		return false;
        // R2.1.1 Scope #522 : Coupons requirement + refactored entire Function - End
    }

    /**
     *  This method implements the logic to get the sku with the highest inventory All the active child skus are added to
     * a list and then getMaxStockSku is called from inventory manager to get the id of max inventory sku.
     *
     * @param productVO the product vo
     * @param siteId the site id
     * @return the max inventory sku for product
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */

    @Override
    public final String getMaxInventorySKUForProduct(ProductVO productVO, final String siteId)
                    throws BBBBusinessException, BBBSystemException {
        String maxInventorySkuId = null;
        if ((siteId != null) && !StringUtils.isEmpty(siteId)) {
            	final List<String> childSkus=productVO.getChildSKUs();
                    if ((childSkus != null) && !childSkus.isEmpty()) {
                        final List<String> skuIds = new ArrayList<String>();
                        for (final String childSku : childSkus) {
                            if (this.isSKUAvailable(siteId,childSku)) {
                                this.logTrace("adding child sku " + childSku
                                                + " to list of sku ids to check max inventory");
                                skuIds.add(childSku);
                            } else {
                                this.logTrace(" ignoring sku id to check max inventory " + childSku
                                                + " as sku is inactive for site " + siteId);
                            }
                        }
                        if (!skuIds.isEmpty()) {
                            maxInventorySkuId = this.getInventoryManager().getMaxStockSku(skuIds, siteId);
                        } else {
                            this.logTrace(" No active sku available for product id " + productVO.getProductId()
                                            + " to get max inventory sku");
                            throw new BBBBusinessException(BBBCatalogErrorCodes.NO_ACTIVE_SKU_FOR_MAX_INVENTORY,
                                            BBBCatalogErrorCodes.NO_ACTIVE_SKU_FOR_MAX_INVENTORY);
                        }
                    } else {
                        this.logTrace(" No child sku available for product id " +  productVO.getProductId()
                                        + " to get max inventory sku");
                        throw new BBBBusinessException(BBBCatalogErrorCodes.NO_CHILD_SKU_AVAILABLE_FOR_PRODUCT,
                                        BBBCatalogErrorCodes.NO_CHILD_SKU_AVAILABLE_FOR_PRODUCT);
                    }
                } 

        return maxInventorySkuId;

    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getSiteChatAttributes(java.lang.String)
     */
    @Override
    public final SiteChatAttributesVO getSiteChatAttributes(final String siteId) throws BBBSystemException {
    	
    	return getSiteRepositoryTools().getSiteChatAttributes(siteId);
    }
	
	// Added for 117-A5 Story
	/* (non-Javadoc)
	 * @see com.bbb.commerce.catalog.BBBCatalogTools#getGridListAttributes(java.lang.String)
	 */
	@Override
	public String getGridListAttributes(String siteId)
			throws BBBSystemException {

		return getSiteRepositoryTools().getGridListAttributes(siteId);
	}
	
    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getTimeZones(java.lang.String)
     */
    @Override
    public final List<String> getTimeZones(final String siteId) throws BBBSystemException, BBBBusinessException {

        this.logDebug("Catalog API Method Name [getTimeZones] site id " + siteId);
        RepositoryItem siteConfiguration = null;
        final List<String> timeZoneList = new ArrayList<String>();
        try {
            siteConfiguration = this.siteRepository.getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
            if (siteConfiguration == null) {
                throw new BBBSystemException(BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY);
            }
            @SuppressWarnings ("unchecked")
            final Set<RepositoryItem> timeZoneRepItems = (Set<RepositoryItem>) siteConfiguration
                            .getPropertyValue(BBBCatalogConstants.TIMEZONES_PROPERTY_NAME);
            if (timeZoneRepItems != null) {
                for (final RepositoryItem timeZoneRepoitem : timeZoneRepItems) {
                    timeZoneList.add(timeZoneRepoitem.getPropertyValue(BBBCatalogConstants.TIMEZONE_PROPERTY_NAME)
                                    .toString());
                }
            } else {
                throw new BBBSystemException(BBBCatalogErrorCodes.TIMEZONE_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.TIMEZONE_NOT_AVAILABLE_IN_REPOSITORY);
            }
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getTimeZones]: RepositoryException ");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        }
        return timeZoneList;

    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getcustomerCareEmailAddress(java.lang.String)
     */
    @Override
    public final String getcustomerCareEmailAddress(final String siteId)
                    throws BBBSystemException, BBBBusinessException {

        RepositoryItem siteConfiguration = null;
        String customerCareEmailAddress = EMPTYSTRING;
        this.logDebug("Catalog API Method Name [getcustomerCareEmailAddress] siteid " + siteId);

        try {
            siteConfiguration = this.siteRepository.getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);

            if (siteConfiguration == null) {
                throw new BBBSystemException(BBBCoreErrorConstants.CATALOG_ERROR_1009,
                                "no rows fetched for given site id");
            }
            this.logDebug((String) siteConfiguration.getPropertyValue("customerCareEmailAddress"));

            customerCareEmailAddress = (String) siteConfiguration.getPropertyValue("customerCareEmailAddress");
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getcustomerCareEmailAddress]: RepositoryException ");
            throw new BBBSystemException(BBBCoreErrorConstants.CATALOG_ERROR_1013,
                            "Repository Exception while getting chat attributes", e);
        }
        this.logDebug("End of method getcustomerCareEmailAddress");
        return customerCareEmailAddress;

    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getAllConfigKeys()
     */
    @Override
    public final Map<String, Map<String, String>> getAllConfigKeys() throws BBBSystemException, BBBBusinessException {

        final Map<String, Map<String, String>> allConfigKeys = new HashMap<String, Map<String, String>>();
        RepositoryView mView = null;
        RepositoryItem[] configRepositoryItems = null;
        QueryBuilder mQueryBuilder = null;

        try {

            mView = this.getConfigureRepository().getView("configKeys");
            mQueryBuilder = mView.getQueryBuilder();
            final Query query = mQueryBuilder.createUnconstrainedQuery();
            configRepositoryItems = mView.executeQuery(query);
            if ((configRepositoryItems != null) && (configRepositoryItems.length != 0)) {
                for (final RepositoryItem item : configRepositoryItems) {
                    final String itemConfigType = (String) item.getPropertyValue("configType");
                    if (itemConfigType != null) {
                    	Map<String, String> configKeysForConfigType = this.getConfigValueByconfigType(itemConfigType);
                        allConfigKeys.put(itemConfigType, configKeysForConfigType);
                    }

                }
            }
        } catch (final RepositoryException e) {
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } catch (final BBBBusinessException e) {
            throw new BBBBusinessException(BBBCoreErrorConstants.CATALOG_ERROR_1013,
                            "Business Exception while getting config keys in getAllConfigKeys", e);
        } catch (final BBBSystemException e) {
            throw new BBBSystemException(BBBCoreErrorConstants.CATALOG_ERROR_1013,
                            "Repository Exception while getting config keys in getAllConfigKeys", e);
        }

        return allConfigKeys;
    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getSearchSortFieldMap()
     */
    @Override
    public final Map<String, String> getSearchSortFieldMap() throws BBBSystemException {

        Map<String, String> sortFieldMap = null;
        RepositoryView mView = null;
        RepositoryItem[] bbbManRepositorySortOptItems = null;
        QueryBuilder mQueryBuilder = null;

        try {

            mView = this.getBbbManagedCatalogRepository().getView("sortingOption");
            mQueryBuilder = mView.getQueryBuilder();
            final Query query = mQueryBuilder.createUnconstrainedQuery();
            bbbManRepositorySortOptItems = mView.executeQuery(query);
            if ((bbbManRepositorySortOptItems != null) && (bbbManRepositorySortOptItems.length != 0)) {
                sortFieldMap = new HashMap<String, String>();
                for (final RepositoryItem item : bbbManRepositorySortOptItems) {
                    final String sortCode = (String) item.getPropertyValue("sortingCode");
                    final String sortURLParam = (String) item.getPropertyValue("sortingUrlParam");
                    if ((sortCode != null) && (sortURLParam != null)) {
                        sortFieldMap.put(sortURLParam.toLowerCase(), sortCode);
                    }
                }
            }
        } catch (final RepositoryException e) {
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        }
        return sortFieldMap;
    }

    /**
     *  This method returns BrandVo having all properties related to brand.
     *
     * @param brandId the brand id
     * @param siteId the site id
     * @return the brand details
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public BrandVO getBrandDetails(final String brandId, final String siteId)
                    throws BBBBusinessException, BBBSystemException {

        this.logDebug("Catalog API Method Name [getBrandDetails]brandId[" + brandId + "]");
        if ((brandId == null) || (siteId == null) || StringUtils.isEmpty(siteId) || StringUtils.isEmpty(brandId)) {
            throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                            BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
        }
        RepositoryItem brandRepositoryItem = null;
        BrandVO brandVO = new BrandVO();
        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getBrandDetails");
            brandRepositoryItem = this.getCatalogRepository().getItem(brandId,
                            BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR);
            if (brandRepositoryItem == null) {
            	 BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getBrandDetails");
                throw new BBBBusinessException(BBBCatalogErrorCodes.BRAND_ID_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.BRAND_ID_NOT_AVAILABLE_IN_REPOSITORY);
            }
            if (brandRepositoryItem.getPropertyValue(BBBCatalogConstants.SITES_BRAND_PROPERTY_NAME) != null) {
                @SuppressWarnings ("unchecked")
                final Set<RepositoryItem> bbbSites = (Set<RepositoryItem>) brandRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.SITES_BRAND_PROPERTY_NAME);
                if (bbbSites != null) {
                    for (final RepositoryItem siteRepoItem : bbbSites) {
                        if (siteRepoItem.getRepositoryId().equalsIgnoreCase(siteId)) {
                            this.logTrace("Brand is applicable for given siteId " + siteId);
                            brandVO = new BrandVO();
                            brandVO.setBrandId(brandRepositoryItem.getRepositoryId());
                            if (brandRepositoryItem
                                            .getPropertyValue(BBBCatalogConstants.BRAND_NAME_BRAND_PROPERTY_NAME) != null) {
                                this.logTrace("Brand Name ["
                                                + brandRepositoryItem
                                                                .getPropertyValue(BBBCatalogConstants.BRAND_NAME_BRAND_PROPERTY_NAME)
                                                + "]");
                                brandVO.setBrandName((String) brandRepositoryItem
                                                .getPropertyValue(BBBCatalogConstants.BRAND_NAME_BRAND_PROPERTY_NAME));
                            }
                            if (brandRepositoryItem
                                            .getPropertyValue(BBBCatalogConstants.BRAND_IMAGE_BRAND_PROPERTY_NAME) != null) {
                                this.logTrace("Brand Image ["
                                                + brandRepositoryItem
                                                                .getPropertyValue(BBBCatalogConstants.BRAND_IMAGE_BRAND_PROPERTY_NAME)
                                                + "]");
                                brandVO.setBrandImage((String) brandRepositoryItem
                                                .getPropertyValue(BBBCatalogConstants.BRAND_IMAGE_BRAND_PROPERTY_NAME));
                            }
                            if (brandRepositoryItem
                                            .getPropertyValue(BBBCatalogConstants.BRAND_DESCRIPTION_BRAND_PROPERTY_NAME) != null) {
                                this.logTrace("Brand Description ["
                                                + brandRepositoryItem
                                                                .getPropertyValue(BBBCatalogConstants.BRAND_DESCRIPTION_BRAND_PROPERTY_NAME)
                                                + "]");
                                brandVO.setBrandImageAltText((String) brandRepositoryItem
                                                .getPropertyValue(BBBCatalogConstants.BRAND_DESCRIPTION_BRAND_PROPERTY_NAME));
                            }
                            if (brandRepositoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_BRAND_PROPERTY_NAME) != null) {
                                this.logTrace("Brand DisplayFlag ["
                                                + brandRepositoryItem
                                                                .getPropertyValue(BBBCatalogConstants.DISPLAY_BRAND_PROPERTY_NAME)
                                                + "]");
                                brandVO.setDisplayFlag(((Boolean) brandRepositoryItem
                                                .getPropertyValue(BBBCatalogConstants.DISPLAY_BRAND_PROPERTY_NAME)).booleanValue());
                            }
                        }
                    }
                } else {
                	 BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getBrandDetails");
                    throw new BBBBusinessException(BBBCatalogErrorCodes.BRAND_NOT_APPLICABLE_FOR_THE_ANY_SITE,
                                    BBBCatalogErrorCodes.BRAND_NOT_APPLICABLE_FOR_THE_ANY_SITE);
                }
            }
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getBrandDetails");
            return brandVO;
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getBrandDetails]: RepositoryException ");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getBrandDetails");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
        	this.logDebug("Catalog API Method Name [getBrandDetails]brandId[" + brandId + "] Exit");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getBrandDetails");
        }
    }

    /**
     *  This method returns the site tag corresponding to a site.
     *
     * @param siteId the site id
     * @return the site tag
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final String getSiteTag(final String siteId) throws BBBBusinessException, BBBSystemException {
        this.logDebug("Catalog API Method Name [getSiteTag] siteId " + siteId);
        RepositoryItem siteConfiguration = null;
        String siteTag = EMPTYSTRING;
        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getSiteTag");
            siteConfiguration = this.siteRepository.getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
            if (siteConfiguration == null) {
            	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSiteTag");
                throw new BBBBusinessException(BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY);
            }
            if (siteConfiguration.getPropertyValue(BBBCatalogConstants.SITE_TAG_SITE_PROPERTY_NAME) != null) {
                siteTag = (String) siteConfiguration.getPropertyValue(BBBCatalogConstants.SITE_TAG_SITE_PROPERTY_NAME);
                this.logDebug("siteTag [" + siteTag + "]");
            }
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getSiteTag]: RepositoryException ");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSiteTag");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
        	this.logDebug("Catalog API Method Name [getSiteTag] siteId " + siteId + " Exit");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSiteTag");
        }
        return siteTag;
    }

    /**
     *  The method takes a school id and returns school details as schoolVO.
     *
     * @param schoolId the school id
     * @return the school details by id
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */

    @Override
    public SchoolVO getSchoolDetailsById(final String schoolId) throws BBBSystemException, BBBBusinessException {
    	
    	return getSchoolRepositoryTools().getSchoolDetailsById(schoolId);

    }    
    /**
     *  The method takes a vendor id and returns vendor name details as string.
     *
     * @param vendorId the vendor id
     * @return the vendor info
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public final VendorInfoVO getVendorInfo(final String vendorId) throws BBBSystemException, BBBBusinessException {

        this.logDebug("Catalog API Method Name [getVendorNameById] for vendorId[" + vendorId + "]");
        RepositoryItem [] vendorArray;
        RepositoryItem vendorRepositoryItem = null;
        VendorInfoVO vendorInfoVO = new VendorInfoVO();
        String siteId = getCurrentSiteId();
        logDebug("GetVendorInfo: Site id is " + siteId );
        if (!BBBUtility.isEmpty(vendorId)) {
            try {
            	
            	final RepositoryView vendorConfiguration = this.getVendorRepository().getView(
                        BBBCatalogConstants.VENDOR_CONFIGURATION_DESCRIPTOR);
            	
            	final QueryBuilder queryBuilder = vendorConfiguration.getQueryBuilder();
    			final QueryExpression pProperty = queryBuilder
    					.createPropertyQueryExpression("customizationVendorId");
    			final QueryExpression pValue = queryBuilder.createConstantQueryExpression(vendorId);
    			final Query query = queryBuilder.createComparisonQuery(pProperty, pValue, QueryBuilder.EQUALS);
            	vendorArray = vendorConfiguration.executeQuery(query);
            	
                if(vendorArray != null && vendorArray.length > 0)
                {
                	vendorRepositoryItem = vendorArray[0];
                	if (vendorRepositoryItem != null) {
	                	
	                    vendorInfoVO.setVendorName((String) vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDORS_NAME_PROPERTY));
	                    vendorInfoVO.setVendorJS((String) vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_JS));
	                    vendorInfoVO.setVendorMobileJS((String)vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_JS_MOBILE));
	                    vendorInfoVO.setApiKey((String) vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_API_KEY));
	                    vendorInfoVO.setApiURL((String) vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_API_URL));
	                    
	                    if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)){
	                    	vendorInfoVO.setClientId((String)vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_BBB_CLIENT_ID));
	                    }else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)){
	                    	vendorInfoVO.setClientId((String)vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_BAB_CLIENT_ID));
	                    }else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
	                    	vendorInfoVO.setClientId((String)vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_CAN_CLIENT_ID));
	                    } else if(siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_US))  {
	                    	vendorInfoVO.setClientId((String)vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_TBS_BBB_CLIENT_ID));
	                    } else if(siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)) {
	                    	vendorInfoVO.setClientId((String)vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_TBS_CAN_CLIENT_ID));
	                    } else if(siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BBB)) {
	                    	vendorInfoVO.setClientId((String)vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_TBS_BAB_CLIENT_ID));
	                    }
	                    
	                    vendorInfoVO.setApiVersion((String) vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_API_VERSION));
	                    
	                }else{
	                	getDefaultVendorJs(vendorInfoVO,siteId);
	                }
                }
            } catch (final RepositoryException e) {            	
            	logError("Catalog API Method Name [getVendorNameById]: RepositoryException ");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
            } 
        } else {        	
        	getDefaultVendorJs(vendorInfoVO,siteId);
        }
        return vendorInfoVO;

    }

    /**
     * Gets the default vendor js.
     *
     * @param vendorInfoVO the vendor info vo
     * @param siteId the site id
     * @return the default vendor js
     * @throws BBBBusinessException the BBB business exception
     */
    private void getDefaultVendorJs(VendorInfoVO vendorInfoVO,String siteId) throws BBBBusinessException {
    	try {
    		RepositoryItem vendorRepositoryItem = null;
			vendorRepositoryItem = this.getVendorRepository().getItem(BBBCatalogConstants.DEFAULT,BBBCatalogConstants.VENDOR_CONFIGURATION_DESCRIPTOR);
			if(vendorRepositoryItem!=null){
	    		vendorInfoVO.setVendorName((String) vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDORS_NAME_PROPERTY));
	    		vendorInfoVO.setVendorJS((String)vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_JS));
	    		vendorInfoVO.setVendorMobileJS((String)vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_JS_MOBILE));
	    		vendorInfoVO.setApiKey((String) vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_API_KEY));
                vendorInfoVO.setApiURL((String) vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_API_URL));

                if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)){
                	vendorInfoVO.setClientId((String)vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_BBB_CLIENT_ID));
                }else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)){
                	vendorInfoVO.setClientId((String)vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_BAB_CLIENT_ID));
                }else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
                	vendorInfoVO.setClientId((String)vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_CAN_CLIENT_ID));
                }else if(siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_US))
                {
                	vendorInfoVO.setClientId((String)vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_TBS_BBB_CLIENT_ID));
                }
                else if(siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA))
                {
                	vendorInfoVO.setClientId((String)vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_TBS_CAN_CLIENT_ID));
                }
                else if(siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BBB))
                {
                	vendorInfoVO.setClientId((String)vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_TBS_BAB_CLIENT_ID));
                }
                
                vendorInfoVO.setApiVersion((String) vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_API_VERSION));
	    		
	    	}
		} catch (RepositoryException e) {
			logError("No default id present in the database");
			throw new BBBBusinessException(BBBCatalogErrorCodes.VENDOR_ID_NOT_AVAILABLE_IN_REPOSITORY,
                    BBBCatalogErrorCodes.VENDOR_ID_NOT_AVAILABLE_IN_REPOSITORY , e);
		}
    	
	}

	/**
	 *  Returns SchoolVO based on the SEO name of the school.
	 *
	 * @param schoolSeoName SEO name of the school
	 * @return schoolVO
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
    @Override
    public final SchoolVO getSchoolDetailsByName(final String schoolSeoName)
                    throws BBBSystemException, BBBBusinessException {
    	
    	return getSchoolRepositoryTools().getSchoolDetailsByName(schoolSeoName);
    	
    }

    /**
     *  This method provides Review Rating of any product.
     *
     * @param pProductId the product id
     * @return Bazaar Voice Details
     */
    @Override
    public final BazaarVoiceProductVO getBazaarVoiceDetails(final String pProductId) {
        this.logDebug("Catalog API Method Name [getBazaarVoiceDetails]ProductId[" + pProductId + "]");
        final BazaarVoiceProductVO bazaarVoiceProductVO = new BazaarVoiceProductVO();
        if (pProductId == null) {
            bazaarVoiceProductVO.setRatingAvailable(false);
        } else {
            RepositoryItem bazaarVProductRepoItem = null;
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL
                                + BBBCatalogConstants.BAZARVOICE_API_NAME);
                
                final RepositoryView repositoryView = this.getBazaarVoiceRepository().getView(
                        BBBCatalogConstants.BAZAAR_VOICE);
		        final QueryBuilder queryBuilder = repositoryView.getQueryBuilder();
		        final QueryExpression expressionProperty = queryBuilder
		                        .createPropertyQueryExpression(BBBCatalogConstants.PRODUCT_ID);
		        final QueryExpression expressionValue = queryBuilder.createConstantQueryExpression(pProductId);
		
		        final Query query = queryBuilder.createComparisonQuery(expressionProperty, expressionValue,
		                        QueryBuilder.EQUALS);
		        final RepositoryItem[] items = repositoryView.executeQuery(query);
		        this.logTrace("BazaarVoice Items :" + Arrays.toString(items));
		
		        if ((items != null) && (items.length > 0)) {
		
		        	bazaarVProductRepoItem = items[0];
	                if (bazaarVProductRepoItem == null) {
	                    this.logTrace("Bazaar Voice Repository Item is NULL for productID : " + pProductId);
	                    bazaarVoiceProductVO.setRatingAvailable(false);
	                } else {
	                	logTrace("found bazaarVoice details for::" + pProductId);
	                    bazaarVoiceProductVO.setRatingAvailable(false);
	                    if (bazaarVProductRepoItem.getPropertyValue(BBBCatalogConstants.AVERAGE_OVERALL_RATING) != null) {
	                        bazaarVoiceProductVO
	                                        .setAverageOverallRating(((Float) bazaarVProductRepoItem
	                                                        .getPropertyValue(BBBCatalogConstants.AVERAGE_OVERALL_RATING))
	                                                        .floatValue());
	                        bazaarVoiceProductVO.setRatingAvailable(true);
	                    }
	                    if (bazaarVProductRepoItem.getPropertyValue(BBBCatalogConstants.EXTERNAL_ID) != null) {
	                        bazaarVoiceProductVO.setExternalId((String) bazaarVProductRepoItem
	                                        .getPropertyValue(BBBCatalogConstants.EXTERNAL_ID));
	                    }
	                    if (bazaarVProductRepoItem.getPropertyValue(BBBCatalogConstants.TOTAL_REVIEW_COUNT) != null) {
	                        bazaarVoiceProductVO.setTotalReviewCount(((Integer) bazaarVProductRepoItem
	                                        .getPropertyValue(BBBCatalogConstants.TOTAL_REVIEW_COUNT)).intValue());
	                    }
	
	                }
		        }
		        
                return bazaarVoiceProductVO;

            } catch (final RepositoryException e) {
                this.logError("Catalog API Method Name [getBazaarVoiceDetails]: RepositoryException " , e);
                bazaarVoiceProductVO.setRatingAvailable(false);
            } finally {
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL
                                + BBBCatalogConstants.BAZARVOICE_API_NAME);
            }
        }
        return bazaarVoiceProductVO;
    }

    /**
     *  This is an overloaded method which provides Review Rating of any product based on productid and site id.
     *
     * @param pProductId the product id
     * @param siteId the site id
     * @return Bazaar Voice Details
     */
    @Override
    public BazaarVoiceProductVO getBazaarVoiceDetails(final String pProductId, final String siteId) {
        this.logDebug("Catalog API Method Name [getBazaarVoiceDetails]ProductId[" + pProductId + "]");
        final BazaarVoiceProductVO bazaarVoiceProductVO = new BazaarVoiceProductVO();
        if (pProductId == null) {
            bazaarVoiceProductVO.setRatingAvailable(false);
        } else {
            RepositoryItem bazaarVProductRepoItem = null;
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL
                                + BBBCatalogConstants.BAZARVOICE_API_NAME);
				final RepositoryView repositoryView = this.getBazaarVoiceRepository().getView(
                        BBBCatalogConstants.BAZAAR_VOICE);
		        String webDefaultSiteId = siteId;
		        
		        if(BBBUtility.isNotEmpty(siteId) && siteId.contains(BBBCatalogConstants.GS_SITE_PREFIX)){
					webDefaultSiteId = siteId.replace(BBBCatalogConstants.GS_SITE_PREFIX, EMPTYSTRING);
				}
		        //TBSN-263 fix 
		        if(BBBUtility.isNotEmpty(siteId) && siteId.contains(TBSConstants.TBS_SITE_PREFIX)){
					webDefaultSiteId = siteId.replace(TBSConstants.TBS_SITE_PREFIX, EMPTYSTRING);
				}
		        
		        // BBBSL-4824 defect fixed. For baby request, stored its site id in a temp variable and set site id to BedBathUS for fetching reviews.
		       /* if(BBBCoreConstants.SITE_BBB.equalsIgnoreCase(webDefaultSiteId)){
		        	tempSiteId = webDefaultSiteId;
		        	webDefaultSiteId = BBBCoreConstants.SITE_BAB_US;
		        }*/
		        this.logDebug("Site Id value for query is: " + webDefaultSiteId);
		        final QueryBuilder queryBuilder = repositoryView.getQueryBuilder();
		        
		        final QueryExpression productIdProperty = queryBuilder
		                        .createPropertyQueryExpression(BBBCatalogConstants.PRODUCT_ID);
		        final QueryExpression productIdValue = queryBuilder.createConstantQueryExpression(pProductId);
		        final Query productIdquery = queryBuilder.createComparisonQuery(productIdProperty, productIdValue,
		                        QueryBuilder.EQUALS);
		
		        final QueryExpression siteIdProperty = queryBuilder
		                        .createPropertyQueryExpression(BBBCatalogConstants.SITE_ID);
		        QueryExpression siteIdValue = queryBuilder.createConstantQueryExpression(webDefaultSiteId);
		        Query siteIdquery = queryBuilder.createComparisonQuery(siteIdProperty, siteIdValue,
		                        QueryBuilder.EQUALS);
		
		        /* Create AND query using productId & siteId */
		        final Query[] subqueries = { productIdquery, siteIdquery };
		        Query combinedQuery = queryBuilder.createAndQuery(subqueries);
		
		        //Check added to allow BV data irrespective of site. 
		        Query bvQuery = null;
		        if(this.isBazaarVoiceSiteSpecific())
		        	bvQuery = combinedQuery;
		        else
		        	bvQuery = productIdquery;
		        
		        RepositoryItem[] items = repositoryView.executeQuery(bvQuery);

		        // BBBSL-4824 defect fixed. For baby request, if reviews are not found with US site id, then again fire the query and fetch reviews with site id as baby.
		        if(BBBCoreConstants.SITE_BBB.equalsIgnoreCase(webDefaultSiteId) && items == null ){
		        	this.logDebug("Results for baby site are empty, hence querying for US site...");
		        	webDefaultSiteId = BBBCoreConstants.SITE_BAB_US;
		        	this.logDebug("Site Id value for query is: " + webDefaultSiteId);
		        	siteIdValue = queryBuilder.createConstantQueryExpression(webDefaultSiteId);
		        	siteIdquery = queryBuilder.createComparisonQuery(siteIdProperty, siteIdValue,
	                        QueryBuilder.EQUALS);
		        	Query[] subqueriesBaby = { productIdquery, siteIdquery };
			        combinedQuery = queryBuilder.createAndQuery(subqueriesBaby);
			        items = repositoryView.executeQuery(combinedQuery);
		        }
		        
		        this.logTrace("BazaarVoice Items :" + Arrays.toString(items));
		
		        if ((items != null) && (items.length > 0)) {
		
		            bazaarVProductRepoItem = items[0];
                
	                if (bazaarVProductRepoItem == null) {
	                    this.logDebug("Bazaar Voice Repository Item is NULL for productID : " + pProductId);
	                    bazaarVoiceProductVO.setRatingAvailable(false);
	                } else {
	                	logTrace("found bazaarVoice details for::" + pProductId);
	                    bazaarVoiceProductVO.setRatingAvailable(false);
	                    if (bazaarVProductRepoItem.getPropertyValue(BBBCatalogConstants.AVERAGE_OVERALL_RATING) != null) {
	                        bazaarVoiceProductVO
	                                        .setAverageOverallRating(((Float) bazaarVProductRepoItem
	                                                        .getPropertyValue(BBBCatalogConstants.AVERAGE_OVERALL_RATING))
	                                                        .floatValue());
	                        bazaarVoiceProductVO.setRatingAvailable(true);
	                    }
	                    if (bazaarVProductRepoItem.getPropertyValue(BBBCatalogConstants.EXTERNAL_ID) != null) {
	                        bazaarVoiceProductVO.setExternalId((String) bazaarVProductRepoItem
	                                        .getPropertyValue(BBBCatalogConstants.EXTERNAL_ID));
	                    }
	                    if (bazaarVProductRepoItem.getPropertyValue(BBBCatalogConstants.TOTAL_REVIEW_COUNT) != null) {
	                        bazaarVoiceProductVO.setTotalReviewCount(((Integer) bazaarVProductRepoItem
	                                        .getPropertyValue(BBBCatalogConstants.TOTAL_REVIEW_COUNT)).intValue());
	                    }
	                    if (bazaarVProductRepoItem.getPropertyValue(BBBCatalogConstants.SITE_ID) != null) {
	                        bazaarVoiceProductVO.setSiteId((String) bazaarVProductRepoItem
	                                        .getPropertyValue(BBBCatalogConstants.SITE_ID));
	                    }
	                }
	                
		        }
		        
                return bazaarVoiceProductVO;

            } catch (final RepositoryException e) {
                this.logError("Catalog API Method Name [getBazaarVoiceDetails]: RepositoryException ", e);
                bazaarVoiceProductVO.setRatingAvailable(false);
            } finally {
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL
                                + BBBCatalogConstants.BAZARVOICE_API_NAME);
            }
        }
        return bazaarVoiceProductVO;
    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getCanadaStoreLocatorInfo()
     */
    @Override
    public final List<StoreVO> getCanadaStoreLocatorInfo() throws BBBSystemException, BBBBusinessException {
    	
    	return getStoreRepositoryTools().getCanadaStoreLocatorInfo();

    }

    /**
     *  The method checks if the input category is a leaf category. If any exception occurs while retrieving data the
     * BBBSystemException is thrown
     *
     * @param siteId the site id
     * @param categoryId the category id
     * @return true, if is leaf category
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public final boolean isLeafCategory(final String siteId, final String categoryId)
                    throws BBBSystemException, BBBBusinessException {

            final StringBuilder debug = new StringBuilder(30);
            debug.append("Catalog API Method Name [isLeafCategory] siteId[").append(siteId).append("] categoryId ")
                            .append(categoryId);
            this.logDebug(debug.toString());


        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " isLeafCategory");
            final RepositoryItem categoryRepositoryItem = this.getCatalogRepository().getItem(categoryId,
                            BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
            if (categoryRepositoryItem == null) {
                throw new BBBBusinessException(BBBCatalogErrorCodes.CATEGORY_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.CATEGORY_NOT_AVAILABLE_IN_REPOSITORY);
            }
            @SuppressWarnings ("unchecked")
            final List<RepositoryItem> childProducts = (List<RepositoryItem>) categoryRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_CATEGORY_PROPERTY_NAME);
            if ((childProducts != null) && !childProducts.isEmpty()) {
                this.logDebug("Return leaf node as TRUE for categoryId : " + categoryId);
                return true;
            }
            this.logDebug("Return leaf node as FALSE for categoryId : " + categoryId);
            return false;
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [isLeafCategory]: RepositoryException ");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " isLeafCategory");
        }
    }

    /**
     *  The method returns promotion Id corresponding to a coupon id.
     *
     * @param couponId the coupon id
     * @return the promotion id
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public  String getPromotionId(final String couponId) throws BBBSystemException, BBBBusinessException {
        final RepositoryItem promotionRepositoryItem[] = this.getPromotions(couponId);
        if (promotionRepositoryItem != null) {
            return promotionRepositoryItem[0].getRepositoryId();
        }
        return null;

    }

    /**
     *  Returns a list of state Codes that are bopus eligible. If input parameter is null then BBBBusinessException is
     * thrown
     *
     * @param siteId the site id
     * @return the bopus eligible states
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public  List<String> getBopusEligibleStates(final String siteId)
                    throws BBBBusinessException, BBBSystemException {
    	
    	 return getSiteRepositoryTools().getBopusEligibleStates(siteId);

    }

    /**
     *  The method gives the expected min and max date string when a customer can expect teh delivery if time at which
     * order is submit is past the cutoff time for the shipping group then min expected date by which order is expected
     * will be (current date +1 + min days to ship) else it will be (current date+min days to ship) Similarly max
     * expected date by which order is expected will be (current date +1 + max days to ship) else it will be (current
     * date+max days to ship).
     *
     * @param shippingMethod the shipping method
     * @param state the state
     * @param siteId the site id
     * @param orderDate the order date
     * @param includeYearFlag the include year flag
     * @return the expected delivery date
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */

    @Override
    public final String getExpectedDeliveryDate(final String shippingMethod, final String state, final String siteId,
                    final Date orderDate, boolean includeYearFlag) throws BBBBusinessException, BBBSystemException {

        this.logDebug("Catalog API Method Name [getExpectedDeliveryDate]shippingMethod[" + shippingMethod + "]" + "includeYearFlag- " + includeYearFlag);

        if ((shippingMethod != null) && !StringUtils.isEmpty(shippingMethod)) {
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getExpectedDeliveryDate");

                final RepositoryItem shippingDurationItem = this.getShippingDuration(shippingMethod, siteId);
	            if ( null != shippingDurationItem){
	                final Calendar calCurrentDate = Calendar.getInstance();
	                final Calendar minDate = Calendar.getInstance();
	                final Calendar maxDate = Calendar.getInstance();
	                final Date cutOffTime = (Date) shippingDurationItem
	                                .getPropertyValue(BBBCatalogConstants.CUT_OFF_TIME_SHIPPING_PROPERTY_NAME);
	                final Calendar calCutOffTime = Calendar.getInstance();
	                calCutOffTime.setTime(cutOffTime);
	
	                calCurrentDate.setTime(orderDate);
	                minDate.setTime(orderDate);
	                maxDate.setTime(orderDate);
	
	                this.logDebug("Value of cutOffTime [ " + cutOffTime + "]");
	                int maxDaysToShip = ((Integer) shippingDurationItem
	                                .getPropertyValue(BBBCatalogConstants.MAX_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME))
	                                .intValue();
	                int minDaysToShip = ((Integer) shippingDurationItem
	                                .getPropertyValue(BBBCatalogConstants.MIN_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME))
	                                .intValue();
	                this.logDebug("maxDaysToShip [ " + maxDaysToShip + " ] minDaysToShip [" + minDaysToShip + "]");
	                this.logDebug("Hour value  in cutOffTime [" + calCutOffTime.get(Calendar.HOUR_OF_DAY)
	                                + "] Minute value in cutOffTime [" + calCutOffTime.get(Calendar.MINUTE) + "]");
	                this.logDebug("Hour value  in current date [" + calCurrentDate.get(Calendar.HOUR_OF_DAY)
	                                + "] Minute value in  current date [" + calCurrentDate.get(Calendar.MINUTE) + "]");
	                boolean isCutOff = false;
	
	                Set<Integer> weekEndDays = this.getWeekEndDays(siteId);
	                this.logDebug("weekEndDays are  " + weekEndDays);
	
	                // Calculate Holiday Dates
	                Set<Date> holidayList = this.getHolidayList(siteId);
	                final int holidayCount = holidayList.size();
	                this.logDebug("holidayCount is  " + holidayCount);
	
	                if (cutOffTime != null) {
	                    if (calCurrentDate.get(Calendar.HOUR_OF_DAY) == calCutOffTime.get(Calendar.HOUR_OF_DAY)) {
	                        if (calCurrentDate.get(Calendar.MINUTE) >= calCutOffTime.get(Calendar.MINUTE)) {
	                            this.logDebug("Value of hours for cutoff ad current is same.So checking for minutes value ");
	                            this.logDebug("Is minutes value withing cutoff "
	                                            + (calCurrentDate.get(Calendar.MINUTE) <= calCutOffTime
	                                                            .get(Calendar.MINUTE)));
	                            isCutOff = true;
	                        }
	                    } else if (calCurrentDate.get(Calendar.HOUR_OF_DAY) > calCutOffTime.get(Calendar.HOUR_OF_DAY)) {
	                        this.logDebug("hours in current date has exceeded cutoff time");
	                        isCutOff = true;
	                    }
	                    // if today is not a weekday or holiday then add +1 day to min and max
	                    if (isCutOff && !weekEndDays.contains(Integer.valueOf(calCurrentDate.get(Calendar.DAY_OF_WEEK)))
	                                    && !isHoliday(holidayList, calCurrentDate)) {
	                        minDaysToShip = minDaysToShip + 1;
	                        maxDaysToShip = maxDaysToShip + 1;
	                    }
	                }
	                this.logDebug("maxDaysToShip after cutofftime check [ " + maxDaysToShip
	                                + " ] minDaysToShip after cutofftime check [" + minDaysToShip + "]");
	                if (this.getExceptionalDeliveryDateStatesList().contains(state)
	                                && (this.getStateNoOfDaysThanNormalMap().get(state) != null)) {
	                    final int extraNoOfDays = Integer.parseInt(this.getStateNoOfDaysThanNormalMap().get(state));
	                    minDaysToShip = minDaysToShip + extraNoOfDays;
	                    maxDaysToShip = maxDaysToShip + extraNoOfDays;
	
	                        final StringBuilder debug = new StringBuilder(50);
	                        debug.append("maxDaysToShip after cutofftime check for exceptional delivery dates state [ ")
	                                        .append(maxDaysToShip)
	                                        .append(" ] minDaysToShip for exceptional delivery dates state  [")
	                                        .append(minDaysToShip).append("for state ").append(state).append("]");
	                        this.logDebug(debug.toString());
	                    }
	
	                final int shippingDuration = maxDaysToShip - minDaysToShip;
	                this.logDebug(SHIPPING_DURATION_IS + shippingDuration);
	                int tmpMinDays = minDaysToShip;
	                int tmpMaxDays = maxDaysToShip;
	
	                while (tmpMinDays != 0) {
	                    if (!weekEndDays.contains(Integer.valueOf(minDate.get(Calendar.DAY_OF_WEEK)))
	                                    && !isHoliday(holidayList, minDate)) {
	                        tmpMinDays--;
	                    }
	                    if (tmpMinDays != 0) {
	                        minDate.add(Calendar.DATE, 1);
	                    }
	                }
	
	                while (tmpMaxDays != 0) {
	                    if (!weekEndDays.contains(Integer.valueOf(maxDate.get(Calendar.DAY_OF_WEEK)))
	                                    && !isHoliday(holidayList, maxDate)) {
	                        tmpMaxDays--;
	                    }
	                    if (tmpMaxDays != 0) {
	                        maxDate.add(Calendar.DATE, 1);
	                    }
	                }

	                String minDateString = BBBCoreConstants.BLANK;
	                String maxDateString = BBBCoreConstants.BLANK;
	
	                if(includeYearFlag) {
	                	final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	                    minDateString = dateFormat.format(minDate.getTime());
	                    maxDateString = dateFormat.format(maxDate.getTime());
	                }else{
	                	if (BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(siteId) || siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA))
	                    {
	                		 minDateString = minDate.get(Calendar.DAY_OF_MONTH) + "/" + (minDate.get(Calendar.MONTH) + 1);                		 
	                		 maxDateString = maxDate.get(Calendar.DAY_OF_MONTH) + "/" + (maxDate.get(Calendar.MONTH) + 1);
	                    }else{
	                    	minDateString = (minDate.get(Calendar.MONTH) + 1) + "/" + minDate.get(Calendar.DAY_OF_MONTH);
	                    	maxDateString = (maxDate.get(Calendar.MONTH) + 1) + "/" + maxDate.get(Calendar.DAY_OF_MONTH);
	                }
	                }
	                
	                this.logDebug("minDateString after format  " + minDateString + "  maxDateString after format "
	                                + maxDateString);
	                return getFormattedDate(minDateString,includeYearFlag,siteId) + HYPHEN + getFormattedDate(maxDateString,includeYearFlag,siteId);
	            }
                else{
                	// returning empty string in case of SDD
                	return(BBBCoreConstants.BLANK); 
                }
}finally {
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getExpectedDeliveryDate");
            }
        }
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }
    
    
    /**
     * Gets the formatted date.
     *
     * @param date the date
     * @param includeYearFlag the include year flag
     * @param siteId the site id
     * @return the formatted date
     */
    protected String getFormattedDate(String date, Boolean includeYearFlag,String siteId){
    	
    	logDebug("Method: BBBCatalogToolsImpl.getFormattedDate");
    	
    	SimpleDateFormat fromUser;
    	SimpleDateFormat myFormat;
    	if(includeYearFlag){

        	fromUser = new SimpleDateFormat(BBBCoreConstants.US_DATE_FORMAT);
        	myFormat = new SimpleDateFormat(BBBCoreConstants.US_DATE_FORMAT);
    	}else{
    		if(BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(siteId)){
    			fromUser = new SimpleDateFormat(BBBCoreConstants.CANADA_DATE_FORMAT);
    			myFormat = new SimpleDateFormat(BBBCoreConstants.CANADA_DATE_FORMAT);
    		}else{
	    		fromUser = new SimpleDateFormat(BBBCoreConstants.UNITED_STATES_DATA_FORMAT);
	    		myFormat = new SimpleDateFormat(BBBCoreConstants.UNITED_STATES_DATA_FORMAT);
    		}
    	}
    	String formattedDate = null;
    	try {
    		
    	   formattedDate   = myFormat.format(fromUser.parse(date));
    	} catch (ParseException e) {
    	   logError("Parsing in the date to correct format");
    	}
    	
    	logDebug("The formatted date is " + formattedDate);
    	
    	return formattedDate;
    }
    
    /**
     *  This method returns the value of skuId for given values of rollup attributes.
     *
     * @param siteId the site id
     * @param productId the product id
     * @param rollUpTypeValueMap the roll up type value map
     * @return the ever living sku details
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public final String getEverLivingSKUDetails(final String siteId, final String productId,
                    final Map<String, String> rollUpTypeValueMap) throws BBBSystemException, BBBBusinessException {

            final StringBuilder debug = new StringBuilder(50);
            debug.append("Catalog API Method Name [getProductDetails] siteId[").append(siteId).append("] productId [")
                            .append(productId);
            this.logDebug(debug.toString());

        final List<String> propertyNameList = new ArrayList<String>();
        final List<String> propertyValueList = new ArrayList<String>();
        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_2");
            final RepositoryItem productRepositoryItem = this.getCatalogRepository().getItem(productId,
                            BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
            if (productRepositoryItem == null) {
                throw new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);
            }
            @SuppressWarnings ("unchecked")
            final List<RepositoryItem> skuRepositoryItems = (List<RepositoryItem>) productRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);
            this.logDebug("skuRepositoryItems value [ " + skuRepositoryItems + "]");
            if (skuRepositoryItems != null) {
                // add properties name list in propertyNameList and property value list in propertyValueList
                final Set<Entry<String, String>> propertyNameSet = rollUpTypeValueMap.entrySet();
                for (final Entry<String, String> propertyName : propertyNameSet) {
                    propertyNameList.add(propertyName.getKey().toLowerCase());
                    propertyValueList.add(rollUpTypeValueMap.get(propertyName.getKey()));
                }
                // for each SKU repository item check if all the properties value match as per the properties in
                // rollUpTypeValueMap
                for (int i = 0; i < skuRepositoryItems.size(); i++) {
                    String skuPropertyValue = EMPTYSTRING;
                    this.logTrace("Check the value of rollup properties for the" + i + "th sku with id value [ "
                                    + skuRepositoryItems.get(i).getRepositoryId() + "]");
                        for (int index = 0; index < rollUpTypeValueMap.size(); index++) {
                            skuPropertyValue = (String) skuRepositoryItems.get(i).getPropertyValue(
                                            this.getRollUpSkuPropertyMap().get(
                                                            propertyNameList.get(index).toLowerCase()));
                            this.logTrace("value of rollup property [" + propertyNameList.get(index).toLowerCase()
                                            + "] for the sku from repository is [ " + skuPropertyValue + "]");
                            this.logTrace("value of input rollup property ["
                                            + propertyNameList.get(index).toLowerCase() + "]to be compared is  [ "
                                            + propertyValueList.get(index) + "]");
                            // If even one of the properties don't match break from the loop
                            if ((skuPropertyValue == null)
                                            || !skuPropertyValue.equalsIgnoreCase(propertyValueList.get(index))) {
                                break;
                            }
                            /* if it is the last roll up property value that needs to be matched and
                             * !skuPropertyValue.equalsIgnoreCase(propertyValueList.get(index) is false return the skuId */

                            else if (index == (rollUpTypeValueMap.size() - 1)) {
                                this.logDebug("Final SkuId that matches is [ "
                                                + skuRepositoryItems.get(i).getRepositoryId() + "]");
                                return skuRepositoryItems.get(i).getRepositoryId();
                            }
                        }
                }
            }
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getProductDetails_2]: RepositoryException ");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
        	debug.append(" Exit");
        	this.logDebug(debug.toString());
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_2");
        }
        return null;
    }

    /**
     * isHoliday() - Moved to configTools class - used by more than 1 class.
     */
    
    /**
     *  The method gives the expected min and max date string when a customer can expect teh delivery if time at which
     * order is submit is past the cutoff time for the shipping group then min expected date by which order is expected
     * will be (current date +1 + min days to ship) else it will be (current date+min days to ship) Similarly max
     * expected date by which order is expected will be (current date +1 + max days to ship) else it will be (current
     * date+max days to ship).
     *
     * @param shippingMethod the shipping method
     * @param state the state
     * @param siteId the site id
     * @return the expected delivery date
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */

    @Override
    public final String getExpectedDeliveryDate(final String shippingMethod, final String state, final String siteId)
                    throws BBBBusinessException, BBBSystemException {

        this.logDebug("Catalog API Method Name [getExpectedDeliveryDate]shippingMethod[" + shippingMethod + "]");

        if (((shippingMethod != null) && !StringUtils.isEmpty(shippingMethod))
                        || ((state != null) && !StringUtils.isEmpty(state))) {
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getExpectedDeliveryDate");

                final RepositoryItem shippingDurationItem = this.getShippingDuration(shippingMethod, siteId);
                final Calendar calCurrentDate = Calendar.getInstance();
                final Calendar minDate = Calendar.getInstance();
                final Calendar maxDate = Calendar.getInstance();
                final Date cutOffTime = (Date) shippingDurationItem
                                .getPropertyValue(BBBCatalogConstants.CUT_OFF_TIME_SHIPPING_PROPERTY_NAME);
                final Calendar calCutOffTime = Calendar.getInstance();
                calCutOffTime.setTime(cutOffTime);

                this.logDebug("Value of cutOffTime [ " + cutOffTime + "]");
                int maxDaysToShip = ((Integer) shippingDurationItem
                                .getPropertyValue(BBBCatalogConstants.MAX_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME))
                                .intValue();
                int minDaysToShip = ((Integer) shippingDurationItem
                                .getPropertyValue(BBBCatalogConstants.MIN_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME))
                                .intValue();
                maxDaysToShip = maxDaysToShip - 1;
                minDaysToShip = minDaysToShip - 1;
                this.logDebug("maxDaysToShip [ " + maxDaysToShip + " ] minDaysToShip [" + minDaysToShip + "]");
                this.logDebug("Hour value  in cutOffTime [" + calCutOffTime.get(Calendar.HOUR)
                                + "] Minute value in cutOffTime [" + calCutOffTime.get(Calendar.MINUTE) + "]");
                this.logDebug("Hour value  in current date [" + calCurrentDate.get(Calendar.HOUR)
                                + "] Minute value in  current date [" + calCurrentDate.get(Calendar.MINUTE) + "]");

                if (cutOffTime != null) {
                    if (calCurrentDate.get(Calendar.HOUR) == calCutOffTime.get(Calendar.HOUR)) {
                        if (calCurrentDate.get(Calendar.MINUTE) >= calCutOffTime.get(Calendar.MINUTE)) {
                            this.logDebug("Value of hours for cutoff ad current is same.So checking for minutes value ");
                            this.logDebug("Is minutes value withing cutoff "
                                            + (calCurrentDate.get(Calendar.MINUTE) <= calCutOffTime
                                                            .get(Calendar.MINUTE)));
                            minDaysToShip = minDaysToShip + 1;
                            maxDaysToShip = maxDaysToShip + 1;
                        }
                    } else if (calCurrentDate.get(Calendar.HOUR) > calCutOffTime.get(Calendar.HOUR)) {
                        this.logDebug("hours in current date has exceeded cutoff time");
                        minDaysToShip = minDaysToShip + 1;
                        maxDaysToShip = maxDaysToShip + 1;
                    }
                }
                this.logDebug("maxDaysToShip after cutofftime check [ " + maxDaysToShip
                                + " ] minDaysToShip after cutofftime check [" + minDaysToShip + "]");
                if (this.getExceptionalDeliveryDateStatesList().contains(state)
                                && (this.getStateNoOfDaysThanNormalMap().get(state) != null)) {
                    final int extraNoOfDays = Integer.parseInt(this.getStateNoOfDaysThanNormalMap().get(state));
                    minDaysToShip = minDaysToShip + extraNoOfDays;
                    maxDaysToShip = maxDaysToShip + extraNoOfDays;

                        final StringBuilder debug = new StringBuilder(50);
                        debug.append("maxDaysToShip after cutofftime check for exceptional delivery dates state [ ")
                                        .append(maxDaysToShip)
                                        .append(" ] minDaysToShip for exceptional delivery dates state  [")
                                        .append(minDaysToShip).append("for state ").append(state).append("]");
                        this.logDebug(debug.toString());

                    }

                final int shippingDuration = maxDaysToShip - minDaysToShip;
                this.logDebug(SHIPPING_DURATION_IS + shippingDuration);
                final int range = shippingDuration * 10;

                Set<Integer> weekEndDays = this.getWeekEndDays(siteId);
                this.logDebug("weekEndDays are  " + weekEndDays);

                // Calculate Holiday Dates
                Set<Date> holidayList = this.getHolidayList(siteId);
                final int holidayCount = holidayList.size();
                this.logDebug("holidayCount is  " + holidayCount);

                // Calculate WeekEnd Dates
                Set<Date> weekEndList = this.getWeekEndList(weekEndDays, null, range);
                final int weekEndCount = weekEndList.size();
                this.logDebug("weekEndCount is  " + weekEndCount);

                // Calculate total non working Days
                final Set<Date> nonWorkingDays = new HashSet<Date>();
                nonWorkingDays.addAll(holidayList);
                nonWorkingDays.addAll(weekEndList);
                int nonWorkingDaysCount = nonWorkingDays.size();
                this.logDebug("nonWorkingDaysCount is  " + nonWorkingDaysCount);

                // Calculate Non working Days count for Min Shipping Date
                int count = 0;
                Set<Date> remainingNonWorkingDays = new HashSet<Date>();
                remainingNonWorkingDays.addAll(nonWorkingDays);
                this.logDebug("remainingNonWorkingDays is  " + remainingNonWorkingDays.size());
                if (remainingNonWorkingDays.size() > 0) {
                    do {
                        remainingNonWorkingDays = getNonWorkingDaysList(minDaysToShip, maxDaysToShip, siteId,
                                        remainingNonWorkingDays, null, false);
                        count = nonWorkingDaysCount - remainingNonWorkingDays.size();
                        if (count > 0) {
                            nonWorkingDaysCount = nonWorkingDaysCount - count;
                            minDaysToShip = minDaysToShip + count;
                        }
                    } while (count > 0);
                }

                // Calculate Non working Days count for Max Shipping Date
                this.logDebug("Calculate Non working Days count for Max Shipping Date");
                maxDaysToShip = minDaysToShip + shippingDuration;
                count = 0;
                remainingNonWorkingDays = new HashSet<Date>();
                remainingNonWorkingDays.addAll(nonWorkingDays);
                nonWorkingDaysCount = nonWorkingDays.size();
                this.logDebug("nonWorkingDaysCount is  " + nonWorkingDaysCount);
                if (!remainingNonWorkingDays.isEmpty()) {
                    do {
                        remainingNonWorkingDays = getNonWorkingDaysList(minDaysToShip, maxDaysToShip, siteId,
                                        remainingNonWorkingDays, null, true);
                        count = nonWorkingDaysCount - remainingNonWorkingDays.size();
                        if (count > 0) {
                            nonWorkingDaysCount = nonWorkingDaysCount - count;
                            maxDaysToShip = maxDaysToShip + count;
                        }
                    } while (count > 0);
                }

                minDate.add(Calendar.DAY_OF_MONTH, minDaysToShip);
                maxDate.add(Calendar.DAY_OF_MONTH, maxDaysToShip);
                String minDateString = BBBCoreConstants.BLANK;
                String maxDateString = BBBCoreConstants.BLANK;
    			if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) || siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)){
                    minDateString = minDate.get(Calendar.DAY_OF_MONTH) + "/" + (minDate.get(Calendar.MONTH) + 1);
                    maxDateString = maxDate.get(Calendar.DAY_OF_MONTH) + "/" + (maxDate.get(Calendar.MONTH) + 1);
                } else {
                    minDateString = (minDate.get(Calendar.MONTH) + 1) + "/" + minDate.get(Calendar.DAY_OF_MONTH);
                    maxDateString = (maxDate.get(Calendar.MONTH) + 1) + "/" + maxDate.get(Calendar.DAY_OF_MONTH);
                }
                this.logDebug("minDateString after format  " + minDateString + "  maxDateString after format "
                                + maxDateString);
                return minDateString + "-" + maxDateString;
            } finally {
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getExpectedDeliveryDate");
            }
        }
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        "Mandatory input parameters missing");
    }

    /**
     * getHolidayList() - method moved to ConfigTools class - used by more than 1 Tools class
     */
   
   /**
     *  getWeekEndDays() - moved to ConfigTools class - used by more than one tools class. 
     */
    
    /**
     *  The method return the Week Ends List.
     *
     * @param weekEndDays the week end days
     * @param orderDate the order date
     * @param count the count
     * @return the week end list
     */
    private Set<Date> getWeekEndList(final Set<Integer> weekEndDays, final Date orderDate, final int count) {

        final Set<Date> weekEndList = new HashSet<Date>();
        final Calendar date = Calendar.getInstance();
        if (orderDate != null) {
            date.setTime(orderDate);
        }
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        for (int i = 0; i < count; i++) {
            date.add(Calendar.DATE, 1);
            for (final Integer weekDay : weekEndDays) {
                if ((null != weekDay) && (date.get(Calendar.DAY_OF_WEEK) == weekDay.intValue())) {
                    try {
                        final String formattedDate = dateFormat.format(date.getTime());
                        weekEndList.add(dateFormat.parse(formattedDate));
                    } catch (final ParseException e) {
                        this.logError("Parsing Exception" + e);
                    }
                }
            }
        }
        return weekEndList;
    }

    /**
     *  The method return the non working Days after excluding used holidays.
     *
     * @param minDaysToShip the min days to ship
     * @param maxDaysToShip the max days to ship
     * @param siteId the site id
     * @param nonWorkingDays the non working days
     * @param orderDate the order date
     * @param check the check
     * @return Non Working Days list
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    public final static Set<Date> getNonWorkingDaysList(final int minDaysToShip, final int maxDaysToShip,
                    final String siteId, final Set<Date> nonWorkingDays, final Date orderDate, final boolean check)
                    throws BBBBusinessException, BBBSystemException {

        Calendar startDate = Calendar.getInstance();
        final Calendar calHolidayDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        final Calendar maxDeliveryDate = Calendar.getInstance();

        if (orderDate != null) {
            startDate.setTime(orderDate);
        }
        endDate.add(Calendar.DATE, minDaysToShip);
        maxDeliveryDate.add(Calendar.DATE, maxDaysToShip);

        if (check) {
            startDate = endDate;
            endDate = maxDeliveryDate;
        }

        final Set<Date> tempHolidayList = new HashSet<Date>();
        tempHolidayList.addAll(nonWorkingDays);
        for (final Date date : nonWorkingDays) {
            final Date holidayDate = date;
            calHolidayDate.setTime(holidayDate);
            if ((calHolidayDate.get(Calendar.DAY_OF_MONTH) == startDate.get(Calendar.DAY_OF_MONTH))
                            && (calHolidayDate.get(Calendar.MONDAY) == startDate.get(Calendar.MONTH))
                            && (calHolidayDate.get(Calendar.YEAR) == startDate.get(Calendar.YEAR))) {
                tempHolidayList.remove(date);
            } else if ((calHolidayDate.get(Calendar.DAY_OF_MONTH) == endDate.get(Calendar.DAY_OF_MONTH))
                            && (calHolidayDate.get(Calendar.MONDAY) == endDate.get(Calendar.MONTH))
                            && (calHolidayDate.get(Calendar.YEAR) == endDate.get(Calendar.YEAR))) {
                tempHolidayList.remove(date);
            } else if (calHolidayDate.after(startDate) && calHolidayDate.before(endDate)) {
                tempHolidayList.remove(date);
            }
        }
        return tempHolidayList;
    }

    /**
     *  The method gives the expected min and max date string when a customer can expect teh delivery if time at which
     * order is submit is past the cutoff time for the shipping group then min expected date by which order is expected
     * will be (current date +1 + min days to ship) else it will be (current date+min days to ship) Similarly max
     * expected date by which order is expected will be (current date +1 + max days to ship) else it will be (current
     * date+max days to ship).
     *
     * @param shippingMethod the shipping method
     * @param siteId the site id
     * @return the expected delivery date
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */

    @Override
    public final String getExpectedDeliveryDate(final String shippingMethod, String siteId)
                    throws BBBBusinessException, BBBSystemException {

        this.logDebug("Catalog API Method Name [getExpectedDeliveryDate]shippingMethod[" + shippingMethod + "]");

        if ((shippingMethod != null) && !StringUtils.isEmpty(shippingMethod)) {
            try {
            	String currentSiteId = siteId;
                currentSiteId = SiteContextManager.getCurrentSiteId();
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getExpectedDeliveryDate");

                this.getShippingMethod(shippingMethod);
                final RepositoryItem shippingDurationItem = this.getShippingDuration(shippingMethod, currentSiteId);
                final Calendar calCurrentDate = Calendar.getInstance();
                final Calendar minDate = Calendar.getInstance();
                final Calendar maxDate = Calendar.getInstance();
                final Date cutOffTime = (Date) shippingDurationItem
                                .getPropertyValue(BBBCatalogConstants.CUT_OFF_TIME_SHIPPING_PROPERTY_NAME);
                final Calendar calCutOffTime = Calendar.getInstance();
                calCutOffTime.setTime(cutOffTime);

                this.logDebug("Value of cutOffTime [ " + cutOffTime + "]");
                int maxDaysToShip = ((Integer) shippingDurationItem
                                .getPropertyValue(BBBCatalogConstants.MAX_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME))
                                .intValue();
                int minDaysToShip = ((Integer) shippingDurationItem
                                .getPropertyValue(BBBCatalogConstants.MIN_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME))
                                .intValue();

                maxDaysToShip = maxDaysToShip - 1;
                minDaysToShip = minDaysToShip - 1;

                this.logDebug("maxDaysToShip [ " + maxDaysToShip + " ] minDaysToShip [" + minDaysToShip + "]");
                this.logDebug("Hour value  in cutOffTime [" + calCutOffTime.get(Calendar.HOUR)
                                + "] Minute value in cutOffTime [" + calCutOffTime.get(Calendar.MINUTE) + "]");
                this.logDebug("Hour value  in current date [" + calCurrentDate.get(Calendar.HOUR)
                                + "] Minute value in  current date [" + calCurrentDate.get(Calendar.MINUTE) + "]");

                if (cutOffTime != null) {
                    if (calCurrentDate.get(Calendar.HOUR) == calCutOffTime.get(Calendar.HOUR)) {
                        if (calCurrentDate.get(Calendar.MINUTE) >= calCutOffTime.get(Calendar.MINUTE)) {
                            this.logDebug("Value of hours for cutoff ad current is same.So checking for minutes value ");
                            this.logDebug("Is minutes value withing cutoff "
                                            + (calCurrentDate.get(Calendar.MINUTE) <= calCutOffTime
                                                            .get(Calendar.MINUTE)));
                            minDaysToShip = minDaysToShip + 1;
                            maxDaysToShip = maxDaysToShip + 1;
                        }
                    } else if (calCurrentDate.get(Calendar.HOUR) > calCutOffTime.get(Calendar.HOUR)) {
                        this.logDebug("hours in current date has exceeded cutoff time");
                        minDaysToShip = minDaysToShip + 1;
                        maxDaysToShip = maxDaysToShip + 1;
                    }
                }
                this.logDebug("maxDaysToShip after cutofftime check [ " + maxDaysToShip
                                + " ] minDaysToShip after cutofftime check [" + minDaysToShip + "]");

                // add min days to get actual date
                minDate.add(Calendar.DAY_OF_MONTH, minDaysToShip);
                maxDate.add(Calendar.DAY_OF_MONTH, maxDaysToShip);
                String minDateString = BBBCoreConstants.BLANK;
                String maxDateString = BBBCoreConstants.BLANK;
    			if(currentSiteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) || currentSiteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)){
                    minDateString = minDate.get(Calendar.DAY_OF_MONTH) + "/" + (minDate.get(Calendar.MONTH) + 1);
                    maxDateString = maxDate.get(Calendar.DAY_OF_MONTH) + "/" + (maxDate.get(Calendar.MONTH) + 1);
                } else {
                    minDateString = (minDate.get(Calendar.MONTH) + 1) + "/" + minDate.get(Calendar.DAY_OF_MONTH);
                    maxDateString = (maxDate.get(Calendar.MONTH) + 1) + "/" + maxDate.get(Calendar.DAY_OF_MONTH);
                }
                this.logDebug("minDateString after format  " + minDateString + "  maxDateString after format "
                                + maxDateString);
                return minDateString + "-" + maxDateString;
            } finally {
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getExpectedDeliveryDate");
            }
        }
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }

    /**
     * Gets the shipping method.
     *
     * @param shippingMethod the shipping method
     * @return RepositoryItem
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public RepositoryItem getShippingMethod(String shippingMethod)
                    throws BBBBusinessException, BBBSystemException {
    	
    	return getShippingRepositoryTools().getShippingMethod(shippingMethod);

    }

    /**
     * Gets the shipping duration.
     *
     * @param pShippingMethod the shipping method
     * @param pSiteId the site id
     * @return RepositoryItem
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final RepositoryItem getShippingDuration(final String pShippingMethod, final String pSiteId)
                    throws BBBBusinessException, BBBSystemException {
    	
    	return getShippingRepositoryTools().getShippingDuration(pShippingMethod, pSiteId);

    }

    /**
     * Gets the parent category.
     *
     * @param categoryId the category id
     * @param siteId the site id
     * @return Parent Category
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final Map<String, CategoryVO> getParentCategory(final String categoryId, final String siteId)
                    throws BBBBusinessException, BBBSystemException {
        this.logDebug("Catalog API Method Name [getParentCategory]categoryId[" + categoryId + "]");
        if ((categoryId != null) && !StringUtils.isEmpty(categoryId)) {
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getParentCategory");
                RepositoryItem categoryRepositoryItem = this.getCatalogRepository().getItem(categoryId,
                                BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
                if (categoryRepositoryItem != null) {
                    final CategoryVO categoryVO = new CategoryVO();
                    final Map<String, CategoryVO> parentCategoryMap = new HashMap<String, CategoryVO>();

                    String parent = this.getImmediateParentCat(categoryRepositoryItem, siteId);
                    this.logTrace("immediate parent of the category " + parent);
                    // if parent of a category is null it means it is a root category and should not be displayed
                    // if parent is not null add the category also in the map so that is displayed
                    @SuppressWarnings ("unchecked")
                    final Set<String> catSiteId = (Set<String>) categoryRepositoryItem.getPropertyValue("siteIds");
                    if (catSiteId.contains(siteId) && (parent != null)) {
                        categoryVO.setCategoryId(categoryId);
                        categoryVO.setCategoryName((String) categoryRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME));
                        if (categoryRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME) != null) {
                            categoryVO.setIsCollege((Boolean) categoryRepositoryItem
                                            .getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME));
                        }
                        parentCategoryMap.put("0", categoryVO);
                    }

                    if (categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY) != null) {
                        categoryVO.setPhantomCategory(((Boolean) categoryRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY)));
                    }

                    String isFromBreadcrumb = (String) ServletUtil.getCurrentRequest().getParameter(BBBCoreConstants.IS_FROM_BREADCRUMB);
                    if (isFromBreadcrumb==null) {
                        isFromBreadcrumb = BBBCoreConstants.FALSE;
					}
                    int count = 1;
                    while (parent != null) {
                        final CategoryVO childCategoryVO = new CategoryVO();
                        categoryRepositoryItem = this.getCatalogRepository().getItem(parent,
                                        BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
                        childCategoryVO.setCategoryId(categoryRepositoryItem.getRepositoryId());
                        childCategoryVO.setCategoryName((String) categoryRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME));
                        if (categoryRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME) != null) {
                            childCategoryVO.setIsCollege((Boolean) categoryRepositoryItem
                                            .getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME));
                        }
                        if (categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY) != null) {
                            childCategoryVO.setPhantomCategory(((Boolean) categoryRepositoryItem
                                            .getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY)));
                        }
                        parentCategoryMap.put(String.valueOf(count), childCategoryVO);
                        parent = this.getImmediateParentCat(categoryRepositoryItem, siteId);
                        if (!isFromBreadcrumb.equalsIgnoreCase(BBBCoreConstants.TRUE) && StringUtils.isEmpty(parent)) {
                            // remove the root category from the map
                            parentCategoryMap.remove(String.valueOf(count));
                            break;
                        }
                        count++;
                    }
                    return parentCategoryMap;
                }
                throw new BBBBusinessException(BBBCatalogErrorCodes.CATEGORY_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.CATEGORY_NOT_AVAILABLE_IN_REPOSITORY);

            } catch (final RepositoryException e) {
                this.logError("Catalog API Method Name [getParentCategory]: RepositoryException ");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
            } finally {
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getParentCategory");
            }
        }
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);

    }

    /**
     *  This method gets the first category from the set of fixed parent categories.
     *
     * @param categoryRepositoryItem the category repository item
     * @param siteId the site id
     * @return the immediate parent cat
     */
    private String getImmediateParentCat(final RepositoryItem categoryRepositoryItem, final String siteId) {
        String parent = null;
        @SuppressWarnings ("unchecked")
        final Set<RepositoryItem> parentCategorySet = (Set<RepositoryItem>) categoryRepositoryItem
                        .getPropertyValue(BBBCatalogConstants.FIXED_PARENT_CATEGORIES_PROPERTY_NAME);
        
        RepositoryItem parentRepositoryItem = null;
        String isFromBreadcrumb = (String) ServletUtil.getCurrentRequest().getParameter(BBBCoreConstants.IS_FROM_BREADCRUMB);
        if (isFromBreadcrumb==null) {
            isFromBreadcrumb = BBBCoreConstants.FALSE;
		}
        if(isFromBreadcrumb.equalsIgnoreCase(BBBCoreConstants.TRUE)) {
        	parentRepositoryItem = this.getActiveCategoryForSite(parentCategorySet, siteId);	
        } else{
        	parentRepositoryItem = this.getCategoryForSite(parentCategorySet, siteId);
        }
        if (parentRepositoryItem != null) {
            parent = parentRepositoryItem.getRepositoryId();
            this.logTrace(" applicable parent for category "
                            + categoryRepositoryItem.getRepositoryId()
                            + " has id  "
                            + parent
                            + " and name "
                            + parentRepositoryItem
                                            .getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME));
        } else {
            this.logTrace(" No applicable parent for category " + categoryRepositoryItem.getRepositoryId());
        }

        return parent;
    }

    /**
     * Gets the category for site.
     *
     * @param parentCategorySet the parent category set
     * @param siteId the site id
     * @return the category for site
     */

    public RepositoryItem getCategoryForSite(final Set<RepositoryItem> parentCategorySet, final String siteId) {
        RepositoryItem categoryRepositoryItem = null;
        if ((parentCategorySet != null) && !parentCategorySet.isEmpty()) {
            this.logTrace("Parent category set is not null values for parent category::" + parentCategorySet);
            for (final RepositoryItem catRepo : parentCategorySet) {
                @SuppressWarnings ("unchecked")
                final Set<String> catSiteId = (Set<String>) catRepo.getPropertyValue("siteIds");
                this.logTrace("sites applicable for potential parent with id::" + catRepo.getRepositoryId() + " are "
                                + catSiteId + " and the current site id is " + siteId);
                if (catSiteId.contains(siteId)) {
                    categoryRepositoryItem = catRepo;
                    this.logTrace("Category id " + catRepo.getRepositoryId() + " is the selected parent ");
                    break;
                }
            }
        } else {
            this.logTrace("Parent category set is NULL  ");
        }
        return categoryRepositoryItem;
    }
    
    /**
     * Gets the active category for site.
     *
     * @param parentCategorySet the parent category set
     * @param siteId the site id
     * @return the active category for site
     */

    public RepositoryItem getActiveCategoryForSite(final Set<RepositoryItem> parentCategorySet, final String siteId) {
        RepositoryItem categoryRepositoryItem = null;
        if ((parentCategorySet != null) && !parentCategorySet.isEmpty()) {
            this.logTrace("Parent category set is not null values for parent category::" + parentCategorySet);
            for (final RepositoryItem catRepo : parentCategorySet) {
                @SuppressWarnings ("unchecked")
                final Set<String> catSiteId = (Set<String>) catRepo.getPropertyValue("siteIds");
                this.logTrace("sites applicable for potential parent with id::" + catRepo.getRepositoryId() + " are "
                                + catSiteId + " and the current site id is " + siteId);
                if (catRepo.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) != null) {
	                boolean catDisable = (Boolean) catRepo.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME);
	                if (catSiteId.contains(siteId) && !catDisable) {
	                    categoryRepositoryItem = catRepo;
	                    this.logTrace("Category id " + catRepo.getRepositoryId() + " is the selected parent ");
	                    break;
	                }
                }
            }
        } else {
            this.logTrace("Parent category set is NULL  ");
        }
        return categoryRepositoryItem;
    }
    
    /**
     *  This method gets the immediate parent category Id.
     *
     * @param productId the product id
     * @param siteId the site id
     * @return Parent Category
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    public final String getImmediateParentCategoryForProduct(String productId, String siteId)
    	    throws BBBBusinessException, BBBSystemException
    	  {
    	    logDebug("Catalog API Method Name [getImmediateParentCategoryForProduct] productId[" + productId + "]");
    	    String categoryId = null;
    	    if ((productId != null) && (!StringUtils.isEmpty(productId)))
    	    {
    	      try
    	      {
    	        BBBPerformanceMonitor.start("Catalog_Api_Call getImmediateParentCategoryForProduct");
    	        RepositoryItem productRepositoryItem = getCatalogRepository().getItem(productId, 
    	          "product");
    	        if (productRepositoryItem != null)
    	        {
    	          logDebug("productRepositoryItem is not null for product id " + productId);
    	          
    	          Set<RepositoryItem> parentCategorySet = (Set)productRepositoryItem
    	            .getPropertyValue("parentCategories");
    	          RepositoryItem categoryRepositoryItem = getCategoryForSite(parentCategorySet, siteId);
    	          if (categoryRepositoryItem != null)
    	          {
    	            categoryId = categoryRepositoryItem.getRepositoryId();
    	          }
    	          return categoryId;
    	        }
    	      }
    	      catch (RepositoryException e)
    	      {
    	        logError("Catalog API Method Name [getImmediateParentCategoryForProduct]: RepositoryException ");
    	        throw new BBBSystemException("2003", 
    	          "2003", e);
    	      }
    	      finally
    	      {
    	        BBBPerformanceMonitor.end("Catalog_Api_Call getImmediateParentCategoryForProduct");
    	      }
    	    }
    	    throw new BBBBusinessException("2006", 
    	      "2006");
    	  }
    
    /**
     *  This method gets the map of parent order and category parent id for a particular product The order goes from
     * category being at 0 level and then its parent and so on.
     *
     * @param productId the product id
     * @param siteId the site id
     * @return Parent Category
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public  Map<String, CategoryVO> getParentCategoryForProduct(final String productId, final String siteId)
                    throws BBBBusinessException, BBBSystemException {
        this.logDebug("Catalog API Method Name [getParentCategoryForProduct] productId[" + productId + "]");
        if ((productId != null) && !StringUtils.isEmpty(productId)) {
            final Map<String, CategoryVO> parentCategoryMap = new HashMap<String, CategoryVO>();
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getParentCategoryForProduct");
                final RepositoryItem productRepositoryItem = this.getCatalogRepository().getItem(productId,
                                BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);

                if (productRepositoryItem != null) {
                    this.logDebug("productRepositoryItem is not null for product id " + productId);
                    @SuppressWarnings ("unchecked")
                    final Set<RepositoryItem> parentCategorySet = (Set<RepositoryItem>) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.PARENT_CATEGORIES_PROPERTY_NAME);
                    
                    RepositoryItem categoryRepositoryItem = null;
                    String isFromBreadcrumb = (String) ServletUtil.getCurrentRequest().getParameter(BBBCoreConstants.IS_FROM_BREADCRUMB);
                    if (isFromBreadcrumb==null) {
                        isFromBreadcrumb = BBBCoreConstants.FALSE;
					}
                    if(isFromBreadcrumb.equalsIgnoreCase(BBBCoreConstants.TRUE)) {
                    	categoryRepositoryItem = this.getActiveCategoryForSite(parentCategorySet, siteId);	
                    } else{
                    	categoryRepositoryItem = this.getCategoryForSite(parentCategorySet, siteId);
                    }
                    
                    if (categoryRepositoryItem != null) {
                        final CategoryVO categoryVO = new CategoryVO();
                        final String categoryId = categoryRepositoryItem.getRepositoryId();
                        this.logTrace(categoryId
                                        + " category id with display name "
                                        + (String) categoryRepositoryItem
                                                        .getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME)
                                        + " is the selected 0th level parent for product id " + productId);
                        categoryVO.setCategoryId(categoryId);
                        categoryVO.setCategoryName((String) categoryRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME));
                        if (categoryRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME) != null) {
                            categoryVO.setIsCollege((Boolean) categoryRepositoryItem
                                            .getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME));
                        }
                        if (categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY) != null) {
                            categoryVO.setPhantomCategory(((Boolean) categoryRepositoryItem
                                            .getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY)));
                        }
                        parentCategoryMap.put("0", categoryVO);
                        String parent = this.getImmediateParentCat(categoryRepositoryItem, siteId);
                        int count = 1;
                        this.logTrace("Moving up the hierarchy to get ancestor tree count value :" + count);

                        while (parent != null) {

                            final CategoryVO childCategoryVO = new CategoryVO();
                            categoryRepositoryItem = this.getCatalogRepository().getItem(parent,
                                            BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);

                            childCategoryVO.setCategoryId(categoryRepositoryItem.getRepositoryId());
                            childCategoryVO.setCategoryName((String) categoryRepositoryItem
                                            .getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME));
                            if (categoryRepositoryItem
                                            .getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME) != null) {
                                childCategoryVO.setIsCollege((Boolean) categoryRepositoryItem
                                                .getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME));
                            }
                            if (categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY) != null) {
                                childCategoryVO.setPhantomCategory(((Boolean) categoryRepositoryItem
                                                .getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY)));
                            }
                            this.logTrace("Adding category id  :"
                                            + categoryRepositoryItem.getRepositoryId()
                                            + " with category Name"
                                            + (String) categoryRepositoryItem
                                                            .getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME)
                                            + " at  " + count + " th level of ancestral tree");
                            parentCategoryMap.put(String.valueOf(count), childCategoryVO);
                            parent = this.getImmediateParentCat(categoryRepositoryItem, siteId);
                            this.logTrace("The next potential parent category is " + parent);
                            if (!isFromBreadcrumb.equalsIgnoreCase(BBBCoreConstants.TRUE) && StringUtils.isEmpty(parent)) {
                                this.logTrace("The parent category is null  that is last parent "
                                                + categoryRepositoryItem.getRepositoryId() + " was the commerce root"
                                                + "Hence removing " + categoryRepositoryItem.getRepositoryId()
                                                + " from the Map of ancestors at position " + count);

                                parentCategoryMap.remove(String.valueOf(count));

                                break;
                            }
                            count++;
                        }
                    }
                    else
                    {
						this.logError(new StringBuilder("Product Id ").append(productId)
								.append(" does not have any parent for site ").append(siteId).toString());
                    }
                        return parentCategoryMap;
                    
                    }
                this.logDebug("Product Id " + productId + " is not present in the repository");
                throw new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);

            } catch (final RepositoryException e) {
                this.logError("Catalog API Method Name [getParentCategoryForProduct]: RepositoryException ");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
            } finally {
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getParentCategoryForProduct");
            }
        }
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }

    /**
     *  The method sorts the list of sku in the category buckets applicable for the registry.
     *
     * @param skuIdList the sku id list
     * @param registryCode the registry code
     * @param siteId the site id
     * @return SKU Sorted by Category
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @SuppressWarnings ("unchecked")
    public final Map<String, List<SKUDetailVO>> sortSkuByCategory(final List<String> skuIdList,
                    final String registryCode, final String siteId) throws BBBBusinessException, BBBSystemException {

            final StringBuilder debug = new StringBuilder(30);
            debug.append("Catalog API Method Name [sortSkuByCategory] siteId[").append(siteId)
                            .append("] registryCode ").append(registryCode);
            this.logDebug(debug.toString());

        final String defaultCat = this.getContentCatalogConfigration(this.getOthersCatKey()).get(0);
        if ((skuIdList != null) && !skuIdList.isEmpty()) {
            Map<String, List<SKUDetailVO>> sortedSkuMap = new HashMap<String, List<SKUDetailVO>>();
            for (int i = 0; i < skuIdList.size(); i++) {
                final String skuId = skuIdList.get(i);
                try {
                    final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId,
                                    BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
                    String catName = null;
                    if (skuRepositoryItem != null) {
                        if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) != null) {

                            final String regQuery = "registryCatTypes= ?0";
                            final Object[] regParam = new Object[1];
                            regParam[0] = registryCode;
                            this.logDebug("Executing the regQuery" + regQuery);
                            final RepositoryItem[] regTypeItem = this.executeRQLQuery(regQuery, regParam,
                                            BBBCatalogConstants.REGISTRY_TYPE_CATEGORY_ITEM_DESCRIPTOR,
                                            this.getRegCatRepo());
                            this.logDebug("Applicable Category Name :" + catName + " for sku :" + skuId);
                            if ((regTypeItem != null) && (regTypeItem.length > 0)) {
                                final RepositoryItem regTypeItemElement = regTypeItem[0];

                                final List<RepositoryItem> catFrmRegSet = (List<RepositoryItem>) regTypeItemElement
                                                .getPropertyValue(BBBCatalogConstants.REGISTRY_CAT_REG_TYPE_PROPERTY_NAME);
                                boolean isCatInReg = false;
                                final RepositoryItem jdaSubDeptFrmSkuItem = (RepositoryItem) skuRepositoryItem
                                                .getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME);
                                if ((catFrmRegSet != null) && !catFrmRegSet.isEmpty()) {
                                    for (final RepositoryItem catFrmRegId : catFrmRegSet) {

                                        String catSiteId = (String) catFrmRegId
                                                        .getPropertyValue(BBBCatalogConstants.SITE_ID_SKU_RELATION_PROPERTY_NAME); 
                                        	if(catSiteId.equals(BBBCoreConstants.SITE_BAB_US) && siteId.equals("TBS_BedBathUS")){
                                        		catSiteId="TBS_BedBathUS";
                            				}else if(catSiteId.equals(BBBCoreConstants.SITE_BBB) && siteId.equals("TBS_BuyBuyBaby")){
                            					catSiteId="TBS_BuyBuyBaby";
                            				}else if(catSiteId.equals(BBBCoreConstants.SITE_BAB_CA) && siteId.equals("TBS_BedBathCanada")){
                            					catSiteId="TBS_BedBathCanada";
                            				}
                                        if (catSiteId.equals(siteId) && (catFrmRegId.getPropertyValue(BBBCatalogConstants.JDA_SUB_CAT) != null)
                                                && ((Set<RepositoryItem>) catFrmRegId
                                                        .getPropertyValue(BBBCatalogConstants.JDA_SUB_CAT))
                                                        .contains(jdaSubDeptFrmSkuItem)) {
                                                catName = (String) catFrmRegId
                                                                .getPropertyValue(BBBCatalogConstants.CATEGORY_NAME_REG_CAT_PROPERTY_NAME);
                                                isCatInReg = true;
                                                this.logDebug(" Sub Cat Id : " + jdaSubDeptFrmSkuItem.getRepositoryId()
                                                                + " of the SKU : " + skuRepositoryItem
                                                                + "  is Applicable for the category bucket :" + catName
                                                                + " of the category Id :"
                                                                + catFrmRegId.getRepositoryId());
                                                break;
                                        }
                                    }
                                    if (isCatInReg) {
                                        this.logDebug("Category Name " + catName + " is applicable for registry code "
                                                        + registryCode);
                                        sortedSkuMap = this.addOrUpdateMap(sortedSkuMap, skuId, catName, siteId);
                                    } else {
                                        this.logDebug(" Category name " + catName
                                                        + " is not applicable for registry code " + registryCode);
                                        sortedSkuMap = this.addOrUpdateMap(sortedSkuMap, skuId, defaultCat, siteId);
                                    }
                                } else {
                                    this.logDebug("The category :" + catName
                                                    + " is not associated with any registry Adding the sku :" + skuId
                                                    + " in the default category:" + defaultCat);
                                    sortedSkuMap = this.addOrUpdateMap(sortedSkuMap, skuId, defaultCat, siteId);
                                }
                            } else {
                                this.logDebug("The registry type  :" + registryCode
                                                + " is present in the registry mapping repository Adding sku :" + skuId
                                                + " in the default category:" + defaultCat);
                                sortedSkuMap = this.addOrUpdateMap(sortedSkuMap, skuId, defaultCat, siteId);
                            }
                        } else {
                            this.logDebug("JDA dept is not present in the registry catalog Map repository:Adding the sku :"
                                            + skuId + " in the default category:" + defaultCat);
                            sortedSkuMap = this.addOrUpdateMap(sortedSkuMap, skuId, defaultCat, siteId);
                        }
                    } else {
                        this.logDebug("Sku has no Jda dept associated to it:Adding the sku :" + skuId
                                        + " in the default category:" + defaultCat);
                        sortedSkuMap = this.addOrUpdateMap(sortedSkuMap, skuId, defaultCat, siteId);
                    }

                } catch (final RepositoryException e) {
                    this.logError("Catalog API Method Name [sortSkuByCategory]: RepositoryException ");
                    throw new BBBSystemException(
                                    BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                    BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
                }
            }
            return sortedSkuMap;
        }

        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);

    }

    /**
     *  The method checks if the map already has the key present if yes then it adds the skuVo in the already created
     * list as the map value else it creates new list adds the skuvo in it and puts it in teh map.
     *
     * @param sortedSkuMap the sorted sku map
     * @param skuId the sku id
     * @param key the key
     * @param siteId the site id
     * @return the map
     */
    private Map<String, List<SKUDetailVO>> addOrUpdateMap(final Map<String, List<SKUDetailVO>> sortedSkuMap,
                    final String skuId, final String key, final String siteId) {
        this.logDebug(" adding skuId:" + skuId + "  in the bucket  :" + key);
        try {
        	String keyDelimeter = "_";
        	 SKUDetailVO skuVo = null;
        	int indexOfDelimeter = skuId.indexOf(keyDelimeter);
			String skuIDFromKey = skuId;
			if (indexOfDelimeter > -1) {
				skuIDFromKey = skuId.substring(0, indexOfDelimeter);
				 skuVo = this.getSKUDetails(siteId, skuIDFromKey, true, false, true);
			}
			else{
             skuVo = this.getSKUDetails(siteId, skuId, true, false, true);
             if(skuVo!=null && (BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(BBBUtility.getChannel()) || (BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(BBBUtility.getChannel())))){
            	 skuVo.setDisplayURL(skuVo.getDisplayName().replaceAll("[^A-Za-z0-9]+", "-"));
			}
			}
			
			if(skuVo!=null){
           	 skuVo.setDisplayURL(skuVo.getDisplayName().replaceAll("[^A-Za-z0-9]+", "-"));
            } 
			
            if (sortedSkuMap.containsKey(key)) {
                this.logTrace("key " + key + " is already present adding skuVo in already created list");
                sortedSkuMap.get(key).add(skuVo);
            } else {
                final List<SKUDetailVO> skuList = new ArrayList<SKUDetailVO>();
                skuList.add(skuVo);
                this.logTrace("key "
                                + key
                                + " is new so creating new list,  adding skuVo in that list and putting in the map corresponding to key");
                sortedSkuMap.put(key, skuList);
            }
        } catch (final BBBSystemException e) {
            this.logError("catalog_1017: Ignore sku ID during sorting: " + skuId , e);
        } catch (final BBBBusinessException e) {
            this.logError("catalog_1018: Ignore sku ID during sorting: " + skuId , e);
        }
        return sortedSkuMap;
    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getPromoBoxForRegistry(java.lang.String)
     */
    @Override
    public final PromoBoxVO getPromoBoxForRegistry(final String registryTypeId) throws BBBSystemException {
        this.logDebug("Catalog API Method Name [getPromoBoxForRegistry] registryTypeId[" + registryTypeId + "]");
        PromoBoxVO pVO = null;
        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getPromoBoxForRegistry");
            final RepositoryView regCatView = this.getRegCatRepo().getView(
                            BBBCatalogConstants.REGISTRY_TYPE_CATEGORY_ITEM_DESCRIPTOR);
            final QueryBuilder queryBuilder = regCatView.getQueryBuilder();
            final QueryExpression pProperty = queryBuilder
                            .createPropertyQueryExpression(BBBCatalogConstants.REG_CAT_TYPE_REG_CAT_PROPERTY_NAME);
            final QueryExpression pValue = queryBuilder.createConstantQueryExpression(registryTypeId);
            final Query query = queryBuilder.createComparisonQuery(pProperty, pValue, QueryBuilder.EQUALS);
            final RepositoryItem[] regTypeItem = regCatView.executeQuery(query);

            if (regTypeItem != null) {
                final RepositoryItem promoBox = (RepositoryItem) regTypeItem[0]
                                .getPropertyValue(BBBCatalogConstants.REG_PROMO_BOX_PROPERTY_NAME);
                pVO = new PromoBoxVO();
                if (promoBox != null) {
                    // sets PromoBoxVO details
                    pVO.setId(promoBox.getRepositoryId());
                    pVO.setImageAltText((String) promoBox
                                    .getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROMOBOX_IMAGE_ALT_TEXT));
                    pVO.setImageLink((String) promoBox
                                    .getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROMOBOX_IMAGE_LINK));
                    pVO.setImageMapName((String) promoBox
                                    .getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROMOBOX_IMAGE_MAP_NAME));
                    pVO.setImageURL((String) promoBox
                                    .getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROMOBOX_IMAGE_URL));
                    pVO.setHeight((String) promoBox
                                    .getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROMOBOX_HEIGHT));
                    pVO.setWidth((String) promoBox
                                    .getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROMOBOX_WIDTH));
                }
            }
        } catch (final RepositoryException e) {

            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getPromoBoxForRegistry");
        }

        return pVO;
    }

    /**
     *  The method implements the logic to get all categories applicable for a registry type.
     *
     * @param registryTypeId the registry type id
     * @return Map of category Name and RegistryCategoryMapVO that has details like category id associated with the
     *         category bucket and if add more items button has to be displayed for the for the bucket Note that these
     *         categories and not the same as those in catalog.These are merely the buckets into which sku will be
     *         bucketed. They however may have a category id associated to them if add more items button is to be
     *         displayed so that user can be directed to a particular category landing page.
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public final Map<String, RegistryCategoryMapVO> getCategoryForRegistry(final String registryTypeId)
                    throws BBBSystemException, BBBBusinessException {

        this.logDebug("Catalog API Method Name [getCategoryForRegistry] registryTypeId[" + registryTypeId + "]");
        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getCategoryForRegistry");
            final RepositoryView regCatView = this.getRegCatRepo().getView(
                            BBBCatalogConstants.REGISTRY_TYPE_CATEGORY_ITEM_DESCRIPTOR);
            final QueryBuilder queryBuilder = regCatView.getQueryBuilder();
            final QueryExpression pProperty = queryBuilder
                            .createPropertyQueryExpression(BBBCatalogConstants.REG_CAT_TYPE_REG_CAT_PROPERTY_NAME);
            final QueryExpression pValue = queryBuilder.createConstantQueryExpression(registryTypeId);
            final Query query = queryBuilder.createComparisonQuery(pProperty, pValue, QueryBuilder.EQUALS);
            final RepositoryItem[] regTypeItem = regCatView.executeQuery(query);

            if ((regTypeItem != null) && (regTypeItem.length > 0)) {
                @SuppressWarnings ("unchecked")
                final List<RepositoryItem> regCatSet = (List<RepositoryItem>) regTypeItem[0]
                                .getPropertyValue(BBBCatalogConstants.REGISTRY_CAT_REG_TYPE_PROPERTY_NAME);
                if ((regCatSet != null) && !regCatSet.isEmpty()) {
                    this.logDebug("categories are applicable for registry type " + registryTypeId);
                    final Map<String, RegistryCategoryMapVO> applicableCatRegistry = new LinkedHashMap<String, RegistryCategoryMapVO>();
                    String catName = EMPTYSTRING;
                    for (final RepositoryItem regCatMapItem : regCatSet) {
                        final RegistryCategoryMapVO regCat = new RegistryCategoryMapVO();

                        if (regCatMapItem.getPropertyValue(BBBCatalogConstants.CATEGORY_ID_REG_CAT_PROPERTY_NAME) != null) {
                            regCat.setCatId(((RepositoryItem) regCatMapItem
                                            .getPropertyValue(BBBCatalogConstants.CATEGORY_ID_REG_CAT_PROPERTY_NAME))
                                            .getRepositoryId());
                        }
                        if (regCatMapItem.getPropertyValue(BBBCatalogConstants.CATEGORY_NAME_REG_CAT_PROPERTY_NAME) != null) {
                            catName = (String) regCatMapItem
                                            .getPropertyValue(BBBCatalogConstants.CATEGORY_NAME_REG_CAT_PROPERTY_NAME);
                            regCat.setCatName(catName);
                        }
                        if (regCatMapItem.getPropertyValue(BBBCatalogConstants.ADD_ITEM_FLAG_REG_CAT_PROPERTY_NAME) != null) {
                            regCat.setAddItemFlag(((Boolean) regCatMapItem
                                            .getPropertyValue(BBBCatalogConstants.ADD_ITEM_FLAG_REG_CAT_PROPERTY_NAME))
                                            .booleanValue());
                        }
                        if (regCatMapItem.getPropertyValue(BBBCatalogConstants.RECOMMENDED_FLAG_REG_CAT_PROPERTY_NAME) != null) {
                            regCat.setRecommendedCatFlag(((Boolean) regCatMapItem
                                            .getPropertyValue(BBBCatalogConstants.RECOMMENDED_FLAG_REG_CAT_PROPERTY_NAME))
                                            .booleanValue());
                        }
                        if ((regCat.getCatId() != null) && (regCat.getCatName() != null)) {
                            regCat.setCatSeoUrl(this.getCategorySeoLinkGenerator().formatUrl(regCat.getCatId(),
                                            regCat.getCatName()));
                        }

                        if (regCat.getCatId() != null) {
                            this.setCatImage(regCat);
                        }

                        this.logDebug(regCat.toString());
                        applicableCatRegistry.put(catName, regCat);
                    }
                    return applicableCatRegistry;

                }
                this.logDebug("No categories are applicable for registry type " + registryTypeId);
                throw new BBBSystemException(BBBCatalogErrorCodes.NO_APPLICABLE_CATEGORY_NAMES_FOR_GIVEN_REGISTRY,
                                BBBCatalogErrorCodes.NO_APPLICABLE_CATEGORY_NAMES_FOR_GIVEN_REGISTRY);
            }
            this.logDebug("No data available in registry mapping repository for registryTypeId " + registryTypeId);
            throw new BBBSystemException(
                            BBBCatalogErrorCodes.REGISTRY_TYPE_NOT_AVAILABLE_IN_REGISTRY_CATALOG_MAP_REPOSITORY,
                            BBBCatalogErrorCodes.REGISTRY_TYPE_NOT_AVAILABLE_IN_REGISTRY_CATALOG_MAP_REPOSITORY);

        } catch (final RepositoryException e) {

            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCategoryForRegistry");
        }

    }

    /**
     * Sets the cat image.
     *
     * @param regCat the new cat image
     * @throws BBBSystemException the BBB system exception
     */
    private void setCatImage(final RegistryCategoryMapVO regCat) throws BBBSystemException {
        this.logDebug("Catalog API Method Name [setCatImage] categoryId[" + regCat.getCatId() + "]");
        BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " setCatImage");
        try {
            final RepositoryView catView = this.getCatalogRepository().getView(
                            BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);

            final QueryBuilder queryBuilder = catView.getQueryBuilder();
            final QueryExpression pProperty = queryBuilder.createPropertyQueryExpression("id");
            final QueryExpression pValue = queryBuilder.createConstantQueryExpression(regCat.getCatId());
            final Query query = queryBuilder.createComparisonQuery(pProperty, pValue, QueryBuilder.EQUALS);
            final RepositoryItem[] items = catView.executeQuery(query);

            if ((items != null) && (items.length > 0)) {
                for (final RepositoryItem item : items) {
                    final String categoryImage = (String) item
                                    .getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_CATEGORY_PROPERTY_NAME);
                    regCat.setCatImage(categoryImage);
                }
            }
        } catch (final RepositoryException e) {
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " setCatImage");
        }

    }

    /**
     *  Get sku detail from UPC.
     *
     * @param upcCode the upc code
     * @return the sku by upc
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final String getSkuByUPC(final String upcCode) throws BBBBusinessException, BBBSystemException {
        RepositoryView mView = null;
        RepositoryItem[] skuRepositoryItem = null;
        String skuId = null;
        try {

                this.logDebug("Catalog API Method Name [getSkuByUPC]");
                this.logDebug("Parameter upcCode[" + upcCode + "]");

            mView = this.getCatalogRepository().getView(BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
            RqlStatement statement = RqlStatement.parseRqlStatement("upc=?0 and not(siteIds is null)");
            Object[] params = new Object[1];
            params[0] = upcCode;
            skuRepositoryItem = statement.executeQuery(mView, params);
            if ((skuRepositoryItem == null) || (skuRepositoryItem.length == 0)) {
                statement = RqlStatement.parseRqlStatement("upc=?0 and siteIds is null");
                params = new Object[1];
                params[0] = upcCode;
                skuRepositoryItem = statement.executeQuery(mView, params);
                if (skuRepositoryItem != null) {
                    skuId = skuRepositoryItem[0].getRepositoryId();
                } else {
                    this.logDebug("SKU_NOT_PRESENT_IN_REPOSITORY_FOR_GIVEN_UPC_CODE");
                    skuId = BBBCoreConstants.SKU_NOT_AVAILABLE;
                }
            } else {
                skuId = skuRepositoryItem[0].getRepositoryId();
            }
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getSkuByUPC]: RepositoryException ");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        }
        return skuId;
    }

    /**
     *  to fetch Jda dept for a particular sku.
     *
     * @param siteId the site id
     * @param skuId the sku id
     * @return JDA Department for SKU
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final String getJDADeptForSku(final String siteId, final String skuId)
                    throws BBBBusinessException, BBBSystemException {

        String jdaDept = null;
        try {
            // get sku repositoryitem to fetch jda related properties
            final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId,
                            BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
            if (skuRepositoryItem == null) {
                this.logDebug("Repository Item is null for SKuId " + skuId);
                throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
            } else if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) != null) {
                jdaDept = ((RepositoryItem) skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME)).getRepositoryId();

            }
            return jdaDept;
        } catch (final RepositoryException e) {

            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        }
    }

    /**
     *  The method sorts the sku by its price.
     *
     * @param skuIdPriceMap the sku id price map
     * @param registryCode the registry code
     * @param siteId the site id
     * @param country the country
     * @return SKU Sorted by Price
     * @throws BBBSystemException the BBB system exception
     */
    public final Map<String, List<SKUDetailVO>> sortSkuByPrice(final Map<String, Double> skuIdPriceMap,
                    final String registryCode, final String siteId,String country) throws BBBSystemException {
        this.logDebug("Catalog API Method Name [sortSkuByPrice] registryCode[" + registryCode + "] siteId " + siteId);
        RepositoryItem[] catalogItems = null;
        Map<String, List<SKUDetailVO>> unsortedMap = new HashMap<String, List<SKUDetailVO>>();
         RepositoryView catalogView=null;
        try {
        	if(!BBBUtility.isEmpty(country) && country.equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY)){
        		catalogView = this.getRegCatRepo().getView(
                        BBBCatalogConstants.REGISTRY_PRICE_BUCKET_ITEM_DESCRIPTOR_MX);
        	}
        	else{
        		catalogView = this.getRegCatRepo().getView(
                            BBBCatalogConstants.REGISTRY_PRICE_BUCKET_ITEM_DESCRIPTOR);
        	}
            final QueryBuilder queryBuilder = catalogView.getQueryBuilder();
            final Query getAllItemsQuery = queryBuilder.createUnconstrainedQuery();

            final SortDirectives sortDirectives = new SortDirectives();
            sortDirectives.addDirective(new SortDirective(BBBCatalogConstants.MIN_PRICE_REG_PRICE_PROPERTY_NAME,
                            SortDirective.DIR_ASCENDING));

            catalogItems = catalogView.executeQuery(getAllItemsQuery, new QueryOptions(0, -1, sortDirectives, null));
            if ((catalogItems != null) && (catalogItems.length > 0)) {

                final Set<String> skuPriceKey = skuIdPriceMap.keySet();
                for (final String skuId : skuPriceKey) {
                    final double skuPrice = skuIdPriceMap.get(skuId).doubleValue();
                    this.logDebug("Price of sku " + skuId + " is " + skuPrice);
                    for (final RepositoryItem catalogItem : catalogItems) {
                        final String priceRange = (String) catalogItem
                                        .getPropertyValue(BBBCatalogConstants.PRICE_RANGE_REG_PRICE_PROPERTY_NAME);
                        if ((catalogItem.getPropertyValue(BBBCatalogConstants.MIN_PRICE_REG_PRICE_PROPERTY_NAME) != null)
                                        && (catalogItem.getPropertyValue(BBBCatalogConstants.MAX_PRICE_REG_PRICE_PROPERTY_NAME) != null)) {
                            final double min = ((Double) catalogItem
                                            .getPropertyValue(BBBCatalogConstants.MIN_PRICE_REG_PRICE_PROPERTY_NAME))
                                            .doubleValue();
                            final double max = ((Double) catalogItem
                                            .getPropertyValue(BBBCatalogConstants.MAX_PRICE_REG_PRICE_PROPERTY_NAME))
                                            .doubleValue();
                            this.logDebug("Min price from repository " + min + " maz price from repository " + max
                                            + " price range " + priceRange);
                            if ((skuPrice >= min) && (skuPrice <= max)) {

                                unsortedMap = this.addOrUpdateMap(unsortedMap, skuId, priceRange, siteId);
                                this.logDebug("sku id " + skuId + " with price " + skuPrice
                                                + " belongs to price range " + priceRange);
                                break;
                            }
                        }
                    }
                }
            } else {
                this.logDebug("No data available in repository to sort as per price");
            }
        } catch (final RepositoryException e) {
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        }
        return unsortedMap;
    }
    
    /**
     *  The method sorts the sku by its price.
     *
     * @param skuIdPriceMap the sku id price map
     * @param registryCode the registry code
     * @param siteId the site id
     * @param mxConversionValue the mx conversion value
     * @return SKU Sorted by Price
     * @throws BBBSystemException the BBB system exception
     */
   public final Map<String, List<SKUDetailVO>> sortMxSkuByPrice(final Map<String, Double> skuIdPriceMap,
                   final String registryCode, final String siteId, final String mxConversionValue) throws BBBSystemException {
       this.logDebug("Catalog API Method Name [sortSkuByPrice] registryCode[" + registryCode + "] siteId " + siteId);
       RepositoryItem[] catalogItems = null;
       Map<String, List<SKUDetailVO>> unsortedMap = new HashMap<String, List<SKUDetailVO>>();

       try {

           final RepositoryView catalogView = this.getRegCatRepo().getView(
                           BBBCatalogConstants.REGISTRY_PRICE_BUCKET_ITEM_DESCRIPTOR);
           final QueryBuilder queryBuilder = catalogView.getQueryBuilder();
           final Query getAllItemsQuery = queryBuilder.createUnconstrainedQuery();

           final SortDirectives sortDirectives = new SortDirectives();
           sortDirectives.addDirective(new SortDirective(BBBCatalogConstants.MIN_PRICE_REG_PRICE_PROPERTY_NAME,
                           SortDirective.DIR_ASCENDING));

           catalogItems = catalogView.executeQuery(getAllItemsQuery, new QueryOptions(0, -1, sortDirectives, null));
           if ((catalogItems != null) && (catalogItems.length > 0)) {

               final Set<String> skuPriceKey = skuIdPriceMap.keySet();
               for (final String skuId : skuPriceKey) {
                   final double skuPrice = skuIdPriceMap.get(skuId).doubleValue();
                   this.logDebug("Price of sku " + skuId + " is " + skuPrice);
                   for (final RepositoryItem catalogItem : catalogItems) {
                       String priceRange = (String) catalogItem
                                       .getPropertyValue(BBBCatalogConstants.PRICE_RANGE_REG_PRICE_PROPERTY_NAME);
                       if(priceRange!=null){
                    	   if(!priceRange.contains("+")){
                        	   String prices[] = priceRange.split("-");
                        	   if(prices.length==1){
                        		   String minPrice = prices[0].replace("$", EMPTYSTRING);
                        		   String maxPrice = prices[1].replace("$", EMPTYSTRING);
                        		  int minIntegerPrice =   Integer.parseInt(minPrice)*15;
                        		  int maxIntegerPrice =   Integer.parseInt(maxPrice)*15;
                        		 String finalPriceRange =  "$"+String.valueOf(minIntegerPrice)+"-"+"$"+String.valueOf(maxIntegerPrice);
                        		 priceRange= finalPriceRange;
                        		
                        	   }
                        	   
                    	   }else{
                    		   priceRange =  priceRange.replace("$",EMPTYSTRING).replace("+",EMPTYSTRING).trim();
                    		   int integerPrice = Integer.parseInt(priceRange)*15;
                    		   priceRange = String.valueOf(integerPrice)+"+" ;
                    	   }
                    	 
                       }
                       if ((catalogItem.getPropertyValue(BBBCatalogConstants.MIN_PRICE_REG_PRICE_PROPERTY_NAME) != null)
                                       && (catalogItem.getPropertyValue(BBBCatalogConstants.MAX_PRICE_REG_PRICE_PROPERTY_NAME) != null)) {
                           final double min = ((Double) catalogItem
                                           .getPropertyValue(BBBCatalogConstants.MIN_PRICE_REG_PRICE_PROPERTY_NAME))
                                           .doubleValue();
                           final double max = ((Double) catalogItem
                                           .getPropertyValue(BBBCatalogConstants.MAX_PRICE_REG_PRICE_PROPERTY_NAME))
                                           .doubleValue();
                           this.logDebug("Min price from repository " + min + " maz price from repository " + max
                                           + " price range " + priceRange);
                           if ((skuPrice >= min*Double.valueOf(mxConversionValue)) && (skuPrice <= max*Double.valueOf(mxConversionValue))) {

                               unsortedMap = this.addOrUpdateMap(unsortedMap, skuId, priceRange, siteId);
                               this.logDebug("sku id " + skuId + " with price " + skuPrice
                                               + " belongs to price range " + priceRange);
                               break;
                           }
                       }
                   }
               }
           } else {
               this.logDebug("No data available in repository to sort as per price");
           }
       } catch (final RepositoryException e) {
           throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                           BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
       }
       return unsortedMap;
   }

    /**
     *  The method is returning the list of price ranges applicable for a registry code.
     *
     * @param registryCode the registry code
     * @param country the country
     * @return Price Ranges
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public final List<String> getPriceRanges(final String registryCode,String country) throws BBBSystemException, BBBBusinessException {
        this.logDebug("Catalog API Method Name [getPriceRanges] registryCode[" + registryCode + "]");
        RepositoryItem[] catalogItems = null;
        final List<String> priceList = new ArrayList<String>();
         RepositoryView catalogView=null;
        try {
        	if(!BBBUtility.isEmpty(country) && country.equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY))
        	{
        		catalogView = this.getRegCatRepo().getView(
                        BBBCatalogConstants.REGISTRY_PRICE_BUCKET_ITEM_DESCRIPTOR_MX);
        	}
        	else{
              catalogView = this.getRegCatRepo().getView(
                            BBBCatalogConstants.REGISTRY_PRICE_BUCKET_ITEM_DESCRIPTOR);
        	}
            final QueryBuilder queryBuilder = catalogView.getQueryBuilder();
            final Query getAllItemsQuery = queryBuilder.createUnconstrainedQuery();

            final SortDirectives sortDirectives = new SortDirectives();
            sortDirectives.addDirective(new SortDirective(BBBCatalogConstants.MIN_PRICE_REG_PRICE_PROPERTY_NAME,
                            SortDirective.DIR_ASCENDING));
            catalogItems = catalogView.executeQuery(getAllItemsQuery, new QueryOptions(0, -1, sortDirectives, null));
            if ((catalogItems != null) && (catalogItems.length > 0)) {
                for (final RepositoryItem catalogItem : catalogItems) {

                    final String priceRange = (String) catalogItem
                                    .getPropertyValue(BBBCatalogConstants.PRICE_RANGE_REG_PRICE_PROPERTY_NAME);
                    this.logDebug("price range key ::::: " + priceRange);
                    priceList.add(priceRange);

                }
            }
        } catch (final RepositoryException e) {
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        }
        return priceList;
    }
    
    
    /**
     *  The method is returning the list of mexico price ranges applicable for a registry code.
     *
     * @param registryCode the registry code
     * @return Price Ranges
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
   @Override
   public final List<String> getMxPriceRanges(final String registryCode) throws BBBSystemException, BBBBusinessException {
       this.logDebug("Catalog API Method Name [getPriceRanges] registryCode[" + registryCode + "]");
       RepositoryItem[] catalogItems = null;
       final List<String> priceList = new ArrayList<String>();
       try {

           final RepositoryView catalogView = this.getRegCatRepo().getView(
                           BBBCatalogConstants.REGISTRY_PRICE_BUCKET_ITEM_DESCRIPTOR);
           final QueryBuilder queryBuilder = catalogView.getQueryBuilder();
           final Query getAllItemsQuery = queryBuilder.createUnconstrainedQuery();

           final SortDirectives sortDirectives = new SortDirectives();
           sortDirectives.addDirective(new SortDirective(BBBCatalogConstants.MIN_PRICE_REG_PRICE_PROPERTY_NAME,
                           SortDirective.DIR_ASCENDING));
           catalogItems = catalogView.executeQuery(getAllItemsQuery, new QueryOptions(0, -1, sortDirectives, null));
           if ((catalogItems != null) && (catalogItems.length > 0)) {
               for (final RepositoryItem catalogItem : catalogItems) {

                    String priceRange = (String) catalogItem
                                   .getPropertyValue(BBBCatalogConstants.PRICE_RANGE_REG_PRICE_PROPERTY_NAME);
                   this.logDebug("price range key ::::: " + priceRange);
                   if(priceRange!=null){
                	   if(!priceRange.contains("+")){
                    	   String prices[] = priceRange.split("-");
                    	   if(prices.length==1){
                    		   String minPrice = prices[0].replace("$", EMPTYSTRING);
                    		   String maxPrice = prices[1].replace("$", EMPTYSTRING);
                    		  int minIntegerPrice =   Integer.parseInt(minPrice)*15;
                    		  int maxIntegerPrice =   Integer.parseInt(maxPrice)*15;
                    		 String finalPriceRange =  "$"+String.valueOf(minIntegerPrice)+"-"+"$"+String.valueOf(maxIntegerPrice);
                    		 priceList.add(finalPriceRange);
                    	   }
                    	   
                	   }else{
                		   priceRange =  priceRange.replace("$",EMPTYSTRING).replace("+",EMPTYSTRING).trim();
                		   int integerPrice = Integer.parseInt(priceRange)*15;
                           priceList.add(String.valueOf(integerPrice)+"+");
                	   }
                	 
                   }

               }
           }
       } catch (final RepositoryException e) {
           throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                           BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
       }
       return priceList;
   }
    

    /**
     *  The method calls the sort method depending on the sort type.
     *
     * @param skuIdList the sku id list
     * @param skuIdPriceMap the sku id price map
     * @param registryCode the registry code
     * @param siteId the site id
     * @param sortType the sort type
     * @param country the country
     * @return SKU Sorted by Registry
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final Map<String, List<SKUDetailVO>> sortSkubyRegistry(final List<String> skuIdList,
                    final Map<String, Double> skuIdPriceMap, final String registryCode, final String siteId,
                    final String sortType,String country) throws BBBBusinessException, BBBSystemException {
        if ((sortType != null) && BBBCatalogConstants.CATEGORY_SORT_TYPE.equalsIgnoreCase(sortType)) {
            return this.sortSkuByCategory(skuIdList, registryCode, siteId);
        }
        return this.sortSkuByPrice(skuIdPriceMap, registryCode, siteId,country);
    }
    
    
    /**
     *  The method calls the sort method depending on the sort type.
     *
     * @param skuIdList the sku id list
     * @param skuIdPriceMap the sku id price map
     * @param registryCode the registry code
     * @param siteId the site id
     * @param sortType the sort type
     * @param mxConversionValue the mx conversion value
     * @return SKU Sorted by Registry
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
   @Override
   public final Map<String, List<SKUDetailVO>> sortMxSkubyRegistry(final List<String> skuIdList,
                   final Map<String, Double> skuIdPriceMap, final String registryCode, final String siteId,
                   final String sortType, final String mxConversionValue) throws BBBBusinessException, BBBSystemException {
       if ((sortType != null) && BBBCatalogConstants.CATEGORY_SORT_TYPE.equalsIgnoreCase(sortType)) {
           return this.sortSkuByCategory(skuIdList, registryCode, siteId);
       }
       return this.sortMxSkuByPrice(skuIdPriceMap, registryCode, siteId, mxConversionValue);
   }

    /**
     *  This method returns the site description and default country code for a site.
     *
     * @param siteId the site id
     * @return Site Details
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public SiteVO getSiteDetailFromSiteId(final String siteId) throws BBBBusinessException, BBBSystemException {
    	
    	return getSiteRepositoryTools().getSiteDetailFromSiteId(siteId);
    }
    
	/**
	 * Gets the rest store details.
	 *
	 * @param storeId the store id
	 * @param appointmentType the appointment type
	 * @return the rest store details
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 * @throws RepositoryException the repository exception
	 */
	public final StoreDetails getRestStoreDetails(final String storeId,
			final String appointmentType) throws BBBBusinessException,
			BBBSystemException, RepositoryException {
		StoreDetails objStoreDetails = this.getRestStoreDetails(storeId);
		int preSelecetedServiceRef=0;
		if (appointmentType != null && objStoreDetails!=null) {
			Map<String, Boolean> appointmentMap = null;
			try {
				appointmentMap = getScheduleAppointmentManager()
						.checkAppointmentAvailable(storeId, appointmentType);
				preSelecetedServiceRef=getScheduleAppointmentManager().fetchPreSelectedServiceRef(appointmentType);
				objStoreDetails.setPreSelectedServiceRef(preSelecetedServiceRef);
				if (!BBBUtility.isMapNullOrEmpty(appointmentMap)) {
					final boolean appointmentEligible = getScheduleAppointmentManager()
							.canScheduleAppointmentForSiteId(
									SiteContextManager.getCurrentSiteId());
					getScheduleAppointmentManager().checkAppointmentEligible(
							objStoreDetails, appointmentMap,
							appointmentEligible);
				}
			} catch (BBBSystemException bbbException) {
				logError(SelfServiceConstants.ERROR_SEARCH_STORE_TECH_ERROR
						+ BBBCoreConstants.MAPQUESTSTORETYPE, bbbException);
				throw new BBBSystemException(
						SelfServiceConstants.ERROR_SEARCH_STORE_TECH_ERROR,
						SelfServiceConstants.ERROR_SEARCH_STORE_TECH_ERROR);
			}
		}

		return objStoreDetails;
	}

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getRestStoreDetails(java.lang.String)
     */
    @Override
    public final StoreDetails getRestStoreDetails(final String storeId)
                    throws BBBBusinessException, BBBSystemException, RepositoryException {

            this.logDebug("Catalog API Method Name [getRestStoreDetails]storeId[" + storeId + "]");

        StoreDetails objStoreDetails = null;
        String storeType = null;

        if ((null == storeId) || StringUtils.isEmpty(storeId)) {
            this.logDebug("Catalog API Method Name [getStoreDetail]: BBBBusinessException ");
            throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                            BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
        }
        try {

            storeType = this.getSearchStoreManager().getStoreType(SiteContextManager.getCurrentSiteId());
            if ((storeType != null) && !StringUtils.isEmpty(storeType)) {
                objStoreDetails = this.getSearchStoreManager().searchStoreById(storeId,
                                SiteContextManager.getCurrentSiteId(), storeType);

                if (null != objStoreDetails) {
                    this.logDebug(" Values return by getRestStoreDetails---- " + objStoreDetails.toString());
                    objStoreDetails = this.setSpecialityVO(objStoreDetails);
                    if(objStoreDetails.getStoreId().startsWith(BBBCoreConstants.STORE_CA_START))
                    {
                    	objStoreDetails.setBabyCanadaFlag(true);
                    }
                } else {
                	logInfo("Store Details are null");
                }
            } else {
                throw new BBBBusinessException("ERROR:STORE_TYPE_NOT_AVAILABLE ", "ERROR:STORE_TYPE_NOT_AVAILABLE");
            }
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getRestStoreDetails]: RepositoryException " + e);
        } catch (final BBBBusinessException e) {
            this.logError("Catalog API Method Name [getRestStoreDetails]: BBBBusinessException " , e);
            throw new BBBBusinessException(SelfServiceConstants.ERROR_SEARCH_STORE_BUSINESS_ERROR,
                            SelfServiceConstants.ERROR_SEARCH_STORE_BUSINESS_ERROR);
        } catch (final BBBSystemException e) {
            this.logError("Catalog API Method Name [getRestStoreDetails]: BBBSystemException " , e);
            throw new BBBSystemException(SelfServiceConstants.ERROR_SEARCH_STORE_TECH_ERROR,
                            SelfServiceConstants.ERROR_SEARCH_STORE_TECH_ERROR);
        }
        return objStoreDetails;
    }

    /**
     * Sets the speciality vo.
     *
     * @param storeDetailsObj the store details obj
     * @return the store details
     * @throws RepositoryException the repository exception
     */
    private StoreDetails setSpecialityVO(final StoreDetails storeDetailsObj) throws RepositoryException {
    	
    	return getStoreRepositoryTools().setSpecialityVO(storeDetailsObj);

    }

    /**
     *  This method returns the store details for a given store id.
     *
     * @param storeId the store id
     * @return Store Details
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public StoreVO getStoreDetails(final String storeId) throws BBBBusinessException, BBBSystemException {
    	
    	return getStoreRepositoryTools().getStoreDetails(storeId);
    }

    
    
    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getStoreAppointmentDetails(java.lang.String)
     */
    public StoreVO getStoreAppointmentDetails(final String storeId) {
    	
    	return getStoreRepositoryTools().getStoreAppointmentDetails(storeId);
    }    
   
    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getStoreSpecialityList(java.util.Set)
     */
    @Override
    public List<StoreSpecialityVO> getStoreSpecialityList(final Set<RepositoryItem> specialityItemSet) {
    	
    	return getGlobalRepositoryTools().getStoreSpecialityList(specialityItemSet);

    }    
    /**
     *  get comma delimited property roll up types for a product get different roll ups in comma separated string and for
     * each rollup collect all the values of the roll up of all the active sku of the product put the rollup and the
     * list of values in the map.
     *
     * @param rollUpTypesStringList the roll up types string list
     * @param skuRepositoryItems the sku repository items
     * @param checkActiveSKUFlag the check active sku flag
     * @return the roll up attribute for product
     */
    private Map<String, List<RollupTypeVO>> getRollUpAttributeForProduct(final String rollUpTypesStringList,
                    final List<RepositoryItem> skuRepositoryItems, boolean checkActiveSKUFlag) {
    	this.logDebug("Catalog API Method Name [getRollUpAttributeForProduct]Parameter rollUpTypesStringList["+rollUpTypesStringList+"]Parameter skuRepositoryItems["+skuRepositoryItems+"]");
		Map<String,List<RollupTypeVO>> rollupAttributes= new LinkedHashMap<String,List<RollupTypeVO>>();
		
		if(BBBUtility.isNotEmpty(rollUpTypesStringList)){
			String[] rollUpTypes=rollUpTypesStringList.split(",");
			if(!BBBUtility.isEmpty(rollUpTypes))
			{
				for(int sizeRollUp=0;sizeRollUp<rollUpTypes.length;sizeRollUp++){
					List<RollupTypeVO> skuPropertyList=new ArrayList<RollupTypeVO>();
					List<String> rollupValuesList = new ArrayList<String>();
					for(int index=0;index<skuRepositoryItems.size();index++){
						// Updated to check Product/SKU is Active Flag for SiteMap 
						if(checkActiveSKUFlag && this.isSkuActive(skuRepositoryItems.get(index))){
							RollupTypeVO rollupTypeVO=new RollupTypeVO();
							String propertyValueFromMap=this.getRollUpSkuPropertyMap().get(rollUpTypes[sizeRollUp].toLowerCase());
						
							//get the property of the sku to look up for the roll up type in rollUpTypes[sizeRollUp]
							if(propertyValueFromMap !=null && !StringUtils.isEmpty(propertyValueFromMap)){
								String propertyValue=(String) skuRepositoryItems.get(index).getPropertyValue(propertyValueFromMap );
								logTrace("name of roll up property"+rollUpTypes[sizeRollUp].toLowerCase()+"Name of sku property to get corresponding to rollup property "+propertyValueFromMap+
										"value of property "+propertyValueFromMap+" is "+propertyValue);
								if(propertyValue!=null && !rollupValuesList.contains(propertyValue)){
									rollupValuesList.add(propertyValue);
									//GS-130 Added Rollup Type for createRollUpVO()
									rollupTypeVO = createRollUpVO(propertyValue, skuRepositoryItems.get(index));
									skuPropertyList.add(rollupTypeVO);
								}
							}
						}else if (!checkActiveSKUFlag){
							RollupTypeVO rollupTypeVO=new RollupTypeVO();
							String propertyValueFromMap=this.getRollUpSkuPropertyMap().get(rollUpTypes[sizeRollUp].toLowerCase());
							//get the property of the sku to look up for the roll up type in rollUpTypes[sizeRollUp]
							if(propertyValueFromMap !=null && !StringUtils.isEmpty(propertyValueFromMap)){
								String propertyValue=(String) skuRepositoryItems.get(index).getPropertyValue(propertyValueFromMap );
								logTrace("name of roll up property"+rollUpTypes[sizeRollUp].toLowerCase()+"Name of sku property to get corresponding to rollup property "+propertyValueFromMap+
										"value of property "+propertyValueFromMap+" is "+propertyValue);
								if(propertyValue!=null && !rollupValuesList.contains(propertyValue)){
									rollupValuesList.add(propertyValue);
									//GS-130 Added Rollup Type for createRollUpVO()
									rollupTypeVO = createRollUpVO(propertyValue, skuRepositoryItems.get(index));
									skuPropertyList.add(rollupTypeVO);
								}
							}
						}
					}
					
					rollupAttributes.put(rollUpTypes[sizeRollUp], skuPropertyList);
				}

				//SORTINg ALPHABETICALLY FOR COLOR AND FINISH
				if(rollupAttributes.get("COLOR")!=null){
					Collections.sort(rollupAttributes.get("COLOR"));
				}

				else if(rollupAttributes.get("FINISH")!=null){
					Collections.sort(rollupAttributes.get("FINISH"));
				}
			}
		}

		return rollupAttributes;
	}

    /**
     * Gets the rollup for collection.
     *
     * @param rollUpTypesStringList the roll up types string list
     * @return the rollup for collection
     */
    private List<String> getRollupForCollection(final String rollUpTypesStringList) {
        List<String> rollUpList = new ArrayList<String>();
        if ((rollUpTypesStringList != null) && !rollUpTypesStringList.isEmpty()) {
            final String[] rollUpTypes = rollUpTypesStringList.split(",");
            rollUpList = Arrays.asList(rollUpTypes);
        }
        this.logTrace("list of roll up for collection  " + rollUpList);
        return rollUpList;
    }

    /**
     *  Creates a RolluptypeVO based on rollup parameter.
     *
     * @param propertyName the property name
     * @param skuRepositoryItem the sku repository item
     * @return the rollup type vo
     */
    private RollupTypeVO createRollUpVO(final String propertyName, final RepositoryItem skuRepositoryItem) {
        final RollupTypeVO rollupTypeVO = new RollupTypeVO();
        rollupTypeVO.setRollupAttribute(propertyName);
        if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME) != null) {
            final String swatchImage = (String) skuRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME);
            final String swatchImagePath = swatchImage;
            this.logTrace("Method[createRollUpVO] value of swatchImagePath " + swatchImagePath);
            rollupTypeVO.setSwatchImagePath(swatchImagePath);

            // START -- Added following Code for R2.1 Item - PDP Color Swatch Story

            // Setting the Large image path for the corrresponding Swatch.
            rollupTypeVO.setLargeImagePath((String) skuRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.LARGE_IMAGE_PROPERTY_NAME));

            // Setting the Thumbnail image path for the corrresponding Swatch.
            rollupTypeVO.setThumbnailImagePath((String) skuRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_PROPERTY_NAME));

            // Setting the Small image path for the corrresponding Swatch.
            rollupTypeVO.setSmallImagePath((String) skuRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_PROPERTY_NAME));

            // END -- Added following getters / setters for R2.1 Item - PDP Color Swatch Story
        }
        this.logTrace(rollupTypeVO.toString());
        return rollupTypeVO;
    }

    /**
     *  /** Sort list in bucket according to place holder property. Placeholder property will be a comma separated string
     * with values indicating the pages on which the attribute is applicable.
     *
     * @param attributeNameRepoItemMap the attribute name repo item map
     * @return Map where key is the page and value is the list of all attributes applicable for that page
     */
    private Map<String, List<AttributeVO>>
                    getSkuAttributeMap(final Map<String, RepositoryItem> attributeNameRepoItemMap) {
        this.logDebug("Catalog API Method Name [getSkuAttributeMap]Parameter Map of attribute Name as key and repositoryitem as value["
                        + attributeNameRepoItemMap + "]");
        final Map<String, List<AttributeVO>> skuAttributesMap = new HashMap<String, List<AttributeVO>>();

        if ((attributeNameRepoItemMap != null) && !(attributeNameRepoItemMap.isEmpty())) {
            final Set<String> attributeNameSet = attributeNameRepoItemMap.keySet();
            for (final String key : attributeNameSet) {
                final RepositoryItem attributeRepositoryItems = attributeNameRepoItemMap.get(key);
                final String placeHolderString = (String) attributeRepositoryItems
                                .getPropertyValue(BBBCatalogConstants.PLACE_HOLDER_ATTRIBUTE_PROPERTY_NAME);

                if ((placeHolderString != null) && !StringUtils.isEmpty(placeHolderString)) {
                    final String[] placeHolderList = placeHolderString.split(",");
                    for (final String element : placeHolderList) {
                        final String placeHolder = element.trim();

                        if (skuAttributesMap.containsKey(placeHolder)) {
                            this.logTrace(placeHolder + " key already in map adding value new attribute "
                                            + attributeRepositoryItems.getRepositoryId());
                            skuAttributesMap.get(placeHolder).add(new AttributeVO(attributeRepositoryItems));
                        } else {
                            this.logTrace("new key added to map [" + placeHolder + " ] adding  new attribute "
                                            + attributeRepositoryItems.getRepositoryId());
                            final List<AttributeVO> newAttributeList = new ArrayList<AttributeVO>();
                            newAttributeList.add(new AttributeVO(attributeRepositoryItems));
                            skuAttributesMap.put(placeHolder, newAttributeList);
                        }
                    }
                }
            }
        }
        return skuAttributesMap;
    }

    /**
     *  The method takes an attributeList and adds an repositoryItem of type attribute if that repository item is not
     * already present in the List if current date is within start and end date property in sku relation item descriptor
     * add the attribute repository item to attributeList if not already present if start and end date is null in
     * sku-relation item descriptor check if current date is within start and end date property in attribute item
     * descriptor and add the attribute repository item to attributeList if not already present.
     *
     * @param skuRepositoryItem the sku repository item
     * @param siteId the site id
     * @param attributeMap the attribute map
     * @param regionPromoAttr the region promo attr
     * @param currentZipEligibility the current zip eligibility
     * @return SKU Attribute List
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */

    @Override
    public  Map<String, RepositoryItem> getSkuAttributeList(final RepositoryItem skuRepositoryItem,
                    final String siteId, final Map<String, RepositoryItem> attributeMap, final String regionPromoAttr, final boolean currentZipEligibility)
                    throws BBBSystemException, BBBBusinessException {

            final StringBuilder debug = new StringBuilder(30);
            debug.append("Catalog API Method Name [getSkuAttributeMap] siteId[").append(siteId)
                            .append("] skuRepositoryItem ").append(skuRepositoryItem).append("] attributeList ")
                            .append(attributeMap);
            this.logDebug(debug.toString());

        try {
            // Set sku attributes
            // Edited as part of Instant preview story
            Date previewDate = new Date();
            if (this.isPreviewEnabled()) {
                previewDate = this.getPreviewDate();
            }
            @SuppressWarnings ("unchecked")
            final Set<RepositoryItem> skuAttributeRelationSet = (Set<RepositoryItem>) skuRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME);

            if (skuAttributeRelationSet != null) {
            	 boolean internationalShippingContext =false;
            	
            	BBBSessionBean sessionBean = (BBBSessionBean) resolveComponentFromRequest(BBBCoreConstants.SESSION_BEAN);
            	if(null!=sessionBean) {
            		internationalShippingContext = sessionBean.isInternationalShippingContext();
            		}
            	this.logTrace("skuAttributeRelationSet is not null " + skuAttributeRelationSet);
            	List<String> sddAttributeKeyList = null;
     			sddAttributeKeyList = this.getAllValuesForKey(BBBCmsConstants.SDD_KEY,
     						BBBCmsConstants.SDD_ATTRIBUTE_LIST);
            	String[] sddAttributes = null;
    			if (!BBBUtility.isListEmpty(sddAttributeKeyList)) {
    				sddAttributes = sddAttributeKeyList.get(0).split(BBBCoreConstants.COMMA);
    			}
                for (final RepositoryItem skuAttributeRelationItem : skuAttributeRelationSet) {
                        boolean isAttributeAvaialbelForSite=false;
                        if(null!=getBBBSiteToAttributeSiteMap().get(siteId)) {
                        	isAttributeAvaialbelForSite=(Boolean)skuAttributeRelationItem.getPropertyValue(getBBBSiteToAttributeSiteMap().get(siteId));
                        }
                        if (isAttributeAvaialbelForSite) {
                            final Date startDateOfSku = (Date) skuAttributeRelationItem
                                            .getPropertyValue(BBBCatalogConstants.START_DATE_SKU_RELATION_PROPERTY_NAME);
                            final Date endDateOfSku = (Date) skuAttributeRelationItem
                                            .getPropertyValue(BBBCatalogConstants.END_DATE_SKU_RELATION_PROPERTY_NAME);
                            final RepositoryItem skuAttributeRepositoryItem = (RepositoryItem) skuAttributeRelationItem
                                            .getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME);

                            // if start date and end date is null in skurelation itemdescriptor check for start and end
                            // date in attribute itemdescriptor
                            this.logDebug("start date of sku relation[" + startDateOfSku + "]end date of sku relation["
                                            + endDateOfSku + "]");
						if ((!checkAttributeIntlApplicability(skuAttributeRepositoryItem, internationalShippingContext)
								&& sameDayDeliverEligibility(skuAttributeRepositoryItem, currentZipEligibility, regionPromoAttr, sddAttributes))
								&& (BBBUtility.isAttributeApplicable(previewDate, startDateOfSku, endDateOfSku))) {
							//BBBSL-9655 | Adding Product level and Promo attribute check to allow null start date when end date is expired
                            this.logDebug("Attribute Applicable for sku");
                            this.logDebug("start date of attribute[" + startDateOfSku + "]");
                            this.logDebug("end date of attribute[" + endDateOfSku + "]");
                            final Date startDateOfAttrib = (Date) skuAttributeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.START_DATE_ATTRIBUTE_PROPERTY_NAME);
                            final Date endDateOfAttrib = (Date) skuAttributeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.END_DATE_ATTRIBUTE_PROPERTY_NAME);

	                            this.logDebug("start date of attribute[" + startDateOfAttrib + "]");
	                            this.logDebug("end date of attribute[" + endDateOfAttrib + "]");
	                         if(BBBUtility.isAttributeApplicable(previewDate, startDateOfAttrib, endDateOfAttrib))   {
                                    if (!attributeMap.containsKey(skuAttributeRepositoryItem.getRepositoryId())) {
                                    this.logDebug("adding attribute as it is not in list "
                                                        + skuAttributeRepositoryItem
                                                                        .getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME));
                                        attributeMap.put(skuAttributeRepositoryItem.getRepositoryId(),
                                                        skuAttributeRepositoryItem);
                                    } else {
                                    this.logDebug("attribute already in  list  "
                                                        + skuAttributeRepositoryItem
                                                                        .getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME));
                                    }
                                }
                            
                                }
                            }
                            }
               
            }
           
        } catch (final Exception e) {
            this.logError("Catalog API Method Name [getSkuAttributeList]: Exception ");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        }
       
       
        
        return attributeMap;
    }
    

    

	/**
	 * This method finds if the sku/product attribute is SDD eligible.
	 * 
	 * @param skuAttributeRepositoryItem
	 *            the sku attribute repository item
	 * @param currentZipEligibility
	 *            the current zip eligibility
	 * @param regionPromoAttr
	 *            the region promo attr
	 * @param sddAttributes
	 * @return true, if successful
	 */
	private boolean sameDayDeliverEligibility(RepositoryItem skuAttributeRepositoryItem, boolean currentZipEligibility,
			final String regionPromoAttr, String[] sddAttributes) {

		logDebug("BBBCatalogToolsImpl | sameDayDeliverEligibility Starts with currentZipEligibility and regionPromoAttr as"
				+ currentZipEligibility + ", " + regionPromoAttr);
		if (null == sddAttributes || sddAttributes.length <= 0) {
			logDebug("BBBCatalogToolsImpl | sameDayDeliverEligibility returning True, sddAttributes  is null or empty");
			return true;
		}
		final String repId = skuAttributeRepositoryItem.getRepositoryId();
		for (final String sddAttributeKey : sddAttributes) {
			if (repId.equals(sddAttributeKey.trim())) {
							if (!BBBUtility.isEmpty(regionPromoAttr)
									&& currentZipEligibility
									&& repId.equals(regionPromoAttr)) {
					logDebug("BBBCatalogToolsImpl | sameDayDeliverEligibility returning True");
					return true;
				} else {
					logDebug("BBBCatalogToolsImpl | sameDayDeliverEligibility returning false");
					return false;
				}

			}
		}
		logDebug("BBBCatalogToolsImpl | sameDayDeliverEligibility, No SDD attribute found returning True");
		return true;
	}

	/**
	 *  The method returns list of media vo associated with a product for a particular site.
	 *
	 * @param productId the product id
	 * @param siteId the site id
	 * @return Product Media
	 * @throws BBBSystemException the BBB system exception
	 */
    @Override
    public final List<MediaVO> getProductMedia(final String productId, String siteId) throws BBBSystemException {
        this.logDebug("Catalog API Method Name [getProductMedia] product Id " + productId + " siteId " + siteId);
        String currentSiteId = siteId;
        final List<MediaVO> productMedia = new ArrayList<MediaVO>();
        try {
            final RepositoryItem productRepositoryItem = this.getCatalogRepository().getItem(productId,
                            BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
            if ((productRepositoryItem != null)
                            && (productRepositoryItem
                                            .getPropertyValue(BBBCatalogConstants.MEDIA_RELN_PRODUCT_PROPERTY_NAME) != null)) {
                @SuppressWarnings ("unchecked")
                final List<RepositoryItem> prdtMediaItemList = (List<RepositoryItem>) productRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.MEDIA_RELN_PRODUCT_PROPERTY_NAME);
                if ((prdtMediaItemList != null) && !prdtMediaItemList.isEmpty()) {
                    final SimpleDateFormat dateFormat = new SimpleDateFormat(MM_DD_YYYY);
                    Date previewDate = null;
                    try {
                        previewDate = dateFormat.parse(dateFormat.format(new Date()));
                    } catch (final ParseException e) {
                        this.logError("Catalog API Method Name [getProductMedia]: Parsing Exception ");
                    }
                    for (final RepositoryItem prdtMediaRepoItem : prdtMediaItemList) {
                        final Date startDateOfMedia = (Date) prdtMediaRepoItem
                                        .getPropertyValue(BBBCatalogConstants.START_DATE_OTHER_MEDIA_PROPERTY_NAME);
                        final Date endDateOfMedia = (Date) prdtMediaRepoItem
                                        .getPropertyValue(BBBCatalogConstants.END_DATE_OTHER_MEDIA_PROPERTY_NAME);
                        // checking if media is active or not if it is not active then continue with other media items
                        if (!BBBUtility.isAttributeApplicable(previewDate, startDateOfMedia, endDateOfMedia)) {
                            continue;
                        }
                        // get all the sites for which the media is applicable
                        @SuppressWarnings ("unchecked")
                        final Set<RepositoryItem> mediaSites = (Set<RepositoryItem>) prdtMediaRepoItem
                                        .getPropertyValue(BBBCatalogConstants.SITES_OTHER_MEDIA_PROPERTY_NAME);
                        boolean mediaApplicableOnCurrSite = false;
                        for (final RepositoryItem siteItem : mediaSites) {
                            // if the site to which video is applicable is same as current site add the mediVO in the
                            // list
                        	if(currentSiteId.startsWith("TBS") ) {
                        		currentSiteId = currentSiteId.substring(4);
                    		}
                            if (siteItem.getRepositoryId().equalsIgnoreCase(currentSiteId)) {
                                this.logDebug(prdtMediaRepoItem.getRepositoryId()
                                                + " nmedia Id is applicable for site " + currentSiteId
                                                + " adding it the list of MediaVO for the product");
                                productMedia.add(populateMediaVO(prdtMediaRepoItem));
                                mediaApplicableOnCurrSite = true;
                                break;
                            }
                        }
                        if (mediaApplicableOnCurrSite) {
                            break;
                        }
                    }
                }

            }
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getProductMedia]: RepositoryException ");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        }
        return productMedia;
    }

    /**
     *  The method sets the values in the MediaVO.
     *
     * @param prdtMediaRepoItem the prdt media repo item
     * @return the media vo
     */
    private static MediaVO populateMediaVO(final RepositoryItem prdtMediaRepoItem) {
        final MediaVO mediaVO = new MediaVO();
        if (prdtMediaRepoItem.getPropertyValue(BBBCatalogConstants.MEDIA_TRANSCRIPT_OTHER_MEDIA_PROPERTY_NAME) != null) {
            mediaVO.setMediaTranscript((String) prdtMediaRepoItem
                            .getPropertyValue(BBBCatalogConstants.MEDIA_TRANSCRIPT_OTHER_MEDIA_PROPERTY_NAME));
        }
        if (prdtMediaRepoItem.getPropertyValue(BBBCatalogConstants.PROVIDER_OTHER_MEDIA_PROPERTY_NAME) != null) {
            mediaVO.setProviderId((String) prdtMediaRepoItem
                            .getPropertyValue(BBBCatalogConstants.PROVIDER_OTHER_MEDIA_PROPERTY_NAME));
        }
        if (prdtMediaRepoItem.getPropertyValue(BBBCatalogConstants.MEDIA_DESCRIPTION_OTHER_MEDIA_PROPERTY_NAME) != null) {
            mediaVO.setMediaDescription((String) prdtMediaRepoItem
                            .getPropertyValue(BBBCatalogConstants.MEDIA_DESCRIPTION_OTHER_MEDIA_PROPERTY_NAME));
        }
        if (prdtMediaRepoItem.getPropertyValue(BBBCatalogConstants.MEDIA_SOURCE_OTHER_MEDIA_PROPERTY_NAME) != null) {
            mediaVO.setMediaSource((String) prdtMediaRepoItem
                            .getPropertyValue(BBBCatalogConstants.MEDIA_SOURCE_OTHER_MEDIA_PROPERTY_NAME));
        }
        if (prdtMediaRepoItem.getPropertyValue(BBBCatalogConstants.COMMENTS_OTHER_MEDIA_PROPERTY_NAME) != null) {
            mediaVO.setComments((String) prdtMediaRepoItem
                            .getPropertyValue(BBBCatalogConstants.COMMENTS_OTHER_MEDIA_PROPERTY_NAME));
        }
        if (prdtMediaRepoItem.getPropertyValue(BBBCatalogConstants.MEDIA_TYPE_OTHER_MEDIA_PROPERTY_NAME) != null) {
            mediaVO.setMediaType((String) prdtMediaRepoItem
                            .getPropertyValue(BBBCatalogConstants.MEDIA_TYPE_OTHER_MEDIA_PROPERTY_NAME));
        }
        return mediaVO;
    }

    /**
     *  Sort list in bucket according to place holder property. Placeholder property will be a comma separated string
     * with values indicating the pages on which the attribute is applicable. We need to provide a map where key is the
     * placeholder and the value is the list of attributes that are applicable for that placeholder
     *
     * @param attributeNameRepoItemMap the attribute name repo item map
     * @return Map where key is the page and value is the list of all attributes applicable for that page
     */
    private Map<String, List<AttributeVO>> getProdAttributeMap(
                    final Map<String, RepositoryItem> attributeNameRepoItemMap) {
        this.logDebug("Catalog API Method Name [getProdAttributeMap]Parameter Map of attribute Name as key and repositoryitem as value["
                        + attributeNameRepoItemMap + "]");
        final Map<String, List<AttributeVO>> prodAttributesMap = new HashMap<String, List<AttributeVO>>();

        if (!BBBUtility.isMapNullOrEmpty(attributeNameRepoItemMap)) {
            final Set<String> attributeNameSet = attributeNameRepoItemMap.keySet();
            for (final String key : attributeNameSet) {
                final RepositoryItem attributeRepositoryItems = attributeNameRepoItemMap.get(key);
                final String placeHolderString = (String) attributeRepositoryItems
                                .getPropertyValue(BBBCatalogConstants.PLACE_HOLDER_ATTRIBUTE_PROPERTY_NAME);

                if (!StringUtils.isEmpty(placeHolderString)) {
                    final String[] placeHolderList = placeHolderString.split(",");
                    for (final String element : placeHolderList) {
                        final String placeHolder = element.trim();

                        if (prodAttributesMap.containsKey(placeHolder)) {
                            this.logTrace(placeHolder + " key already in map adding value new attribute "
                                            + attributeRepositoryItems.getRepositoryId());
                            prodAttributesMap.get(placeHolder).add(new AttributeVO(attributeRepositoryItems));
                        } else {
                            this.logTrace("new key added to map [" + placeHolder + " ] adding  new attribute "
                                            + attributeRepositoryItems.getRepositoryId());
                            final List<AttributeVO> newAttributeList = new ArrayList<AttributeVO>();
                            newAttributeList.add(new AttributeVO(attributeRepositoryItems));
                            prodAttributesMap.put(placeHolder, newAttributeList);
                        }
                    }
                } else {
                    this.logTrace("Placeholder String is Null or Empty ");
                }
            }
        }
        return prodAttributesMap;
    }

    /**
     *  The method takes an attributeList and adds an repositoryItem of type attribute if that repository item is not
     * already present in the List if current date is within start and end date property in product relation item
     * descriptor add the attribute repository item to attributeList if not already present if start and end date is
     * null in product-relation item descriptor check if current date is within start and end date property in attribute
     * item descriptor and add the attribute repository item to attributeList if not already present for staging the
     * business user will have the option to change the current date to preview date so attributes will be checked
     * against preview date and not current date.
     *
     * @param productRepositoryItem the product repository item
     * @param siteId the site id
     * @param attributeMap the attribute map
     * @return attributeMap
     */

    private Map<String, RepositoryItem> getProductAttributeList(final RepositoryItem productRepositoryItem,
                    final String siteId, final Map<String, RepositoryItem> attributeMap) {

            final StringBuilder debug = new StringBuilder(40);
            String regionPromoAttr=null;
            boolean sameDayDeliveryFlag = false;
            boolean currentZipEligibility = false;
            debug.append("Catalog API Method Name [getProductAttributeList] siteId[").append(siteId)
                            .append("] productRepositoryItem ").append(productRepositoryItem)
                            .append("] attributeList ").append(attributeMap);
            this.logDebug(debug.toString());

        // Set sku attributes
        // Edited as part of Instant preview story
        Date previewDate = new Date();
        if (this.isPreviewEnabled()) {
            previewDate = this.getPreviewDate();
        }
        @SuppressWarnings ("unchecked")
        final Set<RepositoryItem> productAttributeRelationSet = (Set<RepositoryItem>) productRepositoryItem
                        .getPropertyValue(BBBCatalogConstants.PRODUCT_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME);
        
        BBBSessionBean sessionBean = null;
        
        if (productAttributeRelationSet != null) {
        	boolean internationalShippingContext =false;
        	
          		sessionBean = BBBProfileManager.resolveSessionBean(ServletUtil.getCurrentRequest());
   	            if(sessionBean!=null){
   	            	internationalShippingContext = sessionBean.isInternationalShippingContext();
   	             
   	            }
          	
            this.logTrace("productAttributeRelationSet is not null " + productAttributeRelationSet);
			List<String> sddAttributeKeyList = null;
			try {
				sddAttributeKeyList = this.getAllValuesForKey(BBBCmsConstants.SDD_KEY,
						BBBCmsConstants.SDD_ATTRIBUTE_LIST);
			} catch (final BBBBusinessException e) {
				this.logError("Business Exeption caught for SameDayDeliveryKeys config key", e);
			} catch (final BBBSystemException e) {
				this.logError("System Exeption caught for SameDayDeliveryKeys config key", e);
			}
			String[] sddAttributes = null;
			if (!BBBUtility.isListEmpty(sddAttributeKeyList)) {
				sddAttributes = sddAttributeKeyList.get(0).split(BBBCoreConstants.COMMA);
			}
            for (final RepositoryItem prodAttributeRelationItem : productAttributeRelationSet) {
                    boolean isAttributeAvaialbelForSite=false;
                    if(null!=getBBBSiteToAttributeSiteMap().get(siteId)) {
                    	isAttributeAvaialbelForSite=(Boolean)prodAttributeRelationItem.getPropertyValue(getBBBSiteToAttributeSiteMap().get(siteId));
                    }
                    
                    if (isAttributeAvaialbelForSite) {
                        final Date startDate = (Date) prodAttributeRelationItem
                                        .getPropertyValue(BBBCatalogConstants.START_DATE_PROD_RELATION_PROPERTY_NAME);
                        final Date endDate = (Date) prodAttributeRelationItem
                                        .getPropertyValue(BBBCatalogConstants.END_DATE_PROD_RELATION_PROPERTY_NAME);
                        final RepositoryItem prodAttributeRepositoryItem = (RepositoryItem) prodAttributeRelationItem
                                        .getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME);
                        if (prodAttributeRepositoryItem != null) // Perform following task only if this item is not null
                                                                 // otherwise null pointer exception will come
                        {
                            // if start date and end date is null in skurelation itemdescriptor check for start and end
                            // date in attribute itemdescriptor
                            this.logTrace("start date of product relation[" + startDate + "]");
                            this.logTrace("end date of product relation[" + endDate + "]");
                            String sddEligibleOn = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG);
                            
                    		if(null != sddEligibleOn){
                    			sameDayDeliveryFlag = Boolean.valueOf(sddEligibleOn);
                    		}
                    		if(sameDayDeliveryFlag) {
                              	if(null!=sessionBean && null!=sessionBean.getCurrentZipcodeVO()) {
                              		regionPromoAttr =sessionBean.getCurrentZipcodeVO().getPromoAttId();
                        			currentZipEligibility = sessionBean.getCurrentZipcodeVO().isSddEligibility();
                              	}
                    		}
                      		
                          	
						if ((!checkAttributeIntlApplicability(prodAttributeRepositoryItem, internationalShippingContext)
								&& sameDayDeliverEligibility(prodAttributeRepositoryItem, currentZipEligibility, regionPromoAttr, sddAttributes))
								&& (BBBUtility.isAttributeApplicable(previewDate, startDate, endDate))) {
                            	//BBBSL-9655 | Adding Product level and Promo attribute check to allow null start date when end date is expired
                            		logDebug("Attribute Applicable for Product");
                                final Date startDateOfProduct = (Date) prodAttributeRepositoryItem
                                                .getPropertyValue(BBBCatalogConstants.START_DATE_ATTRIBUTE_PROPERTY_NAME);
                                final Date endDateOfProduct = (Date) prodAttributeRepositoryItem
                                                .getPropertyValue(BBBCatalogConstants.END_DATE_ATTRIBUTE_PROPERTY_NAME);

 			                            this.logDebug("start date of attribute[" + startDateOfProduct + "]");
 			                            this.logTrace("end date of attribute[" + endDateOfProduct + "]");
 			                         if(BBBUtility.isAttributeApplicable(previewDate, startDateOfProduct, endDateOfProduct))   {
                                    if (!attributeMap.containsKey(prodAttributeRepositoryItem.getRepositoryId())) {
                                             this.logDebug("adding attribute as it is not in list "
                                                        + prodAttributeRepositoryItem
                                                                        .getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME));
                                        attributeMap.put(prodAttributeRepositoryItem.getRepositoryId(),
                                                        prodAttributeRepositoryItem);
                                    } else {
                                             this.logDebug("attribute already in  list  "
                                                        + prodAttributeRepositoryItem
                                                                        .getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME));
                                    }

                                    }
                            }
                                }
                            }
                        }
                    }
        return attributeMap;
    }

    /**
     *  This method is used to set all details for a collectionVo.
     *
     * @param productRepositoryItem the product repository item
     * @param subProductVOList the sub product vo list
     * @param siteId the site id
     * @return the collection product vo
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    protected CollectionProductVO getCollectionProductVO(final RepositoryItem productRepositoryItem,
                    final List<ProductVO> subProductVOList, final String siteId)
                    throws BBBBusinessException, BBBSystemException {
            final StringBuilder debug = new StringBuilder(40);
            debug.append("Catalog API Method Name [getCollectionProductVO] siteId[").append(siteId)
                            .append("] productRepositoryItem ").append(productRepositoryItem)
                            .append("] subProductVOList ").append(subProductVOList);
            this.logDebug(debug.toString());
        
        BazaarVoiceProductVO bvReviews = null;

        CollectionProductVO productVO = new CollectionProductVO();
        productVO.setCollection(true);
        String isIntlRestricted = BBBCoreConstants.NO_CHAR;
        if ( !BBBUtility.isEmpty((String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED))) {
     	   isIntlRestricted = (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED);
        }
        productVO.setIntlRestricted(BBBCoreConstants.YES_CHAR.equalsIgnoreCase(isIntlRestricted));
        @SuppressWarnings ("unchecked")
        final List<RepositoryItem> skuRepositoryItems = (List<RepositoryItem>) productRepositoryItem
                        .getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);
        if (skuRepositoryItems != null) {
            final List<String> childSkuIdList = new ArrayList<String>();
            Map<String, RepositoryItem> attributeMap = new HashMap<String, RepositoryItem>();
            /* Get list of all distinct attribute Repository Items from the Sku Repository Items of the product */
            for (int i = 0; i < skuRepositoryItems.size(); i++) {
                childSkuIdList.add(skuRepositoryItems.get(i).getRepositoryId());
            }
            attributeMap = this.getProductAttributeList(productRepositoryItem, siteId, attributeMap);
            // Code added for ZooM-CR
            boolean zoomFlag = true;
            if ((attributeMap != null) && !attributeMap.isEmpty()) {
            	
                try {
                    final List<String> zoomAttributeKeyList = this.getAllValuesForKey(
                                    BBBCmsConstants.CONTENT_CATALOG_KEYS, this.getZoomKeys());
                    if ((zoomAttributeKeyList != null) && !zoomAttributeKeyList.isEmpty()) {
                        final String zoomAttributeKeys[] = zoomAttributeKeyList.get(0).split(BBBCoreConstants.COMMA);
                        for (final String zoomKey : zoomAttributeKeys) {
                            if (attributeMap.containsKey(zoomKey)) {
                                zoomFlag = false;
                                break;
                            }
                        }
                    }
                } catch (final BBBBusinessException e) {
                    this.logError("Business Exeption caught for zoom config key", e);
                } catch (final BBBSystemException e) {
                    this.logError("System Exeption caught for zoom config key", e);
                }
            }
            this.logTrace("Is Product has zoom available : " + zoomFlag);
            productVO.setZoomAvailable(zoomFlag);
			
            /* Removing SDD attribute in case of SHOW_SDD_ATTRIBUTE is false */
            vlogDebug("BBBCatalogToolsImpl.getCollectionProductVO: Before removing SDD attribute - {0}", attributeMap);
			final List<String> sddAttributeKeyList = this.getAllValuesForKey(BBBCmsConstants.SDD_KEY,
					BBBCmsConstants.SDD_ATTRIBUTE_LIST);
			if ((sddAttributeKeyList != null) && !sddAttributeKeyList.isEmpty()) {
				final String sddAtrribsKeys[] = sddAttributeKeyList.get(0).split(BBBCoreConstants.COMMA);
				final String isSDDON = getAllValuesForKey(BBBCmsConstants.SDD_KEY, BBBCmsConstants.SDD_SHOW_ATTRI).get(
						0);
				for (String itemSddKey : sddAtrribsKeys) {
					itemSddKey = itemSddKey.trim();
					if (attributeMap.containsKey(itemSddKey)) {
						if (isSDDON.equalsIgnoreCase(BBBCoreConstants.FALSE)) {
							attributeMap.remove(itemSddKey);
						}
					}
				}
			}
			vlogDebug("BBBCatalogToolsImpl.getCollectionProductVO: After removing SDD attribute - {0}", attributeMap);
            // sku attribute map in ProductVO
            final Map<String, List<AttributeVO>> skuAttributesMap = this.getProdAttributeMap(attributeMap);

            if ((skuAttributesMap != null) && !skuAttributesMap.isEmpty()) {
                productVO.setAttributesList(skuAttributesMap);
            }
            if (!childSkuIdList.isEmpty()) {
                productVO.setChildSKUs(childSkuIdList);
            }

            final RepositoryItem rollUpTypesRepositoryItem = (RepositoryItem) productRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.PRODUCT_ROLL_UP_PRODUCT_PROPERTY_NAME);
            if ((rollUpTypesRepositoryItem != null)
                            && (rollUpTypesRepositoryItem
                                            .getPropertyValue(BBBCatalogConstants.ROLLUP_ATTRIBUTE_ROLLUP_PROPERTY_NAME) != null)) {
                this.logTrace("Set Roll Up Type for Collection " + productRepositoryItem.getRepositoryId());
                final String rollUpAttribute = (String) rollUpTypesRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.ROLLUP_ATTRIBUTE_ROLLUP_PROPERTY_NAME);
                int lengthOfRollUpAttr= 0;
				int lengthOfSizeSwatch = (Integer.parseInt(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.LENGTH_OF_SWATCH)));
				
					 Map<String, List<RollupTypeVO>> rollup=  this.getRollUpAttributeForProduct(rollUpAttribute,skuRepositoryItems,true);
					 if(rollup.get("SIZE")!= null ){
						 for(int i=0;i<rollup.get("SIZE").size();i++)
						 {
							String rollUpAttributeValue = StringEscapeUtils.unescapeHtml(rollup.get("SIZE").get(i).getRollupAttribute());
							lengthOfRollUpAttr = lengthOfRollUpAttr + rollUpAttributeValue.length()+ lengthOfSwatch;
							  
						 }
						 if(lengthOfRollUpAttr > lengthOfSizeSwatch){
                            	productVO.setDisplaySizeAsSwatch(false);
            				}
                            else{
                            	productVO.setDisplaySizeAsSwatch(true);
                            }
                        }
                
               // Flag Added to check Product/SKU Active item for SiteMap Changes
				productVO.setRollupAttributes(rollup);
                productVO.setCollectionRollUp(this.getRollupForCollection(rollUpAttribute));
            }
        }
        String omnitureCollectionEvar29= EMPTYSTRING;
        if ((subProductVOList != null) && !subProductVOList.isEmpty()) {
            productVO.setChildProducts(subProductVOList);
            for(ProductVO collectionSubProduct:subProductVOList){
            	omnitureCollectionEvar29+=";"+collectionSubProduct.getProductId()+";;;;eVar29="+productRepositoryItem.getRepositoryId()+",";
            } 
        }
        productVO.setOmnitureCollectionEvar29(omnitureCollectionEvar29);
		// Fetch ProductVO with Product Tabs populated and set this into Collection Tab list & Harmon Long Description.
		final ProductVO pVO = this.updateProductTabs(productRepositoryItem, siteId, productVO);
		productVO.setHarmonLongDescription(pVO.getHarmonLongDescription());
		productVO.setProductTabs(pVO.getProductTabs());
		
		productVO.setProductId(productRepositoryItem.getRepositoryId());

        if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.SHOW_IMAGES_IN_COLLECTION_PRODUCT_PROPERTY_NAME) != null) {
            productVO.setShowImagesInCollection(((Boolean) productRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.SHOW_IMAGES_IN_COLLECTION_PRODUCT_PROPERTY_NAME))
                            .booleanValue());
        }else{
        	productVO.setShowImagesInCollection(true);
        }
        if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) != null) {
            productVO.setLeadSKU((Boolean) productRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME));
        }
        
        if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.COLLECTION_THUMBNAIL_PRODUCT_PROPERTY_NAME) != null) {
            final String collectionThumbNailPath = (String) productRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.COLLECTION_THUMBNAIL_PRODUCT_PROPERTY_NAME);
            productVO.setCollectionThumbnail(collectionThumbNailPath);
        }
        productVO = (CollectionProductVO) updateProductBasicDetails(productVO, productRepositoryItem, false, siteId);

        // Update Price Range Description
        productVO.setPriceRangeDescription((String) productRepositoryItem
                        .getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME));
        productVO.setPriceRangeDescriptionRepository((String) productRepositoryItem
                .getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME));
        
        productVO.setDefaultPriceRangeDescription((String) productRepositoryItem
                .getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME));
        this.updatePriceDescription(productVO);
        // Update Bazaar Voice Ratings
        bvReviews = this.getBazaarVoiceDetails(productRepositoryItem.getRepositoryId(), siteId);
        productVO.setBvReviews(bvReviews);
        this.logTrace(productVO.toString());
        productVO.setChildProducts(subProductVOList);
        String shipMsgDisplayFlag = BBBCoreConstants.FALSE;
		try {
			shipMsgDisplayFlag = getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG).get(0);
		} catch (BBBSystemException | BBBBusinessException e) {
			logError("Error while getting config key ShipMsgDisplayFlag value", e);
		}
		if(Boolean.parseBoolean(shipMsgDisplayFlag)){
			 updateShippingMessageFlag(productVO);
		}
        return productVO;

    }

 // Start : R 2.2 Product Image SiteMap Generation 504-b 
 	/**
  * This provide prodcuts min details for all Active/Disables products also.
  *
  * @param productVO the product vo
  * @param productRepositoryItem the product repository item
  * @param isMinimalDetails the is minimal details
  * @return the product vo
  */
 	private ProductVO updateProductBasicDetails(ProductVO productVO,RepositoryItem productRepositoryItem, boolean isMinimalDetails)
 	{
 		if(productRepositoryItem!=null){
 			productVO.setProductId(productRepositoryItem.getRepositoryId());
 			if( productRepositoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME)!=null)
 			{
 				productVO.setName((String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME));
 			}

 			if( productRepositoryItem.getPropertyValue(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME)!=null)
 			{
 				productVO.setShortDescription( (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME));
 			}

 			if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME)!=null)
 			{
 				productVO.setLongDescription( (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME));
 			}
 			//set image details for product
 			final ImageVO productImage=new ImageVO();
 			if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.LARGE_IMAGE_IMAGE_PROPERTY_NAME)!=null){
 				final String largeImagePath= (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.LARGE_IMAGE_IMAGE_PROPERTY_NAME);
 				productImage.setLargeImage(largeImagePath);
 			}
 			if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME)!=null){
 				final String mediumImagePath= (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME);
 				productImage.setMediumImage(mediumImagePath);
 			}
 			if(!isMinimalDetails){
 				if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_IMAGE_PROPERTY_NAME)!=null){
 					final String smallImagePath= (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_IMAGE_PROPERTY_NAME);
 					productImage.setSmallImage(smallImagePath);
 				}
 				if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME)!=null){
 					final String swatchImagePath= (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME);
 					productImage.setSwatchImage(swatchImagePath);
 				}

 				if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME)!=null){
 					final String thumbnailImagePath= (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME);
 					productImage.setThumbnailImage(thumbnailImagePath);
 				}
 				if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.ZOOM_INDEX_PATH_PRODUCT_PROPERTY_NAME)!=null)
 				{
 					productImage.setZoomImageIndex((Integer) productRepositoryItem.getPropertyValue(BBBCatalogConstants.ZOOM_INDEX_PATH_PRODUCT_PROPERTY_NAME));
 				}
 				if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.ZOOM_IMAGE_IMAGE_PROPERTY_NAME)!=null)
 				{
 					final String zoomImagePath= (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.ZOOM_IMAGE_IMAGE_PROPERTY_NAME);
 					productImage.setZoomImage(zoomImagePath);
 				}
 				if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.ANYWHERE_ZOOM_PRODUCT_PROPERTY_NAME)!=null)
 				{
 					productImage.setAnywhereZoomAvailable((Boolean) productRepositoryItem.getPropertyValue(BBBCatalogConstants.ANYWHERE_ZOOM_PRODUCT_PROPERTY_NAME));
 				}
 			}
 			productVO.setProductImages( productImage);
 			//set brand details for product
 			if(!isMinimalDetails){
 				BrandVO brandVO=null;
 				final RepositoryItem brandRepositoryItem=(RepositoryItem)productRepositoryItem.getPropertyValue(BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR);
 				if(brandRepositoryItem!=null){
 					brandVO=new BrandVO();
 					brandVO.setBrandId(brandRepositoryItem.getRepositoryId());
 					if(brandRepositoryItem.getPropertyValue(BBBCatalogConstants.BRAND_NAME_BRAND_PROPERTY_NAME)!=null)
 					{
 						brandVO.setBrandName((String) brandRepositoryItem.getPropertyValue(BBBCatalogConstants.BRAND_NAME_BRAND_PROPERTY_NAME));
 					}
 					if(brandRepositoryItem.getPropertyValue(BBBCatalogConstants.BRAND_IMAGE_BRAND_PROPERTY_NAME)!=null)
 					{
 						brandVO.setBrandImage((String) brandRepositoryItem.getPropertyValue(BBBCatalogConstants.BRAND_IMAGE_BRAND_PROPERTY_NAME));
 					}
 					if( brandRepositoryItem.getPropertyValue(BBBCatalogConstants.BRAND_DESCRIPTION_BRAND_PROPERTY_NAME)!=null)
 					{
 						brandVO.setBrandImageAltText((String) brandRepositoryItem.getPropertyValue(BBBCatalogConstants.BRAND_DESCRIPTION_BRAND_PROPERTY_NAME));
 					}
 				}
 				productVO.setProductBrand( brandVO);
 			}
 			if( productRepositoryItem.getPropertyValue(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME)!=null){
 				productVO.setSkuLowPrice( (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME));
 			}
 			if( productRepositoryItem.getPropertyValue(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME)!=null){
 				productVO.setSkuHighPrice( (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME));
 			}

 			if( productRepositoryItem.getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME)!=null){
 				productVO.setSeoUrl((String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME));
 			}

 		}
 		return  productVO;
 	}
 	// Start : R 2.2 Product Image SiteMap Generation 504-b
    /**
	  * Update product basic details.
	  *
	  * @param productVO the product vo
	  * @param productRepositoryItem the product repository item
	  * @param isMinimalDetails the is minimal details
	  * @param siteId the site id
	  * @return the product vo
	  */
	 private static ProductVO updateProductBasicDetails(final ProductVO productVO,
                    final RepositoryItem productRepositoryItem, final boolean isMinimalDetails, String siteId) {
		 String currentSiteId = siteId;
        if (productRepositoryItem != null) {
            productVO.setProductId(productRepositoryItem.getRepositoryId());
            //LTL check product
            boolean isLTLProduct = false;
            if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) != null) {
            	isLTLProduct = ((Boolean) productRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.IS_LTL_SKU))
                                .booleanValue();
            }
            productVO.setLtlProduct(Boolean.valueOf(isLTLProduct));
            //LTL check end
            if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME) != null) {
                productVO.setName((String) productRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME));
            }

            if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME) != null) {
                productVO.setShortDescription((String) productRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME));
            }

            if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME) != null) {
                productVO.setLongDescription((String) productRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME));
            }
            
            if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.PRODUCT_KEYWORDS) != null) {
                productVO.setPrdKeywords((String) productRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.PRODUCT_KEYWORDS));
            }
            // set image details for product
            final ImageVO productImage = new ImageVO();
            if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.LARGE_IMAGE_IMAGE_PROPERTY_NAME) != null) {
                final String largeImagePath = (String) productRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.LARGE_IMAGE_IMAGE_PROPERTY_NAME);
                productImage.setLargeImage(largeImagePath);
            }
            if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME) != null) {
                final String mediumImagePath = (String) productRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME);
                productImage.setMediumImage(mediumImagePath);
            }
            if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_IMAGE_PROPERTY_NAME) != null) {
                final String smallImagePath = (String) productRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_IMAGE_PROPERTY_NAME);
                productImage.setSmallImage(smallImagePath);
            }
            if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.REGULAR_IMAGE_IMAGE_PROPERTY_NAME)!=null){
 				final String regularImagePath= (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.REGULAR_IMAGE_IMAGE_PROPERTY_NAME);
 				productImage.setRegularImage(regularImagePath);
 			}
            if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.COLLECTION_THUMBNAIL_PRODUCT_PROPERTY_NAME)!=null){
 				final String collectionThumbnailImagePath= (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.COLLECTION_THUMBNAIL_PRODUCT_PROPERTY_NAME);
 				productImage.setCollectionThumbnailImage(collectionThumbnailImagePath);
 			}
            if (!isMinimalDetails) {
               
                if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME) != null) {
                    final String swatchImagePath = (String) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME);
                    productImage.setSwatchImage(swatchImagePath);
                }
                if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME) != null) {
                    final String thumbnailImagePath = (String) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME);
                    productImage.setThumbnailImage(thumbnailImagePath);
                }
                if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.ZOOM_INDEX_PATH_PRODUCT_PROPERTY_NAME) != null) {
                    productImage.setZoomImageIndex(((Integer) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.ZOOM_INDEX_PATH_PRODUCT_PROPERTY_NAME))
                                    .intValue());
                }
                if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.ZOOM_IMAGE_IMAGE_PROPERTY_NAME) != null) {
                    final String zoomImagePath = (String) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.ZOOM_IMAGE_IMAGE_PROPERTY_NAME);
                    productImage.setZoomImage(zoomImagePath);
                }
                if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.ANYWHERE_ZOOM_PRODUCT_PROPERTY_NAME) != null) {
                    productImage.setAnywhereZoomAvailable(((Boolean) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.ANYWHERE_ZOOM_PRODUCT_PROPERTY_NAME))
                                    .booleanValue());
                }
            }
            productVO.setProductImages(productImage);
            // set brand details for product
            if (!isMinimalDetails) {
                BrandVO brandVO = null;
                final RepositoryItem brandRepositoryItem = (RepositoryItem) productRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR);
                if(brandRepositoryItem!=null){
					Set<RepositoryItem> bbbSites=(Set<RepositoryItem>) brandRepositoryItem.getPropertyValue(BBBCatalogConstants.SITES_BRAND_PROPERTY_NAME);
					if(bbbSites!=null ){
	            		for(RepositoryItem siteRepoItem:bbbSites)
	            		{
	            			//this check if for showing shop all by brand name on TBS sites,
	            			//instead of modifying the entire repository data
	            			if(currentSiteId.equals(TBSConstants.SITE_TBS_BAB_US)){
	            				currentSiteId = BBBCoreConstants.SITE_BAB_US;
	            			}else if(currentSiteId.equals(TBSConstants.SITE_TBS_BAB_CA)){
	            				currentSiteId = BBBCoreConstants.SITE_BAB_CA;
	            			}else if(currentSiteId.equals(TBSConstants.SITE_TBS_BBB)){
	            				currentSiteId = BBBCoreConstants.SITE_BBB;
	            			}
	            			if(siteRepoItem.getRepositoryId().equalsIgnoreCase(currentSiteId)){
								brandVO=new BrandVO();
								brandVO.setBrandId(brandRepositoryItem.getRepositoryId());
								if(brandRepositoryItem.getPropertyValue(BBBCatalogConstants.BRAND_NAME_BRAND_PROPERTY_NAME)!=null)
								{
									brandVO.setBrandName((String) brandRepositoryItem.getPropertyValue(BBBCatalogConstants.BRAND_NAME_BRAND_PROPERTY_NAME));
								}
								if(brandRepositoryItem.getPropertyValue(BBBCatalogConstants.BRAND_IMAGE_BRAND_PROPERTY_NAME)!=null)
								{
									brandVO.setBrandImage((String) brandRepositoryItem.getPropertyValue(BBBCatalogConstants.BRAND_IMAGE_BRAND_PROPERTY_NAME));
								}
								if( brandRepositoryItem.getPropertyValue(BBBCatalogConstants.BRAND_DESCRIPTION_BRAND_PROPERTY_NAME)!=null)
								{
									brandVO.setBrandImageAltText((String) brandRepositoryItem.getPropertyValue(BBBCatalogConstants.BRAND_DESCRIPTION_BRAND_PROPERTY_NAME));
								}
	            			}
	            		}
					}
				}
                productVO.setProductBrand(brandVO);
            }
            if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME) != null) {
                productVO.setSkuLowPrice((String) productRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME));
            }
            if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME) != null) {
                productVO.setSkuHighPrice((String) productRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME));
            }

            if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME) != null) {
                productVO.setSeoUrl((String) productRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME));
            }

        }
        return productVO;
    }

    /**
     *  This method is used to validate if CATEGORY is active in the catalog or not For the CATEGORY to be active disable
     * should be false Also if start and end date are not null then current date should be after start date.
     *
     * @param categoryRepositoryItem the category repository item
     * @return Category State
     */
    @Override
    public boolean isCategoryActive(final RepositoryItem categoryRepositoryItem) {
        // Edited as part of Instant preview story
        Date previewDate = new Date();
        if (this.isPreviewEnabled()) {
            previewDate = this.getPreviewDate();
        }
        final Date startDate = (Date) categoryRepositoryItem
                        .getPropertyValue(BBBCatalogConstants.START_DATE_CATEGORY_PROPERTY_NAME);
        final Date endDate = (Date) categoryRepositoryItem
                        .getPropertyValue(BBBCatalogConstants.END_DATE_CATEGORY_PROPERTY_NAME);
        this.logDebug(categoryRepositoryItem.getRepositoryId() + " category:id details category startDate[" + startDate
                + "]category endDate[" + endDate + "]");
        if(((endDate != null) && previewDate.after(endDate)) || ((startDate != null) && previewDate.before(startDate))){
        	return false;
        }
        boolean disable = true;
        if (categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) != null) {
            disable = ((Boolean) categoryRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME)).booleanValue();
        }
        this.logDebug(categoryRepositoryItem.getRepositoryId() + "category disable[" + disable + "]");
        if (disable) {
            return false;
        }
        return true;
    }

    /**
     *  The method checks if the category id is a top level category To check this all the parent category ids of the
     * category is added in a list Then we check if the commerce root id of the current side is one of the parent of the
     * category or not if the commerce root is in the list of category parent then category is a top level category else
     * it is not.
     *
     * @param categoryId the category id
     * @param siteId the site id
     * @return First Level Category State
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public Boolean isFirstLevelCategory(final String categoryId, final String siteId)
                    throws BBBBusinessException, BBBSystemException {
        this.logDebug("Catalog API Method Name [isFirstLevelCategory] category Id [" + categoryId + "] site Id ["
                        + siteId + "]");
        boolean parentFlag = false;
        if (!StringUtils.isEmpty(siteId) && !StringUtils.isEmpty(categoryId)) {
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " isFirstLevelCategory");
                final RepositoryItem categoryRepositoryItem = this.getCatalogRepository().getItem(categoryId,
                                BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
                final List<String> parent = new ArrayList<String>();
                if (null != categoryRepositoryItem) {
                    @SuppressWarnings ("unchecked")
                    final Set<RepositoryItem> parentCategorySet = (Set<RepositoryItem>) categoryRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.FIXED_PARENT_CATEGORIES_PROPERTY_NAME);
                    if ((parentCategorySet != null) && !parentCategorySet.isEmpty()) {
                        for (final RepositoryItem parentCatItem : parentCategorySet) {
                            parent.add(parentCatItem.getRepositoryId());
                        }
                    }
                    // the config key is the siteid+RootCategory e.g. for BuyBuyBaby site the
                    // config key is BuyBuyBabyRootCategory
                    final String configKey = siteId.trim() + "RootCategory";
                    final String commerceRootCatId = this.getAllValuesForKey(this.getConfigType(), configKey).get(0);
                    if (!parent.isEmpty() && parent.contains(commerceRootCatId)) {
                        this.logDebug("Commerce root id " + commerceRootCatId
                                        + " is one of the parents ofthe category " + categoryId
                                        + " thus it is a top level category");
                        parentFlag = true;
                    }
                } else {
                    throw new BBBSystemException(BBBCatalogErrorCodes.CATEGORY_NOT_AVAILABLE_IN_REPOSITORY,
                                    BBBCatalogErrorCodes.CATEGORY_NOT_AVAILABLE_IN_REPOSITORY);
                }
            } catch (final RepositoryException e) {
                this.logError("Catalog API Method Name [isFirstLevelCategory]: RepositoryException ");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
            } finally {
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " isFirstLevelCategory");
            }
            return Boolean.valueOf(parentFlag);
        }
        this.logDebug("input parameter site id is null");
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }


    /**
     *  The method gets the details for EcoFee.
     *
     * @param pStateId the state id
     * @param pSkuId the sku id
     * @return Eco Free SKU Details
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final EcoFeeSKUVO getEcoFeeSKUDetailForState(final String pStateId, final String pSkuId)
                    throws BBBBusinessException, BBBSystemException {
    	
    	return getGlobalRepositoryTools().getEcoFeeSKUDetailForState(pStateId, pSkuId);
    }

    /**
     *  The method checks if the store is eligible for EcoFee.
     *
     * @param pStoreId the store id
     * @return Eco Free Eligibility
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final boolean isEcoFeeEligibleForStore(final String pStoreId)
                    throws BBBBusinessException, BBBSystemException {
    	
    	return getStoreRepositoryTools().isEcoFeeEligibleForStore(pStoreId);
    }

    /**
     *  The method gets the details for eco fee sku for store.
     *
     * @param pStoreId the store id
     * @param pSkuId the sku id
     * @return EcoFeeSKUVO
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final EcoFeeSKUVO getEcoFeeSKUDetailForStore(final String pStoreId, final String pSkuId)
                    throws BBBBusinessException, BBBSystemException {
    	
    	return getStoreRepositoryTools().getEcoFeeSKUDetailForStore(pStoreId, pSkuId);
    }

    /**
     *  the method returns the product id of the first parent of the sku that is active.
     *
     * @param pSkuId the sku id
     * @return First Active Parent
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
   @Override
   public final String getFirstActiveParentProductForSKU(final String pSkuId)
           throws BBBBusinessException, BBBSystemException {
             final String methodName = "getFirstActiveParentProductForSKU(String pSkuId)";
             boolean firstParentProductFound = false;
             try {
                 if ((null != pSkuId) && !StringUtils.isEmpty(pSkuId)) {
                         this.logDebug("Entering  " + methodName + " with SkuId: " + pSkuId);
                     final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(pSkuId,
                                     BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
                     if (null != skuRepositoryItem) {
                             @SuppressWarnings ("unchecked")
                             final Set<RepositoryItem> parentProductList = (Set<RepositoryItem>) skuRepositoryItem
                                             .getPropertyValue(BBBCatalogConstants.PARENT_PRODUCT_PROPERTY_NAME);
                             if ((parentProductList != null) && !parentProductList.isEmpty()) {
                                 for (final RepositoryItem parentProductItem : parentProductList) {
                                     if ((null != (parentProductItem.getRepositoryId()))) {
                                        firstParentProductFound = true;
                                         return parentProductItem.getRepositoryId();
                                     }
                                 }
                                 if (!firstParentProductFound) {
                                         this.logDebug("SKU Doesnt belong to any ACTIVE Product, sku id:" + pSkuId);
                                     throw new BBBBusinessException(BBBCatalogErrorCodes.NO_ACTIVE_PRODUCT_FOR_SKU,
                                                     BBBCatalogErrorCodes.NO_ACTIVE_PRODUCT_FOR_SKU);
                                 }
                             } else {
                                     this.logDebug("SKU Doesnt belong to any Product, sku id:" + pSkuId);
                                 throw new BBBBusinessException(BBBCatalogErrorCodes.NO_PRODUCT_FOR_SKU,
                                                 BBBCatalogErrorCodes.NO_PRODUCT_FOR_SKU);
                             }
                     } else {
                         this.logDebug("Repository Item is null for sku : " + pSkuId);
                         throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
                                         BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
                     }
                 } else {
                     this.logDebug("input parameter SKUId id is null");
                     throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                                     BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
                 }
             } catch (final RepositoryException e) {
                 this.logError("Catalog API Method Name [getFirstActiveParentProductForSKU]: RepositoryException "+e.getMessage(),e);
                 throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                 BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
             }
             
             return null;
      }


    /**
     *  The method gets the registry name corresponding to the registry code.
     *
     * @param registryCode the registry code
     * @param siteId the site id
     * @return Name of registry
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public final String getRegistryTypeName(final String registryCode, final String siteId)
                    throws BBBSystemException, BBBBusinessException {

    	return getSiteRepositoryTools().getRegistryTypeName(registryCode, siteId);
    
    }
    
    /**
     * This method will check if the product is available for everLiving pdp.
     *
     * @param pProductId the product id
     * @param siteId the site id
     * @return true, if is everliving product
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    public boolean isEverlivingProduct(final String pProductId, String pSiteId)throws BBBBusinessException, BBBSystemException{
    	String siteId = pSiteId;
    	if( siteId.equals("TBS_BedBathUS") ) {
    		siteId = "BedBathUS";
		}
		else if( siteId.equals("TBS_BuyBuyBaby") ) {
			siteId = "BuyBuyBaby";			
		}
		else if( siteId.equals("TBS_BedBathCanada") ) {
			siteId = "BedBathCanada";			
		}
    	boolean isEverLivingProduct =false;
    	boolean killFlag =false;
    	Date enableDate;
    	Date date = new Date();
    	boolean validDate=false;
            final StringBuilder debug = new StringBuilder(50);
            debug.append("Catalog API Method Name [getProductDetails] siteId[").append(siteId)
                            .append("] pProductId [").append(pProductId);
            this.logDebug(debug.toString());

        final long startTime = System.currentTimeMillis();
        if (!StringUtils.isEmpty(siteId)) {
              BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " isEverlivingProduct");
              try {
                final RepositoryItem productRepositoryItem = this.getCatalogRepository().getItem(pProductId,
                                BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
                if (productRepositoryItem == null) {
                    throw new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
                                    BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);
                }
       if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.ENABLE_DATE_PRODUCT_PROPERTY_NAME) != null) {
          enableDate = (Date) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.ENABLE_DATE_PRODUCT_PROPERTY_NAME);
          if(date.after(enableDate))
        	  validDate=true;
       }       
       
    	boolean webOffered = false;
        if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) != null) {
            webOffered = ((Boolean) productRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME)).booleanValue();
        }
        boolean disable = true;

        if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) != null) {
            disable = ((Boolean) productRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME)).booleanValue();
        }
    	//reading kill flags for all 4 concepts
        if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)) {
        	Object disableForeverPDPFlag = productRepositoryItem.getPropertyValue(BBBCatalogConstants.DISABLE_FOREVER_PDP_FLAG);
        	if(disableForeverPDPFlag == null){
        		killFlag =false;
        	}else if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.DISABLE_FOREVER_PDP_FLAG) !=null){
        		killFlag = (Boolean) productRepositoryItem.getPropertyValue(BBBCatalogConstants.DISABLE_FOREVER_PDP_FLAG);
        	}
        	if(webOffered && disable && !killFlag && validDate)
        	isEverLivingProduct = true;
        }
        if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
        	Object disableForeverPDPFlag = productRepositoryItem.getPropertyValue(BBBCatalogConstants.CA_DISABLE_FOREVER_PDP_FLAG);
        	if(disableForeverPDPFlag == null){
        		killFlag =false;
        	}else if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.CA_DISABLE_FOREVER_PDP_FLAG) !=null){
        		killFlag = (Boolean) productRepositoryItem.getPropertyValue(BBBCatalogConstants.CA_DISABLE_FOREVER_PDP_FLAG);
        	}
        	if(webOffered && disable && !killFlag && validDate)
        	isEverLivingProduct = true;
            }
        if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)) {
        	Object disableForeverPDPFlag = productRepositoryItem.getPropertyValue(BBBCatalogConstants.BAB_DISABLE_FOREVER_PDP_FLAG);
        	if(disableForeverPDPFlag == null){
        		killFlag =false;
        	}else if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.BAB_DISABLE_FOREVER_PDP_FLAG) !=null){
        		killFlag = (Boolean) productRepositoryItem.getPropertyValue(BBBCatalogConstants.BAB_DISABLE_FOREVER_PDP_FLAG);
        	}
        	if(webOffered && disable && !killFlag && validDate)
        	isEverLivingProduct = true;
            }
              } catch (final RepositoryException e) {
                  this.logError("Catalog API Method Name [isEverlivingProduct]: RepositoryException ");
                  throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                  BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
              } finally {
                  final long totalTime = System.currentTimeMillis() - startTime;
                  this.logDebug("Total time taken for BBBCatalogTools.isEverlivingProduct() is: " + totalTime
                                  + " for product id: " + pProductId);
                  BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " isEverlivingProduct");
              }
          }
          
//      //End reading kill flags for all 4 concepts
        return  isEverLivingProduct;
    }
    
    /**
     *  This method is used to validate if product is active in the catalog or not For the product to be active
     * weboffered should be true and disable should be false Also if start and end date are not null then current date
     * should be after start date and before end date.
     *
     * @param productRepositoryItem the product repository item
     * @return Product Active State
     */

    @Override
    public boolean isProductActive(final RepositoryItem productRepositoryItem) {
        // Edited as part of Instant preview story
        Date previewDate = new Date();
        if (this.isPreviewEnabled()) {
            previewDate = this.getPreviewDate();
        }
        DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
        String channelApp = EMPTYSTRING;
        if(null!=request)
    	{
    	  channelApp = request.getHeader(BBBCoreConstants.CHANNEL);
    	}
		
        final Date startDate = (Date) productRepositoryItem
                        .getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME);
        final Date endDate = (Date) productRepositoryItem
                        .getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME);
        boolean webOffered = false;
        if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) != null) {
            webOffered = ((Boolean) productRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME)).booleanValue();
            if (!webOffered && !StringUtils.isBlank(channelApp)
    				&& channelApp
    						.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP)) {
                    	return true;
        }
        }
        this.logTrace(productRepositoryItem.getRepositoryId() + " Product id details ::Product startDate[" + startDate
                + "]Product endDate[" + endDate + "]Product webOffered[" + webOffered + "]");
        if(((endDate != null) && previewDate.after(endDate)) || ((startDate != null) && previewDate.before(startDate))){
        	return false;
        }
        boolean disable = true;

        if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) != null) {
            disable = ((Boolean) productRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME)).booleanValue();
        }
        this.logTrace(productRepositoryItem.getRepositoryId() + "Product disable[" + disable + "]");
        if (disable || !webOffered) {
            return false;
        }
        return true;

    }
	
	/**
	 * Added as Part of R2.2 Story - 517-a1 
	 * Fetching Product Reposiotry item from product id 
	 * Validate if product is active or not irrespective of associated site ids.
	 * For the product to be active  weboffered should be true and disable should be false
	 * Also if start and end date are not null then current date should be after start date
	 * and before end date.
	 *
	 * @param productId the product id
	 * @return boolean - Active Flag
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	@Override
	public boolean isProductActive (String productId) throws BBBSystemException, BBBBusinessException {
		try {
			BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + Thread.currentThread().getStackTrace()[1].getMethodName() 
											+ " Product Id- " + productId);
			logDebug("Start:: " +
					CATALOG_API_METHOD_NAME + " " + Thread.currentThread().getStackTrace()[1].getMethodName());
			final RepositoryItem productRepositoryItem=this.getCatalogRepository().getItem(productId, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
			if(productRepositoryItem==null) {
				throw new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
												BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);
			}
			else{
				return this.isProductActive(productRepositoryItem);
			}
		} catch (RepositoryException e) {
				logError(CATALOG_API_METHOD_NAME + " " + Thread.currentThread().getStackTrace()[1].getMethodName() + ";: RepositoryException ");
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,
											BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,e);
		} finally {
			logDebug("End:: " +
					CATALOG_API_METHOD_NAME + " " + Thread.currentThread().getStackTrace()[1].getMethodName());
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + Thread.currentThread().getStackTrace()[1].getMethodName());
		}
	}
	
	/**
	 *     /** This method is used to validate if product is active in the catalog or not For the product to be active.
	 * weboffered should be true and disable should be false Also if start and end date are not null then current date
	 * should be after start date and before end date.Also the sites to which the sku is associated should have the
	 * current site too
	 *
	 * @param productRepositoryItem the product repository item
	 * @param siteId the site id
	 * @return Product Active State
	 */

    @Override
    public final boolean isProductActive(final RepositoryItem productRepositoryItem, final String siteId) {
        @SuppressWarnings ("unchecked")
        final Set<String> assocSites = (Set<String>) productRepositoryItem.getPropertyValue("siteIds");

        if ((assocSites != null) && !assocSites.isEmpty() && assocSites.contains(siteId)) {
            // Edited as part of Instant preview story
            Date previewDate = new Date();
            if (this.isPreviewEnabled()) {
                previewDate = this.getPreviewDate();
            }
            final Date startDate = (Date) productRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME);
            final Date endDate = (Date) productRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME);
            boolean webOffered = false;
            if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) != null) {
                webOffered = ((Boolean) productRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME))
                                .booleanValue();
            }
            this.logTrace(productRepositoryItem.getRepositoryId() + " Product id details ::Product startDate["
                    + startDate + "]Product endDate[" + endDate + "]Product webOffered[" + webOffered + "]");
            if(((endDate != null) && previewDate.after(endDate)) || ((startDate != null) && previewDate
                    .before(startDate))){
            	return false;
            }
            boolean disable = true;
            if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) != null) {
                disable = ((Boolean) productRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME)).booleanValue();
            }
            this.logTrace(productRepositoryItem.getRepositoryId() + "Product disable[" + disable + "]");
            if (disable || !webOffered) {
                return false;
            }
            return true;
        }
        return false;

    }
	
	/* (non-Javadoc)
	 * @see com.bbb.commerce.catalog.BBBCatalogTools#isProductActive(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isProductActive (String productId,String siteId) throws BBBSystemException, BBBBusinessException {
		try {
			BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL+" isProductActive_1");
			final RepositoryItem productRepositoryItem=this.getCatalogRepository().getItem(productId, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
			if(productRepositoryItem==null) {
				throw new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);
			}
			else{
				return this.isProductActive(productRepositoryItem, siteId);
			}
		} catch (RepositoryException e) {
				logError("Catalog API Method Name [getProductDetails]: RepositoryException ");
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,e);
		} finally {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL+" isProductActive_2");
		}
	}
	
    /**
     *  This method is used to validate if sku is active in the catalog or not For the sku to be active weboffered should
     * be true and disable should be false Also if start and end date are not null then current date should be after
     * start date and before end date. As part of instant preview on staging a new preview date is introduced.This will
     * work only in staging The user will enter the preview date against which the start and end date will be tested. in
     * all other environments the current date will be the preview date
     *
     * @param skuRepositoryItem the sku repository item
     * @param value the value
     * @return SKU State
     */
    @Override
    public boolean isSkuActive(final RepositoryItem skuRepositoryItem,String ... value) {
    	
    	return getGlobalRepositoryTools().isSkuActive(skuRepositoryItem, value);
    }

    /**
     *  This method is used to validate if sku is active in the catalog or not For the sku to be active weboffered should
     * be true and disable should be false Also if start and end date are not null then current date should be after
     * start date and before end date. As part of instant preview on staging a new preview date is introduced.This will
     * work only in staging The user will enter the preview date against which the start and end date will be tested. in
     * all other environments the current date will be the preview date
     *
     * @param skuId the sku id
     * @return SKU Active State
     * @throws RepositoryException the repository exception
     */
    @Override
    public boolean isSkuActive(final String skuId) throws RepositoryException {
        final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId,
                        BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);

        return this.isSkuActive(skuRepositoryItem);
    }

    /**
     *  This method is used to validate if sku is active in the catalog or not for a store sku if the sku is of type
     * store then weboffered flag is not considered to check if the sku is active.For all other sku the following logic
     * is used: For the sku to be active weboffered should be true and disable should be false Also if start and end
     * date are not null then current date should be after start date and before end date.
     *
     * @param skuRepositoryItem the sku repository item
     * @return SKU Active state for Store
     */
    public final boolean isSkuActiveForStore(final RepositoryItem skuRepositoryItem) {
        // Edited as part of Instant preview story
        Date previewDate = new Date();
        if (this.isPreviewEnabled()) {
            previewDate = this.getPreviewDate();
        }
        if (skuRepositoryItem != null) {
            final Date startDate = (Date) skuRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME);
            final Date endDate = (Date) skuRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME);
            boolean disable = true;
            if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) != null) {
                disable = ((Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME))
                                .booleanValue();
            }
            boolean bopusExclusion = true;
            if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.BOPUS_EXCLUSION_SKU_PROPERTY_NAME) != null) {
                bopusExclusion = ((Boolean) skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.BOPUS_EXCLUSION_SKU_PROPERTY_NAME))
                                .booleanValue();
            }
                this.logTrace("SKU startDate[" + startDate + "]");
                this.logTrace("SKU endDate[" + endDate + "]");
                this.logTrace("SKU disable[" + disable + "]");
            // if the sku is eligible for buy online pick up in store then don't consider web offered flag
            if (!bopusExclusion) {
                if ((((endDate != null) && previewDate.after(endDate)) || ((startDate != null) && previewDate
                                .before(startDate))) || disable) {
                    return false;
                }
                return true;
            }
            return this.isSkuActive(skuRepositoryItem);
        }
        return false;
    }

    /**
     *  This method is used to validate if sku is active in the catalog or not For the sku to be active weboffered should
     * be true and disable should be false Also if start and end date are not null then current date should be after
     * start date and before end date.Also the sites to which the sku is associated should have the current site too As
     * part of instant preview on staging a new preview date is introduced.This will work only in staging The user will
     * enter the preview date against which the start and end date will be tested. in all other environments the current
     * date will be the preview date
     *
     * @param skuRepositoryItem the sku repository item
     * @param siteId the site id
     * @return SKU Active
     */
    public final boolean isSkuActive(final RepositoryItem skuRepositoryItem, final String siteId) {

        // Edited as part of Instant preview .If instant preview in not on like in production
        // then current date will be used as the preview date
        Date previewDate = new Date();
        if (this.isPreviewEnabled()) {
            previewDate = this.getPreviewDate();
            this.logDebug("Preview is enabled Value of Preview Date " + previewDate.toString());
        }
        if (skuRepositoryItem != null) {
            @SuppressWarnings ("unchecked")
            final Set<String> assocSites = (Set<String>) skuRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.SITE_IDS_SKU_PROPERTY_NAME);
            if ((assocSites != null) && !assocSites.isEmpty() && assocSites.contains(siteId)) {
                final Date startDate = (Date) skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME);
                final Date endDate = (Date) skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME);
                boolean webOffered = false;
                if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) != null) {
                    webOffered = ((Boolean) skuRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME)).booleanValue();
                }
                this.logTrace("SKU startDate[" + startDate + "] SKU endDate[" + endDate + "] Product webOffered[" + webOffered + "]");
                if(((endDate != null) && previewDate.after(endDate)) || ((startDate != null) && previewDate
                        .before(startDate))){
                	return false;
                }
                boolean disable = true;
                if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) != null) {
                    disable = ((Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME))
                                    .booleanValue();
                }
                this.logTrace("SKU disable[" + disable + "]");
                if (disable || !webOffered) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }
    
    /**
     * Adding new method for ever living page. Skus could be inactive or active in this case.
     *
     * @param skuRepositoryItem the sku repository item
     * @param siteId the site id
     * @param isSkuBelowLine the is sku below line
     * @return the ever living sku detail vo
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    private SKUDetailVO getEverLivingSKUDetailVO(final RepositoryItem skuRepositoryItem, final String siteId,
            final boolean isSkuBelowLine) throws BBBSystemException, BBBBusinessException {
		this.logDebug("Catalog API Method Name [getSKUDetailVO] for site " + siteId);
		final SKUDetailVO sKUDetailVO = new SKUDetailVO(skuRepositoryItem);
		final String skuId = skuRepositoryItem.getRepositoryId();
		final String isSDDON = getAllValuesForKey(BBBCmsConstants.SDD_KEY , BBBCmsConstants.SDD_SHOW_ATTRI).get(0);
		boolean sameDayDeliveryFlag = false;
		boolean currentZipEligibility = false;
    	String regionPromoAttr = BBBCoreConstants.BLANK;
		String sddEligibleOn = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG);
		
		sKUDetailVO.setSkuId(skuId);
		sKUDetailVO.setWebOfferedFlag(((Boolean) skuRepositoryItem
                .getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME)).booleanValue());
		sKUDetailVO.setActiveFlag(true);
		sKUDetailVO.setDisableFlag(((Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME))
                .booleanValue());
		
		 String isIntlRestricted = BBBCoreConstants.NO_CHAR;
		 if (!BBBUtility.isEmpty((String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED))) {
			 isIntlRestricted = (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED);
		 }
		 sKUDetailVO.setIntlRestricted(BBBCoreConstants.YES_CHAR.equalsIgnoreCase(isIntlRestricted));
		if (skuRepositoryItem.getPropertyValue(ON_SALE) != null) {
		    sKUDetailVO.setOnSale(((Boolean) skuRepositoryItem.getPropertyValue(ON_SALE)).booleanValue());
		}
		
		if(null != sddEligibleOn){
			sameDayDeliveryFlag = Boolean.valueOf(sddEligibleOn);
		}
		if(sameDayDeliveryFlag) {
			BBBSessionBean sessionBean = (BBBSessionBean) resolveComponentFromRequest(BBBCoreConstants.SESSION_BEAN);
	    	if(null!=sessionBean && null!=sessionBean.getCurrentZipcodeVO()) {
	    			currentZipEligibility = sessionBean.getCurrentZipcodeVO().isSddEligibility();
	    			regionPromoAttr =sessionBean.getCurrentZipcodeVO().getPromoAttId();
	      	}
		}
		
    	
		Map<String, RepositoryItem> attributeNameRepoItemMap = new HashMap<String, RepositoryItem>();
		attributeNameRepoItemMap = this.getSkuAttributeList(skuRepositoryItem, siteId, attributeNameRepoItemMap, regionPromoAttr, currentZipEligibility);
		boolean zoomFlag = true;
		if (!BBBUtility.isMapNullOrEmpty(attributeNameRepoItemMap)) {
		    try {
		        boolean rebateFlag = false;
		        final List<String> rebateAttributeKeyList = this.getAllValuesForKey(
		                        BBBCmsConstants.CONTENT_CATALOG_KEYS, this.getRebateKey());
		        if (!BBBUtility.isListEmpty(rebateAttributeKeyList)) {
		            final String rebateKeys[] = rebateAttributeKeyList.get(0).split(BBBCoreConstants.COMMA);
		            for (final String itemRebateKey : rebateKeys) {
		                if (attributeNameRepoItemMap.containsKey(itemRebateKey)) {
		                    rebateFlag = true;
		                    break;
		                }
		            }
		            if (rebateFlag) {
		                this.logTrace("Sku has Rebate Attribute setting eligible rebates ");
		                sKUDetailVO.setHasRebate(true);
		                sKUDetailVO.setEligibleRebates(this.updateRebatesForSku(skuRepositoryItem, siteId));
		            } else {
		                this.logTrace("Sku does not have Rebate Attribute Not setting eligible rebates ");
		                sKUDetailVO.setHasRebate(false);
		            }
		        }
		        
		     // hasSDDAttribs
                boolean sddAttribs = false;
                final List<String> sddAttributeKeyList = this.getAllValuesForKey(
                		BBBCmsConstants.SDD_KEY, BBBCmsConstants.SDD_ATTRIBUTE_LIST);
    			if ((sddAttributeKeyList != null) && !sddAttributeKeyList.isEmpty()) {
                    final String sddAtrribsKeys[] = sddAttributeKeyList.get(0).split(BBBCoreConstants.COMMA);
                    for (String itemSddKey : sddAtrribsKeys) {
                    	itemSddKey=itemSddKey.trim();
                        if (attributeNameRepoItemMap.containsKey(itemSddKey)) {
                        	sddAttribs = true;
                        	if(isSDDON.equalsIgnoreCase(BBBCoreConstants.FALSE)){
                        		attributeNameRepoItemMap.remove(itemSddKey);
                        	}
                        }
                    }
                    if (sddAttribs) {
                        this.logTrace("Sku has sddAttribs Attribute setting eligible SDD ");
                        sKUDetailVO.setHasSddAttribute(true);
                    } else {
                        this.logTrace("Sku has sddAttribs Attribute setting not eligible SDD ");
                        sKUDetailVO.setHasSddAttribute(false);
                    }
                }
		        // Code added for ZooM-CR
		        final List<String> zoomAttributeKeyList = this.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,
		                        this.getZoomKeys());
		        if ((zoomAttributeKeyList != null) && !zoomAttributeKeyList.isEmpty()) {
		            final String zoomAttributeKeys[] = zoomAttributeKeyList.get(0).split(BBBCoreConstants.COMMA);
		            for (final String zoomKey : zoomAttributeKeys) {
		                if (attributeNameRepoItemMap.containsKey(zoomKey)) {
		                    zoomFlag = false;
		                    break;
		                }
		            }
		        }
		    } catch (final BBBBusinessException e) {
		        this.logError("Business Exeption caught for rebate/zoom config key", e);
		    } catch (final BBBSystemException e) {
		        this.logError("System Exeption caught for rebate/zoom config key", e);
		    }
		    sKUDetailVO.setFreeShipMethods(this.updateFreeShipMethodForSku(attributeNameRepoItemMap.keySet()));
		    // sku attribute map in ProductVO
		    final Map<String, List<AttributeVO>> skuAttributesMap = this.getSkuAttributeMap(attributeNameRepoItemMap);
		    if (skuAttributesMap != null) {
		        this.logTrace("Setting attribute map for sku");
		        sKUDetailVO.setSkuAttributes(skuAttributesMap);
		    }
		}
		this.logTrace("IS Sku has zoom available : " + zoomFlag);
		sKUDetailVO.setZoomAvailable(zoomFlag);
//		We don't need shipping states and eligible shipping method for bopus only skus
		    sKUDetailVO.setCommaSepNonShipableStates(null);
		sKUDetailVO.setEligibleShipMethods(null);
		// if sku below line value is required
		if (isSkuBelowLine) {
		    sKUDetailVO.setSkuBelowLine(this.isSKUBelowLine(siteId, skuId));
		}
		sKUDetailVO.setIsEcoFeeEligible(this.isSkuEcoEligible(skuRepositoryItem));
		this.logTrace(sKUDetailVO.toString());
		return sKUDetailVO;
		}
    
    /**
     *  This method is used to set values for skudetails.
     *  It will call getSKUDetailVO with fromCart param value false
     *
     * @param skuRepositoryItem the sku repository item
     * @param siteId the site id
     * @param isSkuBelowLine the is sku below line
     * @return SKUDetailVO
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    protected SKUDetailVO getSKUDetailVO(final RepositoryItem skuRepositoryItem, final String siteId,
            final boolean isSkuBelowLine) throws BBBSystemException, BBBBusinessException {
    	  return this.getSKUDetailVO(skuRepositoryItem, siteId, isSkuBelowLine, false);
    }
    
    /**
     *  This method is used to set values for skudetails.
     *
     * @param skuRepositoryItem the sku repository item
     * @param siteId the site id
     * @param isSkuBelowLine the is sku below line
     * @return SKUDetailVO
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    protected SKUDetailVO getSKUDetailVO(final RepositoryItem skuRepositoryItem, final String siteId,
                    final boolean isSkuBelowLine, final boolean fromCart) throws BBBSystemException, BBBBusinessException {
    	final String isSDDON = getAllValuesForKey(BBBCmsConstants.SDD_KEY , BBBCmsConstants.SDD_SHOW_ATTRI).get(0);
        this.logDebug("Catalog API Method Name [getSKUDetailVO] for site " + siteId);
        final SKUDetailVO sKUDetailVO = new SKUDetailVO(skuRepositoryItem);
        final String skuId = skuRepositoryItem.getRepositoryId();
        boolean sameDayDeliveryFlag = false;
		boolean currentZipEligibility = false;
    	String regionPromoAttr = BBBCoreConstants.BLANK;
		String sddEligibleOn = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG);
        sKUDetailVO.setSkuId(skuId);
        sKUDetailVO.setWebOfferedFlag(true);
        sKUDetailVO.setActiveFlag(true);
        sKUDetailVO.setDisableFlag(false);
        sKUDetailVO.setLtlItem(false); 
        
        if(null != sddEligibleOn){
			sameDayDeliveryFlag = Boolean.valueOf(sddEligibleOn);
		}
        if(!fromCart){
	        @SuppressWarnings("unchecked")
			final Set<RepositoryItem> parentProduct = (Set<RepositoryItem>) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME);
			if ((parentProduct != null) && !parentProduct.isEmpty()) {
			    for (final RepositoryItem productRepositoryItem : parentProduct) {
			        if (this.isProductActive(productRepositoryItem)) {
			        	sKUDetailVO.setParentProdId(productRepositoryItem.getRepositoryId());
			            break;
			        }
			    }
			
			}
	        String isIntlRestricted = BBBCoreConstants.NO_CHAR;
	        if (!BBBUtility.isEmpty((String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED))) {
	        	isIntlRestricted = (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED);
			}
	        sKUDetailVO.setIntlRestricted(BBBCoreConstants.YES_CHAR.equalsIgnoreCase(isIntlRestricted));
	        if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) != null) {
	            sKUDetailVO.setLtlItem(((Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU)).booleanValue());
	        }
	        
	        if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.UPC_SKU_PROPERTY_NAME) != null) {
	            sKUDetailVO.setUpc((String)skuRepositoryItem.getPropertyValue(BBBCatalogConstants.UPC_SKU_PROPERTY_NAME));
	        }
	        	
	      
	        if (skuRepositoryItem.getPropertyValue(ON_SALE) != null) {
	            sKUDetailVO.setOnSale(((Boolean) skuRepositoryItem.getPropertyValue(ON_SALE)).booleanValue());
	        }
	
	        if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_ASSEMBLY_FEE_OFFERED) != null) {
	        	sKUDetailVO.setAssemblyOffered((Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_ASSEMBLY_FEE_OFFERED));
			}
	        
	        if(sameDayDeliveryFlag) {
	        	BBBSessionBean sessionBean = (BBBSessionBean) resolveComponentFromRequest(BBBCoreConstants.SESSION_BEAN);
		    	if(null!=sessionBean && null!=sessionBean.getCurrentZipcodeVO()) {
		    			currentZipEligibility = sessionBean.getCurrentZipcodeVO().isSddEligibility();
		    			regionPromoAttr =sessionBean.getCurrentZipcodeVO().getPromoAttId();
		      	}
	        }
	        
	    	
	        Map<String, RepositoryItem> attributeNameRepoItemMap = new HashMap<String, RepositoryItem>();
	        attributeNameRepoItemMap = this.getSkuAttributeList(skuRepositoryItem, siteId, attributeNameRepoItemMap, regionPromoAttr, currentZipEligibility);
	        boolean zoomFlag = true;
	        if (!BBBUtility.isMapNullOrEmpty(attributeNameRepoItemMap)) {
	            try {
	                boolean rebateFlag = false;
	                final List<String> rebateAttributeKeyList = this.getAllValuesForKey(
	                                BBBCmsConstants.CONTENT_CATALOG_KEYS, this.getRebateKey());
	                if ((rebateAttributeKeyList != null) && !rebateAttributeKeyList.isEmpty()) {
	                    final String rebateKeys[] = rebateAttributeKeyList.get(0).split(BBBCoreConstants.COMMA);
	                    for (final String itemRebateKey : rebateKeys) {
	                        if (attributeNameRepoItemMap.containsKey(itemRebateKey)) {
	                            rebateFlag = true;
	                            break;
	                        }
	                    }
	                    if (rebateFlag) {
	                        this.logTrace("Sku has Rebate Attribute setting eligible rebates ");
	                        sKUDetailVO.setHasRebate(true);
	                        sKUDetailVO.setEligibleRebates(this.updateRebatesForSku(skuRepositoryItem, siteId));
	                    } else {
	                        this.logTrace("Sku does not have Rebate Attribute Not setting eligible rebates ");
	                        sKUDetailVO.setHasRebate(false);
	                    }
	                }
	             // hasSDDAttribs
	                
	                boolean sddAttribs = false;
	                final List<String> sddAttributeKeyList = this.getAllValuesForKey(
	                		BBBCmsConstants.SDD_KEY, BBBCmsConstants.SDD_ATTRIBUTE_LIST);
	    			if ((sddAttributeKeyList != null) && !sddAttributeKeyList.isEmpty()) {
	                    final String sddAtrribsKeys[] = sddAttributeKeyList.get(0).split(BBBCoreConstants.COMMA);
	                    for (String itemSddKey : sddAtrribsKeys) {
	                    	itemSddKey=itemSddKey.trim();
	                        if (attributeNameRepoItemMap.containsKey(itemSddKey)) {
	                        	sddAttribs = true;
	                        	if(isSDDON.equalsIgnoreCase(BBBCoreConstants.FALSE)){
	                        		attributeNameRepoItemMap.remove(itemSddKey);
	                        	}
	                        }
	                    }
	                    if (sddAttribs) {
	                        this.logTrace("Sku has sddAttribs Attribute setting eligible SDD ");
	                        sKUDetailVO.setHasSddAttribute(true);
	                    } else {
	                        this.logTrace("Sku has sddAttribs Attribute setting not eligible SDD ");
	                        sKUDetailVO.setHasSddAttribute(false);
	                    }
	                }
	                // Code added for ZooM-CR
	                final List<String> zoomAttributeKeyList = this.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,
	                                this.getZoomKeys());
	                if ((zoomAttributeKeyList != null) && !zoomAttributeKeyList.isEmpty()) {
	                    final String zoomAttributeKeys[] = zoomAttributeKeyList.get(0).split(BBBCoreConstants.COMMA);
	                    for (final String zoomKey : zoomAttributeKeys) {
	                        if (attributeNameRepoItemMap.containsKey(zoomKey)) {
	                            zoomFlag = false;
	                            break;
	                        }
	                    }
	                }
	            } catch (final BBBBusinessException e) {
	                this.logError("Business Exeption caught for rebate/zoom config key", e);
	            } catch (final BBBSystemException e) {
	                this.logError("System Exeption caught for rebate/zoom config key", e);
	            }
	            sKUDetailVO.setFreeShipMethods(this.updateFreeShipMethodForSku(attributeNameRepoItemMap.keySet()));
	            // sku attribute map in ProductVO
	            final Map<String, List<AttributeVO>> skuAttributesMap = this.getSkuAttributeMap(attributeNameRepoItemMap);
	            if (skuAttributesMap != null) {
	                this.logTrace("Setting attribute map for sku");
	                sKUDetailVO.setSkuAttributes(skuAttributesMap);
	            }
	        }
	        this.logTrace("IS Sku has zoom available : " + zoomFlag);
	        sKUDetailVO.setZoomAvailable(zoomFlag);
	        
	        List<String> shiptoPOBoxOn;
			boolean shiptoPOFlag =false;
			shiptoPOBoxOn = getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON);
			shiptoPOFlag = Boolean.parseBoolean(shiptoPOBoxOn.get(0));
	        final List<StateVO> nonShippableStates = this.getNonShippableStatesForSku(siteId, skuId);
	        if (nonShippableStates != null) {
	            this.logTrace("Setting nonShippableStates for sku");
	            sKUDetailVO.setNonShippableStates(nonShippableStates);
	            final StringBuilder statesString = new StringBuilder();
	            final int nonShippableStatesSize = nonShippableStates.size();
	            for (int counter = 0; counter < nonShippableStatesSize; counter++) {
	                if ((nonShippableStates.get(counter).getStateName() != null) && (!(nonShippableStates.get(counter).getStateCode().equals("PO")  && shiptoPOFlag
		        			&& BBBUtility.siteIsTbs(siteId)))) {

		                    if (counter == (nonShippableStatesSize - 1)) {
		                        statesString.append(nonShippableStates.get(counter).getStateName());
		                    } else {
		                        statesString.append(nonShippableStates.get(counter).getStateName()).append(',');
		                    }
	                }
	            }
	
	            sKUDetailVO.setCommaSepNonShipableStates(statesString.toString());
	        }
	        
	        sKUDetailVO.setShippingRestricted(isShippingRestrictionsExistsForSku(skuId));
	        if (sKUDetailVO.isLtlItem()) {
	        	 sKUDetailVO.setEligibleShipMethods(this.getLTLEligibleShippingMethods(skuId, siteId, this.getDefaultLocale()));
			} else {
				 sKUDetailVO.setEligibleShipMethods(this.getShippingMethodsForSku(siteId, skuId, sameDayDeliveryFlag));
			}
	        
	        // if sku below line value is required
	        if (isSkuBelowLine) {
	            sKUDetailVO.setSkuBelowLine(this.isSKUBelowLine(siteId, skuId));
	        }
	        List<String> isCustomizable = this.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.IS_CUSTOMIZABLE_ON);
	        if(isCustomizable.get(0).equalsIgnoreCase(BBBCoreConstants.TRUE)){
	        	sKUDetailVO.setCustomizationOffered(this.isCustomizationOfferedForSKU(skuRepositoryItem,siteId));
	        	sKUDetailVO.setCustomizableRequired(this.isCustomizationRequiredForSKU(skuRepositoryItem,siteId));
	        	sKUDetailVO.setPersonalizationType((String) skuRepositoryItem.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE));
	        	sKUDetailVO.setCustomizableCodes((String)skuRepositoryItem.getPropertyValue(BBBCoreConstants.ELIGIBLE_CUSTOMIZATION_CODES));
	        }else{
	        	sKUDetailVO.setCustomizationOffered(false);
	        	sKUDetailVO.setCustomizableRequired(false);
	        }
	        sKUDetailVO.setIsEcoFeeEligible(this.isSkuEcoEligible(skuRepositoryItem));
	        if(isLoggingDebug()){
	        	this.logDebug(sKUDetailVO.toString());
	        }
	        String shipMsgDisplayFlag = BBBCoreConstants.FALSE;
			try {
				shipMsgDisplayFlag = getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG).get(0);
			} catch (BBBSystemException | BBBBusinessException e) {
				logError("Error while getting config key ShipMsgDisplayFlag value", e);
			}
			if(Boolean.parseBoolean(shipMsgDisplayFlag)){
				updateShippingMessageFlag(sKUDetailVO, false, 0.0);
			}
		
        }
        return sKUDetailVO;
    }

	
	/**
	 * This methods takes the skuId and siteId and returns the list of shipping methods.
	 *
	 * @param skuId skuId of the commerce item
	 * @param siteId siteId
	 * @param locale the locale
	 * @return list of ShipMethodVO
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public List<ShipMethodVO> getLTLEligibleShippingMethods(final String skuId,
			final String siteId,final String locale) throws BBBSystemException,
			BBBBusinessException {
			this.logDebug("[START] BBBCatalogToolsImpl.getLTLEligibleShippingMethods");
		// get all ltl eligible shipping methods for a sku item & site id
		boolean sameDayDeliveryFlag = false;
		String sddEligibleOn = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG);
		if(null != sddEligibleOn){
			sameDayDeliveryFlag = Boolean.valueOf(sddEligibleOn);
		}
		 List<ShipMethodVO> ltlShipMehtodsVO = this.getShippingMethodsForSku(siteId, skuId, sameDayDeliveryFlag);
		if(ltlShipMehtodsVO != null && !ltlShipMehtodsVO.isEmpty() && !ltlShipMehtodsVO.get(0).isLtlShipMethod()){
			ltlShipMehtodsVO.clear();
			return ltlShipMehtodsVO;
		}

		final Double caseWeight = this.getCaseWeightForSku(skuId);
		
		if(caseWeight!=null && ltlShipMehtodsVO != null && !ltlShipMehtodsVO.isEmpty()){
		
		//get all delivery surcharge prices for ltl shipping methods for a sku 
		final RepositoryItem[] deliverySurchargeItem = this.getCmsTools().getAllSurchargePrice(caseWeight, siteId);
		final boolean isAssemblyFeeOffered=this.isAssemblyFeeOffered(skuId);
		boolean isWhiteGlovePresent=false;
		double whiteGloveDeliverySurcharge = 0.00;
		if(deliverySurchargeItem != null && deliverySurchargeItem.length > 0){
			for (int i = 0; i < deliverySurchargeItem.length; i++) {
				if(deliverySurchargeItem[i] != null){
					final double deliverySurcharge = ((Double)deliverySurchargeItem[i].getPropertyValue(BBBCatalogConstants.DELIVERY_SURCHARGE_PROPERTY_NAME));
					final RepositoryItem shippingMethodRepoItem=(RepositoryItem)deliverySurchargeItem[i].getPropertyValue(BBBCatalogConstants.SHIP_METHOD_CD);
					final String shippingMethodId = (String) shippingMethodRepoItem.getPropertyValue(BBBCatalogConstants.ID);

					for (final ShipMethodVO shipMethodVO : ltlShipMehtodsVO) {
						if (shipMethodVO.getShipMethodId().equalsIgnoreCase(shippingMethodId)) {
							shipMethodVO.setDeliverySurcharge(BBBOrderUtilty.convertToTwoDecimals(deliverySurcharge));
						}
					}
				}
				
			}
		}
			for (final ShipMethodVO shipMethodVO : ltlShipMehtodsVO) {
				if (BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD.equalsIgnoreCase(shipMethodVO.getShipMethodId())) {
					whiteGloveDeliverySurcharge = shipMethodVO.getDeliverySurcharge();
					isWhiteGlovePresent = true;
					break;
				}
				
			}
		//calculate assembly fees if white glove is present
		if(isWhiteGlovePresent && isAssemblyFeeOffered){
					double assemblyFees = 0.00;
					assemblyFees = this.getAssemblyCharge(siteId, skuId);
				
					final ShipMethodVO whiteGloveWithAssemblyVO = new ShipMethodVO();
					whiteGloveWithAssemblyVO.setAssemblyFees(assemblyFees);
					whiteGloveWithAssemblyVO.setShipMethodId(BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD);
					whiteGloveWithAssemblyVO.setShipMethodDescription(getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_WHITE_GLOVE_ASSEMBLY, locale, null,siteId));
					whiteGloveWithAssemblyVO.setDeliverySurcharge(assemblyFees + whiteGloveDeliverySurcharge);
					ltlShipMehtodsVO.add(whiteGloveWithAssemblyVO);
			}
		}
		if (ltlShipMehtodsVO != null && !ltlShipMehtodsVO.isEmpty()) {
			Collections.sort(ltlShipMehtodsVO,new DeliveryChargeComparator());
		}
			this.logDebug("[Exit] BBBCatalogToolsImpl.getLTLEligibleShippingMethods");
		return ltlShipMehtodsVO;		
	}
    
    /**
     *  The method checks if the sku is eligible for EcoFee.
     *
     * @param skuRepositoryItem the sku repository item
     * @return true, if is sku eco eligible
     */
    private boolean isSkuEcoEligible(final RepositoryItem skuRepositoryItem) {
        boolean isEcoEligible = false;
        if (skuRepositoryItem != null) {
            @SuppressWarnings ("unchecked")
            final Set<RepositoryItem> ecoReln = (Set<RepositoryItem>) skuRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.ECO_FEE_SKU_RELN_PROPERTY_NAME);
            if ((ecoReln != null) && !ecoReln.isEmpty()) {
                isEcoEligible = true;
            }

            this.logDebug("skuId " + skuRepositoryItem.getRepositoryId() + " eco eligibility: " + isEcoEligible);
        }
        return isEcoEligible;

    }
    
    /**
     * This method will get the productVO for ever living pdp page. This is differenet from regular productVO.
     * As it's adding the SKUs in the productVO even if they are not active currently. 
     *
     * The method sets the properties of product in the ProductVO.
     *
     * @param productRepositoryItem the product repository item
     * @param siteId the site id
     * @param populateRollUp the populate roll up
     * @param isMinimalDetails the is minimal details
     * @param isEverLivingProduct the is ever living product
     * @return the ever living product vo
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */

   private ProductVO getEverLivingProductVO(final RepositoryItem productRepositoryItem, final String siteId,
                   final boolean populateRollUp, final boolean isMinimalDetails, final boolean isEverLivingProduct)
                   throws BBBBusinessException, BBBSystemException {

           final StringBuilder debug = new StringBuilder(40);
           debug.append("Catalog API Method Name [getProductVO] siteId[").append(siteId).append("] populateRollUp ")
                           .append(populateRollUp).append(']');
           this.logDebug(debug.toString());
      // if (productRepositoryItem != null) {
           ProductVO productVO = new ProductVO();
           String isIntlRestricted = BBBCoreConstants.NO_CHAR;
           if ( !BBBUtility.isEmpty((String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED))) {
        	   isIntlRestricted = (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED);
           }
      	   productVO.setIntlRestricted(BBBCoreConstants.YES_CHAR.equalsIgnoreCase(isIntlRestricted));
           if (!isMinimalDetails) {
               @SuppressWarnings ("unchecked")
               final List<RepositoryItem> skuRepositoryItems = (List<RepositoryItem>) productRepositoryItem
                               .getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);

               if (skuRepositoryItems != null) {
                   this.logTrace("Product has child Sku");
                   final List<String> childSkuIdList = new ArrayList<String>();
                   /* Get list of all distinct attribute Repository Items from the Sku Repository Items of the product */
                   for (int i = 0; i < skuRepositoryItems.size(); i++) {
                           childSkuIdList.add(skuRepositoryItems.get(i).getRepositoryId());
                   }
                   if (!childSkuIdList.isEmpty()) {
                       productVO.setChildSKUs(childSkuIdList);
                   }

                   if (populateRollUp) {
                       this.logDebug("Attributes are populated from SKU as product is not a collection");
                       final RepositoryItem rollUpTypesRepositoryItem = (RepositoryItem) productRepositoryItem
                                       .getPropertyValue(BBBCatalogConstants.PRODUCT_ROLL_UP_PRODUCT_PROPERTY_NAME);
                       if ((rollUpTypesRepositoryItem != null)
                                       && (rollUpTypesRepositoryItem
                                                       .getPropertyValue(BBBCatalogConstants.ROLLUP_ATTRIBUTE_ROLLUP_PROPERTY_NAME) != null)) {
                           final String rollUpAttribute = (String) rollUpTypesRepositoryItem
                                           .getPropertyValue(BBBCatalogConstants.ROLLUP_ATTRIBUTE_ROLLUP_PROPERTY_NAME);
                           // for ever Living we don't need to check product status
							productVO.setRollupAttributes(this.getRollUpAttributeForProduct(rollUpAttribute,skuRepositoryItems,false));
                       }
                   }
					productVO = this.updateProductTabs(productRepositoryItem, siteId, productVO);
               }
           }

           Map<String, RepositoryItem> attributeNameRepoItemMap = new HashMap<String, RepositoryItem>();
           attributeNameRepoItemMap = this.getProductAttributeList(productRepositoryItem, siteId,
                           attributeNameRepoItemMap);
           
           // Code added for ZooM-CR
           boolean zoomFlag = true;
           if (!BBBUtility.isMapNullOrEmpty(attributeNameRepoItemMap)) {
               try {
                   final List<String> zoomAttributeKeyList = this.getAllValuesForKey(
                                   BBBCmsConstants.CONTENT_CATALOG_KEYS, this.getZoomKeys());
                   if (!BBBUtility.isListEmpty(zoomAttributeKeyList)) {
                       final String zoomAttributeKeys[] = zoomAttributeKeyList.get(0).split(BBBCoreConstants.COMMA);
                       for (final String zoomKey : zoomAttributeKeys) {
                           if (attributeNameRepoItemMap.containsKey(zoomKey)) {
                               zoomFlag = false;
                               break;
                           }
                       }
                   }
               } catch (final BBBBusinessException e) {
                   this.logError("Business Exeption caught for zoom config key", e);
               } catch (final BBBSystemException e) {
                   this.logError("System Exeption caught for zoom config key", e);
               }
           }
           this.logTrace("Is Product has zoom available : " + zoomFlag);
           productVO.setZoomAvailable(zoomFlag);
           
			/* Removing SDD attribute in case of SHOW_SDD_ATTRIBUTE is false */
			vlogDebug("BBBCatalogToolsImpl.getEverLivingProductVO: Before removing SDD attribute - {0}",
					attributeNameRepoItemMap);
			final List<String> sddAttributeKeyList = this.getAllValuesForKey(BBBCmsConstants.SDD_KEY,
					BBBCmsConstants.SDD_ATTRIBUTE_LIST);
			if ((sddAttributeKeyList != null) && !sddAttributeKeyList.isEmpty()) {
				final String sddAtrribsKeys[] = sddAttributeKeyList.get(0).split(BBBCoreConstants.COMMA);
				final String isSDDON = getAllValuesForKey(BBBCmsConstants.SDD_KEY, BBBCmsConstants.SDD_SHOW_ATTRI).get(
						0);
				for (String itemSddKey : sddAtrribsKeys) {
					itemSddKey = itemSddKey.trim();
					if (attributeNameRepoItemMap.containsKey(itemSddKey)) {
						if (isSDDON.equalsIgnoreCase(BBBCoreConstants.FALSE)) {
							attributeNameRepoItemMap.remove(itemSddKey);
						}
					}
				}
			}
			vlogDebug("BBBCatalogToolsImpl.getEverLivingProductVO: After removing SDD attribute - {0}",
					attributeNameRepoItemMap);
			
           // sku attribute map in ProductVO
           final Map<String, List<AttributeVO>> prodAttributesMap = this.getProdAttributeMap(attributeNameRepoItemMap);
           if (prodAttributesMap != null) {
               productVO.setAttributesList(prodAttributesMap);
           }
           productVO.setPriceRangeDescription((String) productRepositoryItem
                           .getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME));
           productVO.setPriceRangeDescriptionRepository((String) productRepositoryItem
                   .getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME));
           productVO.setDefaultPriceRangeDescription((String) productRepositoryItem
					.getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME));
           productVO = updateProductBasicDetails(productVO, productRepositoryItem, isMinimalDetails, siteId);
           final BazaarVoiceProductVO bvReviews = this.getBazaarVoiceDetails(productRepositoryItem.getRepositoryId(),
                           siteId);
           productVO.setBvReviews(bvReviews);
           // Logic for View Product guide link on PDP.
           final String prodGuideId = this.getProductGuideId(productRepositoryItem, siteId);
           if (!BBBUtility.isEmpty(prodGuideId)) {
               productVO.setShopGuideId(prodGuideId);
           }
           productVO = this.updatePriceDescription(productVO);
           //setting everliving flag
           productVO.setIsEverLiving(isEverLivingProduct);
           
           productVO.setGiftCertProduct((Boolean) productRepositoryItem.getPropertyValue("giftCertProduct"));
           this.logTrace(productVO.toString());
           return productVO;
      /* }
       return null;*/
   }
    
    /**
     *  The method sets the properties of product in the ProductVO.
     *
     * @param productRepositoryItem the product repository item
     * @param siteId the site id
     * @param populateRollUp the populate roll up
     * @param isMinimalDetails the is minimal details
     * @return the product vo
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */

    public ProductVO getProductVO(final RepositoryItem productRepositoryItem, final String siteId,
                    final boolean populateRollUp, final boolean isMinimalDetails)
                    throws BBBBusinessException, BBBSystemException {
            final StringBuilder debug = new StringBuilder(40);
            debug.append("Catalog API Method Name [getProductVO] siteId[").append(siteId).append("] populateRollUp ")
                            .append(populateRollUp).append(']');
            this.logDebug(debug.toString());
       // if (productRepositoryItem != null) {
            ProductVO productVO = new ProductVO();
            String isIntlRestricted = BBBCoreConstants.NO_CHAR;
            if ( !BBBUtility.isEmpty((String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED))) {
            	isIntlRestricted = (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED);
            }
       	    productVO.setIntlRestricted(BBBCoreConstants.YES_CHAR.equalsIgnoreCase(isIntlRestricted));
            if (!isMinimalDetails) {
                @SuppressWarnings ("unchecked")
                final List<RepositoryItem> skuRepositoryItems = (List<RepositoryItem>) productRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);

                if (skuRepositoryItems != null) {
                    this.logTrace("Product has child Sku");
                    final List<String> childSkuIdList = new ArrayList<String>();
                    /* Get list of all distinct attribute Repository Items from the Sku Repository Items of the product */
                    for (int i = 0; i < skuRepositoryItems.size(); i++) {
                        if (this.isSkuActive(skuRepositoryItems.get(i))) {
                            childSkuIdList.add(skuRepositoryItems.get(i).getRepositoryId());
                        }
                    }
                    if (!childSkuIdList.isEmpty()) {
                        productVO.setChildSKUs(childSkuIdList);
                    }

                    if (populateRollUp) {
                        this.logDebug("Attributes are populated from SKU as product is not a collection");
                        final RepositoryItem rollUpTypesRepositoryItem = (RepositoryItem) productRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.PRODUCT_ROLL_UP_PRODUCT_PROPERTY_NAME);
                        if ((rollUpTypesRepositoryItem != null)
                                        && (rollUpTypesRepositoryItem
                                                        .getPropertyValue(BBBCatalogConstants.ROLLUP_ATTRIBUTE_ROLLUP_PROPERTY_NAME) != null)) {
                        	int lengthOfRollUpAttr= 0;
                        	
                        	int lengthOfSizeSwatch = (Integer.parseInt(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.LENGTH_OF_SWATCH)));
                            final String rollUpAttribute = (String) rollUpTypesRepositoryItem
                                            .getPropertyValue(BBBCatalogConstants.ROLLUP_ATTRIBUTE_ROLLUP_PROPERTY_NAME);
                           
                            Map<String, List<RollupTypeVO>> rollup=  this.getRollUpAttributeForProduct(rollUpAttribute,skuRepositoryItems,true);
							 if(rollup.get("SIZE")!= null ){
								 for(int i=0;i<rollup.get("SIZE").size();i++)
								 {
									String rollUpAttributeValue = StringEscapeUtils.unescapeHtml(rollup.get("SIZE").get(i).getRollupAttribute());
									lengthOfRollUpAttr = lengthOfRollUpAttr + rollUpAttributeValue.length()+ lengthOfSwatch;
									  
								 }
								 if(lengthOfRollUpAttr > lengthOfSizeSwatch){
		                            	productVO.setDisplaySizeAsSwatch(false);
		            				}
		                            else{
		                            	productVO.setDisplaySizeAsSwatch(true);
		                            }
	                            }
                            // Flag Added to check Product/SKU Active item for SiteMap Changes
							productVO.setRollupAttributes(rollup);
                        }
                    }
					productVO = this.updateProductTabs(productRepositoryItem, siteId,productVO);
                }
            }

            Map<String, RepositoryItem> attributeNameRepoItemMap = new HashMap<String, RepositoryItem>();
            attributeNameRepoItemMap = this.getProductAttributeList(productRepositoryItem, siteId,
                            attributeNameRepoItemMap);
            // Code added for ZooM-CR
            boolean zoomFlag = true;
            if (!BBBUtility.isMapNullOrEmpty(attributeNameRepoItemMap)) {
                try {
                    final List<String> zoomAttributeKeyList = this.getAllValuesForKey(
                                    BBBCmsConstants.CONTENT_CATALOG_KEYS, this.getZoomKeys());
                    
                    if (!BBBUtility.isListEmpty(zoomAttributeKeyList)) {
                        final String zoomAttributeKeys[] = zoomAttributeKeyList.get(0).split(BBBCoreConstants.COMMA);
                        for (final String zoomKey : zoomAttributeKeys) {
                            if (attributeNameRepoItemMap.containsKey(zoomKey)) {
                                zoomFlag = false;
                                break;
                            }
                        }
                    }
                } catch (final BBBBusinessException e) {
                    this.logDebug("Business Exeption caught for zoom config key", e);
                } catch (final BBBSystemException e) {
                    this.logDebug("System Exeption caught for zoom config key", e);
                }
            }
            this.logTrace("Is Product has zoom available : " + zoomFlag);
            productVO.setZoomAvailable(zoomFlag);
            
			/* Removing SDD attribute in case of SHOW_SDD_ATTRIBUTE is false */
			vlogDebug("BBBCatalogToolsImpl.getProductVO: Before removing SDD attribute - {0}", attributeNameRepoItemMap);
			final List<String> sddAttributeKeyList = this.getAllValuesForKey(BBBCmsConstants.SDD_KEY,
					BBBCmsConstants.SDD_ATTRIBUTE_LIST);
			if ((sddAttributeKeyList != null) && !sddAttributeKeyList.isEmpty()) {
				final String sddAtrribsKeys[] = sddAttributeKeyList.get(0).split(BBBCoreConstants.COMMA);
				final String isSDDON = getAllValuesForKey(BBBCmsConstants.SDD_KEY, BBBCmsConstants.SDD_SHOW_ATTRI).get(
						0);
				for (String itemSddKey : sddAtrribsKeys) {
					itemSddKey = itemSddKey.trim();
					if (attributeNameRepoItemMap.containsKey(itemSddKey)) {
						if (isSDDON.equalsIgnoreCase(BBBCoreConstants.FALSE)) {
							attributeNameRepoItemMap.remove(itemSddKey);
						}
					}
				}
			}
			vlogDebug("BBBCatalogToolsImpl.getProductVO: After removing SDD attribute - {0}", attributeNameRepoItemMap);
			
            // sku attribute map in ProductVO
            final Map<String, List<AttributeVO>> prodAttributesMap = this.getProdAttributeMap(attributeNameRepoItemMap);
            if (prodAttributesMap != null) {
                productVO.setAttributesList(prodAttributesMap);
            }
            productVO.setPriceRangeDescription((String) productRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME));
            productVO.setPriceRangeDescriptionRepository((String) productRepositoryItem
                    .getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME));
            productVO.setDefaultPriceRangeDescription((String) productRepositoryItem
					.getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME));
            productVO = updateProductBasicDetails(productVO, productRepositoryItem, isMinimalDetails, siteId);
            final BazaarVoiceProductVO bvReviews = this.getBazaarVoiceDetails(productRepositoryItem.getRepositoryId(),
                            siteId);
            productVO.setBvReviews(bvReviews);
            // Logic for View Product guide link on PDP.
            final String prodGuideId = this.getProductGuideId(productRepositoryItem, siteId);
            if (!BBBUtility.isEmpty(prodGuideId)) {
                productVO.setShopGuideId(prodGuideId);
            }
            productVO = this.updatePriceDescription(productVO);
            
            productVO.setGiftCertProduct((Boolean) productRepositoryItem.getPropertyValue("giftCertProduct"));
            //LTL check product
            if(null != (Boolean) productRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU))
            {
                productVO.setLtlProduct((Boolean) productRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU));
            }
            else
            {
            	productVO.setLtlProduct(false);
            }
            
            getVendorInfoProductVO(productVO, productRepositoryItem);
            
            this.logTrace(productVO.toString());
            String shipMsgDisplayFlag = BBBCoreConstants.FALSE;
			try {
				shipMsgDisplayFlag = getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG).get(0);
			} catch (BBBSystemException | BBBBusinessException e) {
				logError("Error while getting config key ShipMsgDisplayFlag value", e);
			}
			if(Boolean.parseBoolean(shipMsgDisplayFlag)){
				 updateShippingMessageFlag(productVO);
			}
            return productVO;
       // }
        //return null;
    }
   
    
    /**
     * Update shipping message flag.
     *
     * @param sKUDetailVO the s ku detail vo
     * @param isSkuPersonalized the is sku personalized
     * @param personalizePrice the personalize price
     * @throws BBBBusinessException the BBB business exception
     */
    public void updateShippingMessageFlag(SKUDetailVO sKUDetailVO, boolean isSkuPersonalized, double personalizePrice) throws BBBBusinessException {
    	
    	if(sKUDetailVO.isLtlItem() || ServletUtil.getCurrentRequest() ==null|| (((BBBSessionBean) BBBProfileManager.resolveSessionBean(null)).isInternationalShippingContext())){
    		return;
    	}

    	double priceVal = 0.00;
    	double higherShipThreshhold = 0.00;
    	String higherShippingThreshhold =  getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_HIGHER_FREE_SHIPPING_THRESHOLD, this.getDefaultLocale(), null,SiteContextManager.getCurrentSiteId());
    	if(!StringUtils.isBlank(higherShippingThreshhold)){
    		String trimedHigherShippingThreshold = higherShippingThreshhold.replaceAll("[^0-9^.]", BBBCoreConstants.BLANK).trim();
    		if(!trimedHigherShippingThreshold.equalsIgnoreCase(BBBCoreConstants.BLANK)){		
    			higherShipThreshhold = Double.parseDouble(higherShippingThreshhold);
    		} 
    	} else{
    		return;
    	}
    	try {
    		priceVal = getListPrice(sKUDetailVO.getParentProdId(), sKUDetailVO.getSkuId());
		if (!StringUtils.isEmpty(sKUDetailVO.getSkuId()) && this.isSkuOnSale(sKUDetailVO.getParentProdId(), sKUDetailVO.getSkuId())) {
			priceVal = getSalePrice(sKUDetailVO.getParentProdId(), sKUDetailVO.getSkuId());
		}
		if(isSkuPersonalized && !StringUtils.isBlank(sKUDetailVO.getPersonalizationType())){
			if(sKUDetailVO.getPersonalizationType().equals("PY")){
				priceVal = priceVal + personalizePrice;
			} else if(sKUDetailVO.getPersonalizationType().equals("CR")){
				priceVal = personalizePrice;
			}
			
		}	
    	} catch (BBBSystemException e) {
    		this.logError("System Exeption caught while getting price", e);
    	}
    	Map<String, String> placeholderMap = new HashMap<String, String>();
    	placeholderMap.put(BBBCoreConstants.CURRENCY, BBBCoreConstants.DOLLAR);
    	placeholderMap.put(BBBCoreConstants.HIGHER_SHIP_THRESHHOLD, higherShippingThreshhold);
    	if(priceVal > higherShipThreshhold){
    		sKUDetailVO.setShipMsgFlag(true);
    		sKUDetailVO.setDisplayShipMsg(getLblTxtTemplateManager().getPageTextArea(BBBCoreConstants.TXT_FREESHIPPING_PRODUCT, placeholderMap));
    	}

    }
    
    /**
     * Update shipping message flag.
     *
     * @param productVO the product vo
     */
    private void updateShippingMessageFlag(ProductVO productVO) {
    	if(productVO.isLtlProduct() || ServletUtil.getCurrentRequest() ==null ||
    			(((BBBSessionBean) ServletUtil.getCurrentRequest().resolveName(BBBGiftRegistryConstants.SESSION_BEAN)).isInternationalShippingContext())){
    		return;
    	}
    	Double lowPrice = productVO.getLowPrice();
    	Double highPrice = productVO.getHighPrice();
    	double higherShipThreshhold = 0.00;
    	String higherShippingThreshhold =  getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_HIGHER_FREE_SHIPPING_THRESHOLD, this.getDefaultLocale(), null,SiteContextManager.getCurrentSiteId());
    	if(!StringUtils.isBlank(higherShippingThreshhold)){
    		String trimedHigherShippingThreshold = higherShippingThreshhold.replaceAll("[^0-9^.]", BBBCoreConstants.BLANK).trim();
    		if(!trimedHigherShippingThreshold.equalsIgnoreCase(BBBCoreConstants.BLANK)){		
    			higherShipThreshhold = Double.parseDouble(higherShippingThreshhold);
    		} 
    	} else{
    		return;
    	} 
    	Map<String, String> placeholderMap = new HashMap<String, String>();
    	placeholderMap.put(BBBCoreConstants.CURRENCY, BBBCoreConstants.DOLLAR);
    	placeholderMap.put(BBBCoreConstants.HIGHER_SHIP_THRESHHOLD, higherShippingThreshhold);
    	if(lowPrice != null && lowPrice < higherShipThreshhold && highPrice != null && highPrice > higherShipThreshhold){
    		productVO.setShipMsgFlag(true);
    		productVO.setDisplayShipMsg(getLblTxtTemplateManager().getPageTextArea(BBBCoreConstants.TXT_FREESHIPPING_COLLECTIONS_PRODUCT, placeholderMap));
    	}   else if(lowPrice != null && lowPrice > higherShipThreshhold){
    		productVO.setShipMsgFlag(true);
    		productVO.setDisplayShipMsg(getLblTxtTemplateManager().getPageTextArea(BBBCoreConstants.TXT_FREESHIPPING_PRODUCT, placeholderMap));
    	}


    }

	// Start : R 2.2 Product Image SiteMap Generation 504-b 
	/**
	 * This method gives ProductVO without checking for Active Flag for SKU's.
	 * This is added to get details for SiteMap generation story
	 *
	 * @param productRepositoryItem the product repository item
	 * @param siteId the site id
	 * @param populateRollUp the populate roll up
	 * @return the product vo
	 */
	private ProductVO getProductVO(RepositoryItem productRepositoryItem,String siteId,boolean populateRollUp){
		
		if(productRepositoryItem!=null ){
			ProductVO productVO=new ProductVO();
			String isIntlRestricted = BBBCoreConstants.NO_CHAR;
	        if ( !BBBUtility.isEmpty((String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED))) {
	        	isIntlRestricted = (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED);
	        }
	      	productVO.setIntlRestricted(BBBCoreConstants.YES_CHAR.equalsIgnoreCase(isIntlRestricted));
			List <RepositoryItem> skuRepositoryItems= (List<RepositoryItem>) productRepositoryItem.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);

				if(skuRepositoryItems!=null){
					List<String> childSkuIdList=new ArrayList<String>();
					/*
					 * Get list of all distinct attribute Repository Items from the Sku Repository Items of the product
					 */
					for(int i=0;i<skuRepositoryItems.size();i++){
						childSkuIdList.add(skuRepositoryItems.get(i).getRepositoryId());
					}
					if(childSkuIdList!=null && !childSkuIdList.isEmpty()){
						productVO.setChildSKUs(childSkuIdList);
					}

					if(populateRollUp){
						logDebug("Attributes are populated from SKU as product is not a collection");
						RepositoryItem rollUpTypesRepositoryItem=(RepositoryItem) productRepositoryItem.getPropertyValue(BBBCatalogConstants.PRODUCT_ROLL_UP_PRODUCT_PROPERTY_NAME);
						int lengthOfRollUpAttr= 0;
						int lengthOfSizeSwatch = (Integer.parseInt(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.LENGTH_OF_SWATCH)));
						if(rollUpTypesRepositoryItem!=null && rollUpTypesRepositoryItem.getPropertyValue(BBBCatalogConstants.ROLLUP_ATTRIBUTE_ROLLUP_PROPERTY_NAME)!=null){
							String rollUpAttribute=(String) rollUpTypesRepositoryItem.getPropertyValue(BBBCatalogConstants.ROLLUP_ATTRIBUTE_ROLLUP_PROPERTY_NAME);
							 Map<String, List<RollupTypeVO>> rollup=  this.getRollUpAttributeForProduct(rollUpAttribute,skuRepositoryItems,true);
							 if(rollup.get("SIZE")!= null ){
								 for(int i=0;i<rollup.get("SIZE").size();i++)
								 {
									String rollUpAttributeValue = StringEscapeUtils.unescapeHtml(rollup.get("SIZE").get(i).getRollupAttribute());
									lengthOfRollUpAttr = lengthOfRollUpAttr + rollUpAttributeValue.length() + lengthOfSwatch ;
									  
								 }
								 if(lengthOfRollUpAttr > lengthOfSizeSwatch){
		                            	productVO.setDisplaySizeAsSwatch(false);
		            				}
		                            else{
		                            	productVO.setDisplaySizeAsSwatch(true);
		                            }
	                            }
							// Flag Added to check Product/SKU Active item for SiteMap Changes
							productVO.setRollupAttributes(rollup);
						}
					}
				}

			Map<String,RepositoryItem> attributeNameRepoItemMap=new HashMap<String,RepositoryItem> ();
			attributeNameRepoItemMap = this.getProductAttributeList(productRepositoryItem, siteId, attributeNameRepoItemMap);
			//sku attribute map in ProductVO
			Map<String, List<AttributeVO>> prodAttributesMap = getProdAttributeMap(attributeNameRepoItemMap);
			if(prodAttributesMap!=null){
				productVO.setAttributesList(prodAttributesMap);
			}
			productVO=updateProductBasicDetails(productVO,productRepositoryItem,false);
			String shipMsgDisplayFlag = BBBCoreConstants.FALSE;
			try {
				shipMsgDisplayFlag = getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG).get(0);
			} catch (BBBSystemException | BBBBusinessException e) {
				logError("Error while getting config key ShipMsgDisplayFlag value", e);
			}
			if(Boolean.parseBoolean(shipMsgDisplayFlag)){
				 updateShippingMessageFlag(productVO);
			}
			logTrace(productVO.toString());
			return productVO;
		}
			return null;
	}
	// End : R 2.2 Product Image SiteMap Generation 504-b 
	
	/**
	 * Update price description.
	 *
	 * @param productVO the product vo
	 * @return the product vo
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	private ProductVO updatePriceDescription(final ProductVO productVO)
	             throws BBBBusinessException, BBBSystemException {
		 String priceRangeDescrip = productVO.getPriceRangeDescriptionRepository();
		 this.logTrace("PriceRangeDescription BEFORE REPLACE for product : " + productVO.getProductId() + " is : "
		                 + priceRangeDescrip);
		
			 final boolean setSalePriceDescrip = this.isPopulateSalePriceDescrip(productVO);
			 // If sale price needs to be calculated first initialize the variable
			 if (setSalePriceDescrip) {
			     productVO.setSalePriceRangeDescription(productVO.getPriceRangeDescriptionRepository().replace('$', ' ').trim());
			     productVO.setSalePriceRangeDescriptionRepository(productVO.getPriceRangeDescriptionRepository().replace('$', ' ').trim());
			 }
		
			 if (!StringUtils.isEmpty(priceRangeDescrip)) {
			     priceRangeDescrip = priceRangeDescrip.replace('$', ' ').trim();
				 if (priceRangeDescrip.contains("%L") || priceRangeDescrip.contains("%l")) {
					 priceRangeDescrip = priceRangeDescrip.replace("%l", "%L");
					 productVO.setPriceRangeDescription(priceRangeDescrip);
					 productVO.setPriceRangeDescriptionRepository(priceRangeDescrip);
					 priceRangeDescrip = this
					                 .updatePriceRangeDescription(productVO, "lowPrice", setSalePriceDescrip);
				 }
				 if ((priceRangeDescrip.contains("%H") || priceRangeDescrip.contains("%h"))) {
					 priceRangeDescrip = priceRangeDescrip.replace("%h", "%H");
					 productVO.setPriceRangeDescription(priceRangeDescrip);
					 productVO.setPriceRangeDescriptionRepository(priceRangeDescrip);
					 priceRangeDescrip = this.updatePriceRangeDescription(productVO, "highPrice",
					                         setSalePriceDescrip);
				 }
		
			 }
			 
			 this.logTrace("PriceRangeDescription AFTER REPLACE for product : " + productVO.getProductId() + " is : "
					 + productVO.getPriceRangeDescriptionRepository() + " SalePriceRangeDescription AFTER REPLACE : "
					         + productVO.getPriceRangeDescriptionRepository());
			
		 return productVO;
	}


    /**
     *  The method returns the prce range description by replacing %L by price of sku in skuLowPrice property of product
     * and %H by price of sku in skuHighPrice property of product.
     *
     * @param productVO the product vo
     * @param type the type
     * @param setSalePriceDescrip the set sale price descrip
     * @return the string
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    private String updatePriceRangeDescription(final ProductVO productVO, final String type,final boolean setSalePriceDescrip) 
    		throws BBBBusinessException, BBBSystemException {
        String replaceString = null;
        final String productId = productVO.getProductId();
        String skuId;
        String priceRangeDescrip = productVO.getPriceRangeDescriptionRepository();
        String saleRangeDescrip = productVO.getSalePriceRangeDescriptionRepository();
        Double listPrice = new Double("0.00");
        if ("lowPrice".equalsIgnoreCase(type)) {
            replaceString = "%L";
            skuId = productVO.getSkuLowPrice();
            if (!StringUtils.isEmpty(skuId)) {
                listPrice = this.getListPrice(productId, skuId.trim());
            }
            productVO.setLowPrice(listPrice);
        } else {
            replaceString = "%H";
            skuId = productVO.getSkuHighPrice();
            if (!StringUtils.isEmpty(skuId)) {
                listPrice = this.getListPrice(productId, skuId.trim());
            }
            productVO.setHighPrice(listPrice);
        }
        

        this.logTrace("Replacing " + replaceString + " with " + listPrice + " for " + productId);
        final String formattedPrice = this.formatCurrency(listPrice);
        boolean isListPriceTBD = false;
		if (listPrice <= 0.01) {
			productVO.setWarrantyPriceCheck(false);
			priceRangeDescrip = getLblTxtTemplateManager().getPageLabel(
					BBBCoreConstants.PRICE_IS_TBD, null, null,
					SiteContextManager.getCurrentSiteId());
			isListPriceTBD = true;
			productVO.setLowPrice(0.00);
			productVO.setHighPrice(0.00);
		} else {
			updateWarrantyPriceCheck(productVO, listPrice);
			this.logTrace(productId + " :product id FormattedPrice "
					+ formattedPrice);
			priceRangeDescrip = priceRangeDescrip.replace(replaceString,
					formattedPrice);
		}
        productVO.setPriceRangeDescription(priceRangeDescrip);
        productVO.setPriceRangeDescriptionRepository(priceRangeDescrip);
        this.logTrace(productId + " :product id Replaced String : " + productVO.getPriceRangeDescriptionRepository());
        if (setSalePriceDescrip) {
            Double salePrice = new Double("0.00");
            if (!StringUtils.isEmpty(skuId)) {
                salePrice = this.getSalePrice(productId, skuId.trim());
            }
            if ((salePrice.doubleValue() > 0)) {
            	if(!StringUtils.isBlank(replaceString) && replaceString.equals("%L")){
            		productVO.setLowPrice(salePrice);
            	} else if(!StringUtils.isBlank(replaceString) && replaceString.equals("%H")){
            		productVO.setHighPrice(salePrice);
            	}
                final String salePriceFormatted = this.formatCurrency(salePrice);
                updateWarrantyPriceCheck(productVO, salePrice);
                if(salePrice <= 0.01 && isListPriceTBD){
                	saleRangeDescrip = getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.PRICE_IS_TBD, null,null,SiteContextManager.getCurrentSiteId());
                } else {
                	saleRangeDescrip = saleRangeDescrip.replace(replaceString, salePriceFormatted);
                }
                
            } else {
                saleRangeDescrip = saleRangeDescrip.replace(replaceString, formattedPrice);
            }
            
            productVO.setSalePriceRangeDescription(saleRangeDescrip);
            productVO.setSalePriceRangeDescriptionRepository(saleRangeDescrip);
        }
        
        return priceRangeDescrip;
    }

	/**
	 * Update warranty price check.
	 *
	 * @param productVO the product vo
	 * @param price the price
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	private void updateWarrantyPriceCheck(final ProductVO productVO,
			final Double price) throws BBBSystemException,
			BBBBusinessException {
		final Double warrantyPrice = Double.parseDouble(this.getAllValuesForKey(
				"ContentCatalogKeys", "WarrantyPrice").get(0));
		if (price > warrantyPrice) {
			productVO.setWarrantyPriceCheck(true);
		} else {
			productVO.setWarrantyPriceCheck(false);
		}

	}

	/**
	 * Checks if is populate sale price descrip.
	 *
	 * @param productVO the product vo
	 * @return true, if is populate sale price descrip
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	private boolean isPopulateSalePriceDescrip(final ProductVO productVO)
                    throws BBBBusinessException, BBBSystemException {

        final String skuLowPrice = productVO.getSkuLowPrice();
        final String skuHighPrice = productVO.getSkuHighPrice();
        final String productId = productVO.getProductId();
        this.logTrace("isPopulateSalePriceDescrip method skuLowPrice: " + skuLowPrice + " skuHighPrice " + skuHighPrice);
        if (!StringUtils.isEmpty(skuLowPrice) && this.isSkuOnSale(productId, skuLowPrice)) {
            return true;
        } else if (!StringUtils.isEmpty(skuHighPrice) && this.isSkuOnSale(productId, skuHighPrice)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Format currency.
     *
     * @param listPrice the list price
     * @return the string
     */
    private String formatCurrency(final Object listPrice) {
    	try {
    		Properties prop = new Properties();
			return  TagConverterManager.getTagConverterByName("currency").convertObjectToString(ServletUtil.getCurrentRequest(),listPrice, prop).toString();
		} catch (TagConversionException e) {
	       	logError("BBBCatalogToolsImpl::: could not find the property, returning NULL"+e.getMessage(),e);
    }
		return (String)listPrice;
    }

    /**
     *  The method gets the List price of the sku.
     *
     * @param productId the product id
     * @param skuId the sku id
     * @return the list price
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public Double getListPrice(final String productId, final String skuId) throws BBBSystemException {
        Double listPrice = null;
        final RepositoryItem profile = ServletUtil.getCurrentUserProfile();
        try {
            final RepositoryItem priceList = this.getPriceListManager().getPriceList(profile,
                            BBBCatalogConstants.PRICE_LIST_ITEM_DESCRIPTOR);
            if (priceList != null) {
                final RepositoryItem price = this.getPriceListManager().getPrice(priceList, productId, skuId);
                if (price != null) {
                    listPrice = (Double) price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME);
                }
            }
        } catch (final PriceListException e) {

            throw new BBBSystemException(BBBCoreErrorConstants.CATALOG_ERROR_1011,
                            "PriceListException while retrieving List Price " + e);
        }
        this.logTrace("Listprice of SKU : " + skuId + " is : " + listPrice);
        if (listPrice == null) {
            listPrice = new Double("0.00");
        }
        return listPrice;
    }

    /**
     *  The method gets the sale price of the sku.
     *
     * @param productId the product id
     * @param skuId the sku id
     * @return the sale price
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public Double getSalePrice(final String productId, final String skuId) throws BBBSystemException {
        Double salePrice = new Double("0.00");
        final RepositoryItem profile = ServletUtil.getCurrentUserProfile();
        try {
            final RepositoryItem priceList = this.getPriceListManager().getPriceList(profile,
                            BBBCatalogConstants.SALE_PRICE_LIST_PRICING_PROPERTY_NAME);
            if (priceList != null) {
                final RepositoryItem price = this.getPriceListManager().getPrice(priceList, productId, skuId);
                if (price != null) {
                    salePrice = (Double) price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME);
                }
            }
        } catch (final PriceListException e) {

            throw new BBBSystemException(BBBCoreErrorConstants.CATALOG_ERROR_1011,
                            "PriceListException while retrieving List Price " + e);
        } catch (final RemovedItemException removedItemException) {
            throw new BBBSystemException(BBBCoreErrorConstants.CATALOG_ERROR_1011,
                            "Attempt to use an item which has been removed - productId : " + productId + " skuId : "
                                            + skuId + removedItemException);
        }
        this.logTrace("Saleprice of SKU : " + skuId + " is : " + salePrice);
        return salePrice;
    }

    /**
     *  The method checks if the skuId is on sale or not.
     *
     * @param productId the product id
     * @param skuId the sku id
     * @return SKU Sale
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public boolean isSkuOnSale(final String productId, final String skuId)
                    throws BBBBusinessException, BBBSystemException {
        boolean isOnSale = false;
        try {
            this.logTrace("skuId to get skuOnsale: " + skuId);
            final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId.trim(),
                            BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
            if (skuRepositoryItem == null) {
                this.logDebug("Repository Item is null for skuId ");
                throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
            }
            final Double salePrice = this.getSalePrice(productId, skuId);
            if ((salePrice != null) && (salePrice.doubleValue() > 0)) {
                isOnSale = true;
            }
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [isSkuOnSale]: RepositoryException ");
            throw new BBBSystemException(BBBCoreErrorConstants.CATALOG_ERROR_1012,
                            "Repository Exception while getting OnSale Property for SKU : " + skuId + " : " + e);
        }
        this.logTrace("SKU : " + skuId + " is onSale : ? " + isOnSale);
        return isOnSale;
    }
    
    /**
     *  The method checks whether sku is on sale. If yes, then it returns
     * the sale Price else List Price
     *
     * @param productId the product id
     * @param skuId the sku id
     * @param refNum the ref num
     * @param pPrice the price
     * @return the effective price
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException BBBSystemException
     */
   public final Double getEffectivePrice(final String productId, final String skuId, final String refNum, final double pPrice) throws BBBSystemException, BBBBusinessException {
	     Double price = null;
	     Double listPrice = null;
	     Double salePrice = null;
	        try {
	        final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId.trim(),
	                BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
	        if (skuRepositoryItem == null) {
	        this.logDebug("Repository Item is null for skuId ");
	        throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
	                    BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
	          }
	        String personalizationType = (String)skuRepositoryItem.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE);
	        //boolean onSale = (Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.ON_SALE);
	        boolean onSale=isSkuOnSale(productId,skuId);
	  		salePrice = this.getSalePrice(productId, skuId);
	  		listPrice = this.getListPrice(productId, skuId);
	  		
	  		price = BBBUtility.checkCurrentPriceForSavedItem(onSale, listPrice, salePrice, personalizationType, pPrice, refNum);
	  		
	        } catch (final RepositoryException e) {
	            this.logError("Catalog API Method Name [isSkuOnSale]: RepositoryException ");
	            throw new BBBSystemException(BBBCoreErrorConstants.CATALOG_ERROR_1012,
	                            "Repository Exception while getting OnSale Property for SKU : " + skuId + " : " + e);
	        }
	       return price;
    }

    /**
     *  The method checks whether sku is on sale. If yes, then it returns
     * the sale Price else List Price
     *
     * @param productId the product id
     * @param skuId the sku id
     * @return the effective price
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException BBBSystemException
     */
   public final Double getEffectivePrice(final String productId, final String skuId) throws BBBSystemException, BBBBusinessException {
        Double price = null;
        try {
        final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId.trim(),
                BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
        if (skuRepositoryItem == null) {
        this.logDebug("Repository Item is null for skuId ");
        throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
                    BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
          }
        boolean onSale = (Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.ON_SALE);
  	    if(onSale){
  		price = this.getSalePrice(productId, skuId);

  	  }else{
  		price = this.getListPrice(productId, skuId);
  	  }
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [isSkuOnSale]: RepositoryException ");
            throw new BBBSystemException(BBBCoreErrorConstants.CATALOG_ERROR_1012,
                            "Repository Exception while getting OnSale Property for SKU : " + skuId + " : " + e);
        }
       
        return price;
    }

    /**
     *  The method sets the product Tabs for teh product Teh method checks if the list of product tabs associaed.
     *  With the product are also associated with the current site.Only then the tab is added to the list of applicable tabs
     *
     * @param productRepositoryItem the product repository item
     * @param siteId the site id
     * @param pProductVO the product vo
     * @return list of Tabs details in the TabVO
     */

	protected ProductVO updateProductTabs(final RepositoryItem productRepositoryItem, final String siteId, ProductVO pProductVO) {
		if (productRepositoryItem != null) {
			final List<TabVO> productTabsList = new ArrayList<TabVO>();
			@SuppressWarnings ("unchecked")
			final List<RepositoryItem> productTabsRepositoryItemsList = (List<RepositoryItem>) productRepositoryItem
			.getPropertyValue(BBBCatalogConstants.PRODUCT_TABS_PRODUCT_PROPERTY_NAME);
			this.logTrace("productTabs RepositoryItems List for product " + productTabsRepositoryItemsList);
			if (!BBBUtility.isListEmpty(productTabsRepositoryItemsList)) {
				this.logTrace("No of productTabs applicable for the product " + productTabsRepositoryItemsList.size());
				
				// Fetch the Config key for PDP Hamron Tab Name for Comparison Purposes.
				String harmonTabName = null;
				try {
					final List<String> configValue = this.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.PDP_TAB_NAME_HARMON);
					if (!BBBUtility.isListEmpty(configValue)) {
						harmonTabName = configValue.get(0);
					}
				} catch (final BBBSystemException e) {
					this.logError(KEY_NOT_FOUND_FOR_PDP_HARMON_TAB_NAME + e);
				} catch (final BBBBusinessException e) {
					this.logError(KEY_NOT_FOUND_FOR_PDP_HARMON_TAB_NAME + e);
				}
				
				for (int index = 0; index < productTabsRepositoryItemsList.size(); index++) {
					final RepositoryItem tabRepositoryItem = productTabsRepositoryItemsList.get(index);
					@SuppressWarnings ("unchecked")
					final Set<RepositoryItem> sitesSet = (Set<RepositoryItem>) tabRepositoryItem
					.getPropertyValue(BBBCatalogConstants.SITES_TABS_PROPERTY_NAME);
					
					// Fetch Tab Name & Tab Content
					final String tabName = (String)tabRepositoryItem.getPropertyValue(BBBCatalogConstants.TAB_NAME_TABS_PROPERTY_NAME);
					final String tabContent = (String)tabRepositoryItem.getPropertyValue(BBBCatalogConstants.TAB_CONTENT_TABS_PROPERTY_NAME);
					
					if ((sitesSet != null) && !sitesSet.isEmpty()) {
						for (final RepositoryItem site : sitesSet) {
							final String tabsSiteId = site.getRepositoryId();
							this.logTrace("site id from tab item desc [" + tabsSiteId + "] site id from input ["
									+ siteId + "]");
							if (tabsSiteId.equalsIgnoreCase(siteId)) {
								// Check if Tab name is that of Harmon & tab content is non empty. If yes, set Harmon long Description Property else add this Tab to Tab List.
								if(BBBUtility.isNotEmpty(tabName) && BBBUtility.isNotEmpty(harmonTabName) && BBBUtility.isNotEmpty(tabContent)
										&& tabName.trim().toLowerCase().equalsIgnoreCase(harmonTabName.trim().toLowerCase())){
									this.logTrace("Setting Harmon Long Description for this Product");
									pProductVO.setHarmonLongDescription(tabContent);
								}
								else{
									productTabsList.add(new TabVO(tabRepositoryItem));
									this.logTrace("Non Harmon Tab Details added to Tab List");
								}
							}
						}
					} else {
						this.logTrace("No sites associated with the Tabs repository item");
					}
				}
			}
			else{
				this.logTrace("No Tabs associated with the product");
			}
			pProductVO.setProductTabs(productTabsList);
			return pProductVO;
		}
		this.logDebug("Product Repository Item is null..");
		return pProductVO;
	}

    /**
     *  The method gets the free ship methods that are applicable for a sku From the list of all applicable attributes
     * the method checks if there are any attribute id for free ship attrbute (present in freeShipAttrShipGrpMap) if yes
     * then that shipping method is added in the VO.
     *
     * @param applicableAttrIds the applicable attr ids
     * @return list of shipping details in the ShipMethodVO
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    private List<ShipMethodVO> updateFreeShipMethodForSku(final Set<String> applicableAttrIds)
                    throws BBBBusinessException, BBBSystemException {

        final List<ShipMethodVO> freeShipMethodList = new ArrayList<ShipMethodVO>();
        this.logDebug("Catalog API Method Name [updateFreeShipMethodForSku] ");
        if ((this.freeShipAttrShipGrpMap != null) && !this.freeShipAttrShipGrpMap.isEmpty()) {
            for (final String freeShipAttrid : this.freeShipAttrShipGrpMap.keySet()) {
                this.logDebug("Attribute Id from the list of Applicable  attributes for the sku " + freeShipAttrid);
                if (applicableAttrIds.contains(freeShipAttrid)) {
                    final String freeShipMethod = this.freeShipAttrShipGrpMap.get(freeShipAttrid).toLowerCase();
                    this.logDebug("Shipping method id [" + freeShipMethod + " is  free ");
                    final RepositoryItem shipItem = this.getShippingMethod(freeShipMethod);
                    final ShipMethodVO shipMethodVO = new ShipMethodVO(shipItem);
                    this.logDebug("Adding ship method to free ship List " + shipMethodVO.toString());
                    freeShipMethodList.add(shipMethodVO);
                }
            }
        }

        return freeShipMethodList;
    }

    /**
     *  The method sets the rebate details for the sku It checks if the site associated with the rebate is same as the
     * current site Also if the current date is within start and end date of the rebate.
     *
     * @param skuRepositoryItem the sku repository item
     * @param siteId the site id
     * @return list of rebate details in the RebateVO
     */
    private List<RebateVO> updateRebatesForSku(final RepositoryItem skuRepositoryItem, final String siteId) {
        // Set Rebates
        final List<RebateVO> eligibleRebates = new ArrayList<RebateVO>();
        Date previewDate = new Date();
        if (this.isPreviewEnabled()) {
            previewDate = this.getPreviewDate();
        }
        @SuppressWarnings ("unchecked")
        final Set<RepositoryItem> rebatesRepositoryItem = (Set<RepositoryItem>) skuRepositoryItem
                        .getPropertyValue(BBBCatalogConstants.REBATES_ITEM_DESCRIPTOR);
        if ((rebatesRepositoryItem != null) && !rebatesRepositoryItem.isEmpty()) {
            for (final RepositoryItem rebateRepositoryItem : rebatesRepositoryItem) {
                @SuppressWarnings ("unchecked")
                final Set<RepositoryItem> sitesSet = (Set<RepositoryItem>) rebateRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.SITES_REBATE_PROPERTY_NAME);
                final Date rebateStartDate = (Date) rebateRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.START_DATE_REBATE_PROPERTY_NAME);
                final Date rebateEndDate = (Date) rebateRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.END_DATE_REBATE_PROPERTY_NAME);
                for (final RepositoryItem siteRepoItem : sitesSet) {
                    this.logDebug("siteIdFromSiteRepository  value[" + siteRepoItem.getRepositoryId()
                                    + "] site id from input [" + siteId + "]");
                    if (siteRepoItem.getRepositoryId().equalsIgnoreCase(siteId)) {
                        this.logDebug("add rebate [" + rebateRepositoryItem.getRepositoryId() + "]" + " for skuid ["
                                        + skuRepositoryItem.getRepositoryId() + "]");
                        if (((rebateEndDate != null) && previewDate.after(rebateEndDate))
                                        || ((rebateStartDate != null) && previewDate.before(rebateStartDate))) {
                            this.logDebug("Rebate id " + rebateRepositoryItem.getRepositoryId()
                                            + " is not active so not including it in the rebates list");
                        } else {
                            this.logDebug("Rebate id " + rebateRepositoryItem.getRepositoryId()
                                            + " is  active so  including it in the rebates list");
                            eligibleRebates.add(new RebateVO(rebateRepositoryItem));
                        }
                    }
                }
            }
        } else {
            this.logDebug("rebates Repository Item set is null or empty");
        }
        return eligibleRebates;
    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#executeRQLQuery(java.lang.String, java.lang.Object[], java.lang.String, atg.repository.MutableRepository)
     */
    @Override
	public RepositoryItem[] executeRQLQuery(final String rqlQuery, final Object[] params, final String viewName,
                    final MutableRepository repository) throws BBBSystemException {
        RqlStatement statement;
        RepositoryItem[] queryResult = null;
        if (rqlQuery != null) {
            if (repository != null) {
                try {
                    statement = parseRqlStatement(rqlQuery);
                    final RepositoryView view = repository.getView(viewName);
                    if ((view == null) && this.isLoggingError()) {
                        this.logError("catalog_1019 : View " + viewName + " is null");
                    }

                    queryResult = statement.executeQuery(view, params);
                    if (queryResult == null) {

                        this.logDebug("No results returned for query [" + rqlQuery + "]");

                    }

                } catch (final RepositoryException e) {
                    if (this.isLoggingError()) {
                        this.logError("catalog_1020 : Unable to retrieve data");
                    }

                    throw new BBBSystemException(
                                    BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                    BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
                }
            } else {
                if (this.isLoggingError()) {
                    this.logError("catalog_1021 : Repository has no data");
                }
            }
        } else {
            if (this.isLoggingError()) {
                this.logError("catalog_1022 : Query String is null");
            }
        }

        return queryResult;
    }

    /**
     * Gets the category vo.
     *
     * @param categoryRepositoryItem the category repository item
     * @param subcategoryVOList the subcategory vo list
     * @param childProductIdList the child product id list
     * @return the category vo
     */
    private CategoryVO getCategoryVO(final RepositoryItem categoryRepositoryItem,
                    final List<CategoryVO> subcategoryVOList, final List<String> childProductIdList) {
        this.logDebug("Catalog API Method Name [isSKUAvailable]Parameter categoryRepositoryItem["
                        + categoryRepositoryItem + "]" + "Parameter subcategoryVOList[" + subcategoryVOList
                        + "] childProductIdList" + childProductIdList + "]");
        final CategoryVO categoryVO = new CategoryVO();
        categoryVO.setCategoryId(categoryRepositoryItem.getRepositoryId());
        if (categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME) != null) {
            categoryVO.setIsCollege((Boolean) categoryRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME));
        }
        if (categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME) != null) {
            categoryVO.setCategoryName((String) categoryRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME));
        }
        final String categoryImage = (String) categoryRepositoryItem
                        .getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_CATEGORY_PROPERTY_NAME);
        if (categoryImage != null) {
            categoryVO.setCategoryImage(categoryImage);
        }
        if (categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.NODE_TYPE_CATEGORY_PROPERTY_NAME) != null) {
            categoryVO.setCategoryDisplayType((String) categoryRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.NODE_TYPE_CATEGORY_PROPERTY_NAME));
        }
        if (categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY) != null) {
            categoryVO.setPhantomCategory((Boolean) categoryRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY));
        }
        if (!BBBUtility.isListEmpty(subcategoryVOList)) {
            categoryVO.setSubCategories(subcategoryVOList);

        } else if (!BBBUtility.isListEmpty(childProductIdList)) {
            categoryVO.setChildProducts(childProductIdList);
        }
        //this.logTrace(categoryVO.toString());

        // generate category SEO URL
        final String seoURL = this.getCategorySeoLinkGenerator().formatUrl(categoryVO.getCategoryId(),
                        (String) categoryRepositoryItem.getPropertyValue("displayName"));

        categoryVO.setSeoURL(seoURL);

        return categoryVO;

    }

    /**
     *  Part of Release 2.1 implementation This method gets the configered values for the chat link.
     *  and sets the values in the CategoryVO
     *
     * @param pCategoryVO the category vo
     * @return the bcc managed category
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public final void getBccManagedCategory(final CategoryVO pCategoryVO)
            throws BBBSystemException, BBBBusinessException {
		this.logDebug("Catalog API Method Name [getBccManagedCategory]Parameter CategoryVO[" + pCategoryVO + "]");
			if (pCategoryVO != null) {
				final List<SortOptionsVO> sortOptionList = new ArrayList<SortOptionsVO>();
				DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
				String channel = null;
				if(pRequest!=null && pRequest.getHeader(BBBCoreConstants.CHANNEL)!=null){
					channel=pRequest.getHeader(BBBCoreConstants.CHANNEL);
				}
				String siteId = "";
				if (null != pRequest) {
					siteId = (String) pRequest.getAttribute(BBBCoreConstants.SITE_ID);
				}
				if (BBBUtility.isEmpty(siteId)){
					siteId = getCurrentSiteId();
				}
				if (BBBUtility.isEmpty(channel)) {
					channel = BBBCoreConstants.DEFAULT_CHANNEL_VALUE;
				}
					
				final SortOptionVO sortOptions = new SortOptionVO();
				SortOptionsVO sortOption = new SortOptionsVO();
				final Object[] params = new Object[2];
				params[0] = pCategoryVO.getCategoryId();
				try {
					BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getBccManagedCategory");
					final RepositoryItem[] bbbManagedCategoryDetails = this.executeRQLQuery("categoryId=?0", params,
							BBBCatalogConstants.BCC_MANAGED_CATEGORY, 
							(MutableRepository) getBbbManagedCatalogRepository());
					final RepositoryItem siteItem = this.getSiteRepository()
							.getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
					if (siteItem != null) {
						final Map<String, RepositoryItem> defSiteSortMap = (Map<String, RepositoryItem>)
								siteItem.getPropertyValue(BBBCatalogConstants.DEFSITESORTOPTION);
						final Map<String, List<RepositoryItem>> siteSortOptMap = (Map<String, List<RepositoryItem>>)
								siteItem.getPropertyValue(BBBCatalogConstants.SITESORTOPTIONLIST);
						RepositoryItem defSiteSortOpt = null;
						RepositoryItem siteSortOptItem = null;
						List<RepositoryItem> siteSortOptList = new ArrayList<RepositoryItem>();
						if (!BBBUtility.isMapNullOrEmpty(defSiteSortMap)) {
							defSiteSortOpt = (RepositoryItem) defSiteSortMap.get(BBBCoreConstants.DEFAULT_CHANNEL_VALUE);
			                if (defSiteSortMap.keySet().contains(channel)) {
								defSiteSortOpt = (RepositoryItem) defSiteSortMap.get(channel);
							}
						} else {
							logError("Catalog API Method Name[getBccManagedCategory]"
									+ ":Default Site Sort Option Map Null");					
						}
						if (!BBBUtility.isMapNullOrEmpty(siteSortOptMap)) {
							siteSortOptItem = (RepositoryItem) siteSortOptMap.get(BBBCoreConstants.DEFAULT_CHANNEL_VALUE);
							if (siteSortOptMap.keySet().contains(channel)) {
								siteSortOptItem = (RepositoryItem) siteSortOptMap.get(channel);
							}
							siteSortOptList = (List<RepositoryItem>) siteSortOptItem
									.getPropertyValue(BBBCatalogConstants.SORTINGOPTIONS);
						} else {
							logError("Catalog API Method Name [getBccManagedCategory]"
									+ ":Site Sort Options Map Null");					
						}
						if (bbbManagedCategoryDetails != null && bbbManagedCategoryDetails[0] != null) {
							final RepositoryItem categoryPromoContent = (RepositoryItem) bbbManagedCategoryDetails[0]
									.getPropertyValue(BBBCatalogConstants.CATEGORY_PROMO_ID);
							if (null != categoryPromoContent && null != categoryPromoContent.getPropertyValue(BBBCatalogConstants.PROMO_CONTENT)) {
								pCategoryVO.setBannerContent(categoryPromoContent.getPropertyValue(BBBCatalogConstants.PROMO_CONTENT).toString());
								
								if(null != categoryPromoContent.getPropertyValue(BBBCatalogConstants.CSS_FILE_PATH))
									pCategoryVO.setCssFilePath(categoryPromoContent.getPropertyValue(BBBCatalogConstants.CSS_FILE_PATH).toString());
								if(null != categoryPromoContent.getPropertyValue(BBBCatalogConstants.JS_FILE_PATH))
									pCategoryVO.setJsFilePath(categoryPromoContent.getPropertyValue(BBBCatalogConstants.JS_FILE_PATH).toString());
								
								logTrace("Category Promo Content \n:"
										+ pCategoryVO.getBannerContent()
										+ "\n CSS File Path: "
										+ pCategoryVO.getCssFilePath()
										+ "\n JS File Path: "
										+ pCategoryVO.getJsFilePath());
							} else {
								logTrace("Category Banner Content is not available for category id: "
										+ pCategoryVO.getCategoryId());
							}
							
							if(null != bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.BCC_MANAGED_PROMO_TYPE) && 
									BBBCatalogConstants.BCC_MANAGED_PROMO_CATEGORY.equalsIgnoreCase((String) bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.BCC_MANAGED_PROMO_TYPE))){
								
								BCCManagedPromoCategoryVO bccManagedPromoCategoryVO = new BCCManagedPromoCategoryVO();
								
								bccManagedPromoCategoryVO.setShowColorSwatches((boolean) bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.ITEM_PROP_SHOW_COLOR_SWATCHES));
								bccManagedPromoCategoryVO.setShowFilters((boolean) bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.ITEM_PROP_SHOW_FILTERS));
								bccManagedPromoCategoryVO.setShowGlobalAttributes((boolean) bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.ITEM_PROP_SHOW_GLOBAL_ATTRIBUTES));
								bccManagedPromoCategoryVO.setShowPrice((boolean) bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.ITEM_PROP_SHOW_PRICE));
								bccManagedPromoCategoryVO.setShowProductCompare((boolean) bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.ITEM_PROP_SHOW_PRODUCT_COMPARE));
								bccManagedPromoCategoryVO.setShowQuickViewLink((boolean) bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.ITEM_PROP_SHOW_QUICK_VIEW));
								bccManagedPromoCategoryVO.setShowReviewRatings((boolean) bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.ITEM_PROP_SHOW_REVIEW_RATINGS));
								bccManagedPromoCategoryVO.setShowTitle((boolean) bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.ITEM_PROP_SHOW_TITLE));
								bccManagedPromoCategoryVO.setProductAttributes((List<String>)bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.PRODUCTATTRIBUTES));
								pCategoryVO.setBccManagedPromoCategoryVO(bccManagedPromoCategoryVO);
								
								this.populateSiblingCategory(pCategoryVO, siteId);
								this.populateCatShortDescription(pCategoryVO);
							}
							
							if(null != bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.BCC_MANAGED_PROMO_TYPE) && 
									BBBCatalogConstants.BCC_MANAGED_PROMO_CATEGORY.equalsIgnoreCase((String) bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.BCC_MANAGED_PROMO_TYPE))){
								
								BCCManagedPromoCategoryVO bccManagedPromoCategoryVO = new BCCManagedPromoCategoryVO();
								
								bccManagedPromoCategoryVO.setShowColorSwatches((boolean) bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.ITEM_PROP_SHOW_COLOR_SWATCHES));
								bccManagedPromoCategoryVO.setShowFilters((boolean) bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.ITEM_PROP_SHOW_FILTERS));
								bccManagedPromoCategoryVO.setShowGlobalAttributes((boolean) bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.ITEM_PROP_SHOW_GLOBAL_ATTRIBUTES));
								bccManagedPromoCategoryVO.setShowPrice((boolean) bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.ITEM_PROP_SHOW_PRICE));
								bccManagedPromoCategoryVO.setShowProductCompare((boolean) bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.ITEM_PROP_SHOW_PRODUCT_COMPARE));
								bccManagedPromoCategoryVO.setShowQuickViewLink((boolean) bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.ITEM_PROP_SHOW_QUICK_VIEW));
								bccManagedPromoCategoryVO.setShowReviewRatings((boolean) bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.ITEM_PROP_SHOW_REVIEW_RATINGS));
								bccManagedPromoCategoryVO.setShowTitle((boolean) bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.ITEM_PROP_SHOW_TITLE));
								bccManagedPromoCategoryVO.setProductAttributes((List<String>)bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.PRODUCTATTRIBUTES));
								pCategoryVO.setBccManagedPromoCategoryVO(bccManagedPromoCategoryVO);
							}
							
							final Boolean isChatEnable = (Boolean) bbbManagedCategoryDetails[0]
									.getPropertyValue("chatEnabled");
							if (isChatEnable != null && isChatEnable.booleanValue()) {
								pCategoryVO.setChatEnabled(Boolean.TRUE);
								pCategoryVO.setChatURL((String) bbbManagedCategoryDetails[0]
										.getPropertyValue(BBBCatalogConstants.CHAT_URL));
								pCategoryVO.setChatCode((String) bbbManagedCategoryDetails[0]
										.getPropertyValue(BBBCatalogConstants.CHAT_CODE));
								pCategoryVO.setChatLinkPlaceholder((String) siteItem
										.getPropertyValue(BBBCatalogConstants.CHAT_LINK_PLACEHOLDER));
							} else if (isChatEnable != null && !isChatEnable.booleanValue()) {
								pCategoryVO.setChatEnabled(Boolean.FALSE);
							} else {
								pCategoryVO.setChatEnabled(Boolean.FALSE);
							}
							final Map<String, RepositoryItem> defCatSortMap  = (Map<String, RepositoryItem>) 
									bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.DEFCATSORTOPTION);
							final Map<String, List<RepositoryItem>> catSortOptMap = (Map<String, List<RepositoryItem>>) 
									bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.CATSORTOPTIONLIST);
							RepositoryItem defCatSortOpt = null;
							RepositoryItem catSortOptItem = null;
							List<RepositoryItem> catSortOptList = null;
							  if (!BBBUtility.isMapNullOrEmpty(defCatSortMap)) {	
								defCatSortOpt = (RepositoryItem) defCatSortMap.get(BBBCoreConstants.DEFAULT_CHANNEL_VALUE);
				                if (defCatSortMap.keySet().contains(channel)) {
				                	defCatSortOpt = (RepositoryItem) defCatSortMap.get(channel);
								}
				            }
							if (!BBBUtility.isMapNullOrEmpty(catSortOptMap)) {
								catSortOptItem = (RepositoryItem) catSortOptMap
				                		.get(BBBCoreConstants.DEFAULT_CHANNEL_VALUE);
								if (catSortOptMap.keySet().contains(channel)) {
									catSortOptItem = (RepositoryItem) catSortOptMap.get(channel);
								}
								catSortOptList = (List<RepositoryItem>) catSortOptItem
										.getPropertyValue(BBBCatalogConstants.SORTINGOPTIONS);
							}
					        if (defCatSortOpt != null) {
								sortOption.setSortCode((String) defCatSortOpt
										.getPropertyValue(BBBCatalogConstants.SORTINGCODE));
								sortOption.setSortValue((String) defCatSortOpt
										.getPropertyValue(BBBCatalogConstants.SORTINGVALUE));
								sortOption.setAscending((Integer) defCatSortOpt
										.getPropertyValue(BBBCatalogConstants.SORTINGORDER));
								sortOption.setSortUrlParam((String) defCatSortOpt
										.getPropertyValue(BBBCatalogConstants.SORTINGURLPARAM));
								sortOption.setRepositoryId(defCatSortOpt.getRepositoryId());
								
							} else {
								if (defSiteSortOpt != null) {
									sortOption.setSortCode((String) defSiteSortOpt
											.getPropertyValue(BBBCatalogConstants.SORTINGCODE));
									sortOption.setSortValue((String) defSiteSortOpt
											.getPropertyValue(BBBCatalogConstants.SORTINGVALUE));
									sortOption.setAscending((Integer) defSiteSortOpt
											.getPropertyValue(BBBCatalogConstants.SORTINGORDER));
									sortOption.setSortUrlParam((String) defSiteSortOpt
											.getPropertyValue(BBBCatalogConstants.SORTINGURLPARAM));
									sortOption.setRepositoryId(defSiteSortOpt.getRepositoryId());
								}
							}
							//if (catSortOptList != null && !catSortOptList.isEmpty()) {
							if (!BBBUtility.isCollectionEmpty(catSortOptList)) {
								for (RepositoryItem catSortItem :catSortOptList) {
									final SortOptionsVO sortOptionVO = new SortOptionsVO();
									sortOptionVO.setSortCode((String) catSortItem
											.getPropertyValue(BBBCatalogConstants.SORTINGCODE));
									sortOptionVO.setSortValue((String) catSortItem
											.getPropertyValue(BBBCatalogConstants.SORTINGVALUE));
									sortOptionVO.setAscending((Integer) catSortItem
											.getPropertyValue(BBBCatalogConstants.SORTINGORDER));
									sortOptionVO.setSortUrlParam((String) catSortItem
											.getPropertyValue(BBBCatalogConstants.SORTINGURLPARAM));
									sortOptionVO.setRepositoryId(catSortItem.getRepositoryId());
									sortOptionList.add(sortOptionVO);
								}
							} else {
								if (!BBBUtility.isCollectionEmpty(siteSortOptList)) {
									for (RepositoryItem siteSortItem :siteSortOptList) {
										final SortOptionsVO sortOptionVO = new SortOptionsVO();
										sortOptionVO.setSortCode((String) siteSortItem
												.getPropertyValue(BBBCatalogConstants.SORTINGCODE));
										sortOptionVO.setSortValue((String) siteSortItem
												.getPropertyValue(BBBCatalogConstants.SORTINGVALUE));
										sortOptionVO.setAscending((Integer) siteSortItem
												.getPropertyValue(BBBCatalogConstants.SORTINGORDER));
										sortOptionVO.setSortUrlParam((String) siteSortItem
												.getPropertyValue(BBBCatalogConstants.SORTINGURLPARAM));
										sortOptionVO.setRepositoryId(siteSortItem.getRepositoryId());
										sortOptionList.add(sortOptionVO);
									}
								}
							}
							// BV Additional tag #216
							final String displayAskAndAnswer = (String) bbbManagedCategoryDetails[0]
									.getPropertyValue("displayaskandanswer");
							if (displayAskAndAnswer != null) {
							    pCategoryVO.setDisplayAskAndAnswer(displayAskAndAnswer);
							}
							// Additonal for Story 117-A5
		
							final String zoomValue = (String)bbbManagedCategoryDetails[0]
									.getPropertyValue("zoomValue");
							if(zoomValue!=null){
								pCategoryVO.setZoomValue(zoomValue);
							}
								
							
							final String defaultViewValue = (String) bbbManagedCategoryDetails[0]
									.getPropertyValue("defaultViewValue");
							if (defaultViewValue != null) {
								pCategoryVO.setDefaultViewValue(defaultViewValue);
							}
							
						} else {
							//Category is not BCC Managed
							if (!BBBUtility.isCollectionEmpty(siteSortOptList)) {
								for (RepositoryItem siteSortItem :siteSortOptList) {
									final SortOptionsVO sortOptionVO = new SortOptionsVO();
									sortOptionVO.setSortCode((String) siteSortItem
											.getPropertyValue(BBBCatalogConstants.SORTINGCODE));
									sortOptionVO.setSortValue((String) siteSortItem
											.getPropertyValue(BBBCatalogConstants.SORTINGVALUE));
									sortOptionVO.setAscending((Integer) siteSortItem
											.getPropertyValue(BBBCatalogConstants.SORTINGORDER));
									sortOptionVO.setSortUrlParam((String) siteSortItem
											.getPropertyValue(BBBCatalogConstants.SORTINGURLPARAM));
									sortOptionVO.setRepositoryId(siteSortItem.getRepositoryId());
									sortOptionList.add(sortOptionVO);
								}
							}
							if (defSiteSortOpt != null) {
								sortOption.setSortCode((String) defSiteSortOpt
										.getPropertyValue(BBBCatalogConstants.SORTINGCODE));
								sortOption.setSortValue((String) defSiteSortOpt
										.getPropertyValue(BBBCatalogConstants.SORTINGVALUE));
								sortOption.setAscending((Integer) defSiteSortOpt
										.getPropertyValue(BBBCatalogConstants.SORTINGORDER));
								sortOption.setSortUrlParam((String) defSiteSortOpt
										.getPropertyValue(BBBCatalogConstants.SORTINGURLPARAM));
								sortOption.setRepositoryId(defSiteSortOpt.getRepositoryId());
							}
							pCategoryVO.setDisplayAskAndAnswer(BBBCatalogConstants.ASK_ANSWER_DEFAULT_VALUE);
							pCategoryVO.setChatEnabled(Boolean.FALSE);
						}
						boolean ifDefInList = false;
						if (!BBBUtility.isListEmpty(sortOptionList)) {
							if (BBBUtility.isNotEmpty(sortOption.getRepositoryId())) {
								for (SortOptionsVO sortOptionListItem :sortOptionList) {
									
									if (sortOptionListItem.getRepositoryId()
											.equalsIgnoreCase(sortOption.getRepositoryId())) {
										ifDefInList= true;
									}
									if(ifDefInList){
										break;
									}
								}
								if(!ifDefInList && defSiteSortOpt != null){
									final String defSiteSortOptRepId = defSiteSortOpt.getRepositoryId();
									for (SortOptionsVO sortOptionListItem :sortOptionList) {
										
										if (sortOptionListItem.getRepositoryId().equalsIgnoreCase(defSiteSortOptRepId)) {
											sortOption.setSortCode((String) defSiteSortOpt
													.getPropertyValue(BBBCatalogConstants.SORTINGCODE));
											sortOption.setSortValue((String) defSiteSortOpt
													.getPropertyValue(BBBCatalogConstants.SORTINGVALUE));
											sortOption.setAscending((Integer) defSiteSortOpt
													.getPropertyValue(BBBCatalogConstants.SORTINGORDER));
											sortOption.setSortUrlParam((String) defSiteSortOpt
													.getPropertyValue(BBBCatalogConstants.SORTINGURLPARAM));
											sortOption.setRepositoryId(defSiteSortOpt.getRepositoryId());
											ifDefInList= true;
										}
										if(ifDefInList){
											break;
										}
									}
								}
							} else {
								sortOption = sortOptionList.get(0);
							}
							}
										
						sortOptions.setDefaultSortingOption(sortOption);
						sortOptions.setSortingOptions(sortOptionList);
						pCategoryVO.setSortOptionVO(sortOptions);
						pCategoryVO.setChatLinkPlaceholder((String) siteItem
								.getPropertyValue(BBBCatalogConstants.CHAT_LINK_PLACEHOLDER));
					} else {
						logError("Catalog API Method Name [getBccManagedCategory]:Site Item Null");
					}
				} catch (final RepositoryException e) {
					this.logError("Catalog API Method Name [getBccManagedCategory]:RepositoryException", e);
					throw new BBBSystemException("chat_repository_exception", 
							"RepositoryException while retriving Chat configuration data");
				} finally {
		        BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getBccManagedCategory");
		    }
		} else {
		    throw new BBBBusinessException("categoryVO_null", "CategoryVO is NULL");
		}
		this.logDebug("Catalog API Method Name [getBccManagedCategory] ends");
	}
    
    /**
     * This method is created to populate sibling category details for a given category.
     * @param CategoryVO : categoryVO
     * @param String : siteId
     * @throws BBBBusinessException
     * @throws BBBSystemException
     * @throws RepositoryException 
    **/
    private void populateSiblingCategory(CategoryVO categoryVO, String siteId) throws BBBBusinessException, BBBSystemException {
 	   if(null != categoryVO.getBccManagedPromoCategoryVO()){
 		   Map<String, CategoryVO> parentCategoryMap = this.getParentCategory(categoryVO.getCategoryId(), siteId);
 			if(null != parentCategoryMap.get("1")){
 				CategoryVO parentCategoryVO = this.getCategoryDetail(siteId, parentCategoryMap.get("1").getCategoryId(),false);
 				List<CategoryVO> siblingCat = parentCategoryVO.getSubCategories();
 				Iterator<CategoryVO> itr = siblingCat.iterator();
 				while(itr.hasNext()){
 					CategoryVO itrElement = itr.next();
 					if(categoryVO.getCategoryId().equalsIgnoreCase(itrElement.getCategoryId())){
 						itr.remove();
 						break;
 					}
 				}
 				categoryVO.setSiblingCategories(parentCategoryVO.getSubCategories());
 			} 			
 		}
    }   
    
    /**
     * This method is created to populate short description for a category.
     * @param CategoryVO : categoryVO
     * @throws RepositoryException 
    **/
    private void populateCatShortDescription(CategoryVO categoryVO) throws RepositoryException{
    	RepositoryItem categoryRepositoryItem = this.getCatalogRepository().getItem(categoryVO.getCategoryId(),
	              BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
	 	 String descr = (String) categoryRepositoryItem.getPropertyValue("description");
	 	categoryVO.setCatDescription(descr);
    }
	
	/**
	 * This method fetches the L2/L3 exclusion list from BCC.
	 *  
	 *
	 * @param tmpDepth the tmp depth
	 * @param categoryId the category id
	 * @return L3 exclusion list
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	@SuppressWarnings("unchecked")
	public final List<String> getBccManagedCategoryList(int tmpDepth, String categoryId)
			throws BBBSystemException, BBBBusinessException {
		List<String> mFacetList = new ArrayList<String>();
		List<String> facetList = null;
		final Object[] params = new Object[2];
		params[0] = categoryId;
		final RepositoryItem[] bbbManagedCategoryDetails = this
				.executeRQLQuery("categoryId=?0", params,
						BBBCatalogConstants.BCC_MANAGED_CATEGORY,
						(MutableRepository) this
								.getBbbManagedCatalogRepository());
		if (bbbManagedCategoryDetails != null
				&& bbbManagedCategoryDetails[0] != null) {
			if(tmpDepth == 1){
				facetList = (List<String>) bbbManagedCategoryDetails[0]
					.getPropertyValue(BBBCatalogConstants.L2_EXCLUSION);
			}else if(tmpDepth == 2){
				facetList = (List<String>) bbbManagedCategoryDetails[0]
						.getPropertyValue(BBBCatalogConstants.L3_EXCLUSION);
			}
			if (null != facetList) {
				mFacetList = facetList;
			}
		}
		return mFacetList;
	}

    
	/**
	 * Gets the bcc managed brand.
	 *
	 * @param brandName the brand name
	 * @param channel the channel
	 * @return the bcc managed brand
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public BrandVO getBccManagedBrand(String brandName, String channel) throws BBBSystemException, BBBBusinessException {
		
		return getSiteRepositoryTools().getBccManagedBrand(brandName, channel);
	}
	
	
	/**
	 *  Part of Release 2.2 79-C story implementation. This method takes categoryId as input and fetches
	 * corresponding US or Canada URL and Category ID from BBBManagedCatalogRepository and sets them in
	 * categoryMappingVO. 
	 *
	 * @param categoryId the category id
	 * @return the US canada category mapping
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException returns categoryMappingVO
	 */
	@SuppressWarnings("unchecked")
	public CategoryMappingVo getUSCanadaCategoryMapping(String categoryId) throws BBBSystemException, BBBBusinessException {
		CategoryMappingVo categoryMappingVO=new CategoryMappingVo();
		this.logDebug("Start: Catalog API Method Name [getUSCanadaCategoryMapping]");
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL+" getUSCanadaCategoryMapping");
		final String siteId = SiteContextManager.getCurrentSiteId();
		Object[] param = new Object[1];
		param[0]=categoryId;
		this.logTrace("Category Id in getUSCanadaCategoryMapping api of BBBCatalogToolsImpl for site id : " + siteId + " is : " + categoryId);
		if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)){
			RepositoryItem[] bccManagedBrandRepositoryItem=this.executeRQLQuery(getUsCategoryQuery(),param, BBBCatalogConstants.BCC_MANAGED_US_CANADA_CAT_MAP_ITEM_DESCRIPTOR, (MutableRepository) getBbbManagedCatalogRepository());
			if(bccManagedBrandRepositoryItem!=null && bccManagedBrandRepositoryItem.length !=0){
				final Map<String, String> canadaUrlMap=(Map<String,String>) bccManagedBrandRepositoryItem[0].getPropertyValue(BBBCatalogConstants.CANADA_URL);
				String canadaUrl=canadaUrlMap.get(BBBCoreConstants.DEFAULT_CHANNEL_VALUE);
				RepositoryItem canadaCatIdRepo=(RepositoryItem) bccManagedBrandRepositoryItem[0].getPropertyValue(BBBCatalogConstants.CANADA_CATEGORY_ID);
				String canadaCatId = null;
				if(canadaCatIdRepo != null && canadaCatIdRepo.getPropertyValue(BBBCoreConstants.ID) != null){
					canadaCatId=(String)canadaCatIdRepo.getPropertyValue(BBBCoreConstants.ID);
					categoryMappingVO.setCanadaCategoryId(canadaCatId);
				}
				this.logTrace("Canada URL in getUSCanadaCategoryMapping api of BBBCatalogToolsImpl for site id : " + siteId + " is : "+canadaUrl);
				this.logTrace("Canada Category Id in getUSCanadaCategoryMapping api of BBBCatalogToolsImpl for site id : " + siteId + " is : "+canadaCatId);
				categoryMappingVO.setUsCategoryId(categoryId);
				categoryMappingVO.setCanadaUrl(canadaUrl);
			}
		} else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
			RepositoryItem[] bccManagedBrandRepositoryItem=this.executeRQLQuery(getCanadaCategoryQuery(),param, BBBCatalogConstants.BCC_MANAGED_US_CANADA_CAT_MAP_ITEM_DESCRIPTOR, (MutableRepository) getBbbManagedCatalogRepository());
			if(bccManagedBrandRepositoryItem!=null && bccManagedBrandRepositoryItem.length !=0){
				final Map<String, String> usUrlMap=(Map<String,String>) bccManagedBrandRepositoryItem[0].getPropertyValue(BBBCatalogConstants.US_URL);
				String usUrl=usUrlMap.get(BBBCoreConstants.DEFAULT_CHANNEL_VALUE);
				RepositoryItem usCatIdRepo=(RepositoryItem) bccManagedBrandRepositoryItem[0].getPropertyValue(BBBCatalogConstants.US_CATEGORY_ID);
				String usCatId = null;
				if(usCatIdRepo != null && usCatIdRepo.getPropertyValue(BBBCoreConstants.ID) != null){
					usCatId=(String)usCatIdRepo.getPropertyValue(BBBCoreConstants.ID);
					categoryMappingVO.setUsCategoryId(usCatId);
				}
				this.logTrace("US URL in getUSCanadaCategoryMapping api of BBBCatalogToolsImpl for site id : " + siteId + " is : "+usUrl);
				this.logTrace("US Category Id in getUSCanadaCategoryMapping api of BBBCatalogToolsImpl for site id : " + siteId + " is : "+usCatId);
				categoryMappingVO.setCanadaCategoryId(categoryId);
				categoryMappingVO.setUsUrl(usUrl);
			}
		}
		this.logDebug("End: Catalog API Method Name [getUSCanadaCategoryMapping]");
		return categoryMappingVO;
	}
	
    /** The category seo link generator. */
    private CategorySeoLinkGenerator categorySeoLinkGenerator;

    /**
     * Gets the category seo link generator.
     *
     * @return Category SEO Link Generator
     */
    public final CategorySeoLinkGenerator getCategorySeoLinkGenerator() {
        return this.categorySeoLinkGenerator;
    }

    /**
     * Sets the category seo link generator.
     *
     * @param seoLinkGenerator the new category seo link generator
     */
    public final void setCategorySeoLinkGenerator(final CategorySeoLinkGenerator seoLinkGenerator) {
        this.categorySeoLinkGenerator = seoLinkGenerator;
    }

    /**
     * Gets the state vo.
     *
     * @param pSiteId the site id
     * @param statesRepositoryItem the states repository item
     * @return the state vo
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    private StateVO getStateVO(final String pSiteId, final RepositoryItem statesRepositoryItem)
                    throws BBBBusinessException, BBBSystemException {
        this.logDebug("Catalog API Method Name [getStateVO]Parameter categoryRepositoryItem[" + statesRepositoryItem
                        + "] Parameter pSiteId[" + pSiteId + "]");
        if (statesRepositoryItem != null) {
            final boolean isNexusState = this.isNexusState(pSiteId, statesRepositoryItem.getRepositoryId());
            boolean isMilitaryState = false;
            boolean isShowOnReg = true;
            boolean isShowOnShipping = true;
            boolean isShowOnBilling = true;
            if (statesRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_MILITARY_STATE) != null) {
                isMilitaryState = ((Boolean) statesRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.IS_MILITARY_STATE)).booleanValue();
            }
            if (statesRepositoryItem.getPropertyValue(BBBCatalogConstants.SHOW_ON_REGISTRY_PAGES) != null) {
            	isShowOnReg = ((Boolean) statesRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.SHOW_ON_REGISTRY_PAGES)).booleanValue();
            }
            if (statesRepositoryItem.getPropertyValue(BBBCatalogConstants.SHOW_ON_SHIPPING_PAGES) != null) {
            	isShowOnShipping = ((Boolean) statesRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.SHOW_ON_SHIPPING_PAGES)).booleanValue();
            }
            if (statesRepositoryItem.getPropertyValue(BBBCatalogConstants.SHOW_ON_BILLING_PAGES) != null) {
            	isShowOnBilling = ((Boolean) statesRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.SHOW_ON_BILLING_PAGES)).booleanValue();
            }
            final String stateName = statesRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.DESCRIPTION_STATE_PROPERTY_NAME) != null ? (String) statesRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.DESCRIPTION_STATE_PROPERTY_NAME) : EMPTYSTRING;
            return new StateVO(statesRepositoryItem.getRepositoryId(), stateName, isNexusState, isMilitaryState,isShowOnReg, isShowOnShipping , isShowOnBilling );
        }
        return null;
    }

    /**
     *  This method return Tag On/OFF status.
     *
     * @param currentSiteId the current site id
     * @param catalogTools the catalog tools
     * @param name the name
     * @return keyStatus
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public String getThirdPartyTagStatus(final String currentSiteId, final BBBCatalogTools catalogTools,
                    final String name) throws BBBBusinessException, BBBSystemException {
        String tagStatus = BBBCatalogConstants.TRUE;

        final StringBuilder tagName = new StringBuilder();
        tagName.append(name);
        tagName.append(BBBCatalogConstants.UNDERSCORE);

        String bedBathUSSite = EMPTYSTRING;
        String buyBuyBabySite = EMPTYSTRING;
        List<String> config = catalogTools.getContentCatalogConfigration(BBBCatalogConstants.BED_BATH_US_SITE_CODE);
        if ((config != null) && !config.isEmpty()) {
            bedBathUSSite = config.get(0);
        }
        config = catalogTools.getContentCatalogConfigration(BBBCatalogConstants.BUY_BUY_BABY_SITE_CODE);
        if ((config != null) && !config.isEmpty()) {
            buyBuyBabySite = config.get(0);
        }

        if (currentSiteId.equalsIgnoreCase(bedBathUSSite)) {
            tagName.append(BBBCatalogConstants.US);
        } else if (currentSiteId.equalsIgnoreCase(buyBuyBabySite)) {
            tagName.append(BBBCatalogConstants.BABY);
        } else {
            tagName.append(BBBCatalogConstants.CA);
        }

        config = catalogTools.getContentCatalogConfigration(tagName.toString());
        if ((config != null) && !config.isEmpty()) {
            tagStatus = config.get(0);
        }
        return tagStatus;
    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getSkuPropFlagStatus(java.lang.String)
     */
    @Override
    public final Map<String, String> getSkuPropFlagStatus(final String skuId)
                    throws BBBBusinessException, BBBSystemException {
        this.logDebug("Catalog API Method Name [getSkuPropFlagStatus]Parameter  sku id[" + skuId + "]");
        RepositoryItem skuRepositoryItem;
        final Map<String, String> skuMap = new HashMap<String, String>();

        if (skuId != null) {

            try {
                skuRepositoryItem = this.getCatalogRepository().getItemForUpdate(skuId,
                                BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
                if (skuRepositoryItem == null) {
                    this.logDebug("Sku Repository Item is null for sku id " + skuId);
                    throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
                                    BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
                }
                this.logDebug("getting Prop details for sku id " + skuId);
                for (final String prop65Attribute : this.getProp65AttributesList()) {
                    if ((skuRepositoryItem.getPropertyValue(prop65Attribute) != null)
                                    && ((Boolean) skuRepositoryItem.getPropertyValue(prop65Attribute)).booleanValue()) {
                        skuMap.put(prop65Attribute, (String) skuRepositoryItem.getPropertyValue("displayName"));
                    }
                }
            } catch (final RepositoryException e) {
                this.logError("Repository Exception ");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
            }
        } else {
            this.logDebug("List of Sku is null or empty");
        }
        return skuMap;
    }

    /**
     * Sets the preview enabled.
     *
     * @param mPreviewEnabled the new preview enabled
     */
    public void setPreviewEnabled(boolean mPreviewEnabled) {
		this.previewEnabled = Boolean.valueOf(mPreviewEnabled);
	}
    
    /**
     *  This method is to identify whether it is a preview site.
     *
     * @return boolean
     */
    @Override
    public final boolean isPreviewEnabled() {
    	
    	return getGlobalRepositoryTools().isPreviewEnabled();

    }

    /**
     *  The method gets the Preview date against which thek of start and end date check will be made for Preview by
     * business users As PreviewAttributes is a request scope so its resolved and not injected.
     *
     * @return Preview Date
     */

    @Override
    public final Date getPreviewDate() {
    	
    	return getGlobalRepositoryTools().getPreviewDate();

    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getSkuClass(atg.repository.RepositoryItem)
     */
    @Override
    public final RepositoryItem[] getSkuClass(final RepositoryItem sku) throws BBBSystemException {
        RepositoryItem[] classItems = null;
        final String jdaClass = (String) sku.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME);
        final String rql = "jdaClass=?0";
        try {
            final RepositoryView catalogView = this.getCatalogRepository().getView(
                            BBBCatalogConstants.CLASS_ITEM_DESCRIPTOR);
            final RqlStatement statement = RqlStatement.parseRqlStatement(rql);
            classItems = statement.executeQuery(catalogView, new Object[] { jdaClass });
        } catch (final RepositoryException e) {
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        }
        return classItems;

    }

    /**
     * Gets the special purchase attr id.
     *
     * @return Special Purchase Attribute Date
     */
    public final String getSpecialPurchaseAttrID() {
        return this.specialPurchaseAttrID;
    }

    /**
     * Sets the special purchase attr id.
     *
     * @param specialPurchaseAttrID the new special purchase attr id
     */
    public final void setSpecialPurchaseAttrID(final String specialPurchaseAttrID) {
        this.specialPurchaseAttrID = specialPurchaseAttrID;
    }

    /**
     * Gets the beyond value attr id.
     *
     * @return Beyond Value Attribute
     */
    public final String getBeyondValueAttrID() {
        return this.beyondValueAttrID;
    }

    /**
     * Sets the beyond value attr id.
     *
     * @param beyondValueAttrID the new beyond value attr id
     */
    public final void setBeyondValueAttrID(final String beyondValueAttrID) {
        this.beyondValueAttrID = beyondValueAttrID;
    }

    /**
     * Gets the clearence attr id.
     *
     * @return Clearence Attribute ID
     */
    public final String getClearenceAttrID() {
        return this.clearenceAttrID;
    }

    /**
     * Sets the clearence attr id.
     *
     * @param clearenceAttrID the new clearence attr id
     */
    public final void setClearenceAttrID(final String clearenceAttrID) {
        this.clearenceAttrID = clearenceAttrID;
    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getSkuRepositoryItem(java.lang.String)
     */
    @Override
    public final RepositoryItem getSkuRepositoryItem(final String skuId)
                    throws BBBBusinessException, BBBSystemException {
        RepositoryItem skuRepositoryItem = null;
        try {
            skuRepositoryItem = this.getCatalogRepository().getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
        } catch (final RepositoryException e) {
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            "failed to retrive sku item from repository", e);
        }
        return skuRepositoryItem;
    }
    
    /**
     * This method is used to fetch multiple sku details.
     *
     * @param skuIds the sku ids
     * @return the list of sku repository items
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final RepositoryItem[] getMultipleSkuRepositoryItems(final String[] skuIds)
                    throws BBBBusinessException, BBBSystemException {
        RepositoryItem[] skuRepositoryItems = null;
        try {
        	skuRepositoryItems = this.getCatalogRepository().getItems(skuIds, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
        } catch (final RepositoryException e) {
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            "failed to retrive multiple sku items from repository", e);
        }
        return skuRepositoryItems;
    }

    /**
     *  Method to return true/false if shipping restrictions are applied for a sku.
     *  
     *
     * @param pSkuId the sku id
     * @return zipCodesRestrictedForSku
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @SuppressWarnings ("unchecked")
    public final boolean isShippingRestrictionsExistsForSku(final String pSkuId) throws BBBBusinessException, BBBSystemException {
    	
    	this.logDebug("Catalog API Call [isShippingRestrictionsExistsForSku] - Start - Parameters SkuId :" + pSkuId);
        boolean isSkuZipStatus = false;
        boolean isSkuStateStatus = false;
        Set<RepositoryItem> mapZipRestricted = null;
        String strZipCodes = null;
        BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " isShippingRestrictionsExistsForSku");
        
        try {
            if ((pSkuId != null) && !(pSkuId.equalsIgnoreCase(DELIVERY_SURCHARGE_SKU) || pSkuId.equalsIgnoreCase(ASSEMBLY_FEE_SKU))) {
                this.logDebug("SkuId :" + pSkuId );
                final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(pSkuId,
                                BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
                
                if (skuRepositoryItem != null) {
                    final Set<RepositoryItem> nonShippableStatesRepoItemSet = (Set<RepositoryItem>) skuRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.NON_SHIPPABLE_STATES_SHIPPING_PROPERTY_NAME);

                    if ((nonShippableStatesRepoItemSet != null) && !nonShippableStatesRepoItemSet.isEmpty()) {
                        this.logDebug("Non shippable states configured for sku id [" + pSkuId + "]");
                        isSkuStateStatus= true;
                    }
                    
                    if (!isSkuStateStatus && (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SITE_SKU_REGIONS)) != null) {
                        mapZipRestricted = ((Set<RepositoryItem>) skuRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.SITE_SKU_REGIONS));
                        for (final RepositoryItem regionItem : mapZipRestricted) {
                            if (regionItem != null) {

                                if (regionItem.getPropertyValue(BBBCatalogConstants.RESTRICTED_SKU_ZIP_CODES) != null) {
                                    strZipCodes = (String) regionItem
                                                    .getPropertyValue(BBBCatalogConstants.RESTRICTED_SKU_ZIP_CODES);
                                    if ((strZipCodes != null) && (strZipCodes.length() > 0)) {
                                    	this.logDebug("Non shippable Zip codes configured for sku id [" + pSkuId + "]");
                                       isSkuZipStatus = true;
                                    }
                                } else {
                                    this.logDebug("No Zip Code available for Sku :" + pSkuId);
                                }
                            } else {
                                this.logDebug("No Zip Code available for Sku :" + pSkuId);
                            }
                            if (isSkuZipStatus) {
                                break;
                            }
                        }
                    }
                } else {
                    this.logDebug("Repository Item is null for sku id " + pSkuId);
                    throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
                }
            } else {
                this.logDebug("Sku or Zip is null");
                throw new BBBBusinessException(BBBCatalogErrorCodes.NO_SKU_PASSED);
            }
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [isShippingRestrictionsExistsForSku]: RepositoryException "
                            + BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,e);
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " isShippingRestrictionsExistsForSku");
        }
        this.logDebug("Catalog API Call [isShippingRestrictionsExistsForSku] - End");
        if(isSkuZipStatus || isSkuStateStatus){
        	return true;
        }
        return false;
    }
    
    /**
     *  Method to return Map with restricted zip codes as value and region name as key if passed skuId .
     *
     * @param pSkuId the sku id
     * @return zipCodesRestrictedForSku
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @SuppressWarnings ("unchecked")
    public final Map<String,String> getZipCodesRestrictedForSku(final String pSkuId) throws BBBBusinessException, BBBSystemException {
        Set<RepositoryItem> mapZipRestricted = null;
        
        this.logDebug("Catalog API Call [getZipCodesRestrictedForSku] - Parameters SkuId :" + pSkuId);
        Map<String,String> zipCodesRestrictedForSku = new HashMap<String,String>();
        String strZipCodes = null;
        String skuRegionName = null;
        BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getZipCodesRestrictedForSku");

        try {
            if (BBBUtility.isNotEmpty(pSkuId)) {
                
                final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(pSkuId,
                                BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
                if (skuRepositoryItem != null && (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SITE_SKU_REGIONS)) != null) {
                    
                	mapZipRestricted = ((Set<RepositoryItem>) skuRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.SITE_SKU_REGIONS));
                    for (final RepositoryItem regionItem : mapZipRestricted) {

                        if (regionItem.getPropertyValue(BBBCatalogConstants.RESTRICTED_SKU_ZIP_CODES) != null && regionItem
                                .getPropertyValue(BBBCatalogConstants.REGION_NAME) != null) {
                            strZipCodes = (String) regionItem
                                            .getPropertyValue(BBBCatalogConstants.RESTRICTED_SKU_ZIP_CODES);
                            skuRegionName = (String) regionItem
                                    .getPropertyValue(BBBCatalogConstants.REGION_NAME);
                           
                            if (BBBUtility.isNotEmpty(strZipCodes)) {
                            	this.logDebug("Adding region : " + skuRegionName + " and zip codes : " + strZipCodes + " to Map for sku :" + pSkuId);
                                zipCodesRestrictedForSku.put(skuRegionName, strZipCodes);
                            }
                        } else {
                            this.logDebug("No Zip Code available for Sku :" + pSkuId);
                        }
                    }
                } else {
                    this.logDebug("No Zip Code available for Sku or Repository Item is null for sku id " + pSkuId);
                    throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
                }
            } else {
                this.logDebug("Sku or Zip is null");
                throw new BBBBusinessException(BBBCatalogErrorCodes.NO_SKU_PASSED);
            }
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getZipCodesRestrictedForSku]: RepositoryException "
                            + BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,e);
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getZipCodesRestrictedForSku");
        }
        this.logDebug("Catalog API Call [getZipCodesRestrictedForSku] - End");
        return zipCodesRestrictedForSku;
    }

    /* Method to return boolean value if passed zip contains in set of set of zip code of perticalr sku
     * @see com.bbb.commerce.catalog.BBBCatalogTools#isShippingZipCodeRestrictedForSku (java.lang.String,
     * java.lang.String, java.lang.String) */
    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#isShippingZipCodeRestrictedForSku(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @SuppressWarnings ("unchecked")
    public  boolean isShippingZipCodeRestrictedForSku(final String pSkuId, final String pSiteId,
                    final String pZipCode) throws BBBBusinessException, BBBSystemException {
        Set<RepositoryItem> mapZipRestricted = null;

        String strZipCodes = null;
        String splitRestrictedZipCode[] = null;
        boolean isSkuZipStatus = false;
        final String zipCodeSeparator = this.getZipCodeSaperater();
        BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " isShippingZipCodeRestrictedForSku");
        // LTL | Check if sku id is not of type AssemblyFee or DeliverySurcharge
        if(!(pSkuId.equalsIgnoreCase(DELIVERY_SURCHARGE_SKU) || pSkuId.equalsIgnoreCase(ASSEMBLY_FEE_SKU))){
        try {
            if ((pSkuId != null) && (pZipCode != null)) {
                    this.logDebug("SkuId :" + pSkuId + "ZipCode :" + pZipCode + "site Id :" + pSiteId);
                final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(pSkuId,
                                BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
                if (skuRepositoryItem != null) {
                    if ((skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SITE_SKU_REGIONS)) != null) {
                        mapZipRestricted = ((Set<RepositoryItem>) skuRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.SITE_SKU_REGIONS));
                        for (final RepositoryItem regionItem : mapZipRestricted) {
                            if (regionItem != null) {

                                if (regionItem.getPropertyValue(BBBCatalogConstants.RESTRICTED_SKU_ZIP_CODES) != null) {
                                    strZipCodes = (String) regionItem
                                                    .getPropertyValue(BBBCatalogConstants.RESTRICTED_SKU_ZIP_CODES);
                                    splitRestrictedZipCode = strZipCodes.split(zipCodeSeparator);
        								if (splitRestrictedZipCode.length > 0) {
                                        for (final String zipCode : splitRestrictedZipCode) {
                                            if (BBBUtility.isNotEmpty(zipCode) && pZipCode.startsWith(zipCode)) {
                                                isSkuZipStatus = true;
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                        this.logDebug("No Zip Code available for Sku :" + pSkuId);
                                    isSkuZipStatus = false;
                                }
                            } else {
                                    this.logDebug("No Zip Code available for Sku :" + pSkuId + "and current site "
                                                    + pSiteId);
                                isSkuZipStatus = false;
                            }
                            if (isSkuZipStatus) {
                                break;
                            }
                        }
                    } else {
                            this.logDebug("No Zip Code available for Sku :" + pSkuId);
                        isSkuZipStatus = false;
                    }
                } else {
                    this.logDebug("Repository Item is null for sku id " + pSkuId);
                    throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
                }
            } else {
                    this.logDebug("Sku or Zip is null");
                throw new BBBBusinessException(BBBCatalogErrorCodes.NO_SKU_PASSED);
            }
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [isShippingZipCodeRestrictedForSku]: RepositoryException "
                            + BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,e);
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " isShippingZipCodeRestrictedForSku");
        }}
        return isSkuZipStatus;
    }

    /* Method to return boolean value if passed zip contains in set of set of zip code of perticalr sku
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getRestrictedSkuDetails (java.lang.String, java.lang.String,
     * java.lang.String) */

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getRestrictedSkuDetails(java.lang.String, java.lang.String)
     */
    @Override
    @SuppressWarnings ("unchecked")
    public final String getRestrictedSkuDetails(final String pSkuId, final String pZipCode)
                    throws BBBBusinessException, BBBSystemException {
        Set<RepositoryItem> setZipRestricted = null;
        String skuRegionName = null;
        String splitRestrictedZipCode[] = null;
        BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + "getRestrictedSkuDetails");
            this.logDebug("Catalog API Method Name [getRestrictedSkuDetails] SkuId :" + pSkuId + "site Id :" + pZipCode);
        
        try {
            if (pSkuId != null) {
                final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(pSkuId,
                                BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
                if (skuRepositoryItem != null) {
                    if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SITE_SKU_REGIONS) != null) {
                        setZipRestricted = ((Set<RepositoryItem>) skuRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.SITE_SKU_REGIONS));
                        if (setZipRestricted != null) {
                            for (final RepositoryItem zipRepositoryItem : setZipRestricted) {
                                final String restrictedZipCodes = (String) zipRepositoryItem
                                                .getPropertyValue(BBBCatalogConstants.RESTRICTED_SKU_ZIP_CODES);
                                if (restrictedZipCodes != null) {
                                    if (restrictedZipCodes.contains(pZipCode)) {
                                        skuRegionName = (String) zipRepositoryItem
                                                        .getPropertyValue(BBBCatalogConstants.REGION_NAME);
                                        break;
                                    }
                                    else{
                                    	//UAT-829 | This implementation is to get sku region name in case zip code provided is 9 digit code which is sub area code for restricted zip code
                                    	  splitRestrictedZipCode = restrictedZipCodes.split(zipCodeSaperater);
                                          if ((splitRestrictedZipCode != null) && (splitRestrictedZipCode.length > 0)) {
                                              for (final String zipCode : splitRestrictedZipCode) {
                                                  if (BBBUtility.isNotEmpty(zipCode) && pZipCode.startsWith(zipCode)) {
                                                	  skuRegionName = (String) zipRepositoryItem
                                                              .getPropertyValue(BBBCatalogConstants.REGION_NAME);
                                                	  break;
                                                  }
                                              }
                                              if(BBBUtility.isNotEmpty(skuRegionName)){
                                            	  break;
                                              }
                                          }
                                    }
                                } else {
                                        this.logDebug("No Region Name available for Sku :" + pSkuId);
                                    }
                                }
                        } else {
                                this.logDebug("No Region Name available for Sku :" + pSkuId + "and current site "
                                                + pZipCode);
                            }
                    } else {
                            this.logDebug("No Region Name available for Sku :" + pSkuId);
                        }
                } else {
                    this.logDebug("Repository Item is null for sku id " + pSkuId);
                    throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
                }
            } else {
                    this.logDebug("Sku or Zip is null");
                throw new BBBBusinessException(BBBCatalogErrorCodes.NO_SKU_PASSED);
            }
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getRestrictedSkuDetails]: RepositoryException "
                            + BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,e);
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
        	this.logDebug("Catalog API Method Name [getRestrictedSkuDetails] SkuId :" + pSkuId + "site Id :" + pZipCode +" Exit");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getRestrictedSkuDetails");
        }
        return skuRegionName;
    }

    /* Method to return boolean value if an sku contains restricted zip code
     * @see com.bbb.commerce.catalog.BBBCatalogTools#isSkuWithRestrictedShipping( java.lang.String, java.lang.String) */
    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#isSkuWithRestrictedShipping(java.lang.String, java.lang.String)
     */
    @SuppressWarnings ("unchecked")
    @Override
    public final boolean isSkuWithRestrictedShipping(final String pSkuId, final String pSiteId)
                    throws BBBBusinessException, BBBSystemException {
        Set<RepositoryItem> mapZipRestricted = null;
        boolean isSkuZipStatus = false;
        BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " isSkuWithRestrictedShipping");
        try {
            if (pSkuId != null) {
                    this.logDebug("SkuId :" + pSkuId + "SiteId :" + pSiteId);
                final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(pSkuId,
                                BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
                if (skuRepositoryItem != null) {
                    if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SITE_SKU_REGIONS) != null) {
                        mapZipRestricted = ((Set<RepositoryItem>) skuRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.SITE_SKU_REGIONS));
                        if (mapZipRestricted != null) {

                            if (!mapZipRestricted.isEmpty()) {
                                isSkuZipStatus = true;

                            } else {
                                    this.logDebug("No Zip Code available for Sku :" + pSkuId);
                                isSkuZipStatus = false;
                            }
                        } else {
                                this.logDebug("No Zip Code available for Sku :" + pSkuId + "and current site "
                                                + pSiteId);
                            isSkuZipStatus = false;
                        }
                    } else {
                            this.logDebug("No Zip Code available for Sku :" + pSkuId + "and current site " + pSiteId);
                        isSkuZipStatus = false;
                    }
                } else {
                    this.logDebug("Repository Item is null for sku id " + pSkuId);
                    throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);

                }
            } else {
                    this.logDebug("Sku is null");
                throw new BBBBusinessException(BBBCatalogErrorCodes.NO_SKU_PASSED);
            }
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [isShippingZipCodeRestrictedForSku]: RepositoryException "
                            + BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,e);
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);

        } finally {
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " isSkuWithRestrictedShipping");
        }
        return isSkuZipStatus;
    }

    /**
     *  the method returns the product id of the first parent of the sku that is active.
     *
     * @param pSkuId the sku id
     * @return First Active Parent
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    public final String getFirstActiveParentProductForInactiveSKU(final String pSkuId)
                    throws BBBBusinessException, BBBSystemException {
    	
    	return getGlobalRepositoryTools().getFirstActiveParentProductForInactiveSKU(pSkuId);

    }

    /**
     * Gets the zip code saperater.
     *
     * @return the mZipCodeSaperater
     */
    public final String getZipCodeSaperater() {
        return this.zipCodeSaperater;
    }

    /**
     * Sets the zip code saperater.
     *
     * @param mZipCodeSaperater the mZipCodeSaperater to set
     */
    public final void setZipCodeSaperater(final String mZipCodeSaperater) {
        this.zipCodeSaperater = mZipCodeSaperater;
    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getUSAStoreDetails()
     */
    @Override
    public final List<StoreVO> getUSAStoreDetails() throws BBBBusinessException, BBBSystemException {
    	
    	return getStoreRepositoryTools().getUSAStoreDetails();

    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getAttributeInfoRepositoryItems(java.lang.String)
     */
    @Override
    public final RepositoryItem[] getAttributeInfoRepositoryItems(final String id)
                    throws BBBBusinessException, BBBSystemException {
        RepositoryItem[] items = null;
        try {
            items = this.getAttributeInfoRepository().getItems(new String[] { id }, BBBCatalogConstants.ATTRIBUTE_INFO);
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getAttributeInfoRepositoryItems]:RepositoryException ",e);
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);

        }
        return items;
    }

    /**
     *  Bopus exclusion check for Store.
     *
     * @param pStoreId the store id
     * @param pSiteId the site id
     * @return BOPUS in Eligible Stores
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public  List<String> getBopusInEligibleStores(final String pStoreId, final String pSiteId)
                    throws BBBSystemException, BBBBusinessException {
    	
    	return getStoreRepositoryTools().getBopusInEligibleStores(pStoreId, pSiteId);

    }

    /**
     * Gets the brand display flag.
     *
     * @param pBrandName the brand name
     * @return Brand Display Flag
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final boolean getBrandDisplayFlag(final String pBrandName) throws BBBBusinessException, BBBSystemException {
        boolean displayFlag = false;
        this.logDebug("Catalog API Method Name [getBrandDisplayFlag]brandName[" + pBrandName + "]");

        if (StringUtils.isEmpty(pBrandName)) {

            this.logDebug("Catalog API Method Name [getBrandDisplayFlag]:Brand Name is null");
        } else {

            RepositoryItem[] brandRepositoryItem = null;
            final Object[] params = new Object[1];
            params[0] = pBrandName;

            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getBrandDisplayFlag");
            brandRepositoryItem = this.executeRQLQuery(this.getBrandNameQuery(), params,
                            BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR, this.getCatalogRepository());

            if ((brandRepositoryItem != null) && (brandRepositoryItem.length != 0)) {
                if (brandRepositoryItem[0].getPropertyValue(BBBCatalogConstants.DISPLAY_BRAND_PROPERTY_NAME) != null) {
                    this.logDebug("Brand DisplayFlag ["
                                    + brandRepositoryItem[0]
                                                    .getPropertyValue(BBBCatalogConstants.DISPLAY_BRAND_PROPERTY_NAME)
                                    + "]");
                    displayFlag = ((Boolean) brandRepositoryItem[0]
                                    .getPropertyValue(BBBCatalogConstants.DISPLAY_BRAND_PROPERTY_NAME)).booleanValue();
                }
            } else {
                this.logDebug("Catalog API Method Name [getBrandDisplayFlag]:Brand with name" + params[0]
                                + " is not found in database");

            }

        }
        return displayFlag;
    }

    /**
     * Gets the sorted college category.
     *
     * @param collegeCategory the college category
     * @return CategoryVO
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final CategoryVO getSortedCollegeCategory(final CategoryVO collegeCategory)
                    throws BBBBusinessException, BBBSystemException {

        List<String> categoryList = null;

        try {
            categoryList = this.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, this.getCollegeCategoryKey());
        } catch (final BBBSystemException e) {
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } catch (final BBBBusinessException e) {
            throw new BBBSystemException(BBBCatalogErrorCodes.CONFIG_KEYS_NOT_AVAILABLE, e);
        }

        if (!BBBUtility.isListEmpty(categoryList)) {

            final String attributes[] = categoryList.get(0).trim().split(",");
            categoryList = Arrays.asList(attributes);
            this.logDebug("College Category from config Keys are : " + categoryList);

            final List<CategoryVO> subCategories = collegeCategory.getSubCategories();
            final List<CategoryVO> sortedCategories = new ArrayList<CategoryVO>();
            if (!BBBUtility.isListEmpty(subCategories)) {
                for (final String category : categoryList) {
                    for (final CategoryVO categoryVO : subCategories) {
                        if (categoryVO.getCategoryId().equalsIgnoreCase(category)) {
                            sortedCategories.add(categoryVO);
                        }
                    }
                }
                subCategories.removeAll(sortedCategories);
                sortedCategories.addAll(subCategories);

                collegeCategory.setSubCategories(sortedCategories);
            }
        }

        return collegeCategory;
    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getBeddingShipAddress(java.lang.String)
     */
    @Override
    public final BeddingShipAddrVO getBeddingShipAddress(final String schoolId)
                    throws BBBSystemException, BBBBusinessException {
    	
    	return getSchoolRepositoryTools().getBeddingShipAddress(schoolId);
    }

    /**
     *  This method will validate bedding kit attribute and if it present it will get school details.
     *
     * @param shipGrpList the ship grp list
     * @param collegeIdValue the college id value
     * @return the bedding ship addr vo
     */

    @Override
    public final BeddingShipAddrVO validateBedingKitAtt(final List shipGrpList,
                    final String collegeIdValue) {
        this.logDebug("Catalog API Method Name [validateBedingKitAtt] starts with schoolId[" + collegeIdValue + "]");
        BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " validateBedingKitAtt");
        BeddingShipAddrVO beddingShipAddrVO = null;
        HardgoodShippingGroup shipGrp;
        boolean isWebLinkOrder = false;
        String beddingAttributeId = null;
        if ((shipGrpList != null) && (shipGrpList.size() == 1)) {
        	if(null != shipGrpList.get(0) && shipGrpList.get(0) instanceof HardgoodShippingGroup){
            shipGrp = (HardgoodShippingGroup) shipGrpList.get(0);
            if (shipGrp != null) {
                @SuppressWarnings ("unchecked")
                final List<BBBShippingGroupCommerceItemRelationship> bbbSGCIRelList = shipGrp
                                .getCommerceItemRelationships();
                beddingAttributeId = BBBConfigRepoUtils.getStringValue(BBBCmsConstants.CONTENT_CATALOG_KEYS,
                                BBBCatalogConstants.BEDDING_KIT_ATTRIBUTE);
                for (final BBBShippingGroupCommerceItemRelationship bbbSGCIRelItem : bbbSGCIRelList) {
                	boolean isBeddingKitProduct= false;
                    final RepositoryItem catalogRefItem = (RepositoryItem) bbbSGCIRelItem.getCommerceItem()
                                    .getAuxiliaryData().getCatalogRef();
                    if (catalogRefItem != null) {
                        @SuppressWarnings ("unchecked")
                        final Set<RepositoryItem> skuAttrRelnIdsSet = (Set<RepositoryItem>) catalogRefItem
                                        .getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME);
                        if ((skuAttrRelnIdsSet != null) && !skuAttrRelnIdsSet.isEmpty()) {
                            for (final RepositoryItem skuAttrRelnIds : skuAttrRelnIdsSet) {
                                String attributeId ;
                                String skuAttrRelnId = skuAttrRelnIds.getRepositoryId();
                                if (BBBUtility.isNotEmpty(skuAttrRelnId)) {
                                    final RepositoryItem skuAttribute = (RepositoryItem) skuAttrRelnIds
                                                    .getPropertyValue(SKU_ATTRIBUTE);
                                    try {
                                        if (skuAttribute != null) {
                                            final SimpleDateFormat dateFormat = new SimpleDateFormat(MM_DD_YYYY);
                                            final Date previewDate = dateFormat.parse(dateFormat.format(new Date()));
                                            final Date startDateOfSku = (Date) skuAttrRelnIds
                                                            .getPropertyValue(BBBCatalogConstants.START_DATE_SKU_RELATION_PROPERTY_NAME);
                                            final Date endDateOfSku = (Date) skuAttrRelnIds
                                                            .getPropertyValue(BBBCatalogConstants.END_DATE_SKU_RELATION_PROPERTY_NAME);
                                            attributeId = (skuAttribute.getPropertyValue(ID)).toString();

                                            if (attributeId.equalsIgnoreCase(beddingAttributeId)
                                                            && BBBUtility.isAttributeApplicable(previewDate,
                                                                            startDateOfSku, endDateOfSku)) {
                                            	isBeddingKitProduct = true;
                                            }
                                        }
                                    } catch (final ParseException e1) {
                                        this.logError("Catalog API Method Name [validateBedingKitAtt]: Parsing Exception ");
                                    }
                                    if ((beddingAttributeId != null) && isBeddingKitProduct) {
                                        try {
                                            beddingShipAddrVO = this.getBeddingShipAddress(collegeIdValue);
                                        } catch (final BBBBusinessException e) {
                                            this.logError("School Id not valid"
                                                            + BBBCatalogErrorCodes.SCHOOL_ID_NOT_AVAILABLE_IN_REPOSITORY, e);
                                        } catch (final BBBSystemException e) {
                                            this.logError("Repository Exception"
                                                            + BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION , e);
                                        }
                                            this.logDebug("Bedding kit attribute is present for sku ::"
                                                            + catalogRefItem.getRepositoryId());
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    // if there is atleast one prouduct in cart which is not bedding kit product
                    //even if other product is of type bedding kit then it is a weblink order not a CBK order
                    if(!isBeddingKitProduct){
                    	isWebLinkOrder = true;
                    	break;
                    }
                }
            }
        	}
        }
        this.logDebug("Catalog API Method Name [validateBedingKitAtt]schoolId ends");
        BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " validateBedingKitAtt");
        if(isWebLinkOrder){
        	return null;
        }
        return beddingShipAddrVO;
        
    }
	
    
    /**
     *  This method will return BeddingShipAddrVO details.
     *
     * @param collegeIdValue the college id value
     * @return the bedding ship addr vo
     */

    public final BeddingShipAddrVO getBeddingShipAddrVO(final String collegeIdValue) {
        this.logDebug("BBBCatalogToolsImpl :  getBeddingShipAddrVO starts with schoolId "+ collegeIdValue);
        BeddingShipAddrVO beddingShipAddrVO = null;
			try {
				beddingShipAddrVO = this.getBeddingShipAddress(collegeIdValue);
			 } catch (final BBBBusinessException e) {
                 this.logError("School Id " + collegeIdValue + " not valid : "
                                 + BBBCatalogErrorCodes.SCHOOL_ID_NOT_AVAILABLE_IN_REPOSITORY + e);
             } catch (final BBBSystemException e) {
                 this.logError("Repository Exception"
                                 + BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION + e);
             }
        this.logDebug("BBBCatalogToolsImpl :  getBeddingShipAddrVO ends");
        return beddingShipAddrVO;
    }

    

    /**
     *  This method will validate bedding date with syatem date.
     *
     * @param beddingShiptDate the bedding shipt date
     * @param currentDate the current date
     * @return true, if successful
     * @throws ParseException the parse exception
     */
    @Override
    public final boolean validateBeddingAttDate(final String beddingShiptDate, final String currentDate)
                    throws ParseException {
            this.logDebug("validateBeddingAttDate method starts bedding date" + beddingShiptDate + "and current date"
                            + currentDate);
        boolean flagBeddingShip = false;
        int daysBetween = 0;
        if(BBBUtility.isNotEmpty(beddingShiptDate) && BBBUtility.isNotEmpty(currentDate)){
        	daysBetween = daysBetween(beddingShiptDate, currentDate);
        	 if (daysBetween >= Integer.parseInt((BBBConfigRepoUtils.getStringValue(
                        BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCatalogConstants.NO_OF_DAYS)))) {
            flagBeddingShip = true;
        }
        }
            this.logDebug("validateBeddingAttDate method ends bedding date");
        return flagBeddingShip;
    }

    /**
     * Days between.
     *
     * @param pShippingEndDate the shipping end date
     * @param pCurrentDate the current date
     * @return the int
     * @throws ParseException the parse exception
     */
    private static int daysBetween(final String pShippingEndDate, final String pCurrentDate) throws ParseException {

        SimpleDateFormat dateformat = new SimpleDateFormat(MM_DD_YYYY);
        final String siteId = SiteContextManager.getCurrentSiteId();
        if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
        	dateformat = new SimpleDateFormat(DD_MM_YYYY); 
		}
        final Date shippingEndDate = dateformat.parse(pShippingEndDate);
        final Date currentDate = dateformat.parse(pCurrentDate);

        return (int) ((shippingEndDate.getTime() - currentDate.getTime()) / (1000 * 60 * 60 * 24));
    }

    /**
     *  This method will fetch the shop guide id for the given product and then check whether any guide id exist for that
     * shop guide id and for the current site. If found, then it will return that guide id else return null string.
     *
     * @param productRepositoryItem the product repository item
     * @param siteId the site id
     * @return the product guide id
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final String getProductGuideId(final RepositoryItem productRepositoryItem, final String siteId)
                    throws BBBSystemException {
    	this.logDebug("Catalog API Method Name []  siteId [" + siteId + "] productRepositoryItem [" + productRepositoryItem.getRepositoryId() + "]");
    	
        String guideId = null;
        final String productShopGuideId = (String) productRepositoryItem
                        .getPropertyValue(BBBCatalogConstants.PRODUCT_SHOP_GUIDE_ID);

        if (!BBBUtility.isEmpty(productShopGuideId)) {
            try {
                RepositoryItem[] guideRepositoryItem = null;
                final RepositoryView guidesView = this.getGuidesRepository().getView(BBBCatalogConstants.GUIDES);
                final RqlStatement fetchGuideIdStatement = parseRqlStatement(this
                                .getProductGuideRqlQuery());
                final Object shopGuideIdParam[] = new Object[2];

                shopGuideIdParam[0] = productShopGuideId;
                shopGuideIdParam[1] = siteId;
                guideRepositoryItem = fetchGuideIdStatement.executeQuery(guidesView, shopGuideIdParam);

                // it will return the guide available for the current site. Also as per requirement, the first guide id
                // found is to be returned in case of multiple guides attached to product.
                if (guideRepositoryItem != null) {
                    guideId = (String) guideRepositoryItem[0].getPropertyValue(BBBCatalogConstants.GUIDE_ID);
                    this.logTrace("Product guide id " + guideId + " exist for " + siteId);
                    return guideId;
                }
                this.logTrace("No guide exist for this product shop guide id " + productShopGuideId);
                return guideId;

            } catch (final RepositoryException e) {
                this.logError("Repository Exception in fetching product guide");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
            }
        }
        this.logTrace("No shop guide id found for this product " + productRepositoryItem);
        this.logDebug("Catalog API Method Name []  siteId [" + siteId + "] productRepositoryItem [" + productRepositoryItem.getRepositoryId() + "] Exit");
        return guideId;
    }

    /**
     *  R2 Scope Item -- API for returning the Page Tabs order for a particular page.
     *
     * @param pPageName the page name
     * @return Tab Name List
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final List<String> getTabNameList(final String pPageName) throws BBBBusinessException, BBBSystemException {
        this.logDebug("Catalog API Method Name [getTabNameList]");

        final List<String> tabList = new ArrayList<String>();
        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getTabNameList");
            RepositoryItem[] pageOrderItem;

            final Object[] params = new Object[1];
            params[0] = pPageName;

            pageOrderItem = this.executeRQLQuery(this.getPageOrderQuery(), params,
                            BBBCatalogConstants.PAGE_ORDER_ITEM_DESCRIPTOR,
                            (MutableRepository) this.getPageOrderRepository());

            if ((pageOrderItem != null) && (pageOrderItem.length > 0)) {
                for (final RepositoryItem element : pageOrderItem) {
                    @SuppressWarnings ("unchecked")
                    final List<RepositoryItem> tabItem = (List<RepositoryItem>) element
                                    .getPropertyValue(BBBCatalogConstants.TABS_LIST_PROPERTY);
                    if (!BBBUtility.isListEmpty(tabItem)) {
                        for (final RepositoryItem pRepItem : tabItem) {
                            tabList.add((String) pRepItem.getPropertyValue(BBBCatalogConstants.TAB_NAME_PROPERTY));
                        }
                    }
                }
            } else {
                throw new BBBBusinessException(BBBCatalogErrorCodes.NO_SUCH_PAGE_ORDER_DEFINED,
                                BBBCatalogErrorCodes.NO_SUCH_PAGE_ORDER_DEFINED);
            }
        } finally {
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getTabNameList");
        }
        return tabList;
    }

    /**
     *  added variables as part of release 2.1 scope#29.
     *
     * @param productId the product id
     * @return the primary category
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public  String getPrimaryCategory(final String productId) throws BBBSystemException, BBBBusinessException {

        if (BBBUtility.isNotEmpty(productId)) {
            String primaryCategory = null;

            try {
                final RepositoryItem productRepositoryItem = this.getCatalogRepository().getItem(productId,
                                BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);

                if (productRepositoryItem != null) {
                    primaryCategory = (String) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.PRIMARY_PARENT_CATEGORY);
                    if (primaryCategory != null) {
                        final RepositoryItem categoryRepositoryItem = this.getCatalogRepository().getItem(
                                        primaryCategory, BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
                        if (categoryRepositoryItem != null) {
                            return primaryCategory;
                        }
                        return null;
                    }
                    return primaryCategory;
                }
                this.logDebug("Product Id " + productId + " is not present in the repository");
                throw new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);

            } catch (final RepositoryException e) {
                this.logError("Catalog API Method Name [getPrimaryCategory]: RepositoryException ");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
            }
        }
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }

    /**
     *  This method returns a VO containing the category related data (+ BCC managed data).
     *
     * @param categoryId : for which VO details would be fetched
     * @return VO having details of a category(id) passed as an argument
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    public final CategoryVO getFullCategoryVO(final String categoryId) throws BBBBusinessException, BBBSystemException {

        final String siteId = SiteContextManager.getCurrentSiteId();
        CategoryVO categoryVO = new CategoryVO();
        try {
        if (BBBUtility.isEmpty(categoryId) ) {
        	categoryVO.setErrorExist(true);
        	categoryVO.setErrorCode(BBBCatalogErrorCodes.INVALID_INPUT_PROVIDED);
        	categoryVO.setErrorMessage(BBBCatalogConstants.INVALID_CATEGORY);
        	return categoryVO;
        }

            categoryVO = this.getCategoryDetail(siteId, categoryId,false);
            this.getBccManagedCategory(categoryVO);

        }
        catch (final BBBBusinessException e) {
        	 this.logError("Catalog API Method Name [getFullCategoryVO]: BBBBusinessException ", e);
        	categoryVO.setErrorExist(true);
        	categoryVO.setErrorMessage(BBBCatalogConstants.INACTIVE_CATEGORY);
        	categoryVO.setErrorCode(BBBCatalogErrorCodes.DATA_NOT_FOUND);
          
        }
        catch (final BBBSystemException e) {
        	this.logError("Catalog API Method Name [getFullCategoryVO]: BBBSystemException ", e);
        	categoryVO.setErrorExist(true);
        	categoryVO.setErrorCode(BBBCatalogErrorCodes.NOT_ABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY);
        	categoryVO.setErrorMessage(BBBCatalogConstants.CATEGORY_UNAVAILABLE);
           
        }
        return categoryVO;
    }

    /* START R2.1 Added Site specific Attribute Values List to be shown Scope Item #213 */
    /**
     *  This is to return List of attrbutes values configured at site level if any to be shown as facets.. Return null if
     * nothing is configured in this Property at site level.
     *
     * @param pSiteId the site id
     * @return List<String>
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final List<String> siteAttributeValues(final String pSiteId) throws BBBBusinessException, BBBSystemException {

    	return getSiteRepositoryTools().siteAttributeValues(pSiteId);

    }

    /* END R2.1 Added Site specific Attribute Values List to be shown Scope Item #213 */

    /**
     *  If the SKU does not exist in the system then the method will throw BBBBusinessException with an error code
     * indicating the SKU does not exist If the SKU is not active yet based on Start date and End date method method
     * will throw BBBBusinessException with an error code indicating the SKU is not active If the SKU is not offered
     * online or on the site which is determined based on WEB_OFFERED_FLAG and DISABLE_FLAG the method will set a flag
     * "isSoldOnline" on the SKU as false. The front end needs to handle this flag in Browse and Registry accordingly If
     * the SKU exists in the system, the details are retrieved from the Repository Cache and the VO is prepared and sent
     * to front end If the calculateAboveBelowLine flag is true then the Above or Below Line logic is calculated for
     * each SKU
     *
     * @param siteId the site id
     * @param skuId the sku id
     * @param calculateAboveBelowLine the calculate above below line
     * @param pAddException the add exception
     * @return the SKU details
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final SKUDetailVO getSKUDetails(final String siteId, final String skuId,
                    final boolean calculateAboveBelowLine, final boolean pAddException)
                    throws BBBBusinessException, BBBSystemException {

        this.logDebug("Catalog API Method Name [getSKUDetails] siteId[" + siteId + "] pSkuId[" + skuId + "]");
        if (StringUtils.isEmpty(siteId)) {
            throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                            BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
        }
        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetails");
            final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId,
                            BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
            if (skuRepositoryItem == null) {
                this.logDebug("Repository Item is null for skuId " + skuId);
                throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
            }
            if (!pAddException) {
                return this.getSKUDetailVO(skuRepositoryItem, siteId, calculateAboveBelowLine);
            }
            final boolean isActive = this.isSkuActive(skuRepositoryItem);
            this.logDebug(skuId + " Sku is disabled no longer available");
            if (isActive) {
                return this.getSKUDetailVO(skuRepositoryItem, siteId, calculateAboveBelowLine);
            }
            this.logDebug(skuId + " In Exception Sku is disabled no longer available");
            throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY,
                            BBBCatalogErrorCodes.SKU_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY);
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getSKUDetails]: RepositoryException for sku Id " + skuId);
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);

        } finally {
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetails");
        }

    }

    /**
     *  This API returns the map containing value as the countries name for International Credit Card implementation for
     * 2.1.1. The value are country names and the keys are country code
     *
     * @param pCountryCode the country code
     * @return Country Information
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    public Map<String, String> getCountriesInfoForCreditCard(final String pCountryCode)
                    throws BBBBusinessException, BBBSystemException {

        this.logDebug(" API Method Name getCountriesInfoForCreditCard ");
        final List<String> countryNamList = new ArrayList<String>();
        final List<String> countryCodList = new ArrayList<String>();
        String query = ALL;
        final Object[] param = new Object[1];
        if (!StringUtils.isEmpty(pCountryCode)) {
            query = this.getCountryName();
            param[0] = pCountryCode;
        }

        final Map<String, String> countryMap = new HashMap<String, String>();
        final RepositoryItem[] countryItem = this.executeRQLQuery(query, param,
                        BBBCatalogConstants.BILLING_ADDRESS_COUNTRY_ITEM_DESCRIPTOR,
                        this.getCreditCardCountryRepository());
        if ((countryItem != null) && (countryItem.length > 0)) {
            for (int i = 0; i < countryItem.length; i++) {
                final String country = (String) countryItem[i]
                                .getPropertyValue(BBBCatalogConstants.COUNTRY_NAME_PROPERTY);
                final String countryCode = (String) countryItem[i]
                                .getPropertyValue(BBBCatalogConstants.COUNTRY_CODE_PROPERTY);

                if (BBBUtility.isNotEmpty(country)) {
                    if (BBBUtility.isNotEmpty(countryCode)) {
                        countryNamList.add(country);
                        countryCodList.add(countryCode);

                        countryMap.put(countryCodList.get(i), countryNamList.get(i));
                    } else{
                        this.logDebug("countryCode is null for " + country);
                    }
                } else{
                    this.logDebug("countryNames is null");
                }
            }
        } else {
            this.logDebug("Repository Item is null ");
        }
        BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCountriesInfoForCreditCard");

        return countryMap;
    }
    

   
    /**
     *  This API returns the map containing value as the countries name for International billing implementation
     * The value are country names and the keys are country code.
     *
     * @param pCountryCode the country code
     * @return Country Information
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    public Map<String, String> getCountriesInfo(final String pCountryCode)
                    throws BBBBusinessException, BBBSystemException {

    
        this.logDebug(" BBBCatalogTools.getCountriesInfo() method START");
        
        String query = ALL;
        final Object[] param = new Object[1];
        if (!StringUtils.isEmpty(pCountryCode)) {
            query = this.getCountryName();
            param[0] = pCountryCode;
        }

        final Map<String, String> countryMap = new HashMap<String, String>();
        final RepositoryItem[] countryItem = this.executeRQLQuery(query, param,
                        BBBCatalogConstants.INTL_BILLING_ADDRESS_COUNTRY_ITEM_DESCRIPTOR,
                        this.getInternationalBillingRepository());
        if ((countryItem != null) && (countryItem.length > 0)) {
            for (int i = 0; i < countryItem.length; i++) {
                final String country = (String) countryItem[i]
                                .getPropertyValue(BBBCatalogConstants.COUNTRY_NAME_PROPERTY);
                final String countryCode = (String) countryItem[i]
                                .getPropertyValue(BBBCatalogConstants.COUNTRY_CODE_PROPERTY);
                final Boolean countryEnabled = (Boolean) countryItem[i]
                        		.getPropertyValue(BBBCatalogConstants.IS_ENABLED_PROPERTY);

                if (BBBUtility.isNotEmpty(country) && countryEnabled) {
                    if (BBBUtility.isNotEmpty(countryCode)) {
                        if(isLoggingDebug())
                        {
                        	logDebug("Adding country for ["+country+"] and country code["+countryCode+"]");
                        }

                        countryMap.put(countryCode, country);
                    } else{
                        this.logDebug("BCC Managed countryCode is null for " + country);
                    }
                } else{
                    this.logDebug("BCC Managed countryNames is null");
                }
            }
        } else {
            this.logDebug("Repository Item for BCC Managed country is null ");
        }
        this.logDebug(" BBBCatalogTools.getCountriesInfo() method END");
        BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCountriesInfo");
        
        return countryMap;
    }
    
    
    /**
     * This API returns the list of Country VO for Mobile REST call for International Billing implementation PS-21858.
     * The values are country Name and the country code in the VO.
     *
     * @param pCountryCode the country code
     * @return the intl countries vo list
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    public final List<CountryVO> getIntlCountriesVOList(final String pCountryCode)
    		throws BBBSystemException, BBBBusinessException {

    	BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getCountriesVOList");
    	this.logDebug(" Method BBBCatalogToolsImpl.getCountriesListVo() starts");
    	final List<CountryVO> countriesVOList = new ArrayList<CountryVO>();

    	 Map<String, String> countryMap = this.getCountriesInfo(pCountryCode);
    	countryMap=BBBUtility.sortByComparator(countryMap);
    	
    	if(!BBBUtility.isMapNullOrEmpty(countryMap)){
    		for (Map.Entry entry : countryMap.entrySet()) {
    			countriesVOList.add(new CountryVO((String)entry.getKey(), (String) entry.getValue()));
    		}
    	}
    	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCountriesVOList");
    	this.logDebug("Method BBBCatalogToolsImpl.getCountriesListVo() ends");
    	return countriesVOList;
}
    
    /**
     * This API returns the list of Country VO for Mobile REST call for International Credit Card implementation for
     * 2.1.1. The values are country code and the country code in the VO.
     *
     * @param pCountryCode the country code
     * @return the countries vo list
     * @throws BBBSystemException the BBB system exception
     */
    public final List<CountryVO> getCountriesVOList(final String pCountryCode)
    		throws BBBSystemException {

    	BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getCountriesVOList");
    	this.logDebug(" Method BBBCatalogToolsImpl.getCountriesListVo() starts");
    	final List<CountryVO> countriesVOList = new ArrayList<CountryVO>();
    	final List<String> countryNameList = new ArrayList<String>();
        final List<String> countryCodeList = new ArrayList<String>();
    	
    	String query = ALL;
    	final Object[] param = new Object[1];
    	if (!StringUtils.isEmpty(pCountryCode)) {
    		query = this.getCountryName();
    		param[0] = pCountryCode;
    	}

    	final Map<String, String> countryMap = new HashMap<String, String>();
    	final RepositoryItem[] countryItem = this.executeRQLQuery(query, param,
    			BBBCatalogConstants.BILLING_ADDRESS_COUNTRY_ITEM_DESCRIPTOR,
    			this.getCreditCardCountryRepository());
    	if ((countryItem != null) && (countryItem.length > 0)) {
    		for (int i = 0; i < countryItem.length; i++) {
    			final String country = (String) countryItem[i]
    					.getPropertyValue(BBBCatalogConstants.COUNTRY_NAME_PROPERTY);
    			final String countryCode = (String) countryItem[i]
    					.getPropertyValue(BBBCatalogConstants.COUNTRY_CODE_PROPERTY);

    			if (BBBUtility.isNotEmpty(country)) {
    				if (BBBUtility.isNotEmpty(countryCode)) {
    					countryNameList.add(country);
                        countryCodeList.add(countryCode);

                        countryMap.put(countryCodeList.get(i), countryNameList.get(i));
    				} else {
    					this.logDebug("countryCode is null for " + country);
    				}
    			} else {
    				this.logDebug("countryNames is null");
    			}
    		}
    	} else {
    		this.logDebug("Repository Item is null ");
    	}
    	if(!countryMap.isEmpty()){
    		for (Map.Entry entry : countryMap.entrySet()) {
    			countriesVOList.add(new CountryVO((String)entry.getKey(), (String) entry.getValue()));
    		}
    	}
    	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCountriesVOList");
    	this.logDebug("Method BBBCatalogToolsImpl.getCountriesListVo() ends");
    	return countriesVOList;
}

    /**
     *  R2.1.1 : Ask and Answer and Chat Story Changes This API returns Set of Repository Items of Categories associated.
     * with the Product on the corresponding Site
     *
     * @param parentCategorySet the parent category set
     * @param siteId the site id
     * @return Set<RepositoryItem>
     */
    private Set<RepositoryItem> getAllCategoryForSite(final Set<RepositoryItem> parentCategorySet, final String siteId) {
        final Set<RepositoryItem> categoryRepositoryItem = new LinkedHashSet<RepositoryItem>();
        if ((parentCategorySet != null) && !parentCategorySet.isEmpty()) {
            this.logTrace("Parent category set is not null values for parent category::" + parentCategorySet);
            for (final RepositoryItem catRepo : parentCategorySet) {
                @SuppressWarnings ("unchecked")
                final Set<String> catSiteId = (Set<String>) catRepo.getPropertyValue("siteIds");
                this.logTrace("sites applicable for potential parent with id::" + catRepo.getRepositoryId() + " are "
                                + catSiteId + " and the current site id is " + siteId);
                if (catSiteId.contains(siteId)) {
                    categoryRepositoryItem.add(catRepo);
                    this.logTrace("Category id " + catRepo.getRepositoryId() + " is the selected parent ");
                }
            }
        } else {
            this.logTrace("Parent category set is NULL  ");
        }
        return categoryRepositoryItem;
    }

    /**
     *  R2.1.1 : Ask and Answer and Chat Story Changes.
     * This API returns a Map of All the Categories associated with the Product on the corresponding Site.
     *
     * @param productId the product id
     * @param siteId the site id
     * @return Map<String,CategoryVO>
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public  Map<String, CategoryVO> getAllParentCategoryForProduct(final String productId, final String siteId)
                    throws BBBBusinessException, BBBSystemException {
        this.logDebug("Catalog API Method Name [getAllParentCategoryForProduct] productId[" + productId + "]");
        final Map<String, CategoryVO> parentCategoryMap = new LinkedHashMap<String, CategoryVO>();

        if (BBBUtility.isNotEmpty(productId)) {
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL
                                + " getAllParentCategoryForProduct");
                final RepositoryItem productRepositoryItem = this.getCatalogRepository().getItem(productId,
                                BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);

                if (productRepositoryItem != null) {
                    this.logDebug("productRepositoryItem is not null for product id " + productId);
                    @SuppressWarnings ("unchecked")
                    final Set<RepositoryItem> parentCategorySet = (Set<RepositoryItem>) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.PARENT_CATEGORIES_PROPERTY_NAME);
                    final Set<RepositoryItem> categoryRepositoryItemList = this.getAllCategoryForSite(
                                    parentCategorySet, siteId);
                    int count = 0;
                    if ((categoryRepositoryItemList != null) && !categoryRepositoryItemList.isEmpty()) {
                        for (final RepositoryItem catRepoItem : categoryRepositoryItemList) {
                            final CategoryVO categoryVO = new CategoryVO();
                            final String categoryId = catRepoItem.getRepositoryId();
                            this.logDebug(categoryId
                                            + " category id with display name "
                                            + (String) catRepoItem
                                                            .getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME)
                                            + " is the " + count + " parent for product id " + productId);
                            categoryVO.setCategoryId(categoryId);
                            categoryVO.setCategoryName((String) catRepoItem
                                            .getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME));
                            if (catRepoItem.getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME) != null) {
                                categoryVO.setIsCollege((Boolean) catRepoItem
                                                .getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME));
                            }
                            if (catRepoItem.getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY) != null) {
                                categoryVO.setPhantomCategory(((Boolean) catRepoItem
                                                .getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY)));
                            }
                            parentCategoryMap.put(Integer.toString(count), categoryVO);
                            count++;
                        }
                    } else {
                        this.logDebug("Product Id " + productId + " does not have any parent for site " + siteId);
                    }
                } else {
                    this.logDebug("Product Id " + productId + " is not present in the repository");
                }

            } catch (final RepositoryException e) {
                this.logError("Catalog API Method Name [getAllParentCategoryForProduct]: RepositoryException ", e);
            } finally {
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getAllParentCategoryForProduct");
            }
        } else {
            this.logDebug("Product Id passed is null");
        }
        return parentCategoryMap;
    }

    /**
     *  R2.1.1 : Ask and Answer and Chat Story Changes This method returns the categoryId based on criteria. If 'ANY'
     * then the Id of first category which has chat enabled is returned. If 'FIRST' the the Id of first category in the
     * list is returned.
     *
     * @param pCategoryList the category list
     * @param pCriteria the criteria
     * @return String
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    protected String chatEnabledInCategory(final Collection<CategoryVO> pCategoryList, final String pCriteria)
                    throws BBBSystemException, BBBBusinessException {
        this.logDebug("Entering Catalog API Method Name [chatEnabledInCategory]");
        String categoryId = BBBCatalogConstants.CATEGORY_ID_BLANK;

        if (!BBBUtility.isEmpty(pCriteria) && !(pCategoryList.isEmpty())) {
            this.logDebug("[Criteria =" + pCriteria + "]");

            for (final CategoryVO categoryVO : pCategoryList) {
                this.getBccManagedCategory(categoryVO);
                if (BBBCatalogConstants.CRITERIA_ANY.equalsIgnoreCase(pCriteria)) {
                    if ((null != categoryVO.getIsChatEnabled()) && categoryVO.getIsChatEnabled().booleanValue()) {
                        categoryId = categoryVO.getCategoryId();
                        break;
                    }
                } else if (BBBCatalogConstants.CRITERIA_FIRST.equalsIgnoreCase(pCriteria)) {
                    categoryId = categoryVO.getCategoryId();
                    break;
                }
            }
        } else {
            this.logDebug("Bad Criteria or Empty Category List");
        }

        this.logDebug("Exiting Catalog API Method Name [chatEnabledInCategory] [categoryId=" + categoryId + "]");
        return categoryId;
    }

    /**
     *  R2.1.1 : Ask and Answer and Chat Story Changes This method returns the categoryId based on criteria. If 'ANY'
     * then the Id of first category which has displayAskandAnswer as 'Show' is returned. If 'FIRST' the the Id of first
     * category in the list is returned.
     *
     * @param pCategoryList the category list
     * @param pCriteria the criteria
     * @return String
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    private String displayAskAndAnswerEnabledInCategory(final Collection<CategoryVO> pCategoryList,
                    final String pCriteria) throws BBBSystemException, BBBBusinessException {
        this.logDebug("Entering Catalog API Method Name [displayAskAndAnswerEnabledInCategory]");
        String categoryId = BBBCatalogConstants.CATEGORY_ID_BLANK;

        if (!BBBUtility.isEmpty(pCriteria) && !(pCategoryList.isEmpty())) {
            this.logDebug("[Criteria =" + pCriteria + "]");

            for (final CategoryVO categoryVO : pCategoryList) {
                this.getBccManagedCategory(categoryVO);
                if (BBBCatalogConstants.CRITERIA_ANY.equalsIgnoreCase(pCriteria)) {
                    if (!(BBBUtility.isEmpty(categoryVO.getDisplayAskAndAnswer()))
                                    && !(BBBCatalogConstants.ASK_ANSWER_DEFAULT_VALUE.equalsIgnoreCase(categoryVO.getDisplayAskAndAnswer()))) {
                        categoryId = categoryVO.getCategoryId();
                        break;
                    }
                } else if (BBBCatalogConstants.CRITERIA_FIRST.equalsIgnoreCase(pCriteria)) {
                    categoryId = categoryVO.getCategoryId();
                    break;
                }
            }
        } else {
            this.logDebug("Bad Criteria or Empty Category List");
        }

        this.logDebug("Exiting Catalog API Method Name [displayAskAndAnswerEnabledInCategory] [categoryId="
                        + categoryId + "]");
        return categoryId;
    }

	/**
	 * Grid and list enabled in category.
	 *
	 * @author psin52 Added for story 117-A5 | Grid/List view configurable
	 * @param pCategoryList the category list
	 * @return the category vo
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */

	/**
	 * @param pCategoryList List of catagory.
	 * @return CategoryVo.
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private CategoryVO gridAndListEnabledInCategory(
			final Collection<CategoryVO> pCategoryList)
			throws BBBSystemException, BBBBusinessException {
		this.logDebug("Entering Catalog API Method Name [gridAndListEnabledInCategory]");
		String categoryId = BBBCatalogConstants.CATEGORY_ID_BLANK;
		CategoryVO category = null;
		if (!(pCategoryList.isEmpty())) {

			for (final CategoryVO categoryVO : pCategoryList) {

				this.getBccManagedCategory(categoryVO);

				if (!BBBUtility.isEmpty(categoryVO.getDefaultViewValue()))
				{
					category = categoryVO;

					break;
				}

			}
		} else {
			this.logDebug("Empty Category List");
			this.logDebug("Exiting Catalog API Method Name [gridAndListEnabledInCategory] [categoryId="
					+ categoryId + "]");
		}

		return category;
	}

	/**
	 * PDP attributes.
	 *
	 * @author psin52 Added for story 117-A5 | Grid/List view configurable | END
	 * @param pProductId the product id
	 * @param pCategoryId the category id
	 * @param pPoc the poc
	 * @param pSiteId the site id
	 * @return the PDP attributes vo
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */

	 
    /** R2.1.1 : Ask and Answer and Chat Story Changes This method returns the PDPAttributesVO for the Product passed in
     * parameter. This is used on PDP to evaluate Enable chat and Display Ask and Answer Logic.
     *
     * @param pCategoryList
     * @param pCriteria
     * @param pSiteId
     * @return PDPAttributesVO
     * @throws BBBSystemException
     * @throws BBBBusinessException */
    @Override
    public final PDPAttributesVO PDPAttributes(final String pProductId, final String pCategoryId, final String pPoc,
                    final String pSiteId) throws BBBBusinessException, BBBSystemException {
        this.logDebug("Entering Catalog API Method Name [PDPAttributes]");

        final PDPAttributesVO pdpAttributesVo = new PDPAttributesVO();
        Map<String, CategoryVO> parentCategoryMap = new HashMap<String, CategoryVO>();
        CategoryVO catVo = new CategoryVO();

        boolean siteChatOnOffFlag = false;
        boolean pdpChatOnOffFlag = false;
        boolean pdpChatOverrideFlag = false;
        boolean pdpDaasOnOffFlag = false;
        boolean pdpDaasOverrideFlag = false;

        boolean isChatEnabled = false;
        boolean isDisplayAskAndAnswerEnabled = false;

        String categoryId = BBBCatalogConstants.CATEGORY_ID_BLANK;

        if (!(BBBUtility.isEmpty(pProductId)) && !(BBBUtility.isEmpty(pSiteId))) {
            this.logDebug("Parameters value[productId=" + pProductId + "][siteId=" + pSiteId + "]" + "][pCategoryId="
                            + pCategoryId + "]" + "][pPoc=" + pPoc + "]");

            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " PDPAttributes");

                SiteChatAttributesVO siteChatAttributesVO = this.getSiteChatAttributes(pSiteId);

                siteChatOnOffFlag = siteChatAttributesVO.isOnOffFlag();
                pdpChatOnOffFlag = siteChatAttributesVO.isChatFlagPDP();
                pdpChatOverrideFlag = siteChatAttributesVO.isChatOverrideFlagPDP();
                pdpDaasOnOffFlag = siteChatAttributesVO.isDaasFlagPDP();
                pdpDaasOverrideFlag = siteChatAttributesVO.isDaasOverrideFlagPDP();

                // Fix for RM #21038, RM #21039
                String categoryID = null;

                if (BBBUtility.isEmpty(pPoc)) {
                    categoryID = pCategoryId;
                }

                if (!BBBUtility.isEmpty(categoryID)) {
                    catVo = this.getCategoryDetail(pSiteId, categoryID,false);
                    this.getBccManagedCategory(catVo);
                }

                if (siteChatOnOffFlag) {
                    if (pdpChatOnOffFlag) {
                        if (pdpChatOverrideFlag) {
                            parentCategoryMap = this.getAllParentCategoryForProduct(pProductId, pSiteId);
                            categoryId = this.chatEnabledInCategory(parentCategoryMap.values(),
                                            BBBCatalogConstants.CRITERIA_FIRST);
                            isChatEnabled = true;
                        } else {
                            if (BBBUtility.isEmpty(categoryID)) {
                                parentCategoryMap = this.getAllParentCategoryForProduct(pProductId, pSiteId);
                                categoryId = this.chatEnabledInCategory(parentCategoryMap.values(),
                                                BBBCatalogConstants.CRITERIA_ANY);
                                if (!BBBCatalogConstants.CATEGORY_ID_BLANK.equalsIgnoreCase(categoryId)) {
                                    isChatEnabled = true;
                                }
                            } else {
                                if ((catVo != null) && (catVo.getIsChatEnabled() != null)) {
                                    isChatEnabled = catVo.getIsChatEnabled().booleanValue();
                                } else {
                                    isChatEnabled = false;
                                }
                                categoryId = categoryID;
                            }
                        }
                    } else {
                        this.logDebug("Chat Disabled for PDP Page");
                    }
                } else {
                    this.logDebug("Chat Disabled for the Entire Site [siteId=" + pSiteId + "]");
                }

                if (pdpDaasOnOffFlag) {
                    if (pdpDaasOverrideFlag) {
                        isDisplayAskAndAnswerEnabled = true;
                    } else {
                        if (BBBUtility.isEmpty(categoryID)) {
                            parentCategoryMap = this.getAllParentCategoryForProduct(pProductId, pSiteId);
                            final String category = this.displayAskAndAnswerEnabledInCategory(
                                            parentCategoryMap.values(), BBBCatalogConstants.CRITERIA_ANY);
                            if (!BBBCatalogConstants.CATEGORY_ID_BLANK.equalsIgnoreCase(category)) {
                                isDisplayAskAndAnswerEnabled = true;
                            }
                        } else {
                            isDisplayAskAndAnswerEnabled = !(BBBCatalogConstants.ASK_ANSWER_DEFAULT_VALUE.equalsIgnoreCase((null != catVo)?catVo.getDisplayAskAndAnswer():""));
                        }
                    }
                } else {
                    this.logDebug("Ask and Answer Disabled for PDP Page");
                }
				
				// // Added for 117-A5 Story R2.2 |Grid/List View Configurable | Start @psin52
				

				if(catVo != null && catVo.getDefaultViewValue() != null){
					pdpAttributesVo.setDefaultDisplayType(catVo.getDefaultViewValue());
				}else if (catVo == null || catVo.getDefaultViewValue() == null){
					parentCategoryMap = this.getAllParentCategoryForProduct(pProductId, pSiteId);
					final CategoryVO categoryVO = this.gridAndListEnabledInCategory(parentCategoryMap.values());
					if(categoryVO != null){
						pdpAttributesVo.setDefaultDisplayType(categoryVO.getDefaultViewValue());
					}else{
						pdpAttributesVo.setDefaultDisplayType(this.getGridListAttributes(pSiteId));
					}
				}
				
				// Added for 117-A5 Story R2.2 |Grid/List View Configurable  | End 

                pdpAttributesVo.setCategoryId(categoryId);
                pdpAttributesVo.setChatEnabled(isChatEnabled);
                pdpAttributesVo.setAskAndAnswerEnabled(isDisplayAskAndAnswerEnabled);
                pdpAttributesVo.setSiteChatOnOffFlag(siteChatOnOffFlag);
                pdpAttributesVo.setPdpChatOnOffFlag(pdpChatOnOffFlag);
                pdpAttributesVo.setPdpChatOverrideFlag(pdpChatOverrideFlag);
                pdpAttributesVo.setPdpDaasOnOffFlag(pdpDaasOnOffFlag);
                pdpAttributesVo.setPdpDaasOverrideFlag(pdpDaasOverrideFlag);
            } catch (final BBBSystemException e) {
                this.logError("Catalog API Method Name [PDPAttributes]: BBBSystemException ", e);
            } catch (final BBBBusinessException e) {
                this.logError("Catalog API Method Name [PDPAttributes]: BBBBusinessException ", e);
            } finally {
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " PDPAttributes");
            }
        }
        this.logDebug("Exiting Catalog API Method Name [PDPAttributes]");
        return pdpAttributesVo;
    }

    /**
     *  R2.1.1 : Ask and Answer and Chat Story Changes.
     * This method returns whether the Global Chat On/Off Flag is set for the site Id.
     *
     * @param pSiteId the site id
     * @return Boolean
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final boolean checkGlobalChat(final String pSiteId) throws BBBSystemException {
        this.logDebug("Entering Catalog API Method Name [checkGlobalChat]");

        boolean siteChatOnOffFlag = false;

        if (!(BBBUtility.isEmpty(pSiteId))) {
            this.logDebug("Parameters value [siteId=" + pSiteId + "]");

            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " checkGlobalChat");

            SiteChatAttributesVO siteChatAttributesVO = this.getSiteChatAttributes(pSiteId);
            siteChatOnOffFlag = siteChatAttributesVO.isOnOffFlag();
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " checkGlobalChat");
        }
        this.logDebug("Exiting Catalog API Method Name [checkGlobalChat]");
        return siteChatOnOffFlag;
    }

    /**
     *  R2.1.1 : Ask and Answer and Chat Story Changes.
     *  This method returns the Category Id to be passed in the Pricing
     * message Vo to be shown for Shop similar items
     *
     * @param pProductId the product id
     * @param pSiteId the site id
     * @return String
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public final String getShopSimilarItemCategory(final String pProductId, final String pSiteId)
                    throws BBBSystemException, BBBBusinessException {
        this.logDebug("Entering Catalog API Method Name [getShopSimilarItemCategory]");
        String categoryId = BBBCatalogConstants.CATEGORY_ID_BLANK;

        if (!BBBUtility.isEmpty(pProductId)) {
            this.logDebug("Parameters value[productId=" + pProductId + "][siteId=" + pSiteId + "]");
            String primaryCat = this.getPrimaryCategory(pProductId);
            final Set<String> similarProductsIgnoreList = this.getSimilarProductsIgnoreList(pSiteId);
            final Map<String, CategoryVO> parentCategoryMap = this.getAllParentCategoryForProduct(pProductId, pSiteId);
            final Collection<CategoryVO> categoryList = parentCategoryMap.values();

            if ((null == similarProductsIgnoreList) || similarProductsIgnoreList.isEmpty()) {
                if (BBBUtility.isEmpty(primaryCat)) {
                    if (!categoryList.isEmpty()) {
                        for (final CategoryVO categoryVO : categoryList) {
                            categoryId = categoryVO.getCategoryId();
                            break;
                        }
                    }
                } else {
                    categoryId = primaryCat;
                }
            } else {
                if (similarProductsIgnoreList.contains(primaryCat)) {
                    primaryCat = BBBCatalogConstants.CATEGORY_ID_BLANK;
                }
                if (BBBUtility.isEmpty(primaryCat)) {
                    if (!categoryList.isEmpty()) {
                        for (final CategoryVO categoryVO : categoryList) {
                            if (!similarProductsIgnoreList.contains(categoryVO.getCategoryId())) {
                                categoryId = categoryVO.getCategoryId();
                                break;
                            }
                        }
                    }
                } else {
                    categoryId = primaryCat;
                }
            }
        } else {
            this.logDebug("Product Id passed is null");
        }
        if (BBBCatalogConstants.CATEGORY_ID_BLANK.equalsIgnoreCase(categoryId)) {
            this.logDebug("Shop Similar Link Disabled");
            categoryId = EMPTYSTRING;
        }
        this.logDebug("Exiting Catalog API Method Name [getShopSimilarItemCategory]");
        return categoryId;
    }

    /**
     *  R2.1.1 : Ask and Answer and Chat Story Changes.
     *  This method returns a Set of Category Ids that are to be ignores
     * for Shop Similar Link for that Site.
     *
     * @param siteId the site id
     * @return Set<String>
     * @throws BBBSystemException the BBB system exception
     */
    private Set<String> getSimilarProductsIgnoreList(final String siteId) throws BBBSystemException {
    	
    	return getSiteRepositoryTools().getSimilarProductsIgnoreList(siteId);
    }

    // R2.1.1 : Shop Similar Item Link Changes - END

    /**
     *  Returns a list of state Codes that are bopus disabled. If input parameter is null then BBBBusinessException is
     * thrown
     *
     * @return the bopus disabled states
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    @Override
    public final List<String> getBopusDisabledStates() throws BBBBusinessException, BBBSystemException {
    	
    	return getShippingRepositoryTools().getBopusDisabledStates();

    }

    /**
     *  The method gives the expected min and max date string when a customer can expect teh delivery if time at which
     * order is submit is past the cutoff time for the shipping group then min expected date by which order is expected
     * will be (current date +1 + min days to ship) else it will be (current date+min days to ship) @ -8505,7 +8551,36 @@
     * return bopusInEligibleStores; } /** Get stores which are bopus disabled.
     *
     * @return BOPUS Disabled Stores
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    @Override
    public final List<String> getBopusDisabledStores() throws BBBSystemException, BBBBusinessException {
    	
    	return getStoreRepositoryTools().getBopusDisabledStores();

    }
    
    /**
     *  The method return the College Name and Id corresponding to given state .
     *
     * @param pStateCode the state code
     * @return collegeList
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
	public List<CollegeVO> getCollegesByState(String pStateCode)
			throws BBBSystemException, BBBBusinessException {
		
		return getSchoolRepositoryTools().getCollegesByState(pStateCode);

	}

    /**
     * Gets the bopus disabled store query.
     *
     * @return Bopus Disabled Store Query
     */
    public final String getBopusDisabledStoreQuery() {
        return this.bopusDisabledStoreQuery;
    }

    /**
     * Sets the bopus disabled store query.
     *
     * @param bopusDisabledStoreQuery the new bopus disabled store query
     */
    public final void setBopusDisabledStoreQuery(final String bopusDisabledStoreQuery) {
        this.bopusDisabledStoreQuery = bopusDisabledStoreQuery;
    }

    /**
     * Gets the bopus disalbed state query.
     *
     * @return Bopus disabled state query
     */
    public final String getBopusDisalbedStateQuery() {
        return this.bopusDisalbedStateQuery;
    }

    /**
     * Sets the bopus disalbed state query.
     *
     * @param bopusDisalbedStateQuery the new bopus disalbed state query
     */
    public final void setBopusDisalbedStateQuery(final String bopusDisalbedStateQuery) {
        this.bopusDisalbedStateQuery = bopusDisalbedStateQuery;
    }
    
	/* (non-Javadoc)
	 * @see com.bbb.commerce.catalog.BBBCatalogTools#getCategoryRepDetail(java.lang.String, java.lang.String)
	 */
	@Override
	/**
	 * The method gets category details in the form of CategoryVO
	 * If the category does not exist in the repository throw BBBBusinessException indicating the error code
	 * Check if product is disabled  throw BBBBusinessException indicating the error code
	 * Check if product is a collection or a lead sku
	 * If isCollection or isLeadSu is true product is a collection and has child products so return collectionVO
	 * if product is a collection it doesnot have child sku or rollup types or attributes
	 * if product is a lead sku it has child sku,rolluptypes and attributes
	 *
	 *
	 */

	public RepositoryItem getCategoryRepDetail(String pSiteId, String pCategoryId) {
		
			final StringBuilder debug = new StringBuilder(50);
			debug.append("Catalog API Method Name [isFixedPriceShipping] siteId[").append(pSiteId).append("] pCategoryId [").append(pCategoryId);
			logDebug(debug.toString());
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL
				+ " getCategoryDetail");
		try {
			RepositoryView repositoryView = getCatalogRepository().getView(BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
			QueryBuilder queryBuilder = repositoryView.getQueryBuilder();
			QueryExpression expressionProperty = queryBuilder.createPropertyQueryExpression("id");
			QueryExpression expressionValue = queryBuilder.createConstantQueryExpression(pCategoryId);

			Query query = queryBuilder.createComparisonQuery(expressionProperty, expressionValue, QueryBuilder.EQUALS);
			RepositoryItem[] items = repositoryView.executeQuery(query);
			if (items == null || items.length == 0) {
				logError("Catalog API Method Name [getCategoryDetail]: BBBBusinessException " + BBBCatalogErrorCodes.CATEGORY_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY);
			} else {
				RepositoryItem categoryRepositoryItem = items[0];
				final Boolean isActive = isCategoryActive(categoryRepositoryItem);
				logTrace(pCategoryId + " isActive " + isActive);
				if (!isActive) {
					logError("Catalog API Method Name [getCategoryDetail]: BBBBusinessException "+ BBBCatalogErrorCodes.CATEGORY_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY);
				}
				return categoryRepositoryItem;
			}

		} catch (RepositoryException e) {
			logError("Catalog API Method Name [getCategoryDetail]: RepositoryException ", e);

		} finally {
			debug.append(" Exit");
        	this.logDebug(debug.toString());
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCategoryDetail");
		}
		return null;

	}

	/**
	 * Gets the brand id query.
	 *
	 * @return the brand id query
	 */
	public String getBrandIdQuery() {
		return brandIdQuery;
	}

	/**
	 * Sets the brand id query.
	 *
	 * @param brandIdQuery the new brand id query
	 */
	public void setBrandIdQuery(String brandIdQuery) {
		this.brandIdQuery = brandIdQuery;
	}

	/**
	 * R2.2 story 178-A4. Product Comparison page. This catalog method will 
	 * update the Product VO with the attributes required on the product 
	 * comparison page for products.
	 * If site id or product id is empty, it will return a null VO.
	 *
	 * @param productId the product id
	 * @param siteId the site id
	 * @param compareProductEntryVO the compare product entry vo
	 * @return ProductVO
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	@Override
	public ProductVO getComparisonProductDetails(String productId, String siteId, CompareProductEntryVO compareProductEntryVO) throws BBBBusinessException, BBBSystemException {

		this.logDebug("BBBCatalogTools.getComparisonProductDetails() method starts");
		if (!StringUtils.isEmpty(siteId) && !StringUtils.isEmpty(productId)) {
			ProductVO productVO = new ProductVO();
			try {
				RepositoryItem productRepositoryItem = getCatalogRepository().getItem(productId, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
				if(productRepositoryItem == null){
					this.logError("Product is not available in repository for product id " + productId);
					throw new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
							BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);
				}

				this.logDebug("updating the product VO for product id : " + productId + "in catalog API for product comparison page");
				productVO.setProductId(productId);
				//check if product is active
				boolean isActive = this.isProductActive(productRepositoryItem);
				String isIntlRestricted = BBBCoreConstants.NO_CHAR;
		        if ( !BBBUtility.isEmpty((String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED))) {
		        	isIntlRestricted = (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED);
		        }
		      	productVO.setIntlRestricted(BBBCoreConstants.YES_CHAR.equalsIgnoreCase(isIntlRestricted));
				productVO.setDisabled(Boolean.valueOf(isActive));

				boolean isCollection = false;
				boolean isLeadProduct = false;
				boolean isLtlProduct = false;
				
				if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) != null) {
					isCollection = ((Boolean) productRepositoryItem
							.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME))
							.booleanValue();
				}
				if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) != null) {
					isLeadProduct = ((Boolean) productRepositoryItem
							.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME))
							.booleanValue();
				}
				if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) != null) {
					isLtlProduct = ((Boolean) productRepositoryItem
							.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME))
							.booleanValue();
				}

				//set collection flag to true if product is collection product
				if(isCollection)
					productVO.setCollection(true);
				
				// Set LTL flag to true if Product is LTL Product 
				if(isLtlProduct)
					productVO.setLtlProduct(true);
				
				//set thumbnail and medium images for product
				ImageVO productImage = new ImageVO();
				if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME) != null) {
					final String mediumImagePath = (String) productRepositoryItem
							.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME);
					productImage.setMediumImage(mediumImagePath);
				}
				if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME)!=null){
					final String thumbnailImagePath= (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME);
					productImage.setThumbnailImage(thumbnailImagePath);
				}
				productVO.setProductImages(productImage);

				//fetch product name
				if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME) != null) {
					productVO.setName((String) productRepositoryItem
							.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME));
				}

				//fetch product short description
				if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME) != null) {
					productVO.setShortDescription((String) productRepositoryItem
							.getPropertyValue(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME));
				}

				//fetch product long description
				if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME) != null) {
					productVO.setLongDescription((String) productRepositoryItem
							.getPropertyValue(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME));
				}

				//fetch product bazaar voice reviews
				final BazaarVoiceProductVO bvReviews = this.getBazaarVoiceDetails(productId, siteId);
				productVO.setBvReviews(bvReviews);

				//fetch products prices
				if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME) != null){
					productVO.setPriceRangeDescription((String) productRepositoryItem
							.getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME));
					productVO.setPriceRangeDescriptionRepository((String) productRepositoryItem
							.getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME));
					productVO.setDefaultPriceRangeDescription((String) productRepositoryItem
							.getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME));
				}

				if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME) != null) {
					productVO.setSkuLowPrice((String) productRepositoryItem
							.getPropertyValue(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME));
				}
				if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME) != null) {
					productVO.setSkuHighPrice((String) productRepositoryItem
							.getPropertyValue(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME));
				}
				
				productVO = this.updatePriceDescription(productVO);
				
				

				final List<RepositoryItem> skuRepositoryItems = (List<RepositoryItem>) productRepositoryItem
						.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);

				//set collection & multisku flag to false if product is lead or single sku product. Also set multiSku flag to true if product is MSWP
				if((!isCollection && skuRepositoryItems != null) && (BBBUtility.isEmpty(compareProductEntryVO.getSkuId()))){
					if(skuRepositoryItems.size() > 1){
						this.logDebug("The product with " + productId + " is a MSWP");
						compareProductEntryVO.setMultiSku(true);
					} else {
						List<String> childSkuId = new ArrayList<String>();
						childSkuId.add(skuRepositoryItems.get(0).getRepositoryId());
						RepositoryItem skuRepositoryItem=null;
						try {
							skuRepositoryItem = getCatalogRepository().getItem(skuRepositoryItems.get(0).getRepositoryId(), BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
							if(skuRepositoryItem!=null && skuRepositoryItem.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) != null)
							{
								compareProductEntryVO.setLtlProduct((Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME));
								productVO.setLtlProduct((Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME));
							}
						} catch (RepositoryException e) {
						    this.logError("Repository exception in fetching the sku details for product id " + skuRepositoryItems.get(0).getRepositoryId());
							throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
									BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
						}

						
						productVO.setChildSKUs(childSkuId);
						productVO.setCollection(false);
						compareProductEntryVO.setMultiSku(false);
					}
				}
				if(!BBBUtility.isEmpty(compareProductEntryVO.getSkuId()))
				{
					compareProductEntryVO.setMultiSku(false);
                    compareProductEntryVO.setCollection(false);
				}

				//fetch promotional attributes for the product only if product is collection, multi sku or LTL product.
				if((productVO.isCollection() || compareProductEntryVO.isMultiSku() || productVO.isLtlProduct()) && (BBBUtility.isEmpty(compareProductEntryVO.getSkuId()))){
						
					Map<String, RepositoryItem> attributeNameRepoItemMap = new HashMap<String, RepositoryItem>();
					attributeNameRepoItemMap = this.getProductAttributeList(productRepositoryItem, siteId, attributeNameRepoItemMap);
					if(!BBBUtility.isMapNullOrEmpty(attributeNameRepoItemMap)){
						Map<String, AttributeVO> prodAttributes = this.getSiteLevelAttributes(siteId, attributeNameRepoItemMap);
						
						// Remove LTL Attributes from Promotional Attributes and set in LTL attribute List which need to be displayed in Shipping Information
						if (!BBBUtility.isMapNullOrEmpty(prodAttributes)) {
						
							List<String> ltlAttributesList = BBBConfigRepoUtils.getAllValues(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.LTL_DELIVERY_ATTRIBUTES_LIST);
							Map<String,AttributeVO> mapLtlAttribute = null;
							List<String> removingList=new ArrayList();
							for(Map.Entry entry : prodAttributes.entrySet()){
								String attributeId = (String) entry.getKey();
							
								if (!BBBUtility.isListEmpty(ltlAttributesList) && ltlAttributesList.contains(attributeId)){
									mapLtlAttribute = new HashMap<String, AttributeVO>();
									compareProductEntryVO.setVdcSkuFlag(BBBCoreConstants.COMPARE_PRODUCT_YES);
									compareProductEntryVO.setLtlAttributeApplicable(true);
									removingList.add(attributeId);
									RepositoryItem repItem = attributeNameRepoItemMap.get(attributeId);
									String descName = repItem.getPropertyValue("displayDescrip").toString();
									String actionURl = (String) repItem.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME);
									String imageURl = (String)repItem.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME);
									AttributeVO ltlAttribute = new AttributeVO();
										ltlAttribute.setAttributeDescrip(descName);
										ltlAttribute.setActionURL(actionURl);
										ltlAttribute.setImageURL(imageURl);
										mapLtlAttribute.put(repItem.getRepositoryId() , ltlAttribute);
									compareProductEntryVO.setLtlAttributesList(mapLtlAttribute);
								}
							}
							if(null != compareProductEntryVO &&  null != prodAttributes.entrySet()){
								for(Iterator<Map.Entry<String,AttributeVO>>it=prodAttributes.entrySet().iterator();it.hasNext();)
								{
									Map.Entry<String,AttributeVO> entry = it.next();
									String 	attributeId=	entry.getKey();
									if(!BBBUtility.isListEmpty(removingList) && removingList.contains(attributeId))
									{
										it.remove();
									}
								}	
							}							
							compareProductEntryVO.setAttributesList(prodAttributes);
						}
					}
				}

				this.logDebug("BBBCatalogTools.getComparisonProductDetails() method ends");
				return productVO;
			} catch (RepositoryException exp) {
				this.logError("Repository exception in fetching the product details for product id " + productId);
				throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, exp);
			}
		}
		this.logError("Product id or site id is null hence returning null VO from BBBCatalogTools.getComparisonProductDetails()");
		return null;
	}

	/**
	 * R2.2 story 178-A4. Product Comparison page. This catalog method will 
	 * update the SKU detail VO with the attributes required on the product 
	 * comparison page if product is a single sku or lead product.
	 * If site id or sku id is empty, it will throw an exception.
	 *
	 * @param siteId the site id
	 * @param skuId the sku id
	 * @param calculateAboveBelowLine the calculate above below line
	 * @param compareProductEntryVO the compare product entry vo
	 * @return SKUDetailVO
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	@Override
	public SKUDetailVO getComparisonSKUDetails(String siteId, String skuId,
			boolean calculateAboveBelowLine, CompareProductEntryVO compareProductEntryVO) throws BBBSystemException, BBBBusinessException {

		this.logDebug("BBBCatalogTools.getComparisonSKUDetails() method siteId [" + siteId + "] pSkuId[" + skuId + "] starts ");
		if (!StringUtils.isEmpty(siteId) && !StringUtils.isEmpty(skuId)) {
			try {
				RepositoryItem skuRepositoryItem = getCatalogRepository().getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
				if(skuRepositoryItem == null){
					this.logError("SKU is not available in repository for sku id " + skuId);
					throw new BBBBusinessException (BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
				}

				this.logDebug("Updating the SKUDetailVO for the sku id " + skuId + "in catalog API for product comparison page");
				SKUDetailVO skuDetailVO = new SKUDetailVO(skuRepositoryItem);
				boolean isActive = this.isSkuActive(skuRepositoryItem);
				skuDetailVO.setActiveFlag(isActive);

				this.logDebug("SKU active ? : " + isActive);
				if (calculateAboveBelowLine) {
					skuDetailVO.setSkuBelowLine(this.isSKUBelowLine(siteId, skuId));
				}
				
				compareProductEntryVO.setEmailAlertOn(skuDetailVO.getEmailStockAlertsEnabled());
				
            	boolean currentZipEligibility = false;
            	String regionPromoAttr = BBBCoreConstants.BLANK;
            	boolean sameDayDeliveryFlag = false;
        		String sddEligibleOn = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG);
        		
        		if(null != sddEligibleOn){
        			sameDayDeliveryFlag = Boolean.valueOf(sddEligibleOn);
        		}
        		if(sameDayDeliveryFlag) {
        			BBBSessionBean sessionBean = (BBBSessionBean) resolveComponentFromRequest(BBBCoreConstants.SESSION_BEAN);
        			if(null!=sessionBean && null!=sessionBean.getCurrentZipcodeVO()) {
                			currentZipEligibility = sessionBean.getCurrentZipcodeVO().isSddEligibility();
                			regionPromoAttr =sessionBean.getCurrentZipcodeVO().getPromoAttId();
                  	}
        		}
            	
				// update the sku promotional attributes for single sku or lead product.
				Map<String, RepositoryItem> attributeNameRepoItemMap = new HashMap<String, RepositoryItem>();
				attributeNameRepoItemMap = this.getSkuAttributeList(skuRepositoryItem, siteId, attributeNameRepoItemMap, regionPromoAttr, currentZipEligibility);
				if(!BBBUtility.isMapNullOrEmpty(attributeNameRepoItemMap)){
					final Map<String,AttributeVO> skuAttributes = this.getSiteLevelAttributes(siteId, attributeNameRepoItemMap);

					if (!BBBUtility.isMapNullOrEmpty(skuAttributes)) {
						compareProductEntryVO.setAttributesList(skuAttributes);
					}
				}

				//check for vdc sku attribute
				List<String> removingList=new ArrayList();
				if(skuRepositoryItem.getPropertyValue(BBBCatalogConstants.VDCSKU_TYPE_SKU_PROPERTY_NAME) != null ){
					List<AttributeVO> mapVdc = new ArrayList();
					Map<String,AttributeVO> mapLtlAttribute = new HashMap<String, AttributeVO>();
					
					if (!BBBUtility.isMapNullOrEmpty(this.getSiteLevelAttributes(null, attributeNameRepoItemMap))) {
						List<String> vdcAttributesList = BBBConfigRepoUtils.getAllValues(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.VDC_ATTRIBUTES_LIST);
						List<String> ltlAttributesList = BBBConfigRepoUtils.getAllValues(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.LTL_DELIVERY_ATTRIBUTES_LIST);
						boolean isVDC =false;
						for(Map.Entry entry : this.getSiteLevelAttributes(null, attributeNameRepoItemMap).entrySet()){
							String attributeId = (String) entry.getKey();
							AttributeVO attributeValue=(AttributeVO)entry.getValue();

							if(!BBBUtility.isListEmpty(vdcAttributesList) && vdcAttributesList.contains(attributeId)){	
								compareProductEntryVO.setVdcSkuFlag(BBBCoreConstants.COMPARE_PRODUCT_YES);
								isVDC=true;
								removingList.add(attributeId);
								RepositoryItem repItem = attributeNameRepoItemMap.get(attributeId);
								String descName = repItem.getPropertyValue("displayDescrip").toString();
								String actionURl = (String) repItem.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME);
								String imageURl = (String) repItem.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME);
								AttributeVO vdcSk = new AttributeVO();
									vdcSk.setAttributeDescrip(descName);
									vdcSk.setActionURL(actionURl);
									vdcSk.setImageURL(imageURl);
									mapVdc.add(vdcSk);
							}else if (!BBBUtility.isListEmpty(ltlAttributesList) && ltlAttributesList.contains(attributeId)){
								compareProductEntryVO.setVdcSkuFlag(BBBCoreConstants.COMPARE_PRODUCT_YES);
								compareProductEntryVO.setLtlAttributeApplicable(true);
								removingList.add(attributeId);
								RepositoryItem repItem = attributeNameRepoItemMap.get(attributeId);
								String descName = repItem.getPropertyValue("displayDescrip").toString();
								String actionURl=(String) repItem.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME);
								String imageURl=(String)repItem.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME);
								AttributeVO ltlAttribute = new AttributeVO();
									ltlAttribute.setAttributeDescrip(descName);
									ltlAttribute.setActionURL(actionURl);
									ltlAttribute.setImageURL(imageURl);
									mapLtlAttribute.put(repItem.getRepositoryId() , ltlAttribute);
							}else if(!isVDC){
								compareProductEntryVO.setVdcSkuFlag(BBBCoreConstants.COMPARE_PRODUCT_NO);
							}
						}
						compareProductEntryVO.setVdcSku(mapVdc);
						compareProductEntryVO.setLtlAttributesList(mapLtlAttribute);
					}

				}
				else{
					compareProductEntryVO.setVdcSkuFlag(BBBCoreConstants.COMPARE_PRODUCT_NO);
				}
				// BPSI- 1305 Added new property on product comparison page, fetching the
				// values of customization codes for the sku if its available.
				// Then converting every code to specific name from BCC
				if(skuRepositoryItem.getPropertyValue(BBBCoreConstants.ELIGIBLE_CUSTOMIZATION_CODES) !=null){

					String customizationCodes = (String) skuRepositoryItem.getPropertyValue(BBBCoreConstants.ELIGIBLE_CUSTOMIZATION_CODES);

					List<String> codes = Arrays.asList(customizationCodes.split(BBBCoreConstants.COMMA));
					List<String> codeValues = new ArrayList<String>();
					Map<String,String> eximCustomizationCodesMap = getEximManager().getEximValueMap();
					for(String s: codes) {
						String codeValue=null;
						codeValue = eximCustomizationCodesMap.get(s);
						
						if(codeValue!=null){
							codeValues.add(codeValue);
						}else{
							this.logDebug("No value found for"+ s);
						}
					}

					compareProductEntryVO.setCustomizationCode((String) skuRepositoryItem.getPropertyValue(BBBCoreConstants.ELIGIBLE_CUSTOMIZATION_CODES));
					compareProductEntryVO.setCustomizationCodeValues(codeValues);
				}else{
					compareProductEntryVO.setCustomizationCode(null);
				}
				List<String> isCustomizable = this.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.IS_CUSTOMIZABLE_ON);
		        if(isCustomizable.get(0).equalsIgnoreCase(BBBCoreConstants.TRUE)){
		        	compareProductEntryVO.setCustomizableRequired(this.isCustomizationRequiredForSKU(skuRepositoryItem, siteId));
		        	compareProductEntryVO.setCustomizationOffered(this.isCustomizationOfferedForSKU(skuRepositoryItem, siteId));
		        	compareProductEntryVO.setPersonalizationType((String) skuRepositoryItem.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE));
		        }else{
		        	compareProductEntryVO.setCustomizableRequired(false);
		        	compareProductEntryVO.setCustomizationOffered(false);
		        }
				if(null != compareProductEntryVO && null != compareProductEntryVO.getAttributesList() && null != compareProductEntryVO.getAttributesList().entrySet()){
					for(Iterator<Map.Entry<String,AttributeVO>>it=compareProductEntryVO.getAttributesList().entrySet().iterator();it.hasNext();)
					{
						Map.Entry<String,AttributeVO> entry = it.next();
						String 	attributeId=	entry.getKey();
						if(!BBBUtility.isListEmpty(removingList) && removingList.contains(attributeId))
						{
							it.remove();
						}
					}	
				}
				this.logDebug("BBBCatalogTools.getComparisonSKUDetails() method ends");
				return skuDetailVO;
			}catch (RepositoryException exp) {
				this.logError("Repository exception in fetching the sku details for sku id " + skuId);
				throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, exp);
			}
		}
		this.logError("Product id or site id is null hence throwing exception from BBBCatalogTools.getComparisonSkuDetails()");
		throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
				BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
	}
	
	/**
	 * Gets the color swatch.
	 *
	 * @param productId the product id
	 * @param productVO the product vo
	 * @return the color swatch
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public Map<String,String> getColorSwatch(String productId,ProductVO productVO) throws BBBSystemException, BBBBusinessException
	{

		Map<String,String> colorimage=new HashMap<String,String>();
		RepositoryItem productRepositoryItem = null;
		try{
			if(productId!=null){
				productRepositoryItem = getCatalogRepository().getItem(productId, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
			}
			if(productRepositoryItem != null){
				final List<RepositoryItem> skuRepositoryItems = (List<RepositoryItem>) productRepositoryItem.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);
			if(skuRepositoryItems == null)
			{
				this.logError("SKU is not available in repository for that product");
				throw new BBBBusinessException (BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
			}
			else{
				if(!productVO.isCollection()){
					int i=0;
					while(i<skuRepositoryItems.size())
					{


						if(skuRepositoryItems.get(i).getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME)!=null)
						{
							String color=EMPTYSTRING;
							 if (skuRepositoryItems.get(i).getPropertyValue(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME) != null) {
						             color= (String) skuRepositoryItems.get(i).getPropertyValue(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME);
						        }
							colorimage.put((String)skuRepositoryItems.get(i).getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME),color);

						}
						i++;
					}
				}
			}
		}
	}catch(RepositoryException exp)
		{
			this.logError("Repository exception in fetching the sku details for sku id ");
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, exp); 
		}
		
		return(colorimage);
	}

	/* (non-Javadoc)
	 * @see com.bbb.commerce.catalog.BBBCatalogTools#getCompareProductDetail(com.bbb.commerce.catalog.comparison.vo.CompareProductEntryVO)
	 */

	/**
	 * Added as part of R2.2 178-A4 story. Product Comparison Page.
	 * This method is used to populate the ProductVO for all types
	 * of products and the SKU VO if product is single sku or lead
	 * product. Using these Vo's, it will update the attributes/properties
	 * of the CompareProductEntryVO which is currently been iterated in
	 * the comparison list.
	 * @param compareProductVO 
	 * 
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * 
	 */
	@Override
	public void getCompareProductDetail(CompareProductEntryVO compareProductVO) throws BBBBusinessException, BBBSystemException {
		this.logDebug("BBBCatalogToolsImpl.getProductDetail() method starts");
		final String siteId =getCurrentSiteId();
		ProductVO productVO;
		this.logDebug("Site id : " + siteId);


		//catalog API call to update the product VO with the attributes required on product comparison page
		this.logDebug("Updating the product VO in comparison for product id : " + compareProductVO.getProductId());

		productVO = getComparisonProductDetails(compareProductVO.getProductId(), siteId, compareProductVO);
		// Changes made for BBBH-2212 
		String shipMsgDisplayFlag = BBBCoreConstants.FALSE;
		try {
			shipMsgDisplayFlag = getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG).get(0);
		} catch (BBBSystemException | BBBBusinessException e) {
			logError("Error while getting config key ShipMsgDisplayFlag value", e);
		}
		//free shipping over $49 changes for compare page
		if(Boolean.parseBoolean(shipMsgDisplayFlag) && productVO != null){
			 updateShippingMessageFlag(productVO);
		}
		if(productVO != null){	
			compareProductVO.setIntlRestricted(productVO.isIntlRestricted());
			//updating the CompareProductEntryVO attributes with the updated ProductVO attributes
			if(BBBUtility.isNotEmpty(compareProductVO.getSkuId()))
			{

				compareProductVO.setProductActive(productVO.getDisabled());
				compareProductVO.setReviews(productVO.getBvReviews());
				RepositoryItem skuRepositoryItem=null;
				try {
					skuRepositoryItem = getCatalogRepository().getItem(compareProductVO.getSkuId(), BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
					if(skuRepositoryItem!=null)
					{
						SKUDetailVO skuDetailVO = getSKUDetails(siteId, compareProductVO.getSkuId());
						if(Boolean.parseBoolean(shipMsgDisplayFlag) && skuDetailVO != null){
							updateShippingMessageFlag(skuDetailVO, false, 0.0);
							compareProductVO.setDisplayShipMsg(skuDetailVO.getDisplayShipMsg());
							compareProductVO.setShipMsgFlag(skuDetailVO.isShipMsgFlag());
						}
						compareProductVO.setShortDescription((String)skuRepositoryItem.getPropertyValue(BBBCatalogConstants.DESCRIPTION_CATEGORY_PROPERTY_NAME));
						compareProductVO.setMediumImagePath((String)skuRepositoryItem.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME));
						compareProductVO.setThumbnailImagePath((String)skuRepositoryItem.getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME));
						compareProductVO.setProductName((String)skuRepositoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME));

						if(skuRepositoryItem.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) != null){
							compareProductVO.setLtlProduct((Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME));
						}
					}
				} catch (RepositoryException e) {
				    this.logError("Repository exception in fetching the sku details for product id " + compareProductVO.getSkuId());
					throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
							BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
				}

			}
			else {
				compareProductVO.setProductActive(productVO.getDisabled());
				compareProductVO.setMediumImagePath(productVO.getProductImages().getMediumImage());
				compareProductVO.setThumbnailImagePath(productVO.getProductImages().getThumbnailImage());
				compareProductVO.setProductName(productVO.getName());
				compareProductVO.setCollection(productVO.isCollection());
				compareProductVO.setShortDescription(productVO.getShortDescription());
				compareProductVO.setLongDescription(productVO.getLongDescription());
				compareProductVO.setReviews(productVO.getBvReviews());
				compareProductVO.setPriceRangeDescription(productVO.getPriceRangeDescription());
				compareProductVO.setSalePriceRangeDescription(productVO.getSalePriceRangeDescription());
				compareProductVO.setDisplayShipMsg(productVO.getDisplayShipMsg());
				compareProductVO.setShipMsgFlag(productVO.isShipMsgFlag());
				compareProductVO.setPriceLabelCode(productVO.getPriceLabelCode());
				compareProductVO.setDynamicPricingProduct(productVO.isDynamicPricingProduct());
				compareProductVO.setInCartFlag(productVO.isInCartFlag());
				
			}
			this.logDebug("CompareProductEntryVO basic product attributes for product id " + compareProductVO.getProductId() + "are : "
					+"\nProduct active : " + compareProductVO.isProductActive()
					+"\nCollection product/ MSWP ? : " + compareProductVO.isCollection());

			//default tabular attributes shown on product comparison page.
			if( BBBUtility.isNotBlank(compareProductVO.getSkuId()))
			{

				compareProductVO.setFreeStandardShipping(BBBCoreConstants.COMPARE_PRODUCT_NO);
				compareProductVO.setVdcSkuFlag(BBBCoreConstants.COMPARE_PRODUCT_NO);
				compareProductVO.setSkuGiftWrapEligible(BBBCoreConstants.COMPARE_PRODUCT_NO);
				compareProductVO.setClearance(BBBCoreConstants.COMPARE_PRODUCT_NO);
			}
			else 
			{
				compareProductVO.setCustomizationCode(BBBCoreConstants.SELECT_OPTIONS);
				compareProductVO.setFreeStandardShipping(BBBCoreConstants.COMPARE_PRODUCT_NO);
				compareProductVO.setVdcSkuFlag(BBBCoreConstants.SELECT_OPTIONS);
				compareProductVO.setSkuGiftWrapEligible(BBBCoreConstants.SELECT_OPTIONS);
				compareProductVO.setClearance(BBBCoreConstants.COMPARE_PRODUCT_NO);
			}
			// Product promotional attributes are shown only in case of collection product/ Multi sku product
			if(((compareProductVO.isCollection() || compareProductVO.isMultiSku()) && !BBBUtility.isMapNullOrEmpty(compareProductVO.getAttributesList())) && BBBUtility.isEmpty(compareProductVO.getSkuId()) ){

				BBBCatalogToolsImpl.getCompareProductsAttributes(compareProductVO);
			}
			if(!productVO.isCollection() && BBBUtility.isEmpty(compareProductVO.getSkuId())){
				Map<String,String> colorimage =  getColorSwatch(compareProductVO.getProductId(),productVO) ;

				if(!BBBUtility.isMapNullOrEmpty(colorimage))
				{
					compareProductVO.setColor(colorimage); 
				}         
			}
			else if(!productVO.isCollection()){
				Map<String,String> colorimage=	getskuSwatch(compareProductVO.getSkuId());
					compareProductVO.setColor(colorimage); 
			}			
			
			String skuId=null;

			if(BBBUtility.isNotBlank(compareProductVO.getSkuId()))
			{

				skuId=compareProductVO.getSkuId();
				compareProductVO.setInCartFlag(getSkuIncartFlag(skuId));
				getCompareSkuDetail(compareProductVO,skuId,siteId);
			}	
			else if(!BBBUtility.isListEmpty(productVO.getChildSKUs())){	
				skuId =	productVO.getChildSKUs().get(0);
				compareProductVO.setDefaultSkuId(skuId);
				compareProductVO.setInCartFlag(getSkuIncartFlag(skuId));
				getCompareSkuDetail(compareProductVO,skuId,siteId);
			}
			
			compareProductVO.setVendorInfoVO(this.getVendorInfo(this.getProductVOMetaDetails(getCurrentSiteId(),compareProductVO.getProductId()).getVendorId()));

			this.logDebug("CompareProductEntryVO tabular attributes for product id " + compareProductVO.getProductId() + "are : "
					+"\nColor : " + compareProductVO.getColor()
					+"\nPromotional attributes : " + compareProductVO.getAttributesList()
					+"\nFree shipping : " + compareProductVO.getFreeStandardShipping()
					+"\nVDC : " + compareProductVO.getVdcSku()
					+"\nClearance : " + compareProductVO.getClearance()
					+"\nGift packaging : " + compareProductVO.getSkuGiftWrapEligible());
		} else {
			this.logError("Product VO is returned as null for product id : " + compareProductVO.getProductId() + " hence not updating its CompareProductEntryVO");
			throw new BBBSystemException(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY, "CompareProductEntryVO is returned as null from BBBCatalogToolsImpl.getProductDetail() method");
		}
		this.logDebug("BBBCatalogToolsImpl.getProductDetail() method ends");
	}
	
	/**
	 * Gets the sku swatch.
	 *
	 * @param skuId the sku id
	 * @return the sku swatch
	 */
	protected Map<String,String> getskuSwatch(String skuId)
	{
		Map<String,String> swatchImage=new HashMap<String,String>();
		if(skuId!=null){
			try {
				RepositoryItem skuRepositoryItem = getCatalogRepository().getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
				if(skuRepositoryItem!=null && skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME)!=null) {
						String color=EMPTYSTRING;
						 if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME) != null) {
					             color= (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME);
					        }
						 swatchImage.put((String)skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME),color);
				}
			} catch (RepositoryException e) {
				
				this.logError("cannot fetch Swatch Image from skuRepository: RepositoryException ", e);
			}
		}
		return(swatchImage);
	}

	/**
	 * Gets the compare sku detail.
	 *
	 * @param compareProductVO the compare product vo
	 * @param skuId the sku id
	 * @param siteId the site id
	 * @return the compare sku detail
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	private void getCompareSkuDetail(CompareProductEntryVO compareProductVO,String skuId,String siteId) throws BBBBusinessException, BBBSystemException
	{

		this.logTrace("Product is a single sku or a lead product with sku id : " + compareProductVO.getSkuId());
		final boolean calculateAboveBelowLine = false;
		int inStockStatus = 1;
		SKUDetailVO skuDetailVO = new SKUDetailVO();

		try {
			inStockStatus = getBbbInventoryManager().getProductAvailability(siteId, skuId, BBBInventoryManager.PRODUCT_DISPLAY, 0);
		} catch (BBBSystemException excep) {
			this.logError("SystemException while fetching stock status for skuid : " + skuId + "from getCompareProductDetail() method", excep);
		}
		catch (BBBBusinessException excep) {
			this.logError("BusinessException while fetching stock status for skuid : " + skuId + "from getCompareProductDetail() method", excep);
		}
		//catalog API call to update the sku vo attributes required on comparison page
		skuDetailVO = getComparisonSKUDetails(siteId,  skuId, calculateAboveBelowLine, compareProductVO);
		compareProductVO.setInStock(true);
		if(inStockStatus == BBBInventoryManager.NOT_AVAILABLE){
			compareProductVO.setInStock(false);
		}
		if(skuDetailVO.isSkuBelowLine()){
			compareProductVO.setInStock(false);
		}
		this.logTrace("Product is in stock ? : " + compareProductVO.isInStock());
		compareProductVO.setBopusExcluded(skuDetailVO.isBopusAllowed());
		this.logTrace("Product bopus excluded ? : " + compareProductVO.isBopusExcluded());
		if(skuDetailVO.getGiftWrapEligible()){
			compareProductVO.setSkuGiftWrapEligible(BBBCoreConstants.AVAILABLE);
		} else {
			compareProductVO.setSkuGiftWrapEligible(BBBCoreConstants.NOT_AVAILABLE);
		}

		//display SKU promotional attributes in case of lead or single sku product.
		if(!BBBUtility.isMapNullOrEmpty(compareProductVO.getAttributesList())){
			BBBCatalogToolsImpl.getCompareProductsAttributes(compareProductVO);
		}
	}





	/**
	 * R2.2 Product Comparison Page. 178-A4 Story
	 * This method is used to check products clearance & free shipping
	 * attribute. If found, set that attribute property to yes and remove
	 * that attribute from the list of attributes.
	 *
	 * @param compareProductVO the compare product vo
	 * @return the compare products attributes
	 */
	private static void getCompareProductsAttributes(CompareProductEntryVO compareProductVO){

		//check for clearance attribute
		for(Map.Entry entry : compareProductVO.getAttributesList().entrySet()){
			String attributeId = (String) entry.getKey();
			List<String> clearanceAttrId = BBBConfigRepoUtils.getAllValues(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.CLEARANCE_ATTRIBUTE);
			if(!BBBUtility.isListEmpty(clearanceAttrId) && clearanceAttrId.contains(attributeId)){
				compareProductVO.setClearance(BBBCoreConstants.COMPARE_PRODUCT_YES);
				compareProductVO.getAttributesList().remove(attributeId);
				break;
			}
		}
		//check for free shipping attribute
		for(Map.Entry entry : compareProductVO.getAttributesList().entrySet()){
			String attributeId = (String) entry.getKey();
			List<String> freeShippingAttrId = BBBConfigRepoUtils.getAllValues(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.FREE_STANDARD_SHIPPING);
			if(!BBBUtility.isListEmpty(freeShippingAttrId) && freeShippingAttrId.contains(attributeId)){
				compareProductVO.setFreeStandardShipping(BBBCoreConstants.COMPARE_PRODUCT_YES);
				compareProductVO.getAttributesList().remove(attributeId);
				break;
			}
		}
	}
	
	/**
	 * R2.2 Product Comparison Page. 178-A4
	 * This method is used to check for site specific promotional attributes.If found,
	 * then a map of promotional attributes as value and attribute id as keys applicable 
	 * for that site and product is returned.
	 *
	 * @param pSiteId the site id
	 * @param attributeNameRepoItemMap the attribute name repo item map
	 * @return List
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	public final Map<String,AttributeVO> getSiteLevelAttributes(final String pSiteId, Map<String, RepositoryItem> attributeNameRepoItemMap)
			throws BBBBusinessException, BBBSystemException {

		return getSiteRepositoryTools().getSiteLevelAttributes(pSiteId, attributeNameRepoItemMap);
	}
	
	/**
	 * RM DEFECT:23496. Get the Brand name from Repository
	 *
	 * @param brandId the brand id
	 * @return String
	 * @throws BBBSystemException the BBB system exception
	 */
	@Override
	public String getBrandName(String brandId) throws BBBSystemException{
				logDebug("BBBCatalogToolsImpl.getBrandName() Method Entering");
			String brandName = null;
			RepositoryItem brandRepositoryItem = null;
			try {
				brandRepositoryItem = this.getCatalogRepository().getItem(brandId,BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR);
				if(null!=brandRepositoryItem && null!=brandRepositoryItem.getPropertyValue(BBBCatalogConstants.BRAND_NAME_BRAND_PROPERTY_NAME)){
						brandName= (String)brandRepositoryItem.getPropertyValue(BBBCatalogConstants.BRAND_NAME_BRAND_PROPERTY_NAME);
				}
			
			} catch (RepositoryException e) {
				this.logError("Catalog API Method Name [getBrandName]: RepositoryException ");
				throw new BBBSystemException(
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
						e);
			}
				logDebug("BBBCatalogToolsImpl.getBrandName() Method Ending");
		return brandName;
	}

	/**This method is used to fetch the packAndHoldEnd Dtae from SiteRepository.
	 * 
	 * @param siteId
	 * @return Date
	 * @throws ParseException 
	 * @throws RepositoryException 
	 */
	@Override
	public String packNHoldEndDate(final String siteId)
			throws RepositoryException {
		final RepositoryItem siteConfiguration = this.getSiteRepository().getItem(siteId,
		                BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
		 String endDate = null;
		 DateFormat formatter;
		 if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) || siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)){
				formatter = new SimpleDateFormat("dd/MM/yyyy");
			}else{
				formatter = new SimpleDateFormat("MM/dd/yyyy");
			}
		 if (siteConfiguration != null && siteConfiguration.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME) != null) {
			 Date packNHoldEnd =  (Date) siteConfiguration
                                 .getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME);
                 endDate = formatter.format(packNHoldEnd);
                 this.logTrace(packNHoldEnd + " packNHoldEnd value for siteId " + siteId);
                 return endDate;
		 }
             return endDate;
	}
	/**
	 * RM DEFECT:23496. Get the Brand Id from Repository
	 *
	 * @param brandName the brand name
	 * @return String
	 */
	@Override
	public String getBrandId(String brandName) {

			String brandId=null;
				logDebug("BBBCatalogToolsImpl.getBrandId() Method Entering");

			try {
				RepositoryItem[] items;
				RepositoryView view =  getCatalogRepository().getView(BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR);

				RqlStatement statement = parseRqlStatement(BRAND_NAME_RQL);

				Object params[] = new Object[2];
				params[0] = brandName;
	
				items = statement.executeQuery(view, params);
				if(null!=items && items.length>0 && null!=items[0].getPropertyValue(BBBCatalogConstants.BRANDS_ITEM_ID)){
					brandId=(String)items[0].getPropertyValue(BBBCatalogConstants.BRANDS_ITEM_ID);
				}
				
			} catch (RepositoryException e) {
				this.logError("Catalog API Method Name [getBrandId]: RepositoryException ", e);
			} 
				logDebug("BBBCatalogToolsImpl.getBrandId() Method Ending");
			
		return brandId;
	}
	
	/**
	 * @return
	 * @throws RepositoryException
	 */
	protected RqlStatement parseRqlStatement(String rqlValue) throws RepositoryException {
		return RqlStatement.parseRqlStatement(rqlValue);
	}
	
    /**
     *  This method gets the bazaar voice key for the current Site from Config Keys repository.
     *
     * @return the bazaar voice key
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
	@Override
    public String getBazaarVoiceKey() throws BBBBusinessException, BBBSystemException {

        final String siteId = getCurrentSiteId() ;

        final String key = siteId + "_BazaarVoiceKey";

        List<String> listKeys = null;
        String bazaarVoicekey = null;

        try {
            listKeys = this.getAllValuesForKey("ContentCatalogKeys", key);

            if (!BBBUtility.isListEmpty(listKeys)) {
                bazaarVoicekey = listKeys.get(0);
            }

        } catch (final BBBSystemException e) {
            this.logError(
                    LogMessageFormatter.formatMessage(null, BBB_SYSTEM + EXCEPTION_BAZAAR_VOICE_KEY_NOT_FOUND_FOR_SITE
                            + siteId, BBBCoreErrorConstants.ACCOUNT_ERROR_1072), e);
            throw e;
        } catch (final BBBBusinessException e) {
            this.logError(
                    LogMessageFormatter.formatMessage(null, BBB_BUSINESS
                            + EXCEPTION_BAZAAR_VOICE_KEY_NOT_FOUND_FOR_SITE + siteId,
                            BBBCoreErrorConstants.ACCOUNT_ERROR_1073), e);
            throw e;
        }

		return bazaarVoicekey;
	}

	/**
	 *  The method gives the expected min and max date string when a customer can expect teh delivery if time at which
	 * order is submit is past the cutoff time for the shipping group then min expected date by which order is expected
	 * will be (current date +1 + min days to ship + order to ship sla) else it will be (current date+min days to ship + order to ship sla) 
	 * Similarly max expected date by which order is expected will be (current date +1 + max days to ship +order to ship sla) else it will be (current
	 * date+max days to ship +order to ship sla).
	 *
	 * @param shippingMethod the shipping method
	 * @param siteId the site id
	 * @param skuId the sku id
	 * @param orderDate the order date
	 * @param includeYearFlag the include year flag
	 * @return the expected delivery date for ltl item
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	@Override
    public final String getExpectedDeliveryDateForLTLItem(final String shippingMethod, final String siteId,final String skuId,final Date orderDate, boolean includeYearFlag)
                    throws BBBBusinessException, BBBSystemException {

    	this.logDebug("Catalog API Method Name [getExpectedDeliveryDateForLTLItem]shippingMethod[" + shippingMethod + "]" + "includeYearFlag- " + includeYearFlag);
    	int maxDaysToShip;
    	int minDaysToShip;
        String minDateString = BBBCoreConstants.BLANK;
        String maxDateString = BBBCoreConstants.BLANK;

        if (!StringUtils.isEmpty(shippingMethod) && !StringUtils.isEmpty(siteId) && !StringUtils.isEmpty(skuId)) {
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getExpectedDeliveryDateForLTLItem");

                final RepositoryItem shippingDurationItem = this.getShippingDuration(shippingMethod, siteId);
                if(shippingDurationItem != null){
	                final Calendar calCurrentDate = Calendar.getInstance();
	                final Calendar minDate = Calendar.getInstance();
	                final Calendar maxDate = Calendar.getInstance();
	                
	                if(null != orderDate) {
		                calCurrentDate.setTime(orderDate);
		                minDate.setTime(orderDate);
		                maxDate.setTime(orderDate);
	                }
	                
	                final Date cutOffTime = (Date) shippingDurationItem
	                                .getPropertyValue(BBBCatalogConstants.CUT_OFF_TIME_SHIPPING_PROPERTY_NAME);
	
	                this.logDebug("Value of cutOffTime [ " + cutOffTime + "]");
	                maxDaysToShip = ((Integer) shippingDurationItem
	                                .getPropertyValue(BBBCatalogConstants.MAX_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME))
	                                .intValue();
	                minDaysToShip = ((Integer) shippingDurationItem
	                                .getPropertyValue(BBBCatalogConstants.MIN_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME))
	                                .intValue();
	                this.logDebug("Hour value  in current date [" + calCurrentDate.get(Calendar.HOUR_OF_DAY)
	                                + "] Minute value in  current date [" + calCurrentDate.get(Calendar.MINUTE) + "]");
	                this.logDebug("maxDaysToShip [ " + maxDaysToShip + " ] minDaysToShip [" + minDaysToShip + "]");
	                
	                boolean isCutOff = false;
	
	                Set<Integer> weekEndDays = this.getWeekEndDays(siteId);
	                this.logDebug("weekEndDays are  " + weekEndDays);
	
	                // Calculate Holiday Dates
	                Set<Date> holidayList = this.getHolidayList(siteId);
	               	
	                if (cutOffTime != null) {
	                	
	                	final Calendar calCutOffTime = Calendar.getInstance();
		                calCutOffTime.setTime(cutOffTime);
		                this.logDebug("Hour value  in cutOffTime [" + calCutOffTime.get(Calendar.HOUR_OF_DAY)
                                + "] Minute value in cutOffTime [" + calCutOffTime.get(Calendar.MINUTE) + "]");
		                
	                    if (calCurrentDate.get(Calendar.HOUR_OF_DAY) == calCutOffTime.get(Calendar.HOUR_OF_DAY)) {
	                        if (calCurrentDate.get(Calendar.MINUTE) >= calCutOffTime.get(Calendar.MINUTE)) {
	                            this.logDebug("Cutoff time reached as Hours and minutes of the day is matching cutoff hours and minutes");
	                            isCutOff = true;
	                        }
	                    } else if (calCurrentDate.get(Calendar.HOUR_OF_DAY) > calCutOffTime.get(Calendar.HOUR_OF_DAY)) {
	                        this.logDebug("hours in current date has exceeded cutoff time");
	                        isCutOff = true;
	                    }
	                    // if today is not a weekday or holiday then add +1 day to min and max
	                    if (isCutOff && !weekEndDays.contains(Integer.valueOf(calCurrentDate.get(Calendar.DAY_OF_WEEK)))
	                                    && !isHoliday(holidayList, calCurrentDate)) {
	                        minDaysToShip = minDaysToShip + 1;
	                        maxDaysToShip = maxDaysToShip + 1;
	                    }
	                }
	                this.logDebug("maxDaysToShip after cutofftime check [ " + maxDaysToShip
	                                + " ] minDaysToShip after cutofftime check [" + minDaysToShip + "]");
	                
	                
	                //adding order to ship SLA from sku.
	                final RepositoryItem skuRepositoryItem = getCatalogRepository().getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
	                SKUDetailVO skuDetailVO = this.getMinimalSku(skuRepositoryItem);
	                String orderToShipSLA = skuDetailVO.getOrderToShipSla();
	                if(! StringUtils.isEmpty(orderToShipSLA) ){
	                	int sla = Integer.parseInt(orderToShipSLA);
	                	this.logDebug("OrderToShipSLA" + orderToShipSLA);
	                	minDaysToShip =  minDaysToShip + sla;
		                maxDaysToShip = maxDaysToShip + sla;
	                }
	                
	                this.logDebug("maxDaysToShip after SLA check [ " + maxDaysToShip
                            + " ] minDaysToShip after SLA check [" + minDaysToShip + "]");
	                
	                int tmpMinDays = minDaysToShip;
	                int tmpMaxDays = maxDaysToShip;
	                //+
	                minDate.add(Calendar.DATE, 1);
	                //-
	                minDate.add(Calendar.DATE, -1);
	
	                while (tmpMinDays != 0) {
	                    if (!weekEndDays.contains(Integer.valueOf(minDate.get(Calendar.DAY_OF_WEEK)))
	                                    && !isHoliday(holidayList, minDate)) {
	                        tmpMinDays--;
	                    }
	                    if (tmpMinDays != 0) {
	                        minDate.add(Calendar.DATE, 1);
	                    }
	                }
	
	                while (tmpMaxDays != 0) {
	                    if (!weekEndDays.contains(Integer.valueOf(maxDate.get(Calendar.DAY_OF_WEEK)))
	                                    && !isHoliday(holidayList, maxDate)) {
	                        tmpMaxDays--;
	                    }
	                    if (tmpMaxDays != 0) {
	                        maxDate.add(Calendar.DATE, 1);
	                    }
	                }

	                if(includeYearFlag) {
	                	final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	                    minDateString = dateFormat.format(minDate.getTime());
	                    maxDateString = dateFormat.format(maxDate.getTime());
	                }else{
	                	int minDay = minDate.get(Calendar.DAY_OF_MONTH);
	                	int minMonth = minDate.get(Calendar.MONTH) + 1;
	                	int maxDay = maxDate.get(Calendar.DAY_OF_MONTH);
	                	int maxMonth = maxDate.get(Calendar.MONTH) + 1;
	                	
	                	if (BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(siteId) || siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA))
	                    {
		                    minDateString = String.format("%02d", minDay) + "/" + String.format("%02d", minMonth);
		                    maxDateString = String.format("%02d", maxDay) + "/" + String.format("%02d", maxMonth);
	                    }else{
		                    minDateString = String.format("%02d", minMonth) + "/" + String.format("%02d", minDay);
		                    maxDateString = String.format("%02d", maxMonth) + "/" + String.format("%02d", maxDay);
		                }
	                }
	                	this.logDebug("minDateString after format  " + minDateString + "  maxDateString after format "
	                                + maxDateString);
	                
                } else {
                    this.logError("Not a Valid shipping method ID [" + shippingMethod + "]");
                    throw new BBBSystemException(BBBCatalogErrorCodes.SHIPPING_METHOD_NOT_AVAILABLE_IN_REPOSITORY,
                                    BBBCatalogErrorCodes.SHIPPING_METHOD_NOT_AVAILABLE_IN_REPOSITORY);
                }
                
            }catch (RepositoryException e) {
    			this.logError("Catalog API Method Name [getExpectedDeliveryDateForLTLItem]: RepositoryException "+ BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION);
    			throw new BBBSystemException (BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,e);
			} finally {
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getExpectedDeliveryDateForLTLItem");
            }
        } else {
        	this.logError("method getExpectedDeliveryDateForLTLItem having null input parms.");
        	throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
        }
        
        
        return minDateString + " - " + maxDateString;
        
    }

   
    /**
     *   
     * Calculate assembly charge for site specific SKU.
     *
     * @param siteId the site id
     * @param skuId the sku id
     * @return double assembly charge
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
	@Override
    public double getAssemblyCharge(final String siteId,final String skuId) throws BBBBusinessException, BBBSystemException{
		this.logDebug("Entering BBBCatalogTools.getAssemblyCharge() method");		
		double assemblyCharge = 0.0;
		if(! StringUtils.isEmpty(siteId) && !StringUtils.isEmpty(skuId)){
            RepositoryItem skuRepositoryItem = null;
			try {
				skuRepositoryItem = getCatalogRepository().getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
	            SKUDetailVO skuDetailVO = this.getMinimalSku(skuRepositoryItem);
				String assemblyTimeStr = skuDetailVO.getAssemblyTime();
				
				double assemblyTime = 0.0;
				if(!StringUtils.isEmpty(assemblyTimeStr)){
					assemblyTime = Double.parseDouble(assemblyTimeStr);
				}
				List<String> assemblyConfigList = this.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.ASSEMBLY_FEE_PER_THIRTY_MINUTES);
				double perThirtyMinAssemblyCharge = 0.0D; 
				if(!BBBUtility.isListEmpty(assemblyConfigList)){
					String assembleFeePerThirtyMin = assemblyConfigList.get(0);
					if(!StringUtils.isEmpty(assembleFeePerThirtyMin)){
						perThirtyMinAssemblyCharge = Double.parseDouble(assembleFeePerThirtyMin);
					}
				}
				
				this.logDebug("assemblyTime : " + assemblyTime + " perThirtyMinAssemblyCharge : " + perThirtyMinAssemblyCharge );
				assemblyCharge = (perThirtyMinAssemblyCharge)* (Math.ceil(assemblyTime/30.00D));
				this.logDebug("Assembly charge for "+ skuId + " is "+ assemblyCharge);
				return assemblyCharge;
			} catch (RepositoryException re) {
				this.logError("Catalog API Method Name [getAssemblyCharge]: RepositoryException "+ BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION + re.getMessage());
				throw new BBBSystemException (BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,re);
			}

		}else {
			this.logError("method BBBCatalogTools.getAssemblyCharge() having null input parms.");
			throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
		}
	}

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getDeliveryCharge(java.lang.String, java.lang.String, java.lang.String)
     */
    public final double getDeliveryCharge(final String siteId, final String skuId, final String shippingMethodCode) throws BBBBusinessException, BBBSystemException{
		this.logDebug("Entering BBBCatalogTools.getDeliveryCharge() method");
		double deliveryCharge = 0.0;
		if(! StringUtils.isEmpty(siteId) && !StringUtils.isEmpty(skuId)){
			final Double caseWeight = this.getCaseWeightForSku(skuId);
			deliveryCharge = this.getCmsTools().getDeliveryCharge(siteId, caseWeight, shippingMethodCode);
		}else {
			this.logError("method BBBCatalogTools.getDeliveryCharge() having null input parms.");
			throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
		}
		return deliveryCharge;
	}
    
    /**
     *  The method checks if the sku is ltl & vdc .
     *
     * @param siteId the site id
     * @param skuId the sku id
     * @return  boolean
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    
    public boolean isSkuLtl(String siteId,String skuId) throws BBBSystemException, BBBBusinessException {
    	boolean isSKULtlVdc=false;
    	SKUDetailVO skuvo =null;
    	if(BBBUtility.isNotEmpty(siteId)&& BBBUtility.isNotEmpty(skuId))
    	{
    	 skuvo = getMinimalSku(skuId);
    	}
    	if(skuvo!=null && skuvo.isLtlItem()) {
    		isSKULtlVdc=true;
    	}
    	return isSKULtlVdc;
    }
	
	/**
	 * This method checks if the sku is eligible for international shipping or not
	 * The following type of sku are restricted for shipping internationally
	 * --VDC sku
	 * --Gift cert
	 * --Store only sku
	 * 
	 * In addition if the sku is in restriction table or any of its properties jda dept,jda sub dept and jda class and brands
	 * is in restriction table the sku is also restricted for international shipping.
	 *
	 * @param skuVO the sku vo
	 * @return true, if is sku restricted for int ship
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	public boolean isSkuRestrictedForIntShip(SKUDetailVO skuVO) throws BBBBusinessException, BBBSystemException{
		String skuId=skuVO.getSkuId();
		logDebug(" Enter BBBCatalogTools:isSkuRestrictedForIntShip for sku id  "+skuId);
		if(isSkuInRestrictedList(skuId)){
			return true;
		}

		if(skuVO.isVdcSku() && isIntShippingVDCRestricted()){
			logDebug(" sku Id "+skuId+" is a VDC sku.Marking the sku as restricted for international shipping");
			return true;
		}else{

			try{
				final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId,
						BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
				if (skuRepositoryItem == null) {
					this.logDebug("Repository Item is null for sku id " + skuId);
					throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
							BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
				}
				if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) != null) {
					boolean isLTLSku =((Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU)).booleanValue();
					if(isLTLSku){
						logDebug(" sku Id "+skuId+" is a LTL sku.Marking the sku as restricted for international shipping");
						return true;
					}
	            }
				//check for store sku
				if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_STORE_SKU_SKU_PROPERTY_NAME) != null) {
					boolean isStoreSku = ((Boolean) skuRepositoryItem
							.getPropertyValue(BBBCatalogConstants.IS_STORE_SKU_SKU_PROPERTY_NAME)).booleanValue();

					if(isStoreSku){
						logDebug(" sku Id "+skuId+" is a store sku.Marking the sku as restricted for international shipping");
						return true;
					}
				}
				//check for physical gift card
				if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.GIFT_CERT_SKU_PROPERTY_NAME) != null) {
					final boolean giftCert = ((Boolean) skuRepositoryItem
							.getPropertyValue(BBBCatalogConstants.GIFT_CERT_SKU_PROPERTY_NAME)).booleanValue();

					if(giftCert){
						logDebug(" sku Id "+skuId+" is a gift certificate sku.Marking the sku as restricted for international shipping");
						return true;
					}
				}
				RepositoryItem jdaDept = ((RepositoryItem) skuRepositoryItem
						.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME));
				if(jdaDept !=null && checkRestrictedItem(jdaDept.getRepositoryId(), BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_JDA_DEPT,BBBCatalogConstants.PROPERTY_NAME_RESTRICTED_JDA_DEPT) ){
					logDebug(" Jda dept Id "+jdaDept.getRepositoryId()+" for sku Id "+skuId+" is restricted.Marking the sku as restricted for international shipping");
					return true;
				}
				else {
					RepositoryItem jdaSubDept = ((RepositoryItem) skuRepositoryItem
							.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME));
					if(jdaSubDept !=null && checkRestrictedItem(jdaSubDept.getRepositoryId(), BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_JDA_SUB_DEPT,BBBCatalogConstants.PROPERTY_NAME_RESTRICTED_JDA_SUB_DEPT)){
						logDebug(" Jda sub dept Id "+jdaSubDept.getRepositoryId()+" for sku Id "+skuId+" is restricted.Marking the sku as restricted for international shipping");
						return true;
					}
					else{
						String jdaClass=((String) skuRepositoryItem
								.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME));
						String jdaClassRepoId = null;
						if(BBBUtility.isNotEmpty(jdaClass) && null != jdaSubDept) {
							jdaClassRepoId=jdaSubDept.getRepositoryId()+"_"+jdaClass;
						}
						
						if(jdaClassRepoId !=null && checkRestrictedItem(jdaClassRepoId, BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_JDA_CLASS,BBBCatalogConstants.PROPERTY_NAME_RESTRICTED_JDA_CLASS)){
								logDebug(" Jda class Id "+jdaClassRepoId+" for sku Id "+skuId+" is restricted.Marking the sku as restricted for international shipping");
								return true;
						} else{
						
						String parentProdId=this.getParentProductForSku(skuId);
						logDebug(" parent product Id for sku is "+parentProdId);
						RepositoryItem productRepositoryItem = this.getCatalogRepository().getItem(parentProdId,
								BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
						if(productRepositoryItem!=null){
							RepositoryItem brands = ((RepositoryItem) productRepositoryItem
									.getPropertyValue(BBBCatalogConstants.BBB_BRANDS));
							if(brands !=null && checkRestrictedItem(brands.getRepositoryId(), BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_BRANDS,BBBCatalogConstants.PROPERTY_NAME_RESTRICTED_BRANDS)){
								String brandName=(String) brands.getPropertyValue(BBBCatalogConstants.BRAND_NAME_BRAND_PROPERTY_NAME);
								logDebug(brandName +" brand is restricted for sku id "+skuId +" with parent "+parentProdId +" Marking the sku as restricted for International shipping");
								return true;
							}
						}
						
						}
					}
				}


			} catch (final RepositoryException e) {
				this.logError("Catalog API Method Name [getSKUDetails]: RepositoryException for sku Id " + skuId);
				throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);

			}
		}
		logDebug(" sku id "+skuId+" is not restricted for international shipping");
		return false;

	}
	
	/**
	 * The method checks if the skuId is in restriction table.
	 *
	 * @param skuId the sku id
	 * @return true, if is sku in restricted list
	 * @throws BBBSystemException the BBB system exception
	 */
	public boolean isSkuInRestrictedList( String skuId) throws BBBSystemException{

		RepositoryItem restrictedItem;
		try {
			restrictedItem = this.getIntShipSkuRestrictionRepository().getItem(skuId, BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_SKU);
			if(restrictedItem!=null && restrictedItem.getRepositoryId().equalsIgnoreCase(skuId)){
				logDebug(" sku Id "+skuId +" is in restriction table marking the sku as restricted for international shipping");
				return true;

			}
		} catch (RepositoryException e) {
			this.logError("Catalog API Method Name [isSkuInRestrictList]: RepositoryException for sku Id " + skuId);
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
		}

		return false;
	}
	/**
	 * The method checks if VDC item restricted for international shipping .
	 *
	 * @return the boolean
	 */
	public Boolean  isIntShippingVDCRestricted(){
		List<String> intShipintShipVDCRestricted=null;
		Boolean isIntShipVDCRestrictedOn=true;
		try {
			intShipintShipVDCRestricted = this.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
					BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_VDC_RESTRICTED);
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"BBBSystemException from service of InternationalShipTools : checkIntShippingVDCRestricted"), e);

		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"BBBBusinessException from service of InternationalShipTools : checkIntShippingVDCRestricted"), e);

		}

		if(!BBBUtility.isListEmpty(intShipintShipVDCRestricted)){
			isIntShipVDCRestrictedOn=Boolean.valueOf(intShipintShipVDCRestricted.get(0));
		}
		logDebug(" is international shipping on ? "+isIntShipVDCRestrictedOn);
		return isIntShipVDCRestrictedOn;
	}
	
	/**
	 * The method checks if invite.
	 *
	 * @return true, if is invite friends
	 */
	public boolean isInviteFriends(){
		List<String> inviteFriends=null;
		Boolean isInviteFriends=false;
		try {
			inviteFriends = this.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,
					BBBCmsConstants.INVITE_FRIENDS_KEY);
			if(!BBBUtility.isListEmpty(inviteFriends)){
				isInviteFriends=Boolean.valueOf(inviteFriends.get(0));
				logDebug(" is Invite Friends on ? "+isInviteFriends);
				return isInviteFriends;
			}
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"BBBSystemException from service of InviteFriends : checkInviteFriends"), e);

		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"BBBBusinessException from service of InviteFriends : checkInviteFriends"), e);

		}	
		return isInviteFriends;
	}
	
	/**
	 * The method checks if sku properties jda dept,jda sub dept,jda class and barnds is in rstriction table or not.
	 *
	 * @param id the id
	 * @param itemDescriptor the item descriptor
	 * @param restrictedProperty the restricted property
	 * @return true, if successful
	 * @throws BBBSystemException the BBB system exception
	 */
	private boolean checkRestrictedItem(String id,String itemDescriptor,String restrictedProperty) throws BBBSystemException{

		if(!StringUtils.isEmpty(id)&& !StringUtils.isEmpty(itemDescriptor)){
			RepositoryItem[] restrictedItem=null;
			Object[] params = new Object[1];
			params[0] = id;
			String rqlQuery=restrictedProperty +"=?0";
			restrictedItem = this.executeRQLQuery(rqlQuery, params,itemDescriptor, this.getCatalogRepository());
			if(restrictedItem!=null && restrictedItem.length>0){
				return true;
			}
		}
		return false;

	}

	// Getting Default View [STRAT] 
	/* (non-Javadoc)
	 * @see com.bbb.commerce.catalog.BBBCatalogTools#getDefaultPLPView(java.lang.String)
	 */
	/**
	 * This method is to getDefaultPLP view.
	 */
	public String getDefaultPLPView(String siteId) throws BBBSystemException {

		return getSiteRepositoryTools().getDefaultPLPView(siteId);
	}

	// Getting Default View [END]
	
	/**
	 * This method checks for vendor inclusion eligibility for the commerce item currently
	 * been iterated. If vendor id matches, then it checks whether or not dept id is 0. If 
	 * yes, then vendor inclusion rule is satisfied & returns true else it checks whether the
	 * dept, sub-dept and class inclusion flag is true or not.
	 * 
	 * It also checks if vendor id is 0, and if dept, sub-dept and class inclusion flag is true,
	 * then it returns true as Dept, Sub Dept & Class rule gets satisfied.
	 *
	 * @param ruleVendorId the rule vendor id
	 * @param skuVendorId the sku vendor id
	 * @param ruleDeptId the rule dept id
	 * @param isDeptSubDeptClassInclusive the is dept sub dept class inclusive
	 * @return boolean flag
	 */
	@Override
	public  boolean checkForVendorInclusion(String ruleVendorId,
			String skuVendorId, String ruleDeptId, boolean isDeptSubDeptClassInclusive) {
		
		// Checks if coupon rule's vendor id is non zero and matches with the sku vendor id.
		if (!BBBUtility.isEmpty(ruleVendorId) && !(BBBCoreConstants.STRING_ZERO.equalsIgnoreCase(ruleVendorId))
				&& ruleVendorId.equalsIgnoreCase(skuVendorId)) {
			
			//If vendor matches, then check if coupon rule's dept id is 0 & returns true.
			if (BBBCoreConstants.STRING_ZERO.equalsIgnoreCase(ruleDeptId)) {
				return true;
			} 
			// if dept id is non zero, then based on dept, sub-dept and class inclusion flag returns true
			else if (isDeptSubDeptClassInclusive) {
				return true;
			}
		}
		// checks if vendor id is 0, and if dept, sub-dept and class inclusion flag is true,then it returns
		// true as Dept, Sub Dept & Class rule gets satisfied.
		if (isDeptSubDeptClassInclusive && BBBCoreConstants.STRING_ZERO.equalsIgnoreCase(ruleVendorId)) {
			return true;
		}
		
		return false;
	}

	/**
	 * This method checks for the inclusion eligibility based on Department Id, Sub-Dept
	 * id and class and returns true based on which condition gets satisfied.If no condition
	 * is satisfied then returns false.
	 *
	 * @param ruleDeptId the rule dept id
	 * @param skuJdaDeptId the sku jda dept id
	 * @param ruleSubDeptId the rule sub dept id
	 * @param skuJdaSubDeptId the sku jda sub dept id
	 * @param ruleClass the rule class
	 * @param skuJdaClass the sku jda class
	 * @return boolean flag
	 */
	@Override
	public  boolean checkForDeptSubDeptClassInclusion(String ruleDeptId,
			String skuJdaDeptId, String ruleSubDeptId, String skuJdaSubDeptId,
			String ruleClass, String skuJdaClass) {
		
		// Checks if coupon rule's dept. id is non zero and matches with the sku dept id.
		if (!BBBUtility.isEmpty(ruleDeptId) && !(BBBCoreConstants.STRING_ZERO.equalsIgnoreCase(ruleDeptId))
				&& ruleDeptId.equalsIgnoreCase(skuJdaDeptId)) {
			//If dept id matches, then check if coupon rule's sub dept id is 0 & returns true.
			if (BBBCoreConstants.STRING_ZERO.equalsIgnoreCase(ruleSubDeptId)) {
				return true;
			}
			// check if coupon rule's sub dept. id is non zero and matches with the sku sub dept id.
			else if (!BBBUtility.isEmpty(ruleSubDeptId) && ruleSubDeptId.equalsIgnoreCase(skuJdaSubDeptId)) {
				//If sub dept id matches, then check if coupon rule's class is 0 & returns true.
				if (BBBCoreConstants.STRING_ZERO.equalsIgnoreCase(ruleClass)) {
					return true;
				}
				// checks if coupon rule's class is non zero and matches with the sku class id.
				else if (!BBBUtility.isEmpty(ruleClass) && ruleClass.equalsIgnoreCase(skuJdaClass)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * This method is used to get the the ImageVo for the Product.
	 * This was added as a part of BBBSL-3022 performance fix
	 *
	 * @param pProductId the product id
	 * @return the product images
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	public ImageVO getProductImages(String pProductId) throws BBBBusinessException, BBBSystemException{
		final ImageVO productImage=new ImageVO();
		try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getProductImages");
            final RepositoryItem productRepositoryItem = this.getCatalogRepository().getItem(pProductId,
                        BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
            if (productRepositoryItem == null) {
            throw new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
                            BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);
            }
            
            //Populating Images
            if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.LARGE_IMAGE_IMAGE_PROPERTY_NAME)!=null){
				final String largeImagePath= (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.LARGE_IMAGE_IMAGE_PROPERTY_NAME);
				productImage.setLargeImage(largeImagePath);
			}
            if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME)!=null){
 				final String mediumImagePath= (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME);
 				productImage.setMediumImage(mediumImagePath);
 			}
			if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_IMAGE_PROPERTY_NAME)!=null){
				final String smallImagePath= (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_IMAGE_PROPERTY_NAME);
				productImage.setSmallImage(smallImagePath);
			}
			if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.REGULAR_IMAGE_IMAGE_PROPERTY_NAME)!=null){
				final String regularImagePath= (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.REGULAR_IMAGE_IMAGE_PROPERTY_NAME);
				productImage.setSmallImage(regularImagePath);
			}
			if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.COLLECTION_THUMBNAIL_PRODUCT_PROPERTY_NAME)!=null){
 				final String collectionThumbnailImagePath= (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.COLLECTION_THUMBNAIL_PRODUCT_PROPERTY_NAME);
 				productImage.setCollectionThumbnailImage(collectionThumbnailImagePath);
 			}
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getProductImages]: RepositoryException ");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductImages");
        } 
    	return productImage;
	}
	
	/**
	 * This method is used to return the value of EComFulfillment property in thSKU repository Item.
	 * This was added as part of BBBSL-3018 performance fix
	 *
	 * @param pSkuId the sku id
	 * @return the sku e com fulfillment
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	public String getSkuEComFulfillment(String pSkuId) throws BBBBusinessException, BBBSystemException {
		this.logDebug("Catalog API Method Name [getSkuEComFulfillment] skuId " + pSkuId);
        RepositoryItem skuRepositoryItem = null;

        BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getSkuEComFulfillment");
        String ecomFullfillment = null;

        try {
            skuRepositoryItem = this.getCatalogRepository().getItem(pSkuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
            if (skuRepositoryItem == null) {
                this.logDebug("Repository Item is null for skuId " + pSkuId);
                throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
            }
                        
			this.logDebug("Getting value for ecom flag in sku ECOM FLAG VALUE ["
					+ skuRepositoryItem
							.getPropertyValue(BBBCatalogConstants.ECOM_FULFILLMENT_SKU_PROPERTY_NAME)
					+ "]");
			if (skuRepositoryItem
					.getPropertyValue(BBBCatalogConstants.ECOM_FULFILLMENT_SKU_PROPERTY_NAME) != null) {
				ecomFullfillment = (String) skuRepositoryItem
						.getPropertyValue(BBBCatalogConstants.ECOM_FULFILLMENT_SKU_PROPERTY_NAME);
			}
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getSkuEComFulfillment]: RepositoryException ");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSkuEComFulfillment");
        }
        return ecomFullfillment;
	}
	
	// End   || BBBSL-3018 || PS - Oct
	

	/**
	 * Gets the object cache.
	 *
	 * @return the mObjectCache
	 */
	public BBBObjectCache getObjectCache() {
		return this.mObjectCache;
	}

	/**
	 * Sets the object cache.
	 *
	 * @param pObjectCache the new object cache
	 */
	public void setObjectCache(final BBBObjectCache pObjectCache) {
		this.mObjectCache = pObjectCache;
	}

	/**
	 * Gets the bbb inventory manager.
	 *
	 * @return the bbb inventory manager
	 */
	public BBBInventoryManager getBbbInventoryManager() {
		return bbbInventoryManager;
	}

	/**
	 * Sets the bbb inventory manager.
	 *
	 * @param bbbInventoryManager the new bbb inventory manager
	 */
	public void setBbbInventoryManager(BBBInventoryManager bbbInventoryManager) {
		this.bbbInventoryManager = bbbInventoryManager;
	}

	/**
	 * Gets the cms tools.
	 *
	 * @return the mCmsTools
	 */
	public CmsTools getCmsTools() {
		return mCmsTools;
	}

	/**
	 * Sets the cms tools.
	 *
	 * @param mCmsTools the mCmsTools to set
	 */
	public void setCmsTools(CmsTools mCmsTools) {
		this.mCmsTools = mCmsTools;
	}

	/**
	 * Gets the BBB site to attribute site map.
	 *
	 * @return the bbbSiteToAttributeSiteMap
	 */
	public Map<String, String> getBBBSiteToAttributeSiteMap() {
		return bbbSiteToAttributeSiteMap;
	}

	/**
	 * Sets the bbb site to attribute site map.
	 *
	 * @param bbbSiteToAttributeSiteMap the bbbSiteToAttributeSiteMap to set
	 */
	public void setBBBSiteToAttributeSiteMap(
			Map<String, String> bbbSiteToAttributeSiteMap) {
		this.bbbSiteToAttributeSiteMap = bbbSiteToAttributeSiteMap;
	}
	
	   /**
   	 *  
   	 * The method gets the state tax details corresponding to a state code.
   	 *
   	 * @param stateCode the state code
   	 * @return the state tax
   	 */
	   public double getStateTax(final String stateCode) {
		   
		   return getShippingRepositoryTools().getStateTax(stateCode);

	
	   }
	   
	   
	   /**
   	 *  
   	 * The method gets the zoom index corresponding to a product id.
   	 *
   	 * @param productId the product id
   	 * @param siteId the site id
   	 * @return zoomIndex
   	 */
		public int getZoomIndex(String productId, String siteId)
		{
			String zoomIndex = null;
			try
			{
				Map<String, CategoryVO> category = this.getParentCategoryForProduct(productId, siteId);
				if(!BBBUtility.isMapNullOrEmpty(category))
				{
					CategoryVO l2Category = category.get(L2CATEGORY);
					this.getBccManagedCategory(l2Category);
					zoomIndex = l2Category.getZoomValue();
				}
			}catch(BBBBusinessException businessException){
		 		logError("Error while Fetching Zoom Index from repository : " + "Product Id " + productId + " is not present in the repository" + businessException);
		 	}
			catch(BBBSystemException systemException){
		 		logError("Error while Fetching Zoom Index from repository : " + systemException);
		 	}
			
			if(BBBUtility.isEmpty(zoomIndex) || !BBBUtility.isFloat(zoomIndex)) {
				zoomIndex=ZOOM_MAX;
			} else {
				if(Float.parseFloat(zoomIndex) < 1) {
					zoomIndex=ZOOM_MIN;
				} else if (Float.parseFloat(zoomIndex) > 5) {
					zoomIndex=ZOOM_MAX;
				} else {
					return Math.round(Float.parseFloat(zoomIndex));
				}
			}
			return Integer.parseInt(zoomIndex);
		}
		
		/**
		 * Gets the currency tag converter.
		 *
		 * @return the currency tag converter
		 */
		public BBBCurrencyTagConvertor getCurrencyTagConverter() {
		return currencyTagConverter;
	}

	/**
	 * Sets the currency tag converter.
	 *
	 * @param currencyTagConverter the new currency tag converter
	 */
	public void setCurrencyTagConverter(BBBCurrencyTagConvertor currencyTagConverter) {
		this.currencyTagConverter = currencyTagConverter;
	}
	
	/**
	 *  
	 * The method takes AttributeInfo repository Item and check for its international applicability.
	 *
	 * @param attributeInfo the attribute info
	 * @param internationalShippingContext the international shipping context
	 * @return boolean
	 */
	
	public boolean checkAttributeIntlApplicability( RepositoryItem attributeInfo, boolean internationalShippingContext ) {
		boolean skipIntlAttribute = false;
		if (attributeInfo != null) {
			String attrIntlFlag = (String) attributeInfo.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_FLAG);
            if (internationalShippingContext && !BBBUtility.isEmpty(attrIntlFlag)  && BBBCoreConstants.NO_CHAR.equalsIgnoreCase(attrIntlFlag)) {
            	this.logDebug("Attribute " + attributeInfo.getRepositoryId() + " is not applicable for International Context so will be skipped");
				skipIntlAttribute = true;
			} else if (!internationalShippingContext &&!BBBUtility.isEmpty(attrIntlFlag) && BBBCatalogConstants.INTL_ONLY_CHAR.equalsIgnoreCase(attrIntlFlag) ) {
				this.logDebug("Attribute " + attributeInfo.getRepositoryId() + " is only applicable for International Context so will be skipped");
				skipIntlAttribute = true;
			}
		}
		return skipIntlAttribute;	
	}

	
	/**
	 *  
	 * The method takes componentPath and returns the resolved the component from Global Nucleus.
	 *
	 * @param componentPath the component path
	 * @return Object
	 */

	@Override
	public Object resolveComponentFromNucleus(String componentPath) {
		Object component = null;
    	if(Nucleus.getGlobalNucleus() != null && !BBBUtility.isEmpty(componentPath)){
    		component = Nucleus.getGlobalNucleus().resolveName(componentPath);
    	}
		return component;
	}
	
	/**
	 *  
	 * The method takes componentPath and returns the resolved the component from current request.
	 *
	 * @param componentPath the component path
	 * @return Object
	 */

	@Override
	public Object resolveComponentFromRequest(String componentPath) {
		Object component = null;
    	if(ServletUtil.getCurrentRequest() != null && !BBBUtility.isEmpty(componentPath)){
    		component = ServletUtil.getCurrentRequest().resolveName(componentPath);
    	}
		return component;
	}

	   /**
   	 *
	 * The method takes componentPath and returns the resolved the component from Global Nucleus.
	 *
	 * @param pSiteId the site id
	 * @param pProductId the product id
	 * @param populateRollUp the populate roll up
	 * @param isMinimalDetails the is minimal details
	 * @param isAddException the is add exception
	 * @return Object
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	@Override
    public final ProductVO getEverLivingMainProductDetails(final String pSiteId, final String pProductId,
            final boolean populateRollUp, final boolean isMinimalDetails, final boolean isAddException)
            throws BBBSystemException, BBBBusinessException {
        final StringBuilder debug = new StringBuilder(50);
        debug.append("Catalog API Method Name [getEverLivingProductDetails] siteId[").append(pSiteId)
                            .append("] pProductId [").append(pProductId);
        this.logDebug(debug.toString());
     
        final long startTime = System.currentTimeMillis();
        if (!StringUtils.isEmpty(pSiteId)) {
            ProductVO productVO;
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_1");
                final RepositoryItem productRepositoryItem = this.getCatalogRepository().getItem(pProductId,
                                BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
                if (productRepositoryItem == null) {
                	 BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_1");
                    throw new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
                                    BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);
                }

                final boolean isEverLiving = this.isEverlivingProduct(pProductId,pSiteId);
                
                this.logDebug(pProductId + " Product is isEverLiving [" + isEverLiving + "]");
                if (!isEverLiving && isAddException) {
                	 BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_1");
                    throw new BBBBusinessException(
                                    BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY,
                                    BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY);
                }

                boolean isCollection = false;
                boolean isLeadProduct = false;
                if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) != null) {
                    isCollection = ((Boolean) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME))
                                    .booleanValue();
                }
                if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) != null) {
                    isLeadProduct = ((Boolean) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME))
                                    .booleanValue();
                }
                this.logTrace("Product is a Collection [" + isCollection + "] product is a lead product ["
                                + isLeadProduct + "]");
                if ((isCollection || isLeadProduct) && !isMinimalDetails) {
                    @SuppressWarnings ("unchecked")
                    final List<RepositoryItem> childProductsRelationList = (List<RepositoryItem>) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME);
                    if (!BBBUtility.isListEmpty(childProductsRelationList)) {
                        final List<ProductVO> subProductVOList = new ArrayList<ProductVO>();
                        this.logDebug("No of child Products [" + childProductsRelationList.size() + "]");
                        RepositoryItem childProdItem = null;
                        
                        
                        String childProductId = null;
                        boolean giftFlag = false;

                        for (int i = 0; i < childProductsRelationList.size(); i++) {

                            childProdItem = (RepositoryItem) childProductsRelationList.get(i).getPropertyValue(
                                            BBBCatalogConstants.CHILD_PRODUCTS_PRODUCT_RELATION_PROPERTY_NAME);
                            if (null != childProdItem.getPropertyValue("giftCertProduct")) {
                                giftFlag = ((Boolean) childProdItem.getPropertyValue("giftCertProduct")).booleanValue();
                            }

                            childProductId = childProdItem.getRepositoryId();
                            this.logDebug("Product Id of " + i + "th child  [" + childProductId + "]");
                            // check if sub product is active only then add to list
                            if (this.isProductActive(childProdItem)) {
                                @SuppressWarnings ("unchecked")
                                ProductVO subProductVO = new ProductVO();
                                subProductVO.setProductId(childProductId);
                                subProductVOList.add(subProductVO);
                            
                            }else if(this.isEverlivingProduct(childProductId,pSiteId)) {
                            	@SuppressWarnings ("unchecked")
                                ProductVO subProductVO = new ProductVO();
                                subProductVO.setProductId(childProductId);
                                subProductVOList.add(subProductVO);
                            } else {
                                this.logDebug("sub product with Product Id   [" + childProductId
                                                + "] is diabled so not including in list of sub products");
                            }
                        }
                        productVO = this.getCollectionProductVO(productRepositoryItem, subProductVOList, pSiteId);
                        // If all child SKU have giftCertProduct true then set this true
                        productVO.setGiftCertProduct(Boolean.valueOf(giftFlag)); 
                        //setting everLiving flag for collections and accessories
                        productVO.setIsEverLiving(isEverLiving);
                        // Logic for View Product guide link on PDP.
                        final String prodGuideId = this.getProductGuideId(productRepositoryItem, pSiteId);
                        if (!BBBUtility.isEmpty(prodGuideId)) {
                            productVO.setShopGuideId(prodGuideId);
                        }
                        this.logTrace(productVO.toString());
                        return productVO;
                    }
                    this.logError("catalog_1006: Product is a collection but has no child products");
                    throw new BBBBusinessException(
                                    BBBCatalogErrorCodes.CHILD_PRODUCTS_NOT_PRESENT_FOR_COLLECTION_PRODUCT,
                                    BBBCatalogErrorCodes.CHILD_PRODUCTS_NOT_PRESENT_FOR_COLLECTION_PRODUCT);
                }
                this.logTrace("Product is not a Collection ");
                return this.getEverLivingProductVO(productRepositoryItem, pSiteId, populateRollUp, isMinimalDetails, isEverLiving);
            } catch (final RepositoryException e) {
                this.logError("Catalog API Method Name [getProductDetails]: RepositoryException ");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_1");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
            } finally {
                final long totalTime = System.currentTimeMillis() - startTime;
                this.logDebug("Total time taken for BBBCatalogTools.getProductDetails() is: " + totalTime
                                + " for product id: " + pProductId + " and minimal details is: " + isMinimalDetails);
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_1");
            }
        }
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }
	
/**
 * The method takes shipping methood and sku Id and then check the shipping method from the eligible shipping methods of sku.
 *
 * @param siteId the site id
 * @param skuId the sku id
 * @param shipMethodId the ship method id
 * @param isAssemblyFeeOfferedForCI the is assembly fee offered for ci
 * @return boolean
 * @throws BBBSystemException the BBB system exception
 * @throws BBBBusinessException the BBB business exception
 */
	public final boolean isShippingMethodExistsForSku(final String siteId, final String skuId, final String shipMethodId, final boolean isAssemblyFeeOfferedForCI)
    		throws BBBSystemException, BBBBusinessException{
    	this.logDebug("Catalog API Method Name [isShippingMethodExistsForSku] Parameter siteId[" + siteId
                + "] Parameter skuId[" + skuId + "] Parameter ShippingMethodId[" + shipMethodId + "] Parameter isAssemblyFeeOfferedForCI[" +isAssemblyFeeOfferedForCI + "]");

    	boolean isExists = false;
    	boolean sameDayDeliveryFlag = false;
 		String sddEligibleOn = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG);
 		if(null != sddEligibleOn){
 			sameDayDeliveryFlag = Boolean.valueOf(sddEligibleOn);
 		}
    	//Get the eligible shipping methods
    	final List<ShipMethodVO> shipMethodsVo = getShippingMethodsForSku(siteId, skuId, sameDayDeliveryFlag);
    	if (!BBBUtility.isListEmpty(shipMethodsVo)) {
    		for (final Iterator itr = shipMethodsVo.iterator(); itr.hasNext();)
    		{
    			final ShipMethodVO shipMethodVo = (ShipMethodVO)itr.next();
    			if(null != shipMethodVo.getShipMethodId() && shipMethodVo.getShipMethodId().equalsIgnoreCase(shipMethodId)){
    				isExists =  true;
    			}
    		}
    	}
    	//Check for assembly in case the shiping method is white glove with assembly
    	if(isExists && shipMethodId.equalsIgnoreCase(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD) && isAssemblyFeeOfferedForCI){
    		this.logDebug("Catalog API Method Name [isShippingMethodExistsForSku] Checking for assembly flag.. " );
    		final boolean isAssemblyFeeOfferedForSku = this.isAssemblyFeeOffered(skuId);
    		if(isAssemblyFeeOfferedForSku){
    			isExists =  true;
    		}else{
    			isExists = false;
    		}
    	}
    	this.logDebug("Catalog API Method Name [isShippingMethodExistsForSku] Returning " + isExists + " for Shipping method ID : " + shipMethodId);
    	return isExists;
    }

	/**
	 * Get the VDC Offset message from label and actual offset date.
	 *
	 * @param skuId the sku id
	 * @param siteId the site id
	 * @return String offsetMessage
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public String getActualOffsetMessage(String skuId, String siteId) throws BBBSystemException, BBBBusinessException{

		this.logDebug("Start :: Catalog API Method Name [getActualOffsetMessage]");
		String offsetMessage = EMPTYSTRING;

		//Get the actual Offset date
		String offsetDateVDC = getActualOffsetDate(siteId, skuId);

		if(BBBUtility.isNotEmpty(offsetDateVDC)){
				//Replace the placeholder in message label with the actual offset date
			 Map<String,String> vdcPlaceHolder = new HashMap<String, String>();
    		 vdcPlaceHolder.put(BBBCoreConstants.ACTUAL_OFF_SET_DATE_PLHOLDER, offsetDateVDC);
    		 offsetMessage =  getLblTxtTemplateManager().getPageTextArea(BBBCoreConstants.TXT_VDC_OFFSET_MSG, null,
    				 	vdcPlaceHolder,siteId);
		}
		this.logDebug("Catalog API Method Name [getActualOffsetMessage] :: The actual offset date is :" + offsetDateVDC + " for skuId :" + skuId
				+ " . Also the complete message after replacing the placeholder is :" + offsetMessage + "End :: Catalog API Method Name [getActualOffsetMessage]");
		return offsetMessage;
	}

	/**
	 * Get the VDC message from Label and the VDC Expected Deliverty Time.
	 *
	 * @param skuId the sku id
	 * @param requireMsgInDate the require msg in date
	 * @param shippingMethod the shipping method
	 * @param inputDate the input date
	 * @param fromPDP the from pdp
	 * @return String vdcMessage
	 * @throws BBBSystemException the BBB system exception
	 */
	public String getVDCShipMessage(String skuId, boolean requireMsgInDate, String shippingMethod, Date inputDate, boolean fromPDP) throws BBBSystemException{

		this.logDebug("Start :: Catalog API Method Name [getVDCShipMessage]");
		String vdcShipMessage = EMPTYSTRING;
		String vdcDelTime = getExpectedDeliveryTimeVDC(shippingMethod, skuId, requireMsgInDate , inputDate, false);
		logDebug("BBBCatalogToolImpl: [getVDCMessage]" + "Delivery message time for sku : " + skuId + " is : " + vdcDelTime);

		if(BBBUtility.isNotEmpty(vdcDelTime)){
			//Replace the placeholder in message label with the actual vdc Delivery time
			Map<String,String> vdcPlaceHolder = new HashMap<String, String>();
			vdcPlaceHolder.put(BBBCoreConstants.VDC_DEL_TIME, vdcDelTime);
			boolean isSkuLTL = false;
			try {
				isSkuLTL = this.isSkuLtl(BBBUtility.getCurrentSiteId(ServletUtil.getCurrentRequest()), skuId);
			} catch (BBBBusinessException e) {
				logError("An error occurred while determining whether sku is LTL or not :-" + skuId + " " + e.getMessage(), e);
			}
			
			
			boolean personalizationOffered = false;
			 try {
				 SKUDetailVO skuVO = this.getSKUDetails(BBBUtility.getCurrentSiteId(ServletUtil.getCurrentRequest()), skuId);
				 if(null != skuVO && skuVO.isCustomizationOffered())
				 {
					 personalizationOffered = skuVO.isCustomizationOffered();
				 }
			 } catch (BBBBusinessException e) {
			 	logError("An error occurred while determining whether sku is LTL or not :-" + skuId, e);
			 }

			//Get a different label value if request is from PDP as message to be shown on PDP is different
			if(fromPDP){
				if(personalizationOffered || isSkuLTL){
					 vdcShipMessage = getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_VDC_DEL_LTL_MSG, null, vdcPlaceHolder,getCurrentSiteId());
				}else{
					 vdcShipMessage = getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_VDC_DEL_REST_MSG, null, vdcPlaceHolder,getCurrentSiteId());
				}
			}else{
				vdcShipMessage = getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_VDC_DEL_REST_CART_MSG, null, vdcPlaceHolder,getCurrentSiteId());
			}
		}
		this.logDebug("Catalog API Method Name [getVDCShipMessage] :: The actual offset date is :" + vdcDelTime + " for skuId :" + skuId
				+ " . Also the complete message after replacing the placeholder is :" + vdcShipMessage + "End :: Catalog API Method Name [getVDCShipMessage]");
		return vdcShipMessage;

	}

	/**
	 *  BPSI 1940- This method is used to drive the actual offset date from date configure in date to show VDC offset message.
	 *
	 * @param siteId the site id
	 * @param skuId the sku id
	 * @return offset date string
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	public final String getActualOffsetDate(final String siteId,final String skuId) throws BBBBusinessException, BBBSystemException {
		this.logDebug("Catalog API Method Name [getActualOffsetDate] siteId ["+siteId+"] skuId ["+skuId+"] Entry");
		String offSetDateString= EMPTYSTRING;
		if (!StringUtils.isEmpty(siteId) && !StringUtils.isEmpty(skuId)) {
			try {
				BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getActualOffsetDate");

				List<String> vdcOffSetDate = getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.VDC_OFFSET_DATE);

				if(!StringUtils.isEmpty(vdcOffSetDate.get(0))){
					SimpleDateFormat formatter = new SimpleDateFormat(BBBCoreConstants.US_DATE_FORMAT);
					Date offSetDate = formatter.parse(vdcOffSetDate.get(0));
					Calendar offSetDateCal = Calendar.getInstance();
					offSetDateCal.setTime(offSetDate);

					Integer offSetDays = null;
					final RepositoryItem skuRepositoryItem = getCatalogRepository().getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
					if(null != skuRepositoryItem && null != skuRepositoryItem.getPropertyValue(BBBCoreConstants.SHIPPING_CUT_OFF_OFFSET)){
						offSetDays = (Integer)skuRepositoryItem.getPropertyValue(BBBCoreConstants.SHIPPING_CUT_OFF_OFFSET);
					}
					if(offSetDays !=null && offSetDays != 0){
						//BBBSL-9000 - Remove Holiday and weekend logic.
						offSetDateCal.add(Calendar.DATE, offSetDays);
					}
					offSetDateString = getSiteBasedFormattedDate(siteId, offSetDateCal, false);
				}
			}
			catch (ParseException e) {
				this.logDebug("ERROR: could not parse date");
			}
			catch (RepositoryException e) {
				this.logDebug("Catalog API Method Name [getActualOffsetDate]: RepositoryException ");
	            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
	                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
			} finally {
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getActualOffsetDate");
			}
		}
		this.logDebug("Catalog API Method Name [getActualOffsetDate] siteId ["+siteId+"] skuId ["+skuId+"] Exit");
		return offSetDateString;

	}


	/**
	 * Method for fetching Delivery dates based on VDC specific SKUs.BPSI 1928.
	 * requireMsgInDate - true : retruns vdc delivery time in Dates
	 * requireMsgInDate - false : returns vdc delivery time in days
	 *
	 * @param shippingMethod the shipping method
	 * @param skuID the sku id
	 * @param requireMsgInDate the require msg in date
	 * @param inputDate the input date
	 * @param includeYearFlag the include year flag
	 * @param fromShippingPage the from shipping page
	 * @return the expected delivery time vdc
	 * @throws BBBSystemException the BBB system exception
	 */
	public String getExpectedDeliveryTimeVDC(String shippingMethod, String skuID, boolean requireMsgInDate, Date inputDate, boolean includeYearFlag, boolean fromShippingPage) throws BBBSystemException{
		
		return getShippingRepositoryTools().getExpectedDeliveryTimeVDC(shippingMethod, skuID, requireMsgInDate, inputDate, includeYearFlag, fromShippingPage);
	}

   /* (non-Javadoc)
    * @see com.bbb.commerce.catalog.BBBCatalogTools#getExpectedDeliveryTimeVDC(java.lang.String, java.lang.String, boolean, java.util.Date, boolean)
    */
   public String getExpectedDeliveryTimeVDC(String shippingMethod, String skuID, boolean requireMsgInDate, Date inputDate, boolean includeYearFlag) throws BBBSystemException{
		return getExpectedDeliveryTimeVDC(shippingMethod, skuID, requireMsgInDate, inputDate, includeYearFlag, false);
	}
   
   
   /**
	 * Rem duplicate attributes.
	 *
	 * @param allAttributes the all attributes
	 * @return the list
	 */
	private static List<AttributeVO> remDuplicateAttributes( List<AttributeVO> allAttributes){

		List<AttributeVO> newAttributesList = null;

		if( allAttributes!=null ){
			Set<AttributeVO> hashSet = new HashSet<AttributeVO>(allAttributes);

			newAttributesList = new ArrayList<AttributeVO>(hashSet);
		}
		return newAttributesList;
	}
	

	/**
	 * Gets the shipping duration rql query.
	 *
	 * @return shippingDurationRqlQuery
	 */
	public String getShippingDurationRqlQuery() {
		return shippingDurationRqlQuery;
	}
	
	/**
	 * Sets the shipping duration rql query.
	 *
	 * @param shippingDurationRqlQuery the new shipping duration rql query
	 */
	public void setShippingDurationRqlQuery(String shippingDurationRqlQuery) {
		this.shippingDurationRqlQuery = shippingDurationRqlQuery;
	}
	
	/**
	 * Gets the shipping method rql query.
	 *
	 * @return shippingMethodRqlQuery
	 */
	public String getShippingMethodRqlQuery() {
		return shippingMethodRqlQuery;
	}
	
	/**
	 * Sets the shipping method rql query.
	 *
	 * @param shippingMethodRqlQuery the new shipping method rql query
	 */
	public void setShippingMethodRqlQuery(String shippingMethodRqlQuery) {
		this.shippingMethodRqlQuery = shippingMethodRqlQuery;
	}

	/**
	 * Gets the customization offered site map.
	 *
	 * @return the customizationOfferedSiteMap
	 */
	public Map<String, String> getCustomizationOfferedSiteMap() {
		return this.customizationOfferedSiteMap;
	}

	/**
	 * Sets the customization offered site map.
	 *
	 * @param customizationOfferedSiteMap the customizationOfferedSiteMap to set
	 */
	public void setCustomizationOfferedSiteMap(
			Map<String, String> customizationOfferedSiteMap) {
		this.customizationOfferedSiteMap = customizationOfferedSiteMap;
	}

	/**
	 * Gets the vendor repository.
	 *
	 * @return the vendorRepository
	 */
	public MutableRepository getVendorRepository() {
		return this.vendorRepository;
	}

	/**
	 * Sets the vendor repository.
	 *
	 * @param vendorRepository the vendorRepository to set
	 */
	public void setVendorRepository(MutableRepository vendorRepository) {
		this.vendorRepository = vendorRepository;
	}
	
	
	/**
	 * Gets the exim manager.
	 *
	 * @return the exim manager
	 */
	public BBBEximManager getEximManager() {
	return eximManager;
    }


   /**
    * Sets the exim manager.
    *
    * @param eximManager the new exim manager
    */
   public void setEximManager(BBBEximManager eximManager) {
	this.eximManager = eximManager;
   }   
	   
   /**
    * Gets the vendor configuration js.
    *
    * @param productId the product id
    * @param section the section
    * @param pageWrapper the page wrapper
    * @param desktopOrMobile the desktop or mobile
    * @return the vendor configuration js
    * @throws BBBSystemException the BBB system exception
    * @throws BBBBusinessException the BBB business exception
    */
	public Set<String> getVendorConfigurationJS(String productId, String section, String pageWrapper,String desktopOrMobile) throws BBBSystemException, BBBBusinessException{
		
		logDebug("GetVendorConfigurationJS method");
		logDebug("Product Id" + productId + " Seciton = " + section + "pageWrapper = " + pageWrapper);
				
		if(pageWrapper.contains(BBBCatalogConstants.PRODUCT_DETAILS)){
			
			return getVendorConfigurationForPDP(productId,desktopOrMobile);
			
		}else if(section.equalsIgnoreCase(BBBCoreConstants.CART) || section.equalsIgnoreCase(BBBCatalogConstants.CART_DETAIL) || pageWrapper.contains(BBBCoreConstants.WISHLIST)){			
			
			return getVendorConfigurationForCartAndWishlist(desktopOrMobile);
			
		}else{			
			return null;
		}
	}
	
	/**
	 * Gets the vendor configuration for cart and wishlist.
	 *
	 * @param channel the channel
	 * @return the vendor configuration for cart and wishlist
	 */
	public Set<String> getVendorConfigurationForCartAndWishlist(String channel) {
		logDebug("Catalog API Method Name [getVendorConfigurationForCartAndWishlist] for channel ["+channel+"] Entry");
		logTrace("Fetching vendor Configuration for the cart or wishlist page");
		
		Set<String> vendorSet = new HashSet<String>();
		Object[] params = null;
		String rql= BBBEximConstants.ALL;
		params = new Object[]{};
		
		IRepositoryWrapper iRepositoryWrapper = new RepositoryWrapperImpl(getVendorRepository());		
		RepositoryItem[] items;		
		items = queryRepositoryItems(params, rql, iRepositoryWrapper);
		String strVendorJS = null;
		
		if(items !=null && items.length>0){
			for(RepositoryItem item: items){
				if(channel.equals(BBBCoreConstants.CHANNEL_DESKTOP)){
					strVendorJS = (String)item.getPropertyValue(BBBCatalogConstants.VENDOR_JS);					
				}else{
					strVendorJS = (String)item.getPropertyValue(BBBCatalogConstants.VENDOR_JS_MOBILE);					
			}
				if(StringUtils.isNotEmpty(strVendorJS)) {
					vendorSet.add(strVendorJS);
				}
			}
		}else{
			strVendorJS = getDefaultConfiguration(channel);
			if(StringUtils.isNotEmpty(strVendorJS)) {
				vendorSet.add(strVendorJS);
		}
		}
		
		logTrace("The vendor JS for the wishlist or cart page is " + vendorSet.toString());
		logDebug("Catalog API Method Name [getVendorConfigurationForCartAndWishlist] for channel ["+channel+"] Exit");
		return vendorSet;
	}

	/**
	 * @param params
	 * @param rql
	 * @param iRepositoryWrapper
	 * @return
	 */
	public RepositoryItem[] queryRepositoryItems(Object[] params, String rql,
			IRepositoryWrapper iRepositoryWrapper) {
		return iRepositoryWrapper.queryRepositoryItems(BBBCatalogConstants.VENDOR_CONFIGURATION_DESCRIPTOR, rql, params,true);
	}

	/**
	 * Gets the default configuration.
	 *
	 * @param channel the channel
	 * @return the default configuration
	 */
	private String getDefaultConfiguration(String channel) {
		
		RepositoryItem vendorRepositoryItem = null;
		String strVendorJS = null;
		try {
			vendorRepositoryItem = this.getVendorRepository().getItem(BBBCatalogConstants.DEFAULT,BBBCatalogConstants.VENDOR_CONFIGURATION_DESCRIPTOR);		
			
			if(vendorRepositoryItem!=null){
				
				if(channel.equals(BBBCoreConstants.CHANNEL_DESKTOP)){
					strVendorJS = (String) vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_JS);
				}else{
					strVendorJS = (String) vendorRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_JS_MOBILE);
				}
			}
		} catch (RepositoryException e) {
			
			logError("Error fetching value of default vendor Id",e);
		}
		
		return strVendorJS;
	}

	/**
	 * Gets the vendor configuration for pdp.
	 *
	 * @param productId the product id
	 * @param channel the channel
	 * @return the vendor configuration for pdp
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public Set<String> getVendorConfigurationForPDP(String productId, String channel) throws BBBSystemException, BBBBusinessException {
		
		logDebug("Catalog API Method Name [getVendorConfigurationForPDP] for productId ["+productId+"] channel ["+channel+"] Entry");
		logTrace("Fetching vendor configuaration for the PDP page");
		
		Set<String> vendorJs = new HashSet<String>();
		ProductVO productVO = null;
		String strVendorJS = null;
		boolean fetchDefaultVendor = true;
		
		if(productId != null){
			productVO = this.getProductVOMetaDetails(getCurrentSiteId(), productId);
		if(productVO.getVendorId() != null){			
				fetchDefaultVendor = false;
			if(channel.equalsIgnoreCase(BBBCoreConstants.CHANNEL_DESKTOP)){
				strVendorJS = productVO.getVendorInfoVO().getVendorJS();
			}else{
				strVendorJS = productVO.getVendorInfoVO().getVendorMobileJS();				
			}
			}
		}
			
		if(fetchDefaultVendor){
			strVendorJS = getDefaultConfiguration(channel);
		}
			
		if(StringUtils.isNotEmpty(strVendorJS)) {
			vendorJs.add(strVendorJS);
		}
		logDebug("The vendor JS for the product description page is " + vendorJs);
		
		logTrace("The vendor JS for the product description page is " + vendorJs.toString());
		logDebug("Catalog API Method Name [getVendorConfigurationForPDP] for productId ["+productId+"] channel ["+channel+"] Exit");
		
		return vendorJs;
	}
		

	/**
	 * This method gets the parent category id for a particular product.
	 *
	 * @param productId the product id
	 * @param siteId the site id
	 * @return Parent Category ID
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	@Override
	public String getParentCategoryIdForProduct(final String productId,
			final String siteId) throws BBBBusinessException,
			BBBSystemException {
		this.logDebug("Catalog API Method Name [getParentCategoryIdForProduct] productId["
				+ productId + "]");
		if (!StringUtils.isEmpty(productId)) {
			String parentCategoryId = EMPTYSTRING;
			try {
				BBBPerformanceMonitor
						.start(BBBPerformanceConstants.CATALOG_API_CALL
								+ " getParentCategoryIdForProduct");
				final RepositoryItem productRepositoryItem = this
						.getCatalogRepository().getItem(productId,
								BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);

				if (productRepositoryItem != null) {
					this.logDebug("productRepositoryItem is not null for product id "
							+ productId);
					@SuppressWarnings("unchecked")
					final Set<RepositoryItem> parentCategorySet = (Set<RepositoryItem>) productRepositoryItem
							.getPropertyValue(BBBCatalogConstants.PARENT_CATEGORIES_PROPERTY_NAME);
					RepositoryItem categoryRepositoryItem = this
							.getActiveCategoryForSite(parentCategorySet, siteId);
					if (categoryRepositoryItem != null) {
						parentCategoryId = categoryRepositoryItem
								.getRepositoryId();
						this.logDebug(parentCategoryId
								+ " category id is the parent for product id "
								+ productId);
					} else {
						this.logError(new StringBuilder("Product Id ")
								.append(productId)
								.append(" does not have any parent category for site ")
								.append(siteId).toString());
					}
					return parentCategoryId;
				}
				this.logDebug("Product Id " + productId
						+ " is not present in the repository");
				throw new BBBBusinessException(
						BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
						BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);

			} catch (final RepositoryException e) {
				this.logError("Catalog API Method Name [getParentCategoryIdForProduct]: RepositoryException ");
				throw new BBBSystemException(
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
						e);
			} finally {
				this.logDebug("Catalog API Method Name [getParentCategoryIdForProduct] productId["
						+ productId + "] Exit");
				BBBPerformanceMonitor
						.end(BBBPerformanceConstants.CATALOG_API_CALL
								+ " getParentCategoryIdForProduct");
			}
		}
		throw new BBBBusinessException(
				BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
				BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
	}
	
	/**
	 * Returns true if challenge question flag is ON in BCC.
	 *
	 * @return true, if is challenge question on
	 */
	public boolean isChallengeQuestionOn() {
		boolean isChallengeQuestionOn = true;
		List<String> challengeQuestionFlag;
		try {
			challengeQuestionFlag = this.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.CHALLENGE_QUESTION_ON_OFF);
			if (challengeQuestionFlag != null && !challengeQuestionFlag.isEmpty()) {
				isChallengeQuestionOn = Boolean.parseBoolean(challengeQuestionFlag.get(0));
			}
		} catch (BBBSystemException | BBBBusinessException be) {
			final String errMsg = "System Exception occured while fetching key(challenge_question_flag) from FlagDrivenFunctions";
			this.logError(errMsg, be);
		}
		return isChallengeQuestionOn;
	}
	
	/**
	 * This is just a skeleton method whose actual implementation is in TBSCatalogToolsImpl.
	 *
	 * @param productItem the product item
	 * @param skuItem the sku item
	 * @return the product details for upc search
	 * @throws BBBBusinessException the BBB business exception
	 */
	@Override
	public BBBProduct getProductDetailsForUPCSearch(RepositoryItem productItem, RepositoryItem skuItem)
			throws BBBBusinessException {

		return null;
	}
	
	/**
	 * This is just a skeleton method whose actual implementation is in TBSCatalogToolsImpl.
	 *
	 * @param skuOrUpcId the sku or upc id
	 * @return the SKU for upc search
	 */
	@Override
	public RepositoryItem getSKUForUPCSearch(String skuOrUpcId) {

		return null;
	}
	
	/**
	 * This is just a skeleton method whose actual implementation is in TBSCatalogToolsImpl.
	 *
	 * @param siteId The site ID for which the threshold needs to be fetched eg TBS_BedBathUS, TBS_BuyBuyBaby, TBS_BedBathCanada
	 * @param type The fee type whose threshold needs to be fetched eg Item price, Ship fee, Tax, Gift Wrap price, Ship surcharge, Delivery fee, Assembly fee
	 * @return the override threshold value 
	 */
	@Override
	public double getOverrideThreshold(String siteId, String type) throws BBBBusinessException, BBBSystemException {

		return 0.0;
	}
	
	/**
	 * Set EDW profile data to session.
	 *
	 * @param profileId the profile id
	 * @param edwDataVO the edw data vo
	 * @return the profile edw info vo
	 * @throws NumberFormatException the number format exception
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 * @throws RepositoryException the repository exception
	 */
	
    public ProfileEDWInfoVO populateEDWProfileData(String profileId,ProfileEDWInfoVO edwDataVO) throws NumberFormatException, BBBSystemException, BBBBusinessException, RepositoryException{

    	//TODO - use this code post-Iliad-Live getEdwRepositoryTools().populateEDWProfileData(profileId, edwDataVO);
    	
    	return getEdwRepositoryTools().populateEDWProfileData(profileId, edwDataVO);
    }
	
	
	/**
	 * Gets the related categories. Uses the parentCategory
	 * of the product based on the site.
	 *
	 * @param productId the product id
	 * @param siteId the site id
	 * @return the related categories
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public List<CategoryVO> getRelatedCategories (final String productId, final String siteId) throws BBBSystemException, BBBBusinessException {
		this.logDebug("Catalog API Method Name [getRelatedCategories] productId	"+ productId + " site Id " + siteId );
		if (!BBBUtility.isEmpty(productId)) {
			try {
				final RepositoryItem productRepoItem = this
						.getCatalogRepository().getItem(productId,
								BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);

				if (productRepoItem != null) {
					this.logDebug("productRepositoryItem is not null for product id "
							+ productId);
					return this.getRelatedCategories(productRepoItem, siteId);
				}
			} catch (final RepositoryException e) {
	                this.logError("Catalog API Method Name [getRelatedCategories]: RepositoryException ");
	                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
	                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
	            }
	        } else {
				logError("getRelatedCategories :: Product id is null ");
	        }
		this.logDebug("Catalog API Method Name [getRelatedCategories] productId	"+ productId + " site Id " + siteId + " Exit");
	        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
	                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
	}
	
	
	
	/**
	 * Gets the related categories.
	 *
	 * @param productRepoItem the product repository item
	 * @param siteId the site id
	 * @return the related categories
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	public List<CategoryVO> getRelatedCategories (final RepositoryItem productRepoItem, final String siteId) throws BBBBusinessException, BBBSystemException {
		
		this.logDebug("Catalog API Method Name [getRelatedCategories] productId["
				+ productRepoItem.getRepositoryId() + "]");
		
		List<CategoryVO> relatedCategories = new ArrayList<>();
		
		@SuppressWarnings("unchecked")
		final Set<RepositoryItem> parentCategories = (Set<RepositoryItem>) productRepoItem
				.getPropertyValue(BBBCatalogConstants.PARENT_CATEGORIES_PROPERTY_NAME);
		if( parentCategories!=null && !parentCategories.isEmpty()){
			relatedCategories = getActiveParentCategoriesForSite(parentCategories, siteId);
		}
		this.logDebug("Catalog API Method Name [getRelatedCategories] productId["
				+ productRepoItem.getRepositoryId() + "] Exit");
				
		return relatedCategories;
	}
	
	
	/**
	 * Gets the active parent category ids for site.
	 *
	 * @param parentCategories the parent categories
	 * @param siteId the site id
	 * @return the active parent category ids for site
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public List<CategoryVO> getActiveParentCategoriesForSite (final Set<RepositoryItem> parentCategories, final String siteId) throws BBBSystemException, BBBBusinessException {
		
		this.logDebug("Catalog API Method Name [getActiveParentCategoryIdsForSite] parentCategories["
				+ parentCategories.toString() + "]");
		final List<CategoryVO> activeParentCats = new ArrayList<>();
		Integer maxActiveCats;
		final List<String> maxRelatedCat = getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.RELATED_CATEGORIES_COUNT);
		if(maxRelatedCat!=null && !StringUtils.isEmpty(maxRelatedCat.get(0)) && BBBUtility.isNumericOnly(maxRelatedCat.get(0)) ){
				maxActiveCats = Integer.valueOf(maxRelatedCat.get(0));
			
       // if (parentCategories != null) {
        	this.logTrace("Parent categories are not null for ::" + parentCategories);
        	int count = 0;
        	Set<String> uniqueCats = new HashSet<>();
        	for (final RepositoryItem catRepo : parentCategories) {
        		if (count == maxActiveCats){
        			break;
        		}
        		final CategoryVO categoryVO = new CategoryVO();
                @SuppressWarnings ("unchecked")
                final Set<String> catSiteId = (Set<String>) catRepo.getPropertyValue(BBBCoreConstants.SITE_IDS);
                this.logTrace("sites applicable for potential parent with id::" + catRepo.getRepositoryId() + " are "
                                + catSiteId + " and the current site id is " + siteId);
                if (catSiteId.contains(siteId) && isCategoryActive(catRepo)) {
                	String categoryId = catRepo.getRepositoryId();
                	String displayName = (String) catRepo.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME);
                	String seoURL = getCategorySeoLinkGenerator().formatUrl(categoryId, displayName);
                	if(!uniqueCats.contains(displayName)) {
                		uniqueCats.add(displayName);
                		categoryVO.setCategoryId(categoryId);
                		categoryVO.setCategoryName(displayName);
                		categoryVO.setSeoURL(seoURL);
                		activeParentCats.add(categoryVO);
                		count++;
                	}
                }
        	}
        	this.logTrace("Active Parent Categories are: " + activeParentCats);
        }
		//}
		this.logDebug("Catalog API Method Name [getActiveParentCategoryIdsForSite] parentCategories["
				+ parentCategories.toString() + "] Exit");
       return activeParentCats;
	}

	
	/**
	 * This method will return a RegionVO object with Region and Store
	 * information, when provided with the zip code.
	 *
	 * @param zipCode the zip code
	 * @return the region data from zip
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	public RegionVO getRegionDataFromZip(String zipCode) throws BBBBusinessException, BBBSystemException {
		
		return getShippingRepositoryTools().getRegionDataFromZip(zipCode);

		//TODO - use this code post-Iliad-Live getShippingRepositoryTools().getRegionDataFromZip(zipCode);


	}

	/**
	 * This method will return RegionVO, with zip codes populated, when provided
	 * with a regionId.
	 *
	 * @param regionId the region id
	 * @return the zip codes from region
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public RegionVO getZipCodesFromRegion(String regionId) throws BBBSystemException, BBBBusinessException {
		
		return getShippingRepositoryTools().getZipCodesFromRegion(regionId);


	}

	/**
	 * This method will return a RegionVO object with StoreIds populated, when
	 * provided with a regionId.
	 *
	 * @param regionId the region id
	 * @return the store ids from region
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	public RegionVO getStoreIdsFromRegion(String regionId) throws BBBBusinessException, BBBSystemException {
		
		return getShippingRepositoryTools().getStoreIdsFromRegion(regionId);

	}
	 
	/**
	 * This method will return a specific regions all values, when provided with
	 * a regionId.
	 *
	 * @param regionId the region id
	 * @return the all region details
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	public RegionVO getAllRegionDetails(String regionId) throws BBBBusinessException, BBBSystemException {
		
		return getShippingRepositoryTools().getAllRegionDetails(regionId);

	}

	
			
		/**
		 * New method to update dynamic price string for sku.
		 *
		 * @param skuDetailVO the sku detail vo
		 * @param skuDynamicPriceVo the sku dynamic price vo
		 * @param country the country
		 * @param siteId the site id
		 */
		private void updateDynamicPriceFlags(String skuId, BBBDynamicPriceSkuVO skuDynamicPriceVo, String country, String siteId) {
		
				logDebug("Entering updatePriceStringSKU() method for sku id: "+skuId +"to populate price label code and in cart flag");
				logDebug("updatePriceStringSKU() method for country: "+country +"siteId :"+siteId);
		
					String pricingLabelCode = null;
					boolean inCartFlag = false;
								 
					if (siteId.equals(BBBCoreConstants.SITE_BAB_CA)
							|| siteId.equals(BBBCoreConstants.SITEBAB_CA_TBS)) {
						if(skuDynamicPriceVo
								.getCaPricingLabelCode()!=null){
						pricingLabelCode = skuDynamicPriceVo.getCaPricingLabelCode();
						}
						inCartFlag=skuDynamicPriceVo.isCaIncartFlag();
					} else if(country.equals(BBBInternationalShippingConstants.COUNTRY_MEXICO)){
						if(skuDynamicPriceVo.getMxPricingLabelCode()!=null){
							pricingLabelCode=skuDynamicPriceVo.getMxPricingLabelCode();
							}
							inCartFlag=skuDynamicPriceVo.isMxIncartFlag();
					}
					else if (siteId.equals(BBBCoreConstants.SITE_BBB)||siteId.equals(BBBCoreConstants.SITEBAB_BABY_TBS)) {
						if(skuDynamicPriceVo.getBabyPricingLabelCode()!=null){
							pricingLabelCode = skuDynamicPriceVo.getBabyPricingLabelCode();
							}
							inCartFlag=skuDynamicPriceVo.isBabyIncartFlag();
					}else {
						if(skuDynamicPriceVo.getBbbPricingLabelCode()!=null){
							pricingLabelCode = skuDynamicPriceVo.getBbbPricingLabelCode();
							}
							inCartFlag=skuDynamicPriceVo.isBbbIncartFlag();
					}

					skuDynamicPriceVo.setPricingLabelCode(pricingLabelCode);
					skuDynamicPriceVo.setInCartFlag(inCartFlag);
					skuDynamicPriceVo.setDynamicPriceSKU(true);
					logDebug("Pricing Label Code(SKU): "+pricingLabelCode + "In Cart Flag : "+inCartFlag);
					logDebug("Exitting updatePriceStringSKU() method");
			

		}

		/**
		 * New method to update dynamic price string for product.
		 *
		 * @param productVO the product vo
		 * @param productdynamicVo the productdynamic vo
		 * @param country the country
		 * @param siteId the site id
		 * @throws BBBSystemException the BBB system exception
		 */
		public void updatePriceStringProd(String producId, BBBDynamicPriceVO productdynamicVo, String country,String siteId) throws BBBSystemException {
			
			logDebug("Entering updatePriceStringProd() method for product id:"+ producId +"to populate dynamic price and in cart pricing changes");
			logDebug("Entering updatePriceStringProd() method : country:"+ country +"siteId : "+ siteId);
			String listPriceString= null;
			String salePriceString= null;
			String pricingLabelCode= null;
			boolean inCartFlag = false;
			if (siteId.equals(BBBCoreConstants.SITE_BAB_CA)
					|| siteId.equals(BBBCoreConstants.SITEBAB_CA_TBS)) {
				if(productdynamicVo.getCaListPriceString()!=null){
					listPriceString = productdynamicVo.getCaListPriceString();
					}
					if(productdynamicVo.getCaSalePriceString()!=null){
					salePriceString = productdynamicVo.getCaSalePriceString();
					}
					if(productdynamicVo.getCaPricingLabelCode()!=null){
					pricingLabelCode = productdynamicVo.getCaPricingLabelCode();
					}				
					inCartFlag=(boolean) productdynamicVo.isCaIncartFlag();			
			
			} else if (country
					.equals(BBBInternationalShippingConstants.COUNTRY_MEXICO)) {
				if(productdynamicVo.getMxListPriceString()!=null){
					listPriceString = productdynamicVo.getMxListPriceString();
					}
					if(productdynamicVo.getMxSalePriceString()!=null){
					salePriceString = productdynamicVo.getMxSalePriceString();
					}
					if(productdynamicVo.getMxPricingLabelCode()!=null){
					pricingLabelCode = productdynamicVo.getMxPricingLabelCode();
					}
					inCartFlag=productdynamicVo.isMxIncartFlag();

			}else if (siteId.equals(BBBCoreConstants.SITE_BBB)||siteId.equals(BBBCoreConstants.SITEBAB_BABY_TBS)) {
				if(productdynamicVo.getBabyListPriceString()!=null){
					listPriceString = productdynamicVo.getBabyListPriceString();
					}
					if(productdynamicVo.getBabySalePriceString()!=null){
					salePriceString = productdynamicVo.getBabySalePriceString();
					}
					if(productdynamicVo.getBabyPricingLabelCode()!=null){
					pricingLabelCode = productdynamicVo.getBabyPricingLabelCode();
					}
					inCartFlag=productdynamicVo.isBabyIncartFlag();

			}  else {
				if(productdynamicVo.getBbbListPriceString()!=null){
					listPriceString = productdynamicVo.getBbbListPriceString();
					}
					if(productdynamicVo.getBbbSalePriceString()!=null){
					salePriceString = productdynamicVo.getBbbSalePriceString();
					}
					if(productdynamicVo.getBbbPricingLabelCode()!=null){
					pricingLabelCode = productdynamicVo.getBbbPricingLabelCode();
					}
					inCartFlag=productdynamicVo.isBbbIncartFlag();

			}
			productdynamicVo.setPriceRangeDescription(updatePennyPriceDescription(listPriceString));
			productdynamicVo.setSalePriceRangeDescription(updatePennyPriceDescription(salePriceString));
			productdynamicVo.setPriceLabelCode(pricingLabelCode);
			productdynamicVo.setInCartFlag(inCartFlag);
			productdynamicVo.setDynamicPricingProduct(true);
			
		    logDebug("List Price String: "+listPriceString+ "Sale Price String: "+salePriceString+ "Pricing Label Code: "+pricingLabelCode + "In Cart Flag: "+inCartFlag );
		    logDebug("Exitting updatePriceStringProd() method");
		}
		
		
		/**
		 * method to update dynamic price string for product.
		 *
		 * @param productVO the product vo
		 * @param productdynamicVo the productdynamic vo
		 * @param country the country
		 * @param siteId the site id
		 * @throws BBBSystemException the BBB system exception
		 */
		public void updatePriceStringProd(ProductVO productVO,BBBDynamicPriceVO productdynamicVo, String country,String siteId) throws BBBSystemException {
			
			logDebug("Entering updatePriceStringProd() method for product id:"+ productVO.getProductId() +"to populate dynamic price and in cart pricing changes");
			logDebug("Entering updatePriceStringProd() method : country:"+ country +"siteId : "+ siteId);
			String listPriceString= null;
			String salePriceString= null;
			String pricingLabelCode= null;
			boolean inCartFlag = false;
			if (siteId.equals(BBBCoreConstants.SITE_BAB_CA)
					|| siteId.equals(BBBCoreConstants.SITEBAB_CA_TBS)) {
				if(productdynamicVo.getCaListPriceString()!=null){
					listPriceString = productdynamicVo.getCaListPriceString();
					}
					if(productdynamicVo.getCaSalePriceString()!=null){
					salePriceString = productdynamicVo.getCaSalePriceString();
					}
					if(productdynamicVo.getCaPricingLabelCode()!=null){
					pricingLabelCode = productdynamicVo.getCaPricingLabelCode();
					}				
					inCartFlag=(boolean) productdynamicVo.isCaIncartFlag();			
			
			} else if (country
					.equals(BBBInternationalShippingConstants.COUNTRY_MEXICO)) {
				if(productdynamicVo.getMxListPriceString()!=null){
					listPriceString = productdynamicVo.getMxListPriceString();
					}
					if(productdynamicVo.getMxSalePriceString()!=null){
					salePriceString = productdynamicVo.getMxSalePriceString();
					}
					if(productdynamicVo.getMxPricingLabelCode()!=null){
					pricingLabelCode = productdynamicVo.getMxPricingLabelCode();
					}
					inCartFlag=productdynamicVo.isMxIncartFlag();

			}else if (siteId.equals(BBBCoreConstants.SITE_BBB)||siteId.equals(BBBCoreConstants.SITEBAB_BABY_TBS)) {
				if(productdynamicVo.getBabyListPriceString()!=null){
					listPriceString = productdynamicVo.getBabyListPriceString();
					}
					if(productdynamicVo.getBabySalePriceString()!=null){
					salePriceString = productdynamicVo.getBabySalePriceString();
					}
					if(productdynamicVo.getBabyPricingLabelCode()!=null){
					pricingLabelCode = productdynamicVo.getBabyPricingLabelCode();
					}
					inCartFlag=productdynamicVo.isBabyIncartFlag();

			}  else {
				if(productdynamicVo.getBbbListPriceString()!=null){
					listPriceString = productdynamicVo.getBbbListPriceString();
					}
					if(productdynamicVo.getBbbSalePriceString()!=null){
					salePriceString = productdynamicVo.getBbbSalePriceString();
					}
					if(productdynamicVo.getBbbPricingLabelCode()!=null){
					pricingLabelCode = productdynamicVo.getBbbPricingLabelCode();
					}
					inCartFlag=productdynamicVo.isBbbIncartFlag();

			}
			productVO.setPriceRangeDescription(listPriceString);
			productVO.setSalePriceRangeDescription(salePriceString);
			productVO.setPriceLabelCode(pricingLabelCode);
			productVO.setInCartFlag(inCartFlag);
			productVO.setDynamicPricingProduct(true);
			
		    logDebug("List Price String: "+listPriceString+ "Sale Price String: "+salePriceString+ "Pricing Label Code: "+pricingLabelCode + "In Cart Flag: "+inCartFlag );
		    logDebug("Exitting updatePriceStringProd() method");
		}
		
		/**
		 * returns dynamic price product item.
		 *
		 * @param productId the product id
		 * @return product item, if is dynamic price product
		 */
	public RepositoryItem getDynamicPriceProductItem(String productId) {
		
		return getPriceListRepositoryTools().getDynamicPriceProductItem(productId);

	}
		
		
	/**
	 * New method to fetch dynamic price description for product.
	 * @param productId
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public BBBDynamicPriceVO getDynamicProdPriceDescription(final String productId) {
		
	 boolean dynamicPricingEnabled=false;
	 BBBDynamicPriceVO productPriceVo = null;
	 String country = BBBInternationalShippingConstants.DEFAULT_COUNTRY;
	 final String siteId = getCurrentSiteId();
	 
	 try{
	 	List<String> dynamicPricingEnabledList=getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, 
			 BBBCoreConstants.ENABLE_INCART_AND_DYNAMIC_PRICING_KEY);
		if(!BBBUtility.isListEmpty(dynamicPricingEnabledList)){
			dynamicPricingEnabled = Boolean.parseBoolean(dynamicPricingEnabledList.get(0));
		}
		
		if(dynamicPricingEnabled) {	
			// Dynamic Price and In cart pricing changes start here . 
			// ProductVO will be populated for price Label code, Price Strings and in cart flag
			
			if (returnCountryFromSession() != null) {
				country = returnCountryFromSession();
			}
		
			if(null != getProductCacheContainer()){
				logDebug("Fetching dynamic product data from cache in updatePriceDescription(): " + productId);
				productPriceVo = (BBBDynamicPriceVO) getProductCacheContainer().get("product_" + productId);
			 	logDebug("Fetched dynamic product data updatePriceDescription() as  " + productPriceVo);
			} 
			
			if( productPriceVo ==null && enableRepoCallforDynPrice()){
				
				logDebug("Fetching dynamic product data from DB in updatePriceDescription() " );
				RepositoryItem dynamicProdItem = getDynamicPriceProductItem(productId);	
				if(null != dynamicProdItem){
				
					logDebug("Dynamic product data fetched from DB in updatePriceDescription() " );
					
					productPriceVo = new BBBDynamicPriceVO();
					productPriceVo.setBabyIncartFlag((boolean)dynamicProdItem.getPropertyValue(BBBCatalogConstants.BABY_IN_CART_FLAG));
					productPriceVo.setBabyListPriceString((String)dynamicProdItem.getPropertyValue(BBBCatalogConstants.BABY_LIST_PRICE_STRING));
					productPriceVo.setBabySalePriceString((String)dynamicProdItem.getPropertyValue(BBBCatalogConstants.BABY_SALE_PRICE_STRING));
					productPriceVo.setBabyPricingLabelCode((String)dynamicProdItem.getPropertyValue(BBBCatalogConstants.BABY_PRICING_LABEL_CODE));
					productPriceVo.setBbbIncartFlag((boolean)dynamicProdItem.getPropertyValue(BBBCatalogConstants.BBB_IN_CART_FLAG));
					productPriceVo.setBbbListPriceString((String)dynamicProdItem.getPropertyValue(BBBCatalogConstants.BBB_LIST_PRICE_STRING));
					productPriceVo.setBbbPricingLabelCode((String)dynamicProdItem.getPropertyValue(BBBCatalogConstants.BBB_PRICING_LABEL_CODE));
					productPriceVo.setBbbSalePriceString((String)dynamicProdItem.getPropertyValue(BBBCatalogConstants.BBB_SALE_PRICE_STRING));
					productPriceVo.setCaIncartFlag((boolean)dynamicProdItem.getPropertyValue(BBBCatalogConstants.CA_IN_CART_FLAG));
					productPriceVo.setCaListPriceString((String)dynamicProdItem.getPropertyValue(BBBCatalogConstants.CA_LIST_PRICE_STRING));
					productPriceVo.setCaPricingLabelCode((String)dynamicProdItem.getPropertyValue(BBBCatalogConstants.CA_PRICING_LABEL_CODE));
					productPriceVo.setCaSalePriceString((String)dynamicProdItem.getPropertyValue(BBBCatalogConstants.CA_SALE_PRICE_STRING));
					productPriceVo.setMxIncartFlag((boolean)dynamicProdItem.getPropertyValue(BBBCatalogConstants.MX_IN_CART_FLAG));
					productPriceVo.setMxListPriceString((String)dynamicProdItem.getPropertyValue(BBBCatalogConstants.MX_LIST_PRICE_STRING));
					productPriceVo.setMxPricingLabelCode((String)dynamicProdItem.getPropertyValue(BBBCatalogConstants.MX_PRICING_LABEL_CODE));
					productPriceVo.setMxSalePriceString((String)dynamicProdItem.getPropertyValue(BBBCatalogConstants.MX_SALE_PRICE_STRING));
				}
			}
		
			// If condition for international user in cart flag for Baby and US site
				if (productPriceVo!=null){
					
					productPriceVo.setCountry(country);
					if(siteId.equals(BBBCoreConstants.SITE_BBB)) {
						productPriceVo.setInCartFlag(productPriceVo.isBabyIncartFlag());
					} else if (siteId.equals(BBBCoreConstants.SITE_BAB_US)) {
						productPriceVo.setInCartFlag(productPriceVo.isBbbIncartFlag());
					}
					logDebug("In Cart Flag For product : " + productPriceVo.isInCartFlag()
					+ " and site Id :" + siteId);
				}
				
				// Dynamic Price and In cart pricing changes end here. 
		}	
	
		//salePriceRangeDescription and priceRangeDescription are already set
		
		if (dynamicPricingEnabled && productPriceVo != null
					&& (country.equals(BBBInternationalShippingConstants.COUNTRY_MEXICO) || 
							country.equals(BBBInternationalShippingConstants.DEFAULT_COUNTRY))) {
				updatePriceStringProd(productId, productPriceVo, country, siteId);
		}
	 } catch (BBBSystemException | BBBBusinessException ex ) {
			logError("Error while fetching Product item from Dynamic Repository for product id: "
							+  productId, ex);
    }

	return productPriceVo;
	
  }
	
	
	/**
	 * returns dynamic price details for BBBProductVO.
	 *
	 * @param dynamicPriceVO the dynamic price vo
	 * @param productId the product id
	 * @return the dynamic price details
	 */
	@Override
	public void getDynamicPriceDetails(BBBDynamicPriceVO dynamicPriceVO,
			String productId) {
		logDebug("getting dynamic price details for PLP and search for product id: "
				+ productId);
		String country = BBBInternationalShippingConstants.DEFAULT_COUNTRY;
		String siteId = SiteContextManager.getCurrentSiteId();
		if (returnCountryFromSession() != null) {
			country = returnCountryFromSession();
		}
		
		BBBDynamicPriceVO productPriceVo = null;
		if(null != getProductCacheContainer()){
			 logDebug("Fetching dynamic product data from cache in getDynamicPriceDetails(): " + productId);
			 productPriceVo = (BBBDynamicPriceVO) getProductCacheContainer().get("product_" + productId);
			 logDebug("Fetched dynamic product data from cache in getDynamicPriceDetails() as " + productPriceVo);
		}
		
		if(productPriceVo ==null &&  isEnableDynRepoCallPLP()){

			logDebug("Fetching dynamic product data from DB in getDynamicPriceDetails(): " + productId);
				RepositoryItem dynamicProdItem = getDynamicPriceProductItem(productId);		
				if(null != dynamicProdItem){
				logDebug("Item found for dynamic product data from DB in getDynamicPriceDetails() for PLP " + productId);
				productPriceVo = new BBBDynamicPriceVO();
				productPriceVo.setBabyIncartFlag((boolean)dynamicProdItem.getPropertyValue(BBBCatalogConstants.BABY_IN_CART_FLAG));
				productPriceVo.setBabyListPriceString((String)dynamicProdItem.getPropertyValue(BBBCatalogConstants.BABY_LIST_PRICE_STRING));
				productPriceVo.setBabySalePriceString((String)dynamicProdItem.getPropertyValue(BBBCatalogConstants.BABY_SALE_PRICE_STRING));
				productPriceVo.setBabyPricingLabelCode((String)dynamicProdItem.getPropertyValue(BBBCatalogConstants.BABY_PRICING_LABEL_CODE));
				productPriceVo.setBbbIncartFlag((boolean)dynamicProdItem.getPropertyValue(BBBCatalogConstants.BBB_IN_CART_FLAG));
				productPriceVo.setBbbListPriceString((String)dynamicProdItem.getPropertyValue(BBBCatalogConstants.BBB_LIST_PRICE_STRING));
				productPriceVo.setBbbPricingLabelCode((String)dynamicProdItem.getPropertyValue(BBBCatalogConstants.BBB_PRICING_LABEL_CODE));
				productPriceVo.setBbbSalePriceString((String)dynamicProdItem.getPropertyValue(BBBCatalogConstants.BBB_SALE_PRICE_STRING));
				productPriceVo.setCaIncartFlag((boolean)dynamicProdItem.getPropertyValue(BBBCatalogConstants.CA_IN_CART_FLAG));
				productPriceVo.setCaListPriceString((String)dynamicProdItem.getPropertyValue(BBBCatalogConstants.CA_LIST_PRICE_STRING));
				productPriceVo.setCaPricingLabelCode((String)dynamicProdItem.getPropertyValue(BBBCatalogConstants.CA_PRICING_LABEL_CODE));
				productPriceVo.setCaSalePriceString((String)dynamicProdItem.getPropertyValue(BBBCatalogConstants.CA_SALE_PRICE_STRING));
				productPriceVo.setMxIncartFlag((boolean)dynamicProdItem.getPropertyValue(BBBCatalogConstants.MX_IN_CART_FLAG));
				productPriceVo.setMxListPriceString((String)dynamicProdItem.getPropertyValue(BBBCatalogConstants.MX_LIST_PRICE_STRING));
				productPriceVo.setMxPricingLabelCode((String)dynamicProdItem.getPropertyValue(BBBCatalogConstants.MX_PRICING_LABEL_CODE));
				productPriceVo.setMxSalePriceString((String)dynamicProdItem.getPropertyValue(BBBCatalogConstants.MX_SALE_PRICE_STRING));
				}

			}
		
		//always populate price string it is found in cache
		if (productPriceVo != null) {
			if (!dynamicPriceVO.isAlreadyPopulated()) {
				populatePriceStringParameters(dynamicPriceVO, country,
						productId,productPriceVo);
			}
			dynamicPriceVO.setSiteId(siteId);
			dynamicPriceVO.setSetFromCache(true);
		} 
		
		//set country for future use to determine for which country
		dynamicPriceVO.setCountry(country);
		logDebug("dynamic price details for PLP and search retrieved: "
				+ dynamicPriceVO);
	}
	
	
	/**
	 * Evaulate dynamic price eligiblity based on country and price info from cache
	 */
	public void evaluateDynamicPriceEligiblity(BBBDynamicPriceVO productPriceVo){
		String country = BBBInternationalShippingConstants.DEFAULT_COUNTRY;
		String siteId = SiteContextManager.getCurrentSiteId();
		if (returnCountryFromSession() != null) {
			country = returnCountryFromSession();
		}

		if (country.equals(BBBInternationalShippingConstants.COUNTRY_MEXICO)
				|| country
						.equals(BBBInternationalShippingConstants.DEFAULT_COUNTRY)) {
			if (productPriceVo != null && productPriceVo.isSetFromCache()) {
				
				productPriceVo.setSiteId(siteId);
				productPriceVo.setDynamicProdEligible(true);
			} else if(productPriceVo != null){
				productPriceVo.setDynamicProdEligible(false);
			}
		} else {
			productPriceVo.setDynamicProdEligible(false);
		}
	}
		
		/**
		 * populate PriceString Parameters for DynamicPriceVO.
		 *
	 * @param dynamicPriceVO
	 *            the dynamic price vo
	 * @param country
	 *            the country
	 * @param productId
	 *            the product id
	 * @param productPriceVo
	 *            the product price vo
		 */
	private void populatePriceStringParameters(BBBDynamicPriceVO dynamicPriceVO, String country, String productId,
			BBBDynamicPriceVO productPriceVo) {

		logDebug("updating dynamicPriceVO for product id:" + productId+ "country id: "+country);
		String tbdString = getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.PRICE_IS_TBD, null, null,
				SiteContextManager.getCurrentSiteId());
		if (null != tbdString && null != productPriceVo && tbdString.equals(updatePennyPriceDescription(productPriceVo.getBbbListPriceString()))) {
			dynamicPriceVO.setBbbListPriceString(tbdString);
			dynamicPriceVO.setCaListPriceString(tbdString);
			dynamicPriceVO.setBabyListPriceString(tbdString);
			dynamicPriceVO.setMxListPriceString(tbdString);
		} else {
			if (productPriceVo.getCaListPriceString() != null) {
				dynamicPriceVO.setCaListPriceString(productPriceVo.getCaListPriceString());
			}
			if (productPriceVo.getCaSalePriceString() != null) {
				dynamicPriceVO.setCaSalePriceString(productPriceVo.getCaSalePriceString());
			}
			if (productPriceVo.getBabyListPriceString() != null) {
				dynamicPriceVO.setBabyListPriceString(productPriceVo.getBabyListPriceString());
			}
			if (productPriceVo.getBabySalePriceString() != null) {
				dynamicPriceVO.setBabySalePriceString(productPriceVo.getBabySalePriceString());
			}
			if (productPriceVo.getMxListPriceString() != null) {
				dynamicPriceVO.setMxListPriceString(productPriceVo.getMxListPriceString());
			}
			if (productPriceVo.getMxSalePriceString() != null) {
				dynamicPriceVO.setMxSalePriceString(productPriceVo.getMxSalePriceString());
			}
			if (productPriceVo.getBbbListPriceString() != null) {
				dynamicPriceVO.setBbbListPriceString(productPriceVo.getBbbListPriceString());
			}
			if (productPriceVo.getBbbSalePriceString() != null) {
				dynamicPriceVO.setBbbSalePriceString(productPriceVo.getBbbSalePriceString());
			}
			if (productPriceVo.getBbbPricingLabelCode() != null) {
				dynamicPriceVO.setBbbPricingLabelCode(productPriceVo.getBbbPricingLabelCode());
			}
			if (productPriceVo.getCaPricingLabelCode() != null) {
				dynamicPriceVO.setCaPricingLabelCode(productPriceVo.getCaPricingLabelCode());
			}
			if (productPriceVo.getBabyPricingLabelCode() != null) {
				dynamicPriceVO.setBabyPricingLabelCode(productPriceVo.getBabyPricingLabelCode());
			}
			if (productPriceVo.getMxPricingLabelCode() != null) {
				dynamicPriceVO.setMxPricingLabelCode(productPriceVo.getMxPricingLabelCode());
			}
		}
		dynamicPriceVO.setBbbIncartFlag(productPriceVo.isBbbIncartFlag());
		dynamicPriceVO.setCaIncartFlag(productPriceVo.isCaIncartFlag());
		dynamicPriceVO.setMxIncartFlag(productPriceVo.isMxIncartFlag());
		dynamicPriceVO.setBabyIncartFlag(productPriceVo.isBabyIncartFlag());
			dynamicPriceVO.setAlreadyPopulated(true);
		logDebug("dynamic Price vo updated for product:" + productId);
	}
		
	/**
	 * This method checks for price, if price contains penny ammount then it
	 * returns TBD label else return original price.
	 * 
	 * @param price
	 * @return
	 */
	private String updatePennyPriceDescription(String price) {
		if (null != price && null != getTbdPriceString() && getTbdPriceString().contains(price)) {
			return getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.PRICE_IS_TBD, null, null,
					SiteContextManager.getCurrentSiteId());
		}
		return price;
	}
		
	/* (non-Javadoc)
	 * @see com.bbb.commerce.catalog.BBBCatalogTools#returnCountryFromSession()
	 */
	@Override
	public String returnCountryFromSession() {
		
		return getGlobalRepositoryTools().returnCountryFromSession();

	}

	/**
	 * Gets the regions rql query.
	 *
	 * @return the regions rql query
	 */
	public String getRegionsRqlQuery() {
		return regionsRqlQuery;
	}

	/**
	 * Sets the regions rql query.
	 *
	 * @param regionsRqlQuery the new regions rql query
	 */
	public void setRegionsRqlQuery(String regionsRqlQuery) {
		this.regionsRqlQuery = regionsRqlQuery;
	}
	
	/**
	 * Gets the dynamic price repository.
	 *
	 * @return the dynamic price repository
	 */
	public Repository getDynamicPriceRepository() {
		return dynamicPriceRepository;
	}
	
	/**
	 * Sets the dynamic price repository.
	 *
	 * @param dynamicPriceRepository the new dynamic price repository
	 */
	public void setDynamicPriceRepository(Repository dynamicPriceRepository) {
		this.dynamicPriceRepository = dynamicPriceRepository;
	}
	
	/**
	 * This method send message to TIBCO to populate EDW Repository.
	 *
	 * @param edwData the edw data
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	public void submitEDWProfileDataMesssage(ProfileEDWInfoVO edwData) throws BBBBusinessException, BBBSystemException {
		logDebug("START: Submitting EDW Data Message");	
		EDWProfileDataVO edwDataRequest = new EDWProfileDataVO();
		edwData.setDataCentre(this.getDataCenterMap().get(this.getDcPrefix()));
		edwData.setUserToken(getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
		edwDataRequest.setProfileEDWData(edwData);
		
		edwDataRequest.setServiceName(BBBCoreConstants.EDW_DATA_SERVICE);
		sendTextMessage(edwDataRequest);
		
		logDebug("END: Submitting EDW Data Message");
	}

	/**
	 * @param edwDataRequest
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	protected void sendTextMessage(EDWProfileDataVO edwDataRequest) throws BBBBusinessException, BBBSystemException {
		ServiceHandlerUtil.sendTextMessage(edwDataRequest);
	}

	@Override
	public String getParentProductId(String pProductId, String siteId) {
		boolean disableCollectionParentCache = Boolean.parseBoolean(BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY, DISABLE_COLLECTION_PARENT_CACHE));
		logDebug("CollectionParentDroplet.service useCollectionParentCache" + disableCollectionParentCache);
	    if (!disableCollectionParentCache && null != pProductId) {
	    	//Product id from the JSP page.
	    		String collectionChildRelnCacheName = BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY, COLLECTION_CHILD_RELN_CACHE_NAME);
	    		String cacheKey = new StringBuilder(pProductId).append(BBBCoreConstants.UNDERSCORE).append(siteId).toString();
	    		String collectionParentProductId = (String) getObjectCache().get(cacheKey, collectionChildRelnCacheName);
	    		logDebug("Child Product Id (cacheKey) : " + cacheKey + " , Parent product Id from cache : " + collectionParentProductId);
	    		return collectionParentProductId;
	    }
		return null;
	}
	public void removePhantomCategory(List<FacetRefinementVO> facetRefinementVO,String pSiteId)
	{
		logDebug("Entering BBBCatalogTools.removePhantomCategory method[START]");
		long startTime = System.currentTimeMillis();
		ListIterator<FacetRefinementVO> facetIterator = facetRefinementVO.listIterator();
		List<FacetRefinementVO> removeCategory = new ArrayList<FacetRefinementVO>();
		try {
			BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " removePhantomCategory");
			
		while(facetIterator.hasNext())
		{
			FacetRefinementVO refinementVO = facetIterator.next();
		if(null != refinementVO){
				
			if(!BBBUtility.isListEmpty(refinementVO.getFacetsRefinementVOs()))
			{
				logDebug("Getting CategoryVO for categoryID: ["+refinementVO.getCatalogId()+"]");
				CategoryVO category = getCategoryDetail(pSiteId, refinementVO.getCatalogId(), false);
					
				//In case L1 category is Phantom category, remove whole node from refinement VO
					//BBBSL-9367 | Gifts and Personalized Gifts L1 missing from left nav
					if(null != category && null!=category.getPhantomCategory() && category.getPhantomCategory() && !isFirstLevelCategory(category.getCategoryId(), pSiteId))
				{
					logDebug("Removing categoryID: ["+refinementVO.getCatalogId()+"] as PhantomCategory is ["+category.getPhantomCategory()+"]");
					removeCategory.add(refinementVO);
				}
				else
				{
					//In case L1 category is not Phantom category, go to L2 or L3 categories iteratively
					removePhantomCategory(refinementVO.getFacetsRefinementVOs(),pSiteId);
				}
			}
			//In case L1 category does not have L2 or L3 categories matching refinementVO
			else
			{
				logDebug("Getting CategoryVO for categoryID: ["+refinementVO.getCatalogId()+"]");
				CategoryVO category = getCategoryDetail(pSiteId, refinementVO.getCatalogId(), false);
					//BBBSL-9367 | Gifts and Personalized Gifts L1 missing from left nav
					if(null != category && null!=category.getPhantomCategory() && category.getPhantomCategory() && !isFirstLevelCategory(category.getCategoryId(), pSiteId))
				{
					logDebug("Removing categoryID: ["+refinementVO.getCatalogId()+"] as PhantomCategory is ["+category.getPhantomCategory()+"]");
					removeCategory.add(refinementVO);
				}
			  }
			}
		  }
		} catch (BBBSystemException | BBBBusinessException e) {
			logError("Error thrown from BBBCatalogTools.removePhantomCategory ",e);
		} finally{
			long endTime = System.currentTimeMillis();
			logDebug("Time taken by removePhantomCategory= "+ (endTime-startTime));
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " removePhantomCategory");
		}
		//Remove categories from facetRefinementVO
		facetRefinementVO.removeAll(removeCategory);
	}
	
	//PS-61408
	/**This method iterates over categoryRefinementVO to get the list of L3 categories
	 * and if found phantom, it removes it from the corresponding L2 and is hidden from 
	 * Catgeory PLP
	 * @param categoryRefinementVO
	 * @param pSiteId
	 */
	
	public void removePhantomCategoryCLP(List<CategoryRefinementVO> categoryRefinementVO,String pSiteId)
	{
		logDebug("Entering BBBCatalogTools.removePhantomCategoryCLP method[START]");
		long startTime = System.currentTimeMillis();
		ListIterator<CategoryRefinementVO> categoryIterator = categoryRefinementVO.listIterator();
		List<CategoryRefinementVO> removeCategory = new ArrayList<CategoryRefinementVO>();
		try {
			BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " removePhantomCategoryClp");
			while(categoryIterator.hasNext())			{
				CategoryRefinementVO refinementVO = categoryIterator.next();
				if(null != refinementVO)
				{
					logDebug("Getting CategoryVO for categoryID: ["+refinementVO.getQuery()+"]");
					CategoryVO category = getCategoryDetail(pSiteId, refinementVO.getQuery(), false);
					if(null != category && null!=category.getPhantomCategory() && category.getPhantomCategory() && !isFirstLevelCategory(category.getCategoryId(), pSiteId))
					{
						logDebug("Removing categoryID: ["+refinementVO.getQuery()+"] as PhantomCategory is ["+category.getPhantomCategory()+"]");
						removeCategory.add(refinementVO);
					}
				}
			} 
		}catch (BBBSystemException | BBBBusinessException e) {
			logError("Error thrown from BBBCatalogTools.removePhantomCategoryCLP ",e);
		} finally{
			long endTime = System.currentTimeMillis();
			logDebug("Time taken by removePhantomCategoryCLP= "+ (endTime-startTime));
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " removePhantomCategoryClp");
		}
		//Remove categories from categoryRefinementVO
		categoryRefinementVO.removeAll(removeCategory);
	}
	
	
	/* (non-Javadoc)
	 * @see com.bbb.commerce.catalog.BBBCatalogTools#isTBSProductActiveMIESearch(atg.repository.RepositoryItem)
	 */
	@Override
	public boolean isTBSProductActiveMIESearch(
			RepositoryItem productRepositoryItem) {

		return false;
	}
		
	
	
	/**
	 * This method gets Incart flag for the sku 
	 * Story - BBBH-2890.
	 *
	 * @param skuId the sku id
	 * @return the sku incart flag
	 */
	
	@Override
	public boolean getSkuIncartFlag(String skuId) {

		return getPriceListRepositoryTools().getSkuIncartFlag(skuId,false);

		//TODO - use this code post-Iliad-Live getPriceListRepositoryTools().getSkuIncartFlag(skuId);
		
			}
	
	/**
	 * This method gets Incart flag for the sku 
	 * Story - BBBH-2890.
	 *
	 * @param skuId the sku id
	 * @return the sku incart flag
	 */
	
	@Override
	public boolean getSkuIncartFlag(String skuId, boolean fetchFromDB) {
		logDebug("BBBCatalogToolsImpl.getSkuIncartFlag - sku Id: " + skuId + " fetchFromDB param="+fetchFromDB);
		return getPriceListRepositoryTools().getSkuIncartFlag(skuId,fetchFromDB);
		
	}
	/**
	 * Populate dynamic sku deatil in vo.
	 *
	 * @param sKUDetailVO the s ku detail vo
	 * @param country the country
	 * @param siteId the site id
	 */
	public void populateDynamicSKUDeatilInVO(SKUDetailVO sKUDetailVO,
			String country, String siteId, boolean fromCart) {
		
		getPriceListRepositoryTools().populateDynamicSKUDeatilInVO(sKUDetailVO, country, siteId, fromCart);	
	}
	
	
	/**
	 * Imran-New Method to optimze SKUDetailVO.
	 * 
	 * @param sKUDetailVO
	 * @param country
	 * @param siteId
	 * @param fromCart
	 */
	public BBBDynamicPriceSkuVO getDynamicPriceSKUVO(String skuId, boolean fromCart) {
		
		logDebug("Inside getDynamicPriceSKUVO ");

		String siteId = BBBUtility.getCurrentSiteId(ServletUtil.getCurrentRequest());
		String country= BBBInternationalShippingConstants.DEFAULT_COUNTRY;
		if (returnCountryFromSession() != null) {
			country = returnCountryFromSession();
		}
		
		RepositoryItem dynamicSkuItem = null;
		BBBDynamicPriceSkuVO  skuDynamicPriceVo = null;
		try {

			//toggle between local cache container or coherence cache container
			if (isLocalSKUDynamicCacheEnabled() && getLocalDynamicSKUPriceCache().isSkuCacheReady()) {
				
				logDebug("Fetching dynamice sku data from local SKU cache in populateDynamicSKUDeatilInVO(): " + skuId);
				
				skuDynamicPriceVo = getLocalDynamicSKUPriceCache().lookUPSKUItemInCache("sku_" + skuId);
				
				logDebug("Feched dynamic skuDynamicPriceVo from local SKU cache="+skuDynamicPriceVo);
			} else if(null != getSkuCacheContainer()){
				logDebug("Fetching dynamice sku data from coherence cache in populateDynamicSKUDeatilInVO(): " + skuId);
				skuDynamicPriceVo = (BBBDynamicPriceSkuVO) getSkuCacheContainer().get("sku_" + skuId);
				logDebug("Feched dynamic skuDynamicPriceVo from coherence="+skuDynamicPriceVo);
			}			
			
			if (skuDynamicPriceVo == null && fromCart) {
				//adding fromCart check to make DB call only while adding to cart.
				
				this.vlogDebug("Making Dynamic repository call to check if incart eligible sku fromCart:{0} & skuId:{1}",fromCart,skuId);
				dynamicSkuItem = getDynamicPriceRepository().getItem(skuId, BBBCatalogConstants.SKU_PRICE_ITEM);	
				if(null != dynamicSkuItem){
					logDebug("Feched dynamic skuDynamicPriceVo from repository "+skuId);
					
					skuDynamicPriceVo = new BBBDynamicPriceSkuVO();
					skuDynamicPriceVo.setBabyIncartFlag((boolean)dynamicSkuItem.getPropertyValue(BBBCatalogConstants.BABY_IN_CART_FLAG_SKU));
					skuDynamicPriceVo.setBabyPricingLabelCode((String)dynamicSkuItem.getPropertyValue(BBBCatalogConstants.BABY_PRICING_LABEL_CODE_SKU));
					skuDynamicPriceVo.setBbbIncartFlag((boolean)dynamicSkuItem.getPropertyValue(BBBCatalogConstants.BBB_IN_CART_FLAG_SKU));
					skuDynamicPriceVo.setBbbPricingLabelCode((String)dynamicSkuItem.getPropertyValue(BBBCatalogConstants.BBB_PRICING_LABEL_CODE_SKU));
					skuDynamicPriceVo.setCaIncartFlag((boolean)dynamicSkuItem.getPropertyValue(BBBCatalogConstants.CA_IN_CART_FLAG_SKU));
					skuDynamicPriceVo.setCaPricingLabelCode((String)dynamicSkuItem.getPropertyValue(BBBCatalogConstants.CA_PRICING_LABEL_CODE_SKU));
					skuDynamicPriceVo.setMxIncartFlag((boolean)dynamicSkuItem.getPropertyValue(BBBCatalogConstants.MX_IN_CART_FLAG_SKU));
					skuDynamicPriceVo.setMxPricingLabelCode((String)dynamicSkuItem.getPropertyValue(BBBCatalogConstants.MX_PRICING_LABEL_CODE_SKU));
				 }
			}

			if (skuDynamicPriceVo != null&& (country.equals(BBBInternationalShippingConstants.COUNTRY_MEXICO) || country.equals(BBBInternationalShippingConstants.DEFAULT_COUNTRY))) {
				updateDynamicPriceFlags(skuId, skuDynamicPriceVo, country,siteId);
			}

			// If condition for international user in cart flag for Baby and US
			// site
			else {
				if (skuDynamicPriceVo != null) {
					if (siteId.equals(BBBCoreConstants.SITE_BBB)
							|| siteId.equals(BBBCoreConstants.SITEBAB_BABY_TBS)) {
						skuDynamicPriceVo.setInCartFlag(skuDynamicPriceVo.isBabyIncartFlag());
					} else if (siteId.equals(BBBCoreConstants.SITE_BAB_US)
							|| siteId.equals(BBBCoreConstants.TBS_BEDBATH_US)) {
						skuDynamicPriceVo.setInCartFlag(skuDynamicPriceVo.isBbbIncartFlag());

					}

					logDebug("In Cart Flag : " + skuId
							+ "for Site Id :" + siteId);
				}

			}

		} catch (RepositoryException e) {
			logError("Error while fetching SKU item from Dynamic Repository for SKU id: "
							+ skuId, e);

		}
		
		return skuDynamicPriceVo;
	}
	
	/**
	 * Fetch flag from configure keys to check is localSKUDynamic Cache Enabled
	 * @return
	 */
	public boolean isLocalSKUDynamicCacheEnabled(){
		
		// default value
		boolean localSKUDynamicCacheEnabled = false;
		String enableLocalSKUDynamicPriceString = getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, 
				BBBCoreConstants.ENABLE_LOCAL_SKU_DYNAMIC_PRICING_KEY, isDynamicSKUCacheEnabledDefault()?"true":"false");
			
		if(enableLocalSKUDynamicPriceString!=null ){
			localSKUDynamicCacheEnabled = Boolean.parseBoolean(enableLocalSKUDynamicPriceString);
		}
		return localSKUDynamicCacheEnabled;
	}

	
	/**
	 * This method gets Incart price.
	 * Story - BBBH-2889.
	 *
	 * @param productId the product id
	 * @param skuId the sku id
	 * @return the incart price
	 */
	@Override
	public Double getIncartPrice(String productId, String skuId) {
		
		return getPriceListRepositoryTools().getIncartPrice(productId, skuId);

    }
	
	
	/**
	 * BBBH-3982 Store-incart price on kickstarter page
	 * Update shipping message flag for incarteligible skus.
	 *
	 * @param sKUDetailVO the s ku detail vo3
	 * @param incartEligible the incart eligible
	 * @throws BBBBusinessException the BBB business exception
	 */
    public void updateShippingMessageFlag(SKUDetailVO sKUDetailVO, boolean incartEligible) throws BBBBusinessException {
    	logDebug("BBBCatalogToolsImpl.updateShippingMessageFlag :starts :: incartEligible"+incartEligible);
    	
    	if(sKUDetailVO.isLtlItem() || ServletUtil.getCurrentRequest() ==null || (((BBBSessionBean) ServletUtil.getCurrentRequest().resolveName(BBBGiftRegistryConstants.SESSION_BEAN)).isInternationalShippingContext())){
    		return;
    	}
    	Profile profile = (Profile) ServletUtil.getCurrentRequest().resolveName(ComponentName
				.getComponentName(BBBCoreConstants.ATG_PROFILE));
	    if(incartEligible && !profile.isTransient()){
	    	double priceVal = 0.00;
	    	double higherShipThreshhold = 0.00;
	    	String higherShippingThreshhold =  getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_HIGHER_FREE_SHIPPING_THRESHOLD, this.getDefaultLocale(), null,SiteContextManager.getCurrentSiteId());
	    	if(!StringUtils.isBlank(higherShippingThreshhold)){
	    		String trimedHigherShippingThreshold = higherShippingThreshhold.replaceAll("[^0-9^.]", BBBCoreConstants.BLANK).trim();
	    		if(!trimedHigherShippingThreshold.equalsIgnoreCase(BBBCoreConstants.BLANK)){		
	    			higherShipThreshhold = Double.parseDouble(higherShippingThreshhold);
	    		} 
	    	} else{
	    		return;
	    	}
			priceVal = getIncartPrice(sKUDetailVO.getParentProdId(), sKUDetailVO.getSkuId());
	    	Map<String, String> placeholderMap = new HashMap<String, String>();
	    	placeholderMap.put(BBBCoreConstants.CURRENCY, BBBCoreConstants.DOLLAR);
	    	placeholderMap.put(BBBCoreConstants.HIGHER_SHIP_THRESHHOLD, higherShippingThreshhold);
	    	if(priceVal > higherShipThreshhold){
	    		sKUDetailVO.setShipMsgFlag(true);
	    		sKUDetailVO.setDisplayShipMsg(getLblTxtTemplateManager().getPageTextArea(BBBCoreConstants.TXT_FREESHIPPING_PRODUCT, placeholderMap));
	    	} else {
	    		sKUDetailVO.setShipMsgFlag(false);
	    		sKUDetailVO.setDisplayShipMsg(EMPTYSTRING);
	    	}

    }
	    logDebug("BBBCatalogToolsImpl.updateShippingMessageFlag : ends :: DisplayShipMessage:"+ sKUDetailVO.isShipMsgFlag() + "and Display Message" + sKUDetailVO.getDisplayShipMsg());
    }
    
    /**
	 * If the SKU does not exist in the system then the method will throw
	 * BBBBusinessException with an error code indicating the SKU does not exist
	 * If the SKU is not active yet based on Start date and End date method
	 * method will throw BBBBusinessException with an error code indicating the
	 * SKU is not active If the calculateAboveBelowLine flag is true then the
	 * Above or Below Line logic is calculated for each SKU. If fromCart is
	 * true, then the details are retrieved from DB or else from cache.
	 * 
	 * @param parameterMapv with all parameters: siteId, skuId, calculateAboveBelowLine, fromCart
	 * @return the Dynamic SKU details
	 * @throws BBBBusinessException
	 *             the BBB business exception
	 * @throws BBBSystemException
	 *             the BBB system exception
	 */
    @Override
	public SKUDetailVO getSKUDynamicPriceDetails(Map<Object, Object> parameterMap)
			throws BBBBusinessException, BBBSystemException {
        String siteId = null;
		String skuId = null;
		boolean calculateAboveBelowLine = false;
		boolean fromCart = false;
    	if(null != parameterMap.get("siteId")){
    		siteId = parameterMap.get("siteId").toString();
    	}
    	if(null != parameterMap.get("skuId")){
    		skuId = parameterMap.get("skuId").toString();
    	}
    	if(null != parameterMap.get("calculateAboveBelowLine")){
    		calculateAboveBelowLine = (boolean) parameterMap.get("calculateAboveBelowLine");
    	}
    	if(null != parameterMap.get("fromCart")){
    		fromCart = (boolean) parameterMap.get("fromCart");
    	}
		if (!BBBUtility.isEmpty(skuId) || !BBBUtility.isEmpty(siteId)){
			this.logDebug("Catalog API Method Name [getSKUDetails] siteId[" + siteId + "] pSkuId[" + skuId + "]");
	        try {
	            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetails");
	            final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId,
	                            BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
	            if (skuRepositoryItem == null) {
	                this.logDebug("Repository Item is null for skuId " + skuId);
	                throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
	                                BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
	            }
	            
	            final boolean isActive = this.isSkuActive(skuRepositoryItem);
	            this.logDebug(skuId + " Sku is disabled no longer available");
	            if (isActive) {
	                return this.getSKUDetailVO(skuRepositoryItem, siteId, calculateAboveBelowLine, fromCart);
	            }
	            this.logDebug(skuId + " In Exception Sku is disabled no longer available");
	            throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY,
	                            BBBCatalogErrorCodes.SKU_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY);
	        } catch (final RepositoryException e) {
	            this.logError("Catalog API Method Name [getSKUDetails]: RepositoryException for sku Id " + skuId);
	            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
	                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);

	        } finally {
	            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetails");
	        }
		} else
			throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                    BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
	}
    
    /**
     * Method will return a omniture value, which is dependent on siteSpect header value (boost code or vendor param)
     * @param pRequest
     * @return
     */
    
    public String getOmnitureVariable(DynamoHttpServletRequest pRequest) {
		logDebug("BBBCatalogToolsImpl.getOmnitureVariable- Value in siteSpect Header: Starts");
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL
				+ " getOmnitureVariable");
		String lOmnitureVariable = EMPTYSTRING;
		String ephQueryScheme = EMPTYSTRING;
		String actualBoostingApplied=EMPTYSTRING;
		SearchQuery searchQry = null;
			searchQry = (SearchQuery) pRequest.getAttribute(BBBEndecaConstants.RQST_PARAM_NAME_SEARCH_QUERY_VO);
			actualBoostingApplied	=	(String) pRequest.getAttribute(BBBCoreConstants.ACTUALAPPLIEDALGORITHM);
		String lPageType = pRequest.getParameter(BBBCoreConstants.PAGE_NAME);
		if ((BBBCoreConstants.SEARCH).equalsIgnoreCase(lPageType)
				&& ephQueryScheme.equalsIgnoreCase(EMPTYSTRING)) {
			if (searchQry != null
					&& !searchQry.isFromBrandPage()
					&& getSearchUtil().getEphLookUpUtil().isEpHLookUpEnable()
					&& StringUtils.isNotBlank(searchQry.getKeyWord())
					&& BBBUtility.isNotEmpty(getSearchUtil()
							.getEPHBoostingScheme(searchQry))) {
				ephQueryScheme = getSearchUtil().getEPHBoostingScheme(
						searchQry);
			}
		}

		if(searchQry!=null && !searchQry.isEPHFound()&& !searchQry.isEphApplied()&& !BBBCoreConstants.OFF.equalsIgnoreCase(ephQueryScheme)){
		
			ephQueryScheme = BBBCoreConstants.No_EPH;
		}
		if ((BBBCoreConstants.SEARCH).equalsIgnoreCase(lPageType)) {
			ephQueryScheme = BBBCoreConstants.COLON + ephQueryScheme;
		} else if ((BBBCoreConstants.L2L3).equalsIgnoreCase(lPageType)) {
			// ephQueryScheme will be 00 in case of category landing page
			ephQueryScheme = BBBCoreConstants.COLON+BBBCoreConstants.DOUBLE_ZERO_VALUE;
		} else if ((BBBCoreConstants.BRAND).equalsIgnoreCase(lPageType)) {
			// ephQueryScheme will be 00 in case of Brand page
			ephQueryScheme = BBBCoreConstants.COLON+BBBCoreConstants.DOUBLE_ZERO_VALUE;
		}
		String boostCodeInSession = null;
		String vendorParamInSession = null;
		if (BBBUtility.isNotEmpty((String) pRequest.getSession().getAttribute(
				BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE))
				/*&& !((String) pRequest.getSession().getAttribute(
						BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE))
						.equalsIgnoreCase(BBBEndecaConstants.BOOST_ALGORITHM_DEFAULT_BOOST_CODE)*/) {
			boostCodeInSession = (String) pRequest.getSession().getAttribute(
					BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE);
		}

		vendorParamInSession = (String) pRequest.getSession().getAttribute(
				BBBCoreConstants.VENDOR_PARAM);

		logDebug("BBBCatalogToolsImpl.getOmnitureVariable- Value in boostCodeInSession : "
				+ boostCodeInSession
				+ " vendorParamInSession : "
				+ vendorParamInSession);
		String vendorFlagOn = (String) this.getConfigKeyValue(
				BBBCoreConstants.VENDOR_KEYS, BBBCoreConstants.VENDOR_FLAG,
				BBBCoreConstants.FALSE);
		// if vendor flag if off, then value for evar61 is control
		logDebug("BBBCatalogToolsImpl.getOmnitureVariable :if vendorFlag is off, then eVar61 will be control, vendorFlag: "
				+ vendorFlagOn);

		if (Boolean.valueOf(vendorFlagOn)
				&& BBBUtility.isEmpty(boostCodeInSession)
				&& BBBUtility.isNotEmpty(vendorParamInSession)) {
			logDebug("BBBCatalogToolsImpl.getOmnitureVariable : Operation for Vendor Param : vendorParamInSession Value is:"
					+ vendorParamInSession);
			// If VendorParam is present in Header(Mobile)/Session(Desktop and
			// TBS) then retrieve Vendor Name from Repository
			// Set Omniture Variable from BCC Config Key
			if (lPageType.equalsIgnoreCase("SEARCH")) {
				String lOmnitureVariablevalue = BBBCoreConstants.COLON+BBBCoreConstants.DOUBLE_ZERO_VALUE
						+BBBCoreConstants.COLON+BBBCoreConstants.DOUBLE_ZERO_VALUE
						+BBBCoreConstants.COLON+BBBCoreConstants.DOUBLE_ZERO_VALUE;
				lOmnitureVariable = getConfigKeyValue(
						BBBCoreConstants.VENDOR_KEYS, vendorParamInSession
								+ BBBCoreConstants._SEARCHVENDOR_SITESPECT, EMPTYSTRING)
						+ lOmnitureVariablevalue;
			}

		} else if(BBBUtility.isNotEmpty(boostCodeInSession) ){

			logDebug("BBBCatalogToolsImpl.getOmnitureVariable : Operation for Boost Code : boostCodeInSession Value is:"
					+ boostCodeInSession);

			// Retrieve Search Boost Algorithm description
			Map<String, String> lSearchAlgorithmParams = new HashMap<String, String>();

			// Setting lSearchAlgorithmParams

			lSearchAlgorithmParams.put(
					BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE,
					boostCodeInSession);
			lSearchAlgorithmParams.put(BBBCoreConstants.PAGE_NAME,
					getPageTypeMap().get(lPageType));
			lSearchAlgorithmParams.put(BBBCoreConstants.SITE_ID,
					SiteContextManager.getCurrentSiteId());
			// Fetch SearchBoostAlgorithmDescription from LocalMap
			SearchBoostingAlgorithmVO lSearchAgorithmVO = getEndecaSearchTools()
					.getSearchBoostingAlgorithmsFromLocalMap(
							lSearchAlgorithmParams);
			if (null != lSearchAgorithmVO
					&& lSearchAgorithmVO.getOmnitureEventRequired()
					&& BBBUtility.isNotEmpty(lSearchAgorithmVO
							.getOmnitureDescription())) {
				logDebug("BBBCatalogToolsImpl.getOmnitureVariable - Getting Value from Search Boost Local Map");
				// Applying Sort string
				lSearchAgorithmVO
						.setOmnitureDescription(applySortFilter(lSearchAgorithmVO.getOmnitureDescription()));
				actualBoostingApplied	=	applySortFilter(actualBoostingApplied, lSearchAgorithmVO.getOmnitureDescription());
				// Append endeca to lOmnitureVariable
				lOmnitureVariable = BBBCoreConstants.ENDECA + BBBCoreConstants.COLON
						+ lSearchAgorithmVO.getOmnitureDescription() + BBBCoreConstants.COLON
						+ actualBoostingApplied + ephQueryScheme;
				pRequest.getSession().setAttribute(BBBCoreConstants.IS_ENDECA_CONTROL, false);
			}else{
				final String defaultEndecaOmnitureVariable = getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.DEFAULT_ENDECA_OMNITURE_VARIABLE, BBBCoreConstants.CONTROL);
				lOmnitureVariable	=	defaultEndecaOmnitureVariable +ephQueryScheme;
				pRequest.getSession().setAttribute(BBBCoreConstants.IS_ENDECA_CONTROL, true);
			}
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL
				+ " getOmnitureVariable");
		logDebug("BBBCatalogToolsImpl.getOmnitureVariable- omnitureValue is:"
				+ lOmnitureVariable);
		return lOmnitureVariable;
	}
    /*
     * This method is used to apply sorting in Assending 
     * order Based on given String in # format
     * 
     * @param sortedString
     * @return 
     * 
     * */
    public String applySortFilter(String sortedString) {
		
		return applySortFilter(sortedString, EMPTYSTRING);
	}
    
    /*
     * This method is used to apply sorting in Assending 
     * order Based on given String in # format
     * 
     * @param SortedString
     * @param inputBoosting
     * @return 
     * 
     * */
    public String applySortFilter(String sortedString, String inputBoosting) {
		String afterSortedString = EMPTYSTRING;
		if (BBBUtility.isNotEmpty(sortedString)) {
			String beforeSortedString[] = sortedString.split(BBBCoreConstants.HASH);
			Arrays.sort(beforeSortedString);
			for (int i = 0; i < beforeSortedString.length; i++) {
				afterSortedString += beforeSortedString[i];
			}
		} else {
			afterSortedString = inputBoosting.replaceAll(NUM_REGEX, STRING_00);//if empty, change input boost with 00 values
		}
		return afterSortedString;
	}

    
    public final ProductVO getProductDetailsForLazyLoading(final String pSiteId, final String pProductId,
    	final boolean populateRollUp, final boolean isMinimalDetails, int index, final boolean isAddException)
        throws BBBSystemException, BBBBusinessException {
			final StringBuffer debug = new StringBuffer(50);
			int prdRowCount = BBBCoreConstants.ONE;
			List<String> prdBatchSizeList = null;
			try {
				prdBatchSizeList = this.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "collection_child_prd_row_count");
			} catch (BBBSystemException se) {
				logError(se);
			} catch (BBBBusinessException be) {
				logError(be);
			}
			
			if (!BBBUtility.isListEmpty(prdBatchSizeList)) {
				prdRowCount = Integer.parseInt(prdBatchSizeList.get(0));
			}
			int size = BBBCoreConstants.THREE * prdRowCount;
			debug.append("Catalog API Method Name [getProductDetails] siteId[").append(pSiteId).append("] pProductId [")
					.append(pProductId);
			this.logDebug(debug.toString());
	
			final long startTime = System.currentTimeMillis();
			if ( !StringUtils.isEmpty(pSiteId)) {
				ProductVO productVO = new ProductVO();
				try {
					BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_1");
					final RepositoryItem productRepositoryItem = this.getCatalogRepository().getItem(pProductId,
							BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
					if (productRepositoryItem == null) {
						BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_1");
						throw new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
								BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);
					}
					final boolean isActive = this.isProductActive(productRepositoryItem);
					if (!isActive && isAddException) {
	                	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_1");
	                	 throw new BBBBusinessException(
	                                    BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY,
	                                    BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY);
	                }
					boolean isCollection = false;
					boolean isLeadProduct = false;
					boolean isLTLProduct = false;
					if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) != null) {
						isLTLProduct = ((Boolean) productRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU))
								.booleanValue();
					}
					
					if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_ID_PRODUCT_PROPERTY)!=null) {
						productVO.setVendorId((String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_ID_PRODUCT_PROPERTY));
					}
					 
	//				LTL check product
					productVO.setLtlProduct(Boolean.valueOf(isLTLProduct));
					 
					if (productRepositoryItem
							.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) != null) {
						isCollection = ((Boolean) productRepositoryItem
								.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME)).booleanValue();
					}
					if (productRepositoryItem
							.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) != null) {
						isLeadProduct = ((Boolean) productRepositoryItem
								.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME)).booleanValue();
					}
					this.logTrace("Product is a Collection [" + isCollection + "] product is a lead product ["
							+ isLeadProduct + "]");
					if ((isCollection || isLeadProduct) && !isMinimalDetails) {
						@SuppressWarnings("unchecked")
						final List<RepositoryItem> childProductsRelationList = (List<RepositoryItem>) productRepositoryItem
								.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME);
	
						if ((!BBBUtility.isListEmpty(childProductsRelationList)) || isLeadProduct) {
							final List<ProductVO> subProductVOList = new ArrayList<ProductVO>();
							this.logTrace("No of child Products [" + subProductVOList.size() + "]");
							RepositoryItem childProdItem = null;
							RepositoryItem childProdRollUpTypesItem = null;
							ProductVO subProductVO = null;
							String childProductId = null;
							boolean giftFlag = false;
							DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
							if (childProductsRelationList != null) {
								int activeProductCount = BBBCoreConstants.ZERO;
								if (index != BBBCoreConstants.INT_MINUS_ONE) {
									for (int counter = index; counter < childProductsRelationList.size(); counter++) {
										
										childProdItem = (RepositoryItem) childProductsRelationList.get(counter).getPropertyValue(
												BBBCatalogConstants.CHILD_PRODUCTS_PRODUCT_RELATION_PROPERTY_NAME);
										if (null != childProdItem.getPropertyValue("giftCertProduct")) {
											giftFlag = ((Boolean) childProdItem.getPropertyValue("giftCertProduct"))
													.booleanValue();
										}
		
										childProductId = childProdItem.getRepositoryId();
										this.logTrace("Product Id of " + counter + "th child  [" + childProductId + "]");
										// check if sub product is active only then
										// add to list
										if (this.isProductActive(childProdItem)) {
											activeProductCount++;
											@SuppressWarnings("unchecked")
											final List<RepositoryItem> skuRepositoryItems = (List<RepositoryItem>) childProdItem
													.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);
											childProdRollUpTypesItem = (RepositoryItem) childProductsRelationList.get(counter).getPropertyValue(BBBCatalogConstants.COLLECTION_ROLL_UP_PRODUCT_RELATION_PROPERTY_NAME);
											subProductVO = this.getProductDetails(pSiteId, childProductId, false);
											if (childProdRollUpTypesItem != null) {
												final String childProductrollUpAttribute = (String) childProdRollUpTypesItem.getPropertyValue(BBBCatalogConstants.ROLLUP_ATTRIBUTE_ROLLUP_PROPERTY_NAME);
												this.logTrace("Product Relation Roll Up attribute String of  " + counter + "th child  [" + childProdItem.getRepositoryId() + "]");
												// Flag Added to check Product/SKU
												// Active
												// item for SiteMap Changes 504 -b
												subProductVO.setPrdRelationRollup(this.getRollUpAttributeForProduct(childProductrollUpAttribute, skuRepositoryItems, true));
												// subProductVO.setRollupAttributes(this.getRollUpAttributeForProduct(childProductrollUpAttribute,
												// skuRepositoryItems));
											}
											if (subProductVO.getAttributesList() != null) {
												Set<String> keySet = subProductVO.getAttributesList().keySet();
												List<AttributeVO> allAttributesList = new ArrayList<AttributeVO>();
												for (String key : keySet) {
													List<AttributeVO> attributesList = subProductVO.getAttributesList().get(key);
													if (attributesList != null) {
														allAttributesList.addAll(attributesList);
													}
												}
												subProductVO.setProductAllAttributesVO(remDuplicateAttributes(allAttributesList));
											}
											subProductVOList.add(subProductVO);
											
										} else {
											this.logTrace("sub product with Product Id   [" + childProductId
													+ "] is diabled so not including in list of sub products");
										}
										if (counter == childProductsRelationList.size() - 1) {
											request.setParameter("nextIndex", -1);
										} else if(activeProductCount == size){
											request.setParameter("nextIndex", counter+1);
											break;
										}
//										if ((counter == childProductsRelationList.size() - 1) && request != null) {
//											request.setParameter("nextIndex", -1);
//										}
									}
								}
							}
							productVO = this.getCollectionProductVO(productRepositoryItem, subProductVOList, pSiteId);
							// If all child SKU have giftCertProduct true then set
							// this true
							productVO.setGiftCertProduct(Boolean.valueOf(giftFlag));
							// Logic for View Product guide link on PDP.
							
							 final String prodGuideId = this.getProductGuideId(productRepositoryItem, pSiteId);
							 if (!BBBUtility.isEmpty(prodGuideId)) {
								 productVO.setShopGuideId(prodGuideId); 
							 }
							 getVendorInfoProductVO(productVO, productRepositoryItem);
							 this.logTrace(productVO.toString());
							 
							// As part of Web Collage we will no longer show Product
							// Tour on mobile
							// BPS-1388 commenting following code
	
							// START || Easy 2 Integration in Mobile || R2.2.1
							// productVO = getProductVOMediaDetails(productVO, pProductId, pSiteId);
							// END || Easy 2 Integration in Mobile || R2.2.1
							return productVO;
						}
						this.logError("catalog_1006: Product is a collection but has no child products");
						BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_1");
						throw new BBBBusinessException(
								BBBCatalogErrorCodes.CHILD_PRODUCTS_NOT_PRESENT_FOR_COLLECTION_PRODUCT,
								BBBCatalogErrorCodes.CHILD_PRODUCTS_NOT_PRESENT_FOR_COLLECTION_PRODUCT);
					}
					this.logTrace("Product is not a Collection ");
					return this.getProductVO(productRepositoryItem, pSiteId, populateRollUp, isMinimalDetails);
				} catch (final RepositoryException e) {
					this.logError("Catalog API Method Name [getProductDetails]: RepositoryException ");
					BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_1");
					throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
							BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
				} finally {
					final long totalTime = System.currentTimeMillis() - startTime;
					this.logDebug("Total time taken for BBBCatalogTools.getProductDetails() is: " + totalTime
							+ " for product id: " + pProductId + " and minimal details is: " + isMinimalDetails);
					BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetails_1");
				}
	}
	throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
	                BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
	}
    
    /**
     * Gets the customization code from sku.
     *
     * @param skuRepoItem the sku repo item
     * @return the customization code from sku
     */
    public String getCustomizationCodeFromSKU(final RepositoryItem skuRepoItem) {
    	String customizationCode = null;
    	if(null!=skuRepoItem.getPropertyValue(BBBCoreConstants.ELIGIBLE_CUSTOMIZATION_CODES)){
    		customizationCode = (String) skuRepoItem.getPropertyValue(BBBCoreConstants.ELIGIBLE_CUSTOMIZATION_CODES);
    	}
    	return customizationCode;
    }
    
    public SortOptionVO getSortOptionsForSite() {
    	try {
			return getSiteRepositoryTools().getSortOptionsForSite();
		} catch (BBBSystemException | BBBBusinessException e) {
			logError("BBBSystemException | BBBBusinessException  in getSortOptionsForSite for  BBBCatalogTools "+e.getMessage());
			if(isLoggingDebug()){
				logError(" Exception occured while generateSitemapUrls ",e);
			}
		   return null;
		}
    	
    }
    /**
     * Gets the customization code from sku.
     *
     * @param skuId the sku id
     * @return the customization code from sku
     */
    public String getCustomizationCodeFromSKU(final String skuId) {
    	RepositoryItem skuRepoItem;
		try {
			skuRepoItem = this.getCatalogRepository().getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
			return getCustomizationCodeFromSKU(skuRepoItem);
		} catch (RepositoryException e) {
			logError("Sku Item not found for skuId " + skuId);
		}
		return null;
    }
    
    /**
     * Gets the category Recommendations .
     *
     * @param categoryId the category Id
     * @param productId the product Id
     * @return List<RecommendedCategoryVO>
     */
    @Override
	public List<RecommendedCategoryVO> getCategoryRecommendation(String categoryId, String productId) {
		logDebug("Entering BBBCatalogTools getCategoryRecommendation() for params: categoryId: "+categoryId+" productId: "+productId);
		List<RecommendedCategoryVO> catRecomm= getCategoryRelatedCatRecommendation(categoryId);
		try {
			if (categoryId != null && !BBBUtility.isListEmpty(catRecomm)) {
				logDebug("Recommendations available for categoryId " + categoryId + ":" + catRecomm.size());

			} else if(productId!=null) {

				String primaryCategory = getPrimaryCategory(productId);
				if (primaryCategory != null) {
					catRecomm = getCategoryRelatedCatRecommendation(primaryCategory);
					logDebug("Recommendations available for primary Category " + primaryCategory + ":"+ catRecomm.size());
				} else {
					String parentCategory = getParentCategoryIdForProduct(productId, getCurrentSiteId());
					catRecomm = getCategoryRelatedCatRecommendation(parentCategory);
					logDebug("Recommendations available for parent Category " + parentCategory + ":"+ catRecomm.size());
				}

			}
		
			
		} catch (BBBSystemException | BBBBusinessException e) {
			logError("Error while fetching Category Recommendations for category Id::"+categoryId+" :: productId: "+ productId+" :"+e);
		}
		logDebug("Exiting BBBCatalogTools getCategoryRecommendation() with for: categoryId: "+categoryId+" productId: "+productId);
		return catRecomm;
	}
   
	 /**
     * Gets the  Recommendation Details .
     *
     * @param categoryId the category Id
     * @return RecommendedCategoryVO
     */
	public List<RecommendedCategoryVO> getCategoryRelatedCatRecommendation(String categoryId) {
		logDebug("Entering BBBCatalogTools getCategoryRelatedCatRecommendation() with for: categoryId: " + categoryId);
		List<RecommendedCategoryVO> allRecommendations = new ArrayList<>();
		int numOfRecommToshow=BBBCoreConstants.ZERO;
		
		if (categoryId == null) {
			return allRecommendations;
		}
		
		try {
			if(getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.NUM_OF_RECOMM_TO_SHOW)!=null){
				 numOfRecommToshow = Integer.parseInt(getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.NUM_OF_RECOMM_TO_SHOW).get(0));
			}
			RepositoryItem categoryRecommRepositoryItems = getBbbManagedCatalogRepository().getItem(categoryId,BBBCoreConstants.CATEGORY_DETAILS_ITEM_DESCRIPTOR);
		
			final List<RepositoryItem> recommendations = (List<RepositoryItem>) getProperyValue(categoryRecommRepositoryItems, BBBCoreConstants.CAT_RECOMM_ID);
			if (!BBBUtility.isEmpty(recommendations) ) {
				int itr=0;
				
				Iterator<RepositoryItem> recommendationIterator = recommendations.iterator();
				while (recommendationIterator.hasNext() && itr<numOfRecommToshow) {
					RecommendedCategoryVO recommCategory = 	populateSuggestedCategory((RepositoryItem)recommendationIterator.next());
					allRecommendations.add(recommCategory);
					itr++;
				}
				
			}
		} catch (final RepositoryException | BBBSystemException | BBBBusinessException e) {
			logError("Error while fetching  Recommendation Details for category Id::"+categoryId+" : " + e);

		}
		
		logDebug("Exiting BBBCatalogTools getCategoryRelatedCatRecommendation() with for: categoryId: " + categoryId);

		return allRecommendations;

	}
	
	private RecommendedCategoryVO populateSuggestedCategory(RepositoryItem recommendation){
		RecommendedCategoryVO recommCategory = new RecommendedCategoryVO();
		if(recommendation.getPropertyValue(BBBCoreConstants.CAT_RECOMM_ID)!=null){
			recommCategory.setCatRecommId((String) recommendation.getPropertyValue(BBBCoreConstants.CAT_RECOMM_ID));
		}
		if(recommendation.getPropertyValue(BBBCoreConstants.RECOMM_IMAGE_URL)!=null){
			recommCategory.setRecommCategoryImageUrl((String) recommendation.getPropertyValue(BBBCoreConstants.RECOMM_IMAGE_URL));
		}
		/*String contextPath = ServletUtil.getCurrentRequest().getContextPath();*/
		if(recommendation.getPropertyValue(BBBCoreConstants.RECOMM_LINK)!=null){
			recommCategory.setRecommCategoryLink((String) recommendation.getPropertyValue(BBBCoreConstants.RECOMM_LINK));
		}
		if(recommendation.getPropertyValue(BBBCoreConstants.RECOMM_TEXT)!=null){
			recommCategory.setRecommCategoryText((String) recommendation.getPropertyValue(BBBCoreConstants.RECOMM_TEXT));
		}
		return recommCategory;
	}

	private static Object getProperyValue(final Object pItem, final String pPropertyName){
		if (pItem ==null){
			return null;
		}
		
		if(pItem instanceof RepositoryItemImpl){
			return ((RepositoryItemImpl)pItem).getPropertyValue(pPropertyName);
		}

		return null;
	}
	
	  /**
     * Gets the Product Child and Siblings .
     *
     *@param productId the product Id
     * @return List<RecommendedCategoryVO>
     */
	@Override
	public final List<ProductVO> getProductDetailsWithSiblings(String pProductId) throws BBBBusinessException, BBBSystemException {
		logDebug("Entering getProductDetailsWithSiblings() with param productId: "+pProductId);
		List<ProductVO> childOrSiblingProductVOList = new ArrayList<>();
		
		if ((getCurrentSiteId() != null) && !StringUtils.isEmpty(getCurrentSiteId())) {
			try {

				final RepositoryItem productRepositoryItem = this.getCatalogRepository().getItem(pProductId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);

				if (productRepositoryItem == null) {
					throw new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);
				}

				final boolean isActive = this.isProductActive(productRepositoryItem);

				if (!isActive) {
					throw new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY,BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY);
				}

				boolean isLeadProduct = false;
				if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) != null) {
					isLeadProduct = ((Boolean) productRepositoryItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME)).booleanValue();
				}

				if (isLeadProduct) {
					
					childOrSiblingProductVOList=getChildProductsForLead(productRepositoryItem);
				} else {
					childOrSiblingProductVOList=getSiblingsForProduct(productRepositoryItem,pProductId);
				}
			} catch (final RepositoryException e) {
				this.logError("Catalog API Method Name [getProductDetails]: RepositoryException ");
				throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
			}
		}
		logDebug("Exiting getProductDetailsWithSiblings() with param productId: "+pProductId);
		return childOrSiblingProductVOList;

	}
	
	 /**
     * Gets Siblings for product.
     *
     *@param productId the product Id
     *@param productRepositoryItem the productRepositoryItem
     * @return List<ProductVO>
     */
	  private List<ProductVO> getSiblingsForProduct(RepositoryItem productRepositoryItem, String pProductId) {
		  List<ProductVO> childOrSiblingProductVOList = new ArrayList<>();
		  RepositoryItem[] productRelItem;
		  try {
				RepositoryView productRelView = this.getCatalogRepository().getView(BBBCatalogConstants.BBB_PRD_RELN);
				RqlStatement productRelStatement;
				productRelStatement = RqlStatement.parseRqlStatement(getProductRelItemQuery());
				Object productRelParams[] = new Object[1];
				productRelParams[0] = productRepositoryItem.getRepositoryId();
				logDebug("Is Not Lead Product : ProductId" + productRelParams[0]);
				productRelItem = extractDBCall(productRelView, productRelStatement, productRelParams);
				if (productRelItem != null) {
					for (RepositoryItem productRelationItem : productRelItem) {
				        setSiblingAndChildProducts( productRelationItem,childOrSiblingProductVOList,productRepositoryItem,pProductId);
					}
				}
			} catch (RepositoryException e) {
				logError("Error while fetching for Repository ::" + e);
			}
			return childOrSiblingProductVOList;
		}

	protected RepositoryItem[] extractDBCall(RepositoryView productRelView, RqlStatement productRelStatement,
			Object[] productRelParams) throws RepositoryException {
		return productRelStatement.executeQuery(productRelView, productRelParams);
	}

	  
	  private void setSiblingAndChildProducts(RepositoryItem productRelationItem, List<ProductVO> childOrSiblingProductVOList,
			  	RepositoryItem productRepositoryItem, String pProductId) throws RepositoryException{
		    boolean continueIteration=true;
		  	boolean isParentProductItem = false;
		  	if (productRelationItem == null) {
		  		return;
		  	}
		  	RepositoryItem[] parentProductItem;
			RepositoryView parentProductView = getCatalogRepository().getView(BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
			RqlStatement parentProductStatement = RqlStatement.parseRqlStatement(getParentProductQuery());
			Object parentProductParams[] = new Object[1];
			parentProductParams[0] = productRelationItem.getRepositoryId();
			logDebug("Is Not Lead Product : Product Relation Id" + parentProductParams[0]);
			parentProductItem = extractDBCall(parentProductView, parentProductStatement, parentProductParams);
			if (parentProductItem != null) {
				logDebug("Inside not Null Site Id check");
				isParentProductItem = true;

			}else {
				parentProductStatement = RqlStatement.parseRqlStatement(getParentProductNullQuery());
				logDebug("Inside Null Site Id check");
				parentProductItem = extractDBCall(parentProductView, parentProductStatement, parentProductParams);
				if (parentProductItem != null) {
					isParentProductItem = true;
				}
			}
			
			 boolean isParentCollection = false;
              if (parentProductItem!=null && parentProductItem[0].getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) != null) {
            	  isParentCollection = ((Boolean) parentProductItem[0].getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME)).booleanValue();
            	  logDebug(" productId: "+productRepositoryItem+ " is a collection product");
              }
		  	
		  	if (parentProductItem != null) {
				logDebug("Inside not Null Site Id check");
				isParentProductItem = true;

			}
		  	if (isParentProductItem && isProductActive(parentProductItem[0])) {
				List<RepositoryItem> childProducts = (List<RepositoryItem>) parentProductItem[0].getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME);
				Iterator<RepositoryItem> iterator = childProducts.iterator();
				RepositoryItem childProductItem;
				while (iterator.hasNext() && continueIteration) {
					childProductItem = iterator.next();
					continueIteration=populateSiblingDetails(childProductItem,childOrSiblingProductVOList,pProductId,isParentCollection);
				
				}

			}
		  
	  }
	  

    private boolean populateSiblingDetails(RepositoryItem childProductItem, List<ProductVO> childOrSiblingProductVOList, String pProductId,boolean isParentCollection) {
    	RepositoryItem productRelRepositoryItem;
    	String productIdSibling;
    	boolean continueIteration=true;
    	List<String> siblingProductIds=new ArrayList<>();
		if (childProductItem != null) {
			productRelRepositoryItem = (RepositoryItem) childProductItem.getPropertyValue(BBBCatalogConstants.PRODUCT_ID_PRODUCT_PROPERTY_NAME);
			if (null != productRelRepositoryItem && isProductActive(productRelRepositoryItem)) {
				productIdSibling = productRelRepositoryItem.getRepositoryId();
				logDebug("Is Not Lead Product : Child Product Id" + productIdSibling);
				if (!BBBUtility.isEmpty(productIdSibling)&& !productIdSibling.equals(pProductId) && !siblingProductIds.contains(productIdSibling)) {
					siblingProductIds.add(productIdSibling);
					continueIteration=addchildSiblingToList(childOrSiblingProductVOList,productRelRepositoryItem,isParentCollection,false);
					logDebug(" productId: "+pProductId+ " childOrSiblingProductVOList size::"+childOrSiblingProductVOList.size());
				}
			}
		}
		return continueIteration;
	}

	/**
     * Gets Child Products for main product.
     *
     *@param productRepositoryItem the productRepositoryItem
     * @return List<ProductVO>
     */
	private List<ProductVO> getChildProductsForLead(RepositoryItem productRepositoryItem) {
		  final List<ProductVO> childOrSiblingProductVOList = new ArrayList<>();
		  boolean continueIteration=true;		  
		  logDebug(" productId: "+productRepositoryItem+ " is a lead product");
			@SuppressWarnings("unchecked")
			final List<RepositoryItem> childProductsRelationList = (List<RepositoryItem>) productRepositoryItem.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME);
			if (childProductsRelationList != null && !childProductsRelationList.isEmpty()) {
				RepositoryItem childProdItem;
				for (int i = 0; i < childProductsRelationList.size() && continueIteration ; i++) {
						childProdItem = (RepositoryItem) childProductsRelationList.get(i).getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_PRODUCT_RELATION_PROPERTY_NAME);
						
						// check if sub product is active only then add
						// to list
				if (this.isProductActive(childProdItem)) {
					continueIteration= addchildSiblingToList(childOrSiblingProductVOList,childProdItem,false,true);
				 } else {
						this.logTrace("sub product with Product Id   [" + childProdItem+ "] is diabled so not including in list of sub products");
				 }
				}
				
			}
			return childOrSiblingProductVOList;
		
	}
	
	private boolean addchildSiblingToList(List<ProductVO> childOrSiblingProductVOList,RepositoryItem productRepositoryItem,boolean isParentCollection,boolean isAccessoryLead) {
		ProductVO siblingChildProductVO;
		boolean addChildToList=true;
		String productIdSibling = productRepositoryItem.getRepositoryId();
		boolean continueIteration=true;
		
		String numOfProdRecommDisplay=getNumOfProdRecommToShow();
		try {
			if (childOrSiblingProductVOList.size() < (Integer.parseInt(numOfProdRecommDisplay))) {
				siblingChildProductVO = getProductDetailsMinimal(getCurrentSiteId(),productRepositoryItem, productIdSibling);

				if (isParentCollection) {
					siblingChildProductVO.setParentcollection(true);
				}else if(isAccessoryLead){
					siblingChildProductVO.setAccessoryLead(true);
				}
				if(siblingChildProductVO.getDefaultSkuDetailVO()!=null && !siblingChildProductVO.getDefaultSkuDetailVO().isSkuInStock()){
					addChildToList=false;
				}
				if(addChildToList){
					childOrSiblingProductVOList.add(siblingChildProductVO);
				}
			}
			else{
				continueIteration=false;
			}
		} catch (BBBBusinessException | BBBSystemException e) {
			logError("Error while fetching sibling Prod data for prodId:" + productIdSibling + "::" + e);
		}
		return continueIteration;
	
	}

	/**
     * Gets the Product Details .
     *
     *@param productIdItem the product Id
     * @return List<RecommendedCategoryVO>
     */
	private ProductVO getProductDetailsMinimal(String pSiteId, RepositoryItem productRepositoryItem, String productId) throws BBBBusinessException, BBBSystemException {
		logDebug("Entering getProductDetailsMinimal() with param productId: "+productId);
		ProductVO productVO = new ProductVO();
		if ((pSiteId != null) && !StringUtils.isEmpty(pSiteId)) {
			String isIntlRestricted = BBBCoreConstants.NO_CHAR;
			productVO.setProductId(productId);
			final boolean isActive = this.isProductActive(productRepositoryItem);
			if (!BBBUtility.isEmpty((String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED))) {
				isIntlRestricted = (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED);
			}
			productVO.setIntlRestricted(BBBCoreConstants.YES_CHAR.equalsIgnoreCase(isIntlRestricted));
			
			boolean isLTLProduct = false;
			if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) != null) {
				isLTLProduct = ((Boolean) productRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU)).booleanValue();
			}
			productVO.setLtlProduct(isLTLProduct);
			updatePriceDescription(productVO);
			
			if (isActive) {
				@SuppressWarnings("unchecked")
				
				final List<RepositoryItem> skuRepositoryItems = (List<RepositoryItem>) productRepositoryItem.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);
				if (skuRepositoryItems != null) {
					getDefaultSKU(skuRepositoryItems, productVO);
					setProductMetaDetails(productRepositoryItem, productVO, pSiteId);
				} else {
				this.logTrace("sub product with Product Id   [" + productId+ "] is diabled so not including in list of sub products");
				}
				logDebug("Exiting getProductDetailsMinimal() with param productId: "+productId);

			
		}
		}
		return productVO;
	}
	
	private void getDefaultSKU(List<RepositoryItem> skuRepositoryItems , ProductVO productVO) throws BBBBusinessException, BBBSystemException{
	
		String pDefaultChildSku ;
		final List<String> childSkuIdList = new ArrayList<>();
		for (int i = 0; i < skuRepositoryItems.size(); i++) {
			if (this.isSkuActive(skuRepositoryItems.get(i))) {
				childSkuIdList.add(skuRepositoryItems.get(i).getRepositoryId());
			}
		}
		if (!childSkuIdList.isEmpty()) {
			productVO.setChildSKUs(childSkuIdList);
		}
		if (childSkuIdList.size() == 1) {					
			pDefaultChildSku = childSkuIdList.get(0);	
			
			int recommItemStock = getBbbInventoryManager().getProductAvailability( getCurrentSiteId(), pDefaultChildSku,BBBInventoryManager.PRODUCT_DISPLAY,0);
			SKUDetailVO defaultSkuVO=new SKUDetailVO();
			defaultSkuVO.setSkuId(pDefaultChildSku);
			boolean isSKUPersonalizedItem = productManager.isPersonalizedSku(pDefaultChildSku);
			productVO.setPersonalizedSku(isSKUPersonalizedItem); 
			logDebug("defaultSkuVO productId: "+productVO.getProductId() +"sku Id:: "+defaultSkuVO.getSkuId());
			if(recommItemStock == BBBInventoryManager.AVAILABLE || recommItemStock == BBBInventoryManager.LIMITED_STOCK){
				defaultSkuVO.setSkuInStock(true);
			}
			productVO.setDefaultSkuDetailVO(defaultSkuVO);
		}
	}
	
	private void setProductMetaDetails(RepositoryItem productRepositoryItem,ProductVO productVO, String pSiteId) throws BBBBusinessException, BBBSystemException{
		
		if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) != null) {
			boolean isCollection = ((Boolean) productRepositoryItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME)).booleanValue();
			productVO.setCollection(isCollection);
		}

		if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) != null) {
			boolean isCollection = ((Boolean) productRepositoryItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME)).booleanValue();
			productVO.setCollection(isCollection);
		}
		if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME) != null) {
			productVO.setName((String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME));
		}
		// set image details for product
		final ImageVO productImage = new ImageVO();
		if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_IMAGE_PROPERTY_NAME) != null) {
			final String smallImagePath = (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_IMAGE_PROPERTY_NAME);
			productImage.setSmallImage(smallImagePath);
		}

		if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME) != null) {
			final String mediumImagePath = (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME);
			productImage.setMediumImage(mediumImagePath);
		}
		productVO.setProductImages(productImage);
		if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME) != null) {
			productVO.setSeoUrl((String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME));
		}
		final BazaarVoiceProductVO bvReviews = this.getBazaarVoiceDetails(productRepositoryItem.getRepositoryId(),pSiteId);
		productVO.setBvReviews(bvReviews);
		this.updatePriceDescription(productVO);
		
		String shipMsgDisplayFlag = BBBCoreConstants.FALSE;
		try {
			shipMsgDisplayFlag = getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,	BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG).get(0);
		} catch (BBBSystemException | BBBBusinessException e) {
			logError("Error while getting config key ShipMsgDisplayFlag value", e);
		}
		if (Boolean.parseBoolean(shipMsgDisplayFlag)) {
			updateShippingMessageFlag(productVO);
		}
	}
	
	

	


	public BBBDynamicPriceCacheContainer getProductCacheContainer() {
		return productCacheContainer;
	}
	
	/**
	 * Sets the product cache container.
	 *
	 * @param productCacheContainer the new product cache container
	 */
	public void setProductCacheContainer(
			BBBDynamicPriceCacheContainer productCacheContainer) {
		this.productCacheContainer = productCacheContainer;
	}
	
	/**
	 * Gets the sku cache container.
	 *
	 * @return the sku cache container
	 */
	public BBBDynamicPriceCacheContainer getSkuCacheContainer() {
		return skuCacheContainer;
	}
	
	/**
	 * Sets the sku cache container.
	 *
	 * @param skuCacheContainer the new sku cache container
	 */
	public void setSkuCacheContainer(BBBDynamicPriceCacheContainer skuCacheContainer) {
		this.skuCacheContainer = skuCacheContainer;
	}
	
	/**
	 * Checks if is enable dyn repo call plp.
	 *
	 * @return the enableDynRepoCallPLP
	 */
	public final boolean isEnableDynRepoCallPLP() {
		return enableDynRepoCallPLP;
	}
	
	/**
	 * Sets the enable dyn repo call plp.
	 *
	 * @param enableDynRepoCallPLP the enableDynRepoCallPLP to set
	 */
	public final void setEnableDynRepoCallPLP(boolean enableDynRepoCallPLP) {
		this.enableDynRepoCallPLP = enableDynRepoCallPLP;
	}

	public int getLengthOfSwatch() {
		return lengthOfSwatch;
	}
	/**
	 * @return the tbdPriceString
	 */
	public List<String> getTbdPriceString() {
		return tbdPriceString;
	}

	public void setLengthOfSwatch(int lengthOfSwatch) {
		this.lengthOfSwatch = lengthOfSwatch;
	}


	/**
	 * @param tbdPriceString the tbdPriceString to set
	 */
	public void setTbdPriceString(List<String> tbdPriceString) {
		this.tbdPriceString = tbdPriceString;
	}

	/**
	 * localDynamicSKUPriceCache Cache for Local Dynamic Cache for SKU
	 */
	BBBLocalDynamicPriceSKUCache localDynamicSKUPriceCache;
	/**
	 * @return the localDynamicSKUPriceCache
	 */
	public BBBLocalDynamicPriceSKUCache getLocalDynamicSKUPriceCache() {
		return localDynamicSKUPriceCache;
	}
	
	/**
	 * @param localDynamicSKUPriceCache the localDynamicSKUPriceCache to set
	 */
	public void setLocalDynamicSKUPriceCache(BBBLocalDynamicPriceSKUCache localDynamicSKUPriceCache) {
		this.localDynamicSKUPriceCache = localDynamicSKUPriceCache;
	}

	/**
	 * 
	 * @return dynamicSKUCacheEnabledDefault
	 */
	public boolean isDynamicSKUCacheEnabledDefault() {
		return dynamicSKUCacheEnabledDefault;
	}

	/**
	 * 
	 * @param dynamicSKUCacheEnabledDefault
	 */
	public void setDynamicSKUCacheEnabledDefault(
			boolean dynamicSKUCacheEnabledDefault) {
		this.dynamicSKUCacheEnabledDefault = dynamicSKUCacheEnabledDefault;
	}

	public String getProductRelItemQuery() {
		return productRelItemQuery;
	}
	

	public void setProductRelItemQuery(String productRelItemQuery) {
		this.productRelItemQuery = productRelItemQuery;
	}

	public String getParentProductQuery() {
		return parentProductQuery;
	}
	

	public void setParentProductQuery(String parentProductQuery) {
		this.parentProductQuery = parentProductQuery;
	}

	public String getParentProductNullQuery() {
		return parentProductNullQuery;
	}
	

	public void setParentProductNullQuery(String parentProductNullQuery) {
		this.parentProductNullQuery = parentProductNullQuery;
	}

	public String getNumOfProdRecommToShow() {
		try {
			numOfProdRecommToShow=getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.NUM_OF_PROD_RECOMM_TO_SHOW).get(0);
			
		} catch (BBBSystemException | BBBBusinessException e) {
			logError("Error while fetching numOfProdRecommToShow Config Key: "+e);
		}
		return numOfProdRecommToShow;
	}
	

	public void setNumOfProdRecommToShow(String numOfProdRecommToShow) {
		this.numOfProdRecommToShow = numOfProdRecommToShow;
	} 
	
	
	
	

}
