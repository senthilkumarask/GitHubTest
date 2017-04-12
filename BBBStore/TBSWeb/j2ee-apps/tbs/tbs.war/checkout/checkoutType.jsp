<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/com/bbb/commerce/droplet/PaypalDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/atg/dynamo/droplet/Redirect" />
	<dsp:importbean bean="/com/bbb/commerce/shipping/droplet/DisplaySingleShippingDroplet" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />

	<%-- Variable --%>
	<dsp:getvalueof var="isGuestCheckout" param="guestCheckout"/>
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<dsp:getvalueof var="orderPriceInfo" vartype="java.lang.Object" bean="ShoppingCart.current.priceInfo"/>
	<dsp:getvalueof var="fromPayPal" bean="PayPalSessionBean.address.fromPayPal"/>
	<dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
	<dsp:getvalueof var="errorList" bean="PayPalSessionBean.errorList"/>
	<dsp:getvalueof var="formExceptions" bean="BBBShippingGroupFormhandler.formExceptions"/>
	<dsp:getvalueof var="isExpress" bean="ShoppingCart.current.expressCheckOut"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="priceOverridden" param="priceOverridden" />
	<c:choose>
		<c:when test="${empty formExceptions}">
		<c:set var="isFormException" value="false"/>
		</c:when>
		<c:otherwise>
			<c:set var="isFormException" value="true"/>
		</c:otherwise>
	</c:choose>
	<c:set var="size" value="${fn:length(errorList)}"/>
	<c:choose>
		<c:when test="${size gt 0}">
			<c:set var="paypalError" value="true"/>
		</c:when>
		<c:otherwise>
			<c:set var="paypalError" value="false"/>
		</c:otherwise>
	</c:choose>
	<dsp:getvalueof var="isFromPreview" param="isFromPreview"/>
	<c:if test = "${payPalOrder eq true && isFromPreview eq true}">
		<dsp:setvalue value="true" bean = "PayPalSessionBean.fromPayPalPreview"/>
	</c:if>
	<c:if test="${orderPriceInfo == null}">
		<dsp:droplet name="RepriceOrderDroplet">
			<dsp:param value="ORDER_SUBTOTAL" name="pricingOp"/>
		</dsp:droplet>
	</c:if>
	<c:if test="${not empty priceOverridden and priceOverridden}">
		<dsp:droplet name="RepriceOrderDroplet">
			<dsp:param value="OP_REPRICE_TAX" name="pricingOp"/>
		</dsp:droplet>
	</c:if>
	<c:if test='${isGuestCheckout eq "1" }'>
		<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="SHIPPING_SINGLE"/>
		<c:set var="isMultishipCheckout" value="false" scope="session" />
	</c:if>
	<dsp:getvalueof var="currentState" bean="CheckoutProgressStates.currentLevelAsInt"/>
	<dsp:getvalueof var="state" value="${30}"/>
	<c:if test="${currentState lt state}">
		<dsp:getvalueof var="url" bean="CheckoutProgressStates.failureURL"/>
		<dsp:getvalueof var="failureURL" value="${contextPath}${url}"/>
		<dsp:droplet name="Redirect">
			<dsp:param name="url" value="${failureURL}"/>
		</dsp:droplet>
	</c:if>

	<%-- logic to display proper page --%>
	<dsp:getvalueof id="token" param="token"/>
	<c:choose>
		<c:when test="${currentState lt state or couponMap}">
			<dsp:getvalueof var="url" bean="CheckoutProgressStates.failureURL"/>
			<dsp:getvalueof var="failureURL" value="${contextPath}${url}"/>
			<dsp:droplet name="Redirect">
				<dsp:param name="url" value="${failureURL}"/>
			</dsp:droplet>
		</c:when>
		<c:when test="${(payPalOrder eq true and paypalError eq false) || (not empty token and paypalError eq false)}">
			<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="REVIEW"/>
			<dsp:droplet name="PaypalDroplet">
				<dsp:param name="order" bean="ShoppingCart.current"/>
				<dsp:param name="PayPalSessionBean" bean="PayPalSessionBean"/>
				<dsp:param name="validateShippingMethod" value="false"/>
				<dsp:param name="token" param="token"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="isShippingMethodChanged" param="isShippingMethodChanged" />
					<dsp:getvalueof var="addExist" param="addExist" />	
				</dsp:oparam>
				<dsp:oparam name="error">
					<dsp:getvalueof param="redirectUrl" var="redirectUrl" />
					<c:if test="${not empty redirectUrl}">
						<dsp:droplet name="Redirect">
							<dsp:param name="url" value="${redirectUrl}?payPalError=true" />
						</dsp:droplet>
					</c:if>
					<dsp:droplet name="IsEmpty">
					<dsp:param name="value" param="paypalAddressVO" />
					<dsp:oparam name="false">
						<dsp:getvalueof id="redirectUrl" param="paypalAddressVO.redirectUrl" />
						<c:if test="${not empty redirectUrl}">
							<dsp:droplet name="Redirect">
								<dsp:param name="url" value="${contextPath}/cart/cart.jsp" />
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
			<c:choose>
				<c:when test="${isMultishipCheckout}">
					<dsp:include page="/checkout/multi.jsp">
						<dsp:param name="paypal" value="true"/>
						<dsp:param name="addcheck" value="${addExist}"/>
					</dsp:include>
				</c:when>
				<c:otherwise>
					<dsp:include page="/checkout/single.jsp">
						<dsp:param name="paypal" value="true"/>
						<dsp:param name="addcheck" value="${addExist}"/>
					</dsp:include>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:when test="${isExpress}">
			<dsp:include page="/checkout/single.jsp"/>
		</c:when>
		<c:otherwise>
			<dsp:droplet name="Switch">
				<dsp:param name="value" param="shippingGr"/>
				<dsp:oparam name="default">
					<dsp:droplet name="DisplaySingleShippingDroplet">
						<dsp:param name="paypalError" value="${paypalError}"/>
						<dsp:param name="isFormException" value="${isFormException}"/>
						<dsp:param name="order" bean="ShoppingCart.current"/>
						<dsp:oparam name="single">
							<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="SHIPPING_SINGLE"/>
							<c:set var="isMultishipCheckout" value="false" scope="session" />
							<dsp:include page="/checkout/single.jsp"/>
						</dsp:oparam>
						<dsp:oparam name="multi">
							<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="SHIPPING_MULTIPLE"/>
							<c:set var="isMultishipCheckout" value="true" scope="session" />
							<dsp:include page="/checkout/multi.jsp"/>
						</dsp:oparam>
						<dsp:oparam name="cart">
							<dsp:droplet name="Redirect">
								<dsp:param name="url" value="${contextPath}/cart/cart.jsp"/>
							</dsp:droplet>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
				<dsp:oparam name="multi">
					<dsp:getvalueof var="cartcount" bean="ShoppingCart.current.commerceItemCount"/>
					<c:choose>
						<c:when test="${cartcount > 0}">
							<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="SHIPPING_MULTIPLE"/>
							<c:set var="isMultishipCheckout" value="true" scope="session" />
							<dsp:include page="/checkout/multi.jsp"/>
						</c:when>
						<c:otherwise>
							<dsp:droplet name="Redirect">
								<dsp:param name="url" value="${contextPath}/cart/cart.jsp"/>
							</dsp:droplet>
						</c:otherwise>
					</c:choose>
				</dsp:oparam>
			</dsp:droplet>
		</c:otherwise>
	</c:choose>

	<dsp:getvalueof var="sessionBeanNull" bean="PayPalSessionBean.sessionBeanNull"/>

	<%-- YourAmigo code starts 6/18/2013 --%>
	<c:if test="${YourAmigoON}">
		<dsp:getvalueof var="isTransient" bean="Profile.transient"/>
		<c:if test="${isTransient eq false}">
			<%--  Configuring the javascript for tracking signups (to be placed on the signup confirmation page, if any) --%>
			<c:choose>
				<c:when test="${(currentSiteId eq TBS_BuyBuyBabySite)}">
					<script src="https://support.youramigo.com/52657396/tracev2.js"></script>
					<c:set var="ya_cust" value="52657396"></c:set>
				</c:when>
				<c:when test="${(currentSiteId eq TBS_BedBathUSSite)}">
					<script src="https://support.youramigo.com/73053126/trace.js"></script>
					<c:set var="ya_cust" value="73053126"></c:set>
				</c:when>
			</c:choose>
			<script type="text/javascript">
				/* <![CDATA[ */
					/*** YA signup tracking code for Bed Bath & Beyond (www.bedbathandbeyond.com) ***/
					// --- begin customer configurable section ---
					// Set XXXXX to the ID counting the signup, or to a random value if you have no such id
					// - eg, ya_tid = Math.random();
					ya_tid = Math.floor(Math.random()*1000000);
					// Set YYYYY to the type of signup - can be blank if you have only one signup type.
					ya_pid = "";
					// Indicate that this is a signup and not a purchase
					ya_ctype = "REG";
					// --- end customer configurable section. DO NOT CHANGE CODE BELOW ---
					ya_cust = '${ya_cust}';
					try { yaConvert(); } catch(e) {}
				/* ]]> */
			</script>
		</c:if>
	</c:if>

</dsp:page>
