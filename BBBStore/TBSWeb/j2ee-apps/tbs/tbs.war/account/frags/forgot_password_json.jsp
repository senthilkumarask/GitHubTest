<%@page contentType="application/json"%>
<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ForgotPasswordHandler" />
	<c:set var="lbl_password_reset_heading"><bbbl:label key="lbl_password_reset_heading" language ="${pageContext.request.locale.language}"/></c:set>
	<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
		<dsp:param name="value" bean="ForgotPasswordHandler.errorMap" />
		<dsp:oparam name="true">
		<json:object escapeXml="false">
				<json:property name="success" value="${true}"/>
					<jsp:useBean id="emailId" class="java.util.HashMap" scope="request"/>
					<c:set target="${emailId}" property="emailAddr"><dsp:valueof bean="ForgotPasswordHandler.emailAddr"/></c:set>
				<json:property name="dialog"><div title="${lbl_password_reset_heading}"><p><bbbl:textArea key="txtarea_forgot_password_success" language ="${pageContext.request.locale.language}" placeHolderMap="${emailId}" /></p></div></json:property>
		</json:object>
		</dsp:oparam>
		<dsp:oparam name="false">
			<json:object>
				<json:property name="error"><dsp:valueof bean="ForgotPasswordHandler.errorMap.emailError"/></json:property>
			</json:object>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>