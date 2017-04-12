<dsp:page>
    <c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
    <%-- <c:set var="jsScriptCacheTimeout">
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

    <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>

    <%-- <dsp:droplet name="/atg/dynamo/droplet/Cache">
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
           
               <%-- Bed Bath & Beyond namespace as a global variable --%>
                var BBB = BBB || {};
                BBB.katoriCodes = ${jsonObject};
                
                BBB.customVendors  = ${vendorJsonString};
                
				BBB.ltlDeliveryMetohds = {
					'LT': 'Threshold',
					'LR': 'Room of Choice',
					'LC': 'Curbside',
					'LW': 'White Glove',
					'LWA': 'White Glove with Assembly'
				};

                /**
                * Extending BBB Object with all the String messages which will be used to show messages through JS.
                */
                BBB.glblStringMsgs = {
                    "selected": "<bbbl:label key='lbl_coupon_selected_text' language='${language}'/>",
                    "applied": "<bbbl:label key='lbl_coupon_applied_text' language='${language}'/>",
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
                    "s7messageZoomDisabled" : "Click to view large image",
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
                    "oldPriceTextWas": "<bbbl:label key='lbl_old_price_text' language='${language}'/>",
					"intlShippingTitle": "<bbbl:label key='lbl_intl_shipping_modal_title' language='${language}'/>",
					"deliveryText": "<bbbl:label key='ltl_delivery_label' language='${language}'/>",
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
                    "cellNumValid": "<bbbl:label key='lbl_cell_Num_Valid' language='${language}'/>",
                    "errAlphaNumericBasicPuncWithPeriod": "<bbbe:error key='err_js_errAlphaNumericBasicPuncWithPeriod' language='${language}'/>",
					"errIntlCountryDropDown": "<bbbe:error key='err_js_IntlCountryDropDown' language='${language}'/>",
					"errIntlCurrencyDropDown": "<bbbe:error key='err_js_IntlCurrencyDropDown' language='${language}'/>",
                    "errNoMatchesFound": "No matches found!",
                    "errSelectServiceRegistry": "Please select a service level",
                    "errRegRequired": "<bbbe:error key='err_reg_required' language='${language}'/>",
                    "errRegEmailRequired": "<bbbe:error key='err_reg_email_required' language='${language}'/>",
                    "savedCustomizationMessage": '<bbbl:label key="lbl_cart_customization_saved" language="${pageContext.request.locale.language}" />',
                    "errCardSecondLastAttempt": "<bbbe:error key='err_card_second_last_attempt' language='${language}'/>",
                     "savedPersonalizationMessage": '<bbbl:label key="lbl_cart_personalization_saved" language="${pageContext.request.locale.language}" />',
                    "customizationRequiredmessage":'<bbbl:label key="lbl_personalization_required_below" language="${pageContext.request.locale.language}" />',
                    "customizeRequiredmessage":'<bbbl:label key="lbl_customization_required_below" language="${pageContext.request.locale.language}" />',
                    "customizeRequiredmessageTbs":'<bbbl:label key="lbl_customization_required_message_tbs" language="${pageContext.request.locale.language}" />',
                    "personalizeRequiredmessageTbs":'<bbbl:label key="lbl_personalization_required_message_tbs" language="${pageContext.request.locale.language}" />',
                    "customizeCTAText":'<bbbl:label key="lbl_customize_this" language="${pageContext.request.locale.language}" />',
                    "personalizeCTAText":'<bbbl:label key="lbl_personalize_this" language="${pageContext.request.locale.language}" />',
                    "inProgressCustomization": '<bbbt:textArea key="txt_inProgress_customization" language ="${language}" />',
                    "addToCartCustomization": '<bbbt:textArea key="txt_cart_items_close_modal_customize" language ="${language}" />',
                    "removePersonalization": '<bbbt:textArea key="txt_remove_personalization" language ="${language}" />',
                    "removePersonalizationTbs": '<bbbt:textArea key="txt_tbs_remove_personalization" language ="${language}" />',
                    "inProgressPersonalization": '<bbbt:textArea key="txt_inProgress_Personalization" language ="${language}" />',
                    "lblPriceChange" : '<bbbl:label key="lbl_price_change" language="${pageContext.request.locale.language}" />',
                    "lblAddPriceText" : '<bbbt:textArea key="txt_pdp_katori_price" language ="${pageContext.request.locale.language}"/>',
                     "lblPBFee" : '<bbbl:label key="lbl_PB_Fee_detail" language="${pageContext.request.locale.language}" />',
                    "lblPYFee" : '<bbbl:label key="lbl_PY_Fee_detail" language="${pageContext.request.locale.language}" />',
                    "lblCRFee" : '<bbbl:label key="lbl_CR_Fee_detail" language="${pageContext.request.locale.language}" />',
                     "lblCRAddPriceText" : '<bbbt:textArea key="txt_pdp_cr_katori_price" language ="${pageContext.request.locale.language}"/>',
                     "lblCRAddPriceCustomizeText" : '<bbbt:textArea key="txt_pdp_cr_customize_katori_price" language ="${pageContext.request.locale.language}"/>',
                    "lbl_price_is_tbd":'<bbbl:label key="lbl_price_is_tbd" language="${language}"/>',
                    "lblEditPersFor": "<bbbl:label key='lbl_cart_edit_personalization_for' language='${language}'/>",
                	"lblRemovePersFor": "<bbbl:label key='lbl_cart_remove_personalization_for' language='${language}'/>",
                	"errPersonalizationRequired":"<bbbl:label key='lbl_message_addAllCollection' language='${language}'/>",                    
                    "errNotEqualTo": "Please enter a different value",
                    "errSameCoRegistrantEmail" : "<bbbt:textArea key='txt_reg_coreg_entered_email' language='${language}'/>",
                    "errInvalidAddress": "Please enter valid address",
                    "lblUpdateContactAddress": "Update Your Contact Address",
                    "lblUpdateShippingAddress": "Update Your Shipping Address",
                    "lblUpdateFutureAddress": "Update Your Future Address",
		     "placeOrderIntervalError": '<bbbt:textArea key="placeOrderIntervalError" language ="${language}" />',
		     "placeOrderIntervalErrorVbvDesktop": '<bbbt:textArea key="placeOrderIntervalErrorVbvTbs" language ="${language}" />'                    
                };

                BBB.config = BBB.config || {};
                BBB.config.envir = 'dev'; // string: 'ue' || 'dev'
                BBB.config.keys = {
                		"eximClientId": "<bbbc:config key='X-ClientId' configName='EXIMKeys'/>",
                        "eximApikey": "<bbbc:config key='X-ApiKey' configName='EXIMKeys' />",
                        "eximApiVersion": "<bbbc:config key='X-ApiVersion' configName='EXIMKeys' />",
                        "eximCssURL": "<bbbc:config key='X-CssURL' configName='EXIMKeys' />",
                        "customizeCTACodes": "<bbbc:config key='CustomizeCTACodes' configName='EXIMKeys' />",
                        "googleAddressSwitch": "<bbbc:config key='google_address_switch' configName='FlagDrivenFunctions' />",
                        "addressCountryCode": "<bbbc:config key='address_country_code' configName='ContentCatalogKeys' />",
                        "qasAddressLength": "<bbbc:config key='qas_address_length' configName='QASKeys'/>",
                        "qasTrimAddress": "<bbbc:config key='qas_trim_address' configName='QASKeys'/>",
                        "enableLTLRegForSite" : "<bbbc:config key='enableLTLRegForSite' configName='FlagDrivenFunctions' />",
                        "itemOverrideThresholdLowPrice" : "<bbbc:config key='ItemPriceOverrideMinAmount' configName='ContentCatalogKeys' />",
                        "defaultStoreNumbers": "<bbbc:config key='DefaultStoreId' configName='GiftRegistryConfig' />",
			 "placeOrderWaitingTime" : "<bbbc:config key='placeOrderWaitingTime' configName='CartAndCheckoutKeys' />",
                        "placeOrderIntervalKey" : "<bbbc:config key='placeOrderIntervalKey' configName='FlagDrivenFunctions' />"
                    };
                BBB.config.url = {
                		'katoriprice': {
                    dev: '${contextPath}/browse/katori_price.jsp'
                },
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
                        ue: '//www.facebook.com/BedBathandBeyond',
                        dev: '//www.facebook.com/BedBathandBeyond'
                    },
                    'checkBalance': {
                        ue: '/_ajax/balance.jsp',
                        dev: '${contextPath}/checkout/billing_payment.jsp'
                    },
                    'forgotPassword': {
                        ue: '/_ajax/forgot_password.jsp',
                        dev: '/_ajax/forgot_password.jsp'
                    },
                    'forgotPasswordSuccess': {
                        ue: '/_includes/modals/forgot_password_registry_success.jsp',
                        dev: '/_includes/modals/forgot_password_registry_success.jsp'
                    },
                    'updateMiniCart': {
                        ue: '/_ajax/update_minicart.jsp',
                        dev: '${contextPath}/cart/mini_cart.jsp'
                    },
                    'updateMiniCartItemCount': {
                        ue: '/_ajax/update_minicart_itemcount.jsp',
                        dev: '${contextPath}/cart/cart_item_count.jsp'
                    },
                    'getSKU': {
                        ue: '/_ajax/product_sku.json',
                        dev: '${contextPath}/browse/SKU_rollup_details.jsp'
                    },
                    'getProductData': {
                        ue: '/_ajax/product_detail.jsp',
                        dev: '${contextPath}/browse/rollup_details.jsp'
                    },
                    'addItemToRegisteryStatus': {
                        ue: '/_ajax/addItemToRegisteryStatus.jsp',
                        dev: '${contextPath}/addgiftregistry/addItemToRegisteryStatus.jsp'
                    },
                    'openCreateRegistry': {
                        ue: '/_ajax/create_registry_popup.jsp',
                        dev: '${contextPath}/addgiftregistry/create_registry_popup.jsp'
                    },
                    'anncCardRequest': {
                        ue: '/_includes/modals/annc_card_request.jsp',
                        dev: '/_includes/modals/annc_card_request.jsp'
                    },
                    'productDetailsEmailAFriend': {
                        ue: '/browse/email_pdp.jsp',
                        dev: '${contextPath}/browse/email_pdp.jsp'
                    },
                    'productDetailsNotifyOOS': {
                        ue: '/_includes/modals/notify_stock.jsp',
                        dev: '${contextPath}/browse/gadgets/notifyMeRequest.jsp'
                    },
                    'announcementCards': {
                        ue: '/_includes/modals/announcement_cards.jsp',
                        dev: '${contextPath}/bbregistry/announcement_cards.jsp'
                    },
                    'emailAFriend': {
                        ue: '/_includes/modals/email_friend.jsp',
                        dev: '${contextPath}/_includes/modules/email_friend.jsp'
                    },
                    'chkGiftCardBal': {
                        ue: '/_includes/modals/check_giftcard_balance.jsp',
                        dev: '${contextPath}/account/check_giftcard_balance.jsp'
                    },
                    'addToRegNoLogin': {
                        ue: '${contextPath}/giftregistry/add_item_to_reg_no_login.jsp',
                        dev: '${contextPath}/addgiftregistry/add_item_to_reg_no_login.jsp'
                    },
                    'clearSessionBean': {
                        ue: '${contextPath}/giftregistry/clear_add_item_to_regb.jsp',
                        dev: '${contextPath}/giftregistry/clear_add_item_to_regb.jsp'
                    },
                    'openEmailCart': {
                        ue: '${contextPath}/cart/email/email_cart.jsp',
                        dev: '${contextPath}/cart/email/email_cart.jsp'
                    },
                    'openEmailSavedItems': {
                        ue: '${contextPath}/cart/email/email_saveditems.jsp',
                        dev: '${contextPath}/cart/email/email_saveditems.jsp'
                    },
					'openIntlShippingModal':{
						ue: '/_includes/modals/intlCountryCurrecyModal.jsp',
                        dev: '${contextPath}/_includes/modals/intlCountryCurrecyModal.jsp'
					},
                    'getDirectionsWithMap': {
                        ue: '/selfservice/get_direction_withmap.jsp',
                        dev: '${contextPath}/selfservice/store/p2p_directions.jsp'
                    },
                    'legacyPwdPopup': {
                        ue: '/_includes/modals/incorrect_password.jsp',
                        dev: '${contextPath}/account/incorrect_password.jsp'
                    },
                    'legacyPwdCheck': {
                        ue: '/_ajax/legacy_password_check.jsp',
                        dev: '/_ajax/legacy_password_check.jsp'
                    },
                    'findCollegeSelect': {
                        ue: '/_ajax/find_your_college.json',
                        dev: '${contextPath}/selfservice/frags/find_your_college_json.jsp'
                    },
                    'privacyPolicy': {
                        ue: '/_includes/modals/privacy_policy.jsp',
                        dev: '${contextPath}/account/privacy_policy.jsp'
                    },
                    'getStoreData': {
                        ue: '/_ajax/find_in_store_result.jsp',
                        dev: '${contextPath}/selfservice/find_in_store_result.jsp'
                    },
                    'captchaImagePath': {
                        ue: '/simpleCaptcha.png',
                        dev: '${contextPath}/simpleCaptcha.png'
                    },
                    'getStoreDataFavorite': {
                        ue: '/_ajax/store_info.jsp',
                        dev: '${contextPath}/selfservice/store/store_info_frag.jsp'
                    },
                    'pathToZeroClipboard': {
                        ue: '/_assets/global/flash_assets/ZeroClipboard10.swf',
                        dev: '${imagePath}/_assets/global/flash_assets/ZeroClipboard10.swf'
                    },
                    'BVDoLogin': {
                        ue: '/_ajax/login.jsp',
                        dev: '${contextPath}/account/login.jsp'
                    },
                    'addItemToCart': {
                        ue: '/_ajax/add_to_cart.json',
                        dev: '${contextPath}/browse/add_item_to_cart_pdp.jsp'
                    },
                    'addItemToListStatus': {
                        ue: '/_ajax/addItemToListStatus.jsp',
                        dev: '${contextPath}/browse/add_item_to_list_pdp.jsp'
                    },
                    'addItemToListStatusPostLogin': {
                        ue: '/_ajax/addItemToListStatusPostLogin.jsp',
                        dev: '${contextPath}/browse/add_item_to_list_pdp_post_login.jsp'
                    },
                    'mapQuestStaticMapContentURL': {
                        ue: '//www.mapquestapi.com/staticmap',
                        dev: '${mapQuestStaticMapContentURL}'
                    },
                    'preloaderImg': {
                        ue: '/_assets/global/images/widgets/loader.gif',
                        dev: '/_assets/global/images/widgets/loader.gif'
                    },
                    'bridalShowData': {
                        ue: '/_ajax/bridal_show_data.jsp',
                        dev: '${contextPath}/cms/bridal_template.jsp'
                    },
                    'collegeEventsData': {
                        ue: '/_ajax/bridal_show_data.jsp',
                        dev: '${contextPath}/cms/college_events_template.jsp'
                    },
                    'fConnectImage': {
                        ue: '/_assets/global/images/f_connect.png',
                        dev: '${imagePath}/_assets/global/images/f_connect.png'
                    },
                    'fConnectImageNotAvailable': {
                        ue: '/_assets/global/images/f_connect_na.png',
                        dev: '${imagePath}/_assets/global/images/f_connect_na.png'
                    },
                    'validateFConnect': {
                        ue: '/_ajax/validate_fb_email_fc_Enabled_sister.json',
                        dev: '${contextPath}/account/facebook_connect.jsp'
                    },
                    'fbLoaderImg': {
                        ue: '/_assets/global/images/fb-loader.gif',
                        dev: '${imagePath}/_assets/global/images/fb-loader.gif'
                    },
                    'addItemToCartFromReg': {
                        ue: '/_ajax/add_to_cart_from_reg.json',
                        dev: '${contextPath}/browse/add_item_to_cart_gift.jsp'
                    },
                    'emailAPageCMS': {
                        ue: '/_includes/modals/email_friend.jsp',
                        dev: '${contextPath}/emailtemplates/emailapage/emailAPage_cms.jsp'
                    },
                    'getStoreDataFavoriteRegistry': {
                        ue: '/_ajax/store_info_no_stock_info.jsp',
                        dev: '${contextPath}/giftregistry/modals/store_info_no_stock_info.jsp'
                    },
                    'blankGIF': {
                        ue: '/_assets/global/images/blank.gif',
                        dev: '${imagePath}/_assets/global/images/blank.gif'
                    },
                    'getStates': {
                        ue: '/_ajax/states.json',
                        dev: '${contextPath}/checkout/billing/states.jsp'
                    },
                    'noImageFound': {
                        ue: '/_assets/global/images/no_image_available.jpg',
                        dev: '${imagePath}/_assets/global/images/no_image_available.jpg'
                    },
                    'migratedAccount': {
                        ue: '/_includes/modals/new_password.jsp',
                        dev: '${contextPath}/account/frags/changepasswordmodel.jsp'
                    },
                    'migratedAccountPwdCheck': {
                        ue: '/_includes/modals/new_password.jsp',
                        dev: '${contextPath}/account/frags/changepasswordmodel.jsp'
                    },
                    'opencCLaunchPage': {
                        ue: '/_includes/modals/cCLaunch.jsp',
                        dev: '${contextPath}/checkout/preview/cCLaunch.jsp'
                    },
                    'bbbLogoDialog': {
                        <c:set var="cursiteimg" value="bbb" />
                        <c:set var="countrySuffix" value="" />
                        <c:choose>
                            <c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
                                <c:set var="cursiteimg" value="bb" />
                            </c:when>
                            <c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
                                <c:set var="cursiteimg" value="bbb" />
                                <c:set var="countrySuffix" value="_ca" />
                            </c:when>
                        </c:choose>
                        ue: '/_assets/global/images/logo/logo_${cursiteimg}_wide${countrySuffix}.png',
                        dev: '${imagePath}/_assets/global/images/logo/logo_${cursiteimg}_wide${countrySuffix}.png'
                    },
                    's7URL': {
                        ue: '//s7d7.scene7.com/is/image/BedBathandBeyond/',
                        dev: '${scene7Path}/'
                    },
                    's7MainURL': {
                        ue: '//s7d7.scene7.com/is/image/',
                        dev: '${scene7AltPath}'
                    },
                    'scene7DomainPath': {
                        //dev: '${scene7DomainPath}'
                        dev: '//s7dx.scene7.com/is/image/BedBathandBeyond'
                    },
                    'profileURL': {
                        ue: '/tbs/giftregistry/droplet/profile_exist_droplet.jsp',
                        dev: '/tbs/giftregistry/droplet/profile_exist_droplet.jsp'
                    },
                    'wishlistMoveToCartError': {
                        ue: '/_ajax/wishlist_move_to_cart_error.jsp',
                        dev: '${contextPath}/wishlist/wishlist_move_to_cart_error.jsp'
                    },
                    'liveClickerBridalVideos': {
                        <c:set var="liveClickerUrl" scope="request">
                           <bbbc:config key="liveclicker_guides_advice" configName="ThirdPartyURLs" />
                        </c:set>
                        ue: '//d2vxgxvhgubbj8.cloudfront.net/custom/64/search/wedding.html',
                        dev: '${liveClickerUrl}'
                    },
                    'smallLoader': {
                        ue: '/_assets/global/images/widgets/small_loader.gif',
                        dev: '${imagePath}/_assets/global/images/widgets/small_loader.gif'
                    },
                    'smallLoader2': {
                        <c:set var="loaderIMGSuffix" value="_by" />
                        <c:if test="${currentSiteId eq TBS_BuyBuyBabySite}">
                            <c:set var="loaderIMGSuffix" value="_bb" />
                        </c:if>
                        ue: '/_assets/global/images/widgets/small_loader${loaderIMGSuffix}.gif',
                        dev: '${imagePath}/_assets/global/images/widgets/small_loader${loaderIMGSuffix}.gif'
                    },
                    'regFlyout': {
                        ue: '/_includes/modules/header_flyouts/bridal_gift_registry_loggedin.jsp',
                        dev: '${contextPath}/giftregistry/reg_flyout.jsp'
                    },
                    'hideBopusSearchForm': {
                        ue: '${contextPath}/selfservice/store/statusShowBopusForm.jsp',
                        dev: '${contextPath}/selfservice/store/statusShowBopusForm.jsp'
                    },
                    'getFavStoreData': {
                        dev: '${contextPath}/giftregistry/registry_fav_store.jsp',
                        ue: '/_ajax/create_reg_fav_store.jsp'
                    },
                    'regHeader': {
                        ue: '/_ajax/subheader.jsp',
                        dev: '${contextPath}/_includes/header/subheader.jsp'
                    },
                    'hhPrefCenterURL': {
                        ue: 'https://bbbprefcenter.test-harte-hanks.com',
                        dev: '${harteAndHanksURL}'
                    },
                    'mapQuestLibURL': {
                        ue: '//www.mapquestapi.com/sdk/js/v7.0.s/mqa.toolkit.js?key=Fmjtd|luu22h0yn9%2Cr5%3Do5-h0rl0',
                        dev: '<c:choose><c:when test="${pageSecured}"><bbbc:config key="js_mapQuest" configName="ThirdPartyURLs" /></c:when><c:otherwise><bbbc:config key="js_mapQuest" configName="ThirdPartyURLs" /></c:otherwise></c:choose>'
                    },
                    'shippingRestrictionPopUp': {
                        ue: '/_includes/modals/shipping_restrictions_checkout.jsp',
                        dev: '${contextPath}/_includes/modals/shipping_restrictions_checkout.jsp'
                    },
                    'cartAjaxSubmitURL': {
                        ue: '/_ajax/ajax_handler_cart.jsp',
                        dev: '${contextPath}/cart/ajax_handler_cart.jsp'
                    },
                    'sflAjaxSubmitURL': {
                        ue: '/_ajax/ajax_handler_sfl.jsp',
                        dev: '${contextPath}/_includes/modules/cart_store_pickup_form.jsp'
                    },
                    'shareWithFacebook': {
                        ue: '//www.facebook.com/share.php?',
                        dev: '//www.facebook.com/share.php?'
                    },
                    'shareWithTwitter': {
                        ue: '//twitter.com/intent/tweet?',
                        dev: '//twitter.com/intent/tweet?'
                    },
                    'shareWithPinterest': {
                        ue: '//pinterest.com/pin/create/button/?',
                        dev: '//pinterest.com/pin/create/button/?'
                    },
                    'bitly': {
                        ue: '//api.bitly.com/v3/shorten?callback=?',
                        dev: '//api.bitly.com/v3/shorten?callback=?'
                    },
                    'bitlyAPIKey': {
                        ue: 'R_3628dc7572a56ea3d97f638ff1b45246',
                        dev: 'R_3628dc7572a56ea3d97f638ff1b45246'
                    },
                    'bitlyLoginID': {
                        ue: 'o_4i5tdj1q5g',
                        dev: 'o_4i5tdj1q5g'
                    },
                    'cookieSecureSIDAjaxed': {
                        ue: '/_ajax/cookieSecureSIDAjaxed.jsp',
                        dev: '${contextPath}/account/cookieSecureSIDAjaxed.jsp'
                    },
                    'fromResetURL': {
                        ue: '/_includes/modals/new_password.jsp',
                        dev: '${contextPath}/account/frags/changePwdAfterResetmodel.jsp'
                    },
                    'migratedUserPopupURL': {
                        ue: '/_includes/modals/migrated_user_popup.jsp',
                        dev: '${contextPath}/account/frags/migrated_user_popup.jsp'
                    },
                    'siteTourJSP': {
                        ue: '/_ajax/siteTour.jsp',
                        dev: '${contextPath}/_includes/modals/siteTour.jsp'
                    },
                    'getMultiSKUCollection': {
                        ue: '/_includes/modals/product_details_collection.jsp',
                        dev: '${contextPath}/browse/product_details_collection.jsp'
                    },
					'addProductToCompareBox': {
						ue: '${contextPath}/compare/addProduct_to_drawer.jsp',
						dev: '${contextPath}/compare/addProduct_to_drawer.jsp'
					},
					'removeProductFromCompareBox': {
						ue: '${contextPath}/compare/removeProduct_from_drawer.jsp',
						dev: '${contextPath}/compare/removeProduct_from_drawer.jsp'
					},
					'removeAllProductsFromCompareBox': {
						ue: '${contextPath}/compare/remove_all_Product.jsp',
						dev: '${contextPath}/compare/remove_all_Product.jsp'
					},
					'getCompareAlert': {
						ue: '${contextPath}/_includes/modules/compare_box_alert.jsp',
						dev: '${contextPath}/_includes/modules/compare_box_alert.jsp'
					},
					'removeBucket': {
			            ue: '${contextPath}/_ajax/remove_from_bucket.jsp',
			            dev: '${contextPath}/_ajax/remove_from_bucket.jsp'
			        },
			        'addCompareItemToRegistryStatus': {
			            ue: '${contextPath}/addgiftregistry/addComparisonItemToRegistryStatus.jsp',
			            dev: '${contextPath}/addgiftregistry/addComparisonItemToRegistryStatus.jsp'
			        },
                    'heartBeat':{
                        ue: '${contextPath}/selfservice/heart_beat.jsp',
                        dev: '${contextPath}/selfservice/heart_beat.jsp'
                    },
					'getMultiSKUView': {
                        ue: '${contextPath}/compare/compare_list_view.jsp',
                        dev: '${contextPath}/compare/compare_list_view.jsp'
                    },
			        'validateAddressURL': {
		                ue: '/_ajax/validate.jsp',
		               dev: '${contextPath}/paypal/validate.jsp'
					},
                    'compareDrawer': {
                        ue: '/browse/compare_drawer.jsp',
                       dev: '${contextPath}/browse/compare_drawer.jsp'
                    },
					'getCompareSameSkuAlert': {
						ue: '${contextPath}/_includes/modules/compare_sameSku_alert.jsp',
						dev: '${contextPath}/_includes/modules/compare_sameSku_alert.jsp'
					},
					'dslAlternateModal': {
                        dev: '${contextPath}/_includes/modals/dsl_alternate_modal.jsp'
                    },
                    'addToCartSuccessModal': {
                        dev: '${contextPath}/_includes/modals/add_to_cart_success_modal.jsp'
                    },
                    'addToCartSuccessModalCustomize': {
                        dev: '${contextPath}/_includes/modals/add_to_cart_success_modal_customize.jsp'
                    },
                    'primeRegistry': {
                   	    dev: '${contextPath}/browse/prime_gift_registry_data.jsp'
                    },
                    'advancedOrderSearch':{
                        <c:set var="minimalOrderRequired">
                        	<bbbc:config key='TBS_MINIMAL_ORDERDETAIL' configName='FlagDrivenFunctions'/>
                        </c:set>
                        <c:choose>
                            <c:when test="${minimalOrderRequired}">
                                <c:set var="URL" value="/tbs/search/frag/minimal_advanced_order_results.jsp" />
                            </c:when>
                            <c:otherwise>
                                <c:set var="URL" value="/tbs/search/frag/advanced_order_results.jsp" />
                            </c:otherwise>
                        </c:choose>
                        url: '${URL}'
                    }
                };
          
			            
            <%-- ### CM-5508 ### --%>
    
            
        <%-- </dsp:oparam>
    </dsp:droplet> --%>
</dsp:page>