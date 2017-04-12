<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />

<dsp:getvalueof var="event" param="eventType" />
<dsp:form method="post">
	<dsp:setvalue bean="GiftRegistryFormHandler.registryEventType" value="${event}" />
	<dsp:setvalue bean="GiftRegistryFormHandler.SuccessURL" value="/store/giftregistry/simpleReg_creation_form.jsp" />
	<dsp:setvalue bean="GiftRegistryFormHandler.registryTypes"/>
</dsp:form>