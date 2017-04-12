<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/BBBPackNHoldDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/shipping/droplet/DisplayShippingAddress"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/com/bbb/commerce/checkout/ShippingAddContainer"/>

	<%-- Variables --%>
	<dsp:getvalueof param="isFormException" var="isFormException"/>
	<c:set var="isChecked" value="false" scope="request" />
	<dsp:getvalueof var="paypal" param="isPaypal" />
<%-- <bbbl:label key="lbl_shipping_address_selection" language="${pageContext.request.locale.language}" /> --%>

	<dsp:droplet name="BBBPackNHoldDroplet">
		<dsp:param name="order" bean="ShoppingCart.current"/>
		<dsp:oparam name="output">
			<dsp:getvalueof param="isPackHold" var="isPackHold"/>
		</dsp:oparam>
	</dsp:droplet>

	<c:out value="${radioIsChecked}" />

	<%-- shipping address form --%>
	<%-- need to put the saved-shipping-addresses container out here because if there is no output
		(saved addresses) it won't render saved-shipping addresses and it messes up the ajax checkout.
		it would be good if there was a "start" oparam, but there isn't. so this works --%>
	<div class="saved-shipping-addresses">
	<dsp:droplet name="DisplayShippingAddress">
		<dsp:param name="profile" bean="Profile"/>
		<dsp:param name="order" bean="ShoppingCart.current"/>
		<dsp:param name="addressContainer" bean="ShippingAddContainer"/>
		<dsp:param name="isPackHold" value="${isPackHold}"/>
		<dsp:oparam name="output">
			<dsp:getvalueof param="cmo" var="cmo" scope="request"/>
			<dsp:getvalueof param="kirsch" var="kirsch" scope="request"/>
			<dsp:include page="/checkout/shipping/displayRegistry.jsp"/>
			<dsp:include page="/checkout/addressDisplay.jsp"/>
		</dsp:oparam>
		<dsp:oparam name="end">
			</div>
			<dsp:getvalueof var="beddingShipAddrVO" param="collegeAddress"/>
			<dsp:include page="/checkout/newAddress.jsp">
				<dsp:param name="beddingShipAddrVO" value="${beddingShipAddrVO}"/>
				<dsp:param name="isFormException" value="${isFormException}"/>
				<dsp:param name="paypal" value="${paypal}"/>
			</dsp:include>
		</dsp:oparam>
	</dsp:droplet>

	<%-- new college address --%>
	<c:if test="${beddingShipAddrVO != null || isPackHold eq true}">
		<dsp:include page="/checkout/newCollegeAddress.jsp"/>
	</c:if>

</dsp:page>
