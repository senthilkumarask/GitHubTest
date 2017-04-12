<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>

	<%-- Variable --%>
	<dsp:getvalueof var="isGuestCheckout" param="guestCheckout"/>
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<dsp:getvalueof var="orderPriceInfo" vartype="java.lang.Object" bean="ShoppingCart.current.priceInfo"/>
	<dsp:getvalueof var= "fromPayPal" bean = "PayPalSessionBean.address.fromPayPal"/>
	<dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
	<dsp:getvalueof var= "errorList" bean = "PayPalSessionBean.errorList"/>
	<dsp:getvalueof var="formExceptions" bean="BBBShippingGroupFormhandler.formExceptions"/>
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
	<c:if test='${isGuestCheckout eq "1" }'>
		<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="SHIPPING_SINGLE"/>
	</c:if>
	<dsp:getvalueof var="currentState" bean="CheckoutProgressStates.currentLevelAsInt"/>
	<dsp:getvalueof var="state" value="${30}"/>
	<c:if test="${currentState lt state}">
		<dsp:droplet name="/atg/dynamo/droplet/Redirect">
			<dsp:param name="url" bean="CheckoutProgressStates.failureURL"/>
		</dsp:droplet>
	</c:if>

	<%-- logic to display proper page --%>
	<c:choose>
		<c:when test="${currentState lt state or couponMap}">
			<dsp:droplet name="/atg/dynamo/droplet/Redirect">
				<dsp:param name="url" bean="CheckoutProgressStates.failureURL"/>
			</dsp:droplet>
		</c:when>
		<c:when test="${fromPayPal eq true}">
			<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="SHIPPING_SINGLE"/>
			<dsp:include page="/checkout/shipping/singleShipping.jsp"/>
		</c:when>
		<c:otherwise>
			<dsp:droplet name="Switch">
				<dsp:param name="value" param="shippingGr"/>
				<dsp:oparam name="default">
					<dsp:droplet name="/com/bbb/commerce/shipping/droplet/DisplaySingleShippingDroplet">
						<dsp:param name="paypalError" value="${paypalError}"/>
						<dsp:param name="isFormException" value="${isFormException}"/>
						<dsp:param name="order" bean="/atg/commerce/ShoppingCart.current"/>
						<dsp:oparam name="single">
							<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="SHIPPING_SINGLE"/>
							<dsp:include page="/checkout/shipping/singleShipping.jsp"/>
						</dsp:oparam>
						<dsp:oparam name="multi">
							<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="SHIPPING_MULTIPLE"/>
							<dsp:include page="/checkout/shipping/multiShipping.jsp"/>
						</dsp:oparam>
						<dsp:oparam name="cart">
							<dsp:droplet name="/atg/dynamo/droplet/Redirect">
								<dsp:param name="url" value="/cart/cart.jsp"/>
							</dsp:droplet>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
				<dsp:oparam name="multi">
					<dsp:getvalueof var="cartcount" bean="/atg/commerce/ShoppingCart.current.commerceItemCount"/>
					<c:choose>
						<c:when test="${cartcount > 0}">
							<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="SHIPPING_MULTIPLE"/>
							<dsp:include page="/checkout/shipping/multiShipping.jsp"/>
						</c:when>
						<c:otherwise>
							<dsp:droplet name="/atg/dynamo/droplet/Redirect">
								<dsp:param name="url" value="/cart/cart.jsp"/>
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
		<dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
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
