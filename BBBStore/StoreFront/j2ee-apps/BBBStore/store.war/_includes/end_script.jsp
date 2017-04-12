<dsp:page>
    <c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>

  <%--   <c:set var="jsScriptCacheTimeout">
        <bbbc:config key="JsScriptCacheTimeout" configName="HTMLCacheKeys" />
    </c:set> --%>
    <c:set var="scene7AltPath">
        <bbbc:config key="scene7_alt_path" configName="ThirdPartyURLs" />
    </c:set>
    <c:set var="scene7DomainPath">
        <bbbc:config key="BBBSceneSevenDomainPath" configName="ThirdPartyURLs" />
    </c:set>
    <c:set var="harteAndHanksURL">
        <bbbc:config key="harteAndHanksURL" configName="ThirdPartyURLs" />
    </c:set>
    <c:set var="mapQuestStaticMapContentURL">
        <bbbc:config key="mapQuestStaticMapContentURL" configName="ThirdPartyURLs" />
    </c:set>
    <c:set var="maxCharLimitTypeAhead">
        <bbbc:config key="maxCharLimitTypeAhead" configName="ContentCatalogKeys" />
    </c:set>
    <c:set var="ajaxTimeOut">
        <bbbc:config key="ajaxTimeOut" configName="ContentCatalogKeys" />
    </c:set>
    <c:set var="imageLocation"><bbbt:textArea key='txt_international_ship_image_html' language='${language}' /></c:set>
    <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
        <dsp:getvalueof var="scheme" bean="/OriginatingRequest.scheme"/>
       <dsp:getvalueof var="serverName" bean="/OriginatingRequest.serverName"/>
   <%--  <dsp:droplet name="/atg/dynamo/droplet/Cache">
        <dsp:param name="key" value="${currentSiteId}_JSMessagePage" />
        <dsp:param name="cacheCheckSeconds" value="${jsScriptCacheTimeout}"/>
        <dsp:oparam name="output"> --%>

			<dsp:droplet name="/com/bbb/common/droplet/GetEximCustomizationCodesDroplet">
				<dsp:oparam name="output">
					<dsp:getvalueof var="jsonObject" param="eximCodesJsonString"/>
				</dsp:oparam>
			</dsp:droplet>

			<dsp:droplet name="/com/bbb/common/droplet/GetVendorJsonDroplet">
				<dsp:oparam name="output">
					<dsp:getvalueof var="vendorJsonString" param="vendorJsonString"/>
				</dsp:oparam>
			</dsp:droplet>
       
                <%--  Bed Bath & Beyond namespace as a global variable--%>
                var BBB = BBB || {};
				BBB.katoriCodes = ${jsonObject};

				BBB.customVendors  = ${vendorJsonString};
                /**
                * Extending BBB Object with all the String messages which will be used to show messages through JS.
                */
                BBB.glblStringMsgs = {
                	"txtAdBlockerMsg": "<bbbt:textArea key='txt_ad_blocker_msg' language='${language}'/>",
                    "txtCoRegTitle": "<bbbt:textArea key='txt_co_reg_modal_title' language='${language}'/>",
                	"lblEditPersFor": "<bbbl:label key='lbl_cart_edit_personalization_for' language='${language}'/>",
                	"manageGuideDropdown": "<bbbl:label key='lbl_manage_guide_dd' language='${language}'/>",
                	"lblRemovePersFor": "<bbbl:label key='lbl_cart_remove_personalization_for' language='${language}'/>",
                    "selected": "<bbbl:label key='lbl_coupon_selected_text' language='${language}'/>",
                    "applied": "<bbbl:label key='lbl_coupon_applied_text' language='${language}'/>",
                    "apply": "<bbbl:label key='lbl_coupon_apply_text' language='${language}'/>",
                    "applying": "<bbbl:label key='lbl_coupon_applying_text' language='${language}'/>",
                    "select": "<bbbl:label key='lbl_coupon_select_text' language='${language}'/>",
                    "selectSize": "<bbbl:label key='lbl_pdp_select_size' language='${language}'/>",
                    "selectColor": "<bbbl:label key='lbl_pdp_select_color' language='${language}'/>",
                    "selectFinish": "<bbbl:label key='lbl_pdp_select_finish' language='${language}'/>",
                    "readMore": "<bbbl:label key='lbl_js_read_more' language='${language}'/>",
                    "readLess": "<bbbl:label key='lbl_js_read_less' language='${language}'/>",
                    "viewCartContents": "<bbbl:label key='lbl_coupon_view_cart' language='${language}'/>",
                    "hideCartContents": "<bbbl:label key='lbl_coupon_hide_cart' language='${language}'/>",
                    "viewOrderContents": "<bbbl:label key='lbl_js_view_order_contents' language='${language}'/>",
                    "hideOrderContents": "<bbbl:label key='lbl_js_hide_order_contents' language='${language}'/>",
                    "addBtn": "<bbbl:label key='lbl_js_add_button' language='${language}'/>",
                    "cancelBtn": "<bbbl:label key='lbl_js_cancel_button' language='${language}'/>",
                    "okBtn": "<bbbl:label key='lbl_js_ok_button' language='${language}'/>",
                    "saveChangesBtn": "<bbbl:label key='lbl_js_save_changes_button' language='${language}'/>",
                    "doneBtn": "<bbbl:label key='lbl_js_done_button' language='${language}'/>",
                    "closeBtn": "<bbbl:label key='lbl_js_close_button' language='${language}'/>",
                    "itemAdded": "<bbbl:label key='lbl_js_item_added' language='${language}'/>",
                    "itemNotAdded": "<bbbl:label key='lbl_js_item_not_added' language='${language}'/>",
                    "enterNewShipAdd": "<bbbl:label key='lbl_js_enter_new_ship_address' language='${language}'/>",
                    "enterNewBillAdd": "<bbbl:label key='lbl_js_enter_new_bill_address' language='${language}'/>",
                    "showBtn": "<bbbl:label key='lbl_js_show_button' language='${language}'/>",
                    "yesBtn": "<bbbl:label key='lbl_js_yes_button' language='${language}'/>",
                    "noBtn": "<bbbl:label key='lbl_js_no_button' language='${language}'/>",
                    "msgSentTo": "<bbbl:label key='lbl_js_msg_sent_to' language='${language}'/>",
                    "charsRemaining": "<bbbl:label key='lbl_js_chars_remaining' language='${language}'/>",
                    "loading": "<bbbl:label key='lbl_js_loading' language='${language}'/>",
                    "goBtn": "<bbbl:label key='lbl_js_go_button' language='${language}'/>",
                    "college": "<bbbl:label key='lbl_js_college' language='${language}'/>",
                    "state": "<bbbl:label key='lbl_js_state' language='${language}'/>",
                    "province": "<bbbl:label key='lbl_js_province' language='${language}'/>",
                    "lblprovince": "<bbbl:label key='lbl_registrants_statecanada' language='${language}'/>",
                    "zipCodeCanada": "<bbbl:label key='lbl_registrants_zip_ca' language='${language}'/>",
                    "lblZip": "<bbbl:label key='lblZip' language='${language}'/>",
                    "lblstate": "<bbbl:label key='lblstate' language='${language}'/>",
                    "pwdReminderSent": "<bbbl:label key='lbl_js_pwd_reminder_sent' language='${language}'/>",
                    "emailAFriend": "<bbbl:label key='lbl_js_email_a_friend' language='${language}'/>",
                    "findInStore": '<bbbl:label key="lbl_js_find_in_store" language="${language}"/>',
                    "findAStore": "<bbbl:label key='lbl_js_find_a_store' language='${language}'/>",
                    "findStoreResults": '<bbbl:label key="lbl_js_find_store_results" language="${language}"/>',
                    "rateAndReview": '<bbbl:label key="lbl_js_rate_and_review" language="${language}"/>',
                    "itemAddedToList": '<bbbl:label key="lbl_item_added_to_list" language="${language}"/>',
                    "itemNotAddedToList": '<bbbl:label key="lbl_item_not_added_to_list" language="${language}"/>',
                    "fbConnectEnabled": '<bbbl:label key="lbl_fb_connect_enable" language="${language}"/>',
                    "fbDisconnectText": '<bbbl:label key="lbl_disconnect_my_overview" language="${language}"/>',
                    "pwdUpdated": '<bbbl:label key="lbl_js_pwd_updated" language="${language}"/>',
                    "s7messageTextDesktop": '<bbbl:label key="lbl_js_s7_tipmessage_desktop" language="${language}"/>',
                    "s7messageTextTouch": '<bbbl:label key="lbl_js_s7_tipmessage_touch" language="${language}"/>',
                    "s7ZoomImageModalTip": '<bbbl:label key="lbl_js_s7_zoom_modal_tip_desktop" language="${language}"/>',
                    "s7ZoomImageModalTipTouch": '<bbbl:label key="lbl_js_s7_zoom_modal_tip_touch" language="${language}"/>',
                    "lblSKU": '<bbbl:label key="lbl_js_s7_sku_number" language="${language}"/>',
                    "btnLBLFindAStore": '<bbbl:label key="lbl_fav_store_find" language="${language}"/>',
                    "btnLBLChangeStore": '<bbbl:label key="lbl_fav_store_change" language="${language}"/>',
                    "passwordRequirements": '<bbbl:label key="lbl_passwd_requirements" language="${language}"/>',
                    "btnBackToQuickView": '<bbbl:label key="lbl_backto_quickinfo" language="${language}"/>',
                    "titleRegPageAddToCart": '<bbbl:label key="lbl_title_reg_page_add_to_cart" language="${language}"/>',
                    "btnRegPageViewCart": '<bbbl:label key="lbl_btn_reg_page_view_cart" language="${language}"/>',
                    "lblRegPageQuantity": '<bbbl:label key="lbl_reg_page_quantity" language="${language}"/>',
                    "quickViewContinueShopping": '<bbbl:label key="lbl_cartdetail_continueshoppinglink" language="${language}"/>',
                    "shareOrderOn_facebook": '<bbbl:label key="lbl_js_shareOrderOn_facebook" language="${language}"/>',
                    "shareOrderOn_twitter": '<bbbl:label key="lbl_js_shareOrderOn_twitter" language="${language}"/>',
                    "shareOrderOn_pinterest": '<bbbl:label key="lbl_js_shareOrderOn_pinterest" language="${language}"/>',
                    "twitterHandle": '<bbbl:label key="lbl_js_twitterHandle" language="${language}"/>',
                    "twitterShareTextPrefix": '<bbbl:label key="lbl_js_twitterShareTextPrefix" language="${language}"/>',
                    "twitterShareTextSuffix": '<bbbl:label key="lbl_js_twitterShareTextSuffix" language="${language}"/>',
                    "twitterShareTweetSuffix": '<bbbl:label key="lbl_js_twitterShareTweetSuffix" language="${language}"/>',
                    "pinterestShareTextSuffix": "<bbbl:label key='lbl_pinterest_Share_Text_Suffix' language='${language}'/>",
                    "oldPriceTextWas": "<bbbl:label key='lbl_was_text' language='${language}'/>",
                    "intlShippingTitle": "<bbbl:label key='lbl_intl_shipping_modal_title' language='${language}'/>",
                    "deliveryText": "<bbbl:label key='ltl_delivery_label' language='${language}'/>",
                    "inviteSentTitle": "<bbbl:label key='lbl_email_invite_sent' language='${language}'/>",
                    "lblInviteFrndEmail":'<bbbl:label key="lbl_invite_friends" language="${language}"/>',
                    "lblFromText":'<bbbl:label key="lbl_cart_registry_from_text" language="${language}"/>',
                    "lblViewMore":'<bbbl:label key="lbl_restrictions_view_more" language="${language}"/>',
                    "lblViewLess":'<bbbl:label key="lbl_restrictions_view_less" language="${language}"/>',
                    "lblRegistryText":'<bbbl:label key="lbl_cart_registry_text" language="${language}"/>',
                    "lblAddedTo":'<bbbl:label key="lbl_buyoff_cart_checked" language="${language}"/>',
                    "lbl_price_is_tbd":'<bbbl:label key="lbl_price_is_tbd" language="${language}"/>',
                    "lblNotifyRegModalTitle" : '<bbbl:label key="notifyRegistrantModalTitle" language ="${pageContext.request.locale.language}"/>',
                    "lblPriceVariationMessage": '<bbbl:label key="lbl_price_variations_orig_text" language="${language}"/>',
                    "lblOrigText": '<bbbl:label key="lbl_orig_text" language="${language}"/>',
                    "lblInCartMessage": '<bbbl:label key="lbl_discounted_incart_text" language="${language}"/>',
                    "lblInCartDisclaimerMessage": '<bbbl:label key="lbl_discount_incart_orig_text" language="${language}"/>',
                    "lblSddMoreDetails" : '<bbbl:label key="lbl_sdd_more_details" language="${pageContext.request.locale.language}" />',
                    <%-- error strings below --%>
                    "QASAjaxFail": "<bbbe:error key='err_js_qas_fail' language='${language}'/>",
                    "s7ZoomErrorHTML": "<bbbe:error key='err_js_s7_zoom_error_message' language='${language}'/>",
                    "futureMonth": "<bbbe:error key='err_js_futher_month' language='${language}'/>",
                    "somethingWentWrong": "<bbbe:error key='err_something_wrong' language='${language}'/>",
                    "regSomethingWentWrong": "<bbbe:error key='err_reg_something_wrong' language='${language}'/>",
                    "ajaxURLMissing": "<bbbe:error key='err_js_ajax_url_missing' language='${language}'/>",
                    "alphaNumericName": "<bbbe:error key='err_js_alpha_numeric_name' language='${language}'/>",
                    "noWhiteSpace": "<bbbe:error key='err_js_no_white_space' language='${language}'/>",
                    "phoneNumValid": "<bbbe:error key='err_phone_invalid' language='${language}'/>",
                    "passwordEqualTo": "<bbbe:error key='err_js_password_must_match' language='${language}'/>",
                    "emailEqualTo": "<bbbe:error key='err_js_email_must_match' language='${language}'/>",
                    "numberOnly": "<bbbe:error key='err_js_number_only' language='${language}'/>",
                    "alphaOnly": "<bbbe:error key='err_js_letters_only' language='${language}'/>",
                    "requiredCommonCVV": "<bbbe:error key='err_js_required_common_cvv' language='${language}'/>",
                    "requiredCommonName": "<bbbe:error key='err_js_required_common_name' language='${language}'/>",
                    "featuresLike": "<bbbe:error key='err_js_features_like' language='${language}'/>",
                    "favoriteWeb": "<bbbe:error key='err_js_favourite_web' language='${language}'/>",
                    "textArea1500": "<bbbe:error key='err_js_text_area_1500' language='${language}'/>",
                    "selectGrreting": "<bbbe:error key='err_js_select_greeting' language='${language}'/>",
                    // "errAlphaNumeric": "<bbbe:error key='err_js_alpha_numeric' language='${language}'/>",
                    "errRequiredCommon": "<bbbe:error key='err_js_required_common' language='${language}'/>",
                    "errRequiredCommonSelect": "<bbbe:error key='err_js_required_common_select' language='${language}'/>",
                    "errCollectionItemNotSelected": "<bbbe:error key='err_js_collection_item_not_selected' language='${language}'/>",
                    "errDataIssue": "<bbbe:error key='err_js_data_issue' language='${language}'/>",
                    "errSelectFinish": "<bbbe:error key='err_js_select_finish' language='${language}'/>",
                    "errSelectColor": "<bbbe:error key='err_js_select_color' language='${language}'/>",
                    "errSelectSize": "<bbbe:error key='err_js_select_size' language='${language}'/>",
                    "errSelectQty": "<bbbe:error key='err_js_select_qty' language='${language}'/>",
                    "errSelectQtyNotNumber": "<bbbe:error key='err_js_select_qty_not_number' language='${language}'/>",
                    "errGeneric": "<bbbe:error key='err_js_generic' language='${language}'/>",
                    "errOutOfStock": "<bbbe:error key='err_js_out_of_stock' language='${language}'/>",
                    "errTitle": "<bbbe:error key='err_js_title' language='${language}'/>",
                    "errSendingInfo": "<bbbe:error key='err_js_sending_info' language='${language}'/>",
                    "errEmailCart": "<bbbe:error key='err_js_email_cart' language='${language}'/>",
                    "errFindRegMultiInput": "<bbbe:error key='err_js_find_reg_multi_input' language='${language}'/>",
                    "errFindRegMultiInput2": "<bbbe:error key='err_js_find_reg_multi_input2' language='${language}'/>",
                    "errSelectRegistry": "<bbbe:error key='err_js_select_registry' language='${language}'/>",
                    "errSubmissionUnavailable": "<bbbe:error key='err_js_submission_unavailable' language='${language}'/>",
                    "errLettersWithBasicPunc": "<bbbe:error key='err_js_select_registry' language='${language}'/>",
                    "errAlphaNumeric": "<bbbe:error key='err_js_alpha_numeric' language='${language}'/>",
                    "errAlphaNumericSpace": "<bbbe:error key='err_js_alpha_numeric_space' language='${language}'/>",
                    "errAlphaNumericBasicPunc": "<bbbe:error key='err_js_alpha_numeric_basic_punc' language='${language}'/>",
                    "errAlpha": "<bbbe:error key='err_js_alpha' language='${language}'/>",
                    "errCannotStartWithNumber": "<bbbe:error key='err_js_cannot_start_with_number' language='${language}'/>",
                    "errLettersOnly": "<bbbe:error key='err_js_letters_only' language='${language}'/>",
                    "errNoWhitespace": "<bbbe:error key='err_js_no_white_space' language='${language}'/>",
                    "errZipRange": "<bbbe:error key='err_js_zip_range' language='${language}'/>",
                    "errInteger": "<bbbe:error key='err_js_integer' language='${language}'/>",
                    "errVinUS": "<bbbe:error key='err_js_vin_us' language='${language}'/>",
                    "errDateITA": "<bbbe:error key='err_js_date_ita' language='${language}'/>",
                    "errDateNL": "<bbbe:error key='err_js_date_nl' language='${language}'/>",
                    "errTime": "<bbbe:error key='err_js_time' language='${language}'/>",
                    "errPhoneUS": "<bbbe:error key='err_js_phone_us' language='${language}'/>",
                    "errPhoneUK": "<bbbe:error key='err_js_phone_uk' language='${language}'/>",
                    "errMobileUK": "<bbbe:error key='err_js_mobile_uk' language='${language}'/>",
                    "errCreditCardTypes": "<bbbe:error key='err_js_credit_card_types' language='${language}'/>",
                    "errIPv4": "<bbbe:error key='err_js_ipv4' language='${language}'/>",
                    "errIPv6": "<bbbe:error key='err_js_ipv6' language='${language}'/>",
                    "errPasswordValid": "<bbbe:error key='err_js_password_valid' language='${language}'/>",
                    "errPhoneNumValid": "<bbbe:error key='err_js_phone_num_valid' language='${language}'/>",
                    "errEmailExtended": "<bbbe:error key='err_js_email_extended' language='${language}'/>",
                    "errEmailExtendedMultiple": "<bbbe:error key='err_js_email_extended_multiple' language='${language}'/>",
                    "errEmailExtendedMultipleComma":"<bbbe:error key='err_js_email_extended_multiple_commas' language='${language}'/>",
                    "errMultiFieldUSPhone": "<bbbe:error key='err_js_multi_field_us_phone' language='${language}'/>",
                    "errValidateCoRegistrant": "<bbbe:error key='err_js_validate_co_registrant' language='${language}'/>",
                    // "errZipUS": "<bbbe:error key='err_js_errZipUS' language='${language}'/>",
                    // "errZipCA": "<bbbe:error key='err_js_errZipCA' language='${language}'/>",
                    "errNoResultsFound": "<bbbe:error key='err_js_errNoResultsFound' language='${language}'/>",
                    "errAddressAlphaNumericBasicPunc": "<bbbe:error key='err_js_errAddressAlphaNumericBasicPunc' language='${language}'/>",
                    "errcannotStartWithSpecialChars": "<bbbe:error key='err_js_errcannotStartWithSpecialChars' language='${language}'/>",
                    "errcannotStartWithWhiteSpace": "<bbbe:error key='err_js_errcannotStartWithWhiteSpace' language='${language}'/>",
                    "errorderFormat": "<bbbe:error key='err_js_errorderFormat' language='${language}'/>",
                    "errDateFormatDDMMYYYY": "<bbbe:error key='err_js_errDateFormatDDMMYYYY' language='${language}'/>",
                    "errDateFormatMMDDYYYY": "<bbbe:error key='err_js_errDateFormatMMDDYYYY' language='${language}'/>",
                    "errAlphaBasicPunc": "<bbbe:error key='err_js_errAlphaBasicPunc' language='${language}'/>",
                    "errAlphaBasicPuncWithComma": "<bbbe:error key='err_js_errAlphaBasicPuncWithComma' language='${language}'/>",
                    "errZoomImageLoadFail": "<bbbe:error key='err_js_errZoomImageLoadFail' language='${language}'/>",
                    "errInvalidQtyNotNumber": "<bbbe:error key='err_js_errInvalidQtyNotNumber' language='${language}'/>",
                    "errNoDigits": "<bbbe:error key='err_js_errNoDigits' language='${language}'/>",
                    "errInvalidGiftMessage": "<bbbe:error key='err_js_errInvalidGiftMessage' language='${language}'/>",
                    "errHasDigits": "<bbbe:error key='err_js_errHasDigits' language='${language}'/>",
                    "errHasLowercase": "<bbbe:error key='err_js_errHasLowercase' language='${language}'/>",
                    "errHasUppercase": "<bbbe:error key='err_js_errHasUppercase' language='${language}'/>",
                    "errPOBoxAddNotAllowed": "<bbbe:error key='err_js_errPOBoxAddNotAllowed' language='${language}'/>",
                    "errEqualToIgnoreCase": "<bbbe:error key='err_js_errEqualToIgnoreCase' language='${language}'/>",
                    "errorSummaryMsg": "<bbbe:error key='err_js_errorSummaryMsg' language='${language}'/>",
                    "errNoFlashEnabled": "<bbbe:error key='err_js_errNoFlashEnabled' language='${language}'/>",
                    "errFBConnectFailed": "<bbbe:error key='err_js_errFBConnectFailed' language='${language}'/>",
                    "errFindInStorePDPCollection": "<bbbe:error key='err_js_errFindInStorePDPCollection' language='${language}'/>",
                    "errQtyExceedRegReqQty": "<bbbe:error key='err_js_valid_quantity' language='${language}'/>",
                    "errInvalidEscapeHTMLTag": "<bbbe:error key='err_js_html_tags_not_allowed' language='${language}'/>",
                    "msgTitle": "<bbbe:error key='err_js_title_warning' language='${language}'/>",
                    // "errPasswordChars": "<bbbe:error key='err_js_password_warning' language='${language}'/>",
                    "errPasswordLength": "<bbbe:error key='err_js_password_length' language='${language}'/>",
                    "errPasswordHasDigits": "<bbbe:error key='err_js_password_number' language='${language}'/>",
                    "errPasswordHasLowercase": "<bbbe:error key='err_js_password_lowercase' language='${language}'/>",
                    "errPasswordHasUppercase": "<bbbe:error key='err_js_password_capital_letter' language='${language}'/>",
                    "errPasswordNoWhitespace": "<bbbe:error key='err_js_password_no_space' language='${language}'/>",
                    "errPasswordNoFirstname": "<bbbe:error key='err_js_password_no_firstname' language='${language}'/>",
                    "errPasswordNoLastname": "<bbbe:error key='err_js_password_no_lastname' language='${language}'/>",
                    "errPasswordFirstName": "<bbbe:error key='err_js_password_firstname_error' language='${language}'/>",
                    "errPasswordLastName": "<bbbe:error key='err_js_password_lastname_error' language='${language}'/>",
                    "errGeoPermissionDenied": "<bbbe:error key='err_js_retrieve_location_denied_error' language='${language}'/>",
                    "errGeoLocationUnavailable": "<bbbe:error key='err_js_retrieve_location_unavailable_error' language='${language}'/>",
                    "errGeoTimeoutExpired": "<bbbe:error key='err_js_retrieve_location_timeout_error' language='${language}'/>",
                    "errGeoNotSupported": "<bbbe:error key='err_js_retrieve_location_not_supported_error' language='${language}'/>",
                    "errGeoAPIFailure": "<bbbe:error key='err_js_retrieve_location_not_initialized_error' language='${language}'/>",
                    "errAlphaBasicPuncWithPeriod": "<bbbe:error key='err_js_errAlphaBasicPuncWithPeriod' language='${language}'/>",
                    "errAlphaBasicPuncWithPeriodAndUnicodeChars": "<bbbe:error key='err_js_errAlphaBasicPuncWithPeriodAndUnicodeChars' language='${language}'/>",
                    "errFriendEmail": "<bbbe:error key='err_js_Friend_Email' language='${language}'/>",
                    "errFriendEmailMaxlimit": "<bbbe:error key='err_js_Friend_Email_Max_limit' language='${language}'/>",
                    "errYourEmail": "<bbbe:error key='err_js_Your_Email' language='${language}'/>",
                    "errCaptchaAnswer": "<bbbe:error key='err_js_Captcha_Answer' language='${language}'/>",
                    "errTxtEmailMessage": "<bbbe:error key='err_js_Txt_Email_Message' language='${language}'/>",
                    "errSelectSubject": "<bbbe:error key='err_js_Select_Subject' language='${language}'/>",
                    "errFirstName": "<bbbe:error key='err_js_First_Name' language='${language}'/>",
                    "errLastName": "<bbbe:error key='err_js_Last_Name' language='${language}'/>",
                    "errNameCard": "<bbbe:error key='err_js_Name_Card' language='${language}'/>",
                    "errAddress1": "<bbbe:error key='err_js_Address1' language='${language}'/>",
                    "errPassword": "<bbbe:error key='err_js_Password' language='${language}'/>",
                    "errCardType": "<bbbe:error key='err_js_Card_Type' language='${language}'/>",
                    "errRegistryAnnouncement": "<bbbe:error key='err_js_Registry_Announcement' language='${language}'/>",
                    "errCardNumber": "<bbbe:error key='err_js_Card_Number' language='${language}'/>",
                    "errRequiredCommonCVV": "<bbbe:error key='err_js_Required_Common_CVV' language='${language}'/>",
                    "errSecurityCode": "<bbbe:error key='err_js_Security_Code' language='${language}'/>",
                    "errMonth": "<bbbe:error key='err_js_Month' language='${language}'/>",
                    "errYear": "<bbbe:error key='err_js_Year' language='${language}'/>",
                    "errCity": "<bbbe:error key='err_js_City' language='${language}'/>",
                    "errState": "<bbbe:error key='err_js_State' language='${language}'/>",
                    "errStateCA": "<bbbe:error key='err_js_StateCA' language='${language}'/>",
                    "errStateText": "<bbbe:error key='err_js_State_Input' language='${language}'/>",
                    "errCountryName": "<bbbe:error key='err_js_Country' language='${language}'/>",
                    "errZip": "<bbbe:error key='err_js_Zip' language='${language}'/>",
                    "errPostalCode": "<bbbe:error key='err_js_PostalCode' language='${language}'/>",
                    "errZipCA": "<bbbe:error key='err_js_Zip_CA' language='${language}'/>",
                    "errZipUS": "<bbbe:error key='err_js_Zip_US' language='${language}'/>",
                    "errUnsubscribeMail": "<bbbe:error key='err_js_Unsubscribe_Mail' language='${language}'/>",
                    "errCurrentPassword": "<bbbe:error key='err_js_Current_Password' language='${language}'/>",
                    "errCoupons": "<bbbe:error key='err_js_Coupons' language='${language}'/>",
                    "errFullName": "<bbbe:error key='err_js_Full_Name' language='${language}'/>",
                    "errRegistryType": "<bbbe:error key='err_js_Registry_Type' language='${language}'/>",
                    "errGiftCardNumber": "<bbbe:error key='err_js_Gift_Card_Number' language='${language}'/>",
                    "errGiftCardPin": "<bbbe:error key='err_js_Gift_Card_Pin' language='${language}'/>",
                    "errRegistryNumber": "<bbbe:error key='err_js_Registry_Number' language='${language}'/>",
                    "errDirStartStreetName": "<bbbe:error key='err_js_Dir_Start_Street_Name' language='${language}'/>",
                    "errNumberOfBox": "<bbbe:error key='err_js_Number_Of_Box' language='${language}'/>",
                    "errNumBoxes": "<bbbe:error key='err_js_Num_Boxes' language='${language}'/>",
                    "errTimeZone": "<bbbe:error key='err_js_Time_Zone' language='${language}'/>",
                    "errPasswordChars": "<bbbe:error key='err_js_password_warning' language='${language}'/>",
                    "errPrefContactMethod": "<bbbe:error key='err_js_Pref_Contact_Method' language='${language}'/>",
                    "errSelectService":"<bbbe:error key='err_js_delivery_sel_warning' language='${language}'/>",
                    "errSelectServiceWishList": "<bbbe:error key='err_js_delivery_sel_warning_wish_list' language='${language}'/>",
					"errSelectServiceRegistry": "<bbbe:error key='err_js_delivery_sel_warning_registry' language='${language}'/>",
                    "cellNumValid": "<bbbl:label key='lbl_cell_Num_Valid' language='${language}'/>",
                    "errAlphaNumericBasicPuncWithPeriod": "<bbbe:error key='err_js_errAlphaNumericBasicPuncWithPeriod' language='${language}'/>",
                    "errIntlCountryDropDown": "<bbbe:error key='err_js_IntlCountryDropDown' language='${language}'/>",
                    "errIntlCurrencyDropDown": "<bbbe:error key='err_js_IntlCurrencyDropDown' language='${language}'/>",
                    "errortxtAppointmentState": "<bbbe:error key='error_appointment_dropdown' language='${language}'/>",
                    "errNoMatchesFound": "No matches found!",
                    "errGetHTML": "Please try after some time",
                    "intlContextModalBanner" : "<img width='552' height='288' data-original-url='' class='productImage noImageFound' src='${imagePath}${imageLocation}' alt='Would you like to ship internationally ?'/>",
                    "lblRegistryNoShipAvailable" : "<bbbl:label key='lbl_registry_noshipavailable_msg' language='${language}'/>",
                    "errFindStoreCurrentLocationException": "<bbbe:error key='err_js_findstore_current_location_exception' language='${language}'/>",
                    "errLoginPassword": "<bbbe:error key='err_js_LoginPassword' language='${language}'/>",
                     "erraddLookup": '<bbbl:label key="txt_walletLookup_msg" language ="${language}" />',

                    "savedPersonalizationMessage": '<bbbl:label key="lbl_cart_personalization_saved" language="${pageContext.request.locale.language}" />',
                    "savedCustomizationMessage": '<bbbl:label key="lbl_cart_customization_saved" language="${pageContext.request.locale.language}" />',
                    "customizationRequiredmessage":'<bbbl:label key="lbl_personalization_required_below" language="${pageContext.request.locale.language}" />',
                    "customizeRequiredmessage":'<bbbl:label key="lbl_customization_required_below" language="${pageContext.request.locale.language}" />',
                    "customizeRequiredMsg":'<bbbl:label key="lbl_customization_required_msg" language="${pageContext.request.locale.language}" />',
                    //****Mexico Registry Spanish Error labels****
                    "errmxFirstName": "<bbbe:error key='err_mx_js_First_Name' language='${language}'/>",
                    "errmxLastName": "<bbbe:error key='err_mx_js_Last_Name' language='${language}'/>",
                    "errmxAlphaBasicPunc": "<bbbe:error key='err_mx_js_errAlphaBasicPunc' language='${language}'/>",
                    "errmxcannotStartWithSpecialChars": "<bbbe:error key='err_mx_js_errcannotStartWithSpecialChars' language='${language}'/>",
                    "errmxNameCard": "<bbbe:error key='err_mx_js_Name_Card' language='${language}'/>",
                    "errmxRegistryNumber": "<bbbe:error key='err_mx_js_Registry_Number' language='${language}'/>",
                    "mxnumberOnly": "<bbbe:error key='err_mx_js_number_only' language='${language}'/>",
                    "errmxEmailExtended": "<bbbe:error key='err_mx_js_email_extended' language='${language}'/>",
                    "errmxYourEmail": "<bbbe:error key='err_mx_js_Your_Email' language='${language}'/>",
                    "removePersonalization": '<bbbt:textArea key="txt_remove_personalization" language ="${language}" />',
                    "addToCartCustomization": '<bbbt:textArea key="txt_cart_items_close_modal_customize" language ="${language}" />',
                    "inProgressPersonalization": '<bbbt:textArea key="txt_inProgress_Personalization" language ="${language}" />',
                    "inProgressCustomization": '<bbbt:textArea key="txt_inProgress_customization" language ="${language}" />',
                    "lblPBFee" : '<bbbl:label key="lbl_PB_Fee_detail" language="${pageContext.request.locale.language}" />',
                    "lblPYFee" : '<bbbl:label key="lbl_PY_Fee_detail" language="${pageContext.request.locale.language}" />',
                    "lblCRFee" : '<bbbl:label key="lbl_CR_Fee_detail" language="${pageContext.request.locale.language}" />',
                    "maxlengthcvv" : '<bbbl:label key="lbl_cvv_max_len_validation" language="${pageContext.request.locale.language}" />',
                    "minlengthcvv" : '<bbbl:label key="lbl_cvv_min_len_validation" language="${pageContext.request.locale.language}" />',
                    "lblPriceChange" : '<bbbl:label key="lbl_price_change" language="${pageContext.request.locale.language}" />',
                    "lblAddPriceText" : '<bbbt:textArea key="txt_pdp_katori_price" language ="${pageContext.request.locale.language}"/>',
                    "lblCVVMaxLenValidation" : '<bbbl:label key="lbl_cvv_max_len_validation" language ="${pageContext.request.locale.language}"/>',
                    "lblCVVMinLenValidation" : '<bbbl:label key="lbl_cvv_min_len_validation" language ="${pageContext.request.locale.language}"/>',
                    "lblCVVAmexLenValidation" : '<bbbl:label key="lbl_cvv_amex_len_validation" language ="${pageContext.request.locale.language}"/>',
                    "lblDslModalHead" : '<bbbl:label key="lbl_dsl_alt_modal_head" language="${pageContext.request.locale.language}" />',
                    "lblDslUpdatedSuccessfully" : '<bbbl:label key="lbl_dsl_updated_successfully" language="${pageContext.request.locale.language}" />',
                    "txtDslNotAvailable" : '<bbbt:textArea key="txt_dsl_not_available" language ="${pageContext.request.locale.language}"/>',
                    "lblDslModalHeadCart" : '<bbbl:label key="lbl_dsl_alt_modal_head_cart" language ="${pageContext.request.locale.language}"/>',
                    "lblDslModalHeadSFL" : 'Please enter information below to add this item to your wishlist',
                    "lblBackToListLink" : '<bbbl:label key="lbl_findstore_back_list" language="${pageContext.request.locale.language}" />',
                    "lblNearByStoreHeading" : '<bbbl:label key="lbl_findyourcollegefrag_near_store" language="${pageContext.request.locale.language}"/>',
					"lblCRAddPriceText" : '<bbbt:textArea key="txt_pdp_cr_katori_price" language ="${pageContext.request.locale.language}"/>',
					"lblCRAddPriceCustomizeText" : '<bbbt:textArea key="txt_pdp_cr_customize_katori_price" language ="${pageContext.request.locale.language}"/>',
					"errRegRequired": "<bbbe:error key='err_reg_required' language='${language}'/>",
					"errAddCartLimit": "<bbbe:error key='err_add_to_cart_max_limit' language='${language}'/>",
					"errSavedItemsLimit": "<bbbe:error key='err_saved_items_max_limit' language='${language}'/>",
                    "errRegEmailRequired": "<bbbe:error key='err_reg_email_required' language='${language}'/>",
					"lblforgotPassword":"<bbbl:label key='lbl_forgot_password_heading' language='${language}'/>",
                    "lblforgotPasswordFromRegistry": "<bbbl:label key='lblforgotPasswordFromRegistry' language='${language}'/>",
                    "facetedAccordionExpand": '<bbbl:label key="lbl_search_left_nav_accordion_expand" language ="${pageContext.request.locale.language}" />',
                    "facetedAccordionCollapse": '<bbbl:label key="lbl_search_left_nav_accordion_collapse" language ="${pageContext.request.locale.language}" />',
                    "lblRefineSearch": '<bbbl:label key="lbl_refine_search" language ="${pageContext.request.locale.language}" />',
                    "lblsearchForMoreListing": '<bbbl:label key="lbl_search_for_more_listing" language ="${pageContext.request.locale.language}" />',
                    "errNumOfGuests":"<bbbe:error key='err_js_num_greater_than_zero' language='${language}'/>",
                    "manageRegDropdown": "<bbbl:label key='lbl_manage_registry_dropdown' language ='${pageContext.request.locale.language}' />",
                    "allRegDropdown": "<bbbl:label key='lbl_all_registries_dropdown' language ='${pageContext.request.locale.language}' />",
                    "expandChecklist": "<bbbl:label key='lbl_click_expand_checklist' language ='${pageContext.request.locale.language}' />",
                    "lblUpdateContactAddress":'<bbbl:label key="lblUpdateContactAddress" language ="${pageContext.request.locale.language}" />', 
                    "lblUpdateShippingAddress":'<bbbl:label key="lblUpdateShippingAddress" language ="${pageContext.request.locale.language}" />',
                    "lblUpdateFutureAddress":'<bbbl:label key="lblUpdateFutureAddress" language ="${pageContext.request.locale.language}" />',
                    "collapseChecklist": "<bbbl:label key='lbl_click_collapse_checklist' language ='${pageContext.request.locale.language}' />",
                    "subcategoryDropdown": "<bbbl:label key='lbl_subcategory_dropdown' language ='${pageContext.request.locale.language}' />",
                    "uncheckMark": "<bbbl:label key='lbl_check_To_Remove' language ='${pageContext.request.locale.language}' />",
                    "checkMark": "<bbbl:label key='lbl_check_To_Add' language ='${pageContext.request.locale.language}' />",
                    "markCompleted": "<bbbl:label key='lbl_items_Marked_Complete' language ='${pageContext.request.locale.language}' />",
                    "markInComplete": "<bbbl:label key='lbl_mark_incomplete' language ='${pageContext.request.locale.language}' />",
                    "itemsRecommended": "<bbbl:label key='lbl_items_Recommended_To_Add' language ='${pageContext.request.locale.language}' />",
                    "clickToViewItems": "<bbbl:label key='lbl_view_Your_Items' language ='${pageContext.request.locale.language}' />",
                    "subcatCompleted":"<bbbl:label key='lbl_subcategory_completed' language ='${pageContext.request.locale.language}' />",
                    "notStarted":"<bbbl:label key='lbl_not_started' language ='${pageContext.request.locale.language}' />",
                    "progressUnavailable":"<bbbl:label key='lbl_progress_unavailable_message' language ='${pageContext.request.locale.language}' />",
                   
                    "checklistLoading":"<bbbl:label key='lbl_checklist_loading' language ='${pageContext.request.locale.language}' />",
                    "clickOrTapAxMsg": "<bbbl:label key='lbl_click_tap_open' language='${language}'/>",
                    "regDropdownAxMsg": "<bbbl:label key='lbl_reg_dropdown' language='${language}'/>",
                    "isRegistryAxMsg":"<bbbl:label key='lbl_checklist_reg_text' language='${language}'/>",
                    "currentlySelectedAxMsg": "<bbbl:label key='lbl_currently_selected' language='${language}'/>",
                    "lblSameDayDeliveryHeading": "<bbbl:label key='lbl_same_day_delivery_heading' language='${language}'/>",
                    "lblSameDayDeliveryCongratualations": "<bbbl:label key='lbl_sdd_congratulations' language='${language}'/>",
                    "lblCoRegistrantEmailValidation": "<bbbl:label key='lbl_coregistrantEmail_validationMessage' language ='${pageContext.request.locale.language}' />",
                    "lblContactAddressUpdate": "<bbbl:label key='lbl_contact_address_update' language ='${pageContext.request.locale.language}' />",
                    "lblRegShippingAddressUpdate": "<bbbl:label key='lbl_reg_shipping_address_update' language ='${pageContext.request.locale.language}' />",
					"quebecRestrictedMsg": "<bbbl:label key='lbl_quebecRestrictedMsg' language='${language}'/>",
					"lblFutureAddressUpdate": "<bbbl:label key='lbl_future_address_update' language ='${pageContext.request.locale.language}' />",
                    "lblAddAddress":"<bbbl:label key='lbl_add_address' language ='${pageContext.request.locale.language}'/>",
					"lblEditAddress":"<bbbl:label key='lbl_edit_address' language ='${pageContext.request.locale.language}'/>",
					"placeOrderIntervalError": '<bbbt:textArea key="placeOrderIntervalError" language ="${language}" />',
					"placeOrderIntervalErrorVbvDesktop": '<bbbt:textArea key="placeOrderIntervalErrorVbvDesktop" language ="${language}" />',
					"lblFindAStoreErrorMessage":"<bbbl:label key='lbl_Find_in_store_error_message' language ='${pageContext.request.locale.language}'/>",
					"errFindInAStoreMessage": "<bbbe:error key='ERROR_IN_VIEW_ALL_STORES' language='${language}'/>",
					"errselfinishsize": "<bbbe:error key='err_sel_finish_size' language='${language}'/>",
					"errselcolorsize": "<bbbe:error key='err_sel_color_size' language='${language}'/>",
					"errenterqtysize": "<bbbe:error key='err_enter_qty_size' language='${language}'/>",
					"errenterqtyfinishsize": "<bbbe:error key='err_enter_qty_finish_size' language='${language}'/>",
					"errenterqtyfinish": "<bbbe:error key='err_enter_qty_finish' language='${language}'/>",
					"errenterqtycolor": "<bbbe:error key='err_enter_qty_color' language='${language}'/>",
                    "errenterqtycolsize": "<bbbe:error key='err_enter_qty_col_size' language='${language}'/>",
                    "lbl_local_store_category_header_conjunction": "<bbbl:label key='lbl_local_store_category_header_conjunction' language='${language}'/>",
                    "checklistsystemerrorprogress":"<bbbl:label key='checklist_system_error_progress' language ='${pageContext.request.locale.language}'/>",
                    "lblFutureAddr" : "<bbbl:label key='lbl_reg_ph_futureaddress' language ='${pageContext.request.locale.language}'/>",
                    "lblShippingAddr" : "<bbbl:label key='lbl_reg_ph_shippingAddress' language ='${pageContext.request.locale.language}'/>",
                    "lblContactAddr" : "<bbbl:label key='lbl_reg_ph_contactAdress' language ='${pageContext.request.locale.language}'/>",
                    "itemAddedAtrModal":"<bbbl:label key='lbl_item_added_atr_modal' language='${pageContext.request.locale.language}' />"
                    


               	};

                BBB.config = BBB.config || {};
                BBB.config.envir = 'dev'; // string: 'ue' || 'dev'
                BBB.config.ajaxTimeOut = ${ajaxTimeOut};
                BBB.config.maxCharLimitTypeAhead = ${maxCharLimitTypeAhead};
                BBB.config.keys = {
                    "eximClientId": "<bbbc:config key='X-ClientId' configName='EXIMKeys' />",
                    "eximApikey": "<bbbc:config key='X-ApiKey' configName='EXIMKeys' />",
                    "eximApiVersion": "<bbbc:config key='X-ApiVersion' configName='EXIMKeys' />",
                    "eximCssURL": "<bbbc:config key='X-CssURL' configName='EXIMKeys' />",
                    "customizeCTACodes": "<bbbc:config key='CustomizeCTACodes' configName='EXIMKeys' />",
                    "googleAddressSwitch": "<bbbc:config key='google_address_switch' configName='FlagDrivenFunctions' />",
                    "addressCountryCode":
                    <c:choose>  <%-- ### BBB-22 ### --%>
                    	<c:when test = "${fn:contains(currentSiteId,'Canada') }">
                    	"<bbbc:config key='address_country_code' configName='ContentCatalogKeys' />"
                    	</c:when>
                    	<c:otherwise>
                    	["<bbbc:config key='address_country_code' configName='ContentCatalogKeys' />","<bbbc:config key='addressCountryCodeSecondary' configName='ContentCatalogKeys' />"]
                    	</c:otherwise>
                    </c:choose>,
                    "primingKeyActive" : "<bbbc:config key='RegItemsWSCall' configName='FlagDrivenFunctions' />",
                    "adBlockerCookieDays" : "<bbbc:config key='AdBlockerCookieDays' configName='FlagDrivenFunctions' />",
                    "notifyRegistrantFlag" : "<bbbc:config key='notifyRegistrantFlag' configName='FlagDrivenFunctions' />",
                    "sameDayDeliveryFlag" : "<bbbc:config key='SameDayDeliveryFlag' configName='SameDayDeliveryKeys' />",
                    "searchUrlParamName" : "<bbbc:config key='swdEPHSearchUrlParam' configName='OmnitureBoosting' />",
                    "enableLTLRegForSite" : "<bbbc:config key='enableLTLRegForSite' configName='FlagDrivenFunctions' />",
                    "psFlyout_flag": "<bbbc:config key='PersonalStoreFlyout_flag' configName='FlagDrivenFunctions' />",
		    		"placeOrderWaitingTime": "<bbbc:config key='placeOrderWaitingTime' configName='CartAndCheckoutKeys' />",
                    "placeOrderIntervalKey": "<bbbc:config key='placeOrderIntervalKey' configName='FlagDrivenFunctions' />",
                    "porchPartnerName" : "<bbbc:config key='partnerName' configName='PorchServiceKeys' />",
          			"ExtendAccount_SimplifyRegistry": "<bbbc:config key='ExtendAccount_SimplifyRegistry' configName='FlagDrivenFunctions' />",
                    "ExtendAccount_SPCPage": "<bbbc:config key='ExtendAccount_SPCPage' configName='FlagDrivenFunctions' />",
                    "ajaxTimeOut": "<bbbc:config key='AjaxTimeOut' configName='ContentCatalogKeys' />",
                    "checklistAjaxTimeOut": "<bbbc:config key='ChecklistAjaxTimeOut' configName='ContentCatalogKeys' />",
                    "ExtendAccount_LoginPage": "<bbbc:config key='ExtendAccount_LoginPage' configName='FlagDrivenFunctions' />",
                    "ExtendAccount_MPCPage": "<bbbc:config key='ExtendAccount_MPCPage' configName='FlagDrivenFunctions' />",
                    "ExtendAccount_PDP": "<bbbc:config key='ExtendAccount_PDP' configName='FlagDrivenFunctions' />",
                    "updateOrderSummaryAjax": "<bbbc:config key='updateOrderSummaryAjax' configName='FlagDrivenFunctions' />",
                    "ExtendAccount_Cart": "<bbbc:config key='ExtendAccount_Cart' configName='FlagDrivenFunctions' />",
		    		"enableATCModal": "<bbbc:config key='enableATCModal' configName='FlagDrivenFunctions' />",
                    "enableATRModal":"<bbbc:config key='enableATRModal' configName='FlagDrivenFunctions' />",
					"collegeMaxLength": "<bbbc:config key='collegeName_maxLength' configName='ContentCatalogKeys' />"
                };
                BBB.config.url = {
                    defaultPath: {
                        dev: ''
                    },
                    '404': {
                        dev: '${contextPath}/404.jsp'
                    },
                    '409': {
                        dev: '${contextPath}/409.jsp'
                    },
                    '500': {
                        dev: '${contextPath}/global/sessionExpired.jsp'
                    },
                    '503': {
                        dev: '${contextPath}/global/sessionExpired.jsp'
                    },
                    'facebookPage': {
                        dev: '//www.facebook.com/BedBathandBeyond'
                    },
                    'checkBalance': {
                        dev: '${contextPath}/checkout/billing_payment.jsp'
                    },
                    'forgotPassword': {
                        dev: '/_ajax/forgot_password.jsp'
                    },
                    'forgotPasswordSuccess': {
                        dev: '/_includes/modals/forgot_password_registry_success.jsp'
                    },
                    'updateMiniCart': {
                        dev: '${contextPath}/cart/mini_cart.jsp'
                    },
                    'updateMiniCartItemCount': {
                        dev: '${contextPath}/cart/cart_item_count.jsp'
                    },
                    'getSKU': {
                        dev: '${contextPath}/browse/SKU_rollup_details.jsp'
                    },
                    'getProductData': {
                        dev: '${contextPath}/browse/rollup_details.jsp'
                    },
                    'addItemToRegisteryStatus': {
                        dev: '${contextPath}/addgiftregistry/addItemToRegisteryStatus.jsp'
                    },
                    'openCreateRegistry': {
                        dev: '${contextPath}/addgiftregistry/create_registry_popup.jsp'
                    },
                    'anncCardRequest': {
                        dev: '/_includes/modals/annc_card_request.jsp'
                    },
                    'productDetailsEmailAFriend': {
                        dev: '${contextPath}/browse/emailAFriend.jsp'
                    },
                    'productDetailsNotifyOOS': {
                        dev: '${contextPath}/browse/gadgets/notifyMeRequest.jsp'
                    },
                    'announcementCards': {
                        dev: '${contextPath}/bbregistry/announcement_cards.jsp'
                    },
                    'emailAFriend': {
                        dev: '${contextPath}/_includes/modules/email_friend.jsp'
                    },
                    'mxEmailAFriend': {
                        dev: '${contextPath}/mx/_includes/modules/email_friend.jsp'
                    },
                    'chkGiftCardBal': {
                        dev: '${contextPath}/account/check_giftcard_balance.jsp'
                    },
                    'addToRegNoLogin': {
                        dev: '${contextPath}/addgiftregistry/add_item_to_reg_no_login.jsp'
                    },
                    'clearSessionBean': {
                        dev: '${contextPath}/giftregistry/clear_add_item_to_regb.jsp'
                    },
                    'openEmailCart': {
                        dev: '${contextPath}/cart/email/email_cart.jsp'
                    },
                    'openEmailSavedItems': {
                        dev: '${contextPath}/cart/email/email_saveditems.jsp'
                    },
                    'katoriprice': {
                        dev: '${contextPath}/browse/katori_price.jsp'
                    },
                    'addGuide':{
                    	 dev: '${contextPath}/_ajax/addNonRegistryGuide.jsp'
                    },
                    'hideGuide':{
                   	 dev: '${contextPath}/_ajax/hideNonRegistryGuideSuccessJson.jsp'
                   	},
					'openIntlShippingModal':{
                        dev: '${contextPath}/_includes/modals/intlCountryCurrecyModal.jsp'
					},
                    'openIntlShippingModalCart':{
                        dev: '${contextPath}/_includes/modals/intlCountryCurrecyModalCart.jsp'
                    },
                    'getDirectionsWithMap': {
                        dev: '${contextPath}/selfservice/store/p2p_directions.jsp'
                    },
                    'legacyPwdPopup': {
                        dev: '${contextPath}/account/incorrect_password.jsp'
                    },
                    'legacyPwdCheck': {
                        dev: '/_ajax/legacy_password_check.jsp'
                    },
                    'findCollegeSelect': {
                        dev: '${contextPath}/selfservice/frags/find_your_college_json.jsp'
                    },
                    'privacyPolicy': {
                        dev: '${contextPath}/account/privacy_policy.jsp'
                    },
                    'getStoreData': {
                        dev: '${contextPath}/selfservice/find_in_store_result.jsp'
                    },
                    'captchaImagePath': {
                        dev: '${contextPath}/simpleCaptcha.png'
                    },
                    'getStoreDataFavorite': {
                        dev: '${contextPath}/selfservice/store/store_info_frag.jsp'
                    },
                    'pathToZeroClipboard': {
                        dev: '${imagePath}/_assets/global/flash_assets/ZeroClipboard10.swf'
                    },
                    'BVDoLogin': {
                        dev: '${contextPath}/account/login.jsp'
                    },
                    'addItemToCart': {
                        dev: '${contextPath}/browse/add_item_to_cart_pdp.jsp'
                    },
                    'addItemToListStatus': {
                        dev: '${contextPath}/browse/add_item_to_list_pdp.jsp'
                    },
                    'addItemToListStatusPostLogin': {
                        dev: '${contextPath}/browse/add_item_to_list_pdp_post_login.jsp'
                    },
                    'mapQuestStaticMapContentURL': {
                        dev: '${mapQuestStaticMapContentURL}'
                    },
                    'preloaderImg': {
                        dev: '${imagePath}/_assets/global/images/widgets/loader.gif'
                    },
                    'bridalShowData': {
                        dev: '${contextPath}/cms/bridal_template.jsp'
                    },
                    'collegeEventsData': {
                        dev: '${contextPath}/cms/college_events_template.jsp'
                    },
                    'fConnectImage': {
                        dev: '${imagePath}/_assets/global/images/f_connect.png'
                    },
                    'fConnectImageNotAvailable': {
                        dev: '${imagePath}/_assets/global/images/f_connect_na.png'
                    },
                    'validateFConnect': {
                        dev: '${contextPath}/account/facebook_connect.jsp'
                    },
                    'fbLoaderImg': {
                        dev: '${imagePath}/_assets/global/images/fb-loader.gif'
                    },
                    'addItemToCartFromReg': {
                        dev: '${contextPath}/browse/add_item_to_cart_gift.jsp'
                    },
                    'emailAPageCMS': {
                        dev: '${contextPath}/emailtemplates/emailapage/emailAPage_cms.jsp'
                    },
                    'mxEmailAPageCMS': {
                        dev: '${contextPath}/emailtemplates/emailapage/emailAPage_cms.jsp'
                    },
                    'getStoreDataFavoriteRegistry': {
                        dev: '${contextPath}/giftregistry/modals/store_info_no_stock_info.jsp'
                    },
                    'blankGIF': {
                        dev: '${imagePath}/_assets/global/images/blank.gif'
                    },
                    'getStates': {
                        dev: '${contextPath}/checkout/billing/states.jsp'
                    },
                    'noImageFound': {
                        dev: '${imagePath}/_assets/global/images/no_image_available.jpg'
                    },
                    'migratedAccount': {
                        dev: '${contextPath}/account/frags/changepasswordmodel.jsp'
                    },
                    'migratedAccountPwdCheck': {
                        dev: '${contextPath}/account/frags/changepasswordmodel.jsp'
                    },
                    'CheckListData': {
                        dev: '${contextPath}/_includes/header/elements/manualCheckC3RegistryChecklist.jsp'
                    },
                    'validateResetPasswordFields': {
                        dev: '${contextPath}/account/frags/validate_fields_on_reset_password.jsp'
                    },
                    'opencCLaunchPage': {
                        dev: '${contextPath}/checkout/preview/cCLaunch.jsp'
                    },
                    'bbbLogoDialog': {
                        <c:set var="cursiteimg" value="bbb" />
                        <c:set var="countrySuffix" value="" />
                        <c:choose>
                            <c:when test="${currentSiteId eq BuyBuyBabySite}">
                                <c:set var="cursiteimg" value="bb" />
                            </c:when>
                            <c:when test="${currentSiteId eq BedBathCanadaSite}">
                                <c:set var="cursiteimg" value="bbb" />
                                <c:set var="countrySuffix" value="_ca" />
                            </c:when>
                        </c:choose>
                        dev: '${imagePath}/_assets/global/images/logo/logo_${cursiteimg}_wide${countrySuffix}.png'
                    },
                    's7URL': {
                        dev: '${scene7Path}/'
                    },
                    's7MainURL': {
                        dev: '${scene7AltPath}'
                    },
                    'scene7DomainPath': {
                        dev: '${scene7DomainPath}'
                    },
                    'profileURL': {
                        dev: '/store/giftregistry/droplet/profile_exist_droplet.jsp'
                    },
                    'wishlistMoveToCartError': {
                        dev: '${contextPath}/wishlist/wishlist_move_to_cart_error.jsp'
                    },
                    'liveClickerBridalVideos': {
                        <c:set var="liveClickerUrl" scope="request">
                           <bbbc:config key="liveclicker_guides_advice" configName="ThirdPartyURLs" />
                        </c:set>
                        dev: '${liveClickerUrl}'
                    },
                    'mobileUrl': {
                        <c:set var="varMobileUrl" scope="request">
                           <bbbc:config key="mobileRedirectHosts" configName="MobileWebConfig" />
                        </c:set>
                        dev: '${varMobileUrl}'
                    },

                    'smallLoader': {
                        dev: '${imagePath}/_assets/global/images/widgets/small_loader.gif'
                    },
                    'smallLoader2': {
                        <c:set var="loaderIMGSuffix" value="_by" />
                        <c:if test="${currentSiteId eq BuyBuyBabySite}">
                            <c:set var="loaderIMGSuffix" value="_bb" />
                        </c:if>
                        dev: '${imagePath}/_assets/global/images/widgets/small_loader${loaderIMGSuffix}.gif'
                    },
                    'regFlyout': {
                        dev: '${contextPath}/giftregistry/reg_flyout.jsp'
                    },
                    'hideBopusSearchForm': {
                        dev: '${contextPath}/selfservice/store/statusShowBopusForm.jsp'
                    },
                    'getFavStoreData': {
                        dev: '${contextPath}/giftregistry/registry_fav_store.jsp'
                    },
                    'regHeader': {
                        dev: '${contextPath}/_includes/header/subheader.jsp'
                    },
                    'persistentRegHeader': {
                        dev: '${contextPath}/_includes/header/persistent_subheader.jsp'
                    },
                    'genericRegHeader': {
                        dev: '${contextPath}/_includes/header/generic_subheader.jsp'
                    },
                    'hhPrefCenterURL': {
                        dev: '${harteAndHanksURL}'
                    },
                    'mapQuestLibURL': {
                        dev: '<c:choose><c:when test="${pageSecured}"><bbbc:config key="js_mapQuest" configName="ThirdPartyURLs" /></c:when><c:otherwise><bbbc:config key="js_mapQuest" configName="ThirdPartyURLs" /></c:otherwise></c:choose>'
                    },
                    'shippingRestrictionPopUp': {
                        dev: '${contextPath}/_includes/modals/shipping_restrictions_checkout.jsp'
                    },
                    'cartAjaxSubmitURL': {
                        dev: '${contextPath}/cart/ajax_handler_cart.jsp'
                    },
                    'sflAjaxSubmitURL': {
                        dev: '${contextPath}/_includes/modules/cart_store_pickup_form.jsp'
                    },
                    'shareWithFacebook': {
                        dev: '//www.facebook.com/share.php?'
                    },
                    'shareWithTwitter': {
                        dev: '//twitter.com/intent/tweet?'
                    },
                    'shareWithPinterest': {
                        dev: '//pinterest.com/pin/create/button/?'
                    },
                    'bitly': {
                        dev: '//api.bitly.com/v3/shorten?callback=?'
                    },
                    'bitlyAPIKey': {
                        dev: 'R_3628dc7572a56ea3d97f638ff1b45246'
                    },
                    'bitlyLoginID': {
                        dev: 'o_4i5tdj1q5g'
                    },
                    'cookieSecureSIDAjaxed': {
                        dev: '${contextPath}/account/cookieSecureSIDAjaxed.jsp'
                    },
                    'fromResetURL': {
                        dev: '${contextPath}/account/frags/changePwdAfterResetmodel.jsp'
                    },
                    'migratedUserPopupURL': {
                        dev: '${contextPath}/account/frags/migrated_user_popup.jsp'
                    },
                    'siteTourJSP': {
                        dev: '${contextPath}/_includes/modals/siteTour.jsp'
                    },
                    'getMultiSKUCollection': {
                        dev: '${contextPath}/browse/product_details_collection.jsp'
                    },
		    'addProductToCompareBox': {
		    	dev: '${contextPath}/compare/addProduct_to_drawer.jsp'
		    },
		    'removeProductFromCompareBox': {
		    	dev: '${contextPath}/compare/removeProduct_from_drawer.jsp'
		    },
		    'removeAllProductsFromCompareBox': {
		    	dev: '${contextPath}/compare/remove_all_Product.jsp'
		    },
		    'getCompareAlert': {
			dev: '${contextPath}/_includes/modules/compare_box_alert.jsp'
		    },
                    'invitationSentPopUp': {
                        dev: '${contextPath}/_includes/modals/inviteFriendsRegistry.jsp'
                    },
		    'removeBucket': {
		    	dev: '${contextPath}/_ajax/remove_from_bucket.jsp'
		    },
		    'addCompareItemToRegistryStatus': {
		    	dev: '${contextPath}/addgiftregistry/addComparisonItemToRegistryStatus.jsp'
		    },
		    'getMultiSKUView': {
	                dev: '${contextPath}/compare/compare_list_view.jsp'
                    },
		    'validateAddressURL': {
		    	dev: '${contextPath}/paypal/validate.jsp'
		    },
		    'localStorePDP': {
                dev: '${contextPath}/browse/local_store_pdp.jsp'
            },
            'ajaxifyFilters':{
                dev: '${scheme}://${serverName}'
            },
			'localStorePLP': {
                dev: '${contextPath}/browse/local_store_plp.jsp'
            },
            'narrowSearch': {
                dev: '${scheme}://${serverName}'
            },
			'inStoreCountForPLP': {
                dev: '${contextPath}/browse/instore_count_through_ajax.jsp'
            },
                    'compareDrawer': {
                       dev: '${contextPath}/browse/compare_drawer.jsp'
                    },
					'getCompareSameSkuAlert': {
                        dev: '${contextPath}/_includes/modules/compare_sameSku_alert.jsp'
                    },
                    'topNavHeader': {
                        dev: '${contextPath}/includes/dynamic_header.jsp'
                    },
                    'getRegistryFlyout': {
                        dev: '${contextPath}/giftregistry/reg_flyout.jsp'
                    },
                    'getCollegeFlyout': {
                        dev: '${contextPath}/bbcollege/shop_for_college.jsp'
                    },
                    'getThirdParty': {
                        dev: '${contextPath}/includes/dynamic_third_parties.jsp'
                    },
                    'recommendPopUp': {
                        dev: '${contextPath}/_includes/modals/recommendPopUp.jsp'
                    },
		    'recognizedUserLogin': {
                        dev: '${contextPath}/account/recognizedUserLogin.jsp'
                    },
                    'category1RegCheckList': {
                        dev: '${contextPath}/_includes/header/elements/registryOverlay.jsp'
                    },
                    'getIntlCust': {
                        dev: '<bbbl:label key="lbl_intl_cust_restriction_popup" language="${language}"/>'
                    },
                    'getIntlHeroRotator': {
                        dev: '${contextPath}/browse/hero_rotator_intl.jsp'
                    },
                    'getScheAppointmentResultStoreData': {
                        dev: '${contextPath}/scheduleappointment/find_in_store_result_appointment.jsp'
                    },
                    'getScheAppointmentStoreData': {
                        dev: '${contextPath}/scheduleappointment/find_in_store_appointment.jsp'
                    },
                    'pendingRecommendation': {
                        dev: '${contextPath}/giftregistry/frags/pending_recommendation_tab.jsp'
                    },
                    'acceptedRecommendation': {
                        dev: '${contextPath}/giftregistry/frags/accepted_recommendation_tab.jsp'
                    },
                    'declinedRecommendation': {
                        dev: '${contextPath}/giftregistry/frags/declined_recommendation_tab.jsp'
                    },
                    'getScheAppointmentStoreBtn': {
                    	dev: '${contextPath}/scheduleappointment/scheduleAppointment.jsp'
                  	},
                    'recommendersRecommendation': {
                        dev: '${contextPath}/giftregistry/frags/recommenders_recommendation_tab.jsp'
                    },
                    'buyOffAssociation': {
                        dev: '${contextPath}/cart/associationBuyOff.jsp'
                    },
                    'savePersonalizationModal': {
                        dev: '${contextPath}/_includes/modals/savePersonalizationModal.jsp'
                    },
                    'spcSitespectTest': {
                        dev: '${contextPath}/checkout/singlePage/ajax/sitespectTest.jsp'
                    },
                   	'primeRegistry': {
                   	    dev: '${contextPath}/browse/prime_gift_registry_data.jsp'
                    },
                    'dslAlternateModal': {
                        dev: '${contextPath}/_includes/modals/dsl_alternate_modal.jsp'
                    },
					'challengeQuestionModal': {
                    	dev: '${contextPath}/account/challengeQuestionModal.jsp'
                    },
					'poBoxModal': {
						dev: '${contextPath}/_includes/modals/poBoxModal.jsp'
					},
                    'notifyRegModal': {
                        dev: '${contextPath}/_includes/modals/notifyRegistrant.jsp'
                    },
                    'notifyRegStatus' : {
                        dev :'${contextPath}/addgiftregistry/notifyRegistrantStatus.jsp'
                    },
                    'findStoreCollege' : {
                    	dev :'${contextPath}/selfservice/store/find_store_pdp.jsp'
                    },
                    'loadEDWData' : {
                    	dev :'${contextPath}/edw/edw_data.jsp'
                    },
					'confirmZipCodePopup' : {
                    	dev :'${contextPath}/_includes/modals/sameDayDeliveryPDPConfirmZipTooltip.jsp'
                    },
					'confirmZipCode' : {
                    	dev :'${contextPath}/browse/sdd_ajax_local_store_pdp.jsp'
                    },
                    'regAjaxLoadAllCategory' : {
                    	dev :'${contextPath}/giftregistry/frags/regAjaxLoadAllCategoriesJson.jsp'
                    },
                    'registryQVModal' : {
                    	dev :'${contextPath}/giftregistry/rlv_quick_view.jsp'
                    },
                    'registryOwnerQVModal' : {
                    	dev :'${contextPath}/giftregistry/registry_owner_quick_view.jsp'
                    },
                    'registryGuestQVModal' : {
                    	dev :'${contextPath}/giftregistry/registry_guest_quick_view.jsp'
                    },
                     'refreshCopyRegistry' : {
                    	dev :'${contextPath}/giftregistry/refreshCopyReg.jsp'
                    },
                    'checklistPrintViewStyleURL' : {
                    	dev :'/_assets/global/css/main.css'
                    },
                    'createSimpleRegistry' : {
                    	dev :'${contextPath}/giftregistry/simpleReg_creation_form.jsp'
                    },
                    'checkAdSenseJs' : {
                    	dev : '${jsPath}/_assets/global/js/extensions/bbb_adsense.js'
                    },
                    'openATCModal':{
                        dev:'${contextPath}/browse/openATCModal.jsp'
                    },
                    'openATRModal':{
                        dev:'${contextPath}/browse/openATRModal.jsp'
                    },
                    'porchScript' : {
                        dev : '<bbbc:config key="serviceWidgetJSURL" configName="PorchServiceKeys" />'
                        
                    },
                    'porchServiceRestrictionPopUp': {
                        dev: '${contextPath}/_includes/modals/porch_service_restrictions_checkout.jsp'
                    }
                };
          

            <%-- ### CM-5508 ### --%>
            

     <%--    </dsp:oparam>
    </dsp:droplet> --%>
</dsp:page>
