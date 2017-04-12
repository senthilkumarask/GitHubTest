<%@page contentType="application/json"%>
<dsp:page>
	<dsp:importbean var="ProfileFormHandler" bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:setvalue bean="ProfileFormHandler.logoutAndRefresh" />
	<json:object>
		<json:property name="success" value="${true}"/>
	</json:object>
</dsp:page>