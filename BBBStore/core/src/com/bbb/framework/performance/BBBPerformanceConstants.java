/*
 *
 * File  : BBBPerformanceConstants.java
 * Project:     BBB
 * 
 */
package com.bbb.framework.performance;


/**
 * BBBPerformanceConstants : This is a declaration class for
 *  ATG performance monitor constants.
 *
 * 
 * @version 0.1
 */
public class BBBPerformanceConstants {
	public BBBPerformanceConstants (){
		super();
	}
    //~ Static variables/initializers ------------------------------------------

    /** The Constant WEB_SERVICE_CALL. */
    public static final String WEB_SERVICE_CALL = "Web_Service_Call";
    
    /** constant MESSAGING_CALL */
    public static final String MESSAGING_CALL = "messaging_call";

    /** TODO DOCUMENT ME! *//*
    public static final String SCUF_OVERALL = "SCUF_Overall";*/

    /** TODO DOCUMENT ME! */
    public static final String SIGNIN_OVERALL = "SignIn_Overall";

    /** TODO DOCUMENT ME! */
    public static final String SIGNIN_FRAUD_CHECK = "SignIn_Fraud_Check";

    /** TODO DOCUMENT ME! */
    public static final String SIGNIN_VALIDATE_USER =
        "SignIn_Validate_User_Credentials";

    /** TODO DOCUMENT ME! */
    public static final String TARGETER_CALL = "Targeter_Execution";

    /** TODO DOCUMENT ME! */
    public static final String CATALOG_API_CALL = "Catalog_Api_Call";
    
    /** Performance Monitor constant for Facebook integration module*/
    public static final String FACEBOOK_INTEGRATION = "Facebook_Integration";
    
    /** Performance Monitor constant for Endeca integration module*/
    public static final String SEARCH_INTEGRATION = "Search_Integration";
    
    /** Performance Monitor constant for Certona integration module*/
    public static final String CERTONA_REC_CALL = "Certona_Recomm_Call";

    /** Performance Monitor constant for Caching integration module*/
    public static final String CACHE_INTEGRATION = "Cache";
        
    /** Performance Monitor constant for Caching integration module*/
    public static final String DROPLET_CACHE_INTEGRATION = "DropletCache";
    
    /** Performance Monitor constant for Certona integration module*/
    public static final String GIFT_REG_FLYOUT_WS_CALL = "Gift_Registry_Flyout";    
    
    /** constant SIGNIN_PRE_LOGIN */
    public static final String SIGNIN_PRE_LOGIN = "SignIn_Pre_Login";
    
    /** constant SIGNIN_POST_LOGIN */
    public static final String SIGNIN_POST_LOGIN = "SignIn_Post_Login";
    
    /** constant ACCESS_CONTROLLER */
    public static final String ACCESS_CONTROLLER = "Access_Controller";
    
    /** constant SIGNIN_CHECK_SITE_GROUP */
    public static final String SIGNIN_CHECK_SITE_GROUP = "SignIn_Check_Site_Group";
    
    /** constant SIGNIN_ADD_SITE */
    public static final String SIGNIN_ADD_SITE = "SignIn_Add_Site";
    
    /** constant FORGOT_PASSWORD */
    public static final String FORGOT_PASSWORD = "Forgot_Password";
    
    /** constant CHANGE_STORE_PICKUP  */
    public static final String SHIPPING_STORE_PICKUP = "Shipping_Change_To_Store";
    
    /** constant CHANGE_ONLINE_SHIP  */
    public static final String SHIPPING_ONLINE_SHIP = "Shipping_Change_To_Online";
    
     /** constant CREATE_CREDIT_CARD */
    public static final String CREATE_CREDIT_CARD = "Create_Credit_Card";
    
    public static final String POPULATE_BILLING_ADDRESS = "populateBillingAddress";
    
    public static final String SAVE_BILLING_ADDRESS = "SaveBillingAddress";
    
    public static final String SET_EMAIL_SIGNUP_IN_ORDER = "SetEmailSignUpInOrder";

	public static final String EDIT_BILLING_ADDRESS = "editBillingAddress";
	
	public static final String EXPRESS_CHECKOUT = "expressCheckout";
	
	public static final String ADD_SINGLE_SHIPPING = "addSingleShippingAddress";
	
	public static final String ADD_MULTIPLE_SHIPPING = "addMultipleShippingAddress";
	
	/** constant  Multi Gift option */
	public static final String ADD_MULTIPLE_GIFT_OPTION = "addMultipleGiftOptions";
	
	/** Performance Monitor constant for Order Confirmation module*/
    public static final String ORDER_CONFIRMATION = "Order_Confirmation";
    
    /** constant COLLEGE_WEBLINK */
    public static final String COLLEGE_WEBLINK = "College_Weblink";
    
    /** constant COLLEGE_WEBLINK */
    public static final String REFERRAL_WEBLINK = "Referral_Weblink";
    
    /** constant Home_Page_Data */
    public static final String HOME_PAGE_DATA = "Home_Page_Data";
    
    /** constant Recommendation_Page_Data */
    public static final String RECOMMENDATION_PAGE_DATA = "Recommendation_Page_Data";
    
    /** constant Recommender_Page_Data */
    public static final String RECOMMENDER_PAGE_DATA = "Recommender_Page_Data";
    
    /** constant Home_Page_Data */
    public static final String SEARCH_DROPLET = "Home_Page_Data";
    public static final String SPC_PAYMENT = "SpPayment";
    
    /** Performance Monitor constant for CMS Nav Flyout */
    public static final String CMS_Nav_Flyout = "cms_nav_flyout";
    
    public static final String AUTO_LOGIN_USER = "autoLoginUser";
    
    public static final String AUTO_SIGN_IN = "autoSignIn";
    
    public static final String BBB_PAYMENTGROUP_DROPLET = "BBBPaymentGroupDroplet";
    
    public static final String VALUE_LINK_PROCESSOR = "ValueLinkGiftCardProcessorImpl";
    
    public static final String PRE_COMMIT_ORDER_PROCESS = "PreCommitOrderProcess";
    
    public static final String PRE_CART_CHECKOUT_PROCESS = "PreCartCheckoutProcess";

    public static final String PRE_COMMIT_INVENTORY_CHECK = "PreCommitInventoryCheck";
    
    public static final String PRE_COMMIT_GIFT_CARD_CHECK = "PreCommitGiftCardCheck";
    
    public static final String COMMIT_ORDER_PROCESS = "CommitOrderProcess";
    
    public static final String COMMIT_ORDER_UPDATE = "CommitOrderUpdate";
    
    public static final String SUBMIT_ORDER_PROCESS = "SubmitOrderProcess";
    
    public static final String CYBERSOURCE_AUTH_CALL = "CyberSourceAuthCall";
    
    public static final String CYBERSOURCE_TAX_CALL = "CyberSourceTaxCall";

    public static final String ADD_ITEM_ORDER = "ADD_ITEM_TO_ORDER";

	public static final String POST_LOGIN_USER = "postLoginUser";

	public static final String BBB_PROFILE_TOOLS = "BBBProfileTools";
	
	public static final String TBS_PROFILE_TOOLS = "TBSProfileTools";

	public static final String LOAD_USER_CART = "loadUserShoppingCartForLogin";

	public static final String REM_DISABLE_SKUS = "removeDisabledSKUs";

	public static final String BBB_ORDER_MGR = "BBBOrderManager";

	public static final String REPRICE_ORDER = "repriceOrder";

	public static final String REPRICE_CART = "repriceShoppingCarts";

	public static final String PERS_ORD_NEED = "persistOrderIfNeeded";
	
	public static final String SEND_EMAIL = "SendEmail";
	
	public static final String SEND_PDP_EMAIL = "SendPDPEmail";
	
	public static final String SEND_TABLE_REGISTRY_CART_EMAIL = "SendTableRegistryCartEmail";
	
	public static final String SEND_COMPARE_EMAIL = "SendCompareEmail";
	
	public static final String SEND_TABLE_CHECKLIST_EMAIL = "SendTableCheckListEmail";
	
	public static final String SEND_FEEDBACK_EMAIL = "SendFeedbackEmail";
	
	public static final String ADD_ITEM_TO_WISH_LIST = "Add_Item_To_Wish_List";

	public static final String ADD_ITEM_TO_GIFT_LIST = "Add_Item_To_Gift_List";

	public static final String ADD_TOITEM_POST_LOGIN_USER = "Add_Toitem_Post_Login_User";

	public static final String CHECKUSER_TOKEN_BVRR = "Cchekuser_Token_BVRR";
	
	public static final String LOAD_ORDER_FROM_COOKIE = "load_order_from_cookie";

	public static final String BAZAAR_VOICE_API_CALL = "Bazaar_Voice_Api_Call";

	
	public static final String LOAD_SAVED_ITEM_FROM_COOKIE = "load_saved_item_from_cookie";
	
	public static final String STATUS_CHANGE_MESSAGE = "status_change_message";
	public static final String PAYPAL_CHECKOUT = "PAYPAL_CHECKOUT";
	
	public static final String INTERNATIONAL_CHECKOUT = "INTERNATIONAL_CHECKOUT";
	public static final String INTERNATIONAL_CHECKOUT_SERVICE = "INTERNATIONAL_CHECKOUT_SERVICE";
	public static final String INTERNATIONAL_ORDER_CONFIRMATION_UNMARSHALLER = "InternationalOrderConfirmationUnMarshaller - processResponse";
	public static final String INTERNATIONAL_ORDER_CONFIRMATION_MARSHALLER = "INTERNATIONAL_CHECKOUT_SERVICE";
	
	 /** Performance Monitor constant for Exim Locak Api Call*/
    public static final String EXIM_LOCK_API = "EximLockApi";
    
    public static final String LOOKUP_IN_US_IP_CACHE = "lookUpInUSIPCache";
    public static final String BUILD_US_IP_CACHE = "buildUSIPAddressCache";
	
    public static final String LOOKUP_LOCAL_DYN_SKU_CACHE = "lookUpInLocalDynSKUCache";
    public static final String BUILD_LOCAL_DYN_SKU_CACHE = "buildLocalDynSKUCache";
	
	// Price Accessors for OCP-11.1 Upgrade 
	public static final String PRICE_ACCESSOR_CALL = "price_accessor_call";
	public static final String MIE_PRICE_ACCESSOR_CALL = "mie_price_accessor_call";
	
	// Performance Monitor constant for EndecaContentResponseParser...	
	public static final String ENDECA_CONTENT_RESPONSE_PARSER = "endeca_content_response_parser";
	
	// Performance Monitor constant for EndecaContentResponseParser...	
	public static final String ENDECA_SEARCH_UTIL = "endeca_search_util";
	
	public static final String LOCAL_INVENTORY_DB_CALL = "localInventoryDbCall";

	public static final String SEARCH_MANAGER = "SearchManager";

	public static final String TBS_ORDER_SEARCH_FORMHANLDER = "TBSOrderSearchFormHandler";


	public static final String REST_SEARCH_MANAGER = "RestSearchManager";

	public static final String SEARCH_CACHE_SCHEDULER = "SearchCacheRefreshScheduler";
	public static final String OMNITURE_PRODUCT_BOOSTING = "OmnitureProductBoosting";
	public static final String OMNITURE_REPORT_TOOLS_IMPL="OmnitureReportAPIToolsImpl";
	public static final String OPB_BOOSTING_STRATEGY = "OPB_BOOSTING_STRATEGY";
	public static final String SORT_BOOSTING_STRATEGY = "SORT_BOOSTING_STRATEGY";
	public static final String FACET_BOOSTING_STRATEGY = "FACET_BOOSTING_STRATEGY";
	public static final String ENDECA_SEARCH_TOOLS = "ENDECA_SEARCH_TOOLS";
	public static final String OMNITURE_BOOSTEDL2L3BRAND_PRODUCT_TOOLS = "OmnitureBoostedL2L3BrandProductTools";
	public static final String RESULTS_LIST_HANDLER = "BBBRESULTS_LIST_HANDLER";
	public static final String MANAGE_REGISTRY_CHECKLIST="manageRegistryChecklist";
	public static final String CHECKLIST_DROPLET="checklistDroplet";
	public static final String MY_ITEMS_CHECKLIST = "MyItemsCheckList";
	
	public static final String CACHE_CONTROL = "Cache_Control";
	public static final String INVALIDATE_LOCAL_STORE_CACHE_SCHEDULER="InvalidateLocalStoreRepositoryCacheScheduler";

	public static final String OMNITURE_VARIABLE_DROPLET = "OmnitureVariableDroplet";
	
	public static final String CATEGORY_REDIRECT_URL_LOADER = "CategoryRedirectURLLoader";	
	public static final String FETCH_CATEGORY_REDIRECTURLS = "fetchCategoryRedirectURLs";
	public static final String DO_START_SERVICE = "doStartService";
	public static final String CATEGORY_REDIRECTURL_LISTENER = "CategoryRedirectURLListener";
	public static final String DEPLOYMENT_EVENT = "deploymentEvent";

}
