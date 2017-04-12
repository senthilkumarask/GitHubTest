/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 * 
 *  FILE:  BBBCmsConstants.java
 *
 *  DESCRIPTION: Stores all constants used in cms module 
 *
 *  HISTORY:
 *  10/13/11 Initial version
 *
 */

package com.bbb.constants;



/**
 * This class keeps all constants for CMS module.
 * 
 * 
 * 
 */
public class BBBCmsConstants {
	
	public BBBCmsConstants(){
		//default constructors
	}
	/**
	 * Constants for Bridal show
	 */
	public static final String SHOW_TEMPLATE = "BridalShowTemplate";
	public static final String DATE = "date";
	public static final String FROM_DATE = "fromDate";
	public static final String TO_DATE = "toDate";
	public static final String STATE_ID = "stateId";
	public static final String SITE_ID = "siteId";
	public static final String EMPTY = "empty";
	public static final String STATE_ITEM = "stateItem";
	public static final String OUTPUT = "output";
	public static final String DESCRIP = "descrip";
	public static final String DEFAULT_STATE = "defaultState";
	public static final String STATE_MAP = "stateMap";
	
	public static final String IMAGE = "image";
	public static final String SHORT_DESCRIPTION = "shortDescription";
	public static final String LONG_DESCRIPTION = "longDescription";
	public static final String RANK = "rank";
	public static final String CONSULTANT_PICKLIST =  "consultanTopPicklist";
	public static final String KICKSTARTER_PICKLISTS = "consultanTopPicklist";
	public static final String KICKSTARTER_SITES = "sites";
	public static final String REGISTRYTYPES = "registryTypes";
	public static final String REGISTRYTYPE = "registryType";
	public static final String SKU = "sku";
	public static final String QUANTITY = "quantity";
	public static final String COMMENT = "comment";
	public static final String CONSULTANT_STATUS = "consultantStatus";
	public static final String TITLE = "title";
	public static final String TOP_SKUS = "topSkus";
	
	public static final String KICKSTARTER_TOPCONSULTANT = "topConsultant";
	public static final String TOPCONSULTANT_ID = "id";
	public static final String CONSULTANT_TYPE = "customerType";
	public static final String RQL_QUERY_TOPCONSULTANTS = "consultantStatus = ?0";
	public static final String RQL_QUERY_CONSULTANT_DETAILS = "id = ?0";
	public static final String ANANYMOUS = "Ananymous";
	public static final String LOGGEDIN = "LoggedIn";
	public static final String BOTH = "Both";

	public static final String NAME = "name";
	public static final String TIME = "time";
	public static final String ADDRESS = "address";
	public static final String PHONE = "phone";
	public static final String DIRECTION = "direction";

	public static final String STATE_LOWER_CASE = "state";
	public static final String STATE_PROPER_CASES = "State";

	public static final String ID = "ID";
	public static final String ALASKA = "Alaska";
	public static final String HAWAII = "Hawaii";
	public static final String TERRITORY = "territory";	
	public static final String BLANKSTATES = "blankStates";
	public static final String ALHUUSTATES = "alHuuStates";
	public static final String OTHERSTATES = "OtherStates";

	public static final String ORDER_PRICE_RANGE = "orderPriceRange";
	public static final String TOTAL_ORDER_AMOUNT = "Total Order Amount";
	public static final String STANDARD_PRICE = "standardPrice";
	public static final String STANDARD_PROPER_CASE = "Standard";
	public static final String EXPEDITED_PRICE = "expeditedPrice";
	public static final String EXPEDITED = "Expedited";
	public static final String EXPRESS_PRICE = "expressPrice";
	public static final String EXPRESS = "Express";

	public static final String UP_TO = "Up to";
	public static final String SPACE = " ";
	public static final String HYPHEN = "-";
	public static final String DOLLOR = "$";
	public static final String AND_ABOVE = "and above";
	public static final String FREE = "FREE";
	public static final String BLANK = "";
	public static final String MAX_DAYS_TO_SHIP = "maxDaysToShip";

	public static final String MIN_DAYS_TO_SHIP = "minDaysToShip";
	public static final String MAX_DAYS_TO_SHIP_VDC = "maxDaysToShipVDC";
	public static final String MIN_DAYS_TO_SHIP_VDC = "minDaysToShipVDC";
	public static final String SHIP_METHOD_NAME = "shipMethodName";
	public static final String SHIP_METHOD_CODE = "shipMethodCode";
	public static final String PRICE = "price";

	public static final String UPPERLIMIT = "upperLimit";
	public static final String LOWERLIMIT = "lowerLimit";
	public static final String PAGENAME = "pageName";
	public static final String PAGETYPE = "pageType";
	public static final String BBBPAGENAME = "bbbPageName";
	public static final String CONFIG_KEY = "clearanceCategories";

	/**
	 * Constants for Static Template
	 */
	public static final String STATIC_TEMPLATE_QUERY = "siteId = ?0 and pageName=?1";
	public static final String STATIC_NEW_TEMPLATE_QUERY = "siteId = ?0 and pageName=?1 and bbbPageName=?2";
	public static final String REGISTRY_NEW_TEMPLATE_QUERY = "siteId = ?0 and pageName=?1 and bbbPageName=?2";
	public static final String SHIPPING_METHOD_ALL_QUERY = "ALL";
	public static final String SHIPPING_PRICE_QUERY = "site = ?0 ORDER BY STATE SORT ASC";
	
	public static final String SITE_STATIC_PAGE = "siteStaticPage";
	public static final String STATIC_TEMPLATE_DATA = "staticTemplateData";
	public static final String PAGE_BREADCRUMB = "staticPageBreadCrumb";
	public static final String SHIPPING_METHODS = "shippingMethods";
	public static final String SHIPPING_METHOD_PRICES = "shippingMethodPrices";
	public static final String SHIPPING_PRICE_TABLE_DETAIL = "ShippingPriceTableDetail";
	public static final String SHIPPING_METHOD_DETAILS = "ShippingMethodDetails";
	public static final String SHIPPING_METHODS_NAME_LIST = "shippingMethodsNameList";
	public static final String SHIPPING_METHOD_STATES = "state";
	public static final String SITE_ITEM_DESCRIPTOR="siteConfiguration";
	public static final String APPLICABLE_SHIPMETHODS_SITE_PROPERTY_NAME="applicableShipMethods";

	public static final String REQUEST_TYPE = "requestType";
	public static final String SORT = "sort";
	public static final String SORTED_APP_SHIP_METHOD_PRICE_VO = "sortedAppShipMethodPriceVO";
	public static final String APP_SHIP_METHOD_PRICE_V_OS = "AppShipMethodPriceVOs";
	public static final String SORT_SHIPPING_PRICE = "sortShippingPrice";
	
	/**
	 * Constants for Static Template
	 */
	public static final String CONTENT_CATALOG_KEYS = "ContentCatalogKeys";
	public static final String BIG_BLUE_TOKEN = "bigBlueToken";
	public static final String PAGE_THEME_KEYS = "PageThemeKeys";
	public static final String MIN_DAYS_ADDITION_TO_ALHUU = "MinDaysAdditionToAlHUU";
	public static final String MAX_DAYS_ADDITION_TO_ALHUU = "MaxDaysAdditionToAlHUU";
	public static final String DEFAULT_STATE_US = "US";
	public static final String DEFAULT_STATE_ALHI = "AL";
	public static final String DEFAULT_STATE_OTHERS = "OTHERS";
	public static final String SHIPPING_METHODS_TABLE = "shippingMethodsTable";
	
	/**
	 * Constants for Email A Page Template (cms)
	 */
	public static final String EMAIL_BABY_EVENTS = "BabyEvents";
	public static final String EMAIL_BRIDAL_SHOW = "BridalShow";
	public static final String PLACE_HOLDER_VALUES = "placeHolderValues";
	public static final String FRM_DATA_PAGE_TITLE = "frmData_pageTitle";
	public static final String WEBSITE_NAME = "frmData_website_Name";
	public static final String FRM_DATA_PAGE_TITLE_TEXT = "frmData_pageURLText";
	public static final String FRM_DATA_SENDERS_EMAIL = "frmData_sendersEmail";
	public static final String EMAIL_SUBJECT = "emailSubject";
	public static final String FRM_DATA_SITE_ID = "frmData_siteId";
	public static final String EMAIL_TYPE = "emailType";
	public static final String EMAIL_SUB_BED_BATH_BEYOND_CANADA = "Bed Bath Beyond Canada";
	public static final String EMAIL_SUB_BED_BATH_BEYOND_US = "Bed Bath Beyond US";
	public static final String EMAIL_SUB_BUY_BUY_BABY = "Buy Buy Baby";
	public static final String SITE_ID_BEDBATH_CANADA = "bedbathCanada";
	public static final String SITE_ID_BEDBATH_US = "bedbathUS";
	public static final String SITE_ID_BUYBUYBABY = "buybuybaby";
	public static final String EMAIL_SUB_BRIDAL_SHOWS = "Bridal Shows - ";
	public static final String EMAIL_SUB_BABY_EVENTS = "Baby Events - ";
	public static final String EMAIL_SUB_COLLEGE_CHECKLIST = "College Checklist - ";
	public static final String EMAIL_COLLEGE_CHECKLIST = "CollegeChecklist";
	public static final String EMAIL_SUB_BRIDAL_REGISTRY_CHECKLIST = "Registry Checklist - ";
	public static final String EMAIL_SUBJECT_BED_BATH_BEYOND_CANADA = "email_subject_bedbathcanada";
	public static final String EMAIL_SUBJECT_BED_BATH_BEYOND_US = "email_subject_bedbathus";
	public static final String EMAIL_SUBJECT_BUY_BUY_BABY = "email_subject_buybuybaby";
	public static final String EMAIL_REGISTRY_CHECKLIST = "RegistryChecklist";

	public static final String EMAIL_BRIDAL_SHOWS_PAGE = "email_bridal_shows_page";
	public static final String EMAIL_BABY_EVENTS_PAGE = "email_baby_events_page";
	public static final String EMAIL_COLLEGE_CHECKLIST_PAGE = "email_college_checklist_page";
	public static final String EMAIL_BRIDAL_REGISTRY_CHECKLIST_PAGE = "email_bridal_registry_checklist_page";
	
	public static final String EMAIL_SUBJECT_BABY_EVENTS = "email_subject_baby_events";
	public static final String EMAIL_SUBJECT_COLLEGE_CHECKLIST = "email_subject_college_checklist";
	public static final String EMAIL_SUBJECT_BRIDAL_REGISTRY_CHECKLIST = "email_subject_bridal_registry_checklist";
	public static final String EMAIL_SUBJECT_BRIDAL_SHOWS = "email_subject_bridal_shows";
	public static final int NAV_MAX_COLUMNS =2;
	public static final int NAV_MAX_ROWS =7;
	public static final int MAX_CATEGORY_COUNT=12;
	
	/**
	 * Constants for Circular Landing Template
	 */
	public static final String NOT_REQUIRED = "NotRequired";
	public static final String LANDING_TEMPLATE_VO = "LandingTemplateVO";

	public static final String PAGE_COPY = "pageCopy";
	public static final String PAGE_HEASER_COPY = "pageHeaderCopy";
	public static final String PAGE_TITLE = "pageTitle";
	public static final String SEO_URL = "seoUrl";
	public static final String OMNITUREDATA ="OmnitureData";
	public static final String PARENTPAGE ="parentPage";
	public static final String TYPE_OMNITUREDATA ="TypeOmnitureData";
	public static final String MODAL_OMNITUREDATA ="ModalOmnitureData";
	public static final String ERROR_VALUE_NOT_FOUND = "value_not_found";
	public static final String ERROR_NULL_INPUT_PARAM = "err_input_param_null";
	public static final String ERROR_FETCH_STATIC_CONTENT = "error_fetch_static_content_500";
	public static final String ERROR_FETCH_HOMEPAGE_CONTENT = "error_fetch_homepage_content_500";
	
	/**
	 * Constants for REST Static Template (CMS)
	 */
	
	public static final String REST_SITE_STATIC_CONTENT_TEMPLATE = "mobileSiteStaticContentTemplate";
	public static final String REST_SITE_STATIC_COLLECTION_TEMPLATE = "mobileSiteStaticCollectionTemplate";
	public static final String REST_SITE_STATIC_MODAL_TEMPLATE = "mobileSiteStaticModalTemplate";
	public static final String REST_SITE_HOMEPAGE_TEMPLATE = "homepageWS";
	public static final String REST_SITE_STATIC_TEMPLATE_CAROUSEL ="carousel";
	public static final String REST_SITE_STATIC_TEMPLATE_PROMOBOX ="promoBox";
	public static final String REST_STATIC_TEMPLATE_QUERY = "siteId = ?0 and pageName=?1";
	public static final String REST_STATIC_TEMPLATE_BBB_PAGENAME_QUERY = "siteId = ?0 and bbbPageName=?1";
	public static final String REST_HOMEPAGE_TEMPLATE_QUERY = "site = ?0 and isInternationalTemplate = ?1";
	public static final String REST_STATIC_NEW_TEMPLATE_QUERY = "siteId = ?0 and pageName=?1 and bbbPageName=?2";
	public static final String REST_STATIC_TEMPLATE_PROMO_QUERY = "siteId = ?0 and id=?1";
	public static final String REST_STATIC_TEMPLATE_PAGE_NAME_OTHERS = "Others";
	public static final String ERROR_NULL_INPUT_PARAM_PAGE_NAME ="err_input_param_page_name_empty";
	public static final String ERROR_INVALID_SITE_ID ="err_invalid_siteId";
	public static final String ERROR_EMPTY_SITE_ID ="err_empty_siteId";
	
	public static final String REST_STATIC_TEMPLATE_PROPERTY_PAGE_NAME = "pageName";
	public static final String REST_STATIC_TEMPLATE_PROPERTY_SEO_URL = "seoUrl";

	public static final String REST_STATIC_CONTENT_TEMPLATE_PROPERTY_PAGE_SUBHEADER_COPY = "pageSubHeaderCopy";
	public static final String REST_STATIC_CONTENT_TEMPLATE_PROPERTY_PAGE_TITLE = "pageTitle";
	public static final String REST_STATIC_CONTENT_TEMPLATE_PROPERTY_PROMOBOX_1 = "promoBox1";
	public static final String REST_STATIC_CONTENT_TEMPLATE_PROPERTY_PROMOBOX_2 = "promoBox2";
	public static final String REST_STATIC_CONTENT_TEMPLATE_PROPERTY_CAROUSEL = "carousel";
	public static final String REST_STATIC_CONTENT_TEMPLATE_PROPERTY_PAGE_CONTENTS = "pageContents";
	public static final String REST_STATIC_CONTENT_TEMPLATE_HEAD_TAG_CONTENT = "headTagContent";
	public static final String REST_STATIC_CONTENT_TEMPLATE_BODY_ENDTAG_CONTENT = "bodyEndTagContent";
	
	public static final String REST_STATIC_COLLECTION_TEMPLATE_PROPERTY_PAGE_HEADER_COPY = "pageHeaderCopy";
	public static final String REST_STATIC_COLLECTION_TEMPLATE_PROPERTY_PAGE_COPY = "pageCopy";
	public static final String REST_STATIC_COLLECTION_TEMPLATE_PROPERTY_BBB_PAGE_NAME = "bbbPageName";
	public static final String REST_STATIC_COLLECTION_TEMPLATE_PROPERTY_PRODUCT_LIST = "product";
	public static final String REST_STATIC_COLLECTION_TEMPLATE_PROPERTY_PROMOBOX = "banner";
	public static final String REST_STATIC_COLLECTION_TEMPLATE_PROPERTY_CAROUSEL = "imageBox";
	
	public static final String REST_HOMEPAGE_TEMPLATE_PROPERTY_CAROUSEL = "homeCarousel";
	public static final String REST_HOMEPAGE_TEMPLATE_PROPERTY_BANNER = "homeBanner";
	public static final String REST_HOMEPAGE_TEMPLATE_PROPERTY_PRODUCT_CAROUSEL = "homeProductCarousel";
	public static final String REST_HOMEPAGE_TEMPLATE_PROPERTY_CATEGORY_CAROUSEL = "homeCategoryCarousel";
	public static final String REST_HOMEPAGE_TEMPLATE_PROPERTY_PROMO_LAYOUT1 = "homePromoTierLayOut1";
	public static final String REST_HOMEPAGE_TEMPLATE_PROPERTY_PROMO_LAYOUT2 = "homePromoTierLayOut2";
	public static final String REST_HOMEPAGE_TEMPLATE_PROPERTY_IMAGE_BOX1 = "imageBox1";
	public static final String REST_HOMEPAGE_TEMPLATE_PROPERTY_IMAGE_BOX2 = "imageBox2";
	public static final String REST_HOMEPAGE_TEMPLATE_IMAGE_COUNT = "carouselImageCount";
	public static final String REST_HOMEPAGE_TEMPLATE_CAROUSEL_IMAGES = "carouselImages";
	public static final String REST_HOMEPAGE_TEMPLATE_CAROUSEL_FLIP_TIME = "flipTime";
	public static final String REST_HOMEPAGE_TEMPLATE_CAROUSEL_TITLE = "title";
	public static final String REST_HOMEPAGE_TEMPLATE_NO_OF_PRODUCTS_IN_CAROUSAL = "noOfProductsInProductCarousel";
	public static final String REST_HOMEPAGE_TEMPLATE_NO_OF_CATEGORY_IN_CATAGORY_CONTAINER = "noOfCatInCatContainer";
	public static final String REST_HOMEPAGE_TEMPLATE_CAROUSEL_PRODUCTS = "carouselProducts";
	public static final String REST_HOMEPAGE_TEMPLATE_CATEGORY_IN_CONTAINER = "categoryInCatContainer";
	
	public static final String REST_CONTENTBOX_IMAGE_POSITION = "imagePosition";
	public static final String REST_CONTENTBOX_CONTENT = "content";
	
	public static final String REST_HOMEPAGE_TEMPLATE_PROMOBOX_HEIGHT = "height";
	public static final String REST_HOMEPAGE_TEMPLATE_PROMOBOX_WIDTH = "width";
	public static final String REST_HOMEPAGE_TEMPLATE_PROMOBOX_IMAGE_URL = "imageUrl";
	public static final String REST_HOMEPAGE_TEMPLATE_PROMOBOX_IMAGE_MAP_NAME = "imageMapName";
	public static final String REST_HOMEPAGE_TEMPLATE_PROMOBOX_IMAGE_LINK = "imageLink";
	public static final String REST_HOMEPAGE_TEMPLATE_PROMOBOX_IMAGE_ALT_TEXT = "imageAltText";
	
	public static final String REST_HOMEPAGE_TEMPLATE_BANNER_TEXT = "text";
	public static final String REST_HOMEPAGE_TEMPLATE_BANNER_LINK = "link";
	public static final String REST_HOMEPAGE_TEMPLATE_BANNER_BACKGROUND = "background";
	public static final String REST_HOMEPAGE_TEMPLATE_BANNER_FOREGROUND = "foreground";
	public static final String REST_HOMEPAGE_TEMPLATE_BANNER_FONTSIZE = "fontSize";
	public static final String REST_HOMEPAGE_TEMPLATE_BANNER_FONTWEIGHT = "fontWeight";
	public static final String REST_HOMEPAGE_TEMPLATE_BANNER_MULTILINKS = "multiLinks";	
	public static final String REST_HOMEPAGE_TEMPLATE_BANNER_HIDDENFLAG = "hiddenFlag";
	public static final String REST_HOMEPAGE_TEMPLATE_BANNER_LINKCODE = "linkCode";
	
	public static final String REST_STATIC_MODAL_TEMPLATE_PROPERTY_PAGE_MESSAGE = "pageMessage";
	public static final String REST_STATIC_MODAL_TEMPLATE_PROPERTY_PAGE_TITLE = "pageTitle";
	public static final String REST_STATIC_MODAL_TEMPLATE_PROPERTY_PROMOBOX = "promoBox";
	
	public static final String REST_STATIC_TEMPLATE_PROMOBOX_IMAGE_URL = "imageUrl";
	public static final String REST_STATIC_TEMPLATE_PROMOBOX_IMAGE_ALT_TEXT = "imageAltText";
	public static final String REST_STATIC_TEMPLATE_PROMOBOX_IMAGE_MAP_NAME = "imageMapName";
	public static final String REST_STATIC_TEMPLATE_PROMOBOX_IMAGE_MAP_CONTENT = "imageMapContent";
	public static final String REST_STATIC_TEMPLATE_PROMOBOX_PROMOBOX_CONTENT = "promoBoxContent";
	public static final String REST_STATIC_TEMPLATE_PROMOBOX_PROMOBOX_TITLE = "promoBoxTitle";
	public static final String REST_STATIC_TEMPLATE_PROMOBOX_IMAGE_LINK = "imageLink";
	
	public static final String REST_STATIC_TEMPLATE_CAROUSEL_FLIP_TIME = "flipTime";
	public static final String REST_STATIC_TEMPLATE_CAROUSEL_TITLE = "title";
	public static final String REST_STATIC_TEMPLATE_CAROUSEL_IMAGES = "images";
	
	public static final String REST_STATIC_MODAL_TEMPLATE_TYPE = "3";
	public static final String REST_STATIC_COLLECTION_TEMPLATE_TYPE = "2";
	
	public static final String REST_NAVIGATION_LINKS_TEMPLATE = "navigationLinksWS";
	public static final String REST_NAVIGATION_LINKS_TEMPLATE_QUERY = "sites = ?0 and channel = ?1";
	public static final String REST_NAVIGATION_LINKS_TEMPLATE_PROPERTY_LINKS = "links";
	public static final String ERROR_FETCH_NAVIGATION_LINKS_CONTENT = "error_fetch_navigation_links_content";
	public static final String REST_NAVIGATION_LINKS_TEMPLATE_BANNER_SEPARATOR = "separator";
	
	public static final String ERROR_FETCH_CATEGORYPAGE_CONTENT = "error_fetch_categoryPage_content";
	public static final String ERROR_NULL_INPUT_PARAM_CATEGORY_ID ="err_input_param_category_id_empty";
	
	/**
	 * Constants for Email Fetch (CMS)
	 */
	
	public static final String EMAIL_DATA ="emailData";
	public static final String EMAIL_ID ="emailId";
	public static final String EMAIL_MESSAGE ="emailMessage";
	public static final String EMAIL_ITEM ="emailItem";
	public static final String TOKEN ="token";
	
	
	/*
	 *92F partial Match Constants 
	*/
	
	public static final String STOP_WORD_LIST = "StopWordList";
	public static final String SEARCH_MODE_ALLPARTIAL = "mode+matchallpartial";
	public static final String SEARCH_MODE_MATCHPARTIAL = "mode+matchpartial";
	public static final String PARTIAL_SEARCH_CAROUSEL_MAX_PRODUCTS_KEY = "partialSearchCarouselMaxProducts";
	public static final String MIN_MATCHALLPARTIAL_SEARCH_RESULTS_KEY = "minMatchAllPartialSearchResults";
	public static final String MIN_PARTIALMATCH_WORDS_KEY = "minPartialMatchKeywords";
	public static final String MAX_PARTIALMATCH_WORDS_KEY = "maxPartialMatchKeywords";

	/*
	 * PromoBox Targeter for Registry Pages Constants
	 */
	
	public static final String REG_PROMO_BOX_TARGETER_PAGE_NAME_PARAMETER  		=  "pageName";
	public static final String REG_PROMO_BOX_TARGETER_REGISTRY_TYPE_PARAMETER  	=  "registryType";
	public static final String REG_PROMO_BOX_TARGETER_PROMO_SPOT_PARAMETER  	=  "promoSpot";
	public static final String REG_PROMO_BOX_TARGETER_CHANNEL_PARAMETER  		=  "channel";
	public static final String REG_PROMO_BOX_TARGETER_SITE_ID_PARAMETER  		=  "siteId";
	public static final String REG_PROMO_BOX_TARGETER_CUST_TYPE_PARAMETER  		=  "customerType";
	public static final String REG_PROMO_BOX_TARGETER_LOGGED_IN_USER_CUST_TYPE  =  "LoggedIn";
	public static final String REG_PROMO_BOX_TARGETER_ANONYMOUS_USER_CUST_TYPE  =  "Anonymous";
	
	// LTL delivery surcharge Start
	public static final String DELIVERY_SURCHARGE = "deliverySurcharge";
	// LTL delivery surcharge End
	public static final String LTL_CONFIG_KEY_TYPE_NAME =  "LTLConfig_KEYS";
	public static final String ASSEMBLY_FEE_PER_THIRTY_MINUTES = "assembly_fee_per_thirty_minutes";
	public static final String THRESHOLD_DELIVERY_AMOUNT = "threshold_delivery_amount";
	public static final String DELIVERY_ITEM_ID_PROPERTY = "deliveryItemId";
	public static final String ASSEMBLY_ITEM_ID_PROPERTY = "assemblyItemId";

	// Shipping Methods Code	
	public static final String SHIP_METHOD_CODE_1A = "1a";
	public static final String SHIP_METHOD_CODE_2A = "2a";
	public static final String SHIP_METHOD_CODE_3G = "3g";
	
	public static final String SESSION_BABY_CA = "sessionBabyCA";
	public static final String BABY_CA = "BabyCA";
	
	//Constants for Recommender Page
	public static final String CHANNEL = "channel";
	public static final String  REGISTRY_TYPE = "registryType";
	
	//Invite Friends Config Key
	public static final String  INVITE_FRIENDS_KEY = "Invite_Friends_Key";
	
	public static final String  APPOINTMENT_SCHEDULER_KEY = "Appointment_Scheduler_Key";
	
	public static final String BUYOFF_START_BROWSING_KEY = "buyoff_start_browsing_key";
	public static final String PERSONALIZED_SHOP_STATIC_PAGE = "perShopStaticPage";
	public static final String PERSONALIZED_SHOP_TEMPLATE_QUERY = "siteId = ?0 and categoryId=?1";
	

	//Constants for SDD
	public static final String SDD_KEY = "SameDayDeliveryKeys";
	public static final String  SDD_ATTRIBUTE_LIST = "sameDayDelAttributeKeyList";
	public static final String SDD_SHOW_ATTRI = "SHOW_SDD_ATTRIBUTE";
	public static final String ERR_INVALID_SITE_ID ="Invalid Site ID";
	public static final String DATA_NOT_FOUND ="No data found for category id";
	
	public static final String REST_STATIC_CONTENT_TEMPLATE_REGISTRY_RIBBON_FLAG ="registryRibbonFlag";
	
	//Constants for giftlist batch size
	
	public static final String GIFTLIST_BATCH_SIZE= "giftList_batch_size";
}

