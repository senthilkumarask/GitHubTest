<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/com/bbb/account/signup/BBBEmailSignUpFormHandler"/>


<html>
<head>
<title><bbbl:label key="lbl_email_test" language="${pageContext.request.locale.language}" /></title>
<link rel="stylesheet" type="text/css" href="//www.bedbathandbeyond.com/_assets/bbbeyond/css/theme.css">
<link rel="stylesheet" type="text/css" href="//www.bedbathandbeyond.com/_assets/global/css/bbb_combined.css">
<script type="text/javascript" src="//www.bedbathandbeyond.com/_assets/global/js/jquery-1.7.1.min.js"></script>
<script type="text/javascript" src="//www.bedbathandbeyond.com/_assets/global/js/bbb_combined_bottom.js"></script>

<style>
.success {color:#666;font-size:24px;text-align:center;}
</style>
</head>

<body>
		 	<dsp:droplet name="ErrorMessageForEach">
				<dsp:param bean="BBBEmailSignUpFormHandler.formExceptions" name="exceptions" />
				<dsp:oparam name="output">
						<p class="error"><dsp:valueof param="message" valueishtml="true"/></p>
				</dsp:oparam>
			</dsp:droplet>

	<dsp:getvalueof var="sourceCode" param="src"/>
	<dsp:getvalueof var="success" bean="BBBEmailSignUpFormHandler.success"/>
	<dsp:getvalueof var="actionURL" idtype="java.lang.String" bean="/OriginatingRequest.requestURIWithQueryString"/>
	 <c:choose>
		 <c:when test="${success eq 'true'}">
		 	<form name="emailSubOptIn" method="post" style="width:450px">
		 	<table >
		    <tr align="left">		
		        <td><div class="success"><bbbl:label key="lbl_thanks_signing_up" language="${pageContext.request.locale.language}" /><br>
		            <bbbl:label key="lbl_first_time_subscriber" language="${pageContext.request.locale.language}" /> </div>
		        </td>		
			</tr>                          
			</table>
			</form>			
		 </c:when>
		 <c:otherwise>
		 <dsp:form method="post" action="${actionURL}" name="emailSubOptIn" style="width:450px">
			<div id="form1" class="by">
			<table >
				<tr><td align="left"><div class="text padBottom_10">
					<dsp:input bean="BBBEmailSignUpFormHandler.email" type="text" size="40" style="width: 440px; color:#ccc; height:30px" maxlength="50" id="contactUsEmail" name="contactUsEmail" value="Enter Your Email Address" iclass="required emailextended escapeHTMLTag" onfocus="if(this.value == 'Enter Your Email Address') { this.value = ''; }" >
			        	<dsp:tagAttribute name="aria-labelledby" value="lblcontactUsEmail errorcontactUsEmail"/>
					</dsp:input>
					<dsp:setvalue param="code" value="e"/>
					
				</td></tr>
				
				
				<c:if test="${sourceCode eq 'ev'}">
				
				<tr><td align="left"><div class="text padBottom_10">
					<dsp:input bean="BBBEmailSignUpFormHandler.emailConfirm" type="text" size="40" style="width: 440px; color:#ccc; height:30px" maxlength="50" id="contactUsEmailConfirm" name="contactUsEmailConfirm" value="Confirm Your Email Address" iclass="required emailextended escapeHTMLTag " onfocus="if(this.value == 'Confirm Your Email Address') { this.value = ''; }">
						<dsp:tagAttribute name="aria-labelledby" value="lblcontactUsEmailConfirm errorcontactUsEmailConfirm"/>
					</dsp:input>
					<dsp:setvalue param="code" value="${sourceCode}"/>
					
				</td></tr>
				</c:if>
				
				<c:if test="${sourceCode eq 'evz'}">
				
				<tr><td align="left"><div class="text padBottom_10">
					<dsp:input bean="BBBEmailSignUpFormHandler.emailConfirm" type="text" size="40" style="width: 440px; color:#ccc; height:30px" maxlength="50" id="contactUsEmailConfirm" name="contactUsEmailConfirm" value="Confirm Your Email Address" iclass="required emailextended escapeHTMLTag " onfocus="if(this.value == 'Confirm Your Email Address') { this.value = ''; }">
						<dsp:tagAttribute name="aria-labelledby" value="lblcontactUsEmailConfirm errorcontactUsEmailConfirm"/>
					</dsp:input>
					
				</td></tr>
				
				
				<tr align="left"><td align="left"><div class="text padBottom_10">
					<dsp:input bean="BBBEmailSignUpFormHandler.zipCode" type="text" size="30" style="width: 440px; color:#ccc; height:30px" maxlength="50" id="zip" name="zip" value="Zip Code" iclass="required emailextended escapeHTMLTag " onfocus="if(this.value == 'Zip Code') { this.value = ''; }">
						<dsp:tagAttribute name="aria-labelledby" value="lblcontactUsEmailConfirm errorcontactUsEmailConfirm"/>
					</dsp:input>
					<dsp:setvalue param="code" value="${sourceCode}"/>
					
				</td></tr>
				
				</c:if>
					
					<dsp:input bean="BBBEmailSignUpFormHandler.sourceCode" type="hidden" paramvalue="code" />
				
				<tr  align ="left">
				<td >
					<div class="button">
				  		<dsp:input bean="BBBEmailSignUpFormHandler.signUp" type="submit" name="submit" title="SUBMIT" id="submit" value="SUBMIT" onclick="return checkEqual(this);"/>
				  	</div>
				</td>
				</tr>                          
			</table>
			</div>
			</dsp:form>
		 	
		 </c:otherwise>
	</c:choose>
	
	
	
	


<script type="text/javascript">

$('#contactUsEmail').on('input', function() {
    var input=$(this);
    var re = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
    var is_email=re.test(input.val());
    input.css("color","#000");
    if(is_email){input.removeClass("error")
    $('.button input').removeAttr("disabled");}
    else{input.addClass("error");
    $('.button input').attr('disabled', true);}
});

$('#contactUsEmailConfirm').on('input', function() {
    var input=$(this);
    var re = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
    var is_email=re.test(input.val());
    input.css("color","#000");
    if(is_email){input.removeClass("error")
    $('.button input').removeAttr("disabled");}
    else{input.addClass("error");
    $('.button input').attr('disabled', true);}
});

$('#zip').on('input', function() {
    var input=$(this);
    var re = /^\d{5}(-\d{4})?$/;
    var is_email=re.test(input.val());
    input.css("color","#000");
    if(is_email){input.removeClass("error");
	$('.button input').removeAttr("disabled");}
    else{input.addClass("error");
	 $('.button input').attr('disabled', true);}
});

$('#contactUsEmailConfirm').on('input', function checkEqual() 
{ 
var input=$(this);
var emailA = document.emailSubOptIn.contactUsEmail.value; 
var emailB = document.emailSubOptIn.contactUsEmailConfirm.value;

       if( emailA != emailB ) 
       {
		   input.addClass("error");
		   $('.button input').attr('disabled', true);
		    $('#submit').attr('disabled', true);
       } 

         else{input.removeClass("error");
	 	 $('.button input').attr('disabled', false);

       } 

}); 

$('#contactUsEmail').on('input', function checkEqual() 
{ 
	if(document.getElementById("contactUsEmailConfirm") != null)
	{
		var emailA = document.emailSubOptIn.contactUsEmail.value; 
		var emailB = document.emailSubOptIn.contactUsEmailConfirm.value;
		
		
		if(emailB != "Confirm Your Email Address")
			{
		       if( emailA != emailB) 
		       {
		    	   $('#contactUsEmailConfirm').addClass("error");
				   $('.button input').attr('disabled', true);
				    $('#submit').attr('disabled', true);
		       } 
		
		         else{$('#contactUsEmailConfirm').removeClass("error");
			 	 $('.button input').attr('disabled', false);
		
		       } 
			}
	}
}); 

$('div.button').on('click', function()
{
	if ($('#contactUsEmail').hasClass("error") || $('#contactUsEmailConfirm').hasClass("error") || $('#zip').hasClass("error"))
	{	
		return false;	
	}
});
</script> 
<script type="text/javascript">
                // Bed Bath & Beyond namespace as a global variable
                var BBB = BBB || {};

                /**
                * Extending BBB Object with all the String messages which will be used to show messages through JS.
                */
                BBB.glblStringMsgs = {
                    "selected": "Selected",
                    "applied": "Applied",
                    "select": "Select",
                    "readMore": "Read More",
                    "readLess": "Read Less",
                    "viewCartContents": "View Cart Contents",
                    "hideCartContents": "Hide Cart Contents",
                    "viewOrderContents": "View Order Contents",
                    "hideOrderContents": "Hide Order Contents",
                    "addBtn": "Add",
                    "cancelBtn": "Cancel",
                    "okBtn": "Ok",
                    "saveChangesBtn": "Save Changes",
                    "doneBtn": "Done",
                    "closeBtn": "Close",
                    "itemAdded": "Item has been added to Registry",
                    "itemNotAdded": "Item couldn&#39;t be added to Registery",
                    "enterNewShipAdd": "Enter a new shipping address",
                    "enterNewBillAdd": "Enter a new billing address",
                    "showBtn": "Show",
                    "yesBtn": "Yes",
                    "noBtn": "No",
                    "msgSentTo": "Message sent to",
                    "charsRemaining": "characters remaining",
                    "loading": "Loading...",
                    "goBtn": "Go",
                    "college": "College",
                    "state": "Select State",
                    "province": "Select Province",
                    "lblprovince": "Province",
                    "zipCodeCanada": "Postal Code",
                    "lblZip": "Zip",
                    "lblstate": "State",
                    "pwdReminderSent": "Password Reminder Sent",
                    "emailAFriend": "Email a Friend",
                    "findInStore": 'Find in Store',
                    "findAStore": "Find a Store",
                    "findStoreResults": 'Search Results',
                    "rateAndReview": 'Rate & Review',
                    "itemAddedToList": 'Item(s) has been added to Saved Items',
                    "itemNotAddedToList": 'Item(s) couldn&#39;t be added to Saved Items',
                    "fbConnectEnabled": 'Facebook Connect Enabled',
                    "fbDisconnectText": '(You can disconnect from facebook in &#39;My Account Overview&#39;)',
                    "pwdUpdated": 'Password Succesfully Updated',
                    "s7messageTextDesktop": 'Hover to zoom, click to view large image.',
                    "s7messageTextTouch": 'Tap and hold to zoom.',
                    "s7ZoomImageModalTip": 'Press escape or click anywhere to close.',
                    "s7ZoomImageModalTipTouch": 'Touch anywhere to close.',
                    "lblSKU": 'SKU #',
                    "btnLBLFindAStore": 'Find a store',
                    "btnLBLChangeStore": 'Change Store',
                    "passwordRequirements": 'Password Requirements',
                    "btnBackToQuickView": 'VALUE NOT FOUND FOR KEY lbl_backto_quickinfo',
                    "titleRegPageAddToCart": 'Your Item Was Successfully Added to Cart',
                    "btnRegPageViewCart": 'View Cart & Checkout',
                    "lblRegPageQuantity": 'Quantity',
                    "quickViewContinueShopping": 'Continue Shopping',
                    "shareOrderOn_facebook": 'Share this order on Facebook',
                    "shareOrderOn_twitter": 'Share this order on Twitter',
                    "shareOrderOn_pinterest": 'Share this order on Pinterest',
                    "twitterHandle": '@bedbathbeyond',
                    "twitterShareTextPrefix": 'Check out what I just ordered: \n',
                    "twitterShareTextSuffix": '&nbsp;from&nbsp;',
                    "twitterShareTweetSuffix": '&nbsp;via&nbsp;',
                    "pinterestShareTextSuffix": " - bedbathandbeyond.com",

                    
                    "QASAjaxFail": "Some Error occured with Dynamic Address check! Do you wish to continue with address which you entered or press Cancel Button to retry?",
                    "s7ZoomErrorHTML": "Zoom feature is not available for this product.",
                    "futureMonth": "Please enter a future month.",
                    "somethingWentWrong": "We are sorry!  A system error occurred. Please try again or contact us.",
                    "ajaxURLMissing": "We are sorry!  A system error occurred. Please try again or contact us.",
                    "alphaNumericName": "No special chars",
                    "noWhiteSpace": "Spaces are not permitted.  Please re-enter the value.",
                    "phoneNumValid": "Please specify a valid phone number.",
                    "passwordEqualTo": "Password must match the Confirm Password.",
                    "emailEqualTo": "Email and Confirm Email do not match.",
                    "numberOnly": "Please enter number only.",
                    "alphaOnly": "Please enter letters only.",
                    "requiredCommonCVV": "Please enter a CVV code.",
                    "requiredCommonName": "Please enter required value.",
                    "featuresLike": "Please enter less than 1000 characters.",
                    "favoriteWeb": "Please enter less than 1000 characters.",
                    "textArea1500": "Please enter a value less than 1000 characters.",
                    "selectGrreting": "Please select a greeting.",
                    // "errAlphaNumeric": "Please enter letters, numbers, spaces or underscore only.",
                    "errRequiredCommon": "Please enter required value.",
                    "errRequiredCommonSelect": "Please seelect a value.",
                    "errCollectionItemNotSelected": "Please ensure that a quantity is entered. If available, please select size/color/finishing. ",
                    "errDataIssue": "This product is currently unavailable.",
                    "errSelectFinish": "Please select a finish.",
                    "errSelectColor": "Please select a color",
                    "errSelectSize": "Please select a size.",
                    "errSelectQty": "Enter a quantity.",
                    "errSelectQtyNotNumber": "Please enter a numeric value.",
                    "errGeneric": "We&#39;re sorry!  A system error occurred. Please try again or contact us.",
                    "errOutOfStock": "Product is out of stock.  Please check later or contact us about purchasing this item.",
                    "errTitle": "We&#39;re sorry!  A system error occurred. Please try again or contact us.",
                    "errSendingInfo": "We&#39;re sorry!  A system error occurred. Please try again or contact us.",
                    "errEmailCart": "Error occoured while sending Email of the cart",
                    "errFindRegMultiInput": "Please enter only one of the following: First & Last Name or Email or Registry Number.",
                    "errFindRegMultiInput2": "Please enter only one of the following: First & Last Name or Registry Number.",
                    "errSelectRegistry": "Letters or punctuation only please.",
                    "errSubmissionUnavailable": "We&#39;re sorry!  A system error occurred. Please try again or contact us.",
                    "errLettersWithBasicPunc": "Letters or punctuation only please.",
                    "errAlphaNumeric": "Please enter letters, numbers, spaces or underscore only.",
                    "errAlphaNumericSpace": "Letters, numbers, or spaces only please.",
                    "errAlphaNumericBasicPunc": "Please enter letters, numbers, spaces or underscore only.",
                    "errAlpha": "Do these show up right as you tab out?  After you press submit at the end of filling in several of them, when?",
                    "errCannotStartWithNumber": "Can&#39;t start with number.",
                    "errLettersOnly": "Please enter letters only.",
                    "errNoWhitespace": "Spaces are not permitted.  Please re-enter the value.",
                    "errZipRange": "Please enter a valid zip or postal code",
                    "errInteger": "Please enter a positive or negative non-decimal number.",
                    "errVinUS": "Please enter a valid VIN number.",
                    "errDateITA": "Please enter value in standard date format: mm/dd/yyyy.",
                    "errDateNL": "Please enter value in standard date format: mm/dd/yyyy.",
                    "errTime": "Please enter a value time between 00:00 and 23:59. ?? - is this 24 hour format?",
                    "errPhoneUS": "Please enter a valid phone number.",
                    "errPhoneUK": "Please enter a valid phone number.",
                    "errMobileUK": "Please sepcify a valid mobile number",
                    "errCreditCardTypes": "Please enter a valid credit card number.",
                    "errIPv4": "Please enter a valid IP v4 address.",
                    "errIPv6": "Please enter a valid IP v6 address.",
                    "errPasswordValid": "Password does not meet minimum requirements",
                    "errPhoneNumValid": "Please enter a valid phone number.",
                    "errEmailExtended": "Please enter a valid email address.",
                    "errEmailExtendedMultiple": "Email address must be valid.  Please separate multiple email addresses with a semi-colon.",
                    "errMultiFieldUSPhone": "Please specify a valid phone number.",
                    "errValidateCoRegistrant": "Please enter a valid phone number.",
                    // "errZipUS": "Please enter a valid US zip code.",
                    // "errZipCA": "Please enter a valid Canadian postal code.",
                    "errNoResultsFound": "No Results Found",
                    "errAddressAlphaNumericBasicPunc": "Address should not contain the following characters  ( ) ,\"",
                    "errcannotStartWithSpecialChars": "Cannot start with special characters.        ",
                    "errcannotStartWithWhiteSpace": "Cannot start with a space.      ",
                    "errorderFormat": "Please enter a valid order number. Valid order numbers consist of 3 letters followed by 10 digits. ",
                    "errDateFormatDDMMYYYY": "Please enter a valid date in the format of \"dd/mm/yyyy\".",
                    "errDateFormatMMDDYYYY": "Please enter a valid date in the format of \"mm/dd/yyyy\".",
                    "errAlphaBasicPunc": "Please enter letters, apostrophes, hyphens, and spaces only. ",
                    "errAlphaBasicPuncWithComma": "Please enter letters, periods, apostrophes, hyphens, commas, and spaces only. ",
                    "errZoomImageLoadFail": "Unable to load zoom image. Please try again later.",
                    "errInvalidQtyNotNumber": "The item(s) with an invalid quantity has not been updated. Please enter a valid number.",
                    "errNoDigits": "Cannot use numbers. ",
                    "errInvalidGiftMessage": "Invalid message. These characters cannot be used ~ ` | &trade; &euro; &cent; &pound; &curren; &yen; &copy; &reg; &iquest;",
                    "errHasDigits": "Must contain a digit.",
                    "errHasLowercase": "Must contain a lowercase letter (a-z).  ",
                    "errHasUppercase": "Must contain an uppercase letter (A-Z).   ",
                    "errPOBoxAddNotAllowed": "We&apos;re sorry! We do not ship to P.O. Box addresses. ",
                    "errEqualToIgnoreCase": "Please enter same value again.",
                    "errorSummaryMsg": "There were 1 or more errors. Please correct these errors and try again.",
                    "errNoFlashEnabled": "Flash is not supported on your browser, please copy the below URL. ",
                    "errFBConnectFailed": "Failed to connect to Facebook. Please try again later.",
                    "errFindInStorePDPCollection": "We&#39;re sorry, we failed to process your request. Please try again.<br/>Before you proceed, please make sure, that a valid value for quantity is entered and valid size / color / finishing choices &#40;if available&#41; are selected.",
                    "errQtyExceedRegReqQty": "Please enter valid quantity. Quantity purchased exceeds quantity requested.",
                    "errInvalidEscapeHTMLTag": "HTML tags are not allowed.",
                    "msgTitle": "Warning",
                    // "errPasswordChars": "Password does not meet minimum requirements",
                    "errPasswordLength": "8-20 Characters",
                    "errPasswordHasDigits": "Number",
                    "errPasswordHasLowercase": "Lowercase Letter",
                    "errPasswordHasUppercase": "Capital Letter",
                    "errPasswordNoWhitespace": "No Space",
                    "errPasswordNoFirstname": "Do not use your first name",
                    "errPasswordNoLastname": "Do not use your last name",
                    "errPasswordFirstName": "Password must not contain your first name",
                    "errPasswordLastName": "Password must not contain your last name",
                    "errGeoPermissionDenied": "Failed to retrieve location - Permission Denied",
                    "errGeoLocationUnavailable": "Failed to retrieve location - Location Unvailable",
                    "errGeoTimeoutExpired": "Failed to retrieve location - Timeout Expired",
                    "errGeoNotSupported": "Failed to retrieve location - Geo location API is not supported by your device",
                    "errGeoAPIFailure": "Failed to retrieve location - Geo location API failed to initialize",
                    "errAlphaBasicPuncWithPeriod": "Please enter alphabets, period, apostrophe, hyphen and space only.",
                    "errAlphaBasicPuncWithPeriodAndUnicodeChars": "Please enter valid alphabets, period, apostrophe, hyphen and space only.",
                    "errFriendEmail": "Please enter a valid email address for your friend.",
                    "errFriendEmailMaxlimit": "Maximum 10 email addresses applicable.",
                    "errYourEmail": "Please enter your valid email.",
                    "errCaptchaAnswer": "Please enter correct Captcha Value.",
                    "errTxtEmailMessage": "Please enter message to email.",
                    "errSelectSubject": "Please select a category.",
                    "errFirstName": "Please enter first name.",
                    "errLastName": "Please enter last name.",
                    "errNameCard": "Please enter the name printed on card.",
                    "errAddress1": "Please enter address 1.",
                    "errPassword": "Please enter valid password.",
                    "errCardType": "Please select card type.",
                    "errRegistryAnnouncement": "Please select the number of registry announcement cards.",
                    "errCardNumber": "Please enter a valid card number.",
                    "errRequiredCommonCVV": "Enter valid CVV number.",
                    "errSecurityCode": "Enter valid security code.",
                    "errMonth": "Please select valid month.",
                    "errYear": "Please select valid year.",
                    "errCity": "Please enter valid city.",
                    "errState": "Please select a state.",
                    "errStateText": "Please enter state name.",
                    "errCountryName": "Please select country name.",
                    "errZip": "Please enter valid zip code.",
                    "errPostalCode": "Please enter valid Postal code.",
                    "errZipCA": "Please enter valid Postal code.",
                    "errZipUS": "Please enter valid zip code.",
                    "errUnsubscribeMail": "Please select an option.",
                    "errCurrentPassword": "Please enter valid current password.",
                    "errCoupons": "Please enter valid coupon number.",
                    "errFullName": "Please enter full name.",
                    "errRegistryType": "Please select registry type.",
                    "errGiftCardNumber": "Please enter valid gift card number.",
                    "errGiftCardPin": "Please enter valid gift card pin.",
                    "errRegistryNumber": "Please enter valid registry number.",
                    "errDirStartStreetName": "Please enter street name.",
                    "errNumberOfBox": "Please enter number of box.",
                    "errNumBoxes": "Please select number of boxes.",
                    "errTimeZone": "Please select the time zone.",
                    "errPasswordChars": "Password does not meet minimum requirements",
                    "errPrefContactMethod": "Please choose a valid a contact method.",
                    "cellNumValid": "Please enter 10 digit mobile number without punctuation.",
                    "errAlphaNumericBasicPuncWithPeriod": "Please enter alphabets, digits, period, apostrophe, hyphen and space only.",
                    "errNoMatchesFound": "No matches found!"
                };

                BBB.config = BBB.config || {};
                BBB.config.envir = 'dev'; // string: 'ue' || 'dev'
                BBB.config.url = {
                    defaultPath: {
                        ue: '/404.html',
                        dev: ''
                    },
                    '404': {
                        ue: '/404.jsp',
                        dev: '/store/404.jsp'
                    },
                    '409': {
                        ue: '/409.jsp',
                        dev: '/store/409.jsp'
                    },
                    '500': {
                        ue: '/500.jsp',
                        dev: '/store/global/sessionExpired.jsp'
                    },
                    '503': {
                        ue: '/500.jsp',
                        dev: '/store/global/sessionExpired.jsp'
                    },
                    'facebookPage': {
                        ue: '//www.facebook.com/BedBathandBeyond',
                        dev: '//www.facebook.com/BedBathandBeyond'
                    },
                    'checkBalance': {
                        ue: '/_ajax/balance.jsp',
                        dev: '/store/checkout/billing_payment.jsp'
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
                        dev: '/store/cart/mini_cart.jsp'
                    },
                    'updateMiniCartItemCount': {
                        ue: '/_ajax/update_minicart_itemcount.jsp',
                        dev: '/store/cart/cart_item_count.jsp'
                    },
                    'getSKU': {
                        ue: '/_ajax/product_sku.json',
                        dev: '/store/browse/SKU_rollup_details.jsp'
                    },
                    'getProductData': {
                        ue: '/_ajax/product_detail.jsp',
                        dev: '/store/browse/rollup_details.jsp'
                    },
                    'addItemToRegisteryStatus': {
                        ue: '/_ajax/addItemToRegisteryStatus.jsp',
                        dev: '/store/addgiftregistry/addItemToRegisteryStatus.jsp'
                    },
                    'openCreateRegistry': {
                        ue: '/_ajax/create_registry_popup.jsp',
                        dev: '/store/addgiftregistry/create_registry_popup.jsp'
                    },
                    'anncCardRequest': {
                        ue: '/_includes/modals/annc_card_request.jsp',
                        dev: '/_includes/modals/annc_card_request.jsp'
                    },
                    'productDetailsEmailAFriend': {
                        ue: '/_includes/modals/email_friend.jsp',
                        dev: '/store/browse/emailAFriend.jsp'
                    },
                    'productDetailsNotifyOOS': {
                        ue: '/_includes/modals/notify_stock.jsp',
                        dev: '/store/browse/gadgets/notifyMeRequest.jsp'
                    },
                    'announcementCards': {
                        ue: '/_includes/modals/announcement_cards.jsp',
                        dev: '/store/bbregistry/announcement_cards.jsp'
                    },
                    'emailAFriend': {
                        ue: '/_includes/modals/email_friend.jsp',
                        dev: '/store/_includes/modules/email_friend.jsp'
                    },
                    'chkGiftCardBal': {
                        ue: '/_includes/modals/check_giftcard_balance.jsp',
                        dev: '/store/account/check_giftcard_balance.jsp'
                    },
                    'addToRegNoLogin': {
                        ue: '/store/giftregistry/add_item_to_reg_no_login.jsp',
                        dev: '/store/addgiftregistry/add_item_to_reg_no_login.jsp'
                    },
                    'clearSessionBean': {
                        ue: '/store/giftregistry/clear_add_item_to_regb.jsp',
                        dev: '/store/giftregistry/clear_add_item_to_regb.jsp'
                    },
                    'openEmailCart': {
                        ue: '/store/cart/email/email_cart.jsp',
                        dev: '/store/cart/email/email_cart.jsp'
                    },
                    'openEmailSavedItems': {
                        ue: '/store/cart/email/email_saveditems.jsp',
                        dev: '/store/cart/email/email_saveditems.jsp'
                    },
                    'getDirectionsWithMap': {
                        ue: '/selfservice/get_direction_withmap.jsp',
                        dev: '/store/selfservice/store/p2p_directions.jsp'
                    },
                    'legacyPwdPopup': {
                        ue: '/_includes/modals/incorrect_password.jsp',
                        dev: '/store/account/incorrect_password.jsp'
                    },
                    'legacyPwdCheck': {
                        ue: '/_ajax/legacy_password_check.jsp',
                        dev: '/_ajax/legacy_password_check.jsp'
                    },
                    'findCollegeSelect': {
                        ue: '/_ajax/find_your_college.json',
                        dev: '/store/selfservice/frags/find_your_college_json.jsp'
                    },
                    'privacyPolicy': {
                        ue: '/_includes/modals/privacy_policy.jsp',
                        dev: '/store/account/privacy_policy.jsp'
                    },
                    'getStoreData': {
                        ue: '/_ajax/find_in_store_result.jsp',
                        dev: '/store/selfservice/find_in_store_result.jsp'
                    },
                    'captchaImagePath': {
                        ue: '/simpleCaptcha.png',
                        dev: '/store/simpleCaptcha.png'
                    },
                    'getStoreDataFavorite': {
                        ue: '/_ajax/store_info.jsp',
                        dev: '/store/selfservice/store/store_info_frag.jsp'
                    },
                    'pathToZeroClipboard': {
                        ue: '/_assets/global/flash_assets/ZeroClipboard10.swf',
                        dev: '/_assets/global/flash_assets/ZeroClipboard10.swf'
                    },
                    'BVDoLogin': {
                        ue: '/_ajax/login.jsp',
                        dev: '/store/account/login.jsp'
                    },
                    'addItemToCart': {
                        ue: '/_ajax/add_to_cart.json',
                        dev: '/store/browse/add_item_to_cart_pdp.jsp'
                    },
                    'addItemToListStatus': {
                        ue: '/_ajax/addItemToListStatus.jsp',
                        dev: '/store/browse/add_item_to_list_pdp.jsp'
                    },
                    'addItemToListStatusPostLogin': {
                        ue: '/_ajax/addItemToListStatusPostLogin.jsp',
                        dev: '/store/browse/add_item_to_list_pdp_post_login.jsp'
                    },
                    'mapQuestStaticMapContentURL': {
                        ue: '//www.mapquestapi.com/staticmap',
                        dev: '//www.mapquestapi.com/staticmap'
                    },
                    'preloaderImg': {
                        ue: '/_assets/global/images/widgets/loader.gif',
                        dev: '/_assets/global/images/widgets/loader.gif'
                    },
                    'bridalShowData': {
                        ue: '/_ajax/bridal_show_data.jsp',
                        dev: '/store/cms/bridal_template.jsp'
                    },
                    'fConnectImage': {
                        ue: '/_assets/global/images/f_connect.png',
                        dev: '/_assets/global/images/f_connect.png'
                    },
                    'fConnectImageNotAvailable': {
                        ue: '/_assets/global/images/f_connect_na.png',
                        dev: '/_assets/global/images/f_connect_na.png'
                    },
                    'validateFConnect': {
                        ue: '/_ajax/validate_fb_email_fc_Enabled_sister.json',
                        dev: '/store/account/facebook_connect.jsp'
                    },
                    'fbLoaderImg': {
                        ue: '/_assets/global/images/fb-loader.gif',
                        dev: '/_assets/global/images/fb-loader.gif'
                    },
                    'addItemToCartFromReg': {
                        ue: '/_ajax/add_to_cart_from_reg.json',
                        dev: '/store/browse/add_item_to_cart_gift.jsp'
                    },
                    'emailAPageCMS': {
                        ue: '/_includes/modals/email_friend.jsp',
                        dev: '/store/emailtemplates/emailapage/emailAPage_cms.jsp'
                    },
                    'getStoreDataFavoriteRegistry': {
                        ue: '/_ajax/store_info_no_stock_info.jsp',
                        dev: '/store/giftregistry/modals/store_info_no_stock_info.jsp'
                    },
                    'blankGIF': {
                        ue: '/_assets/global/images/blank.gif',
                        dev: '/_assets/global/images/blank.gif'
                    },
                    'getStates': {
                        ue: '/_ajax/states.json',
                        dev: '/store/checkout/billing/states.jsp'
                    },
                    'noImageFound': {
                        ue: '/_assets/global/images/no_image_available.jpg',
                        dev: '/_assets/global/images/no_image_available.jpg'
                    },
                    'migratedAccount': {
                        ue: '/_includes/modals/new_password.jsp',
                        dev: '/store/account/frags/changepasswordmodel.jsp'
                    },
                    'migratedAccountPwdCheck': {
                        ue: '/_includes/modals/new_password.jsp',
                        dev: '/store/account/frags/changepasswordmodel.jsp'
                    },
                    'bbbLogoDialog': {
                        
 
 
 
 
 
                        ue: '/_assets/global/images/logo/logo_bbb_wide.png',
                        dev: '/_assets/global/images/logo/logo_bbb_wide.png'
                    },
                    's7URL': {
                        ue: '//s7d7.scene7.com/is/image/BedBathandBeyond/',
                        dev: '//s7d9.scene7.com/is/image/BedBathandBeyond/'
                    },
                    's7MainURL': {
                        ue: '//s7d7.scene7.com/is/image/',
                        dev: '//s7d9.scene7.com/is/image/'
                    },
                    'profileURL': {
                        ue: '/store/giftregistry/droplet/profile_exist_droplet.jsp',
                        dev: '/store/giftregistry/droplet/profile_exist_droplet.jsp'
                    },
                    'wishlistMoveToCartError': {
                        ue: '/_ajax/wishlist_move_to_cart_error.jsp',
                        dev: '/store/wishlist/wishlist_move_to_cart_error.jsp'
                    },
                    'liveClickerBridalVideos': {
                        
                        ue: '//d2vxgxvhgubbj8.cloudfront.net/custom/64/search/wedding.html',
                        dev: '//d2vxgxvhgubbj8.cloudfront.net/custom/64/search/wedding.html'
                    },
                    'smallLoader': {
                        ue: '/_assets/global/images/widgets/small_loader.gif',
                        dev: '/_assets/global/images/widgets/small_loader.gif'
                    },
                    'smallLoader2': {
                        
 
                        ue: '/_assets/global/images/widgets/small_loader_by.gif',
                        dev: '/_assets/global/images/widgets/small_loader_by.gif'
                    },
                    'regFlyout': {
                        ue: '/_includes/modules/header_flyouts/bridal_gift_registry_loggedin.jsp',
                        dev: '/store/giftregistry/reg_flyout.jsp'
                    },
                    'hideBopusSearchForm': {
                        ue: '/store/selfservice/store/statusShowBopusForm.jsp',
                        dev: '/store/selfservice/store/statusShowBopusForm.jsp'
                    },
                    'getFavStoreData': {
                        dev: '/store/giftregistry/registry_fav_store.jsp',
                        ue: '/_ajax/create_reg_fav_store.jsp'
                    },
                    'regHeader': {
                        ue: '/_ajax/subheader.jsp',
                        dev: '/store/_includes/header/subheader.jsp'
                    },
                    'hhPrefCenterURL': {
                        ue: 'https://bbbprefcenter.test-harte-hanks.com',
                        dev: 'https://www.bbbprefcenter.com'
                    },
                    'mapQuestLibURL': {
                        ue: '//www.mapquestapi.com/sdk/js/v7.0.s/mqa.toolkit.js?key=Fmjtd|luu22h0yn9%2Cr5%3Do5-h0rl0',
                        dev: '//www.mapquestapi.com/sdk/js/v7.0.s/mqa.toolkit.js?key=Gmjtd%7Clu6120u8nh%2C2w%3Do5-lwt2l'
                    },
                    'shippingRestrictionPopUp': {
                        ue: '/_includes/modals/shipping_restrictions_checkout.jsp',
                        dev: '/store/_includes/modals/shipping_restrictions_checkout.jsp'
                    },
                    'cartAjaxSubmitURL': {
                        ue: '/_ajax/ajax_handler_cart.jsp',
                        dev: '/store/cart/ajax_handler_cart.jsp'
                    },
                    'sflAjaxSubmitURL': {
                        ue: '/_ajax/ajax_handler_sfl.jsp',
                        dev: '/store/_includes/modules/cart_store_pickup_form.jsp'
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
                        dev: '/store/account/cookieSecureSIDAjaxed.jsp'
                    },
                    'fromResetURL': {
                        ue: '/_includes/modals/new_password.jsp',
                        dev: '/store/account/frags/changePwdAfterResetmodel.jsp'
                    },
                    'migratedUserPopupURL': {
                        ue: '/_includes/modals/migrated_user_popup.jsp',
                        dev: '/store/account/frags/migrated_user_popup.jsp'
                    },
                    'siteTourJSP': {
                        ue: '/_ajax/siteTour.jsp',
                        dev: '/store/_includes/modals/siteTour.jsp'
                    }
                };
            </script>
	
</body>
</html>
</dsp:page>

