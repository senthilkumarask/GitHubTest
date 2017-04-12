<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

	<%-- Variables --%>
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<dsp:getvalueof var="paypal" param="paypal" />
	<dsp:getvalueof var="addcheck" param="addcheck" />
	<c:if test="${not empty paypal && paypal eq 'true'}">
		<dsp:droplet name="RepriceOrderDroplet">
			<dsp:param value="OP_REPRICE_TAX" name="pricingOp"/>
		</dsp:droplet>
	</c:if>
	<%-- render page --%>
	<bbb:pageContainer index="false" follow="false" >

		<%-- checkout header --%>
		<jsp:attribute name="headerRenderer">
			<dsp:include page="/checkout/checkout_header.jsp" flush="true">
				<dsp:param name="step" value="shipping"/>
				<dsp:param name="link" value="multiple"/>
				<dsp:param name="pageId" value="6"/>
				<c:if test="${not empty paypal && paypal eq 'true'}">
					<dsp:param name="ordertype" value="paypal"/>
				</c:if>
			</dsp:include>
		</jsp:attribute>
		<jsp:attribute name="section">checkout</jsp:attribute>
		<%--
		<jsp:attribute name="pageWrapper">billing shippingWrapper useMapQuest multiShippingPage useStoreLocator</jsp:attribute>
		<jsp:attribute name="PageType">MultiShipping</jsp:attribute>
		--%>
		<jsp:attribute name="bodyClass">checkout multiship</jsp:attribute>

		<%-- checkout body --%>
		<jsp:body>
			<div class="checkoutWrapper">
				<div class="row">
					<%-- multiship progress bar --%>
					<div class="small-12 columns">
						<dsp:include page="/checkout/progressBar.jsp">
							<dsp:param name="paypal" param="paypal"/>
						</dsp:include>
	
						<%-- single page checkout step --%>
						<%-- currentStep: current step (used for edit button links) --%>
						<%-- nextStep: next INCOMPLETE step (used for edit button links) --%>
						<c:set var='addCheckBool' value='false' scope='request' />
					     <c:if test="${addcheck == true and paypal}">
								<c:set var='addCheckBool' value='true' scope='request' />							
							</c:if>
						<input type="hidden" id="currentStep" value="shipping" />
						<input type="hidden" id="shippingComplete" value="${addCheckBool}" />
						<input type="hidden" id="isGift" value="false" />
						<input type="hidden" id="giftingComplete" value="false" />
						<input type="hidden" id="billingComplete" value="${addCheckBool}" />
						<input type="hidden" id="paymentComplete" value="${addCheckBool}" />
						<%-- checkoutProgressState used for state machine flow --%>
						<input type="hidden" id="checkoutProgressState" value="<dsp:valueof bean='CheckoutProgressStates.currentLevel'/>" />
					</div>
	
					<%-- shipping --%>
					<div class="small-12 columns">
						<dsp:include page="/checkout/shipping/multi_shipping.jsp">
							<dsp:param name="order" bean="ShoppingCart.current"/>
						</dsp:include>
					</div>
	
					<%-- gifting --%>
					<div class="small-12 columns">
						<dsp:include page="/checkout/gifting/multi_gifting.jsp">
							<dsp:param name="order" bean="ShoppingCart.current"/>
						</dsp:include>
					</div>
	
					<%-- billing --%>
					<div class="small-12 columns">
						<dsp:include page="/checkout/billing/multi_billing.jsp">
							<dsp:param name="order" bean="ShoppingCart.current"/>
						</dsp:include>
					</div>
	
					<%-- payment --%>
					<div class="small-12 columns">
						<dsp:include page="/checkout/payment/multi_payment.jsp">
							<dsp:param name="order" bean="ShoppingCart.current"/>
						</dsp:include>
					</div>
	
					<%-- preview --%>
					<div class="small-12 columns">
						<dsp:include page="/checkout/preview/multi_preview.jsp" flush="true">
							<dsp:param name="order" bean="ShoppingCart.current"/>
							<dsp:param name="hideOrderNumber" value="true"/>
							<dsp:param name="displayTax" value="true"/>
							<dsp:param name="isConfirmation" value="false"/>
							<dsp:param name='addCheck' value="${addCheckBool}" />
							<dsp:param value="${isShippingMethodChanged}" name="isShippingMethodChanged" />
						</dsp:include>
						<c:if test="${addcheck == true and paypal}">
							<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="REVIEW"/>
						</c:if>
					</div>
				</div>
	
				<%-- QAS Modal --%>
				<dsp:include page="/_includes/modals/qasModal.jsp" />
				<dsp:include page="/cart/cart_includes/store_pickup_form.jsp"/>
			</div>
		</jsp:body>

		<jsp:attribute name="footerContent">
			<c:set var="productIds" scope="request"/>
			<dsp:droplet name="ForEach">
				<dsp:param name="array" bean="ShoppingCart.current.commerceItems" />
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


