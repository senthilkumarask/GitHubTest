<%@page contentType="application/json"%>
<dsp:page>

	<dsp:importbean var="ForgotPasswordHandler" bean="/atg/userprofiling/ForgotPasswordHandler" />
	<c:choose>
	<c:when test="${ForgotPasswordHandler.challengeAnswerValidated}">
	<json:object>
	<json:property name="success" value="${true}"/>
	<json:property name="challengeAnswerSucessModal">/store/account/frags/changePwdAfterResetmodel.jsp?token=${ForgotPasswordHandler.resetPasswordToken}</json:property>
	</json:object>
	</c:when>
	<c:otherwise>
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
		<c:choose>
		<c:when test="${ForgotPasswordHandler.wrongAnswerAttemptCount}">
			<json:object escapeXml="false">
			<json:property name="success" value="${true}"/>
			<json:property name="dialog"><div title="${lbl_password_reset_heading}"><p><bbbl:label key="lbl_wrong_password_thrice" language="${pageContext.request.locale.language}" /></p></div></json:property>
			</json:object>
			</c:when>
			<c:otherwise>
			<json:object escapeXml="false">
				<json:property name="error"><dsp:valueof bean="ForgotPasswordHandler.errorMap.emailError"/></json:property>
			</json:object>
			</c:otherwise>
			</c:choose>
		</dsp:oparam>
	</dsp:droplet>
	</c:otherwise>
	</c:choose>
</dsp:page>