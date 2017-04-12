<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/commerce/order/purchase/CommitOrderFormHandler"/>
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/atg/commerce/order/droplet/BBBOrderRKGInfo" />

	<%-- Variables --%>
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<dsp:getvalueof var="currentState" bean="CheckoutProgressStates.currentLevelAsInt"/>
	<dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
	<dsp:getvalueof var="state" value="${80}"/>
	<c:set var="language" value="${pageContext.request.locale.language}" scope="request"/>
	<c:set var="productIds" scope="request"/>
	<dsp:droplet name="/com/bbb/commerce/order/droplet/LTLCustomItemExclusionDroplet">
		<dsp:param name="order" bean="ShoppingCart.current"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="commerceItemList" param="commerceItemList" />
			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param name="array" param="commerceItemList" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="productId" param="element" />
					<dsp:getvalueof var="count" param="count"/>
					<dsp:getvalueof var="size" param="size"/>
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
	<c:choose>
		<c:when test="${currentState lt state or couponMap}">
			<dsp:droplet name="/atg/dynamo/droplet/Redirect">
				<dsp:param name="url" bean="CheckoutProgressStates.failureURL"/>
			</dsp:droplet>
		</c:when>
		<c:otherwise>
			<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="REVIEW"/>
		</c:otherwise>
	</c:choose>
    <input type="hidden" id="checkoutProgressState" value="<dsp:valueof bean='CheckoutProgressStates.currentLevel'/>" />
	<dsp:getvalueof id="token" param="token"/>
	<c:if test = "${(payPalOrder eq true) || not empty token}">
		<dsp:droplet name="/com/bbb/commerce/droplet/PaypalDroplet">
			<dsp:param name="order" bean="ShoppingCart.current"/>
			<dsp:param name="PayPalSessionBean" bean="PayPalSessionBean"/>
			<dsp:param name="validateShippingMethod" value="true"/>
			<dsp:oparam name="output">
				<dsp:getvalueof var="isShippingMethodChanged" param="isShippingMethodChanged" />
			</dsp:oparam>
			<dsp:oparam name="error">
				<dsp:getvalueof param="redirectUrl" var="redirectUrl" />
				<c:if test="${not empty redirectUrl}">
					<dsp:droplet name="/atg/dynamo/droplet/Redirect">
						<dsp:param name="url" value="${redirectUrl}?payPalError=true" />
					</dsp:droplet>
				</c:if>
			</dsp:oparam>
			<dsp:oparam name="setExpressOutput">
				<dsp:getvalueof param="fromCart" var="fromCart" />
				<dsp:getvalueof param="payPalErrorUrl" var="payPalErrorUrl" />
				<dsp:setvalue bean="CartModifierFormHandler.fromCart" value="${fromCart}" />
				<dsp:setvalue bean="CartModifierFormHandler.payPalErrorURL" value="${contextPath}${payPalErrorUrl}" />
				<dsp:setvalue bean="CartModifierFormHandler.checkoutWithPaypal" />
			</dsp:oparam>
		</dsp:droplet>
	</c:if>

	<%-- render page --%>
	<bbb:pageContainer index="false" follow="false">

		<%-- checkout header --%>
		<jsp:attribute name="headerRenderer">
			<dsp:include page="/checkout/checkout_header.jsp" flush="true">
				<dsp:param name="pageId" value="7"/>
				<dsp:param name="step" value="preview"/>
			</dsp:include>
		</jsp:attribute>
		<jsp:attribute name="section">checkout</jsp:attribute>
		<jsp:attribute name="bodyClass">checkout review</jsp:attribute>

		<%-- checkout body --%>
		<jsp:body>
			<div class="row">
				<div class="small-12 columns">
					<%-- multiship progress bar --%>
					<dsp:getvalueof var="step" param="step"/>
					<dsp:include page="/checkout/progressBar.jsp">
						<dsp:param name="step" value="preview"/>
					</dsp:include>
				</div>
				<div class="small-12 columns">
					<%-- Error Messages --%>
					<dsp:include page="/global/gadgets/errorMessage.jsp">
						<dsp:param name="formhandler" bean="CommitOrderFormHandler"/>
					</dsp:include>
				</div>
				<div class="small-12 columns">
					<h2 class="divider">
						Preview
					</h2>
					
					<div class="row">
						<div class="small-12 columns">
							<dsp:include page="/checkout/preview/frag/checkout_review_frag.jsp" flush="true">
								<dsp:param name="order" bean="ShoppingCart.current"/>
								<dsp:param name="hideOrderNumber" value="true"/>
								<dsp:param name="showLinks" value="1"/>
								<dsp:param name="displayTax" value="true"/>
								<dsp:param value="${isShippingMethodChanged}" name="isShippingMethodChanged" />
							</dsp:include>
							 <%--KP COMMENT START: i don't see this in the comps --%>
							<%--<dsp:include page="/checkout/preview/frag/checkout_review_right_frag.jsp"></dsp:include>--%>
							<%-- KP COMMENT END --%>
						</div>
					</div>
				</div>
			</div>
            </div>
		</jsp:body>
		<jsp:attribute name="footerContent">
			<script type="text/javascript">
				if(typeof s !=='undefined') {
					s.pageName='Check Out>Preview';
					s.channel='Check Out';
					s.prop1='Check Out';
					s.prop2='Check Out';
					s.prop3='Check Out';
					s.prop6='${pageContext.request.serverName}';
					s.eVar9='${pageContext.request.serverName}';
					s.events = "event12";
					s.products = '${productIds}';
					var s_code=s.t();
					if(s_code)document.write(s_code);
				}
			</script>
		</jsp:attribute>
	</bbb:pageContainer>

</dsp:page>
