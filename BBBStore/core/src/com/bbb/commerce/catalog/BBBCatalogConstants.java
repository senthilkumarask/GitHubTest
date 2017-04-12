package com.bbb.commerce.catalog;



public class BBBCatalogConstants {
	public BBBCatalogConstants(){
		super();
	}
	public static final String WEB_OFFERED_PROPERTY_NAME="webOffered";
	public static final String DISABLE_PROPERTY_NAME="disable";
	public static final String SKU_ITEM_DESCRIPTOR="sku";
	public static final String STATES_PROPERTY_NAME="states";
	public static final String RECOMMENDED_FLAG_REG_CAT_PROPERTY_NAME="recommendedCatFlag";
	public static final String ITEM_TRANSLATIONS_INVENTORY_PROPERTY_NAME="inventoryTranslation";
	public static final String ITEM_CACHED_TRANSLATIONS_INVENTORY_PROPERTY_NAME="cachedInventoryTranslation";
	public static final String CONTENT_CATALOG_KEYS = "ContentCatalogKeys";
	public static final String SPECIAL_DEPARTMENTS = "SpecialDepartments";
	public static final String PREVIEW_ENABLED = "previewEnabled";
	public static final String INVALID_SKU_ID="Invalid Sku";
	public static final String SKU_DEATILS_NOT_FOUND="Sku Details Not found For that Sku Id";
	public static final String LOWER_FREE_SHIPPING_THRESHOLD = "lower_free_shipping_threshold";
	public static final String HIGHER_FREE_SHIPPING_THRESHOLD = "higher_free_shipping_threshold";
	
	//Catalog
	public static final String CATALOG_ITEM_DESCRIPTOR= "catalog";
	public static final String BABY_CANADA_L1_CATEGORY= "BabyCanada_L1_Category";
	public static final String PDP_TAB_NAME_HARMON = "PDP_Harmon_Tab_Name";
	
	//category
	public static final String CHILD_CATEGORIES_PROPERTY_NAME="childCategories";
	public static final String CATEGORY_ITEM_DESCRIPTOR= "category";
	public static final String CHILD_PRODUCTS_CATEGORY_PROPERTY_NAME= "childProducts";
	public static final String START_DATE_CATEGORY_PROPERTY_NAME="startDate";
	public static final String END_DATE_CATEGORY_PROPERTY_NAME="endDate";
	public static final String DISABLE_CATEGORY_PROPERTY_NAME="catdisable";
	public static final String CREATION_DATE_CATEGORY_PROPERTY_NAME="creationDate";
	public static final String DESCRIPTION_CATEGORY_PROPERTY_NAME="description";
	public static final String LONG_DESCRIPTION_CATEGORY_PROPERTY_NAME="longDescription";
	public static final String SHOP_GUIDE_CATEGORY_PROPERTY_NAME="shopGuide";
	public static final String CAT_KEYWORDS_CATEGORY_PROPERTY_NAME="catKeywords";
	public static final String SITE_IDS_CATEGORY_PROPERTY_NAME="siteIds";
	public static final String DISPLAY_NAME_CATEGORY_PROPERTY_NAME="displayName";
	public static final String SMALL_IMAGE_CATEGORY_PROPERTY_NAME="smallImage";
	public static final String NODE_TYPE_CATEGORY_PROPERTY_NAME="type";
	public static final String IS_COLLEGE_CATEGORY_PROPERTY_NAME="college";
	public static final String FIXED_PARENT_CATEGORIES_PROPERTY_NAME="fixedParentCategories";
	public static final String PARENT_CATEGORIES_PROPERTY_NAME="parentCategories";
	public static final String COMMERCE_ROOT="Commerce Root";
	//shipping
	public static final String DEFAULT_SHIP_METHOD_PROPERTY_NAME="defaultShipMethod";
	public static final String SHIPPING_METHOD_PRICES_ITEM_DESCRIPTOR="shippingMethodPrices";
	public static final String SHIPPING_METHOD_ITEM_DESCRIPTOR="shippingMethods";
	public static final String SHIPPING_DURATIONS_ITEM_DESCRIPTOR="shippingDurations";
	public static final String SHIP_METHOD_CD="shipMethodCode";
	public static final String IS_MILITARY_STATE = "isMilitaryState";
	public static final String NO_SHOW_REGISTRY = "noShowOnRegistry";
	public static final String NO_SHOW_SHIPPING = "noShowOnShipping";
	public static final String NO_SHOW_BILLING = "noShowOnBilling";
	public static final String SHOW_ON_REGISTRY_PAGES = "showOnRegistry";
	public static final String SHOW_ON_SHIPPING_PAGES = "showOnShipping";
	public static final String SHOW_ON_BILLING_PAGES = "showOnBilling";
	

	public static final String PRICE_SHIPPING_PROPERTY_NAME="price";
	public static final String LOWER_LIMIT_SHIPPING_PROPERTY_NAME="lowerLimit";
	public static final String SHIPPING_METHOD_DESCRIPTION_SHIPPING_PROPERTY_NAME="shipMethodDescription";
	public static final String MAX_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME="maxDaysToShip";
	public static final String MIN_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME="minDaysToShip";
	// BPSI 1928 maxDaysToShipVDC attribute specific to VDC sku's 
	public static final String MAX_DAYS_TO_SHIP_VDC_PROPERTY_NAME="maxDaysToShipVDC";
	// BPSI 1928 minDaysToShipVDC attribute specific to VDC sku's 
	public static final String MIN_DAYS_TO_SHIP_VDC_PROPERTY_NAME="minDaysToShipVDC";
	public static final String SHIPPING_SURCHARGE_SKU_PROPERTY_NAME="shippingSurcharge";
	public static final String NON_SHIPPABLE_STATES_SHIPPING_PROPERTY_NAME="nonShippableStates";
	public static final String UPPER_LIMIT_SHIPPING_PROPERTY_NAME="upperLimit";
	public static final String STATE_ITEM_DESCRIPTOR="state";
	public static final String STATE_DESC_PROPERTY_NAME="descrip";
	public static final String CUT_OFF_TIME_SHIPPING_PROPERTY_NAME="cutOffTime";

	public static final String ELIGIBLE_SHIP_METHODS_PROPERTY_NAME="eligibleShipMethods";
	public static final String PAYMENT_CARDS_PROPERTY_NAME="paymentCards";
	//state

	public static final String STATES_ITEM_DESCRIPTOR="state";

	//credit card
	public static final String DESCRIPTION_STATE_PROPERTY_NAME="descrip";
	public static final String CARD_NAME_CREDIT_CARD_PROPERTY_NAME="cardName";
	public static final String CARD_CODE_CREDIT_CARD_PROPERTY_NAME="cardCode";
	public static final String CARD_IMAGE_CREDIT_CARD_PROPERTY_NAME="cardImage";
	public static final String CARD_PATTERN_CREDIT_CARD_PROPERTY_NAME="cardPattern";



	//site
	public static final String SITE_ITEM_DESCRIPTOR="siteConfiguration";
	public static final String APPLICABLE_SHIPMETHODS_SITE_PROPERTY_NAME="applicableShipMethods";
	public static final String REGISTRY_TYPES_SITE_PROPERTY_NAME="registryTypes";
	public static final String NEXUS_STATE_SITE_PROPERTY_NAME="nexusStates";
	public static final String BOPUS_EXCLUDED_STATE_SITE_PROPERTY_NAME="bopusExcludedStates";
	public static final String GIFT_WRAP_PRICE_SITE_PROPERTY_NAME="giftWrapPrice";
	public static final String GIFT_WRAP_SKU_SITE_PROPERTY_NAME="giftWrapSku";
	public static final String GIFT_WRAP_PRODUCT_SITE_PROPERTY_NAME="giftWrapProduct";
	public static final String COMMON_GREETINGS_SITE_PROPERTY_NAME="commonGreetings";
	public static final String PACK_AND_HOLD_SITE_PROPERTY_NAME="packAndHold";
	public static final String PACK_AND_HOLD_START_DATE_SITE_PROPERTY_NAME="packAndHoldStartDate";
	public static final String PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME="packAndHoldEndDate";
	public static final String SITE_TAG_SITE_PROPERTY_NAME="siteTag";
	public static final String DEFAULT_COUNTRY_SITE_PROPERTY_NAME="defaultCountry";
	public static final String SITE_NAME_SITE_PROPERTY_NAME="name";
	public static final String SITE_ATTRIBUTE_VALUES_LIST="attributeValue";
	// TBS Site attrs
	public static final String TBS_ITEM_OVERRIDE_THRESHOLD="itemOverrideThreshold";
	public static final String TBS_SHIPPING_OVERRIDE_THRESHOLD="shippingOverrideThreshold";
	public static final String TBS_SURCHARGE_OVERRIDE_THRESHOLD="surchargeOverrideThreshold";
	public static final String TBS_TAX_OVERRIDE_THRESHOLD="taxOverrideThreshold";
	public static final String TBS_GIFTWRAP_OVERRIDE_THRESHOLD="giftWrapOverrideThreshold";
	public static final String TBS_ASSEMBLY_OVERRIDE_THRESHOLD="assemblyOverrideThreshold";
	public static final String TBS_DELIVERY_OVERRIDE_THRESHOLD="deliveryOverrideThreshold";
	
	public static final String BBB_HOLIDAYS_SITE_PROPERTY_NAME="bbbholidays";
	public static final String BBB_WEEKENDS_SITE_PROPERTY_NAME="bbbweekends";
	public static final String TBS_US_SITE="TBS_BedBathUS";
	public static final String TBS_BABY_SITE="TBS_BuyBuyBaby";
	public static final String TBS_CA_SITE="TBS_BedBathCanada";
	public static final String SIX_TO_EIGHT_WEEKS="0005";
	public static final int FIFTY_SIX= 56;
	public static final int FOURTY_TWO= 42;
	
	//product
	public static final String PRODUCT_ITEM_DESCRIPTOR="product";
	public static final String PRODUCT_TYPE_PRODUCT_PROPERTY_NAME="type";
	public static final String SHOW_IMAGES_IN_COLLECTION_PRODUCT_PROPERTY_NAME="showImagesInCollection";
	public static final String LEAD_PRODUCT_PRODUCT_PROPERTY_NAME="leadPrd";
	public static final String PRODUCT_ID_PRODUCT_PROPERTY_NAME="productId";
	public static final String ROLLUP_TYPES_PRODUCT_PROPERTY_NAME="prodRollupType";
	public static final String SKU_ATTRIBUTE_PRODUCT_PROPERTY_NAME="skuAttributes";
	public static final String PRODUCT_TABS_PRODUCT_PROPERTY_NAME="productTabs";
	public static final String PRODUCT_ROLL_UP_PRODUCT_PROPERTY_NAME=	"prodRollupType";
	public static final String START_DATE_PRODUCT_PROPERTY_NAME="startDate";
	public static final String END_DATE_PRODUCT_PROPERTY_NAME="endDate";
	public static final String COLLECTION_PRODUCT_PROPERTY_NAME="collection";
	public static final String PRICE_RANGE_PRODUCT_PROPERTY_NAME="priceRangeDescrip";
	public static final String EMAIL_OUT_OF_STOCK_PRODUCT_PROPERTY_NAME="emailOutOfStock";
	public static final String CLEARANCE_PRODUCT_PROPERTY_NAME="isClearance";
	public static final String CHILD_SKU_PRODUCT_PROPERTY_NAME="childSKUs";
	public static final String DISPLAY_NAME_PRODUCT_PROPERTY_NAME="displayName";
	public static final String DESCRIPTION_PRODUCT_PROPERTY_NAME="description";
	public static final String LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME="longDescription";
	public static final String CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME="productChildProducts";
	public static final String THUMB_NAIL_PATH_PRODUCT_PROPERTY_NAME="thumbnailImagePath";
	public static final String SMALL_IMAGE_PATH_PRODUCT_PROPERTY_NAME="smallImagePath";
	public static final String LARGE_IMAGE_PATH_PRODUCT_PROPERTY_NAME="largeImagePath";
	public static final String MEDIA_ZOOM_IMAGE_PRODUCT_PROPERTY_NAME="mediaZoomImage";
	public static final String ZOOM_INDEX_PATH_PRODUCT_PROPERTY_NAME="zoomIndex";
	public static final String ANYWHERE_ZOOM_PRODUCT_PROPERTY_NAME="anywhereZoom";
	public static final String COLLECTION_THUMBNAIL_PRODUCT_PROPERTY_NAME="collectionThumbnail";
	public static final String GIFT_CERTIFICATE_PRODUCT_PROPERTY_NAME="giftCertProduct";
	public static final String DISABLE_PRODUCT_PROPERTY_NAME="prodDisable";
	public static final String SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME="skuLowPrice";
	public static final String SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME="skuHighPrice";
	public static final String WEB_OFFERED_PRODUCT_PROPERTY_NAME="webOffered";
	public static final String SITE_PRODUCT_PROPERTY_NAME="site";
	public static final String LIKE_UNLIKE_COLL_PRODUCT_PROPERTY_NAME="likeUnlike";
	public static final String ROLLUP_ATTR_COLL_PRODUCT_PROPERTY_NAME="rollupAttribute";
	public static final String COLL_ROLLUP_TYPE_COLL_PRODUCT_PROPERTY_NAME="collectionRollupType";
	public static final String KEYWRDS_PRODUCT_PROPERTY_NAME="keywords";
	public static final String CREATION_DATE_PRODUCT_PROPERTY_NAME="creationDate";
	public static final String ENABLE_DATE_PRODUCT_PROPERTY_NAME="enableDate";
	public static final String COLLEGE_PRODUCT_PROPERTY_NAME="college";
	public static final String SWATCH_PRODUCT_PROPERTY_NAME="swatch";
	public static final String PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME="parentProducts";	
	public static final String PRODUCT_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME = "productAttributeRelns";
	public static final String SITE_ID_PROD_RELATION_PROPERTY_NAME=	"siteId";
	public static final String START_DATE_PROD_RELATION_PROPERTY_NAME="startDate";
	public static final String END_DATE_PROD_RELATION_PROPERTY_NAME="endDate";
	public static final String SEO_URL_PROD_RELATION_PROPERTY_NAME="seoUrl";
	public static final String MEDIA_RELN_PRODUCT_PROPERTY_NAME="mediaReln";
	public static final String RESTRICTED_ZIP_CODES = "restrictedZipCodes";
	public static final String PRIMARY_PARENT_CATEGORY="primaryCategoryId";                        //   added as part of release 2.1 scope#29
	public static final String PRODUCT_SHOP_GUIDE_ID = "shopGuide";
	public static final String GUIDES = "guides";
	public static final String GUIDES_SITES = "site";
	public static final String GUIDE_ID = "id";
	public static final String BBB_BRANDS="bbbBrand";
	public static final String PRODUCT_KEYWORDS= "prdKeywords";
	public static final String VENDOR_ID_PRODUCT_PROPERTY = "vendorId";
	public static final String VERTICAL_IMAGE_PRODUCT_PROPERTY = "verticalImage";
	
	
	//Ever living pdp properties
	public static final String DISABLE_FOREVER_PDP_FLAG="disableForeverPDPFlag";
	public static final String BAB_DISABLE_FOREVER_PDP_FLAG="babDisableForeverPDPFlag";
	public static final String CA_DISABLE_FOREVER_PDP_FLAG="caDisableForeverPDPFlag";
	public static final String GS_DISABLE_FOREVER_PDP_FLAG="gsDisableForeverPDPFlag";
	
	//Product Other media 
	public static final String MEDIA_TRANSCRIPT_OTHER_MEDIA_PROPERTY_NAME="mediaTranscript";
	public static final String PROVIDER_OTHER_MEDIA_PROPERTY_NAME="provider";
	public static final String MEDIA_DESCRIPTION_OTHER_MEDIA_PROPERTY_NAME="mediaDescription";
	public static final String MEDIA_SOURCE_OTHER_MEDIA_PROPERTY_NAME="mediaSource";
	public static final String MEDIA_TYPE_OTHER_MEDIA_PROPERTY_NAME="mediaType";
	public static final String COMMENTS_OTHER_MEDIA_PROPERTY_NAME="comments" ;
	public static final String SITES_OTHER_MEDIA_PROPERTY_NAME="sites";
	public static final String START_DATE_OTHER_MEDIA_PROPERTY_NAME="startDate";
	public static final String END_DATE_OTHER_MEDIA_PROPERTY_NAME="endDate";
	

	//site translation
	public static final String ATTRIBUTE_NAME_SITE_TRANS_PROPERTY_NAME="attributeName";
	public static final String ATTRIBUTE_VALUE_STRING_SITE_TRANS_PROPERTY_NAME="attributeValueString";
	public static final String ATTRIBUTE_VALUE_BOOLEAN_SITE_TRANS_PROPERTY_NAME="attributeValueBoolean";
	public static final String ATTRIBUTE_VALUE_DATE_SITE_TRANS_PROPERTY_NAME="attributeValueDate";
	public static final String PROD_TRANS_SITE_TRANS_PROPERTY_NAME="prdTranslations";

	//attribute
	public static final String DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME="displayDescrip";
	public static final String PRIORITY_ATTRIBUTE_PROPERTY_NAME="priority";
	public static final String IMAGE_URL_ATTRIBUTE_PROPERTY_NAME="imageURL";
	public static final String ACTION_URL_ATTRIBUTE_PROPERTY_NAME="actionURL";
	public static final String PLACE_HOLDER_ATTRIBUTE_PROPERTY_NAME="placeHolder";
	public static final String START_DATE_ATTRIBUTE_PROPERTY_NAME="startDate";
	public static final String END_DATE_ATTRIBUTE_PROPERTY_NAME="endDate";



	//image constants
	public static final String LARGE_IMAGE_IMAGE_PROPERTY_NAME="largeImage";
	public static final String MEDIUM_IMAGE_IMAGE_PROPERTY_NAME="mediumImage";
	public static final String SMALL_IMAGE_IMAGE_PROPERTY_NAME="smallImage";
	public static final String REGULAR_IMAGE_IMAGE_PROPERTY_NAME="regularImage";
	public static final String SWATCH_IMAGE_IMAGE_PROPERTY_NAME="swatchImage";
	public static final String ZOOM_IMAGE_IMAGE_PROPERTY_NAME="zoomImage";
	public static final String ZOOM_INDEX_IMAGE_PROPERTY_NAME="zoomIndex";
	public static final String IMAGE_URL_PROPERTY_NAME="url";
	public static final String ANYWHERE_ZOOM_IMAGE_PROPERTY_NAME="anywhereZoom";
	public static final String THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME="thumbnailImage";

	//roll up constants
	public static final String ROLLUP_ATTRIBUTE_ROLLUP_PROPERTY_NAME=	"rollupAttribute";

	//brand
	public static final String BRAND_NAME_BRAND_PROPERTY_NAME="brandName";
	public static final String BRANDS_ITEM_ID="id";
	public static final String BRAND_IMAGE_BRAND_PROPERTY_NAME="brandImage";
	public static final String BRAND_DESCRIPTION_BRAND_PROPERTY_NAME="brandDescrip";
	public static final String BRANDS_ITEM_DESCRIPTOR="bbbBrand";
	public static final String SITES_BRAND_PROPERTY_NAME="sites";
	public static final String DISPLAY_BRAND_PROPERTY_NAME="display";

	//Rebate
	public static final String REBATES_ITEM_DESCRIPTOR="rebates";
	public static final String REBATE_URL_REBATE_PROPERTY_NAME="rebateURL";
	public static final String DESCRIPTION_REBATE_PROPERTY_NAME="descrip";
	public static final String SITES_REBATE_PROPERTY_NAME="sites";
	public static final String START_DATE_REBATE_PROPERTY_NAME="startDate";
	public static final String END_DATE_REBATE_PROPERTY_NAME="endDate";

	//SKU
	public static final String SIZE_SKU_PROPERTY_NAME="size";
	public static final String COLOR_SURCHARGE_SKU_PROPERTY_NAME="color";
	public static final String VDCSKU_TYPE_SKU_PROPERTY_NAME="vdcSkuType";
	public static final String GIFT_WRAP_ELIGIBLE_SKU_PROPERTY_NAME="giftWrapEligible";
	public static final String LONG_DESCRIPTION_SKU_PROPERTY_NAME="description";
	public static final String SKU_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME="skuAttributeRelns";
	public static final String FREE_SHIPPING_METHOD_SKU_PROPERTY_NAME="freeShipMethods";
	public static final String START_DATE_SKU_PROPERTY_NAME="startDate";
	public static final String END_DATE_SKU_PROPERTY_NAME="endDate";
	public static final String GIFT_CERT_SKU_PROPERTY_NAME="giftCert";
	public static final String FORCE_BELOW_LINE_SKU_PROPERTY_NAME="forceBelowLine";
	public static final String ECOM_FULFILLMENT_SKU_PROPERTY_NAME="eComFulfillment";
	public static final String JDA_DEPT_SKU_PROPERTY_NAME="jdaDept";
	public static final String JDA_SUBDEPT_SKU_PROPERTY_NAME="jdaSubDept";
	public static final String JDA_CLASS_SKU_PROPERTY_NAME="jdaClass";
	public static final String DISPLAY_NAME_SKU_PROPERTY_NAME="displayName";
	public static final String SWATCH_IMAGE_SKU_PROPERTY_NAME="swatchImagePath";
	public static final String ON_SALE_SKU_PROPERTY_NAME="onSale";
	public static final String EMAIL_OUT_OF_STOCK_SKU_PROPERTY_NAME="emailOutOfStock";
	public static final String ECO_FEE_SKU_RELN_PROPERTY_NAME="ecoFeeSKUs";
	public static final String PARENT_PRODUCT_PROPERTY_NAME="parentProducts";
	public static final String VENDOR_ID_SKU_PROPERTY_NAME="vendorId";
	public static final String SITE_IDS_SKU_PROPERTY_NAME="siteIds";
	public static final String UPC_SKU_PROPERTY_NAME="upc";
	public static final String BOPUS_EXCLUSION_SKU_PROPERTY_NAME="bopusExclusion";
	public static final String VDC_SKU_MESSAGE_SKU_PROPERTY_NAME="vdcSkuMessage";
	public static final String IS_STORE_SKU_SKU_PROPERTY_NAME="storeSKU";
	public static final String ENABLE_DATE_SKU_PROPERTY_NAME="enableDate";
	public static final String LARGE_IMAGE_PROPERTY_NAME="scene7URL";
	public static final String THUMBNAIL_IMAGE_PROPERTY_NAME="thumbnailImage";
	public static final String SMALL_IMAGE_PROPERTY_NAME="smallImage";
	public static final String JDA_SUB_CAT = "jdaSubCat";
	public static final String WEB_ONLY = "webOnly";
	public static final String SKU_UPC_RQL = "id=?0 OR ((upc=?0 and siteIds is null) OR (upc=?0 and not(siteIds is null)))";
	
	//SKu relation
	public static final String START_DATE_SKU_RELATION_PROPERTY_NAME="startDate";
	public static final String END_DATE_SKU_RELATION_PROPERTY_NAME="endDate";
	public static final String SITE_ID_SKU_RELATION_PROPERTY_NAME=	"siteId";
	public static final String SITE_IDS_SKU_RELATION_PROPERTY_NAME ="siteIds";
	public static final String SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME="skuAttribute";
	//tabs
	public static final String SITES_TABS_PROPERTY_NAME=	"sites";
	public static final String TAB_NAME_TABS_PROPERTY_NAME="tabName";
	public static final String TAB_CONTENT_TABS_PROPERTY_NAME="tabContent";

	//siteSKu
	public static final String SITE_SKUSHIPPING_PROPERTY_NAME="site";
	public static final String SHIPPING_METHOD_CODE_SKUSHIPPING_PROPERTY_NAME="shipMethodCode";

	public static final String CHILD_PRODUCTS_PRODUCT_RELATION_PROPERTY_NAME="productId";
	public static final String COLLECTION_ROLL_UP_PRODUCT_RELATION_PROPERTY_NAME="collectionRollupType";

	//Registry Type
	public static final String REGISTRY_TYPE_ITEM_DESCRIPTOR=	"registrytype";
	public static final String REGISTRY_TYPE_ID_REGISTRY_PROPERTY_NAME=	"registryTypeId";
	public static final String REGISTRY_TYPE_NAME_REGISTRY_PROPERTY_NAME="registryTypeName";
	public static final String REGISTRY_TYPE_NAME_REGISTRY_PROPERTY_CODE="registryTypeCode";
	public static final String REGISTRY_TYPE_DESC_REGISTRY_PROPERTY_NAME="registryTypeDesc";
	public static final String REGISTRY_TYPE_INDEX_REGISTRY_PROPERTY_NAME="registryTypeIndex";

	//configKeys
	public static final String IDN_CONFIGKEYS_RQL_GET_CONFIGKEYS_BY_CONFIGTYPE = "getConfigValueByConfigType";
	public static final String IDN_CONFIGKEYS = "configKeys";
	public static final String IPN_CONFIGKEYS_CONFIGVALUE = "configValue";
	public static final String DELIMITER = "::";
	public static final String DELIMITERCOMMA = ",";
	public static final String CONFIG_KEY_VALUE_PROPERTY = "configKeyValue";
	public static final String CONFIG_KEY_PROPERTY = "configKey";
	public static final String CONFIG_VALUE_PROPERTY = "configValue";

	//thresholds
	public static final String SKU_THRESHOLD_ITEM_DESCRIPTOR=	"skuThresholds";
	public static final String THRESHOLD_LIMITED_THRESHOLD_PROPERTY_NAME="thresholdLimited";
	public static final String THRESHOLD_AVAILABLE_THRESHOLD_PROPERTY_NAME="thresholdAvailable";

	//promotion and price

	public static final String PROMOTION_ITEM_DESCRIPTOR="promotion";
	public static final String PRICE_LIST_ITEM_DESCRIPTOR="priceList";
	public static final String SALE_PRICE_LIST_PRICING_PROPERTY_NAME="salePriceList";
	public static final String LIST_PRICE_PRICING_PROPERTY_NAME="listPrice";

	//inventory
	public static final String INVENTORY_ITEM_DESCRIPTOR="inventory";
	public static final String CACHED_INVENTORY_ITEM_DESCRIPTOR="cachedInventory";
	public static final String SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME="siteStockLevelDefault";
	public static final String TRANSLATIONS_SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME="siteStockLevel";
	public static final String STOCK_LEVEL_INVENTORY_PROPERTY_NAME="stockLevel";
	public static final String REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME="registryStockLevelDefault";
	public static final String TRANSLATIONS_REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME="registryStockLevel";
	public static final String START_DATE_INVENTORY_PROPERTY_NAME="startDate";
	public static final String END_DATE_INVENTORY_PROPERTY_NAME="endDate";
	public static final String DISPLAY_NAME_INVENTORY_PROPERTY_NAME="displayName";
	public static final String CREATION_DATE_INVENTORY_PROPERTY_NAME="creationDate";
	public static final String DESCRIPTION_INVENTORY_PROPERTY_NAME="description";
	public static final String CATALOG_REF_ID_INVENTORY_PROPERTY_NAME="catalogRefId";
	public static final String AVAILABILITY_DATE_INVENTORY_PROPERTY_NAME="availabilityDate";
	public static final String TRANSLATIONS_INVENTORY_PROPERTY_NAME="translations";


	//school
	public static final String SCHOOLS_ITEM_DESCRIPTOR="schools";
	public static final String SCHOOLS_VER_ITEM_DESCRIPTOR="schoolsVer";
	public static final String SCHOOL_NAME_SCHOOL_PROPERTY_NAME="schoolName";
	public static final String SMALL_LOGO_URL_SCHOOL_PROPERTY_NAME ="smallLogoURL";
	public static final String LARGE_LOGO_URL_SCHOOL_PROPERTY_NAME="largeLogoURL";
	public static final String SMALL_WELCOME_MSG_SCHOOL_PROPERTY_NAME="smallWelcomeMsg";
	public static final String LARGE_WELCOME_MSG_SCHOOL_PROPERTY_NAME="largeWelcomeMsg";
	public static final String PROMOTION_ID_SCHOOL_PROPERTY_NAME="promotionID";
	public static final String ADDRESS_LINE1_SCHOOL_PROPERTY_NAME="addrLine1";
	public static final String ADDRESS_LINE2_SCHOOL_PROPERTY_NAME="addrLine2";
	public static final String CITY_SCHOOL_PROPERTY_NAME="city";
	public static final String STATE_SCHOOL_PROPERTY_NAME="state";
	public static final String ZIP_SCHOOL_PROPERTY_NAME="zip";
	public static final String SCHOOLS_ITEM_PROPERTY_NAME="schools";
	public static final String IMAGE_URL_SCHOOL_PROPERTY_NAME="imageURL";
	public static final String PREF_STORE_ID_SCHOOL_PROPERTY_NAME="prefStoreId";
	public static final String PDF_URL_SCHOOL_PROPERTY_NAME="pdfURL";
	public static final String COLLEGE_TAG_SCHOOL_PROPERTY_NAME="collegeTag";
	public static final String COLLEGE_LOGO_SCHOOL_PROPERTY_NAME="collegeLogo";
	public static final String  CREATION_DATE_SCHOOL_PROPERTY_NAME="creationDate";
	public static final String LAST_MODIFIED_DATE_SCHOOL_PROPERTY_NAME="lastModifiedDate";
	public static final String  ID="id";
	public static final String  PROMOTION_DESCRIPTION="description";
	public static final String  SCHOOL_SEO_NAME_SCHOOL_PROPERTY_NAME="schoolSeoName";
	
	//bazaar voice
	public static final String BAZARVOICE_API_NAME="getBazaarVoiceDetails";
	public static final String BAZAAR_VOICE = "bazaarVoice";
	public static final String TOTAL_REVIEW_COUNT = "TotalReviewCount";
	public static final String EXTERNAL_ID = "ExternalId";
	public static final String AVERAGE_OVERALL_RATING = "AverageOverallRating";
	public static final String PRODUCT_ID = "productId";
	public static final String SITE_ID="siteId";
	
	// sales data
	public static final String SALES_DATA="salesData";
	
	// bedding kit ship address
	
	public static final String ADDRESS_LINE1_BEDDING_PROPERTY_NAME="addrLine1";
	public static final String ADDRESS_LINE2_BEDDING_PROPERTY_NAME="addrLine2";
	public static final String CITY_BEDDING_PROPERTY_NAME="city";
	public static final String STATE_BEDDING_PROPERTY_NAME="state";
	public static final String ZIP_BEDDING_PROPERTY_NAME="zip";
	public static final String SHIPPING_START_DATE="shippingStartDate";
	public static final String SHIPPING_END_DATE="shippingEndDate";
	public static final String COMPANY_NAME="companyName";
	public static final String COLLEGE_NAME="schoolName";
	public static final String BEDDING_KIT_ATTRIBUTE="beddingKitAttribute";
	public static final String DATE_FORMAT = "dd-MMM-yyyy";
	public static final String NO_OF_DAYS = "noOfDays";
	
	//Store
	public static final String STORE_ITEM_DESCRIPTOR="store";
	public static final String STATE_PROPERTY_NAME="state";
	public static final String STORE_NAME_STORE_PROPERTY_NAME="storeName";
	public static final String ADDRESS_STORE_PROPERTY_NAME="address";
	public static final String CITY_STORE_PROPERTY_NAME="city";
	public static final String PROVINCE_STORE_PROPERTY_NAME="province";
	public static final String POSTAL_CODE_STORE_PROPERTY_NAME="postalCode";
	public static final String COUNTRYCODE_STORE_PROPERTY_NAME="countryCode";
	public static final String PHONE_STORE_PROPERTY_NAME="phone";
	public static final String LONGITUDE_STORE_PROPERTY_NAME="longitude";
	public static final String LATITUDE_STORE_PROPERTY_NAME="latitude";
	public static final String STORE_TYPE_STORE_PROPERTY_NAME="storeType";
	public static final String HOURS_STORE_PROPERTY_NAME="hours";
	public static final String LAT_LONG_SRC_STORE_PROPERTY_NAME="latLongSrc";
	public static final String ROW_XNGDT_STORE_PROPERTY_NAME="rowXngDt";
	public static final String ROW_XNGUSER_STORE_PROPERTY_NAME="rowXngUser";
	public static final String HIRINGIND_STORE_PROPERTY_NAME="hiringInd";
	public static final String FACADE_STORE_TYPE_STORE_PROPERTY_NAME="facadeStoreType";
	public static final String COMMON_NAME_PHONETIC_STORE_PROPERTY_NAME="commonNamePhonetic";
	public static final String ADDRESS_PHONETIC_STORE_PROPERTY_NAME="addressPhonetic";
	public static final String CITY_PHONETIC_STORE_PROPERTY_NAME="cityPhonetic";
	public static final String MQ_TRANS_CODE_STORE_PROPERTY_NAME="mqTransCode";
	public static final String DISPLAY_ONLINE_STORE_PROPERTY_NAME="displayOnline";
	public static final String MON_OPEN_STORE_PROPERTY_NAME="monOpen";
	public static final String MON_CLOSE_STORE_PROPERTY_NAME="monClose";
	public static final String TUES_OPEN_STORE_PROPERTY_NAME="tuesOpen";
	public static final String TUES_CLOSE_STORE_PROPERTY_NAME="tuesClose";
	public static final String WED_OPEN_STORE_PROPERTY_NAME="wedOpen";
	public static final String WED_CLOSE_STORE_PROPERTY_NAME="wedClose";
	public static final String THUR_OPEN_STORE_PROPERTY_NAME="thursOpen";
	public static final String THUR_CLOSE_STORE_PROPERTY_NAME="thursClose";
	public static final String FRI_OPEN_STORE_PROPERTY_NAME="friOpen";
	public static final String FRI_CLOSE_STORE_PROPERTY_NAME="friClose";
	public static final String SAT_OPEN_STORE_PROPERTY_NAME="satOpen";
	public static final String SAT_CLOSE_STORE_PROPERTY_NAME="satClose";
	public static final String SUN_OPEN_STORE_PROPERTY_NAME="sunOpen";
	public static final String SUN_CLOSE_STORE_PROPERTY_NAME="sunClose";
	public static final String SPECIAL_MSG_STORE_PROPERTY_NAME="specialMsg";
	public static final String CONTACT_FLAG_STORE_PROPERTY_NAME="contactFlag";
	public static final String SPECIALITY_CODE_ID_STORE_PROPERTY_NAME="specialityCodeId";
	public static final String SPECIALITY_CODE_NAME_STORE_PROPERTY_NAME="specialityCodeName";
	public static final String CODE_IMAGE_STORE_PROPERTY_NAME="codeImage";
	public static final String PRIORITY_STORE_PROPERTY_NAME="priority";
	public static final String STORE_LIST_ALT_TEXT_STORE_PROPERTY_NAME="storeListAltText";
	public static final String STORE_LIST_TITLE_TEXT_STORE_PROPERTY_NAME="storeListTitleText";
	public static final String IMG_LOC_STORE_PROPERTY_NAME="img2ImgLoc";
	public static final String IMG_ALT_TEXT_STORE_PROPERTY_NAME="img2AltText";
	public static final String IMG_TITLE_TEXT_STORE_PROPERTY_NAME="img2TitleText";
	public static final String LEGEND_FILE_LOC_STORE_PROPERTY_NAME="legendFileLoc";
	public static final String LEGEND_ALT_TEXT_STORE_PROPERTY_NAME="legendAltText";
	public static final String LEGEND_TITLE_TEXT_STORE_PROPERTY_NAME="legendTitleText";
	public static final String BOPUS_STORE_PROPERTY_NAME="bopus";
	public static final String STORE_ACCEPTING_APPOITMENTS_PROPERTY_NAME="acceptingAppointments";
	public static final String STORE_REGISTRY_APPOITMENT_TYPES_PROPERTY_NAME="regAppointmentTypes";	
	public static final String STORE_APPOITMENT_TYPES_PROPERTY_NAME="appointmentTypes";
	public static final String STORE_APPOITMENTS_LAST_MODIFIED_DATE_PROPERTY_NAME="lastModifiedDate";	

	public static final String BILLING_ADDRESS_COUNTRY_ITEM_DESCRIPTOR="countries";
	public static final String INTL_BILLING_ADDRESS_COUNTRY_ITEM_DESCRIPTOR="bccManagedCountry";
	
	public static final String COUNTRY_NAME_PROPERTY="countryName";
	public static final String COUNTRY_CODE_PROPERTY="countryCode";
	public static final String IS_ENABLED_PROPERTY="enabled";
	public static final String STORE_ID_FOR_BABYCANADA="3";
	

	//EcoFeeSku
	public static final String ECO_FEE_ITEM_DESCRIPTOR="bbbEcoFeeSKU";
	public static final String STATE_ECO_FEE_PROPERTY_NAME="state";
	public static final String SKU_ECO_FEE_PROPERTY_NAME="ecoFeeSKU";

//coupoN rules
	public static final String COUPON_RULES_ITEM_DESCRIPTOR="couponRules";
	public static final String COUPON_RULES_SKU_ID="skuId";
	public static final String COUPON_RULES_VENDOR_ID="vendorId";
	public static final String COUPON_RULES_DEPT_ID="jdaDeptId";
	public static final String COUPON_RULES_SUB_DEPT_ID="jdaSubDeptId";
	public static final String COUPON_RULES_CLASS="jdaClass";

// registry mapping with catalog repository
	public static final String REGISTRY_CATEGORY_ITEM_DESCRIPTOR=	"regCatGrp";
	public static final String REGISTRY_PRICE_BUCKET_ITEM_DESCRIPTOR="regPriceGrp";
	public static final String REGISTRY_PRICE_BUCKET_ITEM_DESCRIPTOR_MX="regPriceGrpMX";
	public static final String CATEGORY_NAME_REG_CAT_PROPERTY_NAME="catName";
	public static final String REGISTRY_TYPE_CATEGORY_ITEM_DESCRIPTOR="registryMap";
	public static final String REGISTRY_CAT_REG_TYPE_PROPERTY_NAME="regCat";
	public static final String ADD_ITEM_FLAG_REG_CAT_PROPERTY_NAME="addItemFlag";
	public static final String CATEGORY_ID_REG_CAT_PROPERTY_NAME="catId";
	public static final String MIN_PRICE_REG_PRICE_PROPERTY_NAME="minPrice";
	public static final String MAX_PRICE_REG_PRICE_PROPERTY_NAME="maxPrice";
	public static final String PRICE_RANGE_REG_PRICE_PROPERTY_NAME="priceRange";
	public static final String CATEGORY_SORT_TYPE="category";
	public static final String REG_CAT_TYPE_REG_CAT_PROPERTY_NAME="registryCatTypes";
	public static final String REG_PROMO_BOX_PROPERTY_NAME="promoBox";
	
	//chat attributes

	public static final String CHAT_URL_PROPERTY_NAME = "chatURL";
	public static final String CHAT_GLOBAL_PROPERTY_NAME = "onOffFlag";
	public static final String CHAT_PDP_PROPERTY_NAME = "chatFlagPDP";
	public static final String CHAT_PDP_OVERRIDE_PROPERTY_NAME = "chatOverrideFlagPDP";
	public static final String DAAS_PDP_PROPERTY_NAME = "daasFlagPDP";
	public static final String DAAS_PDP_OVERRIDE_PROPERTY_NAME = "daasOverrideFlagPDP";
	public static final String CHAT_WEEKDAY_OPEN_TIME_PROPERTY_NAME = "weekdayOpenTime";
	public static final String CHAT_WEEKDAY_CLOSE_TIME_PROPERTY_NAME = "weekdayCloseTime";
	public static final String CHAT_WEEKEND_OPEN_TIME_PROPERTY_NAME = "weekendOpenTime";
	public static final String CHAT_WEEKEND_CLOSE_TIME_PROPERTY_NAME = "weekendCloseTime";
	public static final String TIMEZONES_PROPERTY_NAME="timeZones";
	public static final String TIMEZONE_PROPERTY_NAME="timeZone";

	//prop65 
	public static final String PROP65_OTHER = "prop65Other";
	public static final String PROP65_DINNERWARE = "prop65Dinnerware";
	public static final String PROP65_CRYSTAL = "prop65Crystal";
	public static final String PROP65_LIGHTING = "prop65Lighting";
	
	//third party tag status  
	public static final String UNDERSCORE = "_";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String US = "us";
	public static final String BABY = "baby";
	public static final String CA = "ca";
	public static final String BED_BATH_US_SITE_CODE = "BedBathUSSiteCode";
	public static final String BUY_BUY_BABY_SITE_CODE = "BuyBuyBabySiteCode";
	public static final String BED_BATH_CANADA_SITE_CODE = "BedBathCanadaSiteCode";
	public static final String TBS_BED_BATH_US_SITE_CODE = "TBS_BedBathUSSiteCode";
	public static final String TBS_BUY_BUY_BABY_SITE_CODE = "TBS_BuyBuyBabySiteCode";
	
	
	//JDA Dept Constants
	public static final String DESCRIPTION_JDA_PROPERTY_NAME="descrip";
	public static final String CLASS_ITEM_DESCRIPTOR	=	"class";	
	
	//college Config
	public static final String BED_BATH_US = "BedBathUS"; 

	//Sku-Region Relation
	public static final String ZIP_CODE_SAPERATER = "zipCodeSaperater";
	public static final String SKU_ZIP_CODE_SITE = "skuZipCodeSite";
	public static final String RESTRICTED_SKU_ZIP_CODES = "restrictedZipCodes";
	public static final String SITE_SKU_REGIONS = "siteSkuRegions";
	public static final String REGION_NAME = "regionName";
	
	public static final String PRICE_ATTRIBUTE = "price";
	public static final String PRICE_LIST_ATTRIBUTE = "priceList";
	public static final String SKU_ATTRIBUTE = "sku";
	
	public static final String VDC_ATTRIBUTES_LIST = "vdcAttributesList";
	public static final String ATTRIBUTE_INFO = "attributeInfo";
	public static final String CHAT_ENABLED="chatEnabled";
	public static final String CHAT_URL="chatURL";
	public static final String L2_EXCLUSION="l2ExclusionList";
	public static final String L3_EXCLUSION="l3ExclusionList";
	public static final String CHAT_CODE="chatCode";
	public static final String CHAT_LINK_PLACEHOLDER="chatLinkPlaceholder";
	public static final String CHAT_RQL_QUERY="categoryId=?0 and chatEnabled=?1";
	public static final String CHAT_VIEW="bccManagedCategory";
	public static final String BCC_MANAGED_CATEGORY="bccManagedCategory";
	public static final String CATEGORY_PROMO_ID="catPromoId";

	// Page Ordering Template
	public static final String PAGE_ORDER_ITEM_DESCRIPTOR = "pageTabs";
	public static final String TABS_ITEM_DESCRIPTOR = "tabsDetail";
	public static final String PAGE_NAME_PROPERTY = "pageName";
	public static final String TABS_LIST_PROPERTY = "tabDetails";
	public static final String TAB_NAME_PROPERTY = "tabName";
	public static final String PHANTOM_CATEGORY = "phantomCategory";
	//R2 -scope 81
	public static final String PRODUCT_CHILD_PRODUCTS = "productChildProducts";
	public static final String COLLECTION = "collection";
	public static final String LEAD_PRD = "leadPrd";

	//R2 -scope 153
	public static final String CHILD_SKUS = "childSKUs";
	public static final String DECIMAL_FORMAT = "00.00";
	public static final String PRODUCT_DESC = "product";
	public static final String SITE_CONFIGURATION = "siteConfiguration";
	public static final String DEFAULT_PRICE_LIST = "defaultListPriceList";
	public static final String DEFAULT_SALE_PRICE_LIST = "defaultSalePriceList";
	public static final String ON_SALE = "onSale";
	public static final String COST_DEFAULT = "costDefault";
	public static final String MARGIN ="margin";
	public static final String SKU_TRANSLATIONS = "skuTranslations";
	public static final String SITE = "site";
	public static final String ATTRIBUTE_VALUE_STRING = "attributeValueString";
	public static final String ATTRIBUTE_NAME = "attributeName";
	public static final String LIST_PRICE = "listPrice";
	public static final String PRICE2 = "price";
	public static final String TIER ="tier";

	//R2.1 BV Additional Tag #216
	public static final String DISLPAY_ASK_AND_ANSWER ="displayAskAndAnswer";

	//R2.1 Sort Options
	public static final String SORTINGCODE ="sortingCode";
	public static final String SORTINGURLPARAM ="sortingUrlParam";
	public static final String SORTINGVALUE ="sortingValue";
	public static final String SORTINGORDER ="sortingOrder";
	public static final String SORTINGOPTIONS ="sortingOptions";
	public static final String DEFSITESORTOPTION ="defSiteSortOption";
	public static final String SITESORTOPTIONLIST ="siteSortOptionList";
	public static final String DEFCATSORTOPTION ="defCatSortOption";
	public static final String CATSORTOPTIONLIST ="catSortOptionList";
	public static final String DEFAULTSORTINGOPTION ="defaultsortingOption";
	public static final String DEFAULTSORTINGOPTIONS ="defaultsortingOptions";
	public static final String SORTINGOPTIONLIST ="sortingOptionList";
	public static final String SORTOPTION ="sortOption";
	
	public static final String MOBILE_LEGACYURL_BED_BATH_US = "mobile_legacy_url_bedbathUS";
	public static final String MOBILE_LEGACYURL_BUYBUYBABY =  "mobile_legacy_url_buybuybaby";
	public static final String MOBILE_LEGACYURL_BED_BATH_CA = "mobile_legacy_url_bedbathCA";
	
	public static final String MOBILE_LEGACY_ERR_URL_BED_BATH_US = "mobile_legacy_error_url_us";
	public static final String MOBILE_LEGACY_ERR_URL_BUYBUYBABY =  "mobile_legacy_error_url_baby";
	public static final String MOBILE_LEGACY_ERR_URL_BED_BATH_CA = "mobile_legacy_error_url_ca";
	
	public static final String SUNDAY = "SUNDAY";
	public static final String MONDAY = "MONDAY";
	public static final String TUESDAY = "TUESDAY";
	public static final String WEDNESDAY = "WEDNESDAY";
	public static final String THURSDAY = "THURSDAY";
	public static final String FRIDAY = "FRIDAY";
	public static final String SATURDAY = "SATURDAY";

	//R2.1.1 Chat and Display Ask and Answer Story
	public static final String CATEGORY_ID_BLANK ="";
	public static final String CRITERIA_FIRST ="FIRST";
	public static final String CRITERIA_ANY ="ANY";
	public static final String ASK_ANSWER_DEFAULT_VALUE="Hide";
	
	//R2.2 Grid list story |117-A5
	public static final String GRID_LIST_DEFAULT_VALUE="Blank";
	public static final String GRID_DEFAULT_VALUE="Grid";
	public static final String LIST_DEFAULT_VALUE="List";
	public static final String DISPLAY_DEFAULT_TYPE="DisplayType";
	
	
	//BBB_GRID_LIST_ATTRIBUTES
	
	public static final String GRID_LIST_PROPERTY_NAME = "onOffListGridFlag";
	
	//R2.1.1 Similar Product Ignore List
	public static final String SIMILAR_PRODUCT_IGNORE_LIST_PROPERTY_NAME ="similarProductIgnoreList";	
	public static final String SKU_NOT_PRESENT_IN_THE_CATALOG = "Sku Not present in the catalog";
	public static final String FINISH = "FINISH";
	public static final String SIZE3 = "SIZE";
	public static final String COLOR3 = "COLOR";
	public static final String FINISH_SIZE = "FINISH,SIZE";
	public static final String SIZE_FINISH = "SIZE,FINISH";
	public static final String COLOR_SIZE = "COLOR,SIZE";
	public static final String SIZE_COLOR = "SIZE,COLOR";
	public static final String SIZE2 = "size";
	public static final String COLOR2 = "color";
	public static final String NONE = "NONE";
	public static final String ROLLUP_ATTRIBUTE = "rollupAttribute";
	public static final String PROD_ROLLUP_TYPE = "prodRollupType";
	public static final String SALE_PRICE_PRICING_PROPERTY_NAME="salePrice";
	public static final String NO_PARENT_CATEGORY_FOR_PRODUCT_IN_REPOSITORY="No Parent Category present for product in repository";
	public static final String NO_CHILD_SKU_FOR_PRODUCT_IN_REPOSITORY="No child sku present for product in repository";
	public static final String SKU_TRANS_SITE_TRANS_PROPERTY_NAME="skuTranslations";
	public static final String SITE_SKU_PROPERTY_NAME="site";
	public static final String CATEGORY_UNAVAILABLE= "Error occured while retrieving  data for category from repository";
	public static final String INVALID_CATEGORY="categoryId is empty";
	public static final String INVALID_UPC_LIST="Invalid UPC List";
	public static final String INVALID_UPC = "UPC is Empty";
	public static final String INVALID_SCANNED_SKU= "Scanned sku not available in the system";
	public static final String ERROR_FETCHING_PRODUCT_ID= "Error fetching product Id";
	public static final String REPOSITORY_EXCEPTION ="Repository not configured";
	public static final String INACTIVE_CATEGORY="Category id is either invalid or inactive";
	public static final String DATA_UNAVAILABLE_IN_REPOSITORY= "Error occured while retrieving  data from repository";

	//R2.2 Story - 116-D1 & D2
	public static final String PER_PAGE_DROPDOWN = "per_page_dropdown";
	public static final String PER_PAGE_DEFAULT = "per_page_default";
	public static final String PER_PAGE_DEFAULT_ON_IPAD = "per_page_default_ipad";
	public static final String VIEW_ALL = "View All";
	public static final String COHERENCE_CACHES_ALL = "ALL";
	
	public static final String BCC_MANAGED_BRAND_ITEM_DESCRIPTOR="bccManagedBrand";
	public static final String DEF_BRAND_SORT_OPTION="defBrandSortOption";
	public static final String BRAND_SORT_OPTION_LIST="brandSortOptList";
	public static final String SORTING_OPTIONS="sortingOptions";
	
	// Stofu Constant
	public static final String GS_SITE_PREFIX = "GS_";
	
	//R2.2 Story - 79C
	public static final String BCC_MANAGED_US_CANADA_CAT_MAP_ITEM_DESCRIPTOR = "bccManagedUSCanadaCatMap";
	public static final String CANADA_URL = "CanadaUrl";
	public static final String CANADA_CATEGORY_ID = "CanadaCategoryID";
	public static final String US_CATEGORY_ID = "USCategoryID";
	public static final String US_URL = "USUrl";
	public static final String DIM_CONFIG = "DimConfig";

	public static final String BRAND_CONTENT ="brandContent";
	public static final String PROMO_CONTENT ="brandContent";
	public static final String HTML_SNIPPET ="HTMLSnippet";
	public static final String JS_FILE_PATH ="jsFilePath";
	public static final String CSS_FILE_PATH ="cssFilePath";
	public static final String SCHOOL_PROMO_ID ="schoolPromoId";
	public static final String PROMO_ID ="brandPromoId";
	public static final String DEFAULT_VIEW_PLP = "defaultViewValue";
	
	
	//International shipping constants
	
	public static final String ITEM_DESCRIPTOR_RESTRICTED_SKU="restrictedSku";
	public static final String ITEM_DESCRIPTOR_RESTRICTED_JDA_DEPT="restrictedJdaDept";
	public static final String ITEM_DESCRIPTOR_RESTRICTED_JDA_SUB_DEPT="restrictedJdaSubDept";
	public static final String ITEM_DESCRIPTOR_RESTRICTED_JDA_CLASS="restrictedJdaclass";
	public static final String ITEM_DESCRIPTOR_RESTRICTED_BRANDS="restrictedBBBBrands";
	public static final String PROPERTY_NAME_RESTRICTED_JDA_DEPT="jdaDeptId";
	public static final String PROPERTY_NAME_RESTRICTED_JDA_SUB_DEPT="jdaSubDeptId";
	public static final String PROPERTY_NAME_RESTRICTED_JDA_CLASS="jdaClassId";
	public static final String PROPERTY_NAME_RESTRICTED_BRANDS="bbbBrands";
	public static final String INTERNATIONAL_FLAG="intlFlag";
	public static final String INTERNATIONAL_RESTRICTED="intlRestricted";
	public static final String INTL_ONLY_CHAR="X";
	
	//Cart Analyzer
	public static final String ITEM_DESCRIPTOR_RECOMMENDED_SKUS="recommendedSkus";
	public static final String PROPERTY_NAME_SKU="sku";
	public static final String PROPERTY_NAME_RECOMM_SKU="recommSku";
	public static final String PROPERTY_NAME_PRIORITY="priority";
	public static final String PROPERTY_NAME_DESCRIPTION="description";	
	
	public static final String PROPERTY_SKU_IMAGES = "gsSkuImages";
	
	//LTL
	public static final String ORDER_TO_SHIP_SLA =  "orderToShipSla";
	public static final String IS_LTL_SKU = "ltlFlag";
	public static final String LTL_PRODUCT_PROPERTY_NAME = "ltlFlag";
	public static final String IS_LTL_SHIPPING_METHOD = "isLTLShippingMethod";
	public static final String LTL_ITEM_ASSEMBLY_TIME = "assemblyTime";
	public static final String SKU_WEIGHT = "caseweight";
	public static final String DELIVERY_SURCHARGE_PROPERTY_NAME = "price";
	public static final String IS_ASSEMBLY_FEE_OFFERED = "isAssemblyOffered";
	public static final String WHITE_GLOVE_SHIP_METHOD = "LW";
	public static final String WHITE_GLOVE_ASSEMBLY_SHIP_METHOD = "LWA";
	public static final String LTL_SHIP_METHOD = "ltlShipMethod";	
	public static final String LTL_ASSEMBLY_SKU_PRICE_SITE_PROPERTY_NAME = "assemblyFeePrice";
	public static final String LTL_ASSEMBLY_SKU_SITE_PROPERTY_NAME = "assemblyFeeSku";
	public static final String LTL_ASSEMBLY_PRODUCT_SITE_PROPERTY_NAME = "assemblyFeeProduct";
	public static final String LTL_DELIVERY_CHARGE_SKU_PRICE_SITE_PROPERTY_NAME = "deliveryChargePrice";
	public static final String LTL_DELIVERY_CHARGE_SKU_SITE_PROPERTY_NAME = "deliveryChargeSku";
	public static final String LTL_DELIVERY_CHARGE_PRODUCT_SITE_PROPERTY_NAME = "deliveryChargeProduct";
	public static final String WHITE_GLOVE_ASSEMLBY="whiteGloveAssembly";
	public static final String LTL_DELIVERY_ITEM_ID = "deliveryItemId";
	public static final String LTL_ASSEMBLY_ID = "assemblyItemId";
	public static final String IS_LTL_PRODUCT = "isLtlProduct";
	public static final String LTL_DELIVERY_ATTRIBUTES_LIST = "ltlDeliveryAttributeList";
	public static final String SHIPPING_ATTRIBUTES_LIST = "shippingAttributesList";
	public static final String PROPERTY_NAME_STATE_TAX="stateTaxValue";
	public static final String TOKENS="tokens";
	public static final String REGISTRY_ID="registryId";
	public static final String REGISTRY_RECOMMENDATIONS="RegistryRecommendations";
	public static final String INVITEES = "invitees";
	public static final String INVITEE = "invitee";
	public static final String INVITEE_EMAILI_ID = "inviteeEmailIid";
	public static final String TOKEN_CREATION_DATE = "tokenCreationDate";
	public static final String TOKEN_STATUS = "tokenStatus";
	public static final String EVENT_TYPE = "eventType";
	public static final String EVENT_DATE = "eventDate";
	public static final String REGISTRANT_NAME = "registrantName";
	public static final String REGISTRY_TYPE_BABY = "Baby";
	
	// PSI 6 Tag Convertor 
    public static final String EXCHANGE_RATES = "exchangeRates";
	
	// Attributes Table Columns
    public static final String FREE_STANDARD_SHIPPING = "freeStandardShipping";
    public static final String CLEARANCE = "clearance";
    public static final String SKU_GIFT_WRAPELIGIBLE = "skuGiftWrapEligible";
	
	// product thumbnail image size
	public static final String PRODUCT_THUMBNAIL_IMAGE_SIZE="229";
	//skuId url in Marketing Feed
	public static final String SKU_ID_CONSTANT="?skuId=";
	
	public static final String IS_FROM_FACEBOOK = "isFromFacebook";
	
	public static final String DEFAULT_CHANNEL_THEME = "defaultChannelTheme";
	
	// Heartbeat Monitoring Service
	public static final String APP_ID = "appId";
	public static final String LOG_TIME = "logTime";
	public static final String APP_STATE = "appState";
	public static final String STORE_ID = "storeId";
	public static final String CHANNEL_ID = "channelId";
	public static final String TERMINAL_ID = "terminalId";
	public static final String FRIENDLY_NAME = "friendlyName";
	public static final String CHANNEL_THEME_ID = "channelThemeId";
	public static final String GS_APP_STATE_VALUES = "GS_AppStateValues";
	public static final String HEARTBEAT_REPOSITORY_ITEM = "heartbeatmonitor";
	public static final String IS_CUSTOMIZATION_REQUIRED = "isCustomizationRequired";
	public static final String HEARTBEAT_CONFIG_TYPE = "GS_Heartbeat" ;
	public static final String HEARTBEAT_DESIRED_LIMIT = "heartbeat_desired_limit" ;
	public static final String HEARTBEAT_PERMISSIBLE_LIMIT = "heartbeat_permissible_limit" ;
	public static final String MIN_SHIPPING_DAYS = "minShippingDays";
	public static final String MAX_SHIPPING_DAYS = "maxShippingDays";
	
	public static final String VENDORS_ITEM_DESCRIPTOR = "vendors";
	public static final String VENDORS_NAME_PROPERTY = "customizationVendorName";
	public static final String VENDOR_CONFIGURATION_DESCRIPTOR = "vendorsCustomization";
	public static final String VENDOR_JS = "vendorJs";
	public static final String VENDOR_JS_MOBILE = "vendorMobileJs";
	public static final String VENDOR_API_URL = "apiURL";
	public static final String VENDOR_API_VERSION = "apiVersion";
	public static final String VENDOR_BBB_CLIENT_ID = "bbbClientId";
	public static final String VENDOR_BAB_CLIENT_ID = "babyClientId";
	public static final String VENDOR_CAN_CLIENT_ID = "canClientId";
	public static final String VENDOR_TBS_BBB_CLIENT_ID = "tbsBbbClientId";
	public static final String VENDOR_TBS_BAB_CLIENT_ID = "tbsBabyClientId";
	public static final String VENDOR_TBS_CAN_CLIENT_ID = "tbsCanClientId";
	public static final String VENDOR_API_KEY = "apiKey";
	public static final String VENDOR_METHOD_NAME ="methodName";
	public static final String VENDOR_CSS ="vendorCss";
	public static final String VENDOR_CSS_MOBILE = "vendorMobileCss";
	
	public static final String DEFAULT = "DEFAULT";
	public static final String CART_DETAIL = "cartDetail";
	public static final String PRODUCT_DETAILS = "productDetails";
	public static final String INDEX_PRODUCT_COLL_PRODUCT_PROPERTY_NAME="indexProduct";	
	public static final String INDEX_RQL_GET_PROD_RELATION_BY_CHILD_PROD_ID = "getProductRelationByChildProductId";
	public static final String PRODUCT_RELATION_ITEM_DESCRIPTOR="bbbPrdReln";	
	
	public static final String FORGOT_PASSWORD_TOKEN = "ForgotPwdToken";
	public static final String EXCLUSION_RULES_ITEM_DESCRIPTOR = "exclusionText";
	
	public static final String PARENT_PRODUCT_COLUMN_NAME = "parent_product_id";
	public static final String CHILD_PRODUCT_COLUMN_NAME = "child_product_id";
	
	public static final String BAZAARVOICE_FAMILY_EXPAND_ID = "BV_FE_EXPAND";
	public static final String BAZAARVOICE_FAMILY_ID = "BV_FE_FAMILY";
	public static final String BAZAARVOICE_FAMILY_EXPAND_PREFIX = "BV_FE_FAMILY:";
	
	
	//Region
	public static final String REGIONS_ITEM_DESCRIPTOR = "regions";
	public static final String REGIONS_STORES = "stores";
	public static final String REGIONS_ZIPCODES = "zipCodes";
	public static final String REGIONS_REG_STORES = "regionNStore";
	public static final String REGIONS_ALL = "all";
	
	public static final String REGIONS_REGION_ID = "regionId";
	public static final String REGIONS_REGION_NAME = "regionName";
	public static final String REGIONS_CUT_OFF = "cutOffTimeRegion";
	public static final String REGIONS_DISP_CUT_OFF = "displayCutOffTime";
	public static final String REGIONS_PROMO_ATT = "promoAttributeId";
	public static final String SITES = "sites";
	public static final String MIN_SHIP_FEE = "minShipFee";
	public static final String GET_BY_TIME = "getByTime";
	public static final String TIME_ZONE = "regionTimeZone";
	public static final String REGIONS_SDD_FLAG = "sddFlag";
	public static final String REGIONVO_DISP_CUT_OFF="displayCutoffTime";
	public static final String REGIONVO_DISP_GET_BY="displayGetByTime";
	
	public static final String PRODUCT_PRICE_ITEM = "productPrice";
	public static final String CA_LIST_PRICE_STRING = "caListPriceString";
	public static final String CA_SALE_PRICE_STRING = "caSalePriceString";
	public static final String CA_PRICING_LABEL_CODE = "caPricingLabelCode";
	public static final String BABY_LIST_PRICE_STRING = "babyListPriceString";
	public static final String BABY_SALE_PRICE_STRING = "babySalePriceString";
	public static final String BABY_PRICING_LABEL_CODE = "babyPricingLabelCode";
	public static final String MX_LIST_PRICE_STRING = "mxListPriceString";
	public static final String MX_SALE_PRICE_STRING = "mxSalePriceString";
	public static final String MX_PRICING_LABEL_CODE = "mxPricingLabelCode";
	public static final String BBB_LIST_PRICE_STRING = "bbbListPriceString";
	public static final String BBB_SALE_PRICE_STRING = "bbbSalePriceString";
	public static final String BBB_PRICING_LABEL_CODE = "bbbPricingLabelCode";
	public static final Object WAS_PRICE_LABEL = "WAS";
	public static final String WAS = "WAS";
	public static final String IS = "IS";
	public static final Object ORIG_PRICE_LABEL = "ORIG";
	public static final String ORIG = "ORIG";
	public static final String NOW = "NOW";
	public static final String SKU_PRICE_ITEM = "skuPrice";
	public static final String CA_PRICING_LABEL_CODE_SKU = "caPricingLabelCode";
	public static final String BABY_PRICING_LABEL_CODE_SKU = "babyPricingLabelCode";
	public static final String MX_PRICING_LABEL_CODE_SKU = "mxPricingLabelCode";
	public static final String BBB_PRICING_LABEL_CODE_SKU = "bbbPricingLabelCode";
	public static final String CA_IN_CART_FLAG = "caIncartFlag";
	public static final String BABY_IN_CART_FLAG = "babyIncartFlag";
	public static final String MX_IN_CART_FLAG = "mxIncartFlag";
	public static final String BBB_IN_CART_FLAG = "bbbIncartFlag";
	public static final String CA_IN_CART_FLAG_SKU = "caIncartFlag";
	public static final String BABY_IN_CART_FLAG_SKU = "babyIncartFlag";
	public static final String MX_IN_CART_FLAG_SKU = "mxIncartFlag";
	public static final String BBB_IN_CART_FLAG_SKU = "bbbIncartFlag";
	public static final String MEXICO_CODE = "Mex";
	public static final String MEXICO_COUNTRY = "MX";
	public static final String IN_CART_PRICELIST = "InCartPriceList";
    
	//categoryRedirectURLs
	public static final String CATEGORY_REDIRECTURL_ITEM_DESCRIPTOR = "bccManagedCategoryRedirectURLs";
	public static final String SOURCE_CATEGORY_ID = "sourceCategoryId";
	public static final String TARGET_REDIRECT_URL = "targetRedirectURL";
	public static final String MOBILE_TARGET_REDIRECT_URL = "mobileTargetRedirectURL";
	
	public static final String BCC_MANAGED_PROMO_TYPE = "type";
	public static final String BCC_MANAGED_PROMO_CATEGORY = "bccManagedPromotionCategory";
	public static final String ITEM_PROP_SHOW_COLOR_SWATCHES = "showColorSwatches";
	public static final String ITEM_PROP_SHOW_FILTERS = "showFilters";
	public static final String ITEM_PROP_SHOW_GLOBAL_ATTRIBUTES = "showGlobalAttributes";
	public static final String ITEM_PROP_SHOW_PRICE = "showPrice";
	public static final String ITEM_PROP_SHOW_PRODUCT_COMPARE = "showProductCompare";
	public static final String ITEM_PROP_SHOW_QUICK_VIEW = "showQuickViewLink";
	public static final String ITEM_PROP_SHOW_REVIEW_RATINGS = "showReviewRatings";
	public static final String ITEM_PROP_SHOW_TITLE = "showTitle";
	public static final String PRODUCTATTRIBUTES	=	"productAttributes";
	public static final String BBB_PRD_RELN = "bbbPrdReln";
}

