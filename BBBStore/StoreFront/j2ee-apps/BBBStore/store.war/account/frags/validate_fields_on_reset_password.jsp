<dsp:page>
    <dsp:importbean bean="/com/bbb/account/ValidateResetPasswordLoginDroplet" />
<dsp:getvalueof var="userEmailId" param="userEmailId" />
	<dsp:droplet name="ValidateResetPasswordLoginDroplet">
		<dsp:param value="${userEmailId}" name="forgetEmail" />
   		<dsp:oparam name="output">
   			<dsp:getvalueof var="firstName" param="firstName" />
   			<dsp:getvalueof var="lastName" param="lastName" />
   		</dsp:oparam>
   	</dsp:droplet>
	<json:object>
		<json:property name="firstName" value="${firstName}"></json:property>
		<json:property name="lastName" value="${lastName}"></json:property>		
	</json:object> 
</dsp:page>