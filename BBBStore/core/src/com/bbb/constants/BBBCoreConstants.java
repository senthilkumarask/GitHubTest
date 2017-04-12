/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBCoreConstants.java
 *
 *  DESCRIPTION: Stores all constants used in core module
 *
 *  HISTORY:
 *  10/13/11 Initial version
 *
 */

package com.bbb.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bbb.repositorywrapper.IRepositoryWrapper;
import com.bbb.repositorywrapper.RepositoryWrapperImpl;

import atg.nucleus.Nucleus;
import atg.nucleus.naming.ParameterName;
import atg.repository.Repository;

/**
 * This class keeps all constants for core module.
 *
 *
 */
public class BBBCoreConstants {
	public static final String WHITE_SPACE = " ";
	public static final String MODERATED = "Moderated";
	public static final String VIEW_ALL = "1";
	public static final String VIEW_REMAINING = "2";
	public static final String VIEW_PURCHASED = "3";
	public static final String PRODUCT_REQUIRED = "productsReq";
	public static final String DOT_EXTENTION = ".";
	public static final String SINGLE_QUOTE = "'";
	public static final String USED_PROMOTIONS = "usedPromotions";
	public static final String SELECTED_PROMOTIONS_LIST = "selectedPromotionsList";
	public static final String AVAILABLE_PROMOTIONS_LIST = "availablePromotionsList";
	public static final String SHIP_ZIP_CODE_RESTRICTED_FOR_SKU = "err_ship_zipcode_restricted_for_sku";
	public static final String PORCH_SERVICE_NOT_AVAILABLE = "err_porchServiceNotAvailable";
	public static final String SHIP_ZIP_CODE_RESTRICTED_FOR_SKU_MSSG = "err_ship_zipcode_restricted_for_sku_extended";
	public static final String NO_SKU_ID_TO_REMOVE_FROM_CART = "err_no_sku_id_to_remove_from_cart";
	public static final String NO_SKU_ID_TO_UPDATE_IN_CART = "err_no_sku_id_to_update_in_cart";
	public static final String ATG_PROFILE = "/atg/userprofiling/Profile";
	public static final String ATG_LOCALE = "/atg/dynamo/servlet/RequestLocale";
	public static final String ATG_SHOPPING_CART = "/atg/commerce/ShoppingCart";
	public static final String STORE_CA_START = "3";
	public static final Object PIPE_SYMBOL = "|";
	public static final String NO = "no";
	public static final String USD = "USD";
	public static final String NO_CHAR = "N";
	public static final String YES_CHAR = "Y";
	public static final String YES = "yes";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String NULL_VALUE = "null";
	public static final String BLANK = "";
	public static final String SPACE = " ";
	public static final String CC_DATE_FORMAT = "yyyy-MM-dd";
	public static final String UNKNOWN_STATE_CODE = "unknown";
	public static final String REDIRECTION = "redirection";
	public static final String CART_WISH_LIST_REDIRECTION = "cartWishList";
	public static final String ORDER = "order";
	public static final String COMMERCE_ITEM_LIST = "commerceItemList";
	public static final String REGISTRY_ID = "registryId";
	public static final String GUIDE_ID = "guideId";
	public static final String GUIDE_TYPE = "guideType";
	public static final String REGISTRY_TYPE = "registryType";
	public static final String EXPECTED_DELIVERY_DATE = "expectedDeliveryDate";
	public static final String COUNT = "count";
	public static final String BRIDAL_TOOLKIT = "BridalToolkit";
	public static final String PAGE_NAME = "pageName";
	public static final String HTTP_INVKR_AUTH = "Authorization";
	public static final String HTTP_INVKR_CNT_TYPE = "Content-Type";
	public static final String COLLEGE = "college";
	public static final String ORDER_SUB_STATUS = "substatus";
	public static final String CREATED_BY_ORDER_ID = "createdByOrderId";
	public static final String ALGORITHM_DEFAULT_PERCENTAGE = "_0";
	public static final String No_EPH = "noEPH";
	public static final String SEARCH = "SEARCH";
	public static final String L2L3 = "L2L3";
	public static final String HASH = "#";
	public static final String DOUBLE_ZERO_VALUE = "00";
	public static final String ACTUALAPPLIEDALGORITHM = "actualBoostingApplied";

	// Shipping Methods Constants

	public static final String SHIPPING_METHOD = "shippingMethod";
	public static final String SHIPPING_METHOD_CODE = "shippingMethodCode";
	public static final String SHIPPING_METHOD_DESC = "shippingMethodDescription";
	public static final String OPERATION = "operation";
	public static final String PER_SKU = "perSku";
	public static final String PER_ORDER = "perOrder";
	public static final String SKU_MEHOD_MAP = "skuMethodsMap";
	public static final String SHIP_METHOD_VO_LIST = "shipMethodVOList";
	public static final String EXPRESS_SHIP_METHOD = "Express";
	public static final String PRE_SELECTED_SHIP_METHOD = "preSelectedShipMethod";

	public static final String REGISTRANT_EMAIL = "registrantEmail";

	public static final boolean RETURN_TRUE = true;
	public static final boolean RETURN_FALSE = false;

	public static final String QAS_POBOXSTATUS = "Y";
	public static final String US = "US";
	public static final String QAS_POBOXFLAG = "P";
	// Inventory Constants
	public static final String SKU_ID = "catalogRefId";
	public static final String PRODUCTID = "productId";
	public static final String SITE_BAB_CA = "BedBathCanada";
	public static final String SITE_BAB_US_VALUE = "1";
	public static final String SITE_BBB_VALUE = "2";
	public static final String SITE_BAB_CA_VALUE = "3";
	public static final String SITE_BAB_US = "BedBathUS";
	public static final String SITE_BBB = "BuyBuyBaby";
	public static final String QUANTITY_DESIRED = "quantityDesired";
	public static final String LTL_SHIP_METHOD = "ltlShipMethod";
	public static final String QUANTITY_PURCHASED = "quantityPurchased";
	public static final String WISHLIST_ITEM_ID = "id";
	public static final boolean CACHE_ENABLED = true;
	public static final boolean CACHE_DISABLED = false;

	// Account Managment Constants
	public static final String PROFILE_ITEM_DISCRIPTOR_NAME = "user";
	public static final String PHONE_NUMBER = "Phone Number";
	public static final String MOBILE_NUMBER = "Mobile Number";
	public static final Character AT_THE_RATE = '@';
	public static final String LOGIN_ERROR = "loginError";
	public static final String PASSWORD_ERROR = "passwordError";
	public static final String RESET_PASSWORD_ERROR = "resetPasswordError";
	public static final String LEGACY_GET_ACCOUNT_ERROR = "legacyGetAccountError";
	public static final String LEGACY_VERIFY_ACCOUNT_ERROR = "legacyVerifyError";
	public static final String LOGIN_PASSWORD_ERROR = "loginPasswordError";
	public static final String CREATE_PROFILE_EMAIL_ERROR = "createProfileEmailError";
	public static final String CREATE_PROFILE_FIRSTNAME_ERROR = "createProfileFirstNameError";
	public static final String CREATE_PROFILE_LASTNAME_ERROR = "createProfileLastNameError";
	public static final String CREATE_PROFILE_PASSWORD_ERROR = "createProfilePasswordError";
	public static final String CREATE_PROFILE_CONFIRMPASSWORD_ERROR = "createProfileConfirmPasswordError";
	public static final String CREATE_PROFILE_CONTAINS_NAME = "createProfileContainsName";
	public static final String CREATE_PROFILE_PHONENUMBER_ERROR = "createProfilePhoneNumberError";
	public static final String CREATE_PROFILE_MOBILENUMBER_ERROR = "createProfileMobileNumberError";
	public static final String CREATE_PROFILE_SYSTEM_ERROR = "createProfileSystemError";
	public static final String SYSTEM_ERROR = "systemError";
	public static final String REGISTER_ERROR = "registerError";
	public static final String MSG_STATE_IS_INCORRECT = "state";
	public static final String MSG_ERROR_DUPLICATE_CC_NICKNAME = "duplicate nick-name";
	public static final String MSG_CC_NUMBER_EMPTY = "creditCardNumber";
	public static final String MSG_CC_TYPE_EMPTY = "creditCardType";
	public static final String MSG_CC_NOC_EMPTY = "nameOnCard";
	public static final String MSG_CC_EXP_MONTH_EMPTY = "expirationMonth";
	public static final String MSG_CC_EXP_YEAR_EMPTY = "expirationYear";
	public static final String MSG_CC_NUMBER_LENGTH_INCORRECT = "lengthIncorrect";
	public static final String MSG_FNAME_LENGTH_INCORRECT = "fnameLengthIncorrect";
	public static final String MSG_LNAME_LENGTH_INCORRECT = "lnameLengthIncorrect";
	public static final String MSG_COMPANY_LENGTH_INCORRECT = "companyLengthIncorrect";
	public static final String MSG_ADDR1_LENGTH_INCORRECT = "addr1LengthIncorrect";
	public static final String MSG_ADDR2_LENGTH_INCORRECT = "addr2LengthIncorrect";
	public static final String MSG_CC_NOC_LENGTH_INCORRECT = "lengthIncorrect";
	public static final String MSG_CC_NUMBER_NON_NUMERIC = "Credit Card Number Should be Numeric";
	public static final String MSG_EMPTY_CC_FIRST_NAME = "firstName";
	public static final String MSG_EMPTY_CC_LAST_NAME = "lastName";
	public static final String MSG_EMPTY_CC_CITY = "city";
	public static final String MSG_EMPTY_CC_COMPANY = "company";
	public static final String MSG_EMPTY_CC_STATE = "state";
	public static final String MSG_EMPTY_CC_POSTAL_CODE = "postalCode";
	public static final String MSG_EMPTY_CC_COUNTRY = "country";
	public static final String MSG_EMPTY_CC_ADDRESS1 = "address1";
	public static final String MSG_EMPTY_CC_ADDRESS2 = "address2";
	public static final String FORM_SITE_NAME = "frmData_siteName";
	public static final String FORM_BRIDAL_BOOK_ID = "frmData_bridalBookId";
	public static final String REST_REDIRECT_URL = "atg-rest-ignore-redirect";
	public static final String HTML_CACHE_CONFIG_KEY = "HTMLCacheKeys";
	public static final String KICK_STARTERS_CACHE_TIMEOUT = "kickStartersCacheTimeout";

	public static final String LOAD_FB_INFO = "load_fb_info";
	public static final String MEMBER_ID = "memberId";
	public static final String LOAD_FB_INFO_ERROR_URL = "load_fb_info=true&memberId=";
	public static final String MEMBER_ID_ERROR_URL = "memberId=";

	// Single Page Checkout authentication FLAG
	public static final String IS_SINGLE_PAGE_CHECKOUT_ENABLED = "singlePageCheckoutEnabled";

	// Gift registry validation
	public static final String NUMERIC_FIELD_ONLY = "\\d+";
	public static final String DATE_FORMAT = "MM/dd/yyyy";
	public static final String CA_DATE_FORMAT = "dd/MM/yyyy";
	public static final String US_DATE_FORMAT = "MM/dd/yyyy";
	public static final String WS_DATE_FORMAT = "yyyyMMdd";
	public static final String CANADA_DATE_FORMAT = "dd/mm";
	public static final String UNITED_STATES_DATA_FORMAT = "mm/dd";

	public static final String TIME_FORMAT = "hh:mm aa";
	public static final int FIFTY = 50;
	public static final String BABY_NAME_REGULAR_EXP = "^[a-zA-Z0-9_,.'\\-\\s]*$";
	public static final String ALPHANUMERIC_WITH_SPACE = "^[a-zA-Z0-9_,\\s]*$";
	public static final String ALPHANUMERIC_WITH_SPECIAL_CHAR = "^[a-zA-Z\\-.,\\'\\s]*$";

	// Item Type
	public static final String PERSONALIZATION_ITEM_TYPE = "PER";
	public static final String LTL_ITEM_TYPE = "LTL";

	// Numeric COnstant
	public static final int ONE = 1;
	public static final int SIX = 6;
	public static final int FOUR = 4;
	public static final int FIVE = 5;
	public static final int SEVEN = 7;
	public static final int NINE = 9;
	public static final int SIXTY_FOUR = 64;
	public static final int TWO_FIFTY_SIX = 256;
	public static final int TWENTY = 20;
	public static final int TWENTYFIVE = 25;
	public static final int THIRTY = 30;
	public static final int THIRTYFIVE = 35;
	public static final int TEN = 10;
	public static final int FIFTEEN = 15;
	public static final int SIXTEEN = 16;
	public static final int NINETEEN = 19;
	public static final int ZERO = 0;
	public static final int EIGHT = 8;
	public static final int HUNDRED = 100;
	public static final int SIXTYONE = 61;
	public static final int FIFTEEN_HUNDRED = 1500;
	public static final int ONE_THOUSAND = 1000;
	public static final int FOURTY = 40;
	public static final int ONE_THIRTY = 130;
	public static final int EIGHTY = 80;
	public static final int ONE_TWO_FIVE = 125;
	public static final int TEN_THOUSANDS_THREE = 10003;
	public static final double DOUBLE_ZERO = 0.0;
	public static final double POINT_ZERO_ONE = 0.01d;
	public static final int INT_MINUS_ONE = -1;
	public static final int TEN_THOUSANDS = 10000;
	public static final int FIFTY_THOUSANDS = 50000;
	public static final int SIXTY_THOUSANDS = 60000;
	public static final int THIRTY_THOUSANDS = 30000;

	public static final long MILLISECONDS_IN_ONE_DAY = 86400000;

	public static final String STRING_ZERO = "0";
	public static final String STRING_ONE = "1";
	public static final String STRING_TWO = "2";
	public static final String STRING_THREE = "3";
	public static final String STRING_TEN = "10";
	public static final String STRING_X = "x";

	// Inventory Feed Status Constants
	public static final String INVENTORY_FEED_CREATED = "CREATED";
	public static final String FULL_OPEN = "FULL_OPEN";
	public static final String PARTIAL_OPEN = "PARTIAL_OPEN";
	public static final String INVENTORY_FEED_IN_PROGRESS = "IN_PROGRESS";
	public static final String INVENTORY_FEED_ERROR = "ERROR";
	public static final String INVENTORY_FEED_COMPLETE = "COMPLETE";

	// Facebook Linking Status Constants
	public static final String FACEBOOK_LINKED = "FACEBOOK_LINKED";
	public static final String FACEBOOK_NOT_LINKED = "FACEBOOK_NOT_LINKED";
	public static final String FACEBOOK_LINK_ERROR = "FACEBOOK_LINK_ERROR";

	/* Facebook parameters */
	public static final String FACEBOOK_CLIENT_ID = "client_id";
	public static final String FACEBOOK_CLIENT_SECRET = "client_sec";
	public static final String FACEBOOK_AUTH_CODE = "code";
	public static final String FACEBOOK_AUTH_TOKEN = "access_token";
	public static final String FACEBOOK_REDIRECT_URL = "redirect";
	public static final String FACEBOOK_LANDING_URL = "landing";

	// Repository Items Constants
	public static final String USER_SITE_ASSOCIATION_REPOSITORY_ITEM = "userSiteAssoc";
	public static final String USER_SITE_REPOSITORY_ITEM = "userSiteItems";

	public static final String SENDER_EMAIL_PARAM_NAME = "senderEmail";
	public static final String TEMPLATE_URL_PARAM_NAME = "templateUrl";
	public static final String RECIPIENT_EMAIL_PARAM_NAME = "recipientEmail";
	public static final String BCC_RECIPIENT_EMAILS = "messageBCC";
	public static final String SUBJECT_PARAM_NAME = "subject";

	public static final String SKU_PARAM_NAME = "SKU";
	public static final String PRODUCT_ID_PARAM_NAME = "PRODUCT_ID";
	public static final String PRODUCT_NAME_PARAM_NAME = "PRODUCT_NAME";
	public static final String EMAIL_ADDR_PARAM_NAME = "EMAIL_ADDR";
	public static final String MOBILE_NUMBER_PARAM_NAME = "MOBILE_NUMBER";
	public static final String WALLET_ID = "wallet_id";
	public static final String OFFER_ID = "offerId";
	public static final String WS_FORMAT_COUPONS = "yyyy/MM/dd HH:mm:ss";
	public static final String COUPON_EXPIRY_DATE = "expiryDate";
	public static final String COUPON_DISPLAY_NAME = "displayName";
	public static final String COUPON_DESCRIPTION = "description";
	public static final String SHOWACTIVEONLY = "showOnlyActive";
	public static final String CUST_NAME_PARAM_NAME = "CUST_NAME";
	public static final String USER_IP_PARAM_NAME = "USER_IP";
	public static final String REQUESTED_DT_PARAM_NAME = "REQUESTED_DT";
	public static final String WALLETID = "walletId";
	public static final String PROFILE_SOURCE_TYPE = "couponWallet";
	public static final String CHECK_LIST_FLOW = "checkListFlow";
	public static final String CHECK_LIST_FLOW_SUCCESS_URL = "checkListflowSuccessUrl";
	public static final String ERROR_MAP_FOR_SERVICE = "errorMapForService";
	public static final String CREATE_WALLET_MOBILE = "BBBGetCouponsManager/createWalletMobile";

	public static final String IN_STOCK_NOTIFY_DT_PARAM_NAME = "IN_STOCK_NOTIFY_DT";
	public static final String NOTICE_1_DT_PARAM_NAME = "NOTICE_1_DT";
	public static final String NOTICE_2_DT_PARAM_NAME = "NOTICE_2_DT";
	public static final String UNSUBSCRIBE_DT_PARAM_NAME = "UNSUBSCRIBE_DT";
	public static final String FINAL_NOTICE_DT_PARAM_NAME = "FINAL_NOTICE_DT";

	public static final String SITE_FLAG_PARAM_NAME = "SITE_FLAG";
	public static final String URL = "URL";
	public static final String DESCRIPTION = "Description";

	public static final String CONTACT_US_SUBJECT = "subject";
	public static final String COMMERCE_ITEMS = "commerceItems";
	public static final String TYPE = "type";
	public static final String GIFT_WRAP_COMMERCE_ITEM = "giftWrapCommerceItem";
	public static final String COMMERCE_ITEM_COUNT = "commerceItemCount";
	public static final String SHOPPING_CART = "shoppingCart";

	public static final String EMAIL = "email";
	public static final String LOGIN = "login";
	public static final String PASSWORD = "password";
	public static final String USERNAME = "username";
	public static final String ADMIN_NAME = "admin_name";
	public static final String ADMIN_PASSWRD = "admin_password";
	public static final String REMEMBER_ME = "rememberMe";
	public static final String PHONE = "phone";

	/* Scope 69 */
	public static final String PURGE_REPOSITORY = "PurgeRepository";
	public static final String PURGE_DESCRIPTOR = "purge";
	public static final String PROPERTY_PURGE_ID = "purgeId";
	public static final String PROPERTY_REPOSITORY = "repository";
	public static final String PROPERTY_ITEM_DESCRIPTOR = "itemDescriptor";
	public static final String PROPERTY_INTERVAL = "interval";
	public static final String PROPERTY_LAST_MODIFIED_DATE = "lastModifiedDate";
	public static final String PROPERTY_PURGE_PARAM = "valueParam";
	public static final String COLUMN_NAME = "ColumnName";
	public static final String COLUMN_NAME_DATE = "dateColumnName";
	public static final String COLUMN_NAME_ID = "idColumnName";

	// credit-card related constants
	public static final String CREDIT_CARDS = "creditCards";
	public static final String DEFAULT_CREDIT_CARD = "defaultCreditCard";
	public static final String FIELD_NAME = "fieldName";
	public static final String MAKE_BILLING = "makeBilling";
	public static final String NEW_NICKNAME = "newNickname";
	public static final String CC_NUMBER = "creditCardNumber";
	public static final String CC_TYPE = "creditCardType";
	public static final String CC_NOC = "nameOnCard";
	public static final String CC_EXP_MONTH = "expirationMonth";
	public static final String CC_EXP_YEAR = "expirationYear";
	public static final String CC_FIRST_NAME = "firstName";
	public static final String CC_LAST_NAME = "lastName";
	public static final String CC_CITY = "city";
	public static final String CC_COMPANY = "company";
	public static final String CC_COMPANY_NAME = "companyName";
	public static final String CC_STATE = "state";
	public static final String CC_POSTAL_CODE = "postalCode";
	public static final String CC_ADDRESS1 = "address1";
	public static final String CC_ADDRESS2 = "address2";
	public static final String CC_ADDRESS3 = "address3";
	public static final String CC_COUNTRY = "country";
	public static final String CC_NICKNAME = "nickname";
	public static final String CC_POBOXADDRESS = "poBoxAddress";
	public static final String CC_QASVALIDATED = "qasValidated";
	public static final String CC_NEW_BILLING_ADDR = "newBillingAddress";
	public static final String DEFAULT_COUNTRY = "defaultCountry";
	public static final String INERNATIONAL_CREDIT_CARDS = "internationalCreditCard";

	public static final String RECLAIM_PWD_SENT = "A password reminder has been sent to the email address you provided.";

	public static final int ERR_CODE_RECLAIM_INVALID_USER_ID = 208;
	public static final int ERR_CODE_RECLAIM_INVALID_USER_ID_PASSWORD = 206;
	public static final int ERR_CODE_ALREADY_RECLAIMED = 222;
	public static final int ERR_CODE_UNABLE_TO_RECLAIM = 221;
	public static final int ERR_CODE_ACCOUNT_NOT_FOUND = 220;

	// Release 2.2.1 - Constant to set / get session attribute for Baby Canada
	// Flag
	public static final String BABY_CANADA_FLAG_COOKIE = "babyCanadaFlagCookie";
	public static final String BABY_CANADA_SITE = "BabyCanada";
	public static final String COMPRESS_HTML_TAGLIB_USER_AGENTS = "compressHTMLTaglibUserAgents";

	public static final String NARROWDOWN_DELIMITER_FL = "fl";

	public enum SUBSCRIPTION_TYPE {
		TYPE_SUBSCRIBE_EMAIL_DIRECTMAIL, TYPE_UNSUBSCRIBE_DIRECTMAIL, TYPE_UNSUBSCRIBE_EMAIL
	};

	public enum FREQUENCY {
		FREQUENCY_TWICE_A_MONTH, FREQUENCY_ONCE_A_MONTH, FREQUENCY_ONLINE_GIFTCERTIFICATE_COUPONS, UNSUBSCRIBE_ALL_EMAILS
	};

	/**
	 * Instance variable for output.
	 */
	public static final String ERROR_OPARAM = "error";

	/**
	 * Instance variable for applicationConfiguration.
	 */
	public static final String PARAM_PRICE_OBJECT = "priceObject";

	/**
	 * Instance variable for Order Object.
	 */
	public static final String PARAM_ORDER_OBJECT = "orderObject";

	/**
	 * Instance variable for forItemsRawTotal constant.
	 */
	public static final String PARAM_FOR_ITEMS_RAW_TOTAL = "forItemsRawTotal";

	/**
	 * Instance variable for Order Object.
	 */
	public static final String PARAM_CART_ITEMS_RAW_TOTAL = "cartItemsRawTotal";

	/**
	 * Instance variable for output.
	 */
	public static final String OPARAM = "output";
	public static final String OPARAM_ERROR = "error";
	/**
	 * Instance variable for empty.
	 */
	public static final String EMPTY_OPARAM = "empty";
	/**
	 * Instance variable for priceInfoVO.
	 */
	public static final String OUTPUT_PARAM_PRICEINFOVO = "priceInfoVO";

	public static final String OUTPUT_PARAM_ECO_FEE_AMOUNT = "ecoFeeAmount";
	public static final String PARAM_COMMERCE_ID_ECO_FEE = "commerceIdForEcoFee";

	/**
	 * Instance variable for displayName.
	 */
	public static final String DISPLAY_NAME = "displayName";
	public static final String REDIRECT_URL = "redirectURL";
	public static final String REDIRECT_PARAM_NAME = "r";
	public static final String REDIRECT_PARAM_VALUE = "y";
	public static final String STRING_FALSE = "FALSE";
	public static final String HANDLE_LOGIN = "HANDLE_LOGIN";
	public static final String DATE_FORMAT1 = "yyyy/MM/dd hh:mm:ss";
	public static final String BBB_VALIDATION_RULES_PROPERTY = "/com/bbb/framework/validation/BBBValidationRules";
	public static final String EMAIL_ERROR = "emailError";
	public static final String BBB_COMMON_CONFIGURATION = "/com/bbb/utils/CommonConfiguration";

	// Special Char constants
	public static final String COLON = ":";
	public static final String SEMICOLON = ";";
	public static final String EQUAL = "=";
	public static final String AMPERSAND = "&";
	public static final String COMMA = ",";
	public static final String PLUS = "+";
	public static final String UNDERSCORE = "_";
	public static final String PERCENT_TWENTY = "%20";
	public static final String HYPHEN = "-";
	public static final String FORMAT_DISPLAY_NAME = "[^a-zA-Z0-9]+";
	public static final String DOLLAR = "$";
	public static final String PERCENTILE = "%";
	public static final String RIGHT_PARENTHESIS = ")";
	public static final String LEFT_PARENTHESIS = "(";

	// performance monitor keys
	public static final String PRE_LOGIN_USER = "BBBProfileFormHandler.preLoginUser()";
	public static final String POST_LOGIN_USER = "BBBProfileFormHandler.postLoginUser()";
	public static final String SERVICE = "BBBAccessControler.service()";
	public static final String PRE_FORGOT_PASSWORD = "BBBForgotPasswordFormHandler.preForgotPassword()";
	public static final String CONTACTUS_TIBCO_CALL = "ContactUsManager.requestInfoTIBCO()";
	public static final String INVENTORY_TIBCO_CALL = "InvetoryDecrementManager.requestInfoTIBCO()";
	public static final String SURVEY_TIBCO_CALL = "SurveyManager.requestInfoTIBCO()";
	public static final String SUBSCRIPTION_TIBCO_CALL = "SubscriptionManager.requestInfoTIBCO()";
	public static final String BELONG_TO_OTHER_GROUP = "BBBProfileManager.isUserBelongToOtherGroup()";
	public static final String ADD_SITE_TO_PROFILE = "BBBProfileManager.addSiteToProfile()";
	public static final String SEARCH_FRAMEWORK = "SearchManager.performSearch()";
	public static final String HANDLE_CREATE_CREDIT_CARD = "BBBProfileFormHandler.handleCreateCreditCard()";
	public static final String ADD_ITEM_TO_GIFTLIST = "BBBProfileFormHandler.addItemTOGiftList()";
	public static final String ADD_ITEM_TO_WISHLIST = "BBBProfileFormHandler.addItemTOWishList()";
	public static final String ADD_TO_ITEM_POST_LOGIN_USER = "BBBProfileFormHandler.addToItemPostLoginUser()";
	public static final String CHECK_USER_TOKEN_BVRR = "BBBProfileFormHandler.checkUserTokenBVRR()";
	public static final String ENDECA_SEARCH = "EndecaSearch.performSearch()";
	public static final String LOOKUP_US_IP_CAHE = "BBBUSIPStartUpCache.lookUpInUSIPCache()";
	public static final String BUILD_US_IP_ADDRESS_CAHE = "BBBUSIPStartUpCache.buildUSIPAddressCache()";
	public static final String BUILD_LOCAL_DYN_SKU_CAHE = "BBBLocalDynamicPriceSKUCache.buildLocalDynamicSKUCache()";
	public static final String LOOKUP_DYN_SKU_CAHE = "BBBLocalDynamicPriceSKUCache.lookUpInLocalDynSKUCache()";
	// 92F story
	public static final String PERFORM_ENDECA_SEARCH = "EndecaSearch.performEndecaSearch()";
	public static final String PARTIAL_SEARCH = "EndecaSearch.makeRepeatCallsForPartial()";
	public static final String PARTIAL_ENDECA_SEARCH = "EndecaSearch.performPartialMatchEndecaSearch()";

	public static final String ENDECA_SEARCH_GET_COLLEGES = "EndecaSearch.getColleges()";
	public static final String ENDECA_SEARCH_GET_STATES = "EndecaSearch.getStates()";
	public static final String ENDECA_FACET_SEARCH = "EndecaSearch.performFacetSearch()";
	public static final String ENDECA_FACET_SEARCH_IN_DEPT = "EndecaSearch.populateSearchInDept()";
	public static final String ENDECA_CATALOG_ID_SEARCH = "EndecaSearch.getCatalogId()";
	public static final String SEARCH_DROPLET = "SearchDroplet.service()";
	public static final String CERTONA_DROPLET = "CertonaDroplet.service()";
	public static final String OBJECT_CACHE_PUT = "BBBObjectCache.put()";
	public static final String OBJECT_CACHE_PUT_TIME = "BBBObjectCache.put(time)";
	public static final String OBJECT_CACHE_GET = "BBBObjectCache.get()";
	public static final String DROPLET_CACHE = "BBBCacheDroplet.service()";
	public static final String POPULATE_L2_BOOSTED_SEARCH_IN_DEPT = "populateL2BoostedSearchInDept()";
	public static final String CATALOG_ID = "catalogId";

	public static final String PASSWORD_NOT_MATCH = "PASSWORD_NOT_MATCH";

	// Checkout related
	public static final String PROFILE = "profile";
	public static final String PROFILE_CAPS = "PROFILE";
	public static final String BILLING_ADDRESS_CONTAINER = "billingAddrContainer";
	public static final String POSSIBLE_BILLING_ADDRESSES = "PossibleBillingAddresses";
	public static final String BILLING_ADDRESS = "billingAddress";
	public static final String SHIPPING_TAX_PRICE_INFO_KEY = "shippingTaxPriceInfo";

	// Search Registry - Gift Registry
	public static final String USER_STATUS = "userStatus";
	public static final String REGISTRY_SUMMARY_VO = "registrySummaryVO";
	public static final String REGISTRY_VO = "registryVO";
	public static final String USER_PROFILE = "profile";
	public static final String REGISTRY_IDS = "registryIds";
	public static final String OUTPUT = "output";
	public static final String SITE_ID = "siteId";
	public static final String ORDER_TYPE = "orderType";
	public static final String USER_NOT_LOGGED_IN = "1";
	public static final String USER_LOGGED_IN_WITH_NO_REGISTRIES = "2";
	public static final String USER_LOGGED_IN_WITH_SINGLE_REGISTRY = "3";
	public static final String USER_LOGGED_IN_WITH_MULTIPLE_REGISTRIES = "4";
	public static final String BBB_BUSINESS_EXCEPTION = "5";
	public static final String BBB_SYSTEM_EXCEPTION = "6";
	public static final String GIFT_REG_UPDATE_STATUS = "&status=update";
	public static final String GIFT_REG_REMOVE_STATUS = "&status=remove";

	// validation constants
	public static final String INVALID_FIRSTNAME = "Invalid firstname";
	public static final String INVALID_EMAIL = "Invalid email";
	public static final String INVALID_LASTNAME = "Invalid lastname";
	public static final String INVALID_ADDRESS1 = "Invalid address1";
	public static final String INVALID_ADDRESS2 = "Invalid address2";
	public static final String INVALID_ADDRESS3 = "Invalid Apartment Number";
	public static final String INVALID_CITY = "Invalid city";
	public static final String INVALID_ZIPCODE = "Invalid zipcode";
	public static final String NULL_BILLINGADDRESS = "No billing address is selected. Please select or add a billing address";
	public static final String INVALID_PHONE = "Invalid phone number";
	public static final String INVALID_COUNTRY_NAME = "Invalid country name";
	public static final String INVALID_STATE = "Invalid state";
	public static final String INVALID_COMPANY = "Invalid company";
	public static final String UPDATE_FAILED = "Update Failed. Please try in few minutes.";

	public enum BRIDAL_REQUEST_TYPE {
		TYPE_BRIDAL_BOOK_REGISTRATION
	};

	public enum TELLAFRIEND_REQUEST_TYPE {
		TYPE_TELL_A_FRIEND
	};

	public static final String BRIDALBOOK_TIBCO_CALL = "requestBridalBookTIBCO";
	public static final String BABYBOOK_TIBCO_CALL = "requestBabyBookTIBCO";

	public static final String DATE_FORMAT_YEAR = "yyyy";
	public static final String DATE_FORMAT_MONTH = "MM";
	public static final String DATE_FORMAT_DATE = "dd";

	// Process Coupon COnstants
	public static final String REQUEST_TYPE = "requestType";
	public static final String GET_STATUS = "getStatus";
	public static final String STATUS = "status";
	public static final String ACTIVATE = "activate";
	public static final String NOT_ACTIVATED = "Not Activated";
	public static final String PRE_ACTIVATED = "Pre Activated";
	public static final String ACTIVATION_LABEL_ID = "activationText";
	public static final String ACTIVATED = "Activated";
	public static final String ACTIVATED_LABEL_ID = "activatedText";
	public static final String CMS_CONTENT = "cmsContent";
	public static final String COUPON_STATUS = "couponStatus";
	public static final String REDEEMED = "Redeemed";
	public static final String EXPIRED = "Expired";
	public static final String EMAIL_ERROR_KEY = "EmailError";
	public static final String COUPON_ACTIVATE_ERROR = "CouponActivateError";
	public static final String COUPON_UNKNOWN_ERROR = "CouponUnknownError";
	public static final String ERROR = "error";
	public static final String SERVICE_NAME = "serviceName";
	public static final String ENTRY_CD = "entryCd";
	public static final String EMAIL_ADDR = "emailAddr";
	public static final String OLD_PASSWORD = "oldPassword";
	public static final String SITE_FLAG = "siteFlag";
	public static final String USER_TOKEN = "userToken";
	public static final String PROMOTION = "promotion";
	public static final String MEDIA = "media";
	public static final String TERMS_N_CONDITIONS_PROMOTIONS = "tandc";
	public static final String CMS_ERROR = "cmsError";
	public static final int FOUR_HUNDRED_TWO = 402;
	public static final int FOUR_HUNDRED_ONE = 401;
	public static final String PROCESS_SERVICE = "ProcessCouponDroplet.service()";
	public static final String VALIDATE_COUPON = "ProcessCouponDroplet.validateCoupon()";
	public static final String GET_VO = "ProcessCouponDroplet.getVO()";
	public static final String GET_CMS = "ProcessCouponDroplet.getCMS()";
	public static final String SITE = "site";
	public static final String ID = "id";
	public static final String SITES = "sites";
	public static final String ALREADY_REEDEMED = "alreadyReedemed";
	public static final String BUILD_REQUEST = "ProcessCouponInfoMarshaller.buildRequest()";
	public static final String GET_DOZER_MAPPED_REQUEST = "ProcessCouponInfoMarshaller.getDozerMappedRequest()";
	public static final String PROCESS_RESPONSE = "ProcessCouponInfoUnMarshaller.processResponse()";
	public static final String GET_DOZER_MAPPED_RESPONSE = "ProcessCouponInfoUnMarshaller.getDozerMappedResponse()";
	public static final String GET_DOZER_MAPPED_ERROR = "ProcessCouponInfoUnMarshaller.getDozerMappedError()";
	public static final String END_USABLE = "endUsable";
	public static final String TIME_FORMAT_COUPON = "hh:mm a z";
	public static final String DATE_FORMAT_COUPON = "dd/MM/yyyy";
	public static final String ON_STRING = " on ";
	public static final String BEGIN_USABLE = "beginUsable";
	public static final String DATA = "data";
	public static final String ENABLED = "enabled";

	// Order Substatus
	public static final String ORDER_SUBSTATUS_SUBMITTED = "SUBMITTED";
	public static final String ORDER_SUBSTATUS_UNSUBMITTED = "UNSUBMITTED";
	public static final String ORDER_SUBSTATUS_DUMMY_RESTORE_INVENTORY = "DUMMY_RESTORE_INVENTORY";
	public static final String ORDER_SUBSTATUS_DUMMY_IGNORE_INVENTORY = "DUMMY_IGNORE_INVENTORY";
	public static final String DUMMY_ORDERS_FLAG = "dummyOrdersFlag";
	public static final String DUMMY_ORDERS_INVENTORY_CHECK = "dummyOrders_inventoryCheck";
	public static final String SHIPTO_POBOXON = "shipTo_POBoxOn";
	public static final String ORDER_SUBSTATUS_FAILED = "FAILED";

	/* TIBCO Message Destinations */
	public static final String TIBCO_MSG_ORDER_SUBMIT = "submitOrderTibcoMessage";
	public static final String QUANTITY = "quantity";
	public static final String ROWID = "rowId";
	public static final String PUR_QUANTITY = "purchasedQuantity";
	public static final String REGISTRY_URL = "registryURL";
	public static final String RECIPIENT_EMAIL = "recipientEmail";
	public static final String MESSAGE = "user_message";
	public static final String SENDER_NAME = "senderName";
	public static final String SENDER_EMAIL = "senderEmail";
	public static final String EVENT_TYPE = "eventType";
	public static final String REGISTRY_ITEM_OLD_QTY = "regItemOldQty";
	public static final String PRODUCT_ID = "PRODUCT_ID";
	public static final String SKU_NOT_AVAILABLE = "skuNotAVailable";

	public static final String INVENTORY_DECREMENT_SERVICE = "inventoryJMSMessage";
	public static final String CACHE_INVALIDATION_SERVICE = "cacheInvalidatorJMSMessage";

	// Order Status Feed Constants
	public static final String ORDER_ITEM_DESCRIPTOR = "order";
	public static final String STATE_ORDER_PROPERTY_NAME = "state";
	public static final String STATE_DETAIL_ORDER_PROPERTY_NAME = "stateDetail";
	public static final String LAST_MODIFIED_DATE_ORDER_PROPERTY_NAME = "lastModifiedDate";
	public static final String SHIPPING_GROUPS_ORDER_PROPERTY_NAME = "shippingGroups";
	public static final String TRACKING_INFO_ITEM_DESCRIPTOR = "trackingInfo";
	public static final String CARRIER_CODE_TRACKING_INFO_PROPERTY_NAME = "carrierCode";
	public static final String TRACKING_NUMBER_TRACKING_INFO_PROPERTY_NAME = "trackingNumber";
	public static final String ACTUAL_SHIPDATE_TRACKING_INFO_PROPERTY_NAME = "actualShipDate";
	public static final String HARD_GOODS_SHIP_GROUP_ITEM_DESCRIPTOR = "hardgoodShippingGroup";
	public static final String STATE_DETAIL_SHIPGROUP_PROPERTY_NAME = "stateDetail";
	public static final String DESCRIPTION_SHIPGROUP_PROPERTY_NAME = "description";
	public static final String SHIPMENT_TRACKING_SHIPGROUP_PROPERTY_NAME = "shipmentTracking";
	public static final String ORDER_FEED_JMS_MESSAGE = "orderFeedJMSMessage";
	public static final String INCOMPLETE_ORDER_STATUS = "INCOMPLETE";
	public static final String REMOVED_ORDER_STATUS = "REMOVED";
	public static final String SELECT = "select";
	public static final String OUTPUT_START = "outputStart";
	public static final String NOT_APPLICABLE = "notApplicable";
	public static final String INDEX = "index";
	public static final String KEY = "key";
	public static final String ITEM = "item";
	public static final String OUTPUT_END = "outputEnd";
	public static final String CONGRATS_FREE_SHIP_MSG_SHOWN = "congratsFreeShipMsgShown";
	public static final String SUBMITTED_DATE_ORDER_PROPERTY_NAME = "submittedDate";
	public static final String SHIPMENT_QUANTITY = "shipmentQty";

	// GetCollegeProductDroplet
	public static final String COLLEGE_ID = "collegeId";
	public static final String GET_VO_2 = "getVO";

	// PromotionLookupDroplet
	public static final String PROMTION_DETAILS = "PromtionDetails";
	public static final String EMPTY = "empty";
	public static final String PROMOTION_ID = "promotionId";
	public static final String COUPON_ID = "couponId";

	// SchoolLookupDroplet
	public static final String SCHOOL_VO = "SchoolVO";
	public static final String SCHOOL_ID = "schoolId";
	public static final String SCHOOL_COOKIE = "SchoolCookie";
	// college weblink promotion
	public static final String SCHOOL_PROMOTIONS = "schoolPromotions";
	public static final String SCHOOL_IDS = "schoolIds";
	public static final String END_PROMOTION_DATE = "endUsable";
	public static final String FORMAT = "yyyy-MMM-dd HH:mm:ss";
	public static final String FAVOURITE_STORE_ID = "favouriteStoreId";
	public static final String FAVOURITE_STORE = "favouriteStore";
	public static final String SCHOOL_GENERIC_SEO = "nwp";
	public static final String ACTIVE_PROMOTIONS = "activePromotions";

	// for bazaar voice ratings and review
	public static final String BAZAAR_VOICE_ERROR = "err_bazaar_voice_biz_exception";
	public static final String DATE_FORMAT_BV = "yyyy-MM-dd";
	// public static final String BV_SHARED_KEY = "5AsvMggY";
	public static final String RETURN_PAGE = "returnPage";
	public static final String USER_TOKEN_BVRR = "userTokenBVRR";
	public static final String BV_DO_LOGIN = "BVDoLogin";
	public static final String BV_DO_WRITE_REVIEW = "BVDoWriteReview";
	public static final String BV_WRITE_REVIEW_URL = "bvWriteReviewUrl";
	public static final String DATE = "date";
	public static final String USER_ID = "userid";
	public static final String UTF_8 = "UTF-8";
	public static final String MD_5 = "MD5";

	// Used in MessageServiceListener.java for Messaging framework
	public static final String QUEUE = "Queue";
	public static final String TOPIC = "Topic";

	// profile repository BBBGetProfileInfoWebservices
	public static final String MOBILENUMBER = "mobileNumber";
	public static final String PHONENUMBER = "phoneNumber";
	public static final String USER = "user";
	public static final String TIME_STAMP = "timeStamp";
	public static final String CURR_SITE = "currSite";
	public static final String TOKEN = "token";
	public static final String PAYERID = "payerId";
	public static final String IP_ADDRESS = "ipaddress";
	public static final String TOKEN_STRING = "?token=";
	public static final String SITE_STRING = "&siteId=";
	public static final String SUCCESS = "success";
	public static final String FAIL = "fail";
	public static final String TOKEN_TIMEOUT = "Token Timeout";
	public static final String INVALID_TOKEN = "Invalid token or Ip or site";
	public static final int THREE = 3;
	public static final int TWO = 2;
	public static final String HOMEADDRESS = "homeAddress";
	public static final int SIXTY = 60;
	public static final int THOUSAND = 1000;
	public static final int TEN_THOUSAND = 10000;

	// for referrals like wedding channel, commission junction
	public static final String REFERRAL_IDS = "referralIds";
	public static final String REF_LINK = "refLink";
	public static final String WCS_ID = "wcsid";
	public static final String REFERRER = "Referer";
	public static final String COM_MATRIX_PARAM = "utm_source";
	public static final String COM_JUN_REF = "cj";
	public static final String WED_CHANNEL_REF = "wc";
	public static final String WEDCHANEEL_BUMP_REF_VAL = "wcref=yes";
	public static final String RKG_REF = "rkg";
	public static final String THEBUMP_REF = "bp";
	public static final String COM_JUNCTION_PREFIX = "CJ";
	public static final String PRODUCT_URI = "/product/";
	public static final String REF = "ref";
	public static final String COM = ".com";
	public static final String REF_URL = "refUrlParam";
	public static final String REF_URL_VO = "refVO";
	public static final String WC_REG_URL = "wcRegUrl";
	public static final String BP_REG_URL = "bpRegUrl";
	public static final String WC_ITEM_URL = "wcItemUrl";
	public static final String BP_ITEM_URL = "bpItemUrl";
	public static final String WC_REG_PARAM = "wcRegParam";
	public static final String BP_REG_PARAM = "bpRegParam";
	public static final String WC_SALE_URL = "wcSaleUrl";
	public static final String BP_SALE_URL = "bpSaleUrl";
	public static final String WC_SALE_PARAM = "wcSaleParam";
	public static final String BP_SALE_PARAM = "bpSaleParam";
	public static final String CJ_SALE_URL = "cjSaleUrl";
	public static final String CJ_SALE_PARAM = "cjSaleParam";
	public static final String CJ_ITEM_URL = "cjItemUrl";
	public static final String CJ_BOPUS_ONLY_ORDER = "cjBopusOnly";
	public static final String CJ_SKUIDS = "cjSkuIds";
	public static final String CJ_SKUPRICES = "cjSkuPrices";
	public static final String CJ_SKUQTY = "cjSkuQty";
	public static final String REF_ID = "refId";
	public static final String TODAY_DATE = "todayDate";
	public static final String REF_PARAM = "refParam";
	public static final String RETAILER_UID = "retailer_uid";
	public static final String RETAILER_REGISTRY_ID = "retailer_registry_id";
	public static final String WC_REF_REG_URL = "wc_referral_registry_url";
	public static final String WC_REF_SALE_URL = "wc_referral_sale_url";
	public static final String RKG_PRODUCT_NAMES = "rkgProductNames";
	public static final String RKG_PRODUCT_IDS = "rkgProductIds";
	public static final String RKG_PRODUCT_COUNT = "rkgProductCount";
	public static final String RKG_PROMOTIONS = "rkgPromotions";
	public static final String RKG_PROD_CATEGORYID_L1 = "rkgProdCatIdL1";
	public static final String RKG_PROD_CATEGORY_NAME_L1 = "rkgProdCatNameL1";
	public static final String RKG_PROD_CATEGORYID_L2 = "rkgProdCatIdL2";
	public static final String RKG_PROD_CATEGORY_NAME_L2 = "rkgProdCatNameL2";
	public static final String RKG_PROD_CATEGORYID_L3 = "rkgProdCatIdL3";
	public static final String RKG_PROD_CATEGORY_NAME_L3 = "rkgProdCatNameL3";
	public static final String WC_REGISTRY_IDS = "wcRegistryIds";
	// for bumps.com referral registry URL
	public static final String BP_REF_REG_URL = "bp_referral_registry_url";
	// for bumps.com referral sale URL
	public static final String BP_REF_SALE_URL = "bp_referral_sale_url";
	public static final String CJ_REF_SALE_URL = "cj_referral_sale_url";
	public static final String THIRD_PARTY_URL = "ThirdPartyURLs";
	public static final String HARTE_AND_HANKS_URL = "harteAndHanksURL";

	// RKG Tag
	public static final String RKG_CONFIG_TYPE = "RKGKeys";
	public static final String RKG_ITEM_URL = "rkgItemUrl";
	public static final String RKG_COMPARISON_ITEM_URL = "rkgComparisonItemUrl";
	public static final String RKG_MERCHANT_ID = "rkgMerchantId";
	public static final String RKG_DATE_PATTERN = "yyyyMMddhhmmss";
	public static final String RKG_REF_SALE_URL = "rkg_referral_sale_url";
	public static final String RKG_SALE_URL = "rkgSaleUrl";
	public static final String RKG_SALE_PARAM = "rkgSaleParam";

	public static final String REFERRAl_CONTROLS = "ReferralControls";
	public static final String REFERRAl_CONTROLS_REG_EVENTS = "ReferralControls_RegistryEvents";
	public static final String REFERRAL_STRINGS = "referralStrings";
	public static final String REFERRAL_URL_PATTERN = "referralURLPatterns";
	public static final String REFERRER_PATTERNS = "referrerPatterns";
	public static final String THE_BUMP_REFERRER = "the_bump_referrer";
	public static final String WEDDING_CHANNEL_REFERRER = "wedding_channel_referrer";
	public static final String BABY_CANADA_SOURCE_URL = "BabyCanada_Source_URL";
	public static final String BABY_CANADA_TARGET_URL = "BabyCanada_Target_URL";

	// Commission junction
	public static final String CJ_REF_URL_PARAM = "cj_ref_url_param";
	public static final String CJ_REF_URL_PARAM_PREFIX = "cj_ref_url_param_prefix";
	public static final String CJ_REF_URL_PARAM_SPLITTER = "cj_ref_url_param_split";
	public static final String CJ_CID_PARAM = "CID";
	public static final String REFFERAL_PARAMS_MAP = "paramValueMap";
	public static final String CJ_SALE_REQ_PARAMS = "cj_sale_params";
	// wedding channel
	public static final String WC_CREATE_REGISTRY_PARAMS = "wc_create_reg_params";
	public static final String WC_SALE_REGISTRY_PARAMS = "wc_sale_reg_params";
	public static final String WC_PRODUCT_SALE_PARAMS = "wc_prod_sale_reg_params";
	public static final String GRAND_ORDER_TOTAL = "grandTotal";
	public static final String WC_REFERRER_PATTERN = "wc_referrer_pattern";

	// bumps
	public static final String BP_CREATE_REGISTRY_PARAMS = "bp_create_reg_params";
	public static final String BP_SALE_REGISTRY_PARAMS = "bp_sale_reg_params";
	public static final String BP_PRODUCT_SALE_PARAMS = "bp_prod_sale_reg_params";
	public static final String BP_REFERRER_PATTERN = "bp_referrer_pattern";

	// icrossing
	public static final String SUBSCRIBE = "subscribe";
	// TellAPart
	public static final String RETURNING_USER = "returningUser";

	public static final String HTTPS = "https";

	public static final String OMNITURE_VO = "omnitureVO";
	public static final String ADDED = "added";
	public static final String MOVED = "moved";
	public static final String UPDATED = "updated";
	public static final String REMOVED = "removed";

	// Email Template Constants
	public static final String PLACE_HOLDER = "placeHolderValues";
	public static final String EMAIL_TYPE = "emailType";
	public static final String ET_ORDER_CONFIRMATION = "OrderConfirmation";
	public static final String ET_CREATE_ACCOUNT = "ProfileCreation";
	public static final String ET_FORGOT_PASSWORD = "ForgotPassword";
	public static final String ET_BRIDAL = "BridalBookTellAFriend";
	public static final String ET_ContactUs = "ContactUs";
	public static final String FORM_CONTACT_METHOD = "frmData_ContactMethod";
	public static final String FORM_MESSAGE = "frmData_ContactMessage";
	public static final String FORM_PHONE = "frmData_Phone";
	public static final String FORM_TIME_TO_CALL = "frmData_timeToCall";
	public static final String FORM_SITE = "frmData_siteId";
	public static final String FORM_FNAME = "frmData_firstName";
	public static final String FORM_LNAME = "frmData_lastName";
	public static final String FORM_EMAIL = "frmData_email";
	public static final String FORM_ORDER = "frmData_order";
	public static final String FORM_PHONE_NUMBER = "frmData_phoneNumber";
	public static final String FORM_MOBILE_NUMBER = "frmData_mobileNumber";
	public static final String FORM_OPTIN = "frmData_optin";
	public static final String FORM_PROFILEID = "frmData_profileId";
	public static final String FORM_SHARE_ACCOUNT = "frmData_shareAccount";
	public static final String FORM_REGISTRANT_NAME = "frmData_registrantName";
	public static final String FORM_EVENT_TYPE = "frmData_eventType";
	public static final String FORM_REG_ID = "frmData_registryId";

	public static final String FORM_FRND_FNAME = "frmData_friendFirstName";
	public static final String FORM_FRND_LNAME = "frmData_friendLastName";
	public static final String FORM_NEW_PASSWORD = "newPassword";
	public static final String SERVER = "server";
	public static final String ERROR_MESSAGES = "errorMessages";
	public static final String REGISTRY = "registry";
	public static final String AFFILIATE_MOM365_COOKIE_KEY = "rdMOM365";
	public static final String EMAIL_SUBJECT = "emailSubject";
	public static final String EMAIL_PERSIST_ID = "emailPersistId";

	// profile properties
	public static final String FIRST_NAME = "firstName";
	public static final String MIDDLE_NAME = "middleName";
	public static final String LAST_NAME = "lastName";
	public static final String PHONE_NUM = "phoneNumber";
	public static final String MOBILE_NUM = "mobileNumber";
	public static final String CONTACT_INFO = "contactInfo";
	public static final String TIBCO_KEYS = "TibcoKeys";

	public static final String VALIDATE_OLD_ACCOUNT = "validateOldAccount";
	public static final String DEFAULT_PASSWORD = "DEFAULT_PASSWORD";
	public static final String SHOW_POP_UP = "?showMigratedPopup=true";
	public static final String FROM_RESET = "?fromReset=true";
	public static final String ADD_TOKEN = "?token=";
	public static final String DEFAULT_LOCALE = "EN";
	public static final String EXPIRED_DATE = "(NO EXPIRED DATE AVAILABLE)";
	public static final String ET_BABY = "BabyBookTellAFriend";
	public static final String FORM_BABY_BOOK_ID = "frmData_babyBookId";
	public static final String REQUEST_TELL_A_FRIEND_TIBCO = "requestTellAFriendTIBCO";
	public static final String BABYPOPUP = "?showBridalBookSuccessPopup=";
	public static final String RECIPIENT_FIRST_NAME = "recipientFirstName";
	public static final String RECIPIENT_LAST_NAME = "recipientLastName";
	public static final String SENDER_FIRST_NAME = "senderFirstName";
	public static final String SENDER_LAST_NAME = "senderLastName";

	public enum BABY_BOOK_REQUEST_TYPE {
		TYPE_BABY_BOOK_REGISTRATION
	};

	public enum HEALTHY_WOMAN_REGISTER_REQUEST_TYPE {
		TYPE_HEALTHY_WOMAN_REGISTER_REQUEST_TYPE
	};

	public static final String ALLREADY_ACTIVE = "Already Activated";
	public static final String MINI_CART = "?showMiniCartFlyout=true";
	public static final String WISHLIST_ERROR = "?showMoveToCartError=true";
	public static final String STORE_TYPE = "store_type=";
	public static final String MAPQUESTSTORETYPE = "MapQuestStoreType";
	public static final String BABY_CANADA_STORE_COUNT = "babyCanadaStore";
	public static final String BED_BATH_CANADA_STORE_COUNT = "bedBathCanadaStore";
	public static final String STORE_MILES = "storeMiles";
	public static final String STORE_SEARCH_STRING = "inputSearchString";
	public static final String PRODUCT_DIM_ID_CACHE_NAME = "PRODUCT_DIM_ID_CACHE_NAME";
	public static final String SEARCH_RESULT_CACHE_NAME = "SEARCH_RESULT_CACHE_NAME";
	public static final String BOOSTED_PRODUCTS_CACHE_NAME = "BOOSTED_PRODUCTS_CACHE_NAME";
	public static final String BOOSTING_STRATEGY_CACHE_NAME = "BOOSTING_STRATEGY_CACHE_NAME";
	public static final String BOOSTING_STRATEGY_CACHE_NAME_VALUE = "boosting-strategy-near-cache";
	public static final String BOOSTING_STRATEGY_CACHE_TIMEOUT = "BOOSTING_STRATEGY_CACHE_TIMEOUT";
	public static final String REG_POPULAR_ITEMS_CACHE_NAME = "REG_POPULAR_ITEMS_CACHE_NAME";
	public static final String REG_POPULAR_ITEMS_MAX_COUNT = "REG_POPULAR_ITEMS_MAX_COUNT";
	public static final String KEYWORD_SEARCH_CACHE_NAME = "KEYWORD_SEARCH_CACHE_NAME";
	public static final String KEYWORD_SEARCH_MATCHALL_CACHE_NAME = "KEYWORD_SEARCH_MATCHALL_CACHE_NAME";
	public static final String HEADER_FLYOUT_CACHE_NAME = "HEADER_FLYOUT_CACHE_NAME";
	public static final String MAX_CACHE_KEYS_LIMIT = "MAX_CACHE_KEYS_LIMIT";
	public static final String CHECKLIST_PLP_SEO_URL_CACHE_NAME = "CHECKLIST_PLP_SEO_URL_CACHE_NAME";
	public static final String CHECKLIST_PLP_SEO_URL_CACHE_TIMEOUT = "CHECKLIST_PLP_SEO_URL_CACHE_TIMEOUT";

	// CACHE NAME FOR MOBILE CMS NAV
	public static final String MOBILE_NAV_FLYOUT_CACHE_NAME = "MOBILE_HEADER_FLYOUT_CACHE_NAME";
	// CACHE NAME FOR Popular Keywords
	public static final String POPULAR_KEYWORDS_CACHE_NAME = "POPULAR_KEYWORDS_RESULT_CACHE_NAME";
	// CACHE NAME FOR MOBILE CMS NAV
	public static final String MOBILE_ALL_TOP_CATEGORIES_CACHE_NAME = "MOBILE_TOP_CATS_RESULT_CACHE_NAME";

	// cache constants for Clearance Products
	public static final String CLEARANCE_PRODS_CACHE_NAME = "CLEARANCE_PRODS_CACHE_NAME";
	public static final String CLEARANCE_PRODS_CACHE_TIMEOUT = "CLEARANCE_PRODS_CACHE_TIMEOUT";

	public static final String CATEGORY_LANDING_CACHE_NAME = "CATEGORY_LANDING_CACHE_NAME";
	public static final String DROPLET_CACHE_NAME = "DROPLET_CACHE_NAME";
	public static final String MAPQUEST_CACHE_NAME = "MAPQUEST_CACHE_NAME";
	public static final String COLLEGE_CACHE_NAME = "COLLEGE_CACHE_NAME";
	public static final String COLLEGE_DETAIL_CACHE_NAME = "COLLEGE_DETAIL_CACHE_NAME";
	public static final String OBJECT_CACHE_CONFIG_KEY = "ObjectCacheKeys";
	public static final String INVENTORY_COUNT_CACHE_FLAG = "INVENTORY_COUNT_CACHE_FLAG";
	public static final String INVENTORY_COUNT_CACHE_NAME = "INVENTORY_COUNT_CACHE_NAME";
	public static final String INVENTORY_COUNT_CACHE_TIMEOUT = "INVENTORY_COUNT_CACHE_TIMEOUT";
	public static final String DISABLE_COLLECTION_PARENT_CACHE = "disableCollectionParentCache";
	public static final String COHERENCE_CACHE_CONFIG_KEY = "CoherenceCacheKeys";
	public static final String MOBILE_COHERENCE_CACHE_CONFIG_KEY = "ServiceCacheMapType";
	public static final String PRODUCT_DIM_ID_CACHE_TIMEOUT = "PRODUCT_DIM_ID_CACHE_TIMEOUT";
	public static final String SEARCH_RESULT_CACHE_TIMEOUT = "SEARCH_RESULT_CACHE_TIMEOUT";
	public static final String KEYWORD_SEARCH_CACHE_TIMEOUT = "KEYWORD_SEARCH_CACHE_TIMEOUT";
	public static final String HEADER_FLYOUT_CACHE_TIMEOUT = "HEADER_FLYOUT_CACHE_TIMEOUT";
	public static final String CATEGORY_LANDING_CACHE_TIMEOUT = "CATEGORY_LANDING_CACHE_TIMEOUT";
	public static final String DROPLET_CACHE_TIMEOUT = "DROPLET_CACHE_TIMEOUT";
	public static final String MAPQUEST_CACHE_TIMEOUT = "MAPQUEST_CACHE_TIMEOUT";
	public static final String COLLEGE_CACHE_TIMEOUT = "COLLEGE_CACHE_TIMEOUT";
	public static final String CLEAR_DROPLET_CACHE_ON_DEPLOYMENT = "ClearDropletCacheOnDeployment";
	public static final String CLEAR_MAPQUEST_CACHE_ON_DEPLOYMENT = "ClearMapquestCacheOnDeployment";
	public static final String OBJECT_CACHING_ENABLED = "OBJECT_CACHING_ENABLED";
	public static final String DROPLET_CACHING_ENABLED = "DROPLET_CACHING_ENABLED";
	public static final String INVALIDATE_FLYOUT_CACHE_ON_PIM_DEPLOYMENT = "invalidateFlyoutCacheOnPIMDeployment";
	public static final String CLEAR_LOCAL_CACHE_FLAG = "clearLocalCacheFlag";

	public static final String COLLECTION_CHILD_RELN_CACHE_NAME = "COLLECTION_CHILD_RELATION_CACHE_NAME";

	// FLAG TO INVALIDATE MOBILE CMS FLYOUT CACHE
	public static final String INVALIDATE_MOBILE_CMSFLYOUT_CACHE = "invalidateMobileFlyoutCache";
	// Flag to invalidate Popular Keywords Cache
	public static final String INVALIDATE_POPULAR_KEYWORDS_CACHE = "invalidatePopularKeywordsCache";
	// FLAG TO INVALIDATE MOBILE TOP CATEGORIES CACHE
	public static final String INVALIDATE_MOBILE_TOP_CATEGORIES_CACHE = "invalidateMobTopCategoriesCache";

	public static final String INVALIDATE_OBJECT_CACHE_NAMES = "InvalidateObjectCacheNames";
	public static final String SLASH = "/";
	public static final String DOUBLE_SLASH = "//";

	public static final String TILDE = "~";
	public static final String IMAGE_HOST = "image_host";
	public static final String SCENE7_URL = "scene7_url";
	public static final String HOST_URL = "host_url";
	public static final String FRMDATA_IMAGE_URL = "frmData_imageURL";
	public static final String FRMDATA_HOST_URL = "frmData_hostURL";
	public static final String FRMDATA_HOSTURL = "frmdata_hosturl";
	public static final String FRMDATA_babyCanadaUrl = "babyCanadaUrl";
	public static final String SHIP_METHOD_STANDARD_ID = "3g";
	public static final String SHIP_METHOD_EXPEDIATED_ID = "2a";
	public static final String SHIP_METHOD_EXPRESS_ID = "1a";

	public static final String RECLAME_URL_PARAMETER_1 = "?showLegacyPwdPopup=true&";
	public static final String QUESTION_MARK = "?";
	public static final String ADDITEMFLAGERROR = "additemflagerror=";
	public static final String SHOWPOPUP = "&showpopup=true";
	public static final String SHOWPOPUP_FALSE = "&showpopup=false";
	public static final String ADDITEMFLAGERROR_SHOWPOPUP = "additemflagerror=true&showpopup=true";
	public static final String ADDITEMFLAGERROR_SHOWPOPUP_2 = "additemflagerror=true&showpopup=true";
	public static final String FBCONNECTFORMHANDLER_HANDLEFBLOGIN = "FBConnectFormHandler.handleFbLogin()";
	public static final String BBBPROFILEFORMHANDLER_HANDLEVALIDATE = "BBBProfileFormHandler.handleValidateAndExtendAccount";
	public static final String HANDLEEXTENDACCOUNT = "BBBProfileFormHandler.handleExtendAccount";
	public static final String ENSURETRANSACTION = "BBBProfileFormHandler.ensureTransaction";
	public static final String AUTOSIGNIN = "BBBProfileFormHandler.autoSignIn";

	// SearchStoreDroplet
	public static final String STOREID = "storeId";
	public static final String PAGEKEY = "pageKey";
	public static final String PAGENUMBER = "pageNumber";
	public static final String SEARCHSTRING = "searchString";
	public static final String SEARCHTYPE = "searchType";
	public static final String SEARCHBASEDON = "searchBasedOn";
	public static final String ORIGINPARAM = "&origin=";
	public static final String RADIUSPARAM = "&radius=";
	public static final String ADDRESSSUGGESTION = "addressSuggestion";
	public static final String STOREDETAILSWRAPPER = "StoreDetailsWrapper";
	public static final String ERRORMESSAGE = "errorMessage";
	public static final String PAGESIZE = "pageSize";
	public static final String STATICMAPKEY = "staticMapKey";
	public static final String INPUTSEARCHSTRING = "inputSearchString";
	public static final String STOREDETAILS = "StoreDetails";
	public static final String CLOSESTSTOREDETAILS = "closestStoreDetails";
	public static final String STOREDETAILS_FOR_REGISTLRY = "StoreDetailsRegistry";
	public static final String RADIUS = "radius";
	public static final String UNITSPARAM = "&units=";
	public static final String UNITS = "units";
	public static final String POBoxPattern = "(?i)([\\w\\s*\\W]*(P(OST)?\\.?\\s*((O(FF(ICE)?)?)?\\.?\\s*(B(IN|OX)?\\.?\\b))+))[\\w\\s*\\W]*";
	public static final String[] ADDRESSPROPERTIES = new String[] { "firstName", "middleName", "lastName", "address1",
			"address2", "city", "state", "postalCode", "ownerId", "companyName", "nickname" };
	public static final String[] CARDPROPERTIES = new String[] { "creditCardNumber", "creditCardType",
			"expirationMonth", "expirationYear", "billingAddress", "nameOnCard" };
	public static final String NICKNAME = "nickname";
	public static final String ADDRESSID = "addressId";
	public static final String SHIPPINGADDRNICKNAME = "shippingAddrNickname";
	public static final String[] IMMUTABLECARDPROPERTIES = new String[] { "creditCardNumber", "creditCardType" };
	public static final String BRIDALPOPUP = "?showBridalBookSuccessPopup=";
	public static final String COMMA_WITH_SPACE = ", ";
	public static final String MINUS_ONE = "-1";
	public static final String MINUS_ZERO = "-0";

	public static final String SITE_BBB_IMG_LOC = "bbbaby";
	public static final String SITE_BBB_US_IMG_LOC = "bbbeyond";
	public static final String SITE_BBB_CA_IMG_LOC = "bbbeyondCA";
	public static final ParameterName DEFAULT_ID_2 = ParameterName.getParameterName("defaultId2");

	// GiftList
	public static final String SKUID = "skuId";
	public static final String PRODID = "prodId";
	public static final String PRICE = "price";
	public static final String QTY = "qty";
	public static final String WISHLISTID = "id";

	public static final String DOLLORWITHSLASH = "\\$";
	public static final String DOLLOR = "$";
	public static final String OMNI_SYMBOL_1 = ";;;event38=";

	public static final String OMNI_SYMBOL_2 = "|event39=";
	public static final String PARENTPRODID = "parentProdId";
	public static final String ADDITEMRESULTS = "addItemResults";

	// label constant
	public static final String PHONE_NUMBER_IS_INVALID = "PHONE_NUMBER_IS_INVALID";
	public static final String ERR_PROFILE_EMAIL_EMPTY = "err_profile_email_empty";
	public static final String ERROR_OCCURR_LINKING_FB = "error_occurr_linking_fb";
	public static final String MOBILE_NUMBER_IS_INVALD = "MOBILE_NUMBER_IS_INVALD";
	public static final String ERR_RESET_LINK = "err_reset_link";
	public static final String ERR_RESET_EMAIL = "err_reset_email";
	public static final String RESET_FLAG = "resetFlag";
	public static final String LOGIN_EMAIL_ID = "loginemailid";
	public static final String URL_TOKEN = "urlToken";
	public static final String FORGOT_PWD_TOKEN = "ForgotPwdToken";
	public static final String PASSWORD_SALT = "passwordSalt";
	public static final String VALUE = "value";

	public static final String MSG_SELECT_RADIO_OPTION = "err_cc_select_billing_addr";
	public static final String ERROR_DUPLICATE_CC_NICKNAME = "error_duplicate_cc_nickname";

	public static final String ERR_ADDRESSBOOK_ADDITIONERROR = "err_addressbook_additionerror";
	public static final String ERR_ADDRESSBOOK_UPDATIONERROR = "err_addressbook_updationerror";
	public static final String ERR_CREATE_REG_PHONE_NUMBER_IS_INVALID = "err_create_reg_phone_number_is_invalid";
	public static final String ERR_LEGACY_PASSWORD_ERROR = "err_legacy_Password_Error";
	public static final String ERR_LOGIN_FRAG_TECH_ISSUE = "err_login_frag_tech_issue";
	public static final String ERR_LOGIN_PASSWORD_ERROR = "err_login_Password_Error";
	public static final String ERR_MOBILE_NUMBER_IS_INVALID = "err_mobile_number_is_invalid";
	public static final String ERR_PROFILE_CONFIRM_PASSWORD_EMPTY = "err_profile_confirm_password_empty";
	public static final String ERR_PROFILE_CONFIRMPASSWORD_FIELD_EMPTY = "err_profile_confirmpassword_field_empty";
	public static final String ERR_PROFILE_FIRSTNAME_FIELD_EMPTY = "err_profile_firstname_field_empty";
	public static final String ERR_PROFILE_FIRSTNAME_INVALID = "err_profile_firstname_invalid";
	public static final String ERR_PROFILE_LASTNAME_FIELD_EMPTY = "err_profile_lastname_field_empty";
	public static final String ERR_PROFILE_LASTNAME_INVALID = "err_profile_lastname_invalid";
	public static final String ERR_PROFILE_LOGIN_FIELD_EMPTY = "err_profile_login_field_empty";
	public static final String ERR_PROFILE_OLDPASSWORD_FIELD_EMPTY = "err_profile_oldpassword_field_empty";
	public static final String ERR_PROFILE_PASSWORD_CONFIRMPASSWORD_NOTEQUAL = "err_profile_password_confirmpassword_notequal";
	public static final String ERR_PROFILE_PASSWORD_CONTAINS_NAME = "err_profile_password_contains_name";
	public static final String ERR_PROFILE_PASSWORD_EMPTY = "err_profile_password_empty";
	public static final String ERR_PROFILE_PASSWORD_FIELD_EMPTY = "err_profile_password_field_empty";
	public static final String ERR_PROFILE_PASSWORD_OLD_NEW_NOTEQUAL = "err_profile_password_old_new_notequal";
	public static final String ERR_PROFILE_PASSWORDNOTIN_FNAMEORLNAME = "err_profile_passwordnotin_fnameorlname";
	public static final String ERR_PROFILE_INVALID_RESET_PASSWORD_TOKEN = "err_profile_invalid_reset_password_token";
	public static final String ERR_PROFILE_EMPTY_RESET_PASSWORD_TOKEN = "err_profile_empty_reset_password_token";
	public static final String ERR_PROFILE_REPOSITORY_PROFILE_TYPE_FETCHED_NULL = "err_profile_repository_profile_type_fetched_null";
	public static final String ERR_PROFILE_SYSTEM_ERROR_SHARE_SITE = "err_profile_system_error_share_site";
	public static final String ERR_RECLAIM_ACCOUNT_NOT_FOUND = "err_reclaim_account_not_found";
	public static final String ERR_RECLAIM_ACCOUNT_TECH_ERROR_FORGET_PASSWORD = "err_reclaim_account_tech_error_forget_password";
	public static final String ERR_RECLAIM_ACCOUNT_UNABLE_TO_RECLAIM = "err_reclaim_account_unableto_reclaim";
	public static final String ERR_SUBSCRIBTION_FIRSTNAME_INVALID = "err_subscribtion_firstname_invalid";
	public static final String ERR_UPDATEPROFILE = "err_updateprofile";
	public static final String ERR_UPDATEPROFILE_EMAILALREADYEXIST = "err_updateprofile_emailalreadyexist";
	public static final String ERR_UPDATEPROFILE_LOGINFIRST = "err_updateprofile_loginfirst";
	public static final String ERR_CHECKOUT_INVALID_CARD_NAME = "err_checkout_invalidCardName";
	public static final String ERR_RECLAIM_ACCOUNT = "err_reclaim_account_tech_error_reclaim_account";
	public static final String ERR_PROFILE_EMAIL_INVALID = "err_profile_email_invalid";
	public static final String ERR_PASSWORD_EMPTY_ERROR = "err_password_Empty_Error";
	public static final String ERR_ACCOUNT_LOCKED_ERROR = "err_account_locked_error";
	public static final String ERR_CC_SYS_EXCEPTION = "err_cc_sys_exception";
	public static final String ERR_CC_BSYS_EXCEPTION = "err_cc_bsys_exception";
	public static final String ERR_CHECKOUT_LEGACY_USER_MOB = "err_checkout_legacy_user_mob";
	public static final String ERR_LEGACY_USER_LOGIN_MOB = "err_legacy_user_login_mob";
	public static final String ERR_ADDRESSBOOK_REMOVEADDRESS = "err_addressbook_removeaddress";
	public static final String ERR_PROFILE_FIRSTNAME = "err_profile_firstname";
	public static final String ERROR_INVALID_CARD_NAME = "error_invalid_card_name";
	public static final String ERR_CC_NOC_LENGTH_INCORRECT = "err_cc_noc_length_incorrect";
	public static final String ERR_CC_NUMBER_LENGTH_INCORRECT = "err_cc_number_length_incorrect";
	public static final String ERR_CC_EXP_YEAR_EMPTY = "err_cc_exp_year_empty";
	public static final String ERR_CC_EXP_MONTH_EMPTY = "err_cc_exp_month_empty";
	public static final String ERR_CC_NOC_EMPTY = "err_cc_noc_empty";
	public static final String ERR_CC_TYPE_EMPTY = "err_cc_type_empty";
	public static final String ERR_CC_NUMBER_EMPTY = "err_cc_number_empty";
	public static final String ERR_LOGIN_EMPTY_ERROR = "err_login_Empty_Error";
	public static final String ERR_RECLAIM_ACCOUNT_WS_ERR = "err_reclaim_account_ws_err";
	public static final String ERR_RECLAIM_ACCOUNT_INVALID_EMAIL = "err_reclaim_account_invalid_email";
	public static final String ERR_PROFILE_LOOKUP_USER_ERROR = "err_profile_lookup_user_error";
	public static final String ERR_PROFILE_SYSTEM_EMAIL_TEMPLATE_NOT_SET = "err_profile_system_email_template_not_set";
	public static final String ERR_USER_ALREADY_ASSOCIATED_TO_SITE = "err_user_already_associated_to_site";
	public static final String ERR_CC_PROFILE_EMAILALREADYEXIST = "err_cc_profile_emailalreadyexist";
	public static final String ERR_CHANGE_PASSWORD_NOTIFICATION = "err_change_password_notification";
	public static final String ERR_CHANGE_PASSWORD_NOTIFICATION_LEGACY = "err_change_password_notification_legacy";

	public static final String ERR_CC_LAST_NAME_EMPTY = "err_cc_last_name_empty";
	public static final String ERR_COMPANY_LENGTH_INCORRECT = "err_company_length_incorrect";
	public static final String ERR_CC_ADDR1_EMPTY = "err_cc_addr1_empty";
	public static final String ERR_CC_ADDR2_EMPTY = "err_cc_addr2_empty";
	public static final String ERR_CC_STATE_EMPTY = "err_cc_state_empty";
	public static final String ERR_CC_CITY_EMPTY = "err_cc_city_empty";
	public static final String ERR_CC_ZIP_EMPTY = "err_cc_zip_empty";
	public static final String ERR_CC_COUNTRY_EMPTY = "err_cc_country_empty";

	public static final String LENGTHINCORRECT = "LENGTH INCORRECT";
	public static final String ERR_PROFILE_INVALID = "err_profile_invalid";
	public static final String ERR_CC_ORDER_NOT_EXIST = "err_cc_order_not_exist";
	public static final String ERR_PROFILE_SEND_EMAIL = "err_profile_send_email";
	public static final String ENCODING_TYPE = "encodingType";
	public static final String ENCODED_URL = "encodedURL";
	public static final String UTF_ENCODING = "UTF-8";
	public static final String QUERY_STRING = "queryString";

	public static final String UPDATE_ALL_INVENTORY_CONFIG = "UpdateAllInventory";
	public static final String SECONDARY_ADDRESS = "secondaryAddresses";
	public static final String REGISTRY_SUCCESS_BACK_BTN_URL = "Registry_Success_Back_Btn_Url";
	public static final String REGISTRY_SUCCESS_BACK_BTN_URL_FLAG = "registry_Success_Back_Btn_Url_Flag";
	public static final String REGISTRY_BACK_BTN_FLAG_REDIRECT_ENABLED = "redirectEnabled";
	public static final String REGISTRY_BACK_BTN_FLAG_REDIRECT_SET = "redirectSet";
	public static final String REGISTRY_BACK_BTN_FLAG_REDIRECT_NONE = "none";
	public static final String SCHEDULED_FEEDS_CONFIG_TYPE = "ScheduledFeedsConfig";
	public static final String SCHEDULED_FEEDS_PATH_CONFIG_KEY = "thirdpartyFeedsPath";
	public static final String COOKIE_USED_ONCE = "cookie_used_once";
	public static final String SAVE4LATER_COOKIE = "Save4LaterCookie";

	public static final String SESSION_BEAN = "/com/bbb/profile/session/SessionBean";

	public static final String QUESTION_MARK_2 = "\\?";
	public static final String SMALL_IMAGE = "smallImage";
	public static final String THUMBNAIL_IMAGE = "thumbnailImage";
	public static final String HOME_PAGE_URL = "/store/cms/homepage.jsp";

	public static final String CLIENT_ID = "Client ID:";

	public static final String CLIENT_ID_PARM = "clientID";
	public static final String ATG_REST_IGNORE_REDIRECT = "atg-rest-ignore-redirect";
	public static final String GIFT_LIST_ITEMS = "giftlistItems";
	public static final String GIFT_ITEM = "gift-item";
	public static final String CATALOG_REF_ID = "catalogRefId";
	public static final String CHANNEL = "X-bbb-channel";
	public static final String CHANNEL_THEME = "X-bbb-channel-theme";
	public static final String HEADER_STORE_ID = "X-bbb-store";
	public static final String MOBILEWEB = "MobileWeb";
	public static final String MOBILEAPP = "MobileApp";
	public static final String THIRD_PARTY_MOBILE_APP = "ThirdPartyMobileApp";
	public static final String DEFAULT_CHANNEL_VALUE = "DesktopWeb";
	public static final String CHANNEL_DESCRIPTOR = "channelInfo";
	public static final String CHANNEL_RQL = "channelName=?0";
	public static final String CHANNEL_REPOSITORY = "/com/bbb/channel/ChannelRepository";

	// store timings constants
	public static final String MONDAY = "Monday";
	public static final String TUESDAY = "Tuesday";
	public static final String WEDNESDAY = "Wednesday";
	public static final String THURSDAY = "Thursday";
	public static final String FRIDAY = "Friday";
	public static final String SATURDAY = "Saturday";
	public static final String SUNDAY = "Sunday";
	// stofu email
	public static final String SKU_ID_STOFU = "skuId";
	public static final String CHANNEL_ID = "channelId";
	public static final String PRODUCT_AVAILABILITY_FLAG = "productAvailabilityFlag";
	public static final String RECIPIENT_LIST = "RecipientList";
	public static final String REVIEW_RATING = "reviewRating";
	public static final String STORE_ID = "storeId";
	public static final String GOOD_TO_KNOW = "goodToKnow";
	public static final String SIBLING_PRODUCTS = "siblingProducts";
	public static final String PRICE_RANGE_DESCRIPTION = "PriceRangeDescription";
	public static final String STOREVO = "storeVO";
	public static final String PRODUCTVO = "productVo";
	public static final String TABLE_REGISTRY_CART_MAP = "tableRegistryCartMap";
	public static final String COMPARE_ARRAY = "compareArray";
	public static final String TABLE_CHECKLIST_MAP_OUTER = "tableCheckListMapOuter";
	public static final String PRIMARY_CATEGORY_COUNT_MAP = "primaryCategoryCountMap";
	public static final String TABLE_NAME = "tableName";
	public static final String STOFU_GS_EMAIL = "stofuGSEmail";
	public static final String PDP_EMAIL = "GSEmailPDP";
	public static final String FRMDATA_IMAGE = "frmData_image";
	public static final String FRMDATA_HOST_STORE_URL = "frmData_hostStoreUrl";
	public static final String FRMDATA_SMALL_IMAGE = "frmData_smallImage";
	public static final String FRMDATA_LARGE_IMAGE = "frmData_largeImage";
	public static final String FRMDATA_THUMBNAIL_IMAGE = "frmData_thumbnailImage";
	public static final String IS_ACTIVE = "isActive";
	// stofu email
	public static final String GS_KEYWORD_SEARCH_CACHE_NAME = "GS_KEYWORD_SEARCH_CACHE_NAME";
	public static final String GS_SEARCH_RESULT_CACHE_NAME = "GS_SEARCH_RESULT_CACHE_NAME";
	public static final String GS_HEADER_FLYOUT_CACHE_NAME = "GS_HEADER_FLYOUT_CACHE_NAME";

	// config key constants
	public static final String REQUESTDOMAIN_CONFIGURE = "requestDomainName";
	public static final String MOBILEWEB_CONFIG_TYPE = "MobileWebConfig";

	public static final String RESTRICTED_ATTRIBUTES_REST = "restrictedAttributesRest";
	public static final String OFFSET_DATE_VDC = "offsetDateVDC";
	public static final String SCENE_SEVEN_DOMAIN_PATH = "BBBSceneSevenDomainPath";
	public static final String WORKING_KEY = "Workingkey";
	public static final String ON_DEMAND_UNAME = "qas_onDemandUsername";
	public static final String ON_DEMAND_PSWD = "qas_onDemandPassword";

	// R2.1 Constants
	// R2.1 related change

	public static final String SHIP_METHOD_IS_DISCOUNTED = "shipMethodIsDiscounted";
	public static final String SHIP_METHOD_TEMP = "newShippingInfo";
	public static final String SHOPPING_CART_PATH = "/atg/commerce/ShoppingCart";
	public static final String FLAGGEDOFF = "flaggedoff";
	public static final String OUTPUTPRICE = "outputPrice";
	public static final String AVAILABILITYMESSAGE = "availabilityMessage";
	public static final String OUTPUTAVAILABLE = "outputAvailable";
	public static final String PREVIOUSPRICE = "previousPrice";
	public static final String CURRENTPRICE = "currentPrice";
	public static final String REQTYPE = "reqType";
	public static final String CART = "cart";
	public static final String WISHLIST = "wishlist";
	public static final String SESSIONBEAN = "sessionbean";
	public static final String SAVEDCOMP = "/com/bbb/profile/session/BBBSavedItemsSessionBean";
	public static final String LBL_ITEM_CHANGE = "lbl_item_change";
	public static final String LBL_NO_LONGER = "lbl_no_longer";
	public static final String TEMPITEM = "tempItem";
	public static final String FROM_CART = "fromCart";
	public static final String ITEM_LEVEL_EXP_DELIVERY = "itemLevelExpDelivery";
	public static final String ITEM_LEVEL_EXP_DELIVERY_REQ = "itemLevelExpDeliveryReq";
	public static final String TXT_FREE_SHIPPING_YOU_MOBILE = "txt_free_shipping_you_mobile";
	public static final String TXT_FREE_SHIPPING_AWAY_MOBILE = "txt_free_shipping_away_mobile";
	public static final String TXT_CONGRATS_FREE_SHIP_MSG = "txt_congrats_free_ship_msg";
	public static final String LBL_LOWER_FREE_SHIPPING_THRESHOLD = "lbl_lower_free_shipping_threshold";
	public static final String LBL_HIGHER_FREE_SHIPPING_THRESHOLD = "lbl_higher_free_shipping_threshold";

	// Email Persist Repository

	public static final String EMAIL_DATA = "emailData";
	public static final String EMAIL_MESSAGE = "emailMessage";
	public static final String EMAIL_DATE = "emailDate";
	public static final String EMAIL_ID = "emailId";
	public static final String EMAIL_FLAG = "emailFlag";
	public static final String SHOW_VIEW_IN_BROWSER = "showViewInBrowser";
	public static final String TEMP = "temp";
	public static final String GIFTLIST = "giftList";
	public static final String PRODUCT_ID_LIST = "prodIdList";
	public static final String REV = "rev";

	public static final String FLAG_OFF_CHECKED = "flagOffChecked";
	public static final String ITEM_OUT_OF_STOCK = "itemOutOfStock";
	public static final String ITEM_LINK = "itemLink";
	public static final String MSGSHOWNFLAGOFF = "msgShownFlagOff";
	public static final String MSGSHOWNOOS = "msgShownOOS";
	public static final String MSGSHIPMETHODUNSUPPORTED = "msgShipMethodUnsupported";
	public static final String TOP_LINK = "topLink";
	public static final String PREVPRICE = "prevPrice";
	public static final String OOS = "oos";
	public static final String ORIGINAL_LTL_SHIP_METHOD = "osm";
	public static final String REGISTRANT_SHIP_METHOD = "registrantShipMethod";
	public static final String HTTP = "http://";
	public static final String HTTPS_COLON = "https://";
	public static final String HTTP_COLON = "http:";
	public static final String IS_MOBILE_ORDER = "isMobileOrder";
	public static final String CHANNEL_MOBILE = "MOBILE";
	public static final String ORDER_CHANNEL = "orderChannel";
	public static final String CHANNEL_MOBILE_WEB = "MOBILE";
	public static final String CHANNEL_MOBILE_APP = "MOBIAPP";
	public static final String CHANNEL_THIRD_PARTY_MOBILE_APP = "TPM_APP";
	public static final String CHANNEL_DESKTOP = "DESKTOP";
	public static final String JVM_PROPERTY = "weblogic.Name";

	public static final String GS_BBB_SITE_ID = "GS_BedBathUS";
	public static final String GS_CA_SITE_ID = "GS_BedBathCanada";
	public static final String GS_BAB_SITE_ID = "GS_BuyBuyBaby";

	// R 2.2 Changes Start
	// Site Map Constants
	public static final String SITE_MAP_TYPE_PRODUCT_IMAGE = "productImageSitemap";
	public static final String SITE_MAP_TYPE_SKU_IMAGE = "SKUImageSitemap";
	public static final String PRODUCTION_URL = "productionURL";

	// Product Comparison Tool Constants

	// Sprint1(R2.2.1)| Maximum 4 check-box selection from PLPs | [Start]
	public static final int MAX_COMPARE_PRODUCT_COUNT = 4;
	// Sprint1(R2.2.1)| Maximum 4 check-box selection from PLPs | [End]

	public static final String _7C = "%7C";
	public static final String MAP_QUEST_VAR_RECORD_ID = "%22RecordId%22";
	public static final String HOST = "host";
	public static final String CONSTANT_SLASH = "://";

	public static final String PLACE_HOLDER_COMPARE_PRODUCT = "PLSR";
	public static final String COMPARE_PRODUCT_YES = "Yes";
	public static final String COMPARE_PRODUCT_NO = "No";
	public static final String MULTI_COLORS = "Multi Colors";
	public static final String SELECT_OPTIONS = "Select Options";
	public static final String NOT_AVAILABLE = "Not Available";
	public static final String FREE_STANDARD_SHIPPING = "freeStandardShippingAttrId";
	public static final String AVAILABLE = "Available";
	public static final String EMPTY_ATTRIBUTE = "---";
	public static final String CLEARANCE_ATTRIBUTE = "clearanceAttrId";

	public static final String RES1 = "res1";

	// UPS
	public static final String UPS_EASY_RETURNS_URL = "UPS_easy_returns_URL";
	public static final String EASY_RETURNS_KEY = "EasyReturnsKeys";
	public static final String EASY_RETURNS_LOGIN_ID = "login_id";
	public static final String EASY_RETURNS_LOGIN_PASSWORD = "login_password";
	public static final String EASY_RETURNS_PACKAGE_TYPE = "package_type";
	public static final String EASY_RETURNS_UOM = "uom";
	public static final String EMAIL_ADDRESS = "emailaddress";
	public static final String SHIP_FROM_CITY = "shipfromcity";
	public static final String BASE_PHONE_ERF1 = "basePhoneERF1";
	public static final String BASE_PHONE_ERF2 = "basePhoneERF2";
	public static final String BASE_PHONE_ERF3 = "basePhoneERF3";
	public static final String SHIPTORMA = "shiptorma";
	public static final String NUM_BOXES = "numboxes";
	public static final String RS_LEVEL = "rslevel";
	public static final String WEIGHT1 = "weight1";
	public static final String WEIGHT2 = "weight2";
	public static final String WEIGHT3 = "weight3";
	public static final String EASY_RETURNS_SERVICE = "service";
	public static final String EASY_RETURNS_SERVICE_CODE = "03";
	public static final String ONE_STRING = "1";
	public static final String CREATE_GROUP = "createGroup";
	public static final String VALIDATION = "VALIDATION";
	public static final String PAYPAL = "paypal";
	public static final String EXPIRES_CONSTANT = "; expires=";
	public static final String PATH_CONSTANT = "; path=/";

	public static final String FLAG_DRIVEN_FUNCTIONS = "FlagDrivenFunctions";
	public static String ASCIICHARACTER = "[^\\p{ASCII}]";
	public static String ASCII_CHARACTER_CHECK = "\\A\\p{ASCII}*\\z";
	public static String DEFAULTCURRENCY = "defaultCurrency";
	public static String _SEARCHVENDOR_SITESPECT = "_SearchVendor_Sitespect";
	public static final String PACKAGE_TYPE = "package_type";
	public static final String UOM = "uom";

	public static final String FIRST_VISIT = "firstVisit";
	public static final String PROFILE_AVAILABLE_FOR_MIGRATION = "profile_available_for_migration";
	public static final String PROFILE_ALREADY_EXIST = "profile_already_exist";
	public static final String PROFILE_AVAILABLE_FOR_EXTENSION = "profile_available_for_extenstion";
	public static final String PROFILE_NOT_FOUND = "Profile not found";
	public static final String FF1 = "FF1";
	public static final String FF2 = "FF2";
	public static final String IMAGE_HOST_GS = "image_host_gs";

	// inclusion functionality for promotion
	public static final String INCLUSION_RULE = "I";
	public static final String EXCLUSION_RULE = "E";
	public static final String MOBILE_SESSION_ID = "mobileSessionId";
	public static final String MOBILE_APP_REQUEST_HEADER_PARAM = "mobile_app_request_param";

	// BBBSL-2302. Corrected BV disabled flag
	public static final String SITE_IDS = "siteIds";
	public static final String _3_EN_US_PROD_DISABLE_N = "3_en_US_prodDisable_N";
	public static final String _3_EN_US_PROD_DISABLE_Y = "3_en_US_prodDisable_Y";
	public static final String _2_EN_US_PROD_DISABLE_N = "2_en_US_prodDisable_N";
	public static final String _2_EN_US_PROD_DISABLE_Y = "2_en_US_prodDisable_Y";
	public static final String _3_EN_US_WEB_OFFERED_Y = "3_en_US_webOffered_Y";
	public static final String _3_EN_US_WEB_OFFERED_N = "3_en_US_webOffered_N";
	public static final String _2_EN_US_WEB_OFFERED_N = "2_en_US_webOffered_N";
	public static final String _2_EN_US_WEB_OFFERED_Y = "2_en_US_webOffered_Y";

	// For Easy2 Provider Id
	public static final String MEDIA_VENDOR_EASY2_PROVIDER_ID = "Easy2_Provider_Id";

	public static final String MOBILE_HOST_NAME = "mobileHostName";

	// Tracking Status Constant
	public static final String TRACKING_ORDER_STATUS_P = "P";
	public static final String TRACKING_ORDER_STATUS_F = "F";
	public static final String TRACKING_ORDER_STATUS_T = "T";
	public static final String TRACKING_ORDER_STATUS_C = "C";
	public static final String TRACKING_ORDER_STATUS_D = "D";
	public static final String STATUS_ORDER_BEING_PROCESSED = "Order being processed.";
	public static final String STATUS_ORDER_T = "All items shipped on ";
	public static final String STATUS_ORDER_D = "All items delivered on ";
	public static final String STATUS_ORDER_C = "Was cancelled on ";

	public static final String LBL_WHITE_GLOVE_ASSEMBLY = "lbl_white_glove_assembly";
	public static final String SHIPPING_METHOD_AVL = "shippingMethodAvl";
	public static final String RECOMMANDED_ITEM_SELECTED = "Recommanded_Item_Selected";
	public static final String DESTINATION_ORDER = "destOrder";
	public static final String SOURCE_ORDER = "srcOrder";
	public static final String IS_PHANTOM = "isPhantomCategory";

	// Baby Canada Redirection Constants

	public static final String DOMAN_VS_REFERER = "DomainVsReferer";
	public static final String DOMAIN = "Domain";
	public static final String REFERER = "referer";
	public static final String INDENT_1 = "\n\t";
	public static final String INDENT_2 = "\n\t\t";
	public static final String URL_TAG = "<url>";
	public static final String URL_TAG_CLOSE = "</url>";
	public static final String LOC_TAG = "<loc>";
	public static final String LOC_TAG_CLOSE = "</loc>";
	public static final String MOBILE_TAG = "<mobile:mobile/>";
	public static final String CHANGE_FREQ_TAG = "<changefreq>";
	public static final String CHANGE_FREQ_TAG_CLOSE = "</changefreq>";
	public static final String PRIORITY_TAG = "<priority>";
	public static final String PRIORITY_TAG_CLOSE = "</priority>";

	public static final String CUSTOM_LANDING_PAGE = "custom_landing_page";
	public static final String CUSTOM_LANDING_PAGE_EMPTY = "clpEmpty";

	public static final String TEMPLATE_ARG_MISSING = "err_template_name_param_missing";
	public static final String TEMPLATE_ARG_MISSING_MESSAGE = "Parameter template name is missing";
	public static final String TEMPLATE_NOT_EXIST = "err_template_not_exist";
	public static final String TEMPLATE_NOT_EXIT_MESSAGE = "The template does not exist";
	public static final String CHANNEL_PARAM_MISSING = "err_channel_id_missing";
	public static final String CHANNEL_PARAM_MISSING_MESSAGE = "The header param channelId missing";
	public static final String TEMPLATE_VIEW_MISSING = "err_template_view_not_exist";
	public static final String TEMPLATE_VIEW_MISSING_MESSAGE = "The template view does not exist";
	public static final String ERROR_TEMPLATE_ARGS = "err_template_args_error";
	public static final String ERROR_TEMPLATE_ARGS_MESSAGE = "There is some error with arguments";
	public static final String CLP_PAGE = "clpPage";
	public static final String CONSTANT_SERVICE = "service";
	public static final String CLP_NAME = "clpName";

	// For personal Store

	public static final String STRATEGY_LAYOUT = "strategyLayout";
	public static final String LAYOUT_NAME = "layoutName";
	public static final String CONCAT_DELIMITER = ";";
	public static final String PERSONAL_STORE_TITLE_DEFAULT = "personalStoreTitleDefault";
	public static final String PERSONAL_STORE_TITLE_TRENDING = "personalStoreTitleTrending";
	public static final String STRATEGY_PAGE_TITLE = "strategyPageTitle";
	public static final String LAYOUT_TYPE_DATA = "layoutTypeData";
	public static final String JSP_DELIMITER = "_";
	public static final String JSP_EXTENSION = ".jsp";
	public static final String STRATEGIES = "strategies";
	public static final String STRATEGY_ID = "id";
	public static final String CUSTOMER_TYPE = "customerType";
	public static final String STRATEGY_NAME = "strategyName";
	public static final String SCHEME = "scheme";
	public static final String CONTEXT = "context";
	public static final String CERTONARESPONSEVO = "certonaResponseVO";
	public static final String FROM_PS_PARAM = "fromPersonalStore";
	public static final String CONTEXT_PARAM = "context";
	public static final String SOURCE_PARAM = "sourceParameters";
	public final static String PERSONAL_STORE = "personalStore";
	public final static String PS_STRATEGY = "psStrategy";
	public static final String ANONYMOUS = "Anonymous";
	public final static String DEF_LAYOUT_NAME = "mason";
	public final static String DEF_TYPE_DATA = "product";
	public final static String PRODUCT = "Product";
	public final static String SITE_PROPERTY = "sites";
	public static final String MAX_AGE_LV_COOKIE = "maxAgeLastViewedCookie";
	public static final String MAX_AGE_LB_COOKIE = "maxAgeLastBoughtCookie";
	public static final String RECOMMENDATION = "Recommendation";
	public static final String STRATEGY_TYPE = "strategyType";
	public static final String CATEGORY_COOKIE = "categoryCookie";
	public static final String BBB_CONSTANT = "BEDBATHUS";
	public static final String BABY_CONSTANT = "BUYBUYBABY";
	public static final String CA_CONSTANT = "BEDBATHCA";
	public static final String SEARCHTERM = "SEARCHTERM";
	public static final String PS_DETAILS = "personalStoreDetails";
	public static final String PS_USER_PROFILE = "userProfile";
	public static final String Is_ERROR = "isError";
	public static final String STRATEGYID = "strategyId";
	public static final String STRATEGY_CONTEXT_VALUE = "strategyContextValue";
	public static final String STRATEGY_CODE = "strategyContextCode";
	public static final String TRENDING = "Trending";
	public static final String ST_DETAILS = "psStrategyDetails";
	public static final String PAGE_ID = "pageid";
	public static final String LINK_CERTONA = "linksCertona";

	// For CLP
	public final static String CATEGORYL1 = "categoryL1";
	public final static String CATEGORYL2 = "categoryL2";
	public final static String CATEGORYL3 = "categoryL3";
	public final static String PARAM_CATEGORY_ID = "categoryId";
	public static final String PARAM_RESPONSE_VO = "cmsResponseVO";
	public static final String TEMPLATE_NAME = "templateName";
	public static final String PARAM_NAME = "name";
	public static final String DOUBLE_COLON_SYMBOL = "::";
	public static final String TYPE_AHEAD_DEPT_MAX_COUNT = "TYPE_AHEAD_DEPT_MAX_COUNT";
	public static final String TYPE_AHEAD_BRAND_MAX_COUNT = "TYPE_AHEAD_BRAND_MAX_COUNT";
	public static final String TYPE_AHEAD_POPULAR_MAX_COUNT = "TYPE_AHEAD_POPULAR_MAX_COUNT";
	public static final String CONTENT_CATALOG_KEYS = "ContentCatalogKeys";
	public static final String CATEGORY_L3 = "L3";
	public static final String CATEGORY_L2 = "L2";
	public static final String MAX_AGE_CAT_COOKIE = "maxAgeCategoryCookie";
	public static final String TYPE_AHEAD_REST_CALL = "getTypeAheadSearch";
	public static final String SITE_SPECTBRAND_TYPE_AHEAD_SIZE = "brandSize";
	public static final String SITE_SPECTPOPULAR_TYPE_AHEAD_SIZE = "popularSize";
	public static final String SITE_SPECTDEPT_TYPE_AHEAD_SIZE = "deptSize";
	public static final String SITE_SPECTSEARCH_IN_DEPT_TYPE_AHEAD_SIZE = "searchInDeptSize";

	public static final String BRAND_TYPE_AHEAD_SIZE = "brandSize";
	public static final String POPULAR_TYPE_AHEAD_SIZE = "popularSize";
	public static final String DEPT_TYPE_AHEAD_SIZE = "deptSize";
	public static final String SEARCH_IN_DEPT_TYPE_AHEAD_SIZE = "searchInDeptSize";

	public static final String TYPE_AHEAD_MAX_L2_Dept_COUNT = "TYPE_AHEAD_MAX_L2_Dept_COUNT";

	public static final String IS_BTS = "isBts";
	public static final String RKG_PRETAX_TOTAL = "RKG_PRETAX_TOTAL";

	// Fedex

	public static final String FEDEX_ENABLED = "fedexEnabled";

	public static final String FEDEX_SHIPSERVICE_CMN_VERSIONID_BIGDECIMAL = "fedex.shipservice.cmn.bigdecimal";
	public static final String FEDEX_SHIPSERVICE_CMN_ERROR = "fedex.shipservice.cmn.error";

	public static final String FEDEX_SHIPSERVICE_CMN_ACCOUNTNUMBER = "fedex.shipservice.cmn.accountNumber";
	public static final String FEDEX_SHIPSERVICE_CMN_METERNUMBER = "fedex.shipservice.cmn.meterNumber";

	public static final String FEDEX_SHIPSERVICE_CMN_KEY = "fedex.shipservice.cmn.key";
	public static final String FEDEX_SHIPSERVICE_CMN_PASSWORD = "fedex.shipservice.cmn.password";

	public static final String FEDEX_SHIPSERVICE_CA_ACCOUNTNUMBER = "fedex.shipservice.ca.accountNumber";
	public static final String FEDEX_SHIPSERVICE_CA_METERNUMBER = "fedex.shipservice.ca.meterNumber";

	public static final String FEDEX_SHIPSERVICE_CA_KEY = "fedex.shipservice.ca.key";
	public static final String FEDEX_SHIPSERVICE_CA_PASSWORD = "fedex.shipservice.ca.password";

	public static final String FEDEX_SHIPSERVICE_CMN_COMPANYNAME = "fedex.shipservice.cmn.companyName";
	public static final String FEDEX_SHIPSERVICE_CMN_STREETLINE1 = "fedex.shipservice.cmn.streetLine1";
	public static final String FEDEX_SHIPSERVICE_CMN_STREETLINE2 = "fedex.shipservice.cmn.streetLine2";
	public static final String FEDEX_SHIPSERVICE_CMN_CITY = "fedex.shipservice.cmn.city";
	public static final String FEDEX_SHIPSERVICE_CMN_STATE = "fedex.shipservice.cmn.state";
	public static final String FEDEX_SHIPSERVICE_CMN_COUNTRYCODE = "fedex.shipservice.cmn.countryCode";
	public static final String FEDEX_SHIPSERVICE_CMN_PHONENUMBER = "fedex.shipservice.cmn.phoneNumber";
	public static final String FEDEX_SHIPSERVICE_CMN_POSTALCODE = "fedex.shipservice.cmn.postalCode";

	public static final String FEDEX_SHIPSERVICE_CA_COMPANYNAME = "fedex.shipservice.ca.companyName";
	public static final String FEDEX_SHIPSERVICE_CA_STREETLINE1 = "fedex.shipservice.ca.streetLine1";
	public static final String FEDEX_SHIPSERVICE_CA_STREETLINE2 = "fedex.shipservice.ca.streetLine2";
	public static final String FEDEX_SHIPSERVICE_CA_CITY = "fedex.shipservice.ca.city";
	public static final String FEDEX_SHIPSERVICE_CA_STATE = "fedex.shipservice.ca.state";
	public static final String FEDEX_SHIPSERVICE_CA_COUNTRYCODE = "fedex.shipservice.ca.countryCode";
	public static final String FEDEX_SHIPSERVICE_CA_PHONENUMBER = "fedex.shipservice.ca.phoneNumber";
	public static final String FEDEX_SHIPSERVICE_CA_POSTALCODE = "fedex.shipservice.ca.postalCode";

	public static final String FEDEX_SHIPSERVICE_BABY_COMPANYNAME = "fedex.shipservice.baby.companyName";
	public static final String FEDEX_SHIPSERVICE_BABY_STREETLINE1 = "fedex.shipservice.baby.streetLine1";
	public static final String FEDEX_SHIPSERVICE_BABY_STREETLINE2 = "fedex.shipservice.baby.streetLine2";
	public static final String FEDEX_SHIPSERVICE_BABY_CITY = "fedex.shipservice.baby.city";
	public static final String FEDEX_SHIPSERVICE_BABY_STATE = "fedex.shipservice.baby.state";
	public static final String FEDEX_SHIPSERVICE_BABY_COUNTRYCODE = "fedex.shipservice.baby.countryCode";
	public static final String FEDEX_SHIPSERVICE_BABY_PHONENUMBER = "fedex.shipservice.baby.phoneNumber";
	public static final String FEDEX_SHIPSERVICE_BABY_POSTALCODE = "fedex.shipservice.baby.postalCode";

	public static final String FEDEX_SHIPSERVICE_CMN_SUCCESS_URL_EMAIL_MESSAGE = "fedex.shipservice.email.message";
	public static final String FEDEX_SHIPSERVICE_CMN_DEFSULT_LABEL_NAME = "fedex.shipservice.label.name";
	public static final String FEDEX_SHIPSERVICE_CMN_NUM_EXPIRATION_DAYS = "fedex.shipservice.expiration.days";

	public static final String FEDEX_SHIPSERVICE_WEIGHT_UNITS = "fedex.shipservice.weight.units";
	public static final String WSDL_FEDEX = "WSDL_Fedex";
	public static final String TRACKING_URL_TRACKING_INFO_PROPERTY_NAME = "trackingURL";

	// International Cart messaging
	public static final String OUTPUTENVOY = "outputEnvoy";
	public static final String OUTPUTINTLSHIP = "outputIntlShip";

	// label constants for Coupon Wallet

	public static final String ERR_COUPON_EMAIL_EMPTY = "err_couponwallet_email_empty";
	public static final String ERR_COUPON_EMAIL_INVALID = "err_couponwallet_email_invalid";
	public static final String ERR_COUPON_CODE_EMPTY = "err_couponwallet_couponcode_empty";
	public static final String ERR_COUPON_CODE_INVALID = "err_couponwallet_couponcode_invalid";

	public static final String COUPON_WALLET_ERROR = "couponError";
	public static final String ADDWALLET_TO_PROFILE_CANADA_SITE = "addwalletprofile_canada";

	public static final String LAST_ACTIVITY = "lastActivity";

	public static final String SHALLOW_PROFILE_STATUS_VALUE = "shallow";
	public static final String FULL_PROFILE_STATUS_VALUE = "full";
	public static final String ADD_WALLETID_TO_PROFILE = "addWalletItToProfile";

	public static final String IS_MOBILE = "isMobile";
	public static final String QTY_REQUESTED = "qtyRequested";
	public static final String QTY_FULFILLED = "qtyFulfilled";
	public static final String QTY_WEBPURCHASED = "qtyWebPurchased";
	public static final String LAST_MAINTAINED = "lastMaintained";
	public static final String CREATE_TIMESTAMP = "createTimestamp";
	public static final String CURRENCY = "currency";
	public static final String UNFORMATTED_CURRENCY = "unformattedCurrency";
	public static final String PATTERN_FORMAT = "\\$(\\d{0,9})*+(.\\d{1,2})?";
	public static final String PATTERN_FORMAT_MX = "MXN\\s(\\d{0,9})*+(.\\d{1,2})?";

	// BPS-1394
	public static final String ITEM_DESC_REG_DETAIL = "regDetail";
	public static final String CLEAR_FILTERS = "clearFilters";
	public static final String STOREDETAILSPATH = "/com/bbb/selfservice/common/StoreDetails";
	public static final String PREVIEWATTRIBUTESPATH = "/com/bbb/commerce/catalog/PreviewAttributes";

	public static final String PRICE_RANGE_MX = "Price_Range_MX";
	public static final String PRICE_RANGE = "Price_Range";
	public static final String FROM_COLLEGE = "fromCollege";
	public static final String QUERY_PARAMS = "queryParams";
	public static final String AMPERSAND_HTML = "&amp;";
	public static final String REGEX_QUES_MARK = "\\?";
	public static final String CLEAR_FILTERS_PARAM = "clearFilters=true";
	public static final String FORWARD_QUERY_STRING = "javax.servlet.forward.query_string";
	public static final String REQUESTURI_QUERY_STRING = "javax.servlet.forward.request_uri";

	// harte hanks toked id for coupon wallet pages
	public static final String ATG_TOKEN_HARTE_HANKS = "atg_harte_hanks_token";

	public static final String RECOVERY_TIME_PERIOD = "RECOVERY_TIME_PERIOD";
	public static final String COHERENCE_EXCEPTION_COUNT_THRESHOLD = "COHERENCE_EXCEPTION_COUNT_THRESHOLD";
	public static final String COHERENCE_OPTIMIZATION_ENABLE = "COHERENCE_OPTIMIZATION_ENABLE";
	public static final String COHERENCE_ENABLE_THRESHOLD_TIME = "COHERENCE_ENABLE_THRESHOLD_TIME";
	public static final String IS_FROM_SCHEDULER = "isFromScheduler";
	public static final String DYNAMO_HANDLER = "/atg/dynamo/servlet/dafpipeline/DynamoHandler";

	// SiteSpect parameter for search Configurable
	public static final String SITESPEC_SEARCHGROUP = "X-siteSpectSearchGroup";

	// click through coupon wallet changes
	public static final String CLICK_THROUGH_PARAMETER_CLICKDATA = "d";
	public static final String CLICK_THROUGH_PARAMETER_CLICKACTION = "a";
	public static final String COUPONS_MANAGER_SCHEME = "coupons_manager_clickthrough_scheme";
	public static final String COUPONS_MANAGER_HOST_NAME = "coupons_manager_clickthrough_host";
	public static final String COUPONS_MANAGER_PORT = "coupons_manager_clickthrough_port";
	public static final String COUPONS_MANAGER_PATH = "coupons_manager_clickthrough_path";
	public static final String CONTEXT_STORE = "/store";
	public static final String CONTEXT_TBS = "/tbs";
	public static final String CONTEXT_REST = "/rest";

	public static final String ORIGIN_OF_TRAFFIC = "origin_of_traffic";

	// Exim Changes
	public static final String ELIGIBLE_CUSTOMIZATION_CODES = "eligibleCustomizationCodes";
	public static final String CUSTOMIZATION_OFFERED_FLAG = "customizationOfferedFlag";
	public static final String BAB_CUSTOMIZATION_OFFERED_FLAG = "babCustomizationOfferedFlag";
	public static final String CA_CUSTOMIZATION_OFFERED_FLAG = "caCustomizationOfferedFlag";
	public static final String GS_CUSTOMIZATION_OFFERED_FLAG = "gsCustomizationOfferedFlag";
	public static final String CUSTOMIZATION_CODES = "CustomizationCodes_";
	public static final String IS_CUSTOMIZABLE_ON = "isCustomizableOn";
	public static final String IS_CUSTOMIZATION_REQUIRED = "isCustomizationRequired";
	public static final String PERSONALIZATION_TYPE = "personalizationType";
	public static final String PERSONALIZATION_CODE_PB = "PB";
	public static final String PERSONALIZATION_CODE_PY = "PY";
	public static final String PERSONALIZATION_CODE_CR = "CR";
	public static final String EXIM_KEYS = "EXIMKeys";
	public static final String REFERENCE_NUMBER = "referenceNumber";
	public static final String FULL_IMAGE_PATH = "fullImagePath";
	public static final String THUMBNAIL_IMAGE_PATH = "thumbnailImagePath";
	public static final String MOBILE_FULL_IMAGE_PATH = "mobileFullImagePath";
	public static final String MOBILE_THUMBNAIL_IMAGE_PATH = "mobileThumbnailImagePath";
	public static final String PERSONALIZE_PRICE = "personalizePrice";
	public static final String PERSONALIZATION_OPTIONS = "personalizationOptions";
	public static final String PERSONALIZATION_DETAILS = "personalizationDetails";
	public static final String PERSONALIZATION_ATTR = "personalizationAttr";
	public static final String PERSONALIZATION_STATUS = "personalizationStatus";
	public static final String PERSONALIZATION_STATUS_COMPLETE = "saved_complete";
	public static final String PERSONALIZATION_STATUS_SAVED = "saved";
	public static final String EXIM_PRICING_REQ = "eximPricingReq";
	public static final String EXIM_ERROR_EXISTS = "eximErrorExists";
	public static final String EXIM_DETAILS_MAP = "eximDetailsMap";

	public static final String REFERENCE_NUMBER_PARAM = "refNum";
	public static final String FULL_IMAGE_PATH_PARAM = "pFImg";
	public static final String THUMBNAIL_IMAGE_PATH_PARAM = "pTImg";
	public static final String MOBILE_FULL_IMAGE_PATH_PARAM = "pMb_FImg";
	public static final String MOBILE_THUMBNAIL_IMAGE_PATH_PARAM = "pMb_TImg";
	public static final String PERSONALIZE_PRICE_PARAM = "pPrc";
	public static final String PERSONALIZATION_OPTIONS_PARAM = "pOpt";
	public static final String PERSONALIZATION_DETAILS_PARAM = "pDet";
	public static final String PERSONALIZATION_STATUS_PARAM = "pStat";
	public static final String EXIM_PRICING_REQUIRED = "eximPricingRequired";
	public static final String SQUARE_BRACKET = "[]";

	// DSK | VDC messaging - combine cart and PDP | offset message
	public static final String VDC_OFFSET_DATE = "vdcOffsetDate";
	public static final String VDC_OFFSET_FLAG = "vdcOffsetFlag";
	public static final String ACTUAL_OFF_SET_DATE = "actualOffsetDate";
	public static final String ACTUAL_OFF_SET_DATE_PLHOLDER = "actualOffSetDate";
	public static final String VDC_DEL_TIME = "vdcDelTime";

	public static final String ITEM_TYPES = "itemTypes";
	public static final String REQUIRE_MSG_IN_DATES = "requireMsgInDates";
	public static final String LBL_VDC_DEL_MSG = "lbl_vdc_del_time_msg";
	public static final String SHIPPING_CUT_OFF_OFFSET = "shippingCutoffOffset";
	public static final String LBL_VDC_DEL_REST_MSG = "lbl_vdc_del_rest_msg";
	public static final String LBL_VDC_DEL_LTL_MSG = "lbl_vdc_del_ltl_msg";
	public static final String LBL_VDC_DEL_REST_CART_MSG = "lbl_vdc_del_cart_rest_msg";
	public static final String TXT_VDC_OFFSET_MSG = "txt_vdc_offset_msg";
	public static final String LBL = "lbl_";
	public static final String GET = "GET";
	public static final String PUT = "PUT";
	public static final String POST = "POST";
	public static final String X_ClientId = "X-ClientId";
	public static final String X_ApiKey = "X-ApiKey";
	public static final String PORCH_PARTNER_NAME = "partnerName";
	public static final String EXIM_API_URL = "EximAPIURL";
	public static final String LOCK_VIEW = "/lock";
	public static final String SUMMARY_VIEW = "/summary";
	public static final String LOCKED = "locked";
	public static final String ENABLE_KATORI = "enableKatori";
	public static final String CUSTOMIZATBLE_CTA_CODES = "CustomizeCTACodes";
	public static final String KATORI = "KATORI";

	// Error constant for throwing error when reference number is null
	public static final String EXIM_ADDTOCART_ERROR = "There seems to be an error with your personalization.Please try again!!";
	public static final String EXIM_ADDTOSFL_ERROR = "There seems to be an error with your personalization while adding SFL item from cookie. Please try again!!";
	public static final String REFNUM = "refId";
	public static final String PERSONALIZATION_CODE = "personalizationCode";
	public static final String PERSONALIZATION_DESC = "personalizationDescription";
	public static final String IMAGE_URL = "imageUrl";
	public static final String IMAGE_URL_THUMB = "imageUrlThumb";
	public static final String MOB_IMAGE_URL = "mobImageUrl";
	public static final String MOB_IMAGE_URL_THUMB = "mobImageUrlThumb";
	public static final String ITEM_TYPE = "itemType";
	public static final String CUSTOMIZATION_PRICE = "customizationPrice";
	public static final String PERSONALIZATION_PRICE = "personalizationPrice";
	public static final String IMAGE_ID = "front";
	public static final String IMAGE_PREVIEW_LARGE = "large";
	public static final String IMAGE_PREVIEW_X_SMALL = "x-small";
	public static final String IMAGE_PREVIEW_SMALL = "small";
	public static final String IMAGE_PREVIEW_MEDIUM = "medium";
	public static final String ERR_EDIT_INVALID_REF_NUM_MSG = "Reference number of item doesnt match reference number after editing";
	public static final String ERR_EDIT_REF_NUM_NOT_EXIST_MSG = "Reference number of item is empty or null";
	public static final String ERR_EDIT_INVALID_REF_NUM = "err_edit_invalid_ref_num";
	public static final String ERR_ACCOUNT_ALREADY_EXISTS = "err_email_already_exists";
	public static final String ITEM_DESC_REG_DETAIL2 = "regDetail2";
	public static final String LAST_MODIFIED_DATE_COLUMN = "LAST_MODIFIED_DATE";
	public static final String SCHEDULAR_LAST_CLEAR_TIME = "SCHEDULAR_LAST_CLEAR_TIME";
	public static final String CHANNEL_FORM_FACTOR_1 = "FF1";
	public static final String CHANNEL_FORM_FACTOR_2 = "FF2";
	public static final String AND_WITH_START_BRACKET = "AND(";
	public static final String END_BRACKET = ")";
	public static final String PERSONALIZEDSHOPTEMPLATE = "personalizedShopTemplate";
	public static final String PERSONALIZED_ATTR_ID = "personalizedAttrId";
	public static final String OMNI_SYMBOL_3 = "|eVar54=";
	public static final String PERSIONALIZATION_OPTION = "pOpt";

	// RKG Tag Constants

	public static final String RKG_URL_PARAM_MID = "mid";
	public static final String RKG_URL_PARAM_OID = "oid";
	public static final String RKG_URL_PARAM_LID = "lid";
	public static final String RKG_URL_PARAM_CID = "cid";
	public static final String RKG_URL_PARAM_IID = "iid";
	public static final String RKG_URL_PARAM_TS = "ts";
	public static final String RKG_URL_PARAM_ICENT = "icent";
	public static final String RKG_URL_PARAM_IQTY = "iqty";
	public static final String RKG_URL_PARAM_INAME = "iname";

	// Certona Constants

	public static final String PURCHASE_CONFIRMATION = "purchase+confirmation";
	public static final String TO_PUT_IN_CACHE = "putInCache";
	public static final String OMNI_BUY_OFF_REG_MSG = "buy outside registry";
	public static final String PRICE_IS_TBD = "lbl_price_is_tbd";
	public static final String JOB_NAME = "jobName";

	// BPSI-5391

	public static final String IS_CLEAR_ENDECA_CACHE_ON_DEPLOYMENT = "is_clear_endeca_cache_on_deployment";

	public static final String COUNTRY = "COUNTRY";
	public static final String FROM_IP = "FROM_IP";
	public static final String TO_IP = "TO_IP";
	public static final String ENABLE_US_IP_STARTUP_CACHE = "enable_us_ip_startup_cache";
	public static final String STRATEGY_NAME_MAP = "strategyNameMap";
	public static final String WEB_FOOTER_LINK_TEMPLATE = "webFooterLinksTemplate";
	public static final String FOOTERNAME = "footerName";
	public static final String LINKS = "links";
	public static final String PROFILE_FORM_HANDLER = "/atg/userprofiling/ProfileFormHandler";
	public static final String SHOPGUIDEID = "shopGuideId";
	public static final String SHOPGUIDE = "shopGuide";
	public static final String REPOSITORY_NAME = "repositoryName";
	public static final String GUIDEFEED_SITE_MAP = "GuideFeedSiteMap";
	public static final String TBS = "TBS";
	public static final String CHECKOUT = "/checkout/";
	public static final String LOGIN_JSP = "/account/login.jsp";
	public static final String GENERATED_PASSWORD = "generatedPassword";
	public static final String ENVOY_CHECKOUT = "envoy_checkout.jsp";
	public static final String SITE_SPECT_JSP = "sitespecttest.jsp";
	public static final String FIND_STORE = "/store/selfservice/FindStore";
	public static final String LWA = "LWA";
	public static final String LTL = "LTL";
	public static final String LW = "LW";
	// BBBP-68 | Registry listing page optimization
	public static final String ITEM_DESC_REG_DETAIL2OWNER = "regDetail2Owner";
	public static final String ITEM_DESC_REG_DETAILOWNER = "regDetailOwner";
	public static final String SHIP_METHOD_DESCRIPTION = "shipMethodDescription";
	public static final String SHIP_METHOD_ID = "shipMethodId";
	public static final String LTL_DELIVERY_SERVICES = "ltlDeliveryServices";
	public static final String ASSEMBLY_SELECTED = "assemblySelected";
	public static final String SHIPPINGQUANTITY = "SHIPPINGQUANTITY";
	public static final String MAKE_FAVOURITE_STORE_ID = "favouriteStoreId";
	public static final String PER = "PER";
	public static final String ALTERNATE_NUM = "altNumber";
	public static final String A = "A";
	public static final String SHIPPING = "SHIPPING";

	public static final String BED_BATH_US_SITE_CODE = "BedBathUSSiteCode";
	public static final String COUPON_TAG_US = "CouponTag_us";
	public static final String COUPON_TAG_CA = "CouponTag_ca";
	public static final String BED_BATH_CANADA_SITE_CODE = "BedBathCanadaSiteCode";
	public static final String COUPON_TAG_BABY = "CouponTag_baby";
	public static final String BUY_BUY_BABY_SITE_CODE = "BuyBuyBabySiteCode";
	public static final String CHALLENGE_QUESTION_1 = "challengeQuestion1";
	public static final String PREFERRED_QUESTION_1 = "PreferredQuestion1";
	public static final String CHALLENGE_QUESTION_2 = "challengeQuestion2";
	public static final String PREFERRED_QUESTION_2 = "PreferredQuestion2";
	public static final String CHALLENGE_ANSWER_1 = "challengeAnswer1";
	public static final String PREFERRED_ANSWER_1 = "PreferredAnswer1";
	public static final String CHALLENGE_ANSWER_2 = "challengeAnswer2";
	public static final String PREFERRED_ANSWER_2 = "PreferredAnswer2";
	public static final String CHALLENGE_QUESTION_MAP = "challengeQuestionMap";
	public static final String CHALLENGE_QUESTION = "ChallengeQuestion";
	public static final String CHALLENGE_QUESTION_CONNSTANT = "1_";
	public static final String CHALLENGE_QUESTION_MAP1 = "ChallengeQuestionsMap1";
	public static final String CHALLENGE_QUESTION_MAP2 = "ChallengeQuestionsMap2";
	public static final String USER_CHALLENGE_QUESTION = "userChallengQuestion";
	public static final String CHALLENGE_QUESTION_REPOSITORY_1 = "ChallengeQuestion1";
	public static final String CHALLENGE_QUESTION_REPOSITORY_2 = "ChallengeQuestion2";
	public static final String CHALLENGE_ANSWER_REPOSITORY_1 = "ChallengeAnswer1";
	public static final String CHALLENGE_ANSWER_REPOSITORY_2 = "ChallengeAnswer2";
	public static final String QUESTIONS = "questions";
	public static final String QUESTIONS_TYPE = "questionType";
	public static final String CHALLENGE_QUESTIONS_REP = "challengeQuestions";
	public static final String EMAIL_PROVIDED = "emailProvided";
	public static final String QUESTION_1 = "Question1";
	public static final String QUESTION_2 = "Question2";
	public static final String PROFILE_ID = "profileId";
	public static final String FORGET_EMAIL = "forgetEmail";
	public static final String CHALLENGE_QUESTION_PROFILE_ID = "challengeQuestionProfileID";
	public static final String CHALLENGE_QUESTION_ERROR_EXIST = "challenegeQuestionErrorExist";
	public static final String FROM_LOGIN_PAGE = "fromLoginPage";
	public static final String ERR_CHALLENGE_QUESTION_SETUP = "err_challenge_question_setup";
	public static final String CHALLENGE_QUESTION_FAILURE_ATTEMPT = "ChallengeQuestionFailureAttempt";
	public static final String CREATE_CHALLENGE_QUESTION_ERROR = "createProfileChallengeQuestionError";
	public static final String TRAFFIC_OS = "Traffic_OS";
	public static final String ANDRIOD_OS = "ANDRIOD";
	public static final String IOS_OS = "IOS";

	public static final String FRM_DATA_CUR_YEAR = "frmData_currentYear";

	public static final String PERSONALIZED_SKU = "personalizedSku";
	public static final String PERSONALIZED_SINGLE_CODE = "personalizedSingleCode";
	public static final String PERSONALIZED_DESCRIPTION = "personalizedDescription";
	public static final String REMOVE_PERSONALIZATION = "removePersonalization";

	public static final String SP_REVIEW = "SP_REVIEW";
	public static final String PAGE_WRAPPER = "pagewrapper";
	public static final String SECTION = "section";

	public static final String VIEW_REGISTRY_OWNER = "view_registry_owner";
	public static final String VIEW_REGISTRY_OWNER_PAGE = "/giftregistry/view_registry_owner.jsp";
	public static final String HARTE_HANKS_MODAL = "hartehanks_modal.jsp";
	public static final String TARGET_PARAMETER = "&target=/store/giftregistry/view_registry_owner.jsp";
	public static final String MSG_TYPE_VDC = "vdc";
	public static final String MSG_TYPE_OFFSET = "offset";
	public static final String REGISTRY_TAB = "registryTab";
	public static final String XSS_VALIDATE_HEADER_FLAG = "XssValidateHeaderFlag";

	// SAP-735
	public static final String LOADER = "loader";
	public static final String TIER = "tier";
	public static final String TAB = "\t";
	public static final String JMXATTRIBUTE_EXCEPTION = "AttributeNotFoundException has occured .please check MBean server object n it's attribute.";
	public static final String JMXINSTANCE_EXCEPTION = "InstanceNotFoundException has occured .please check MBean instance.";
	public static final String JMXMBEAN_EXCEPTION = "MBeanException has occured .please check MBean attributes.";
	public static final String JMXREFELCTION_EXCEPTION = "ReflectionException has occured .please check MBean.";
	public static final String WRITELOGFILE_EXCEPTION = "IOException has occured .please check File existance in which we are writing log.";
	public static final String THRESHOLDRECHABLE = " Address is rechable in ";
	public static final String THRESHOLDNOTRECHABLE = " Address is not rechable in ";
	public static final String MILLISECONDS = " milliseconds ";
	public static final String THRESHOLDIO_EXCEPTION = "has not be reachable.Please enter correct site address .";
	public static final String THRESHOLDUNKNOWNHOST_EXCEPTION = "UnknownHostException has occured.Please enter correct host name.";
	public static final String JMXQUERY_EXCEPTION = "JMX object has not parsed JMX query properly.";
	public static final String JMXQUERYNULL_EXCEPTION = "JMX object returned NULL";
	public static final String JMXQUERYIO_EXCEPTION = "JMX object has not reterived data from MBean.";
	public static final String FILEPATH_EXCEPTION = " has not created.Please check file system permission";
	public static final String HEADERFILEPATH_EXCEPTION = "IOException has occured in data writing in log file .May be Header has not been written to file system.";
	public static final String IS_ENABLE = "IS_ENABLE";
	public static final String IS_THRESHOLDRECHABLE_ENABLE = "IS_THRESHOLDRECHABLE_ENABLE";
	public static final String IS_RECHABLE_TIMEOUT = "IS_RECHABLE_TIMEOUT";
	public static final String COHERENCE_STATICS_FILEPATH = "COHERENCE_STATICS_FILEPATH";
	public static final String COHERENCE_STATICS_FILENAME = "COHERENCE_STATICS_FILENAME";
	public static final String IP_PING_LIST = "IP_PING_LIST";
	public static final String BBBSYSTEM_EXCEPTION = "System Exception occured when initializing BCC configuration";
	public static final String BBBBUSINESS_EXCEPTION = "Business Exception occured when initializing BCC configuration";
	public static final String FILE_PATH = "File created at location: ";
	public static final String MBEAN_REGION = "";
	public static final String MBEAN_SERVER = "MBeanServerConnection has been established.";
	public static final String MBEAN_SERVER_FAILED = "MBeanServerConnection has not been established .";
	public static final String FRONT_QUERY = "Front Scheme  Query has executed : ";
	public static final String BACK_QUERY = "Back Scheme Query has executed : ";
	public static final String HEADER = "Header has been written in file : ";
	public static final String MBEAN_VALUE = "Attribute data has been written in file of MBean : ";
	public static final String XO_USER = "xouser";
	public static final String BBB_DESKTOP = "BBB-Desktop";
	public static final String BBB_MOBILE = "BBB-Mobile";
	public static final String MOBILE_CLIENT = "client123";
	// BPSI-6385
	public static final String COHERENCE_CACHE_MONITOR_KEY = "CoherenceCacheMonitorKeys";
	public static final String IS_FROM_GIFT_GIVER = "isFromGiftGiver";
	public static final String DS_LINEITEM_TAXINFO = "dsLineItemTaxInfo";
	public static final String COMMERCE_ITEM_ID = "commerceItemId";
	public static final String COMMERCE_ITEM = "commerceItem";
	public static final String DPI_ID = "dpiId";
	public static final String CITY_TAX = "cityTax";
	public static final String STATE_TAX = "stateTax";
	public static final String COUNTY_TAX = "countyTax";
	public static final String DISTRICT_TAX = "districtTax";
	public static final String DPI_ITEM = "DPI_ITEM";
	public static final String DPI_DELIVERY = "DPI_DELIVERY";
	public static final String DPI_ASSEMBLY = "DPI_ASSEMBLY";
	public static final String DPI_ECOFEE = "DPI_ECOFEE";
	public static final String DS_LINE_ITEM_REL = "dsLineItemRel";
	public static final String DPI_DELIVERY_SPLIT = "DPI_DELIVERY_SPLIT";
	public static final String DPI_ITEM_SPLIT = "DPI_ITEM_SPLIT";
	public static final String DPI_ASSEMBLY_SPLIT = "DPI_ASSEMBLY_SPLIT";
	public static final String DPI_ECOFEE_SPLIT = "DPI_ECOFEE_SPLIT";
	public static final String ORDER_ID = "orderId";

	public static final String CART_AND_CHECKOUT_KEYS = "CartAndCheckoutKeys";
	public static final String CHALLENGE_QUESTION_ON_OFF = "challenge_question_flag";
	public static final String SCENE7_IMAGE_SIZE_FROM_ENDECA = "scene7EndecaImageSize";

	// Omniture Boosting Products
	public static final String OMNITURE_TIME_FROMAT = "MM/dd/yyyy hh:mm a";
	public static final String OMNITURE_REPORT_SUCCESS = "Success";
	public static final String OMNITURE_REPORT_QUEUED = "Queued";
	public static final String OMNITURE_REPORT_FAILED = "Failed";
	public static final String OMNITURE_DATE_FORMAT = "yyyy-MM-dd";
	public static final String OMNITURE_DATE_FORMAT1 = "MM/dd/yyyy";
	public static final String OMNITURE_BOOSTING = "OmnitureBoosting";
	public static final String OMNITURE_END_POINT = "OmnitureEndPointURL";
	public static final String OMNITURE_REPORT_RETENTION_DAYS = "OmnitureReportRetentionDays";
	public static final String KEYWORD_EPH_CAT_MAP_RETENTION_DAYS = "KeywordEphCatMapRetentionDays";
	public static final String OMNITURE_CHECK_QUEUE = "Check_Queue_Email_Event";
	public static final String OMNITURE_CONSUMED_QUEUE = "Consumed_Queue_Email_Event";
	public static final String QUEUED_SCHEDULED_REPORT = "Queued_Report_Email_Event";
	public static final String OMNITURE_PROCESSING_EXCEPTION = "Omniture_Report_Processing_Exception";
	public static final String OMNITURE_ARCHIVE_REPORT = "Omniture_Archive_Report_Event";
	public static final String METHOD = "method=";
	public static final String OMNITURE_REPORT_GET = "OmnitureReportGet";
	public static final String OMNITURE_REPORT_CANCEL = "OmnitureReportCancel";
	public static final String OMNITURE_REPORT_QUEUE = "OmnitureReportQueue";
	public static final String OMNITURE_REPORT_GET_QUEUE = "OmnitureReportGetQueue";
	public static final String OMNITURE_REPORT_STATUS_CANCEL = "Cancel";
	public static final String OMNITURE_THRESHOLD_EXCEEDED = "ThresholdExceeded";
	public static final String REPORT_GET_TIME_1 = "reportGetTime1";
	public static final String REPORT_GET_TIME_2 = "reportGetTime2";
	public static final String REPORT_ERROR_CODE = "errorCode";
	public static final String REPORT_ERROR_DESC = "errorDescription";
	public static final String REPORT_BATCH_ID = "batchId";
	public static final String REPORT_BATCH_SEQ = "batchSeq";
	public static final String REPORT_CONFIG_ID = "reportConfigId";
	public static final String REPORT_FREQ = "freq";
	public static final String REPORT_CANCEL_ALLOWED = "cancelAllowed";
	public static final String REPORT_PRIORITY = "priority";
	public static final String REPORT_BATCH_COUNT = "batchCount";
	public static final String REPORT_NUMBER_OF_THREAD = "numOfThread";
	public static final String REPORT_THRESHOLD_DAYS = "thresholdDays";
	public static final String REPORT_TIME_RANGE = "reportTimeRange";
	public static final String REPORT_TIME_RANGE_ADJUST = "reportTimeRangeAdjustment";
	public static final String REPORT_PRODUCT_COUNT = "productCount";
	public static final String REPORT_LAST_SUCCESS_RUN_DATE = "lastSuccessRunDate";
	public static final String REPORT_LAST_MODI_DATE = "lastModifiedDate";
	public static final String REPORT_COUNT = "count";
	public static final String REPORT_RANGE_FROM = "rangeFrom";
	public static final String REPORT_RANGE_TO = "rangeTo";
	public static final String REPORT_ID_DDL = "REPORT_ID";
	public static final String REPORT_TYPE_DDL = "REPORT_TYPE";
	public static final String REPORT_QUEUED_DATE_DDL = "QUEUED_DATE";
	public static final String REPORT_GET_TIME_FIRST_DDL = "REPORT_GET_TIME_FIRST";
	public static final String REPORT_ERROR_CODE_DDL = "ERROR_CODE";
	public static final String REPORT_ERROR_DESCRIPTION_DDL = "ERROR_DESCRIPTION";
	public static final String REPORT_OP_STATUS_DDL = "REPORT_OP_STATUS";
	public static final String REPORT_BATCH_ID_DDL = "BATCH_ID";
	public static final String REPORT_BATCH_SEQ_DDL = "BATCH_SEQ";
	public static final String REPORT_COUNT_DDL = "COUNT";
	public static final String REPORT_RANGE_FROM_DDL = "RANGE_FROM";
	public static final String REPORT_RANGE_TO_DDL = "RANGE_TO";
	public static final String GET_ATTEMPTS_DDL = "GET_ATTEMPTS";
	public static final String REPORT_ERROR_CODE_BATCH_FAIL = "Report Batch Failed";
	public static final String REPORT_ERROR_DESC_BATCH_FAIL = "Report Manually marked failed due to previous report in batch failed or not ready , Failed report id ";
	public static final String REPORT_ID = "reportId";
	public static final String CONCEPT = "concept";
	public static final String REPORT_QUEUED_TIME = "queuedDate";
	public static final String REPORT_STATUS = "reportStatus";
	public static final String OMNITURE_REPORT_CONFIG = "reportConfig";
	public static final String REPORT_OP_STATUS = "reportOperationStatus";
	public static final String REPORT_EX_TIME = "reportExecutionTime";
	public static final String REPORT_PROC_FINISH_TIME = "reportProcessingFinishTime";
	public static final String INSERT_REPORT_DATA_QUERY = "INSERT INTO BBB_OMNITURE_REPORT_DATA (ID, REPORT_ID, REPORT_SUITE, PERIOD, CONCEPT, BOOST_SCORE, PRODUCT_ID, KEYWORD) VALUES"
			+ "(OMNITURE_BOOSTING_SEQ.nextVal,?,?,?,?,?,?,?)";

	public static final String ENABLE_OCB_BOOSTING_FLAG = "enableOCBBoosting";
	public static final String L2_BOOSTING_TOTAL_SEARCHED_TERMS = "L2BoostingTotalSearchedTerms";
	public static final String L2_BOOSTING_TOP_SEARCHED_TERMS = "L2BoostingTopSearchedTerms";
	public static final String L2_BOOSTING_REPORT_METRIC_ID = "L2BoostingReportMetricId";
	public static final String L2_BOOSTING_REPORT_DAYS_TO = "L2BoostingReportDaysTo";
	public static final String L2_BOOSTING_REPORT_DAYS_FROM = "L2BoostingReportDaysFrom";
	public static final String L2_BOOSTING_KEYWORDS_TO_OMIT = "L2BoostingKeywordsToOmit";
	public static final String OMNITURE_BOOSTED_PRODUCTS_KEYWORDS_TO_OMIT = "OmnitureBoostedProductsKeywordsToOmit";
	public static final String L2_BOOSTING_REQUEST_TYPE_NOT = "L2BoostingRequestTypeNot";
	public static final String POPULAR_TERMS_BOOSTING_REQUEST_TYPE_NOT = "PopularTermsRequestTypeNot";
	public static final String POPULAR_TERMS_BOOSTING_KEYWORDS_TO_OMIT = "PopularSearchKeywordsToOmit";
	public static final String L2_BOOSTING_SEARCH_CLASSIFICATION = "L2BoostingSearchClassification";
	public static final String L2_BOOSTING_REPORT = "L2 Boost Score";
	public static final String PRODUCT_BOOSTING_REPORT = "Search Boost Score";
	public static final String METHOD_TYPE = "methodType";
	public static final String GET_ATTEMPTS = "getAttempts";
	public static final String REPORT_NOT_READY = "report_not_ready";
	public static final String OMNITURE_CALL_WAIT_TIME_MILLS = "omnitureCallWaitTimeMills";
	public static final String OMNITURE_RETRY_WAIT_TIME_MILLIS = "omnitureRetryWaitTimeMills";
	public static final String OMNITURE_MAX_RETRY_ATTEMPTS = "omnitureMaxRetryAttempts";
	public static final String CATEGORY_BRAND_PROUCT_BOOSTING_REPORT_METRIC_ID = "CategoryBrandProductBoostingReportMetricId";
	public static final String POPULAR_SEARCH_BOOSTING_REPORT_METRIC_ID = "PopularSearchBoostingReportMetricId";
	public static final String POPULAR_SEARCH_KEYWORD_FILTER_PATTERN = "PopularSearchKeywordPatternFilter";// BBBI-3040
	public static final String SMTP_HOST_NAME = "smtp_host_name";
	public static final String MAP_SMTP_HOST = "smtpHostName";
	public static final String EMAIL_CONTENT = "emailContent";
	public static final String SUBJECT = "subject";
	public static final String L2_BOOSTING_BATCH_SIZE = "L2BoostingBatchSize";
	public static final String OMNITURE_BATCH_SIZE = "OmnitureBatchSize";
	public static final String OMNITURE_TOTAL_SEARCHED_TERMS = "OmnitureTotalSearchedTerms";
	public static final String OMNITURE_TOP_SEARCHED_TERMS = "OmnitureTopSearchedTerms";
	public static final String OMNITURE_REPORT_SUITE_ID = "OmnitureReportSuiteId";
	public static final String OMNITURE_REPORT_DAYS_TO = "OmnitureReportDaysTo";
	public static final String OMNITURE_REPORT_DAYS_FROM = "OmnitureReportDaysFrom";
	public static final String OMNITURE_REPORT_METRIC_ID = "OmnitureReportMetricId";
	public static final String OMNITURE_API_USER_NAME = "OmnitureAPIUserName";
	public static final String OMNITURE_API_USER_NAME_IN_RESPONSE = "OmnitureAPIUserNameInResponse";
	public static final String OMNITURE_API_DEFAULT_USER_NAME_IN_RESPONSE = "bbbywebmaster";
	public static final String OMNITURE_API_SECRET_KEY = "OmnitureAPISecretKey";
	public static final String OMNITURE_API_NO_OF_THREAD = "OmnitureAPINumberOfAvailableThread";
	public static final String OMNITURE_UNKNOWN_PURCHASE = "unknown at time of purchase";
	public static final String OMNITURE_NO_SEARCH = "non-search";
	public static final String OMNITURE_UNSPECIFIED = "::unspecified::";
	public static final String OMNITURE_UNDEFINED = "undefined";
	public static final String OMNITURE_PRODUCT_OTHER = "::other::";
	public static final String OMNITURE_REPORT_UPDATED = "RecordsUpdated";
	public static final String OMNITURE_EMAIL_SUBJECT = "omnitureEmailSubject";
	public static final String OMNITURE_REPORT_DETAILS_PARA = "<p>";
	public static final String OMNITURE_REPORT_DETAILS_PARA_END = "</p>";
	public static final String OMNITURE_REPORT_ID = "ReportID";
	public static final String SMTP_CONFIG = "SMTPConfig";
	public static final String RECIPIENT_FROM = "RecipientFrom";
	public static final String RECIPIENT_TO = "RecipientTo";
	public static final String OMNITURE_GET_REPORT_MSSG_TXT = "txt_omniture_boosting_report_get_mssg";
	public static final String OMNITURE_GET_REPORT_BODY_TXT = "txt_omniture_boosting_report_body";
	public static final String OMNITURE_PURGE_FAIL_MSSG_TXT = "txt_omniture_purge_failure_mssg";
	public static final String OMNITURE_PURGE_FAIL_BODY_TXT = "txt_omniture_purge_failure_body";
	public static final String OMNITURE_ARCHIVAL_FAIL_MSSG = "txt_omniture_archival_failure_mssg";
	public static final String OMNITURE_ARCHIVAL_FAIL_BODY = "txt_omniture_archival_failure_body";
	public static final String OMNITURE_QUEUE_REPORT_MSSG = "txt_omniture_queue_report_mssg";
	public static final String OMNITURE_QUEUE_REPORT_BODY = "txt_omniture_queue_report_body";
	public static final String OMNITURE_GENERAL_FAIL_MSSG = "txt_omniture_general_failure_mssg";
	public static final String OMNITURE_GENERAL_FAIL_BODY = "txt_omniture_general_failure_body";
	public static final String OMNITURE_TOP_PRODUCT_COUNT_EPH_MAPPING = "topProductCountForKeywordToEPHMapping";
	public static final String SITESPECT_EPH_SCHEME_PARAMETER = "siteSpectSearchEPHParameter";
	public static final String CURRENT_EPH_SCHEME = "currentEPHScheme";
	public static final String SAVED_EPH_SCHEME = "savedEPHScheme";
	public static final String SITESPECT_EPH_SCHEME_PARAMETER_VALUE = "X-siteSpectSearchEPH";
	public static final String SWD_SEARCH_URL_PARAM_NAME = "swdEPHSearchUrlParam";
	public static final String SWD_SEARCH_URL_PARAM_NAME_DEFAULT_VALUE = "ef";
	public static final String FILTER_VALUE = "filter";
	public static final String SORT_VALUE = "sort";
	public static final String SORT_FILTER_OFF = "off";
	public static final String EPH_LOOK_UP_FLAG = "ephLookUpFlag";
	public static final String EPH_ID = "EPH_ID";
	public static final String NODE_ID = "Node_ID";
	public static final String OR = "OR";
	public static final String PROD_COUNT_FOR_EPH_AND_CUSTOM_BOOSTING = "prodCountForEPHAndCustomBoosting";
	public static final String MAX_PARSED_STRING_LEN_FOR_EPH = "maxParsedStringLenforEPH";
	public static final String STORE_ALL_DIMENSION_IN_BOOSTING_CACHE = "storeAllDimensionInBoostingCache";
	public static final String EPH_CATEGORY_MAP_CACHE_LOAD_CACHE_NAME = "KEYWORD_EPH_CAT_MAP_CACHE_NAME";
	public static final String EPH_QUERY_SCHEME = "ephQueryScheme";
	public static final String EPH_RESULT_MAP = "ephResultMap";
	public static final String EPH_QUERY_SCHEME_DEFAULT_OFF = "OFF";
	public static final String EPH_PROD_NODE_ID = "EPH_PROD_NODE_ID";
	public static final String SEARCH_KEYWORD = "searchKeyword";
	public static final String EPH_IDS = "ephIds";
	public static final String CATEGORYIDS = "categoryIds";
	public static final String SQLEXCEPTION = "SQL_EXCEPTION";
	public static final String EPH_CATEGORY_MAPPING = "ephCategoryMapping";
	public static final String INVALId_SKU = "Invalid SKU";
	public static final String SYSTEM_EXCEPTION = "System Exception occured";
	public static final String DYN_USER_CONFIRM = "DYN_USER_CONFIRM";
	public static final String DYN_USER_ID = "DYN_USER_ID";
	public static final String IS_DYN_USER_COOKIE_EXISTS = "isDynUserCookieExists";
	public static final String FACET_BOOSTING_DEFAULT_PRODUCTION_COUNT = "FacetBoostingDefaultProductCount";

	public static final String SORT_BOOSTING_DEFAULT_PRODUCTION_COUNT = "SortBoostingDefaultProductCount";
	public static final String FIFTY_STRING = "50";

	public static final String STORE_LOCAL_INVENTORY = "storeLocalInventory";
	public static final String INVENTORY_STATUS = "invStatus";
	public static final String INVENTORY_NOT_AVAILABLE = "INVENTORY_NOT_AVAILABLE";
	public static final String ERROR_MSG_INVENTORY_NOT_AVAILABLE = "inventoryNotAvailableInFavStore";
	public static final String OUTPUT_INVENTORY_NOT_AVAILABLE = "outputInventoryNotAvailable";
	public static final String STOCK_LEVEL = "stockLevel";
	public static final String ERROR_FROM_LOCAL_INVENTORY = "ERROR_FROM_LOCAL_INVENTORY";
	public static final String ERROR_FROM_INVENTORY = "errorFromLocalStoreInventory";
	public static final String ERROR_IN_VIEW_ALL_STORES = "ERROR_IN_VIEW_ALL_STORES";
	public static final String ERROR_VIEW_ALL_STORES = "errorinViewAllStores";
	public static final String ERROR_IN_VIEW_FAV_STORE = "ERROR_IN_VIEW_FAV_STORES";
	public static final String ERROR_VIEW_FAV_STORES = "errorinViewFavStores";
	public static final String CONFIG_KEY_PDP_RADIUS = "radius_fis_pdp";
	public static final String FAVORITE_STORE_ID = "favoriteStoreId";
	public static final String Default_Store_Type = "DefaultStoreType";

	// BBBSL-6574 | Adding constants for request URL and response URL for
	// Certona
	public static final String CERTONA_REQUEST_URL = "requestURL";
	public static final String CERTONA_RESPONSE_XML = "responseXML";
	public static final String WEBSERVICE_ERROR = "";
	public static final String CERTONA_TARGET_URL = "http://www.res-x.com/ws/r2/resonance.aspx";
	public static final String DEFAULT_ERROR_MSG_FAV_STORE = "There is some error in fetching avaibility from your preferred store.";
	public static final String DEFAULT_ERROR_MSG_VIEW_STORES = "We're sorry!  A system error occurred. Please try again or contact us (1-800-462-3966) to place order";
	public static final String EXCLUSION_TEXT = "exclusionText";
	public static final String APPLIED_COUPONVO = "appliedCouponVo";
	public static final String COOKIE_AUTO_LOGIN = "cookieAutoLogin";
	public static final String TBS_SITE_PREFIX = "TBS_";
	public static final String RESERVE_NOW = "reserveNow";
	public static final String FAV_STORE_STATE = "favStoreState";
	public static final String NOTIFY_CONSTANT = "notify";
	public static final String DISPLAY_MESSAGE = "displayMessage";
	public static final String FAVOURITE_STORE_REQ = "favStoreId";
	public static final String FAVORITE_STORE_DETAILS = "storeDetailsFavStore";
	public static final String STORE_DETAILS = "storeDetails";
	public static final String PRODUCT_AVAILABLE_VIEW_STORE = "productAvailableViewStore";
	public static final String ORDER_QUANTITY = "orderedQty";
	public static final String IS_FROM_PDP = "isFromPDP";
	public static final String OUTPUT_NOT_ELIGIBLE = "outputNotEligible";
	public static final String FAV_STORE_DETAIL = "storeDetailsFavStore";
	public static final String OUTPUT_FAV_STORE = "outputFavStore";
	public static final String EMPTY_FAV_STORE = "favStoreEmpty";
	public static final String OUTPUT_VIEW_STORE = "outputViewStore";
	public static final String EMPTY_VIEW_STORE = "viewStoreEmpty";

	// EDW
	public static final String EDW_SITESPECT_HEADER = "personalize";
	public static final String EDW_PERSONALIZATION_REQD = "personalizationReqd";
	public static final String EDW_PROFILE_LOADED = "profileLoaded";
	public static final String EDW_PROFILE_DATA = "personalization_data";
	public static final String EDW_ITEM_DESCRIPTOR = "bbbEDWData";
	public static final String EDW_BCC_FIELDS = "EDWfields";
	public static final String TWENTY_FIVE = "25";
	public static final String TWENTY_NINE = "29";

	public static final String PROFILE_ATTRIBUTE_VALUES = "profileAttributeValues";
	public static final String LAST_MODIFIED_DATE = "lastModifiedDate";

	public static final String LAST_MAINT_PROGRAM = "LastMaintProgram";
	public static final String ERROR_CART_MAX_REACHED = "ERROR_CART_MAX_REACHED";
	public static final String PROFILE_SESSION_MESSAGE = "A session already exists, and the username and/or password you have given differ from those of the currently authenticated user. Logging out the currently authenticated session.";
	public static final String IS_ITEM_IN_WISHLIST = "isItemInWishlist";
	public static final String WISH_LIST = "wishlist";
	public static final String EMPTY_INPUT = "emptyInput";
	public static final String LBL_FIND_IN_YOUR_STORE = "lbl_find_in_your_store";
	public static final String LOCAL_STORE_OFF = "localStoreOff";
	public static final String CALL_STORE = "callStore";

	public static final double DOUBLE_TWENTY_FIVE = 25;
	public static final String REC_USER_CHECKOUT = "REC_USER_CHECKOUT";
	public static final String CHECKOUT_TRUE = "?checkout=true";
	public static final String EMPTY_RESPONSE_DOM = "emptyDomResponse";
	public static final String ORACLE_RESPONSYS_FLAG = "oracleResponsysFlag";
	public static final String RESPONSYS_COOKIE_NAME = "ResponsysCookieName";
	public static final String RESPONSYS_ENABLED = "ResponsysEnabled";
	public static final String ORACLE_RESPONSYS_URL = "OracleResponsys_url";
	public static final String IS_FROM_GIFT_REGISTRY = "isFromGiftRegistry";
	public static final String SITEBAB_CA_TBS = "TBS_BedBathCanada";

	public static final String BBB_AJAX_HEADER_PARAM = "BBB-ajax-redirect-url";
	public static final String BBB_AJAX_REQUEST_PARAM = "BBB-ajax-request";
	public static final String USE_MY_CURRENT_LOCATION = "useMyCurrentLocation";
	public static final String LBL_PROMO_KEY_CENTER = "lbl_promo_key_center";
	public static final String GREATER_THAN_SYMBOL = ">";

	public static final String DEPARTMENT = "DEPARTMENT";
	public static final String SHIPPING_DT = "Shipping";
	public static final String JMS_INVENTORY_DEC = "JMSInventoryDec";
	public static final String RELATED_CATEGORIES = "relatedCategories";
	public static final String RELATED_CATEGORIES_COUNT = "relatedCategoriesCount";

	// TBS Search
	public static final String PARTIAL_FLAG = "partialFlag";
	public static final String ORIG_SEARCH_TERM = "origSearchTerm";
	public static final String COMMA_CONCATE_ZERO = ",0";
	public static final String ENTERED_SEARCH_TERM = "enteredSearchTerm";
	public static final String UPC_SEARCH = "UPC_Search";
	public static final String PAGE_NUM = "pagNum";
	public static final String PAGE_SORT_OPT = "pagSortOpt";
	public static final String BCC_SORT_CODE = "bccSortCode";
	public static final String BCC_SORT_ORDER = "bccSortOrder";
	public static final String SEARCH_TERM = "searchTerm";
	public static final String BROWSE_SEARCH_VO = "browseSearchVO";
	public static final String RECORD_TYPE = "Record Type";

	// Free Shipping
	public static final String HIGHER_SHIP_THRESHHOLD = "higherShipThreshhold";
	public static final String TXT_FREESHIPPING_COLLECTIONS_PRODUCT = "txt_freeshipping_collections_product";
	public static final String TXT_FREESHIPPING_PRODUCT = "txt_freeshipping_product";
	public static final String SHIP_MSG_DISPLAY_FLAG = "ShipMsgDisplayFlag";
	public static final String APPOINT_TYPE_STORE_LOCATOR = "appointTypeStoreLocatorPage";
	public static final String SKEDGE_ME = "skedgeMe";
	public static final String IS_SKU_LTL = "isSkuLtl";
	public static final String CLEARANCE_PRICE = "clearancePrice";
	public static final String ORDER_CONTAINS_EMPTY_SG = "orderContainsEmptySG";
	public static final String ORDER_HAS_LTL = "orderHasLtl";
	public static final String STORE_SHIPPING_GROUP = "storeShippingGroup";

	// same day delivery constants
	public static final String CUSTOMER_ZIP = "customerZip";
	public static final String SAME_DAY_DELIVERY_FLAG = "SameDayDeliveryFlag";
	public static final String SAME_DAY_DELIVERY_KEYS = "SameDayDeliveryKeys";
	public static final String SDD = "SDD";
	public static final String SDD_ON_CART_FLAG = "sddOnCartFlag";
	public static final String SDD_ITEM_UNAVAILABLE = "unavailable";
	public static final String SDD_ITEM_AVAILABLE = "available";
	public static final String SDD_ITEM_INELIGIBLE = "ineligible";
	public static final String SDD_ITEM_STORE_NOT_IN_SESSION = "storeNotInSession";
	public static final String ITEM_INELIGIBLE = "itemIneligible";
	public static final String MARKET_INELIGIBLE = "marketIneligible";
	public static final String MARKET_ELIGIBLE = "marketEligible";
	public static final String ITEM_ELIGIBLE = "itemEligible";
	public static final String ITEM_UNAVAILABLE = "itemUnavailable";
	public static final String SDD_ELIGIBLE = "sddEligible";
	public static final String ADDRESS_INELIGIBLE = "addressIneligible";
	public static final String SDD_COOKIE_MAX_AGE = "sddCookieMaxAge";
	public static final String PRODUCT_SDD_INELIGIBLE = "product_sdd_ineligible";
	public static final String PRODUCT_SDD_ELIGIBLE = "product_sdd_eligible";
	public static final String PRODUCT_SDD_MARKET_INELIGIBLE = "product_sdd_market_ineligible";
	
	public static final String SDD_ELIGIBLITY_STATUS = "sddEligiblityStatus";
	public static final String SDD_OPTION_ENABLED = "sddOptionEnabled";

	// SDD constants
	public static final String SDD_ENABLED = "sddEnabled";
	public static final String INPUT_ZIP = "inputZip";
	public static final String API_KEY = "Api-Key";
	public static final String DELIV_API_KEY = "delivApiKey";
	public static final String DELIV_URL_KEY = "delivAPIURL";
	public static final String DELIV_CONTENT_TYPE_KEY = "delivContentTypeKey";

	public static final String PROMO_MAP = "promoCouponMap";
	public static final String SKU_MAP = "skuMap";
	public static final String SHIPPING_DISCOUNT = "Shipping Discount";
	public static final String ITEM_DISCOUNT = "Item Discount";
	public static final String COUPON_CODE = "couponCode";

	public static final String SEVERITY_WARNING = "Warning";
	public static final String SEVERITY_ERROR = "Error";
	public static final String SB_ERROR_MESSAGE = "message";
	public static final String SB_ERROR_MESSAGES = "messages";
	public static final String SB_AVAILABILITY = "availability";
	public static final String SB_AVL_DETAILS = "availabilityDetails";
	public static final String SB_AVL_DETAIL = "availabilityDetail";
	public static final String SB_ITEM_NAME = "itemName";
	public static final String SB_FACILITY_NAME = "facilityName";
	public static final String SB_VIEW_NAME = "FACILITY_VIEW_NAME";
	public static final String SB_TRANSACTION_DATETIME = "transactionDateTime";
	public static final String SB_VIEW_CONFIG = "viewConfiguration";
	public static final String SB_VIEW_NAME_IN_RESPONSE = "viewName";
	public static final String SUPPLY_BALANCE_HTTP_URL = "END_POINT_SUPPLY_BALANCE_CALL";

	public static final String EOM_THRESHOLD_FLAG = "EOM_Threshold_Flag";
	public static final String EOM_ENABLE_FLAG = "EnableEOM";
	public static final String EOM_BATCH_SIZE = "EOMBatchSize";
	public static final String TBS_NETWORK_CALL_VIEW_NAME = "TBS_NETWORK_CALL_VIEW_NAME";
	public static final String END_POINT_INVENTORY_CALL = "END_POINT_INVENTORY_CALL";

	public static final String SITEBAB_BABY_TBS = "TBS_BuyBuyBaby";
	public static final String SESSION_BEAN_NAME = "sessionBean";
	public static final String ACTION_REQ = "actionReq";
	public static final String SAME_DAY_DELIVERY_REQ = "SameDayDeliveryReq";

	public static final String GLOBAL_PROMO_PROPERTY = "global";
	public static final String MOVE_WISHLIST_TO_ORDER = "moveWishListItemToOrder";
	public static final String MOVE_FROM_CART_To_SFL = "moveFromCartToSaveForLater";
	public static final String SEND_SFL_COOKIE = "sendSFLCookie";

	public static final String REPORT_DATA = "reportData";
	public static final String OPB_KEYWORD = "keyword";
	public static final String BOOST_SCORE = "boostScore";
	public static final String STRING_DEL = ",";
	public static final String CONCEPT_BAB_CA = "BEDBATHCA";
	public static final String ENABLE_OPB_BOOSTING_FLAG = "enableOPBBoosting";
	public static final String OPB_ENABLED = "OPBEnabled";
	public static final String OPB_RESULTS_POSITION = "OPBResultsPosition";
	public static final String OPB_PREPEND = "PREPEND";
	public static final String P_PRODUCT_SITE_ID = "P_Product_Site_ID";
	public static final String SET_COOKIE = "setCookie";
	public static final String IS_FROM_STORE_LOCATOR = "isFromStoreLocator";
	public static final String OUT_OF_STOCK_FLAG = "outOfStockFlag";
	public static final String STOCK_LEVEL_COLUMN = "STOCK_LEVEL";
	public static final String STORE_ID_COLUMN = "STORE_ID";
	public static final String LOCAL_STORE_FETCH = "LocalStoreRepoFetch";
	public static final String CACHE_STORE_INV = "localstore-near-local-store-inv";

	public static final String INPUT_LINK = "InputLink";
	public static final String OUTPUT_LINK = "OutputLink";
	public static final String STORE = "store";

	// BBB EDW Constants
	public static final String EDW_Stale_Data = "staleEDWData";
	public static final String EDW_SiteSpect_repo = "EDWSiteSpectData";
	public static final String EDW_Data_Key = "edwKey";
	public static final String IS_SITESPECTDATA = "sendToSiteSpect";
	public static final String IS_TIBCODATA = "sendToTIBCO";
	public static final String PROFILE_EDW_DATA = "profileEDWData";
	public static final String EDW_COFIG_TTL = "EDW_TTL";
	public static final String EDW_DATA_SERVICE = "edwJMSMessage";
	public static final String MAX_EDW_REPO_RETRY = "maxEDWRepoRetry";
	public static final String EDW_APP_NAME = "ProfileDataIntegrationATG-EDW";
	public static final String EDW_EMAIL_ADDRESS = "emailAdress";

	public static final String ERR_REMOVE_ITEM = "error_remove_items";
	public static final String ERR_IN_LOG_OUT = "error_log_out";
	public static final String RECGONIZED_USER_FLOW = "recgonized_user_flow";
	public static final String GUEST_CHECKOUT = "guestCheckout";
	public static final String STATE_CODE_QC = "QC";
	public static final String RECGONIZED_GUEST_USER_FLOW = "recgonized_guest_user_flow";
	public static final String SESSIONIDFORSPC = "sessionIdForSPC";
	public static final String TIBCO_ENABLED_CONFIG_KEY = "is_tibco_enabled";

	public static final String RECORDS_INELIGIBLE = "recordsInEligible";
	public static final String TBS_BEDBATH_US = "TBS_BedBathUS";
	public static final String OMNITURE_TOTAL_RECORDS = "totalRecords";
	public static final String ORDER_DISCOUNT = "Order Discount";
	public static final String SHIP_DISCOUNT = "Ship Discount";
	public static final String TBS_PRICING_SERVICE_FLAG = "TBS_PRICING_SERVICE_FLAG";
	public static final String PROMO_DISPLAY_NAME = "displayName";
	public static final String PRODUCT_COMPARISON_LIST_ITEMS = "comparisonListItems";
	public static final String AKAMAI_HEADER = "X-Akamai-Edgescape";
	public static final String ZIP = "zip";

	public static final String GET_PROMOTION_QUERY = "id=?0";
	public static final String ATG_PROMOTION = "ATG_PROMOTION";

	public static final String COUPON_LIST = "couponList";
	public static final String PROMOTION_MAIN_IMAGE_KEY = "mainImage";
	public static final String ORIG = "ORIG";

	public static final String HTML_TABLE_START = "<table border=\"1\">";
	public static final String HTML_TABLE_END = "</table>";
	public static final String HTML_TABLE_ROW_START = "<tr>";
	public static final String HTML_TABLE_ROW_END = "</tr>";
	public static final String HTML_TABLE_DATA_START = "<td>";
	public static final String HTML_TABLE_DATA_END = "</td>";

	public static final String REPORT_TABLE_HEADER = "<tr><th>Report ID</th><th>Conecpt</th><th>Queued Date</th><th>Report Type</th><th>Status</th><th>Records Updated</th><th>Exception</th></tr>";
	public static final String QUEUED_REPORT_TABLE_HEADER = "<tr><th>Report ID</th><th>Conecpt</th><th>Queued Date</th><th>Report Type</th><th>Status</th><th>Exception</th></tr>";
	public static final String INPROGRESS_REPORT_TABLE_HEADER = "<tr><th>Report ID</th><th>UserId</th><th>Report Suite Id</th></tr>";
	public static final String CONSUMED_REPORT_TABLE_HEADER = "<tr><th>Report ID</th><th>Conecpt</th><th>Queued Date</th><th>Report Type</th><th>Status</th><th>Records Updated</th><th>Number Of Search Terms</th><th>Records Archived/Records Deleted</th><th>Exception</th></tr>";
	public static final String REPORT_TABLE_EXCEPTION_HEADER = "<tr><th>Exception</th></tr>";
	public static final String QUEUED_EMAIL_EVENT = "Queued_Report_Email_Event";
	public static final String INPROGRESS_EMAIL_EVENT = "InProgress_Report_Email_Event";
	public static final String PREVIOUS_REPORT_HIGHEST_RANK = "Highest_Rank";
	public static final String DYNAMIC_REPOSITORY_CACHE_NAME = "DYNAMIC_REPOSITORY_CACHE_NAME";
	public static final String DYNAMIC_REPOSITORY_CACHE_NAME_PRODUCT = "DYNAMIC_REPOSITORY_CACHE_NAME_PRODUCT";
	public static final String DYNAMIC_REPOSITORY_CACHE_NAME_SKU = "DYNAMIC_REPOSITORY_CACHE_NAME_SKU";
	public static final String DYNAMIC_REPOSITORY_CACHE_TIMEOUT = "DYNAMIC_REPOSITORY_CACHE_TIMEOUT";
	public static final String HEADERS_AUTH_BASIC = "Basic ";
	public static final String HEADERS_APP_JSON = "application/json";
	public static final String SBC_USERNAME = "SBC_USERNAME";
	public static final String SBC_PASSWORD = "SBC_PASSWORD";
	public static final String REPORT_TYPE = "reportType";
	public static final String OAUTH_TOKEN_SCOPE = "oauth_token_scope";
	public static final String OAUTH_TOKEN_CLIENT_SECRET = "oauth_token_client_secret";
	public static final String OAUTH_TOKEN_CLIENT_ID = "oauth_token_client_id";
	public static final String OAUTH_TOKEN_GRANT_TYPE = "oauth_token_grant_type";
	public static final String OAUTH_TOKEN_HTTP_URL = "oauth_token_http_url";
	public static final String OAUTH_TOKEN_BUFFER_EXPIRE_TIME = "oauth_token_buffer_expire_time";
	public static final String SCOPE = "scope";
	public static final String CLIENT_SECRET = "client_secret";
	public static final String REQ_PARAM_CLIENT_ID = "client_id";
	public static final String GRANT_TYPE = "grant_type";
	public static final String J_USERNAME = "j_username";

	// Vendor Constants
	public static final String VENDOR_SEARCH_PARAM = "VENDOR_SEARCH_PARAM";
	public static final String FROM_BRAND_PAGE = "frmBrandPage";
	public static final String VENDOR_KEYS = "VendorKeys";
	public static final String VENDOR_FLAG = "VendorFlagOn";
	public static final String VENDOR_PARAM = "VendorParam";
	public static final String VENDOR_PARAM_MOBILE = "vendorParamValue";
	public final static String SITE_SPECT_VENDOR_CODE = "X-siteSpectSearchAlVenCode";
	public static final String SET_VENDOR_SEARCH_PARAM_SERVLET = "SetVendorSearchParamServlet";
	public static final String SEARCH_VENDOR_LIST_ACTIVE = "SearchVendorListActive";
	public static final String START_WITH = "startWith";
	public static final String AKAMAI_BOT_HEADER = "ak_bot";
	public static final String MANAGE_CHECKLIST_LINK = "ManageCheckListLink";
	public static final String ARRAY_SEPARETOR = "}";
	public static final String ARRAY_SEPARETORSTART = "{registryType:";
	public static final char ARRAY_SEPARETOR_COLON = '"';
	public static final String FROM_REGISTRY_ACTIVITY = "fromRegistryActivity";
	public static final String CAT_ID = "catId";
	public static final String CAT1_ID = "cat1Id";
	public static final String CAT2_ID = "cat2Id";
	public static final String CAT3_ID = "cat3Id";
	public static final String DROPLET_MANAGE_REGSITRY_CHECKLIST = "ManageRegistryChecklistDroplet.service()";
	public static final String INTERACTIVE_CHECKLIST_KEY = "interactive_checklist_key";
	public static final String IS_DISABLED = "isDisabled";
	public static final String SUBTYPE_CODE = "subTypeCode";
	public static final String CHECK_LIST_CATEGORIES = "checkListCategories";
	public static final String CHECKLIST_CATEGORY_ITEM_DESCRIPTOR = "checkListCategory";
	public static final String CATEGORY_NAME = "categoryName";
	public static final String CATEGORY_URL = "categoryURL";
	public static final String SEQUENCE_NUMBER = "sequenceNumber";
	public static final String CHECKLIST_IMAGE_URL = "imageURL";
	public static final String SUGGESTED_QUANTITY = "suggestedQuantity";
	public static final String PRIMARY_PARENT_CATEGORY_ID = "primaryParentCategoryId";
	public static final String BABY_CATEGORY_URL = "babyCategoryURL";
	public static final String BABY_IMAGE_URL = "babyImageURL";
	public static final String CA_CATEGORY_URL = "caCategoryURL";
	public static final String CA_IMAGE_URL = "caImageURL";
	public static final String TBS_CATEGORY_URL = "tbsCategoryURL";
	public static final String TBS_IMAGE_URL = "tbsImageURL";
	public static final String CHILD_CHECKLIST_CATEGORY_ID = "childCheckListCategoryId";
	public static final String USER_COMPLETED_CATEGORY_LIST = "userCompletedCategoryList";
	public static final String PACKAGE_COUNT = "packageCount";
	public static final String CATEGORIES_LIST = "categoriesList";
	public static final String CHECKLIST_FLAG = "CheckListFlag";

	public static final String CHECKLIST = "checkList";
	public static final String DROPLET_CHECKLIST = "ChecklistDroplet.service()";
	public static final String THRESHOLD_CHECKLIST_PERCENTAGE_RANGE = "ThresholdCheckListPercentage";
	public static final String CHECKLIST_PERCENTAGE_RANGE = "CheckListPercentageRange";
	public static final String CHECKLIST_PERCENTAGE = "CheckListPercentage";
	public static final String ZER0_CHECKLIST_PERCENTAGE = "0_CheckListPercentage";
	public static final String HUNDRED_CHECKLIST_PERCENTAGE = "100_CheckListPercentage";
	public static final String SKU = "sku";
	public static final String INTERACTIVE_CHECKLIST_CACHE_NAME = "INTERACTIVE_CHECKLIST_CACHE_NAME";
	public static final String INTERACTIVE_CHECKLIST_CACHE_TIMEOUT = "INTERACTIVE_CHECKLIST_CACHE_TIMEOUT";
	public static final String CHECKLIST_CACHE_ENABLED = "ChecklistCacheEnabled";
	public static final String CHECKLIST_ID = "checklistId";
	public static final String LIST_ID = "list_id";
	public static final String CHECKLIST_THREAD_POOL_SIZE = "CheckListCategoryIdThreadPoolSize";
	public static final String LIST_CAT_ID = "list_cat_id";
	public static final String USER_MANUAL_CHECKED_CATEGORIES = "userManualCheckedCategories";
	public static final String INSERT_REPORT_BATCH_SIZE = "insertReportBatchSize";
	public static final String EPH_MAPPING_INSERT_BATCH_SIZE = "batchSizeToInsertKeywordEPHMapping";
	public static final String EPH_MAPPING_UPDATE_BATCH_SIZE = "batchSizeToUpdateKeywordEPHMapping";
	public static final String SEARCH_CACHE_SCHEDULER_TYPE = "SearchCacheSchedulerKeys";
	public static final String POOL_SIZE = "PoolSize";
	public static final String TOTAL_SIZE = "TotalSize";
	public static final int DEFAULT_BATCH_SIZE = 1000;
	public static final String INSERT_L2L3BRAND_BATCH_SIZE = "insertL2L3BrandBatchSize";
	public static final String SHOW_ON_CHECKLIST = "isShowOnCheckList";

	public static final String SHOW_STATIC_CHECKLIST = "StaticCheckList";
	public static final String COMMITMENT = "Commitment";
	public static final String SHOW_ON_RLP = "isShowOnRLP";
	public static final String ALWAYS_SHOW_ON_RLP = "isAlwaysShowOnRLP";
	public static final String SHOW = "show";
	public static final String HIDE = "hide";
	public static final String CHECKLIST_VO = "checklistVO";
	public static final String TOTAL_C3_QUANTITY_ADDED = "totalC3QuantityAdded";
	public static final String CHECKLIST_PROGRESS_VO = "checkListProgressVO";
	public static final String TOTAL_C3_SUGGESTED = "totalC3QuanitySuggested";
	public static final String NO_OF_C1 = "noOfC1";
	public static final String AVERAGE_PERCENTAGE = "averagePercentage";
	public static final String IS_CHECKLIST_SELECTED = "isChecklistSelected";
	public static final String STATIC_CHECKLIST = "staticChecklist";
	public static final String OTHER = "other";
	public static final String ON = "on";
	public static final String OFF = "off";
	public static final String STATIC = "static";
	public static final String DYNAMIC = "dynamic";
	public static final String GUIDE = "guide";
	public static final String CHECKLIST_PROGRESS = "CheckListProgress";
	public static final String COLLEGE_REG = "College";
	public static final String UNIVERSITY_REG = "University";
	public static final String IS_CONFIGURE_COMPLETE = "isConfigComplete";
	public static final String COMPLETE_IMAGE = "_complete?";
	public static final String DECIMAL_PLACES = "#.##";
	public static final String STATIC_CHECKLIST_VO = "staticChecklistVO";
	public static final String C3_ADDED_QUANTITY = "c3AddedQuantity";
	public static final String L2L3BRAND = "L2L3BRAND";
	public static final String CEREMONY = "Ceremony";
	public static final String C1_ID = "c1id";
	public static final String C2_ID = "c2id";
	public static final String C3_ID = "c3id";
	public static final String C1_NAME = "c1name";
	public static final String C2_NAME = "c2name";
	public static final String C3_NAME = "c3name";
	public static final String OF = "of";
	public static final String IS_DELETED = "isDeleted";
	public static final String FROM_REGISTRY_CONTROLLER = "fromRegistryController";
	public static final String LOAD_SELECTED_CATEGORY = "loadSelectedCategory";
	public static final String SELECTED_GUIDE_TYPE = "selectedGuideType";
	public static final String GUIDE_COOKIE_MAX_AGE = "interactive_guide_cookie_max_age";
	public static final String INTERACTIVE_GUIDE_COOKIE_NAME = "ShoppingGuidesCookie";
	public static final String SELECTED_GUIDE_VO = "selectedGuideVO";
	public static final String FROM_AJAX = "fromAjax";
	public static final String OBB = "BRAND";
	public static final String OBL2 = "L2";
	public static final String OBL3 = "L3";
	public static final String ALL = "All";
	public static final String BRAND = "Brand";
	public static final String NON_BROWSE = "non-browse";
	public static final String UNKNOWN_AT_TIME_OF_PURCHASE = "unknown at time of purchase";
	public static final String ENABLE_L2_L3_BRAND_PAGE_BOOSTING = "EnableL2L3BrandPageBoosting";

	public static final String SORT_ALGORITHM_NUMBER_OF_DAYS = "SORT_ALGORITHM_NUMBER_OF_DAYS";
	public static final String TOTAL_BOOSTED_PRODUCTS_COUNT = "BoostedProductsForZeroOBP";
	public static final String CATEGORY_PAGE = "Category Page";
	public static final String POPULAR_SEARCHES = "Popular Searches";

	// constants for facet descriptors
	public static final String DIM_DISPLAY_CONFIGTYPE = "DimDisplayConfig";
	public static final String FACET_DESCRIPTOR_COLORGROUP = "Color";
	public static final String COLORGROUP_KEY = "ColorGroup";
	public static final String ENABLE_COLOR_RELEVANCY = "EnableColorRelevancy";
	public static final String KEYWORD_BOOST_FLAG = "KeywordBoostFlag";
	public static final String L2L3_BOOST_FLAG = "L2L3BoostFlag";
	public static final String BRANDS_BOOST_FLAG = "BrandsBoostFlag";
	public static final String SET_KEYWORD_SEARCH_CACHE = "setKeywordSearchCache";
	public static final String SETKEYWORD_SEARCHMATCH_ALL_CACHE = "setKeywordSearchMatchallCache";
	public static final String SET_BRANDS_CACHE = "setBrandsCache";
	public static final String SET_SEARCH_RESULTS_CACHE = "setSearchResultsCache";
	public static final String FETCH_RESULT_FROMCACHE = "fetchResultFromCache";
	public static final String DOSEARCH_AND_CUSTOMIZE = "doSearchandCustomize";
	public static final String BOOSTED_PRODUCTS_CACHE_TIMEOUT = "BOOSTED_PRODUCTS_CACHE_TIMEOUT";
	public static final String BOOSTED_PRODUCTS_CACHE = "boosted-products-near-cache";
	public static final String SEND_EMAIL = "sendEmail";
	public static final String STORE_EMAIL = "Email";
	public static final String LOCAL_STORE_EMAIL = "LocalStoreEmail";
	public static final String SET_CHECKLIST_CATEGORY_RESULTS_CACHE = "setChecklistCategoryResultsCache";
	public static final String CHECKLIST_CATEGORY_RESULTS_CACHE = "CHECKLIST_CATEGORY_RESULTS_CACHE";
	public static final String CHECKLIST_CATEGORY_RESULTS_CACHE_TIMEOUT = "CHECKLIST_CATEGORY_RESULTS_CACHE_TIMEOUT";
	public static final String REMOVE_OOS_BOOSTED_PRODUCT_CATEGORY = "isRemoveOOSBoostedProductForCategory";
	public static final String REMOVE_OOS_BOOSTED_PRODUCT_BRAND = "isRemoveOOSBoostedProductForBrand";

	public static final String SLASH_PLUS = "\\+";

	public static final String FROM_REIGISTRY_OWNER = "fromRegOwnerName";
	public static final String ENABLE_INCART_AND_DYNAMIC_PRICING_KEY = "DynamicPricing";
	public static final String ENABLE_LOCAL_SKU_DYNAMIC_PRICING_KEY = "enable_local_dynamic_pricing";

	public static final String DPI_DSLINEITEM_ITEM_TYPE_PROPERTY = "itemType";
	public static final String DPI_DSLINEITEM_PROPERTY = "dsLineItemTaxInfos";
	public static final String DETAILED_ITEM_PRICE_INFO = "detailedItemPriceInfo";

	public static final String FEED_ID = "FEED_ID";
	public static final String PRICE_FEED = "PRICEFEED";
	public static final String PIM_FEED = "PIMFEED";
	public static final String TARGET_PRODUCTION = "Production";
	public static final String RUN_DYNAMIC_PRICE_SP = "runDynamicPriceSP";

	public final static String OMNITURE_VARIABLE = "OmnitureVariable";
	public final static String ENDECA = "En";
	public final static String CONTROL = "control";
	public final static String DEFAULT_ENDECA_OMNITURE_VARIABLE = "DefaultEndecaOmnitureVariable";

	public static final String IS_FROM_BREADCRUMB = "isFromBreadcrumb";

	public static final String DATA_CENTER1 = "DC1";
	public static final String AMPERSAND_SPACE = " & ";
	public static final String HYPHEN_SPACE = " - ";

	public static final String DYNAMIC_PRODUCT_OBJECT = "productObject";
	public static final String DYNAMIC_SKU_OBJECT = "skuObject";

	public static final String TAX_OVERRIDE_SPECIAL_INSTRUCTION = "TaxOverride";
	public static final String ENABLE_REPO_CALL_DYN_PRICE = "enableDynamicRepoCall";

	public static final String LENGTH_OF_SWATCH = "lengthOfSwatch";
	public static final String EMAILTYPE = "emailType";
	public static final String FRMDATA_SITEID = "frmData_siteId";
	public static final String EMAILFROM = "emailFrom";
	public static final String EMAIL_CART_MESSAGE = "message";
	public static final String GUIDES = "guides";
	public static final String TITLE = "title";

	public static final String CACHE_REFRESH_REQUIRED = "CACHE_REFRESH_REQUIRED";
	public static final String PROPERTY_CACHE_CONTROL = "Cache-Control";
	public static final String BBBRESTRESPONSEPROPERTIESCONFIGURATIONSERVLET_SERVICE = "BBBRestResponsePropertiesConfigurationServlet.serviceRESTRequest";
	public static final String DEFAULT = "DEFAULT";
	public static final String FRM_BRAND_PAGE = "frmBrandPage";
	public static final String ADVANCED_ORDER_INQUIRY_KEYS = "AdvancedOrderInquiryKeys";
	public static final String PER_PAGE = "PerPage";
	public static final String POPULATE_KEYWORD_CACHE_FLAG = "populateKeywordCacheFlag";
	public static final String POPULATE_CATEGORY_CACHE_FLAG = "populateCategoryCacheFlag";
	public static final String POPULATE_BRAND_CACHE_FLAG = "populateBrandCacheFlag";
	public static final String TOP_CONSULTANTS_PREFIX = "/topconsultant/";
	public static final String SHOP_THIS_LOOK_PREFIX = "/shopthislook/";

	public static final String REFRESH_COUPON = "refreshCoupon";

	public static final String IS_ENDECA_CONTROL = "isEndecaControl";
	public static final String WEDDING_REG_CODE = "BRD";
	public static final String WEDDING_REG_NAME = "Wedding";
	public static final String COMMITMENT_REG_CODE = "COM";
	public static final String COMMITMENT_REG_NAME = "Commitment";
	public static final String ANNIVERSARY_REG_CODE = "ANN";
	public static final String ANNIVERSARY_REG_NAME = "Anniversary";
	public static final String HOUSEWARMING_REG_CODE = "HSW";
	public static final String HOUSEWARMING_REG_NAME = "Housewarming";
	public static final String COLLEGE_REG_CODE = "COL";
	public static final String COLLEGE_REG_NAME = "College";
	public static final String BIRTHDAY_REG_CODE = "BIR";
	public static final String BIRTHDAY_REG_NAME = "Birthday";
	public static final String RETIREMENT_REG_CODE = "RET";
	public static final String RETIREMENT_REG_NAME = "Retirement";
	public static final String BABY_REG_CODE = "BA1";
	public static final String BABY_REG_NAME = "Baby";
	public static final String OTHER_REG_CODE = "OTH";
	public static final String OTHER_REG_NAME = "Other";
	public static final String BABYBIRTHDAY_REG_CODE = "BR1";
	public static final String BABYBIRTHDAY_REG_NAME = "BabyBirthday";
	public static final String BABYOTHER_REG_CODE = "OT1";
	public static final String BABYOTHER_REG_NAME = "BabyOther";
	public static final String COMMITMENT_CEREMONY_REG_TYPE = "Commitment Ceremony";
	public static final String COLLEGE_REG_TYPE = "College/University";
	public static final String MANUAL_C3_CHECK = "manualC3Check";
	public static final String CHECKLIST_TYPE = "checklistType";
	public static final String CHECK_LIST_TYPE = "checkListType";
	public static final String TYPE_NAME = "typeName";
	public static final String NONREGISTRY = "NONREGISTRY";
	public static final String ITEM_PRICEOVERRIDEMIN = "ItemPriceOverrideMinAmount";
	public static final String TIBCO_TIMEOUT_KEY = "TibcoTimeOutKey";
	public static final String EXIM_MOCK_TEST = "eximMockTest";
	public static final String DOMAIN_REQ_PARAM = "domain";
	public static final String PATH_REQ_PARAM = "path";

	// GiftRegistryFormHandler Constants :
	public static final String REGISTRY_SKINNY_VO_LIST = "registrySkinnyVOList";
	public static final String INVALID_PROFILE_ID_IS_PROVIDED = "Invalid ProfileId is Provided.";
	public static final String GIFTREGISTRY_INVALID_CO_REGISTRANT_EMAIL = "GiftRegistry Invalid Co-registrant email from coregistrantValidation of GiftRegistryFormHandler";
	public static final String COREGISTRANT_VALIDATION_METHOD_ENDS = "GiftRegistryFormHandler.coregistrantValidation() method ends";
	public static final String SHOW_SHIPPING_ADDRESS = "showShippingAddress";
	public static final String SHOW_FUTURE_SHIPPING_ADDRESS = "showFutureShippingAddr";
	public static final String FUTURE_SHIPPING_DATE = "futureShippingDate";
	public static final String SHOW_CONTACT_ADDRESS = "showContactAddress";
	public static final String NURSERY_THEME = "nurseryTheme";
	public static final String PRODUCT_CATALOG_PATH = "/atg/commerce/catalog/ProductCatalog";
	public static final String ERR_WHILE_FETCHING_SKU_FROM_REPOSITORY = "error while fetching sku from the repository";
	public static final String LTL_FLAG = "ltlFlag";
	public static final String ERR_REG_SHIPPING_DATE_FURTURE_INVALID = "err_reg_shipping_date_future_invalid";
	public static final String ERR_CREATE_REG_BABYNAME_INVALID = "err_create_reg_babyname_invalid";
	public static final String INVALID_CELLPHNO_FROM_VALIDATECOLLEGE = "GiftRegistry Invalid registrant cell phone from validateCollege of GiftRegistryFormHandler";
	public static final String FROM_MX = "fromMx";
	public static final String RETURN_URL = "returnURL";
	public static final String TBS_BED_BATH_CANADA_SITE_CODE = "TBS_BedBathCanadaSiteCode";
	public static final String UPDATE_REG_SUMMARY_REQUIRED = "updateRegSummaryRequired";
	public static final String CHECKLIST_REGISTRY_TYPES = "Checklist_Registry_Types";
	public static final String CHECKLIST_URL_CONSTANT = "Checklist";

	public static final String ORDER_REPO_ITEM = "OrderRepositoryItem";
	public static final String ORDER_ID_FROM_MAP = "OrderID";
	public static final String ORDER_FROM_MAP = "Order";
	public static final String ORDER_MANAGER = "OrderManager";
	public static final String ORDER_REPOSITORY = "OrderRepository";
	public static final String EMAIL_PROPERTY_ON_ORDER = "emailAddress";
	public static final String INAVLID_ORDER = "Invalid order passed";
	public static final String ORDERMANAGER_NOT_AVAILABLE = "OrderManager not passed as parameter";
	public static final String ORDER_NOT_AVAILABLE = "Order does not exist";
	public static final String ORDERITEM_NOT_AVAILABLE = "Order repository item is reqired";
	public static final String MAX_CARD_FAIL_ATTEMPTS = "MaxCardFailAttempts";
	public static final String BBB_PROCESS_STATUS = "PARAM_VALUE";
	public static final String SPACE_HYPHEN = "[ /]";
	public static final String KEY_REBUILD_HEADER_CACHE_FLAG = "rebuildHeaderCacheFlag";
	public static final String QAS_TIME_OUT = "QasTimeOut";
	public static final List<String> errorCodeList = new ArrayList<>(
			Arrays.asList("6006", "1002", "1005", "2006", "1003", "1009", "2000"));
	public static final String CHECKLIST_CATEGORY_ID = "checklistCategoryId";
	public static final String CHECKLIST_DISPLAY_NAME = "ckDisplayName";
	public static final String CAT1_NAME = "cat1Name";
	public static final String CAT2_NAME = "cat2Name";
	public static final String CAT3_NAME = "cat3Name";
	public static final String SEO_URL = "seoURL";
	public static final String SORT_OPTIONS = "sortOptions";

	public static final String FROM_ORDER_FEED_LISTENER = "isFromOrderFeedListener";

	public static final String INTERACTIVE_CHECKLIST_REPOSITORY_CACHE = "Interactive_Checklist_RepositoryCache";
	public static final String IS_FROM_ORDER_DETAIL = "isFromOrderDetail";
	public static final String HOLIDAY_MESSAGING_START_DATE = "holidayMessagingStartDate";
	public static final String HOLIDAY_MESSAGING_END_DATE = "holidayMessagingEndDate";
	public static final String CATEGORY_REDIRECTURLS_CACHE_NAME = "CATEGORY_REDIRECTURLS_CACHE_NAME";
	public static final String CLEAR_CATEGORY_REDIRECTURL_CACHE = "CLEAR_CATEGORY_REDIRECTURL_CACHE";
	public static final String CATEGORIES_REDIRECTURLS_MOBILEWEB = "getCategoriesRedirectURLsMobileWeb";
	public static final String FETCH_ARCHIVED_ORDERS = "fetchArchivedOrders";
	public static final String GET_CHECKLIST_FLAG = "getChecklistFlag";
	public static final String LEAF_CATEGORY_URL = "leafCategoryUrl";
	public static final String IS_OVERRIDDEN_URL = "isOverriddenUrl";
	public static final String IS_LOGGEDIN_USER = "loggedin";
	public static final String UPDATE_COHERENCE_CACHE = "Update_Coherence_Cache";
	public static final String RECOG_MOBILE_USER_LOGIN = "recogMobileUserLogin";
	public static final String USER_CHECKING_OUT = "userCheckingOut";
	public static final String CATEGORY_ID_PARAM = "categoryId";
	public static final String PRODUCT_ID_PARAM = "productId";
			public static final String NUM_OF_PROD_RECOMM_TO_SHOW = "numOfProdRecommToShow";
	public static final String CATEGORY_DETAILS_ITEM_DESCRIPTOR = "categoryDetails";
	public static final String CAT_RECOMM_ID = "catRecommId";
	public static final String RECOMM_IMAGE_URL = "recommImageUrl";
	public static final String RECOMM_LINK = "recommLink";
	public static final String RECOMM_TEXT = "recommText";
	public static final String NUM_OF_RECOMM_TO_SHOW = "numOfCatRecommToshow";

	// Porch constants
	public static final String PORCH_CONFIG_KEY = "PorchServiceKeys";
	public static final String PORCH_X_Auth_Token = "X-Auth-Token";
	public static final String PORCH_CONTENT_TYPE = "content-type";
	public static final String PORCH_BOOK_A_JOB_API = "bookAJobAPIURL";
	public static final String PORCH_VALIDATE_ZIPCODE_API = "validateZipCodeAPI";
	public static final String PORCH_BOOK_A_JOB_API_SCHEDULER = "PorchBookAJobScheduler";
	public static final String PORCH_ORDER_EDW_REPORT_SCHEDULER = "PorchOrdersEDWReport";
	public static final String PORCH_ORDER_REPORT_TIBCO_CALL = "sendPorchOrderFeedXMLString";

	public static final String COLLEGENAME_MAXLENGTH = "collegeName_maxLength";

	public static final String AJAXIFY_FILTERS = "ajaxifyFilters";
	public static final String REDIRECTURL = "redirectUrl";
	public static final String NARROW_SEARCH_USED = "narrowSearchUsed";
	public static final String HOME_PAGE_CACHING_KEY="HOME_PAGE_CACHING_ON_OFF";
	public static final String SDD_ATTRIBUTE = "sddAttribute";
	public static final String SDD_ATTRIBUTE_COOKIE_AGE = "sddAttributeCookieAge";
	public static final String ERR_LBL_PRICING_EXCEPTION_LOGIN="err_lbl_pricing_execption_login";

	public static final String BBB_STORE_INVENTORY_CONTAINER = "/com/bbb/commerce/common/BBBStoreInventoryContainer";
	public BBBCoreConstants() {
		super();
	}

	// BBBStore Repository
	private static IRepositoryWrapper bbbCatalog;
	private static IRepositoryWrapper bbbScheduledFeed;

	public static IRepositoryWrapper getBbbCatalog() {
		if (bbbCatalog == null) {
			bbbCatalog = new RepositoryWrapperImpl(
					(Repository) Nucleus.getGlobalNucleus().resolveName("/atg/commerce/catalog/ProductCatalog"));
		}
		return bbbCatalog;
	}

	public static void setBbbCatalog(IRepositoryWrapper bbbCatalog) {
		BBBCoreConstants.bbbCatalog = bbbCatalog;
	}

	public static IRepositoryWrapper getBbbScheduledFeed() {
		if (bbbScheduledFeed == null) {
			bbbScheduledFeed = new RepositoryWrapperImpl((Repository) Nucleus.getGlobalNucleus()
					.resolveName("/com/bbb/feeds/repository/ScheduledFeedRepository"));
		}
		return bbbScheduledFeed;
	}

	public static void setBbbScheduledFeed(IRepositoryWrapper bbbScheduledFeed) {
		BBBCoreConstants.bbbScheduledFeed = bbbScheduledFeed;
	}
}
