/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBWebServiceConstants.java
 *
 *  DESCRIPTION: Stores all constants used in web services module 
 *
 *  HISTORY:
 *  12/02/11 Initial version
 *
 */

package com.bbb.constants;

/**
 * This class keeps all constants for Web Service module.
 * 
 * 
 * 
 */
public class BBBWebServiceConstants {

	
	public static final String EMPTY = "";
	public static final int ZERO = 0;
	/** Constant for Login Personalization */
	public static final String LOGIN_FROM = "loginFrom";
    public static final String TXT_LOGIN_ADD_REGISTRY = "txt_login_AddRegistry";
    public static final String TXT_LOGIN_CREATE_REGISTRY = "txt_login_CreateRegistry";
    public static final String TXT_LOGIN_WISHLIST =  "txt_login_WishList";
    
    
    
    
    public static final String TXT_WSDLKEY_WSTOKEN =  "WebServiceUserToken";

    public static final String TXT_WSDLKEY_SIGNATURE_PAYPAL = "WSDLSignature";
    public static final String TXT_WSDLKEY_PAYPAL_REDIRECT_URL = "WSDLRedirectURL";
    public static final String TXT_WSDLKEY_PAYPAL_VERSION = "WSDLVersion";
    public static final String TXT_WSDLKEY_PAYPAL_SETEXP = "WSDLSetExpService";
    
    /** Constant for WSDL Keys */
    public static final String TXT_WSDLKEY =  "WSDLKeys";
    public static final String TXT_WSDLKEY_LOGIN =  "BBBWebServiceLogin";
    public static final String TXT_WSDLKEY_PASSWORD =  "BBBWebServicePwd";
    public static final String TXT_WSDLKEY_WSENDPOINT =  "WSEndPoint";
    public static final String TXT_WSDLKEY_WSSTUBCLASSES =  "WSStubClasses";
    public static final String TXT_WSDLKEY_WSTIMEOUT =  "WSTimeout";
    public static final String TXT_WSDLKEY_WSSITEFLAG =  "WSDLSiteFlags";
    // Error code constants for create registry;
    
    public static final int ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS = 200;
	public static final int ERR_CODE_GIFT_REG_INVALID_REG_PASSWORD = 305;
	public static final int ERR_CODE_GIFT_REGISTRY_FATAL_ERROR = 900;
	public static final int ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN = 901;
	public static final int ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT = 902;
	
	public static final int ERR_CODE_COUPON_WALLET_PHONE = 68;
	public static final int ERR_CODE_COUPON_WALLET_EMAIL = 100;
	public static final int ERR_CODE_COUPON_WALLET_OFFER_1 = 132;
	public static final int ERR_CODE_COUPON_WALLET_PROFILE_EMAIL =356;
	public static final int ERR_CODE_COUPON_WALLET_OFFER_2 = 580;
	public static final int ERR_CODE_COUPON_WALLET_OFFER_3 = 596;
	public static final int ERR_CODE_COUPON_WALLET_OFFER_4 = 1;
	public static final int ERR_CODE_COUPON_WALLET_OFFER_5 = 612;
	public static final int ERR_CODE_COUPON_WALLET_OFFER_6 = 615;
	public static final int ERR_CODE_COUPON_WALLET_OFFER_7 = 602;
	
	
	public static final String TRUE = "true";
	public static final String COUPON_LIST_PARAMETER="couponList";
	public static final String SYSTEM_EXCEPTION_CODE="WS_system_exception";
	public static final String WEB_SERVICE_RESPONSE_ERROR="web_service_response_error";
	public static final String WEB_SERVICE_CALL_ERROR="web_service_call_error";
	public static final String ERROR_RETRIEVING_VALID_INPUT="error_retrieving_valid_input";
	
	//PayPal Constant
	public static final String TXT_WSDLKEY_PP_WSTOKEN =  "PPWebServiceUserToken";
    public static final String TXT_UNDERSCORE =  "_";
    public static final String TXT_WSDLKEY_PP_LOGIN =  "PPWebServiceLogin";
    public static final String TXT_WSDLKEY_PP_PASSWORD =  "PPWebServicePwd";  
    public static final String TXT_WSDLKEY_PP_SIGNATURE = "PPWSDLSignature";
    public static final String TXT_WSDLKEY_PP_CREDIENTAIL = "payPalCred";
    public static final String TXT_WSDLKEY_PP_REDIRECT_URL = "PPWSDLRedirectURL";
    public static final String TXT_WSDLKEY_PP_VERSION = "PPWSDLVersion"; 
    
    public static final String TXT_WSDLKEY_PP_ADDR_OR = "PPWSDLAddrOverride";
    public static final String TXT_WSDLKEY_PP_PAY_ACT = "PPWSDLPaymentAction";
    public static final String TXT_WSDLKEY_PP_EXP_MINUTES = "PPExpirationMinutes";
    
    public static final String SET_EXPRESS_CHECKOUT_SERVICE = "setExpressCheckout";
    public static final String GET_EXPRESS_CHECKOUT_SERVICE  = "getExpressCheckoutDetails";
    public static final String DO_EXPRESS_CHECKOUT_SERVICE  = "doExpressCheckoutPayment";
    public static final String DO_AUTHORIZATION  = "doAuthorization";
	public static final String TXT_WSDLKEY_PP_SUBJECT = "PPWSDLSubject";
	
	public static final String TXT_LOGIN_COUPONWALLET =  "txt_login_CouponWallet";
      
}
