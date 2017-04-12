package com.bbb.constants;

/*
 * Constant variable specific to International Shipping.
 */

public class BBBInternationalShippingConstants {
	
	public static final String IPLIST_ITEMDESCRIPTOR = "ipList";
	public static final String DEFAULT_COUNTRY = "US";
	public static final String MEXICO_COUNTRY = "MX";
	public static final String COUNTRY_LIST_ITEMDESCRIPTOR = "countryList";
	public static final String DEFAULT_LOCALE = "en-US";
	public static final String INTERNATIONAL_SHIPPING = "International_Shipping";
	public static final String INTL_SINGLE_DATACENTER = "intl_single_datacenter_processing";
	public static final String MERCHANT_ID = "international_merchant_id";
	public static final String CONFIG_KEY_INTERNATIONAL_SHIP_SWITCH="international_shipping_switch";
	public static final String CONFIG_KEY_INTERNATIONAL_SHIP_VDC_RESTRICTED="intl_shipping_vdc_restricted";
	public static final String CONFIG_KEY_INTERNATIONAL_SHIP_SURCHARGE_SWITCH="international_shipping_surcharge_switch";
	public static final String HEADER_PARAMETER_FOR_IP_ADDRESS="TRUE_IP_HEADER";
	public static final String CONFIG_TYPE_FOR_IP_ADDRESS="CartAndCheckoutKeys";
	public static final String CONFIG_KEY_INTERNATIONAL_SHIP_COOKIE_MAX_AGE="international_ship_cookie_max_age";
	public static final String CONFIG_KEY_INTERNATIONAL_SHIP_GIFT_MESSAGE="intl_shipping_gift_message";
	public static final String CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID="intl_shipping_tax_exempt_id";
	public static final String JVM_PROPERTY="weblogic.Name";
	
	public static final String KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT="defaultUserCountryCode";

	public static final String KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CURRENCY = "defaultUserCurrencyCode";

	public static final String KEY_FOR_MERCHANT_ID="DF_merchandId";
	public static final String EXCHANGE_RATE_ITEMDESCRIPTOR="exchangeRates";
	public static final String IMAGE_SIZE = "?$75$";
	public static final String NO_IMG_PATH = "/_assets/global/images/no_image_available.jpg";
	public static final String SKU_ID_IN_URL = "?skuId=";
	public static final String SHIPPING_METHOD_ID = "STANDARD_DDP";
	public static final String INTERNATIONAL_CHECKOUT_END_POINT = "international_checkout_endPoint";
	public static final String INTERNATIONAL_CHECKOUT_USER_ID = "international_checkout_userId";
	public static final String INTERNATIONAL_CHECKOUT_PWD = "international_checkout_pwd";
	public static final String PAYPAL_HEADER_LOGO_KEY = "Paypal_Header_Logo";
	public static final String HOST_PATH = "hostPath";
	public static final String SERVER_NAME = "serverName";
	public static final String CONTEXT_PATH = "contextPath";
	public static final String CONFIG_KEY_SHIPPING_WITHOUT_PROMOTION = "Shipping_Charge_Without_Promotion";
	public static final String CONFIG_KEY_SHIPPING_ZERO = "Shipping_Charge_Zero";
	public static final String CONFIG_KEY_SHIPPING_WITH_PROMOTION = "Shipping_Charge_With_Promotion";
	public static final String CONFIG_KEY_INTERNATIONAL_CHECKOUT_END_POINT = "international_checkout_endPoint";
	public static final String CONFIG_KEY_INTERNATIONAL_CHECKOUT_USER_ID = "international_checkout_userId";
	public static final String CONFIG_KEY_INTERNATIONAL_CHECKOUT_PWD = "international_checkout_pwd";
	public static final String LOCALE = "locale";
	public static final String PRICE_RANGE = "PRICE RANGE";
	public static final String PRICE_RANGE_ATTR = "Price_Range";
	public static final String PRICE_RANGE_MEXICO = "Price_Range_MX";
	//International Order Constants
	public static final String INTERNATIONAL_ORDER="internationalOrder";
	public static final String INTL_EXCHANGE_OORDER_ID="e4xOrderId";
	public static final String INTL_ORDER_XML="orderXml";
	public static final String INTERNATIONAL_ORDER_REPOSITORY_NULL="International order repository is null";
	public static final String PENDING_FRAUD_STATE="pending";
	public static final String FAILED_FRAUD_STATE="failure";
	public static final String SUCCESS_FRAUD_STATE="success";
	public static final String PROFILE_ID="9000";
	public static final String INTL_INCOMPLETE="INTL_INCOMPLETE";
	public static final String INTL_HOLD="INTL_HOLD";
	public static final String INTL_SUBMITTED="INTL_SUBMITTED";
	//International Order constant for Order Repository
	
	public static final String BBB_ORDER="order";
	public static final String INTERNATIONAL_ORDER_ID="internationalOrderId";
	public static final String INTERNATIONAL_STATE="internationalState";
	public static final String COUNTRY_CODE="countryCode";
	public static final String CURRENCY_CODE="currencyCode";
	public static final String CURRENCY_CODE_CA="CA";
	public static final String CURRENCY_CODE_CAD="CAD";
	
	public static final String INTL_SUBMIT_DATE="internationalSubmitteddate";
	public static final String INTL_CANCELLED = "INTL_CANCELLED";
	public static final String USER= "user";
	public static final String LPTOKEN= "$%L";
	public static final String HPTOKEN= "$%H";
	
	
	
	

	public static final String ORDER_DS_SEED_NAME = "OrderDS";
	public static final String ORDER_ID = "orderId";
	public static final String SITE_ID = "siteId";
	public static final String ITEM_LIST = "itemList";

	public static final String ENVOY_URL ="envoyURL";
	public static final String ENVOY_ERROR ="envoyError";
	public static final String CYBERSOURCE_VERIFIED ="cybersourceVerified";
	public static final String RESTRICTED_SKU_LIST_IN_SESSION ="internationalShipRestrictedSku";
	public static final String BOPUS_SKU_LIST_IN_SESSION ="bopusSkuNoIntShip";
	public static final String ONLINE_ORDER_NUMBER = "onlineOrderNumber";
	
	public static final String CHANNEL_DESKTOP_BFREE = "D_BFREE";
	public static final String CHANNEL_MOBILE_APP_BFREE = "A_BFREE";
	public static final String CHANNEL_MOBILE_BFREE = "M_BFREE";
	public static final String CHANNEL_CUSTOM_DATA = "Channel:";
	public static final String TAX_EXEMPTID_CUSTOM_DATA = "TaxExemptID:";
	public static final String DATACENTER="DataCenter";
	
	public static final String CURRENCY_USD = "USD";
	public static final String CURRENCY_MEXICO = "MXN";
	public static final String COUNTRY_MEXICO = "MX";
	public static final String PROPERTY_PRICELIST = "priceList";
	public static final String PROFILE_SALE_PRICELIST = "salePriceList";
	public static final String PROPERTY_LIST_PRICELIST = "ListPriceList";
	public static final String PROPERTY_SALE_PRICELIST = "SalePriceList";
	public static final String PROPERTY_CATALOG = "catalog";
	public static final String PROPERTY_DEFAULT_CATALOG = "defaultCatalog";
	
	//exceptions
	public static final String PGP_EXCEPTION_ERROR_CODE="INTL_1000";
	public static final String PGP_EXCEPTION_MESSAGE="exception occured while decrypting PO file:PGPException";
	
	public static final String DECRYPT_IO_ERROR_CODE="INTL_1001";
	public static final String DECRYPT_IO_MESSAGE="exception occured while decrypting PO file:IOException";
	
	public static final String NO_SUCH_PROVIDER_ERROR_CODE="INTL_1002";
	public static final String NO_SUCH_PROVIDER_MESSAGE="exception occured while decrypting PO file:NoSuchProviderException";
	
	public static final String JAXB_EXCEPTION_ERROR_CODE="INTL_1003";
	public static final String JAXB_EXCEPTION_MESSAGE="Error while unmarshalling file :JAXBEXCEPTION";
	
	
	public static final String INTL_MERCHANT_ID_NULL_ERROR_CODE = "INTL_1004";
	public static final String INTL_MERCHANT_ID_NULL_MESSAGE = "Merchant Id is not available in the PO file";
	
	public static final String ATG_ORDER_ID_NOT_IN_REPOSITORY_ERROR_CODE = "INTL_1005";
	public static final String ATG_ORDER_ID_NOT_IN_REPOSITORY_MESSAGE = "No ATG order ID found in order Repository for the merchant order ID";
	
	public static final String REPOSITORY_EXCEPTION_ERROR_CODE = "INTL_1006";
	public static final String REPOSITORY_EXCEPTION_MESSAGE = "Error querying repository:RepositoryException";
	
	public static final String UPDATE_ORDER_REPOSITORY_EXCEPTION_ERROR_CODE = "INTL_1007";
	public static final String UPDATE_ORDER_REPOSITORY_EXCEPTION_MESSAGE = "Error Updating order in repository:RepositoryException";
	
	
	public static final String UPDATE_ORDER_TRANSACTION_DEMARCATION_EXCEPTION_ERROR_CODE = "INTL_1008";
	public static final String UPDATE_ORDER_TRANSACTION_DEMARCATION_EXCEPTION_MESSAGE = "Error Updating order in repository:TransactionDemarcationException";
	
	
	public static final String UPDATE_ORDER_COMMERCE_EXCEPTION_ERROR_CODE = "INTL_1009";
	public static final String UPDATE_ORDER_COMMERCE_EXCEPTION_MESSAGE = "Error Updating order in repository:CommerceException";
	
	public static final String ORDER_CONFIRMATION_WS_EXCEPTION_ERROR_CODE="INTL_2000";
	public static final String ORDER_CONFIRMATION_WS_EXCEPTION_MESSAGE="Exception while calling Order Confirmationa API";
	
	public static final String UPDATE_ORDER_PROCESS_ORDER_ERROR_CODE = "INTL_1010";
	public static final String UPDATE_ORDER_PROCESS_ORDER_MESSAGE = "Error while processing or authorizing the order";
	
	//constants for 
	
	public static final String RED_PO_FILE="RED";
	public static final String GREEN_PO_FILE="GREEN";
	public static final String CANCELLED_PO_FILE="CANCELLED";
	public static final String REMOVED = "REMOVED";
    public static final int CANCELLED = 5021;
    public static final int INTL_INCOMPLETE_STATE =7001;
	public static final Object KEY_FOR_SHIPPING_ENABLED = "shippingEnabled";
	public static final String USD = "USD";
	public static final String US_Dollar = "US Dollar";
	

	public static final String RETRY_ORDER_CONFIRMATION_ITEM_DESCRIPTOR = "retryOrderConfirmation";
	public static final String RETRY_COUNT = "retryCount";
	public static final String MERCHANT_ORDER_ID = "merchantOrderId";
	public static final String LOG_TRACE = "logTrace";
	public static final String RETRY_SUCCESSFUL = "retrySuccessful";
	
	//config key value for decryption
	public static final String PGP_FILE_PATH="po_decrypt_pgp_file_path";
	public static final String PGP_KEY_PATH="po_decrypt_pgp_key_path";
	public static final String PO_ENCRYPT_PWD_FILE_PATH="po_decrypt_encryption_pwd_file_path";
	public static final String PO_PWD="popwd";
	public static final String PGP_PASS_PHRASE="po_decrypt_pass_phrase";
	
	public static final String CONFIG_KEY_ORDER_CONF_RETRY_COUNT = "order_conf_retry_count";
	
	public static final String RETRY_FILE_PROCESS_ITEM_DESCRIPTOR = "retryFileProcess";
	public static final String TRADEMARK = "&#153;";
	public static final String TRADEMARKCODE = "&trade;";
	public static final String PO_DECRYPT_KEY = "intl_key";
	public static final String INTERNATIONAL_CONTEXT = "internationalShippingContext";
	public static final String ROUND="round";
	public static final String ROUNDMETHOD="roundMethod";
	public static final String VALUE="VALUE";
	public static final String SHOPPERCURRENCY="shopperCurrency";
	public static final String FRONTLOADCOEFFICINET="frontLoadCoefficient";
	public static final String DOWN="down";
	public static final String CONFIG_KEY_MEXICOORDER_TAXANDDUTIES = "MexicoOrder_TaxAndDuties_switch";
	public static final String DUTIES = "DUTIES";
	public static final String TAXES="TAXES";
	public static final String DUTIES_AND_TAXES="DUTIES_AND_TAXES";
	public static final String ORDER_SUBTOTAL = "Order Subtotal";
	public static final String LIST_RPICE = "List price";
	public static final String SALE_PRICE = "Sale price";
	public static final String BORDER_FNAME="Border_FirstName";
	public static final String BORDER_LNAME="Border_LastName";
	public static final String INTERNATIONAL_RESTRICTED_SKU = "internationalShipRestrictedSku";
	public static final String BOPUSSKU_NOT_INTSHIP = "bopusSkuNoIntShip";
	public static final String PROFILE = "profile";
	public static final String Bed_Bath___Beyond_Type_Ahead = "Bed_Bath___Beyond_Type-Ahead";
	public static final String SESSIONBEAN = "sessionBean";
	public static final String DISPLAY_DESCRIPTION = "displayDescrip";
	public static final String INTL_FLAG = "intlFlag";
	public static final Object ATTRIBUTE_MAP = "attributeMap";
	public static final String MCID = "mcid";
	public static final String GOOGLE = "PS_google";
	public static final String MCID_GOOGLE = "mcid=PS_google";
	public static final String INTERNATIONALSHIPFORMHANDLER = "com/bbb/internationalshipping/formhandler/InternationalShipFormHandler";
	public static final String GOOGLE_FLOW = "googleFlow";
	public static final String RESET_GOOGLE_ADDFLOW = "resetGoogleAddFlow";

}
