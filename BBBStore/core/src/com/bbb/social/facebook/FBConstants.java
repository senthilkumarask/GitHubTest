/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  FBConstants.java
 *
 *  DESCRIPTION: FBConnectFormHandler extends ATG OOTB CommerceProfileFormHandler
 *  			 and perform form handling activities related to facebook user profile. 	
 *  HISTORY:
 *  03/02/12 Initial version
 *  	
 */
package com.bbb.social.facebook;

/**
 * Class containing contants specific to Facebook Connect
 * @author hbandl
 *
 */
public class FBConstants {

	
	/**
	 * Constant for event suffix to represent Facebook connect   
	 */
	public static final String EVENT_SUFFIX_FB_CONNECTED = "_WITH_FB_CONNECT";
	
	/**
	 * Constant for event profile not found 
	 */
	public static final String EVENT_PROFILE_NOT_FOUND = "PROFILE_NOT_FOUND";
	
	/**
	 * Constant for event BBB profile found in same site 
	 */
	public static final String EVENT_BBB_PROFILE_EXIST_IN_SAME_SITE = "BBB_PROFILE_EXIST_IN_SAME_SITE";
	
	/**
	 * Constant for event BBB profile found in sister sites 
	 */
	public static final String EVENT_BBB_PROFILE_EXIST_IN_SAME_SITE_GROUP = "BBB_PROFILE_EXIST_IN_SAME_SITE_GROUP";
	
	/**
	 * Constant for event BBB profile found in other site group 
	 */
	public static final String EVENT_BBB_PROFILE_EXIST_ON_DIFFERENT_SITE_GROUP = "BBB_PROFILE_EXIST_ON_DIFFERENT_SITE_GROUP";
	
	/**
	 * Constant for error while fetching Facebook profile 
	 */
	public static final String ERROR_FETCH_FACEBOOK_PROFILE = "error_fetch_fb_profile";
	
	/**
	 * Constant for empty string 
	 */
	public static final String EMPTY_STRING = "";
	
	/**
	 * Constant for event Facebook profile found but not connected with any BBB profile. 
	 */
	public static final String EVENT_FB_PROFILE_FOUND_NOT_LINKED_WITH_BBB_PROFILE = "FB_PROFILE_FOUND_NOT_LINKED_WITH_BBB_PROFILE";
	
	/**
	 * Constant for removing Facebook profile association with BBB profile  
	 */
	public static final String FB_OPERATION_REMOVE = "FB_OPERATION_REMOVE";
	
	/**
	 * Constant for creating Facebook profile association with BBB profile 
	 */
	public static final String FB_OPERATION_CREATE = "FB_OPERATION_CREATE";
	
	/**
	 * Constant for updating Facebook profile association with BBB profile 
	 */
	public static final String FB_OPERATION_UPDATE = "FB_OPERATION_UPDATE";
	
	/**
	 * Constant for security status for auto login
	 */
	public static final String SECURITY_STATUS = "securityStatus";
	
	/**
	 * Constant for operation parse Facebook basic information 
	 */
	public static final String PARSE_FB_BASIC_INFO = "PARSE_FB_BASIC_INFO";
	
	/**
	 * Constant for Facebook basic information 
	 */
	public static final String FB_BASIC_INFO = "FB_BASIC_INFO";
	
	/**
	 * Constant for error occurred while parsing Facebook basic information
	 */
	public static final String FB_ERROR_PARSE_BASIC_INFO = "Error occurred while fetching Facebook information";
	
	/**
	 * Response header paramerter to set
	 */
	public static final String FB_HEADER_PARAM = "BBB-ajax-redirect-url";
	
	/**
	 * BBBProfile Lookup based on FBAccountId - FBUserName
	 */
	public static final String PROFILE_LOOKUP_BASED_ON_FBACCOUNTID = "username";
	
	/**
	 * BBBProfile Lookup based on FBEMailId - Email Id
	 */
	public static final String PROFILE_LOOKUP_BASED_ON_FBEMAILID = "emailid";
	
	public static final String FB_UNLINK_SUCCESS = "unlinked";
	public static final String FB_UNLINK_ERROR = "unlinkingfailed";
	
	/**
	 * Facebook integration page account overview 
	 */
	public static final String FB_PAGE_ACCOUNT_OVERVIEW = "myAccountOverview";
	
	/**
	 * unexpected error occured 
	 */
	public static final String ERROR_FB_UNEXPECTED = "error_fb_unexpected_error";
	
	/**
	 * page section as Login registration 
	 */
	public static final String PAGE_SECTION_LOGIN_REG = "loginRegistration";
	
	/**
	 * event EVENT_BBB_PROFILE_EXIST_IN_SAME_SITE_WITH_FB_CONNECT 
	 */
	public static final String EVENT_BBB_PROFILE_EXIST_IN_SAME_SITE_WITH_FB_CONNECT = "BBB_PROFILE_EXIST_IN_SAME_SITE_WITH_FB_CONNECT";
	
	/**
	 * Error Facebook id provided already linked with other BBB profile. 
	 */
	public static final String ERROR_FB_ALREADY_LINKED = "error_fb_already_link";
	
	/**
	 * Error message 
	 */
	public static final String ERROR_MESSAGE = "error";
	
	/**
	 * Migrated user event   
	 */
	public static final String EVENT_MIGRATED_USER = "EVENT_MIGRATED_USER";
	
}
