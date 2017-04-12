package com.bbb.commerce.catalog;

public class BBBCatalogErrorCodes {
	public BBBCatalogErrorCodes(){
		super();
	}
	public static final String SKU_NOT_AVAILABLE_IN_REPOSITORY="1000" ;
	public static final String CATEGORY_NOT_AVAILABLE_IN_REPOSITORY="1001" ;
	public static final String CATEGORY_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY="1002" ;
	public static final String SKU_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY="1003" ;
	public static final String PRODUCT_NOT_AVAILABLE_IN_REPOSITORY="1004" ;
	public static final String PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY="1005" ;
	public static final String STATE_NOT_AVAILABLE_IN_REPOSITORY="1006" ;
	public static final String SITE_NOT_AVAILABLE_IN_REPOSITORY="1007" ;
	public static final String CREDIT_CARD_NOT_AVAILABLE_IN_REPOSITORY="1008" ;
	public static final String CHILD_PRODUCTS_NOT_PRESENT_FOR_COLLECTION_PRODUCT="1009" ;
	public static final String NO_GIFT_CARD_FOR_SHIPPING_ID_IN_REPOSITORY="2000" ;
	public static final String SHIPPING_METHOD_NOT_CONFIGURED_FOR_SITE_IN_REPOSITORY="2001" ;
	public static final String DEFAULT_SHIPPING_METHOD_NOT_CONFIGURED_FOR_SITE_IN_REPOSITORY="2002" ;
	public static final String UNABLE_TO_CACHE_DATA_EXCEPTION="4004";
	public static final String UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION="2003";
	public static final String UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION="2003";
	
	
	
	public static final String TIMEZONE_NOT_AVAILABLE_IN_REPOSITORY="time_zone_not_available" ;
	public static final String SHIPPING_METHOD_NOT_AVAILABLE_IN_REPOSITORY="2004" ;
	public static final String REGISTRY_NOT_AVAILABLE_IN_REPOSITORY="2005" ;
	public static final String INPUT_PARAMETER_IS_NULL="2006" ;
	public static final String BRAND_ID_NOT_AVAILABLE_IN_REPOSITORY="2007" ;
	public static final String BRAND_NOT_APPLICABLE_FOR_THE_ANY_SITE="2008" ;
	public static final String PROMOTIONID_DOES_NOT_EXIST_FOR_GIVEN_COUPONID="2009" ;
	public static final String INVENTORY_INFO_NOT_AVAILABLE_IN_REPOSITORY_FOR_GIVEN_SKUID="3000" ;
	public static final String SCHOOL_ID_NOT_AVAILABLE_IN_REPOSITORY="3001" ;
	public static final String CATEGORY_IS_NOT_COLLEGE_CATEGORY="3002" ;
	public static final String NO_GIFT_CARD_PRODUCT_AVAILABLE_IN_REPOSITORY="3003" ;
	public static final String NO_SKU_AVAILABLE_IN_REPOSITORY_FOR_GIVEN_COLLEGE_ID="3004" ;
	public static final String NO_PARENT_PRODUCT_FOR_SKU_IN_REPOSITORY="3005" ;
	public static final String NO_BOPUS_ELIGIBLE_STATE_AVAILABLE_IN_REPOSITORY="3006" ;
	public static final String NO_PARENT_CATEGORY_FOR_PRODUCT_IN_REPOSITORY="3007" ;
	public static final String STORE_NOT_AVAILABLE_IN_REPOSITORY="3008" ;
	public static final String COMMERCE_ROOT_NOT_AVAILABLE_IN_REPOSITORY="3009" ;
	public static final String NO_DEFAULT_COLLEGE_ID_FOR_SITE="4000" ;
	public static final String NO_STORES_AVAILABLE_FOR_CANADA_IN_REPOSITORY="4001" ;
	public static final String NO_ECO_FEE_SKU_AVAILABLE_FOR_SKU="6001" ;
	public static final String NO_ECO_FEE_STATE_AVAILABLE_FOR_SKU="6002" ;
	public static final String NO_PRODUCT_FOR_SKU="6003" ;
	public static final String NO_ACTIVE_PRODUCT_FOR_SKU="6004" ;
	public static final String MORE_ECO_FEE_SKUS_AVAILABLE_FOR_SKU="6005" ;
	public static final String CONFIG_KEYS_NOT_AVAILABLE="6006" ;
	public static final String NO_CHILD_SKU_AVAILABLE_FOR_PRODUCT="6007" ;
	public static final String BOPUS_SKU_IS_DISABLED_NO_LONGER_AVAILABLE="6008" ;
	public static final String INVENTORY_ITEMS_UNAVAILABLE="7001" ;
	public static final String UNABLE_TO_MARSHALL="7002" ;
	public static final String UNABLE_TO_UPDATE_REPOSITORY_REPOSITORY_EXCEPTION = "7003";
	public static final String REPOSITORY_NOT_CONFIGURED_REPOSITORY_EXCEPTION = "7004";
	public static final String UNABLE_TO_FETCH_DATA_REPOSITORY_EXCEPTION = "7005";
	public static final String CERTONA_FEED_FILE_NOT_CONFIGURED = "7006";
	public static final String NO_DATA_FOR_CATEGORY_FEED = "7007";
	public static final String NO_DATA_FOR_GUIDE_FEED = "7010";
	public static final String NO_DATA_FOR_PRODUCT_FEED = "7008";
	public static final String INPUT_OUTPUT_EXCEPTION = "7009";
	public static final String JAXB_EXCEPTION = "8000";
	public static final String ECO_FEE_SKU_FOUND = "8001";
	public static final String ROOT_COLLEGE_ID_NOT_FOUND = "8002";
	public static final String NO_APPLICABLE_CATEGORY_NAMES_FOR_GIVEN_REGISTRY = "8003";
	public static final String REGISTRY_TYPE_NOT_AVAILABLE_IN_REGISTRY_CATALOG_MAP_REPOSITORY = "8004";
	public static final String NO_ACTIVE_SKU_FOR_MAX_INVENTORY = "8005";
	public static final String NO_PRODUCT_AVAILABLE_IN_REPOSITORY_FOR_GIVEN_COLLEGE_ID="8006" ;
	public static final String BUSSINESS_EXCEPTION_COLLEGE_DORM_ROOM="8008" ;
	public static final String SCANNED_SKU_NOT_IN_THE_SYSTEM="8009";

	public static final String PRICEING_EXCEPTION_DROPLET_SERVLET_EXCEPTION="8100" ;
	public static final String PRICEING_EXCEPTION_DROPLET_IO_EXCEPTION="8101" ;
	public static final String PRICEING_EXCEPTION_DROPLET_INPUT_ERROR="8102" ;
	public static final String REGISTRY_INFO_EXCEPTION_SERVLET_EXCEPTION="8103" ;
	public static final String REGISTRY_INFO_EXCEPTION_IO_EXCEPTION="8104" ;
	public static final String REGISTRY_DETAIL_EXCEPTION_SERVLET_EXCEPTION="8105" ;
	public static final String REGISTRY_DETAIL_EXCEPTION_IO_EXCEPTION="8106" ;
	public static final String REGISTRY_DETAIL_INPUT_EXCEPTION="8107" ;
	public static final String REGISTRY_DETAIL_EXCEPTION_PRICELIST_EXCEPTION="8108" ;
	public static final String REGISTRY_SEARCH_EXCEPTION_IO_EXCEPTION="8401" ;
	public static final String REGISTRY_SEARCH_EXCEPTION_SERVLET_EXCEPTION="8402" ;
	public static final String REGISTRY_SEARCH_EXCEPTION_SYSTEM_EXCEPTION="8403";
	public static final String REGISTRY_SEARCH_EXCEPTION_BUSSINESS_EXCEPTION="8404";


	public static final String err_fetch_sku_detail="9100";
	public static final String err_fetch_Sku_list_price="9101";
	public static final String err_fetch_Sku_Sale_price="9102";
	public static final String err_fetch_Sku_Inventory="9103";
	public static final String TRANSACTION_DEMARCATION_EXCEPTION="100001";
	public static final String MAX_GIFT_CARD_LIMIT_REACHED="MAX_GIFT_CARD_LIMIT_REACHED";
	public static final String ERR_ORDER_AMOUNT_COVERED_BY_GC="ERR_ORDER_AMOUNT_COVERED_BY_GC";
	public static final String COMMERCE_EXCEPTION="1000012";
	public static final String FEED_PRICE_LIST_EXCEPTION="1000015";
	public static final String ERROR_MSG_NOT_NULL = "error_msg_not_null";
	public static final String GET_BRIDAL_SHOWS_IO_EXCEPTION="1000013" ;
	public static final String GET_BRIDAL_SHOWS_SERVLET_EXCEPTION="1000014" ;
	public static final String GET_HartNHANKS_URL_IO_EXCEPTION="1000017" ;
	public static final String GET_HartNHANKS_URL_SERVLET_EXCEPTION="1000018" ;
	public static final String NO_SKU_PASSED="8007" ;

	public static final String BRIDAL_TOOLKIT_REGISTRY_SYSTEM_EXCEPTION="8501";
	public static final String BRIDAL_TOOLKIT_REGISTRY_IO_EXCEPTION="8502";
	public static final String NO_STORES_AVAILABLE_FOR_USA_IN_REPOSITORY="4002" ;

	public static final String GIFT_WRAP_SKU_SYSTEM_EXCEPTION="8901";
	public static final String GIFT_WRAP_SKU_IO_EXCEPTION="8902";
	public static final String GIFT_WRAP_SKU_BUSSINESS_EXCEPTION="8903";
	public static final String CATEGORY_CHAT_DETAILS_EXCEPTION="8904";
	public static final String BUSSINESS_EXCEPTION_CATEGORY_CHAT="8905" ;
	public static final String BBBSYSTEM_EXCEPTION_CATEGORY_CHAT="8906" ;
	
	public static final String NO_SUCH_PAGE_ORDER_DEFINED ="8960";
	public static final String err_fetch_time_zones="ERR_FETCH_TIME_ZONES";
	public static final String UNABLE_TO_ADD_DATA_IN_REPOSITORY_EXCEPTION="3010";
	public static final String UNABLE_TO_CREATE_RECORD_IN_REPOSITORY_EXCEPTION="3011";
	public static final String OWNER_IS_NOT_ASSOCIATED_WITH_THIS_REGISTRY="3012";
	public static final String COOWNER_IS_NOT_ASSOCIATED_WITH_THIS_REGISTRY="3013";
	public static final String NO_BOPUS_DISABLED_STATE_AVAILABLE_IN_REPOSITORY="3014";

	public static final String NO_SKU_ID_NO_SITE_ID="9001";
	public static final String NO_DATA_FOUND_FOR_DELIVERY_SURCHARGE="9002";
	
	public static final String CLP_NOT_AVAILABLE_IN_REPO="1004" ;
	public static final String INPUT_PARAMETER_INVALID="20001" ;
	public static final String INPUT_PARAMETER_INVALID_ERROR="The input parameter(s) is/are invalid, empty or null";
	public static final String FTP_CONNECTION_ERROR="20010";
	public static final String FILE_NOT_FOUND_EXCEPTION="20011";
	
	public static final String VENDOR_ID_NOT_AVAILABLE_IN_REPOSITORY="30001" ;
	public static final String ERROR_SFL_MAX_REACHED="30002" ;
	public static final String INVALID_INPUT_PROVIDED="5001" ;
	public static final String NOT_ABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY="5005" ;
	public static final String DATA_NOT_FOUND="5003" ;
}
