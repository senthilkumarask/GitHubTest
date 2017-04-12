<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />

<dsp:getvalueof var="event" param="eventType" />
<dsp:form method="post">
	<dsp:setvalue bean="GiftRegistryFormHandler.registryEventType" value="${event}" />
	<dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="createSpecificRegistry" />
	<%-- <dsp:setvalue bean="GiftRegistryFormHandler.SuccessURL" value="/tbs/giftregistry/registry_creation_form.jsp" /> --%>
	<dsp:setvalue bean="GiftRegistryFormHandler.registryTypes"/>
</dsp:form>