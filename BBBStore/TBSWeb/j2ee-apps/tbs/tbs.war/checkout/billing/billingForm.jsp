<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/bbb/commerce/checkout/formhandler/BBBBillingAddressFormHandler" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/com/bbb/commerce/common/BBBAddressContainer" />
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/BBBBillingAddressDroplet" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />

	<%-- Variables --%>
	<dsp:getvalueof var="transient" bean="Profile.transient" />
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />

	<%-- render page --%>
	<bbb:pageContainer index="false" follow="false">

		<%-- checkout header --%>
		<jsp:attribute name="headerRenderer">
			<dsp:include page="/checkout/checkout_header.jsp" flush="true">
				<dsp:param name="step" value="billing"/>
				<dsp:param name="link" value="billing"/>
				<dsp:param name="pageId" value="5"/>
			</dsp:include>
		</jsp:attribute>
		<jsp:attribute name="section">checkout</jsp:attribute>
		<jsp:attribute name="bodyClass">checkout billing</jsp:attribute>

		<jsp:body>

			<div class="row">
				<div class="small-12 columns">
					<%-- multiship progress bar --%>
					<dsp:include page="/checkout/progressBar.jsp">
						<dsp:param name="step" value="billing"/>
					</dsp:include>
				</div>
				<div class="small-12 columns">
					<%-- error messages --%>
					<dsp:include page="/global/gadgets/errorMessage.jsp">
						<dsp:param name="formhandler" bean="BBBBillingAddressFormHandler"/>
					</dsp:include>
				</div>
				<div class="small-12 columns">
					<h2 class="divider bill no-padding-left">
						What is your Billing Address?
						<%-- <bbbl:label key="lbl_shipping_address_selection" language="${pageContext.request.locale.language}" />: --%>
					</h2>
					<div class="row">
						<dsp:form name="form" method="post" id="formShippingSingleLocation">

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
								<dsp:include page="/checkout/billing/couponInfoForBilling.jsp" />

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
								<dsp:input bean="BBBBillingAddressFormHandler.saveBillingAddress" type="submit" value="${lbl_button_next}" id="billingAddressNextBtn" iclass="small button service expand">
									<dsp:tagAttribute name="aria-checked" value="false"/>
									<dsp:tagAttribute name="aria-labelledby" value="billingAddressNextBtn"/>
									<dsp:tagAttribute name="role" value="button"/>
								</dsp:input>
							</div>

						</dsp:form>
					</div>
				</div>
			</div>

		</jsp:body>

		<jsp:attribute name="footerContent">
			<c:set var="productIds" scope="request"/>
			<dsp:droplet name="/com/bbb/commerce/order/droplet/LTLCustomItemExclusionDroplet">
				<dsp:param name="order" bean="ShoppingCart.current"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="commerceItemList" param="commerceItemList" />
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param name="array" param = "commerceItemList" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="productId" param="element" />
							<dsp:getvalueof var="count" param="count" />
							<dsp:getvalueof var="size" param="size" />
							<c:choose>
								<c:when test="${count eq size}">
									<c:set var="productIds" scope="request">
										${productIds};${productId}
									</c:set>
								</c:when>
								<c:otherwise>
									<c:set var="productIds" scope="request">
										${productIds};${productId},
									</c:set>
								</c:otherwise>
							</c:choose>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
			</dsp:droplet>
			<script type="text/javascript">
				if (typeof s !== 'undefined') {
					s.pageName = 'Check Out>Billing';
					s.channel = 'Check Out';
					s.prop1 = 'Check Out';
					s.prop2 = 'Check Out';
					s.prop3 = 'Check Out';
					s.prop6='${pageContext.request.serverName}';
					s.eVar9='${pageContext.request.serverName}';
					s.events = "event9";
					s.products = '${productIds}';
					var s_code = s.t();
					if (s_code)
						document.write(s_code);
				}
			</script>
		</jsp:attribute>

	</bbb:pageContainer>

</dsp:page>
