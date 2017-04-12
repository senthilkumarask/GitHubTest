<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/bbb/commerce/checkout/formhandler/BBBBillingAddressFormHandler" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/com/bbb/commerce/common/BBBAddressContainer" />
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/BBBBillingAddressDroplet" />
	<dsp:importbean bean="/atg/multisite/Site" />

	<%-- Variables --%>
	<dsp:getvalueof var="transient" bean="Profile.transient" />
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />

	<div class="row checkout-panel hidden" id="billingForm">

		<div class="small-12 columns">
			<h2 class="divider">
				What is your Billing Address?
				<%-- <bbbl:label key="lbl_shipping_address_selection" language="${pageContext.request.locale.language}" />: --%>
			</h2>
		</div>

		<%-- errors --%>
		<div class="small-12 columns backend-errors" id="billErrors">
			<dsp:include page="/global/gadgets/errorMessage.jsp">
				<dsp:param name="formhandler" bean="BBBBillingAddressFormHandler"/>
			</dsp:include>
		</div>

		<dsp:form name="form" method="post" id="formBillingMultipleLocation">

			<%-- billing address forms --%>
			<div class="small-12 large-8 columns">
				<dsp:droplet name="BBBBillingAddressDroplet">
					<dsp:param name="order" bean="ShoppingCart.current" />
					<dsp:param name="profile" bean="Profile" />
					<dsp:param name="billingAddrContainer" bean="BBBAddressContainer" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="selectedAddrKey" param="selectedAddrKey"/>
						<dsp:getvalueof var="addresses" param="addresses"/>
					</dsp:oparam>
				</dsp:droplet>
				<c:set var="size" value="${fn:length(addresses)}"/>

				<h3 class="checkout-title">Billing Address</h3>
				<p class="p-footnote">
					<c:choose>
						<c:when test="${payPalOrder eq true && size ne null}">
							<c:choose>
								<c:when test = "${size eq 1 }">
									<bbbl:label key="lbl_paypal_one_billing_address_title" language="${pageContext.request.locale.language}" />
								</c:when>
								<c:otherwise>
									<bbbl:label key="lbl_paypal_billing_address_title" language="${pageContext.request.locale.language}" />
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<bbbl:label key="lbl_billing_address_match" language="${pageContext.request.locale.language}" />
						</c:otherwise>
					</c:choose>
				</p>
				<dsp:include page="/checkout/billing/addNewBillingAddress.jsp" >
					<dsp:param name="selectedAddrKey" value="${selectedAddrKey}"/>
					<dsp:param name="addresses" value="${addresses}"/>
				</dsp:include>
			</div>

			<%-- billing coupons --%>
			<div class="small-12 large-4 columns">
				<%--- RM# 30395 --%>
				<div id="emailPhone">
					<dsp:include page="/checkout/billing/couponInfoForBilling.jsp" />
				</div>
				<%--Changes for BBBSL-2662-Start--%>
				<c:if test="${transient == 'true'}">
					<c:set var="isChecked">
						<dsp:valueof bean="BBBBillingAddressFormHandler.emailChecked" />
					</c:set>
					<label class="inline-rc checkbox" for="emailSignUp">
						<c:choose>
							<c:when test="${isChecked eq 'checked'}">
								<dsp:input type="checkbox" checked="true" name="emailSignUp" id="emailSignUp" bean="BBBBillingAddressFormHandler.emailSignUp" />
							</c:when>
							<c:otherwise>
								<dsp:input type="checkbox" checked="false" name="emailSignUp" id="emailSignUp" bean="BBBBillingAddressFormHandler.emailSignUp" />
							</c:otherwise>
						</c:choose>
						<span></span>
						<bbbl:label key="lbl_subscribe_email_billing" language="<c:out param='${language}'/>" />
					</label>
				</c:if>
				<%--Changes for BBBSL-2662-End--%>
			</div>

			<%-- continue button --%>
			<div class="small-12 large-offset-10 large-2 columns">
				<c:set var="lbl_button_next" scope="page">
					<bbbl:label key="lbl_button_next" language="${pageContext.request.locale.language}" />
				</c:set>
<%-- 				<a class="small button service expand" id="billingAddressNextBtnTrigger"><c:out value="${lbl_button_next}"/></a> --%>
				<dsp:input bean="BBBBillingAddressFormHandler.saveBillingAddress" type="submit" value="${lbl_button_next}" id="billingAddressNextBtn" iclass="small button service expand">
					<dsp:tagAttribute name="aria-checked" value="false"/>
					<dsp:tagAttribute name="aria-labelledby" value="billingAddressNextBtn"/>
					<dsp:tagAttribute name="role" value="button"/>
				</dsp:input>
			</div>

		</dsp:form>
	</div>

</dsp:page>
