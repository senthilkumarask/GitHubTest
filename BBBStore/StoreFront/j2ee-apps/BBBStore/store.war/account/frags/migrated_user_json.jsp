<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ForgotPasswordHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:droplet name="IsEmpty">
		<dsp:param name="value" bean="ForgotPasswordHandler.errorMap"/>
		<dsp:oparam name="false">
			<json:object>
				<json:property name="error"><bbbt:textArea key="txt_error_extend_account" language ="${pageContext.request.locale.language}"/></json:property>
			</json:object>
		</dsp:oparam>
		<dsp:oparam name="true">
			<json:object>
				<json:property name="success"><bbbt:textArea key="txt_success_extend_account" language ="${pageContext.request.locale.language}"/></json:property>
			</json:object>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>