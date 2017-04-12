<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>

	<%-- Variables --%>
	<dsp:getvalueof param="commerceId" var="commerceId"/>
	<dsp:getvalueof param="registryId" var="registryId"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

	<dsp:form action="${contextPath}/cart/cart.jsp" method="post" id="select">
		<dsp:input bean="CartModifierFormHandler.commerceItemId" type="hidden" value="${commerceId}"/>
		<dsp:input bean="CartModifierFormHandler.registryId" type="hidden" value="${registryId}"/>
		<dsp:input bean="CartModifierFormHandler.quantity" type="hidden" value="1"/>
		<dsp:input bean="CartModifierFormHandler.splitAndAssignRegistry" id="keepInRegistry" type="submit" value="Assign" iclass="hidden" />
		<dsp:input bean="CartModifierFormHandler.fromPage" type="hidden"
												value="regUnselect" />
		<dsp:input bean="CartModifierFormHandler.splitAndAssignRegistrySuccessUrl" value="${contextPath}/cart/cart.jsp" type="hidden"/>
		<dsp:input bean="CartModifierFormHandler.splitAndAssignRegistryErrorUrl" value="${contextPath}/cart/cart.jsp" type="hidden"/>
	</dsp:form>

</dsp:page>
