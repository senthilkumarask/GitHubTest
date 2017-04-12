<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/droplet/PaypalDroplet" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean" />
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:getvalueof var="contextPath" vartype="java.lang.String" bean="/OriginatingRequest.contextPath" />
	<dsp:getvalueof var="isCart" param="isCart" />
	<dsp:getvalueof var="token" param="token" />
	<dsp:getvalueof var="PayerID" param="PayerID" />
		 
		 <%--BBBSL-6713 | Changing referrer URL in case user comes from payment page flow for Omniture --%>
	<c:choose>
		<c:when test="${isCart eq 'false'}">
			<bbb:pageContainer section="checkout">
				<div id="content" class="container_12 clearfix">
					<div class="validation">
						<h1><bbbl:label key="lbl_paypal_validating_Details" language="${pageContext.request.locale.language}" /></h1>
						<p><bbbl:label key="lbl_paypal_validating_Msg" language="${pageContext.request.locale.language}" /></p>
						<p class="ajaxLoader"></p>
					</div>
				</div>
		 		<form name="paypalRedirectPayment"  method="post" action="/store/checkout/preview/review_cart.jsp?token=${token}&PayerID=${PayerID}" id="paypalRedirectPayment">
				</form> 
			</bbb:pageContainer>
		</c:when>
		<c:otherwise>
				<dsp:droplet name="/com/bbb/commerce/droplet/PaypalDroplet">
				<dsp:param name="order" bean="ShoppingCart.current" />
				<dsp:param name="PayPalSessionBean" bean="PayPalSessionBean" />
				<dsp:param name="validateAddress" value="false" />
				<dsp:param name="token" param="token" />
				<dsp:oparam name="output">
					<bbb:pageContainer section="checkout">
						<div id="content" class="container_12 clearfix">
							<div class="validation">
								<h1><bbbl:label key="lbl_paypal_validating_Details" language="${pageContext.request.locale.language}" /></h1>
								<p><bbbl:label key="lbl_paypal_validating_Msg" language="${pageContext.request.locale.language}" /></p>
								<p class="ajaxLoader"></p>
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
						<form name="validationForm" action="#" method="POST" id="validationForm">
							<input type="hidden" id="address1" name="address1" value="${address1}" autocomplete="off"/> <input type="hidden" id="address2" name="address2" value="${address2}" /> <input type="hidden" id="cityName" name="cityName" value="${city}" /> <input type="hidden" id="stateNamePaypal" name="stateNamePaypal" value="${state}" /> <input type="hidden" id="zipPaypal" name="zip" value="${postalCode}" /> <input type="hidden" id="addressExistInOrder" name="addressExistInOrder" value="${addressExistInOrder}" />
						</form>
					</bbb:pageContainer>
				</dsp:oparam>
				<dsp:oparam name="error">
					<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
							<dsp:param name="value" param="paypalAddressVO" />
							<dsp:oparam name="false">
								<dsp:getvalueof id="redirectUrl" param="paypalAddressVO.redirectUrl" />
								<c:if test="${not empty redirectUrl}">
									<dsp:droplet name="/atg/dynamo/droplet/Redirect">
										<dsp:param name="url" value="${redirectUrl}" />
									</dsp:droplet>
								</c:if>
							</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
				<dsp:oparam name="setExpressOutput">
					<dsp:getvalueof param="fromCart" var="fromCart" />
					<dsp:getvalueof param="payPalErrorUrl" var="payPalErrorUrl" />
					<dsp:setvalue bean="CartModifierFormHandler.fromCart" value="${fromCart}" />
					<dsp:setvalue bean="CartModifierFormHandler.payPalErrorURL" value="${contextPath}${payPalErrorUrl}" />
					<dsp:setvalue bean="CartModifierFormHandler.checkoutWithPaypal" />	
				</dsp:oparam>
			</dsp:droplet>
		</c:otherwise>
	</c:choose>
</dsp:page>
