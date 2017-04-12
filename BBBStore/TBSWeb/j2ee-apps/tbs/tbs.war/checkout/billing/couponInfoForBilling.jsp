<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/bbb/commerce/checkout/formhandler/BBBBillingAddressFormHandler" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/com/bbb/common/TBSSessionComponent"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" />

	<%-- Variables --%>
	<dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
	<dsp:getvalueof var="transient" bean="Profile.transient" />

	<h3 class="checkout-title">
		<bbbl:label key="lbl_contact_info_heading" language="${pageContext.request.locale.language}" />
	</h3>

	<dsp:getvalueof var="userEmail" bean="ShoppingCart.current.billingAddress.email"/>
	<c:if test="${empty userEmail}">
		<dsp:getvalueof bean="TBSSessionComponent.emailId" var="userEmail"/>
	</c:if>
	<c:if test="${empty userEmail}">
		<dsp:getvalueof var="userEmail" bean="Profile.email"/>
	</c:if>
	<c:set var="placeholderText"><bbbl:label key="lbl_cart_email_email" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="default_email_no_email_exists"><bbbl:label key="lbl_default_email_no_email_exists" language="${pageContext.request.locale.language}" /></c:set>
	<%--R2.2 Story - AY - Disable field if order is of type Paypal and continue to paypal is selected --%>
	<c:choose>
		<c:when test="${payPalOrder eq true}">
			<dsp:input type="text" name="email" bean="BBBBillingAddressFormHandler.billingAddress.email" value="${userEmail}" id="email" required="true">
				<dsp:tagAttribute name="placeholder" value="${placeholderText} *"/>
				<dsp:tagAttribute name="aria-required" value="true"/>
				<dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
				<dsp:tagAttribute name="data-paypalEmail" value="${userEmail}"/>
			</dsp:input>
		</c:when>
		<c:otherwise>
			<dsp:input type="text" name="email" bean="BBBBillingAddressFormHandler.billingAddress.email" value="${userEmail}" id="email" required="true">
				<dsp:tagAttribute name="placeholder" value="${placeholderText} *"/>
				<dsp:tagAttribute name="aria-required" value="true"/>
				<dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
			</dsp:input>
		</c:otherwise>
	</c:choose>

	<dsp:getvalueof var="conEmail" bean="BBBBillingAddressFormHandler.confirmedEmail"/>
	<c:if test="${empty conEmail}">
		<c:set var="conEmail" value="${userEmail}" />
	</c:if>
	<c:set var="placeholderText"><bbbl:label key="lbl_checkout_email_confirm" language="${pageContext.request.locale.language}" /></c:set>
	<%--R2.2 Story - AY - Disable field if order is of type Paypal and continue to paypal is selected --%>
	<c:choose>
		<c:when test="${payPalOrder eq true}">
			<dsp:input type="text" name="emailConfirm" bean="BBBBillingAddressFormHandler.confirmedEmail" value="${conEmail}" id="emailConfirm">
				<dsp:tagAttribute name="placeholder" value="${placeholderText} *"/>
				<dsp:tagAttribute name="aria-required" value="true"/>
				<dsp:tagAttribute name="aria-labelledby" value="lblemailConfirm erroremailConfirm"/>
				<dsp:tagAttribute name="data-paypalConfEmail" value="${conEmail}"/>
			</dsp:input>
		</c:when>
		<c:otherwise>
			<dsp:input type="text" name="emailConfirm" bean="BBBBillingAddressFormHandler.confirmedEmail" value="${conEmail}" id="emailConfirm">
				<dsp:tagAttribute name="placeholder" value="${placeholderText} *"/>
				<dsp:tagAttribute name="aria-required" value="true"/>
				<dsp:tagAttribute name="aria-labelledby" value="lblemailConfirm erroremailConfirm"/>
			</dsp:input>
		</c:otherwise>
	</c:choose>

	<dsp:getvalueof var="mobileNumber" bean="ShoppingCart.current.billingAddress.mobileNumber"/>
	<c:if test="${empty mobileNumber}">
		<dsp:getvalueof bean="TBSSessionComponent.mobileNumber" var="mobileNumber"/>	
	</c:if>
	<c:if test="${empty mobileNumber && not transient}">
		<dsp:getvalueof var="mobileNumber" bean="Profile.phoneNumber"/>
	</c:if>
	<c:if test="${empty mobileNumber && not transient}"> 
		<dsp:getvalueof var="mobileNumber" bean="Profile.mobileNumber"/>
	</c:if>
	<c:set var="placeholderText"><bbbl:label key="lbl_checkout_phone" language="${pageContext.request.locale.language}" /></c:set>
	<%--R2.2 Story - AY - Disable field if order is of type Paypal and continue to paypal is selected --%>
	<c:choose>
		<c:when test="${payPalOrder eq true}">
			<dsp:input type="text" id="basePhoneFull" name="basePhoneFull" iclass="phoneField required" bean="BBBBillingAddressFormHandler.billingAddress.mobileNumber" value="${mobileNumber}">
				<dsp:tagAttribute name="placeholder" value="${placeholderText} *"/>
				<dsp:tagAttribute name="maxlength" value="10"/>
				<dsp:tagAttribute name="aria-required" value="true"/>
				<dsp:tagAttribute name="paypalMobile" value="${mobileNumber}"/>
				<dsp:tagAttribute name="aria-labelledby" value="lblbasePhoneFull errorbasePhoneFull"/>
			</dsp:input>
		</c:when>
		<c:otherwise>
			<dsp:input type="text" id="basePhoneFull" name="mobileNumber" iclass="phoneField required" bean="BBBBillingAddressFormHandler.billingAddress.mobileNumber" value="${mobileNumber}">
				<dsp:tagAttribute name="placeholder" value="${placeholderText} *"/>
				<dsp:tagAttribute name="maxlength" value="10"/>
				<dsp:tagAttribute name="aria-required" value="false"/>
				<dsp:tagAttribute name="aria-labelledby" value="lblbasePhoneFull errorbasePhoneFull"/>
			</dsp:input>
		</c:otherwise>
	</c:choose>
	<c:if test="${transient == 'true'}">
		<span></span>
		<label class="inline-rc checkbox" for="noEmailExists">
			<input type="checkbox" id="noEmailExists" name="noEmailExists" value="true" data-email="${default_email_no_email_exists}">
			<span></span>
			I do not have an email address
		</label>
	</c:if>
</dsp:page>
