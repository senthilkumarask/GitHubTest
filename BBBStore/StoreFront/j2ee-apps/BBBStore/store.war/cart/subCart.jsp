<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
<div id="cartRightCertonaSlot" class="grid_2 alpha marTop_5">

			<dsp:getvalueof var="cert_CertonaContext" param="CertonaContext"/>
			<dsp:getvalueof var="cert_RegistryContext" param="RegistryContext"/>
			<dsp:getvalueof var="cert_shippingThreshold" param="shippingThreshold"/>
			<c:set var="cert_BazarVoiceOn" scope="request" value="${BazaarVoiceOn}"></c:set>
			<c:set var="cert_registryId" scope="request" value="${registryId}"></c:set>
			<dsp:getvalueof var="cert_recomSkuId" param="recomSkuId"/>
			
			<div class="certonaProducts clearfix">
		<%-- AJAX Call to get the response from Certona --%>
				<div class="clearfix loadAjaxContent" 
                            data-ajax-url="/store/cart/cart_certona_slots.jsp" 
                            data-ajax-params-count="7" 
                            data-ajax-param1-name="CertonaContext" data-ajax-param1-value="${cert_CertonaContext}" 
                            data-ajax-param2-name="RegistryContext" data-ajax-param2-value="${cert_RegistryContext}" 
                            data-ajax-param3-name="shippingThreshold" data-ajax-param3-value="${cert_shippingThreshold}" 
                            data-ajax-param4-name="BazaarVoiceOn" data-ajax-param4-value="${cert_BazarVoiceOn}"
                            data-ajax-param5-name="registryId" data-ajax-param5-value="${cert_registryId}"
                            data-ajax-param6-name="certonaSwitch" data-ajax-param6-value="${CertonaOn}" 
							data-ajax-param7-name="recomSkuId" data-ajax-param7-value="${cert_recomSkuId}" 
							data-ajax-param8-name="isinternationalcustomer" data-ajax-param8-value="${isInternationalCustomer}">
                            <div class="grid_12 clearfix"><img width="20" height="20" alt="small loader" src="/_assets/global/images/widgets/small_loader.gif" /></div>
               </div>
    		</div>
</div>

<dsp:form method="post" action="${contextPath}/cart/cart.jsp" formid="changeToShipOnline">
	<dsp:input id="onlineSubmit" type="submit" style="display:none" bean="BBBShippingGroupFormhandler.changeToShipOnline" />
	<dsp:input id="onlineComId" type="hidden" bean="BBBShippingGroupFormhandler.commerceItemId" />
	<dsp:input id="onlineShipId" type="hidden" bean="BBBShippingGroupFormhandler.oldShippingId" />
	<dsp:input id="onlineQuantity" type="hidden" bean="BBBShippingGroupFormhandler.newQuantity" />
</dsp:form>
</dsp:page>