<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/bbb/commerce/droplet/PaypalDroplet" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean" />
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>

	<%-- Variables --%>
	<dsp:getvalueof var="contextPath" vartype="java.lang.String" bean="/OriginatingRequest.contextPath" />

	<dsp:droplet name="/com/bbb/commerce/droplet/PaypalDroplet">
		<dsp:param name="order" bean="ShoppingCart.current" />
		<dsp:param name="PayPalSessionBean" bean="PayPalSessionBean" />
		<dsp:param name="validateAddress" value="false" />
		<dsp:param name="token" param="token" />
		<dsp:oparam name="output">
			<bbb:pageContainer section="checkout">
				<div id="content" class="row">
					<div class="small-12 columns validation">
						<h1>
							<bbbl:label key="lbl_paypal_validating_Details" language="${pageContext.request.locale.language}" />
						</h1>
						<p>
							<bbbl:label key="lbl_paypal_validating_Msg" language="${pageContext.request.locale.language}" />
						</p>
					</div>
				</div>
				<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
					<dsp:param name="value" param="PayPalAddress" />
					<dsp:oparam name="false">
						<dsp:getvalueof id="address1" param="PayPalAddress.address1" />
						<dsp:getvalueof id="address2" param="PayPalAddress.address2" />
						<dsp:getvalueof id="city" param="PayPalAddress.city" />
						<dsp:getvalueof id="state" param="PayPalAddress.state" />
						<dsp:getvalueof id="postalCode" param="PayPalAddress.postalCode" />
					</dsp:oparam>
				</dsp:droplet>
				<dsp:getvalueof var="addressExistInOrder" param="addInOrder" />
				<form name="validationForm" action="" method="POST" id="validationForm">
					<input type="hidden" id="address1" name="address1" value="${address1}" autocomplete="off"/> />
					<input type="hidden" id="address2" name="address2" value="${address2}" />
					<input type="hidden" id="cityName" name="cityName" value="${city}" />
					<input type="hidden" id="stateNamePaypal" name="stateNamePaypal" value="${state}" />
					<input type="hidden" id="zipPaypal" name="zip" value="${postalCode}" />
					<input type="hidden" id="addressExistInOrder" name="addressExistInOrder" value="${addressExistInOrder}" />
				</form>
			</bbb:pageContainer>
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
	<%-- QAS Modal --%>
	<dsp:include page="/_includes/modals/qasModal.jsp" />
</dsp:page>
