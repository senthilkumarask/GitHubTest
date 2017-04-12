<dsp:page>	
	<dsp:importbean	bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler" />
	
	
	<dsp:form action="/tbs/cart/cart.jsp" method="post" id="changeToOnlineForm">
		<dsp:input bean="BBBShippingGroupFormhandler.commerceItemId" id="commerceItemId" type="hidden"/>
		<dsp:input bean="BBBShippingGroupFormhandler.oldShippingId" id="ShipId"  type="hidden"/>
		<dsp:input bean="BBBShippingGroupFormhandler.newQuantity" id="qty"  type="hidden"/>
		<dsp:input bean="BBBShippingGroupFormhandler.storeId" id="storeId"  type="hidden"/>	
		<dsp:input bean="BBBShippingGroupFormhandler.successURL" type="hidden" id="sucessUrl"/>
		<dsp:input bean="BBBShippingGroupFormhandler.errorURL" type="hidden" id="sucessUrl"/>
		<dsp:input bean="BBBShippingGroupFormhandler.changeToShipOnline" type="submit" id="changeToShipOnlineSubmit" iclass="hidden"/>
	</dsp:form>
	
	<dsp:form action="/tbs/cart/cart.jsp" method="post" id="changeToStoreForm">
		<dsp:input bean="BBBShippingGroupFormhandler.commerceItemId" id="commerceItemId" type="hidden"/>
		<dsp:input bean="BBBShippingGroupFormhandler.oldShippingId" id="ShipId"  type="hidden"/>
		<dsp:input bean="BBBShippingGroupFormhandler.newQuantity" id="qty"  type="hidden"/>
		<dsp:input bean="BBBShippingGroupFormhandler.storeId" id="storeId"  type="hidden"/>	
		<dsp:input bean="BBBShippingGroupFormhandler.successURL" id="sucessUrl" type="hidden"/>
		<dsp:input bean="BBBShippingGroupFormhandler.errorURL" id="sucessUrl" type="hidden"/>
		<dsp:input bean="BBBShippingGroupFormhandler.changeToStorePickup" type="submit" id="changeToStorePickup" iclass="hidden"/>
	</dsp:form>
	
</dsp:page>
