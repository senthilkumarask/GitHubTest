<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>

	<%-- Variables --%>
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />

	<%-- render page --%>
	<bbb:pageContainer index="false" follow="false" >

		<%-- checkout header --%>
		<jsp:attribute name="headerRenderer">
			<dsp:include page="/checkout/checkout_header.jsp" flush="true">
				<dsp:param name="step" value="shipping"/>
				<dsp:param name="link" value="single"/>
				<dsp:param name="pageId" value="6"/>
			</dsp:include>
		</jsp:attribute>
		<jsp:attribute name="section">checkout</jsp:attribute>
		<jsp:attribute name="bodyClass">checkout singleship</jsp:attribute>

		<%-- checkout body --%>
		<jsp:body>

			<div class="row">
				<div class="small-12 columns">
					<h1 class="checkout-title">Order Details</h1>

					<%-- error messages --%>
					<dsp:include page="/global/gadgets/errorMessage.jsp">
						<dsp:param name="formhandler" bean="BBBShippingGroupFormhandler"/>
					</dsp:include>

					<%-- form exceptions --%>
					<dsp:getvalueof var="formExceptions" bean="BBBShippingGroupFormhandler.formExceptions"/>
					<c:choose>
						<c:when test="${empty formExceptions}">
							<c:set var="isFormException" value="false"/>
						</c:when>
						<c:otherwise>
							<c:set var="isFormException" value="true"/>
						</c:otherwise>
					</c:choose>
				</div>
				<div class="small-12 columns">
					<h2 class="divider start">
						Where are your items going?
						<%-- <bbbl:label key="lbl_shipping_address_selection" language="${pageContext.request.locale.language}" />: --%>
					</h2>
					<div class="row">
						<dsp:form id="formShippingSingleLocation" formid="com_bbb_checkoutShippingAddress" action="${pageContext.request.requestURI}" method="post">

							<%-- shipping address --%>
							<div class="small-12 large-4 columns">
								<h3 class="checkout-title">Shipping Address</h3>
								<dsp:include page="/checkout/shipping/shippingAddressForm.jsp">
									<dsp:param name="isFormException" value="${isFormException}"/>
								</dsp:include>
							</div>

							<%-- shipping method --%>
							<div class="small-12 large-4 columns">
								<h3 class="checkout-title">Shipping Method</h3>
								<dsp:include page="/checkout/shipping/shippingMethods.jsp">
									<dsp:param name="formExceptionFlag" value="${formExceptionFlag}" />
								</dsp:include>
								<p class="p-footnote">
									<bbbl:label key="lbl_cartdetail_viewshippingcostslink" language="${pageContext.request.locale.language}" />
								</p>
							</div>

							<%-- shipping options (gift options) --%>
							<div class="small-12 large-4 columns">
								<dsp:include page="frag/singleShippingOptionsFrag.jsp">
									<dsp:param name="formExceptionFlag" value="${formExceptionFlag}" />
								</dsp:include>
							</div>

							<%-- continue button --%>
							<div class="small-12 columns">
								<%-- make sure this is disabled if we're not ready to continue --%>
								<%-- <div class="button button_active button_disabled"> --%>
								<c:set var="lbl_button_next" scope="page">
									CONTINUE
									<%--<bbbl:label key="lbl_shipping_button_next" language="${pageContext.request.locale.language}" />--%>
								</c:set>
								<dsp:input type="submit" value="${lbl_button_next}" iclass="small button service right" id="submitShippingSingleLocationBtn" bean="BBBShippingGroupFormhandler.addShipping">
									<dsp:tagAttribute name="aria-pressed" value="false"/>
									<dsp:tagAttribute name="aria-labelledby" value="submitShippingSingleLocationBtn"/>
									<dsp:tagAttribute name="role" value="button"/>
								</dsp:input>
								<%-- </div> --%>
							</div>

						</dsp:form>
					</div>

					<%-- order summary (i think this is 'preview your order' now) --%>
					<dsp:include page="/checkout/order_summary_frag.jsp">
						<dsp:param name="displayTax" value="false"/>
						<dsp:param name="displayShippingDisclaimer" value="true"/>
					</dsp:include>

				</div>
			</div>

		</jsp:body>

		<jsp:attribute name="footerContent">
			<c:set var="productIds" scope="request"/>
			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param name="array" bean="/atg/commerce/ShoppingCart.current.commerceItems" />
				<dsp:oparam name="output">
				<dsp:getvalueof var="count" param="count"/>
				<dsp:getvalueof var="size" param="size"/>
				<dsp:getvalueof var="productId" param="element.auxiliaryData.productId" />
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
			<script type="text/javascript">
				if (typeof s !=='undefined') {
					s.pageName='Check Out>Shipping';
					s.channel='Check Out';
					s.prop1='Check Out';
					s.prop2='Check Out';
					s.prop3='Check Out';
					s.prop6='${pageContext.request.serverName}';
					s.eVar9='${pageContext.request.serverName}';
					s.events="scCheckout,event8";
					s.products='${productIds}';
					var s_code=s.t();
					if(s_code)document.write(s_code);
				}
			</script>
		</jsp:attribute>

	</bbb:pageContainer>

</dsp:page>


