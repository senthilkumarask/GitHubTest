package com.bbb.search.endeca.constants;

public class BBBEndecaConstants {

	public static final String POPULAR = "popular";
	public static final String BRAND = "brand";
	public static final String DEPARTMENT = "department";
	public static final String MAX_POPULAR_ITEMS_COUNT = "maxPopularItemsCount";
	public static final String MAX_DEPARTMENT_COUNT = "maxDepartmentCount";
	public static final String MAX_BRANDS_COUNT = "maxBrandsCount";
	public static final String MAX_L2_DEPT_COUNT = "maxL2DeptCount";
	public static final String MAX_MATCHES = "maxMatches";
	public static final String NTPR = "Ntpr";
	public static final String NTPC = "Ntpc";
	public static final String L2_DEPARTMENT = "L2Department";
	public static final String RECORD_TYPE ="Record Type";
	public static final String PROD_RECORD_TYPE ="Product";
	public static final String ALL = "All";
	public static final String ENABLED = "enabled";
	public static final String DYNRANK = "dynrank";
	public static final String DYNCOUNT = "dyncount";
	public static final String NODE_ID="node_id";
	public static final String DEFAULT_PAG_NUM= "1";
	public static final String DASH_LITERAL= "-";
	public static final String BLANK_STRING="";
	public static final String NAVIGATION = "N";
	public static final String NAV_REFINEMENT = "Ne";
	public static final String NAV_REFINEMENT_CONFIG = "Nrc";
	public static final String PIPE = "|";
	public static final String NAV_PROPERTY_NAME = "Ntk";
	public static final String NAV_KEYWORD = "Ntt";
	private static final String DIM_DISPLAY_VALUE = "dimDisplayValue_";
	public static final String STORE_MOBILE_TYPE_AHEAD_CACHE = "STORE_MOBILE_TYPE_AHEAD_CACHE";
	public static final String TRUE = "true";
	public static final String BBB_TYPE_AHEAD_VALUE = "Bed_Bath___Beyond_Type-Ahead";
	public static final String BBCA_TYPE_AHEAD_VALUE = "Bed_Bath_Canada_Type-Ahead"; 
	public static final String BBBABY_TYPE_AHEAD_VALUE  = "Buy_Buy_Baby_Type-Ahead"; 
	public static final String SEARCH_MODE_MATCHALL = "mode+matchall";
	public static final String INVENTORY_STATUS = "Inventory_Status";
	public static final String POSITIVE = "Positive";
	public static final String SEARCH_MODE_MATCHANY = "mode matchany";
//	public static final Map<String,String> TYPE_AHEAD_REF_DIM_MAP = Collections.unmodifiableMap(new HashMap<String, String>(){
//	{
//	 put(BBB_TYPE_AHEAD_VALUE, "110000");
//	 put(BBCA_TYPE_AHEAD_VALUE,"120000");
//	 put(BBBABY_TYPE_AHEAD_VALUE,"130000");
//	}});
	
	public static final String NEGATIVEMATCHQUERY = "negativeMatchQuery";
	public static final String ONLINE_PRODUCT_COUNT ="onlineProductCount";
	public static final String TYPE_AHEAD = "_Type-Ahead";
	public static final String P_STORES = "P_Stores";
	//private static final String MODE_MATCHPARTIAL = "mode+matchpartial";
	// Constants for holding dimension Query parameters.
	public static final String DIM_QUERY_KEYWORD = "D";
	public static final String DIM_QUERY_MODE = "Dx";
	public static final String DIM_REC_FILTER = "Dr";
	public static final String NAV_SEARCH_MODE = "Ntx";
	public static final String SITE_ID = "Site_ID";
	public static final String SCHOOL_NAME = "COLLEGE";
	public static final String SCHOOL_STATE = "SCHOOL STATE";
	public static final String COLLEGE_LOGO = "COLLEGE_LOGO";
	public static final String SEARCH_MODE = "searchMode";
	public static final String NAV_SEARCH_PHRASES_NTPC = "Ntpc";
	public static final String NAV_SEARCH_PHRASES_NTPR = "Ntpr";
	public static final String RQST_PARAM_NAME_SEARCH_QUERY_VO = "searchQueryVO";
	public static final String RQST_PARAM_NAME_ENDECA_QUERY_VO = "endecaQueryVO";
	public static final String CONTENT_ITEM_PARAM_NAME_SEARCH_RESULTS_VO = "searchResults";
	public static final String CONTENT_ITEM_PARAM_NAME_REDIRECTS_VO = "redirects";
	// Constants for Endeca Specific Parameters
	private static final String NAV_PAGE_ZONE = "NavigationPageZone";
	public static final String DGRAPH_BINS = "DGraph.Bins";
	// Constants for holding Query parameters.
	public static final String CATA_ID = "catalogId";
	public static final String CATA_REF_ID = "catalogRefId";
	/*private String dimDisplayMapConfig;
	private String dimNonDisplayMapConfig;*/
	/*private String scene7Path;*/
	public static final String ISREDIRECTED="&isRedirected";
	public static final String BOOSTCODE="&boostCode=";
	public static final String EPH_QUERYSCHEME_TYPE="&ephScheme=";
	public static final String NEWEST_FEATURED_PRODUCT = "NEWEST_FEATURED_PRODUCT";
	public static final String POPULAR_FEATURED_PRODUCT = "POPULAR_FEATURED_PRODUCT";
	public static final String TOP_RATED_FEATURED_PRODUCT = "TOP_RATED_FEATURED_PRODUCT";
	public static final String TRENDING_FEATURED_PRODUCT = "TRENDING_FEATURED_PRODUCT";
	public static final String SPONSORED_FEATURE_PRODUCT = "SPONSORED_FEATURE_PRODUCT";
	public static final String FEATURED_PRODUCT_ID = "P_Product_ID";
	public static final String FEATURED_PRODUCT_PROMO_LOCATION = "CENTER";
	//type-ahead dimemnsion name
	//used to store node id for retrieving depttree 
	//static dimension names 
	//N parameter for retrieving current refinement list
	//used for forming navigation query of facets and descriptors
	public static final String QUESTION_MARK = "?";
	public static final String URL_ENCODING = "UTF-8";
	// Constants for holding Query parameters.
	public static final String PAGE_NUMBER = "pagNum";
	//private static final String PAGE_SIZE = "pagFilterOpt";
	public static final String SORT_FIELD = "pagSortOpt";
	public static final String SEARCH_FIELD = "SearchField";
	public static final String DID_YOU_MEAN = "DidYouMean";
	public static final String CAT_REF_ID = "CatalogRefId";
	public static final String CAT_ID = "CatalogId";
	public static final String KEYWORD = "Keyword";
	public static final String FRM_BRAND_PAGE = "frmBrandPage" ;
	public static final String FRM_COLLEGE_PAGE = "fromCollege" ;
	//private static final String NAV_SEARCH_MODE = "Ntx";
	public static final String PARTIAL_FLAG = "partialFlag";
	/*private static final String MIN_PRICE = "priceMin" ;
	private static final String MAX_PRICE = "priceMax" ;
	private static final String DEFAULT_MIN_PRICE_RANGE = "defMin";
	private static final String DEFAULT_MAX_PRICE_RANGE = "defMax";*/
	public static final String NAV_FILTER = "Nf";
	public static final String IS_REDIRECT = "isRedirect";
	// Added for R2.2 SEO friendly Story : Start
	public static final String NAV_REC_OFFSET = "No";
	public static final String NAV_SEARCH_PHRASES_NRC = "Nrc";
	// Added for R2.2 SEO friendly Story : End
	public static final String CONTENT_ITEM_TYPE = "ThreeColumnContentItem";
	public static final String NAV_NR = "Nr";
	public static final String STRING_END = ")";
	public static final String STRING_DEL = ",";
	public static final String AND = "AND(";
	public static final String CHANNEL = "Channel ID";
	public static final String SITE_ID2 = "Site_ID";
	public static final String PARAM_PAGESIZE = "pageSize";
	public static final String PARAM_KEY = "key";
	public static final String PARAM_SORT_STRING = "sortString";
	public static final String IPAD_PATTERN = "(.*)(iPad|ipad)(.*)";
	// Constants for holding Navigation Query parameters.
	public static final String NAV_SORT_FIELD = "Ns";
	public static final String NAV_SORT_ORDER = "Nso";
	public static final String NAV_DID_YOU_MEAN = "Nty";
	// Constants for Endeca Specific Parameters
	public static final String LINK_REFERENCE = "LINK_REFERENCE";
	public static final String IMAGE = "IMAGE";
	public static final String CAPTION = "CAPTION";
	public static final String PROMO_TEXT = "PROMO_TEXT";
	public static final String IMAGE_HREF = "IMAGE_HREF";
	public static final String IMAGE_SRC = "IMAGE_SRC";
	public static final String IMAGE_ALT = "IMAGE_ALT";
	public static final String MOBILE_IMAGE_HREF = "MOBILE_IMAGE_HREF";
	public static final String MOBILE_IMAGE_SRC = "MOBILE_IMAGE_SRC";
	public static final String MOBILE_IMAGE_ALT = "MOBILE_IMAGE_ALT";
	public static final String FOOTER_TEXT = "FOOTER_TEXT";
	public static final String RELATED_SEARCHES = "RELATED_SEARCHES";
	// Request scope attribute name strings
	public static final String URL_FORMATTER = "urlFormatter";
	public static final String USER_AGENT = "User-Agent";
	// Context and servlet path string, set by the Controller
	public static final String CONTROLLER = "";

	public static final String CI_PROP_TEMPLATE_ID = "TemplateId";
	public static final String CI_PROPVAL_TEMPLATE_ID_FLYOUT_IMG = "FlyoutImage";
	public static final String CI_PROP_HEADER_CONTENT = "headerContent";
	public static final String CI_PROP_DIMENSION_NAME = "dimensionName";
	public static final String CI_PROP_NAME = "name";
	public static final String CI_PROP_DIMENSION_ID = "dimensionId";
	public static final String CI_PROP_NUM_REFINEMENTS = "numRefinements";
	public static final String CI_PROP_SORT = "sort"; 
	public static final String TYPE_AHEAD_CI_NAME = "typeAheadCI";
	public static final String CI_PROP_NODE_TYPE = "NODE_TYPE";
	public static final String CI_PROP_ALLCAPS_DGRAPH_BINS = "DGRAPH.BINS";

	
	public static final String CI_PROP_SORT_VALUE_DYNRANK = "DYNRANK"; 
	public static final String CI_PROP_SORT_VALUE_STATIC = "STATIC"; 
	public static final String CI_PROP_SORT_VALUE_DEFAULT = "DEFAULT"; 
	
	//internal name used for storing sub content items 
	public static final String CI_CONTENTS_NAME = "contents";
	
	public static final String CI_ROOT_CONTENT_PATH_NAME = "content";
	public static final String CI_CONTENT_PATH_SEPARATOR = "/";
	
	//lookup keys used for reading from config util
	public static final String CI_SEARCH_PLP_CONTENT_PATH_LOOKUP_KEY = "SEARCH_PLP";
	public static final String CI_BRAND_PLP_CONTENT_PATH_LOOKUP_KEY = "BRAND_PLP";
	public static final String CI_CATEGORY_PLP_CONTENT_PATH_LOOKUP_KEY = "CATEGORY_PLP";
	public static final String CI_ALL_BRANDS_CONTENT_PATH_LOOKUP_KEY = "ALL_BRANDS";
	public static final String CI_FLYOUT_CONTENT_PATH_LOOKUP_KEY = "FLYOUT";
	public static final String CI_CONTENT_PATH_DEFAULT_CHANNEL_NAME = "DEFAULT";
	public static final String CI_CHECKLIST_CATEGORY_PLP_CONTENT_PATH_LOOKUP_KEY = "CHECKLIST_CATEGORY";

	//MATCH MODES
	public static final String MATCH_MODE_ALL_PARTIAL = "matchallpartial";
	public static final String MATCH_MODE_PARTIAL = "matchpartial";
	public static final String MATCH_MODE_ALL = "matchall";
	public static final String MATCH_MODE_ANY = "matchany";
	public static final String MATCH_MODE_ALL_ANY = "matchallany";
	public static final String MATCH_MODE_PARTIAL_MAX = "matchpartialmax";
	public static final String MATCH_MODE_BOOLEAN = "matchboolean";
	
	//lookup keys for overriding default values in RefinementMenuHandler
	public static final String REFINEMENTS_SHOWN = "refinementsShown";
	public static final String MAX_NUM_REFINEMENTS = "maxNumRefinements";
	
	public static final String CI_ERROR_MESSAGE_LOOKUP_KEY = "@error";
	public static final String CI_ERROR_EXCEPTION_LOOKUP_KEY = "com.endeca.store.exceptions.PathNotFoundException";
	
	//Draco Merge
	public static final String CHANNEL_ID = "Channel ID";
	public static final String THEME_ID = "Theme_ID";
	public static final String _1 = "1";
	public static final String DK = "Dk";
    public static final String GROUP_ID="groupId";
    //Draco Merge

    //Tree Structure L2 ID property name in Exp Mgr
	public static final String TREE_STRUCTURE_L2_ID = "treeStructureL2ID";
	
	public static final String OPB_RECORD_TYPE ="OPB_Terms";
	public static final String P_OMNITURE_PRODUCT_ID ="P_Omniture_ProductId";
	public static final String NTK_P_OMNITURE_SEARCHTERM ="P_Omniture_SearchTerm";

	public static final String OCB_L2_DEL = "##";
	public static final String OCB_DATA_DEL = "\\*#";
	public static final int OCB_DATA_L2_NAME_INDEX = 1;
	public static final int OCB_DATA_L2_ID_INDEX = 2;
	public static final String L2_BOOST_L2S = "P_OCB_L2s";
	public static final String NTK_L2_BOOST_SEARCH_TERM = "P_OCB_SearchTerm";

	public static final String OPB_BOOSTING_STRATEGY = "OPB";
	public static final String SORT_BOOSTING_STRATEGY = "SORT";
	public static final String FACET_BOOSTING_STRATEGY = "FACET";
	public static final Integer OPB_BOOSTING_STRATEGY_TYPE = 1;
	public static final Integer SORT_BOOSTING_STRATEGY_TYPE = 2;
	public static final Integer FACET_BOOSTING_STRATEGY_TYPE = 3;
	public static final String TOTAL_BOOSTED_PRODUCTS = "TOTAL_BOOSTED_PRODUCTS";
	public static final String SEARCH_BOOST_REPOSITORY_VIEW = "SearchBoostAlgorithm";
	public static final String BOOST_ALGORITHM_BOOST_CODE = "boostCode";
	public static final String BOOST_ALGORITHM_DEFAULT_BOOST_CODE = "00";
	public static final String BOOST_ALGORITHM_OMNITURE_EVT_REQ = "omnitureEventRequired";
	public static final String BOOST_ALGORITHM_RANDOMIZATION_REQ = "randomizationRequired";
	public static final String BOOST_ALGORITHM_SITES = "sites";
	public static final String BOOST_ALGORITHM_LAST_MOD_DATE = "lastModifiedDate";
	public static final String BOOST_ALGORITHM_ID = "algorithmId";
	public static final String BOOST_ALGORITHM_NAME = "algorithmName";
	public static final String BOOST_ALGORITHM_PERCENTAGE = "percentage";
	public static final String BOOST_ALGORITHM_TYPE = "algorithmType";
	public static final String BOOST_ALGORITHM_DESCRIPTION = "algorithmDescription";
	public static final String BOOST_ALGORITHM_ENDECA_PROPERTY = "endecaProperty";
	public static final String BOOST_ALGORITHM_RECORD_COUNT = "RECORD_COUNT";
	public static final String BOOST_ALGORITHM_PAGE_TYPE = "pageType";
	public static final String SORT_BOOSTING_QUERY_NAV_FILTER = "Enabled_Age|LTEQ+"; 
	public static final String REPORT_DATA_FOR_L2_L3_BRAND = "reportDataForL2L3Brand"; 
	public static final String IDENTIFIER = "identifier"; 
	public static final String DEFAULT_0 = "DEFAULT-0"; 
	public static final String MDEX_ENGINE_FILTER_STATE = "filterState";
	public static final String NEGATIVE_RECORD_FILTER = "Nrs";
	public static final String NEW_SORT_TYPE = "P_Date";
	public static final String OPB_BOOST_STRATEGY_CACHE_ENABLED = "OPB_BOOST_STRATEGY_CACHE_ENABLED";
	public static final String SORT_BOOST_STRATEGY_CACHE_ENABLED = "SORT_BOOST_STRATEGY_CACHE_ENABLED";
	public static final String FACET_BOOST_STRATEGY_CACHE_ENABLED = "FACET_BOOST_STRATEGY_CACHE_ENABLED";
	public static final String BRAND_DIM_ID = "brandDimId";
	public static final String OMNITURE_BOOST_DESCRIPTION = "omnitureBoostDescription";
	public static final String NEGATIVE_FILTER = "NegativeFilter";
	public static final String OPB_BOOST_IN_STOCK_CACHE_ENABLED = "OPB_BOOST_IN_STOCK_CACHE_ENABLED";
	

	//filter conditions used for adding in EQL
	public static final String FILTER_CONDITION_START = "( ";
	public static final String FILTER_CONDITION_END = " )";
	public static final String FILTER_CONDITION_AND = " and ";
	
	//final condition defined for EQL
	public static final String NAV_REC_STRUCT_CONDITION_START = "collection()/record[";
	public static final String NAV_REC_STRUCT_CONDITION_END = "]";
	
	public static final String  CHECKLIST_CATEGORY="Checklist_Category";
}