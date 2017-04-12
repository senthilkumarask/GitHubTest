<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	

	<%-- delivery charge override override modal inputs --%>
	<dsp:form id="deliveryChargeOverrideForm" name="deliveryChargeOverrideForm" method="post">
		<dsp:input type="hidden" bean="CartModifierFormHandler.overrideId" id="commerceItemId" value="" />
		<dsp:input type="hidden" bean="CartModifierFormHandler.overridePrice" id="overridePrice" value="" />
		<dsp:input type="hidden" bean="CartModifierFormHandler.overrideQuantity" id="overrideQuantity" value="0" />
		<dsp:input type="hidden" bean="CartModifierFormHandler.reasonCode" id="reasonCode" value="" />
		<dsp:input type="hidden" bean="CartModifierFormHandler.competitor" id="competitor" value="" />
		<dsp:input bean="CartModifierFormHandler.fromPage" type="hidden"
												value="deliveryChargeOverride" />
		<%-- <dsp:input type="hidden" bean="CartModifierFormHandler.overrideSuccessURL" value="/tbs/checkout/overrides/json/deliveryChargeOverride_json.jsp" />
		<dsp:input type="hidden" bean="CartModifierFormHandler.overrideErrorURL" value="/tbs/checkout/overrides/json/deliveryChargeOverride_json.jsp" /> --%>
		<dsp:input type="submit" bean="CartModifierFormHandler.deliveryChargeOverride" id="deliveryChargeOverrideSubmit" iclass="hidden" value="Override"/>
	</dsp:form>


</dsp:page>


