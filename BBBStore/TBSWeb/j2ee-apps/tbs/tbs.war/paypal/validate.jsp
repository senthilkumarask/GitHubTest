<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/bbb/commerce/droplet/PaypalDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />

	<%-- Variables --%>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="qasVerified" param="qasVerified" />
	<dsp:getvalueof var="address1" param="address1" />
	<dsp:getvalueof var="address2" param="address2" />
	<dsp:getvalueof var="cityName" param="cityName" />
	<dsp:getvalueof var="stateName" param="stateName" />
	<dsp:getvalueof var="zip" param="zip" />
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />

	<c:choose>
		<c:when test="${currentSiteId eq 'BedBathUS'}">
			<c:set var="QASOn"><tpsw:switch tagName="QASTag_us"/></c:set>
		</c:when>
		<c:when test="${currentSiteId eq 'BuyBuyBaby'}">
			<c:set var="QASOn"><tpsw:switch tagName="QASTag_baby"/></c:set>
		</c:when>
		<c:otherwise>
			<c:set var="QASOn"><tpsw:switch tagName="QASTag_ca"/></c:set>
		</c:otherwise>
	</c:choose>

	<json:object>
		<dsp:droplet name="/com/bbb/commerce/droplet/PaypalDroplet">
			<dsp:param name="order" bean="ShoppingCart.current"/>
			<dsp:param name="PayPalSessionBean" bean="PayPalSessionBean"/>
			<dsp:param name="validateAddress" value="true"/>
			<dsp:param name="qasVerified" value="${qasVerified}"/>
			<dsp:param name="address1" value="${address1}"/>
			<dsp:param name="address2" value="${address2}"/>
			<dsp:param name="cityName" value="${cityName}"/>
			<dsp:param name="stateName" value="${stateName}"/>
			<dsp:param name="zipCode" value="${zip}"/>
			<dsp:oparam name="addressOutput">
				<dsp:setvalue bean="CheckoutProgressStates.currentLevel" paramvalue="redirectState"/>
				<dsp:getvalueof var="success" param="success" />
				<json:property name="success" value="${success}"></json:property>
				<dsp:getvalueof var="redirectUrl" param="redirectUrl" />
				<c:if test="${not empty redirectUrl}">
					<json:property name="redirectUrl" value="${contextPath}${redirectUrl}"></json:property>
				</c:if>
				<json:property name="qasOn" value="${QASOn}"></json:property>
			</dsp:oparam>
		</dsp:droplet>
	</json:object>

</dsp:page>
