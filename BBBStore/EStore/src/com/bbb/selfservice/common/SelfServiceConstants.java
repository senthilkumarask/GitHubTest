package com.bbb.selfservice.common;

public class SelfServiceConstants {

	public static final String ID_BASED_STORE_SEARCH = "1";
	public static final String ZIPCODE_BASED_STORE_SEARCH = "2";
	public static final String ADDRESS_BASED_STORE_SEARCH = "3";
	public static final String COORDINATES_BASED_SEARCH = "4";
	public static final String STORESEARCHCOOKIENAME = "storeSearchString";
	public static final String STORESEARCHTYPECOOKIENAME = "storeSearchType";
	public static final String STORESEARCHINPUTSTRINGCOOKIENAME = "storeSearchInputString";
	public static final String RADIUSMILES = "miles";
	public static final String HIDE_STORE_SEARCH_FORM = "hideStoreSearchForm";
	public static final String SEARCH_BASED_ON_USER_LOCATION = "UserLocation";
	public static final String SEARCH_BASED_ON_USER_INPUTS = "UserInputs";
	//List of fields from hosted data table
	public static final String MAPQUEST_COLUMN_LIST="RecordId,N,Address,City,State,Country,Phone,Lat,Lng,postal,hours,specialty_shops_cd";
	//Store search response field list
	public static final String RECORDID = "RecordId";
	public static final String NAME = "N";
	public static final String SPECIAL_MSG = "SPECIAL_MSG";
	public static final String ADDRESS = "Address";
	public static final String CITY = "City";
	public static final String STATE = "State";
	public static final String COUNTRY = "Country";
	public static final String POSTAL = "postal";
	public static final String PHONE = "Phone";
	public static final String STORE_DISTANCE = "distance";
	public static final String RESULTNUMBER = "resultNumber";
	public static final String LNG = "Lng";
	public static final String LAT = "Lat";
	public static final String SPECIALTY_SHOPS_CD = "specialty_shops_cd";
	public static final String STORE_TYPE = "store_type";
	public static final String INFO = "info";	
	public static final String MESSAGES = "messages";
	public static final String HOURS = "hours";
	public static final String STATUSCODE = "statusCode";
	public static final String TOTALPAGES = "totalPages";
	public static final String OPTIONS = "options";
	public static final String UNITS = "units";
	public static final String PAGEKEY = "pageKey";
	public static final String CURRENTPAGE = "currentPage";
	public static final String RESULTSCOUNT = "resultsCount";
	public static final String SEARCHRESULTS = "searchResults";
	public static final String FIELDS = "fields";
	public static final String COLLECTIONS ="collections";
	public static final String ADMINAREA4 = "adminArea4";
	public static final String CITY_EQUALS="city=";
	public static final String STREET_EQUALS="street=";
	public static final String STATE_EQUALS="state=";
	public static final String POSTAL_CODE_EQUALS="postalCode=";
	public static final String LOCATION_EQUALS="location=";
	
	
	//Route Directions response field list
	public static final String ROUTE_FASTEST = "fastest";
	public static final String HAS_TOLL_ROAD = "hasTollRoad";
	public static final String LOCATIONS = "locations";
	public static final String LATLNG = "latLng";
	public static final String INDEX = "index";
	public static final String TIME = "time";
	public static final String LOCATIONLNG = "lng";
	public static final String LOCATIONLAT = "lat";
	public static final String DISTANCE = "distance";
	public static final String SESSIONID = "sessionId";
	public static final String HAS_SEASONAL_CLOSURE = "hasSeasonalClosure";
	public static final String HAS_COUNTRY_CROSS = "hasCountryCross";
	public static final String FORMATTED_TIME = "formattedTime";
	public static final String ROAD_GRADE_STRATEGY = "roadGradeStrategy";
	public static final String HAS_UNPAVED = "hasUnpaved";
	public static final String HAS_HIGHWAY = "hasHighway";
	public static final String HAS_FERRY = "hasFerry";
	public static final String MANEUVERS = "maneuvers";
	public static final String SIGNS = "signs";
	public static final String MANEUVERNOTES = "maneuverNotes";
	public static final String DIRECTION = "direction";
	public static final String LINKIDS = "linkIds";
	public static final String NARRATIVE = "narrative";
	public static final String STREETS = "streets";
	public static final String ATTRIBUTES = "attributes";
	public static final String TRANSPORTMODE = "transportMode";
	public static final String DIRECTION_NAME = "directionName";
	public static final String MAPURL = "mapUrl";
	public static final String START_POINT = "startPoint";
	public static final String ROUTE = "route";
	public static final String LEGS = "legs";	
	public static final String ICONURL = "iconUrl";	
	public static final String AVOIDS_HIGHWAYS = "Limited Access";
	public static final String AVOIDS_TOLLROAD = "Toll Road";
	public static final String AVOIDS_SEASONAL_ROADS = "Approximate Seasonal Closure";
	public static final String ADMINAREA5 = "adminArea5";
	public static final String ADMINAREA3 = "adminArea3";
	public static final String POSTALCODE = "postalCode";
	public static final String DRIVINGHOURS = "hours";
	public static final String DRIVINGMINUTE = "minute";
	public static final String ROUTETYPEQUICK = "quick";
	public static final String LIKE = " like ";
	public static final int STORE_PICKUP_NOT_AVAILABLE = 10;
	
	public static final String ERROR_FAVORITE_STORE = "err_favourite_missing_exception";
	public static final String ERROR_FAVORITE_SITE_STORE = "err_favourite_site_exception";
	public static final String REST = "rest";
	public static final String RADIUS = "radius";
	public static final String SEARCHTYPE = "searchType";
	public static final String ADDRESS_LOWER_CASE = "address";
	public static final String CITY_LOWER_CASE = "city";
	public static final String STATE_LOWER_CASE = "state";
	public static final String COUNTRY_LOWER_CASE = "country";
	public static final String STOREDETAILS = "StoreDetails";
	public static final String PRODUCTAVAILABLE = "productAvailable";
	
	public static final String ERROR_SEARCH_STORE_INVALID_INPUT = "err_search_store_invalid_input";
	public static final String ERROR_SEARCH_STORE_MANDATORY_INPUT = "err_store_mandatory_input";
	public static final String ERROR_SEARCH_STORE_INVALID_RADIUS = "err_invalid_radius";
	public static final String ERROR_SEARCH_STORE_INVALID_PAGENUMBER ="err_invalid_pageNumber";
	public static final String ERROR_SEARCH_STORE_INVALID_PAGESIZE ="err_invalid_pageSIze";
	public static final String ERROR_SEARCH_STORE_TECH_ERROR = "err_store_search_tech_error";
	public static final String ERROR_SEARCH_STORE_BUSINESS_ERROR = "err_store_search_business_error";
	public static final String ERROR_SEARCH_STORE_IO_ERROR = "err_store_search_io_exception";
	public static final String ERROR_SEARCH_STORE_INVALID_QUANTITY = "err_invalid_quantity";
	public static final String ERROR_SEARCH_STORE_PRODUCT_NOT_AVAILABLE = "err_product_not_available";
	public static final String DISPLAY_ONLINE = "display_online";
	public static final String DISPLAY_ONLINE_FLAG = "Y";
	public static final String ERROR_SEARCH_STORE_ERROR = "err_something_wrong";
	public static final String STREET = "street";
	
	public static final String RESULTS = "results";
	public static final String STATUS_CODE = "statuscode";
	public static final String LAT_LNG_COOKIE = "latLngCookie";
	public static final String STORE = "store";
	public static final String SPECIALITY_CODE_ID = "specialityCodeId";
	public static final String SPECIALITY_CODE = "specialityCode";
	public static final String SPECIALITY_CODE_NAME = "specialityCodeName";
	public static final String SPECIALITY_CODE_MAP = "specialityCodeMap";
	public static final String SPECIALITY_SHOP_CD = "specialityShopCd";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String STORE_NAME = "storeName";
	public static final String SPECIAL_MSG_PROPERTY = "specialMsg";
	public static final String COUNTRY_CODE = "countryCode";
	public static final String PHONE_LOWER_CASE = "phone";
	public static final String STORE_TYPE_PROPERTY = "storeType";
	public static final String CONTACT_FLAG = "contactFlag";
	public static final String SEARCH_BASED_ON_COOKIE = "searchBasedOnLatLngCookie";
	public static final String COOKIE = "cookie";
	public static final String LONG = "long";
	public static final String DUMMY_STORES = "dummyStores";
	public static final String ORIGNAL_STORE_ID = "orignalStoreId";
	public static final String DUMMY_STORE_ID = "dummyStoreId";
	public static final String DUMMY_STORE_MAP = "dummyStoreMap";
	public static final String FLAG_DRIVEN_FUNCTIONS="FlagDrivenFunctions";
	public static final String SUPPLY_BALANCE_ON = "SUPPLY_BALANCE_ON";
	public static final String LOCAL_STORE_PDP_FLAG = "LOCAL_STORE_PDP_FLAG";
	public static final String MILES = "m";
	public static final String KILO_METERS = "km";
	public static final String RADIUS_CHANGE = "radiusChange";
	public static final String PROVINCE ="province";
	public static final String RADIUS_STORE ="radiusStore";
	public static final String COOKIE_LAT_LNG ="CookieLatLng";
	
	
	public SelfServiceConstants() {
		super();
		// TODO Auto-generated constructor stub
	}
}
