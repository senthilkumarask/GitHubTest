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


/**
 * This class keeps all constants for PayPal.
 *
 * 
 *
 */
public class BBBPayPalConstants {
	
	
	public static final String ERR_PROPERTY_INVALID_PP_ADDRESS = "err_property_invalid_pp_address";
	public static final String ERR_PROPERTY_MISSING_PP_ADDRESS = "err_property_missing_pp_address";
	public static final String ERR_SHIPPING_METHOD_RESTRICTION = "err_shipping_method_restriction";
	public static final String ERR_PP_ADDRESS_EMPTY = "err_pp_address_empty";
	public static final String ERR_PO_BOX_PP_ADDRESS = "err_PoBox_pp_address";
	public static final String ERR_INTERNATIONAL_PP_ADDRESS = "err_international_pp_address";
	public static final String ERR_INTERNATIONAL_PP_ADDRESS_CART = "err_international_pp_address_cart";
	public static final String ERR_SHIPPING_METHOD_INCORRECT = "err_shipping_method_incorrect";
	public static final String ERR_ZIP_CODE_RESTRICTION_PP_ADDRESS = "err_zip_code_restriction_pp_address";
	public static final String ERR_STATE_INCORRECT_PP_ADDRESS = "err_state_incorrect_pp_address";
	public static final String ERR_POBOX_INCORRECT_PP_ADDRESS = "err_pobox_incorrect_pp_address";
	public static final String REDIRECT_URL = "redirectUrl";
	public static final String REDIRECT_STATE = "redirectState";
	public static final String SUCCESS = "success";
	public static final String ADDRESS_OUTPUT = "addressOutput";
	public static final String ADDRESS_ERROR_MAP = "address_error_map";
	public static final String US_COUNTRY_CODE = "US";
	public static final String CA_COUNTRY_CODE = "CA";
	public static final String QAS_VERIFIED = "qasVerified";
	public static final String VALIDATE_ADDRESS = "validateAddress";
	public static final String PAYPAL_SESSION_BEAN = "PayPalSessionBean";
	public static final String PAYPAL_ADDRESS = "PayPalAddress";
	public static final String ADDRESS_IN_ORDER = "addInOrder";
	public static final String COUPONS = "COUPONS";
	public static final String SHIPPING = "SHIPPING";
	public static final String CART = "CART";
	public static final String CART_REDIRECT_STATE = "cart";
	public static final String SHIPPING_SINGLE = "SHIPPING_SINGLE";
	public static final String SP_PAYPAL ="SP_PAYPAL";
	public static final String PREVIEW = "PREVIEW";
	public static final String COUPONS_US = "CouponTag_us";
	public static final String COUPONS_BABY = "CouponTag_baby";
	public static final String COUPONS_CANADA = "CouponTag_ca";
	public static final String ORDER = "Order";
	public static final String PROFILE = "Profile";
	public static final String ADDRESS = "bbbAddress";
	public static final String PAYERID = "payerId";
	public static final String SITE_CURRENCY_USD = "USD";
	public static final String QAS_SUGGESTED_ADDRESS = "QasSuggestedAddress";
	public static final String FROM_PAYPAL = "fromPayPal";
	public static final String ADDRESS1 = "address1";
	public static final String ADDRESS2 = "address2";
	public static final String CITY_NAME = "cityName";
	public static final String STATE_NAME = "stateName";
	public static final String ZIP_CODE = "zipCode";
	public static final String PAYPAL_REDIRECT_PATH = "/cart/paypalRedirect.jsp";
	public static final String PAYPAL_SESSION_BEAN_PATH = "/com/bbb/commerce/order/paypal/PayPalSessionBean";
	public static final String EMAIL = "paypalemail";
	public static final String SHIPPING_RESTRICTION = "?shippingRestriction=true";
	public final static String FIRST_NAME = "firstName";
	public final static String LAST_NAME = "lastName";
	public final static String COMPANY_NAME = "companyName";
	public final static String STATE = "state";
	public final static String CITY = "city";
	public final static String POSTAL_CODE = "postalCode";
	public final static String COUNTRY = "country";
	public final static String IS_ADDRESS_SENT ="isAddressSent";
	public final static String PAYPAL_ADDRESS_VO ="paypalAddressVO";
	public final static String UPDATED_MOBILE_ADDRESS ="updatedMobileAddress";
	public final static String FROM_PAYMENT_PAGE ="fromPaymentPage";
	public final static String ISSHIPPINGMETHODCHANGE ="isShippingMethodChanged";
	public final static String FROMPREVIEW = "&isFromPreview=true";
	public final static String PAYPAL_BILLING_ADDRESS = "paypalAddress";
	public final static String BILLING = "billing";
	public final static String SHIPPING_PAGE = "shipping";
	public final static String PAYMENT = "PAYMENT";
	public final static String SP_CHECKOUT_SINGLE = "SP_CHECKOUT_SINGLE";
	public final static String COUPON_MANAGER = "/com/bbb/account/BBBGetCouponsManager";
	public static final String ERR_CART_PAYPAL_SET_EXPRESS_SERVICE = "err_cart_paypal_set_express_service";
	public static final String ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR = "err_paypal_set_express_shipping_error";
	public final static String OPARAM_SETEXPRESS = "setExpressOutput";
	public final static String PAYPAL_ERROR_URL = "payPalErrorUrl";
	public static final String ERR_PAYPAL_GET_SERVICE_FAIL = "err_paypal_get_service_fail";
	public static final String ERR_PAYPAL_FAIL_GENERIC_MSG = "err_paypal_fail_generic_msg";
	public static final String ERR_PAYPAL_DETAILS_REMOVE_FAIL = "err_paypal_details_remove_fail";
	public static final String ERR_PAYPAL_TOKEN_UPDATE_FAIL = "err_paypal_token_update_fail";
	public static final String ERR_PAYPAL_PAYMENT_GROUP_FAIL = "err_paypal_payment_group_fail";
	public static final String ERR_PAYPAL_EMAIL_NOT_VALID = "err_paypal_email_not_valid";
	public static final String ERR_PAYPAL_PHONE_NOT_VALID = "err_paypal_phone_not_valid";
	public static final String ERR_PAYPAL_PAYER_ID_NOT_VALID = "err_paypal_payer_id_not_valid";
	public static final String ERR_PAYPAL_SHIPPING_FAIL = "err_paypal_shipping_fail";
	public static final String VALIDATE_SHIPPING_METHOD = "validateShippingMethod";
	public static final String ERR_PAYPAL_BILLING_FAIL = "err_paypal_billing_fail";
	public final static String PARTIALLY_ELIGIBLE = "PartiallyEligible";
	public final static String ELIGIBLE = "Eligible";
	public final static String VERIFIED = "verified";
	public final static String UNVERIFIED ="unverified";
	public final static String UNCONFIRMED ="Unconfirmed";
	public final static String CONFIRMED ="Confirmed";
	public static final String REVIEW = "REVIEW";
	public static final String SP_REVIEW = "SP_REVIEW";
	public static final String PAYPAL_ERROR_EXIST = "PayPalErrorExist";
	public static final String ERR_QUEBEC_PP_SHIPPING_ADDRESS = "err_quebec_pp_shipping_address";
	public static final String QUEBEC = "Quebec";
	public static final int PAYPAL_SHIPPING_ERROR_ID = 10736;
	public static final String PAYPAL_SHIPPING_ERROR_ID_STRING = "10736";
	public static final String PAYPAL_SHIPPING_ERROR = "?paypalShipping=true&isFromPreview=true";
	
	public static final String DUMMY_PHONE_NUMBER ="0000000000";
	public final static String VALIDATE_ORDER_ADD = "validateOrderAddress"; 
	public static final String ERROR_EXIST ="error_exist";
	public static final String PAYPAL_ERROR ="?paypalError=true";
}
