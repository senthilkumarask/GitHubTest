<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:setvalue bean="ProfileFormHandler.logoutErrorURL" value="${contextPath}" />
	<dsp:setvalue bean="ProfileFormHandler.logout" value="${true}" />
</dsp:page>