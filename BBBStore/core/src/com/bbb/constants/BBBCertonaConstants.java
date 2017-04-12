/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBCertonaConstants.java
 *
 *  DESCRIPTION: Constant variable specific to Certona
 * 
 *  @author rsain4
 *  
 *  HISTORY:
 *  
 *  05/02/12 Initial version
 *
 */

package com.bbb.constants;

public class BBBCertonaConstants {
	public BBBCertonaConstants() {
		super();
	}

	// Certona Repository constants
	public static final String FEED = "feed";
	public static final String LAST_MODIFIED_DATE = "lastModifiedDate";
	public static final String SCHEDULER_COMPLETION_DATE = "schedulerCompletionDate";
	public static final String SCHEDULER_START_DATE = "schedulerStartDate";
	public static final String STATUS = "status";
	public static final String TYPE_OF_FEED = "typeOfFeed";
	public static final String MODE = "mode";
	
	public static final String FEED_CONFIG_TYPE = "CertonaKeys";

	// Profile constants
	public static final String PROFILE_VIEW_NAME = "user";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String REGISTRATION_DATE = "registrationDate";
	public static final String USER_TYPE = "userType";
	public static final String LOCALE = "locale";
	public static final String LAST_ACTIVITY = "lastActivity";
	public static final String EMAIL = "email";
	public static final String MOBILE_NUMBER = "mobileNumber";
	public static final String PHONE_NUMBER = "phoneNumber";
	public static final String DATE_OF_BIRTH = "dateOfBirth";
	public static final String GENDER = "gender";
	public static final String FACEBOOK_PROFILE = "facebookProfile";
	public static final String USER_SITE_ITEMS = "userSiteItems";
	public static final String SITE_ID = "siteId";
	public static final String FAVOURITE_STORE_ID = "favouriteStoreId";
	public static final String MEMBER_ID = "memberId";
	public static final String REGISRRY_IDS = "registryIds";
	public static final String EVENT_TYPE = "eventType";
	public static final String EVENT_DATE = "eventDate";
	public static final String UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION = "2003";
	
	
	public static final String SCH_COMPLETION_DATE ="schedulerCompletionDate";
	public static final String SCH_START_DATE ="schedulerStartDate";	
	public static final String INVENTORY ="inventory";	
	public static final String SITE_STOCK_LEVEL_PROPERTY = "siteStockLevel";
	public static final String REG_STOCK_LEVEL_PROPERTY = "registryStockLevel";
	public static final String TRANSLATIONS_PROPERTY = "translations";

	public static final String LAST_MODIFIED_DATE_CERTONA_PROPERTY_NAME = "lastModifiedDate";
	public static final String SKU_ID = "catalogRefId";
	public static final String ATTRIBUTE_VALUE_STRING="attributeValueString";
	public static final String ATTRIBUTE_VALUE_BOOLEAN="attributeValueBoolean";
	public static final String ATTRIBUTE_VALUE_NUMBER="attributeValueNumber";
	public static final String TRANSLATION_PROPERTY_ATTRIBUTE_NAME="attributeName";
	
	public static final String USER_AGENT = "User-Agent";
	public static final String SCHEME = "scheme";
	public static final String SCHEME_ID = "schemeId";
	 public static final String SCHEME_ID_MOBILE="scheme_Id";
	public static final String CERTONA = "certona";
	public static final String FULL_CART_LAST_MINUTE_ITEMS = "fc_lmi";
	public static final String PRODDETAIL_FREQUENT_BOUGHT = "pdp_fbw";
	public static final String PRODDETAIL_CUSTOMER_VIEWED = "pdp_cav";
	public static final String PRODDETAIL_COLL_FREQUENT_BOUGHT = "pdp_collfbw";
	public static final String PRODDETAIL_COLL_CUSTOMER_VIEWED = "pdp_collcav";
	public static final String PRODDETAIL_ACC_FREQUENT_BOUGHT = "pdp_accfbw";
	public static final String PRODDETAIL_ACC_CUSTOMER_VIEWED = "pdp_acccav";
	public static final String PRODDETAIL_OOS = "pdp_oos";
	public static final String SEARCH_RR = "search_rr";

	public static final String GIFT_REGISTRY_TOP_REG_ITEMS = "gru_tri";
	public static final String RECOMM_PRODUCTS = "recommendedProd";
	

	public static final String CERTONA_RES_TRACKING_COOKIE_NAME = "RES_TRACKINGID";
	public static final String CERTONA_RES_SESSIONID_COOKIE_NAME = "RES_SESSIONID";
	public static final int CERTONA_RES_COOKIE_AGE = 10 * 365 * 24 * 60 * 60;
	public static final String CERTONA_RES_COOKIE_PATH = "/store";

	public static final String LMI_CONFIG_KEY = "ContentCatalogKeys";
	public static final String LAST_MINUTE_ITEMS = "lastMinuteItems";
	public static final String FLAG_DRIVEN_CONFIG_KEY = "FlagDrivenFunctions";

	
	public static final String SKU_MAX_INVENTORY_RETRIEVAL_ERR_MSG = "Error in finding sku with max inventory for product ";
	public static final String SKU_DETAIL_RETRIEVAL_ERR_MSG = "Error while retreiving the SKUDetailVO's for sku ";
	public static final String BACKUP_LMI_CONFIG_RETREIVAL_ERR_MSG = "Error while fetching the Config key sku values ";
	public static final String BACKUP_LMI_SKUVO_RETREIVAL_ERR_MSG = "LMI Backup error while retreiving SKUDetailsVO for sku ";

	public static final String SEMICOL_DELIMITER = ";";
	public static final String TRACKINGID = "trackingid";
	public static final String SESSIONID = "sessionid";
	public static final String FROM_REST = "FromRest";
	public static final String SHIPPINGTHRESHOLD = "shippingThreshold";
	public static final String SHIPPINGTHRESHOLDVALUE = "0.00";
	public static final String FREESHIPPINGTHRESHOLD = "freeshippingthreshold";

	public static final String THIRD_PARTY_CERTONA_ERROR_CODE = "8222";
	public static final String THIRD_PARTY_CERTONA_ERROR_MESSAGE = "err_null_response_certona_details";
	public static final String ERROR_INVALID_INPUT = "err_invalid_input";
	public static final String IPADDRESS = "ipaddress";
	public static final String HIDDEN_IPADDRESS = "ipaddress=xx.xxx.xxx.xx";
	public static final String CARTANDCHECKOUTKEYS = "CartAndCheckoutKeys";
	public static final String TRUE_IP_HEADER = "TRUE_IP_HEADER";
	public static final String BABY_CA_MODE = "babyCAMode";
	public static final String BABY_CANADA = "BabyCanada";
	public static final String CO_BRAND = "coBrand";
	public static final String BABYCA_PARAMETER = "babyCA";
	public static final String BABYCA_PARAMETER_TRUE = "true";
	
	public static final String PRODUCTID = "productId";
	public static final String GIFTREGID = "giftregid";
	public static final String USERID = "userid";
	public static final String CONTEXT = "context";

	public static final String NUMBER = "number";
	public static final String EXITEMID = "exitemid";
	public static final String SITEID = "X-bbb-site-id";

	public static final String CATEGORYID = "categoryId";
	public static final String COLLEGEID = "collegeid";
	public static final String REGISTRYTYPE = "registrytype";

	public static final String APPID_CERTONA = "appIdCertona";
	public static final String APP_ID = "appid";
	public static final String CERTONA_RESPONSE_VO = "certonaResponseVO";
	//BPS-2023
	public static final String OUTPUT = "OUTPUT";
	public static final String SEARCH_SCHEME_OUTPUT = "output";
	public static final String OUTPUT_XML = "xml";
	//(BPS-2413) OOS recommendation: New certona slot & tagging - MSWP
	public static final String GROUP_FOR_RESLINK ="groupForResLink";
	public static final String IS_INTERNATIONAL_CUSTOMER ="isInternationalCustomer";
	public static final String PARAM_SEARCH_TERMS ="searchterms";
	public static final String SYSTEM_ERROR_COMPONENT = "/com/bbb/utils/SystemErrorInfo";
	public static final String QUOTES = "\"";
	
}
