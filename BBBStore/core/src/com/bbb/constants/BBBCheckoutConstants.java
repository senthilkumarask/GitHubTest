package com.bbb.constants;

import com.bbb.commerce.catalog.BBBCatalogConstants;

public class BBBCheckoutConstants {
	
	public static final String STRING_DELIMITER=":";
	public static final String BILLING_ADDRESS="billingAddress";
	public static final String SECURITY_STATUS="securityStatus";
	
	public static final String SITEID = "siteId";
	public static final String GIFT_WRAP_MESSAGES = "giftWrapMessages";
	public static final String COMMERCE_ITEM = "commerceItem";
	public static final String SKU_ID = "skuId";
	
	// Multi Shipping - Gift Wrap options
	public static final String GIFT_WRAP_OPTION = "giftWrapOption";
	public static final String MULTI_GIFT_OPTION = "multi";
	public static final String MULTI_GIFT_ADDR = "shipAddress";
	public static final String MULTI_GIFT_METHOD = "shippingMethod";
	public static final String MULTI_GIFT_OPT_MAP = "giftWrapMap";
	public static final String MULTI_GIFT_ITEM_LIST = "commItemList";
	public static final String MULTI_GIFT_SHIP_ID = "shipGroupId";
	public static final String MULTI_GIFT_SHIP_MSG = "shipGroupGiftMessage";
	public static final String MULTI_GIFT_SHIP_IND = "shipGroupGiftInd";
	public static final String MULTI_GIFT_SHIP_PARAM = "shipGroupParam";
	public static final String MULTI_NON_WRAP_SKUS = "nonGiftWrapSkus";
		
	public static final String ORDER = "order";
	public static final String SHIPPING_GROUPS = "shippingGroups";
	public static final String SHIPPING_GROUP = "shippingGroup";
	public static final String GIFT_WRAP_FLAG = "giftWrapFlag";
	public static final String FALSE_STRING = "false";
	public static final String TRUE_STRING = "true";
	public static final String GIFT_WRAP_PRICE = "giftWrapPrice";
	public static final String SINGLE_NON_WRAP_SKUS="nonGiftWrapSkus";
	
	public static final String OUTPUT = "output";
	public static final String OUTPUT_START = "outputStart";
	public static final String OUTPUT_END = "outputEnd";
	public static final String EMPTY = "empty";
	public static final String NEW = "NEW";
	public static final String EDIT = "EDIT";
	public static final String BILL = "BILL";
	
	// validation constants
	public static final String INVALID_FIRSTNAME = "Invalid firstname";
	public static final String INVALID_EMAIL = "Invalid email";
	public static final String INVALID_LASTNAME = "Invalid lastname";
	public static final String INVALID_ADDRESS1 = "Invalid address1";
	public static final String INVALID_ADDRESS2 = "Invalid address2";
	public static final String INVALID_CITY = "Invalid city";
	public static final String INVALID_ZIPCODE = "Invalid zipcode";
	public static final String INVALID_STATE = "Invalid State";
	public static final String NULL_BILL_ADDR = "ERR_CHECKOUT_NULL_BILLING_ADDRESS";
	
	// constants used in DisplayBillingDroplet
	public static final String DISPLAY_CART = "displayCart";
	public static final String DISPLAY_BILLING = "displayBilling";
	public static final String BBB_JMS_CONFIG = "BBBJMSConnectionFactory";
	/**
	 * ValueLink constants
	 */
	public static final String VAL_LNK_REQ_HDR = "valueLinkRequestHeader";
	public static final String VAL_LNK_REQ_BODY = "valueLinkRequestBody";
	public static final String VAL_LNK_RES_HDR = "valueLinkResponseHeader";
	public static final String VAL_LNK_RES_BODY = "valueLinkResponseBody";
	//public static final String FIELD_SEPARATER_VAL = "fieldSeparaterVal";
	public static final String RESPONSE_CODE = "resCode";
	public static final String RESP_SUCC_CODE = "00";
	public static final String GIFT_CARD_IDN = "giftCardNumberIDN";
	public static final String NEW_BALANCE_IDN = "newBalanceIDN";
	public static final String PREV_BAL_IDN = "previousBalanceIDN";
	public static final String REQUEST_PAYLOAD = "reqPayload";
	public static final String VALUE_LINK_KEYS = "valueLinkKeys";
	public static final String UNDERSCORE = "_";
	public static final String UNIQUE_TRANS_ID = "uniqueTransID";
	public static final String MID = "mid";
	public static final String VXN_MID = "vxnMID";
	public static final String VXN_TID = "vxnTID";
	public static final String VXN_DID = "vxnDID";
	public static final String MID_TID_IDN = "midTidIDN";
	public static final String LOCAL_CURRENCY = "localCurrency";
	public static final String MESSAGE_IDN = "msgIDN";
	public static final String MESSAGE_VAL = "msgVal";
	public static final String TERMINAL_ID = "terminalID";
	
	
	public static final String GET_BALANCE = "getBalance";
	public static final String REDEEM = "redeem";
	public static final String REDEEM_ROLLBACK = "redeemRollback";
	public static final String BLANK = "";
	//public static final String FIELD_SEPARATOR = "fieldSeparator";
	public static final String USER1_IDN = "user1IDN";
	public static final String USER2_IDN = "user2IDN";
	public static final String USER1_VAL = "user1VAL";
	public static final String USER2_VAL = "user2VAL";
	
	public static final String SEC_CARD_VAL_IDN = "securityCardValueIDN";
	public static final String SECURITY_CARD_VALUE = "securityCardValueVAL";
	public static final String EXT_ACC_IDN = "extAccountNumberIDN";
	public static final String ALT_MERCHANT_NUMBER_IDN = "altMerchantNumberIDN";
	public static final String ALT_MERCHANT_VAL = "altMerchantNumberVAL";
	public static final String CLERKID_IDN = "clerkIdIDN";
	public static final String CLERKID_VAL = "clerkIdVAL";
	public static final String ACCOUNT_ORIGIN_IDN = "accountOriginIDN";
	public static final String ACC_ORIGIN_VAL = "accountOriginVAL";
	public static final String FOREIGN_CODE_IDN = "foreignAccessCodeIDN";
	public static final String FOREIGN_CODE_VAL = "foreignAccessCodeVAL";
	public static final String LOCAL_CURR_IDN = "localCurrencyIDN";
	public static final String LOCAL_CURR_VAL = "localCurrencyVAL";
	public static final String MER_KEY_IDN = "merchantKeyIdIDN";
	public static final String MER_KEY_VAL = "merchantKeyIdVAL";
	public static final String ECHO_BACK_IDN = "echoBackIDN";
	public static final String ECHO_BACK_VAL = "echoBackVAL";
	public static final String SIC_CODE_IDN = "sicCodeIDN";
	public static final String SIC_CODE_VAL = "sicCodeVAL";
	public static final String TERM_TRANS_IDN = "terminalTransNumberIDN";
	public static final String TERM_TRANS_VAL = "terminalTransNumberVAL";
	public static final String POST_DATE_IDN = "postDateIDN";
	public static final String POST_DATE_VAL = "postDateVAL";
	public static final String LOC_TRANS_TM_IDN = "localTransTimeIDN";
	public static final String LOC_TRANS_DT_IDN = "localTransDateIDN";
	public static final String SOURCE_CODE_IDN = "sourceCodeIDN";
	public static final String SOURCE_CODE_VAL = "sourceCodeVAL";
	public static final String EMB_CARD_NUM_IDN = "embossedCardNumberIDN";
	public static final String TRAN_AMOUNT_IDN = "transactionAmountIDN";
	
	public static final String AUTH_CODE_IDN = "authCodeIDN";
	public static final String SYS_TRACE_NUM_IDN = "systTraceNumberIDN";
	public static final String CARD_CLASS_IDN = "cardClassIDN";
	
	public static final String VER_NUMBER_VAL = "versionNumberVal";
	public static final String FORMAT_NUMBER_VAL = "formatNumberVal";
	public static final String CALL_ON_ERROR = "callOnError";
	public static final String REQ_WRKING_KEY = "reqestWorkingKey";
	public static final String TRANS_REQ_CD_BAL = "transReqCodeVal_balance";
	public static final String TRANS_REQ_CD_RED = "transReqCodeVal_redeem";
	public static final String TRANS_REQ_CD_UNRED = "transReqCodeVal_unredeem";
	public static final String TRANS_REQ_CD_VAL_ER = "transReqCodeVal_error";
	public static final String ORIG_TRANS_CD_SEP = "originalTransCodeSeparator";
	public static final String CLIENT_REF_VERSION = "clientRefVersion";
	public static final String TRANS_REQ_CD_WORK_KEY = "transReqCodeVal_reqWorkingKey";
	public static final String SVCID = "svcID";
	public static final String APPID = "appID";
	public static final String PRIMARY_URL = "primaryURL";
	public static final String SECONDARY_URL = "secondaryURL";
	public static final String CALL_TYPE = "callType";
	
	public static final String GIFTCARD_NO = "giftCardNo";
	public static final String PIN_NO = "pinNo";	

	//BBBPaymentGroupDroplet constants
	public static final String GIFTCARD = "giftCard";
	public static final String GIFTCARDS = "giftcards";
	public static final String CREDITCARD = "creditCard";
	public static final String COVEREDBYGC = "coveredByGC";
	public static final String GIFT_DET_SERVICE = "GiftCardDetailService";
	public static final String GET_PG_ST_ON_LOAD = "GetPaymentGroupStatusOnLoad";
	public static final String SERVICE_TYPE = "serviceType";
	public static final String IS_GIFTCARDS = "isGiftcards";
	public static final String IS_OD_AMT_COV = "isOrderAmtCovered";
	public static final String IS_MAX_GC_AD = "isMaxGiftcardAdded";
	
	public static final String MMDDYYYY = "MMddyyyy";
	public static final String HHMMSS = "HHmmss";
	public static final String HYBRID = "HYBRID";
	public static final String ONLINE_ONLY = "ONLINE_ONLY";
	public static final String BOPUS_ONLY = "BOPUS_ONLY";
	public static final String DOL_CENT_FMT = "#0.00";
	public static final String DOT = ".";
	public static final String GIFTCARDERROR = "giftcarderror";
	public static final String GIFTCARD_ERROR_SPC_MOB = "giftcard_error_spc_mob";
		
	//Error constants
	public static final String GENERIC_ERROR_TRY_LATER = "ERR_CART_GENERIC_ERROR_TRY_LATER";
	public static final String INVALID_GC_PIN = "err_ivalid_giftcard_pin";
	public static final String GC_NOT_SUFF_BAL = "err_giftcard_not_sufficient_balance";
	public static final String GC_MAX_INV_ATTEMPT = "err_giftcard_max_invalidattempt";
	public static final String GC_BAL_SERV_ERR = "err_giftcard_check_balance_service";
	public static final String GC_ALRDY_EXIST = "err_giftcard_already_exist";
	public static final String ERR_CHECKOUT_CREDITCARD_INVALID_ATTEMPT = "err_checkout_creditcard_invalidattempt";
	public static final String ERR_CHECKOUT_INSUFFICIENT_INVENTORY = "err_checkout_insufficient_inventory";
	public static final String ERR_CHECKOUT_CREDITCARD_ERROR = "err_checkout_creditcard_error";
	public static final String ERR_CHECKOUT_CYBERSOURCE_ERROR = "err_checkout_cybersource_error";
	public static final String ERR_CHECKOUT_PIPELINE_ERROR = "err_checkout_pipeline_error";
	public static final String ERR_CHECKOUT_PAYGRP_ERROR = "err_checkout_payment_grp_error";
	public static final String ERR_CHECKOUT_PAYPAL_ERROR_ONE = "err_checkout_paypal_error_one";
	public static final String ERR_CHECKOUT_PAYPAL_ERROR_TWO = "err_checkout_paypal_error_two";
	
	//R2.2 PAYPAL PARAMETERS
	public static final String PAYPAL_ERROR_ONE =  "?paypalFailOne=true";
	public static final String PAYPAL_ERROR_TWO = "?paypalFailTwo=true";
	
	
	
	public static final String US_LOC = "en_us";
	public static final String ERR_RED_CALL = "err_redeem_call";
	public static final String ERR_GET_BAL = "err_getBalance_call";
	public static final String ERR_TIMEOUT_REVERSAL_CALL = "err_timeout_reversal_call";
	public static final String TIMEOUT_REVERSAL = "timeoutReversal";
	public static final String BOPUSCONFIG="BOPUSCONFIG";
	public static final Object STRING_ZERO = "0";
	
	//private static final String GetBalanceCallERROR = "ValueLink response for GetBalance call returning ERROR";
	//private static final String redeemCallERROR = "ValueLink response for REDEEM call returning ERROR";
	
	
	
	public static final String THRESHOLD_AMOUNT = "thresholdAmount";
	public static final String EXCLUDED_PROMO_ITEMS = "excludedPromoItems";
	public static final String PACKANDHOLDDATE = "packAndHoldDate";
	public static final String ISPACKHOLD = "isPackHold";
	public static final String DATE_SEPARATOR = "/";
	public static final String GET_PAY_GRP_STATUS_LOAD = "getPaymentGroupStatusOnLoad";
	public static final String GET_GIFTCARD_DETAILSERVICE = "getGiftCardDetailService";
	public static final String ORDER_TOTAL = "orderTotal";
	
		
	public static final String SITE_CONFIGURATION_ITEM = BBBCatalogConstants.SITE_ITEM_DESCRIPTOR;
	public static final String SITE_CURRENCY = "currency";
	public static final String SITE_CURRENCY_USD = "USD";
	public static final String PROPERTY_SKU = "sku";
	public static final String PROPERTY_PARENT_PRODUCT = "parentProducts";
	
	public static final String BBB_COUPONS = "bbbCoupons";
	public static final String TYPE_DETAILS = "typeDetail";
	public static final String DISPLAY_NAME = "displayName";
			
	public static final String SELLER_REGISTRATION_NUMBER = "SellerRegistrationNumber";
	public static final String SELLER_REGISTRATION_CA_ONTARIO = "seller_registration_ca_on";
	public static final String SELLER_REGISTRATION_CA_BRITISH_COLUMBIA = "seller_registration_ca_bc";
	public static final String CA_STATE_ONTARIO = "ON";
	public static final String CA_STATE_BRITISH_COLUMBIA = "BC";
	public static final String SCHOOLPROMO = "schoolPromo";
	public static final String SCHOOLIDS = "schoolIds";
	public static final String RESP_VL_TIME_REVERSAL_NO_PREVIOUS_TRANS = "33";
	public static final String RESP_VL_TIME_REVERSAL_ALREADY_DONE = "34";
	
	public static final String ITEM_PROMOTIONS = "itemPromotions";
	public static final String SHIPPING_PROMOTIONS = "shippingPromotions";
	public static final String ORDER_PROMOTIONS = "orderPromotions";
	
	public static final String WEBSERVICE_FLOW = "webserviceFlow";
	public static final String WEBSERVICE_ITEM_INFO_MAP= "itemInfoMap";
	public static final String PARAM_SGCI_RELATION_TO_ITEM = "SGCI_RELATION_TO_ITEM";
	public static final String PARAM_ITEM_LINE_NUMBER = "ITEM_LINE_NUMBER";
	public static final String PARAM_SHIPPING_GROUP_ID_RELATION = "SHIPPING_GROUP_ID_RELATION";
	public static final String IS_PARTIALLY_SHIPPED = "IS_PARTIALLY_SHIPPED";
	public static final String PARAM_SGCI_PRICE_INFO = "PARAM_SGCI_PRICE_INFO" ;
	
	public static final String REGISTRY_SOURCE = "registry";
	
	public static final String PROMO_EXCLUSION_MAP = "promoExclusionMap";
	public static final String SCHOOL_PROMOTIONS = "schoolPromotions";
	
	public static final String CLOSENESS_QUALIFIER = "closenessQualifier";
	public static final String BBBCOUPONS = "bbbCoupons";
	
	public static final String CYBERSOURCE_TAX_FAILURE = "CYBERSOURCE_TAX_FAILURE";
	public static final String CYBERSOURCE_AUTH_FAILURE = "CYBERSOURCE_AUTH_FAILURE";
    public static final String ERROR_POBOX_ADDRESS = "err_am_cc_po_box";
    
    public static final String CART_CONTINUE_SHOPPING_URL = "continue_shopping_url";
	public static final String GIFT_ITEM_IND = "giftItemIndicator";
	
	  /** Gift message key name. */
	public static final String GIFT_MESSAGE_KEY = "giftMessage";
	public static final String CONFIRMATION_EMAIL_ID = "CONFIRMATION_EMAIL_ID";
	public static final String ERROR_SESSION_EXPIRED="err_generic_error";

	public static final String ERROR_MAPQUEST_FAILED_DESCRIPTION="Error in getting details from MapQuest";
	public static final String NOT_APPLICABLE="NA";
	public static final String PAYPAL="PAYPAL";
	public static final String LOCALE = "locale";
	public static final String PRICING_WEBSERVICE_ORDER = "pricingWebserviceOrder";
	public static final String TBS_PRICING_WEBSERVICE_ORDER = "TBS_pricingWebserviceOrder";
	public static final String REPRICE_SHOPPING_CART_ORDER = "repriceShoppingCartOrder";
	public static final String TAX_CALCUATED_BY_BCC = "TAX_CALCUATED_BY_BCC";
	public static final String TAX_CALCULATED_BY_USR_ADDR = "TAX_CALCULATED_BY_USR_ADDR";
	public static final String TAX_CALCULATED_BY_BBB_ADDR = "TAX_CALCULATED_BY_BBB_ADDR";
	
	
	public static final String IN_STOCK = "in stock";
	public static final String OUT_OF_STOCK = "out of stock";
		
	public static final String CREDIT_CARD_AMOUNT = "creditCardAmount";
	public static final String CREDIT_CARD_INFO = "creditCardInfo";
	public static final String SELECTED_ID = "selectedId";
	public static final String SFL_MAX_LIMIT = "SFL_MAX_LIMIT";
	public static final String WSDL_GIFT_REGISRTY = "WSDL_GiftRegisrty";
	public static final String CART_MAX_LIMIT = "CART_MAX_LIMIT";
	public static final String ERR_CARD_SECOND_LAST_ATTEMPT = "err_card_second_last_attempt";
	
	public static final String ONLY_GIFT_WRAP_PRODUCT_DETAILS="onlyGiftWrapProductDetails";
}
