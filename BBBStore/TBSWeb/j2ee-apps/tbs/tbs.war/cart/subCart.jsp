<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
<!-- <div id="cartRightCertonaSlot" class="grid_2 alpha marTop_5"> -->
<%-- 	<dsp:include page="cart_certona_slots.jsp"> --%>
<%-- 		<dsp:param name="CertonaContext" param="CertonaContext" /> --%>
<%-- 		<dsp:param name="RegistryContext" param="RegistryContext" /> --%>
<%-- 		<dsp:param name="shippingThreshold" param="shippingThreshold"/> --%>
<%-- 	</dsp:include> --%>
<!-- </div> -->

<dsp:form method="post" action="${contextPath}/cart/cart.jsp" formid="changeToShipOnline">
	<dsp:input id="onlineSubmit" type="submit" style="display:none" bean="BBBShippingGroupFormhandler.changeToShipOnline" />
	<dsp:input id="onlineComId" type="hidden" bean="BBBShippingGroupFormhandler.commerceItemId" />
	<dsp:input id="onlineShipId" type="hidden" bean="BBBShippingGroupFormhandler.oldShippingId" />
	<dsp:input id="onlineQuantity" type="hidden" bean="BBBShippingGroupFormhandler.newQuantity" />
</dsp:form>
</dsp:page>
