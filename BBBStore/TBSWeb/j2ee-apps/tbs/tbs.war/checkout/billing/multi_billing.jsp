<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/dynamo/droplet/Redirect"/>
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/DisplayBillingDroplet"/>

	<%-- Variables --%>
	<c:set var="firstVisit" scope="session">false</c:set>
	<dsp:getvalueof var="currentState" bean="CheckoutProgressStates.currentLevelAsInt"/>
	<dsp:getvalueof var="state" value="${50}"/>
	<dsp:getvalueof param="colg" var="colg" scope="request"/>
	<dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
	<dsp:getvalueof var="isFromPreview" param="isFromPreview"/>
	<c:if test="${payPalOrder eq true && isFromPreview eq true}">
		<dsp:setvalue value="true" bean = "PayPalSessionBean.fromPayPalPreview"/>
	</c:if>
	<c:choose>
		<c:when test="${currentState lt state or couponMap}">
			<dsp:droplet name="Redirect">
				<dsp:param name="url" bean="CheckoutProgressStates.failureURL"/>
			</dsp:droplet>
		</c:when>
		<c:otherwise>
			<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="BILLING"/>
		</c:otherwise>
	</c:choose>

	<dsp:droplet name="DisplayBillingDroplet">
		<dsp:param name="order" bean="ShoppingCart.current" />
		<dsp:oparam name="displayCart">
			<dsp:droplet name="Redirect">
				<dsp:param name="url" value="/cart/cart.jsp" />
			</dsp:droplet>
		</dsp:oparam>
		<dsp:oparam name="displayBilling">
			<dsp:include page="/checkout/billing/multi_billing_form.jsp" />
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
