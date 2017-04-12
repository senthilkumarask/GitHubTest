<dsp:page>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
    <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
    <dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <c:set var="pageWrapper" value="login myAccount useFB" scope="request" />
    <c:set var="pageNameFB" value="guestLogin" scope="request" />
    <dsp:getvalueof var="transientUser" bean="Profile.transient"/>
    <dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
    <dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	<c:set var="BedBathUSSite"><bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" /></c:set>
	<c:set var="BuyBuyBabySite"><bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" /></c:set>
	<c:set var="BedBathCanadaSite"><bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" /></c:set>
	<dsp:getvalueof var="emid" param="emid"/>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<div id="newCustomer" class="grid_4 alpha fbConnectWrap">
		<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="GUEST"/>
		<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
		<c:set var="lbl_checkout_checkoutasguest"><bbbl:label key="lbl_checkout_checkoutasguest" language="${language}"></bbbl:label></c:set>
		<div class="grid_9">
			<p class="error cb fcConnectErrorMsg hidden"></p>
			<div class="grid_4 alpha" id="newCustomer">
				<h3><bbbl:label key="lbl_checkout_newcustomers" language="<c:out param='${language}'/>"/></h3>
				<p class="bold">${lbl_checkout_checkoutasguest}</p>
				<p><bbbl:label key="lbl_checkout_guestcheckouthint" language="<c:out param='${language}'/>"/></p>
				<div class="button button_active">		
						<dsp:form id="signIn" action="${contextPath}/account/login.jsp?checkout=true" method="post">
							<input type="hidden" name="guestCheckout" id="guestCheckout" value="1"/>
							<dsp:getvalueof var ="securityStatus" bean="Profile.securityStatus"/>
							<c:if test="${securityStatus eq 2 }">
								<dsp:input bean="ProfileFormHandler.redirectURL" type="hidden" value="${redirectURL}"/>
							    	<dsp:input bean="ProfileFormHandler.value.guestCheckout" type="hidden" value="true"/>
								<dsp:input bean="ProfileFormHandler.removeItemsFromOrderAndLogOut" type="submit" value="${lbl_checkout_checkoutasguest}" disabled="true" iclass="enableOnDOMReady">
								</dsp:input>
							</c:if>
						</dsp:form>
				</div>
			</div>
		</div>
	</div>
</dsp:page>