package com.bbb.commerce.browse;


public class BBBSearchBrowseConstants {



	public final static String LAST_VIEWED_PRODUCT_ID_LIST = "productIdList";
	public final static String LAST_VIEWED_REGISTRY_ID_LIST = "registryIdList";

	public static final String SESSION_BEAN = "sessionBean";
	public static final String PRODUCT_ID_PARAMETER="id";
	public static final String PRODUCT_VO_LIST= "productVOList";
	public static final String IS_META_DETAILS = "isMetaDataRequired";

	/**
	 * Instance variable for priceInfoVO.
	 */
	public static final String SKUDETAILVO = "pSKUDetailVO";
	public static final String PRODUCTVO = "productVO";
	public static final String DEFAULTCHILDSKU = "pDefaultChildSku";
	public static final String FIRSTATTRIBUTSVO = "pFirstAttributsVO";
	
	/*
	 * Constants for REST Module.
	 */
	
	public static final String ERROR_INVALID_KEYWORD = "error_invalid_keyword";
	public static final String ERROR_INVALID_KEYWORD_MESSAGE = "Keyword length is less than 3.";
	public static final String ERROR_IN_INPUT = "error_in_input";
	public static final String ERROR_IN_INPUT_MESSAGE = "There is an issue in Input JSON having all Search parameters.";
	public static final String ERROR_SEARCH_KEYWORD_LESS_THAN_3 = "error_search_keyword_length_less_than_3";
	public static final String ERROR_SEARCH_KEYWORD_LESS_THAN_3_MESSAGE = "Search Keyword length is less than 3.";
	public static final String ERROR_JSON_SEARCH_PARAM_MISSING = "error_json_search_param_missing";
	public static final String ERROR_JSON_SEARCH_PARAM_MISSING_MESSAGE = "There is an issue in Input JSON having Search parameters.";
	public static final String SYSTEM_ERROR = "system_error_querying_endeca";
	public static final String SYSTEM_ERROR_MESSAGE = "Some issue occured while querying Endeca. Check for Correct configurations.";
	public static final String ERROR_CATALOGID_MISSING = "error_catalogid_missing";
	public static final String ERROR_ROOT_CATEGORYID_MISSING = "error_rootCategoryid_missing";
	public static final String EMPTY_PARAMETER = "required_parameter_empty";
	public static final String RQL_EMPTY_PARAMETER ="rql_parameter_empty";
	
	public static final String TEMPLATE_ARG_MISSING = "param_template_name_missing";
	public static final String TEMPLATE_ARG_MISSING_MESSAGE = "Parameter template name is missing";
	public static final String TEMPLATE_NOT_EXIST = "template_not_exist";
	public static final String TEMPLATE_NOT_EXIT_MESSAGE = "The template does not exist";
	public static final String CHANNEL_PARAM_MISSING = "channelid_headparam_missing";
	public static final String CHANNEL_PARAM_MISSING_MESSAGE = "The header param channelId missing";
	public static final String TEMPLATE_VIEW_MISSING = "template_view_not_exist";
	public static final String TEMPLATE_VIEW_MISSING_MESSAGE = "The template view does not exist";
	public static final String ERROR_TEMPLATE_ARGS = "template_args_error";
	public static final String ERROR_TEMPLATE_ARGS_MESSAGE = "There is some error with arguments";

	
	
	/** Page URL parameters */
	public static final String CATEGORY_LANDING_FLAG = "catFlg";
	public static final String PRODUCT_LISTING_FLAG = "subCatPlp";
	public static final String TYPE_AHEAD_KEYWORD = "search";
	
	/** Landing Template Names */
	public static final String CATEGORY_LANDING_TEMPLATE = "CategoryLandingPage";
	
	/** Output Parameters */
	public static final String OPARAM_SUBCAT = "subcat";
	public static final String BROWSE_SEARCH_VO = "browseSearchVO";
	public static final String SEARCH_RESULTS_VO = "BBBProductListVO";
	public static final String TYPE_AHEAD_RESULTS_VO = "FacetQueryResults";
	public static final String PRODUCTS_LIST = "productList";
	
	/** Other Constants */
	public static final String TYPE_AHEAD_FACET_LIST_DELIMITER = ",";
	

	// Dimension Name for School State in Search Engine.
	public static final String SCHOOL_STATE = "School_State";
}
